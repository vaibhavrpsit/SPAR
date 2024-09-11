package max.retail.stores.pos.services.tender.upi;

import max.retail.stores.pos.services.tender.loyaltypoints.MAXDialogScreensIfc;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXScanForUpiSite extends PosSiteActionAdapter
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public void arrive(BusIfc bus)
	{
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		//MSRModel msr = (MSRModel) cargo.getTenderAttributes().get(TenderConstants.MSR_MODEL);
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		RetailTransactionADOIfc txnADO = (RetailTransactionADOIfc) cargo.getCurrentTransactionADO();
		((ADO) txnADO)
		.toLegacy();
		DialogBeanModel model = new DialogBeanModel();
		model.setResourceID("SendUpiPayment");
		model.setType(MAXDialogScreensIfc.CONFIRMATION);
		// when loyal customer does not found the it throws "RequiredCustomer"
		// letter
		model.setButtonLetter(MAXDialogScreensIfc.BUTTON_YES, "UpiSuccess");

		/* Rev 1.2 Start from letter Success to RequireTICCustomer */
		model.setButtonLetter(MAXDialogScreensIfc.BUTTON_NO, "Failure");

		/* Rev 1.2 END */
		ui.showScreen(MAXPOSUIManagerIfc.DIALOG_TEMPLATE, model);
		
	

	}
     
}