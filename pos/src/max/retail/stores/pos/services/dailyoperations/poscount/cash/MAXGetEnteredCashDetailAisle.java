/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.1	Prateek		5/Sep/2013		Changes done to show only entered count in till summary report.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.dailyoperations.poscount.cash;

// foundation imports
import java.util.ArrayList;
import java.util.List;

import max.retail.stores.domain.financial.MAXFinancialTotals;
import max.retail.stores.domain.tender.MAXDenominationCount;
import max.retail.stores.pos.services.dailyoperations.poscount.MAXPosCountCargo;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CurrencyDetailBeanModel;

//--------------------------------------------------------------------------
/**
    Save the entered cash detail count to the cargo.
    <p>
     @version $Revision: 4$
**/
//--------------------------------------------------------------------------
public class MAXGetEnteredCashDetailAisle extends PosLaneActionAdapter
{
    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: 4$";

    //----------------------------------------------------------------------
    /**
       Saves the entered cash detail count to the cargo and mails a Continue 
       letter. <p>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // Get the tender amount from the UI
        POSUIManagerIfc ui         = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        MAXPosCountCargo   cargo      = (MAXPosCountCargo)bus.getCargo();

        // Save the entered amount in the cargo in case we need it later.
        CurrencyDetailBeanModel beanModel = (CurrencyDetailBeanModel)ui.getModel(POSUIManagerIfc.CURRENCY_DETAIL);
        CurrencyIfc            enteredAmt = beanModel.getTotal();
        cargo.addCurrencyDetailBeanModel(enteredAmt.getCountryCode(), beanModel);
        cargo.setCurrentAmount(enteredAmt);
        
        
        /**Changes for denomination capture**/
        List cashDnm = new ArrayList();
        String name[] = beanModel.getTotal().getDenominationNames();
        String dnm[] = beanModel.getTotal().getDenominationValues();
        Long dnmCount[] = beanModel.getDenominationCounts();
        MAXDenominationCount count = new MAXDenominationCount();
        for(int i=0;i< dnm.length;i++)
        {
        	count = new MAXDenominationCount();
        	count.setCurrency(DomainGateway.getBaseCurrencyInstance(dnm[i]));
        	count.setQuantity(dnmCount[i].intValue());
        	count.setSubType(name[i]);
        	cashDnm.add(count);
        }
        FinancialTotalsIfc totals = (FinancialTotalsIfc)cargo.getFinancialTotals();
        if(totals instanceof MAXFinancialTotals)
        	((MAXFinancialTotals)totals).setCashDenomination(cashDnm);
        cargo.setCashDenomination(cashDnm);
		/**MAX Rev 1.1 Change : Start**/
        checkIfTenderedIsEntered(cargo, "Cash", beanModel.getTotal());
		/**MAX Rev 1.1 Change : End**/
        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
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
