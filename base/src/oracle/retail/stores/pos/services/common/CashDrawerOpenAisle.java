/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/CashDrawerOpenAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:52 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:22 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:01 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:50 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/03 23:15:06  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:08  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:54:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   Jul 14 2003 15:12:10   mrm
 * Wait for drawer close; handle offline cash drawer
 * Resolution for POS SCR-3042: Device is Offline message does not appear during No Sale with Drawer offline
 * 
 *    Rev 1.2   05 Jun 2002 22:01:26   baa
 * support for  opendrawerfortrainingmode parameter
 * Resolution for POS SCR-1645: Training Mode Enhancements
 *
 *    Rev 1.1   23 May 2002 17:44:02   vxs
 * Removed unneccessary concatenations in logging statements.
 * Resolution for POS SCR-1632: Updates for Gap - Logging
 *
 *    Rev 1.0   Apr 29 2002 15:34:34   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:08:26   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:22:12   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:13:28   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;
// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//--------------------------------------------------------------------------
/**
    This aisle opens the cash drawer.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CashDrawerOpenAisle extends PosLaneActionAdapter
{
    /**
       revision number supplied by source-code control system
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Attempts to open cash drawer.
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // drawer-online indicator
        boolean drawerOnline = true;
        // letter to be mailed at end
        LetterIfc letter = null;

        POSUIManagerIfc ui =
            (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        try
        {


            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc)bus);

            // assuming cash drawer is closed
            pda.openCashDrawer();
            pda.waitForDrawerClose(); // blocks

            // Update cash drawer status to ONLINE
            ui.statusChanged(POSUIManagerIfc.CASHDRAWER_STATUS,
                             POSUIManagerIfc.ONLINE);

            CashDrawerCargoIfc cargo = (CashDrawerCargoIfc) bus.getCargo();
            cargo.setCashDrawerOnline(true);

        }
        catch (DeviceException e)
        {
            logger.warn(
                        "Exception: Unable to open cash drawer. " + e.getMessage() + "");

            // Update cash drawer status to OFFLINE
            ui.statusChanged(POSUIManagerIfc.CASHDRAWER_STATUS,
                             POSUIManagerIfc.OFFLINE);

            drawerOnline = false;

            letter = new Letter(CommonLetterIfc.CASH_DRAWER_OFFLINE);
        }

        // Don't send the letter if we put up the error dialog
        if (drawerOnline)
        {
            letter = new Letter(CommonLetterIfc.CASH_DRAWER_CLOSED);
        }

        if (letter != null)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    }

}
