/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcListItemPriceMaintenance.java /main/16 2012/05/21 15:50:17 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreene   05/21/12 - XbranchMerge cgreene_bug-13951397 from
 *                         rgbustores_13.5x_generic
 *    cgreene   05/16/12 - arrange order of businessDay column to end of
 *                         primary key to improve performance since most
 *                         receipt lookups are done without the businessDay
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   01/18/09 - Removed use of column
 *                         FIELD_MAINTENANCE_EVENT_STATUS_CODE since this
 *                         status code is not used any longer.
 *    ohorne 10/08/08 - deprecated methods per I18N Database Technical
 *                      Specification
 *
 * ===========================================================================

  $Log:
   6    360Commerce 1.5         6/20/2006 3:36:48 PM   Brendan W. Farrell Fixed
         unit tests for UDM.
   5    360Commerce 1.4         5/30/2006 10:06:38 AM  Brett J. Larsen CR 18490
         - UDM - eventID type changed to int
   4    360Commerce 1.3         1/25/2006 4:11:09 PM   Brett J. Larsen merge
        7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
   3    360Commerce 1.2         3/31/2005 4:28:38 PM   Robert Pearse   
   2    360Commerce 1.1         3/10/2005 10:22:38 AM  Robert Pearse   
   1    360Commerce 1.0         2/11/2005 12:11:55 PM  Robert Pearse   
  $:
   4    .v700     1.2.1.0     11/16/2005 16:27:21    Jason L. DeLeau 4215: Get
        rid of redundant ArtsDatabaseifc class
   3    360Commerce1.2         3/31/2005 15:28:38     Robert Pearse
   2    360Commerce1.1         3/10/2005 10:22:38     Robert Pearse
   1    360Commerce1.0         2/11/2005 12:11:55     Robert Pearse
  $
  Revision 1.4  2004/08/13 13:53:51  kll
  @scr 0: deprecation fixes

  Revision 1.3  2004/02/12 17:13:14  mcs
  Forcing head revision

  Revision 1.2  2004/02/11 23:25:22  bwf
  @scr 0 Organize imports.

  Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
  updating to pvcs 360store-current

 *    Rev 1.0   Aug 29 2003 15:30:58   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jan 06 2003 09:43:28   adc
 * Integrate domain 5.5 changes
 * Resolution for 1659: Integrate BO related changes from domain 5.5 into domain 6.0
 *
 *    Rev 1.0   Jun 03 2002 16:36:42   msg
 * Initial revision.
 *
 *    Rev 1.2   18 May 2002 17:07:20   sfl
 * Using upper case in LIKE qualifier to handle the matching search situations when database
 * sever to be configured either case-sensitive or case-insensitive.
 * Resolution for POS SCR-1666: Employee - Search by employee name cannot find existing employees
 *
 *    Rev 1.1   16 May 2002 15:02:46   adc
 * db2 fixes
 * Resolution for Domain SCR-50: db2 port fixes
 *
 *    Rev 1.0   Mar 18 2002 12:07:06   msg
 * Initial revision.
 *
 *    Rev 1.2   Mar 04 2002 16:32:46   dcm
 * Fixed select distinct.
 *
 *    Rev 1.1   Feb 11 2002 14:35:08   dcm
 * Added retail store id qualifier
 *
 *    Rev 1.0   Sep 20 2001 15:59:02   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.event.EventConstantsIfc;
import oracle.retail.stores.domain.event.ItemPriceMaintenanceEvent;
import oracle.retail.stores.domain.event.ItemPriceMaintenanceEventIfc;
import oracle.retail.stores.domain.event.PriceMaintenanceSearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * Searches for all the price maintenance events that meet a specific search
 * criteria.
 * 
 * @version $Revision: /main/16 $
 */
public class JdbcListItemPriceMaintenance
    extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -5266500942348536334L;
    /**
     * revision number of this class
     */
    public static String revisionNumber = "$Revision: /main/16 $";

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
        PriceMaintenanceSearchCriteriaIfc criteria = (PriceMaintenanceSearchCriteriaIfc) action.getDataObject();
        ItemPriceMaintenanceEventIfc[] events = listItemPriceMaintenance(connection, criteria);
        dataTransaction.setResult(events);
    }

    /**
     * List all the item price maintenance events based on a search criteria.
     * 
     * @param dataConnection connection to the db
     * @param criteria The specific search criteria
     * @exception DataException upon error
     */
    protected ItemPriceMaintenanceEventIfc[] listItemPriceMaintenance(JdbcDataConnection dataConnection,
                                                                      PriceMaintenanceSearchCriteriaIfc criteria)
        throws DataException
    {
        ItemPriceMaintenanceEventIfc[] events = null;
        try
        {
            SQLSelectStatement sql = buildSelectItemPriceMaintenanceSQL(criteria);
            // SELECT DISTINCT should be done in SQLSelectStatement
            dataConnection.execute("SELECT DISTINCT " + sql.getSQLString().substring(7).trim() + " DESC");

            events = parseItemPriceMaintenanceResultSet(dataConnection);
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "listItemPriceMaintenance", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "listItemPriceMaintenance", e);
        }

        return(events);
    }

    /**
     * Builds SQL statement for searching price maintenance events.
     * 
     * @param criteria The specific search criteria
     * @exception SQLException thrown if error occurs
     * @deprecated as of Release 13.1
     */
    protected SQLSelectStatement buildSelectItemPriceMaintenanceSQL(PriceMaintenanceSearchCriteriaIfc criteria)
        throws SQLException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables
        sql.addTable(TABLE_MAINTENANCE_EVENT);
        if ((criteria.getItemID() != null) && (criteria.getItemID().length() > 0))
        {
            String pt = EventConstantsIfc.EVENT_TYPE_CODE[EventConstantsIfc.EVENT_TYPE_TEMPORARY_PRICE_CHANGE];
            if (criteria.getEventType().equals(pt))
            {
                sql.addTable(TABLE_TEMPORARY_PRICE_CHANGE_ITEM);
            }
            else
            {
                sql.addTable(TABLE_PERMANENT_PRICE_CHANGE_ITEM);
            }
        }

        // add columns
        sql.addColumn(TABLE_MAINTENANCE_EVENT + "." + FIELD_MAINTENANCE_EVENT_EVENT_ID);
        sql.addColumn(TABLE_MAINTENANCE_EVENT + "." + FIELD_MAINTENANCE_EVENT_NAME);
        sql.addColumn(TABLE_MAINTENANCE_EVENT + "." + FIELD_MAINTENANCE_EVENT_EFFECTIVE_DATE);
        sql.addColumn(TABLE_MAINTENANCE_EVENT + "." + FIELD_MAINTENANCE_EVENT_EXPIRATION_DATE);

        // add qualifiers
        if (criteria.getEventID() != EventConstantsIfc.EVENT_ID_UNSPECIFIED)
        {
            sql.addQualifier(TABLE_MAINTENANCE_EVENT + "." + FIELD_MAINTENANCE_EVENT_EVENT_ID +
                             " = " + criteria.getEventID());
        }

        if ((criteria.getStoreID() != null) && !criteria.getStoreID().equals(""))
        {
            sql.addQualifier(TABLE_MAINTENANCE_EVENT + "." + FIELD_RETAIL_STORE_ID +
                             " = '" + criteria.getStoreID() + "'");
        }

        if ((criteria.getEventName() != null) && !criteria.getEventName().equals(""))
        {
            sql.addQualifier("UPPER(" + TABLE_MAINTENANCE_EVENT + "." + FIELD_MAINTENANCE_EVENT_NAME + ")" +
                                 " like " + "UPPER('" + criteria.getEventName() + "%')");
        }

        if ((criteria.getEventType() != null) && !criteria.getEventType().equals(""))
        {
            sql.addQualifier(TABLE_MAINTENANCE_EVENT + "." + FIELD_MAINTENANCE_EVENT_TYPE_CODE +
                                 " = '" + criteria.getEventType() + "'");
        }

        if (criteria.getStartDateAfter() != null)
        {
            sql.addQualifier(TABLE_MAINTENANCE_EVENT + "." + FIELD_MAINTENANCE_EVENT_EFFECTIVE_DATE +
                                 " >= " + dateToSQLTimestampString(criteria.getStartDateAfter()));
        }

        if (criteria.getStartDateBefore() != null)
        {
            sql.addQualifier(TABLE_MAINTENANCE_EVENT + "." + FIELD_MAINTENANCE_EVENT_EFFECTIVE_DATE +
                                 " <= " + dateToSQLTimestampString(criteria.getStartDateBefore()));
        }

        if (criteria.getEndDateAfter() != null)
        {
            sql.addQualifier(TABLE_MAINTENANCE_EVENT + "." + FIELD_MAINTENANCE_EVENT_EXPIRATION_DATE +
                                 " >= " + dateToSQLTimestampString(criteria.getEndDateAfter()));
        }

        if (criteria.getEndDateBefore() != null)
        {
            sql.addQualifier(TABLE_MAINTENANCE_EVENT + "." + FIELD_MAINTENANCE_EVENT_EXPIRATION_DATE +
                                 " <= " + dateToSQLTimestampString(criteria.getEndDateBefore()));
        }

        if ((criteria.getItemID() != null) && (criteria.getItemID().length() > 0))
        {
            String pt = EventConstantsIfc.EVENT_TYPE_CODE[EventConstantsIfc.EVENT_TYPE_TEMPORARY_PRICE_CHANGE];
            if (criteria.getEventType().equals(pt))
            {
                sql.addQualifier(TABLE_MAINTENANCE_EVENT + "." + FIELD_MAINTENANCE_EVENT_EVENT_ID + " = " +
                                 TABLE_TEMPORARY_PRICE_CHANGE_ITEM + "." + FIELD_TEMPORARY_PRICE_CHANGE_ITEM_EVENT_ID);

                sql.addQualifier("UPPER(" + TABLE_TEMPORARY_PRICE_CHANGE_ITEM + "." + FIELD_TEMPORARY_PRICE_CHANGE_ITEM_ITEM_ID + ")" +
                                     " like " + "UPPER('" + criteria.getItemID() + "%')");
            }
            else
            {
                sql.addQualifier(TABLE_MAINTENANCE_EVENT + "." + FIELD_MAINTENANCE_EVENT_EVENT_ID + " = " +
                                 TABLE_PERMANENT_PRICE_CHANGE_ITEM + "." + FIELD_PERMANENT_PRICE_CHANGE_ITEM_EVENT_ID);

                sql.addQualifier("UPPER(" + TABLE_PERMANENT_PRICE_CHANGE_ITEM + "." + FIELD_PERMANENT_PRICE_CHANGE_ITEM_ITEM_ID + ")" +
                                     " like " + "UPPER('" + criteria.getItemID() + "%')");
            }
        }

        sql.addOrdering(TABLE_MAINTENANCE_EVENT + "." + FIELD_MAINTENANCE_EVENT_EFFECTIVE_DATE);


        return(sql);
    }

    /**
     * Parse the result set returned by the query and builds the array of
     * events.
     * 
     * @param dataConnection connection to the db
     * @exception DataException upon error
     * @exception SQLException thrown if error occurs
     */
    protected ItemPriceMaintenanceEventIfc[] parseItemPriceMaintenanceResultSet(DataConnectionIfc dataConnection)
        throws DataException, SQLException
    {
        ArrayList<ItemPriceMaintenanceEvent> result = new ArrayList<ItemPriceMaintenanceEvent>();

        ResultSet rs = (ResultSet) dataConnection.getResult();
        if (rs != null)
        {
            int eventID = EventConstantsIfc.EVENT_ID_UNSPECIFIED;
            String eventName = null;
            Timestamp startTime = null;
            Timestamp endTime = null;
            while (rs.next())
            {
                ItemPriceMaintenanceEvent event = new ItemPriceMaintenanceEvent();

                eventID = rs.getInt(1);
                event.setEventID(eventID);

                eventName = getSafeString(rs, 2);
                event.setName(eventName);

                startTime = rs.getTimestamp(3);
                event.setEffectiveDateTimestamp(timestampToEYSDate(startTime));

                endTime = rs.getTimestamp(4);
                if (endTime != null)
                {
                    event.setExpirationDateTimestamp(timestampToEYSDate(endTime));
                }

                event.setStatus(EventConstantsIfc.EVENT_STATUS_UNKNOWN);

                result.add(event);
            }

            rs.close();
        }

        ItemPriceMaintenanceEventIfc[] events = new ItemPriceMaintenanceEventIfc[result.size()];
        result.toArray(events);
        return(events);
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return(Util.classToStringHeader("JdbcListItemPriceMaintenance",
                                        getRevisionNumber(),
                                        hashCode()).toString());
    }

}

