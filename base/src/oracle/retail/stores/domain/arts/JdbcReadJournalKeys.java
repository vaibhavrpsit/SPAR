/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadJournalKeys.java /main/12 2012/05/21 15:50:18 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/21/12 - XbranchMerge cgreene_bug-13951397 from
 *                         rgbustores_13.5x_generic
 *    cgreene   05/16/12 - arrange order of businessDay column to end of
 *                         primary key to improve performance since most
 *                         receipt lookups are done without the businessDay
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         8/23/2006 2:44:15 PM   Brett J. Larsen CR
 *         20740 - oracle db port - some refactoring and changes to format
 *         dates w/ db helper class
 *    4    360Commerce 1.3         1/25/2006 4:11:16 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:44 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:59 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.2.0     11/16/2005 16:26:09    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:41     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:44     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:59     Robert Pearse
 *
 *   Revision 1.8  2004/07/30 23:25:11  kmcbride
 *   @scr 6639: Fixing up EJ to use only the sequence number portion of the transaction id when comparing transaction ids within the same store, reg. and biz date.
 *
 *   Revision 1.7  2004/06/29 15:35:47  kll
 *   @scr 5895: retrieval of journal entries updated
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:46  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Dec 17 2003 08:17:14   rrn
 * Initial revision.
 * Resolution for 3611: EJournal to database
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.EJournalKey;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.journal.EJournalKeyIfc;
import oracle.retail.stores.foundation.manager.ifc.journal.SearchableIfc;

/**
 * This operation finds journal entries in the database matching the input
 * search criteria.
 * 
 * @version $Revision: /main/12 $
 */
public class JdbcReadJournalKeys extends JdbcDataOperation implements ARTSDatabaseIfc, CodeConstantsIfc
{
    private static final long serialVersionUID = -1267660612222959031L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadJournalKeys.class);

    /**
     * Class constructor.
     */
    public JdbcReadJournalKeys()
    {
        super();
        setName("JdbcReadJournalKeys");
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

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadJournalKeys.execute");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;
        SearchableIfc search = (SearchableIfc) action.getDataObject();

        searchDatabase(dataTransaction, connection, search);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadJournalKeys.execute");
    }

    /**
       Searches the journal for entries matching the search criteria.  Results
       are put on the DataTransaction object as an ArrayList.
       <p>
       @param  dataConnection       The connection to the data source
       @param  search               The search criteria
       @exception DataException upon error
     */
    public void searchDatabase(DataTransactionIfc dataTransaction,
                               JdbcDataConnection dataConnection,
                               SearchableIfc search)
        throws DataException
    {

        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables
        sql.addTable(TABLE_EJOURNAL);

        // Retrieve columns that comprise the key
        //   ID_STR_RT, ID_WS, DC_DY_BSN, AI_TRN, TS_JRL_BGN
        sql.addColumn(FIELD_RETAIL_STORE_ID);                   // 1
        sql.addColumn(FIELD_WORKSTATION_ID);                    // 2
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER);       // 3
        sql.addColumn(FIELD_BUSINESS_DAY_DATE);                 // 4
        sql.addColumn(FIELD_EJOURNAL_ENTRY_BEGIN_TIMESTAMP);    // 5

        try
        {

            // Add qualifiers based on input search criteria
            addSearchQualifiers(search, sql);

            // order resulting list of by timestamp (earliest to latest)
            sql.addOrdering(FIELD_EJOURNAL_ENTRY_BEGIN_TIMESTAMP);

            dataConnection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet) dataConnection.getResult();

            ArrayList keys = new ArrayList();

            while(rs.next())
            {

                EJournalKey aKey = new EJournalKey();

                int index = 0;
                String storeID = getSafeString(rs, ++index);
                String regId = getSafeString(rs, ++index);
                int seqNo = rs.getInt(++index);
                String bizDate = getSafeString(rs, ++index);
                Timestamp beginTimestamp =  rs.getTimestamp(++index);
               
               
                aKey.initialize(storeID,
                                regId,
                                bizDate,
                                seqNo,
                                beginTimestamp.toString());
                
                keys.add(aKey);

            }

            rs.close();

            EJournalKeyIfc[] ejKeys = new EJournalKeyIfc[keys.size()];

            keys.toArray(ejKeys);

            dataTransaction.setResult(ejKeys);

        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR,
                                    "ReadJournalKeys",
                                    se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN,
                                    "ReadJournalKeys",
                                    e);
        }

    }

    /**
       Adds qualifiers to the input SQL select statement based on the criteria
       specified in the input Search object.
       <p>
       If start and end dates are provided, but start and end times are not, the
       start time defaults to 00:00:00 and the end time defaults to 23:59:59.
       <p>
       If start and end times are provided, the requester should also specify
       start and end dates.  If not, the system date will be used as the start
       and end date.
       @param  search       Search criteria class
       @param  sql          SQL select statement
     */
    public void addSearchQualifiers(SearchableIfc search, SQLSelectStatement sql)
        throws Exception
    {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");

        String  startDate   = dateFormatter.format(new Date()); // default to today
        String  endDate     = dateFormatter.format(new Date()); // default to today
        String  startTime   = "00:00:00";   // default - if start/end dates entered, but not s/e times
        String  endTime     = "23:59:59";   // default - if start/end dates entered, but not s/e times
        boolean dateOrTimeSpecified = false;

        int         items = search.getNumberSearchItems();

        String[][]  searchData = new String[items][2];
        int[][]     searchType = new int[items][1];
        int[][]     searchPos = new int[items][1];

        for (int j = 0; j < items; j++)
        {
            search.getSearchData(j, searchData[j], searchType[j], searchPos[j]);

            switch(searchPos[j][0])
            {
            
                case SearchableIfc.POSITION_TRANSACTION_NUMBER: // Transaction Number (last 4 of 12-char Receipt ID)      
                    String start =  extractTransactionNumber(searchData[j][0]);
                    String end =  extractTransactionNumber(searchData[j][1]);
                    sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " >= " + start);
                    sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " <= " + end);

                    break;

                case SearchableIfc.POSITION_DATE:               // Start and End date
                    dateOrTimeSpecified = true;
                    startDate = searchData[j][0];
                    endDate = searchData[j][1];
                    break;

                case SearchableIfc.POSITION_TIME:               // Start and End time
                    dateOrTimeSpecified = true;
                    startTime = searchData[j][0];
                    endTime = searchData[j][1];
                    break;

                case SearchableIfc.POSITION_OPERATOR_ID:        // User ID (aka Cashier ID or Operator ID)
                    sql.addQualifier(FIELD_OPERATOR_ID + " = " + makeSafeString(searchData[j][0]));
                    break;

                case SearchableIfc.POSITION_ASSOCIATE_ID:       // Associate ID
                    sql.addQualifier(FIELD_EMPLOYEE_ID + " = " + makeSafeString(searchData[j][0]));
                    break;

                case SearchableIfc.POSITION_REGISTER_NUMBER:    // Register Number
                    sql.addQualifier(FIELD_WORKSTATION_ID + " = " + makeSafeString(searchData[j][0]));
                    break;

            }

        }

        // Process Date and Time fields, if they were entered.  Pair StartDate with StartTime
        //  and End Date with End Time.
        if( dateOrTimeSpecified )
        {

            SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");

            EYSDate startDateTime = new EYSDate(dateTimeFormatter.parse(startDate + " " + startTime));
            EYSDate endDateTime = new EYSDate(dateTimeFormatter.parse(endDate + " " + endTime));
                    
            sql.addQualifier(FIELD_EJOURNAL_ENTRY_BEGIN_TIMESTAMP + " >= " + dateToSQLTimestampFunction(startDateTime));
            sql.addQualifier(FIELD_EJOURNAL_ENTRY_BEGIN_TIMESTAMP + " <= " + dateToSQLTimestampFunction(endDateTime));
            
        }
    }

    /**
     * Extract the transaction number.
     * <p>
     *
     * @param tn    The sequence number
     * @param  journalEntry        The journal entry to save
     * @return
     */
    private String extractTransactionNumber(String searchData)
    {
        // caution: this method assumes an order in the sequence number
        String returnValue = null;
        if(searchData != null)
        {
            int a = Integer.parseInt(DomainGateway.getProperty("TransactionIDStoreIDLength"));
            int b = Integer.parseInt(DomainGateway.getProperty("TransactionIDWorkstationIDLength"));
            int c = Integer.parseInt(DomainGateway.getProperty("TransactionIDSequenceNumberLength"));

            // If the code calling search was smart enough
            // to harvest out just the sequence number,
            // then simply return the value we were passed.
            //
            if(searchData.length() == c)
            {
                returnValue = searchData;
            }
            else
            {
                returnValue = searchData.substring((a + b), (a + b + c));
            }
        }

        return returnValue;

    }

}
