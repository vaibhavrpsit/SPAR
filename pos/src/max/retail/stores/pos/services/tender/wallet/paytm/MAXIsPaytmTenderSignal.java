package max.retail.stores.pos.services.tender.wallet.paytm;

import max.retail.stores.domain.tender.MAXTenderChargeIfc;
import max.retail.stores.domain.tender.MAXTenderMobikwik;
import max.retail.stores.domain.tender.MAXTenderPaytm;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.TenderBeanModel;

public class MAXIsPaytmTenderSignal  implements TrafficLightIfc
{

	private static final long serialVersionUID = -3907319525867521091L;

	public boolean roadClear(BusIfc bus)
    {
    	POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        TenderBeanModel model = (TenderBeanModel)ui.getModel();
        
        TenderLineItemIfc tenderToRemove = model.getTenderToDelete();
        if (tenderToRemove instanceof MAXTenderPaytm || tenderToRemove instanceof  MAXTenderMobikwik)
        {
        	/*MAXTenderChargeIfc tenderCharge = (MAXTenderChargeIfc)tenderToRemove;
        	if(tenderCharge.getCardType() != null && 
        			tenderCharge.getCardType().equalsIgnoreCase("PAYTM"))*/
        		return true;
        }
        return false;
        //return true;
    }


}