/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.dailyoperations.poscount.credit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import max.retail.stores.domain.tender.MAXCreditCardDetails;
import max.retail.stores.domain.tender.MAXTIDDetails;
import max.retail.stores.pos.services.dailyoperations.poscount.MAXPosCountCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXTidCaptureBeanModel;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

public class MAXEnterTidAmountDetailSite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String siteName = "MAXEnterTidAmountDetailSite";
	
	public void arrive(BusIfc bus)
	{
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		MAXTidCaptureBeanModel model = new MAXTidCaptureBeanModel();
		ui.showScreen(MAXPOSUIManagerIfc.ENTER_TID_AMOUNT_DETAIL, model);
		
	}
	
	public void depart(BusIfc bus)
	{
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		MAXPosCountCargo cargo = (MAXPosCountCargo)bus.getCargo();
		if(!bus.getCurrentLetter().getName().equalsIgnoreCase("Undo"))
		{
			MAXTidCaptureBeanModel model = (MAXTidCaptureBeanModel)ui.getModel(MAXPOSUIManagerIfc.ENTER_TID_AMOUNT_DETAIL);
			
			String tid = model.getTid();
			String batchid = model.getBatchid();
			CurrencyIfc amount = model.getAmount();
			String bankName = cargo.getSelectedAcquirerBankName();
			MAXTIDDetails tidDtls = new MAXTIDDetails(tid, batchid, amount);
			
			HashMap map = cargo.getAcquirerBankDetails();
			List list = new ArrayList();
			if(map == null)
				map = new HashMap();
			MAXCreditCardDetails card = null;
			if(map.containsKey(bankName))
				card = (MAXCreditCardDetails)map.get(bankName);
			else
				card = new MAXCreditCardDetails();
			card.setBankName(bankName);
			list = card.getTidDetails();
			int index = -1;
			if(list == null)
				list = new ArrayList();
			else
			{
				for(int i=0; i<list.size();i++)
				{
					MAXTIDDetails tDtls = (MAXTIDDetails)list.get(i);
					if(tDtls.getTid().equalsIgnoreCase(tid) && tDtls.getBatchid().equalsIgnoreCase(batchid))
					{
						index =i;
						break;
					}
				}
	
			}
			if(index == -1)
				list.add(tidDtls);
			else
				list.set(index, tidDtls);
			card.setTidDetails(list);
			map.put(bankName, card);
			
			cargo.setAcquirerBankDetails(map);
		}
	}
}
