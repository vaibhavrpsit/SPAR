/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/common/IsWebManagedOrderSignal.java /main/1 2014/03/11 17:13:56 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/04/14 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.common;

import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.pos.services.returns.returncommon.AbstractFindTransactionCargo;
import oracle.retail.stores.pos.services.returns.returnfindtrans.ReturnFindTransCargo;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

/**
 * If the transaction in the cargo (or any returns in the cargo) are marked as
 * {@link SaleReturnTransactionIfc#isWebManagedOrder()} then this signal
 * returns true.
 * 
 * @author cgreene
 * @since 14.0.1
 */
@SuppressWarnings("serial")
public class IsWebManagedOrderSignal implements TrafficLightIfc
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc#roadClear(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public boolean roadClear(BusIfc bus)
    {
        SaleReturnTransactionIfc transaction = null;
        if (bus.getCargo() instanceof SaleCargoIfc)
        {
            SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
            transaction = cargo.getTransaction();
        }
        else if (bus.getCargo() instanceof AbstractFindTransactionCargo)
        {
            AbstractFindTransactionCargo cargo = (AbstractFindTransactionCargo)bus.getCargo();
            transaction = cargo.getTransaction();
            if (transaction == null && cargo instanceof ReturnFindTransCargo)
            {
                for (SaleReturnTransactionIfc txn : ((ReturnFindTransCargo)cargo).getTransactions())
                {
                    if (txn != null)
                    {
                        transaction = txn;
                        break;
                    }
                }
            }
        }

        return (transaction != null)? transaction.isWebManagedOrder() : false;
    }

}
