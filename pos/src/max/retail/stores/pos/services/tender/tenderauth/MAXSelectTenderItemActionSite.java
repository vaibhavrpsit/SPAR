/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
  	Rev 1.0  24/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.tenderauth;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

public class MAXSelectTenderItemActionSite extends PosSiteActionAdapter{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3657943721666247833L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extendyourstore.foundation.tour.application.SiteActionAdapter#arrive
	 * (com.extendyourstore.foundation.tour.ifc.BusIfc)
	 */
	public void arrive(BusIfc bus) {
		// get the next tender item from the transaction
		MAXTenderAuthCargo cargo = (MAXTenderAuthCargo) bus.getCargo();
		// get all pending authorizable tenders
		if (cargo.getCurrentTransactionADO() != null) {
			TenderADOIfc[] tenders = cargo.getCurrentTransactionADO().getTenderLineItems(cargo.getTenderCategory());

			// if empty, we're done, otherwise, get type of tender and route to
			// proper
			// site

			if (tenders.length == 0) // we're done
			{
				String letterName = "Success";
				if (cargo.getGiftCardDepletedAmount() != null) {
					letterName = "Deplete";
				}

				bus.mail(new Letter(letterName), BusIfc.CURRENT);
			} else {
				// set the tender into cargo
				cargo.setCurrentAuthTender(tenders[0]);
				TenderTypeEnum tenderType = tenders[0].getTenderType();
				bus.mail(new Letter(tenderType.toString()), BusIfc.CURRENT);
				//if (tenders[0] instanceof TenderGiftCardADO) {
					//bus.mail(new Letter("Success"), BusIfc.CURRENT);
				//}

			}
		} else {
			bus.mail(new Letter("Success"), BusIfc.CURRENT);
		}
	}


}
