/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *  Rev 1.0     Dec 19, 2016	        Ashish Yadav		Initial Changes for StoreCredit FES
 *
 ********************************************************************************/

package max.retail.stores.pos.ado.tender;

import oracle.retail.stores.domain.tender.TenderCertificateIfc;
import oracle.retail.stores.pos.ado.tender.CertificateValidatorIfc;
import oracle.retail.stores.pos.ado.tender.TenderUtilityFactory;

public class MAXTenderUtilityFactory extends TenderUtilityFactory {

	/**
	 * Change for MAX: Rev 1.0 Creates a certificate validator instance.
	 * 
	 * @return certificateValidator
	 */
	public CertificateValidatorIfc createCertificateValidator(TenderCertificateIfc tenderRDO) {
		MAXCertificateValidatorIfc certificateValidator = new MAXCertificateValidator(tenderRDO);
		return certificateValidator;
	}
}
