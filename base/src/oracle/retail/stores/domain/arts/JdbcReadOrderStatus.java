/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadOrderStatus.java /main/21 2014/06/20 15:58:38 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       06/18/14 - fix nullpointer in orderstatus
 *    sgu       06/09/14 - read item original transaction for an order
 *    abondala  10/29/13 - retrieve order by order id supports case insensitive
 *                         search.
 *    jswan     05/15/13 - Modified to prevent the return of orders which are
 *                         part of a suspended transaction.
 *    sgu       05/09/12 - separate minimum deposit amount into xchannel part
 *                         and store order part
 *    sgu       05/08/12 - prorate store order and xchannel deposit amount
 *                         separatly
 *    sgu       05/04/12 - refactor OrderStatus to support store order and
 *                         xchannel order
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    jswan     04/24/09 - Code review changes.
 *    jswan     04/24/09 - Modified to ensure that orders created in training
 *                         mode can only retrieve in training mode, and
 *                         non-training mode orders can only be retrieved in
 *                         non-training mode.
 *    jswan     02/20/09 - Added Order Type column to read. Removed unused
 *                         columns.
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:11:17 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:44 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:59 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:33    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:41     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:44     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:59     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:45  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
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
 *    Rev 1.0   Aug 29 2003 15:32:02   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Mar 07 2003 15:45:58   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.0   Jun 03 2002 16:37:48   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:47:34   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:07:34   msg
 * Initial revision.
 *
 *    Rev 1.6   28 Jan 2002 17:02:46   sfl
 * Changed the date data type from EYSDate to Timestamp
 * when reading the order status created time value from the
 * database order table so that correct create date will be displayed in the Order Location screen.
 * Resolution for Domain SCR-14: Special Order modifications
 *
 *    Rev 1.5   Jan 24 2002 16:44:14   dfh
 * updates to support order item location, default value is NA
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.4   Jan 17 2002 21:26:22   dfh
 * initialize order status object
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.3   Jan 09 2002 14:34:08   mpm
 * Modified to properly handle status, timestamp created.
 * Resolution for Domain SCR-14: Special Order modifications
 *
 *    Rev 1.2   05 Dec 2001 12:53:38   mpm
 * Corrected for not-found in read-order.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   03 Dec 2001 19:18:34   mpm
 * Added database support for minimum deposit amount, balance due.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   27 Nov 2001 06:25:40   mpm
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;
// java imports
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderStatusIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSStatusIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;
import oracle.retail.stores.persistence.utility.DBConstantsIfc;

import org.apache.log4j.Logger;

//--------------------------------------------------------------------------
/**
    This operation reads the order status for a given order ID. <P>
    @version $Revision: /main/21 $
**/
//--------------------------------------------------------------------------
public class JdbcReadOrderStatus
extends JdbcDataOperation implements ARTSDatabaseIfc
{
    /** UID */
    private static final long serialVersionUID = 8134444693412391860L;
    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcReadOrderStatus.class);

    //----------------------------------------------------------------------
    /**
       Class constructor.
    **/
    //----------------------------------------------------------------------
    public JdbcReadOrderStatus()
    {
        super();
        setName("JdbcReadOrderStatus");
    }

    //----------------------------------------------------------------------
    /**
       Executes the SQL statements against the database.
       <P>
       @param  dataTransaction     The data transaction
       @param  dataConnection      The connection to the data source
       @param  action              The information passed by the valet
       @exception DataException upon error
    **/
    //----------------------------------------------------------------------
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcReadOrderStatus.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        try
        {
            // grab arguments and call retrieveorder()
            OrderStatusIfc orderIn = (OrderStatusIfc) action.getDataObject();
            OrderStatusIfc orderOut = readOrderStatus(connection, orderIn);
            if (orderIn.getLocale() != null)
            {
                orderOut.setLocale(orderIn.getLocale());
            }
            dataTransaction.setResult(orderOut);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            logger.error(
                         "" + Util.throwableToString(e) + "");
            throw new DataException(DataException.UNKNOWN,
                                    "read order status");
        }

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcReadOrderStatus.execute");
    }

    //---------------------------------------------------------------------
    /**
       Reads the order status object. <P>
       @param  dataConnection  connection to the db
       @param  orderIn         order with order id to search for the complete order
       @return order status object
       @exception DataException upon error
    **/
    //---------------------------------------------------------------------
    /**
     * @param connection
     * @param orderIn
     * @return
     * @throws DataException
     */
    public OrderStatusIfc readOrderStatus(JdbcDataConnection connection,
                                          OrderStatusIfc orderIn) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcOrderReadTransaction.readOrderStatus()");

        // build SQL statement
        SQLSelectStatement sql = buildSQLSelectStatement(orderIn);

        int recordsFound = 0;
        int orderItemColumnIndex = 0;
        OrderStatusIfc orderStatus = null;

        try
        {
            // execute sql and get result set
            connection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet) connection.getResult();

            int index = 0;
            EYSStatusIfc status = null;
            TransactionIDIfc originalTransactionID = null;
            TransactionIDIfc recordingTransactionID = null;
            TransactionIDIfc itemOriginalTransactionID = null;

            // loop through result set
            while (rs.next())
            {                           // begin loop through result set
                recordsFound++;

                // instantatiate order status object once 
                if (orderStatus == null)
                {
                    orderStatus =
                            DomainGateway.getFactory().getOrderStatusInstance();
                    status = DomainGateway.getFactory().getEYSStatusInstance();
                    orderStatus.setStoreOrderStatus(status);
                    originalTransactionID =
                            DomainGateway.getFactory().getTransactionIDInstance();
                    orderStatus.setInitialTransactionID(originalTransactionID);
                    recordingTransactionID =
                            DomainGateway.getFactory().getTransactionIDInstance();
                    orderStatus.setRecordingTransactionID(recordingTransactionID);

                    orderStatus.setOrderID(getSafeString(rs, ++index));
                    status.setStatus(rs.getInt(++index));
                    status.setPreviousStatus(rs.getInt(++index));
                    status.setLastStatusChange(getEYSDateFromString(rs, ++index));
                    orderStatus.setTimestampBegin(getEYSDateFromString(rs, ++index));
                    Timestamp createTime = rs.getTimestamp(++index);
                    EYSDate testDate = new EYSDate(createTime);
                    orderStatus.setTimestampCreated(testDate);
                    orderStatus.setStoreOrderTotal(getCurrencyFromDecimal(rs, ++index));
                    orderStatus.setStoreOrderDepositAmount(getCurrencyFromDecimal(rs, ++index));
                    orderStatus.setInitiatingChannel(rs.getInt(++index));
                    orderStatus.setStoreOrderMinimumDepositAmount(getCurrencyFromDecimal(rs, ++index));
                    orderStatus.setStoreOrderBalanceDue(getCurrencyFromDecimal(rs, ++index));
                    originalTransactionID.setStoreID(getSafeString(rs, ++index));
                    originalTransactionID.setWorkstationID(getSafeString(rs, ++index));
                    orderStatus.setInitialTransactionBusinessDate(getEYSDateFromString(rs, ++index));
                    originalTransactionID.setSequenceNumber(rs.getLong(++index));
                    recordingTransactionID.setStoreID(getSafeString(rs, ++index));
                    recordingTransactionID.setWorkstationID(getSafeString(rs, ++index));
                    orderStatus.setRecordingTransactionBusinessDate(getEYSDateFromString(rs, ++index));
                    recordingTransactionID.setSequenceNumber(rs.getLong(++index));
                    orderStatus.initializeStatus();
                    orderStatus.setLocation(getSafeString(rs, ++index));
                    orderStatus.setOrderType(rs.getInt(++index));
                    orderStatus.setTrainingModeFlag(getBooleanFromString(rs, ++index));
                    orderStatus.setSaleAmount(getCurrencyFromDecimal(rs, ++index));
                    orderItemColumnIndex = index; // remember the start index of order item columns
                }
                
                index = orderItemColumnIndex+1; // set the index to the start of order item original transaction id columns; add 1 to skip the order item sequence number column
                itemOriginalTransactionID = DomainGateway.getFactory().getTransactionIDInstance();
                itemOriginalTransactionID.setStoreID(getSafeString(rs, ++index));
                itemOriginalTransactionID.setWorkstationID(getSafeString(rs, ++index));
                EYSDate itemOriginalBusinessDate = getEYSDateFromString(rs, ++index);
                itemOriginalTransactionID.setSequenceNumber(rs.getLong(++index));
                orderStatus.addItemOriginalTransactionIDBusinessDate(itemOriginalTransactionID, itemOriginalBusinessDate);
                
            } // end loop through result set
            // close result set
            rs.close();
            
            // This is for backward compatibility for 14.0. In case item original transaction id is not populated, just use 
            // the recording transaction id at order level.
            if ((orderStatus != null) && orderStatus.getItemOriginalTransactionIDs().length == 0)
            {
                orderStatus.addItemOriginalTransactionIDBusinessDate(orderStatus.getRecordingTransactionID(), 
                        orderStatus.getRecordingTransactionBusinessDate());
            }
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (SQLException se)
        {
            connection.logSQLException(se, "order table");
            throw new DataException(DataException.SQL_ERROR, "order table", se);
        }

        if (recordsFound == 0)
        {
            logger.warn( "No order found");
            throw new DataException(DataException.NO_DATA, "No order found");
        }

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcReadOrderStatus.readOrderStatus()");
        
        return(orderStatus);
    }    // end readOrderStatus

    //---------------------------------------------------------------------
    /**
       Build SQL select statement for retrieving order status
       @param orderIn order status object to be used as key
       @return SQLSelectStatement to be used for reading order
    **/
    //---------------------------------------------------------------------
    public SQLSelectStatement buildSQLSelectStatement(OrderStatusIfc orderIn)
    {                                   // begin buildSQLSelectStatement()
        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables
        sql.addTable(TABLE_ORDER, ALIAS_ORDER);
        sql.addTable(TABLE_ORDER_ITEM, ALIAS_ORDER_ITEM);

        // add select columns
        addSelectColumns(sql);

        // add qualifiers
        addQualifiers(sql, orderIn);

        return(sql);
    }                                   // end buildSQLSelectStatement()

    //---------------------------------------------------------------------
    /**
       Adds select columns to specified SQL statement.
       @param sql SQLSelectStatement
    **/
    //---------------------------------------------------------------------
    protected void addSelectColumns(SQLSelectStatement sql)
    {                                   // begin addSelectColumns()
        // add order table columns
        sql.addColumn(ALIAS_ORDER+"."+FIELD_ORDER_ID);
        sql.addColumn(ALIAS_ORDER+"."+FIELD_ORDER_STATUS);
        sql.addColumn(ALIAS_ORDER+"."+FIELD_ORDER_STATUS_PREVIOUS);
        sql.addColumn(ALIAS_ORDER+"."+FIELD_ORDER_STATUS_CHANGE);
        sql.addColumn(ALIAS_ORDER+"."+FIELD_ORDER_BEGIN);
        sql.addColumn(ALIAS_ORDER+"."+FIELD_ORDER_CREATE_TIMESTAMP);
        sql.addColumn(ALIAS_ORDER+"."+FIELD_ORDER_TOTAL);
        sql.addColumn(ALIAS_ORDER+"."+FIELD_ORDER_DEPOSIT_AMOUNT);
        sql.addColumn(ALIAS_ORDER+"."+FIELD_ORDER_INITIATION_CHANNEL);
        sql.addColumn(ALIAS_ORDER+"."+FIELD_ORDER_MINIMUM_DEPOSIT_AMOUNT);
        sql.addColumn(ALIAS_ORDER+"."+FIELD_ORDER_BALANCE_DUE);
        sql.addColumn(ALIAS_ORDER+"."+FIELD_ORDER_ORIGINAL_STORE_ID);
        sql.addColumn(ALIAS_ORDER+"."+FIELD_ORDER_ORIGINAL_WORKSTATION_ID);
        sql.addColumn(ALIAS_ORDER+"."+FIELD_ORDER_ORIGINAL_TRANSACTION_BUSINESS_DATE);
        sql.addColumn(ALIAS_ORDER+"."+FIELD_ORDER_ORIGINAL_TRANSACTION_SEQUENCE_NUMBER);
        sql.addColumn(ALIAS_ORDER+"."+FIELD_ORDER_RECORDING_STORE_ID);
        sql.addColumn(ALIAS_ORDER+"."+FIELD_ORDER_RECORDING_WORKSTATION_ID);
        sql.addColumn(ALIAS_ORDER+"."+FIELD_ORDER_RECORDING_TRANSACTION_BUSINESS_DATE);
        sql.addColumn(ALIAS_ORDER+"."+FIELD_ORDER_RECORDING_TRANSACTION_SEQUENCE_NUMBER);
        sql.addColumn(ALIAS_ORDER+"."+FIELD_ORDER_LOCATION);
        sql.addColumn(ALIAS_ORDER+"."+FIELD_ORDER_TYPE);
        sql.addColumn(ALIAS_ORDER+"."+FIELD_TRANSACTION_TRAINING_FLAG);
        sql.addColumn(ALIAS_ORDER+"."+FIELD_ORDER_SALE_AMOUNT);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ORDER_ORIGINAL_STORE_ID);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ORDER_ORIGINAL_WORKSTATION_ID);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ORDER_ORIGINAL_TRANSACTION_BUSINESS_DATE);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ORDER_ORIGINAL_TRANSACTION_SEQUENCE_NUMBER);
    }                                   // end addSelectColumns()

    //---------------------------------------------------------------------
    /**
       Add qualifiers for specified SQL statement.
       @param sql SQLSelectStatement
       @param orderIn order input object
    **/
    //---------------------------------------------------------------------
    protected void addQualifiers(SQLSelectStatement sql,
                                 OrderStatusIfc orderIn)
    {                                   // begin addQualifiers()
        // join with OR_ITM table
        sql.addJoinQualifier(ALIAS_ORDER, FIELD_ORDER_ID, ALIAS_ORDER_ITEM, FIELD_ORDER_ID);
      
        // add qualifiers for the transaction ID
        sql.addQualifier(ALIAS_ORDER+"."+FIELD_ORDER_ID_UPPER_CASE + " = UPPER('" + orderIn.getOrderID() + "')");
        String trainingMode = DBConstantsIfc.FALSE;
        if (orderIn.isTrainingMode())
        {
            trainingMode = DBConstantsIfc.TRUE;
        }
        sql.addQualifier(ALIAS_ORDER+"."+FIELD_TRANSACTION_TRAINING_FLAG + " = " + JdbcDataOperation.makeSafeString(trainingMode));
        sql.addQualifier(ALIAS_ORDER+"."+FIELD_ORDER_STATUS + " != " + OrderConstantsIfc.ORDER_STATUS_UNDEFINED);
        sql.addQualifier(ALIAS_ORDER+"."+FIELD_ORDER_STATUS + " != " + OrderConstantsIfc.ORDER_STATUS_SUSPENDED_CANCELED);
        sql.addOrdering(ALIAS_ORDER_ITEM, FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER);
    }                                   // end addQualifiers()


}                                     // end  JdbcreadOrderStatus
