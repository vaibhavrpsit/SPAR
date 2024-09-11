/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transform/entity/customer/CustomerGroupTransformer.java /main/14 2014/02/03 19:30:30 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  01/31/14 - added null pointer check for customerGroup
 *    rabhawsa  01/31/14 - null check for the groupid
 *    mkutiana  12/09/13 - Added Javadocs
 *    abondala  01/27/13 - extending JPA
 *    abondala  01/10/13 - support extending jpa
 *    abondala  01/03/13 - refactored transformers
 *    abondala  08/17/12 - fixing the exisitng customer updates and few other
 *                         issues
 *    abondala  08/09/12 - customer jpa related changes
 *    abondala  08/08/12 - updted related to discount rules
 *    acadar    08/01/12 - changes for JPA
 *    acadar    08/01/12 - new transformer
 * ===========================================================================
 */
package oracle.retail.stores.domain.transform.entity.customer;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.transform.entity.TransformerUtilities;
import oracle.retail.stores.domain.utility.EYSTime;
import oracle.retail.stores.storeservices.entities.customer.CustomerAffiliation;
import oracle.retail.stores.storeservices.entities.customer.CustomerAffiliationPriceDerRuleEligibility;
import oracle.retail.stores.storeservices.entities.customer.CustomerGroup;
import oracle.retail.stores.storeservices.entities.price.GroupPriceDerivationRule;
import oracle.retail.stores.storeservices.entities.price.TimeDatePriceDerivationRuleEligibility;
import oracle.retail.stores.transform.TransformerIfc;

import org.apache.log4j.Logger;


/**
 * The CustomerGroupTransformer is a utility class for converting between the 
 * set of Domain objects oracle.retail.stores.domain.customer.CustomerGroupIfc and oracle.retail.stores.domain.discount.DiscountRuleIfc
 * and Customer Entity objects(JPA) objects {@link oracle.retail.stores.storeservices.entities.customer.CustomerAffiliation}, 
 * {@link oracle.retail.stores.storeservices.entities.customer.CustomerAffiliationPriceDerRuleEligibility}, 
 * <p>
 * The CustomerGroupTransformer is a spring loaded bean defined in TransformerContext.xml
 * @since 14.0
 */
public class CustomerGroupTransformer implements CustomerGroupTransformerIfc, TransformerIfc
{

    /**
     * Static Logger for this class
     */
    protected static Logger logger = Logger.getLogger(CustomerGroupTransformer.class);

    /** Static variable to indicate Customers with Active Status */
    public static String CUSTOMER_STATUS_ACTIVE = "1";

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transform.entity.customer.CustomerGroupTransformerIfc#transform
     *             (oracle.retail.stores.storeservices.entities.customer.CustomerAffiliation, oracle.retail.stores.common.utility.LocaleRequestor)
     */
    public CustomerGroupIfc transform(CustomerAffiliation cAffil, LocaleRequestor localeReq)
    {
        CustomerGroupIfc cGroup = DomainGateway.getFactory().getCustomerGroupInstance();
        if (cAffil != null && cAffil.getCustomerGroup() != null)
        {
            cGroup.setGroupID(Integer.toString(cAffil.getCustomerGroup().getCustomerGroupID()));
            cGroup.setLocalizedNames(cAffil.getCustomerGroup().getCustomerGroupName(localeReq));
            cGroup.setLocalizedDescriptions(cAffil.getCustomerGroup().getCustomerGroupDescription(localeReq));
        }
        

        List<CustomerAffiliationPriceDerRuleEligibility> cfpdreList =    cAffil.getCustomerDiscountRuleEligibility();
        DiscountRuleIfc[] discountRules = transform(cfpdreList, localeReq);
        cGroup.setDiscountRules(discountRules);

        return cGroup;
    }
    

   /*
    * (non-Javadoc)
    * @see oracle.retail.stores.domain.transform.entity.customer.CustomerGroupTransformerIfc#transform
    *               (oracle.retail.stores.storeservices.entities.customer.CustomerGroup, oracle.retail.stores.common.utility.LocaleRequestor)
    */
   public CustomerGroupIfc transform(CustomerGroup customerGroup, LocaleRequestor localeReq)
   {
       CustomerGroupIfc cGroup = DomainGateway.getFactory().getCustomerGroupInstance();

       cGroup.setGroupID(Integer.toString(customerGroup.getCustomerGroupID()));
       cGroup.setLocalizedNames(customerGroup.getCustomerGroupName(localeReq));
          cGroup.setLocalizedDescriptions(customerGroup.getCustomerGroupDescription(localeReq));

       return cGroup;
   }    
    

   /*
    * (non-Javadoc)
    * @see oracle.retail.stores.domain.transform.entity.customer.CustomerGroupTransformerIfc#transform
    *                           (java.util.List, oracle.retail.stores.common.utility.LocaleRequestor)
    */
   public DiscountRuleIfc[] transform(List<CustomerAffiliationPriceDerRuleEligibility> cfpdreList, LocaleRequestor localeReq)
   {
       ArrayList <DiscountRuleIfc> discountRulesList = new ArrayList<DiscountRuleIfc>(); 
       for(CustomerAffiliationPriceDerRuleEligibility cfpdre: cfpdreList)
       {
           GroupPriceDerivationRule pdRuleEntity = cfpdre.getGroupPriceDerivationRule();
           if(pdRuleEntity != null)
           {
               List<TimeDatePriceDerivationRuleEligibility> trRuleEntities = pdRuleEntity.getTimeDatePriceDerivationRuleEligibility();
    
               for(TimeDatePriceDerivationRuleEligibility  trRuleEntity: trRuleEntities)
               {
                   DiscountRuleIfc rule = DomainGateway.getFactory().getDiscountRuleInstance();
                   rule.setEffectiveDate(TransformerUtilities.timestampToEYSDate(trRuleEntity.getTimeDatePriceDerivationRuleEligibilityID().getEffectiveDate()));
                   rule.setEffectiveTime(new EYSTime(rule.getEffectiveDate()));
                   rule.setExpirationDate(TransformerUtilities.timestampToEYSDate(trRuleEntity.getExpirationDate()));
                   rule.setExpirationTime(new EYSTime(rule.getExpirationDate()));
        
                   rule.setLocalizedNames(pdRuleEntity.getLocaleDescription(localeReq));
        
                   String appliedWhen = TransformerUtilities.getSafeString(pdRuleEntity.getControlBreakCode());
                   boolean includedInBestDealFlag = pdRuleEntity.isIncludeInBestDeal();
        
                   rule.setDescription(pdRuleEntity.getDescription());
                   rule.setRuleID(pdRuleEntity.getPricingRuleID().getRuleID());
    
                   // set status
                   String status = TransformerUtilities.getSafeString(pdRuleEntity.getStatus());
    
                   rule.setDiscountScope(DiscountRuleConstantsIfc.DISCOUNT_SCOPE_TRANSACTION);
                   rule.setDiscountMethod(pdRuleEntity.getMethodCode());
                   rule.setDiscountAmount(TransformerUtilities.getCurrencyFromDecimal(pdRuleEntity.getPriceReductionAmount()));
                   rule.setDiscountRate(TransformerUtilities.getPercentage(pdRuleEntity.getPriceReductionPercent()));
                   rule.setAssignmentBasis(DiscountRuleConstantsIfc.ASSIGNMENT_CUSTOMER);
    
                   setDiscountRuleValues(rule, appliedWhen, status, includedInBestDealFlag);
    
                   LocalizedCodeIfc reasonCode = DomainGateway.getFactory().getLocalizedCode();
                   reasonCode.setCode(String.valueOf(cfpdre.getCustomerAffiliationPriceDerRuleEligibilityID().getCustomerGroupID()));
                   reasonCode.setText(pdRuleEntity.getLocaleDescription(localeReq));
                   rule.setReason(reasonCode);
                   rule.getReason().setCode(Integer.toString(pdRuleEntity.getReasonCode()));
                   
                   discountRulesList.add(rule);
               }
           }
       }
       
       DiscountRuleIfc[]  discountRulesArray = new DiscountRuleIfc[discountRulesList.size()];
       discountRulesList.toArray(discountRulesArray);

       return discountRulesArray;
   }
    

    private void setDiscountRuleValues(DiscountRuleIfc rule, String appliedWhen, String status,
            boolean includedInBestDealFlag)
    {
        // set applied when value
        if (appliedWhen.equals("DT"))
        {
            rule.setAppliedWhen(DiscountRuleConstantsIfc.APPLIED_DETAIL);
        }
        else if (appliedWhen.equals("MT"))
        {
            rule.setAppliedWhen(DiscountRuleConstantsIfc.APPLIED_MERCHANDISE_SUBTOTAL);
        }
        else
        {
            rule.setAppliedWhen(DiscountRuleConstantsIfc.APPLIED_UNDEFINED);
        }

        // set status
        rule.setStatus(DiscountRuleConstantsIfc.STATUS_PENDING);
        for (int i = 0; i < DiscountRuleConstantsIfc.STATUS_DESCRIPTORS.length; i++)
        {
            if (status.equals(DiscountRuleConstantsIfc.STATUS_DESCRIPTORS[i]))
            {
                rule.setStatus(i);
                i = DiscountRuleConstantsIfc.STATUS_DESCRIPTORS.length;
            }
        }

        if (includedInBestDealFlag)
        {
            rule.setIncludedInBestDeal(true);
        }
        else
        {
            rule.setIncludedInBestDeal(false);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transform.entity.customer.CustomerGroupTransformerIfc#transform
     *                                       (oracle.retail.stores.domain.customer.CustomerGroupIfc, java.lang.String)
     */
    public CustomerAffiliation transform(CustomerGroupIfc group, String customerId)
    {
       
        CustomerAffiliation cAffil = new CustomerAffiliation();
        if (group != null && group.getGroupID() != null)
        {
            cAffil.setCustomerGroupID(Integer.parseInt(group.getGroupID()));
        }
        cAffil.setCustomerID(customerId);
    
        return cAffil;
    }

    /**
     * Utility method returns java.util.Locale from String Locale Description
     * @param value String Locale Description
     * @return Locale entity
     */
    protected Locale getLocale(String value)
    {
        Locale locale = new Locale(value);

        return locale;
    }

}
