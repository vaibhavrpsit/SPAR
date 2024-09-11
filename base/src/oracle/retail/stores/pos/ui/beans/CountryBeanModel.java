/* ===========================================================================
* Copyright (c) 1999, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CountryBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:47 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  4    360Commerce 1.3         4/24/2007 1:16:09 PM   Charles D. Baker CR
 *       26556 - I18N Code Merge.
 *  3    360Commerce 1.2         3/31/2005 4:27:31 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:20:25 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:10:13 PM  Robert Pearse   
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
 *    Rev 1.0   Dec 09 2003 16:38:26   crain
 * Initial revision.
 * Resolution for 3421: Tender redesign
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Domain imports
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
//------------------------------------------------------------------------------ 
/**
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------ 
public class CountryBeanModel extends POSBaseBeanModel
{
     /** Index of the country that issue the id */
     protected int countryIndx = 0;

     /** Array of country names */
     protected String[] countryNames = null;

     /**    the model for the country field   **/
     protected CurrencyTypeIfc[] countries = null; 
     
    /** Default country */
    protected String defaultCountryCode = "";
       
    //---------------------------------------------------------------------
    /**
        Constructor 
    **/
    //---------------------------------------------------------------------
    public CountryBeanModel()
    {
    }
 
    //----------------------------------------------------------------------------
    /**
        Retrieves the current country index
        @return int the current country index
    **/
    //----------------------------------------------------------------------------
    public int getCountryIndex()
    {
        return countryIndx;
    }
     
     //----------------------------------------------------------------------------
    /**
        Sets the current country index
        @return value the current coutnry index
    **/
    //----------------------------------------------------------------------------
    public void setCountryIndex(int value)
    {
        countryIndx = value;
    }   
    
    //----------------------------------------------------------------------------
    /**
        Returns the country info for the currently selected country
        @return CurrencyTypeIfc the country info object
    **/
    //----------------------------------------------------------------------------
    public CurrencyTypeIfc getCountryInfo()
    {
        return countries[countryIndx];
    }
    //----------------------------------------------------------------------------
    /**
        Get the value of the IDCountry field
        @return the value of IDCountry
    **/
    //----------------------------------------------------------------------------
    public String getCountry()
    {
        return getCountryInfo().getCountryCode();
    }

     //----------------------------------------------------------------------------
    /**
        Get the value of a country
        @param index country index
        @return the value of IDCountry
    **/
    //----------------------------------------------------------------------------
    public CurrencyTypeIfc getCountry(int index)
    {
        return countries[index];
    }

       //----------------------------------------------------------------------------
    /**
        Get the countries
        @return countries as CurrencyTypeIfc[]
    **/
    //----------------------------------------------------------------------------
    public CurrencyTypeIfc[] getCountries()
    {
        return countries;
    }
   //----------------------------------------------------------------------------
    /**
        Set countries 
        @param CurrencyTypeIfc[] values
    **/
    //----------------------------------------------------------------------------
    public  void setCountries(CurrencyTypeIfc[] values)
    {
        countries = values;
    }
    //----------------------------------------------------------------------------
    /**
        Get country names
        @return String[]
    **/
    //----------------------------------------------------------------------------
    public  String[] getCountryNames()
    {
        if (countryNames == null)
        {
           if (countries != null)
           {
             countryNames = new String[countries.length];
             for (int i=0; i <countries.length; i++)
             {
               StringBuffer country = new StringBuffer(countries[i].getCurrencyCode());
               country.append(" - ").append(UIUtilities.retrieveCommonText(countries[i].getCountryCode()));
               countryNames[i] = country.toString();
             }
           }
        }   
        return countryNames;
    } 
    //----------------------------------------------------------------------------
    /**
        Set country names
        @param String[] names
    **/
    //----------------------------------------------------------------------------
    public void  setCountryNames(String[] names)
    {
          countryNames = names;
    }
    //----------------------------------------------------------------------------
    /**
        Get the value of the default country
        @return the value of default country
    **/
    //----------------------------------------------------------------------------
    public String getDefaultCountryCode()
    {
        return defaultCountryCode;
    }
    //----------------------------------------------------------------------------
    /**
        Get the value of the default country
        @param the value of the default country
    **/
    //----------------------------------------------------------------------------
    public void setDefaultCountryCode(String value)
    {
        defaultCountryCode = value;
    }
}

