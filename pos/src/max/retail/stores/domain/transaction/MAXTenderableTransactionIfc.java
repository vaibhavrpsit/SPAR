/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 * 
 * 
 * Rev 1.1  08 Nov, 2016    Nadia Arora     MAX-StoreCredi_Return requirement.
 * Rev 1.0	Aug 26,2016		Nitesh Kumar	changes for code merging
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.domain.transaction;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import max.retail.stores.domain.paytm.MAXPaytmResponse;
//import max.retail.stores.domain.MAXPaytmResponse;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.IRSCustomerIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;

public abstract interface MAXTenderableTransactionIfc extends TenderableTransactionIfc {
	public static final String revisionNumber = "$Revision: /main/22 $";

	public abstract boolean containsTenderLineItems(int paramInt);

	public abstract TenderLineItemIfc[] getTenderLineItemArray(int paramInt);

	public abstract Iterator<TenderLineItemIfc> getTenderLineItemIterator(int paramInt);

	public abstract Collection<TenderLineItemIfc> getTenderLineItems(int paramInt);

	public abstract void removeTenderLineItems();

	public abstract void removeTenderLineItems(int paramInt);

	public abstract void linkCustomer(CustomerIfc paramCustomerIfc);

	public abstract CustomerIfc getCustomer();

	public abstract void setCustomer(CustomerIfc paramCustomerIfc);

	public abstract IRSCustomerIfc getIRSCustomer();

	public abstract void setIRSCustomer(IRSCustomerIfc paramIRSCustomerIfc);

	public abstract String getUniqueID();

	public abstract void setUniqueID(String paramString);

	public abstract TenderLineItemIfc[] getCollectedTenderLineItems();

	public abstract boolean hasCollectedTenderLineItems();

	public abstract TenderStoreCreditIfc[] getIssuedStoreCredit();

	public abstract CurrencyIfc getCollectedTenderTotalAmount();

	public abstract CurrencyIfc getTenderTotalAmountPlusChangeDue();

	public abstract void setTenderLineItems(TenderLineItemIfc[] paramArrayOfTenderLineItemIfc);

	public abstract void addTenderLineItem(TenderLineItemIfc paramTenderLineItemIfc);

	public abstract void addTender(TenderLineItemIfc paramTenderLineItemIfc);

	public abstract TenderLineItemIfc[] getTenderLineItems();

	public abstract Vector<TenderLineItemIfc> getTenderLineItemsVector();

	public abstract void removeTenderLineItem(int paramInt);

	public abstract void removeTenderLineItem(TenderLineItemIfc paramTenderLineItemIfc);

	public abstract CurrencyIfc calculateChangeDue();

	public abstract CurrencyIfc calculateChangeGiven();

	public abstract MAXTransactionTotalsIfc getTransactionTotals();

	public abstract void setTransactionTotals(TransactionTotalsIfc paramTransactionTotalsIfc);

	public abstract TransactionTotalsIfc getTenderTransactionTotals();

	public abstract boolean exceedsMaxChangeLimit(TenderLineItemIfc paramTenderLineItemIfc);

	public abstract boolean exceedsMaxAmountLimit(TenderLineItemIfc paramTenderLineItemIfc);

	public abstract boolean exceedsMaxCashRefundLimit(TenderLineItemIfc paramTenderLineItemIfc);

	public abstract FinancialTotalsIfc getFinancialTotals();

	public abstract int getTenderLineItemsSize();

	public abstract void addECheckDeclinedItems(TenderLineItemIfc paramTenderLineItemIfc);

	public abstract Vector<TenderLineItemIfc> getECheckDeclinedItems();

	public abstract void updateTenderTotals();

	public abstract CurrencyIfc getNegativeCashTotal();

	public abstract String getReturnTicket();

	public abstract void setReturnTicket(String paramString);

	public abstract String getCustomerId();

	public abstract void setCustomerId(String paramString);

	/** Change for MAX Tic Customer Changes **/
	public CustomerIfc getTicCustomer();

	public void setTicCustomer(CustomerIfc ticCustomer);
	
	public MAXPaytmResponse getPaytmRespose();

	public void setPaytmRespose(MAXPaytmResponse paytm);
}