/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   	 Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved. 
   	 Rev 2.1  	May 14, 2024	Kamlesh Pant		Store Credit OTP:
   	 Rev 2.0  	15/May/2023   	Kumar Vaibhav 		Chnages for CN lock
	 Rev 1.2  	27/May/2013		Jyoti Rawal,		Changes for Credit Card Functionality
     Rev 1.1  	20/05/2013		Prateek				Initial Draft: Changes for TIC Customer Integration
  	 Rev 1.0  	08/May/2013		Jyoti Rawal, 		Initial Draft: Changes for Hire Purchase Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.ado.tender;


public class MAXTenderConstants {

	//approval code
	public static final String APPROVAL_CODE = "APPROVAL_CODE";

    /** MAX Rev 1.1 Change : Start **/
	public static final String LOYALTY_CARD_NUMBER = "LOYALTY_CARD_NUMBER";
	public static final String LOYALTY_CARD_REDEEM_AMOUNT = "LOYALTY_CARD_REDEEM_AMOUNT";

    /** MAX Rev 1.1 Change : End **/

 /**
     * Rev 1.2 changes start here
     */
	 //  The type of the BANK_NAME
    public static final String BANK_NAME = "BANK_NAME";
    //  The type of the CHECK_DATE
    public static final String CHECK_DATE = "CHECK_DATE";
      public static final String BK_ID = "ACQUIRING_BANK_CODE";
    
    public static final String ONLINE_OFFLINE_STATUS = "ONLINE_OFFLINE_STATUS";
	public static final String ONLINE = "ONLINE";
	public static final String OFFLINE = "OFFLINE";
    // Authorization Method
    public static final String AUTH_METHOD = "AUTH_METHOD";
    /**
     * Rev 1.2 changes end here
     */
    // changes start for code merging(adding below variable as it is not present in base 14)
    public static final String GIFT_CARD_APPROVED_FLAG = "GIFT_CARD_APPROVED_FLAG";
    // changes ends for code merging
    
 // The Bank Code
 	//public static final String BANK_CODE = "BANK_CODE";
 	// added by atul shukla
 	//public static final String PAYTM_MOBILE_NUMBER = "PAYTM_MOBILE_NUMBER";
 	//public static final String PAYTM_TOTP = "PAYTM_TOTP";
 	// The bank Name
 //	public static final String BANK_NAME = "BANK_NAME";
 	
 	public static final String WALLET_TRANSACTIONID = "WALLET_TRANSACTIONID";
	public static final String WALLET_ORDERID = "WALLET_ORDERID";
	public static final String AUTH_CODE = "AUTH_CODE";
	public static final String BANK_CODE = "BANK_CODE";
	public static final String CARD_TYPE="CARD_TYPE";
	public static final String POINTS = "POINTS";
	public static final String MERCHANT_TRANSACTION_ID = "MERCHANT_TRANSACTION_ID";
	
	//Added by Vaibhav LS Credit note code merging start Rev 2.0
	public static final String SC_LOCK_STATUS = "SC_LOCK_STATUS";
	//end Rev 2.0
	
	//Change for Rev 2.1 Start 
		public static final String mobileNumber = "mobileNumber";
	//Change for Rev 2.1 End
}
