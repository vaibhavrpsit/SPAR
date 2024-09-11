/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 *  Copyright (c) 2010 Lifestyle India Pvt Ltd.    All Rights Reserved. 
 *
 *  Rev 1.0   July 05,2019           Mohan Yadav           Changes for GooglePay new requirement.
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.tender.creditdebit;

import java.util.List;

import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXPineLabCreditGPayNumberConfSite extends PosSiteActionAdapter{

	private static final long serialVersionUID = -4354045474935486867L;

	@Override
	public void arrive(BusIfc bus) {
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		String mobile=null;

		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		POSBaseBeanModel model = (POSBaseBeanModel)ui.getModel(MAXPOSUIManagerIfc.ENTER_GPAY_NUMBER);
		PromptAndResponseModel prModel= model.getPromptAndResponseModel();
		if(cargo.getCustomer() instanceof MAXCustomerIfc && cargo.getCustomer() !=null){
			MAXCustomerIfc customer= (MAXCustomerIfc) cargo.getCustomer();
			String phoneList = customer.getLoyaltyCustomerPhone() != null ? customer.getLoyaltyCustomerPhone().getPhoneNumber() : null;
			if (phoneList != null && !phoneList.isEmpty()) {
				mobile = customer.getLoyaltyCustomerPhone().getPhoneNumber();
			}
			if(mobile == null || mobile.equals("")) {
				List<PhoneIfc> phoneLst = customer.getContact() != null ? customer.getContact().getPhoneList() : null;
				if (phoneLst != null && !phoneLst.isEmpty()) {
					mobile = phoneLst.get(0).getPhoneNumber();
				}
			}
		}
		
		if(mobile !=null && !mobile.equals("")) {
			if(prModel ==null) {
				PromptAndResponseModel prompt = new PromptAndResponseModel();
				model.setPromptAndResponseModel(prompt);
				model.getPromptAndResponseModel().setResponseText(mobile);
				 NavigationButtonBeanModel nModel = new NavigationButtonBeanModel();
				 nModel.setButtonEnabled(CommonActionsIfc.NEXT, true);
				 model.setGlobalButtonBeanModel(nModel);
			}else {
				prModel.setResponseText(mobile);
				 NavigationButtonBeanModel nModel = new NavigationButtonBeanModel();
				 nModel.setButtonEnabled(CommonActionsIfc.NEXT, true);
				 model.setGlobalButtonBeanModel(nModel);
			}
		
		DialogBeanModel dialogModel = new DialogBeanModel();
		 String[] args = new String[1];
         args[0] = mobile;
		dialogModel.setResourceID("GPayNumberConfirmation");
		dialogModel.setType(DialogScreensIfc.CONFIRMATION);
		dialogModel.setArgs(args);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, CommonLetterIfc.YES);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, CommonLetterIfc.NO);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		}else {
			if(model !=null) {
				/* prModel.setPromptAndResponseModel(null); */
				PromptAndResponseModel prompt = new PromptAndResponseModel();
				model.setPromptAndResponseModel(prompt);
				 NavigationButtonBeanModel nModel = new NavigationButtonBeanModel();
				 nModel.setButtonEnabled(CommonActionsIfc.NEXT, false);
				model.setGlobalButtonBeanModel(nModel);
			}
			bus.mail("No", BusIfc.CURRENT);
		}
	}


}

