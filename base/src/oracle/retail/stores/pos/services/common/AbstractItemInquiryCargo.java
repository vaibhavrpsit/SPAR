/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/AbstractItemInquiryCargo.java /main/23 2012/06/14 09:02:13 rsnayak Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   09/22/14 - Fixed NullPointerException
 *    rsnayak   06/13/12 - Array out of bounds fix for style, uom,
 *                         color,itemtype
 *    mjwallac  05/02/12 - Fortify: fix redundant null checks, part 4
 *    mjwallac  04/24/12 - Fixes for Fortify redundant null check
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   05/05/09 - prevent returning null in getLocalizedDeptName
 *    mchellap  01/05/09 - Department search field changes
 *    akandru   10/30/08 - EJ changes
 *    ranojha   10/27/08 - Removed I18N functionality for Order and EMessages
 *    ranojha   10/23/08 - Fixed the localized text for ItemSize and Buttons
 *    ranojha   10/23/08 - Fixed UnitOfMeasure I18N changes
 *    ranojha   10/21/08 - Changes for POS for UnitOfMeasure I18N
 *    akandru   10/20/08 - EJ -- I18N
 *    akandru   10/20/08 -
 *    ranojha   10/17/08 - Changes for code review
 *    ranojha   10/17/08 - Changes for UnitOfMeasure and Item Size/Color and
 *                         Style
 *    miparek   10/17/08 - dept list changes for locale requestor
 *    miparek   10/17/08 - dept list changes with locale requestor
 *    miparek   10/17/08 - dept list changes with respect to localized text
 *    ranojha   10/16/08 - Implementation for UnitOfMeasure I18N Changes
 *    miparek   10/16/08 - deptartment list changes for localized dept
 *                         description
 *    miparek   10/16/08 - department description changes for localized text
 *    miparek   10/16/08 - dept list changes
 *    mchellap  10/16/08 - Advance Item Inquiry
 *    mchellap  10/16/08 -
 *    ranojha   10/14/08 - Enhanced Sites, Beans for I18N work for
 *                         UnitOfMeasure, Style and Color
 *    ranojha   10/13/08 - Working on Item Style, Size and Color changes
 *    ddbaker   10/09/08 - Refactor of reference implementation of POS I18N
 *                         Persistence
 *    ddbaker   10/06/08 - Preliminary I18N Persistence Updates for Size, Style
 *                         and Color.
 *    mchellap  09/30/08 - QuickWin Item Inquiry
 *    mchellap  09/30/08 - Abstract Item Inquiry Cargo
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import java.util.Hashtable;
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.stock.ItemColorIfc;
import oracle.retail.stores.domain.stock.ItemSizeIfc;
import oracle.retail.stores.domain.stock.ItemStyleIfc;
import oracle.retail.stores.domain.stock.ItemTypeIfc;
import oracle.retail.stores.domain.stock.UnitOfMeasureIfc;
import oracle.retail.stores.domain.store.DepartmentIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;

public class AbstractItemInquiryCargo extends AbstractFinancialCargo
{
    private static final long serialVersionUID = -6503044217939927789L;

    /**
     * The department list hash by id.
     */
    protected Hashtable<String,LocalizedTextIfc> deptNames = new Hashtable<String,LocalizedTextIfc>();

    /**
     * The selected department.
     */
    protected DepartmentIfc dept = null;

    protected DepartmentIfc[] deptList = null;

    /**
     * The selected item type.
     */
    protected ItemTypeIfc itemType = null;

    /**
     * List of Item types.
     */
    protected ItemTypeIfc[] typeList = null;

    /**
     * The item type list hash by id.
     */
    protected Hashtable<String,String> typeNames = new Hashtable<String,String>();

    /**
     * The selected UOM.
     */
    protected UnitOfMeasureIfc uom = null;

    /**
     * List of UOMs.
     */
    protected UnitOfMeasureIfc[] uomList = null;

    /**
     * The UOM list hash by id.
     */
    protected Hashtable<String,LocalizedTextIfc> uomNames = new Hashtable<String,LocalizedTextIfc>();

    /**
     * The selected item style.
     */
    protected ItemStyleIfc style = null;

    /**
     * List of Item styles.
     */
    protected ItemStyleIfc[] styleList = null;

    /**
     * The style list hash by id.
     */
    protected Hashtable<String,LocalizedTextIfc> styleNames = new Hashtable<String,LocalizedTextIfc>();

    /**
     * The selected item color .
     */
    protected ItemColorIfc color = null;

    /**
     * List of Item colors.
     */
    protected ItemColorIfc[] colorList = null;

    /**
     * The color list hash by id.
     */
    protected Hashtable<String,LocalizedTextIfc> colorNames = new Hashtable<String,LocalizedTextIfc>();

    /**
     * The selected item size.
     */
    protected ItemSizeIfc size = null;

    /**
     * List of Item sizes.
     */
    protected ItemSizeIfc[] sizeList = null;

    /**
     * The size list hash by id.
     */
    protected Hashtable<String,LocalizedTextIfc> sizeNames = new Hashtable<String,LocalizedTextIfc>();

    /**
     * This flag indicates whether the item can be searched by department
     */
    protected boolean searchItemByDepartment = false;

    /**
     * This flag indicates whether the item can be searched by Item type
     */
    protected boolean searchItemByType = false;

    /**
     * This flag indicates whether the item can be searched by UOM.
     */
    protected boolean searchItemByUOM = false;

    /**
     * This flag indicates whether the item can be searched by Style.
     */
    protected boolean searchItemByStyle = false;

    /**
     * This flag indicates whether the item can be searched by Color.
     */
    protected boolean searchItemByColor = false;

    /**
     * This flag indicates whether the item can be searched by Size.
     */
    protected boolean searchItemBySize = false;

    /**
     * Returns the department name.
     * 
     * @return String The department name.
     * @deprecated as of 13.1 use {@link #getLocalizedDeptName(String)} instead
     */
    public String getDeptName()
    {
        return (getDeptName(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE)));
    }

    /**
     * Returns the department name.
     * 
     * @param locale
     * @return String The department name.
     */
    public String getDeptName(Locale lcl)
    {
        String departmentName = "";
        if ( getDept()!=null )
        {
            departmentName = getDept().getDescription(lcl);
        }

        return departmentName;
    }

    /**
     * Returns the Size name given the color id.
     * 
     * @param key the size id.
     * @return String size name.
     */
    public LocalizedTextIfc getLocalizedDeptName(String key)
    {
        LocalizedTextIfc name = DomainGateway.getFactory().getLocalizedText();
        if (deptNames.get(key) != null)
        {
            name = deptNames.get(key);
        }
        return name;
    }

    /**
     * Sets the Department info given the name.
     * 
     * @param name the department name.
     */
    public void setDept(String deptId)
    {

        if (dept == null)
        {
            dept = DomainGateway.getFactory().getDepartmentInstance();
        }
        dept.setDepartmentID(deptId);
        dept.setLocalizedDescriptions(getLocalizedDeptName(deptId));
    }

    /**
     * Gets the Department info given the name.
     * 
     * @param name the department name.
     */
    public DepartmentIfc getDept()
    {
        return (dept);
    }

    /**
     * Returns the Department list info.
     * 
     * @return DepartmentIfc[] The Department list info.
     */
    public DepartmentIfc[] getDeptList()
    {
        return (deptList);
    }

    /**
     * Returns the Department list hashtable
     * 
     * @return Hashtable The Department list hash table
     */
    public Hashtable<String,LocalizedTextIfc> getDeptListHash()
    {
        return (deptNames);
    }

    /**
     * Sets the Department list.
     * 
     * @param list The new department list.
     */
    public void setDeptList(DepartmentIfc[] list)
    {
        deptList = new DepartmentIfc[list.length+1];

        UtilityManagerIfc utility = (UtilityManagerIfc)  Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        // add default to department list hash tables
        deptList[0] = DomainGateway.getFactory().getDepartmentInstance();
        deptList[0].setDepartmentID("-1");
        deptList[0].setDescription(uiLocale,utility.retrieveText("Common", BundleConstantsIfc.INQUIRY_OPTIONS_BUNDLE_NAME, "AllLabel", "<All>", uiLocale));
        deptNames.put(deptList[0].getDepartmentID(),deptList[0].getLocalizedDescriptions());
        for ( int i= 0; i< list.length; i++)
        {
           deptList[i+1] = DomainGateway.getFactory().getDepartmentInstance();
           deptList[i+1].setDepartmentID(list[i].getDepartmentID());
           deptList[i+1].setLocalizedDescriptions(list[i].getLocalizedDescriptions());
           deptNames.put(list[i].getDepartmentID(),list[i].getLocalizedDescriptions());
        }
    }

    /**
     * Gets the searchItemByColor flag.
     * 
     * @return boolean The searchItemByColor flag.
     */
    public boolean isSearchItemByColor()
    {
        return searchItemByColor;
    }

    /**
     * Sets the searchItemByColor flag.
     * 
     * @param boolean searchItemByColor.
     */
    public void setSearchItemByColor(boolean searchItemByColor)
    {
        this.searchItemByColor = searchItemByColor;
    }

    /**
     * Returns isSearchItemByDepartment flag.
     * 
     * @return boolean searchItemByDepartment.
     */
    public boolean isSearchItemByDepartment()
    {
        return searchItemByDepartment;
    }

    /**
     * Sets searchItemByDepartment flag.
     * 
     * @param boolean The searchItemByDepartment flag.
     */
    public void setSearchItemByDepartment(boolean searchItemByDepartment)
    {
        this.searchItemByDepartment = searchItemByDepartment;
    }

    /**
     * Gets the searchItemBySize flag.
     * 
     * @return boolean The searchItemBySize flag.
     */
    public boolean isSearchItemBySize()
    {
        return searchItemBySize;
    }

    /**
     * Sets the searchItemBySize flag.
     * 
     * @param boolean searchItemBySize.
     */
    public void setSearchItemBySize(boolean searchItemBySize)
    {
        this.searchItemBySize = searchItemBySize;
    }

    /**
     * Gets the searchItemByStyle flag.
     * 
     * @return boolean The searchItemByStyle flag.
     */
    public boolean isSearchItemByStyle()
    {
        return searchItemByStyle;
    }

    /**
     * Sets the searchItemByStyle flag.
     * 
     * @param boolean searchItemByStyle.
     */
    public void setSearchItemByStyle(boolean searchItemByStyle)
    {
        this.searchItemByStyle = searchItemByStyle;
    }

    /**
     * Gets the searchItemByType flag.
     * 
     * @return boolean The searchItemByType flag.
     */
    public boolean isSearchItemByType()
    {
        return searchItemByType;
    }

    /**
     * Sets the searchItemByType flag.
     * 
     * @param boolean searchItemByType.
     */
    public void setSearchItemByType(boolean searchItemByType)
    {
        this.searchItemByType = searchItemByType;
    }

    /**
     * Gets the searchItemByUOM flag.
     * 
     * @return boolean The searchItemByUOM flag.
     */
    public boolean isSearchItemByUOM()
    {
        return searchItemByUOM;
    }

    /**
     * Sets the searchItemByUOM flag.
     * 
     * @param boolean searchItemByUOM.
     */
    public void setSearchItemByUOM(boolean searchItemByUOM)
    {
        this.searchItemByUOM = searchItemByUOM;
    }

    /**
     * Sets the item type list.
     * 
     * @param list The new item type list.
     */
    public void setTypeList(ItemTypeIfc [] list)
    {
        if(list == null)
        {
            typeList = new ItemTypeIfc[1];
        }
        else
        {
            typeList = new ItemTypeIfc[list.length+1];
        }

        UtilityManagerIfc utility = (UtilityManagerIfc)  Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        // add default to department list hash tables
        typeList[0] = DomainGateway.getFactory().getItemTypeInstance();
        typeList[0].setItemTypeID("-1");
        typeList[0].setItemTypeName(utility.retrieveText("Common", BundleConstantsIfc.INQUIRY_OPTIONS_BUNDLE_NAME, "AllLabel", "<All>", uiLocale));
        int j = 0 ;
        for ( int i= 0; list != null && i< typeList.length; i++)
        {  
          if( j < list.length)
          {
            typeList[i+1] = DomainGateway.getFactory().getItemTypeInstance();
            typeList[i+1].setItemTypeID(list[i].getItemTypeID());
            typeList[i+1].setItemTypeName(list[i].getItemTypeName());
            typeNames.put(list[i].getItemTypeID(),new String(list[i].getItemTypeName()));
            j++;
          }
        }
    }

    /**
     * Sets the item color list.
     * 
     * @param list The new item color list.
     */
    public void setColorList(ItemColorIfc[] list)
    {
        if(list == null)
        {
            colorList = new ItemColorIfc[1];
        }
        else
        {
            colorList = new ItemColorIfc[list.length+1];
        }

        UtilityManagerIfc utility = (UtilityManagerIfc)  Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        // add default to color list hash tables
        colorList[0] = DomainGateway.getFactory().getItemColorInstance();
        colorList[0].setIdentifier("-1");
        colorList[0].setName(uiLocale, utility.retrieveText("Common", BundleConstantsIfc.INQUIRY_OPTIONS_BUNDLE_NAME, "AllLabel", "<All>", uiLocale));
        int j = 0 ;
        for ( int i= 0; list != null && i< colorList.length; i++)
        {
          if( j < list.length)
          {
            colorList[i+1] = DomainGateway.getFactory().getItemColorInstance();
            colorList[i+1].setIdentifier(list[i].getIdentifier());
            colorList[i+1].setLocalizedNames(list[i].getLocalizedNames());
            colorNames.put(list[i].getIdentifier(), list[i].getLocalizedNames());
            j++;
          }
        }
    }

    /**
     * Sets the UOM list.
     * 
     * @param list The new UOM list.
     */
    public void setUomList(UnitOfMeasureIfc[] list)
    {
        if(list == null)
        {
            uomList = new UnitOfMeasureIfc[1];
        }
        else
        {
            uomList = new UnitOfMeasureIfc[list.length+1];
        }

        UtilityManagerIfc utility = (UtilityManagerIfc)  Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        // add default to UOM list hash tables
        uomList[0] = DomainGateway.getFactory().getUnitOfMeasureInstance();
        uomList[0].setUnitID("-1");
        uomList[0].setName(uiLocale, utility.retrieveText("Common", BundleConstantsIfc.INQUIRY_OPTIONS_BUNDLE_NAME, "AllLabel", "<All>", uiLocale));
        int j = 0 ;
        for ( int i= 0; list != null && i< uomList.length; i++)
        {
          if( j < list.length)
          {
            uomList[i+1] = DomainGateway.getFactory().getUnitOfMeasureInstance();
            uomList[i+1].setUnitID(list[i].getUnitID());
            uomList[i+1].setLocalizedNames(list[i].getLocalizedNames());
            uomNames.put(list[i].getUnitID(), list[i].getLocalizedNames());                
            j++;
          }
        }
    }

    /**
     * Sets the item style list.
     * 
     * @param list The new item style list.
     */
    public  void setStyleList(ItemStyleIfc[] list)
    {
        if(list == null)
        {
            styleList = new ItemStyleIfc[1];
        }
        else
        {
            styleList = new ItemStyleIfc[list.length+1];
        }

        UtilityManagerIfc utility = (UtilityManagerIfc)  Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        // add default to style list hash tables
        styleList[0] = DomainGateway.getFactory().getItemStyleInstance();
        styleList[0].setIdentifier("-1");
        styleList[0].setName(uiLocale, utility.retrieveText("Common", BundleConstantsIfc.INQUIRY_OPTIONS_BUNDLE_NAME, "AllLabel", "<All>", uiLocale));
        int j =0;
        for ( int i= 0; list != null && i< styleList.length; i++)
        {  
          if( j < list.length)
          {
            styleList[i+1] = DomainGateway.getFactory().getItemStyleInstance();
            styleList[i+1].setIdentifier(list[i].getIdentifier());
            styleList[i+1].setLocalizedNames(list[i].getLocalizedNames());
            styleNames.put(list[i].getIdentifier(),list[i].getLocalizedNames());
            j++;
          }
        }
    }

    /**
     * Sets the item size list.
     * 
     * @param list The new item color list.
     */
    public  void setSizeList(ItemSizeIfc[] list)
    {
        if(list == null)
        {
            sizeList = new ItemSizeIfc[1];
        }
        else
        {
            sizeList = new ItemSizeIfc[list.length+1]; 
        }

        UtilityManagerIfc utility = (UtilityManagerIfc)  Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        // add default to department list hash tables
        sizeList[0] = DomainGateway.getFactory().getItemSizeInstance();
        sizeList[0].setSizeCode("-1");
        sizeList[0].setDescription(uiLocale, utility.retrieveText("Common", BundleConstantsIfc.INQUIRY_OPTIONS_BUNDLE_NAME, "AllLabel", "<All>", uiLocale));
        int j = 0;
        for ( int i= 0; list != null && i< sizeList.length; i++)
        { 
          if( j < list.length) 
           {
            sizeList[i+1] = DomainGateway.getFactory().getItemSizeInstance();
            sizeList[i+1].setSizeCode(list[i].getSizeCode());
            sizeList[i+1].setLocalizedDescriptions(list[i].getLocalizedDescriptions());
            sizeNames.put(list[i].getSizeCode(), list[i].getLocalizedDescriptions());
            j++;
           }
          }
    }

    /**
     * Returns the selected color
     * 
     * @return ItemColorIfc The selected color.
     */
    public ItemColorIfc getColor()
    {
        return color;
    }

    /**
     * Sets the selected color
     * 
     * @param colorId The item color identifier
     */
    public void setColor(String colorId)
    {
        if (color == null)
        {
            color = DomainGateway.getFactory().getItemColorInstance();
        }
        color.setIdentifier(colorId);
        color.setLocalizedNames(getLocalizedColorName(colorId));
    }

    /**
     * Returns color list
     * 
     * @return ItemColorIfc[] The color list.
     */
    public ItemColorIfc[] getColorList()
    {
        return colorList;
    }

    /**
     * Returns the selected item type
     * 
     * @return ItemTypeIfc The selected item type.
     */
    public ItemTypeIfc getItemType()
    {
        return itemType;
    }

    /**
     * Sets the selected color
     * 
     * @param itemTypeID The item type identifier
     */
    public void setItemType(String itemTypeID)
    {
        if (itemType == null)
        {
            itemType = DomainGateway.getFactory().getItemTypeInstance();
        }
        itemType.setItemTypeID(itemTypeID);
        itemType.setItemTypeName(getTypeName(itemTypeID));
    }

    /**
     * Returns the selected size
     * 
     * @return ItemSizeIfc The selected item size.
     */
    public ItemSizeIfc getSize()
    {
        return size;
    }

    /**
     * Sets the selected size
     * 
     * @param sizeCode The item size code
     */
    public void setSize(String sizeCode)
    {
        if (size == null)
        {
            size = DomainGateway.getFactory().getItemSizeInstance();
        }
        size.setSizeCode(sizeCode);
        size.setLocalizedNames(getLocalizedSizeName(sizeCode));
    }

    /**
     * Returns the item size list
     * 
     * @return ItemSizeIfc[] The item size list.
     */
    public ItemSizeIfc[] getSizeList()
    {
        return sizeList;
    }

    /**
     * Returns the selected style
     * 
     * @return ItemStyleIfc The selected item style.
     */
    public ItemStyleIfc getStyle()
    {
        return style;
    }

    /**
     * Sets the selected style
     * 
     * @param styleCode The item style code
     */
    public void setStyle(String styleCode)
    {
        if (style == null)
        {
            style = DomainGateway.getFactory().getItemStyleInstance();
        }
        style.setIdentifier(styleCode);
        style.setLocalizedNames(getLocalizedStyleName(styleCode));
    }

    /**
     * Returns the item style list
     * 
     * @return ItemStyleIfc[] The item style list.
     */
    public ItemStyleIfc[] getStyleList()
    {
        return styleList;
    }

    /**
     * Returns the item type list
     * 
     * @return ItemTypeIfc[] The item type list.
     */
    public ItemTypeIfc[] getTypeList()
    {
        return typeList;
    }

    /**
     * Returns the selected UOM
     * 
     * @return uom The slected uom
     */
    public UnitOfMeasureIfc getUom()
    {
        return uom;
    }

    /**
     * Sets the selected UOM
     * 
     * @param uomCode The UOM code
     */
    public void setUom(String uomCode)
    {
        if (uom == null)
        {
            uom = DomainGateway.getFactory().getUnitOfMeasureInstance();
        }
        uom.setUnitID(uomCode);
        uom.setLocalizedNames(getLocalizedUOMName(uomCode));
    }

    /**
     * Returns the UOM list
     * 
     * @return UnitOfMeasureIfc[] The UOM list.
     */
    public UnitOfMeasureIfc[] getUomList()
    {
        return uomList;
    }

    /**
     * Returns the color name given the color id.
     * 
     * @param key the color id.
     * @return LocalizedText color name.
     */
    public LocalizedTextIfc getLocalizedColorName(String key)
    {
        return (colorNames.get(key));
    }

    /**
     * Returns the color name given the color id.
     * 
     * @param key the color id.
     * @return String color name.
     * @deprecated as of release 13.1 use {@link #getLocalizedColorName(String)} instead
     */
    public String getColorName(String key)
    {
        return(colorNames.get(key).getText(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));
    }

    /**
     * Returns the type name given the type id.
     * 
     * @param key the type id.
     * @return String type name.
     */
    public String getTypeName(String key)
    {
        return (typeNames.get(key));
    }

    /**
     * Returns the UnitOfMeasure name given the id.
     * 
     * @param key the uom id.
     * @return LocalizedText uom name.
     */
    public LocalizedTextIfc getLocalizedUOMName(String key)
    {
        return (uomNames.get(key));
    }

    /**
     * Returns the UOM name given the color id.
     * 
     * @param key the UOM id.
     * @return String UOM name.
     * @deprecated as of release 13.1 use {@link #getLocalizedUnitName(String)} instead
     */
    public String getUOMName(String key)
    {
        return (uomNames.get(key).getText(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));
    }

    /**
     * Returns the Style name given the id.
     * 
     * @param key the style id.
     * @return String style name.
     */
    public LocalizedTextIfc getLocalizedStyleName(String key)
    {
        return (styleNames.get(key));
    }

    /**
     * Returns the Style name given the color id.
     * 
     * @param key the style id.
     * @return String style name.
     * @deprecated as of release 13.1 use {@link #getLocalizedStyleName(String)} instead
     */
    public String getStyleName(String key)
    {
        return (styleNames.get(key).getText(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));
    }

    /**
     * Returns the Size name given the color id.
     * 
     * @param key the size id.
     * @return String size name.
     */
    public LocalizedTextIfc getLocalizedSizeName(String key)
    {
        return (sizeNames.get(key));
    }

    /**
     * Returns the Size name given the color id.
     * 
     * @param key the size id.
     * @return String size name.
     * @deprecated as of release 13.1 use {@link #getLocalizedSizeNames(String)} instead
     */
    public String getSizeName(String key)
    {
        return (sizeNames.get(key).getText(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));
    }
}
