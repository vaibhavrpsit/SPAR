/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 * Rev  	1.0  	21 Dec, 2016              Ashish Yadav              Credit Card FES
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.tender.creditdebit;

import java.util.HashMap;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderDebitADO;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;

public class MAXConvertDebitToCreditActionSite extends PosSiteActionAdapter {
	public void arrive(BusIfc bus) {
		TenderCargo cargo = (TenderCargo) bus.getCargo();

		TenderDebitADO debit = (TenderDebitADO) cargo.getTenderADO();

		HashMap tenderAttributes = debit.getTenderAttributes();
		tenderAttributes.put("TENDER_TYPE", TenderTypeEnum.CREDIT);

		cargo.setTenderADO(null);
		cargo.setTenderAttributes(tenderAttributes);

		bus.mail(new Letter("Continue"), BusIfc.CURRENT);
	}
}