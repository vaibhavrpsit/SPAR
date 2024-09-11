/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/DisplayForeignTenderSelectionsSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:23 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     12/14/10 - Fix issues found during retest.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    mchellap  03/30/09 - Code review comments
 *    mchellap  03/26/09 - Fixed foreign currency nationality retrieval
 *
 * ===========================================================================
 * $Log:
 *     3    360Commerce 1.2         3/31/2005 4:27:48 PM   Robert Pearse
 *     2    360Commerce 1.1         3/10/2005 10:21:03 AM  Robert Pearse
 *     1    360Commerce 1.0         2/11/2005 12:10:38 PM  Robert Pearse
 *    $
 *    Revision 1.3  2004/07/22 00:06:33  jdeleau
 *    @scr 3665 Standardize on I18N standards across all properties files.
 *    Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *    Revision 1.2  2004/06/10 16:24:02  dcobb
 *    @scr 4204 Feature Enhancement: Till Options
 *    Add foreign currency count.
 *
 *    Revision 1.1  2004/06/07 18:29:38  dcobb
 *    @scr 4204 Feature Enhancement: Till Options
 *    Add foreign currency counts.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

import java.util.Vector;

import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.utility.CountryCodeMap;
import oracle.retail.stores.foundation.manager.gui.ButtonSpec;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.SummaryCountBeanModel;
import oracle.retail.stores.pos.ui.beans.SummaryForeignTenderMenuBeanModel;

//------------------------------------------------------------------------------
/**
     This class builds the list of Foreign Tender types that the Cashier must
     count.<P>

     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class DisplayForeignTenderSelectionsSite extends PosSiteActionAdapter
{

    /**
       revision number
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       Tag for Nationality Descriptor
    **/
    public static final String NATIONALITY_TAG = "_Nationality";
    /**
      default pattern text
    **/
    public static final String DEF_PATTERN_TEXT = "{0} {1}";
    /**
      Description  tag
    **/
    public static final String DESCRIPTION_TAG = "Description";
    /**
      Cash Msg  tag
    **/
    public static final String CASH_MSG_TAG = "CashMsg";
    /**
      Cash tag and default text
    **/
    public static final String CASH_TAG = "Cash";
    /**
      Tvl Ck tag
    **/
    public static final String TVL_CK_TAG = "TvlCk";
    /**
      Tvl Ck default text
    **/
    public static final String TVL_CK_TEXT = "Tvl Ck";
    /**
      Tvl Ck Msg tag
    **/
    public static final String TVL_CK_MSG_TAG = "TvlCkMsg";
    /**
      Check Msg tag
    **/
    public static final String CHECK_MSG_TAG = "CheckMsg";
    /**
      Check tag and default text
    **/
    public static final String CHECK_TAG = "Check";
    /**
      Prefix for the currency description tag used to look up text for the prompt arg
    **/
    public static final String SELECT_TENDER_PREFIX = "Select";
    //--------------------------------------------------------------------------
    /**
       This method builds the list of Foreign Tender
       types and calls the UI to display the screen.<p>

       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Get the cargo
        PosCountCargo cargo   = (PosCountCargo)bus.getCargo();

        /*
         * Ask the UI Manager to display the screen
         */
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        String currentForeignCurrency = cargo.getCurrentForeignCurrency();
        SummaryForeignTenderMenuBeanModel sftmbm = new SummaryForeignTenderMenuBeanModel();

        // Get the SummaryCountBeanModel from the cargo ONLY IF we haven't already set it (i.e. This is
        // the first time we are viewing this screen).  Otherwise, we get it from the SummaryTenderMenuBeanModel.
        NavigationButtonBeanModel navModel = null;
        SummaryCountBeanModel sc[] = null;
        if (cargo.isForeignTenderCountModelAssigned())
        {
            sc = cargo.getForeignTenderModels();
            navModel = cargo.getForeignTenderLocalNavigationModel();
            sftmbm.setLocalButtonBeanModel(navModel);
            String countTenders[] = cargo.getForeignTendersToCount();
            sftmbm.setTendersToCount(countTenders);
            String tendersAccepted[] = cargo.getForeignTendersAccepted();
            sftmbm.setTendersAccepted(tendersAccepted);
        }
        else
        {
            cargo.setCurrentActivity(PosCountCargo.NONE);
            sc = sftmbm.getSummaryCountBeanModel();

            // loop through all screen tenders and set amount to expected if field disabled
            FinancialCountIfc fc = cargo.getFinancialTotals()
                                        .getCombinedCount()
                                        .getExpected();

            // get blind close parameter value and set bean model flag
            boolean blindClose = cargo.getBlindCloseParameterValue(bus);

            // set expected amounts
            sftmbm.setBlindClose(blindClose);
            for (int i=0; i<sc.length; i++)
            {
                // set expected amount
                String nationality = CountryCodeMap.getCountryDescriptor(cargo.getCountryCodeForForeignCurrency(currentForeignCurrency));
                StringBuffer scDescription = new StringBuffer().append(nationality).append(" ").append(sc[i].getDescription());

                sc[i].setExpectedAmount(cargo.getExpectedAmount(scDescription.toString(), sc[i].getAmount().getCountryCode()));

                if (blindClose)
                {
                    sc[i].setExpectedAmountHidden(true);
                }
                else
                {
                    sc[i].setExpectedAmountHidden(false);
                    if(cargo.getCurrentActivity().equals("None")) // we have not had anything counted yet
                    {
                        sc[i].setAmount(sc[i].getExpectedAmount()); // set all expected amounts
                    }
                }
            }
            // set tender model in cargo
            // Note:  Usually this list is generated by cargo based on tenders in financial totals, but
            // we have a static tender list, so are supplying it instead for this case.
            cargo.setForeignTenderModels(sc);
            cargo.setTenderModels(sc);
            cargo.setForeignTenderCountModelAssigned(true);

            // get the local navigation button bean model
            ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
            // Initialize list of tenders to count
            String[] countTenders  = null;
            try
            {
                countTenders  = pm.getStringValues("TendersToCountAtTillReconcile");
                countTenders = filterForeignCurrencyTenders(currentForeignCurrency, countTenders);
            }
            catch (ParameterException pe)
            {
                // assign common defaults according to the current foreign currency
                if (cargo.getForeignCurrencyFinancialCountTenderTotals()[0].equals(currentForeignCurrency))
                {
                    countTenders = new String[5];
                    countTenders[0] = "Cash";
                    countTenders[1] = "Check";
                    countTenders[2] = "TravelCheck";
                    countTenders[3] = "GiftCert";
                    countTenders[4] = "StoreCredit";
                }
                else
                {
                    countTenders = new String[2];
                    countTenders[0] = "GiftCert";
                    countTenders[1] = "StoreCredit";
                }
            }
            sftmbm.setTendersToCount(countTenders);
            cargo.setForeignTendersToCount(countTenders);
            navModel = getLocalNavigationButtonBeanModel(currentForeignCurrency, countTenders, pm, cargo);
            sftmbm.setLocalButtonBeanModel(navModel);
            cargo.setForeignTenderLocalNavigationModel(navModel);

            // Build the list of tenders accepted
            ButtonSpec[] button = navModel.getNewButtons();
            String tendersAccepted[] = null;
            if (button != null)
            {
                tendersAccepted = new String[button.length];
                for (int i = 0; i < button.length; i++)
                {
                    tendersAccepted[i] = button[i].getActionName();
                }
            }
            sftmbm.setTendersAccepted(tendersAccepted);
            cargo.setForeignTendersAccepted(tendersAccepted);

        }  // end if SummaryCountBeanModel has not been initialized in the cargo

        sftmbm.setSummaryCountBeanModel(sc);

        // Set the type of currency being counted in the prompt argument
        String lookup = SELECT_TENDER_PREFIX + cargo.getCurrentForeignCurrency();
        String description = utility.retrieveText("SelectTenderSpec",
                BundleConstantsIfc.TILL_BUNDLE_NAME,
                lookup,
                lookup);
        PromptAndResponseModel pandrModel = new PromptAndResponseModel();
        pandrModel.setArguments(description);
        sftmbm.setPromptAndResponseModel(pandrModel);

        ui.showScreen(POSUIManagerIfc.SELECT_FOREIGN_TENDER_TO_COUNT, sftmbm);
    }

    //----------------------------------------------------------------------
    /**
       Returns the currencies accepted for the specified foreign currency.
       <P>
       @param currency      The current foreign currency
       @param countTenders  The tenders to count at till reconcile
       @return  String[]    The tenders to count for the current foreign currency
    **/
    //----------------------------------------------------------------------
    private String[] filterForeignCurrencyTenders(String currency, String[] list)
    {
        String [] returnValue = null;
        Vector tenders = new Vector();
        int count = 0;

        if (list != null)
        {

            for (int i = 0; i < list.length; i++)
            {
                if (list[i].startsWith(currency))
                {
                    tenders.addElement(list[i].substring(currency.length()));
                    count++;
                }
            }
        }
        returnValue = new String[count];
        tenders.copyInto(returnValue);

        return returnValue;
    }

    //----------------------------------------------------------------------
    /**
       Determines if the specified value is listed in the list of values.
       <P>
       @param value     The value to look for
       @param list      The list to search for the given value
       @return true if the specified value is listed, false otherwise.
    **/
    //----------------------------------------------------------------------
    public boolean isValueListed(String value, Object[] list)
    {
        boolean enable = false;
        if ( list != null)
        {
            for (int i=0; i < list.length; i++)
            {
                if (value.equals(list[i]))
                {
                   enable = true;
                   break;
                }
            }
        }
        return enable;
    }

    //----------------------------------------------------------------------
    /**
        Extracts Currency Part from the Parameter Values for ChecksAccepted
        and TravelersChecksAccepted<P>
        @param  list  Array of Parameter Values
        @return An array of Parameter Values with currency part extracted
    **/
    //----------------------------------------------------------------------
    public String[] extractCurrencyPart(String[] list)
    {
        String[] currencyChecks;
        if (list != null)
        {
            currencyChecks = new String[list.length];
            for (int i=0; i < list.length; i++)
            {
                String value = list[i];
                if (value.compareTo("None") != 0)
                {
                    int chkIndex = value.indexOf("CHK");
                    String extractedCurrency = value.substring(0, chkIndex);
                    currencyChecks[i] = extractedCurrency;
                }
                else
                {
                    currencyChecks[i] = "None";
                }
            }
        }
        else
        {
            currencyChecks = new String[0];
        }
        return currencyChecks;
    }

    //----------------------------------------------------------------------
    /**
        Creates the local navigation button bean model according to the
        tenders accepted and enables or disables the buttons according to
        the tenders to count.<P>
        @param  currentForeignCurrency  The current foreign currency descriptor
        @param  countTenders            The list of tenders to count
        @param  pm                      The parameter manager
        @param  cargo                   The PosCountCargo
        @return The local navigation button bean model.
    **/
    //----------------------------------------------------------------------
    protected NavigationButtonBeanModel getLocalNavigationButtonBeanModel(String currentForeignCurrency,
                                                                          String[] countTenders,
                                                                          ParameterManagerIfc pm,
                                                                          PosCountCargo cargo)
    {
        // Initialize lists of tenders accepted
        String[] cashAccepted = null;
        String[] travelersChecksAccepted= null;
        String[] checksAccepted = null;
        String[] giftCertificatesAccepted  = null;
        String[] storeCreditsAccepted  = null;
        try
        {
            // determines whether tenders are accepted
            cashAccepted = pm.getStringValues("CashAccepted");
            travelersChecksAccepted = pm.getStringValues("TravelersChecksAccepted");
            travelersChecksAccepted = extractCurrencyPart(travelersChecksAccepted);
            checksAccepted = pm.getStringValues("ChecksAccepted");
            checksAccepted = extractCurrencyPart(checksAccepted);
            giftCertificatesAccepted = pm.getStringValues("GiftCertificatesAccepted");
            storeCreditsAccepted = pm.getStringValues("StoreCreditsAccepted");
        }
        catch (ParameterException pe)
        {
            logger.error(
                    "" + "The Till Reconcile tenders to count could not be retrieved" + " " + "from the ParameterManager.  The following exception occurred: " + " " + pe.getMessage() + "");

            // assign common defaults according to the current foreign currency
            if (cargo.getForeignCurrencyFinancialCountTenderTotals()[0].equals(currentForeignCurrency))
            {
                String CanadianCHK = PosCountCargo.CAD + "CHK";

                cashAccepted                = new String[1];
                cashAccepted[0]             = currentForeignCurrency; // default value

                travelersChecksAccepted     = new String[1];
                travelersChecksAccepted[0]  = CanadianCHK; // default value

                checksAccepted              = new String[1];
                checksAccepted[0]           = CanadianCHK; // default value

                giftCertificatesAccepted    = new String[1]; // default value
                giftCertificatesAccepted[0] = currentForeignCurrency;

                storeCreditsAccepted        = new String[1]; // default value
                storeCreditsAccepted[0]     = currentForeignCurrency;
            }
            else
            {
                cashAccepted                = new String[0];
                travelersChecksAccepted     = new String[0];
                checksAccepted              = new String[0];

                giftCertificatesAccepted    = new String[1]; // default value
                giftCertificatesAccepted[0] = currentForeignCurrency;

                storeCreditsAccepted        = new String[1]; // default value
                storeCreditsAccepted[0]     = currentForeignCurrency;
            }
        }
        // Create buttons for foreign tenders according to tenders accepted.
        // Enable according to tenders to count
        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();
        int i = 0;
        if (isValueListed(currentForeignCurrency, cashAccepted))
        {
            StringBuffer keyName = new StringBuffer("F");
            keyName.append(i+2);
            boolean enabled = isValueListed("Cash", countTenders);
            navModel.addButton("Cash", "Cash", "Cash", enabled, keyName.toString());
            i++;
        }
        if (isValueListed(currentForeignCurrency, checksAccepted))
        {
            StringBuffer keyName = new StringBuffer("F");
            keyName.append(i+2);
            boolean enabled = isValueListed("Check", countTenders);
            navModel.addButton("Check", "Check", "Check", enabled, keyName.toString());
            i++;
        }
        if (isValueListed(currentForeignCurrency, travelersChecksAccepted))
        {
            StringBuffer keyName = new StringBuffer("F");
            keyName.append(i+2);
            boolean enabled = isValueListed("TravelCheck", countTenders);
            navModel.addButton("TravCheck", "Travel Check", "TravelersCheck", enabled, keyName.toString());
            i++;
        }
        if (isValueListed(currentForeignCurrency, giftCertificatesAccepted))
        {
            StringBuffer keyName = new StringBuffer("F");
            keyName.append(i+2);
            boolean enabled = isValueListed("GiftCert", countTenders);
            navModel.addButton("GiftCert", "Gift Cert.", "GiftCertificate", enabled, keyName.toString());
            i++;
        }
        if (isValueListed(currentForeignCurrency, storeCreditsAccepted))
        {
            StringBuffer keyName = new StringBuffer("F");
            keyName.append(i+2);
            boolean enabled = isValueListed("StoreCredit", countTenders);
            navModel.addButton("StoreCredit", "Store Credit", "StoreCredit", enabled, keyName.toString());
            i++;
        }

        return navModel;
    }
}
