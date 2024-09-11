/* ===========================================================================
* Copyright (c) 2006, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/CounterGenerationAction.java /main/12 2012/04/25 10:25:36 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  04/24/12 - Fixes for Fortify redundant null check
 *    cgreene   05/26/10 - deprecated
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 4    360Commerce 1.3         3/2/2008 2:21:25 PM    Anda D. Cadar   CR 29724:
 *    Code merge from V12x to Trunk
 3    360Commerce 1.2         6/20/2006 1:26:09 PM   Brendan W. Farrell Fixed
 *    select.
 2    360Commerce 1.1         6/20/2006 11:21:33 AM  Brendan W. Farrell Fixed
 *    base method.
 1    360Commerce 1.0         5/31/2006 5:01:34 PM   Brendan W. Farrell 
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;

/**
 * @deprecated as of 13.3 use {@link IdentifierServiceLocator#getIdentifierService()} instead.
 */
public class CounterGenerationAction implements ARTSDatabaseIfc
{
    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(CounterGenerationAction.class);

    private static CounterGenerationAction instance = null;
    
    /**
     * Singleton Constructor
     */
    private CounterGenerationAction()
    {        
    }
    
    /** 
     * Returns the single instance.
     *
     *  @return CounterGenerationAction
     */
    public static synchronized CounterGenerationAction getInstance()
    {
        if (instance == null)
        {
            instance = new CounterGenerationAction();
        }
        return instance;
    }
   
    /**
     * Return a string representation of the value return by calling
     * {@next #getCounterID(DataConnectionIfc, String)}.
     * 
     * @param dataConnection the data connection to use.
     * @param counterName the name of the counter being returned and incremented.
     * @return a string value of the next id
     * @throws DataException
     */
    public String getCounterIDString(DataConnectionIfc dataConnection,
                                     String counterName) throws DataException
    {
        String counterID = null;
        counterID = Integer.toString(getCounterID(dataConnection, counterName));
        return counterID;
    }
    
    /**
     * This method gets the next counter id from the id generation table. Then
     * it updates the value in the table to the next usable id for any
     * subsequent operation.
     * 
     * @param dataConnection
     * @param counterName
     * @return
     * @throws DataException
     * @see oracle.retail.stores.domain.data.AbstractDBUtils#getNextID(String)
     * @see data seeding script 'InsertTableIDGeneration.sql'
     */
    public int getCounterID(DataConnectionIfc dataConnection,
                               String counterName) throws DataException
    {
    	// sql string to use to set incremented id onto table
    	String applyNextIdSql = null;

        int counterID = 1; // defaults to 1 if not found.
        // select new ID
        SQLSelectStatement sql = new SQLSelectStatement();
        // set table
        sql.addTable(TABLE_ID_GENERATION);
        // set column
        sql.addColumn(FIELD_COUNTER_NEXT_VALUE);
        // set qualifier
        sql.addQualifier(FIELD_COUNTER_GENERATION_NAME,
                         JdbcDataOperation.makeSafeString(counterName));
        try
        {
            // execute SQl and retrieve answer from result set
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet) dataConnection.getResult();
            if (rs != null && rs.next())
            {
                counterID = rs.getInt(1);

                // record was found. update the value in the generation table
                SQLUpdateStatement sqlUpdate = new SQLUpdateStatement();
                sqlUpdate.setTable(TABLE_ID_GENERATION);
                sqlUpdate.addColumn(FIELD_COUNTER_NEXT_VALUE, counterID + 1);
                sqlUpdate.addQualifier(FIELD_COUNTER_GENERATION_NAME, 
                                       JdbcDataOperation.makeSafeString(counterName));
                applyNextIdSql = sqlUpdate.getSQLString();
            }
            else
            {
            	// record was not found. insert a new value into the table
                SQLInsertStatement sqlInsert = new SQLInsertStatement();
                sqlInsert.setTable(TABLE_ID_GENERATION);
                sqlInsert.addColumn(FIELD_COUNTER_GENERATION_NAME, 
                        JdbcDataOperation.makeSafeString(counterName));
                sqlInsert.addColumn(FIELD_COUNTER_NEXT_VALUE, counterID + 1);
                applyNextIdSql = sqlInsert.getSQLString();
            }
            // close result set
            if (rs != null)
            {
                rs.close();
            }
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (SQLException se)
        {
            logger.error(se);
            throw new DataException(DataException.SQL_ERROR,
                            "Selecting counter for " + counterName, se);
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN,
                            "Selecting counter for " + counterName, e);
        }
        
        try
        {
        	if (applyNextIdSql != null)
        	{
        		// execute SQl
        		dataConnection.execute(applyNextIdSql);
        	}
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN,
                            "Trying to update counter for " + counterName, e);
        }

        return counterID;
    }
}
