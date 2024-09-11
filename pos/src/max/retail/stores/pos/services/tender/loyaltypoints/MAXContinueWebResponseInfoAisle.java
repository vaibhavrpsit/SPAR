/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev	1.0 	Jan 06, 2017		Ashish Yadav		Changes for Online redemption loyalty OTP FES	
 *
 ********************************************************************************/

package max.retail.stores.pos.services.tender.loyaltypoints;

import java.util.HashMap;

import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.pos.ado.tender.MAXTenderConstants;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;

public class MAXContinueWebResponseInfoAisle extends PosLaneActionAdapter {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -706891723202453083L;

	public void traverse(BusIfc bus) {
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		HashMap tenderAttributes = cargo.getTenderAttributes();

		if (CommonLetterIfc.OK.equals(bus.getCurrentLetter().getName())) {
			bus.mail(new Letter("Undo"), BusIfc.CURRENT);
		} else {
			// Changes starts for rev 1.0 (Ashish : Loyalty OTP)
			tenderAttributes.put(TenderConstants.AMOUNT, tenderAttributes.get(MAXTenderConstants.LOYALTY_CARD_REDEEM_AMOUNT).toString());
			tenderAttributes.put(TenderConstants.FACE_VALUE_AMOUNT, tenderAttributes.get(MAXTenderConstants.LOYALTY_CARD_REDEEM_AMOUNT).toString());
			// Changes starts for rev 1.0 (Ashish : Loyalty OTP)
			/**Changes end for rev 1.1**/
			MAXCustomerIfc customer = (MAXCustomerIfc) cargo.getCustomer();
			tenderAttributes.put(TenderConstants.NUMBER, customer.getLoyaltyCardNumber());
			/* Changes for Rev 1.1 ends */
			tenderAttributes.put(TenderConstants.TENDER_TYPE, "Loyalty Points");
			bus.mail(new Letter("Continue"), BusIfc.CURRENT);
		}

	}
}
