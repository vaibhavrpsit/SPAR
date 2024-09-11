/* ===========================================================================
* Copyright (c) 1999, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CountryModel.java /main/17 2013/06/25 15:30:45 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   06/25/13 - Fix for ArrayIndexOutofBoundsException - EIT defect
 *    cgreene   08/30/11 - removed deprecated methods
 *    jswan     10/12/10 - Fixed defect in which data from a UI model was
 *                         corrupting subsequent tender objects.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mkochumm  01/23/09 - check for nulls
 *
 * ===========================================================================
 * $Log:
 *  4    360Commerce 1.3         8/9/2006 9:01:42 PM    Robert Zurga    Merge
 *       4159: Country Name appearing incorrectly, defect fixed.
 *  3    360Commerce 1.2         3/31/2005 4:27:31 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:20:25 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:10:13 PM  Robert Pearse   
 *
 * Revision 1.7  2004/07/29 20:53:11  jdeleau
 * @scr 6594 backout changes until BA clarifies requirements
 *
 * Revision 1.4  2004/04/21 18:50:38  tfritz
 * @scr 4252 - Found while testing this SCR.  The getStateInfo() was throwing a null pointer exception.
 *
 * Revision 1.3  2004/03/16 17:15:22  build
 * Forcing head revision
 *
 * Revision 1.2  2004/03/16 17:15:17  build
 * Forcing head revision
 *
 * Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 * updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Oct 22 2003 09:35:00   baa
 * refactoring
 * 
 *    Rev 1.1   Sep 29 2003 15:56:16   rsachdeva
 * getStateNames for country index specified
 * Resolution for POS SCR-3056: UI Testing:  Data lost if selected FIND & then ESC from populated MBC Cust.
 * 
 *    Rev 1.0   Aug 29 2003 16:09:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.5   Jun 25 2003 16:00:12   baa
 * remove default state setting for customer info lookup
 * 
 *    Rev 1.4   Mar 20 2003 18:18:56   baa
 * customer screens refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.3   Sep 20 2002 17:56:28   baa
 * country/state fixes and other I18n changes
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.2   Sep 19 2002 09:45:32   baa
 * fix state country  pair for I18n
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Sep 18 2002 17:15:28   baa
 * country/state changes
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Sep 11 2002 23:39:44   baa
 * Initial revision.
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.2   Sep 03 2002 16:04:58   baa
 * externalize domain  constants and parameter values
 * Resolution for POS SCR-1740: Code base Conversions
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.domain.utility.CountryIfc;
import oracle.retail.stores.domain.utility.StateIfc;

/**
 * @version $Revision: /main/17 $
 */
public class CountryModel extends POSBaseBeanModel
{
    private static final long serialVersionUID = 1529681861137053857L;

    /** revision number supplied by PVCS **/
    public static final String revisionNumber = "$Revision: /main/17 $";

    /** Index of the country that issue the id */
    protected int countryIndx = 0;

    /** Index of the state that issue the id */
    protected int stateIndx = 0;

    /** Array of country names */
    protected String[] countryNames = null;

    /** Array of state names */
    protected String[] stateNames = null;

    /** the model for the country field **/
    protected CountryIfc[] countries = null;

    /**
     * Constructor
     */
    public CountryModel()
    {
    }

    /**
     * Retrieves the current state index
     * 
     * @return int the current state index
     */
    public int getStateIndex()
    {
        return stateIndx;
    }

    /**
     * Updates the current state index
     * 
     * @param value the value for the current state index
     * @return int the current state index
     */
    public void setStateIndex(int value)
    {
        stateIndx = value;
    }

    /**
     * Retrieves the current country index
     * 
     * @return int the current country index
     */
    public int getCountryIndex()
    {
        return countryIndx;
    }

    /**
     * Sets the current country index
     * 
     * @return value the current coutnry index
     */
    public void setCountryIndex(int value)
    {
        countryIndx = value;
    }

    /**
     * Returns the states info for the current country
     * 
     * @return the value of IDState
     */
    public StateIfc getStateInfo()
    {
        StateIfc state = null;

        if (countries != null)
        {
            if (countryIndx > -1 && countryIndx < countries.length)
            {
                StateIfc[] states = countries[countryIndx].getStates();
                if (stateIndx >= 0)
                {
                    state = states[stateIndx];
                }
            }
        }

        return state;
    }

    /**
     * Returns the country info for the currently selected country
     * 
     * @return CountryIfc the country info object
     */
    public CountryIfc getCountryInfo()
    {
        CountryIfc value = null;
        if (countries != null)
        {
            value = countries[countryIndx];
        }
        return value;
    }

    /**
     * Get the value of the IDState field
     * 
     * @return the value of IDState
     */
    public String getState()
    {
        String state = null;
        if (getStateInfo() != null)
        {
            state = getStateInfo().getStateCode();
        }
        return state;
    }

    /**
     * Get the value of the IDState field
     * 
     * @return the value of IDState
     */
    public String getStateName()
    {
        String state = null;
        if (getStateInfo() != null)
        {
            state = getStateInfo().getStateName();
        }
        return state;
    }

    /**
     * Get the value of the IDCountry field
     * 
     * @return the value of IDCountry
     */
    public String getCountry()
    {
        String value = null;
        if (getCountryInfo() != null)
        {
            value = getCountryInfo().getCountryCode();
        }
        return value;
    }

    /**
     * Get the value of the IDCountry field
     * 
     * @return the value of IDCountry
     */
    public String getCountryName()
    {
        return getCountryInfo().getCountryName();
    }

    /**
     * Get the value of a country
     * 
     * @param index country index
     * @return the value of IDCountry
     */
    public CountryIfc getCountry(int index)
    {
        CountryIfc value = null;
        if (countries != null)
        {
            value = countries[index];
        }
        return value;
    }

    /**
     * Get weather the postal code is required
     * 
     * @param index country index
     * @return the value of IDCountry
     */
    public boolean isExtPostalCodeRequired(int index)
    {
        return getCountry(index).isExtPostalCodeRequired();
    }

    /**
     * Get weather the postal code is required
     * 
     * @param index country index
     * @return the value of IDCountry
     */
    public boolean isPostalCodeRequired(int index)
    {
        return getCountry(index).isPostalCodeRequired();
    }

    /**
     * Get weather the postal code is required
     * 
     * @param index country index
     * @return the value of IDCountry
     */
    public String getPostalCodeDelim(int index)
    {
        return getCountry(index).getPostalCodeDelimiter();
    }

    /**
     * Get the state model
     * 
     * @return the value of IDState
     */
    public CountryIfc[] getCountries()
    {
        return countries;
    }

    /**
     * Get the state model
     * 
     * @return the value of IDState
     */
    public void setCountries(CountryIfc[] values)
    {
        countries = values;
    }

    /**
     * Get the state model
     * 
     * @return the value of IDState
     */
    public String[] getCountryNames()
    {
        if (countryNames == null)
        {
            if (countries != null)
            {
                countryNames = new String[countries.length];
                for (int i = 0; i < countries.length; i++)
                {
                    countryNames[i] = countries[i].getCountryName();
                }
            }
        }
        return countryNames;
    }

    /**
     * Get the state model
     * 
     * @return the value of IDState
     */
    public void setCountryNames(String[] names)
    {
        countryNames = names;
    }

    /**
     * Get the state model
     * 
     * @return the value of IDState
     */
    public String[] getStateNames()
    {
        return getStateNames(countryIndx);
    }

    /**
     * Get the state names for the country index specified
     * 
     * @param countryIndexSpecified country index specified
     * @return array of State Names
     */
    public String[] getStateNames(int countryIndexSpecified)
    {

        if (countries != null)
        {
            StateIfc[] aList = countries[countryIndexSpecified].getStates();
            stateNames = new String[aList.length];
            for (int i = 0; i < aList.length; i++)
            {
                stateNames[i] = aList[i].getStateName();
            }
        }
        return stateNames;
    }

    /**
     * Get the state model
     * 
     * @return the value of IDState
     */
    public void setStateNames(String[] names)
    {
        stateNames = names;
    }

    /**
     * Resets the model to original values.
     */
    public void reset()
    {
        countryIndx = 0;
        stateIndx = 0;
        countryNames = null;
        stateNames = null;
        countries = null;
    }
}
