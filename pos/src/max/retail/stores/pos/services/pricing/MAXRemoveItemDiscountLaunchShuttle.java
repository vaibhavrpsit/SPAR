/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Jyoti Rawal		09/04/2013		Initial Draft: Changes for Employee Discount
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.pricing;

// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.pricing.PricingCargo;

//------------------------------------------------------------------------------
/**
 * This shuttle carries the required contents from the pricing service to the
 * price override service.
 * <P>
 * 
 * @version $Revision: 1.2 $
 **/
// ------------------------------------------------------------------------------
public class MAXRemoveItemDiscountLaunchShuttle extends FinancialCargoShuttle {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9140521891880644531L;

	/** The logger to which log messages will be sent. **/
	protected static Logger logger = Logger.getLogger(max.retail.stores.pos.services.pricing.MAXRemoveItemDiscountLaunchShuttle.class);

	/** revision number supplied by source-code-control system **/
	public static String revisionNumber = "$Revision: 1.2 $";

	/** class name constant **/
	public static final String SHUTTLENAME = "MAXRemoveItemDiscountLaunchShuttle";		//Rev 1.0 changes

	/** Pricing Cargo **/
	protected PricingCargo pricingCargo = null;

	// --------------------------------------------------------------------------
	/**
	 * Copies information from the cargo used in the pricing service.
	 * <P>
	 * 
	 * @param bus
	 *            the bus being loaded
	 **/
	// --------------------------------------------------------------------------
	public void load(BusIfc bus) {
		super.load(bus);
		pricingCargo = (PricingCargo) bus.getCargo();
	}

	// --------------------------------------------------------------------------
	/**
	 * Copies information to the cargo used in the pricing service.
	 * <P>
	 * 
	 * @param bus
	 *            the bus being unloaded
	 **/
	// --------------------------------------------------------------------------
	public void unload(BusIfc bus) {
		super.unload(bus);
		MAXPricingCargo pc = (MAXPricingCargo) bus.getCargo(); //Rev 1.0 changes
		pc.setItems(pricingCargo.getItems());
		pc.setIndices(pricingCargo.getIndices());
		pc.setTransaction(pricingCargo.getTransaction());
		pc.setDiscountType(pricingCargo.getDiscountType());
		pc.setAccessFunctionID(RoleFunctionIfc.DISCOUNT);
		pc.setEmployeeRemoveSelected(true);
		pc.setEmployeeDiscountID(pricingCargo.getEmployeeDiscountID());

	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves the source-code-control system revision number.
	 * <P>
	 * 
	 * @return String representation of revision number
	 **/
	// ---------------------------------------------------------------------
	public String getRevisionNumber() {
		return (revisionNumber);
	}
}
