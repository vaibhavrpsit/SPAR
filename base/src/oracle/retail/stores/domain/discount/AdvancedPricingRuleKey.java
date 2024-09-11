/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/AdvancedPricingRuleKey.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:43 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.discount;

import oracle.retail.stores.foundation.utility.Util;

/**
 * A small object to encapsulate the two part Key of an AdvancedPricingRule
 */
public class AdvancedPricingRuleKey
    implements AdvancedPricingRuleKeyIfc, Comparable {

    /** revision number supplied by source-code-control system */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

  /** Unique identifier for the Store that defined this rule */
 private String storeID;

  /** Unique identifer for this rule */
  private int ruleID;


  /**
   * Constructor - does nothing special.
   */
  public AdvancedPricingRuleKey() {
  }

  /**
   * Just remember the constituent parts.
   * @param storeID store ID to remember
   * @param ruleID rule ID to remember
   * @return this AdvancedPricingRuleKeyIfc object
   */
  public AdvancedPricingRuleKeyIfc initialize (String storeID, int ruleID) {
    this.storeID = storeID;
    this.ruleID = ruleID;

    return this;
  }

  /**
   * Accessor method for storeID.
   * @return the store ID of the key
   */
  public String getStoreID() {
    return this.storeID;
  }

  /**
   * Accessor method for ruleID.
   * @return the rule ID of the key
   */
  public int getRuleID() {
    return this.ruleID;
  }

  /**
   * Make a duplicate AdvancedPricingRuleKey.
   * @return a copy of this object
   */
  public Object clone() {
    return new AdvancedPricingRuleKey().initialize(this.storeID, this.ruleID);
  }

  /**
   * Two AdvancedPricingRuleKeys can be compared by comparing their constituent parts.
   * @param obj The object to compare to.
   * @return true if objects are equal
   */
  public int compareTo(Object obj)
  {
    // Make sure we have the right kind of object
    AdvancedPricingRuleKeyIfc aKey = (AdvancedPricingRuleKeyIfc) obj;

    // StoreID is the primary part of the key
    int result = aKey.getStoreID().compareTo(this.storeID);
    if(result != 0)
      return result;

    // StoreIDs are the same, compare ruleIDs
    return this.ruleID - aKey.getRuleID();
  }


  /**
   * Two AdvancedPricingRuleKeys are equal if their constituent parts are equal.
   * @param obj The object to compare to.
   * @return true if objects are equal
   */
  public boolean equals(Object obj) {
    return (this.compareTo(obj) == 0);
  }

  /**
   * Debug/Logging representation of this class
   * @return String containing class name and version number
   */
  public String toString()
  {
    return "AdvancedPricingRuleKey - version: " + getRevisionNumber();
  }

  /**
   * Revision number of the code
   * @return String containing revision number as set by source code control system.
   */
  public String getRevisionNumber()  {
    return Util.parseRevisionNumber(revisionNumber);
  }
}
