package max.retail.stores.pos.services.pricing.employeediscount;

import org.codehaus.jettison.json.JSONObject;

import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.transaction.MAXLayawayPaymentTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.services.pricing.MAXPricingCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.timer.DefaultTimerModel;

public class MAXEnterOtpSite extends PosSiteActionAdapter{

	/**
	 * @author kajal nautiyal Employee Discount validation through OTP
	 */
	private static final long serialVersionUID = 8291261569953634094L;
	
	
	public void arrive(BusIfc bus) {
	//	int timeoutinterval=0;
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		
		
		MAXPricingCargo cargo = (MAXPricingCargo) bus.getCargo();
	
		LineItemsModel beanModel = new LineItemsModel();
		DefaultTimerModel timeModel = new DefaultTimerModel(bus, false);
//		timeModel.setActionName(DefaultTimerModel.TRANS_LETTER);
		timeModel.setActionName("Timeout");
	//	String timeOut = Gateway.getProperty("application", "LoyaltytimeOutInMilliSeconds", null);
		//timeModel.setTimerInterval(Integer.parseInt(timeOut));
	
	//	beanModel.setTimerModel(timeModel);
		ui.showScreen(MAXPOSUIManagerIfc.ENTER_OTP, beanModel);
			}

}
