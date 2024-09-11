/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/specialorder/add/DisplayNoLinkCustomerAisle.java /main/11 2012/08/27 11:23:03 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  08/17/12 - wptg - removed placeholder from key NoLinkedCustomer
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:48 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:04 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:39 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:52:01  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:25  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:07:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:02:06   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:48:04   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 10 2002 18:01:18   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.0   Dec 04 2001 14:55:10   dfh
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.specialorder.add;

//foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
    Displays the No Linked Customer screen.
    <P>
    @version $Revision: /main/11 $
**/
//------------------------------------------------------------------------------
public class DisplayNoLinkCustomerAisle extends PosLaneActionAdapter
{
    /**
        class name constant
    **/
    public static final String LANENAME = "DisplayNoLinkCustomerAisle";
    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";
    /**
        no link customer screen name
    **/
    private static final String RESOURCE_ID = "NoLinkedCustomer";

    //--------------------------------------------------------------------------
    /**
       Displays No Linked Customer screen when user Cancels from the customer
       service. Sets the No buton letter to Failure and the Yes button letter to Retry.
       <P>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean". display the error dialog
        DialogBeanModel model = new DialogBeanModel();

        // Set buttons and arguments
        model.setResourceID(RESOURCE_ID);
        model.setType(DialogScreensIfc.CONFIRMATION);

        model.setButtonLetter(DialogScreensIfc.BUTTON_NO, "Failure");
        model.setButtonLetter(DialogScreensIfc.BUTTON_YES, "Retry");

        // set and display the model
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}
