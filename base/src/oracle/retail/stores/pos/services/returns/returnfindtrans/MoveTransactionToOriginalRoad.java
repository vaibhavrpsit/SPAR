/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnfindtrans/MoveTransactionToOriginalRoad.java /main/1 2014/03/11 17:13:56 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/05/14 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnfindtrans;

import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

/**
 * Apply the return order transaction by "moving" it in the cargo to the original
 * transaction.
 * 
 * @version $Revision: /main/1 $
 */
@SuppressWarnings("serial")
public class MoveTransactionToOriginalRoad extends LaneActionAdapter
{
    /**
     * revision number supplied by Team Connection
     **/
    public static final String revisionNumber = "$Revision: /main/1 $";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // Update the cargo
        ReturnFindTransCargo cargo = (ReturnFindTransCargo)bus.getCargo();
        for (SaleReturnTransactionIfc trans : cargo.getTransactions())
        {
            if (trans.getTransactionIdentifier().equals(cargo.getOriginalTransactionId()))
            {
                cargo.moveTransactionToOriginal(trans);
                break;
            }
        }
    }


}
