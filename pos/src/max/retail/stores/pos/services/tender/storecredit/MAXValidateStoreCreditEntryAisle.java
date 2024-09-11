/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved.
 * 
 * Rev 1.2   Mar 30, 2017           Nitika Arora     Changes for showing the Redeem store credit dialog.
 * Rev 1.1   Mar 29,2017            Nitika Arora     Fix for showing a valid Store Credit as Invalid after entering one invalid store credit. 
 * Rev 1.0   Dec 20,2016    		Ashish Yadav     Changes for Store credit FES
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.tender.storecredit;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import max.retail.stores.domain.arts.MAXStoreCreditDataTransaction;
import max.retail.stores.domain.tender.MAXTenderStoreCredit;
import max.retail.stores.domain.tender.MAXTenderStoreCreditIfc;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.utility.MAXStoreCredit;
import max.retail.stores.domain.utility.MAXStoreCreditIfc;
import max.retail.stores.pos.ado.tender.MAXTenderConstants;
import max.retail.stores.pos.ado.tender.MAXTenderConstantsIfc;
import max.retail.stores.pos.ado.tender.MAXTenderStoreCreditADO;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.commerceservices.common.currency.CurrencyDecimal;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.transaction.AbstractTenderableTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.StoreCreditIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXValidateStoreCreditEntryAisle extends PosLaneActionAdapter {
	private static final long serialVersionUID = 1L;

	public static final String revisionNumber = "$Revision: 1.0$";

	private static final String CO_OFFLINE_MESG = "Error getting home interface for service";

	private static final String INVALID_LETTER = "Invalid";

	private static final String ST_EXPIRED = "Stexpired";

	private static final String ST_EXPIRED_WITH_GRACEPERIOD = "Stexpiredwithgraceperiod";

	public void traverse(BusIfc bus) {
		String usedStrCrdtNumber = null;
		int storeCreditGracePeriod = 30; // Initialize to default value
		Vector strCrdtVector = null;

		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

		try {
			storeCreditGracePeriod = pm.getIntegerValue(MAXTenderConstantsIfc.STORE_CREDIT_GRACE_PERIOD).intValue();
		} catch (ParameterException e1) {
			logger.warn("Could not found the Parameter StoreCreditGraceDaysToExpiration");
		}
		
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		HashMap tenderAttributes = cargo.getTenderAttributes();
		MAXTenderStoreCreditADO storeCreditTender = null;
		RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

		AbstractTenderableTransaction transaction = (AbstractTenderableTransaction) cargo.getCurrentTransactionADO()
				.toLegacy();

		boolean isScanned = ((POSBaseBeanModel) ui.getModel()).getPromptAndResponseModel().isScanned();

		// Get the store credit number
		String strCrdtNumber = ui.getInput().trim();
		//Added by Vaibhav   for CN number length check
		 if(strCrdtNumber.length()<18) { 
			 displayDialog(bus, DialogScreensIfc.ACKNOWLEDGEMENT, "StoreCreditNotFound", null, "Invalid");
		 return;
		 
		 }
		 

		boolean bCheck = false;
		Boolean strCrdtValidated = new Boolean(true);

		if (transaction != null) {
			strCrdtVector = transaction.getTenderLineItemsVector();
		}
		for (int count = 0; count < strCrdtVector.size(); count++) {
			if (strCrdtVector.get(count) instanceof MAXTenderStoreCredit) {
				MAXTenderStoreCredit strCrdt = (MAXTenderStoreCredit) strCrdtVector.get(count);
				usedStrCrdtNumber = strCrdt.getStoreCreditID();
				if (usedStrCrdtNumber.equals(strCrdtNumber.toUpperCase())) {
					bCheck = true;
					break;
				}
			}
		}

		if (bCheck) {
			displayDialog(bus, DialogScreensIfc.ERROR, "StoreCreditRepeatNotice", null, "Repeat");
			MAXSaleReturnTransaction trns = (MAXSaleReturnTransaction) txnADO.toLegacy();
			Vector tenderLineitems = trns.getTenderLineItemsVector();
			Iterator itr = tenderLineitems.iterator();
			Enumeration e = tenderLineitems.elements();
			while (e.hasMoreElements()) {
				Object obj = e.nextElement();
				if (obj instanceof MAXTenderStoreCredit) {
					StoreCreditIfc storeCreditIfc = ((MAXTenderStoreCredit) obj).getStoreCredit();
					if (storeCreditIfc instanceof MAXStoreCredit) {
						MAXStoreCredit storeCredit = (MAXStoreCredit) storeCreditIfc;

						if ((storeCredit.getStoreCreditID().equals(tenderAttributes.get(TenderConstants.NUMBER)))) {

							cargo.getCurrentTransactionADO().removeTender(storeCreditTender);
						}
					}
				}
			}
			return;
		}

		tenderAttributes.put(TenderConstants.NUMBER, strCrdtNumber.toUpperCase());

		if (isScanned) {
			// Changes starts for code merging(commenting below line as per
			// lsipl)
			// tenderAttributes.put(TenderConstants.ENTRY_METHOD,
			// TenderLineItemIfc.ENTRY_METHOD_AUTO);
			tenderAttributes.put(TenderConstants.ENTRY_METHOD, EntryMethod.Automatic);
			// Changes ends for code merging
		} else {
			// Changes starts for code merging(commenting below line as per
			// lsipl)
			// tenderAttributes.put(TenderConstants.ENTRY_METHOD,
			// TenderLineItemIfc.ENTRY_METHOD_MANUAL);
			tenderAttributes.put(TenderConstants.ENTRY_METHOD, EntryMethod.Manual);
			// Changes ends for code merging
		}
		tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.STORE_CREDIT);
		if (transaction != null) {
			strCrdtVector = transaction.getTenderLineItemsVector();
		}
		tenderAttributes.put(MAXTenderConstantsIfc.STORE_CREDIT_VALIDATED, strCrdtValidated);
		// create the store credit tender
		// Changes start for Rev 1.0 (Ashish : Store credit)
		
		if (cargo.getTenderADO() == null) {
			try {
				TenderFactoryIfc factory = (TenderFactoryIfc) ADOFactoryComplex.getFactory("factory.tender");
				storeCreditTender = (MAXTenderStoreCreditADO) factory.createTender(tenderAttributes);

			} catch (ADOException adoe) {
				adoe.printStackTrace();
			} catch (TenderException e) {
				TenderErrorCodeEnum error = e.getErrorCode();
				if (error == TenderErrorCodeEnum.INVALID_AMOUNT) {
					// assert(false) : "This should never happen, because UI
					// enforces proper format";
				}
			}
		} else {
			storeCreditTender = (MAXTenderStoreCreditADO) cargo.getTenderADO();
			//Changes for Rev 1.1 starts
			((MAXTenderStoreCreditIfc) storeCreditTender.toLegacy()).setStoreCreditStatus("");
			//Changes for Rev 1.1 ends
		}

		MAXStoreCreditDataTransaction storeCreditTrans = null;
		//RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
		try {
			storeCreditTender.setTenderAttributes(tenderAttributes);
		} catch (TenderException e1) {
			logger.error(e1);
		}
		storeCreditTender.getTenderAttributes();
		try {
			txnADO.addTender(storeCreditTender);
			if(txnADO != null && txnADO.toLegacy() instanceof SaleReturnTransaction)
			{
				((SaleReturnTransaction)txnADO.toLegacy()).updateTransactionTotals();
			}
		} catch (TenderException e2) {
			logger.error(e2);
		}
		// Changes ends for Rev 1.0 (Ashish : Store credit)
		/*
		 * storeCreditTrans = (MAXStoreCreditDataTransaction)
		 * DataTransactionFactory.create(MAXDataTransactionKeys.
		 * STORE_CREDIT_DATA_TRANSACTION); TenderStoreCreditIfc strCrdt =
		 * (TenderStoreCreditIfc)storeCreditTender.toLegacy(); // MFL Change for
		 * Rev 1.2 : Start StoreDataTransaction storeDataTrans = null;
		 * storeDataTrans = (StoreDataTransaction)
		 * DataTransactionFactory.create(DataTransactionKeys.
		 * STORE_DATA_TRANSACTION);
		 */
		// MFL Change for Rev 1.2 : Start
		MAXTenderStoreCreditIfc strCrdt = (MAXTenderStoreCreditIfc) storeCreditTender.toLegacy();
		String mobileNumber = (String) storeCreditTender.getTenderAttributes().get(MAXTenderConstants.mobileNumber);
		
		try {
			Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
			// fix for bugs 6847,6848
			// strCrdt.setTrainingMode(cargo.getRegister().getWorkstation().isTrainingMode());
			// end fix for bugs 6847,6848
			strCrdt.setStoreNumber(transaction.getFormattedStoreID());
			strCrdt.setTransactionSeqNumber(String.valueOf(transaction.getTransactionSequenceNumber()));
			((MAXTenderStoreCreditIfc) strCrdt).setBusinessDate(transaction.getBusinessDay());
			String tenderOriginalAmount = (String) tenderAttributes.get(TenderConstants.AMOUNT);
			BigDecimal tenderOriginalBigDecimal = new BigDecimal(tenderOriginalAmount);
			// Changes start for Rev 1.0 (Ashish : Store credit)
			// TenderStoreCreditIfc validatedStrCrdt =
			// storeCreditTrans.lookUpStoreCredit(strCrdt);
			// MAXTenderStoreCreditIfc validatedStrCrdt =
			// (MAXTenderStoreCreditIfc)
			// txnADO.getTenderStoreCreditIfcLineItem();
			// String
			// s=((MAXTenderStoreCreditIfc)txnADO.getTenderStoreCreditIfcLineItem()).getStoreCreditStatus();
			// Changes ends for Rev 1.0 (Ashish : Store credit)

			// tenderAttributes.put(TenderConstants.AMOUNT,
			// validatedStrCrdt.getAmount().getStringValue());
			if (strCrdt != null) {
				tenderAttributes.put(MAXTenderConstantsIfc.STORE_CREDIT_EXPIRED, strCrdt.getExpirationDate());
				tenderAttributes.put(TenderConstants.AMOUNT, strCrdt.getAmount().toString());
				tenderAttributes.put(MAXTenderConstants.mobileNumber, mobileNumber);
			}
			storeCreditTender.setTenderAttributes(tenderAttributes);

			EYSDate exp_Date = (EYSDate) tenderAttributes.get(MAXTenderConstantsIfc.STORE_CREDIT_EXPIRED);
			// aDDED BY hIMANSHU

			// MFL Change for Rev 1.2 : Start
			// String issuedStrCrdtStoreID =
			// validatedStrCrdt.getStoreCreditID().substring(0, 5);
			/*
			 * String issuedStrCrdtStoreIDTsfEntId =
			 * storeDataTrans.getStoreTransferEntity(issuedStrCrdtStoreID);
			 * String storeTransferEntityID =
			 * storeDataTrans.getStoreTransferEntity(strCrdt.getStoreNumber());
			 * 
			 * if (issuedStrCrdtStoreIDTsfEntId == null || storeTransferEntityID
			 * == null || !issuedStrCrdtStoreIDTsfEntId.equalsIgnoreCase(
			 * storeTransferEntityID)) { String args[] = new String[1]; args[0]
			 * = utility.retrieveDialogText("StoreCredit", "StoreCredit");
			 * displayDialog(bus, DialogScreensIfc.ERROR,
			 * "BrandValidationError", args, INVALID_LETTER); return; }
			 */

			cargo.setTenderADO(storeCreditTender);

			// Changes start for Rev 1.0 (Ashish : Store credit)
			
			if (strCrdt.getStoreCreditStatus().equals("NO_DATA"))
			// MFL Change for Rev 1.2 : End
			{
				displayDialog(bus, DialogScreensIfc.ACKNOWLEDGEMENT, "StoreCreditNotFound", null, "Invalid");
// Changes starts resolve class cast when putting wrong str credit during layaway
				if(txnADO.toLegacy() instanceof MAXSaleReturnTransaction){
				MAXSaleReturnTransaction trns = (MAXSaleReturnTransaction) txnADO.toLegacy();
				Vector tenderLineitems = trns.getTenderLineItemsVector();
				Iterator itr = tenderLineitems.iterator();
				Enumeration e = tenderLineitems.elements();
				while (e.hasMoreElements()) {
					Object obj = e.nextElement();
					if (obj instanceof MAXTenderStoreCredit) {
						StoreCreditIfc storeCreditIfc = ((MAXTenderStoreCredit) obj).getStoreCredit();
						if (storeCreditIfc instanceof MAXStoreCredit) {
							MAXStoreCredit storeCredit = (MAXStoreCredit) storeCreditIfc;

							if ((storeCredit.getStoreCreditID().equalsIgnoreCase((String) tenderAttributes.get(TenderConstants.NUMBER)))) {

								cargo.getCurrentTransactionADO().removeTender(storeCreditTender);
							}
						}
					}
				}
				return;
			}
				else if (txnADO.toLegacy() instanceof MAXLayawayTransaction){
					MAXLayawayTransaction trns = (MAXLayawayTransaction) txnADO.toLegacy();
					Vector tenderLineitems = trns.getTenderLineItemsVector();
					Iterator itr = tenderLineitems.iterator();
					Enumeration e = tenderLineitems.elements();
					while (e.hasMoreElements()) {
						Object obj = e.nextElement();
						if (obj instanceof MAXTenderStoreCredit) {
							StoreCreditIfc storeCreditIfc = ((MAXTenderStoreCredit) obj).getStoreCredit();
							if (storeCreditIfc instanceof MAXStoreCredit) {
								MAXStoreCredit storeCredit = (MAXStoreCredit) storeCreditIfc;

								if ((storeCredit.getStoreCreditID().equalsIgnoreCase((String) tenderAttributes.get(TenderConstants.NUMBER)))) {

									cargo.getCurrentTransactionADO().removeTender(storeCreditTender);
								}
							}
						}
				}
					return;
			}
			}
// Changes ends resolve class cast when putting wrong str credit during layaway
			else if (strCrdt.getStoreCreditStatus().equals("REDEEM"))
			// MFL Change for Rev 1.2 : End
			{
				String args[] = new String[4];
				args[0] = utility.retrieveDialogText("StoreCredit", "StoreCredit");
				args[1] = (String) storeCreditTender.getTenderAttributes().get(TenderConstants.NUMBER);				
				args[2] = strCrdt.getRedeemTransactionID();
				args[3] = strCrdt.getRedeemDate().toFormattedString(locale);
                if(args[2]!=null)
                {
                	args[0] = (String) storeCreditTender.getTenderAttributes().get(TenderConstants.NUMBER);	
                	args[1] = strCrdt.getRedeemDate().toFormattedString(locale);
                	displayDialog(bus, DialogScreensIfc.ACKNOWLEDGEMENT, "TenderRedeemed", args, "Invalid");
                }
                else
				displayDialog(bus, DialogScreensIfc.ACKNOWLEDGEMENT, "TenderRedeemedStoreCredit", args, "Invalid");
                tenderAttributes.put(TenderConstants.AMOUNT, tenderOriginalBigDecimal.toString());
    			storeCreditTender.setTenderAttributes(tenderAttributes);
				return;
			}
			
			else if(tenderOriginalBigDecimal.compareTo(strCrdt.getAmount().getDecimalValue())!=0)
       	 {
       		 displayDialog(bus, DialogScreensIfc.ACKNOWLEDGEMENT, "TenderAmountMismatch", null, "Invalid");
       		MAXSaleReturnTransaction trns = (MAXSaleReturnTransaction) txnADO.toLegacy();
			Vector tenderLineitems = trns.getTenderLineItemsVector();
			Iterator itr = tenderLineitems.iterator();
			Enumeration e = tenderLineitems.elements();
			while (e.hasMoreElements()) {
				Object obj = e.nextElement();
				if (obj instanceof MAXTenderStoreCredit) {
					StoreCreditIfc storeCreditIfc = ((MAXTenderStoreCredit) obj).getStoreCredit();
					if (storeCreditIfc instanceof MAXStoreCredit) {
						MAXStoreCredit storeCredit = (MAXStoreCredit) storeCreditIfc;

						if ((storeCredit.getStoreCreditID().equalsIgnoreCase((String) tenderAttributes.get(TenderConstants.NUMBER)))) {

							cargo.getCurrentTransactionADO().removeTender(storeCreditTender);
						}
					}
				}
			}
			tenderAttributes.put(TenderConstants.AMOUNT, tenderOriginalBigDecimal.toString());
			storeCreditTender.setTenderAttributes(tenderAttributes);
                return;
       	 }
			
				DataException de = strCrdt.getError();
				if(de != null)
				{
			if (de.getMessage().startsWith(CO_OFFLINE_MESG) && de.getErrorCode() == DataException.CONNECTION_ERROR)
				
        	{        
				cargo.getCurrentTransactionADO().removeTender(storeCreditTender);
				displayDialog(bus, DialogScreensIfc.ERROR, "StoreCreditValidationOffline", null, "Offline");
				return;
        	}
        	else if (de.getErrorCode() == DataException.NO_DATA)
			{
	        	if(cargo.getStoreStatus().getStore().getStoreID().equals(strCrdt.getStoreCreditID().substring(0, 5)))
	        	{
	        		cargo.getCurrentTransactionADO().removeTender(storeCreditTender);
	        		displayDialog(bus, DialogScreensIfc.ERROR, "InvalidStoreCreditError", null, INVALID_LETTER);
	        		return;
	        		
	        	}
	        	else
	        	{
	        		cargo.getCurrentTransactionADO().removeTender(storeCreditTender);
	        		displayDialog(bus, DialogScreensIfc.ERROR, "StoreCreditValidationOffline", null, "Offline");
					return;
	        	}
			}
        	else if (de.getErrorCode() == DataException.SQL_ERROR)
			{
        		cargo.getCurrentTransactionADO().removeTender(storeCreditTender);
	        	displayDialog(bus, DialogScreensIfc.ERROR, "StoreCreditValidationOffline", null, INVALID_LETTER);
	            return;
			}
        	else if (de.getErrorCode() == DataException.UNKNOWN)
			{
        		cargo.getCurrentTransactionADO().removeTender(storeCreditTender);
	        	displayDialog(bus, DialogScreensIfc.ERROR, "StoreCreditValidationOffline", null, INVALID_LETTER);
	            return;
			}
        	else if (de.getErrorCode() == DataException.CONNECTION_ERROR ||
        			     de.getErrorCode() == DataException.CONFIG_ERROR)
			{	        	
        		String[] args = new String[1];
                args[0] = "Store Credit";
	        	displayDialog(bus, DialogScreensIfc.ERROR, "StoreCreditValidationOffline", args, INVALID_LETTER);
	            return;
			}
				}
			// Changes ends for Rev 1.0 (Ashish : Store credit)
			else if (tenderOriginalBigDecimal.compareTo(strCrdt.getAmount().getDecimalValue()) != 0) {
				displayDialog(bus, DialogScreensIfc.ACKNOWLEDGEMENT, "TenderAmountMismatch", null, "Invalid");
				MAXSaleReturnTransaction trns = (MAXSaleReturnTransaction) txnADO.toLegacy();
				Vector tenderLineitems = trns.getTenderLineItemsVector();
				Iterator itr = tenderLineitems.iterator();
				Enumeration e = tenderLineitems.elements();
				while (e.hasMoreElements()) {
					Object obj = e.nextElement();
					if (obj instanceof MAXTenderStoreCredit) {
						StoreCreditIfc storeCreditIfc = ((MAXTenderStoreCredit) obj).getStoreCredit();
						if (storeCreditIfc instanceof MAXStoreCredit) {
							MAXStoreCredit storeCredit = (MAXStoreCredit) storeCreditIfc;

							if ((storeCredit.getStoreCreditID().equals(tenderAttributes.get(TenderConstants.NUMBER)))) {

								cargo.getCurrentTransactionADO().removeTender(storeCreditTender);
							}
						}
					}
				}
				return;
			}

			else if (isStoreCreditExpired(strCrdt)) {
				CurrencyDecimal totalTenderAmount = (CurrencyDecimal) (((MAXTenderCargo) cargo).getTotalTenderAmount());

				// CurrencyDecimal storeCreditAmount=(CurrencyDecimal)
				// tenderAttributes.get(TenderConstants.AMOUNT);
				CurrencyIfc amount = parseAmount((String) tenderAttributes.get(TenderConstants.AMOUNT));
				BigDecimal tenderAmount = amount.getDecimalValue();
				BigDecimal stCreditAmount = strCrdt.getAmountTender().getDecimalValue();

				if (isStoreCreditGracePeriodExpired(exp_Date, storeCreditGracePeriod)) {
					displayDialog(bus, DialogScreensIfc.ERROR, "StoreCreditGracePeriodExpired", null,
							ST_EXPIRED_WITH_GRACEPERIOD);
					MAXSaleReturnTransaction trns = (MAXSaleReturnTransaction) txnADO.toLegacy();
					Vector tenderLineitems = trns.getTenderLineItemsVector();
					Iterator itr = tenderLineitems.iterator();
					Enumeration e = tenderLineitems.elements();
					while (e.hasMoreElements()) {
						Object obj = e.nextElement();
						if (obj instanceof MAXTenderStoreCredit) {
							StoreCreditIfc storeCreditIfc = ((MAXTenderStoreCredit) obj).getStoreCredit();
							if (storeCreditIfc instanceof MAXStoreCredit) {
								MAXStoreCredit storeCredit = (MAXStoreCredit) storeCreditIfc;

								if ((storeCredit.getStoreCreditID()
										.equals(tenderAttributes.get(TenderConstants.NUMBER)))) {

									cargo.getCurrentTransactionADO().removeTender(storeCreditTender);
								}
							}
						}
					}
					return;
				} else if (tenderAmount.compareTo(stCreditAmount) == 1) {
					displayDialog(bus, DialogScreensIfc.ERROR, "StoreCreditOverTenderNotAllowed", null,
							ST_EXPIRED_WITH_GRACEPERIOD);
					MAXSaleReturnTransaction trns = (MAXSaleReturnTransaction) txnADO.toLegacy();
					Vector tenderLineitems = trns.getTenderLineItemsVector();
					Iterator itr = tenderLineitems.iterator();
					Enumeration e = tenderLineitems.elements();
					while (e.hasMoreElements()) {
						Object obj = e.nextElement();
						if (obj instanceof MAXTenderStoreCredit) {
							StoreCreditIfc storeCreditIfc = ((MAXTenderStoreCredit) obj).getStoreCredit();
							if (storeCreditIfc instanceof MAXStoreCredit) {
								MAXStoreCredit storeCredit = (MAXStoreCredit) storeCreditIfc;

								if ((storeCredit.getStoreCreditID()
										.equals(tenderAttributes.get(TenderConstants.NUMBER)))) {

									cargo.getCurrentTransactionADO().removeTender(storeCreditTender);
								}
							}
						}
					}
					return;
				}
				// fix for bug 7286
				else if (totalTenderAmount.getDecimalValue().compareTo(stCreditAmount) == -1) {
					displayDialog(bus, DialogScreensIfc.ERROR, "StoreCreditOverTenderNotAllowed", null,
							ST_EXPIRED_WITH_GRACEPERIOD);
					MAXSaleReturnTransaction trns = (MAXSaleReturnTransaction) txnADO.toLegacy();
					Vector tenderLineitems = trns.getTenderLineItemsVector();
					Iterator itr = tenderLineitems.iterator();
					Enumeration e = tenderLineitems.elements();
					while (e.hasMoreElements()) {
						Object obj = e.nextElement();
						if (obj instanceof MAXTenderStoreCredit) {
							StoreCreditIfc storeCreditIfc = ((MAXTenderStoreCredit) obj).getStoreCredit();
							if (storeCreditIfc instanceof MAXStoreCredit) {
								MAXStoreCredit storeCredit = (MAXStoreCredit) storeCreditIfc;

								if ((storeCredit.getStoreCreditID()
										.equals(tenderAttributes.get(TenderConstants.NUMBER)))) {

									cargo.getCurrentTransactionADO().removeTender(storeCreditTender);
								}
							}
						}
					}
					return;
				}
				// end fix for bug 7286
				cargo.getCurrentTransactionADO().removeTender(storeCreditTender);
				displayDialog(bus, DialogScreensIfc.YES_NO, "StoreCreditExpired", new String[] { exp_Date.toString() },ST_EXPIRED);
				return;
			}
		}
		/*
		 * catch (DataException de) { // MFL Change for Rev 1.1 - Start if
		 * (de.getMessage().startsWith(CO_OFFLINE_MESG) && de.getErrorCode() ==
		 * DataException.CONNECTION_ERROR) { displayDialog(bus,
		 * DialogScreensIfc.ERROR, "StoreCreditValidationOffline", null,
		 * "Offline"); return; } else if (de.getErrorCode() ==
		 * DataException.NO_DATA) {
		 * if(cargo.getStoreStatus().getStore().getStoreID().equals(strCrdt.
		 * getStoreCreditID().substring(0, 5))) { displayDialog(bus,
		 * DialogScreensIfc.ERROR, "InvalidStoreCreditError", null,
		 * INVALID_LETTER); return; } else { displayDialog(bus,
		 * DialogScreensIfc.ERROR, "StoreCreditValidationOffline", null,
		 * "Offline"); return; } } else if (de.getErrorCode() ==
		 * DataException.SQL_ERROR) { displayDialog(bus, DialogScreensIfc.ERROR,
		 * "StoreCreditValidationOffline", null, INVALID_LETTER); return; } else
		 * if (de.getErrorCode() == DataException.UNKNOWN) { displayDialog(bus,
		 * DialogScreensIfc.ERROR, "StoreCreditValidationOffline", null,
		 * INVALID_LETTER); return; } else if (de.getErrorCode() ==
		 * DataException.CONNECTION_ERROR || de.getErrorCode() ==
		 * DataException.CONFIG_ERROR) { String[] args = new String[1]; args[0]
		 * = "Store Credit"; displayDialog(bus, DialogScreensIfc.ERROR,
		 * "StoreCreditValidationOffline", args, INVALID_LETTER); return; } //
		 * MFL Change for Rev 1.1 - End }
		 */
		catch (TenderException e) {
			logger.error(e);
		}

		bus.mail("Valid", BusIfc.CURRENT);

	}

	private MAXStoreCreditIfc getStoreCredit() {
		// TODO Auto-generated method stub
		return null;
	}

	// ----------------------------------------------------------------------
	/**
	 * 
	 * MFL Customizations Display an error dialog Added by Shavinki Goyal
	 *
	 * 
	 * @param bus
	 *            The bus arriving at the site
	 * @param screenType
	 *            The dialog type to be displayed
	 * @param message
	 *            The dialog text to be displayed
	 * @param args
	 *            The arguments to be displayed
	 * @param letter
	 *            The letter to be mailed
	 */
	// ----------------------------------------------------------------------
	protected void displayDialog(BusIfc bus, int screenType, String message, String[] args, String letter) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		if (letter != null) {
			UIUtilities.setDialogModel(ui, screenType, message, args, letter);
		}
	}

	protected boolean isStoreCreditExpired(TenderStoreCreditIfc validatedStrCrdt) {
		EYSDate exp_Date = validatedStrCrdt.getExpirationDate();
		EYSDate current_Date = new EYSDate(); // initialize to today
		boolean storeCreditExpiredFlag = false;
		if (exp_Date != null && current_Date.after(exp_Date)) {
			storeCreditExpiredFlag = true;
		}

		return storeCreditExpiredFlag;

	}

	protected boolean isStoreCreditGracePeriodExpired(EYSDate storeCreditExpiryDate, int storeCreditGracePeriod) {
		boolean isStoreCreditGracePeriodExpired = false;
		Calendar cal = Calendar.getInstance();
		cal.setTime(storeCreditExpiryDate.dateValue());
		cal.add(Calendar.DAY_OF_MONTH, storeCreditGracePeriod);
		EYSDate final_expirationDate = new EYSDate(cal.getTime());
		EYSDate current_Date = new EYSDate();
		if (current_Date.after(final_expirationDate)) {
			isStoreCreditGracePeriodExpired = true;
			return isStoreCreditGracePeriodExpired;
		}

		return isStoreCreditGracePeriodExpired;
	}

	protected CurrencyIfc parseAmount(String amountString) throws TenderException {
		CurrencyIfc amount = null;
		try {
			amount = DomainGateway.getBaseCurrencyInstance(amountString);
		} catch (Exception e) {
			throw new TenderException("Attempted to parse amount string", TenderErrorCodeEnum.INVALID_AMOUNT, e);
		}
		return amount;
	}
}
