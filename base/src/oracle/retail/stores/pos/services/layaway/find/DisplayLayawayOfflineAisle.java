/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/find/DisplayLayawayOfflineAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:13 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:48 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:04 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:39 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/07/27 14:35:35  bvanschyndel
 *   @scr 2076 Changed offline dialog from acknowledgement to error type.
 *
 *   Revision 1.4  2004/07/27 02:13:00  bvanschyndel
 *   @scr 2076 Changed Database Offline dialog to an acknowledgement dialog.
 *   Layaway payments cannot be made offline.
 *
 *   Revision 1.3  2004/02/12 16:50:49  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:00:40   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   04 Sep 2002 17:06:20   dfh
 * allow pickup when offline when searching by customer
 * Resolution for POS SCR-1760: Layaway feature updates
 * 
 *    Rev 1.1   Aug 30 2002 13:19:52   jriggins
 * Now attempting to pull previously hardcoded strings from the bundles.
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:20:36   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:35:08   msg
 * Initial revision.
 * 
 *    Rev 1.3   08 Jan 2002 13:46:16   jbp
 * removed deprecated methods
 * Resolution for POS SCR-562: Trying to Find layaway offline causes the application to hang on No Layaway screen
 *
 *    Rev 1.1   29 Nov 2001 16:45:40   jbp
 * modified layaway dialog screens.
 * Resolution for POS SCR-335: Layaway Updates
 *
 *    Rev 1.0   Sep 21 2001 11:21:20   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.find;

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
    Displays the menu screen for finding layaway(s).

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class DisplayLayawayOfflineAisle extends PosLaneActionAdapter
{
    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        layaway offline screen id constant
    **/
    protected static final String LAYAWAY_OFFLINE_ERROR = "LayawayOfflineConfirm";
     /**
        delete layaway offline error message
    **/
    public static final String NO_LAYAWAY = "NoLayaway";

    //--------------------------------------------------------------------------
    /**
            Displays the no layaway delete error screen or the offline
            layaway confirmation screen.
            <P>
            @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc uiManager =
                        (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        FindLayawayCargoIfc cargo = (FindLayawayCargoIfc)bus.getCargo();
            // show the screen
        DialogBeanModel dialogModel = new DialogBeanModel();

        if (cargo.getLayawayOperation() == FindLayawayCargoIfc.LAYAWAY_DELETE)
        {                
            //No Layaway Dialog Screen
            UtilityManagerIfc utility =
                (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);            
            String args[] = new String[1];
            args[0] = utility.retrieveReportText("Delete", "Delete");
            dialogModel.setResourceID(NO_LAYAWAY);
            dialogModel.setType(DialogScreensIfc.ERROR);
            dialogModel.setArgs(args);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Offline");
        }
        else
        {
            dialogModel.setResourceID(LAYAWAY_OFFLINE_ERROR);
            dialogModel.setType(DialogScreensIfc.ERROR);
        }

        // display dialog
        uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

}
