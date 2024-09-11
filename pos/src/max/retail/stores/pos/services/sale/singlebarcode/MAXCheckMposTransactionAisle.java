package max.retail.stores.pos.services.sale.singlebarcode;

import java.util.Vector;

import max.retail.stores.domain.singlebarcode.SingleBarCodeData;
import max.retail.stores.pos.services.sale.MAXSaleCargoIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXCheckMposTransactionAisle extends LaneActionAdapter implements LaneActionIfc {

	private static final String ERROR_TOKEN_ERROR = "ErrorToken";

	public void traverse(BusIfc bus)
	{
		MAXSaleCargoIfc saleCargo = (MAXSaleCargoIfc)bus.getCargo();
		Vector result = saleCargo.getSingleBarCodeVector();
		int lineItem = saleCargo.getSingleBarCodeLineItem();
		SingleBarCodeData data = null; 
		try
		{
			if(lineItem>result.size()-1 || result == null)
			{
				data = saleCargo.getSingleBarCodeData();
				saleCargo.setSingleBarCodeData(null);
				saleCargo.setSingleBarCodeVector(null);
				bus.mail("Next");
			}
			else
				bus.mail("Continue");
		}
		catch(NullPointerException ex)
		{
			bus.mail("Next");
		}
	}
	protected void showInvalidTokenDialog(UtilityManagerIfc utility, POSUIManagerIfc ui)
    {
      DialogBeanModel dialogModel = new DialogBeanModel();
      dialogModel.setResourceID(ERROR_TOKEN_ERROR);
      dialogModel.setType(DialogScreensIfc.ERROR);
      ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
}
