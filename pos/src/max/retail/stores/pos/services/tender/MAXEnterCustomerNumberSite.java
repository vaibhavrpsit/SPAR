package max.retail.stores.pos.services.tender;

//import max.retail.stores.pos.services.printing.MAXPrintingCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXPromptAndResponseBean;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
//import oracle.retail.stores.domain.DomainGateway;
//import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
//import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
//import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
//import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
//import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXEnterCustomerNumberSite extends PosSiteActionAdapter {
	/**
	 *  
	 */
	private static final long serialVersionUID = -3214158365526724142L;

public void arrive(BusIfc bus){
POSUIManagerIfc ui = ( POSUIManagerIfc )bus.getManager( UIManagerIfc.TYPE );
MAXTenderCargo cargo = ( MAXTenderCargo )bus.getCargo();
POSBaseBeanModel model = (POSBaseBeanModel)ui.getModel(MAXPOSUIManagerIfc.CUSTOMER_MOBILE_NUMBER_SCREEN);
PromptAndResponseModel prModel= model.getPromptAndResponseModel();
CustomerInfoIfc customerInfo = cargo.getCustomerInfo();
int customerInfoType = customerInfo.getCustomerInfoType();
if(customerInfoType == CustomerInfoIfc.CUSTOMER_INFO_TYPE_PHONE_NUMBER)
{
PhoneIfc phone = DomainGateway.getFactory().getPhoneInstance();
String value = prModel.getResponseText();
phone.setPhoneNumber(value);
customerInfo.setPhoneNumber(phone);
}
ui.showScreen(MAXPOSUIManagerIfc.CUSTOMER_MOBILE_NUMBER_SCREEN,model);
}
}