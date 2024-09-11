/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 *	Rev 1.0			27 Oct 2017			Jyoti Yadav				Changes for Innoviti Integration CR
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.domain.tender;

import oracle.retail.stores.domain.tender.TenderDebitIfc;

public interface MAXTenderDebitIfc extends TenderDebitIfc {

	public String getAuthCode();

	public void setAuthCode(String authCode);

	public String getBankCode();

	public void setBankCode(String bankCode);

	public boolean isEmiTransaction();

	public void setEmiTransaction(boolean emiTransaction);

}
