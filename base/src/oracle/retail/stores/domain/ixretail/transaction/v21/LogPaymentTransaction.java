/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/transaction/v21/LogPaymentTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:09 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:56 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:15 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:25 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/06/24 09:15:09  mwright
 *   POSLog v2.1 (second) merge with top of tree
 *
 *   Revision 1.2.2.1  2004/06/23 00:29:25  mwright
 *   Cange order in which payment and tender lines are added to the XML, to preserve line numbers
 *
 *   Revision 1.2  2004/05/06 03:21:07  mwright
 *   Initial revision for POSLog v2.1 merge with top of tree
 *
 *   Revision 1.1.2.2  2004/05/05 02:31:22  mwright
 *   Refactored to call methods in parent class explicitly, because a payment transaction is not a retail transaction
 *
 *   Revision 1.1.2.1  2004/04/26 22:20:47  mwright
 *   Initial revision for v2.1
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.transaction.v21;


import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import oracle.retail.stores.domain.ixretail.transaction.LogPaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.PaymentTransactionIfc;


/**
 * This class uses the existing RetailTransaction logger to do most of the work of logging a payment transaction.
 * Because this is not a SaleReturnTransaction, we have to call a few of the component methods explicitly.
 */
public class LogPaymentTransaction 
extends LogRetailTransaction
implements LogPaymentTransactionIfc
{
    /**
    revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    
    /**
     * This method uses the base class to do all the work, then adds a single payment line item.
     */
    protected void createBaseElements()
    throws XMLConversionException
    {
       super.createBaseElements();

       PaymentTransactionIfc paymentTransaction = (PaymentTransactionIfc)transaction;
       
       // These are automatically called for SaleReturnTransactions...but a PaymentTransaction is not a SaleReturnTransaction
       // v2.1 specific: tender lines come before payment lines, so the sequence numbers match
       createTenderLineItemElements(paymentTransaction.getTenderLineItemsVector().iterator(), paymentTransaction.calculateChangeDue());
       makePaymentLineItem(((PaymentTransactionIfc)transaction).getPayment());
       setCustomerDetails(paymentTransaction.getCustomer());
       
    }
    
}
