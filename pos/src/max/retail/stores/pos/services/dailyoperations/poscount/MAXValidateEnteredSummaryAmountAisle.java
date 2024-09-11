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
import oracle.retail.stores.pos.services.dailyoperations.poscount.ValidateEnteredSummaryAmountAisle;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXValidateEnteredSummaryAmountAisle extends
		ValidateEnteredSummaryAmountAisle {

	public void traverse(BusIfc bus)
    {
        PosCountCargo cargo = (PosCountCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        
        // Get the tender amount from the cargo
        CurrencyIfc enteredAmt = cargo.getCurrentAmount();
        CurrencyIfc c = (CurrencyIfc)enteredAmt.clone();

        // Check to make sure they are not trying to remove more than
        // what exists in the till (for pickups)
        TillIfc till = cargo.getRegister().getTillByID(cargo.getTillID());
 // Changes start for code merging(The method getAmountTotal(TenderDescriptorIfc) in the type TillIfc is not applicable for the arguments (String))
  
        //if (cargo.getCountType() == PosCountCargo.PICKUP &&till.getAmountTotal(cargo.getCurrentFLPTender()).compareTo(enteredAmt) == CurrencyIfc.LESS_THAN)
        if (cargo.getCountType() == PosCountCargo.PICKUP &&till.getAmountTotal(cargo.getTenderDescriptorForCurrentFLPTender()).compareTo(enteredAmt) == CurrencyIfc.LESS_THAN)
 // Changes ends for code merging
        {
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("TillCountPickupInvalidAmount");
            model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else //if (cargo.getExpectedAmount().toFormattedString().compareTo(c.toFormattedString()) == 0)
        {
            // Let the cargo know that the user has accepted this count
            cargo.updateAcceptedCount();
            cargo.updateCountModel(enteredAmt);

            if (cargo.getCountType() == PosCountCargo.TILL)
            {
                // Defer update of financial totals until the user
                // exits the service.
            }
            else // Update float, loan or pickup.
            {
                // Put the entered amount in the totals objects.
                cargo.updateFLPSummaryInTotals(enteredAmt, 1);
            }

            // Our work is done here.
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
//        else
//        {
//            // Display the error screen
////            DialogBeanModel dialogModel = new DialogBeanModel();
////            Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
////            UtilityManagerIfc utility =
////              (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
////
////            String args[] = new String[2];
////            String text;
////            if (cargo.getCountType() == PosCountCargo.TILL)
////            {
////                String description = cargo.getCurrentActivityOrCharge();
////                // Convert alternate currency type to complex message
////                if (description.indexOf("Alt") == 0)
////                {
////                    CurrencyTypeIfc[]   altCurrencies = DomainGateway.getAlternateCurrencyTypes();
////                    if (altCurrencies != null) // are there alternate currencies to use
////                    {
////                        String firstAltNationalityTag = altCurrencies[0].getCountryCode() + NATIONALITY_TAG;
////                        String firstAltNationality = utility.retrieveCommonText(firstAltNationalityTag,
////                                                                                firstAltNationalityTag);
////                        String[] vars = new String[1];
////                        vars[0] = firstAltNationality;
////                        String pattern = utility.retrieveDialogText(description, description);
////                        description = LocaleUtilities.formatComplexMessage(pattern,vars);
////                    }
////                }
////
////                String[] vars = new String[2];
////                vars[0] = utility.retrieveText("postCountSpec",
////                                               BundleConstantsIfc.POSCOUNT_BUNDLE_NAME,
////                                               PosCountCargo.COUNT_TYPE_DESCRIPTORS[cargo.getCountType()] + PosCountCargo.LOWER_CASE,
////                                               PosCountCargo.COUNT_TYPE_DESCRIPTORS_DEFAULT_LOWERCASE[cargo.getCountType()]);
////                //Checking for spaces
////                if (!(description.indexOf(" ") < 0))
////                {
////                  description = removeSpaces(description);
////                }
////                vars[1] = utility.retrieveDialogText(description, description);
////                String pattern = utility.retrieveDialogText(TENDER_RECONCILIATION_ERROR_ERRMSG_TAG,
////                                                            DEF_PATTERN_TEXT);
////                text = LocaleUtilities.formatComplexMessage(pattern,vars);
////            }
////            else
////            {
////                text = PosCountCargo.COUNT_TYPE_DESCRIPTORS[cargo.getCountType()].
////                  toLowerCase(locale);
////            }
////
////            args[0] = text;
////            args[1] = text;
////
////            dialogModel.setResourceID(PosCountCargo.RECONCILIATION_ERROR);
////            dialogModel.setType(DialogScreensIfc.CONFIRMATION);
////            dialogModel.setArgs(args);
////
////            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
//        	bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
//        }

    }
}
