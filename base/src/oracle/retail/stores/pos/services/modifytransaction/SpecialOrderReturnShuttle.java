/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/SpecialOrderReturnShuttle.java /rgbustores_13.4x_generic_branch/2 2011/08/09 11:31:52 cgreene Exp $
 * ===========================================================================
 * NOTES
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   08/09/11 - formatting and removed deprecated code
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mchellap  09/30/08 - Added generated serialVersionUID
 *    mchellap  09/29/08 - QW-IIMO Updates for code review comments
 *    mchellap  09/19/08 - QW-IIMO
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.specialorder.SpecialOrderCargo;

/**
 * Shuttles the required data from the Special Order cargo to the Pos Cargo.
 */
public class SpecialOrderReturnShuttle extends FinancialCargoShuttle
{
    private static final long serialVersionUID = 5147832879258107176L;

    /**
     * special order cargo
     */
    protected SpecialOrderCargo specialOrderCargo;

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
        specialOrderCargo = (SpecialOrderCargo)bus.getCargo();
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
        super.unload(bus);

        // retrieve Pos cargo
        ModifyTransactionCargo cargo = (ModifyTransactionCargo)bus.getCargo();

        // pass along the special order transaction
        cargo.setTransaction(specialOrderCargo.getRetailTransaction());

        // if there is a transaction, set the tender limits
        if (cargo.getTransaction() != null)
        {
            cargo.getTransaction().setTenderLimits(cargo.getTenderLimits());
        }
    }
}