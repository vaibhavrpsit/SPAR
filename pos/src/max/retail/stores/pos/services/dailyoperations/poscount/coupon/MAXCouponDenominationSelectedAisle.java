/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.dailyoperations.poscount.coupon;

import java.util.ArrayList;
import java.util.HashMap;

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
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

	public class MAXCouponDenominationSelectedAisle extends PosLaneActionAdapter {
	
	public void traverse(BusIfc bus)
	{
		MAXPosCountCargo cargo = (MAXPosCountCargo)bus.getCargo();
		ButtonPressedLetter letter = null;
		if(bus.getCurrentLetter() instanceof ButtonPressedLetter)
		{
			letter = (ButtonPressedLetter)bus.getCurrentLetter();
		}		
		CurrencyIfc denomination = null;
		String currency[] = null;
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		ArrayList countModelList= null;
		MAXCouponDenominationCounterBeanModel cpnModel = null;
		try 
		{
			currency = pm.getStringValues("SupportedDenomination");
		
			if(letter.getNumber()<=currency.length)
			{
				MAXCouponDenominationCountSummaryBeanModel countModel = new MAXCouponDenominationCountSummaryBeanModel();
				if(letter.getNumber()>7)
					denomination = DomainGateway.getBaseCurrencyInstance(currency[letter.getNumber()-1]);
				else
					denomination = DomainGateway.getBaseCurrencyInstance(currency[letter.getNumber()]);
				POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
				POSBaseBeanModel model = (POSBaseBeanModel)ui.getModel();
				PromptAndResponseModel rspModel = model.getPromptAndResponseModel();
				int quantity = Integer.parseInt(rspModel.getResponseText());
				countModel.setAmount(denomination);
				countModel.setQuantity(quantity);
				countModel.setLabel(denomination+"");
				if (ui.getModel(MAXPOSUIManagerIfc.SELECT_COUPON_TO_COUNT_DETAIL) instanceof MAXCouponDenominationCounterBeanModel)
				{
					cpnModel = (MAXCouponDenominationCounterBeanModel) ui
							.getModel(MAXPOSUIManagerIfc.SELECT_COUPON_TO_COUNT_DETAIL);
					countModelList = cpnModel.getDenominationCount();
					int index =getIndexOfDenomination(countModelList, denomination); 
					if(index == -1)
						countModelList.add(countModel);
					else
						((MAXCouponDenominationCountSummaryBeanModel)countModelList.get(index)).setQuantity(quantity);
					cpnModel.setDenominationCount(countModelList);
					ui.setModel(MAXPOSUIManagerIfc.SELECT_COUPON_TO_COUNT_DETAIL, cpnModel);
					
					HashMap couponMap= cargo.getCouponCargo();
					MAXCouponDenominationCountSummaryBeanModel smmryModel = null;								
					if(couponMap == null)
						couponMap = new HashMap();
					MAXCouponTypes cpnType = new MAXCouponTypes();
					cpnType.setCouponName(cargo.getSelectedCoupon());
					ArrayList cpnTypeList = new ArrayList(); 
					MAXDenominationCount count = null;
					
					for(int i=0; i<countModelList.size();i++)
					{
						smmryModel = (MAXCouponDenominationCountSummaryBeanModel)countModelList.get(i);
						count = new MAXDenominationCount();
						count.setCurrency(smmryModel.getAmount());
						count.setQuantity(smmryModel.getQuantity());
						cpnTypeList.add(count);
					}
					cpnType.setDenominationCount(cpnTypeList);
					couponMap.put(cargo.getSelectedCoupon(), cpnType);
					cargo.setCouponCargo(couponMap);
				} 
				bus.mail("Continue");
			}
			else
			{
				bus.mail("UserDefined");
			}
		}
		catch(NumberFormatException e)
		{
			showErrorDialog(bus, "InvalidDenominationQuantity");
		}
		catch(ParameterException e)
		{
			showErrorDialog(bus, "InavlidDenominationParameter");
		}
	}
	public int getIndexOfDenomination(ArrayList countModelList, CurrencyIfc currency)
	{
		int index=-1;
		for(int i=0;i<countModelList.size();i++)
		{
			MAXCouponDenominationCountSummaryBeanModel model= (MAXCouponDenominationCountSummaryBeanModel)countModelList.get(i);
			if(model.getAmount().compareTo(currency)==0)
			{
				return i;
			}
		}
		return index;
	}
	private void showErrorDialog(BusIfc bus, String errorMessage)
	{
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(errorMessage);        
        model.setType(DialogScreensIfc.ERROR);
        model.setButtonLetter(DialogScreensIfc.ACKNOWLEDGEMENT, CommonLetterIfc.OK);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
	}
}
