/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 * Rev  	1.0  	21 Dec, 2016              Ashish Yadav             Intial Credit Card FES
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.tender.creditdebit;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import max.retail.stores.pos.services.edc.CallingOnlineDebitCardTender;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXCreditTenderExpDateUISite extends PosSiteActionAdapter {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void arrive(BusIfc bus) {
		
		HashMap responseMap = null;
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		BigDecimal amount = new BigDecimal(cargo.getTenderAttributes().get("AMOUNT").toString());
		String letter = bus.getCurrentLetter().getName();
		CallingOnlineDebitCardTender edcObj = new CallingOnlineDebitCardTender();
		if (cargo.getTenderAttributes().get(TenderConstants.MSR_MODEL) == null) {
			if (("CreditDebit").equals(letter) || ("EMI").equals(letter)) {
				String transactionTime = "2012-07-21T13:55:58.0Z";
				String amountString = amount.multiply(new BigDecimal("100.00")).intValue() + "";
				String invoiceNumber = "123"; // no need in sale
				POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
				DialogBeanModel dialogModel = new DialogBeanModel();
				ui.showScreen(MAXPOSUIManagerIfc.EDC_POST_VOID_SCREEN, dialogModel);
				try {
					if (letter.equals("CreditDebit")) {
						responseMap = edcObj.makePostVoidEDC(cargo.getCurrentTransactionADO().getTransactionID(),
								amountString, invoiceNumber, transactionTime, "0", "0");
					} else if (letter.equals("EMI")) {
						responseMap = edcObj.makePostVoidEDC(cargo.getCurrentTransactionADO().getTransactionID(),
								amountString, invoiceNumber, transactionTime, "0", "14");
					}
					edcObj.printChargeSlipData(bus, responseMap);
					String dateString = "12/2024";
					cargo.getTenderAttributes().put("CARD_TYPE",responseMap.get("SelectedAquirerName"));
					cargo.getTenderAttributes().put("NUMBER", responseMap.get("CardNumber"));
					cargo.getTenderAttributes().put("EXPIRATION_DATE", dateString);
					cargo.setResponseMap(responseMap);
				} catch (Exception e) {
					if (responseMap != null && !("00").equals(responseMap.get("HostResponseCode").toString())) {
						String msgLetter = "SwipeWithOutExpError";
						showDialogBoxMethod(responseMap, bus, msgLetter);
						return;
					}
				}
			}
			if (((letter.equals("CreditDebit") || letter.equals("EMI")) && responseMap != null
					&& responseMap.get("HostResponseCode").toString().equals("00"))
					|| (!letter.equals("CreditDebit") && !letter.equals("EMI"))) {
			try {
				TenderFactoryIfc factory = (TenderFactoryIfc) ADOFactoryComplex.getFactory("factory.tender");
				factory.createTender(cargo.getTenderAttributes());
			} catch (ADOException adoe) {
				adoe.printStackTrace();
			} catch (TenderException e) {
				if (e.getErrorCode() == TenderErrorCodeEnum.INVALID_EXPIRATION_DATE) {
					POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager("UIManager");

					TDOUIIfc tdo = null;
					try {
						tdo = (TDOUIIfc) TDOFactory.create("tdo.tender.CreditExpDate");
					} catch (TDOException tdoe) {
						tdoe.printStackTrace();
					}
					ui.showScreen("CREDIT_EXP_DATE", tdo.buildBeanModel(null));
					return;
				}
			}

				if (cargo.getTenderAttributes().get(TenderConstants.EXPIRATION_DATE) == null) {
					POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
					TDOUIIfc tdo = null;
					try {
						tdo = (TDOUIIfc) TDOFactory.create("tdo.tender.CreditExpDate");
					} catch (TDOException tdoe) {
						tdoe.printStackTrace();
					}
					if (letter.equals("CreditDebit") || letter.equals("EMI")) {
						bus.mail(new Letter("CreditDebit"), BusIfc.CURRENT);
						return;
					} else {
						ui.showScreen(POSUIManagerIfc.CREDIT_EXP_DATE, tdo.buildBeanModel(null));
						return;
					}
				}
			}
		}

		if (responseMap != null && ("00").equals(responseMap.get("HostResponseCode").toString())) {
			if (responseMap != null && ("00").equals(responseMap.get("HostResponseCode").toString())) // Gaurav
			{
				if (letter.equals("CreditDebit") || letter.equals("EMI")) {
					String msgLetter = "SwipeWithOutExp";
					bus.mail(msgLetter, BusIfc.CURRENT);
		}
			} else
		bus.mail(new Letter("Continue"), BusIfc.CURRENT);
		} else {
			String msgLetter = "SwipeWithOutExpError";
			showDialogBoxMethod(responseMap, bus, msgLetter);
		}
	}

	public void depart(BusIfc bus) {
		if (!(bus.getCurrentLetter().getName().equals("Next"))) {
			//return;
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager("UIManager");
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		cargo.getTenderAttributes().put("EXPIRATION_DATE", ui.getInput());
		}
		else if (bus.getCurrentLetter().getName().equals("SwipeWithOutExpError")) {
			MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
			cargo.getTenderAttributes().put(TenderConstants.EXPIRATION_DATE, null);
			cargo.setResponseMap(new HashMap<>());
		}
	}
	public void showDialogBoxMethod(Map responseMap, BusIfc bus, String buttonLetter) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		String msg[] = new String[6];
		dialogModel.setResourceID("RESPONSE_DETAILS");
		msg[0] = "<<--||--:: Please Find The Response Details As Below ::--||-->>";
		msg[1] = "Your Credit/Debit Card has been Swiped";
		msg[2] = " Response Code Returned Is ";
		if (responseMap != null) {
			if (responseMap.get("HostResponseMessage") != null)
				msg[3] = responseMap.get("HostResponseMessage").toString();
		}
		msg[4] = "Press ENTER To Proceed / Using another Tender";
		msg[5] = "::Thanks::";
		dialogModel.setArgs(msg);
		dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, buttonLetter);
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}
}