// Generated by xsd compiler for android/java
// DO NOT CHANGE!
package com.ebay.shopping.api;

/**
 * 
 * Specifies the selling format used for a listing.
 * 
 */
public enum ListingTypeCodeType {

    /**
     * 
   * Unknown auction type. (This is not normally used.)
   * 
     */
    UNKNOWN("Unknown"),
  

    /**
     * 
   * Single-quantity online auction format.
   * A Chinese auction has a Quantity of 1. Buyers engage in competitive bidding,
   * although Buy It Now may be offered as long as no bids have been placed.
   * Online auctions are listed on eBay.com, and they are also listed in
   * the seller's eBay Store if the seller is a Store owner.
   * 
     */
    CHINESE("Chinese"),
  

    DUTCH("Dutch"),
  

    /**
     * 
   * Live auction, on-site auction that can include non-eBay bidders. Live auctions
   * are listed on the eBay Live Auctions site, in live auction categories. They can
   * also appear on eBay if the seller lists the lot in a secondary, eBay category.
   * 
     */
    LIVE("Live"),
  

    /**
     * 
   * Reserved for future use.
   * 
     */
    AUCTION("Auction"),
  

    /**
     * 
   * Advertisement to solicit inquiries on listings such as real estate. Permits no
   * bidding on that item, service, or property. To express interest, a buyer fills
   * out a contact form that eBay forwards to the the seller as a lead. This format
   * does not enable buyers and sellers to transact online through eBay, and eBay
   * Feedback is not available for ad format listings.
   * 
     */
    AD_TYPE("AdType"),
  

    /**
     * 
     */
    STORES_FIXED_PRICE("StoresFixedPrice"),
  

    /**
     * 
   * Second chance offer made to a non-winning bidder on an ended listing. A seller
   * can make an offer to a non-winning bidder when either the winning bidder has
   * failed to pay for an item or the seller has a duplicate of the item. Second-
   * chance offer items are on eBay, but they do not appear when browsing or
   * searching listings. You need to already know the item ID in order to retrieve a
   * second-chance offer.
   * 
     */
    PERSONAL_OFFER("PersonalOffer"),
  

    /**
     * 
   * A basic fixed-price listing with a Quantity of 1. Allows no auction-style
   * bidding. Also known as Buy It Now Only on some sites, this should not to be
   * confused with the BuyItNow option that is available for competitive-bid
   * auctions. Fixed-price listings appear on eBay.com. They are also listed in a
   * seller's eBay Store if the seller is a Store owner.
   * 
     */
    FIXED_PRICE_ITEM("FixedPriceItem"),
  

    /**
     * 
   * Half.com listing (item is listed on Half.com, not on eBay).
   * Reserved for future use.
   * 
     */
    HALF("Half"),
  

    /**
     * 
   * Lead Generation format (advertisement-style listing to solicit
   * inquiries or offers, no bidding or fixed price, listed on eBay).
   * 
     */
    LEAD_GENERATION("LeadGeneration"),
  

    EXPRESS("Express"),
  

    /**
     * 
   * Placeholder value. See
   * <a href="types/simpleTypes.html#token">token</a>.
   * 
     */
    CUSTOM_CODE("CustomCode");
  
  
    private final String value;
  
    ListingTypeCodeType(String v) {
        value = v;
    }
    
    public String value() {
        return value;
    }
    
    public static ListingTypeCodeType fromValue(String v) {
        if (v != null) {
            for (ListingTypeCodeType c: ListingTypeCodeType.values()) {
                if (c.value.equals(v)) {
                    return c;
                }
            }
        }
        throw new IllegalArgumentException(v);
    }
}