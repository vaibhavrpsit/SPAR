/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadTransactionsByExternalOrderID.java /main/1 2014/03/20 16:53:21 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/20/14 - implement search for tranasctions by external order
 *                         id
 *    cgreene   03/20/14 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation reads all of the transactions matching a specified external
 * order ID.
 * 
 * @version $Revision: /main/1 $
 * @since 14.0.1
 */
public class JdbcReadTransactionsByExternalOrderID extends JdbcReadTransaction
{
    private static final long serialVersionUID = 5836658872285210989L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadTransactionsByID.class);

    /**
     * Class constructor.
     */
    public JdbcReadTransactionsByExternalOrderID()
    {
        setName("JdbcReadTransactionsByExternalOrderID");
    }

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        logger.debug("JdbcReadTransactionsByExternalOrderID.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // grab arguments and call readTransactionsByID()
        SearchCriteriaIfc criteria = (SearchCriteriaIfc)action.getDataObject();
        LocaleRequestor localeRequestor = criteria.getLocaleRequestor();
        String externalOrderID = criteria.getExternalOrderID();

        TransactionIfc[] transactions = readTransactionsByExternalOrderID(connection, externalOrderID, localeRequestor);

        // return array
        dataTransaction.setResult(transactions);

        logger.debug("JdbcReadTransactionsByExternalOrderID.execute");
    }

    /**
     * Reads all transactions for a specified externalOrderID
     *
     * @param dataConnection a connection to the database
     * @param externalOrderID externalOrderID information to search for
     * @param localeRequestor The request locales
     * @return The list of transactions.
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    public TransactionIfc[] readTransactionsByExternalOrderID(JdbcDataConnection dataConnection,
            String externalOrderID, LocaleRequestor localeRequestor) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readTransactionsByID()");
        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables
        sql.addTable(TABLE_RETAIL_TRANSACTION, ALIAS_RETAIL_TRANSACTION);

        // add columns
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_RETAIL_STORE_ID);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_WORKSTATION_ID);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE);

        // add qualifiers for the transaction ID
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION, FIELD_EXTERNAL_ORDER_ID, inQuotes(externalOrderID));

        // execute sql and get result set
        dataConnection.execute(sql.getSQLString());
        ResultSet rs = (ResultSet)dataConnection.getResult();

        // set up transaction array
        List<TransactionIfc> results = new ArrayList<TransactionIfc>();

        // loop through result set
        try
        {
            while (rs.next())
            {
                // parse the data from the database
                int index = 0;
                String storeID = getSafeString(rs, ++index);
                String workstationID = getSafeString(rs, ++index);
                int sequenceNumber = rs.getInt(++index);
                EYSDate businessDate = getEYSDateFromString(rs, ++index);

                TransactionIfc transaction = DomainGateway.getFactory().getTransactionInstance();
                TransactionIDIfc transactionID = DomainGateway.getFactory().getTransactionIDInstance();
                transactionID.setStoreID(storeID);
                transactionID.setWorkstationID(workstationID);
                transactionID.setSequenceNumber(sequenceNumber);
                transactionID.setBusinessDate(businessDate);
                transaction.initialize(transactionID);
                TransactionIfc[] fullTransactions = readTransactionsByID(dataConnection, transaction, localeRequestor);
                results.addAll(Arrays.asList(fullTransactions));
            }
        }
        catch (DataException de)
        {
            logger.warn(de.toString());
            throw de;
        }
        catch (SQLException se)
        {
            String msg = "Error reading transactions from " + TABLE_RETAIL_TRANSACTION;
            dataConnection.logSQLException(se, msg);
            throw new DataException(DataException.SQL_ERROR, msg, se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "Error reading transactions from " + TABLE_RETAIL_TRANSACTION, e);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readTransactionsByID()");

        return results.toArray(new TransactionIfc[results.size()]);
    }

}
