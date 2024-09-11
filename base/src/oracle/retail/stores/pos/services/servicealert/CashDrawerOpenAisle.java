/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/servicealert/CashDrawerOpenAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:10 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    blarsen   03/31/10 - Required for case when Till is already open and
 *                         simply needs to be inserted into cash drawer.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.servicealert;

// Foundation imports
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.FinancialTotalsDataTransaction;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.DrawerIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

//--------------------------------------------------------------------------
/**
    This aisle opens the cash drawer.
**/
//--------------------------------------------------------------------------
public class CashDrawerOpenAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 117738876951924880L;
    
    //----------------------------------------------------------------------
    /**
       Attempts to open the cash drawer.
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        StatusBeanModel statusModel = new StatusBeanModel();
        SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();

        try
        {
            String letter = "CloseDrawer";

            if (!(cargo.getRegister().getWorkstation().isTrainingMode()))
            {
                POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);

                // assuming cash drawer is closed
                pda.openCashDrawer();
                // Update cash drawer status to ONLINE
                statusModel.setStatus(POSUIManagerIfc.CASHDRAWER_STATUS, POSUIManagerIfc.ONLINE);

                // set drawer status as occupied with current till
                RegisterIfc register = cargo.getRegister();

                register.getDrawer(DrawerIfc.DRAWER_PRIMARY).setDrawerStatus(
                    AbstractStatusEntityIfc.DRAWER_STATUS_OCCUPIED,
                    register.getCurrentTillID());

                // update drawer status in database
                try
                {
                    FinancialTotalsDataTransaction db = null;
                    db = (FinancialTotalsDataTransaction) DataTransactionFactory.create(DataTransactionKeys.FINANCIAL_TOTALS_DATA_TRANSACTION);
                    db.updateDrawerStatus(register);
                }
                catch (DataException e)
                {
                    logger.error("Exception: Unable to update cash drawer. " + e.getMessage() + "");
                }
            }
            else
            {
                letter = "Failure"; // should not happen, training mode for service alert is not supported (the fulfillment button should be disabled)
            }

            bus.mail(new Letter(letter), BusIfc.CURRENT);
        }
        catch (DeviceException e)
        {
            logger.warn("Exception: Unable to open cash drawer. " + e.getMessage() + "");

            // Update cash drawer status to OFFLINE
            statusModel.setStatus(POSUIManagerIfc.CASHDRAWER_STATUS, POSUIManagerIfc.OFFLINE);

            UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

            // set error message for dialog
            String msg[] = new String[1];
            msg[0] = utility.retrieveDialogText("RetryContinueCancel.CashDrawerOffline", "Cash drawer is offline.");

            // display the Retry/Continue dialog for the cash drawer
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("RetryContinueCancel");
            model.setType(DialogScreensIfc.RETRY_CONTINUE_CANCEL);
            model.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, "RetryCashDrawerOpen");
            model.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, "CloseDrawer");
            model.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, "Failure");
            model.setArgs(msg);
            model.setStatusBeanModel(statusModel);
            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }

    }
}
