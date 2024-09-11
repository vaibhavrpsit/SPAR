/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *  Rev 1.2  	Jun 01, 2019     	Purushotham Reddy    	Changes done for POS-Amazon Pay Integration
 *	Rev 1.1		Jan 11, 2017		Ashish Yadav			Chnages done for Hirepurchase,Loyalty points
 *	Rev 1.0     Dec 06, 2016		Ashish Yadav			Changes for Employee Discount  FES
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ado.tender;

import max.retail.stores.domain.tender.MAXTenderStoreCreditIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc.TenderType;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;

/**
 * @author Himanshu
 * 
 */
public class MAXTenderTypeEnum extends TenderTypeEnum {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3790833661651989206L;
	// Chnages starts for Rev 1.1 (Ashish)

	// protected static final HashMap<String, MAXTenderTypeEnum> enumMap = new
	// HashMap<String, MAXTenderTypeEnum>();
	public static final TenderTypeEnum STORE_CREDIT = enumMap.get("StoreCredit");
	/** MFL Change for Rev 1.2 - End */
	public static final TenderTypeEnum PAYTM = enumMap.get("Paytm");

	public static final TenderTypeEnum MOBIKWIK = enumMap.get("Mobikwik");
	/** MAX Rev 1.1 Change: Start **/
	public static final TenderTypeEnum LOYALTY_POINTS = enumMap.get("LoyaltyPoints");
	/** MAX Rev 1.1 Change: End **/
	/**
	 * Rev 1.2 CREDIT Card changes
	 */
	public static final TenderTypeEnum CREDIT = enumMap.get("Credit");
	/**
	 * Rev 1.2 CREDIT Card changes end
	 */
	// Rev 1.3 changes start
	public static final TenderTypeEnum PURCHASE_ORDER = enumMap.get("PurchaseOrder");
	// Rev 1.3 changes end

	// Rev 1.4 changes start
	public static final TenderTypeEnum ECOM_PREPAID = enumMap.get("EComPrepaid");

	public static final TenderTypeEnum ECOM_COD = enumMap.get("EComCOD");
	// Rev 1.4 changes end

	public static final TenderTypeEnum AMAZON_PAY = enumMap.get("AmazonPay");
	public static final TenderTypeEnum EWALLET = enumMap.get("EWallet");

	protected MAXTenderTypeEnum(String tenderName, Class rdoType, String value, TenderType tenderType) {
		super(tenderName, rdoType, tenderType, value);
	}

	// Changes for Rev 1.1 : Starts
	public static TenderTypeEnum makeTenderTypeEnumFromRDO(TenderLineItemIfc rdoObject) {
		return (TenderTypeEnum.makeTenderTypeEnumFromRDO(rdoObject));
	}

	/** factory method. May return null */
	public static MAXTenderTypeEnum makeEnumFromString(String enumer) {
		return (MAXTenderTypeEnum) enumMap.get(enumer);
	}

	public TenderType getTenderType() {
		return tenderType;
	}

	// Chnages ends for Rev 1.1 (Ashish)
	
			 
}
