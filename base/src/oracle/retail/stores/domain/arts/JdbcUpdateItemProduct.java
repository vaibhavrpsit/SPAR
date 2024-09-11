/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcUpdateItemProduct.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:55 mszekely Exp $
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
 * $ 5    360Commerce 1.4         7/6/2007 8:36:29 AM    Christian Greene
 * $      Remove reference to deleted ItemProduct table
 * $ 4    360Commerce 1.3         1/25/2006 4:11:26 PM   Brett J. Larsen merge
 * $      7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 * $ 3    360Commerce 1.2         3/31/2005 4:28:45 PM   Robert Pearse   
 * $ 2    360Commerce 1.1         3/10/2005 10:22:52 AM  Robert Pearse   
 * $ 1    360Commerce 1.0         2/11/2005 12:12:05 PM  Robert Pearse   
 * $$$
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.stock.ProductIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This operation updates the stock item table from the ProductIfc object.
 * 
 * @deprecated 02JUL2007 Concept of Product has been replaced by Merchandise
 *             Hierarchy.
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 * @see oracle.retail.stores.domain.arts.ItemDataTransaction
 * @see oracle.retail.stores.domain.stock.ProductIfc
 * @see oracle.retail.stores.domain.arts.JdbcSaveItemProduct
 */
public class JdbcUpdateItemProduct extends JdbcSaveItemProduct implements ARTSDatabaseIfc
{
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcUpdateItemProduct.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Class constructor.
     */
    public JdbcUpdateItemProduct()
    {
        setName("JdbcUpdateItemProduct");
    }

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
        if (logger.isDebugEnabled())
            logger.debug("JdbcUpdateItemProduct.execute()");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        ProductIfc product = (ProductIfc)action.getDataObject();
        try
        {
            updateItemProduct(connection, product);
        }
        catch (DataException de)
        {
            if (de.getErrorCode() == DataException.NO_DATA)
            {
                insertItemProduct(connection, product);
            }
            else
            {
                throw de;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcUpdateItemProduct.execute()");
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
        return (Util.classToStringHeader("JdbcUpdateItemProduct", getRevisionNumber(), hashCode()).toString());
    }
}
