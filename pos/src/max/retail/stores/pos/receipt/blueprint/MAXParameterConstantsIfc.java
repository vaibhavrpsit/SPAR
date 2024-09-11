/* *****************************************************************************************
 * Copyright (c) 2016 MAX Hyper Market Inc.    All Rights Reserved.
 *
 * Rev 1.2    05 April 2016    Kritica Agarwal GST Changes
 * Rev 1.1  Nitika          22 Mar,2017 Code done for EComPrepaid and EComCOD tender functionality.
 * Rev 1.0 Hitesh.dua 		15dec,2016	Initial revision.
 * New constant added for each parameter to print customized receipt. 
 * *******************************************************************************************
 */

package max.retail.stores.pos.receipt.blueprint;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;

public interface MAXParameterConstantsIfc extends ParameterConstantsIfc {

	public static final String DISCOUNT_EmployeeDiscountAmountPercent = "EmployeeDiscountAmountPercent";
	
	public static final String PRINTING_LoyaltyPointsSlipFooter = "LoyaltyPointsSlipFooter";
	
	public static final String TENDER_LoyaltyPointConversionRate = "LoyaltyPointConversionRate";
	
	public static final String PRINTING_ReceiptFooter = "ReceiptFooter";
	//Changes start for Rev 1.1 starts
	public static final String PRINTING_PrintAdditionalDetailsForEComOrder = "PrintAdditionalDetailsForEComOrder";
	//Changes start for Rev 1.1 ends
	
	//public static final String TENDER_LoyaltyPointConversionRate = "LoyaltyPointConversionRate";
	//Change for Rev 1.2 : Starts
	public static final String PRINTING_TIN = "TIN";
	public static final String PRINTING_GSTIN = "GSTIN";
	public static final String PRINTING_Footer = "Footer";
	public static final String PRINTING_GSTSaleReceiptTypeLabel = "GSTSaleReceiptTypeLabel";
	public static final String PRINTING_GSTReturnReceiptTypeLabel = "GSTReturnReceiptTypeLabel";
	public static final String PRINTING_GSTNumber = "GSTNumber";
	public static final String ENABLEERECEIPT = "EnableEReceipt";
	
	//Change for Rev 1.2 : Ends
	
}
