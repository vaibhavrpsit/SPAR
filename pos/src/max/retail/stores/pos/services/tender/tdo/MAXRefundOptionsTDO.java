/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *  Rev 1.2    	Sep 14, 2018		Purushotham Reddy   Changes for Code Merge Prod Defects
 *  Rev 1.0     Nov 22, 2016	    Ashish Yadav		Changes for Employee Discount FES
 *
 ********************************************************************************/
package max.retail.stores.pos.services.tender.tdo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import max.retail.stores.pos.ui.beans.MAXTenderBeanModel;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.tender.TenderLineItemConstantsIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.ReturnableTransactionADOIfc;
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
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;
import oracle.retail.stores.pos.ui.beans.TenderBeanModel;

/**
 *  
 */
public class MAXRefundOptionsTDO extends TDOAdapter
                                      implements TDOUIIfc
{
	// Changes starts for Rev 1.0 (Ashish : Employee discount(This file is merged with 14 version)) 
    private static Logger logger = Logger.getLogger(MAXRefundOptionsTDO.class);

    // attributeMap constants
    public static final String BUS = "Bus";
    public static final String TRANSACTION = "Transaction";
    public static final String ORIG_RETURN_TXNS = "OriginalReturnTransactions";
    
    // valid refund tenders
    public static final String REFUND_TENDER_CASH = "Cash";
    public static final String REFUND_TENDER_MAILCHECK = "MailCheck";
    public static final String REFUND_TENDER_CREDIT = "Credit";
    public static final String REFUND_TENDER_DEBIT = "Debit";
    public static final String REFUND_TENDER_GIFTCARD = "GiftCard";
    public static final String REFUND_TENDER_STORECREDIT = "StoreCredit";
    public static final String RETURN_RESPONSE = "ReturnResponse";
    public static final int ENTER_AMOUNT_AND_CHOOSE = 0;
    public static final int NEXT_FOR_CREDIT_REFUND = 1;
    public static final int NEXT_FOR_HOUSE_ACCOUNT_REFUND = 3;
    public static final int NEXT_FOR_CASH_REFUND = 4;
    
    /* (non-Javadoc)
     * @see com._360commerce.tdo.TDOIfc#buildBeanModel(com.extendyourstore.foundation.tour.ifc.BusIfc)
     */
    public POSBaseBeanModel buildBeanModel(HashMap attributeMap)
    {
        // get new tender bean model
        RetailTransactionADOIfc txnADO = (RetailTransactionADOIfc)attributeMap.get(TRANSACTION);
        BusIfc bus = (BusIfc)attributeMap.get(BUS);
        MAXTenderBeanModel model = (MAXTenderBeanModel) initializeTenderBeanModel(attributeMap, txnADO, bus);

        // set the local navigation button bean model
        model.setLocalButtonBeanModel(getNavigationBeanModel(txnADO.getEnabledRefundTenderTypes(), txnADO.isHouseCardsAccepted(), bus));
        model = (MAXTenderBeanModel) setCorrectPromptAndButtons(model, txnADO, bus);
        
        // This is a return
        model.setReturn(true);

        return model;
    }
protected TenderBeanModel initializeTenderBeanModel(HashMap attributeMap, 
            RetailTransactionADOIfc txnADO, BusIfc bus)
    {
        // Get RDO version of transaction for use in some processing
        TenderableTransactionIfc txnRDO = (TenderableTransactionIfc)((ADO)txnADO).toLegacy();

        // get new tender bean model
        MAXTenderBeanModel model = new MAXTenderBeanModel();
        // populate tender bean model w/ tender and totals info
        model.setTenderLineItems(txnRDO.getTenderLineItemsVector());
        model.setTransactionTotals(txnRDO.getTenderTransactionTotals());

        if (((TenderCargo)bus.getCargo()).isGiftCardActivationsCanceled())
        {
            model.setManageClearButton(false);
        }
        
        // set customer information
        StatusBeanModel sModel = getStatusBean(bus, txnRDO.getCustomer());
        if (sModel != null)
        {
            model.setStatusBeanModel(sModel);
        }

        // set prompt not editable if this return was NOT with a receipt
        if (txnADO instanceof ReturnableTransactionADOIfc &&
            !((ReturnableTransactionADOIfc)txnADO).isReturnWithReceipt())
        {
            PromptAndResponseModel parModel = new PromptAndResponseModel();
            model.setPromptAndResponseModel(parModel);
        }
        
        return model;
    }
protected TenderBeanModel setCorrectPromptAndButtons(TenderBeanModel model, RetailTransactionADOIfc txnADO, BusIfc bus)
    {
        PromptAndResponseModel prModel = model.getPromptAndResponseModel();
        // if we haven't created one yet, then make a new one
        if (prModel == null)
        {
            prModel = new PromptAndResponseModel();
        }
        boolean setNext = false;
        UtilityManagerIfc util = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        // get the correct prompt arguments based on the refund options row
        // this row corresponds to the refund options requirements for button availability and prompt text
        switch (txnADO.getRefundOptionsRow())
        {
            case ENTER_AMOUNT_AND_CHOOSE:
                prModel.setArguments(util.retrieveText("RefundOptionsSpec", "tenderText", "EnterAmountAndChoose", "EnterAmountAndChoose"));
                break;
            case NEXT_FOR_CREDIT_REFUND:
                prModel.setArguments(util.retrieveText("RefundOptionsSpec", "tenderText", "NextForCreditRefund", "NextForCreditRefund"));
                setNext=true;
                break;
            case NEXT_FOR_CASH_REFUND:
            	// Rev 1.2 Changes for Code Merge Prod Defects
                prModel.setArguments(util.retrieveText("RefundOptionsSpec", "tenderText", "NextForCashOrChoose", "NextForCashOrChoose"));
                setNext=false;
                break;
            case NEXT_FOR_HOUSE_ACCOUNT_REFUND:
                prModel.setArguments(util.retrieveText("RefundOptionsSpec", "tenderText", "NextForHouseAccountRefund", "NextForHouseAccountRefund"));
                setNext=true;
                break;
        }

        // set next button
        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();
        if (setNext)
        {
            navModel.setButtonEnabled(CommonActionsIfc.NEXT, true);
            model.setGlobalButtonBeanModel(navModel);
        }

        if (((TenderCargo)bus.getCargo()).isGiftCardActivationsCanceled())
        {
            navModel.setButtonEnabled(CommonActionsIfc.UNDO, false);
            navModel.setButtonEnabled(CommonActionsIfc.CLEAR, false);
            model.setGlobalButtonBeanModel(navModel);
        }
        
        // set prompt and response
        model.setPromptAndResponseModel(prModel);
        return model;
    }
protected StatusBeanModel getStatusBean(BusIfc bus, CustomerIfc customer)
    {
        StatusBeanModel sModel = null;
        sModel = UIUtilities.getStatusBean((AbstractFinancialCargo) bus.getCargo());
        if (customer != null)
        {
            String[] vars = { customer.getFirstName(), customer.getLastName() };
            if(customer.isBusinessCustomer())
            {
                vars[0]=customer.getLastName();
                vars[1]="";
            }
            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            String pattern = utility.retrieveText("CustomerAddressSpec",
                                                  BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                                                  TagConstantsIfc.CUSTOMER_NAME_TAG,
                                                  TagConstantsIfc.CUSTOMER_NAME_PATTERN_TAG,
                                                  LocaleConstantsIfc.USER_INTERFACE);
            String customerName = LocaleUtilities.formatComplexMessage(pattern,vars);
            sModel.setCustomerName(customerName);
        }
        return sModel;
    }
protected NavigationButtonBeanModel getNavigationBeanModel(TenderTypeEnum[] enabledTypes, BusIfc bus)
    {
        // convert to list
        ArrayList<TenderTypeEnum> typeList = new ArrayList<TenderTypeEnum>(enabledTypes.length);
        for (int i = 0; i < enabledTypes.length; i++)
        {
            typeList.add(enabledTypes[i]);
        }
        
        List<Integer> enabledTenderTypes = new ArrayList<Integer>();
        if(typeList.contains(TenderTypeEnum.CASH))
        {
            enabledTenderTypes.add(TenderLineItemConstantsIfc.TENDER_TYPE_CASH);
        }
        if(typeList.contains(TenderTypeEnum.MAIL_CHECK))
        {
            enabledTenderTypes.add(TenderLineItemConstantsIfc.TENDER_TYPE_MAIL_BANK_CHECK);
        }
        if(typeList.contains(TenderTypeEnum.CREDIT))
        {
            enabledTenderTypes.add(TenderLineItemConstantsIfc.TENDER_TYPE_CHARGE);
        }
        if(typeList.contains(TenderTypeEnum.GIFT_CARD))
        {
            enabledTenderTypes.add(TenderLineItemConstantsIfc.TENDER_TYPE_GIFT_CARD);
        }
        if(typeList.contains(TenderTypeEnum.GIFT_CARD))
        {
            enabledTenderTypes.add(TenderLineItemConstantsIfc.TENDER_TYPE_STORE_CREDIT);
        }
        
        return getNavigationBeanModel(enabledTenderTypes, true, bus);
    }
protected NavigationButtonBeanModel getNavigationBeanModel(List<Integer> enabledTypes, boolean areHouseCardsAccpeted, BusIfc bus)
    {
        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();
        navModel.setButtonEnabled(CommonActionsIfc.CASH, enabledTypes.contains(TenderLineItemConstantsIfc.TENDER_TYPE_CASH));
        navModel.setButtonEnabled(CommonActionsIfc.MAIL_CHECK, enabledTypes.contains(TenderLineItemConstantsIfc.TENDER_TYPE_MAIL_BANK_CHECK));
        navModel.setButtonEnabled(CommonActionsIfc.CREDIT, enabledTypes.contains(TenderLineItemConstantsIfc.TENDER_TYPE_CHARGE));
        navModel.setButtonEnabled(CommonActionsIfc.GIFT_CARD, enabledTypes.contains(TenderLineItemConstantsIfc.TENDER_TYPE_GIFT_CARD));
        navModel.setButtonEnabled(CommonActionsIfc.STORE_CREDIT, enabledTypes.contains(TenderLineItemConstantsIfc.TENDER_TYPE_STORE_CREDIT));
        navModel.setButtonEnabled(CommonActionsIfc.PURCHASE_ORDER, enabledTypes.contains(TenderLineItemConstantsIfc.TENDER_TYPE_PURCHASE_ORDER));
        boolean includeHouseAccount = false;
        if (enabledTypes.contains(TenderLineItemConstantsIfc.TENDER_TYPE_CHARGE) && areHouseCardsAccpeted)
        {
            includeHouseAccount = true;
        }
        navModel.setButtonEnabled(CommonActionsIfc.HOUSEACCOUNT, includeHouseAccount);
        navModel.setButtonEnabled("EWallet", true);
        navModel.setButtonEnabled(CommonActionsIfc.CASH, true);
        return navModel;
    }
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
 // Changes starts for Rev 1.0 (Ashish : Employee discount(This file is merged with 14 version)) 
}
