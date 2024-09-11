/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *
 *	Rev 1.8		Jun 01, 2019		Purushotham Reddy   Changes  for POS-Amazon Pay Integration
 *  Rev 1.7     May 04, 2017	    Kritica Agarwal 	GST Changes
 * 	Rev	1.6	    Apr 20, 2017		Hitesh dua			bug:unexpected error while redeeming capillary ABS type coupon. 
 *	Rev	1.5 	Mar 30, 2016		Mansi Goel			Changes to resolve bill buster amount is  
 *														added twice during postvoid & reprint 
 *	Rev 1.4		Mar 20, 2017		Mansi Goel			Changes to resolve Emp discount issue for promotional discounted items 
 *	Rev 1.3   	Dec 20,2016    		Ashish Yadav    	Changes for Store credit FES
 *	Rev	1.2 	Nov 30, 2016		Mansi Goel			Changes for Discount Rule FES
 *	Rev	1.1 	Nov 08, 2016		Ashish Yadav		Changes for Home Delivery Send FES
 *	Rev 1.0		Nov 08, 2016		Nadia				Changes for MAX-StoreCredi_Return requirement.
 *
 ********************************************************************************/


package max.retail.stores.domain.factory;

//java imports
import java.util.Locale;

import max.retail.stores.domain.customer.MAXCaptureCustomer;
import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.customer.MAXTICCustomer;
import max.retail.stores.domain.customer.MAXTICCustomerIfc;
import max.retail.stores.domain.discount.MAXAdvancedPricingRule;
import max.retail.stores.domain.discount.MAXBestDealGroup;
import max.retail.stores.domain.discount.MAXDiscountListEntry;
import max.retail.stores.domain.discount.MAXItemDiscountByAmountStrategy;
import max.retail.stores.domain.discount.MAXItemDiscountByPercentageIfc;
import max.retail.stores.domain.discount.MAXItemDiscountByPercentageStrategy;
import max.retail.stores.domain.discount.MAXSourceCriteria;
import max.retail.stores.domain.discount.MAXStandardDiscountCalculation;
import max.retail.stores.domain.discount.MAXTargetCriteria;
import max.retail.stores.domain.discount.MAXTransactionDiscountAudit;
import max.retail.stores.domain.discount.MAXTransactionDiscountByAmountStrategy;
import max.retail.stores.domain.event.MAXItemPriceMaintenanceEvent;
import max.retail.stores.domain.event.MAXPriceChange;
import max.retail.stores.domain.financial.MAXFinancialCountTenderItem;
import max.retail.stores.domain.financial.MAXFinancialTotals;
import max.retail.stores.domain.financial.MAXLayaway;
import max.retail.stores.domain.financial.MAXShippingMethod;
import max.retail.stores.domain.lineitem.MAXItemContainerProxy;
import max.retail.stores.domain.lineitem.MAXItemPrice;
import max.retail.stores.domain.lineitem.MAXItemTax;
import max.retail.stores.domain.lineitem.MAXMaximumRetailPriceChange;
import max.retail.stores.domain.lineitem.MAXMaximumRetailPriceChangeIfc;
import max.retail.stores.domain.lineitem.MAXReturnItem;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItem;
import max.retail.stores.domain.manager.centralvalidation.MAXCentralizedDataEntry;
import max.retail.stores.domain.manager.centralvalidation.MAXCentralizedDataEntryIfc;
import max.retail.stores.domain.order.MAXOrder;
import max.retail.stores.domain.stock.MAXGiftCardPLUItem;
import max.retail.stores.domain.stock.MAXPLUItem;
import max.retail.stores.domain.tax.MAXTaxAssignment;
import max.retail.stores.domain.tax.MAXTaxAssignmentIfc;
import max.retail.stores.domain.tax.MAXTaxInformation;
import max.retail.stores.domain.tax.MAXTaxInformationIfc;
import max.retail.stores.domain.tender.MAXTenderAmazonPay;
import max.retail.stores.domain.tender.MAXTenderAmazonPayIfc;
import max.retail.stores.domain.tender.MAXTenderCharge;
import max.retail.stores.domain.tender.MAXTenderEComCOD;
import max.retail.stores.domain.tender.MAXTenderEComCODIfc;
import max.retail.stores.domain.tender.MAXTenderEComPrepaid;
import max.retail.stores.domain.tender.MAXTenderEComPrepaidIfc;
import max.retail.stores.domain.tender.MAXTenderLoyaltyPoints;
import max.retail.stores.domain.tender.MAXTenderLoyaltyPointsIfc;
import max.retail.stores.domain.tender.MAXTenderMobikwik;
import max.retail.stores.domain.tender.MAXTenderMobikwikIfc;
import max.retail.stores.domain.tender.MAXTenderPaytm;
import max.retail.stores.domain.tender.MAXTenderPaytmIfc;
import max.retail.stores.domain.tender.MAXTenderPurchaseOrder;
import max.retail.stores.domain.tender.MAXTenderStoreCredit;
import max.retail.stores.domain.tender.MAXTenderStoreCreditIfc;
import max.retail.stores.domain.tender.MAXTenderTypeMap;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXOrderTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSearchCriteria;
import max.retail.stores.domain.transaction.MAXSearchCriteriaIfc;
import max.retail.stores.domain.transaction.MAXTransactionTotals;
import max.retail.stores.domain.transaction.MAXTransactionTotalsIfc;
import max.retail.stores.domain.transaction.MAXVoidTransaction;
import max.retail.stores.domain.utility.MAXGiftCard;
import max.retail.stores.domain.utility.MAXStoreCredit;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.discount.BestDealGroupIfc;
import oracle.retail.stores.domain.discount.DiscountCalculationIfc;
import oracle.retail.stores.domain.discount.DiscountListEntryIfc;
import oracle.retail.stores.domain.discount.DiscountListIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByAmountIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByPercentageIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountAuditIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByAmountIfc;
import oracle.retail.stores.domain.event.ItemPriceMaintenanceEventIfc;
import oracle.retail.stores.domain.event.PriceChangeIfc;
import oracle.retail.stores.domain.factory.DomainObjectFactory;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.lineitem.ItemContainerProxyIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.shipping.ShippingMethod;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderLimitsIfc;
import oracle.retail.stores.domain.tender.TenderPurchaseOrderIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.StoreCreditIfc;


public class MAXDomainObjectFactory extends DomainObjectFactory implements MAXDomainObjectFactoryIfc {
	
	protected String factoryID = "";

	public MAXDomainObjectFactory() {
	}

	/**
	 * Constructor for a factory with a given default locale
	 * 
	 * @param locale
	 *            Default locale this object should use
	 */
	public MAXDomainObjectFactory(Locale locale) {
		super(locale);
	}

	public CustomerIfc getCustomerInstance(Locale locale) {
		return new MAXCustomer();
	}

	public CustomerIfc getCustomerInstance() {
		return new MAXCustomer();
	}

	public PLUItemIfc getPLUItemInstance() {
		return new MAXPLUItem();
	}

	public PLUItemIfc getPLUItemInstance(Locale locale) {
		return new MAXPLUItem();
	}

	
	public SaleReturnTransactionIfc getSaleReturnTransactionInstance() {
		return new MAXSaleReturnTransaction();
	}

	public SaleReturnTransactionIfc getSaleReturnTransactionInstance(Locale locale) {
		return new MAXSaleReturnTransaction();
	}

	public CaptureCustomerIfc getCaptureCustomerInstance(Locale locale) {
		return new MAXCaptureCustomer();
	}

	public CaptureCustomerIfc getCaptureCustomerInstance() {
		return new MAXCaptureCustomer();
	}

	// Changes starts for Rev 1.1(Send)
	public ShippingMethodIfc getShippingMethodInstance() {
		return (ShippingMethod) new MAXShippingMethod();
	}

	// Changes ends for Rev 1.1(Send)

	public GiftCardIfc getGiftCardInstance(Locale locale) {
		return new MAXGiftCard();
	}

	public GiftCardPLUItemIfc getGiftCardPLUItemInstance(Locale locale) {
		return new MAXGiftCardPLUItem();
	}

	public SaleReturnLineItemIfc getSaleReturnLineItemInstance(Locale locale) {
		return new MAXSaleReturnLineItem();
	}

	public TenderChargeIfc getTenderChargeInstance(Locale locale) {
		return new MAXTenderCharge();
	}
	
	public OrderTransactionIfc getOrderTransactionInstance() {
		return new MAXOrderTransaction();
	}

	public OrderIfc getOrderInstance() {
		return new MAXOrder();
	}

	public TenderStoreCreditIfc getTenderStoreCreditInstance(Locale locale) {
		return new MAXTenderStoreCredit();
	}

	public TenderPurchaseOrderIfc getTenderPurchaseOrderInstance() {
		return getTenderPurchaseOrderInstance(getLocale());
	}

	public TenderPurchaseOrderIfc getTenderPurchaseOrderInstance(Locale locale) {
		return new MAXTenderPurchaseOrder();
	}

	public MAXTenderLoyaltyPointsIfc getTenderLoyaltyPointsInstance() {
		return getTenderLoyaltyPointsInstance(getLocale());
	}

	public MAXTenderLoyaltyPointsIfc getTenderLoyaltyPointsInstance(Locale locale) {
		return new MAXTenderLoyaltyPoints();
	}

	public TenderTypeMapIfc getTenderTypeMapInstance() {
		return getTenderTypeMapInstance(getLocale());
	}

	public TenderTypeMapIfc getTenderTypeMapInstance(Locale locale) {
		return (TenderTypeMapIfc) MAXTenderTypeMap.getTenderTypeMap();
	}

	public ReturnItemIfc getReturnItemInstance() {
		return getReturnItemInstance(getLocale());
	}

	public ReturnItemIfc getReturnItemInstance(Locale locale) {
		return new MAXReturnItem();
	}

	public DiscountListIfc getSourceCriteriaInstance(Locale locale) {
		return new MAXSourceCriteria();
	}

	public BestDealGroupIfc getBestDealGroupInstance(Locale locale) {
		return new MAXBestDealGroup();
	}

	public DiscountListIfc getTargetCriteriaInstance(Locale locale) {
		return new MAXTargetCriteria();
	}

	public DiscountListEntryIfc getDiscountListEntryInstance(Locale locale) {
		return new MAXDiscountListEntry();
	}

	public AdvancedPricingRuleIfc getAdvancedPricingRuleInstance(Locale locale) {
		return new MAXAdvancedPricingRule();
	}

	public AdvancedPricingRuleIfc getAdvancedPricingRuleInstance() {
		return new MAXAdvancedPricingRule();
	}

	public ItemContainerProxyIfc getItemContainerProxyInstance() {
		return new MAXItemContainerProxy();
	}

	public ItemContainerProxyIfc getItemContainerProxyInstance(Locale locale) {
		return new MAXItemContainerProxy();
	}

	public ItemPriceIfc getItemPriceInstance() {
		return new MAXItemPrice();
	}

	public ItemPriceIfc getItemPriceInstance(Locale locale) {
		return new MAXItemPrice();
	}

	public FinancialTotalsIfc getFinancialTotalsInstance() {
		return getFinancialTotalsInstance(getLocale());
	}

	public FinancialTotalsIfc getFinancialTotalsInstance(Locale locale) {
		return new MAXFinancialTotals();
	}
	
	public LayawayTransactionIfc getLayawayTransactionInstance() {
		return getLayawayTransactionInstance(getLocale());
	}

	public LayawayTransactionIfc getLayawayTransactionInstance(Locale locale) {
		return new MAXLayawayTransaction();
	}

	public FinancialCountTenderItemIfc getFinancialCountTenderItemInstance() {
		return getFinancialCountTenderItemInstance(getLocale());
	}

	public FinancialCountTenderItemIfc getFinancialCountTenderItemInstance(Locale locale) {
		return new MAXFinancialCountTenderItem();
	}

	public MAXTaxAssignmentIfc getTaxAssignmentInstance() {
		return new MAXTaxAssignment();
	}

	public VoidTransactionIfc getVoidTransactionInstance() {
		return getVoidTransactionInstance(getLocale());
	}

	public VoidTransactionIfc getVoidTransactionInstance(Locale locale) {
		return new MAXVoidTransaction();
	}

	public LayawayIfc getLayawayInstance() {
		return new MAXLayaway();
	}

	public MAXTICCustomerIfc getTICCustomerInstance(Locale locale) {
		return new MAXTICCustomer();
	}

	public MAXTICCustomerIfc getTICCustomerInstance() {
		return getTICCustomerInstance(getLocale());
	}

	public MAXTenderEComPrepaidIfc getTenderEComPrepaidInstance() {
		return getTenderEComPrepaidInstance(getLocale());
	}

	public MAXTenderEComPrepaidIfc getTenderEComPrepaidInstance(Locale locale) {
		return new MAXTenderEComPrepaid();
	}

	public MAXTenderEComCODIfc getTenderEComCODInstance() {
		return getTenderEComCODInstance(getLocale());
	}

	public MAXTenderEComCODIfc getTenderEComCODInstance(Locale locale) {
		return new MAXTenderEComCOD();
	}

	@Override
	public MAXCentralizedDataEntryIfc getCentralizedDataEntryInstance(Locale locale) {
		return new MAXCentralizedDataEntry();
	}

	public MAXMaximumRetailPriceChangeIfc getMaximumRetailPriceChangeInstance() {
		return new MAXMaximumRetailPriceChange();
	}

	public PriceChangeIfc getPriceChangeInstance(Locale locale) {
		return new MAXPriceChange();
	}

	public ItemTaxIfc getItemTaxInstance(Locale locale) {
		return new MAXItemTax();
	}

	public ItemPriceMaintenanceEventIfc getItemPriceMaintenanceEventInstance(Locale locale) {
		return new MAXItemPriceMaintenanceEvent();
	}

	public MAXTaxInformationIfc getTaxInformationInstance() {
		return getTaxInformationInstance(getLocale());
	}

	public MAXTaxInformationIfc getTaxInformationInstance(Locale locale) {

		return new MAXTaxInformation();
	}

	public MAXTransactionTotalsIfc getTransactionTotalsInstance() {
		return getTransactionTotalsInstance(getLocale());
	}

	public MAXTransactionTotalsIfc getTransactionTotalsInstance(Locale locale) {
		return new MAXTransactionTotals();
	}

	//Changes for Rev 1.2 : Starts
	public ItemDiscountByAmountIfc getItemDiscountByAmountInstance(Locale locale) {
		return new MAXItemDiscountByAmountStrategy();
	}

	public ItemDiscountByPercentageIfc getItemDiscountByPercentageInstance(Locale locale) {
		return new MAXItemDiscountByPercentageStrategy();
	}
	//Changes for Rev 1.2 : Ends
	
	public MAXItemDiscountByPercentageIfc getItemDiscountByPercentageInstance()
    {
        return (MAXItemDiscountByPercentageIfc) new MAXItemDiscountByPercentageStrategy();
    }
		// Changes start for Rev 1.3 (Ashish : Store credit)
	public StoreCreditIfc getStoreCreditInstance(Locale locale)
	{
		return new MAXStoreCredit();
	}

	public StoreCreditIfc getStoreCreditInstance()
	{
		return getStoreCreditInstance(getLocale());
	}
	// Changes ends for Rev 1.3 (Ashish : Store credit)
	
	//Changes for Rev 1.4 : Starts
	public DiscountCalculationIfc getDiscountCalculationInstance(Locale locale) {
		return new MAXStandardDiscountCalculation();
	}
	//Changes for Rev 1.4 : Ends

	//Changes for Rev 1.5 : Starts
	public TransactionDiscountAuditIfc getTransactionDiscountAuditInstance(Locale locale) {
		return new MAXTransactionDiscountAudit();
	}
	//Changes for Rev 1.5 : Ends

	   /**
     * Returns instance of TransactionDiscountByAmountIfc class. <P>
     *
     * @param locale Locale to get an object for.
     * @return TransactionDiscountByAmountIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTransactionDiscountByAmountInstance(java.util.Locale)
     */
	//changes for rev 1.6 start
	public TransactionDiscountByAmountIfc getTransactionDiscountByAmountInstance(Locale locale)
    {
        return new MAXTransactionDiscountByAmountStrategy();
}
	//changes for rev 1.6 end
	//changes for rev 1.7 Starts
	
	public MAXSearchCriteriaIfc getSearchCriteriaInstance()
    {
        return  getSearchCriteriaInstance(getLocale());
    }
    
	public MAXSearchCriteriaIfc getSearchCriteriaInstance(Locale locale)
	{
		return new MAXSearchCriteria();
	}
	// Changes start by Bhanu Priya 
	@Override
	public MAXTenderPaytmIfc getTenderPaytmInstance() {
		return getTenderPaytmInstance(getLocale());
	}

	public MAXTenderPaytmIfc getTenderPaytmInstance(Locale locale) {
		return new MAXTenderPaytm();
	}
	//changes for rev 1.7 Starts
	// Changes End by Bhanu Priya 

	@Override
	public MAXTenderMobikwikIfc getTenderMobikwikInstance() {
		return getTenderMobikwikInstance(getLocale());
	}

	public MAXTenderMobikwikIfc getTenderMobikwikInstance(Locale locale) {
		return new MAXTenderMobikwik();
		// Changes End by Bhanu Priya 
	}
	//  Changes  for POS-Amazon Pay Integration @Purushotham Reddy
	@Override
	public MAXTenderAmazonPayIfc getTenderAmazonPayInstance() {
		return getTenderAmazonPayInstance(getLocale());
	}

	public MAXTenderAmazonPayIfc getTenderAmazonPayInstance(Locale locale) {
		return new MAXTenderAmazonPay();
	}
	/*
	 * Added Manager Override attribute for Credit Note Central Failure
	 * requirement instead of Central Local Fail over.
	 * 
	 * Manager Override Successful in case of INVALID credit Note accepted
	 * <Start>
	 */

	/**
	 * Returns instance of TenderStoreCreditIfc class.
	 * <P>
	 * 
	 * @param locale
	 *            Locale to get an object for.
	 * @return TenderStoreCreditIfc instance
	 * @see com.extendyourstore.domain.factory.DomainObjectFactoryIfc#getTenderStoreCreditInstance(java.util.Locale)
	 */
	public MAXTenderStoreCreditIfc getTenderStoreCreditInstance1(Locale locale) {
		return new MAXTenderStoreCredit();
	}

	/*
	 * Added Manager Override attribute for Credit Note Central Failure
	 * requirement instead of Central Local Fail over.
	 * 
	 * Manager Override Successful in case of INVALID credit Note accepted <End>
	 */
}