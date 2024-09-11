/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.1	13/Aug/2013	  	Prateek, Changes done for Special Order - Suggested Tender Type
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.order;

import java.util.Vector;

import max.retail.stores.domain.lineitem.MAXOrderLineItemIfc;
import max.retail.stores.domain.transaction.MAXOrderTransactionIfc;
import max.retail.stores.domain.transaction.MAXTransactionIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.PaymentConstantsIfc;
import oracle.retail.stores.domain.financial.PaymentHistoryInfoIfc;
import oracle.retail.stores.domain.financial.PaymentIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.Order;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSTime;
import oracle.retail.stores.foundation.utility.Util;

public class MAXOrder extends Order implements MAXOrderIfc {

	private static final long serialVersionUID = 8530120163867194041L;

	protected boolean alterOrder = false;

	protected boolean hasShippingCharge = false;

	protected EYSDate expectedDeliveryDate;

	protected EYSTime expectedDeliveryTime;

	protected Vector deletedItems = new Vector();

	protected boolean trainingMode = false;

	protected String trainingModeFlag = "0";

	public Vector getDeletedItems() {
		return deletedItems;
	}

	public void setDeletedItems(Vector deletedItems) {
		this.deletedItems = deletedItems;
	}

	public EYSDate getExpectedDeliveryDate() {
		return expectedDeliveryDate;
	}

	public void setExpectedDeliveryDate(EYSDate expectedDeliveryDate) {
		this.expectedDeliveryDate = expectedDeliveryDate;
	}

	public EYSTime getExpectedDeliveryTime() {
		return expectedDeliveryTime;
	}

	public void setExpectedDeliveryTime(EYSTime expectedDeliveryTime) {
		this.expectedDeliveryTime = expectedDeliveryTime;
	}

	public boolean isHasShippingCharge() {
		return hasShippingCharge;
	}

	public void setHasShippingCharge(boolean hasShippingCharge) {
		this.hasShippingCharge = hasShippingCharge;
	}

	public boolean isAlterOrder() {
		return alterOrder;
	}

	public void setAlterOrder(boolean alterOrder) {
		this.alterOrder = alterOrder;
	}

	// fix for bug 6615
	protected TransactionIfc originalTransaction = null;

	// end
	public OrderTransactionIfc createOrderTransaction(int[] indexes) { // begin
																		// createTransaction()

		OrderTransactionIfc transaction = DomainGateway.getFactory().getOrderTransactionInstance();
		((MAXTransactionIfc) transaction).setRounding(((MAXTransactionIfc) this.getTotals()).getRounding());
		((MAXTransactionIfc) transaction)
				.setRoundingDenominations(((MAXTransactionIfc) this.getTotals()).getRoundingDenominations());
		// set main attributes
		transaction.setTransactionTax(getTransactionTax());
		transaction.setTransactionDiscounts(getTransactionDiscounts());
		transaction.setSalesAssociate(getSalesAssociate());
		transaction.setCustomer(customer);

		if (transaction instanceof MAXOrderTransactionIfc) {
			((MAXOrderTransactionIfc) transaction).setExpectedDeliveryDate(getExpectedDeliveryDate());
			((MAXOrderTransactionIfc) transaction).setExpectedDeliveryTime(getExpectedDeliveryTime());
			((MAXOrderTransactionIfc) transaction).setSuggestedTender(getSuggestedTender());
		}
		for (int k = 0; k < this.getPaymentHistoryInfoCollection().size(); k++) {
			PaymentHistoryInfoIfc paymentHistoryInfo = (PaymentHistoryInfoIfc) getPaymentHistoryInfoCollection().get(k);
			transaction.addPaymentHistoryInfo((PaymentHistoryInfoIfc) paymentHistoryInfo.clone());
		}

		String orderID = getOrderID();
		transaction.setOrderID(orderID);

		// push across picked up line items
		int numLines = 0;
		if (indexes != null) {
			numLines = indexes.length;
		}

		CurrencyIfc totalCanceledItemsPrice = DomainGateway.getBaseCurrencyInstance();
		CurrencyIfc totalItemsDue = DomainGateway.getBaseCurrencyInstance();

		Vector itemsVector = new Vector();
		for (int i = 0; i < numLines; i++) {
			AbstractTransactionLineItemIfc item = retrieveItemByIndex(indexes[i]);
			item.setLineNumber(i);
			itemsVector.addElement(item);
			if (item instanceof SaleReturnLineItemIfc) {
				SaleReturnLineItemIfc srLineItem = (SaleReturnLineItemIfc) item;
				CurrencyIfc deposit = srLineItem.getOrderItemStatus().getDepositAmount();
				CurrencyIfc itemBalance = null;
				if (srLineItem.getOrderItemStatus().getStatus()
						.getStatus() == MAXOrderLineItemIfc.ORDER_ITEM_STATUS_CANCELED) {
					itemBalance = deposit.negate();
					totalCanceledItemsPrice = totalCanceledItemsPrice.add(srLineItem.getItemPrice().getItemTotal());
				} else {
					itemBalance = srLineItem.getItemPrice().getItemTotal().subtract(deposit);
				}
				totalItemsDue = totalItemsDue.add(itemBalance);
			}
		}

		CurrencyIfc balanceDue = status.getBalanceDue();
		((PaymentIfc) status)
				.setBalanceDue(status.getBalanceDue().subtract(totalItemsDue).subtract(totalCanceledItemsPrice));

		PaymentIfc payment = DomainGateway.getFactory().getPaymentInstance();
		if (this.getTotals().getBalanceDue().equals(balanceDue)) {
			payment.setBalanceDue(balanceDue);
		} else {
			payment.setBalanceDue(totalItemsDue);
		}
		payment.setPaymentAmount(totalItemsDue);
		if (isHasShippingCharge())
			payment.setPaymentAmount(payment.getPaymentAmount().add(getTotals().getBalanceDue()));
		payment.setBalanceDue(status.getBalanceDue());
		payment.setTransactionID(transaction.getTransactionIdentifier());
		payment.setDescription("Special Order Payment");
		payment.setPaymentAccountType(PaymentConstantsIfc.ACCOUNT_TYPE_ORDER);
		payment.setReferenceNumber(orderID);

		transaction.setOrderStatus(status);
		transaction.setPayment(payment);

		if (numLines > 0) {
			int size = itemsVector.size();
			OrderLineItemIfc[] o = new OrderLineItemIfc[size];
			itemsVector.copyInto(o);
			transaction.setLineItems(o);
		}

		return (transaction);
	}

	public Object clone() {
		// TODO Auto-generated method stub
		MAXOrder c = new MAXOrder();

		// set attributes
		setCloneAttributes(c);

		// pass back Object
		return ((Object) c);
	}

	protected void setCloneAttributes(MAXOrder newClass) {
		// TODO Auto-generated method stub
		super.setCloneAttributes(newClass);
		newClass.setAlterOrder(alterOrder);
		newClass.setHasShippingCharge(hasShippingCharge);
		newClass.setDeletedItems(getDeletedItems());
		// fix for bug 6615
		if (originalTransaction != null) {
			newClass.setOriginalTransaction((SaleReturnTransactionIfc) originalTransaction.clone());
		}
		newClass.setSuggestedTender(getSuggestedTender());
		// end
	}

	@SuppressWarnings("deprecation")
	public boolean equals(Object obj) { // begin equals()
		boolean isEqual = false;
		if (obj instanceof Order) { // begin compare objects
			Order c = (Order) obj; // downcast the input object

			// compare all the attributes of Order
			if (Util.isObjectEqual(getItemContainerProxy(), c.getItemContainerProxy())
					&& Util.isObjectEqual(getTenderLineItems(), c.getTenderLineItems())
					&& Util.isObjectEqual(totals, c.getTotals()) && Util.isObjectEqual(customer, c.getCustomer())
					&& Util.isObjectEqual(shipToStore, c.getShipToStore())
					&& Util.isObjectEqual(location, c.getLocation()) && Util.isObjectEqual(getStatus(), c.getStatus())
					&&
					// orderStatus == c.getOrderStatus() &&
					// previousOrderStatus == c.getPreviousOrderStatus() &&
					// Util.isObjectEqual(lastStatusChange,
					// c.getLastStatusChange()) &&
					// Util.isObjectEqual(timestampBegin, c.getTimestampBegin())
					// &&
					// Util.isObjectEqual(timestampCreated,
					// c.getTimestampCreated()) &&
			trainingModeFlag == c.getTrainingModeFlag() && trainingMode == ((MAXOrder) c).getTrainingMode() &&
					// fix for bug 6615
					// Util.isObjectEqual(this.paymentHistoryInfoCollection,
					// c.getPaymentHistoryInfoCollection()))
			Util.isObjectEqual(this.paymentHistoryInfoCollection, c.getPaymentHistoryInfoCollection())
					&& Util.isObjectEqual(originalTransaction, c.getOriginalTransaction()))
			// end
			{
				isEqual = true; // set the return code to true
			} else {
				isEqual = false; // set the return code to false
			}
		} // end compare objects
		return (isEqual);
	}

	// fix for bug 6615
	public void setOriginalTransaction(TransactionIfc transaction) {
		this.originalTransaction = transaction;
	}

	public TransactionIfc getOriginalTransaction() {
		return originalTransaction;
	}
	// end

	/** MAX Rev 1.1 Change : Start **/
	protected String suggestedTender = null;

	public String getSuggestedTender() {
		return suggestedTender;
	}

	public void setSuggestedTender(String suggestedTender) {
		this.suggestedTender = suggestedTender;
	}

	/** MAX Rev 1.1 Change : End **/
	public boolean getTrainingMode() { // begin getTrainingMode()
		return (trainingMode);
	}

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves training mode flag.
	 * <P>
	 * 
	 * @return training mode flag
	 **/
	// ----------------------------------------------------------------------------
	public String getTrainingModeFlag() { // begin getTrainingModeFlag()
		return (trainingModeFlag);
	} // end setTrainingModeFlag()
		// ----------------------------------------------------------------------------

	/**
	 * Sets training mode flag.
	 * <P>
	 * 
	 * @param value
	 *            training mode flag
	 * @deprecated use setTrainingModeFlag instead
	 **/
	// ----------------------------------------------------------------------------
	public void setTrainingMode(boolean value) { // begin setTrainingMode()
		trainingMode = value;
	} // end setTrainingMode()
		// ----------------------------------------------------------------------------

	/**
	 * Sets training mode flag.
	 * <P>
	 * 
	 * @param value
	 *            training mode flag
	 **/
	// ----------------------------------------------------------------------------
	public void setTrainingModeFlag(String value) { // begin
													// setTrainingModeFlag()
		trainingModeFlag = value;
	}
}
