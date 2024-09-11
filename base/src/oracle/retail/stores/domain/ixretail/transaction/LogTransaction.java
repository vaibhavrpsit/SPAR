/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/transaction/LogTransaction.java /rgbustores_13.4x_generic_branch/3 2011/09/02 13:05:38 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    rrkohli   07/19/11 - encryption CR
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    mdecama   10/28/08 - I18N - Reason Codes for Customer Types.
 * ===========================================================================
     $Log:
      4    360Commerce 1.3         4/25/2007 10:00:44 AM  Anda D. Cadar   I18N
           merge
      3    360Commerce 1.2         3/31/2005 4:28:57 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:23:18 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:27 PM  Robert Pearse
     $
     Revision 1.4  2004/08/10 07:17:10  mwright
     Merge (3) with top of tree

     Revision 1.3.6.1  2004/07/29 01:19:50  mwright
     Implemented calls to lineItem interface with added transaction parameter

     Revision 1.3  2004/02/12 17:13:48  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:26:32  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.1   Oct 02 2003 10:45:34   baa
 * fix for rss defect 1406
 * Resolution for 3402: RSS  PRF 1406 Tender Information on Void transactions does not looks correct
 *
 *    Rev 1.0   Aug 29 2003 15:36:46   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Jul 01 2003 14:09:26   jgs
 * Modifications for new 6.0 data.
 * Resolution for 1157: Add task for Importing IX Retail Transactions.
 *
 *    Rev 1.1   Jan 22 2003 10:00:16   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.0   Sep 05 2002 11:13:00   msg
 * Initial revision.
 *
 *    Rev 1.7   Aug 11 2002 18:46:34   vpn-mpm
 * Modified createLineItemElements() signature to facilitate extensibility.
 *
 *    Rev 1.6   08 Jun 2002 15:58:20   vpn-mpm
 * Made modifications to enhance extensibility.
 *
 *    Rev 1.5   May 30 2002 17:17:28   mpm
 * Added support for transaction type map.
 * Resolution for Domain SCR-76: Add transaction type map
 *
 *    Rev 1.4   May 13 2002 19:04:14   mpm
 * Added more columns to order; add support for deleted items (line voids).
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.3   Apr 30 2002 17:55:48   mpm
 * Added support for store-open.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.2   Apr 29 2002 17:52:16   mpm
 * Added support for no-sale transaction.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.1   Apr 28 2002 13:32:22   mpm
 * Completed translation of sale transactions.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   Apr 21 2002 15:24:40   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.domain.ixretail.transaction;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.customer.LogCustomerIfc;
import oracle.retail.stores.domain.ixretail.financial.LogLayawayIfc;
import oracle.retail.stores.domain.ixretail.lineitem.LogSaleReturnLineItemIfc;
import oracle.retail.stores.domain.ixretail.log.AbstractIXRetailTranslator;
import oracle.retail.stores.domain.ixretail.tender.LogTenderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.AbstractTenderableTransaction;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTypeMapIfc;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

/**
 * This class creates the TLog in IXRetail format for the Retail Transaction
 * View.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/3 $
 */
public class LogTransaction extends AbstractIXRetailTranslator implements LogTransactionIfc
{
    /**
     * revision number supplied by source-code-control system
     */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/3 $";
    /**
     * element type
     */
    protected String elementType = IXRetailConstantsIfc.TYPE_POS_360_TRANSACTION_ELEMENT;
    /**
     * indicator that this type element has a version string
     */
    protected boolean hasVersion = false;
    /**
     * transaction type map
     */
    protected static TransactionTypeMapIfc transactionTypeMap = DomainGateway.getFactory()
            .getTransactionTypeMapInstance();
    /**
     * line item logger
     */
    protected LogSaleReturnLineItemIfc srliLogger = null;

    /**
     * Constructs LogTransaction object.
     * <P>
     */
    public LogTransaction()
    {
    }

    /**
     * Creates element for the specified transaction.
     *
     * @param transaction transaction for which RetailTransaction is to be
     *            created
     * @param doc Document containing transactions
     * @return Element representing transaction
     */
    public Element createTransactionElement(TransactionIfc transaction, Document doc) throws XMLConversionException
    {
        setParentDocument(doc);

        initializeTransactionElement();

        createBaseElements(transaction);

        createElements(transaction);

        createExtendedElements(transaction);

        appendElements(transaction, parentElement);

        return (parentElement);
    }

    /**
     * Create transaction elements and append to document. This is a no-op in
     * this case.
     * 
     * @param transaction TransactionIfc object
     */
    protected void createElements(TransactionIfc transaction) throws XMLConversionException
    {
    }

    /**
     * Creates and adds elements for line items on a transaction.
     * 
     * @param transaction transaction containing line items
     * @return number of line items written to log
     * @exception XMLConversionException if error occurs translating to XML
     */
    protected int createLineItemElements(TransactionIfc transaction) throws XMLConversionException
    {
        int sequenceNumber = 0;
        if (transaction instanceof RetailTransactionIfc)
        {
            sequenceNumber = createLineItemElements(((RetailTransactionIfc)transaction).getLineItemsIterator());
        }
        return (sequenceNumber);
    }

    /**
     * Creates and adds elements for line items.
     *
     * @param i iterator for line items
     * @param salesAssociate transaction sales associate
     * @exception XMLConversionException if error occurs translating to XML
     */
    protected int createLineItemElements(Iterator i) throws XMLConversionException
    {
        int sequenceNumber = 0;
        while (i.hasNext())
        {
            getSaleReturnLineItemLogger().createElement((SaleReturnLineItemIfc)i.next(), null, // no
                                                                                               // transaction
                                                                                               // information
                                                                                               // used
                                                                                               // by
                                                                                               // v1.0
                    parentDocument, parentElement, sequenceNumber++);
        }

        return (sequenceNumber);

    }

    /**
     * Creates and adds elements for voided line items.
     *
     * @param i iterator for line items
     * @param sequenceNumber sequence number
     * @exception XMLConversionException if error occurs translating to XML
     */
    protected int createVoidedLineItemElements(Iterator i, int sequenceNumber) throws XMLConversionException
    {
        // get line item facility
        LogSaleReturnLineItemIfc srliLog = IXRetailGateway.getFactory().getLogSaleReturnLineItemInstance();

        int numberOfElements = 0;

        while (i.hasNext())
        {
            srliLog.createElement((SaleReturnLineItemIfc)i.next(), null, // no
                                                                         // transaction
                                                                         // information
                                                                         // used
                                                                         // by
                                                                         // v1.0
                    parentDocument, parentElement, true, sequenceNumber++);
            numberOfElements++;
        }

        return (numberOfElements);

    }

    /**
     * Initializes transaction element.
     */
    protected void initializeTransactionElement()
    {
        parentElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_POS_360_TRANSACTION);

        parentElement.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_NAMESPACE_TAG,
                IXRetailConstantsIfc.ATTRIBUTE_NAMESPACE_IXRETAIL);

        parentElement.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_NAMESPACE_TAG,
                IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_NAMESPACE);

        parentElement.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_LOCATION_TAG,
                IXRetailConstantsIfc.ATTRIBUTE_NAMESPACE_IXRETAIL + " " + RETAIL_TRANSACTION_SCHEMA_LOCATION);

        parentElement.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_TYPE_TAG, elementType);

        if (hasVersion)
        {
            parentElement.setAttribute(IXRetailConstantsIfc.ELEMENT_VERSION, IXRetailConstantsIfc.ELEMENT_VERSION_DATA);
        }

    }

    /**
     * Create standard transaction elements and append to document.
     * 
     * @param transaction TransactionIfc object
     */
    protected void createBaseElements(TransactionIfc transaction) throws XMLConversionException
    {
        // create store id element
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_RETAIL_STORE_ID, transaction.getTransactionIdentifier()
                .getStoreID());
        // create workstation ID element
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_WORKSTATION_ID, transaction.getTransactionIdentifier()
                .getWorkstationID());
        // create sequence number element
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_SEQUENCE_NUMBER, transaction.getTransactionIdentifier()
                .getFormattedTransactionSequenceNumber());
        // create business date element
        createDateTextNodeElement(IXRetailConstantsIfc.ELEMENT_BUSINESS_DAY_DATE, transaction.getBusinessDay());
        // create begin time timestamp element
        createTimestampTextNodeElement(IXRetailConstantsIfc.ELEMENT_BEGIN_DATE_TIME, transaction.getTimestampBegin());
        // create end time timestamp element
        createTimestampTextNodeElement(IXRetailConstantsIfc.ELEMENT_END_DATE_TIME, transaction.getTimestampEnd());
        // create operator id element
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_OPERATOR_ID, transaction.getCashier().getEmployeeID());
        // create cancel flag element
        boolean canceledFlag = false;
        if (transaction.getTransactionStatus() == TransactionIfc.STATUS_CANCELED)
        {
            canceledFlag = true;
        }
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_CANCEL_FLAG, canceledFlag);
        // create training mode flag
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_TRAINING_MODE_FLAG, transaction.isTrainingMode());
        //
        // offline flag is not supported in POS
        //
    }

    /**
     * Create transaction elements for 360 extensions and append to document.
     * 
     * @param transaction TransactionIfc object
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createExtendedElements(TransactionIfc transaction) throws XMLConversionException
    {
        // create elements for transaction status and type
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_TRANSACTION_STATUS,
                TransactionIfc.IXRETAIL_STATUS_DESCRIPTORS[transaction.getTransactionStatus()]);
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_TRANSACTION_TYPE,
                transactionTypeMap.getDescriptor(transaction.getTransactionType()));
        // create customer info element
        if (transaction.getCustomerInfo() != null)
        {
            createCustomerInfoElement(transaction.getCustomerInfo());
        }
        // create till ID element
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_TILL_ID, transaction.getTillID());

    }

    /**
     * Creates CustomerInfo360 element.
     * 
     * @param customerInfo customer information object
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createCustomerInfoElement(CustomerInfoIfc customerInfo) throws XMLConversionException
    {
        Element customerInfoElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_CUSTOMER_INFO);
        String infoType = IXRetailConstantsIfc.ELEMENT_INFO_TYPE_VALUE_NONE;
        // derive info type
        switch (customerInfo.getCustomerInfoType())
        {
        case CustomerInfoIfc.CUSTOMER_INFO_TYPE_PHONE_NUMBER:
            infoType = IXRetailConstantsIfc.ELEMENT_INFO_TYPE_VALUE_PHONE_NUMBER;
            break;
        case CustomerInfoIfc.CUSTOMER_INFO_TYPE_POSTAL_CODE:
            infoType = IXRetailConstantsIfc.ELEMENT_INFO_TYPE_VALUE_POSTAL_CODE;
            break;
        default:
            break;
        }
        // set type element
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_INFO_TYPE, infoType, customerInfoElement);
        // set data element
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_CUSTOMER_DATA, customerInfo.getCustomerInfo(),
                customerInfoElement);

        // If an id had been entered for a return, log it.
        if (customerInfo.getPersonalID().getEncryptedNumber() != null)
        {
            // set type element
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_PERSONAL_ID_REQUIRED_TYPE, customerInfo
                    .getLocalizedPersonalIDType().getCode(), customerInfoElement);

            // set id element
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ENCRYPTED_PERSONAL_ID_NUMBER, customerInfo
                    .getPersonalID().getEncryptedNumber(), customerInfoElement);

            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_MASKED_PERSONAL_ID_NUMBER, customerInfo.getPersonalID()
                    .getMaskedNumber(), customerInfoElement);

            // set state element
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_PERSONAL_ID_STATE, customerInfo.getPersonalIDState(),
                    customerInfoElement);

            // set country element
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_PERSONAL_ID_COUNTRY,
                    customerInfo.getPersonalIDCountry(), customerInfoElement);
        }

        parentElement.appendChild(customerInfoElement);
    }

    /**
     * Creates and adds elements for tender line items.
     *
     * @param transaction transaction object
     * @param sequenceNumber sequence number after line items added
     * @return number of elements created
     * @exception XMLConversionException if error occurs translating to XML
     */
    protected int createTenderLineItemElements(TransactionIfc transaction, int sequenceNumber)
            throws XMLConversionException
    {
        int numberOfElements = 0;

        // only create line items if transaction is tenderable
        if (transaction instanceof AbstractTenderableTransaction)
        {
            AbstractTenderableTransaction tenderableTransaction = (AbstractTenderableTransaction)transaction;
            Iterator i = tenderableTransaction.getTenderLineItemsVector().iterator();

            // get tender line items facility
            LogTenderLineItemIfc tenderLog = IXRetailGateway.getFactory().getLogTenderLineItemInstance();

            while (i.hasNext())
            {
                tenderLog.createElement((TenderLineItemIfc)i.next(), parentDocument, parentElement, sequenceNumber++);
                numberOfElements++;
            }

            // check for tender change element, if needed
            // if no elements were written in previous step, we haven't really
            // tendered; therefore, no change record is required.
            if (numberOfElements > 0 && tenderableTransaction.calculateChangeDue().signum() != 0)
            {
                tenderLog.createTenderChangeElement(tenderableTransaction.calculateChangeDue(), parentDocument,
                        parentElement, sequenceNumber++);

                numberOfElements++;
            }
        }

        return (numberOfElements);

    }

    /**
     * Create totals element.
     * 
     * @param totalType attribute value for type of total
     * @param amount amount of total
     * @exception XMLConversionException if error occurs translating to XML
     */
    protected void createTotalElement(String totalType, CurrencyIfc amount) throws XMLConversionException
    {
        // The default is a IXRetail total, therefore the final parameter
        // is false.
        createTotalElement(totalType, amount, false);
    }

    /**
     * Create totals element.
     * 
     * @param totalType attribute value for type of total
     * @param amount amount of total
     * @param total360 true if the tag should be "Total360".
     * @exception XMLConversionException if error occurs translating to XML
     */
    protected void createTotalElement(String totalType, CurrencyIfc amount, boolean total360)
            throws XMLConversionException
    {
        Element totalsElement = null;
        if (total360)
        {
            totalsElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_TOTAL_360);
        }
        else
        {
            totalsElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_TOTAL);
        }

        totalsElement.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_TOTAL_TYPE, totalType);
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_AMOUNT, amount, totalsElement);

        parentElement.appendChild(totalsElement);
    }

    /**
     * Creates RetailTransactionCustomer360 element.
     * 
     * @param customer customer reference
     * @exception XMLConversionException thrown if error occurs translating to
     *                XML
     */
    protected void createCustomerElement(CustomerIfc customer) throws XMLConversionException
    {
        LogCustomerIfc log = IXRetailGateway.getFactory().getLogCustomerInstance();

        log.createElement(customer, parentDocument, parentElement);
    }

    /**
     * Creates elements for layaway.
     * <P>
     * The IXRetail standard logs layaway data with the line items. In the
     * 360Commerce implementation, layaway data is carried at the transaction
     * level.
     *
     * @param layaway layaway object
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createLayawayElements(LayawayIfc layaway) throws XMLConversionException
    {
        if (layaway != null)
        {
            LogLayawayIfc logLayaway = IXRetailGateway.getFactory().getLogLayawayInstance();

            logLayaway.createElement(parentDocument, parentElement, layaway);
        }
    }

    /**
     * Create original transaction link elements.
     * 
     * @param transaction original transaction
     * @param el parent element
     * @return transaction link element
     * @exception XMLConversionException thrown if error occurs
     */
    protected Element createOriginalTransactionLinkElements(TransactionIfc transaction, Element el)
            throws XMLConversionException
    {
        // create original transaction link element
        Element transElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_ORIGINAL_TRANSACTION);

        TransactionIDIfc originalTransactionID = transaction.getTransactionIdentifier();

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_RETAIL_STORE_ID, originalTransactionID.getStoreID(),
                transElement);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_WORKSTATION_ID, originalTransactionID.getWorkstationID(),
                transElement);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_SEQUENCE_NUMBER,
                originalTransactionID.getFormattedTransactionSequenceNumber(), transElement);

        createDateTextNodeElement(IXRetailConstantsIfc.ELEMENT_BUSINESS_DAY_DATE, transaction.getBusinessDay(),
                transElement);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_TRANSACTION_TYPE,
                TransactionIfc.IXRETAIL_TYPE_DESCRIPTORS[transaction.getTransactionType()], transElement);

        el.appendChild(transElement);

        return (transElement);

    }

    /**
     * Returns instance of sale return line item logger. If class attribute is
     * null, logger is instantiated.
     * 
     * @return instance of sale return line item logger
     */
    protected LogSaleReturnLineItemIfc getSaleReturnLineItemLogger()
    {
        if (srliLogger == null)
        {
            srliLogger = IXRetailGateway.getFactory().getLogSaleReturnLineItemInstance();
        }
        return (srliLogger);
    }

}
