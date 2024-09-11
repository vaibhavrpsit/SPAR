/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadWorkstationIDsByStore.java /main/13 2013/09/05 10:36:19 abondala Exp $
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
 *    4    360Commerce 1.3         1/25/2006 4:11:19 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:42 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:46 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:00 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:25:46    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:42     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:46     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:00     Robert Pearse
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
 *   Revision 1.3  2004/02/12 17:13:17  mcs
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
 *    Rev 1.0   Aug 29 2003 15:32:20   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   May 15 2003 16:42:46   cdb
 * Added filtering of workstation classification when requested.
 * Resolution for 1930: RE-FACTORING AND FEATURE ENHANCEMENTS TO PARAMETER SUBSYSTEM
 *
 *    Rev 1.0   Jun 03 2002 16:38:46   msg
 * Initial revision.
 *
 *    Rev 1.1   25 Mar 2002 13:12:32   adc
 * Call makeSafeString for store
 * Resolution for Backoffice SCR-735: BackOffice and POS are inconsistant with how Parameters are grouped
 *
 *    Rev 1.0   Mar 18 2002 12:07:58   msg
 * Initial revision.
 *
 *    Rev 1.1   30 Jan 2002 17:29:00   mpb
 * Removed logging statements.
 * Resolution for Backoffice SCR-150: ParameterManagement
 *
 *    Rev 1.0   30 Jan 2002 17:25:08   mpb
 * Initial revision.
 * Resolution for Backoffice SCR-150: ParameterManagement
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
    This operation reads all of the registers for a store.
    <P>
    @version $Revision: /main/13 $
**/
public class JdbcReadWorkstationIDsByStore extends JdbcDataOperation
                                           implements ARTSDatabaseIfc
{
    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadWorkstationIDsByStore.class);

    /**
       Class constructor.
     */
    public JdbcReadWorkstationIDsByStore()
    {
        super();
        setName("JdbcReadWorkstationIDsByStore");
    }

    /**
       Executes the SQL statements against the database.
       <P>
       @param  dataTransaction     The data transaction
       @param  dataConnection      The connection to the data source
       @param  action              The information passed by the valet
       @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadWorkstationIDsByStore.execute()");

        /*
         * getUpdateCount() is about the only thing outside of
         * DataConnectionIfc that we need.
         */
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        Vector workstationIDs = null;

        // Navigate the input object
        String store = "";
        Object dataObject = action.getDataObject();
        if (dataObject instanceof String)
        {
            store = (String)dataObject;
            workstationIDs = readWorkstationIDs(connection, store);
        }
        else
        {
            WorkstationIfc workstation = (WorkstationIfc)dataObject;
            store = workstation.getStoreID();
            String workstationClassification = workstation.getWorkstationClassification();
            workstationIDs = readWorkstationIDs(connection, store, workstationClassification);
        }

        /*
         * Send back the result
         */
        dataTransaction.setResult(workstationIDs);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadWorkstationIDsByStore.execute()");
    }

    /**
       Returns the registers for a store.
       <P>
       @param  dataConnection  connection to the db
       @param  store           the store
       @return vector of registers
       @exception DataException upon error
     */
    public Vector readWorkstationIDs(JdbcDataConnection dataConnection,
                                     String store)
        throws DataException
    {
        return readWorkstationIDs(dataConnection, store, null);
    }

    /**
       Returns the registers for a store.
       <P>
       @param  dataConnection  connection to the db
       @param  store           the store
       @param  classification  the workstation classification
       @return vector of registers
       @exception DataException upon error
     */
    public Vector readWorkstationIDs(JdbcDataConnection dataConnection,
                                     String store,
                                     String classification)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Add the desired tables (and aliases)
         */
        sql.addTable(TABLE_WORKSTATION, ALIAS_WORKSTATION);

        /*
         * Add desired columns
         */
        sql.addColumn(FIELD_WORKSTATION_ID);

        /*
         * Add Qualifier(s)
         */

        // For the specified workstation only
        sql.addQualifier(ALIAS_WORKSTATION + "." + FIELD_RETAIL_STORE_ID
                         + " = " + makeSafeString(store));
        if (!Util.isEmpty(classification))
        {
            sql.addQualifier(ALIAS_WORKSTATION + "." + FIELD_WORKSTATION_CLASSIFICATION
                             + " = " + makeSafeString(classification));
        }

        Vector workstationVector = new Vector(4);
        try
        {

            dataConnection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet)dataConnection.getResult();

            while (rs.next())
            {
                /*
                 * Grab the fields selected from the database
                 */
                String workstationID = getSafeString(rs, 1);

                workstationVector.addElement(workstationID);
            }

            rs.close();

            if (workstationVector.isEmpty())
            {
                throw new DataException(NO_DATA, "readRegisters");
            }
        }
        catch (SQLException se)
        {
            throw new DataException(SQL_ERROR, "readRegisters", se);
        }
        catch (DataException de)
        {
            logger.warn(de);
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(UNKNOWN, "readRegisters", e);
        }

        return(workstationVector);
    }

}
