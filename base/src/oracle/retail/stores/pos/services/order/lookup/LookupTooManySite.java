/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/lookup/LookupTooManySite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:34 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:59 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:22 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:29 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 16:03:42   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:12:18   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:41:26   msg
 * Initial revision.
 * 
 *    Rev 1.1   05 Nov 2001 17:37:42   baa
 * Code Review changes. Customer, Customer history Inquiry Options
 * Resolution for POS SCR-244: Code Review  changes
 *
 *    Rev 1.0   Sep 24 2001 13:01:12   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.lookup;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
public class LookupTooManySite extends PosSiteActionAdapter
{

    /**
       class name constant
    **/
    public static final String SITENAME = "LookupTooManySite";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Display an error message, wait for user acknowlegement.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        // get the POS UI manager
        POSUIManagerIfc uiManager =
            (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // show the screen
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("TooManyMatches");
        dialogModel.setType(DialogScreensIfc.ERROR);


        // display dialog
        uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);


    }   // arrive

    //----------------------------------------------------------------------
    /**
       Returns a string representation of the object.
       <P>
       @return String representation of object
       **
       //----------------------------------------------------------------------
       public String toString()
       {                                   // begin toString()
       // result string
       String strResult = new String("Class:  LookupTooManySite (Revision " +
       getRevisionNumber() +
       ")" + hashCode());
       } */

}
