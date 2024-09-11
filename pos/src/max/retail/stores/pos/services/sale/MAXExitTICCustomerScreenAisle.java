

/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 

 *  Rev 1.0     11/03/2015      Akhilesh kumar          		Loyalty Customer
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/


package max.retail.stores.pos.services.sale;

import max.retail.stores.domain.customer.MAXTICCustomerIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

public class MAXExitTICCustomerScreenAisle extends PosLaneActionAdapter {

	private static final long serialVersionUID = 1132529737045210034L;


	public void traverse(BusIfc bus)
	{
		MAXSaleCargo cargo = (MAXSaleCargo) bus.getCargo();

		MAXTICCustomerIfc customerIfc = (MAXTICCustomerIfc) cargo.getTicCustomer();


		if(cargo.getTransaction()!=null && cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc ){
			MAXSaleReturnTransactionIfc transactionIfc=(MAXSaleReturnTransactionIfc)cargo.getTransaction();
			if(customerIfc!=null){
				customerIfc.setTICFirstName("");
				customerIfc.setTICCustomerID("");
				customerIfc.setTICbirthdate("");
				customerIfc.setTICEmail("");
				customerIfc.setTICLastName("");
				customerIfc.setTICGender("");
				customerIfc.setTICMobileNumber("");
				customerIfc.setTICPinNumber("");
			}

			transactionIfc.setMAXTICCustomer(customerIfc);
			transactionIfc.setTicCustomerVisibleFlag(false);
			cargo.setTransaction(transactionIfc);  
		}
		else if(cargo.getTicCustomer() != null && cargo.getTicCustomer().getExistingCustomer() != null && 
				cargo.getTicCustomer().getExistingCustomer().booleanValue()){
			cargo.setTicCustomer(null);
		}
		

        bus.mail(new Letter("Success"),BusIfc.CURRENT);
		
	}

}
