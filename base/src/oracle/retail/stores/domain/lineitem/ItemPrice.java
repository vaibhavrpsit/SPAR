/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/lineitem/ItemPrice.java /main/46 2014/07/14 18:14:39 sgu Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *   sgu    11/19/14 - add negateItemQuantity function for post voiding case
 *   sgu     07/14/14 - recalculate item total when transactino discount is
 *                      prorated
 *   mjwalla 01/09/14 - fix null dereferences
 *   tksharm 12/31/13 - modified calculateTransactionDiscounts method to
 *                      involve itemDiscountAmount
 *   tksharm 12/18/13 - clearItemTransactionDiscounts(int basis)
 *   subrdey 10/21/13 - modified clearTransactionDiscounts() to clear
 *                      itemTransactionDiscountsVector also
 *   tksharm 10/16/13 - changed calculateTransactionDiscounts to use
 *                      extendedSellingPrice instead of
 *                      extendedDiscountedSellingPrice
 *   tksharm 10/15/13 - corrected method clearTransactionScopeSystemDiscounts
 *                      to remove only Assignement Item discounts
 *   tksharm 10/03/13 - added method clearTransactionScopeSystemDiscounts
 *   yiqzhao 09/17/13 - Add recalculating transaction discounts.
 *   mkutian 06/06/13 - Rollback of previous fix, prevented BO generated
 *                      promotions (id=0) based transactions from creating IDSC
 *                      RTLog entry
 *   mjwalla 04/09/13 - Do not add promotional line item to a trans when item
 *                      is not part of a price promotion
 *   tksharm 03/25/13 - added getBestSystemTransactionDiscount and
 *                      setBestSystemTransactionDiscount for printing
 *                      transdiscount name on Sale receipt
 *   sgu     12/10/12 - prorate discount for order pickup and return
 *   tksharm 12/10/12 - commons-lang update 3.1
 *   sgu     10/16/12 - only prorate item if needed
 *   sgu     10/09/12 - create pickup cancel order transaction from an order
 *   sgu     08/24/12 - pass more transaction rule attributes to item
 *                      transaction rule
 *   sgu     08/17/12 - refactor discount audit
 *   sgu     08/16/12 - add ItemDiscountAudit discount rule
 *   sgu     08/14/12 - add ItemDiscountAudit
 *   jswan   01/05/12 - Refactor the status change of suspended transaction to
 *                      occur in a transaction so that status change can be
 *                      sent to CO as part of DTM.
 *   blarsen 02/22/12 - XbranchMerge
 *                      blarsen_bug13714601-order-pickup-stuck-in-tender-options
 *                      from rgbustores_13.4x_generic_branch
 *   blarsen 02/22/12 - Adding itemQuantity to calculateItemDiscount() call.
 *                      (Discount strategy was fixed to consistently calculate
 *                      discounts for % discounts with quantity > 1.)
 *   cgreene 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *   sgu     09/22/11 - add function to set sign for tax amounts
 *   sgu     09/22/11 - negate return tax in post void case
 *   cgreene 07/26/11 - corrected setting of promotion lineitem so that
 *                      readjustments, like setting a different customer,
 *                      overrides the previous promotion
 *   blarsen 12/22/10 - Refactored. Moved discount eligible logic into
 *                      DiscountUtility.
 *   cgreene 12/01/10 - implement saving applied promotion names into
 *                      tr_ltm_prm table
 *   npoola  08/17/10 - Added the new method getLineItemPriceCode to get
 *                      Individual Line Item price code
 *   cgreene 05/26/10 - convert to oracle packaging
 *   abondal 01/03/10 - update header date
 *   cgreene 06/22/09 - remove unnecassary creation of BigDecimal
 *   cgreene 03/31/09 - fixed missing promotion name on receipt by refactoring
 *                      appliedPromotion to ItemPrice object and
 *                      setPromotionName from PriceChange.setCloneAttributes
 *   cgreene 03/26/09 - implemented price override mraker in Items.bpt by
 *                      adding method to ItemPrice
 *   npoola  11/30/08 - CSP POS and BO changes
 *   mdecama 11/07/08 - I18N - Fixed Clone Method
 *   acadar  11/03/08 - transaction tax reason codes updates
 *   acadar  11/03/08 - localization of reason codes for discounts and merging
 *                      to tip
 *   acadar  10/31/08 - fixes
 *   acadar  10/31/08 - fixed the ispriceOverride
 *   acadar  10/30/08 - use localized reason codes for item and transaction
 *                      discounts
 *   acadar  10/28/08 - localization for item tax reason codes
 *   acadar  10/27/08 - fix broken unittests
 *   acadar  10/25/08 - localization of price override reason codes
 *   mdecama 10/23/08 - ReasonCode - Added new methods to the interfaces and
 *                      method stubs to the respective classes.
 *   cgreene 09/19/08 - updated with changes per FindBugs findings
 *   cgreene 09/11/08 - update header
 *
 * ===========================================================================
     $Log:
      11   360Commerce 1.10        4/29/2008 2:28:16 PM   Anil Rathore
           Updated to fix defect 31207. Code Reviewed by Sandy Gu.
      10   360Commerce 1.9         4/12/2008 5:44:57 PM   Christian Greene
           Upgrade StringBuffer to StringBuilder
      9    360Commerce 1.8         3/25/2008 6:24:02 AM   Manikandan Chellapan
           CR#30190 Forward porting of v12x CR#30190
      8    360Commerce 1.7         3/2/2008 2:08:09 PM    Jack G. Swan
           Changed to support a new column in the SaleReturnLineItem table
           that contains the Permanent Price at the time item was sold.  The
           new approach to pricing from Belieze invalidates the previous
           approach to getting the original retail price for RTLOG.
      7    360Commerce 1.6         5/18/2007 12:10:39 PM  Maisa De Camargo
           Added PromotionLineItems
      6    360Commerce 1.5         4/30/2007 5:38:35 PM   Sandy Gu        added
            api to handle inclusive tax
      5    360Commerce 1.4         4/25/2007 10:00:41 AM  Anda D. Cadar   I18N
           merge
      4    360Commerce 1.3         1/22/2006 11:41:39 AM  Ron W. Haight
           Removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:28:33 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:22:29 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:11:39 PM  Robert Pearse
     $
     Revision 1.22.2.4  2004/11/24 22:24:48  jdeleau
     @scr 7745 Make sure numbers for returns are negative in the ejournal.

     Revision 1.22.2.3  2004/11/18 22:05:05  cdb
     @scr 7742 Updated to impose order on added discounts to match calculation order. Modified
     price adjustment to correct for trans % discounts being applied before item amt discounts.

     Revision 1.22.2.2  2004/11/09 22:51:16  rsachdeva
     @scr 7610 Till Summary Report has wrong Item Disc. when multiple quantities are used

     Revision 1.22.2.1  2004/11/09 21:23:26  rsachdeva
     @scr 7610 Till Summary Report has wrong Item Disc. when multiple quantities are used

     Revision 1.22  2004/09/23 00:30:54  kmcbride
     @scr 7211: Inserting serialVersionUIDs in these Serializable classes

     Revision 1.21  2004/08/23 16:15:45  cdb
     @scr 4204 Removed tab characters

     Revision 1.20  2004/08/18 16:21:29  jriggins
     @scr 4985 Reworked CheckForPriceAdjustableItemsSite.carryDiscountsForward() removed need for PriceAdjustmentItemTransactionDiscount

     Revision 1.19  2004/07/30 22:50:01  jriggins
     @scr 4985 Reworked the CheckForPriceAdjustableItemsSite.carryDiscountsForward() method so that indivdual discounts are copied over and prorated as needed. This was needed for printing out the individual discounts on the receipt.

     Revision 1.18  2004/06/21 22:29:16  jdeleau
     @scr 3767 Make sure the default tax rate is used if no rules can be found.

     Revision 1.17  2004/06/02 13:33:47  mkp1
     @scr 2775 Implemented item tax overrides using new tax engine

     Revision 1.16  2004/05/27 16:59:23  mkp1
     @scr 2775 Checking in first revision of new tax engine.

     Revision 1.15  2004/05/20 22:54:56  cdb
     @scr 4204 Removed tabs from code base again.

     Revision 1.14  2004/05/19 18:33:31  cdb
     @scr 5103 Updating to more correctly handle register reports.

     Revision 1.13  2004/05/18 00:35:11  cdb
     @scr 5103    Corrected behavior of item and transaction discounts.

     Revision 1.12  2004/05/14 00:00:42  cdb
     @scr 5103 Updated to retrieve assignment basis of employee discounts for
     transactions. Removed debugging print statements.

     Revision 1.11  2004/05/13 20:56:21  cdb
     @scr 5103 Added Employee Discounts to Finanancial Totals.

     Revision 1.10  2004/05/11 23:03:02  jdeleau
     @scr 4218 Backout recent changes to remove TransactionDiscounts,
     going to go a different route and remove the newly added
     voids and grosses instead.

     Revision 1.8  2004/05/05 22:33:04  jriggins
     @scr 4681 Added getTotalItemDiscountsByAmount()

     Revision 1.7  2004/04/09 21:21:43  mweis
     @scr 4206 JavaDoc updates.

     Revision 1.6  2004/03/16 18:27:07  cdb
     @scr 0 Removed tabs from all java source code.

     Revision 1.5  2004/03/15 20:28:34  cdb
     @scr 3588 Updated ItemPrice test. Removed ItemPrice deprecated (2 release) methods.

     Revision 1.4  2004/03/02 23:14:06  cdb
     @scr 3588 Updated so item discount audits get employee
     ID for employee transaction discounts.

     Revision 1.3  2004/02/12 17:13:57  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:26:32  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:32  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.1   Feb 09 2004 14:56:02   cdb
 * Made methods for clearing item discounts more precise to handle damage and employee discount conditions.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.0   Aug 29 2003 15:37:58   CSchellenger
 * Initial revision.
 *
 *    Rev 1.7   Jul 25 2003 21:21:56   sfl
 * Applied aboslute value to the discount amount before it is being substracted from total item mark down.
 * Resolution for POS SCR-3249: Voiding trans completed w/ Item Markdowns where price = 0 incorrectly updates the Amount line for Item Markdown
 *
 *    Rev 1.6   10 Jul 2003 06:32:46   mpm
 * Added price override authorization support.
 *
 *    Rev 1.5   Jul 03 2003 13:59:26   jgs
 * Added check for void processing on markdowns.
 * Resolution for 2887: Voiding a markdown trans does not update the Item Markdown line correctly
 *
 *    Rev 1.4   Jun 06 2003 14:17:58   RSachdeva
 * Item Discounts count and amount not capturing for Void Transactions
 * Resolution for POS SCR-2554: Item Discounts not capturing and printing data correctly on Statistical Summary Report
 *
 *    Rev 1.3   Jan 22 2003 15:30:28   mpb
 * SCR #1626
 * Added methods to support getting and clearing markdowns in addition to discounts.
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.2   11 Jun 2002 16:25:24   jbp
 * changes to report markdowns
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.1   05 Jun 2002 17:11:56   jbp
 * changes for pricing updates
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.0   Jun 03 2002 16:58:12   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.domain.lineitem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.PriceCodeConverter;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByAmountIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByAmountStrategy;
import oracle.retail.stores.domain.discount.ItemDiscountByPercentageIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByPercentageStrategy;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAudit;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAuditIfc;
import oracle.retail.stores.domain.discount.PromotionLineItem;
import oracle.retail.stores.domain.discount.PromotionLineItemIfc;
import oracle.retail.stores.domain.discount.ReturnItemTransactionDiscountAuditIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.event.PriceChangeIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.tax.RunTimeTaxRuleIfc;
import oracle.retail.stores.domain.tax.TaxRuleIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.DiscountUtility;
import oracle.retail.stores.domain.utility.DomainUtil;
import oracle.retail.stores.domain.utility.SecurityOverrideIfc;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.Logger;

/**
 * Item pricing object.
 *
 * @version $Revision: /main/46 $
 */
public class ItemPrice implements ItemPriceIfc, DiscountRuleConstantsIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 2853148947303375535L;

    /** The logger to which log messages will be sent. **/
    protected static final Logger logger = Logger.getLogger(ItemPrice.class);

    /**
     * calculated selling price (excluding discount)
     */
    protected CurrencyIfc sellingPrice;

    /**
     * extended selling price (excluding discount)
     */
    protected CurrencyIfc extendedSellingPrice;

    /**
     * extended discounted selling price
     */
    protected CurrencyIfc extendedDiscountedSellingPrice;

    /**
     * line item total (extended selling price - discount + tax
     */
    protected CurrencyIfc itemTotal;

    /**
     * item quantity
     */
    protected BigDecimal itemQuantity = null;

    /**
     * line item discount amount (does not include amounts from transaction
     * discounts)
     */
    protected CurrencyIfc itemDiscountAmount;

    /**
     * transaction discount amount for line item
     */
    protected CurrencyIfc itemTransactionDiscountAmount;

    /**
     * line item discount total (from both transaction and item discounts)
     */
    protected CurrencyIfc itemDiscountTotal;

    /**
     * line item restocking fee (only for return items)
     */
    protected CurrencyIfc restockingFee = null;

    /**
     * line item extended restocking fee (restocking fee * item quantity)
     */
    protected CurrencyIfc extendedRestockingFee = null;

    /**
     * discounts vector
     */
    protected Vector<ItemDiscountStrategyIfc> itemDiscountsVector = new Vector<ItemDiscountStrategyIfc>();
    
    /**
     * item transaction discounts vector
     */
    protected Vector<TransactionDiscountStrategyIfc> itemTransactionDiscountsVector = new Vector<TransactionDiscountStrategyIfc>();
    /**
     * Localized Reason code
     */
    protected LocalizedCodeIfc itemPriceOverrideReason = DomainGateway.getFactory().getLocalizedCode();

    /**
     * price override authorization
     */
    protected SecurityOverrideIfc priceOverrideAuthorization = null;

    /**
     * item tax object
     */
    protected ItemTaxIfc itemTax = null;

    /**
     * indicator item is eligible for discounting
     */
    protected boolean discountEligible = true;

    /**
     * indicator item is eligible for employee discounting
     */
    protected boolean employeeDiscountEligible = true;

    /**
     * indicator item is eligible for damage discounting
     */
    protected boolean damageDiscountEligible = true;

    /**
     * Promotion lineitems used to record promotions applied to effect this line
     * item's price. As of and up to v13.3 there is not going to be more than
     * one of these objects. Price adjustments, manual discounts and complex
     * promotions are recorded in other manners.
     */
    protected Vector<PromotionLineItemIfc> promotionLineItems = new Vector<PromotionLineItemIfc>(1);

    /**
     * The current permanent selling prive (excluding discounts) at the time the
     * item was or is being sold.
     */
    protected CurrencyIfc permanentSellingPrice;

    /**
     * The reference to the promotion applied to this line item.
     *
     * @since 13.1
     * @deprecated as of 13.3 use the lighter-weight {@link #promotionLineItems}
     *      object instead.
     */
    protected PriceChangeIfc appliedPromotion;
    
    /**
     * Transction discount applied by system on the lineitem
     */
    protected TransactionDiscountStrategyIfc bestSystemTransactionDiscount;

    /**
     * Constructs ItemPrice object.
     */
    public ItemPrice()
    {
        itemQuantity = BigDecimal.ZERO;
        sellingPrice = DomainGateway.getBaseCurrencyInstance();
        permanentSellingPrice = DomainGateway.getBaseCurrencyInstance();
        extendedSellingPrice = DomainGateway.getBaseCurrencyInstance();
        extendedDiscountedSellingPrice = DomainGateway.getBaseCurrencyInstance();
        itemTotal = DomainGateway.getBaseCurrencyInstance();
        itemDiscountAmount = DomainGateway.getBaseCurrencyInstance();
        itemTransactionDiscountAmount = DomainGateway.getBaseCurrencyInstance();
        itemDiscountTotal = DomainGateway.getBaseCurrencyInstance();
        itemTax = DomainGateway.getFactory().getItemTaxInstance();
        itemTax.setItemPrice(this);
    }

    /**
     * Constructs copy of ItemPrice.
     *
     * @return generic object copy of item price object
     */
    public Object clone()
    {
        ItemPriceIfc ip = DomainGateway.getFactory().getItemPriceInstance();
        setCloneAttributes((ItemPrice)ip);
        return ip;
    }

    /**
     * Sets the Reason
     *
     * @return
     */
    public LocalizedCodeIfc getItemPriceOverrideReason()
    {
        return itemPriceOverrideReason;
    }

    /**
     * Gets the Reason
     *
     * @param reason
     */
    public void setItemPriceOverrideReason(LocalizedCodeIfc reason)
    {
        itemPriceOverrideReason = reason;
    }

    /**
     * Sets attributes for clone.
     *
     * @param newClass instance of new object
     */
    protected void setCloneAttributes(ItemPrice newClass)
    {
        newClass.setItemQuantity(itemQuantity);
        newClass.setSellingPrice((CurrencyIfc)sellingPrice.clone());
        newClass.setPermanentSellingPrice((CurrencyIfc)permanentSellingPrice.clone());
        newClass.setItemTax((ItemTaxIfc)itemTax.clone());
        for (ItemDiscountStrategyIfc d : itemDiscountsVector)
        {
            newClass.addItemDiscount((ItemDiscountStrategyIfc)d.clone());
        }

        for (TransactionDiscountStrategyIfc d : itemTransactionDiscountsVector)
        {
            newClass.addItemTransactionDiscount((TransactionDiscountStrategyIfc)d.clone());
        }

        // clone other entries
        newClass.extendedSellingPrice = (CurrencyIfc)extendedSellingPrice.clone();
        newClass.extendedDiscountedSellingPrice = (CurrencyIfc)extendedDiscountedSellingPrice.clone();
        newClass.itemTotal = (CurrencyIfc)itemTotal.clone();
        newClass.itemDiscountAmount = (CurrencyIfc)itemDiscountAmount.clone();
        newClass.itemDiscountTotal = (CurrencyIfc)itemDiscountTotal.clone();
        newClass.itemTransactionDiscountAmount = (CurrencyIfc)itemTransactionDiscountAmount.clone();
        if (itemPriceOverrideReason != null)
        {
            newClass.itemPriceOverrideReason = (LocalizedCodeIfc)itemPriceOverrideReason.clone();
        }
        newClass.discountEligible = discountEligible;
        newClass.employeeDiscountEligible = employeeDiscountEligible;
        newClass.damageDiscountEligible = damageDiscountEligible;

        if (restockingFee != null)
        {
            newClass.restockingFee = (CurrencyIfc)restockingFee.clone();
        }

        if (extendedRestockingFee != null)
        {
            newClass.extendedRestockingFee = (CurrencyIfc)extendedRestockingFee.clone();
        }
        if (priceOverrideAuthorization != null)
        {
            newClass.priceOverrideAuthorization = (SecurityOverrideIfc)priceOverrideAuthorization.clone();
        }
        if (appliedPromotion != null)
        {
            newClass.appliedPromotion = (PriceChangeIfc)appliedPromotion.clone();
        }
        if (promotionLineItems != null)
        {
            for (PromotionLineItemIfc newPromotionLineItem : promotionLineItems)
            {
                newClass.addPromotionLineItem((PromotionLineItemIfc)newPromotionLineItem.clone());
            }
        }
        if (bestSystemTransactionDiscount != null)
        {
            newClass.bestSystemTransactionDiscount = (TransactionDiscountStrategyIfc)bestSystemTransactionDiscount.clone();
        }
    }

    /**
     * Calculate line item total.
     *
     * @return line item total
     */
    public CurrencyIfc calculateItemTotal()
    {
        extendedSellingPrice = sellingPrice.multiply(getItemQuantityDecimal());
        itemDiscountAmount.setZero();

        calculateBestDealDiscount();
        
        calculatePercentageDiscounts();
        calculateAmountDiscounts();
        
        calculateTransactionDiscounts();
        
        calculateReturnItemDiscounts();

        recalculateItemTotal();
        return itemTotal;
    }
 
    /**
     * Negate line item total.
     * 
     * @return line item total
     */
    public CurrencyIfc negateItemTotal()
    {
    	itemQuantity = itemQuantity.negate();
        extendedSellingPrice = sellingPrice.multiply(itemQuantity);
        
        negateItemDiscounts();
        recalculateItemTotal();
        return itemTotal; 	
    }

    /**
     * Calculate best deal discount.
     */
    protected void calculateBestDealDiscount()
    {
        // Calculate the discount that corresponds to the best deal
        ItemDiscountStrategyIfc bd = getBestDealDiscount();
        if (bd != null && isDiscountEligible())
        {
            CurrencyIfc localDisc = bd.calculateItemDiscount(extendedSellingPrice, itemQuantity);
            bd.setItemDiscountAmount(localDisc);
            itemDiscountAmount = itemDiscountAmount.add(localDisc);
        }
    }

    /**
     * Calculate percentage discounts.
     */
    protected void calculatePercentageDiscounts()
    {
        // calculate discounts by percentage
        ItemDiscountStrategyIfc[] d = getItemDiscountsByPercentage();
        if (d != null)
        {
            for (int i = 0; i < d.length; i++)
            {
                if (DiscountUtility.isDiscountEligible(this, d[i]))
                {
                        CurrencyIfc tempPrice = extendedSellingPrice.subtract(itemDiscountAmount);
                        CurrencyIfc localDisc = d[i].calculateItemDiscount(tempPrice, itemQuantity);
                        d[i].setItemDiscountAmount(localDisc);
                        itemDiscountAmount = itemDiscountAmount.add(localDisc);
                }
            }
        }
    }


    /**
     * Calculate dollar amount discounts.
     */
    protected void calculateAmountDiscounts()
    {
        // calculate manual discount by amount
        ItemDiscountStrategyIfc[] d = getItemDiscountsByAmount();
        if (d != null)
        {
            CurrencyIfc c = null;
            for (int i = 0; i < d.length; i++)
            {
                if (DiscountUtility.isDiscountEligible(this, d[i]))
                {
                    c = d[i].calculateItemDiscount(extendedSellingPrice, itemQuantity);
                    d[i].setItemDiscountAmount(c);
                    itemDiscountAmount = itemDiscountAmount.add(c);
                }
            }
        }
    }

    /**
     * Re-calculate transaction discounts. Transaction discount amounts(calculated by percentage) may change
     * after applying other discounts, such as item markdown.
     */
    protected void calculateTransactionDiscounts()
    {
        itemTransactionDiscountAmount.setZero();
   
        Iterator<TransactionDiscountStrategyIfc> d = itemTransactionDiscountsVector.iterator();
        if (d != null)
        {
            while (d.hasNext())
            {
                TransactionDiscountStrategyIfc rds = d.next();
                CurrencyIfc tempPrice = extendedSellingPrice.subtract(itemDiscountAmount).subtract(itemTransactionDiscountAmount);
                CurrencyIfc localDisc = rds.calculateTransactionDiscountTotal(tempPrice);
                rds.setDiscountAmount(localDisc);
                
                itemTransactionDiscountAmount = itemTransactionDiscountAmount.add(localDisc);
            }
        }
    }
        
    /**
     * Calculate discounts for return items.
     */
    protected void calculateReturnItemDiscounts()
    {
        // get discounts for returned items
        ItemDiscountStrategyIfc[] d = getReturnItemDiscounts();
        if (d != null)
        {
            CurrencyIfc c = null;
            for (int i = 0; i < d.length; i++)
            {
                if (DiscountUtility.isDiscountEligible(this, d[i]))
                {
                    c = d[i].calculateItemDiscount(extendedSellingPrice, itemQuantity);
                    d[i].setItemDiscountAmount(c);
                    // stored discount value is for single item
                    itemDiscountAmount = itemDiscountAmount.add(c);
                }
            }
        }
    }

    /**
     * Negate item level discounts
     */
    protected void negateItemDiscounts()
    {
        ItemDiscountStrategyIfc[] discounts = getItemDiscounts();
       
        for (ItemDiscountStrategyIfc discount : discounts)
        {
        	discount.setDiscountAmount(discount.getDiscountAmount().negate());
            discount.setItemDiscountAmount(discount.getItemDiscountAmount().negate());
        }
        
        // Negate total item level discount
        itemDiscountAmount = itemDiscountAmount.negate();
        itemTransactionDiscountAmount = itemTransactionDiscountAmount.negate();
    }

    /**
     * Calculate total for item.
     */
    public void recalculateItemTotal()
    {
        itemTotal = extendedSellingPrice.subtract(itemDiscountAmount);
        itemTotal = itemTotal.subtract(itemTransactionDiscountAmount);
        itemDiscountTotal = extendedSellingPrice.subtract(itemTotal);
        extendedDiscountedSellingPrice.setStringValue(itemTotal.getStringValue());
        itemTax.setItemTaxableAmount(itemTotal);
        itemTax.setSignforTaxAmounts(itemTotal.signum());
        itemTotal = itemTotal.add(getItemTaxAmount());
        // add the restocking fee (only for return items)
        if (restockingFee != null)
        {
            extendedRestockingFee = restockingFee.multiply(itemQuantity);
            itemTotal = itemTotal.subtract(extendedRestockingFee);
        }
    }
    
    

    
    /**
     * Prorate order item for pickup or cancel
     * @param qty the quantity to pickup or cancel
     * @param pickedupQty the quantity picked up 
     * @param orderItemStatus the order item status
     */
    public void prorateOrderItemForPickupOrCancel(BigDecimal qty, BigDecimal pickedupQty,
           OrderItemStatusIfc orderItemStatus)
    {
        // set new quantity. qty is always positive.
        setItemQuantity(qty);
        
        // pendingQty is the item quantity that has not been picked up, shipped, or cancelled.
        BigDecimal pendingQty = orderItemStatus.getQuantityOrdered().
                subtract(orderItemStatus.getQuantityPickedUp()).
                subtract(orderItemStatus.getQuantityShipped()).
                subtract(orderItemStatus.getQuantityCancelled());
       
        // prorate all item discounts
        Vector<ItemDiscountStrategyIfc> proratedItemDiscountVector = new Vector<ItemDiscountStrategyIfc>();
        for (ItemDiscountStrategyIfc itemDiscount : itemDiscountsVector)
        {
            OrderItemDiscountStatusIfc discountStatus = orderItemStatus.getDiscountStatus(
                    itemDiscount.getOrderItemDiscountLineReference());
            ItemDiscountStrategyIfc proratedItemDiscount = itemDiscount.getProratedOrderItemDiscountForPickupOrCancel(
                    qty, pickedupQty, pendingQty, discountStatus);

            proratedItemDiscountVector.add(proratedItemDiscount);
        }
        itemDiscountsVector = proratedItemDiscountVector;

        // recalculate item total
        calculateItemTotal();
    }
    
    /**
     * Prorate order item for return 
     * @param returnQty the quantity to return
     * @param orderItemStatus the order item status
     */
    public void prorateOrderItemForReturn(BigDecimal returnQty, OrderItemStatusIfc orderItemStatus)
    {
        // set new quantity. returnQty is always negative.
        setItemQuantity(returnQty);
        
        // returnableQty is the item quantity that has been picked up or shipped, and not returned.
        BigDecimal returnableQty = orderItemStatus.getQuantityPickedUp().add(
                orderItemStatus.getQuantityShipped()).subtract(
                orderItemStatus.getQuantityReturned());
       
        // prorate all item discounts
        Vector<ItemDiscountStrategyIfc> proratedItemDiscountVector = new Vector<ItemDiscountStrategyIfc>();
        for (ItemDiscountStrategyIfc itemDiscount : itemDiscountsVector)
        {
            OrderItemDiscountStatusIfc discountStatus = orderItemStatus.getDiscountStatus(
                    itemDiscount.getOrderItemDiscountLineReference());
            ItemDiscountStrategyIfc proratedItemDiscount = itemDiscount.getProratedOrderItemDiscountForReturn(
                    returnQty, returnableQty, discountStatus);

            proratedItemDiscountVector.add(proratedItemDiscount);
        }
        itemDiscountsVector = proratedItemDiscountVector;

        // recalculate item total
        calculateItemTotal();
    }


    /**
     * Override line item tax rate.
     *
     * @param newRate new tax rate
     * @param reason reason code
     * @deprecated as of 13.1. Use {@link overrideTaxRate(double,
     *             LocalizedCodeIfc reason)}
     */
    public void overrideTaxRate(double newRate, int reason)
    {
        // reset values in tax object
        itemTax.overrideTaxRate(newRate, reason);
        calculateItemTotal();
    }

    /**
     * Override line item tax rate.
     *
     * @param newRate new tax rate
     * @param reason reason code
     */
    public void overrideTaxRate(double newRate, LocalizedCodeIfc reason)
    {
        // reset values in tax object
        itemTax.overrideTaxRate(newRate, reason);
        calculateItemTotal();
    }

    /**
     * Override line item tax rate.
     *
     * @param amount new amount
     * @param reason reason code
     * @deprecated as of 13.1. Use
     *             {@overrideTaxAmount(CurrencyIfc, LocalizedCodeIfc)}
     */
    public void overrideTaxAmount(CurrencyIfc amount, int reason)
    {
        // reset values in tax object
        itemTax.overrideTaxAmount(amount, reason);
        calculateItemTotal();
    }

    /**
     * Override line item tax rate.
     *
     * @param amount new amount
     * @param reason reason code
     */
    public void overrideTaxAmount(CurrencyIfc amount, LocalizedCodeIfc reason)
    {
        // reset values in tax object
        itemTax.overrideTaxAmount(amount, reason);
        calculateItemTotal();
    }

    /**
     * Clears tax override and restores standard default tax rules. This is the
     * same as toggleTax to on.
     */
    public void clearTaxOverride()
    {
        LocalizedCodeIfc reasonCode = DomainGateway.getFactory().getLocalizedCode();
        reasonCode.setCode(CodeConstantsIfc.CODE_UNDEFINED);
        toggleTax(true, reasonCode);
    }

    /**
     * Toggle tax off (false) or on (true).
     *
     * @param toggle switch indicating tax is on (true) or off (false)
     * @param reason reason code (of tax being switched off)
     * @deprecated as of 13.1 Use {@link toggleTax(boolean, LocalizedCodeIfc)}
     */
    public void toggleTax(boolean toggle, int reason)
    {
        itemTax.toggleTax(toggle, reason);
        calculateItemTotal();
    }

    /**
     * Toggle tax off (false) or on (true).
     *
     * @param toggle switch indicating tax is on (true) or off (false)
     * @param reason reason code (of tax being switched off)
     */
    public void toggleTax(boolean toggle, LocalizedCodeIfc reason)
    {
        itemTax.toggleTax(toggle, reason);
        calculateItemTotal();
    }

    /**
     * Overrides item selling price
     *
     * @param CurrencyIfc new price
     * @param LocalizedCodeIfc reason
     */
    public void overridePrice(CurrencyIfc newPrice, LocalizedCodeIfc reason)
    {
        sellingPrice = newPrice;
        itemPriceOverrideReason = reason;
    }

    /**
     * Returns financial totals objects containing item discounts data.
     *
     * @return totals FinancialTotals object bearing item discounts data
     */
    public FinancialTotalsIfc getItemDiscountsFinancialTotals()
    {
        // create totals object
        FinancialTotalsIfc totals = DomainGateway.getFactory().getFinancialTotalsInstance();

        // loop through item discounts
        ItemDiscountStrategyIfc d = null;

        // initialize currency totals
        CurrencyIfc totalItemDiscount = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc totalEmployeeItemDiscount = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc totalTransactionDiscount = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc totalEmployeeTransactionDiscount = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc totalItemMarkdown = DomainGateway.getBaseCurrencyInstance();

        if (getItemDiscounts() != null)
        {
            int numDiscounts = itemDiscountsVector.size();
            for (int i = 0; i < numDiscounts; i++)
            { // begin loop through discounts
                // retrieve element
                d = itemDiscountsVector.get(i);
                CurrencyIfc tempDiscountAmount = d.getDiscountAmount();

                // if an item discount, retrieve data
                if (d.getDiscountScope() == DiscountRuleConstantsIfc.DISCOUNT_SCOPE_ITEM)
                {
                    int unit = 1;
                    if (itemQuantity.signum() < 0)
                    {
                        unit = -1;
                    }

                    if (d.getDiscountMethod() == DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT)
                    {
                        tempDiscountAmount = tempDiscountAmount.multiply(itemQuantity.abs());
                    }

                    if (d.getAccountingMethod() == DiscountRuleConstantsIfc.ACCOUNTING_METHOD_DISCOUNT)
                    {
                        if (d.getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE)
                        {
                            totals.addUnitsGrossItemEmployeeDiscount(new BigDecimal(unit));
                            totalEmployeeItemDiscount = totalEmployeeItemDiscount.add(tempDiscountAmount);
                        }
                        else
                        {
                            totals.addNumberItemDiscounts(unit);
                            totalItemDiscount = totalItemDiscount.add(tempDiscountAmount);
                        }
                    }
                    else if (d.getAccountingMethod() == DiscountRuleConstantsIfc.ACCOUNTING_METHOD_MARKDOWN)
                    {
                        totals.addNumberItemMarkdowns(unit);
                        totalItemMarkdown = totalItemMarkdown.add(tempDiscountAmount);
                    }
                }
                else if (d.getDiscountScope() == DiscountRuleConstantsIfc.DISCOUNT_SCOPE_TRANSACTION)
                {
                    if (d.getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE)
                    {
                        totalEmployeeTransactionDiscount = totalEmployeeTransactionDiscount.add(tempDiscountAmount);
                    }
                    else
                    {
                        totalTransactionDiscount = totalTransactionDiscount.add(tempDiscountAmount);
                    }
                }

            } // end loop through discounts
        }

        // add item discount amount
        totals.addAmountItemDiscounts(totalItemDiscount);
        totals.addAmountGrossItemEmployeeDiscount(totalEmployeeItemDiscount);
        totals.addAmountItemMarkdowns(totalItemMarkdown);
        // add transaction discount amount
        totals.addAmountTransactionDiscounts(totalTransactionDiscount);
        totals.addAmountGrossTransactionEmployeeDiscount(totalEmployeeTransactionDiscount);

        // pass back totals
        return (totals);
    }

    /**
     * Returns financial totals objects containing item discounts data.
     *
     * @param discAction discount Action to decide increment/decrement
     * @return totals FinancialTotals object bearing item discounts data
     */
    public FinancialTotalsIfc getItemDiscountsFinancialTotals(boolean discAction)
    {
        return getItemDiscountsFinancialTotals();
    }

    /**
     * Retrieves calculated selling price (excluding discount).
     *
     * @return selling price
     */
    public CurrencyIfc getSellingPrice()
    {
        return (sellingPrice);
    }

    /**
     * Sets line item selling price.
     *
     * @param value new selling price
     */
    public void setSellingPrice(CurrencyIfc value)
    {
        sellingPrice = value;
    }

    /**
     * Retrieves calculated extended selling price (excluding discount).
     *
     * @return extended selling price
     */
    public CurrencyIfc getExtendedSellingPrice()
    {
        return (extendedSellingPrice);
    }

    /**
     * Sets line item extended selling price.
     *
     * @param value new extended selling price
     */
    public void setExtendedSellingPrice(CurrencyIfc value)
    {
        extendedSellingPrice = value;
    }

    /**
     * Retrieves calculated extended discounted selling price.
     *
     * @return extended discounted selling price
     */
    public CurrencyIfc getExtendedDiscountedSellingPrice()
    {
        return (extendedDiscountedSellingPrice);
    }

    /**
     * Sets line item extended discounted selling price.
     *
     * @param value new extended discounted selling price
     */
    public void setExtendedDiscountedSellingPrice(CurrencyIfc value)
    {
        extendedDiscountedSellingPrice = value;
        itemTax.setItemTaxableAmount(value);
    }


    /**
     * Retrieves line item restocking fee.
     *
     * @return restockingFee as CurrencyIfc
     */
    public CurrencyIfc getRestockingFee()
    {
        return (restockingFee);
    }

    /**
     * Sets line item restocking fee.
     *
     * @param value as CurrencyIfc
     */
    public void setRestockingFee(CurrencyIfc value)
    {
        restockingFee = value;
    }

    /**
     * Retrieves line item extended restocking fee.
     *
     * @return extendedRestockingFee as CurrencyIfc
     */
    public CurrencyIfc getExtendedRestockingFee()
    {
        return (extendedRestockingFee);
    }

    /**
     * Sets line item extended restocking fee.
     *
     * @param value as CurrencyIfc
     */
    public void setExtendedRestockingFee(CurrencyIfc value)
    {
        extendedRestockingFee = value;
    }

    /**
     * Retrieves line item total.
     *
     * @return item total
     */
    public CurrencyIfc getItemTotal()
    {
        return (itemTotal);
    }

    /**
     * Sets line item total.
     *
     * @param value new item total
     */
    public void setItemTotal(CurrencyIfc value)
    {
        itemTotal = value;
    }

    /**
     * Retrieves line item extended tax amount.
     *
     * @return item tax amount
     */
    public CurrencyIfc getItemTaxAmount()
    {
        return (getItemTax().getItemTaxAmount());
    }

    /**
     * Sets line item tax amount. Reset value, reset total.
     *
     * @param value new item tax amount
     */
    public void setItemTaxAmount(CurrencyIfc value)
    {
        getItemTax().setItemTaxAmount(value);
        recalculateItemTotal();
    }

    /**
     * Retrieves line item extended inclusive tax amount.
     *
     * @return item inclusive tax amount
     */
    public CurrencyIfc getItemInclusiveTaxAmount()
    {
        return (getItemTax().getItemInclusiveTaxAmount());
    }

    /**
     * Sets line item inclusive tax amount. Reset value, reset total.
     *
     * @param value new item inclusive tax amount
     */
    public void setItemInclusiveTaxAmount(CurrencyIfc value)
    {
        getItemTax().setItemInclusiveTaxAmount(value);
        recalculateItemTotal();
    }

    /**
     * Retrieves line item quantity. Add 1/2 to value to force proper rounding,
     * since {@link BigDecimal#longValue()} truncates decimal values.
     *
     * @return item quantity
     */
    public long getItemQuantity()
    {
        BigDecimal roundQty = itemQuantity.add(BigDecimalConstants.POINT_FIVE);
        return (roundQty.longValue());
    }

    /**
     * Retrieves line item quantity.
     *
     * @return item quantity
     */
    public BigDecimal getItemQuantityDecimal()
    {
        return itemQuantity;
    }

    /**
     * Sets line item quantity.
     *
     * @param value new quantity
     */
    public void setItemQuantity(BigDecimal value)
    {
        itemQuantity = value;
    }

    /**
     * Sets line item quantity.
     *
     * @param value new quantity
     */
    public void setItemQuantity(long value)
    {
        itemQuantity = BigDecimal.valueOf(value);
    }

    /**
     * Sets line item tax rate. Set rate if item tax object exists.
     *
     * @param value new item tax rate
     */
    public void setItemTaxRate(double value)
    {
        if (itemTax != null)
        {
            itemTax.setDefaultRate(value);
        }
    }

    /**
     * Retrieves override price reason code.
     *
     * @return override price reason code
     * @deprecated as of 13.1 Use {@link #getItemPriceOverrideReason()}
     */
    public int getItemPriceOverrideReasonCode()
    {
        return Integer.parseInt(itemPriceOverrideReason.getCode());
    }

    /**
     * Sets the override price reason code.
     *
     * @param reasonCode override price reason code
     * @deprecated as of 13.1 Use
     *             {@link #setItemPriceOverrideReason(LocalizedCodeIfc)}
     */
    public void setItemPriceOverrideReasonCode(int reasonCode)
    {
        itemPriceOverrideReason.setCode(Integer.toString(reasonCode));
    }

    /**
     * Retrieves line item discount strategy array.
     *
     * @return item discount strategy array
     */
    public ItemDiscountStrategyIfc[] getItemDiscounts()
    {
        ItemDiscountStrategyIfc[] array = new ItemDiscountStrategyIfc[itemDiscountsVector.size()];
        itemDiscountsVector.toArray(array);

        return array;
    }

    /**
     * Retrieves line item discount amount.
     *
     * @return item discount amount
     */
    public CurrencyIfc getItemDiscountAmount()
    {
        return itemDiscountAmount;
    }

    /**
     * Sets line item discount amount.
     *
     * @param value new item discount amount
     */
    public void setItemDiscountAmount(CurrencyIfc value)
    {
        itemDiscountAmount = value;
    }

    /**
     * Retrieves line item discount total.
     *
     * @return item discount total
     */
    public CurrencyIfc getItemDiscountTotal()
    {
        return itemDiscountTotal;
    }

    /**
     * Sets line item discount total.
     *
     * @param value new item discount total
     */
    public void setItemDiscountTotal(CurrencyIfc value)
    {
        itemDiscountTotal = value;
    }

    /**
     * Returns the total discount amount for discounts matching specified
     * parameters.
     *
     * @param discountScope discount scope
     * @param assignmentBasis assignment basis
     * @return CurrencyIfc
     */
    public CurrencyIfc getDiscountAmount(int discountScope, int assignmentBasis)
    {
        CurrencyIfc total = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc amount = null;

        for (ItemDiscountStrategyIfc discount : itemDiscountsVector)
        {
            if (discount != null && discount.getDiscountScope() == discountScope
                    && discount.getAssignmentBasis() == assignmentBasis)
            {
                amount = discount.getDiscountAmount();
                if (amount != null)
                {
                    total = total.add(amount);
                }
            }
        }

        return total;
    }

    /**
     * Retrieves line item transaction discount amount.
     *
     * @return item transaction discount amount
     */
    public CurrencyIfc getItemTransactionDiscountAmount()
    {
        return itemTransactionDiscountAmount;
    }

    /**
     * Sets line item transaction discount amount.
     *
     * @param value new item transaction discount amount
     */
    public void setItemTransactionDiscountAmount(CurrencyIfc value)
    {
        itemTransactionDiscountAmount = value;
        recalculateItemTotal();
    }

    /**
     * Retrieves array of transaction discount audit objects.
     *
     * @return array of transasction discount audit objects, null if not found
     */
    public ItemDiscountStrategyIfc[] getTransactionDiscounts()
    {
        ArrayList<ItemDiscountStrategyIfc> discounts = new ArrayList<ItemDiscountStrategyIfc>(itemDiscountsVector.size());

        for (ItemDiscountStrategyIfc d : itemDiscountsVector)
        {
            if (d instanceof ItemTransactionDiscountAudit)
            {
                discounts.add(d);
            }
        }

        ItemDiscountStrategyIfc[] array = new ItemDiscountStrategyIfc[discounts.size()];
        discounts.toArray(array);

        return array;
    }

    /**
     * Clears item discounts by percentage.
     */
    private void clearItemDiscountsByPercentage()
    {
        ItemDiscountStrategyIfc d = null;
        for (Iterator<ItemDiscountStrategyIfc> i = itemDiscountsVector.iterator(); i.hasNext();)
        {
            d = i.next();

            // clear only % discounts that are not part of advanced pricing
            if (d instanceof ItemDiscountByPercentageStrategy && !d.isAdvancedPricingRule()
                    && d.getAccountingMethod() == DiscountRuleConstantsIfc.ACCOUNTING_METHOD_DISCOUNT)
            {
                i.remove();
            }
        }
    }

    /**
     * Clears item discounts by percentage with a given basis and damage flag.
     *
     * @param basis The assignment basis number
     * @param damage The damage flag
     */
    public void clearItemDiscountsByPercentage(int basis, boolean damage)
    {
        ItemDiscountStrategyIfc d = null;
        for (Iterator<ItemDiscountStrategyIfc> i = itemDiscountsVector.iterator(); i.hasNext();)
        {
            d = i.next();

            // clear only % discounts that are not part of advanced pricing
            if (d instanceof ItemDiscountByPercentageStrategy && !d.isAdvancedPricingRule()
                    && d.getAccountingMethod() == DiscountRuleConstantsIfc.ACCOUNTING_METHOD_DISCOUNT
                    && d.getAssignmentBasis() == basis && d.isDamageDiscount() == damage)
            {
                i.remove();
            }

        }// end for (Iterator i = itemDiscountsVector.iterator();..)

    }

    /**
     * Clears item transaction audit discounts and item transaction discounts
     * with a given basis
     * 
     * @param basis
     */
    public void clearItemTransactionDiscounts(int basis)
    {
        ItemDiscountStrategyIfc d = null;
        for (Iterator<ItemDiscountStrategyIfc> i = itemDiscountsVector.iterator(); i.hasNext();)
        {
            d = i.next();
            if (d instanceof ItemTransactionDiscountAudit && d.getAssignmentBasis() == basis)
            {
                i.remove();
            }
        }

        TransactionDiscountStrategyIfc td = null;

        ListIterator<TransactionDiscountStrategyIfc> it = itemTransactionDiscountsVector.listIterator();
        while (it.hasNext())
        {
            td = it.next();

            if (d != null && d.getAssignmentBasis() == basis)
            {
                it.remove();
            }
        }

    }
    /**
     * Clears item discounts by percentage with a given type code, basis and
     * damage flag.
     *
     * @param typeCode int
     * @param basis The assignment basis number
     * @param damage The damage flag
     */
    public void clearItemDiscountsByPercentage(int typeCode, int basis, boolean damage)
    {
        ItemDiscountStrategyIfc d = null;
        for (Iterator<ItemDiscountStrategyIfc> i = itemDiscountsVector.iterator(); i.hasNext();)
        {
            d = i.next();

            // clear only % discounts that are not part of advanced pricing
            // and of the specified type
            if (d instanceof ItemDiscountByPercentageStrategy && !d.isAdvancedPricingRule()
                    && d.getTypeCode() == typeCode
                    && d.getAccountingMethod() == DiscountRuleConstantsIfc.ACCOUNTING_METHOD_DISCOUNT
                    && d.getAssignmentBasis() == basis && d.isDamageDiscount() == damage)
            {
                i.remove();
            }

        }// end for (Iterator i = itemDiscountsVector.iterator();..)
    }

    /**
     * Clears item markdowns by percentage.
     */
    public void clearItemMarkdownsByPercentage()
    {
        ItemDiscountStrategyIfc d = null;
        for (Iterator<ItemDiscountStrategyIfc> i = itemDiscountsVector.iterator(); i.hasNext();)
        {
            d = i.next();

            // clear only % discounts that are not part of advanced pricing
            if (d instanceof ItemDiscountByPercentageStrategy && !d.isAdvancedPricingRule()
                    && d.getAccountingMethod() == DiscountRuleConstantsIfc.ACCOUNTING_METHOD_MARKDOWN)
            {
                i.remove();
            }

        }// end for (Iterator i = itemDiscountsVector.iterator();..)
    }

    /**
     * Clears item discounts by percentage.
     *
     * @param typeCode type code
     */
    public void clearItemMarkdownsByPercentage(int typeCode)
    {
        ItemDiscountStrategyIfc d = null;
        for (Iterator<ItemDiscountStrategyIfc> i = itemDiscountsVector.iterator(); i.hasNext();)
        {
            d = i.next();

            // clear only % discounts that are not part of advanced pricing
            // and of the specified type
            if (d instanceof ItemDiscountByPercentageStrategy && !d.isAdvancedPricingRule()
                    && d.getTypeCode() == typeCode
                    && d.getAccountingMethod() == DiscountRuleConstantsIfc.ACCOUNTING_METHOD_MARKDOWN)
            {
                i.remove();
            }

        }// end for (Iterator i = itemDiscountsVector.iterator();..)
    }

    /**
     * Retrieves array of item discounts by percentage.
     *
     * @return array of disc item discount objects, null if not found
     */
    public ItemDiscountStrategyIfc[] getItemDiscountsByPercentage()
    {
        ArrayList<ItemDiscountStrategyIfc> discounts = new ArrayList<ItemDiscountStrategyIfc>(itemDiscountsVector.size());
        ItemDiscountStrategyIfc d = null;

        for (Iterator<ItemDiscountStrategyIfc> i = itemDiscountsVector.iterator(); i.hasNext();)
        {
            d = i.next();

            if (d instanceof ItemDiscountByPercentageIfc && !d.isAdvancedPricingRule())
            {
                discounts.add(d);
            }
        }

        ItemDiscountStrategyIfc[] array = new ItemDiscountStrategyIfc[discounts.size()];
        discounts.toArray(array);

        return array;
    }

    /**
     * Sets array of item discounts by percentage.
     *
     * @param value array of disc item discount objects
     */
    public void setItemDiscountsByPercentage(ItemDiscountStrategyIfc[] value)
    {
        // clear discounts by percentage
        clearItemDiscountsByPercentage();

        // if new discounts exist, add them to vector
        if (value != null)
        {
            itemDiscountsVector.addAll(Arrays.asList(value));
        }
    }

    /**
     * Clears item discounts by amount with a given type code, basis and damage
     * flag.
     *
     * @param typeCode int
     * @param basis The assignment basis number
     * @param damage The damage flag
     */
    public void clearItemDiscountsByAmount(int typeCode, int basis, boolean damage)
    {
        ItemDiscountStrategyIfc d = null;
        for (Iterator<ItemDiscountStrategyIfc> i = itemDiscountsVector.iterator(); i.hasNext();)
        {
            d = i.next();

            // clear only amount discounts that are not part of advanced pricing
            // and are not part of specified type
            if (d instanceof ItemDiscountByAmountStrategy && !d.isAdvancedPricingRule() && d.getTypeCode() == typeCode
                    && d.getAccountingMethod() == DiscountRuleConstantsIfc.ACCOUNTING_METHOD_DISCOUNT
                    && d.getAssignmentBasis() == basis && d.isDamageDiscount() == damage)
            {
                i.remove();
            }
        }
    }

    /**
     * Clears item discounts by amount.
     */
    private void clearItemDiscountsByAmount()
    {
        ItemDiscountStrategyIfc d = null;
        for (Iterator<ItemDiscountStrategyIfc> i = itemDiscountsVector.iterator(); i.hasNext();)
        {
            d = i.next();

            // clear only % discounts that are not part of advanced pricing
            if (d instanceof ItemDiscountByAmountStrategy && !d.isAdvancedPricingRule()
                    && d.getAccountingMethod() == DiscountRuleConstantsIfc.ACCOUNTING_METHOD_DISCOUNT)
            {
                i.remove();
            }
        }
    }

    /**
     * Clears item discounts by amount with a given basis and damage flag.
     *
     * @param basis The assignment basis number
     * @param damage The damage flag
     */
    public void clearItemDiscountsByAmount(int basis, boolean damage)
    {
        ItemDiscountStrategyIfc d = null;
        for (Iterator<ItemDiscountStrategyIfc> i = itemDiscountsVector.iterator(); i.hasNext();)
        {
            d = i.next();

            // clear only % discounts that are not part of advanced pricing
            if (d instanceof ItemDiscountByAmountStrategy && !d.isAdvancedPricingRule()
                    && d.getAccountingMethod() == DiscountRuleConstantsIfc.ACCOUNTING_METHOD_DISCOUNT
                    && d.getAssignmentBasis() == basis && d.isDamageDiscount() == damage)
            {
                i.remove();
            }

        }
    }

    /**
     * Clears item markdows by amount. The type entered is the only type that
     * will be cleared.
     *
     * @param typeCode type code
     */
    public void clearItemMarkdownsByAmount(int typeCode)
    {
        ItemDiscountStrategyIfc d = null;
        for (Iterator<ItemDiscountStrategyIfc> i = itemDiscountsVector.iterator(); i.hasNext();)
        {
            d = i.next();

            // clear only amount markdowns that are not part of advanced pricing
            // and are not part of specified type
            if (d instanceof ItemDiscountByAmountStrategy && !d.isAdvancedPricingRule() && d.getTypeCode() == typeCode
                    && d.getAccountingMethod() == DiscountRuleConstantsIfc.ACCOUNTING_METHOD_MARKDOWN)
            {
                i.remove();
            }
        }
    }

    /**
     * Clears item markdowns by amount.
     */
    public void clearItemMarkdownsByAmount()
    {
        ItemDiscountStrategyIfc d = null;
        for (Iterator<ItemDiscountStrategyIfc> i = itemDiscountsVector.iterator(); i.hasNext();)
        {
            d = i.next();

            // clear only % markdowns that are not part of advanced pricing
            if (d instanceof ItemDiscountByAmountStrategy && !d.isAdvancedPricingRule()
                    && d.getAccountingMethod() == DiscountRuleConstantsIfc.ACCOUNTING_METHOD_MARKDOWN)
            {
                i.remove();
            }
        }
    }

    /**
     * Retrieves the best deal discount from the collection of item discounts.
     *
     * @return ItemDiscountStrategyIfc
     */
    public ItemDiscountStrategyIfc getBestDealDiscount()
    {
        ItemDiscountStrategyIfc discount = null;
        ItemDiscountStrategyIfc bestDealDiscount = null;

        for (Iterator<ItemDiscountStrategyIfc> i = itemDiscountsVector.iterator(); i.hasNext();)
        {
            discount = i.next();

            if (discount.isAdvancedPricingRule())
            {
                bestDealDiscount = discount;
                break;
            }
        }

        return bestDealDiscount;
    }

    /**
     * Retrieves array of item discounts by amount.
     *
     * @return array of disc item discount objects
     */
    public ItemDiscountStrategyIfc[] getItemDiscountsByAmount()
    {
        ArrayList<ItemDiscountStrategyIfc> discounts = new ArrayList<ItemDiscountStrategyIfc>(itemDiscountsVector.size());
        ItemDiscountStrategyIfc d = null;

        for (Iterator<ItemDiscountStrategyIfc> i = itemDiscountsVector.iterator(); i.hasNext();)
        {
            d = i.next();

            if (d instanceof ItemDiscountByAmountIfc && !d.isAdvancedPricingRule())
            {
                discounts.add(d);
            }
        }

        ItemDiscountStrategyIfc[] array = new ItemDiscountStrategyIfc[discounts.size()];
        discounts.toArray(array);

        return array;
    }

    /**
     * Retrieves array of return item discounts.
     *
     * @return array of disc item discount objects, null if not found
     */
    public ItemDiscountStrategyIfc[] getReturnItemDiscounts()
    {
        ArrayList<ItemDiscountStrategyIfc> discounts = new ArrayList<ItemDiscountStrategyIfc>(itemDiscountsVector.size());
        ItemDiscountStrategyIfc d = null;

        for (Iterator<ItemDiscountStrategyIfc> i = itemDiscountsVector.iterator(); i.hasNext();)
        {
            d = i.next();

            if (d instanceof ReturnItemTransactionDiscountAuditIfc)
            {
                discounts.add(d);
            }
        }

        ItemDiscountStrategyIfc[] array = new ItemDiscountStrategyIfc[discounts.size()];
        discounts.toArray(array);

        return array;
    }

    /**
     * Sets array of item discounts by amount.
     *
     * @param value array of disc item discount objects
     */
    public void setItemDiscountsByAmount(ItemDiscountStrategyIfc[] value)
    {
        // clear discounts by amount
        clearItemDiscountsByAmount();

        // if new discounts exist, add them to vector
        if (value != null)
        {
            itemDiscountsVector.addAll(Arrays.asList(value));
        }
    }

    /**
     * Adds a item discount.
     *
     * @param disc ItemDiscountStrategyIfc
     */
    public void addItemDiscount(ItemDiscountStrategyIfc disc)
    {
        addOrderedItemDiscount(disc);
    }
    
    /**
     * Adds a item discount.
     *
     * @param disc ItemDiscountStrategyIfc
     */
    public void addItemTransactionDiscount(TransactionDiscountStrategyIfc disc)
    {
        itemTransactionDiscountsVector.add(disc);
    }

    /**
     * Adds an item discount in a pre-determined order.
     *
     * @param disc ItemDiscountStrategyIfc
     */
    public void addOrderedItemDiscount(ItemDiscountStrategyIfc disc)
    {
        if (itemDiscountsVector.size() == 0)
        {
            itemDiscountsVector.addElement(disc);
        }
        else
        {
            boolean inserted = false;
            for (int x = 0; x < itemDiscountsVector.size() && !inserted; x++)
            {
                ItemDiscountStrategyIfc ids = itemDiscountsVector.get(x);
                if (getDiscountOrder(disc) < getDiscountOrder(ids))
                {
                    itemDiscountsVector.insertElementAt(disc, x);
                    inserted = true;
                }
            }
            if (!inserted)
            {
                itemDiscountsVector.insertElementAt(disc, itemDiscountsVector.size());
            }
        }
    }

    /**
     * Determines the order in which a discount should be added.
     *
     * @param disc ItemDiscountStrategyIfc
     * @return the discount order
     */
    protected int getDiscountOrder(ItemDiscountStrategyIfc discountStrategy)
    {
        int discountOrder = -1;

        if (discountStrategy.getDiscountScope() != ItemDiscountStrategyIfc.DISCOUNT_SCOPE_TRANSACTION)
        { // Item level discounts
            if (discountStrategy.getDiscountMethod() == ItemDiscountStrategyIfc.DISCOUNT_METHOD_PERCENTAGE)
            {
                // Item Level discounts by percent
                discountOrder = 10;
            }
            else
            {
                // Item Level discounts by amount
                discountOrder = 20;
            }
        }
        else
        // Transaction level discounts
        {
            if (discountStrategy.getDiscountMethod() == ItemDiscountStrategyIfc.DISCOUNT_METHOD_PERCENTAGE)
            {
                // Transaction Level Discounts by percent
                discountOrder = 30;
            }
            else
            {
                // Transaction Discounts by amount
                discountOrder = 40;
            }
        }

        return discountOrder;
    }

    /**
     * Adds an item transaction discount.
     *
     * @param disc ItemTransactionDiscountAuditIfc
     */
    public void addItemTransactionDiscount(ItemTransactionDiscountAuditIfc disc)
    {
        CurrencyIfc discAmt = disc.getDiscountAmount();
        itemTransactionDiscountAmount = itemTransactionDiscountAmount.add(discAmt);
        itemDiscountTotal = itemDiscountTotal.add(discAmt);

        addItemDiscount(disc);
        
        recalculateItemTotal();
    }

    /**
     * Adds transaction discount audit record.
     *
     * @param value the amount of the transaction discount
     * @param td strategy to collect attributes from
     */
    public void addTransactionDiscount(CurrencyIfc value, TransactionDiscountStrategyIfc td)
    {       
        // instantiate new discount
        ItemTransactionDiscountAuditIfc itda = DomainGateway.getFactory().getItemTransactionDiscountAuditInstance();
        itda.initialize(value, td.getReason(), td.getAssignmentBasis());
        itda.setOriginalDiscountMethod(td.getDiscountMethod());
        itda.setDiscountRate(td.getDiscountRate());
        itda.setReferenceID(td.getReferenceID());
        itda.setReferenceIDCode(td.getReferenceIDCode());
        itda.setDiscountEmployee(td.getDiscountEmployeeID());

        // added ruleId to allow coupon rule to survive suspend so POS won't
        // reject it later. CR30190 14FEB08 CMG
        // See {@link ItemContainerProxyIfc#areAllStoreCouponsApplied()}.
        itda.setRuleID(td.getRuleID());
        itda.setAccountingMethod(td.getAccountingMethod());
        itda.setLocalizedNames(td.getLocalizedNames());
        itda.setPromotionId(td.getPromotionId());

        // add new discount by percentage
        addItemDiscount(itda);
        addItemTransactionDiscount(td);
        
        itemTransactionDiscountAmount = itemTransactionDiscountAmount.add(value);
        itemDiscountTotal = itemDiscountTotal.add(value);
        
        recalculateItemTotal();
    }

    /**
     * Clears transaction discounts.
     */
    public void clearTransactionDiscounts()
    {
        ItemDiscountStrategyIfc d = null;

        for (Iterator<ItemDiscountStrategyIfc> i = itemDiscountsVector.iterator(); i.hasNext();)
        {
            d = i.next();

            if (d instanceof ItemTransactionDiscountAudit)
            {
                i.remove();
            }

        }
        
        itemTransactionDiscountsVector.clear();
        
    }

    /**
     * Clears transaction discounts.
     *
     * @param dm discount method
     */
    public void clearTransactionDiscounts(int dm)
    {
        Vector<ItemDiscountStrategyIfc> newVector = new Vector<ItemDiscountStrategyIfc>();
        ItemDiscountStrategyIfc d = null;

        // begin loop through discounts
        int numDiscounts = itemDiscountsVector.size();
        for (int i = 0; i < numDiscounts; i++)
        {
            d = itemDiscountsVector.get(i);

            // if amount discount not found, add it to new vector
            if (!(d instanceof ItemTransactionDiscountAudit) || d.getDiscountMethod() != dm)
            {
                newVector.addElement(d);
            }
        }

        // set item discounts vector
        itemDiscountsVector = newVector;
    }
    
    /**
     * Clears the transaction discounts applied by the system. It clears all
     * transaction discounts which are not applied manually. For clarifiaction,
     * employee, store coupon, customer specific and other pos manual disocunts
     * are applied by cashier interaction.
     */
    public void clearTransactionScopeSystemDiscounts()
    {
        TransactionDiscountStrategyIfc d = null;

        ListIterator<TransactionDiscountStrategyIfc> it = itemTransactionDiscountsVector.listIterator();
        while (it.hasNext())
        {
            d = it.next();
            
            if (d.getAssignmentBasis() == TransactionDiscountStrategyIfc.ASSIGNMENT_ITEM)
            {
                it.remove();
            }
        }
    }

    /**
     * Clears ItemDiscountStrategyIfcs with corresponding discountRuleID.
     *
     * @param discountRuleID discount rule ID
     */
    public void clearItemDiscounts(String discountRuleID)
    {
        if (discountRuleID != null)
        {
            ItemDiscountStrategyIfc discount = null;
            for (Iterator<ItemDiscountStrategyIfc> j = itemDiscountsVector.iterator(); j.hasNext();)
            {
                discount = j.next();
                if (discount.getRuleID().equals(discountRuleID))
                {
                    j.remove();
                }
            }
        }
    }

    /**
     * Sets discount strategy array.
     *
     * @param value discount strategy array
     */
    public void setItemDiscounts(ItemDiscountStrategyIfc[] value)
    {
        // clear discounts
        clearItemDiscounts();
        // add discounts to vector
        if (value != null)
        {
            itemDiscountsVector.addAll(Arrays.asList(value));
        }
    }

    /**
     * Clears item discounts.
     */
    public void clearItemDiscounts()
    {
        itemDiscountsVector.clear();
    }

    /**
     * Retrieves line item tax object.
     *
     * @return item tax object
     */
    public ItemTaxIfc getItemTax()
    {
        return itemTax;
    }

    /**
     * Sets tax object.
     *
     * @param value item tax object
     */
    public void setItemTax(ItemTaxIfc value)
    {
        itemTax = value;
        itemTax.setItemPrice(this);
    }

    /**
     * Determine if two given object references refer to equal Objects. This
     * method also provides for the possibility of a null reference.
     *
     * @param accessor identifier of accessor
     * @param obj1 first Object to compare
     * @param obj2 second Object to compare
     * @return boolean true if the references refer to equal Objects or are both
     *         null
     */
    public static boolean objectEquals(String accessor, Object obj1, Object obj2)
    {
        boolean isEqual = Util.isObjectEqual(obj1, obj2);

        if (!isEqual)
        {
            logger.info("Accessor " + accessor + "'s values are not equal");
        }
        return isEqual;
    }

    /**
     * Retrieves indicator item is eligible for discounting.
     *
     * @return indicator item is eligible for discounting
     */
    public boolean getDiscountEligible()
    {
        return (discountEligible);
    }

    /**
     * Retrieves indicator item is eligible for discounting.
     *
     * @return indicator item is eligible for discounting
     */
    public boolean isDiscountEligible()
    {
        return (getDiscountEligible());
    }

    /**
     * Sets indicator item is eligible for discounting.
     *
     * @param value indicator item is eligible for discounting
     */
    public void setDiscountEligible(boolean value)
    {
        discountEligible = value;
    }

    /**
     * Retrieves indicator item is eligible for employee discounting.
     *
     * @return indicator item is eligible for employee discounting
     */
    public boolean getEmployeeDiscountEligible()
    {
        return (employeeDiscountEligible);
    }

    /**
     * Retrieves indicator item is eligible for employee discounting.
     *
     * @return indicator item is eligible for employee discounting
     */
    public boolean isEmployeeDiscountEligible()
    {
        return (getEmployeeDiscountEligible());
    }

    /**
     * Sets indicator item is eligible for employee discounting.
     *
     * @param value indicator item is eligible for employee discounting
     */
    public void setEmployeeDiscountEligible(boolean value)
    {
        employeeDiscountEligible = value;
    }

    /**
     * Retrieves indicator item is eligible for damage discounting.
     *
     * @return indicator item is eligible for damage discounting
     */
    public boolean getDamageDiscountEligible()
    {
        return (damageDiscountEligible);
    }

    /**
     * Retrieves indicator item is eligible for damage discounting.
     *
     * @return indicator item is eligible for damage discounting
     */
    public boolean isDamageDiscountEligible()
    {
        return (getDamageDiscountEligible());
    }

    /**
     * Sets indicator item is eligible for damage discounting.
     *
     * @param value indicator item is eligible for damage discounting
     */
    public void setDamageDiscountEligible(boolean value)
    {
        damageDiscountEligible = value;
    }

    /**
     * Sets price override authorization.
     *
     * @param value price override authorization
     */
    public void setPriceOverrideAuthorization(SecurityOverrideIfc value)
    {
        priceOverrideAuthorization = value;
    }

    /**
     * Returns price override authorization.
     *
     * @return price override authorization
     */
    public SecurityOverrideIfc getPriceOverrideAuthorization()
    {
        return (priceOverrideAuthorization);
    }

    /**
     * Returns true if price is overridden, false otherwise.
     *
     * @return true if price is overridden, false otherwise.
     */
    public boolean isPriceOverride()
    {
        boolean isOverride = false;
        if (!getItemPriceOverrideReason().getCode().equals(CodeConstantsIfc.CODE_UNDEFINED))
        {
            isOverride = true;
        }
        return (isOverride);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.ItemPriceIfc#getPriceOverrideMarker()
     */
    public String getPriceOverrideMarker()
    {
        return isPriceOverride() ? DomainUtil.retrieveOverrideMarker("receipt").trim() : "";
    }

    /**
     * Calculates the total dollar value of all the item discounts by amount
     *
     * @return The total dollar value of all the item discounts by amount
     */
    public CurrencyIfc getTotalItemDiscountsByAmount()
    {
        CurrencyIfc returnVal = DomainGateway.getBaseCurrencyInstance();

        // Get list of ItemDiscountStrategyByAmounts and add them up.
        ItemDiscountStrategyIfc discounts[] = getItemDiscountsByAmount();
        if (discounts != null)
        {
            for (int i = 0; i < discounts.length; i++)
            {
                returnVal = returnVal.add(discounts[i].getDiscountAmount());
            }
        }

        return returnVal;
    }

    /**
     * Calculates the total dollar value of all the item discounts by percentage
     *
     * @return The total dollar value of all the item discounts by percentage
     */
    public CurrencyIfc getTotalItemDiscountsByPercentage()
    {
        CurrencyIfc returnVal = DomainGateway.getBaseCurrencyInstance();

        // Get list of ItemDiscountStrategyByPercentages and add them up.
        ItemDiscountStrategyIfc discounts[] = getItemDiscountsByPercentage();
        if (discounts != null)
        {
            for (int i = 0; i < discounts.length; i++)
            {
                returnVal = returnVal.add(discounts[i].getDiscountAmount());
            }
        }

        return returnVal;
    }

    /**
     * Calculates the total dollar value of all the transaction discounts by
     * amount
     *
     * @return The total dollar value of all the transaction discounts by amount
     */
    public CurrencyIfc getTotalTransactionDiscountsByAmount()
    {
        CurrencyIfc returnVal = DomainGateway.getBaseCurrencyInstance();

        // Get list of ItemDiscountStrategyByAmounts and add them up.
        ItemDiscountStrategyIfc discounts[] = getTransactionDiscounts();
        if (discounts != null)
        {
            for (int i = 0; i < discounts.length; i++)
            {
                if (discounts[i].getDiscountMethod() != DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE)
                {
                    returnVal = returnVal.add(discounts[i].getDiscountAmount());
                }
            }
        }

        return returnVal;
    }

    /**
     * Calculates the total dollar value of all the transaction discounts by
     * percentage
     *
     * @return The total dollar value of all the transaction discounts by
     *         percentage
     */
    public CurrencyIfc getTotalTransactionDiscountsByPercentage()
    {
        CurrencyIfc returnVal = DomainGateway.getBaseCurrencyInstance();

        // Get list of ItemDiscountStrategyByAmounts and add them up.
        ItemDiscountStrategyIfc discounts[] = getTransactionDiscounts();
        if (discounts != null)
        {
            for (int i = 0; i < discounts.length; i++)
            {
                if (discounts[i].getDiscountMethod() == DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE)
                {
                    returnVal = returnVal.add(discounts[i].getDiscountAmount());
                }
            }
        }

        return returnVal;
    }

    /**
     * Determine if two objects have equal attribute values and that the
     * associated objects are equivalent.
     *
     * @param obj object to compare with
     * @return boolean true if the objects pass the test
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = false;

        if (obj instanceof ItemPrice)
        {
            ItemPrice price = (ItemPrice)obj;
            try
            {
                if (objectEquals("getSellingPrice",
                                 getSellingPrice(), price.getSellingPrice())
                    && objectEquals("getSellingPriceBeforeOverride",
                                 getPermanentSellingPrice(),
                                 price.getPermanentSellingPrice())
                    && objectEquals("getExtendedSellingPrice",
                                    getExtendedSellingPrice(),
                                    price.getExtendedSellingPrice())
                    && objectEquals("getExtendedDiscountedSellingPrice",
                                    getExtendedDiscountedSellingPrice(),
                                    price.getExtendedDiscountedSellingPrice())
                    && objectEquals("getItemTotal",
                                    getItemTotal(), price.getItemTotal())
                    && objectEquals("getItemTaxAmount",
                                    getItemTaxAmount(), price.getItemTaxAmount())
                    && objectEquals("getItemInclusiveTaxAmount",
                                    getItemInclusiveTaxAmount(), price.getItemInclusiveTaxAmount())
                    && objectEquals("getItemQuantity",
                                    getItemQuantityDecimal(),
                                    price.getItemQuantityDecimal())
                    && objectEquals("getItemPriceOverrideReasonCode",
                                     getItemPriceOverrideReason().getCode(),
                                     price.getItemPriceOverrideReason().getCode())
                     && objectEquals("getItemPriceOverrideReasonCode",
                                     getItemPriceOverrideReason().getText(),
                                     price.getItemPriceOverrideReason().getText())
                    && (getDiscountEligible() == price.getDiscountEligible())
                    && (getEmployeeDiscountEligible() == price.getEmployeeDiscountEligible())
                    && (getDamageDiscountEligible() == price.getDamageDiscountEligible())
                    && objectEquals("getItemDiscountAmount",
                                    getItemDiscountAmount(),
                                    price.getItemDiscountAmount())
                    && objectEquals("getItemDiscountTotal",
                                    getItemDiscountTotal(),
                                    price.getItemDiscountTotal())
                    && objectEquals("getItemTransactionDiscountAmount",
                                    getItemTransactionDiscountAmount(),
                                    price.getItemTransactionDiscountAmount())
                    && objectEquals("getItemDiscount",
                                    getItemDiscounts(),
                                    price.getItemDiscounts())
                    && objectEquals("getItemTax",
                                    getItemTax(),
                                    price.getItemTax())
                    && objectEquals("getRestockingFee",
                                    getRestockingFee(),
                                    price.getRestockingFee())
                    && objectEquals("getExtendedRestockingFee",
                                    getExtendedRestockingFee(),
                                    price.getExtendedRestockingFee())
                    && objectEquals("getPriceOverrideAuthorization",
                                    getPriceOverrideAuthorization(),
                                    price.getPriceOverrideAuthorization()))

                {
                    isEqual = true;
                }
                else
                {
                    isEqual = false;
                }
            }
            catch (Exception e)
            {
                logger.error("ItemPrice.equals, Exception: " + e + Util.throwableToString(e));
                isEqual = false;
            }
        }
        return isEqual;
    }

    /**
     * Method to default display string function.
     *
     * @return String representation of object
     */
    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("sellingPrice", getSellingPrice());
        builder.append("quantity", getItemQuantityDecimal());
        builder.append("extendedSellingPrice", getExtendedSellingPrice());
        builder.append("extendedDiscountedSellingPrice", getExtendedDiscountedSellingPrice());
        builder.append("restockingFee", getRestockingFee());
        builder.append("extendedRestockingFee", getExtendedRestockingFee());
        builder.append("itemDiscountAmount", getItemDiscountAmount());
        builder.append("itemTransactionDiscountAmount", getItemTransactionDiscountAmount());
        builder.append("itemDiscountTotal", getItemDiscountTotal());
        builder.append("itemTotal", getItemTotal());
        builder.append("itemTax", getItemTax());
        builder.append("itemDiscounts", getItemDiscounts());
        builder.append("priceOverrideAuthorization", getPriceOverrideAuthorization());
        builder.append("permanentSellingPrice", permanentSellingPrice);
        builder.append("promotionLineItems", getPromotionLineItems());

        return builder.toString();
    }

    /**
     * Get the active tax rules for this item
     *
     * @return array of active tax rules
     */
    public RunTimeTaxRuleIfc[] getActiveTaxRules()
    {
        return itemTax.getActiveTaxRules();
    }

    /**
     * Get the default tax rules when none are available in the DB
     *
     * @return list of all default tax rules
     */
    public TaxRuleIfc[] getDefaultTaxRules()
    {
        return itemTax.getDefaultTaxRules();
    }

    /**
     * Get the identifier the uniquely identifies this item
     *
     * @return unique identifier for this tax line item
     */
    public int getLineItemTaxIdentifier()
    {
        return itemTax.getLineItemTaxIdentifier();
    }

    /**
     * Gets all the promotionLineItems for the Sale Return Line Item
     *
     * @return Returns the promotionLineItems.
     */
    public PromotionLineItemIfc[] getPromotionLineItems()
    {
        PromotionLineItemIfc[] array = null;
        if (promotionLineItems != null && promotionLineItems.size() > 0)
        {
            array = new PromotionLineItemIfc[promotionLineItems.size()];
            promotionLineItems.toArray(array);
        }
        return array;
    }

    /**
     * Sets all the promotionLineItems for the Sale Return Line Item
     *
     * @param promotionLineItems
     */
    public void setPromotionLineItems(PromotionLineItemIfc[] promotionLineItems)
    {
        if (this.promotionLineItems == null)
        {
            this.promotionLineItems = new Vector<PromotionLineItemIfc>();
        }
        else
        {
            this.promotionLineItems.clear();
        }

        if (promotionLineItems != null)
        {
            this.promotionLineItems.addAll(Arrays.asList(promotionLineItems));
        }
    }

    /**
     * Adds a Promotion Line Item to the Sale Return Line Item
     *
     * @param promotionLineItem
     */
    public void addPromotionLineItem(PromotionLineItemIfc promotionLineItem)
    {
        if (promotionLineItems == null)
        {
            promotionLineItems = new Vector<PromotionLineItemIfc>();
        }
        promotionLineItems.add(promotionLineItem);
    }

    /**
     * Removes a Promotion Line Item to the Sale Return Line Item
     *
     * @param promotionLineItem
     */
    public void removePromotionLineItem(PromotionLineItemIfc promotionLineItem)
    {
        if (promotionLineItems != null)
        {
            promotionLineItems.remove(promotionLineItem);
        }
    }

    /**
     * @return Returns the permanentSellingPrice.
     */
    public CurrencyIfc getPermanentSellingPrice()
    {
        return permanentSellingPrice;
    }

    /**
     * @param sellingPriceBeforeOverride The permanentSellingPrice to set.
     */
    public void setPermanentSellingPrice(CurrencyIfc permanentSellingPrice)
    {
        this.permanentSellingPrice = permanentSellingPrice;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.ItemPriceIfc#getAppliedPromotion()
     */
    public PromotionLineItemIfc getAppliedPromotion()
    {
        PromotionLineItemIfc appliedPromotion = null;
        PromotionLineItemIfc[] promotions = getPromotionLineItems();
        if (promotions != null && promotions.length > 0)
        {
            appliedPromotion = promotions[0];
        }
        return appliedPromotion;
    }


    /**
     * Converts and sets the specified non-null {@link PriceChangeIfc} to a
     * {@link PromotionLineItemIfc}. Any promotion, whether created from ORBO
     * or sent from an external system (e.g. ORPM) will be converted.
     *
     * @see ItemPriceIfc#setAppliedPromotion(PriceChangeIfc)
     * @see #setPromotionLineItems(PromotionLineItemIfc[])
     */
    public void setAppliedPromotion(PriceChangeIfc appliedPromotion)
    {
        if (appliedPromotion != null)
        {
            PromotionLineItemIfc promotionLineItem = DomainGateway.getFactory().getPromotionLineItemInstance();
            CurrencyIfc discountAmount = getPermanentSellingPrice().subtract(getSellingPrice());
            promotionLineItem.setPromotionType(PromotionLineItem.PROMOTION_TYPE_PRICE_CHANGE);
            promotionLineItem.setDiscountAmount(discountAmount);
            // promo ids from ORBO will be zero. ORPM promos will be non-zero.
            promotionLineItem.setPromotionId(appliedPromotion.getPromotionId());
            promotionLineItem.setPromotionComponentId(appliedPromotion.getPromotionComponentId());
            promotionLineItem.setPromotionComponentDetailId(appliedPromotion.getPromotionComponentDetailId());
            promotionLineItem.setPricingGroupID(appliedPromotion.getPricingGroupID());
            promotionLineItem.setPromotionName(appliedPromotion.getPromotionName());
            setPromotionLineItems(new PromotionLineItemIfc[] { promotionLineItem });
        }
    }

    /**
     * @return the bestSystemTransactionDiscount
     */
    public TransactionDiscountStrategyIfc getBestSystemTransactionDiscount()
    {
        return bestSystemTransactionDiscount;
    }

    /**
     * @param bestSystemTransactionDiscount the bestSystemTransactionDiscount to set
     */
    public void setBestSystemTransactionDiscount(TransactionDiscountStrategyIfc bestSystemTransactionDiscount)
    {
        this.bestSystemTransactionDiscount = bestSystemTransactionDiscount;
    }

    /**
     * Clears all discounts and markdown with a given basis.
     *
     * @param basis The assignment basis number
     */
    public void clearItemDiscountByBasis(int basis)
    {
        for (Iterator<ItemDiscountStrategyIfc> i = itemDiscountsVector.iterator(); i.hasNext();)
        {
            Object obj = i.next();

            if ( obj instanceof DiscountRuleIfc &&
                ((DiscountRuleIfc)obj).getAssignmentBasis() == basis)
            {
                i.remove();
            }
        }
    }

    /**
     * Gets the Line Item price and divides with the item quantity and returns the single item price code
     */
    public String getLineItemPriceCode()
    {
        CurrencyIfc sellPrice = getExtendedDiscountedSellingPrice();
        PriceCodeConverter priceCodeConverter = PriceCodeConverter.getInstance();

        if (itemQuantity.intValue() > 1)
        {
            sellPrice = getExtendedDiscountedSellingPrice().divide(itemQuantity);
        }
        return priceCodeConverter.convertPriceToPriceCode(sellPrice);
    }


}
