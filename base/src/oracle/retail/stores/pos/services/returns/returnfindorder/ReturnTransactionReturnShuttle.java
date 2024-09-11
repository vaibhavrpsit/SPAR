/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnfindorder/ReturnTransactionReturnShuttle.java /main/1 2012/10/29 12:55:21 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     10/25/12 - Added to support returns by order.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnfindorder;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnData;
import oracle.retail.stores.pos.services.returns.returnfindtrans.ReturnFindTransCargo;
import oracle.retail.stores.pos.services.returns.returntransaction.ReturnTransactionCargo;

/**
 * This shuttle gets the data from the Return Transaction Service.
 */
public class ReturnTransactionReturnShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 251294877847834834L;

    /**
     * Child cargo
     */
    protected ReturnTransactionCargo rtCargo = null;

    /**
     * Store data from child service in the shuttle
     * 
     * @param bus Child Service Bus.
     */
    @Override
    public void load(BusIfc bus)
    {
        rtCargo = (ReturnTransactionCargo)bus.getCargo();
    }

    /**
     * Transfer child data to parent cargo.
     * 
     * @param bus Child Service Bus to copy cargo to.
     */
    @Override
    public void unload(BusIfc bus)
    {
        ReturnFindTransCargo cargo = (ReturnFindTransCargo)bus.getCargo();
        if (rtCargo.getTransferCargo())
        {
            // Build the ReturnData object from the PLU Items,
            // SaleReturnLineItemms
            // and Return items in the ReturnTransactionCargo
            cargo.setTransferCargo(true);
            ReturnData rd = cargo.buildReturnData(rtCargo.getPLUItems(), rtCargo.getReturnSaleLineItems(),
                    rtCargo.getReturnItems());
            cargo.setReturnData(rd);

            // Set the original data
            cargo.setOriginalTransaction(rtCargo.getOriginalTransaction());
            cargo.setOriginalTransactionId(rtCargo.getOriginalTransactionId());
            cargo.setOriginalExternalOrderReturnTransactions(rtCargo.getOriginalExternalOrderReturnTransactions());
        }
        else
        {
            // Reseting the ID here forces the lookup code to use the criteria
            // rather than the transaction ID on a retry.
            cargo.setOriginalTransactionId(null);
        }

        cargo.setSearchCriteria(rtCargo.getSearchCriteria());
        cargo.setTransactionFound(rtCargo.isTransactionFound());
    }

}