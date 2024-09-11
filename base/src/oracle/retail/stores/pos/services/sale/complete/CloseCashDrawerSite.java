/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/complete/CloseCashDrawerSite.java /main/15 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 03/06/12 - prevent npe when runnign without devices
 *    cgreen 02/15/11 - move constants into interfaces and refactor
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    cgreen 02/04/09 - strip out cash drawer warning logic and move to
 *                      CheckCashInDrawerSite
 *    nkgaut 11/17/08 - Check Added for Cash Warning when paramter set to 0.0
 *    nkgaut 10/20/08 - Check added for cash drawer warning message

 * ===========================================================================
     $Log:
      3    360Commerce 1.2         3/31/2005 4:27:27 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:20:16 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:10:02 PM  Robert Pearse
     $
     Revision 1.3.4.1  2004/12/21 16:55:27  lzhao
     @scr 7863: call ui.setModel() conditionally.

     Revision 1.3  2004/02/12 16:48:18  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:28:20  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
     updating to pvcs 360store-current
 *
 *    Rev 1.0   Nov 05 2003 14:14:54   rsachdeva
 * Initial revision.
 * Resolution for POS SCR-3430: Sale Service Refactoring
 * ===================================================
 */
package oracle.retail.stores.pos.services.sale.complete;

import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
 * This site waits for the user to close the cash drawer if necessary.
 * 
 * @version $Revision: /main/15 $
 */
public class CloseCashDrawerSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -1275845206422136611L;

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * Wait for the drawer to close or for the user to press 'Enter'.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        POSDeviceActions drawer = new POSDeviceActions((SessionBusIfc)bus);
        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        StatusBeanModel statusModel = new StatusBeanModel();
        String currentScreen = null;
        try
        {
            // if cash drawer open then prompt to close and wait
            if (Boolean.TRUE.equals(drawer.isOpen()))
            {
                try
                {
                    currentScreen = ui.getActiveScreenID();

                    // Don't change the prompt if it's already there
                    if (!currentScreen.equals(POSUIManagerIfc.ISSUE_CHANGE)
                            && !currentScreen.equals(POSUIManagerIfc.ISSUE_REFUND)
                            && !currentScreen.equals(POSUIManagerIfc.CLOSE_DRAWER))
                    {
                        ui.showScreen(POSUIManagerIfc.CLOSE_DRAWER, baseModel);
                    }
                }
                catch (UIException uie)
                {
                    logger.warn("Unable to get the current screen ID.");
                }

                try
                {
                    drawer.waitForDrawerClose(); // blocks
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
        }
        catch (DeviceException e)
        {
            logger.warn("Unable to close cash drawer.", e);
            // Update cash drawer status to OFFLINE
            statusModel.setStatus(POSUIManagerIfc.CASHDRAWER_STATUS, POSUIManagerIfc.OFFLINE);
        }

        baseModel.setStatusBeanModel(statusModel);
        if ((currentScreen != null) && !currentScreen.equals(POSUIManagerIfc.ISSUE_REFUND)
                && !currentScreen.equals(POSUIManagerIfc.ISSUE_CHANGE)
                && !currentScreen.equals(POSUIManagerIfc.CLOSE_DRAWER))
        {
            ui.setModel(POSUIManagerIfc.CLOSE_DRAWER, baseModel);
        }

        // clear line display device of leftover tender information
        try
        {
            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc)bus);
            pda.clearText();
        }
        catch (DeviceException e)
        {
            logger.warn("Unable to use Line Display.", e);
        }

        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }
}