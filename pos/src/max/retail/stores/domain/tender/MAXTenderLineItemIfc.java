/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
*   
*  	 Rev 1.5		Jun 01, 2019		Purushotham Reddy   	Changes  for POS-Amazon Pay Integration
*    Rev 1.4   		16/oct/2017   		Bhanu Priya    			Changes done for Mobikwik fes
*    Rev 1.3   		16/oct/2017   		Bhanu Priya    			Changes done for paytm fes
*    Rev 1.2  		14/July/2016   		Abhishek Goyal          Initial Draft: Changes for CR
*    Rev 1.1  		20/May/2013			Prateek		 			Changes for TIC Customer Integration. 
*  	 Rev 1.0  		15/Apr/2013			Jyoti Rawal 			Initial Draft: Changes for Gift Card Functionality 
*  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.tender;

import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.utility.EYSDate;

/**
 * @author Jyoti Rawal.
 *
 */
public interface MAXTenderLineItemIfc extends TenderLineItemIfc {

	public static final int TENDER_TYPE_LOYALTY_POINTS = 14;
	public static final int TENDER_TYPE_ECOM_PREPAID = 15;
	public static final int TENDER_TYPE_ECOM_COD = 16;
	public static final int TENDER_TYPE_PAYTM = 17;
	public static final int TENDER_TYPE_MOBIKWIK = 18;
	
	public static final int TENDER_TYPE_AMAZON_PAY = 19;
	
	public static final int TENDER_TYPE_LAST_USED = 21;
	
	public static final int TENDER_TYPE_EWALLET = 21;

	public static final String[] TENDER_LINEDISPLAY_DESC = { "Cash", "Credit", "Check", "TravelCk", "GiftCert",
		"MailCheck", "Debit", "Coupon", "GiftCard", "StoreCr", "MallCert", "P.O.", "MoneyOrder", "E-Check",
		"LoyaltyPoints","Paytm","Mobikwik","AmazonPay","EComPrepaid", "EComCOD","EWALLET"};
	
	public String getQcApprovalCode();

	public void setQcApprovalCode(String qcApprovalCode);

	public EYSDate getQcExpiryDate();

	public void setQcExpiryDate(EYSDate qcExpiryDate);

	public String getQcType();

	public void setQcType(String qcType);

	public String getQcTransId();

	public void setQcTransId(String qcTransId);
}
