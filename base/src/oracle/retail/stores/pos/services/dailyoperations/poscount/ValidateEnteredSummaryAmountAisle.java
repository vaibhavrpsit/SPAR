/* ===========================================================================
* Copyright (c) 2004, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/ValidateEnteredSummaryAmountAisle.java /main/17 2014/07/08 14:45:46 crain Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    crain     07/08/14 - Refactored dialog screens.
 *    crain     07/08/14 - Added error sdialog screen
 *                         TillCountPickupSameRegister
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/09/10 - optimize calls to LocaleMAp
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    blarsen   04/23/09 - the *float* value was not being translated for till
 *                         reconcile entry error case
 *    ranojha   11/21/08 - Fixed code to use the latest api instead of the
 *                         deprecate getAmountTotal
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:52:30 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:30:42 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:40 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:28 PM  Robert Pearse
 *
 *   Revision 1.5  2004/03/03 23:15:06  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.4  2004/02/16 14:40:13  blj
 *   @scr 3838 - cleanup code
 *
 *   Revision 1.3  2004/02/12 16:49:39  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:40  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Feb 09 2004 17:45:54   DCobb
 * Use CommonLetterIfc constants.
 * Resolution for 3381: Feature Enhancement:  Till Pickup and Loan
 *
 *    Rev 1.0   Feb 04 2004 18:35:14   DCobb
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;


import java.util.StringTokenizer;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    Validates the amount entered.
    <p>
    @version $Revision: /main/17 $
**/
//--------------------------------------------------------------------------
public class ValidateEnteredSummaryAmountAisle extends PosLaneActionAdapter
{
    /**
     *
     */
    private static final long serialVersionUID = -9055518542999762259L;
    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /main/17 $";
    /**
       Tag for Nationality Descriptor
    **/
    public static final String NATIONALITY_TAG = "_Nationality";
    /**
      default pattern text
    **/
    public static final String DEF_PATTERN_TEXT = "{0} {1}";
    /**
      Tender Reconciliation Error error Msg  tag
    **/
    public static final String TENDER_RECONCILIATION_ERROR_ERRMSG_TAG = "TenderReconciliationError.errorMsg";
    /**
        Till count pickup same register
    **/
    public static final String TILL_COUNT_PICKUP_SAME_REGISTER ="TillCountPickupSameRegister";
    /**
        Till count pickup same register
    **/
    public static final String TILL_COUNT_PICKUP_INVALID_AMOUNT ="TillCountPickupInvalidAmount";
    //----------------------------------------------------------------------
    /**
       Gets the Summary Amount and saves it in the cargo.
       <p>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        PosCountCargo cargo = (PosCountCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        
      String pickupAndLoanFromRegister = cargo.getPickupAndLoanFromRegister();
        
      if (pickupAndLoanFromRegister != null && pickupAndLoanFromRegister.equals(cargo.getPickupAndLoanToRegister()))
      {
            showErrorDialog(ui,TILL_COUNT_PICKUP_SAME_REGISTER);
      }
      else 
      {        
        // Get the tender amount from the cargo
        CurrencyIfc enteredAmt = cargo.getCurrentAmount();
        CurrencyIfc c = (CurrencyIfc)enteredAmt.clone();


        // Check to make sure they are not trying to remove more than
        // what exists in the till (for pickups)
        TillIfc till = cargo.getRegister().getTillByID(cargo.getTillID());
        if (cargo.getCountType() == PosCountCargo.PICKUP && till.getAmountTotal(cargo.getTenderDescriptorForCurrentFLPTender()).compareTo(enteredAmt) == CurrencyIfc.LESS_THAN)
        {
            showErrorDialog(ui,TILL_COUNT_PICKUP_INVALID_AMOUNT);
        }
        else if (cargo.getExpectedAmount().toFormattedString().compareTo(c.toFormattedString()) == 0)
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
        else
        {
            // Display the error screen
            DialogBeanModel dialogModel = new DialogBeanModel();

            UtilityManagerIfc utility =
              (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

            String args[] = new String[2];
            String text;
            if (cargo.getCountType() == PosCountCargo.TILL)
            {
                String description = cargo.getCurrentActivityOrCharge();
                // Convert alternate currency type to complex message
                if (description.indexOf("Alt") == 0)
                {
                    CurrencyTypeIfc[]   altCurrencies = DomainGateway.getAlternateCurrencyTypes();
                    if (altCurrencies != null) // are there alternate currencies to use
                    {
                        String firstAltNationalityTag = altCurrencies[0].getCountryCode() + NATIONALITY_TAG;
                        String firstAltNationality = utility.retrieveCommonText(firstAltNationalityTag,
                                                                                firstAltNationalityTag);
                        String[] vars = new String[1];
                        vars[0] = firstAltNationality;
                        String pattern = utility.retrieveDialogText(description, description);
                        description = LocaleUtilities.formatComplexMessage(pattern,vars);
                    }
                }

                String[] vars = new String[2];
                vars[0] = utility.retrieveText("postCountSpec",
                                               BundleConstantsIfc.POSCOUNT_BUNDLE_NAME,
                                               PosCountCargo.COUNT_TYPE_DESCRIPTORS[cargo.getCountType()] + PosCountCargo.LOWER_CASE,
                                               PosCountCargo.COUNT_TYPE_DESCRIPTORS_DEFAULT_LOWERCASE[cargo.getCountType()]);
                //Checking for spaces
                if (!(description.indexOf(" ") < 0))
                {
                  description = removeSpaces(description);
                }
                vars[1] = utility.retrieveDialogText(description, description);
                String pattern = utility.retrieveDialogText(TENDER_RECONCILIATION_ERROR_ERRMSG_TAG,
                                                            DEF_PATTERN_TEXT);
                text = LocaleUtilities.formatComplexMessage(pattern,vars);
            }
            else
            {
                text = utility.retrieveText("postCountSpec",
                        BundleConstantsIfc.POSCOUNT_BUNDLE_NAME,
                        PosCountCargo.COUNT_TYPE_DESCRIPTORS[cargo.getCountType()] + PosCountCargo.LOWER_CASE,
                        PosCountCargo.COUNT_TYPE_DESCRIPTORS_DEFAULT_LOWERCASE[cargo.getCountType()]);
            }

            args[0] = text;
            args[1] = text;

            dialogModel.setResourceID(PosCountCargo.RECONCILIATION_ERROR);
            dialogModel.setType(DialogScreensIfc.CONFIRMATION);
            dialogModel.setArgs(args);

            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
      }

    }
    //---------------------------------------------------------------------
    /**
        Remove spaces from String. <P>
        @param s String that needs to get spaces removed
        @return String without spaces
    **/
    //---------------------------------------------------------------------
    public String removeSpaces(String s)
    {
        StringTokenizer st = new StringTokenizer(s," ",false);
        StringBuffer sBuffer = new StringBuffer();
        while (st.hasMoreElements())
        {
            sBuffer = sBuffer.append(st.nextElement());
        }
        return sBuffer.toString();
    }
    //---------------------------------------------------------------------
    /**
        Show simple error dialog screen. <P>
        @param ui as POSUIManagerIfc, resourceID as String
    **/
    //---------------------------------------------------------------------
    protected void showErrorDialog(POSUIManagerIfc ui, String resourceID)
    {
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(resourceID);
        model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}
