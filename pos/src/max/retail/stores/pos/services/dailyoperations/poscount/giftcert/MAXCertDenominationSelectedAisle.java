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
import max.retail.stores.pos.ui.beans.MAXGiftCertDenominationBeanModel;
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
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXCertDenominationSelectedAisle extends PosLaneActionAdapter {

	public void traverse(BusIfc bus)
	{
		int quantity = 0, letterNumber=-1;
		MAXPosCountCargo cargo = (MAXPosCountCargo)bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		MAXGiftCertDenominationBeanModel beanModel = (MAXGiftCertDenominationBeanModel)ui.getModel(MAXPOSUIManagerIfc.GIFT_CERT_DETAIL_SCREEN);
		PromptAndResponseModel model = beanModel.getPromptAndResponseModel();
		if(bus.getCurrentLetter() instanceof ButtonPressedLetter)
		{
			ButtonPressedLetter letterPressed = (ButtonPressedLetter) bus.getCurrentLetter();
			letterNumber = letterPressed.getNumber();
			if(letterNumber > 7)
				letterNumber--;
		}
		
		ParameterManagerIfc param = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
		String denomination[] = null;
		try
		{
			denomination = param.getStringValues("SupportedDenomination");
			MAXDenominationCount count = null;
			quantity= Integer.parseInt(model.getResponseText());
			List gclist = cargo.getGiftCertList();
			if(gclist == null)
			{
				gclist = new ArrayList();
				count = new MAXDenominationCount();
				count.setCurrency(DomainGateway.getBaseCurrencyInstance(denomination[letterNumber]));
				count.setQuantity(quantity);
				gclist.add(count);
			}
			else
			{
				int index =-1;
				for(int i=0;i< gclist.size();i++)
				{
					count = (MAXDenominationCount)gclist.get(i);
					if(count.getCurrency().getStringValue().equalsIgnoreCase(denomination[letterNumber]))
					{	
						index =i;
						break;
					}
				}
				if(index == -1)
					count = new MAXDenominationCount();
				else
					count = (MAXDenominationCount)gclist.get(index);
				count.setCurrency(DomainGateway.getBaseCurrencyInstance(denomination[letterNumber]));
				count.setQuantity(quantity);
				if(index !=-1)
					gclist.set(index, count);
				else
					gclist.add(count);
			}
			cargo.setGiftCertList(gclist);
			bus.mail("Continue");
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
