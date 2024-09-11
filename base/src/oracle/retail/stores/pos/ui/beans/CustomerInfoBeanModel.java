/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CustomerInfoBeanModel.java /main/29 2014/06/03 17:06:10 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   06/02/14 - Added support for extended customer data.
 *    rgour     06/07/13 - marking a phone record as deleted if it is removed
 *    icole     02/01/13 - Changed getCustomerName logic.
 *    icole     02/01/13 - Changes to customer first and last names not
 *                         reflected on UI nor receipt nor persisted.
 *    hyin      05/18/12 - rollback changes made to CustomerUI for AddressType.
 *                         Change required field to phone number from
 *                         postalcode.
 *    asinton   03/26/12 - Customer UI changes to accomodate multiple
 *                         addresses.
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    rrkohli   06/30/11 - encryption CR
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    mahising  03/18/09 - Fixed CSP issue if item qty change and customer link
 *                         to the transaction
 *    mkochumm  01/23/09 - set country
 *    aphulamb  01/02/09 - fix delivery issues
 *    mahising  11/27/08 - fix after merge
 *    mahising  11/26/08 - fixed merge issue
 *    mkochumm  11/20/08 - cleanup based on i18n changes
 *    mkochumm  11/19/08 - cleanup based on i18n changes
 *    mkochumm  11/19/08 - cleanup based on i18n changes
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:37 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:39 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:22 PM  Robert Pearse
 *
 *   Revision 1.6  2004/04/27 17:24:31  cdb
 *   @scr 4166 Removed unintentional null pointer exception potential.
 *
 *   Revision 1.5  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.4  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:09:56   CSchellenger
 * Initial revision.
 *
 *    Rev 1.10   17 Jul 2003 03:35:36   baa
 * customer fixes
 *
 *    Rev 1.9   May 27 2003 08:49:32   baa
 * rework customer offline flow
 * Resolution for 2387: Deleteing Busn Customer Lock APP- & Inc. Customer.
 *
 *    Rev 1.8   May 09 2003 12:50:48   baa
 * more fixes to business customer
 * Resolution for POS SCR-2366: Busn Customer - Tax Exempt- Does not display Tax Cert #
 *
 *    Rev 1.7   Apr 02 2003 17:50:46   baa
 * customer and screen changes
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.6   Apr 02 2003 13:52:22   baa
 * I18n Database support for customer groups
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.5   Mar 21 2003 10:58:46   baa
 * Refactor mailbankcheck customer screen, second wave
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.4   Mar 20 2003 18:18:58   baa
 * customer screens refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.3   Sep 23 2002 13:26:44   baa
 * fix decimal field
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Sep 20 2002 18:03:06   baa
 * country/state fixes and other I18n changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Sep 18 2002 17:15:30   baa
 * country/state changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:56:54   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:54:54   msg
 * Initial revision.
 *
 *    Rev 1.3   25 Jan 2002 21:03:48   baa
 * ui fixes for customer
 * Resolution for POS SCR-824: Application crashes on Customer Add screen after selecting Enter
 *
 *    Rev 1.2   Jan 19 2002 10:29:36   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.1   11 Jan 2002 18:09:30   baa
 * change phone field
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 * Resolution for POS SCR-561: Changing the Type on Add Customer causes the default to change
 * Resolution for POS SCR-567: Customer Select Add, Find, Delete display telephone type as Home/Work
 *
 *    Rev 1.0   Sep 21 2001 11:37:52   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:18:00   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.List;

import oracle.retail.stores.common.customer.CustomerGiftList;
import oracle.retail.stores.common.item.ExtendedItemData;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.domain.customer.PricingGroupIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.Gender;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.PersonConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.factory.FoundationObjectFactoryIfc;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;

/**
 * This model is used by CustomerInfoBean.
 * 
 * @version $Revision: /main/29 $
 * @see oracle.retail.stores.pos;ui.beans.CustomerInfoBean
 */
public class CustomerInfoBeanModel extends ReasonBeanModel
{
    private static final long serialVersionUID = -3671761268083006111L;

    // Revision number supplied by source-code control system
    public static final String revisionNumber = "$Revision: /main/29 $";

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
    /**
     * @deprecated as of 13.4. Use {@link #customerTaxCertificate} instead.
     */
    protected String fieldTaxCertificate = "";
    protected EncipheredDataIfc customerTaxCertificate = null;
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

    /** is delivery address req. **/
    protected boolean deliveryAddress = false;

    /** values for customer field **/
    protected String[] customerGroupStrings = null;

    /** Array of supported languages **/
    protected String[] languages = null;
    
    /** Array of supported receipt modes **/
    protected String[] receiptModes = null;

    /** Array of phone types **/
    protected String[] phoneTypes = null;

    /** Array of gender types **/
    protected String[] genderTypes = null;

    /** Index of language selected **/
    protected int selectedLanguage = 0;

    /** Preselected receipt mode **/
    protected String preferredReceiptMode = "";
    
    /** Index of receipt mode selected **/
    protected int selectedReceiptMode = 0;

    /** Preselected language **/
    protected String preferredLanguage = "";

    /** selected discount rule **/
    protected int selectedCustomerGroupIndex = 0;

    /**
     * The linkDoneSwitch indicates whether to activate the link key, the done
     * key or both.
     */
    protected int linkDoneSwitch = 0;

    /**
     * telephone type.
     */
    protected int fieldTelephoneType = PhoneConstantsIfc.PHONE_TYPE_HOME;

    protected String[] reasonCodeTags = null;

    /**
     * Customer Tax ID
     * @deprecated as of 13.4. Use {@link #customerTaxID} instead.
     */
    protected String fieldCustomerTaxID = "";
    protected EncipheredDataIfc customerTaxID = null;

    /**
     * Customer Pricing Group
     */
    protected int fieldCustomerPricingGroup;

    /**
     * Array of String of Pricing group
     */
    protected String[] pricingGroups;

    /**
     * Array of Object of Pricing group
     */
    protected PricingGroupIfc[] customerPricingGroups;

    /**
     * Boolean to hold if its customer search flow or not
     */
    protected boolean customerSearchSpec = false;

    protected boolean mailBankCheck = false;

    // Flag indicating is application flow from layaway
    protected boolean fromLayaway = false;

    protected String instructions = "";

    /** List of recent purchased items */
    protected List<ExtendedItemData> recentItems = null;

    /** List of recommended items */
    protected List<ExtendedItemData> recommendedItems = null;

    /** list of gift lists of items */ 
    protected List<CustomerGiftList> giftLists = null;

    /** flag to indicate that the tabbed UI should be used */
    private boolean showTabbedPane = true;

    /**
     * CustomerInfoBeanModel constructor.
     */
    public CustomerInfoBeanModel()
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
        if (!isBusinessCustomer() && !fieldFirstName.isEmpty() && !fieldLastName.isEmpty())
        {
            return fieldFirstName + " " + fieldLastName;
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
        this.fieldCustomerName = customerName;
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
        this.fieldCustomerName = value;
    }

    /**
     * Gets the tax certificate.
     * 
     * @return String tax certificate number
     * @see #setTaxCertificate
     * @deprecated as of 13.4. Use {@link #getEncipheredTaxCertificate()} instead.
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
     * @deprecated as of 13.4. Use {@link #setEncipheredTaxCertificate(EncipheredDataIfc)} instead.
     */
    public void setTaxCertificate(String value)
    {
        this.fieldTaxCertificate = value;
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
     * Sets the line1 property (java.lang.String) value.
     * 
     * @param addressLine1 The new value for the property.
     * @see #getAddressLine1
     */
    public void setAddressLine1(String addressLine1)
    {
    	 this.fieldAddressLine1 = addressLine1;
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
    	this.fieldAddressLine2 = addressLine2;
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
    	this.fieldAddressLine3 = addressLine3;
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
    	 this.fieldCity = city;
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
        this.fieldCustomerID = customerID;
    }

    /**
     * Sets the EmployeeID property (java.lang.String) value.
     * 
     * @param EmployeeID The new value for the property.
     * @see #getEmployeeID
     */
    public void setEmployeeID(String EmployeeID)
    {
        this.fieldEmployeeID = EmployeeID;
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
        this.fieldEmail = email;
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
        this.fieldFirstName = firstName;
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
        this.fieldLastName = lastName;
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
        this.fieldTelephoneNumber = telephoneNumber;

        PhoneIfc phone = DomainGateway.getFactory().getPhoneInstance();
        phone.parseString(telephoneNumber);
        phone.setCountry(getCountry());
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
        this.fieldTelephoneNumber = telephoneNumber;
        fieldTelephoneType = type;

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
            PhoneIfc phoneObj = phoneList[type];
            if (phoneObj.isActive())
            {
                phone = phoneList[type].getPhoneNumber();
            }
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
        this.phoneTypes = data;
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
        this.fieldTelephoneType = telephoneType;
    }

    /**
     * Sets the list of phones associated to a customer
     * 
     * @param telephoneType The new value for the property.
     * @see #getTelephoneType
     */
    public void setPhoneList(PhoneIfc[] phones)
    {
        this.phoneList = phones;
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
     * Sets the postalCode property (java.lang.String) value.
     * 
     * @param postalCode The new value for the property.
     * @see #getPostalCode
     */
    public void setPostalCode(String postalCode)
    {
    	 this.fieldPostalCode = postalCode;
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
        this.fieldCustomerGroup = value;
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
            for (int i = 0; i < numberRules; i++)
            {
                ruleStrings[i] = value[i].getName(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
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
        this.isContactOnlyInfo = value;
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
        this.setEmployeeID(value);
    }

    /**
     * Sets the index for gender type
     * 
     * @param gender The new value for the property.
     * @see #getGenderIndex
     */
    public void setGenderIndex(int index)
    {
        this.fieldGenderIndex = index;
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
        this.genderTypes = data;
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
        this.birthdate = value;
    }

    /**
     * Sets the emailPrivacy property (boolean) value.
     * 
     * @param emailPrivacy The new value for the property.
     * @see #getEmailPrivacy
     */
    public void setEmailPrivacy(boolean emailPrivacy)
    {
        this.fieldEmailPrivacy = emailPrivacy;
    }

    /**
     * Sets the mailPrivacy property (boolean) value.
     * 
     * @param mailPrivacy The new value for the property.
     * @see #getMailPrivacy
     */
    public void setMailPrivacy(boolean mailPrivacy)
    {
        this.fieldMailPrivacy = mailPrivacy;
    }

    /**
     * Sets the middleName property (java.lang.String) value.
     * 
     * @param middleName The new value for the property.
     * @see #getMiddleName
     */
    public void setMiddleName(String middleName)
    {
        this.fieldMiddleName = middleName;
    }

    /**
     * Sets the salutation property (java.lang.String) value.
     * 
     * @param salutation The new value for the property.
     * @see #getSalutation
     */
    public void setSalutation(String salutation)
    {
        this.fieldSalutation = salutation;
    }

    /**
     * Sets the birth year.
     * 
     * @param long
     */
    public void setBirthYear(long year)
    {
        this.fieldBirthYear = year;
    }

    /**
     * Sets the validity of birth year.
     * 
     * @param boolean
     */
    public void setBirthYearValid(boolean b)
    {
        this.fieldValidBirthYear = b;
    }

    /**
     * Sets the validity of birthdate
     * 
     * @param boolean b
     */
    public void setBirthdateValid(boolean b)
    {
        this.fieldValidBirthdate = b;
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
        this.languages = values;
    }

    /**
     * Sets the SelectedLanguage field
     * 
     * @param value the value to be set for SelectedLanguage
     */
    public void setSelectedLanguage(int value)
    {
        this.selectedLanguage = value;
    }

    /**
     * Sets the SelectedLanguage field
     * 
     * @param value the value to be set for SelectedLanguage
     */
    public void setPreferredLanguage(String value)
    {
        this.preferredLanguage = value;
    }
    
    /**
     * @return the preferredReceiptMode
     */
    public String getPreferredReceiptMode()
    {
        return preferredReceiptMode;
    }

    /**
     * @param preferredReceiptMode the preferredReceiptMode to set
     */
    public void setPreferredReceiptMode(String preferredReceiptMode)
    {
        this.preferredReceiptMode = preferredReceiptMode;
    }

    /**
     * @return the selectedReceiptMode
     */
    public int getSelectedReceiptMode()
    {
        return selectedReceiptMode;
    }

    /**
     * @param selectedReceiptMode the selectedReceiptMode to set
     */
    public void setSelectedReceiptMode(int selectedReceiptMode)
    {
        this.selectedReceiptMode = selectedReceiptMode;
    }
    
    /**
     * @return the receiptModes
     */
    public String[] getReceiptModes()
    {
        return receiptModes;
    }

    /**
     * @param receiptModes the receiptModes to set
     */
    public void setReceiptModes(String[] receiptModes)
    {
        this.receiptModes = receiptModes;
    }

    /**
     * Sets the telephonePrivacy property (boolean) value.
     * 
     * @param telephonePrivacy The new value for the property.
     * @see #getTelephonePrivacy
     */
    public void setTelephonePrivacy(boolean telephonePrivacy)
    {
        this.fieldTelephonePrivacy = telephonePrivacy;
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
        this.selectedCustomerGroupIndex = value;
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
        this.editableFields = value;
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
        this.businessCustomer = value;
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
        this.threeLineAddress = value;
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
        this.linkDoneSwitch = value;
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
    public String toString()
    {
        StringBuffer buf = new StringBuffer("CustomerInfoBeanModel{");
        buf.append("CustomerId=").append(getCustomerID()).append(",");
        buf.append("EmployeeId=").append(getEmployeeID()).append(",");
        buf.append("First Name=").append(getFirstName()).append(",");
        buf.append("Last Name=").append(getLastName()).append(",");
        buf.append("Address Line 1=").append(getAddressLine1()).append(",");
        buf.append("Address Line 2=").append(getAddressLine2()).append(",");
        buf.append("Address Line 3=").append(getAddressLine3()).append(",");
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
        this.reasonCodeTags = values;
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
        this.mailBankCheck = b;
    }

    /**
     * Set customer tax ID
     * 
     * @param CustomerTaxID
     * @deprecated as of 13.4. Use {@link #setTaxID(EncipheredDataIfc)} instead.
     */
    public void setCustomerTaxID(String argCustomerTaxID)
    {
        this.fieldCustomerTaxID = argCustomerTaxID;
    }

    /**
     * Get customer tax ID
     * 
     * @return CustomerTaxID
     * @deprecated as of 13.4. Use {@link #getTaxID()} instead.
     */
    public String getCustomerTaxID()
    {
        return fieldCustomerTaxID;
    }

    /**
     * get CustomerTaxID
     * 
     * @return customerTaxID
     */
    public EncipheredDataIfc getTaxID()
    {
        if (customerTaxID == null)
        {
            FoundationObjectFactoryIfc factory = FoundationObjectFactory.getFactory();
            customerTaxID = factory.createEncipheredDataInstance();
        }
        return customerTaxID;
    }

    /**
     * Set CustomerTaxID
     * 
     * @param customerTaxID
     */
    public void setTaxID(EncipheredDataIfc encipheredData)
    {
        this.customerTaxID = encipheredData;
    }

    /**
     * set selected customer pricing group from pricing group array
     * 
     * @param selected CustomerPricingGroup
     */
    public void setSelectedCustomerPricingGroup(int argCustomerPricingGroup)
    {
        this.fieldCustomerPricingGroup = argCustomerPricingGroup;
    }

    /**
     * Get selected customer prcing group from Array of pricing group
     * 
     * @return Selected CustomerPricingGroup
     */
    public int getSelectedCustomerPricingGroup()
    {
        return fieldCustomerPricingGroup;
    }

    /**
     * Get Array of pricing group
     * 
     * @return Array of Pricing Groups
     */
    public String[] getPricingGroups()
    {
        return pricingGroups;
    }

    /**
     * Set Array of Pricing group
     * 
     * @param Array of Pricing Groups
     */
    public void setPricingGroups(String[] value)
    {
        this.pricingGroups = value;
    }

    /**
     * Set customerSearchSpec
     * 
     * @param boolean
     */
    public void setCustomersearchSpec(boolean value)
    {
        this.customerSearchSpec = value;
    }

    /**
     * Get customerSearchSpec
     * 
     * @return boolean
     */
    public boolean isCustomerSearchSpec()
    {
        return customerSearchSpec;
    }

    /**
     * Set the PricingGroups
     * 
     * @param customerPricingGroups pricing group
     */
    public void setCustomerPricingGroups(PricingGroupIfc[] customerPricingGroups)
    {
        this.customerPricingGroups = customerPricingGroups;
    }

    /**
     * Sets the Special DirectionField to True if its Delivery Action
     * 
     * @param enabled boolean to set editableFields
     */
    public void setIsDeliveryAddress(boolean value)
    {
        this.deliveryAddress = value;
    }

    /**
     * Returns true if if its Delivery Action
     * 
     * @return boolean delivery address customer.
     */
    public boolean isDeliveryAddress()
    {
        return (deliveryAddress);
    }

    /**
     * Get the value of the layawayFlag field
     * 
     * @return the value of layawayFlag
     */
    public boolean isFromLayaway()
    {
        return fromLayaway;
    }

    /**
     * Sets the layawayFlag field
     * 
     * @param the value to be set for layawayFlag
     */
    public void setFromLayaway(boolean layaway)
    {
        this.fromLayaway = layaway;
    }

    /**
     * Get the PricingGroups
     * 
     * @return PricingGroup[] pricing group
     */
    public PricingGroupIfc[] getCustomerPricingGroups()
    {
        return customerPricingGroups;
    }

    public void setInstructions(String value)
    {
        this.instructions = value;
    }

    public String getInstructions()
    {
        return instructions;
    }

    /**
     * get CustomerTaxCertificate
     * 
     * @return customerTaxCertificate
     */
    public EncipheredDataIfc getEncipheredTaxCertificate()
    {
        if (customerTaxCertificate == null)
        {
            FoundationObjectFactoryIfc factory = FoundationObjectFactory.getFactory();
            customerTaxCertificate = factory.createEncipheredDataInstance();
        }
        return customerTaxCertificate;
    }

    /**
     * Set CustomerTaxCertificate
     * 
     * @param customerTaxCertificate
     */
    public void setEncipheredTaxCertificate(EncipheredDataIfc encipheredData)
    {
        this.customerTaxCertificate = encipheredData;
    }

    /**
     * Returns the <code>recentItems</code> value.
     * @return the recentItems
     */
    public List<ExtendedItemData> getRecentItems()
    {
        return recentItems;
    }

    /**
     * Sets the <code>recentItems</code> value.
     * @param recentItems the recentItems to set
     */
    public void setRecentItems(List<ExtendedItemData> recentItems)
    {
        this.recentItems = recentItems;
    }

    /**
     * Returns the <code>recommendedItems</code> value.
     * @return the recommendedItems
     */
    public List<ExtendedItemData> getRecommendedItems()
    {
        return recommendedItems;
    }

    /**
     * Sets the <code>recommendedItems</code> value.
     * @param recommendedItems the recommendedItems to set
     */
    public void setRecommendedItems(List<ExtendedItemData> recommendedItems)
    {
        this.recommendedItems = recommendedItems;
    }

    /**
     * Returns the <code>giftLists</code> value.
     * @return the giftLists
     */
    public List<CustomerGiftList> getGiftLists()
    {
        return giftLists;
    }

    /**
     * Sets the <code>giftLists</code> value.
     * @param giftLists the giftLists to set
     */
    public void setGiftLists(List<CustomerGiftList> giftLists)
    {
        this.giftLists = giftLists;
    }

    /**
     * Returns the <code>showTabbedPane</code> value.
     * @return the showTabbedPane
     */
    public boolean isShowTabbedPane()
    {
        return showTabbedPane;
    }

    /**
     * Sets the <code>showTabbedPane</code> value.
     * @param showTabbedPane the showTabbedPane to set
     */
    public void setShowTabbedPane(boolean showTabbedPane)
    {
        this.showTabbedPane = showTabbedPane;
    }

}
