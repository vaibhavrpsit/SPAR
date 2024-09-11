/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/max/retail/stores/domain/tender/MAXTenderLineItemConstantsIfc.java /main/32 2014/06/17 15:26:38 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * Rev 1.0	Aug 26,2016		Nitesh Kumar	changes for code merging 
 * ===========================================================================
 */
package max.retail.stores.domain.tender;

import oracle.retail.stores.domain.tender.TenderLineItemConstantsIfc;

public interface MAXTenderLineItemConstantsIfc extends TenderLineItemConstantsIfc {
	public static final int TENDER_TYPE_Loyalty_Points = 14;
	public static final int TENDER_TYPE_ECOM_PREPAID = 15;
	public static final int TENDER_TYPE_PAYTM = 17;
	public static final int TENDER_TYPE_MOBIKWIK = 18;
	public static final int TENDER_TYPE_LAST_USED = 18;
	public static final String[] TENDER_LINEDISPLAY_DESC = { "Cash", "Credit", "Check", "TravelCk", "GiftCert",
			"MailCheck", "Debit", "Coupon", "GiftCard", "StoreCr", "MallCert", "P.O.", "MoneyOrder", "E-Check",
			"LoyaltyPoints", "EComPrepaid", "EComCOD","Paytm","Mobikwik" };
}
