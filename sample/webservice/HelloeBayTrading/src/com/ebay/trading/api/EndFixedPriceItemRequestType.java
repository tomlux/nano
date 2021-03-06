// Generated by xsd compiler for android/java
// DO NOT CHANGE!
package com.ebay.trading.api;

import java.io.Serializable;
import com.leansoft.nano.annotation.*;

/**
 * 
 * Ends the specified fixed-price listing before the date and time at which
 * it would normally end (per the listing duration).
 * 
 */
@RootElement(name = "EndFixedPriceItemRequest", namespace = "urn:ebay:apis:eBLBaseComponents")
public class EndFixedPriceItemRequestType extends AbstractRequestType implements Serializable {

    private static final long serialVersionUID = -1L;

	@Element(name = "ItemID")
	@Order(value=0)
	public String itemID;	
	
	@Element(name = "EndingReason")
	@Order(value=1)
	public EndReasonCodeType endingReason;	
	
	@Element(name = "SKU")
	@Order(value=2)
	public String sku;	
	
    
}