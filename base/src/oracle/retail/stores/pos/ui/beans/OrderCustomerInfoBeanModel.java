/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/OrderCustomerInfoBeanModel.java /main/4 2013/05/29 18:20:51 abondala Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* abondala    05/28/13 - add business name field to the order search screen
* yiqzhao     01/22/13 - Set phone number in Phone object for removing non
*                        digit charaters.
* yiqzhao     07/27/12 - modify order search flow and populate order cargo for
*                        searching
* yiqzhao     07/23/12 - modify order search flow for xchannel order and
*                        special order
* yiqzhao     07/20/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.ui.beans;

// java imports
import oracle.retail.stores.domain.utility.CountryIfc;
import oracle.retail.stores.domain.utility.Phone;


//----------------------------------------------------------------------------
/**
    Data transport between the bean and the application for credit card data
**/
//----------------------------------------------------------------------------
public class OrderCustomerInfoBeanModel extends POSBaseBeanModel
{
    private static final long serialVersionUID = 1L;

    public static String revisionNumber = "$Revision: /main/4 $";

    /** The first name  **/
    protected String firstName = "";
    
    /** The last name **/
	protected String lastName = "";
	
    /** Business name **/
    protected String businessName = "";
	
    /** Index of the country that issue the id */
    protected int countryIndx = 0;	
	
    /** Array of country names */
    protected String[] countryNames = null;

    /** the model for the country field **/
    protected CountryIfc[] countries = null;
	
    /** The phone number **/
	protected Phone phone = new Phone();

	/**
     * get the first name
     * @return
     */
    public String getFirstName() {
		return firstName;
	}

    /**
     * set the first name
     * @return
     */
    public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

    /**
     * get the last name
     * @return
     */	
	public String getLastName() {
		return lastName;
	}

    /**
     * set the last name
     * @return
     */	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * set the business name
	 * @return
	 */
    public String getBusinessName()
    {
        return businessName;
    }

    /**
     * get the business name
     * @param businessName
     */
    public void setBusinessName(String businessName)
    {
        this.businessName = businessName;
    }	
    /**
     * get the phone number
     * @return
     */	
	public String getTelephoneNumber() {
		return phone.getPhoneNumber();
	}

    /**
     * set the phone number
     * @return
     */
	public void setTelephone(String telephoneNumber) {
	    phone.parseString(telephoneNumber);
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
     * set country names
     * @param countryNames
     */
	public void setCountryNames(String[] countryNames) {
		this.countryNames = countryNames;
	}

    /**
     * Get the state model
     * 
     * @return the value of IDState
     */
	public CountryIfc[] getCountries() {
		return countries;
	}

	public void setCountries(CountryIfc[] countries) {
		this.countries = countries;
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
	//---------------------------------------------------------------------------
    /**
        Converts to a string representing the data in this Object
        @returns string representing the data in this Object
    **/
    //---------------------------------------------------------------------------
    public String toString()
    {
        StringBuffer buff = new StringBuffer();

        buff.append("Class:OrderCustomerBeanModel Revision: "
                    + revisionNumber
                    + "\n");
        buff.append("firstName        [" + firstName + "]\n");
        buff.append("lastName [" + lastName + "]\n");
        buff.append("businessName [" + businessName + "]\n");
        buff.append("Country=");
        if (getCountryInfo() != null)
        {
        	buff.append(getCountryInfo().getCountryName()).append(",");
        }
        else
        {
        	buff.append("null,");
        }
        if ( phone.getPhoneNumber() != null )
            buff.append("telephoneNumber [" + phone.getPhoneNumber() + "]\n");
        return(buff.toString());
    }

}