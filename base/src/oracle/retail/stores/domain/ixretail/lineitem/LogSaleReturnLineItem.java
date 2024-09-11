/* ===========================================================================
* Copyright (c) 2002, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/lineitem/LogSaleReturnLineItem.java /main/18 2012/05/14 09:43:57 yiqzhao Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    yiqzha 05/14/12 - remove shipping charge, keep the file unchange.
 *    cgreen 07/07/11 - convert entryMethod to an enum
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/28/10 - updating deprecated names
 *    cgreen 04/28/10 - updating deprecated names
 *    cgreen 04/27/10 - updating deprecated names
 *    abonda 01/03/10 - update header date
 *    ranojh 10/31/08 - Refreshed View and Merged changes with Reason Codes
 *    ranojh 10/29/08 - Fixed ReturnItem
 *    acadar 10/29/08 - merged to tip
 *    ddbake 10/28/08 - Update for merge
 *    acadar 10/25/08 - localization of price override reason codes
 *
 * ===========================================================================
     $Log:
      5    360Commerce 1.4         4/25/2007 10:00:50 AM  Anda D. Cadar   I18N
           merge
      4    360Commerce 1.3         1/22/2006 11:41:34 AM  Ron W. Haight
           Removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:28:56 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:23:16 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:26 PM  Robert Pearse
     $
     Revision 1.7  2004/08/10 07:17:12  mwright
     Merge (3) with top of tree


     Revision 1.6  2004/08/09 17:58:37  dcobb
     @scr 6801 createRetailTransactionTaxElements() in LogSaleReturnLineItem should have protected scope

     Revision 1.5  2004/07/19 15:40:45  mweis
     @scr 6342 createAlterationElements() method now protected instead of private


     Revision 1.4.6.1  2004/07/29 01:03:55  mwright
     Added transaction to interface parameter list


     Revision 1.4  2004/02/17 16:18:58  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:44  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:29  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:36:26   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Jul 01 2003 14:09:28   jgs
 * Modifications for new 6.0 data.
 * Resolution for 1157: Add task for Importing IX Retail Transactions.
 *
 *    Rev 1.3   May 18 2003 09:06:34   mpm
 * Merged 5.1 changes into 6.0
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.2   Feb 15 2003 14:52:08   mpm
 * Merged 5.1 changes.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.1   Jan 22 2003 09:58:20   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.1   Dec 14 2002 13:12:34   mpm
 * Merged KB changes.
 * Resolution for Domain SCR-102: Merge IXRetail corrections into 5.5
 *
 *    Rev 1.0   Sep 05 2002 11:12:50   msg
 * Initial revision.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.domain.ixretail.lineitem;

// XML imports
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByAmountIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.ReturnItemTransactionDiscountAuditIfc;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.discount.LogDiscountLineItemIfc;
import oracle.retail.stores.domain.ixretail.utility.LogGiftCardIfc;
import oracle.retail.stores.domain.ixretail.utility.LogQuantityIfc;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.lineitem.KitComponentLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.AlterationPLUItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.ItemClassificationConstantsIfc;
import oracle.retail.stores.domain.stock.ItemKitConstantsIfc;
import oracle.retail.stores.domain.stock.UnknownItemIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.AlterationIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

/**
 * This class creates the elements for a SaleReturnLineItem
 *
 * @version $Revision: /main/18 $
 */
public class LogSaleReturnLineItem extends LogLineItem implements LogSaleReturnLineItemIfc
{
    /**
     * revision number supplied by source-code-control system
     */
    public static String revisionNumber = "$Revision: /main/18 $";

    /**
     * Constructs LogSaleReturnLineItem object.
     */
    public LogSaleReturnLineItem()
    {
    }

    /**
     * Creates element for the specified SaleReturnLineItem.
     *
     * @param srli SaleReturnLineItem
     * @param doc parent document
     * @param el parent element
     * @param voidFlag flag indicating line has been voided
     * @param sequenceNumber sequence number
     * @return Element representing SaleReturnLineItem
     * @exception XMLConversionException thrown if error occurs
     */
    public Element createElement(SaleReturnLineItemIfc srli, TransactionIfc transaction, Document doc, Element el,
            boolean voidFlag, int sequenceNumber) throws XMLConversionException
    {
        setParentDocument(doc);
        setParentElement(el);

        // create appropriate item element
        Element itemElement = createRetailTransactionLineItemElement(srli);

        Element lineItemElement = createElement(doc, el, itemElement.getNodeName(), voidFlag, sequenceNumber);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ENTRY_METHOD, srli.getEntryMethod().getIxRetailDescriptor(),
                lineItemElement);

        createItemElements(srli, itemElement);

        lineItemElement.appendChild(itemElement);

        parentElement.appendChild(lineItemElement);

        return (lineItemElement);

    }

    /**
     * Creates element for the specified SaleReturnLineItem.
     *
     * @param srli SaleReturnLineItem
     * @param doc parent document
     * @param el parent element
     * @param sequenceNumber sequence number
     * @return Element representing SaleReturnLineItem
     * @exception XMLConversionException thrown if error occurs
     */
    public Element createElement(SaleReturnLineItemIfc srli, TransactionIfc transaction, Document doc, Element el,
            int sequenceNumber) throws XMLConversionException
    {
        return (createElement(srli, transaction, doc, el, false, sequenceNumber));
    }

    /**
     * Creates elements for item.
     *
     * @param srli sale return line item
     * @param itemElement element for item
     * @exception XMLConversionException thrown if error occurs.
     */
    protected void createItemElements(SaleReturnLineItemIfc srli, Element itemElement) throws XMLConversionException
    {
        // create identity id node and append to item element
        Element posIdentityElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_POS_IDENTITY);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_POS_ITEM_ID, srli.getPosItemID(), posIdentityElement);

        itemElement.appendChild(posIdentityElement);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ITEM_ID, srli.getPLUItem().getItemID(), itemElement);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_DESCRIPTION,
                srli.getPLUItem().getDescription(LocaleMap.getLocale(LocaleMap.DEFAULT)), itemElement);

        // set list price based on override code (this may reflect a defect)
        CurrencyIfc unitListPrice = srli.getPLUItem().getSellingPrice();
        // if no override, use item price
        if (srli.getItemPrice().getItemPriceOverrideReason().getCode().equals(CodeConstantsIfc.CODE_UNDEFINED))
        {
            unitListPrice = srli.getItemPrice().getSellingPrice();
        }

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_UNIT_LIST_PRICE, unitListPrice, itemElement);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_EXTENDED_AMOUNT, srli.getItemPrice()
                .getExtendedSellingPrice(), itemElement);

        createQuantityElement(srli, itemElement);

        createAssociateElement(srli, itemElement);

        // if return is without receipt, -1 is item link value, which is invalid
        if (srli.isReturnLineItem() && srli.getReturnItem().getOriginalLineNumber() != -1)
        {
            // item link is sequence number
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ITEM_LINK, srli.getReturnItem().getOriginalLineNumber(),
                    itemElement);
        }

        createRetailPriceModifierElements(srli, itemElement);

        createRetailTransactionTaxElements(srli, itemElement);

        if (!Util.isEmpty(srli.getItemSerial()))
        {
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_SERIAL_NUMBER, srli.getItemSerial(), itemElement);
        }

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_POS_DEPARTMENT_ID, srli.getPLUItem().getDepartmentID(),
                itemElement);

        // denote item not on file, as needed
        if (srli.getPLUItem() instanceof UnknownItemIfc)
        {
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ITEM_NOT_ON_FILE_FLAG, true, itemElement);
        }

        itemElement.setAttribute(IXRetailConstantsIfc.ELEMENT_ITEM_TYPE, getItemType(srli));

        // handle kit line items
        createKitElements(srli, itemElement);

        if (srli.getRegistry() != null && !Util.isEmpty(srli.getRegistry().getID()))
        {
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_REGISTRY_ID, srli.getRegistry().getID(), itemElement);
        }

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_EXTENDED_DISCOUNTED_AMOUNT, srli.getItemPrice()
                .getExtendedDiscountedSellingPrice(), itemElement);

        // get gift receipt, if necessary
        if (srli.isGiftReceiptItem())
        {
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_GIFT_RECEIPT_FLAG, true, itemElement);
        }

        // get gift receipt, if necessary
        if (srli.isAlterationItem())
        {
            createAlterationElements(srli, itemElement);
        }

        // add order item elements
        if (srli.isOrderItem())
        {
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_REFERENCE_NUMBER, srli.getOrderLineReference(),
                    itemElement);

            createOrderItemElements(srli.getOrderItemStatus(), itemElement);
        }

        // if return, create disposal and original transaction elements
        if (srli.isReturnLineItem())
        {
            createReturnItemElements(srli, itemElement);
        }

        // if gift card
        if (srli.getPLUItem() instanceof GiftCardPLUItemIfc)
        {
            createGiftCardElements(((GiftCardPLUItemIfc)srli.getPLUItem()).getGiftCard(), itemElement);
        }

    }

    /**
     * Creates appropriate RetailTransactionLineItem element.
     *
     * @param srli sale return line item object
     * @return RetailTransactionLineItem object
     * @exception XMLConversionException thrown if error occurs
     */
    protected Element createRetailTransactionLineItemElement(SaleReturnLineItemIfc srli) throws XMLConversionException
    {
        Element itemElement = null;

        // check for return
        if (srli.isReturnLineItem())
        {
            itemElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_RETURN_360);
            itemElement.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_TYPE_TAG,
                    IXRetailConstantsIfc.TYPE_RETAIL_TRANSACTION_RETURN_360);
        }
        else if (srli.isOrderItem())
        {
            itemElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_ORDER_360);
            itemElement.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_TYPE_TAG,
                    IXRetailConstantsIfc.TYPE_RETAIL_TRANSACTION_ORDER_FOR_PICKUP_360);
        }
        // check for gift card
        else if (srli.getPLUItem() instanceof GiftCardPLUItemIfc)
        {
            itemElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_SALE_OF_GIFT_CARD360);
        }
        else
        {
            itemElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_SALE_360);
            itemElement.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_TYPE_TAG,
                    IXRetailConstantsIfc.TYPE_RETAIL_TRANSACTION_SALE_360);
        }

        return (itemElement);
    }

    /**
     * Translates 360 item classification type to IXRetail-compliant type.
     *
     * @param srli SaleReturnLineItem object
     * @return item type attribute value
     */
    protected String getItemType(SaleReturnLineItemIfc srli)
    {
        int itemType = srli.getPLUItem().getItem().getItemClassification().getItemType();
        String itemTypeString = IXRetailConstantsIfc.ATTRIBUTE_VALUE_STOCK;
        if (itemType == ItemClassificationConstantsIfc.TYPE_SERVICE)
        {
            itemTypeString = IXRetailConstantsIfc.ATTRIBUTE_VALUE_SERVICE;
        }

        return (itemTypeString);
    }

    /**
     * Creates Quantity element.
     *
     * @param srli SaleReturnLineItemIfc object
     * @param el line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createQuantityElement(SaleReturnLineItemIfc srli, Element el) throws XMLConversionException
    {
        LogQuantityIfc logQuantity = IXRetailGateway.getFactory().getLogQuantityInstance();

        logQuantity.createElement(srli.getItemQuantityDecimal(), srli.getPLUItem().getUnitOfMeasure(), parentDocument,
                el);

    }

    /**
     * Creates RetailTransactionAssociate element.
     *
     * @param srli SaleReturnLineItemIfc object
     * @param el sale return line item element
     * @exception XMLConversionException thrown if error occurs translating to
     *                XML
     */
    protected void createAssociateElement(SaleReturnLineItemIfc srli, Element el) throws XMLConversionException
    {
        String associateID = null;
        if (srli.getSalesAssociate() != null)
        {
            associateID = srli.getSalesAssociate().getEmployeeID();
        }

        if (!Util.isEmpty(associateID))
        {
            Element associateElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_ASSOCIATE);

            associateElement.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_TYPE_TAG,
                    IXRetailConstantsIfc.TYPE_RETAIL_TRANSACTION_ASSOCIATE_360);

            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ASSOCIATE_ID, srli.getSalesAssociate().getEmployeeID(),
                    associateElement);

            if (srli.getSalesAssociateModifiedFlag())
            {
                createTextNodeElement(IXRetailConstantsIfc.ELEMENT_COMMISSION_OVERRIDE,
                        srli.getSalesAssociateModifiedFlag(), associateElement);
            }

            el.appendChild(associateElement);
        }
    }

    /**
     * Creates RetailPriceModifier elements.
     *
     * @param srli SaleReturnLineItemIfc object
     * @param el sale return line item element
     * @exception XMLConversionException thrown if error occurs translating to
     *                XML
     */
    protected void createRetailPriceModifierElements(SaleReturnLineItemIfc srli, Element el)
            throws XMLConversionException
    {
        int discountSequenceNumber = 0;

        // check for price override
        if (srli.getItemPrice().getItemPriceOverrideReason().getCode().equals(CodeConstantsIfc.CODE_UNDEFINED))
        {
            createRetailPriceModifierElement(srli, discountSequenceNumber, null, el);
            ++discountSequenceNumber;
        }

        ItemDiscountStrategyIfc[] modifiers = srli.getItemPrice().getItemDiscounts();
        ItemDiscountStrategyIfc discountLineItem = null;

        // get number of discounts for loop
        int numDiscounts = 0;
        if (modifiers != null)
        {
            numDiscounts = modifiers.length;
        }

        // loop through line items
        for (int i = 0; i < numDiscounts; i++)
        {
            discountLineItem = modifiers[i];

            // skip transaction discounts
            if (discountLineItem.getDiscountScope() != DiscountRuleConstantsIfc.DISCOUNT_SCOPE_TRANSACTION)
            {
                createRetailPriceModifierElement(srli, discountSequenceNumber, discountLineItem, el);
            }

            ++discountSequenceNumber;
        }
    }

    /**
     * Creates a single RetailPriceModifierElement.
     *
     * @param srli SaleReturnLineItemIfc object
     * @param discountSequenceNumber discount sequence number
     * @param discountLineItem discount line item
     * @param el parent element for sale return line item
     * @exception XMLConversionException thrown if error occurs translating to
     *                XML
     */
    protected void createRetailPriceModifierElement(SaleReturnLineItemIfc srli, int discountSequenceNumber,
            ItemDiscountStrategyIfc discountLineItem, Element el) throws XMLConversionException
    {
        Element rpmElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_PRICE_MODIFIER);
        // set element as a 360 type
        rpmElement.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_TYPE_TAG,
                IXRetailConstantsIfc.TYPE_PRICE_MODIFIER_360);

        // set sequence number
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_SEQUENCE_NUMBER, Integer.toString(discountSequenceNumber),
                rpmElement);

        // set elements in item discount
        if (discountLineItem != null)
        { // Item discount
            createItemDiscountElements(rpmElement, discountLineItem, srli);
        }
        else
        { // Price Override
            createPriceOverrideElements(rpmElement, srli);
        }

        el.appendChild(rpmElement);
    }

    /**
     * Creates retail price modifier element(s) for item discount.
     *
     * @param rpmElement retail price modifier element
     * @param discountLineItem discount line item
     * @param srli sale return line item
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createItemDiscountElements(Element rpmElement, ItemDiscountStrategyIfc discountLineItem,
            SaleReturnLineItemIfc srli) throws XMLConversionException
    {
        Element useElement = null;
        Text useText = null;

        // set method code attribute
        String methodCode = IXRetailConstantsIfc.ATTRIBUTE_VALUE_PROMOTION;
        // note: this leaves customer discounts as a promotion
        if (discountLineItem.isAdvancedPricingRule())
        {
            methodCode = IXRetailConstantsIfc.ATTRIBUTE_VALUE_PRICE_RULE;
        }
        rpmElement.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_METHOD_CODE, methodCode);

        switch (discountLineItem.getDiscountMethod())
        {
        case DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE:
            // build percentage discount element
            useElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_PERCENT);
            useText = parentDocument.createTextNode(discountLineItem.getDiscountRate().movePointRight(2).toString());
            useElement.appendChild(useText);
            useElement.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_ACTION,
                    IXRetailConstantsIfc.ATTRIBUTE_VALUE_SUBTRACT);
            rpmElement.appendChild(useElement);
            break;
        case DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT:
        case DiscountRuleConstantsIfc.DISCOUNT_METHOD_FIXED_PRICE:
            // build amount discount element
            useElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_AMOUNT);
            // derive amount text
            String amount = null;
            if (discountLineItem instanceof ItemDiscountByAmountIfc)
            {
                ItemDiscountByAmountIfc discount = (ItemDiscountByAmountIfc)discountLineItem;
                amount = discount.getDiscountAmount().getStringValue();
            }
            else if (discountLineItem instanceof ReturnItemTransactionDiscountAuditIfc)
            {
                ReturnItemTransactionDiscountAuditIfc discount = (ReturnItemTransactionDiscountAuditIfc)discountLineItem;
                amount = discount.getDiscountAmount().getStringValue();
            }
            else
            {
                amount = "0.00";
            }
            useText = parentDocument.createTextNode(amount);
            // append data to element
            useElement.appendChild(useText);
            useElement.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_ACTION,
                    IXRetailConstantsIfc.ATTRIBUTE_VALUE_SUBTRACT);
            rpmElement.appendChild(useElement);
            break;
        default:
            break;
        }

        // get previous price
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_PREVIOUS_PRICE, srli.getItemPrice().getSellingPrice(),
                rpmElement);

        // create discount line item element for adding price derivation rule
        LogDiscountLineItemIfc logDiscountLineItem = IXRetailGateway.getFactory().getLogDiscountLineItemInstance();

        logDiscountLineItem.createPriceDerivationRuleElements((DiscountRuleIfc)discountLineItem, parentDocument,
                rpmElement);

    }

    /**
     * Creates retail price modifier element(s) for price override
     *
     * @param rpmElement retail price modifier element
     * @param srli sale return line item
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createPriceOverrideElements(Element rpmElement, SaleReturnLineItemIfc srli)
            throws XMLConversionException
    {
        Element useElement = null;
        Text useText = null;

        rpmElement.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_METHOD_CODE,
                IXRetailConstantsIfc.ATTRIBUTE_VALUE_PRICE_OVERRIDE);

        // amount element
        useElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_AMOUNT);
        // derive amount text
        useText = parentDocument.createTextNode(srli.getItemPrice().getSellingPrice().toString());
        // append data to element
        useElement.appendChild(useText);
        useElement.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_ACTION, IXRetailConstantsIfc.ATTRIBUTE_VALUE_REPLACE);
        rpmElement.appendChild(useElement);

        // get previous price
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_PREVIOUS_PRICE, srli.getPLUItem().getSellingPrice(),
                rpmElement);

        // get reason code
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_REASON_CODE, srli.getItemPrice()
                .getItemPriceOverrideReason().getCode(), rpmElement);

        LogDiscountLineItemIfc logDiscountLineItem = IXRetailGateway.getFactory().getLogDiscountLineItemInstance();

        logDiscountLineItem.createPriceOverrideRuleElements(parentDocument, rpmElement);

    }

    /**
     * Method createAlterationElements.
     *
     * @param srli
     * @param itemElement
     */
    protected void createAlterationElements(SaleReturnLineItemIfc srli, Element itemElement)
            throws XMLConversionException
    {
        AlterationIfc alteration = ((AlterationPLUItemIfc)(srli.getPLUItem())).getAlteration();

        Element alterationElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_ALTERATION_360);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ALTERATION_TYPE, alteration.getAlterationType(),
                alterationElement);

        if (!alteration.getValue1().equals(""))
        {
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ALTERATION_INSTRUCTION, alteration.getValue1(),
                    alterationElement);
        }

        if (!alteration.getValue2().equals(""))
        {
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ALTERATION_INSTRUCTION, alteration.getValue2(),
                    alterationElement);
        }

        if (!alteration.getValue3().equals(""))
        {
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ALTERATION_INSTRUCTION, alteration.getValue3(),
                    alterationElement);
        }

        if (!alteration.getValue4().equals(""))
        {
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ALTERATION_INSTRUCTION, alteration.getValue4(),
                    alterationElement);
        }

        if (!alteration.getValue5().equals(""))
        {
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ALTERATION_INSTRUCTION, alteration.getValue5(),
                    alterationElement);
        }

        if (!alteration.getValue6().equals(""))
        {
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ALTERATION_INSTRUCTION, alteration.getValue6(),
                    alterationElement);
        }

        itemElement.appendChild(alterationElement);
    }

    /**
     * Creates RetailTransactionTax elements for line item.
     *
     * @param srli sale return line item object
     * @param el parent element
     * @exception XMLConversionException is thrown if error occurs
     */
    protected void createRetailTransactionTaxElements(SaleReturnLineItemIfc srli, Element el)
            throws XMLConversionException
    {

        Element taxElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_TAX);

        taxElement.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_TYPE_TAG, IXRetailConstantsIfc.TYPE_TAX_360);

        // the app now supports authority, but not in this instance
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_AUTHORITY_ID,
                IXRetailConstantsIfc.ELEMENT_VALUE_NOT_APPLICABLE, taxElement);

        // the app now supports ruleID, used in this case indicate it is the
        // the total item tax
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_RULE_ID, IXRetailConstantsIfc.ELEMENT_TOTAL, taxElement);

        // tax group
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_TAX_GROUP_ID,
                Integer.toString(srli.getPLUItem().getTaxGroupID()), taxElement);

        // taxable amount
        ItemTaxIfc itemTax = srli.getItemPrice().getItemTax();
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_TAXABLE_AMOUNT, Double.toString(itemTax.getTaxableAmount()),
                taxElement);

        // tax amount
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_AMOUNT, itemTax.getItemTaxAmount(), taxElement);

        // tax rate
        int taxMode = itemTax.getTaxMode();
        String taxRateString = Double.toString(itemTax.getDefaultRate() * 100.0);
        // handle override rate
        if (taxMode == TaxIfc.TAX_MODE_OVERRIDE_RATE)
        {
            taxRateString = Double.toString(itemTax.getOverrideRate() * 100.0);
        }

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_PERCENT, taxRateString, taxElement);

        // add 360 elements for mode, scope
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_TAX_MODE,
                TaxIfc.IXRETAIL_TAX_MODE_DESCRIPTOR[itemTax.getTaxMode()], taxElement);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_TAX_SCOPE,
                TaxIfc.TAX_SCOPE_DESCRIPTOR[itemTax.getTaxScope()], taxElement);

        if (taxMode == TaxIfc.TAX_MODE_OVERRIDE_AMOUNT || taxMode == TaxIfc.TAX_MODE_OVERRIDE_RATE
                || taxMode == TaxIfc.TAX_MODE_TOGGLE_OFF || taxMode == TaxIfc.TAX_MODE_TOGGLE_ON)
        {
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_REASON_CODE, itemTax.getReason().getCode(), taxElement);
        }

        el.appendChild(taxElement);

        // Jurisdictional Tax data is currently held in a hashtable of vectors;
        // this vector must be replaced by a class.
        Hashtable taxJurisdiction = srli.getItemTax().getTaxByTaxJurisdiction();
        if (taxJurisdiction != null)
        {
            Enumeration authority = taxJurisdiction.keys();
            while (authority.hasMoreElements())
            {
                String authorityGroupID = (String)authority.nextElement();
                Vector taxData = (Vector)taxJurisdiction.get(authorityGroupID);
                createRetailTransactionTaxElements(srli, el, taxData, authorityGroupID);
            }
        }

    }

    /**
     * Method createRetailTransactionTaxElements.
     *
     * @param srli the sale return line item that contains the tax info
     * @param el the parent element
     * @param taxData holds add autority specific data.
     * @param authorityGroupID a combination of tax group and authority IDs.
     */
    protected void createRetailTransactionTaxElements(SaleReturnLineItemIfc srli, Element el, Vector taxData,
            String authorityGroupID) throws XMLConversionException
    {

        CurrencyIfc taxAmountByTaxAuthority = (CurrencyIfc)taxData.elementAt(0);
        String taxRuleName = (String)taxData.elementAt(1);
        BigDecimal taxRate = (BigDecimal)taxData.elementAt(2);
        // Parse the authority from the authorityGroupID; i.e, "20.122" where 20
        // = tax group
        // and 122 = authority ID.
        String authorityID = authorityGroupID.substring((authorityGroupID).indexOf(".") + 1, authorityGroupID.length());

        Element taxElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_TAX);

        taxElement.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_TYPE_TAG, IXRetailConstantsIfc.TYPE_TAX_360);

        // authority
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_AUTHORITY_ID, authorityID, taxElement);

        // rule ID
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_RULE_ID, taxRuleName, taxElement);

        // tax group
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_TAX_GROUP_ID,
                Integer.toString(srli.getPLUItem().getTaxGroupID()), taxElement);

        // taxable amount
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_TAXABLE_AMOUNT, srli.getExtendedDiscountedSellingPrice()
                .getStringValue(), taxElement);

        // tax amount
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_AMOUNT, taxAmountByTaxAuthority, taxElement);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_PERCENT, taxRate.toString(), taxElement);

        // add 360 elements for mode, scope
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_TAX_MODE,
                TaxIfc.IXRETAIL_TAX_MODE_DESCRIPTOR[TaxIfc.TAX_MODE_STANDARD], taxElement);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_TAX_SCOPE,
                TaxIfc.TAX_SCOPE_DESCRIPTOR[TaxIfc.TAX_SCOPE_ITEM], taxElement);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_TOTAL_ITEM_TAX, srli.getItemPrice().getItemTax()
                .getItemTaxAmount(), taxElement);

        el.appendChild(taxElement);
    }

    /**
     * Create elements for kits.
     *
     * @param srli sale return line item
     * @param el element to which additions are to be made
     * @exception XMLConversionException is thrown if error occurs
     */
    protected void createKitElements(SaleReturnLineItemIfc srli, Element el) throws XMLConversionException
    {
        // Note: 360 handling of kits is not compatible with IXRetail standard
        if (srli.isKitHeader())
        {
            // create header elements
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ITEM_KIT_SET_CODE,
                    ItemKitConstantsIfc.ITEM_KIT_CODE_DESCRIPTORS[ItemKitConstantsIfc.ITEM_KIT_CODE_HEADER], el);
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ITEM_COLLECTION_ID, srli.getItemID(), el);
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_KIT_HEADER_REFERENCE_ID,
                    Integer.toString(srli.getKitHeaderReference()), el);
        }
        else if (srli.isKitComponent())
        {
            // create component elements
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ITEM_KIT_SET_CODE,
                    ItemKitConstantsIfc.ITEM_KIT_CODE_DESCRIPTORS[ItemKitConstantsIfc.ITEM_KIT_CODE_COMPONENT], el);
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ITEM_COLLECTION_ID,
                    ((KitComponentLineItemIfc)srli).getItemKitID(), el);
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_KIT_HEADER_REFERENCE_ID,
                    Integer.toString(srli.getKitHeaderReference()), el);
        }
    }

    /**
     * Creates elements for return-item disposal.
     *
     * @param srli sale return line item object
     * @param el line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createDisposalElements(SaleReturnLineItemIfc srli, Element el) throws XMLConversionException
    {
        // Note: disposal element is not supported in 360Commerce applications
        Element disposalElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_DISPOSAL);

        disposalElement.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_METHOD,
                IXRetailConstantsIfc.ATTRIBUTE_VALUE_UNDECIDED);

        el.appendChild(disposalElement);
    }

    /**
     * Creates elements for return-item original transaction link.
     *
     * @param srli sale return line item object
     * @param el line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createTransactionLinkElements(SaleReturnLineItemIfc srli, Element el) throws XMLConversionException
    {
        ReturnItemIfc returnItem = srli.getReturnItem();
        TransactionIDIfc originalTransactionID = returnItem.getOriginalTransactionID();

        // if no transaction ID, this is return without original transaction;
        // this element is not created
        if (originalTransactionID != null)
        {
            // create original transaction link element
            Element transElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_ORIGINAL_TRANSACTION);

            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_RETAIL_STORE_ID, originalTransactionID.getStoreID(),
                    transElement);

            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_WORKSTATION_ID,
                    originalTransactionID.getWorkstationID(), transElement);

            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_SEQUENCE_NUMBER,
                    originalTransactionID.getFormattedTransactionSequenceNumber(), transElement);

            // return without receipt may have transaction ID but no business
            // date.
            // therefore, bypass if null
            createDateTextNodeElement(IXRetailConstantsIfc.ELEMENT_BUSINESS_DAY_DATE,
                    returnItem.getOriginalTransactionBusinessDate(), transElement, true);

            transElement.setAttribute(IXRetailConstantsIfc.ELEMENT_REASON_CODE, IXRetailConstantsIfc.ELEMENT_RETURN);

            el.appendChild(transElement);
        }

    }

    /**
     * Creates elements for a return item.
     *
     * @param srli sale return line item
     * @param el item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createReturnItemElements(SaleReturnLineItemIfc srli, Element el) throws XMLConversionException
    {
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_REASON_CODE, srli.getReturnItem().getReason().getCode(), el);

        createDisposalElements(srli, el);

        // get restocking fee, if necessary
        if (srli.getItemPrice().getRestockingFee().signum() != 0)
        {
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_RESTOCKING_FEE, srli.getItemPrice().getRestockingFee(),
                    el);
        }

        createTransactionLinkElements(srli, el);

    }

    /**
     * Creates elements for order-item status.
     *
     * @param orderItem order item status object
     * @param el line item element
     * @exception XMLConversionException thrown if error occurs.
     */
    protected void createOrderItemElements(OrderItemStatusIfc orderItem, Element el) throws XMLConversionException
    {
        LogOrderItemIfc logOrderLine = IXRetailGateway.getFactory().getLogOrderItemInstance();
        logOrderLine.createElement(orderItem, parentDocument, el);
    }

    /**
     * Creates elements for gift card.
     *
     * @param giftCard gift card object
     * @param el line item element
     * @exception XMLConversionException thrown if error occurs.
     */
    protected void createGiftCardElements(GiftCardIfc giftCard, Element el) throws XMLConversionException
    {
        LogGiftCardIfc logGiftCard = IXRetailGateway.getFactory().getLogGiftCardInstance();

        logGiftCard.createGiftCardNumberElement(giftCard, IXRetailConstantsIfc.ELEMENT_GIFT_CARD, parentDocument, el);
    }

}
