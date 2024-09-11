/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/queue/ConfirmClearSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:08 mszekely Exp $
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
 *    2    360Commerce 1.1         3/10/2005 10:20:22 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:11 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:48:52  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:35:20  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Oct 02 2003 10:06:34   bwf
 * Removed deprecation because flow has been reinstated.  Also removed unused imports.
 * 
 *    Rev 1.1   Sep 25 2003 12:25:14   bwf
 * Deprecated.
 * Resolution for 3334: Feature Enhancement:  Queue Exception Handling
 * 
 *    Rev 1.0   Aug 29 2003 15:53:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:38:26   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:06:14   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:20:24   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:12:30   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:11:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.queue;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
    This site displays the Confirm Queue Clear screen.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class ConfirmClearSite extends PosSiteActionAdapter
{                                       // begin class DisplayTillOptionSite

    /**
       site name constant
    **/
    public static final String SITENAME = "ConfirmClearSite";
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
       This site displays the Confirm Queue Clear screen.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {                                   // begin arrive()

        POSUIManagerIfc ui
            = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("ConfirmClearQueue");
        model.setType(DialogScreensIfc.CONFIRMATION);
        //ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);

    }                                   // end arrive()

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

}                                       // end class DisplayTillOptionSite
