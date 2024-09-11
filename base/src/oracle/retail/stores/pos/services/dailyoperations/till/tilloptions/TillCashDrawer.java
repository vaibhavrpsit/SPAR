/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tilloptions/TillCashDrawer.java /main/12 2014/07/09 13:10:48 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     06/26/14 - Forward port fix for handling the condition of two
 *                         registers opened with same till with one or both
 *                         offline at time of open.
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    npoola    09/24/10 - changed the parameter name from
 *                         TrainingModeOpenDrawer to OpenDrawerInTrainingMode
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:11:52 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:30:29 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:10 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:03 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     10/31/2005 13:39:47    Rohit Sachdeva  Cash
 *         Drawer Status Online/Offline
 *    3    360Commerce1.2         3/31/2005 15:30:29     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:26:10     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:15:03     Robert Pearse
 *
 *   Revision 1.6  2004/03/31 20:54:04  bjosserand
 *   @scr 4093 Transaction Reentry
 *
 *   Revision 1.5  2004/03/31 20:19:01  bjosserand
 *   @scr 4093 Transaction Reentry
 *
 *   Revision 1.4  2004/03/30 23:52:26  bjosserand
 *   @scr 4093 Transaction Reentry
 *
 *   Revision 1.3  2004/02/12 16:50:02  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:47:34  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:58:08   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   05 Jun 2002 22:01:42   baa
 * support for  opendrawerfortrainingmode parameter
 * Resolution for POS SCR-1645: Training Mode Enhancements
 *
 *    Rev 1.1   24 May 2002 18:54:36   vxs
 * Removed unncessary concatenations from log statements.
 * Resolution for POS SCR-1632: Updates for Gap - Logging
 *
 *    Rev 1.0   Apr 29 2002 15:27:06   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:29:56   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:19:16   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:14:48   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tilloptions;
// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.TillCargo;
import oracle.retail.stores.pos.services.dailyoperations.till.tillclose.TillCloseCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

import org.apache.log4j.Logger;

/**
 * Interface class for all Till open and close cash drawer operations. Catches
 * device exceptions and displays dialogs that a device error occurred or to
 * close the cash drawer. Issues Letters and logs the exceptions.
 * 
 * @version $Revision: /main/12 $
 */
public class TillCashDrawer implements ParameterConstantsIfc
{
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(TillCashDrawer.class);;

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * Opens the cash drawer, assumes the cash drawer is closed. Catches
     * DeviceExceptions, logs them, and displays a dialog noting the error .
     * 
     * @param BusIfc bus
     */
    static public void tillOpenCashDrawer(BusIfc bus, String description)
    {
        Letter letter = new Letter(CommonLetterIfc.CONTINUE);
        TillCargo cargo = (TillCargo) bus.getCargo();

        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        boolean openDrawerForTraining = false;
        try
        {
            openDrawerForTraining = pm.getBooleanValue(BASE_OpenDrawerInTrainingMode);
        }
        catch (ParameterException pe)
        {
            logger.error("Could not retrieve setting for OpenDrawerInTrainingMode Parameter");
        }

        boolean isTrainingMode = cargo.getRegister().getWorkstation().isTrainingMode();
        // If the opendrawer parameter is set to false bypass openning the drawer.
        if (!isTrainingMode || openDrawerForTraining)
        {
            //--------------------------------------------------------------------------
            //   Opens the cash drawer then issues Continue letter, catches exception,
            //   log it, updates the cash drawer status and issue open cash drawer
            //   dialog that issues Retry or Cancel letters
            //
            //--------------------------------------------------------------------------

            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            // Update cash drawer status to ONLINE
            ui.statusChanged(POSUIManagerIfc.CASHDRAWER_STATUS, POSUIManagerIfc.ONLINE);

            if (logger.isInfoEnabled())
                logger.info("tillOpenCashDrawer called");

            try
            {

                POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);

                // assume cash drawer is closed
                pda.openCashDrawer();

                bus.mail(letter, BusIfc.CURRENT);
            }

            catch (DeviceException e)
            {
                logger.warn("Exception: Unable to open cash drawer.", e);

                // Update cash drawer status to OFFLINE
                ui.statusChanged(POSUIManagerIfc.CASHDRAWER_STATUS, POSUIManagerIfc.OFFLINE);

                // set error message for dialog
                String msg[] = new String[1];
                msg[0] = description;

                // display the Retry/Cancel dialog for the cash drawer open error
                DialogBeanModel model = new DialogBeanModel();
                model.setResourceID("CashDrawerRetryContinueCancel");
                model.setType(DialogScreensIfc.RETRY_CONTINUE_CANCEL);
                model.setArgs(msg);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);

            } // end - catch openCashDrawer
        } // end -check for training mode
        else
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    } // end - tillOpenCashDrawer

    /**
     * Closes the cash drawer, determines if the cash drawer is already open.
     * Displays dialog to close the cash drawer if it is open. Catches
     * DeviceExceptions, logs them, and mails Continue letter. Updates the cash
     * drawer status.
     * 
     * @param BusIfc bus
     */
    static public void tillCloseCashDrawer(BusIfc bus)
    {
        if (logger.isInfoEnabled())
            logger.info("tillCloseCashDrawer called");
        // if cash drawer is open then display close cash drawer acknowledgement
        // dialog to remind use to close the cash drawer else issue Continue letter
        // catches exception, logs the exception, and then issues Continue letter
        // when cash drawer is closed then issues Continue letter

        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        StatusBeanModel statusModel = new StatusBeanModel();
        baseModel.setStatusBeanModel(statusModel);
        
        if(bus.getCargo() instanceof TillCloseCargo)
        {
            TillCloseCargo cargo = (TillCloseCargo)bus.getCargo();
            RegisterIfc register = cargo.getRegister();
            register.setTillClose(false);
        }
        
        try
        {
            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);

            Boolean pdaOpen = pda.isOpen();

            // if cash drawer open then display close drawer acknowledgement
            // dialog to remind user to close the cash drawer now
            if (pdaOpen.booleanValue() == true)
            {
                ui.showScreen(POSUIManagerIfc.CLOSE_DRAWER, baseModel);

                try
                {
                    pda.waitForDrawerClose(); // wait til drawer closes (blocks)

                    // Update cash drawer status to ONLINE
                    statusModel.setStatus(POSUIManagerIfc.CASHDRAWER_STATUS, POSUIManagerIfc.ONLINE);
                }
                catch (DeviceException e)
                {
                    logger.warn("Unable to wait for cash drawer.", e);

                    // Update cash drawer status to OFFLINE
                    statusModel.setStatus(POSUIManagerIfc.CASHDRAWER_STATUS, POSUIManagerIfc.OFFLINE);
                }
            }
        } // end - outer try block

        catch (DeviceException e)
        {
            logger.warn("Unable to close cash drawer.", e);

            // Update cash drawer status to OFFLINE
            statusModel.setStatus(POSUIManagerIfc.CASHDRAWER_STATUS, POSUIManagerIfc.OFFLINE);
        }

        Letter letter = new Letter("Continue");
        bus.mail(letter, BusIfc.CURRENT);

    } // end - tillCloseCashDrawer

    /**
     * Returns a string representation of the object.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = new String("Class:  TillCashDrawer (Revision " + getRevisionNumber() + ")" + hashCode());

        // pass back result
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

}