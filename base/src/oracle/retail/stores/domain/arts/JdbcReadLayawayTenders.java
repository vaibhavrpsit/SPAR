/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadLayawayTenders.java /main/10 2013/09/05 10:36:19 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       08/31/12 - convert payment to tender in xc order
 *    sgu       10/14/11 - read credit debit line item for charge card tender
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  1    360Commerce 1.0         4/30/2008 2:00:16 PM   Maisa De Camargo CR
 *       31328 - JDBC Class to Read all the tenders applied to a layaway. Code
 *        Reviewed by Jack Swan.
 * $
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.TenderUtility;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

import org.apache.log4j.Logger;

/**
 * This operation reads all the tender line items associated to a layaway
 * <P>
 *
 * @version $Revision: /main/10 $
 */
// --------------------------------------------------------------------------
public class JdbcReadLayawayTenders extends JdbcReadTransaction
{
    /**
     * The logger to which log messages will be sent.
     */
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcReadLayawayTenders.class);

    /**
     * Class constructor.
     */
    public JdbcReadLayawayTenders()
    {
        super();
        setName("JdbcReadLayawayTenders");
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
    // ----------------------------------------------------------------------
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadLayawayTenders.execute");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        LayawayTransactionIfc layawayTransaction = (LayawayTransactionIfc) action.getDataObject();
        TenderLineItemIfc[] tenderLineItems = readLayawayTenderLineItems(connection, layawayTransaction);
        dataTransaction.setResult(tenderLineItems);

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadLayawayTenders.execute");
    }

    // ----------------------------------------------------------------------
    /**
     * Reads all the payments applied to the layaway
     * <P>
     *
     * @param dataConnection a connection to the database
     * @param inputLayaway layaway containing key values
     * @return layaway updated with payments
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    // ----------------------------------------------------------------------
    protected TenderLineItemIfc[] readLayawayTenderLineItems(JdbcDataConnection dataConnection,
            TransactionIfc transaction) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadLayawayTenders.readLayawayTenderLineItems()");
        LayawayIfc layaway = ((LayawayTransactionIfc) transaction).getLayaway();

        SQLSelectStatement sql = new SQLSelectStatement();

        sql.addTable(TABLE_RETAIL_TRANSACTION, ALIAS_RETAIL_TRANSACTION);
        sql.addTable(TABLE_TENDER_LINE_ITEM, ALIAS_TENDER_LINE_ITEM);

        sql.addColumn(ALIAS_TENDER_LINE_ITEM, FIELD_LINE_ITEM_SEQUENCE_NUMBER);
        sql.addColumn(ALIAS_TENDER_LINE_ITEM, FIELD_TENDER_TYPE_CODE);
        sql.addColumn(ALIAS_TENDER_LINE_ITEM, FIELD_TENDER_LINE_ITEM_AMOUNT);

        sql.addJoinQualifier(ALIAS_RETAIL_TRANSACTION, FIELD_RETAIL_STORE_ID, ALIAS_TENDER_LINE_ITEM,
                FIELD_RETAIL_STORE_ID);
        sql.addJoinQualifier(ALIAS_RETAIL_TRANSACTION, FIELD_WORKSTATION_ID, ALIAS_TENDER_LINE_ITEM,
                FIELD_WORKSTATION_ID);
        sql.addJoinQualifier(ALIAS_RETAIL_TRANSACTION, FIELD_TRANSACTION_SEQUENCE_NUMBER, ALIAS_TENDER_LINE_ITEM,
                FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION, FIELD_LAYAWAY_ID, inQuotes(layaway.getLayawayID()));

        Vector<TenderLineItemIfc> tenderLineItems = new Vector<TenderLineItemIfc>(2);

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet) dataConnection.getResult();

            while (rs.next())
            {
                int index = 0;
                int sequenceNumber = rs.getInt(++index);
                String tenderType = getSafeString(rs, ++index);
                CurrencyIfc tenderAmount = getCurrencyFromDecimal(rs, ++index);

                TenderLineItemIfc tenderLineItem = TenderUtility.instantiateTenderLineItem(tenderType);
                tenderLineItem.setAmountTender(tenderAmount);
                if (tenderLineItem.getTypeCode() == TenderLineItemIfc.TENDER_TYPE_CHARGE)
                {
                    readCreditDebitTenderLineItem(dataConnection, transaction, (TenderChargeIfc)tenderLineItem, sequenceNumber);
                }
                tenderLineItems.addElement(tenderLineItem);

            }
            rs.close();

        }
        catch (SQLException exc)
        {
            dataConnection.logSQLException(exc, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR, "readLayawayTenderLineItems", exc);
        }

        TenderLineItemIfc[] lineItems = new TenderLineItemIfc[tenderLineItems.size()];
        tenderLineItems.copyInto(lineItems);

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadLayawayTenders.readLayawayTenderLineItems()");

        return (lineItems);
    }
}
