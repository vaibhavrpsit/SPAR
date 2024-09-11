/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadOrderPaymentHistoryInfo.java /main/13 2012/05/11 14:47:09 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       05/04/12 - refactor OrderStatus to support store order and
 *                         xchannel order
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    1    360Commerce 1.0         12/13/2005 4:47:56 PM  Barry A. Pape
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.PaymentHistoryInfoIfc;
import oracle.retail.stores.domain.order.OrderStatusIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.utility.EYSStatusIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * JdbcReadOrderPaymentHistoryInfo implements the order payment history info
 * look up
 *
 * @version $Revision: /main/13 $
 */
public class JdbcReadOrderPaymentHistoryInfo extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 7285599785521321789L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadOrderPaymentHistoryInfo.class);

    /**
     * The performance logger
     */
    protected static final Logger perf = Logger.getLogger("PERF." + JdbcReadOrderPaymentHistoryInfo.class.getName());

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
     * Class constructor.
     */
    public JdbcReadOrderPaymentHistoryInfo()
    {
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadOrderPaymentHistoryInfo.execute");
        if (perf.isDebugEnabled())
        {
            perf.debug("Entering JdbcReadOrderPaymentHistoryInfo.execute");
        }
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
        OrderTransactionIfc inputOrderTransaction =
          (OrderTransactionIfc) action.getDataObject();
        OrderTransactionIfc orderTransaction = readOrderPaymentHistoryInfo(connection, inputOrderTransaction);
        dataTransaction.setResult(orderTransaction);
        if (perf.isDebugEnabled())
        {
            perf.debug("Exiting JdbcReadOrderPaymentHistoryInfo.execute");
        }
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadOrderPaymentHistoryInfo.execute");
    }

    /**
        Reads order payment history table. <P>
        @param  dataConnection  a connection to the database
        @param  inputOrderTransaction order transaction containing key values
        @return order transaction updated with payment history info
        @exception  DataException thrown when an error occurs executing the
        SQL against the DataConnection, or when processing the ResultSet
     */
    protected OrderTransactionIfc readOrderPaymentHistoryInfo(JdbcDataConnection dataConnection,
                                                              OrderTransactionIfc inputOrderTransaction)
                                                              throws DataException
    {
        OrderTransactionIfc orderTransaction = null;
        SQLSelectStatement sql = buildOrderPaymentHistoryInfoSQLStatement(inputOrderTransaction);
        ResultSet rs;
        try
        {
            String sqlString = sql.getSQLString();
            dataConnection.execute(sqlString);
            rs = (ResultSet) dataConnection.getResult();
            orderTransaction = parseOrderPaymentHistoryInfoResultSet(rs, inputOrderTransaction);
        }
        catch (DataException de)
        {
            logger.warn(de);
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "Order Payment History Info lookup");
            throw new DataException(DataException.SQL_ERROR, "Order Payment History Info lookup", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "Order Payment History Info lookup", e);
        }
        return(orderTransaction);
    }

    /**
        Builds and returns SQL statement for retrieving order payment history info. <P>
        @param inputOrderTransaction input order transaction
        @return SQL select statement for retrieving payment history info
     */
    protected SQLSelectStatement buildOrderPaymentHistoryInfoSQLStatement(OrderTransactionIfc inputOrderTransaction)
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        //table
        sql.addTable(TABLE_IRS_ORDER);
        //coloumn
        sql.addColumn(FIELD_TENDER_TYPE_CODE);
        sql.addColumn(FIELD_PAYMENT_HISTORY_INFO_COUNTRY_CODE);
        sql.addColumn(FIELD_PAYMENT_HISTORY_INFO_TENDER_AMOUNT);
        //qualifier
        sql.addQualifier(FIELD_ORDER_ID,
                         makeSafeString(inputOrderTransaction.getOrderID()));
        if (inputOrderTransaction.getCustomer() != null)
        {
            sql.addQualifier(FIELD_CUSTOMER_ID,
                    makeSafeString(inputOrderTransaction.getCustomer().getCustomerID()));
        }

        return(sql);
    }

    /**
        Parse result set and returns order object with payment history info. <P>
        @param rs ResultSet object
        @param inputOrderTransaction order transaction reference object
        @return updated order transaction reference with payment history info
        @exception  DataException data exception
        @exception  SQLException sql exception
     */
    protected OrderTransactionIfc parseOrderPaymentHistoryInfoResultSet(ResultSet rs, OrderTransactionIfc inputOrderTransaction)
    throws DataException, SQLException
    {
        try
        {
            PaymentHistoryInfoIfc paymentHistoryInfo = null;
            while (rs.next())
            {
                paymentHistoryInfo = DomainGateway.getFactory().getPaymentHistoryInfoInstance();
                int index = 0;
                paymentHistoryInfo.setTenderType(getSafeString(rs, ++index));
                paymentHistoryInfo.setCountryCode(getSafeString(rs, ++index));
                paymentHistoryInfo.setTenderAmount(getCurrencyFromDecimal(rs, ++index));
                inputOrderTransaction.addPaymentHistoryInfo(paymentHistoryInfo);
            }

            rs.close();
        }
        catch (SQLException se)
        {
            throw se;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "Order Payment History Info lookup", e);
        }

        return inputOrderTransaction;
    }
}
