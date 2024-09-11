/*
  Rev 1.1	Seema Kumari		17/04/2014		Initial Draft:	Food Totals Button requirement.
  
  */

package max.retail.stores.pos.services.tender;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.StringTokenizer;

import max.retail.stores.pos.ui.beans.MAXTenderBeanModel;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItem;
import oracle.retail.stores.domain.stock.ItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

public class MAXFoodTotalTenderActionSite extends PosSiteActionAdapter {

	private static String FOOD_DEPT = "FoodDeptID";
	public static final String Food_Total_Display = "FoodTotalsDisplay";
	

	public void arrive(BusIfc bus) {

		long[] food_dept_id = null;
		String[] food_dept_id_withName = null;
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
		.getManager(UIManagerIfc.TYPE);
		MAXTenderBeanModel model=(MAXTenderBeanModel) ui.getModel();
		ParameterManagerIfc pm = (ParameterManagerIfc) bus
		.getManager(ParameterManagerIfc.TYPE);
		int barCodeLength = 0;
		try {

			food_dept_id_withName = pm.getStringValues(FOOD_DEPT);

			// food_dept_id = pm.getStringValues(FOOD_DEPT);
		} catch (ParameterException e1) {

			logger.error("" + Util.throwableToString(e1) + "");
		}
		// START Code Added by Mahendra for Parameter Modification

		StringTokenizer st = null;
		food_dept_id = new long[food_dept_id_withName.length];

			for (int i = 0; i < food_dept_id_withName.length; i++) {
				st = new StringTokenizer(food_dept_id_withName[i], "_");
				if (st.hasMoreTokens()) {
					food_dept_id[i] = getParsedLongValue(st.nextToken()
							.toString());
				}

			}

		
		// Sorting the Array
		Arrays.sort(food_dept_id);

		TenderCargo cargo = (TenderCargo) bus.getCargo();
		// Added for the bug#1869 by aarti
		AbstractTransactionLineItemIfc[] lines = null;

		if (cargo.getTransaction() != null
				&& (cargo.getTransaction().getTransactionType() == TransactionIfc.TYPE_SALE) ||(cargo.getTransaction().getTransactionType() == TransactionIfc.TYPE_LAYAWAY_INITIATE)
				||(cargo.getTransaction().getTransactionType() == TransactionIfc.TYPE_LAYAWAY_COMPLETE)||(cargo.getTransaction().getTransactionType() == TransactionIfc.TYPE_LAYAWAY_PAYMENT)) {

			if (cargo.getTransaction() instanceof SaleReturnTransaction) {
				lines = ((SaleReturnTransaction) cargo.getTransaction())
				.getItemContainerProxy().getLineItems();

			}
			CurrencyIfc curr = DomainGateway.getBaseCurrencyInstance();

			for (int i = 0; i < lines.length; i++) {

				ItemIfc item = ((SaleReturnLineItem) lines[i]).getPLUItem()
				.getItem();

				for (int j = 0; j < food_dept_id.length; j++) {
					String food_id = String.valueOf(food_dept_id[j]);
					if (food_id.length() > 9) {

						if (item.getItemClassification()
								.getMerchandiseHierarchyGroup().equals(food_id)) {

							curr = curr.add(((SaleReturnLineItem) lines[i])
									.getExtendedDiscountedSellingPrice());
							break;

						}
					} else {
						if (item.getItemClassification()
								.getMerchandiseHierarchyGroup().startsWith(
										food_id)) {

							curr = curr.add(((SaleReturnLineItem) lines[i])
									.getExtendedDiscountedSellingPrice());
							break;
						}
					}

				}
			}
			
	 
			
			BigDecimal foodTotal =model.getFoodTotal();
			String args[] = { foodTotal.toString() };
			displayDialog(bus, DialogScreensIfc.ACKNOWLEDGEMENT,
					"FoodTotalsDisplay", args, "Continue");
			
			/*
			DialogBeanModel dialogBeanmodel = new DialogBeanModel();
				dialogBeanmodel.setResourceID(Food_Total_Display);
				dialogBeanmodel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
				dialogBeanmodel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Continue");
				dialogBeanmodel.setArgs(args);
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogBeanmodel);*/
			
		}}
	
	private long getParsedLongValue(String foodId) {

		long longFoodId = 0l;

		if (foodId.charAt(3) == '0') {

			longFoodId = Long.parseLong(foodId.subSequence(0, 3).toString());
		} else if (foodId.charAt(6) == '0') {

			longFoodId = Long.parseLong(foodId.subSequence(0, 6).toString());
		} else if (foodId.charAt(9) == '0') {

			longFoodId = Long.parseLong(foodId.subSequence(0, 9).toString());
		} else {

			longFoodId = Long.parseLong(foodId.subSequence(0, 12).toString());
		}

		return longFoodId;
	}

	protected void displayDialog(BusIfc bus, int screenType, String message,
			String[] args, String letter) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
		.getManager(UIManagerIfc.TYPE);
		if (letter != null) {
			UIUtilities.setDialogModel(ui, screenType, message, args, letter);
		} else {
			UIUtilities.setDialogModel(ui, screenType, message, args);
		}
 
}}