/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/processorder/ReturnReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:59 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       07/22/10 - set original return txn to the process order cargo
 *    acadar    06/21/10 - changes for return flow
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    05/21/10 - additional changes for process order flow
 *    acadar    05/14/10 - initial version for external order processing
 *    acadar    05/14/10 - initial version
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.externalorder.processorder;

import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.returns.returnoptions.ReturnOptionsCargo;

/**
 * This shuttle updates the Process Order service with the information from the Return
 * service.
 *
 */
public class ReturnReturnShuttle extends oracle.retail.stores.pos.services.sale.ReturnReturnShuttle
{

    /**
     * This id is used to tell
     * the compiler not to generate a
     * new serialVersionUID.
     */
    private static final long serialVersionUID = -7313684941747028661L;

    /**
     * Returns cargo
     */
    protected ReturnOptionsCargo returnsCargo;

    /**
     * Copies information needed from child service.
     * @param bus Child Service Bus to copy cargo from.
     */
    public void load(BusIfc bus)
    {
        super.load(bus);
        // retrieve cargo from the child(ReturnOptions Cargo)
        returnsCargo = (ReturnOptionsCargo) bus.getCargo();
    }

    /**
     * Stores information needed by parent service.
     *
     * @param bus
     *            Parent Service Bus to copy cargo to.
     */
    public void unload(BusIfc bus)
    {

        // Call unload on oracle.retail.stores.pos.services.sale.ReturnReturnShuttle
        super.unload(bus);
        ProcessOrderCargo cargo = (ProcessOrderCargo)bus.getCargo();
        SaleReturnTransactionIfc returnTransaction = returnsCargo.getTransaction();
        returnTransaction.setExternalOrderID(cargo.getExternalOrder().getId());
        returnTransaction.setExternalOrderNumber(cargo.getExternalOrder().getNumber());
        cargo.setTransaction(returnTransaction);
        cargo.setOriginalReturnTransactions(returnsCargo.getOriginalReturnTransactions());
    }



}
