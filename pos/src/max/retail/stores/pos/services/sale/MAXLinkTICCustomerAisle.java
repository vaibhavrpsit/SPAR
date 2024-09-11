

/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 

 *  Rev 1.0     11/03/2015      Akhilesh kumar          		Loyalty Customer
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.sale;

import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.customer.MAXTICCustomerIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;


public class MAXLinkTICCustomerAisle extends PosLaneActionAdapter {

	public void traverse(BusIfc bus) {
		// retrieve cargo
		
		//System.out.println("in the traverse");
		MAXSaleCargo cargo = (MAXSaleCargo) bus.getCargo();
		
		// save current letter
		LetterIfc currentLetter = new Letter("Success");
		// if a customer was not link reset remove the customer instance.
		
			// Use customer locale preferrences for the
			// pole display and receipt subsystems
		     MAXTICCustomerIfc customerIfc = (MAXTICCustomerIfc) cargo.getTicCustomer();
		    
		     
		     if(cargo.getTransaction()!=null && cargo.getTransaction() instanceof MAXSaleReturnTransaction){
		    	 MAXSaleReturnTransactionIfc transactionIfc=(MAXSaleReturnTransactionIfc)cargo.getTransaction();
		    	 transactionIfc.setMAXTICCustomer(cargo.getTicCustomer());

		    	 if(transactionIfc!=null && (transactionIfc.getCustomer()==null || transactionIfc.getCustomer().getFirstLastName()==null || transactionIfc.getCustomer().getFirstLastName().trim().equalsIgnoreCase(""))){
		    		 transactionIfc.setTicCustomerVisibleFlag(true);
		    	 }
		    	 cargo.setTransaction(transactionIfc);
		     }
		     else if(cargo.getTransaction()!=null && cargo.getTransaction().getCustomer()!=null && cargo.getTransaction().getCustomer() instanceof MAXCustomerIfc ){
		    	 
		    	 if(cargo.getTicCustomer()==null && ((MAXCustomerIfc)cargo.getTransaction().getCustomer()).getMAXTICCustomer()!=null){
		    		 cargo.setTicCustomer((MAXTICCustomerIfc)cargo.getTransaction().getCustomer());
		    	 }
		    		 
		     }
		     
			if (customerIfc != null && customerIfc.getTICCustomerID()!=null && !customerIfc.getTICCustomerID().trim().equalsIgnoreCase("")) {
				displayPoleDisplayInfo(bus);
			}

		bus.mail(currentLetter, BusIfc.CURRENT);

	}

	protected void displayPoleDisplayInfo(BusIfc bus) {
		try {
			POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
			pda.clearText();

			MAXSaleCargo cargo = (MAXSaleCargo) bus.getCargo();
			MAXTICCustomerIfc customer = (MAXTICCustomerIfc) cargo.getTicCustomer();

			// build bean model helper
	/*		MAXBalancePointTDOUIIfc tdo = null;
			try {
				tdo = (MAXBalancePointTDOUIIfc) TDOFactory.create("tdo.customer.MAXBalancePointDisplayTDO");
			} catch (TDOException tdoe) {
				// TODO Auto-generated catch block
				tdoe.printStackTrace();
			}*/

			pda.displayTextAt(0, 0, customer.getTICCustomerID());
		} catch (DeviceException e) {

			//logger.warn("Error while using balance point  display for customer inteface: " + e.getMessage() + "");
		}

	}
}
