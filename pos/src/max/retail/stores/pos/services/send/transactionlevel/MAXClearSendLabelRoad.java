  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 * Rev 1.0  11/Jun/2013               Tanmaya            Bug 6093 - Incorrect marking of the not send status.
 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
 package max.retail.stores.pos.services.send.transactionlevel;

import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.modifytransaction.ModifyTransactionCargo;

public class MAXClearSendLabelRoad extends PosLaneActionAdapter {
	private static final long serialVersionUID = 6742412224299976913L;
	public static final String revisionNumber = "$Revision: 16$";

	public void traverse(BusIfc bus) {
		ModifyTransactionCargo cargo = (ModifyTransactionCargo) bus.getCargo();
		AbstractTransactionLineItemIfc[] lineItems = cargo.getTransaction()
				.getLineItems();

		for (int i = 0; i < lineItems.length; i++) {
			if (lineItems[i] instanceof SaleReturnLineItemIfc) {
				SaleReturnLineItemIfc item = (SaleReturnLineItemIfc)lineItems[i];
				if (item.getSendLabelCount() == -1)
					item.setSendLabelCount(0);
			}
		}

	}
}
