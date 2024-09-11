/*===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreatepickup/InventoryLookupStorage.java /main/1 2013/08/27 14:46:50 abhinavs Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* abhinavs    08/27/13 - Xchannel Inventory lookup enhancement phase I
* abhinavs    08/27/13 - Initial Version
* abhinavs    08/27/13 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.services.order.xchannelcreatepickup;

import java.util.List;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;


//--------------------------------------------------------------------------
/**
* This class contains the data structures for inventory lookup WS.
* @version $Revision: /main/1 $ 
*/
//--------------------------------------------------------------------------
public class InventoryLookupStorage
{  
    /**
     * ItemID 
     */
    protected String itemId;

    /**
     * Flag set to true if SRLI has multiple qty and to be bundled together for xchannel 
     * inventory lookup WS invocation
     */
    protected boolean isMultiQuantityBundled = false;

    /**
     * Itembucket for XChannel inventory lookup WS 
     */
    protected List<SaleReturnLineItemIfc> itemBucket;

    /**
     * Returns the itemId.
     * @return The itemId.
     */
    public String getItemId()
    {
        return itemId;
    }

    /**
     * Sets the itemId.
     * @param The itemId.
     */
    public void setItemId(String itemId)
    {
        this.itemId = itemId;
    }

    /**
     * Returns the itemBucket
     * @return The List of itemBucket.
     */
    public List<SaleReturnLineItemIfc> getItemBucket()
    {
        return itemBucket;
    }

    /**
     * Sets the itemBucket.
     * @param The itemBucket.
     */
    public void setItemBucket(List<SaleReturnLineItemIfc> itemBucket)
    {
        this.itemBucket = itemBucket;
    }

    /**
     * Returns the multiQuantityBundled
     * @return The flag multiQuantityBundled.
     */
    public boolean getMultiQuantityBundled()
    {
        return isMultiQuantityBundled;
    }

    /**
     * Sets the multiQuantityBundled.
     * @param The  multiQuantityBundled.
     */
    public void setMultiQuantityBundled(boolean isMultiQuantityBundled)
    {
        this.isMultiQuantityBundled = isMultiQuantityBundled;
    }

}

