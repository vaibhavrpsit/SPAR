/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcUpdatePriceChange.java /main/15 2012/05/21 15:50:20 cgreene Exp $
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
 *    4    360Commerce 1.3         1/25/2006 4:11:27 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:46 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:53 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:06 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:26:46    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:46     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:53     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:06     Robert Pearse
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:24  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:33:30   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:41:18   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:49:56   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:09:28   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:56:42   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:42   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;


import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.common.sql.SQLUpdateStatement;
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
 * Updates the data for a temporary or a permanent price change.
 * 
 * @version $Revision: /main/15 $
 */
public class JdbcUpdatePriceChange extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 5084832597488301934L;
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
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
        ItemPriceMaintenanceEventIfc itemPriceMaintenance = (ItemPriceMaintenanceEventIfc) action.getDataObject();
        updatePriceChange(connection, itemPriceMaintenance);
    }

    /**
     * Updates the data for a temporary or a permanent price change.
     * 
     * @param dataConnection connection to the db
     * @param priceEvent The price event object
     * @exception DataException upon error
     */
    protected void updatePriceChange(JdbcDataConnection dataConnection,
                                     ItemPriceMaintenanceEventIfc itemPriceMaintenance)
        throws DataException
    {
        try
        {
            SQLUpdateStatement sql = buildUpdatePriceChangeSQL(itemPriceMaintenance);
            dataConnection.execute(sql.getSQLString());
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "updatePriceChange", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "updatePriceChange", e);
        }
    }

    /**
     * Builds SQL statement for updating the database.
     * 
     * @param itemPriceMaintenance The price event object
     * @exception SQLException thrown if error occurs
     */
    protected SQLUpdateStatement buildUpdatePriceChangeSQL(ItemPriceMaintenanceEventIfc itemPriceMaintenance)
        throws SQLException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // add tables
        if (itemPriceMaintenance.getTypeCode() == EventConstantsIfc.EVENT_TYPE_TEMPORARY_PRICE_CHANGE)
        {
            sql.setTable(TABLE_TEMPORARY_PRICE_CHANGE);
        }
        else
        {
            sql.setTable(TABLE_PERMANENT_PRICE_CHANGE);
        }

        // add columns
        if (itemPriceMaintenance.getTypeCode() == EventConstantsIfc.EVENT_TYPE_TEMPORARY_PRICE_CHANGE)
        {
            sql.addColumn(FIELD_TEMPORARY_PRICE_CHANGE_SALE_UNIT_AMOUNT_TYPE_CODE,
                          "'" + PriceChangeConstantsIfc.APPLICATION_CODE[itemPriceMaintenance.getApplicationCode()] + "'");
            sql.addColumn(FIELD_TEMPORARY_PRICE_CHANGE_SALE_UNIT_AMOUNT, itemPriceMaintenance.getSaleUnitAmount().toString());
        }
        else
        {
            sql.addColumn(FIELD_PERMANENT_PRICE_CHANGE_SALE_UNIT_AMOUNT_TYPE_CODE,
                          "'" + PriceChangeConstantsIfc.APPLICATION_CODE[itemPriceMaintenance.getApplicationCode()] + "'");
            sql.addColumn(FIELD_PERMANENT_PRICE_CHANGE_SALE_UNIT_AMOUNT, itemPriceMaintenance.getSaleUnitAmount().toString());
        }


        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

        // add qualifiers
        if (itemPriceMaintenance.getTypeCode() == EventConstantsIfc.EVENT_TYPE_TEMPORARY_PRICE_CHANGE)
        {
            sql.addQualifier(FIELD_PERMANENT_PRICE_CHANGE_EVENT_ID + " = '" + itemPriceMaintenance.getEventID() + "'");
            sql.addQualifier(FIELD_RETAIL_STORE_ID + " = '" + itemPriceMaintenance.getStore().getStoreID() + "'");
        }
        else
        {
            sql.addQualifier(FIELD_TEMPORARY_PRICE_CHANGE_EVENT_ID + " = '" + itemPriceMaintenance.getEventID() + "'");
            sql.addQualifier(FIELD_RETAIL_STORE_ID + " = '" + itemPriceMaintenance.getStore().getStoreID() + "'");
        }

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
        return (Util.classToStringHeader("JdbcUpdatePriceChange", getRevisionNumber(), hashCode()).toString());
    }

}
