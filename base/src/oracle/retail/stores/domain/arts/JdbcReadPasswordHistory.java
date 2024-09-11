/* ===========================================================================
* Copyright (c) 2006, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadPasswordHistory.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:04 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
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
 *    2    360Commerce 1.1         11/2/2006 6:57:15 AM   Rohit Sachdeva
 *         21237: Activating Password Policy Evaluation and Change Password 
 *    1    360Commerce 1.0         9/29/2006 12:34:59 PM  Rohit Sachdeva  
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.Connection;
import java.util.Collection;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.security.EmployeeComplianceIfc;
import oracle.retail.stores.commerceservices.security.persistence.PasswordHistoryDAOIfc;
import oracle.retail.stores.domain.data.DAOFactory;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation reads all of the password history stored in database. This
 * uses PasswordHistoryDAO. By shared connection the statement execution between
 * WebApps and POS could be shared. $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcReadPasswordHistory extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -1140953445375866052L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadPasswordPolicy.class);

    /**
     * Class constructor.
     */
    public JdbcReadPasswordHistory()
    {
        super();
        setName("JdbcReadPasswordHistory");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadPasswordPolicy.execute starts");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
        
        EmployeeIfc employee = (EmployeeIfc)action.getDataObject();
        Collection history = readPasswordHistory(employee, connection);
        EmployeeComplianceIfc employeeCompliance = employee.getEmployeeCompliance();
        employeeCompliance.setPasswordHistory(history);
        employee.setEmployeeCompliance(employeeCompliance);
        dataTransaction.setResult(employee);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadPasswordPolicy.execute exits");
    }


    /**
       Executes the SQL statements against the database.
       @return  Collection password history collection
       @param  dataConnection
       @param employee
       @exception DataException upon error
     */
    public Collection readPasswordHistory(EmployeeIfc employee, JdbcDataConnection dataConnection) throws DataException
    {
    	Connection connection = dataConnection.getConnection();
    	PasswordHistoryDAOIfc passwordHistoryDAO = (PasswordHistoryDAOIfc)DAOFactory.createBean(PasswordHistoryDAOIfc.PASSWORD_HISTORY_DAO_BEAN_KEY);
    	passwordHistoryDAO.setConnection(connection);
        String employeeId = employee.getEmployeeID();
        Collection passwordHistory = passwordHistoryDAO.readPasswordHistory(employeeId);
    	return passwordHistory;
    }                                   
}
