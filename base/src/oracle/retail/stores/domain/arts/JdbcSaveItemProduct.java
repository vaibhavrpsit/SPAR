/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveItemProduct.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:03 mszekely Exp $
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
 * $ 6    360Commerce 1.5         7/6/2007 8:36:29 AM    Christian Greene
 * $      Remove reference to deleted ItemProduct table
 * $ 5    360Commerce 1.4         5/30/2006 10:07:58 AM  Brett J. Larsen CR
 * $      18490 - UDM - eventID type changed to int
 * $ 4    360Commerce 1.3         1/25/2006 4:11:21 PM   Brett J. Larsen merge
 * $      7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 * $ 3    360Commerce 1.2         3/31/2005 4:28:43 PM   Robert Pearse   
 * $ 2    360Commerce 1.1         3/10/2005 10:22:48 AM  Robert Pearse   
 * $ 1    360Commerce 1.0         2/11/2005 12:12:02 PM  Robert Pearse   
 * $$$
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.sql.SQLUpdatableStatementIfc;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.stock.ProductIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This operation saves the item product data from the ProductIfc object.
 * 
 * @deprecated 02JUL2007 Concept of Product has been replaced by Merchandise
 *             Hierarchy.
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 * @see oracle.retail.stores.domain.arts.ItemDataTransaction
 * @see oracle.retail.stores.domain.stock.ProductIfc
 * @see oracle.retail.stores.domain.arts.JdbcUpdateItemProduct
 * @see oracle.retail.stores.domain.arts.JdbcInsertItemProductIfNew
 */
public abstract class JdbcSaveItemProduct extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 8832033409890381972L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveItemProduct.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Perform item product update.
     * 
     * @param dataConnection JdbcDataConnection
     * @param item ProductIfc reference
     * @exception DataException thrown if error occurs
     */
    public void updateItemProduct(JdbcDataConnection dataConnection, ProductIfc product)
        throws DataException
    {
        // build sql statement
        SQLUpdateStatement sql = new SQLUpdateStatement();
        // add table, columns, qualifiers
        // sql.setTable(TABLE_ITEM_PRODUCT);
        addUpdateColumns(product, sql);
        addUpdateQualifiers(product, sql);
        // execute statement
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e.toString());
            throw new DataException(DataException.UNKNOWN, "ItemProduct update", e);
        }

    }

    /**
     * Perform item product insert.
     * 
     * @param dataConnection JdbcDataConnection
     * @param item ProductIfc reference
     * @exception DataException thrown if error occurs
     */
    public void insertItemProduct(JdbcDataConnection dataConnection, ProductIfc product)
        throws DataException
    {
        // build sql statement
        SQLInsertStatement sql = new SQLInsertStatement();
        // add table, columns, qualifiers
        // sql.setTable(TABLE_ITEM_PRODUCT);
        addInsertColumns(product, sql);
        // execute statement
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e.toString());
            throw new DataException(DataException.UNKNOWN, "ItemProduct insert", e);
        }

    }

    /**
     * Add update columns.
     * 
     * @param ProductIfc product object
     * @param sql SQLUpdateStatement
     */
    public void addUpdateColumns(ProductIfc product, SQLUpdatableStatementIfc sql)
    {
    // sql.addColumn(FIELD_ITEM_PRODUCT_DESCRIPTION,
    // makeSafeString(product.getDescription()));
        sql.addColumn(FIELD_MANUFACTURER_ID, Integer.toString(product.getManufacturer().getManufacturerID()));
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
    }

    /**
     * Add insert columns.
     * 
     * @param ProductIfc product object
     * @param sql SQLInsertStatement
     */
    public void addInsertColumns(ProductIfc product, SQLInsertStatement sql)
    {
    // sql.addColumn(FIELD_ITEM_PRODUCT_ID,
    // makeSafeString(product.getProductID()));
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
        addUpdateColumns(product, sql);
    }

    /**
     * Adds update qualifier columns to SQL statement.
     * 
     * @param ProductIfc product object
     * @param sql SQLUpdateStatement
     */
    public void addUpdateQualifiers(ProductIfc product, SQLUpdateStatement sql)
    {
    // sql.addQualifier(FIELD_ITEM_PRODUCT_ID,
    // makeSafeString(product.getProductID()));
    }

    /**
     * Indicates product exists in product table.
     * 
     * @param dataConnection data connection
     * @param product product object
     * @return true if product exists in product table; false otherwise
     * @exception DataException thrown if error occurs
     */
    public boolean doesProductExist(JdbcDataConnection dataConnection, ProductIfc product)
        throws DataException
    {
        boolean productExists = false;
        // build sql statement
        SQLSelectStatement sql = new SQLSelectStatement();
        // sql.addTable(TABLE_ITEM_PRODUCT);
        // sql.addColumn(FIELD_ITEM_PRODUCT_ID);
        // sql.addQualifier(FIELD_ITEM_PRODUCT_ID,
        // makeSafeString(product.getProductID()));
        // execute statement
        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();
            if (rs.next())
            {
                productExists = true;
            }
            rs.close();
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "ItemProduct exists");
            throw new DataException(DataException.SQL_ERROR, "ItemProduct insert", se);
        }
        catch (Exception e)
        {
            logger.error(e.toString());
            throw new DataException(DataException.UNKNOWN, "ItemProduct exists", e);
        }

        return (productExists);
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
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
        return (Util.classToStringHeader("JdbcSaveItemProduct", getRevisionNumber(), hashCode()).toString());
    }
}
