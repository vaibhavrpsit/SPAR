/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/create/DisplayNoLayawayAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:14 mszekely Exp $
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
 *   Revision 1.5  2004/04/08 20:33:02  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *   Revision 1.4  2004/03/03 23:15:10  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:47  mcs
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
 *    Rev 1.0   Aug 29 2003 16:00:24   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.4   Feb 18 2003 16:02:28   crain
 * Added reason for deprecation
 * Resolution for 1760: Layaway feature updates
 * 
 *    Rev 1.3   Dec 26 2002 15:05:18   crain
 * Added the release when deprecation occured
 * Resolution for 1760: Layaway feature updates
 * 
 *    Rev 1.2   Dec 24 2002 16:23:08   crain
 * Deprecated
 * Resolution for 1760: Layaway feature updates
 * 
 *    Rev 1.1   Aug 29 2002 13:22:46   jriggins
 * Replaced hardcoded string with dialog bundle string
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:21:16   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:34:36   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:20:56   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:08:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.create; 
 
//foundation imports 
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
 
//------------------------------------------------------------------------------ 
/** 
    Displays the no layaway screen.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @deprecated in release 6.0, obsolete
**/ 
//------------------------------------------------------------------------------ 
public class DisplayNoLayawayAisle extends LaneActionAdapter 
{ 
    /** 
        class name constant 
    **/ 
    public static final String LANENAME = "DisplayNoLayawayAisle"; 
    /** 
        revision number for this class 
    **/ 
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /** 
        no link customer screen name 
    **/ 
    private static final String RESOURCE_ID = "NoLayaway";
 
    //-------------------------------------------------------------------------- 
    /** 
       Displays the layaway No Layaway screen.
       <P> 
       @param bus the bus arriving at this site 
    **/ 
    //-------------------------------------------------------------------------- 
    public void traverse(BusIfc bus) 
    { 
        POSUIManagerIfc ui=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // set arg strings to layaway 
        String args[] = new String[1];
        UtilityManagerIfc utility = 
            (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        args[0] = utility.retrieveDialogText("New", "New");

        // Using "generic dialog bean". display the error dialog
        DialogBeanModel model = new DialogBeanModel();

        // Set model to same name as dialog 
        // Set button and arguments
        model.setResourceID(RESOURCE_ID);
        model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        model.setArgs(args);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);

        // set and display the model
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }  
} 
