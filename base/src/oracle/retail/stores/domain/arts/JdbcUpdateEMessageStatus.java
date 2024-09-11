/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcUpdateEMessageStatus.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:02 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
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
 *    4    360Commerce 1.3         1/25/2006 4:11:26 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:45 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:52 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:05 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:25:59    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:45     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:52     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:05     Robert Pearse
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
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:22  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:33:18   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:40:54   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:49:30   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:09:08   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:56:26   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:48   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.emessage.EMessageIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation updates an emessage from the emessage table, matching a
 * specified emessage id.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcUpdateEMessageStatus extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 5691258968007698903L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcUpdateEMessageStatus.class);

    /**
     * Class constructor.
     */
    public JdbcUpdateEMessageStatus()
    {
        super();
        setName("JdbcUpdateEMessageStatus");
    }

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction
     * @param dataConnection
     * @param action
     */
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcUpdateEMessageStatus.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // grab arguments and call UpdateEMessageStatus()
        EMessageIfc emessage = (EMessageIfc) action.getDataObject();
        UpdateEMessageStatus(connection, emessage);

        if (logger.isDebugEnabled())
            logger.debug("JdbcUpdateEMessageStatus.execute");
    }

    /**
     * Creates the SQL statements against the database. Updates the emessage
     * object based upon the emessage argument.
     * 
     * @param dataConnection
     * @param emessage
     */
    public void UpdateEMessageStatus(JdbcDataConnection connection,
                                     EMessageIfc emessage) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcEMessageWriteTransaction.UpdateEMessageStatus()");

        SQLUpdateStatement sql = new SQLUpdateStatement();

        // set table
        sql.setTable(TABLE_EMESSAGE);

        // add columns
        sql.addColumn(FIELD_EMESSAGE_STATUS, "'" + emessage.getMessageStatus() + "'");

        // set update timestamp to current date
        sql.addColumn(FIELD_EMESSAGE_UPDATED, "'" + emessage.getTimestampUpdate().toFormattedString("yyyy-MM-dd HH:mm:ss") + "'");

        // add qualifiers for the EMessageID
        sql.addQualifier(FIELD_EMESSAGE_ID + " = '" + emessage.getMessageID() + "'");

        try
        {
            connection.execute(sql.getSQLString());
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

        if (0 >= connection.getUpdateCount())
        {
            logger.warn( "No emessage updated ");
            throw new DataException(DataException.NO_DATA, "No emessage updated ");
        }
    }
}
