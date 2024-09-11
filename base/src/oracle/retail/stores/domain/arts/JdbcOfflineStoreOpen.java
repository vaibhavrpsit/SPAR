/* ===========================================================================
* Copyright (c) 2013, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcOfflineStoreOpen.java /main/1 2014/07/23 15:44:28 rhaight Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  08/21/14 - Fix to address null deference issue reported by Fortify
 *    rhaight   07/23/14 - Code review updates
 *    rhaight   07/03/14 - store offline open revisions
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.common.data.JdbcUtilities;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.transaction.StoreOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

/**
 * 
 * @since 14.1
 * 
 * DataOperation implementation to provide special handling for StoreOpenClose 
 * transactions performed when the client is offline from the store server.
 * If the store open transaction happens offline, a check for a preexisting open
 * store business date is made. If no record exists, the store open is persisted as
 * a normal open transaction. If an open record is found, the store open transaction
 * is saved in a a cancelled state and does not modify the financials
 * @author rhaight
 *
 */
public class JdbcOfflineStoreOpen extends JdbcDataOperation implements ARTSDatabaseIfc
{

   	/** Serial Version ID */
	private static final long serialVersionUID = 2073368770832762295L;




	/**
     * Default constructor for JdbcOfflineStoreOpen
     */
    public JdbcOfflineStoreOpen()
    {
        name = "OfflineStoreOpen";
    }
    

    /**
     * Handle a Store Open Transaction performed on the client in offline mode.
     * The method is synchronized to make sure the first transaction perists the store open data,
     * while subsequent transactions should be treated as a duplicate.
     * 
     */
    @Override
    public synchronized void execute(DataTransactionIfc dt, DataConnectionIfc dc, DataActionIfc da) throws DataException
    {
        StoreOpenCloseTransactionIfc storeOC = (StoreOpenCloseTransactionIfc) da.getDataObject();
     
        EYSDate busDate = storeOC.getBusinessDay();
        String storeID = storeOC.getWorkstation().getStoreID();
        
        if (isDuplicateStoreOpen(dc, storeID, busDate))
        {
            logger.warn("Found duplicate store entry for " + storeID + " on business date " + busDate.toFormattedString());
            
            postDuplicateOfflineOpen(dc, storeOC);
        }
        else
        {
            
            logger.warn("Saving store open transaction in offline processing for store " + storeID + " and business date " + busDate.toFormattedString());
            // This is the first need to save a a regular store open
            storeOC.setStoreOpenMode(StoreOpenCloseTransactionIfc.STORE_OPEN_MODE_ONLINE);
            TransactionWriteDataTransaction dbTrans = (TransactionWriteDataTransaction) DataTransactionFactory
                    .create(DataTransactionKeys.TRANSACTION_WRITE_NOT_QUEUED_DATA_TRANSACTION);
            
            dbTrans.saveTransaction(storeOC);
        }
    }    
     
    /**
     * Save the Offline Store Open transaction with an existing opened business date. The
     * default action will be to save as a No Sale transaction
     * @param con
     * @param tran
     * @throws DataException
     */
    protected void postDuplicateOfflineOpen(DataConnectionIfc con, StoreOpenCloseTransactionIfc tran) throws DataException
    {
        logger.warn("Saving StoreOpenCloseTransaction " + tran.getTransactionID() + " as a duplicate in cancelled status");
        
        tran.setStoreOpenMode(StoreOpenCloseTransactionIfc.STORE_OPEN_MODE_DUPLICATE);
    	tran.setTransactionStatus(TransactionIfc.STATUS_CANCELED);
    	
        TransactionWriteDataTransaction dbTrans = (TransactionWriteDataTransaction) DataTransactionFactory
                .create(DataTransactionKeys.TRANSACTION_WRITE_NOT_QUEUED_DATA_TRANSACTION);
        
        dbTrans.saveTransaction(tran);
    	
    }

    

    
    protected boolean isDuplicateStoreOpen(DataConnectionIfc connection, String storeID, EYSDate busDate ) throws DataException
    {
        boolean prevStoreRecord = false;

        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Add the desired tables (and aliases)
         */
        sql.addTable(TABLE_STORE_HISTORY, ALIAS_STORE_HISTORY);
        sql.addTable(TABLE_REPORTING_PERIOD, ALIAS_REPORTING_PERIOD);
        sql.addTable(TABLE_BUSINESS_DAY, ALIAS_BUSINESS_DAY);

        /*
         * Add desired columns
         */
        sql.addColumn(FIELD_STORE_HISTORY_STATUS_CODE);
        sql.addColumn(FIELD_STORE_START_DATE_TIMESTAMP);
        sql.addColumn(ALIAS_BUSINESS_DAY + "." + FIELD_BUSINESS_DAY_DATE);

        /*
         * Add Qualifiers
         */

        // For the specified store only
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = '" + storeID +"'");

        // Join Store History and Reporting Period
        sql.addQualifier(ALIAS_STORE_HISTORY + "." + FIELD_FISCAL_YEAR
                         + " = " + ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_YEAR);
        sql.addQualifier(ALIAS_STORE_HISTORY + "." + FIELD_REPORTING_PERIOD_TYPE_CODE
                         + " = " + ALIAS_REPORTING_PERIOD + "." + FIELD_REPORTING_PERIOD_TYPE_CODE);
        sql.addQualifier(ALIAS_STORE_HISTORY + "." + FIELD_REPORTING_PERIOD_ID
                         + " = " + ALIAS_REPORTING_PERIOD + "." + FIELD_REPORTING_PERIOD_ID);

        // Join Reporting Period and Business Day
        sql.addQualifier(ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_YEAR
                         + " = " + ALIAS_BUSINESS_DAY + "." + FIELD_FISCAL_YEAR);
        sql.addQualifier(ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_WEEK_NUMBER
                         + " = " + ALIAS_BUSINESS_DAY + "." + FIELD_FISCAL_WEEK_NUMBER);
        sql.addQualifier(ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_DAY_NUMBER
                         + " = " + ALIAS_BUSINESS_DAY + "." + FIELD_FISCAL_DAY_NUMBER);

//      For the specified business day only
        sql.addQualifier(ALIAS_BUSINESS_DAY + "." + FIELD_BUSINESS_DAY_DATE
                         + " = " + JdbcUtilities.dateToSQLDateString(busDate.dateValue()));


        ResultSet rs = null;
        try
        {
            connection.execute(sql.getSQLString());
            rs = (ResultSet) connection.getResult();
            
            while (rs.next())
            {
                prevStoreRecord = true;
                break;
            }
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "offlineStoreOpen", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "offlineStoreOpen", e);
        }
        finally
        {
            try
            {
                if(rs != null)
                {
                    rs.close();
                }
            }
            catch (Throwable eth)
            {
                // ignore while closing record set
            }
        }
        
        return prevStoreRecord; 
    }

}
