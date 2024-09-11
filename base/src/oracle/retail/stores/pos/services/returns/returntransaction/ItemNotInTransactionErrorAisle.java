/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/ItemNotInTransactionErrorAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     08/20/10 - Provide a more descriptive error message where an
 *                         item is not found in the retrieved transaction.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/26/10 - Fixed warning messages.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:32 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:29 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:39 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/02/23 13:54:52  baa
 *   @scr 3561 Return Enhancements to support item size
 *
 *   Revision 1.3  2004/02/12 16:51:53  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:30  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:06:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:04:28   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:46:40   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:25:48   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:58   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returntransaction;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This road is traveled when application detects no items have been selected
    to be returned.
    @deprecated in 13.3; no longer used.
**/
//--------------------------------------------------------------------------
public class ItemNotInTransactionErrorAisle extends PosLaneActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = -5218171713387756909L;
    
    /**
       Constant for error screen
    **/
    public static final String NO_SELECTED_ITEM = "NoSelectedItem";

    //----------------------------------------------------------------------
    /**
       This road is traveled when application detects no items have been
       selected to be returned.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        // Using "generic dialog bean".
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("ITEM_NOT_IN_TRANS");
        dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Retry");
        
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of the object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  NoSelectedItemErrorAisle (Revision " +
                                      ")" + hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()
}
