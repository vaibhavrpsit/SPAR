/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.dailyoperations.poscount;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.dailyoperations.poscount.PosCountCargo;
import oracle.retail.stores.pos.services.dailyoperations.poscount.ValidateEnteredCashDetailAisle;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CurrencyDetailBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXValidateEnteredCashDetailAisle extends
		ValidateEnteredCashDetailAisle {

	public void traverse(BusIfc bus)
	{
		 PosCountCargo cargo = (PosCountCargo)bus.getCargo();
	        CurrencyIfc enteredAmt = cargo.getCurrentAmount();
	        POSUIManagerIfc ui         = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
	        
	        // Check to make sure they are not trying to remove more than
	        // what exists in the till (for pickups)
	        TillIfc till = cargo.getRegister().getTillByID(cargo.getTillID()); 
	        CurrencyIfc expectedAmount = cargo.getExpectedAmount();
	        
	        if ((cargo.getCountType() == PosCountCargo.PICKUP)            
	            && (till.getAmountTotal(cargo.getTenderDescriptorForCurrentFLPTender())
	                .compareTo(enteredAmt) == CurrencyIfc.LESS_THAN))
	        {
	            DialogBeanModel model = new DialogBeanModel();
	            model.setResourceID("TillCountPickupInvalidAmount");
	            model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
	            model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);
	            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
	        }
	        else //if (expectedAmount.equals(enteredAmt))
	        {
	            // Let the cargo know that the user has accepted this count
	            cargo.updateAcceptedCount();
	            cargo.updateCountModel(enteredAmt);

	            if (cargo.getCountType() == PosCountCargo.TILL)
	            {
	                // Defer update of financial totals until the user
	                // exits the service.
	            }
	            else
	            {
	                // Put the entered amount in the totals objects.
	                String key = enteredAmt.getCountryCode();
	                CurrencyDetailBeanModel beanModel = cargo.getCurrencyDetailBeanModel(key);
	                cargo.updateCashDetailAmountInTotals(beanModel);
	            }

	            // Our work is done here.
	            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
	        }
////	        else
//	        {
////	            // Display the error screen
////	            DialogBeanModel model = new DialogBeanModel();
////	            Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
////	            UtilityManagerIfc utility = 
////	              (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
////	            String args[] = new String[2];
////	            String text;
////	            if (cargo.getCountType() == PosCountCargo.TILL)
////	            {
////	                String[] vars = new String[2];
////	                vars[0] = utility.retrieveText("postCountSpec", 
////	                                               BundleConstantsIfc.POSCOUNT_BUNDLE_NAME,
////	                                               PosCountCargo.COUNT_TYPE_DESCRIPTORS[cargo.getCountType()]+PosCountCargo.LOWER_CASE,
////	                                               PosCountCargo.COUNT_TYPE_DESCRIPTORS_DEFAULT_LOWERCASE[cargo.getCountType()]);
////	                vars[1] = cargo.getCurrentActivityOrCharge();
////	                String pattern = utility.retrieveDialogText(TENDER_RECONCILIATION_ERROR_ERRMSG_TAG,
////	                                                            DEF_PATTERN_TEXT);
////	                text = LocaleUtilities.formatComplexMessage(pattern,vars);
////	            }
////	            else
////	            {
////	                text = PosCountCargo.COUNT_TYPE_DESCRIPTORS[cargo.getCountType()].
////	                  toLowerCase(locale);
////	            }
////
////	            args[0] = text;
////	            args[1] = text;
////
////	            model.setResourceID(PosCountCargo.RECONCILIATION_ERROR);
////	            model.setType(DialogScreensIfc.CONFIRMATION);
////	            model.setArgs(args);
////	            
////	            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
//	            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
//
//	        }

	}
}
