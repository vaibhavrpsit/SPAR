/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveOrderPaymentHistoryInfo.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:03 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
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

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.financial.PaymentHistoryInfoIfc;
import oracle.retail.stores.domain.order.OrderStatusIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.utility.EYSStatusIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This operation saves (inserts/updates) info in the order payment history info
 * table This info is used for IRS Patriot Act
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcSaveOrderPaymentHistoryInfo extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 8594594207839179431L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveOrderPaymentHistoryInfo.class);

    /**
     * The performance logger
     */
    protected static final Logger perf = Logger.getLogger("PERF." + JdbcSaveOrderPaymentHistoryInfo.class.getName());

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Class constructor.
     */
    public JdbcSaveOrderPaymentHistoryInfo()
    {
        setName("JdbcSaveOrderPaymentHistoryInfo");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveOrderPaymentHistoryInfo.execute()");
        if (perf.isDebugEnabled())
        {
            perf.debug("Entering JdbcSaveOrderPaymentHistoryInfo.execute");
        }
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
        OrderTransactionIfc orderTransaction =
          (OrderTransactionIfc) action.getDataObject();
        
        for (int listIndex = 0; listIndex < orderTransaction.getPaymentHistoryInfoCollection().size(); listIndex++)
        {
            this.updateOrderPaymentHistoryInfo(connection,
                orderTransaction,
                listIndex);
        }
        if (perf.isDebugEnabled())
        {
            perf.debug("Exiting JdbcSaveOrderPaymentHistoryInfo.execute");
        }
        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveOrderPaymentHistoryInfo.execute()");
    }
    
    
    /**
        Perform order payment history info insert. <P>
        @param dataConnection JdbcDataConnection
        @param orderTransaction OrderTransaction reference
        @param listIndex retrieve index from list
        @exception DataException thrown if error occurs
     */
    protected void insertOrderPaymentHistoryInfo(JdbcDataConnection dataConnection,
                                                 OrderTransactionIfc orderTransaction,
                                                  int listIndex)
                                                  throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();
        
        String status = getOrderStatus(orderTransaction);
        PaymentHistoryInfoIfc paymentHistory = orderTransaction.getPaymentHistoryInfoCollection().get(listIndex);
        
        // Table
        sql.setTable(TABLE_IRS_ORDER);
        
        //coloumns
        sql.addColumn(FIELD_ORDER_ID, 
                      makeSafeString(orderTransaction.getOrderID()));
        sql.addColumn(FIELD_ORDER_STATUS, 
                      status);
        sql.addColumn(FIELD_CUSTOMER_ID, 
                      makeSafeString(orderTransaction.getCustomer().getCustomerID()));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, 
                      dateToSQLDateString(orderTransaction.getBusinessDay()));
        sql.addColumn(FIELD_TENDER_TYPE_CODE, 
                      makeSafeString(paymentHistory.getTenderType()));                 
        sql.addColumn(FIELD_PAYMENT_HISTORY_INFO_COUNTRY_CODE, 
                      makeSafeString(paymentHistory.getCountryCode()));
        sql.addColumn(FIELD_PAYMENT_HISTORY_INFO_TENDER_AMOUNT, 
                      paymentHistory.getTenderAmount().toString());
        
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "insertOrderPaymentHistoryInfo", e);
        }
    }
    
    /**
        Perform order payment history info update. <P>
        @param dataConnection JdbcDataConnection
        @param orderTransaction OrderTransaction reference
        @param listIndex retrieve index from list
        @exception DataException thrown if error occurs
     */
    public void updateOrderPaymentHistoryInfo(JdbcDataConnection dataConnection,
                                              OrderTransactionIfc orderTransaction,
                                              int listIndex)
                                              throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();
        
        String status = getOrderStatus(orderTransaction);
        PaymentHistoryInfoIfc paymentHistory = orderTransaction.getPaymentHistoryInfoCollection().get(listIndex);
        
        // Table
        sql.setTable(TABLE_IRS_ORDER);
        
        // Fields        
        sql.addColumn(FIELD_PAYMENT_HISTORY_INFO_TENDER_AMOUNT, paymentHistory.getTenderAmount().toString());
        sql.addColumn(FIELD_ORDER_STATUS, status);
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(orderTransaction.getBusinessDay()));
              
        //Qualifiers
        sql.addQualifier(FIELD_ORDER_ID, 
                         makeSafeString(orderTransaction.getOrderID()));
        sql.addQualifier(FIELD_CUSTOMER_ID, 
                         makeSafeString(orderTransaction.getCustomer().getCustomerID()));
        sql.addQualifier(FIELD_TENDER_TYPE_CODE, 
                         makeSafeString(paymentHistory.getTenderType()));       
        sql.addQualifier(FIELD_PAYMENT_HISTORY_INFO_COUNTRY_CODE, 
                         makeSafeString(paymentHistory.getCountryCode()));
        
        try
        {
            dataConnection.execute(sql.getSQLString());
            int recordsUpdated = dataConnection.getUpdateCount();
            if (recordsUpdated == 0)
            {
                this.insertOrderPaymentHistoryInfo(dataConnection,
                    orderTransaction,
                    listIndex);
            }
            else if (recordsUpdated > 1)
            {
                throw new DataException(DataException.KEY_VIOLATION_ERROR, "Multiple records updated " + recordsUpdated);
            }
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "updateOrderPaymentHistoryInfo", e);
        }

    }
    
    /**
     * Gets the order status
     * @param orderTransaction order transaction reference
     * @return status value for the order
     */
    protected String getOrderStatus(OrderTransactionIfc orderTransaction) 
    {
        OrderStatusIfc orderStatus = orderTransaction.getOrderStatus();
        EYSStatusIfc eysStatus = orderStatus.getStatus();
        int statusId = eysStatus.getStatus();
        String status = Integer.toString(statusId);
        return status;
    }

    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return(revisionNumber);
    }

    /**
       Returns the string representation of this object.
       @return String representation of object
     */
    @Override
    public String toString()
    {
        return(Util.classToStringHeader("JdbcSaveOrderPaymentHistoryInfo",
                                        getRevisionNumber(),
                                        hashCode()).toString());
    }
}

