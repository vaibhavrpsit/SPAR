/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcCancelSuspendedTransactions.java /main/15 2013/09/05 10:36:14 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    jswan     01/05/12 - Refactor the status change of suspended transaction
 *                         to occur in a transaction so that status change can
 *                         be sent to CO as part of DTM.
 *    vtemker   10/28/11 - Reverting fix for defect 11074373 (Suspended
 *                         Canceled status not updated in CO)
 *    vtemker   10/11/11 - Fixed bug # 200 (Suspended canceled transactions
 *                         shown as Suspended in CO)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/2/2008 5:27:37 PM    Anda D. Cadar   CR
 *         31567: Update order status to cancel suspended during end of day.
 *         Change reviewed by Jack Swan
 *    4    360Commerce 1.3         1/25/2006 4:11:06 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:36 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:35 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:53 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:26:02    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:36     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:35     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:53     Robert Pearse
 *
 *   Revision 1.7.2.1  2004/11/02 23:07:34  jdeleau
 *   @scr 7583 Remove invalid db2 syntax.
 *
 *
 *   Revision 1.7  2004/08/12 12:53:01  kll
 *   @scr 0: deprecation fixes
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:46  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:13  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Jan 27 2004 14:05:46   kll
 * current version of MySQL does not support certain subqueries
 * Resolution for 3507: SQLException; Syntax error during Store close when Suspended Trans are present and deleted
 *
 *    Rev 1.0   Aug 29 2003 15:30:16   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:35:26   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:46:26   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:06:34   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:58:10   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:52   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSStatusIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation cancels all the suspended transactions for a store and
 * business date, setting the status to "Suspended-Canceled".
 * 
 * @version $Revision: /main/15 $
 * @deprecated in 14.0; see oracle.retail.stores.domain.arts.JdbcCancelSuspendedOrdersAndLayaways and
 * oracle.retail.stores.domain.arts.JdbcSaveStatusChangeTransaction
 */
public class JdbcCancelSuspendedTransactions extends JdbcSaveTransaction implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -5304475832378144949L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcCancelSuspendedTransactions.class);

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * Class constructor.   
     */
    public JdbcCancelSuspendedTransactions()
    {
        super();
        setName("JdbcCancelSuspendedTransactions");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcCancelSuspendedTransactions.execute()");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;
        // get transaction object
        TransactionIfc transaction = (TransactionIfc) action.getDataObject();

        // cancel any layaways which may be lurking about;
        // i.e., any layaways corresponding to suspended
        // layaway-initiate transactions
        cancelSuspendedLayaways(connection, transaction);
        //CR 31567 updateOrderStatus
        updateOrderStatus(connection, transaction);
        // update transactions
        updateTransactionStatus(connection, transaction);

        if (logger.isDebugEnabled()) logger.debug( "JdbcCancelSuspendedTransactions.execute()");
    }

    /**
     * Cancel layaways corresponding to suspended layaway-initiate transactions,
     * if any.
     * 
     * @param connection JdbcDataConnection
     * @param transaction TransactionIfc object key
     * @exception DataException thrown if error occurs
     */
    public void cancelSuspendedLayaways(JdbcDataConnection dataConnection,
                                        TransactionIfc transaction)
                                        throws DataException
    {
        try
        {
            SQLUpdateStatement[] sql =  buildCancelSuspendedLayawaysSQL(transaction, dataConnection);

            for(int i=0; i<sql.length; i++)
            {
                dataConnection.execute(sql[i].getSQLString());
            }
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
            throw new DataException(DataException.SQL_ERROR, "cancelSuspendedLayaways", se);
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "cancelSuspendedLayaways", e);
        }
    }

    /**
     * Create an SQL statement for updating layaway status.  This actually has to do
     * a database query before building the sql because we must work without relying on sub-selects.
     *
     *  @param transaction
     *  @param dataConnection
     *  @return SQL string
     *  @throws SQLException if there was a problem getting the sub-select data
     *  @since 7.0
     */
    protected SQLUpdateStatement[] buildCancelSuspendedLayawaysSQL(TransactionIfc transaction, JdbcDataConnection dataConnection)
      throws SQLException
    {
        SQLSelectStatement sqlSelect = new SQLSelectStatement();
        // add tables
        sqlSelect.addTable(TABLE_TRANSACTION);
        // add select column
        sqlSelect.addColumn(FIELD_WORKSTATION_ID);
        sqlSelect.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER);
        // add qualifiers
        sqlSelect.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sqlSelect.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        sqlSelect.addQualifier(FIELD_TRANSACTION_STATUS_CODE + " = " + String.valueOf(TransactionIfc.STATUS_SUSPENDED));
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>(4);
        try
        {
            dataConnection.execute(sqlSelect.getSQLString());
            HashMap<String, String> map = new HashMap<String, String>(4);

            ResultSet rs = (ResultSet) dataConnection.getResult();

            while (rs.next())
            {
                int index = 0;
                String workstationID = getSafeString(rs, ++index);
                String transactionSequenceNumber = getSafeString(rs, ++index);
                map.put("ws", workstationID);
                map.put("seq", transactionSequenceNumber);
                list.add(map);
            }
        }
        catch(DataException de)
        {
            logger.error(de.toString());
        }

        // Get an array of update statements.
        SQLUpdateStatement[] updates = new SQLUpdateStatement[list.size()];
        for(int i=0; i<list.size(); i++)
        {
            SQLUpdateStatement sqlUpdate = new SQLUpdateStatement();
            sqlUpdate.setTable(TABLE_LAYAWAY);
            sqlUpdate.addColumn(FIELD_LAYAWAY_STATUS, Integer.toString(LayawayConstantsIfc.STATUS_SUSPENDED_CANCELED));
            // set previous status equal to current status
            sqlUpdate.addColumn(FIELD_LAYAWAY_PREVIOUS_STATUS, FIELD_LAYAWAY_STATUS);
            sqlUpdate.addColumn(FIELD_LAYAWAY_TIMESTAMP_LAST_STATUS_CHANGE, getSQLCurrentTimestampFunction());
            sqlUpdate.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
            // Qualifiers
            HashMap map = (HashMap) list.get(i);
            sqlUpdate.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
            sqlUpdate.addQualifier(FIELD_LAYAWAY_ORIGINAL_TRANSACTION_BUSINESS_DATE + " = " + getBusinessDayString(transaction));
            sqlUpdate.addQualifier(FIELD_LAYAWAY_ORIGINAL_TRANSACTION_SEQUENCE_NUMBER, map.get("seq").toString());
            sqlUpdate.addQualifier(FIELD_LAYAWAY_ORIGINAL_WORKSTATION_ID, "'"+map.get("ws").toString()+"'");
            updates[i] = sqlUpdate;
        }
        return updates;
    }

    /**
     * Updates the transaction status.
     * 
     * @param connection JdbcDataConnection
     * @param transaction TransactionIfc object
     * @exception DataException thrown if error occurs
     */
    protected void updateTransactionStatus(JdbcDataConnection dataConnection,
                                           TransactionIfc transaction)
        throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_TRANSACTION);

        // Fields
        if (transaction.getTimestampEnd() != null)
        {
            sql.addColumn(FIELD_TRANSACTION_END_DATE_TIMESTAMP,
                          getTransactionEndDateString(transaction));
        }
        sql.addColumn(FIELD_TRANSACTION_STATUS_CODE,
                String.valueOf(TransactionIfc.STATUS_SUSPENDED_CANCELED));
  
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " +
                         getStoreID(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " +
                         getBusinessDayString(transaction));
        sql.addQualifier(FIELD_TRANSACTION_STATUS_CODE + " = " +
                         String.valueOf(TransactionIfc.STATUS_SUSPENDED));

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
     * Updates the order status.
     * 
     * @param connection JdbcDataConnection
     * @param transaction TransactionIfc object
     * @exception DataException thrown if error occurs
     */
    protected void updateOrderStatus(JdbcDataConnection dataConnection,
                                           TransactionIfc transaction)
        throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_ORDER);

        // Fields
        if (transaction.getTimestampEnd() != null)
        {
            sql.addColumn(FIELD_TRANSACTION_END_DATE_TIMESTAMP,
                          getTransactionEndDateString(transaction));
        } 
        sql.addColumn(FIELD_ORDER_STATUS,
                      String.valueOf(TransactionIfc.STATUS_SUSPENDED_CANCELED));

        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " +
                         getStoreID(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " +
                         getBusinessDayString(transaction));
        
        // for orders the status is unknown if order is suspended
        sql.addQualifier(FIELD_ORDER_STATUS + " = " +
                         String.valueOf(EYSStatusIfc.STATUS_UNDEFINED));
       

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
     * Returns the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
