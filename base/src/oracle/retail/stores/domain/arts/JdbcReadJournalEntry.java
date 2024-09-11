/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadJournalEntry.java /main/16 2012/05/21 15:50:18 cgreene Exp $
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
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    akandru   10/31/08 - EJ Changes_I18n
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         8/23/2006 2:40:01 PM   Brett J. Larsen CR
 *         20740 - oracle db port - changed date formatting to use db helper
 *         class
 *    4    360Commerce 1.3         1/25/2006 4:11:16 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:44 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:59 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.2.0     11/16/2005 16:28:06    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:41     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:44     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:59     Robert Pearse
 *
 *   Revision 1.7  2004/08/06 20:42:05  jdeleau
 *   @scr 6779 Make sure e-journal entries can be saved and retrieved as
 *   blob types.
 *
 *   Revision 1.6  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:38  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:47  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:26  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Dec 17 2003 08:17:12   rrn
 * Initial revision.
 * Resolution for 3611: EJournal to database
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.journal.EJournalKeyIfc;

/**
 * This operation reads a EJournal entry from the database.
 * 
 * @version $Revision: /main/16 $
 */
public class JdbcReadJournalEntry extends JdbcDataOperation implements ARTSDatabaseIfc, CodeConstantsIfc
{
    private static final long serialVersionUID = 639531314422329418L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadJournalEntry.class);

    /**
     * Class constructor.
     */
    public JdbcReadJournalEntry()
    {
        super();
        setName("JdbcReadJournalEntry");
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

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadJournalEntry.execute");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;
        EJournalKeyIfc key = (EJournalKeyIfc) action.getDataObject();

        readJournalEntry(dataTransaction, connection, key);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadJournalEntry.execute");
    }

    /**
     * Reads the ejournal transaction(s) from the database
     * 
     * @param dataTransaction The data transaction object
     * @param dataConnection The connection to the data source
     * @param key Entry key
     * @exception DataException upon error
     */
    public void readJournalEntry(DataTransactionIfc dataTransaction,
                                 JdbcDataConnection dataConnection,
                                 EJournalKeyIfc key)
        throws DataException
    {

        String journalEntry = null;

        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables
        sql.addTable(TABLE_EJOURNAL);

        // add column
        sql.addColumn(FIELD_EJOURNAL_JOURNAL_ENTRY);

        // add qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + makeSafeString(key.getStoreID()));
        sql.addQualifier(FIELD_WORKSTATION_ID  + " = " + makeSafeString(key.getRegisterID()));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + String.valueOf(key.getTransactionNumber()));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + makeSafeString(key.getBusinessDate()));
        Timestamp bdate = Timestamp.valueOf(key.getBeginTimestamp());
        sql.addQualifier(FIELD_EJOURNAL_ENTRY_BEGIN_TIMESTAMP + " = " + dateToSQLTimestampFunction(bdate));

        try
        {
            dataConnection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet) dataConnection.getResult();

            if( rs.next() )
            {
//                journalEntry = getBlobStringFromResultSet(rs);
            	journalEntry = rs.getString(FIELD_EJOURNAL_JOURNAL_ENTRY);
                /* Blob blob = rs.getBlob(1);
                journalEntry = new String(blob.getBytes(1L, (int)blob.length())); */
            }

            rs.close();

            dataTransaction.setResult(journalEntry);

        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR,
                                    "ReadJournalEntry",
                                    se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN,
                                    "ReadJournalEntry",
                                    e);
        }

    }

}
