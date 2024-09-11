package max.retail.stores.pos.services.tender.creditdebit;

import java.util.List;

import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXPineLabCreditGPayConfirmationSite extends PosSiteActionAdapter
{
	private static final long serialVersionUID = -4354045474935486867L;

	public void arrive(BusIfc bus)
	{
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		String mobile=null;

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
		if(bus.getCurrentLetter().getName().equals(CommonLetterIfc.UNDO) && mobile !=null && !mobile.equals("")) {
			bus.mail(CommonLetterIfc.YES, BusIfc.CURRENT);
		}else {
			POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager("UIManager");
			DialogBeanModel dialogModel = new DialogBeanModel();
			dialogModel.setResourceID("GPayConfirmation");
			dialogModel.setType(0);
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, CommonLetterIfc.YES);
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, CommonLetterIfc.NO);
			ui.showScreen("DIALOG_TEMPLATE", dialogModel);
		}
	}
}