/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveJournalEntry.java /main/19 2012/09/12 11:57:21 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   08/29/12 - Merge from project Echo (MPOS) into Trunk.
 *    cgreene   05/16/12 - arrange order of businessDay column to end of
 *                         primary key to improve performance since most
 *                         receipt lookups are done without the businessDay
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   06/15/11 - try to prevent index exceptions when parsing journal
 *                         number
 *    asinton   01/21/11 - Fix for timezone issue on the EJournal.
 *    cgreene   01/10/11 - refactor blob helpers into one
 *    cgreene   05/26/10 - convert to oracle packaging
 *    vapartha  02/17/10 - Added code to avoid insertion of empty ejournals
 *                         without sequence number or journal text.
 *    acadar    02/15/10 - forward port for spurkaya_8581588_303
 *    abondala  01/03/10 - update header date
 *    mahising  01/20/09 - fix ejournal issue when only customer added
 *    akandru   10/31/08 - EJ Changes_I18n
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         8/23/2006 2:49:08 PM   Brett J. Larsen CR
 *         20740 - oracle db port
 *
 *         intercepting null or empty strings and replacing with " " (oracle
 *         interprets empty strings as null values, and these are non-null
 *         columns)
 *
 *         changed date formatting to use db helper class
 *    4    360Commerce 1.3         1/25/2006 4:11:22 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:44 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:49 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:03 PM  Robert Pearse
 *:
 *    4    .v700     1.2.2.0     11/16/2005 16:25:56    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:44     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:49     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:03     Robert Pearse
 *
 *   Revision 1.14.2.1  2005/01/17 15:30:37  rsachdeva
 *   @scr 7837 ts_jrl_end getEndTimestamp() used
 *
 *   Revision 1.14  2004/07/31 20:00:30  kll
 *   @scr 6644: db2 syntax, remove quotes from integers
 *
 *   Revision 1.13  2004/07/29 16:26:24  kll
 *   @scr 6312: db2 support for inserting Blobs
 *
 *   Revision 1.12  2004/07/14 18:57:59  kll
 *   @scr 6234: index out of bounds error
 *
 *   Revision 1.11  2004/06/29 15:35:47  kll
 *   @scr 5895: retrieval of journal entries updated
 *
 *   Revision 1.10  2004/06/29 13:37:46  kll
 *   @scr 4400: usage of JournalManager's entry type to dictate whether Customer addition belongs inside or outside the context of a transaction
 *
 *   Revision 1.9  2004/06/19 14:35:23  kll
 *   @scr 5606: check for non-transaction indicating save as '0'
 *
 *   Revision 1.8  2004/06/15 13:13:10  kll
 *   @scr 5606: method to extract transaction number from sequence number format and journal entry
 *
 *   Revision 1.7  2004/06/07 22:57:48  kll
 *   @scr 5219, 5325: journal entry sql attempt not catching DataException
 *
 *   Revision 1.6  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:38  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:49  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:26  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Dec 17 2003 08:17:16   rrn
 * Initial revision.
 * Resolution for 3611: EJournal to database
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import oracle.retail.stores.common.data.JdbcUtilities;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.journal.JournalableIfc;
import oracle.retail.stores.foundation.manager.journal.JournalEntry;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;
import oracle.retail.stores.persistence.utility.DatabaseBlobHelperFactory;
import oracle.retail.stores.persistence.utility.DatabaseBlobHelperIfc;

import org.apache.log4j.Logger;

/**
 * This operation inserts a EJournal entry into the database.
 * 
 */
public class JdbcSaveJournalEntry extends JdbcDataOperation
    implements ARTSDatabaseIfc, CodeConstantsIfc
{
    private static final long serialVersionUID = 2198272991846159714L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveJournalEntry.class);

    private static final String[] journalTypes =
    {
        "UNKNOWN",                  // JournalableIfc.ENTRY_TYPE_UNKNOWN
        "TRANSACTION",              // JournalableIfc.ENTRY_TYPE_START
        "TRANSACTION",              // JournalableIfc.ENTRY_TYPE_END
        "TRANSACTION",              // JournalableIfc.ENTRY_TYPE_TRANS
        "NOT TRANS",                // JournalableIfc.ENTRY_TYPE_NOTTRANS
        "TRANSACTION"               // JournalableIfc.ENTRY_TYPE_CUST
    };

    private static final int COMPLETION_FLAG = 1;
    private static final int INCOMPLETION_FLAG = 0;

    /**
     * Class constructor.
     */
    public JdbcSaveJournalEntry()
    {
        super();
        setName("JdbcSaveJournalEntry");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveJournalEntry.execute");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;
        JournalEntry journalEntry = (JournalEntry) action.getDataObject();

        saveJournalEntry(connection, journalEntry);

        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveJournalEntry.execute");
    }

    /**
     * Saves the ejournal entry to the database.
     * 
     * @param dataConnection The connection to the data source
     * @param journalEntry The journal entry to save
     * @exception DataException upon error
     */
    public void saveJournalEntry(JdbcDataConnection dataConnection,
                                 JournalEntry journalEntry)
        throws DataException
    {
        try
        {
            SQLInsertStatement sql = new SQLInsertStatement();

            sql.setTable(TABLE_EJOURNAL);

            String storeID = journalEntry.getStoreNumber();
            if (storeID == null || storeID.equals(""))
            {
                storeID = " ";
            }
            sql.addColumn(FIELD_RETAIL_STORE_ID, inQuotes(storeID));

            String workstationId = journalEntry.getWorkstationID();
            if(workstationId == null || workstationId.equals(""))
            {
                workstationId = " ";
            }
            sql.addColumn(FIELD_WORKSTATION_ID, inQuotes(workstationId));

            sql.addColumn(FIELD_OPERATOR_ID, makeSafeString(journalEntry.getUser()));
            sql.addColumn(FIELD_EMPLOYEE_ID, makeSafeString(journalEntry.getSalesAssociateID()));


            int journalType = journalEntry.getEntryType();

            String transSeqNo = extractTransactionNumber(journalEntry);
            sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, transSeqNo);

            sql.addColumn(FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(journalEntry.getBusinessDate()));
            sql.addColumn(FIELD_EJOURNAL_ENTRY_BEGIN_TIMESTAMP, dateToSQLTimestampFunction(journalEntry.getTimestamp()));
            sql.addColumn(FIELD_EJOURNAL_ENTRY_END_TIMESTAMP, dateToSQLTimestampFunction(journalEntry.getEndTimestamp()));

            sql.addColumn(FIELD_EJOURNAL_JOURNAL_TYPE, makeSafeString(journalTypes[journalType]));


            // if journal entry type is not of END type then transaction is incomplete
            // thus flag in journal table is set to incompleted status
            // else its set to completed status.
            if (journalEntry.getEntryType() == JournalableIfc.ENTRY_TYPE_TRANS ||
                    journalEntry.getEntryType() == JournalableIfc.ENTRY_TYPE_START)
            {
                sql.addColumn(FIELD_EJOURNAL_FLAG_NORMAL_COMPLETION, INCOMPLETION_FLAG);
            }
            else
            {
                sql.addColumn(FIELD_EJOURNAL_FLAG_NORMAL_COMPLETION, COMPLETION_FLAG);
            }

            if(!transSeqNo.equals("0") || !journalEntry.getText().equals(""))
            {
            	dataConnection.execute(sql.getSQLString());
            }
            // resave the blob because 1st time doesn't work past 4000 bytes.
            HashMap<String,Object> map = new HashMap<String,Object>(5);
            map.put(FIELD_RETAIL_STORE_ID, storeID);
            map.put(FIELD_WORKSTATION_ID, workstationId);
            SimpleDateFormat format
                = new SimpleDateFormat(JdbcUtilities.YYYYMMDD_DATE_FORMAT_STRING);
            String dateSQL = format.format(journalEntry.getBusinessDate().dateValue());
            map.put(FIELD_BUSINESS_DAY_DATE, dateSQL);
            map.put(FIELD_TRANSACTION_SEQUENCE_NUMBER, new Integer(transSeqNo));
            map.put(FIELD_EJOURNAL_ENTRY_BEGIN_TIMESTAMP, journalEntry.getTimestamp().dateValue());
            DatabaseBlobHelperFactory factory = DatabaseBlobHelperFactory.getInstance();
            DatabaseBlobHelperIfc helper = factory.getDatabaseBlobHelper(dataConnection.getConnection());
            if(helper != null)
            {
                helper.updateClob(dataConnection.getConnection(),
                        TABLE_EJOURNAL,
                        FIELD_EJOURNAL_JOURNAL_ENTRY,
                        journalEntry.getText(),
                        map);
            }
        }
        catch (DataException de)
        {
            logger.error(journalEntry.toString(), de);
            throw de;
        }
        catch (SQLException se)
        {
            logger.error(journalEntry.toString(), se);
            throw new DataException(DataException.SQL_ERROR,
                                    "saveJournalEntry",
                                    se);
        }
        catch (Exception e)
        {
            logger.error(journalEntry.toString(), e);
            throw new DataException(DataException.UNKNOWN,
                                    "saveJournalEntry",
                                    e);
        }

    }

    /**
     * Extract the transaction number from the journal entry
     * and the sequence number format.
     *
     * @param tn    The sequence number
     * @param  journalEntry        The journal entry to save
     * @return
     */
    private String extractTransactionNumber(JournalEntry je)
    {
        // caution: this method assumes an order in the sequence number
        int journalType = je.getEntryType();
        if (journalType == JournalableIfc.ENTRY_TYPE_NOTTRANS || (je.getTransaction() == null)
                || (journalType == JournalableIfc.ENTRY_TYPE_CUST))
        {
            return "0";
        }

        String journal = je.getTransaction();
        if (!Util.isEmpty(journal))
        {
            int a = Integer.parseInt(DomainGateway.getProperty("TransactionIDStoreIDLength"));
            int b = Integer.parseInt(DomainGateway.getProperty("TransactionIDWorkstationIDLength"));
            int c = Integer.parseInt(DomainGateway.getProperty("TransactionIDSequenceNumberLength"));
            int begin = (a + b);
            int end = (a + b + c);
            if (journal.length() >= end)
            {
                // Return only the transaction number portion of the "transaction ID"
                // in the transaction_sequence_number field. But if this journal
                // entry is not part of a transaction (ENTRY_TYPE_NOTTRANS), return
                // "0" as the transaction number.
                return journal.substring(begin, end);
            }
            
            logger.warn("Unable to determine transaction sequence number from journal.");
        }
        
        return "0";
    }
}
