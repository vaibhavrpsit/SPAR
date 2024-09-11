/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    mchell 03/06/13 - Scansheet tour refactoring
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

import java.util.ArrayList;
import java.util.HashMap;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.stock.ScanSheet;
import oracle.retail.stores.domain.stock.ScanSheetComponent;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.beans.ImageGridBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

/**
 * This site handles undo operations on scan sheet screens.
 */
public class ProcessUndoSite extends PosSiteActionAdapter
{

    private static final long serialVersionUID = -4907139209561945176L;

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
        NavigationButtonBeanModel nModel = new NavigationButtonBeanModel();
        ImageGridBeanModel igbm = cargo.getImageGridBeanModel();
        HashMap<String, ArrayList<ScanSheetComponent>> cMap = igbm.getScanSheet().getCategoryMap();
        HashMap<String, String> parentCategoryMap = igbm.getScanSheet().getCategoryParentMap();
        HashMap<String, String> descCategoryMap = igbm.getScanSheet().getCategroyDescMap();
        ImageGridBeanModel currentModel = igbm.getCurrentCategoryModel();

        String parentCategory = null;
        if (currentModel != null)
        {
            parentCategory = parentCategoryMap.get(igbm.getCurrentCategoryModel().getCategoryID());
        }
        if (Util.isEmpty(parentCategory))
        {
            if (igbm.getCurrentCategoryModel() == null)
            {
                // mail a letter to return to sale screen
                bus.mail(CommonLetterIfc.RETURN);
            }
            else
            {
                ScanSheetUtility.configureImageGridBeanModel(igbm, nModel);
                igbm.setLocalButtonBeanModel(nModel);
                igbm.setCurrentCategoryModel(null);
                igbm.setCategoryID(null);
                igbm.setCategoryDescription(null);
                igbm.setDisplayCategory(false);
                bus.mail(CommonLetterIfc.CONTINUE);
            }
        }
        else
        {
            ImageGridBeanModel cigbm = new ImageGridBeanModel(new ScanSheet(cMap.get(parentCategory), cMap,
                    parentCategoryMap, descCategoryMap));
            cigbm.setCurrentPageNumber(cargo.getReturnPageNumberMap().get(parentCategory));
            ScanSheetUtility.configureImageGridBeanModel(cigbm, nModel);
            cigbm.setLocalButtonBeanModel(nModel);
            cigbm.setCategoryID(parentCategory);
            cigbm.setCategoryDescription(descCategoryMap.get(parentCategory));
            igbm.setCurrentCategoryModel(cigbm);
            igbm.setDisplayCategory(true);
            cargo.setImageGridBeanModel(igbm);
            bus.mail(CommonLetterIfc.CONTINUE);
        }

    }

}
