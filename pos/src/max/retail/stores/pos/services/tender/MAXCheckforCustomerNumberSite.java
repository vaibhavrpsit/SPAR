//changes by shyvanshu mehra


package max.retail.stores.pos.services.tender;

import max.retail.stores.pos.ui.beans.MAXDialogBeanModel;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

public class MAXCheckforCustomerNumberSite extends PosSiteActionAdapter {
	private static final long serialVersionUID = -3652750985308412657L;


public void arrive(BusIfc bus)
{
MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
System.out.println("MAXCheckforCustomerNumberSite===== "+cargo.getTransaction().getCustomerInfo().getPhoneNumber().getPhoneNumber());
if(cargo.getTransaction().getCustomerInfo().getPhoneNumber().getPhoneNumber().equalsIgnoreCase(""))
{ 
System.out.println("dfggfdghfg Shyvanshu ");
MAXDialogBeanModel dialogModel = new MAXDialogBeanModel();
dialogModel.setResourceID("Customer_Number_Used");
dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
}
else
{bus.mail("Yes");}
}
}
