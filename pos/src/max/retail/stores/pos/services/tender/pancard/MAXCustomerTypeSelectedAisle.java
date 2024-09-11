/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev	1.0 	Aug 21, 2018		Bhanu Priya		Changes for Capture PAN CARD CR
 *
 ********************************************************************************/
package max.retail.stores.pos.services.tender.pancard;

import java.util.Vector;

import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXSelectCustomerTypeBeanModel;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXCustomerTypeSelectedAisle extends PosLaneActionAdapter

{
	private static final long serialVersionUID = 4956788268919304222L;
	
	public static final int FIELD_COUNT = 4;
	/*public static final String RESIDENT_CUSTOMER_WITH_PAN ="Resident Customer with PAN Card";
	public static final String RESIDENT_CUSTOMER_WITHOUT_PAN ="Resident Customer without PAN Card";
	public static final String NON_RESIDENT_CUSTOMER ="Non Resident Customer";*/
	public void traverse(BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);

		MAXSelectCustomerTypeBeanModel beanModel = (MAXSelectCustomerTypeBeanModel) ui
				.getModel(MAXPOSUIManagerIfc.CUSTOMER_TYPE_CAPTURE);
		Vector<String> typeCustomer = new Vector<String>();

		LetterIfc letter = null;
		String selectedCustomerType = beanModel.getSelectedReason().trim();

		if (!(selectedCustomerType.isEmpty())) {
						
			for (int i = 0; i < 4; i++) {
				typeCustomer = beanModel.getCustomerType();

				if (typeCustomer.get(i).equalsIgnoreCase(selectedCustomerType)) {
					if (i == 1) {
						letter = new Letter("CustWithPAN");
						break;
					} else if (i == 2) {
						letter = new Letter("CustWithoutPAN");
						break;
					} else if (i == 3) {
						letter = new Letter("NonResident");
						break;
					}
					else  {
						DialogBeanModel dModel = new DialogBeanModel();
						dModel.setType(DialogScreensIfc.ERROR);
						dModel.setResourceID("InvalidSelection");
						dModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
								"NoOptionSelected");
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dModel);
						//break;
						return;
					}
					
				}
				
				
			}
			
			
			bus.mail(letter, BusIfc.CURRENT);
		}


		
		
		
	}
}

