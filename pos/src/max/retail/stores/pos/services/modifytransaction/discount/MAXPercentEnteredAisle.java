/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *	Rev	1.0 	Nov 30, 2016		Mansi Goel		Changes for Discount Rule FES	
 *
 ********************************************************************************/

package max.retail.stores.pos.services.modifytransaction.discount;

import java.math.BigDecimal;
import java.math.BigInteger;

import max.retail.stores.domain.discount.MAXDiscountRuleConstantsIfc;
import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.TransactionDiscountByPercentageIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.modifytransaction.discount.ModifyTransactionDiscountCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.utility.CheckDigitUtility;


public class MAXPercentEnteredAisle extends PosLaneActionAdapter {
	
	private static final long serialVersionUID = 4000393262299591639L;

	/**
	 * revision number
	 */
	public static final String revisionNumber = "$Revision: /main/16 $";

	/**
	 * constant for parameter name
	 */
	public static final String MAX_DISC_PCT = "MaximumTransactionDiscountAmountPercent";

	/**
	 * resource id for invalid transaction discount dialog
	 */
	protected static final String INVALID_TRANSACTION_DISCOUNT_DIALOG = "InvalidTransactionDiscountPercent";

	/**
	 * constant for error dialog screen
	 */
	public static final String INVALID_REASON_CODE = "InvalidReasonCode";

	/**
	 * Stores the percent and reason code.
	 * 
	 * @param bus
	 *            Service Bus
	 */
	@Override
	public void traverse(BusIfc bus) {
		ModifyTransactionDiscountCargo cargo = (ModifyTransactionDiscountCargo) bus.getCargo();

		if (cargo.getDiscountType() == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NatZPctoffTiered) {

			// Retrieve data from UI model
			POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			DecimalWithReasonBeanModel beanModel = (DecimalWithReasonBeanModel) uiManager
					.getModel(POSUIManagerIfc.TRANS_DISC_PCNT);
			BigDecimal percent = beanModel.getValue();
			cargo.setAccessFunctionID(MAXRoleFunctionIfc.PRICE_DISCOUNT);
			if (percent.toString().length() >= 5) {
				BigDecimal scaleOne = new BigDecimal(1);
				percent = percent.divide(scaleOne, 2);
			}
			TransactionDiscountByPercentageIfc percentDiscount = DomainGateway.getFactory()
					.getTransactionDiscountByPercentageInstance();
			percentDiscount.setDiscountRate(percent);
			percentDiscount.setReasonCode(cargo.getDiscountType());

			// reference this discount in the cargo
			cargo.setAccessFunctionID(MAXRoleFunctionIfc.PRICE_DISCOUNT);
			cargo.getAccessFunctionID();
			cargo.setDiscount(percentDiscount);
			cargo.setDoDiscount(true);
			bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);

		} else {

			String dialog = null;
			UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

			// Retrieve data from UI model
			POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			DecimalWithReasonBeanModel beanModel = (DecimalWithReasonBeanModel) uiManager
					.getModel(POSUIManagerIfc.TRANS_DISC_PCNT);
			BigDecimal percent = beanModel.getValue();
			cargo.setAccessFunctionID(MAXRoleFunctionIfc.PRICE_DISCOUNT);
			cargo.getAccessFunctionID();

			// retrieve the amount discount
			if (percent.toString().length() >= 5) {
				BigDecimal scaleOne = new BigDecimal(1);
				percent = percent.divide(scaleOne, 2);
			}
			String reason = beanModel.getSelectedReasonKey();

			LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
			CodeListIfc rcl = cargo.getLocalizedDiscountPercentReasonCodes();
			// Validate the Reason Code ID Check Digit, Valid Reason Code exists
			CodeEntryIfc reasonEntry = null;
			if (rcl != null) {
				reasonEntry = rcl.findListEntryByCode(reason);
				localizedCode.setCode(reason);
				localizedCode.setText(reasonEntry.getLocalizedText());
			} else {
				localizedCode.setCode(CodeConstantsIfc.CODE_UNDEFINED);
			}

			if (reasonEntry == null || !isValidCheckDigit(utility, reasonEntry.getCode(), bus.getServiceName())) {
				dialog = INVALID_REASON_CODE;
			} else {
				if (isValidDiscount(bus, percent,cargo,uiManager)) {
					// retrieve the reason string
					TransactionDiscountByPercentageIfc percentDiscount = createDiscountStrategy(localizedCode, percent);
					cargo.setAccessFunctionID(MAXRoleFunctionIfc.PRICE_DISCOUNT);

					// reference this discount in the cargo
					cargo.setDiscount(percentDiscount);
					cargo.setDoDiscount(true);
				} else {
					dialog = INVALID_TRANSACTION_DISCOUNT_DIALOG;
				}
			}

			if (dialog == null) {
				bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
			} else if (dialog.equals(INVALID_REASON_CODE)) {
				showInvalidReasonCodeDialog(uiManager);
			} else if (dialog.equals(INVALID_TRANSACTION_DISCOUNT_DIALOG)) {
				ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
				// get maximum disc % allowed parameter
				BigInteger maxTransDiscPct = getMaximumDiscountPercent(pm);

				TransactionDiscountByPercentageIfc percentDiscount = createDiscountStrategy(localizedCode, percent);

				// reference this discount in the cargo
				cargo.setAccessFunctionID(MAXRoleFunctionIfc.PRICE_DISCOUNT);
				cargo.setDiscount(percentDiscount);
				cargo.setDoDiscount(true);

				//String[] msg = { LocaleUtilities.formatNumber(maxTransDiscPct,
					//	LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)) };
			//	displayErrorDialog(bus, "DiscountTransactionPercentage", msg, DialogScreensIfc.CONFIRMATION);
				DialogBeanModel dialogModel = new DialogBeanModel();
	              dialogModel.setResourceID("InvalidTransactionDiscountPercent");
	              dialogModel.setType(DialogScreensIfc.ERROR);
	              uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

			} else {
				logger.error("Unexpected dialog requested in AmountEnteredAisle: " + dialog);
			}
		}
	}

	/**
	 * Validates the discount.
	 * 
	 * @param bus
	 *            The service bus
	 * @param percent
	 *            The percentage of the discount as a Big Decimal
	 * @return boolean return true if valid
	 */
	public boolean isValidDiscount(BusIfc bus, BigDecimal percent,ModifyTransactionDiscountCargo cargo,POSUIManagerIfc uiManager) {
		BigDecimal percentEntered = percent.movePointRight(2);
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		BigDecimal maxTransDiscPct = new BigDecimal(getMaximumDiscountPercent(pm));
		//Added by Vaibhav for % transaction discount thersold
		DecimalWithReasonBeanModel beanModel = (DecimalWithReasonBeanModel) uiManager
				.getModel(POSUIManagerIfc.TRANS_DISC_PCNT);
		 String tranPercParameter=null;
		 BigDecimal response = new BigDecimal("0.00");
		 LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
	        String codeName = null;
	        int   pmValue = 0;
	        String reason = beanModel.getSelectedReasonKey();
	        if (localizedCode != null)
	        {
	            localizedCode = DomainGateway.getFactory().getLocalizedCode();
	            CodeListIfc rcl = cargo.getLocalizedDiscountPercentReasonCodes();
	            CodeEntryIfc reasonEntry = null;
	            
	            if (rcl != null)
	            {
	            	reasonEntry = rcl.findListEntryByCode(reason);
	                codeName=reasonEntry.getCodeName();
	                codeName=codeName.replaceAll("\\s","");
	            }
	        }
	        tranPercParameter=codeName.concat("TranPercValue");
	         ParameterManagerIfc pm2 = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
	       
				try {
					pmValue = pm2.getIntegerValue(tranPercParameter).intValue();
				} catch (ParameterException e) {
					if (logger.isInfoEnabled())
						logger.info("MAXPercentEnteredAisle.isValidDiscount(), cannot find tranPercParameter paremeter.");
				}
				
				response = new BigDecimal(pmValue).setScale(2);
				boolean validdisc=false;
				
				 if(percentEntered.compareTo(maxTransDiscPct) < 1 && percentEntered.compareTo(response) < 1) {
					 validdisc=true;
				 }else {
					 validdisc=false;
				 }
		return validdisc;
	}

	/**
	 * Displays the invalid discount error screen.
	 * 
	 * @param uiManager
	 *            The POSUIManager
	 */
	protected void showInvalidReasonCodeDialog(POSUIManagerIfc uiManager) {
		// display the invalid discount error screen
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID(INVALID_REASON_CODE);
		dialogModel.setType(DialogScreensIfc.ERROR);

		uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	/**
	 * Displays the invalid discount error screen.
	 * 
	 * @param uiManager
	 *            The POSUIManager
	 * @param msg
	 *            The string array representing the arguments for the dialog
	 */
	protected void showInvalidTransactionDiscountDiscountDialog(POSUIManagerIfc uiManager, String[] msg) {
		// display the invalid discount error screen
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID(INVALID_TRANSACTION_DISCOUNT_DIALOG);
		dialogModel.setType(DialogScreensIfc.ERROR);
		dialogModel.setArgs(msg);

		// display dialog
		uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	/**
	 * Creates discount strategy.
	 * 
	 * @param LocalizedCodeIfc
	 *            reason code
	 * @param percent
	 *            The transaction discount percentage
	 * @return ItemDiscountByAmountIfc sgy
	 */
	protected TransactionDiscountByPercentageIfc createDiscountStrategy(LocalizedCodeIfc reasonCode, BigDecimal percent) {

		TransactionDiscountByPercentageIfc percentDiscount = DomainGateway.getFactory()
				.getTransactionDiscountByPercentageInstance();
		percentDiscount.setDiscountRate(percent);
		percentDiscount.setReason(reasonCode);

		return percentDiscount;
	}

	/**
	 * Returns a BigInteger, the maximum discount % allowed from the parameter
	 * file.
	 * 
	 * @param pm
	 *            ParameterManagerIfc reference
	 * @return maximum discount percent allowed as BigInteger
	 */
	private BigInteger getMaximumDiscountPercent(ParameterManagerIfc pm) {
		BigInteger maximum = BigInteger.valueOf(100); // default
		try {
			String s = pm.getStringValue(MAX_DISC_PCT);
			s.trim();
			maximum = new BigInteger(s);
			if (logger.isInfoEnabled())
				logger.info("Parameter read: " + MAX_DISC_PCT + "=[" + maximum + "]");
		} catch (ParameterException e) {
			logger.error("" + Util.throwableToString(e) + "");
		}

		return (maximum);
	}

	/**
	 * Check digit validation.
	 * 
	 * @param utility
	 *            utility manager
	 * @param reasonCodeID
	 *            the reason code ID that needs to be checked
	 * @param serviceName
	 *            service name
	 * @return boolean return true if valid, otherwise return false
	 */
	public static boolean isValidCheckDigit(UtilityManagerIfc utility, String reasonCodeID, String serviceName) {
		boolean isValid = false;
		if (!utility.validateCheckDigit(CheckDigitUtility.CHECK_DIGIT_FUNCTION_REASON_CODE, reasonCodeID)) {
			// If check digit is not configured for reason code, the check digit
			// function will always return true
			if (logger.isInfoEnabled())
				logger.info("Invalid number received. check digit is invalid. Prompting user to re-enter the information ...");
		} else {
			isValid = true;
		}
		return isValid;
	}
	
	protected void displayErrorDialog(BusIfc bus, String name, String[] msg, int dialogType) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID(name);
		dialogModel.setType(dialogType);
		ModifyTransactionDiscountCargo cargo = (ModifyTransactionDiscountCargo) bus.getCargo();
		cargo.setAccessFunctionID(MAXRoleFunctionIfc.PRICE_DISCOUNT);
		if (dialogType == DialogScreensIfc.ERROR) {
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.NO);
		}
		dialogModel.setArgs(msg);

		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}
}
