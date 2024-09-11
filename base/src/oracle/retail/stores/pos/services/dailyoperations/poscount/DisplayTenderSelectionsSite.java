/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/DisplayTenderSelectionsSite.java /main/22 2013/12/12 11:04:19 bhsuthar Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    bhsuthar  12/11/13 - provided to include negative values also for all the
 *                         tender type except cash at CountreconcileSummary
 *                         page
 *    cgreene   10/25/13 - remove currency type deprecations and use currency
 *                         code instead of description
 *    abondala  09/04/13 - initialize collections
 *    jswan     01/04/11 - Fix issues found during re-test.
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    jswan     12/14/10 - Fix issues found during retest.
 *    jswan     12/10/10 - Fixed issue with BlindCount at Till Reconcile whith
 *                         tenders which are not in the tenders to be counted
 *                         list.
 *    jswan     10/20/10 - Fix an issue in the ARG summary report for a till
 *                         that has less money in it than the original float
 *                         amount.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    ohorne    03/19/10 - added creation of zero amount charge models for
 *                         blind close
 *    abondala  01/03/10 - update header date
 *    cgreene   12/24/09 - add check to card type to prevent gift cards from
 *                         being added to credit cards
 *    asinton   06/26/09 - Added back what is probably unused code but might be
 *                         risky to remove at this time.
 *    asinton   06/26/09 - Using hashmap to temporarily store desired button
 *                         states before setting them on the
 *                         NavigationButtonBeanModel. This is a fix for the
 *                         defect on NavigationButtonBeanModel where 2nd call
 *                         to setButtonEnabled is not honored.
 *
 * ===========================================================================
 * $Log:
 *     4    360Commerce 1.3         4/24/2007 1:16:09 PM   Charles D. Baker CR
 *          26556 - I18N Code Merge.
 *     3    360Commerce 1.2         3/31/2005 4:27:50 PM   Robert Pearse
 *     2    360Commerce 1.1         3/10/2005 10:21:06 AM  Robert Pearse
 *     1    360Commerce 1.0         2/11/2005 12:10:41 PM  Robert Pearse
 *    $
 *    Revision 1.6  2004/07/22 00:06:33  jdeleau
 *    @scr 3665 Standardize on I18N standards across all properties files.
 *    Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *    Revision 1.5  2004/06/07 18:29:38  dcobb
 *    @scr 4204 Feature Enhancement: Till Options
 *    Add foreign currency counts.
 *
 *    Revision 1.4  2004/05/20 20:40:53  dcobb
 *    @scr 4204 Feature Enhancement: Till Options
 *    Removed alternate tender from Select Tender screen and
 *    corrected Select Charge screen.
 *
 *    Revision 1.3  2004/02/12 16:49:38  mcs
 *    Forcing head revision
 *
 *    Revision 1.2  2004/02/11 21:45:40  rhafernik
 *    @scr 0 Log4J conversion and code cleanup
 *
 *    Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *    updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Oct 24 2003 11:06:50   sfl
 * Added code in extractCurrencyPart method to handle
 * the parameter value PosCountCargo.NONE.
 * Resolution for POS SCR-3416: System crashes while closing till and Check or Trav. Check Accepted = 'None'
 *
 *    Rev 1.0   Aug 29 2003 15:56:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.15   Jul 16 2003 17:21:24   DCobb
 * Initialize all buttons to disabled.
 * Resolution for POS SCR-2704: System is not Checking the Tender To Count At Till Reconcile
 *
 *    Rev 1.14   Jul 02 2003 10:24:46   RSachdeva
 * Calculating credit totals using individual credit card types
 * Resolution for POS SCR-2572: Tender Totals are incorrect.
 *
 *    Rev 1.13   Jul 01 2003 11:02:32   RSachdeva
 * Blind close and TendersToCountAtTillReconcile can be independently modified.
 * isFieldDisabled  should not control display for expected amounts
 * Resolution for POS SCR-2412: Check amount displayed during Till Reconcile when Blind Close = Yes
 * Resolution for POS SCR-2856: Check button disabled on Select Tender screen when amount displays for checks
 *
 *    Rev 1.12   Jun 18 2003 12:54:36   bwf
 * Remove default localeconstants.
 * Resolution for 2613: Internationalization: try to print till summary report, POS client hangs up.
 *
 *    Rev 1.10   May 13 2003 19:31:40   bwf
 * Check if currency has been changed and show it.
 * Resolution for 2432: Select Tender screen does not update when a different tender amount is entered, financials incorrect
 *
 *    Rev 1.9   Apr 28 2003 13:13:06   RSachdeva
 * For ChecksAccepted and TravelersChecksAccepted Parameter Values Currency Extracted
 * Resolution for POS SCR-2186: List of values for Checks Accepted references 'Dollars' instead of 'Checks'
 *
 *    Rev 1.8   Mar 04 2003 11:05:34   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.7   Dec 09 2002 15:10:44   DCobb
 * Fixed expected amounts for alternate currencies.
 * Resolution for POS SCR-1852: Multiple defects on Till Close Select Tenders screen funtionality.
 *
 *    Rev 1.6   Nov 27 2002 15:55:54   DCobb
 * Add Canadian Check tender.
 * Resolution for POS SCR-1842: POS 6.0 Canadian Check Tender
 *
 *    Rev 1.5   Nov 26 2002 17:40:02   kmorneau
 * fix blind close to properly display expected values in expected places
 * Resolution for 1824: Blind Close
 *
 *    Rev 1.4   Nov 18 2002 13:38:00   kmorneau
 * added capability to display expected amounts for Blind Close
 * Resolution for 1824: Blind Close
 *
 *    Rev 1.3   Sep 26 2002 13:28:52   DCobb
 * Replaced hard-coded indexes for alternate cash and trav ck with definitions from SummaryTenderMenuBeanModel.
 * Resolution for POS SCR-1799: POS 5.5 Purchase Order Tender Package
 *
 *    Rev 1.2   Sep 03 2002 16:03:38   baa
 * externalize domain  constants and parameter values
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 19 2002 14:26:40   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:30:26   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:14:24   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:27:08   msg
 * Initial revision.
 *
 *    Rev 1.4   04 Mar 2002 16:21:08   epd
 * Updates to accommodate use of TenderTypeMap class
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.3   21 Jan 2002 16:00:42   epd
 * optimizations
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.2   09 Jan 2002 10:29:38   epd
 * simplified some program logic
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.1   08 Jan 2002 16:05:30   epd
 * fixed bug in display screen
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   02 Jan 2002 15:42:24   epd
 * Initial revision.
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 21 2001 11:16:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:11:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.tender.tdo.AlternateCurrencyTenderOptionsTDO;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.Card;
import oracle.retail.stores.domain.utility.CardType;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.SummaryChargeMenuBeanModel;
import oracle.retail.stores.pos.ui.beans.SummaryCountBeanModel;
import oracle.retail.stores.pos.ui.beans.SummaryTenderMenuBeanModel;

/**
 * This class builds the list of Tender or Credit types that the Cashier must
 * count.
 *
 * @version $Revision: /main/22 $
 */
public class DisplayTenderSelectionsSite extends PosSiteActionAdapter
{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 8406794249341969093L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/22 $";

    /**
     * Site name for logging
     */
    public static final String SITENAME = "DisplayTenderSelectionsSite";

    /**
     * default pattern text
     */
    public static final String DEF_PATTERN_TEXT = "{0} {1}";

    /**
     * Description tag
     */
    public static final String DESCRIPTION_TAG = "Description";

    /**
     * Cash Msg tag
     */
    public static final String CASH_MSG_TAG = "CashMsg";

    /**
     * Cash tag and default text
     */
    public static final String CASH_TAG = "Cash";

    /**
     * Tvl Ck tag
     */
    public static final String TVL_CK_TAG = "TvlCk";

    /**
     * Tvl Ck default text
     */
    public static final String TVL_CK_TEXT = "Tvl Ck";

    /**
     * Tvl Ck Msg tag
     */
    public static final String TVL_CK_MSG_TAG = "TvlCkMsg";

    /**
     * Check Msg tag
     */
    public static final String CHECK_MSG_TAG = "CheckMsg";

    /**
     * Check tag and default text
     */
    public static final String CHECK_TAG = "Check";

    /**
     * Prefix for the currency description tag used to look up text for the
     * prompt arg
     */
    public static final String SELECT_TENDER_PREFIX = "Select";

    
    public static final String TRAVEL_CHECK_TAG = "TravelCheck";

    /**
     * This method deterines whether to build the list of Tender or Credit types
     * and calls the UI to display the screen.
     *
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // Get the cargo
        PosCountCargo cargo   = (PosCountCargo)bus.getCargo();

        /*
         * Ask the UI Manager to display the screen
         */
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        SummaryTenderMenuBeanModel stmbm = new SummaryTenderMenuBeanModel();
        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();
        /*
         * Because of a defect in NavigationButtonBeanModel the most recent call
         * to setButtonEnabled(String,boolean) is not honored I am setting the
         * boolean values in a map to be used to set the enbale state for each
         * button near the end of this method.
         */
        Map<String, Boolean> enableButtonMap = new HashMap<String, Boolean>(0);
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        // Get the SummaryCountBeanModel from the cargo ONLY IF we haven't already set it (i.e. This is
        // the first time we are viewing this screen).  Otherwise, we get it from the SummaryTenderMenuBeanModel.
        SummaryCountBeanModel sc[] = null;
        if (cargo.isTenderCountModelAssigned())
        {
            sc = cargo.getTenderModels();
        }
        else
        {
            sc = stmbm.getSummaryCountBeanModel();
        }

        String[] countTenders  = null;
        String[] cashAccepted = null;
        String[] travelersChecksAccepted= null;
        String[] checksAccepted = null;
        try
        {
            // get parameter manager
            ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
            countTenders  = pm.getStringValues("TendersToCountAtTillReconcile");

            // determines whether alternate tenders accepted
            cashAccepted = pm.getStringValues("CashAccepted");
            travelersChecksAccepted = pm.getStringValues("TravelersChecksAccepted");
            travelersChecksAccepted = extractCurrencyPart(travelersChecksAccepted);
            checksAccepted = pm.getStringValues("ChecksAccepted");
            checksAccepted = extractCurrencyPart(checksAccepted);
        }
        catch (ParameterException pe)
        {
            logger.error("The Till Reconcile tenders to count could not be retrieved.", pe);

            // assign common defaults
            countTenders = new String[3];
            countTenders[0] = CASH_TAG;
            countTenders[1] = CHECK_TAG;
            countTenders[2] = TRAVEL_CHECK_TAG;

            cashAccepted    = new String[1];
            cashAccepted[0] = DomainGateway.getBaseCurrencyType().getCurrencyCode(); // default value

            travelersChecksAccepted    = new String[1];
            travelersChecksAccepted[0] = DomainGateway.getBaseCurrencyType().getCurrencyCode(); // default value

            checksAccepted    = new String[1];
            checksAccepted[0] = DomainGateway.getBaseCurrencyType().getCurrencyCode(); // default value
        }

        boolean cashEnabled = false;
        boolean checkEnabled = false;
        boolean travCheckEnabled = false;

        // Initialize all buttons to disabled
        for (int j=0; j<sc.length; j++)
        {
            enableButtonMap.put(sc[j].getActionName(), false);
        }

        for (int i=0; i<countTenders.length; i++)
        {
            enableButtonMap.put(countTenders[i], true);
            for (int j=0; j<sc.length; j++)
            {
                if (sc[j].getActionName().equals(countTenders[i]))
                {
                    sc[j].setFieldDisabled(false);
                    break; // break the inner for loop
                }
            }
            if (countTenders[i].equals(CASH_TAG))
            {
                cashEnabled = true;
            }
            else if (countTenders[i].equals(CHECK_TAG))
            {
                checkEnabled = true;
            }
            else if (countTenders[i].equals(TRAVEL_CHECK_TAG))
            {
                travCheckEnabled = true;
            }
        }

        // get blind close parameter value and set bean model flag
        String[] blindCloseParam = cargo.getParameterStringValues(bus, "BlindClose");
        if (blindCloseParam == null)
        {
            blindCloseParam = new String[1];
        }
        if (blindCloseParam[0] == null)
        {
            blindCloseParam[0] = new String("Y");
        }
        boolean blindClose = blindCloseParam[0].equalsIgnoreCase("Y");

        // set expected amounts
        stmbm.setBlindClose(blindClose);
        for (int i=0; i<sc.length; i++)
        {
            // set expected amount
            String scDescription = sc[i].getDescription();
            if (scDescription.equals(TenderTypeEnum.CREDIT.toString()))
            {
                //calculating credit totals using individual credit card types
                CardType cardType = utility.getConfiguredCardTypeInstance();
                List<Card> cardList = cardType.getCardList();
                Iterator<Card> it = cardList.iterator();
                CurrencyIfc expectedCreditTotal = null;
                while (it.hasNext())
                {
                    Card currentCard = it.next();
                    if (TenderTypeEnum.CREDIT.toString().equals(currentCard.getCardType()))
                    {
                        CurrencyIfc expectedCurrent = cargo.getExpectedAmount(currentCard.getCardName(), sc[i].getAmount().getCountryCode());
                        if (expectedCreditTotal == null)
                        {
                            expectedCreditTotal = expectedCurrent;
                        }
                        else
                        {
                            expectedCreditTotal = expectedCreditTotal.add(expectedCurrent);
                        }
                    }
                }
                sc[i].setExpectedAmount(expectedCreditTotal);
            }
            else
            {
                sc[i].setExpectedAmount(cargo.getExpectedAmount(sc[i].getDescription(), sc[i].getAmount().getCountryCode()));
            }

            if (blindClose)
            {
                sc[i].setExpectedAmountHidden(true);
            }
            else
            {
                sc[i].setExpectedAmountHidden(false);
                //Check if TenderType is CASH and the expectedAmount is negative, if so then show a 0 for the negative value.
                if(scDescription.equals(TenderTypeEnum.CASH.toString()))
                {
                	if(cargo.getCurrentActivity().equals(PosCountCargo.NONE) && sc[i].getExpectedAmount().signum() > CurrencyIfc.ZERO)
                	{
                		sc[i].setAmount(sc[i].getExpectedAmount()); // set all expected amounts
                	
                	}
                }
                else if(cargo.getCurrentActivity().equals(PosCountCargo.NONE))
                {
                    sc[i].setAmount(sc[i].getExpectedAmount()); // set all expected amounts
                }
            }
        }

        String baseCurrency = DomainGateway.getBaseCurrencyInstance().getCurrencyCode();

        // Enable the Cash button if the base currency is listed in the Cash currencies
        // and Cash is in the TendersToCountAtTillReconcile list
        enableButtonMap.put(CASH_TAG, cashEnabled && isCurrencyListed(baseCurrency,cashAccepted));

        // Enable the Travel Check button if the base currency is listed in the Traveler's Check currencies
        // and TravelCheck is in the TendersToCountAtTillReconcile list
        enableButtonMap.put(TRAVEL_CHECK_TAG, travCheckEnabled && isCurrencyListed(baseCurrency,travelersChecksAccepted));

        // Enable the Check button if the base currency is listed in the Check currencies
        // and Check is in the TendersToCountAtTillReconcile list
        enableButtonMap.put(CHECK_TAG, checkEnabled && isCurrencyListed(baseCurrency,checksAccepted));

        // set tender model in cargo
        // Note:  Usually this list is generated by cargo based on tenders in financial totals, but
        // we have a static tender list, so are supplying it instead for this case.
        cargo.setTenderModels(sc);
        cargo.setTenderCountModelAssigned(true);

        // initialize with zero amount charge models if blind close
        if (!cargo.isChargeCountModelAssigned() && blindClose && PosCountCargo.NONE.equals(cargo.getCurrentActivity()))
        {
            //this is done in case user fails to enter tender count/amount.  In a blind close
            //the user is expected to provide the amounts, otherwise the zero value created
            //here is used as the count/amount.
            Dispatcher d = Dispatcher.getDispatcher();
            UtilityManager util = (UtilityManager) d.getManager(UtilityManagerIfc.TYPE);
            CardType cardType = util.getConfiguredCardTypeInstance();
            SummaryChargeMenuBeanModel scmbm = new SummaryChargeMenuBeanModel(cardType.getCardList());
            cargo.setChargeModels(scmbm.getSummaryCountBeanModel());
            cargo.setChargeCountModelAssigned(true);
        }

        // Set the enable state in the navModel for each key (button name) in the enableButtonMap
        for (Iterator<String> keys = enableButtonMap.keySet().iterator(); keys.hasNext();)
        {
            String key = keys.next();
            navModel.setButtonEnabled(key, enableButtonMap.get(key));            
        }
        stmbm.setLocalButtonBeanModel(navModel);
        stmbm.setSummaryCountBeanModel(sc);

        // Set the type of currency being counted in the prompt argument
        String lookup = SELECT_TENDER_PREFIX + DomainGateway.getBaseCurrencyType().getCurrencyCode();
        String description = utility.retrieveText("SelectTenderSpec",
                BundleConstantsIfc.TILL_BUNDLE_NAME,
                lookup,
                lookup);
        PromptAndResponseModel pandrModel = new PromptAndResponseModel();
        pandrModel.setArguments(description);
        stmbm.setPromptAndResponseModel(pandrModel);

        ui.showScreen(POSUIManagerIfc.SELECT_TENDER_TO_COUNT, stmbm);

    }

    /**
     * Determines if the specified currency is listed in the currencies
     * accepted.
     *
     * @param currency The currency to look for
     * @param list The list of currencies accepted
     * @return true if the specified currency is listed, false otherwise.
     */
    public boolean isCurrencyListed(String currency, Object[] list)
    {
        boolean enable = false;
        if ( list != null)
        {
            for (int i=0; i < list.length; i++)
            {
                if (currency.equals(list[i]))
                {
                   enable = true;
                   break;
                }
            }
        }
        return enable;
    }

    /**
     * Extracts Currency Part from the Parameter Values for ChecksAccepted and
     * TravelersChecksAccepted
     *
     * @param list Array of Parameter Values
     * @return An array of Parameter Values with currency part extracted
     */
    public String[] extractCurrencyPart(String[] list)
    {
        String[] currencyChecks = new String[0];
        if (list != null)
        {
            currencyChecks = new String[list.length];
            for (int i=0; i < list.length; i++)
            {
                String value = list[i];
                if (value.compareTo(PosCountCargo.NONE) != 0)
                {
                    int chkIndex = value.indexOf("CHK");
                    String extractedCurrency = value.substring(0, chkIndex);
                    currencyChecks[i] = extractedCurrency;
                }
                else
                {
                    currencyChecks[i] = PosCountCargo.NONE;
                }
            }
        }
        return currencyChecks;
    }
}
