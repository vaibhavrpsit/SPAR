/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadStoreStatuses.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:00 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/28/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *
 * ===========================================================================

     $Log:
      3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:22:45 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:12:00 PM  Robert Pearse   
     $
     Revision 1.6  2004/04/09 16:55:47  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.5  2004/02/17 17:57:38  bwf
     @scr 0 Organize imports.

     Revision 1.4  2004/02/17 16:18:47  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:17  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:26  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
     updating to pvcs 360store-current


 * 
 *    Rev 1.0   Aug 29 2003 15:32:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:38:18   msg
 * Initial revision.
 * 
 *    Rev 1.2   25 Mar 2002 12:30:04   epd
 * Jose asked me to check these in.  Updates to use TenderDescriptor
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.2   03 Dec 2001 16:09:34   epd
 * added code to add Store safe tender types
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.1   24 Oct 2001 17:34:36   mpm
 * Merged changes from Pier 1/Virginia ABC effort relating to inventory inquiry.
 *
 *    Rev 1.0   Sep 20 2001 15:59:58   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:20   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.domain.arts;
// java imports
import java.sql.SQLException;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

//-------------------------------------------------------------------------
/**
    This data operation retrieves the current status choices for the given
    store.  It first attempts to retrieve the open business dates.
    If none are found, it selects the most recent non-open business date.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//-------------------------------------------------------------------------
public class JdbcReadStoreStatuses extends JdbcReadStore
{
    private static final long serialVersionUID = 5780210143622865602L;

    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcReadStoreStatuses.class);

    //---------------------------------------------------------------------
    /**
       Class constructor.
    **/
    //---------------------------------------------------------------------
    public JdbcReadStoreStatuses()
    {
        super();
        setName("JdbcReadStoreStatuses");
    }

    //---------------------------------------------------------------------
    /**
       Executes the SQL statements against the database.
       <P>
       @param  dataTransaction     The data transaction
       @param  dataConnection      The connection to the data source
       @param  action              The information passed by the valet
       @exception DataException upon error
    **/
    //---------------------------------------------------------------------
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        /*
         * getUpdateCount() is about the only thing outside of
         * DataConnectionIfc that we need.
         */
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;
               
        StoreStatusIfc[] statusList = null;
        
        Object dataObject = action.getDataObject();
        if (dataObject instanceof StringSearchCriteria)
        {
            // search for specific table size which has the table description
            StringSearchCriteria searchCriteria = (StringSearchCriteria)dataObject;
            
            String storeID = searchCriteria.getIdentifier();            
            LocaleRequestor localeRequestor = searchCriteria.getLocaleRequestor();
            statusList = getStatusList(connection, storeID, localeRequestor);
        }
        else if (dataObject instanceof String)
        {
            //this block deprecated as of Release 13.1
            String storeID = (String) action.getDataObject();
            statusList = getStatusList(connection, storeID, new LocaleRequestor(LocaleMap.getLocale(LocaleMap.DEFAULT)));
        }
        else 
        {
            logger.error("JdbcReadStoreStatuses.execute: Invalid search object");
            throw new DataException("Invalid search object");
        }
        
        /*
         * Send back the result
         */
        dataTransaction.setResult(statusList);
    }

    /**
     * @param connection
     * @param storeID
     * @return
     * @throws DataException
     * @deprecated As of release 13.1 use  {@link #getStatusList(JdbcDataConnection, String, LocaleRequestor)}
     */
    private StoreStatusIfc[] getStatusList(JdbcDataConnection connection, String storeID) throws DataException
    {
        return getStatusList(connection, storeID, new LocaleRequestor(LocaleMap.getLocale(LocaleMap.DEFAULT)));
    }

    /**
     * @param connection
     * @param storeID
     * @return
     * @throws DataException
     */
    private StoreStatusIfc[] getStatusList(JdbcDataConnection connection, String storeID, LocaleRequestor localeRequestor) 
    throws DataException
    {
        StoreStatusIfc[] statusList = null;

        try
        {
            // attempt to read all open business dates
            statusList = readStoreStatuses(connection, storeID);
        }
        catch (DataException de)
        {
            if (de.getErrorCode() != DataException.NO_DATA)
            {
                throw de;
            }
        }

        // if no open business dates, read most recent status
        if (statusList == null)
        {
            // get single store status
            statusList = new StoreStatusIfc[1];
            statusList[0] = readStoreStatus(connection, storeID);
        }

        statusList[0].setStore(readRegionDistrict(connection, statusList[0].getStore(), localeRequestor));

        // Read and set the TendersForStoreSafe in each status
        TenderDescriptorIfc[] tenderDescList = readSafeTenders(connection);
        for (int i = 0; i < statusList.length; i++)
        {
            for (int j = 0; j < tenderDescList.length; j++)
            {
                statusList[i].addSafeTenderDesc(tenderDescList[j]);
            }  // for j (each tender)
        } // for i (each status in the list)
        return statusList;
    }
    
    //---------------------------------------------------------------------
    /**
       Returns the current open business dates in the store history table.
       <P>
       @param  dataConnection  connection to the db
       @param  storeID         the store id
       @return statuses of store, or null if no store history records.
       @exception DataException upon error
    **/
    //---------------------------------------------------------------------
    public StoreStatusIfc[] readStoreStatuses(JdbcDataConnection dataConnection,
                                              String storeID)
        throws DataException
    {                                   // begin readStoreStatuses()
        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Add the desired tables (and aliases)
         */
        sql.addTable(TABLE_STORE_HISTORY + " sh");
        sql.addTable(TABLE_REPORTING_PERIOD + " rp");
        sql.addTable(TABLE_BUSINESS_DAY + " bd");

        /*
         * Add desired columns
         */
        sql.addColumn(FIELD_STORE_HISTORY_STATUS_CODE);
        sql.addColumn("sh." + FIELD_OPERATOR_ID);
        sql.addColumn(FIELD_STORE_START_DATE_TIMESTAMP);
        sql.addColumn("bd." + FIELD_BUSINESS_DAY_DATE);

        /*
         * Add Qualifiers
         */

        // For the specified store only
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(storeID));

        // for the given status
        sql.addQualifier(FIELD_STORE_HISTORY_STATUS_CODE + " = " + AbstractFinancialEntityIfc.STATUS_OPEN);

        // Join Store History and Reporting Period
        sql.addQualifier("sh." + FIELD_FISCAL_YEAR + " = rp." + FIELD_FISCAL_YEAR);
        sql.addQualifier("sh." + FIELD_REPORTING_PERIOD_TYPE_CODE + " = rp." + FIELD_REPORTING_PERIOD_TYPE_CODE);
        sql.addQualifier("sh." + FIELD_REPORTING_PERIOD_ID + " = rp." + FIELD_REPORTING_PERIOD_ID);

        // Join Reporting Period and Business Day
        sql.addQualifier("rp." + FIELD_FISCAL_YEAR + " = bd." + FIELD_FISCAL_YEAR);
        sql.addQualifier("rp." + FIELD_FISCAL_WEEK_NUMBER + " = bd." + FIELD_FISCAL_WEEK_NUMBER);
        sql.addQualifier("rp." + FIELD_FISCAL_DAY_NUMBER + " = bd." + FIELD_FISCAL_DAY_NUMBER);

        /*
         * Add Ordering
         */
        sql.addOrdering("bd." + FIELD_BUSINESS_DAY_DATE + " DESC");

        StoreStatusIfc[] list = null;
        try
        {
            dataConnection.execute(sql.getSQLString());
            list = parseResultSet(dataConnection,
                                  storeID);
        }
        catch (SQLException se)
        {
            throw new DataException(SQL_ERROR, "readStoreStatuses", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(UNKNOWN, "readStoreStatuses", e);
        }

        return(list);
    }                                   // end readStoreStatuses()

}
