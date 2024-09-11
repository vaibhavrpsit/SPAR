/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2016	MAX HyperMarkets.    All Rights Reserved.
 
	Rev 1.0 	12/07/2016		Abhishek Goyal		Initial Draft: Changes for CR
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXEComOrderDetailsBeanModel extends POSBaseBeanModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String txtEComTransNoField = null;
	protected String txtEComOrderNoField = null;
	protected CurrencyIfc txtEComOrderAmountField = null;
	protected String[] orderTypes;
	protected int selectedOrderType;
	protected String orderType;
	
	public String getTxtEComTransNoField() {
		return txtEComTransNoField;
	}
	public void setTxtEComTransNoField(String txtEComTransNoField) {
		this.txtEComTransNoField = txtEComTransNoField;
	}
	public String getTxtEComOrderNoField() {
		return txtEComOrderNoField;
	}
	public void setTxtEComOrderNoField(String txtEComOrderNoField) {
		this.txtEComOrderNoField = txtEComOrderNoField;
	}
	public CurrencyIfc getTxtEComOrderAmountField() {
		return txtEComOrderAmountField;
	}
	public void setTxtEComOrderAmountField(CurrencyIfc txtEComOrderAmountField) {
		this.txtEComOrderAmountField = txtEComOrderAmountField;
	}
	public String[] getOrderTypes() {
		return orderTypes;
	}
	public void setOrderTypes(String[] orderTypes) {
		this.orderTypes = orderTypes;
	}
	public int getSelectedOrderType() {
		return selectedOrderType;
	}
	public void setSelectedOrderType(int selectedOrderType) {
		this.selectedOrderType = selectedOrderType;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

}
