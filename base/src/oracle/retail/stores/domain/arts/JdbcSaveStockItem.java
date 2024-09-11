/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveStockItem.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:06 mszekely Exp $
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
 *    6    360Commerce 1.5         8/29/2006 6:31:36 PM   Brett J. Larsen CR
 *         20917 - remove stock item return disposition codes (aka inventory
 *         reason codes)
 *
 *         part of inventory feature which is no longer supported
 *    5    360Commerce 1.4         4/27/2006 7:26:59 PM   Brett J. Larsen CR
 *         17307 - remove inventory functionality - stage 2
 *    4    360Commerce 1.3         1/25/2006 4:11:24 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:44 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:49 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:04 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:26:04    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:44     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:49     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:04     Robert Pearse
 *
 *   Revision 1.10  2004/09/01 20:34:20  mweis
 *   @scr 7012 Inventory updates for POS in the Returns arena.
 *
 *   Revision 1.9  2004/08/30 17:17:15  mweis
 *   @scr 7012 Refactor POS inventory database methods calls to be more descriptive: blahInventoryLocation()
 *
 *   Revision 1.8  2004/08/27 18:34:33  mweis
 *   @scr 7021 Make AS_ITM_STK table's FL_INV_LOC_PRMT column be a '0' or '1' flag, like it should have been.
 *
 *   Revision 1.7  2004/06/29 21:58:58  aachinfiev
 *   Merge the changes for inventory & POS integration
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:46  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:58   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jun 07 2002 17:46:04   cdb
 * Updated to save code disposition in Back Office.
 * Resolution for Backoffice SCR-1098: Add return/disposition code to Item Maintenance
 *
 *    Rev 1.0   Jun 03 2002 16:40:10   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:48:44   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:08:32   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:57:02   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:00   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdatableStatementIfc;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.stock.ItemClassificationIfc;
import oracle.retail.stores.domain.stock.StockItemIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This operation updates the stock item table from the StockItemIfc object.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 * @see oracle.retail.stores.domain.arts.ItemDataTransaction
 * @see oracle.retail.stores.domain.stock.StockItemIfc
 */
public abstract class JdbcSaveStockItem extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -1588088563717312927L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveStockItem.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Perform stock item update.
     * 
     * @param dataConnection JdbcDataConnection
     * @param item StockItemIfc reference
     * @exception DataException thrown if error occurs
     */
    public void updateStockItem(JdbcDataConnection dataConnection,
                                StockItemIfc item)
                                throws DataException
    {
        // build sql statement
        SQLUpdateStatement sql = new SQLUpdateStatement();
        // add table, columns, qualifiers
        sql.setTable(TABLE_STOCK_ITEM);
        addUpdateColumns(item,
                         sql);
        addUpdateQualifiers(item,
                            sql);
        // execute statement
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(
                         de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error(
                         e.toString());
            throw new DataException(DataException.UNKNOWN,
                                    "StockItem update",
                                    e);
        }

    }

    /**
        Perform stock item insert. <P>
        @param dataConnection JdbcDataConnection
        @param item StockItemIfc reference
        @exception DataException thrown if error occurs
     */
    public void insertStockItem(JdbcDataConnection dataConnection,
                                StockItemIfc item)
                                throws DataException
    {
        // build sql statement
        SQLInsertStatement sql = new SQLInsertStatement();
        // add table, columns, qualifiers
        sql.setTable(TABLE_STOCK_ITEM);
        addInsertColumns(item,
                         sql);
        // execute statement
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(
                         de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error(
                         e.toString());
            throw new DataException(DataException.UNKNOWN,
                                    "StockItem insert",
                                    e);
        }

    }

    /**
        Add update columns. <P>
        @param StockItemIfc item object
        @param sql SQLUpdateStatement
     */
    public void addUpdateColumns(StockItemIfc item,
                                 SQLUpdatableStatementIfc sql)
    {
        ItemClassificationIfc classification = item.getItemClassification();
        // add columns
        sql.addColumn(FIELD_STOCK_ITEM_SALE_UNIT_OF_MEASURE_CODE,
                      makeSafeString(item.getUnitOfMeasure().getUnitID()));
        sql.addColumn(FIELD_SERIALIZED_ITEM_VALIDATION_FLAG,
                      makeStringFromBoolean(classification.getSerializedItem()));
        sql.addColumn(FIELD_STOCK_ITEM_RESTOCKING_FEE_FLAG,
                      makeStringFromBoolean(classification.getRestockingFeeFlag()));
        sql.addColumn(FIELD_COLOR_CODE,
                      makeSafeString(item.getItemColor().getIdentifier()));
        sql.addColumn(FIELD_SIZE_CODE,
                      makeSafeString(item.getItemSize().getIdentifier()));
        sql.addColumn(FIELD_STYLE_CODE,
                      makeSafeString(item.getItemStyle().getIdentifier()));
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
    }

    /**
        Add update columns. <P>
        @param StockItemIfc item object
        @param sql SQLUpdateStatement
     */
    public void addInsertColumns(StockItemIfc item,
                                 SQLUpdatableStatementIfc sql)
    {
        // add columns
        sql.addColumn(FIELD_ITEM_ID,
                      makeSafeString(item.getItemID()));
        addUpdateColumns(item, sql);
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
    }

    /**
        Adds update qualifier columns to SQL statement. <P>
        @param StockItemIfc item object
        @param sql SQLUpdateStatement
     */
    public void addUpdateQualifiers(StockItemIfc item,
                                    SQLUpdateStatement sql)
    {
        sql.addQualifier(FIELD_ITEM_ID,
                         makeSafeString(item.getItemID()));
    }

    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return(revisionNumber);
    }

    /**
       Returns the string representation of this object.
       @return String representation of object
     */
    @Override
    public String toString()
    {
        return(Util.classToStringHeader("JdbcSaveStockItem",
                                        getRevisionNumber(),
                                        hashCode()).toString());
    }
}

