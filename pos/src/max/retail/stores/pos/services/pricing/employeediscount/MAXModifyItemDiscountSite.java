/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Jyoti Rawal		09/04/2013		Initial Draft: Changes for Employee Discount
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.pricing.employeediscount;

import max.retail.stores.pos.services.modifytransaction.discount.MAXModifyTransactionDiscountCargo;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.pricing.DiscountCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//------------------------------------------------------------------------------
/**
 * Displays item discount by amount dialog.
 * 
 * @version $Revision: 3$
 **/
// ------------------------------------------------------------------------------
public class MAXModifyItemDiscountSite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2395270236028256203L;
	/**
	 * revision number supplied by Team Connection
	 **/
	public static final String revisionNumber = "$Revision: 3$";

	// --------------------------------------------------------------------------
	/**
	 * Displays item discount by amount dialog.
	 * 
	 * @param bus
	 *            BusIfc
	 */
	// --------------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		/**
		 * Rev 1.0 changes start here
		 */
		String pmValue = null;
		ParameterManagerIfc pm2 = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

		MAXModifyTransactionDiscountCargo mTDC = (MAXModifyTransactionDiscountCargo) bus.getCargo();
		
		try {
			pmValue = pm2.getStringValue("EmployeeDiscountMethod");
		} catch (ParameterException e) {
			if (logger.isInfoEnabled())
				logger.info("MAXModifyItemDiscountSite.arrive(), cannot find EmployeeDiscountMethod parameter.");
		}
		if (((MAXModifyTransactionDiscountCargo) bus.getCargo()).isEmployeeRemoveSelected()
				&& (((MAXModifyTransactionDiscountCargo) bus.getCargo()).getEmployeeDiscountID() != null)) {
			bus.mail("RemoveAutoEmpDisc", BusIfc.CURRENT);
		}
		if (("Automatic".equalsIgnoreCase(pmValue))) {
			bus.mail("AutoEmpDisc", BusIfc.CURRENT);
			/**
			 * Rev 1.0 changes end here
			 */
		} else {

			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			DiscountCargoIfc cargo = (DiscountCargoIfc) bus.getCargo();

			// check to see if this is an item discount by amount or percent
			// set argument text with localized text
			if (cargo.getDiscountType() == DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT) {
				// show the discount amount entry screen
				ui.showScreen(POSUIManagerIfc.ENTER_EMPLOYEE_AMOUNT_DISCOUNT, new POSBaseBeanModel());
			} else {
				// show the discount percent entry screen
				ui.showScreen(POSUIManagerIfc.ENTER_EMPLOYEE_PERCENT_DISCOUNT, new POSBaseBeanModel());
			}
		}

	}
}
