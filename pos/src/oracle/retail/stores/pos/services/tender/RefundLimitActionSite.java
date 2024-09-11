package oracle.retail.stores.pos.services.tender;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.ReturnableTransactionADOIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class RefundLimitActionSite extends PosSiteActionAdapter {
	public void arrive(BusIfc bus) {
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		String refundTenderLetter = null;

		RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
		boolean isReturnWithReceipt = ((ReturnableTransactionADOIfc) txnADO).isReturnWithReceipt();
		boolean isReturnWithOriginalRetrieved = ((ReturnableTransactionADOIfc) txnADO).isReturnWithOriginalRetrieved();
		//txnADO.validateRefundLimits(cargo.getTenderAttributes(), isReturnWithReceipt,isReturnWithOriginalRetrieved);
		refundTenderLetter = cargo.getRefundTenderLetter();
		cargo.setRefundTenderLetter((String) null);

		if (refundTenderLetter != null) {
			bus.mail(refundTenderLetter, BusIfc.CURRENT);
		} else {
			bus.mail(bus.getCurrentLetter().getName(), BusIfc.CURRENT);
		}

	}
}