/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ItemInfoBeanModel.java /main/30 2014/06/11 13:22:15 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    abhinavs  10/03/14 - Setting selected clearance index from the dropdown
 *                         to include it in verifying the condition if atleast one
 *                         search criteria is given.
 *    yiqzhao   09/22/14 - Add style and item type attributes.
 *    jswan     06/06/14 - Modified to support a tabbed UI for the item info bean
 *                         for the ICE project.
 *    abhinavs  05/12/14 - Filtering Item search results enhancement
 *    hyin      11/12/12 - re-work on item search result screen to make it
 *                         editable. So a new search can be performed.
 *    sthall    05/30/12 - Enhanced RPM Integration - Clearance Pricing
 *    cgreen    12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    sgu       06/08/10 - enhance ItemNotFoundPriceCodeBean to display external
 *                         order quantity and description
 *    cgreen    05/28/10 - convert to oracle packaging
 *    cgreen    05/27/10 - convert to oracle packaging
 *    cgreen    05/27/10 - convert to oracle packaging
 *    cgreen    05/26/10 - convert to oracle packaging
 *    cgreen    04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreen    04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda    01/03/10 - update header date
 *    cgreen    09/03/09 - XbranchMerge cgreene_bug8394467-timer from
 *                         rgbustores_13.1x_branch
 *    cgreen    09/03/09 - refactored Image to ImageIcon and added field for image
 *                         blob
 *    cgreen    03/30/09 - removed item name column from item image table
 *    nkgaut    12/01/08 - Addition of a class variable for ILRM
 *    ranojh    10/23/08 - Fixed UnitOfMeasure I18N changes
 *    ranojh    10/21/08 - Changes for POS for UnitOfMeasure I18N
 *    atirke    09/29/08 - Changes for item images added place holders
 *
 *
 * ===========================================================================
 $Log:
 5    360Commerce 1.4         1/22/2006 11:45:25 AM  Ron W. Haight
 removed references to com.ibm.math.BigDecimal
 4    360Commerce 1.3         12/13/2005 4:42:45 PM  Barry A. Pape
 Base-lining of 7.1_LA
 3    360Commerce 1.2         3/31/2005 4:28:31 PM   Robert Pearse
 2    360Commerce 1.1         3/10/2005 10:22:27 AM  Robert Pearse
 1    360Commerce 1.0         2/11/2005 12:11:37 PM  Robert Pearse
 $
 Revision 1.3  2004/03/16 17:15:17  build
 Forcing head revision

 Revision 1.2  2004/02/11 20:56:26  rhafernik
 @scr 0 Log4J conversion and code cleanup

 Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 updating to pvcs 360store-current


 *
 *    Rev 1.1   Dec 17 2003 11:21:52   baa
 * return enhancements
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 *
 *    Rev 1.0   Aug 29 2003 16:10:50   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 14:55:36   msg
 * Initial revision.
 *
 *    Rev 1.1   25 Apr 2002 18:52:30   pdd
 * Removed unnecessary BigDecimal instantiations.
 * Resolution for POS SCR-1610: Remove inefficient instantiations of BigDecimal

 * ===========================================================================
 */

package oracle.retail.stores.pos.ui.beans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.ImageIcon;

import oracle.retail.stores.common.item.ExtendedItemData;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.stock.ItemColorIfc;
import oracle.retail.stores.domain.stock.ItemSizeIfc;
import oracle.retail.stores.domain.stock.ItemStyleIfc;
import oracle.retail.stores.domain.stock.ItemTypeIfc;
import oracle.retail.stores.domain.stock.UnitOfMeasureIfc;
import oracle.retail.stores.domain.store.DepartmentIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;

/**
 * This class is the data model for ItemInfoBean.
 */
public class ItemInfoBeanModel extends POSBaseBeanModel
{
    private static final long serialVersionUID = 1205106906786638920L;

    /**
     * Revision Number furnished by TeamConnection.
     */
    public static final String revisionNumber = "$Revision: /main/30 $";

    public static final String EACH = "Each";

    /**
     * Item number.
     */
    protected String itemNumber = null;

    /**
     * Item description.
     */
    protected String itemDescription = "";

    /**
     * Item department.
     */
    protected String itemDept = null;
    
    /**
     * item type
     */
    protected String itemType = null;

    /**
     * item style
     */
    protected String itemStyle = null;
    
    /**
     * Unit of Measure.
     */
    protected String unitOfMeasure = null;

    /**
     * Item Size.
     */
    protected String itemSize = null;

    /**
     * isSizeRequired Flag.
     */
    protected boolean itemSizeRequired = false;

    /**
     * Taxable Flag.
     */
    protected boolean taxable = false;

    /**
     * Discountable Flag.
     */
    protected boolean discountable = false;

    /**
     * BigDecimal price of item.
     */
    protected BigDecimal price = BigDecimal.ZERO;

    /**
     * Item Manufacturer.
     */
    protected String itemManufacturer = null;

    /**
     * Planogram Id for the item.
     */
    protected String[] planogramID = null;

    /**
     * This flag indicates whether the item can be searched by Manufacturer.
     */
    protected boolean searchItemByManufacturer = false;

    /**
     * This flag indicates whether Planogram ID should be displayed.
     */
    protected boolean usePlanogramID = false;

    /**
     * This is the image variable
     */
    protected ImageIcon image;

    /**
     * The image as a straight byte array.
     */
    protected byte[] imageBlob;

    /**
     * place holder for image location
     */
    protected String imageLocation;

    /**
     * flag for error image
     */
    protected boolean imageError;

    /**
     * flag for blob
     *
     * @return
     */
    protected boolean blobImage;

    /**
     * flag for loading image
     */
    protected boolean loadingImage;

    /**
     * flag for empty image
     */

    protected boolean emptyImage;

    /**
     * Item Level Message
     */
    protected String itemLevelMessage;
    
    /**
     * Clearance Indicator
     */
    protected boolean onClearance;
    
    /**
     * the list of UOMs
     */
    protected UnitOfMeasureIfc[] uomList = null; 
    
    /**
     * the list of item colors
     */
    protected ItemColorIfc[] colorList = null;

    /**
     * the list of item sizes
     */
    protected ItemSizeIfc[] sizeList = null;
    
    /**
     * the list of item styles
     */
    protected ItemStyleIfc[] styleList = null;
    
    /**
     * the list of item types
     */
    protected ItemTypeIfc[] typeList = null;   

    /**
        the list of department names
     **/
    protected DepartmentIfc[] deptList = null;
    
    /** Default dropdown list **/
    protected String[] yesAndNo = null;
    
    /** Dropdown list for Tax field**/
    protected String[] yesAndNoTax = null;
    
    /** Dropdown list for Clearance field**/
    protected String[] yesAndNoClearance = null;
    
    /** Dropdown list for Discount field**/
    protected String[] yesAndNoDiscount = null;
    
    protected String colorDesc = null;
    
    /** Indicates if tabbed panel should be displayed. */
    protected boolean showExtendedDataUI = true;
    
    /** List of recommended items */
    protected ArrayList<ExtendedItemData> recommendedItems = null;
    
    /** Details about the item */
    protected String detail = null;

    protected int selectedColorIdx = -1;
    protected int selectedDeptIdx = -1;
    protected int selectedDiscountableIdx = -1;
    protected int selectedSizeIdx = -1;
    protected int selectedStyleIdx = -1;
    protected int selectedTaxableIdx = -1;
    protected int selectedTypeIdx = -1;
    protected int selectedUomIdx = -1;
    protected int selectedClearanceIdx = -1;

    
    /**
     * This flag denotes if user opted for filtering already searched results
     */
    protected boolean nonFilteringItemSearch=true;

    /**
     * Gets the item description.
     *
     * @return String the item description
     */
    public String getItemDescription()
    {
        return itemDescription;
    }

    /**
     * Gets the item number.
     *
     * @return String the item number
     */
    public String getItemNumber()
    {
        return itemNumber;

    }

    /**
     * Gets the item price.
     *
     * @return BigDecimal the item price
     */
    public BigDecimal getPrice()
    {
        return price;
    }

    /**
     * Gets the item Dept.
     *
     * @return String the item dept
     */
    public String getItemDept()
    {
        return itemDept;
    }

    /**
     * Gets the unit of Measure.
     *
     * @return String the unit of Measure
     */
    public String getUnitOfMeasure()
    {
        return unitOfMeasure;
    }

    /**
     * Gets the item manufacturer.
     *
     * @return String itemManufacturer
     */
    public String getItemManufacturer()
    {
        return itemManufacturer;
    }

    /**
     * Gets the planogram id.
     *
     * @return String planogram id
     */
    public String[] getPlanogramID()
    {
        return planogramID;
    }

    /**
     * Gets the taxable flag.
     *
     * @return boolean taxable flag
     */
    public boolean isTaxable()
    {
        return taxable;
    }

    /**
     * Gets the discountable flag.
     *
     * @return boolean discountable flag
     */
    public boolean isDiscountable()
    {
        return discountable;
    }

    /**
     * Sets the item description.
     *
     * @param flag the item taxable flag
     */
    public void setTaxableFlag(boolean flag)
    {
        this.taxable = flag;
    }

    /**
     * Sets the item discountable flag.
     *
     * @param flag the item discountable flag
     */
    public void setDiscountableFlag(boolean flag)
    {
        this.discountable = flag;
    }
    
    /**
     * directly set UOM string
     * @param uom
     */
    public void setUnitOfMeasure(String uom)
    {
        this.unitOfMeasure = uom;
    }
    
    /**
     * Sets the unit of measure.
     *
     * @param uom unit of measure
     */
    public void setUnitOfMeasure(UnitOfMeasureIfc uom)
    {
        String unit = null;
        UtilityManagerIfc utility = (UtilityManagerIfc)Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        if (uom == null)
        {
            this.unitOfMeasure = utility.retrieveText("Common", BundleConstantsIfc.COMMON, "Each", "Each", uiLocale);
            // this.unitOfMeasure = new String("Each");
        }
        else
        {
            unit = uom.getName(uiLocale).trim();
            if (unit != null && !unit.equals(""))
            {
                this.unitOfMeasure = unit;
            }
            else
            {
                this.unitOfMeasure = utility
                        .retrieveText("Common", BundleConstantsIfc.COMMON, "Each", "Each", uiLocale);
                // this.unitOfMeasure = new String(EACH);
            }

        }
    }
    
    /**
     * Sets the item dept.
     *
     * @param string the item dept
     */
    public void setItemDept(String dept)
    {
        this.itemDept = dept;
    }

    /**
     * Sets the item description.
     *
     * @param number the item description
     */
    public void setItemDescription(String description)
    {
        this.itemDescription = description;
    }

    /**
     * Sets the item number.
     *
     * @param number the item number
     */
    public void setItemNumber(String number)
    {
        this.itemNumber = number;
    }

    /**
     * Sets the item price.
     *
     * @param price the item price
     */
    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    /**
     * Returns the item type
     *
     * @return String the type
     */
    public String getItemType()
    {
        return itemType;
    }

    /**
     * Sets the item size
     *
     * @param String the size
     */
    public void setItemType(String type)
    {
        itemType = type;
    }
    
    /**
     * Returns the item type
     *
     * @return String the type
     */
    public String getItemStyle()
    {
        return itemStyle;
    }

    /**
     * Sets the item size
     *
     * @param String the size
     */
    public void setItemStyle(String style)
    {
        itemStyle = style;
    }    
    
    /**
     * Returns the item size
     *
     * @return String the size
     */
    public String getItemSize()
    {
        return itemSize;
    }

    /**
     * Sets the item size
     *
     * @param String the size
     */
    public void setItemSize(String value)
    {
        itemSize = value;
    }

    /**
     * Indicates if item size info is required
     *
     * @return boolean itemSizeRequired flag
     */
    public boolean isItemSizeRequired()
    {
        return itemSizeRequired;
    }

    /**
     * Sets itemSizeRequired flag
     *
     * @param boolean itemSizeRequired flag
     */
    public void setItemSizeRequired(boolean b)
    {
        itemSizeRequired = b;
    }

    /**
     * Sets the item manufacturer.
     *
     * @param string itemManufacturer
     */
    public void setItemManufacturer(String itemManufacturer)
    {
        this.itemManufacturer = itemManufacturer;
    }

    /**
     * Sets the planogram id.
     *
     * @param string planogramID
     */
    public void setPlanogramID(String[] planogramID)
    {
        this.planogramID = planogramID;
    }

    /**
     * To confirm if the item can be searched using the Manufacturer search
     * criteria.
     *
     * @return boolean value of the parameter " SearchItemByManufacturer"
     */
    public boolean isSearchItemByManufacturer()
    {
        return searchItemByManufacturer;
    }

    /**
     * To set the boolean value confirming if the item can be searched using the
     * Manufacturer criteria.
     *
     * @param boolean value of the parameter " SearchItemByManufacturer"
     */
    public void setSearchItemByManufacturer(boolean searchItemByManufacturer)
    {
        this.searchItemByManufacturer = searchItemByManufacturer;
    }

    /**
     * To confirm if the planogram Id for the item needs to be displayed
     *
     * @return boolean value of the parameter " UsePlanogramID"
     */
    public boolean isUsePlanogramID()
    {
        return usePlanogramID;
    }

    /**
     * To set the boolean value confirming if the planogram ID needs to be
     * displayed
     *
     * @param boolean value of the parameter " UsePlanogramID"
     */
    public void setUsePlanogramID(boolean usePlanogramID)
    {
        this.usePlanogramID = usePlanogramID;
    }

    public String getImageLocation()
    {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation)
    {
        this.imageLocation = imageLocation;
    }

    public ImageIcon getImage()
    {
        return this.image;

    }

    public void setImage(ImageIcon image)
    {
        this.image = image;
    }

    public boolean isImageError()
    {
        return imageError;
    }

    public void setImageError(boolean imageError)
    {
        this.imageError = imageError;
    }

    public boolean isBlobImage()
    {
        return blobImage;
    }

    public void setBlobImage(boolean blobImage)
    {
        this.blobImage = blobImage;
    }

    public boolean isLoadingImage()
    {
        return loadingImage;
    }

    public void setLoadingImage(boolean loadingImage)
    {
        this.loadingImage = loadingImage;
    }

    public boolean isEmptyImage()
    {
        return emptyImage;
    }

    public void setEmptyImage(boolean emptyImage)
    {
        this.emptyImage = emptyImage;
    }

    public String getItemLevelMessage()
    {
        return itemLevelMessage;
    }

    public void setItemLevelMessage(String itemLevelMessage)
    {
        this.itemLevelMessage = itemLevelMessage;
    }

    /**
     * @return the imageBlob
     */
    public byte[] getImageBlob()
    {
        return imageBlob;
    }
    
    public void setImageBlob(byte[] imgBlob)
    {
        this.imageBlob = imgBlob;
    }

    /**
     * Reset all the item image related fields.
     */
    public void clearImageInfo()
    {
        image = null;
    }
    
    /**
     * Returns clearance indicator, true if item is on clearance, false otherwise.
     *
     * @return boolean onClearance flag
     */
    public boolean isOnClearance() 
    {
        return onClearance;
    }
    
    /**
     * To set the boolean value confirming that item is on clearance.
     *
     * @param boolean value of the parameter "onClearance"
     */
    public void setOnClearance(boolean onClearance) 
    {
        this.onClearance = onClearance;
    }

    public UnitOfMeasureIfc[] getUomList() {
        return uomList;
    }

    public void setUomList(UnitOfMeasureIfc[] uomList) {
        this.uomList = uomList;
    }

    public ItemTypeIfc[] getTypeList() {
        return typeList;
    }
    
    public void setTypeList(ItemTypeIfc[] typeList) {
        this.typeList = typeList;
    }
    
    public ItemColorIfc[] getColorList() {
        return colorList;
    }

    public void setColorList(ItemColorIfc[] colorList) {
        this.colorList = colorList;
    }

    public ItemSizeIfc[] getSizeList() {
        return sizeList;
    }

    public void setSizeList(ItemSizeIfc[] sizeList) {
        this.sizeList = sizeList;
    }

    public DepartmentIfc[] getDeptList() {
        return deptList;
    }

    public void setDeptList(DepartmentIfc[] deptList) {
        this.deptList = deptList;
    }

    public String[] getYesAndNo() {
        return yesAndNo;
    }

    public void setYesAndNo(String[] yesAndNo) {
        this.yesAndNo = yesAndNo;
    }

    public String getColorDesc() {
        return colorDesc;
    }

    public void setColorDesc(String colorDesc) {
        this.colorDesc = colorDesc;
    }

    /**
     * Gets the <code>showExtendedDataUI</code> value.
     * @return the showExtendedDataUI
     */
    public boolean isShowExtendedDataUI()
    {
        return showExtendedDataUI;
    }

    /**
     * Sets the <code>showExtendedDataUI</code> value.
     * @param showExtendedDataUI the showExtendedDataUI to set
     */
    public void setShowExtendedDataUI(boolean showExtendedDataUI)
    {
        this.showExtendedDataUI = showExtendedDataUI;
    }

    /**
     * Gets the <code>recommendedItems</code> value.
     * @return the recommendedItems
     */
    public ArrayList<ExtendedItemData> getRecommendedItems()
    {
        return recommendedItems;
    }

    /**
     * Sets the <code>recommendedItems</code> value.
     * @param recommendedItems the recommendedItems to set
     */
    public void setRecommendedItems(ArrayList<ExtendedItemData> recommendedItems)
    {
        this.recommendedItems = recommendedItems;
    }

    /**
     * Gets the <code>detail</code> value.
     * @return the detail
     */
    public String getDetail()
    {
        return detail;
    }

    /**
     * Sets the <code>detail</code> value.
     * @param detail the detail to set
     */
    public void setDetail(String detail)
    {
        this.detail = detail;
    }

    public int getSelectedDeptIdx() {
        return selectedDeptIdx;
    }

    public void setSelectedDeptIdx(int selectedDeptIdx) {
        this.selectedDeptIdx = selectedDeptIdx;
    }

    public int getSelectedTypeIdx() {
        return selectedTypeIdx;
    }

    public void setSelectedTypeIdx(int selectedTypeIdx) {
        this.selectedTypeIdx = selectedTypeIdx;
    }
    
    public int getSelectedStyleIdx() {
        return selectedStyleIdx;
    }

    public void setSelectedStyleIdx(int selectedStyleIdx) {
        this.selectedStyleIdx = selectedStyleIdx;
    }
    
    public int getSelectedSizeIdx() {
        return selectedSizeIdx;
    }

    public void setSelectedSizeIdx(int selectedSizeIdx) {
        this.selectedSizeIdx = selectedSizeIdx;
    }

    public int getSelectedColorIdx() {
        return selectedColorIdx;
    }

    public ItemStyleIfc[] getStyleList()
    {
        return styleList;
    }

    public void setStyleList(ItemStyleIfc[] styleList)
    {
        this.styleList = styleList;
    }

    public int getSelectedItemTypeIdx()
    {
        return selectedTypeIdx;
    }

    public void setSelectedItemTypeIdx(int selectedItemTypeIdx)
    {
        this.selectedTypeIdx = selectedItemTypeIdx;
    }

    public void setSelectedColorIdx(int selectedColorIdx) {
        this.selectedColorIdx = selectedColorIdx;
    }

    public int getSelectedUomIdx() {
        return selectedUomIdx;
    }

    public void setSelectedUomIdx(int selectedUomIdx) {
        this.selectedUomIdx = selectedUomIdx;
    }

    public int getSelectedTaxableIdx() {
        return selectedTaxableIdx;
    }

    public void setSelectedTaxableIdx(int selectedTaxableIdx) {
        this.selectedTaxableIdx = selectedTaxableIdx;
    }

    public int getSelectedDiscountableIdx() {
        return selectedDiscountableIdx;
    }

    public void setSelectedDiscountableIdx(int selectedDiscountableIdx) {
        this.selectedDiscountableIdx = selectedDiscountableIdx;
    }

    
    /**
     * @return selectedClearanceIdx the selected index for Clearance  
     */
    public int getSelectedClearanceIdx()
    {
        return selectedClearanceIdx;
    }
    
    /**
     * Sets selectedClearanceIdx the selected Clearance index
     * @param selectedClearanceIdx
     */
    public void setSelectedClearanceIdx(int selectedClearanceIdx)
    {
        this.selectedClearanceIdx = selectedClearanceIdx;
    }

    /**
     * flag set to true when user opt for filtering searched results
     * @return
     */
    public boolean isNonFilteringItemSearch()
    {
        return nonFilteringItemSearch;
    }

    /**
     * set flag for filtering searched results
     * @param filterSearchResults
     */
    public void setNonFilteringItemSearch(boolean nonFilteringItemSearch)
    {
        this.nonFilteringItemSearch = nonFilteringItemSearch;
    }
    
    /**
     * Array containing criteria (if item is taxable)
     * for filtering already searched results
     * @return
     */
    public String[] getYesAndNoTax()
    {
        return yesAndNoTax;
    }
    
    /**
     * set item's taxability filtering criteria
     * @param yesAndNoTax
     */
    public void setYesAndNoTax(String[] yesAndNoTax)
    {
        this.yesAndNoTax = yesAndNoTax;
    }
    
    /**
     * Array containing criteria (if item is on clearance)
     * for filtering already searched results
     * @return
     */
    public String[] getYesAndNoClearance()
    {
        return yesAndNoClearance;
    }
    
    /**
     * set item's clearance filtering criteria
     * @param yesAndNoClearance
     */
    public void setYesAndNoClearance(String[] yesAndNoClearance)
    {
        this.yesAndNoClearance = yesAndNoClearance;
    }
    
    /**
     * Array containing criteria (if item is on discount)
     * for filtering already searched results
     * @return
     */
    public String[] getYesAndNoDiscount()
    {
        return yesAndNoDiscount;
    }
    
    /**
     * set item's discountability filtering criteria
     * @param yesAndNoDiscount
     */
    public void setYesAndNoDiscount(String[] yesAndNoDiscount)
    {
        this.yesAndNoDiscount = yesAndNoDiscount;
    }
}
