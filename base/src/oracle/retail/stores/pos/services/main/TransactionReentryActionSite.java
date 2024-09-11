/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/main/TransactionReentryActionSite.java /rgbustores_13.4x_generic_branch/2 2011/07/07 15:55:47 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   07/07/11 - removed obsolete FinancialNetworkManager call to
 *                         prevent null pointer exception
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:30:35 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:26:24 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:15:16 PM  Robert Pearse   
 * $
 * Revision 1.12  2004/07/14 18:47:09  epd
 * @scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation
 *
 * Revision 1.11  2004/04/16 14:39:39  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * Revision 1.10  2004/04/15 22:13:32  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * Revision 1.9  2004/04/14 15:35:43  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * Revision 1.8  2004/04/09 19:31:44  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * Revision 1.7  2004/04/09 19:08:14  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * Revision 1.6  2004/04/01 16:04:10  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * Revision 1.5  2004/03/31 20:54:04  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * Revision 1.4  2004/03/31 20:19:01  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * Revision 1.3  2004/03/26 21:18:20  cdb
 * @scr 4204 Removing Tabs.
 *
 * Revision 1.2  2004/03/26 15:56:29  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * Revision 1.1  2004/03/17 23:51:13  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.main;

import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ApplicationFrame;
import oracle.retail.stores.pos.ui.beans.BrowserStatusBean;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site displays the Transaction Reentry ON or OFF dialog screen 
 * according to the current state of the transaction reentry flag.
 * 
 */
@SuppressWarnings("serial")
public class TransactionReentryActionSite extends PosSiteActionAdapter
{

    public static final String SITENAME = "TransactionReentryActionSite";

    public static final String JOURNAL_TEXT = "Transaction Re-entry Mode";

    protected final String APPLICATION_PROP_KEY = "application";
    protected final String DIALOG_TEXT_KEY_ON = "TransReentryOn";
    protected final String DIALOG_TEXT_KEY_OFF = "TransReentryOff";

    //--------------------------------------------------------------------------
    /**
     * @param bus
     *            the bus arriving at this site
     */
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        MainCargo cargo = (MainCargo) bus.getCargo();

        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.cashierNameChanged(cargo.getOperator().getPersonName().getFirstLastName());

        WorkstationIfc ws = cargo.getRegister().getWorkstation();
        // reentryMode reflects the last setting of this option. Thus, it tells us the
        // opposite of what we want to do here. For example, if reentryMode is false,
        // that means we want to turn in on, journal turning it on, etc.
        boolean reentryMode = ws.isTransReentryMode();
        // reentryModeTarget is the mode we want to set (opposite of the current setting).
        boolean reentryModeTarget = !reentryMode;

        DialogBeanModel dialogModel = new DialogBeanModel();

        // set new mode in workstation
        ws.setTransReentryMode(reentryModeTarget);
        // Update transaction reentry status
        ui.statusChanged(POSUIManagerIfc.TRANS_REENTRY_STATUS, reentryModeTarget); // update status bar

        if (reentryModeTarget)
        {
            // turning transaction reentry mode on
            dialogModel.setResourceID(DIALOG_TEXT_KEY_ON); // show dialog "turning on trans entry"
        }
        else
        {
            // turning transaction reentry mode off
            dialogModel.setResourceID(DIALOG_TEXT_KEY_OFF); // show dialog "turning off trans entry"
        }

        TransactionReentryControllerIfc controller = new TransactionReentryController();
        controller.journalTransaction(bus, reentryModeTarget, cargo.getOperator());

        // enable or disable the cash drawer based on whether enabling or disabling trans. reentry
        POSDeviceActions deviceActions = new POSDeviceActions((SessionBusIfc) bus);
        // If Transaction Reentry is being turned on, we want to disable the cash drawer.
        // If Transaction Reentry is being turned off, we want to enable the cash drawer.
        try
        {
            deviceActions.setCashDrawerEnabled(!reentryModeTarget);
        }
        catch (DeviceException de)
        {            
            logger.error("Device exception setting Cash Drawer Enabled to " + reentryMode, de);
        }

        // set background depending on transaction reentry mode
        BrowserStatusBean bsn = new BrowserStatusBean(); // any old bean will do for our purposes
        ApplicationFrame applFrame = bsn.getApplicationFrame();
        applFrame.setTransReentryMode(reentryModeTarget);

        dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
}
