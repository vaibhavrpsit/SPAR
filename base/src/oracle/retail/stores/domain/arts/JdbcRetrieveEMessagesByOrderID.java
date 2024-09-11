/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcRetrieveEMessagesByOrderID.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:00 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/15/10 - remove deprecate string-to-timestamp methods
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    djenning  12/01/08 - wrapping pos sql timestamp inserts/updates/selects
 *                         with the proper db function for oracle 11g
 *
 * ===========================================================================
 * $Log:
 *    7    360Commerce 1.6         3/29/2008 9:50:08 AM   Dwight D. Jennings
 *         changing column holding content of the EMessage from varchar 250 to
 *          a blob.
 *
 *         Reviewed by Dan Baker. Luis approved the column type change.
 *    6    360Commerce 1.5         6/30/2006 4:13:59 PM   Brendan W. Farrell
 *         Update for UDM.
 *    5    360Commerce 1.4         6/9/2006 2:38:36 PM    Brett J. Larsen CR
 *         18490 - UDM
 *         FL_NM_.* changed to NM_.*
 *         timestamp fields in DO_EMSG renamed to ts_..._emsg for consistency
 *    4    360Commerce 1.3         1/25/2006 4:11:20 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:42 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:47 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:01 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:26:04    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:42     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:47     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:01     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:44  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:35  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:44  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:21  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:32   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:39:02   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:46:10   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:06:16   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:59:46   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:12   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.emessage.EMessageIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation reads all of the emessages from the emessage table, matching a
 * specified status with optional storeid, begin date, and end date.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcRetrieveEMessagesByOrderID extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -7062614742858477765L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcRetrieveEMessagesByOrderID.class);

    /**
     * Class constructor.
     */
    public JdbcRetrieveEMessagesByOrderID()
    {
        super();
        setName("JdbcRetrieveEMessagesByOrderID");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcRetrieveEMessagesByOrderID.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // grab arguments and call RetrieveEMessagesByOrderID()
        OrderSearchKey orderSearchKey = (OrderSearchKey)action.getDataObject();
        Vector<EMessageIfc> emessageVector = retrieveEMessagesByOrderID(connection, orderSearchKey);
        EMessageIfc[] emessageList = new EMessageIfc[emessageVector.size()];
        emessageVector.copyInto(emessageList);

        dataTransaction.setResult(emessageList);

        if (logger.isDebugEnabled())
            logger.debug("JdbcRetrieveEMessagesByOrderID.execute");
    }

    /**
     * Creates the SQL statements against the database. Retrieves emessage
     * objects based upon the orderSearchKey: orderID,beginDate,endDate. The
     * beginDate, and endDate are optional.
     * 
     * @param dataConnection
     * @param orderSearchKey
     */
    public Vector<EMessageIfc> retrieveEMessagesByOrderID(JdbcDataConnection connection,
                                             OrderSearchKey orderSearchKey) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcOrderReadTransaction.RetrieveEMessagesByOrderID()");

        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables
        sql.addTable(TABLE_EMESSAGE, ALIAS_EMESSAGE);

        // add columns
        sql.addColumn(FIELD_EMESSAGE_TEXT);

        sql.addColumn(FIELD_EMESSAGE_ID);
        sql.addColumn(FIELD_ORDER_ID);
        sql.addColumn(FIELD_EMESSAGE_STATUS);
        sql.addColumn(FIELD_EMESSAGE_CREATED);
        sql.addColumn(FIELD_EMESSAGE_SENT);
        sql.addColumn(FIELD_EMESSAGE_UPDATED);
        sql.addColumn(FIELD_EMESSAGE_SOURCE);
        sql.addColumn(FIELD_EMESSAGE_DESTINATION);
        sql.addColumn(FIELD_EMESSAGE_SUBJECT);
        sql.addColumn(FIELD_RETAIL_STORE_ID);
        sql.addColumn(FIELD_CUSTOMER_ID);
        sql.addColumn(FIELD_CUSTOMER_NAME);
        sql.addColumn(FIELD_ORDER_LOCATION);

        // get order ID

        String orderID = orderSearchKey.getOrder().getOrderID();
        if (orderID != null)
        {
            sql.addQualifier(FIELD_ORDER_ID + " = '" + orderID +"'");
        }

        EYSDate beginDate = orderSearchKey.getBeginDate();
        if (beginDate != null)
        {
            sql.addQualifier(FIELD_EMESSAGE_CREATED + " >= " + dateToSQLTimestampFunction(beginDate));
        }

        EYSDate endDate = orderSearchKey.getEndDate();
        if (endDate != null)
        {
            sql.addQualifier(FIELD_EMESSAGE_CREATED + " <= " + dateToSQLTimestampFunction(endDate));
        }

        // Ordering
        sql.addOrdering(FIELD_EMESSAGE_CREATED + " DESC");

        // Instantiate Order
        int rsStatus = 0;
        Vector<EMessageIfc> emessageVector = new Vector<EMessageIfc>();
        try
        {
            // execute sql and get result set
            connection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)connection.getResult();

            int index;

            // loop through result set
            while (rs.next())
            {
                ++rsStatus;
                String text = getBlobStringFromResultSetWithoutNext(rs);

                // since we already processed the first column as a blob, start index at 1
                index = 1;
                String emsgIDString = getSafeString(rs, ++index);
                String orderIDString = getSafeString(rs, ++index);
                int emsgStatus = rs.getInt(++index);
                Date beginDay = rs.getDate(++index);
                Date sentDay = rs.getDate(++index);
                Date updateDate = rs.getDate(++index);
                String src = getSafeString(rs, ++index);
                String dest = getSafeString(rs, ++index);
                String subject = getSafeString(rs, ++index);
                String storeID = getSafeString(rs, ++index);
                String custID = getSafeString(rs, ++index);
                String custName = getSafeString(rs, ++index);
                String storeLoc = getSafeString(rs, ++index);

                // Instantiate emessage
                EMessageIfc emessage = DomainGateway.getFactory().getEMessageInstance();
                emessage.setMessageID(emsgIDString);
                emessage.setOrderID(orderIDString);
                emessage.setMessageStatus(emsgStatus);
                emessage.setTimestampBegin(new EYSDate(beginDay));
                emessage.setTimestampSent(new EYSDate(sentDay));
                emessage.setTimestampUpdate(new EYSDate(updateDate));
                emessage.setMessageText(text);
                emessage.setSender(src);
                emessage.addRecipient(dest);
                emessage.setSubject(subject);
                emessage.setShipToStoreID(storeID);
                emessage.setCustomerID(custID);
                emessage.setCustomerName(custName);
                emessage.setShipToLocationName(storeLoc);

                emessageVector.addElement(emessage);
            }
            // end loop through result set
            // close result set
            rs.close();

        }
        catch (DataException de)
        {
            logger.warn(de);
            if (de.getErrorCode() == DataException.UNKNOWN)
            {
                throw new DataException(DataException.CONNECTION_ERROR, "Connection lost");
            }
            
            throw de;
        }
        catch (SQLException se)
        {
            connection.logSQLException(se, "emessage table");
            throw new DataException(DataException.SQL_ERROR, "emessage table", se);
        }

        if (rsStatus == 0)
        {
            logger.warn( "No emessages found");
            throw new DataException(DataException.NO_DATA, "No emessagers found");
        }

        if (logger.isDebugEnabled()) logger.debug( "JdbcOrderReadTransaction.RetrieveEMessagesByOrderID()");

        return(emessageVector);
    }
}
