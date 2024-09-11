/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 2015 Lifestyle India Pvt Ltd.    All Rights Reserved.
 *
 * Rev 1.3 Jul 12th 2018	Vidhya Kommareddi
 * REQ: CC Offline CR. 
 *
 * Rev 1.2	Feb 01, 2017	Aakash Gupta
 * Bug Fix: Incorrect screen displayed while using offlinw Credit/ Debit.
 *
 * Rev 1.1  Dec 08, 2014    Shavinki Goyal 		Resolution for max-FES:-Multiple Tender using Innoviti
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import max.retail.stores.pos.services.edc.CallingOnlineDebitCardTender;
import max.retail.stores.pos.services.edc.pinelab.CallingOnlinePineLabDebitCardTender;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Determines whether a screen prompting for Exp Date needs to be displayed
 */
public class MAXPineLabCreditTenderExpDateUISite extends PosSiteActionAdapter {
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 */

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static String readRequestXmlClient() {
		String fileName = "C:\\opt\\innoviti\\requestClient_chk.xml";

		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
			String bufferString = "";
			String lineString = "";
			while ((lineString = reader.readLine()) != null) {
				bufferString += lineString;
			}

			return bufferString;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void arrive(BusIfc bus) {

		HashMap responseMap = null;
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		BigDecimal amount = new BigDecimal(cargo.getTenderAttributes().get("AMOUNT").toString());

		// MAX Change for Rev 1.1: Start
		String letter = bus.getCurrentLetter().getName();
		CallingOnlinePineLabDebitCardTender edcObj = new CallingOnlinePineLabDebitCardTender();
		if (cargo.getTenderAttributes().get(TenderConstants.MSR_MODEL) == null) {
			if (("SwipeCard").equals(letter) || ("EMI").equals(letter)) {
				// System.out.println("Inside Test");

				// Needs to provide request xml
				String transactionTime = "2012-07-21T13:55:58.0Z";
				String amountString = amount.multiply(new BigDecimal("100.00")).intValue() + "";
				String invoiceNumber = "123"; // no need in sale
				POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
				DialogBeanModel dialogModel = new DialogBeanModel();
				ui.showScreen(MAXPOSUIManagerIfc.EDC_POST_VOID_SCREEN, dialogModel);

				try {
					if (letter.equals("SwipeCard")) {
						responseMap = edcObj.doSaleTransaction(cargo.getCurrentTransactionADO().getTransactionID(),
								amountString, invoiceNumber, transactionTime, "0", "0",MAXPineLabTransactionConstantsIfc.PLUTUS_SALE_TRANSACTION_TYPE,null);
					} else if (letter.equals("EMI")) {
						responseMap = edcObj.doSaleTransaction(cargo.getCurrentTransactionADO().getTransactionID(),
								amountString, invoiceNumber, transactionTime, "0", "10",MAXPineLabTransactionConstantsIfc.PLUTUS_EMI_TRANSACTION_TYPE,null);
					}
					
//					*************************************************Need To show error when reponse is null*************************************************************************

					if (responseMap == null || responseMap.isEmpty()) {
						String msgLetter = "SwipeWithOutExpError";
						showDialogBoxMethod(responseMap, bus, msgLetter);
						return;

					}
//					****************************************************************************************************************************
					edcObj.printChargeSlipData(bus, responseMap);
					String dateString = "12/2024";
					cargo.getTenderAttributes().put("NUMBER", responseMap.get("CardNumber"));
					cargo.getTenderAttributes().put("EXPIRATION_DATE", dateString);
					cargo.setResponseMap(responseMap);

				} catch (Exception e) {
					if (responseMap != null && !("APPROVED").equals(responseMap.get("HostResponse").toString())) {
						String msgLetter = "SwipeWithOutExpError";
						showDialogBoxMethod(responseMap, bus, msgLetter);
						return;

					}
				}

			}

			if (((letter.equals("SwipeCard") || letter.equals("EMI")) && responseMap != null
					&& responseMap.get("HostResponse").toString().equals("APPROVED"))
					|| (!letter.equals("SwipeCard") && !letter.equals("EMI"))) {
				try {
					TenderFactoryIfc factory = (TenderFactoryIfc) ADOFactoryComplex.getFactory("factory.tender");
					factory.createTender(cargo.getTenderAttributes());

					// System.out.println(factory.createTender(cargo.getTenderAttributes()));
				} catch (ADOException adoe) {
					adoe.printStackTrace();
				} catch (TenderException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// commented and added as tender exception was never coming and
				// expiry date prompt was not coming by Vaibhav
				/*
				 * catch (TenderException e) { if (((TenderException)
				 * e).getErrorCode() ==
				 * TenderErrorCodeEnum.INVALID_EXPIRATION_DATE) {
				 */
				// prompt for the exp. date
				if (cargo.getTenderAttributes().get(TenderConstants.EXPIRATION_DATE) == null) {
					POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
					// build the bean model
					TDOUIIfc tdo = null;
					try {
						tdo = (TDOUIIfc) TDOFactory.create("tdo.tender.CreditExpDate");
					} catch (TDOException tdoe) {
						// TODO Auto-generated catch block
						tdoe.printStackTrace();
					}
					if (letter.equals("SwipeCard") || letter.equals("EMI")) {
						bus.mail(new Letter("SwipeCard"), BusIfc.CURRENT);
						return;
					} else {	
						//Rev 1.3 start
						bus.mail(new Letter("Next"), BusIfc.CURRENT);
						//ui.showScreen(POSUIManagerIfc.CREDIT_EXP_DATE, tdo.buildBeanModel(null));	
						//Rev 1.3 end
						return;
					}
				}
			}
		}
		// }// end of tender creation if

		// }

		if (responseMap != null && ("APPROVED").equals(responseMap.get("HostResponse").toString())) {
			// this will only get mailed if we don't need to prompt for an exp.
			// date
			if (responseMap != null && ("APPROVED").equals(responseMap.get("HostResponse").toString())) // Gaurav
			{
				if (letter.equals("SwipeCard") || letter.equals("EMI")) {
					String msgLetter = "SwipeWithOutExp";
					showDialogBoxMethod(responseMap, bus, msgLetter);

				}
				// bus.mail(new Letter("SwipeWithOutExp"), BusIfc.CURRENT);
			} else
				bus.mail(new Letter("Continue"), BusIfc.CURRENT);
		}
		// MAX Change for Rev 1.1: End
		else {
			String msgLetter = "SwipeWithOutExpError";
			showDialogBoxMethod(responseMap, bus, msgLetter);
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
			if (responseMap.get("HostResponse") != null)
				msg[3] = responseMap.get("HostResponse").toString();
		}
		msg[4] = "Press ENTER To Proceed / Using another Tender";
		msg[5] = "::Thanks::";
		dialogModel.setArgs(msg);
		dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, buttonLetter);
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	@Override
	public void depart(BusIfc bus) {
		if (bus.getCurrentLetter().getName().equals("Next")) {
			// Get information from UI
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			TenderCargo cargo = (TenderCargo) bus.getCargo();
			cargo.getTenderAttributes().put(TenderConstants.EXPIRATION_DATE, ui.getInput());
		}

		// Change for Rev 1.2: Starts
		else if (bus.getCurrentLetter().getName().equals("SwipeWithOutExpError")) {
			MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
			cargo.getTenderAttributes().put(TenderConstants.EXPIRATION_DATE, null);
			cargo.setResponseMap(new HashMap<>());
		}
		// Change for Rev 1.2:Ends
	}

}
