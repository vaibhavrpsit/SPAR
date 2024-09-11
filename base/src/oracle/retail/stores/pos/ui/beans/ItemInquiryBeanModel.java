/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ItemInquiryBeanModel.java /main/21 2012/08/16 16:47:12 hyin Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   09/22/14 - add itemSizeRequired attribute.
 *    hyin      08/15/12 - new meta tag adv search feature.
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    mchellap  01/05/09 - Department search field changes
 *    akandru   10/20/08 - EJ -- I18N
 *    akandru   10/20/08 -
 *    miparek   10/17/08 - Deptartment list changes for localized text
 *    miparek   10/16/08 - dept list changes
 *    mchellap  09/30/08 - Updated copy right header
 *
 *    $Log:
 *     4    360Commerce 1.3         12/13/2005 4:42:45 PM  Barry A. Pape
 *          Base-lining of 7.1_LA
 *     3    360Commerce 1.2         3/31/2005 4:28:31 PM   Robert Pearse
 *     2    360Commerce 1.1         3/10/2005 10:22:27 AM  Robert Pearse
 *     1    360Commerce 1.0         2/11/2005 12:11:38 PM  Robert Pearse
 *    $
 *    Revision 1.4  2004/05/03 18:30:29  lzhao
 *    @scr 4544, 4556: keep user entered info when back to the page.
 *
 *    Revision 1.3  2004/04/09 13:59:07  cdb
 *    @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.stock.ItemColorIfc;
import oracle.retail.stores.domain.stock.ItemSizeIfc;
import oracle.retail.stores.domain.stock.ItemStyleIfc;
import oracle.retail.stores.domain.stock.ItemTypeIfc;
import oracle.retail.stores.domain.stock.UnitOfMeasureIfc;
import oracle.retail.stores.domain.store.DepartmentIfc;

//-------------------------------------------------------------------------
/**
 This bean model is used by the ItemInquiryBean. <P>
 @see oracle.retail.stores.pos.ui.beans.ItemInquiryBean
 @version $KW=@(#); $Ver=pos_4.5.0:49; $EKW;
**/
//-------------------------------------------------------------------------
public class ItemInquiryBeanModel extends POSBaseBeanModel
{
   /**
        revision number
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:49; $EKW;";

    /**
     * XC adv search string
     */
    protected String metaTagSearchStr = new String();
    
    /**
       the item number
    **/
    protected String itemNumber = new String();
    
    /**
       the item description
    **/
    protected String itemDescription = new String();
    
    /**
       the list of department names
    **/
    protected DepartmentIfc[] deptList = null;
    /**
       the selected department
    **/
    protected String selectedDept = null;

    /**
     * parameter value of isSizeRequired.
     */
    protected boolean itemSizeRequired = false;
    
    /**
     * This flag indicates whether the item can be searched by department.
     */
    protected boolean searchItemByDepartment = false;
    /**
       a flag to check if an item has been selected
    **/
    protected boolean selectFlag = true;

    /**
       the item list index
    **/
    protected int     index = 0;

    /**
   	  the manufacturer
    **/
    protected String manufacturer = new String();

    /**
     * This flag indicates whether the item can be searched by Manufacturer.
     */
    protected boolean searchItemByManufacturer = false;

    /**
     * the selected type
     */
    protected String selectedType =null;

    /**
     * This flag indicates whether the item can be searched by item type.
     */
    protected boolean searchItemByType = false;

    /**
     * the selected UOM
     */
    protected String selectedUOM = null;

    /**
     * This flag indicates whether the item can be searched by UOM.
     */
    protected boolean searchItemByUOM = false;

    /**
     * the selected style
     */
    protected String selectedStyle = null;

    /**
     * This flag indicates whether the item can be searched by style.
     */
    protected boolean searchItemByStyle = false;

    /**
     * the selected color
     */
    protected String selectedColor = null;

    /**
     * This flag indicates whether the item can be searched by item color.
     */
    protected boolean searchItemByColor = false;

    /**
     * the selected size
     */
    protected String selectedSize = null;

    /**
     * This flag indicates whether the item can be searched by item type.
     */
    protected boolean searchItemBySize = false;

    /**
     * This flag indicates whether this is meta tag type of search.
     */
    protected boolean metaTagAdvSearch = false;
    
    /**
     * the list of item type names
     */
    protected ItemTypeIfc[] typeList = null;

    /**
     * the list of UOMs
     */
    protected UnitOfMeasureIfc[] uomList = null;

    /**
     * the list of item styles
     */
    protected ItemStyleIfc[] styleList = null;

    /**
     * the list of item colors
     */
    protected ItemColorIfc[] colorList = null;

    /**
     * the list of item sizes
     */
    protected ItemSizeIfc[] sizeList = null;

    /**
    the item type index
    **/
    protected int selectedTypeIndex = 0;

    /**
    the UOM index
    **/
    protected int selectedUOMIndex = 0;

    /**
    the item style index
    **/
    protected int selectedStyleIndex = 0;

    /**
    the item color index
    **/
    protected int selectedColorIndex = 0;

    /**
    the item size index
    **/
    protected int selectedSizeIndex = 0;
    
    //--------------------------------------------------------------------------
    /**
       Returns isSearchItemByColor flag.
       @return boolean searchItemByColor.
    **/
    //--------------------------------------------------------------------------
    public boolean isSearchItemByColor()
    {
        return searchItemByColor;
    }

    //--------------------------------------------------------------------------
    /**
       Sets searchItemByColor flag.
       @param boolean The searchItemByColor flag.
    **/
    //--------------------------------------------------------------------------
    public void setSearchItemByColor(boolean searchItemByColor)
    {
        this.searchItemByColor = searchItemByColor;
    }

    //--------------------------------------------------------------------------
    /**
       Returns isSearchItemByDepartment flag.
       @return boolean searchItemByDepartment.
    **/
    //--------------------------------------------------------------------------
    public boolean isSearchItemByDepartment()
    {
        return searchItemByDepartment;
    }

    //--------------------------------------------------------------------------
    /**
       Sets searchItemByDepartment flag.
       @param boolean The searchItemByDepartment flag.
    **/
    //--------------------------------------------------------------------------
    public void setSearchItemByDepartment(boolean searchItemByDepartment)
    {
        this.searchItemByDepartment = searchItemByDepartment;
    }

    //--------------------------------------------------------------------------
    /**
       Returns searchItemBySize flag.
       @return boolean searchItemBySize.
    **/
    //--------------------------------------------------------------------------
    public boolean isSearchItemBySize()
    {
        return searchItemBySize;
    }

    //--------------------------------------------------------------------------
    /**
       Sets searchItemBySize flag.
       @param boolean The searchItemBySize flag.
    **/
    //--------------------------------------------------------------------------
    public void setSearchItemBySize(boolean searchItemBySize)
    {
        this.searchItemBySize = searchItemBySize;
    }

    //--------------------------------------------------------------------------
    /**
       Returns searchItemByStyle flag.
       @return boolean searchItemByStyle.
    **/
    //--------------------------------------------------------------------------
    public boolean isSearchItemByStyle()
    {
        return searchItemByStyle;
    }

    //--------------------------------------------------------------------------
    /**
       Sets searchItemByStyle flag.
       @param boolean The searchItemByStyle flag.
    **/
    //--------------------------------------------------------------------------
    public void setSearchItemByStyle(boolean searchItemByStyle)
    {
        this.searchItemByStyle = searchItemByStyle;
    }

    //--------------------------------------------------------------------------
    /**
       Returns searchItemByType flag.
       @return boolean searchItemByType.
    **/
    //--------------------------------------------------------------------------
    public boolean isSearchItemByType()
    {
        return searchItemByType;
    }

    //--------------------------------------------------------------------------
    /**
       Sets searchItemByType flag.
       @param boolean The searchItemByType flag.
    **/
    //--------------------------------------------------------------------------
    public void setSearchItemByType(boolean searchItemByType)
    {
        this.searchItemByType = searchItemByType;
    }

    //--------------------------------------------------------------------------
    /**
       Returns searchItemByUOM flag.
       @return boolean searchItemByUOM.
    **/
    //--------------------------------------------------------------------------
    public boolean isSearchItemByUOM()
    {
        return searchItemByUOM;
    }

    //--------------------------------------------------------------------------
    /**
       Sets searchItemByUOM flag.
       @param boolean The searchItemByUOM flag.
    **/
    //--------------------------------------------------------------------------
    public void setSearchItemByUOM(boolean searchItemByUOM)
    {
        this.searchItemByUOM = searchItemByUOM;
    }

    //--------------------------------------------------------------------------
    /**
        Gets the selected item color
        @return selectedColor.
    **/
    //--------------------------------------------------------------------------
    public String getSelectedColor()
    {
        return selectedColor;
    }

    //--------------------------------------------------------------------------
    /**
       Sets the selected color.
       @param String The slected color.
    **/
    //--------------------------------------------------------------------------
    public void setSelectedColor(String selectedColor)
    {
        this.selectedColor = selectedColor;
    }

    //--------------------------------------------------------------------------
    /**
        Gets the selected item size
        @return selectedSize.
    **/
    //--------------------------------------------------------------------------
    public String getSelectedSize()
    {
        return selectedSize;
    }

    //--------------------------------------------------------------------------
    /**
       Sets the selected color.
       @param String The slected color.
    **/
    //--------------------------------------------------------------------------
    public void setSelectedSize(String selectedSize)
    {
        this.selectedSize = selectedSize;
    }

    //--------------------------------------------------------------------------
    /**
        Gets the selected item style
        @return selectedSize.
    **/
    //--------------------------------------------------------------------------
    public String getSelectedStyle()
    {
        return selectedStyle;
    }

    //--------------------------------------------------------------------------
    /**
       Sets the selected color.
       @param String The slected color.
    **/
    //--------------------------------------------------------------------------
    public void setSelectedStyle(String selectedStyle)
    {
        this.selectedStyle = selectedStyle;
    }

    //--------------------------------------------------------------------------
    /**
        Gets the selected item type
        @return selectedSize.
    **/
    //--------------------------------------------------------------------------
    public String getSelectedType()
    {
        return selectedType;
    }

    //--------------------------------------------------------------------------
    /**
       Sets the selected color.
       @param String The slected color.
    **/
    //--------------------------------------------------------------------------
    public void setSelectedType(String selectedType)
    {
        this.selectedType = selectedType;
    }

    //--------------------------------------------------------------------------
    /**
        Gets the selected UOM
        @return selectedSize.
    **/
    //--------------------------------------------------------------------------
    public String getSelectedUOM()
    {
        return selectedUOM;
    }

    //--------------------------------------------------------------------------
    /**
       Sets the selected color.
       @param String The slected color.
    **/
    //--------------------------------------------------------------------------
    public void setSelectedUOM(String selectedUOM)
    {
        this.selectedUOM = selectedUOM;
    }

    //--------------------------------------------------------------------------
    /**
        Gets the list of colors
        @return ItemColorIfc[] The list of color objects .
    **/
    //--------------------------------------------------------------------------
    public ItemColorIfc[] getColorList()
    {
        return colorList;
    }

    //--------------------------------------------------------------------------
    /**
       Sets the selected color.
       @param String The slected color.
    **/
    //--------------------------------------------------------------------------
    public void setColorList(ItemColorIfc[] colorList)
    {
        this.colorList = colorList;
    }

    //--------------------------------------------------------------------------
    /**
        Gets the list of sizes
        @return ItemSizeIfc[] The size list.
    **/
    //--------------------------------------------------------------------------
    public ItemSizeIfc[] getSizeList()
    {
        return sizeList;
    }

    //--------------------------------------------------------------------------
    /**
       Sets style list.
       @param ItemStyleIfc The style list.
    **/
    //--------------------------------------------------------------------------
    public void setSizeList(ItemSizeIfc[] sizeList)
    {
        this.sizeList = sizeList;
    }

    //--------------------------------------------------------------------------
    /**
        Gets the list of item styles
        @return ItemStyleIfc[] The style list.
    **/
    //--------------------------------------------------------------------------
    public ItemStyleIfc[]  getStyleList()
    {
        return styleList;
    }

    //--------------------------------------------------------------------------
    /**
       Sets the style list.
       @param ItemStyleIfc[] The type list.
    **/
    //--------------------------------------------------------------------------
    public void setStyleList(ItemStyleIfc[] styleList)
    {
        this.styleList = styleList;
    }

    //--------------------------------------------------------------------------
    /**
        Gets the list of item types
        @return ItemTypeIfc[] The type list.
    **/
    //--------------------------------------------------------------------------
    public ItemTypeIfc[] getTypeList()
    {
        return typeList;
    }

    //--------------------------------------------------------------------------
    /**
       Sets the type list.
       @param ItemTypeIfc[] The type list.
    **/
    //--------------------------------------------------------------------------
    public void setTypeList(ItemTypeIfc[] typeList)
    {
        this.typeList = typeList;
    }
    //--------------------------------------------------------------------------
    /**
        Gets the list of UOMs.
        @return UnitOfMeasureIfc[] The UOM list.
    **/
    //--------------------------------------------------------------------------
    public UnitOfMeasureIfc[] getUomList()
    {
        return uomList;
    }

    //--------------------------------------------------------------------------
    /**
       Sets the UOL list.
       @param UnitOfMeasureIfc[] The UOM list.
    **/
    //--------------------------------------------------------------------------
    public void setUomList(UnitOfMeasureIfc[] uomList)
    {
        this.uomList = uomList;
    }

    //--------------------------------------------------------------------------
    /**
        Sets the selected item type index
        @parm value.
    **/
    //--------------------------------------------------------------------------
    public void setSelectedTypeIndex(int value)
    {
        selectedTypeIndex = value;
        setSelectedType(typeList[value].getItemTypeID());
    }

    //--------------------------------------------------------------------------
    /**
        Gets the selected type index
        @return itemTypeIndex.
    **/
    //--------------------------------------------------------------------------
    public int getSelectedTypeIndex()
    {
        return selectedTypeIndex;
    }

    //--------------------------------------------------------------------------
    /**
        Gets the selected color index
        @return itemTypeIndex.
    **/
    //--------------------------------------------------------------------------
    public int getSelectedColorIndex()
    {
        return selectedColorIndex;
    }

    //--------------------------------------------------------------------------
    /**
        Sets the selected color type index
        @parm value.
    **/
    //--------------------------------------------------------------------------
    public void setSelectedColorIndex(int selectedColorIndex)
    {
        this.selectedColorIndex = selectedColorIndex;
        setSelectedColor(colorList[selectedColorIndex].getIdentifier());
    }

    //--------------------------------------------------------------------------
    /**
        Gets the selected size index
        @return itemTypeIndex.
    **/
    //--------------------------------------------------------------------------
    public int getSelectedSizeIndex()
    {
        return selectedSizeIndex;
    }

    //--------------------------------------------------------------------------
    /**
        Sets the selected item size index
        @parm value.
    **/
    //--------------------------------------------------------------------------
    public void setSelectedSizeIndex(int selectedSizeIndex)
    {
        this.selectedSizeIndex = selectedSizeIndex;
        setSelectedSize(sizeList[selectedSizeIndex].getSizeCode());
    }

    //--------------------------------------------------------------------------
    /**
        Gets the selected style index
        @return itemTypeIndex.
    **/
    //--------------------------------------------------------------------------
    public int getSelectedStyleIndex()
    {
        return selectedStyleIndex;
    }

    //--------------------------------------------------------------------------
    /**
        Sets the selected item style index
        @parm value.
    **/
    //--------------------------------------------------------------------------
    public void setSelectedStyleIndex(int selectedStyleIndex)
    {
        this.selectedStyleIndex = selectedStyleIndex;
        setSelectedStyle(styleList[selectedStyleIndex].getIdentifier());
    }

    //--------------------------------------------------------------------------
    /**
        Gets the selected UOM index
        @return itemTypeIndex.
    **/
    //--------------------------------------------------------------------------
    public int getSelectedUOMIndex()
    {
        return selectedUOMIndex;
    }

    //--------------------------------------------------------------------------
    /**
        Sets the selected UOM index
        @parm value.
    **/
    //--------------------------------------------------------------------------
    public void setSelectedUOMIndex(int selectedUOMIndex)
    {
        this.selectedUOMIndex = selectedUOMIndex;
        setSelectedUOM(uomList[selectedUOMIndex].getUnitID());
    }

    //--------------------------------------------------------------------------
    /**
         Sets the selected item type id
         @param value the item type id.
     **/
    //--------------------------------------------------------------------------
    public void setSelectedItemTypeByID(String value)
    {
       for ( int i=0; i < typeList.length; i++)
       {
            if ( typeList[i].getItemTypeID() != null )
            {
                if ( typeList[i].getItemTypeID().equals(value) )
                {
                    setSelectedTypeIndex(i);
                    break;
                }
            }
       }
    }

    //--------------------------------------------------------------------------
    /**
         Sets the selected item type id
         @param value the item type id.
     **/
    //--------------------------------------------------------------------------
    public void setSelectedItemUOMByID(String value)
    {
       for ( int i=0; i < uomList.length; i++)
       {
            if ( uomList[i].getUnitID() != null )
            {
                if ( uomList[i].getUnitID().equals(value) )
                {
                    setSelectedUOMIndex(i);
                    break;
                }
            }
       }
    }

    //--------------------------------------------------------------------------
    /**
         Sets the selected item style id
         @param value the item style id.
     **/
    //--------------------------------------------------------------------------
    public void setSelectedItemStyleByID(String value)
    {
       for ( int i=0; i < styleList.length; i++)
       {
            if ( styleList[i].getIdentifier() != null )
            {
                if ( styleList[i].getIdentifier().equals(value) )
                {
                    setSelectedStyleIndex(i);
                    break;
                }
            }
       }
    }

    //--------------------------------------------------------------------------
    /**
         Sets the selected item color id
         @param value the item color id.
     **/
    //--------------------------------------------------------------------------
    public void setSelectedItemColorByID(String value)
    {
       for ( int i=0; i < colorList.length; i++)
       {
            if ( colorList[i].getIdentifier() != null )
            {
                if ( colorList[i].getIdentifier().equals(value) )
                {
                    setSelectedColorIndex(i);
                    break;
                }
            }
       }
    }

    //--------------------------------------------------------------------------
    /**
         Sets the selected item size id
         @param value the item size id.
     **/
    //--------------------------------------------------------------------------
    public void setSelectedItemSizeByID(String value)
    {
       for ( int i=0; i < sizeList.length; i++)
       {
            if ( sizeList[i].getSizeCode() != null )
            {
                if ( sizeList[i].getSizeCode().equals(value) )
                {
                    setSelectedSizeIndex(i);
                    break;
                }
            }
       }
    }

    //--------------------------------------------------------------------------
    /**
       Returns the items description.
       @return String itemDescription.
    **/
    //--------------------------------------------------------------------------
    public String getItemDesc() {
        return itemDescription;
    }

   //--------------------------------------------------------------------------
   /**
       Returns the items number.
       @return String itemNumber.
   **/
   //--------------------------------------------------------------------------
   public String getItemNumber() {
       return itemNumber;
   }

   //--------------------------------------------------------------------------
   /**
       Returns the list of departments.
       @return DepartmentIfc[] deptList.
   **/
   //--------------------------------------------------------------------------
   public DepartmentIfc[] getDeptList() {
       return deptList;
   }

   //--------------------------------------------------------------------------
   /**
       Sets the item desc value.
       @param value The new value for the property.
   **/
   //--------------------------------------------------------------------------
   public void setItemDesc(String value) {
       itemDescription = value;
   }

   //--------------------------------------------------------------------------
   /**
       Sets the item number value.
       @param value The new value for the property.
    **/
   //--------------------------------------------------------------------------
   public void setItemNumber(String value) {
      itemNumber = value;
   }
   //--------------------------------------------------------------------------
   /**
       Sets the department array value.
       @param value The new value for the property.
    **/
   //--------------------------------------------------------------------------
   public void setDeptList(DepartmentIfc[] value) {
      deptList = value;
   }

   //--------------------------------------------------------------------------
   /**
       Returns the value of the selectedFlag.
       @return boolean selectedFlag the boolean.
   **/
   //--------------------------------------------------------------------------
   public boolean isSelected()
   {
       return selectFlag;
   }

   //--------------------------------------------------------------------------
   /**
       Returns the index
       @return int the index.
   **/
   //--------------------------------------------------------------------------
   public int getSelectedIndex()
   {
      return index;
   }
   //--------------------------------------------------------------------------
   /**
        Sets the selected department id
        @param value the department id.
    **/
   //--------------------------------------------------------------------------
   public void setSelectedDept(String value)
   {
       selectedDept = value;
   }
   //--------------------------------------------------------------------------
   /**
        Returns the selected department id
        @return selectedDept.
    **/
   //--------------------------------------------------------------------------
   public String getSelectedDept()
   {
       return(selectedDept);
   }

   //--------------------------------------------------------------------------
   /**
       Sets the selected index
       @parm value.
   **/
   //--------------------------------------------------------------------------
   public void setSelectedIndex(int value)
   {
       index = value;
       setSelectedDept(deptList[index].getDepartmentID());
   }

   //--------------------------------------------------------------------------
   /**
      Sets the selected flag
      @parm value boolean.
    **/
   //--------------------------------------------------------------------------
   public void setSelected(boolean value)
   {
       selectFlag = value;
   }

   //--------------------------------------------------------------------------
   /**
        Sets the selected department description
        @param value the department description.
    **/
   //--------------------------------------------------------------------------
   public void setSelectedDeptByDescription(String value)
   {
      for ( int i=0; i < deptList.length; i++)
      {
           if ( deptList[i].getDescription(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)) != null )
           {
               if ( deptList[i].getDescription(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)).equals(value) )
               {
                   setSelectedIndex(i);
                   break;
               }
           }
      }
   }

   // --------------------------------------------------------------------------
   /**
      Returns the manufacturer.
      @return String manufacturer.
   **/
   //--------------------------------------------------------------------------
   public String getManufacturer()
   {
	   return manufacturer;
   }

   // --------------------------------------------------------------------------
   /**
      Set the new value for manufacturer.
      @param value manufacturer.
   **/
   //--------------------------------------------------------------------------
   public void setManufacturer(String manufacturer)
   {
	   this.manufacturer = manufacturer;
   }

   // ----------------------------------------------------------------------
   /**
       To confirm if the item can be searched using the Manufacturer search
       criteria.
       <P>
       @return boolean value of the parameter " SearchItemByManufacturer"
   **/
   //----------------------------------------------------------------------
	public boolean isSearchItemByManufacturer()
	{
		return searchItemByManufacturer;
	}

	//	----------------------------------------------------------------------
   /**
       To set the boolean value confirming if the item can be searched
       using the Manufacturer
       criteria.
       <P>
       @param boolean value of the parameter " SearchItemByManufacturer"
   **/
   //----------------------------------------------------------------------
	public void setSearchItemByManufacturer(boolean searchItemByManufacturer)
	{
		this.searchItemByManufacturer = searchItemByManufacturer;
	}

	/**
	 * returns meta tag adv search flag
	 * @return boolean xcAdvSearch
	 */
    public boolean isMetaTagAdvSearch() {
        return metaTagAdvSearch;
    }

    /**
     * set meta tag adv search flag
     * @param xcAdvSearch
     */
    public void setMetaTagAdvSearch(boolean xcAdvSearch) {
        this.metaTagAdvSearch = xcAdvSearch;
    }

    /**
     * returns search string
     * @return String xcSearchStr
     */
    public String getMetaTagSearchStr() {
        return metaTagSearchStr;
    }

    /**
     * set search string
     * @param xcSearchStr
     */
    public void setMetaTagSearchStr(String xcSearchStr) {
        this.metaTagSearchStr = xcSearchStr;
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

}
