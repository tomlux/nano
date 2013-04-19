// Generated by xsd compiler for android/java
// DO NOT CHANGE!
package com.ebay.api.trading;

/**
 * 
 * PayPal account status.
 * 
 */
public enum PayPalAccountStatusCodeType {

    /**
     * 
   * Account is active.
   * 
     */
    ACTIVE("Active"),
  

    /**
     * 
   * Account is closed.
   * 
     */
    CLOSED("Closed"),
  

    /**
     * 
   * Account is highly restricted.
   * 
     */
    HIGH_RESTRICTED("HighRestricted"),
  

    /**
     * 
   * Account restriction is low.
   * 
     */
    LOW_RESTRICTED("LowRestricted"),
  

    /**
     * 
   * Account is locked.
   * 
     */
    LOCKED("Locked"),
  

    /**
     * 
   * Reserved for internal or future use
   * 
     */
    CUSTOM_CODE("CustomCode"),
  

    WIRE_OFF("WireOff"),
  

    UNKNOWN("Unknown"),
  

    INVALID("Invalid");
  
  
    private final String value;
  
    PayPalAccountStatusCodeType(String v) {
        value = v;
    }
    
    public String value() {
        return value;
    }
    
    public static PayPalAccountStatusCodeType fromValue(String v) {
        if (v != null) {
            for (PayPalAccountStatusCodeType c: PayPalAccountStatusCodeType.values()) {
                if (c.value.equals(v)) {
                    return c;
                }
            }
        }
        throw new IllegalArgumentException(v);
    }
}