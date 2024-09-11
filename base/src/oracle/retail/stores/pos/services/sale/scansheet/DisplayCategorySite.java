/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    mchell 03/06/13 - Scansheet tour refactoring
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.scansheet;

import java.util.ArrayList;
import java.util.HashMap;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.stock.ScanSheet;
import oracle.retail.stores.domain.stock.ScanSheetComponent;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ImageGridBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

/**
 * Creates bean model object for selected scan sheet category.
 */
public class DisplayCategorySite extends PosSiteActionAdapter
{

    private static final long serialVersionUID = 3772861764307184988L;

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
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ImageGridBeanModel igbm = cargo.getImageGridBeanModel();
        String categoryName = ((ImageGridBeanModel) ui.getModel(POSUIManagerIfc.SCAN_SHEET)).getCategoryID();

        if (!Util.isEmpty(categoryName))
        {
            ((ScanSheetCargo) bus.getCargo()).getReturnPageNumberMap().put(categoryName, igbm.getCurrentPageNumber());
        }

        HashMap<String, ArrayList<ScanSheetComponent>> cMap = igbm.getScanSheet().getCategoryMap();
        HashMap<String, String> parentCategoryMap = igbm.getScanSheet().getCategoryParentMap();
        HashMap<String, String> descCategoryMap = igbm.getScanSheet().getCategroyDescMap();
        String selectedItem = null;

        if (igbm.getCurrentCategoryModel() != null)
        {
            selectedItem = igbm.getCurrentCategoryModel().getSelectedItemID();
        }
        else
        {
            selectedItem = igbm.getSelectedItemID();
        }
        ArrayList<ScanSheetComponent> itemList = cMap.get(selectedItem);

        // If the selected category has no items configured, show a
        // warning dialog.
        if (itemList == null)
        {
            ScanSheetUtility.showErrorDialog(bus, "ScanSheetCategoryConfigWarning", CommonLetterIfc.UNDO);
        }
        else
        {
            ImageGridBeanModel cigbm = new ImageGridBeanModel(new ScanSheet(itemList, cMap, parentCategoryMap,
                    descCategoryMap));
            ScanSheetUtility.configureImageGridBeanModel(cigbm, nModel);
            cigbm.setLocalButtonBeanModel(nModel);
            cigbm.setCategoryID(selectedItem);
            cigbm.setCategoryDescription(descCategoryMap.get(selectedItem));
            igbm.setCurrentCategoryModel(cigbm);
            igbm.setDisplayCategory(true);
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }

    }

}
