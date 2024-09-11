/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/DiscountRule.java /main/28 2013/09/23 14:48:28 tksharma Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *   jswan   10/21/14 - Fixed clone issue that causes an RTLog defect and removed 
 *                      low hanging deprecations.
 *   tksharm 09/23/13 - added promotionComponentId and
 *                      promotionComponentDetailId getters/setters
 *   abhinav 12/11/12 - Fixing HP Fortify missing null check issues
 *   tksharm 12/09/12 - added AdvancedPricingRule setter/getters
 *   jswan   12/06/12 - Modified to support JDBC opertions for order tax and
 *                      discount status.
 *   sgu     11/14/12 - added discount and tax pickup view
 *   cgreene 01/05/11 - XbranchMerge cgreene_itemprice_empdiscount from
 *                      rgbustores_13.3x_generic_branch
 *   cgreene 05/26/10 - convert to oracle packaging
 *   cgreene 04/27/10 - updating deprecated names
 *   abondal 01/03/10 - update header date
 *   cgreene 03/27/09 - added method getDescriptionCode for receipt printing
 *   mdecama 11/07/08 - I18N - updated toString()
 *   mdecama 11/07/08 - I18N - Fixed Clone Method
 *   acadar  11/04/08 - localization of reason codes for transaction tax
 *   acadar  11/03/08 - updates as per code review
 *   acadar  11/03/08 - transaction tax reason codes updates
 *   acadar  11/03/08 - localization of reason codes for discounts and merging
 *                      to tip
 *   acadar  11/02/08 - updates to unit tests
 *   acadar  10/30/08 - cleanup
 *   acadar  10/30/08 - localization of reason codes for item and transaction
 *                      discounts
 *   acadar  10/30/08 - use localized reason codes for item and transaction
 *                      discounts
 *   mdecama 10/23/08 - Reason Codes - Added new methods to the interface and
 *                      related method stubs to the related classes.
 *   acadar  10/17/08 - undid Anil's checked ins
 *   abondal 10/17/08 - I18Ning manufacturer name
 *   abondal 10/14/08 - I18Ning manufacturer name
 *   acadar  10/14/08 - unit test fixes
 *   acadar  10/14/08 - cleanup for retrieval of localized discount rule name
 *   acadar  10/14/08 - updates for reading the localized discount name for
 *                      customer
 *   acadar  10/13/08 - updates for reading localized information
 *   acadar  10/08/08 - use LocaleRequestor to read the localzed name and
 *                      description for advanced pricing rules
 *   cgreene 09/19/08 - updated with changes per FindBugs findings
 *   cgreene 09/11/08 - update header
 *
 * ===========================================================================
     $Log:
      6    360Commerce 1.5         4/3/2008 10:36:42 PM   Leona R. Slepetis
           Insert ID_PRM into TR_LTM_DSC for a store coupon. Reviewed by
           A.Sinton
      5    360Commerce 1.4         4/25/2007 10:01:00 AM  Anda D. Cadar   I18N
           merge
      4    360Commerce 1.3         1/22/2006 11:41:28 AM  Ron W. Haight
           Removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:27:46 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:20:59 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:10:36 PM  Robert Pearse
     $
     Revision 1.4  2004/06/04 19:18:24  cdb
     @scr 4180 Removed some deprectated methods and constants from commerce services
     and some dependent files in domain.

     Revision 1.3  2004/02/12 17:13:28  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:27  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:34:56   CSchellenger
 * Initial revision.
 *
 *    Rev 1.6   Jul 10 2003 16:59:40   bwf
 * Updated clone attributes.
 * Resolution for 2678: Markdown Reason Codes not displaying correct information in Pricing
 *
 *    Rev 1.5   Jul 10 2003 16:53:16   bwf
 * Added markdownFlg and get/set functions.
 * Resolution for 2678: Markdown Reason Codes not displaying correct information in Pricing
 *
 *    Rev 1.4   Mar 20 2003 09:26:30   jgs
 * Changes due to code reveiw.
 * Resolution for 103: New Advanced Pricing Features
 *
 *    Rev 1.3   Jan 22 2003 15:16:48   mpb
 * SCR #1626
 * Added accountingMethod attribute.
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.2   Jan 22 2003 09:39:42   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * ===========================================================================
 */
package oracle.retail.stores.domain.discount;

import java.math.BigDecimal;
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSTime;

import org.apache.log4j.Logger;

/**
 * This class defines a discount rule or plan and is the base class for all
 * discount rules or strategies in the extendyourstore.domain.discount package.
 * A rule is used to transform the base retail price to the discounted selling
 * price actually paid by a customer at the point-of-sale.
 * 
 * @see oracle.retail.stores.domain.discount.DiscountRuleIfc
 * @version $Revision: /main/28 $
 */
public class DiscountRule implements DiscountRuleIfc
{
    private static final long serialVersionUID = -7923297504817381911L;

    private static final Logger logger = Logger.getLogger(DiscountRule.class);

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/28 $";

    /**
     * when in the course of a transaction the rule is applied
     */
    protected int appliedWhen = APPLIED_DETAIL;

    /**
     * status of discount rule
     */
    protected int status = STATUS_PENDING;

    /**
     * type discount rule
     */
    protected int typeCode = DISCOUNT_APPLICATION_TYPE_ITEM;

    /**
     * date rule becomes effective
     */
    protected EYSDate effectiveDate = null;

    /**
     * date rule ceases to be effective
     */
    protected EYSDate expirationDate = null;

    /**
     * time rule becomes effective
     */
    protected EYSTime effectiveTime = null;

    /**
     * time rule ceases to be effective
     */
    protected EYSTime expirationTime = null;

    /**
     * description
     */
    protected String description = "";

    /**
     * rule name
     */
    protected String name = "";

    /**
     * rule identifier
     */
    protected String ruleID = "";

    /**
     * reason code
     * 
     * @deprecated as of 13.1. Use {@link reason}
     */
    protected int reasonCode = CodeConstantsIfc.CODE_INTEGER_UNDEFINED;

    /**
     * Localized reason code for discounts
     */
    protected LocalizedCodeIfc reason = DomainGateway.getFactory().getLocalizedCode();

    /**
     * discount by amount or rate
     */
    protected int discountMethod = DISCOUNT_METHOD_NONE;

    /**
     * discount by transaction or item
     */
    protected int discountScope = DISCOUNT_SCOPE_TRANSACTION;

    /**
     * amount of discount
     */
    protected CurrencyIfc discountAmount = null;

    /**
     * discount rate
     */
    protected BigDecimal discountRate = null;

    /**
     * basis of assignment, e.g., manual, by customer selection, by item
     * selection
     */
    protected int assignmentBasis = ASSIGNMENT_MANUAL;

    /**
     * indicates if discount is enabled
     */
    protected boolean enabled = true;

    /**
     * indicates if the Preferred Customer Discount is an alternative best deal
     */
    protected boolean includedInBestDeal = false;

    /**
     * indicates if the rule is an advanced pricing one
     */
    protected boolean advancedPricingRule = false;

    /**
     * Discount Rule Reference ID
     */
    protected String referenceID = null;

    /**
     * indicates the Reference ID code
     */
    protected int referenceIDCode = REFERENCE_ID_CODE_NONE;

    /**
     * Discount Rule Promotion ID
     */
    protected int promotionId = PROMOTION_ID_CODE_NONE;
    
    /**
     * The Discount Promotion Component Id
     */
    protected int promotionComponentId = PROMOTION_COMPONENT_ID_CODE_NONE;

    /**
     * The Discount Promotion Component Detail Id
     */
    protected int promotionComponentDetailId = PROMOTION_COMPONENT_DETAIL_ID_CODE_NONE;

    /**
     * indicates if rule is a markdown or a discount
     */
    protected int accountingMethod = DiscountRuleConstantsIfc.ACCOUNTING_METHOD_DISCOUNT;

    /**
     * Indicates how this discount should be treated in special post sale
     * processing. The values are application dependent.
     */
    protected int postProcessTypeCode = 0;

    /**
     * indicates whether a markdown or not
     */
    protected boolean markdownFlg = false;

    /**
     * LocalizedTextIfc containing localized names
     */
    protected LocalizedTextIfc localizedNames = DomainGateway.getFactory().getLocalizedText();
    
    /**
     * Order item discount line reference
     */
    protected int orderItemDiscountLineReference = -1;
    
    /**
     * Advanced Pricing Rule reference to a discount Strategy.
     */
    protected AdvancedPricingRuleIfc rule;

    /**
     * Constructs DiscountRule object.
     * <P>
     */
    public DiscountRule()
    {
        discountAmount = DomainGateway.getBaseCurrencyInstance();
        discountRate = BigDecimal.ZERO;
    }

    /**
     * Creates clone of this object.
     * 
     * @return Object clone of this object
     */
    public Object clone()
    {
        DiscountRule clone = new DiscountRule();
        setCloneAttributes(clone);
        return clone;
    }

    /**
     * Sets attributes in clone of this object.
     * 
     * @param newClass new instance of object
     */
    public void setCloneAttributes(DiscountRule newClass)
    {

        if (effectiveDate != null)
        {
            newClass.setEffectiveDate((EYSDate)getEffectiveDate().clone());
        }
        if (expirationDate != null)
        {
            newClass.setExpirationDate((EYSDate)getExpirationDate().clone());
        }
        if (effectiveTime != null)
        {
            newClass.setEffectiveTime((EYSTime)getEffectiveTime().clone());
        }
        if (expirationTime != null)
        {
            newClass.setExpirationTime((EYSTime)getExpirationTime().clone());
        }
        if (discountAmount != null)
        {
            newClass.setDiscountAmount((CurrencyIfc)getDiscountAmount().clone());
        }
        if (discountRate != null)
        {
            newClass.setDiscountRate(getDiscountRate());
        }
        if (rule != null)
        {
            newClass.setRule(getRule());
        }
        

        newClass.setAppliedWhen(getAppliedWhen());
        newClass.setStatus(getStatus());
        newClass.setDescription(getDescription());
        newClass.setLocalizedNames(getLocalizedNames());
        newClass.setRuleID(getRuleID());
        if (reason != null)
        {
            newClass.setReason((LocalizedCodeIfc)getReason().clone());
        }
        newClass.setDiscountMethod(getDiscountMethod());
        newClass.setDiscountScope(getDiscountScope());
        newClass.setAssignmentBasis(getAssignmentBasis());
        newClass.setEnabled(getEnabled());
        newClass.setIncludedInBestDeal(isIncludedInBestDeal());
        newClass.setAdvancedPricingRule(isAdvancedPricingRule());
        newClass.setReferenceID(getReferenceID());
        newClass.setReferenceIDCode(getReferenceIDCode());
        newClass.setPromotionId(getPromotionId());
        newClass.setPromotionComponentId(getPromotionComponentId());
        newClass.setPromotionComponentDetailId(getPromotionComponentDetailId());
        newClass.setTypeCode(getTypeCode());
        newClass.setAccountingMethod(getAccountingMethod());
        newClass.setPostProcessTypeCode(getPostProcessTypeCode());
        newClass.setMarkdownFlag(isMarkdown());
        newClass.setOrderItemDiscountLineReference(getOrderItemDiscountLineReference());
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true; // quick exit

        boolean isEqual = false;

  
           if(obj instanceof DiscountRule)
            {
            DiscountRule c = (DiscountRule)obj; // downcast the input object

            // compare all the attributes of DiscountRule
            if (getAppliedWhen() == c.getAppliedWhen() && getStatus() == c.getStatus()
                    && Util.isObjectEqual(getEffectiveDate(), c.getEffectiveDate())
                    && Util.isObjectEqual(getExpirationDate(), c.getExpirationDate())
                    && Util.isObjectEqual(getEffectiveTime(), c.getEffectiveTime())
                    && Util.isObjectEqual(getExpirationTime(), c.getExpirationTime())
                    && Util.isObjectEqual(getDescription(), c.getDescription())
                    && Util.isObjectEqual(getLocalizedNames(), c.getLocalizedNames())
                    && Util.isObjectEqual(getRule(), c.getRule())
                    && Util.isObjectEqual(getRuleID(), c.getRuleID()) && Util.isObjectEqual(getReason(), c.getReason())
                    && getDiscountMethod() == c.getDiscountMethod() && getDiscountScope() == c.getDiscountScope()
                    && Util.isObjectEqual(getDiscountAmount(), c.getDiscountAmount())
                    && Util.isObjectEqual(getDiscountRate(), c.getDiscountRate())
                    && getAssignmentBasis() == c.getAssignmentBasis() && getEnabled() == c.getEnabled()
                    && Util.isObjectEqual(getReferenceID(), c.getReferenceID())
                    && getReferenceIDCode() == c.getReferenceIDCode() && getPromotionId() == c.getPromotionId()
                    && getPromotionComponentId() == c.getPromotionComponentId()
                    && getPromotionComponentDetailId() == c.getPromotionComponentDetailId()
                    && getTypeCode() == c.getTypeCode() && getPostProcessTypeCode() == c.getPostProcessTypeCode()
                    && getAccountingMethod() == c.getAccountingMethod()
                    && isIncludedInBestDeal() == c.isIncludedInBestDeal()
                    && getOrderItemDiscountLineReference() == c.getOrderItemDiscountLineReference())
              {
                isEqual = true; // set the return code to true
              }
            
           }

        return isEqual;
    }

    /**
     * Retrieves when in the course of a transaction the rule is applied.
     * 
     * @return when in the course of a transaction the rule is applied
     */
    public int getAppliedWhen()
    {
        return (appliedWhen);
    }

    /**
     * Sets when in the course of a transaction the rule is applied.
     * 
     * @param value when in the course of a transaction the rule is applied
     */
    public void setAppliedWhen(int value)
    {
        appliedWhen = value;
    }

    /**
     * Sets the value for includedInBestDeal flag: true - the Preferred Customer
     * Discount is an alternative best deal; false - the Preferred Customer
     * Discount is added to the transaction in addition to all other discounts.
     * 
     * @param value
     */
    public void setIncludedInBestDeal(boolean value)
    {
        includedInBestDeal = value;
    }

    /**
     * Returns the value of includedInBestDeal flag. If the return value is
     * true, the Preferred Customer Discount (PCD) is an alternative best deal,
     * if false PCD is added to the transaction in addition to all other
     * discounts.
     * 
     * @param value
     */
    public boolean isIncludedInBestDeal()
    {
        return (includedInBestDeal);
    }

    /**
     * Retrieves status of discount rule.
     * 
     * @return status of discount rule
     */
    public int getStatus()
    {
        return (status);
    }

    /**
     * Retrieves status of discount rule.
     * 
     * @return status of discount rule
     */
    public String getStatusString()
    {
        return STATUS_DESCRIPTORS[status];
    }

    /**
     * Sets status of discount rule.
     * 
     * @param value status of discount rule
     */
    public void setStatus(int value)
    {
        status = value;
    }

    /**
     * Retrieves type of discount rule.
     * 
     * @return type of discount rule
     */
    public int getTypeCode()
    {
        return (typeCode);
    }

    /**
     * Sets typeCode of discount rule.
     * 
     * @param value type code of discount rule
     */
    public void setTypeCode(int value)
    {
        typeCode = value;
    }

    /**
     * Returns String descriptor of status.
     * 
     * @param value status value
     * @return String descriptor of status
     * @see oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc#STATUS_DESCRIPTORS
     */
    public String statusToString(int value)
    {
        // if exception, use Unknown verbiage
        String statusString = Util.UNKNOWN;
        try
        {
            statusString = STATUS_DESCRIPTORS[value];
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            // display real value
            logger.warn("Unable to get value for STATUS_DESCRIPTORS at index " + value);
        }
        return (statusString);
    }

    /**
     * Returns String descriptor of status.
     * 
     * @return String descriptor of status
     * @see oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc#STATUS_DESCRIPTORS
     */
    public String statusToString()
    {
        return (statusToString(getStatus()));
    }

    /**
     * Retrieves date rule becomes effective.
     * 
     * @return date rule becomes effective
     */
    public EYSDate getEffectiveDate()
    {
        return (effectiveDate);
    }

    /**
     * Sets date rule becomes effective.
     * 
     * @param value date rule becomes effective
     */
    public void setEffectiveDate(EYSDate value)
    {
        effectiveDate = value;
    }

    /**
     * Retrieves date rule ceases to be effective.
     * 
     * @return date rule ceases to be effective
     */
    public EYSDate getExpirationDate()
    {
        return (expirationDate);
    }

    /**
     * Sets date rule ceases to be effective.
     * 
     * @param value date rule ceases to be effective
     */
    public void setExpirationDate(EYSDate value)
    {
        expirationDate = value;
    }

    /**
     * Retrieves time rule becomes effective.
     * 
     * @return time rule becomes effective
     */
    public EYSTime getEffectiveTime()
    {
        return (effectiveTime);
    }

    /**
     * Retrieves time rule becomes effective.
     * 
     * @return time rule becomes effective
     */
    public String getEffectiveTimeString()
    {
        return effectiveTime.toFormattedString("HH:mm:ss");
    }

    /**
     * Retrieves time rule becomes expired.
     * 
     * @return time rule becomes expired
     */
    public String getExpirationTimeString()
    {
        return expirationTime.toFormattedString("HH:mm:ss");
    }

    /**
     * Sets time rule becomes effective.
     * 
     * @param value time rule becomes effective
     */
    public void setEffectiveTime(EYSTime value)
    {
        effectiveTime = value;
    }

    /**
     * Retrieves time rule ceases to be effective.
     * 
     * @return time rule ceases to be effective
     */
    public EYSTime getExpirationTime()
    {
        return (expirationTime);
    }

    /**
     * Sets time rule ceases to be effective.
     * 
     * @param value time rule ceases to be effective
     */
    public void setExpirationTime(EYSTime value)
    {
        expirationTime = value;
    }

    /**
     * Retrieves description.
     * 
     * @return description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets description.
     * 
     * @param value description
     */
    public void setDescription(String value)
    {
        description = value;
    }

    
    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.DiscountRuleIfc#getDescriptionCode()
     */
    public int getDescriptionCode()
    {
        String code = Integer.toString(getAssignmentBasis()) +
                                        getDiscountMethod() +
                                        getDiscountScope() +
                                        getAccountingMethod();
        return Integer.valueOf(code);
    }

    /**
     * Retrieves rule name for a specified locale.
     * 
     * @param Locale the specified locale
     * @return String rule name
     */
    public String getName(Locale lcl)
    {
        Locale bestMatch = LocaleMap.getBestMatch(lcl);
        return (localizedNames.getText(bestMatch));
    }

    /**
     * Sets rule name.
     * 
     * @param value rule name
     * @param Locale lcl
     */
    public void setName(Locale lcl, String value)
    {
        Locale bestMatch = LocaleMap.getBestMatch(lcl);
        localizedNames.putText(bestMatch, value);
    }

    /**
     * Retrieves rule identifier.
     * 
     * @return rule identifier
     */
    public String getRuleID()
    {
        return (ruleID);
    }

    /**
     * Sets rule identifier.
     * 
     * @param value rule identifier
     */
    public void setRuleID(String value)
    {
        ruleID = value;
    }

    /**
     * Retrieves reason code.
     * 
     * @return reason code
     * @deprecated as of 13.1 Use {@link #getReason()}
     */
    public int getReasonCode()
    {
        return Integer.parseInt(reason.getCode());
    }

    /**
     * Retrieves reason code Text.
     * 
     * @return reason code
     * @deprecated as of 13.1 Use {@link #getReason()}
     */
    public String getReasonCodeText()
    {
        return reason.getText(LocaleMap.getLocale(LocaleMap.DEFAULT));

    }

    /**
     * Sets reason code.
     * 
     * @param value reason code
     * @deprecated as of 13.1 Use {@link #setReason(LocalizedCodeIfc)}
     */
    public void setReasonCode(int value)
    {
        reason.setCode(Integer.toString(value));
    }

    /**
     * Sets reason code Text.
     * 
     * @param value reason code
     * @deprecated as of 13.1 Use {@link #setReason(LocalizedCodeIfc)}
     */
    public void setReasonCodeText(String value)
    {
        LocalizedTextIfc text = DomainGateway.getFactory().getLocalizedText();
        text.putText(LocaleMap.getLocale(LocaleMap.DEFAULT), value);
        reason.setText(text);
    }

    /**
     * non-Javadoc)
     * 
     * @see oracle.retail.stores.domain.discount.DiscountRuleIfc#getReason()
     */
    public LocalizedCodeIfc getReason()
    {
        return reason;
    }

    /*
     * (non-Javadoc)
     * @seeoracle.retail.stores.domain.discount.DiscountRuleIfc#setReason(com.
     * _360commerce.common.utility.LocalizedCodeIfc)
     */
    public void setReason(LocalizedCodeIfc reason)
    {
        this.reason = reason;

    }

    /**
     * Retrieves discount by amount or rate.
     * 
     * @return discount by amount or rate
     */
    public int getDiscountMethod()
    {
        return (discountMethod);
    }

    /**
     * Sets discount by amount or rate.
     * 
     * @param value discount by amount or rate
     */
    public void setDiscountMethod(int value)
    {
        discountMethod = value;
    }

    /**
     * Retrieves scope of discount.
     * 
     * @return scope of discount
     */
    public int getDiscountScope()
    {
        return (discountScope);
    }

    /**
     * Sets scope of discount.
     * 
     * @param value scope of discount
     */
    public void setDiscountScope(int value)
    {
        discountScope = value;
    }

    /**
     * Retrieves amount of discount.
     * 
     * @return amount of discount
     */
    public CurrencyIfc getDiscountAmount()
    {
        return (discountAmount);
    }

    /**
     * Sets amount of discount.
     * 
     * @param value amount of discount
     */
    public void setDiscountAmount(CurrencyIfc value)
    {
        discountAmount = value;
    }

    /**
     * Retrieves discount rate.
     * 
     * @return discount rate
     */
    public BigDecimal getDiscountRate()
    {
        return (discountRate);
    }

    /**
     * Sets discount rate.
     * 
     * @param value discount rate
     */
    public void setDiscountRate(BigDecimal value)
    {
        discountRate = value;
    }

    /**
     * Retrieves assignment basis of discount.
     * 
     * @return assignment basis of discount
     */
    public int getAssignmentBasis()
    {
        return assignmentBasis;
    }

    /**
     * Sets assignment basis of discount.
     * 
     * @param value assignment basis of discount
     */
    public void setAssignmentBasis(int value)
    {
        assignmentBasis = value;
    }

    /**
     * Returns String descriptor of assignment basis.
     * 
     * @param value assignmentBasis value
     * @return String descriptor of assignment basis
     * @see oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc#ASSIGNMENT_BASIS_DESCRIPTORS
     */
    public String assignmentBasisToString(int value)
    {
        // if exception, use Unknown verbiage
        String assignmentBasisString = Util.UNKNOWN;
        try
        {
            assignmentBasisString = ASSIGNMENT_BASIS_DESCRIPTORS[value];
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            // display real value
            logger.warn("Unable to get value for ASSIGNMENT_BASIS_DESCRIPTORS at index " + value);
        }
        return (assignmentBasisString);
    }

    /**
     * Returns String descriptor of assignment basis.
     * 
     * @return String descriptor of assignment basis
     * @see oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc#ASSIGNMENT_BASIS_DESCRIPTORS
     */
    public String assignmentBasisToString()
    {
        return assignmentBasisToString(assignmentBasis);
    }

    /**
     * @return discount-enabled indicator
     * @deprecated as of 13.3. Use {@link #isEnabled()} instead.
     */
    public boolean getEnabled()
    {
        return isEnabled();
    }

    /**
     * Retrieves discount-enabled indicator.
     * 
     * @return discount-enabled indicator
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Sets the discount-enabled indicator.
     * 
     * @param value discount-enabled indicator
     */
    public void setEnabled(boolean value)
    {
        enabled = value;
    }

    /**
     * Retrieves advanced pricing rule indicator. This flag indicates if the
     * discount is an advanced pricing one, not a manual one
     * 
     * @return advancedPricingRule indicator
     */
    public boolean isAdvancedPricingRule()
    {
        return advancedPricingRule;
    }

    /**
     * Sets advanced pricing rule indicator.
     * 
     * @param value
     */
    public void setAdvancedPricingRule(boolean value)
    {
        advancedPricingRule = value;
    }

    /**
     * Sets Reference ID.
     * 
     * @param value
     */
    public void setReferenceID(String value)
    {
        referenceID = value;
    }

    /**
     * Sets Reference ID Code.
     * 
     * @param value
     */
    public void setReferenceIDCode(int value)
    {
        referenceIDCode = value;
    }

    /**
     * Is Discount Rule a Store Coupon Rule.
     * 
     * @param value
     */
    public boolean isStoreCoupon()
    {
        return (assignmentBasis == ASSIGNMENT_STORE_COUPON) ? true : false;
    }

    /**
     * Gets Reference ID.
     * 
     * @param value
     */
    public String getReferenceID()
    {
        return referenceID;
    }

    /**
     * Gets Reference ID Code.
     * 
     * @param value
     */
    public int getReferenceIDCode()
    {
        return referenceIDCode;
    }

    /**
     * @return Returns the promotionID.
     */
    public int getPromotionId()
    {
        return promotionId;
    }

    /**
     * @param promotionID The promotionID to set.
     */
    public void setPromotionId(int promotionId)
    {
        this.promotionId = promotionId;
    }
    
    /**
     * Get the Promotion Component Id
     * @return
     */
    public int getPromotionComponentId ()
    {
        return promotionComponentId;
    }

    /**
     * Set the Promotion Component Id
     * @param promotionComponentId
     */
    public void setPromotionComponentId (int promotionComponentId)
    {
        this.promotionComponentId = promotionComponentId;
    }

    /**
     * Get the Promotion Component Detail Id
     * @return
     */
    public int getPromotionComponentDetailId ()
    {
        return promotionComponentDetailId;
    }

    /**
     * Set the Promotion Component Detail Id
     * @param promotionComponentDetailId
     */
    public void setPromotionComponentDetailId (int promotionComponentDetailId)
    {
        this.promotionComponentDetailId = promotionComponentDetailId;
    }


    /**
     * Gets the end date as string
     * 
     * @return String
     */
    public String getExpirationDateString()
    {
        return getExpirationDate().toFormattedString("MM/dd/yyyy");
    }

    /**
     * Gets the end date as string
     * 
     * @return String
     */
    public String getEffectiveDateString()
    {
        return getEffectiveDate().toFormattedString("MM/dd/yyyy");
    }

    /**
     * Retrieves the post processing type code of an advanced pricing rule.
     * 
     * @return post processing type code of discount rule
     */
    public int getPostProcessTypeCode()
    {
        return postProcessTypeCode;
    }

    /**
     * Sets post processingTypeCode of an advanced pricing rule.
     * 
     * @param value post processing type code of advanced pricing rule
     */
    public void setPostProcessTypeCode(int value)
    {
        postProcessTypeCode = value;
    }

    /**
     * Sets accounting method flag indicator.
     * 
     * @param accounting method
     */
    public void setAccountingMethod(int value)
    {
        accountingMethod = value;
    }

    /**
     * Gets accounting method flag indicator.
     * 
     * @return accounting method
     */
    public int getAccountingMethod()
    {
        return accountingMethod;
    }

    /**
     * Return the value of the markdownFlg. true - discount is a markdown false
     * - discount is not a markdown
     * 
     * @return boolean
     */
    public boolean isMarkdown()
    {
        return (markdownFlg);
    }

    /**
     * Return the value of the markdownFlg. true - discount is a markdown false
     * - discount is not a markdown
     * 
     * @param value
     */
    public void setMarkdownFlag(boolean value)
    {
        markdownFlg = value;
    }

    /**
     * Gets the localized names
     * 
     * @return LocalizedTextIfc
     */
    public LocalizedTextIfc getLocalizedNames()
    {
        return (localizedNames);
    }

    /**
     * Sets name.
     * 
     * @param LocalizedTextIfc localizedNames
     */
    public void setLocalizedNames(LocalizedTextIfc localizedNames)
    {
        this.localizedNames = localizedNames;
    }

    /**
     * Get order item discount line reference
     * 
     * @return the reference id
     */
    public int getOrderItemDiscountLineReference() 
    {
        return orderItemDiscountLineReference;
    }

     /**
      * Set order item discount line reference
      * 
      * @param orderItemDiscountLineReference the reference id
      */
    public void setOrderItemDiscountLineReference(int orderItemDiscountLineReference) 
    {
        this.orderItemDiscountLineReference = orderItemDiscountLineReference;
    }

     /**
     * @return the rule
     */
    public AdvancedPricingRuleIfc getRule()
    {
        return rule;
    }

    /**
     * @param rule the rule to set
     */
    public void setRule(AdvancedPricingRuleIfc rule)
    {
        this.rule = rule;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        // build result string
        StringBuilder strResult = new StringBuilder();
        strResult.append("Class:  " + getClass().getName() + " (Revision ").append(getRevisionNumber()).append(") @")
                .append(hashCode()).append(Util.EOL);
        // add attributes to string
        strResult.append("appliedWhen:                        ").append("[").append(getAppliedWhen()).append("]")
                .append(Util.EOL).append("includedInBestDeal:                     ").append("[").append(
                        isIncludedInBestDeal()).append("]").append(Util.EOL).append(
                        "advancedPricingRule:                    ").append("[").append(isAdvancedPricingRule()).append(
                        "]").append(Util.EOL).append("status:                             ").append("[").append(
                        statusToString()).append("]").append(Util.EOL).append("assignment basis:                   ")
                .append("[").append(assignmentBasisToString()).append("]").append(Util.EOL).append(
                        "description:                        ").append("[").append(getDescription().toString()).append(
                        "]").append(Util.EOL).append("name:                               ").append("[").append(
                        getLocalizedNames().toString()).append("]").append(Util.EOL).append(
                        "ruleID:                             ").append("[").append(getRuleID()).append("]").append(
                        Util.EOL).append("reason:                             ").append("[").append(getReason())
                .append("]").append(Util.EOL).append("discountMethod:                     ").append("[").append(
                        getDiscountMethod()).append("]").append(Util.EOL)
                .append("discountScope:                      ").append("[").append(getDiscountScope()).append("]")
                .append(Util.EOL);
        if (getEffectiveDate() == null)
        {
            strResult.append("effectiveDate:                      [null]").append(Util.EOL);
        }
        else
        {
            strResult.append("effectiveDate:                      ").append("[").append(getEffectiveDate()).append("]")
                    .append(Util.EOL);
        }
        if (getExpirationDate() == null)
        {
            strResult.append("expirationDate:                     [null]").append(Util.EOL);
        }
        else
        {
            strResult.append("expirationDate:                     ").append("[").append(getExpirationDate())
                    .append("]").append(Util.EOL);
        }
        if (getEffectiveTime() == null)
        {
            strResult.append("effectiveTime:                      [null]").append(Util.EOL);
        }
        else
        {
            strResult.append("effectiveTime:                      ").append("[").append(getEffectiveTime()).append("]")
                    .append(Util.EOL);
        }
        if (getExpirationTime() == null)
        {
            strResult.append("expirationTime:                     [null]").append(Util.EOL);
        }
        else
        {
            strResult.append("expirationTime:                     ").append("[").append(getExpirationTime())
                    .append("]").append(Util.EOL);
        }
        if (getDiscountAmount() == null)
        {
            strResult.append("discountAmount:                             [null]").append(Util.EOL);
        }
        else
        {
            strResult.append("discountAmount:                             ").append("[").append(getDiscountAmount())
                    .append("]").append(Util.EOL);
        }
        if (getDiscountRate() == null)
        {
            strResult.append("discountRate:                               [null]").append(Util.EOL);
        }
        else
        {
            strResult.append("discountRate:                               ").append("[").append(getDiscountRate())
                    .append("]").append(Util.EOL);
        }
        strResult.append("ReferenceIDCode:                               ").append("[").append(getReferenceIDCode())
                .append("]").append(Util.EOL);
        strResult.append("ReferenceID:                               ").append("[").append(getReferenceID())
                .append("]").append(Util.EOL);

        strResult.append("PromotionID:                               ").append("[").append(getPromotionId())
                .append("]").append(Util.EOL);
        
        strResult.append("PromotionComponentID:                               ").append("[").append(getPromotionComponentId())
        .append("]").append(Util.EOL);
        
        strResult.append("PromotionComponentDetailID:                               ").append("[").append(getPromotionComponentDetailId())
        .append("]").append(Util.EOL);

        strResult.append("AccountingMethod:              ").append("[").append(getAccountingMethod()).append("]")
                .append(Util.EOL);

        strResult.append("PostProcessTypeCode:                       ").append("[").append(getPostProcessTypeCode())
                .append("]").append(Util.EOL);
        strResult.append("OrderItemDiscountLineReference:            ").append("[").append(getOrderItemDiscountLineReference())
        .append("]").append(Util.EOL);
        // pass back result
        return (strResult.toString());
    }

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * DiscountRulemain method.
     * 
     * @param String args[] command-line parameters
     */
    public static void main(String args[])
    {
        // instantiate class
        DiscountRule c = new DiscountRule();
        // output toString()
        System.out.println(c.toString());
    }
    
    

}
