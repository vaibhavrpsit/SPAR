/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved. 
   Rev 1.1  29/May/2013               Izhar                                      Discount rule
  Rev 1.0	Jyoti Rawal		09/04/2013		Initial Draft: Changes for Employee Discount
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.modifytransaction.discount;

import java.util.Vector;

import max.retail.stores.domain.discount.MAXDiscountRuleConstantsIfc;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItem;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ItemContainerProxyIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItem;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.modifytransaction.discount.CheckDiscountAlreadyAppliedSite;
import oracle.retail.stores.pos.services.modifytransaction.discount.ModifyTransactionDiscountCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//--------------------------------------------------------------------------
/**
 * This site determines if discounts of this type have already been applied.
 * 
 * @version $Revision: 1.2 $
 **/
// --------------------------------------------------------------------------
public class MAXCheckDiscountAlreadyAppliedSite extends CheckDiscountAlreadyAppliedSite {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2195590373612568973L;

	/** revision number supplied by version control **/
	public static final String revisionNumber = "$Revision: 1.2 $";

	boolean condition = false;  //Rev 1.0 changes 

	// ----------------------------------------------------------------------
	/**
	 * Determines if discounts of this type have already been applied. Displays
	 * dialog if discounts have been applied and one of each type of discount is
	 * allowed.
	 * 
	 * @param bus
	 *            Service Bus
	 **/
	// ----------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		// Get required managers
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		/**
		 * Rev 1.0 changes start here
		 */
		boolean discountRuleAlreadyApplied = false;
		boolean ifSale = false;
		boolean ifLayaway = false;
		boolean spclEmpDisc = false;

		CurrencyIfc price = null;
		MAXModifyTransactionDiscountCargo mTDC = (MAXModifyTransactionDiscountCargo) bus.getCargo();

		if (mTDC.getTransaction().getTransactionType() == 19)
			ifLayaway = true;
		else if (mTDC.getTransaction().getTransactionType() == 1)
			ifSale = true;

		if (ifSale)
			condition = (((SaleReturnTransaction) mTDC.getTransaction()).getItemContainerProxy().getBestDealWinners() != null)
					&& (((SaleReturnTransaction) mTDC.getTransaction()).getItemContainerProxy().getBestDealWinners().size() != 0)
					&& (mTDC.getEmployeeDiscountID() != null);
		
		else if (ifLayaway)
			condition = (((LayawayTransactionIfc) mTDC.getTransaction()).getItemContainerProxy().getBestDealWinners() != null)
					&& (((LayawayTransactionIfc) mTDC.getTransaction()).getItemContainerProxy().getBestDealWinners().size() != 0)
					&& (mTDC.getEmployeeDiscountID() != null);

		try {
				ModifyTransactionDiscountCargo cargo = (ModifyTransactionDiscountCargo) bus.getCargo();
				SaleReturnTransactionIfc  transactionForPromo= (SaleReturnTransaction) cargo.getTransaction();
			
				MAXSaleReturnLineItemIfc li = (MAXSaleReturnLineItem) (transactionForPromo.getItemContainerProxy().getLineItemsVector()).get(0);
				//System.out.println("100 ================= "+li.getPLUItem().getSpclEmpDisc());
				//System.out.println("88============="+transactionForPromo.toString());
				
				//transactionForPromo.
				//Fix for Employee Discount not working for discounted items
				
			//	if ((li.getItemPrice().getPermanentSellingPrice()).compareTo(li.getItemPrice().getExtendedSellingPrice()) != 0)
		//			discountRuleAlreadyApplied = true;
			} 
		catch (Exception e) {

			}
			
		if (discountAlreadyAppliedDialogRequired((MAXModifyTransactionDiscountCargo) bus.getCargo(),
				(ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE))) {
			showDiscountAlreadyAppliedDialog(ui);
		} else if (discountRuleAlreadyApplied) {
			bus.mail(CommonLetterIfc.CANCEL, BusIfc.CURRENT);
		} else {
			// Send the continue or success letter
			bus.mail(CommonLetterIfc.CONTINUE, BusIfc.CURRENT);
		}
		/**
		 * Rev 1.0 changes end here
		 */
	}

	// ----------------------------------------------------------------------
	/**
	 * Returns true if the Discount Already Applied Dialog should be displayed.
	 * 
	 * @param cargo
	 *            The bus cargo
	 * @param pm
	 *            The Parameter Manager
	 * @return true if dialog should be displayed
	 */
	// ----------------------------------------------------------------------
	private boolean discountAlreadyAppliedDialogRequired(MAXModifyTransactionDiscountCargo cargo, ParameterManagerIfc pm) {
		boolean showDialogRequired = false;
		/**
		 * Rev 1.0 changes start here
		 */
		//Fix for Employee Discount not working for discounted items
//		if (condition) {
//			return showDialogRequired;
//		}
		if (cargo.isEmployeeRemoveSelected()) {
			return showDialogRequired; 
		}
		/**
		 * Rev 1.0 changes end here
		 */
		 /**
		 * Rev 1.1 changes start here
		 */
		System.out.println("165============="+cargo.getDiscountType());
		
		if (cargo.getDiscountType() == 99 || (cargo.getDiscountType() == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NatZPctoffTiered)
				|| (cargo.getDiscountType() == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NatZ$offTiered) || (cargo.getDiscountType() == 0))
			return showDialogRequired;
/**
		 * Rev 1.1 changes end here
		 */
		// If the parameter isn't only one of each discount type allowed,
		// then the warning dialog that a discount has been applied
		// hasn't been displayed yet.
		//System.out.println("pm================="+pm.toString());
		//System.out.println("logger================="+logger.toString());
		if (!isOnlyOneDiscountAllowed(pm, logger)) {
			TransactionDiscountStrategyIfc discount = cargo.getDiscount();
			/**
		 * Rev 1.1 changes start here
		 */
		if ((discount != null) && (discount.getReasonCode() == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NatZPctoffTiered)) // check
				discount = null;
				/**
		 * Rev 1.1 changes end here
		 */
		
			showDialogRequired = (discount != null);
					}
		// For transaction discounts, there may be items not selected that have
		// item discounts, we need to check if those should be overridden.
		// CheckOneDiscountSite
		// would not have caught this, because it only checks for selected
		// items.
		else if (cargo.getTransaction() != null) {
			
			// For transaction discounts, all items on the transaction must
			// be checked for discounts, not just the selected item.
			AbstractTransactionLineItemIfc[] lineItems = cargo.getTransaction().getLineItems();
			
			for (int i = 0; i < lineItems.length; i++) {
				if (lineItems[i] instanceof SaleReturnLineItemIfc) {
					ItemDiscountStrategyIfc[] discounts = ((SaleReturnLineItemIfc) lineItems[i]).getItemPrice().getItemDiscounts();
					if (discounts != null && discounts.length > 0) {
						showDialogRequired = true;
						break;
					}
				} else {
					logger.warn("Could not check for discounts already applied, for items of type " + lineItems[i].getClass().getName());
				}
			}

		}
		System.out.println("showDialogRequired 218=========== "+showDialogRequired);
		return showDialogRequired;
	}


}
