/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.dailyoperations.poscount.credit;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import max.retail.stores.domain.tender.MAXCreditCardDetails;
import max.retail.stores.domain.tender.MAXTIDDetails;
import max.retail.stores.pos.services.dailyoperations.poscount.MAXPosCountCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXAcquirerBankBeanModel;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.dailyoperations.poscount.PosCountCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXDisplayCreditOptionSite extends PosSiteActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(max.retail.stores.pos.services.dailyoperations.poscount.credit.MAXDisplayCreditOptionSite.class);
	private MAXAcquirerBankBeanModel beanModel;


	public void arrive(BusIfc bus)
	{
		MAXPosCountCargo cargo = (MAXPosCountCargo)bus.getCargo();
		ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
		String[] acquirerBanks = null;
		try {
			acquirerBanks = pm.getStringValues("CreditDebitOfflineBank");
			cargo.setAcquirerBanks(acquirerBanks);
		} 
		catch (ParameterException e) {
			logger.error(e);
		}
        cargo.setCurrentActivity(PosCountCargo.CHARGE);
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();
        int key =2;
        for(int i=0;i<acquirerBanks.length;i++)
        {
        	if(key<=9)
        		navModel.addButton("Button", acquirerBanks[i], acquirerBanks[i], true,"F"+(key++));
        	else
        	{
        		key=2;
        		i--;
        	}
        }
        POSBaseBeanModel pos =new POSBaseBeanModel();
        pos.setLocalButtonBeanModel(navModel);
        
        HashMap acquirerBankMap = cargo.getAcquirerBankDetails();
        CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance();
        
        beanModel = new MAXAcquirerBankBeanModel();
        beanModel.setBankName(acquirerBanks);
        
        CurrencyIfc currency[] = new CurrencyIfc[acquirerBanks.length];
        if(acquirerBankMap!= null)
        {
        	for(int i=0; i<acquirerBanks.length;i++)
        	{
        		if(acquirerBankMap.containsKey(acquirerBanks[i]))
        		{
        			MAXCreditCardDetails crdDtls = (MAXCreditCardDetails)acquirerBankMap.get(acquirerBanks[i]);
        			List tidDtls = crdDtls.getTidDetails();
        			for(int j=0;j<tidDtls.size();j++)
        			{
        				MAXTIDDetails dtls = (MAXTIDDetails)tidDtls.get(j);
        				amount = amount.add(dtls.getAmount());
        			}
        			currency[i] = amount;
        			amount = DomainGateway.getBaseCurrencyInstance();
        		}
        		else
        			currency[i]=DomainGateway.getBaseCurrencyInstance();
        	}
            beanModel.setAmount(currency);
        }
        else
        	for(int i =0;i <currency.length;i++)
        		currency[i] = DomainGateway.getBaseCurrencyInstance("0.00");
        beanModel.setAmount(currency);
        beanModel.setLocalButtonBeanModel(navModel);
        ui.showScreen(MAXPOSUIManagerIfc.ACQUIRER_BANK_DETAIL, beanModel);
	}
	public void depart(BusIfc bus)
	{
		MAXPosCountCargo cargo = (MAXPosCountCargo)bus.getCargo();
		cargo.setCurrentActivity(PosCountCargo.NONE);
	}
}
