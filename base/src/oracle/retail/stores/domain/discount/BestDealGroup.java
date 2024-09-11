/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/BestDealGroup.java /main/24 2012/12/12 11:01:49 cgreene Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *   crain   10/06/14 - For rule BuyNorMoreOfXforZ$Each the discount is applied to an item 
 *                      only when the actual price is greater than the fixed price.
 *   cgreene 12/11/12 - allow sale renderer to show item's promotion name
 *   blarsen 12/12/11 - XbranchMerge vikashku_bug12901304 from
 *                      rgbustores_13.3.3_generic_branch
 *   vikashk 09/15/11 - Modified BestDealGroup.applyAdvancedPricingDiscounts()
 *                      to use absolute value of the selling price while
 *                      calculating the discount
 *   dwfung  07/09/10 - fixed advanced group pricing for returns
 *   cgreene 05/26/10 - convert to oracle packaging
 *   aariyer 01/22/10 - For the discounts
 *   abondal 01/03/10 - update header date
 *   cgreene 12/07/09 - minor update
 *   cgreene 12/07/09 - fix proration to include more sigfigs in its ratio
 *   cgreene 06/18/09 - added index for reason code search performance
 *   mchella 06/11/09 - Fixed BuyNorMoreOfXforZ$Each rule's fixedprice
 *                      calculation
 *   cgreene 04/14/09 - convert pricingGroupID to integer instead of string
 *   npoola  11/30/08 - CSP POS and BO changes
 *   lslepet 11/13/08 - fix failure in BestDealGroupTest
 *   lslepet 11/05/08 - add rules of type BuyNorMoreOfXforZ%off and
 *                      BuyNorMoreOfXforZ$each
 *   acadar  11/03/08 - localization of reason codes for discounts and merging
 *                      to tip
 *   acadar  10/30/08 - use localized reason codes for item and transaction
 *                      discounts
 *   cgreene 09/19/08 - updated with changes per FindBugs findings
 *   cgreene 09/11/08 - update header
 *
 * ===========================================================================
 *   $Log:
     $Log:
      9    360Commerce 1.8         11/15/2007 10:48:46 AM Christian Greene
           Belize merge - add support for Any/All sources/targets
      8    360Commerce 1.7         8/22/2007 5:59:40 PM   Michael P. Barnett In
            applyAdvancedPricingDiscounts(), only apply discounts to source
           items that are discount eligible.
      7    360Commerce 1.6         5/15/2007 5:53:46 PM   Maisa De Camargo
           Added PromotionId, PromotionComponentId and
           PromotionComponentDetailId
      6    360Commerce 1.5         4/25/2007 10:01:01 AM  Anda D. Cadar   I18N
           merge
      5    360Commerce 1.4         1/22/2006 11:41:27 AM  Ron W. Haight
           Removed references to com.ibm.math.BigDecimal
      4    360Commerce 1.3         12/13/2005 4:43:46 PM  Barry A. Pape
           Base-lining of 7.1_LA
      3    360Commerce 1.2         3/31/2005 4:27:16 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:19:49 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:09:35 PM  Robert Pearse
     $
     Revision 1.6  2004/09/23 00:30:53  kmcbride
     @scr 7211: Inserting serialVersionUIDs in these Serializable classes

     Revision 1.5  2004/02/25 21:18:59  mweis
     @scr 0 protect against nulls

     Revision 1.4  2004/02/17 16:18:50  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:28  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:27  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:34:52   CSchellenger
 * Initial revision.
 *
 *    Rev 1.10   Jul 11 2003 09:37:00   jgs
 * Added code back in to set the availability of sources that have been consumed by a best deal.
 * Resolution for 3146: A Discount Rule with specified target item/quantity is applying the discount to more than one target item.
 *
 *    Rev 1.9   Jul 09 2003 06:35:28   jgs
 * Added check on highest priced item discount to prevent the discount from being applied to all items that have the highest price.
 * Resolution for 3054: When using the Buy n of X, get the highest price X at Z% off rule where the Deal Distribution Indicator = Target only, the discount is being applied to both sources and target items.
 *
 *    Rev 1.8   Jun 16 2003 12:05:08   bwf
 * Added code to handle new advanced pricing functionality.
 * Resolution for 2765: Advanced Pricing Rule - Discount on Highest Priced Item
 *
 *    Rev 1.7   Mar 20 2003 09:26:28   jgs
 * Changes due to code reveiw.
 * Resolution for 103: New Advanced Pricing Features
 *
 *    Rev 1.6   Jan 22 2003 15:05:02   mpb
 * SCR #1626
 * In applyAdvancedPricingDiscounts(), set the accounting method in the discount strategy.
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.5   Jan 20 2003 11:50:14   jgs
 * Added allow repeating sources, deal distribution, and percent off lowest priced Item to Advanced Pricing Rule processing.
 * Resolution for 103: New Advanced Pricing Features
 *
 *    Rev 1.4   Dec 16 2002 14:21:22   pjf
 * Deprecated replaceLineItem() in BestDealGroup, clone pricing rules in PLUItem.
 * Resolution for 101: Merge KB discount fixes.
 *
 *    Rev 1.3   Dec 13 2002 10:39:18   pjf
 * Don't clone best deal groups, recalculate best deal instead.
 * Resolution for 101: Merge KB discount fixes.
 *
 *    Rev 1.1   Nov 20 2002 17:53:20   pjf
 * Move control of sourceAvailable flag to line item.
 * Resolution for 101: Merge KB discount fixes.
 *
 *    Rev 1.0   Sep 05 2002 11:11:24   msg
 * Initial revision.
 *
 *    Rev 1.4   Aug 24 2002 13:23:54   vpn-mpm
 * Added support for discount rule post-process-type code
 *
 *    Rev 1.3   07 Jun 2002 11:14:52   jbp
 * changes to handle sources are targets.
 * Resolution for POS SCR-1719: Advanced Pricing - discount is removed when going into Item if source/target are same
 *
 *    Rev 1.2   04 Jun 2002 15:14:44   jbp
 * added replaceLineItem()
 * Resolution for POS SCR-1647: Advanced Pricing - Assigning a gift receipt removes the deal
 *
 *    Rev 1.1   Mar 18 2002 22:57:30   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:17:48   msg
 * Initial revision.
 *
 *    Rev 1.2   Feb 23 2002 10:31:22   mpm
 * Modified Util.BIG_DECIMAL to Util.I_BIG_DECIMAL, Util.ROUND_HALF to Util.I_ROUND_HALF.
 * Resolution for Domain SCR-35: Accept Foundation BigDecimal backward-compatibility changes
 *
 *    Rev 1.1   Feb 05 2002 16:34:12   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 *
 *    Rev 1.0   Sep 20 2001 16:12:16   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:36:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.discount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.ItemContainerProxy;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;

import org.apache.log4j.Logger;

/**
 * The BestDealGroup is used for storing a best deal combination, and is
 * responsible for calculating the discount corresponding to this combination.
 * <P>
 * When an AdvancedPricingRule is satisfied, a new instance of BestDealGroup
 * gets created and saved in the corresponding SaleReturnTransaction.
 * <P>
 * The best deal winner will be chosen from the collection of best deal groups
 * stored in the SaleReturnTransaction.
 *
 * @version $Revision: /main/24 $
 */
public class BestDealGroup implements BestDealGroupIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 1777116601978020148L;

    /** The logger to which log messages will be sent. */
    private static Logger logger = Logger.getLogger(BestDealGroup.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/24 $";

    /**
     * flag for indicating the winner for best deal
     */
    protected boolean bestDeal = false;

    /**
     * the AdvancedPricingRule associated with this instance
     */
    protected AdvancedPricingRuleIfc discountRule = null;

    /**
     * the total discount calculated for this possible deal
     */
    protected CurrencyIfc totalDiscount = null;

    /**
     * Total Selling price before applying discounts
     */
    protected CurrencyIfc totalSellingPrice = null;

    /**
     * Applied ItemDiscountStrategy
     */
    protected ItemDiscountStrategyIfc strategy = null;

    /**
     * contains the objects used to satisfy the source criteria for this rule
     */
    protected ArrayList<DiscountSourceIfc> sources = null;

    /**
     * contains the objects used to satisfy the target criteria for this rule
     */
    protected ArrayList<DiscountTargetIfc> targets = null;

    /**
     * contains the objects to which discounts have been applied; due to the
     * effects of the deal distribution flag in the advanced pricing rule, it is
     * possible that the targets used to calculate the total amount of the
     * discount may be different than the targets to which the discouts are
     * applied.
     */
    protected ArrayList<DiscountTargetIfc> discountAppliedTargets = null;

    /**
     * Default Constructor.
     */
    public BestDealGroup()
    {
        totalDiscount = DomainGateway.getBaseCurrencyInstance();
        totalSellingPrice = DomainGateway.getBaseCurrencyInstance();
    }

    /**
     * Constructs an instance of a BestDealGroup for this specific rule.
     * 
     * @param rule the satisfied AdvancedPricingRule
     */
    public BestDealGroup(AdvancedPricingRuleIfc discountRule)
    {
        this();
        this.discountRule = discountRule;
    }

    /**
     * Sets the flag for the best deal winner.
     * 
     * @param value
     */
    public void setBestDeal(boolean value)
    {
        bestDeal = value;
    }

    /**
     * Returns the best deal winner flag.
     * 
     * @return boolean
     */
    public boolean isBestDeal()
    {
        return bestDeal;
    }

    /**
     * Returns the best deal winner flag.
     * 
     * @return boolean
     */
    public boolean getBestDeal()
    {
        return bestDeal;
    }

    /**
     * Sets the discount rule.
     * 
     * @param discountRule
     */
    public void setDiscountRule(AdvancedPricingRuleIfc discountRule)
    {
        this.discountRule = discountRule;

    }

    /**
     * Gets the discount rule.
     * 
     * @return AdvancedPricingRuleIfc
     */
    public AdvancedPricingRuleIfc getDiscountRule()
    {
        return discountRule;
    }

    /**
     * Gets the strategy.
     * 
     * @return ItemDiscountStrategyIfc
     */
    public ItemDiscountStrategyIfc getStrategy()
    {
        return strategy;
    }

    /**
     * Sets the strategy.
     * 
     * @param ItemDiscountStrategyIfc
     */
    public void setStrategy(ItemDiscountStrategyIfc strategy)
    {
        this.strategy = strategy;
    }

    /**
     * Gets the total discount for this combination.
     * 
     * @return CurrencyIfc
     */
    public CurrencyIfc getTotalDiscount()
    {
        return totalDiscount;
    }

    /**
     * Sets the total discount for this combination.
     * 
     * @param CurrencyIfc
     */
    public void setTotalDiscount(CurrencyIfc totalDiscount)
    {
        this.totalDiscount = totalDiscount;
    }

    /**
     * Sets the applied sources.
     * <P>
     */
    public void setSources(ArrayList<DiscountSourceIfc> srcs)
    {
        sources = srcs;
    }

    /**
     * Sets the applied targets.
     * <P>
     */
    public void setTargets(ArrayList<DiscountTargetIfc> tgts)
    {
        targets = tgts;
    }

    /**
     * Sets the applied discount targets.
     * <P>
     */
    public void setDiscountAppliedTargets(ArrayList<DiscountTargetIfc> tgts)
    {
        discountAppliedTargets = tgts;
    }

    /**
     * Gets the applied Sources.
     * 
     * @return DiscountTargetIfc[]
     */
    public ArrayList<DiscountSourceIfc> getSources()
    {
        return sources;
    }

    /**
     * Gets the applied targets.
     * 
     * @return DiscountTargetIfc[]
     */
    public ArrayList<DiscountTargetIfc> getTargets()
    {
        return targets;
    }

    /**
     * Gets the applied targets.
     * 
     * @return DiscountTargetIfc[]
     */
    public ArrayList<DiscountTargetIfc> getDiscountAppliedTargets()
    {
        return discountAppliedTargets;
    }

    /**
     * Gets the discount rule ID.
     * 
     * @return String
     */
    public String getRuleID()
    {
        return discountRule.getRuleID();
    }

    /**
     * Sets the total selling price
     * 
     * @param CurrencyIfc
     */
    public void setTotalSellingPrice(CurrencyIfc price)
    {
        totalSellingPrice = price;
    }

    /**
     * Returns the total selling price.
     * 
     * @param CurrencyIfc
     */
    public CurrencyIfc getTotalSellingPrice()
    {
        return totalSellingPrice;
    }

    /**
     * Clears the totals.
     */
    protected void clearTotals()
    {
        totalDiscount.setZero();
        totalSellingPrice.setZero();
    }

    /**
     * Calculates the total discount for this possible combination. Preferred
     * Customer discount is considered an alternative to best deal.
     */
    public CurrencyIfc calculateTotalDiscount()
    {
        clearTotals();

        totalSellingPrice = ItemContainerProxy.calculateTotalSellingPrice(discountRule.getTargets());

        switch(discountRule.getDiscountMethod())
        {
            case DISCOUNT_METHOD_PERCENTAGE :
            {
                if (discountRule.getCalcDiscOnItemType() == DiscountRuleConstantsIfc.DISCOUNT_TYPE_ON_ITEM_LOWEST)
                {
                    totalDiscount = discountRule.getDiscountAmountOnLowestPricedItem();
                }
                else if(discountRule.getCalcDiscOnItemType() == DiscountRuleConstantsIfc.DISCOUNT_TYPE_ON_ITEM_HIGHEST)
                {
                    totalDiscount = discountRule.getDiscountAmountOnHighestPricedItem();
                }
                else
                {
                    totalDiscount = totalSellingPrice.multiply(discountRule.getDiscountRate());
                }
                break;
            }
            case DISCOUNT_METHOD_AMOUNT :
            {
                totalDiscount = discountRule.getDiscountAmount();
                break;
            }
            case DISCOUNT_METHOD_FIXED_PRICE :
            {
                CurrencyIfc totalFixedPrice = discountRule.getFixedPrice();
                if (discountRule.getDescription().equals(DiscountRuleConstantsIfc.DISCOUNT_DESCRIPTION_BuyNorMoreOfXforZ$Each))
                {
                    List<DiscountTargetIfc> targets = discountRule.getTargets();  
                    Iterator<DiscountTargetIfc> targetsIterator = targets.iterator();
                    totalSellingPrice = DomainGateway.getBaseCurrencyInstance();
                    totalFixedPrice = DomainGateway.getBaseCurrencyInstance();
                    CurrencyIfc fixedPrice = discountRule.getFixedPrice();
                    while (targetsIterator.hasNext())
                    {
                        DiscountTargetIfc target = (DiscountTargetIfc)targetsIterator.next();
                        
                        if(target.getExtendedSellingPrice().compareTo(fixedPrice) == CurrencyIfc.GREATER_THAN)
                        {
                            totalSellingPrice = totalSellingPrice.add(target.getExtendedSellingPrice());
                            totalFixedPrice = totalFixedPrice.add(fixedPrice);
                            
                        }
                    }
                    totalDiscount = totalSellingPrice.abs().subtract(totalFixedPrice);
                                        
                }
                //Discount valids only when the fixed price is less than selling price
                else 
                {
                    if (totalSellingPrice.abs().compareTo(totalFixedPrice) == CurrencyIfc.GREATER_THAN)
                
                    {
                        totalDiscount = totalSellingPrice.abs().subtract(totalFixedPrice);
                    }
                }
                break;
            }
            default:
            {
                throw new IllegalStateException (
                "Unknown discount rule type in BestDealGroup.calculateTotalDiscount(): "
                                                 + discountRule.toString());
            }
        } // end switch

        totalDiscount = totalDiscount.abs();
        return totalDiscount;
    }

    /**
     * Calculates the total discount for this possible combination adding the
     * Preferred Customer discount on top of it.
     */
    public CurrencyIfc calculateTotalDiscount(BigDecimal pcd)
    {
        calculateTotalDiscount();
        CurrencyIfc priceAfterDiscount = totalSellingPrice.subtract(totalDiscount);
        CurrencyIfc c = priceAfterDiscount.multiply(pcd);

        totalDiscount = totalDiscount.add(c);

        return totalDiscount;
    }

    /**
     * Updates the total discount for this possible combination adding the
     * Preferred Customer discount on top of it.
     */
    public CurrencyIfc updateTotalDiscount(BigDecimal pcd)
    {
        CurrencyIfc c = totalSellingPrice.multiply(pcd);

        totalDiscount = totalDiscount.add(c);

        return totalDiscount;
    }

    /**
     * Prorates the discount amount across the specified target item. This
     * method should be called repeatedly with each target, taking care to
     * reduce the <code>runningTotal</code> by the <code>target</code>'s
     * resulting extended selling price and the total <code>discount</code> left
     * to be applied each time this method is called.
     * 
     * @param target the item receiving the prorated discount.
     * @param runningTotal the current extended selling price of all the items
     *            in this group not yet prorated, including the target.
     * @param discount the current total discount left to be applied across all
     *            items in the group not yet prorated, including the target.
     * @see BestDealGroupIfc#calculateProratedDiscount(DiscountTargetIfc,
     *      CurrencyIfc, CurrencyIfc)
     * @see DiscountItemIfc#getExtendedSellingPrice()
     */
    public CurrencyIfc calculateProratedDiscount(DiscountTargetIfc target, CurrencyIfc runningTotal,
            CurrencyIfc discount)
    {
        // get bigdecimal values from currencies
        BigDecimal sellingPrice = target.getExtendedSellingPrice().getDecimalValue();
        BigDecimal totalDecimal = runningTotal.getDecimalValue();
        BigDecimal discountDecimal = discount.getDecimalValue();

        // initialize results
        CurrencyIfc proratedDiscount = DomainGateway.getBaseCurrencyInstance();

        // sanity check running total to ensure its not currently zero
        if (runningTotal.compareTo(proratedDiscount) != CurrencyIfc.EQUALS)
        {
            BigDecimal ratioOfTotal = sellingPrice.divide(totalDecimal, 10, BigDecimal.ROUND_HALF_UP);

            // determine final prorated amount
            BigDecimal proratedAmount = ratioOfTotal.multiply(discountDecimal);
            proratedDiscount.setDecimalValue(proratedAmount);
        }

        return proratedDiscount;
    }

    /**
     * Prorates the fixed discount price across target items.
     * 
     * @param CurrencyIfc targetPrice - the extended selling price of a target
     *            item for this discount
     * @param CurrencyIfc targetTotals - the sum extended selling price of all
     *            target items for this discount
     * @param CurrencyIfc fixedPrice - the remaining price to apply for
     *            remaining target items
     * @return CurrencyIfc - the discountAmount for a target item
     */
    public CurrencyIfc calculateFixedPriceDiscount(CurrencyIfc targetPrice, CurrencyIfc targetTotals,
            CurrencyIfc fixedPrice)
    {
        // convert to big decimal values
        BigDecimal targetDecimal = targetPrice.getDecimalValue().abs();
        BigDecimal totalDecimal = targetTotals.getDecimalValue().abs();
        BigDecimal fixedPriceDecimal = fixedPrice.getDecimalValue().abs();

        // get the percentage
        BigDecimal b1 = targetDecimal.divide(totalDecimal, 4, BigDecimal.ROUND_HALF_UP);
        BigDecimal b2 = b1.multiply(fixedPriceDecimal);

        return DomainGateway.getBaseCurrencyInstance(targetDecimal.subtract(b2));
    }

    /**
     * Gets the strategies applicable for the selected rule.
     * 
     * @return DiscountRuleIfc[]
     */
    public ItemDiscountStrategyIfc[] getAdvancedPricingDiscounts()
    {
        Iterator<DiscountTargetIfc> targetsIterator = targets.iterator();
        ArrayList<ItemDiscountStrategyIfc> strategyList = new ArrayList<ItemDiscountStrategyIfc>();

        while (targetsIterator.hasNext())
        {
            DiscountTargetIfc target = targetsIterator.next();
            ItemDiscountStrategyIfc strategy = target.getAdvancedPricingDiscount();

            if (strategy != null)
            {
                strategyList.add(strategy);
            }

        } // end while
        ItemDiscountStrategyIfc[] newStrategies = new ItemDiscountStrategyIfc[strategyList.size()];
        strategyList.toArray(newStrategies);
        return newStrategies;
    }

    /**
     * Creates discount strategies for the selected rule and applies them to
     * each DiscountTarget.
     */
    public void applyAdvancedPricingDiscounts()
    {
        if (logger.isDebugEnabled()) logger.debug("applying best deal: " + this);
        CurrencyIfc runningTotal    = (CurrencyIfc) getTotalSellingPrice().clone();
        CurrencyIfc discountAmount  = null;
        CurrencyIfc fixedPrice      = null;
        int         discountMethod  = discountRule.getDiscountMethod();
        int pricingGroupID = discountRule.getPricingGroupID();
        Iterator<DiscountSourceIfc>    sourcesIterator;
        DiscountTargetIfc source;

        boolean lowestPricedDiscountApplied  = false;
        boolean highestPricedDiscountApplied = false;

        initializeAppliedTargets();

        // If the deal distribution flag is set to true, this means that
        // the discount will be calculated based on the targets, but applied
        // proportionally to all items in the (including sources) in the
        // deal.
        // The discount method is changed to the AMOUNT because it is the
        // only way to distribute the discount across all items in the
        // deal.
        if (discountRule.getDealDistribution())
        {
            discountMethod = DISCOUNT_METHOD_AMOUNT;
            discountAmount = getTotalDiscount();

            // If the sources are targets flag is true, the sources are
            // already in the discountAppliedTargets list: don't add them
            // again.
            if (discountRule.getSourcesAreTargets() == false)
            {
                // Add all sources that are eligibile targets to the target collection
                sourcesIterator = sources.iterator();
                while (sourcesIterator.hasNext() == true)
                {
                  // Cast to DiscountTargetIfc to check if it's an eligible target.
                  source = (DiscountTargetIfc) sourcesIterator.next();
                  if (source.isTargetEnabled() == true)
                  {
                      discountAppliedTargets.add(source);
                  }
                }
            }

            runningTotal = ItemContainerProxy.calculateTotalSellingPrice(discountAppliedTargets);
        }

        if (discountMethod == DISCOUNT_METHOD_FIXED_PRICE)
        {
            // get the fixed price for all target items
            if (discountRule.getDescription().equals(DiscountRuleConstantsIfc.DISCOUNT_DESCRIPTION_BuyNorMoreOfXforZ$Each))
            {
                // For this discount rule both source and targets are same. Get the quantity from
                // the source list and calculate the total fixed price.
                int quantity = 1;
                Object[] criterias = discountRule.getSourceList().criteriaArray();
                if(criterias.length > 0)
                {
                    quantity = discountRule.getSourceList().getQuantity((String)criterias[0]);
                }
                fixedPrice = discountRule.getFixedPrice().multiply(new BigDecimal(quantity));
            }
            else
            {
                fixedPrice  = (CurrencyIfc)discountRule.getFixedPrice().clone();
            }
        }

        if (!discountAppliedTargets.isEmpty())
        {
            Iterator<DiscountTargetIfc> targetsIterator = discountAppliedTargets.iterator();

            //create an ItemDiscountStrategy object and apply it to the targets
            DiscountTargetIfc target = null;

            while (targetsIterator.hasNext())
            {
                ItemDiscountStrategyIfc tempStrategy = null;
                target = targetsIterator.next();

                switch(discountMethod)
                {
                    case DISCOUNT_METHOD_PERCENTAGE :
                    {
                        if (discountRule.getCalcDiscOnItemType() == DiscountRuleConstantsIfc.DISCOUNT_TYPE_ON_ITEM_LOWEST)
                        {
                            if (isLowestPricedTarget(target) &&
                                !lowestPricedDiscountApplied)
                            {
                                lowestPricedDiscountApplied = true;
                                tempStrategy =
                                DomainGateway.getFactory().getItemDiscountByPercentageInstance();
                                tempStrategy.setDiscountRate(discountRule.getDiscountRate());
                                discountAppliedTargets = new ArrayList<DiscountTargetIfc>();
                                discountAppliedTargets.add(target);
                                tempStrategy.setPricingGroupID(pricingGroupID);
                             }
                             else
                             {
                                ((DiscountSourceIfc)target).setSourceAvailable(false);
                             }
                        }
                        else if (discountRule.getCalcDiscOnItemType() == DiscountRuleConstantsIfc.DISCOUNT_TYPE_ON_ITEM_HIGHEST)
                        {
                            if (isHighestPricedTarget(target) &&
                                !highestPricedDiscountApplied)
                            {
                                highestPricedDiscountApplied = true;
                                tempStrategy =
                                DomainGateway.getFactory().getItemDiscountByPercentageInstance();
                                tempStrategy.setDiscountRate(discountRule.getDiscountRate());
                                discountAppliedTargets = new ArrayList<DiscountTargetIfc>();
                                discountAppliedTargets.add(target);
                                tempStrategy.setPricingGroupID(pricingGroupID);
                            }
                            else
                            {
                                ((DiscountSourceIfc)target).setSourceAvailable(false);
                            }
                        }
                        else
                        {
                            tempStrategy =  DomainGateway.getFactory().getItemDiscountByPercentageInstance();

                            tempStrategy.setDiscountRate(discountRule.getDiscountRate());
                            tempStrategy.setPricingGroupID(pricingGroupID);
                        }

                        break;
                    }
                    case DISCOUNT_METHOD_AMOUNT :
                    {
                        tempStrategy =
                            DomainGateway.getFactory().getItemDiscountByAmountInstance();
                        if (discountAmount == null)
                        {
                            discountAmount = getDiscountRule().getDiscountAmount();
                        }
                        if (discountRule.getDescription().equals(DiscountRuleConstantsIfc.DISCOUNT_DESCRIPTION_BuyNorMoreOfXforZ$Each))
                        {
                            if(target.getExtendedSellingPrice().abs().compareTo(getDiscountRule().getFixedPrice()) == CurrencyIfc.GREATER_THAN)
                            {
                                CurrencyIfc da = target.getExtendedSellingPrice().abs().subtract(getDiscountRule().getFixedPrice());
                                tempStrategy.setDiscountAmount(da);
                            }
                            else
                            {
                                CurrencyIfc da = DomainGateway.getBaseCurrencyInstance();
                                tempStrategy.setDiscountAmount(da);
                            }
                        }
                        else
                        {
                            tempStrategy.setDiscountAmount(calculateProratedDiscount(target, runningTotal, discountAmount));
                        }
                        runningTotal = runningTotal.subtract(target.getExtendedSellingPrice());
                        discountAmount = discountAmount.subtract(tempStrategy.getDiscountAmount());
                        tempStrategy.setPricingGroupID(pricingGroupID);
                        break;
                    }
                    case DISCOUNT_METHOD_FIXED_PRICE :
                    {
                        tempStrategy =
                            DomainGateway.getFactory().getItemDiscountByFixedPriceStrategyInstance();

                        //get a reference to the price for this target item
                        CurrencyIfc targetPrice = target.getExtendedSellingPrice();

                        //calculate and set the discount amount for this target's strategy
                        discountAmount  = calculateFixedPriceDiscount(targetPrice,
                                                                      runningTotal,
                                                                      fixedPrice);
                        tempStrategy.setDiscountAmount(discountAmount);

                        //reduce the fixed price of the
                        fixedPrice   = fixedPrice.subtract(targetPrice.abs().subtract(discountAmount));
                        runningTotal = runningTotal.subtract(targetPrice);

                        break;
                    }
                    default:
                    {
                            throw new IllegalStateException (
                            "Unknown discount rule type in BestDealGroup.applyAdvancedPricingDiscounts(): "
                                                            + discountRule.toString());
                    }
                }//end switch

                if (tempStrategy != null)
                {
                    strategy = tempStrategy;
                    strategy.setReason(discountRule.getReason());

                    strategy.setRuleID(getRuleID());
                    strategy.setLocalizedNames(discountRule.getLocalizedNames());
                    //set the assignment basis for the strategy to that of the parent rule
                    strategy.setAssignmentBasis(discountRule.getAssignmentBasis());
                    // set the advancedPricingFlag
                    strategy.setAdvancedPricingRule(true);
                    strategy.setIncludedInBestDeal(discountRule.isIncludedInBestDeal());
                    // add referenceID referenceIDTypeCode
                    strategy.setReferenceID(discountRule.getReferenceID());
                    strategy.setReferenceIDCode(discountRule.getReferenceIDCode());
                    strategy.setAccountingMethod(discountRule.getAccountingMethod());

                    // Add Temporary Change Promotion Ids
                    strategy.setPromotionId(discountRule.getPromotionId());
                    strategy.setPromotionComponentId(discountRule.getPromotionComponentId());
                    strategy.setPromotionComponentDetailId(discountRule.getPromotionComponentDetailId());

                    // clear the previously applied strategy
                    target.removeAdvancedPricingDiscount();

                    target.applyAdvancedPricingDiscount(strategy);
                }

            }// end while targets.hasNext()

            // Set all sources used by this rule to be unavailable.
            setSourceAvailability(false);

            bestDeal = true;
        }
        else
        {
             throw new IllegalStateException (
                        "No targets in BestDealGroup.applyAdvancedPricingDiscounts(): "
                                                         + discountRule.toString());
        }
    }

    /**
     * Initialize the discountAppliedTargets list with the known targets. If
     * the <code>targetAnyQuantity</code> is set, some targets may be eliminated
     * from the list.
     */
    protected void initializeAppliedTargets()
    {
        discountAppliedTargets = new ArrayList<DiscountTargetIfc>(targets);
    }

    /**
     * Return from the list of {@link #targets} which target has not yet been
     * added to the list of <code>targetsChosen</code> and makes most sense.
     *
     * @param targetsChosen
     * @return
     */
    protected DiscountTargetIfc getNextApplicableTarget(List<DiscountTargetIfc> targetsChosen)
    {
        SaleReturnLineItemIfc chosen = null;

        // set up list of available to choose from
        List<DiscountTargetIfc> notYetChosen = new ArrayList<DiscountTargetIfc>(targets);
        notYetChosen.removeAll(targetsChosen);

        for (Iterator<DiscountTargetIfc> iter = notYetChosen.iterator(); iter.hasNext();)
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)iter.next();
            if (chosen == null)
            {
                chosen = srli;
            }
            else
            {
                CurrencyIfc highestPrice = chosen.getExtendedSellingPrice();
                CurrencyIfc price = srli.getExtendedSellingPrice();
                if (price.compareTo(highestPrice) == CurrencyIfc.GREATER_THAN)
                {
                    chosen = srli;
                }
            }
        }
        return (DiscountTargetIfc)chosen;
    }

    /**
     * Replace a line item in the bestDealGroup winner collection.
     * 
     * @param SaleReturnLineItemIfc line item object
     * @param index index into line item vector
     * @deprecated as of release 5.5 - BestDealGroups should be regenerated when
     *             a line item is replaced. Maintaining a consistent state
     *             between BestDealGroups when replacing an item would be
     *             complex and prone to error.
     */
    public void replaceLineItem(SaleReturnLineItemIfc newItem, int index)
    {
        boolean replaceSource = false;
        boolean replaceTarget = false;

        SaleReturnLineItemIfc item = null;

        Iterator<DiscountSourceIfc> sourceIter = getSources().iterator();
            // loop through sources
        while(sourceIter.hasNext())
        {
            item = (SaleReturnLineItemIfc)sourceIter.next();
            if(item.getLineNumber() == newItem.getLineNumber())
            {
                sourceIter.remove();
                replaceSource = true;
            }
        }

        // loop through targets
        Iterator<DiscountTargetIfc> targetIter = getTargets().iterator();
        while (targetIter.hasNext())
        {
            item = (SaleReturnLineItemIfc)targetIter.next();
            if(item.getLineNumber() == newItem.getLineNumber())
            {
                targetIter.remove();
                replaceTarget = true;
            }
        }

        // replace the old item with the new one in the appropriate Array
        if (replaceSource)
        {
            getSources().add((DiscountSourceIfc) newItem);
        }
        if (replaceTarget)
        {
            getTargets().add((DiscountTargetIfc) newItem);
        }
    }

    /**
     * Removes the discount strategies associated with the targets.
     */
    public void removeAdvancedPricingDiscounts()
    {
        if (discountAppliedTargets != null)
        {
            Iterator<DiscountTargetIfc> targetsIterator = discountAppliedTargets.iterator();

            // removes the coresponding ItemDiscountStrategy from each target
            while (targetsIterator.hasNext())
            {
                targetsIterator.next().removeAdvancedPricingDiscount();
            }
        }
    }

    /**
     * Removes the sources and targets used by this group from the parameter
     * lists.
     * 
     * @param sources - An ArrayList from which to remove this groups sources
     *            and targets
     * @param targets - An ArrayList from which to remove this groups targets
     *            and sources
     */
    public void removeSourcesAndTargets(ArrayList<DiscountSourceIfc> sources, ArrayList<DiscountTargetIfc> targets)
    {
        for (Iterator<DiscountSourceIfc> i = getSources().iterator(); i.hasNext(); )
        {
            Object o = i.next();

            for (Iterator<DiscountSourceIfc> j = sources.iterator(); j.hasNext(); )
            {
                if (o == j.next())
                {
                    j.remove();
                    break;
                }
            }
            for (Iterator<DiscountTargetIfc> m = targets.iterator(); m.hasNext(); )
            {
                if (o == m.next())
                {
                    m.remove();
                    break;
                }
            }
        }

        for (Iterator<DiscountTargetIfc> k = getTargets().iterator(); k.hasNext(); )
        {
            Object o = k.next();

            for (Iterator<DiscountTargetIfc> l = targets.iterator(); l.hasNext(); )
            {
                if (o == l.next())
                {
                    l.remove();
                    break;
                }
            }
            for (Iterator<DiscountSourceIfc> n = sources.iterator(); n.hasNext(); )
            {
                if (o == n.next())
                {
                    n.remove();
                    break;
                }
            }
        }
    }

    /**
     * @param value whether each source is available
     */
    protected void setSourceAvailability(boolean value)
    {
        if (sources == null)
            return;

        for (Iterator<DiscountSourceIfc> i = sources.iterator(); i.hasNext();)
        {
            i.next().setSourceAvailable(value);
        }
    }

    /**
     * This method this method determines if this target has the lowest price in
     * the target list. There maybe other targets that have the same price,
     * 
     * @Return DiscountRuleIfc implementation
     */
    public boolean isLowestPricedTarget(DiscountTargetIfc target)
    {
        boolean returnValue       = false;
        CurrencyIfc lowestPrice    = discountRule.getLowestTargetPrice(targets);
        SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)target;
        CurrencyIfc tempPrice      = srli.getExtendedSellingPrice();

        if (lowestPrice != null &&
            tempPrice.compareTo(lowestPrice) == CurrencyIfc.EQUALS)
        {
            returnValue = true;
        }

        return returnValue;
    }

    /**
     * This method this method determines if this target has the highest price
     * in the target list. There maybe other targets that have the same price,
     * 
     * @Return DiscountRuleIfc implementation
     */
    public boolean isHighestPricedTarget(DiscountTargetIfc target)
    {
        boolean returnValue = false;
        CurrencyIfc highestPrice = discountRule.getHighestTargetPrice(targets);
        SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) target;
        CurrencyIfc tempPrice = srli.getExtendedSellingPrice();

        if (highestPrice != null && tempPrice.compareTo(highestPrice) == CurrencyIfc.EQUALS)
        {
            returnValue = true;
        }

        return returnValue;
    }

    /**
     * Returns the revision number of this class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }

    /**
     * Clone this object.
     * 
     * @return generic object copy of this object
     */
    @Override
    public Object clone()
    {
        BestDealGroup newClass = new BestDealGroup();
        setCloneAttributes(newClass);
        return newClass;
    }

    /**
     * Sets the cloneable attributes of a BestDealGroup object.
     * 
     * @param BestDealGroupIfc
     */
    protected void setCloneAttributes(BestDealGroup newClass)
    {
        newClass.sources = new ArrayList<DiscountSourceIfc>();
        newClass.targets = new ArrayList<DiscountTargetIfc>();

        //clone the attributes specific to an AdvancedPricingRule
        newClass.bestDeal      = bestDeal;

        if (discountRule != null)
        {
            newClass.discountRule  = (AdvancedPricingRuleIfc)discountRule.clone();
        }

         if (totalDiscount != null)
        {
            newClass.totalDiscount  =  (CurrencyIfc)totalDiscount.clone();
        }

        if (totalSellingPrice != null)
        {
            newClass.totalSellingPrice  = (CurrencyIfc)totalSellingPrice.clone();
        }

        if (strategy != null)
        {
            newClass.strategy  = (ItemDiscountStrategyIfc) strategy.clone();
        }

        if (sources != null)
        {
            Iterator<DiscountSourceIfc> i = sources.iterator();
            DiscountSourceIfc source = null;
            while (i.hasNext())
            {
                source = i.next();
                newClass.sources.add((DiscountSourceIfc)source.clone());
            }
        }

        if (targets != null)
        {
            Iterator<DiscountTargetIfc> i = targets.iterator();
            DiscountTargetIfc target = null;
            while (i.hasNext())
            {
                target = i.next();
                newClass.targets.add((DiscountTargetIfc)target.clone());
            }
        }

        if (discountAppliedTargets != null)
        {
            Iterator<DiscountTargetIfc> i = discountAppliedTargets.iterator();
            DiscountTargetIfc target = null;
            while (i.hasNext())
            {
                target = i.next();
                newClass.discountAppliedTargets.add((DiscountTargetIfc)target.clone());
            }
        }
    }

    /**
     * Method to default display string function.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        StringBuilder strResult = new StringBuilder("Class:  BestDealGroup ");

        strResult.append("(Revision ").append(getRevisionNumber())
                 .append(") @").append(hashCode()).append(Util.EOL)
                 .append("\tbestDeal:        [")
                 .append(isBestDeal()).append("]").append(Util.EOL)
                 .append("\ttotalDiscount:   [").append(getTotalDiscount().toString()).append("]").append(Util.EOL);

        if (getDiscountRule() == null)
        {
            strResult.append("\tdiscountRule:    [null]").append(Util.EOL);
        }
        else
        {
            strResult.append("\tdiscountRule:    [").append(getDiscountRule().getRuleID()).append("]").append(Util.EOL);
        }
        // sources
        if (sources == null)
        {
            strResult.append("\tsources:         [null]").append(Util.EOL);
        }
        else
        {
            strResult.append("\tsources:         [");
            for(Iterator<DiscountSourceIfc> iter = sources.iterator(); iter.hasNext();)
            {
                DiscountSourceIfc source = iter.next();
                strResult.append(source.getItemID()).append(",");
            }
            int len = strResult.length();
            strResult.replace(len - 1, len, "]").append(Util.EOL);
        }
        // targets
        if (targets == null)
        {
            strResult.append("\ttargets:         [null]").append(Util.EOL);
        }
        else
        {
            strResult.append("\ttargets:         [");
            for(Iterator<DiscountTargetIfc> iter = targets.iterator(); iter.hasNext();)
            {
                DiscountTargetIfc target = iter.next();
                strResult.append(target.getItemID()).append(",");
            }
            int len = strResult.length();
            strResult.replace(len - 1, len, "]").append(Util.EOL);
        }

        // pass back result
        return(strResult.toString());
    }
}
