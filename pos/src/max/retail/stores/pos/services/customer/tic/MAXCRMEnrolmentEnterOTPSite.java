/* ===========================================================================
 *  Copyright (c) 2019 Lifestyle India Pvt Ltd.    All Rights Reserved. 
 * ===========================================================================
 *
 * Rev 1.0  5th May 2019	Karni Singh  POS REQ: Register CRM customer with OTP
 * Initial revision.
 *
 * ===========================================================================
 */
package max.retail.stores.pos.services.customer.tic;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXCRMEnrolmentEnterOTPSite extends PosSiteActionAdapter{

	private static final long serialVersionUID = 1380316288509276853L;

	public void arrive(BusIfc bus)
	{
		POSUIManagerIfc uiManager=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		
		POSBaseBeanModel model = new POSBaseBeanModel();
		PromptAndResponseModel parModel = new PromptAndResponseModel();				
		parModel.setResponseText("");
		model.setPromptAndResponseModel(parModel); 				
		uiManager.showScreen(MAXPOSUIManagerIfc.CRM_ENROLMENT_OTP_SCREEN,model);
	}
}
