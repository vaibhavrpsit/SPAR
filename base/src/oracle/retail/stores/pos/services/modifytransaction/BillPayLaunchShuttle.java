/* ===========================================================================
* Copyright (c) 2009, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    nkgautam  06/22/10 - bill pay changes
 *    nkgautam  06/21/10 - initial version
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.BillPayCargo;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;

public class BillPayLaunchShuttle extends FinancialCargoShuttle 
{
    private static final long serialVersionUID = -659789790218622395L;
    /**
	 * Parent class cargo
	 */
	protected ModifyTransactionCargo cargo;

	/**
	 * Loads the parent cargo class
	 */
	public void load(BusIfc bus) {
		super.load(bus);
		cargo = (ModifyTransactionCargo) bus.getCargo();
	}

	/**
	 * loads data into called cargo
	 */
	public void unload(BusIfc bus) 
	{
		super.unload(bus);
		BillPayCargo billPayCargo = (BillPayCargo) bus.getCargo();
		billPayCargo.setSalesAssociate(cargo.getSalesAssociate());
		billPayCargo.setOperator(cargo.getOperator());
		billPayCargo.setRegister(cargo.getRegister());
		billPayCargo.setStoreStatus(cargo.getStoreStatus());
		billPayCargo.setTenderLimits(cargo.getTenderLimits());
	}


}
