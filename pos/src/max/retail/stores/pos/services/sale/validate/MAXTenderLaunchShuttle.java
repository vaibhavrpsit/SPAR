/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package max.retail.stores.pos.services.sale.validate;

import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.sale.validate.TenderLaunchShuttle;
import oracle.retail.stores.pos.services.tender.TenderCargo;

public class MAXTenderLaunchShuttle extends TenderLaunchShuttle {
	static final long serialVersionUID = -8942316912525520108L;
	protected static Logger logger = Logger
			.getLogger(MAXTenderLaunchShuttle.class);
	public static final String revisionNumber = "$Revision: 3$";
	String panno = null;
	String formno = null;
	String passport = null;

	public void load(BusIfc bus) {
		super.load(bus);

	}

	public void unload(BusIfc bus) {
		super.unload(bus);
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		MAXSaleReturnTransaction trans = (MAXSaleReturnTransaction) cargo.getTransaction();
		try {
			panno = trans.getPanNumber();
			formno = trans.getForm60IDNumber();
			passport = trans.getPassportNumber();
			if (panno != null || formno != null || passport != null) {
				trans.setPanNumber(null);
				trans.setForm60IDNumber(null);
				trans.setPassportNumber(null);
			}
			// Changes for  E-Receipt Integration With Karnival
			if (trans.getEReceiptOTP() != null) {
				trans.setEReceiptOTP(null);
			}

		} catch (NullPointerException e) {
			logger.warn(e.getStackTrace());
		}
	}

}