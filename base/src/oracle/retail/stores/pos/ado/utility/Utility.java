/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/utility/Utility.java /main/19 2012/09/12 11:57:18 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  10/17/14 - Fixing wrongly used reason code type 
 *                         for capturing customer ID type.
 *    asinton   10/02/14 - Fix issue with parsing way in the future expiration
 *                         dates.
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    rrkohli   07/19/11 - encryption CR
 *    blarsen   06/22/11 - Added isOvertenderAllowed() method.
 *    blarsen   06/22/11 - Added another (overloaded) isStringListed method.
 *    mkutiana  02/22/11 - Added logic to get the correct password policy and
 *                         if fingerprints for login are allowed
 *    mchellap  07/30/10 - Added validation for customer id expiration date
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   05/11/10 - convert Base64 from axis
 *    cgreene   05/11/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    abondala  11/06/08 - updated files related to reason codes
 *
 * ===========================================================================
 * $Log:
 *  5    360Commerce 1.4         3/29/2007 3:59:50 PM   Michael Boyd    CR
 *       26172 - v8x merge to trunk
 *
 *
 *       5    .v8x      1.3.1.0     3/11/2007 4:51:18 PM   Brett J. Larsen CR
 *       4530 -
 *       adding support for retrieval of devault code list value - several
 *       site/model/beans do not work direclty with CodeList
 *  4    360Commerce 1.3         7/28/2006 5:36:00 PM   Brett J. Larsen CR
 *       4530: default reason code fix
 *       v7x->360Commerce merge
 *  3    360Commerce 1.2         3/31/2005 4:30:41 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:26:38 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:15:27 PM  Robert Pearse
 * $
 *
 *  4    .v7x      1.2.1.0     6/23/2006 4:43:05 AM   Dinesh Gautam   CR 4530:
 *       Fixed for reason code
 *
 * Revision 1.7  2004/09/15 16:34:22  kmcbride
 * @scr 5881: Deprecating parameter retrieval logic in cargo classes and logging parameter exceptions
 *
 * Revision 1.6  2004/07/14 18:47:09  epd
 * @scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation
 *
 * Revision 1.5  2004/07/12 21:30:34  bwf
 * @scr 6125 Made available expiration validation of debit before pin.
 *
 * Revision 1.4  2004/05/17 03:48:01  blj
 * @scr 4974 - resolution for this scr
 *
 * Revision 1.3  2004/02/12 16:47:58  mcs
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 21:12:55  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 * Revision 1.1.1.1 2004/02/11 01:04:11 cschellenger
 * updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.4 Feb 05 2004 13:24:20 rhafernik log4j conversion
 *
 * Rev 1.3 Jan 22 2004 14:05:40 khassen Resolution for scr3705: added empty
 * string entry in getIDTypes method. Resolution for 3705: Default selection
 * for ID Type is the previous selection; instead of blank selection
 *
 * Rev 1.2 Dec 17 2003 14:48:46 epd Refactorings to accommodate Unit testing
 *
 * Rev 1.1 Nov 07 2003 13:24:06 bwf Added validate license. Resolution for
 * 3429: Check/ECheck Tender
 *
 * Rev 1.0 Nov 04 2003 11:15:08 epd Initial revision.
 *
 * Rev 1.1 Oct 31 2003 16:46:40 epd added utility method to get ID types
 *
 * Rev 1.0 Oct 17 2003 12:39:08 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.utility;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Vector;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.manager.ifc.ValidationManagerIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.KeyStoreEncryptionManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.TourContext;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.context.ADOContextIfc;
import oracle.retail.stores.pos.ado.context.ContextFactory;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.utility.tdo.FingerprintPasswordPolicyTDO;
import oracle.retail.stores.pos.ado.utility.tdo.PasswordPolicyTDO;
import oracle.retail.stores.pos.ado.utility.tdo.PasswordPolicyTDOIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.tdo.TDOFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

/**
 * Container for ad-hoc utility methods to be used at ADO layer
 *
 */
public class Utility implements UtilityIfc
{
    /** Logger */
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.ado.utility.Utility.class);

    /** protected to enforce Singleton */
    protected Utility() {}

    protected static UtilityIfc instance = null;

    /**
     * Factory method that will attempt to return an instance of this class or
     * another implementation as specified by a property file
     *
     * @return
     */
    public static UtilityIfc createInstance() throws ADOException
    {
        if (instance != null)
        {
            return instance;
        }

        final String APP_PROP_GROUP = "application";
        final String UTILITY_KEY = "ado.utility";
        final String DEFAULT = Utility.class.getName();

        try
        {
            String className =
                Gateway.getProperty(APP_PROP_GROUP, UTILITY_KEY, DEFAULT);
            if (className.length() == 0)
            {
                throw new ADOException(
                    "Failed to find factory class for " + UTILITY_KEY);
            }
            Class utilityClass = Class.forName(className);
            instance = (UtilityIfc) utilityClass.newInstance();
            return instance;
        }
        catch (ADOException e)
        {
            throw e;
        }
        catch (ClassNotFoundException e)
        {
            throw new ADOException(
                "Factory Class not found for " + UTILITY_KEY,
                e);
        }
        catch (InstantiationException e)
        {
            throw new ADOException(
                "Failed to Instantiate factory for " + UTILITY_KEY,
                e);
        }
        catch (IllegalAccessException e)
        {
            throw new ADOException(
                "IllegalAccessException creating factory for " + UTILITY_KEY,
                e);
        }
        catch (NullPointerException e)
        {
            throw new ADOException(
                "Failed to find class for " + UTILITY_KEY,
                e);
        }
        catch (Throwable eth)
        {
            throw new ADOException(
                "Failed to create factory for " + UTILITY_KEY,
                eth);
        }

    }


    /*
     * Returns an instance of the oracle.retail.stores.pos.ado.utility.UtilityIfc class
     * @Return UtilityIfc
     */
    public static UtilityIfc getUtil()
    {
        UtilityIfc util;
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
        return util;
    }


    /**
     * Retrieves a parameter value from the parameter subsystem
     *
     * @param parameterName
     *            The parameter to retrieve
     * @param defaultValue
     *            A default in case the parameter cannot be read
     * @return The retrieved parameter value
     */
    public synchronized String getParameterValue(String parameterName, String defaultValue)
    {
        // get context
        ADOContextIfc context = getContext();
        // get parameter manager
        ParameterManagerIfc pm =
            (ParameterManagerIfc) context.getManager(ParameterManagerIfc.TYPE);
        String result = defaultValue;
        try
        {
            result = pm.getStringValue(parameterName);
        }
        catch (ParameterException e)
        {
            // Log the exception, this means we will return the
            // default value, so there should be no reason to
            // re-throw the exception
            //
            logger.warn(e);
        }
        return result;
    }

    /**
     * Retrieves a list of values given a parameter name.
     *
     * @param paramName
     *            The parameter to retrieve.
     * @return an array of values for the given parameter.
     */
    public synchronized String[] getParameterValueList(String paramName)
    {
        String[] result = new String[0]; // so as not to return a null;

        // get context
        BusIfc bus = TourContext.getInstance().getTourBus();
        // get parameter manager
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        try
        {
            // convert serializable[] to string[]
            Serializable[] values = pm.getParameterValues(paramName);
            result = new String[values.length];
            for (int i = 0; i < values.length; i++)
            {
                result[i] = (String) values[i];
            }
        }
        catch (ParameterException e)
        {
            logger.error(e);
        }

        return result;
    }

    /**
     * Returns true if the specified string is found in the list, false
     * otherwise
     * <P>
     *
     * @return boolean if string exists in list
     */
    public synchronized boolean isStringListed(String str, Object[] list)
    {
        boolean found = false;
        if (list != null)
        {
            for (int i = 0; i < list.length; i++)
            {
                if (str.equals(list[i]))
                {
                    found = true;
                    break;
                }
            }
        }
        return found;
    }

    /**
     * Gets the ID types from the config file
     *
     * @return Vector of ID types retrieved from the config file
     * @deprecated as of 13.1 Use UtilityManager.getReasonCodes
     */
    public synchronized Vector getIDTypes()
    {
        ADOContextIfc context = getContext();
        UtilityManagerIfc utility =
            (UtilityManagerIfc) context.getManager(UtilityManagerIfc.TYPE);
        String storeID = Gateway.getProperty("application", "StoreID", "");
        CodeListIfc list = utility.getReasonCodes(storeID, CodeConstantsIfc.CODE_LIST_CAPTURE_CUSTOMER_ID_TYPES);
        Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        Vector result = list.getTextEntries(lcl);

        return (result);
    }
    /**
     * Gets the mail bank check ID types from the config file
     *
     * @return Vector of ID types retrieved from the config file
     * @deprecated as of 13.1 Use {@link #UtilityManager.getReasonCodes}
     */
    public synchronized Vector getIDTypes(String codeConstant)
    {
        ADOContextIfc context = getContext();
        UtilityManagerIfc utility =
            (UtilityManagerIfc) context.getManager(UtilityManagerIfc.TYPE);
        String storeID = Gateway.getProperty("application", "StoreID", "");
        CodeListIfc list = utility.getReasonCodes(storeID, codeConstant);
        Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        Vector result = list.getTextEntries(lcl);

        return (result);
    }

    /**
     * Gets the default mail bank check ID value from the config file
     *
     * @return Vector of ID types retrieved from the config file
     * @deprecated as of 13.1 Use {@link #UtilityManager.getReasonCodes}
     **/
    public synchronized String getDefaultIDType(String codeConstant)
    {
        ADOContextIfc context = getContext();
        UtilityManagerIfc utility =
            (UtilityManagerIfc) context.getManager(UtilityManagerIfc.TYPE);
        String storeID = Gateway.getProperty("application", "StoreID", "");
        CodeListIfc list = utility.getReasonCodes(storeID, codeConstant);
        Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        return list.getDefaultOrEmptyString(lcl);
    }


    protected synchronized ADOContextIfc getContext()
    {
        return ContextFactory.getInstance().getContext();
    }

    /**
     * This validates the drivers license.
     *
     * @param state
     * @param license
     * @param country
     * @throws TenderException
     */
    public synchronized void validateDriversLicense(
        byte[] license,
        String state,
        String country)
        throws TenderException
    {
        ValidationManagerIfc validationManager =
            (ValidationManagerIfc) Dispatcher.getDispatcher().getManager(
                ValidationManagerIfc.DRIVERS_LICENECE_TYPE);

        String maskName = country + ValidationManagerIfc.DL_MASK_NAME_POSTFIX;
        if (!validationManager.validateString(state, license, maskName))
        {
            throw new TenderException(
                "Invalid Drivers License Format",
                TenderErrorCodeEnum.INVALID_LICENSE);
        }
    }

    /**
     * Takes an expiration date as entered from the UI and converts it into an
     * EYS date.
     *
     * @param format
     *            A date format describing the formatting of dateStr
     * @param dateStr
     *            a String to convert
     * @return an EYSDate representative of the dateStr string
     */
    public synchronized EYSDate parseExpirationDate(
        String format,
        String dateStr)
        throws TenderException
    {
        // check for null
        if (dateStr == null)
        {
            throw new TenderException(
                "Expiration Date is null",
                TenderErrorCodeEnum.INVALID_EXPIRATION_DATE);
        }
        EYSDate eysDate;
        DateFormat dateFormat = createNormalizedDateFormat(format);
        eysDate = null;
        Date date = null;
        try
        {
            date = dateFormat.parse(dateStr);
        }
        catch (ParseException e)
        {
            throw new TenderException(
                "Invalid expiration date format",
                TenderErrorCodeEnum.INVALID_EXPIRATION_DATE,
                e);
        }
        eysDate = DomainGateway.getFactory().getEYSDateInstance();
        eysDate.initialize(date);
        return eysDate;
    }


    /**
     * Returns a DateFormat that will interpret future dates given in 
     * the format YYMM say 3510, to October 2035.
     *  
     * @param format the format representing the input date format
     * @return a DateFormat instance that will interpret as decribed above
     */
    /*
     * For parsing, if the number of pattern letters is more than 2, the year is 
     * interpreted literally, regardless of the number of digits. So using the 
     * pattern "MM/dd/yyyy", "01/11/12" parses to Jan 11, 12 A.D.
     * 
     * For parsing with the abbreviated year pattern ("y" or "yy"),
     * SimpleDateFormat must interpret the abbreviated year relative to some
     * century. It does this by adjusting dates to be within 80 years before
     * and 20 years after the time the SimpleDateFormat instance is created.
     * For example, using a pattern of "MM/dd/yy" and a SimpleDateFormat instance
     * created on Jan 1, 1997, the string "01/11/12" would be interpreted as
     * Jan 11, 2012 while the string "05/04/64" would be interpreted as May 4,
     * 1964. Since 12/49 becomes 12/1949, our app thinks that the card has expired. 
     * 
     * Hence Updated parseExpirationDate() method to properly calculate year for 2 
     * digit format.
     */
    protected DateFormat createNormalizedDateFormat(String format)
    {
        EYSDate eysDate = DomainGateway.getFactory().getEYSDateInstance();
        int currentYear = eysDate.calendarValue().get(Calendar.YEAR);
        int century = currentYear/100;
        century *= 100;
        eysDate.initialize(century-49, 1, 1);

        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, locale);
        dateFormat.set2DigitYearStart(eysDate.dateValue());
        return dateFormat;
    }

    //----------------------------------------------------------------------
    /**
        This method validates the expiration date.
        @param expirationDate
        @throws TenderException if not a valid expiration date
    **/
    //----------------------------------------------------------------------
    public synchronized void validateExpirationDate(String expDate) throws TenderException
    {
        GregorianCalendar gc = (GregorianCalendar)Calendar.getInstance();
        // get today's date and from that get the current
        // Month and Year.
        Date today = new Date();
        gc.setTime(today);
        int thisMonth = gc.get(Calendar.MONTH);
        int thisYear  = gc.get(Calendar.YEAR);
        gc.clear();
        EYSDate expirationDate = parseEncryptedExpirationDate(expDate);
        // reset the calendar with this tender's Exp. Date
        Calendar c = expirationDate.calendarValue();
        gc.setTime(c.getTime());

        // if expiration date is before today and
        // the month and year both do not match the
        // current month and year, the card must be expired.
        EYSDate todayEYS = new EYSDate(today);
        if (expirationDate.before(todayEYS) &&
                        !((thisMonth == gc.get(Calendar.MONTH)) &&
                                        (thisYear == gc.get(Calendar.YEAR))))
        {
            throw new TenderException("Expired", TenderErrorCodeEnum.EXPIRED);
        }
        // set the object to nul
        expirationDate = null;
    }

    /**
     * Takes an swiped encrypted expiration date and converts it into an EYS date
     *
     * @param format
     *            A date format describing the formatting of expirationDateStr
     * @param expirationDateStr
     *            an encrypted String to convert
     * @return an EYSDate representative of the expirationDate string
     */
    private EYSDate parseEncryptedExpirationDate(String expirationDateStr)
        throws TenderException
    {
    	String format = EYSDate.CARD_FORMAT_MMYYYY;
        // check for null
        if (expirationDateStr == null)
        {
            throw new TenderException(
                "Expiration Date is null",
                TenderErrorCodeEnum.INVALID_EXPIRATION_DATE);
        }
        // decrypt the expiration date
        KeyStoreEncryptionManagerIfc encryptionManager =
            (KeyStoreEncryptionManagerIfc)Gateway.getDispatcher().getManager(KeyStoreEncryptionManagerIfc.TYPE);
        String decryptedExpDate = "";
        try
        {
            decryptedExpDate = new String(encryptionManager.decrypt(Base64.decodeBase64(expirationDateStr.getBytes())));
        }
        catch(EncryptionServiceException ese)
        {
            logger.warn("Could not decrypt date", ese);
        }
        // determine the date format
        if(decryptedExpDate.length() == 4)
        {
        	format = EYSDate.FORMAT_YYMM;
        }

        if(decryptedExpDate.length() == 6)
        {
        	decryptedExpDate = decryptedExpDate.substring(0, 2) + "/" + decryptedExpDate.substring(2,6);
        }

        DateFormat dateFormat = createNormalizedDateFormat(format);
        Date date = null;
        try
        {
            date = dateFormat.parse(decryptedExpDate.toString());
        }
        catch (ParseException e)
        {
            throw new TenderException(
                "Invalid expiration date format",
                TenderErrorCodeEnum.INVALID_EXPIRATION_DATE,
                e);
        }
        EYSDate eysDate = DomainGateway.getFactory().getEYSDateInstance();
        eysDate.initialize(date);
        return eysDate;
    }

    /**
     *   This method checks if the entered expiry date of ID is valid. Returns false
     *   if entered expiry date is before the current date. True otherwise.
     *   @param expirationDate
     *   @throws TenderException if not a valid expiration date
     */
    public boolean isValidExpirationDate(String expirationDate) throws TenderException
    {
        boolean result = true;
        Date today = new Date();
        EYSDate todayEYS = new EYSDate(today);
        String format = EYSDate.ID_EXPIRATION_DATE_FORMAT;
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
        DateFormat dateFormat = new SimpleDateFormat(format, locale);
        Date date = null;
        EYSDate dateEYS = null;
        try
        {
            date = dateFormat.parse(expirationDate);
            dateEYS = new EYSDate(date);
        }
        catch (ParseException e)
        {
            throw new TenderException("Invalid expiration date format", TenderErrorCodeEnum.INVALID_EXPIRATION_DATE, e);
        }
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(today);
        int thisMonth = gc.get(Calendar.MONTH);
        int thisYear = gc.get(Calendar.YEAR);
        gc.clear();
        Calendar c = dateEYS.calendarValue();
        gc.setTime(c.getTime());
        if (dateEYS.before(todayEYS) && !((thisMonth == gc.get(Calendar.MONTH)) && (thisYear == gc.get(Calendar.YEAR))))
        {
            result = false;
        }
        return result;
    }

    /*
     * @see oracle.retail.stores.pos.ado.utility.UtilityIfc#getPasswordPolicyTDO()
     */
    public PasswordPolicyTDOIfc getPasswordPolicyTDO()
    {
        //Defaulting to manual enter password style password policy
        String passwordPolicyType = PasswordPolicyTDO.PASSWORD_POLICY_TDO_BEAN_KEY;

        if (isFingerprintAllowed())
        {
            passwordPolicyType = FingerprintPasswordPolicyTDO.FINGERPRINT_PASSWORD_POLICY_TDO_BEAN_KEY;
        }
        PasswordPolicyTDOIfc tdo = (PasswordPolicyTDOIfc)TDOFactory.createBean(passwordPolicyType);
        return tdo;
    }

    /*
    * @see oracle.retail.stores.pos.ado.utility.UtilityIfc#isFingerprintAllowed()
    */
    public boolean isFingerprintAllowed()
    {
        String fingerprintOption = getParameterValue(ParameterConstantsIfc.OPERATORID_FingerprintLoginOptions, ParameterConstantsIfc.OPERATORID_FingerprintLoginOptions_NO_FINGERPRINT);
        return !ParameterConstantsIfc.OPERATORID_FingerprintLoginOptions_NO_FINGERPRINT.equals(fingerprintOption);
    }

    /*
     * @see oracle.retail.stores.pos.ado.utility.UtilityIfc#isStringListed(String, String)
     */
    public boolean isStringListed(String parameterValue, String parameterName)
    {
        String[] parameterList = getParameterValueList(parameterName);
        return isStringListed(parameterValue, parameterList);
    }

    /*
    * @see oracle.retail.stores.pos.ado.utility.UtilityIfc#isOvertenderAllowed(String)
    */
    public boolean isOvertenderAllowed(String tenderType)
    {

        // In 13.4 the customer selects credit, debit or gift card after the tender options are selected.
        // So, there are assumptions that these 3 tender types have the same overtender-allowed value.
        // The credit, debit and gift card overtender tender-type param values were combined into a single
        // CreditDebitGiftCard param value to prevent the merchant from misconfiguring the values.
        // This code compensates for this change.  No longer can you simply compare tender-type to the
        // values in parameter "TendersNotAllowedForOvertender"
        if (TenderTypeEnum.CREDIT.toString().equals(tenderType) ||
                        TenderTypeEnum.DEBIT.toString().equals(tenderType) ||
                        TenderTypeEnum.GIFT_CARD.toString().equals(tenderType))
        {
            return !isStringListed(
                            ParameterConstantsIfc.TENDER_TendersNotAllowedForOvertender_CreditDebitGiftCard,
                            ParameterConstantsIfc.TENDER_TendersNotAllowedForOvertender);
        }
        else
        {
            return !isStringListed(tenderType, ParameterConstantsIfc.TENDER_TendersNotAllowedForOvertender);
        }
    }

 }
