/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	 Rev 1.2	Oct 11, 2017		    Atul Shukla 	    Mobikwik FES
 *  Rev 1.1		May 04, 2017		    Kritica Agarwal 	GST Changes
 *  Rev 1.0     Dec 19, 2016	        Ashish Yadav		Changes for StoreCredit FES
 *
 ********************************************************************************/

package max.retail.stores.domain.arts;

import oracle.retail.stores.domain.arts.DataTransactionKeys;

public class MAXDataTransactionKeys implements DataTransactionKeys {

	public static final String MAX_HOT_KEYS_LOOKUP_TRANSACTION = "persistence_HotKeysTransaction";

	public static final String STORE_CREDIT_DATA_TRANSACTION = "persistence_StoreCreditDataTransaction";
	
	public static final String LOYALTY_DATA_TRANSACTION = "persistence_LoyaltyDataTransaction";
	
	public static final String TRANSACTION_READ_CUSTOM_DISCOUNT_RULES = "persistence_TransactionReadCustomDiscountRules";
	public static final String TRANSACTION_READ_PRINTED_ITEM_FREE_DISCOUNT_RULE = "persistence_TransactionReadPrintedItemFreeDiscountRule";
	public static final String TRANSACTION_READ_SRC_AND_TGT_DISCOUNT_PERC = "persistence_TransactionReadSrcandTgtDiscountPerc";
	public static final String TRANSACTION_LAYAWAY_READ_ROUNDED_AMOUNT = "persistence_LayawayReadRoundedAmountDataTransaction";
	public static final String GSTIN_DATA_BO_CO_TRANSFER_TRANSACTION = "persistence_GstinDataBoCoTransferTransaction";
	
	public static final String EMPLOYEE_CENTRAL_TRANSACTION = "persistence_CentralEmployeeTransaction";

	public static final String EMPLOYEE_CENTRAL_UPDATION_TRANSACTION = "persistence_CentralUpdationEmployeeTransaction";
	
	public static final String TIC_CUSTOMER_CONFIG_TRANSACTION = "persistence_TICCustomerDataTransaction";
	
	public static final String MAX_CODE_DATA_TRANSACTION = "persistence_CodeDataTransaction";
	
	public static final String ORDER_WRITE_DATA_TRANSACTION = "persistence_OrderWriteDataTransaction";
	public static final String MAXCERTIFICATE_TRANSACTION = "persistence_MAXCertificateTransaction";
	public static final String CERTIFICATE_TRANSACTION = "persistence_CertificateTransaction";
	// Changes ends for Rev 1.0

	
	/** MAX Rev 1.2 Change : Start **/
	public static final String MOBIKWIK_DATA_TRANSACTION = "persistence_MobikwikDataTransaction";
	
	public static final String CONFIG_PARAMETER_TRANSACTION = "persistence_ConfigParameterTransaction";
	public static final String TIC_CUSTOMER_DATA_TRANSACTION = "persistence_TICCustomerDataTransaction";
	public static final String GSTIN_INVOICE_AUTOMATION ="persistence_GstinInvoiceAutomation";

	public static  String GSTIN_DATA_TRANSFER_TRANSACTION="persistence_MAXEGSTINDataTransferTransaction";

	public static final String SAVE_MANAGER_OVERRIDE_TRANSACTION = "persistence_WriteManagerOverride";
	
	public static final String SAVE_SUBMIT_INV_REQ_REP= "persistence_SubInvReqRep";

	public static final String ReadHomeStateTransactions = "persistence_ReadHomeStateTransactions";
	public static final String GSTMappingTransaction = "persistence_GSTMappingTransaction";
	public static final String ReadIGSTTaxTransactions = "persistence_ReadIGSTTaxTransactions";
	//Added by Kumar vaibhav for liquidation report
	
	public static final String SAVE_LIQUIDATION_REPORT_TRANSACTION = "persistence_WriteLiquidationItem";
	
}
