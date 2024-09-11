/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   11/17/14 - set fromFulfillment flag 
 *    asinton   10/22/14 - Refactor to restore Fulfillment main option flow.
 *    asinton   10/22/14 - Initial checkin.
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.modifytransaction;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.order.common.OrderCargo;

/**
 * Launch Shuttle to set state values on the Order Cargo.
 * @since 14.1
 *
 */
public class OrderLaunchShuttle extends FinancialCargoShuttle
{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3069302542680775645L;

    /**
     * Handle to the ModifyTransactionCargo.
     */
    protected ModifyTransactionCargo modifyTransactionCargo = null;

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.FinancialCargoShuttle#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
        super.load(bus);
        modifyTransactionCargo = (ModifyTransactionCargo)bus.getCargo();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.FinancialCargoShuttle#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        if(modifyTransactionCargo.isFromFulfillment())
        {
            OrderCargo orderCargo = (OrderCargo)bus.getCargo();
            orderCargo.setPopupMenuNeeded(false);
            orderCargo.setFromFulfillment(true);
        }
    }

}
