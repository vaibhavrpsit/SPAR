/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveCertificate.java /main/4 2014/02/26 15:17:49 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     02/26/14 - Fixed an issue with retrieving transactions with a
 *                         Mall Certificate tender for returns and voids.
 *    abhineek  03/25/13 - fix for transactions tendedred with mall gift
 *                         certificate do not make to co
 *    jswan     05/18/12 - Fixed issue with saving gift certificates issued as
 *                         change.
 *    jswan     03/26/12 - Modified to support centralized gift certificate and
 *                         store credit.
 *    jswan     03/21/12 - Modified to support centralized gift certificate and
 *                         store credit.
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCertificateItemIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.RedeemTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.domain.utility.AbstractTenderDocumentIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.GiftCertificateDocumentIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * This operation saves the gift certificate document.
 *
 * @version $Revision: /main/4 $
 */
public class JdbcSaveCertificate extends JdbcDataOperation implements ARTSDatabaseIfc
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1221978721509174934L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveCertificate.class);

    /**
     * Executes the SQL statements against the database.
     *
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
                        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug("JdbcSaveStoreCredit.execute()");

        // pull the data from the action object
        ARTSTransaction trans = (ARTSTransaction)action.getDataObject();
        TenderableTransactionIfc transaction = (TenderableTransactionIfc)trans.getPosTransaction();

        // Save Gift Certificate information associated with tender line items.
        TenderLineItemIfc[] tenderLineItems = transaction.getTenderLineItems();
        for (TenderLineItemIfc tenderLineItem: tenderLineItems)
        {
            if (tenderLineItem instanceof TenderGiftCertificateIfc)
            {
                saveTenderLine((JdbcDataConnection)dataConnection, transaction,
                        (TenderGiftCertificateIfc) tenderLineItem);
            }
        }

        // If the transaction is a redeem transaction and the redeemed tender is 
        // a gift certificate, update the 
        if (transaction instanceof RedeemTransactionIfc)
        {
            RedeemTransactionIfc rTrans = (RedeemTransactionIfc)transaction;
            if (rTrans.getRedeemTender() instanceof TenderGiftCertificateIfc)
            {
                saveTenderLine((JdbcDataConnection)dataConnection, transaction,
                        (TenderGiftCertificateIfc) rTrans.getRedeemTender());
            }
        }
        
        // If the transaction is a void transaction, get the original transaction
        // which contains the sale return line items.
        TenderableTransactionIfc localTransaction = transaction;
        if (transaction instanceof VoidTransactionIfc)
        {
            localTransaction = ((VoidTransactionIfc)transaction).getOriginalTransaction();
        }
            
        // Save Gift Certificate information associated with sale return line items.
        if (localTransaction instanceof SaleReturnTransactionIfc)
        {
            AbstractTransactionLineItemIfc[] transLineItems = 
                ((SaleReturnTransactionIfc)localTransaction).getItemContainerProxy().getLineItems();
            
            for (AbstractTransactionLineItemIfc transLineItem: transLineItems)
            {
                if (transLineItem instanceof SaleReturnLineItemIfc)
                {
                    if (((SaleReturnLineItemIfc)transLineItem).getPLUItem() instanceof GiftCertificateItemIfc)
                    {
                        saveSaleReturnLineItem((JdbcDataConnection)dataConnection, transaction,
                            (SaleReturnLineItemIfc) transLineItem);
                    }
                }
            }
            
        }
        if (logger.isDebugEnabled()) logger.debug("JdbcSaveStoreCredit.execute()");
    }
    
    /**
     * Get the document from the tender line item, set the status and date,
     * and call the saveGiftCertificateDocument() method.
     * @param dataConnection
     * @param transaction
     * @param lineItem
     * @throws DataException
     */
    protected void saveTenderLine(JdbcDataConnection dataConnection, TenderableTransactionIfc transaction,
            TenderGiftCertificateIfc lineItem) throws DataException
    {
        GiftCertificateDocumentIfc document = ((TenderGiftCertificateIfc)lineItem).getDocument();
        if (transaction instanceof VoidTransactionIfc)
        {
            document.setPreviousStatus(AbstractTenderDocumentIfc.ISSUED);
            document.setStatus(AbstractTenderDocumentIfc.VOIDED);
            document.setVoidDate(new EYSDate());
            document.setAmount(lineItem.getAmountTender().abs());
            document.setIssuingStoreID(transaction.getTransactionIdentifier().getStoreID());
            document.setIssuingWorkstationID(transaction.getTransactionIdentifier().getWorkstationID());
            document.setDocumentID(lineItem.getGiftCertificateNumber());
        }
        else
        if (transaction instanceof RedeemTransactionIfc)
        {
            document.setPreviousStatus(AbstractTenderDocumentIfc.ISSUED);
            document.setStatus(AbstractTenderDocumentIfc.REDEEMED);
            document.setRedeemDate(new EYSDate());
            document.setAmount(lineItem.getAmountTender().abs());
            document.setIssuingStoreID(transaction.getTransactionIdentifier().getStoreID());
            document.setIssuingWorkstationID(transaction.getTransactionIdentifier().getWorkstationID());
            document.setDocumentID(lineItem.getGiftCertificateNumber());
        }
        else
        if (lineItem.getAmountTender().signum() < 0)
        {
            document.setStatus(AbstractTenderDocumentIfc.ISSUED);
            document.setIssueDate(new EYSDate());
            document.setDocumentID(lineItem.getGiftCertificateNumber());
            document.setIssuingStoreID(transaction.getTransactionIdentifier().getStoreID());
            document.setIssuingWorkstationID(transaction.getTransactionIdentifier().getWorkstationID());
            document.setIssuingTransactionSeqNumber(transaction.getTransactionIdentifier().getSequenceNumber());
            document.setIssuingBusinessDate(transaction.getBusinessDay());
            document.setIssuingLineItemNumber(lineItem.getLineNumber());
            document.setAmount(lineItem.getAmountTender().abs());
        }
        else
        {
            document.setPreviousStatus(AbstractTenderDocumentIfc.ISSUED);
            document.setStatus(AbstractTenderDocumentIfc.REDEEMED);
            document.setRedeemDate(new EYSDate());
            document.setAmount(lineItem.getAmountTender().abs());
            document.setIssuingStoreID(transaction.getTransactionIdentifier().getStoreID());
            document.setIssuingWorkstationID(transaction.getTransactionIdentifier().getWorkstationID());
            document.setIssuingTransactionSeqNumber(transaction.getTransactionSequenceNumber());
            document.setIssuingLineItemNumber(lineItem.getLineNumber());
            document.setDocumentID(lineItem.getGiftCertificateNumber());
        }
        
        if ((lineItem.isMallCertificateAsCheck() || 
                lineItem.isMallCertificateAsPurchaseOrder()) && 
                lineItem.getNumber().length == 0)
        {
            // Don't save the gift certificate document for mall certs that have no
            // serial number
        }
        else
        {
            saveGiftCertificateDocument((JdbcDataConnection)dataConnection, document);
        }
    }

    /**
     * Create the document from the sale return line item, set the status and date,
     * and call the saveGiftCertificateDocument() method.
     * @param dataConnection
     * @param transaction
     * @param transLineItem
     * @throws DataException
     */
    protected void saveSaleReturnLineItem(JdbcDataConnection dataConnection,
            TenderableTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem) throws DataException
    {
        GiftCertificateDocumentIfc document = DomainGateway.getFactory().getGiftCertificateDocumentInstance();
        
        document.setDocumentID(lineItem.getPLUItem().getItemID());
        document.setIssuingStoreID(transaction.getTransactionIdentifier().getStoreID());
        document.setIssuingWorkstationID(transaction.getTransactionIdentifier().getWorkstationID());
        document.setIssuingTransactionSeqNumber(transaction.getTransactionIdentifier().getSequenceNumber());
        document.setIssuingBusinessDate(transaction.getBusinessDay());
        document.setIssuingLineItemNumber(lineItem.getLineNumber());
        document.setAmount(lineItem.getSellingPrice());
        document.setIssueDate(new EYSDate());
        if (transaction.getTransactionType() == TransactionIfc.TYPE_VOID)
        {
            document.setPreviousStatus(AbstractTenderDocumentIfc.ISSUED);
            document.setStatus(AbstractTenderDocumentIfc.VOIDED);
            document.setVoidDate(new EYSDate());
        }
        else
        {
            document.setStatus(AbstractTenderDocumentIfc.ISSUED);
            document.setIssueDate(new EYSDate());
        }
        document.setTrainingMode(transaction.isTrainingMode());
        
        saveGiftCertificateDocument((JdbcDataConnection)dataConnection, document);
    }

    /**
     * Update or insert a row in the Gift Certificate Document table, as required.
     * @param dataConnection
     * @param transaction
     * @param lineItemSequenceNumber
     * @param certificateNumber
     * @param faceValue
     * @param redeemed
     * @throws DataException
     */
    public void saveGiftCertificateDocument(JdbcDataConnection dataConnection, 
            GiftCertificateDocumentIfc document) throws DataException
    {
        if (!updateGiftCertificateDocument(dataConnection, document))
        {
            insertGiftCertificateDocument(dataConnection, document);
        }
    }
    
    /**
     * Inserts a row into the gift certificate document table.
     *
     * @param dataConnection Data source connection to use
     * @param transaction The retail transaction
     * @param lineItem sale/return line item
     * @param boolean indicates if the the gift certificate has been redeemed by the customer.
     * @exception DataException upon error
     */
    protected boolean updateGiftCertificateDocument(JdbcDataConnection dataConnection, 
            GiftCertificateDocumentIfc document) throws DataException
    {
        boolean rowUpdated = true;
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_GIFT_CERTIFICATE);

        // Fields
        if (document.getPreviousStatus() != null)
        {
            sql.addColumn(FIELD_GIFT_CERTIFICATE_PREVIOUS_STATUS, inQuotes(document.getPreviousStatus()));
        }
        sql.addColumn(FIELD_GIFT_CERTIFICATE_STATUS, inQuotes(document.getStatus()));

        if (document.getIssueDate() != null)
        {
            sql.addColumn(FIELD_GIFT_CERTIFICATE_ISSUE_DATE, getSQLCurrentTimestampFunction());
        }
        if (document.getRedeemDate() != null)
        {
            sql.addColumn(FIELD_GIFT_CERTIFICATE_REDEEM_DATE, getSQLCurrentTimestampFunction());
        }
        if (document.getVoidDate() != null)
        {
            sql.addColumn(FIELD_GIFT_CERTIFICATE_VOID_DATE, getSQLCurrentTimestampFunction());
        }

        // Qualifier
        sql.addQualifier(FIELD_GIFT_CERTIFICATE_SERIAL_NUMBER, inQuotes(document.getDocumentID()));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e.toString());
            throw new DataException(DataException.UNKNOWN, "updateGiftCertificateDocument", e);
        }

        // Determine if a row has been upated or not.
        if (0 >= dataConnection.getUpdateCount())
        {
            rowUpdated = false;
        }
        
        return rowUpdated;
    }

    /**
     * Inserts a row into the gift certificate document table.
     *
     * @param dataConnection Data source connection to use
     * @param transaction The retail transaction
     * @param lineItem sale/return line item
     * @param boolean indicates if the the gift certificate has been redeemed by the customer.
     * @exception DataException upon error
     */
    protected void insertGiftCertificateDocument(JdbcDataConnection dataConnection, 
            GiftCertificateDocumentIfc document) throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_GIFT_CERTIFICATE);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, inQuotes(document.getIssuingStoreID()));
        sql.addColumn(FIELD_WORKSTATION_ID, inQuotes(document.getIssuingWorkstationID()));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(document.getIssuingBusinessDate()));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, document.getIssuingTransactionSeqNumber());
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, document.getIssuingLineItemNumber());
        sql.addColumn(FIELD_GIFT_CERTIFICATE_SERIAL_NUMBER, inQuotes(document.getDocumentID()));
        sql.addColumn(FIELD_GIFT_CERTIFICATE_FACE_VALUE_AMOUNT, document.getAmount().toString());
        sql.addColumn(FIELD_TRAINING_MODE, makeStringFromBoolean(document.isTrainingMode()));
        sql.addColumn(FIELD_CURRENCY_ID, document.getAmount().getType().getCurrencyId());
        sql.addColumn(FIELD_CURRENCY_ISO_CODE, inQuotes(document.getAmount().getType().getCurrencyCode()));
        if (document.getPreviousStatus() != null)
        {
            sql.addColumn(FIELD_GIFT_CERTIFICATE_PREVIOUS_STATUS, inQuotes(document.getPreviousStatus()));
        }
        else
        {
            sql.addColumn(FIELD_GIFT_CERTIFICATE_PREVIOUS_STATUS, null);
        }
        sql.addColumn(FIELD_GIFT_CERTIFICATE_STATUS, inQuotes(document.getStatus()));

        if (document.getIssueDate() != null)
        {
            sql.addColumn(FIELD_GIFT_CERTIFICATE_ISSUE_DATE, getSQLCurrentTimestampFunction());
        }
        else
        {
            sql.addColumn(FIELD_GIFT_CERTIFICATE_ISSUE_DATE, null);
        }
        if (document.getRedeemDate() != null)
        {
            sql.addColumn(FIELD_GIFT_CERTIFICATE_REDEEM_DATE, getSQLCurrentTimestampFunction());
        }
        else
        {
            sql.addColumn(FIELD_GIFT_CERTIFICATE_REDEEM_DATE, null);
        }
        if (document.getVoidDate() != null)
        {
            sql.addColumn(FIELD_GIFT_CERTIFICATE_VOID_DATE, getSQLCurrentTimestampFunction());
        }
        else
        {
            sql.addColumn(FIELD_GIFT_CERTIFICATE_VOID_DATE, null);
        }

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e.toString());
            throw new DataException(DataException.UNKNOWN, "insertGiftCertificateDocument()", e);
        }
    }
}
