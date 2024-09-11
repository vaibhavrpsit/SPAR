/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/group/TenderGroupFactory.java /rgbustores_13.4x_generic_branch/2 2011/09/09 17:41:56 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       09/08/11 - add house account as a refund tender
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:24 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:59 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:53 PM  Robert Pearse
 *
 *   Revision 1.2  2004/02/12 16:47:56  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.4   Feb 05 2004 13:20:58   rhafernik
 * log4j conversion
 *
 *    Rev 1.3   Dec 03 2003 17:13:52   bwf
 * Add mall certificate.
 * Resolution for 3538: Mall Certificate Tender
 *
 *    Rev 1.2   Nov 20 2003 17:59:44   epd
 * fixed import
 *
 *    Rev 1.1   Nov 20 2003 17:13:52   epd
 * updates for ADO Factory Complex
 *
 *    Rev 1.0   Nov 04 2003 11:13:56   epd
 * Initial revision.
 *
 *    Rev 1.2   Oct 25 2003 16:07:12   blj
 * added Money Order Tender
 *
 *    Rev 1.1   Oct 21 2003 09:33:32   blj
 * added Money Order Tender
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender.group;

import oracle.retail.stores.pos.ado.factory.TenderGroupFactoryIfc;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;

/**
 *
 *
 */
public class TenderGroupFactory implements TenderGroupFactoryIfc
{
    /**
     * Attempt to create a new TenderGroup given a tender type enum instance.
     * @param tenderType The type of the desired tender group
     * @return a new TenderGroup instance or null.
     */
    public TenderGroupADOIfc createTenderGroup(TenderTypeEnum tenderType)
    {
        TenderGroupADOIfc result = null;

        if (tenderType == TenderTypeEnum.CASH)
        {
            result = new TenderGroupCashADO();
        }
        else if (tenderType == TenderTypeEnum.CHECK)
        {
            result = new TenderGroupCheckADO();
        }
        else if (tenderType == TenderTypeEnum.COUPON)
        {
            result = new TenderGroupCouponADO();
        }
        else if (tenderType == TenderTypeEnum.CREDIT)
        {
            result = new TenderGroupCreditADO();
        }
        else if (tenderType == TenderTypeEnum.DEBIT)
        {
            result = new TenderGroupDebitADO();
        }
        else if (tenderType == TenderTypeEnum.GIFT_CARD)
        {
            result = new TenderGroupGiftCardADO();
        }
        else if (tenderType == TenderTypeEnum.GIFT_CERT)
        {
            result = new TenderGroupGiftCertificateADO();
        }
        else if (tenderType == TenderTypeEnum.MAIL_CHECK)
        {
            result = new TenderGroupMailCheckADO();
        }
        else if (tenderType == TenderTypeEnum.PURCHASE_ORDER)
        {
            result = new TenderGroupPurchaseOrderADO();
        }
        else if (tenderType == TenderTypeEnum.STORE_CREDIT)
        {
            result = new TenderGroupStoreCreditADO();
        }
        else if (tenderType == TenderTypeEnum.TRAVELERS_CHECK)
        {
            result = new TenderGroupTravelersCheckADO();
        }
        else if (tenderType == TenderTypeEnum.MONEY_ORDER)
        {
            result = new TenderGroupMoneyOrderADO();
        }
        else if (tenderType == TenderTypeEnum.MALL_CERT)
        {
            result = new TenderGroupMallCertificateADO();
        }
        else if (tenderType == TenderTypeEnum.HOUSE_ACCOUNT)
        {
            result = new TenderGroupHouseAccountADO();
        }
        return result;
    }
}
