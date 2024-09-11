/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/lookup/DisplayNoMatchSite.java /main/12 2013/05/10 15:49:24 rgour Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rgour     05/10/13 - ClassCastException ServiceAlertCargo cannot be cast
 *                         to OrderSearchCargoIfc
 *    jswan     04/29/13 - When transaction lookup for returns (by trans id,
 *                         customer number, etc.) fails to find a match, the
 *                         applicaiton gives the user the options to return by
 *                         item. The modification supports the same behavior
 *                         for lookup by Order.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:48 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:04 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:39 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:24  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:03:38   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:12:14   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:41:22   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 24 2001 13:01:12   MPM
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:10:38   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.lookup;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.order.common.OrderSearchCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
    Displays the info not found screen.
    <P>
    @version $Revision: /main/12 $
**/
//--------------------------------------------------------------------------
public class DisplayNoMatchSite extends PosSiteActionAdapter
{

    /**
       class name constant
    **/
    public static final String LANENAME = "DisplayNoMatchSite";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
       info not found screen id constant
    **/
    private static final String INFO_NOT_FOUND_ERROR = "INFO_NOT_FOUND_ERROR";

    //----------------------------------------------------------------------
    /**
       Display the info not found screen, wait for user acknowlegement.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        POSUIManagerIfc uiManager =
            (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        CargoIfc cargo = bus.getCargo();
        
        if (cargo instanceof OrderSearchCargoIfc && ((OrderSearchCargoIfc)cargo).isRetrieveForReturn()) 
        {
            // Get the ui manager
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("RetrieveTransactionNotFound");
            dialogModel.setType(DialogScreensIfc.CONFIRMATION);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, "Retry");
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "ReturnItem");
            // display the screen
            uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }   
        else
        {
            // show the screen
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID(INFO_NOT_FOUND_ERROR);
            dialogModel.setType(DialogScreensIfc.ERROR);
            //uiManager.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    
            // display dialog
            uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
    }

}
