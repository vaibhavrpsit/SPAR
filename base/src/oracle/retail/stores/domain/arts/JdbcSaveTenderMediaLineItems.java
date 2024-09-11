/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveTenderMediaLineItems.java /main/15 2012/05/02 12:32:16 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  05/01/12 - Fortify: fix redundant null checks, part 3
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    06/08/09 - removed commented out code
 *    acadar    06/05/09 - save tender display description when doing a bank
 *                         deposit
 *    acadar    06/05/09 - changes for bank deposit report - save tender
 *                         description even when for summary counts
 *    acadar    06/05/09 - refactor the way tender media line items are read
 *                         from the database
 *    acadar    06/05/09 - dynamically add to the forein tender map
 *    acadar    12/08/08 - fix for till pickup receipt
 *    acadar    12/05/08 - fix for bank deposit defect
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/17/2008 2:36:47 PM   Deepti Sharma
 *         CR-31078:Changes to display correct values in POSLOG when Till
 *         pickup is done for Canadian Currency.
 *    4    360Commerce 1.3         1/25/2006 4:11:24 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:44 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:50 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:04 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:28:26    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:44     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:50     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:04     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:39  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:50  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:27  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:33:02   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:40:18   msg
 * Initial revision.
 *
 *    Rev 1.3   May 12 2002 23:40:08   mhr
 * db2 quote fixes.  chars/varchars must be quoted and ints/decimals must not be quoted.
 * Resolution for Domain SCR-50: db2 port fixes
 *
 *    Rev 1.2   May 09 2002 18:26:54   mpm
 * Completed re-factoring of store open/close transaction.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.1   Mar 18 2002 22:48:52   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:08:38   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;
// foundation imports

import org.apache.log4j.Logger;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.common.sql.SQLInsertStatement;

//-------------------------------------------------------------------------
/**
    This class is the data operation for saving tender media line items
    to the database.
    <P>
    @version $Revision: /main/15 $
**/
//-------------------------------------------------------------------------
public class JdbcSaveTenderMediaLineItems
extends JdbcDataOperation
implements ARTSDatabaseIfc
{
    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcSaveTenderMediaLineItems.class);

    //---------------------------------------------------------------------
    /**
        Class constructor.
     **/
    //---------------------------------------------------------------------
    public JdbcSaveTenderMediaLineItems()
    {
        super();
        setName("JdbcSaveTenderMediaLineItems");
    }

    //---------------------------------------------------------------------
    /**
        Execute the SQL statements against the database.
        <P>
        @param  dataTransaction     The data transaction
        @param  dataConnection      The connection to the data source
        @param  action              The information passed by the valet
        @exception DataException upon error
    **/
    //---------------------------------------------------------------------
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
                        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcSaveTenderMediaLineItems.execute()");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        TillAdjustmentTransactionIfc transaction =
          (TillAdjustmentTransactionIfc) action.getDataObject();

        try
        {
            saveTenderMediaLineItems(connection, transaction);
        }
        catch (Exception e)
        {
            System.out.println(Util.throwableToString(e));
        }

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcSaveTenderMediaLineItems.execute()");
    }

    //---------------------------------------------------------------------
    /**
        Saves the tender media line items. <P>
        @param  dataConnection  Data Source
        @param  transaction transaction object
        @exception DataException upon error
    **/
    //---------------------------------------------------------------------
    public void saveTenderMediaLineItems(JdbcDataConnection dataConnection,
                                         TillAdjustmentTransactionIfc transaction)
                                         throws DataException
    {
        FinancialCountTenderItemIfc fcti[] = null;
        FinancialTotalsIfc totals = transaction.getFinancialTotals();
        ReconcilableCountIfc[] rArray = null;
        FinancialCountIfc fc = null;
        // derive array of tender items from transaction
        switch(transaction.getTransactionType())
        {
            case TransactionIfc.TYPE_PICKUP_TILL:
                rArray = totals.getTillPickups();
                fc = rArray[rArray.length - 1].getEntered();
                fcti = fc.getTenderItems();
                break;
            case TransactionIfc.TYPE_LOAN_TILL:
                rArray = totals.getTillLoans();
                fc = rArray[rArray.length - 1].getEntered();
                fcti = fc.getTenderItems();
                break;
            default:
                break;
        }

        insertTenderMediaLineItems(dataConnection,
                                   transaction,
                                   fcti);
    }

    //---------------------------------------------------------------------
    /**
       Saves array of financial count tender items to database.  This is
       provided so that other transactions which have financial count
       tender items (other than a TillAdjustmentTransactionIfc) can
       utilize this data operation.
       @param dataConnection connection to database
       @param transaction transaction object
       @param fcti array of financial count tender items
       @exception DataException thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public void insertTenderMediaLineItems(JdbcDataConnection dataConnection,
                                           TransactionIfc transaction,
                                           FinancialCountTenderItemIfc[] fcti)
    throws DataException
    {                                   // begin insertTenderMediaLineItems()
        int numItems = 0;
        if (fcti != null)
        {
            numItems = fcti.length;
            for (int i = 0; i < numItems; i++)
            {
                insertTenderMediaLineItem(dataConnection,
                                          transaction,
                                          fcti[i],
                                          i);
            }
        }
    }                                   // end insertTenderMediaLineItems()

    //---------------------------------------------------------------------
    /**
        Inserts a tender media line item. <P>
        @param  dataConnection          Data source connection to use
        @param transaction transaction object
        @param  fcti FinancialCountTenderItemIfc object
        @param  lineItemSequenceNumber  The sequence number of the line item
                                        within a Transaction
        @exception DataException upon error
    **/
    //---------------------------------------------------------------------
    public void insertTenderMediaLineItem(JdbcDataConnection dataConnection,
                                          TransactionIfc transaction,
                                          FinancialCountTenderItemIfc fcti,
                                          int lineItemSequenceNumber)
                                          throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_TENDER_MEDIA_LINE_ITEM);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID,
                      inQuotes(transaction.getTransactionIdentifier().getStoreID()));
        sql.addColumn(FIELD_WORKSTATION_ID,
                      inQuotes(transaction.getTransactionIdentifier().getWorkstationID()));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER,
                      transaction.getTransactionIdentifier().getSequenceNumber());
        sql.addColumn(FIELD_BUSINESS_DAY_DATE,
                      dateToSQLDateString(transaction.getBusinessDay()));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER,
                      lineItemSequenceNumber);
        sql.addColumn(FIELD_TRANSACTION_TYPE_CODE,
                      "'" + Integer.toString(transaction.getTransactionType()) + "'");

        String tenderCode = DomainGateway.getFactory().getTenderTypeMapInstance().getCode((fcti.getTenderType()));

        sql.addColumn(FIELD_TENDER_TYPE_CODE, makeSafeString(tenderCode));
        sql.addColumn(FIELD_TENDER_MEDIA_AMOUNT_IN,
                      fcti.getAmountIn().getStringValue());
        sql.addColumn(FIELD_TENDER_MEDIA_AMOUNT_OUT,
                      fcti.getAmountOut().getStringValue());
        // changes to save country code
        if(fcti.getAmountIn()!= null )
        {
        	sql.addColumn(FIELD_TENDER_PICKUP_TENDER_COUNTRY_CODE,
        			inQuotes(fcti.getAmountIn().getType().getCountryCode().trim()));
        }
        else if(fcti.getAmountOut()!= null )
        {
        	sql.addColumn(FIELD_TENDER_PICKUP_TENDER_COUNTRY_CODE,
        			inQuotes(fcti.getAmountOut().getType().getCountryCode().trim()));
        }
        sql.addColumn(FIELD_TENDER_MEDIA_UNITS_IN,
                      fcti.getNumberItemsIn());
        sql.addColumn(FIELD_TENDER_MEDIA_UNITS_OUT,
                      fcti.getNumberItemsOut());
        String description = fcti.getSummaryDescription();

        sql.addColumn(FIELD_TENDER_MEDIA_SUMMARY_DESCRIPTION, inQuotes(description));
        sql.addColumn(FIELD_TENDER_MEDIA_SUMMARY_FLAG,
                      makeStringFromBoolean(fcti.isSummary()));
        sql.addColumn(FIELD_TENDER_MEDIA_DENOMINATIONS_FLAG,
                      makeStringFromBoolean(fcti.hasDenominations()));
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
        try
        {
        	dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error( "" + de + "");
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "insertTenderMediaLineItem", e);
        }
    }



}
