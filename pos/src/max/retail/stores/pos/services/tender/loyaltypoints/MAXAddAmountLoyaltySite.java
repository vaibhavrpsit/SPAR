/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *	Rev 1.0     Jan 06, 2016		Ashish Yadav		Online Points Redemption FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.loyaltypoints;

import max.retail.stores.domain.customer.MAXCustomerIfc;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;

public class MAXAddAmountLoyaltySite extends PosSiteActionAdapter {
	/**
	 * @author mohd.arif
	 */
	private static final long serialVersionUID = 1L;

	public void arrive(BusIfc bus) {
		TenderCargo cargo=(TenderCargo) bus.getCargo();	
		MAXCustomerIfc customer=null;
		if(cargo.getCurrentTransactionADO().toLegacy() instanceof SaleReturnTransaction )
		{
			SaleReturnTransaction transaction= ((SaleReturnTransaction) cargo.getCurrentTransactionADO().toLegacy());
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
		customer.setOtpValidation(true);
		
	bus.mail(new Letter("OverideSuccess"), BusIfc.CURRENT);
	}
	
}
