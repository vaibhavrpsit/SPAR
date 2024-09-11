/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSelectIDDIData.java /main/9 2013/03/12 14:05:02 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  03/07/13 - SQLSelectStatement is used instead of TableQueryInfo
 *                         so that multiple tables can be used.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.foundation.iddi.ifc.IDDIWriterIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * This class reads the table data
 */
public class JdbcSelectIDDIData extends JdbcReadIDDIData implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -1622779482340495488L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSelectEmployees.class);

    /**
     * Class constructor.
     */
    public JdbcSelectIDDIData()
    {
        setName("JdbcSelectIDDIData");
    }

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    public void execute(
        DataTransactionIfc dataTransaction,
        DataConnectionIfc dataConnection,
        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcSelectEmployees.execute()");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        Vector selectedIDDIData = new Vector();


        Collection dataObjects = (Collection) action.getDataObject();

        // get the TableQueryInfo and IDDIWriter objects from action
        Iterator it = dataObjects.iterator();

        SQLSelectStatement selectStatement = (SQLSelectStatement)it.next();

        IDDIWriterIfc writer = (IDDIWriterIfc)it.next();

        // retrieve table data
        selectedIDDIData= selectIDDIData(connection, selectStatement, writer);

        // Set the table data in dataTransaction
        dataTransaction.setResult(selectedIDDIData);

        if (logger.isDebugEnabled())
            logger.debug("JdbcSelectEmployees.execute()");
    }
}
