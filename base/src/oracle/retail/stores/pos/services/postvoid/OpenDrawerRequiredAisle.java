/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/postvoid/OpenDrawerRequiredAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:51 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:11 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:47 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:49 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/05/11 16:05:29  blj
 *   @scr 4603 - fixed for post void of giftcard issue/reload/redeem/credit
 *
 *   Revision 1.3  2004/02/12 16:48:15  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:28:20  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Nov 04 2003 11:16:04   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 23 2003 17:28:34   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 17 2003 13:03:22   epd
 * Initial revision.
 * 
 *    Rev 1.0   Aug 29 2003 16:05:04   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:08:06   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:44:08   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 10 2002 18:00:38   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.0   Sep 21 2001 11:22:26   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:11:32   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.postvoid;

// Foundation imports
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
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

//--------------------------------------------------------------------------
/**
    This aisle opens the cash drawer.

     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class OpenDrawerRequiredAisle extends PosLaneActionAdapter
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

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
        boolean drawerOnline = true;
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        StatusBeanModel statusModel = new StatusBeanModel();
        ui.showScreen(POSUIManagerIfc.CLOSE_DRAWER, new POSBaseBeanModel());

        try
        {
            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);

            // assuming cash drawer is closed
            pda.openCashDrawer();
            // Update cash drawer status to ONLINE
            statusModel.setStatus(POSUIManagerIfc.CASHDRAWER_STATUS, POSUIManagerIfc.ONLINE);
        }
        catch (DeviceException e)
        {
            logger.warn( "Exception: Unable to open cash drawer. " + e.getMessage() + "");

            // Update cash drawer status to OFFLINE
            statusModel.setStatus(POSUIManagerIfc.CASHDRAWER_STATUS, POSUIManagerIfc.OFFLINE);

            drawerOnline = false;
            // set error message for dialog
            UtilityManagerIfc utility =
              (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

            String msg[] = new String[1];
            msg[0] = utility.retrieveDialogText("RetryContinueCancel.CashDrawerOffline",
                                                "Cash drawer is offline.");

            // display the Retry/Continue dialog for the cash drawer
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("RetryContinue");
            model.setType(DialogScreensIfc.RETRY_CONTINUE);
            model.setArgs(msg);
            model.setStatusBeanModel(statusModel);
            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }

        // Don't send the letter if we put up the error dialog
        if (drawerOnline)
        {
            bus.mail(new Letter("CheckForRedeem"), BusIfc.CURRENT);
        }

    }
}
