/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
*  Rev 1.0  28/May/2013	Jyoti Rawal, Initial Draft: Changes for Credit Card Functionality 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender;

import max.retail.stores.domain.tender.MAXTenderPaytm;
import max.retail.stores.domain.tender.paytm.MAXPaytmTenderConstants;
import max.retail.stores.pos.ui.beans.MAXTenderBeanModel;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * @author Jyoti Rawal
 *
 */
public class MAXDeleteTenderActionAisle extends PosLaneActionAdapter 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2131129592500939641L;

	public void traverse(BusIfc bus) 
 {
		System.out.println("MAXDeleteTenderActionAisle");
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		MAXTenderCargo tenderCargo = (MAXTenderCargo) bus.getCargo();
		MAXTenderBeanModel model = (MAXTenderBeanModel) ui.getModel();
		TenderLineItemIfc tenderLineItem = null;

		if (model != null) {
			System.out.println("Delete for cash");
			tenderLineItem = model.getTenderToDelete();
			tenderCargo.setTenderLineItem(tenderLineItem);

		}
		bus.mail("EDCPostVoided");

	}
}
