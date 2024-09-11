/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/priceadjustment/CheckForPriceAdjustableItemsSite.java /main/29 2013/06/26 19:34:23 arabalas Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    arabal 06/26/13 - added a check for ITEM TYPE
 *    vtemke 04/16/13 - Moved constants in OrderLineItemIfc to
 *                      OrderConstantsIfc in common project
 *    rabhaw 03/27/13 - Refund more amount than paid amount when price adj and
 *                      return are done simultaneous
 *    yiqzha 01/04/13 - Refactoring ItemManager
 *    jswan  06/29/12 - Rename NewTaxRuleIfc to TaxRulesIfc
 *    mjwall 04/25/12 - Fixes for Fortify redundant null check, take2
 *    vtemke 03/30/12 - Refactoring of getNumber() method of TenderCheck class
 *                      - returns sensitive data in byte[] instead of String
 *    rsnaya 10/25/11 - price adjustment throwing unexpected error fix
 *    sgu    09/08/11 - add house account as a refund tender
 *    rsnaya 07/01/11 - price adjustment fix
 *    cgreen 02/15/11 - move constants into interfaces and refactor
 *    abhayg 10/15/10 - FIX FOR APPLICATION CRASHES WHEN A RELOADED GIFT CARD
 *                      TRANSACTION IS PRICE ADJUSTED
 *    asinto 08/26/10 - Prevent price adjustment on a pickup or delivery item
 *                      that has not been picked up.
 *    jswan  08/18/10 - Added a group classification ID for Gift Certificates;
 *                      this allows sale return lines containing a gift
 *                      certificate to suspended and retrieved correctly.
 *    rrkohl 08/06/10 - added fix to enable price adjustment for completed
 *                      order items
 *    abonda 06/16/10 - disallow price adjustment if the transaction has
 *                      external order
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    rsnaya 03/30/10 - adding item serial to the new priceadjustment line
 *                      items
 *    abonda 01/03/10 - update header date
 *    abonda 10/17/08 - I18Ning manufacturer name
 *    abonda 10/15/08 - I18Ning manufacturer name
 *
 *
 * ===========================================================================
 *
 * $Log:
 *  9    360Commerce 1.8         4/29/2008 2:28:16 PM   Anil Rathore    Updated
 *        to fix defect 31207. Code Reviewed by Sandy Gu.
 *  8    360Commerce 1.7         3/10/2008 4:15:08 PM   Sandy Gu        specify
 *        store id for plu look for price adjustment.
 *  7    360Commerce 1.6         3/5/2008 12:08:39 PM   Siva Papenini   CR
 *       30773 , Handled Null Pointer Exception
 *  6    360Commerce 1.5         7/31/2007 7:18:50 PM   Alan N. Sinton  CR
 *       27192 Set the default tax rules for price adjust transactions.
 *  5    360Commerce 1.4         4/25/2007 8:52:18 AM   Anda D. Cadar   I18N
 *       merge
 *
 *  4    360Commerce 1.3         1/22/2006 11:45:15 AM  Ron W. Haight   removed
 *        references to com.ibm.math.BigDecimal
 *  3    360Commerce 1.2         3/31/2005 4:27:25 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:20:08 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:09:55 PM  Robert Pearse
 * $
 * Revision 1.34.2.7  2004/12/10 19:03:00  cdb
 * @scr 7833 Updated to avoid creating new discount when original transaction didn't contain a discount.
 *
 * Revision 1.34.2.6  2004/11/19 20:27:42  cdb
 * @scr 7742 Fine tuning of Price Adjustments - assure that Markdowns are considered correctly.
 *
 * Revision 1.34.2.5  2004/11/18 22:05:07  cdb
 * @scr 7742 Updated to impose order on added discounts to match calculation order. Modified
 * price adjustment to correct for trans % discounts being applied before item amt discounts.
 *
 * Revision 1.34.2.4  2004/11/04 20:11:26  rsachdeva
 * @scr 4985 Price Adjustment Receipt Discount Scope
 *
 * Revision 1.34.2.3  2004/10/29 21:35:50  kmcbride
 * @scr 7553: Code was doing a division by zero in this case, added check to ensure this does not happen
 *
 * Revision 1.34.2.2  2004/10/29 19:31:12  cdb
 * @scr 7555 Excluded all but Manual discounts from being carried forward in carryDiscountsForward method.
 *
 * Revision 1.34.2.1  2004/10/15 22:06:09  cdb
 * @scr 7265 Updated to ignore customer discounts as they are added to
 * the transaction at the time the customer is added to the transaction. Otherwise
 * there is a duplication of the customer discount.
 *
 * Revision 1.34  2004/10/01 21:55:20  lzhao
 * @scr 7265: set transactionDiscount to zero to avoid double discount.
 *
 * Revision 1.33  2004/09/30 17:56:24  bwf
 * @scr 7264 Keep original tenders in current transaction.
 *
 * Revision 1.32  2004/08/18 16:21:30  jriggins
 * @scr 4985 Reworked CheckForPriceAdjustableItemsSite.carryDiscountsForward() removed need for PriceAdjustmentItemTransactionDiscount
 *
 * Revision 1.31  2004/07/30 22:50:02  jriggins
 * @scr 4985 Reworked the CheckForPriceAdjustableItemsSite.carryDiscountsForward() method so that indivdual discounts are copied over and prorated as needed. This was needed for printing out the individual discounts on the receipt.
 *
 * Revision 1.30  2004/07/09 22:02:04  jriggins
 * @scr 5029 Added an explicit check for $0 items in isEligibleForPriceAdjustment().  Also modified the currentItemList in addPriceAdjustmentLineItems() to append the new entries to its list rather than by index which can lead to out of bounds exceptions
 *
 * Revision 1.29  2004/07/09 20:43:12  jriggins
 * @scr 5029 Updated carryDiscountsForward() to have safeguards against bringing the extended discounted selling price below $0
 *
 * Revision 1.28  2004/06/30 00:41:58  jriggins
 * @scr 5466 Added logic for maintaining original SaleReturnTransactionIfc instances for transactions which contain returns. This is needed in order to update the line item data for the return components of price adjusted line items in the database.
 *
 * Revision 1.27  2004/06/28 21:39:16  jriggins
 * @scr 5777 Added logic for copying item-level taxes over to the sale component of a price adjustment. Removed unecessary calls in SaleReturnTransaction.addPriceAdjustmentLineItem()
 *
 * Revision 1.26  2004/06/24 20:38:00  jriggins
 * @scr 4984 Modified the journalling output
 *
 * Revision 1.25  2004/06/21 21:06:29  jriggins
 * @scr 5686 Added a mechanism to allow manually setting the isPartOfPriceAdjustment status which is useful for displaying price adjustment components when they are normally filtered out.
 *
 * Revision 1.24  2004/06/21 16:11:07  jriggins
 * @scr 5667 Updated the conversion of discounts from the original item to the current item
 *
 * Revision 1.23  2004/06/11 21:31:49  jriggins
 * @scr 5465 Added check for matching return items in the current transaction
 *
 * Revision 1.22  2004/06/10 23:06:36  jriggins
 * @scr 5018 Added logic to support replacing PriceAdjustmentLineItemIfc instances in the transaction which happens when shuttling to and from the pricing service
 *
 * Revision 1.21  2004/06/07 14:58:49  jriggins
 * @scr 5016 Added logic to persist previously entered transactions with price adjustments outside of the priceadjustment service so that a user cannot enter the same receipt multiple times in a transaction.
 *
 * Revision 1.20  2004/06/03 14:47:45  epd
 * @scr 5368 Update to use of DataTransactionFactory
 *
 * Revision 1.19  2004/06/02 16:55:12  jriggins
 * @scr 4971 Removed price override indicators and $0 discounts from price adjustment component objects which was causing the RetailPriceModifier table to be incorrectly updated leading to DataExceptions
 *
 * Revision 1.18  2004/05/19 17:16:11  jriggins
 * @scr 3979 added call to getPLUItem() when checking for gift certificates
 *
 * Revision 1.17  2004/05/11 15:12:22  jriggins
 * @scr 4681 Added logic for carrying the transaction discounts forward especially for the return component of the price adjustment
 *
 * Revision 1.16  2004/05/05 22:34:55  jriggins
 * @scr 4681 Added support for the new PriceAdjustmentCarryDiscountForwardMethod parameter
 *
 * Revision 1.15  2004/05/03 16:02:19  jriggins
 * @scr 3979 Updated logic for isTransactionDateWithinLimit() and added a unit test
 *
 * Revision 1.14  2004/04/29 22:27:48  jriggins
 * @scr 3979 Changed comparison logic in isTransactionWithinTimeLimit()
 *
 * Revision 1.13  2004/04/27 21:31:14  jriggins
 * @scr 3979 Code review cleanup
 *
 * Revision 1.12  2004/04/21 23:05:23  jriggins
 * @scr 3979 More refactoring
 *
 * Revision 1.11  2004/04/21 13:39:20  jriggins
 * @scr 3979 Refactored logic into additional methods
 *
 * Revision 1.10  2004/04/20 12:54:59  jriggins
 * @scr 3979 Added clone of original line item to be the return line item to modify
 *
 * Revision 1.9  2004/04/19 03:27:28  jriggins
 * @scr 3979 Clear transaction discounts before carrying other discounts forward
 *
 * Revision 1.8  2004/04/17 17:59:28  tmorris
 * @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 * Revision 1.7  2004/04/16 22:33:21  jriggins
 * @scr 3979 Added parameter information
 *
 * Revision 1.6  2004/04/15 15:45:59  jriggins
 * @scr 3979 Refactored logic
 *
 * Revision 1.5 2004/04/06 20:18:01 jriggins @scr 3979 Reworked logic
 * making some original methods obsolete Revision 1.4 2004/04/03 02:12:26 jriggins @scr 3979 added price calculation
 * for the sale component of a price adjustment
 *
 * Revision 1.3 2004/04/03 00:23:38 jriggins @scr 3979 Price Adjustment feature dev Revision 1.2 2004/03/30 23:49:17
 * jriggins @scr 3979 Price Adjustment feature dev
 *
 * Revision 1.1 2004/03/30 00:04:59 jriggins @scr 3979 Price Adjustment feature dev
 *
 **********************************************************************************************************************/

package oracle.retail.stores.pos.services.priceadjustment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.PLUTransaction;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByAmountIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByPercentageIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.PriceAdjustmentLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.manager.item.ItemManagerIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.returns.ReturnTenderDataElementIfc;
import oracle.retail.stores.domain.stock.GiftCertificateItemIfc;
import oracle.retail.stores.domain.stock.ItemSearchCriteria;
import oracle.retail.stores.domain.stock.ItemSearchCriteriaIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.SearchCriteria;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Attemps to retrieve the line items associated with a given transaction ID.
 *
 * Pre Conditions: Cargo contains original and current SaleReturnTransactionIfc
 * instances
 *
 * Post Conditions: Cargo and curreent transaction contain price adjustable line
 * items if any exist
 *
 * @version $Revision: /main/29 $
 */
public class CheckForPriceAdjustableItemsSite extends PosSiteActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5809923428380006152L;

    /**
     *
     * Attemps to retrieve the line items associated with a given transaction
     * ID.
     *
     * Pre Conditions: Cargo contains original and current
     * SaleReturnTransactionIfc instances
     *
     * Post Conditions: Cargo and curreent transaction contain price adjustable
     * line items if any exist
     *
     * @version $Revision: /main/29 $
     */
    @Override
    public void arrive(BusIfc bus)
    {
        String letter = PriceAdjustmentUtilities.LETTER_NO_ITEMS;

        PriceAdjustmentCargo cargo = (PriceAdjustmentCargo) bus.getCargo();

        try
        {
            
            CustomerIfc customer = null;
            SaleReturnTransactionIfc originalTransaction = cargo.getOriginalTransaction();
            SaleReturnTransactionIfc currentTransaction = cargo.getTransaction();

            // Add any original customer info if none is in the current transaction
            if (originalTransaction != null)
            {
                customer = originalTransaction.getCustomer();
            }
            if (currentTransaction.getCustomer() == null && customer != null)
            {
                currentTransaction.linkCustomer(customer);
            }

            // Check the date of the transaction and make sure it is still
            // eligible

            // Get the time limit parameter value
            ParameterManagerIfc parameterManager = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
            Integer timeLimit = null;
            try
            {
                timeLimit = parameterManager
                        .getIntegerValue(ParameterConstantsIfc.PRICEADJUSTMENT_PriceAdjustmentTimeLimit);
            }
            catch (ParameterException pe)
            {
                logger.error(pe);
                timeLimit = new Integer(-1);
            }

            boolean dateValid = isTransactionDateWithinLimit(timeLimit.intValue(), originalTransaction,
                    currentTransaction);
            if (!dateValid)
            {
                letter = PriceAdjustmentUtilities.LETTER_TIME_EXPIRED;
            }
            // If valid, search for price adjustable line items and place them
            // in cargo and the current transaction
            else
            {
                if (originalTransaction != null)
                {
                    // Search for price adjustable line items and add them to
                    // the current transaction
                    PriceAdjustmentLineItemIfc priceAdjustmentLineItems[] = null;
                    priceAdjustmentLineItems = addPriceAdjustmentLineItems(bus, cargo.getTransaction(),
                            originalTransaction);


                    if (priceAdjustmentLineItems != null && priceAdjustmentLineItems.length > 0)
                    {
                        // Add price adjustments to cargo
                        cargo.setPriceAdjustmentLineItems(priceAdjustmentLineItems);

                        // Make note of original transaction for updating in the db later.
                        cargo.addOriginalPriceAdjustmentTransaction(originalTransaction);

                        // If there are price adjustable line items, set the letter
                        // to ItemsFound
                        letter = PriceAdjustmentUtilities.LETTER_ITEMS_FOUND;

                        // get the original tenders used for refund options calculations
                        currentTransaction.setReturnTenderElements(getOriginalTenders(originalTransaction.getTenderLineItems()));

                    }
                }
            }
        }
        catch (DataException de)
        {
            logger.error("Database error", de);
            letter = CommonLetterIfc.DB_ERROR;
        }

        // Mail the appropriate letter
        bus.mail(letter, BusIfc.CURRENT);
    }

    //----------------------------------------------------------------------
    /**
     * Checks to make sure that the transaction date is within the configured
     * time limit for price adjustments
     *
     * @param timeLimit
     *            The number of days for which a price adjustment is applicable
     * @param originalTransaction
     *            Original transaction
     * @param currentTransaction
     *            Current transaction
     * @return true only if the current transaction is within the time limit
     */
    //----------------------------------------------------------------------
    public boolean isTransactionDateWithinLimit(int timeLimit, SaleReturnTransactionIfc originalTransaction,
            SaleReturnTransactionIfc currentTransaction)
    {
        boolean withinLimit = false;

        // Return false if it's not set or is less than or equal to 0
        // Log as a warning.
        if (timeLimit <= 0)
        {
            withinLimit = false;

            StringBuffer logString = new StringBuffer("Parameter ").append(
                    ParameterConstantsIfc.PRICEADJUSTMENT_PriceAdjustmentTimeLimit).append(" = ").append(timeLimit);

            logger.warn(logString);
        }
        // Otherwise add the number of days allowable to the original date (to
        // get the actual expiration date) and then compare to the current day.
        else
        {
            EYSDate expirationDate = (EYSDate) originalTransaction.getBusinessDay().clone();
            EYSDate currentDate = (EYSDate) currentTransaction.getBusinessDay().clone();

            // Is still within limit if expiration date == current date
            expirationDate.setType(EYSDate.TYPE_DATE_ONLY);
            currentDate.setType(EYSDate.TYPE_DATE_ONLY);
            expirationDate.add(Calendar.DATE, timeLimit);
            if (expirationDate.after(currentDate) || expirationDate.equals(currentDate))

            {
                withinLimit = true;
            }
            else
            {
                withinLimit = false;
            }
        }

        return withinLimit;
    }

    //----------------------------------------------------------------------
    /**
     * Adds those line items which can be price adjusted to the current
     * transaction
     *
     * Postconditions: current transaction contains a list of price adjusted
     * line items if any
     *
     * @param currentTransaction
     *            Transaction for which to add any price adjustable line items
     * @param originalTransaction
     *            Transaction being searched for price adjustable line itmes
     * @return array of PriceAdjustmentLineItems that is a subset of the
     *         lineItems array and only contains those items which can be price
     *         adjusted
     */
    //----------------------------------------------------------------------
    protected PriceAdjustmentLineItemIfc[] addPriceAdjustmentLineItems(BusIfc bus,
                                                                       SaleReturnTransactionIfc currentTransaction,
                                                                       SaleReturnTransactionIfc originalTransaction)
            throws DataException
    {
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        PriceAdjustmentLineItemIfc priceAdjustmentLineItems[] = new PriceAdjustmentLineItemIfc[0];

        // We'll need the cargo
        PriceAdjustmentCargo cargo = (PriceAdjustmentCargo) bus.getCargo();

        // Get the line items from the original transaction
        Vector<AbstractTransactionLineItemIfc> lineItems = originalTransaction.getLineItemsVector();
        if (originalTransaction.hasExternalOrder() || lineItems == null || lineItems.size() <= 0)
        {
            return null;
        }
     
        // Get the line items from the current transaction to check if any
        // return items present
        Vector<AbstractTransactionLineItemIfc> currentTransactionLineItems = currentTransaction.getLineItemsVector();
        if (currentTransactionLineItems != null && currentTransactionLineItems.size() > 0)
        {
            // If any return items present in the current transaction update
            // them in the Original transaction
            updateCurrentTransactionReturnItems(currentTransactionLineItems, lineItems, originalTransaction);
        }
        
        // Create an array list for storing the price adjusted line items
        ArrayList<PriceAdjustmentLineItemIfc> priceAdjustableLineItemList =
            new ArrayList<PriceAdjustmentLineItemIfc>(lineItems.size());

        // Create a dummy transaction in order to activate the advance pricing
        // rules
        SaleReturnTransactionIfc dummyTransaction = DomainGateway.getFactory().getSaleReturnTransactionInstance();
        dummyTransaction.setTransactionDiscounts(currentTransaction.getTransactionDiscounts());
        TransactionTaxIfc originalTransactionTax = originalTransaction.getTransactionTax();
        originalTransactionTax.setDefaultTaxRules(currentTransaction.getTransactionTax().getDefaultTaxRules());
        dummyTransaction.setTransactionTax(originalTransactionTax);
        dummyTransaction.addAdvancedPricingRules(currentTransaction.getAdvancedPricingRules());

        // Get the carry discounts forward parameter value.
        Boolean carryDiscountsForward = new Boolean(true);
        ParameterManagerIfc parameterManager = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        try
        {
            carryDiscountsForward = parameterManager
                    .getBooleanValue(ParameterConstantsIfc.PRICEADJUSTMENT_PriceAdjustmentCarryDiscountForward);
        }
        catch (Throwable t)
        {
            carryDiscountsForward = new Boolean(false);
            logger.error(new StringBuffer("Parameter Error. Setting ").append(
                    ParameterConstantsIfc.PRICEADJUSTMENT_PriceAdjustmentCarryDiscountForward).append(" = ").append(
                    carryDiscountsForward), t);
        }

        // Get the carry discounts forward method parameter value.
        String carryDiscountsForwardMethod = null;
        try
        {
            carryDiscountsForwardMethod = parameterManager
                    .getStringValue(ParameterConstantsIfc.PRICEADJUSTMENT_PriceAdjustmentCarryDiscountForwardMethod);
        }
        catch (Throwable t)
        {
            carryDiscountsForwardMethod = ParameterConstantsIfc.PRICEADJUSTMENT_PriceAdjustmentCarryDiscountForwardMethod_AMOUNT;
            logger.error(new StringBuffer("Parameter Error. Setting ").append(
                    ParameterConstantsIfc.PRICEADJUSTMENT_PriceAdjustmentCarryDiscountForwardMethod).append(" = ").append(
                            carryDiscountsForwardMethod), t);
        }

        /*
         * Investigate the line items from the original transaction to see if
         * any are price adjustable. Then perform a PLU lookup on that item to
         * find it's current price. We'll add all of these items to a dummy transaction
         * in order to activate any advanced pricing rules which may apply.
         */
        // List of original line items that are eligible for price adjustments
        String storeID = currentTransaction.getFormattedStoreID();
        ArrayList<SaleReturnLineItemIfc> priceAdjustableLineItems =
            new ArrayList<SaleReturnLineItemIfc>(lineItems.size());
        // List of current line items that correspond to the price adjustable ones
        ArrayList<SaleReturnLineItemIfc> currentLineItems =
            new ArrayList<SaleReturnLineItemIfc>(lineItems.size());

        for (int i = 0; i < lineItems.size(); i++)
        {
            SaleReturnLineItemIfc originalLineItem = (SaleReturnLineItemIfc)lineItems.get(i);

            // First off, check to see if the item is eligible for price
            // adjustment
            if (!isEligibleForPriceAdjustment(cargo, originalLineItem))
            {
                continue;
            }
            else if (priceAdjustableLineItems != null)
            {
                priceAdjustableLineItems.add(originalLineItem);
            }

            // Do a PLU lookup to see what the current price is
            String pluItemID = originalLineItem.getPLUItemID();
            StringBuffer logString = new StringBuffer("Attempting to perform a PLU lookup for item ");
            logString.append(pluItemID);
            logger.debug(logString);


            ItemManagerIfc mgr = (ItemManagerIfc) bus.getManager(ItemManagerIfc.TYPE);

            ItemSearchCriteriaIfc searchCriteria = DomainGateway.getFactory().getItemSearchCriteriaInstance();
            searchCriteria.setItemID(pluItemID);
            searchCriteria.setStoreNumber(storeID);
            searchCriteria.setGeoCode(cargo.getRegister().getWorkstation().getStore().getGeoCode());
            searchCriteria.setLocaleRequestor(utility.getRequestLocales());
            searchCriteria.setRetrieveFromStore(true);
            PLUItemIfc currentItem = mgr.getPluItem(searchCriteria);

            // Add current items to the dummy transaction in order to activate
            // advanced pricing rules
            SaleReturnLineItemIfc currentLineItem =
                dummyTransaction.addPLUItem(currentItem, originalLineItem.getItemQuantityDecimal());

            if (currentLineItem != null)
            {
                // Carry over original manual discounts if configured for it
                if (carryDiscountsForward.booleanValue())
                {
                    carryDiscountsForward(originalLineItem, currentLineItem, carryDiscountsForwardMethod);
                }
    
                if(originalLineItem.getItemSerial() != null)
                {
                	currentLineItem.setItemSerial(originalLineItem.getItemSerial());
                }
    
                // Add the current line item to the list for processing later
                currentLineItems.add(currentLineItem);
            }
        }

        /*
         * Now that we've populated the dummy transaction with the current prices of price adjustable
         * line items, go through the lists and compare the original prices to the current prices
         */
        if (priceAdjustableLineItems != null && currentLineItems != null && priceAdjustableLineItems.size() > 0
                && priceAdjustableLineItems.size() == currentLineItems.size())
        {
            for (int i = 0; i < currentLineItems.size(); i++)
            {
                // Check each item to see if its current price is less than its original price
                // If so, then add the price adjustment line item to the transaction

                // Note: originalLineItem is a reference (not clone) to the line item in the original transaction
                // Don't modify its data unless you mean for it to be updated in the database, which will happen
                // if this price adjustment line item is performed
                SaleReturnLineItemIfc originalLineItem
                    = (SaleReturnLineItemIfc) priceAdjustableLineItems.get(i);
                SaleReturnLineItemIfc currentLineItem = (SaleReturnLineItemIfc)currentLineItems.get(i);

                if (PriceAdjustmentUtilities.isBetterDeal(originalLineItem, currentLineItem))
                {
                    PriceAdjustmentLineItemIfc priceAdjustmentLineItem =
                        createPriceAdjustmentLineItem(cargo, originalLineItem, currentLineItem,
                                originalTransaction, currentTransaction);

                    // Append the list of price adjustable line items, if applicable
                    if (priceAdjustmentLineItem != null)
                    {
                        priceAdjustableLineItemList.add(priceAdjustmentLineItem);

                        // Update return quantity of the original line item for updating in the database later
                        BigDecimal quantityReturned = priceAdjustmentLineItem.getItemQuantityDecimal();
                        originalLineItem.setQuantityReturned(quantityReturned);
                    }
                }
            }

            // Convert the ArrayList to an array to return
            if (priceAdjustableLineItemList.size() > 0)
            {
                priceAdjustmentLineItems = new PriceAdjustmentLineItemIfc[priceAdjustableLineItemList.size()];
                priceAdjustableLineItemList.toArray(priceAdjustmentLineItems);
            }
        }

        // Free up resources acquired for the dummy transaction
        dummyTransaction = null;

        return priceAdjustmentLineItems;
    }
    
    /**
     * update the original transaction line items if there are any return line
     * items in the current transaction
     * 
     * @param currentTransactionLineItems Current line item
     * @param lineItems Original line item
     * @param originalTransaction Original transaction
     */
    private void updateCurrentTransactionReturnItems(
            Vector<AbstractTransactionLineItemIfc> currentTransactionLineItems,
            Vector<AbstractTransactionLineItemIfc> lineItems, SaleReturnTransactionIfc originalTransaction)
    {
        for (int x = 0; x < lineItems.size(); x++)
        {
            SaleReturnLineItemIfc originalLineItem = (SaleReturnLineItemIfc)lineItems.get(x);
            for (int y = 0; y < currentTransactionLineItems.size(); y++)
            {
                SaleReturnLineItemIfc returnItem = (SaleReturnLineItemIfc)currentTransactionLineItems.get(y);
                if (returnItem.getReturnItem() != null
                        && originalTransaction.getTransactionIdentifier().equals(
                                returnItem.getReturnItem().getOriginalTransactionID())
                        && originalLineItem.getPLUItemID().equals(returnItem.getPLUItemID())
                        && originalLineItem.getExtendedDiscountedSellingPrice().equals(
                                returnItem.getExtendedDiscountedSellingPrice().negate()))
                {
                    originalTransaction.replaceLineItem(returnItem, originalLineItem.getLineNumber());
                }
            }
        }
    }

    /**
     * Creates a PriceAdjustmentLineItemIfc instance by converting the original
     * line item to a return item and creating a new sale line item for the new
     * price
     *
     * Postcondition: currentTransaction has all applicable price adjusted line
     * items added to it.
     *
     * @param originalLineItem
     *            Original line item
     * @param currentLineItem
     *            Current line item
     * @param originalTransaction
     *            Original transaction
     * @param currentTransaction
     *            Current transaction
     *
     * @return PriceAdjustmentLineItemIfc instance only if a better deal is
     *         determined. Null otherwise
     */
    protected PriceAdjustmentLineItemIfc createPriceAdjustmentLineItem(PriceAdjustmentCargo cargo,
                                                                       SaleReturnLineItemIfc originalLineItem,
                                                                       SaleReturnLineItemIfc currentLineItem,
                                                                       SaleReturnTransactionIfc originalTransaction,
                                                                       SaleReturnTransactionIfc currentTransaction)
    {
        PriceAdjustmentLineItemIfc priceAdjustmentLineItem = null;

        // Save the original line and transaction numbers for use
        // when updating the
        // original line item
        currentLineItem.setOriginalLineNumber(originalLineItem.getLineNumber());
        currentLineItem.setOriginalTransactionSequenceNumber(originalTransaction.getTransactionSequenceNumber());

        // Set the sales associate to the current associate
        currentLineItem.setSalesAssociate(currentTransaction.getSalesAssociate());

        currentTransaction.setTransactionTax(originalTransaction.getTransactionTax());

        // Create a return line item using the original line item info
        SaleReturnLineItemIfc returnLineItem = PriceAdjustmentUtilities.createReturnLineItem(originalTransaction,
                originalLineItem);

        // Add the price adjustment to the transaction
        priceAdjustmentLineItem = currentTransaction.addPriceAdjustmentLineItem(currentLineItem, returnLineItem);

        return priceAdjustmentLineItem;
    }

    /**
     * Checks to see if a line item is eligible for price adjustment.
     *
     * @param item
     *            Line item to test.
     * @return boolean True only if the item is eligible for price adjustment.
     */
    protected boolean isEligibleForPriceAdjustment(PriceAdjustmentCargo cargo, SaleReturnLineItemIfc item)
    {
        boolean isEligibleForPriceAdjustment = false;

        // $0 items are not eligible
        if ( !(item.getExtendedDiscountedSellingPrice().signum() == CurrencyIfc.ZERO) )
        {
            SaleReturnTransactionIfc originalTransaction = cargo.getOriginalTransaction();

            // Make sure that this item hasn't already been entered in the current transaction as a return
            boolean isReturnItemPresent = false;
            SaleReturnTransactionIfc currentTransaction = cargo.getTransaction();
            List<AbstractTransactionLineItemIfc> lineItems = currentTransaction.getLineItemsVector();
            for (int x = 0; x < lineItems.size(); x++)
            {
                SaleReturnLineItemIfc returnItem = (SaleReturnLineItemIfc)lineItems.get(x);
                if (returnItem.getReturnItem() != null && returnItem.getReturnItem().getOriginalTransactionID() != null
                        && originalTransaction.getTransactionIdentifier().equals(returnItem.getReturnItem().getOriginalTransactionID())
                        && item.getPLUItemID().equals(returnItem.getPLUItemID())
                        && item.getExtendedDiscountedSellingPrice().equals(returnItem.getExtendedDiscountedSellingPrice().negate()))
                {
                    isReturnItemPresent = true;
                    break;
                }
            }

            // Rules for price adjustment eligibility
            // 1) Not a return item entered in the current transaction
            // 2) Not a return item from a previous transaction
            // 3) Not a web order
            // 4) No damage discounts
            // 5) No employee discounts
            // 6) Not a previous price adjustment
            // 7) Not a gift certificate
            isEligibleForPriceAdjustment = !isReturnItemPresent && !item.isReturnLineItem()
                    && (item.getQuantityReturnable().signum() > 0) && !item.hasDamageDiscount()
                    && !item.hasEmployeeDiscount() && !item.isPartOfPriceAdjustment() && !item.isPriceAdjustmentLineItem()
                    && (item.getPLUItem().getItemClassification().getItemType())==1
                    && !item.isGiftCardIssue()&& !item.isGiftCardReload()&& !(item.getPLUItem() instanceof GiftCertificateItemIfc);

            // also, if the item is Pickup or delivery and was not yet picked up
            // set isEligibleForPriceAdjustment to false
            if(originalTransaction instanceof OrderTransactionIfc)
            {
                int orderType = ((OrderTransactionIfc)originalTransaction).getOrderType();
                int transactionType = originalTransaction.getTransactionType();
                if (orderType == OrderConstantsIfc.ORDER_TYPE_ON_HAND
                        && transactionType == TransactionIfc.TYPE_ORDER_INITIATE)
                {
                    if (item.getOrderItemStatus().getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_DELIVERY
                            || item.getOrderItemStatus().getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_PICKUP)
                    {
                        isEligibleForPriceAdjustment = false;
                    }
                }

            }
        }

        // If the item has passed the checks above, check to see if
        return isEligibleForPriceAdjustment;
    }


    /**
     * Carries Manual item and transaction discounts from the original transaction,
     * Specifically does not carry forward customer and best deal discounts.
     *
     * @param originalLineItem Line item from which to copy the discounts
     * @param currentLineItem Line item to which to copy the discounts
     * @param carryDiscountsForwardMethod corresponds to the setting of the Carry
     * Discounts Forward Method parameter
     */
    public void carryDiscountsForward(SaleReturnLineItemIfc originalLineItem, SaleReturnLineItemIfc currentLineItem,
                                      String carryDiscountsForwardMethod)
    {
        ItemPriceIfc originalItemPrice = originalLineItem.getItemPrice();
        ItemPriceIfc currentItemPrice = currentLineItem.getItemPrice();

        // If configured, convert the original transaction amounts to a percent and enter as a new discount
        if (ParameterConstantsIfc.PRICEADJUSTMENT_PriceAdjustmentCarryDiscountForwardMethod_PERCENT.equals(carryDiscountsForwardMethod))
        {
            ItemDiscountStrategyIfc[] itemDiscounts = originalItemPrice.getItemDiscounts();
            if (itemDiscounts != null && itemDiscounts.length > 0)
            {
                // Get discount percentage of original price. We're only interested in Manual discounts here.
                CurrencyIfc transDiscVal = originalItemPrice.getDiscountAmount(ItemDiscountStrategyIfc.DISCOUNT_SCOPE_ITEM, ItemDiscountStrategyIfc.ASSIGNMENT_MANUAL);
                transDiscVal = transDiscVal.add(originalItemPrice.getDiscountAmount(ItemDiscountStrategyIfc.DISCOUNT_SCOPE_ITEM, ItemDiscountStrategyIfc.ASSIGNMENT_EMPLOYEE));
                transDiscVal = transDiscVal.add(originalItemPrice.getDiscountAmount(ItemDiscountStrategyIfc.DISCOUNT_SCOPE_TRANSACTION, ItemDiscountStrategyIfc.ASSIGNMENT_MANUAL));
                transDiscVal = transDiscVal.add(originalItemPrice.getDiscountAmount(ItemDiscountStrategyIfc.DISCOUNT_SCOPE_TRANSACTION, ItemDiscountStrategyIfc.ASSIGNMENT_EMPLOYEE));

                CurrencyIfc transDiscPct = transDiscVal.divide(originalItemPrice.getExtendedSellingPrice());

                // Make sure percentage is not > 100%
                final CurrencyIfc hundredPct = DomainGateway.getBaseCurrencyInstance("1.00");
                if (transDiscPct.compareTo(hundredPct) == CurrencyIfc.GREATER_THAN)
                {
                    transDiscPct = hundredPct;
                }

                // Add the new percentage discount
                ItemDiscountByPercentageIfc percentDiscount =
                    DomainGateway.getFactory().getItemDiscountByPercentageInstance();
                percentDiscount.setDiscountRate(transDiscPct.getDecimalValue());
                percentDiscount.setDiscountAmount(transDiscPct.multiply(currentItemPrice.getExtendedSellingPrice()));
                percentDiscount.setAssignmentBasis(DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
                percentDiscount.setDiscountMethod(DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE);
                currentItemPrice.addItemDiscount(percentDiscount);
            }
        }
        // Otherwise, copy over all the discounts
        else
        {
            ItemDiscountStrategyIfc[] itemDiscounts = originalItemPrice.getItemDiscounts();
            if (itemDiscounts != null && itemDiscounts.length > 0)
            {
                // need to remove original discount.
                //currentItemPrice.clearItemDiscounts();
                currentItemPrice.clearItemDiscountByBasis(DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
                currentItemPrice.clearItemDiscountByBasis(DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE);

                for (int i = 0; i < itemDiscounts.length; i++)
                {
                    ItemDiscountStrategyIfc discount = (ItemDiscountStrategyIfc)itemDiscounts[i].clone();

                    // We're only interested in Manual discounts here.
                    if (discount.getAssignmentBasis() == ItemDiscountStrategyIfc.ASSIGNMENT_MANUAL
                        || discount.getAssignmentBasis() == ItemDiscountStrategyIfc.ASSIGNMENT_EMPLOYEE)
                    {
                        if (discount.getDiscountMethod() == ItemDiscountStrategyIfc.DISCOUNT_METHOD_PERCENTAGE)
                        {
                            discount.setDiscountAmount(currentItemPrice.getExtendedDiscountedSellingPrice().multiply(discount.getDiscountRate()));
                        }
                        currentItemPrice.addItemDiscount(discount);
                        currentItemPrice.calculateItemTotal();
                    }
                }

                // Get discount percentage of original price. We're only interested in Manual discounts here.
                CurrencyIfc discVal = currentItemPrice.getDiscountAmount(ItemDiscountStrategyIfc.DISCOUNT_SCOPE_ITEM, ItemDiscountStrategyIfc.ASSIGNMENT_MANUAL);
                discVal = discVal.add(currentItemPrice.getDiscountAmount(ItemDiscountStrategyIfc.DISCOUNT_SCOPE_ITEM, ItemDiscountStrategyIfc.ASSIGNMENT_EMPLOYEE));
                discVal = discVal.add(currentItemPrice.getDiscountAmount(ItemDiscountStrategyIfc.DISCOUNT_SCOPE_TRANSACTION, ItemDiscountStrategyIfc.ASSIGNMENT_MANUAL));
                discVal = discVal.add(currentItemPrice.getDiscountAmount(ItemDiscountStrategyIfc.DISCOUNT_SCOPE_TRANSACTION, ItemDiscountStrategyIfc.ASSIGNMENT_EMPLOYEE));

                if (discVal.compareTo(currentItemPrice.getExtendedSellingPrice()) == CurrencyIfc.GREATER_THAN)
                {
                    discVal = (CurrencyIfc)currentItemPrice.getExtendedSellingPrice().clone();
                }

                // Add the new percentage discount
                ItemDiscountByAmountIfc amountDiscount =
                    DomainGateway.getFactory().getItemDiscountByAmountInstance();
                amountDiscount.setDiscountAmount(discVal);
                amountDiscount.setAssignmentBasis(DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
                amountDiscount.setDiscountMethod(DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT);
                //currentItemPrice.clearItemDiscounts();
                currentItemPrice.clearItemDiscountByBasis(DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
                currentItemPrice.clearItemDiscountByBasis(DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE);

                currentItemPrice.addItemDiscount(amountDiscount);

            }
        }

        currentItemPrice.calculateItemTotal();
    }

    /**
     * Retrieve tenders from original transaction
     * @param tenderList
     * @return ReturnTenderDataElement[] list of tenders
     */
    protected ReturnTenderDataElementIfc[] getOriginalTenders(TenderLineItemIfc[] tenderList)
    {
        ReturnTenderDataElementIfc [] tenders = new ReturnTenderDataElementIfc[tenderList.length];
        for (int i =0; i < tenderList.length; i++)
        {
            tenders[i]=DomainGateway.getFactory().getReturnTenderDataElementInstance();
            tenders[i].setTenderType(tenderList[i].getTypeCode());
            if (tenderList[i].getTypeCode() == TenderLineItemIfc.TENDER_TYPE_CHARGE)
            {
                tenders[i].setCardType(((TenderChargeIfc)tenderList[i]).getCardType());
            }
            tenders[i].setAccountNumber(new String(tenderList[i].getNumber()));
            tenders[i].setTenderAmount(tenderList[i].getAmountTender());
        }
        return tenders;
    }
}
