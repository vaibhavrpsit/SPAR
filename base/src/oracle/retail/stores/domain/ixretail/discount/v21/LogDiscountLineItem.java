/* ===========================================================================
* Copyright (c) 2000, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/discount/v21/LogDiscountLineItem.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:07 mszekely Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/27/10 - updating deprecated names
 *    abonda 01/03/10 - update header date
 *    acadar 11/03/08 - localization of reason codes for discounts and merging
 *                      to tip
 *    acadar 10/30/08 - use localized reason codes for item and transaction
 *                      discounts
 *    acadar 10/25/08 - localization of price override reason codes
 *
 * ===========================================================================
     $Log:
      4    360Commerce 1.3         12/13/2005 4:43:47 PM  Barry A. Pape
           Base-lining of 7.1_LA
      3    360Commerce 1.2         3/31/2005 4:28:53 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:23:10 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:22 PM  Robert Pearse
     $
     Revision 1.6  2004/08/10 07:17:11  mwright
     Merge (3) with top of tree

     Revision 1.5.2.2  2004/08/06 02:24:50  mwright
     Set employee ID for employee discount

     Revision 1.5.2.1  2004/08/01 22:35:33  mwright
     Removed redundant call to set parent document

     Revision 1.5  2004/06/24 09:15:10  mwright
     POSLog v2.1 (second) merge with top of tree

     Revision 1.4.2.2  2004/06/23 00:16:52  mwright
     Set mandatory elements with default values (not used by import)
     Set default rule ID to 1 instead of 0

     Revision 1.4.2.1  2004/06/10 10:46:20  mwright
     Updated to use schema types in commerce services

     Revision 1.4  2004/05/06 03:33:06  mwright
     POSLog v2.1 merge with top of tree

     Revision 1.1.2.5  2004/04/27 22:03:51  mwright
     Changed from Amount360 to Amount in discount element

     Revision 1.1.2.4  2004/04/26 07:06:21  mwright
     Changes to use 360 price derivation rule and transaction discount elements, instead of the unextendable ixretail ones

     Revision 1.1.2.3  2004/04/13 06:02:39  mwright
     Ready for testing

     Revision 1.1.2.2  2004/03/28 10:31:03  mwright
     Work in progress

     Revision 1.1.2.1  2004/03/21 14:33:09  mwright
     Initial revision for POSLog v2.1

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.domain.ixretail.discount.v21;
// XML imports
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.discount.CustomerDiscountByPercentageIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;

import oracle.retail.stores.domain.ixretail.lineitem.v21.LogLineItem;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

import oracle.retail.stores.domain.ixretail.discount.LogDiscountLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;

import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionPriceDerivationRule360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionLineItemIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionDiscount360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogAmount;
import oracle.retail.stores.commerceservices.ixretail.schematypes.Currency;

//--------------------------------------------------------------------------
/**
    This class creates the elements for a DiscountLineItem
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogDiscountLineItem
extends LogLineItem
implements LogDiscountLineItemIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------------
    /**
        Constructs LogDiscountLineItem object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogDiscountLineItem()
    {                                   // begin LogDiscountLineItem()
    }                                   // end LogDiscountLineItem()

    //---------------------------------------------------------------------
    /**
       Creates element for the specified DiscountLineItem. <P>
       @param discountLineItem transaction discount line item
       @param doc parent document
       @param el parent element
       @param sequenceNumber sequence number
       @return Element representing discount line item
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createElement(TransactionDiscountStrategyIfc discountLineItem,
                                 Document doc,
                                 Element el,
                                 int sequenceNumber)
    throws XMLConversionException
    {

        super.createElement(doc, el, null, sequenceNumber);

        RetailTransactionLineItemIfc lineItemElement = (RetailTransactionLineItemIfc)el;

        RetailTransactionDiscount360Ifc discountElement = getSchemaTypesFactory().getRetailTransactionDiscountInstance();

        // The schema demands a value here, so we insert a zero that may be over-written later:
        discountElement.setRounding(new POSLogAmount().initialize(new Currency("0")));

        // set enabled attribute
        discountElement.setEnabled(new Boolean(discountLineItem.getEnabled()));

        // add pricing rule
        RetailTransactionPriceDerivationRule360Ifc priceRule = getSchemaTypesFactory().getRetailTransactionPriceDerivationRuleInstance();
        createPriceDerivationRuleElements((DiscountRuleIfc) discountLineItem,
                                           parentDocument,
                                           priceRule);


        // if percentage discount, get percentage
        if (discountLineItem.getDiscountMethod() == DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE)
        {
            discountElement.setPercentage(discountLineItem.getDiscountRate().movePointRight(2));
            discountElement.setAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(discountLineItem.getDiscountAmount())));
            // The schema requires a percent and action in the price rule...we set the percent, and guess the action (we never use it)
            priceRule.setPercent(discountLineItem.getDiscountRate().movePointRight(2));
            priceRule.setPercentAction("Add");
        }
        else
        {
            // get amount
            discountElement.setAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(discountLineItem.getDiscountAmount())));
            // The schema requires an amount and action in the price rule...we set the amount, and guess the action (we never use it)
            priceRule.setAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(discountLineItem.getDiscountAmount())));
            priceRule.setAmountAction("Add");
        }

        discountElement.setPriceDerivationRule(priceRule);
        discountElement.setDiscountEmployee(discountLineItem.getDiscountEmployeeID());
        lineItemElement.setDiscount(discountElement);

        return lineItemElement;

    }

    //---------------------------------------------------------------------
    /**
       Creates an element for a price derivation rule.
       @param discountRule discount rule
       @param doc parent document
       @param el parent element
       @return price derivation rule element
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createPriceDerivationRuleElements(DiscountRuleIfc discountRule,
                                                     Document doc,
                                                     Element el)
    throws XMLConversionException
    {

        RetailTransactionPriceDerivationRule360Ifc ruleElement = (RetailTransactionPriceDerivationRule360Ifc)el;


        // get rule ID
        String rid = discountRule.getRuleID();
        if (rid == null || rid.length() == 0)
        {
            rid = "1";      // NB: default in database when there is no applicable rule is to set rule ID to 1
        }
        ruleElement.setPriceDerivationRuleID(rid);


        // get reason code
        ruleElement.setReasonCode(discountRule.getReason().getCode());

        // get assignment basis
        ruleElement.setAssignmentbasis(DiscountRuleConstantsIfc.IXRETAIL_ASSIGNMENT_BASIS_DESCRIPTORS[discountRule.getAssignmentBasis()]);

        // get method code
        ruleElement.setPriceDerivationMethod(DiscountRuleConstantsIfc.IXRETAIL_DISCOUNT_METHOD_DESCRIPTOR[discountRule.getDiscountMethod()]);

        ruleElement.setPriceDerivationAccountingType(DiscountRuleConstantsIfc.ACCOUNTING_METHOD_DESCRIPTOR[discountRule.getAccountingMethod()]);
        ruleElement.setPriceDerivationApplicationType(DiscountRuleConstantsIfc.DISCOUNT_APPLICATION_TYPE_DESCRIPTOR[discountRule.getTypeCode()]);

        // get best deal flag
        boolean flag = false;
        if (discountRule instanceof CustomerDiscountByPercentageIfc)
        {
            flag = ((CustomerDiscountByPercentageIfc) discountRule).isIncludedInBestDeal();
        }
        ruleElement.setIncludedInBestDeal(new Boolean(flag));

        // get advanced pricing rule flag
        ruleElement.setAdvancedPricingRuleFlag(new Boolean(discountRule.isAdvancedPricingRule()));

        // get reference data, if it exsits
        if (!Util.isEmpty(discountRule.getReferenceID()))
        {
            // get discount reference
            ruleElement.setReferenceID(discountRule.getReferenceID());

            // get discount reference type
            ruleElement.setReferenceIDType(DiscountRuleConstantsIfc.IXRETAIL_REFERENCE_ID_CODE_DESCRIPTOR[discountRule.getReferenceIDCode()]);
        }

        // get post process type code
        ruleElement.setPostProcessTypeCode(Integer.toString(discountRule.getPostProcessTypeCode()));

        return ruleElement;

    }

    //---------------------------------------------------------------------
    /**
       Creates the price derivation rule element for a price override.
       @param doc parent document
       @param el parent element
       @return price derivation rule element
       @exception XMLConversionException if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createPriceOverrideRuleElements(Document doc,
                                                   Element el)
    throws XMLConversionException
    {
        RetailTransactionPriceDerivationRule360Ifc ruleElement = (RetailTransactionPriceDerivationRule360Ifc)el;

        // mandatory stuff, all meaningless in this context
        // The above comment is nonesense.  This needs to be filled in with correct data.
        ruleElement.setIncludedInBestDeal(new Boolean(false));
        ruleElement.setAdvancedPricingRuleFlag(new Boolean(false));
        ruleElement.setPriceDerivationRuleID("0");
        ruleElement.setReasonCode("0");
        ruleElement.setAssignmentbasis("None");
        ruleElement.setPriceDerivationMethod("None");
        ruleElement.setPriceDerivationApplicationType("None");
        ruleElement.setPostProcessTypeCode("0");
        return ruleElement;
    }

    public Element createPriceOverrideRuleElements(Document doc,
            Element el, SaleReturnLineItemIfc lineItem)
    throws XMLConversionException
    {
        RetailTransactionPriceDerivationRule360Ifc ruleElement = (RetailTransactionPriceDerivationRule360Ifc)el;

//      mandatory stuff, all meaningless in this context
//      The above comment is nonesense.  This needs to be filled in with correct data.
        ruleElement.setIncludedInBestDeal(new Boolean(false));
        ruleElement.setAdvancedPricingRuleFlag(new Boolean(false));
        ruleElement.setPriceDerivationRuleID("0");
        if(lineItem != null)
        {
            ruleElement.setReasonCode(lineItem.getItemPrice().getItemPriceOverrideReason().getCode());
        }
        else
        {
            ruleElement.setReasonCode("0");
        }
        ruleElement.setAssignmentbasis("None");
        ruleElement.setPriceDerivationMethod("None");
        ruleElement.setPriceDerivationApplicationType("None");
        ruleElement.setPostProcessTypeCode("0");
        return ruleElement;
    }
    //---------------------------------------------------------------------
    /**
       Creates the price derivation rule for an advanced pricing rule
       record.  This is used to record the rules for pricing on a suspended
       transaction.
       @param discountRule advanced pricing rule
       @param doc document
       @param el element
       @return advanced pricing rule element
       @exception XMLConversionException if error occurs
    **/
    //---------------------------------------------------------------------
    public Element createAdvancedPricingRuleElement(AdvancedPricingRuleIfc discountRule,
                                                    Document doc,
                                                    Element el)
    throws XMLConversionException
    {
        setParentDocument(doc);

        RetailTransactionPriceDerivationRule360Ifc ruleElement = (RetailTransactionPriceDerivationRule360Ifc)el;

        // get rule ID
        String rid = discountRule.getRuleID();
        if (rid == null || rid.length() == 0)
        {
            rid = "1";
        }
        ruleElement.setPriceDerivationRuleID(rid);


        // get reference data, if it exsits
        if (!Util.isEmpty(discountRule.getReferenceID()))
        {
            // get discount reference
            ruleElement.setReferenceID(discountRule.getReferenceID());

            // get discount reference type
            ruleElement.setReferenceIDType(DiscountRuleConstantsIfc.REFERENCE_ID_CODE_DESCRIPTOR[discountRule.getReferenceIDCode()]);
        }

        if (discountRule.getSourceComparisonBasis() != DiscountRuleConstantsIfc.COMPARISON_BASIS_UNINITIALIZED)
        {
            // get comparison basis
            ruleElement.setComparisonBasis(DiscountRuleConstantsIfc.COMPARISON_BASIS_DESCRIPTORS[discountRule.getSourceComparisonBasis()]);
        }

        return ruleElement;
   }

}
