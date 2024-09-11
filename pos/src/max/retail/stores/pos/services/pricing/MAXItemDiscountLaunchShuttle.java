/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Jyoti Rawal		09/04/2013		Initial Draft: Changes for Employee Discount
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.pricing;

// foundation imports
import org.apache.log4j.Logger;

import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXSaveManagerOverrideTransaction;
import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import max.retail.stores.domain.manageroverride.MAXManagerOverride;
import max.retail.stores.pos.manager.ifc.MAXUtilityManagerIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.common.RetailTransactionCargoIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

//------------------------------------------------------------------------------
/**
    This shuttle carries the required contents from
    the pricing service to the price override service. <P>
    @version $Revision: 3$
**/
//------------------------------------------------------------------------------
public class MAXItemDiscountLaunchShuttle extends FinancialCargoShuttle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3891757066753690097L;

	/** The logger to which log messages will be sent. **/
	protected static Logger logger = Logger
			.getLogger(max.retail.stores.pos.services.pricing.MAXItemDiscountLaunchShuttle.class); // Rev 1.0 changes

	/** revision number supplied by source-code-control system **/
	public static String revisionNumber = "$Revision: 3$";

	/** class name constant **/
	public static final String SHUTTLENAME = "MAXItemDiscountLaunchShuttle"; // Rev 1.0 changes

	/** Pricing Cargo **/
	protected MAXPricingCargo MAXPricingCargo = null; // Rev 1.0 changes

	// --------------------------------------------------------------------------
	/**
	 * Copies information from the cargo used in the pricing service.
	 * <P>
	 * 
	 * @param bus the bus being loaded
	 **/
	// --------------------------------------------------------------------------
	public void load(BusIfc bus) {
		super.load(bus);
		MAXPricingCargo = (MAXPricingCargo) bus.getCargo(); // Rev 1.0 changes
	}

	// --------------------------------------------------------------------------
	/**
	 * Copies information to the cargo used in the pricing service.
	 * <P>
	 * 
	 * @param bus the bus being unloaded
	 **/
	// --------------------------------------------------------------------------
	public void unload(BusIfc bus) {
		super.unload(bus);
		/**
		 * Rev 1.0 changes start here
		 */
		MAXPricingCargo pc = (MAXPricingCargo) bus.getCargo();
		pc.setItems(MAXPricingCargo.getItems());
		pc.setIndices(MAXPricingCargo.getIndices());
		pc.setTransaction(MAXPricingCargo.getTransaction());
		pc.setDiscountType(MAXPricingCargo.getDiscountType());
		pc.setAccessFunctionID(RoleFunctionIfc.DISCOUNT);
		pc.setEmployeeDiscountID(MAXPricingCargo.getEmployeeDiscountID());
		pc.setAccessFunctionID(RoleFunctionIfc.DISCOUNT);
		MAXUtilityManagerIfc MAXUtilityManager = (MAXUtilityManagerIfc) bus.getManager("UtilityManager");
		SaleReturnLineItemIfc[] MAXSaleReturnLineItem = pc.getItems();
	
		pc.setSpclEmpDisc(MAXPricingCargo.getSpclEmpDisc());
		System.out.println("93 MAXItemDiscountLaunchShuttle============== "+pc.getSpclEmpDisc());
		/*
		 * for (int i = 0; i < MAXSaleReturnLineItem.length; i++) {
		 * 
		 * //RetailTransactionCargoIfc cargo;
		 * 
		 * MAXUtilityManager.updateManagerOverrideMap( MAXRoleFunctionIfc.DISCOUNT,
		 * MAXSaleReturnLineItem[i] .getPLUItem().getItemID()); logger.assertLog(true,
		 * "Mukesh");
		 * 
		 * //changes for set value in manager override start // SaleCargoIfc cargo1 =
		 * (SaleCargoIfc)bus.getCargo(); RetailTransactionIfc trans =
		 * pc.getTransaction(); MAXManagerOverride mgo= new MAXManagerOverride();
		 * mgo.setBusinessDay(pc.getTransaction().getBusinessDay());
		 * mgo.setStoreCreditId(null); mgo.setFeatureId("111"); mgo.setItemId(null);
		 * mgo.setManagerId(pc.getOverrideOperator().getEmployeeID());
		 * mgo.setSequenceNumber("-1");
		 * mgo.setTransactionID(pc.getTransaction().getTransactionID());
		 * mgo.setWsID(trans.getWorkstation().getWorkstationID().toString());
		 * mgo.setStoreID(trans.getWorkstation().getStoreID().toString());
		 * MAXSaveManagerOverrideTransaction dbTrans = null; dbTrans =
		 * (MAXSaveManagerOverrideTransaction)
		 * DataTransactionFactory.create(MAXDataTransactionKeys.
		 * SAVE_MANAGER_OVERRIDE_TRANSACTION); try { dbTrans.saveManagerOverride(mgo); }
		 * catch (DataException |
		 * com.extendyourstore.foundation.manager.data.DataException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } //changes for set value in
		 * manager override end catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 * 
		 * }
		 */
		// Object pricingCargo1;
		/*
		 * if (pc instanceof MAXPricingCargo && pricingCargo1 != null) {
		 * ((MAXPricingCargo)
		 * pc).setAutoDiscountApplied(pricingCargo1).isAutoDiscountApplied());
		 * ((MAXPricingCargo)
		 * pc).setAutoDiscountCode(pricingCargo1).getAutoDiscountCode()); }
		 */
		/**
		 * Rev 1.0 changes end here
		 */

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
