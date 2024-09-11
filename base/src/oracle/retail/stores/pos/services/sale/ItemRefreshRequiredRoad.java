/* ===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/ItemRefreshRequiredRoad.java /main/1 2013/10/09 11:37:47 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     10/09/13 - Initial
 * 
 */

package oracle.retail.stores.pos.services.sale;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * This road is traversed when UNDO is selected on the ItemNotFound screen with the sole 
 * purpose of setting refreshNeeded in the cargo
 * .
 * @since 14.0
 */
public class ItemRefreshRequiredRoad extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -7410317201191199678L;
    
    /* 
     * Set refreshNeeded in the cargo.
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        cargo.setRefreshNeeded(true);
    }

}
