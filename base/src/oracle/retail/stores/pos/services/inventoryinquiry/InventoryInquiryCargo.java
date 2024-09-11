/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inventoryinquiry/InventoryInquiryCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:51 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mchellap  09/30/08 - Moved getters and setters to AbstractItemInquiry
 *                         cargo
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.inventoryinquiry;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Vector;

import oracle.retail.stores.pos.services.common.AbstractItemInquiryCargo;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.inventoryinquiry.InventoryResultsListItem;
import oracle.retail.stores.domain.stock.ItemInfoIfc;
import oracle.retail.stores.domain.stock.ItemInquirySearchCriteriaIfc;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.ObjectRestoreException;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamSnapshot;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;

// ------------------------------------------------------------------------------
/**
 * This class serves as the cargo for the complete Inventory Inquiry Service
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
// ------------------------------------------------------------------------------
public class InventoryInquiryCargo extends AbstractItemInquiryCargo implements ProductGroupConstantsIfc, CargoIfc,
        TourCamIfc, Serializable
{
    private static final long serialVersionUID = -6924405926281369114L;

    /**
     * The selected item.
     */
    protected ItemInfoIfc itemInfo = null;

    /**
     * The list of matching item(s).
     */
    protected ItemInfoIfc[] itemList = null;

    /**
     * The serial number of item to be added to current transaction.
     */
    protected String itemSerial = null;

    /**
     * This flag indicates whether the item that was entered was scanned or
     * typed.
     */
    protected boolean itemScanned = false;

    /**
     * The item that we are looking for
     */
    protected ItemInquirySearchCriteriaIfc inquiryItem = null;

    /**
     * The vector to keep track of the fields with invalid data
     */
    protected Vector<Integer> fields = new Vector<Integer>(2);

    /**
     * Flag to determine if a list of items is display
     */
    protected boolean showListFlag = false;

    protected boolean mainInventoryFlowFlag = true;

    /**
     * This flag indicates whether the item should be added to the transaction.
     * True, if the item should be added to the transaction. False otherwise.
     * Default is true.
     */
    protected boolean modifiedFlag = true;

    /**
     * This flag indicates whether PlanogramID should be displayed.
     */
    protected boolean usePlanogramID = false;

    /**
     * This flag indicates whether the item can be searched by Manufacturer.
     */
    protected boolean searchItemByManufacturer = false;

    /**
     * item size
     */
    protected String itemSize = null;

    /**
     **/
    protected InventoryResultsListItem[] inventoryList = null;

    /**
     * holds the letter name of the tour
     */
    private String letterName;

    /**
     * holds the specific store id
     */
    private String specificStoreId = null;

    /**
     * store select type, by specific store or multi store.
     */
    protected int storeSelectType = HOME_STORE_INQUIRY;

    /**
     * minimum quatity available for searching
     */
    protected BigDecimal minQtyAvailable = new BigDecimal(0);

    /**
     * item inquiry type, by inventory or non inventory.
     */
    protected int itemInquiryType = NON_INVENTORY_INQUIRY;

    /**
     * ParentIDs list for querying
     */
    protected ArrayList parentIDs = null;

    /**
     * Map containing all the levels of the current hierarchy ID
     */
    protected TreeMap merchHierarchyLevelsMap = null;

    /**
     * Merchandise Hierarchy drop down map
     */
    private TreeMap merchHierarchyDropDownMap = null;

    /**
     * SearchForAtRootLevel
     */
    protected boolean serachItemAtRootLevel = false;

    /**
     * value for attribute selectedLevel
     */
    protected String selectedLevel = null;

    // protected String inventoryLookupitemId = null;

    protected static final int FALSE = 0;

    protected static final int TRUE = 1;

    public static final int NON_INVENTORY_INQUIRY = 0;

    public static final int INVENTORY_INQUIRY = 1;

    /**
     * value for attribute storeSelectType
     */
    public static final int HOME_STORE_INQUIRY = 0;

    /**
     * value for attribute storeSelectType
     */
    public static final int BUDDY_STORE_INQUIRY = 1;

    /**
     * value for attribute storeSelectType
     */
    public static final int TRANSFER_ZONE_INQUIRY = 2;

    /**
     * value for attribute storeSelectType
     */
    public static final int SPECIFIC_STORE_INQUIRY = 3;

    // --------------------------------------------------------------------------
    /**
     * Create a SnapshotIfc which can subsequently be used to restore the cargo
     * to its current state.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>The cargo is able to make a snapshot.
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>A snapshot is returned which contains enough data to restore the
     * cargo to its current state.
     * </UL>
     *
     * @return an object which stores the current state of the cargo.
     * @see com.cornerstoneretail.bedrock.tour.application.tourcam.SnapshotIfc
     */
    // --------------------------------------------------------------------------
    public SnapshotIfc makeSnapshot()
    {
        return new TourCamSnapshot(this);
    }

    // --------------------------------------------------------------------------
    /**
     * Reset the cargo data using the snapshot passed in.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>The snapshot represents the state of the cargo, possibly relative to
     * the existing state of the cargo.
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>The cargo state has been restored with the contents of the snapshot.
     * </UL>
     *
     * @param snapshot is the SnapshotIfc which contains the desired state of
     *            the cargo.
     * @exception ObjectRestoreException is thrown when the cargo cannot be
     *                restored with this snapshot
     */
    // --------------------------------------------------------------------------
    public void restoreSnapshot(SnapshotIfc snapshot) throws ObjectRestoreException
    {
    }

    // ----------------------------------------------------------------------
    /**
     * Returns the appropriate function ID.
     *
     * @return int RoleFunctionIfc.INVENTORY_INQUIRY
     */
    // ----------------------------------------------------------------------
    public int getAccessFunctionID()
    {
        return RoleFunctionIfc.INVENTORY_INQUIRY;
    }

    public String getLetterName()
    {
        return letterName;
    }

    public void setLetterName(String letterName)
    {
        this.letterName = letterName;
    }

    public String getSpecificStoreId()
    {
        return specificStoreId;
    }

    public void setSpecificStoreId(String specificStoreId)
    {
        this.specificStoreId = specificStoreId;
    }

    public int getStoreSelectType()
    {
        return storeSelectType;
    }

    public void setStoreSelectType(int storeSelectType)
    {
        this.storeSelectType = storeSelectType;
    }

    public void resetStoreSelectType()
    {
        this.storeSelectType = HOME_STORE_INQUIRY;
    }

    // ----------------------------------------------------------------------
    /**
     * Sets the minimun quantity for the lookup
     *
     * @param value minimun quantity
     */
    // ----------------------------------------------------------------------
    public void setMinQtyAvailable(BigDecimal value)
    {
        minQtyAvailable = value;
    }

    // ----------------------------------------------------------------------
    /**
     * Gets the minimum quantity obtained after the lookup
     *
     * @return BigDecimal minimum quantity
     */
    // ----------------------------------------------------------------------
    public BigDecimal getMinQtyAvailable()
    {
        return minQtyAvailable;
    }

    public int getItemInquiryType()
    {
        return itemInquiryType;
    }

    public void setItemInquiryType(int itemInquiryType)
    {
        this.itemInquiryType = itemInquiryType;
    }

    // ----------------------------------------------------------------------
    /**
     * Returns the item.
     *
     * @return ItemInfoIfc The item.
     */
    // ----------------------------------------------------------------------
    public ItemInfoIfc getItemInfo()
    {
        return (itemInfo);
    }

    // ----------------------------------------------------------------------
    /**
     * Sets the item selected.
     *
     * @param itemID The selected item.
     */
    // ----------------------------------------------------------------------
    public void setItemInfo(ItemInfoIfc item)
    {
        itemInfo = item;

    }

    // ----------------------------------------------------------------------
    /**
     * Returns the items list.
     *
     * @return ItemInfoIfc[] The item list.
     */
    // ----------------------------------------------------------------------
    public ItemInfoIfc[] getItemList()
    {
        return (itemList);
    }

    // ----------------------------------------------------------------------
    /**
     * Returns the item serial number.
     *
     * @return The item serial number.
     */
    // ----------------------------------------------------------------------
    public String getItemSerial()
    {
        return (itemSerial);
    }

    // ----------------------------------------------------------------------
    /**
     * Sets the item serial number.
     *
     * @param addedItemSerial The new item serial number.
     */
    // ----------------------------------------------------------------------
    public void setItemSerial(String addedItemSerial)
    {
        itemSerial = addedItemSerial;
    }

    // ----------------------------------------------------------------------
    /**
     * Sets the itemScanned flag.
     *
     * @param value boolean
     */
    // ----------------------------------------------------------------------
    public void setItemScanned(boolean value)
    {
        itemScanned = value;
    }

    // ----------------------------------------------------------------------
    /**
     * Returns the itemScanned flag.
     *
     * @return boolean
     */
    // ----------------------------------------------------------------------
    public boolean isItemScanned()
    {
        return itemScanned;
    }

    // ----------------------------------------------------------------------
    /**
     * Returns the item number.
     *
     * @return String item number.
     */
    // ----------------------------------------------------------------------
    public ItemInquirySearchCriteriaIfc getInquiry()
    {
        return (inquiryItem);
    }

    // ----------------------------------------------------------------------
    /**
     * Sets the search criteria.
     *
     * @param value search certeria.
     */
    // ----------------------------------------------------------------------
    public void setInquiry(ItemInquirySearchCriteriaIfc inquiry)
    {
        inquiryItem = inquiry;
    }

    /**
     * Sets inquiry object
     *
     * @param itemNo
     * @param itemDesc
     * @param deptID
     */
    public void setInquiry(String itemNo, String itemDesc, String deptID)
    {
        setInquiry(itemNo, itemDesc, deptID, null);
    }

    /**
     * Adds manufacturer name to cargo
     *
     * @param itemNo
     * @param itemDesc
     * @param manufacturer
     * @param deptID
     */
    public void setInquiry(String itemNo, String itemDesc, String deptID, String geoCode)
    {
        // setInquiry(itemNo, itemDesc, deptID, geoCode, null);
    }

    // ----------------------------------------------------------------------
    /**
     * Sets the inquiry item.
     *
     * @param value the new item number.
     */
    // ----------------------------------------------------------------------
    public void setInquiry(int hierarchyId, String itemNo, String itemDesc, String deptID, String geoCode,
            String manufacturer, ArrayList<String> parentIDs, boolean serachItemAtRootLevel,
            String itemTypeCode, String uomID, String itemStyleCode, String itemColorCode, String itemSizeCode)
    {
        if (inquiryItem == null)
        {
            inquiryItem = DomainGateway.getFactory().getItemInquirySearchCriteriaInstance();
        }
        inquiryItem.setHierarchyID(hierarchyId);
        inquiryItem.setItemID(itemNo);
        inquiryItem.setDescription(itemDesc);
        inquiryItem.setManufacturer(manufacturer);
        setShowListFlag(false);
        setModifiedFlag(true);
        inquiryItem.setSearchForItemByManufacturer(searchItemByManufacturer);
        inquiryItem.setParentIDs(parentIDs);
        inquiryItem.setSerachItemAtRootLevel(serachItemAtRootLevel);

        inquiryItem.setSearchItemByType(searchItemByType);
        if(searchItemByType)
            setItemType(itemTypeCode);
        inquiryItem.setSearchItemByUOM(searchItemByUOM);

        if(searchItemByUOM)
            setUom(uomID);
        inquiryItem.setSearchItemByStyle(searchItemByStyle);

        if(searchItemByStyle)
            setStyle(itemStyleCode);
        inquiryItem.setSearchItemByColor(searchItemByColor);

        if(searchItemByColor)
            setColor(itemColorCode);
        inquiryItem.setSearchItemBySize(searchItemBySize);

        if(searchItemBySize)
            setSize(itemSizeCode);

        inquiryItem.setItemTypeCode(itemTypeCode);
        inquiryItem.setItemUOMCode(uomID);
        inquiryItem.setItemStyleCode(itemStyleCode);
        inquiryItem.setItemColorCode(itemColorCode);
        inquiryItem.setItemSizeCode(itemSizeCode);
    }

    public void setMaxMatches(int maxMatches)
    {
        inquiryItem.setMaximumMatches(maxMatches);
    }

    // ----------------------------------------------------------------------
    /**
     * Returns the flag that indicates wether to show item detail or a list of
     * items.
     *
     * @return boolean showListFlag
     */
    // ----------------------------------------------------------------------
    public boolean getShowListFlag()
    {
        return showListFlag;
    }

    // ----------------------------------------------------------------------
    /**
     * Sets the flag that indicates wether to show item detail or a list of
     * items.
     *
     * @param boolean showListFlag
     */
    // ----------------------------------------------------------------------
    public void setShowListFlag(boolean value)
    {
        showListFlag = value;
    }

    // ----------------------------------------------------------------------
    /**
     * Returns true if the item should be added to the current transaction.
     * False otherwise.
     *
     * @return boolean True if the item should be added to the current
     *         transaction. False otherwise.
     */
    // ----------------------------------------------------------------------
    public boolean getModifiedFlag()
    {
        return (modifiedFlag);
    }

    // ----------------------------------------------------------------------
    /**
     * Sets the flag that determines whether the item should be added to the
     * current transaction.
     *
     * @param value True if the item should be added to the current transaction.
     *            False otherwise.
     */
    // ----------------------------------------------------------------------
    public void setModifiedFlag(boolean value)
    {
        modifiedFlag = value;
    }

    // ----------------------------------------------------------------------
    /**
     * Returns list of invalid fields.
     *
     * @return The list of invalid fields.
     */
    // ----------------------------------------------------------------------
    public Vector getInvalidFields()
    {
        return fields;
    }

    // ----------------------------------------------------------------------
    /**
     * Resets invalid fields buffer.
     */
    // ----------------------------------------------------------------------
    public void resetInvalidFieldCounter()
    {
        fields = null;
    }

    // ----------------------------------------------------------------------
    /**
     * Sets the list of invalid fields
     *
     * @param fieldId the id of the field with invalid data.
     */
    // ----------------------------------------------------------------------
    public void setInvalidField(int fieldId)
    {
        // this should be an enumeration
        if (fields == null)
        {
            fields = new Vector<Integer>(2);
        }
        fields.addElement(new Integer(fieldId));
    }

    // ----------------------------------------------------------------------
    /**
     * Sets the itemlist.
     *
     * @param items The new list of items.
     */
    // ----------------------------------------------------------------------
    public void setItemList(ItemInfoIfc[] items)
    {
        if (items == null)
            return;

        // update list
        itemList = new ItemInfoIfc[items.length];
        System.arraycopy(items, 0, itemList, 0, items.length);
        setShowListFlag(true);
    }

    // ----------------------------------------------------------------------
    /**
     * Sets the item size.
     *
     * @param value String
     */
    // ----------------------------------------------------------------------
    public void setItemSize(String size)
    {
        itemSize = size;
    }

    // ----------------------------------------------------------------------
    /**
     * Gets the item size.
     *
     * @return String
     */
    // ----------------------------------------------------------------------
    public String getItemSize()
    {
        return itemSize;
    }

    // ----------------------------------------------------------------------
    /**
     * To confirm if the item can be searched using the Manufacturer search
     * criteria.
     * <P>
     *
     * @return boolean value of the parameter " SearchItemByManufacturer"
     */
    // ----------------------------------------------------------------------
    public boolean isSearchItemByManufacturer()
    {
        return searchItemByManufacturer;
    }

    // ----------------------------------------------------------------------
    /**
     * To set the boolean value confirming if the item can be searched using the
     * Manufacturer criteria.
     * <P>
     *
     * @param boolean value of the parameter " SearchItemByManufacturer"
     */
    // ----------------------------------------------------------------------
    public void setSearchItemByManufacturer(boolean searchItemByManufacturer)
    {
        this.searchItemByManufacturer = searchItemByManufacturer;
    }

    public InventoryResultsListItem[] getInventoryList()
    {
        return inventoryList;
    }

    public void setInventoryList(InventoryResultsListItem[] inventory)
    {
        // update list
        inventoryList = inventory;
    }

    /**
     * Getter method to obtain the flag to identify the flow is whether through
     * Main inventory or Item price inquiry
     * @return true if the flow is through main Inventory, false otherwise.
     */
    public boolean isMainInventoryFlowFlag()
    {
        return mainInventoryFlowFlag;
    }

    /**
     * Setter method to set the flag to identify the flow is whether through
     * Main inventory or Item price inquiry
     */
    public void setMainInventoryFlowFlag(boolean mainInventoryFlowFlag)
    {
        this.mainInventoryFlowFlag = mainInventoryFlowFlag;
    }

    /**
     * Getter method to obtain the flag to search the item at the root level or not
     * @return true if the item has to be searched at the root level, false otherwise.
     */
    public boolean isSerachItemAtRootLevel()
    {
        return serachItemAtRootLevel;
    }

    /**
     * Setter method to set the flag to search the item at the root level
     */
    public void setSerachItemAtRootLevel(boolean serachItemAtRootLevel)
    {
        this.serachItemAtRootLevel = serachItemAtRootLevel;
    }

    /**
     *
     * Getter method to obtain the selected level
     * @return String The selected level.
     */
    public String getSelectedLevel()
    {
        return selectedLevel;
    }

    /**
     * Setter method to set the selected level
     */
    public void setSelectedLevel(String selectedLevel)
    {
        this.selectedLevel = selectedLevel;
    }

    /**
     * Getter method to obtain the merchandise hierarchy levels map
     * @return TreeMap Merchandise hierarchy levels map.
     */
    public TreeMap getMerchHierarchyLevelsMap()
    {
        return merchHierarchyLevelsMap;
    }

    /**
     * Setter method to set the merchandise hierarchy levels map
     */
    public void setMerchHierarchyLevelsMap(TreeMap merchHierarchyLevelsMap)
    {
        this.merchHierarchyLevelsMap = merchHierarchyLevelsMap;
    }

    /**
     * Getter method to obtain the merchandise hierarchy drop down map
     * @return Treemap true Merchandise hierarchy drop down map.
     */
    public TreeMap getMerchHierarchyDropDownMap()
    {
        return merchHierarchyDropDownMap;
    }

    /**
     * Setter method to obtain the merchandise hierarchy drop down map
     */
    public void setMerchHierarchyDropDownMap(TreeMap merchHierarchyDropDownMap)
    {
        this.merchHierarchyDropDownMap = merchHierarchyDropDownMap;
    }

    /**
     * Getter method to obtain the list of parent IDs
     * @return ArrayList List of parent IDs.
     */
    public ArrayList getParentIDs()
    {
        return parentIDs;
    }

    /**
     * Setter method to set the list of parent IDs into a local arraylist
     */
    public void setParentIDs(ArrayList parentIDs)
    {
        this.parentIDs = parentIDs;
    }
}
