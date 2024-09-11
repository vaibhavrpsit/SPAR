/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	Rev 1.1 	May 14, 2024		Kamlesh Pant		Store Credit OTP:
 *	Rev	1.0 	Jan 06, 2017		Ashish Yadav		Changes for Online redemption loyalty OTP FES	
 *Changes for RTS Manager Override
 *    
 ********************************************************************************/
package max.retail.stores.domain.employee;  

import oracle.retail.stores.domain.employee.RoleFunctionIfc;

public interface MAXRoleFunctionIfc extends RoleFunctionIfc {
		
	/**
	 * Rev 1.0 changes
	 */
	public static final int PRICE_DISCOUNT = 322;
	public static final int ALLOW_EXPIRED_STORE_CREDIT = 999;

	/**
	 * Rev 1.1 changes
	 */

	public static final int INVOICE_TYPE_DISCOUNT_PERCENT = 19;
	public static final int INVOICE_TYPE_DISCOUNT_DOLLAR = 20;
	public static final int INVOICE_TYPE_DISCOUNT = 99;
	/**
	 * Rev 1.1 changes
	 */
	/**
	 * Rev 1.2 changes Start
	 */

	public static final int BUSINESS_DATE_MISMATCH = 800;
	/**
	 * Rev 1.2 changes ends
	 */
	/**
	 * Rev 1.3 changes Start
	 */
	public static final int POS_OFFLINE_ALERT = 801;
	/**
	 * Rev 1.3 changes ends
	 */

	/**
	 * Rev 1.4 changes Start
	 */
	public static final int CASH_REFUND = 802;

	/**
	 * Rev 1.4 changes ends
	 */
	// Changes starts for Rev 1.0 (Ashish : Loyalty OTP)
		public static final int OTP_CANCEL = 326;
		// Changes ends for Rev 1.0 (Ashish : Loyalty OTP)
		//Changes for RTS Manager Override: Start
	    public static final int RTS_ALLOW_NON_RETURNABLE_ITEMS = 4025;
		//Changes for RTS Manager Override: End
		public static final int ITEM_DELETE = 0;
		public static final int CREATE_ITEM = 0;
		public static final int RETURN_WITHOUT_RECEIPT = 0;
		public static final int RETURN_NON_RETRIEVAL = 0;
		public static final int ACCEPT_INVALID_CREDIT_NOTE = 0;
		public static final int ACCEPT_SUSPEND_TRANSACTION = 0;
		public static final int ACCEPT_MALL_CERT_TENDER = 0;
		public static final int CUSTOMER_LINK = 0;
		public static final int EDIT_DELIVERY_DATE_SLOT = 0;
		public static final int GDMS_OFFLINE = 0;
		public static final int ACCEPT_MANAUL_CREDIT_NOTE = 0;
		public static final int ACCEPT_EXPIRED_CREDIT_NOTE = 0;
		
		//Rev 1.1 Starts
		public static final int PICKUP_CHARGE_ITEM = 347;		
		public static final int OtpCancelCNI = 348;
		public static final int OtpCancelCNR = 349;
		// Rev 1.1 end
		public static final int INVOICE_TYPE_DISCOUNT_PERCENT_BB = 33;
		public static final int INVOICE_TYPE_DISCOUNT_DOLLAR_BB = 34;
		public static final int OtpCancelED = 326;
		
}
