/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tilloptions/DisplayTillOptionsSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:19 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/17/2008 1:02:57 AM   Manas Sahu      Event
 *          originator changes
 *    3    360Commerce 1.2         3/31/2005 4:27:50 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:06 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:41 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/04/22 14:44:52  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Removed unused code.
 *
 *   Revision 1.4  2004/04/15 18:57:00  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Till reconcile service is now separate from till close.
 *
 *   Revision 1.3  2004/02/12 16:50:02  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:47:34  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:58:06   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:27:06   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:29:54   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:19:14   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:14:48   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tilloptions;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.EventOriginatorInfoBean;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//------------------------------------------------------------------------------
/**
    This site displays the options available from the Till Options screen.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class DisplayTillOptionsSite extends PosSiteActionAdapter
{                                       // begin class DisplayTillOptionSite

    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    /** Function key for Reconcile button */
    public static final String RECONCILE_FUNCTION_KEY = "F6";

    //--------------------------------------------------------------------------
    /**
       Displays till options menu.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {                                   // begin arrive()
        TillOptionsCargo cargo = (TillOptionsCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        POSBaseBeanModel model = (POSBaseBeanModel)ui.getModel(POSUIManagerIfc.TILL_OPTIONS);
        NavigationButtonBeanModel nModel = new NavigationButtonBeanModel();
        
        // Display the Reconcile button based on the Till Reconcile system setting
        if (cargo.getRegister().isTillReconcile())
        {           
            nModel.setButtonEnabled(CommonActionsIfc.RECONCILE, true);
            String reconcileLabel = utility.retrieveText(
                    "TillOptionsButtonSpec", 
                    BundleConstantsIfc.TILL_BUNDLE_NAME,
                    CommonActionsIfc.RECONCILE,
                    CommonActionsIfc.RECONCILE_TEXT);
            nModel.setButtonLabel(CommonActionsIfc.RECONCILE, reconcileLabel);
            nModel.setButtonKeyLabel(CommonActionsIfc.RECONCILE, RECONCILE_FUNCTION_KEY);
        }
        else
        {
            nModel.setButtonEnabled(CommonActionsIfc.RECONCILE, false);
            nModel.setButtonLabel(CommonActionsIfc.RECONCILE, "");
            nModel.setButtonKeyLabel(CommonActionsIfc.RECONCILE, "");
        }
        model.setLocalButtonBeanModel(nModel);
        
        EventOriginatorInfoBean.setEventOriginator("DisplayTillOptionsSite.arrive");
        
        // get ui reference and display screen 
        ui.showScreen(POSUIManagerIfc.TILL_OPTIONS, model);

    }                                   // end arrive()

}                                       // end class DisplayTillOptionSite
