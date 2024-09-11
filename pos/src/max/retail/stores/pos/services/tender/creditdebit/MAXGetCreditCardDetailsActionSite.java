/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 * Rev  	1.1  	21 Dec, 2016              Ashish Yadav              Credit Card FES
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.tender.creditdebit;

import java.util.HashMap;
import java.util.Vector;

import oracle.retail.stores.domain.utility.EntryMethod;

import max.retail.stores.pos.ado.tender.MAXTenderConstants;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXCreditCardBeanModel;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.tender.AuthorizableTenderIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;


public class MAXGetCreditCardDetailsActionSite extends PosSiteActionAdapter {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public static final String revisionNumber = "$Revision: 1.3 $";
	String cardNumber = "";
	String cardExpiryDate = "";

	// ----------------------------------------------------------------------
	/**
	 * The arrive method displays the screen.
	 * 
	 * @param bus
	 *            BusIfc
	 **/
	// ----------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		// Check first for eventual card already swiped
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		MSRModel msr = (MSRModel) cargo.getTenderAttributes().get(
				TenderConstants.MSR_MODEL);
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		RetailTransactionADOIfc txnADO = (RetailTransactionADOIfc) cargo
				.getCurrentTransactionADO();
		TenderableTransactionIfc txnRDO = (TenderableTransactionIfc) ((ADO) txnADO)
		.toLegacy();
		// store the bank name list a parameter read from application.xml file :
		ParameterManagerIfc pm = (ParameterManagerIfc) bus
		.getManager(ParameterManagerIfc.TYPE);
		MAXCreditCardBeanModel maxCreditCardBeanModel = new MAXCreditCardBeanModel();
		maxCreditCardBeanModel.setCardNumber("");
		maxCreditCardBeanModel.setExpirationDate("");
		 String[]  banksList = null;
		try {
			banksList = pm.getStringValues("CreditDebitOfflineBank");
		} catch (ParameterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        maxCreditCardBeanModel.setBankDes(banksList);
        maxCreditCardBeanModel.setSelectedBank(0);
		if (msr != null) {
			cardNumber = msr.getAccountNumber();
			cardExpiryDate = msr.getExpirationDate();
			StringBuffer expirationDate = new StringBuffer();
			//modified by vaibhav for avoiding client crash in offline credit card tender mode
			if(cardExpiryDate.length() < 4){
				expirationDate.append("12/99");
			}else{
			expirationDate.append(cardExpiryDate.substring(2, 4)).append("/")
					.append(cardExpiryDate.substring(0, 2));
			}
			//end
			logger.info(cardNumber.length() + "  " + cardNumber + "    "
					+ expirationDate.toString());

			cardExpiryDate = expirationDate.toString();
			maxCreditCardBeanModel.setCardSwiped(true);
		} else {

			cardNumber = (String) cargo.getTenderAttributes().get(
					TenderConstants.NUMBER);
			maxCreditCardBeanModel.setCardSwiped(false);
		}
		if (txnRDO != null
				&& (txnRDO.getTransactionType() == TransactionConstantsIfc.TYPE_RETURN && cargo
						.getSubTourLetter().equals("CreditRefund"))) {

			String originalTxnNo = "";
			SaleReturnTransactionIfc saleReturnTrans = (SaleReturnTransactionIfc) txnRDO;
			Vector lineItems = ((SaleReturnTransactionIfc) saleReturnTrans)
					.getItemContainerProxy().getLineItemsVector();

			// Look for a receipt on each return item
			for (int i = 0; i < lineItems.size(); i++) {
				SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc) lineItems
						.get(i);
				ReturnItemIfc item = lineItem.getReturnItem();
				if (item != null) {
					TransactionIDIfc originalID = item
							.getOriginalTransactionID();
					if (originalID != null) {
						originalTxnNo = "" + originalID.getSequenceNumber();
						if (!originalTxnNo.equals("")) {
							break;
						}
					}
				}
			}

		}


//		if (maxCreditCardBeanModel.isCardSwiped()) {
//			maxCreditCardBeanModel.setCardNumber(cardNumber.trim());
//			maxCreditCardBeanModel
//					.setExpirationDate((cardExpiryDate.length() > 0) ? cardExpiryDate
//							: "");
			maxCreditCardBeanModel.setAuthCode( (String)cargo.getTenderAttributes().get(TenderConstants.AUTH_CODE));
//
//		}
//		else {
//			maxCreditCardBeanModel.setCardNumber(cardNumber.trim());
			maxCreditCardBeanModel.setExpirationDate((cardExpiryDate.length() > 0) ? cardExpiryDate: "");
			maxCreditCardBeanModel.setAuthCode( (String)cargo.getTenderAttributes().get(TenderConstants.AUTH_CODE));
			String selectedBankStr = "0";
			if (cargo.getTenderAttributes().get("SELECTED_BANK") != null) {
				selectedBankStr = (cargo.getTenderAttributes().get(
				"SELECTED_BANK").toString().equals("")) ? "0" : cargo
						.getTenderAttributes().get("SELECTED_BANK").toString();
			}
	
		cargo.getTenderAttributes().put(MAXTenderConstants.ONLINE_OFFLINE_STATUS,
				MAXTenderConstants.OFFLINE);
		ui.showScreen(MAXPOSUIManagerIfc.ENTER_CREDIT_CARD_DETAILS,
				maxCreditCardBeanModel);

	}

	// ----------------------------------------------------------------------
	/**
	 * The depart method captures the user input.
	 * 
	 * @param bus
	 *            BusIfc
	 **/
	// ----------------------------------------------------------------------
	public void depart(BusIfc bus) {
		LetterIfc ltr = bus.getCurrentLetter();
		String letterName = ltr.getName();
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		HashMap tenderAttributes = cargo.getTenderAttributes();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
		.getManager(UIManagerIfc.TYPE);
		
		if (letterName.equals("Next")) {
			MAXCreditCardBeanModel model = (MAXCreditCardBeanModel) ui
			.getModel(MAXPOSUIManagerIfc.ENTER_CREDIT_CARD_DETAILS);
			String selBank = "";
			selBank = model.getSelectedBankName();
			tenderAttributes.put(TenderConstants.NUMBER, model.getCardNumber());
			tenderAttributes.put(TenderConstants.EXPIRATION_DATE, model
					.getExpirationDate());
			tenderAttributes
			.put(TenderConstants.AUTH_CODE, model.getAuthCode());
			tenderAttributes.put(MAXTenderConstants.BK_ID, selBank);
			tenderAttributes.put(MAXTenderConstants.BANK_NAME, selBank);  //mohan
			tenderAttributes.put(MAXPlutusAuthConstantsIfc.TRANSACTION_ACQ_NAME,
					selBank);
			tenderAttributes.put("CARD_TYPE", selBank);
			tenderAttributes.put(TenderConstants.TENDER_TYPE,
					TenderTypeEnum.CREDIT);

			tenderAttributes.put("AUTH_METHOD",
					AuthorizableTenderIfc.AUTHORIZATION_NETWORK_OFFLINE);

			tenderAttributes.put("AUTH_RESPONSE", "APPROVED");
			tenderAttributes.put("AUTH_RESPONSE_CODE", "APPROVED");
// Changes start for rev 1.1 (Ashish : Credit card)
			tenderAttributes.put("ENTRY_METHOD", EntryMethod.Manual);
			// Changes ends for rev 1.1 (Ashish : Credit card)
			cargo.setTenderAttributes(tenderAttributes);
		}

	}

}
