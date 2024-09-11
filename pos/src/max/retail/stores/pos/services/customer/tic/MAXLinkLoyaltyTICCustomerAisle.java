package max.retail.stores.pos.services.customer.tic;

import max.retail.stores.domain.customer.MAXTICCustomerIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.pos.services.customer.main.MAXCustomerMainCargo;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;


public class MAXLinkLoyaltyTICCustomerAisle extends PosLaneActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void traverse(BusIfc bus) {
		// retrieve cargo
		MAXCustomerMainCargo cargo = (MAXCustomerMainCargo) bus.getCargo();
		
		// save current letter
		LetterIfc currentLetter = new Letter("Undo");
		// if a customer was not link reset remove the customer instance.
		
			// Use customer locale preferrences for the
			// pole display and receipt subsystems
		     MAXTICCustomerIfc customerIfc = (MAXTICCustomerIfc) cargo.getTICCustomer();
		    
		     
		     if(cargo.getTransaction()!=null){
		     MAXSaleReturnTransactionIfc transactionIfc=(MAXSaleReturnTransactionIfc)cargo.getTransaction();
		    // transactionIfc.setLSSIPLTICCustomer(cargo.getTICCustomer());
		    
		     if(transactionIfc!=null && (transactionIfc.getCustomer()==null || transactionIfc.getCustomer().getFirstLastName()==null || 
		    		 transactionIfc.getCustomer().getFirstLastName().trim().equalsIgnoreCase(""))){
		    	 transactionIfc.setTicCustomerVisibleFlag(true);
		     }
		     cargo.setTransaction(transactionIfc);
		     //cargo.setCustomer(transactionIfc.getCustomer());
		     //Changes done for Loyalty error @Puru
		     cargo.setCustomer(customerIfc);
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

			MAXCustomerMainCargo cargo = (MAXCustomerMainCargo) bus.getCargo();
			MAXTICCustomerIfc customer = (MAXTICCustomerIfc) cargo.getTICCustomer();

			// build bean model helper
//			MAXBalancePointTDOUIIfc tdo = null;
//			try {
//				tdo = (MAXBalancePointTDOUIIfc) TDOFactory.create("tdo.customer.MAXBalancePointDisplayTDO");
//			} catch (TDOException tdoe) {
//				tdoe.printStackTrace();
//			}

			pda.displayTextAt(0, 0, customer.getTICCustomerID());
		} catch (DeviceException e) {

			//logger.warn("Error while using balance point  display for customer inteface: " + e.getMessage() + "");
		}

	}
}
