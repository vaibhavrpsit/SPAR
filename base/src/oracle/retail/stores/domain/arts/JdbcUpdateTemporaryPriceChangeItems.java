/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcUpdateTemporaryPriceChangeItems.java /main/15 2012/05/21 15:50:20 cgreene Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:46 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:53 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:06 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:33:44   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:41:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:50:16   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:09:44   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 15:56:34   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:33:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.SQLException;
import java.util.List;

import oracle.retail.stores.common.sql.SQLDeleteStatement;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.event.ItemPriceMaintenanceEventIfc;
import oracle.retail.stores.domain.event.PriceChangeIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * Updates the data for a temporary or a permanent price change.
 * 
 * @version $Revision: /main/15 $
 */
public class JdbcUpdateTemporaryPriceChangeItems extends JdbcUpdatePriceChangeItems
{
    private static final long serialVersionUID = 6819246605178695719L;
    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * Builds SQL statement for creating the Event table record.
     * 
     * @param priceEvent The price event object
     * @exception SQLException thrown if error occurs
     */
    protected SQLSelectStatement buildSelectPriceChangeItemsSQL(ItemPriceMaintenanceEventIfc searchObj)
        throws SQLException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables
        sql.addTable(TABLE_TEMPORARY_PRICE_CHANGE_ITEM);

        // add columns
        sql.addColumn(FIELD_TEMPORARY_PRICE_CHANGE_ITEM_ITEM_ID);

        // add qualifier
        sql.addQualifier(FIELD_TEMPORARY_PRICE_CHANGE_ITEM_EVENT_ID +
                         " = '" + searchObj.getEventID() + "'");
        sql.addQualifier(FIELD_RETAIL_STORE_ID +
                         " = '" + searchObj.getStore().getStoreID() + "'");

        return(sql);
    }

    /**
     * Builds SQL statement for deleting items.
     * 
     * @param itemPriceMaintenance The price event object
     * @param deleteIDs array of the item IDs marked for deletion
     * @exception SQLException thrown if error occurs
     */
    public SQLDeleteStatement
        buildDeletePriceChangeItemsSQL(ItemPriceMaintenanceEventIfc itemPriceMaintenance,
                                       List<String> deleteIDs)
    {
        SQLDeleteStatement sql = new SQLDeleteStatement();

        // table
        sql.setTable(TABLE_TEMPORARY_PRICE_CHANGE_ITEM);

        // add qualifier
        sql.addQualifier(FIELD_TEMPORARY_PRICE_CHANGE_ITEM_EVENT_ID +
                         " = '" + itemPriceMaintenance.getEventID() + "'");
        sql.addQualifier(FIELD_RETAIL_STORE_ID +
                         " = '" + itemPriceMaintenance.getStore().getStoreID() + "'");
        sql.addQualifier(FIELD_TEMPORARY_PRICE_CHANGE_ITEM_ITEM_ID +
                         " IN (" + getItemIDs(deleteIDs) + ")");

        return(sql);
    }

    /**
     * Builds SQL statement for creating an item.
     * 
     * @param itemPriceMaintenance The price event object
     * @param itemID the ID of the item to be created
     * @exception SQLException thrown if error occurs
     */
    public SQLInsertStatement
        buildCreatePriceChangeItemsSQL(ItemPriceMaintenanceEventIfc itemPriceMaintenance,
                                       String itemID)
    {
        SQLInsertStatement sql = new SQLInsertStatement();
        PriceChangeIfc priceChangeItem = itemPriceMaintenance.findItem(itemID);
        
        // add tables
        sql.setTable(TABLE_TEMPORARY_PRICE_CHANGE_ITEM);

        // add columns
        sql.addColumn(FIELD_TEMPORARY_PRICE_CHANGE_ITEM_EVENT_ID, "'" + itemPriceMaintenance.getEventID() + "'");
        sql.addColumn(FIELD_RETAIL_STORE_ID, "'" + itemPriceMaintenance.getStore().getStoreID() + "'");
        sql.addColumn(FIELD_TEMPORARY_PRICE_CHANGE_ITEM_ITEM_ID, "'" + itemID + "'");
        if (priceChangeItem.getOverridePriceAmount() != null)
        {
            sql.addColumn(FIELD_TEMPORARY_PRICE_CHANGE_ITEM_PRICE_OVERRIDE_AMOUNT, priceChangeItem.getOverridePriceAmount().getStringValue());
        }
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

        return(sql);
    }

    /**
     * Builds SQL statement for updating an item.
     * 
     * @param itemPriceMaintenance The price event object
     * @param itemID the ID of the item to be updated exception SQLException
     *            thrown if error occurs
     */
    public SQLUpdateStatement
        buildUpdatePriceChangeItemsSQL(ItemPriceMaintenanceEventIfc itemPriceMaintenance,
                                       String itemID)
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();
        PriceChangeIfc priceChangeItem = itemPriceMaintenance.findItem(itemID);
        
        // add tables
        sql.setTable(TABLE_TEMPORARY_PRICE_CHANGE_ITEM);

        // add columns
        if (priceChangeItem.getOverridePriceAmount() != null)
        {
            sql.addColumn(FIELD_TEMPORARY_PRICE_CHANGE_ITEM_PRICE_OVERRIDE_AMOUNT, priceChangeItem.getOverridePriceAmount().getStringValue());
        }
        else
        {
            sql.addColumn(FIELD_TEMPORARY_PRICE_CHANGE_ITEM_PRICE_OVERRIDE_AMOUNT, null);
        }
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

        // add qualifiers
        sql.addQualifier(FIELD_TEMPORARY_PRICE_CHANGE_EVENT_ID + " = '" + itemPriceMaintenance.getEventID() + "'");
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = '" + itemPriceMaintenance.getStore().getStoreID() + "'");
        sql.addQualifier(FIELD_TEMPORARY_PRICE_CHANGE_ITEM_ITEM_ID + " = '" + itemID + "'");

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
        return (Util.classToStringHeader("JdbcUpdateTemporaryPriceChangeItems", getRevisionNumber(), hashCode())
                .toString());
    }

}
