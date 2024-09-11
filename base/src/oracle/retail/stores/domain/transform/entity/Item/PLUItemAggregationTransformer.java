/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transform/entity/Item/PLUItemAggregationTransformer.java /main/15 2014/04/09 15:58:57 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   04/09/14 - check for null PLU requestor
 *    jswan     12/09/13 - Added JAVADOC.
 *    yiqzhao   09/05/13 - Get correct kit item price when a quantity of one
 *                         kit component is greater than one.
 *    yiqzhao   08/05/13 - Mapping JPA localized text of pricing rule to domain
 *                         advanced pricing rule.
 *    ohorne    06/03/13 - added transformAvailableItemSizes(..) method
 *    jswan     05/30/13 - Modified to make retrieval of Coupon Items uniform
 *                         when to source either the Store or CO database.
 *    tksharma  04/01/13 - discountrule list type fix for Xchannel
 *    abondala  01/27/13 - extending JPA
 *    abondala  01/10/13 - support extending jpa
 *    abondala  01/03/13 - refactored transformers
 *    tksharma  12/17/12 - Clearance Return Price and Clearance
 *                         DiscountAmount/DiscountPercent fix
 *    tksharma  10/25/12 - added method transformClearancePrices
 *    jswan     10/11/12 - Fixed issue with retrieving both Group and Deal
 *                         pricing rules on the same item.
 *    hyin      09/25/12 - additional CO PLUItem service work.
 *    hyin      09/19/12 - CO pluitem webservice work.
 *    jswan     09/17/12 - Added PLUItemAggregationTransformer to provide
 *                         simple interface for both Jpa PLU Operation and the
 *                         PLU Lookup through the web service.
 * ===========================================================================
 */
package oracle.retail.stores.domain.transform.entity.Item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocaleUtilities;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.PLURequestor;
import oracle.retail.stores.domain.arts.PLURequestor.RequestType;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.event.PriceChangeIfc;
import oracle.retail.stores.domain.stock.ItemClassificationConstantsIfc;
import oracle.retail.stores.domain.stock.ItemSizeIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.tax.TaxRuleIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.storeservices.entities.item.ItemSize;
import oracle.retail.stores.storeservices.entities.item.ItemSizeI18N;
import oracle.retail.stores.storeservices.entities.item.PLUItemAggregationIfc;
import oracle.retail.stores.storeservices.entities.item.POSIdentity;
import oracle.retail.stores.storeservices.entities.price.ClearanceItemPrice;
import oracle.retail.stores.storeservices.entities.price.CorePriceDerivationRule;
import oracle.retail.stores.storeservices.entities.price.DealPriceDerivationRule;
import oracle.retail.stores.storeservices.entities.price.GroupPriceDerivationRule;
import oracle.retail.stores.storeservices.entities.price.ItemPriceIfc;
import oracle.retail.stores.storeservices.entities.price.PermanentItemPrice;
import oracle.retail.stores.storeservices.entities.price.TemporaryItemPrice;
import oracle.retail.stores.storeservices.entities.tax.TaxRateRule;
import oracle.retail.stores.transform.TransformerIfc;

import org.apache.log4j.Logger;

/**
 * This class manages the transformation of a PLUItemAggregationIfc object into a 
 * PLUItemIfc Domain object.
 * @since 14.0
 */
public class PLUItemAggregationTransformer implements PLUItemAggregationTransformerIfc
{
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(PLUItemAggregationTransformer.class);

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transform.entity.Item.PLUItemAggregationTransformerIfc#transform(oracle.retail.stores.storeservices.entities.item.PLUItemAggregationIfc, oracle.retail.stores.domain.transaction.SearchCriteriaIfc, java.lang.String)
     */
    @Override
    public PLUItemIfc transform(PLUItemAggregationIfc pluItemAggregation, 
            SearchCriteriaIfc criteria, PLUItemAggregationIfc kitHeader)
    {
        Locale bestMatchingDefaultLocale = LocaleMap.getBestMatch(criteria.getLocaleRequestor().getDefaultLocale());

        PLUItemIfc pluItem = transformItem(pluItemAggregation.getPOSIdentity(), kitHeader, criteria, bestMatchingDefaultLocale);

        transformTaxRules(pluItemAggregation.getTaxRules(), pluItem);

        transformPermanentPrices(pluItemAggregation.getPermanentItemPrices(), pluItem);

        transformTemporaryPrices(pluItemAggregation.getTemporaryItemPrices(), pluItem);

        transformClearancePrices(pluItemAggregation.getClearanceItemPrices(), pluItem);

        transformPriceDerivationRules(pluItemAggregation.getDealPriceRules(), pluItemAggregation.getGroupPriceRules(), pluItem, criteria.getLocaleRequestor());

        // TODO: Fix correctly?
        transformAvailableItemSizes(pluItemAggregation.getAvailableItemSizes(), pluItem);

        if (pluItem.getItemClassification().getItemType() == ItemClassificationConstantsIfc.TYPE_STORE_COUPON)
        {
            setCouponPricingRuleValues(pluItem);
        }

        return pluItem;
    }

    /**
     * TODO: fix correctly?
     * This method transforms each member of a list of ItemSize entities into 
     * a domain ItemSizeIfc instance and adds it to a PLUItem.
     * @param sizes ItemSize entities in which this PluItem is available 
     * @param pluItem PLUItemIfc object 
     */
    protected void transformAvailableItemSizes(List<ItemSize> sizes, PLUItemIfc pluItem)
    {
        if (sizes != null && !sizes.isEmpty())
        {
            ItemSizeIfc[] itemSizeArray = new ItemSizeIfc[sizes.size()];
            for (int i = 0; i < itemSizeArray.length; i++)
            {
                ItemSize size  = sizes.get(i);

                itemSizeArray[i] = DomainGateway.getFactory().getItemSizeInstance();
                itemSizeArray[i].setIdentifier(size.getItemSizeID());
                itemSizeArray[i].setSizeCode(size.getDefaultTableCode());
                List<ItemSizeI18N> localizedDescriptions = size.getLocalizedDescriptions();
                for(ItemSizeI18N localizedDescription: localizedDescriptions)
                {
                    Locale locale = LocaleUtilities.getLocaleFromString(localizedDescription.getLocale());
                    itemSizeArray[i].setDescription(locale, localizedDescription.getTableDescription());
                    itemSizeArray[i].setName(locale, localizedDescription.getTableSize());
                }
                pluItem.addAvailableItemSize(itemSizeArray[i]);
            }
        }
    }
    /**
     * This method calls Spring to instantiate an implementation of the PluItemTransformerIfc 
     * interface and uses that class to transform the POSIdentity object into a PLUItemIfc object.
     * The configuration for this class is in the ServiceContext.xml file, and the bean ID is
     * "transformer_PluItemTransformer".  
     * @param posIdentity the POS Identity entity 
     * @param kitHeader the Kit Header object if the is posItem is a kit component; otherwise null.
     * @param bestMatchingDefaultLocale the default local. 
     * @return the domain PLUItemIfc object.
     */
    protected PLUItemIfc transformItem(POSIdentity posIdentity, PLUItemAggregationIfc kitHeader, SearchCriteriaIfc criteria,
            Locale bestMatchingDefaultLocale)
    {
        PluItemTransformerIfc  piTransformer = 
                (PluItemTransformerIfc)BeanLocator.getTransformerBean(TransformerIfc.TRANSF_PLU_ITEM_TRANSFORMER);

        PLUItemIfc pluItem = piTransformer.transform(posIdentity, kitHeader, bestMatchingDefaultLocale);
        
        if (criteria != null)
        {
            PLURequestor pluRequestor = criteria.getPLURequestor();
            if (pluRequestor != null && !pluRequestor.containsRequestType(RequestType.Planogram))
            {
                pluItem.setPosItemID(null);
            }
            if (pluRequestor != null && !pluRequestor.containsRequestType(RequestType.ItemImage))
            {
                pluItem.setItemImage(null);
            }
        }
        return pluItem;
    }

    /**
     * This method calls Spring to instantiate an implementation of the TaxRulesTransformerIfc interface and
     * uses that class to transform a list of TaxRateRule objects into a list of TaxRuleIfc 
     * objects.  It then sets the tax rules on the PLUItemIfc object.
     * The configuration for this class is in the ServiceContext.xml file, and the bean ID is
     * "transformer_TaxRulesTransformer".  
     * @param jpaTaxRules a List of Tax Rate Rule entities.
     * @param pluItem the domain PLUItemIfc object.
     */
    protected void transformTaxRules(List<TaxRateRule> jpaTaxRules, PLUItemIfc pluItem)
    {
        if ((jpaTaxRules != null) && (jpaTaxRules.size() > 0))
        {
            TaxRulesTransformerIfc trTransformer = 
                    (TaxRulesTransformerIfc)BeanLocator.getTransformerBean(TransformerIfc.TRANSF_TAX_RULES_TRANSFORMER);

            TaxRuleIfc[] taxRules = trTransformer.transform(jpaTaxRules);
            pluItem.setTaxRules(taxRules);
        }
    }
    
    /**
     * This method calls Spring to instantiate an implementation of the PriceChangeTransformerIfc interface and
     * uses that class to transform a list of PermanentItemPrice objects into a list of PriceChangeIfc 
     * objects.  It then sets the prices on the PLUItemIfc object.
     * The configuration for this class is in the ServiceContext.xml file, and the bean ID is
     * "transformer_PriceChangeTransformer".  
     * @param permPrices a list of Permanent Price entities. 
     * @param pluItem the domain PLUItemIfc object.
     */
    protected void transformPermanentPrices(List<PermanentItemPrice> permPrices, PLUItemIfc pluItem)
    {
        if (permPrices != null && permPrices.size() > 0)
        {
            List<ItemPriceIfc> prices = new ArrayList<ItemPriceIfc>();
            prices.addAll(permPrices);
            PriceChangeTransformerIfc ppcTransformer = 
                    (PriceChangeTransformerIfc)BeanLocator.getTransformerBean(TransformerIfc.TRANSF_PRICE_CHANGE_TRANSFORMER);
            PriceChangeIfc[] pChanges = ppcTransformer.transform(prices, pluItem);
            pluItem.setPermanentPriceChanges(pChanges);
        }
    }

    /**
     * This method calls Spring to instantiate an implementation of the PriceChangeTransformerIfc interface and
     * uses that class to transform a list of TemporaryItemPrice objects into a list of PriceChangeIfc 
     * objects.  It then sets the prices on the PLUItemIfc object.
     * The configuration for this class is in the ServiceContext.xml file, and the bean ID is
     * "transformer_PriceChangeTransformer".  
     * @param tempPrices a list of Temporary Price entities
     * @param pluItem the domain PLUItemIfc object.
     */
    protected void transformTemporaryPrices(List<TemporaryItemPrice> tempPrices, PLUItemIfc pluItem)
    {
        if (tempPrices != null && tempPrices.size() > 0)
        {
            List<ItemPriceIfc> prices = new ArrayList<ItemPriceIfc>();
            prices.addAll(tempPrices);
            PriceChangeTransformerIfc ppcTransformer = 
                    (PriceChangeTransformerIfc)BeanLocator.getTransformerBean(TransformerIfc.TRANSF_PRICE_CHANGE_TRANSFORMER);
            PriceChangeIfc[] tChanges = ppcTransformer.transform(prices, pluItem);
            pluItem.setTemporaryPriceChanges(tChanges);
            pluItem.setTemporaryPriceChangesAndTemporaryPriceChangesForReturns(tChanges);
        }
    }
    
    /**
     * This method calls Spring to instantiate an implementation if the PriceChangeTransformerIfc interface and
     * uses that class to transform a list of ClearanceItemPrice objects into a list of PriceChangeIfc
     * objects. It then sets the prices on the PLUItemIfc object.
     * The configuration for this class is in the ServiceContext.xml file, and the bean ID is
     * "transformer_PriceChangeTransformer".  
     * @param clrPrices a list of Clearance Price entities.
     * @param pluItem the domain PLUItemIfc object.
     */
    protected void transformClearancePrices(List<ClearanceItemPrice> clrPrices, PLUItemIfc pluItem)
    {
        if (clrPrices != null && clrPrices.size() >0)
        {
            List<ItemPriceIfc> prices = new ArrayList<ItemPriceIfc>();
            prices.addAll(clrPrices);
            PriceChangeTransformerIfc ppcTransformer = 
                    (PriceChangeTransformerIfc)BeanLocator.getTransformerBean(TransformerIfc.TRANSF_PRICE_CHANGE_TRANSFORMER);
            PriceChangeIfc[] cChanges = ppcTransformer.transform(prices, pluItem);
            pluItem.setClearancePriceChanges(cChanges);
            pluItem.setClearancePriceChangesAndClearancePriceChangesForReturns(cChanges);
        }
    }

    /**
     * This method calls Spring to instantiate an implementation of the PriceDerivationRuleTransformerIfc 
     * interface and uses that class to transform a list of CorePriceDerivationRule objects 
     * into a list of AdvancedPricingRuleIfc objects.  It then sets the pricing rules on the PLUItemIfc object.
     * The configuration for this class is in the ServiceContext.xml file, and the bean ID is
     * "transformer_PriceChangeTransformer".  
     * @param dealRules a List of Deal Rule entities
     * @param groupRules a List of Group Rule entities
     * @param pluItem the domain PLUItemIfc object.
     * @param localeReq the Locale Requestor from the search criteria
     */
    protected void transformPriceDerivationRules(List<DealPriceDerivationRule> dealRules, 
            List<GroupPriceDerivationRule> groupRules, PLUItemIfc pluItem, LocaleRequestor localeReq)
    {
        List<AdvancedPricingRuleIfc> ruleList = new ArrayList<AdvancedPricingRuleIfc>();
        if (dealRules != null && dealRules.size() > 0)
        {
            PriceDerivationRuleTransformerIfc pdrTransformer = 
                    (PriceDerivationRuleTransformerIfc)BeanLocator.getTransformerBean(TransformerIfc.TRANSF_PRICEDERIVATION_RULE_TRANSFORMER);
            @SuppressWarnings("unchecked")
            List<CorePriceDerivationRule> coreRules = (List<CorePriceDerivationRule>)(List<?>)dealRules;
            List<AdvancedPricingRuleIfc> rules = pdrTransformer.transform(coreRules, pluItem, localeReq);
            ruleList.addAll(rules);
        }

        if (groupRules != null && groupRules.size() > 0)
        {
            PriceDerivationRuleTransformerIfc pdrTransformer = 
                    (PriceDerivationRuleTransformerIfc)BeanLocator.getTransformerBean(TransformerIfc.TRANSF_PRICEDERIVATION_RULE_TRANSFORMER);
            @SuppressWarnings("unchecked")
            List<CorePriceDerivationRule> coreRules = (List<CorePriceDerivationRule>)(List<?>)groupRules;
            List<AdvancedPricingRuleIfc> rules = pdrTransformer.transform(coreRules, pluItem, localeReq);
            ruleList.addAll(rules);
        }

        AdvancedPricingRuleIfc[] ruleArray = new AdvancedPricingRuleIfc[ruleList.size()];
        ruleList.toArray(ruleArray);

        pluItem.setAdvancedPricingRules(ruleArray);
    }

    /**
     * This method iterates through the pricing rules, and makes needed adjustments
     * to transaction and coupon discount rules.
     * @param pluItem
     */
    protected void setCouponPricingRuleValues(PLUItemIfc pluItem)
    {
        AdvancedPricingRuleIfc advPricingRule = null;
        for (Iterator<AdvancedPricingRuleIfc> i = pluItem.advancedPricingRules(); i.hasNext();)
        {
            advPricingRule = i.next();

            if (advPricingRule.isScopeTransaction())
            {
                advPricingRule.activateTransactionDiscount();
            }
            // Set the ReferenceID and Code for this rule
            advPricingRule.setReferenceID(pluItem.getItemID());
            advPricingRule.setReferenceIDCode(DiscountRuleConstantsIfc.REFERENCE_ID_CODE_STORE_COUPON);
        }
    }
}
