/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/queue/ClearQueueAisle.java /main/14 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    aariyer   04/24/09 - rework.removed commented code from clearqueueaisle
 *    aariyer   04/22/09 - For Clear Queue in POS
 *    nganesh   03/12/09 - Retained getFormattedReport call also
 *    nganesh   03/12/09 - Modified for Clear Queue EJ
 *    nganesh   03/12/09 - Changes for Clear Queue EJ
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:27 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:16 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:01 PM  Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:48:52  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:35:20  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Oct 02 2003 10:06:28   bwf
 * Removed deprecation because flow has been reinstated.  Also removed unused imports.
 *
 *    Rev 1.1   Sep 25 2003 12:25:14   bwf
 * Deprecated.
 * Resolution for 3334: Feature Enhancement:  Queue Exception Handling
 *
 *    Rev 1.0   Aug 29 2003 15:53:10   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:38:26   msg
 * Initial revision.
 *
 *    Rev 1.2   20 Mar 2002 12:28:54   epd
 * Updated for EJournal of clearing queue
 * Resolution for POS SCR-821: Clear Queue EJ Entry text errors not to spec
 *
 *    Rev 1.1   Mar 18 2002 23:06:12   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:20:22   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:12:30   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:11:58   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.queue;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.DataManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionQueueIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.reports.QueuedTransactionReportItem;
import oracle.retail.stores.pos.reports.QueuedTransactionsReport;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.manager.registerreports.RegisterReportsCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This road performs all the steps required to delete the first entry in the
    queue.
    @version $Revision: /main/14 $
**/
//--------------------------------------------------------------------------
public class ClearQueueAisle extends PosLaneActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/14 $";

    //----------------------------------------------------------------------
    /**
       This Aisle performs all the steps required to delete the first entry
       in the queue.
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        QueueCargo cargo = (QueueCargo)bus.getCargo();
        QueuedTransactionsReport qtr;

        // Get the data manager and dialog model
        DataManagerIfc     dm = (DataManagerIfc)Gateway.getDispatcher().getManager(DataManagerIfc.TYPE);
        DialogBeanModel model = new DialogBeanModel();

        // Get and test the queue names
        String[] qNames = dm.getTransactionQueues();
        if (qNames == null)
        {
            // Report the error
            logger.error( "ClearQueueAisle: There is no queue to clear.");
            model.setResourceID("DeleteQueueError");
            model.setType(DialogScreensIfc.ERROR);
        }
        else
        {
            // Append the empt queue report to the journal text.
            String workstationID = Gateway.getProperty("application", "WorkstationID", null);
            String storeID = Gateway.getProperty("application", "StoreID", null);

            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);

            //boolean mailLetter = true;
            qtr = getQueuedTransactionsReport(cargo,storeID,workstationID);

            JournalManagerIfc jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
            if (jmi != null)
            {
                // Fix this when interface has been fixed.
                jmi.journal(qtr.getFormattedJournal());
            }
            else
            {
                logger.warn( "No journal manager found!");
            }
            try
            {
                PrintableDocumentManagerIfc printMgr = (PrintableDocumentManagerIfc)Gateway.getDispatcher().getManager(
                        PrintableDocumentManagerIfc.TYPE);
                printMgr.printReceipt((SessionBusIfc)bus, qtr);
                bus.mail(new Letter("DeleteQueue"), BusIfc.CURRENT);
            }
            catch (PrintableDocumentException e)
            {
                // Update printer status
                logger.error("Error printing register report", e.getNestedException());
                ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);
                DialogBeanModel model1 = new DialogBeanModel();

                String msg[] = new String[1];
                UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
                msg[0] = utility.retrieveDialogText(BundleConstantsIfc.PRINTER_OFFLINE_TAG,
                        BundleConstantsIfc.PRINTER_OFFLINE);

                model1.setResourceID("RetryCancel");
                model1.setType(DialogScreensIfc.RETRY_CANCEL);
                model1.setArgs(msg);

                // display dialog
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model1);
            }
        }
    }

    protected QueuedTransactionsReport getQueuedTransactionsReport(QueueCargo cargo,String storeId,String workstationId)
    {
        QueuedTransactionReportItem[] reportItems = QueuedTransactionsReport.getReportableQueuedTransactions();

        return new QueuedTransactionsReport(reportItems,storeId,workstationId,cargo.getOperator().getEmployeeID());
    }
}
