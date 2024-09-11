/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/scansheet/ScanSheetCargo.java /main/5 2013/09/05 10:36:16 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    abonda 09/04/13 - initialize collections
 *    mchell 03/06/13 - Removed unused new visit attribute
 *    cgreen 09/04/12 - Code cleanup, method name cleanup and refactor to allow
 *                      for single-clicks and ESC back to previous category
 *    asinto 02/28/12 - XbranchMerge asinton_bug-13732985 from
 *                      rgbustores_13.4x_generic_branch
 *    asinto 02/27/12 - refactored the flow so that items added from scan sheet
 *                      doesn't allow for a hang or mismatched letter.
 *    jkoppo 03/04/11 - Introduced returnPageNoMap for supporting 'return' to
 *                      original page rather than the first page.
 *    jkoppo 03/02/11 - New Cargo for scan sheet service
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.scansheet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.ui.beans.ImageGridBeanModel;

public class ScanSheetCargo implements CargoIfc, Serializable
{
    private static final long serialVersionUID = -4047305408354225643L;

    private ImageGridBeanModel imageGridBeanModel;

    private boolean isNewVisitToScanSheet;

    /**
     * This map holds the return page numbers for the categories Required during
     * return from one category to other
     */
    private Map<String, Integer> returnPageNumberMap = new HashMap<String, Integer>(0);

    /**
     * selected scan sheet item ID.
     */
    private String selectedScanSheetItemID;

    public Map<String, Integer> getReturnPageNumberMap()
    {
        return returnPageNumberMap;
    }

    public void setReturnPageNumber(Map<String,Integer> returnPageNumberMap)
    {
        this.returnPageNumberMap = returnPageNumberMap;
    }

    public ImageGridBeanModel getImageGridBeanModel()
    {
        return imageGridBeanModel;
    }

    public void setImageGridBeanModel(ImageGridBeanModel imageGridBeanModel)
    {
        this.imageGridBeanModel = imageGridBeanModel;
    }

    /**
     * Sets the selected scan sheet item.
     * 
     * @param itemID
     */
    public void setSelectedScanSheetItemID(String itemID)
    {
        this.selectedScanSheetItemID = itemID;
    }

    /**
     * Gets the selected scan sheet item.
     * 
     * @return the selected scan sheet item.
     */
    public String getSelectedScanSheetItemID()
    {
        return this.selectedScanSheetItemID;
    }
    private String categoryID;
    private String categoryDescription;
    public void setScansheetCategoryID(String categoryID)
    {
        this.categoryID = categoryID;
    }
    public String getScansheetCategoryID()
    {
        return this.categoryID;
    }
    public void setScansheetCategoryDescription(String categoryDescription)
    {
        this.categoryDescription = categoryDescription;
    }
    public String getScansheetCategoryDescription()
    {
        return this.categoryDescription;
    }
}
