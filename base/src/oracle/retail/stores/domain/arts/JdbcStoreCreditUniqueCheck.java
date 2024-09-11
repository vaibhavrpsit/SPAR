/* ===========================================================================
* Copyright (c) 2007, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcStoreCreditUniqueCheck.java /main/9 2012/04/02 10:35:23 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   03/30/12 - Refactoring of getNumber() method of TenderCheck
 *                         class - returns sensitive data in byte[] instead of
 *                         String
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
 *  1    360Commerce 1.0         5/19/2008 2:46:20 AM   ASHWYN TIRKEY   Added
 *       this file to search for a store credit that has been issued for issue
 *        31453
 *
 *       
 * $
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation searches for a store credit that has been issued.
 * 
 * @version $Revision: /main/9 $
 */
public class JdbcStoreCreditUniqueCheck extends JdbcDataOperation implements ARTSDatabaseIfc
{
    /**
	 * 
	 */
    private static final long serialVersionUID = -3979575290055877600L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcStoreCreditUniqueCheck.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/9 $";

    /**
     * Class constructor.
     */
    public JdbcStoreCreditUniqueCheck()
    {
        setName("JdbcStoreCreditUniqueCheck");
    }

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcStoreCreditUniqueCheck.execute()");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;
        
        TenderStoreCreditIfc storeCredit = (TenderStoreCreditIfc) action.getDataObject();
        
        lookupStoreCredit(connection, storeCredit);

        dataTransaction.setResult(storeCredit);
         
        if (logger.isDebugEnabled()) logger.debug( "JdbcStoreCreditUniqueCheck.execute()");

    }
    
    /**
       Searches for a store credit in TABLE_STORE_CREDIT.
       <P>
       @param  dataConnection  connection to the db
       @param  String store credit id
       @exception DataException upon error
     */
    public void lookupStoreCredit(JdbcDataConnection dataConnection,
                                       TenderStoreCreditIfc storeCredit)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcStoreCreditUniqueCheck.lookupStoreCredit()");
        
        SQLSelectStatement sql = new SQLSelectStatement();
        // Table
        sql.setTable(TABLE_STORE_CREDIT);
        
        // add column
        sql.addColumn(FIELD_STORE_CREDIT_ID);

        // Qualifiers
        sql.addQualifier(FIELD_STORE_CREDIT_ID
                         + " = " + makeSafeString(new String(storeCredit.getNumber())));
        
        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();
            
            if (!rs.next())
            {
                throw new DataException(DataException.NO_DATA, "lookupStoreCredit");
            }
            
            rs.close();
        }
        catch (DataException de)
        {
            logger.warn(de);
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "lookupStoreCredit");
            throw new DataException(DataException.SQL_ERROR, "lookupStoreCredit", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "lookupStoreCredit", e);
        }
        
        if (logger.isDebugEnabled()) logger.debug( "JdbcLookupStoreCredit.lookupStoreCredit()");
    }

}
