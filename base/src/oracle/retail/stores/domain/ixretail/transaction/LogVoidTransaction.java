/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/transaction/LogVoidTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:09 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:57 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:19 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:27 PM  Robert Pearse   
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
 *    Rev 1.1   Oct 02 2003 10:45:38   baa
 * fix for rss defect 1406
 * Resolution for 3402: RSS  PRF 1406 Tender Information on Void transactions does not looks correct
 * 
 *    Rev 1.0   Aug 29 2003 15:36:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jul 01 2003 14:09:24   jgs
 * Modifications for new 6.0 data.
 * Resolution for 1157: Add task for Importing IX Retail Transactions.
 * 
 *    Rev 1.1   Jan 22 2003 10:00:14   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 * 
 *    Rev 1.0   Sep 05 2002 11:13:02   msg
 * Initial revision.
 * 
 *    Rev 1.3   08 Jun 2002 15:58:22   vpn-mpm
 * Made modifications to enhance extensibility.
 *
 *    Rev 1.2   Jun 01 2002 15:46:20   mpm
 * Made minor corrections to translations.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.1   Apr 30 2002 17:55:52   mpm
 * Added support for store-open.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   Apr 28 2002 13:34:16   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.transaction;
// java imports
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

//--------------------------------------------------------------------------
/**
    This class creates the TLog in IXRetail format for the Retail Transaction
    View.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LogVoidTransaction
extends LogTransaction
implements LogVoidTransactionIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        void transaction reference
    **/
    protected VoidTransactionIfc voidTransaction = null;
    /**
        original transaction reference
    **/
    protected TenderableTransactionIfc originalTransaction = null;

    //----------------------------------------------------------------------------
    /**
        Constructs LogVoidTransaction object. <P>
    **/
    //----------------------------------------------------------------------------
    public LogVoidTransaction()
    {                                   // begin LogVoidTransaction()
    }                                   // end LogVoidTransaction()

    //---------------------------------------------------------------------
    /**
       Creates element for the specified transaction. <P>
       @param transaction transaction for which RetailTransaction is to be created
       @param doc Document containing transactions
       @return Element representing transaction
    **/
    //---------------------------------------------------------------------
    public Element createTransactionElement(TransactionIfc transaction,
                                            Document doc)
    throws XMLConversionException
    {                                   // begin testCreateTransactionElement()
        // set reference to void transaction
        voidTransaction = (VoidTransactionIfc) transaction;
        originalTransaction = voidTransaction.getOriginalTransaction();
        // set for retail transaction if this is a void of a retail transaction
        // so the line items can be included
        if (originalTransaction instanceof SaleReturnTransactionIfc)
        {
            elementType =
              IXRetailConstantsIfc.TYPE_POS_360_RETAIL_TRANSACTION_ELEMENT;
            hasVersion = true;
        }
        setParentDocument(doc);

        initializeTransactionElement();

        createBaseElements(transaction);

        createElements(transaction);

        createExtendedElements(transaction);

        appendElements(transaction,
                       parentElement);

        return(parentElement);
    }                                   // end testCreateTransactionElement()

    //---------------------------------------------------------------------
    /**
       Create transaction elements and append to document.
       @param transaction TransactionIfc object
    **/
    //---------------------------------------------------------------------
    protected void createElements(TransactionIfc transaction)
    throws XMLConversionException
    {                                   // begin createElements()
        // get sequence number from line items
        int sequenceNumber = 0;

        if (originalTransaction instanceof SaleReturnTransactionIfc)
        {
            // add receipt date/time
            // Technically, this should be the time printed on the receipt.
            // However, that is not currently supported in the application.
            createTimestampTextNodeElement
              (IXRetailConstantsIfc.ELEMENT_RECEIPT_DATE_TIME,
               transaction.getTimestampEnd());

            sequenceNumber = super.createLineItemElements
              (((SaleReturnTransactionIfc) originalTransaction).getLineItemsIterator());
        }

        // create tender line item elements
        int numberOfElements = createTenderLineItemElements(voidTransaction,
                                                            sequenceNumber);
        sequenceNumber += numberOfElements;

        if (originalTransaction instanceof SaleReturnTransactionIfc)
        {
            // add gross totals
            createTotalElement(IXRetailConstantsIfc.ATTRIBUTE_VALUE_TRANSACTION_GROSS_AMOUNT,
                               voidTransaction.getTransactionTotals().getPreTaxSubtotal(), false);
            // add tax totals
            createTotalElement(IXRetailConstantsIfc.ATTRIBUTE_VALUE_TRANSACTION_TAX_AMOUNT,
                               voidTransaction.getTransactionTotals().getTaxTotal(), false);
            // add grand totals
            createTotalElement(IXRetailConstantsIfc.ATTRIBUTE_VALUE_TRANSACTION_GRAND_AMOUNT,
                               voidTransaction.getTransactionTotals().getGrandTotal(), false);

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

        // add original transaction link
        createOriginalTransactionLinkElements(originalTransaction,
                                              parentElement);

        if (originalTransaction instanceof SaleReturnTransactionIfc)
        {
            // add non discounted subtotal
            createTotalElement(IXRetailConstantsIfc.ATTRIBUTE_VALUE_TRANSACTION_NON_DISCOUNTED_GROSS_AMOUNT,
                               voidTransaction.getTransactionTotals().getSubtotal(), true);
    
            // add total discount amount
            createTotalElement(IXRetailConstantsIfc.ATTRIBUTE_VALUE_TRANSACTION_DISCOUNT_AMOUNT,
                               voidTransaction.getTransactionTotals().getDiscountTotal(), true);
    
            // add total Tender amount
            createTotalElement(IXRetailConstantsIfc.ATTRIBUTE_VALUE_TRANSACTION_TENDER_AMOUNT,
                               voidTransaction.getTransactionTotals().getAmountTender(), true);
        }

    }                                   // end createExtendedElements()

}
