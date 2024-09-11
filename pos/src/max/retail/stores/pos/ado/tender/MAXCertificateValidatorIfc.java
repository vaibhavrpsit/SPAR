/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *  Rev 1.0     Dec 19, 2016	        Ashish Yadav		Changes for StoreCredit FES
 *
 ********************************************************************************/

package max.retail.stores.pos.ado.tender;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.pos.ado.tender.CertificateValidatorIfc;

public interface MAXCertificateValidatorIfc extends CertificateValidatorIfc {

	// ----------------------------------------------------------------------
	/**
	 * Returns Amount.
	 * 
	 * @return Amount CurrencyIfc
	 */
	// ----------------------------------------------------------------------
	public CurrencyIfc getAmount();

	// ----------------------------------------------------------------------
	/**
	 * Returns ExpirationDate.
	 * 
	 * @return ExpirationDate EYSDate
	 */
	// ----------------------------------------------------------------------
	public EYSDate getExpirationDate();
	public String getStoreCreditStatus();
}
