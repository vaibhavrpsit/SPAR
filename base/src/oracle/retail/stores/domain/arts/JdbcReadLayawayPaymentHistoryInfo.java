/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadLayawayPaymentHistoryInfo.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:59 mszekely Exp $
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


import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.financial.PaymentHistoryInfoIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * JdbcReadLayawayPaymentHistoryInfo implements the layaway payment history info
 * look up
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcReadLayawayPaymentHistoryInfo extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 6085799171699168022L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadLayawayPaymentHistoryInfo.class);

    /**
     * The performance logger
     */
    protected static final Logger perf = Logger.getLogger("PERF." + JdbcReadLayawayPaymentHistoryInfo.class.getName());

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Class constructor.
     */
    public JdbcReadLayawayPaymentHistoryInfo()
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadLayawayPaymentHistoryInfo.execute");
        if (perf.isDebugEnabled())
        {
            perf.debug("Entering JdbcReadLayawayPaymentHistoryInfo.execute");
        }
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
        LayawayIfc inputLayaway = (LayawayIfc) action.getDataObject();
        LayawayIfc layaway = readLayawayPaymentHistoryInfo(connection, inputLayaway);
        dataTransaction.setResult(layaway);
        if (perf.isDebugEnabled())
        {
            perf.debug("Exiting JdbcReadLayawayPaymentHistoryInfo.execute");
        }
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadLayawayPaymentHistoryInfo.execute");
    }

    /**
        Selects a layaway from the layaway payment history table. <P>
        @param  dataConnection  a connection to the database
        @param  inputLayaway layaway containing key values
        @return layaway updated with payment history info
        @exception  DataException thrown when an error occurs executing the
        SQL against the DataConnection, or when processing the ResultSet
     */
    protected LayawayIfc readLayawayPaymentHistoryInfo(JdbcDataConnection dataConnection,
                                     LayawayIfc inputLayaway)
                                     throws DataException
    {                                   
        LayawayIfc layaway = null;
        SQLSelectStatement sql = buildLayawayPaymentHistoryInfoSQLStatement(inputLayaway);
        ResultSet rs;
        try
        {
            String sqlString = sql.getSQLString();
            dataConnection.execute(sqlString);
            rs = (ResultSet) dataConnection.getResult();
            layaway = parseLayawayPaymentHistoryInfoResultSet(rs, inputLayaway);
        }
        catch (DataException de)
        {
            logger.warn(de);
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "Layaway Payment History Info lookup");
            throw new DataException(DataException.SQL_ERROR, "Layaway Payment History Info lookup", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "Layaway Payment History Info lookup", e);
        }
        return(layaway);
    }               

    /**
        Builds and returns SQL statement for retrieving layaway payment history info. <P>
        @param layaway input layaway
        @return SQL select statement for retrieving a layaway
     */
    protected SQLSelectStatement buildLayawayPaymentHistoryInfoSQLStatement(LayawayIfc layaway)
    {                                   
        SQLSelectStatement sql = new SQLSelectStatement();
        sql.addTable(TABLE_IRS_LAYAWAY);
        sql.addColumn(FIELD_TENDER_TYPE_CODE);
        sql.addColumn(FIELD_PAYMENT_HISTORY_INFO_COUNTRY_CODE);
        sql.addColumn(FIELD_PAYMENT_HISTORY_INFO_TENDER_AMOUNT);
        sql.addQualifier(FIELD_LAYAWAY_ID,
                         "UPPER(" + makeSafeString(layaway.getLayawayID()) + ")");

        return(sql);
    }                                  

    /**
        Parse layaway result set and returns layaway object. <P>
        @param rs ResultSet object
        @param inputLayaway layaway reference object
        @return updated layaway reference with payment history info
        @exception  DataException data exception
        @exception  SQLException sql exception
     */
    protected LayawayIfc parseLayawayPaymentHistoryInfoResultSet(ResultSet rs, LayawayIfc inputLayaway)
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
                inputLayaway.addPaymentHistoryInfo(paymentHistoryInfo);
            }
            
            rs.close();
        }
        catch (SQLException se)
        {
            throw se;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "Layaway Payment History Info lookup", e);
        }
        
        return inputLayaway;
    }                                  
}
