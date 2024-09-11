/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcUpdateEMessage.java /main/15 2013/09/05 10:36:14 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    cgreene   01/10/11 - refactor blob helpers into one
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   03/19/09 - change access to blob helper
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         3/29/2008 9:50:08 AM   Dwight D. Jennings
 *         changing column holding content of the EMessage from varchar 250 to
 *          a blob.
 *
 *         Reviewed by Dan Baker. Luis approved the column type change.
 *    4    360Commerce 1.3         1/25/2006 4:11:26 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:45 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:52 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:05 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:26:18    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:45     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:52     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:05     Robert Pearse
 *
 *   Revision 1.7  2004/08/18 22:33:27  cdb
 *   @scr 6644 DB2 updates.
 *
 *   Revision 1.6  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:38  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:48  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:26  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:33:16   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:40:52   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:49:28   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:09:06   msg
 * Initial revision.
 *
 *    Rev 1.1   Feb 05 2002 13:24:26   dfh
 * protect strings to db
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Sep 20 2001 15:57:42   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:48   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.SQLException;
import java.util.HashMap;

import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.emessage.EMessageIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;
import oracle.retail.stores.persistence.utility.DatabaseBlobHelperIfc;
import oracle.retail.stores.persistence.utility.DatabaseBlobHelperFactory;

import org.apache.log4j.Logger;

/**
 * This operation updates an emessage from the emessage table, matching a
 * specified emessage id.
 * 
 * @version $Revision: /main/15 $
 */
public class JdbcUpdateEMessage extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 1401902389857332107L;
    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(JdbcUpdateEMessage.class);

    /**
     * Class constructor.
     */
    public JdbcUpdateEMessage()
    {
        setName("JdbcUpdateEMessage");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdateEMessage.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // grab arguments and call UpdateEMessage()
        EMessageIfc emessage = (EMessageIfc)action.getDataObject();
        UpdateEMessage(connection, emessage);

        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdateEMessage.execute");
    }

    /**
     * Creates the SQL statements against the database. Updates the emessage
     * object based upon the emessage argument.
     * 
     * @param dataConnection
     * @param emessage
     **/
    public void UpdateEMessage(JdbcDataConnection connection,
                               EMessageIfc emessage) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcEMessageWriteDataTransaction.UpdateEMessage()");

        SQLUpdateStatement sql = new SQLUpdateStatement();

        // set table
        sql.setTable(TABLE_EMESSAGE);

        // add columns
        sql.addColumn(FIELD_EMESSAGE_STATUS, emessage.getMessageStatus());
        sql.addColumn(FIELD_EMESSAGE_SENT, dateToSQLTimestampString(emessage.getTimestampSent()));
        // set update timestamp to current date
        sql.addColumn(FIELD_EMESSAGE_UPDATED, dateToSQLTimestampString(emessage.getTimestampUpdate()));
        sql.addColumn(FIELD_ORDER_LOCATION, makeSafeString(emessage.getShipToLocationName()));
        sql.addColumn(FIELD_EMESSAGE_SOURCE, makeSafeString(emessage.getSender()));
        sql.addColumn(FIELD_EMESSAGE_SUBJECT, makeSafeString(emessage.getSubject()));
        sql.addColumn(FIELD_CUSTOMER_ID, makeSafeString(emessage.getCustomerID()));
        sql.addColumn(FIELD_CUSTOMER_NAME, makeSafeString(emessage.getCustomerName()));
        sql.addColumn(FIELD_RETAIL_STORE_ID, makeSafeString(emessage.getShipToStoreID()));

        // add qualifiers for the EMessageID
        sql.addQualifier(FIELD_EMESSAGE_ID + " = '" + emessage.getMessageID() + "'");

        try
        {
            connection.execute(sql.getSQLString());
            
            // save the blob.
            HashMap<String,Object> map = new HashMap<String,Object>(1);
            map.put(FIELD_EMESSAGE_ID, emessage.getMessageID());
            DatabaseBlobHelperIfc helper = DatabaseBlobHelperFactory.getInstance().getDatabaseBlobHelper(
                    connection.getConnection());
            if (helper != null)
            {

                String msgText = (emessage.getMessageText()).trim();
                helper.updateBlob(connection.getConnection(),
                        TABLE_EMESSAGE, FIELD_EMESSAGE_TEXT, msgText.getBytes(), map);
            }               
            
        }
        catch (DataException de)
        {
            logger.warn(de.toString());
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

        if (0 >= connection.getUpdateCount())
        {
            logger.warn( "No emessage updated ");
            throw new DataException(DataException.NO_DATA, "No emessage updated ");
        }
    }
}
