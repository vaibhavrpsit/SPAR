/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013	MAX HyperMarkets.    All Rights Reserved.
	Rev 1.0 	20/05/2013		Prateek		Initial Draft: Changes for TIC Customer Integration
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.tender;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;

public interface MAXTenderLoyaltyPointsIfc extends TenderLineItemIfc {

	public void setFaceValue(CurrencyIfc faceValue);

	public CurrencyIfc getFaceValue();

	public void setLoyaltyCardNumber(String loyaltyCardNumber);

	public String getLoyaltyCardNumber();

	public String getLoyaltyPointAmount();

	public void setLoyaltyPointAmount(String loyaltyPointAmount);

	public void setTypeCode(int type);

}
