/**************************************************************xxxxxxx******************

 * Copyright (c) 2018-19 MAX Hyper Market Inc.    All Rights Reserved.
 * 
 * Rev 2.8  	June 20 2019		Purushotham Reddy 	     Changes for POS-Amazon Pay Integration 
 * Rev 2.7  	Feb 07,2019    		Purushotham Reddy		 Changes for Re-print Sale Receipt for Weighted items Promo 
 * 
 * Rev 2.6  	July 03,2017    	Nayya Gupta			issue 28: Item level send is printing wrong total tender and Tax. 
 * 														It is not printing items which is not applicable for item level send. 
 * Rev 2.3  	hitesh dua   22 June,2017  Item grouping for promo items.
 * Rev 2.5  	hitesh dua   31 May,2017 By default “NA” is printing after last name in bill copy  
 * Rev 2.4      Jun 17,2017         Nayya Gupta			Non-taxable item changes
 * Rev 2.3		Jun 16, 2017		Jyoti Yadav 		Reprint issues
 * Rev 2.2		May 04, 2017		Kritica Agarwal 	GST Changes
 * Rev 2.1  hitesh dua   19 Apr,2017 
 * item clubbing is not happing properly for discounted items. 
 *
 * Rev 2.1  hitesh dua   13 Apr,2017 
 * bug_fix:employee discount on capillary items, other discount and employee discount was printing wrong and removed extra code 
 * 
 * Rev 2.0  Nitika Arora   11 Apr,2017 
 * Code done for printing the Refund Amount on the receipt for Ecom functionality and commented the check for promotions for grouping the items 
 * 
 * Rev 1.9  Hitesh Dua   29 Mar,2017 
 * for employee discount, hire purchase receipt auto cut and print bill buster   
 *
 * Rev 1.8  Nitika Arora   22 Mar,2017 
 * Code done for EComPrepaid and EComCOD tender functionality.
 *
 * Rev 1.7 Nitika Arora    20 Mar 2017
 * Changes for fixing the discount column in case of promotions.
 *
 * Rev 1.6  Hitesh.dua 		17 Mar,2017 
 * Bug_fix: disc avl amnt is prnting wrong in the receipt
 * bug_fix: if we apply only emp disc, other disc also printing in the receipt
 *
 * Rev 1.5  Hitesh.dua 		15 Mar,2017 	Capillary coupon receipt
 * 
 * Rev 1.4  Hitesh.dua 		09 Mar,2017
 * Bug: For 0% tax in tax breakup taxable amount should be zero.  
 * Bug: line item no is printing wrong.
 * Bug: Gift Cert. Total Amount and Total Rounded is printing wrong.
 *
 * Rev 1.3 Nitika Arora    01 Mar 2017
 * Changes for printing the csp mrp difference on discount column of receipt.
 *
 * Rev 1.2 Hitesh.dua 		21feb,2017	
 * changes for gift card and receipt footer
 * created method for rounding amount getTotalRoundedAmount
 * Bug_fix: In send receipt line item is printing wrong 
 *
 * Rev 1.1 Hitesh.dua 		07feb,2017	
 * Item Grouping is not happening in Receipt. 
 *
 * Rev 1.0 Hitesh.dua 		15dec,2016	Initial revision.
 * changes for printing customized receipt. 
 * ===========================================================================
 */

package max.retail.stores.pos.receipt;

import java.math.BigDecimal; 
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import max.retail.stores.domain.MAXAmazonPayResponse;
import max.retail.stores.domain.MAXMobikwikResponse;
import max.retail.stores.domain.bakery.MAXBakeryItemIfc;
import max.retail.stores.domain.customer.MAXCustomerConstantsIfc;
import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.customer.MAXTICCustomerIfc;
import max.retail.stores.domain.discount.MAXDiscountRuleConstantsIfc;
import max.retail.stores.domain.discount.MAXItemDiscountByAmountStrategy;
import max.retail.stores.domain.discount.MAXItemDiscountByPercentageStrategy;
import max.retail.stores.domain.factory.MAXDomainObjectFactory;
import max.retail.stores.domain.gstin.MAXGSTINValidationResponseIfc;
import max.retail.stores.domain.lineitem.MAXItemPriceIfc;
import max.retail.stores.domain.lineitem.MAXItemTaxIfc;
import max.retail.stores.domain.lineitem.MAXLineItemTaxBreakUpDetail;
import max.retail.stores.domain.lineitem.MAXLineItemTaxBreakUpDetailIfc;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItem;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.mcoupon.MAXMcouponIfc;
import max.retail.stores.domain.paytm.MAXPaytmQRCodeResponse;
import max.retail.stores.domain.MAXPaytmResponse;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.domain.tax.MAXTaxAssignmentIfc;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.domain.transaction.MAXVoidTransaction;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByAmountStrategy;
import oracle.retail.stores.domain.discount.ItemDiscountByFixedPriceStrategy;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAudit;
import oracle.retail.stores.domain.discount.ReturnItemTransactionDiscountAudit;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.PriceAdjustmentLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.SendPackageLineItemIfc;
import oracle.retail.stores.domain.tax.TaxInformationContainer;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderCashIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransaction;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransaction;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.receipt.ReceiptParameterBean;
import oracle.retail.stores.pos.receipt.SendPackInfo;
import oracle.retail.stores.pos.receipt.VATHelper;

/**
 * Simple bean used to pass receipt controlling parameters from Site code
 *
 * @author epd
 */
/**
 * @author Hitesh.Dua
 *
 */
public class MAXReceiptParameterBean extends ReceiptParameterBean implements
		MAXReceiptParameterBeanIfc {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1326684038544256804L;
	
	CurrencyIfc transCouponDiscountByPerc=null;
	CurrencyIfc itemCouponDiscountByPerc=null;

	 protected boolean printAddDetailsEcomReceipt = false;
	 
	 protected MAXPaytmQRCodeResponse paytmQRCodeResponse;
	 
	public boolean isPrintAddDetailsEcomReceipt() {
		return printAddDetailsEcomReceipt;
	}

	public void setPrintAddDetailsEcomReceipt(boolean printAddDetailsEcomReceipt) {
		this.printAddDetailsEcomReceipt = printAddDetailsEcomReceipt;
	}

	/**
	 * store loyaltyPointsSlipFooter parameter of application.xml
	 */
	String[] loyaltyPointsSlipFooter;
	
	/* (non-Javadoc)
	 * @see max.retail.stores.pos.receipt.maxreceiptifc#getLoyaltyPointsSlipFooter()
	 */
	@Override
	public String[] getLoyaltyPointsSlipFooter() {
		return loyaltyPointsSlipFooter;
	}

	/* (non-Javadoc)
	 * @see max.retail.stores.pos.receipt.maxreceiptifc#setLoyaltyPointsSlipFooter(java.lang.String[])
	 */
	@Override
	public void setLoyaltyPointsSlipFooter(String[] loyaltyPointsSlipFooter) {
		this.loyaltyPointsSlipFooter = loyaltyPointsSlipFooter;
	}

	/**
	 * store no. of loyalty redeem points
	 */
	String loyaltyRedeemedPoints;
	
	/* 
	 * @see max.retail.stores.pos.receipt.MAXReceiptParameterBeanIfc#getLoyaltyRedeemedPoints()
	 */
	/* (non-Javadoc)
	 * @see max.retail.stores.pos.receipt.maxreceiptifc#getLoyaltyRedeemedPoints()
	 */
	@Override
	public String getLoyaltyRedeemedPoints() {
		return loyaltyRedeemedPoints;
	}

	/* 
	 * @see max.retail.stores.pos.receipt.MAXReceiptParameterBeanIfc#setLoyaltyRedeemedPoints(java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see max.retail.stores.pos.receipt.maxreceiptifc#setLoyaltyRedeemedPoints(java.lang.String)
	 */
	@Override
	public void setLoyaltyRedeemedPoints(String loyaltyRedeemedPoints) {
		this.loyaltyRedeemedPoints = loyaltyRedeemedPoints;
	}

	/**
	 * store no of redeemed loyalty amount   
	 */
	String loyaltyRedeemedAmount;
	
	/* 
	 * @see max.retail.stores.pos.receipt.MAXReceiptParameterBeanIfc#getLoyaltyRedeemedAmount()
	 */
	/* (non-Javadoc)
	 * @see max.retail.stores.pos.receipt.maxreceiptifc#getLoyaltyRedeemedAmount()
	 */
	@Override
	public String getLoyaltyRedeemedAmount() {
		return loyaltyRedeemedAmount;
	}

	/* 
	 * @see max.retail.stores.pos.receipt.MAXReceiptParameterBeanIfc#setLoyaltyRedeemedAmount(java.lang.String)
	 */
	/* (non-Javadoc)
	 * @see max.retail.stores.pos.receipt.maxreceiptifc#setLoyaltyRedeemedAmount(java.lang.String)
	 */
	@Override
	public void setLoyaltyRedeemedAmount(String loyaltyRedeemedAmount) {
		this.loyaltyRedeemedAmount = loyaltyRedeemedAmount;
	}

	/* (non-Javadoc)
	 * @see max.retail.stores.pos.receipt.maxreceiptifc#getVATHelper()
	 */
	@Override
	public VATHelper getVATHelper() {
		if (vatHelper == null) {
			vatHelper = new MAXVATHelper((RetailTransactionIfc) transaction);
		}
		return vatHelper;
	}

	/* retrive customer mobile no first by MOBILE type and then HOME type
	 * @see max.retail.stores.pos.receipt.MAXReceiptParameterBeanIfc#getPhoneByMobileType()
	 */
	/* (non-Javadoc)
	 * @see max.retail.stores.pos.receipt.maxreceiptifc#getPhoneByMobileType()
	 */
	@Override
	public String getPhoneByMobileType() {
		String mobile = "";
		if (((TenderableTransactionIfc) transaction).getCustomer() != null
				&& ((TenderableTransactionIfc) transaction).getCustomer() instanceof MAXCustomerIfc) {
			MAXCustomerIfc customer = (MAXCustomerIfc) ((TenderableTransactionIfc) transaction)
					.getCustomer();
			if (customer.getPhoneByType(PhoneConstantsIfc.PHONE_TYPE_MOBILE) != null)

				mobile = customer.getPhoneByType(
						PhoneConstantsIfc.PHONE_TYPE_MOBILE).getPhoneNumber();

			else if (customer.getPhoneByType(PhoneConstantsIfc.PHONE_TYPE_MOBILE) != null) 
				
				mobile = customer.getPhoneByType(
						PhoneConstantsIfc.PHONE_TYPE_HOME).getPhoneNumber();
			
			//set mobile no if card less customer is in transaction
			if(customer.getMAXTICCustomer()!=null && customer.getMAXTICCustomer().getCustomerType().equalsIgnoreCase(
					MAXCustomerConstantsIfc.CRM) && customer.getMAXTICCustomer() instanceof MAXTICCustomerIfc){
				//setting value for customer
				mobile=((MAXTICCustomerIfc)customer.getMAXTICCustomer()).getTICMobileNumber();
			}	

		}
		return mobile;
	}

	//changes for rev 1.9 start
	/* Calculate Available Amount :
	 * @see max.retail.stores.pos.receipt.MAXReceiptParameterBeanIfc#getTotalAmountForEmployeeDiscount()
	 */
	/* (non-Javadoc)
	 * @see max.retail.stores.pos.receipt.maxreceiptifc#getTotalAmountForEmployeeDiscount()
	 */
	@Override
	public CurrencyIfc getTotalAmountForEmployeeDiscount() {
		if (transaction instanceof SaleReturnTransactionIfc
				&& ((SaleReturnTransactionIfc) transaction)
						.getEmployeeDiscountID() != null) {
			Double extendedDiscountedSellingPrice = 0.0;
			Vector lineItemVector = ((SaleReturnTransactionIfc) transaction)
					.getItemContainerProxy().getLineItemsVector();
			for (Object lineItemObject : lineItemVector) {
				MAXSaleReturnLineItem lineItem = (MAXSaleReturnLineItem) lineItemObject;
				AdvancedPricingRuleIfc[] advancedPricingRuleArray=(AdvancedPricingRuleIfc[])lineItem.getPLUItem().getAdvancedPricingRules();
				if(advancedPricingRuleArray.length==0 && (lineItem.getBdwList()==null ||lineItem.getBdwList()!=null && lineItem.getBdwList().size()==0)){
	
				extendedDiscountedSellingPrice = extendedDiscountedSellingPrice
						+ lineItem.getItemPrice()
								.getExtendedDiscountedSellingPrice()
								.getDoubleValue();
			}
			}
			//changes for rev 1.6
			return DomainGateway
					.getBaseCurrencyInstance(extendedDiscountedSellingPrice.toString());
		} else
			return DomainGateway.getBaseCurrencyInstance();
	}
	
	//changes for rev 1.9 end

	/* if transaction doesn't have send item then normal item should print else not.
	 * @see max.retail.stores.pos.receipt.MAXReceiptParameterBeanIfc#isNormalItemShouldPrint()
	 */
	/* (non-Javadoc)
	 * @see max.retail.stores.pos.receipt.maxreceiptifc#isNormalItemShouldPrint()
	 */
	@Override
	public boolean isNormalItemShouldPrint() {
		return !isTransactionHasSendItem();
	}

	/* Check whether loyalty customer is link or not
	 * @see max.retail.stores.pos.receipt.MAXReceiptParameterBeanIfc#printLoyaltyCustomer()
	 */
	/* (non-Javadoc)
	 * @see max.retail.stores.pos.receipt.maxreceiptifc#printLoyaltyCustomer()
	 */
	@Override
	public boolean printLoyaltyCustomer() {

		MAXCustomerIfc customer = (MAXCustomerIfc) ((TenderableTransactionIfc) transaction)
				.getCustomer();
		if (customer!=null && customer.getCustomerType().equalsIgnoreCase(
				MAXCustomerConstantsIfc.CRM)) 
			return true;
		//check for cardless customer
		else if(customer.getMAXTICCustomer()!=null && customer.getMAXTICCustomer().getCustomerType().equalsIgnoreCase(
				MAXCustomerConstantsIfc.CRM) && customer.getMAXTICCustomer() instanceof MAXTICCustomerIfc){
			//setting value for customer
			MAXTICCustomerIfc tic= (MAXTICCustomerIfc)customer.getMAXTICCustomer();
			customer.setCustomerID(tic.getTICCustomerID());
			customer.setCustomerName(tic.getTICFirstName() + " "+ tic.getTICLastName());
			
			return true;
		}
		else
			return false;
	}


	/* (non-Javadoc)
	 * @see max.retail.stores.pos.receipt.maxreceiptifc#getTotalSavings()
	 */
	@Override
	public CurrencyIfc getTotalSavings() {

		CurrencyIfc totalMrpAmount = DomainGateway.getBaseCurrencyInstance();
		CurrencyIfc totalSavedAmount = DomainGateway.getBaseCurrencyInstance();
		CurrencyIfc totalRetailAmt = DomainGateway.getBaseCurrencyInstance();
		CurrencyIfc diffInMRPCSP = DomainGateway.getBaseCurrencyInstance(); // Added
																			// By
																			// Abhishek

		MAXSaleReturnLineItemIfc item = null;
		for (Enumeration e = ((RetailTransactionIfc) transaction)
				.getLineItemsVector().elements(); e.hasMoreElements();) {

			item = (MAXSaleReturnLineItemIfc) e.nextElement();
			if (item.getPLUItem() instanceof MAXPLUItemIfc) {
				MAXPLUItemIfc pluItem = (MAXPLUItemIfc) item.getPLUItem();

				if (!item.isReturnLineItem()
						&& pluItem.getMaximumRetailPrice().signum() > 0
						&& item.getExtendedDiscountedSellingPrice().signum() >= 0) {
					totalMrpAmount = totalMrpAmount.add(pluItem
							.getMaximumRetailPrice().multiply(
									item.getItemQuantityDecimal().abs()));
					totalRetailAmt = totalRetailAmt.add(item
							.getExtendedDiscountedSellingPrice().abs());
				}
				CurrencyIfc csp = item.getItemPrice().getSellingPrice();
				CurrencyIfc mrp = pluItem.getMaximumRetailPrice();
				if (!csp.toString().equalsIgnoreCase(mrp.toString())) {
					CurrencyIfc diffInMRPCSPPerItem = mrp.subtract(csp);

					if (diffInMRPCSPPerItem.getDoubleValue() < 0) {
						diffInMRPCSPPerItem.setZero();
					} else if (diffInMRPCSPPerItem.getDoubleValue() < 0.05
							&& item.isUnitOfMeasureItem()) {
						diffInMRPCSPPerItem.setZero();

					}

					diffInMRPCSP = diffInMRPCSP.add(diffInMRPCSPPerItem
							.multiply(item.getItemQuantityDecimal()));
				}
			}
		}
		totalSavedAmount = ((TenderableTransactionIfc) getTransaction())
				.getTransactionTotals().getDiscountTotal().add(diffInMRPCSP);
		// Don't Display Negative Savings
		if (totalSavedAmount.signum() < 0) {
			totalSavedAmount = DomainGateway.getBaseCurrencyInstance();
		}
		return totalSavedAmount;

	}

	/* (non-Javadoc)
	 * @see max.retail.stores.pos.receipt.maxreceiptifc#getTotalAmount()
	 */
	@Override
	public CurrencyIfc getTotalAmount() {
/*
		CurrencyIfc totalRetailAmt = DomainGateway.getBaseCurrencyInstance();
		//((MAXSaleReturnTransaction)transaction).getItemContainerProxy()
		MAXSaleReturnLineItemIfc item = null;
		for (Enumeration e = ((RetailTransactionIfc) transaction)
				.getLineItemsVector().elements(); e.hasMoreElements();) {
			//changes for rev 1.4 : Gift Cert. Total Amount and Total Rounded is printing wrong.
			item = (MAXSaleReturnLineItemIfc) e.nextElement();
			totalRetailAmt = totalRetailAmt.add(item
						.getExtendedDiscountedSellingPrice().abs());
		}
*/		return ((TenderableTransactionIfc)getTransaction()).getTransactionTotals().getSubtotal().subtract(((TenderableTransactionIfc)getTransaction()).getTransactionTotals().getDiscountTotal()).abs();
	}

	/* having cappilary coupon details.
	 * @see max.retail.stores.pos.receipt.MAXReceiptParameterBeanIfc#getCapillaryCoupon()
	 */
	/* (non-Javadoc)
	 * @see max.retail.stores.pos.receipt.maxreceiptifc#getCapillaryCoupon()
	 */
	@Override
	public Vector getCapillaryCoupon(){
		Vector capillaryCouponVector=null;
		
		TenderableTransactionIfc transaction1=(TenderableTransactionIfc)transaction;
		if(transaction instanceof VoidTransaction )
			transaction1=((VoidTransaction) transaction).getOriginalTransaction();
		
		if (transaction instanceof MAXSaleReturnTransactionIfc ) {
			MAXSaleReturnTransactionIfc saleReturnTransaction = (MAXSaleReturnTransactionIfc) transaction1;
			 capillaryCouponVector = saleReturnTransaction
					 .getCapillaryCouponsApplied();
		
		}
		else if (transaction instanceof MAXLayawayTransaction ) {
			MAXLayawayTransaction saleReturnTransaction = (MAXLayawayTransaction) transaction1;
			 capillaryCouponVector = saleReturnTransaction
					.getCapillaryCouponsApplied();
		}
		
		return capillaryCouponVector;	
	}


	/*
	 * customized for printing loyalty points tender in case of void transaction 
	 * @see oracle.retail.stores.pos.receipt.ReceiptParameterBean#getTenders()
	 * 
	 */
	/* (non-Javadoc)
	 * @see max.retail.stores.pos.receipt.maxreceiptifc#getTenders()
	 */
	@Override
	public TenderLineItemIfc[] getTenders()
	  {
		TenderLineItemIfc[] tenderItem=null;
		 if (getTransaction().getTransactionType() == TransactionIfc.TYPE_VOID){
			 tenderItem= ((VoidTransaction)getTransaction()).getOriginalTransaction().getTenderLineItems();
			 for (TenderLineItemIfc tenderLineItem : tenderItem) {
				tenderLineItem.setAmountTender(tenderLineItem.getAmountTender().negate());
			}
		 }
		 else
	    	  tenderItem=getTenders2();
		
		return tenderItem;
	  }
	
	public TenderLineItemIfc[] getTenders2() {
		if (this.tenders == null) {
			if (this.getTransaction().getTransactionType() == 25) {
				this.tenders = ((TenderableTransactionIfc) this.getTransaction()).getTenderLineItems();
			} else {
				TenderCashIfc cash = null;
				final Vector<TenderLineItemIfc> tenderLineItemsVector = (Vector<TenderLineItemIfc>) ((TenderableTransactionIfc) this
						.getTransaction()).getTenderLineItemsVector();
				final List<TenderLineItemIfc> list = new ArrayList<TenderLineItemIfc>(tenderLineItemsVector.size());
				for (TenderLineItemIfc tender : tenderLineItemsVector) {
					if (tender.isCollected()) {
						if (tender instanceof TenderCashIfc && tender.getAmountTender().signum() > -1
								&& tender instanceof TenderAlternateCurrencyIfc
								&& ((TenderAlternateCurrencyIfc) tender).getAlternateCurrencyTendered() == null) {
							if (cash != null) {
								cash.setAmountTender(cash.getAmountTender().add(tender.getAmountTender()));
								continue;
							}
							tender = (TenderLineItemIfc) tender.clone();
							cash = (TenderCashIfc) tender;
						}
						list.add(tender);
					} else {
						if (this.getTransaction().getTransactionType() != 22 || tender.isCollected()) {
							continue;
						}
						list.add(tender);
					}
				}
				this.tenders = list.toArray(new TenderLineItemIfc[list.size()]);
			}
		}
		return this.tenders;
	}


	//changes for rev 2.2 used MAXCleanReceiptItem instead of CleanReceiptItem
	 public AbstractTransactionLineItemIfc[] getLineItems()
	    {
	        // lazily init the line items
	        if (lineItems == null && getTransaction() instanceof RetailTransactionIfc)
	        {
	            RetailTransactionIfc retailTransaction = (RetailTransactionIfc)getTransaction();
	            Vector<AbstractTransactionLineItemIfc> actualLineItems = retailTransaction.getLineItemsVector();
	            @SuppressWarnings("rawtypes")
	            List<AbstractTransactionLineItemIfc> rearrangedLineItems = (List)actualLineItems.clone();
	            ArrayList<Integer> compressedLineItemsIndexes = new ArrayList<Integer>();

	            // Check if Parameter is set to group Like Items on receipt.
	            if (isGroupLikeItems())
	            {
	                // create a list of first occurrences of unique items
	                List<MAXCleanReceiptItem> uniqueItems = new ArrayList<MAXCleanReceiptItem>();

	                for (int i = 0; i < actualLineItems.size(); i++)
	                {
	                    if (actualLineItems.get(i) instanceof SaleReturnLineItemIfc)
	                    {	//changes for rev 2.3
	                    	//Change for Rev 2.2 : starts
	                        MAXSaleReturnLineItemIfc saleLineItem = (MAXSaleReturnLineItemIfc)actualLineItems.get(i);
	                      //Change for Rev 2.2 : Ends
	                        // continue if serialized items and gift cards
	                        //changes for rev 1.1 
	                        if (saleLineItem.isSerializedItem()
	                                || saleLineItem.isGiftItem()
	                               /* || (saleLineItem.getAdvancedPricingDiscount() != null && saleLineItem
	                                        .getAdvancedPricingDiscount().isAdvancedPricingRule())*/)
	                        {
	                        	//Change for Rev 2.2 : Starts
	                        	if(saleLineItem.getPLUItem() != null && saleLineItem.getPLUItem() instanceof MAXPLUItemIfc)
									saleLineItem.setHSNNumber(((MAXPLUItemIfc)saleLineItem.getPLUItem()).getHsnNum());	
								/*else if(saleLineItem.getPLUItem() != null && saleLineItem.getPLUItem() instanceof MAXGiftCardPLUItemIfc){
									saleLineItem.setHSNNumber(((MAXGiftCardPLUItemIfc)saleLineItem.getPLUItem()).getHsnNum());
								}*/
	                        	//Change for Rev 2.2 : Ends
	                            continue;
	                        }

	                        // find this sale item in the list of unique items
							int indexInUniques = uniqueItems.indexOf(new MAXCleanReceiptItem(saleLineItem,getTransaction()));

	                        // check if we've found an item that equals this unique item
	                        if (indexInUniques == -1)
	                        {
	                            int indexInRearranged = rearrangedLineItems.indexOf(saleLineItem);
	                            // clone this lineitem so we can modify its quantity
								//changes for rev 2.3 
	                            saleLineItem = (MAXSaleReturnLineItemIfc)saleLineItem.clone();
	                            // replace its double in the cloned list
	                            rearrangedLineItems.set(indexInRearranged, saleLineItem);
	                            // add this first unique item
	                            uniqueItems.add(new MAXCleanReceiptItem(saleLineItem,getTransaction()));
	                        }
	                        else
	                        {
							//changes for rev 2.3 start
	                    		CurrencyIfc disc = DomainGateway.getBaseCurrencyInstance();
	                    		CurrencyIfc oldDiscount = null;
	                    		CurrencyIfc newDiscount = null;
	                            // get the previous item and update its quantity
	                            MAXCleanReceiptItem previousReceiptItem = uniqueItems.get(indexInUniques);
	                            MAXSaleReturnLineItemIfc uniqueItem = (MAXSaleReturnLineItemIfc)previousReceiptItem.getSaleReturnLineItem();
	                            BigDecimal oldQuantity = uniqueItem.getItemQuantityDecimal();
	                            BigDecimal addQuantity = saleLineItem.getItemQuantityDecimal();
	                            BigDecimal newQuantity = oldQuantity.add(addQuantity);
//	                            uniqueItem.modifyItemQuantity(newQuantity);
	//                            compressedLineItemsIndexes.add(indexInUniques);
	                            
	                            //adding discount for duplicate item
	                            oldDiscount = uniqueItem
										.getPromoDiscountForReceipt();
								if (oldDiscount == null) {
									oldDiscount = DomainGateway
											.getBaseCurrencyInstance();
								}
								newDiscount = saleLineItem
										.getPromoDiscountForReceipt();
								if (newDiscount == null) {
									newDiscount = DomainGateway
											.getBaseCurrencyInstance();

								}
								newDiscount = oldDiscount.add(newDiscount);
								if (uniqueItem.getPromoDiscountForReceipt() != null) {
									disc = disc.add(uniqueItem
											.getPromoDiscountForReceipt());
								} else {
									disc = uniqueItem.getItemPrice()
											.getItemDiscountAmount()
											.multiply(newQuantity);
								}
	                            uniqueItem.modifyItemQuantity(newQuantity);
								uniqueItem.setPromoDiscountForReceipt(newDiscount);
															uniqueItem = (MAXSaleReturnLineItemIfc) changeDiscountDetailOnReceipt(
										uniqueItem, saleLineItem, transaction);

								uniqueItem.recalculateItemTotal();

								
								MAXLineItemTaxBreakUpDetailIfc[] saleLineItemBreakUpDetails = null;
								MAXLineItemTaxBreakUpDetailIfc[] uniqueLineItemBreakUpDetails = null;

								saleLineItemBreakUpDetails = ((MAXItemTaxIfc)saleLineItem
										.getItemPrice().getItemTax())
										.getLineItemTaxBreakUpDetail();
								uniqueLineItemBreakUpDetails = ((MAXItemTaxIfc)uniqueItem
										.getItemPrice().getItemTax())
										.getLineItemTaxBreakUpDetail();

								CurrencyIfc totalTaxAmt = uniqueItem
										.getItemPrice()
										.getItemTax()
										.getItemTaxAmount()
										.add(saleLineItem.getItemPrice()
												.getItemTax().getItemTaxAmount());
								CurrencyIfc totalTaxInclusiveAmt = uniqueItem
										.getItemPrice()
										.getItemTax()
										.getItemInclusiveTaxAmount()
										.add(saleLineItem.getItemPrice()
												.getItemTax()
												.getItemInclusiveTaxAmount());
								CurrencyIfc totalTaxableAmt = uniqueItem
										.getItemPrice()
										.getItemTax()
										.getItemTaxableAmount()
										.add(saleLineItem.getItemPrice()
												.getItemTax()
												.getItemTaxableAmount());
								
								uniqueItem.getItemPrice().getItemTax()
										.setItemTaxAmount(totalTaxAmt);
								uniqueItem
										.getItemPrice()
										.getItemTax()
										.setItemInclusiveTaxAmount(
												totalTaxInclusiveAmt);
								uniqueItem.getItemPrice().getItemTax()
										.setItemTaxableAmount(totalTaxableAmt);

								if (saleLineItemBreakUpDetails != null) {
									for (int j = 0; j < saleLineItemBreakUpDetails.length; j++) {
										MAXLineItemTaxBreakUpDetail saleItemTaxDetail = (MAXLineItemTaxBreakUpDetail) saleLineItemBreakUpDetails[j];
										MAXTaxAssignmentIfc newSaleItemTaxDetailAsgmt = ((MAXDomainObjectFactory) DomainGateway
												.getFactory()).getTaxAssignmentInstance();
										setTaxAssignment(
												saleItemTaxDetail
														.getTaxAssignment(),
												newSaleItemTaxDetailAsgmt);

										for (int k = 0; k < uniqueLineItemBreakUpDetails.length; k++) {
											MAXLineItemTaxBreakUpDetail uniqueItemTaxDetail = (MAXLineItemTaxBreakUpDetail) uniqueLineItemBreakUpDetails[k];
											MAXTaxAssignmentIfc newUniqueItemTaxDetailAsgmt = ((MAXDomainObjectFactory) DomainGateway
													.getFactory())
													.getTaxAssignmentInstance();
											setTaxAssignment(
													uniqueItemTaxDetail
															.getTaxAssignment(),
													newUniqueItemTaxDetailAsgmt);
											if (newSaleItemTaxDetailAsgmt
													.equals(newUniqueItemTaxDetailAsgmt)) {
												CurrencyIfc totalTaxableAmount = uniqueItemTaxDetail
														.getTaxableAmount()
														.add(saleItemTaxDetail
																.getTaxableAmount());
												uniqueItemTaxDetail
														.setTaxableAmount(totalTaxableAmount);
												CurrencyIfc totalTaxAmount = uniqueItemTaxDetail
														.getTaxAmount()
														.add(saleItemTaxDetail
																.getTaxAmount());
												uniqueItemTaxDetail
														.setTaxAmount(totalTaxAmount);
												break;
											}
										}
									}
								}

								//changes for rev 2.3 end
	                            // remove the now obsolete line item from the clone vector
	                            rearrangedLineItems.remove(saleLineItem);
	                        }
	                      //Change for Rev 2.2 : starts
							if(saleLineItem.getPLUItem() != null && saleLineItem.getPLUItem() instanceof MAXPLUItemIfc)
								saleLineItem.setHSNNumber(((MAXPLUItemIfc)saleLineItem.getPLUItem()).getHsnNum());	
							/*else if(saleLineItem.getPLUItem() != null && saleLineItem.getPLUItem() instanceof LSIPLGiftCardPLUItemIfc){
								saleLineItem.setHSNNumber(((LSIPLGiftCardPLUItemIfc)saleLineItem.getPLUItem()).getHsnNum());
							}*/
							//Change for Rev 2.2 : starts
							
	                    }
	                }
	            } // groupLikeItems

	            // complete line item arrangement starting end to beginning
	            for (int i = rearrangedLineItems.size() - 1; i >= 0; i--)
	            {
	                AbstractTransactionLineItemIfc lineItem = rearrangedLineItems.get(i);
	                if (lineItem instanceof SaleReturnLineItemIfc)
	                {
	                    MAXSaleReturnLineItemIfc saleLineItem = (MAXSaleReturnLineItemIfc)lineItem;
	                    if ((saleLineItem.isReturnLineItem() && saleLineItem.isKitHeader()) // don't include any return kit headers
	                            || (saleLineItem.isPriceAdjustmentLineItem() && saleLineItem.isKitHeader()) // don't include any adj kit headers
	                            || saleLineItem.getPLUItem().isStoreCoupon()) // don't print coupons
	                    {
	                        rearrangedLineItems.remove(i);
	                        continue;
	                    }

	                    if (lineItem instanceof PriceAdjustmentLineItemIfc)
	                    {
	                        // remove the price override wrapper so we just print the two adjustments
	                        PriceAdjustmentLineItemIfc priceAdjustment = (PriceAdjustmentLineItemIfc)lineItem;
	                        rearrangedLineItems.remove(i);
	                        // set sale to false so an extra linefeed prints after this item
	                        priceAdjustment.getPriceAdjustSaleItem().setIsPartOfPriceAdjustment(false);
	                    }
	                    else if (lineItem instanceof OrderLineItemIfc && ((OrderLineItemIfc)lineItem).isPriceCancelledDuringPickup())
	                    {
	                        rearrangedLineItems.remove(i);
	                    }
	                  //Change for Rev 2.2 : starts
						if(saleLineItem.getPLUItem() != null && saleLineItem.getPLUItem() instanceof MAXPLUItemIfc)
							saleLineItem.setHSNNumber(((MAXPLUItemIfc)saleLineItem.getPLUItem()).getHsnNum());	
						/*else if(saleLineItem.getPLUItem() != null && saleLineItem.getPLUItem() instanceof MAXGiftCardPLUItemIfc){
							saleLineItem.setHSNNumber(((MAXGiftCardPLUItemIfc)saleLineItem.getPLUItem()).getHsnNum());
						}*/
						 //Change for Rev 2.2 : Ends
	                }
	                //changes for rev 1.4
	                lineItem.setLineNumber(rearrangedLineItems.indexOf(lineItem));
	            }

	            lineItems = rearrangedLineItems.toArray(new AbstractTransactionLineItemIfc[rearrangedLineItems.size()]);
	        }

	        return lineItems;
	    }

 	//changes for rev 1.2 start

		//changes for rev 2.3 start
		protected void setTaxAssignment(MAXTaxAssignmentIfc saleItemTaxDetailAsgmt,
				MAXTaxAssignmentIfc newSaleItemTaxDetailAsgmt) {
			newSaleItemTaxDetailAsgmt.setAppliedOn(saleItemTaxDetailAsgmt
					.getAppliedOn());
			newSaleItemTaxDetailAsgmt.setApplicationOrder(saleItemTaxDetailAsgmt
					.getApplicationOrder());
			newSaleItemTaxDetailAsgmt.setTaxableAmountFactor(saleItemTaxDetailAsgmt
					.getTaxableAmountFactor());
			newSaleItemTaxDetailAsgmt.setTaxCategory(saleItemTaxDetailAsgmt
					.getTaxCategory());
			newSaleItemTaxDetailAsgmt.setTaxCode(saleItemTaxDetailAsgmt
					.getTaxCode());
			newSaleItemTaxDetailAsgmt.setTaxRate(saleItemTaxDetailAsgmt
					.getTaxRate());
			newSaleItemTaxDetailAsgmt.setTaxAmountFactor(saleItemTaxDetailAsgmt
					.getTaxAmountFactor());
			newSaleItemTaxDetailAsgmt.setTaxCodeDescription(saleItemTaxDetailAsgmt
					.getTaxCodeDescription());
		}


	static int couponCount=0;
	 static int itemCouponCount=0;
	 static int billCouponCount=0;
	 public SaleReturnLineItemIfc changeDiscountDetailOnReceipt(
				MAXSaleReturnLineItemIfc itm1, MAXSaleReturnLineItemIfc itm2,
				TransactionIfc transaction) {

			ItemDiscountStrategyIfc[] discounts = itm1.getItemPrice()
					.getItemDiscounts();
			ItemDiscountStrategyIfc[] discounts1 = itm2.getItemPrice()
					.getItemDiscounts();
			// added by vaibhav
			CurrencyIfc disc = null;
			for (int i = 0; i < discounts.length; i++) {
				DiscountRuleIfc d = discounts[i];
				
				CurrencyIfc sp = itm1.getExtendedDiscountedSellingPrice();
				if (d instanceof ItemTransactionDiscountAudit) {
					
					// for recognizig bill bustor
					if(!(d.getReasonCode()==19||d.getReasonCode()==20)){
							
						billCouponCount--;
						if(d.getReasonCode()==5170 && billCouponCount == 0){ // For single loop in case of Coupon
							
							CurrencyIfc trxnAmt = itm1.getItemPrice()
							.getItemTransactionDiscountAmount();
					trxnAmt = trxnAmt.add(itm2.getItemPrice()
							.getItemTransactionDiscountAmount());
					itm1.getItemPrice().setItemTransactionDiscountAmount(trxnAmt);
						
						}
						else if(d.getReasonCode()==5170 && billCouponCount != 0){ // For set the discount in case of multiple coupon
							CurrencyIfc trxnAmt = itm1.getItemPrice()
							.getItemTransactionDiscountAmount();
							
							itm1.getItemPrice().setItemTransactionDiscountAmount(trxnAmt);
						}
						else{ // If Coupon is not added
							CurrencyIfc trxnAmt = itm1.getItemPrice()
							.getItemTransactionDiscountAmount();
					trxnAmt = trxnAmt.add(itm2.getItemPrice()
							.getItemTransactionDiscountAmount());
					itm1.getItemPrice().setItemTransactionDiscountAmount(trxnAmt);
						}
					//	}
						
				}
					else if((d.getReasonCode()==19||d.getReasonCode()==20) && billCouponCount==0){
						CurrencyIfc trxnAmt = itm1.getItemPrice()
						.getItemTransactionDiscountAmount();
						trxnAmt = trxnAmt.add(itm2.getItemPrice()
								.getItemTransactionDiscountAmount());
						itm1.getItemPrice().setItemTransactionDiscountAmount(trxnAmt);
					}
					else{
						CurrencyIfc trxnAmt = itm1.getItemPrice()
						.getItemTransactionDiscountAmount();
						itm1.getItemPrice().setItemTransactionDiscountAmount(trxnAmt);
					}
				} else {
					if (!d.isAdvancedPricingRule()) {
						itemCouponCount--;
						if(d.getReasonCode()==5170 && itemCouponCount == 0){
							
							
							// Changes for Manual Item Amt and Coupon Item Amt Fix
							if(d.getDiscountRate().doubleValue()==0.00){ // This is for Percent Discount
							
							CurrencyIfc itemAmt = itm1.getItemPrice()
							.getItemDiscountAmount();
					itemAmt = itemAmt.add(itm2.getItemPrice()
							.getItemDiscountAmount());
					itm1.getItemPrice().setItemDiscountAmount(itemAmt);
					itm1.getItemPrice().recalculateItemTotal();
							}
							else {
								CurrencyIfc itemAmt = itm1.getItemPrice()
								.getItemDiscountAmount();
						
						itm1.getItemPrice().setItemDiscountAmount(itemAmt);
						itm1.getItemPrice().recalculateItemTotal();
							}
							
						// Ends here	
							
						}
						else if(d.getReasonCode()==5170 && itemCouponCount > 0){
							
							
							// Changes for Manual Item Amt and Coupon Item Amt Fix
							if(d.getDiscountRate().doubleValue()==0.00){ // This is for Percent Discount
							
							CurrencyIfc itemAmt = itm1.getItemPrice()
							.getItemDiscountAmount();
					itemAmt = itemAmt.add(itm2.getItemPrice()
							.getItemDiscountAmount());
					itm1.getItemPrice().setItemDiscountAmount(itemAmt);
					itm1.getItemPrice().recalculateItemTotal();
							}
							else {
								CurrencyIfc itemAmt = itm1.getItemPrice()
								.getItemDiscountAmount();
						
						itm1.getItemPrice().setItemDiscountAmount(itemAmt);
						itm1.getItemPrice().recalculateItemTotal();
							}
							
						// Ends here	
							
						}
						else if(d.getReasonCode() != 5170){

							// Changes for Manual Item Amt and Coupon Item Amt Fix
							if(d.getDiscountRate().doubleValue()==0.00){ // This is for Amount Discount
							
							CurrencyIfc itemAmt = itm1.getItemPrice()
							.getItemDiscountAmount();
					itemAmt = itemAmt.add(itm2.getItemPrice()
							.getItemDiscountAmount());
					itm1.getItemPrice().setItemDiscountAmount(itemAmt);
					itm1.getItemPrice().recalculateItemTotal();
							}
							else {
								CurrencyIfc itemAmt = itm1.getItemPrice()
								.getItemDiscountAmount();
						
						itm1.getItemPrice().setItemDiscountAmount(itemAmt);
						itm1.getItemPrice().recalculateItemTotal();
							}
						
						}
						else {
							CurrencyIfc itemAmt = itm1.getItemPrice()
							.getItemDiscountAmount();
					
					itm1.getItemPrice().setItemDiscountAmount(itemAmt);
					itm1.getItemPrice().recalculateItemTotal();
						}
						
					} else {
						// Added by vaibhav
						if (itm1.getPromoDiscountForReceipt() != null) {
							disc = itm1.getPromoDiscountForReceipt();
						} else {
							disc = itm1.getAdvancedPricingDiscount()
									.getDiscountAmount();
						}
						
						if (itm1.getAdvancedPricingDiscount().getDescription()
								.equalsIgnoreCase("BuyNofXgetYatZ%off")) {
							
							d.setDiscountAmount(d.getDiscountAmount().add(
									(discounts1[i].getDiscountAmount())));

						} else {

							BigDecimal qty = itm1.getItemQuantityDecimal();
							float k = qty.floatValue();
							if (k < 0) {
								
								if (transaction instanceof VoidTransaction) {
									disc = disc.add(
											discounts1[i].getDiscountAmount())
											.multiply(qty);
									disc = disc.negate();
								}
								
								else {
									disc = disc.add(discounts1[i]
											.getDiscountAmount().negate());// rajeev
									
								}
							}

							else {
								if (itm1.getPromoDiscountForReceipt() != null) {
									disc = itm1.getPromoDiscountForReceipt();
								} else
									disc = disc.add(discounts1[i]
											.getDiscountAmount());
							}

							d.setDiscountAmount(disc);
							discounts[i] = (ItemDiscountStrategyIfc) d;

							itm1.getItemPrice().setItemDiscounts(discounts);
							itm1.getAdvancedPricingDiscount().setDiscountAmount(
									disc);

							CurrencyIfc discAmt = DomainGateway
									.getBaseCurrencyInstance();
							discAmt.setDecimalValue(disc.getDecimalValue());
							CurrencyIfc discAmt1 = DomainGateway
									.getBaseCurrencyInstance();
							discAmt1.setDecimalValue(disc.getDecimalValue());

							itm1.getItemPrice().setItemDiscountAmount(discAmt);
							itm1.getItemPrice().setItemDiscountTotal(discAmt1);

							itm1.getItemPrice().recalculateItemTotal();

						}
						
					}
				}
				// modified by vaibhav
				if (!d.isAdvancedPricingRule() && i < discounts1.length) {
					
					
					d.setDiscountAmount((discounts1[i].getDiscountAmount().divide(itm2.getItemQuantityDecimal())).multiply(
							itm1.getItemPrice().getItemQuantityDecimal()));
					
					// Changes for wrong discount bug by Gaurav and Manpreet: Start
					if (transaction instanceof VoidTransaction
							&& d.getDiscountAmount().getDoubleValue() > 0.0) {
						d.setDiscountAmount(d.getDiscountAmount().negate());
					}
					// Changes for wrong discount bug by Gaurav and Manpreet: END

				}
			}
			return itm1;
		}
		
	//changes for rev 2.3 end
	 
	 public int giftCardRequestType;
	 
	 public int getGiftCardRequestType() {
		return giftCardRequestType;
	}

	public void setGiftCardRequestType(int giftCardRequestType) {
		this.giftCardRequestType = giftCardRequestType;
	}

	public GiftCardIfc giftCard;

	public GiftCardIfc getGiftCard() {
		return giftCard;
	}

	public void setGiftCard(GiftCardIfc giftCard) {
		this.giftCard = giftCard;
	}
		 

	public int getNumItems(){
		AbstractTransactionLineItemIfc[] lineItems= (AbstractTransactionLineItemIfc[])getLineItems();
		if(!isTransactionHasSendItem())
		 return getLineItems().length;
		int numItem=((RetailTransactionIfc)transaction)
			      .getLineItemsVector().size();
		for (AbstractTransactionLineItemIfc lineItem : lineItems) {
			if(((SaleReturnLineItemIfc)lineItem).getPLUItem().getItem().getItemID().equals("30060009"))
				numItem--;
	    }
		return numItem;
	}
	
	public String[] receiptFooter;

	@Override
	public String[] getReceiptFooter() {
		
		return receiptFooter;
	}

	@Override
	public void setReceiptFooter(String[] receiptFooter) {
		this.receiptFooter = receiptFooter;
	}

	@Override
	public CurrencyIfc getTotalRoundedAmount(){
			
		CurrencyIfc totalRoundedAmount=getTotalAmount();
		if(!(((TenderableTransactionIfc)getTransaction()).getTenderTransactionTotals().getCashChangeRoundingAdjustment().equals(DomainGateway.getBaseCurrencyInstance())))
			totalRoundedAmount=DomainGateway.getBaseCurrencyInstance(String.valueOf(Math.round(totalRoundedAmount.getDoubleValue())));
		
			return totalRoundedAmount;
	}
	//changes for rev 1.2 end
	
	
	/**
	 * set the calculated value for discount on receipt.
	 * 
	 * @param srli
	 *            sale return line item
	 * 
	 */
	// ---------------------------------------------------------------------
	public void setDiscountColumn(MAXSaleReturnLineItemIfc srli)
   {
		CurrencyIfc unitPrice = null;
		BigDecimal useQty = Util.I_BIG_DECIMAL_ZERO;
		BigDecimal qty = srli.getItemQuantityDecimal();
		CurrencyIfc taxAmount = srli.getItemTaxAmount();
		CurrencyIfc price = (srli.getExtendedSellingPrice());//.add(taxAmount);

		if (qty.compareTo(Util.I_BIG_DECIMAL_ONE) == 0) {
			unitPrice = price;
			useQty = qty;
		} else {
			unitPrice = price.divide(qty);
			if (qty.signum() < 0) {
				useQty = qty.negate();
			} else {
				useQty = qty;
			}
		}
			boolean isMRPToSellingPriceDiff = false;
			MAXItemPriceIfc ip = (MAXItemPriceIfc) srli.getItemPrice();
			CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance();
			CurrencyIfc itemdisc = DomainGateway.getBaseCurrencyInstance();
			ItemDiscountStrategyIfc[] discounts = srli.getItemPrice()
					.getItemDiscounts();
			CurrencyIfc totalItemMRP  = DomainGateway.getBaseCurrencyInstance();
			if(srli.getPLUItem() instanceof MAXPLUItemIfc){
			 totalItemMRP = ((MAXPLUItemIfc) srli.getPLUItem()).getMaximumRetailPrice().multiply(useQty);
			
			if(totalItemMRP.compareTo(price.abs())!=0)
				isMRPToSellingPriceDiff = true;
			}
			
			AdvancedPricingRuleIfc[] advancedPricingRule =   srli.getPLUItem().getAdvancedPricingRules();
			boolean isAdvanceRule=false;
			boolean isFixedPriceDiscountPresent=false;
			
			if (discounts.length > 0) {
				for (int i = 0; i < discounts.length; i++) {
					if(discounts[i].getReasonCode()==22 || discounts[i] instanceof ItemDiscountByFixedPriceStrategy)
						isFixedPriceDiscountPresent=true;	
				}
			}
			
			for(int pricingRule=0; pricingRule<advancedPricingRule.length;pricingRule++)
			{
				if(advancedPricingRule[pricingRule].getDescription().length()!=0){
					isAdvanceRule=true;
					break;
				}
			}
				
			if (discounts.length > 0) {
				for (int i = 0; i < discounts.length; i++) {
					//changes for rev 1.5
					itemdisc = DomainGateway.getBaseCurrencyInstance();
					DiscountRuleIfc dis = discounts[i];
					/*Rev 1.23 start Add this condition to collect transaction level coupon percentage disount amount to print on receipt seprately*/
                       if(dis instanceof ItemTransactionDiscountAudit && dis.getReasonCode()==5170 && dis.getDiscountMethod()==1 ){
						transCouponDiscountByPerc=transCouponDiscountByPerc.add(dis.getDiscountAmount());
					}
                       /*if(dis instanceof ItemDiscountByPercentageStrategy && dis.getReasonCode()==5170 && dis.getDiscountMethod()==1 && (duplicateReceipt || transaction.getTransactionType()==3)){
                    	   itemCouponDiscountByPerc=itemCouponDiscountByPerc.add(dis.getDiscountAmount());
                       }*/
                       /*Rev 1.23 End*/
					if(dis.getReasonCode()!=5170 || transaction.getTransactionType()==2){
					if(!((dis instanceof ItemTransactionDiscountAudit) || dis instanceof ReturnItemTransactionDiscountAudit)
							|| transaction.getTransactionType()==2)
					{
						if(isAdvanceRule && !(dis instanceof ItemDiscountByFixedPriceStrategy) && transaction.getTransactionType()!=2)
						{
							if(dis instanceof ItemDiscountByAmountStrategy && dis.getAssignmentBasis()==0 && 
									(isFixedPriceDiscountPresent && (!duplicateReceipt|| dis.getReasonCode()==5)))
							{
								itemdisc = dis.getDiscountAmount();
							}
							else
							{
								itemdisc = dis.getDiscountAmount().divide(useQty);
							}
						}
						else if ((transaction.getTransactionType()==1 || transaction.getTransactionType()==19) 
								&& !(dis instanceof ItemDiscountByAmountStrategy)){
							// changes for rev 1.5 & 1.7
							if (dis instanceof MAXItemDiscountByPercentageStrategy
									&& ((MAXItemDiscountByPercentageStrategy) dis)
											.getCapillaryCoupon().isEmpty())
								itemdisc = dis.getDiscountAmount().divide(useQty);
							else if (!(dis instanceof MAXItemDiscountByPercentageStrategy)) {
								// Rev 2.7 Changes for Re-print Sale Receipt for Weighted items Promo @ Purushotham Reddy
								if(duplicateReceipt){
									itemdisc = dis.getDiscountAmount();
								}else{
									itemdisc = dis.getDiscountAmount().divide(useQty);
								}
							}
						}
						else if (transaction.getTransactionType() == 2
								|| transaction.getTransactionType() == 3
								|| transaction.getTransactionType() == 20
								|| transaction.getTransactionType() == 22
								|| transaction.getTransactionType() == 39
								|| duplicateReceipt) {// Rev 1.25
							if ((dis instanceof ItemDiscountByFixedPriceStrategy || dis instanceof ItemDiscountByAmountStrategy)
									&& (transaction.getTransactionType() != 20 || !isAdvanceRule)
									&& dis.getReasonCode() != 5170
									&& !duplicateReceipt
									&& transaction.getTransactionType() != 2
									&& transaction.getTransactionType() != 3
									&& transaction.getTransactionType() != 22
									&& !dis.isAdvancedPricingRule()) {
								itemdisc = dis.getDiscountAmount();
							}
							// Change for wrong item amount discount in case of
							// sale return and postvoid
							// Rev 1.25
							else if ((transaction.getTransactionType() == 2
									|| transaction.getTransactionType() == 3 || duplicateReceipt)
									&& dis instanceof ItemDiscountByAmountStrategy
									&& dis.getReasonCode() != 5170
									&& !dis.isAdvancedPricingRule()) {
								itemdisc = dis.getDiscountAmount();
							} else {
								itemdisc = dis.getDiscountAmount();//.divide(useQty);
										
							}
						}
						/*Rev 1.20 start*/
                         else if(dis instanceof ItemDiscountByAmountStrategy && dis.getReasonCode()==5170){
                        	 itemdisc = dis.getDiscountAmount().divide(useQty);
						}
						/*Rev 1.20 end*/
						//Rev 1.25
						else if((dis.getDescription().equalsIgnoreCase("BuyNofXgetYatZ$off") && dis.getReasonCode()==2) ||
								(dis.getDescription().equalsIgnoreCase("BuyNofXgetYatZ%off") && dis.getReasonCode()==1)){
							itemdisc = dis.getDiscountAmount().divide(useQty);
						}
						else if(dis instanceof MAXItemDiscountByAmountStrategy && 
								!((MAXItemDiscountByAmountStrategy)dis).getCapillaryCoupon().isEmpty()){
							//do not add
						}
						else {
							itemdisc = dis.getDiscountAmount();
						}
						amount = amount.add(itemdisc);
					}
				}
				}
			} 
			if (isMRPToSellingPriceDiff)
			{
				itemdisc = ((MAXPLUItemIfc) srli.getPLUItem()).getMaximumRetailPrice().subtract((price.abs()).divide(useQty));
				if (transaction.getTransactionType()==2 || transaction.getTransactionType()==3 || transaction.getTransactionType()==22)
				itemdisc = itemdisc.negate();
				amount = amount.add(itemdisc);
			}
			CurrencyIfc saveAmount = amount;
			CurrencyIfc itemSellingPrice = DomainGateway.getBaseCurrencyInstance();
			CurrencyIfc totalSaveAmount=saveAmount.abs().multiply(useQty);
			BigDecimal totalSaveAmountBD=new BigDecimal(totalSaveAmount.getDoubleValue());
			BigDecimal bigOne = new BigDecimal(1);
			totalSaveAmountBD = totalSaveAmountBD.divide(bigOne, 2, totalSaveAmountBD.ROUND_HALF_EVEN);
			CurrencyIfc totalSaveAmountRounded = DomainGateway
			.getBaseCurrencyInstance(totalSaveAmountBD);
			
			if (transaction.getTransactionType()==2 || transaction.getTransactionType()==3)
				totalSaveAmountRounded=totalSaveAmountRounded.negate();
			if(isMRPToSellingPriceDiff)
			{
				itemSellingPrice = (totalItemMRP.subtract(totalSaveAmountRounded.abs()));
			}
			else
			{
				itemSellingPrice = (price.subtract(totalSaveAmountRounded));
			}
			if (saveAmount.signum()<0)
			{
				saveAmount= saveAmount.negate();
			}
		
			if ((transaction.getTransactionType()==2 ||transaction.getTransactionType()==3) && itemSellingPrice.signum()>0)
			itemSellingPrice = itemSellingPrice.negate();
			
			ip.setDiscountAmount(saveAmount);
		}

	
	//changes for rev 1.4 end
	/* (non-Javadoc)
	 * @see max.retail.stores.pos.receipt.MAXReceiptParameterBeanIfc#isNonTaxableItem()
	 * checking transaction for having any non taxable item or not
	 */
	@Override 
	public boolean isNonTaxableItem(){
		boolean nonVatItem=false;
		AbstractTransactionLineItemIfc[] lineItems=getLineItems();
		for (AbstractTransactionLineItemIfc lineItem : lineItems) {
			if(((TaxInformationContainer)lineItem.getTaxInformationContainer()).getReceiptCode().equals("E"))
			{	
				if(!(lineItem instanceof MAXSaleReturnLineItem && ((MAXSaleReturnLineItem) lineItem).checkGiftItem()))
					nonVatItem=true;
					break;
			}
		}
		return nonVatItem;
	}		
	//changes for rev 1.4 end
	
	//changes for rev 1.6 start
	/* (non-Javadoc) print "Available Amount :" on EmployeeReceipt.bpt
	 * @see max.retail.stores.pos.receipt.MAXReceiptParameterBeanIfc#getEmpDiscountAvailLimit()
	 */
	@Override
	public CurrencyIfc getEmpDiscountAvailLimit(){
		CurrencyIfc updatedAmount=DomainGateway.getBaseCurrencyInstance();
		
			if(getTransaction() instanceof MAXSaleReturnTransactionIfc){
				updatedAmount=DomainGateway.getBaseCurrencyInstance(((MAXSaleReturnTransactionIfc)getTransaction()).getEmpDiscountAvailLimit());
				updatedAmount=updatedAmount.subtract(getTotalAmountForEmployeeDiscount());
			}//changes for rev 2.1
			return updatedAmount;
		
	}

	/* (non-Javadoc) print "Other Discount" on total.bpt
	 * @see max.retail.stores.pos.receipt.MAXReceiptParameterBeanIfc#printOtherDiscount()
	 */
	@Override
	public CurrencyIfc printOtherDiscount(){
		CurrencyIfc updatedAmount=DomainGateway.getBaseCurrencyInstance();
		CurrencyIfc transactionDiscountTotal=((TenderableTransactionIfc)getTransaction()).getTransactionTotals().getTransactionDiscountTotal();
		//changes for rev 2.1
		if(transactionDiscountTotal.signum()>0)
		updatedAmount=transactionDiscountTotal.subtract(getEmployeeDiscountAmount()).subtract(getBillBusterDiscount());
		return updatedAmount;
		
	}

	/* (non-Javadoc) print "Employee Discount" on total.bpt
	 * @see max.retail.stores.pos.receipt.MAXReceiptParameterBeanIfc#getEmployeeDiscountAmount()
	 */	//changes for rev 1.9 and 2.1
	@Override
	public CurrencyIfc getEmployeeDiscountAmount() {
		CurrencyIfc employeeDiscount=DomainGateway.getBaseCurrencyInstance();
		boolean print=false;

		if(getTransaction() instanceof MAXSaleReturnTransactionIfc){
			if(getEmpDiscountAvailLimit().subtract(DomainGateway.getBaseCurrencyInstance(((MAXSaleReturnTransactionIfc)
					getTransaction()).getEmpDiscountAvailLimit())).signum()!=0)
			{
				TransactionDiscountStrategyIfc[] discounts=((MAXSaleReturnTransactionIfc)getTransaction())
						.getItemContainerProxy().getTransactionDiscounts();
				for (TransactionDiscountStrategyIfc discount : discounts) {
					if(discount!=null && discount.getDiscountEmployeeID()!=null && 
							!discount.getDiscountEmployeeID().equals("") && discount.getDiscountEmployee()!=null && discount.getReasonCode()==-1){
					employeeDiscount=employeeDiscount.add(discount.getDiscountAmount());
					}
				}
			}
		}
			return employeeDiscount;
	}

	
	 //changes for rev 1.6 end
	//changes for rev 1.9 start

	String copyReportText;
	@Override
	public void setCopyReportText(String copyReportText){
		this.copyReportText=copyReportText;
	}
	
	@Override
	public String getCopyReportText(){
		return copyReportText;
	}
	
	
	//changes for rev 1.9 end
	@Override
	public boolean printEcomDetails()
	{
		if(isPrintAddDetailsEcomReceipt() && transaction instanceof MAXSaleReturnTransaction && ((MAXSaleReturnTransaction) transaction).iseComSendTransaction())
		return true;
		else 
		return false;	
	}

	//Changes for Rev 2.0 starts 
	public CurrencyIfc geteComOrderRefund()
	{
		return ((MAXSaleReturnTransaction)transaction).geteComOrderAmount().subtract(((TenderableTransactionIfc)transaction).getTransactionTotals().getGrandTotal());
	}
	//Changes for Rev 2.0 ends
	
	//changes for rev 1.9 start
	@Override
	public CurrencyIfc getBillBusterDiscount(){
		
		CurrencyIfc billBusterDiscount=DomainGateway.getBaseCurrencyInstance();
		
		if(getTransaction() instanceof SaleReturnTransactionIfc){
			TransactionDiscountStrategyIfc[] discounts=((SaleReturnTransactionIfc)getTransaction()).getItemContainerProxy().getTransactionDiscounts();
			for (TransactionDiscountStrategyIfc discount : discounts) {
				if(discount!=null && (discount.getReasonCode() == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NatZPctoffTiered || discount
						.getReasonCode() == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NatZ$offTiered)){
					billBusterDiscount=billBusterDiscount.add(discount.getDiscountAmount());
				}
			}
		}
		
		return billBusterDiscount;
	}

	@Override
	public boolean isExtraCopy(){
		if(isTransactionHasSendItem() || getDocumentType()==MAXReceiptTypeConstantsIfc.HIRE_PURCHASE)
			return true;
			return false;
	}
	//changes for rev 1.9 end	
	
		//changes for rev 2.5 start	
	
	public String getLoyaltyCustomerName(){
		String customerName="";
		if(getTransaction().getTransactionType() != TransactionConstantsIfc.TYPE_LAYAWAY_PAYMENT)
			customerName=((SaleReturnTransactionIfc)getTransaction()).getCustomer().getCustomerName();
		else
			customerName=((LayawayPaymentTransaction)getTransaction()).getCustomer().getCustomerName();
		if(customerName.endsWith(" NA")==true)
		{
			customerName=customerName.substring(0,customerName.lastIndexOf(" NA"));
		}
		return customerName;
	}
		@Override
	public String getLayawayCustomerName() throws NullPointerException{
		
		String customerName="";
		if(getTransaction().getTransactionType() != TransactionConstantsIfc.TYPE_LAYAWAY_PAYMENT)
			customerName=((SaleReturnTransactionIfc)getTransaction()).getCustomer().getCustomerName();
		else
			customerName=((LayawayPaymentTransaction)getTransaction()).getCustomer().getCustomerName();
		//String customerName=((LayawayTransactionIfc)getTransaction()).getLayaway().getCustomer().getFirstLastName();
		if(customerName.endsWith(" NA")==true)
		{
			customerName=customerName.substring(0,customerName.lastIndexOf(" NA"));
		}
		return customerName;
	}
	//changes for rev 2.5 end
	//Change for Rev 2.2 : Ends


	
	public Boolean isGSTEnabled() {
		//true
		if(transaction instanceof MAXSaleReturnTransactionIfc){
			/*Change for Rev 2.3: Start*/
			if(Gateway.getBooleanProperty("application","GSTEnabled", true)){
				return true;
			}else{
				/*Change for Rev 2.3: End*/
				return ((MAXSaleReturnTransactionIfc)transaction).isGstEnable();	
			}
		}
		/*Change for Rev 2.3: Start*/
		else if(transaction instanceof MAXVoidTransaction){
			if(Gateway.getBooleanProperty("application","GSTEnabled", true)){
				return true;
			}
			else{
				return false;
			}
		}
		/*Change for Rev 2.3: End*/
		else
			return false;
	}
	
	
	public boolean isGSTEnabledReturnTransaction() {
		if(transaction instanceof MAXSaleReturnTransactionIfc && ((MAXSaleReturnTransactionIfc)transaction).isGstEnable() && getTransactionType() != TransactionConstantsIfc.TYPE_RETURN)
			return true;
		else
			return false;
	}
	
	/*public boolean isAnyCustomerAttachedReturnTransaction(){
		boolean cust = false;
		if(getTransactionType() != TransactionConstantsIfc.TYPE_RETURN){
		cust=isLoyaltyCustomer();
		if(!cust){
			if(getTransaction() instanceof LSIPLSaleReturnTransactionIfc && ((LSIPLSaleReturnTransactionIfc)getTransaction()).getCustomer() instanceof LSIPLCustomerIfc){
				cust=((LSIPLCustomerIfc)((LSIPLSaleReturnTransactionIfc)getTransaction()).getCustomer()).isBothLocalAndLoyaltyCustomerAttached();
			}}
		if(!cust)
			cust=isNormalCustomer();
		if(!cust)
			cust=isCardlessCustomer();
		}
		else{
			cust=true;
		}
		return cust;
		
	}*/

	public MAXTaxSummaryDetailsBean[] printTaxSummaryDetails()
	{

		if (!(transaction instanceof VoidTransaction && (((VoidTransaction) transaction)
				.getOriginalTransactionType() == TransactionConstantsIfc.TYPE_LAYAWAY_INITIATE || ((VoidTransaction) transaction)
				.getOriginalTransactionType() == TransactionConstantsIfc.TYPE_LAYAWAY_PAYMENT)))
		{
			if ((transaction instanceof RetailTransactionIfc) && 
					((RetailTransactionIfc) transaction).getLineItemsVector() != null)
			{
				List lineItemTaxBreakupDetailsList = new ArrayList();
				String taxCode = null;
				String taxCodeVal = null;
				Map taxmap = new HashMap();

				if (transaction instanceof RetailTransactionIfc)
				{
					for (Enumeration e = ((RetailTransactionIfc) transaction).getLineItemsVector()
							.elements(); e.hasMoreElements();)
					{
						SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) e.nextElement();

						MAXLineItemTaxBreakUpDetailIfc[] lineItemBreakUpDetails = null;
						lineItemBreakUpDetails = ((MAXItemTaxIfc) (srli.getItemPrice().getItemTax()))
								.getLineItemTaxBreakUpDetail();
					
						if (lineItemBreakUpDetails != null)
						{
							for (int j = 0; j < lineItemBreakUpDetails.length; j++)
							{
								lineItemTaxBreakupDetailsList.add(lineItemBreakUpDetails[j]);
							}
						}
					}
				}

				// Group the Tax Breakup's by TaxCode.
				Map taxSummaryDetailsMap = groupByTaxCode(lineItemTaxBreakupDetailsList);
				taxSummaryDetailsMap.values().toArray();
				Object arr[] = taxSummaryDetailsMap.values().toArray();

				MAXTaxSummaryDetailsBean taxSummaryDetailsBean[] = new MAXTaxSummaryDetailsBean[arr.length];
				// colorDescArray[0] = initialSubDept;
				for (int i = 0; i < arr.length; i++)
				{
					if(transaction instanceof VoidTransaction){
					taxSummaryDetailsBean[i] = (MAXTaxSummaryDetailsBean) arr[i];
						taxSummaryDetailsBean[i].setTaxableAmount(taxSummaryDetailsBean[i].getTaxableAmount().negate());
						taxSummaryDetailsBean[i].setTaxAmount(taxSummaryDetailsBean[i].getTaxAmount().negate());
					}					
					else{
						taxSummaryDetailsBean[i] = (MAXTaxSummaryDetailsBean) arr[i];
					}
					
					RetailTransactionIfc retailTransaction = (RetailTransactionIfc) getTransaction();
				
				   if (retailTransaction instanceof MAXVoidTransaction) {

						taxmap = ((MAXVoidTransaction) retailTransaction)
								.getTaxCode();
					}
				   if (retailTransaction instanceof MAXSaleReturnTransactionIfc) 
					{
						
						taxmap =  ((MAXSaleReturnTransactionIfc)retailTransaction).getTaxCode();
					}
				   if(taxmap!=null){
					for(	Object key : taxmap.keySet() )
					{
						taxCode = key.toString();
						taxCodeVal = (String) taxmap.get(key);
						if(taxSummaryDetailsBean[i].getTaxCode() == taxCode)
						{
							taxSummaryDetailsBean[i].setTaxCodeValue(taxCodeVal);
							break;
						}
					}
				   }
				}
				setTaxSummary(taxSummaryDetailsBean);
				
				return getTaxSummary();
			}
		}
		return null;
	}
	protected Map groupByTaxCode(List lineItemTaxBreakupDetailsList)
	{

		Map taxCodeMap = new HashMap();

		Iterator it = lineItemTaxBreakupDetailsList.iterator();
		while (it.hasNext())
		{

			MAXLineItemTaxBreakUpDetail litbd = (MAXLineItemTaxBreakUpDetail) it.next();
			String taxCode = litbd.getTaxAssignment().getTaxCode();
			// Add only + ve Tax Amount based on the Tax Code. Negative Taxes
			// shouldn't be showed on Receipt.
		
			// if (taxCode != null && !taxCode.equals(Util.EMPTY)) {
			if (taxCode != null && !taxCode.equals(Util.EMPTY_STRING))
			{

				if (taxCodeMap.containsKey(taxCode))
				{
					MAXTaxSummaryDetailsBean tsdb = (MAXTaxSummaryDetailsBean) taxCodeMap
							.get(taxCode);
					tsdb.addTaxableAmount(litbd.getTaxableAmount());
					tsdb.addTaxAmount(litbd.getTaxAmount());
					tsdb.setTaxRate(litbd.getTaxAssignment().getTaxRate());
					tsdb.setTaxDescription(litbd.getTaxAssignment().getTaxCodeDescription());
					tsdb.addTotalTaxAmount(litbd.getTaxableAmount().add(litbd.getTaxAmount()));

				}
				else
				{
					MAXTaxSummaryDetailsBean tsdb = new MAXTaxSummaryDetailsBean();
					tsdb.setTaxableAmount(litbd.getTaxableAmount());
					tsdb.setTaxAmount(litbd.getTaxAmount());
					tsdb.setTaxRate(litbd.getTaxAssignment().getTaxRate());
					tsdb.setTaxDescription(litbd.getTaxAssignment().getTaxCodeDescription());
					tsdb.setTaxCode(litbd.getTaxAssignment().getTaxCode());
					tsdb.setTotalTaxAmount(litbd.getTaxableAmount().add(litbd.getTaxAmount()));
					taxCodeMap.put(taxCode, tsdb);
				}
			}
		}
		return taxCodeMap;
	}
	
	protected MAXTaxSummaryDetailsBean[] taxSummary;

	
	public MAXTaxSummaryDetailsBean[] getTaxSummary() {
		return taxSummary;
	}
	CurrencyIfc taxPaid = DomainGateway.getBaseCurrencyInstance();
	public void setTaxSummary(MAXTaxSummaryDetailsBean[] taxSummary)
	{
		if(isGSTEnabled()){
		HashMap<String,CurrencyIfc> tax= new HashMap<String,CurrencyIfc>();
		HashMap<String,CurrencyIfc> taxable= new HashMap<String,CurrencyIfc>();
		for(int i=0; i<taxSummary.length;i++){
			tax.put(taxSummary[i].getTaxCodeValue(), DomainGateway.getBaseCurrencyInstance());
			taxable.put(taxSummary[i].getTaxCodeValue(), DomainGateway.getBaseCurrencyInstance());
		}
		
		}		
		taxSummary = sortItem(taxSummary);
		this.taxSummary = taxSummary;
	}
	public boolean isGSTDisable(){
		if(transaction instanceof MAXSaleReturnTransactionIfc ){
			/*Change for Rev 2.3: Start*/
			if(Gateway.getBooleanProperty("application","GSTEnabled", true)){
				return false;
			}else{
				/*Change for Rev 2.3: End*/
				return !((MAXSaleReturnTransactionIfc)transaction).isGstEnable();	
			}	
		}				
		/*Change for Rev 2.3: Start*/
		else if(transaction instanceof VoidTransaction || transaction instanceof MAXVoidTransaction){
			if(Gateway.getBooleanProperty("application","GSTEnabled", true)){
				return false;
			}
			else{
				return true;
			}
		}
		/*Change for Rev 2.3: End*/
		else
			return true;
	}
	public CurrencyIfc getTAXSummaryTotal()
    {
        // accumulate totals
        MAXTaxSummaryDetailsBean[] taxInfos = getTaxSummary();
        CurrencyIfc totalTax = DomainGateway.getBaseCurrencyInstance(new BigDecimal("0.00"));
        for (int i = taxInfos.length - 1; i >= 0; i--)
        {
        	totalTax=totalTax.add(taxInfos[i].getTaxAmount());
        }
        
        return totalTax;
    }
	
	public String getOriginalTransactionNumber(){
	//	if(transaction instanceof LSIPLSaleReturnTransactionIfc )
		if(transaction instanceof RetailTransactionIfc ){
			AbstractTransactionLineItemIfc[] lineItems = ((RetailTransactionIfc) transaction).getLineItems();
			if(lineItems!= null && lineItems.length>0 && lineItems[0]!= null){
				SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc) lineItems[0];
				if(lineItem!=null && lineItem.getReturnItem()!= null && lineItem.getReturnItem().getOriginalTransactionID()!=null ){
					return lineItem.getReturnItem().getOriginalTransactionID().getTransactionIDString();
				}
				else{
					return null;
				}
			}
			else{
				return null;
			}
		}
		else
			return null;
		
	}
	public String getOriginalTransactionDate(){
		if(transaction instanceof RetailTransactionIfc ){
			AbstractTransactionLineItemIfc[] lineItems = ((RetailTransactionIfc) transaction).getLineItems();
			if(lineItems!= null && lineItems.length>0 && lineItems[0]!= null){
				SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc) lineItems[0];
				if(lineItem!=null && lineItem.getReturnItem()!= null && lineItem.getReturnItem().getOriginalTransactionID()!=null ){
					{
						lineItem.getReturnItem().getOriginalTransactionBusinessDate().dateValue();
						return new SimpleDateFormat("dd-MM-yyy").format(lineItem.getReturnItem().getOriginalTransactionBusinessDate().dateValue());
					}
				}
				else{
					return null;
				}
			}
			else{
				return null;
			}
		}
		else
			return null;
	}
		
	public CurrencyIfc getTaxCollectedOnAdvance(){
		CurrencyIfc totalTax = null;
		for (Enumeration e = ((RetailTransactionIfc) transaction).getLineItemsVector().elements(); e
				.hasMoreElements();) {
			MAXSaleReturnLineItemIfc srli = (MAXSaleReturnLineItemIfc) e.nextElement();
			MAXLineItemTaxBreakUpDetailIfc[] lineItemTaxBreakUpDetails = null;
			lineItemTaxBreakUpDetails = ((MAXItemTaxIfc) (srli.getItemPrice().getItemTax()))
					.getLineItemTaxBreakUpDetail();
			for(int i=0 ;i<lineItemTaxBreakUpDetails.length;i++){
				if(totalTax== null)
					totalTax=(lineItemTaxBreakUpDetails[i].getTaxAmount());
				else
					totalTax=totalTax.add(lineItemTaxBreakUpDetails[i].getTaxAmount());
			}
		}
		
		return null;
	}
	
	private MAXTaxSummaryDetailsBean[] sortItem(
			MAXTaxSummaryDetailsBean[] taxLineItem) {
		quickSort(taxLineItem, taxLineItem.length);
		return taxLineItem;
	}

	public static void quickSort(MAXTaxSummaryDetailsBean[] slItems,
			int len) {

		int a, b;
		MAXTaxSummaryDetailsBean temp = null;
		int sortTheStrings = len - 1;
		for (a = 0; a < sortTheStrings; ++a)
			for (b = 0; b < sortTheStrings; ++b)
			{
				String itemDesc = ((MAXTaxSummaryDetailsBean) slItems[b]).getTaxCodeValue();
				String itemDesc1 = ((MAXTaxSummaryDetailsBean) slItems[b+1]).getTaxCodeValue();
				if(itemDesc != null && itemDesc1 != null){
					if (itemDesc.compareTo(itemDesc1)> 0)
					{
						temp = slItems[b];
						slItems[b] = slItems[b + 1];
						slItems[b + 1] = temp;
					}
				}
	      }
	}
		
	//Change for Rev 2.2 : Ends

	//defect fix rev 2.4 starts
	public boolean isGSTTaxApplicable()
	{
		boolean flag = true;
		MAXTaxSummaryDetailsBean[] details = getTaxSummary();
		if(details == null || details.length == 0)
		{
			flag = false;
		}
		return flag;
	}
	//defect fix rev 2.4 ends

	//changes starts for Rev 2.6
	/**
	 * Added method for Rev 2.6 
	 */
	private transient SendPackInfo nextSendPackInfo;
	@Override
	 public boolean hasSendPackages()
	 {
		 SendPackageLineItemIfc[] sendPackages = null;
		 if ((this.transaction instanceof RetailTransactionIfc)) {
			 sendPackages = ((RetailTransactionIfc)this.transaction).getSendPackages();
	      }
		 if ((!this.transaction.isSuspended()) && (sendPackages != null))
		 {
	      for (int i = 0; i < sendPackages.length; i++)
	      {
	 
	    	  if (!sendPackages[i].isExternalSend())
	    	  {
	 
	            if (this.nextSendPackInfo == null)
	            {
	              this.nextSendPackInfo = new SendPackInfo(sendPackages[i], i + 1, (TenderableTransactionIfc)this.transaction);
	              return true;
	            }
	            if ((this.nextSendPackInfo.getSendPackage().equals(sendPackages[i])) && (this.nextSendPackInfo.getSendLabelCount() == i + 1))
	            {
	              this.nextSendPackInfo = null; }
	             }
	        }
	      }
	      return false;
	 }
	public SendPackInfo getNextSendPackageInfo()
	{
		return this.nextSendPackInfo;
	}
	protected MAXBakeryItemIfc[] bakery; 
	public MAXBakeryItemIfc[]  getBakeryItems() {
		return this.bakery;
	}
	@Override
	public void setBakeryItems(MAXBakeryItemIfc[] bakery) {
		this.bakery=bakery;
	}
	protected String categoryDesc; 
	public String getCategoryDesc() {
		return this.categoryDesc;
	}
	@Override
	public void setCategoryDesc(String categoryDesc) {
		this.categoryDesc=categoryDesc;
	}
	//changes starts for Rev 2.6 added by atul shukla
	protected MAXPaytmResponse paytmResponse; 
	@Override
	public MAXPaytmResponse getPaytmResponse() {
		return this.paytmResponse;
	}

	@Override
	public void setPaytmResponse(MAXPaytmResponse paytmResponse) {
		this.paytmResponse=paytmResponse;
	}
	
	protected MAXMobikwikResponse mobikwikResponse; 
	@Override
	public MAXMobikwikResponse getMobikwikResponse() {
		return this.mobikwikResponse;
	}

	@Override
	public void setMobikwikResponse(MAXMobikwikResponse mobikwikResponse) {
		this.mobikwikResponse=mobikwikResponse;
	}
	
	protected MAXAmazonPayResponse amazonPayResponse; 

	public MAXAmazonPayResponse getAmazonPayResponse() {
		return amazonPayResponse;
	}

	public void setAmazonPayResponse(MAXAmazonPayResponse amazonPayResponse) {
		this.amazonPayResponse = amazonPayResponse;
	}

	public MAXMcouponIfc mcoupon;

	/**
	 * @return the mcoupon
	 */
	@Override
	public MAXMcouponIfc getMcoupon() {
		return mcoupon;
	}

	/**
	 * @param mcoupon
	 *            the mcoupon to set
	 */
	@Override
	public void setMcoupon(MAXMcouponIfc mcoupon) {
		this.mcoupon = mcoupon;
	}

	/* changes for Rev 2.7 end */	


	// below code is added by atul shukla
	public String companyName="";
	@Override
	public void setEmployeeCompanyName(String companyName) {
		 this.companyName=companyName;
	}

	@Override
	public String getEmployeeCompanyName() {
		
		return companyName;
	}

	public String eReceiptOTPNumber=null;
	
	public String getEReceiptOTPNumber() {
		return eReceiptOTPNumber;
	}

	public void setEReceiptOTPNumber(String eReceiptOTP) {
		this.eReceiptOTPNumber = eReceiptOTP;
	}
	
	// Changes start by Bhanu priya for PAN card CR
	public String pancard=null;
	public String form60id=null;
	public String passportNum=null;
	public String acknum=null;
	public String visaNum=null;
	public String TaxableAmount=null;
	
	@Override
	public String getPanCardNumber() {
		return pancard;
	}

	@Override
	public void setPanCardNumber(String pancard) {
		this.pancard=pancard;
	}

	@Override
	public String getForm60Number() {
		return form60id;
	}

	@Override
	public void setForm60Number(String form60id) {
		this.form60id=form60id;
	}

	@Override
	public String getPassportNumber() {
		return passportNum;
	}

	@Override
	public String getITRAckNumber() {
		return acknum;
	}

	@Override
	public void setITRAckNumber(String acknum) {
		this.acknum=acknum;
	}

	@Override
	public String getVisaNumber() {
		return visaNum;
	}

	@Override
	public void setPassportrdNumber(String passportNum) {
		this.passportNum=passportNum;
	}

	@Override
	public void setVisNumber(String visaNum) {
		this.visaNum=visaNum;
	}

	// Changes End by Bhanu priya for PAN card CR
	
	public MAXPaytmQRCodeResponse getPaytmQRCodeResponse() {
		return paytmQRCodeResponse;
	}

	public void setPaytmQRCodeResponse(MAXPaytmQRCodeResponse paytmQRCodeResponse) {
		this.paytmQRCodeResponse = paytmQRCodeResponse;
	}
	
	public MAXGSTINValidationResponseIfc getTransactionGSTDetails() {
		//System.out.println("Return Gstin numner--------------------");
		MAXSaleReturnTransactionIfc maxSaleReturn = null;
		if (transaction != null && transaction instanceof MAXSaleReturnTransactionIfc) {
		//	System.out.println("transaction ___________________________________");
			if (transaction instanceof MAXLayawayTransaction) {
				//System.out.println(" MAXLayawayTransaction----------------------");
				maxSaleReturn = (MAXLayawayTransaction) transaction;
			}
			else {
				maxSaleReturn = (MAXSaleReturnTransaction) transaction;
				//System.out.println(maxSaleReturn +"transaction return-----------------------");
			}
			System.out.println(maxSaleReturn.getGstinresp()+"return-----------------------");
			return maxSaleReturn.getGstinresp();
			
		}
		//System.out.println( "fsffsggffggfdf---------------");
		return null;
	}
	public String getExchangeDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-YYYY");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 30);
		return sdf.format(cal.getTime());
		
	}

	@Override
	
	public void setTaxableAmount(String taxableAmount) {
		TaxableAmount = taxableAmount;
	}

	@Override
	public String getTaxableAmount() {
		// TODO Auto-generated method stub
		return TaxableAmount;
	}

		
}
