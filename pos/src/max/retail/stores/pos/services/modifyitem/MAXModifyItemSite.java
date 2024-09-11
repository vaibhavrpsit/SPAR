/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *  Rev 1.2     May 04, 2017		Kritica Agarwal GST Changes
 *	Rev 1.1		Apr 29, 2017		Mansi Goel		Changes to disable Gift Registry button
 *	Rev	1.0 	Oct 13, 2016		Ashish Yadav	Initial Revision : Code Merging	
 *
 ********************************************************************************/


package max.retail.stores.pos.services.modifyitem;

import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.GiftCertificateItemIfc;
import oracle.retail.stores.domain.stock.ItemClassificationIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.DataManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.DispatcherIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;
import oracle.retail.stores.pos.services.modifyitem.ModifyItemSite;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//--------------------------------------------------------------------------
/**
 * This site displays the main menu for the Modify Item service.
 * <p>
 * 
 * @version $Revision: 5$
 **/
// --------------------------------------------------------------------------
public class MAXModifyItemSite extends ModifyItemSite {
	
	// Changes starts for code merging(adding below variables as thet are not present in base 14)
	public static final String ACTION_DISCOUNT_AMOUNT = "DiscountAmount";
	public static final String ACTION_PRICE_OVERRIDE = "PriceOverride";
	public static final String ACTION_DISCOUNT_PERCENT = "DiscountPercent";
	public static final String ACTION_OVERRIDE = "Override";
	protected boolean taxInclusiveFlag = Gateway.getBooleanProperty("application", "InclusiveTaxEnabled", false);
	private boolean isTransactionLevelSendAssigned= false;
	// Changes ends for code merging

	public void arrive(BusIfc bus) {
		ItemCargo cargo = (ItemCargo) bus.getCargo();
		POSBaseBeanModel pModel = new POSBaseBeanModel();
		// *****************************izhar
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		UtilityIfc utility;
		// String letter = "";
		boolean offline = false;
		boolean showDialog = false;
		// ******************************end
		// ******************************end
		// *****************************izhar
		try {
			//Change for Rev 1.2 : Starts
			if(cargo.getTransaction() instanceof MAXSaleReturnTransaction){
				isTransactionLevelSendAssigned=((MAXSaleReturnTransaction)cargo.getTransaction()).isTransactionLevelSendAssigned();
			}
			//Change for Rev 1.2 : Ends
			utility = Utility.createInstance();

			// added by izhar MAX-POS-CASH_THRESHOLD_AMOUNT-FES_v1.1
			offline = isSystemOffline(utility);
			FinancialCountIfc fci = cargo.getRegister().getCurrentTill().getTotals().getCombinedCount().getExpected();
			FinancialCountTenderItemIfc[] fctis = fci.getTenderItems();
			String tillFloat = "0.00";
			for (int i = 0; i < fctis.length; i++) {
				if (fctis[i].getDescription().equalsIgnoreCase("CASH")) {
					tillFloat = fctis[i].getAmountTotal().toString();
				}

			}

			String limitallowed = utility.getParameterValue("CashThresholdAmount", "50000.00");
			double tf = Double.parseDouble(tillFloat);
			double cta = Double.parseDouble(limitallowed);
			if (tf >= cta)
				showDialog = true;
			// addition ends
			// added by izhar MAX-POS-CASH_THRESHOLD_AMOUNT-FES_v1.1
			if (showDialog && !offline) {

				DialogBeanModel model = new DialogBeanModel();
				model.setResourceID("cashthresholdamounterror");
				model.setType(DialogScreensIfc.ERROR);

				model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "blockitem");
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
			} else {

				int taxMode = TaxIfc.TAX_MODE_STANDARD; // starting default
														// value
				// Initialize the bean model
				SaleReturnLineItemIfc[] lineItemList = cargo.getItems();

				// get tax mode if transaction in progress
				if (cargo.getTransaction() != null) {
					taxMode = cargo.getTransaction().getTransactionTax().getTaxMode();
				}
				ListBeanModel model = getModifyItemBeanModel(lineItemList, cargo.getTransactionType(), taxMode);

				// if transaction level send has been assigned, then disable
				// item level send(as per reqs.)
				// changes starts ofr code merging(commenting below lines as per
				// MAX (updated to 14.1))
				/*
				 * if (cargo.getTransaction() != null &&
				 * cargo.getTransaction().getTransactionTotals()
				 * .isTransactionLevelSendAssigned()) {
				 * model.getLocalButtonBeanModel().setButtonEnabled(
				 * ACTION_SEND, false); }
				 */
				// Changes ends for code merging
				ui.showScreen(POSUIManagerIfc.ITEM_OPTIONS, model);
			}

		} catch (ADOException e) {
			String message = "Configuration problem: could not instantiate UtilityIfc instance";
			logger.error(message, e);
			throw new RuntimeException(message, e);
		}
	}

	// ******************************end

	// added by izhar MAX-POS-CASH_THRESHOLD_AMOUNT-FES_v1.1
	protected boolean isSystemOffline(UtilityIfc utility) {
		DispatcherIfc d = Gateway.getDispatcher();
		DataManagerIfc dm = (DataManagerIfc) d.getManager(DataManagerIfc.TYPE);
		boolean offline = true;
		try {
			if (dm.getTransactionOnline(UtilityManagerIfc.CLOSE_REGISTER_TRANSACTION_NAME)
					|| dm.getTransactionOnline(UtilityManagerIfc.CLOSE_STORE_REGISTER_TRANSACTION_NAME)) {
				offline = false;
			}
		} catch (DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return offline;

	}

	// end
	// Changes starts for code merging(added below method as it is not present
	// in base 14)
	protected ListBeanModel getModifyItemBeanModel(SaleReturnLineItemIfc[] lineItemList, int transType, int taxMode) {
		ListBeanModel model = new ListBeanModel();

		if (lineItemList != null) {
			model.setListModel(lineItemList);
			if (lineItemList.length == 1) {
				// model.setLineItem(lineItemList[0]);
				// model.setItemHighlightFlag(true);
			} else if (lineItemList.length > 1) {
				// model.setItemHighlightFlag(false);
			}
		}
		model.setLocalButtonBeanModel(getNavigationButtonBeanModel(lineItemList, transType, taxMode));

		return model;
	}

	protected NavigationButtonBeanModel getNavigationButtonBeanModel(SaleReturnLineItemIfc[] lineItemList,
			int transType, int taxMode) {
		NavigationButtonBeanModel nbbModel = new NavigationButtonBeanModel();

		// refresh buttons turned off in previous iterations
		nbbModel.setButtonEnabled(ACTION_INQUIRY, true);
		nbbModel.setButtonEnabled(ACTION_SERVICES, true);
		nbbModel.setButtonEnabled(ACTION_GIFT_RECEIPT, true);

		// If there is no item
		if (lineItemList == null) { // turn off everything except Inquiry and
									// Services
			nbbModel.setButtonEnabled(ACTION_QUANTITY, false);
			nbbModel.setButtonEnabled(ACTION_PRICE_OVERRIDE, false);
			nbbModel.setButtonEnabled(ACTION_DISCOUNT_AMOUNT, false);
			nbbModel.setButtonEnabled(ACTION_DISCOUNT_PERCENT, false);
			nbbModel.setButtonEnabled(ACTION_TAX, false);
			nbbModel.setButtonEnabled(ACTION_SALES_ASSOCIATE, false);
			nbbModel.setButtonEnabled(ACTION_GIFT_REGISTRY, false);
			nbbModel.setButtonEnabled(ACTION_SERIAL_NUMBER, false);
			nbbModel.setButtonEnabled(ACTION_GIFT_RECEIPT, false);
			nbbModel.setButtonEnabled(ACTION_SEND, false);
			nbbModel.setButtonEnabled(ACTION_COMPONENTS, false);
			nbbModel.setButtonEnabled(ACTION_ALTERATIONS, false);
		} else {
			if (lineItemList.length > 1) {
				nbbModel.setButtonEnabled(ACTION_INQUIRY, true);
				nbbModel.setButtonEnabled(ACTION_QUANTITY, false);
				nbbModel.setButtonEnabled(ACTION_PRICE_OVERRIDE, false);
				nbbModel.setButtonEnabled(ACTION_DISCOUNT_AMOUNT, false);
				nbbModel.setButtonEnabled(ACTION_DISCOUNT_PERCENT, false);
				nbbModel.setButtonEnabled(ACTION_SALES_ASSOCIATE, true);
				nbbModel.setButtonEnabled(ACTION_GIFT_REGISTRY, false);
				nbbModel.setButtonEnabled(ACTION_TAX, true);
				nbbModel.setButtonEnabled(ACTION_SERVICES, true);
				nbbModel.setButtonEnabled(ACTION_SERIAL_NUMBER, false);
				nbbModel.setButtonEnabled(ACTION_GIFT_RECEIPT, true);
				// When transaction is layaway transaction, disable Send button
				//Change for Rev 1.2 : Starts
				if ((transType == TransactionIfc.TYPE_LAYAWAY_INITIATE)
						|| (transType == TransactionIfc.TYPE_LAYAWAY_PAYMENT)
						|| (transType == TransactionIfc.TYPE_LAYAWAY_COMPLETE)
						|| (transType == TransactionIfc.TYPE_LAYAWAY_DELETE)
						||isTransactionLevelSendAssigned) {
					nbbModel.setButtonEnabled(ACTION_SEND, false);
				} else {
					nbbModel.setButtonEnabled(ACTION_SEND, true);
				}
				//Change for Rev 1.2 : Ends
				nbbModel.setButtonEnabled(ACTION_COMPONENTS, false);
				nbbModel.setButtonEnabled(ACTION_ALTERATIONS, false);
			}
			if (lineItemList.length == 1) {
				// Send button is disabled when transaction is layaway
				// transaction
				if ((transType == TransactionIfc.TYPE_LAYAWAY_INITIATE)
						|| (transType == TransactionIfc.TYPE_LAYAWAY_PAYMENT)
						|| (transType == TransactionIfc.TYPE_LAYAWAY_COMPLETE)
						|| (transType == TransactionIfc.TYPE_LAYAWAY_DELETE)
						||isTransactionLevelSendAssigned) {
					nbbModel.setButtonEnabled(ACTION_SEND, false);
				} else {
					nbbModel.setButtonEnabled(ACTION_SEND, true);
				}

				// Alterations button
				PLUItemIfc pluItem = lineItemList[0].getPLUItem();
				String productGroup = pluItem.getProductGroupID();
				if (productGroup != null && productGroup.equals(ProductGroupConstantsIfc.PRODUCT_GROUP_ALTERATION)) {
					nbbModel.setButtonEnabled(ACTION_ALTERATIONS, !lineItemList[0].isReturnLineItem());
				} else {
					nbbModel.setButtonEnabled(ACTION_ALTERATIONS, false);
				}

				// If the item is from CrossReach,
				if (lineItemList[0] instanceof OrderLineItemIfc) {
					nbbModel.setButtonEnabled(ACTION_QUANTITY, false);
					nbbModel.setButtonEnabled(ACTION_PRICE_OVERRIDE, false);
					nbbModel.setButtonEnabled(ACTION_TAX, false);
					nbbModel.setButtonEnabled(ACTION_SALES_ASSOCIATE, false);
					nbbModel.setButtonEnabled(ACTION_SERIAL_NUMBER, false);

				} else {
					if (lineItemList[0].isKitHeader()) {
						nbbModel.setButtonEnabled(ACTION_QUANTITY, false);
						nbbModel.setButtonEnabled(ACTION_PRICE_OVERRIDE, false);
						nbbModel.setButtonEnabled(ACTION_DISCOUNT_AMOUNT, false);
						nbbModel.setButtonEnabled(ACTION_DISCOUNT_PERCENT, false);
						nbbModel.setButtonEnabled(ACTION_TAX, false);
						nbbModel.setButtonEnabled(ACTION_SERVICES, false);
						nbbModel.setButtonEnabled(ACTION_SERIAL_NUMBER, false);
						nbbModel.setButtonEnabled(ACTION_SALES_ASSOCIATE, true);
						//Changes for Rev 1.1 : Starts
						nbbModel.setButtonEnabled(ACTION_GIFT_REGISTRY, false);
						//Changes for Rev 1.1 : Ends
						// add these for 5.0
						nbbModel.setButtonEnabled(ACTION_GIFT_RECEIPT, true);
						// nbbModel.setButtonEnabled(ACTION_SEND, true);
						nbbModel.setButtonEnabled(ACTION_COMPONENTS, true);

					} else {
						// Set the actions individually based on the item
						// attributes
						ItemClassificationIfc item = pluItem.getItemClassification();

						// If retrieved return, item is not elig for discount.
						// If item discount elig flag set to false, then not
						// elig for discount.
						boolean discElig = isDiscountEligible(lineItemList[0]);

						nbbModel.setButtonEnabled(ACTION_SALES_ASSOCIATE, !isRetrievedReturnItem(lineItemList[0]));

						boolean isRetrievedReturnItem = isRetrievedReturnItem(lineItemList[0]);
						nbbModel.setButtonEnabled(ACTION_TAX, !isRetrievedReturnItem);
						ReturnItemIfc returnItem = lineItemList[0].getReturnItem();

						if (isRetrievedReturnItem && returnItem != null) {
							if (!returnItem.isFromRetrievedTransaction()) {
								nbbModel.setButtonEnabled(ACTION_TAX, true); // manual
																				// return
							}
						}

						boolean quantityModifiable = (Util.isEmpty(lineItemList[0].getItemSerial())
								&& item.isQuantityModifiable());
						nbbModel.setButtonEnabled(ACTION_QUANTITY, quantityModifiable);

						if (isRetrievedReturnItem(lineItemList[0])) {
							nbbModel.setButtonEnabled(ACTION_PRICE_OVERRIDE, false);
						} else {
							nbbModel.setButtonEnabled(ACTION_PRICE_OVERRIDE, item.isPriceOverridable());
						}

						nbbModel.setButtonEnabled(ACTION_DISCOUNT_AMOUNT, discElig);
						nbbModel.setButtonEnabled(ACTION_DISCOUNT_PERCENT, discElig);
						//Changes for Rev 1.1 : Starts
						nbbModel.setButtonEnabled(ACTION_GIFT_REGISTRY, false);
						//Changes for Rev 1.1 : Ends
						nbbModel.setButtonEnabled(ACTION_OVERRIDE, lineItemList[0].getItemTax().getTaxToggle());
						boolean isSerializable = (Util.isObjectEqual(lineItemList[0].getItemQuantityDecimal(),
								Util.I_BIG_DECIMAL_ONE));

						nbbModel.setButtonEnabled(ACTION_SERIAL_NUMBER, isSerializable);
						nbbModel.setButtonEnabled(ACTION_COMPONENTS, false);
					}
				} // not order line item

				if (lineItemList[0].isReturnLineItem()) {
					// turn off gift receipt if a return item
					nbbModel.setButtonEnabled(ACTION_GIFT_RECEIPT, false);
					if (isRetrievedReturnItem(lineItemList[0])) {
						nbbModel.setButtonEnabled(ACTION_QUANTITY, false);
						nbbModel.setButtonEnabled(ACTION_GIFT_REGISTRY, false);
					}
					boolean isSerializable = (Util.isObjectEqual(lineItemList[0].getItemQuantityDecimal(),
							Util.I_BIG_DECIMAL_ONE.negate()));

					nbbModel.setButtonEnabled(ACTION_SERIAL_NUMBER, isSerializable);
				}
				// Tax cannot be modified for GiftCard.
				if (lineItemList[0].getPLUItem() instanceof GiftCardPLUItemIfc
						|| lineItemList[0].getPLUItem() instanceof GiftCertificateItemIfc) {
					nbbModel.setButtonEnabled(ACTION_TAX, false);
					// need to add this even though it is stored in the db with
					// the item number
					// if the certificate is add through the menu and not an
					// item number it will not have a
					// FIELD_ITEM_QUANTITY_KEY_PROHIBIT_FLAG
					nbbModel.setButtonEnabled(ACTION_QUANTITY, false);
					nbbModel.setButtonEnabled(ACTION_SERIAL_NUMBER, false);

				}
				// Item tax cannot be modified if transaction is tax exempt
				if (taxMode == TaxIfc.TAX_MODE_EXEMPT) {
					nbbModel.setButtonEnabled(ACTION_TAX, false);
				}
				//Change for Rev 1.2 :Starts
				// Send button
				if ((transType == TransactionIfc.TYPE_LAYAWAY_INITIATE)
						|| (transType == TransactionIfc.TYPE_LAYAWAY_PAYMENT)
						|| (transType == TransactionIfc.TYPE_LAYAWAY_COMPLETE)
						|| (transType == TransactionIfc.TYPE_LAYAWAY_DELETE)
						||isTransactionLevelSendAssigned) {
					//Change for Rev 1.2 :Ends
					nbbModel.setButtonEnabled(ACTION_SEND, false);
				} else {
					nbbModel.setButtonEnabled(ACTION_SEND, true);
				}

			} // 1 line item highlighted
		} // line item(s)

		// turn off Send, Gift Receipt, Serial Number if special order in
		// progress
		if (transType == TransactionIfc.TYPE_ORDER_INITIATE) {
			nbbModel.setButtonEnabled(ACTION_SEND, false);
			nbbModel.setButtonEnabled(ACTION_GIFT_RECEIPT, false);
			nbbModel.setButtonEnabled(ACTION_SERIAL_NUMBER, false);
		}

		// If InclusiveTaxEnabled is true, then disable the "TAX" buttons for
		// VAT.
		if (this.taxInclusiveFlag) {
			nbbModel.setButtonEnabled(ACTION_TAX, false);
		}

		return nbbModel;
	}
	// Changes ends for code merging
}
