/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/complete/RemoveTendersFromTransactionRoad.java /rgbustores_13.4x_generic_branch/1 2011/06/19 10:05:37 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   06/19/11 - remove tenders when activation fails and we return
 *                         to the sell item screen and the transaction is still
 *                         in progress.
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.sale.complete;

import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

/**
 * This road will remove all tender line items from the transaction because we're
 * going back to the sell item screen.
 * 
 * @author asinton
 * @since 13.4
 */
@SuppressWarnings("serial")
public class RemoveTendersFromTransactionRoad extends PosLaneActionAdapter
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        SaleCargoIfc saleCargo = (SaleCargoIfc)bus.getCargo();
        SaleReturnTransactionIfc transaction = saleCargo.getTransaction();
        transaction.removeTenderLineItems();
    }

}
