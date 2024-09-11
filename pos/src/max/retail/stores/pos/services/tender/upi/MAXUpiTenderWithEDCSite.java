package max.retail.stores.pos.services.tender.upi;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import max.retail.stores.domain.tender.MAXTenderChargeIfc;
//import max.retail.stores.pos.ado.tender.MAXTenderUpiADO;
import max.retail.stores.pos.services.edc.CallingOnlineDebitCardTender;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.JournalableADOIfc;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderCreditADO;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXUpiTenderWithEDCSite extends PosSiteActionAdapter {
private static final long serialVersionUID = 8876180447240798387L;


	public void arrive(BusIfc bus) {

		
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		final String transactionTime = "2020-01-20T13:55:58.0Z";
        final BigDecimal amount = new BigDecimal(cargo.getTenderAttributes().get("AMOUNT").toString());
        final String amountString = amount.multiply(new BigDecimal("100.00")).intValue() + "";
        final String invoiceNumber = "123";
        HashMap responseMap = null;
        final POSBaseBeanModel beanModel = new POSBaseBeanModel();
        ui.showScreen(MAXPOSUIManagerIfc.UPI_PAYMENT_SITE, beanModel);
        final HashMap tenderAttributes = cargo.getTenderAttributes();
        final CallingOnlineDebitCardTender edcObj = new CallingOnlineDebitCardTender();
        try {
            responseMap = edcObj.makePostVoidEDC(cargo.getCurrentTransactionADO().getTransactionID(), amountString, invoiceNumber, transactionTime, "00", "86");
            if (responseMap != null) {
                final String hostResponseCode = (String) responseMap.get("HostResponseCode");
                if (hostResponseCode.equals("00")) {
                    final HashMap bankNameStrings = this.getBankNameStrings(bus);
                    final String acqName = responseMap.get("StateAquirerName").toString();
                   // final String aquirerStatus = responseMap.get("SelectedAquirerStatus").toString();
                    final String bankCode = this.getBankCodeFromCardType(bankNameStrings, acqName);
                    String schemeType = null;
                    if (responseMap.get("SchemeType") != null) {
                        schemeType = responseMap.get("SchemeType").toString();
                    }
                    String reqBankName = acqName;
                    if (reqBankName.startsWith(" _")) {
                        reqBankName = reqBankName.substring(2);
                    }
                    if (reqBankName.length() > 20) {
                        reqBankName = reqBankName.substring(1, 20);
                    }
					
                    tenderAttributes.put("AUTH_CODE", responseMap.get("HostResponseApprovalCode").toString());
                    tenderAttributes.put("BANK_CODE", bankCode);
                    tenderAttributes.put("BANK_NAME", reqBankName);
                    tenderAttributes.put("AMOUNT", tenderAttributes.get("AMOUNT").toString());
                    final String dateString = "12/2024";
                    tenderAttributes.put("NUMBER", responseMap.get("CardNumber").toString());
                    tenderAttributes.put("EXPIRATION_DATE", dateString);
					//Added for HostResponseRetrievelRefNumber in EJ by Vaibhav
					tenderAttributes.put("REF_TRN_ID",responseMap.get("HostResponseRetrievelRefNumber").toString());
					//End
                    TenderADOIfc creditTender = null;
                    try {
                        final TenderFactoryIfc factory = (TenderFactoryIfc)ADOFactoryComplex.getFactory("factory.tender");
                        creditTender = factory.createTender(tenderAttributes);
                        if (creditTender instanceof TenderCreditADO) {
                            creditTender = this.setTransactionReentry((TenderCreditADO)creditTender, cargo);
                        }
                        cargo.getCurrentTransactionADO().addTender(creditTender);
                        cargo.setLineDisplayTender(creditTender);
                        final MAXTenderChargeIfc tenderCharge = (MAXTenderChargeIfc)creditTender.toLegacy();
                        tenderCharge.setCardType(reqBankName);
                        tenderCharge.setResponseDate(responseMap);
                        if (schemeType != null && schemeType.equalsIgnoreCase("UPI") && responseMap.get("HostResponseRetrievelRefNumber") != null) {
                            tenderCharge.setRetrievalRefNumber(responseMap.get("HostResponseRetrievelRefNumber").toString());
                        }
                        creditTender.setTenderAttributes(tenderAttributes);
                        JournalFactoryIfc jrnlFact = null;
                        try {
                            jrnlFact = JournalFactory.getInstance();
                        }
                        catch (ADOException e) {
                        	MAXUpiTenderWithEDCSite.logger.error((Object)"Configuration problem: could not instantiate JournalFactoryIfc instance", (Throwable)e);
                            throw new RuntimeException("Configuration problem: could not instantiate JournalFactoryIfc instance", (Throwable)e);
                        }
                        final RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();
                        registerJournal.journal((JournalableADOIfc)creditTender, JournalFamilyEnum.TENDER, JournalActionEnum.ADD);
                        
                       bus.mail((LetterIfc)new Letter("Next"), BusIfc.CURRENT);
                    }
                    catch (ADOException adoe) {
                    	MAXUpiTenderWithEDCSite.logger.warn((Object)("Could not get TenderFactory: " + adoe.getMessage()));
                    }
                    catch (TenderException e2) {
                        this.handleException(e2, bus);
                    }
                }
                else {
                    this.showDialogBoxMethod(responseMap, bus, "OnlineCredit");
                }
            }
        }
        catch (Exception e3) {
            e3.printStackTrace();
        }
	}
        
	public void showDialogBoxMethod(final Map responseMap, final BusIfc bus, final String buttonLetter) {
        final DialogBeanModel dialogModel = new DialogBeanModel();
        final String[] msg = new String[6];
        dialogModel.setResourceID("RESPONSE_DETAILS");
        msg[0] = "<<--||--:: Please Find The Response Details As Below ::--||-->>";
        msg[1] = "Your Credit/Debit Card has been Swiped";
        msg[2] = " Response Code Returned Is ";
        if (responseMap != null && responseMap.get("HostResponseMessage") != null) {
            msg[3] = responseMap.get("HostResponseMessage").toString();
        }
        msg[4] = "Press ENTER To Proceed / Using another Tender";
        msg[5] = "::Thanks::";
        dialogModel.setArgs(msg);
        dialogModel.setType(1);
        dialogModel.setButtonLetter(0, buttonLetter);
        final POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager("UIManager");
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
    
    private TenderADOIfc setTransactionReentry(final TenderCreditADO creditTender, final TenderCargo cargo) {
        creditTender.setTransactionReentryMode(cargo.getRegister().getWorkstation().isTransReentryMode());
        return (TenderADOIfc)creditTender;
    }
    
    public HashMap getBankNameStrings(final BusIfc bus) {
        final UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager("UtilityManager");
        final Locale locale = LocaleMap.getLocale("UI_LOCALE_KEY");
        final String storeID = Gateway.getProperty("application", "StoreID", "");
        final CodeListIfc list = utility.getReasonCodes(storeID, "CreditDebitBankCodes");
        if (list != null) {
            final Vector reason = list.getTextEntries(locale);
            final Vector codes = list.getKeyEntries();
            final HashMap combinedcodes = new HashMap();
            for (int i = 0; i < codes.size(); ++i) {
                combinedcodes.put(codes.get(i).toString(), reason.get(i).toString());
            }
            return combinedcodes;
        }
        return null;
    }
    
    public String getBankCodeFromCardType(final HashMap responseMap, String cardType) {
        final String bankCode = null;
        String value = null;
        Object key = null;
        final Iterator it = responseMap.keySet().iterator();
        while (it.hasNext()) {
            key = it.next();
            value = (String) responseMap.get(key);
            value = value.replaceAll("\\s+", "");
            cardType = cardType.replaceAll("\\s+", "");
            if (cardType.equalsIgnoreCase(value)) {
                break;
            }
        }
        return (String)key;
    }
    
    protected void handleException(final TenderException e, final BusIfc bus) {
        final TenderErrorCodeEnum error = e.getErrorCode();
        final POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager("UIManager");
        final UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager("UtilityManager");
        final TenderCargo cargo = (TenderCargo)bus.getCargo();
        cargo.getTenderAttributes().remove("MSR_MODEL");
        cargo.getTenderAttributes().remove("NUMBER");
        cargo.getTenderAttributes().remove("EXPIRATION_DATE");
        cargo.setPreTenderMSRModel((MSRModel)null);
        if (error == TenderErrorCodeEnum.UNKNOWN_CARD_TYPE) {
            this.showUnknownCardDialog(ui);
        }
        else if (error == TenderErrorCodeEnum.INVALID_CARD_TYPE) {
            this.showCardTypeNotAcceptedDialog(ui);
        }
        else if (error == TenderErrorCodeEnum.INVALID_CARD_NUMBER) {
            this.showInvalidNumberDialog(utility, ui);
        }
        else if (error == TenderErrorCodeEnum.EXPIRED) {
            this.showExpiredCardDialog(utility, ui);
        }
        else if (error == TenderErrorCodeEnum.BAD_MAG_SWIPE) {
            this.showBadMagStripeDialog(ui);
        }
        else if (error == TenderErrorCodeEnum.INVALID_TENDER_TYPE) {
            this.showInvalidTenderTypeDialog(ui);
        }
    }
    
    protected void showUnknownCardDialog(final POSUIManagerIfc ui) {
        final DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("UnknownCreditCard");
        dialogModel.setType(0);
        dialogModel.setButtonLetter(1, "Loop");
        dialogModel.setButtonLetter(2, "Invalid");
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
    
    protected void showCardTypeNotAcceptedDialog(final POSUIManagerIfc ui) {
        final DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("CardTypeNotAccepted");
        dialogModel.setType(7);
        dialogModel.setButtonLetter(0, "Loop");
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
    
    protected void showInvalidNumberDialog(final UtilityManagerIfc utility, final POSUIManagerIfc ui) {
        final DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("InvalidNumberError");
        dialogModel.setType(1);
        dialogModel.setButtonLetter(0, "Loop");
        final String[] args = { null, utility.retrieveDialogText("CreditCard", "Credit Card") };
        final StringBuilder sb = new StringBuilder();
        final String[] array = args;
        final int n = 1;
        array[n] = sb.append(array[n]).append(" ").append(utility.retrieveDialogText("Number", "number")).toString();
        final Locale locale = LocaleMap.getLocale("UI_LOCALE_KEY");
        args[0] = args[1].toLowerCase(locale);
        dialogModel.setArgs(args);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
    
    protected void showExpiredCardDialog(final UtilityManagerIfc utility, final POSUIManagerIfc ui) {
        final String titleTag = "ExpiredCreditCardTitle";
        final String cardString = utility.retrieveDialogText("ExpiredCardError.Credit", "credit");
        final DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("ExpiredCardError");
        dialogModel.setType(7);
        dialogModel.setButtonLetter(0, "Invalid");
        dialogModel.setTitleTag(titleTag);
        final String[] args = { cardString, cardString };
        dialogModel.setArgs(args);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
    
    protected void showBadMagStripeDialog(final POSUIManagerIfc ui) {
        final DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("BadCreditMSRReadError");
        dialogModel.setType(7);
        dialogModel.setButtonLetter(0, "Loop");
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
    
    protected void showInvalidTenderTypeDialog(final POSUIManagerIfc ui) {
        final DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("NoHouseAccountPaymentWithHouseAccount");
        dialogModel.setType(7);
        dialogModel.setButtonLetter(0, "Invalid");
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }  
        
		
	}
