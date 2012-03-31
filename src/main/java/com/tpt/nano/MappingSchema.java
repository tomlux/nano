package com.tpt.nano;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.tpt.nano.annotation.XmlAttribute;
import com.tpt.nano.annotation.XmlElement;
import com.tpt.nano.annotation.XmlIgnore;
import com.tpt.nano.annotation.XmlRootElement;
import com.tpt.nano.annotation.XmlValue;
import com.tpt.nano.annotation.schema.AttributeSchema;
import com.tpt.nano.annotation.schema.ElementSchema;
import com.tpt.nano.annotation.schema.RootElementSchema;
import com.tpt.nano.annotation.schema.ValueSchema;
import com.tpt.nano.transform.Transformer;
import com.tpt.nano.util.LRUCache;
import com.tpt.nano.util.StringUtil;
import com.tpt.nano.util.TypeReflector;
import com.tpt.nano.util.XmlUtil;

/**
 * Factory class for OX mapping schema
 * 
 * 
 * @author bulldog
 *
 */
class MappingSchema {
	
	private RootElementSchema rootElementSchema;
	private Map<String, Object> field2SchemaMapping;
	private Map<String, Object> xml2SchemaMapping;
	private Map<String, Object> xmlFullname2SchemaMapping;
	
	private Map<String, AttributeSchema> field2AttributeSchemaMapping;
	//private Map<String, ElementSchema> field2ElementSchemaMapping;
	private ValueSchema valueSchema;
	private Map<String, AttributeSchema> xml2AttributeSchemaMapping;
	private Map<String, AttributeSchema> xmlFullname2AttributeSchemaMapping;
	//private Map<String, ElementSchema> xml2ElementSchemaMapping;
	
	private Class<?> type;
	
	private static final int CACHE_SIZE = 100;
	// use LRU cache to limit memory consumption.
	private static Map<Class<?>, MappingSchema> schemaCache = Collections.synchronizedMap(new LRUCache<Class<?>, MappingSchema>(CACHE_SIZE));
	
	private MappingSchema(Class<?> type) throws MappingException {
		this.type = type;
		
		// step 1
		this.buildRootElementSchema();
		// step 2
		this.buildField2SchemaMapping();
		// step 3
		this.buildXml2SchemaMapping();
		// step 4
		this.buildField2AttributeSchemaMapping();
	}

	private void buildRootElementSchema() {
		rootElementSchema = new RootElementSchema();
		if (type.isAnnotationPresent(XmlRootElement.class)) {
			XmlRootElement xre = type.getAnnotation(XmlRootElement.class);
			if (StringUtil.isEmpty(xre.name())) {
				rootElementSchema.setXmlName(StringUtil.lowercaseFirstLetter(type.getSimpleName()));
			} else {
				rootElementSchema.setXmlName(xre.name());
			}
			String namespace = StringUtil.isEmpty(xre.namespace())?null:xre.namespace();
			rootElementSchema.setNamespace(namespace);
		} else { // if no XmlRootElement, use class name instead
			rootElementSchema.setXmlName(StringUtil.lowercaseFirstLetter(type.getSimpleName()));
			rootElementSchema.setNamespace(null);
		}
	}
	
	private void buildField2SchemaMapping() throws MappingException {
		field2SchemaMapping = this.scanFieldSchema(type);
		
		Class<?> superType = type.getSuperclass();
		// scan super class fields
		while(superType != null && superType != Object.class) {
			Map<String, Object> parentField2SchemaMapping = this.scanFieldSchema(superType);
			// redefined fields in sub-class will overwrite corresponding fields in super-class.
			parentField2SchemaMapping.putAll(field2SchemaMapping);
			field2SchemaMapping = parentField2SchemaMapping;
			superType = superType.getSuperclass();
		}
	}
	
	private void buildXml2SchemaMapping() {
		xml2SchemaMapping = new HashMap<String, Object>();
		xmlFullname2SchemaMapping = new HashMap<String, Object>();
		xml2AttributeSchemaMapping = new HashMap<String, AttributeSchema>();
		xmlFullname2AttributeSchemaMapping = new HashMap<String, AttributeSchema>();
		
		for(String fieldName : field2SchemaMapping.keySet()) {
			Object schemaObj = field2SchemaMapping.get(fieldName);
			if(schemaObj instanceof AttributeSchema) {
				AttributeSchema as = (AttributeSchema)schemaObj;
				String xmlFullname = XmlUtil.getXmlFullname(as.getNamespace(), as.getXmlName());
				xml2SchemaMapping.put(as.getXmlName(), as);
				xmlFullname2SchemaMapping.put(xmlFullname, as);
				// build xml2AttributeSchemaMapping at the same time.
				xml2AttributeSchemaMapping.put(as.getXmlName(), as);
				xmlFullname2AttributeSchemaMapping.put(xmlFullname, as);
			} else if (schemaObj instanceof ElementSchema) {
				ElementSchema es = (ElementSchema)schemaObj;
				String xmlFullname = XmlUtil.getXmlFullname(es.getNamespace(), es.getXmlName());
				xml2SchemaMapping.put(es.getXmlName(), es);
				xmlFullname2SchemaMapping.put(xmlFullname, es);
			}
		}
	}
	
	private void buildField2AttributeSchemaMapping() {
		field2AttributeSchemaMapping = new HashMap<String, AttributeSchema>();
		
		for(String fieldName : field2SchemaMapping.keySet()) {
			Object schemaObj = field2SchemaMapping.get(fieldName);
			if(schemaObj instanceof AttributeSchema) {
				field2AttributeSchemaMapping.put(fieldName, (AttributeSchema)schemaObj);
			}
		}
	}
	
	private Map<String, Object> scanFieldSchema(Class<?> type) throws MappingException {
		Map<String, Object> fieldsMap = new HashMap<String, Object>();
		Field[] fields = type.getDeclaredFields();
		
		// used for validation
		int valueSchemaCount = 0;
		int elementSchemaCount = 0;
		
		for(Field field : fields) {
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			
			if (field.isAnnotationPresent(XmlAttribute.class)) {
				// validation
				if (!Transformer.isTransformable(field.getType())) {
					throw new MappingException("XmlAttribute can't annotate complex type field, " +
							"only primivte type or frequently used java type or enum type field is allowed, " +
							"field = " + field.getName() + ", type = " + type.getName());
				}
				
				XmlAttribute xmlAttribute = field.getAnnotation(XmlAttribute.class);
				AttributeSchema attributeSchema = new AttributeSchema();
				// if attribute name was not provided, use field name instead
				if (StringUtil.isEmpty(xmlAttribute.name())) {
					attributeSchema.setXmlName(field.getName());
				} else {
					attributeSchema.setXmlName(xmlAttribute.name());
				}
				String namespace = StringUtil.isEmpty(xmlAttribute.namespace())?null:xmlAttribute.namespace();
				attributeSchema.setNamespace(namespace);
				attributeSchema.setField(field);
				
				fieldsMap.put(field.getName(), attributeSchema);
			} else if (field.isAnnotationPresent(XmlElement.class)) {
				
				elementSchemaCount++;
				
				XmlElement xmlElement = field.getAnnotation(XmlElement.class);
				ElementSchema elementSchema = new ElementSchema();
				
				if(StringUtil.isEmpty(xmlElement.name())) {
					elementSchema.setXmlName(field.getName());
				} else {
					elementSchema.setXmlName(xmlElement.name());
				}
				
				elementSchema.setData(xmlElement.data());
				String namespace = StringUtil.isEmpty(xmlElement.namespace())?null:xmlElement.namespace();
				elementSchema.setNamespace(namespace);
				elementSchema.setField(field);
				
				fieldsMap.put(field.getName(), elementSchema);
				
			} else if (field.isAnnotationPresent(XmlValue.class)) {
				valueSchemaCount++;
				
				// validation
				if (!Transformer.isTransformable(field.getType())) {
					throw new MappingException("XmlValue can't annotate complex type field, " +
							"only primivte type or frequently used java type or enum type field is allowed, " +
							"field = " + field.getName() + ", type = " + type.getName());
				}
				
				XmlValue xmlValue = field.getAnnotation(XmlValue.class);
				
				valueSchema = new ValueSchema();
				valueSchema.setData(xmlValue.data());
				valueSchema.setField(field);
				
			} else if (field.isAnnotationPresent(XmlIgnore.class)) {
				
				// ignore this field
				
			} else { // default to XmlElement
				
				elementSchemaCount++;
				
				ElementSchema elementSchema = new ElementSchema();
				
				// List validation
				if (TypeReflector.isCollection(field.getType())) {
					if (!TypeReflector.isList(field.getType())) {
						throw new MappingException("Nano framework only supports java.util.List as collection type, " +
								"field = " + field.getName() + ", type = " + type.getName());
					} else {
						elementSchema.setList(true);
						Class<?> paramizedType = TypeReflector.getParameterizedType(field);
						if (paramizedType == null) {
							throw new MappingException("Can't get parameterized type of a List field, " +
									"Nano framework only supports collection field of List<T> type, and T must be a Nano bindable type, " +
									"field = " + field.getName() + ", type = " + type.getName());
						} else {
							elementSchema.setParameterizedType(paramizedType);
						}
					}
				}
				
				elementSchema.setXmlName(field.getName());
				elementSchema.setField(field);
				elementSchema.setNamespace(null);
				
				fieldsMap.put(field.getName(), elementSchema);
			}
		}
		
		// more validation
		if (valueSchemaCount > 1) {
			throw new MappingException("XmlValue can't annotate more than one fields in same class," + 
					" type = " + type.getName());
		}
		
		if (valueSchemaCount == 1 && elementSchemaCount >= 1) {
			throw new MappingException("XmlValue and XmlElement(or XmlElementWrapper) annotations can't coexist in same class," + 
					" type = " + type.getName());
		}
		
		return fieldsMap;
		
	}
	
	public Class<?> getType() {
		return this.type;
	}
	
	/**
	 * Factory method.
	 * 
	 * @param object an object to get mapping schema from.
	 * @return a MappingSchema instance.
	 */
	public static MappingSchema fromObject(Object object) throws MappingException {
		return fromClass(object.getClass());
	}
	
	/**
	 * Factory method.
	 * 
	 * @param type a Class type to get mapping schema from.
	 * @return a MappingSchema instance.
	 */
	public static MappingSchema fromClass (Class<?> type) throws MappingException {
		if (schemaCache.containsKey(type)) {
			return schemaCache.get(type);
		} else {
			MappingSchema mappingSchema = new MappingSchema(type);
			schemaCache.put(type, mappingSchema);
			return mappingSchema;
		}
	}
	
	public Map<String, Object> getField2SchemaMapping() {
		return field2SchemaMapping;
	}

	public Map<String, Object> getXml2SchemaMapping() {
		return xml2SchemaMapping;
	}
	
	public Map<String, Object> getXmlFullname2SchemaMapping() {
		return xmlFullname2SchemaMapping;
	}
	
	public RootElementSchema getRootElementSchema() {
		return rootElementSchema;
	}
	
	public Map<String, AttributeSchema> getField2AttributeSchemaMapping() {
		return field2AttributeSchemaMapping;
	}
	
	public ValueSchema getValueSchema() {
		return valueSchema;
	}
	
	public Map<String, AttributeSchema> getXml2AttributeSchemaMapping() {
		return xml2AttributeSchemaMapping;
	}
	
	public Map<String, AttributeSchema> getXmlFullname2AttributeSchemaMapping() {
		return xmlFullname2AttributeSchemaMapping;
	}
	
}
