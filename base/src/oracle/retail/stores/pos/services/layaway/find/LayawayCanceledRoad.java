/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/find/LayawayCanceledRoad.java /main/12 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/30/12 - get journalmanager from bus
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    hyin      10/27/11 - fixed sending cancel letter twice problem when user
 *                         double click on cancel button
 *    hyin      10/06/11 - fix null pointer exception
 *    mchellap  12/17/10 - XbranchMerge mchellap_bug-10381134 from
 *                         rgbustores_13.3x_generic_branch
 *    mchellap  12/16/10 - Bug#10381134 Cancel recepits not printing line items
 *                         and layaway operation type
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:49 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:01 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:15 PM  Robert Pearse
 *
 *   Revision 1.4  2004/09/27 22:32:03  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.3  2004/02/12 16:50:49  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:00:42   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:20:42   msg
 * Initial revision.
 *
 *    Rev 1.1   26 Mar 2002 10:45:36   dfh
 * removed unlink customer from journal string
 * Resolution for POS SCR-644: Canceled Layaway Delete EJ entry has undefined line of info
 *
 *    Rev 1.0   Mar 18 2002 11:35:14   msg
 * Initial revision.
 *
 *    Rev 1.1   08 Feb 2002 16:37:34   jbp
 * keep customer on transaction when escaping from layaway find
 * Resolution for POS SCR-995: Escaping from Layaway List causes customer to unlink and disables Find button on Layaway Options
 *
 *    Rev 1.0   Sep 21 2001 11:21:28   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:34   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.find;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

/**
 * Displays the menu screen for finding layaway(s).
 * 
 * @version $Revision: /main/12 $
 */
@SuppressWarnings("serial")
public class LayawayCanceledRoad extends PosLaneActionAdapter
{
    /**
     * lane name constant
     */
    public static final String LANENAME = "LayawayCanceledRoad";

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * Either sets Sale Return Transaction in cargo to the new layaway
     * transaction, or turns the seed transaction into a Sale Return Transaction
     * so that the POS service will cancel it.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void traverse(BusIfc bus)
    {
        LayawayCargo cargo = (LayawayCargo)bus.getCargo();

        if (cargo.getTenderableTransaction() == null && cargo.getSeedLayawayTransaction() != null)
        {
            cargo.setTenderableTransaction(instantiateLayawayTransaction(cargo, bus));
        }
    }

    /**
     * Instantiates an object implementing the LayawayTransactionIfc interface
     * and sets it as the tendrable transaction in the cargo..
     * 
     * @return object implementing LayawayIfc
     */
    protected LayawayTransactionIfc instantiateLayawayTransaction(LayawayCargo cargo, BusIfc bus)
    {
        TransactionIfc seedTransaction = cargo.getSeedLayawayTransaction();
        LayawayTransactionIfc  layawayTransaction = DomainGateway.getFactory().getLayawayTransactionInstance();
        // Initialize the transaction for cancellation
        LayawayTransactionIfc layTxn = cargo.getInitialLayawayTransaction(); 
        if ( layTxn == null){
            layTxn = DomainGateway.getFactory().getLayawayTransactionInstance();
            layTxn.setCashier(cargo.getOperator());
        }
        layawayTransaction.initialize(seedTransaction, layTxn);

        // Set layaway operation type for receipt printing
        if (cargo.getLayawayOperation() == FindLayawayCargoIfc.LAYAWAY_PICKUP)
        {
            layawayTransaction.setTransactionType(TransactionIfc.TYPE_LAYAWAY_COMPLETE);
        }
        else if (cargo.getLayawayOperation() == FindLayawayCargoIfc.LAYAWAY_PAYMENT)
        {
            layawayTransaction.setTransactionType(TransactionIfc.TYPE_LAYAWAY_PAYMENT);
        }
        else if (cargo.getLayawayOperation() == FindLayawayCargoIfc.LAYAWAY_DELETE)
        {
            layawayTransaction.setTransactionType(TransactionIfc.TYPE_LAYAWAY_DELETE);
        }

        // Don't think this is necessary for payments, pickups, and deletes,
        // but may be necessary for undo's which need to become sale return
        // transactions.
        layawayTransaction.setSalesAssociate(((AbstractFinancialCargo)cargo).getOperator());

        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        layawayTransaction.
                setTransactionTax(utility.getInitialTransactionTax());

        // journal unlink customer if customer exists...
        if (cargo.getCustomer() != null)
        {
            JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
            StringBuilder sb = new StringBuilder();

            if (journal != null)
            {
                journal.journal(layawayTransaction.getCashier().getLoginID(),
                        layawayTransaction.getTransactionID(),
                                sb.toString());
            }
            else
            {
                logger.error( "No JournalManager found");
            }
        }

        // clear customer name in the status area
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.customerNameChanged("", false);

        cargo.setCustomer(null);

        return layawayTransaction;
    }
}
