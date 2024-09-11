/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcUpdateTransactionStatus.java /main/19 2012/05/21 15:50:20 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/16/12 - arrange order of businessDay column to end of
 *                         primary key to improve performance since most
 *                         receipt lookups are done without the businessDay
 *    jswan     01/05/12 - Refactor the status change of suspended transaction
 *                         to occur in a transaction so that status change can
 *                         be sent to CO as part of DTM.
 *    vtemker   10/28/11 - Reverting fix for defect 11074373 (Suspended
 *                         Canceled status not updated in CO)
 *    vtemker   10/11/11 - Fixed bug # 200 (Suspended canceled transactions
 *                         shown as Suspended in CO)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    mdecama   02/19/09 - Using method commaSeparatedList(List<String>) from
 *                         the Util class.
 *    mdecama   02/19/09 - Fixed the concatenation of layawayIDs in the
 *                         getLayawayIDSToUpdate() method.
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         6/18/2008 8:21:51 PM   Sandy Gu        fixed
 *          the result set closed defect on blue. Code reviewed by Anil B.
 *    4    360Commerce 1.3         1/25/2006 4:11:28 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:46 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:54 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:06 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:26:03    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:46     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:54     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:06     Robert Pearse
 *
 *   Revision 1.8  2004/06/29 14:18:26  cdb
 *   @scr 5860 Had new method throw exceptions that can occur during execution.
 *
 *   Revision 1.7  2004/06/29 00:36:09  cdb
 *   @scr 5860 Corrected problem caused by lack of ArtsTransaction in TransactionWriteDataTransaction
 *   that caused QueueExceptionReport to suffer a null pointer exception.
 *
 *   Revision 1.6  2004/04/09 16:55:44  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:45  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:22  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:33:46   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:41:42   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:50:20   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:09:48   msg
 * Initial revision.
 *
 *    Rev 1.2   22 Jan 2002 18:05:02   cir
 * Added type order partial
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   Dec 26 2001 17:25:24   mpm
 * Modified to properly handle order transaction retrieval.
 * Resolution for Domain SCR-14: Special Order modifications
 *
 *    Rev 1.0   Sep 20 2001 15:59:06   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation updates the status columns in the transaction tables.
 * 
 * @version $Revision: /main/19 $
 * @deprecated in 14.0; see oracle.retail.stores.domain.arts.JdbcSaveTransactionStatus
 */
public class JdbcUpdateTransactionStatus extends JdbcSaveTransaction implements ARTSDatabaseIfc
{
    /**
     * Generated Serial Version UID
     */
    private static final long serialVersionUID = -6717273697720510473L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcUpdateTransactionStatus.class);

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/19 $";

    /**
     * Class constructor.   
     */
    public JdbcUpdateTransactionStatus()
    {
        super();
        setName("JdbcUpdateTransactionStatus");
    }

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
        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdateTransactionStatus.execute()");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;
        // get transaction object
        TransactionIfc transaction = (TransactionIfc) action.getDataObject();

        // if transaction is being cancelled, layaway may need to be cancelled as well
        if (transaction.getTransactionStatus() == TransactionIfc.STATUS_SUSPENDED_RETRIEVED ||
            transaction.getTransactionStatus() == TransactionIfc.STATUS_SUSPENDED_CANCELED)
        {
            switch(transaction.getTransactionType())
            {
                case TransactionIfc.TYPE_LAYAWAY_INITIATE:
                case TransactionIfc.TYPE_LAYAWAY_DELETE:
                case TransactionIfc.TYPE_LAYAWAY_COMPLETE:
                    updateLayawayStatus(connection,
                                        transaction);
                    break;
                case TransactionIfc.TYPE_ORDER_INITIATE:
                case TransactionIfc.TYPE_ORDER_CANCEL:
                case TransactionIfc.TYPE_ORDER_PARTIAL:
                case TransactionIfc.TYPE_ORDER_COMPLETE:
                    updateOrderStatus(connection,
                                      transaction);
                    break;
                default:
                    break;
            }
        }
        // update transaction status
        updateTransactionStatus(connection,
                                transaction);

        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdateTransactionStatus.execute()");
    }

    /**
     * Cancel orders corresponding to suspended order-initiate transactions, if
     * any.
     * 
     * @param connection JdbcDataConnection
     * @param transaction TransactionIfc object key
     * @exception DataException thrown if error occurs
     */
    public void updateOrderStatus(JdbcDataConnection dataConnection,
                                  TransactionIfc transaction)
                                  throws DataException
    {
        try
        {
            SQLUpdateStatement sql =
              buildUpdateOrderStatusSQL(transaction);

            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            // no data found error is Ok
            if (de.getErrorCode() != DataException.NO_DATA)
            {
                logger.error(de);
                throw de;
            }
        }
        catch (SQLException se)
        {
            logger.error( "" + se + "");
            throw new DataException(DataException.SQL_ERROR, "updateLayawayStatus", se);
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "updateLayawayStatus", e);
        }
    }

    /**
     * Builds SQL statement used to cancel orders corresponding to suspended
     * order-initiate transactions.
     * 
     * @param transaction TransactionIfc object
     * @exception SQLException thrown if error occurs building SQL
     */
    protected SQLUpdateStatement buildUpdateOrderStatusSQL(TransactionIfc transaction) throws SQLException
    {
        OrderTransactionIfc orderTransaction =
          (OrderTransactionIfc) transaction;
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // set table
        sql.setTable(TABLE_ORDER);
        // set columns
        // set previous status equal to current status
        sql.addColumn(FIELD_ORDER_STATUS,
                      Integer.toString
                        (OrderConstantsIfc.ORDER_STATUS_SUSPENDED_CANCELED));
        sql.addColumn(FIELD_ORDER_STATUS_PREVIOUS,
                      FIELD_ORDER_STATUS);
        sql.addColumn(FIELD_ORDER_MODIFIED_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                      getSQLCurrentTimestampFunction());

        // add sub-select as qualifier for main sql statement
        sql.addQualifier(FIELD_ORDER_ID,
                         inQuotes(orderTransaction.getOrderID()));

        return (sql);
    }

    /**
     * Cancel layaways corresponding to suspended layaway-initiate transactions,
     * if any.
     * 
     * @param connection JdbcDataConnection
     * @param transaction TransactionIfc object key
     * @exception DataException thrown if error occurs
     */
    public void updateLayawayStatus(JdbcDataConnection dataConnection, TransactionIfc transaction) throws DataException
    {
        try
        {
            String layawayIDs = getLayawayIDSToUpdate(dataConnection, transaction);

            SQLUpdateStatement sql =
              buildUpdateLayawaySQL(transaction, layawayIDs);

            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            // no data found error is Ok
            if (de.getErrorCode() != DataException.NO_DATA)
            {
                logger.error(de);
                throw de;
            }
        }
        catch (SQLException se)
        {
            logger.error( "" + se + "");
            throw new DataException(DataException.SQL_ERROR, "updateLayawayStatus", se);
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "updateLayawayStatus", e);
        }
    }

    /**
     * Builds SQL statement used to cancel layaways corresponding to suspended
     * layaway-initiate transactions.
     * 
     * @param transaction TransactionIfc object
     * @param layawayIDs The layaways to update
     * @exception SQLException thrown if error occurs building SQL
     */
    protected SQLUpdateStatement buildUpdateLayawaySQL(TransactionIfc transaction, String layawayIDs)
            throws SQLException
    {
        // map new transaction status to new layaway status
        int layawayStatus = LayawayConstantsIfc.STATUS_UNDEFINED;
        switch(transaction.getTransactionStatus())
        {
        case TransactionIfc.STATUS_SUSPENDED_RETRIEVED:
            layawayStatus = LayawayConstantsIfc.STATUS_SUSPENDED_RETRIEVED;
            break;
        case TransactionIfc.STATUS_SUSPENDED_CANCELED:
        default:
            layawayStatus = LayawayConstantsIfc.STATUS_SUSPENDED_CANCELED;
            break;
        }

        SQLUpdateStatement sql = new SQLUpdateStatement();

        // set table
        sql.setTable(TABLE_LAYAWAY);
        // set columns
        // set previous status equal to current status
        sql.addColumn(FIELD_LAYAWAY_STATUS,
                Integer.toString(layawayStatus));
        sql.addColumn(FIELD_LAYAWAY_PREVIOUS_STATUS,
                FIELD_LAYAWAY_STATUS);
        sql.addColumn(FIELD_LAYAWAY_TIMESTAMP_LAST_STATUS_CHANGE,
                getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                getSQLCurrentTimestampFunction());
        // set qualifier
        sql.addQualifier(FIELD_LAYAWAY_ID + " in ( " + layawayIDs + " )");

        return(sql);
    }

    /**
     * Returns comma delimited list of layaway IDs matching the search criteria.
     * 
     * @param dataConnection The JdbcDataConnection object
     * @param transaction TransactionIfc object
     * @exception DataException thrown if error occurs building SQL
     * @exception SQLException thrown if error occurs building SQL
     */
    protected String getLayawayIDSToUpdate(JdbcDataConnection dataConnection, TransactionIfc transaction)
        throws DataException, SQLException
    {
        SQLSelectStatement sql2 = new SQLSelectStatement();
        // add tables
        sql2.addTable(TABLE_LAYAWAY, ALIAS_LAYAWAY);
        sql2.addTable(TABLE_TRANSACTION, ALIAS_TRANSACTION);
        // add select column
        sql2.addColumn(FIELD_LAYAWAY_ID);
        // add qualifiers
        sql2.addQualifier(ALIAS_TRANSACTION + "." + FIELD_RETAIL_STORE_ID + " = " +
                getStoreID(transaction));
        sql2.addQualifier(ALIAS_TRANSACTION + "." + FIELD_WORKSTATION_ID + " = " +
                getWorkstationID(transaction));
        sql2.addQualifier(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " +
                Long.toString(transaction.getTransactionSequenceNumber()));
        sql2.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " +
                getBusinessDayString(transaction));
        // add join qualifiers
        sql2.addJoinQualifier(ALIAS_TRANSACTION, FIELD_RETAIL_STORE_ID,
                ALIAS_LAYAWAY, FIELD_RETAIL_STORE_ID);
        sql2.addJoinQualifier(ALIAS_TRANSACTION, FIELD_WORKSTATION_ID,
                ALIAS_LAYAWAY, FIELD_LAYAWAY_ORIGINAL_WORKSTATION_ID);
        sql2.addJoinQualifier(ALIAS_TRANSACTION, FIELD_TRANSACTION_SEQUENCE_NUMBER,
                ALIAS_LAYAWAY, FIELD_LAYAWAY_ORIGINAL_TRANSACTION_SEQUENCE_NUMBER);
        sql2.addJoinQualifier(ALIAS_TRANSACTION, FIELD_BUSINESS_DAY_DATE,
                ALIAS_LAYAWAY, FIELD_LAYAWAY_ORIGINAL_TRANSACTION_BUSINESS_DATE);

        dataConnection.execute(sql2.getSQLString());

        // Extract data from the result set.
        ResultSet rs2 = (ResultSet)dataConnection.getResult();

        List<String> layawayIDs = new ArrayList<String>();
        while(rs2.next())
        {
            String strPropValue = getSafeString(rs2,1);
            layawayIDs.add(inQuotes(strPropValue));
        }

        return Util.commaSeparatedList(layawayIDs);

    }

    /**
     * Updates the transaction status.
     * 
     * @param connection JdbcDataConnection
     * @param transaction TransactionIfc object
     * @exception DataException thrown if error occurs
     */
    public void updateTransactionStatus(JdbcDataConnection dataConnection,
                                        TransactionIfc transaction)
        throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_TRANSACTION);

        // Fields
        sql.addColumn(FIELD_TRANSACTION_END_DATE_TIMESTAMP,
                      getTransactionEndDateString(transaction));
        sql.addColumn(FIELD_TRANSACTION_STATUS_CODE,
                      getTransactionStatus(transaction));

      
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER
                         + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE
                         + " = " + getBusinessDayString(transaction));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "updateTransactionStatus", e);
        }
    }

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

}
