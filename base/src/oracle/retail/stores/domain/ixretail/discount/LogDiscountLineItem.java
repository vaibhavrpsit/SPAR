/* ===========================================================================
* Copyright (c) 2000, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/discount/LogDiscountLineItem.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:07 mszekely Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/28/10 - updating deprecated names
 *    cgreen 04/28/10 - updating deprecated names
 *    cgreen 04/27/10 - updating deprecated names
 *    abonda 01/03/10 - update header date
 *    acadar 11/03/08 - localization of reason codes for discounts and merging
 *                      to tip
 *    acadar 10/30/08 - use localized reason codes for item and transaction
 *                      discounts
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         12/13/2005 4:43:47 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:28:53 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:10 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:22 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 17:13:39  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:28  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:36:12   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Jul 01 2003 14:09:30   jgs
 * Modifications for new 6.0 data.
 * Resolution for 1157: Add task for Importing IX Retail Transactions.
 *
 *    Rev 1.1   Jan 22 2003 09:56:34   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.0   Sep 05 2002 11:12:40   msg
 * Initial revision.
 *
 *    Rev 1.3   Aug 24 2002 13:25:10   vpn-mpm
 * Added support for discount rule post-process-type code
 *
 *    Rev 1.2   Apr 28 2002 13:32:00   mpm
 * Completed translation of sale transactions.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.1   Apr 26 2002 07:48:56   mpm
 * Modified to set line-item-type attribute.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   Apr 25 2002 09:02:18   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.domain.ixretail.discount;
// XML imports
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.discount.CustomerDiscountByPercentageIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.ixretail.lineitem.LogLineItem;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

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
    {                                   // begin createElement()
        // create discount line item element
        Element lineItemElement =
          createElement(doc,
                        el,
                        IXRetailConstantsIfc.ELEMENT_DISCOUNT_360,
                        sequenceNumber);

        // create discount element with 360 type
        Element discountElement = parentDocument.createElement
          (IXRetailConstantsIfc.ELEMENT_DISCOUNT_360);
        discountElement.setAttribute
          (IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_TYPE_TAG,
           IXRetailConstantsIfc.TYPE_DISCOUNT_360);

        // set enabled attribute
        discountElement.setAttribute
          (IXRetailConstantsIfc.ATTRIBUTE_ENABLED,
           new Boolean(discountLineItem.getEnabled()).toString());

        // get amount
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT,
           discountLineItem.getDiscountAmount().toString(),
           discountElement);

        // if percentage discount, get percentage
        if (discountLineItem.getDiscountMethod() ==
             DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE)
        {
             createTextNodeElement
               (IXRetailConstantsIfc.ELEMENT_PERCENTAGE,
                discountLineItem.getDiscountRate().movePointRight(2).toString(),
                discountElement);
        }

        // add pricing rule
        Element priceDerivationRuleElement =
          createPriceDerivationRuleElements((DiscountRuleIfc) discountLineItem,
                                            parentDocument,
                                            parentElement);

        discountElement.appendChild(priceDerivationRuleElement);

        lineItemElement.appendChild(discountElement);

        parentElement.appendChild(lineItemElement);

        return(lineItemElement);

    }                                   // end createElement()

    //---------------------------------------------------------------------
    /**
       Creates an element for a price derivation rule.
       @param discountLineItem discount line item
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
    {                                   // begin createPriceDerivationRuleElements()
        setParentDocument(doc);
        setParentElement(el);

        Element ruleElement = doc.createElement
          (IXRetailConstantsIfc.ELEMENT_PRICE_DERIVATION_RULE);

        // get reason code
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_REASON_CODE,
           discountRule.getReason().getCode(),
           ruleElement);

        // get assignment basis
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_ASSIGNMENT_BASIS,
           DiscountRuleConstantsIfc.IXRETAIL_ASSIGNMENT_BASIS_DESCRIPTORS[discountRule.getAssignmentBasis()],
           ruleElement);

        // get rule ID
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_PRICE_DERIVATION_RULE_ID,
           discountRule.getRuleID(),
           ruleElement);

        // get method code
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_PRICE_DERIVATION_METHOD,
           DiscountRuleConstantsIfc.IXRETAIL_DISCOUNT_METHOD_DESCRIPTOR[discountRule.getDiscountMethod()],
           ruleElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_PRICE_DERIVATION_ACCOUNTING_TYPE,
           DiscountRuleConstantsIfc.ACCOUNTING_METHOD_DESCRIPTOR[discountRule.getAccountingMethod()],
           ruleElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_PRICE_DERIVATION_APPLICATION_TYPE,
           DiscountRuleConstantsIfc.DISCOUNT_APPLICATION_TYPE_DESCRIPTOR[discountRule.getTypeCode()],
           ruleElement);

        // get best deal flag
        boolean flag = false;
        if (discountRule instanceof CustomerDiscountByPercentageIfc)
        {
            flag = ((CustomerDiscountByPercentageIfc) discountRule).isIncludedInBestDeal();
        }
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_INCLUDED_IN_BEST_DEAL,
           new Boolean(flag).toString(),
           ruleElement);

        // get advanced pricing rule flag
        createTextNodeElement
         (IXRetailConstantsIfc.ELEMENT_ADVANCED_PRICING_RULE_FLAG,
          new Boolean(discountRule.isAdvancedPricingRule()).toString(),
          ruleElement);

        // get reference data, if it exsits
        if (!Util.isEmpty(discountRule.getReferenceID()))
        {
            // get discount reference
            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_REFERENCE_ID,
               discountRule.getReferenceID(),
               ruleElement);

            // get discount reference type
            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_REFERENCE_ID_TYPE,
               DiscountRuleConstantsIfc.IXRETAIL_REFERENCE_ID_CODE_DESCRIPTOR[discountRule.getReferenceIDCode()],
               ruleElement);
        }

        // get post process type code
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_POST_PROCESS_TYPE_CODE,
           discountRule.getPostProcessTypeCode(),
           ruleElement);

        el.appendChild(ruleElement);

        return(ruleElement);

    }                                   // end createPriceDerivationRuleElements()

    /**
     * Create OverridePriceRule elements.
     * @param doc Doc
     * @param el Element
     * @param lineItem SaleReturnLineItem
     * @return price derivation rule element
     * @see oracle.retail.stores.domain.ixretail.discount.LogDiscountLineItemIfc#createPriceOverrideRuleElements(org.w3c.dom.Document, org.w3c.dom.Element, oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc)
     */
    public Element createPriceOverrideRuleElements(Document doc,
            Element el, SaleReturnLineItemIfc lineItem)
    throws XMLConversionException
    {
        return createPriceOverrideRuleElements(doc, el);
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
    {                                   // begin createPriceOverrideRuleElements()
        setParentDocument(doc);
        setParentElement(el);

        Element ruleElement = doc.createElement
          (IXRetailConstantsIfc.ELEMENT_PRICE_DERIVATION_RULE);

        // get rule ID
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_PRICE_DERIVATION_RULE_ID,
           "0",
           ruleElement);

        el.appendChild(ruleElement);

        return(ruleElement);
    }                                   // end createPriceOverrideRuleElements()

    //---------------------------------------------------------------------
    /**
       Creates the price derivation rule for an advanced pricing rule
       record.  This is used to record the rules for pricing on a suspended
       transaction.
       @param rule advanced pricing rule
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
    {                                   // begin createAdvancedPricingRuleElement()
        setParentDocument(doc);
        setParentElement(el);

        Element ruleElement = doc.createElement
          (IXRetailConstantsIfc.ELEMENT_PRICE_DERIVATION_RULE);

        // get rule ID
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_PRICE_DERIVATION_RULE_ID,
           discountRule.getRuleID(),
           ruleElement);

        // get reference data, if it exsits
        if (!Util.isEmpty(discountRule.getReferenceID()))
        {
            // get discount reference
            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_REFERENCE_ID,
               discountRule.getReferenceID(),
               ruleElement);

            // get discount reference type
            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_REFERENCE_ID_TYPE,
               DiscountRuleConstantsIfc.REFERENCE_ID_CODE_DESCRIPTOR[discountRule.getReferenceIDCode()],
               ruleElement);
        }

        if (discountRule.getSourceComparisonBasis() !=
             DiscountRuleConstantsIfc.COMPARISON_BASIS_UNINITIALIZED)
        {
            // get comparison basis
            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_PRICING_RULE_COMPARISON_BASIS,
               DiscountRuleConstantsIfc.COMPARISON_BASIS_DESCRIPTORS[discountRule.getSourceComparisonBasis()],
               ruleElement);
        }

        el.appendChild(ruleElement);

        return(ruleElement);
   }                                   // end createAdvancedPricingRuleElement()

}
