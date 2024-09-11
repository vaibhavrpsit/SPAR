
/********************************************************************************
 *
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 *	
 * 	Rev 1.0  02 Jan, 2016		Ashish yadav		Changes for Item inquiry
 *
 ********************************************************************************/

package max.retail.stores.pos.services.inquiry.iteminquiry;

import max.retail.stores.domain.factory.MAXDomainObjectFactoryIfc;
import max.retail.stores.pos.ui.beans.MAXItemInfoBeanModel;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManager;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

public class MAXAnotherItemNumberEnteredRoad extends PosLaneActionAdapter {

	/**
	 *
	 */
	private static final long serialVersionUID = -5507869801357709877L;

	@Override
	public void traverse(BusIfc bus){
		POSUIManagerIfc ui=(POSUIManagerIfc)bus.getManager(POSUIManager.TYPE);
		MAXItemInfoBeanModel model=(MAXItemInfoBeanModel)ui.getModel("ITEM_DISPLAY");
		String itemNumber=model.getPromptAndResponseModel().getResponseText();
		//Change for Rev 1.0:Starts
		if(itemNumber!=null && itemNumber.length()>0){
			MAXDomainObjectFactoryIfc domainObjectFactory=(MAXDomainObjectFactoryIfc)DomainGateway.getFactory();
			SearchCriteriaIfc searchCriteria=(SearchCriteriaIfc)domainObjectFactory.getSearchCriteriaInstance();
			searchCriteria.setItemNumber(itemNumber);
			MAXItemInquiryCargo cargo= (MAXItemInquiryCargo)bus.getCargo();
			cargo.setInquiry(searchCriteria);
		}
		//Change for Rev 1.0:Ends
		else{
			MAXDomainObjectFactoryIfc domainObjectFactory=(MAXDomainObjectFactoryIfc)DomainGateway.getFactory();
			SearchCriteriaIfc searchCriteria=(SearchCriteriaIfc)domainObjectFactory.getSearchCriteriaInstance();
			MAXItemInquiryCargo cargo= (MAXItemInquiryCargo)bus.getCargo();
			String selectedItemNumber=cargo.getPLUItem().getPosItemID();
			searchCriteria.setItemNumber(selectedItemNumber);
			cargo.setInquiry(searchCriteria);
		}
	}
}
