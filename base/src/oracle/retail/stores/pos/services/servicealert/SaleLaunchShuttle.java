/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   10/22/14 - Refactor to restore Fulfillment main option flow.
 *    asinton   10/22/14 - initial checkin
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.servicealert;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

/**
 * @since 14.1
 *
 */
public class SaleLaunchShuttle extends FinancialCargoShuttle
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.FinancialCargoShuttle#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
        super.load(bus);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.FinancialCargoShuttle#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        SaleCargoIfc saleCargo = (SaleCargoIfc)bus.getCargo();
        saleCargo.setFromFulfillment(true);
    }

}
