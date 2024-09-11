/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/OpenCashDrawerAisle.java /main/14 2014/01/28 17:48:01 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   01/27/14 - do not unlock the container when updating the status
 *    cgreene   01/24/14 - do not unlock screen when updating cash drawer
 *                         status
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   01/29/09 - do not wait for cash drawer to close to continue
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:11 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:46 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:49 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/04/21 17:03:20  mweis
 *   @scr 0 Removed interesting System.out.println statements concerning the cash drawer
 *
 *   Revision 1.3  2004/02/12 16:48:22  mcs
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
 *    Rev 1.1   Jan 06 2004 14:11:16   rsachdeva
 * Cash Drawer Status
 * Resolution for POS SCR-3517: Status of Cash Drawer remains Offline on Device Status even when Cash Drawer is online and used by POS
 * 
 *    Rev 1.0   Nov 04 2003 11:17:48   epd
 * Initial revision.
 * 
 *    Rev 1.1   Oct 24 2003 10:12:04   epd
 * removed dead code
 * 
 *    Rev 1.0   Oct 23 2003 17:29:50   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 17 2003 13:06:46   epd
 * Initial revision.
 * 
 *    Rev 1.0   Aug 29 2003 16:07:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:00:18   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:48:50   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 10 2002 18:01:22   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.0   Sep 21 2001 11:26:14   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:13:44   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

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

/**
 * This aisle opens the cash drawer. It does not wait for it close.
 * CloseCashDrawerSite and CashDrawerCloseAisle will accomplish this.
 * 
 * @version $Revision: /main/14 $
 */
public class OpenCashDrawerAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 7212838623366737214L;

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
     * Set attempts to open the cash drawer.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        POSDeviceActions pda = null;

        boolean drawerOnline = true;
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        // Initially set cash drawer status to ONLINE
        ui.statusChanged(POSUIManagerIfc.CASHDRAWER_STATUS, POSUIManagerIfc.ONLINE, POSUIManagerIfc.DO_NOT_UNLOCK_CONTAINER);

        try
        {
            pda = new POSDeviceActions((SessionBusIfc)bus);

            // assuming cash drawer is closed
            pda.openCashDrawer();

            // don't wait for cash drawer. CloseCashDrawerSite in completesale tour will do that.
        }
        catch (DeviceException e)
        {
            logger.warn("Unable to open cash drawer.", e);

            // Update cash drawer status to OFFLINE
            ui.statusChanged(POSUIManagerIfc.CASHDRAWER_STATUS, POSUIManagerIfc.OFFLINE, POSUIManagerIfc.DO_NOT_UNLOCK_CONTAINER);

            drawerOnline = false;

            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

            // set error message for dialog
            String msg[] = new String[1];
            msg[0] = utility.retrieveDialogText("RetryContinueCancel.CashDrawerOffline", "Cash drawer is offline.");

            // display the Retry/Continue dialog for the cash drawer
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("RetryContinue");
            model.setType(DialogScreensIfc.RETRY_CONTINUE);
            model.setArgs(msg);
            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }

        // Don't send the letter if we put up the error dialog
        if (drawerOnline)
        {
            bus.mail(new Letter("ExitTender"), BusIfc.CURRENT);
        }

    }
}
