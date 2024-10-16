/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveTillOpenCloseTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:04 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:11:24 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:44 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:50 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:04 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:40    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:44     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:50     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:04     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:46  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:33:02   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:40:22   msg
 * Initial revision.
 *
 *    Rev 1.1   May 14 2002 15:29:58   mpm
 * Corrected transaction type method.
 * Resolution for Domain SCR-50: db2 port fixes
 *
 *    Rev 1.0   May 12 2002 20:19:06   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.transaction.TillOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This operation performs inserts into the TillOpenCloseTransaction table.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcSaveTillOpenCloseTransaction extends JdbcSaveTransaction
{
    private static final long serialVersionUID = -4561335696222197421L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveTillOpenCloseTransaction.class);

    /**
     * Class constructor.
     */
    public JdbcSaveTillOpenCloseTransaction()
    {
        super();
        setName("JdbcSaveTillOpenCloseTransaction");
    }

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction
     * @param dataConnection
     * @param action
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,           DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcSaveTillOpenCloseTransaction.execute()");

        // get connection, transaction
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
        TillOpenCloseTransactionIfc tocTransaction =
          (TillOpenCloseTransactionIfc) action.getDataObject();
        saveTillOpenCloseTransaction(connection,
                                     tocTransaction);

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcSaveTillOpenCloseTransaction.execute()");
    }

    /**
       Saves a till open/close transaction.
       @param  dataConnection  connection to the db
       @param  transaction     a till open/close transaction
       @exception DataException upon error
     */
    public void saveTillOpenCloseTransaction(JdbcDataConnection dataConnection,
                                              TillOpenCloseTransactionIfc transaction)
    throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();
        // set table
        sql.setTable(ARTSDatabaseIfc.TABLE_TILL_OPEN_CLOSE_TRANSACTION);
        // set columns
        sql.addColumn(ARTSDatabaseIfc.FIELD_RETAIL_STORE_ID,
                      inQuotes(transaction.getTransactionIdentifier().getStoreID()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_WORKSTATION_ID,
                      inQuotes(transaction.getTransactionIdentifier().getWorkstationID()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_TRANSACTION_SEQUENCE_NUMBER,
                      Long.toString(transaction.getTransactionSequenceNumber()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_BUSINESS_DAY_DATE,
                      dateToSQLDateString(transaction.getBusinessDay()));

        int transactionType = transaction.getTransactionType();
        TillIfc till = transaction.getTill();
        String operatorID = null;
        EYSDate transactionTimestamp = null;
        if (transactionType == TransactionIfc.TYPE_CLOSE_TILL)
        {
            operatorID = till.getSignOffOperator().getEmployeeID();
            transactionTimestamp = till.getCloseTime();
        }
        else
        {
            operatorID = till.getSignOnOperator().getEmployeeID();
            transactionTimestamp = till.getOpenTime();
        }

        sql.addColumn(ARTSDatabaseIfc.FIELD_TENDER_REPOSITORY_ID,
                      inQuotes(till.getTillID()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_OPERATOR_ID,
                      inQuotes(operatorID));
        sql.addColumn(ARTSDatabaseIfc.FIELD_TRANSACTION_TYPE_CODE,
                      inQuotes(transactionType) );
        sql.addColumn(ARTSDatabaseIfc.FIELD_TILL_STATUS_CODE,
                      Integer.toString(till.getStatus()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_TRANSACTION_TIMESTAMP,
                      dateToSQLTimestampString(transactionTimestamp.dateValue()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_RECORDED_WORKSTATION_ID,
                inQuotes(transaction.getTransactionIdentifier().getWorkstationID()));
        sql.addColumn(ARTSDatabaseIfc.FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
        sql.addColumn(ARTSDatabaseIfc.FIELD_RECORD_CREATION_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
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
            logger.error(
                         Util.throwableToString(e));
            throw new DataException(DataException.UNKNOWN, "insertTillOpenCloseTransaction", e);
        }
    }

}
