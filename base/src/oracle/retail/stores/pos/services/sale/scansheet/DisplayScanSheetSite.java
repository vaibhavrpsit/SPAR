/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    mchell 03/06/13 - Scansheet tour refactoring
 *    mchell 03/04/13 - Scansheet refactoring
 *    cgreen 10/30/12 - Logo click browser cleanup
 *    cgreen 09/04/12 - Code cleanup, method name cleanup and refactor to allow
 *                      for single-clicks and ESC back to previous category
 *    asinto 02/28/12 - XbranchMerge asinton_bug-13732985 from
 *                      rgbustores_13.4x_generic_branch
 *    asinto 02/27/12 - refactored the flow so that items added from scan sheet
 *                      doesn't allow for a hang or mismatched letter.
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    jkoppo 04/19/11 - XbranchMerge jkoppolu_bug11820604-offline_fix from main
 *    jkoppo 03/09/11 - I18N changes.
 *    jkoppo 03/07/11 - Modified the code to take care of the case when there
 *                      are no scan sheet items configured.
 *    jkoppo 03/04/11 - Several code tweaks and performance improvements
 *    jkoppo 03/02/11 - New site in scan sheet tour
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.scansheet;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ImageGridBeanModel;

/**
 * Displays scan sheet screen.
 */
public class DisplayScanSheetSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -4741565133005749308L;

    /*
     * (non-Javadoc)
     * @see
     * oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive
     * (oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ScanSheetCargo cargo = (ScanSheetCargo) bus.getCargo();
        ImageGridBeanModel igbm = cargo.getImageGridBeanModel();

        // Display items in the current category if the display flag is true.
        if (igbm.isDisplayCategory())
        {
            this.showScreen(igbm.getCurrentCategoryModel(), bus);
            // Reset the display flag
            igbm.setDisplayCategory(false);
        }
        else
        {
            this.showScreen(igbm, bus);
        }

    }

    private void showScreen(ImageGridBeanModel igbm, BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.SCAN_SHEET, igbm);
    }

}
