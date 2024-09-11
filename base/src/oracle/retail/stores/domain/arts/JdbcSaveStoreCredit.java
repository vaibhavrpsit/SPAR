/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveStoreCredit.java /main/20 2013/01/10 15:05:57 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   01/10/13 - Add business name for store credit and store credit
 *                         tender line tables.
 *    jswan     03/21/12 - Modified to support centralized gift certificate and
 *                         store credit.
 *    sgu       02/03/11 - check in all
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    abondala  11/06/08 - updated files related to reason codes
 *    abondala  11/05/08 - updated files related to the reason codes
 *                         CheckIDTypes and MailBankCheckIDTypes
 *
 * ===========================================================================
 * $Log:
 *    11   360Commerce 1.10        1/28/2008 4:31:09 PM   Sandy Gu
 *         Export foreign currency id, code and exchange rate for store credit
 *          and gift certificate foreign tender.
 *    10   360Commerce 1.9         5/30/2007 9:00:02 AM   Anda D. Cadar   code
 *         cleanup
 *    9    360Commerce 1.8         5/29/2007 12:26:54 PM  Ashok.Mondal
 *         Insert currencyID to store credit table.
 *    8    360Commerce 1.7         5/18/2007 9:16:35 AM   Anda D. Cadar   use
 *         decimalValue toString when saving amounts in the database
 *    7    360Commerce 1.6         12/12/2006 5:10:43 PM  Charles D. Baker CR
 *         3861 - Updated to clarify behavior - before could do insert with
 *         duplicate ID and then re-attempt with same ID again!
 *    6    360Commerce 1.5         4/5/2006 6:01:37 AM    Akhilashwar K. Gupta
 *         CR-3861: As per BA decision, reverted back the changes done earlier
 *          to fix the CR i.e. addition of following 4 fields in Store Credit
 *         and related code:
 *         - RetailStoreID
 *         - WorkstationID
 *         - TransactionSequenceNumber
 *         - BusinessDayDate
 *    5    360Commerce 1.4         3/15/2006 11:53:23 PM  Akhilashwar K. Gupta
 *         CR-3861: Modified  updateStoreCredit() and insertStoreCredit()
 *         methods
 *    4    360Commerce 1.3         1/25/2006 4:11:24 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:44 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:50 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:04 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:26:14    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:44     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:50     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:04     Robert Pearse
 *
 *   Revision 1.9  2004/08/09 14:03:53  kll
 *   @scr 6796: logging clean-up
 *
 *   Revision 1.8  2004/04/15 20:49:22  blj
 *   @scr 3871 - fixed problems with postvoid.
 *
 *   Revision 1.7  2004/03/24 17:06:37  blj
 *   @scr 3871-3872 - Added the ability to reprint redeem transaction receipts and added a void receipt.
 *
 *   Revision 1.6  2004/03/01 18:05:27  nrao
 *   Added method to use makeSafeString from JdbcUtilities.java.
 *
 *   Revision 1.5  2004/02/29 16:25:19  nrao
 *   Added new fields to the sql insert statement for first name, last name and id type.
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
 *    Rev 1.0   Aug 29 2003 15:32:58   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Jun 26 2003 15:59:40   vxs
 * Calling insertStoreCredit() inside catch{} of execute()
 *
 *    Rev 1.1   Jun 10 2002 11:14:56   epd
 * Merged in changes for Oracle
 * Resolution for Domain SCR-83: Merging database fixes into base code
 *
 *    Rev 1.4   Jun 07 2002 17:47:44   epd
 * Merging in fixes made for McDonald's Oracle demo
 * Resolution for Domain SCR-83: Merging database fixes into base code
 *
 *    Rev 1.3   May 12 2002 23:40:08   mhr
 * db2 quote fixes.  chars/varchars must be quoted and ints/decimals must not be quoted.
 * Resolution for Domain SCR-50: db2 port fixes
 *
 *    Rev 1.2   08 May 2002 16:39:46   vxs
 * Not abstract anymore, therefore added execute()
 * Resolution for POS SCR-1627: Correctly save store credit without breaking data transaction/data operation architecture
 *
 *    Rev 1.1   Mar 18 2002 22:48:48   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:08:34   msg
 * Initial revision.
 *
 *    Rev 1.1   09 Jan 2002 17:23:16   vxs
 * Store Credit training mode functionality in place.
 * Resolution for POS SCR-596: Store Credit package training mode updates
 *
 *    Rev 1.0   Sep 20 2001 15:57:06   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:58   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.transaction.RedeemTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.domain.utility.AbstractTenderDocumentIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.StoreCreditIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * This operation saves the store credit.
 *
 * @version $Revision: /main/20 $
 */
public class JdbcSaveStoreCredit extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -2939048266045970412L;

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/20 $";

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveStoreCredit.class);

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
        if (logger.isDebugEnabled()) logger.debug("JdbcSaveStoreCredit.execute()");

        // pull the data from the action object
        ARTSTransaction trans = (ARTSTransaction)action.getDataObject();
        TenderableTransactionIfc transaction = (TenderableTransactionIfc)trans.getPosTransaction();

        /*
         * Loop through each line item.
         * Continue through them all even if one has failed.
         */
        TenderLineItemIfc[] tenderLineItems = transaction.getTenderLineItems();
        for (TenderLineItemIfc lineItem: tenderLineItems)
        {
            if (lineItem instanceof TenderStoreCreditIfc)
            {
                saveStoreCredit((JdbcDataConnection)dataConnection, transaction,
                        (TenderStoreCreditIfc) lineItem);
            }
        }
        
        // If the transaction is a redeem transaction and the redeemed tender is 
        // a store credit, update the document. 
        if (transaction instanceof RedeemTransactionIfc)
        {
            RedeemTransactionIfc rTrans = (RedeemTransactionIfc)transaction;
            if (rTrans.getRedeemTender() instanceof TenderStoreCreditIfc)
            {
                saveStoreCredit((JdbcDataConnection)dataConnection, transaction,
                        (TenderStoreCreditIfc) rTrans.getRedeemTender());
            }
        }
        
        if (logger.isDebugEnabled()) logger.debug("JdbcSaveStoreCredit.execute()");
    }

    /**
     * Get the document from the tender line item, set the status and date,
     * and call the saveGiftCertificateDocument() method.
     * @param dataConnection
     * @param transaction
     * @param lineItem
     * @throws DataException
     */
    protected void saveStoreCredit(JdbcDataConnection dataConnection, TenderableTransactionIfc transaction,
            TenderStoreCreditIfc lineItem) throws DataException
    {
        StoreCreditIfc document = ((TenderStoreCreditIfc)lineItem).getStoreCredit();
        if (transaction instanceof VoidTransactionIfc)
        {
            document.setPreviousStatus(AbstractTenderDocumentIfc.ISSUED);
            document.setStatus(AbstractTenderDocumentIfc.VOIDED);
            document.setVoidDate(new EYSDate());
        }
        else
        if (lineItem.getAmountTender().signum() < 0)
        {
            document.setStatus(AbstractTenderDocumentIfc.ISSUED);
            document.setIssueDate(new EYSDate());
        }
        else
        {
            document.setPreviousStatus(AbstractTenderDocumentIfc.ISSUED);
            document.setStatus(AbstractTenderDocumentIfc.REDEEMED);
            document.setRedeemDate(new EYSDate());
        }
        
        if (updateStoreCredit(dataConnection, document) < 1)
        {
            insertStoreCreditDocument(dataConnection, document);
        }
    }

    /**
        Executes the update statement against the db.
        <P>
        @param  connection          a JdbcDataConnection object
        @param  store credit                the object to put in the DB.
        @exception DataException upon error
     */
    protected Integer updateStoreCredit(JdbcDataConnection connection, StoreCreditIfc sc)
                                throws DataException
    {
        // Put away the store credit record
        // Define the table
        SQLUpdateStatement sql = new SQLUpdateStatement();
        sql.setTable(TABLE_STORE_CREDIT);

        // Add columns and their values and qualifiers
        sql.addColumn(FIELD_STORE_CREDIT_STATUS, inQuotes(sc.getStatus()));
        sql.addColumn(FIELD_STORE_CREDIT_PREVIOUS_STATUS, makeSafeString(sc.getPreviousStatus()));
        if (sc.getIssueDate() != null)
        {
            sql.addColumn(FIELD_STORE_CREDIT_ISSUE_DATE, getSQLCurrentTimestampFunction());
        }
        else
        {
            sql.addColumn(FIELD_STORE_CREDIT_ISSUE_DATE, null);
        }
        if (sc.getRedeemDate() != null)
        {
            sql.addColumn(FIELD_STORE_CREDIT_REDEEM_DATE, getSQLCurrentTimestampFunction());
        }
        else
        {
            sql.addColumn(FIELD_STORE_CREDIT_REDEEM_DATE, null);
        }
        if (sc.getVoidDate() != null)
        {
            sql.addColumn(FIELD_STORE_CREDIT_VOID_DATE, getSQLCurrentTimestampFunction());
        }
        else
        {
            sql.addColumn(FIELD_STORE_CREDIT_VOID_DATE, null);
        }

        sql.addQualifier(FIELD_STORE_CREDIT_ID + " = " + makeSafeString(sc.getStoreCreditID()));

        // Execute the SQL statement
        connection.execute(sql.getSQLString());

        return connection.getUpdateCount();
    }

    /**
        Executes the insert statement against the db.
        <P>
        @param  connection          a Jdbcconnection object
        @param  store credit tender        the object to put in the DB.
        @exception DataException upon error
        @deprecated in 14.0; all updates/inserts will now be performed using the StoreCredit
        sc.
     */
    protected Integer insertStoreCredit(JdbcDataConnection connection, TenderStoreCreditIfc tsc)
                                        throws DataException
    {
    	// build insert SQL statement
    	SQLInsertStatement sql = buildInsertStoreCreditSQL(tsc);

        // Execute the SQL statement
        connection.execute(sql.getSQLString());

        if (connection.getUpdateCount() < 1)
        {
            logger.error( "Store Credit Insert count was not greater than 0");
            throw new DataException(DataException.UNKNOWN, "Store Credit Insert count was not greater than 0");
        }

        return Integer.valueOf(connection.getUpdateCount());
    }

    /**
     * Build the insert SQL statement based on the store credit tender
     * @param  store credit tender        the object to put in the DB.
     * @return the SQL statement
        @deprecated in 14.0; all updates/inserts will now be performed using the StoreCredit
        sc.
     */
    protected SQLInsertStatement buildInsertStoreCreditSQL(TenderStoreCreditIfc tsc)
    {
    	SQLInsertStatement sql = buildInsertStoreCreditSQL(tsc.getStoreCredit());
    	 //+I18N
        if (tsc.getAlternateCurrencyTendered() != null)
        {
            sql.addColumn(
                    FIELD_STORE_CREDIT_FOREIGN_FACE_VALUE_AMOUNT,
                    tsc.getAlternateCurrencyTendered().toString());
            sql.addColumn(FIELD_CURRENCY_ID, tsc.getAlternateCurrencyTendered().getType().getCurrencyId());
        }
        else
        {
            // NOT NULL field designation
            sql.addColumn(
                    FIELD_STORE_CREDIT_FOREIGN_FACE_VALUE_AMOUNT,
                    "0.00");
            sql.addColumn(FIELD_CURRENCY_ID, tsc.getAmount().getType().getCurrencyId());
        }
        //-I18N

        return sql;
    }

    /**
     * Insert values into the Store Credit Table
     * @param connection
     * @param sc
     */
    protected void insertStoreCreditDocument(JdbcDataConnection connection, StoreCreditIfc sc)
    throws DataException
    {
        // build insert SQL statement
        SQLInsertStatement sql = buildInsertStoreCreditSQL(sc);

        // Execute the SQL statement
        connection.execute(sql.getSQLString());
    }

    /**
     * Build the insert SQL statement based on the store credit
     * @param  store credit        the object to put in the DB.
     * @return the SQL statement
     */
    protected SQLInsertStatement buildInsertStoreCreditSQL(StoreCreditIfc sc)
    {
    	// Put away the store credit record
        // Define the table
        SQLInsertStatement sql = new SQLInsertStatement();
        sql.setTable(TABLE_STORE_CREDIT);

        // Add columns and their values and qualifiers
        sql.addColumn(FIELD_STORE_CREDIT_ID, inQuotes(sc.getStoreCreditID()));
        sql.addColumn(FIELD_STORE_CREDIT_BALANCE, sc.getAmount().getDecimalValue().toString());//toDecimalFormattedString());

        if (sc.getExpirationDate() == null)
        {
            sql.addColumn(FIELD_STORE_CREDIT_EXPIRATION_DATE, null);
        }
        else
        {
            sql.addColumn(FIELD_STORE_CREDIT_EXPIRATION_DATE, dateToSQLDateFunction(sc.getExpirationDate()));
        }
        sql.addColumn(FIELD_STORE_CREDIT_STATUS, inQuotes(sc.getStatus()));
        sql.addColumn(FIELD_STORE_CREDIT_FIRST_NAME, makeSafeString(sc.getFirstName()));
        sql.addColumn(FIELD_STORE_CREDIT_LAST_NAME, makeSafeString(sc.getLastName()));
        sql.addColumn(FIELD_STORE_CREDIT_BUSINESS_NAME, makeSafeString(sc.getBusinessName()));
        sql.addColumn(FIELD_STORE_CREDIT_ID_TYPE, makeSafeString(sc.getPersonalIDType().getCode()));
        sql.addColumn(FIELD_STORE_CREDIT_TRAINING_FLAG, makeStringFromBoolean(sc.isTrainingMode()));
        sql.addColumn(FIELD_CURRENCY_ID, sc.getAmount().getType().getCurrencyId());
        sql.addColumn(FIELD_CURRENCY_ISO_CODE, inQuotes(sc.getAmount().getType().getCurrencyCode()));
        sql.addColumn(FIELD_STORE_CREDIT_PREVIOUS_STATUS, makeSafeString(sc.getPreviousStatus()));
        if (sc.getIssueDate() != null)
        {
            sql.addColumn(FIELD_STORE_CREDIT_ISSUE_DATE, getSQLCurrentTimestampFunction());
        }
        else
        {
            sql.addColumn(FIELD_STORE_CREDIT_ISSUE_DATE, null);
        }
        if (sc.getRedeemDate() != null)
        {
            sql.addColumn(FIELD_STORE_CREDIT_REDEEM_DATE, getSQLCurrentTimestampFunction());
        }
        else
        {
            sql.addColumn(FIELD_STORE_CREDIT_REDEEM_DATE, null);
        }
        if (sc.getVoidDate() != null)
        {
            sql.addColumn(FIELD_STORE_CREDIT_VOID_DATE, getSQLCurrentTimestampFunction());
        }
        else
        {
            sql.addColumn(FIELD_STORE_CREDIT_VOID_DATE, null);
        }


        return sql;
    }

}
