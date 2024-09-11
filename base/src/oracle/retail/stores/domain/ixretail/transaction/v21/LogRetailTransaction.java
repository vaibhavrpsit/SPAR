/* ===========================================================================
* Copyright (c) 2004, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/transaction/v21/LogRetailTransaction.java /main/30 2014/05/16 17:19:40 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   05/16/14 - Get order id from sale return line item in order to
 *                         avoid transaction to OrderTransactionIfc class cast
 *                         exception.
 *    mjwallac  01/30/14 - fix null dereferences
 *    abondala  09/04/13 - initialize collections
 *    rgour     04/01/13 - CBR cleanup
 *    sgu       05/09/12 - separate minimum deposit amount into xchannel part
 *                         and store order part
 *    sgu       05/04/12 - refactor OrderStatus to support store order and
 *                         xchannel order
 *    yiqzhao   04/16/12 - refactor store send from transaction totals
 *    rsnayak   03/22/12 - cross border return changes
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    rsnayak   05/13/11 - APF Send Changes
 *    npoola    09/13/10 - added the check to see if customer is null
 *    rsnayak   07/21/10 - poslog fix for kits
 *    mchellap  06/24/10 - Billpay changes
 *    acadar    06/16/10 - external order changes for poslog export
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    crain     10/24/09 - Forward Port: TAXEXEMPTED AMOUNT IS NOT LOGGED IN
 *                         POSLOG FOR TAXEXEMPTED ITEM-NONVATENVIRONMEN
 *    rkar      11/12/08 - Adds/changes for POS-RM integration
 *    rkar      11/04/08 - Added code for POS-RM integration
 *    acadar    11/03/08 - localization of transaction tax reason codes
 *    mdecama   10/28/08 - I18N - Reason Codes for Customer Types.
 *    mdecama   10/24/08 - I18N updates for Suspend Transaction Reason Codes.
 *
 * ===========================================================================
     $Log:
      18   360Commerce 1.17        5/2/2008 2:05:27 PM    Anda D. Cadar   Do
           not read layaway details if status is canceled. Code reviewed by
           Jack Swan
      17   360Commerce 1.16        6/5/2007 2:04:43 PM    Ranjan X Ojha   Code
           Review updates to POSLog for VAT
      16   360Commerce 1.15        5/22/2007 9:14:25 AM   Sandy Gu        Check
            in PosLog enhancement for VAT
      15   360Commerce 1.14        4/25/2007 10:00:44 AM  Anda D. Cadar   I18N
           merge
      14   360Commerce 1.13        4/9/2007 4:28:56 PM    Ashok.Mondal    CR
           4069 - v7.2.2 merge to trunk. Product extensibility issues for
           circuit city POSLog.

      13   360Commerce 1.12        12/29/2006 3:35:24 PM  Charles D. Baker CR
           21473 - Took a different approach than for fix in .v7x. The root
           cause was the sales associate not being set to the operator when
           the sales associate isn't specified in the transaction. The .v7x
           fix lead to an empty assocaite ID tag in the house account payment
           transaction.
      12   360Commerce 1.11        8/10/2006 11:17:00 AM  Brendan W. Farrell
           16500 -Merge fix from v7.x.  Maintain sales associate to be used in
            reporting.
      11   360Commerce 1.10        4/27/2006 7:29:47 PM   Brett J. Larsen CR
           17307 - remove inventory functionality - stage 2
      10   360Commerce 1.9         4/5/2006 2:19:55 PM    Michael Wisbauer
           Added logic to check if the ordertransaciton variable has already
           been set because it was a void transaction and it was trying to
           cast a void transaction to an order transaction
      9    360Commerce 1.8         2/13/2006 12:05:21 PM  Jason L. DeLeau
           Correct Class Cast Exception
      8    360Commerce 1.7         2/8/2006 5:20:23 PM    Jason L. DeLeau 5373:
            Make sure a pos log is generated for the post void of a redeemed
           gift card.
      7    360Commerce 1.6         2/8/2006 10:32:19 AM   Jason L. DeLeau 5373:
            Make sure a pos log is generated for the post void of a redeemed
           gift card.
      6    360Commerce 1.5         1/25/2006 4:11:29 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      5    360Commerce 1.4         1/22/2006 11:41:36 AM  Ron W. Haight
           Removed references to com.ibm.math.BigDecimal
      4    360Commerce 1.3         12/13/2005 4:43:48 PM  Barry A. Pape
           Base-lining of 7.1_LA
      3    360Commerce 1.2         3/31/2005 4:28:56 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:23:16 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:26 PM  Robert Pearse
     $: LogRetailTransaction.java,v $
      10    .v710     1.2.2.2     10/25/2005 13:23:51    Brendan W. Farrell
           Merge 702
      9    .v710     1.2.2.1     9/28/2005 11:37:16     Brendan W. Farrell
           Update per dan's cr.
      8    .v710     1.2.2.0     9/21/2005 13:39:52     Brendan W. Farrell
           Initial Check in merge 67.
      7    .v700     1.2.3.3     1/6/2006 10:46:00      Rohit Sachdeva  4123:
           For Send Customer Physically Present
      6    .v700     1.2.3.2     1/4/2006 17:45:05      Rohit Sachdeva
           4123:Customer Physically Present
      5    .v700     1.2.3.1     12/14/2005 13:19:43    Deepanshu       CR
           7527: During return, the Kit item is like a normal line item and
           KitHeader is checked for null
      4    .v700     1.2.3.0     9/29/2005 19:52:44     Jason L. DeLeau 5615:
           Correct PosLog Import/Export so that it works on oracle, and layaway
           data is transmitted on a layaway payment.
      3    360Commerce1.2         3/31/2005 15:28:56     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:23:16     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:12:26     Robert Pearse
     $
     Revision 1.8.2.5  2005/01/27 19:47:21  jdeleau
     @scr 7888 Remove unused import

     Revision 1.8.2.4  2005/01/21 22:40:57  jdeleau
     @scr 7888 merge Branch poslogconf into v700

     Revision 1.8.2.3.2.1  2005/01/20 16:37:23  jdeleau
     @scr 7888 Various POSLog fixes from mwright

     Revision 1.8.2.3  2004/11/11 22:31:47  mwright
     Merge from top of tree

     Revision 1.11  2004/11/11 11:11:08  mwright
     Removed redundant unique id from tax line item

     Revision 1.10  2004/11/02 21:40:20  mwright
     Added transaction level send flag to export
     Exported the captured customer info at the transaction level when send lines with the same data exist. This is required to get the capture customer table imported.

     Revision 1.9  2004/10/29 03:26:30  mwright
     Add change as tender line item

     Revision 1.8  2004/08/10 07:17:09  mwright
     Merge (3) with top of tree

     Revision 1.7.2.4  2004/08/06 02:32:41  mwright
     Added code to determine if transaction-level customer object is already in the send item element.
     Added element to makr customer as captured or linked

     Revision 1.7.2.3  2004/08/01 22:56:07  mwright
     Added customer email and order description for orders

     Revision 1.7.2.2  2004/07/29 01:41:38  mwright
     Moved code to add shipping method and delivery details to base class, where it is used on a per-line-item basis.
     Change customer logging to distinguish between linked and captured customers
     Added customer email and order description to order element
     Add transaction to lineitem logger calls

     Revision 1.7.2.1  2004/07/09 04:20:24  mwright
     Added support for order cancel and house payment transactions
     Added suspend reason code
     Added inventory location and state for order transaction
     Added entry method for kit members
     Set parent transaction before processing line items
     Changed the way post-voids are logged, to prevent change from being given twice

     Revision 1.7  2004/06/30 08:12:03  mwright
     Changes introduced due to new tax engine
     Changes to get customer record: tries several places, including new shipping records

     Revision 1.6  2004/06/24 09:15:09  mwright
     POSLog v2.1 (second) merge with top of tree


     Revision 1.5  2004/06/02 19:01:53  lzhao
     @scr 4670: add shippingRecords table.


     Revision 1.4.2.3  2004/06/23 00:38:10  mwright
     Set transaction type for all possible retail transactions, not just sale and return
     Add layaway creation fee to transaction grand total
     Add store ID to return without receipt line items
     Add store ID to order line items
     Changed order in which line items are created, to preserve line numbering
     Added methods to allow extended class (for post-void transactons) to modify behaviour of this class when making line items

     Revision 1.4.2.2  2004/06/10 10:55:23  mwright
     Updated to use schema types in commerce services

     Revision 1.4.2.1  2004/05/21 01:46:33  mwright
     Added optional customer info (demographic) element


     Revision 1.4  2004/05/06 03:43:36  mwright
     POSLog v2.1 merge with top of tree

     Revision 1.1.2.9  2004/05/05 02:32:52  mwright
     Refactored methods to cater for the fact that some transactions are not retail transactions, but need to call the methods in this class.

     Revision 1.1.2.8  2004/04/26 22:18:12  mwright
     Replaced ixretail customer, address and delivery elements with extended 360-specific elements.
     Parameterized methods that use the (implied) transaction, so they could be extended (e.g. by LogVoidTransaction) to use another transaction instead of the one supplied by the driving system. (e.g. the voided transaction). This led to the "standard" methods createBaseElements() and createExtendedElement() becoming tiny, and just calling the new addBaseElements(transaction) addExtendedElements(transaction) methods.

     Revision 1.1.2.7  2004/04/19 07:38:26  mwright
     Fixes after unit tests

     Revision 1.1.2.6  2004/04/13 07:32:16  mwright
     Removed tabs

     Revision 1.1.2.5  2004/03/25 22:16:35  smcgrigor
     Cleaned up TODOs

     Revision 1.1.2.4  2004/03/21 14:02:04  mwright
     Weekly checkin of work in progress: everything compiles OK, but there are several TODOs that need cleaning up

     Revision 1.1.2.3  2004/03/18 02:19:37  mwright
     Implemented use of schema type factory

     Revision 1.1.2.2  2004/03/17 04:13:50  mwright
     Initial revision for POSLog v2.1

     Revision 1.1.2.1  2004/03/17 00:20:41  mwright
     Initial revision for POSLog v2.1

 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.domain.ixretail.transaction.v21;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.ixretail.IXRetailConstantsV21Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogAmountIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogCustomerInfo360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogOperatorIDIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogRetailTransactionIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogSuspendedTransaction360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionAssociateIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionBillPayment360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionCustomer360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionCustomerOrderForDeliveryIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionDelivery360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionIRSCustomer360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionIRSPaymentHistory360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionItemIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionKitMemberIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionLayaway360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionLineItemIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionPriceDerivationRule360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionSaleForDeliveryIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionSpecialOrder360Ifc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionTaxExemptionIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionTaxIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionTaxOverrideIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionTenderIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.RetailTransactionTotalIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.TenderAuthorizationIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.TenderCustomerVerificationIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.customer.IRSCustomerIfc;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.financial.BillPayIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.financial.PaymentIfc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.discount.LogDiscountLineItemIfc;
import oracle.retail.stores.domain.ixretail.financial.LogLayawayIfc;
import oracle.retail.stores.domain.ixretail.financial.LogPaymentLineItemIfc;
import oracle.retail.stores.domain.ixretail.lineitem.LogSaleReturnLineItemIfc;
import oracle.retail.stores.domain.ixretail.tender.LogTenderLineItemIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogRetailTransactionIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderStatusIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.BillPayTransactionIfc;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSStatusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

/**
 *
 */
public class LogRetailTransaction extends LogTransaction implements LogRetailTransactionIfc
{
    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/30 $";

    protected POSLogRetailTransactionIfc retailTransactionElement = null;

    int lineItemSequenceNumber = 0;

    /**
     * Constructs LogRetailTransaction object.
     */
    public LogRetailTransaction()
    {
    }

    protected void createBaseElements() throws XMLConversionException
    {
        super.createBaseElements(); // creates trnElement

        // create the retail transaction schema type object, and add it to the
        // transaction object:
        retailTransactionElement = getSchemaTypesFactory().getPOSLogRetailTransactionInstance();
        trnElement.setRetailTransaction(retailTransactionElement);

        addBaseElements(transaction);
    }

    /**
     * This method is used to manipulate the source of the transaction being
     * logged. For a void, the voided transaction is substituted in the
     * parameter.
     *
     * @param transaction transaction reference
     * @exception XMLConversionException xml conversion exception
     */
    protected void addBaseElements(TransactionIfc transaction) throws XMLConversionException
    {
        String typeCode;

        int transactionType = transaction.getTransactionType();
        if (transactionType == TransactionIfc.TYPE_SALE)
        {
            typeCode = ENUMERATION_RETAIL_TRANSACTION_TYPE_CODE_SALE_360;
        }
        else if (transactionType == TransactionIfc.TYPE_RETURN)
        {
            typeCode = ENUMERATION_RETAIL_TRANSACTION_TYPE_CODE_RETURN_360;
        }
        else if (transactionType == TransactionIfc.TYPE_LAYAWAY_INITIATE)
        {
            typeCode = ENUMERATION_RETAIL_TRANSACTION_TYPE_CODE_LAYAWAY_INITIATE_360;
        }
        else if (transactionType == TransactionIfc.TYPE_LAYAWAY_COMPLETE)
        {
            typeCode = ENUMERATION_RETAIL_TRANSACTION_TYPE_CODE_LAYAWAY_COMPLETE_360;
        }
        else if (transactionType == TransactionIfc.TYPE_LAYAWAY_PAYMENT)
        {
            typeCode = ENUMERATION_RETAIL_TRANSACTION_TYPE_CODE_LAYAWAY_PAYMENT_360;
        }
        else if (transactionType == TransactionIfc.TYPE_LAYAWAY_DELETE)
        {
            typeCode = ENUMERATION_RETAIL_TRANSACTION_TYPE_CODE_LAYAWAY_DELETE_360;
        }
        else if (transactionType == TransactionIfc.TYPE_ORDER_INITIATE)
        {
            typeCode = ENUMERATION_RETAIL_TRANSACTION_TYPE_CODE_ORDER_INITIATE_360;
        }
        else if (transactionType == TransactionIfc.TYPE_ORDER_COMPLETE)
        {
            typeCode = ENUMERATION_RETAIL_TRANSACTION_TYPE_CODE_ORDER_COMPLETE_360;
        }
        else if (transactionType == TransactionIfc.TYPE_ORDER_PARTIAL)
        {
            typeCode = ENUMERATION_RETAIL_TRANSACTION_TYPE_CODE_ORDER_PARTIAL_360;
        }
        else if (transactionType == TransactionIfc.TYPE_ORDER_CANCEL)
        {
            typeCode = ENUMERATION_RETAIL_TRANSACTION_TYPE_CODE_ORDER_DELETE_360;
        }
        else if (transactionType == TransactionIfc.TYPE_HOUSE_PAYMENT)
        {
            typeCode = ENUMERATION_RETAIL_TRANSACTION_TYPE_CODE_HOUSE_PAYMENT_360;
        }
        else if (transactionType == TransactionIfc.TYPE_REDEEM)
        {
            typeCode = ENUMERATION_RETAIL_TRANSACTION_TYPE_CODE_REDEEM_360;
        }

        else if (transactionType == TransactionIfc.TYPE_BILL_PAY)
        {
            typeCode = ENUMERATION_RETAIL_TRANSACTION_TYPE_CODE_BILL_PAYMENT_360;
        }
        else
        {
            throw new XMLConversionException("Unmapped transaction type " + transactionType);
        }

        retailTransactionElement.setVersion(ATTRIBUTE_RETAIL_TRANSACTION_VERSION_DATA);
        retailTransactionElement.setTypeCode(typeCode);
        String status = getStatus(transaction);
        retailTransactionElement.setTransactionStatus(status);

        retailTransactionElement.setReceiptDateTime(dateValue(transaction.getTimestampEnd()));

        if (transaction instanceof BillPayTransactionIfc)
        {
            createTenderLineItemElements(((BillPayTransactionIfc)transaction).getTenderLineItemsVector().iterator(),
                    ((BillPayTransactionIfc)transaction).calculateChangeDue());
        }

        if (transaction instanceof TenderableTransactionIfc)
        {
            TenderableTransactionIfc tenderableTransaction = (TenderableTransactionIfc)transaction;
            createTotal(ATTRIBUTE_VALUE_TRANSACTION_GROSS_AMOUNT, tenderableTransaction.getTransactionTotals()
                    .getPreTaxSubtotal());
            createTotal(ATTRIBUTE_VALUE_TRANSACTION_TAX_AMOUNT, tenderableTransaction.getTransactionTotals()
                    .getTaxTotal());
            createTotal(ATTRIBUTE_VALUE_TRANSACTION_INCLUSIVE_TAX_AMOUNT_360, tenderableTransaction
                    .getTransactionTotals().getInclusiveTaxTotal());

            // v1.0 does not include layaway initiation fee.....fails row
            // comparison

            if (transactionType == TransactionIfc.TYPE_LAYAWAY_INITIATE
                    || transactionType == TransactionIfc.TYPE_LAYAWAY_COMPLETE)
            {
                if (transaction.getTransactionStatus() != TransactionConstantsIfc.STATUS_CANCELED)
                {
                    LayawayTransactionIfc layawayTrx = (LayawayTransactionIfc)transaction;
                    CurrencyIfc fee = layawayTrx.getLayaway().getCreationFee();
                    CurrencyIfc amt = tenderableTransaction.getTransactionTotals().getGrandTotal();
                    CurrencyIfc total = amt.add(fee);
                    String str = total.toString();
                    createTotal(ATTRIBUTE_VALUE_TRANSACTION_GRAND_AMOUNT, total);
                }
            }
            else
            {
                createTotal(ATTRIBUTE_VALUE_TRANSACTION_GRAND_AMOUNT, tenderableTransaction.getTransactionTotals()
                        .getGrandTotal());
            }

        }

        if (transaction instanceof SaleReturnTransactionIfc)
        {
            SaleReturnTransactionIfc retailTrans = (SaleReturnTransactionIfc)transaction; // this
                                                                                          // is
                                                                                          // a
                                                                                          // v1.0
                                                                                          // assumption

            if (!Util.isEmpty(retailTrans.getOrderID()))
            {
                retailTransactionElement.setSpecialOrderNumber(retailTrans.getOrderID());
            }

            if (retailTrans.hasExternalOrder())
            {
                retailTransactionElement.setExternalOrderID(retailTrans.getExternalOrderID());
                retailTransactionElement.setExternalOrderNumber(retailTrans.getExternalOrderNumber());
                retailTransactionElement.setLegalDocumentSignatureRequiredFlag(retailTrans.requireServiceContract());

            }

            // adds all the line item elements:
            makeLineItems(retailTrans, status);

            boolean isSendCustomerCaptured = false;
            CustomerIfc customer = retailTrans.getCustomer();
            if (customer != null)
            {
                if (!isSendCustomer(retailTrans, customer, retailTransactionElement))
                {
                    retailTransactionElement.setCapturedCustomer(new Boolean(false));
                    setCustomerDetails(customer);
                }
            }
            else
            {
                customer = retailTrans.getCaptureCustomer();
                if (customer != null)
                {
                    if (!isSendCustomer(retailTrans, customer, retailTransactionElement))
                    {
                        setCustomerDetails(customer);
                        retailTransactionElement.setCapturedCustomer(new Boolean(true));
                    }
                    else
                    {
                        isSendCustomerCaptured = true; // only true for captured
                                                       // send customers
                    }
                }
            }

            if (!isSendCustomerCaptured)
            {
                if (customer != null)
                {
                    retailTransactionElement.setCustomerSendType(ENUMERATION_CUSTOMER_SEND_TYPE_LINKED);
                }
                else
                {
                    retailTransactionElement.setCustomerSendType("");
                }
            }
            else
            {
                retailTransactionElement.setCustomerSendType(ENUMERATION_CUSTOMER_SEND_TYPE_CAPTURED);

                // we still need to export the captured customer details, even
                // if it is a send item, so PA_CT_CAPT gets updated
                setCustomerDetails(customer);
                retailTransactionElement.setCapturedCustomer(new Boolean(true));
            }

            if (retailTrans.getTransactionStatus() != TransactionIfc.STATUS_CANCELED)
            {
                setAssociateDetails(retailTrans.getSalesAssociateID());
            }

            retailTransactionElement.setDeliveryPackageCount(retailTrans.getSendPackageCount());

            if ( retailTrans.isTransactionLevelSendAssigned())
            {
                retailTransactionElement.setTransactionLevelSendAssigned(new Boolean(true));
            }

            if (retailTrans.getIRSCustomer() != null)
            {
                setIRSCustomerDetails(retailTrans.getIRSCustomer());
            }

            if (retailTrans.getAgeRestrictedDOB() != null)
            {
                retailTransactionElement.setAgeRestrictionDOB(retailTrans.getAgeRestrictedDOB().dateValue());
            }
            // CurrencyIfc inclusiveTaxTotal =
            // retailTrans.getTransactionTotals().getInclusiveTaxTotal();
            // retailTransactionElement.setInclusiveTaxTotal(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(inclusiveTaxTotal)));

            if (retailTrans.getReturnTicket() != null)
            {
                retailTransactionElement.setReturnTicket(retailTrans.getReturnTicket());
            }

            // Set Transaction currency type and country ISO code for Cross Border Return
            
           
            if(retailTrans.getCurrencyType() !=null)
            {
                retailTransactionElement.setTransactionCurrencyType(retailTrans.getCurrencyType().getCurrencyCode());
            }
            if(retailTrans.getTransactionCountryCode() !=null)
            {
                retailTransactionElement.setTransactionCountryCode(retailTrans.getTransactionCountryCode());
            }
        }

    }

    /**
     * The transaction returns a customer in getCustomer() or
     * getCaptureustomer() even when there is not one in the table, because a
     * send item (with send customer details in SHP_RDS_SLS_RTN) was specified.
     * This method determines id the customer's details are already in the send
     * item. It will return false for linked customers.
     */
    protected boolean isSendCustomer(SaleReturnTransactionIfc retailTrans, CustomerIfc customer,
            POSLogRetailTransactionIfc retailTransactionElement)
    {
        // If we have a real linked customer, the customer records may need
        // updating, even if he is linked to a send item
        // TODO changes in the customer details (during send dialog) are not
        // reflected in the customer or shipping tables
        // TODO a capture customer row is inserted when the send customer is
        // linked (the fact that I edited the address may be a factor)

        // TODO this returns false even when an existing customer is linked to
        // the send item
        if (retailTrans.isSendCustomerLinked())
        {
            return false;
        }

        // see if the transaction element contains delivery items
        RetailTransactionLineItemIfc[] lines = retailTransactionElement.getLineItem();
        if (lines != null)
        {
            String customerFirstName = customer.getFirstName();
            String customerLastName = customer.getLastName();

            for (int i = 0; i < lines.length; i++)
            {
                RetailTransactionSaleForDeliveryIfc deliveryLine = lines[i].getSaleForDelivery();
                if (deliveryLine != null)
                {
                    RetailTransactionDelivery360Ifc delivery = deliveryLine.getDelivery();
                    if (delivery != null)
                    {
                        RetailTransactionCustomer360Ifc deliveryCustomer = delivery.getCustomer();
                        if (deliveryCustomer != null)
                        {
                            String deliveryFirstName = deliveryCustomer.getFirstName();
                            String deliveryLastName = deliveryCustomer.getLastName();
                            boolean namesMatch = false;
                            if (customerFirstName != null && deliveryFirstName != null)
                            {
                                if (customerFirstName.equals(deliveryFirstName))
                                {
                                    namesMatch = true;
                                }
                            }
                            if (customerLastName != null && deliveryLastName != null)
                            {
                                if (customerLastName.equals(deliveryLastName))
                                {
                                    namesMatch = true;
                                }
                            }
                            if (namesMatch)
                            {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    protected void createExtendedElements() throws XMLConversionException
    {
        super.createExtendedElements();
        addExtendedElements(transaction);
    }

    protected void addExtendedElements(TransactionIfc transaction) throws XMLConversionException
    {
        createCustomerInfoElement(transaction.getCustomerInfo());

        if (transaction instanceof SaleReturnTransactionIfc)
        {
            SaleReturnTransactionIfc retailTrans = (SaleReturnTransactionIfc)transaction; // this
                                                                                          // is
                                                                                          // a
                                                                                          // v1.0
                                                                                          // assumption

            // create registry entry
            if (retailTrans.getDefaultRegistry() != null && !Util.isEmpty(retailTrans.getDefaultRegistry().getID()))
            {
                retailTransactionElement.setRegistryID(retailTrans.getDefaultRegistry().getID());
            }

            // if suspended transaction, capture advanced pricing rules
            int status = transaction.getTransactionStatus();
            int suspendReason = transaction.getSuspendReason() == null? CodeConstantsIfc.CODE_INTEGER_UNDEFINED
                    : Integer.parseInt(transaction.getSuspendReason().getCode());
            if (status == TransactionIfc.STATUS_SUSPENDED)
            {
                createSuspendedTransactionPricingRulesElements(retailTrans);
            }
            else if (suspendReason != CodeConstantsIfc.CODE_INTEGER_UNDEFINED)
            {
                createSuspendedTransactionReasonCode(retailTrans, suspendReason);
            }
        }

        if (transaction.getSalesAssociate() == null || transaction.getSalesAssociate().getEmployeeID() == null
                || transaction.getSalesAssociate().getEmployeeID().length() == 0)
        {
            transaction.setSalesAssociate(transaction.getCashier());
        }
        setAssociateDetails(transaction.getSalesAssociate().getEmployeeID());

        // this may move to proper elements if 2.1 schema includes it....
        if (transaction instanceof LayawayTransactionIfc)
        {
            if (transaction.getTransactionStatus() != TransactionConstantsIfc.STATUS_CANCELED)
            {
                createLayawayElements(((LayawayTransactionIfc)transaction).getLayaway());
                createPaymentHistoryElements(((LayawayTransactionIfc)transaction).getLayaway()
                        .getPaymentHistoryInfoCollection(), ENUMERATION_ACCOUNT_CODE_LAYAWAY);
            }
        }
        else if (transaction instanceof LayawayPaymentTransactionIfc)
        {
            createLayawayElements(((LayawayPaymentTransactionIfc)transaction).getLayaway());
        }
        else if (transaction instanceof OrderTransactionIfc)
        {
            createOrderElements();
            createPaymentHistoryElements(((OrderTransactionIfc)transaction).getPaymentHistoryInfoCollection(),
                    ENUMERATION_ACCOUNT_CODE_ORDER);
        }
        else if (transaction instanceof LayawayPaymentTransactionIfc)
        {
            createPaymentHistoryElements(((LayawayPaymentTransactionIfc)transaction).getLayaway()
                    .getPaymentHistoryInfoCollection(), ENUMERATION_ACCOUNT_CODE_LAYAWAY);
        }
        // Billpayment
        else if (transaction instanceof BillPayTransactionIfc)
        {
            createBillPaymentElements(((BillPayTransactionIfc)transaction).getBillPayInfo());
        }

        if (transaction instanceof TenderableTransactionIfc)
        {
            TenderableTransactionIfc tenderableTransaction = (TenderableTransactionIfc)transaction;
            // add non discounted subtotal
            createTotal(ATTRIBUTE_VALUE_TRANSACTION_NON_DISCOUNTED_GROSS_AMOUNT_360, tenderableTransaction
                    .getTransactionTotals().getSubtotal());

            // add total discount amount
            createTotal(ATTRIBUTE_VALUE_TRANSACTION_DISCOUNT_AMOUNT_360, tenderableTransaction.getTransactionTotals()
                    .getDiscountTotal());

            // add total Tender amount
            createTotal(ATTRIBUTE_VALUE_TRANSACTION_TENDER_AMOUNT_360, tenderableTransaction.getTransactionTotals()
                    .getAmountTender());
        }

    }

    /**
     * Creates elements for advanced pricing rules, which must be recorded on a
     * suspended transaction. This may or may not be necessary.
     *
     * @exception XMLConversionException thrown if error occurs
     **/
    protected void createSuspendedTransactionPricingRulesElements(SaleReturnTransactionIfc retailTrans)
            throws XMLConversionException
    {
        POSLogSuspendedTransaction360Ifc suspend = getSchemaTypesFactory().getPOSLogSuspendedTransaction360Instance();
        retailTransactionElement.setSuspendedTransaction(suspend);

        // get suspend reason code
        if (!transaction.getSuspendReason().getCode().equals(CodeConstantsIfc.CODE_UNDEFINED))
        {
            suspend.setReasonCode(transaction.getSuspendReason().getCode());
        }

        Iterator i = retailTrans.advancedPricingRules();
        if (i.hasNext())
        {
            LogDiscountLineItemIfc discountLog = IXRetailGateway.getFactory().getLogDiscountLineItemInstance();
            while (i.hasNext())
            {
                RetailTransactionPriceDerivationRule360Ifc suspendRule = getSchemaTypesFactory()
                        .getRetailTransactionPriceDerivationRuleInstance();
                discountLog.createAdvancedPricingRuleElement((AdvancedPricingRuleIfc)i.next(), parentDocument,
                        suspendRule);
                suspend.addPricingRules(suspendRule);
            }
        }
    }

    /**
     * Creates suspended transaction element, in order to store suspend reason.
     *
     * @exception XMLConversionException thrown if error occurs
     **/
    protected void createSuspendedTransactionReasonCode(SaleReturnTransactionIfc retailTrans, int suspendReason)
            throws XMLConversionException
    {
        POSLogSuspendedTransaction360Ifc suspend = getSchemaTypesFactory().getPOSLogSuspendedTransaction360Instance();
        retailTransactionElement.setSuspendedTransaction(suspend);
        suspend.setReasonCode(Integer.toString(suspendReason));
    }

    protected void createLayawayElements(LayawayIfc layaway) throws XMLConversionException
    {
        if (layaway != null)
        {
            RetailTransactionLayaway360Ifc layawayElement = getSchemaTypesFactory().getLayawayElementInstance();
            LogLayawayIfc logLayaway = IXRetailGateway.getFactory().getLogLayawayInstance();
            logLayaway.createElement(parentDocument, layawayElement, layaway);
            retailTransactionElement.setLayaway(layawayElement);
        }
    }

    /**
     * Creates entries for special order.
     *
     * @exception XMLConversionException thrown if error occurs
     **/
    protected void createOrderElements() throws XMLConversionException
    {
        // order number is v2.1 retail trx element

        OrderTransactionIfc orderTransaction = null;
        if (transaction instanceof VoidTransactionIfc)
        {
            VoidTransactionIfc voidTransaction = (VoidTransactionIfc)transaction;
            orderTransaction = (OrderTransactionIfc)voidTransaction.getOriginalTransaction();
        }
        else
        {
            orderTransaction = (OrderTransactionIfc)transaction;
        }
        OrderStatusIfc orderStatus = orderTransaction.getOrderStatus();
        EYSStatusIfc status = orderStatus.getStoreOrderStatus();

        // get current timestamp to use if necessary (this is what v1.0 does, it
        // can only be wrong!
        EYSDate useTime = DomainGateway.getFactory().getEYSDateInstance();

        // create element and add elements
        RetailTransactionSpecialOrder360Ifc order = getSchemaTypesFactory().getSpecialOrderInstance();
        retailTransactionElement.setSpecialOrder(order);

        order.setTotal(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(orderStatus.getStoreOrderTotal())));
        order.setLocation(orderStatus.getLocation());
        order.setStatus(status.statusToString());
        order.setPreviousStatus(status.statusToString(status.getPreviousStatus()));

        if (status.getLastStatusChange() == null)
        {
            order.setStatusChange(dateValue(useTime));
        }
        else
        {
            order.setStatusChange(dateValue(status.getLastStatusChange()));
        }

        order.setDepositAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(
                currency(orderStatus.getStoreOrderDepositAmount())));
        order.setMinimumDepositAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(
                currency(orderStatus.getStoreOrderMinimumDepositAmount())));
        order.setBalanceDue(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(
                currency(orderStatus.getStoreOrderBalanceDue())));
        order.setInitiatingChannel(OrderConstantsIfc.ORDER_CHANNEL_FULL_DESCRIPTORS[orderStatus.getInitiatingChannel()]);

        if (orderStatus.getTimestampBegin() == null)
        {
            order.setBeginDateTime(dateValue(useTime));
        }
        else
        {
            order.setBeginDateTime(dateValue(orderStatus.getTimestampBegin()));
        }

        if (orderStatus.getTimestampCreated() == null)
        {
            order.setCreatedDateTime(dateValue(useTime));
        }
        else
        {
            order.setCreatedDateTime(dateValue(orderStatus.getTimestampCreated()));
        }

        order.setCustomerEmail(orderTransaction.getOrderCustomerEmailAddress());
        order.setOrderDescription(orderTransaction.getOrderDescription());

        // ts_mdf_ord, dc_dy_bsn_chg - domain object problems

    }

    /**
     * Creates entries for IRS Payment History. No element is created if the
     * payment history is null or empty
     *
     * @param paymentHistoryInfoCollection List of payment history buckets
     * @param typeCode Type of payment history to create - layaway or order
     * @exception XMLConversionException thrown if error occurs
     **/
    protected void createBillPaymentElements(BillPayIfc billPayment) throws XMLConversionException
    {
        if (billPayment != null)
        {
            RetailTransactionBillPayment360Ifc paymentElement = getSchemaTypesFactory()
                    .getRetailTransactionBillPayment360Instance();
            addBillPaymentDetails(paymentElement, billPayment);
            retailTransactionElement.setBillPayment(paymentElement);
        }
    }

    /**
     * Creates entries for IRS Payment History. No element is created if the
     * payment history is null or empty
     *
     * @param paymentHistoryInfoCollection List of payment history buckets
     * @param typeCode Type of payment history to create - layaway or order
     * @exception XMLConversionException thrown if error occurs
     **/
    protected void createPaymentHistoryElements(List paymentHistoryInfoCollection, String typeCode)
            throws XMLConversionException
    {
        if (paymentHistoryInfoCollection != null && paymentHistoryInfoCollection.size() > 0)
        {
            RetailTransactionIRSPaymentHistory360Ifc paymentHistoryElement = getSchemaTypesFactory()
                    .getPaymentHistoryElementInstance();
            addPaymentHistoryDetails(paymentHistoryElement, paymentHistoryInfoCollection, typeCode);
            retailTransactionElement.setPaymentHistory(paymentHistoryElement);
        }
    }

    void addKitComponent(SaleReturnLineItemIfc srli, RetailTransactionLineItemIfc kitHeader,
            RetailTransactionLineItemIfc lineItemElement) throws XMLConversionException
    {
        RetailTransactionKitMemberIfc kitMemberElement = getSchemaTypesFactory()
                .getRetailTransactionKitMemberInstance();

        // The action is mandatory, but we don't know what it is. (See
        // KitComponent.java - no code to distinguish component type)
        // So everything defaults to "IsPartOf"
        kitMemberElement.setAction("IsPartOf");
        kitMemberElement.setSequenceNumber(lineItemElement.getSequenceNumber());
        kitMemberElement.setEntryMethod(srli.getEntryMethod().getIxRetailDescriptor());

        // we assume this value is the same as the parents item id
        // kitMemberElement.setItemCollectionID(((KitComponentLineItemIfc)srli).getItemKitID());

        // ASSUMPTION: We assume the kit header line item is of the same type as
        // the kit member.
        switch (lineItemElement.getLineItemType())
        {
        case RetailTransactionLineItemIfc.LINE_ITEM_SALE:
            kitMemberElement.setSale(lineItemElement.getSale());
            kitHeader.getSale().getLineItem().getKit().addMember(kitMemberElement);
            break;

        case RetailTransactionLineItemIfc.LINE_ITEM_SALE_FOR_DELIVERY:
            kitMemberElement.setSaleForDelivery(lineItemElement.getSaleForDelivery());
            kitHeader.getSaleForDelivery().getItem().getKit().addMember(kitMemberElement);
            break;

        case RetailTransactionLineItemIfc.LINE_ITEM_RETURN:
            kitMemberElement.setReturn(lineItemElement.getReturn());
            kitHeader.getReturn().getRetailtransactionItem().getKit().addMember(kitMemberElement);
            break;

        case RetailTransactionLineItemIfc.LINE_ITEM_CUSTOMER_ORDER_FOR_DELIVERY:
            kitMemberElement.setCustomerOrderForDelivery(lineItemElement.getCustomerOrderForDelivery());
            kitHeader.getCustomerOrderForDelivery().getItem().getKit().addMember(kitMemberElement);
            break;

        default:
            throw new XMLConversionException("Kit member with unimplemented line item type");
        }

    }

    void addLineItems(Iterator iter, boolean voidFlag, LogSaleReturnLineItemIfc logSrli) throws XMLConversionException
    {

        Map<Integer, RetailTransactionLineItemIfc> kitHeaderMap = null;
        while (iter.hasNext())
        {
            RetailTransactionLineItemIfc lineItemElement = getSchemaTypesFactory()
                    .getRetailTransactionLineItemInstance();
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)iter.next();

            logSrli.createElement(srli, transaction, parentDocument, lineItemElement, voidFlag,
                    lineItemSequenceNumber++);

            // order item needs order ID added - but it is only in the trx, not
            // in the line item

            OrderTransactionIfc orderTransaction = null;
            if (transaction instanceof VoidTransactionIfc)
            {
                VoidTransactionIfc voidTransaction = (VoidTransactionIfc)transaction;
                if (voidTransaction.getOriginalTransaction() instanceof OrderTransactionIfc)
                {
                    orderTransaction = (OrderTransactionIfc)voidTransaction.getOriginalTransaction();
                }
            }
            if (srli.isOrderItem())
            {
                RetailTransactionCustomerOrderForDeliveryIfc deliveryOrderElement = lineItemElement
                        .getCustomerOrderForDelivery();
                RetailTransactionItemIfc retailLineItem = deliveryOrderElement.getItem();
                retailLineItem.setSpecialOrderNumber(srli.getOrderID());

            }

            if (srli.isKitHeader()) // save the fact that we have just processed
                                    // a kit header, we assume the components
                                    // will follow
            {
                if (kitHeaderMap == null)
                {
                    kitHeaderMap = new HashMap<Integer, RetailTransactionLineItemIfc>(0);
                }

                kitHeaderMap.put(new Integer(srli.getKitHeaderReference()), lineItemElement);
                retailTransactionElement.addLineItem(lineItemElement);
            }
            else if (srli.isKitComponent()) // kit component line items are not
                                            // put into the normal line item
                                            // list (unlike v1.0)
            {
                if (kitHeaderMap == null)
                {
                    retailTransactionElement.addLineItem(lineItemElement);
                }
                else
                {
                    RetailTransactionLineItemIfc kitHeaderElem = kitHeaderMap.get(srli.getKitHeaderReference());
                    addKitComponent(srli, kitHeaderElem, lineItemElement);
                }
            }
            else
            // normal non-kit line item
            {
                retailTransactionElement.addLineItem(lineItemElement);
            }

        }

    }

    void makeNormalLineItems(Iterator iter) throws XMLConversionException
    {
        addLineItems(iter, false, IXRetailGateway.getFactory().getLogSaleReturnLineItemInstance());
    }

    void makeVoidedLineItems(Iterator iter) throws XMLConversionException
    {
        addLineItems(iter, true, IXRetailGateway.getFactory().getLogSaleReturnLineItemInstance());
    }

    void makeDiscountLineItems(Iterator iter) throws XMLConversionException
    {
        LogDiscountLineItemIfc discountLog = IXRetailGateway.getFactory().getLogDiscountLineItemInstance();

        while (iter.hasNext())
        {
            RetailTransactionLineItemIfc lineItemElement = getSchemaTypesFactory()
                    .getRetailTransactionLineItemInstance();
            discountLog.createElement((TransactionDiscountStrategyIfc)iter.next(), parentDocument, lineItemElement,
                    lineItemSequenceNumber++);
            retailTransactionElement.addLineItem(lineItemElement);
        }
    }

    void makePaymentLineItem(PaymentIfc payment) throws XMLConversionException
    {
        if (payment != null)
        {
            LogPaymentLineItemIfc logPayment = IXRetailGateway.getFactory().getLogPaymentLineItemInstance();
            RetailTransactionLineItemIfc lineItemElement = getSchemaTypesFactory()
                    .getRetailTransactionLineItemInstance();
            logPayment.createElement(payment, null, lineItemElement, // ensure
                                                                     // it
                                                                     // expects
                                                                     // this
                                                                     // type of
                                                                     // schema
                                                                     // type
                                                                     // element
                    lineItemSequenceNumber++);
            // the default line item only contains sequence number and void
            // flag.
            // we could add more data now (e.g. time stamps etc)
            retailTransactionElement.addLineItem(lineItemElement);
        }
    }

    /**
     * This method will be extended by LogVoidTransaction to indicate when we
     * are processing a voided transaction
     */
    boolean postVoided()
    {
        return false;
    }

    /**
     * This method is used to return the tender line items for a non-post-voided
     * transaction. It is extended in LogVoidTransaction to return the tender
     * line items for the void transaction.
     */
    Iterator getTenderLineItemsIterator(SaleReturnTransactionIfc retailTrans)
    {
        return retailTrans.getTenderLineItemsVector().iterator();
    }

    /**
     * This method return the change for a non-post-voided transaction. A return
     * transaction has a negative tender, thus no change amount.
     */
    CurrencyIfc getChangeDue(SaleReturnTransactionIfc retailTrans)
    {
        if (retailTrans.getTransactionType() == TransactionIfc.TYPE_SALE)
        {
            return retailTrans.calculateChangeDue();
        }
        return null;
    }

    /**
     * This method has been decomposed to drive worker methods that are
     * independant of the retail transaction. This allows us to use the worker
     * methods for other transactions (e.g. payment)
     */
    void makeLineItems(SaleReturnTransactionIfc retailTrans, String status) throws XMLConversionException
    {
        LogSaleReturnLineItemIfc logSrli = IXRetailGateway.getFactory().getLogSaleReturnLineItemInstance();

        // Post-voided transactions have the "real" tender lines at the

        // transaction.
        if (postVoided())
        {
            // post-voids have a change line item that is the voided
            // amount....it is not the change amount
            // this changed with new developments - postvoids no longer behave
            // the same!
            // postvoids now do not have change elements in the database (even
            // though change is given)
            CurrencyIfc changeAmount = getChangeDue(retailTrans);
            // createTenderLineItemElements(getTenderLineItemsIterator(retailTrans),
            // changeAmount);
            createTenderLineItemElements(getTenderLineItemsIterator(retailTrans), null);
        }

        // Transactions that were cleared before completion need special
        // treatment.
        // the have line items in TR_LTM_RTL_TRN with column FL_VD_LN_ITM set to
        // 0
        // However, they do not appear in retailTrans.getLineItemsIterator()!!!
        if (status.equals(ENUMERATION_TRANSACTION_STATUS_VOIDED))
        {
            // The problem is that we don't get any line items...
            // see JdbcreadTransaction.ReadTransactionForBatch() - has a
            // "do nothign" for cancelled trx.......
        }
        else
        {
            makeNormalLineItems(retailTrans.getLineItemsIterator());
        }

        makeTaxLineItems(retailTrans);

        makeDiscountLineItems(retailTrans.getItemContainerProxy().getTransactionDiscountsIterator());

        // Add the tender line items last for non-postvoided transactions
        if (!postVoided())
        {
            createTenderLineItemElements(getTenderLineItemsIterator(retailTrans), getChangeDue(retailTrans));
        }

        // v2.1 issue: payment lines are added after tender lines, to keep
        // sequence number correct
        // payment
        // pull payment from layaway or order transaction
        if (retailTrans instanceof LayawayTransactionIfc)
        {
            makePaymentLineItem(((LayawayTransactionIfc)retailTrans).getPayment());
        }
        else if (retailTrans instanceof OrderTransactionIfc)
        {
            makePaymentLineItem(((OrderTransactionIfc)retailTrans).getPayment());
        }

        // v1.0 does this BEFORE tax/discount/tender line items, so line numbers
        // in the XML are wrong.
        makeVoidedLineItems(retailTrans.getDeletedLineItems().iterator());

    }

    protected void createTenderLineItemElements(Iterator tenderLineItemsIterator, CurrencyIfc changeAmount)
            throws XMLConversionException
    {
        LogTenderLineItemIfc tenderLog = IXRetailGateway.getFactory().getLogTenderLineItemInstance();

        /*
         * IXRetail conformance issue: The IXRetail use cases specify that the
         * change (or cash back) element be part of the tender line item. The
         * 360 POS puts the change in a new line item. In order to satisfy the
         * use case, the change is DUPLICATED as a subelement in the first
         * tender element. This is ignored by the import.
         */

        // We remember the first tender line, and the change line, so we can add
        // the change to the tender
        RetailTransactionLineItemIfc firstTenderLineItem = null;
        RetailTransactionLineItemIfc changeLineItem = null;

        while (tenderLineItemsIterator.hasNext())
        {
            // The tender change line item is now included in the list of tender
            // line items.
            // we compare the change amount with the amount for each tender line
            // item to see if it is the change item
            RetailTransactionLineItemIfc lineItemElement = getSchemaTypesFactory()
                    .getRetailTransactionLineItemInstance();

            TenderLineItemIfc tenderLineItem = (TenderLineItemIfc)tenderLineItemsIterator.next();

            if (firstTenderLineItem == null)
            {
                firstTenderLineItem = lineItemElement;
            }

            if (changeAmount != null && changeAmount.equals(tenderLineItem.getAmountTender()))
            {
                changeLineItem = lineItemElement;
                tenderLog.createTenderChangeElement(changeAmount, null, lineItemElement, lineItemSequenceNumber++);
            }
            else
            {
                tenderLog.createElement(tenderLineItem, null, lineItemElement, lineItemSequenceNumber++);
            }
            retailTransactionElement.addLineItem(lineItemElement);
        }

        // Add the change to the first tender line item
        if (changeLineItem != null && firstTenderLineItem != null)
        {
            RetailTransactionTenderIfc tenderElement = firstTenderLineItem.getTender();

            POSLogAmountIfc cashback = null;

            // We add cashback elements to credit or check tenders, else we add
            // change:
            if ("Credit".equals(tenderElement.getTenderID()) || "Check".equals(tenderElement.getTenderID())
                    || "E-Check".equals(tenderElement.getTenderID()))
            {
                cashback = changeLineItem.getTender().getTenderChange()[0].getAmount();
                tenderElement.setCashback(cashback);
            }
            else
            {
                tenderElement.setTenderChange(changeLineItem.getTender().getTenderChange());
            }

            // Some use cases want all the extra elements in the tender element
            // to be present in the change element as well:
            changeLineItem.getTender().setCashback(cashback);

            TenderAuthorizationIfc[] authorizations = tenderElement.getAuthorization();
            changeLineItem.getTender().setAuthorization(authorizations);

            TenderCustomerVerificationIfc verification = tenderElement.getCustomerVerification();
            changeLineItem.getTender().setCustomerVerification(verification);
        }

    }

    void makeTaxLineItems(SaleReturnTransactionIfc retailTrans)
    {

        // taxable amount
        TransactionTaxIfc transactionTax = retailTrans.getTransactionTax();
        TransactionTotalsIfc totals = retailTrans.getTransactionTotals();

        /*
         * // we ASSUME that the first tax information element contains the
         * relevant data: TaxInformationContainerIfc container =
         * totals.getTaxInformationContainer();
         */

        /*
         * CurrencyIfc debugTaxableAmount = totals.getPreTaxSubtotal().abs();
         * int debugTaxMode = transactionTax.getTaxMode(); CurrencyIfc
         * debugTaxTotal = totals.getTaxTotal(); double debugTaxRate =
         * transactionTax.getDefaultRate() * 100.0; // this is the default rate,
         * that goes into CO_MDFR_SLS_RTN_TX.PE_TX
         * System.out.println("Tax info from transaction tax object:");
         * System.out.println("Taxable amount:  " +
         * debugTaxableAmount.toString());
         * System.out.println("Tax mode:        " + debugTaxMode);
         * System.out.println("Tax total:       " + debugTaxTotal.toString());
         * System.out.println("Tax rate:        " + debugTaxRate);
         * System.out.println("Override rate:   " +
         * transactionTax.getOverrideRate());
         * System.out.println("Override amount: " +
         * transactionTax.getOverrideAmount());
         */

        /*
         * String uniqueId = null; TaxInformationIfc[] infoArray =
         * container.getTaxInformation(); if (infoArray != null &&
         * infoArray.length > 0) { // This should be retrieved from the
         * transactionTax object uniqueId = infoArray[0].getUniqueID(); // this
         * lists one row per line item....where do we get the transaction
         * tax???? for (int i = 0; i < infoArray.length; i++) {
         * System.out.println("Tax info for array element " + (i+1) + " of " +
         * infoArray.length); System.out.println("Mode:       " +
         * infoArray[i].getTaxMode()); System.out.println("Amount:     " +
         * infoArray[i].getTaxAmount()); System.out.println("Percentage: " +
         * infoArray[i].getTaxPercentage()); } }
         */

        RetailTransactionLineItemIfc lineItemElement = getSchemaTypesFactory().getRetailTransactionLineItemInstance();
        RetailTransactionTaxIfc taxElement = getSchemaTypesFactory().getRetailTransactionTaxInstance();

        int sequenceNumber = lineItemSequenceNumber++;
        lineItemElement.setSequenceNumber(Integer.toString(sequenceNumber));
        taxElement.setSequenceNumber(Integer.toString(sequenceNumber));

        // The tax type and subType attributes are not set.
        // They could be something like
        // TaxType = "Sales"
        // TaxType = "GST" or "PST" or "HST" for Canada
        // = "VAT" for United Kingdom, or Europe
        // TaxSubType = "c360:State" "c360:County" "c360:City" "c360:Metro"

        // These were set in v1.0, may be optional here in v2.1:
        taxElement.setTaxAuthority(ELEMENT_VALUE_NOT_SUPPORTED);
        taxElement.setTaxRuleID(ELEMENT_VALUE_NOT_SUPPORTED);
        taxElement.setTaxGroupID(ELEMENT_VALUE_NOT_APPLICABLE);

        // taxable amount is entire transaction pre-tax total if transaction is
        // not tax-exempt
        CurrencyIfc taxableAmount = totals.getPreTaxSubtotal().abs();
        int taxMode = transactionTax.getTaxMode();

        if (taxMode == TaxIfc.TAX_MODE_EXEMPT)
        {
            taxableAmount.setZero();
        }

        if (taxMode != TaxIfc.TAX_MODE_EXEMPT)
        {
            taxElement.setTaxableAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(
                    currency(taxableAmount)));
            Boolean taxIncludedInTaxableAmountFlag = new Boolean(DomainGateway.getProperty("InclusiveTaxEnabled",
                    "false"));
            taxElement.setTaxIncludedInTaxableAmountFlag(taxIncludedInTaxableAmountFlag);
        }

        // tax amount
        CurrencyIfc taxTotal = totals.getTaxTotal();
        taxElement.setAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(currency(taxTotal)));

        // tax rate
        double taxRate = transactionTax.getDefaultRate() * 100.0;

        // handle override rate
        if (taxMode == TaxIfc.TAX_MODE_OVERRIDE_RATE || taxMode == TaxIfc.TAX_MODE_RETURN_RATE)
        {
            taxRate = transactionTax.getOverrideRate() * 100.0;
        }

        taxElement.setPercent(new BigDecimal(taxRate));

        if (taxMode == TaxIfc.TAX_MODE_OVERRIDE_AMOUNT || taxMode == TaxIfc.TAX_MODE_OVERRIDE_RATE)
        {
            RetailTransactionTaxOverrideIfc overrideElement = getSchemaTypesFactory()
                    .getRetailTransactionTaxOverrideInstance();

            // These two are required by the schema, but the values are already
            // in the parent element. We duplicate them here just to pass the
            // schema test.
            overrideElement.setNewTaxAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(
                    currency(taxTotal)));
            overrideElement.setNewTaxPercent(new BigDecimal(taxRate));

            // this returns the override rate, not the default rate:
            double defaultRate = transactionTax.getDefaultRate() * 100.00;
            overrideElement.setOriginalPercent(new BigDecimal(defaultRate));

            // original amount is required but not available
            overrideElement.setOriginalTaxAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(
                    currency(taxTotal)));
            overrideElement.setReasonCode(transactionTax.getReason().getCode());

            taxElement.setTaxOverride(overrideElement);
        }
        // handle tax exempt
        else if (taxMode == TaxIfc.TAX_MODE_EXEMPT)
        {
            RetailTransactionTaxExemptionIfc exemptionElement = getSchemaTypesFactory()
                    .getRetailTransactionTaxExemptionInstance();

            exemptionElement.setCustomerExemptionID(transactionTax.getTaxExemptCertificateID());
            exemptionElement.setExemptTaxAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(
                    currency(totals.getExemptTaxTotal())));
            // Note: The taxable amount was set to zero for tax-exempt mode, so
            // this will always result in a zero value for the element: (same in
            // v1.0)
            exemptionElement.setExemptTaxableAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(
                    currency(totals.getSaleSubtotal())));
            exemptionElement.setReasonCode(transactionTax.getReason().getCode());
            taxElement.setTaxExemption(exemptionElement);
        }

        taxElement.setUniqueID(IXRetailConstantsV21Ifc.ELEMENT_VALUE_NOT_APPLICABLE);
        taxElement.setTaxMode(TaxIfc.IXRETAIL_TAX_MODE_DESCRIPTOR[taxMode]);
        taxElement.setTaxScope(TaxIfc.TAX_SCOPE_DESCRIPTOR[TaxIfc.TAX_SCOPE_TRANSACTION]);
        CurrencyIfc inclusiveAmount = retailTrans.getTransactionTotals().getInclusiveTaxTotal();
        taxElement.setInclusiveAmount(getSchemaTypesFactory().getPOSLogAmountInstance().initialize(
                currency(inclusiveAmount)));

        lineItemElement.setTax(taxElement);
        retailTransactionElement.addLineItem(lineItemElement);

    }

    void createTotal(String attribute, CurrencyIfc amount)
    {
        RetailTransactionTotalIfc totalElement = getSchemaTypesFactory().getRetailTransactionTotalInstance();
        retailTransactionElement.addTotal(totalElement.initialize(attribute, currency(amount)));
    }

    void setAssociateDetails(String operatorID)
    {
        RetailTransactionAssociateIfc associate = getSchemaTypesFactory().getRetailTransactionAssociateInstance();
        POSLogOperatorIDIfc operator = getSchemaTypesFactory().getPOSLogOperatorIDInstance();

        operator.setOperatorID(operatorID);
        associate.setAssociateID(operator);

        retailTransactionElement.setAssociate(new RetailTransactionAssociateIfc[] { associate });
    }

    void setCustomerDetails(CustomerIfc customer) throws XMLConversionException
    {
        if (customer != null)
        {
            RetailTransactionCustomer360Ifc customerElement = getSchemaTypesFactory()
                    .getRetailTransactionCustomerInstance();
            addCustomerDetails(customerElement, customer);
            retailTransactionElement.setCustomer(customerElement);
        }
    }

    /**
     * This method is used to set irs customer details
     *
     * @param irsCustomer irs customer reference
     * @throws XMLConversionException xml conversion exception
     **/
    void setIRSCustomerDetails(IRSCustomerIfc irsCustomer) throws XMLConversionException
    {
        if (irsCustomer != null)
        {
            RetailTransactionIRSCustomer360Ifc irsCustomerElement = getSchemaTypesFactory()
                    .getRetailTransactionIRSCustomerInstance();
            addIRSCustomerDetails(irsCustomerElement, irsCustomer);
            retailTransactionElement.setIRSCustomer(irsCustomerElement);
        }
    }

    void createCustomerInfoElement(CustomerInfoIfc customerInfo)
    {
        if (customerInfo != null)
        {
            POSLogCustomerInfo360Ifc info = getSchemaTypesFactory().getPOSLogCustomerInfo360Instance();

            switch (customerInfo.getCustomerInfoType())
            {
            case CustomerInfoIfc.CUSTOMER_INFO_TYPE_PHONE_NUMBER:
                info.setInfoType(ENUMERATION_INFO_TYPE_VALUE_PHONE_NUMBER_360);
                break;

            case CustomerInfoIfc.CUSTOMER_INFO_TYPE_POSTAL_CODE:
                info.setInfoType(ENUMERATION_INFO_TYPE_VALUE_POSTAL_CODE_360);
                break;

            default:
                info.setInfoType(ENUMERATION_INFO_TYPE_VALUE_NONE_360);
            }
            info.setCustomerData(customerInfo.getCustomerInfo()); // may be null
                                                                  // if no
                                                                  // customer
                                                                  // data
                                                                  // present

            // If an id had been entered for a return, log it.
            if (customerInfo.getPersonalID().getEncryptedNumber() != null)
            {
                info.setPersonalIDType(customerInfo.getLocalizedPersonalIDType().getCode());
                info.setEncryptedPersonalIDNumber(customerInfo.getPersonalID().getEncryptedNumber());
                info.setMaskedPersonalIDNumber(customerInfo.getPersonalID().getMaskedNumber());
                info.setPersonalIDState(customerInfo.getPersonalIDState());
                info.setPersonalIDCountry(customerInfo.getPersonalIDCountry());
            }

            retailTransactionElement.setCustomerInfo360(info);
        }
    }
}