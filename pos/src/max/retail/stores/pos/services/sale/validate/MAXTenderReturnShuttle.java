/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package max.retail.stores.pos.services.sale.validate;

import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.pos.services.sale.MAXSaleCargoIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.services.sale.validate.TenderReturnShuttle;

import org.apache.log4j.Logger;

public class MAXTenderReturnShuttle extends TenderReturnShuttle {
	static final long serialVersionUID = -8288983768835929378L;
	protected boolean isGiftCardApproved =  false;
	protected static Logger logger = Logger
			.getLogger(MAXTenderReturnShuttle.class);
	public static final String revisionNumber = "$Revision: 3$";
	

	public void load(BusIfc bus) {
		super.load(bus);
	}

	public void unload(BusIfc bus) {
		super.unload(bus);
		

		SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();
		if(transaction instanceof MAXSaleReturnTransactionIfc){
		cargo.setTransaction((MAXSaleReturnTransactionIfc) this.transaction);
		}
		((MAXSaleCargoIfc) cargo).setGiftCardApproved(this.isGiftCardApproved);
	}


}