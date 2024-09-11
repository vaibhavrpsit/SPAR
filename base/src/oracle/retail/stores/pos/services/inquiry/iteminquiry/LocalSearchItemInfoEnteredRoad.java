/* ===========================================================================
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;

/**
 * This road is traveled when the user enters the item number and requests the 
 * for search on a brand new ID. It extends ItemInfoEnteredRoad so that it can
 * call super.traverse() on that class.
 */
@SuppressWarnings("serial")
public class LocalSearchItemInfoEnteredRoad extends ItemInfoEnteredRoad
{
    /**
     * Set itemFromWebStore to false be calling super.traverse().
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // Setting this cargo variable forces the next search to be
        // local.
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        cargo.setItemFromWebStore(false);
        super.traverse(bus);
    }
}
