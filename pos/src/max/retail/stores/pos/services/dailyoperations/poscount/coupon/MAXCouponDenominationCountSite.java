/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.1	Prateek		25/June/2013	Changes done for BUG 6626.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.dailyoperations.poscount.coupon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import max.retail.stores.domain.tender.MAXCouponTypes;
import max.retail.stores.domain.tender.MAXDenominationCount;
import max.retail.stores.pos.services.dailyoperations.poscount.MAXPosCountCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXCouponDenominationCountSummaryBeanModel;
import max.retail.stores.pos.ui.beans.MAXCouponDenominationCounterBeanModel;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.manager.ui.jfc.ButtonPressedLetter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXCouponDenominationCountSite extends PosSiteActionAdapter 
{
	public void arrive(BusIfc bus)
	{
		MAXPosCountCargo cargo = (MAXPosCountCargo)bus.getCargo();
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		MAXCouponDenominationCounterBeanModel model = null;
		CurrencyIfc[] denominations = null;
		String currency[] = null;

		try {
			currency = pm.getStringValues("SupportedDenomination");
		} catch (ParameterException e) {
			e.printStackTrace();
		}
		if(currency!=null)
		{
			denominations= new CurrencyIfc[currency.length];
			for(int i=0;i<currency.length;i++)
			{
				denominations[i] = DomainGateway.getBaseCurrencyInstance((String)currency[i]);
			}
		}
		ArrayList countModelList = null;
		MAXCouponDenominationCountSummaryBeanModel countModel = new MAXCouponDenominationCountSummaryBeanModel();
		if(bus.getCurrentLetter() instanceof ButtonPressedLetter)
		{
			ButtonPressedLetter letterPressed = (ButtonPressedLetter) bus.getCurrentLetter();
			/**MAX Rev 1.1 Change : Start**/
			if(letterPressed.getName().equalsIgnoreCase("Button"))
			{
				if(letterPressed.getNumber()<=7)			
					cargo.setSelectedCoupon(cargo.getCouponTypes()[letterPressed.getNumber()]);
				else
					cargo.setSelectedCoupon(cargo.getCouponTypes()[letterPressed.getNumber()-1]);
			}
			/**MAX Rev 1.1 Change : End**/
		}
		HashMap map = cargo.getCouponCargo();
		model = new MAXCouponDenominationCounterBeanModel();
		countModelList = new ArrayList();
		if(map != null)		
		{
			if(map.containsKey(cargo.getSelectedCoupon()))
			{
				MAXCouponTypes cpnType = (MAXCouponTypes)map.get(cargo.getSelectedCoupon());
				List cpnDenm = cpnType.getDenominationCount();
				for(int i =0;i<cpnDenm.size();i++)
				{
					countModel = new MAXCouponDenominationCountSummaryBeanModel();
					MAXDenominationCount count = (MAXDenominationCount)cpnDenm.get(i);
					countModel.setAmount(count.getCurrency());
					countModel.setLabel(count.getCurrency()+"");
					countModel.setQuantity(count.getQuantity());
					countModelList.add(countModel);
				}
				model.setDenominationCount(countModelList);
			}
			else
			{
				for(int i=0;i<denominations.length;i++)
				{
					countModel = new MAXCouponDenominationCountSummaryBeanModel();
					countModel.setAmount(denominations[i]);
					countModel.setLabel(currency[i]);
					countModel.setQuantity(0);
					countModelList.add(countModel);
				}
				
				model.setDenominationCount(countModelList);
			}
		}
		else
		{
			for(int i=0;i<denominations.length;i++)
			{
				countModel = new MAXCouponDenominationCountSummaryBeanModel();
				countModel.setAmount(denominations[i]);
				countModel.setLabel(currency[i]);
				countModel.setQuantity(0);
				countModelList.add(countModel);
			}
			model.setDenominationCount(countModelList);
		}
		NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();
		int i=0, key=2;
		for( i=0;i<currency.length;i++)
		{
			if(key==9)
				key=2;
			navModel.addButton("Button", currency[i], currency[i], true, "F"+(key++));
		}
		navModel.addButton("Button", "User Defined", "User Defined", true, "F"+(key));
		PromptAndResponseModel prModel = new PromptAndResponseModel();
		prModel.setArguments("");
		model.setPromptAndResponseModel(prModel);
		model.setLocalButtonBeanModel(navModel);
		ui.showScreen(MAXPOSUIManagerIfc.SELECT_COUPON_TO_COUNT_DETAIL, model);
	}
}
