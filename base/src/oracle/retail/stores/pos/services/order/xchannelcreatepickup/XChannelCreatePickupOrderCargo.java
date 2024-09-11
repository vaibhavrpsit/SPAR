/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreatepickup/XChannelCreatePickupOrderCargo.java /main/7 2014/06/09 14:40:17 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   06/04/14 - Added a flag to indicate if the inventory details
 *                         are for the current storegroup
 *    yiqzhao   04/30/14 - Make pickup item from the same store as a cross
 *                         channel item.
 *    abhinavs  04/22/14 - Upgrade Common-collections to 4.0
 *    abhinavs  08/24/13 - Xchannel Inventory lookup enhancement phase I
 *    abhinavs  06/04/13 - Fix to update item attributes on undo action of
 *                         xchannel pickup
 *    sgu       01/11/13 - set xchannel order item flag based on if item is
 *                         avilable in the store inventory
 *    jswan     04/18/12 - Added to support cross channel create pickup order
 *                         feature.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.xchannelcreatepickup;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.collections4.map.MultiValueMap;

import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.domain.inventoryinquiry.promise.AvailableToPromiseInventoryIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.services.order.xchannelcreatepickup.InventoryLookupStorage;
import oracle.retail.stores.pos.services.order.createpickup.PickupDeliveryOrderCargo;

/**
 * This class contains the state for the XChannelCreatePickupOrder tour.
 */
public class XChannelCreatePickupOrderCargo extends PickupDeliveryOrderCargo implements CargoIfc
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 5293738112049027472L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/7 $";

    /** Index into the collection of line items.*/
    protected int lineItemIndex = 0;
    /** The list of stores used to lookup available inventory. */
    protected StoreIfc[] storeGroup = null;
    /** The list of available invnetory. */
    protected List<AvailableToPromiseInventoryIfc> itemAvailablityList = null;
    /** The map of stores keyed by the sale return line item number. */
    protected HashMap<Integer, StoreIfc> storeForPickupByLineNum = null;
    /** The map of customers keyed by the sale return line item number. */
    protected LinkedHashMap<Integer, CaptureCustomerIfc> customerForPickupByLineNum = null;
    /** The map of pickup dates keyed by the sale return line item number. */
    protected HashMap<Integer, EYSDate>  dateForPickupByLineNum = null;

    /** Indicates if the store list was retrieved by criteria or group. */
    protected boolean listFromStoreGroup = true;
    /** Indicates if xchannel create pickup is executed */
    protected boolean xchannelCreatePickupExecuted=true;
    
    /** The multivalue map of line items keyed by the itemID */
    protected MultiValueMap lineItemMap;
    /** The iterator of multivalue map of line items keyed by the itemID */
    protected Iterator<String> itemIdListIterator;
    /** CurrentItemID for inventory lookup WS */
    protected String currentItemID;
    /** Flag to indicate if the inventory available for promise is from the current store's group*/
    protected boolean isInventoryFromCurrentStoreGroup;

    /**
     * Lineitem bucket for number of inventory lookup WS invocation
     */
    protected List<InventoryLookupStorage> lineItemsBucket;

	// ---------------------------------------------------------------------
    /**
     * Constructs InquiryOptionsCargo object.
     * <P>
     */
    // ---------------------------------------------------------------------
    public XChannelCreatePickupOrderCargo()
    {
        super();
        storeForPickupByLineNum = new HashMap<Integer, StoreIfc>();
        customerForPickupByLineNum = new LinkedHashMap<Integer, CaptureCustomerIfc>();
        dateForPickupByLineNum = new HashMap<Integer, EYSDate>();
    }

    /**
     * Gets the index into the collection of line items.
     * @return integer
     */
    public int getLineItemIndex()
    {
        return lineItemIndex;
    }

    /**
     * Increments the index into the collection of line items.
     */
    public void incrementLineItemIndex()
    {
        lineItemIndex++;
    }

    /**
     * Gets the index into the collection of line items.
     * @return integer
     */
    public void resetLineItemIndex()
    {
        lineItemIndex = 0;
    }

    /**
     * Gets the current line item
     * @return SaleReturnLineItemIfc
     */
    public SaleReturnLineItemIfc getCurrentLineItem()
    {
        return lineItems[lineItemIndex];
    }

    /**
     * Sets the store group array
     * @param stores
     */
    public void setStoreGroup(StoreIfc[] stores)
    {
        this.storeGroup = stores;
    }

    /**
     * Gets the store group array
     * @return stores
     */
    public StoreIfc[] getStoreGroup()
    {
        return storeGroup;
    }

    /**
     * Sets itemAvailablityList
     * @param itemAvailablityList
     */
    public void setItemAvailablityList(List<AvailableToPromiseInventoryIfc> itemAvailablityList)
    {
        this.itemAvailablityList  = itemAvailablityList;
    }
    
    /**
     * @return Returns the xchannelCreatePickupExecuted
     */
    public boolean isXchannelCreatePickupExecuted() {
    	return xchannelCreatePickupExecuted;
    }

    /**
     * @param XchannelCreatePickupExecuted The XchannelCreatePickupExecuted to set.
     */
    public void setXchannelCreatePickupExecuted(boolean xchannelCreatePickupExecuted) {
    	this.xchannelCreatePickupExecuted = xchannelCreatePickupExecuted;
    }

    /**
     * Gets itemAvailablityList
     * @return itemAvailablityList
     */
    public List<AvailableToPromiseInventoryIfc> getItemAvailablityList()
    {
        return itemAvailablityList;
    }
    
    /**
     * @return the storeForPickupByItem
     */
    public HashMap<Integer, StoreIfc> getStoreForPickupByLineNum()
    {
        return storeForPickupByLineNum;
    }

    /**
     * @param storeForPickupByItem the storeForPickupByItem to set
     */
    public void setStoreForPickupByLineNum(
            HashMap<Integer, StoreIfc> storeForPickupByLineNum)
    {
        this.storeForPickupByLineNum = storeForPickupByLineNum;
    }

    /**
     * @return the customerForPickupByLineNum
     */
    public LinkedHashMap<Integer, CaptureCustomerIfc> getCustomerForPickupByLineNum()
    {
        return customerForPickupByLineNum;
    }

    /**
     * @param customerForPickupByLineNum the customerForPickupByLineNum to set
     */
    public void setCustomerForPickupByLineNum(
            LinkedHashMap<Integer, CaptureCustomerIfc> customerForPickupByLineNum)
    {
        this.customerForPickupByLineNum = customerForPickupByLineNum;
    }

    /**
     * @return the dateForPickupByLineNum
     */
    public HashMap<Integer, EYSDate> getDateForPickupByLineNum()
    {
        return dateForPickupByLineNum;
    }

    /**
     * @param dateForPickupByLineNum the dateForPickupByLineNum to set
     */
    public void setDateForPickupByLineNum(
            HashMap<Integer, EYSDate> dateForPickupByLineNum)
    {
        this.dateForPickupByLineNum = dateForPickupByLineNum;
    }

	/**
     * @return the listFromStoreGroup
     */
    public boolean isListFromStoreGroup()
    {
        return listFromStoreGroup;
    }

    /**
     * @param listFromStoreGroup the listFromStoreGroup to set
     */
    public void setListFromStoreGroup(boolean listFromStoreGroup)
    {
        this.listFromStoreGroup = listFromStoreGroup;
    }

    /**
     * @return the lineItemMap
     */
    public MultiValueMap getLineItemMap() {
        return lineItemMap;
    }

    /**
     * @param lineItemMap the lineItemMap to set
     */
    public void setLineItemMap(MultiValueMap lineItemMap) {
        this.lineItemMap = lineItemMap;
    }

    /**
     * @return the ItemIdListIterator
     */
    public Iterator<String> getItemIdListIterator() {
        return itemIdListIterator;
    }


    /**
     * @param lineItemMap the lineItemMap to set
     */
    public void setItemIdListIterator(Iterator<String> itemIdListIterator) {
        this.itemIdListIterator = itemIdListIterator;
    }
    
    

    public String getCurrentItemID() {
        return currentItemID;
    }

    public void setCurrentItemID(String currentItemID) {
        this.currentItemID = currentItemID;
    }

    /**
     * Get the ItemAvailabilityIfc object associated with a specific store
     * @param storeID
     * @param iaList
     * @return ItemAvailabilityIfc
     */
    public AvailableToPromiseInventoryIfc getStoreItemAvailablity(String storeID,
            List<AvailableToPromiseInventoryIfc> iaList)
    {
        AvailableToPromiseInventoryIfc itemAvailability = null;
        
        for(AvailableToPromiseInventoryIfc ia: iaList)
        {
            if (ia.getStoreID().equals(storeID))
            {
                itemAvailability = ia;
                break;
            }
        }
        
        return itemAvailability;
    }

    /**
     * Get the store object associated the storeID
     * @param storeID
     * @return
     */
    public StoreIfc getStoreByID(String storeID)
    {
        StoreIfc selected = null;
        
        for(StoreIfc store: storeGroup)
        {
            if (store.getStoreID().equals(storeID))
            {
                selected = store;
                break;
            }
        }
        
        return selected;
    }
    
    // ----------------------------------------------------------------------
    /**
     * Returns the line items bucket
     * <P>
     *
     * @return The List<InventoryLookupStorage>.
     */
    // ----------------------------------------------------------------------
    public List<InventoryLookupStorage> getLineItemsBucket()
    {
        return lineItemsBucket;
    }
    
    /**
     * Sets line items bucket
     */
    public void setLineItemsBucket(List<InventoryLookupStorage> lineItemsBucket)
    {
        this.lineItemsBucket = lineItemsBucket;
    }

    /**
     * Returns if isInventoryFromCurrentStoreGroup
     * <P>
     *
     * @return The List<InventoryLookupStorage>.
     */
    public boolean isInventoryFromCurrentStoreGroup()
    {
        return isInventoryFromCurrentStoreGroup;
    }

    /**
     * Sets the isInventoryFromCurrentStoreGroup flag
     */
    public void setInventoryFromCurrentStoreGroup(boolean isInventoryFromCurrentStoreGroup)
    {
        this.isInventoryFromCurrentStoreGroup = isInventoryFromCurrentStoreGroup;
    }
}
