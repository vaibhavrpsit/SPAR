/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveRetailTransactionDocument.java /main/4 2013/09/05 10:36:14 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    cgreene   01/10/11 - refactor blob helpers into one
 *    acadar    06/11/10 - changes for postvoid and signature capture
 *    acadar    06/08/10 - changes for signature capture, disable txn send, and
 *                         discounts
 *    acadar    06/08/10 - initial version
 *    acadar    06/08/10 - save the retail transaction document
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.awt.Point;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import oracle.retail.stores.common.data.JdbcUtilities;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.externalorder.LegalDocumentIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;
import oracle.retail.stores.persistence.utility.DatabaseBlobHelperIfc;
import oracle.retail.stores.persistence.utility.DatabaseBlobHelperFactory;

import org.apache.log4j.Logger;

/**
 * This operation saves any legal documents associated with a transaction
 *
 */
public class JdbcSaveRetailTransactionDocument extends JdbcDataOperation implements ARTSDatabaseIfc
{



    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -3733304905286162398L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveRetailTransactionDocument.class);

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
        if (logger.isDebugEnabled()) logger.debug("JdbcSaveRetailTransactionDocument.execute()");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // pull the data from the action object
        ARTSTransaction trans = (ARTSTransaction)action.getDataObject();
        SaleReturnTransactionIfc transaction = (SaleReturnTransactionIfc)trans.getPosTransaction();

        List <LegalDocumentIfc> documents = transaction.getLegalDocuments();
        Iterator<LegalDocumentIfc> it = documents.iterator();
        while (it.hasNext())
        {
            insertRetailTransactionDocument(connection, transaction, (LegalDocumentIfc)it.next());
        }

        if (logger.isDebugEnabled()) logger.debug("JdbcSaveRetailTransactionDocument.execute ends()");
    }



    /**
     * Executes the insert statement against the db.
     * <P>
     * @param  connection  a Jdbcconnection object
     * @param  transaction that contains the legal document
     * @exception DataException upon error
     */
    protected void insertRetailTransactionDocument(JdbcDataConnection connection, SaleReturnTransactionIfc transaction, LegalDocumentIfc document) throws DataException
    {
        // Define the table
        SQLInsertStatement sql = new SQLInsertStatement();
        sql.setTable(TABLE_RETAIL_TRANSACTION_DOCUMENT);


        // Fields
        String storeId = transaction.getWorkstation().getStoreID();
        sql.addColumn(FIELD_RETAIL_STORE_ID, makeSafeString(storeId));


        String workstationId = transaction.getWorkstation().getWorkstationID();
        sql.addColumn(FIELD_WORKSTATION_ID, makeSafeString(workstationId));


        Integer seqNumber = Integer.valueOf(String.valueOf(transaction.getTransactionSequenceNumber()));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, seqNumber);

        String dateString = getBusinessDayString(transaction);
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, dateString);


        // Add columns and their values and qualifiers
        sql.addColumn(FIELD_DOCUMENT_ID, makeSafeString(document.getId()));
        sql.addColumn(FIELD_DOCUMENT_TYPE, makeSafeString(Integer.toString(document.getType())));
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());


        try
        {
            connection.execute(sql.getSQLString());


            HashMap<String, Object> map = new HashMap<String, Object>(5);
            map.put(FIELD_RETAIL_STORE_ID, storeId);
            map.put(FIELD_WORKSTATION_ID, workstationId);
            map.put(FIELD_TRANSACTION_SEQUENCE_NUMBER, seqNumber);

            SimpleDateFormat format = new SimpleDateFormat(JdbcUtilities.YYYYMMDD_DATE_FORMAT_STRING);
            dateString = format.format(transaction.getBusinessDay().dateValue());
            map.put(FIELD_BUSINESS_DAY_DATE, dateString);
            map.put(FIELD_DOCUMENT_ID, document.getId());

            DatabaseBlobHelperIfc helper = DatabaseBlobHelperFactory.getInstance().getDatabaseBlobHelper(connection.getConnection());
            String signatureData = getData(document.getSignature());
            String documentData = getData(document.getTerms());
            if(helper != null)
            {
                helper.updateBlob(connection.getConnection(),
                              TABLE_RETAIL_TRANSACTION_DOCUMENT,
                              FIELD_SIGNATURE_IMAGE,
                              signatureData.getBytes(),
                              map);
                if (documentData != null)
                {
                    helper.updateBlob(connection.getConnection(),
                              TABLE_RETAIL_TRANSACTION_DOCUMENT,
                              FIELD_DOCUMENT_IMAGE,
                              documentData.getBytes(),
                              map);
                }
            }
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "insertRetailTransactionDocument", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "insertRetailTransactionDocument", e);
        }

    }

    /**
     * Gets the blob
     * @param lineItem
     * @return
     */
    public String getData(Serializable legalData)
    {
        Point[] data = (Point[]) legalData;
        StringBuffer value = new StringBuffer();

        if (data != null)
        {
            Point p = null;
            for (int i = 0; i < data.length; i++)
            {
                p = data[i];
                value.append("x" + Integer.toString(p.x) + "y" + Integer.toString(p.y));
            }
        }
        else
        {
            value.append("null");
        }
        return value.toString();
    }

    /**
     * Returns the string value for the business day
     * <P>
     * @param  transaction     a pos transaction
     * @return  The business day
     */
    public String getBusinessDayString(TransactionIfc transaction)
    {
        return (dateToSQLDateString(transaction.getBusinessDay()));
    }



}
