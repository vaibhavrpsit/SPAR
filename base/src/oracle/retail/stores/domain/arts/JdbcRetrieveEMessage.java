/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcRetrieveEMessage.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:03 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/15/10 - remove deprecate string-to-timestamp methods
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         3/29/2008 9:50:08 AM   Dwight D. Jennings
 *         changing column holding content of the EMessage from varchar 250 to
 *          a blob.
 *
 *         Reviewed by Dan Baker. Luis approved the column type change.
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
 *    4    .v700     1.2.1.0     11/16/2005 16:25:54    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.alert.AlertEntryIfc;
import oracle.retail.stores.domain.emessage.EMessageIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation reads the emessage from the emessage table, matching a
 * specified emessage id.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcRetrieveEMessage extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 8901833077871019874L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcRetrieveEMessage.class);

    /**
     * Class constructor.
     */
    public JdbcRetrieveEMessage()
    {
        super();
        setName("JdbcRetrieveEMessage");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcRetrieveEMessage.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
        EMessageIfc emessage;

        // grab arguments and call RetrieveEMessage()
        if (action.getDataObject() instanceof AlertEntryIfc)
        {
            if (logger.isInfoEnabled()) logger.info("RetrieveEMessage by alert");
            AlertEntryIfc alert = (AlertEntryIfc)action.getDataObject();
            emessage = RetrieveEMessage(connection, alert.getItemID());
        }
        else
        {
            if (logger.isInfoEnabled()) logger.info("RetrieveEMessage by emessageID");
            String emessageID = (String)action.getDataObject();
            emessage = RetrieveEMessage(connection, emessageID);
        }
        dataTransaction.setResult(emessage);

        if (logger.isDebugEnabled()) logger.debug( "JdbcRetrieveEMessage.execute");
    }

    /**
     * Creates the SQL statements against the database. Retrieves an emessage
     * object based upon the emessage id.
     * 
     * @param dataConnection
     * @param emessageID emessage identifier
     */
    public EMessageIfc RetrieveEMessage(JdbcDataConnection connection,
                                        String emessageID) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcEMessageReadDataTransaction.RetrieveEMessage()");

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
        sql.addColumn(FIELD_ORDER_LOCATION);    // store location

        // add qualifier
        sql.addQualifier(FIELD_EMESSAGE_ID + " = '" + emessageID + "'");

        // Instantiate emessage
        int rsStatus = 0;
        EMessageIfc emessage = DomainGateway.getFactory().getEMessageInstance();

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

                // parse the data from the database
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
            throw new DataException(DataException.NO_DATA, "No emessages found");
        }

        if (logger.isDebugEnabled()) logger.debug( "JdbcEMessageReadDataTransaction.RetrieveEMessage()");

        return(emessage);
    }
}
