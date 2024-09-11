package max.retail.stores.pos.ui.beans;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXUserDefinedCouponBeanModel extends POSBaseBeanModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3553094025808560661L;

	protected int quantity=0;
	
	protected CurrencyIfc amount=null;

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public CurrencyIfc getAmount() {
		return amount;
	}

	public void setAmount(CurrencyIfc amount) {
		this.amount = amount;
	}
	
	
	
}
