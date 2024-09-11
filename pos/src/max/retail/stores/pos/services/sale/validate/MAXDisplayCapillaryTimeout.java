package max.retail.stores.pos.services.sale.validate;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXDisplayCapillaryTimeout  extends PosSiteActionAdapter
{
	  private static final long serialVersionUID = 1L;

	  public void arrive(BusIfc bus)
	  {
	    POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager("UIManager");
	    DialogBeanModel beanModel = new DialogBeanModel();
	    String[] errorMsg = new String[1];

	   // beanModel.setResourceID("TimeoutMessageScreen");
	   // beanModel.setType(1);
	   // beanModel.setButtonLetter(9, "Ok");	   
	    
	    beanModel.setResourceID("CRMCapillaryIsRedeemRequestError");
	    beanModel.setType(1);
	    beanModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Offline");
	    errorMsg[0] = "Network is unavailable. Check if internet connectivity is there.";
	    beanModel.setArgs(errorMsg);	
	    ui.showScreen("DIALOG_TEMPLATE", beanModel);
	  }
	}