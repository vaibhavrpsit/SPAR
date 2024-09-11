/* ===========================================================================
* Copyright (c) 2006, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcUpdateEmployeeNumberFailedAttempts.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:06 mszekely Exp $
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
 *  2    360Commerce 1.1         9/29/2006 12:49:30 PM  Rohit Sachdeva  21237:
 *       Password Policy Service Persistence Updates 
 *  1    360Commerce 1.0         9/29/2006 12:24:02 PM  Rohit Sachdeva  
 * $
 * 
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation updates the number of Failed Password Attempts. It only
 * updates the attempts column. This was made for Performance reasons so that we
 * don't update all attributes when employee makes for example 5 invalid
 * password attempts.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcUpdateEmployeeNumberFailedAttempts extends JdbcSaveEmployee implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -5088475979496497188L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcUpdateEmployeeNumberFailedAttempts.class);

    /**
     * Class constructor.
     */
    public JdbcUpdateEmployeeNumberFailedAttempts()
    {
        setName("JdbcUpdateEmployeeNumberFailedAttempts");
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
            logger.debug("JdbcEmployeeUpdate.execute()");

        /*
         * getUpdateCount() is about the only thing outside of
         * DataConnectionIfc that we need.
         */
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        EmployeeIfc employee = (EmployeeIfc) action.getDataObject();
        updateEmployeeNumberFailedAttempts(connection, employee);

        if (logger.isDebugEnabled())
            logger.debug("JdbcEmployeeUpdate.execute()");
    }

}
