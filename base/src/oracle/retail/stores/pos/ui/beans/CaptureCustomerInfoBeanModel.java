/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CaptureCustomerInfoBeanModel.java /main/28 2013/09/19 08:05:35 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     09/13/13 - Fixed null pointer exception.
 *    abhinavs  05/10/13 - Fix to set and display correct address type
 *    yiqzhao   07/30/12 - remove id type from shipping customer info screen
 *    cgreene   04/03/12 - removed deprecated methods
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   03/01/09 - deprecate phoneType and remove some unneeded
 *                         telephone methods and fields
 *    mahising  02/21/09 - Fixed phonetype issue for mailcheck tender
 *    mahising  02/19/09 - Resolved Bug Id:2211
 *    npoola    02/11/09 - fix issue for send customer info
 *    mkochumm  01/23/09 - set country
 *    mkochumm  01/23/09 - set country
 *    mdecama   12/03/08 - Setting the currentIDType to -1 (undefined) instead
 *                         of 0 (firstEntry)
 *    mkochumm  11/20/08 - cleanup based on i18n changes
 *    mkochumm  11/19/08 - cleanup based on i18n changes
 *    mkochumm  11/19/08 - cleanup based on i18n changes
 *    mkochumm  11/17/08 - cleanup based on i18n changes
 *    abondala  11/07/08 - fixing the POS crash if the personal id type is not
 *                         selected
 *    mdecama   10/27/08 - I18N - Refactoring Reason Codes for
 *                         CaptureCustomerIDTypes
 * ===========================================================================
     $Log:
      5    .v8x      1.3.1.0     3/11/2007 3:53:55 PM   Brett J. Larsen CR 4530
            - default reason code not being displayed in list

           added support for default reason code (IDType for this screen)
      4    360Commerce1.3         7/28/2006 6:05:48 PM   Brett J. Larsen CR
           4530 - default reason code fix
           CR 6131 - phone type values updated
           v7x->360Commerce merge
      3    360Commerce1.2         3/31/2005 4:27:20 PM   Robert Pearse
      2    360Commerce1.1         3/10/2005 10:19:59 AM  Robert Pearse
      1    360Commerce1.0         2/11/2005 12:09:48 PM  Robert Pearse
     $

      5    .v7x      1.2.1.1     7/15/2006 8:02:54 PM   Michael Wisbauer Added
           checking index if Id type and if it is -1 then return a blank
           string ""
      4    .v7x      1.2.1.0     4/28/2006 7:28:35 AM   Dinesh Gautam   CR
           6131: Phone Type Values text updated

     Revision 1.15  2004/09/23 17:46:05  rsachdeva
     @scr 7188 Capture Customer Journal

     Revision 1.14  2004/07/23 16:25:57  aachinfiev
     @scr 5008 - Added journalling of captured customer information

     Revision 1.13  2004/07/14 18:47:09  epd
     @scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation

     Revision 1.12  2004/07/08 14:44:13  khassen
     @scr 6039 - updated validation methods/fields/functionality for the postal code.

     Revision 1.11  2004/06/18 12:12:26  khassen
     @scr 5684 - Feature enhancements for capture customer use case.

     Revision 1.10  2004/06/02 17:40:35  dfierling
     @scr 4891 - fixed spelling error

     Revision 1.9  2004/05/06 16:26:57  aschenk
     @scr 4647 - The Country and State/Region data fields both contained a value of "Other" which was removed when collecting Customer information for Mail Check tenders.

     Revision 1.8  2004/04/27 17:24:31  cdb
     @scr 4166 Removed unintentional null pointer exception potential.

     Revision 1.7  2004/04/09 16:56:00  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.6  2004/03/16 17:15:22  build
     Forcing head revision

     Revision 1.5  2004/03/16 17:15:16  build
     Forcing head revision

     Revision 1.4  2004/02/27 19:20:49  khassen
     @scr 0 Capture Customer Info use-case
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.Vector;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;

/**
 * @author kph
 */
public class CaptureCustomerInfoBeanModel extends CountryModel
{
    // Generated VersionUID
    private static final long serialVersionUID = -8920303813259806879L;

    // Fields that contain Customer Information
    protected String fieldFirstName = "";
    protected String fieldLastName = "";
    protected String fieldAddressLine1 = "";
    protected String fieldAddressLine2 = "";
    protected String fieldCity = "";
    protected String fieldPostalCode = "";
    protected String addressType="";

	/** The currently selected phone */
    protected PhoneIfc phone;
    /** The complete list of phones for this customer. */
    protected PhoneIfc[] phoneList = null;
    protected String[] phoneTypes = null;
    protected Vector<String> idTypes = null;
    protected Vector<String> idTypesKeys = null;
    protected String defaultIDType = null;
    protected int currentIDType = -1;
    protected boolean isPostalCodeRequired = false;
    protected String fieldCustomerName = "";
    /** editable indicator **/
    protected boolean editableFields = true;
    /** Journal string for updated customer fields */
    protected String journalString = "";
    /** business customer indicator **/
    protected boolean businessCustomer = false;
    /** mailCheck indicator * */
    protected boolean mailCheck = false;
    /** cross channel shipping flag **/
    protected boolean xchannelShipping = false;

    /**
     * CustomerInfoBeanModel constructor.
     */
    public CaptureCustomerInfoBeanModel()
    {
        idTypes = new Vector<String>();
        idTypesKeys = new Vector<String>();
        phone = DomainGateway.getFactory().getPhoneInstance();
    }

    public boolean isPostalCodeRequired()
    {
        return isPostalCodeRequired;
    }

    public void setPostalCodeRequired(boolean val)
    {
        isPostalCodeRequired = val;
    }

    /**
     * Gets the addressLine1 property (java.lang.String) value.
     * 
     * @return The addressLine1 property value.
     * @see #setAddressLine1
     */
    public String getAddressLine1()
    {
        return fieldAddressLine1;
    }

    /**
     * Sets the addressLine1 property (java.lang.String) value.
     * 
     * @param addressLine1 The new value for the property.
     * @see #getAddressLine1
     */
    public void setAddressLine1(String addressLine1)
    {
        fieldAddressLine1 = addressLine1;
    }

    /**
     * Gets the addressLine2 property (java.lang.String) value.
     * 
     * @return The addressLine2 property value.
     * @see #setAddressLine2
     */
    public String getAddressLine2()
    {
        return fieldAddressLine2;
    }

    /**
     * Sets the addressLine2 property (java.lang.String) value.
     * 
     * @param addressLine2 The new value for the property.
     * @see #getAddressLine2
     */
    public void setAddressLine2(String addressLine2)
    {
        fieldAddressLine2 = addressLine2;
    }

    /**
     * Gets the city property (java.lang.String) value.
     * 
     * @return The city property value.
     * @see #setCity
     */
    public String getCity()
    {
        return fieldCity;
    }

    /**
     * Sets the city property (java.lang.String) value.
     * 
     * @param city The new value for the property.
     * @see #getCity
     */
    public void setCity(String city)
    {
        fieldCity = city;
    }

    /**
     * Gets the firstName property (java.lang.String) value.
     * 
     * @return The firstName property value.
     * @see #setFirstName
     */
    public String getFirstName()
    {
        return fieldFirstName;
    }

    /**
     * Sets the firstName property (java.lang.String) value.
     * 
     * @param firstName The new value for the property.
     * @see #getFirstName
     */
    public void setFirstName(String firstName)
    {
        fieldFirstName = firstName;
    }

    /**
     * Gets the lastName property (java.lang.String) value.
     * 
     * @return The lastName property value.
     * @see #setLastName
     */
    public String getLastName()
    {
        return fieldLastName;
    }

    /**
     * Sets the lastName property (java.lang.String) value.
     * 
     * @param lastName The new value for the property.
     * @see #getLastName
     */
    public void setLastName(String lastName)
    {
        fieldLastName = lastName;
    }

    /**
     * Gets the postalCode property (java.lang.String) value.
     * 
     * @return The postalCode property value.
     * @see #setPostalCode
     */
    public String getPostalCode()
    {
        return fieldPostalCode;
    }

    /**
     * Sets the telephoneNumber property (java.lang.String) value.
     * 
     * @param telephoneNumber The new value for the property.
     * @see #getPhoneNumber()
     */
    public void setPhoneNumber(String telephoneNumber, int phoneType)
    {
        if (telephoneNumber != null)
        {
            phone.parseString(telephoneNumber);
            phone.setCountry(getCountry());
        }

        phone.setPhoneType(phoneType);
        if (phoneList == null)
        {
            phoneList = new PhoneIfc[PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR.length];
        }
        phoneList[phoneType] = (PhoneIfc)phone.clone();
    }

    public String getPhoneNumber()
    {
        return phone.getPhoneNumber();
    }

    /**
     * Gets the telephoneNumber property (java.lang.String) value.
     * 
     * @param int the telephone type
     * @return The telephoneNumber property value.
     * @see #setTelephoneNumber
     */
    public String getPhoneNumber(int type)
    {
        String number = "";

        if (phoneList != null && phoneList[type] != null)
        {
            number = phoneList[type].getPhoneNumber();
        }
        return number;
    }

    /**
     * Sets the list of phones associated to a customer
     * 
     * @param telephoneType The new value for the property.
     * @see #getTelephoneType
     */
    public void setPhoneList(PhoneIfc[] phones)
    {
        phoneList = phones;
        if (phoneList != null && phoneList.length > getPhoneType())
        {
            phone = phoneList[getPhoneType()];
        }
    }

    /**
     * Sets the list of phones associated to a customer
     * 
     * @param telephoneType The new value for the property.
     * @see #getTelephoneType
     */
    public PhoneIfc[] getPhoneList()
    {
        return (phoneList);
    }

    public String getFormattedPhone()
    {
        return phone.toFormattedString();
    }

    public int getPhoneType()
    {
        return phone.getPhoneType();
    }

    public void setPhoneType(int type)
    {
        phone.setPhoneType(type);
    }

    public String[] getPhoneTypes()
    {
        return phoneTypes;
    }

    /**
     * Sets the list of available phone types
     * 
     * @param data the list of phonetypes.
     * @see #setPhoneTypes
     */
    public void setPhoneTypes(String[] data)
    {
        phoneTypes = data;
    }

    /**
     * Sets the postalCode property (java.lang.String) value.
     * 
     * @param postalCode The new value for the property.
     * @see #getPostalCode
     */
    public void setPostalCode(String postalCode)
    {
        fieldPostalCode = postalCode;
    }

    /**
     * Sets the editableFields attribute.
     * 
     * @param enabled boolean to set editableFields
     */
    public void setEditableFields(boolean value)
    {
        editableFields = value;
    }

    /**
     * Get the editableFields attribute.
     * 
     * @return boolean editableFields returned
     */
    public boolean getEditableFields()
    {
        return (editableFields);
    }

    public Vector<String> getIDTypes()
    {
        return idTypes;
    }

    public void setIDTypes(Vector<String> idList)
    {
        idTypes = idList;
    }

    public void setIDType(String type)
    {
        if (type != null)
        {
            for (int i = 0; i < idTypes.size(); i++)
            {
                if (type.compareTo(idTypes.get(i)) == 0)
                    currentIDType = i;
            }
        }
    }

    public String getIDType(int i)
    {
        if (i > -1)
            return idTypes.get(i);

        return "";
    }

    public String getSelectedIDType()
    {
        if (currentIDType > -1)
            return idTypes.get(currentIDType);

        return "";
    }

    public void setSelectedIDType(int i)
    {
        currentIDType = i;
    }

    /**
     * Get the current id type
     * 
     * @return int current id type
     */
    public int getCurrentIDType()
    {
        return currentIDType;
    }

    /**
     * Get the value of the journalString field
     * 
     * @return the value of journalString
     */
    public String getJournalString()
    {
        return journalString;
    }

    //--------------------------------------------------------------------------
    // --
    /**
     * Sets the journalString field
     * 
     * @param the value to be set for journalString
     */
    //--------------------------------------------------------------------------
    // --
    public void setJournalString(String value)
    {
        journalString = value;
    }

    /**
     * @return Returns the defaultIDType.
     */
    public String getDefaultIDType()
    {
        return defaultIDType;
    }

    /**
     * @param defaultIDType The defaultIDType to set.
     */
    public void setDefaultIDType(String defaultIDType)
    {
        this.defaultIDType = defaultIDType;
    }

    /**
     * @return the idTypesKeys
     */
    public Vector<String> getIdTypesKeys()
    {
        return idTypesKeys;
    }

    /**
     * @param idTypesKeys the idTypesKeys to set
     */
    public void setIdTypesKeys(Vector<String> idTypesKeys)
    {
        this.idTypesKeys = idTypesKeys;
    }

    public String getSelectedKey()
    {
        if (CodeConstantsIfc.CODE_INTEGER_UNDEFINED == currentIDType)
        {
            return Integer.toString(currentIDType);
        }

        return idTypesKeys.get(currentIDType);
    }

    /**
     * Sets the business customer indicator. True is for business customer false
     * is for regular customers
     * 
     * @param enabled boolean to set editableFields
     */
    public void setBusinessCustomer(boolean value)
    {
        businessCustomer = value;
    }

    /**
     * Returns true if this is a business customer false otherwise
     * 
     * @return boolean business customer indicator
     */
    public boolean isBusinessCustomer()
    {
        return (businessCustomer);
    }

    /**
     * Gets the business organization name.
     * <P>
     * 
     * @return String organization name
     * @see #setOrgName
     */
    public String getOrgName()
    {
        return fieldCustomerName;
    }

    /**
     * Sets organization name.
     * 
     * @param value organization name
     * @see #setOrgName
     */
    public void setOrgName(String value)
    {
        fieldCustomerName = value;
    }

    /**
     * Sets the MailCheck indicator. True if for MailCheck false if for other
     * tenders
     * 
     * @param enabled boolean to set editableFields
     */
    public void setMailCheck(boolean value)
    {
        mailCheck = value;
    }

    /**
     * Returns true if this is a MailCheck false otherwise
     * 
     * @return boolean MailCheck indicator
     */
    public boolean isMailCheck()
    {
        return (mailCheck);
    }
    
    /**
     * return the captured customer is for cross channel shipping
     * @return
     */
    public boolean isXchannelShipping() {
		return xchannelShipping;
	}

    /**
     * set the captured customer is for cross channel shipping
     * @param xchannelShipping
     */
	public void setXchannelShipping(boolean xchannelShipping) {
		this.xchannelShipping = xchannelShipping;
	}
	
    public String getAddressType() {
		return addressType;
	}

	public void setAddressType(String addressType) {
		this.addressType = addressType;
	}


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuffer buf = new StringBuffer("CustomerInfoBeanModel{");
        buf.append("First Name=").append(getFirstName()).append(",");
        buf.append("Last Name=").append(getLastName()).append(",");
        buf.append("Address Line 1=").append(getAddressLine1()).append(",");
        buf.append("Address Line 2=").append(getAddressLine2()).append(",");
        buf.append("City=").append(getCity()).append(",");
        buf.append("PostalCode=").append(getPostalCode()).append(",");
        buf.append("Country=");
        if (getCountryInfo() != null)
        {
            buf.append(getCountryInfo().getCountryName()).append(",");
        }
        else
        {
            buf.append("null,");
        }
        buf.append("Telephone=").append(getFormattedPhone()).append(",");
        buf.append("editableFields=").append(getEditableFields()).append(",");
        buf.append("}");
        return buf.toString();
    }

}
