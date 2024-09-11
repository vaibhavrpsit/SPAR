/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transform/entity/Item/PriceDerivationRuleTransformer.java /main/17 2014/01/24 16:58:49 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  01/24/14 - fix null dereferences
 *    jswan     12/13/13 - Upated JAVADOC.
 *    yiqzhao   08/05/13 - Mapping JPA localized text of pricing rule to domain
 *                         advanced pricing rule.
 *    rabhawsa  07/22/13 - execlude items based on the effective start date
 *                         time.
 *    tksharma  04/01/13 - discountrule list type fix for Xchannel
 *    jswan     03/19/13 - Modified to support many to one relationship between
 *                         RU_PRVD table and TR_ITM_MXMH_PRDV table.
 *    abondala  01/27/13 - extending JPA
 *    abondala  01/10/13 - support extending jpa
 *    cgreene   12/11/12 - allow sale renderer to show item's promotion name
 *    tksharma  10/17/12 - added call for transformNonEligibleItems to
 *                         dealDiscountRules
 *    jswan     10/11/12 - Fixed issue with retrieving both Group and Deal
 *                         pricing rules on the same item.
 *    jswan     09/17/12 - Made changes due to code review.
 *    tksharma  08/31/12 - Merged code with jswan's work
 *    jswan     08/30/12 - Result of merge with repository.
 *    tksharma  08/29/12 - CR 188 - JPA changes for non eligible Items
 *    jswan     08/29/12 - Modified to support coupon pricing rules.
 *    tksharma  08/17/12 - merge before promote
 *    tksharma  08/07/12 - jpa changes for multithreshold
 *    jswan     07/20/12 - Added to transform JPA Item Entitities into a
 *                         PLUItemIfc.
 * ===========================================================================
 */
package oracle.retail.stores.domain.transform.entity.Item;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocaleUtilities;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.Threshold;
import oracle.retail.stores.domain.stock.ItemClassificationConstantsIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transform.entity.TransformerUtilities;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.EYSTime;
import oracle.retail.stores.storeservices.entities.price.CorePriceDerivationRule;
import oracle.retail.stores.storeservices.entities.price.DealPriceDerivationRule;
import oracle.retail.stores.storeservices.entities.price.DepartmentBasedEligibility;
import oracle.retail.stores.storeservices.entities.price.GroupPriceDerivationRule;
import oracle.retail.stores.storeservices.entities.price.ItemBasedEligibility;
import oracle.retail.stores.storeservices.entities.price.ItemBasedNonEligibility;
import oracle.retail.stores.storeservices.entities.price.MerchandiseClassBasedEligibility;
import oracle.retail.stores.storeservices.entities.price.MixMatchDerivationRule;
import oracle.retail.stores.storeservices.entities.price.PricingRuleI18N;
import oracle.retail.stores.storeservices.entities.price.ThresholdBasedEligibility;

import org.apache.log4j.Logger;

/**
 * This class transforms a list of Price Derivation Rule Entity objects into a list of 
 * Domain Advanced Pricing objects.
 * @since 14.0
 */
public class PriceDerivationRuleTransformer implements PriceDerivationRuleTransformerIfc
{
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(PriceDerivationRuleTransformer.class);

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transform.entity.Item.PriceDerivationRuleTransformerIfc#Transform(java.util.List, oracle.retail.stores.domain.stock.PLUItemIfc)
     */
    @Override
    public List <AdvancedPricingRuleIfc> transform(List<CorePriceDerivationRule> priceDerivationRules, 
            PLUItemIfc pluItem, LocaleRequestor localeReq)
    {
        List<AdvancedPricingRuleIfc> pricingRules = new ArrayList<AdvancedPricingRuleIfc>();
        String lastRuleID = "";
        AdvancedPricingRuleIfc rule = null;
        for (CorePriceDerivationRule priceDerivationRule: priceDerivationRules)
        {
            if (priceDerivationRule instanceof DealPriceDerivationRule)
            {
                DealPriceDerivationRule dealRule = (DealPriceDerivationRule)priceDerivationRule;
                Collection<MixMatchDerivationRule> mmRules = dealRule.getMixMatchDerivationRules();
                for(MixMatchDerivationRule mmRule: mmRules)
                {
                    if (!dealRule.getPricingRuleID().getRuleID().equals(lastRuleID))
                    {
                        rule = DomainGateway.getFactory().getAdvancedPricingRuleInstance();
                        transformCoreRule(dealRule, rule, pluItem, localeReq);
                        transformDealRule(dealRule, rule, mmRule);
                        pricingRules.add(rule);
                        lastRuleID = dealRule.getPricingRuleID().getRuleID();
                    }
                    if (rule != null)
                    {
                        rule.getTargetList().addEntry(mmRule.getPromotionalID(), 
                                TransformerUtilities.getSafeInt(mmRule.getTargetThresholdQuantity(), 0));
                    }
                }
            }
            else
            if (priceDerivationRule instanceof GroupPriceDerivationRule)
            {
                GroupPriceDerivationRule groupRule = (GroupPriceDerivationRule)priceDerivationRule;
                rule = DomainGateway.getFactory().getAdvancedPricingRuleInstance();
                transformCoreRule(groupRule, rule, pluItem, localeReq);
                transformGroupRule(groupRule, rule);
                pricingRules.add(rule);
            }
        }
        
        return pricingRules;
    }
    
    /**
     * This method updates the rule parameter (oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc) with 
     * information from dealRule ({@link DealPriceDerivationRule}), and mmRule ({@link MixMatchDerivationRule}) entities. 
     * @param dealRule a DealPriceDerivationRule entity
     * @param rule an AdvancedPricingRule domain object
     * @param mmRule a MixMatchDerivationRuleIfc entity
     */
    protected void transformDealRule(DealPriceDerivationRule dealRule, AdvancedPricingRuleIfc rule, MixMatchDerivationRule mmRule)
    {
        rule.setRuleID(dealRule.getPricingRuleID().getRuleID());
        rule.setStoreID(dealRule.getPricingRuleID().getStoreID());
        rule.setDiscountRate(TransformerUtilities.getPercentage(mmRule.getPriceReductionPercent()));
        rule.setSourcesAreTargets(false);
        rule.setTargetComparisonBasis(TransformerUtilities.getSafeInt(mmRule.getComparisonBasisCode(), 0));

        CurrencyIfc discountAmount = TransformerUtilities.getCurrencyFromDecimal(mmRule.getPriceReductionAmount());
        CurrencyIfc fixedPrice = TransformerUtilities.getCurrencyFromDecimal(mmRule.getPricePricePoint());
        if (fixedPrice.signum() != 0)
        {
            rule.setFixedPrice(fixedPrice);
        }
        else
        {
            rule.setDiscountAmount(discountAmount);
        }
        
        if (dealRule.getEligibleItems() != null && !dealRule.getEligibleItems().isEmpty())
        {
            transformEligibleItems(dealRule.getEligibleItems(), rule);
        }
        if (dealRule.getEligibleDepartments() != null && !dealRule.getEligibleDepartments().isEmpty())
        {
            transformEligibleDepartments(dealRule.getEligibleDepartments(), rule);
        }
        if (dealRule.getEligibleMerchandiseClasses() != null && !dealRule.getEligibleMerchandiseClasses().isEmpty())
        {
            transformEligibleMerchandiseClasses(dealRule.getEligibleMerchandiseClasses(), rule);
        }
        if (dealRule.getNonEligibileItems() != null && !dealRule.getNonEligibileItems().isEmpty())
        {
            transformNonEligibleItems(dealRule.getNonEligibileItems(), rule);
        }
    }
    
    /**
     * This method updates the rule parameter (oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc) with 
     * information from groupRule ({@link GroupPriceDerivationRule}) entity. 
     * @param groupRule a GroupPriceDerivationRule entity
     * @param rule a AdvancedPricingRuleIfc domain object.
     */
    protected void transformGroupRule(GroupPriceDerivationRule groupRule, AdvancedPricingRuleIfc rule)
    {
        rule.setRuleID(groupRule.getPricingRuleID().getRuleID());
        rule.setStoreID(groupRule.getPricingRuleID().getStoreID());
        rule.setDiscountRate(TransformerUtilities.getPercentage(groupRule.getPriceReductionPercent()));
        rule.setSourcesAreTargets(true);

        CurrencyIfc discountAmount = TransformerUtilities.getCurrencyFromDecimal(groupRule.getPriceReductionAmount());
        CurrencyIfc fixedPrice = TransformerUtilities.getCurrencyFromDecimal(groupRule.getPricePricePoint());
        if (fixedPrice.signum() != 0)
        {
            rule.setFixedPrice(fixedPrice);
        }
        else
        {
            rule.setDiscountAmount(discountAmount);
        }
        
        if (groupRule.getEligibleItems() != null && !groupRule.getEligibleItems().isEmpty())
        {
            transformEligibleItems(groupRule.getEligibleItems(), rule);
        }
        if (groupRule.getEligibleDepartments() != null && !groupRule.getEligibleDepartments().isEmpty())
        {
            transformEligibleDepartments(groupRule.getEligibleDepartments(), rule);
        }
        if (groupRule.getEligibleMerchandiseClasses() != null && !groupRule.getEligibleMerchandiseClasses().isEmpty())
        {
            transformEligibleMerchandiseClasses(groupRule.getEligibleMerchandiseClasses(), rule);
        }
        if (groupRule.getThresholdEligibilities() != null && !groupRule.getThresholdEligibilities().isEmpty())
        {
            transformThresholdEligibilities(groupRule.getThresholdEligibilities(), rule);
        }
        if (groupRule.getNonEligibileItems() != null && !groupRule.getNonEligibileItems().isEmpty())
        {
            transformNonEligibleItems(groupRule.getNonEligibileItems(), rule);
        }
    }

    /**
     * All deal and group pricing rules are AdvancedPricingRule domain objects; all deal and group entities are
     * CorePriceDerivationRule entities.  This method updates the rule parameter 
     * (oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc) with the common information from the  
     * coreRule ({@link CorePriceDerivationRule}) entity.  
     * and the DealPriceDerivationRule on the AdvancedPricingRuleIfc object
     * @param coreRule a CorePriceDerivationRule entity
     * @param rule an AdvancedPricingRuleIfc domain object
     * @param pluItem a PLUItmeIfc domain object
     * @param localeReq the locale requestor use to retrieve the item and pricing information
     */
    protected void transformCoreRule(CorePriceDerivationRule coreRule, AdvancedPricingRuleIfc rule, PLUItemIfc pluItem, LocaleRequestor localeReq)
    {
        rule.setDiscountScope(TransformerUtilities.getSafeInt(coreRule.getScopeCode(), 0));
        rule.setDiscountMethod(TransformerUtilities.getSafeInt(coreRule.getMethodCode(), 0));
        rule.setAssignmentBasis(TransformerUtilities.getSafeInt(coreRule.getAssignmentBasis(), 0));
        rule.setApplicationLimit(TransformerUtilities.getSafeInt(coreRule.getApplicationLimit(), 0));
        rule.setSourceItemPriceCategory(TransformerUtilities.getSafeString(coreRule.getSourceItemCategory()));
        rule.setTargetItemPriceCategory(TransformerUtilities.getSafeString(coreRule.getTagetItemPriceCategory()));
        rule.setSourceThreshold(TransformerUtilities.getCurrencyFromDecimal(coreRule.getSourceThresholdAmount()));
        rule.setSourceLimit(TransformerUtilities.getCurrencyFromDecimal(coreRule.getSourceLimitAmount()));
        rule.setTargetThreshold(TransformerUtilities.getCurrencyFromDecimal(coreRule.getTargetThresholdAmount()));
        rule.setTargetLimit(TransformerUtilities.getCurrencyFromDecimal(coreRule.getTargetLimitAmount()));
        rule.setSourceAnyQuantity(TransformerUtilities.getSafeInt(coreRule.getSourceAnyQuantity(), 0));
        rule.setTargetAnyQuantity(TransformerUtilities.getSafeInt(coreRule.getTargetAnyQuantity(), 0));
        rule.setThresholdTypeCode(TransformerUtilities.getSafeInt(coreRule.getThresholdTypeCode(), 
                DiscountRuleConstantsIfc.THRESHOLD_QUANTITY));
        rule.setAccountingMethod(TransformerUtilities.getSafeInt(coreRule.getStockLedgerDispositionCode(), 0));
        rule.setDealDistribution(coreRule.isDealDistribution());
        rule.setAllowRepeatingSources(coreRule.isAllowRepeatingSources());
        rule.setCalcDiscOnItemType(coreRule.getDiscountType());
        rule.setPromotionId(TransformerUtilities.getSafeInt(coreRule.getPromotionID(), 0));
        rule.setPromotionComponentId(TransformerUtilities.getSafeInt(coreRule.getPromotionComponentID(), 0));
        rule.setPromotionComponentDetailId(TransformerUtilities.getSafeInt(coreRule.getPromotionComponentDetailID(), 0));
        rule.setEffectiveDate(TransformerUtilities.getEYSDate(coreRule.getEffectiveDate(), null));
        rule.setEffectiveTime(new EYSTime(coreRule.getEffectiveDate()));
        rule.setExpirationDate(TransformerUtilities.getEYSDate(coreRule.getExpirationDate(), null));
        rule.setEffectiveTime(new EYSTime(coreRule.getExpirationDate()));
        rule.setPricingGroupID(TransformerUtilities.getSafeInt(coreRule.getCustomerPricingGroupID(), 0));

        // set applied when value
        if (coreRule.getControlBreakCode().equals("DT"))
        {
            rule.setAppliedWhen(DiscountRuleConstantsIfc.APPLIED_DETAIL);
        }
        else if (coreRule.getControlBreakCode().equals("MT"))
        {
            rule.setAppliedWhen(DiscountRuleConstantsIfc.APPLIED_MERCHANDISE_SUBTOTAL);
        }
        else
        {
            rule.setAppliedWhen(DiscountRuleConstantsIfc.APPLIED_UNDEFINED);
        }

        rule.setStatus(DiscountRuleConstantsIfc.STATUS_PENDING);
        for (int i = 0; i < DiscountRuleConstantsIfc.STATUS_DESCRIPTORS.length; i++)
        {
            if (DiscountRuleConstantsIfc.STATUS_DESCRIPTORS[i].equals(coreRule.getStatus()))
            {
                rule.setStatus(i);
                i = DiscountRuleConstantsIfc.STATUS_DESCRIPTORS.length;
            }
        }

        rule.setIncludedInBestDeal(coreRule.isIncludeInBestDeal());
        
        rule.setDescription(coreRule.getDescription());
        if (coreRule.getLocaleDescriptions() != null)
        {
            for (PricingRuleI18N desc : coreRule.getLocaleDescriptions())
            {
                Locale locale = LocaleUtilities.getLocaleFromString(desc.getPricingRuleI18NID().getLocale());
                rule.setName(locale, desc.getDescription());
            }
        }
        String reasonCodeString = CodeConstantsIfc.CODE_UNDEFINED;
        if (coreRule.getReasonCode() != null)
        {
            reasonCodeString = coreRule.getReasonCode().toString();
        }
        LocalizedCodeIfc reason = DomainGateway.getFactory().getLocalizedCode();
        reason.setCode(reasonCodeString);
        reason.setText(coreRule.getLocaleDescription(localeReq));
        rule.setReason(reason);
        
        if (pluItem.getItemClassification().getItemType() == 
            ItemClassificationConstantsIfc.TYPE_STORE_COUPON)
        {
            if (rule.isScopeTransaction())
            {
                rule.activateTransactionDiscount();
            }
            // Set the ReferenceID and Code for this rule
            rule.setReferenceID(pluItem.getItemID());
            rule.setReferenceIDCode(DiscountRuleConstantsIfc.REFERENCE_ID_CODE_STORE_COUPON);
        }
    }

    /**
     * This method iterates through the list of {@link ItemBasedEligibility} entities and for each
     * adds an entry to the rule (oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc) 
     * source list.  Each source has a quantity or an amount threshold.
     * <p>
     * A rule that has a list of ItemBasedEligibility entities, has item ID source comparison basis.
     * @param eligibleItems a list of ItemBasedEligibility entities
     * @param rule a AdvancedPricingRuleIfc domain object.
     */
    protected void transformEligibleItems(Collection<ItemBasedEligibility> eligibleItems,
            AdvancedPricingRuleIfc rule)
    {
        rule.setSourceComparisonBasis(DiscountRuleConstantsIfc.COMPARISON_BASIS_ITEM_ID);
        for (ItemBasedEligibility eligibleItem: eligibleItems)
        {
            if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_QUANTITY)
            {
                rule.getSourceList().addEntry(eligibleItem.getItemBasedEligibilityID().getItemID(), 
                    TransformerUtilities.getSafeInt(eligibleItem.getThresholdQuantity(), 1));
            }
            else
            if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_AMOUNT)
            {
                rule.getSourceList().addEntry(eligibleItem.getItemBasedEligibilityID().getItemID(), 
                        TransformerUtilities.getCurrencyFromDecimal(eligibleItem.getThresholdAmount()));
            }
        }
    }

    /**
     * This method iterates through the list of {@link DepartmentBasedEligibility} entities and for each
     * adds an entry to the rule (oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc) 
     * source list.  Each source has a quantity or an amount threshold.
     * <p>
     * A rule that has a list of DepartmentBasedEligibility entities, has department ID source comparison basis.
     * @param eligibleDepartments a list of DepartmentBasedEligibility entities
     * @param rule a AdvancedPricingRuleIfc domain object.
     */
    protected void transformEligibleDepartments(Collection<DepartmentBasedEligibility> eligibleDepartments,
            AdvancedPricingRuleIfc rule)
    {
        rule.setSourceComparisonBasis(DiscountRuleConstantsIfc.COMPARISON_BASIS_DEPARTMENT_ID);
        for (DepartmentBasedEligibility eligibleDepartment: eligibleDepartments)
        {
            if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_QUANTITY)
            {
                rule.getSourceList().addEntry(eligibleDepartment.getDepartmentBasedEligibilityID().getDepartmentID(), 
                        TransformerUtilities.getSafeInt(eligibleDepartment.getThresholdQuantity(), 1));
            }
            else
            if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_AMOUNT)
            {
                rule.getSourceList().addEntry(eligibleDepartment.getDepartmentBasedEligibilityID().getDepartmentID(), 
                        TransformerUtilities.getCurrencyFromDecimal(eligibleDepartment.getThresholdAmount()));
            }
        }
    }
    
    /**
     * This method iterates through the list of {@link MerchandiseClassBasedEligibility} entities and for each
     * adds an entry to the rule (oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc) 
     * source list.  Each source has a quantity or an amount threshold.
     * <p>
     * A rule that has a list of MerchandiseClassBasedEligibility entities, has merchandise classification ID source comparison basis.
     * @param eligibleMerchandiseClasses a list of MerchandiseClassBasedEligibility entities
     * @param rule a AdvancedPricingRuleIfc domain object.
     */
    protected void transformEligibleMerchandiseClasses(Collection<MerchandiseClassBasedEligibility> eligibleMerchandiseClasses,
            AdvancedPricingRuleIfc rule)
    {
        rule.setSourceComparisonBasis(DiscountRuleConstantsIfc.COMPARISON_BASIS_MERCHANDISE_CLASS);
        for (MerchandiseClassBasedEligibility eligibleMerchandiseClass: eligibleMerchandiseClasses)
        {
            if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_QUANTITY)
            {
                rule.getSourceList().addEntry(eligibleMerchandiseClass.getMerchandiseClassBasedEligibilityID().getMerchandiseClassCode(), 
                    TransformerUtilities.getSafeInt(eligibleMerchandiseClass.getThresholdQuantity(), 1));
            }
            if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_AMOUNT)
            {
                rule.getSourceList().addEntry(eligibleMerchandiseClass.getMerchandiseClassBasedEligibilityID().getMerchandiseClassCode(), 
                        TransformerUtilities.getCurrencyFromDecimal(eligibleMerchandiseClass.getThresholdAmount()));
            }
        }
    }
    
    /**
     * This method iterates through the list of {@link ThresholdBasedEligibility} entities and for each
     * adds an entry to the rule (oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc) 
     * threshold list.
     * @param thresholdEligibilities a list of ThresholdBasedEligibility entities
     * @param rule a AdvancedPricingRuleIfc domain object.
     */
    protected void transformThresholdEligibilities(Collection<ThresholdBasedEligibility> thresholdEligibilities,
            AdvancedPricingRuleIfc rule)
    {
        for (ThresholdBasedEligibility thresholdEligibility: thresholdEligibilities)
        {
            Threshold threshold = new Threshold();
            threshold.setThresholdID(thresholdEligibility.getThresholdBasedEligibilityID().getThresholdID());
            threshold.setThresholdVal(Integer.parseInt(thresholdEligibility.getThresholdValue()));
            threshold.setDiscountAmount(thresholdEligibility.getPriceReductionAmount());
            threshold.setDiscountPercent(thresholdEligibility.getPriceReductionPercent());
            threshold.setNewPrice(thresholdEligibility.getPricePricePoint());
            
            rule.getThresholdList().add(threshold);
            
        }
        
    }

    /**
     * This method iterates through the list of {@link ItemBasedNonEligibility} entities and for each
     * adds an entry to the rule (oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc) 
     * exclude item list.
     * @param nonEligibleItems a list of ItemBasedNonEligibility entities
     * @param rule a AdvancedPricingRuleIfc domain object.
     */
    protected void transformNonEligibleItems(Collection<ItemBasedNonEligibility> nonEligibleItems,
            AdvancedPricingRuleIfc rule)
    {
        List<String> excludedItemList = new ArrayList<String>();
        Timestamp now = new Timestamp(new java.util.Date().getTime());
        for (ItemBasedNonEligibility nonEligibleItem : nonEligibleItems)
        {
            if (nonEligibleItem.getEffectiveDate().compareTo(now) <= 0)
            {
                excludedItemList.add(nonEligibleItem.getItemBasedNonEligibilityID().getItemID());
            }
        }

        rule.setExcludedItems(excludedItemList);
    }
}
