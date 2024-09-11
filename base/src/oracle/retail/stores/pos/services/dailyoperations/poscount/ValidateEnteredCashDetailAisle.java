/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/ValidateEnteredCashDetailAisle.java /main/12 2011/12/05 12:16:18 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    jswan     11/04/11 - Fixed issue with getting expected float amount
 *    rsnayak   10/14/11 - country code fix for till pickup
 *    hyin      10/04/11 - forward port: wrong cash count message
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
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
 *   Revision 1.6  2004/07/30 21:17:11  dcobb
 *   @scr 6462 Financial Totals are not correct for the detail count during Till Open/Reconcile
 *   Replaced all instances of FinancialCountIfc.getTenderItem(int, String) with getSummaryTenderItemByDescriptor(TenderDescriptorIfc).
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
 *    Rev 1.2   Feb 09 2004 17:43:24   DCobb
 * Added check for amount in till when picking up cash.
 * Resolution for 3381: Feature Enhancement:  Till Pickup and Loan
 * 
 *    Rev 1.1   Feb 09 2004 13:32:28   DCobb
 * Correct expected amount.
 * Resolution for 3381: Feature Enhancement:  Till Pickup and Loan
 * 
 *    Rev 1.0   Feb 06 2004 17:09:26   DCobb
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CurrencyDetailBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    Validate the entered cash detail count.
    <p>
    @version $Revision: /main/12 $
**/
//--------------------------------------------------------------------------
public class ValidateEnteredCashDetailAisle extends PosLaneActionAdapter
{
    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /main/12 $";
    /**
      default pattern text
    **/
    public static final String DEF_PATTERN_TEXT = "{0} {1}";
    /**
      Tender Reconciliation Error error Msg  tag
    **/
    public static final String TENDER_RECONCILIATION_ERROR_ERRMSG_TAG = "TenderReconciliationError.errorMsg";
    

    //----------------------------------------------------------------------
    /**
       Validates the entered detail cash count. Sends a Success letter or  
       displays the Tender Reconciliation Error dialog.
       <p>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
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
        else if (expectedAmount.equals(enteredAmt))
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
        else
        {
            // Display the error screen
            DialogBeanModel model = new DialogBeanModel();
            Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
            UtilityManagerIfc utility = 
              (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            String args[] = new String[2];
            String text;
            if (cargo.getCountType() == PosCountCargo.TILL)
            {
                String[] vars = new String[2];
                vars[0] = utility.retrieveText("postCountSpec", 
                                               BundleConstantsIfc.POSCOUNT_BUNDLE_NAME,
                                               PosCountCargo.COUNT_TYPE_DESCRIPTORS[cargo.getCountType()]+PosCountCargo.LOWER_CASE,
                                               PosCountCargo.COUNT_TYPE_DESCRIPTORS_DEFAULT_LOWERCASE[cargo.getCountType()]);
                vars[1] = cargo.getCurrentActivityOrCharge();
                String pattern = utility.retrieveDialogText(TENDER_RECONCILIATION_ERROR_ERRMSG_TAG,
                                                            DEF_PATTERN_TEXT);
                text = LocaleUtilities.formatComplexMessage(pattern,vars);
            }
            else
            {
                text = PosCountCargo.COUNT_TYPE_DESCRIPTORS[cargo.getCountType()].
                  toLowerCase(locale);
            }

            args[0] = text;
            args[1] = text;

            model.setResourceID(PosCountCargo.RECONCILIATION_ERROR);
            model.setType(DialogScreensIfc.CONFIRMATION);
            model.setArgs(args);
            
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }

    }
}
