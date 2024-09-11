/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/TenderFactory.java /rgbustores_13.4x_generic_branch/2 2011/10/13 15:15:28 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       10/12/11 - create house account tender correctly from legacy
 *                         tender line item
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:23 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:57 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:51 PM  Robert Pearse
 *
 *   Revision 1.3.2.1  2004/11/15 22:27:36  bwf
 *   @scr 7671 Create tender from rdo instead of class.  This is necessary because ADO's are not 1:1 with RDOs.
 *
 *   Revision 1.3  2004/05/27 13:57:26  epd
 *   @scr 5290 made private method protected
 *
 *   Revision 1.2  2004/02/12 16:47:55  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.4   Feb 05 2004 13:46:32   rhafernik
 * log4j changes
 *
 *    Rev 1.3   Dec 16 2003 11:17:06   bwf
 * Create new createTenderMethod to accomidate code review.
 *
 *    Rev 1.2   Dec 03 2003 17:13:48   bwf
 * Add mall certificate.
 * Resolution for 3538: Mall Certificate Tender
 *
 *    Rev 1.1   Nov 20 2003 16:57:16   epd
 * updated to use new ADO Factory Complex
 *
 *    Rev 1.0   Nov 04 2003 11:13:16   epd
 * Initial revision.
 *
 *    Rev 1.5   Oct 29 2003 16:00:26   epd
 * removed dead code
 *
 *    Rev 1.4   Oct 28 2003 16:10:24   crain
 * Added TenderCouponADO
 * Resolution for 3421: Tender redesign
 *
 *    Rev 1.3   Oct 27 2003 18:23:34   epd
 * Added code for Credit tender
 *
 *    Rev 1.2   Oct 25 2003 16:07:02   blj
 * added Money Order Tender
 *
 *    Rev 1.0   Oct 17 2003 12:33:48   epd
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender;

import java.util.HashMap;

import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
;

/**
 * Utility class for Tenders
 */
public class TenderFactory implements TenderFactoryIfc
{
    /**
     * Attempts to create a tender based on the attributes contained in the HashMap
     * @param tenderAttributes  HashMap containing attributes needed to create a tender
     * @return A TenderADOIfc instance
     * @throws TenderException Thrown when it's not possible to create a tender
     *                         due to invalid attributes.
     */
    public TenderADOIfc createTender(HashMap tenderAttributes)
    throws TenderException
    {
        assert(tenderAttributes.get(TenderConstants.TENDER_TYPE) != null) : "Must provide tender type";

        TenderTypeEnum tenderType = (TenderTypeEnum)tenderAttributes.get(TenderConstants.TENDER_TYPE);

        TenderADOIfc tender = getTenderADO(tenderType);
        ((AbstractTenderADO)tender).setTenderAttributes(tenderAttributes);
        return tender;
    }

    //----------------------------------------------------------------------
    /**
        Attempts to create an ADO tender given a corresponding RDO.
        @param rdoObject An RDO tender
        @return A TenderADOIfc instance
        @see oracle.retail.stores.pos.ado.factory.TenderFactoryIfc#createTender(oracle.retail.stores.domain.tender.TenderLineItemIfc)
    **/
    //----------------------------------------------------------------------
    public TenderADOIfc createTender(TenderLineItemIfc rdoObject)
    {
        TenderTypeEnum tenderType = TenderTypeEnum.makeTenderTypeEnumFromRDO(rdoObject);

        // If using this method, one should _always_ have a tender type.
        assert(tenderType != null);
        return createTender(tenderType);
    }

    //----------------------------------------------------------------------
    /**
        Attempts to create an ADO tender given a corresponding TenderTypeEnum.
        @param tte TenderTypeEnum
        @return TenderADOIfc
    **/
    //----------------------------------------------------------------------
    public TenderADOIfc createTender(TenderTypeEnum tenderType)
    {
        TenderADOIfc tender = getTenderADO(tenderType);
        return tender;
    }

    /**
     * Instantiate the proper concrete tender type
     * @param tenderType The enumerated ADO tender type
     * @return a new tenderADO instance
     */
    protected TenderADOIfc getTenderADO(TenderTypeEnum tenderType)
    {
        TenderADOIfc tender = null;
        if (tenderType == TenderTypeEnum.CASH)
        {
            tender = new TenderCashADO();
        }
        else if (tenderType == TenderTypeEnum.CHECK)
        {
            tender = new TenderCheckADO();
        }
        else if (tenderType == TenderTypeEnum.COUPON)
        {
            tender = new TenderCouponADO();
        }
        else if (tenderType == TenderTypeEnum.CREDIT)
        {
            tender = new TenderCreditADO();
        }
        else if (tenderType == TenderTypeEnum.DEBIT)
        {
            tender = new TenderDebitADO();
        }
        else if (tenderType == TenderTypeEnum.GIFT_CARD)
        {
            tender = new TenderGiftCardADO();
        }
        else if (tenderType == TenderTypeEnum.GIFT_CERT)
        {
            tender = new TenderGiftCertificateADO();
        }
        else if (tenderType == TenderTypeEnum.MAIL_CHECK)
        {
            tender = new TenderMailCheckADO();
        }
        else if (tenderType == TenderTypeEnum.PURCHASE_ORDER)
        {
            tender = new TenderPurchaseOrderADO();
        }
        else if (tenderType == TenderTypeEnum.STORE_CREDIT)
        {
            tender = new TenderStoreCreditADO();
        }
        else if (tenderType == TenderTypeEnum.TRAVELERS_CHECK)
        {
            tender = new TenderTravelersCheckADO();
        }
        else if (tenderType == TenderTypeEnum.MONEY_ORDER)
        {
            tender = new TenderMoneyOrderADO();
        }
        else if (tenderType == TenderTypeEnum.COUPON)
        {
            tender = new TenderCouponADO();
        }
        else if (tenderType == TenderTypeEnum.MALL_CERT)
        {
            tender = new TenderMallCertificateADO();
        }
        else if (tenderType == TenderTypeEnum.HOUSE_ACCOUNT)
        {
            tender = new TenderCreditADO();
        }
        return tender;
    }
}


