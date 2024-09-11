/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		05/Sep/2013		Initial Draft: Chagne done for printing entered count in the till summary report
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.dailyoperations.poscount;

import java.util.List;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.dailyoperations.poscount.PosCountCargo;
import oracle.retail.stores.pos.ui.beans.CurrencyDetailBeanModel;


//------------------------------------------------------------------------------
/**
     Update all foreign tender counts. <P>

     @version $Revision: 7$
**/
//------------------------------------------------------------------------------

public class MAXCheckAllForeignTendersSite extends PosSiteActionAdapter
{
    /**
       revision number
    **/
    public static String revisionNumber = "$Revision: 7$";

    //--------------------------------------------------------------------------
    /**
        Updates the totals with the counted amounts when counting the till. <P>
        @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        // Get the cargo
        MAXPosCountCargo cargo = (MAXPosCountCargo)bus.getCargo();

        // If the count is for float, loan or pick, exit.
        if (cargo.getCountType() != PosCountCargo.TILL)
        {
            bus.mail(new Letter("Success"), BusIfc.CURRENT);
        }
        else // Count is for Till; perform the check.
        {
            if (cargo.getSummaryFlag())
            {
                //Taking into account all the currencies that were entered/expected during reconcile process
                CurrencyTypeIfc[]  altCurrencies = DomainGateway.getAlternateCurrencyTypes();
                for (int i = 0; i < altCurrencies.length; i++)
                {
                    cargo.setCurrentForeignCurrency(altCurrencies[i].getCurrencyCode());
                    // set the tender models for the current foreign currency
                    cargo.setTenderModels(cargo.getForeignTenderModels());
                    // update the financial totals from the tender models
                    cargo.updateTillSummaryInTotalsNoChargeModels();
                }
            }
            else
            {
                // update cash detail count for all foreign currencies
                CurrencyDetailBeanModel[] model = cargo.getCurrencyDetailBeanModels();
                for(int i = 0; i < model.length; i++)
                {
                    cargo.updateCashDetailAmountInTotals(model[i]);
                }
                // update other tenders for all foreign tenders
                cargo.updateTenderDetailAmountsInTotals();
                checkIfTenderedIsEntered(cargo, "Cash", cargo.getCurrentAmount());

            }
            bus.mail(new Letter("Success"), BusIfc.CURRENT);
        }

    }
    
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

}
