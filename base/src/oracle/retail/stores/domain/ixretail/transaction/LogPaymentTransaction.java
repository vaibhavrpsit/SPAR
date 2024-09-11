/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/transaction/LogPaymentTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:09 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:56 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:15 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:25 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 17:13:48  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:26:32  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:36:40   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jan 22 2003 10:00:30   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * 
 *    Rev 1.0   Sep 05 2002 11:12:56   msg
 * Initial revision.
 * 
 *    Rev 1.2   Apr 30 2002 17:55:44   mpm
 * Added support for store-open.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.1   Apr 28 2002 14:13:44   mpm
 * Corrected problem in createExtendedElements().
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   Apr 28 2002 13:34:54   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.transaction;
// XML imports
import oracle.retail.stores.domain.financial.PaymentIfc;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.financial.LogPaymentLineItemIfc;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.PaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

//--------------------------------------------------------------------------
/**
    This class creates the TLog in IXRetail format for the Retail Transaction
    View for a payment transaction.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogPaymentTransaction
extends LogRetailTransaction
implements LogPaymentTransactionIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------------
    /**
        Constructs LogPaymentTransaction object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogPaymentTransaction()
    {                                   // begin LogPaymentTransaction()
        elementType =
          IXRetailConstantsIfc.TYPE_POS_360_RETAIL_TRANSACTION_ELEMENT;
        hasVersion = true;
    }                                   // end LogPaymentTransaction()

    //---------------------------------------------------------------------
    /**
       Create transaction elements and append to document.
       @param transaction TransactionIfc object
    **/
    //---------------------------------------------------------------------
    protected void createElements(TransactionIfc transaction)
    throws XMLConversionException
    {                                   // begin createElements()
        // set reference to PaymentTransactionIfc
        PaymentTransactionIfc paymentTransaction =
          (PaymentTransactionIfc) transaction;

        // add receipt date/time
        // Technically, this should be the time printed on the receipt.
        // However, that is not currently supported in the application.
        createTimestampTextNodeElement
          (IXRetailConstantsIfc.ELEMENT_RECEIPT_DATE_TIME,
           transaction.getTimestampEnd());

        // add payment line item element
        createPaymentLineItemElements(paymentTransaction.getPayment());

        // get sequence number from line items
        int sequenceNumber = 1;

        // create tender line item elements
        int numberOfElements = createTenderLineItemElements(paymentTransaction,
                                                            sequenceNumber);
        sequenceNumber += numberOfElements;

        // add gross totals
        createTotalElement(IXRetailConstantsIfc.ATTRIBUTE_VALUE_TRANSACTION_GROSS_AMOUNT,
                           paymentTransaction.getTransactionTotals().getPreTaxSubtotal());
        // add tax totals
        createTotalElement(IXRetailConstantsIfc.ATTRIBUTE_VALUE_TRANSACTION_TAX_AMOUNT,
                           paymentTransaction.getTransactionTotals().getTaxTotal());
        // add grand totals
        createTotalElement(IXRetailConstantsIfc.ATTRIBUTE_VALUE_TRANSACTION_GRAND_AMOUNT,
                           paymentTransaction.getTransactionTotals().getGrandTotal());

        // create customer element, if needed
        if (paymentTransaction.getCustomer() != null)
        {
            createCustomerElement(paymentTransaction.getCustomer());
        }

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

        if (transaction instanceof LayawayPaymentTransactionIfc)
        {
            createLayawayElements
              (((LayawayPaymentTransactionIfc) transaction).getLayaway());
        }

    }                                   // end createExtendedElements()

    //---------------------------------------------------------------------
    /**
       Creates elements for payment line item. <P>
       @param payment payment information object
       @exception XMLConversionException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    protected void createPaymentLineItemElements(PaymentIfc payment)
    throws XMLConversionException
    {                                   // begin createPaymentLineItemElements()
        LogPaymentLineItemIfc logPayment =
          IXRetailGateway.getFactory().getLogPaymentLineItemInstance();

        logPayment.createElement(payment,
                                 parentDocument,
                                 parentElement,
                                 0);

    }                                   // end createPaymentLineItemElements()

}
