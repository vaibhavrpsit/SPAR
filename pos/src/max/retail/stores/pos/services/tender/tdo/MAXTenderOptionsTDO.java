/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *  Rev 1.3     Apr 11, 2017        Nitika Arora        Changes for Loyalty button to get disable in certain conditions.
 *	Rev	1.2 	Mar 15, 2017		Ashish Yadav		fix for issue: Disable Loyalty points tender in reentry mode
 *  Rev	1.1 	Feb 15, 2017		Nadia Arora			fix for issue: send with suspend retrive, application crash
 *	Rev	1.0 	Jan 06, 2017		Ashish Yadav		Changes for Online redemption loyalty OTP FES	
 *
 * Rev 1.2 Dec 08 2003 09:23:22 rsachdeva Alternate Currency Resolution for POS SCR-3551: Tender using Canadian Cash
 *
 * Rev 1.1 Nov 19 2003 14:11:30 epd TDO refactoring to use factory
 *
 * Rev 1.0 Nov 04 2003 11:19:14 epd Initial revision.
 *
 * Rev 1.4 Nov 03 2003 17:39:58 nrao Added button for Instant Credit.
 *
 * Rev 1.3 Oct 30 2003 13:28:40 epd enabled alternate tenders
 *
 * Rev 1.2 Oct 26 2003 14:23:42 blj updated for money order tender
 *
 * Rev 1.1 Oct 20 2003 16:34:10 epd Updated logic determining which buttons are enabled/disabled
 *
 * Rev 1.0 Oct 17 2003 12:45:28 epd Initial revision.
 * ===========================================================================
 */
package max.retail.stores.pos.services.tender.tdo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;

import max.retail.stores.domain.customer.MAXCaptureCustomer;
import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.customer.MAXCustomerConstantsIfc;
import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.customer.MAXTICCustomer;
import max.retail.stores.domain.customer.MAXTICCustomerIfc;
import max.retail.stores.domain.tender.MAXTenderLoyaltyPoints;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.ado.tender.MAXTenderTypeEnum;
import max.retail.stores.pos.ui.beans.MAXTenderBeanModel;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.customer.CaptureCustomer;
import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItem;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.TagConstantsIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.tdo.TDOAdapter;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
 * TenderOptionsTDO builds the TenderBeanModel and calculates the appropriate
 * enabled-ness of the tender options buttons on the TENDER_OPTIONS screen.
 */
public class MAXTenderOptionsTDO extends TDOAdapter implements TDOUIIfc
{
    protected static final Logger logger = Logger.getLogger(MAXTenderOptionsTDO.class);

    // attributeMap constants
    public static final String BUS = "Bus";
    public static final String TRANSACTION = "Transaction";
    public static final String TRANSACTION_REENTRY_MODE = "TransactionReentry";
    public static final String SWIPE_ANYTIME = "SwipeAnytime";
    protected final String APPLICATION_PROP_KEY = "application";
	// Changes start for code merging
	protected boolean isGiftCard = false;
	// Changes ends for code merging

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.tdo.TDOUIIfc#buildBeanModel(java.util.HashMap)
     */
    public POSBaseBeanModel buildBeanModel(HashMap attributeMap)
    {
        BusIfc bus = (BusIfc) attributeMap.get(BUS);
		// Changes start for code merging
		 /**
         * Rev 1.1 changes start
         */
        SaleReturnLineItemIfc[] items = null;
        TenderCargo cargo = (TenderCargo) bus.getCargo();
        if(cargo != null && cargo.getTransaction() instanceof MAXSaleReturnTransaction)
        {
        	MAXSaleReturnTransaction tran = (MAXSaleReturnTransaction) cargo.getTransaction();
        	items = tran.getProductGroupLineItems(ProductGroupConstantsIfc.PRODUCT_GROUP_GIFT_CARD);
        	if (items != null && items.length != 0) {

    			for (int j = 0; j < items.length; j++) {
    				if (items[j].getPLUItem() instanceof GiftCardPLUItem) 
    				{
    					isGiftCard= true;
   					}
    				}
    		}
        }
        /**
         * Rev 1.1 changes end
         * */
		// Change ends for code merging
        RetailTransactionADOIfc txnADO = (RetailTransactionADOIfc) attributeMap.get(TRANSACTION);

        // Get RDO version of transaction for use in some processing
        TenderableTransactionIfc txnRDO = (TenderableTransactionIfc) ((ADO) txnADO).toLegacy();

        // get new tender bean model
		// Changes start for code merging
        //TenderBeanModel model = new TenderBeanModel();
		 MAXTenderBeanModel model = new MAXTenderBeanModel();
		 // Changes ends for code merging
        // populate tender bean model w/ tender and totals info
        model.setTenderLineItems(txnRDO.getTenderLineItemsVector());

        if (txnRDO instanceof OrderTransactionIfc
                && ((OrderTransactionIfc)txnRDO).getOrderType() == OrderConstantsIfc.ORDER_TYPE_ON_HAND
                && ((OrderTransactionIfc)txnRDO).getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE)
        {
            model.setTransactionTotals(txnRDO.getTransactionTotals());
        }
        else
        {
            model.setTransactionTotals(txnRDO.getTenderTransactionTotals());
        }


        // set customer information
        
		// Changes start for code merging
	//	StatusBeanModel sModel = getStatusBean(bus, txnRDO.getCustomer());
		StatusBeanModel sModel = null;
        /*if (sModel != null)
        {
            model.setStatusBeanModel(sModel);
        } */
        
        if(txnRDO!=null && txnRDO instanceof MAXSaleReturnTransaction ){
        	MAXSaleReturnTransaction returnTransaction=(MAXSaleReturnTransaction)txnRDO;
        	
        	if(returnTransaction!=null && (returnTransaction.getMAXTICCustomer())!=null && returnTransaction.isTicCustomerVisibleFlag()){
     	    sModel = getStatusBean(bus, returnTransaction);
        	}else if(returnTransaction!=null && (returnTransaction.getTicCustomer())!=null ){
        		sModel = getStatusBean(bus, returnTransaction.getTicCustomer());
        	}
        	else if(returnTransaction!=null && returnTransaction.getCustomer()!=null && returnTransaction.getCustomer()instanceof MAXTICCustomer &&
        			((MAXTICCustomer)returnTransaction.getCustomer()).getTiccustomer()!=null){
        		sModel = getStatusBean(bus, ((MAXTICCustomer)returnTransaction.getCustomer()).getTiccustomer());
        	}
        	else{
             sModel = getStatusBean(bus, txnRDO.getCustomer());
        	}
        }else{
        	 sModel = getStatusBean(bus, txnRDO.getCustomer());
        }
		// Changes ends for code merging
        /* Changes for Rev 1.1 starts*/
        if (txnRDO.getCustomer() == null)
        {
        	/* Changes for Rev 1.1 ends*/
        	if (txnRDO.getCaptureCustomer() != null)
            {
                CaptureCustomerIfc captureCustomer = null;
                if(txnRDO.getCaptureCustomer() instanceof MAXCaptureCustomer)
                	captureCustomer = (MAXCaptureCustomer)txnRDO.getCaptureCustomer();
                else
                	captureCustomer = (CaptureCustomer)txnRDO.getCaptureCustomer();
                String customerName = captureCustomer.getFirstLastName();
                sModel.setCustomerName(customerName);
            }
        }
        if (sModel != null)
        {
            model.setStatusBeanModel(sModel);
        }

        // set the local navigation button bean model
        model.setLocalButtonBeanModel(getNavigationBeanModel(txnADO.getEnabledTenderOptions(), bus, attributeMap));

        // This is not a return
        model.setReturn(false);
		// Changes start for code merging
		//Added By Veeresh for Food and Non Food Totals       
        model.setFoodTotal((BigDecimal)attributeMap.get("fooTotals"));
        model.setNonFoodTotal((BigDecimal)attributeMap.get("nonFoodTotals"));
        model.setEasyBuyTotal((BigDecimal)attributeMap.get("easyBuyTotals"));
		//ends Here Done by Veeresh
		// Changes ends for code merging

        return model;
    }

    /**
     * builds status bean based on customer information
     *
     * @param bus
     * @param customer
     * @return
     */
    protected StatusBeanModel getStatusBean(BusIfc bus, CustomerIfc customer)
    {
      StatusBeanModel sModel = UIUtilities.getStatusBean((AbstractFinancialCargo) bus.getCargo());
        if (customer != null)
        {
            String[] vars = { customer.getFirstName(), customer.getLastName()};
            if(customer.isBusinessCustomer())
            {
                vars[0]=customer.getLastName();
                vars[1]="";
            }
            UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            String pattern =
                utility.retrieveText(
                    "CustomerAddressSpec",
                    BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                    TagConstantsIfc.CUSTOMER_NAME_TAG,
                    TagConstantsIfc.CUSTOMER_NAME_PATTERN_TAG,
                    LocaleConstantsIfc.USER_INTERFACE);
            String customerName = LocaleUtilities.formatComplexMessage(pattern, vars);
            sModel.setCustomerName(customerName);
        }
        return sModel;
    }

    /**
     * enables/disables tender buttons as they exist in enabledTypes array.
     *
     * @param enabledTypes
     * @return
     */

    protected NavigationButtonBeanModel getNavigationBeanModel(
        TenderTypeEnum[] enabledTypes,
        BusIfc bus,
        HashMap attributeMap)
    {
        // convert to list
        ArrayList<TenderTypeEnum> typeList = new ArrayList<TenderTypeEnum>(enabledTypes.length);
        for (int i = 0; i < enabledTypes.length; i++)
        {
            typeList.add(enabledTypes[i]);
        }

        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();
        navModel.setButtonEnabled(CommonActionsIfc.CASH, typeList.contains(TenderTypeEnum.CASH));
        navModel.setButtonEnabled(CommonActionsIfc.CHECK, typeList.contains(TenderTypeEnum.CHECK));
        navModel.setButtonEnabled(CommonActionsIfc.COUPON, typeList.contains(TenderTypeEnum.COUPON));
        navModel.setButtonEnabled(CommonActionsIfc.HOUSEACCOUNT, typeList.contains(TenderTypeEnum.HOUSE_ACCOUNT));
        // Changes start for code merging
		if (!isGiftCard) {
			navModel.setButtonEnabled("GiftCard", typeList.contains(TenderTypeEnum.GIFT_CARD));
			navModel.setButtonEnabled("GiftCert", typeList.contains(TenderTypeEnum.GIFT_CERT));
			navModel.setButtonEnabled("MallCert", typeList.contains(TenderTypeEnum.MALL_CERT));
		}
		// Changes ends for code merging
		navModel.setButtonEnabled(CommonActionsIfc.GIFT_CARD, typeList.contains(TenderTypeEnum.GIFT_CARD));
        navModel.setButtonEnabled(CommonActionsIfc.GIFT_CERT, typeList.contains(TenderTypeEnum.GIFT_CERT));
        navModel.setButtonEnabled(CommonActionsIfc.MALL_CERT, typeList.contains(TenderTypeEnum.MALL_CERT));
        navModel.setButtonEnabled(CommonActionsIfc.PURCHASE_ORDER, typeList.contains(TenderTypeEnum.PURCHASE_ORDER));
        navModel.setButtonEnabled(CommonActionsIfc.STORE_CREDIT, typeList.contains(TenderTypeEnum.STORE_CREDIT));
        navModel.setButtonEnabled(CommonActionsIfc.TRAVEL_CHECK, typeList.contains(TenderTypeEnum.TRAVELERS_CHECK));
        navModel.setButtonEnabled(CommonActionsIfc.MONEY_ORDER, typeList.contains(TenderTypeEnum.MONEY_ORDER));
        navModel.setButtonEnabled("OxigenWallet", true);
        //navModel.setButtonEnabled("EWallet", true);
	// Changes start for code merging
	//Rev 1.9 : Start
        RetailTransactionADOIfc txnADO = (RetailTransactionADOIfc) attributeMap.get(TRANSACTION);
        TenderCargo cargo = (TenderCargo) bus.getCargo();
        MAXSaleReturnTransaction tran = null;
        MAXCustomerIfc crmCustomer = null;
        // Changes starts for Rev 1.0 (Ashish :Loyalty OTP)
        TenderableTransactionIfc trxnRDO = (TenderableTransactionIfc) ((ADO) txnADO).toLegacy();
        boolean loyaltyTender =false;
		Vector v=trxnRDO.getTenderLineItemsVector();
		for(int k=0;k<v.size();k++)
		{
			if(v.get(k) instanceof MAXTenderLoyaltyPoints)
			{
				loyaltyTender=true;
			}
			
		}
		// Changes starts for Rev 1.0 (Ashish :Loyalty OTP)
		
        if(cargo != null && cargo.getTransaction() instanceof MAXSaleReturnTransaction)
        {
        	tran = (MAXSaleReturnTransaction) cargo.getTransaction();
            crmCustomer = tran.getMAXTICCustomer();
        }   
     // Changes starts for Rev 1.0 (Ashish :Loyalty OTP)
        if(txnADO!=null && txnADO.getCustomer() instanceof MAXCustomer && loyaltyTender == false)
        {
        	// Changes ends for Rev 1.0 (Ashish :Loyalty OTP)
        	MAXCustomerIfc customer = (MAXCustomerIfc)txnADO.getCustomer();
        	if((customer.getCustomerType().equalsIgnoreCase(MAXCustomerConstantsIfc.CRM)|| (crmCustomer!=null && crmCustomer.getCustomerType().equalsIgnoreCase("T"))) && !isGiftCard)
        		navModel.setButtonEnabled("LoyaltyPoints", typeList.contains(MAXTenderTypeEnum.LOYALTY_POINTS));
        	else
        		navModel.setButtonEnabled("LoyaltyPoints", false);
        }
        else
        	navModel.setButtonEnabled("LoyaltyPoints", false);
	        navModel.setButtonEnabled("Wallet", true);
	        navModel.setButtonEnabled("Paytm", true);
	        navModel.setButtonEnabled("Mobikwik", true);
        
        //Rev 1.9 : End
        /**
         * Rev 1.6 changes start
         */
       // Rev 1.10 changes Starts
        if (isGiftCard){
        		navModel.setButtonEnabled("FoodTotal", false);
        		navModel.setButtonEnabled("Wallet", false);
        	    navModel.setButtonEnabled("Paytm", false);
        	    navModel.setButtonEnabled("Mobikwik", false);
        	}
        else {
        	navModel.setButtonEnabled("FoodTotal", true);
        	navModel.setButtonEnabled("Wallet", true);
	        navModel.setButtonEnabled("Paytm", true);
	        navModel.setButtonEnabled("Mobikwik", true);
        }
        navModel.setButtonEnabled("OxigenWallet", true);
        //navModel.setButtonEnabled("EWallet", true);
     // Rev 1.10 changes Ends
        /**
         * Rev 1.6 changes end
         */
               
               
        
        /**MAX Rev 1.3 Chanege : Start change by arif in m2.**/
        //Enable disable the button on the basis of TIC customer type.
        //RetailTransactionADOIfc txnADO = (RetailTransactionADOIfc) attributeMap.get(TRANSACTION);
        /**MAX Rev 1.2 Change : Start**/
        //For Loyalty Point redumption
        //Changes start for rev 1.8
       // TenderCargo cargo = (TenderCargo) bus.getCargo();
      //  MAXSaleReturnTransaction tran = null;
       // MAXCustomerIfc crmCustomer = null;
        if(cargo != null && cargo.getTransaction() instanceof MAXSaleReturnTransaction)
        {
        	tran = (MAXSaleReturnTransaction) cargo.getTransaction();
            crmCustomer = tran.getMAXTICCustomer();
        }   
        // Changes starts for Rev 1.0 (Ashish : Loyalty OTP)
        if(txnADO!=null && txnADO.getCustomer() instanceof MAXCustomer  && loyaltyTender == false)
        	// Changes ends for Rev 1.0 (Ashish : Loyalty OTP)
        {
        	MAXCustomerIfc customer = (MAXCustomerIfc)txnADO.getCustomer();
        	if((customer.getCustomerType().equalsIgnoreCase(MAXCustomerConstantsIfc.CRM)|| (crmCustomer!=null && crmCustomer.getCustomerType().equalsIgnoreCase("T"))) && !isGiftCard)
        		navModel.setButtonEnabled("LoyaltyPoints", typeList.contains(MAXTenderTypeEnum.LOYALTY_POINTS));
        	else
        		navModel.setButtonEnabled("LoyaltyPoints", false);
        }
        else
        	navModel.setButtonEnabled("LoyaltyPoints", false);
        /**MAX Rev 1.3 Change : End**/
        // if debit is enabled AND checking BIN file, then enable credit/debit button
        // Also, we must use the correct lable for the Alternate button, depending
        // on this setting.
      //Changes end for rev 1.8
	  // Changes ends for code merging
	
        // if debit is enabled AND checking BIN file, then enable credit/debit button
        // Also, we must use the correct lable for the Alternate button, depending
        // on this setting.

        UtilityIfc util = null;
        try
        {
            util = Utility.createInstance();
        }
        catch (ADOException e)
        {
            String message = "Configuration problem: could not instantiate UtilityIfc instance";
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }

        String creditCards = util.getParameterValue(ParameterConstantsIfc.TENDER_CreditDebitCardsAccepted, "Y");
        boolean allowCreditCards = creditCards.equalsIgnoreCase("Y");
        Boolean transReentry = (Boolean) attributeMap.get(TRANSACTION_REENTRY_MODE);

        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        updateCreditDebitButton(utility, allowCreditCards, transReentry, typeList, navModel);


        String alternateButtonLabelKey = "";
        String alternateButtonLabelDefault = "";

        if(typeList.contains(TenderTypeEnum.DEBIT) || typeList.contains(TenderTypeEnum.CREDIT))
        {
            if (allowCreditCards)
            {
                alternateButtonLabelKey = "AlternateButtonKeyLabel";
                alternateButtonLabelDefault = "F6";
            }
            else
            {
                alternateButtonLabelKey = "AlternateButtonKeyLabel2";
                alternateButtonLabelDefault = "F7";
            }
        }

        // set button labels for alternate if alternate listed
        if (typeList.contains(TenderTypeEnum.ALTERNATE))
        {
            navModel.setButtonEnabled(CommonActionsIfc.ALTERNATE, true);
            String keyLabel =
                utility.retrieveText(
                        CommonActionsIfc.COMMON,
                        BundleConstantsIfc.TENDER_BUNDLE_NAME,
                        alternateButtonLabelKey,
                        alternateButtonLabelDefault,
                        LocaleConstantsIfc.USER_INTERFACE);
            navModel.setButtonKeyLabel(CommonActionsIfc.ALTERNATE, keyLabel);
            navModel.setButtonLabel(
                    CommonActionsIfc.ALTERNATE,
                    utility.retrieveText(CommonActionsIfc.COMMON, BundleConstantsIfc.TENDER_BUNDLE_NAME, "ForeignCurrency", "Foreign Currency"));

            if (util.getParameterValue("ForeignCurrency", "Y").equalsIgnoreCase("N"))
            {
                navModel.setButtonEnabled(CommonActionsIfc.ALTERNATE,false);
            }

        }
// Changes start for code merging
        //RetailTransactionADOIfc txnADO = (RetailTransactionADOIfc) attributeMap.get(TRANSACTION);
// Changes ends for code merging
        TenderableTransactionIfc txnRDO = (TenderableTransactionIfc) ((ADO) txnADO).toLegacy();

        // Enable the button for Instant Credit except for House Account payment and re-entry mode
        // Disable Instance Credit button for special order pickup, special order partial, layaway pickup and layaway payment
        boolean isWebManagedOrder = false;
        if ( txnRDO instanceof SaleReturnTransactionIfc && ((SaleReturnTransactionIfc)txnRDO).isWebManagedOrder())
        {
            isWebManagedOrder = true;
        }

        if (!transReentry.booleanValue() && !isWebManagedOrder
                && (util.getParameterValue("InstantCreditEnrollment", "Y").equalsIgnoreCase("Y"))
                && txnRDO.getTransactionType() != TransactionConstantsIfc.TYPE_HOUSE_PAYMENT
                && txnRDO.getTransactionType()!=TransactionConstantsIfc.TYPE_BILL_PAY)
        {
            String houseCardsAccepted = util.getParameterValue(ParameterConstantsIfc.TENDER_HouseCardsAccepted, "Y");
            boolean enableInstantCredit = "Y".equalsIgnoreCase(houseCardsAccepted);
            navModel.setButtonEnabled(CommonActionsIfc.INSTANT_CREDIT, enableInstantCredit);
        }
        else
        {
            navModel.setButtonEnabled(CommonActionsIfc.INSTANT_CREDIT, false);
        }

        // if a card was swiped for swipe anytime only allow credit, debit, or gift card
        Boolean swipeAnytime = (Boolean) attributeMap.get(SWIPE_ANYTIME);
        if (swipeAnytime.booleanValue())
        {
            navModel.setButtonEnabled(CommonActionsIfc.CASH, false);
            navModel.setButtonEnabled(CommonActionsIfc.CHECK, false);
            navModel.setButtonEnabled(CommonActionsIfc.COUPON, false);
            navModel.setButtonEnabled(CommonActionsIfc.GIFT_CERT, false);
            navModel.setButtonEnabled(CommonActionsIfc.MALL_CERT, false);
            navModel.setButtonEnabled(CommonActionsIfc.PURCHASE_ORDER, false);
            navModel.setButtonEnabled(CommonActionsIfc.STORE_CREDIT, false);
            navModel.setButtonEnabled(CommonActionsIfc.TRAVEL_CHECK, false);
            navModel.setButtonEnabled(CommonActionsIfc.MONEY_ORDER, false);
            navModel.setButtonEnabled(CommonActionsIfc.ALTERNATE,false);
            navModel.setButtonEnabled(CommonActionsIfc.INSTANT_CREDIT, false);
            navModel.setButtonEnabled(CommonActionsIfc.HOUSEACCOUNT, false);
        }
		// Changes start for code merging
		if(isGiftCard)
        {
        	 navModel.setButtonEnabled("Cash", typeList.contains(TenderTypeEnum.CASH));
             navModel.setButtonEnabled("Check", typeList.contains(TenderTypeEnum.CHECK));
             navModel.setButtonEnabled("Coupon", false);
             navModel.setButtonEnabled("GiftCard", false);
             navModel.setButtonEnabled("GiftCert", false);
             navModel.setButtonEnabled("MallCert", false);
             navModel.setButtonEnabled("PurchaseOrder", false);
             navModel.setButtonEnabled("StoreCredit", false);
             navModel.setButtonEnabled("TravelCheck", false);
             navModel.setButtonEnabled("MoneyOrder", false);
             navModel.setButtonEnabled("InstantCredit", false);
//             navModel.setButtonEnabled("More", false);   //Rev 1.4 changes
             /**MAX Rev 1.3 Change : Start
             navModel.setButtonEnabled("LoyaltyPoints", false);
             /**MAX Rev 1.3 Change : End**/
             navModel.setButtonEnabled("Alternate", false);
             navModel.setButtonEnabled("Credit", typeList.contains(TenderTypeEnum.CREDIT));
             navModel.setButtonEnabled("Debit", typeList.contains(TenderTypeEnum.DEBIT));
        }
        /**
		 * Rev 1.1 changes end here
		 */
        
        /**MAX Rev 1.11 Change : Start**/
        if(tran!=null && tran.iseComSendTransaction())
        {
        	 navModel.setButtonEnabled("EComPrepaid", typeList.contains(MAXTenderTypeEnum.ECOM_PREPAID));
        	 navModel.setButtonEnabled("EComCOD", typeList.contains(MAXTenderTypeEnum.ECOM_COD));
        	/* navModel.setButtonEnabled("Cash", false);
             navModel.setButtonEnabled("Check", false);
             navModel.setButtonEnabled("Coupon", false);
             navModel.setButtonEnabled("GiftCard", false);
             navModel.setButtonEnabled("GiftCert", false);
             navModel.setButtonEnabled("MallCert", false);
             navModel.setButtonEnabled("PurchaseOrder", false);
             navModel.setButtonEnabled("StoreCredit", false);
             navModel.setButtonEnabled("TravelCheck", false);
             navModel.setButtonEnabled("MoneyOrder", false);
             navModel.setButtonEnabled("InstantCredit", false);
             navModel.setButtonEnabled("LoyaltyPoints", false);
             navModel.setButtonEnabled("Alternate", false);
             navModel.setButtonEnabled("Credit",false);
             navModel.setButtonEnabled("Debit",false);
             navModel.setButtonEnabled("FoodTotal",false);
             navModel.setButtonEnabled("CreditDebit",false);*/
        	 /*navModel.setButtonEnabled("Cash", typeList.contains(TenderTypeEnum.CASH));
             navModel.setButtonEnabled("Check", typeList.contains(TenderTypeEnum.CHECK));
             navModel.setButtonEnabled("Coupon", typeList.contains(TenderTypeEnum.COUPON));
             navModel.setButtonEnabled("GiftCard", typeList.contains(TenderTypeEnum.GIFT_CARD));
             navModel.setButtonEnabled("GiftCert", typeList.contains(TenderTypeEnum.GIFT_CERT));
             navModel.setButtonEnabled("MallCert", typeList.contains(TenderTypeEnum.MALL_CERT));
             navModel.setButtonEnabled("PurchaseOrder", typeList.contains(TenderTypeEnum.PURCHASE_ORDER));
             navModel.setButtonEnabled("StoreCredit", typeList.contains(TenderTypeEnum.STORE_CREDIT));
             navModel.setButtonEnabled("TravelCheck", typeList.contains(TenderTypeEnum.TRAVELERS_CHECK));
             navModel.setButtonEnabled("MoneyOrder", typeList.contains(TenderTypeEnum.MONEY_ORDER));
             navModel.setButtonEnabled("InstantCredit", true);
             navModel.setButtonEnabled("LoyaltyPoints", typeList.contains(MAXTenderTypeEnum.LOYALTY_POINTS));
             navModel.setButtonEnabled("Alternate",false);
             navModel.setButtonEnabled("Credit",typeList.contains(TenderTypeEnum.CREDIT));
             navModel.setButtonEnabled("Debit",typeList.contains(TenderTypeEnum.DEBIT));
             navModel.setButtonEnabled("FoodTotal",true);
             navModel.setButtonEnabled("CreditDebit",true);*/
        }
        /**MAX Rev 1.11 Change : End**/
        // Changes starts for Rev 1.2 (Ashish : Loyalty Points)
        if(transReentry){
        	navModel.setButtonEnabled("LoyaltyPoints", false);
        }
     // Changes ends for Rev 1.2 (Ashish : Loyalty Points)
        
		// Changes ends for code merging

        return navModel;
    }


    /**
     * enables/disables credit/debit tender button considering tender types list, reentry mode and parameters
     *
     * @param utility
     * @param allowCreditCards
     * @param transReentry
     * @param typeList
     * @param navModel
     */
    protected void updateCreditDebitButton(UtilityManagerIfc utility, boolean allowCreditCards, Boolean transReentry, ArrayList<TenderTypeEnum> typeList, NavigationButtonBeanModel navModel)
    {
        // enable/disable credit/debit button considering parameter, reentry mode and allowed tenders
        // in some cases the "debit" label must be removed from the botton
        if (allowCreditCards && (typeList.contains(TenderTypeEnum.CREDIT) || typeList.contains(TenderTypeEnum.DEBIT)))
        {
            if (transReentry.booleanValue())
            {
                if (typeList.contains(TenderTypeEnum.CREDIT) && typeList.contains(TenderTypeEnum.DEBIT))
                {
                    navModel.setButtonEnabled(CommonActionsIfc.CREDIT_DEBIT, true);
                    navModel.setButtonLabel(CommonActionsIfc.CREDIT_DEBIT,
                        utility.retrieveText(CommonActionsIfc.COMMON, BundleConstantsIfc.TENDER_BUNDLE_NAME, CommonActionsIfc.CREDIT, "Credit"));
                }
                else if (typeList.contains(TenderTypeEnum.CREDIT))
                {
                    navModel.setButtonEnabled(CommonActionsIfc.CREDIT_DEBIT, true);
                    navModel.setButtonLabel(CommonActionsIfc.CREDIT_DEBIT,
                                    utility.retrieveText(CommonActionsIfc.COMMON, BundleConstantsIfc.TENDER_BUNDLE_NAME, CommonActionsIfc.CREDIT, "Credit"));

                }
                else // debit only - not allowed in reentry mode
                {
                    navModel.setButtonEnabled(CommonActionsIfc.CREDIT_DEBIT, false);
                }
            }
            else // not in reentry mode
            {
                if (typeList.contains(TenderTypeEnum.CREDIT) && typeList.contains(TenderTypeEnum.DEBIT))
                {
                    navModel.setButtonEnabled(CommonActionsIfc.CREDIT_DEBIT, true);
                }
                else if (typeList.contains(TenderTypeEnum.CREDIT))
                {
                    navModel.setButtonEnabled(CommonActionsIfc.CREDIT_DEBIT, true);
                    navModel.setButtonLabel(CommonActionsIfc.CREDIT_DEBIT,
                                    utility.retrieveText(CommonActionsIfc.COMMON, BundleConstantsIfc.TENDER_BUNDLE_NAME, CommonActionsIfc.CREDIT, "Credit"));
                }
                else // debit only
                {
                    navModel.setButtonEnabled(CommonActionsIfc.CREDIT_DEBIT, true);
                    navModel.setButtonLabel(CommonActionsIfc.CREDIT_DEBIT,
                                    utility.retrieveText(CommonActionsIfc.COMMON, BundleConstantsIfc.TENDER_BUNDLE_NAME, CommonActionsIfc.DEBIT, "Debit"));
                }
            }
        }
        else // credit or debit not allowed
        {
            navModel.setButtonEnabled(CommonActionsIfc.CREDIT_DEBIT, false);
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.tdo.TDOUIIfc#formatPoleDisplayLine1(oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc)
     */
    public String formatPoleDisplayLine1(RetailTransactionADOIfc txnADO)
    {
        return null;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.tdo.TDOUIIfc#formatPoleDisplayLine2(oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc)
     */
    public String formatPoleDisplayLine2(RetailTransactionADOIfc txnADO)
    {
        return null;
    }
	
	// Changes start for code merging
		///akhilesh changes for tic Customer CR START
	 
 protected StatusBeanModel getStatusBean(BusIfc bus, MAXSaleReturnTransaction transaction)
    {
        StatusBeanModel sModel = null;
        MAXTICCustomerIfc customerIfc=null;
        if (transaction.getMAXTICCustomer() != null && transaction.getMAXTICCustomer() instanceof MAXTICCustomerIfc)
        {
        	
        	customerIfc=(MAXTICCustomerIfc)transaction.getMAXTICCustomer();
        	if(customerIfc!=null && customerIfc.getTICCustomerID()!=null && !customerIfc.getTICCustomerID().equalsIgnoreCase("")){
            sModel = new StatusBeanModel();
            String[] vars = { customerIfc.getTICFirstName(), customerIfc.getTICLastName()};
            UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            String pattern =
                utility.retrieveText(
                    "CustomerAddressSpec",
                    BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                    TagConstantsIfc.CUSTOMER_NAME_TAG,
                    TagConstantsIfc.CUSTOMER_NAME_PATTERN_TAG,
                    LocaleConstantsIfc.USER_INTERFACE);
            String customerName = LocaleUtilities.formatComplexMessage(pattern, vars);
            sModel.setCustomerName(customerName);
        	}else{
	        	getStatusBean( bus, transaction.getCustomer());
	        }
        }else{
        	getStatusBean( bus, transaction.getCustomer());
        }
        return sModel;
    }

 	///akhilesh changes for tic Customer CR END
	// Changes ends for code merging
}
