/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2018 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.0 	Aug 21, 2018		Bhanu Priya		Changes for Capture PAN CARD CR
 *
 ********************************************************************************/
package max.retail.stores.pos.services.tender.pancard;

import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;

public class MAXCapturePassportDetailsAisle extends PosLaneActionAdapter {

	private static final long serialVersionUID = 1L;

	public void traverse(BusIfc bus) {

		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		  JournalManagerIfc jmi = (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();

		MAXSaleReturnTransaction transaction = (MAXSaleReturnTransaction) cargo
				.getTransaction();

		DataInputBeanModel model = (DataInputBeanModel) ui
				.getModel(MAXPOSUIManagerIfc.CAPTURE_PASSPORT_DETAILS);
		String passportNum = (String) model
				.getValueAsString("PassportNumberField");
		String visaNum = (String) model.getValueAsString("VisaNumberField");
		String ackNumber = (String) model.getValueAsString("ITRAckField");

		transaction.setPassportNumber(passportNum);
		transaction.setVisaNumber(visaNum);
		transaction.setITRAckNumber(ackNumber);
		 try {
		        // Set the value in Journal 
		        if (jmi != null)
		        	
		        {
		        	
		        	   StringBuilder message =	 new StringBuilder(Util.EOL);
		        	   Object dataArgs[] = new Object[1];
		        	   dataArgs[0] = passportNum;
		        	   message.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, "JournalEntry.PassportNo", dataArgs));
		        	   message.append(Util.EOL);
		        	   jmi.journal(cargo.getTransaction().getCashier().getLoginID(), cargo.getTransaction().getTransactionID(), message.toString());
		        }
		        }
		        catch (NullPointerException e)
		        {
		        	e.printStackTrace();
		        }
		        // ENd
		        
		
		bus.mail("Continue");
	}
}
