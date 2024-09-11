package max.retail.stores.pos.services.customer.tic;

import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.customer.MAXTICCustomerIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.pos.services.customer.main.MAXCustomerMainCargo;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

public class MAXExitTICCustomerScreenAisle extends PosLaneActionAdapter {

	private static final long serialVersionUID = 1132529737045210034L;


	public void traverse(BusIfc bus)
	{
		MAXCustomerMainCargo cargo = (MAXCustomerMainCargo) bus.getCargo();

		MAXTICCustomerIfc customerIfc = (MAXTICCustomerIfc) cargo.getTICCustomer();

		if(cargo.getTransaction()!=null && cargo.getTransaction() instanceof SaleReturnTransactionIfc ){
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
				customerIfc.setCustomerType("");
			}

			if(transactionIfc.getCustomer()!=null && transactionIfc.getCustomer() instanceof MAXCustomer){
				MAXCustomer maxCustomer=(MAXCustomer)transactionIfc.getCustomer();
				maxCustomer.setMAXTICCustomer(customerIfc);
				maxCustomer.setTicCustomerVisibleFlag(false);
				transactionIfc.setCustomer(maxCustomer);

			}

			transactionIfc.setMAXTICCustomer(customerIfc);
			transactionIfc.setTicCustomerVisibleFlag(false);
			cargo.setTransaction(transactionIfc);  

		}
		else if(cargo.getTICCustomer()!=null && cargo.getTICCustomer().getExistingCustomer()!=null && cargo.getTICCustomer().getExistingCustomer().booleanValue()){
			cargo.setTICCustomer(null);
		}

		bus.mail(new Letter("Undo"),BusIfc.CURRENT);

	}

}
