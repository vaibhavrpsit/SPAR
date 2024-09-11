/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
*  	Rev 1.0  01/June/2013	Jyoti Rawal, Initial Draft: Changes for Bug 6090 :Incorrect EJ of the transaction in which Hire
*  	Purchase is used as a tender type 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.purchaseorder;

import java.util.HashMap;

import max.retail.stores.pos.ado.journal.MAXJournalFactory;
import max.retail.stores.pos.ado.tender.MAXTenderPurchaseOrderADO;
import max.retail.stores.pos.ado.tender.MAXTenderTypeEnum;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.purchaseorder.PurchaseOrderLimitActionSite;


//--------------------------------------------------------------------------
/**
    This class creates a purchase order tender and tries to add it to
    the transaction.  If it cannot be validated, then it is discarded.
    $Revision: 4$
**/
//--------------------------------------------------------------------------
public class MAXPurchaseOrderActionSite extends PosSiteActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -7958063229665501433L;
	/**
       revision number
    **/
    public static final String revisionNumber = "$Revision: 4$";

    //  --------------------------------------------------------------------------
    /**
       @param bus the bus arriving at this site
    **/
    //  --------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
    	MAXTenderCargo cargo = (MAXTenderCargo)bus.getCargo();

        // add tender type to attributes
        HashMap tenderAttributes = cargo.getTenderAttributes();
        tenderAttributes.put(TenderConstants.TENDER_TYPE, MAXTenderTypeEnum.PURCHASE_ORDER);

        // create the purchase order tender
        MAXTenderPurchaseOrderADO purchaseOrderTender = null;
        if (cargo.getTenderADO() == null)
        {
            try
            {
                TenderFactoryIfc factory = (TenderFactoryIfc)ADOFactoryComplex.getFactory("factory.tender");
                purchaseOrderTender = (MAXTenderPurchaseOrderADO)factory.createTender(tenderAttributes);
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
//                    assert(false) : "This should never happen, because UI enforces proper format";
                }
            }
        }
        else
        {
            purchaseOrderTender = (MAXTenderPurchaseOrderADO)cargo.getTenderADO();
        }

        // add the tender to the transaction
        try
        {
            cargo.getCurrentTransactionADO().addTender(purchaseOrderTender);
            cargo.setLineDisplayTender(purchaseOrderTender);

            journalTaxStatus(bus, tenderAttributes);
            // journal tender
            JournalFactoryIfc jrnlFact = null;
            try
            {
            	jrnlFact = MAXJournalFactory.getInstance();
//                jrnlFact = JournalFactory.getInstance();
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
//            assert(false) : "This should never happen, because UI enforces proper format";
            cargo.setTenderADO(purchaseOrderTender);
        }
    }

    protected void journalTaxStatus(BusIfc bus, HashMap tenderAttributes)
    {
        String status = (String)tenderAttributes.get(TenderConstants.TAXABLE_STATUS);
         if (status != null && status.equals(PurchaseOrderLimitActionSite.TAX_EXEMPT)) {
             JournalManagerIfc journal =
                 (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);
             journal.journal("Transaction Tax Removed");
         }
    }
}
