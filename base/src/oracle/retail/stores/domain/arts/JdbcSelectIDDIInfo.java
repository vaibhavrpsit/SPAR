/* ===========================================================================
* Copyright (c) 2007, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSelectIDDIInfo.java /main/12 2013/03/12 14:05:06 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  03/07/13 - SQLSelectStatement is used instead of TableQueryInfo
 *                         so that multiple tables can be used.
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
 *  3    360Commerce 1.2         6/4/2007 9:52:23 PM    Tony Zgarba     Fixed a
 *        threading issue that occurred during concurring writes.
 *  2    360Commerce 1.1         5/28/2007 5:28:45 AM   Naveen Ganesh
 *       Modified Logger and Log Messages
 *  1    360Commerce 1.0         5/9/2007 5:09:07 PM    Naveen Ganesh   
 * $
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.util.Vector;

import oracle.retail.stores.common.sql.SQLSelectStatement;
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
public class JdbcSelectIDDIInfo extends JdbcReadIDDIInfo implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -4866358164683863800L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSelectIDDIInfo.class);

    /**
     * Class constructor.
     */
    public JdbcSelectIDDIInfo()
    {
        setName("JdbcSelectIDDIInfo");
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
            logger.debug("JdbcSelectIDDIInfo.execute()");


        action.getDataObject();
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        Vector selectedIDDIInfo = new Vector();

        // get the SQLSelectStatement object from action
        SQLSelectStatement selectStatement = (SQLSelectStatement)action.getDataObject();

        // retrieve table data
        selectedIDDIInfo= selectIDDIInfo(connection, selectStatement);


        // Set the table data in dataTransaction
        dataTransaction.setResult(selectedIDDIInfo);

        if (logger.isDebugEnabled())
            logger.debug("JdbcSelectIDDIInfo.execute()");
    }

}
