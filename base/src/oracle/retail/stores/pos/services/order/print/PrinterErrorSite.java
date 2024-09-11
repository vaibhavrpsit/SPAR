/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/print/PrinterErrorSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:34 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:29 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:23 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:26 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:27  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Jan 12 2004 16:47:54   DCobb
 * Removed printer offline halt behavior.
 * Resolution for 3502: Remove "Printer Offline Behavior" parameter
 * 
 *    Rev 1.1   Oct 17 2003 10:05:00   kll
 * SCR-2394: specific printer dialog text
 * 
 *    Rev 1.0   Aug 29 2003 16:03:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jul 08 2003 14:34:24   RSachdeva
 * For Print Order Use Case, the Retry/Cancel Dialog is shown
 * Resolution for POS SCR-2307: Select "Continue" at Device Offline" screen, Sp. Order status is not updated
 * 
 *    Rev 1.0   Apr 29 2002 15:11:42   msg
 * Initial revision.
 * 
 *    Rev 1.1   21 Mar 2002 10:17:32   dfh
 * cleanup, uses printer offline parm value to display correct screen
 * Resolution for POS SCR-1570: Wrong Printer Offline screens display during Order
 * 
 *    Rev 1.0   Mar 18 2002 11:41:52   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 24 2001 13:01:22   MPM
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:10:42   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.print;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.order.common.OrderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
    Displays printer offline dialog.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class PrinterErrorSite extends PosSiteActionAdapter
{
    /**
        class name constant
    **/
    public static final String SITENAME = "PrinterErrorSite";
    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       Printer offline behavior parameter constant
    **/
    public static final String PRINTEROFFLINEBEHAVIOR = "PrinterOfflineBehavior";

    //--------------------------------------------------------------------------
    /**
       Displays the printer offline dialog as indicated by the
       PrinterOfflineBehavior parameter.
       <p>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc     ui      = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        OrderCargo cargo = (OrderCargo) bus.getCargo();
        
        String msg[] = new String[1];
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        msg[0] = utility.retrieveDialogText("RetryContinue.PrinterOffline",
                                            "Printer is offline.");
        if (!cargo.viewOrder())
        {
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("PrinterOfflineProceed");
            // set Continue button letter to Failure
            model.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE,"Failure");
            model.setType(DialogScreensIfc.RETRY_CONTINUE);

            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else
        {
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("PrinterOffline");
            model.setType(DialogScreensIfc.RETRY_CANCEL);
            // set Cancel button letter to Failure
            model.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL,"Failure");  
          
            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
    } // arrive
} // PrinterErrorSite
