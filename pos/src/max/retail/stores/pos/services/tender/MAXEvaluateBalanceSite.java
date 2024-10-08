/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.1  16/Aug/2013               Prateek		Changes done for Bug 6807			
 *  Rev 1.0  30/Mar/2013               Izhar        MAX-POS-Customer-FES_v1.2.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.tender;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXTransactionReadPrintedItemFreeDiscountRule;
import max.retail.stores.domain.arts.MAXTransactionReadSrcandTgtDiscountPerc;
import max.retail.stores.domain.discount.MAXBestDealGroup;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItem;
import max.retail.stores.domain.tender.MAXTenderPaytmIfc;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.discount.SuperGroup;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItem;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.DataManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.DispatcherIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderStoreCreditADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.SaleReturnTransactionADO;
import oracle.retail.stores.pos.ado.transaction.TenderStateEnum;
import oracle.retail.stores.pos.ado.transaction.TransactionPrototypeEnum;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.tender.EvaluateBalanceSite;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site checks the balance due on the transaction for the purpose of making
 * a flow control decision
 */
public class MAXEvaluateBalanceSite extends EvaluateBalanceSite {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extendyourstore.foundation.tour.application.SiteActionAdapter#arrive
	 * (com.extendyourstore.foundation.tour.ifc.BusIfc)
	 */
	public void arrive(BusIfc bus) {
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
	
		// ((SaleReturnTransaction) sr).getPrintFreeItem();

		// reset cargo for new tenders
		cargo.setTenderADO(null);
		HashMap tenderAttributes = cargo.getTenderAttributes();
		cargo.resetTenderAttributes();
		if (tenderAttributes.get("foodTotals") != null)
			cargo.getTenderAttributes().put("foodTotals", tenderAttributes.get("foodTotals"));
		if (tenderAttributes.get("nonFoodTotals") != null)
			cargo.getTenderAttributes().put("nonFoodTotals", tenderAttributes.get("nonFoodTotals"));
		/**MAX Rev 1.1 Change : Start**/
		if(tenderAttributes.get(TenderConstants.FIRST_NAME) != null)
		{
			cargo.getTenderAttributes().put(TenderConstants.FIRST_NAME, tenderAttributes.get(TenderConstants.FIRST_NAME));
			cargo.getTenderAttributes().put(TenderConstants.LAST_NAME, tenderAttributes.get(TenderConstants.LAST_NAME));
			cargo.getTenderAttributes().put(TenderConstants.ID_TYPE, tenderAttributes.get(TenderConstants.ID_TYPE));
		}
		/**MAX Rev 1.1 Change : End**/
		cargo.setOverrideOperator(null);
		POSUIManagerIfc uiManager = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		//TenderStoreCreditADO storeCreditTender = (TenderStoreCreditADO)cargo.getTenderADO();
		RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
		//TenderStoreCreditADO storeCreditTender = (TenderStoreCreditADO)cargo.getCurrentTransactionADO();
		//String trxtype=txnADO.getTenderLineItems(paramTenderLineItemCategoryEnum)
		
		String letter = "";

		// Check to see if we need to capture customer info
		UtilityIfc utility;
		// added by izhar MAX-POS-CASH_THRESHOLD_AMOUNT-FES_v1.1
		boolean offline = false;
		boolean showDialog = false;
		// izharDR
		MAXSaleReturnTransaction sr = null;
		//Add by vaibhav
		MAXLayawayTransaction lt =null;
		//end
		String[] str = null;
		String thresholdAmount = null;
		String printedItemText = null;
		MAXTransactionReadPrintedItemFreeDiscountRule pIRL = null;
		MAXTransactionReadSrcandTgtDiscountPerc srcAndTgtDiscPerc = null;
		ArrayList pItemRulelist = null;
		ArrayList bestDealWinnersList = null;
		MAXBestDealGroup bestDealgp = null;
		SuperGroup bestDealSupergp = null;
		//izharDR
		if(cargo.getTransaction() instanceof MAXSaleReturnTransaction)
			sr = (MAXSaleReturnTransaction) cargo.getTransaction();
		

		ArrayList bestDealWinners = new ArrayList();
		//izharDR
		if(sr!=null)
			bestDealWinners = sr.getBestDealWinners();


		pIRL = (MAXTransactionReadPrintedItemFreeDiscountRule) DataTransactionFactory
				.create(MAXDataTransactionKeys.TRANSACTION_READ_PRINTED_ITEM_FREE_DISCOUNT_RULE);
		srcAndTgtDiscPerc = (MAXTransactionReadSrcandTgtDiscountPerc) DataTransactionFactory
				.create(MAXDataTransactionKeys.TRANSACTION_READ_SRC_AND_TGT_DISCOUNT_PERC);
		try {
			bestDealWinnersList = new ArrayList();



			if (bestDealWinners.size() != 0) {
				for (int i = 0; i < bestDealWinners.size(); i++) {
					if(bestDealWinners
							.get(i) instanceof MAXBestDealGroup){
						bestDealgp = (MAXBestDealGroup) bestDealWinners
								.get(i);
						String srcAndDiscPerc = srcAndTgtDiscPerc
								.readSrcandTgtDiscountPerc(bestDealgp
										.getDiscountRule().getRuleID());
						if(!bestDealWinnersList.contains(srcAndDiscPerc)) {
							bestDealWinnersList.add(srcAndDiscPerc);
						}
						//	bestDealWinnersList.add(srcAndDiscPerc);
					}
					if(bestDealWinners
							.get(i) instanceof SuperGroup){
						bestDealSupergp = (SuperGroup) bestDealWinners
								.get(i);
						bestDealSupergp.getSubgroups();
						for (int s = 0; s < bestDealSupergp.getSubgroups().size(); s++) {
							if( bestDealSupergp.getSubgroups()
									.get(s) instanceof MAXBestDealGroup){
								MAXBestDealGroup bestdealSubGp=(MAXBestDealGroup)bestDealSupergp.getSubgroups().get(s);
								String srcAndDiscPerc = srcAndTgtDiscPerc
										.readSrcandTgtDiscountPerc(bestdealSubGp
												.getDiscountRule().getRuleID());
								if(!bestDealWinnersList.contains(srcAndDiscPerc)) {
									bestDealWinnersList.add(srcAndDiscPerc);
								}
								//bestDealWinnersList.add(srcAndDiscPerc);
							}
							else{

								if(bestDealSupergp.getSubgroups().get(s) instanceof SuperGroup){
									SuperGroup bestdealSubGp=(SuperGroup)bestDealSupergp.getSubgroups().get(s);
									String srcAndDiscPerc = srcAndTgtDiscPerc
											.readSrcandTgtDiscountPerc(bestdealSubGp
													.getDiscountRule().getRuleID());
									if(!bestDealWinnersList.contains(srcAndDiscPerc)) {
										bestDealWinnersList.add(srcAndDiscPerc);
									}
								}
								else{
									MAXBestDealGroup bestdealSubGp=(MAXBestDealGroup)bestDealSupergp.getSubgroups().get(s);
									String srcAndDiscPerc = srcAndTgtDiscPerc
											.readSrcandTgtDiscountPerc(bestdealSubGp
													.getDiscountRule().getRuleID());
									if(!bestDealWinnersList.contains(srcAndDiscPerc)) {
										bestDealWinnersList.add(srcAndDiscPerc);
									}
								}
							}

						}
					}
					for (Enumeration e = sr
							.getLineItemsVector().elements(); 
							e
							.hasMoreElements(); )
					{
						SaleReturnLineItem item = (SaleReturnLineItem)e
								.nextElement();

						if (item.getAdvancedPricingDiscount() != null)
							((MAXSaleReturnLineItem)item).setBdwList(bestDealWinnersList);
						sr.setBdwList(bestDealWinnersList);
					}


				}
			}
			pItemRulelist = new ArrayList();
			//izhar
			utility = Utility.createInstance();
			offline = isSystemOffline(utility);
			//if(!offline)
			pItemRulelist = pIRL.readPrintedItemFreeDiscountRule();
			//end
			pItemRulelist = pIRL.readPrintedItemFreeDiscountRule();
			if (pItemRulelist.size() != 0) {
				for (int index = 0; index < pItemRulelist.size(); index++) {
					str = pItemRulelist.get(index).toString().split("_");
					thresholdAmount = str[0];
					printedItemText = str[1];
					if(sr!=null){
						if (Double.parseDouble(sr.getTransactionTotals()
								.getGrandTotal().toString()) >= Double.parseDouble
								(thresholdAmount)) {
							((MAXSaleReturnTransaction) sr)
							.setPrintFreeItem(printedItemText);
						}
					}
					/*	if(lt!=null){
						if (Double.parseDouble(lt.getTransactionTotals()
								.getGrandTotal().toString()) >= Double.parseDouble
								(thresholdAmount)) {
							((MAXLayawayTransaction) lt)
									.setPrintFreeItem(printedItemText);
						}
						}*/

				}
			}
			// ends
			//utility = Utility.createInstance();
			// added by izhar MAX-POS-CASH_THRESHOLD_AMOUNT-FES_v1.1
			//offline = isSystemOffline(utility);
			String tillFloat = cargo.getRegister().getCurrentTill().getTotals()
					.getCombinedCount().getExpected().getAmount().toString();

			String limitallowed = utility.getParameterValue(
					"CashThresholdAmount", "50000.00");
			double tf = Double.parseDouble(tillFloat);
			double cta = Double.parseDouble(limitallowed);
			//commented for bug 6499
			/*if (tf >= cta)
				showDialog = true;*/
			// addition ends

		} catch (ADOException e) {
			String message = "Configuration problem: could not instantiate UtilityIfc instance";
			logger.error(message, e);
			throw new RuntimeException(message, e);
		} catch (DataException e) {
			String message = "Configuration problem: could not instantiate UtilityIfc instance";
			logger.error(message, e);
			throw new RuntimeException(message, e);
		}
		// added by izhar MAX-POS-CASH_THRESHOLD_AMOUNT-FES_v1.1
		// below code added by atul shukla for undo paytm option error
		//TenderLineItemIfc []tender=cargo.getTransaction().getTenderLineItems();
	//	for(int i=0;i<tender.length;i++)
	//	{
		//	if(tender[i].g instanceof instanceof MAXTenderPaytmIfc)
			//	letter="Wallet";
		//	break;
		//}
		
		if (showDialog) {

			if (!offline) {

				DialogBeanModel model = new DialogBeanModel();
				model.setResourceID("cashthresholdamounterror");
				model.setType(DialogScreensIfc.ERROR);

				// model.setButtonLetter(DialogScreensIfc.ERROR,
				// "cashthreshold");
				uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
			} else {
				// Evaluate the balance
				TenderStateEnum tenderState = txnADO.evaluateTenderState();

				letter = tenderState.toString();
				bus.mail(new Letter(letter), BusIfc.CURRENT);
			}
			// addition ends12
		} else if (isCaptureCustomerNeeded(utility, txnADO)) {
			bus.mail(new Letter("CaptureCustomer"), BusIfc.CURRENT);
		} else if (txnADO.capturePATCustomer()) {
			bus.mail(new Letter("CaptureIRSCustomer"), BusIfc.CURRENT);
		}

		else {
			// Evaluate the balance
			TenderStateEnum tenderState1 = txnADO.evaluateTenderState();

			letter = tenderState1.toString();
			bus.mail(new Letter(letter), BusIfc.CURRENT);
		}
	}

	// added by izhar MAX-POS-CASH_THRESHOLD_AMOUNT-FES_v1.1
	protected boolean isSystemOffline(UtilityIfc utility) {
		DispatcherIfc d = Gateway.getDispatcher();
		DataManagerIfc dm = (DataManagerIfc) d.getManager(DataManagerIfc.TYPE);
		boolean offline = true;
		try {
			if (dm.getTransactionOnline(UtilityManagerIfc.CLOSE_REGISTER_TRANSACTION_NAME)
					|| dm.getTransactionOnline(UtilityManagerIfc.CLOSE_STORE_REGISTER_TRANSACTION_NAME)) {
				offline = false;
			}
		} catch (DataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return offline;

	}
	// Chnages starts for code merging(adding below method as it is not present in base 14)
	/**
     * Determines whether or not customer information needs to be captured based on factors such as whether or not 
     * the info has already been captured, transaction type/conditions (redeems, returns, exchanges, sends, 
     * price adjustments) and statuses of the Customer.360Customer and Customer.NegativeAmtDue parameters
     * 
     * @param utility Utility class
     * @param txnADO current transaction
     */
    protected boolean isCaptureCustomerNeeded(UtilityIfc utility, RetailTransactionADOIfc txnADO)
    {
        boolean isCaptureCustomerNeeded = false;
        
        // Make sure the customer info hasn't already been captured and that we 
        // aren't set to use OracleCustomer
         
        if(!(txnADO.getCustomer()!=null)) {
        	
        if (txnADO != null && txnADO.getCaptureCustomer() == null && 
                "N".equals(utility.getParameterValue("OracleCustomer", "N")) )
        {
            // If the transaction is a redeem we know we need to capture customer info
            TransactionPrototypeEnum transactionType = txnADO.getTransactionType();
            TenderStateEnum tenderState = txnADO.evaluateTenderState();
            if (TransactionPrototypeEnum.REDEEM.equals(transactionType))
            {
                isCaptureCustomerNeeded = true;
            }
            // If there are any return line items that were not associated with
            // a retrieved return transaction and there is a zero balance, then
            // we must call the capture customer info use case.
            else if ((TenderStateEnum.PAID_UP.equals(tenderState)) &&
                     (TransactionPrototypeEnum.SALE_RETURN.equals(transactionType)))
            {
                SaleReturnTransactionADO saleReturnADO = (SaleReturnTransactionADO) txnADO;
                if (saleReturnADO.containsNonRetrievedReturnItems())
                {
                    isCaptureCustomerNeeded = true;
                }
            }
            // Otherwise, check to see if we need to capture customer info if there are return items in the 
            // transaction but regardless of balance            
            else if ("N".equals(utility.getParameterValue("NegativeAmtDue", "N")) )
            {
                if (TransactionPrototypeEnum.SALE_RETURN.equals(transactionType) 
                    && (txnADO.containsReturnItems() || txnADO.containsSendItems()) )
                {
                    isCaptureCustomerNeeded = true;
                }
            }
            // Otherwise, we only capture customer info when there is a refund due
            else
            {                
                isCaptureCustomerNeeded = TenderStateEnum.REFUND_DUE.equals(tenderState); 
            }
        }
        }
        
        return isCaptureCustomerNeeded;
    }    
    // Changes ends for code merging

}
