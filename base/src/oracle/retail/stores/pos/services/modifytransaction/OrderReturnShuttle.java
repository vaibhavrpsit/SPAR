/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/OrderReturnShuttle.java /main/1 2012/07/23 11:44:40 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     07/23/12 - modify order search flow for xchannel order and
*                        special order
* yiqzhao     07/19/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.modifytransaction;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.order.common.OrderCargo;

/**
 * Shuttles the required data from the Special Order cargo to the Pos Cargo.
 */
public class OrderReturnShuttle extends FinancialCargoShuttle
{
    private static final long serialVersionUID = 5147832879258107176L;

    /**
    Child cargo.
     **/
    protected OrderCargo orderCargo = null;

    /**
     * Get a local copy of the SpecialOrdercargo.
     * 
     * @param bus the bus being loaded
     */
    @Override
    public void load(BusIfc bus)
    {
        super.load(bus);
        // retrieve special order cargo
        orderCargo = (OrderCargo)bus.getCargo();
    }

    /**
     * Copy required data from the SpecialOrder cargo to the ModifyTransaction
     * Cargo.Sets the retailtransaction to the newly created special order
     * transaction.
     * 
     * @param bus the bus being unloaded
     */
    @Override
    public void unload(BusIfc bus)
    {
    	ModifyTransactionCargo cargo = (ModifyTransactionCargo)bus.getCargo();

        cargo.setLastReprintableTransactionID(orderCargo.getLastReprintableTransactionID());       

        // pass along the special order transaction
        cargo.setTransaction(orderCargo.getOrderTransaction());

        // if there is a transaction, set the tender limits
        if (cargo.getTransaction() != null)
        {
            cargo.getTransaction().setTenderLimits(cargo.getTenderLimits());
        }
    }
}