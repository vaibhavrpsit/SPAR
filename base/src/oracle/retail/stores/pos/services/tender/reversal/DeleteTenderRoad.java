/* ===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/reversal/DeleteTenderRoad.java /main/1 2012/09/12 11:57:17 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/27/12 - implement deleting and reversing tender for
 *                         mobilepos
 *    cgreene   03/27/12 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.reversal;

import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * Deletes a selected tender from the transaction if specified in the cargo.
 */
@SuppressWarnings("serial")
public class DeleteTenderRoad extends PosLaneActionAdapter
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // Get the tender from the cargo and construct an ADO tender from it
        ReversalCargo cargo = (ReversalCargo)bus.getCargo();
        TenderLineItemIfc tenderToRemove = cargo.getTenderToDelete();
        if (tenderToRemove != null)
        {
            try
            {
                // Create ADO tender
                TenderFactoryIfc factory = (TenderFactoryIfc)ADOFactoryComplex.getFactory("factory.tender");
                TenderADOIfc tenderADO = factory.createTender(tenderToRemove);
                ((ADO)tenderADO).fromLegacy(tenderToRemove);
                
                // get Current txn from cargo
                RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
                txnADO.removeTender(tenderADO); 
                
                try
                {
                    // Journal the removal of the tender
                    JournalFactoryIfc jrnlFact = JournalFactory.getInstance();
                    RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();
                    registerJournal.journal(tenderADO, JournalFamilyEnum.TENDER, JournalActionEnum.DELETE);
                }
                catch (ADOException e)
                {
                    logger.error(JournalFactoryIfc.INSTANTIATION_ERROR, e);
                }
            }
            catch (ADOException e)
            {
                logger.error("Unable to obtain ADO factory.", e);
            }
        }
    }
}
