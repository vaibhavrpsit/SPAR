/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.dailyoperations.poscount;

import java.util.List;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.dailyoperations.poscount.PosCountCargo;
import oracle.retail.stores.pos.services.dailyoperations.poscount.ValidateEnteredTenderDetailAisle;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.OtherTenderDetailBeanModel;

public class MAXValidateEnteredTenderDetailAisle extends
		ValidateEnteredTenderDetailAisle {

	public void traverse(BusIfc bus)
	{
		
		// Get the tender amount from the UI
        POSUIManagerIfc ui    = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        MAXPosCountCargo   cargo = (MAXPosCountCargo)bus.getCargo();

        OtherTenderDetailBeanModel beanModel =
            cargo.getOtherTenderDetailBeanModel();
        CurrencyIfc enteredAmt = cargo.getCurrentAmount();
        if (cargo.getCountType() == PosCountCargo.PICKUP)
        {
            beanModel.setSummaryDescription(cargo.getCurrentFLPTender());
            beanModel.setDescription(cargo.getCurrentFLPTender());
        }

//        if (cargo.getExpectedAmount().equals(enteredAmt))
//        {
            // Let the cargo know that the user has accepted this count
            cargo.updateAcceptedCount();
            cargo.updateCountModel(enteredAmt);
            checkIfTenderedIsEntered(cargo, beanModel);
            // Put the entered amount in the totals objects.
            if (cargo.getCountType() == PosCountCargo.TILL)
            {
                // Defer update of financial totals until the user
                // exits the services.
            }
            else // Update float, loan or pickup.
            {
                cargo.updateCheckAmountsInTotals(beanModel);
            }
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
//        }
//        else
//        {
//            // Display the error screen
//            DialogBeanModel model = new DialogBeanModel();
//            Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
//            UtilityManagerIfc utility = 
//              (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
//            
//            String args[] = new String[2];
//            String text;
//            if (cargo.getCountType() == PosCountCargo.TILL)
//            {
//                String[] vars = new String[2];
//                vars[0] = utility.retrieveText("postCountSpec", 
//                                               BundleConstantsIfc.POSCOUNT_BUNDLE_NAME,
//                                               PosCountCargo.COUNT_TYPE_DESCRIPTORS[cargo.getCountType()] + PosCountCargo.LOWER_CASE,
//                                               PosCountCargo.COUNT_TYPE_DESCRIPTORS_DEFAULT_LOWERCASE[cargo.getCountType()]);
//                vars[1] = cargo.getCurrentActivityOrCharge();
//                String pattern = utility.retrieveDialogText(TENDER_RECONCILIATION_ERROR_ERRMSG_TAG,
//                                                            DEF_PATTERN_TEXT);
//                text = LocaleUtilities.formatComplexMessage(pattern,vars);
//            }
//            else
//            {
//                text = PosCountCargo.COUNT_TYPE_DESCRIPTORS[cargo.getCountType()].
//                  toLowerCase(locale);
//            }
//
//            args[0] = text;
//            args[1] = text;
//
//            model.setResourceID(PosCountCargo.RECONCILIATION_ERROR);
//            model.setType(DialogScreensIfc.CONFIRMATION);
//            model.setArgs(args);
//
//            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
//        }
	}
	private void checkIfTenderedIsEntered(MAXPosCountCargo cargo, OtherTenderDetailBeanModel beanModel)
	{
		List list = cargo.getEnteredTender();
		boolean flag = false;
		if(beanModel.getTotal().compareTo(DomainGateway.getBaseCurrencyInstance("0.00"))>0)
		{
			for(int i=0;i<list.size();i++)
			{
				String tender = (String)list.get(i);
				if(tender.equalsIgnoreCase(beanModel.getDescription()))
					flag = true;
			}
			if(!flag)
				list.add(beanModel.getDescription());
		}
		if(beanModel.getTotal().compareTo(DomainGateway.getBaseCurrencyInstance("0.00"))==0)
		{
			for(int i=0;i<list.size();i++)
			{
				String tender = (String)list.get(i);
				if(tender.equalsIgnoreCase(beanModel.getDescription()))
				{
					list.remove(i);
				}
			}
		}
		cargo.setEnteredTender(list);
		
	}
}
