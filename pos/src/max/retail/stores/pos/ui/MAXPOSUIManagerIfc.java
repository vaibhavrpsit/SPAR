/********************************************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	Rev	4.0 	Sep 01, 2020		Kumar Vaibhav 		    Pinelab Integration
 *  Rev	3.0 	Jul 01, 2019		Purushotham Reddy 		Changes for POS-Amazon Pay Integration 
 *  Rev	2.0 	Sept 06, 2018		Bhanu Priya				Changes for PAN CARD CR
 *	Rev	1.0 	Jan 06, 2017		Ashish Yadav			Changes for Online redemption loyalty OTP FES
 *
 ********************************************************************************************************/

package max.retail.stores.pos.ui;

import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//--------------------------------------------------------------------------
/**
 * This interface defines the contract for the POSUIManager and any
 * POSUISubsystem. This contract references other documents for details of the
 * screens and the letters that will be sent back.
 * <P>
 * $Revision: /rgbustores_12.0.9in_branch/1 $
 */
// --------------------------------------------------------------------------
public interface MAXPOSUIManagerIfc extends POSUIManagerIfc {

	/**
	 * Rev 1.0 changes start
	 */
	static final public String LINEITEM_VOID = "LINEITEM_VOID";
	static final public String LINEITEM_VOID_LIST = "LINEITEM_VOID_LIST";
	static final public String COUPON_LIST = "COUPON_LIST";
	static final public String STATUS_ENQUIRY = "STATUS_ENQUIRY";
	static final public String COUPON_DENOMINATION = "COUPON_DENOMINATION";
	static final public String USER_DEFINED_COUPON = "USER_DEFINED_COUPON";
	// GiftCard Changes For Rev 1.1 changes :Start
	static final public String GIFT_CARD_VALIDATING_SCREEN = "GIFT_CARD_VALIDATING_SCREEN";

	static final public String Gift_CARD_BATCH_CLOASE_SCREEN = "Gift_CARD_BATCH_CLOASE_SCREEN";

	static final public String CREDIT_DEBIT_ONLINE_OFFLINE = "CREDIT_DEBIT_ONLINE_OFFLINE";
	// GiftCard Changes For Rev 1.1 changes :End

	// Changes For Rev 1.1 changes :starts
	static final public String ALTER_ORDER = "ALTER_ORDER";
	static final public String ADD_ORDER_ITEM = "ADD_ORDER_ITEM";
	static final public String MAX_ORDER_SHIPPING_METHOD = "MAX_ORDER_SHIPPING_METHOD";
	// Changes For Rev 1.1 changes :end

	/** MAX Rev 1.3 Change : Start **/
	static final public String MAX_TIC_CUSTOMER_OPTIONS = "TIC_CUSTOMER_OPTIONS";
	static final public String MAX_TIC_LOYALTY_POINTS = "MAX_TIC_LOYALTY_POINTS";
	 static final public String ENTER_LOYALTY_POINTS_AMOUNT="ENTER_LOYALTY_POINTS_AMOUNT";
	/** MAX Rev 1.3 Change : End **/
	/**
	 * Rev 1.4 changes start here
	 */
	static final public String MAX_SHOW_CREDIT_OFFLINE = "MAX_SHOW_CREDIT_OFFLINE";
	static final public String ENTER_CREDIT_CARD_DETAILS = "ENTER_CREDIT_CARD_DETAILS";
	static final public String CREDIT_CARD_EDC = "CREDIT_CARD_EDC";
	/**
	 * Rev 1.4 changes end here
	 */
	/** MAX Rev 1.5 Change : Start **/
	static final public String SELECT_COUPON_TO_COUNT = "SELECT_COUPON_TO_COUNT";
	static final public String SELECT_COUPON_TO_COUNT_DETAIL = "SELECT_COUPON_TO_COUNT_DETAIL";
	static final public String USER_DEFINED_DENOMINATION = "USER_DEFINED_DENOMINATION";
	static final public String ACQUIRER_BANK_DETAIL = "ACQUIRER_BANK_DETAIL";
	static final public String CREDIT_TID_DETAIL = "CREDIT_TID_DETAIL";
	static final public String ENTER_TID_AMOUNT_DETAIL = "ENTER_TID_AMOUNT_DETAIL";
	static final public String GIFT_CERT_DETAIL_SCREEN = "GIFT_CERT_DETAIL_SCREEN";
	/** MAX Rev 1.5 Change : End **/

	static final public String ENTER_TIC_CUSTOMER_ID = "ENTER_TIC_CUSTOMER_ID";

	/** MAX Rev 1.6 Change : Start **/
	// Create by Akhilesh for TIC Customer Screen entry screen
		public static final String TIC_CUSTOMER_OPTIONS = "TIC_CUSTOMER_OPTIONS";
		
		//Create by Akhilesh for TIC Customer Screen entry screen
	public static final String CARDLESS_TIC_CUSTOMER_OPTIONS = "CARDLESS_TIC_CUSTOMER_OPTIONS";

	// Create by Akhilesh for New TIC Customer ADD Screen entry screen
	public static final String ADD_TIC_CUSTOMER_OPTIONS = "ADD_TIC_CUSTOMER_OPTIONS";

	// Create by Akhilesh for New TIC Customer ADD Screen entry screen
	public static final String PROCESS_CRM_REQUEST = "PROCESS_CRM_REQUEST";

	// Create by Akhilesh for CRM response for new Customer Screen entry screen
	public static final String CRM_RESPONSE_NEW = "CRM_RESPONSE_NEW";

	// Create by Akhilesh for CRM response for existing Customer ADD Screen
	// entry screen
	public static final String CRM_RESPONSE_EXISTING = "CRM_RESPONSE_EXISTING";

	// Capillary Coupon Changes For Rev 1.7 changes :Starts

	public static final String NON_TIC_MOBILE_NUMBER = "NON_TIC_MOBILE_NUMBER";
	public static final String ENTER_COUPON_NUMBER = "ENTER_COUPON_NUMBER";

	// Capillary Coupon Changes For Rev 1.7 changes :End

	// Changes For Rev 1.8 changes :Starts
	public static final String ECOM_ORDER_DETAILS = "ECOM_ORDER_DETAILS";
	public static final String TENDER_OPTIONS3 = "TENDER_OPTIONS3";
	public static final String TENDER_OPTIONS3_CPOI = "TENDER_OPTIONS3_CPOI";
	// Changes For Rev 1.8 changes :Ends
	// Changes start for code merging(adding below variable)
	public static final String PICK_ONE_MRP = "PICK_ONE_MRP";
	static final public String TENDER_OPTIONS2 = "TENDER_OPTIONS2";
	static final public String TENDER_OPTIONS2_CPOI = "TENDER_OPTIONS2_CPOI";
	// Changes neds for code merging
	// change starts for Rev 1.0 (Ashish : Loyalty OTP)
	public static final String ENTER_OTP = "ENTER_OTP";
	// change ends for Rev 1.0 (Ashish : Loyalty OTP)
	public static final String CREDIT_DEBIT_ONLINE_OFFLINE_SWIPE = "CREDIT_DEBIT_ONLINE_OFFLINE_SWIPE";
	public static final String CREDIT_LOYALTY_SCREEN = "CREDIT_LOYALTY_SCREEN";
	public static final String CREDIT_DEBIT_DETAILS = "CREDIT_DEBIT_DETAILS";
	public static final String EDC_POST_VOID_SCREEN = "EDC_POST_VOID_SCREEN";
	static final public String EDC_VOID_SCREEN = "EDC_VOID_SCREEN";
	public static final String LOYALTY_EMI_AMOUNT_SCREEN = "LOYALTY_EMI_AMOUNT_SCREEN";
	// screen name added by atul shukla for mobikwik screen
	public static final String TENDER_WALLET_OPTION = "TENDER_WALLET_OPTION";
	public static final String ENTER_WALLET_MOBILE_NUMBER_AND_TOTP = "ENTER_WALLET_MOBILE_NUMBER_AND_TOTP";
	public static final String MCOUPON_PHONE_NUMBER = "MCOUPON_PHONE_NUMBER";
	public static final String PROCESS_CAPILLARY_SCREEN = "PROCESS_CAPILLARY_SCREEN";
	// Changes start by Bhanu priya
	public static final String ENTER_WALLET_MOBILE_NUMBER_AND_TOTP_MOBIKWIK = "ENTER_WALLET_MOBILE_NUMBER_AND_TOTP_MOBIKWIK";

	// Changes Start by Bhanu Priya for Capture PAN CARD CR
	public static final String CUSTOMER_TYPE_CAPTURE = "CUSTOMER_TYPE_CAPTURE";
	public static final String PANCARD_NUMBER_CAPTURE = "PANCARD_NUMBER_CAPTURE";
	public static final String FORM60_IDENTIFICATION_NUMBER = "FORM60_IDENTIFICATION_NUMBER";
	public static final String CAPTURE_PASSPORT_DETAILS = "CAPTURE_PASSPORT_DETAILS";
	// Changes END by Bhanu Priya for Capture PAN CARD CR
	
	public static final String ENTER_AMAZON_PAY_MOBILE_NUMBER_SCREEN = "ENTER_AMAZON_PAY_MOBILE_NUMBER_SCREEN";
	public static final String ERECEIPT_MOBILE_NUMBER_SCREEN = "ERECEIPT_MOBILE_NUMBER_SCREEN";
	
	// Create upi in tender section (vibhu)
    public static final String UPI_PAYMENT_SITE = "UPI_PAYMENT_SITE";
	
		//Rev 4.0 start
	//public static final String CRM_ENROLMENT_OTP_SCREEN = "ENTER_CRM_ENROL_OTP";
	//Rev 4.0 end
public static final String CREDIT_OPTIONS ="CREDIT_OPTIONS";
  //Changes starts for Rev 4.0 (Vaibhav ; PineLab)
  	static final public String CREDIT_DEBIT_PINELAB_INNOVITI = "CREDIT_DEBIT_PINELAB_INNOVITI";
  	public static final String PINELAB_CREDIT_LOYALTY_SCREEN = "PINELAB_CREDIT_LOYALTY_SCREEN";
  	static final public String PINELAB_PHONEPE_GETSTATUS = "PINELAB_PHONEPE_GETSTATUS";
  	static final public String PINELAB_UPI_GETSTATUS = "PINELAB_UPI_GETSTATUS";
  	//Changes ends for Rev 4.0 (Vaibhav ; PineLab)
  	static final public String ENTER_GPAY_NUMBER="ENTER_GPAY_NUMBER";
  	static final public String EDC_POST_VOID_GPAY_SCREEN = "EDC_POST_VOID_GPAY_SCREEN";
  	static final public String EDC_PHONEPE_SCREEN = "EDC_PHONEPE_SCREEN";
	static final public String ENTER_PHONEPE_NUMBER="ENTER_PHONEPE_NUMBER";
	public static final String MAX_EMI_OPTIONS_SCREEN = "MAX_EMI_OPTIONS_SCREEN";
	static final public String EDC_POST_VOID_UPI_SCREEN = "EDC_POST_VOID_UPI_SCREEN";
	public static final String CRM_ENROLMENT_OTP_SCREEN = "CRM_ENROLMENT_OTP_SCREEN";
	
	public static final String SBI_CARD_NUMBER = "SBI_CARD_NUMBER";
	public static final String REWARD_AMOUNT_SCREEN = "REWARD_AMOUNT_SCREEN";
	public static final String ENTER_EDGE_ITEM = "ENTER_EDGE_ITEM";
	
	public static final String ENTER_EWALLET_MOBILE_NUMBER_SCREEN = "ENTER_EWALLET_MOBILE_NUMBER_SCREEN";
	
	public static final String OXIGEN_WALLET_OTP_SCREEN = "OXIGEN_WALLET_OTP_SCREEN";
	
	public static final String GSTIN_CAPTURE = "GSTIN_CAPTURE";
	public static final String PROCESS_CYGNET_SCREEN = "PROCESS_CYGNET_SCREEN";
	
	//Changes Starts by Kamlesh Pant for SpecialEmpDiscount
	public static final String EMPLOYEE_ID = "EMPLOYEE_ID";
	
	//Changes by shyvanshu Mehra FOR CUSTOMER SCRREN
	public static final String CUSTOMER_MOBILE_NUMBER_SCREEN = "CUSTOMER_MOBILE_NUMBER_SCREEN";
	public static final String CUSTOMER_NEW_MOBILE_NUMBER_SCREEN = "CUSTOMER_NEW_MOBILE_NUMBER_SCREEN";
	public static final String PARKING_COUPON_SCREEN = "PARKING_COUPON_SCREEN";
	public static final String NEW_SCREEN ="NEW_SCREEN";
	
	//Added by Kumar vaibhav for reentry mode tender options
	
	 public static final String TENDER_OPTIONS4 = "TENDER_OPTIONS4";
	  
	  public static final String TENDER_OPTIONS4_CPOI = "TENDER_OPTIONS4_CPOI";
	  
	  
	//Rev 4.1 Starts
			public static final String ENTER_SC_OTP = "ENTER_SC_OTP";
		//Rev 4.1 ends
}
