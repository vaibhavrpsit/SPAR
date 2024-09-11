/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadPayment.java /main/13 2012/05/21 15:50:18 cgreene Exp $
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
 *    asinton   10/27/11 - Prevent ArrayIndexOutOfBoundsException.
 *    mkutiana  08/17/11 - Removed deprecated Customer.ID_HSH_ACNT from DB and
 *                         all using classes
 *    cgreene   07/19/11 - store layaway and order ids in separate column from
 *                         house account number.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         2/7/2008 3:25:10 PM    Alan N. Sinton  CR
 *         30132: updated database (tr_ltm_pyan) to save encrypted, hashed,
 *         and masked house account card values.  Code was reviewed by Anil
 *         Bondalapati.
 *    4    360Commerce 1.3         1/25/2006 4:11:17 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:44 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:59 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:03    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:41     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:44     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:59     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:47  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:25  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:02   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:37:50   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:47:36   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:07:36   msg
 * Initial revision.
 *
 *    Rev 1.1   Feb 05 2002 16:33:38   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 *
 *    Rev 1.0   Sep 20 2001 16:00:04   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;


import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.StringUtils;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.PaymentConstantsIfc;
import oracle.retail.stores.domain.financial.PaymentIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * JdbcReadPayment implements the payment lookup JDBC data store operation. This
 * class reads a payment record by its transaction identifier.
 */
public class JdbcReadPayment extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -7717469957725071873L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadPayment.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
     * Class constructor.
     */
    public JdbcReadPayment()
    {
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
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
                        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadPayment.execute");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // look up the payment
        PaymentIfc inputPayment = (PaymentIfc) action.getDataObject();
        PaymentIfc payment = readPayment(connection, inputPayment);
        dataTransaction.setResult(payment);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadPayment.execute");
    }

    /**
     * Selects a payment from the payment table.
     * 
     * @param dataConnection a connection to the database
     * @param inputPayment payment containing key values
     * @return selected payment
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected PaymentIfc readPayment(JdbcDataConnection dataConnection,
                                     PaymentIfc inputPayment)
                                     throws DataException
    {
        PaymentIfc payment = null;

        // build SQL statement, execute and parse
        SQLSelectStatement sql = buildPaymentSQLStatement(inputPayment);
        try
        {
            ResultSet rs = execute(dataConnection, sql);
            payment = parsePaymentResultSet(rs);
            // set these fields off input payment
            payment.setTransactionID(inputPayment.getTransactionID());
            payment.setBusinessDate(inputPayment.getBusinessDate());
        }
        catch (DataException de)
        {
            logger.warn(de);
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "Payment lookup");
            throw new DataException(DataException.SQL_ERROR,
                                    "Payment lookup",
                                    se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN,
                                    "Payment lookup",
                                    e);
        }

        return(payment);
    }

    /**
     * Builds and returns SQL statement for retrieving a payment.
     * 
     * @param payment input payment
     * @return SQL select statement for retrieving a payment
     */
    protected SQLSelectStatement buildPaymentSQLStatement(PaymentIfc payment)
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        // add tables
        sql.addTable(TABLE_PAYMENTONACCOUNT_LINE_ITEM);
        // add select columns
        addPaymentSelectColumns(sql);
        // add qualifiers
        addPaymentQualifierColumns(sql,
                                   payment);
        return(sql);
    }

    /**
     * Adds select columns for payment lookup to SQL statement.
     * 
     * @param sql SQLSelectStatement object
     */
    protected void addPaymentSelectColumns(SQLSelectStatement sql)
    {
        sql.addColumn(FIELD_PAYMENT_AGAINST_RECEIVABLE_CUSTOMER_ACCOUNTID);
        sql.addColumn(FIELD_PAYMENT_AGAINST_RECEIVABLE_CARD_NUMBER_ENCRYPTED);
        sql.addColumn(FIELD_PAYMENT_AGAINST_RECEIVABLE_CARD_NUMBER_MASKED);
        sql.addColumn(FIELD_PAYMENT_AGAINST_RECEIVABLE_AMOUNT);
        sql.addColumn(FIELD_PAYMENT_AGAINST_RECEIVABLE_ACCOUNT_CODE);
        sql.addColumn(FIELD_ACCOUNT_BALANCE_DUE);
    }

    /**
     * Adds qualifiers for payment lookup to SQL statement.
     * 
     * @param sql SQLSelectStatement object
     * @param payment input payment
     */
    protected void addPaymentQualifierColumns(SQLSelectStatement sql,
                                              PaymentIfc payment)
    {
        TransactionIDIfc transactionID = payment.getTransactionID();
        // add payment ID to predicate
        sql.addQualifier(FIELD_RETAIL_STORE_ID,
                         inQuotes(transactionID.getStoreID()));
        sql.addQualifier(FIELD_WORKSTATION_ID,
                         inQuotes(transactionID.getWorkstationID()));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER,
                         Long.toString(transactionID.getSequenceNumber()));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE,
                dateToSQLDateString(payment.getBusinessDate()));
    }

    /**
     * Parse payment result set and returns payment object.
     * 
     * @param rs ResultSet object
     * @return PaymentIfc object
     * @exception thrown if error parsing result set
     */
    protected PaymentIfc parsePaymentResultSet(ResultSet rs) throws DataException, SQLException
    {
        PaymentIfc payment = null;
        try
        {
            if (rs.next())
            {
                payment = DomainGateway.getFactory().getPaymentInstance();
                int index = 0;
                // parse result set fields
                String accountID = getSafeString(rs, ++index);
                String encryptedCardNumber = getSafeString(rs, ++index);
                String maskedCardNumber = getSafeString(rs, ++index);
                payment.setReferenceNumber(accountID);
                if (StringUtils.isNotEmpty(encryptedCardNumber))
                {
                    EncipheredCardDataIfc cardData = FoundationObjectFactory.getFactory().createEncipheredCardDataInstance(
                            encryptedCardNumber,
                            maskedCardNumber,
                            null);
                    payment.setEncipheredCardData(cardData);
                }
                payment.setPaymentAmount(DomainGateway.getBaseCurrencyInstance(getSafeString(rs, ++index)));
                payment.setPaymentAccountType(getSafeString(rs, ++index));
                if(payment.getPaymentAccountType().equals(PaymentConstantsIfc.ACCOUNT_TYPE_HOUSE_ACCOUNT))
                {
                    payment.setDescription("House Account Payment");
                }
                payment.setBalanceDue
                    (DomainGateway.getBaseCurrencyInstance(getSafeString(rs, ++index)));

            }
            rs.close();
        }

        catch (SQLException se)
        {
            throw se;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "Payment lookup", e);
        }

        if (payment == null)
        {
            throw new DataException(DataException.NO_DATA,
                "No payment was found processing the result set in JdbcReadPayment.");
        }

        // close result set
        rs.close();

        return payment;
    }

    /**
     * Executes the SQL Statement.
     * 
     * @param dataConnection a connection to the database
     * @param sql the SQl statement
     * @param int id comparison basis type
     * @return result set
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected ResultSet execute(JdbcDataConnection dataConnection, SQLSelectStatement sql)
        throws DataException
    {
        ResultSet rs;
        String sqlString = sql.getSQLString();
        dataConnection.execute(sqlString);
        rs = (ResultSet) dataConnection.getResult();
        return rs;
    }

}
