/********************************************************************************
 *   
 *	Copyright (c) 2019 MAX SPAR Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.0 	June 01, 2019		Purushotham Reddy 	Changes for POS_Amazon Pay Integration 
 *
 ********************************************************************************/

package max.retail.stores.pos.ado.tender.group;

/**
@author Purushotham Reddy Sirison
**/

import java.util.HashMap;

import max.retail.stores.pos.ado.tender.MAXTenderTypeEnum;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.tender.group.AbstractTenderGroupADO;

public class MAXTenderGroupAmazonPayADO extends AbstractTenderGroupADO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TenderTypeEnum getGroupType() {
		return MAXTenderTypeEnum.AMAZON_PAY;
	}

	public TenderTypeEnum getVoidType() {
		return getGroupType();
	}

	public void validateLimits(HashMap tenderAttributes, CurrencyIfc balanceDue)
			throws TenderException {
		
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
		
	}

	public EYSDomainIfc toLegacy() {
		return null;
	}

	public EYSDomainIfc toLegacy(Class arg0) {
		return null;
	}

}
