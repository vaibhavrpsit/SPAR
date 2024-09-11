/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcUpdatePriceAdjustedLineItems.java /main/15 2012/05/21 15:50:20 cgreene Exp $
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
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/3/2007 11:57:43 PM   Sandy Gu
 *         Enhance transaction persistence layer to store inclusive tax
 *    4    360Commerce 1.3         1/25/2006 4:11:27 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:46 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:53 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:06 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:26:08    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:46     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:53     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:06     Robert Pearse
 *
 *   Revision 1.2  2004/04/28 19:51:45  jriggins
 *   @scr 3979 Code review cleanup
 *
 *   Revision 1.1  2004/04/20 12:49:25  jriggins
 *   @scr 3979 Added UpdatePriceAdjustedItemsDataTransaction and associated operations
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation updates the original sale line item of a transaction in order
 * to indicate that it has now been price adjusted
 * 
 * @version $Revision: /main/15 $
 * @deprecated has never been used; considering deletion
 */
public class JdbcUpdatePriceAdjustedLineItems extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -4171224143558098073L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcUpdatePriceAdjustedLineItems.class);

    /**
     * Class constructor.
     */
    public JdbcUpdatePriceAdjustedLineItems()
    {
        super();
        setName("JdbcUpdatePriceAdjustedLineItems");
    }

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction
     * @param dataConnection a connection to the database
     * @param action
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdatePriceAdjustedLineItems.execute");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        /*
         * Send back the correct transaction (or lack thereof)
         */
        SaleReturnTransactionIfc[] transactions =
            (SaleReturnTransactionIfc[]) action.getDataObject();
        for(int i = 0; i < transactions.length; i++)
        {
            updateTransaction(connection, transactions[i]);
        }

        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdatePriceAdjustedLineItems.execute");
    }

    /**
     * Reads all transactions between the specified reporting periods.
     * 
     * @param dataConnection a connection to the database
     * @param storeID The retail store ID
     * @param periods The reporting periods that begin and end the time period
     *            wanted.
     * @return The list of transactions.
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    public void updateTransaction(JdbcDataConnection dataConnection,
                                  SaleReturnTransactionIfc transaction)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdatePriceAdjustedLineItems.updateTransactions()");

        int numItems = 0;
        AbstractTransactionLineItemIfc[] lineItems =
            transaction.getLineItems();
        if (lineItems != null)
        {
            numItems = lineItems.length;
        }
        for (int i = 0; i < numItems; i++)
        {
            if (lineItems[i] instanceof SaleReturnLineItemIfc
                    && ((SaleReturnLineItemIfc) lineItems[i]).isPartOfPriceAdjustment()
                    && !((SaleReturnLineItemIfc) lineItems[i]).isReturnLineItem())
            {
                updatePriceAdjustedLineItem(dataConnection, (SaleReturnLineItemIfc) lineItems[i], transaction);
            }
        }
        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdatePriceAdjustedLineItems.updateTransactions()");
    }

    /**
     * Reads the sale return line items.
     * 
     * @param dataConnection a connection to the database
     * @param transaction the base transaction
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected void updatePriceAdjustedLineItem(JdbcDataConnection dataConnection, SaleReturnLineItemIfc item,
            SaleReturnTransactionIfc transaction)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcUpdatePriceAdjustedLineItems.updateSaleReturnLineItem()");

        SQLUpdateStatement sql = new SQLUpdateStatement();

        /*
         * Add Table(s)
         */
        sql.setTable(TABLE_SALE_RETURN_LINE_ITEM);

        /*
         * Add Column(s)
         */
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_AMOUNT,
                      item.getItemPrice().getExtendedSellingPrice().getStringValue());
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_DISCOUNTED_AMOUNT,
                      item.getItemPrice().getExtendedDiscountedSellingPrice().getStringValue());
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_VAT_AMOUNT, item.getItemTaxAmount().getStringValue());
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_TAX_INC_AMOUNT, item.getItemInclusiveTaxAmount().getStringValue());
        sql.addColumn(FIELD_ITEM_PRICEADJ_REFERENCE_ID, item.getPriceAdjustmentReference());

        /*
         * Add Qualifier(s)
         */
        // For the specific transaction only
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER
                         + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER
                         + " = " + String.valueOf(item.getOriginalLineNumber()));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));

        dataConnection.execute(sql.getSQLString());

        int count = dataConnection.getUpdateCount();
        if (count <= 0)
        {
            throw new DataException(DataException.NO_DATA, "Update SaleReturnLineItem");
        }

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcUpdatePriceAdjustedLineItems.selectSaleReturnLineItems");
    }

    /**
     */
    protected String getWorkstationID(SaleReturnTransactionIfc trans)
    {
        return getWorkstationID(trans.getWorkstation().getWorkstationID());
    }

    /**
     * Returns SQL-formatted workstation identifier from transaction ID object.
     * 
     * @param transactionID object
     * @return SQL-formatted workstation identifier
     */
    protected String getWorkstationID(TransactionIDIfc transactionID)
    {
        return (getWorkstationID(transactionID.getWorkstationID()));
    }

    /**
     * Returns SQL-formatted workstation identifier from string.
     * 
     * @param input string
     * @return SQL-formatted workstation identifier
     */
    protected String getWorkstationID(String input)
    {
        StringBuffer sb = new StringBuffer("'");
        sb.append(input);
        sb.append("'");
        return (sb.toString());
    }

    /**
     * Returns the store ID for the transaction
     * 
     * @param trans The transaction
     * @return the store ID
     */
    protected String getStoreID(TransactionIfc trans)
    {
        return ("'" + trans.getWorkstation().getStore().getStoreID() + "'");
    }

    /**
     * Returns the SQL-formatted store ID from the transaction ID object.
     * 
     * @param transactionID transaction ID object
     * @return the sql-formatted store ID
     */
    protected String getStoreID(TransactionIDIfc transactionID)
    {
        return (getStoreID(transactionID.getStoreID()));
    }

    /**
     * Returns the store ID
     * 
     * @param storeID The store ID
     * @return the store ID
     */
    protected String getStoreID(String storeID)
    {
        return ("'" + storeID + "'");
    }

    /**
     */
    protected String getBusinessDayString(TransactionIfc trans)
    {
        return (dateToSQLDateString(trans.getBusinessDay().dateValue()));
    }

    /**
     * Returns the transaction sequence number
     * 
     * @param transaction a pos transaction
     * @return The transaction sequence number
     */
    public String getTransactionSequenceNumber(TransactionIfc transaction)
    {
        return (String.valueOf(transaction.getTransactionSequenceNumber()));
    }

}
