/* ===========================================================================
 *  Copyright (c) 2017 MAX Hypermarkets Pvt Ltd.    All Rights Reserved.  
 * ===========================================================================
 *
 * Rev 1.0  June 15,2021     Kumar Vaibhav  SBI reward points integration
 *
 * ===========================================================================
 */
package max.retail.stores.pos.services.tender.sbi;

import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * Create a cash tender and attempt to add it to the transaction. If validation
 * fails, either punt, or attempt override, depending on the problem.
 */
public class MAXSBIAmountEnterSite extends PosSiteActionAdapter {
	private static final long serialVersionUID = 4340745363476760442L;

	@Override
	public void arrive(BusIfc bus) {
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		POSBaseBeanModel model = new POSBaseBeanModel();
		PromptAndResponseModel prModel = new PromptAndResponseModel();
		String sbiAmount =((MAXTenderCargo)cargo).getTotalPointAmount();
		try {
		if(!sbiAmount.contains(".")) {
			sbiAmount = sbiAmount+"00";
		}else {
			String[] amt = sbiAmount.split("\\.");
			if(amt[1].length()==1) {
				sbiAmount = sbiAmount+"0";
			}
		}
		}catch (Exception e) {
			
		}
		prModel.setResponseText(sbiAmount);
		model.setPromptAndResponseModel(prModel);
		uiManager.showScreen(MAXPOSUIManagerIfc.REWARD_AMOUNT_SCREEN, model);
	}

}
