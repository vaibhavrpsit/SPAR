/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013	MAX HyperMarkets.    All Rights Reserved.
    
   	Rev 1.0 	20/05/2013		Bhanu Priya 		Initial Draft: Paytm Changes 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package max.retail.stores.domain.tender;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;

public interface MAXTenderPaytmIfc extends TenderLineItemIfc {

	public void setFaceValue(CurrencyIfc faceValue);

	public CurrencyIfc getFaceValue();

	public void setPaytmMobileNumber(String moblieno);

	public String getPaytmMobileNumber();

	public String getPaytmAmount();

	public void setPaytmAmount(String paytmamout);

	public void setTypeCode(int type);

	public void setPaytmWalletTransactionID(String walletTransID);

	public String getPaytmWalletTransactionID();
	
	public void setPaytmOrderID(String orderID);

	public String getOrderID();
	
}
