/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/initialize/CashDrawerOpenAisle.java /main/13 2013/12/13 14:58:37 abananan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abananan  12/13/13 - maintain current till id while updating offline
 *                         tillclose in db
 *    npoola    09/24/10 - changed the parameter name from
 *                         TrainingModeOpenDrawer to OpenDrawerInTrainingMode
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:22 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:01 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:50 PM  Robert Pearse   
 *
 *   Revision 1.9  2004/06/03 14:47:43  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.8  2004/04/20 13:10:59  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.7  2004/04/13 12:57:46  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.6  2004/03/31 20:54:04  bjosserand
 *   @scr 4093 Transaction Reentry
 *
 *   Revision 1.5  2004/03/31 20:19:01  bjosserand
 *   @scr 4093 Transaction Reentry
 *
 *   Revision 1.4  2004/03/30 23:52:25  bjosserand
 *   @scr 4093 Transaction Reentry
 *
 *   Revision 1.3  2004/02/12 16:48:20  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   08 Nov 2003 01:24:26   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.0   Nov 04 2003 19:03:34   cdb
 * Initial revision.
 * Resolution for 3430: Sale Service Refactoring
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.initialize;

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
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
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

     @version $Revision: /main/13 $
**/
//--------------------------------------------------------------------------
public class CashDrawerOpenAisle extends PosLaneActionAdapter
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";
    public static final String TRAINING_MODE_OPEN_DRAWER = "OpenDrawerInTrainingMode";
    //----------------------------------------------------------------------
    /**
       Set attempts to open the cash drawer.
       <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        StatusBeanModel statusModel = new StatusBeanModel();
        SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();

        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        
        boolean openDrawerForTraining = false;
        try
        {
            openDrawerForTraining = "Y".equalsIgnoreCase(pm.getStringValue(TRAINING_MODE_OPEN_DRAWER));
        }
        catch (ParameterException pe)
        {
            logger.error("Could not retrieve setting for OpenDrawerInTrainingMode Parameter");
        }
        try
        {
            String letter = "CloseDrawer";

            if (!(cargo.getRegister().getWorkstation().isTrainingMode()) || openDrawerForTraining)
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
                    db.updateRegisterStatus(register);
                }
                catch (DataException e)
                {
                    logger.error("Exception: Unable to update cash drawer. " + e.getMessage() + "");
                }
            }
            else
            {
                letter = "Continue";
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
            model.setArgs(msg);
            model.setStatusBeanModel(statusModel);
            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }

    }
}
