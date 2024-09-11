/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 *	Rev 1.0			27 Oct 2017			Jyoti Yadav				Changes for Innoviti Integration CR
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.ado.tender;

import java.util.HashMap;

import max.retail.stores.domain.tender.MAXTenderChargeIfc;
import max.retail.stores.domain.tender.MAXTenderDebitIfc;
import oracle.retail.stores.pos.ado.tender.TenderDebitADO;
import oracle.retail.stores.pos.ado.tender.TenderException;

public class MAXTenderDebitADO extends TenderDebitADO {
	public void setTenderAttributes(HashMap tenderAttributes) throws TenderException {
		super.setTenderAttributes(tenderAttributes);
		// setting the TenderAttributes with AUTH_CODE and BANK_CODE
		if (tenderRDO instanceof MAXTenderDebitIfc && tenderAttributes.get(MAXTenderConstants.AUTH_CODE) != null
				&& tenderAttributes.get(MAXTenderConstants.BANK_CODE) != null && tenderAttributes.get(MAXTenderConstants.BANK_NAME) != null) {

			((MAXTenderDebitIfc) tenderRDO).setAuthCode(tenderAttributes.get(MAXTenderConstants.AUTH_CODE).toString());
			((MAXTenderDebitIfc) tenderRDO).setBankCode(tenderAttributes.get(MAXTenderConstants.BANK_CODE).toString());
			((MAXTenderDebitIfc) tenderRDO).setCardType(tenderAttributes.get(MAXTenderConstants.BANK_NAME).toString());
		}
	}
	
	public HashMap getTenderAttributes() {
		HashMap map = super.getTenderAttributes();
		// The AUTH_CODE and BANK_CODE to be displayed in EJournal
		map.put(MAXTenderConstants.AUTH_CODE, ((MAXTenderChargeIfc) tenderRDO).getAuthCode());
		map.put(MAXTenderConstants.BANK_CODE, ((MAXTenderChargeIfc) tenderRDO).getBankCode());

		return map;
	}
}
