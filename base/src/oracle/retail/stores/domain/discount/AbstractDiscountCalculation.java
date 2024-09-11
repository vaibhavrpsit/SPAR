/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/AbstractDiscountCalculation.java /main/31 2014/06/30 12:35:31 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   11/14/14 - Use isNonRetrievedReceiptedItem for mpos and pos.
 *    jswan     10/24/14  -Fix to prevent the application of employee transaction
 *                         discounts to return line items that originate from
 *                         a retrieved transaction.
 *    amishash  09/29/14  -Set the flag systemDiscountApplied for system
 *                         discounts
 *    yiqzhao   06/30/14 - Add isInStorePriceDuringPickup check for re-price.
 *    sgu       06/20/14 - disallow pickup cancel order item in tranactional
 *                         discount
 *    sgu       06/20/14 - disable transactional discount and tax override for
 *                         pickup cancel order line item
 *    mjwallac  12/19/13 - fix POS null dereferences (part 1)
 *    subrdey   10/21/13 - changed calculateDiscounts method to put non item
 *                         assignments as manual discounts
 *    tksharma  10/03/13 - fixed indexout of bound exception
 *    rabhawsa  08/30/13 - bestDiscount should be used while applying
 *                         transaction discounts.
 *    abhinavs  06/21/13 - Fix to remove return item from the list of
 *                         discountable items
 *    rabhawsa  04/09/13 - nullpointer fix in pickBestSystemDiscount().
 *    tksharma  04/03/13 - modified removeNonDiscountableItems method to not
 *                         remove the return items from the list
 *    tksharma  03/25/13 - added method applySystemDiscountstoItems() to attach
 *                         the bestSystem Discount to item for Sale Rece4ipt
 *                         printing
 *    tksharma  03/19/13 - ClassCastException fixed for manualDiscounts in
 *                         calculateDiscounts method
 *    tksharma  03/06/13 - Added pickBestSystemDiscount(Totals, discounts) to
 *                         get the bestSystemDiscount
 *    rabhawsa  01/29/13 - Enabling employee discount for items during return
 *                         without receipt
 *    asinton   06/08/12 - set discount and promotion total on
 *                         TransactionTotals so receipt can print You Saved
 *                         amount.
 *    sgu       08/23/12 - set discount amount
 *    sgu       08/23/12 - add support for transaction discount audit
 *    mjwallac  04/24/12 - Fixes for Fortify redundant null check
 *    mkutiana  03/06/12 - XbranchMerge
 *                         mkutiana_bug13692985-receipt_you_save_calc_3 from
 *                         rgbustores_13.4x_generic_branch
 *    mkutiana  02/28/12 - Adding transaction level discounts to you save
 *                         totals
 *    cgreene   03/18/11 - XbranchMerge cgreene_124_receipt_quick_wins from
 *                         main
 *    cgreene   03/16/11 - implement You Saved feature on reciept and
 *                         AllowMultipleQuantity parameter
 *    acadar    06/08/10 - changes for signature capture, disable txn send, and
 *                         discounts
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    ranojha   11/12/08 - Fixed Transaction Discount Amount for PosLog Forward
 *                         Port Defect
 *
 * ===========================================================================
 * $Log:
 *    7    360Commerce 1.6         8/23/2007 11:32:29 AM  Ashok.Mondal    CR
 *         28084 :Display discount amount for kit on sell item screen.
 *    6    360Commerce 1.5         4/25/2007 10:01:01 AM  Anda D. Cadar   I18N
 *         merge
 *    5    360Commerce 1.4         1/25/2006 4:10:47 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         1/22/2006 11:41:27 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:27:06 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:19:26 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:19 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     12/7/2005 22:52:34     Deepanshu       CR
 *         5249: Discount is not to be applied for KitHeader and PriceAdjusted
 *         line ietms
 *    3    360Commerce1.2         3/31/2005 15:27:06     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:19:26     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:09:19     Robert Pearse
 *
 *   Revision 1.5.2.5  2004/11/18 22:10:15  cdb
 *   @scr 7742 Added deprecation javadoc detail.
 *
 *   Revision 1.5.2.4  2004/11/18 22:05:04  cdb
 *   @scr 7742 Updated to impose order on added discounts to match calculation order. Modified
 *   price adjustment to correct for trans % discounts being applied before item amt discounts.
 *
 *   Revision 1.5.2.3  2004/11/13 14:07:02  cdb
 *   @scr 7622 Corrected prorate algorithm flaw.
 *
 *   Revision 1.5.2.2  2004/11/11 18:58:27  jdeleau
 *   @scr 7662 Fix rounding error on discounts, fix pro-rated amounts for
 *   employee discounts by amount on a transaction basis.
 *
 *   Revision 1.5  2004/09/23 00:30:53  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.4  2004/07/16 19:11:35  cdb
 *   @scr 4559 Discount Cleanup. Corrected prorate behavior for 0 subtotal to be
 *   more consistent with similar behavior.
 *
 *   Revision 1.3  2004/02/12 17:13:28  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:27  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:34:50   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   19 Jun 2002 17:15:04   pjf
 * Sort transaction discounts so Store Coupon assigned come first.
 * Resolution for POS SCR-1731: Discount - The receipt is printing the wrong discount amounts for trans% and store coupon
 *
 *    Rev 1.0   Jun 03 2002 16:49:02   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:57:22   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:17:42   msg
 * Initial revision.
 *
 *    Rev 1.4   06 Mar 2002 18:44:04   pjf
 * Discount performance enhancements.
 * Resolution for POS SCR-117: Fixed price discounts are not in parentheses on returns
 *
 *    Rev 1.3   21 Feb 2002 21:01:16   pjf
 * Changes for POS receipt requirements.
 * Resolution for POS SCR-1303: Item % disct on a kit comp is applied before AP disct and is rounding up
 * Resolution for POS SCR-1304: Store Coupon trans level does not print the coupon number on the receipt.
 *
 *    Rev 1.2   Feb 05 2002 16:34:08   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 *
 *    Rev 1.1   09 Jan 2002 15:07:40   pjf
 * Discount updates, correct SCR 161.
 * Resolution for POS SCR-161: Item discount % amount, not printing on sales receipt
 *
 *    Rev 1.0   Sep 20 2001 16:12:54   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:36:58   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.discount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.comparators.Comparators;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.DiscountableLineItemIfc;
import oracle.retail.stores.domain.lineitem.DiscountableTaxableLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;

/**
 * This is the abstract class for discount calculations. It contains method used
 * by the subclasses, e.g. StandardDiscountCalculation.
 *
 * @see oracle.retail.stores.domain.discount.DiscountCalculationIfc
 * @see oracle.retail.stores.domain.discount.StandardDiscountCalculation
 */
public abstract class AbstractDiscountCalculation implements DiscountCalculationIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 6831075202989735955L;

    /**
     * Calculates discounts, pro-rating transaction discounts across line items.
     *
     * @param totals transaction totals reference
     * @param lineItems vector of line items
     * @param discounts array of transaction discounts
     */
    public void calculateDiscounts(TransactionTotalsIfc totals, TransactionDiscountStrategyIfc[] discounts,
            Vector<AbstractTransactionLineItemIfc> lineItems)
    {
        int numDiscounts = 0;
        CurrencyIfc discountsApplied = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc discountAmount = null;
        TransactionDiscountStrategyIfc[] bestDiscounts = null;
        /*
         * Segregate the discounts as manual and system discounts.
         * Calculate the best system discount
         * Then club the bestSystem Discount with Manual Discounts and proceed.
         */
        if (discounts != null)
        {
            List<TransactionDiscountStrategyIfc> manualDiscounts = new ArrayList<>();
            List<TransactionDiscountStrategyIfc> systemDiscounts = new ArrayList<>();
            for (int i = 0; i < discounts.length; i++)
            {
                if (discounts[i].getAssignmentBasis() != TransactionDiscountStrategyIfc.ASSIGNMENT_ITEM)
                {
                    manualDiscounts.add(discounts[i]);
                }
                else
                {
                    systemDiscounts.add(discounts[i]);
                }
            }
            
            TransactionDiscountStrategyIfc[] systemDiscountsClone = new TransactionDiscountStrategyIfc[systemDiscounts
                    .size()];
            for (int i = 0; i < systemDiscounts.size(); i++)
            {
                systemDiscountsClone[i] = (TransactionDiscountStrategyIfc)systemDiscounts.get(i).clone();
            }
            TransactionDiscountStrategyIfc bestSystemDiscount = pickBestSystemDiscount(totals, systemDiscountsClone);
            if (bestSystemDiscount != null)
            {
                applySystemDiscountToItems(bestSystemDiscount, lineItems);
                // Set the flag systemDiscountApplied for system discounts. Changes as part of forward port 19692936
                for (int i = 0; i < systemDiscounts.size(); i++)
                {
                    TransactionDiscountStrategyIfc disc = systemDiscounts.get(i);
                    if (bestSystemDiscount.getRuleID() != null
                            && bestSystemDiscount.getRuleID().equals(disc.getRuleID()))
                    {
                        disc.setSystemDiscountApplied(true);
                    }
                    else
                    {
                        disc.setSystemDiscountApplied(false);
                    }
                }
            }
            int j = 0;
            if (bestSystemDiscount != null)
            {
                bestDiscounts = new TransactionDiscountStrategyIfc[manualDiscounts.size() + 1];
                bestDiscounts[0] = bestSystemDiscount;
                for (; j < manualDiscounts.size(); j++)
                {
                    bestDiscounts[j+1] = manualDiscounts.get(j);
                }
            }
            else
            {
                bestDiscounts = new TransactionDiscountStrategyIfc[manualDiscounts.size()];
                for (; j < manualDiscounts.size(); j++)
                {
                    bestDiscounts[j] = manualDiscounts.get(j);
                }
            }
            
        }
        
        if (bestDiscounts != null)
        {
            numDiscounts = bestDiscounts.length;
        }
        
        TransactionDiscountStrategyIfc ds = null;
        Vector<TransactionDiscountByPercentageIfc> dpVector = new Vector<TransactionDiscountByPercentageIfc>();
        Vector<TransactionDiscountByAmountIfc> daVector = new Vector<TransactionDiscountByAmountIfc>();
        Vector<CustomerDiscountByPercentageIfc> dcVector = new Vector<CustomerDiscountByPercentageIfc>();
        // clear transaction discounts from items, if necessary
        if (numDiscounts > 0)
        {
            DiscountableLineItemIfc dli = null;
            for (AbstractTransactionLineItemIfc atli : lineItems)
            {
                if (atli instanceof DiscountableLineItemIfc)
                {
                    dli = (DiscountableLineItemIfc) atli;
                    dli.clearTransactionDiscounts();
                }
            }
        }

        if (bestDiscounts != null)
        {
            // partition discounts
            for (int i = 0; i < numDiscounts; i++)
            {
                // read discounts
                ds = bestDiscounts[i];
                // if match found, keep it

                // check customer first since it's derived from
                // TransactionDiscountByPercentageIfc
                if (ds instanceof CustomerDiscountByPercentageIfc)
                {
                    dcVector.add((CustomerDiscountByPercentageIfc)ds);
                }
                else if (ds instanceof TransactionDiscountByPercentageIfc)
                {
                    dpVector.add((TransactionDiscountByPercentageIfc)ds);
                }
                else if (ds instanceof TransactionDiscountByAmountIfc)
                {
                    daVector.add((TransactionDiscountByAmountIfc)ds);
                }
            }
        }

        // do transaction discount by percentage
        // sort the collection so that discounts assigned by store coupons
        // are applied prior to non-store coupon discounts
        {
            Collections.sort(dpVector, Comparators.storeCouponsFirst);

            discountAmount = calculateTransactionDiscounts(lineItems, dpVector);
            // accumulate applied discounts
            discountsApplied = discountsApplied.add(discountAmount);
        }

        // do transaction discount by amount
        // sort the collection so that discounts assigned by store coupons
        // are applied prior to non-store coupon discounts
        {
            Collections.sort(daVector, Comparators.storeCouponsFirst);
            discountAmount = calculateTransactionDiscounts(lineItems, daVector);
            // accumulate applied discounts
            discountsApplied = discountsApplied.add(discountAmount);
        }

        // do Preferred Customer Discounts
        {
            discountAmount = calculateTransactionDiscounts(lineItems, dcVector);
            // accumulate applied discounts
            discountsApplied = discountsApplied.add(discountAmount);
        }

        // add discount to sale discount total (returned items don't get
        // transaction discounts), transaction discount total, discount
        // total
        totals.setSaleDiscountTotal(totals.getSaleDiscountTotal().add(discountsApplied));
        totals.setSaleDiscountAndPromotionTotal(totals.getSaleDiscountAndPromotionTotal().add(discountsApplied));
        totals.setDiscountTotal(totals.getDiscountTotal().add(discountsApplied));
        totals.setTransactionDiscountTotal(totals.getTransactionDiscountTotal().add(discountsApplied));
        // recalculate grand totals
        totals.calculateGrandTotal();

    }

    /**
     * Attaches the bestSystem Transaction discount to all items so that it can
     * be printed on Sale Receipt
     * 
     * @param bestSystemDiscount
     * @param lineItems
     */
    private void applySystemDiscountToItems(TransactionDiscountStrategyIfc bestSystemDiscount,
            Vector<AbstractTransactionLineItemIfc> lineItems)
    {
        if (lineItems != null)
        {
            Iterator i = lineItems.iterator();
            SaleReturnLineItemIfc li = null;

            while (i.hasNext())
            {
                li = (SaleReturnLineItemIfc)i.next();
                if (li.isDiscountEligible() || li.isInStorePriceDuringPickup() || !li.isReturnLineItem() || !li.isPickupCancelLineItem())
                {
                    li.getItemPrice().setBestSystemTransactionDiscount(bestSystemDiscount);
                }
            }
        }

    }

    /**
     * Calculates transaction discounts of a particular type.
     *
     * @param lineItems vector of line items
     * @param discounts Vector of disocunts
     * @param standingTotal subtotal without discounts (used for calculating
     *            amount of discount)
     * @param runningTotal running grand total
     * @return sum of discounts applied
     */
    public CurrencyIfc calculateTransactionDiscounts(Vector<AbstractTransactionLineItemIfc> incomingLineItems,
            Vector discounts)
    {
        // remove line items which aren't eligible for transaction discounts
        Vector discountableItems = removeNonDiscountableItems(incomingLineItems);
        Vector employeeDiscountableItems = removeNonEmployeeDiscountableItems(incomingLineItems);

        // check transaction discount by percentage
        int numDiscounts = discounts.size();
        CurrencyIfc discountAmount = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc discountsApplied = DomainGateway.getBaseCurrencyInstance();
        Vector lineItems = null;
        if (numDiscounts > 0)
        {
            for (int i = 0; i < numDiscounts; i++)
            {
                TransactionDiscountStrategyIfc disc = (TransactionDiscountStrategyIfc) discounts.get(i);

                if (disc instanceof TransactionDiscountAuditIfc)
                {
                    // For a transaction discount audit, donot recalute its item discounts. Instead call this
                    // new calcuateTransactionDiscount function to redistribute the saved item discounts.
                    discountAmount = calcuateTransactionDiscount(incomingLineItems, (TransactionDiscountAuditIfc)disc);
                }
                else
                {
                    // Recalcualte item discounts by prorate transaction discount across all eligible line items.
                    if (disc.getAssignmentBasis() != TransactionDiscountStrategyIfc.ASSIGNMENT_EMPLOYEE)
                    {
                        lineItems = discountableItems;
                    }
                    else
                    {
                        lineItems = employeeDiscountableItems;
                    }

                    if (disc instanceof CustomerDiscountByPercentageIfc)
                    {
                        if (((CustomerDiscountByPercentageIfc) disc).isIncludedInBestDeal()
                                && ((CustomerDiscountByPercentageIfc) disc).isEnabled())
                        { // cd will be applied only to
                            // items not part of an advanced pricing rule
                            lineItems = getNonAdvancedPricingItems(lineItems);
                        }
                    }
                    // maybe all the line items are part of a best deal so PCD
                    // should not apply
                    if (!lineItems.isEmpty())
                    {
                        // If the total amount of returns and sales are equal, no discount will be calculated.  This
                        // looks very odd as you sell and return the same plu over and over.  When the amounts match,
                        // there is no discount amount for the line items; when amounts don't this is a discount amount
                        // for each item.  The solution is calculate returns and sales seperately.
                        Vector<AbstractTransactionLineItemIfc> returnLineItems = new Vector<AbstractTransactionLineItemIfc>();
                        Vector<AbstractTransactionLineItemIfc> saleLineItems   = new Vector<AbstractTransactionLineItemIfc>();
                        for(Object lineItem: lineItems)
                        {
                            if (((SaleReturnLineItemIfc)lineItem).isReturnLineItem())
                            {
                                returnLineItems.add((AbstractTransactionLineItemIfc)lineItem);
                            }
                            else 
                            {
                                saleLineItems.add((AbstractTransactionLineItemIfc)lineItem);
                            }
                        }
                        
                        if (!returnLineItems.isEmpty())
                        {
                            discountAmount = discountAmount.add(calculateTransactionDiscount(returnLineItems, disc,
                                getDiscountEligibleSubtotal(returnLineItems)));
                        }

                        if (!saleLineItems.isEmpty())
                        {
                            discountAmount = discountAmount.add(calculateTransactionDiscount(saleLineItems, disc,
                                getDiscountEligibleSubtotal(saleLineItems)));
                        }
                    }
                }
                discountsApplied = discountsApplied.add(discountAmount);
            }
        }

        return (discountsApplied);
    }

    /**
     * Donot recalculate item transaction discounts. Instead redistribute the item discounts saved
     * in the transaction discount audit.
     * @param lineItems the line items
     * @param audit the transaction discount audit
     * @return total transaction discount amount
     */
    protected CurrencyIfc calcuateTransactionDiscount(Vector<AbstractTransactionLineItemIfc> lineItems,
            TransactionDiscountAuditIfc audit)
    {
        CurrencyIfc discountAmount = DomainGateway.getBaseCurrencyInstance();
        if (audit.isEnabled())
        {
            discountAmount = audit.getDiscountAmount();
            Collection<ItemTransactionDiscountAuditIfc>[] transactionItemDiscounts = audit.getItemDiscounts();
            if (transactionItemDiscounts != null)
            {
                for (int i=0; i<transactionItemDiscounts.length; i++)
                {
                    Collection<ItemTransactionDiscountAuditIfc> lineItemDiscounts = transactionItemDiscounts[i];
                    if (lineItemDiscounts != null)
                    {
                        for (ItemTransactionDiscountAuditIfc itemDiscount : lineItemDiscounts)
                        {
                            ((SaleReturnLineItemIfc)lineItems.get(i)).getItemPrice().addItemTransactionDiscount(itemDiscount);
                        }
                    }
                }
            }
        }
        return discountAmount;
    }

    /**
     * Calculates transaction discount and pro-rates it across line items using
     * pro-ration of remaining total algorithm. Grand total is also updated
     * after discount is calculated.
     *
     * @param lineItems vector of line items
     * @param dp TransactionDiscount object
     * @param standingTotal subtotal without discounts (used for calculating
     *            amount of discount)
     * @param runningTotal running grand total
     * @return amount calculated for this discount
     */
    protected CurrencyIfc calculateTransactionDiscount(Vector<AbstractTransactionLineItemIfc> lineItems,
            TransactionDiscountStrategyIfc dp, CurrencyIfc runningTotal)
    {

        // get total for this discount
        CurrencyIfc discountAmount = null;
        if (dp.isEnabled())
        {
            discountAmount = dp.calculateTransactionDiscountTotal(runningTotal);
        }
        // if disabled, set amount to zero (will force audit records)
        else
        {
            discountAmount = DomainGateway.getBaseCurrencyInstance();
        }
        dp.setDiscountAmount(discountAmount);
        // pro-rate discount amount across lines
        calculateProRatedTransactionDiscount(lineItems, dp, runningTotal, discountAmount);

        return (discountAmount);

    }

    /**
     * Calculates pro-rated transaction discount across line items using
     * pro-ration of remaining total algorithm.
     *
     * @param lineItems vector of line items
     * @param td transaction discount object
     * @param runningTotal running grand total
     * @param transactionDiscountAmount total of transaction discount
     */
    protected void calculateProRatedTransactionDiscount(Vector<AbstractTransactionLineItemIfc> lineItems,
            TransactionDiscountStrategyIfc td, CurrencyIfc runningTotal, CurrencyIfc transactionDiscountAmount)
    {
        // local references for line item, item price, dollar amounts
        AbstractTransactionLineItemIfc li = null;
        CurrencyIfc itemTransactionDiscountAmount = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc itemTotal = DomainGateway.getBaseCurrencyInstance();

        // get enumeration of line items
        Enumeration<AbstractTransactionLineItemIfc> e = lineItems.elements();

        // Need special handling of zero item total so discounts don't get
        // misplaced in the line items.
        if (runningTotal.signum() == 0)
        {
            int count = lineItems.size();

            // loop through line items , capturing sale,
            // return subtotals and item discounts
            while (e.hasMoreElements())
            {
                itemTransactionDiscountAmount = transactionDiscountAmount.divide(new BigDecimal(count));
                DiscountableLineItemIfc dtli = (DiscountableTaxableLineItemIfc) e.nextElement();
                // set line item transaction discount
                dtli.addTransactionDiscount(itemTransactionDiscountAmount, td);
                transactionDiscountAmount = transactionDiscountAmount.subtract(itemTransactionDiscountAmount);
                count--;
            }
        }
        else
        {

            // loop through line items , capturing sale,
            // return subtotals and item discounts
            while (e.hasMoreElements())
            {

                // localize references
                li = e.nextElement();

                SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) li;

                // only discount eligible items are handled here
                // removeNonDiscountableItems() was to eliminate
                // non-discountable items from array
                if (!srli.isKitHeader() && !srli.isPriceAdjustmentLineItem())
                {
                    DiscountableLineItemIfc dtli = (DiscountableTaxableLineItemIfc) li;

                    // calculate line total (Can't use
                    // getExtendedDiscountedSellingPrice due to precision loss
                    itemTotal = dtli.getExtendedSellingPrice();
                    itemTotal = itemTotal.subtract(dtli.getItemDiscountAmount());
                    itemTotal = itemTotal.subtract(dtli.getItemTransactionDiscountAmount());
                    // calculate item transaction amount, pro-rating against
                    // remainder of total
                    itemTransactionDiscountAmount = transactionDiscountAmount.prorate(itemTotal, runningTotal);
                    // set line item transaction discount
                    dtli.addTransactionDiscount(itemTransactionDiscountAmount, td);

                    // reset remaining totals
                    runningTotal = runningTotal.subtract(itemTotal);
                    transactionDiscountAmount = transactionDiscountAmount.subtract(itemTransactionDiscountAmount);
                    itemTotal.setZero();
                }
            }
        }
    }

    /**
     * Returns the items that are not part of advanced pricing rule.
     *
     * @param lineItems existing Vector of line items
     * @return nonAdvancedPricingItems line items eligible for
     *         PreferredCustomerDiscount
     */
    protected Vector getNonAdvancedPricingItems(Vector<AbstractTransactionLineItemIfc> lineItems)
    {
        Vector nonAdvancedPricingItems = new Vector();

        for (int i = 0; i < lineItems.size(); i++)
        {
            AbstractTransactionLineItemIfc lineItem = lineItems.get(i);
            // return only items that are not being used to satisfy
            // an advanced pricing rule
            if (((DiscountTargetIfc) lineItem).isTargetEnabled() && ((DiscountSourceIfc) lineItem).isSourceAvailable())
            {
                nonAdvancedPricingItems.add(lineItem);
            }
        }
        return nonAdvancedPricingItems;
    }

    /**
     * Removes non-discount-eligible items from list.
     * 
     * @param lineItems existing Vector of line items
     * @return discountableLineItems items eligible for discounting
     */
    public Vector removeNonDiscountableItems(Vector<AbstractTransactionLineItemIfc> lineItems)
    {
        Vector discountableItems = null;

        if (lineItems != null)
        {
            discountableItems = (Vector) lineItems.clone();
            Iterator i = discountableItems.iterator();
            SaleReturnLineItemIfc li = null;

            while (i.hasNext())
            {
                li = (SaleReturnLineItemIfc) i.next();
                if (!li.isDiscountEligible() || (li.isReturnLineItem() && (li.isFromTransaction() || li.getReturnItem().isNonRetrievedReceiptedItem())) || 
                		(li.isPickupCancelLineItem() && !li.isInStorePriceDuringPickup()))
                {
                    i.remove();
                }
            }
        }

        return discountableItems;
    }

    /**
     * Removes non-employee-discount-eligible items from list.
     *
     * @param lineItems existing Vector of line items
     * @return discountableLineItems items eligible for employee discount
     */
    public Vector removeNonEmployeeDiscountableItems(Vector<AbstractTransactionLineItemIfc> lineItems)
    {
        Vector discountableItems = null;

        if (lineItems != null)
        {
            discountableItems = (Vector) lineItems.clone();
            Iterator i = discountableItems.iterator();
            SaleReturnLineItemIfc li = null;

            while (i.hasNext())
            {
                li = (SaleReturnLineItemIfc) i.next();
                if (!li.getPLUItem().getItemClassification().getEmployeeDiscountAllowedFlag()
                        || (li.isReturnLineItem() && (li.isFromTransaction() || li.getReturnItem().isNonRetrievedReceiptedItem()))
                        || li.hasExternalPricing() || (li.isPickupCancelLineItem() && !li.isInStorePriceDuringPickup()))
                {
                    i.remove();
                }
            }
        }

        return discountableItems;
    }

    /**
     * Returns the current discounted selling price of the given Vector of line
     * items.
     *
     * @param discountableItems existing Vector of line items
     * @return The Currency subtotal amount of the given line items
     */
    public CurrencyIfc getDiscountEligibleSubtotal(Vector discountableItems)
    {
        CurrencyIfc runningTotal = DomainGateway.getBaseCurrencyInstance();
        for (int i = 0; i < discountableItems.size(); i++)
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) discountableItems.get(i);
            if (!srli.isKitHeader() && !srli.isPriceAdjustmentLineItem())
            {
                runningTotal = runningTotal.add(srli.getExtendedDiscountedSellingPrice());
            }
        }
        
        return runningTotal;
    }
    
    /**
     * This function returns the TransactionDiscount which gives the most
     * discount amount
     * 
     * @param totals
     * @param discounts
     * @return
     */
    protected TransactionDiscountStrategyIfc pickBestSystemDiscount(TransactionTotalsIfc totals,
            TransactionDiscountStrategyIfc[] discounts)
    {
        TransactionDiscountStrategyIfc bestSystemDiscount = null;
        CurrencyIfc bestDiscountAmount = null;
        //lets assume first discount is the the bestdiscount
        if (discounts != null && discounts.length > 0)
        {
            bestSystemDiscount = discounts[0];
            if (bestSystemDiscount instanceof TransactionDiscountByAmountStrategy)
            {
                bestDiscountAmount = bestSystemDiscount.getDiscountAmount();
            }
            else if (bestSystemDiscount instanceof TransactionDiscountByPercentageStrategy)
            {
                bestDiscountAmount = totals.getDiscountEligibleSubtotal()
                        .multiply(bestSystemDiscount.getDiscountRate());
            }
        }
        // now compare bestdiscount with other discounts, replace bestdiscount
        // with systemdiscount if required.
        for (int i = 1; i < discounts.length; i++)
        {
            TransactionDiscountStrategyIfc systemDiscount = discounts[i];
            CurrencyIfc systemDiscountAmount = null;
            if (systemDiscount instanceof TransactionDiscountByAmountStrategy)
            {
                systemDiscountAmount = systemDiscount.getDiscountAmount();
            }
            else if (systemDiscount instanceof TransactionDiscountByPercentageStrategy)
            {
                systemDiscountAmount = totals.getDiscountEligibleSubtotal().multiply(systemDiscount.getDiscountRate());
            }
            if (systemDiscountAmount != null && systemDiscountAmount.compareTo(bestDiscountAmount) == CurrencyIfc.GREATER_THAN)
            {
                bestSystemDiscount = systemDiscount;
                bestDiscountAmount = systemDiscountAmount;
            }
        }
        return bestSystemDiscount;
    }

}
