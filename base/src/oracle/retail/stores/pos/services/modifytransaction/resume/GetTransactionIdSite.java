/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/resume/GetTransactionIdSite.java /main/4 2014/05/14 14:41:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/14/14 - rename retrieve to resume
 *    rgour     12/10/12 - Enhancement in suspended transaction phase
 *    rgour     12/04/12 - Suspened Transaction not found if entered id is
 *                         found in both Item Master and suspended transaction
 *                         list
 *    rgour     11/02/12 - Enhancements in Suspended Transactions
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.resume;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

@SuppressWarnings("serial")
public class GetTransactionIdSite extends PosSiteActionAdapter
{
    /**
     * Sending the Next letter to get the suspended transaction details
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // selected row from ui
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // set entered transaction ID in cargo
        ModifyTransactionResumeCargo cargo = (ModifyTransactionResumeCargo)bus.getCargo();

        // get data from input area
        String transactionID = ui.getInput();
        if (transactionID == null || transactionID.equals(""))
        {
            transactionID = cargo.getTransactionIDEntered();
        }

        // create summary from ID
        TransactionSummaryIfc summary = DomainGateway.getFactory().getTransactionSummaryInstance();
        TransactionIDIfc transID = DomainGateway.getFactory().getTransactionIDInstance();
        transID.setTransactionID(transactionID);
        summary.setBusinessDate(cargo.getStoreStatus().getBusinessDate());
        summary.setTransactionID(transID);
        cargo.setSelectedSummary(summary);

        if (logger.isInfoEnabled())
        {
            logger.info("Selected transaction ID: " + transactionID);
        }

        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
    } 
}