/* ===========================================================================
* Copyright (c) 2007, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/foreigncurrency/ForeignCurrencyCargo.java /main/9 2014/02/10 12:20:18 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   02/10/14 - Externalize the number of supported alternate
 *                         currencies
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  2    360Commerce 1.1         6/21/2007 1:03:30 PM   Charles D. Baker CR
 *       27280 - Updated to limit number of alternate currencies to the number
 *        of letters supported in the tourscript.
 *  1    360Commerce 1.0         6/21/2007 12:53:14 PM  Charles D. Baker CR
 *       27280 - Added to remove dependency of country codes to exist in
 *       tourscript when tendering with alternate currencies.
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.foreigncurrency;

import java.io.Serializable;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.tour.gate.Gateway;

//--------------------------------------------------------------------------
/**
    Carries the financial data used through much of the
    application.  Extending the TenderCargo, the ForeignCurrencyCargo
    adds functionality for mapping country codes to button names so that
    alternate tenders can be treated without modifications to the tour script.

    @see oracle.retail.stores.pos.services.tender.TenderCargo
    @version $Revision: /main/9 $
**/
//--------------------------------------------------------------------------
public class ForeignCurrencyCargo extends TenderCargo implements Serializable
{
    /**
     * revision number supplied by source-code-control system
     */
    public static String revisionNumber = "$Revision: /main/9 $";

    /**
     * The logger to which log messages will be sent.
     */
    protected static transient Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.foreigncurrency.ForeignCurrencyCargo.class);

    /**
     * Application properties group
     */
    private static final String APPLICATION_PROPERTY_GROUP_NAME = "application";
    
    /**
     * Mapping from country code to button action
     */
    protected Hashtable countryCodeToButtonActionMapping = null;

    /**
     * Mapping from button action to country code
     */
    protected Hashtable buttonActionToCountryCodeMapping = null;
        
    //--------------------------------------------------------------------------
    /**
        Initial set up of mappings between country codes and button actions (aka letters).
    **/
    //--------------------------------------------------------------------------
    public void initializeCurrencyActions()
    {
        // get list of countries
        CurrencyTypeIfc[] countries = DomainGateway.getAlternateCurrencyTypes();
        
        int maximumLetterMatches;
        try
        {
            maximumLetterMatches = Integer.parseInt(Gateway.getProperty(APPLICATION_PROPERTY_GROUP_NAME, "maxAlternateCurrencies", "50"));
        }
        catch(NumberFormatException ne)
        {
            maximumLetterMatches = 50;
            logger.error("Number format exception for maxAlternateCurrencies property. " + 
                    "Defaulting to " + maximumLetterMatches);
        }
        
        // Highly unlikely, this will prevent hard crashes while logging the problem
        if (countries.length > maximumLetterMatches)
        {
            logger.error("Illegal number of alternate currencies. " + 
                         "Maximum number of supported alternate currencies is " +
                         maximumLetterMatches);
        }

        countryCodeToButtonActionMapping = new Hashtable(countries.length);
        buttonActionToCountryCodeMapping = new Hashtable(countries.length);
        int x = 1;
        for (int i = 0; i < countries.length && i < maximumLetterMatches ; i++)
        {
            String button = "Button" + x++;
            countryCodeToButtonActionMapping.put(countries[i].getCountryCode(), button);
            buttonActionToCountryCodeMapping.put(button, countries[i].getCountryCode());
        }
    }
    
    //--------------------------------------------------------------------------
    /**
        Returns the button action that corresponds to a country code.
        
        @param countryCode The country code that corresponds to a given button action (aka letter)
        @return The button action (aka letter)
    **/
    //--------------------------------------------------------------------------
    public String getButtonAction(String countryCode)
    {
        return (String)countryCodeToButtonActionMapping.get(countryCode);
    }

    //--------------------------------------------------------------------------
    /**
        Returns the button action that corresponds to a country code.
        
        @param buttonAction The button action (aka letter) that corresponds to a given country code
        @return The country code
    **/
    //--------------------------------------------------------------------------
    public String getCountryCode(String buttonAction)
    {
        return (String)buttonActionToCountryCodeMapping.get(buttonAction);
    }
}
