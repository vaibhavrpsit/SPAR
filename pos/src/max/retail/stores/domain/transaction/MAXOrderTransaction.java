/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.4  13/08/2013     Prateek, Changes done for Special Order CR - Food Totals, Suggested Tender Type
  Rev 1.3  30/07/2013     Jyoti Rawal, Bug 7354 - Remove employee discount : POS crashed 
  Rev 1.2  04/07/2013     Jyoti Rawal, Bug 6842 - Special Order: Expected Delivery Date and time not being printed on receipt
  Rev 1.1  27/06/2013     Jyoti Rawal, Fix for Bug 6662 Special Order : POS Crashed while tendering using GC
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.transaction;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;

import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.transaction.OrderTransaction;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSTime;

public class MAXOrderTransaction extends OrderTransaction implements MAXOrderTransactionIfc {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7461847862067999002L;

	protected EYSDate expectedDeliveryDate;

	protected EYSTime expectedDeliveryTime;
	// Rev 1.1 changes start
	public HashMap cardNumAck1 = new HashMap();
	protected boolean isFatalDeviceCall = false;

	// Rev 1.1 changes end
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

	// Rev 1.1 changes start
	public boolean isFatalDeviceCall() {
		return isFatalDeviceCall;
	}

	public void setFatalDeviceCall(boolean isFatalDeviceCall) {
		this.isFatalDeviceCall = isFatalDeviceCall;
	}
	// Rev 1.1 changes end

	public Object clone() {
		// TODO Auto-generated method stub
		MAXOrderTransaction c = new MAXOrderTransaction();

		// set values
		setCloneAttributes(c);

		// pass back Object
		return ((Object) c);
	}

	public void setCloneAttributes(MAXOrderTransaction newClass) {
		// TODO Auto-generated method stub
		super.setCloneAttributes(newClass);

		if (expectedDeliveryDate != null)
			newClass.setExpectedDeliveryDate(expectedDeliveryDate);
		if (expectedDeliveryTime != null)
			newClass.setExpectedDeliveryTime(expectedDeliveryTime); // Rev 1.2
																	// changes
		if (foodTotals != null)
			newClass.setFoodTotals(foodTotals);
		if (suggestedTender != null)
			newClass.setSuggestedTender(suggestedTender);
	}

	// Rev 1.3 changes start
	public void clearEmployeeDiscount(TransactionDiscountStrategyIfc tds) {
		Iterator itr = itemProxy.getTransactionDiscountsIterator();
		while (itr.hasNext()) {
			if (itr.next() == tds) {
				itr.remove();
				break;
			}
		}

	}

	// Rev 1.3 changes end
	/** MAX Rev 1.4 Change : Start **/
	protected BigDecimal foodTotals = null;
	protected String suggestedTender = null;

	public BigDecimal getFoodTotals() {
		return foodTotals;
	}

	public void setFoodTotals(BigDecimal foodTotals) {
		this.foodTotals = foodTotals;
	}

	public String getSuggestedTender() {
		return suggestedTender;
	}

	public void setSuggestedTender(String suggestedTender) {
		this.suggestedTender = suggestedTender;
	}
	/** MAX Rev 1.4 Change : End **/
}
