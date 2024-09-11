package max.retail.stores.pos.services.dailyoperations.poscount;

import max.retail.stores.domain.tender.MAXTenderLineItemIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.dailyoperations.poscount.PosCountCargo;

public class MaxEComPrepaidSelectedAisle  extends PosLaneActionAdapter {
	private static final long serialVersionUID = -101415319029258993L;
	public static String revisionNumber = "$Revision: 3$";

	public void traverse(BusIfc bus) {
		PosCountCargo cargo = (PosCountCargo) bus.getCargo();
		cargo.setCurrentActivityOrCharge(DomainGateway.getFactory()
				.getTenderTypeMapInstance()
				.getDescriptor(MAXTenderLineItemIfc.TENDER_TYPE_ECOM_PREPAID));

		String letterName = "CountSummary";
		if (!cargo.getSummaryFlag()) {
			if (cargo.currentHasDenominations()) {
				letterName = "CashDetail";
			} else {
				letterName = "CountDetail";
			}
		}

		bus.mail(new Letter(letterName), BusIfc.CURRENT);
	}


	public String toString() {
		String strResult = new String("Class:  " + getClass().getName()
				+ "(Revision " + getRevisionNumber() + ")" + hashCode());
		return (strResult);
	}

	public String getRevisionNumber() {
		return (revisionNumber);
	}
}
