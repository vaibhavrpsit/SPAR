/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.1	Prateek		5/Sep/2013		Changes done for printing only entered count in summary report.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.dailyoperations.poscount.giftcert;

import java.util.List;

import max.retail.stores.domain.financial.MAXFinancialTotals;
import max.retail.stores.pos.services.dailyoperations.poscount.MAXPosCountCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXGiftCertDenominationBeanModel;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.OtherTenderDetailBeanModel;
import oracle.retail.stores.pos.ui.beans.SummaryCountBeanModel;

public class MAXCertDenominationEnteredAisle extends PosLaneActionAdapter {

	public void traverse(BusIfc bus)
	{
		MAXPosCountCargo cargo = (MAXPosCountCargo)bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		MAXGiftCertDenominationBeanModel beanModel = (MAXGiftCertDenominationBeanModel)ui.getModel(MAXPOSUIManagerIfc.GIFT_CERT_DETAIL_SCREEN);
		SummaryCountBeanModel sc[] = cargo.getTenderModels();
		for(int i=0;i<sc.length;i++)
		{
			if(sc[i].getActionName().equalsIgnoreCase("GiftCert"))
			{
				sc[i].setAmount(beanModel.getTotal());
			}
		}
		
		cargo.setTenderModels(sc);
		FinancialTotalsIfc totals = cargo.getFinancialTotals();
		((MAXFinancialTotals)totals).setGiftCertificateDenomination(cargo.getGiftCertList());
		cargo.setFinancialTotals(totals);
		
		
		cargo.setCurrentActivityOrCharge(DomainGateway.getFactory()
                .getTenderTypeMapInstance()
                .getDescriptor(TenderLineItemIfc.TENDER_TYPE_GIFT_CERTIFICATE));
		
		OtherTenderDetailBeanModel otherModel = cargo.getOtherTenderDetailBeanModel();
		otherModel.setDescription(DomainGateway.getFactory()
	                         .getTenderTypeMapInstance()
	                         .getDescriptor(TenderLineItemIfc.TENDER_TYPE_GIFT_CERTIFICATE));
		CurrencyIfc[] currency ={beanModel.getTotal()};
		otherModel.setTenderAmounts(currency);
		otherModel.setTotal(beanModel.getTotal());
		cargo.setOtherTenderDetailBeanModel(otherModel);
		
		cargo.updateAcceptedCount();
        cargo.updateCountModel(beanModel.getTotal());
		/**MAX Rev 1.1 Change : Start**/
        checkIfTenderedIsEntered(cargo, "GiftCert", beanModel.getTotal());
		/**MAX Rev 1.1 Change : End**/
        
		bus.mail("Success");
	}

	/**MAX Rev 1.1 Change : Start**/
	private void checkIfTenderedIsEntered(MAXPosCountCargo cargo, String currencyName, CurrencyIfc amount)
	{
		List list = cargo.getEnteredTender();
		boolean flag = false;
		if(amount.compareTo(DomainGateway.getBaseCurrencyInstance("0.00"))>0)
		{
			for(int i=0;i<list.size();i++)
			{
				String tender = (String)list.get(i);
				if(tender.equalsIgnoreCase(currencyName))
					flag = true;
			}
			if(!flag)
				list.add(currencyName);
		}
		if(amount.compareTo(DomainGateway.getBaseCurrencyInstance("0.00"))==0)
		{
			for(int i=0;i<list.size();i++)
			{
				String tender = (String)list.get(i);
				if(tender.equalsIgnoreCase(currencyName))
				{
					list.remove(i);
				}
			}
		}
		cargo.setEnteredTender(list);
		
	}
	/**MAX Rev 1.1 Change : End**/
}
