/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcUpdateReturnedLineItems.java /main/16 2014/07/17 15:09:41 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   07/16/14 - Update return quantity for order take with item.
 *    mkutiana  05/02/13 - Handling NPE for return XC order transactions where
 *                         Business Date is null
 *    cgreene   05/21/12 - XbranchMerge cgreene_bug-13951397 from
 *                         rgbustores_13.5x_generic
 *    cgreene   05/16/12 - arrange order of businessDay column to end of
 *                         primary key to improve performance since most
 *                         receipt lookups are done without the businessDay
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
 *    6    360Commerce 1.5         6/10/2008 3:31:01 PM   Jack G. Swan
 *         Modified to check if transaction details are available before
 *         writing them to the queued exception file.
 *    5    360Commerce 1.4         5/16/2007 5:02:27 PM   Owen D. Horne
 *         CR#24874 - Merged fix from v8.0.1
 *         5    .v8x       1.3.1.0     4/10/2007 10:15:10 AM  Michael Wisbauer
 *         Modified how sequence numbers are being set mostly for the orginal
 *         transactions for refunds so items are updated correclty in the db.
 *    4    360Commerce 1.3         1/25/2006 4:11:27 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:46 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:53 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:06 PM  Robert Pearse   
 *:
 *    6    .v700     1.2.1.2     11/16/2005 16:28:29    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    5    .v700     1.2.1.1     11/7/2005 10:29:09     Deepanshu       CR
 *         6088: Changed method to retreive formatted store id & workstation id
 *         from transaction identifier
 *    4    .v700     1.2.1.0     11/1/2005 11:32:19     Rohit Sachdeva
 *         FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER
 *    3    360Commerce1.2         3/31/2005 15:28:46     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:53     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:06     Robert Pearse
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
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:25  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:33:36   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:41:26   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:50:04   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:09:36   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:57:48   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:40   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.util.Date;

import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * This operation reads a transaction in the POS from a database. It contains
 * the method that reads the transaction table in the database.
 * 
 * @version $Revision: /main/16 $
 */
public class JdbcUpdateReturnedLineItems extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -1189757875199611111L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcUpdateReturnedLineItems.class);

    /**
     * Class constructor.
     */
    public JdbcUpdateReturnedLineItems()
    {
        super();
        setName("JdbcUpdateReturnedLineItems");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdateReturnedLineItems.execute");

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

        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdateReturnedLineItems.execute");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdateReturnedLineItems.updateTransactions()");

        int numItems = 0;

        AbstractTransactionLineItemIfc[] lineItems =
            transaction.getLineItems();
        if (lineItems != null)
        {
            numItems = lineItems.length;
        }
        for (int i = 0; i < numItems; i++)
        {
            if (lineItems[i] instanceof SaleReturnLineItemIfc &&
                ((SaleReturnLineItemIfc) lineItems[i]).getQuantityReturnedDecimal().signum() > 0)
            {
                updateSaleReturnLineItem(dataConnection,
                                         (SaleReturnLineItemIfc) lineItems[i],
                                         transaction,
                                         lineItems[i].getLineNumber());
            }
        }
        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdateReturnedLineItems.updateTransactions()");
    }

    /**
     * Reads the sale return line items.
     * 
     * @param dataConnection a connection to the database
     * @param transaction the base transaction
     * @param index line item sequence number
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected void updateSaleReturnLineItem(JdbcDataConnection dataConnection,
                                            SaleReturnLineItemIfc item,
                                            SaleReturnTransactionIfc transaction,
                                            int index)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcUpdateReturnedLineItems.updateSaleReturnLineItem()");

        SQLUpdateStatement sql = new SQLUpdateStatement();

        /*
         * Add Table(s)
         */
        sql.setTable(TABLE_SALE_RETURN_LINE_ITEM);

        /*
         * Add Column(s)
         */
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_RETURN_QUANTITY,
                      item.getQuantityReturnedDecimal().toString());

        /*
         * Add Qualifier(s)
         */
        // For the specific transaction only
        if (transaction instanceof OrderTransactionIfc)
        {
            //Return a pickup or cancel order transaction
            if (item.getOrderItemStatus().getOriginalTransactionId()==null) 
            {
                //skip pickup or canncel order item
                return;
            }
            //update return quantity for take with items
            TransactionIDIfc origTxnID = item.getOrderItemStatus().getOriginalTransactionId();
            EYSDate origBusinessDate = item.getOrderItemStatus().getOriginalBusinessDate();
            
            sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + origTxnID.getStoreID());
            sql.addQualifier(FIELD_WORKSTATION_ID + " = " + origTxnID.getWorkstationID());
            sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER  + " = " + origTxnID.getSequenceNumber());
            sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " +  dateToSQLDateString(origBusinessDate));
            
            sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER
                    + " = " + String.valueOf(item.getOrderItemStatus().getOriginalLineNumber()));
        }
        else
        {
            sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getFormattedStoreID(transaction.getTransactionIdentifier()));
            sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction.getTransactionIdentifier()));
            sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER
                             + " = " + getTransactionSequenceNumber(transaction));
            sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
    
            sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER
                             + " = " + String.valueOf(index));
        }

        dataConnection.execute(sql.getSQLString());
        
        int count = dataConnection.getUpdateCount();
        if (count <= 0)
        {
            logger.info("The original transaction for transaction " + transaction.getTransactionIdentifier().getStoreID() + 
                        " is not available to update quantity returned.");
        }
        if (logger.isDebugEnabled()) logger.debug(
                "JdbcUpdateReturnedLineItems.selectSaleReturnLineItems");
       
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
     * Returns the length formatted SQL-formatted store ID from the transaction
     * ID object.
     * 
     * @param transactionID transaction ID object
     * @return the sql-formatted store ID
     */
    protected String getFormattedStoreID(TransactionIDIfc transactionID)
    {
        return (getStoreID(transactionID.getFormattedStoreID()));
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
     * Returns an Business date from transaction in SQl String format (SimpleDateFormat)
     */
    protected String getBusinessDayString(TransactionIfc trans)
    {
        Date businessDate = null;
        if (trans.getBusinessDay() != null)
        {
            businessDate = trans.getBusinessDay().dateValue();
        }
        return dateToSQLDateString(businessDate);
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
