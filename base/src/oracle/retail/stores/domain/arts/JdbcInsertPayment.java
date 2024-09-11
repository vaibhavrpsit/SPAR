/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcInsertPayment.java /main/16 2013/10/04 09:20:38 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   09/25/13 - EIT Defect - Incorrect deposit amount stored for
 *                         Pickup and Delivery orders with regular sale items
 *    sgu       03/20/13 - set payment amount including sale amount in PDO
 *    cgreene   05/21/12 - XbranchMerge cgreene_bug-13951397 from
 *                         rgbustores_13.5x_generic
 *    cgreene   05/16/12 - arrange order of businessDay column to end of
 *                         primary key to improve performance since most
 *                         receipt lookups are done without the businessDay
 *    cgreene   07/19/11 - store layaway and order ids in separate column from
 *                         house account number.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    npoola    01/27/09 - Fixed the PDO receipt and PDO payment totals in
 *                         TR_LTM_PYAN
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         2/7/2008 3:25:10 PM    Alan N. Sinton  CR
 *         30132: updated database (tr_ltm_pyan) to save encrypted, hashed,
 *         and masked house account card values.  Code was reviewed by Anil
 *         Bondalapati.
 *    4    360Commerce 1.3         1/25/2006 4:11:08 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:37 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:38 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:55 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:25:51    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:37     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:38     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:55     Robert Pearse
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
 *   Revision 1.3  2004/02/12 17:13:14  mcs
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
 *    Rev 1.0   Aug 29 2003 15:30:50   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:36:30   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:46:48   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:06:56   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:58:54   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:38   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdatableStatementIfc;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.financial.PaymentIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This operation inserts to the payment account line item table from the
 * PaymentIfc object.
 * 
 * @version $Revision: /main/16 $
 * @see oracle.retail.stores.domain.financial.PaymentIfc
 */
public class JdbcInsertPayment extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 543803619976301098L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcInsertPayment.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/16 $";

    /**
     * Class constructor.
     */
    public JdbcInsertPayment()
    {
        setName("JdbcInsertPayment");
    }

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    @Override
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        logger.debug("JdbcInsertPayment.execute()");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        PaymentIfc item = (PaymentIfc)action.getDataObject();
        insertPayment(connection, item);

        logger.debug("JdbcInsertPayment.execute()");
    }

    /**
     * Perform payment update. At this time, this method is not used.
     * 
     * @param dataConnection JdbcDataConnection
     * @param payment PaymentIfc reference
     * @exception DataException thrown if error occurs
     */
    public void updatePayment(JdbcDataConnection dataConnection, PaymentIfc payment) throws DataException
    {
        // build sql statement
        SQLUpdateStatement sql = new SQLUpdateStatement();
        // add table, columns, qualifiers
        sql.setTable(TABLE_PAYMENTONACCOUNT_LINE_ITEM);
        addUpdateColumns(payment, sql);
        addUpdateQualifiers(payment, sql);
        // execute statement
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e.toString());
            throw new DataException(DataException.UNKNOWN, "Payment update", e);
        }
    }

    /**
     * Perform payment insert.
     * 
     * @param dataConnection JdbcDataConnection
     * @param payment PaymentIfc reference
     * @exception DataException thrown if error occurs
     */
    public void insertPayment(JdbcDataConnection dataConnection, PaymentIfc payment) throws DataException
    {
        // build sql statement
        SQLInsertStatement sql = new SQLInsertStatement();
        // add table, columns, qualifiers
        sql.setTable(TABLE_PAYMENTONACCOUNT_LINE_ITEM);
        addInsertColumns(payment, sql);
        // execute statement
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e.toString());
            throw new DataException(DataException.UNKNOWN, "Payment insert", e);
        }

    }

    /**
     * Add update columns.
     * 
     * @param PaymentIfc payment object
     * @param sql SQLUpdateStatement
     */
    public void addUpdateColumns(PaymentIfc payment, SQLUpdatableStatementIfc sql)
    {
        sql.addColumn(FIELD_PAYMENT_AGAINST_RECEIVABLE_ACCOUNT_CODE, inQuotes(payment.getPaymentAccountType()));
        if(payment.isPDOPayment())
        {
            sql.addColumn(FIELD_PAYMENT_AGAINST_RECEIVABLE_AMOUNT, payment.getPickupDeliveryItemDepositAmount().toString());
        }
        else
        {
            sql.addColumn(FIELD_PAYMENT_AGAINST_RECEIVABLE_AMOUNT, payment.getPaymentAmount().toString());    
        }
        sql.addColumn(FIELD_PAYMENT_AGAINST_RECEIVABLE_CUSTOMER_ACCOUNTID, makeSafeString(payment.getReferenceNumber()));
        if (payment.getEncipheredCardData() != null)
        {
            sql.addColumn(FIELD_PAYMENT_AGAINST_RECEIVABLE_CARD_NUMBER_ENCRYPTED, makeSafeString(payment
                    .getEncipheredCardData().getEncryptedAcctNumber()));
            sql.addColumn(FIELD_PAYMENT_AGAINST_RECEIVABLE_CARD_NUMBER_MASKED, makeSafeString(payment
                    .getEncipheredCardData().getMaskedAcctNumber()));
        }

        sql.addColumn(FIELD_ACCOUNT_BALANCE_DUE, payment.getBalanceDue().toString());
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
    }

    /**
     * Add insert columns.
     * 
     * @param PaymentIfc payment object
     * @param sql SQLInsertStatement
     */
    public void addInsertColumns(PaymentIfc payment, SQLUpdatableStatementIfc sql)
    {
        TransactionIDIfc transactionID = payment.getTransactionID();
        sql.addColumn(FIELD_RETAIL_STORE_ID, makeSafeString(transactionID.getStoreID()));
        sql.addColumn(FIELD_WORKSTATION_ID, makeSafeString(transactionID.getWorkstationID()));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, Long.toString(transactionID.getSequenceNumber()));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(payment.getBusinessDate()));
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
        addUpdateColumns(payment, sql);
    }

    /**
     * Adds update qualifier columns to SQL statement.
     * 
     * @param PaymentIfc payment object
     * @param sql SQLUpdateStatement
     */
    public void addUpdateQualifiers(PaymentIfc payment, SQLUpdateStatement sql)
    {
        TransactionIDIfc transactionID = payment.getTransactionID();
        sql.addQualifier(FIELD_RETAIL_STORE_ID, makeSafeString(transactionID.getStoreID()));
        sql.addQualifier(FIELD_WORKSTATION_ID, makeSafeString(transactionID.getWorkstationID()));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER, Long.toString(transactionID.getSequenceNumber()));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(payment.getBusinessDate()));
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

    /**
     * Returns the string representation of this object.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        return (Util.classToStringHeader("JdbcInsertPayment", getRevisionNumber(), hashCode()).toString());
    }
}
