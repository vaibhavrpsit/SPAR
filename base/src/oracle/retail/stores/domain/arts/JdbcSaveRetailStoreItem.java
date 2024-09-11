/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveRetailStoreItem.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:59 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    7    360Commerce 1.6         11/15/2007 11:37:56 AM Christian Greene
 *         Belize merge - PLU changes
 *    6    360Commerce 1.5         6/1/2007 3:16:05 PM    Christian Greene
 *         Backing out PLU to pre-v1.0.0.414 version code
 *    5    360Commerce 1.4         5/31/2007 6:23:15 AM   Christian Greene
 *         Changed selling price lookup to rely on price change tables.
 *         Removed selling and permanenet price columns from posidentity and
 *         retailstoreitem tables. Moved some pricing DIMP beans to item
 *         module so ItemImport DAO can persist price.
 *    4    360Commerce 1.3         1/25/2006 4:11:23 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:44 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:49 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:03 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:26:53    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:44     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:49     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:03     Robert Pearse
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
 *    Rev 1.0   Aug 29 2003 15:32:54   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   11 Jul 2003 12:23:40   crain
 * Added FIELD_ITEM_EMPLOYEE_DISCOUNT_ALLOWED_FLAG
 *
 *    Rev 1.0   Jun 03 2002 16:40:02   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:48:36   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:08:26   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:59:28   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:02   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdatableStatementIfc;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.stock.ItemIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This operation inserts and updates the retail stock item table from the
 * ItemIfc object.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 * @see oracle.retail.stores.domain.arts.ItemDataTransaction
 * @see oracle.retail.stores.domain.stock.ItemIfc
 */
public abstract class JdbcSaveRetailStoreItem extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -5525728796997361421L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveRetailStoreItem.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Perform retail stock item update.
     * 
     * @param dataConnection JdbcDataConnection
     * @param item ItemIfc reference
     * @exception DataException thrown if error occurs
     */
    public void updateRetailStoreItem(JdbcDataConnection dataConnection,
                                      ItemIfc item)
                           throws DataException
    {
        // build sql statement
        SQLUpdateStatement sql = new SQLUpdateStatement();
        // add table, columns, qualifiers
        sql.setTable(TABLE_RETAIL_STORE_ITEM);
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
                                    "RetailStoreItem update",
                                    e);
        }

    }

    /**
        Perform retail stock item insert. <P>
        @param dataConnection JdbcDataConnection
        @param item ItemIfc reference
        @exception DataException thrown if error occurs
     */
    public void insertRetailStoreItem(JdbcDataConnection dataConnection,
                                      ItemIfc item,
                                      String storeID)
                           throws DataException
    {
        // build sql statement
        SQLInsertStatement sql = new SQLInsertStatement();
        // add table, columns, qualifiers
        sql.setTable(TABLE_RETAIL_STORE_ITEM);
        addInsertColumns(item,
                         storeID,
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
                                    "RetailStoreItem insert",
                                    e);
        }

    }

    /**
        Add update columns. <P>
        @param ItemIfc item object
        @param sql SQLUpdateStatement
     */
    public void addUpdateColumns(ItemIfc item,
                                 SQLUpdatableStatementIfc sql)
    {
        // add columns
        sql.addColumn(FIELD_COMPARE_AT_SALE_UNIT_RETAIL_PRICE_AMOUNT,
                      item.getCompareAtPrice().toString());
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                      getSQLCurrentTimestampFunction());



    }

    /**
        Add insert columns. <P>
        @param ItemIfc item object
        @param sql SQLInsertStatement
     */
    public void addInsertColumns(ItemIfc item,
                                 String storeID,
                                 SQLInsertStatement sql)
    {
        // add columns
        sql.addColumn(FIELD_ITEM_ID,
                      makeSafeString(item.getItemID()));
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
        addUpdateColumns(item, sql);

        // this is a temporary kludge
        sql.addColumn(FIELD_RETAIL_STORE_ID,
                      makeSafeString(storeID));
    }

    /**
        Adds update qualifier columns to SQL statement. <P>
        @param ItemIfc item object
        @param sql SQLUpdateStatement
     */
    public void addUpdateQualifiers(ItemIfc item,
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
        return(Util.classToStringHeader("JdbcSaveRetailStoreItem",
                                        getRevisionNumber(),
                                        hashCode()).toString());
    }
}

