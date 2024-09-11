/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.1	Prateek		5/Sep/2013		Change done for printing only entered count in summary report.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.dailyoperations.poscount.credit;

import java.util.List;

import max.retail.stores.domain.financial.MAXFinancialTotals;
import max.retail.stores.pos.services.dailyoperations.poscount.MAXPosCountCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXAcquirerBankBeanModel;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.dailyoperations.poscount.PosCountCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.OtherTenderDetailBeanModel;
import oracle.retail.stores.pos.ui.beans.SummaryCountBeanModel;

public class MAXUpdateTotalDetailAisle extends PosLaneActionAdapter {
	
	public void traverse(BusIfc bus)
	{
		MAXPosCountCargo cargo = (MAXPosCountCargo)bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		
		MAXAcquirerBankBeanModel beanModel = (MAXAcquirerBankBeanModel)ui.getModel(MAXPOSUIManagerIfc.ACQUIRER_BANK_DETAIL);
		SummaryCountBeanModel sc[] = cargo.getChargeModels();
		
		for(int i=0;i<sc.length;i++)
		{
			if(sc[i].getActionName().equalsIgnoreCase("Credit"))
			{
				sc[i].setAmount(beanModel.getTotal());
				break;
			}
		}
		
		FinancialTotalsIfc totals = cargo.getFinancialTotals();
		((MAXFinancialTotals)totals).setAcquirerBankDetails(cargo.getAcquirerBankDetails());
		cargo.setFinancialTotals(totals);
		
		SummaryCountBeanModel crd[] = new SummaryCountBeanModel[beanModel.getBankName().length];
		for(int i=0;i<crd.length;i++)
		{
			crd[i] = new SummaryCountBeanModel();
			crd[i].setActionName(beanModel.getBankName()[i]);
			crd[i].setAmount(beanModel.getAmount()[i]);
			crd[i].setDescription(beanModel.getBankName()[i]);
			crd[i].setLabel(beanModel.getBankName()[i]);
		}
		
		cargo.setCurrentActivityOrCharge(PosCountCargo.CHARGE);

		OtherTenderDetailBeanModel otherModel = new OtherTenderDetailBeanModel();
		otherModel.setDescription(DomainGateway.getFactory()
	                         .getTenderTypeMapInstance()
	                         .getDescriptor(TenderLineItemIfc.TENDER_TYPE_CHARGE));
		
		CurrencyIfc[] currency ={beanModel.getTotal()};
		otherModel.setTenderAmounts(currency);
		otherModel.setTotal(beanModel.getTotal());
		cargo.setOtherTenderDetailBeanModel(otherModel);
		cargo.setChargeModels(crd);
		cargo.updateAcceptedCount();
        cargo.updateCountModel(beanModel.getTotal());
		/**MAX Rev 1.1 Change : Start**/
        checkIfTenderedIsEntered(cargo, "Credit", beanModel.getTotal());
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
