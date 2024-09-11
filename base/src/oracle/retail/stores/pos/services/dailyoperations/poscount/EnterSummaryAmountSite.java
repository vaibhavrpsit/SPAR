/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/EnterSummaryAmountSite.java /main/14 2013/03/29 10:51:37 subrdey Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    subrdey   03/28/13 - Making proper text massage for Loan screen.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    tksharma  08/25/11 - Made cash Text to pick based on foreign currency
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    miparek   01/27/09 - changes to fix d#1812 Canadian Cash Pickup shows
 *                         unexpected non-mock characters in the prompt
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:05 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:28 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:55 PM  Robert Pearse
 *
 *   Revision 1.6  2004/07/22 00:06:33  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.5  2004/05/20 20:40:53  dcobb
 *   @scr 4204 Feature Enhancement: Till Options
 *   Removed alternate tender from Select Tender screen and
 *   corrected Select Charge screen.
 *
 *   Revision 1.4  2004/02/16 14:40:13  blj
 *   @scr 3838 - cleanup code
 *
 *   Revision 1.3  2004/02/12 16:49:38  mcs
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
 *    Rev 1.1   Feb 04 2004 18:39:18   DCobb
 * Added SUMMARY_COUNT_PICKUP and SUMMARY_COUNT_LOAN screens.
 * Resolution for 3381: Feature Enhancement:  Till Pickup and Loan
 *
 *    Rev 1.0   Aug 29 2003 15:56:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.5   Jun 23 2003 13:44:06   DCobb
 * Canadian Check Till Pickup
 * Resolution for POS SCR-2484: Canadian Check Till Pickup
 *
 *    Rev 1.4   Mar 04 2003 11:24:12   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.3   Dec 09 2002 15:10:44   DCobb
 * Fixed expected amounts for alternate currencies.
 * Resolution for POS SCR-1852: Multiple defects on Till Close Select Tenders screen funtionality.
 *
 *    Rev 1.2   Nov 27 2002 15:55:54   DCobb
 * Add Canadian Check tender.
 * Resolution for POS SCR-1842: POS 6.0 Canadian Check Tender
 *
 *    Rev 1.1   Aug 19 2002 14:32:20   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:30:52   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:14:28   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:27:10   msg
 * Initial revision.
 *
 *    Rev 1.2   08 Feb 2002 14:03:18   epd
 * Corrected UI messages
 * Resolution for POS SCR-726: Till pickup - 'Summary Count' screen is incorrect
 * Resolution for POS SCR-727: Till Loan - 'Summary Count' screen has incorrect text
 * Resolution for POS SCR-728: Till loan - 'Report Printing' screen, text is incorrect
 * Resolution for POS SCR-729: Till pickup - 'Report Printing' screen text is incorrect
 * Resolution for POS SCR-730: Till pickup - checks - 'Report Printing' screen text is incorrect
 *
 *    Rev 1.1   24 Jan 2002 13:32:56   epd
 * Updated to call new screen allowing negative values based on current operation
 * Resolution for POS SCR-159: When closing/counting till negative amounts are not accepted.
 *
 *    Rev 1.0   Sep 21 2001 11:17:18   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:11:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

import java.util.Locale;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;


//--------------------------------------------------------------------------
/**
     Displays the SUMMARY_COUNT screen.
     @version $Revision: /main/14 $
**/
//--------------------------------------------------------------------------
public class EnterSummaryAmountSite extends PosSiteActionAdapter
{
    /**
        revision number
    **/
    public static String revisionNumber = "$Revision: /main/14 $";
    /**
      default pattern text
    **/
    public static final String DEF_PATTERN_TEXT = "{0} {1}";
    /**
      Cash Lower  tag
    **/
    public static final String CASH_LOWER_TAG = "cashLower";
    /**
      Cash Lower  default text
    **/
    public static final String CASH_LOWER_TEXT = "cash";
    /**
      Current FLP Tender  tag
    **/
    public static final String CURRENT_FLP_TENDER_TAG = "CurrentFLPTender";
    /**
      Summary Count Prefix
    **/
    public static final String SUMMARY_COUNT_PREFIX = "SC_";

    //----------------------------------------------------------------------
    /**
        Display the SUMMARY_COUNT screen. The SUMMARY_NEGATIVE_COUNT screen
        is displayed instead for all noncash tenders when closing till.
        @param bus the bus arriving at this site
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        Locale locale=LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

        // Get the cargo
        PosCountCargo cargo = (PosCountCargo)bus.getCargo();
        String description;

        // Set up the model
        String tenderDescription = null;
        if (cargo.getCurrentActivity().equals(PosCountCargo.CHARGE))
        {
            tenderDescription = cargo.getCurrentCharge();
        }
        else
        {
            tenderDescription = cargo.getCurrentActivity();
        }
        String lookup = SUMMARY_COUNT_PREFIX + cargo.removeBlanks(tenderDescription);
        description = utility.retrieveText("SummaryCountSpec",
                                           BundleConstantsIfc.POSCOUNT_BUNDLE_NAME,
                                           lookup,
                                           lookup);

        // Negative values allowed for all noncash tenders when closing till
        boolean negativeAllowed = false;
        String cashLowerCase = utility.retrieveText(POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                                                    BundleConstantsIfc.DAILY_OPERATIONS_BUNDLE_NAME,
                                                    CASH_LOWER_TAG,
                                                    CASH_LOWER_TEXT);
        String foreignCurrency = cargo.getCurrentForeignCurrency();
        if (cargo.getCountType() == PosCountCargo.TILL && description.toLowerCase(locale).indexOf(cashLowerCase) == -1)
        {
            negativeAllowed = true;
        }

        if (cargo.getCountType() == PosCountCargo.PICKUP ||
            cargo.getCountType() == PosCountCargo.LOAN)
        {
            String[] vars = new String[2];
            if (!foreignCurrency.equals(PosCountCargo.NONE))
            {
                vars[0] = utility.retrieveText("SummaryCountSpec", BundleConstantsIfc.POSCOUNT_BUNDLE_NAME,
                        foreignCurrency, foreignCurrency);
            }
            else
            {
                lookup = SUMMARY_COUNT_PREFIX + cargo.removeBlanks(cargo.getCurrentFLPTender());
                vars[0] = utility.retrieveText("SummaryCountSpec", BundleConstantsIfc.POSCOUNT_BUNDLE_NAME, lookup,
                        lookup);
            }
            vars[1] = description;
            String pattern = utility.retrieveText(POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                                                  BundleConstantsIfc.DAILY_OPERATIONS_BUNDLE_NAME,
                                                  CURRENT_FLP_TENDER_TAG,
                                                  DEF_PATTERN_TEXT);
            description = LocaleUtilities.formatComplexMessage(pattern,vars);
        }

        /*
         * Ask the UI Manager to display the screen
         */
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        PromptAndResponseModel pandrModel = new PromptAndResponseModel();
        //Future watch: If canadian money, then might be different.
        pandrModel.setArguments(description);
        baseModel.setPromptAndResponseModel(pandrModel);
        if (negativeAllowed)
        {
            ui.showScreen(POSUIManagerIfc.SUMMARY_NEGATIVE_COUNT, baseModel);
        }
        else
        {
            ui.showScreen(POSUIManagerIfc.SUMMARY_COUNT, baseModel);
        }
    }
}
