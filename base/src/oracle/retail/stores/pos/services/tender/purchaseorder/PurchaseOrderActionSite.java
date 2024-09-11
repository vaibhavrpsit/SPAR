/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/purchaseorder/PurchaseOrderActionSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:47 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    aphulamb  04/14/09 - Fixed issue if Special Order is done by Purchase
 *                         Order
 *    nganesh   02/04/09 - Externalized Transaction Tax Removed Text in EJ
 *
 * ===========================================================================
 * $Log:
 |    5    360Commerce 1.4         6/12/2008 4:32:39 PM   Charles D. Baker CR
 |         32040 - Updated to avoid clearing tax exempt status unless a) we're
 |          removing a tax exempt tender and b) there are not remaining
 |         tenders that are tax exempt purchase orders. Code review by Jack
 |         Swan.
 |    4    360Commerce 1.3         1/25/2006 4:11:40 PM   Brett J. Larsen merge
 |          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 |    3    360Commerce 1.2         3/31/2005 4:29:32 PM   Robert Pearse
 |    2    360Commerce 1.1         3/10/2005 10:24:27 AM  Robert Pearse
 |    1    360Commerce 1.0         2/11/2005 12:13:29 PM  Robert Pearse
 |   $:
 |    4    .v700     1.2.1.0     11/3/2005 15:59:59     Deepanshu       CR
 |         6119: updated tax status information for EJournal
 |    3    360Commerce1.2         3/31/2005 15:29:32     Robert Pearse
 |    2    360Commerce1.1         3/10/2005 10:24:27     Robert Pearse
 |    1    360Commerce1.0         2/11/2005 12:13:29     Robert Pearse
 |   $
 |   Revision 1.5.2.1  2004/11/12 14:28:53  kll
 |   @scr 7337: JournalFactory extensibility initiative
 |
 |   Revision 1.5  2004/07/22 22:38:41  bwf
 |   @scr 3676 Add tender display to ingenico.
 |
 |   Revision 1.4  2004/06/15 22:31:27  crain
 |   @scr 5596 Tender_No Change Due Options available for PO Tender
 |
 |   Revision 1.3  2004/05/26 23:09:03  crain
 |   @scr 5062 Purchase Order- Taxable status missing from journal when agency is other/business
 |
 |   Revision 1.2  2004/05/17 19:30:56  crain
 |   @scr 4198 Receipt prints incorrect PO Tender amount
 |
 |   Revision 1.1  2004/04/02 22:13:51  epd
 |   @scr 4263 Updates to move Purchase Order tender to its own tour
 |
 |   Revision 1.6  2004/03/02 19:47:48  crain
 |   *** empty log message ***
 |
 |   Revision 1.5  2004/02/27 02:43:33  crain
 |   @scr 3421 Tender redesign
 |
 |   Revision 1.4  2004/02/18 18:17:48  tfritz
 |   @scr 3718 - Added setNonTaxable() method.
 |
 |   Revision 1.3  2004/02/12 16:48:22  mcs
 |   Forcing head revision
 |
 |   Revision 1.2  2004/02/11 21:22:51  rhafernik
 |   @scr 0 Log4J conversion and code cleanup
 |
 |   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 |   updating to pvcs 360store-current
 |
 |    Rev 1.3   Dec 18 2003 21:14:22   crain
 | Changed constant
 | Resolution for 3421: Tender redesign
 |
 |    Rev 1.2   Dec 17 2003 12:10:56   crain
 | Added tax exemption
 | Resolution for 3421: Tender redesign
 |
 |    Rev 1.1   Nov 20 2003 16:57:36   epd
 | updated to use new ADO Factory Complex
 |
 |    Rev 1.0   Nov 04 2003 11:17:50   epd
 | Initial revision.
 |
 |    Rev 1.1   Oct 24 2003 15:05:46   bwf
 | Added asserts.
 | Resolution for 3418: Purchase Order Tender Refactor
 |
 |    Rev 1.0   Oct 24 2003 14:54:54   bwf
 | Initial revision.
 | Resolution for 3418: Purchase Order Tender Refactor
 |
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.purchaseorder;

import java.util.HashMap;
import java.util.Locale;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderPurchaseOrderADO;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;


//--------------------------------------------------------------------------
/**
    This class creates a purchase order tender and tries to add it to
    the transaction.  If it cannot be validated, then it is discarded.
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class PurchaseOrderActionSite extends PosSiteActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //  --------------------------------------------------------------------------
    /**
       @param bus the bus arriving at this site
    **/
    //  --------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();

        // add tender type to attributes
        HashMap tenderAttributes = cargo.getTenderAttributes();
        tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.PURCHASE_ORDER);

        // create the purchase order tender
        TenderPurchaseOrderADO purchaseOrderTender = null;
        if (cargo.getTenderADO() == null)
        {
            try
            {
                TenderFactoryIfc factory = (TenderFactoryIfc)ADOFactoryComplex.getFactory("factory.tender");
                purchaseOrderTender = (TenderPurchaseOrderADO)factory.createTender(tenderAttributes);
            }
            catch (ADOException adoe)
            {
                adoe.printStackTrace();
            }
            catch (TenderException e)
            {
                TenderErrorCodeEnum error = e.getErrorCode();
                if (error == TenderErrorCodeEnum.INVALID_AMOUNT)
                {
                    assert(false) : "This should never happen, because UI enforces proper format";
                }
            }
        }
        else
        {
            purchaseOrderTender = (TenderPurchaseOrderADO)cargo.getTenderADO();
        }

        // add the tender to the transaction
        try
        {
            cargo.getCurrentTransactionADO().addTender(purchaseOrderTender);
            cargo.getCurrentTransactionADO().updateOrderStatus();
            cargo.setLineDisplayTender(purchaseOrderTender);

            journalTaxStatus(bus, tenderAttributes);
            // journal tender
            JournalFactoryIfc jrnlFact = null;
            try
            {
                jrnlFact = JournalFactory.getInstance();
            }
            catch (ADOException e)
            {
                logger.error(JournalFactoryIfc.INSTANTIATION_ERROR, e);
                throw new RuntimeException(JournalFactoryIfc.INSTANTIATION_ERROR, e);
            }
            RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();
            registerJournal.journal(purchaseOrderTender, JournalFamilyEnum.TENDER, JournalActionEnum.ADD);

            // mail a letter
            bus.mail(new Letter("PaidUp"), BusIfc.CURRENT);
        }
        catch(TenderException e)
        {
            assert(false) : "This should never happen, because UI enforces proper format";
            cargo.setTenderADO(purchaseOrderTender);
        }
    }

    //  --------------------------------------------------------------------------
    /**
     * Journal the tax status<P>
     *
     *  @param bus the bus arriving at this site
     *  @param tenderAttributes
    **/
    //  --------------------------------------------------------------------------

    protected void journalTaxStatus(BusIfc bus, HashMap tenderAttributes)
    {
        String status = (String)tenderAttributes.get(TenderConstants.TAXABLE_STATUS);
         if (status != null && status.equals(TenderConstants.TAX_EXEMPT)) {
             JournalManagerIfc journal =
                 (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);

             Locale journalLocale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
             String transactionTaxRemoved=I18NHelper.getString(
						I18NConstantsIfc.EJOURNAL_TYPE,
						JournalConstantsIfc.TRANS_TAX_REMOVED, null,
						journalLocale);
             journal.journal(transactionTaxRemoved);
         }
    }
}
