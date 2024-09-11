/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
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
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;

public class MAXEnterForm60IdentificationNumberAisle extends PosLaneActionAdapter {

	
	private static final long serialVersionUID = 1L;

	public void traverse(BusIfc bus)
	    {        
	    	          
	        POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
	        JournalManagerIfc jmi = (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);
	      	MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
			//Changes done By Purushotham
			MAXSaleReturnTransaction maxSaleReturnTransaction = (MAXSaleReturnTransaction) cargo.getTransaction();
	        POSBaseBeanModel model = (POSBaseBeanModel)ui.getModel(MAXPOSUIManagerIfc.FORM60_IDENTIFICATION_NUMBER);
	        PromptAndResponseModel pAndRModel = model.getPromptAndResponseModel();
	        
	        String idnum=pAndRModel.getResponseText();
	        maxSaleReturnTransaction.setForm60IDNumber(idnum);
	        
	        try {
		        // Set the value in Journal 
		        if (jmi != null)
		        	
		        {
		        	   StringBuilder message = new StringBuilder(Util.EOL);
		        	   Object dataArgs[] = new Object[1];
		        	   dataArgs[0] = idnum;
		        	   message.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, "JournalEntry.UniqueIdentification", dataArgs));
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
