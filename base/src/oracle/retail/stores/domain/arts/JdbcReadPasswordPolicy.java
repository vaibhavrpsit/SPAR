/* ===========================================================================
* Copyright (c) 2006, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadPasswordPolicy.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:06 mszekely Exp $
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
 *    2    360Commerce 1.1         11/2/2006 6:57:15 AM   Rohit Sachdeva
 *         21237: Activating Password Policy Evaluation and Change Password 
 *    1    360Commerce 1.0         9/29/2006 12:29:17 PM  Rohit Sachdeva  
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.Connection;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.security.PasswordPolicyIfc;
import oracle.retail.stores.commerceservices.security.persistence.PasswordPolicyDAOIfc;
import oracle.retail.stores.domain.data.DAOFactory;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation reads all of the password policy settings stored in database
 * Password Policy related tables. This uses PasswordPolicyDAO by shared
 * connection to share the statement execution between WebApps and POS.
 * $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcReadPasswordPolicy extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 8367598833108129253L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadPasswordPolicy.class);

    /**
     * Class constructor.
     */
    public JdbcReadPasswordPolicy()
    {
        super();
        setName("JdbcReadPasswordPolicy");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadPasswordPolicy.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
        
        PasswordPolicyIfc passwordPolicy = readPasswordPolicy(connection);
        
        dataTransaction.setResult(passwordPolicy);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadPasswordPolicy.execute");
    }


    /**
       Executes the SQL statements against the database.
       @return  PasswordPolicyIfc password policy
       @param  dataConnection
       @exception DataException upon error
     */
    public PasswordPolicyIfc readPasswordPolicy(JdbcDataConnection dataConnection) throws DataException
    {
        Connection connection = dataConnection.getConnection();
        PasswordPolicyDAOIfc passwordPolicyDAO = (PasswordPolicyDAOIfc)DAOFactory.createBean(PasswordPolicyDAOIfc.PASSWORD_POLICY_DAO_BEAN_KEY);
        passwordPolicyDAO.setConnection(connection);
        PasswordPolicyIfc passwordPolicy = passwordPolicyDAO.findDefault();
    	return passwordPolicy;
    }                                   
}
