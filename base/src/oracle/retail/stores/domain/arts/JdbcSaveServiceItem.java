/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveServiceItem.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:04 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
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
 *    4    360Commerce 1.3         1/25/2006 4:11:23 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:44 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:49 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:04 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:26:11    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:44     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:49     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:04     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
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
 *    Rev 1.0   Aug 29 2003 15:32:56   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:40:08   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:48:42   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:08:30   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:59:30   msg
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
import oracle.retail.stores.domain.stock.ItemIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This operation updates the stock item table from the ItemIfc object.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 * @see oracle.retail.stores.domain.arts.ItemDataTransaction
 * @see oracle.retail.stores.domain.stock.ItemIfc
 */
public abstract class JdbcSaveServiceItem extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -5628751166799019517L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveServiceItem.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Perform stock item update.
     * 
     * @param dataConnection JdbcDataConnection
     * @param item ItemIfc reference
     * @exception DataException thrown if error occurs
     */
    public void updateServiceItem(JdbcDataConnection dataConnection,
                                ItemIfc item)
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
                                    "ServiceItem update",
                                    e);
        }

    }

    /**
        Perform stock item insert. <P>
        @param dataConnection JdbcDataConnection
        @param item ItemIfc reference
        @exception DataException thrown if error occurs
     */
    public void insertServiceItem(JdbcDataConnection dataConnection,
                                ItemIfc item)
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
                                    "ServiceItem insert",
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
        ItemClassificationIfc classification = item.getItemClassification();
        // add columns
        sql.addColumn(FIELD_STOCK_ITEM_SALE_UNIT_OF_MEASURE_CODE,
                      makeSafeString(item.getUnitOfMeasure().getUnitID()));
        sql.addColumn(FIELD_SERIALIZED_ITEM_VALIDATION_FLAG,
                      makeStringFromBoolean(classification.getSerializedItem()));
        sql.addColumn(FIELD_STOCK_ITEM_RESTOCKING_FEE_FLAG,
                      makeStringFromBoolean(classification.getRestockingFeeFlag()));
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
    }

    /**
        Add update columns. <P>
        @param ItemIfc item object
        @param sql SQLUpdateStatement
     */
    public void addInsertColumns(ItemIfc item,
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
        return(Util.classToStringHeader("JdbcSaveServiceItem",
                                        getRevisionNumber(),
                                        hashCode()).toString());
    }
}

