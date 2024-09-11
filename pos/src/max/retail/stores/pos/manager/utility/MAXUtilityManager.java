/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 * Rev 1.4		May 04, 2017		Kritica Agarwal 	GST Changes
 * 	Rev  	1.3  	21 Dec, 2016    Ashish Yadav    Credit Card FES
 *	Rev 	1.2		Dec 29,2016		Nitesh Kumar	Changes done for Till Reconcillation 
 * Rev 		1.1		Dec 09,2016		Nitesh Kumar	Changes done for Capillary Coupon Redemption
 * Rev 		1.0		Sep 01,2016		Ashish Yadav	Changes done for code merging
 * Change for Manager Override in case of Suspend transaction and Mall tender.
 * Whenever an existing customer is to be linked, it will be done only through manager override.
 * Changes to capture ManagerOverride for Reporting purpose
 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.manager.utility;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

import max.retail.stores.domain.utility.MAXCardTypeIfc;
import max.retail.stores.domain.utility.MAXCodeListMapIfc;
import max.retail.stores.pos.manager.ifc.MAXUtilityManagerIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.StoreDataTransaction;
import oracle.retail.stores.domain.arts.TransactionWriteDataTransaction;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.HardTotalsBuilderIfc;
import oracle.retail.stores.domain.financial.HardTotalsFormatException;
import oracle.retail.stores.domain.financial.HardTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.tax.NewTaxRuleIfc;
import oracle.retail.stores.domain.tax.TaxByLineRule;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.utility.CardType;
import oracle.retail.stores.domain.utility.CardTypeIfc;
import oracle.retail.stores.domain.utility.CardTypeUtility;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.CountryIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.domain.utility.StateIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.KeyStoreEncryptionManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.journal.JournalableIfc;
import oracle.retail.stores.foundation.manager.ifc.parameter.ParameterIfc;
import oracle.retail.stores.foundation.manager.parameter.EnumeratedListValidator;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.ResourceBundleUtil;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.tender.CreditTypeEnum;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.utility.CheckDigitUtility;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import max.retail.stores.domain.manageroverride.MAXManagerOverride; //ADDED PACKAGE FOR MANAGER OVERRIDE

/**
 * The UtilityManager implements utility methods used in the POS application.
 * Prior to release 4.5.0, these methods were implemented as static methods in
 * oracle.retail.stores.pos.services.common.DefaultSite.
 * <P>
 * These methods are generally used to initiate and save transaction. Those
 * activities are used in several places in the application, and the
 * UtilityManager provides a single, extendable implementation point for those
 * actions.
 * <P>
 * Typically, this manager won't be associated with a technician.
 */
public class MAXUtilityManager extends UtilityManager implements MAXUtilityManagerIfc
{
    /** The logger to which log messages will be sent. */
    private static final Logger logger = Logger.getLogger(UtilityManager.class);

    /**
     * The list of supported countries
     */
    protected static CountryIfc[] countries = null;

    /**
     * The list of supported locales
     */
    protected static Locale[] supportedLocales = null;

    /**
     * current UI locale
     */
    protected static Locale currentUILocale = null;

    /**
     * The collection of credit card objects.
     */
    protected static CardType singletonCardType = null;

    /**
     * The flat file configuration for the credit card objects.
     */
    protected static String cardTypeRulesFile = "";

    /**
     * IMEI Enabled/Disabled Property name
     */
    protected static final String IMEIProperty = "IMEIEnabled";

    /**
     * IMEI Enabled/Disabled Property name
     */
    protected static final String SerializationProperty = "SerializationEnabled";

    /**
     * IMEI Enabled/Disabled Property name
     */
    protected static final String IMEIFieldLengthProperty = "IMEIFieldLength";
    
    protected static final String MANUAL_ENTRY_ID = "ManualEntryID";
	
    protected static final String EMPTY_SALESASSOCIATE = "EMPTYASSOC";
    
    protected static MAXCodeListMapIfc codeListMap = null;
    
    protected static Hashtable taxGroupTaxRulesMapping = new Hashtable();

    /**
     * The default constructor; sets up a unique address.
     * 
     * @exception IllegalStateException is thrown if the manager cannot be
     *                created.
     */
    public MAXUtilityManager()
    {
        getAddress();
    }
  //Changes for manager override start
    
    /**
	 * Map containing ManagerOverride data for the transaction..
	 */
	private static HashMap managerOverrideMap = new HashMap();

	private ArrayList allTenderTypes = new ArrayList();

	/**
	 * @return the managerOverrideMap
	 */
	public HashMap getManagerOverrideMap() {
		return managerOverrideMap;
		
	}
	/**
	 * @param managerOverrideMap the managerOverrideMap to set
	 */
	public void setManagerOverrideMap(HashMap managerOverrideMap) {
		MAXUtilityManager.managerOverrideMap = managerOverrideMap;
		//System.out.println("Going inside MAXUtilityManager");

	}
	/**
	 * Updates ManagerOverride Map with Seq No, Function Id, Item Id..
	 * 
	 */
	public void updateManagerOverrideMap(int functionId, String entry) {
		if (null != getManagerOverrideMap()) {
			HashMap tempMap = getManagerOverrideMap();

			// Temp obj to set Item Id in Class obj..
			MAXManagerOverride obj = new MAXManagerOverride();

			obj.setFeatureId(String.valueOf(functionId));
			if (MAXRoleFunctionIfc.ITEM_DELETE == functionId || MAXRoleFunctionIfc.CREATE_ITEM == functionId) {
				obj.setItemId(entry);
			} else if (MAXRoleFunctionIfc.RETURN_WITHOUT_RECEIPT == functionId
					|| MAXRoleFunctionIfc.RETURN_NON_RETRIEVAL == functionId) {
			} else if (MAXRoleFunctionIfc.ACCEPT_INVALID_CREDIT_NOTE == functionId) {
				obj.setStoreCreditId(entry);
			} else if (RoleFunctionIfc.TENDER_LIMIT == functionId) {
				// Do Nothing
			}
			// Changes start for Price and Discount manager override 
			else if (RoleFunctionIfc.PRICE_OVERRIDE == functionId) {
				obj.setItemId(entry);
			} else if (RoleFunctionIfc.DISCOUNT == functionId) {
				obj.setItemId(entry);
			}
			// Changes End for Price and Discount manager override
			 else if (MAXRoleFunctionIfc.ACCEPT_SUSPEND_TRANSACTION == functionId) {
				// Do Nothing
			} else if (MAXRoleFunctionIfc.ACCEPT_MALL_CERT_TENDER == functionId) {

			}
			// Setting the Obj to Hashmap.
			
			else if (MAXRoleFunctionIfc.CUSTOMER_LINK == functionId) {
				// Do Nothing
			} // MAX Changes for COD BS
			else if (MAXRoleFunctionIfc.EDIT_DELIVERY_DATE_SLOT == functionId) {
				// Do Nothing
			} else if (MAXRoleFunctionIfc.GDMS_OFFLINE == functionId) {
				// Do Nothing
			} else if (MAXRoleFunctionIfc.ACCEPT_MANAUL_CREDIT_NOTE == functionId) {
				obj.setStoreCreditId(entry);
			} else if (MAXRoleFunctionIfc.ACCEPT_EXPIRED_CREDIT_NOTE == functionId) {
				obj.setStoreCreditId(entry);
			}

			// Setting the Obj to Hashmap.
			
			tempMap.put(tempMap.size() + 1, obj);

			// Updating the Manager with updated Hashmap.
			setManagerOverrideMap(tempMap);
		}
	}

	/**
	 * Map containing ManagerOverride data for the transaction..
	 */
	private static HashMap attributeMap = new HashMap();
	private static HashMap tenderedAmountMap = new HashMap();
	private static HashMap totalAmountMap = new HashMap();
	private static boolean hasDiscount = false;

	public HashMap getTenderedAmountMap() {
		return tenderedAmountMap;
	}

	public HashMap getTotalAmountMap() {
		return totalAmountMap;
	}

	public void setTenderedAmountMap(HashMap tenderedAmountMap) {
		this.tenderedAmountMap = tenderedAmountMap;
	}

	public void setTotalAmountMap(HashMap totalAmountMap) {
		this.totalAmountMap = totalAmountMap;
	}

	//Changes for manager override ends
    /**
     * Updates the store status from the database.
     * 
     * @param status The last known store status
     * @return The store status
     * @exception DataException upon error
     */
    public StoreStatusIfc refreshStoreStatus(StoreStatusIfc status) throws DataException
    {
        try
        {
            // This would be handled better if we had asynchronous
            // notification of a change in store status.
            // Since we currently don't have this, we may need to update
            // the store status twice. Once if we think the store is
            // opened and a second time if that business day is closed.
            // This covers the possibility that someone has closed what
            // we think is the current business day and then opened a
            // new business day.
            StoreDataTransaction dt = null;

            dt = (StoreDataTransaction) DataTransactionFactory.create(DataTransactionKeys.STORE_DATA_TRANSACTION);

            if (status.getStatus() == AbstractStatusEntityIfc.STATUS_OPEN)
            {
                // confirm the store status
                dt.refreshStoreStatus(status);
            }

            if (status.getStatus() != AbstractStatusEntityIfc.STATUS_OPEN)
            {
                // Check to see if a new business day has been opened
                status = dt.readStoreStatus(status.getStore().getStoreID());
            }
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }

        return (status);
    }

    /**
     * Checks for a change in UI locale
     * 
     * @return boolean Returns true if the current UI has changed false
     *         otherwise
     */
    public boolean hasUILocaleChanged()
    {
        return !LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE).equals(currentUILocale);
    }

    /**
     * Retrieves the list of supported countries and its Administrative Regions
     * as specified on the application.xml
     * 
     * @param pm the Parameter Manager
     * @return array of countries
     */
    public CountryIfc[] getCountriesAndStates(ParameterManagerIfc pm)
    {
        if (countries == null || hasUILocaleChanged())
        {
            currentUILocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
            // get list of countries from parameterManager
            Serializable[] stateCodes = null;
            Serializable[] countryCodes = null;

            // Retrieve the list of supported countries from the system
            // parameters
            try
            {

                ParameterIfc storeCountry = pm.getSource().getParameter("StoreCountry");

                if (storeCountry.getValidator() instanceof EnumeratedListValidator)
                {
                    countryCodes = ((EnumeratedListValidator) storeCountry.getValidator()).getAllowableValues();
                }

                // Retrieve the list of states for the supported countries from
                // the system parameters
                ParameterIfc storeStates = pm.getSource().getParameter("StoreStateProvince");
                if (storeStates.getValidator() instanceof EnumeratedListValidator)
                {
                    stateCodes = ((EnumeratedListValidator) storeStates.getValidator()).getAllowableValues();
                }
                
                if (countryCodes != null)
                {
                    countries = new CountryIfc[countryCodes.length];
    
                    for (int i = 0; i < countryCodes.length; i++)
                    {
                        CountryIfc countryInfo = getCountryProperties((String) countryCodes[i]);
                        String countryName = retrieveText("Common", BundleConstantsIfc.LOCALIZATION_BUNDLE_NAME,
                                (String) countryCodes[i], (String) countryCodes[i]);
                        countryInfo.setCountryName(countryName);
    
                        if (stateCodes != null)
                        {
                            StateIfc[] stateList = null;
                            ArrayList<StateIfc> alist = new ArrayList<StateIfc>();
                            for (int j = 0; j < stateCodes.length; j++)
                            {
                                if (((String) stateCodes[j]).startsWith((String) countryCodes[i]))
                                {
                                    StateIfc aState = DomainGateway.getFactory().getStateInstance();
                                    // Remove the appended country and store
                                    // only the ISO code for the state
                                    aState.setStateCode(((String) stateCodes[j]).substring(3, ((String) stateCodes[j])
                                            .length()));
                                    String stateName = retrieveText("Common", BundleConstantsIfc.LOCALIZATION_BUNDLE_NAME,
                                            (String) stateCodes[j], (String) stateCodes[j]);
                                    aState.setStateName(stateName);
                                    aState.setCountryCode((String) countryCodes[i]);
                                    alist.add(aState);
                                }
                            }
                            // sort the state list based on state name
                            Collections.sort(alist, new Comparator<StateIfc>()
                            {
                                public int compare(StateIfc o1, StateIfc o2)
                                {
                                    return LocaleUtilities.compareValues(o1.getStateName(), o2.getStateName());
                                }
                            });
                            stateList = new StateIfc[alist.size()];
                            alist.toArray(stateList);
                            countryInfo.setStates(stateList);
                            countries[i] = countryInfo;
                        }
                    }
                }
            }
            catch (ParameterException e)
            {
            }

        }
        return countries;
    }

    /**
     * Retrieves the country properties from the property file
     * 
     * @param country the country code
     * @return The country
     */
    protected CountryIfc getCountryProperties(String country)
    {
        // ask it to read the countries properties

        CountryIfc countryInfo = DomainGateway.getFactory().getCountryInstance();

        // use default values if properties are not defined for the specified
        // country
        countryInfo.setCountryCode(country);
        countryInfo.setPhoneFormat(DomainGateway.getProperty(country + LocaleConstantsIfc.PHONE_MASK, null));
        countryInfo.setPostalCodeFormat(DomainGateway.getProperty(country + LocaleConstantsIfc.POSTAL_MASK, null));
        countryInfo.setExtPostalCodeFormat(DomainGateway
                .getProperty(country + LocaleConstantsIfc.EXT_POSTAL_MASK, null));

        String postalRequired = DomainGateway.getProperty(country + LocaleConstantsIfc.POSTAL_CODE_REQUIRED, "false");
        countryInfo.setPostalCodeRequired(Boolean.valueOf(postalRequired));

        String extPostalRequired = DomainGateway.getProperty(country + LocaleConstantsIfc.EXT_POSTAL_CODE_ENABLED,
                "false");
        countryInfo.setExtPostalCodeRequired(Boolean.valueOf(extPostalRequired));
        countryInfo.setPostalCodeDelimiter(DomainGateway.getProperty(
                country + LocaleConstantsIfc.POSTAL_CODE_DELIMITER, ""));
        countryInfo.setAddressDelimiter(DomainGateway.getProperty(country + LocaleConstantsIfc.ADDRESS_DELIMITER, ""));

        return countryInfo;
    }
    
    //Changes for manager override by Anuj Singh
    
    //private static HashMap managerOverrideMap = new HashMap();
    
    private ArrayList allTendertypes = new ArrayList();
    
	/*
	 * public HashMap getManagerOverrideMap() { return managerOverrideMap; }
	 * 
	 * public void setManagerOverrideMap(HashMap managerOverrideMap) {
	 * this.managerOverrideMap=managerOverrideMap; }
	 */
    /**
     * Retrieves the index in the country array given the country code
     * 
     * @param code the country code
     * @param pm parameter manager reference
     * @return index in the country array
     */
    public int getCountryIndex(String code, ParameterManagerIfc pm)
    {
        int index = 0;
        if (getCountriesAndStates(pm) != null)
        {
            for (int i = 0; i < countries.length; i++)
            {
                if (code.equals(countries[i].getCountryCode()))
                {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    /**
     * Retrieves the index in the state array given the state code
     * 
     * @param countryIndex country index
     * @param code the state code
     * @param pm parameter manager reference
     * @return index in the state array
     */
    public int getStateIndex(int countryIndex, String code, ParameterManagerIfc pm)
    {
        int index = 0;
        if (getCountriesAndStates(pm) != null)
        {
            StateIfc[] states = countries[countryIndex].getStates();
            for (int i = 0; i < states.length; i++)
            {
                if (code.equals(states[i].getStateCode()))
                {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    /**
     * Retrieves text through international text support facility for specified
     * spec name, bundle name and property. Implements default if property not
     * found.
     * 
     * @param specName bean specification name
     * @param bundleName bundle in which to search for answer
     * @param propName property key
     * @param defaultValue default value
     * @return text from support facility
     */
    public String retrieveText(String specName, String bundleName, String propName, String defaultValue)
    {
        return retrieveText(specName, bundleName, propName, defaultValue, LocaleConstantsIfc.USER_INTERFACE);
    }

    /**
     * Retrieves text through international text support facility for specified
     * spec name, bundle name and property. Implements default if property not
     * found.
     * 
     * @param specName bean specification name
     * @param bundleName bundle in which to search for answer
     * @param propName property key
     * @param defaultValue default value
     * @param subsystem the subsystem which determines which locale to use
     * @return text from support facility
     */
    public String retrieveText(String specName, String bundleName, String propName, String defaultValue,
            String subsystem)
    {
        Locale locale = LocaleMap.getLocale(subsystem);
        return (retrieveText(specName, bundleName, propName, defaultValue, locale));
    }

    /**
     * Retrieves text through ResourceBundleUtil facility for specified spec
     * name, bundle name and property. Implements default if property not found.
     * 
     * @param specName bean specification name
     * @param bundleName bundle in which to search for answer
     * @param propName property key
     * @param defaultValue default value
     * @param locale the locale used to retrieve the bundle
     * @return text from support facility
     */
    public String retrieveText(String specName, String bundleName, String propName, String defaultValue, Locale locale)
    {
        Properties props = null;
        if (Util.isObjectEqual(bundleName, BundleConstantsIfc.COMMON_BUNDLE_NAME))
        {
            props = getBundleProperties(specName, BundleConstantsIfc.COMMON_BUNDLE_NAME, locale);
        }
        else
        {
            // use multiple bundles to include common
            String bundles[] = { BundleConstantsIfc.COMMON_BUNDLE_NAME, bundleName };
            props = getBundleProperties(specName, bundles, locale);
        }

        String returnValue = null;

        // Adding brakets to the property names help us
        // determine if text is comming from bundles or the
        // default values. To activate the BUNDLE_TESTING flag
        // the application has to be run with
        // -DBUNDLE_TESTING
        String testPropName = "<" + propName + ">";
        boolean testingBundles = (System.getProperty("BUNDLE_TESTING") != null);
        if (props == null)
        {
            if (testingBundles)
            {
                returnValue = testPropName;
            }
            else
            {
                returnValue = defaultValue;
            }

        }
        else
        {
            if (testingBundles)
            {
                returnValue = props.getProperty(propName, testPropName);
            }
            else
            {
                returnValue = props.getProperty(propName, defaultValue);
            }

        }
        return (returnValue);
    }

    /**
     * Retrieves a handle to the bundle properties
     * 
     * @param tag bean key name
     * @param bundle bundle in which to search for answer
     * @param locale the locale used to retrieve the bundle
     * @return Properties handle to the bundle
     */
    public Properties getBundleProperties(String tag, String bundle, Locale locale)
    {
        Locale bestMatchLocale = LocaleMap.getBestMatch(locale);
        Properties props = ResourceBundleUtil.getGroupText(tag, bundle, bestMatchLocale);
        return props;
    }

    /**
     * Retrieves a handle to the bundle properties
     * 
     * @param tag bean key name
     * @param bundles bundles in which to search for answer
     * @param locale the locale used to retrieve the bundle
     * @return Properties handle to the bundle
     */
    public Properties getBundleProperties(String tag, String[] bundles, Locale locale)
    {
        Locale bestMatchLocale = LocaleMap.getBestMatch(locale);
        Properties props = ResourceBundleUtil.getGroupText(tag, bundles, bestMatchLocale);
        return props;
    }

    /**
     * Retrieves journal text through international text support facility.
     * 
     * @param propName property key
     * @param defaultValue default value
     * @return text from support facility
     */
    public String retrieveJournalText(String propName, String defaultValue)
    {
        return (retrieveText("JournalEntry", BundleConstantsIfc.EJOURNAL_BUNDLE_NAME, propName, defaultValue,
                LocaleConstantsIfc.JOURNAL));
    }

    /**
     * Retrieves report text through international text support facility.
     * 
     * @param propName property key
     * @param defaultValue default value
     * @return text from support facility
     */
    public String retrieveReportText(String propName, String defaultValue)
    {
        return (retrieveText("ReportSpec", BundleConstantsIfc.REPORTS_BUNDLE_NAME, propName, defaultValue,
                LocaleConstantsIfc.REPORTS));
    }

    /**
     * Retrieves dialog text through international text support facility.
     * 
     * @param propName property key
     * @param defaultValue default value
     * @return text from support facility
     */
    public String retrieveDialogText(String propName, String defaultValue)
    {
        return (retrieveText(POSUIManagerIfc.DIALOG_SPEC, BundleConstantsIfc.DIALOG_BUNDLE_NAME, propName,
                defaultValue, LocaleConstantsIfc.USER_INTERFACE));
    }

    /**
     * Retrieves dialog text through international text support facility.
     * 
     * @param propName property key
     * @return text from support facility
     */
    public String retrieveCommonText(String propName)
    {
        return retrieveCommonText(propName, propName);
    }

    /**
     * Retrieves dialog text through international text support facility.
     * 
     * @param propName property key
     * @param defaultValue default value
     * @return text from support facility
     */
    public String retrieveCommonText(String propName, String defaultValue)
    {
        return (retrieveText("Common", BundleConstantsIfc.COMMON_BUNDLE_NAME, propName, defaultValue,
                LocaleConstantsIfc.USER_INTERFACE));
    }

    /**
     * Retrieves common text through international text support facility.
     * 
     * @param propName property key
     * @param defaultValue default value
     * @param subsystem id that identifies the subsystem retrieving the message
     * @return text from support facility
     */
    public String retrieveCommonText(String propName, String defaultValue, String subsystem)
    {
        return (retrieveText("Common", BundleConstantsIfc.COMMON_BUNDLE_NAME, propName, defaultValue, subsystem));
    }

    /**
     * Retrieves common text through international text support facility.
     * 
     * @param propName property key
     * @param defaultValue default value
     * @param locale the locale of the desired text
     * @return text from support facility
     */
    public String retrieveCommonText(String propName, String defaultValue, Locale locale)
    {
        return (retrieveText("Common", BundleConstantsIfc.COMMON_BUNDLE_NAME, propName, defaultValue, locale));
    }

    /**
     * Retrieve translation for supported Locales
     * 
     * @param localeKey
     * @param defaultValue
     * @param locale
     * @return
     */
    public String getLocaleDisplayName(String localeKey, String defaultValue, Locale locale)
    {
        return (retrieveCommonText(localeKey, defaultValue, locale));
    }

    /**
     * Retrieves line-display text through international text support facility.
     * 
     * @param propName property key
     * @param defaultValue default value
     * @return text from support facility
     */
    public String retrieveLineDisplayText(String propName, String defaultValue)
    {
        return (retrieveText(BundleConstantsIfc.LINE_DISPLAY_SPEC, BundleConstantsIfc.LINE_DISPLAY_BUNDLE_NAME,
                propName, defaultValue, LocaleConstantsIfc.POLE_DISPLAY));
    }

    /**
     * Validates the check digit (last element in String number) according to
     * the algorithm mapped to posFunction. If String posFunction is not mapped
     * to a CheckDigitStrategy in CheckDigitUtility, this method returns true
     * and no validation is performed.
     * 
     * @param posFunction - the name of the pos function requesting validation
     * @param number - a numeric String, with last element containing the check
     *            digit
     * @return boolean true if the check digit is valid for the given function
     *         or if the function is not configured in the CheckDigitUtility
     * @see oracle.retail.stores.pos.utility.CheckDigitUtility
     */
    public boolean validateCheckDigit(String posFunction, String number)
    {
        boolean valid = true;
        CheckDigitUtility util = CheckDigitUtility.getInstance();

        if (util.isConfigured(posFunction))
        {
            valid = util.validateCheckDigit(posFunction, number.getBytes());
        }

        return valid;
    }

    /**
     * Validates the check digit (last element in String number) according to
     * the algorithm mapped to posFunction. If String posFunction is not mapped
     * to a CheckDigitStrategy in CheckDigitUtility, this method returns true
     * and no validation is performed.
     * 
     * @param posFunction - the name of the pos function requesting validation
     * @param cardData - Instance of the EncipheredCardData
     * @return boolean true if the check digit is valid for the given function
     *         or if the function is not configured in the CheckDigitUtility
     * @see oracle.retail.stores.pos.utility.CheckDigitUtility
     */
    public boolean validateCheckDigit(String posFunction, EncipheredCardDataIfc cardData)
    {
        boolean valid = false;
        if (cardData != null && (cardData.isCheckDigitEvaluated() == false))
        {
            KeyStoreEncryptionManagerIfc encryptionManager = (KeyStoreEncryptionManagerIfc) Gateway.getDispatcher()
                    .getManager(KeyStoreEncryptionManagerIfc.TYPE);
            byte[] cardNumber = null;
            try
            {
                cardNumber = encryptionManager.decrypt(Base64
                        .decodeBase64(cardData.getEncryptedAcctNumber().getBytes()));
            }
            catch (EncryptionServiceException ese)
            {
                logger.error("Couldn't decrypt card number", ese);
            }
            CheckDigitUtility util = CheckDigitUtility.getInstance();

            if (util.isConfigured(posFunction))
            {
                valid = util.validateCheckDigit(posFunction, cardNumber);
            }
            else
            {
                // were not checking the check digit.
                valid = true;
            }
            // clear the cardNumber
            Util.flushByteArray(cardNumber);
            cardData.setCheckDigitValid(valid);
        }
        else if (cardData != null)
        {
            valid = cardData.isCheckDigitValid();
        }
        return valid;
    }

    /**
     * Retrieves the string corresponding to the error represented by errorCode
     * translated into the Locale specified for the user interface.
     * 
     * @param errorCode int representing the error that has occurred. The
     *            {@link oracle.retail.stores.foundation.manager.data.DataException}
     *            class has the corresponding error code constants.
     * @return String containing the corresponding error message for the given
     *         errorCode and translated per the Locale for the user interface.
     * @see oracle.retail.stores.foundation.manager.data.DataException
     */
    public String getErrorCodeString(int errorCode)
    {
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        String errorCodeIntString = LocaleUtilities.formatNumber(errorCode, locale);
        String errorCodeString = this.retrieveText("Error", BundleConstantsIfc.COMMON_BUNDLE_NAME, errorCodeIntString,
                "An unknown exception occurred.");

        return errorCodeString;

    }

    /**
     * Set the filename of the card rules file.
     * 
     * @param filename the String filename
     */
    public void setCardTypeRulesFile(String filename)
    {
        cardTypeRulesFile = filename;
    }

    /**
     * Return the filename of the card rules file.
     * 
     * @return the filename of the flat file rules
     */
    public String getCardTypeRulesFile()
    {
        return cardTypeRulesFile;
    }

    /**
     * Return a configured CardType through the CardType utility.
     * 
     * @return a configured CardType object
     */
    public CardType getConfiguredCardTypeInstance()
    {
        CardTypeUtility util = CardTypeUtility.getInstance(getCardTypeRulesFile());
        return util.getCardTypeInstance();
    }

    /**
     * Given a card number, attempt to find out what type of credit (VISA, MC,
     * etc)
     * 
     * @param cardData The EncipheredCardData instance to test
     * @return The appropriate enumerated credit type.
     */
    public CreditTypeEnum determineCreditType(EncipheredCardDataIfc cardData)
    {
        CardTypeIfc cardTypeUtility = getConfiguredCardTypeInstance();

        // return the card type
        String cardType = cardTypeUtility.identifyCardType(cardData, TenderTypeEnum.CREDIT.toString());
        return CreditTypeEnum.makeEnumFromString(cardType);
    }

    /**
     * This method returns an employee id string after being given a prompt and
     * response model.
     * 
     * @param pAndRModel
     * @return employeeID string
     */
    public String getEmployeeFromModel(PromptAndResponseModel pAndRModel)
    {
        String employeeID = null;
        MSRModel msrModel = pAndRModel.getMSRModel();
        // we are using the credit card as an employee id card
        // see if the surname is not null, it is not equal to empty string
        // and the first chaacter is ' '
        // if it is set surname to " "
        if (msrModel.getSurname() == null || msrModel.getSurname().equals(""))
        {
            employeeID = " ";
        }
        else
        if (msrModel.getSurname().length() > 10)
        {
            employeeID = msrModel.getSurname().substring(0, 10);
        }
        else
        {
            employeeID = msrModel.getSurname();
        }
        return employeeID;
    }

    /**
     * If we are in training mode, this method will check the
     * SendTrainingModeTransactionToJournal parameter to see if it set to Y. If
     * it is set to Y, the training mode transaction should be written to the
     * e-journal
     * 
     * @param trainingModeOn - set to true for training mode
     * @return true if the transaction should be written to the e-journal
     */
    public boolean journalTransaction(boolean trainingModeOn)
    {
        boolean journalOn = true;
        // If in training mode then check to see if
        // SendTrainingModeTransactionsToJournal parameter
        // is set to Yes to journal training mode transactions
        if (trainingModeOn)
        {
            // get parameter manager
            UtilityIfc util = null;
            try
            {
                util = Utility.createInstance();
            }
            catch (ADOException e)
            {
                String message = "Configuration problem: could not instantiate UtilityIfc instance";
                logger.error(message, e);
                throw new RuntimeException(message, e);
            }
            journalOn = util.getParameterValue("SendTrainingModeTransactionsToJournal", "Y").equalsIgnoreCase("Y");
        }
        return (journalOn);
    }

    /**
     * Returns the a Locale Requestor with all potentially required locales
     * 
     * @return A locale requestor object with all required locales.
     */
    public LocaleRequestor getRequestLocales()
    {
        LocaleRequestor localeRequestor = LocaleMap.getSupportedLocaleRequestor();
        return localeRequestor;
    }

    /**
     * Returns reason code text given a String constant and int reason code.
     * 
     * @param String codeConstant value
     * @param int reasonCode value
     * @return reason code text
     * @see oracle.retail.stores.domain.utility.CodeConstantsIfc
     */
    public String getReasonCodeText(CodeListIfc list, int reasonCode)
    {
        String value = "";
        if (list != null)
        {
            CodeEntryIfc entry = list.findListEntryByCode(Integer.toString(reasonCode));
            if (entry != null)
            {
                String text = entry.getText(LocaleMap.getLocale(LocaleMap.DEFAULT));
                if (text != null)
                {
                    value = text;
                }
            }
        }

        return value;
    }

    /**
     * Returns phone format for a country defined in domain.properties
     * 
     * @param String countryCode
     * @return PhoneMask corresponding to countryCode
     */
    public String getPhoneFormat(String countryCode)
    {
        String phoneFormat = DomainGateway.getProperty(countryCode + LocaleConstantsIfc.PHONE_MASK, "");
        return phoneFormat;
    }

    /**
     * Returns regular expression validation string for a country defined in
     * domain.properties
     * 
     * @param String countryCode
     * @return PhoneValidationRegexp corresponding to countryCode
     */
    public String getPhoneValidationRegexp(String countryCode)
    {
        String validationRegexp = DomainGateway.getProperty(countryCode + ".PhoneValidationRegexp", "");
        return validationRegexp;
    }

    /**
     * Returns the regular expression used for email address format validation,
     * defined in domain.properties
     * 
     * @return the regex pattern
     */
    public String getEmailValidationRegexp()
    {
        String validationRegexp = DomainGateway.getProperty("EmailValidationRegexp", "");
        return validationRegexp;
    }

    /**
     * get a phone number formatted as per the phone pattern for that country
     * 
     * @return formatted phone number
     */
    public String getFormattedNumber(String phoneNumber, String countryCode)
    {
        String formatted = phoneNumber;
        try
        {
            String phoneMask = getPhoneFormat(countryCode);
            if (phoneMask != null && phoneMask.length() > 0)
            {
                MaskFormatter mf = new MaskFormatter(phoneMask);
                mf.setValueContainsLiteralCharacters(false);

                JFormattedTextField ftf = new JFormattedTextField();
                DefaultFormatterFactory factory = new DefaultFormatterFactory(mf);
                ftf.setFormatterFactory(factory);

                ftf.setValue(formatted);
                formatted = ftf.getText();
            }
        }
        catch (ParseException ex)
        {
            logger.debug("ParseException ex", ex);
        }

        return formatted;
    }

    /**
     * Returns postalcode format for a country defined in domain.properties
     * 
     * @param String countryCode
     * @return PostalCodeMask corresponding to countryCode
     **/
    public String getPostalCodeFormat(String countryCode)
    {
        String phoneFormat = DomainGateway.getProperty(countryCode + LocaleConstantsIfc.POSTAL_MASK, "");
        return phoneFormat;
    }

    /**
     * Returns regular expression validation string for a country defined in
     * domain.properties
     * 
     * @param String countryCode
     * @return PostalCodeValidationRegexp corresponding to countryCode
     **/
    public String getPostalCodeValidationRegexp(String countryCode)
    {
        String validationRegexp = DomainGateway.getProperty(countryCode + ".PostalCodeValidationRegexp", "");
        return validationRegexp;
    }

   /**
     * Gets the IMEI Enabled/Disabled property
     * 
     * @return boolean
     */
    public boolean getIMEIProperty()
    {
        return (Gateway.getBooleanProperty("application", IMEIProperty, false));
    }

    /**
     * Gets the Serialisation Enabled/Disabled property
     * 
     * @return boolean
     */
    public boolean getSerialisationProperty()
    {
        return (Gateway.getBooleanProperty("application", SerializationProperty, false));
    }

    /**
     * Gets the IMEI Field Length Property
     * 
     * @return boolean
     */
    public String getIMEIFieldLengthProperty()
    {
        return (Gateway.getProperty("domain", IMEIFieldLengthProperty, "15"));
    }
	// Changes starts for rev 1.0
	public String getReasonCodeText(CodeListIfc list, String reasonCode)
    {
    	String      value   = "";
    	if (list != null)
    	{
    		CodeEntryIfc entry = list.findListEntryByCode(reasonCode);
    		if (entry != null)
    		{
    			String text = entry.getText(LocaleMap.getLocale(LocaleMap.DEFAULT));
    			if (text != null)
    			{
    				value = text;
    			}
    		}
    	}

    	return value;
    }

// Changes ends for Rev 1.0

    /**
     * This method is used to create dialog box specific for data/queue
     * exceptions
     * 
     * @param ex DataException
     * @return DialogBeanModel
     */
    public DialogBeanModel createErrorDialogBeanModel(DataException ex)
    {
        return createErrorDialogBeanModel(ex, true);
    }

    /**
     * This method is used to create dialog box specific for certain data/queue
     * exceptions
     * 
     * @param ex DataException
     * @param defaultModel boolean
     * @return DialogBeanModel
     */
    public DialogBeanModel createErrorDialogBeanModel(DataException ex, boolean defaultModel)
    {
        String errorString[];
        DialogBeanModel dialogModel = new DialogBeanModel();

        if (ex.getErrorCode() == DataException.QUEUE_FULL_ERROR)
        {
            errorString = new String[1];
            errorString[0] = getErrorCodeString(ex.getErrorCode());
            dialogModel.setResourceID("QueueFullError");
            dialogModel.setType(DialogScreensIfc.ERROR);
            dialogModel.setArgs(errorString);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "QueueFull");
        }
        else if (ex.getErrorCode() == DataException.STORAGE_SPACE_ERROR)
        {
            errorString = new String[1];
            errorString[0] = getErrorCodeString(DataException.STORAGE_SPACE_ERROR);
            dialogModel.setResourceID("QueueError");
            dialogModel.setType(DialogScreensIfc.RETRY);
            dialogModel.setArgs(errorString);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, "Retry");
        }
        else if (ex.getErrorCode() == DataException.QUEUE_OP_FAILED)
        {
            errorString = new String[1];
            errorString[0] = getErrorCodeString(ex.getErrorCode());
            dialogModel.setResourceID("DATABASE_ERROR_RETRY");
            dialogModel.setType(DialogScreensIfc.RETRY);
            dialogModel.setArgs(errorString);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, "Retry");
        }
        else if (defaultModel)
        {
            errorString = new String[2];
            errorString[0] = getErrorCodeString(ex.getErrorCode());
            errorString[1] = "";
            dialogModel.setResourceID("TranDatabaseError");
            dialogModel.setType(DialogScreensIfc.ERROR);
            dialogModel.setArgs(errorString);
        }

        return dialogModel;
    }
// Changes starts for rev 1.0
public String storeOpenTime()
	{
		return "";
	}
// Chnages ends for rev 1.0
// Chnages Starts for rev 1.1
public Vector getReasonCodeTextEntries(Vector reasonCodeKeys) {
		if (reasonCodeKeys == null)
			return null;

		// Use reasonCodeKeys as keys in the common text bundle in order to
		// pull out the proper text
		Vector returnText = null;
		int numTextEntries = reasonCodeKeys.size();
		returnText = new Vector(numTextEntries);
		for (int x = 0; x < numTextEntries; x++)
			returnText.add(retrieveCommonText((String) reasonCodeKeys.get(x)));

		// Return the result
		return returnText;
	}
// Changes starts for rev 1.3 (Ashish : Credit Card)
public CreditTypeEnum determineCreditType(String cardNumber)
{
    CardTypeIfc cardTypeUtility = getConfiguredCardTypeInstance();

    // return the card type
    String cardType = ((MAXCardTypeIfc)cardTypeUtility).identifyCardType(cardNumber, TenderTypeEnum.CREDIT.toString());
    return CreditTypeEnum.makeEnumFromString(cardType);
}
//Changes ends for rev 1.3 (Ashish : Credit Card)
public void initializeTransaction(TransactionIfc trans, BusIfc bus, long seq, String custID) { // begin

		// get cargo reference
		AbstractFinancialCargoIfc cargo = (AbstractFinancialCargoIfc) bus.getCargo();

		boolean writeTransactionToJournal = true;
		// always write exit training mode txn to journal
		if (trans.getTransactionType() != TransactionIfc.TYPE_EXIT_TRAINING_MODE) {
			writeTransactionToJournal = journalTransaction(cargo.getRegister().getWorkstation().isTrainingMode());
		}

		// Since we're about to start using a new transaction, index the
		// previous one in the journal. The transaction is indexed using the
		// transactionID(combination of storeID + RegisterID + sequenceNumber)
		// which is set to sequenceNumber in the JournalManager. Need to
		// change it

		// store loginid or employeeid based upon ManualEntryID parameter

		JournalFormatterManagerIfc formatter = null;
		JournalManagerIfc journal = null;
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		if (writeTransactionToJournal) {
			String meidParamValue = null;
			try {
				meidParamValue = pm.getStringValue(MANUAL_ENTRY_ID);
			} catch (ParameterException pe) {
				logger.error("" + pe.getMessage() + "");
				throw new IllegalStateException("ParameterException received in "
						+ "UtilityManager.initializeTransaction():" + pe.getMessage());
			}
			journal = (JournalManagerIfc) Gateway.getDispatcher().getManager(JournalManagerIfc.TYPE);
			formatter = (JournalFormatterManagerIfc) Gateway.getDispatcher()
					.getManager(JournalFormatterManagerIfc.TYPE);
			String sequenceNo = journal.getSequenceNumber();
			if (sequenceNo != null && sequenceNo != "") {
				indexTransactionInJournal(sequenceNo);
			}

			// set storeID,registerID,cashierID,salesAssociateId for journal
			journal.setStoreID(cargo.getStoreStatus().getStore().getStoreID());
			journal.setRegisterID(cargo.getRegister().getWorkstation().getWorkstationID());
			if (meidParamValue.equalsIgnoreCase("Employee")) {
				journal.setCashierID(cargo.getOperator().getEmployeeID());
			} else {
				journal.setCashierID(cargo.getOperator().getLoginID());
			}
			if (trans != null && trans.getSalesAssociate() != null
					&& trans.getSalesAssociate().getEmployeeID() != null) {
				if (trans.getSalesAssociate().getEmployeeID().equals("")) {
					// This is being done for Journal Index file so that
					// the file entry is correctly parsed.
					journal.setSalesAssociateID(EMPTY_SALESASSOCIATE);

				} else {
					if (meidParamValue.equalsIgnoreCase("Employee")) {
						journal.setSalesAssociateID(trans.getSalesAssociate().getEmployeeID());
					} else {
						journal.setSalesAssociateID(trans.getSalesAssociate().getLoginID());
					}
				}
			} else {
				if (meidParamValue.equalsIgnoreCase("Employee")) {
					journal.setSalesAssociateID(cargo.getOperator().getEmployeeID());
				} else {
					journal.setSalesAssociateID(cargo.getOperator().getLoginID());
				}
			}
			journal.setEntryType(JournalableIfc.ENTRY_TYPE_START);
			journal.setBusinessDayDate(cargo.getStoreStatus().getBusinessDate().dateValue().getTime());
		}

		// set workstation ID, business date
		trans.setCashier(cargo.getOperator());
		trans.setWorkstation(cargo.getRegister().getWorkstation());
		trans.setTillID(cargo.getRegister().getCurrentTillID());
		trans.setBusinessDay(cargo.getStoreStatus().getBusinessDate());
		trans.setTrainingMode(cargo.getRegister().getWorkstation().isTrainingMode());
		trans.setCustomerInfo(cargo.getCustomerInfo());

		// set the TenderLimits object for transaction level checking
		trans.setTenderLimits(cargo.getTenderLimits());
		if (trans.getTenderLimits() == null) {
			logger.error("Tender limits are null.");
			throw new NullPointerException("UtilityManager.initializeTransaction() - null TenderLimits reference.");
		}

		if (trans instanceof SaleReturnTransactionIfc) {
			((SaleReturnTransactionIfc) trans).setTransactionTax(getInitialTransactionTax(bus));

		} // end if SaleReturnTransaction

		// get a sequence number
		long sequenceNumber;

		if (seq == -1) {
			// Get the next sequence number
			sequenceNumber = cargo.getRegister().getNextTransactionSequenceNumber();
			try {
				writeHardTotals(bus);
			} catch (DeviceException e) {
				logger.error("Unable to update hard totals with next used transaction number: " + sequenceNumber, e);
			}
			if (trans.getTransactionStatus() == TransactionIfc.STATUS_UNKNOWN) {
				// this covers those places that create a transaction and don't
				// bother to set the status.
				trans.setTransactionStatus(TransactionIfc.STATUS_IN_PROGRESS);
			}
		} else {
			// Use the one supplied to us
			sequenceNumber = seq;
		}

		trans.setTransactionSequenceNumber(sequenceNumber);
		trans.buildTransactionID();
		trans.setTimestampBegin();
		trans.setReentryMode(cargo.getRegister().getWorkstation().isTransReentryMode());

		// If no customer has been linked reset subsystem defaults.
		if (Util.isEmpty(custID)) {
			// Set up default locales for pole display and receipt
			Locale defaultLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);

			LocaleMap.putLocale(LocaleConstantsIfc.RECEIPT, defaultLocale);
			LocaleMap.putLocale(LocaleConstantsIfc.POLE_DISPLAY, defaultLocale);
			LocaleMap.putLocale(LocaleConstantsIfc.DEVICES, defaultLocale);
		}
		if (seq == -1) {
			// If we did build a new one, log and journal the fact
			// that we have a new number
			if (logger.isInfoEnabled())
				logger.info("Transaction ID created:  " + trans.getTransactionID() + "");
			// write journal entry
			if (writeTransactionToJournal) {
				if (journal != null) {
					journal.setSequenceNumber(trans.getTransactionID());
					// journal customer ID
					if (custID != null) {
						StringBuffer journalTxt = new StringBuffer();

						journalTxt.append("Entering customer\n");
						journalTxt.append(formatter.toJournalString(trans, pm));

						// Merge changes from .v7x for CR 19560 to correct the
						// incorrect EJournal spacing for linked customer
						journalTxt.append("\nLink customer: ").append(custID);
						journal.journal(cargo.getOperator().getLoginID(), trans.getTransactionID(),
								journalTxt.toString());
					} else {
						if (!(trans instanceof OrderTransactionIfc)) {
							journal.journal(cargo.getOperator().getLoginID(), trans.getTransactionID(),
									formatter.toJournalString(trans, pm));
						} else {
							// Journal a "dummy" JournalableIfc.ENTRY_TYPE_START
							// entry so that a single transaction's entry can
							// be grouped together.
							journal.journal("");
						}
					}
					journal.setEntryType(JournalableIfc.ENTRY_TYPE_TRANS);
				} else {
					logger.error("No JournalManager found");
				}
			}
		}
	} // end initializeTransaction()

	public void saveTransaction(TransactionIfc trans) throws DataException {
		// Set the complete transaction journaling flag.
		saveTransaction(trans, true);
	}
	// Changes for Rev 1.2 Start
	public void saveTransaction(TransactionIfc trans, boolean journalEndOfTransaction) throws DataException
    {
        saveTransaction(trans, null, null, null, journalEndOfTransaction);
    }
	
	public void saveTransaction(TransactionIfc trans, FinancialTotalsIfc totals, TillIfc till, RegisterIfc register)
	        throws DataException
	    { // begin saveTransaction()

	        // Set the end of transaction journaling flag.
	        saveTransaction(trans, totals, till, register, true);

	    } // end saveTransaction()
	
	public void saveTransaction(
	        TransactionIfc trans,
	        FinancialTotalsIfc totals,
	        TillIfc till,
	        RegisterIfc register,
	        boolean journalEndOfTransaction)
	        throws DataException
	    { // begin saveTransaction()
	        // set transaction name and instantiate data transaction
	        TransactionWriteDataTransaction dbTrans = null;

	        if (trans.getTransactionType() == TransactionIfc.TYPE_CLOSE_REGISTER
	                || trans.getTransactionType() == TransactionIfc.TYPE_CLOSE_STORE)
	        {
	            dbTrans = (TransactionWriteDataTransaction) DataTransactionFactory.create(DataTransactionKeys.TRANSACTION_WRITE_NOT_QUEUED_DATA_TRANSACTION);
	        }
	        else
	        {
	            dbTrans = (TransactionWriteDataTransaction) DataTransactionFactory.create(DataTransactionKeys.TRANSACTION_WRITE_DATA_TRANSACTION);
	        }

	        try
	        {
	            if (trans.getTransactionStatus() == TransactionIfc.STATUS_IN_PROGRESS)
	            { // this covers those places that complete a transaction and
	                // don't bother to update the status.
	                trans.setTransactionStatus(TransactionIfc.STATUS_COMPLETED);
	            }
	            if (trans.getTimestampEnd() == null)
	            { // this covers those places that complete a transaction and
	                // don't bother to set the end timestamp.  Lookups for transaction export
	                // depends on this being set in the client so that the ordering of the
	                // the transactions in the extract is correct.
	            	trans.setTimestampEnd();
	            }
	            dbTrans.saveTransaction(trans, totals, till, register);
	        }
	        catch (DataException e)
	        {
	            logger.error("An error occurred saving the transaction: " + e + "");
	            throw e;
	        }

	        if (journalEndOfTransaction)
	        {
	            if (journalTransaction(trans.isTrainingMode()))
	            {
	                completeTransactionJournaling(trans);
	            }
	        }
	    } // end saveTransaction()
	
	public void completeTransactionJournaling(TransactionIfc trans)
    {
        // Add the Journal Footer to the this transaction
        JournalManagerIfc journal = (JournalManagerIfc) Gateway.getDispatcher().getManager(JournalManagerIfc.TYPE);
        Locale defaultLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
        StringBuffer journalTxt = new StringBuffer();
        journalTxt.append(trans.journalFooter(defaultLocale));
        journal.setEntryType(JournalableIfc.ENTRY_TYPE_END);
        journal.journal(journalTxt.toString());
        indexTransactionInJournal(journal.getSequenceNumber());
        journal.setEntryType(JournalableIfc.ENTRY_TYPE_NOTTRANS);
        journal.setSalesAssociateID("");
        journal.setCashierID("");
    }
	
	//Changes for Rev 1.2 Ends

	public MAXCodeListMapIfc getCodeListMap() { // begin getCodeListMap()
		return codeListMap;
	} // end getCodeListMap()

	public TransactionTaxIfc getInitialTransactionTax(BusIfc bus) { // begin
																	// getInitialTransactionTax()
																	// create a
																	// TransactionTax
																	// object
																	// with a
																	// default
																	// rate of 0
		TransactionTaxIfc tax = DomainGateway.getFactory().getTransactionTaxInstance();
		tax.setDefaultRate(0.0);

		// Attempt to get the default tax rate from the properties file
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

		try {
			double dbl = pm.getDoubleValue("TaxRate").doubleValue();

			if (dbl >= 0 && dbl <= 100) {
				// set the transaction tax rate to the default local tax rate
				// (TaxRate from the properties file divided by 100)
				tax.setDefaultRate(dbl / 100);
				Vector taxRules = (Vector) taxGroupTaxRulesMapping.get("-1");
				if (taxRules != null && !taxRules.isEmpty()) {
					TaxByLineRule rule = (TaxByLineRule) taxRules.elementAt(0);
					tax.setDefaultTaxRules(new NewTaxRuleIfc[] { (NewTaxRuleIfc) rule });
				}
			} else {
				logger.warn(getName() + ": " + "Tax rate parameter value is out of range: "
						+ "Value must be between 0.0000 and 100.0000;" + "Tax rate will default to 0.0");
			}

			CheckExternalTaxEnable(tax, pm);

		} catch (ParameterException pe) {
			logger.error("" + pe.getMessage() + "");
			throw new IllegalStateException(
					"ParameterException received in " + "UtilityManager.getInitialTransactionTax():" + pe.getMessage());
		}

		return tax;
	} // end getInitialTransactionTax()

	public String hasEmployeeDiscounts(SaleReturnTransactionIfc trans) {
		String foundEmployeeNumber = null;

		TransactionDiscountStrategyIfc[] discountArray = trans.getTransactionDiscounts(
				DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE, DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE);
		if (discountArray.length > 0) {
			foundEmployeeNumber = discountArray[0].getDiscountEmployeeID();
		} else {
			discountArray = trans.getTransactionDiscounts(DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT,
					DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE);
			if (discountArray.length > 0) {
				foundEmployeeNumber = discountArray[0].getDiscountEmployeeID();
			} else {
				// Check every line item until employee discount is found
				SaleReturnLineItemIfc[] lineItems = (SaleReturnLineItemIfc[]) (trans).getLineItems();
				EmployeeFound: for (int x = 0; x < lineItems.length && foundEmployeeNumber == null; x++) {
					// Check item discounts by amount
					ItemDiscountStrategyIfc[] discounts = lineItems[x].getItemDiscountsByAmount();
					for (int y = 0; y < discounts.length; y++) {
						if (!discounts[y].isAdvancedPricingRule()
								&& discounts[y].getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE) {
							foundEmployeeNumber = discounts[y].getDiscountEmployeeID();
							break EmployeeFound;
						}
					}
					if (foundEmployeeNumber == null) {
						discounts = lineItems[x].getItemDiscountsByPercentage();
						for (int y = 0; y < discounts.length; y++) {
							if (!discounts[y].isAdvancedPricingRule() && discounts[y]
									.getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE) {
								foundEmployeeNumber = discounts[y].getDiscountEmployeeID();
								break EmployeeFound;
							}
						}
					}
				}
			}
		}
		return foundEmployeeNumber;
	} // end hasEmployeeDiscounts

	public Vector getReasonCodeTextEntries(CodeListIfc codeList) {
		if (codeList == null)
			return null;
		Locale defaultLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
		return (getReasonCodeTextEntries(codeList.getTextEntries(defaultLocale)));
	}
	
	public void indexTransactionInJournal(String tid)
    { // begin indexTransactionInJournal()

        StringBuffer data = new StringBuffer();

        data.append(tid);
        data.append(" ");

        Locale defaultLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
        DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();
        Date now = new Date();
        //use the same date format for all locales in jnlindex.dat (ej saved in local filesystem) 
        //and journal (jl_enr) table for ej saved in database so that ej search will work for 
        //all locales.
        String date = dateTimeService.formatDate(now, defaultLocale, "MM/dd/yyyy");
        String time = dateTimeService.formatTime(now, defaultLocale, "HH:mm");
        data.append(date).append(" ").append(time).append(" ");

        JournalManagerIfc jmi = (JournalManagerIfc) Gateway.getDispatcher().getManager(JournalManagerIfc.TYPE);

        if (jmi != null)
        {
            data.append(jmi.getCashierID());
            data.append(" ");
            data.append(jmi.getSalesAssociateID());

            data.append(" ");
            data.append(jmi.getRegisterID());

            jmi.index(tid, data.toString());
            //jmi.setSequenceNumber("");
        }
    } // end indexTransactionInJournal()
	
	public void writeHardTotals(BusIfc bus) throws DeviceException
    { // begin writeHardTotals()
        // get cargo reference
        AbstractFinancialCargoIfc cargo = (AbstractFinancialCargoIfc) bus.getCargo();
        RegisterIfc register = cargo.getRegister();

        if ( (register!= null) && register.getWorkstation().isTrainingMode() )
        {
            //get non traning mode register
            register = register.getOtherRegister();
        }

        // Do not write hard totals if the register is in training mode
        if ( (register != null) && !register.getWorkstation().isTrainingMode())
        {
            // write hard totals
            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);

            // set up hard totals
            HardTotalsIfc ht = DomainGateway.getFactory().getHardTotalsInstance();
            ht.setStoreStatus(cargo.getStoreStatus());
            ht.setRegister(cargo.getRegister());
            ht.setLastUpdate();

            try
            {
                HardTotalsBuilderIfc builder = DomainGateway.getFactory().getHardTotalsBuilderInstance();
                ht.getHardTotalsData(builder);
                pda.writeHardTotals(builder.getHardTotalsOutput());
            }
            catch (HardTotalsFormatException htre)
            {
                DeviceException de = new DeviceException(0, "Hard Totals Data Format Error", htre);
                throw de;
            }
        }
    } // end writeHardTotals
	
	public void setCodeListMap(MAXCodeListMapIfc value)
    { // begin setCodeListMap()
        codeListMap = value;
    } // end setCodeListMap()
	
	private void CheckExternalTaxEnable(TransactionTaxIfc tax, ParameterManagerIfc pm)
    {
        //attempt to get the external tax package setting from the parameter
        // list
        try
        {
            String value = pm.getStringValue("UseExternalTaxPackage");

            if (value.equalsIgnoreCase("Y"))
            {
            	//Commented in code merging as TaxManagerIfc by Nitesh - Start 
            	
                /*//set the taxManager attribute on the TransactionTax object
                tax.setTaxManager((TaxManagerIfc) Gateway.getDispatcher().getManager(TaxManagerIfc.TYPE));

                if (tax.getTaxManager() != null)
                {
                    if (logger.isInfoEnabled())
                        logger.info("External Tax enabled.");
                }
                else
                {
                    logger.warn(
                        ""
                            + "External tax was not enabled: "
                            + " "
                            + "Check TaxManager configuration in conduit script."
                            + "");
                }*/
            	
            	//Commented in code merging as TaxManagerIfc by Nitesh - End
            }
            else
            {
                logger.warn(
                    getName()
                        + ": "
                        + "UseExternalTaxPackage parameter could not be read;"
                        + "Default tax rate will be used for tax calculations");
            }
        }
        catch (ParameterException pe)
        {
            logger.error("" + pe.getMessage() + "");
            throw new IllegalStateException(
                "ParameterException received in " + "UtilityManager.getInitialTransactionTax():" + pe.getMessage());
        }

    }

	public void initializeTransaction(TransactionIfc trans, BusIfc bus, long seq)
    {
        initializeTransaction(trans, bus, seq, null);
    }
	// Chnages ends for rev 1.1
	//Change for Rev 1.4 : Starts
	public void setCountries(){
    	countries=null;
    }
	//Change for Rev 1.4 : Ends
}
