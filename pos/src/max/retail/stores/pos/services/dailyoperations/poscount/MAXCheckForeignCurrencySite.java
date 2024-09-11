/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.1	Prateek		25/July/2013		Changes done for 7187
  Rev 1.0	Prateek		04/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.dailyoperations.poscount;

import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.dailyoperations.poscount.CheckForeignCurrencySite;
import oracle.retail.stores.pos.services.dailyoperations.poscount.PosCountCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXCheckForeignCurrencySite extends CheckForeignCurrencySite {

	public void arrive(BusIfc bus)
    {       
		PosCountCargo cargo = (PosCountCargo)bus.getCargo();
		ParameterManagerIfc param = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
		boolean value = true;
    	try {
			value= param.getBooleanValue("CountForeignCurrency").booleanValue();
			/**MAX Rev 1.1 Change : Start**/
			FinancialCountTenderItemIfc[] count = cargo.getForeignCurrencyFinancialCountTenderTotals();
			if(count!=null &&  count.length>0)
				value=true;
			/**MAX Rev 1.1 Change : End**/
		} catch (ParameterException e) {
			logger.error(e);
		}
    	if(value)
    	{
        // Get the cargo
	        
	        String letterName = null;
	
	        // If the count is for float, loan or pick, exit.
	        if ((cargo.getCountType() != PosCountCargo.TILL) ||
	            (getAlternateCurrenciesToCount() == 0))
	        {
	           letterName = CommonLetterIfc.SUCCESS;
	        }
	        else // Count is for Till; perform the check for foregin currency.
	        {
	            if (foreignCurrencyCollected(cargo))
	            {    
	                letterName = CommonLetterIfc.YES;
	            }
	            else
	            {
	                // Display Foreign Currency To Count dialog
	                DialogBeanModel model = new DialogBeanModel();
	                model.setResourceID(FOREIGN_CURRENCY_TO_COUNT);
	                // Yes, No buttons
	                model.setButtonLetter(DialogScreensIfc.BUTTON_NO, CommonLetterIfc.SUCCESS);
	                model.setType(DialogScreensIfc.CONFIRMATION);
	                
	                POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
	                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
	            }
	        }
	        if (letterName != null)
	        {    
	            bus.mail(new Letter(letterName), BusIfc.CURRENT);
	        }
    	}
    	else
    		bus.mail(CommonLetterIfc.SUCCESS);
    } 
}
