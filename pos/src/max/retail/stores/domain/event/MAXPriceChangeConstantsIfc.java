/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved.
  Rev 1.1	Veeresh Singh		3/04/2013		Initial Draft:	Food Totals requirement.
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.event;

import org.springframework.core.enums.StringCodedLabeledEnum;

import oracle.retail.stores.domain.event.PriceChangeConstantsIfc;

//----------------------------------------------------------------------------
/**
 * This interface contains the constants associated with price changes.
 * <P>
 * 
 * @version $Revision: /rgbustores_12.0.9in_branch/1 $
 **/
// ----------------------------------------------------------------------------
public interface MAXPriceChangeConstantsIfc extends PriceChangeConstantsIfc { // begin
																				// interface
																				// PriceChangeConstantsIfc
	/**
	 * revision number supplied by source-code-control system
	 **/
	public static String revisionNumber = "$Revision: /rgbustores_12.0.9in_branch/1 $";
	/**
	 * price change application type constant for undefined types
	 **/
	public static final int APPLICATION_CODE_UNDEFINED = 0;
	/**
	 * price change application type constant for set by percent
	 **/
	public static final int APPLICATION_CODE_CHANGE_BY_PERCENT = 1;
	/**
	 * price change application type constant for set by amount
	 **/
	public static final int APPLICATION_CODE_CHANGE_BY_AMOUNT = 2;
	/**
	 * price change application type constant for set by percent over/under cost
	 **/
	public static final int APPLICATION_CODE_PERCENT_COST = 3;
	/**
	 * price change application type constant for set by amount over/under cost
	 **/
	public static final int APPLICATION_CODE_AMOUNT_COST = 4;
	/**
	 * price change application type constant for set by replacement amount
	 **/
	public static final int APPLICATION_CODE_AMOUNT_REPLACE = 5;
	/**
	 * event type constant descriptors
	 **/
	public static final String[] APPLICATION_CODE_DESCRIPTORS = { "Undefined", "ChangeByPercentage", "ChangeByAmt",
			"SetByPercent", "SetByAmt", "SetByReplacement" };
	/**
	 * event type constant
	 **/
	public static final String[] APPLICATION_CODE = { "UND", "PCT", "AMT", "CSTPCT", "CSTAMT", "AMTREPL" };

	/**
	 * Price change applied on Maximum Retail Price
	 **/
	public static final StringCodedLabeledEnum APPLIED_ON_MRP = new StringCodedLabeledEnum("MRP",
			"Applied on MaximumRetailPrice");
	/**
	 * Price change applied on Sellign Retail
	 **/
	public static final StringCodedLabeledEnum APPLIED_ON_SELLING_RETAIL = new StringCodedLabeledEnum("SR",
			"Applied on Selling Retail");

} // end interface PriceChangeConstantsIfc
