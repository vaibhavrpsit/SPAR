/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DeliveryAddressBeanModel.java /rgbustores_13.4x_generic_branch/2 2011/08/23 13:05:22 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   08/22/11 - removed deprecated methods
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    mkochumm  01/23/09 - set country
 *    npoola    12/18/08 - fix bat issue for PDO
 *    aphulamb  12/17/08 - bug fixing of PDO
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    aphulamb  11/13/08 - Delivery address bean model
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.Gender;
import oracle.retail.stores.domain.utility.PersonConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This model is used by CustomerInfoBean.
 * 
 * @version $$Revision: /rgbustores_13.4x_generic_branch/2 $
 * @see oracle.retail.stores.pos;ui.beans.CustomerInfoBean
 */
public class DeliveryAddressBeanModel extends ReasonBeanModel
{
    private static final long serialVersionUID = 8786159199204658443L;

    // Revision number supplied by source-code control system
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    // Fields that contain Customer Information
    protected String fieldCustomerID = "";
    protected String fieldEmployeeID = "";
    protected String fieldFirstName = "";
    protected String fieldCustomerName = "";
    protected String fieldLastName = "";
    protected String fieldAddressLine1 = "";
    protected String fieldAddressLine2 = "";
    protected String fieldAddressLine3 = "";
    protected String fieldCity = "";
    protected String fieldPostalCode = "";
    protected String fieldTelephoneNumber = "";
    protected String fieldEmail = "";
    protected PhoneIfc[] phoneList = null;
    protected String fieldCustomerGroup = "";
    protected String fieldTaxCertificate = "";
    protected String fieldSalutation = "";
    protected String fieldSuffix = "";
    protected int fieldGenderIndex = Gender.GENDER_UNSPECIFIED;
    protected String fieldMiddleName = new String();
    protected boolean fieldMailPrivacy = false;
    protected boolean fieldTelephonePrivacy = false;
    protected boolean fieldEmailPrivacy = false;
    protected boolean fieldValidBirthdate = false;
    protected boolean fieldValidBirthYear = false;
    protected long fieldBirthYear = PersonConstantsIfc.YEAR_OF_BIRTH_UNSPECIFIED;
    protected EYSDate birthdate = null;
    protected String fieldExtPostalCode = "";
    /** 3 line address indicator **/
    protected boolean isContactOnlyInfo = false;
    /** editable indicator **/
    protected boolean editableFields = true;
    /** 3 line address indicator **/
    protected boolean threeLineAddress = false;
    /** business customer indicator **/
    protected boolean businessCustomer = false;
    /** values for customer field **/
    protected String[] customerGroupStrings = null;
    /** Array of supported languages **/
    protected String[] languages = null;
    /** Array of phone types **/
    protected String[] phoneTypes = null;
    /** Array of gender types **/
    protected String[] genderTypes = null;
    /** Index of language selected **/
    protected int selectedLanguage = 0;
    /** Preselected language **/
    protected String preferredLanguage = "";
    /** selected discount rule **/
    protected int selectedCustomerGroupIndex = 0;
    /**
     * The linkDoneSwitch indicates whether to activate the link key, the done
     * key or both.
     */
    protected int linkDoneSwitch = 0;
    // @deprecated as of release 5.0.0
    protected int fieldTelephoneType = PhoneConstantsIfc.PHONE_TYPE_HOME;
    // @deprecated as of release 5.0.0
    protected String fieldTelephoneStringType = PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR[PhoneConstantsIfc.PHONE_TYPE_HOME];
    // @deprecated as of release 6.0.0 replaced by fieldGenderIndex
    protected String fieldGender = "";
    protected String[] reasonCodeTags = null;
    protected boolean mailBankCheck = false;
    protected String instructions = "";

    /**
     * DeliveryAddressBeanModel constructor.
     */
    public DeliveryAddressBeanModel()
    {
        super();
    }

    /**
     * Gets the customerName property (java.lang.String) value.
     * 
     * @return The customerName property value.
     * @see #setCustomerName
     */
    public String getCustomerName()
    {
        if (Util.isEmpty(fieldCustomerName))
        {
            if (!isBusinessCustomer())
            {
                return fieldFirstName + " " + fieldLastName;
            }
        }
        return fieldCustomerName;
    }

    /**
     * Sets the customerName property (java.lang.String) value.
     * 
     * @param customerName The new value for the property.
     * @see #getCustomerName
     */
    public void setCustomerName(String customerName)
    {
        fieldCustomerName = customerName;
    }

    /**
     * Gets the business organization name.
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
     * Gets the tax certificate.
     * 
     * @return String tax certificate number
     * @see #setTaxCertificate
     */
    public String getTaxCertificate()
    {
        return fieldTaxCertificate;
    }

    /**
     * Sets tax certificate.
     * 
     * @param value tax certificate number
     * @see #setTaxCertificate
     */
    public void setTaxCertificate(String value)
    {
        fieldTaxCertificate = value;
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
     * Gets the addressLine3 property (java.lang.String) value.
     * 
     * @return The addressLine3 property value.
     * @see #setAddressLine3
     */
    public String getAddressLine3()
    {
        return fieldAddressLine3;
    }

    /**
     * Sets the addressLine2 property (java.lang.String) value.
     * 
     * @param addressLine2 The new value for the property.
     * @see #getAddressLine2
     */
    public void setAddressLine3(String addressLine3)
    {
        fieldAddressLine3 = addressLine3;
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
     * Gets the customerID property (java.lang.String) value.
     * 
     * @return The customerID property value.
     * @see #setCustomerID
     */
    public String getCustomerID()
    {
        return fieldCustomerID;
    }

    /**
     * Sets the customerID property (java.lang.String) value.
     * 
     * @param customerID The new value for the property.
     * @see #getCustomerID
     */
    public void setCustomerID(String customerID)
    {
        fieldCustomerID = customerID;
    }

    /**
     * Sets the EmployeeID property (java.lang.String) value.
     * 
     * @param EmployeeID The new value for the property.
     * @see #getEmployeeID
     */
    public void setEmployeeID(String EmployeeID)
    {
        fieldEmployeeID = EmployeeID;
    }

    /**
     * Gets the customerID property (java.lang.String) value.
     * 
     * @return The customerID property value.
     * @see #setCustomerID
     */
    public String getEmployeeID()
    {
        return fieldEmployeeID;
    }

    /**
     * Gets the extPostalCode property (java.lang.String) value.
     * 
     * @return The extPostalCode property value.
     * @see #setExtPostalCode
     */
    public String getExtPostalCode()
    {
        return fieldExtPostalCode;
    }

    /**
     * Gets the email property (java.lang.String) value.
     * 
     * @return The email property value.
     * @see #setEmail
     */
    public String getEmail()
    {
        return fieldEmail;
    }

    /**
     * Sets the email property (java.lang.String) value.
     * 
     * @param email The new value for the property.
     * @see #getEmail
     */
    public void setEmail(String email)
    {
        fieldEmail = email;
    }

    /**
     * Gets the email property (java.lang.String) value.
     * 
     * @return The email property value.
     * @see #setEmail
     * @deprecated as of release 6.0.0 replaced by getEmail()
     */
    public String getEmailAddress()
    {
        return getEmail();
    }

    /**
     * Sets the email property (java.lang.String) value.
     * 
     * @param email The new value for the property.
     * @see #getEmail
     * @deprecated as of release 6.0.0 replaced by setEmail()
     */
    public void setEmailAddress(String email)
    {
        setEmail(email);
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
     * @see #getTelephoneNumber
     */
    public void setTelephoneNumber(String telephoneNumber)
    {
        fieldTelephoneNumber = telephoneNumber;

        PhoneIfc phone = DomainGateway.getFactory().getPhoneInstance();
        if (telephoneNumber != null)
        {
            phone.parseString(telephoneNumber);
            phone.setCountry(getCountry());
        }
        phone.setPhoneType(fieldTelephoneType);
        if (phoneList == null)
        {
            phoneList = new PhoneIfc[PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR.length];
        }
        phoneList[fieldTelephoneType] = phone;
    }

    /**
     * Sets the telephoneNumber property (java.lang.String) value.
     * 
     * @param telephoneNumber The new value for the property.
     * @see #getTelephoneNumber
     */
    public void setTelephoneNumber(String telephoneNumber, int type)
    {
        fieldTelephoneNumber = telephoneNumber;
        fieldTelephoneType = type;
        fieldTelephoneStringType = PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR[type];

        PhoneIfc phone = DomainGateway.getFactory().getPhoneInstance();
        if (telephoneNumber != null)
        {
            phone.parseString(telephoneNumber);
            phone.setCountry(getCountry());
        }
        phone.setPhoneType(fieldTelephoneType);
        if (phoneList == null)
        {
            phoneList = new PhoneIfc[PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR.length];
        }
        phoneList[fieldTelephoneType] = phone;

    }

    /**
     * Gets the telephoneNumber property (java.lang.String) value.
     * 
     * @param int the telephone type
     * @return The telephoneNumber property value.
     * @see #setTelephoneNumber
     */
    public String getTelephoneNumber(int type)
    {

        String phone = new String("");
        if (phoneList != null && phoneList[type] != null)
        {
            phone = phoneList[type].getPhoneNumber();
        }
        return (phone);
    }

    /**
     * Gets the telephoneNumber property (java.lang.String) value.
     * 
     * @return The telephoneNumber property value.
     * @see #setTelephoneNumber
     */
    public String getTelephoneNumber()
    {
        return fieldTelephoneNumber;
    }

    /**
     * Gets the list of available phone types
     * 
     * @return String[] returns the list of phonetypes.
     * @see #setPhoneTypes
     */
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
     * Gets the telephoneType property (java.lang.String) value.
     * 
     * @return The telephoneType property value.
     * @see #setTelephoneType
     */
    public int getTelephoneIntType()
    {
        return fieldTelephoneType;
    }

    /**
     * Sets the telephoneType property (java.lang.String) value.
     * 
     * @param telephoneType The new value for the property.
     * @see #getTelephoneType
     */
    public void setTelephoneType(int telephoneType)
    {
        fieldTelephoneType = telephoneType;
        fieldTelephoneStringType = PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR[telephoneType];
    }

    /**
     * Gets the telephoneType property (java.lang.String) value.
     * 
     * @return The telephoneType property value.
     * @see #setTelephoneType
     * @deprecated as of release 5.0.0 replace by getTelephoneIntType()
     */
    public String getTelephoneType()
    {
        return fieldTelephoneStringType;
    }

    /**
     * Sets the telephoneType property (java.lang.String) value.
     * 
     * @param telephoneType The new value for the property.
     * @see #getTelephoneType
     * @deprecated as of release 5.0.0 replace by setTelephoneType(int
     *             telephoneType)
     */
    public void setTelephoneType(String telephoneType)
    {
        fieldTelephoneStringType = telephoneType;
        for (int i = 0; i < PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR.length; i++)
        {
            if (telephoneType.equalsIgnoreCase(PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR[i]))
            {
                fieldTelephoneType = i;
                break;
            }
        }
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

    /**
     * Sets the extPostalCode property (java.lang.String) value.
     * 
     * @param extPostalCode The new value for the property.
     * @see #getExtPostalCode
     */
    public void setExtPostalCode(String extPostalCode)
    {
        fieldExtPostalCode = extPostalCode;
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
     * Gets the discount rule property (java.lang.String) value.
     * 
     * @return The discount rule property value.
     * @see #setCustomerGroup
     */
    public String getCustomerGroup()
    {
        return fieldCustomerGroup;
    }

    /**
     * Sets the discount rule property (java.lang.String) value.
     * 
     * @param value The new value for the property.
     * @see #getCustomerGroup
     */
    public void setCustomerGroup(String value)
    {
        fieldCustomerGroup = value;
    }

    /**
     * Sets discount rules for bean model by taking passed-in array of discount
     * rules and placing the names in customerGroupStrings.
     * 
     * @param value array of discountRules
     */
    public void setCustomerGroups(CustomerGroupIfc[] value)
    {
      // if empty list sent, pass it on (and set up default value)
        if (value == null)
        {
            setCustomerGroupStrings((String[])null);
        }
        else
        {
            int numberRules = value.length;
            String[] ruleStrings = new String[numberRules];
            Locale locale = LocaleMap.getBestMatch(LocaleMap.getLocale(LocaleMap.DEFAULT));
            for (int i = 0; i < numberRules; i++)
            {
                ruleStrings[i] = value[i].getName(locale);
            }
            setCustomerGroupStrings(ruleStrings);
        }
    }

    /**
     * Sets discount rule strings.
     * 
     * @param value discount rule strings
     */
    public void setCustomerGroupStrings(String[] value)
    {
      // if empty array passed in, set "(none)"
        if (value == null)
        {
            value = new String[1];
            value[0] = "(none)";
        }
        customerGroupStrings = value;
    }

    /**
     * Gets the birthdate property (EYSDate) value. NOTE!!! this only contains a
     * valid Month and Day Use getBirthYear to get year!!!!
     * 
     * @return The birthdate property value.
     * @see #setBirthdate
     */
    public EYSDate getBirthMonthAndDay()
    {
        return birthdate;
    }

    /**
     * Gets the birthYear property value.
     * 
     * @return The birthyear property value.
     * @see #setBirthdate
     */
    public long getBirthYear()
    {
        return fieldBirthYear;
    }

    /**
     * Returns validity of birth year.
     * 
     * @return boolean
     */
    public boolean isBirthYearValid()
    {
        return fieldValidBirthYear;
    }

    /**
     * Returns validity of birth date.
     * 
     * @return boolean
     */
    public boolean isBirthdateValid()
    {
        return fieldValidBirthdate;
    }

    /**
     * Returns validity of birth year.
     * 
     * @return boolean
     */
    public void setContactInfoOnly(boolean value)
    {
        isContactOnlyInfo = value;
    }

    /**
     * Returns true is screen is to display contact info only.
     * 
     * @return boolean
     */
    public boolean isContactInfoOnly()
    {
        return isContactOnlyInfo;
    }

    /**
     * Gets the emailPrivacy property (boolean) value.
     * 
     * @return The emailPrivacy property value.
     * @see #setEmailPrivacy
     */
    public boolean getEmailPrivacy()
    {
        return fieldEmailPrivacy;
    }

    /**
     * Gets the employeeNumber property (java.lang.String) value.
     * 
     * @return The employeeNumber property value.
     * @see #setEmployeeNumber
     */
    public String getEmployeeNumber()
    {
        return getEmployeeID();
    }

    /**
     * Sets the employeeNumber property (java.lang.String) value.
     * 
     * @return The employeeNumber property value.
     * @see #setEmployeeNumber
     */
    public void setEmployeeNumber(String value)
    {
        setEmployeeID(value);
    }

    /**
     * Sets the gender property (java.lang.String) value.
     * 
     * @param gender The new value for the property.
     * @see #getGender
     * @deprecated as of release 6.0.0 replaced by setGenderIndex()
     */
    public void setGender(String gender)
    {
        fieldGender = gender;
    }

    /**
     * Gets the gender property (java.lang.String) value.
     * 
     * @return The gender property value.
     * @see #setGender
     * @deprecated as of release 6.0.0 replaced by getGenderIndex()
     */
    public String getGender()
    {
        return fieldGender;
    }

    /**
     * Sets the index for gender type
     * 
     * @param gender The new value for the property.
     * @see #getGenderIndex
     */
    public void setGenderIndex(int index)
    {
        fieldGenderIndex = index;
    }

    /**
     * Gets the gender selected index
     * 
     * @return int the gender index
     * @see #setGenderIndex
     */
    public int getGenderIndex()
    {
        return fieldGenderIndex;
    }

    /**
     * Gets the list of available gender types
     * 
     * @return String[] returns the list of gendertypes.
     * @see #setGenderTypes
     */
    public String[] getGenderTypes()
    {
        return genderTypes;
    }

    /**
     * Sets the list of available gender types
     * 
     * @param data the list of gendertypes.
     * @see #setGenderTypes
     */
    public void setGenderTypes(String[] data)
    {
        genderTypes = data;
    }

    /**
     * Gets the salutation property (java.lang.String) value.
     * 
     * @return The salutation property value.
     * @see #setSalutation
     */
    public String getSalutation()
    {
        return fieldSalutation;
    }

    /**
     * Gets the telephonePrivacy property (boolean) value.
     * 
     * @return The telephonePrivacy property value.
     * @see #setTelephonePrivacy
     */
    public boolean getTelephonePrivacy()
    {
        return fieldTelephonePrivacy;
    }

    /**
     * Gets the mailPrivacy property (boolean) value.
     * 
     * @return The mailPrivacy property value.
     * @see #setMailPrivacy
     */
    public boolean getMailPrivacy()
    {
        return fieldMailPrivacy;
    }

    /**
     * Sets the birthdate property (Date) value.
     * 
     * @param birthdate The new value for the property.
     * @see #getBirthdate
     */
    public void setBirthdate(EYSDate value)
    {
        birthdate = value;
    }

    /**
     * Sets the emailPrivacy property (boolean) value.
     * 
     * @param emailPrivacy The new value for the property.
     * @see #getEmailPrivacy
     */
    public void setEmailPrivacy(boolean emailPrivacy)
    {
        fieldEmailPrivacy = emailPrivacy;
    }

    /**
     * Sets the mailPrivacy property (boolean) value.
     * 
     * @param mailPrivacy The new value for the property.
     * @see #getMailPrivacy
     */
    public void setMailPrivacy(boolean mailPrivacy)
    {
        fieldMailPrivacy = mailPrivacy;
    }

    /**
     * Sets the middleName property (java.lang.String) value.
     * 
     * @param middleName The new value for the property.
     * @see #getMiddleName
     */
    public void setMiddleName(String middleName)
    {
        fieldMiddleName = middleName;
    }

    /**
     * Sets the salutation property (java.lang.String) value.
     * 
     * @param salutation The new value for the property.
     * @see #getSalutation
     */
    public void setSalutation(String salutation)
    {
        fieldSalutation = salutation;
    }

    /**
     * Sets the birth year.
     * 
     * @param long
     */
    public void setBirthYear(long year)
    {
        fieldBirthYear = year;
    }

    /**
     * Sets the validity of birth year.
     * 
     * @param boolean
     */
    public void setBirthYearValid(boolean b)
    {
        fieldValidBirthYear = b;
    }

    /**
     * Sets the validity of birthdate
     * 
     * @param boolean b
     */
    public void setBirthdateValid(boolean b)
    {
        fieldValidBirthdate = b;
    }

    /**
     * Get the value of the Roles field
     * 
     * @return the value of Roles
     */
    public String[] getLanguages()
    {
        return languages;
    }

    /**
     * Get the value of the SelectedRole field
     * 
     * @return the value of SelectedRole
     */
    public int getSelectedLanguage()
    {
        return selectedLanguage;
    }

    /**
     * Get the value of the Selectedlanguage field
     * 
     * @return the value of SelectedRole
     */
    public String getPreferredLanguage()
    {
        return preferredLanguage;
    }

    /**
     * Sets the language field
     * 
     * @param values the value to be set for Roles
     */
    public void setLanguages(String[] values)
    {
        languages = values;
    }

    /**
     * Sets the SelectedLanguage field
     * 
     * @param value the value to be set for SelectedLanguage
     */
    public void setSelectedLanguage(int value)
    {
        selectedLanguage = value;
    }

    /**
     * Sets the SelectedLanguage field
     * 
     * @param value the value to be set for SelectedLanguage
     */
    public void setPreferredLanguage(String value)
    {
        preferredLanguage = value;
    }

    /**
     * Sets the telephonePrivacy property (boolean) value.
     * 
     * @param telephonePrivacy The new value for the property.
     * @see #getTelephonePrivacy
     */
    public void setTelephonePrivacy(boolean telephonePrivacy)
    {
        fieldTelephonePrivacy = telephonePrivacy;
    }

    /**
     * Retrieves discount rule strings.
     * 
     * @return discount rule strings
     */
    public String[] getCustomerGroupStrings()
    {
        return (customerGroupStrings);
    }

    /**
     * Sets the index of the selected discount rule.
     * 
     * @param value the index of the selected discount rule
     */
    public void setSelectedCustomerGroupIndex(int value)
    {
        selectedCustomerGroupIndex = value;
    }

    /**
     * Returns the index of the selected disocunt rule.
     * 
     * @return the index of the selected discount rule
     */
    public int getSelectedCustomerGroupIndex()
    {
        return (selectedCustomerGroupIndex);
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
     * Sets threeLineAddress attribute.
     * 
     * @param enabled boolean to setthreeLineAddress
     */
    public void set3LineAddress(boolean value)
    {
        threeLineAddress = value;
    }

    /**
     * Returns true if address should be printed with 3 lines, false if it has
     * two lines only.
     * 
     * @return boolean editableFields returned
     */
    public boolean is3LineAddress()
    {
        return (threeLineAddress);
    }

    /**
     * Sets the linkDoneSwitch attribute.
     * 
     * @param enabled boolean to set linkDoneSwitch
     */
    public void setLinkDoneSwitch(int value)
    {
        linkDoneSwitch = value;
    }

    /**
     * Get the linkDoneSwitch attribute.
     * 
     * @return boolean linkDoneSwitch returned
     */
    public int getLinkDoneSwitch()
    {
        return (linkDoneSwitch);
    }

    /**
     * Returns a String that represents the value of this object.
     * 
     * @return a string representation of the receiver
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder("DeliveryAddressBeanModel{");
        buf.append("CustomerId=").append(getCustomerID()).append(",");
        buf.append("EmployeeId=").append(getEmployeeID()).append(",");
        buf.append("First Name=").append(getFirstName()).append(",");
        buf.append("Last Name=").append(getLastName()).append(",");
        buf.append("Address Line 1=").append(getAddressLine1()).append(",");
        buf.append("Address Line 2=").append(getAddressLine2()).append(",");
        buf.append("Address Line 3=").append(getAddressLine3()).append(",");
        buf.append("City=").append(getCity()).append(",");
        buf.append("PostalCode=").append(getPostalCode()).append("-");
        buf.append(getExtPostalCode()).append(",");
        buf.append("Country=");
        if (getCountryInfo() != null)
        {
            buf.append(getCountryInfo().getCountryName()).append(",");
        }
        else
        {
            buf.append("null,");
        }
        buf.append("Telephone=").append(getTelephoneNumber()).append(",");
        buf.append("editableFields=").append(getEditableFields()).append(",");
        buf.append("email=").append(getEmail());
        buf.append("}");
        return buf.toString();
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * Returns the reasonCodeTags.
     * 
     * @return String[]
     */
    public String[] getReasonCodeTags()
    {
        return reasonCodeTags;
    }

    /**
     * Sets the reasonCodeTags.
     * 
     * @param reasonCodeTags The reasonCodeTags to set
     */
    public void setReasonCodeTags(String[] values)
    {
        reasonCodeTags = values;
    }

    /**
     * Retrieves flag that indicates if customer info is used for mailbank
     * check.
     * 
     * @return boolean
     */
    public boolean isMailBankCheck()
    {
        return mailBankCheck;
    }

    /**
     * Sets the flag that indicates if customer info is used for mailbank check.
     * 
     * @param b indicates if mailbank check screen or not
     */
    public void setMailBankCheck(boolean b)
    {
        mailBankCheck = b;
    }

    public void setInstructions(String value)
    {
        instructions = value;
    }

    public String getInstructions()
    {
        return instructions;
    }
}
