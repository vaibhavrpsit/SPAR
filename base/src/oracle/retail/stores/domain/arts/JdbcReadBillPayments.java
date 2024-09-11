/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
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
 *    mchellap  08/24/10 - Billpay datamodel changes
 *    nkgautam  08/05/10 - remove post voided bill pay transactions from
 *                         reports
 *    nkgautam  07/06/10 - bill pay report changes
 *    nkgautam  06/30/10 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.Bill;
import oracle.retail.stores.domain.financial.BillIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

public class JdbcReadBillPayments extends JdbcDataOperation implements ARTSDatabaseIfc
{

    private static final long serialVersionUID = -3979429483156936198L;

    private static final long transactionStatusCode = 2;


    /**
     * Executes the SQL statements against the database.
     *
     * @param dataTransaction
     * @param dataConnection
     * @param action
     */
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug("JdbcReadBillPayments.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
        SearchCriteriaIfc searchKey = (SearchCriteriaIfc)action.getDataObject();
        TransactionSummaryIfc transSummary = retrieveBillPayments(connection,searchKey);

        dataTransaction.setResult(transSummary);
    }

    /**
     * Retrieves array of Transaction summaries. This uses the following method which
     * builds a vector and then converts the result to an array.
     *
     * @param connection data connection
     * @param orderSearchKey order search key
     * @param locale Locale
     * @exception throws DataException if error occurs
     */
    protected TransactionSummaryIfc retrieveBillPayments(JdbcDataConnection connection,SearchCriteriaIfc searchKey) throws DataException
    {
        Vector<TransactionSummaryIfc> billPaymentsVector = retrieveBillPaymentsVector(connection, searchKey);
        TransactionSummaryIfc billPaymentsList = billPaymentsVector.get(0);
        return (billPaymentsList);
    }

    /**
     * Executes the SQL statements against the database.
     *
     * @param dataConnection
     * @param SearchCriteriaIfc object
     * @exception DataException upon error
     */
    protected Vector<TransactionSummaryIfc> retrieveBillPaymentsVector(JdbcDataConnection connection,SearchCriteriaIfc searchKey) throws DataException
    {
        Vector<TransactionSummaryIfc> billPaymentsVector = null;
        SQLSelectStatement sql = new SQLSelectStatement();

        sql.addTable(TABLE_BILL_PAY, ALIAS_BILL_PAY);
        sql.addTable(TABLE_BILL_PAY_LINE_ITEM, ALIAS_BILL_PAY_TRANSACTION_LINE_ITEM);
        sql.addTable(TABLE_TRANSACTION, ALIAS_TRANSACTION);

        sql.addColumn(ALIAS_BILL_PAY + "." + FIELD_RETAIL_STORE_ID);
        sql.addColumn(ALIAS_BILL_PAY + "." + FIELD_WORKSTATION_ID);
        sql.addColumn(ALIAS_BILL_PAY + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addColumn(ALIAS_BILL_PAY + "." + FIELD_BILLPAY_CUSTOMER_NAME);
        sql.addColumn(ALIAS_BILL_PAY_TRANSACTION_LINE_ITEM + "." + FIELD_BILLPAY_CHILD_CUSTOMER_NAME);
        sql.addColumn(ALIAS_BILL_PAY + "." + FIELD_BILLPAY_ACCOUNT_NUMBER);
        sql.addColumn(ALIAS_BILL_PAY_TRANSACTION_LINE_ITEM + "." + FIELD_BILLPAY_CHILD_ACCOUNT_NUMBER);
        sql.addColumn(ALIAS_BILL_PAY + "." + FIELD_BUSINESS_DAY_DATE);
        sql.addColumn(ALIAS_BILL_PAY_TRANSACTION_LINE_ITEM + "." + FIELD_BILLPAY_BILL_NUMBER);
        sql.addColumn(ALIAS_BILL_PAY_TRANSACTION_LINE_ITEM + "." + FIELD_BILLPAY_PAYMENT_COLLECTED);

        sql.addJoinQualifier(ALIAS_BILL_PAY, FIELD_BILLPAY_ACCOUNT_NUMBER, ALIAS_BILL_PAY_TRANSACTION_LINE_ITEM,
                FIELD_BILLPAY_ACCOUNT_NUMBER);
        sql.addJoinQualifier(ALIAS_BILL_PAY, FIELD_RETAIL_STORE_ID, ALIAS_BILL_PAY_TRANSACTION_LINE_ITEM,
                FIELD_RETAIL_STORE_ID);
        sql.addJoinQualifier(ALIAS_BILL_PAY, FIELD_WORKSTATION_ID, ALIAS_BILL_PAY_TRANSACTION_LINE_ITEM,
                FIELD_WORKSTATION_ID);
        sql.addJoinQualifier(ALIAS_BILL_PAY, FIELD_TRANSACTION_SEQUENCE_NUMBER,
                ALIAS_BILL_PAY_TRANSACTION_LINE_ITEM, FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addJoinQualifier(ALIAS_BILL_PAY, FIELD_RETAIL_STORE_ID,
                ALIAS_TRANSACTION, FIELD_RETAIL_STORE_ID);
        sql.addJoinQualifier(ALIAS_BILL_PAY, FIELD_WORKSTATION_ID,
                ALIAS_TRANSACTION, FIELD_WORKSTATION_ID);
        sql.addJoinQualifier(ALIAS_BILL_PAY, FIELD_TRANSACTION_SEQUENCE_NUMBER,
                ALIAS_TRANSACTION, FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_STATUS_CODE, transactionStatusCode);
        sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_TYPE_CODE, TransactionConstantsIfc.TYPE_BILL_PAY);
        addDateQualifiers(sql,searchKey,FIELD_BUSINESS_DAY_DATE);
        setSelectOrdering(sql);

        billPaymentsVector = executeAndParse(connection, sql);

        return billPaymentsVector;

    }

    /**
     * Executes SQL and parses result set, returning vector of order summaries.
     *
     * @param connection data connection
     * @param sql SQLSelectStatement
     * @exception DataException thrown if error occurs.
     */
    protected Vector<TransactionSummaryIfc> executeAndParse(JdbcDataConnection connection, SQLSelectStatement sql) throws DataException
    {
        // Instantiate Order
        int rsStatus = 0;
        Vector<TransactionSummaryIfc> billPaymentsVector = new Vector<TransactionSummaryIfc>(2);

        ArrayList<BillIfc> billPaymentList = new ArrayList<BillIfc>();
        TransactionSummaryIfc transSummary = DomainGateway.getFactory().getTransactionSummaryInstance();

        try
        {
            // execute sql and get result set
            connection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)connection.getResult();

            int index;

            // loop through result set
            while (rs.next())
            {
                TransactionIDIfc transactionID = DomainGateway.getFactory().getTransactionIDInstance();
                BillIfc billDetails = new Bill();
                ++rsStatus;
                index = 0;

                //Transaction Sequence
                String storeID = getSafeString(rs, ++index);
                String workStationID = getSafeString(rs, ++index);
                String transactionSeq = getSafeString(rs, ++index);
                transactionID.setTransactionID(storeID, workStationID, Long.valueOf(transactionSeq));
                billDetails.setTransactionID(transactionID.getTransactionIDString());

                //Customer Name
                String customerName = getSafeString(rs, ++index);
                String childCustomerName = getSafeString(rs, ++index);

                //Account Number
                String accountNumber = getSafeString(rs, ++index);
                String childAccountNumber = getSafeString(rs, ++index);

                if(Util.isEmpty(childAccountNumber))
                {
                    billDetails.setAccountNumber(accountNumber);
                    billDetails.setCustomerName(customerName);
                }
                else
                {
                    billDetails.setAccountNumber(childAccountNumber);
                    billDetails.setCustomerName(childCustomerName);
                }

                //Payment Date
                EYSDate paymentDate = getEYSDateFromString(rs, ++index);
                billDetails.setBillDate(paymentDate);

                //Bill Number
                String billNumber = getSafeString(rs, ++index);
                billDetails.setBillNumber(billNumber);

                //Paid Amount
                String paymentAmount = getSafeString(rs, ++index);
                CurrencyIfc paidAmount = DomainGateway.getBaseCurrencyInstance(paymentAmount);
                billDetails.setBillAmountPaid(paidAmount);

                billPaymentList.add(billDetails);
            }
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

        transSummary.setBillPaymentList(billPaymentList);
        billPaymentsVector.add(transSummary);

        return billPaymentsVector;
    }

    /**
     * Adds date qualifiers to select SQL statement based on search key and
     * specified column.
     *
     * @param sql SQLSelectStatement object
     * @param orderSearchKey OrderSearchKey object
     * @param columnName column name to check against date
     */
    protected void addDateQualifiers(SQLSelectStatement sql, SearchCriteriaIfc SearchKey, String columnName)
    {
        EYSDate[] dateRange = SearchKey.getDateRange();
        EYSDate beginDate = dateRange[0];
        EYSDate endDate   = dateRange[1];
        if (beginDate != null)
        {
            sql.addQualifier(ALIAS_BILL_PAY + "." + columnName + " >= '" + beginDate.toFormattedString("yyyy-MM-dd") + "'");
        }
        if (endDate != null)
        {
            sql.addQualifier(ALIAS_BILL_PAY + "." + columnName + " <= '" + endDate.toFormattedString("yyyy-MM-dd") + "'");
        }
    }

    /**
     * Sets ordering on select SQL statement based on search key. NOTE: order
     * columns must be among select columns.
     *
     * @param sql SQLSelectStatement object
     * @param orderSearchKey OrderSearchKey object
     */
    protected void setSelectOrdering(SQLSelectStatement sql)
    {
        // Ordering
        sql.addOrdering(ALIAS_BILL_PAY + "." + FIELD_BUSINESS_DAY_DATE);
        sql.addOrdering(ALIAS_BILL_PAY + "." + FIELD_WORKSTATION_SEQUENCE_NUMBER);
    }
}
