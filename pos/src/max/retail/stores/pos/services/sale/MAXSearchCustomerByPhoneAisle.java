/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 2012-2013 MAX, Inc.    All Rights Reserved.
 *
 * Rev. 1.0		Hitesh Dua		15 Dec, 2016		\
 * Fix Bug: transaction should not get started if we don't link customer and direct move to sell item screen.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 */

package max.retail.stores.pos.services.sale;

import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

public class MAXSearchCustomerByPhoneAisle extends PosLaneActionAdapter {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void traverse(BusIfc bus){

		MAXSaleCargoIfc cargo = (MAXSaleCargoIfc) bus.getCargo();
		String customerPhoneNo=null;
		
		if(cargo.getCustomerInfo()!=null && cargo.getCustomerInfo().getCustomerInfoType()==CustomerInfoIfc.CUSTOMER_INFO_TYPE_PHONE_NUMBER && cargo.getCustomerInfo().getPhoneNumber()!=null){
			// Changes starts for code merging(commenting below line)
			//customerPhoneNo=cargo.getCustomerInfo().getPhoneNumber().getAreaCode()+cargo.getCustomerInfo().getPhoneNumber().getPhoneNumber();
			//changes for rev 1.0
			customerPhoneNo=cargo.getCustomerInfo().getPhoneNumber().getPhoneNumber();
			// Changes ends for code merging
		}
		
		if(customerPhoneNo!=null && !customerPhoneNo.equalsIgnoreCase("") && customerPhoneNo.length()>=10){
			bus.mail(new Letter("CustomerSearchStation"), BusIfc.CURRENT);
			cargo.setTicCustomerPhoneNo(cargo.getCustomerInfo().getPhoneNumber());
			cargo.setTicCustomerPhoneNoFlag(true);
			
		}else{
			bus.mail(new Letter("DemographicShowSale"), BusIfc.CURRENT);
			cargo.setTicCustomerPhoneNo(null);
			cargo.setTicCustomerPhoneNoFlag(false);
			
		}
		
		
	}
	
}
