/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcCreatePriceChange.java /main/16 2012/05/21 15:50:17 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/21/12 - XbranchMerge cgreene_bug-13951397 from
 *                         rgbustores_13.5x_generic
 *    cgreene   05/16/12 - arrange order of businessDay column to end of
 *                         primary key to improve performance since most
 *                         receipt lookups are done without the businessDay
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
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
 *    6    360Commerce 1.5         4/12/2008 5:42:56 PM   Christian Greene
 *         31304 Updatet to us "AMT", "PCT" and "AMTREPL" at codes for
 *         promotion types.
 *    5    360Commerce 1.4         5/30/2006 10:06:02 AM  Brett J. Larsen CR
 *         18490 - UDM - eventID type changed to int
 *    4    360Commerce 1.3         1/25/2006 4:11:07 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:36 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:36 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:53 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:34    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:36     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:36     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:53     Robert Pearse
 *
 *   Revision 1.4  2004/08/12 12:33:11  kll
 *   @scr 0: import 360common SQLInsertStatement
 *
 *   Revision 1.3  2004/02/12 17:13:13  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:25  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:30:24   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:35:46   msg
 * Initial revision.
 *
 *    Rev 1.2   16 May 2002 14:41:28   adc
 * Db2 fixes
 * Resolution for Domain SCR-50: db2 port fixes
 *
 *    Rev 1.1   Mar 18 2002 22:46:32   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:06:44   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:58:02   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.event.EventConstantsIfc;
import oracle.retail.stores.domain.event.ItemPriceMaintenanceEventIfc;
import oracle.retail.stores.domain.event.PriceChangeConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * Creates the audit data for an immediate price cahnge.
 * 
 * @version $Revision: /main/16 $
 */
public class JdbcCreatePriceChange extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 7223129045754362159L;
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
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
        ItemPriceMaintenanceEventIfc priceEvent = (ItemPriceMaintenanceEventIfc) action.getDataObject();
        createPriceChange(connection, priceEvent);
    }

    /**
     * Creates the audit data for an immediat price change. An immediate price
     * change is considered a permanent price change effective immediatelly.
     * 
     * @param dataConnection connection to the db
     * @param priceEvent The price event object
     * @exception DataException upon error
     */
    protected void createPriceChange(JdbcDataConnection dataConnection,
                                              ItemPriceMaintenanceEventIfc priceEvent)
        throws DataException
    {
        try
        {
            // create PermanentPriceChange table record
            SQLInsertStatement sql = buildInsertPriceChangeSQL(priceEvent);
            dataConnection.execute(sql.getSQLString());
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "createPriceChange", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "createPriceChange", e);
        }
    }

    /**
     * Builds SQL statement for creating the Event table record.
     * 
     * @param priceEvent The price event object
     * @exception SQLException thrown if error occurs
     */
    protected SQLInsertStatement buildInsertPriceChangeSQL(ItemPriceMaintenanceEventIfc priceEvent)
        throws SQLException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // add tables
        if (priceEvent.getTypeCode() == EventConstantsIfc.EVENT_TYPE_TEMPORARY_PRICE_CHANGE)
        {
            sql.setTable(TABLE_TEMPORARY_PRICE_CHANGE);
        }
        else
        {
            sql.setTable(TABLE_PERMANENT_PRICE_CHANGE);
        }

        // add columns
        sql.addColumn(FIELD_PERMANENT_PRICE_CHANGE_EVENT_ID,
                    Integer.toString(priceEvent.getEventID()));
        sql.addColumn(FIELD_RETAIL_STORE_ID,
                    makeSafeString(priceEvent.getStore().getStoreID()));

        if (priceEvent.getTypeCode() == EventConstantsIfc.EVENT_TYPE_TEMPORARY_PRICE_CHANGE)
        {
            sql.addColumn(FIELD_TEMPORARY_PRICE_CHANGE_SALE_UNIT_AMOUNT_TYPE_CODE,
                    makeSafeString(PriceChangeConstantsIfc.APPLICATION_CODE[priceEvent.getApplicationCode()]));
            sql.addColumn(FIELD_TEMPORARY_PRICE_CHANGE_SALE_UNIT_AMOUNT, priceEvent.getSaleUnitAmount().toString());
        }
        else
        {
            sql.addColumn(FIELD_PERMANENT_PRICE_CHANGE_SALE_UNIT_AMOUNT_TYPE_CODE,
                    makeSafeString(PriceChangeConstantsIfc.APPLICATION_CODE[priceEvent.getApplicationCode()]));
            sql.addColumn(FIELD_PERMANENT_PRICE_CHANGE_SALE_UNIT_AMOUNT, priceEvent.getSaleUnitAmount().toString());
        }


        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

        return(sql);
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
        return (Util.classToStringHeader("JdbcReadCodeListMap", getRevisionNumber(), hashCode()).toString());
    }

}
