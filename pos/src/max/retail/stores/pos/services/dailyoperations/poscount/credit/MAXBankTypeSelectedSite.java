/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.dailyoperations.poscount.credit;

import java.util.HashMap;
import java.util.List;

import max.retail.stores.domain.tender.MAXCreditCardDetails;
import max.retail.stores.domain.tender.MAXTIDDetails;
import max.retail.stores.pos.services.dailyoperations.poscount.MAXPosCountCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXCreditTIDDetailBeanModel;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.ui.jfc.ButtonPressedLetter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.dailyoperations.poscount.PosCountCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

public class MAXBankTypeSelectedSite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void arrive(BusIfc bus)
	{
		MAXPosCountCargo cargo = (MAXPosCountCargo)bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		
		HashMap bankMap = cargo.getAcquirerBankDetails();
		
		if(bus.getCurrentLetter() instanceof ButtonPressedLetter)
		{
			ButtonPressedLetter letterPressed = (ButtonPressedLetter) bus.getCurrentLetter();
			if(letterPressed.getName().equalsIgnoreCase("Button"))
			{
				System.out.println("letterPressed.getNumber()"+letterPressed.getNumber());
				if(letterPressed.getNumber() <=8)
					cargo.setSelectedAcquirerBankName(cargo.getAcquirerBanks()[letterPressed.getNumber()]);
				else
					cargo.setSelectedAcquirerBankName(cargo.getAcquirerBanks()[letterPressed.getNumber()-1]);
			}
		}
		
		MAXCreditTIDDetailBeanModel model = new MAXCreditTIDDetailBeanModel();
		String[] tid, batchid;
		CurrencyIfc amount[];
		if(bankMap != null)
		{
			if(bankMap.containsKey(cargo.getSelectedAcquirerBankName()))
			{
				MAXCreditCardDetails card = (MAXCreditCardDetails)bankMap.get(cargo.getSelectedAcquirerBankName());
				List tidList = card.getTidDetails();
				tid = new String[tidList.size()];
				batchid = new String[tidList.size()];
				amount = new CurrencyIfc[tidList.size()];
				for(int i=0;i<tidList.size();i++)
				{
					MAXTIDDetails tidDtls = (MAXTIDDetails)tidList.get(i);
					tid[i] = tidDtls.getTid();
					batchid[i] = tidDtls.getBatchid();
					amount[i] = tidDtls.getAmount();
				}
				model.setTid(tid);
				model.setBatchid(batchid);
				model.setAmount(amount);
				model.updateTotals();
			}
			else
			{
				model = getDefaultModel(model);
			}
		}
		else
			model = getDefaultModel(model);
		
		
		NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();
		navModel.addButton("TID", "TID", "TID", true, "F2");
		model.setLocalButtonBeanModel(navModel);
		ui.showScreen(MAXPOSUIManagerIfc.CREDIT_TID_DETAIL, model);
		
	}
	
	public void depart(BusIfc bus)
	{
		MAXPosCountCargo cargo = (MAXPosCountCargo)bus.getCargo();
		cargo.getCurrentActivity().equals(PosCountCargo.NONE);
	}
	
	public MAXCreditTIDDetailBeanModel getDefaultModel(MAXCreditTIDDetailBeanModel model)
	{
		String[] tid, batchid;
		CurrencyIfc amount[];
		tid = new String[0];
		batchid = new String[0];
		amount = new CurrencyIfc[0];
		model.setTid(tid);
		model.setBatchid(batchid);
		model.setAmount(amount);
		model.setTotal(DomainGateway.getBaseCurrencyInstance());
		return model;
		
	}
}
