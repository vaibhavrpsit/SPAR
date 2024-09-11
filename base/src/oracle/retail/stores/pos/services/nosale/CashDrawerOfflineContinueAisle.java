/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/nosale/CashDrawerOfflineContinueAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:00 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
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
 *   Revision 1.2  2004/06/28 16:31:09  rsachdeva
 *   @scr 5548 Retry fixed so that it tries to Open Cash Drawer
 *
 *   Revision 1.1  2004/06/24 15:16:40  rsachdeva
 *   @scr 3960 Continue Offline Cash Drawer
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.nosale;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This aisle is traversed when the cash drawer responds as Offline and
    a Continue option is desired. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CashDrawerOfflineContinueAisle extends PosLaneActionAdapter
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
      cash drawer message tag
    **/
    protected static String CASH_DRAWER_OFFLINE_TAG = "RetryCancel.CashDrawerOffline";
    /**
      cash drawer message
    **/
    protected static String CASH_DRAWER_OFFLINE = "Cash drawer is offline.";
    /**
      print letter
    **/
    protected static final String PRINT = "Print";
    /**
       open cash drawer letter
    **/
    protected static final String OPEN_CASH_DRAWER = "OpenCashDrawer";
    /**
       rtetry continue resource id
    **/
    protected static final String RETRYCONTINUE = "RetryContinue";

    //----------------------------------------------------------------------
    /**
       Displays the cash-drawer-offline retry-continue screen. <P>
       @param  bus Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        String resourceID = RETRYCONTINUE;
        int resourceType = DialogScreensIfc.RETRY_CONTINUE;        
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ui.statusChanged(POSUIManagerIfc.CASHDRAWER_STATUS,
                         POSUIManagerIfc.OFFLINE);
        String msg[] = new String[1];
        msg[0] = utility.retrieveDialogText(CASH_DRAWER_OFFLINE_TAG,
                                            CASH_DRAWER_OFFLINE);
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(resourceID);
        model.setType(resourceType);
        model.setArgs(msg);
        model.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, OPEN_CASH_DRAWER);
        model.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, PRINT);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of the object.
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  CashDrawerOfflineContinueContinueAisle (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }                                   // end toString()

}
