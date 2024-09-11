/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *	Rev 1.0     Jan 06, 2016		Ashish Yadav		Online Points Redemption FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.loyaltypoints;

import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.ui.timer.DefaultTimerModel;

public class MAXEnterOtpSite extends PosSiteActionAdapter{

	/**
	 * @author mohd.arif
	 * This site will use for enter otp code  with timeout Interval parametrer
	 *for FES MAX-FES-OTP Loyalty Point Redemption in POS v1.0.docx
	 */
	private static final long serialVersionUID = 8291261569953634094L;

	public void arrive(BusIfc bus) {
		int timeoutinterval=0;
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		
		TenderCargo cargo=(TenderCargo) bus.getCargo();	
		
		MAXCustomerIfc customer=null;
		if(cargo.getCurrentTransactionADO().toLegacy() instanceof SaleReturnTransaction )
		{
			SaleReturnTransaction transaction = ((SaleReturnTransaction) cargo.getCurrentTransactionADO().toLegacy());
			customer=(MAXCustomerIfc) transaction.getCustomer();
		}
		else if( cargo.getCurrentTransactionADO().toLegacy() instanceof LayawayPaymentTransaction)
		{
			LayawayPaymentTransaction transaction=((LayawayPaymentTransaction)cargo.getCurrentTransactionADO().toLegacy());
			customer=(MAXCustomerIfc) transaction.getCustomer();	
		}
		else{
			SaleReturnTransaction transaction= ((SaleReturnTransaction) cargo.getCurrentTransactionADO().toLegacy());
			customer=(MAXCustomerIfc) transaction.getCustomer();
		}
		timeoutinterval=customer.getLoyaltyTimeout();
		LineItemsModel beanModel = new LineItemsModel();
		DefaultTimerModel timeModel = new DefaultTimerModel(bus, false);
		// Changes starts for Rev 1.0 (Ashish : Loyalty OTP)
		timeModel.setActionName("Timeout");
		// Changes ends for Rev 1.0 (Ashish : Loyalty OTP)
		String timeOut = Gateway.getProperty("application", "LoyaltytimeOutInMilliSeconds", null);
		timeModel.setTimerInterval(Integer.parseInt(timeOut));
		// Changes starts for Rev 1.0 (Ashish : Loyalty OTP)
		//timeModel.setSetUIModel(true);
		// Changes starts for Rev 1.0 (Ashish : Loyalty OTP)
		if(timeoutinterval==0){
			timeModel.setActionName(CommonLetterIfc.DONE);
		}
		else if(timeoutinterval==1){
			timeModel.setActionName(CommonLetterIfc.INVALID);
		}
		beanModel.setTimerModel(timeModel);
		ui.showScreen(MAXPOSUIManagerIfc.ENTER_OTP, beanModel);
	}
}
