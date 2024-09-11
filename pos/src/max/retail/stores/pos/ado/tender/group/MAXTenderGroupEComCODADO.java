/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2016	MAX HyperMarkets.    All Rights Reserved.
 
	Rev 1.0 	12/07/2016		Abhishek Goyal		Initial Draft: Changes for CR
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ado.tender.group;

import java.util.HashMap;

import max.retail.stores.pos.ado.tender.MAXTenderTypeEnum;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.tender.group.AbstractTenderGroupADO;

public class MAXTenderGroupEComCODADO extends AbstractTenderGroupADO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TenderTypeEnum getGroupType() {
		// TODO Auto-generated method stub
		return MAXTenderTypeEnum.ECOM_COD;
	}

	public TenderTypeEnum getVoidType() {
		// TODO Auto-generated method stub
		return getGroupType();
	}

	public void validateLimits(HashMap tenderAttributes, CurrencyIfc balanceDue)
			throws TenderException {
		// TODO Auto-generated method stub
		
	}
	
	public void validateOvertender(HashMap tenderAttributes, CurrencyIfc balanceDue, CurrencyIfc overtenderLimit)
    throws TenderException
    {
	    CurrencyIfc tenderAmount = parseAmount((String)tenderAttributes.get("AMOUNT"));
	    if (tenderAmount.compareTo(balanceDue) == 1) {
	        	throw new TenderException("Overtender not allowed", TenderErrorCodeEnum.OVERTENDER_ILLEGAL);
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
