/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcCreateEvent.java /main/15 2012/05/21 15:50:17 cgreene Exp $
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
 *    npoola 08/25/10 - passed the connection object to the IdentifierService
 *                      getNextID method to use right connection
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech75 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                      SQLException to DataException
 *    abonda 01/03/10 - update header date
 *    ohorne 10/08/08 - deprecated methods per I18N Database Technical
 *                      Specification
 *    ohorne 10/07/08 - Deprecated unused classes
 *
 * ===========================================================================

     $Log:
      8    360Commerce 1.7         6/1/2006 12:49:56 PM   Charles D. Baker
           Remove unused imports
      7    360Commerce 1.6         6/1/2006 12:28:42 PM   Brendan W. Farrell
           Update comments.
      6    360Commerce 1.5         5/31/2006 5:04:00 PM   Brendan W. Farrell
           Move from party to id gen.
           
      5    360Commerce 1.4         5/30/2006 10:01:08 AM  Brett J. Larsen CR
           18490 - UDM - eventID type changed to int
      4    360Commerce 1.3         1/25/2006 4:11:06 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      3    360Commerce 1.2         3/31/2005 4:28:36 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:22:35 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:11:53 PM  Robert Pearse   
     $:
      4    .v700     1.2.1.0     11/16/2005 16:25:49    Jason L. DeLeau 4215:
           Get rid of redundant ArtsDatabaseifc class
      3    360Commerce1.2         3/31/2005 15:28:36     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:22:35     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:11:53     Robert Pearse
     $
     Revision 1.4  2004/08/12 12:53:01  kll
     @scr 0: deprecation fixes

     Revision 1.3  2004/02/12 17:13:13  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:27  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:30:18   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jan 06 2003 09:43:30   adc
 * Integrate domain 5.5 changes
 * Resolution for 1659: Integrate BO related changes from domain 5.5 into domain 6.0
 *
 *    Rev 1.0   Jun 03 2002 16:35:32   msg
 * Initial revision.
 *
 *    Rev 1.2   16 May 2002 14:41:10   adc
 * Db2 fixes
 * Resolution for Domain SCR-50: db2 port fixes
 *
 *    Rev 1.1   Mar 18 2002 22:46:26   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:06:36   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:57:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:52   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.domain.arts;

import java.sql.SQLException;

import oracle.retail.stores.common.identifier.IdentifierConstantsIfc;
import oracle.retail.stores.common.identifier.IdentifierServiceIfc;
import oracle.retail.stores.common.identifier.IdentifierServiceLocator;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.event.EventConstantsIfc;
import oracle.retail.stores.domain.event.EventIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

/**
 * Creates a record in the Event table.
 * 
 * @version $Revision: /main/15 $
 */
public class JdbcCreateEvent extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 5947908861577257370L;

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

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
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
        EventIfc event = (EventIfc) action.getDataObject();
        int eventID = createEvent(connection, event);
        dataTransaction.setResult(Integer.toString(eventID));
    }

    /**
        Creates a record in the Event table. <P>
        @param dataConnection  connection to the db
        @param event The event object
        @exception DataException upon error
     */
    protected int createEvent(JdbcDataConnection dataConnection,
                                 EventIfc event)
        throws DataException
    {
        int eventID = EventConstantsIfc.EVENT_ID_UNSPECIFIED;
        try
        {
            eventID = generateEventID(dataConnection);
            event.setEventID(eventID);
            SQLInsertStatement sql = buildInsertEventSQL(event);
            dataConnection.execute(sql.getSQLString());
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "createEvent", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "createEvent", e);
        }
        return(eventID);
    }

    /**
        Builds SQL statement for creating the Event table record. <P>
        @param priceEvent The price event object
        @exception SQLException thrown if error occurs
        @deprecated As of release 13.1
     */
    protected SQLInsertStatement buildInsertEventSQL(EventIfc event)
        throws SQLException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // add tables
        sql.setTable(TABLE_EVENT);

                // add columns
        sql.addColumn(FIELD_EVENT_EVENT_ID, Integer.toString(event.getEventID()));
        sql.addColumn(FIELD_RETAIL_STORE_ID, makeSafeString(event.getStore().getStoreID()));
        sql.addColumn(FIELD_EVENT_DESCRIPTION, makeSafeString( event.getDescription()));
        sql.addColumn(FIELD_EVENT_NAME, makeSafeString(event.getName()));
        sql.addColumn(FIELD_EVENT_STATUS_CODE, "'PENDING'");
        sql.addColumn(FIELD_EVENT_TYPE_CODE,
                      makeSafeString(EventConstantsIfc.EVENT_TYPE_CODE[event.getTypeCode()]));
        sql.addColumn(FIELD_EVENT_PLAN_START_DATE,
                      dateToSQLTimestampString(event.getPlannedStartTimestamp()));
        sql.addColumn(FIELD_EVENT_PLAN_END_DATE,
                      dateToSQLTimestampString(event.getPlannedEndTimestamp()));
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

        return(sql);

    }

    /**
        Generates a unique event id.  Uses {@link IdentifierServiceIfc}
        
        @param dataConnection The connection to the data source
        @return the event ID
        @exception DataException upon error
     */
    protected int generateEventID(DataConnectionIfc dataConnection)
        throws DataException
    {
        return IdentifierServiceLocator.getIdentifierService().getNextID(((JdbcDataConnection) dataConnection).getConnection(), IdentifierConstantsIfc.COUNTER_EVENT);
    }

    /**
        Retrieves the source-code-control system revision number. <P>
        @return String representation of revision number
     */
    protected String getRevisionNumber()
    {
        return(revisionNumber);
    }

    /**
       Returns the string representation of this object.
       @return String representation of object
     */
    @Override
    public String toString()
    {
        return(Util.classToStringHeader("JdbcReadCodeListMap",
                                        getRevisionNumber(),
                                        hashCode()).toString());
    }

}

