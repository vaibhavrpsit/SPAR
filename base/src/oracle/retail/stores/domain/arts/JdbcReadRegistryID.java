/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadRegistryID.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:55 mszekely Exp $
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
 *    5    360Commerce 1.4         5/12/2006 5:26:28 PM   Charles D. Baker
 *         Merging with v1_0_0_53 of Returns Managament
 *    4    360Commerce 1.3         1/25/2006 4:11:17 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:45 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:59 PM  Robert Pearse   
 *:
 *    5    .v700     1.2.1.1     11/17/2005 16:10:44    Jason L. DeLeau 4345:
 *         Replace any uses of Gateway.log() with the log4j.
 *    4    .v700     1.2.1.0     11/16/2005 16:27:17    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:41     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:45     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:59     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:44  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:35  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:45  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:21  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:04   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:37:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:47:42   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:07:40   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:57:40   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation reads the gift registered item table.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcReadRegistryID extends JdbcDataOperation implements ARTSDatabaseIfc
{
    /**
     * 
     */
    private static final long serialVersionUID = -3602785245442376539L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadRegistryID.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Executes the SQL statements againts the database.
     * 
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    public void execute (DataTransactionIfc dataTransaction,
                         DataConnectionIfc dataConnection,
                         DataActionIfc action)
                         throws DataException
    {
    	if (logger.isDebugEnabled()) logger.debug("Entering JdbcReadRegistryID.execute()");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        String registryID = (String) action.getDataObject();
        RegistryIDIfc retrievedRegistryID = null;

        try
        {
             retrievedRegistryID = readRegistryID(connection, registryID);
        }
        catch (DataException de)
        {
            throw de;
        }
        dataTransaction.setResult(retrievedRegistryID);
        if (logger.isDebugEnabled()) logger.debug("Exiting JdbcReadRegistryID.execute()");
    }

    /**
       Checks the existence of register ID based on registryID string.
       <P>
       @param  dataConnection  connection to the db
       @param  registryID      the gift registryID
       @return true if successful
       @exception DataException upon error
     */
    public RegistryIDIfc readRegistryID(JdbcDataConnection dataConnection,
                                    String registryID)
        throws DataException
    {
        RegistryIDIfc retrievedRegistryID = null;
        try
        {
            SQLSelectStatement sql = new SQLSelectStatement();

            /*
             * Define table
             */
            sql.setTable(TABLE_GIFT_REGISTERED_ITEM);

            /*
             * Add columns and their values
             */
            sql.addColumn(FIELD_GIFT_REGISTRY_ID);

            /*
             * Add Qualifier(s)
             */

            sql.addQualifier(TABLE_GIFT_REGISTERED_ITEM + "." + FIELD_GIFT_REGISTRY_ID + " = '" + registryID + "'");

            dataConnection.execute(sql.getSQLString());
            retrievedRegistryID = parseResultSet(dataConnection);
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "readRegistryID", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readRegistryID", e);
        }

        return(retrievedRegistryID);
    }

    protected RegistryIDIfc parseResultSet(JdbcDataConnection dataConnection)
                              throws SQLException, DataException
    {
        RegistryIDIfc retrievedRegistryID = null;
        ResultSet rs = (ResultSet) dataConnection.getResult();
        int recordsFound = 0;

        if (rs != null && rs.next())
        {
            retrievedRegistryID = DomainGateway.getFactory().getRegistryIDInstance();
            retrievedRegistryID.setID(getSafeString(rs, ++recordsFound));
            rs.close();
        }

        return(retrievedRegistryID);
    }

    /**
       Returns a string representation of this object.
       <P>
       @param none
       @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = new String("Class:  JdbcReadRegistryID (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }

    /**
       Returns the revision number of the class.
       <P>
       @param none
       @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return(revisionNumber);
    }

}
