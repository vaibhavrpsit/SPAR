/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/transaction/v21/LogVoidTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:09 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rrkohli   10/01/10 - FIX TO ADD POST VOID REASON CODE TO POS LOG
 *    mchellap  06/24/10 - Billpay changes
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    7    360Commerce 1.6         4/25/2007 10:00:42 AM  Anda D. Cadar   I18N
 *         merge
 *    6    360Commerce 1.5         5/12/2006 5:26:30 PM   Charles D. Baker
 *         Merging with v1_0_0_53 of Returns Managament
 *    5    360Commerce 1.4         2/9/2006 11:52:43 AM   Jason L. DeLeau 5883:
 *          Make sure a poslog is generate for the post void of a layaway
 *         payment.
 *    4    360Commerce 1.3         2/8/2006 10:32:20 AM   Jason L. DeLeau 5373:
 *          Make sure a pos log is generated for the post void of a redeemed
 *         gift card.
 *    3    360Commerce 1.2         3/31/2005 4:28:57 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:19 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:27 PM  Robert Pearse
 *
 *   Revision 1.3  2004/06/24 09:15:09  mwright
 *   POSLog v2.1 (second) merge with top of tree
 *
 *   Revision 1.2.2.2  2004/06/23 00:43:14  mwright
 *   Added methods to get the tender and change lines for a post-voiding transaction
 *
 *   Revision 1.2.2.1  2004/06/10 10:55:23  mwright
 *   Updated to use schema types in commerce services
 *
 *   Revision 1.2  2004/05/06 03:21:07  mwright
 *   Initial revision for POSLog v2.1 merge with top of tree
 *
 *   Revision 1.1.2.1  2004/04/26 22:20:47  mwright
 *   Initial revision for v2.1
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.transaction.v21;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import oracle.retail.stores.domain.ixretail.transaction.LogVoidTransactionIfc;
import oracle.retail.stores.domain.transaction.BillPayTransactionIfc;
import oracle.retail.stores.domain.transaction.PaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.RedeemTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogTransactionLinkIfc;

/**
 * This class uses the existing RetailTransaction logger to do most of the work of logging a voided transaction.
 * A transaction link is added to the transaction element, and the line items are obtained from the original
 * transction (because the void transaction does not have any).
 * I suspect that all the totals should be gathered from the original transaction.... leave that TODO
 */
public class LogVoidTransaction
extends LogRetailTransaction
implements LogVoidTransactionIfc
{
    /**
    revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * The incoming transaction should be an instance of VoidTransactionIfc
     */
    protected VoidTransactionIfc voidTransaction           = null;

    /**
     * This is the transaction that was voided
     */
    protected TenderableTransactionIfc originalTransaction = null;

    /**
     * Main entry point for the logger. Simply set up the void and voided transaction,
     * then use the Retailtransaction loger to do all the work.
     */
    public Element createTransactionElement(TransactionIfc transaction, Document doc)
    throws XMLConversionException
    {
        voidTransaction     = (VoidTransactionIfc) transaction;
        originalTransaction = voidTransaction.getOriginalTransaction();

        return super.createTransactionElement(transaction, doc);
    }

    /**
     * This method uses the base class to do all the work, then adds a transaction link element.
     */
    protected void createBaseElements()
    throws XMLConversionException
    {
       super.createBaseElements();

       // capture the payment line items for void of House Account payment.
       if(originalTransaction instanceof PaymentTransactionIfc)
       {
           createTenderLineItemElements(getTenderLineItemsIterator(null), null);
       }
       // The base processing would have set the voided transactiopn status as "completed", which is weird.
       // We over-ride the status here, so when the XML is processed, we can determine that this is a voided transaction
       // without looking ahead to the link element.
       retailTransactionElement.setTransactionStatus(ENUMERATION_TRANSACTION_STATUS_POSTVOIDED);

       // add transaction link
       POSLogTransactionLinkIfc link = getSchemaTypesFactory().getTransactionLinkInstance();
       // link.setReasonCode("Voided");
       link.setReasonCode(voidTransaction.getReason().getCodeName());
       link.setRetailStoreID(originalTransaction.getFormattedStoreID());
       link.setWorkstationID(originalTransaction.getFormattedWorkstationID());
       link.setSequenceNumber(originalTransaction.getFormattedTransactionSequenceNumber());
       link.setBusinessDayDate(dateValue(originalTransaction.getBusinessDay()));
       retailTransactionElement.setTransactionLink(link);

    }

    /**
     * Intercept this call from the base class, and substitute the voided transaction in the parameters.
     * Note that we expect all voided transactions to be SaleReturnTransactions.
     */
    protected void addBaseElements(TransactionIfc transaction)
    throws XMLConversionException
    {
        if (originalTransaction instanceof SaleReturnTransactionIfc ||
        	originalTransaction instanceof RedeemTransactionIfc ||
            originalTransaction instanceof PaymentTransactionIfc ||
            originalTransaction instanceof BillPayTransactionIfc)
        {
            super.addBaseElements(originalTransaction);
        }
        else
        {
            throw new XMLConversionException("Voided transaction is not supported for type: "+originalTransaction.getClass().getName());
        }
    }

    /**
     * Intercept this call from the base class, and substitute the voided transaction in the parameters.
     * Note that we expect all voided transactions to be SaleReturnTransactions.
     */
    protected void addExtendedElements(TransactionIfc transaction)
    throws XMLConversionException
    {
        if (originalTransaction instanceof SaleReturnTransactionIfc ||
            	originalTransaction instanceof RedeemTransactionIfc ||
                originalTransaction instanceof PaymentTransactionIfc ||
                originalTransaction instanceof BillPayTransactionIfc)
        {
            super.addExtendedElements(originalTransaction);
        }
        else
        {
        	throw new XMLConversionException("Voided transaction is not supported for type: "+originalTransaction.getClass().getName());
        }

        retailTransactionElement.setPostVoidReasonCodeNumeric(voidTransaction.getReason().getCode());

    }


    /**
     * This method return false in the base class, here it is extended to indicate that we are processign a void
     */
    protected boolean postVoided()
    {
        return true;
    }

    /**
     * This method is used to return the tender line items for a post-voided transaction.
     */
    protected Iterator getTenderLineItemsIterator(SaleReturnTransactionIfc retailTrans)
    {
        return voidTransaction.getTenderLineItemsVector().iterator();
    }

    /**
     * This method return the change for a post-voided transaction.
     */
    protected CurrencyIfc getChangeDue(SaleReturnTransactionIfc retailTrans)
    {
        return voidTransaction.getNegativeCashTotal();      // TODO this is a bit of a guess.....
    }
}
