/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveTillPickup.java /main/18 2012/05/21 15:50:19 cgreene Exp $
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
 *    ranojha   02/18/09 - Fixed import and export logic for TenderDescriptor
 *                         in till Pickup POSLog
 *    kulu      01/12/09 - Fix the bug that till pickup and till loan tender
 *                         type is not padded in Transaction Tracker
 *    kulu      01/12/09 - Fix the bug that Till Pickup and Till loan tender
 *                         type missing padding.
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/25/2007 1:57:58 PM   Ashok.Mondal
 *         Insert currencyID field to tender pickup transaction table.
 *    4    360Commerce 1.3         1/25/2006 4:11:24 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:45 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:50 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:04 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:56    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:45     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:50     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:04     Robert Pearse
 *
 *   Revision 1.7  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.6  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.5  2004/02/17 16:18:46  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.4  2004/02/13 23:07:40  dcobb
 *   @scr 3381 Feature Enhancement:  Till Pickup and Loan
 *   Add to/from register to database.
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
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
 *    Rev 1.1   Mar 18 2002 22:48:58   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:08:42   msg
 * Initial revision.
 *
 *    Rev 1.3   27 Feb 2002 12:43:26   epd
 * Updated to include Country Code for Till Pickups so alternate tenders are treated properly when voiding
 * Resolution for POS SCR-1416: Voiding a Canadian Till Pickup drops the amount into the Local bucket instead of updating the Canadian line
 *
 *    Rev 1.2   20 Nov 2001 16:18:04   adc
 * Changed tender ID field from integer to char
 * Resolution for Backoffice SCR-20: Till Pickup/Loan
 *
 *    Rev 1.1   12 Nov 2001 16:58:22   adc
 * Added the tillID column
 * Resolution for Backoffice SCR-20: Till Pickup/Loan
 *
 *    Rev 1.0   Sep 20 2001 15:56:52   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This class save the Till Pickup transaction info.
 * 
 * @version $Revision: /main/18 $
 */
public class JdbcSaveTillPickup extends JdbcSaveTransaction implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 894801792869663233L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveTillPickup.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/18 $";

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
        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveTillPickup.execute()");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // pull the data from the action object
        TillAdjustmentTransactionIfc transaction = (TillAdjustmentTransactionIfc)action.getDataObject();

        insertTransaction(connection, transaction);

        saveTillPickup(connection, transaction);

        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveTillPickup.execute()");
    }

    /**
       Inserts into the Financial Accounting transaction table.
       <P>
       @param  dataConnection  connection to the db
       @param  transaction     a TillAdjustmentTransactionIfc
       @exception DataException thrown when an error occurs.
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
            sql.addColumn(FIELD_TENDER_TYPE_CODE, getTenderType(transaction));
            sql.addColumn(FIELD_FINANCIAL_ACCOUNTING_TRANSACTION_TENDER_MEDIA_COUNT_TYPE,
                          transaction.getCountType());

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
        Saves the Till Pickup
        @param  dataConnection  connection to the db
        @param  transaction The Till Adjustment Transaction
        @exception DataException upon error
     */
    public void saveTillPickup(JdbcDataConnection dataConnection,
                               TillAdjustmentTransactionIfc transaction)
    throws DataException
    {
        try
        {
            insertFinancialAccountingTransaction(dataConnection, transaction);

            SQLInsertStatement sql = buildSQLStatement(transaction);
            dataConnection.execute(sql.getSQLString());
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "saveTillPickup", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "saveTillPickup", e);
        }
    }

    /**
        Builds SQL statement. <P>
        @return sql string
        @exception SQLException thrown if error occurs
     */
    protected SQLInsertStatement buildSQLStatement(TillAdjustmentTransactionIfc transaction)
    throws SQLException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // define tables, aliases
        sql.setTable(TABLE_TENDER_PICKUP_TRANSACTION);

        // add columns
        sql.addColumn(FIELD_TENDER_PICKUP_AMOUNT,
                      transaction.getAdjustmentAmount().getStringValue());
        sql.addColumn(FIELD_TENDER_PICKUP_TENDER_COUNTRY_CODE,
                      makeSafeString(transaction.getAdjustmentAmount().getCountryCode()));
        String tenderCode = DomainGateway.getFactory().getTenderTypeMapInstance().getCode(transaction.getTender().getTenderType());

        sql.addColumn(FIELD_TENDER_TYPE_CODE,
                      makeSafeString(tenderCode));
        sql.addColumn(FIELD_RETAIL_STORE_ID,
                      getStoreID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER,
                      getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE,
                getBusinessDayString(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID,
                      getWorkstationID(transaction));
        sql.addColumn(FIELD_TENDER_PICKUP_EXPECTED_AMOUNT,
                      transaction.getExpectedAmount().getStringValue());
        sql.addColumn(FIELD_TENDER_PICKUP_EXPECTED_COUNT,
                      Integer.toString(transaction.getExpectedCount()));
        sql.addColumn(FIELD_TENDER_PICKUP_TENDER_MEDIA_COUNT_TYPE,
                      Integer.toString(transaction.getCountType()));
        sql.addColumn(FIELD_TENDER_REPOSITORY_ID,
                       JdbcDataOperation.makeSafeString(transaction.getTillID()));
        sql.addColumn(FIELD_TENDER_PICKUP_TO_REGISTER_ID,
                       JdbcDataOperation.makeSafeString(transaction.getToRegister()));
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
        //+I18N
        sql.addColumn(FIELD_CURRENCY_ID, transaction.getAdjustmentAmount().getType().getCurrencyId());
        //-I18N

        return(sql);
    }

    /**
       Returns the type of tender for this adjustment.
       <p>
       @param  transaction the till adjustment transaction
       @return the type of tender line item.
     */
    public String getTenderType(TillAdjustmentTransactionIfc transaction)
    {
        return makeSafeString(DomainGateway.getFactory().getTenderTypeMapInstance().getCode(transaction.getTender().getTenderType()));
    }

    /**
        Retrieves the source-code-control system revision number. <P>
        @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return(revisionNumber);
    }
}
