/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *     Copyright (c) 2010 Lifestyle India Pvt Ltd.    All Rights Reserved.
 *   
 * Rev 1.0  	Dec 08, 2014  	Shavinki/Deepshikha    Resolution for LSIPL-FES:-Multiple Tender using Innoviti  
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import max.retail.stores.domain.tender.MAXTenderChargeIfc;
import max.retail.stores.domain.utility.MAXCodeConstantsIfc;
import max.retail.stores.pos.ado.tender.MAXTenderConstants;
import max.retail.stores.pos.services.edc.CallingOnlineDebitCardTender;
import max.retail.stores.pos.services.edc.pinelab.CallingOnlinePineLabDebitCardTender;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderCreditADO;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
/**
 * This site is used to apply validations on Loyalty amount and Put values for authorization.
*/
public class MAXPineLabLoyaltyTenderLimitSite extends PosSiteActionAdapter
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void arrive(BusIfc bus)
	   {	
		 POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		 TenderCargo cargo = (TenderCargo) bus.getCargo();
		 HashMap tenderAttributes = cargo.getTenderAttributes();
		 POSBaseBeanModel beanModel = (POSBaseBeanModel)uiManager.getModel(MAXPOSUIManagerIfc.LOYALTY_EMI_AMOUNT_SCREEN);
	     PromptAndResponseModel promptAndResponseModel = beanModel.getPromptAndResponseModel();
	     String cardAmount = promptAndResponseModel.getResponseText();
	        
	     tenderAttributes.put(TenderConstants.AMOUNT, cardAmount);
			
		try 
		{
			// invoke tender limit validation
			cargo.getCurrentTransactionADO().validateTenderLimits(tenderAttributes);
		} 
		catch (TenderException te) 
		{
			TenderErrorCodeEnum errorCode = te.getErrorCode();
			if (errorCode == TenderErrorCodeEnum.OVERTENDER_ILLEGAL) 
			{
				displayErrorDialog(uiManager, "OvertenderNotAllowed", null, DialogScreensIfc.ERROR);
				return;
			}
		}
	        
	        String amountString = new BigDecimal(cardAmount).multiply(new BigDecimal("100.00")).intValue() + "";
	        String transactionTime = "2012-07-21T13:55:58.0Z";
			String invoiceNumber = "123"; // no need in sale
	    	HashMap responseMap = null;
	   	    DialogBeanModel dialogModel = new DialogBeanModel();
			PromptAndResponseModel promptModel = new PromptAndResponseModel();	   	
			promptModel.setResponseEditable(false);
			dialogModel.setPromptAndResponseModel(promptModel);
	   	    POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
	 	    ui.showScreen(MAXPOSUIManagerIfc.EDC_POST_VOID_SCREEN, dialogModel);
	        CallingOnlinePineLabDebitCardTender edcObj = new CallingOnlinePineLabDebitCardTender();

			try 
			{
//				responseMap = edcObj.makePostVoidEDC(cargo.getCurrentTransactionADO().getTransactionID(), amountString, invoiceNumber, transactionTime,
//							"0", "5");

				responseMap = edcObj.doSaleTransaction(cargo.getCurrentTransactionADO().getTransactionID(),
						amountString, invoiceNumber, transactionTime, "0", "0",MAXPineLabTransactionConstantsIfc.PLUTUS_POINTS_REDEMPTION_TRANSACTION_TYPE,null);
				
				if (responseMap != null && responseMap.size() > 0)
				{
					if (!("APPROVED").equals(responseMap.get("HostResponse").toString())) 
					{
						showDialogBoxMethod(responseMap, bus, CommonLetterIfc.FAILURE);
						return;
					}
					else if (("APPROVED").equals(responseMap.get("HostResponse").toString()))
					{
						HashMap bankNameStrings = getBankNameStrings(bus);
//						String acqName = responseMap.get("SelectedAquirerName").toString();
						// priya changes start for bank code bug 15886
						String acqName = responseMap.get("SelectedAquirerName").toString();
						// priya changes end for bank code bug 15886
						String bankCode = getBankCodeFromCardType(bankNameStrings, acqName);

					//	String reqBankName = "LTYPT-"+acqName;
					//akhilesh changes for "LTYPT" to L
						String reqBankName = "L-"+acqName;
						
						if (reqBankName.length()>20)
						{
							reqBankName = reqBankName.substring(0,20);
						}
						tenderAttributes.put(MAXTenderConstants.AUTH_CODE, responseMap.get("HostResponseApprovalCode").toString());
						tenderAttributes.put(MAXTenderConstants.BANK_CODE, bankCode);
						tenderAttributes.put(MAXTenderConstants.BANK_NAME, reqBankName);
						tenderAttributes.put("AMOUNT", promptAndResponseModel.getResponseText());
						String dateString = "12/2024";
						tenderAttributes.put("NUMBER",  responseMap.get("CardNumber").toString());
						tenderAttributes.put("EXPIRATION_DATE", dateString);
			
				       
						TenderADOIfc creditTender = null;
						try
						{
							TenderFactoryIfc factory = (TenderFactoryIfc) ADOFactoryComplex.getFactory("factory.tender");
							creditTender = factory.createTender(tenderAttributes);
							if (creditTender instanceof TenderCreditADO) 
							{
								creditTender = setTransactionReentry((TenderCreditADO) creditTender, cargo);
							}
							// attempt to add the tender to the transaction
							cargo.getCurrentTransactionADO().addTender(creditTender);
							cargo.setLineDisplayTender(creditTender);
							MAXTenderChargeIfc tenderCharge = (MAXTenderChargeIfc) creditTender.toLegacy();
							tenderCharge.setCardType(reqBankName);
							tenderCharge.setResponseDate(responseMap);
							creditTender.setTenderAttributes(tenderAttributes);
						} 
						catch (ADOException adoe)
						{
							logger.warn("Could not get TenderFactory: " + adoe.getMessage());
						}
						catch (TenderException e) 
						{
							handleException(e, bus);
							return;
						}
						showDialogBoxMethod(responseMap, bus, CommonLetterIfc.NEXT);
					}// end of else

				}
				else{
					bus.mail(CommonLetterIfc.FAILURE);
				}


			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				bus.mail(CommonLetterIfc.FAILURE);
			}
					
			
	 }
	
	protected void handleException(TenderException e, BusIfc bus) {
		TenderErrorCodeEnum error = e.getErrorCode();

		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

		// clear the tender attributes
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		cargo.getTenderAttributes().remove(TenderConstants.MSR_MODEL);
		cargo.getTenderAttributes().remove(TenderConstants.NUMBER);
		cargo.getTenderAttributes().remove(TenderConstants.EXPIRATION_DATE);
		cargo.setPreTenderMSRModel(null);

		if (error == TenderErrorCodeEnum.UNKNOWN_CARD_TYPE) {
			showUnknownCardDialog(ui);
		} else if (error == TenderErrorCodeEnum.INVALID_CARD_TYPE) {
			showCardTypeNotAcceptedDialog(ui);
		} else if (error == TenderErrorCodeEnum.INVALID_CARD_NUMBER) {
			showInvalidNumberDialog(utility, ui);
		} else if (error == TenderErrorCodeEnum.EXPIRED) {
			showExpiredCardDialog(utility, ui);
		} else if (error == TenderErrorCodeEnum.BAD_MAG_SWIPE) {
			showBadMagStripeDialog(ui);
		} else if (error == TenderErrorCodeEnum.INVALID_TENDER_TYPE) {
			showInvalidTenderTypeDialog(ui);
		}

	}
	
	protected void showUnknownCardDialog(POSUIManagerIfc ui) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("UnknownCreditCard");
		dialogModel.setType(DialogScreensIfc.CONFIRMATION);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, "Loop");
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, CommonLetterIfc.INVALID);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	// --------------------------------------------------------------------------
	/**
	 * Shows the card type not accepted dialog screen.
	 */
	// --------------------------------------------------------------------------
	protected void showCardTypeNotAcceptedDialog(POSUIManagerIfc ui) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("CardTypeNotAccepted");
		dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Loop");
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	// --------------------------------------------------------------------------
	/**
	 * Shows the Invalid Number Error dialog screen.
	 */
	// --------------------------------------------------------------------------
	protected void showInvalidNumberDialog(UtilityManagerIfc utility, POSUIManagerIfc ui) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("InvalidNumberError");
		dialogModel.setType(DialogScreensIfc.ERROR);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Loop");

		String[] args = new String[2];
		args[1] = utility.retrieveDialogText("CreditCard", "Credit Card");
		args[1] += " " + utility.retrieveDialogText("Number", "number");
		Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
		args[0] = args[1].toLowerCase(locale);
		dialogModel.setArgs(args);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	// --------------------------------------------------------------------------
	/**
	 * Shows the expired card dialog screen.
	 */
	// --------------------------------------------------------------------------
	protected void showExpiredCardDialog(UtilityManagerIfc utility, POSUIManagerIfc ui) {
		// set screen args
		String titleTag = "ExpiredCreditCardTitle";
		String cardString = utility.retrieveDialogText("ExpiredCardError.Credit", "credit");

		// Display error message
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("ExpiredCardError");
		dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.INVALID);
		dialogModel.setTitleTag(titleTag);
		String args[] = new String[2];
		args[0] = cardString;
		args[1] = cardString;
		dialogModel.setArgs(args);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	/**
	 * Display dialog indicating bad mag swipe
	 * 
	 * @param utility
	 * @param ui
	 */
	protected void showBadMagStripeDialog(POSUIManagerIfc ui) {
		DialogBeanModel dialogModel = new DialogBeanModel();

		// set model properties
		dialogModel.setResourceID("BadCreditMSRReadError");
		dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Loop");
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}

	// --------------------------------------------------------------------------
	/**
	 * Shows the card type not accepted dialog screen.
	 */ 
	// --------------------------------------------------------------------------
	protected void showInvalidTenderTypeDialog(POSUIManagerIfc ui) {
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("NoHouseAccountPaymentWithHouseAccount");
		dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.INVALID);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}
	
	private TenderADOIfc setTransactionReentry(TenderCreditADO creditTender, TenderCargo cargo) {
		creditTender.setTransactionReentryMode(cargo.getRegister().getWorkstation().isTransReentryMode());
		return creditTender;
	}
	

	public void showDialogBoxMethod(Map responseMap, BusIfc bus, String buttonLetter) 
	{
		DialogBeanModel dialogModel = new DialogBeanModel();
		String msg[] = new String[6];
		dialogModel.setResourceID("RESPONSE_DETAILS");
		msg[0] = "<<--||--:: Please Find The Response Details As Below ::--||-->>";
		msg[1] = "Your Credit/Debit Card has been Swiped";
		msg[2] = " Response Code Returned Is ";
		if(responseMap!=null){
		if(responseMap.get("HostResponseMessage")!=null)
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
	
	
	public HashMap getBankNameStrings(BusIfc bus) 
	{
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
		//CodeListIfc list = utility.getCodeListMap().get(MAXCodeConstantsIfc.CODE_LIST_CREDIT_DEBIT_BANK_CODES);
		 Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
		  String storeID = Gateway.getProperty("application", "StoreID", "");

			CodeListIfc list = utility.getReasonCodes(storeID, MAXCodeConstantsIfc.CODE_LIST_CREDIT_DEBIT_BANK_CODES);
		
		if (list != null) {
			Vector reason = list.getTextEntries(locale);
			Vector codes = list.getKeyEntries();
			HashMap combinedcodes = new HashMap();
			for (int i = 0; i < codes.size(); i++) {

				combinedcodes.put(codes.get(i).toString(), reason.get(i).toString());
			}

			return (combinedcodes);
		}
		return null;
	}

	public String getBankCodeFromCardType(HashMap responseMap, String cardType) {
		String bankCode = null;
		String value = null;
		Object key = null;

		Iterator it = responseMap.keySet().iterator();
		while (it.hasNext()) 
		{
			key = it.next();
			value = (String) responseMap.get(key);
          
		 value= value.replaceAll("\\s+","");
		 cardType= cardType.replaceAll("\\s+","");
		//Rev 1.1 End by Akanksha 
           
			if (cardType.equalsIgnoreCase(value))
				break;
		}

		return (String) key;
	}
	
	// ----------------------------------------------------------------------
	/**
	 * Displays the specified Dialog.
	 * 
	 * @param ui
	 *            UI Manager to handle the IO
	 * @param name
	 *            name of the Error Dialog to display
	 * @param args
	 *            arguments for the dialog screen
	 * @param type
	 *            the dialog type
	 * @*/
	// ----------------------------------------------------------------------
	private void displayErrorDialog(POSUIManagerIfc ui, String name, String[] args, int type) 
	{
		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID(name);
		if (args != null)
		{
			dialogModel.setArgs(args);
		}
		dialogModel.setType(type);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
	}
	
}
