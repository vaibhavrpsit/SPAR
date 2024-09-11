/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013	MAX HyperMarkets.    All Rights Reserved.
    
   	Rev 1.0 	20/05/2013		Bhanu Priya 		Initial Draft: Mobikwik Changes 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package max.retail.stores.domain.tender;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;

public interface MAXTenderMobikwikIfc extends TenderLineItemIfc {

	public void setFaceValue(CurrencyIfc faceValue);

	public CurrencyIfc getFaceValue();

	public void setMobikwikMobileNumber(String moblieno);

	public String getMobikwikMobileNumber();

	public String getMobikwikAmount();

	public void setMobikwikAmount(String mobikwikAmt);

	public void setTypeCode(int type);

	public void setMobikwikWalletTransactionID(String walletTransID);

	public String getMobikwikWalletTransactionID();
	
	public void setMobikwikOrderID(String orderID);

	public String getMobikwikOrderID();
	
}
