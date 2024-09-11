/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/AdvancedPricingRule.java /main/57 2013/12/18 14:02:08 tksharma Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    tkshar 12/18/13 - added clearThresholdList()
 *    mjwall 12/17/13 - fix misused calls to Boolean.getBoolean()
 *    tkshar 09/23/13 - modified createDiscountStrategy method to set promoId,
 *                      promoCompId, promoCompDetailId to Transaction Strategy
 *                      object
 *    rabhaw 08/14/13 - cloning exclude items also.
 *    ckanch 07/09/13 - Junit fix - <Null Pointer exception>
 *    rabhaw 07/03/13 - factored transaction rule evaluation to use the
 *                      evaluation from SourceCriteria.java
 *    tkshar 06/26/13 - set description and localized names to
 *                      TransactionStrategy object
 *    rabhaw 06/20/13 - store level rules should allow source to repeat.
 *    tkshar 05/15/13 - added functions to splitSourcesForTransactionRules
 *    tkshar 05/14/13 - Made Allow Source to Repeat always false for storelevel
 *                      rules
 *    tkshar 05/14/13 - introduced allSourceRepeat in transaction rules source
 *                      evaluation
 *    tkshar 05/10/13 - fixed source evaluation for transaction rules
 *    tkshar 05/09/13 - transaction level rule source any criteria evaluate fix
 *    tkshar 03/12/13 - added excludeDscountIneligibleItems (...) for
 *                      transaction level discounts
 *    tkshar 02/22/13 - created evaluateSourcesForQtyBasedTransRules to support
 *                      transaction level discounts with BuyNorMoreOfX for
 *                      Z$/%off
 *    rabhaw 01/22/13 - modifed evaluateSourcesForTransactionRules to fix
 *                      non-StoreLevel rules
 *    tkshar 01/08/13 - used ExtendedSellingPrice instead of
 *                      ExtendedDiscountedSellingPrice in
 *                      evaluateSourcesForTransactionRules method
 *    tkshar 12/09/12 - modified evaluateSources method to fix transaction
 *                      scope advancedPricingRule criteria eval
 *    tkshar 10/11/12 - fixed infinite loop issue in evaluateSources method
 *    tkshar 10/09/12 - modified excludeRuleExclusions method to check for
 *                      source.getItemID()
 *    jswan  08/30/12 - Initializing excludedItems data member to prevent null
 *                      pointer exception.
 *    tkshar 08/20/12 - CR # 188 - exclude items from promotion
 *    tkshar 08/02/12 - multithreshold-merge with sthallam code
 *    sthall 07/10/12 - Enhanced RPM Integration - Multithreshold promotions
 *    tkshar 06/07/12 - code merge with clearance code for RPM Integration
 *    tkshar 06/06/12 - Enhanced RPM Integration - New Discount Rules
 *    sthall 05/30/12 - Enhanced RPM Integration - Clearance Pricing
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    mchell 08/11/11 - BUG#12623177 Added support for Equal or Lesser Value
 *                      (EOLV)
 *    sgu    09/09/10 - remove the logic to remove the source if it is not
 *                      target eligible
 *    sgu    09/09/10 - remove the target if it not discount eligible
 *    sgu    09/09/10 - fix store coupon discount rule
 *    dwfung 07/09/10 - fixed advanced group pricing for returns
 *    cgreen 06/22/10 - Do not clone rule criteria when cloning rule to avoid
 *                      heap space wastage
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    vapart 02/19/10 - Added code to handle discount rules when the source and
 *                      target is a class.
 *    abonda 01/03/10 - update header date
 *    cgreen 09/03/09 - XbranchMerge cgreene_bug-8860860 from
 *                      rgbustores_13.1x_branch
 *    cgreen 09/03/09 - add logic to remove sources that are not target
 *                      eligible when sourcesAreTargets
 *    stalla 06/08/09 - The method setReasonCode(LocalizedCodeIfc ) renamed as
 *                      setReason(LocalizedCodeIfc ) to override the method
 *                      DiscountRule.setReason(LocalizedCodeIfc )
 *    cgreen 04/14/09 - convert pricingGroupID to integer instead of string
 *    mahisi 03/18/09 - Fixed CSP issue if item qty change and customer link to
 *                      the transaction
 *    deghos 02/12/09 - Cleaning the deprecated method toJournalString()
 *    deghos 12/23/08 - EJ i18n changes
 *    vcheng 12/17/08 - ej defect fixes
 *    deghos 12/08/08 - EJ i18n changes
 *    npoola 11/30/08 - CSP POS and BO changes
 *    lslepe 11/05/08 - add rules of type BuyNorMoreOfXforZ%off and
 *                      BuyNorMoreOfXforZ$each
 *    acadar 11/03/08 - localization of reason codes for discounts and merging
 *                      to tip
 *    acadar 11/02/08 - cleanup
 *    akandr 10/31/08 - EJ Changes_I18n
 *    acadar 10/30/08 - use localized reason codes for item and transaction
 *                      discounts
 *    akandr 10/23/08 - new helper class is used.
 *    akandr 10/20/08 - new method added to take the client's journal locale
 *    akandr 10/16/08 -
 *    acadar 10/15/08 - I18n changes for discount rules: code reviews comments
 *    acadar 10/14/08 - unit test fixes
 *    acadar 10/14/08 - updates for reading the localized discount name for
 *                      customer
 *    acadar 10/09/08 - merges with label
 *    cgreen 10/08/08 - removed deprecated method from 5.0, added generics
 *
 * ===========================================================================
 * $Log:
      10   360Commerce 1.9         4/3/2008 10:36:42 PM   Leona R. Slepetis
           Insert ID_PRM into TR_LTM_DSC for a store coupon. Reviewed by
           A.Sinton
      9    360Commerce 1.8         11/15/2007 10:48:46 AM Christian Greene
           Belize merge - add support for Any/All sources/targets
      8    360Commerce 1.7         7/26/2007 2:33:28 PM   Michael P. Barnett In
            toString(), remove line which initiates circular toString
           invocations.
      7    360Commerce 1.6         5/15/2007 5:53:46 PM   Maisa De Camargo
           Added PromotionId, PromotionComponentId and
           PromotionComponentDetailId
      6    360Commerce 1.5         4/25/2007 10:01:01 AM  Anda D. Cadar   I18N
           merge
      5    360Commerce 1.4         1/25/2006 4:10:48 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      4    360Commerce 1.3         1/22/2006 11:41:27 AM  Ron W. Haight
           Removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:27:11 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:19:34 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:09:25 PM  Robert Pearse
     $:
      5    .v700     1.2.1.1     10/10/2005 11:25:11    Leona R. Slepetis
           revert to pre-CC changes
      4    .v700     1.2.1.0     9/14/2005 14:43:19     Leona R. Slepetis
           resolution for CR5537/CC SCR 1031 Advanced Pricing Item Eligibility
      3    360Commerce1.2         3/31/2005 15:27:11     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:19:34     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:09:25     Robert Pearse
     $
     Revision 1.4  2004/02/17 16:18:50  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:28  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:27  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:34:50   CSchellenger
 * Initial revision.
 *
 *    Rev 1.6   Jun 16 2003 12:05:06   bwf
 * Added code to handle new advanced pricing functionality.
 * Resolution for 2765: Advanced Pricing Rule - Discount on Highest Priced Item
 *
 *    Rev 1.5   Mar 20 2003 09:26:26   jgs
 * Changes due to code reveiw.
 * Resolution for 103: New Advanced Pricing Features
 *
 *    Rev 1.4   Feb 17 2003 11:45:22   adc
 * Check for null in getFormattedRuleID
 * Resolution for 1853: Advanced Pricing  Unit Test
 *
 *    Rev 1.3   Jan 22 2003 09:39:42   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.2   Jan 20 2003 11:50:12   jgs
 * Added allow repeating sources, deal distribution, and percent off lowest priced Item to Advanced Pricing Rule processing.
 * Resolution for 103: New Advanced Pricing Features
 *
 *    Rev 1.1   Dec 13 2002 10:39:18   pjf
 * Don't clone best deal groups, recalculate best deal instead.
 * Resolution for 101: Merge KB discount fixes.

 *    Rev 1.0   Mar 18 2002 12:17:42   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.domain.discount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.ItemContainerProxyIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItem;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import oracle.retail.stores.foundation.utility.xml.XMLConverterIfc;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/**
 * AdvancedPricingRule aggregates the criteria for applying an advanced discount
 * to an item or groups of items.
 *
 * @version $Revision: /main/57 $
 */
/**
 * @author tksharma
 *
 */
public class AdvancedPricingRule extends DiscountRule
                                 implements AdvancedPricingRuleIfc
{
    /** Generated Serial Version UID */
    private static final long serialVersionUID = -5461358483414712885L;

    /** The logger to which log messages will be sent. */
	private static Logger logger = Logger.getLogger(AdvancedPricingRule.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/57 $";

    /**
     * indicates whether the sources for this rule are to be used as its targets
     */
    protected boolean sourcesAreTargets = false;

    /**
     * indicates whether the sources should be checked against the source
     * criteria list (value is set to false in order to include preferred
     * customer discounts in best deal calculation)
     */
    protected boolean checkSources = true;

    /**
     * the number of times this rule should be applied per transaction (number
     * of bestDealGroups to generate) the default value of -1 indicates that the
     * application of the rule is unlimited
     */
    protected int applicationLimit = DISCOUNT_APPLICATION_COUNT_UNLIMITED;

    /**
     * contains the source criteria for this rule
     */
    protected DiscountListIfc sourceList = null;

    /**
     * contains the target criteria for this rule
     */
    protected DiscountListIfc targetList = null;

    /**
     * temporary storage for the items used to satisfy the source criteria for
     * this rule
     */
    protected ArrayList<DiscountSourceIfc> sources = null;

    /**
     * temporary storage for the items used to satisfy the target criteria for
     * this rule
     */
    protected ArrayList<DiscountTargetIfc> targets = null;

    /**
     * fixed selling price for fixed price discounts
     */
    protected CurrencyIfc fixedPrice = null;

    /**
     * contains the best deal groups that were generated by this rule
     */
    protected ArrayList<BestDealGroupIfc> groups = null;

    /**
     * indicates the Store ID - added to support Back Office
     */
    protected String storeID = null;

    /**
     * If true, the disocunt amount should be distributed over all items in the
     * deal, including sources.
     */
    protected boolean dealDistribution = false;

    /**
     * If 0, no discount. If 1, discount on lowest item price. If 2, discount on
     * highest item price.
     */
    protected int calcDiscOnItemType = DiscountRuleConstantsIfc.DISCOUNT_TYPE_ON_ITEM_NONE;

    /**
     * The Discount Promotion Id
     */
    protected int promotionId;

    /**
     * The Discount Promotion Component Id
     */
    protected int promotionComponentId;

    /**
     * The Discount Promotion Component Detail Id
     */
    protected int promotionComponentDetailId;

    /**
     * The pricing Group Id
     */
    protected int pricingGroupID = -1;

    /**
     * indicates whether the total value of the targets selected as qualified by
     * this rule must also be "equal to or less than" the value (EOLV) of the
     * selected sources. By default, EOLV is disabled.
     */
    protected boolean equalOrLesserValue;

    /**
     * The source item price category
     */
    protected String sourceItemPriceCategory;

    /**
     * The target item price category
     */
    protected String targetItemPriceCategory;

    /**
     * The thresholdList
     */
    protected List<Threshold> thresholdList;
    
    /**
     *  The excluded Items
     */
    protected List<String> excludedItems;
    /**
     * EOLV property name
     */
    public static final String EOLV_PROPERTY_NAME = "enableEqualOrLesserValue";

    /**
     * Default constructor.
     */
    public AdvancedPricingRule()
    {
        sources = new ArrayList<DiscountSourceIfc>();
        targets = new ArrayList<DiscountTargetIfc>();
        groups = new ArrayList<BestDealGroupIfc>();
        thresholdList = new ArrayList<Threshold>();
        excludedItems = new ArrayList<String>();
        
        sourceList = DomainGateway.getFactory().getSourceCriteriaInstance();
        targetList = DomainGateway.getFactory().getTargetCriteriaInstance();
        
        // advanced pricing discounts are assigned by item selection
        assignmentBasis = ASSIGNMENT_ITEM;
        includedInBestDeal = true;

        fixedPrice = DomainGateway.getBaseCurrencyInstance();

        equalOrLesserValue = Boolean.parseBoolean(DomainGateway.getProperty(EOLV_PROPERTY_NAME, "false"));
    }

    /**
     * Calls {@link #generateBestDealGroups(ArrayList, ArrayList, int) with {
     * @link Integer#MAX_VALUE}.
     * 
     * @param ArrayList sa - an array list containing potential
     *            DiscountSourceIfcs for this rule.
     * @param ArrayList ta - an array list containing potential
     *            DiscountTargetIfcs for this rule.
     */
    public ArrayList<BestDealGroupIfc> generateBestDealGroups(ArrayList<DiscountSourceIfc> sources,
            ArrayList<DiscountTargetIfc> targets)
    {
        return generateBestDealGroups(sources, targets, Integer.MAX_VALUE);
    }

    /**
     * Generates and returns BestDealGroups for this rule if all its source and
     * target criteria are met, otherwise returns an empty array list.
     * 
     * @param ArrayList sa - an array list containing potential
     *            DiscountSourceIfcs for this rule.
     * @param ArrayList ta - an array list containing potential
     *            DiscountTargetIfcs for this rule.
     * @param int groupLimit - an integer limit on the number of groups to
     *        generate
     */
    public ArrayList<BestDealGroupIfc> generateBestDealGroups(ArrayList<DiscountSourceIfc> sources,
            ArrayList<DiscountTargetIfc> targets, int groupLimit)
    {
        reset();
        excludeRuleExclusions(sources, targets);
        validateItemPrices(sources, targets);
        validateItemPriceCategories(sources, targets);
        int numberGenerated = 0;

        while ((numberGenerated < groupLimit) && evaluateSourceAndTargets(sources, targets))
        {
            // selected sources and targets can no longer be sources
            removeSelectedSources(sources);
            removeSelectedTargets(sources);
            addBestDealGroup();
            numberGenerated++;
        }

        createSuperGroups();

        return groups;
    }

    /**
     * Instantiates a best deal group, initializes it with source/target
     * references, and adds it to the collection of groups maintained by this
     * rule.
     */
    @SuppressWarnings("unchecked")
    protected void addBestDealGroup()
    {
        // create a new BestDealGroup
        BestDealGroupIfc group = DomainGateway.getFactory().getBestDealGroupInstance();
        group.setDiscountRule(this);

        // calculate the discount amount
        CurrencyIfc totalDiscount = group.calculateTotalDiscount();
        if (totalDiscount.signum() > 0) // Apply discount only when the discount
                                        // amount is positive
        {
            // set groups sources/targets
            group.setSources((ArrayList)sources.clone());
            group.setTargets((ArrayList)targets.clone());

            // add to temporary group collection
            groups.add(group);
        }

        // clear the temp source/target collections
        sources.clear();
        targets.clear();
    }

    /**
     * Generates a number of Supergroups from the set of BestDealGroups
     * contained in the groups list. Backfills the list with SuperGroups and
     * removes the excess BestDealGroups from the front of the list.
     */
    protected void createSuperGroups()
    {
        int groupSize = groups.size();

        // create SuperGroups if there was more than one set
        if (groupSize > 1)
        {
            for (int i = groupSize; i > 1; i--)
            {
                ArrayList<BestDealGroupIfc> list = new ArrayList<BestDealGroupIfc>();

                for (int j = 0; j < i; j++)
                {
                    list.add(groups.get(j));
                }
                SuperGroupIfc supergroup = DomainGateway.getFactory().getSuperGroupInstance();
                supergroup.setSubgroups(list);
                supergroup.setDiscountRule(this);
                groups.add(supergroup);
            }
            for (int i = 1; i < groupSize; i++)
            {
                groups.remove(1);
            }
        }
    }

    /**
     * Tests to see if the source criteria for this rule have been met. Adds any
     * objects which satisfy a source criterion to the collection of sources for
     * this rule. The contents of this collection are reset each time this
     * method is called.
     * 
     * @param ArrayList containing potential sources for this rule
     * @return boolean indicating whether all the source criteria for this rule
     *         have been met.
     */
    protected boolean sourcesSatisfied(ArrayList<DiscountSourceIfc> possibleSources)
    {
        boolean satisfied = false;

        if (checkSources)
        {
            if (getThresholdTypeCode() == THRESHOLD_AMOUNT)
            {
                removeTarget(possibleSources);
            }

            // When both the source and target of the discount rule are set to
            // "Class", sources
            // are evaluated in this flow.
            if (areSourceAndTargetSetToMerchandiseClass())
            {
                satisfied = sourceList.evaluateAllEligibleSourcesAndTargets(possibleSources, sources);
            }
            else
            {
                satisfied = sourceList.evaluate(possibleSources, sources);
            }

            // if the source list is satisfied for this rule, debug the names
            if (satisfied && logger.isDebugEnabled())
            {
                logger.debug("The following rule has required sources: Rule ID:" + getRuleID() + ", Name: "
                        + getLocalizedNames());
            }
        }
        else
        // not evaluating source criteria so just add all to sources
        {
            if (!possibleSources.isEmpty())
            {
                sources.addAll(possibleSources);
                possibleSources.clear();
                satisfied = true;
            }
            else
            {
                // exit the calling loop if sources have already been added
                satisfied = false;
            }
        }
        return satisfied;
    }

    /**
     * Tests to see if the target criteria for this rule have been met. Adds any
     * objects which satisfy a target criterion to the collection of targets for
     * this rule. The contents of the target collection are reset each time this
     * method is called. Note: If the sourcesAreTargets attribute is set to
     * true, the check against the targetList data is bypassed and the targets
     * collection is set to the value of the sources collection.
     * 
     * @param ArrayList containing potential targets for this rule
     * @return boolean indicating whether all the target criteria for this rule
     *         have been met.
     */
    protected boolean targetsSatisfied(ArrayList<DiscountTargetIfc> possibleTargets)
    {
        boolean value = false;

        if (sourcesAreTargets)
        {
            value = convertSourcesToTargets();
        }
        else
        {
            // When both the source and target of the discount rule are set to
            // "Class", or when EOLV is enabled targets
            // are evaluated in this flow.
            if (areSourceAndTargetSetToMerchandiseClass() || isEqualOrLesserValue())
            {
                value = targetList.evaluateAllEligibleSourcesAndTargets(possibleTargets, targets);
            }
            else
            {
                // sources are not targets so check against the criteria in the
                // list
                removeSelectedSources(possibleTargets);
                value = targetList.evaluate(possibleTargets, targets);
            }

        }
        return value;
    }

    /**
     * Empties the temporary storage containers that are used by this rule to
     * generate best deal groups.
     */
    protected void reset()
    {
        sources.clear();
        targets.clear();
        groups.clear();
    }

    /**
     * Clone this object.
     * 
     * @return generic object copy of this object
     */
    public Object clone()
    {
        AdvancedPricingRule newClass = new AdvancedPricingRule();

        setCloneAttributes(newClass);

        return newClass;
    }

    /**
     * Sets the clone attributes for this object.
     * <p>
     * Attempting a deep clone here is be expensive and prone to error. Cloning
     * the {@link #sourceList} and {@link #targetList} causes un-necessary heap
     * space growth when these rule criteria objects really shouldn't ever
     * change for this rule. Hence they are skipped. The sources, targets and
     * groups attributes will be re-populated when
     * {@link ItemContainerProxyIfc#calculateBestDeal()} is called from within
     * its own {@link Object#clone()} method.
     * 
     * @param AdvancedPricingRule to set the clone attributes on
     */
    public void setCloneAttributes(AdvancedPricingRule newClass)
    {
        // clone the superclass attributes
        super.setCloneAttributes(newClass);

        // clone the attributes specific to an AdvancedPricingRule
        newClass.applicationLimit = applicationLimit;
        newClass.checkSources = checkSources;
        newClass.sourcesAreTargets = sourcesAreTargets;
        newClass.sourceList = (DiscountListIfc)sourceList.clone();
        newClass.targetList = (DiscountListIfc)targetList.clone();
        newClass.fixedPrice = fixedPrice == null ? fixedPrice : (CurrencyIfc)fixedPrice.clone();
        newClass.storeID = storeID;
        newClass.dealDistribution = dealDistribution;
        newClass.calcDiscOnItemType = calcDiscOnItemType;
        newClass.promotionId = promotionId;
        newClass.promotionComponentId = promotionComponentId;
        newClass.promotionComponentDetailId = promotionComponentDetailId;
        newClass.pricingGroupID = pricingGroupID;
        newClass.equalOrLesserValue = equalOrLesserValue;
        newClass.sourceItemPriceCategory = sourceItemPriceCategory;
        newClass.targetItemPriceCategory = targetItemPriceCategory;
        newClass.thresholdList.addAll(thresholdList);
        newClass.excludedItems.addAll(excludedItems);
    }

    /**
     * Determine if two objects are identical.
     * 
     * @param obj object to compare with
     * @return true if the objects are identical, false otherwise
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true; // quick exit

        boolean equal = false;

        if (obj instanceof AdvancedPricingRule && super.equals(obj))
        {
            AdvancedPricingRule rule = (AdvancedPricingRule)obj;

            if (sourcesAreTargets == rule.sourcesAreTargets
                    && checkSources == rule.checkSources
                    && dealDistribution == rule.getDealDistribution()
                    && calcDiscOnItemType == rule.getCalcDiscOnItemType()
                    && promotionId == rule.getPromotionId()
                    && promotionComponentId == rule.getPromotionComponentId()
                    && promotionComponentDetailId == rule.getPromotionComponentDetailId()
                    && equalOrLesserValue == rule.equalOrLesserValue
                    && sourceList.equals(rule.sourceList)
                    && targetList.equals(rule.targetList)
                    && sources.equals(rule.sources)
                    && targets.equals(rule.targets)
                    && groups.equals(rule.groups)
                    && fixedPrice.equals(rule.fixedPrice)
                    && Util.isObjectEqual(storeID, rule.storeID))
            {
                equal = true;
            }
        }
        return equal;
    }

    /**
     * Returns a boolean indicating whether there is a limit on the number of
     * times this rule can be applied.
     * <P>
     * if the applicationLimit value is 0 or a positive number, the number of
     * times this rule can be applied is limited.
     * 
     * @return boolean
     */
    public boolean isApplicationLimited()
    {
        return (applicationLimit > DISCOUNT_APPLICATION_COUNT_UNLIMITED);
    }

    
    /**
     * Returns a boolean indicating whether the discount rule is of type multithreshold.
     * 
     * @return boolean
     */
    public boolean isThresholdDiscountRule()
    {
        boolean multithresholdDiscountRule = false;
        if(thresholdList!=null)
        {
            multithresholdDiscountRule = (thresholdList.size()>0);
        }
        return multithresholdDiscountRule;
    }
    
    /**
     * Returns a boolean indicating whether this rule is intended to generate
     * transaction wide discounts. Currently this is only the case for preferred
     * customer rules with includedInBestDeal flag set to true and transaction
     * level store coupons (by %).
     * 
     * @return boolean
     */
    public boolean isScopeTransaction()
    {
         return (  (assignmentBasis == ASSIGNMENT_CUSTOMER ||
                    assignmentBasis == ASSIGNMENT_STORE_COUPON || 
                    assignmentBasis == ASSIGNMENT_ITEM)
                             &&
                   (discountScope == DISCOUNT_SCOPE_TRANSACTION)  );
    }

    /**
     * Sets the appropriate flags to cause this rule to calculate and apply
     * transaction level discounts. After calling this method all poetential
     * sources are used to generate a best deal group and become targets for
     * this rule. The source attributes are not verified against the criteria in
     * the source criteria list.
     */
    public void activateTransactionDiscount()
    {
        checkSources = false;
        sourcesAreTargets = true;
    }

    /**
     * Returns the applicationLimit attribute for this rule. This value
     * corresponds to the number of times a rule can be applied on a per
     * transaction basis.
     * 
     * @return int
     */
    public int getApplicationLimit()
    {
        return applicationLimit;
    }

    /**
     * Sets the applicationLimit attribute for this rule. This value corresponds
     * to the number of times a rule can be applied on a per transaction basis.
     * 
     * @param int limit
     */
    public void setApplicationLimit(int limit)
    {
        applicationLimit = limit;
    }

    /**
     * Sets reason code onto this rule object and updates the source and target
     * criteria.
     * 
     * @param value reason code
     * @deprecated as of 13.1.Use {@link setReasonCode(LocalizedCodeIfc value)}
     */
    public void setReasonCode(int value)
    {
        sourceList.setRuleReasonCode(value);
        targetList.setRuleReasonCode(value);
        super.setReasonCode(value);
    }

    /**
     * Sets reason code onto this rule object and updates the source and target
     * criteria.
     * 
     * @param value reason code
     */
    @Override
    public void setReason(LocalizedCodeIfc value)
    {
        sourceList.setRuleReasonCode(Integer.parseInt(value.getCode()));
        targetList.setRuleReasonCode(Integer.parseInt(value.getCode()));
        super.setReason(value);
    }

    /**
     * Returns the any quantity for the source list.
     * 
     * @return any quantity
     */
    public int getSourceAnyQuantity()
    {
        return sourceList.getAnyQuantity();
    }

    /**
     * Sets the any quantity value for the source list.
     * 
     * @param qty any quantity
     */
    public void setSourceAnyQuantity(int qty)
    {
        sourceList.setAnyQuantity(qty);
    }

    /**
     * Returns the source comparison basis constant for this rule.
     * 
     * @return int source comparison basis identifier
     * @see DiscountRuleConstantsIfc
     */
    public int getSourceComparisonBasis()
    {
        return sourceList.getComparisonBasis();
    }

    /**
     * Sets the source comparison basis constant for this rule.
     * 
     * @param int source comparison basis identifier
     * @see DiscountRuleConstantsIfc
     */
    public void setSourceComparisonBasis(int basis)
    {
        sourceList.setComparisonBasis(basis);
    }

    /**
     * Returns the list containing the source criteria for this rule.
     * 
     * @return DiscountListIfc containing source criteria
     * @see DiscountListIfc
     * @see DiscountListEntry
     */
    public DiscountListIfc getSourceList()
    {
        return sourceList;
    }

    /**
     * Sets the list containing the source criteria for this rule.
     * 
     * @param DiscountListIfc containing source criteria
     * @see DiscountListIfc
     * @see DiscountListEntry
     * @deprecated - use setSourceCriteria(SourceCriteria criteria)
     */
    public void setSourceList(DiscountListIfc newList)
    {
        sourceList = newList;
    }

    /**
     * Sets the list containing the source criteria for this rule.
     * 
     * @param DiscountListIfc containing source criteria
     * @see DiscountListIfc
     * @see DiscountListEntry
     */
    public void setSourceCriteria(SourceCriteria newList)
    {
        sourceList = newList;
    }

    /**
     * Returns the source threshold for this rule.
     * 
     * @return CurrencyIfc threshold
     */
    public CurrencyIfc getSourceThreshold()
    {
        return sourceList.getItemThreshold();
    }

    /**
     * Sets the source threshold for this rule.
     * 
     * @param CurrencyIfc threshold
     */
    public void setSourceThreshold(CurrencyIfc threshold)
    {
        sourceList.setItemThreshold(threshold);
    }

    /**
     * Returns the source limit for this rule.
     * 
     * @return CurrencyIfc
     */
    public CurrencyIfc getSourceLimit()
    {
        return sourceList.getItemLimit();
    }

    /**
     * Sets the source limit for this rule.
     * 
     * @param CurrencyIfc limit
     */
    public void setSourceLimit(CurrencyIfc limit)
    {
        sourceList.setItemLimit(limit);
    }

    /**
     * Tests a source's price to see if it is within the limits for this rule.
     * 
     * @param CurrencyIfc limit
     */
    public boolean isSourcePriceValid(DiscountSourceIfc source)
    {
        return sourceList.isPriceValid(source);
    }

    /**
     * Returns the target threshold for this rule.
     * 
     * @return CurrencyIfc threshold
     */
    public CurrencyIfc getTargetThreshold()
    {
        return targetList.getItemThreshold();
    }

    /**
     * Sets the target threshold for this rule.
     * 
     * @param CurrencyIfc threshold
     */
    public void setTargetThreshold(CurrencyIfc threshold)
    {
        targetList.setItemThreshold(threshold);
    }

    /**
     * Returns the target limit for this rule.
     * 
     * @return CurrencyIfc
     */
    public CurrencyIfc getTargetLimit()
    {
        return targetList.getItemLimit();
    }

    /**
     * Sets the target limit for this rule.
     * 
     * @param CurrencyIfc limit
     */
    public void setTargetLimit(CurrencyIfc limit)
    {
        targetList.setItemLimit(limit);
    }

    /**
     * Tests a target's price to see if it is within the limits for this rule.
     * 
     * @param CurrencyIfc limit
     */
    public boolean isTargetPriceValid(DiscountTargetIfc target)
    {
        return targetList.isPriceValid(target);
    }

    /**
     * Returns a boolean indicating whether the argument can be used to satisfy
     * this rule's source criteria.
     * 
     * @return boolean true if source can be used, false otherwise
     */
    public boolean isPotentialSource(DiscountSourceIfc source)
    {
        boolean isSource = false;

        if (checkSources)
        {
            isSource = sourceList.uses(source);
            if (!isSource && isStoreLevelDiscountRule())
            {
                isSource = true;
            }
        }
        else
        {
            // everything is a source
            isSource = true;
        }

        return isSource;
    }

    /**
     * Returns a boolean indicating whether the argument can be used to satisfy
     * this rule's source criteria.
     * 
     * @return boolean true if source can be used, false otherwise
     */
    public boolean isPotentialTarget(DiscountTargetIfc target)
    {
        return targetList.uses(target);
    }

    /**
     * Sets a flag to indicate if the sources for this rule are also its
     * targets.
     * 
     * @param boolean true if sources are targets, false otherwise
     */
    public void setSourcesAreTargets(boolean value)
    {
        sourcesAreTargets = value;
        if (sourceList instanceof SourceCriteria)
        {
            ((SourceCriteria)sourceList).setSourcesAreTargets(value);
        }
        else
        {
            logger.error("Unable to set SourcesAreTargets flag in sourceList because sourceList is not an instance of SourceCriteria");
        }
    }

    /**
     * Return the flag that determines if the sources for this rule are also its
     * targets.
     * 
     * @return true if the sources are targets, false otherwise
     */
    public boolean getSourcesAreTargets()
    {
        return sourcesAreTargets;
    }

    /**
     * Returns the any quantity for the target list.
     * 
     * @return any quantity
     */
    public int getTargetAnyQuantity()
    {
        return targetList.getAnyQuantity();
    }

    /**
     * Sets the any quantity value for the target list.
     * 
     * @param qty any quantity
     */
    public void setTargetAnyQuantity(int qty)
    {
        targetList.setAnyQuantity(qty);
    }

    /**
     * Sets the target comparison basis constant for this rule.
     * 
     * @param int target comparison basis identifier
     * @see DiscountRuleConstantsIfc
     */
    public void setTargetComparisonBasis(int basis)
    {
        targetList.setComparisonBasis(basis);
    }

    /**
     * Returns the target comparison basis constant for this rule.
     * 
     * @return int target comparison basis identifier
     * @see DiscountRuleConstantsIfc
     */
    public int getTargetComparisonBasis()
    {
        return targetList.getComparisonBasis();
    }

    /**
     * Returns the list containing the target criteria for this rule.
     * 
     * @param DiscountListIfc containing target criteria
     * @see DiscountListIfc
     * @see DiscountListEntry
     */
    public DiscountListIfc getTargetList()
    {
        return targetList;
    }

    /**
     * Sets the list containing the target criteria for this rule.
     * 
     * @param DiscountListIfc containing target criteria
     * @see DiscountListIfc
     * @see DiscountListEntry
     * @deprecated use setTargetCriteria instead.
     */
    public void setTargetList(DiscountListIfc newList)
    {
        targetList = newList;
    }

    /**
     * Sets the list containing the target criteria for this rule.
     * 
     * @param TargetCriteria containing target criteria
     * @see DiscountListIfc
     * @see DiscountListEntry
     */
    public void setTargetCriteria(TargetCriteria newList)
    {
        targetList = newList;
    }

    /**
     * Returns the flag that determines if this advanced pricing strategy is
     * eligible for best deal calculations.
     * 
     * @return true if the rule is eligible, false otherwise
     * @deprecated use isIncludedInBestDeal() instead
     */
    public boolean isBestDealEligible()
    {
        return includedInBestDeal;
    }

    /**
     * Sets store identifier.
     * 
     * @param value store identifier
     */
    public void setStoreID(String value)
    {
        storeID = value;
    }

    /**
     * Retrieves store identifier.
     * 
     * @return store identifier
     */
    public String getStoreID()
    {
        return (storeID);
    }

    /**
     * Sets the flag that determines if this advanced pricing strategy is
     * eligible for best deal calculations.
     * 
     * @param boolean indicating best deal eligibility
     */
    public void setBestDealEligible(boolean b)
    {
        includedInBestDeal = b;
    }

    /**
     * Retrieves the threshold type code of an advanced pricing rule. As of
     * release 5.0, threshold type applies to source criteria only.
     * 
     * @return threshold type code of discount rule
     */
    public int getThresholdTypeCode()
    {
        return (sourceList.getThresholdType());
    }

    /**
     * Sets thresholdTypeCode of an advanced pricing rule.
     * 
     * @param value - threshold type code of advanced pricing rule
     */
    public void setThresholdTypeCode(int value)
    {
        sourceList.setThresholdType(value);
    }

    /**
     * Returns an ArrayList containing the BestDealGroups that were generated by
     * this rule.
     * 
     * @return ArrayList containing BestDealGroups
     * @see BestDealGroupIfc
     */
    public ArrayList<BestDealGroupIfc> getBestDealGroups()
    {
        return groups;
    }

    /**
     * Gets the Deal Distribution Flag.
     * 
     * @return true if discount sould be shared by all items in the deal.
     */
    public boolean getDealDistribution()
    {
        return dealDistribution;
    }

    /**
     * Sets the Deal Distribution Flag.
     * 
     * @param value - the Deal Distribution flag.
     */
    public void setDealDistribution(boolean value)
    {
        dealDistribution = value;
    }

    /**
     * Gets the Calculate Discount On Item Type.
     * 
     * @return 0 if discount not applied, 1 if applied to lowest priced item, 2
     *         if applied to highest priced item
     */
    public int getCalcDiscOnItemType()
    {
        return calcDiscOnItemType;
    }

    /**
     * Sets the Calculate Discount On Item Type.
     * 
     * @param value - the Calculate Discount Type On Priced Item.
     */
    public void setCalcDiscOnItemType(int value)
    {
        calcDiscOnItemType = value;
    }

    /**
     * Gets the Allow Repeating Sources Flag. This overrides the method in
     * Discount Rule.
     * 
     * @return true if sources may have the same Item ID.
     */
    public boolean getAllowRepeatingSources()
    {
        return ((SourceCriteria)sourceList).getAllowRepeatingSources();
    }

    /**
     * Sets the Allow Repeating Sources Flag. This overrides the method in
     * Discount Rule.
     * 
     * @param value - the Allow Repeating Sources flag.
     */
    public void setAllowRepeatingSources(boolean value)
    {
        ((SourceCriteria)sourceList).setAllowRepeatingSources(value);
    }

    /**
     * Checks validity of line item's itemPriceCategory against
     * itemPriceCategory of AdvancedPricingRule
     * 
     * @param sa - ArrayList containing potential source items for this rule
     * @param ta - ArrayList containing potential target items for this rule
     */
    protected void validateItemPriceCategories(ArrayList<DiscountSourceIfc> sa, ArrayList<DiscountTargetIfc> ta)
    {
        if (sa != null)
        {
            for (Iterator<DiscountSourceIfc> i = sa.iterator(); i.hasNext();)
            {
                DiscountSourceIfc source = i.next();
                String ItemPriceCategory = getItemPriceCategory(source.isOnClearance());
                if (getSourceItemPriceCategory() != null && !getSourceItemPriceCategory().equals(ItemPriceCategory)
                        && !getSourceItemPriceCategory().equals(DiscountRuleConstantsIfc.ITEMPRICECATEGORY_BOTH))
                    i.remove();
            }
        }
        if (ta != null)
        {
            for (Iterator<DiscountTargetIfc> i = ta.iterator(); i.hasNext();)
            {
                DiscountTargetIfc source = i.next();
                String ItemPriceCategory = getItemPriceCategory(source.isOnClearance());
                if (getTargetItemPriceCategory() != null && !getTargetItemPriceCategory().equals(ItemPriceCategory)
                        && !getTargetItemPriceCategory().equals(DiscountRuleConstantsIfc.ITEMPRICECATEGORY_BOTH))
                    i.remove();
            }
        }
    }

    private String getItemPriceCategory(boolean onClearance)
    {
        if (onClearance)
            return DiscountRuleConstantsIfc.ITEMPRICECATEGORY_CLEARANCE;
        else
            return DiscountRuleConstantsIfc.ITEMPRICECATEGORY_REGULAR;
    }

    /**
     * Checks validity of line item prices against limit/threshold criteria.
     * Removes source or target items that do not meet the criteria from the
     * array lists.
     * 
     * @param sa - ArrayList containing potential source items for this rule
     * @param ta - ArrayList containing potential target items for this rule
     */
    protected void validateItemPrices(ArrayList<DiscountSourceIfc> sa, ArrayList<DiscountTargetIfc> ta)
    {
        if (sa != null)
        {
            for (Iterator<DiscountSourceIfc> i = sa.iterator(); i.hasNext();)
            {
                if (!isSourcePriceValid(i.next()))
                {
                    i.remove();
                }
            }
        }
        if (sourcesAreTargets)
        {
            if (sa != null)
            {
                for (Iterator<DiscountSourceIfc> i = sa.iterator(); i.hasNext();)
                {
                    if (!isTargetPriceValid((DiscountTargetIfc)i.next()))
                    {
                        i.remove();
                    }
                }
            }
        }
        else
        {
            if (ta != null)
            {
                for (Iterator<DiscountTargetIfc> i = ta.iterator(); i.hasNext();)
                {
                    if (!isTargetPriceValid(i.next()))
                    {
                        i.remove();
                    }
                }
            }
        }
    }

    /**
     * Removes selected targets from the list of potential sources passed in as
     * a parameter.
     * 
     * @param ArrayList containing potential sources
     */
    protected void removeSelectedTargets(ArrayList<DiscountSourceIfc> possibleSources)
    {
        // remove targets from the list of possible sources
        // for the next iteration of the while loop
        Object o = null;
        for (Iterator<DiscountSourceIfc> s = possibleSources.iterator(); s.hasNext();)
        {
            o = s.next();
            for (Iterator<DiscountTargetIfc> t = targets.iterator(); t.hasNext();)
            {
                if (o == t.next())
                {
                    s.remove();
                    break;
                }
            }
        }
    }

    /**
     * Removes selected sources from the list of potential targets passed in as
     * a parameter.
     * 
     * @param ArrayList containing potential targets
     */
    @SuppressWarnings("unchecked")
    protected void removeSelectedSources(ArrayList possibleTargets)
    {
        // remove any items that are sources from the possible targets
        Object o = null;
        for (Iterator t = possibleTargets.iterator(); t.hasNext();)
        {
            o = t.next();
            for (Iterator<DiscountSourceIfc> s = sources.iterator(); s.hasNext();)
            {
                if (o == s.next())
                {
                    t.remove();
                    break;
                }
            }
        }
    }

    /**
     * Removes selected targets from the list of potential sources passed in as
     * a parameter.
     * 
     * @param ArrayList containing potential sources
     * @param ArrayList containing potential targets
     */
    @SuppressWarnings("unchecked")
    protected void removeSelectedTargets(ArrayList possibleSources, ArrayList possibleTargets)
    {
        // remove any items that are in possibleTargets from the possibleSources
        Object o = null;
        for (Iterator<DiscountSourceIfc> t = possibleTargets.iterator(); t.hasNext();)
        {
            o = t.next();
            for (Iterator<DiscountSourceIfc> s = possibleSources.iterator(); s.hasNext();)
            {
                if (o == s.next())
                {
                    s.remove();
                    break;
                }
            }
        }
    }

    /**
     * Removes a potential target from the list of potential sources passed in
     * as a parameter.
     * 
     * @param ArrayList containing potential sources
     * @return boolean indicating whether a target was removed
     */
    @SuppressWarnings("unchecked")
    protected boolean removeTarget(ArrayList sources)
    {
        boolean value = false;
        // check sources for a potential target
        DiscountTargetIfc target = null;
        for (Iterator s = sources.iterator(); s.hasNext();)
        {
            target = (DiscountTargetIfc)s.next();
            if (isPotentialTarget(target))
            {
                // remove it from the list
                s.remove();
                value = true;
                break;
            }
        }
        return value;
    }

    /**
     * Returns the fixed discount price for this rule. This amount may be
     * pro-rated among the targets for this rule.
     * 
     * @return CurrencyIfc
     */
    public CurrencyIfc getFixedPrice()
    {
        return fixedPrice;
    }

    /**
     * Sets the fixed discount price for this rule. This amount may be prorated
     * among the targets for this rule.
     * 
     * @param CurrencyIfc
     */
    public void setFixedPrice(CurrencyIfc discountPrice)
    {
        fixedPrice = discountPrice;
    }

    /**
     * Returns the flag that determines whether or not to validate the source
     * criteria for this rule.
     * 
     * @return - boolean checkSources
     */
    public boolean checkSources()
    {
        return checkSources;
    }

    /**
     * Sets the flag that determines whether or not to validate the source
     * criteria for this rule.
     * 
     * @param - boolean value
     */
    public void setCheckSources(boolean value)
    {
        checkSources = value;
    }

    /**
     * Returns a String that is used to identify a rule within the context of a
     * Store
     * 
     * @return a concatenation between storeID and ruleID
     */
    public String getFormattedRuleID()
    {
        String formattedID = "";
        if (storeID != null)
        {
            formattedID = storeID.concat(ruleID);
        }

        return formattedID;
    }

    /**
     * Instantiates and initializes the appropriate regular discount strategy
     * using its attribute values. This method is to be used when an advanced
     * pricing rule encapsulates discount strategies that are not eligible for
     * best deal processing. Discounts generated by these strategies will be
     * applied in addition to the discounts applied through the best deal
     * algorithm.
     * 
     * @Return DiscountRuleIfc implementation
     */
    public DiscountRuleIfc createDiscountStrategy()
    {
        DiscountRuleIfc strategy = null;
        if (isScopeTransaction())
        {
            switch (assignmentBasis)
            {
                case ASSIGNMENT_ITEM:
                	
            
                case ASSIGNMENT_STORE_COUPON:
                    switch (discountMethod)
                    {
                        case DISCOUNT_METHOD_PERCENTAGE:
                            strategy = DomainGateway.getFactory().getTransactionDiscountByPercentageInstance();
                            strategy.setRuleID(ruleID);
                            strategy.setRule(this);
                            strategy.setDescription(description);
                            strategy.setLocalizedNames(getLocalizedNames());
                            strategy.setReason(reason);
                            strategy.setAssignmentBasis(assignmentBasis);
                            strategy.setDiscountScope(discountScope);
                            strategy.setDiscountRate(discountRate);
                            strategy.setDiscountAmount(discountAmount);
                            strategy.setReferenceID(referenceID);
                            strategy.setReferenceIDCode(referenceIDCode);
                            strategy.setPromotionId(promotionId);
                            strategy.setPromotionComponentId(promotionComponentId);
                            strategy.setPromotionComponentDetailId(promotionComponentDetailId);
                            strategy.setPostProcessTypeCode(postProcessTypeCode);

                    break;
                case DISCOUNT_METHOD_AMOUNT:
                    strategy = DomainGateway.getFactory().getTransactionDiscountByAmountInstance();
                    strategy.setRule(this);
                    strategy.setRuleID(ruleID);
                    strategy.setDescription(description);
                    strategy.setLocalizedNames(getLocalizedNames());
                    strategy.setReason(reason);
                    strategy.setAssignmentBasis(assignmentBasis);
                    strategy.setDiscountScope(discountScope);
                    strategy.setDiscountAmount(discountAmount);
                    strategy.setReferenceID(referenceID);
                    strategy.setReferenceIDCode(referenceIDCode);
                    strategy.setPromotionId(promotionId);
                    strategy.setPromotionComponentId(promotionComponentId);
                    strategy.setPromotionComponentDetailId(promotionComponentDetailId);
                    strategy.setPostProcessTypeCode(postProcessTypeCode);

                    break;
                default:
                    break;
                }
                break;
            }
        }
        return strategy;
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
        StringBuilder strResult = new StringBuilder("Class:  ");

        strResult
                .append("AdvancedPricingRule (Revision ")
                .append(getRevisionNumber())
                .append(") @")
                .append(hashCode())
                .append(Util.EOL)
                .append("\tcheckSources: " + checkSources + Util.EOL)
                .append("\tsourcesAreTargets: " + sourcesAreTargets + Util.EOL)
                // .append("\tsourceComparisonBasis: " + sourceComparisonBasis +
                // Util.EOL)
                // .append("\ttargetComparisonBasis: " + targetComparisonBasis +
                // Util.EOL)
                .append("\tsourceList:\n" + sourceList + Util.EOL)
                .append("\ttargetList:\n" + targetList + Util.EOL)
                .append("\tstoreID:\n" + storeID + Util.EOL)
                .append("\tfixedPrice: " + fixedPrice)
                // .append("\tthresholdTypeCode: " + thresholdTypeCode)
                // .append("\tgroups: " + groups + Util.EOL) - causes circular
                // toString() calls
                .append("\tpromotionId: " + promotionId + Util.EOL)
                .append("\tpromotionComponentId: " + promotionComponentId + Util.EOL)
                .append("\tpromotionComponentDetailId: " + promotionComponentDetailId + Util.EOL);

        strResult.append(super.toString());

        return (strResult.toString());
    }

    /**
     * Returns journal string for this object.
     * 
     * @return journal string
     * @deprecated as of 13.1. new method added to take the journal locale
     */
    public String toJournalString()
    {
        return (toJournalString(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)));
    }

    /**
     * Returns journal string for this object.
     * 
     * @param journalLocale client's journal Locale
     * @return journal string
     */
    public String toJournalString(Locale journalLocale)
    {
        BigDecimal discountRate = getDiscountRate().movePointLeft(-2);
        StringBuilder strResult = new StringBuilder();
        strResult
                .append(Util.EOL)
                .append(Util.EOL)
                .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.TRANS_DISCOUNT_TAG_LABEL, null, journalLocale)).append(Util.EOL);
        Object[] dataArgs = new Object[] { discountRate.toString() };
        strResult.append(
                I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DISCOUNT_LABEL, dataArgs,
                        journalLocale)).append(Util.EOL);
        dataArgs[0] = getReason().getCode();
        strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.DISCOUNT_RSN_TAG_LABEL, dataArgs, journalLocale));
        return (strResult.toString());
    }

    /**
     * Restores the object from the contents of the xml tree based on the
     * current node property of the converter.
     * 
     * @param converter is the conversion utility
     * @exception XMLConversionException if error occurs translating XML
     */
    public void translateFromElement(XMLConverterIfc converter) throws XMLConversionException
    {
        Element top = converter.getCurrentElement();
        Element[] properties = converter.getChildElements(top, XMLConverterIfc.TAG_PROPERTY);

        // Retrieve and store the values for each property
        for (int i = 0; i < properties.length; i++)
        {
            Element element = properties[i];
            String name = element.getAttribute("name");

            if ("discountRate".equals(name))
            {
                discountRate = (BigDecimal)converter.getPropertyObject(element);
            }
            else if ("enabled".equals(name))
            {
                enabled = Boolean.valueOf(converter.getElementText(element));
            }
            else if ("reasonCode".equals(name))
            {
                reasonCode = Integer.parseInt(converter.getElementText(element));
            }
        }
    }

    /**
     * When both the source and target of the discount rule are set to "Class",
     * evaluates the eligible sources and targets dynamically.
     * 
     * @param availableSources all the available sources
     * @param availableTargets all the available targets
     * @return returns a boolean value indicating the status of the evaluation.
     */
    @SuppressWarnings("unchecked")
    protected boolean evaluateSourceAndTargetsForMerchandiseClassOrEOLV(ArrayList availableSources,
            ArrayList availableTargets)
    {
        boolean allConditionsSatisfied = false;
        boolean satisfied = false;
        ArrayList eligibleSources = new ArrayList();
        ArrayList eligibleTargets = new ArrayList();
        eligibleSources.addAll(availableSources);
        eligibleTargets.addAll(availableTargets);
        // Put all the eligible sources into sourcesTemp and all the eligible
        // targets into
        // targetsTemp.
        satisfied = sourcesSatisfied(eligibleSources) && targetsSatisfied(eligibleTargets);

        if (sourcesAreTargets)
        {
            // if sourcesAreTargets, all the evaluation required has already
            // happened.
            return satisfied;
        }

        if (satisfied)
        {
            // If all the eligible sources and the eligible targets meet the
            // discount rule criteria, remove targets from the list of the
            // possible sources
            removeSelectedTargets(sources, targets);

            // evaluate the sources and the targets again dynamically.
            allConditionsSatisfied = sourceList.reevaluate(sources, targets, isEqualOrLesserValue())
                    && targetList.reevaluate(sources, targets, isEqualOrLesserValue());

            if (allConditionsSatisfied)
            {
                // availableSources and availableTargets after the following
                // steps would contain only the items which are unused in this
                // iteration.
                availableSources.removeAll(sources);
                availableSources.removeAll(targets);
                availableTargets.removeAll(targets);
                availableTargets.removeAll(sources);
            }
        }
        return allConditionsSatisfied;

    }

    /**
     * Returns true if both the source and target are set to "Class".
     * 
     * @return returns a boolean value
     */
    protected boolean areSourceAndTargetSetToMerchandiseClass()
    {
        boolean value = false;
        if (getTargetComparisonBasis() == COMPARISON_BASIS_MERCHANDISE_CLASS
                && getSourceComparisonBasis() == COMPARISON_BASIS_MERCHANDISE_CLASS)
        {
            value = true;
        }
        return value;
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
     * Adds a DiscountTargetIfc to the collection of targets for this rule.
     * 
     * @param source DiscountTargetIfc to add
     */
    /* package */void addTarget(DiscountTargetIfc target)
    {
        targets.add(target);
    }

    /**
     * Returns an array of DiscountTargetIfcs (objects that satisfy the target
     * criteria) for this rule.
     * 
     * @return DiscountTargetIfc[] containing targets
     * @see DiscountTargetIfc
     */
    public ArrayList<DiscountTargetIfc> getTargets()
    {
        return targets;
    }

    /**
     * Adds a source object to the collection of sources for this rule.
     * 
     * @param source DiscountSourceIfc to add
     */
    /* package */void addSource(DiscountSourceIfc source)
    {
        sources.add(source);
    }

    /**
     * This method finds the lowest priced target in the deal and calculates the
     * discount on the target only. This method assumes that the discount method
     * is percent; if it is not the discount amount will be 0.00.
     * 
     * @return DiscountRuleIfc implementation
     */
    public CurrencyIfc getDiscountAmountOnLowestPricedItem()
    {
        CurrencyIfc lowestPrice = getLowestTargetPrice(targets);
        CurrencyIfc discountAmount = DomainGateway.getBaseCurrencyInstance();
        if (lowestPrice != null)
        {
            if (getDiscountRate() != null)
            {
                discountAmount = lowestPrice.multiply(getDiscountRate());
            }
        }

        return discountAmount;
    }

    /**
     * This method this method determines if this target has the lowest price in
     * the target list. There maybe other targets that have the same price,
     * 
     * @param ArrayList of targets; BestDealGroup also uses this method,
     *            therefore the targets must be passed in as a parameter.
     * @return CurrencyIfc containing the lowewst price. If the target list is
     *         empty the object will be null.
     */
    public CurrencyIfc getLowestTargetPrice(ArrayList<DiscountTargetIfc> pTargets)
    {
        CurrencyIfc lowestPrice = null;
        for (Iterator<DiscountTargetIfc> t = pTargets.iterator(); t.hasNext();)
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)t.next();
            CurrencyIfc tempPrice = srli.getExtendedSellingPrice();

            if (lowestPrice == null || tempPrice.compareTo(lowestPrice) == CurrencyIfc.LESS_THAN)
            {
                lowestPrice = tempPrice;
            }
        }

        return lowestPrice;
    }

    /**
     * This method finds the highest priced target in the deal and calculates
     * the discount on the target only. This method assumes that the discount
     * method is percent; if it is not the discount amount will be 0.00.
     * 
     * @return DiscountRuleIfc implementation
     */
    public CurrencyIfc getDiscountAmountOnHighestPricedItem()
    {
        CurrencyIfc highestPrice = getHighestTargetPrice(targets);
        CurrencyIfc discountAmount = DomainGateway.getBaseCurrencyInstance();
        if (highestPrice != null)
        {
            if (getDiscountRate() != null)
            {
                discountAmount = highestPrice.multiply(getDiscountRate());
            }
        }

        return discountAmount;
    }

    /**
     * This method this method determines if this target has the highest price
     * in the target list. There maybe other targets that have the same price,
     * 
     * @param ArrayList of targets; BestDealGroup also uses this method,
     *            therefore the targets must be passed in as a parameter.
     * @return CurrencyIfc containing the highest price. If the target list is
     *         empty the object will be null.
     */
    public CurrencyIfc getHighestTargetPrice(ArrayList<DiscountTargetIfc> pTargets)
    {
        CurrencyIfc highestPrice = null;
        for (Iterator<DiscountTargetIfc> t = pTargets.iterator(); t.hasNext();)
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)t.next();
            CurrencyIfc tempPrice = srli.getExtendedSellingPrice();

            if (highestPrice == null || tempPrice.compareTo(highestPrice) == CurrencyIfc.GREATER_THAN)
            {
                highestPrice = tempPrice;
            }
        }

        return highestPrice;
    }

    /**
     * Get the PromotionId
     * 
     * @return
     */
    public int getPromotionId()
    {
        return promotionId;
    }

    /**
     * Set the PromotionId
     * 
     * @param promotionId
     */
    public void setPromotionId(int promotionId)
    {
        this.promotionId = promotionId;
    }

    /**
     * Get the Promotion Component Id
     * 
     * @return
     */
    public int getPromotionComponentId()
    {
        return promotionComponentId;
    }

    /**
     * Set the Promotion Component Id
     * 
     * @param promotionComponentId
     */
    public void setPromotionComponentId(int promotionComponentId)
    {
        this.promotionComponentId = promotionComponentId;
    }

    /**
     * Get the Promotion Component Detail Id
     * 
     * @return
     */
    public int getPromotionComponentDetailId()
    {
        return promotionComponentDetailId;
    }

    /**
     * Set the Promotion Component Detail Id
     * 
     * @param promotionComponentDetailId
     */
    public void setPromotionComponentDetailId(int promotionComponentDetailId)
    {
        this.promotionComponentDetailId = promotionComponentDetailId;
    }

    /**
     * Internal method to swap the {@link #sources} into the {@link #targets}
     * list.
     */
    private boolean convertSourcesToTargets()
    {
        for (DiscountSourceIfc source : sources)
        {
            DiscountTargetIfc target = (DiscountTargetIfc)source;
            if (target.isTargetEnabled())
            {
                targets.add(target);
            }
        }
        return !targets.isEmpty();
    }

    /**
     * Method to determine whether to evaluate sources or targets first, then
     * check if they are satisfied. Basically, the more specific comparison
     * basis (Items) should be evaluated before Classes or Departments.
     * 
     * @param sources
     * @param targets
     * @return true if both satisfied.
     */
    protected boolean evaluateSourceAndTargets(ArrayList<DiscountSourceIfc> sources,
            ArrayList<DiscountTargetIfc> targets)
    {
        if (isEqualOrLesserValue())
        {
            ArrayList allSources = new ArrayList();
            allSources.addAll(sources);
            boolean satisfied = false;
            satisfied = evaluateSourceAndTargetsForMerchandiseClassOrEOLV(allSources, targets);

            // For the Discount Rules of type BuyNofXgetYatZPctoff,
            // BuyNofXgetYatZ$, Buy$NorMoreOfXgetYatZPctoff, or
            // Buy$NorMoreOfXgetYatZ$, since the sources are sorted in ascending
            // order,
            // when sources are not targets, evaluate the left over sources also
            // if the first iteration
            // fails to get the right combination..
            if (description.equals(DISCOUNT_DESCRIPTION_BuyNofXgetYatZPctoff)
                    || description.equals(DISCOUNT_DESCRIPTION_BuyNofXgetYatZ$)
                    || description.equals(DISCOUNT_DESCRIPTION_Buy$NorMoreOfXgetYatZPctoff)
                    || description.equals(DISCOUNT_DESCRIPTION_Buy$NorMoreOfXgetYatZ$))
            {
                while (!sourcesAreTargets && !satisfied && (allSources.size() != 0 && this.sources.size() != 0))
                {
                    sourceList.resetQuantities();
                    // remove the selected sources and proceed with the next
                    // iteration.
                    allSources.removeAll(this.sources);
                    satisfied = evaluateSourceAndTargetsForMerchandiseClassOrEOLV(allSources, targets);
                    if (satisfied)
                    {
                        sources.removeAll(this.sources);
                        break;
                    }
                }
            }

            return satisfied;
        }
        else if (getTargetComparisonBasis() == DiscountRuleConstantsIfc.COMPARISON_BASIS_ITEM_ID
                && getSourceComparisonBasis() != DiscountRuleConstantsIfc.COMPARISON_BASIS_ITEM_ID)
        {
            boolean satisfied = targetsSatisfied(targets);
            if (satisfied)
            {
                removeSelectedTargets(sources);
                satisfied = sourcesSatisfied(sources);
            }
            return satisfied;
        }
        if (areSourceAndTargetSetToMerchandiseClass())
        {
            return evaluateSourceAndTargetsForMerchandiseClassOrEOLV(sources, targets);
        }
        return sourcesSatisfied(sources) && targetsSatisfied(targets);
    }

    /**
     * Retrieves the PricingGroupID.
     * 
     * @return pricingGroupID
     */
    public int getPricingGroupID()
    {
        return pricingGroupID;
    }

    /**
     * Sets the PricingGroupID
     * 
     * @param pricingGroupID
     */
    public void setPricingGroupID(int pricingGroupID)
    {
        this.pricingGroupID = pricingGroupID;
    }

    /**
     * Returns the boolean value indicating whether EqualOrLesserValue(EOLV)
     * should be used for Discount Rules.
     * 
     * @param pricingGroupID
     */
    public boolean isEqualOrLesserValue()
    {
        return equalOrLesserValue;
    }

    /**
     * Set the EqualOrLesserValue flag.
     * 
     * @param equalOrLesserValue
     */
    public void setEqualOrLesserValue(boolean equalOrLesserValue)
    {
        this.equalOrLesserValue = equalOrLesserValue;
    }
    
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc#evaluateSources(java.util.ArrayList, java.util.ArrayList)
     */
    public boolean evaluateSourcesForTransactionRules(ArrayList<DiscountSourceIfc> sources,
            ArrayList<DiscountTargetIfc> targets)
    {
        excludeRuleExclusions(sources, targets);
        excludeDiscountIneligibleItems(sources, targets);
        validateItemPrices(sources, targets);
        validateItemPriceCategories(sources, targets);
        if (description.equals(DISCOUNT_DESCRIPTION_Buy$NofXforZ$off)
                || description.equals(DISCOUNT_DESCRIPTION_Buy$NofXforZPctoff))
        {
            return evaluateSourcesForAmtBasedTransRules(sources, targets);
        }
        else if (description.equals(DISCOUNT_DESCRIPTION_BuyNofXforZ$off)
                || description.equals(DISCOUNT_DESCRIPTION_BuyNofXforZPctoff))
        {
            return evaluateSourcesForQtyBasedTransRules(sources, targets);
        }
        else
        {
            return true;
        }
    }
    
    /**
     * evaluates the sources for Quantity Based Transaction level discounts
     * 
     * @param sources
     * @param targets
     * @return
     */
    private boolean evaluateSourcesForQtyBasedTransRules(ArrayList<DiscountSourceIfc> possibleSources, ArrayList<DiscountTargetIfc> targets)
    {
        Iterator<String> criteriaIterator = sourceList.criteria();

        if (isStoreLevelDiscountRule())
        {
            while (criteriaIterator.hasNext())
            {

                int qty = 0;
                for (int i = 0; i < possibleSources.size(); i++)
                {
                    SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)possibleSources.get(i);
                    qty += srli.getItemQuantityDecimal().intValue();
                }
                if (qty >= sourceList.getQuantityRequired(criteriaIterator.next()))
                {
                    return true;
                }
                else
                {
                    return false;
                }

            }

        }

        sourceList.setDescription(getDescription());
        return sourceList.evaluate(possibleSources, new ArrayList());

    }
    
    /**
     * Evaluates the sources for AmountBased Transaction level discounts
     * 
     * @param sources
     * @param targets
     * @return
     */
    private boolean evaluateSourcesForAmtBasedTransRules(ArrayList<DiscountSourceIfc> possibleSources,
            ArrayList<DiscountTargetIfc> targets)
    {
        Iterator<String> criteriaIterator = sourceList.criteria();
        if (isStoreLevelDiscountRule())
        {
            while (criteriaIterator.hasNext())
            {

                CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance();
                for (int i = 0; i < possibleSources.size(); i++)
                {
                    SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)possibleSources.get(i);
                    amount = amount.add(srli.getExtendedSellingPrice());
                }
                if (amount.compareTo(sourceList.getAmountRequired(criteriaIterator.next())) == CurrencyIfc.GREATER_THAN)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        sourceList.setDescription(getDescription());
        return sourceList.evaluate(possibleSources, new ArrayList());

    }


    /**
     * Returns true, If it is store Level discount
     * @return
     */
    protected boolean isStoreLevelDiscountRule()
    {
        boolean storeLevel = false;

        if (sourceList.criteriaArray().length == 1 && sourceList.criteriaArray()[0].equals("*"))
        {
            storeLevel = true;
        }
        return storeLevel;
    }

    /**
     * Retrieves the SourceItemPriceCategory.
     * 
     * @return sourceItemPriceCategory
     */
    public String getSourceItemPriceCategory()
    {
        return sourceItemPriceCategory;
    }

    /**
     * Set the SourceItemPriceCategory.
     * 
     * @param sourceItemPriceCategory
     */
    public void setSourceItemPriceCategory(String sourceItemPriceCategory)
    {
        this.sourceItemPriceCategory = sourceItemPriceCategory;
    }

    /**
     * Retrieves the TargetItemPriceCategory.
     * 
     * @return targetItemPriceCategory
     */
    public String getTargetItemPriceCategory()
    {
        return targetItemPriceCategory;
    }

    /**
     * Set the TargetItemPriceCategory.
     * 
     * @param targetItemPriceCategory
     */
    public void setTargetItemPriceCategory(String targetItemPriceCategory)
    {
        this.targetItemPriceCategory = targetItemPriceCategory;
    }

    @Override
    public void setThresholdList(List<Threshold> thresholdList)
    {
        this.thresholdList = thresholdList;
        
    }

    @Override
    public List<Threshold> getThresholdList()
    {
        return thresholdList;
    }

    @Override
    public void setExcludedItems(List<String> excludedItems)
    {
        this.excludedItems = excludedItems;
        
    }

    @Override
    public List<String> getExcludedItems()
    {
        return excludedItems;
    }
    
    /**
     * clears the threshold list for the rule.
     */
    public void clearThresholdList()
    {
        thresholdList.clear();
    }
    
    /**
     * excludes the cancel items from sources and targets
     * @param sa
     * @param ta
     */
    protected void excludeRuleExclusions(ArrayList<DiscountSourceIfc> sa, ArrayList<DiscountTargetIfc> ta)
    {
        if (sa != null)
        {
            for (Iterator<DiscountSourceIfc> i = sa.iterator(); i.hasNext();)
            {
                DiscountSourceIfc source = i.next();
                boolean hasExclusionSource = this.getExcludedItems().contains(source.getItemID());
                if (hasExclusionSource)
                    i.remove();
            }
        }
        if (ta != null)
        {
            for (Iterator<DiscountTargetIfc> i = ta.iterator(); i.hasNext();)
            {
                DiscountTargetIfc target = i.next();
                boolean hasExclusionTarget = this.getExcludedItems().contains(target.getItemID());
                if (hasExclusionTarget)
                    i.remove();
            }
        }
    }
    
    /**
     * Removes the items from source and target list which are not elligible for
     * discounts
     * 
     * @param sa
     * @param ta
     */
    protected void excludeDiscountIneligibleItems(ArrayList<DiscountSourceIfc> sa, ArrayList<DiscountTargetIfc> ta)
    {
        if (sa != null)
        {
            for (Iterator<DiscountSourceIfc> i = sa.iterator(); i.hasNext();)
            {
                SaleReturnLineItem source = (SaleReturnLineItem)i.next();
                boolean discountEligible = source.getItemPrice().isDiscountEligible();
                if (!discountEligible)
                    i.remove();
            }
        }
        if (ta != null)
        {
            for (Iterator<DiscountTargetIfc> i = ta.iterator(); i.hasNext();)
            {
                SaleReturnLineItem target = (SaleReturnLineItem)i.next();
                boolean discountEligible = target.getItemPrice().isDiscountEligible();
                if (!discountEligible)
                    i.remove();
            }
        }

    }
}
