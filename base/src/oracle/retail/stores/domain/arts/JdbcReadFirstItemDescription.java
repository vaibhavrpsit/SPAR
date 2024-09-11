/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadFirstItemDescription.java /main/20 2012/09/17 15:27:13 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     09/13/12 - Modified to support deprecation of JdbcPLUOperation.
 *    cgreene   05/21/12 - XbranchMerge cgreene_bug-13951397 from
 *                         rgbustores_13.5x_generic
 *    cgreene   05/16/12 - arrange order of businessDay column to end of
 *                         primary key to improve performance since most
 *                         receipt lookups are done without the businessDay
 *    cgreene   09/15/11 - removed deprecated methods and changed static
 *                         methods to non-static
 *    cgreene   07/22/11 - update unittest inserts for id_acnt_nmb column
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    cgreene   09/25/09 - XbranchMerge cgreene_bug-8931126 from
 *                         rgbustores_13.1x_branch
 *    cgreene   09/24/09 - refactor SQL statements up support
 *                         preparedStatements for updates and inserts to
 *                         improve dept hist perf
 *    cgreene   03/01/09 - upgrade to using prepared statements for PLU
 *    sgu       10/30/08 - check in after refresh
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:11:15 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:40 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:43 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:58 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:28:01    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:40     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:43     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:58     Robert Pearse
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
 *    Rev 1.0   Aug 29 2003 15:31:46   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:37:16   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:45:40   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:05:38   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:58:34   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.UnknownItemIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * This operation reads a Description of the first item of a POS transaction
 * from a database. It contains the methods that read the transaction tables in
 * the database. It expects a data action a which contains a
 * TransactionSummaryIfc which has a TransactionID (consisting of a store id,
 * register id, and sequence number), and a business date.
 * 
 * @version $Revision: /main/20 $
 */
public class JdbcReadFirstItemDescription extends JdbcReadlocalizedDescription
                                 implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -6849278271315802873L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadFirstItemDescription.class);

    /**
     * Class constructor.
     */
    public JdbcReadFirstItemDescription()
    {
        super();
        setName("JdbcReadFirstItemDescription");
    }

    /**
     * Executes the SQL statements against the database. It expects a data
     * action a which contains a TransactionSummaryIfc which has a TransactionID
     * (consisting of a store id, register id, and sequence number), and a
     * business date.
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
        if (logger.isDebugEnabled()) logger.debug(
            "JdbcReadFirstItemDescription.execute");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // Send back the correct first item description (or lack thereof)
        TransactionSummaryIfc transactionSummary = (TransactionSummaryIfc)action.getDataObject();
        LocalizedTextIfc itemDescription = readFirstItemDescription(connection, transactionSummary, transactionSummary.getLocaleRequestor());

        dataTransaction.setResult(itemDescription);

        if (logger.isDebugEnabled()) logger.debug(
            "JdbcReadFirstItemDescription.execute");
    }

    /**
     * Retrieve the first line item and retrieve the description.
     * 
     * @param dataConnection a connection to the database
     * @return description of first line item
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    public LocalizedTextIfc readFirstItemDescription(JdbcDataConnection dataConnection,
                                              TransactionSummaryIfc summary,
                                              LocaleRequestor localeRequestor)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcReadTransaction.readFirstItemDescription()");

        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables
        sql.addTable(TABLE_SALE_RETURN_LINE_ITEM);

        // add columns
        sql.addColumn(FIELD_ITEM_ID);
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);

        // add qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID +  " = " + getStoreID(summary));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(summary));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getSequenceNumber(summary));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDate(summary));

        // add ordering
        sql.addOrdering(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);

        try
        {
            dataConnection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet)dataConnection.getResult();

            // we're only interested in the first one
            String itemID = null;
            int lineItemSequenceNumber = -1;
            if (rs.next())
            {
                int index = 0;
                itemID = getSafeString(rs, ++index);
                lineItemSequenceNumber = rs.getInt(++index);
            }

            rs.close();

            PLUItemIfc pluItem = null;
            try
            {
                pluItem = DomainGateway.getFactory().getPLUItemInstance();
                pluItem.setItemID(itemID);
                readLocalizedItemDescriptions(dataConnection, pluItem, localeRequestor);
            }
            catch (DataException de)
            {
                // If there's no PLUItem, then check UnknownItem
                pluItem = selectUnknownItem(dataConnection, summary, lineItemSequenceNumber, localeRequestor);
            }
            // Transaction summary uses item description of default locale
            summary.setLocalizedDescriptions(pluItem.getLocalizedDescriptions());
        }
        catch (SQLException exc)
        {
            dataConnection.logSQLException(exc, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR,
                                    "error processing sale return line items",
                                    exc);
        }

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcReadTransaction.readFirstItemDescription()");
        return(summary.getLocalizedDescriptions());
    }

    /**
     * Selects an item from the Unknown Item table.
     * 
     * @param dataConnection a connection to the database
     * @param summary a transaction summary
     * @param key the item lookup key
     * @return an unknown item
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected UnknownItemIfc selectUnknownItem(JdbcDataConnection dataConnection,
                                            TransactionSummaryIfc summary,
                                            int lineItemSequenceNumber,
                                            LocaleRequestor localeRequestor)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
            "JdbcReadTransactionHistory.selectUnknownItem()");

        // build a skeleton transaction and lineItem,
        // then call the real selectUnknownItem()
        TransactionIfc transaction = instantiateTransaction();
        transaction.initialize(summary.getTransactionID().getTransactionIDString());
        transaction.setBusinessDay(summary.getBusinessDate());
        AbstractTransactionLineItemIfc lineItem = instantiateSaleReturnLineItem();
        lineItem.setLineNumber(lineItemSequenceNumber);

        if (logger.isDebugEnabled()) logger.debug(
                "JdbcReadTransactionHistory.selectUnknownItem()");
        return(new JdbcReadTransaction().selectUnknownItem(dataConnection,
                                                     transaction,
                                                     lineItem,
                                                     localeRequestor));
    }

    /**
     * Returns the formatted store identifier from the summary.
     * 
     * @param summary TransactionSummaryIfc object
     * @return formatted store identifier
     */
    protected String getStoreID(TransactionSummaryIfc summary)
    {
        return ("'" + summary.getTransactionID().getStoreID() + "'");
    }

    /**
     * Returns the formatted workstation identifier from the summary.
     * 
     * @param summary TransactionSummaryIfc object
     * @return formatted workstation identifier
     */
    protected String getWorkstationID(TransactionSummaryIfc summary)
    {
        return ("'" + summary.getTransactionID().getWorkstationID() + "'");
    }

    /**
     * Returns the formatted sequence number from the summary.
     * 
     * @param summary TransactionSummaryIfc object
     * @return formatted sequence number
     */
    protected String getSequenceNumber(TransactionSummaryIfc summary)
    {
        return (Long.toString(summary.getTransactionID().getSequenceNumber()));
    }

    /**
     * Returns the formatted business date from the summary.
     * 
     * @param summary TransactionSummaryIfc object
     * @return formatted business date
     */
    protected String getBusinessDate(TransactionSummaryIfc summary)
    {
        return (dateToSQLDateString(summary.getBusinessDate().dateValue()));
    }

    /**
     * Instantiates an object implementing the SaleReturnLineItemIfc interface.
     * 
     * @return object implementing SaleReturnLineItemIfc
     */
    protected SaleReturnLineItemIfc instantiateSaleReturnLineItem()
    {
        return (DomainGateway.getFactory().getSaleReturnLineItemInstance());
    }

    /**
     * Instantiates an object implementing the TransactionIfc interface.
     * 
     * @return object implementing TransactionIfc
     */
    protected TransactionIfc instantiateTransaction()
    {
        return (DomainGateway.getFactory().getTransactionInstance());
    }

    /**
     * Instantiates an object implementing the TransactionSummaryIfc interface.
     * Convenience method for subclasses.
     * 
     * @return object implementing TransactionSummaryIfc
     */
    protected TransactionSummaryIfc instantiateTransactionSummary(TransactionIDIfc transactionID, EYSDate businessDate,
            LocaleRequestor localeRequestor)
    {
        TransactionSummaryIfc transactionSummary = DomainGateway.getFactory().getTransactionSummaryInstance();
        transactionSummary.setTransactionID(transactionID);
        transactionSummary.setBusinessDate(businessDate);
        transactionSummary.setLocaleRequestor(localeRequestor);
        return (transactionSummary);
    }

}
