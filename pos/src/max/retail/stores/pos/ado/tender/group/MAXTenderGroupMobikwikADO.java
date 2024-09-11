/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013	MAX HyperMarkets.    All Rights Reserved.
	Rev 1.0 	16/11/2017		Bhanu Priya		Initial Draft: Changes for paytm Integration
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package max.retail.stores.pos.ado.tender.group;

import java.util.HashMap;

import max.retail.stores.pos.ado.tender.MAXTenderTypeEnum;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.tender.group.AbstractTenderGroupADO;
import oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc;

public class MAXTenderGroupMobikwikADO extends AbstractTenderGroupADO
		implements TenderGroupADOIfc {

	public TenderTypeEnum getGroupType() {
		// TODO Auto-generated method stub
		return MAXTenderTypeEnum.MOBIKWIK;
	}

	public TenderTypeEnum getVoidType() {
		return getGroupType();
	}

	public void validateLimits(HashMap tenderAttributes, CurrencyIfc balanceDue)
			throws TenderException {
		 CurrencyIfc tenderAmount = parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT));
	        
	     if(tenderAmount.compareTo(balanceDue) > CurrencyIfc.EQUALS)
	     {
	        TenderException childException = new TenderException("Loyalty Points amount is greater than Balance Due",
	                                  TenderErrorCodeEnum.MAX_CHANGE_LIMIT_VIOLATED);
	        throw new TenderException("Loyalty Points amount does not equal Balance Due",
	                                      TenderErrorCodeEnum.INVALID_AMOUNT,
	                                      childException);
	     }
	     else if(tenderAmount.compareTo(balanceDue) != CurrencyIfc.EQUALS)
	     {
	    	 throw new TenderException("Loyalty Points amount does not equal Balance Due",
	                                      TenderErrorCodeEnum.INVALID_AMOUNT);
	     }

	}

	public void fromLegacy(EYSDomainIfc arg0) {
		// TODO Auto-generated method stub

	}

	public EYSDomainIfc toLegacy() {
		// TODO Auto-generated method stub
		return null;
	}

	public EYSDomainIfc toLegacy(Class arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
