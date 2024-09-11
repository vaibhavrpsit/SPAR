/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 
 *
 *  Rev 1.6     May 04, 2017		Kritica Agarwal     GST Changes
 *	Rev 1.5		Apr 29, 2017		Mansi Goel			Changes to resolve item not authorized prompt is coming during 
 *														suspend retrieve if last item scanned is not authorized for sale
 * 	Rev 1.4     Feb 27, 2017       	Nitika				Fix for Advance search item, searh by description, select the item and press on details , press on add butten , 
 *                                                		it will ask for qty, (KG Based Item) Application hanged * 
 * 	Rev 1.3  	Nov 08, 2016       	Nadia				MAX-StoreCredi_Return requirement.
 * 	Rev 1.2		Oct 28, 2016		Nadia				Code Merging for base version 14 with Max file.
 * 	Rev 1.1		Oct 27, 2016		Nadia				Fix for Cashier and Sales Assoc not coming on Sell Item Screen * 
 * 	Rev 1.0		Sep 13, 2016		Ashish Yadav		Changes done for code merging	
 *	 Changes to capture ManagerOverride for Reporting purpose
 ********************************************************************************/

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAX, Inc.    All Rights Reserved.
  
 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.sale;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import max.retail.stores.domain.arts.MAXConfigParameterTransaction;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.customer.MAXTICCustomer;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.domain.transaction.MAXTransactionTotalsIfc;
import max.retail.stores.domain.utility.MAXConfigParametersIfc;
import max.retail.stores.pos.device.MAXPOSDeviceActions;
import max.retail.stores.pos.manager.ifc.MAXUtilityManagerIfc;
import max.retail.stores.pos.services.common.MAXRoundingConstantsIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.comparators.Comparators;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.financial.ShippingMethodIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItem;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.ItemConstantsIfc;
import oracle.retail.stores.domain.stock.ItemKitConstantsIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.dualdisplay.DualDisplayManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.sale.ShowSaleScreenSite;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DualDisplayFrame;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;
import oracle.retail.stores.pos.ui.beans.TotalsBeanModel;
import oracle.retail.stores.pos.ui.timer.DefaultTimerModel;

public class MAXShowSaleScreenSite extends ShowSaleScreenSite {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1214139692820002115L;
	/**
	 * Constant for quantity button action name
	 */
	public static final String QUANTITY_ACTION = "Quantity";
	// Rev 1.1 changes
	public static final String PRICING = "Pricing";

	/**
	 * Constant for Apply best deal button action name
	 */
	public static final String APPLY_BEST_DEAL_ACTION = "ApplyDiscounts";

	/**
	 * This timestamp is recorded when this site leaves. When this site arrives
	 * again, the current timestamp is compared against this one for a duration.
	 * This will effectively denote how much time it takes to scan an item.
	 */
	private long debugRoundtripTimestamp;

	// changes starts for cod emerging(adding below variables as they are not
	// present in base 14 of file showsalescreen)
	protected String StrRounding;
	protected List roundingDenominations;
	public static final String REDEEM = "Redeem";
	public static final String CUSTOMER_ACTION = "Customer";
	public static final String UNDO_ACTION = "Undo";
	public static final String CANCEL_ACTION = "Cancel";
	public static final String NOSALE_ACTION = "NoSale";
	public static final String TENDER_ACTION = "Tender";
	public static final String RETURN_ACTION = "Return";
	public static final String TILLFUNCTIONS_ACTION = "TillFunctionsContinue";
	public static final String REPRINT_RECEIPT_ACTION = "ReprintReceipt";
	public static final String SPECIAL_ORDER_ACTION = "SpecialOrder";
	public static final String HOUSEACCOUNT_ACTION = "HouseAccount";
	public static final String TRAINING_ON_OFF_ACTION = "TrainingOnOff";
	
	//Changes Starts by Kamlesh Pant for SpecialEmpDiscount
	public static final String EMPLOYEE_ID = "EmployeeID";
	// Chnages ends for code merging

	// ----------------------------------------------------------------------
	/**
	 * Displays the SELL_ITEM screen.
	 * <P>
	 *
	 * @param bus
	 *            Service Bus
	 */
	// ----------------------------------------------------------------------
	public void arrive(BusIfc bus) {

		// grab the transaction (if it exists)
		// Changes start for cod merging(change SaleCargoIfc to MAXSaleCargoIfc)
		// SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();
		MAXSaleCargoIfc cargo = (MAXSaleCargoIfc) bus.getCargo();
		//System.out.println("141 :"+cargo.getPLUItem().getliqcat());
		//cargo.setPLUItems(cargo.getPLUItems());
		//System.out.println("SaleCargo 141 :"+cargo.getPLUItems().getliqcat());
		//Fix for advance search defect (line item & plu item data does not reset when cancel transaction is done)
		
		//System.out.println("MAXShowSaleScreenSite==========148::"+cargo.getEmpID());
		if(cargo.getTransaction()==null && (cargo.getLineItem()!=null || cargo.getPLUItem()!=null))
		{
			cargo.setEmpID(false);
			cargo.setLineItem(null);
			cargo.setInterStateDelivery(false);
			cargo.setFromRegion(null);
			cargo.setToRegion(null);
			//Changes for Rev 1.5 : Starts
			//cargo.setPLUItem(null);
			//Changes for Rev 1.5 : Ends			
		}
		
		//Change for Rev 1.6 : Starts
		if(cargo.getTransaction()!= null && cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc ){
			cargo.setInterStateDelivery(((MAXSaleReturnTransactionIfc)cargo.getTransaction()).isIgstApplicable());
			cargo.setToRegion(((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getToState());
			cargo.setFromRegion(((MAXSaleReturnTransactionIfc)cargo.getTransaction()).getHomeState());
		}
		//Change for Rev 1.6 : Ends
		// Changes ends for code merging
		// get device actions for linedisplay and msr controls
		MAXPOSDeviceActions pda = new MAXPOSDeviceActions((SessionBusIfc) bus);
		//System.out.println("221");
		/*
		 * 
		 * 
		 * move this to correct place or this is fine also lets check 
		 */
		//chnages by Anuj Singh
		
		if(cargo.getTransaction() != null && (cargo.getTransaction().isExchange() || cargo.getTransaction().isReturn())){
			
			if(cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc ) {
				SaleReturnTransactionIfc[] transactions = cargo.getOriginalReturnTransactions();
				for(SaleReturnTransactionIfc transaction: transactions) {
					String gstNumber = ((MAXSaleReturnTransactionIfc)transaction).getGSTINNumber();
					if(gstNumber != null && !gstNumber.equalsIgnoreCase("")) {
						SaleReturnTransactionIfc tran = cargo.getTransaction();
						((MAXSaleReturnTransactionIfc)tran).setGSTINNumber(gstNumber);
						((MAXSaleReturnTransactionIfc)tran).setGstinresp(((MAXSaleReturnTransactionIfc)transaction).getGstinresp());
					}
					
				}
			}
			
		}

		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		try {
			// changes starts for code merging(commenting below line as chaging
			// this to max )
			// StrRounding = pm.getStringValue(RoundingConstantsIfc.ROUNDING);
			StrRounding = pm.getStringValue(MAXRoundingConstantsIfc.ROUNDING);
			// Changes ends for code merging
			cargo.setRounding(StrRounding);
			String[] roundingDenominationsArray = pm.getStringValues(MAXRoundingConstantsIfc.ROUNDING_DENOMINATIONS);

			if (roundingDenominationsArray == null || roundingDenominationsArray.length == 0) {
				throw new ParameterException("List of parameters undefined");
			}
			roundingDenominations = new ArrayList();
			roundingDenominations.add(0, new BigDecimal(0.0));
			for (int i = 0; i < roundingDenominationsArray.length; i++) {
				roundingDenominations.add(new BigDecimal(roundingDenominationsArray[i]));
			}
			roundingDenominations.add(roundingDenominationsArray.length, new BigDecimal(1.00));

			// List must be sorted before setting on the cargo.
			Collections.sort(roundingDenominations, new Comparator() {
				public int compare(Object o1, Object o2) {
					BigDecimal denomination1 = (BigDecimal) o1;
					BigDecimal denomination2 = (BigDecimal) o2;
					return denomination1.compareTo(denomination2);
				}
			});

			cargo.setRoundingDenominations(roundingDenominations);
			// }
		} catch (ParameterException pe) {
			// if there is an error with the parameters, the price rounding
			// logic should be disabled
			// cargo.setRoundingEnabledLogic(false);
			logger.error("" + Util.throwableToString(pe) + "");
		}

		dualDisplayEnabled = Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP_NAME, DualDisplayFrame.DUALDISPLAY_ENABLED, false);

		UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
		boolean imeiEnabled = utility.getIMEIProperty();
		boolean serializationEnabled = utility.getSerialisationProperty();
		String imeiResponseFieldLength = utility.getIMEIFieldLengthProperty();

		// Setup bean models information for the UI to display
		LineItemsModel beanModel = new LineItemsModel();
		// beanModel.setMoveHighlightToTop(true); - CR 4801
		NavigationButtonBeanModel localModel = new NavigationButtonBeanModel();
		beanModel.setLocalButtonBeanModel(localModel);
		NavigationButtonBeanModel globalModel = new NavigationButtonBeanModel();
		beanModel.setGlobalButtonBeanModel(globalModel);
		StatusBeanModel statusModel = new StatusBeanModel();
		beanModel.setStatusBeanModel(statusModel);

		// allow the status model to dynamically compute a negative till balance.
		statusModel.setRegister(cargo.getRegister());
		TotalsBeanModel totalsModel = new TotalsBeanModel();
		beanModel.setTotalsBeanModel(totalsModel);

		MAXSaleReturnTransactionIfc transaction = (MAXSaleReturnTransactionIfc) cargo.getTransaction();
		
		AbstractTransactionLineItemIfc[] lineItems = null;
		ArrayList<AbstractTransactionLineItemIfc> itemList = new ArrayList<AbstractTransactionLineItemIfc>();

		// Check for Cash drawer UNDER Warning
		if (cargo.isCashDrawerUnderWarning())
		{
			statusModel.setCashDrawerWarningRequired(true);
			cargo.setCashDrawerUnderWarning(false);
		}
		// Reset locale to default values
		if (transaction == null || transaction.getCustomer() == null) {
			Locale defaultLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
			LocaleMap.putLocale(LocaleConstantsIfc.RECEIPT, defaultLocale);
			LocaleMap.putLocale(LocaleConstantsIfc.POLE_DISPLAY, defaultLocale);
			LocaleMap.putLocale(LocaleConstantsIfc.DEVICES, defaultLocale);
			UIUtilities.setUILocaleForCustomer(defaultLocale);
		}

		if (transaction != null) {
			// Disable Redeem button when transaction != null
			localModel.setButtonEnabled(REDEEM, false);

			itemList.addAll(Arrays.asList(cargo.getTransaction().getSaleLineItemsExcluding(
					ItemKitConstantsIfc.ITEM_KIT_CODE_COMPONENT | ItemConstantsIfc.ITEM_PRICEADJ_COMPONENT)));

			itemList.addAll(Arrays.asList(cargo.getTransaction().getReturnLineItemsExcluding(
					ItemKitConstantsIfc.ITEM_KIT_CODE_HEADER | ItemConstantsIfc.ITEM_PRICEADJ_COMPONENT)));

			itemList.addAll(Arrays.asList(cargo.getTransaction().getPriceAdjustmentLineItemsExcluding(
					ItemKitConstantsIfc.ITEM_KIT_CODE_HEADER | ItemConstantsIfc.ITEM_PRICEADJ_COMPONENT)));

			// sort the line items list by line number
			Collections.sort(itemList, Comparators.lineNumberAscending);
		}
		beanModel.setTimerModel(new DefaultTimerModel(bus, transaction != null));

		if (transaction != null && transaction.getSalesAssociate() != null) {
			if (transaction instanceof SaleReturnTransactionIfc
					&& ((SaleReturnTransactionIfc) transaction).getSalesAssociateModifiedFlag()) {
				for (int i = 0; i < itemList.size(); i++) {
					if (!((AbstractTransactionLineItemIfc) itemList.get(i)).getSalesAssociateModifiedFlag()) {
						((AbstractTransactionLineItemIfc) itemList.get(i)).setSalesAssociateModifiedFlag(true);
					}
				}
				
			}

		}

		lineItems = new AbstractTransactionLineItemIfc[itemList.size()];
		itemList.toArray(lineItems);
		if (lineItems != null && lineItems.length > 0) {
			beanModel.setLineItems(lineItems);
		}
		// Get the rows that may have been selected
		int[] rows = cargo.getIndices();
		if (rows != null) {
			boolean setIndices = true;
			for (int i = 0; i < rows.length; i++) {
				// If the rows previously selected are in range then set them
				if (lineItems.length <= rows[i]) {
					setIndices = false;
				}
			}
			if (setIndices) {
				// beanModel.setSelectedRows(rows);
			}
		}

		// Set the training and customer buttons enabled state based on
		// flags from the cargo. Training Mode is a single toggle on/off
		// button.
		boolean trainingModeOn = cargo.getRegister().getWorkstation().isTrainingMode();

		/* Rev. 1.7 start */
		if ((transaction != null && transaction.getCustomer() != null
				&& transaction.getCustomer() instanceof MAXCustomer
				&& ((MAXCustomer) transaction.getCustomer()).getCustomerType().equalsIgnoreCase("T"))
				|| (transaction != null && transaction.getMAXTICCustomer() != null
				&& transaction.getMAXTICCustomer() instanceof MAXTICCustomer
				&& ((MAXTICCustomer) transaction.getMAXTICCustomer()).getCustomerType()
				.equalsIgnoreCase("T"))) {
			localModel.setButtonEnabled(CUSTOMER_ACTION, false);
			/* Rev. 1.7 end */
		} else {
			localModel.setButtonEnabled(CUSTOMER_ACTION, cargo.isCustomerEnabled());
		}

		// If training mode is turned on, then put Training Mode
		// indication in status panel. Otherwise, return status
		// to online/offline status.
		statusModel.setStatus(POSUIManagerIfc.TRAINING_MODE_STATUS, trainingModeOn);

		// Set the undo, cancel and tender buttons enabled state based on
		// transaction from cargo.
		TotalsBeanModel tbm = new TotalsBeanModel();

		if (transaction == null) {
			// initialize lineitem list on cargo
			cargo.setLineItems(null);
			globalModel.setButtonEnabled(UNDO_ACTION, true);
			localModel.setButtonEnabled(NOSALE_ACTION, true);
			globalModel.setButtonEnabled(CANCEL_ACTION, false);
			localModel.setButtonEnabled(TENDER_ACTION, false);
			localModel.setButtonEnabled(APPLY_BEST_DEAL_ACTION, false);// Changes
			// by
			// Sakshi
			localModel.setButtonEnabled(HOUSEACCOUNT_ACTION, false);
			localModel.setButtonEnabled(TILLFUNCTIONS_ACTION, true);
			localModel.setButtonEnabled(RETURN_ACTION, true);
			localModel.setButtonEnabled(REPRINT_RECEIPT_ACTION, true);
			localModel.setButtonEnabled(SPECIAL_ORDER_ACTION, false); // Rev 1.6
			localModel.setButtonEnabled(REDEEM, false);
			localModel.setButtonEnabled(QUANTITY_ACTION, false);
			localModel.setButtonEnabled(PRICING, false); // Rev 1.2 changes
			
			//Changes Starts by Kamlesh Pant for SpecialEmpDiscount
			MAXConfigParametersIfc configParam = getAllConfigparameter();
			if(configParam.isSpclEmpDisc())
			{
				localModel.setButtonEnabled(EMPLOYEE_ID, true);
			}
			else
			{
				localModel.setButtonEnabled(EMPLOYEE_ID, false);
			}
			//SpclEMpDisc Changes End
			
			// Change for capturing ManagerOverride start
			MAXUtilityManagerIfc MAXUtilityManager = (MAXUtilityManagerIfc) bus
					.getManager("UtilityManager");
			MAXUtilityManager.setManagerOverrideMap(new HashMap());
			// Change for capturing ManagerOverride end
			boolean retrieveValueForSuspendedTransactionOnsaleScreen = retrieveValueForSuspendedTransactionOnSaleScreen(bus);
			cargo.setRetrieveSuspendedTransactionOnSaleScreen(retrieveValueForSuspendedTransactionOnsaleScreen);
			if(retrieveValueForSuspendedTransactionOnsaleScreen==true)
			{
				//System.out.println("MAXShowSaleScreen 350 :"+cargo);
			}
		} else {
			//Change for Rev 1.6 : Starts
			transaction.setGstEnable(Gateway.getBooleanProperty("application",
					"GSTEnabled", true));
			//Change for Rev 1.6 : Ends
			localModel.setButtonEnabled(QUANTITY_ACTION, true);
			// changes done for code merging(commenting below line and changing
			// TransactionTotalsIfc to MaxTransactionTotalsIfc)
			// TransactionTotalsIfc totals = transaction.getTransactionTotals();
			MAXTransactionTotalsIfc totals = (MAXTransactionTotalsIfc) transaction.getTransactionTotals();
			// changes ends for code merging
			//System.out.println("cargo.getEmployeeID()=============400: "+cargo.getTransaction().getEmployeeDiscountID());
			if(cargo.getTransaction().getEmployeeDiscountID()!=null)
			{
				localModel.setButtonEnabled(EMPLOYEE_ID, false);
			}
			if (lineItems != null && lineItems.length > 0) {
				if (totals.isTransactionLevelSendAssigned()) {
					if (transaction.hasSendItems()) {
						// enabled only when there is at least one send item
						// in transaction level send (as per reqs.)
						localModel.setButtonEnabled(TENDER_ACTION, true);
						if (transaction.getTransactionType() != 2)
							localModel.setButtonEnabled(APPLY_BEST_DEAL_ACTION, true);// Changes
						// by
						// Sakshi
					} else {
						localModel.setButtonEnabled(TENDER_ACTION, false);
						localModel.setButtonEnabled(APPLY_BEST_DEAL_ACTION, false); // Changes
						// by
						// Sakshi
					}
				} else {
					localModel.setButtonEnabled(TENDER_ACTION, true);
					if (transaction.getTransactionType() != 2)
						localModel.setButtonEnabled(APPLY_BEST_DEAL_ACTION, true); // Chnages
					// BY
					// Sakshi
				}
				beanModel.setSelectedRow(lineItems.length - 1);
				localModel.setButtonEnabled(PRICING, true); // Rev 1.5 changes
				localModel.setButtonEnabled(APPLY_BEST_DEAL_ACTION, true); // Changes
				// by
				// Sakshi
			} else {
				localModel.setButtonEnabled(TENDER_ACTION, false);
				beanModel.setSelectedRow(-1);
				localModel.setButtonEnabled(PRICING, false); // Rev 1.5 changes
				localModel.setButtonEnabled(APPLY_BEST_DEAL_ACTION, false); // Changes
				// by
				// Sakshi
			}
			localModel.setButtonEnabled(TRAINING_ON_OFF_ACTION, false);
			localModel.setButtonEnabled(NOSALE_ACTION, false);
			globalModel.setButtonEnabled(UNDO_ACTION, false);
			globalModel.setButtonEnabled(CANCEL_ACTION, true);
			localModel.setButtonEnabled(HOUSEACCOUNT_ACTION, false);
			localModel.setButtonEnabled(TILLFUNCTIONS_ACTION, false);
			localModel.setButtonEnabled(REPRINT_RECEIPT_ACTION, false);
			localModel.setButtonEnabled(SPECIAL_ORDER_ACTION, false);
			// localModel.setButtonEnabled(PRICING, true); //Rev 1.2 changes
			// //Rev 1.5 commented

			// Check for Shipping Method in Transaction level send
			// Discard all shipping method info if required (as per reqs.)
			if (totals.isTransactionLevelSendAssigned()) {
				// Changes done for cod emerging(commenting below line)
				// ShippingMethodIfc sendShippingMethod =
				// totals.getSendPackages()[0].getShippingMethod();

				ShippingMethodIfc sendShippingMethod = (ShippingMethodIfc) totals.getSendPackages()[0]
						.getShippingMethod();
				CustomerIfc sendCustomer = totals.getSendPackages()[0].getCustomer();
				// ShippingMethodIfc shippingMethod =
				// DomainGateway.getFactory().getShippingMethodInstance();
				ShippingMethodIfc shippingMethod = (ShippingMethodIfc) DomainGateway.getFactory()
						.getShippingMethodInstance();
				// Changes ends ofr code merging
				if (sendShippingMethod != null && !sendShippingMethod.equals(shippingMethod)) {
					transaction.updateSendPackageInfo(0, (oracle.retail.stores.domain.shipping.ShippingMethodIfc) shippingMethod, sendCustomer);
					transaction.updateTransactionTotals(); // Must do this to
					// force tax
					// recalculation
				}
			}
			

			// If there is a transaction, send the transaction totals that
			// can be displayed to the UI.

			// Before display taxTotals, need to convert the longer precision
			// calculated total tax amount back to shorter precision tax total
			// amount for UI display.
			transaction.getTransactionTotals().setTaxTotal(transaction.getTransactionTotals().getTaxTotalUI());

			// Now, display on the UI.
			tbm.setTotals(transaction.getTransactionTotals());

			// Set screen name to Layaway Item if transaction is a layaway
			// disable no sale, customer, and return buttons
			int transType = transaction.getTransactionType();
			if (transType == TransactionIfc.TYPE_LAYAWAY_INITIATE || transType == TransactionIfc.TYPE_ORDER_INITIATE) {
				localModel.setButtonEnabled(NOSALE_ACTION, false);
				localModel.setButtonEnabled(CUSTOMER_ACTION, false);
				localModel.setButtonEnabled(RETURN_ACTION, false);
				localModel.setButtonEnabled(SPECIAL_ORDER_ACTION, false);

				if (transType == TransactionIfc.TYPE_ORDER_INITIATE) {
					String spOrdItem = utility.retrieveText(POSUIManagerIfc.STATUS_SPEC,
							BundleConstantsIfc.POS_BUNDLE_NAME, SP_ORD_ITEM_SCREEN_NAME_TAG,
							SP_ORD_ITEM_SCREEN_NAME_TEXT, LocaleConstantsIfc.USER_INTERFACE);
					statusModel.setScreenName(spOrdItem);
					localModel.setButtonEnabled(TRAINING_ON_OFF_ACTION, false);
				} else {
					String layawayItem = utility.retrieveText(POSUIManagerIfc.STATUS_SPEC,
							BundleConstantsIfc.POS_BUNDLE_NAME, LAYAWAY_ITEM_SCREEN_NAME_TAG,
							LAYAWAY_ITEM_SCREEN_NAME_TEXT, LocaleConstantsIfc.USER_INTERFACE);
					statusModel.setScreenName(layawayItem);
				}
			} else {
				localModel.setButtonEnabled(RETURN_ACTION, true);
			}

			if (transaction.getTransactionTax().getTaxMode() == TaxIfc.TAX_MODE_EXEMPT) {
				localModel.setButtonEnabled(CUSTOMER_ACTION, false);
			}
			
			 
		        // Reset the transaction status to "In Progress" on returning of 'No'
		        // from cancel transaction prompt
		        if (bus.getCurrentLetter() != null && CommonLetterIfc.FAILURE.equals(bus.getCurrentLetter().getName())
		                && transaction.getTransactionStatus() == TransactionConstantsIfc.STATUS_CANCELED
		                && transaction.getPreviousTransactionStatus() == TransactionConstantsIfc.STATUS_IN_PROGRESS)
		        {
		            transaction.setTransactionStatus(TransactionConstantsIfc.STATUS_IN_PROGRESS);
		        }

		        // Disable Redeem button when transaction != null
		        
		        itemList.addAll(Arrays.asList(transaction.getLineItemsExceptExclusions()));
		     // sort the line items list by line number
		        Collections.sort(itemList, Comparators.lineNumberAscending);
			
			if (isLineItemContainReturnableItem(itemList))
	        {
	            for (AbstractTransactionLineItemIfc item : itemList)
	            {
	                SaleReturnLineItemIfc saleReturnItem = (SaleReturnLineItemIfc)item;
	                saleReturnItem.setHasReturnItem(true);
	            }
	        }
	        if (isLineItemContainSendItem(itemList))
	        {
	            for (AbstractTransactionLineItemIfc item : itemList)
	            {
	                SaleReturnLineItemIfc saleReturnItem = (SaleReturnLineItemIfc)item;
	               saleReturnItem.setHasSendItem(true);
	            }
	           
	        }
	        
	        if (transaction.isTransactionLevelSendAssigned())
	        {
	        	oracle.retail.stores.domain.shipping.ShippingMethodIfc sendShippingMethod = transaction.getSendPackages()[0].getShippingMethod();
	            CustomerIfc sendCustomer = transaction.getSendPackages()[0].getCustomer();
	            oracle.retail.stores.domain.shipping.ShippingMethodIfc shippingMethod = DomainGateway.getFactory().getShippingMethodInstance();
	            if (sendShippingMethod != null && !sendShippingMethod.equals(shippingMethod))
	            {
	                transaction.updateSendPackageInfo(0, (oracle.retail.stores.domain.shipping.ShippingMethodIfc) shippingMethod, sendCustomer);
	                // Must do this to force tax recalculation
	                transaction.updateTransactionTotals();
	            }
	        }
	            CustomerIfc customer = (transaction.getCustomer() != null) ? transaction.getCustomer() : (CustomerIfc)transaction.getCaptureCustomer();
	            String customerName = getDisplayCustomerName(customer, utility);
	            statusModel.setCustomerName(customerName);
	       
		}
		// disable special order button if in training mode
		if (cargo.getRegister().getWorkstation().isTrainingMode()) {
			localModel.setButtonEnabled(SPECIAL_ORDER_ACTION, false);
		}
		/** MAX Change Rev. 1.3: Start **/
		if (lineItems == null || lineItems.length == 0) {
			localModel.setButtonEnabled(QUANTITY_ACTION, false);
		}
		/** MAX Change Rev. 1.3: End **/

		/** MAX Change Rev. 1.4: Start **/
		if (transaction != null) {
			if (transaction.getTransactionType() == TransactionConstantsIfc.TYPE_RETURN) {
				if (!isRetrievedReturnItem(transaction))
					localModel.setButtonEnabled(QUANTITY_ACTION, true);
				else
					localModel.setButtonEnabled(QUANTITY_ACTION, false);
			}
		}
		/** MAX Change Rev. 1.4: End **/

		// Set the local button, global button, totals and status bean
		// models on the sale bean model.

		if (transaction != null && transaction.getLineItemsVector() != null
				&& transaction.getLineItemsVector().size() > 0)
			globalModel.setButtonEnabled("ItemVoid", true);
		else
			globalModel.setButtonEnabled("ItemVoid", false);
		// Rev 1.5 start
		localModel.setButtonEnabled(HOUSEACCOUNT_ACTION, false);
		localModel.setButtonEnabled(REDEEM, false);
		// Changes for Rev 1.5
		// Changes for TIC by Gaurav : Start
		if (transaction != null && transaction.getCustomer() != null && transaction.getCustomer() instanceof MAXCustomer
				&& ((MAXCustomer) transaction.getCustomer()).getCustomerType().equalsIgnoreCase("T")) {
			localModel.setButtonEnabled(CUSTOMER_ACTION, false);
		} else {
			cargo.setTicCustomerPhoneNoFlag(false);
		}
		// Changes for TIC by Gaurav End
		beanModel.setTotalsBeanModel(tbm);
		beanModel.setGlobalButtonBeanModel(globalModel);
		beanModel.setLocalButtonBeanModel(localModel);
		beanModel.setStatusBeanModel(statusModel);

		// Side-effect: Allow the status model to dynamically compute a negative
		// till balance.
		statusModel.setRegister(cargo.getRegister());
		/*Changes for Rev 1.0 starts */
		statusModel.setCashierName(cargo.getOperator().getPersonName().getFirstLastName());
		if (transaction == null || transaction.getSalesAssociate() == null)
		{
			try
			{
				boolean defaultToCashier = pm.getBooleanValue(ParameterConstantsIfc.DAILYOPERATIONS_DefaultToCashier);
				if (defaultToCashier)
				{
					boolean identifySaleAssoc = pm.getBooleanValue(ParameterConstantsIfc.DAILYOPERATIONS_IdentifySalesAssociateEveryTransaction);
					if (identifySaleAssoc)
					{
						statusModel.setSalesAssociateName(cargo.getEmployee().getPersonName().getFirstLastName());
					}
					else
					{
						statusModel.setSalesAssociateName(cargo.getOperator().getPersonName().getFirstLastName());
					}
				}
			}
			catch (ParameterException e)
			{
				logger.error(Util.throwableToString(e));
			}
		}
		else
		{
			// Set Sales Associate
			statusModel.setSalesAssociateName(cargo.getEmployee().getPersonName().getFirstLastName());
		}

		/*Changes for Rev 1.0 ends */

		// Side-effect: Show linked customer, if applicable.
		CustomerIfc customer = null;

		/// Changes for Rev 1.7 : Starts

		if (transaction instanceof MAXSaleReturnTransaction) {
			MAXSaleReturnTransaction maxTransaction = (MAXSaleReturnTransaction) transaction;

			if (maxTransaction != null && cargo.isCustomerEnabled() && (customer = maxTransaction.getCustomer()) != null
					&& !maxTransaction.isTicCustomerVisibleFlag()) {
				/// akhilesh changes for tic customer CR END
				String customerName = customer.getFirstLastName();
				if (customerName != null) {
					statusModel.setCustomerName(customerName);
				}
				/// akhilesh changes for tic customer CR start
			} else if (maxTransaction != null && cargo.isCustomerEnabled() && maxTransaction.getMAXTICCustomer() != null
					&& maxTransaction.isTicCustomerVisibleFlag()) {

				if (maxTransaction.getMAXTICCustomer() instanceof MAXTICCustomer) {
					MAXTICCustomer ticCustomer = (MAXTICCustomer) maxTransaction.getMAXTICCustomer();
					statusModel.setCustomerName(ticCustomer.getTICFirstName() + " " + ticCustomer.getTICLastName());

				}
			}
			/// akhilesh changes for tic customer CR ENd
		} else if (transaction != null && cargo.isCustomerEnabled() && (customer = transaction.getCustomer()) != null) {
			String customerName = customer.getFirstLastName();
			if (customerName != null) {
				statusModel.setCustomerName(customerName);
			}
		}

		// Display the screen
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

		// Don't call showScreen() if SELL_ITEM is the current screen
		// because showScreen() will result in resetting the scanner session.
		try {
			if (ui.getActiveScreenID() == POSUIManagerIfc.SELL_ITEM) {
				ui.setModel(POSUIManagerIfc.SELL_ITEM, beanModel);
			} else {
				ui.showScreen(POSUIManagerIfc.SELL_ITEM, beanModel);
				ui.setModel(POSUIManagerIfc.SELL_ITEM, beanModel);
				try {
					LineItemsModel tempBeanModel = (LineItemsModel) ui.getModel(POSUIManagerIfc.SELL_ITEM);
					cargo.setMaxPLUItemIDLength(
							Integer.valueOf("40"));
				} catch (Exception e) {
					logger.warn("ShowSaleScreenSite.arrive() unable to get the maximum PLU item ID length", e);
				}
			}
		} catch (UIException uie) {
			logger.warn("ShowSaleScreenSite.arrive() unable to get the active screen ID");
		}

		// line display part
		setLineDisplay(bus, cargo, transaction, utility, beanModel.getLineItems());

		// cpoi part
		setCPOIDisplay(bus, cargo, transaction, beanModel.getLineItems());

		// activate the msr device if transaction in progress with atleast one
		// item
		try {
			if (lineItems != null && lineItems.length > 0) {
				pda.beginMSRSwipe();
			} else {
				pda.endMSRSwipe();
			}
		} catch (DeviceException e) {
			logger.warn("Unable to use MSR device: " + e.getMessage() + "");
		}
		// set debug timestamp and print to console	
		if (debugRoundtripTimestamp != 0 && logger.isDebugEnabled())
		{
			String message = "Roundtrip to ShowSaleScreen is " + (System.currentTimeMillis() - debugRoundtripTimestamp) + "ms";
			logger.debug(message);
			//System.out.println(message);
		}

		if (dualDisplayEnabled)
		{
			//If dualdisplay is enabled, show sale screen on second display 
			DualDisplayManagerIfc dualDisplayManager = (DualDisplayManagerIfc)bus.getManager(DualDisplayManagerIfc.TYPE);            
			dualDisplayManager.showSaleScreen(DualDisplayManagerIfc.DUALDISPLAY_SELLITEM, beanModel);             
		}
	}

	/** MAX Change Rev. 1.4: Start **/
	protected boolean isRetrievedReturnItem(SaleReturnTransactionIfc transaction) {
		boolean retrievedItem = false;
		Vector lineItemsVector = transaction.getLineItemsVector();

		SaleReturnLineItemIfc lineItem = null;
		for (int i = 0; i < lineItemsVector.size(); i++) {
			if (lineItemsVector.get(i) instanceof SaleReturnLineItem)
				lineItem = (SaleReturnLineItemIfc) lineItemsVector.get(i);
			if (lineItem.isReturnLineItem()) {
				retrievedItem = (lineItem.getReturnItem().getOriginalTransactionID() != null);
				if (!retrievedItem)
					break;
			}
		}
		return (retrievedItem);
	}

	/** MAX Change Rev. 1.4: End **/



	private boolean retrieveValueForSuspendedTransactionOnSaleScreen(BusIfc bus)
	{
		boolean isSupported = false;
		try
		{
			ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
			boolean value = pm.getBooleanValue(ParameterConstantsIfc.SALE_ResumeSuspendedTransactionOnSaleScreen);

			if (value)
			{
				isSupported = true;
			}
		}
		catch (ParameterException e)
		{
			logger.error("Could not determine retrieve Suspended Transaction On Sale Screen setting.", e);
		}

		return isSupported;
	}
	
	//Changes by kamlesh Pant for Special Employee Discount
	private MAXConfigParametersIfc getAllConfigparameter() {

		MAXConfigParameterTransaction configTransaction = new MAXConfigParameterTransaction();
		MAXConfigParametersIfc configParameters = null;
		configTransaction = (MAXConfigParameterTransaction) DataTransactionFactory
				.create(MAXDataTransactionKeys.CONFIG_PARAMETER_TRANSACTION);

		try {
			configParameters = configTransaction.selectConfigParameters();
		} catch (DataException e1) {
			//e1.printStackTrace();
			logger.error(e1.getMessage());
		}
		return configParameters;
	}
}