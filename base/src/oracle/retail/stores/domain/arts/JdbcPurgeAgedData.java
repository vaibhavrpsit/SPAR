/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcPurgeAgedData.java /main/19 2012/05/21 15:50:18 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/21/12 - XbranchMerge cgreene_bug-13951397 from
 *                         rgbustores_13.5x_generic
 *    cgreene   05/16/12 - arrange order of businessDay column to end of
 *                         primary key to improve performance since most
 *                         receipt lookups are done without the businessDay
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/27/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    10   360Commerce 1.9         4/12/2008 5:44:57 PM   Christian Greene
 *         Upgrade StringBuffer to StringBuilder
 *    9    360Commerce 1.8         6/8/2006 3:54:24 PM    Brett J. Larsen CR
 *         18490 - UDM - columns CD_MTH_PRDV, CD_SCP_PRDV and CD_BAS_PRDV's
 *         type was changed to INTEGER
 *    8    360Commerce 1.7         6/6/2006 6:03:44 PM    Brett J. Larsen CR
 *         18490 - UDM - TimeDatePriceDerivationRuleEligibility
 *         (CO_EL_TM_PRDV) - Effective/Expiration Dates changed to type:
 *         TIMESTAMP
 *    7    360Commerce 1.6         6/1/2006 12:48:12 PM   Charles D. Baker
 *         Remove unused imports
 *    6    360Commerce 1.5         5/31/2006 10:39:32 AM  Brett J. Larsen CR
 *         18490 - UDM - removing code that supports deleted tables
 *    5    360Commerce 1.4         5/30/2006 10:07:14 AM  Brett J. Larsen CR
 *         18490 - UDM - eventID type changed to int
 *    4    360Commerce 1.3         4/27/2006 7:26:57 PM   Brett J. Larsen CR
 *         17307 - remove inventory functionality - stage 2
 *    3    360Commerce 1.2         3/31/2005 4:28:39 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:57 PM  Robert Pearse   
 *
 *   Revision 1.8  2004/07/08 23:34:31  jdeleau
 *   @scr 6086 payroll till pay out on post void was crashing the system.  In fact it
 *   was not implemented at all.  Now its implemented just as normal till pay out.
 *
 *   Revision 1.7  2004/06/02 19:01:53  lzhao
 *   @scr 4670: add shippingRecords table.
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
 *   Revision 1.3  2004/02/12 17:13:16  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Sep 03 2003 16:21:38   mrm
 * DB2 support
 * Resolution for POS SCR-3357: Add support needed by RSS
 * 
 *    Rev 1.0   Aug 29 2003 15:31:34   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   May 18 2003 09:06:24   mpm
 * Merged 5.1 changes into 6.0
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.0   Feb 15 2003 17:32:26   mpm
 * Initial revision.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.1   Feb 15 2003 06:52:04   mpm
 * Merged changes from K*B extendyourstore tree.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.0   24 Jul 2002 15:36:12   jgs
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLDeleteStatement;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.financial.ReportingPeriodIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.transaction.PurgeCriteriaIfc;
import oracle.retail.stores.domain.transaction.PurgeResultIfc;
import oracle.retail.stores.domain.transaction.PurgeTransactionEntry;
import oracle.retail.stores.domain.transaction.PurgeTransactionEntryIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This operation attempts to remove all aged, transient data from the database;
 * this includes transactions, orders and layaways which are complete and over a
 * certain number of days old. It also includes Advanced Pricing Data that has
 * expired and Timed Item Maintenance that has been applied.
 * 
 * @version $Revision: /main/19 $
 */
public class JdbcPurgeAgedData extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -2633831218121290613L;

    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcPurgeAgedData.class);

    /**
       revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/19 $";

    /**
       Permanent Price Change Event Code.
     */
    public static String PERM_TYPE_CODE = "PPC";
    /**
       Active status code
     */
    public static String ACTIVE_STATUS_CODE = "ACTIVE";
    /**
       Temporary Price Change Event Code.
     */
    public static String TEMP_TYPE_CODE = "TPC";
    /**
       Expired status code
     */
    public static String EXPIRED_STATUS_CODE = "EXPIRED";
    /**
       Purge order indicator
     */
    public static int PURGE_ORDERS = 0;
    /**
       Purge layaway indicator
     */
    public static int PURGE_LAYAWAYS = 1;


    /**
        Class constructor.
     */
    public JdbcPurgeAgedData()
    {
        super();
        setName("JdbcPurgeAgedData");
    }

    /**
        Entry point for processing data to be purged.
        <P>
        @param  dataTransaction     The data transaction
        @param  dataConnection      The connection to the data source
        @param  action              The information passed by the valet
        @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
                        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcPurgeAgedData.execute()");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // Get purge criteria.
        PurgeCriteriaIfc criteria = (PurgeCriteriaIfc)action.getDataObject();
        PurgeResultIfc purgeResult = purgeData(connection, criteria);
        dataTransaction.setResult(purgeResult);

        if (logger.isDebugEnabled()) logger.debug( "JdbcPurgeAgedData.execute()");
    }

    /**
       Purges data from various tables.
       @param connection connection to database
       @param criteria purge criteria
       @return purge results
       @exception DataException thrown if error occurs
     */
    protected PurgeResultIfc purgeData(JdbcDataConnection connection,
                                       PurgeCriteriaIfc criteria)
    throws DataException
    {
        PurgeResultIfc purgeResult =
          DomainGateway.getFactory().getPurgeResultInstance();
        // Purge transaction
        if (criteria.getTransactionAge() > -1)
        {
            purgeResult.setTransactionsPurged(purgeTransactions(connection, criteria));
        }

        // Purge Till, Register, Store, Bank Deposit Tender History Data
        if (criteria.getTransactionAge() > -1)
        {
            purgeResult.setFinancialHistoryPurged(purgeFinancialHistory(connection, criteria));
        }

        // Purge Layaway Data
        if (criteria.getLayawayAge() > -1)
        {
            purgeOrdersOrLayaways(connection, criteria, PURGE_LAYAWAYS, purgeResult);
        }

        // Purge Order Data
        if (criteria.getOrderAge() > -1)
        {
            purgeOrdersOrLayaways(connection, criteria, PURGE_ORDERS, purgeResult);
        }

        // Purge Advanced Pricing Data
        if (criteria.getAdvancedPricingAge() > -1)
        {
            purgeResult.setAdavncedPricingRulesPurged(purgeAdvancedPricingRules(connection, criteria));
        }

        // Purge Timed Item Data
        purgeResult.setTimedItemsPurged(purgeTimedItemPricing(connection, criteria));

        return(purgeResult);
    }

    /**
       Removes aged data from the transaction tables.
       <P>
       @param  dataConnection  connection to the db
       @param  criteria        contains the criteria on which the data should be deleted.
       @return the number of transactions deleted
       @exception  DataException upon error
     */
    public int purgeTransactions(JdbcDataConnection connection,
                                  PurgeCriteriaIfc criteria) throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        // set table, add columns and qualifiers
        sql.addTable(ARTSDatabaseIfc.TABLE_TRANSACTION);
        addSelectTransactionColumns(sql, true);
        addSelectTransactionQualifiers(sql, criteria);

        ArrayList entryList = new ArrayList();

        try
        {
            // execute sql and get result set
            connection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet) connection.getResult();
            // parse result set
            entryList = parseSelectTransactionResultSet(rs, true);
            rs.close();
        }
        catch (DataException de)
        {
            logger.error(
                        de.toString());
            throw de;
        }
        catch (SQLException se)
        {
            logger.error(
                         Util.throwableToString(se));
            connection.logSQLException(se, "select transaction IDs");
            throw new DataException(DataException.SQL_ERROR, "select transaction IDs", se);
        }
        catch (Exception e)
        {
            logger.error(
                         Util.throwableToString(e));
            throw new DataException(DataException.UNKNOWN, "transaction table", e);
        }

        int deleteCount = 0;
        for (int i = 0; i < entryList.size(); i++)
        {
            PurgeTransactionEntryIfc entry = (PurgeTransactionEntryIfc)entryList.get(i);
            boolean deleted = deleteTransaction(connection, entry);
            if (deleted)
            {
                deleteCount++;
            }
        }

        if (logger.isDebugEnabled()) logger.debug( "JdbcPurgeAgedData.readTransactionsByID()");
        return deleteCount;
    }

    /**
       Add columns to SQL statement for selecting transaction IDs.
       @param sql SQLSelectStatement under construction
     */
    protected void addSelectTransactionColumns(SQLSelectStatement sql, boolean getType)
    {
        sql.addColumn(FIELD_RETAIL_STORE_ID);
        sql.addColumn(FIELD_WORKSTATION_ID);
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addColumn(FIELD_BUSINESS_DAY_DATE);
        if (getType)
        {
            sql.addColumn(FIELD_TRANSACTION_TYPE_CODE);
        }
    }

    /**
       Adds qualifiers for SQL statement to be used for selecting transaction
       IDs.
       @param sql SQLSelectStatement under construction
       @param tLogEntry TLog entry object to be used as key
     */
    protected void addSelectTransactionQualifiers(SQLSelectStatement sql,
                                                  PurgeCriteriaIfc criteria)
    {
        EYSDate purgeDate = getPurgeDate(criteria.getTransactionAge());

        sql.addQualifier(FIELD_RECORD_CREATION_TIMESTAMP + " < " +
                             dateToSQLTimestampString(purgeDate));

        sql.addQualifier(FIELD_TRANSACTION_STATUS_CODE,
            String.valueOf(TransactionIfc.STATUS_COMPLETED));

        // Do not include hese transactions.  They should be deleted at
        // the same time as the Layaway or Order they are associated with.
        sql.addQualifier("NOT " + FIELD_TRANSACTION_TYPE_CODE + " = " +
            inQuotes(String.valueOf(TransactionIfc.TYPE_LAYAWAY_PAYMENT)));

        sql.addQualifier("NOT " + FIELD_TRANSACTION_TYPE_CODE + " = " +
            inQuotes(String.valueOf(TransactionIfc.TYPE_LAYAWAY_INITIATE)));

        sql.addQualifier("NOT " + FIELD_TRANSACTION_TYPE_CODE + " = " +
            inQuotes(String.valueOf(TransactionIfc.TYPE_LAYAWAY_DELETE)));

        sql.addQualifier("NOT " + FIELD_TRANSACTION_TYPE_CODE + " = " +
            inQuotes(String.valueOf(TransactionIfc.TYPE_LAYAWAY_COMPLETE)));

        sql.addQualifier("NOT " + FIELD_TRANSACTION_TYPE_CODE + " = " +
            inQuotes(String.valueOf(TransactionIfc.TYPE_ORDER_INITIATE)));

        sql.addQualifier("NOT " + FIELD_TRANSACTION_TYPE_CODE + " = " +
            inQuotes(String.valueOf(TransactionIfc.TYPE_ORDER_PARTIAL)));

        sql.addQualifier("NOT " + FIELD_TRANSACTION_TYPE_CODE + " = " +
            inQuotes(String.valueOf(TransactionIfc.TYPE_ORDER_COMPLETE)));

        sql.addQualifier("NOT " + FIELD_TRANSACTION_TYPE_CODE + " = " +
            inQuotes(String.valueOf(TransactionIfc.TYPE_ORDER_CANCEL)));

        // Might want to add this; should have a boolean in the
        // criteria to indicate whether or not to used this qualifier.
        // sql.addQualifier(" NOT " + FIELD_TRANSACTION_ARCHIVE_BATCH_IDENTIFIER + " = -1");
    }

    /**
       Parses result set and returns list of entries. <P>
       @param rs result set
       @return list of entries
       @exception SQLException thrown if SQL error occurs
       @exception DataException if error retrieving EYSDate from string
     */
    protected ArrayList parseSelectTransactionResultSet(ResultSet rs, boolean getType)
    throws SQLException, DataException
    {
        ArrayList entryList = new ArrayList();
        PurgeTransactionEntryIfc entry = null;
        int index = 0;

        while (rs.next())
        {
            index = 0;

            // Note to MMANN: Add PurgeTransactionEntry and PurgeCriteria to
            // the Domain Factory.
            entry = new PurgeTransactionEntry();
            entry.setTransactionID(DomainGateway.getFactory().getTransactionIDInstance());
            entry.getTransactionID().setStoreID(getSafeString(rs, ++index));
            entry.getTransactionID().setWorkstationID(getSafeString(rs, ++index));
            entry.getTransactionID().setSequenceNumber(rs.getInt(++index));
            entry.setBusinessDate(getEYSDateFromString(rs, ++index));
            if (getType)
            {
                entry.setTransactionType(rs.getInt(++index));
            }
            // add entry to list
            entryList.add(entry);
        }

        return(entryList);
    }

    /**
       Removes rows from each table associated with a single transaction;
       This method issues an SQL DELETE command for each table that might
       contain a row associated with this type of transaction.
       <P>
       @param  dataConnection  connection to the db
       @param  entry contains the transaction id informationi
       @return true if the transaction was deleted without error.
     */
    public boolean deleteTransaction(JdbcDataConnection connection,
        PurgeTransactionEntryIfc entry)
    {
        boolean transactionDeleted = false;
        SQLDeleteStatement sql = new SQLDeleteStatement();
        sql.addQualifier(FIELD_RETAIL_STORE_ID,
                    inQuotes(entry.getTransactionID().getStoreID()));
        sql.addQualifier(FIELD_WORKSTATION_ID,
                    entry.getTransactionID().getWorkstationID());
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER,
                    String.valueOf(entry.getTransactionID().getSequenceNumber()));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE,
                dateToSQLDateString(entry.getBusinessDate()));

        ArrayList tableNames = getTransactionTableNames(entry.getTransactionType());

        for(int i = 0; i < tableNames.size(); i++)
        {
            String tableName = (String)tableNames.get(i);
            sql.setTable(tableName);
            try
            {
                connection.execute(sql.getSQLString());
                transactionDeleted = true;
            }
            catch (DataException de)
            {
                // It is very likely that rows will not exist in all tables;
                // many failures will occur.
                if (logger.isInfoEnabled()) logger.info(
                    "JdbcPurgeAgedData.deleteTransaction() - row deletion failed.");
            }
        }

        return transactionDeleted;
    }

    /**
       Get a list of tables to delete based on the the transaction type.
       <P>
       @param  transactionType  transaction type
       @return an array list of strings
     */
    protected ArrayList getTransactionTableNames(int transactionType)
    {
        ArrayList tableNames = new ArrayList();

        tableNames.add(TABLE_TRANSACTION);

        switch(transactionType)
        {
            case TransactionIfc.TYPE_VOID:

                tableNames.add(TABLE_CONTROL_TRANSACTION);
                tableNames.add(TABLE_POST_VOID_TRANSACTION);
                tableNames.add(TABLE_RETAIL_TRANSACTION_LINE_ITEM);
                tableNames.add(TABLE_CREDIT_DEBIT_CARD_TENDER_LINE_ITEM);
                tableNames.add(TABLE_CHECK_TENDER_LINE_ITEM);
                tableNames.add(TABLE_COUPON_TENDER_LINE_ITEM);
                tableNames.add(TABLE_GIFT_CARD_TENDER_LINE_ITEM);
                tableNames.add(TABLE_GIFT_CERTIFICATE_TENDER_LINE_ITEM);
                tableNames.add(TABLE_SEND_CHECK_TENDER_LINE_ITEM);
                tableNames.add(TABLE_STORE_CREDIT_TENDER_LINE_ITEM);
                tableNames.add(TABLE_TENDER_LINE_ITEM);
                tableNames.add(TABLE_TRAVELERS_CHECK_TENDER_LINE_ITEM);
                break;

            case TransactionIfc.TYPE_SALE:
            case TransactionIfc.TYPE_RETURN:
            case TransactionIfc.TYPE_HOUSE_PAYMENT:
            case TransactionIfc.TYPE_LAYAWAY_PAYMENT:
            case TransactionIfc.TYPE_LAYAWAY_INITIATE:
            case TransactionIfc.TYPE_LAYAWAY_DELETE:
            case TransactionIfc.TYPE_LAYAWAY_COMPLETE:
            case TransactionIfc.TYPE_ORDER_INITIATE:
            case TransactionIfc.TYPE_ORDER_PARTIAL:
            case TransactionIfc.TYPE_ORDER_COMPLETE:
            case TransactionIfc.TYPE_ORDER_CANCEL:

                tableNames.add(TABLE_RETAIL_TRANSACTION);
                tableNames.add(TABLE_SHIPPING_RECORDS);
                tableNames.add(TABLE_SALE_RETURN_LINE_ITEM);
                tableNames.add(TABLE_RETAIL_PRICE_MODIFIER);
                tableNames.add(TABLE_RETAIL_PRICE_MODIFIER);
                tableNames.add(TABLE_COMMISSION_MODIFIER);
                tableNames.add(TABLE_GIFT_CARD);
                tableNames.add(TABLE_RETAIL_TRANSACTION_LINE_ITEM);
                tableNames.add(TABLE_CREDIT_DEBIT_CARD_TENDER_LINE_ITEM);
                tableNames.add(TABLE_CHECK_TENDER_LINE_ITEM);
                tableNames.add(TABLE_COUPON_TENDER_LINE_ITEM);
                tableNames.add(TABLE_GIFT_CARD_TENDER_LINE_ITEM);
                tableNames.add(TABLE_GIFT_CERTIFICATE_TENDER_LINE_ITEM);
                tableNames.add(TABLE_SEND_CHECK_TENDER_LINE_ITEM);
                tableNames.add(TABLE_STORE_CREDIT_TENDER_LINE_ITEM);
                tableNames.add(TABLE_TENDER_LINE_ITEM);
                tableNames.add(TABLE_TRAVELERS_CHECK_TENDER_LINE_ITEM);
                break;

            case TransactionIfc.TYPE_NO_SALE:

                tableNames.add(TABLE_NO_SALE_TRANSACTION);
                break;

            case TransactionIfc.TYPE_OPEN_STORE:
            case TransactionIfc.TYPE_CLOSE_STORE:

                tableNames.add(TABLE_CONTROL_TRANSACTION);
                tableNames.add(TABLE_STORE_OPEN_CLOSE_TRANSACTION);
                tableNames.add(TABLE_TENDER_MEDIA_LINE_ITEM);
                break;

            case TransactionIfc.TYPE_BANK_DEPOSIT_STORE:

                tableNames.add(TABLE_CONTROL_TRANSACTION);
                break;

            case TransactionIfc.TYPE_OPEN_REGISTER:
            case TransactionIfc.TYPE_CLOSE_REGISTER:

                tableNames.add(TABLE_CONTROL_TRANSACTION);
                tableNames.add(TABLE_WORKSTATION_OPEN_CLOSE_TRANSACTION);
                break;

            case TransactionIfc.TYPE_OPEN_TILL:
            case TransactionIfc.TYPE_CLOSE_TILL:

                tableNames.add(TABLE_CONTROL_TRANSACTION);
                tableNames.add(TABLE_TILL_OPEN_CLOSE_TRANSACTION);
                break;

            case TransactionIfc.TYPE_SUSPEND_TILL:

                tableNames.add(TABLE_CONTROL_TRANSACTION);
                break;

            case TransactionIfc.TYPE_RESUME_TILL:

                tableNames.add(TABLE_CONTROL_TRANSACTION);
                break;

            case TransactionIfc.TYPE_PAYIN_TILL:
            case TransactionIfc.TYPE_PAYOUT_TILL:
            case TransactionIfc.TYPE_PAYROLL_PAYOUT_TILL:

                tableNames.add(TABLE_FINANCIAL_ACCOUNTING_TRANSACTION);
                tableNames.add(TABLE_FUNDS_RECEIPT_TRANSACTION);
                break;

            case TransactionIfc.TYPE_LOAN_TILL:

                tableNames.add(TABLE_FINANCIAL_ACCOUNTING_TRANSACTION);
                tableNames.add(TABLE_TENDER_MEDIA_LINE_ITEM);
                tableNames.add(TABLE_TENDER_LOAN_TRANSACTION);
                break;

            case TransactionIfc.TYPE_PICKUP_TILL:

                tableNames.add(TABLE_FINANCIAL_ACCOUNTING_TRANSACTION);
                tableNames.add(TABLE_TENDER_MEDIA_LINE_ITEM);
                tableNames.add(TABLE_TENDER_PICKUP_TRANSACTION);
                break;

        }

        return tableNames;
    }

    /**
       Removes aged data from the Order and Layaway tables.
       <P>
       @param  dataConnection  connection to the db
       @param  criteria        contains the criteria on which the data should be deleted.
       @return the number of transactions deleted
       @exception  DataException upon error
     */
    public void purgeOrdersOrLayaways(JdbcDataConnection connection,
        PurgeCriteriaIfc criteria, int purgeIndicator, PurgeResultIfc purgeResult)
            throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        EYSDate purgeDate      = null;
        if (purgeIndicator == PURGE_ORDERS)
        {
            // Get "completed" orders;
            sql.addTable(TABLE_ORDER);
            sql.addColumn(FIELD_ORDER_ID);
            sql.addQualifier("( " + FIELD_ORDER_STATUS + " = " +
                            Integer.toString(OrderConstantsIfc.ORDER_STATUS_COMPLETED) +
                            " or " +
                            FIELD_ORDER_STATUS + " = " +
                            Integer.toString(OrderConstantsIfc.ORDER_STATUS_CANCELED) +
                            " or " +
                            FIELD_ORDER_STATUS + " = " +
                            Integer.toString(OrderConstantsIfc.ORDER_STATUS_SUSPENDED_CANCELED) +
                            " or " +
                            FIELD_ORDER_STATUS + " = " +
                            Integer.toString(OrderConstantsIfc.ORDER_STATUS_VOIDED) +
                            " )");
            purgeDate = getPurgeDate(criteria.getOrderAge());
        }
        else
        {
            // Get "completed" layaways;
            sql.addTable(TABLE_LAYAWAY);
            sql.addColumn(FIELD_LAYAWAY_ID);
            sql.addQualifier("( " + FIELD_LAYAWAY_STATUS + " = " +
                            Integer.toString(LayawayConstantsIfc.STATUS_COMPLETED) +
                            " or " +
                            FIELD_LAYAWAY_STATUS + " = " +
                            Integer.toString(LayawayConstantsIfc.STATUS_DELETED) +
                            " or " +
                            FIELD_LAYAWAY_STATUS + " = " +
                            Integer.toString(LayawayConstantsIfc.STATUS_SUSPENDED_CANCELED) +
                            " or " +
                            FIELD_LAYAWAY_STATUS + " = " +
                            Integer.toString(LayawayConstantsIfc.STATUS_VOIDED) +
                            " )");
            purgeDate = getPurgeDate(criteria.getLayawayAge());
        }
        sql.addQualifier(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP + " < " +
                             dateToSQLTimestampString(purgeDate));

        ArrayList rowIDList = new ArrayList();

        try
        {
            // execute sql and get result set
            connection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet) connection.getResult();
            // parse result set
            while (rs.next())
            {
                // Get data from the result set
                String rowID = getSafeString(rs, 1);
                rowIDList.add(rowID);
            }
            rs.close();
        }
        catch (DataException de)
        {
            logger.error(
                        de.toString());
            throw de;
        }
        catch (SQLException se)
        {
            logger.error(
                         Util.throwableToString(se));
            connection.logSQLException(se, "select timed pricing events.");
            throw new DataException(DataException.SQL_ERROR, "select timed pricing events", se);
        }
        catch (Exception e)
        {
            logger.error(
                         Util.throwableToString(e));
            throw new DataException(DataException.UNKNOWN, "timed pricing events", e);
        }

        // Delete all the rows in the tables associated with the rows ids in the list.
        int deleteCount      = 0;
        int deleteTransCount = 0;
        ArrayList tableNames = getOrderOrLayawayTableNames(purgeIndicator);
        for (int i = 0; i < rowIDList.size(); i++)
        {
            String rowID   = (String)rowIDList.get(i);
            boolean deleted = deleteOrderOrLayaway(connection, rowID, tableNames, purgeIndicator);
            if (deleted)
            {
                deleteCount++;
            }
            deleteOrderOrLayawayTransactions(connection, rowID, purgeIndicator, purgeResult);

        }

        if (purgeIndicator == PURGE_ORDERS)
        {
            purgeResult.setOrdersPurged(deleteCount);
        }
        else
        {
            purgeResult.setLayawaysPurged(deleteCount);
        }
    }

    /**
       Removes rows from each table associated with a layaway or order ID;
       <P>
       @param  dataConnection  connection to the db
       @param  rowID contains the value of the row id.
       @param  tableNames contains a list of String values of the table names.
       @return true if the transaction was deleted without error.
     */
    public boolean deleteOrderOrLayaway(JdbcDataConnection connection,
        String rowID, ArrayList tableNames, int purgeIndicator)
    {
        boolean deleted = false;
        SQLDeleteStatement sql = new SQLDeleteStatement();
        if (purgeIndicator == PURGE_ORDERS)
        {
            sql.addQualifier(FIELD_ORDER_ID, inQuotes(rowID));
        }
        else
        {
            sql.addQualifier(FIELD_LAYAWAY_ID, inQuotes(rowID));
        }

        for(int i = 0; i < tableNames.size(); i++)
        {
            String tableName = (String)tableNames.get(i);
            sql.setTable(tableName);
            try
            {
                connection.execute(sql.getSQLString());
                deleted = true;
            }
            catch (DataException de)
            {
                // It is very likely that rows will not exist in all tables;
                // many failures will occur.
                if (logger.isInfoEnabled()) logger.info(
                    "JdbcPurgeAgedData.deleteOrderOrLayaway() - row deletion failed.");
            }
        }

        return deleted;
    }

    /**
       Get a list of advanced pricing tables
       <P>
       @return an array list of strings
     */
    protected ArrayList getOrderOrLayawayTableNames(int purgeIndicator)
    {
        ArrayList tableNames = new ArrayList();

        if (purgeIndicator == PURGE_ORDERS)
        {
            tableNames.add(TABLE_ORDER_LINE_ITEM_RETAIL_PRICE_MODIFIER);
            tableNames.add(TABLE_ORDER_ITEM);
            tableNames.add(TABLE_ORDER);
        }
        else
        {
            tableNames.add(TABLE_LAYAWAY);
        }

        return tableNames;
    }

    /**
       Removes rows from the transaction table tables that are associated
       with the row ID.
       <P>
       @param  dataConnection  connection to the db
       @param  rowID contains the value of the row id.
       @return true if the transaction was deleted without error.
     */
    protected void deleteOrderOrLayawayTransactions(JdbcDataConnection connection,
        String rowID, int purgeIndicator, PurgeResultIfc purgeResult)
    {
        boolean deleted = false;
        SQLSelectStatement sql = new SQLSelectStatement();
        // set table, add columns and qualifiers
        sql.addTable(ARTSDatabaseIfc.TABLE_RETAIL_TRANSACTION);
        addSelectTransactionColumns(sql, false);
        if (purgeIndicator == PURGE_ORDERS)
        {
            sql.addQualifier(FIELD_ORDER_ID, inQuotes(rowID));
        }
        else
        {
            sql.addQualifier(FIELD_LAYAWAY_ID, inQuotes(rowID));
        }

        ArrayList entryList = new ArrayList();

        try
        {
            // execute sql and get result set
            connection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet) connection.getResult();
            // parse result set
            entryList = parseSelectTransactionResultSet(rs, false);
            rs.close();
        }
        catch (DataException de)
        {
            logger.error(
                        de.toString());
        }
        catch (SQLException se)
        {
            logger.error(
                         Util.throwableToString(se));
            connection.logSQLException(se, "select transaction IDs");
        }
        catch (Exception e)
        {
            logger.error(
                         Util.throwableToString(e));
        }

        int deleteCount = 0;
        for (int i = 0; i < entryList.size(); i++)
        {
            PurgeTransactionEntryIfc entry = (PurgeTransactionEntryIfc)entryList.get(i);
            if (purgeIndicator == PURGE_ORDERS)
            {
                entry.setTransactionType(TransactionIfc.TYPE_ORDER_COMPLETE);
            }
            else
            {
                entry.setTransactionType(TransactionIfc.TYPE_LAYAWAY_COMPLETE);
            }
            if (deleteTransaction(connection, entry))
            {
                deleteCount++;
            }
        }

        int currentCount = purgeResult.getTransactionsPurged();
        purgeResult.setTransactionsPurged(currentCount + deleteCount);

        if (logger.isDebugEnabled()) logger.debug( "JdbcPurgeAgedData.readTransactionsByID()");
    }

    /**
       Removes aged data from the financial history tables.  These records
       are associated with transactions and will be deleted at the same "age"
       as the transactions.

       <P>
       @param  dataConnection  connection to the db
       @param  criteria        contains the criteria on which records should
                               be deleted.
       @return the number of records deleted
       @exception  DataException upon error
     */
    public int purgeFinancialHistory(JdbcDataConnection connection,
                                  PurgeCriteriaIfc criteria) throws DataException
    {
        int updateCount = 0;
        EYSDate purgeDate = getPurgeDate(criteria.getTransactionAge());
        SQLDeleteStatement sql = new SQLDeleteStatement();
        sql.addQualifier(FIELD_RECORD_CREATION_TIMESTAMP + " < " +
                             dateToSQLTimestampString(purgeDate));

        ArrayList tableNames = getFinancialHistoryTableNames();

        for(int i = 0; i < tableNames.size(); i++)
        {
            String tableName = (String)tableNames.get(i);
            sql.setTable(tableName);
            try
            {
                connection.execute(sql.getSQLString());
                updateCount = updateCount + connection.getUpdateCount();
            }
            catch (DataException de)
            {
                // It is very likely that rows will not exist in all tables;
                // many failures will occur.
                if (logger.isInfoEnabled()) logger.info(
                    "JdbcPurgeAgedData.purgeFinancialHistory() - row deletion failed.");
            }
        }

        updateCount = updateCount + purgeDepartmentHistory(connection, criteria);
        return updateCount;
    }

    /**
       Get a list of financial history tables.
       <P>
       @return an array list of strings
     */
    protected ArrayList getFinancialHistoryTableNames()
    {
        ArrayList tableNames = new ArrayList();

        tableNames.add(TABLE_STORE_HISTORY);
        tableNames.add(TABLE_STORE_TENDER_HISTORY);
        tableNames.add(TABLE_TILL_HISTORY);
        tableNames.add(TABLE_TILL_TENDER_HISTORY);
        tableNames.add(TABLE_WORKSTATION_HISTORY);
        tableNames.add(TABLE_WORKSTATION_TENDER_HISTORY);
        tableNames.add(TABLE_STORE_SAFE_TENDER);
        tableNames.add(TABLE_STORE_SAFE_TENDER_HISTORY);
        tableNames.add(TABLE_WORKSTATION_TIME_ACTIVITY_HISTORY);
        tableNames.add(TABLE_TENDER_MEDIA_LINE_ITEM);

        return tableNames;
    }

    /**
       Removes aged data from the deparment history table.  Unfortunately
       this table does not contain a creation or modified date; as result
       this method uses the reporting period instead.

       <P>
       @param  dataConnection  connection to the db
       @param  criteria        contains the criteria on which records should
                               be deleted.
       @return the number of records deleted
       @exception  DataException upon error
     */
    public int purgeDepartmentHistory(JdbcDataConnection connection,
                                  PurgeCriteriaIfc criteria) throws DataException
    {
        int updateCount = 0;
        SQLDeleteStatement sql = new SQLDeleteStatement();
        sql.setTable(TABLE_POS_DEPARTMENT_HISTORY);
        EYSDate purgeDate = getPurgeDate(criteria.getTransactionAge());
        int pYear  = purgeDate.getYear();
        Calendar c = purgeDate.calendarValue();
        int pDay   = c.get(Calendar.DAY_OF_YEAR);

        // Qualify with the year, reporting type (DAY), day of the year
        sql.addQualifier(FIELD_FISCAL_YEAR, inQuotes(String.valueOf(pYear)));
        sql.addQualifier(FIELD_REPORTING_PERIOD_TYPE_CODE,
            inQuotes(ReportingPeriodIfc.REPORTING_PERIOD_CODES
                [ReportingPeriodIfc.TYPE_BUSINESS_DAY]));
        sql.addQualifier(FIELD_REPORTING_PERIOD_ID + " < " + String.valueOf(pDay));

        try
        {
            connection.execute(sql.getSQLString());
            updateCount = connection.getUpdateCount();
        }
        catch (DataException de)
        {
            // It is very likely that rows will not exist in all tables;
            // many failures will occur.
            if (logger.isInfoEnabled()) logger.info(
                "JdbcPurgeAgedData.purgeFinancialHistory() - row deletion failed.");
        }

        return updateCount;
    }

    /**
       Removes aged data from the advanced pricing rule tables.
       <P>
       @param  dataConnection  connection to the db
       @param  criteria        contains the criteria on which the data should be deleted.
       @return the number of transactions deleted
       @exception  DataException upon error
     */
    public int purgeAdvancedPricingRules(JdbcDataConnection connection,
                                  PurgeCriteriaIfc criteria) throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        // set table, add columns and qualifiers
        sql.addTable(TABLE_PRICE_DERIVATION_RULE);
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_ID);
        sql.addColumn(FIELD_RETAIL_STORE_ID);
        // Do not delete the rules until they have been expired for a day.
        EYSDate purgeDate = getPurgeDate(criteria.getAdvancedPricingAge());
        sql.addQualifier(FIELD_PRICE_DERIVATION_RULE_EXPIRATION_DATE + " < " +
                             dateToSQLTimestampString(purgeDate));

        // do not remove manual, customer-based rules with this task
        sql.addQualifier(FIELD_PRICE_DERIVATION_RULE_ASSIGNMENT_BASIS_CODE +
                         " not in (" + DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL + ", " +
                         DiscountRuleConstantsIfc.ASSIGNMENT_CUSTOMER + ")");

        ArrayList ruleIDList  = new ArrayList();
        ArrayList storeIDList = new ArrayList();

        try
        {
            // execute sql and get result set
            connection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet) connection.getResult();
            // parse result set
            while (rs.next())
            {
                String ruleID  = getSafeString(rs, 1);
                String storeID = getSafeString(rs, 2);
                ruleIDList.add(ruleID);
                storeIDList.add(storeID);
            }
            rs.close();
        }
        catch (DataException de)
        {
            logger.error(
                        de.toString());
            throw de;
        }
        catch (SQLException se)
        {
            logger.error(
                         Util.throwableToString(se));
            connection.logSQLException(se, "select advanced pricing rules.");
            throw new DataException(DataException.SQL_ERROR, "select advanced pricing rules", se);
        }
        catch (Exception e)
        {
            logger.error(
                         Util.throwableToString(e));
            throw new DataException(DataException.UNKNOWN, "select advanced pricing rules", e);
        }

        int deleteCount = 0;
        ArrayList tableNames = getAdvancedPricingTableNames();
        for (int i = 0; i < ruleIDList.size(); i++)
        {
            String ruleID   = (String)ruleIDList.get(i);
            String storeID  = (String)storeIDList.get(i);
            boolean deleted = deleteAdvancedPricingRule(connection, ruleID, storeID, tableNames);
            if (deleted)
            {
                deleteCount++;
            }
        }

        return deleteCount;
    }

    /**
       Removes rows from each table associated with a single pricing rule;
       <P>
       @param  dataConnection  connection to the db
       @param  ruleID contains the value of the rule id.
       @param  storeID contains the value of the store id.
       @param  tableNames contains a list of String values of the table names.
       @return true if the transaction was deleted without error.
     */
    public boolean deleteAdvancedPricingRule(JdbcDataConnection connection,
        String ruleID, String storeID, ArrayList tableNames)
    {
        boolean deleted = false;
        SQLDeleteStatement sql = new SQLDeleteStatement();
        sql.addQualifier(FIELD_PRICE_DERIVATION_RULE_ID, ruleID);
        sql.addQualifier(FIELD_RETAIL_STORE_ID, inQuotes(storeID));

        for(int i = 0; i < tableNames.size(); i++)
        {
            String tableName = (String)tableNames.get(i);
            sql.setTable(tableName);
            try
            {
                connection.execute(sql.getSQLString());
                deleted = true;
            }
            catch (DataException de)
            {
                // It is very likely that rows will not exist in all tables;
                // many failures will occur.
                if (logger.isInfoEnabled()) logger.info(
                    "JdbcPurgeAgedData.deleteAdvancedPricingRule() - row deletion failed.");
            }
        }

        return deleted;
    }

    /**
       Get a list of advanced pricing tables
       <P>
       @return an array list of strings
     */
    protected ArrayList getAdvancedPricingTableNames()
    {
        ArrayList tableNames = new ArrayList();

        tableNames.add(TABLE_DEPARTMENT_PRICE_DERIVATION_RULE_ELIGIBILITY);
        tableNames.add(TABLE_ITEM_PRICE_DERIVATION);
        tableNames.add(TABLE_ITEM_PRICE_DERIVATION_RULE_ELIGIBILITY);
        tableNames.add(TABLE_MERCHANDISE_STRUCTURE_PRICE_DERIVATION_RULE_ELIGIBILITY);
        tableNames.add(TABLE_MIX_AND_MATCH_PRICE_DERIVATION_ITEM);
        tableNames.add(TABLE_PRICE_DERIVATION_RULE);
        tableNames.add(TABLE_CUSTOMER_AFFILIATION_PRICE_DERIVATION_RULE_ELIGIBILITY);
        tableNames.add(TABLE_STORE_COUPON_PRICE_DERIVATION_RULE_ELIGIBILITY);
        tableNames.add(TABLE_TIME_DATE_PRICE_DERIVATION_RULE_ELIGIBILITY);

        return tableNames;
    }

    /**
       Removes aged data from the advanced pricing rule tables.
       <P>
       @param  dataConnection  connection to the db
       @param  criteria        contains the criteria on which the data should be deleted.
       @return the number of transactions deleted
       @exception  DataException upon error
     */
    public int purgeTimedItemPricing(JdbcDataConnection connection,
                                  PurgeCriteriaIfc criteria) throws DataException
    {
        // Get Permanent Price Changes
        SQLSelectStatement sql = new SQLSelectStatement();
        sql.addTable(TABLE_EVENT);
        sql.addColumn(FIELD_EVENT_EVENT_ID);
        sql.addColumn(FIELD_RETAIL_STORE_ID);
        EYSDate purgeDate = getPurgeDate(criteria.getPermanentPriceChangeAge());
        sql.addQualifier(FIELD_EVENT_PLAN_START_DATE + " < " +
                             dateToSQLTimestampString(purgeDate));
        sql.addQualifier(FIELD_EVENT_TYPE_CODE, inQuotes(PERM_TYPE_CODE));
        sql.addQualifier(FIELD_EVENT_STATUS_CODE, inQuotes(ACTIVE_STATUS_CODE));
        ArrayList eventIDList  = new ArrayList();
        ArrayList storeIDList  = new ArrayList();

        if (criteria.getPermanentPriceChangeAge() > -1)
        {
            getEventIDListElements(connection, sql, eventIDList, storeIDList);
        }

        // Get Temporary Price Changes
        sql = new SQLSelectStatement();
        sql.addTable(TABLE_EVENT);
        sql.addColumn(FIELD_EVENT_EVENT_ID);
        sql.addColumn(FIELD_RETAIL_STORE_ID);
        purgeDate = getPurgeDate(criteria.getTemporaryPriceChangeAge());
        sql.addQualifier(FIELD_EVENT_PLAN_END_DATE + " < " +
                             dateToSQLTimestampString(purgeDate));
        sql.addQualifier(FIELD_EVENT_TYPE_CODE, inQuotes(TEMP_TYPE_CODE));
        sql.addQualifier(FIELD_EVENT_STATUS_CODE, inQuotes(EXPIRED_STATUS_CODE));
        if (criteria.getTemporaryPriceChangeAge() > -1)
        {
            getEventIDListElements(connection, sql, eventIDList, storeIDList);
        }

        int deleteCount = 0;
        ArrayList tableNames = getTimedItemEventTableNames();
        for (int i = 0; i < eventIDList.size(); i++)
        {
            int    eventID   = Integer.parseInt((String)eventIDList.get(i));
            String storeID   = (String)storeIDList.get(i);
            boolean deleted = deleteTimedItemEvent(connection, eventID, storeID, tableNames);
            if (deleted)
            {
                deleteCount++;
            }
        }

        return deleteCount;
    }

    /**
       Removes aged data from the advanced pricing rule tables.
       <P>
       @param  dataConnection  connection to the db
       @param  criteria        contains the criteria on which the data should be deleted.
       @return the number of transactions deleted
       @exception  DataException upon error
     */
    protected void getEventIDListElements(JdbcDataConnection connection,
        SQLSelectStatement sql, ArrayList eventIDList, ArrayList storeIDList)
            throws DataException
    {
        try
        {
            // execute sql and get result set
            connection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet) connection.getResult();
            // parse result set
            while (rs.next())
            {
                String eventID  = getSafeString(rs, 1);
                String storeID  = getSafeString(rs, 2);
                eventIDList.add(eventID);
                storeIDList.add(storeID);
            }
            rs.close();
        }
        catch (DataException de)
        {
            logger.error(
                        de.toString());
            throw de;
        }
        catch (SQLException se)
        {
            logger.error(Util.throwableToString(se));
            connection.logSQLException(se, "select timed pricing events.");
            throw new DataException(DataException.SQL_ERROR, "select timed pricing events", se);
        }
        catch (Exception e)
        {
            logger.error(
                         Util.throwableToString(e));
            throw new DataException(DataException.UNKNOWN, "timed pricing events", e);
        }
    }

    /**
       Removes rows from each table associated with a single pricing rule;
       <P>
       @param  dataConnection  connection to the db
       @param  ruleID contains the value of the rule id.
       @param  storeID contains the value of the store id.
       @param  tableNames contains a list of String values of the table names.
       @return true if the transaction was deleted without error.
     */
    public boolean deleteTimedItemEvent(JdbcDataConnection connection,
        int eventID, String storeID, ArrayList tableNames)
    {
        boolean deleted = false;
        SQLDeleteStatement sql = new SQLDeleteStatement();
        sql.addQualifier(FIELD_EVENT_EVENT_ID, inQuotes(eventID));
        sql.addQualifier(FIELD_RETAIL_STORE_ID, inQuotes(storeID));

        for(int i = 0; i < tableNames.size(); i++)
        {
            String tableName = (String)tableNames.get(i);
            sql.setTable(tableName);
            try
            {
                connection.execute(sql.getSQLString());
                deleted = true;
            }
            catch (DataException de)
            {
                // It is very likely that rows will not exist in all tables;
                // many failures will occur.
                if (logger.isInfoEnabled()) logger.info(
                    "JdbcPurgeAgedData.deleteAdvancedPricingRule() - row deletion failed.");
            }
        }

        return deleted;
    }

    /**
       Get a list of advanced pricing tables
       <P>
       @return an array list of strings
     */
    protected ArrayList getTimedItemEventTableNames()
    {
        ArrayList tableNames = new ArrayList();

        tableNames.add(TABLE_EVENT);
        tableNames.add(TABLE_PERMANENT_PRICE_CHANGE);
        tableNames.add(TABLE_PERMANENT_PRICE_CHANGE_ITEM);
        tableNames.add(TABLE_TEMPORARY_PRICE_CHANGE);
        tableNames.add(TABLE_TEMPORARY_PRICE_CHANGE_ITEM);

        return tableNames;
    }

    /**
       Based on the number of days old a record must be, this method
       calculates the date a record must contain in order to be deleted.
       <P>
       @param   days     the number of days old a record must be in order to be deleted.
       @return  the purge date
     */
    protected EYSDate getPurgeDate(int days)
    {
        EYSDate purgeDate = new EYSDate();
        purgeDate.setHour(0);
        purgeDate.setMinute(0);
        purgeDate.setSecond(0);
        purgeDate.setMillisecond(0);
        int decrement = -1 * days;
        purgeDate.add(Calendar.DATE, decrement);

        return purgeDate;
    }

    /**
        Method to default display string function. <P>
        @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        StringBuilder strResult = Util.classToStringHeader
          ("JdbcPurgeAgedData",
           getRevisionNumber(),
           hashCode());
        return(strResult.toString());
    }

    /**
       Returns the source-code-control system revision number. <P>
       @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }
}

