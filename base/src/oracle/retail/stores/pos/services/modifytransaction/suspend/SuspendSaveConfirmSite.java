/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/suspend/SuspendSaveConfirmSite.java /main/11 2012/04/16 12:48:21 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     04/16/12 - Clean up code from forward port.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:17 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:36 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:16  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:47  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:02:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:15:26   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:39:38   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:31:26   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:09:52   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.suspend;
// java imports
// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    Site arrived upon successful save of suspended transaction.  An
    acknowledgement message is displayed.
    @version $Revision: /main/11 $
**/
//--------------------------------------------------------------------------
public class SuspendSaveConfirmSite extends PosSiteActionAdapter
{                                                                               // begin class SuspendSaveConfirmSite

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";

    //----------------------------------------------------------------------
    /**
       Display an acknowledgement message. <P>
       @param bus Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // get ui handle
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // set bean model
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("SuspendTransactionSaved");
        model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "SuspendedTransaction");
        // display dialog
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);

    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class. <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

}                                                                               // end class SuspendSaveConfirmSite
