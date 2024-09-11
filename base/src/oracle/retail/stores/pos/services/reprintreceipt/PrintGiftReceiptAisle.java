/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/reprintreceipt/PrintGiftReceiptAisle.java /main/13 2012/01/27 09:17:46 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   01/27/12 - XbranchMerge
 *                         cgreene_prevent_reprint_extra_button_presses from
 *                         rgbustores_13.4x_generic_branch
 *    cgreene   01/27/12 - do not unlock container when updating status
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    nkgautam  10/15/10 - forward port : reprint receipt of gift receipt
 *                         doesn't update the reprint receipt count in EJ
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *5    360Commerce 1.4         1/9/2008 3:25:21 AM    Subhasis Gorai  Code
 *     changed for fixing CR# 28576: ABLE TO SELECT ITEM WITHOUT PRESSING
 *     SPACEBAR
 *
 *4    360Commerce 1.3         4/30/2007 4:56:45 PM   Alan N. Sinton  CR 26484
 *     - Merge from v12.0_temp.
 *3    360Commerce 1.2         3/31/2005 4:29:29 PM   Robert Pearse
 *2    360Commerce 1.1         3/10/2005 10:24:23 AM  Robert Pearse
 *1    360Commerce 1.0         2/11/2005 12:13:26 PM  Robert Pearse
 *
 Revision 1.5  2004/08/17 18:30:09  jdeleau
 @scr 6529 Correct errors introduced when fixing this defect earlier
 *
 Revision 1.4  2004/07/28 18:28:41  jdeleau
 @scr 6539 Gift Receipts set on a transaction level need to print
 all line items on the same gift receipt.
 *
 Revision 1.3  2004/04/27 22:25:53  dcobb
 @scr 4452 Feature Enhancement: Printing
 Code review updates.
 *
 Revision 1.2  2004/04/26 19:51:14  dcobb
 @scr 4452 Feature Enhancement: Printing
 Add Reprint Select flow.
 *
 Revision 1.1  2004/04/22 17:39:00  dcobb
 @scr 4452 Feature Enhancement: Printing
 Added REPRINT_SELECT screen and flow to Reprint Receipt use case..
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.reprintreceipt;

import java.util.ArrayList;

import oracle.retail.stores.pos.receipt.GiftReceiptParameterBeanIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
 * Print the gift receipt for the selected item or items.
 * 
 * @version $Revision: /main/13 $
 */
public class PrintGiftReceiptAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -7240584709353834380L;

    /**
     * Print the gift receipt for the selected item or items and send a letter.
     * 
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // get transaction from cargo
        ReprintReceiptCargo cargo = (ReprintReceiptCargo) bus.getCargo();
        SaleReturnTransactionIfc trans = (SaleReturnTransactionIfc)cargo.getTransaction();

        // initialize variables
        boolean sendMail = true;

        LineItemsModel beanModel = null;

        // get ui and utility manager
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        PrintableDocumentManagerIfc printMgr = (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);

        // First figure out which items in the transaction are eligible for gift receipts,if
        // this is printing because of gift receipt items on a current transaction
        ArrayList<SaleReturnLineItemIfc> giftReceiptItems = new ArrayList<SaleReturnLineItemIfc>();
        if(trans.isTransactionGiftReceiptAssigned())
        {
            // get lineItems for the transaction
            SaleReturnLineItemIfc lineItems[] = (SaleReturnLineItemIfc[])trans.getLineItems();
            for(int i=0; i<lineItems.length; i++)
            {
                if(lineItems[i].isGiftReceiptItem())
                {
                    giftReceiptItems.add(lineItems[i]);
                }
            }
        }
        // From reprint receipt, add the selected items as the gift receipt items
        // to be reprinted.
        else
        {
            if (ui.getModel(getScreenID()) instanceof LineItemsModel)
                beanModel = (LineItemsModel) ui.getModel(getScreenID());
            else
                beanModel = (LineItemsModel) cargo.getModel();
            AbstractTransactionLineItemIfc[] lineItems = beanModel.getLineItems();
            int rows[] = beanModel.getSelectedRows();
            for(int i=0; i<rows.length; i++)
            {
                if(lineItems[rows[i]] instanceof SaleReturnLineItemIfc)
                {
                    giftReceiptItems.add((SaleReturnLineItemIfc)lineItems[rows[i]]);
                }
            }
            // START FIX 6365423
            cargo.setSelectedIndexes(new int[0]);
            // END FIX 6365423
        }
        // Convert them to an array
        SaleReturnLineItemIfc[] grli = giftReceiptItems.toArray(new SaleReturnLineItemIfc[giftReceiptItems.size()]);

        // If the transaction was set to print out a gift receipt, then print for all items in the gift receipt,
        if(grli.length > 0)
        {
            try
            {
                GiftReceiptParameterBeanIfc bean = printMgr.getGiftReceiptParameterBeanInstance((SessionBusIfc)bus, trans, grli);
                printMgr.printReceipt((SessionBusIfc)bus, bean);
                updatePrinterStatus(ui, POSUIManagerIfc.ONLINE);
            }
            catch (PrintableDocumentException e)
            {
                if (e.getCause() instanceof DeviceException)
                {
                    handleDeviceError(bus, (DeviceException)e.getCause(), beanModel);
                }
                sendMail = false;
            }
        }
        
        cargo.setReprintReceiptCount(cargo.getReprintReceiptCount() + 1);

        if (sendMail)
        {
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }

    }

    /**
     * Returns REPRINT_SELECT screen ID.
     *
     * @return
     */
    protected String getScreenID()
    {
        return POSUIManagerIfc.REPRINT_SELECT;
    }

    /**
     * Update the printer status on the screen, to show that the
     * printer is online or offline
     *
     * @param uiManager
     * @param mode one of POSUIManagerIfc.ONLINE or POSUIManagerIfc.OFFLINE
     */
    public void updatePrinterStatus(POSUIManagerIfc uiManager, boolean mode)
    {
        // Update printer status
        StatusBeanModel statusModel = new StatusBeanModel();
        statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, mode);
        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        baseModel.setStatusBeanModel(statusModel);
        uiManager.setModel(POSUIManagerIfc.SHOW_STATUS_ONLY, baseModel, false);
    }

    /**
     * Show the error dialog caused by some kind of device exception
     *
     * @param bus containing the various managers we need to show the error screen
     * @param e Exception causing the error
     */
    public void handleDeviceError(BusIfc bus, DeviceException e,LineItemsModel beanModel)
    {
        // Get the ui and utility managers
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        logger.warn("Unable to print gift receipt: " + e.getMessage() + "");

        //set the mode. in cargo to maintain the the selected line items for a retry
        ReprintReceiptCargo cargo = (ReprintReceiptCargo) bus.getCargo();
        cargo.setModel(beanModel);


        // Update printer status
        updatePrinterStatus(ui, POSUIManagerIfc.OFFLINE);

        if (e.getCause() != null)
        {
            logger.warn("DeviceException.NestedException:\n" + Util.throwableToString(e.getCause()));
        }

        String msg[] = new String[1];
        if (e.getErrorCode() != DeviceException.UNKNOWN)
        {
            msg[0] = utility.retrieveDialogText("RetryContinue.PrinterOffline", "Printer is offline.");
        }
        else
        {
            msg[0] = utility.retrieveDialogText("RetryContinue.UnknownPrintingError",
                "An unknown error occurred while printing.");
        }

        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("RetryContinue");
        model.setType(DialogScreensIfc.RETRY_CONTINUE);
        model.setArgs(msg);
        // display dialog
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}
