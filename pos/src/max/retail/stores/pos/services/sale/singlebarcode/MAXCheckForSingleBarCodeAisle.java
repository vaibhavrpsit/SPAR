package max.retail.stores.pos.services.sale.singlebarcode;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXCheckForSingleBarCodeAisle extends PosLaneActionAdapter {

	public void traverse(BusIfc bus)
	{
		String startMask = Gateway.getProperty("application", "startMask","");
		String endMask = Gateway.getProperty("application", "endMask","");
		
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        PromptAndResponseModel parModel =
            ((POSBaseBeanModel) ui.getModel(POSUIManagerIfc.SELL_ITEM)).getPromptAndResponseModel();
        String itemID = parModel.getResponseText();
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
		if(itemID!=null && startMask!=null && endMask!=null && isSingleBarCode(itemID, startMask, endMask))
		{
			
			bus.mail("SingleBarCode");
		}
		else
			bus.mail("Next1");
		
		
	}
	protected boolean isSingleBarCode(String itemId, String startMask, String endMask)
	{
		if(itemId.length()>(startMask.length()+endMask.length()))
		{
			if(startMask.equals(itemId.substring(0,startMask.length())) && 
					endMask.equals(itemId.substring(itemId.length()-endMask.length(), itemId.length())))
				return true;
			else
				return false;
		}
		return false;
	}
	
}
