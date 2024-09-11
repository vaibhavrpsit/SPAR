/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.1	Prateek		28/June/2013	Changes done for BUG 6265
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.dailyoperations.poscount;

import max.retail.stores.pos.ui.beans.MAXSummaryTenderMenuBeanModel;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.dailyoperations.poscount.PosCountCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.SummaryCountBeanModel;

//------------------------------------------------------------------------------
/**
     This class builds the list of Tender or Credit types that the Cashier must
     count.<P>

     @version $Revision: 4$
**/
//------------------------------------------------------------------------------

public class MAXDisplayTenderSelectionsSite extends PosSiteActionAdapter
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
       revision number
    **/
    public static String revisionNumber = "$Revision: 4$";
    /**
       Site name for logging
    **/
    public static final String SITENAME = "DisplayTenderSelectionsSite";
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
       This method deterines whether to build the list of Tender or Credit
       types and calls the UI to display the screen.<p>

       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Get the cargo
        MAXPosCountCargo cargo   = (MAXPosCountCargo)bus.getCargo();

        /*
         * Ask the UI Manager to display the screen
         */
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        MAXSummaryTenderMenuBeanModel stmbm = new MAXSummaryTenderMenuBeanModel();
        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();
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
//            travelersChecksAccepted = pm.getStringValues("TravelersChecksAccepted");
//            travelersChecksAccepted = extractCurrencyPart(travelersChecksAccepted);
            checksAccepted = pm.getStringValues("ChecksAccepted");
            checksAccepted = extractCurrencyPart(checksAccepted);
        }
        catch (ParameterException pe)
        {
            logger.error(
                         "" + "The Till Reconcile tenders to count could not be retrieved" + " " + "from the ParameterManager.  The following exception occurred: " + " " + pe.getMessage() + "");

            // assign common defaults
            countTenders = new String[3];
            countTenders[0] = "Cash";
            countTenders[1] = "Check";
          //  countTenders[2] = "TravelCheck";

            cashAccepted            = new String[1];
            cashAccepted[0]         = DomainGateway.getBaseCurrencyType().getCurrencyCode(); // default value

//            travelersChecksAccepted = new String[1];
//            travelersChecksAccepted[0] = DomainGateway.getBaseCurrencyType().getCurrencyCode(); // default value

            checksAccepted          = new String[1];
            checksAccepted[0]       = DomainGateway.getBaseCurrencyType().getCurrencyCode(); // default value
        }

        boolean cashEnabled = false;
        boolean checkEnabled = false;
        boolean loyaltyPointsEnabled = false;
        boolean paytmTenderEnabled = false;
        boolean mobikwikTenderEnabled = false;

        TenderTypeMapIfc tenderMap = DomainGateway.getFactory().getTenderTypeMapInstance();
        
        // Initialize all buttons to disabled
        for (int j=0; j<sc.length; j++)
        {
            navModel.setButtonEnabled(sc[j].getActionName(), false);
        }

        for (int i=0; i<countTenders.length; i++)
        {
            navModel.setButtonEnabled(countTenders[i], true);
            for (int j=0; j<sc.length; j++)
            {
                if (sc[j].getActionName().equals(countTenders[i]))
                {
                    sc[j].setFieldDisabled(false);
                    break; // break the inner for loop
                }
            }
            if (countTenders[i].equals("Cash"))
            {
                cashEnabled = true;
            }
            else if (countTenders[i].equals("Check"))
            {
                checkEnabled = true;
            }
            else if (countTenders[i].equals("LoyaltyPoints"))
            {
            	loyaltyPointsEnabled = true;
            }else if(countTenders[i].equals("Paytm"))  // added by atul
            {
            	paytmTenderEnabled=true;
            }else if(countTenders[i].equals("Mobikwik"))  // added by atul
            {
            	mobikwikTenderEnabled=true;
            }
        }

        // loop through all screen tenders and set amount to expected if field disabled
        FinancialCountTenderItemIfc[] tenderItems = cargo.getFinancialTotals()
                                                         .getCombinedCount()
                                                         .getExpected()
                                                         .getTenderItems();

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
        try
        {
        	int key = 0;
	        for (int i=0; i<sc.length; i++)
	        {
	            // set expected amount
	            String scDescription = sc[i].getDescription();
	            if (scDescription.equals("Credit"))
	            {
	            	ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
	            	String[] bankname =pm.getStringValues("CreditDebitOfflineBank");
	                //calculating credit totals using individual credit card types
//	                CardType cardType = utility.getConfiguredCardTypeInstance();
//	                List cardList = cardType.getCardList();
//	                Iterator it = cardList.iterator();
	                CurrencyIfc expectedCreditTotal = null;
	                while (key<bankname.length)
	                {
            CurrencyIfc expectedCurrent = cargo.getEnteredAmount(bankname[(key++)]);
	                    if (expectedCreditTotal == null)
	                    {
	                        expectedCreditTotal = expectedCurrent;
	                    }
	                    else
	                    { 
	                        expectedCreditTotal = expectedCreditTotal.add(expectedCurrent);
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
	                if(cargo.getCurrentActivityOrCharge().equals(PosCountCargo.NONE) && !cargo.getCurrentCharge().equals(PosCountCargo.CHARGE)) // we have not had anything counted yet
	                {
	                    sc[i].setAmount(sc[i].getExpectedAmount()); // set all expected amounts
	                }	
	            }
	        }
        }
        catch(ParameterException e)
        {
        	logger.error(e);
        }
        String              baseCurrency = DomainGateway.getBaseCurrencyInstance().getDescription();
        
        // Enable the Cash button if the base currency is listed in the Cash currencies        
        // and Cash is in the TendersToCountAtTillReconcile list
        navModel.setButtonEnabled("Cash", cashEnabled && isCurrencyListed(baseCurrency,cashAccepted));

        // Enable the Travel Check button if the base currency is listed in the Traveler's Check currencies
        // and TravelCheck is in the TendersToCountAtTillReconcile list
      //  navModel.setButtonEnabled("TravelCheck", travCheckEnabled && isCurrencyListed(baseCurrency,travelersChecksAccepted));
        
        // Enable the Check button if the base currency is listed in the Check currencies
        // and Check is in the TendersToCountAtTillReconcile list
        navModel.setButtonEnabled("Check", checkEnabled && isCurrencyListed(baseCurrency,checksAccepted));
        navModel.setButtonEnabled("LoyaltyPoints", loyaltyPointsEnabled);  
// added by atul shukla for paytm reconcillation
        
        navModel.setButtonEnabled("Paytm", paytmTenderEnabled);  
        navModel.setButtonEnabled("Mobikwik", mobikwikTenderEnabled);  
        // set tender model in cargo
        // Note:  Usually this list is generated by cargo based on tenders in financial totals, but
        // we have a static tender list, so are supplying it instead for this case.
        cargo.setTenderModels(sc);
        cargo.setTenderCountModelAssigned(true);
        
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
    //----------------------------------------------------------------------
    /**
       Determines if the specified currency is listed in the currencies 
       accepted.
       <P>
       @param currency  The currency to look for
       @param list      The list of currencies accepted
       @return true if the specified currency is listed, false otherwise.
    **/
    //----------------------------------------------------------------------
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
        String[] currencyChecks = new String[list.length];
        if (list != null)
        {
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
        return currencyChecks;
    }
}
