 /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.dailyoperations.poscount.giftcert;

import java.util.ArrayList;
import java.util.List;

import max.retail.stores.domain.tender.MAXDenominationCount;
import max.retail.stores.pos.services.dailyoperations.poscount.MAXPosCountCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXUserDefineDenominationBeanModel;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

public class MAXGCUserDefinedDenominationSite extends PosSiteActionAdapter {

	public void arrive(BusIfc bus)
	{
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		MAXUserDefineDenominationBeanModel model = new MAXUserDefineDenominationBeanModel();
		ui.showScreen(MAXPOSUIManagerIfc.USER_DEFINED_DENOMINATION, model);
	}
	public void depart(BusIfc bus)
	{
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		MAXPosCountCargo cargo = (MAXPosCountCargo)bus.getCargo();
		
		MAXUserDefineDenominationBeanModel model = (MAXUserDefineDenominationBeanModel)ui.getModel(MAXPOSUIManagerIfc.USER_DEFINED_DENOMINATION);
		CurrencyIfc denomination = model.getCurrency();
		int quantity = model.getQuantity();
		MAXDenominationCount count = null;
		List gclist = cargo.getGiftCertList();
		if(gclist == null)
		{
			if(!denomination.getStringValue().equalsIgnoreCase(DomainGateway.getBaseCurrencyInstance().getStringValue()) && quantity != 0)
			{
				gclist = new ArrayList();
				count = new MAXDenominationCount();
				count.setCurrency(denomination);
				count.setQuantity(quantity);
				gclist.add(count);
			}
		}
		else
		{
			int index =-1;
			for(int i=0;i< gclist.size();i++)
			{
				count = (MAXDenominationCount)gclist.get(i);
				if(count.getCurrency().getStringValue().equalsIgnoreCase(denomination.getStringValue()))
				{	
					index =i;
					break;
				}
			}
			if(index == -1)
			{
				count = new MAXDenominationCount();
				count.setCurrency(denomination);
				count.setQuantity(quantity);
				gclist.add(count);

			}
			else
			{
				count = (MAXDenominationCount)gclist.get(index);
				count.setCurrency(denomination);
				count.setQuantity(quantity);
				gclist.set(index, count);

			}
		}
		cargo.setGiftCertList(gclist);
	}
}
