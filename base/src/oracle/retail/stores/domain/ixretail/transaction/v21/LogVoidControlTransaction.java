/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/transaction/v21/LogVoidControlTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:09 mszekely Exp $
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
 *    1    360Commerce 1.0         5/20/2008 5:12:41 PM   Leona R. Slepetis Add
 *          POSLog support for postvoided till functions (loan, payroll,
 *         pickup, payin, payout). Reviewed by R. Ojha.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.transaction.v21;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.domain.ixretail.transaction.LogVoidControlTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;

import oracle.retail.stores.commerceservices.ixretail.schematypes.v21.POSLogTransactionLinkIfc;

/**
 * This class uses the existing LogTenderControlTransaction logger to do most of the work of logging a voided till 
 * transaction.
 * A transaction link is added to the transaction element, and the data are obtained from the original
 * transction (because the void transaction does not have any).
 */
public class LogVoidControlTransaction 
extends LogTillAdjustmentTransaction
implements LogVoidControlTransactionIfc
{
    /**
    revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * The incoming transaction should be an instance of VoidTransactionIfc
     */
    VoidTransactionIfc voidTransaction           = null;
    
    /**
     * This is the transaction that was voided
     */
    TenderableTransactionIfc originalTransaction = null;
    
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
       
       // The base processing would have set the voided transactiopn status as "completed", which is weird.
       // We over-ride the status here, so when the XML is processed, we can determine that this is a voided transaction
       // without looking ahead to the link element.
       tenderControlTransactionElement.setTransactionStatus(ENUMERATION_TRANSACTION_STATUS_POSTVOIDED);
       
       // add transaction link
       POSLogTransactionLinkIfc link = getSchemaTypesFactory().getTransactionLinkInstance();
       link.setReasonCode("Voided");
       link.setRetailStoreID(originalTransaction.getFormattedStoreID());
       link.setWorkstationID(originalTransaction.getFormattedWorkstationID());
       link.setSequenceNumber(originalTransaction.getFormattedTransactionSequenceNumber());
       link.setBusinessDayDate(dateValue(originalTransaction.getBusinessDay()));
       tenderControlTransactionElement.setTransactionLink(link);
       
       addBaseElements(originalTransaction);
       
    }
    
    /**
     * This method return false in the base class, here it is extended to indicate that we are processign a void
     */
    boolean postVoided()
    {
        return true;
    }
    
    //---------------------------------------------------------------------
    /**
     * This method is used to manipulate the source of the transaction being logged.
     * For a void, the voided transaction is substituted in the parameter.
     * @param transaction transaction reference
     * @exception XMLConversionException xml conversion exception
     */
    //---------------------------------------------------------------------
    protected void addBaseElements(TransactionIfc transaction)
    throws XMLConversionException
    {
        super.addBaseElements(originalTransaction);
    }

}
