/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  3/4/2013               Izhar                                       MAX-POS-Customer-FES_v1.2.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.customer.lookup;

import max.retail.stores.domain.customer.MAXCustomerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;

public class MAXCustomerTypeSite extends PosSiteActionAdapter {
//  MAX Rev 1.0 Change : Start  
	/*public void arrive(BusIfc bus)
	    {
		 POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		 uiManager.showScreen("CUSTOMER_TYPE");
	    }*/
	//public void depart(BusIfc bus){
	public void arrive(BusIfc bus){
		CustomerCargo cargo = (CustomerCargo)bus.getCargo();
		
		((MAXCustomerIfc) cargo.getCustomer()).setCustomerType("L");
		bus.mail("POSCustomer");
		/*//<!-- MAX Rev 1.0 Change : Start --> 
        if(bus.getCurrentLetter().getName().equalsIgnoreCase("POSCustomer"))
        	((MAXCustomerIfc) cargo.getCustomer()).setCustomerType("L");
        else if(bus.getCurrentLetter().getName().equalsIgnoreCase("TICCustomer"))
        	((MAXCustomerIfc) cargo.getCustomer()).setCustomerType("T");
      //<!-- MAX Rev 1.0 Change : end --> 
*/	 /*
		 POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		 PhoneIfc phone = null;
		CustomerCargo cc = (CustomerCargo)bus.getCargo();
		Vector phones =cc.getCustomer().getPhones();
		phone = (PhoneIfc) phones.elementAt(0);
		//phone.getPhoneNumber();
		if(cc.getCustomer().getFirstName().equalsIgnoreCase("") && cc.getCustomer().getLastName().equalsIgnoreCase("") && 
				phone.getPhoneNumber().equalsIgnoreCase("") || 
				!cc.getCustomer().getFirstName().equalsIgnoreCase("") && cc.getCustomer().getLastName().equalsIgnoreCase("") &&  
				phone.getPhoneNumber().equalsIgnoreCase("") || cc.getCustomer().getFirstName().equalsIgnoreCase("") && 
				!cc.getCustomer().getLastName().equalsIgnoreCase("") &&  phone.getPhoneNumber().equalsIgnoreCase(""))
		{
			//POSUIManagerIfc ui=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

	        UtilityManagerIfc utility =
	          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

	        String argSeed = utility.retrieveDialogText("NoLinkedCustomer.Send",
	                                                    //"Send");
	        String args[] =
	        {
	            argSeed,
	            argSeed,
	            argSeed
	        };
	        // Using "generic dialog bean". display the error dialog
	        DialogBeanModel model = new DialogBeanModel();

	        // Set model to same name as dialog
	        // Set button and arguments
	        model.setResourceID("EnterCustSearchDetails");
	        model.setType(DialogScreensIfc.ERROR);
	        //model.setArgs(args);
	        model.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, CommonLetterIfc.RETRY);
	        //model.setButtonLetter(DialogScreensIfc.BUTTON_YES, CommonLetterIfc.LINK);
	        // set and display the model
	        uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
		}
		else if(!cc.getCustomer().getFirstName().equalsIgnoreCase("") && cc.getCustomer().getLastName().equalsIgnoreCase("") && !phone.getPhoneNumber().equalsIgnoreCase("") || cc.getCustomer().getFirstName().equalsIgnoreCase("") && !cc.getCustomer().getLastName().equalsIgnoreCase("") && !phone.getPhoneNumber().equalsIgnoreCase(""))
		{
			
			//POSUIManagerIfc ui=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

	        UtilityManagerIfc utility =
	          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

	        String argSeed = utility.retrieveDialogText("NoLinkedCustomer.Send",
	                                                    //"Send");
	        String args[] =
	        {
	            argSeed,
	            argSeed,
	            argSeed
	        };
	        // Using "generic dialog bean". display the error dialog
	        DialogBeanModel model = new DialogBeanModel();

	        // Set model to same name as dialog
	        // Set button and arguments
	        model.setResourceID("EnterCustSearchDetails");
	        model.setType(DialogScreensIfc.ERROR);
	        //model.setArgs(args);
	        model.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, CommonLetterIfc.RETRY);
	        //model.setButtonLetter(DialogScreensIfc.BUTTON_YES, CommonLetterIfc.LINK);
	        // set and display the model
	        uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
		}
		else if(!cc.getCustomer().getFirstName().equalsIgnoreCase("") && !cc.getCustomer().getLastName().equalsIgnoreCase("") && phone.getPhoneNumber().equalsIgnoreCase(""))
		     uiManager.showScreen("CUSTOMER_TYPE");
		else if(cc.getCustomer().getFirstName().equalsIgnoreCase("") && cc.getCustomer().getLastName().equalsIgnoreCase("") && !phone.getPhoneNumber().equalsIgnoreCase(""))
     uiManager.showScreen("CUSTOMER_TYPE");
     
		else if (!cc.getCustomer().getFirstName().equalsIgnoreCase("") && !cc.getCustomer().getLastName().equalsIgnoreCase("") && !phone.getPhoneNumber().equalsIgnoreCase(""))
			 uiManager.showScreen("CUSTOMER_TYPE");

	    }
//  MAX Rev 1.0 Change : end
	public void depart(BusIfc bus){
		CustomerCargo cargo = (CustomerCargo)bus.getCargo();
		//<!-- MAX Rev 1.0 Change : Start --> 
        if(bus.getCurrentLetter().getName().equalsIgnoreCase("POSCustomer"))
        	((MAXCustomerIfc) cargo.getCustomer()).setCustomerType("L");
        else
        	((MAXCustomerIfc) cargo.getCustomer()).setCustomerType("T");
      //<!-- MAX Rev 1.0 Change : end --> 
		
		
	}*/
	 }
}
