/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ImageGridBeanModel.java /main/3 2013/03/06 12:53:26 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    mchell 03/06/13 - Scansheet tour refactoring
 *    cgreen 08/31/12 - correct abbreviated method and member names
 *    jkoppo 03/02/11 - maxNumberOfItems are now set using the application
 *                      property - maxGridSize
 *    jkoppo 03/02/11 - New bean model for scan sheet screen
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.domain.stock.ScanSheet;
import oracle.retail.stores.foundation.tour.gate.Gateway;

public class ImageGridBeanModel extends POSBaseBeanModel
{
    private static final long serialVersionUID = -1988044652729552349L;

    private static final String APPLICATION_PROPERTY_GROUP_NAME = "application";

    public final int maxNumberOfItems;

    private ScanSheet scanSheet;
    private String selectedItemID;
    private boolean categorySelected;
    private String categoryID;
    private String categoryDescription;
    private int currentPageNumber;
    public int numberOfPages;
    private ImageGridBeanModel currentCategoryModel;
    private boolean displayCategory;


    /**
     * Constructor
     * 
     * @param scanSheet
     */
    public ImageGridBeanModel(ScanSheet scanSheet)
    {
        this.scanSheet = scanSheet;
        int i = Integer.parseInt(Gateway.getProperty(APPLICATION_PROPERTY_GROUP_NAME, "maxGridSize", "4"));
        this.maxNumberOfItems = i * i;
    }

    public ImageGridBeanModel getCurrentCategoryModel()
    {
        return currentCategoryModel;
    }

    public void setCurrentCategoryModel(ImageGridBeanModel currentCategoryModel)
    {
        this.currentCategoryModel = currentCategoryModel;
    }

    public boolean isCategorySelected()
    {
        return categorySelected;
    }

    public void setCategorySelected(boolean categorySelected)
    {
        this.categorySelected = categorySelected;
    }

    public String getSelectedItemID()
    {
        return selectedItemID;
    }

    public int getNumberOfPages()
    {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages)
    {
        this.numberOfPages = numberOfPages;
    }

    public int getCurrentPageNumber()
    {
        return currentPageNumber;
    }

    public void setCurrentPageNumber(int currentPageNumber)
    {
        this.currentPageNumber = currentPageNumber;
    }

    public void setSelectedItemID(String selectedItemID)
    {
        this.selectedItemID = selectedItemID;
    }

    public ScanSheet getScanSheet()
    {
        return scanSheet;
    }

    public void setScanSheet(ScanSheet scanSheet)
    {
        this.scanSheet = scanSheet;
    }

    public void setCategoryID(String categoryName)
    {
        this.categoryID = categoryName;
    }

    public String getCategoryID()
    {
        return categoryID;
    }

    public void setCategoryDescription(String categoryDescription)
    {
        this.categoryDescription = categoryDescription;
    }

    public String getCategoryDescription()
    {
        return categoryDescription;
    }
    
    /**
     * @return the displayCategory
     */
    public boolean isDisplayCategory()
    {
        return displayCategory;
    }

    /**
     * @param displayCategory the displayCategory to set
     */
    public void setDisplayCategory(boolean displayCategory)
    {
        this.displayCategory = displayCategory;
    }

}