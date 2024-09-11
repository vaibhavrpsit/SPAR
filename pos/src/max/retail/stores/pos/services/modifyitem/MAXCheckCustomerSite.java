package max.retail.stores.pos.services.modifyitem;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXCheckCustomerSite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -447966981057346306L;

	public void arrive(BusIfc bus) {

		MAXItemCargo cargo = (MAXItemCargo) bus.getCargo();
		CustomerIfc customer = cargo.getTransaction().getCustomer();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		if (customer != null) {
			bus.mail("Success");
			if (cargo.getTransaction() instanceof SaleReturnTransactionIfc)
				((SaleReturnTransactionIfc) cargo.getTransaction())
						.setCheckedCustomerPresent(true);
		} else {

			DialogBeanModel dialogModel = new DialogBeanModel();
			dialogModel.setResourceID("CustomerNotLinked");
			dialogModel.setArgs(null);
			dialogModel.setType(DialogScreensIfc.ERROR);

			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

		}

	}

}
