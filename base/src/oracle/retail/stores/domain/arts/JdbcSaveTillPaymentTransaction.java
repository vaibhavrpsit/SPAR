/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveTillPaymentTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:01 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    crain     02/17/10 - Forward Port: ORPOS76327- PAY OUT TRANS
 *    abondala  01/03/10 - update header date
 *    ohorne    11/06/08 - TillPayment Tender Type now persisted as Code
 *                         instead of DisplayName. Deprecated
 *                         TillAdjustmentTransaction.tenderType, which has been
 *                         replaced with a TenderDescriptorIfc attribute.
 *    ohorne    11/03/08 - Localization of Till-related Reason Codes
 *
 * ===========================================================================
 *
 * $Log:
 *    7    360Commerce 1.6         5/30/2007 9:00:02 AM   Anda D. Cadar   code
 *         cleanup
 *    6    360Commerce 1.5         5/29/2007 5:50:49 PM   Ashok.Mondal
 *         Insert currencyID to funds receipt transaction.
 *    5    360Commerce 1.4         5/18/2007 9:16:35 AM   Anda D. Cadar   use
 *         decimalValue toString when saving amounts in the database
 *    4    360Commerce 1.3         1/25/2006 4:11:24 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:45 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:50 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:04 PM  Robert Pearse   
 *:
 *    5    .v700     1.2.1.1     11/16/2005 16:26:31    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    4    .v700     1.2.1.0     11/15/2005 10:27:53    Jason L. DeLeau 4207:
 *         Make private getter methods public.
 *    3    360Commerce1.2         3/31/2005 15:28:45     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:50     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:04     Robert Pearse
 *
 *   Revision 1.7  2004/07/22 04:56:31  khassen
 *   @scr 6296/6297/6298 - Updating pay in, pay out, payroll pay out:
 *   Adding database fields, print and reprint receipt functionality to reflect
 *   persistence of additional data in transaction.
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:45  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:22  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:33:04   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:40:24   msg
 * Initial revision.
 *
 *    Rev 1.2   16 May 2002 22:02:54   vxs
 * db2 port fixes, quotation issues with Strings/Integers
 *
 *    Rev 1.1   Mar 18 2002 22:48:56   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:08:40   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:59:32   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation inserts till adjustment data into the till accounting tables.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcSaveTillPaymentTransaction extends JdbcSaveTransaction implements ARTSDatabaseIfc
{

    private static final long serialVersionUID = -3195876545696237370L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveTillPaymentTransaction.class);

    /**
     * Class constructor.
     * <P>
     */
    public JdbcSaveTillPaymentTransaction()
    {
        super();
        setName("JdbcSaveTillPaymentTransaction");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveTillPaymentTransaction.execute()");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        TillAdjustmentTransactionIfc transaction =
                                    (TillAdjustmentTransactionIfc)action.getDataObject();

        insertTransaction(connection, transaction);

        switch (transaction.getTransactionType())
        {
            case TransactionIfc.TYPE_PAYIN_TILL:
                saveTillPayInTransaction(connection, transaction);
                break;
            case TransactionIfc.TYPE_PAYOUT_TILL:
                saveTillPayOutTransaction(connection, transaction);
                break;
            case TransactionIfc.TYPE_PAYROLL_PAYOUT_TILL:
                saveTillPayrollPayOutTransaction(connection, transaction);
                break;
            default:
                break;
        }

        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveTillPaymentTransaction.execute()");
    }

    public void saveTillPayInTransaction(JdbcDataConnection dataConnection,
                                       TillAdjustmentTransactionIfc transaction)
    throws DataException
    {
        try
        {
            insertFinancialAccountingTransaction(dataConnection, transaction);

            SQLInsertStatement sql = new SQLInsertStatement();

            // Table
            sql.setTable(TABLE_FUNDS_RECEIPT_TRANSACTION);

            // Fields
            sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
            sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
            sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
            sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
            sql.addColumn(FIELD_FUND_RECEIPT_MONETARY_AMOUNT, transaction.getAdjustmentAmount().getDecimalValue().toString());//toDecimalFormattedString());
            sql.addColumn(FIELD_DISBURSEMENT_RECEIPT_REASON_CODE, getReasonCode(transaction));
            sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP,getSQLCurrentTimestampFunction());
            sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,getSQLCurrentTimestampFunction());
            //+I18N
            sql.addColumn(FIELD_CURRENCY_ID, transaction.getAdjustmentAmount().getType().getCurrencyId());
            //-I18N

            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error( "Couldn't save TillPayment transaction.");
            logger.error(e);
            throw new DataException(DataException.UNKNOWN,"Couldn't save TillPayment transaction.",e);
        }
    }

    public void saveTillPayOutTransaction(JdbcDataConnection dataConnection,
            TillAdjustmentTransactionIfc transaction)
    throws DataException
    {
        try
        {
            insertFinancialAccountingTransaction(dataConnection, transaction);

            SQLInsertStatement sql = new SQLInsertStatement();

            // Table
            sql.setTable(TABLE_FUNDS_RECEIPT_TRANSACTION);

            // Fields
            sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
            sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
            sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
            sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
            sql.addColumn(FIELD_FUND_RECEIPT_MONETARY_AMOUNT, transaction.getAdjustmentAmount().getDecimalValue().toString());//toDecimalFormattedString());
            sql.addColumn(FIELD_DISBURSEMENT_RECEIPT_REASON_CODE, getReasonCode(transaction));
            // New fields:
            sql.addColumn(FIELD_TILL_PAYMENT_PAYEE_NAME, getPayeeName(transaction));
            sql.addColumn(FIELD_TILL_PAYMENT_ADDRESS_LINE_1, getAddressLine(transaction, 0));
            sql.addColumn(FIELD_TILL_PAYMENT_ADDRESS_LINE_2, getAddressLine(transaction, 1));
            sql.addColumn(FIELD_TILL_PAYMENT_ADDRESS_LINE_3, getAddressLine(transaction, 2));
            sql.addColumn(FIELD_TILL_PAYMENT_COMMENTS, getComments(transaction));
            sql.addColumn(FIELD_TILL_PAYMENT_APPROVAL_CODE, getApprovalCode(transaction));

            sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP,getSQLCurrentTimestampFunction());
            sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,getSQLCurrentTimestampFunction());
            //+I18N
            sql.addColumn(FIELD_CURRENCY_ID, transaction.getAdjustmentAmount().getType().getCurrencyId());
            //-I18N

            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error( "Couldn't save TillPayment transaction.");
            logger.error(e);
            throw new DataException(DataException.UNKNOWN,"Couldn't save TillPayment transaction.",e);
        }
    }

    public void saveTillPayrollPayOutTransaction(JdbcDataConnection dataConnection,
            TillAdjustmentTransactionIfc transaction)
    throws DataException
    {
        try
        {
            insertFinancialAccountingTransaction(dataConnection, transaction);

            SQLInsertStatement sql = new SQLInsertStatement();

            // Table
            sql.setTable(TABLE_FUNDS_RECEIPT_TRANSACTION);

            // Fields
            sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
            sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
            sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
            sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
            sql.addColumn(FIELD_FUND_RECEIPT_MONETARY_AMOUNT, transaction.getAdjustmentAmount().getDecimalValue().toString());//toDecimalFormattedString());
            sql.addColumn(FIELD_DISBURSEMENT_RECEIPT_REASON_CODE, getReasonCode(transaction));
            // New fields:
            sql.addColumn(FIELD_TILL_PAYMENT_PAYEE_NAME, getPayeeName(transaction));
            sql.addColumn(FIELD_TILL_PAYMENT_ADDRESS_LINE_1, getAddressLine(transaction, 0));
            sql.addColumn(FIELD_TILL_PAYMENT_ADDRESS_LINE_2, getAddressLine(transaction, 1));
            sql.addColumn(FIELD_TILL_PAYMENT_ADDRESS_LINE_3, getAddressLine(transaction, 2));
            sql.addColumn(FIELD_TILL_PAYMENT_COMMENTS, getComments(transaction));
            sql.addColumn(FIELD_TILL_PAYMENT_APPROVAL_CODE, getApprovalCode(transaction));
            sql.addColumn(FIELD_TILL_PAYMENT_EMPLOYEE_ID, getEmployeeID(transaction));

            sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP,getSQLCurrentTimestampFunction());
            sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,getSQLCurrentTimestampFunction());
            //+I18N
            sql.addColumn(FIELD_CURRENCY_ID, transaction.getAdjustmentAmount().getType().getCurrencyId());
            //-I18N

            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error( "Couldn't save TillPayment transaction.");
            logger.error(e);
            throw new DataException(DataException.UNKNOWN,"Couldn't save TillPayment transaction.",e);
        }
    }
    /**
     * @param transaction
     * @return employee ID
     */
    public String getEmployeeID(TillAdjustmentTransactionIfc transaction)
    {
        return makeSafeString(transaction.getEmployeeID());
    }

    /**
     * @param transaction
     * @return approval Code
     */
    public String getApprovalCode(TillAdjustmentTransactionIfc transaction)
    {
        return makeSafeString(transaction.getApproval().getCode());
    }

    /**
     * @param transaction
     * @return comments
     */
    public String getComments(TillAdjustmentTransactionIfc transaction)
    {
        return makeSafeString(transaction.getComments());
    }

    /**
     * @param transaction
     * @return address line
     */
    public String getAddressLine(TillAdjustmentTransactionIfc transaction, int i)
    {
        return makeSafeString(transaction.getAddressLine(i));
    }

    /**
     * @param transaction
     * @return payee name
     */
    public String getPayeeName(TillAdjustmentTransactionIfc transaction)
    {
        return makeSafeString(transaction.getPayeeName());
    }

    /**
     * Inserts into the Financial Accounting transaction table.
     * 
     * @param dataConnection connection to the db
     * @param transaction a TillAdjustmentTransactionIfc
     * @exception DataException thrown when an error occurs.
     */
    public void insertFinancialAccountingTransaction(JdbcDataConnection dataConnection,
                                                     TillAdjustmentTransactionIfc transaction)
    throws DataException
    {
        try
        {
            SQLInsertStatement sql = new SQLInsertStatement();

            // Table
            sql.setTable(TABLE_FINANCIAL_ACCOUNTING_TRANSACTION);

            // Fields
            sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
            sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
            sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
            sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
            sql.addColumn(FIELD_FINANCIAL_ACCOUNTING_TRANSACTION_TYPE_CODE, getTransactionType(transaction));
            sql.addColumn(FIELD_TENDER_TYPE_CODE, getTenderTypeCode(transaction));

            sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
            sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,getSQLCurrentTimestampFunction());

            dataConnection.execute(sql.getSQLString());

        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "saveTillPaymentTransaction", e);
        }
    }

    /**
     * Returns the amount of the till adjustment
     * 
     * @param transaction the till adjustment transaction
     * @return the string amount of the till adjustment
     */
    public String getAdjustmentAmount(TillAdjustmentTransactionIfc transaction)
    {
        return ("'" + transaction.getAdjustmentAmount() + "'");
    }

    /**
     * Returns the type of tender for this adjustment.
     * 
     * @param transaction the till adjustment transaction
     * @return the type of tender line item.
     * @deprecated As of release 13.1 use
     *             {@link #getTenderTypeCode(TillAdjustmentTransactionIfc)}
     */
    public String getTenderType(TillAdjustmentTransactionIfc transaction)
    {
        return getTenderTypeCode(transaction);
    }

    /**
     * Returns the tender type code for this adjustment.
     * 
     * @param transaction the till adjustment transaction
     * @return the code, or null if the tenderType cannot be determined
     * @since 13.1
     */
    public String getTenderTypeCode(TillAdjustmentTransactionIfc transaction)
    {
        String code = null;
        if (transaction.getTender() != null)
        {
            code = makeSafeString(DomainGateway.getFactory().
                    getTenderTypeMapInstance().getCode(transaction.getTender().getTenderType()));   
        }        
        return code;
    }

    /**
     * Returns the reason code for this adjustment.
     * 
     * @param transaction the till adjustment transaction
     * @return the reason code for the adjustment
     */
    public String getReasonCode(TillAdjustmentTransactionIfc transaction)
    {
        return makeSafeString(transaction.getReason().getCode());
    }
}
