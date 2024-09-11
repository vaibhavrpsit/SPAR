/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcRetrieveOrderSummary.java /main/29 2014/06/17 15:26:37 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  06/16/14 - CAE Order summary enhancement phase I
 *    abondala  06/03/13 - fixed the query that retrieve orders by customer
 *                         information
 *    yiqzhao   01/20/13 - Replace EYSDate by java.util.Date in order to use in
 *                         CO.
 *    yiqzhao   01/16/13 - Using _360Date to replace domain object EYSDate in
 *                         OrderSearchCriteria. This is also used in
 *                         storeservice in CO.
 *    yiqzhao   12/24/12 - Refactoring xc formatter, transformer and others.
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    sgu       07/24/12 - fix defect in order summary retrieval
 *    sgu       07/18/12 - rename itemID to itemNumber
 *    sgu       07/18/12 - set xc order status to order summary
 *    sgu       07/17/12 - enhance order summary search by item and customer
 *                         info
 *    sgu       07/17/12 - add item serach criteria
 *    sgu       07/16/12 - add order search by customer info
 *    sgu       07/13/12 - remove order retrieval by its status
 *    sgu       05/07/12 - add xchannel support in order summary
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    mahising  04/02/09 - Fixed order type issue at service alert for PDO
 *    mahising  03/26/09 - Fixed sale total transaction issue for PDO at
 *                         service alert
 *    mahising  03/16/09 - Fixed total mismatch issue with sale total for PDO
 *                         transaction
 *    mahising  03/05/09 - fixed issue to see partial pickup/delivery and
 *                         special order in service alert
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/14/08 - check in for Pickup Delivery Order functionality
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         4/7/2008 8:55:22 AM    Christian Greene
 *         Performance tweaks and upgrade to Collections and add generics
 *         notations
 *    7    360Commerce 1.6         4/25/2007 10:01:11 AM  Anda D. Cadar   I18N
 *         merge
 *    6    360Commerce 1.5         6/14/2006 1:43:19 PM   Brendan W. Farrell
 *         UDM
 *    5    360Commerce 1.4         2/8/2006 11:11:41 AM   Deepanshu       CR
 *         3840: Orders are retrieved on the basis of Business day date on
 *         which they occur and ot on the basis of status change date
 *    4    360Commerce 1.3         1/25/2006 4:11:20 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:43 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:47 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:01 PM  Robert Pearse
 *:
 *    5    .v700     1.2.1.1     11/16/2005 16:26:30    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    4    .v700     1.2.1.0     11/16/2005 06:27:19    Akhilashwar K. Gupta
 *         Modifed as per the reviewer comments in CR #5273
 *    3    360Commerce1.2         3/31/2005 15:28:43     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:47     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:01     Robert Pearse
 *
 *   Revision 1.7  2004/09/17 13:09:56  kll
 *   @scr 7150: 7151, construction of an exclusive time range
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:47  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:24  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:36   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   May 09 2003 15:36:44   bwf
 * Added code to put status qualifier into sql for use with alert lists.
 * Resolution for 2361: Completed special order shows on service alert list
 *
 *    Rev 1.2   Apr 07 2003 10:30:42   bwf
 * Database Internationalization and deprecation fixes
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.1   Feb 15 2003 17:25:54   mpm
 * Merged 5.1 changes.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.0   Jun 03 2002 16:39:12   msg
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.order.OrderSearchCriteriaIfc;
import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * This operation reads all of the matching a specified order ID.
 * This class has been deprecated in the wake of an enhancement from JDBC 
 * operation to JPA operation. In lieu of this use {@link #JpaRetrieveOrderSummary}
 * @version $Revision: /main/29 $
 * @deprecated as of 14.1
 */
public class JdbcRetrieveOrderSummary extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 6629367518577855186L;
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(JdbcRetrieveOrderSummary.class);

    /**
     * Class constructor.
     */
    public JdbcRetrieveOrderSummary()
    {
        super();
    }

    /**
     * Executes the SQL statements against the database.
     *
     * @param dataTransaction
     * @param dataConnection
     * @param action
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcRetrieveOrderSummary.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // grab arguments and call JdbcRetrieveOrderSummary()
        OrderSearchCriteriaIfc orderSearchCriteria =
          (OrderSearchCriteriaIfc) action.getDataObject();
        OrderSummaryEntryIfc[] orderSumList =
          retrieveOrderSummaries(connection, orderSearchCriteria);

        dataTransaction.setResult(orderSumList);

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcRetrieveOrderSummary.execute");
    }

    /**
     * Retrieves array of order summaries. This uses the following method which
     * builds a vector and then converts the result to an array.
     *
     * @param connection data connection
     * @param orderSearchCriteria order search key
     * @exception throws DataException if error occurs
     */
    protected OrderSummaryEntryIfc[] retrieveOrderSummaries(JdbcDataConnection connection, OrderSearchCriteriaIfc orderSearchCriteria)
            throws DataException
    {
        Vector orderSumVector = retrieveOrderSummaryVector(connection, orderSearchCriteria);
        OrderSummaryEntryIfc[] orderSumList = new OrderSummaryEntryIfc[orderSumVector.size()];
        orderSumVector.copyInto(orderSumList);
        return (orderSumList);
    }

    /**
     * Retrieves array of order summaries. This uses the following method which
     * builds a vector and then converts the result to an array.
     *
     * @param connection data connection
     * @param orderSearchCriteria order search key
     * @param locale Locale
     * @exception throws DataException if error occurs
     * @deprecated As of release 13.1 Use @link
     *             #retrieveOrderSummaries(JdbcDataConnection, orderSearchCriteria)
     */
    protected OrderSummaryEntryIfc[] retrieveOrderSummaries(JdbcDataConnection connection,
            OrderSearchCriteriaIfc orderSearchCriteria, Locale locale) throws DataException
    {
        return retrieveOrderSummaries(connection, orderSearchCriteria);
    }

    /**
     * Executes the SQL statements against the database.
     *
     * @param dataConnection
     * @param orderSearchCriteria object contains customer, store id, begindate, and
     *            enddate
     * @exception DataException upon error
     */
    protected Vector retrieveOrderSummaryVector(JdbcDataConnection connection, OrderSearchCriteriaIfc orderSearchCriteria)
            throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        String statusQualifier = null; // OR statement qualifier for SELECT

        // add tables
        sql.addTable(TABLE_ORDER, ALIAS_ORDER);

        // set columns
        setSelectColumns(sql);

        // set qualifiers
        setSelectQualifiers(sql, orderSearchCriteria);

        // set ordering
        setSelectOrdering(sql, orderSearchCriteria);

        // execute and parse
        Vector orderSumVector = executeAndParse(connection, sql);

        return (orderSumVector);
    }

    /**
     * Executes the SQL statements against the database.
     *
     * @param dataConnection
     * @param orderSearchCriteria object contains customer, store id, begindate, and
     *            enddate
     * @param locale Locale
     * @exception DataException upon error
     * @deprecated As of release 13.1 Use @link
     *             #retrieveOrderSummaryVector(JdbcDataConnection,
     *             orderSearchCriteria)
     */
    protected Vector retrieveOrderSummaryVector(JdbcDataConnection connection, OrderSearchCriteriaIfc orderSearchCriteria,
            Locale locale) throws DataException
    {
        return retrieveOrderSummaryVector(connection, orderSearchCriteria);
    }

    /**
     * Sets select columns in SQLSelectStatement.
     *
     * @param sql SQLSelectStatement object
     */
    protected void setSelectColumns(SQLSelectStatement sql)
    {
        // add columns
        sql.addColumn(ALIAS_ORDER, FIELD_ORDER_ID);
        sql.addColumn(ALIAS_ORDER, FIELD_ORDER_DESCRIPTION);
        sql.addColumn(ALIAS_ORDER, FIELD_ORDER_STATUS);
        sql.addColumn(ALIAS_ORDER, FIELD_CONTACT_FIRST_NAME);
        sql.addColumn(ALIAS_ORDER, FIELD_CONTACT_MIDDLE_INITIAL);
        sql.addColumn(ALIAS_ORDER, FIELD_CONTACT_LAST_NAME);
        sql.addColumn(ALIAS_ORDER, FIELD_CONTACT_BUSINESS_NAME);
        sql.addColumn(ALIAS_ORDER, FIELD_ORDER_CREATE_TIMESTAMP);
        sql.addColumn(ALIAS_ORDER, FIELD_ORDER_TOTAL);
        sql.addColumn(ALIAS_ORDER, FIELD_ORDER_INITIATION_CHANNEL);
        sql.addColumn(ALIAS_ORDER, FIELD_ORDER_RECORDING_STORE_ID);
        sql.addColumn(ALIAS_ORDER, FIELD_ORDER_RECORDING_WORKSTATION_ID);
        sql.addColumn(ALIAS_ORDER, FIELD_ORDER_RECORDING_TRANSACTION_BUSINESS_DATE);
        sql.addColumn(ALIAS_ORDER, FIELD_ORDER_RECORDING_TRANSACTION_SEQUENCE_NUMBER);
        sql.addColumn(ALIAS_ORDER, FIELD_ORDER_MODIFIED_TIMESTAMP);
        sql.addColumn(ALIAS_ORDER, FIELD_RETAIL_STORE_ID);
        sql.addColumn(ALIAS_ORDER, FIELD_ORDER_TYPE);

        // Retrieve order status and total from order status table for an xc order.
        // This is a snapshot of the xc order in ORPOS database.
        sql.addColumn(ALIAS_ORDER_STATUS, FIELD_ORDER_STATUS);
        sql.addColumn(ALIAS_ORDER_STATUS, FIELD_ORDER_TOTAL);

        // Add the order by column
        sql.addColumn(ALIAS_ORDER, FIELD_ORDER_STATUS_CHANGE);

        // Join with order status table.
        sql.addOuterJoinQualifier(" LEFT OUTER JOIN "+TABLE_ORDER_STATUS+" "+ALIAS_ORDER_STATUS
                +" ON " +ALIAS_ORDER_STATUS+"."+FIELD_RETAIL_STORE_ID + " = " + ALIAS_ORDER+"."+FIELD_ORDER_ORIGINAL_STORE_ID
                +" AND "+ALIAS_ORDER_STATUS+"."+FIELD_WORKSTATION_ID + " = " + ALIAS_ORDER+"."+FIELD_ORDER_ORIGINAL_WORKSTATION_ID
                +" AND "+ALIAS_ORDER_STATUS+"."+FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + ALIAS_ORDER+"."+FIELD_ORDER_ORIGINAL_TRANSACTION_SEQUENCE_NUMBER
                +" AND "+ALIAS_ORDER_STATUS+"."+FIELD_BUSINESS_DAY_DATE + " = " + ALIAS_ORDER+"."+FIELD_ORDER_ORIGINAL_TRANSACTION_BUSINESS_DATE
                +" AND "+ALIAS_ORDER_STATUS+"."+FIELD_XC_ORDER_FLAG + " = '" + "1" + "'");
    }			

    /**
     * Sets qualifiers to select SQL statement based on search key.
     *
     * @param sql SQLSelectStatement object
     * @param orderSearchCriteria orderSearchCriteria object
     */
    protected void setSelectQualifiers(SQLSelectStatement sql, OrderSearchCriteriaIfc orderSearchCriteria)
    {
        // add store ID
        String storeID = orderSearchCriteria.getStoreID();
        if (storeID != null)
        {
            sql.addQualifier(ALIAS_ORDER + "." + FIELD_RETAIL_STORE_ID + " = " + JdbcDataOperation.makeSafeString(storeID));
        }
        // add qualifiers for the transaction training flag
        String trainingMode = "0";
        if (orderSearchCriteria.isTrainingMode())
        {
            trainingMode = "1";
        }
        sql.addQualifier(ALIAS_ORDER + "." + FIELD_TRANSACTION_TRAINING_FLAG + " = " + JdbcDataOperation.makeSafeString(trainingMode));

        // get statuses and use them in search, if they have been sent
        if (orderSearchCriteria.getOrderStatuses() != null)
        {
            int[] statusList = orderSearchCriteria.getOrderStatuses(); // required
            if (statusList.length > 1)
            {
                String statusQualifier = null;
                statusQualifier = "(" + ALIAS_ORDER + "." + FIELD_ORDER_STATUS + " = " + statusList[0];
                for (int i = 1; i < statusList.length; i++) // build rest of
                // qualifier
                {
                    statusQualifier += " OR ";
                    statusQualifier += ALIAS_ORDER + "." + FIELD_ORDER_STATUS + " = " + statusList[i];
                }
                statusQualifier += ")\n";

                sql.addQualifier(statusQualifier);
            }
            else
            {
                sql.addQualifier(ALIAS_ORDER + "." + FIELD_ORDER_STATUS + " = " + statusList[0]);
            }
        }

        addDateQualifiers(sql, orderSearchCriteria, FIELD_BUSINESS_DAY_DATE);
        addItemQualifiers(sql, orderSearchCriteria);

    }

    /**
     * Adds date qualifiers to select SQL statement based on search key and
     * specified column.
     *
     * @param sql SQLSelectStatement object
     * @param orderSearchCriteriaIfc orderSearchCriteria object
     * @param columnName column name to check against date
     */
    protected void addDateQualifiers(SQLSelectStatement sql, OrderSearchCriteriaIfc orderSearchCriteria, String columnName)
    {
       
        if (orderSearchCriteria.getStartDate() != null)
        {
            EYSDate beginDate = new EYSDate(orderSearchCriteria.getStartDate());
            sql.addQualifier(ALIAS_ORDER + "." + columnName + " >= '" + beginDate.toFormattedString("yyyy-MM-dd") + "'");
        }
        
        if (orderSearchCriteria.getEndDate() != null)
        {
            EYSDate endDate = new EYSDate(orderSearchCriteria.getEndDate());
            sql.addQualifier(ALIAS_ORDER + "." + columnName + " <= '" + endDate.toFormattedString("yyyy-MM-dd") + "'");
        }
    }

    /**
     * Adds item qualifiers to select SQL statement based on search key
     * @param sql SQLSelectStatement object
     * @param orderSearchCriteriaIfc orderSearchCriteria object
     */
    protected void addItemQualifiers(SQLSelectStatement sql, OrderSearchCriteriaIfc orderSearchCriteria)
    {
        String itemNumber = orderSearchCriteria.getItemNumber();
        if (!StringUtils.isBlank(itemNumber))
        {
            sql.setDistinctFlag(true);

            sql.addOuterJoinQualifier(" LEFT OUTER JOIN "+TABLE_SALE_RETURN_LINE_ITEM+" "+ALIAS_SALE_RETURN_LINE_ITEM
                    +" ON " +ALIAS_SALE_RETURN_LINE_ITEM+"."+FIELD_RETAIL_STORE_ID + " = " + ALIAS_ORDER+"."+FIELD_ORDER_ORIGINAL_STORE_ID
                    +" AND "+ALIAS_SALE_RETURN_LINE_ITEM+"."+FIELD_WORKSTATION_ID + " = " + ALIAS_ORDER+"."+FIELD_ORDER_ORIGINAL_WORKSTATION_ID
                    +" AND "+ALIAS_SALE_RETURN_LINE_ITEM+"."+FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + ALIAS_ORDER+"."+FIELD_ORDER_ORIGINAL_TRANSACTION_SEQUENCE_NUMBER
                    +" AND "+ALIAS_SALE_RETURN_LINE_ITEM+"."+FIELD_BUSINESS_DAY_DATE + " = " + ALIAS_ORDER+"."+FIELD_ORDER_ORIGINAL_TRANSACTION_BUSINESS_DATE);
            sql.addQualifier("(" + ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_ID + " = " + makeSafeString(itemNumber) +
                    " OR " + ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_POS_ITEM_ID + " = " + makeSafeString(itemNumber) + ")");
        }
    }

    /**
     * Sets ordering on select SQL statement based on search key. NOTE: order
     * columns must be among select columns.
     *
     * @param sql SQLSelectStatement object
     * @param orderSearchCriteriaIfc orderSearchCriteria object
     */
    protected void setSelectOrdering(SQLSelectStatement sql, OrderSearchCriteriaIfc orderSearchCriteria)
    {
        // Ordering
        sql.addOrdering(ALIAS_ORDER, FIELD_ORDER_STATUS_CHANGE + " ASC");
    }

    /**
     * Executes SQL and parses result set, returning vector of order summaries.
     *
     * @param connection data connection
     * @param sql SQLSelectStatement
     * @exception DataException thrown if error occurs.
     */
    protected Vector executeAndParse(JdbcDataConnection connection, SQLSelectStatement sql) throws DataException
    {
        // Instantiate Order
        int rsStatus = 0;
        Vector orderSumVector = new Vector();
        try
        {
            // execute sql and get result set
            connection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)connection.getResult();

            int index;

            // loop through result set
            while (rs.next())
            {
                // Instantiate OrderSummaryEntry
                OrderSummaryEntryIfc orderSum = DomainGateway.getFactory().getOrderSummaryEntryInstance();
                TransactionIDIfc recordingTransactionID = DomainGateway.getFactory().getTransactionIDInstance();
                StoreIfc store = DomainGateway.getFactory().getStoreInstance();

                ++rsStatus;
                index = 0;

                // parse the data from the database
                String orderIDString = getSafeString(rs, ++index);
                String desc = getSafeString(rs, ++index);
                int storeOrderStatus = rs.getInt(++index);
                String customerFirstName = getSafeString(rs, ++index);
                String customerMiddleName = getSafeString(rs, ++index);
                String customerLastName = getSafeString(rs, ++index);
                String customerCompanyName = getSafeString(rs, ++index);         
                EYSDate beginDay = timestampToEYSDate(rs, ++index);
                CurrencyIfc storeOrderTotal = getCurrencyFromDecimal(rs, ++index);

                // set values in order summary object
                orderSum.setInitiatingChannel(rs.getInt(++index));
                recordingTransactionID.setStoreID(getSafeString(rs, ++index));
                recordingTransactionID.setWorkstationID(getSafeString(rs, ++index));
                orderSum.setRecordingTransactionBusinessDate(getEYSDateFromString(rs, ++index));
                recordingTransactionID.setSequenceNumber(rs.getLong(++index));
                orderSum.setTimestampModified(getEYSDateFromString(rs, ++index));
                orderSum.setRecordingTransactionID(recordingTransactionID);
                store.setStoreID(getSafeString(rs, ++index));
                orderSum.setOrderType(rs.getInt(++index));
                orderSum.setShipToStore(store);

                String xcOrderStatus = getSafeString(rs, ++index);
                CurrencyIfc xcOrderTotal = getCurrencyFromDecimal(rs, ++index);

                orderSum.setOrderID(orderIDString);
                orderSum.setOrderDescription(desc);
                orderSum.setStoreOrderStatus(storeOrderStatus);
                // If an order does not contain any xc item, its xcOrderStatus is blank;
                if (!StringUtils.isBlank(xcOrderStatus))
                {
                    orderSum.setXChannelStatus(Integer.parseInt(xcOrderStatus));
                }
                orderSum.setCustomerFirstName(customerFirstName);
                orderSum.setCustomerLastName(customerLastName);
                orderSum.setCustomerMiddleInitial(customerMiddleName);
                orderSum.setCustomerCompanyName(customerCompanyName);
                orderSum.setTimestampCreated(beginDay);
                orderSum.setStoreOrderTotal(storeOrderTotal);
                orderSum.setXChannelTotal(xcOrderTotal);
                orderSumVector.addElement(orderSum);
            }
            // end loop through result set
            // close result set
            rs.close();

        }
        catch (DataException de)
        {
            logger.warn("" + de + "");
            if (de.getErrorCode() == DataException.UNKNOWN)
            {
                throw new DataException(DataException.CONNECTION_ERROR, "Connection lost");
            }
            else
            {
                throw de;
            }
        }
        catch (SQLException se)
        {
            connection.logSQLException(se, "order table");
            throw new DataException(DataException.SQL_ERROR, "order table", se);
        }
        catch (Exception e)
        {
            logger.error(Util.throwableToString(e));
            throw new DataException(DataException.UNKNOWN, "order table", e);
        }

        if (rsStatus == 0)
        {
            logger.warn("No orders found");
            throw new DataException(DataException.NO_DATA, "No orders found");
        }

        return (orderSumVector);
    }

    /**
     * Executes SQL and parses result set, returning vector of order summaries.
     *
     * @param connection data connection
     * @param sql SQLSelectStatement
     * @param locale Locale
     * @exception DataException thrown if error occurs.
     * @deprecated As of release 13.1 Use @link {
     *             JdbcRetrieveOrderSummary#executeAndParse(JdbcDataConnection,
     *             SQLSelectStatement) }
     */
    protected Vector executeAndParse(JdbcDataConnection connection, SQLSelectStatement sql,
            OrderSearchKey orderSearchKey, Locale locale) throws DataException
    {
        return executeAndParse(connection, sql);
    }

}
