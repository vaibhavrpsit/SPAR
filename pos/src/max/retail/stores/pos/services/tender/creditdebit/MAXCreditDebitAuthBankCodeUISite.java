/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 *	Rev 1.0			27 Oct 2017			Jyoti Yadav				Changes for Innoviti Integration CR
 *  Rev 1.1			01 Sep 2020			Kumar Vaibhav			Pinelabs Inegration	
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.tender.creditdebit;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import max.retail.stores.domain.tender.MAXTenderChargeIfc;
import max.retail.stores.domain.tender.MAXTenderDebitIfc;
import max.retail.stores.domain.utility.MAXCodeConstantsIfc;
import max.retail.stores.pos.ado.tender.MAXTenderConstants;
import max.retail.stores.pos.ado.tender.MAXTenderCreditADO;
import max.retail.stores.pos.ado.tender.MAXTenderDebitADO;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXCreditDebitInfoBeanModel;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.lineitem.TenderLineItemCategoryEnum;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

public class MAXCreditDebitAuthBankCodeUISite  extends PosSiteActionAdapter{
	
	private static final String SEPARATOR = "-";
	private static final String ONLINE = "ONLINE";
	private static final String OFFLINE = "OFFLINE";
	private static final String SPACE = " ";
	private static final String BANK = "BANK";
	
	public void arrive(BusIfc bus) {
		if (("WithOutExpValOCC").equals(bus.getCurrentLetter().getName()))
		{
			bus.mail(new Letter("Next"), BusIfc.CURRENT);
		} else {
			MAXCreditDebitInfoBeanModel model = new MAXCreditDebitInfoBeanModel();
			// Display the screen
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			model.setBankCodeStrings(getBankReasonCodeStrings(bus));
			ui.showScreen(MAXPOSUIManagerIfc.CREDIT_DEBIT_DETAILS, model);
		}
	}
	
	public void depart(BusIfc bus) 
	{
		if (bus.getCurrentLetter().getName().equalsIgnoreCase(CommonLetterIfc.NEXT)) 
		{
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

			

			MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
			TenderADOIfc tender = cargo.getLineDisplayTender();

			MAXCreditDebitInfoBeanModel beanModel = null;
			HashMap respMap = cargo.getResponseMap();
			if (respMap != null && respMap.get("HostResponseApprovalCode") != null
					&& respMap.get("SelectedAquirerName") != null) {

			} 
			else 
			{
				beanModel = (MAXCreditDebitInfoBeanModel) ui.getModel(MAXPOSUIManagerIfc.CREDIT_DEBIT_DETAILS);
			}

			String authCode = null;
			String bankCode = null;
			HashMap bankNameStrings = getBankNameStrings(bus);
			String cardType = null;
			// MAX Change for Rev 1.1: Start
			String issuerName = null;
			String issuerBankCode = null;
			String emiTenure = null;

			if (respMap != null && respMap.get("HostResponseApprovalCode") != null
					&& respMap.get("SelectedAquirerName") != null) 
			{

				authCode = respMap.get("HostResponseApprovalCode").toString();
				cardType = respMap.get("SelectedAquirerName").toString();
				
				if (respMap.get("IssuerName") != null)
				{
					issuerName = respMap.get("IssuerName").toString();
					issuerBankCode = getBankCodeFromCardType(bankNameStrings, issuerName);
				}
				
				bankCode = getBankCodeFromCardType(bankNameStrings, cardType);
//				 Set set = bankNameStrings.entrySet();
//				Iterator i = set.iterator();
			// Changes start - Production issue resolved - Karni
			//	bankNameStrings.equals(cardType);
				//cardType = cardType + SEPARATOR + ONLINE;
				if((cardType.equalsIgnoreCase("SBI") || cardType.equalsIgnoreCase("ICICI"))){
				cardType = cardType + SPACE + BANK;
				}
				
				if(cardType.equalsIgnoreCase("AMEX") ){
					cardType = "AMERICAN EXPRESS";
				}
				// Changes end - Production issue resolved - Karni
			} 
			else 
			{
				authCode = beanModel.getAuthorizationCode();
				bankCode = beanModel.getSelectedBankCode();
				// bankNameStrings = getBankNameStrings(bus);
				cardType = bankNameStrings.get(bankCode).toString();

				//cardType = cardType + SEPARATOR + OFFLINE;
			}

			if (respMap != null && respMap.get("EMITenure") != null)
			{
				emiTenure = respMap.get("EMITenure").toString();
			}		
			
			MAXTenderChargeIfc tenderCharge = null;
			if (tender instanceof MAXTenderCreditADO) {
				tenderCharge = (MAXTenderChargeIfc) tender.toLegacy();
				tenderCharge.setAuthCode(authCode);
				tenderCharge.setBankCode(bankCode);
				tenderCharge.setResponseDate(respMap);
				tenderCharge.setCardType(cardType);
				//tenderCharge.setBankName(cardType.substring(0, cardType.indexOf("-")));
				tenderCharge.setBankName(cardType);
				// tenderCharge.setCardType(bankNameStrings.get(bankCode).toString());
				if (emiTenure != null)
				{
					tenderCharge.setEmiTransaction(true);
				}
			} 
			else if (tender instanceof MAXTenderDebitADO) 
			{
				MAXTenderDebitIfc tenderDebit = (MAXTenderDebitIfc) tender.toLegacy();
				tenderDebit.setAuthCode(authCode);
				tenderDebit.setBankCode(bankCode);
				// tenderCharge.setCardType(bankNameStrings.get(bankCode).toString());
				tenderDebit.setCardType(cardType);
				if (emiTenure != null)
				{
					tenderDebit.setEmiTransaction(true);
				}
			}

			HashMap tenderAttributes = cargo.getTenderAttributes();
			tenderAttributes.put(MAXTenderConstants.AUTH_CODE, authCode);
			tenderAttributes.put(MAXTenderConstants.BANK_CODE, bankCode);
			tenderAttributes.put(MAXTenderConstants.BANK_NAME, cardType);
			tenderAttributes.put(MAXTenderConstants.CARD_TYPE, cardType);
			// update tender with new attributes
			try
			{
				tender.setTenderAttributes(tenderAttributes);
			} catch (TenderException e) {
				logger.error("TenderException:  This should not happen.");
			}

			TenderADOIfc[] tenderVector = cargo.getCurrentTransactionADO().getTenderLineItems(TenderLineItemCategoryEnum.ALL);
			if (tenderVector != null && tenderVector.length > 0)
			{
				for (int i=0; i < tenderVector.length; i++)
				{
					TenderADOIfc tenderObject = tenderVector[i];
					if (tenderObject instanceof MAXTenderCreditADO)
					{
							MAXTenderChargeIfc tenderChargeObj = (MAXTenderChargeIfc) tenderObject.toLegacy();
							if (tenderChargeObj.getCardType().startsWith("CSHBK") &&
								tenderChargeObj.getResponseDate().equals(tenderCharge.getResponseDate()))
							{
								tenderChargeObj.setAuthCode(tenderCharge.getAuthCode());								
								tenderChargeObj.setEmiTransaction(tenderCharge.isEmiTransaction());
								
								if (issuerName != null)
								{
							      //changes from CSHBK to C
								  //String reqBankName = tenderChargeObj.getCardType() + SEPARATOR + issuerName;
									String reqBankName = "C" + SEPARATOR + "CSHBK";
									
									if (reqBankName.length() > 20)
									{
										reqBankName = reqBankName.substring(0, 20);
									}
									tenderChargeObj.setCardType(reqBankName);
									tenderChargeObj.setBankCode(issuerBankCode);
								}
								// Ideally this should never happen for Cashback
								else
								{
									tenderChargeObj.setCardType(tenderCharge.getCardType());
									tenderChargeObj.setBankCode(tenderCharge.getBankCode());
								}
								break;
							}
					 }
				 }
						
			}

			// journal
			JournalManagerIfc journal = (JournalManagerIfc) Gateway.getDispatcher().getManager(JournalManagerIfc.TYPE);

			StringBuffer sb = new StringBuffer();
			sb.append("Authorization Code: " + authCode);
			if (emiTenure != null)
			{
				//sb.append("  EMI Tenure: " + emiTenure);
			}
			sb.append(Util.EOL);
			//sb.append("  Bank Code: " + bankCode);
			journal.journal(sb.toString());
		} else if (bus.getCurrentLetter().getName().equalsIgnoreCase(CommonLetterIfc.UNDO)) {
			TenderCargo cargo = (TenderCargo) bus.getCargo();
			HashMap tenderAttributes = cargo.getTenderAttributes();
			tenderAttributes.remove(TenderConstants.EXPIRATION_DATE);
		}
	}

	public Vector getBankReasonCodeStrings(BusIfc bus) {
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
		 Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
		  String storeID = Gateway.getProperty("application", "StoreID", "");

			CodeListIfc list = utility.getReasonCodes(storeID, MAXCodeConstantsIfc.CODE_LIST_CREDIT_DEBIT_BANK_CODES);
		if (list != null) {
			Vector reason = list.getTextEntries(locale);
			Vector codes = list.getKeyEntries();
			String codesreason = null;
			Vector combinedcodes = new Vector();
			for (int i = 0; i < codes.size(); i++) {
				//Changes starts for Rev 1.1(Vaibhav : PineLab)
				/*codesreason = codes.get(i).toString() + "-" + reason.get(i).toString();
				combinedcodes.add(codesreason);*/
				
				if(!reason.get(i).toString().contains("-P")){
					codesreason = codes.get(i).toString() + "-" + reason.get(i).toString();
					combinedcodes.add(codesreason);
				}
				//Changes ends for Rev 1.1(Vaibhav : PineLab)
			}

			return (combinedcodes);
		}
		return null;
	}

	public HashMap getBankNameStrings(BusIfc bus) {
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
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
		while (it.hasNext()) {
			key = it.next();
			value = (String) responseMap.get(key);
          
		 value= value.replaceAll("\\s+","");
		 cardType= cardType.replaceAll("\\s+",""); 
			if (cardType.equalsIgnoreCase(value))
				break;
		}
		return (String) key;
	}
}
