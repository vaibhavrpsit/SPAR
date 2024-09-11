/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/ReturnUtilities.java /main/26 2014/07/04 09:47:50 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  14/10/14 - Added method for validating license format.
 *    mchellap  07/03/14 - Fixed original card refund flow
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    rgour     04/01/13 - CBR cleanup
 *    jswan     11/15/12 - Modified to support parameter controlled return
 *                         tenders.
 *    jswan     10/25/12 - Modified to support returns by order.
 *    sgu       08/17/12 - use original discount method
 *    sgu       08/17/12 - refactor discount audit
 *    rsnayak   03/22/12 - cross border return changes
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    ohorne    07/28/11 - created localalizeDateRangeList()
 *    cgreene   06/07/11 - add generics
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   05/11/10 - convert Base64 from axis
 *    cgreene   05/11/10 - updating deprecated names
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    asinton   05/04/09 - Removed method getDefaultTaxRate, which was an
 *                         inappropriate way to retrieve the default tax rate.
 *    acadar    11/03/08 - merges to tip
 *    acadar    10/30/08 - use localized reason codes for item and transaction
 *                         discounts
 * ===========================================================================
   $Log:
    10   360Commerce 1.9         1/17/2008 5:24:06 PM   Alan N. Sinton  CR
         29954: Refactor of EncipheredCardData to implement interface and be
         instantiated using a factory.
    9    360Commerce 1.8         12/14/2007 8:59:59 AM  Alan N. Sinton  CR
         29761: Removed non-PABP compliant methods and modified card RuleIfc
         to take an instance of EncipheredCardData.
    8    360Commerce 1.7         12/12/2007 6:47:38 PM  Alan N. Sinton  CR
         29761: FR 8: Prevent repeated decryption of PAN data.
    8    I18N_P2    1.6.1.0     12/18/2007 3:09:14 PM  Sandy Gu        static
         text fix for POS
    7    360Commerce 1.6         6/12/2007 8:48:18 PM   Anda D. Cadar   SCR
         27207: Receipt changes -  proper alignment for amounts
    6    360Commerce 1.5         4/25/2007 8:52:14 AM   Anda D. Cadar   I18N
         merge

    5    360Commerce 1.4         5/12/2006 5:25:32 PM   Charles D. Baker
         Merging with v1_0_0_53 of Returns Managament
    4    360Commerce 1.3         1/22/2006 11:45:17 AM  Ron W. Haight   removed
          references to com.ibm.math.BigDecimal
    3    360Commerce 1.2         3/31/2005 4:29:46 PM   Robert Pearse
    2    360Commerce 1.1         3/10/2005 10:24:54 AM  Robert Pearse
    1    360Commerce 1.0         2/11/2005 12:13:56 PM  Robert Pearse
   $
   Revision 1.18.2.1  2004/11/17 21:31:35  mweis
   @scr 7729 A zero value in the DaysWithinDateRange property was not being interpreted as 'All'.

   Revision 1.18  2004/06/21 21:06:29  jriggins
   @scr 5686 Added a mechanism to allow manually setting the isPartOfPriceAdjustment status which is useful for displaying price adjustment components when they are normally filtered out.

   Revision 1.17  2004/05/27 19:31:33  jdeleau
   @scr 2775 Remove unused imports as a result of tax engine rework

   Revision 1.16  2004/05/27 17:12:48  mkp1
   @scr 2775 Checking in first revision of new tax engine.

   Revision 1.15  2004/05/11 15:11:05  jriggins
   @scr 4681 Copied setTransactionDiscounts() method from returns.returnoptions.CreateAndUpdateTransactionSite while adding the change to explicitly make the discountAmount negative.

   Revision 1.14  2004/04/01 00:11:34  cdb
   @scr 4206 Corrected some header foul ups caused by Eclipse auto formatting.


 * Revision 1.13  2004/03/15 21:43:30  baa
 * @scr 0 continue moving out deprecated files
 *
 * Revision 1.12  2004/03/10 14:16:46  baa
 * @scr 0 fix javadoc warnings
 *
 * Revision 1.11  2004/03/09 21:16:47  epd
 * @scr 3561 bug fixes
 *
 * Revision 1.10  2004/03/09 19:28:58  aarvesen
 * @scr 0 removed the import again
 *
 * Revision 1.9  2004/03/09 19:15:01  baa
 * @scr 3561 modify item number 4142  to 141420
 *
 */
package oracle.retail.stores.pos.services.returns.returncommon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TreeSet;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceLocator;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAuditIfc;
import oracle.retail.stores.domain.discount.ReturnItemTransactionDiscountAuditIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.manager.ifc.ValidationManagerIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.returns.ReturnTenderDataElementIfc;
import oracle.retail.stores.domain.stock.ItemKitConstantsIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.UnknownItemIfc;
import oracle.retail.stores.domain.tender.TenderTypeMap;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.CardTypeIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.ado.tender.CreditTypeEnum;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;

import org.apache.log4j.Logger;

/**
 * Utility class for the Return Service
 */
public class ReturnUtilities
{
    /**
     * logger constant
     */
    protected static final Logger logger = Logger.getLogger(ReturnUtilities.class);

    /**
     *  application property group constant
     */
    public static final String APPLICATION_PROPERTIES = "application";

    /**
     * DaysWithin text
     */
    public static final String DATE_RANGE_LIST = "DaysWithinDateRange";

    /**
     * Default date range values
     */
    public static final String DEFAULT_DATE_RANGE = "0,14,30";

    /**
     * datefield constant
     */
    public static final String DATE_RANGE_FIELD = "dateRangeField";

    /**
     * item number field constant
     */
    public static final String ITEM_NUMBER = "itemNumberField";

    /**
     * itemSizefield constant
     */
    public static final String ITEM_SIZE = "itemSizeField";

    /**
     * All text constant
     */
    public static final String ALL = "All";

    /**
     * within text constant
     */
    public static final String WITHIN_NDAYS = "Within {0} Days";

    /**
     * day in millis
     */
    public static long DAY_IN_MILLIS = 24 * 60 * 60 * 1000;

    /**
     * zero constant
     */
    public static final CurrencyIfc ZERO_AMOUNT =
        DomainGateway.getBaseCurrencyInstance(BigDecimal.ZERO);

    /**
     * customer name constant
     */
    public static final String CUSTOMER_NAME_TAG = "CustomerName";

    /**
     *  customer name pattern
     */
    public static final String CUSTOMER_NAME_TEXT = "{0} {1}";

    /**
     * bad magnetic stripe dialog key
     */
    public static final String BAD_MAG_SWIPE  = "BadMSRReadError";

    /**
     * Letter constant
     */
    public static final String TRANSACTION_HAS_ORDER = "TransactionHasOrder";

    /**
        Text to look up tender list in Parameters
    **/
    public static final String SALE_TENDERS_FOR_REFUND = "SaleTendersForRefund";
    
    /**
     * Retrieve the list of valid date ranges
     *
     * @param utility pointer to the utility manager
     * @param rawData date range values from property file
     * @return DataInputBeanModel the bean model
     */
    public static DataInputBeanModel setDateRangeList(
        UtilityManagerIfc utility,
        ArrayList<String> rawData)
    {
        ArrayList<String> i18nData = localalizeDateRangeList(utility, rawData);
        DataInputBeanModel model = new DataInputBeanModel();
        model.setSelectionChoices(DATE_RANGE_FIELD, i18nData);
        model.setSelectionIndex(DATE_RANGE_FIELD, 0);
        return model;
    }

    /**
     * Localizes the Raw Data Range Text for appropriately for
     * the User Interface Subsystem locale.
     * @param utility pointer to the utility manager
     * @param rawData date range values from property file
     * @return the localized text
     */
    public static ArrayList<String> localalizeDateRangeList(UtilityManagerIfc utility, ArrayList<String> rawData)
    {
        ArrayList<String> i18nData = new ArrayList<String>(rawData.size());

        String withinRange =
            utility.retrieveText(
                "Common",
                BundleConstantsIfc.RETURNS_BUNDLE_NAME,
                "WithinDateRange",
                WITHIN_NDAYS);
        String allDates =
            utility.retrieveText(
                "Common",
                BundleConstantsIfc.RETURNS_BUNDLE_NAME,
                "AllDates",
                "All");
        String formattedData, value;

        Iterator<String> propertyList = rawData.iterator();
        while (propertyList.hasNext())
        {
            value = propertyList.next();
            if (Integer.parseInt(value) > 0)
            {
                formattedData = LocaleUtilities.formatComplexMessage(withinRange, value);
            }
            else
            {
                formattedData = allDates;
            }

            // Only add option if not already included
            if (!i18nData.contains(formattedData))
            {
                i18nData.add(formattedData);
            }
        }
        return i18nData;
    }


    /**
     * Converts a comma delimited property values into an arraylist
     *
     * @param propertyGroup
     * @param property
     * @param defaultValue
     * @return arraylist of property values
     */
    public static ArrayList<String> getPropertyValues(
        String propertyGroup,
        String property,
        String defaultValue)
    {
        String propertyValues =
            Gateway.getProperty(propertyGroup, property, defaultValue);
        ArrayList<String> list = new ArrayList<String>(3);
        StringTokenizer parser =
            new StringTokenizer(
                propertyValues,
                UtilityManagerIfc.COMMA_DELIMITER);
        while (parser.hasMoreTokens())
        {
            list.add(parser.nextToken());
        }
        return list;
    }

    /**
     * Calculate the dates to use on date range
     * @param index
     * @param pm pointer to parameter manager
     * @return EYSDate Array with start and end date
     */
    public static EYSDate[] calculateDateRange(int index, ParameterManagerIfc pm)
    {
        ArrayList<String> list =
            getPropertyValues(
                APPLICATION_PROPERTIES,
                DATE_RANGE_LIST,
                DEFAULT_DATE_RANGE);
        EYSDate[] dateRange = null;

        if (list != null && index >= 0 && index < list.size())
        {
            String value = list.get(index);

            int range = Integer.parseInt(value);

            // When selecting 'All' in the date range selection field a
            // value of 0 or a negative number is returned.
            // If no cap has been defined on the 'All' parameter, we will not use the date
            // range as a search criteria.
            // However, if a cap was defined, we will limit 'All' to honor that.
            if (range <= 0)
            {
                // We are using 'All'.
                try
                {
                   // Check from the paramters what is the Max Search range.
                   int maxRange = pm.getIntegerValue("MaximumSearchDateRange").intValue();

                   // If a cap for max range has been defined,
                   // use this instead of searching all the records.
                   if (maxRange > 0)
                   {
                       range = maxRange;
                   }
                }
                catch (ParameterException e)
                {
                    logger.error("Parameter exception " + e.getMessage());
                }
            }

            // We might have set a search cap (a positive number, indicating how many
            // days prior to today we need to search).
            // If we have a cap, calculate the correct date range.
            if (range > 0)
            {
                Calendar today = new GregorianCalendar();
                EYSDate endDate =
                    DomainGateway.getFactory().getEYSDateInstance();
                endDate.initialize(today.getTime());

                // calculate starting date base on range within [n] days
                long startingTime =
                    today.getTimeInMillis() - (range * DAY_IN_MILLIS);
                Calendar startingDay = new GregorianCalendar();
                startingDay.setTimeInMillis(startingTime);

                EYSDate startDate =
                    DomainGateway.getFactory().getEYSDateInstance();
                startDate.initialize(startingDay.getTime());
                dateRange = new EYSDate[2];
                dateRange[0] = startDate;
                dateRange[1] = endDate;
            }
        }
        return dateRange;
    }

    /**
     * Validates that the MSR data read from the card is valid
     * @param msrModel
     * @return boolean
     */
    public static boolean isMSRDataValid(MSRModel msrModel)
    {
        boolean isValid = false;
        if (msrModel != null)
        {
            // if any of the following is null, error
            if (!Util.isEmpty(msrModel.getAccountNumber()) && // has a number...
                msrModel.getAccountNumber().length() >= 10 && // ... that is at least 10 digits,
                !Util.isEmpty(msrModel.getExpirationDate()) && // has an exp date...
                msrModel.getExpirationDate().length() >= 4 && // ... that is at least 4 digits,
                (msrModel.getTrack2Data() != null && msrModel.getTrack2Data().length > 0))  // ... that is non-zero in length
           {
               isValid = true;
           }
        }
        return isValid;
    }

    /**
     * Attempt to determine what type of credit the number represents.
     *
     * @param cardData  The EncipheredCardData instance to identify.
     * @param utility   the utility manager
     * @return The credit type.
     */
    public static CreditTypeEnum determineCreditType(
        UtilityManagerIfc utility,
        EncipheredCardDataIfc cardData)
    {
        CardTypeIfc cardTypeUtility = utility.getConfiguredCardTypeInstance();

        // return the card type
        String cardType =
            cardTypeUtility.identifyCardType(
                cardData,
                TenderTypeEnum.CREDIT.toString());
        return CreditTypeEnum.makeEnumFromString(cardType);
    }

    /**
     * Displayed linked customer name
     *
     * @param ui
     * @param utility
     * @param customer
     */
    public static void displayLinkedCustomer(
        POSUIManagerIfc ui,
        UtilityManagerIfc utility,
        CustomerIfc customer)
    {
        if (customer != null)
        {
            // Create the string from the bundle.
            Locale locale =
                LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

            Object parms[] = { customer.getFirstName(), customer.getLastName()};
            String pattern =
                utility.retrieveText(
                    "CustomerAddressSpec",
                    BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                    CUSTOMER_NAME_TAG,
                    CUSTOMER_NAME_TEXT);
            String customerName =
                LocaleUtilities.formatComplexMessage(pattern, parms, locale);

            ui.customerNameChanged(customerName);
        }
    }

    /**
     * @param utility
     * @param saleTransaction
     * @return
     */
    public static SaleReturnLineItemIfc[] processNonKitCodeHeaderItems(
        UtilityManagerIfc utility,
        SaleReturnTransactionIfc saleTransaction)
    {
        SaleReturnLineItemIfc[] itemsArray =
            saleTransaction.getLineItemsExcluding(
                ItemKitConstantsIfc.ITEM_KIT_CODE_HEADER);

        for (int i = 0; i < itemsArray.length; i++)
        {
            itemsArray[i] =
                processANonKitCodeHeaderItem(utility, itemsArray[i], saleTransaction);
        }
        return itemsArray;
    }

    /**
     * @param utility
     * @param saleTransaction
     * @param item
     * @return
     */
    public static SaleReturnLineItemIfc processANonKitCodeHeaderItem(
        UtilityManagerIfc utility,
        SaleReturnLineItemIfc item,
        SaleReturnTransactionIfc saleTransaction)
    {
        if (item.getOrderItemStatus() != null)
        {
            if (item.getOrderItemStatus().getStatus().getStatus()
                == OrderConstantsIfc.ORDER_ITEM_STATUS_CANCELED)
            {
                item.setQuantityReturned(item.getItemQuantityDecimal());
            }
        }

        // Manually set price adjustment components to false so that they
        // can show up in the sale line item screen where they are normally
        // filtered out.
        if (item.isPartOfPriceAdjustment())
        {
            item.setIsPartOfPriceAdjustment(false);
        }
        
        return item;
    }

    /**
     * Take away the extra digits starting from the 3rd one after decimal point
     *
     * @param longAmount
     *            CurrencyIfc object
     * @return shortString String
     */
    public static String makeShorter(CurrencyIfc longAmount)
    {
        String shortString = "";

        Locale locale = LocaleMap.getLocale(LocaleMap.DEFAULT);
        shortString = CurrencyServiceLocator.getCurrencyService().formatCurrency(longAmount, locale);

        return (shortString);
    }

    /**
     * The pro-rated amount of the transaction discount amount must be set to a
     * ReturnItemTransactionAudit object so that the totaling will be handled
     * properly.
     *
     * @param srli
     *            SaleReturnLineItemIfc object
     * @param quantity
     *            quantity of item to be returned (already negative)
     */
    public static void setTransactionDiscounts(SaleReturnLineItemIfc srli, BigDecimal quantity)
    {
        // pull transaction discounts
        ItemDiscountStrategyIfc[] discounts = srli.getItemPrice().getTransactionDiscounts();
        int len = 0;
        // clear transaction discounts
        srli.clearTransactionDiscounts();
        ReturnItemTransactionDiscountAuditIfc ritda = null;
        ItemTransactionDiscountAuditIfc itda = null;
        if (discounts != null)
        {
            len = discounts.length;
        }
        for (int i = 0; i < len; i++)
        {
            itda = (ItemTransactionDiscountAuditIfc) discounts[i];
            CurrencyIfc discountAmount = itda.getDiscountAmount();
            BigDecimal quantityDecimal = new BigDecimal(quantity.toString());
            discountAmount = discountAmount.multiply(quantityDecimal);
            quantityDecimal = new BigDecimal(srli.getItemQuantityDecimal().toString());
            discountAmount = discountAmount.divide(quantityDecimal).abs().negate();
            ritda = DomainGateway.getFactory().getReturnItemTransactionDiscountAuditInstance();
            ritda.initialize(discountAmount, itda.getReason());
            ritda.setAssignmentBasis(itda.getAssignmentBasis());
            ritda.setOriginalDiscountMethod(itda.getOriginalDiscountMethod());
            srli.addItemDiscount(ritda);
        }

        srli.calculateLineItemPrice();
    }
    
    /**
     * Look up the Refund Tenders parameter based on the tender type and return
     * a List of integer tender types. 
     * 
     * @param pm The parameter manager
     * @param tenderType The tender type
     */
    public static TreeSet<Integer> getRefundTenderTypes(ParameterManagerIfc pm, int tenderType)
    {
        TenderTypeMapIfc map = TenderTypeMap.getTenderTypeMap();
        String parameterName = "RefundTenderFor" +
            map.getDescriptor(tenderType)+ "Payment";
        TreeSet<Integer> acceptedTenderTypes = new TreeSet<Integer>();

        try
        {
            String[] tendersDescriptors = pm.getStringValues(parameterName);
            for(String descriptor: tendersDescriptors)
            {
                acceptedTenderTypes.add(map.getTypeFromDescriptor(descriptor));
            }
        }
        catch (ParameterException e)
        {
            logger.error("Could not retrieve " + parameterName + " from the ParameterManager.", e);
        }

        return acceptedTenderTypes;
    }

    /**
     * Look up the Non Retrieved Refund Tender Types parameter and return a List
     * of integer tender types.
     */
    public static TreeSet<Integer> getNonRetrievedRefundTenderTypes(ParameterManagerIfc pm)
    {
        TenderTypeMapIfc map = TenderTypeMap.getTenderTypeMap();
        TreeSet<Integer> acceptedTenderTypes = new TreeSet<Integer>();

        try
        {
            String[] tendersDescriptors = pm.getStringValues("RefundTenderForNonRetrievedTrans");
            for (String descriptor : tendersDescriptors)
            {
                acceptedTenderTypes.add(map.getTypeFromDescriptor(descriptor));
            }
        }
        catch (ParameterException e)
        {
            logger.error("Could not retrieve RefundTenderForNonRetrievedTrans from the ParameterManager.", e);
        }

        return acceptedTenderTypes;
    }
    
    /**
     * This method determines if the transaction contains a retrieved original transaction.
     *
     * @return true if transaction contains a retrieved original transaction.
     */
    public static boolean hasBeenRetrieved(SaleReturnTransactionIfc transaction)
    {

        boolean retrieved = false;

        AbstractTransactionLineItemIfc[] items = transaction.getLineItems();
        if (items != null && items.length > 0)
        {
            for (int i = 0; i < items.length; i++)
            {
                if (items[i] instanceof SaleReturnLineItemIfc)
                {
                    ReturnItemIfc item = ((SaleReturnLineItemIfc) items[i]).getReturnItem();
                    if (item != null && item.isFromRetrievedTransaction())
                    {
                        retrieved = true;
                        break;
                    }
                }
            }
        }
        return retrieved;
    }
    
    /**
     * This method checks to see if a regular receipt was supplied by the customer.
     * See the hasGiftReceipt() method for testing for gift receipt
     * @return true if a regular receipt was supplied by the customer.
     */
    public static boolean hasReceipt(SaleReturnTransactionIfc transaction)
    {
        boolean hasReceipt = false;

        AbstractTransactionLineItemIfc[] items = transaction.getLineItems();
        if (items != null && items.length > 0)
        {
            for (int i = 0; i < items.length; i++)
            {
                if (items[i] instanceof SaleReturnLineItemIfc)
                {
                    ReturnItemIfc item = ((SaleReturnLineItemIfc) items[i]).getReturnItem();
                    if (item != null)
                    {
                        if (item.haveReceipt())
                        {
                            hasReceipt = true;
                            break;
                        }
                    }
                }
            }
        }
        return hasReceipt;
    }
    
    /**
     * This method checks to see if the type selected was a tender used in the
     * original transaction
     * 
     * @param type Tender Type
     * @return true if the type selected was a tender used in the original
     *         transaction
     */
    public static boolean isReturnTender(SaleReturnTransactionIfc transaction, int type)
    {
        boolean hasOnlyOneTender = false;

        ReturnTenderDataElementIfc[] tenders = transaction.getReturnTenderElements();
        if (tenders != null)
        {
            for (int i = 0; i < tenders.length; i++)
            {
                // if tender type is equal to the parameter type irrelevant of
                // whether it is a positive/negative tender
                if (tenders[i].getTenderType() == type)
                {
                    hasOnlyOneTender = true;
                    break;
                }
            }
        }

        return hasOnlyOneTender;
    }
    
    
    /**
     * This method determines if there was only one type of tender used in the original transaction.
     *
     * @return True if there was only one type of tender used in the original transaction
     */
    public static boolean hasOnlyOneOriginalTender(SaleReturnTransactionIfc transaction)
    {
        boolean oneTender = true;
        // do we have a positive tender
        boolean positiveTender = false;

        ReturnTenderDataElementIfc[] tenders = transaction.getReturnTenderElements();

        if (tenders != null)
        {
            for (int i = 0; i < tenders.length; i++)
            {
                if (tenders[i].getTenderAmount().signum() == CurrencyIfc.POSITIVE)
                {
                    // did we find a positive tender already?
                    if (!positiveTender)
                    {
                        // this is the first tender
                        positiveTender = true;
                    }
                    else
                    {
                        // then we have more than one tender
                        oneTender = false;
                        break;
                    }
                }
            }
        }

        return oneTender;
    }
    
    /*
     * This method determines the unit of measure name.  If the localized name
     * is already in the PLUItem, get it from there.  Otherwise, get the localized
     * name from the code list using the Unit of Measure code.  The unit of measure
     * code can come from either an UnknownItem object or UnitOfMeasure object in
     * the Item object associated with PLUItem.
     */
    public static String getUOMName(PLUItemIfc item, Locale locale, UtilityManagerIfc utility, String storeID)
    {
        String name    = "";
        String uomCode = null;

        // Try to get the name directly from the UnitOfMeasure object
        if (item.getUnitOfMeasure() != null)
        {
            String lName = item.getUnitOfMeasure().getName(locale);
            if (!Util.isEmpty(lName))
            {
                name = lName;
            }
            else
            {
                uomCode = item.getUnitOfMeasure().getUnitID();
            }
        }

        // If that fails, get the name using the reason code list.
        if (Util.isEmpty(name))
        {
            if (item instanceof UnknownItemIfc)
            {
                UnknownItemIfc unknownItem = (UnknownItemIfc) item;
                uomCode = unknownItem.getUOMCode();
            }

            if (uomCode != null)
            {
                CodeListIfc list = utility.getReasonCodes(storeID, CodeConstantsIfc.CODE_LIST_UNIT_OF_MEASURE);
                CodeEntryIfc uomCodeEntry = list.findListEntryByCode(uomCode);
                if (uomCodeEntry == null)
                {
                    name = "";
                }
                else
                {
                    name = uomCodeEntry.getText(locale);
                }
            }
        }

        return name;
    }
    
    /**
     * This method determines whether the driver license format is valid or not.
     * 
     * @param ParameterManagerIfc The parameter manager.
     * @param CustomerInfoIfc The customer information containing personal id.
     * @return boolean 
     */
    public static boolean isDriversLicenceValid(String storeID, UtilityManagerIfc utility, ParameterManagerIfc pm, CustomerInfoIfc customerInfo)
    {
        boolean isValidationRequired = false;
        boolean isDriversLicence = false;
        boolean isValid = true;
        String personalIDType = customerInfo.getLocalizedPersonalIDType().getCode();
        byte[] personalID = customerInfo.getPersonalID().getDecryptedNumber();
        String state = customerInfo.getPersonalIDState();
        String country = customerInfo.getPersonalIDCountry();

        // Determine if a driver's license must be validated. The default is no;
        // this the backward compatibility is preserved.
        try
        {
            if (pm.getStringValue(ParameterConstantsIfc.BASE_ValidateDriverLicenseFormat).equalsIgnoreCase("Y"))
            {
                isValidationRequired = true;
            }
            
            if(isValidationRequired)
            {
                // Get driver license code from reason codes
                String driverLicenseCode = "";
                CodeListIfc reasonCodes = utility.getReasonCodes(storeID, CodeConstantsIfc.CODE_LIST_CAPTURE_CUSTOMER_ID_TYPES);

                if (reasonCodes != null)
                {
                    for (CodeEntryIfc entry : reasonCodes.getEntries())
                    {
                        if (CodeConstantsIfc.CODE_LIST_CAPTURE_CUSTOMER_ID_DRIVERLICENSE.equalsIgnoreCase(entry.getCodeName()))
                        {
                            driverLicenseCode = entry.getCode();
                            break;
                        }
                    }

                }        

                // Determine if the id is a driver's license.
                if (personalIDType.equals(driverLicenseCode))
                {
                    isDriversLicence = true;
                }

                // If the id is a driver's license and validation is required, validate
                // the id.
                if (isValidationRequired && isDriversLicence)
                {
                    ValidationManagerIfc validationManager = (ValidationManagerIfc)Dispatcher.getDispatcher().getManager(
                            ValidationManagerIfc.DRIVERS_LICENECE_TYPE);

                    String maskName = country + ValidationManagerIfc.DL_MASK_NAME_POSTFIX;
                    if (!validationManager.validateString(state, personalID, maskName))
                    {
                        isValid = false;
                    }

                }

                Util.flushByteArray(personalID);
            }
        }
        catch (ParameterException e)
        {
            logger.error("" + Util.throwableToString(e) + "");
        }
        
        return isValid;
    }
    
}
