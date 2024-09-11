/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tksharma  09/14/11 - unload() method , getting serialinput from
 *                         SerialNumber and not from itemId
 *    cgreene   05/26/10 - convert to oracle packaging
 *    nkgautam  01/22/10 - fix for imei lookup flow
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/15/09 - Return Shuttle class for UIN Lookup tour
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import java.util.Collection;
import java.util.HashMap;

import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;

/**
 * Return Shuttle class for UIN Lookup tour
 * @author nkgautam
 */
public class UINLookUpReturnShuttle implements ShuttleIfc
{

    /**
     * IMEI Cargo class
     */
    protected UINLookUpCargo uinCargo;

    /**
     * Loads the UIN LookUp cargo.
     * @param  bus
     */
    public void load(BusIfc bus)
    {
        uinCargo = (UINLookUpCargo)bus.getCargo();
    }

    /**
     * Transfers the UIN Lookup cargo to the Item Inquiry
     * cargo.
     * @param  bus
     */
    public void unload(BusIfc bus)
    {
        ItemInquiryCargo inquiryCargo = (ItemInquiryCargo) bus.getCargo();
        SearchCriteriaIfc criteria = uinCargo.getInquiry();
        String serialInput = criteria.getItemSerialNumber();
        inquiryCargo.setItemIMEINumber(serialInput);
        if (uinCargo.getPluItemsMap() != null && uinCargo.getPluItemsMap().size() != 0)
        {
            PLUItemIfc[] pluItems = convertMapToArray(uinCargo.getPluItemsMap());
            inquiryCargo.setItemList(pluItems);
            if (pluItems.length == 1)
            {
                inquiryCargo.setPLUItem(pluItems[0]);
            }
            inquiryCargo.setPLUItem(uinCargo.getPluItem());
        }
    }

    /**
     * Utilitu method to convert HashMap to Array
     * @param pluItems
     * @return
     */
    public PLUItemIfc[] convertMapToArray(HashMap<String, PLUItemIfc> pluItems)
    {
        PLUItemIfc[] pluItemsArray = new PLUItemIfc[pluItems.size()];
        Collection<PLUItemIfc> itemCollection = pluItems.values();
        int itemCount = 0;
        for (PLUItemIfc item : itemCollection)
        {
            pluItemsArray[itemCount++] = item;
        }
        return pluItemsArray;
    }
}
