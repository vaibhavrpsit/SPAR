/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.dailyoperations.poscount.giftcert;

import java.util.ArrayList;
import java.util.List;

import max.retail.stores.pos.services.dailyoperations.poscount.MAXPosCountCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXGiftCertDenominationBeanModel;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXDisplayGiftCertOptionSite extends PosSiteActionAdapter {
	
	public void arrive(BusIfc bus)
	{
		MAXPosCountCargo cargo = (MAXPosCountCargo)bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		
		ParameterManagerIfc param = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
		String[] denomination = null;
		try
		{
			denomination = param.getStringValues("SupportedDenomination");
		}
		catch(ParameterException e)
		{
			logger.error(e);
		}
		List giftCertList = cargo.getGiftCertList();
		MAXGiftCertDenominationBeanModel beanModel = new MAXGiftCertDenominationBeanModel();
		if(giftCertList != null)
		{
			beanModel.setDenomination(giftCertList);
		}
		else
		{
			giftCertList = new ArrayList();
			beanModel.setDenomination(giftCertList);
		}
		NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();
		int i=0, key=2;
		for( i=0;i<denomination.length;i++)
		{
			if(key==9)
				key=2;
			navModel.addButton("Button", denomination[i], denomination[i], true, "F"+(key++));
		}
		navModel.addButton("UserDefined", "User Defined", "User Defined", true, "F"+(key));
		PromptAndResponseModel prModel = new PromptAndResponseModel();
		prModel.setArguments("");
		beanModel.setPromptAndResponseModel(prModel);
		beanModel.setLocalButtonBeanModel(navModel);
		ui.showScreen(MAXPOSUIManagerIfc.GIFT_CERT_DETAIL_SCREEN, beanModel);
	}
}
