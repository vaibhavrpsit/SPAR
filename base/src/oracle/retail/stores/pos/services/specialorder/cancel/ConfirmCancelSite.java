/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/specialorder/cancel/ConfirmCancelSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:02 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:30 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:21 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:11 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:52:02  mcs
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
 *    Rev 1.0   Aug 29 2003 16:07:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:02:00   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:48:14   msg
 * Initial revision.
 * 
 *    Rev 1.0   Dec 07 2001 16:40:04   dfh
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.specialorder.cancel; 
 
//foundation imports 
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------ 
/** 
    Displays the Confirm Action (Cancel) screen.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/ 
//------------------------------------------------------------------------------ 
public class ConfirmCancelSite extends PosSiteActionAdapter 
{ 
    /** 
        class name constant 
    **/ 
    public static final String SITENAME = "ConfirmCancelSite"; 
    /** 
        revision number for this class 
    **/ 
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /** 
        no link customer screen name 
    **/ 
    private static final String RESOURCE_ID = "ConfirmAction";
 
    //-------------------------------------------------------------------------- 
    /** 
       Displays Confirm Action screen when user Cancels the special order
       service. Sets the No buton letter to Failure and the Yes button letter to Continue.
       <P> 
       @param bus the bus arriving at this site 
    **/ 
    //-------------------------------------------------------------------------- 
    public void arrive(BusIfc bus) 
    { 
        POSUIManagerIfc ui=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // set arg strings to cancel 
        String args[] = new String[2];
        args[0] = "cancel"; 
        args[1] = "cancel"; 

        // Using "generic dialog bean". display the error dialog
        DialogBeanModel model = new DialogBeanModel();

        // Set buttons and arguments
        model.setResourceID(RESOURCE_ID);
        model.setType(DialogScreensIfc.CONFIRMATION);
        model.setArgs(args);
        model.setButtonLetter(DialogScreensIfc.BUTTON_NO,"Failure");
        model.setButtonLetter(DialogScreensIfc.BUTTON_YES,"Continue");

        // set and display the model
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }  
} 
