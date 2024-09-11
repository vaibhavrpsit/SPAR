/* ===========================================================================
* Copyright (c) 2006, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSavePasswordHistory.java /rgbustores_13.4x_generic_branch/1 2011/04/11 11:48:44 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  04/11/11 - XbranchMerge abondala_bug11827952-salting_passwords
 *                         from main
 *    abondala  03/23/11 - Implemented salting for the passwords
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    abondala  01/29/09 - updated files related to hashing algorithm which can
 *                         be configured through properties file.
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         11/2/2006 6:57:15 AM   Rohit Sachdeva
 *         21237: Activating Password Policy Evaluation and Change Password
 *    2    360Commerce 1.1         10/13/2006 4:43:31 PM  Rohit Sachdeva
 *         21237: base16encode added for Password History
 *    1    360Commerce 1.0         9/29/2006 12:39:28 PM  Rohit Sachdeva
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.Connection;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.security.persistence.PasswordHistoryDAOIfc;
import oracle.retail.stores.common.data.JdbcUtilities;
import oracle.retail.stores.domain.data.DAOFactory;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation saves password history. This uses PasswordHistoryDAO. By
 * shared connection the statement execution between WebApps and POS could be
 * shared.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcSavePasswordHistory extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -6798177903094217014L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSavePasswordHistory.class);

    /**
     * Class constructor.
     */
    public JdbcSavePasswordHistory()
    {
        super();
        setName("JdbcSavePasswordHistory");
    }

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction
     * @param dataConnection
     * @param action
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcSavePasswordPolicy.execute starts");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        EmployeeIfc employee = (EmployeeIfc)action.getDataObject();
        insertPasswordHistory(employee, connection);

        if (logger.isDebugEnabled()) logger.debug( "JdbcSavePasswordPolicy.execute exits");
    }

    /**
       Executes the SQL statements against the database.
       @param employee employee reference
       @param  dataConnection
       @exception DataException upon error
     */
    public void insertPasswordHistory(EmployeeIfc employee, JdbcDataConnection dataConnection) throws DataException
    {
        Connection connection = dataConnection.getConnection();
        PasswordHistoryDAOIfc passwordHistoryDAO = (PasswordHistoryDAOIfc)DAOFactory.createBean(PasswordHistoryDAOIfc.PASSWORD_HISTORY_DAO_BEAN_KEY);
        passwordHistoryDAO.setConnection(connection);
        String employeeId = employee.getEmployeeID();
        String passwordHex = JdbcUtilities.base64encode(employee.getPasswordBytes());
        String pwdSalt = employee.getEmployeePasswordSalt();
        passwordHistoryDAO.insertPasswordHistory(employeeId, passwordHex, pwdSalt);
    }
}
