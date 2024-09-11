/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
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
import max.retail.stores.pos.ui.beans.MAXUserDefineDenominationBeanModel;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXUserDefinedDenominationSite extends PosSiteActionAdapter {

	private static final long serialVersionUID = 1L;
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
		if(!denomination.getStringValue().equalsIgnoreCase("0.00"))
		{
			int quantity = model.getQuantity();
			HashMap cpnTypes = cargo.getCouponCargo();
			if(cpnTypes == null)
				cpnTypes = new HashMap();
			if(cpnTypes.containsKey(cargo.getSelectedCoupon()))
			{
				MAXCouponTypes cType = (MAXCouponTypes)cpnTypes.get(cargo.getSelectedCoupon());
				List denmType = cType.getDenominationCount();
				MAXDenominationCount count = new MAXDenominationCount();
				int index=-1;
				if(denmType == null)
					denmType = new ArrayList();
				else
				{
					for(int i=0;i< denmType.size();i++)
					{
						count= (MAXDenominationCount)denmType.get(i);
						if(count.getCurrency().equals(denomination))
						{
							index=i;
							break;
						}
					}
				}
				if(index == -1)
				{
					count = new MAXDenominationCount();
					count.setCurrency(denomination);
					count.setQuantity(quantity);
					denmType.add(count);
				}
				else
				{
					count=(MAXDenominationCount)denmType.get(index);
					count.setQuantity(quantity);
					denmType.set(index, count);
				}
				cType.setDenominationCount(denmType);
				cpnTypes.put(cargo.getSelectedCoupon(), cType);
				cargo.setCouponCargo(cpnTypes);
			}
			else
			{
				ParameterManagerIfc prMgr = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
				String currency[] = null;
				CurrencyIfc[] denmType = null;
				try	{
					currency = prMgr.getStringValues("SupportedDenomination");
				}
				catch(ParameterException e){
					logger.info(e);
					showErrorDialog(bus, "InavlidDenominationParameter");
				}
				denmType = new CurrencyIfc[currency.length];
				for(int i=0;i<currency.length;i++)
					denmType[i] = DomainGateway.getBaseCurrencyInstance(currency[i]);
				MAXCouponTypes cType = new MAXCouponTypes();
				MAXDenominationCount count = new MAXDenominationCount();
				cType.setCouponName(cargo.getSelectedCoupon());
				List denType = new ArrayList();
				for(int i=0;i<currency.length;i++)
				{
					count = new MAXDenominationCount();
					count.setCurrency(denmType[i]);
					count.setQuantity(0);
					denType.add(count);
				}
				count = new MAXDenominationCount();
				count.setCurrency(denomination);
				count.setQuantity(quantity);
				denType.add(count);
				cType.setDenominationCount(denType);
				cpnTypes.put(cargo.getSelectedCoupon(),cType);
				cargo.setCouponCargo(cpnTypes);
			}
		}

	}
	private void showErrorDialog(BusIfc bus,String errorMessage)
	{
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(errorMessage);        
        model.setType(DialogScreensIfc.ERROR);
        model.setButtonLetter(DialogScreensIfc.ACKNOWLEDGEMENT, CommonLetterIfc.OK);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
	}
}