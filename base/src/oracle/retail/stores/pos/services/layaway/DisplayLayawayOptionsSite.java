/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/DisplayLayawayOptionsSite.java /rgbustores_13.4x_generic_branch/2 2011/08/03 15:24:39 tksharma Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tksharma  08/03/11 - removed Find button code from arrive(bus)
 *    tksharma  08/03/11 - arrive(bus) removed disabling of 'Find' button
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:48 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:04 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:39 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/04/08 20:33:03  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *   Revision 1.3  2004/02/12 16:50:46  mcs
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
 *    Rev 1.0   Aug 29 2003 16:00:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:19:52   msg
 * Initial revision.
 * 
 *    Rev 1.1   28 Mar 2002 14:37:48   dfh
 * updates to turn off the cancel button when sale in progress and to cancel the new layaway once the trans has been burned and the customer linked
 * Resolution for POS SCR-398: Selecting cancel during Layaway cancels current transaction
 * 
 *    Rev 1.0   Mar 18 2002 11:34:26   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:20:50   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:08:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway;

//foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//------------------------------------------------------------------------------
/**
    Displays the layaway options screen.
    <P>       
    @version $Revision: /rgbustores_13.4x_generic_branch/2 $
**/
//------------------------------------------------------------------------------
public class DisplayLayawayOptionsSite extends PosSiteActionAdapter
{
    /**
        class name constant
    **/
    public static final String SITENAME = "DisplayLayawayOptionsSite";
    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    //--------------------------------------------------------------------------
    /**
       Displays the layaway options screen. Enables/disables Find button and
       Cancel button based upon whether a transaction is in progress.
       <P>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel pModel = new POSBaseBeanModel();
        NavigationButtonBeanModel gModel = new NavigationButtonBeanModel();
        NavigationButtonBeanModel nModel = new NavigationButtonBeanModel();

        LayawayCargo cargo = (LayawayCargo)bus.getCargo();

        // disable Find if transaction in progress
        if (cargo.getSaleTransaction() != null ||
            (cargo.getTenderableTransaction() != null) )
        {   // disable Cancel if sale transaction in progress
            if (cargo.getSaleTransaction() != null)
            {
                gModel.setButtonEnabled(CommonActionsIfc.CANCEL, false);
            }
            else
            {
                gModel.setButtonEnabled(CommonActionsIfc.CANCEL, true);
            }            
        }
        
        pModel.setLocalButtonBeanModel(nModel);
        pModel.setGlobalButtonBeanModel(gModel);
        ui.showScreen(POSUIManagerIfc.LAYAWAY_OPTIONS, pModel);
    }
}
