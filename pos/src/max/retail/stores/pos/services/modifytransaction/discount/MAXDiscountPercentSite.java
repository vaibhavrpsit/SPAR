/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev 1.1     Dec 16, 2016	    Ashish Yadav    Changes for Employee Discount FES
 *	Rev	1.0 	Nov 30, 2016		Mansi Goel		Changes for Discount Rule FES	
 *
 ********************************************************************************/

package max.retail.stores.pos.services.modifytransaction.discount;

import java.math.BigDecimal;
import java.util.Vector;

import max.retail.stores.domain.discount.MAXDiscountRuleConstantsIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.discount.TransactionDiscountByPercentageStrategy;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.modifytransaction.discount.ModifyTransactionDiscountCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel;

public class MAXDiscountPercentSite extends PosSiteActionAdapter {

	private static final long serialVersionUID = 7891552331416129984L;
	/**
	 * The instant credit discount parameter name
	 **/
	public static final String INSTANT_CREDIT_DISCOUNT = "DefaultInstantCreditDiscount";

	public void arrive(BusIfc bus) {

		// model to use for the UI
		DecimalWithReasonBeanModel model = new DecimalWithReasonBeanModel();

		// retrieve cargo
		ModifyTransactionDiscountCargo cargo = (ModifyTransactionDiscountCargo) bus.getCargo();

		// get the POS UI manager
		POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

		// get the discount percent object
		TransactionDiscountByPercentageStrategy percentDiscount = null;

		if (!cargo.getDoDiscount()) {
			cargo.setDoDiscount(false);
			percentDiscount = (TransactionDiscountByPercentageStrategy) cargo.getDiscount();
			// cargo.setDiscount(null);
		} else {
			cargo.setDoDiscount(false);
			cargo.setDiscount(percentDiscount);
		}

		// set the reason codes in the model
		// a list of all the reasons
		// Changes start for Rev 1.0 (Ashish : Employee Discount)
		//CodeListIfc reasons = cargo.getReasonCodes();
		 UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
		CodeListIfc reasons = utility.getReasonCodes(cargo.getOperator().getStoreID(), CodeConstantsIfc.CODE_LIST_TRANSACTION_DISCOUNT_BY_PERCENTAGE);
		cargo.setLocalizedDiscountPercentReasonCodes(reasons);
		// Changes end for Rev 1.1 (Ashish : Employee Discount)
		String defaultEntry = "";
		if (reasons == null) {
			model.setReasonCodes(new Vector());
			model.setReasonCodeKeys(new Vector());
			logger.error("NO Valid Code IDs for Transaction Discount by %!!");
		} else {
			model.setReasonCodes(reasons.getTextEntries(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));
			model.setReasonCodeKeys(reasons.getKeyEntries());
			defaultEntry = reasons.getDefaultOrEmptyString(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
			logger.info("Valid Code IDs for Transaction Discount by % are: " + reasons.getKeyEntries());
		}

		// the string of the selected reason to be shown on the screen
		String selectedEntry = null;
		String reasonString = "";

		// check to see if there is a percenet discount on the transaction
		if ((percentDiscount != null) && (percentDiscount.getReasonCode() != MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NatZPctoffTiered)) // check
																																					// for
		// reasoncode
		// introduced by
		// Gaurav, for
		// invoice type
		// rules
		{
			// if so set the current discount values as default on the model
			model.setValue(percentDiscount.getDiscountRate());
			// retrieve the reason code
			selectedEntry = percentDiscount.getReason().getCode();
		}

		// if Instant Credit, then get parameter & calculate percentage.
		if (cargo.isInstantCreditDiscount()) {
			getPercent(bus, model);
		}

		// set in the model
		model.inject(reasons, selectedEntry, LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));

		// show the screen
		uiManager.showScreen(POSUIManagerIfc.TRANS_DISC_PCNT, model);

	}

	/**
	 * This method will get the parameter and calculate discount percentage
	 * 
	 * @param bus
	 *            Service Bus
	 * @param model
	 *            Model in which percent is displayed.
	 */
	private void getPercent(BusIfc bus, DecimalWithReasonBeanModel model) {
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		try {
			// parameter range: 100% - 0%
			double per = pm.getIntegerValue(ParameterConstantsIfc.HOUSEACCOUNT_DefaultInstantCreditDiscount)
					.doubleValue();
			// convert the value obtained to a rate
			per = per / 100.0;
			BigDecimal value = new BigDecimal(per);
			value = value.setScale(2, BigDecimal.ROUND_HALF_UP); // range: 1.00
																	// - 0.00
			model.setValue(value);
		} catch (ParameterException pe) {
			logger.warn(pe.getStackTraceAsString());
		}
	}
}