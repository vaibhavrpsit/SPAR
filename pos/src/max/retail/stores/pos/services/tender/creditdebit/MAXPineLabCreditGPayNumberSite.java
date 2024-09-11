/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 *  Copyright (c) 2010 Lifestyle India Pvt Ltd.    All Rights Reserved. 
 *
 *  Rev 1.0   July 05,2019           Mohan Yadav           Changes for GooglePay new requirement.
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.tender.creditdebit;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXPineLabCreditGPayNumberSite extends PosSiteActionAdapter {
	// ----------------------------------------------------------------------
	/**
	 * serialVersionUID long
	 **/
	// ----------------------------------------------------------------------
	private static final long serialVersionUID = -6194581546532639067L;

	// ----------------------------------------------------------------------
	/**
	 * Displays the ENTER_MALL_CERT_CODE screen.
	 * <P>
	 * 
	 * @param bus
	 *            Service Bus
	 */
	// ----------------------------------------------------------------------
	public void arrive(BusIfc bus) {	
		POSUIManagerIfc ui =(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		// POSBaseBeanModel model = (POSBaseBeanModel)ui.getModel(MAXPOSUIManagerIfc.ENTER_GPAY_NUMBER);
		ui.showScreen(MAXPOSUIManagerIfc.ENTER_GPAY_NUMBER);
						
    }
}
