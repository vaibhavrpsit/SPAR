/* ===========================================================================
* Copyright (c) 2009, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    nkgautam  10/14/10 - included cash drawer warning check
 *    nkgautam  06/22/10 - bill pay changes
 *    nkgautam  06/21/10 - initial version
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.BillPayCargo;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;

public class BillPayReturnShuttle extends FinancialCargoShuttle 
{
    private static final long serialVersionUID = -4429182668462333886L;
    
    /*
     * Parent cargo
     */
    protected BillPayCargo  billPayCargo = null;
    
    /**
     * Loads the called cargo class
     */
    public void load(BusIfc bus)
    {
        super.load(bus);
        billPayCargo = (BillPayCargo) bus.getCargo();
    }

    /**
     * loads data into parent tour cargo
     */
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        ModifyTransactionCargo  modifyTransactionCargo = (ModifyTransactionCargo)bus.getCargo();
        modifyTransactionCargo.setCashDrawerUnderWarning(billPayCargo.isCashDrawerUnderWarning());
    }


}
