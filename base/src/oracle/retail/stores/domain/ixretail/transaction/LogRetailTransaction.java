/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/transaction/LogRetailTransaction.java /main/16 2012/05/11 14:47:09 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       05/09/12 - separate minimum deposit amount into xchannel part
 *                         and store order part
 *    sgu       05/04/12 - refactor OrderStatus to support store order and
 *                         xchannel order
 *    asinton   03/21/12 - update CustomerIfc to use collections generics (i.e.
 *                         List<AddressIfc>) and remove old deprecated methods
 *                         and references to them
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    crain     10/24/09 - Forward Port: TAXEXEMPTED AMOUNT IS NOT LOGGED IN
 *                         POSLOG FOR TAXEXEMPTED ITEM-NONVATENVIRONMEN
 *    acadar    11/03/08 - localization of transaction tax reason codes
 *    mdecama   10/24/08 - I18N updates for Suspend Transaction Reason Codes.
 *
 * ===========================================================================
     $Log:
      4    360Commerce 1.3         4/25/2007 10:00:44 AM  Anda D. Cadar   I18N
           merge
      3    360Commerce 1.2         3/31/2005 4:28:56 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:23:16 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:26 PM  Robert Pearse
     $
     Revision 1.4  2004/06/02 19:01:53  lzhao
     @scr 4670: add shippingRecords table.

     Revision 1.3  2004/02/12 17:13:48  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:26:32  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:36:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Jul 29 2003 10:09:10   jgs
 * Modified to use the correct transaction reference for totals export.
 * Resolution for 3284: NullPointer Translation Error in log for POSLog
 *
 *    Rev 1.2   Jul 01 2003 14:09:26   jgs
 * Modifications for new 6.0 data.
 * Resolution for 1157: Add task for Importing IX Retail Transactions.
 *
 *    Rev 1.1   Jan 22 2003 10:00:24   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.0   Sep 05 2002 11:12:58   msg
 * Initial revision.
 *
 *    Rev 1.11   Aug 29 2002 14:29:24   vpn-mpm
 * Modified to support cancelled order transactions.
 *
 *    Rev 1.10   Aug 18 2002 18:53:18   vpn-mpm
 * Modified to support presale-issued gift cards.
 *
 *    Rev 1.9   Aug 11 2002 18:46:10   vpn-mpm
 * Modified createLineItemElements signature to facilitate extensibility.
 *
 *    Rev 1.8   May 27 2002 16:59:08   mpm
 * Modified naming convention for type constants.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.7   May 13 2002 19:04:14   mpm
 * Added more columns to order; add support for deleted items (line voids).
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.6   Apr 30 2002 17:55:46   mpm
 * Added support for store-open.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.5   Apr 28 2002 13:32:12   mpm
 * Completed translation of sale transactions.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.4   Apr 26 2002 07:49:02   mpm
 * Modified to set line-item-type attribute.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.3   Apr 25 2002 10:28:00   mpm
 * Added support for first name, last name.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.2   Apr 25 2002 09:00:46   mpm
 * Completed handling of basic sale transactions.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.1   Apr 22 2002 19:32:40   mpm
 * Additional TLog work
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   Apr 21 2002 15:24:38   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.domain.ixretail.transaction;
// java imports
import java.util.Iterator;

import org.w3c.dom.Element;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.financial.PaymentIfc;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.discount.LogDiscountLineItemIfc;
import oracle.retail.stores.domain.ixretail.financial.LogPaymentLineItemIfc;
import oracle.retail.stores.domain.ixretail.lineitem.LogLineItemIfc;
import oracle.retail.stores.domain.ixretail.utility.LogAddressIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderStatusIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSStatusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

//--------------------------------------------------------------------------
/**
    This class creates the TLog in IXRetail format for the Retail Transaction
    View.
    @version $Revision: /main/16 $
**/
//--------------------------------------------------------------------------
public class LogRetailTransaction
extends LogTransaction
implements LogRetailTransactionIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /main/16 $";
    /**
        sale return transaction reference
    **/
    protected SaleReturnTransactionIfc saleReturnTransaction = null;

    //----------------------------------------------------------------------------
    /**
        Constructs LogRetailTransaction object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogRetailTransaction()
    {                                   // begin LogRetailTransaction()
        elementType =
          IXRetailConstantsIfc.TYPE_POS_360_RETAIL_TRANSACTION_ELEMENT;
        hasVersion = true;
    }                                   // end LogRetailTransaction()

    //---------------------------------------------------------------------
    /**
       Create transaction elements and append to document.
       @param transaction TransactionIfc object
    **/
    //---------------------------------------------------------------------
    protected void createElements(TransactionIfc transaction)
    throws XMLConversionException
    {                                   // begin createElements()
        // set reference to SaleReturnTransactionIfc
        saleReturnTransaction = (SaleReturnTransactionIfc) transaction;

        // add special order number
        if (!Util.isEmpty(saleReturnTransaction.getOrderID()))
        {
            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_SPECIAL_ORDER_NUMBER,
               saleReturnTransaction.getOrderID());
        }
        // add suspend flag
        boolean suspendFlag = false;
        if (transaction.getTransactionStatus() == TransactionIfc.STATUS_SUSPENDED)
        {
            suspendFlag = true;
        }
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_SUSPEND_FLAG,
           suspendFlag);
        // add receipt date/time
        // Technically, this should be the time printed on the receipt.
        // However, that is not currently supported in the application.
        createTimestampTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_RECEIPT_DATE_TIME,
           transaction.getTimestampEnd());

        // add retail transaction elements
        int sequenceNumber =
          createLineItemElements(saleReturnTransaction);

        // add voided line items
        int numberOfElements = createVoidedLineItemElements
          (saleReturnTransaction.getDeletedLineItems().iterator(),
           sequenceNumber);
        sequenceNumber += numberOfElements;

        // create tax line item elements
        numberOfElements = createTaxLineItemElements(sequenceNumber);
        sequenceNumber += numberOfElements;

        // create discount line item elements
        numberOfElements = createDiscountLineItemElements(sequenceNumber);
        sequenceNumber += numberOfElements;

        // create payments line items, if needed
        numberOfElements = createPaymentElements(saleReturnTransaction,
                                                 sequenceNumber);

        // increment sequence number
        sequenceNumber += numberOfElements;

        // create tender line item elements
        numberOfElements = createTenderLineItemElements(saleReturnTransaction,
                                                        sequenceNumber);
        sequenceNumber += numberOfElements;

        // add gross totals
        createTotalElement(IXRetailConstantsIfc.ATTRIBUTE_VALUE_TRANSACTION_GROSS_AMOUNT,
                           saleReturnTransaction.getTransactionTotals().getPreTaxSubtotal(), false);
        // add tax totals
        createTotalElement(IXRetailConstantsIfc.ATTRIBUTE_VALUE_TRANSACTION_TAX_AMOUNT,
                           saleReturnTransaction.getTransactionTotals().getTaxTotal(), false);
        // add grand totals
        createTotalElement(IXRetailConstantsIfc.ATTRIBUTE_VALUE_TRANSACTION_GRAND_AMOUNT,
                           saleReturnTransaction.getTransactionTotals().getGrandTotal(), false);

        // If the transaction contains shipping items create delivery element
        /*
         * TODO: getShippingMethod() is deprecated since 7.0 for multiple send. Instead, use
         * sendShippingMethods.get(index) to get one of shippingMethod of the transaction.
         * The following code need to be updated based on the changes.
         */
        /*if (!Util.isEmpty(saleReturnTransaction.getTransactionTotals().getShippingMethod().getShippingType()) &&
            transaction.getTransactionStatus() != TransactionIfc.STATUS_SUSPENDED)
        {
            // Note:  Currently, 360Store POS supports only one destination
            // per transaction, even though multiple address lines are written
            // to the database.  Therefore, we will treat the delivery element
            // as a one-per-transaction item.  IXRetail also supports a delivery
            // element at the line-item level.
            createDeliveryElement();
        }*/

        // create customer element, if needed
        if (saleReturnTransaction.getCustomer() != null)
        {
            createCustomerElement(saleReturnTransaction.getCustomer());
        }

        // create associate entry
        createAssociateElement();

    }                                   // end createElements()

    //---------------------------------------------------------------------
    /**
       Create transaction elements for 360 extensions and append to document.
       @param transaction TransactionIfc object
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createExtendedElements(TransactionIfc transaction)
    throws XMLConversionException
    {                                   // begin createExtendedElements()
        super.createExtendedElements(transaction);

        if (transaction instanceof LayawayTransactionIfc)
        {
            createLayawayElements
              (((LayawayTransactionIfc) transaction).getLayaway());
        }
        else if (transaction instanceof OrderTransactionIfc)
        {
            createOrderElements(transaction);
        }

        if (transaction instanceof SaleReturnTransactionIfc)
        {
            // create registry entry
            createRegistryElements();

            // get suspend reason code
            if (transaction.getSuspendReason() == null ||
               !transaction.getSuspendReason() .getCode().equals (CodeConstantsIfc.CODE_UNDEFINED))
            {
                createTextNodeElement
                  (IXRetailConstantsIfc.ELEMENT_SUSPEND_REASON_CODE,
                   transaction.getSuspendReason().getCode());
            }

            // if suspended transaction, capture advanced pricing rules
            if (transaction.getTransactionStatus() == TransactionIfc.STATUS_SUSPENDED)
            {
                createSuspendedTransactionPricingRulesElements();
            }

        }

        if (transaction instanceof TenderableTransactionIfc)
        {
            TenderableTransactionIfc tenerableTransaction = (TenderableTransactionIfc)transaction;
            // add non discounted subtotal
            createTotalElement(IXRetailConstantsIfc.ATTRIBUTE_VALUE_TRANSACTION_NON_DISCOUNTED_GROSS_AMOUNT,
                               tenerableTransaction.getTransactionTotals().getSubtotal(), true);

            // add total discount amount
            createTotalElement(IXRetailConstantsIfc.ATTRIBUTE_VALUE_TRANSACTION_DISCOUNT_AMOUNT,
                               tenerableTransaction.getTransactionTotals().getDiscountTotal(), true);

            // add total Tender amount
            createTotalElement(IXRetailConstantsIfc.ATTRIBUTE_VALUE_TRANSACTION_TENDER_AMOUNT,
                               tenerableTransaction.getTransactionTotals().getAmountTender(), true);
        }

    }                                   // end createExtendedElements()

    //---------------------------------------------------------------------
    /**
       Creates and adds elements for transaction tax and tax exempt.
       These elements will be of the RetailTransactionTax360 type. <P>
       @param sequenceNumber line item sequence number
       @return number of elements created
       @exception XMLConversionException if error occurs translating to XML
    **/
    //---------------------------------------------------------------------
    protected int createTaxLineItemElements(int sequenceNumber)
    throws XMLConversionException
    {                                   // begin createTaxLineItemElements()

        // create line item element
        Element lineItemElement =
          createLineItemElement(sequenceNumber,
                                IXRetailConstantsIfc.ELEMENT_TAX);
        // create tax element and identify as 360
        Element taxElement = parentDocument.createElement
          (IXRetailConstantsIfc.ELEMENT_TAX);
        taxElement.setAttribute
          (IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_TYPE_TAG,
           IXRetailConstantsIfc.TYPE_TAX_360);

        // authority, rule ID not supported
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AUTHORITY_ID,
           IXRetailConstantsIfc.ELEMENT_VALUE_NOT_SUPPORTED,
           taxElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_RULE_ID,
           IXRetailConstantsIfc.ELEMENT_VALUE_NOT_SUPPORTED,
           taxElement);

        // tax group not applicable in this context
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_TAX_GROUP_ID,
           IXRetailConstantsIfc.ELEMENT_VALUE_NOT_APPLICABLE,
           taxElement);

        // taxable amount
        TransactionTaxIfc transactionTax =
          saleReturnTransaction.getTransactionTax();
        TransactionTotalsIfc totals =
          saleReturnTransaction.getTransactionTotals();
        // taxable amount is entire transaction pre-tax total if transaction
        // is not tax-exempt
        CurrencyIfc taxableAmount = totals.getPreTaxSubtotal().abs();
        int taxMode = transactionTax.getTaxMode();
        if (taxMode == TaxIfc.TAX_MODE_EXEMPT)
        {
            taxableAmount.setZero();
        }
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_TAXABLE_AMOUNT,
           taxableAmount,
           taxElement);

        // tax amount
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_AMOUNT,
           totals.getTaxTotal(),
           taxElement);

        // tax rate
        String taxRateString = Double.toString(transactionTax.getDefaultRate() * 100.0);
        // handle override rate
        if (taxMode == TaxIfc.TAX_MODE_OVERRIDE_RATE ||
            taxMode == TaxIfc.TAX_MODE_RETURN_RATE)
        {
            taxRateString = Double.toString(transactionTax.getOverrideRate() * 100.0);
        }

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_PERCENT,
           taxRateString,
           taxElement);

        if (taxMode == TaxIfc.TAX_MODE_OVERRIDE_AMOUNT ||
            taxMode == TaxIfc.TAX_MODE_OVERRIDE_RATE)
        {
            Element overrideElement = parentDocument.createElement
              (IXRetailConstantsIfc.ELEMENT_TAX_OVERRIDE);

            overrideElement.setAttribute
              (IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_TYPE_TAG,
               IXRetailConstantsIfc.TYPE_TAX_OVERRIDE_360);

            // override ID, original rule ID not supported
            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_TAX_OVERRIDE_ID,
               IXRetailConstantsIfc.ELEMENT_VALUE_NOT_SUPPORTED,
               overrideElement);

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_ORIGINAL_TAX_RULE_ID,
               IXRetailConstantsIfc.ELEMENT_VALUE_NOT_SUPPORTED,
               overrideElement);

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_ORIGINAL_TAX_PERCENTAGE,
               Double.toString(transactionTax.getDefaultRate() * 100.00),
               overrideElement);

            // original amount is required but not available
            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_ORIGINAL_TAX_AMOUNT,
               taxableAmount,
               overrideElement);

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_REASON_CODE,
               transactionTax.getReason().getCode(),
               overrideElement);

            taxElement.appendChild(overrideElement);
        }
        // handle tax exempt
        else if (taxMode == TaxIfc.TAX_MODE_EXEMPT)
        {
            Element exemptElement = parentDocument.createElement
              (IXRetailConstantsIfc.ELEMENT_TAX_EXEMPTION);

            // get certificate ID
            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_CUSTOMER_EXEMPTION_ID,
               transactionTax.getTaxExemptCertificateID(),
               exemptElement);

            // this is required but not available
            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_EXEMPT_TAX_AMOUNT,
               totals.getExemptTaxTotal(),
               exemptElement);

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_EXEMPT_TAXABLE_AMOUNT,
               totals.getSaleSubtotal(),
               exemptElement);

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_REASON_CODE,
               transactionTax.getReason().getCode(),
               exemptElement);

            taxElement.appendChild(exemptElement);
        }

        // add 360 elements for mode, scope
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_TAX_MODE,
           TaxIfc.IXRETAIL_TAX_MODE_DESCRIPTOR[transactionTax.getTaxMode()],
           taxElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_TAX_SCOPE,
           TaxIfc.TAX_SCOPE_DESCRIPTOR[TaxIfc.TAX_SCOPE_TRANSACTION],
           taxElement);

        lineItemElement.appendChild(taxElement);

        parentElement.appendChild(lineItemElement);

        // only one element created (at this time)
        return(1);

    }                                   // end createTaxLineItemElements()


    //---------------------------------------------------------------------
    /**
       Creates and adds elements for discount line items. <P>
       @param sequenceNumber sequence number after line items added
       @return number of elements created
       @exception XMLConversionException if error occurs translating to XML
    **/
    //---------------------------------------------------------------------
    protected int createDiscountLineItemElements(int sequenceNumber)
    throws XMLConversionException
    {                                   // begin createDiscountLineItemElements()
        int numberOfElements = 0;

        // get iterator for discounts
        Iterator i = saleReturnTransaction.getItemContainerProxy().
          getTransactionDiscountsIterator();

        // get discount line items facility
        LogDiscountLineItemIfc discountLog =
          IXRetailGateway.getFactory().getLogDiscountLineItemInstance();

        while (i.hasNext())
        {
            discountLog.createElement((TransactionDiscountStrategyIfc) i.next(),
                                      parentDocument,
                                      parentElement,
                                      sequenceNumber++);
            numberOfElements++;
        }

        return(numberOfElements);

    }                                   // end createDiscountLineItemElements()

    //---------------------------------------------------------------------
    /**
       Creates RetailTransactionDelivery360 element.
       @exception XMLConversionException thrown if error occurs translating to XML
    **/
    //---------------------------------------------------------------------
    protected void createDeliveryElement()
    throws XMLConversionException
    {                                   // begin createDeliveryElement()
        Element deliveryElement =
          parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_DELIVERY);

        deliveryElement.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_TYPE_TAG,
                                     IXRetailConstantsIfc.TYPE_DELIVERY_360);

        CustomerIfc customer = saleReturnTransaction.getCustomer();
        // create name element
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_NAME,
           customer.getFirstLastName(),
           deliveryElement);

        // get address information
        createAddressElement(customer,
                             deliveryElement);

        /*
         * TODO: getShippingMethod() is deprecated since 7.0 for multiple send. Instead, use
         * sendShippingMethods.get(index) to get one of shippingMethod of the transaction.
         * The following code need to be updated based on the changes.
         */
        /*ShippingMethodIfc shipping =
          saleReturnTransaction.getTransactionTotals().getShippingMethod();
        // create method element
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_METHOD,
           shipping.getShippingCarrier(),
           deliveryElement);

        // create instructions element
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_NOTES,
           shipping.getShippingInstructions(),
           deliveryElement);

        // create charges element
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_SHIPPING_CHARGES,
           saleReturnTransaction.getTransactionTotals().
             getCalculatedShippingCharge(),
           deliveryElement);

        // create shipping type element
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_SHIPPING_TYPE,
           shipping.getShippingType(),
           deliveryElement);
           */

        // create first name element
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_FIRST_NAME,
           customer.getFirstName(),
           deliveryElement);

        // create last name element
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_LAST_NAME,
           customer.getLastName(),
           deliveryElement);

        parentElement.appendChild(deliveryElement);

    }                                   // end createDeliveryElement()

    //---------------------------------------------------------------------
    /**
       Creates RetailTransactionAssociate element.
       @exception XMLConversionException thrown if error occurs translating to XML
    **/
    //---------------------------------------------------------------------
    protected void createAssociateElement()
    throws XMLConversionException
    {                                   // begin createAssociateElement()
        // canceled transaction doesn't have a sales associate
        if (saleReturnTransaction.getTransactionStatus() !=
             TransactionIfc.STATUS_CANCELED)
        {
            Element associateElement =
              parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_ASSOCIATE);
            associateElement.setAttribute
              (IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_TYPE_TAG,
               IXRetailConstantsIfc.TYPE_RETAIL_TRANSACTION_ASSOCIATE_360);

            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_ASSOCIATE_ID,
               saleReturnTransaction.getSalesAssociateID(),
               associateElement);

            parentElement.appendChild(associateElement);
        }
    }                                   // end createAssociateElement()

    //---------------------------------------------------------------------
    /**
       Create element for address. <P>
       @param customer customer object
       @param el parent element
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createAddressElement(CustomerIfc customer,
                                        Element el)
    throws XMLConversionException
    {                                   // begin createAddressElement()
        LogAddressIfc logAddress =
          IXRetailGateway.getFactory().getLogAddressInstance();
        logAddress.createElement((AddressIfc) customer.getAddressList().get(0),
                                 parentDocument,
                                 el);
    }                                   // end createAddressElement()

    //---------------------------------------------------------------------
    /**
       Creates and returns an element for a line item.
       @param sequenceNumber sequence number
       @param itemType item type
       @return line item element
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected Element createLineItemElement(int sequenceNumber,
                                            String itemType)
    throws XMLConversionException
    {                                   // begin createLineItemElement()
        LogLineItemIfc logLineItem =
          IXRetailGateway.getFactory().getLogLineItemInstance();
        return(logLineItem.createElement(parentDocument,
                                         parentElement,
                                         itemType,
                                         sequenceNumber));
    }                                   // end createLineItemElement()

    //---------------------------------------------------------------------
    /**
       Creates elements for gift registry, if needed. <P>
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createRegistryElements()
    throws XMLConversionException
    {                                   // begin createRegistryElements()

        if (saleReturnTransaction != null &&
            saleReturnTransaction.getDefaultRegistry() != null &&
            !Util.isEmpty(saleReturnTransaction.getDefaultRegistry().getID()))
        {
            createTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_REGISTRY_ID,
               saleReturnTransaction.getDefaultRegistry().getID());
        }
    }                                   // end createRegistryElements()

    //---------------------------------------------------------------------
    /**
       Creates elements for advanced pricing rules, which must be recorded
       on a suspended transaction.  This may or may not be necessary.
       @param transaction transaction object
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createSuspendedTransactionPricingRulesElements()
    throws XMLConversionException
    {                                   // begin createSuspendedTransactionPricingRulesElements()
        Iterator i = saleReturnTransaction.advancedPricingRules();
        // if rules exist, set up element
        Element suspendRules = null;
        LogDiscountLineItemIfc discountLog = null;
        if (i.hasNext())
        {
             // get discount line items facility
            discountLog =
              IXRetailGateway.getFactory().getLogDiscountLineItemInstance();

           suspendRules = parentDocument.createElement
              (IXRetailConstantsIfc.ELEMENT_SUSPENDED_TRANSACTION_PRICING_RULES);
        }
        // iterate through rules
        while (i.hasNext())
        {
            discountLog.createAdvancedPricingRuleElement
              ((AdvancedPricingRuleIfc) i.next(),
               parentDocument,
               suspendRules);
        }

        if (suspendRules != null)
        {
            parentElement.appendChild(suspendRules);
        }

    }                                   // end createSuspendedTransactionPricingRulesElements()

    //---------------------------------------------------------------------
    /**
       Creates line items for layaway payments, if needed.
       @param srt sale return transaction
       @param sequenceNumber sequence number
       @param numberOfElements number of elements created
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected int createPaymentElements(SaleReturnTransactionIfc srt,
                                         int sequenceNumber)
    throws XMLConversionException
    {                                   // begin createPaymentElements()
        // pull payment from layaway or order transaction
        PaymentIfc payment = null;

        if (srt instanceof LayawayTransactionIfc)
        {
            payment =
              ((LayawayTransactionIfc) srt).getPayment();
        }
        else if (srt instanceof OrderTransactionIfc)
        {
            payment =
              ((OrderTransactionIfc) srt).getPayment();
        }

        int numberOfElements = 0;
        if (payment != null)
        {
            LogPaymentLineItemIfc logPayment =
              IXRetailGateway.getFactory().getLogPaymentLineItemInstance();
            logPayment.createElement(payment,
                                     parentDocument,
                                     parentElement,
                                    sequenceNumber);
            numberOfElements = 1;
        }

        return(numberOfElements);
    }                                   // end createPaymentElements()

    //---------------------------------------------------------------------
    /**
       Creates entries for special order.
       @param transaction transaction containing order
       @return order element
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected Element createOrderElements(TransactionIfc transaction)
    throws XMLConversionException
    {                                   // begin createOrderElements()
        OrderStatusIfc orderStatus =
          ((OrderTransactionIfc) transaction).getOrderStatus();

        // get current timestamp to use if necessary
        EYSDate useTime = DomainGateway.getFactory().getEYSDateInstance();

        // create element and add elements
        Element orderElement = parentDocument.createElement
          (IXRetailConstantsIfc.ELEMENT_ORDER);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_TOTAL,
           orderStatus.getStoreOrderTotal(),
           orderElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_LOCATION,
           orderStatus.getLocation(),
           orderElement);

        EYSStatusIfc status = orderStatus.getStoreOrderStatus();

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_STATUS,
           status.statusToString(),
           orderElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_PREVIOUS_STATUS,
           status.statusToString(status.getPreviousStatus()),
           orderElement);

        if (status.getLastStatusChange() == null)
        {
            createTimestampTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_STATUS_CHANGE,
               useTime,
               orderElement);
        }
        else
        {
            createTimestampTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_STATUS_CHANGE,
               status.getLastStatusChange(),
               orderElement);
        }

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_DEPOSIT_AMOUNT,
           orderStatus.getStoreOrderDepositAmount(),
           orderElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_MINIMUM_DEPOSIT_AMOUNT,
           orderStatus.getStoreOrderMinimumDepositAmount(),
           orderElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_BALANCE_DUE,
           orderStatus.getStoreOrderBalanceDue(),
           orderElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_INITIATING_CHANNEL,
           OrderConstantsIfc.ORDER_CHANNEL_FULL_DESCRIPTORS[orderStatus.getInitiatingChannel()],
           orderElement);

        if (orderStatus.getTimestampBegin() == null)
        {
            createTimestampTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_BEGIN_DATE_TIME,
               useTime,
               orderElement);
        }
        else
        {
            createTimestampTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_BEGIN_DATE_TIME,
               orderStatus.getTimestampBegin(),
               orderElement);
        }

        if (orderStatus.getTimestampCreated() == null)
        {
            createTimestampTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_CREATED_DATE_TIME,
               useTime,
               orderElement);
        }
        else
        {
            createTimestampTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_CREATED_DATE_TIME,
               orderStatus.getTimestampCreated(),
               orderElement);
        }

        // append to parent
        parentElement.appendChild(orderElement);

        // pass back created element
        return(orderElement);

    }                                   // end createOrderElements()

    //---------------------------------------------------------------------
    /**
       Creates entries for special order.
       @param orderStatus order status object
       @exception XMLConversionException thrown if error occurs
       @deprecated As of release 5.1.0, replaced by createOrderElements(transaction)
    **/
    //---------------------------------------------------------------------
    protected void createOrderElements(OrderStatusIfc orderStatus)
    throws XMLConversionException
    {                                   // begin createOrderElements()
        // create element and add elements
        Element orderElement = parentDocument.createElement
          (IXRetailConstantsIfc.ELEMENT_ORDER);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_TOTAL,
           orderStatus.getStoreOrderTotal(),
           orderElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_LOCATION,
           orderStatus.getLocation(),
           orderElement);

        EYSStatusIfc status = orderStatus.getStoreOrderStatus();
        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_STATUS,
           status.statusToString(),
           orderElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_PREVIOUS_STATUS,
           status.statusToString(status.getPreviousStatus()),
           orderElement);

        createTimestampTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_STATUS_CHANGE,
           status.getLastStatusChange(),
           orderElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_DEPOSIT_AMOUNT,
           orderStatus.getStoreOrderDepositAmount(),
           orderElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_MINIMUM_DEPOSIT_AMOUNT,
           orderStatus.getStoreOrderMinimumDepositAmount(),
           orderElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_BALANCE_DUE,
           orderStatus.getStoreOrderBalanceDue(),
           orderElement);

        createTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_INITIATING_CHANNEL,
           OrderConstantsIfc.ORDER_CHANNEL_FULL_DESCRIPTORS[orderStatus.getInitiatingChannel()],
           orderElement);

        createTimestampTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_BEGIN_DATE_TIME,
           orderStatus.getTimestampBegin(),
           orderElement);

        createTimestampTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_CREATED_DATE_TIME,
           orderStatus.getTimestampCreated(),
           orderElement);

        // append to parent
        parentElement.appendChild(orderElement);

    }                                   // end createOrderElements()


}
