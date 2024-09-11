/* ===========================================================================
* Copyright (c) 2009, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rahravin  02/24/14 - Modified FIELD_BILLPAY_TOTAL_PAYMENT_COLLECTED in
 *                         case of cancelled transactions and null checked on
 *                         FIELD_BILLPAY_PAYMENT_COLLECTED
 *    mchellap  08/24/10 - Billpay datamodel changes
 *    mchellap  07/08/10 - Added customer name and account id to bill line item
 *                         table
 *    nkgautam  07/06/10 - bill pay report changes
 *    nkgautam  06/23/10 - bill pay changes
 *    nkgautam  06/21/10 - initial version
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.math.BigDecimal;
import java.util.ArrayList;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.Bill;
import oracle.retail.stores.domain.financial.BillIfc;
import oracle.retail.stores.domain.transaction.BillPayTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

import org.apache.log4j.Logger;

public class JdbcSaveBillPayTransaction extends JdbcSaveRetailTransaction
{

    private static final long serialVersionUID = 1L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveBillPayTransaction.class);

    /**
     * Class constructor.
     */
    public JdbcSaveBillPayTransaction()
    {
        super();
        setName("JdbcSaveBillPayTransaction");
    }

    /**
     * Executes the SQL statements against the database.
     * <P>
     *
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */

    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
    throws DataException
    {

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // Navigate the input object to obtain values that will be inserted into
        // the database
        ARTSTransaction artsTransaction = (ARTSTransaction) action.getDataObject();
        saveBillPayTransaction(connection, (BillPayTransactionIfc) artsTransaction.getPosTransaction());

    }

    /**
     * Save billpay transaction to bill tables.
     * <P>
     *
     * @param dataConnection The connection to the data source
     * @param transaction The billpayment transaction
     * @exception DataException upon error
     */
    private void saveBillPayTransaction(JdbcDataConnection connection, BillPayTransactionIfc transaction)
            throws DataException
    {

        try
        {
            insertRetailTransaction(connection, transaction);
            insertBillPayTransaction(connection, transaction);
        }
        catch (DataException de)
        {
            logger.error("" + de + "");
            throw de;
        }
        catch (Exception e)
        {
            logger.error("Couldn't save retail transaction.");
            logger.error("" + e + "");
            throw new DataException(DataException.UNKNOWN, "Couldn't save retail transaction.", e);
        }

    }

    /**
     * Inserts billpay transaction details to billpayment table.
     * <P>
     *
     * @param dataConnection The connection to the data source
     * @param transaction The billpayment transaction
     * @exception DataException upon error
     */
    private void insertBillPayTransaction(JdbcDataConnection connection, BillPayTransactionIfc transaction)
            throws DataException
    {

        SQLInsertStatement sql = new SQLInsertStatement();
        ArrayList<BillIfc> billList = transaction.getBillPayInfo().getBillsList();

        if (billList != null && billList.size() != 0)
        {
            sql.setTable(TABLE_BILL_PAY);

            // Fields
            sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
            sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
            sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, transaction.getTransactionSequenceNumber());
            sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
            sql.addColumn(FIELD_BILLPAY_ACCOUNT_NUMBER, makeSafeString(transaction.getBillPayInfo().getAccountNumber()));
            sql.addColumn(FIELD_BILLPAY_CUSTOMER_NAME, makeSafeString(transaction.getBillPayInfo().getFirstLastName()));
            if (transaction.getTransactionStatus() != TransactionIfc.STATUS_CANCELED)
            {
            sql.addColumn(FIELD_BILLPAY_TOTAL_PAYMENT_COLLECTED, getTotalPaymentCollected(transaction));
            }
            sql.addColumn(FIELD_BILLPAY_PAYMENT_DATE, getSQLCurrentTimestampFunction());
            
            try
            {
                connection.execute(sql.getSQLString());
                insertBillPayTransactionBillLineItems(connection, transaction);
            }
            catch (DataException de)
            {
                logger.error(de.toString());
                throw de;
            }
            catch (Exception e)
            {
                logger.error(e.toString());
                throw new DataException(DataException.UNKNOWN, "insertBillPayTransaction", e);
            }
        }

    }

    /**
     * Inserts billpayment bill details to billpayment line item table.
     * <P>
     *
     * @param dataConnection The connection to the data source
     * @param transaction The billpayment transaction
     * @exception DataException upon error
     */
    private void insertBillPayTransactionBillLineItems(JdbcDataConnection connection, BillPayTransactionIfc transaction)
    throws DataException
    {

        SQLInsertStatement sql = new SQLInsertStatement();
        ArrayList<BillIfc> billList = transaction.getBillPayInfo().getBillsList();
        if (billList != null && billList.size() != 0)
        {
            sql.setTable(TABLE_BILL_PAY_LINE_ITEM);
            int lineItemNumber = 1;
            for (int i = 0; i < billList.size(); i++)
            {
                Bill billDetails = (Bill) billList.get(i);
                // Fields
                sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
                sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
                sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, transaction.getTransactionSequenceNumber());
                sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
                sql.addColumn(FIELD_BILLPAY_BILL_LINE_ITEM_SEQUENCE_NUMBER, lineItemNumber++);

                String accountNumber = transaction.getBillPayInfo().getAccountNumber();
                sql.addColumn(FIELD_BILLPAY_ACCOUNT_NUMBER, makeSafeString(accountNumber));

                // If the parent is paying for the child account, save the child's account details.
                if (!accountNumber.equalsIgnoreCase(billDetails.getAccountNumber()))
                {
                    sql.addColumn(FIELD_BILLPAY_CHILD_ACCOUNT_NUMBER, makeSafeString(billDetails.getAccountNumber()));
                    sql.addColumn(FIELD_BILLPAY_CHILD_CUSTOMER_NAME, makeSafeString(billDetails.getCustomerName()));
                }

                sql.addColumn(FIELD_BILLPAY_BILL_NUMBER, makeSafeString(billDetails.getBillNumber()));
                
                if(billDetails.getBillAmountPaid() != null)
                {
                sql.addColumn(FIELD_BILLPAY_PAYMENT_COLLECTED, billDetails.getBillAmountPaid().toString());
                }
                
                sql.addColumn(FIELD_BILLPAY_BILL_DUE_DATE, dateToSQLDateFunction(billDetails.getDueDate()));

                try
                {
                    connection.execute(sql.getSQLString());
                }
                catch (DataException de)
                {
                    logger.error(de.toString());
                    throw de;
                }
                catch (Exception e)
                {
                    logger.error(e.toString());
                    throw new DataException(DataException.UNKNOWN, "insertBillPayTransaction", e);
                }
             }
        }
      }

    /**
     * Returns total amount collected for billpayment.
     * <P>
     *
     * @param dataConnection The connection to the data source
     * @param transaction The billpayment transaction
     * @return String The total amount collected
     */
    public String getTotalPaymentCollected(BillPayTransactionIfc transaction)
    {
        ArrayList<BillIfc> billList = transaction.getBillPayInfo().getBillsList();
        CurrencyIfc totalPayment = DomainGateway.getBaseCurrencyInstance(new BigDecimal(0.0));

        for (BillIfc bill : billList)
        {
            totalPayment = totalPayment.add(bill.getBillAmountPaid());
        }

        return totalPayment.getStringValue();
    }

}

