/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcUpdateMaintenanceEvent.java /main/16 2012/05/21 15:50:20 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/21/12 - XbranchMerge cgreene_bug-13951397 from
 *                      rgbustores_13.5x_generic
 *    cgreen 05/16/12 - arrange order of businessDay column to end of primary
 *                      key to improve performance since most receipt lookups
 *                      are done without the businessDay
 *    cgreen 05/28/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech75 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                      SQLException to DataException
 *    abonda 01/03/10 - update header date
 *    ohorne 10/08/08 - deprecated methods per I18N Database Technical
 *                      Specification
 *
 * ===========================================================================

     $Log:
      5    360Commerce 1.4         5/30/2006 10:11:43 AM  Brett J. Larsen CR
           18490 - UDM - eventID type changed to int
      4    360Commerce 1.3         1/25/2006 4:11:26 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      3    360Commerce 1.2         3/31/2005 4:28:46 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:22:52 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:12:06 PM  Robert Pearse   
     $:
      4    .v700     1.2.1.0     11/16/2005 16:26:22    Jason L. DeLeau 4215:
           Get rid of redundant ArtsDatabaseifc class
      3    360Commerce1.2         3/31/2005 15:28:46     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:22:52     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:12:06     Robert Pearse
     $
     Revision 1.3  2004/02/12 17:13:19  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:22  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:33:26   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jan 06 2003 09:43:26   adc
 * Integrate domain 5.5 changes
 * Resolution for 1659: Integrate BO related changes from domain 5.5 into domain 6.0
 *
 *    Rev 1.0   Jun 03 2002 16:41:12   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:49:48   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:09:22   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:56:44   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:44   msg
 * header update
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.domain.arts;

import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.event.MaintenanceEventIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * Creates a record in the Event table.
 * 
 * @version $Revision: /main/16 $
 */
public class JdbcUpdateMaintenanceEvent extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -6415391189311916503L;
    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/16 $";

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
        MaintenanceEventIfc maintenanceEvent = (MaintenanceEventIfc) action.getDataObject();
        updateMaintenanceEvent(connection, maintenanceEvent);
    }

    /**
     * Creates a record in the Event table.
     * 
     * @param dataConnection connection to the db
     * @param event The event object
     * @exception DataException upon error
     */
    protected void updateMaintenanceEvent(JdbcDataConnection dataConnection, MaintenanceEventIfc maintenanceEvent)
            throws DataException
    {
        try
        {
            SQLUpdateStatement sql = buildUpdateMaintenanceEventSQL(maintenanceEvent);
            dataConnection.execute(sql.getSQLString());
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "updateMaintenanceEvent", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "updateMaintenanceEvent", e);
        }
    }

    /**
     * Builds SQL statement for creating the Event table record.
     * 
     * @param priceEvent The price event object
     * @exception SQLException thrown if error occurs
     * @deprecated As of release 13.1
     */
    protected SQLUpdateStatement buildUpdateMaintenanceEventSQL(MaintenanceEventIfc maintenanceEvent)
            throws SQLException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // add tables
        sql.setTable(TABLE_MAINTENANCE_EVENT);

        // add columns and their values
        sql.addColumn(FIELD_MAINTENANCE_EVENT_DESCRIPTION, makeSafeString(maintenanceEvent.getDescription()));
        sql.addColumn(FIELD_MAINTENANCE_EVENT_NAME, makeSafeString(maintenanceEvent.getName()));
        sql.addColumn(FIELD_MAINTENANCE_EVENT_EFFECTIVE_DATE, dateToSQLTimestampString(maintenanceEvent
                .getEffectiveDateTimestamp()));
        sql.addColumn(FIELD_MAINTENANCE_EVENT_EXPIRATION_DATE, dateToSQLTimestampString(maintenanceEvent
                .getExpirationDateTimestamp()));
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

        // add qualifiers
        sql.addQualifier(FIELD_MAINTENANCE_EVENT_EVENT_ID + " = " + Integer.toString(maintenanceEvent.getEventID()));
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = "
                + makeSafeString(maintenanceEvent.getStore().getStoreID()));

        return (sql);

    }

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    protected String getRevisionNumber()
    {
        return (revisionNumber);
    }

    /**
     * Returns the string representation of this object.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        return (Util.classToStringHeader("JdbcUpdateMaintenanceEvent", getRevisionNumber(), hashCode()).toString());
    }
}
