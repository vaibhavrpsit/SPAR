/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveStore.java /main/22 2013/02/27 15:01:18 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     02/20/13 - Modified for Currency Rounding.
 *    mjwallac  05/01/12 - Fortify: fix redundant null checks, part 3
 *    nkgautam  07/28/10 - Bill Payment Report changes
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    cgreene   10/05/09 - XbranchMerge cgreene_bug8931126-storehist3 from
 *                         rgbustores_13.1x_branch
 *    cgreene   10/05/09 - un-override methods used by super-implementations
 *    cgreene   09/30/09 - XbranchMerge cgreene_bug8931126-storehist2 from
 *                         rgbustores_13.1x_branch
 *    cgreene   09/30/09 - switch the columns that use SYSDATE to use
 *                         SQLParameterFunction as parameter
 *    cgreene   09/25/09 - Refactor to use PreparedStatements
 *    cgreene   09/25/09 - refactored to use preparedstatements
 *    jswan     08/13/09 - Added the till reconcile amount and count to the
 *                         till, workstation and store tender history tables
 *                         and to code that reads from and writes to them.
 *
 * ===========================================================================
 * $Log:
 *    7    360Commerce 1.6         5/16/2007 7:55:27 PM   Brett J. Larsen
 *         CR 26903 - 8.0.1 merge to trunk
 *
 *         BackOffice <ARG> Summary Report overhaul (many CRs fixed)
 *         
 *    6    360Commerce 1.5         5/14/2007 6:08:34 PM   Sandy Gu
 *         update inclusive information in financial totals and history tables
 *    5    360Commerce 1.4         4/25/2007 10:01:09 AM  Anda D. Cadar   I18N
 *         merge
 *    4    360Commerce 1.3         1/25/2006 4:11:24 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:44 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:49 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:04 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:26:34    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:44     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:49     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:04     Robert Pearse
 *
 *   Revision 1.19  2004/07/30 21:04:04  dcobb
 *   @scr 6462 Financial Totals are not correct for the detail count during Till Open/Reconcile
 *   Replaced all instances of FinancialCountIfc.getTenderItem(int, String) with getSummaryTenderItemByDescriptor(TenderDescriptorIfc).
 *   Also replaced deprecated call with FinancialCountIfc.getAmountGrossTransactionEmployeeDiscount() and getUnitsGrossTransactionEmployeeDiscount().
 *
 *   Revision 1.18  2004/06/18 22:56:43  cdb
 *   @scr 4205 Corrected problems caused by searching financial counts
 *   by tender description rather than tender descriptor - which caused problems
 *   with foreign currencies.
 *
 *   Revision 1.17  2004/06/15 00:44:30  jdeleau
 *   @scr 2775 Support register reports and financial totals with the new
 *   tax engine.
 *
 *   Revision 1.16  2004/05/12 15:03:57  jdeleau
 *   @scr 4218 Remove GrossTransactionDiscount Amounts, Units, UnitsVoid,
 *   and AmountVoids in favor of the already existing AmountTransactionDiscounts
 *   and NumberTransactionDiscounts, which end up already being NET totals.
 *
 *   Revision 1.15  2004/05/11 23:03:01  jdeleau
 *   @scr 4218 Backout recent changes to remove TransactionDiscounts,
 *   going to go a different route and remove the newly added
 *   voids and grosses instead.
 *
 *   Revision 1.13  2004/04/28 19:41:40  jdeleau
 *   @scr 4218 Add StoreCreditsIssued (count/amount and voided counts and amounts)
 *   to Financial Totals.
 *
 *   Revision 1.12  2004/04/27 20:01:17  jdeleau
 *   @scr 4218 Add in the concrete calls for register reports data, refactor
 *   the houseCardEnrollment methods to be in line with other FinancialTotals
 *   methods.
 *
 *   Revision 1.11  2004/04/26 21:07:22  jdeleau
 *   @scr 4218 Put calls to new Financial Totals data in register reports,
 *   correct error in ArtsDatabaseIfc
 *
 *   Revision 1.10  2004/04/26 18:23:40  jdeleau
 *   @scr 4128 JDBC changes to support new data required for register reports
 *
 *   Revision 1.9  2004/04/07 20:56:49  lzhao
 *   @scr 4218: add gift card info for summary report.
 *
 *   Revision 1.8  2004/04/05 23:03:01  jdeleau
 *   @scr 4218 JavaDoc fixes associated with RegisterReports changes
 *
 *   Revision 1.7  2004/04/02 23:07:33  jdeleau
 *   @scr 4218 Register Reports - House Account and initial changes to
 *   the way SummaryReports are built.
 *
 *   Revision 1.6  2004/02/25 22:53:37  crain
 *   @scr 3814 Issue Gift Certificate
 *
 *   Revision 1.5  2004/02/17 17:57:38  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:47  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:26  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:58   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Jul 15 2003 11:29:06   sfl
 * Store the void counts into database to support store report.
 * Resolution for POS SCR-3165: Register Reports -  Store "Net Trans Tax Count " line is not correct.
 *
 *    Rev 1.3   Feb 15 2003 17:26:00   mpm
 * Merged 5.1 changes.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.2   Feb 11 2003 17:30:18   sfl
 * Optimized the calculation formula in getItemTaxAmount method.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.1   11 Jun 2002 16:25:06   jbp
 * changes to report markdowns
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.0   Jun 03 2002 16:40:10   msg
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLParameterFunction;
import oracle.retail.stores.common.sql.SQLParameterValue;
import oracle.retail.stores.common.sql.SQLUpdatableStatementIfc;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.ReconcilableCount;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.financial.ReportingPeriodIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;

/**
 * This operation performs inserts into the store history, store tender history,
 * and reporting period tables.
 * 
 * @version $Revision: /main/22 $
 */
public abstract class JdbcSaveStore extends JdbcSaveReportingPeriod implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -5843813283156182434L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveStore.class);
    private static final CurrencyTypeIfc BASE_TYPE = DomainGateway.getFactory().getCurrencyTypeInstance();
    private static final CurrencyIfc ZERO = DomainGateway.getFactory().getCurrencyInstance(BASE_TYPE);

    // Tender type map
    protected static TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();

    /**
     * Adds the financial totals to the store history record.
     * 
     * @param dataConnection connection to the db
     * @param store The retail store
     * @param businessDate The business date
     * @param totals The financial totals information
     * @exception DataException thrown when an error occurs.
     */
    public void addStoreHistory(JdbcDataConnection dataConnection, StoreIfc store, EYSDate businessDate,
            FinancialTotalsIfc totals) throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Define the table
        sql.setTable(TABLE_STORE_HISTORY);

        // Add columns and their values
        addFinancialTotals(sql, totals);

        // Add Qualifier(s)
        sql.addQualifier(new SQLParameterValue(FIELD_RETAIL_STORE_ID, store.getStoreID()));
        sql.addQualifier(new SQLParameterValue(FIELD_FISCAL_YEAR, getFiscalYearCode(businessDate)));
        sql.addQualifier(new SQLParameterValue(FIELD_REPORTING_PERIOD_TYPE_CODE, getReportingPeriodTypeCode(businessDate)));
        sql.addQualifier(new SQLParameterValue(FIELD_REPORTING_PERIOD_ID, getReportingPeriod(businessDate)));

        dataConnection.execute(sql.getSQLString(), sql.getParameterValues());

        if (0 >= dataConnection.getUpdateCount())
        {
            // insert the record
            insertStoreHistory(dataConnection, store, businessDate, totals);
        }

        addStoreTenders(dataConnection, store, businessDate, totals);
    }

    /**
     * Updates or inserts all of the tender entries in the store financial
     * totals.
     * 
     * @param dataConnection The connection to the data source
     * @param store The store
     * @param businessDate The business date
     * @param totals The financial totals information to add
     * @return true on success, false on failure
     * @throws DataException upon error
     */
    public boolean addStoreTenders(JdbcDataConnection dataConnection,
                                StoreIfc store,
                                EYSDate businessDate,
                                FinancialTotalsIfc totals)
                                throws DataException
    {
        boolean returnCode = true;

        // Do this once
        ReconcilableCountIfc loans = combineByTenderType(totals.getTillLoans());
        ReconcilableCountIfc pickups = combineByTenderType(totals.getTillPickups());
        ReconcilableCountIfc payIns = combineByTenderType(totals.getTillPayIns());
        ReconcilableCountIfc payOuts = combineByTenderType(totals.getTillPayOuts());

        // Walk through each tender item and save it
        FinancialCountTenderItemIfc[] tenderTypes = getTenderTypes(totals.getCombinedCount());

        for (int i = 0; i < tenderTypes.length; ++i)
        {
            if (!addStoreTenderHistory(dataConnection, store, businessDate, totals, tenderTypes[i], loans, pickups, payIns, payOuts))
            {
                if (!insertStoreTenderHistory(dataConnection, store, businessDate, totals, tenderTypes[i], loans, pickups, payIns, payOuts))
                {
                    returnCode = false;
                }
            }
        }

        return(returnCode);
    }

    /**
     * Adds the financial tender totals to the store tender history records.
     * 
     * @param dataConnection connection to the db
     * @param store The retail store
     * @param businessDate The business date
     * @param totals The financial totals information
     * @param tenderItem The tender item
     * @param loans till loans
     * @param pickups till pickups
     * @param payIns till pay-ins
     * @param payOuts till pay-outs
     * @return true on success, false on failure
     * @throws DataException thrown when an error occurs.
     */
    public boolean addStoreTenderHistory(JdbcDataConnection dataConnection,
                                      StoreIfc store,
                                      EYSDate businessDate,
                                      FinancialTotalsIfc totals,
                                      FinancialCountTenderItemIfc tenderItem,
                                      ReconcilableCountIfc loans,
                                      ReconcilableCountIfc pickups,
                                      ReconcilableCountIfc payIns,
                                      ReconcilableCountIfc payOuts)
                                      throws DataException
    {
        boolean returnCode = false;
        String tenderType = tenderTypeMap.getCode(tenderItem.getTenderType());

        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Define the table
        sql.setTable(TABLE_STORE_TENDER_HISTORY);

        // Add columns and their values
        addTenderTotals(sql, totals, tenderItem, loans, pickups, payIns, payOuts);
        sql.addColumn(new SQLParameterFunction(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction()));

        // Add Qualifier(s)
        sql.addQualifier(new SQLParameterValue(FIELD_RETAIL_STORE_ID, store.getStoreID()));
        sql.addQualifier(new SQLParameterValue(FIELD_FISCAL_YEAR, getFiscalYearCode(businessDate)));
        sql.addQualifier(new SQLParameterValue(FIELD_REPORTING_PERIOD_TYPE_CODE, getReportingPeriodTypeCode(businessDate)));
        sql.addQualifier(new SQLParameterValue(FIELD_REPORTING_PERIOD_ID, getReportingPeriod(businessDate)));
        sql.addQualifier(new SQLParameterValue(FIELD_TENDER_TYPE_CODE, tenderType));
        sql.addQualifier(new SQLParameterValue(FIELD_TENDER_SUBTYPE, emptyStringToSpaceString(tenderItem.getTenderSubType())));
        sql.addQualifier(new SQLParameterValue(FIELD_CURRENCY_ISSUING_COUNTRY_CODE, tenderItem.getCurrencyCode()));

        dataConnection.execute(sql.getSQLString(), sql.getParameterValues());

        if (0 < dataConnection.getUpdateCount())
        {
            returnCode = true;
        }

        return returnCode;
    }

    /**
     * Updates the store history table with status information only, not
     * financial totals.
     * 
     * @param dataConnection connection to the db
     * @param status the store status
     * @exception DataException thrown when an error occurs.
     */
    public void updateStoreHistory(JdbcDataConnection dataConnection,
                                   StoreStatusIfc status)
                                   throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Define the table
        sql.setTable(TABLE_STORE_HISTORY);

        // Add columns and their values
        sql.addColumn(new SQLParameterValue(FIELD_STORE_HISTORY_STATUS_CODE, getStoreStatusCode(status)));
        sql.addColumn(new SQLParameterValue(FIELD_OPERATOR_ID, getOperatorID(status)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_START_DATE_TIMESTAMP, getStartTime(status)));
        CurrencyIfc curr = DomainGateway.getBaseCurrencyInstance();
        sql.addColumn(new SQLParameterValue(FIELD_CURRENCY_ID, curr.getType().getCurrencyId()));

        // Add Qualifier(s)
        EYSDate rp = getReportingPeriod(status);
        sql.addQualifier(new SQLParameterValue(FIELD_RETAIL_STORE_ID, getStoreID(status)));
        sql.addQualifier(new SQLParameterValue(FIELD_FISCAL_YEAR, getFiscalYearCode(status.getBusinessDate())));
        sql.addQualifier(new SQLParameterValue(FIELD_REPORTING_PERIOD_TYPE_CODE, getReportingPeriodTypeCode(rp)));
        sql.addQualifier(new SQLParameterValue(FIELD_REPORTING_PERIOD_ID, getReportingPeriod(rp)));

        // if this is a request to open or close the store, use additional qualifier to
        // ensure that store was not already opened or closed, respectively, from another register
        switch(status.getStatus())
        {
            case AbstractFinancialEntityIfc.STATUS_RECONCILED:
                sql.addQualifier(new SQLParameterValue(FIELD_STORE_HISTORY_STATUS_CODE, AbstractFinancialEntityIfc.STATUS_OPEN));
                break;
            default:
                break;
        }

        try
        {
            dataConnection.execute(sql.getSQLString(), sql.getParameterValues());
        }
        // if attempting to open store and referential integrity exception occurs,
        // it will be ignored.  This could happen because of two registers opening
        // a store (there is some latency because it's a queued transaction).
        // Such an occurrence is not a cause for alarm.
        catch (DataException de)
        {
            if (de.getErrorCode() == DataException.REFERENTIAL_INTEGRITY_ERROR &&
                status.getStatus() == AbstractFinancialEntityIfc.STATUS_OPEN)
            {
                logger.warn(
                            "Referential integrity error on store open is ignored.");
            }
            else
            {
                throw de;
            }
        }

        // if no rows updated and not store open, throw no-data exception
        if (0 >= dataConnection.getUpdateCount() &&
            status.getStatus() != AbstractFinancialEntityIfc.STATUS_OPEN)
        {
            throw new DataException(DataException.NO_DATA, "Update Store History");
        }
    }

    /**
     * Updates the store history table with financial totals.
     * 
     * @param dataConnection connection to the db
     * @param status the store status
     * @param totals Financial totals to update to history
     * @exception DataException thrown when an error occurs.
     */
    public void updateStoreHistory(JdbcDataConnection dataConnection,
                                   StoreStatusIfc status,
                                   FinancialTotalsIfc totals)
                                   throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Define the table
        sql.setTable(TABLE_STORE_HISTORY);

        // Add columns and their values
        sql.addColumn(new SQLParameterValue(FIELD_STORE_HISTORY_STATUS_CODE, getStoreStatusCode(status)));
        sql.addColumn(new SQLParameterValue(FIELD_OPERATOR_ID, getOperatorID(status)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_START_DATE_TIMESTAMP, getStartTime(status)));
        CurrencyIfc curr = DomainGateway.getBaseCurrencyInstance();
        sql.addColumn(new SQLParameterValue(FIELD_CURRENCY_ID, curr.getType().getCurrencyId()));

        updateFinancialTotals(sql, totals);

        // Add Qualifier(s)
        EYSDate rp = getReportingPeriod(status);
        sql.addQualifier(new SQLParameterValue(FIELD_RETAIL_STORE_ID, getStoreID(status)));
        sql.addQualifier(new SQLParameterValue(FIELD_FISCAL_YEAR, getFiscalYearCode(status.getBusinessDate())));
        sql.addQualifier(new SQLParameterValue(FIELD_REPORTING_PERIOD_TYPE_CODE, getReportingPeriodTypeCode(rp)));
        sql.addQualifier(new SQLParameterValue(FIELD_REPORTING_PERIOD_ID, getReportingPeriod(rp)));

        dataConnection.execute(sql.getSQLString(), sql.getParameterValues());

        if (0 >= dataConnection.getUpdateCount())
        {
            throw new DataException(DataException.NO_DATA, "Update Store History");
        }
    }

    /**
     * Inserts into the store history table.
     * 
     * @param dataConnection connection to the db
     * @param store The store to insert store history for
     * @param businessDate The date
     * @param totals Data to insert
     * @exception DataException thrown when an error occurs.
     */
    protected void insertStoreHistory(JdbcDataConnection dataConnection,
                                      StoreIfc store,
                                      EYSDate businessDate,
                                      FinancialTotalsIfc totals)
                                      throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Define table
        sql.setTable(TABLE_STORE_HISTORY);

        // Add columns and their values
        sql.addColumn(new SQLParameterValue(FIELD_RETAIL_STORE_ID, store.getStoreID()));
        sql.addColumn(new SQLParameterValue(FIELD_FISCAL_YEAR, getFiscalYearCode(businessDate)));
        sql.addColumn(new SQLParameterValue(FIELD_REPORTING_PERIOD_TYPE_CODE, getReportingPeriodTypeCode(businessDate)));
        sql.addColumn(new SQLParameterValue(FIELD_REPORTING_PERIOD_ID, getReportingPeriod(businessDate)));
        CurrencyIfc curr = DomainGateway.getBaseCurrencyInstance();
        sql.addColumn(new SQLParameterValue(FIELD_CURRENCY_ID, curr.getType().getCurrencyId()));

        if (totals == null)
        {
            // set timestamps
            sql.addColumn(new SQLParameterFunction(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction()));
            sql.addColumn(new SQLParameterFunction(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction()));
        }
        else
        {
            updateFinancialTotals(sql, totals);
        }

        dataConnection.execute(sql.getSQLString(), sql.getParameterValues());

        if (totals != null)
        {
            insertStoreTenders(dataConnection, store, businessDate, totals);
        }
    }

    /**
     * Inserts all of the tender entries in the store financial totals.
     * 
     * @param dataConnection The connection to the data source
     * @param store The store
     * @param businessDate The business date
     * @param totals The financial totals information to add
     * @return true on success, false on failure
     * @throws DataException upon error
     */
    public boolean insertStoreTenders(JdbcDataConnection dataConnection,
                                   StoreIfc store,
                                   EYSDate businessDate,
                                   FinancialTotalsIfc totals)
                                   throws DataException
    {
        boolean returnCode = true;

        // Do this once
        ReconcilableCountIfc loans = combineByTenderType(totals.getTillLoans());
        ReconcilableCountIfc pickups = combineByTenderType(totals.getTillPickups());
        ReconcilableCountIfc payIns = combineByTenderType(totals.getTillPayIns());
        ReconcilableCountIfc payOuts = combineByTenderType(totals.getTillPayOuts());

        // Walk through each tender item and save it
        FinancialCountTenderItemIfc[] tenderTypes = getTenderTypes(totals.getCombinedCount());

        for (int i = 0; i < tenderTypes.length; ++i)
        {
            if (!insertStoreTenderHistory(dataConnection, store, businessDate, totals, tenderTypes[i], loans, pickups, payIns, payOuts))
            {
                returnCode = false;
            }
        }

        return(returnCode);
    }

    /**
     * Inserts the financial tender totals into the store tender history table.
     * 
     * @param dataConnection connection to the db
     * @param store The retail store
     * @param businessDate The business date
     * @param totals The financial totals information
     * @param tenderItem The type item
     * @param loans The combined tender loans
     * @param pickups The combined tender pickups
     * @param payIns combined till pay-ins
     * @param payOuts combined till pay-outs
     * @return true on success, false on failure
     * @throws DataException thrown when an error occurs.
     */
    public boolean insertStoreTenderHistory(JdbcDataConnection dataConnection,
                                         StoreIfc store,
                                         EYSDate businessDate,
                                         FinancialTotalsIfc totals,
                                         FinancialCountTenderItemIfc tenderItem,
                                         ReconcilableCountIfc loans,
                                         ReconcilableCountIfc pickups,
                                         ReconcilableCountIfc payIns,
                                         ReconcilableCountIfc payOuts)
                                         throws DataException
    {
        boolean returnCode = false;
        String tenderType = tenderTypeMap.getCode(tenderItem.getTenderType());
        SQLInsertStatement sql = new SQLInsertStatement();

        // Define the table
        sql.setTable(TABLE_STORE_TENDER_HISTORY);

        // Add Columns
        sql.addColumn(new SQLParameterValue(FIELD_RETAIL_STORE_ID, store.getStoreID()));
        sql.addColumn(new SQLParameterValue(FIELD_FISCAL_YEAR, getFiscalYearCode(businessDate)));
        sql.addColumn(new SQLParameterValue(FIELD_REPORTING_PERIOD_TYPE_CODE, getReportingPeriodTypeCode(businessDate)));
        sql.addColumn(new SQLParameterValue(FIELD_REPORTING_PERIOD_ID, getReportingPeriod(businessDate)));
        sql.addColumn(new SQLParameterValue(FIELD_TENDER_TYPE_CODE, tenderType));
        sql.addColumn(new SQLParameterValue(FIELD_TENDER_SUBTYPE, emptyStringToSpaceString(tenderItem.getTenderSubType())));
        sql.addColumn(new SQLParameterValue(FIELD_CURRENCY_ISSUING_COUNTRY_CODE, tenderItem.getCurrencyCode()));
        sql.addColumn(new SQLParameterValue(FIELD_CURRENCY_ID, tenderItem.getCurrencyID()));
        sql.addColumn(new SQLParameterFunction(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction()));
        sql.addColumn(new SQLParameterFunction(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction()));

        // Add tender columns and their values
        updateTenderTotals(sql, totals, tenderItem, loans, pickups, payIns, payOuts);

        dataConnection.execute(sql.getSQLString(), sql.getParameterValues());

        if (0 < dataConnection.getUpdateCount())
        {
            returnCode = true;
        }

        return(returnCode);

    }

    /**
     * Adds the columns and values for the financial totals fields
     * 
     * @param sql The SQL statement to add the column-value pairs to
     * @param totals The financial totals to draw the values from
     */
    protected void updateFinancialTotals(SQLUpdatableStatementIfc sql, FinancialTotalsIfc totals)
    {
        // Totals
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TRANSACTION_COUNT, getTransactionCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_NONTAXABLE_TOTAL_AMOUNT, getNetNontaxableAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_NONTAXABLE_COUNT, getNontaxableCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TAXABLE_COUNT, getTaxableCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TAX_EXEMPT_TOTAL_AMOUNT, getNetTaxExemptAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TAX_EXEMPT_TRANSACTION_COUNT, getTaxExemptCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_REFUND_TOTAL_AMOUNT, getRefundAmount(totals)));
        // Transaction Sales/Refunds
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_SALES_EX_TAX_TOTAL_AMOUNT, getSalesAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_REFUND_COUNT, getRefundCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_REFUND_NONTAXABLE_TOTAL_AMOUNT, getRefundNontaxableAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_NONTAXABLE_REFUND_COUNT, getRefundNontaxableCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_REFUND_TAX_EXEMPT_TOTAL_AMOUNT, getRefundTaxExemptAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TAX_EXEMPT_REFUND_COUNT, getRefundTaxExemptCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TAXABLE_TRANSACTION_SALES_AMOUNT, getTaxableTransactionSalesAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TAXABLE_TRANSACTION_SALES_COUNT, getTaxableTransactionSalesCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_NON_TAXABLE_TRANSACTION_SALES_AMOUNT, getNonTaxableTransactionSalesAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_NON_TAXABLE_TRANSACTION_SALES_COUNT, getNonTaxableTransactionSalesCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TAX_EXEMPT_TRANSACTION_SALES_AMOUNT, getTaxExemptTransactionSalesAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TAX_EXEMPT_TRANSACTION_SALES_COUNT, getTaxExemptTransactionSalesCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TAXABLE_TRANSACTION_RETURNS_AMOUNT, getTaxableTransactionReturnsAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TAXABLE_TRANSACTION_RETURNS_COUNT, getTaxableTransactionReturnsCount(totals)));

        // Item Sales/Returns
        sql.addColumn(new SQLParameterValue(FIELD_STORE_LINE_ITEM_SALES_TOTAL_AMOUNT, getItemSalesAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_SALE_LINE_ITEM_COUNT, getItemSalesCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_LINE_ITEM_NONTAXABLE_TOTAL_AMOUNT, getItemNontaxableAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_NONTAXABLE_LINE_ITEM_COUNT, getItemNontaxableCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_LINE_ITEM_TAX_EXEMPT_TOTAL_AMOUNT, getItemTaxExemptAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TAX_EXEMPT_LINE_ITEM_COUNT, getItemTaxExemptCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_RETURN_TOTAL_AMOUNT, getReturnAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_RETURN_COUNT, getReturnCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_RETURN_NONTAXABLE_TOTAL_AMOUNT, getReturnNontaxableAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_NONTAXABLE_RETURN_COUNT, getReturnNontaxableCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_RETURN_TAX_EXEMPT_TOTAL_AMOUNT, getReturnTaxExemptAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TAX_EXEMPT_RETURN_COUNT, getReturnTaxExemptCount(totals)));

        sql.addColumn(new SQLParameterValue(FIELD_STORE_NONMERCH_NONTAX_TOTAL_AMOUNT, getNonMerchNonTaxAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_NONMERCH_NONTAX_COUNT, getNonMerchNonTaxCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_RETURN_NONMERCH_NONTAX_TOTAL_AMOUNT, getNonMerchNonTaxReturnAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_NONMERCH_NONTAX_RETURN_COUNT, getNonMerchNonTaxReturnCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_NONMERCH_TAX_TOTAL_AMOUNT, getNonMerchAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_NONMERCH_TAX_COUNT, getNonMerchCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_RETURN_NONMERCH_TAX_TOTAL_AMOUNT, getNonMerchReturnAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_NONMERCH_TAX_RETURN_COUNT, getNonMerchReturnCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_AMOUNT, getGiftCardAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_COUNT, getGiftCardCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_RETURN_GIFT_CARD_AMOUNT, getGiftCardReturnAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_RETURN_COUNT, getGiftCardReturnCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_HOUSE_PAYMENT_AMOUNT, getHousePaymentAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_HOUSE_PAYMENT_COUNT, getHousePaymentCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_RESTOCKING_FEE_AMOUNT, getRestockingFeeAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_RESTOCKING_FEE_COUNT, getRestockingFeeCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_RESTOCKING_FEE_AMOUNT_FROM_NON_TAXABLE, getRestockingFeeFromNonTaxableAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_RESTOCKING_FEE_COUNT_FROM_NON_TAXABLE, getRestockingFeeFromNonTaxableCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_SHIPPING_CHARGE_TOTAL_AMOUNT, getShippingChargeAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_SHIPPING_CHARGE_COUNT, getShippingChargeCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_SHIPPING_CHARGE_TAX_AMOUNT, getShippingChargeTax(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_SHIPPING_CHARGE_INCLUSIVE_TAX_AMOUNT, getShippingChargeInclusiveTax(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TAXABLE_ITEM_SALES_AMOUNT, getTaxableItemSalesAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TAXABLE_ITEM_SALES_COUNT, getTaxableItemSalesCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TAXABLE_ITEM_RETURNS_AMOUNT, getTaxableItemReturnsAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TAXABLE_ITEM_RETURNS_COUNT, getTaxableItemReturnsCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TAXABLE_NON_MERCHANDISE_SALES_AMOUNT, getTaxableNonMerchandiseSalesAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TAXABLE_NON_MERCHANDISE_SALES_COUNT, getTaxableNonMerchandiseSalesCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES_AMOUNT, getNonTaxableNonMerchandiseSalesAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES_COUNT, getNonTaxableNonMerchandiseSalesCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CERTIFICATE_ISSUED_AMOUNT, getGiftCertificateIssuedAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CERTIFICATE_ISSUED_COUNT, getGiftCertificateIssuedCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_ISSUED_AMOUNT, getGiftCardIssuedAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_ISSUED_COUNT, getGiftCardIssuedCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_RELOADED_AMOUNT, getGiftCardReloadedAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_RELOADED_COUNT, getGiftCardReloadedCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_REDEEMED_AMOUNT, getGiftCardRedeemedAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_REDEEMED_COUNT, getGiftCardRedeemedCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_ISSUE_VOIDED_AMOUNT, getGiftCardIssueVoidedAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_ISSUE_VOIDED_COUNT, getGiftCardIssueVoidedCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_RELOAD_VOIDED_AMOUNT, getGiftCardReloadVoidedAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_RELOAD_VOIDED_COUNT, getGiftCardReloadVoidedCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_REDEEM_VOIDED_AMOUNT, getGiftCardRedeemVoidedAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_REDEEM_VOIDED_COUNT, getGiftCardRedeemVoidedCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_HOUSE_ACCOUNT_ENROLLMENT_APPROVED_COUNT, getHouseCardEnrollmentsApprovalCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_HOUSE_ACCOUNT_ENROLLMENT_DECLINED_COUNT, getHouseCardEnrollmentsDeclinedCount(totals)));

        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CARD_ITEM_CREDIT_AMOUNT, getAmountGrossGiftCardItemCredit(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CARD_ITEM_CREDIT_UNITS, getUnitsGrossGiftCardItemCredit(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CARD_ITEM_CREDIT_VOIDED_AMOUNT, getAmountGrossGiftCardItemCreditVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CARD_ITEM_CREDIT_VOIDED_UNITS, getUnitsGrossGiftCardItemCreditVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CERTIFICATE_REDEEMED_AMOUNT, getAmountGrossGiftCertificatesRedeemed(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CERTIFICATE_REDEEMED_UNITS, getUnitsGrossGiftCertificatesRedeemed(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CERTIFICATE_REDEEMED_VOIDED_AMOUNT, getAmountGrossGiftCertificatesRedeemedVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CERTIFICATE_REDEEMED_VOIDED_UNITS, getUnitsGrossGiftCertificatesRedeemedVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_STORE_CREDITS_ISSUED_AMOUNT, getAmountGrossStoreCreditsIssued(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_STORE_CREDITS_ISSUED_UNITS, getUnitsGrossStoreCreditsIssued(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_STORE_CREDITS_ISSUED_VOIDED_AMOUNT, getAmountGrossStoreCreditsIssuedVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_STORE_CREDITS_ISSUED_VOIDED_UNITS, getUnitsGrossStoreCreditsIssuedVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_STORE_CREDITS_REDEEMED_AMOUNT, getAmountGrossStoreCreditsRedeemed(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_STORE_CREDITS_REDEEMED_UNITS, getUnitsGrossStoreCreditsRedeemed(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_STORE_CREDITS_REDEEMED_VOIDED_AMOUNT, getAmountGrossStoreCreditsRedeemedVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_STORE_CREDITS_REDEEMED_VOIDED_UNITS, getUnitsGrossStoreCreditsRedeemedVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_ITEM_EMPLOYEE_DISCOUNT_AMOUNT, getAmountGrossItemEmployeeDiscount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_ITEM_EMPLOYEE_DISCOUNT_UNITS, getUnitsGrossItemEmployeeDiscount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_ITEM_EMPLOYEE_DISCOUNT_VOIDED_AMOUNT, getAmountGrossItemEmployeeDiscountVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_ITEM_EMPLOYEE_DISCOUNT_VOIDED_UNITS, getUnitsGrossItemEmployeeDiscountVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_AMOUNT, getAmountGrossTransactionEmployeeDiscount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_UNITS, getUnitsGrossTransactionEmployeeDiscount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_VOIDED_AMOUNT, getAmountGrossTransactionEmployeeDiscountVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_VOIDED_UNITS, getUnitsGrossTransactionEmployeeDiscountVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CERTIFICATE_ISSUED_VOIDED_AMOUNT, getAmountGrossGiftCertificateIssuedVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CERTIFICATE_ISSUED_VOIDED_COUNT, getUnitsGrossGiftCertificateIssuedVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CERTIFICATE_TENDERED_AMOUNT, getAmountGrossGiftCertificateTendered(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CERTIFICATE_TENDERED_COUNT, getUnitsGrossGiftCertificateTendered(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CERTIFICATE_TENDERED_VOIDED_AMOUNT, getAmountGrossGiftCertificateTenderedVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CERTIFICATE_TENDERED_VOIDED_COUNT, getUnitsGrossGiftCertificateTenderedVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_EMPLOYEE_DISCOUNTS_AMOUNT, getAmountEmployeeDiscounts(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_EMPLOYEE_DISCOUNTS_COUNT, getUnitsEmployeeDiscounts(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_CUSTOMER_DISCOUNTS_AMOUNT, getAmountCustomerDiscounts(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_CUSTOMER_DISCOUNTS_COUNT, getUnitsCustomerDiscounts(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_PRICE_OVERRIDES_AMOUNT, getAmountPriceOverrides(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_PRICE_OVERRIDES_COUNT, getUnitsPriceOverrides(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_PRICE_ADJUSTMENTS_COUNT, getUnitsPriceAdjustments(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TRANSACTIONS_WITH_RETURNED_ITEMS_COUNT, getCountTransactionsWithReturnedItems(totals)));


        if (totals != null)
        {
            sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_LAYAWAY_PAYMENTS_COLLECTED_COUNT, totals.getCountLayawayPayments()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_LAYAWAY_PAYMENTS_COLLECTED_TOTAL_AMOUNT, totals.getAmountLayawayPayments()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_LAYAWAY_NEW_TOTAL_AMOUNT, totals.getAmountLayawayNew()));
	        sql.addColumn(new SQLParameterValue(FIELD_STORE_LAYAWAY_PICKUP_TOTAL_AMOUNT, totals.getAmountLayawayPickup()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_LAYAWAY_DELETIONS_DISBURSEMENT_COUNT, totals.getCountLayawayDeletions()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_LAYAWAY_DELETIONS_DISBURSEMENT_TOTAL_AMOUNT, totals.getAmountLayawayDeletions()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_LAYAWAY_INITIATION_FEES_COLLECTED_TOTAL_AMOUNT, totals.getAmountLayawayInitiationFees()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_LAYAWAY_INITIATION_FEES_COLLECTED_COUNT, totals.getCountLayawayInitiationFees()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_LAYAWAY_DELETION_FEES_COLLECTED_TOTAL_AMOUNT, totals.getAmountLayawayDeletionFees()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_LAYAWAY_DELETION_FEES_COUNT, totals.getCountLayawayDeletionFees()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_FUNDS_RECEIVED_IN_TOTAL_AMOUNT, totals.getAmountTillPayIns()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_FUNDS_RECEIVED_OUT_TOTAL_AMOUNT, totals.getAmountTillPayOuts()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_FUNDS_RECEIVED_IN_UNIT_COUNT, totals.getCountTillPayIns()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_FUNDS_RECEIVED_OUT_UNIT_COUNT, totals.getCountTillPayOuts()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_SPECIAL_ORDER_NEW_TOTAL_AMOUNT, totals.getAmountSpecialOrderNew()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_SPECIAL_ORDER_PARTIAL_TOTAL_AMOUNT, totals.getAmountSpecialOrderPartial()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_ORDER_PAYMENTS_COLLECTED_COUNT, totals.getCountOrderPayments()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_ORDER_PAYMENTS_COLLECTED_TOTAL_AMOUNT, totals.getAmountOrderPayments()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_ORDER_CANCELS_DISBURSEMENT_COUNT, totals.getCountOrderCancels()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_ORDER_CANCELS_DISBURSEMENT_TOTAL_AMOUNT, totals.getAmountOrderCancels()));
        }

        // Tax
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TAX_TOTAL_AMOUNT, getNetTaxAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_INCLUSIVE_TAX_TOTAL_AMOUNT, getNetInclusiveTaxAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_REFUND_TAX_TOTAL_AMOUNT, getTaxRefundedAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_REFUND_INCLUSIVE_TAX_TOTAL_AMOUNT, getInclusiveTaxRefundedAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_RETURN_TAX_TOTAL_AMOUNT, getTaxReturnedAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_RETURN_INCLUSIVE_TAX_TOTAL_AMOUNT, getInclusiveTaxReturnedAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_ITEM_SALES_TAX_AMOUNT, getItemTaxAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_ITEM_SALES_INCLUSIVE_TAX_AMOUNT, getItemSalesInclusiveTaxAmount(totals)));
        if (totals != null)
        {
            sql.addColumn(new SQLParameterValue(FIELD_STORE_TRANSACTION_SALES_TAX_AMOUNT, totals.getAmountTaxTransactionSales()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_TRANSACTION_SALES_INCLUSIVE_TAX_AMOUNT, totals.getAmountInclusiveTaxTransactionSales()));
        }
        
        // Misc
            // StoreCouponDiscount
        sql.addColumn(new SQLParameterValue(FIELD_STORE_ITEM_DISCOUNT_STORE_COUPON_AMOUNT, getItemDiscStoreCouponAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_ITEM_DISCOUNT_STORE_COUPON_COUNT, getItemDiscStoreCouponCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TRANSACTION_DISCOUNT_STORE_COUPON_AMOUNT, getTransactionDiscStoreCouponAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TRANSACTION_DISCOUNT_STORE_COUPON_COUNT, getTransactionDiscStoreCouponCount(totals)));
            //
        sql.addColumn(new SQLParameterValue(FIELD_STORE_MISCELLANEOUS_DISCOUNT_TOTAL_AMOUNT, getTransactionDiscountAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_MISCELLANEOUS_DISCOUNT_COUNT, getTransactionDiscountCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_DISCOUNT_TOTAL_AMOUNT, getDiscountAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_DISCOUNT_COUNT, getDiscountCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_MARKDOWN_TOTAL_AMOUNT, getMarkdownAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_MARKDOWN_COUNT, getMarkdownCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_POST_TRANSACTION_VOID_TOTAL_AMOUNT, getPostVoidAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_POST_TRANSACTION_VOID_COUNT, getPostVoidCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_LINE_ITEM_VOID_TOTAL_AMOUNT, getLineVoidAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_LINE_ITEM_VOID_COUNT, getLineVoidCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TRANSACTION_VOID_TOTAL_AMOUNT, getVoidAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TRANSACTION_VOID_COUNT, getVoidCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_NO_SALE_TRANSACTION_COUNT, getNoSaleCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TENDER_PICKUP_COUNT, getTenderPickupCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TENDER_PICKUP_TOTAL_AMOUNT, getTenderPickupAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TENDER_LOAN_COUNT, getTenderLoanCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TENDER_LOAN_TOTAL_AMOUNT, getTenderLoanAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_GROSS_TAXABLE_SALES_VOID_COUNT, getTaxableSalesVoidCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_GROSS_TAXABLE_RETURNS_VOID_COUNT, getTaxableReturnsVoidCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_GROSS_NONTAXABLE_SALES_VOID_COUNT, getNonTaxableSalesVoidCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_GROSS_NONTAXABLE_RETURNS_VOID_COUNT, getNonTaxableReturnsVoidCount(totals)));
        // set timestamps
        sql.addColumn(new SQLParameterFunction(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction()));
        sql.addColumn(new SQLParameterFunction(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction()));
    }

    /**
     * Adds the values from the financial totals to the existing values in the
     * database.
     * 
     * @param sql The SQL statement
     * @param totals The financial totals to draw the values from
     */
    protected void addFinancialTotals(SQLUpdatableStatementIfc sql, FinancialTotalsIfc totals)
    {
        // Totals
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TRANSACTION_COUNT + "=" + FIELD_STORE_TOTAL_TRANSACTION_COUNT + "+?", getTransactionCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_NONTAXABLE_TOTAL_AMOUNT + "=" + FIELD_STORE_NONTAXABLE_TOTAL_AMOUNT + "+?", getNetNontaxableAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_NONTAXABLE_COUNT + "=" + FIELD_STORE_TOTAL_NONTAXABLE_COUNT + "+?", getNontaxableCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TAXABLE_COUNT + "=" + FIELD_STORE_TOTAL_TAXABLE_COUNT + "+?", getTaxableCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TAX_EXEMPT_TOTAL_AMOUNT + "=" + FIELD_STORE_TAX_EXEMPT_TOTAL_AMOUNT + "+?", getNetTaxExemptAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TAX_EXEMPT_TRANSACTION_COUNT + "=" + FIELD_STORE_TOTAL_TAX_EXEMPT_TRANSACTION_COUNT + "+?", getTaxExemptCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_REFUND_TOTAL_AMOUNT + "=" + FIELD_STORE_REFUND_TOTAL_AMOUNT + "+?", getRefundAmount(totals)));
        // Transaction Sales/Refunds
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_SALES_EX_TAX_TOTAL_AMOUNT + "=" + FIELD_STORE_GROSS_SALES_EX_TAX_TOTAL_AMOUNT + "+?", getSalesAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_REFUND_COUNT + "=" + FIELD_STORE_TOTAL_REFUND_COUNT + "+?", getRefundCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_REFUND_NONTAXABLE_TOTAL_AMOUNT + "=" + FIELD_STORE_REFUND_NONTAXABLE_TOTAL_AMOUNT + "+?", getRefundNontaxableAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_NONTAXABLE_REFUND_COUNT + "=" + FIELD_STORE_TOTAL_NONTAXABLE_REFUND_COUNT + "+?", getRefundNontaxableCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_REFUND_TAX_EXEMPT_TOTAL_AMOUNT + "=" + FIELD_STORE_REFUND_TAX_EXEMPT_TOTAL_AMOUNT + "+?", getRefundTaxExemptAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TAX_EXEMPT_REFUND_COUNT + "=" + FIELD_STORE_TOTAL_TAX_EXEMPT_REFUND_COUNT + "+?", getRefundTaxExemptCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TAXABLE_TRANSACTION_SALES_AMOUNT + "=" + FIELD_STORE_GROSS_TAXABLE_TRANSACTION_SALES_AMOUNT + "+?", getTaxableTransactionSalesAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TAXABLE_TRANSACTION_SALES_COUNT + "=" + FIELD_STORE_GROSS_TAXABLE_TRANSACTION_SALES_COUNT + "+?", getTaxableTransactionSalesCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_NON_TAXABLE_TRANSACTION_SALES_AMOUNT + "=" + FIELD_STORE_GROSS_NON_TAXABLE_TRANSACTION_SALES_AMOUNT + "+?", getNonTaxableTransactionSalesAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_NON_TAXABLE_TRANSACTION_SALES_COUNT + "=" + FIELD_STORE_GROSS_NON_TAXABLE_TRANSACTION_SALES_COUNT + "+?", getNonTaxableTransactionSalesCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TAX_EXEMPT_TRANSACTION_SALES_AMOUNT + "=" + FIELD_STORE_GROSS_TAX_EXEMPT_TRANSACTION_SALES_AMOUNT + "+?", getTaxExemptTransactionSalesAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TAX_EXEMPT_TRANSACTION_SALES_COUNT + "=" + FIELD_STORE_GROSS_TAX_EXEMPT_TRANSACTION_SALES_COUNT + "+?", getTaxExemptTransactionSalesCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TAXABLE_TRANSACTION_RETURNS_AMOUNT + "=" + FIELD_STORE_GROSS_TAXABLE_TRANSACTION_RETURNS_AMOUNT + "+?", getTaxableTransactionReturnsAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TAXABLE_TRANSACTION_RETURNS_COUNT + "=" + FIELD_STORE_GROSS_TAXABLE_TRANSACTION_RETURNS_COUNT + "+?", getTaxableTransactionReturnsCount(totals)));
        // Item Sales/Returns
        sql.addColumn(new SQLParameterValue(FIELD_STORE_LINE_ITEM_SALES_TOTAL_AMOUNT + "=" + FIELD_STORE_LINE_ITEM_SALES_TOTAL_AMOUNT + "+?", getItemSalesAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_SALE_LINE_ITEM_COUNT + "=" + FIELD_STORE_TOTAL_SALE_LINE_ITEM_COUNT + "+?", getItemSalesCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_LINE_ITEM_NONTAXABLE_TOTAL_AMOUNT + "=" + FIELD_STORE_LINE_ITEM_NONTAXABLE_TOTAL_AMOUNT + "+?", getItemNontaxableAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_NONTAXABLE_LINE_ITEM_COUNT + "=" + FIELD_STORE_TOTAL_NONTAXABLE_LINE_ITEM_COUNT + "+?", getItemNontaxableCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_LINE_ITEM_TAX_EXEMPT_TOTAL_AMOUNT + "=" + FIELD_STORE_LINE_ITEM_TAX_EXEMPT_TOTAL_AMOUNT + "+?", getItemTaxExemptAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TAX_EXEMPT_LINE_ITEM_COUNT + "=" + FIELD_STORE_TOTAL_TAX_EXEMPT_LINE_ITEM_COUNT + "+?", getItemTaxExemptCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_RETURN_TOTAL_AMOUNT + "=" + FIELD_STORE_RETURN_TOTAL_AMOUNT + "+?", getReturnAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_RETURN_COUNT + "=" + FIELD_STORE_TOTAL_RETURN_COUNT + "+?", getReturnCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_RETURN_NONTAXABLE_TOTAL_AMOUNT + "=" + FIELD_STORE_RETURN_NONTAXABLE_TOTAL_AMOUNT + "+?", getReturnNontaxableAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_NONTAXABLE_RETURN_COUNT + "=" + FIELD_STORE_TOTAL_NONTAXABLE_RETURN_COUNT + "+?", getReturnNontaxableCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_RETURN_TAX_EXEMPT_TOTAL_AMOUNT + "=" + FIELD_STORE_RETURN_TAX_EXEMPT_TOTAL_AMOUNT + "+?", getReturnTaxExemptAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TAX_EXEMPT_RETURN_COUNT + "=" + FIELD_STORE_TOTAL_TAX_EXEMPT_RETURN_COUNT + "+?", getReturnTaxExemptCount(totals)));


        sql.addColumn(new SQLParameterValue(FIELD_STORE_NONMERCH_NONTAX_TOTAL_AMOUNT + "=" + FIELD_STORE_NONMERCH_NONTAX_TOTAL_AMOUNT        + "+?", getNonMerchNonTaxAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_NONMERCH_NONTAX_COUNT + "=" + FIELD_STORE_TOTAL_NONMERCH_NONTAX_COUNT         + "+?", getNonMerchNonTaxCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_RETURN_NONMERCH_NONTAX_TOTAL_AMOUNT + "=" + FIELD_STORE_RETURN_NONMERCH_NONTAX_TOTAL_AMOUNT + "+?", getNonMerchNonTaxReturnAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_NONMERCH_NONTAX_RETURN_COUNT + "=" + FIELD_STORE_TOTAL_NONMERCH_NONTAX_RETURN_COUNT  + "+?", getNonMerchNonTaxReturnCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_NONMERCH_TAX_TOTAL_AMOUNT + "=" + FIELD_STORE_NONMERCH_TAX_TOTAL_AMOUNT           + "+?", getNonMerchAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_NONMERCH_TAX_COUNT + "=" + FIELD_STORE_TOTAL_NONMERCH_TAX_COUNT            + "+?", getNonMerchCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_RETURN_NONMERCH_TAX_TOTAL_AMOUNT + "=" + FIELD_STORE_RETURN_NONMERCH_TAX_TOTAL_AMOUNT    + "+?", getNonMerchReturnAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_NONMERCH_TAX_RETURN_COUNT + "=" + FIELD_STORE_TOTAL_NONMERCH_TAX_RETURN_COUNT     + "+?", getNonMerchReturnCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_AMOUNT + "=" + FIELD_STORE_GIFT_CARD_AMOUNT                    + "+?", getGiftCardAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_COUNT + "=" + FIELD_STORE_GIFT_CARD_COUNT                     + "+?", getGiftCardCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_RETURN_GIFT_CARD_AMOUNT + "=" + FIELD_STORE_RETURN_GIFT_CARD_AMOUNT             + "+?", getGiftCardReturnAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_RETURN_COUNT + "=" + FIELD_STORE_GIFT_CARD_RETURN_COUNT              + "+?", getGiftCardReturnCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_HOUSE_PAYMENT_AMOUNT + "=" + FIELD_STORE_HOUSE_PAYMENT_AMOUNT                + "+?", getHousePaymentAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_HOUSE_PAYMENT_COUNT + "=" + FIELD_STORE_HOUSE_PAYMENT_COUNT                 + "+?", getHousePaymentCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_RESTOCKING_FEE_AMOUNT + "=" + FIELD_STORE_RESTOCKING_FEE_AMOUNT               + "+?", getRestockingFeeAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_RESTOCKING_FEE_COUNT + "=" + FIELD_STORE_RESTOCKING_FEE_COUNT                + "+?", getRestockingFeeCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_RESTOCKING_FEE_AMOUNT_FROM_NON_TAXABLE + "=" + FIELD_STORE_RESTOCKING_FEE_AMOUNT_FROM_NON_TAXABLE    + "+?", getRestockingFeeFromNonTaxableAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_RESTOCKING_FEE_COUNT_FROM_NON_TAXABLE + "=" + FIELD_STORE_RESTOCKING_FEE_COUNT_FROM_NON_TAXABLE + "+?", getRestockingFeeFromNonTaxableCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_SHIPPING_CHARGE_TOTAL_AMOUNT + "=" + FIELD_STORE_SHIPPING_CHARGE_TOTAL_AMOUNT               + "+?", getShippingChargeAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_SHIPPING_CHARGE_COUNT + "=" + FIELD_STORE_TOTAL_SHIPPING_CHARGE_COUNT                + "+?", +  getShippingChargeCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_SHIPPING_CHARGE_TAX_AMOUNT + "=" + FIELD_STORE_SHIPPING_CHARGE_TAX_AMOUNT               + "+?", getShippingChargeTax(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_SHIPPING_CHARGE_INCLUSIVE_TAX_AMOUNT + "=" + FIELD_STORE_SHIPPING_CHARGE_INCLUSIVE_TAX_AMOUNT               + "+?", getShippingChargeInclusiveTax(totals))); 
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TAXABLE_NON_MERCHANDISE_SALES_AMOUNT + "=" + FIELD_STORE_GROSS_TAXABLE_NON_MERCHANDISE_SALES_AMOUNT + "+?", getTaxableNonMerchandiseSalesAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TAXABLE_NON_MERCHANDISE_SALES_COUNT + "=" + FIELD_STORE_GROSS_TAXABLE_NON_MERCHANDISE_SALES_COUNT + "+?", getTaxableNonMerchandiseSalesCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES_AMOUNT + "=" + FIELD_STORE_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES_AMOUNT + "+?", getNonTaxableNonMerchandiseSalesAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES_COUNT + "=" + FIELD_STORE_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES_COUNT + "+?", getNonTaxableNonMerchandiseSalesCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TAXABLE_ITEM_SALES_AMOUNT + "=" + FIELD_STORE_GROSS_TAXABLE_ITEM_SALES_AMOUNT + "+?", getTaxableItemSalesAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TAXABLE_ITEM_SALES_COUNT + "=" + FIELD_STORE_GROSS_TAXABLE_ITEM_SALES_COUNT + "+?", getTaxableItemSalesCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TAXABLE_ITEM_RETURNS_AMOUNT + "=" + FIELD_STORE_GROSS_TAXABLE_ITEM_RETURNS_AMOUNT + "+?", getTaxableItemReturnsAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TAXABLE_ITEM_RETURNS_COUNT + "=" + FIELD_STORE_GROSS_TAXABLE_ITEM_RETURNS_COUNT + "+?", getTaxableItemReturnsCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CERTIFICATE_ISSUED_AMOUNT + "=" + FIELD_STORE_GIFT_CERTIFICATE_ISSUED_AMOUNT + "+?", getGiftCertificateIssuedAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CERTIFICATE_ISSUED_COUNT + "=" + FIELD_STORE_GIFT_CERTIFICATE_ISSUED_COUNT + "+?", getGiftCertificateIssuedCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_ISSUED_AMOUNT + "=" + FIELD_STORE_GIFT_CARD_ISSUED_AMOUNT + "+?", getGiftCardIssuedAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_ISSUED_COUNT + "=" + FIELD_STORE_GIFT_CARD_ISSUED_COUNT + "+?", getGiftCardIssuedCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_RELOADED_AMOUNT + "=" + FIELD_STORE_GIFT_CARD_RELOADED_AMOUNT + "+?", getGiftCardReloadedAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_RELOADED_COUNT + "=" + FIELD_STORE_GIFT_CARD_RELOADED_COUNT + "+?", getGiftCardReloadedCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_REDEEMED_AMOUNT + "=" + FIELD_STORE_GIFT_CARD_REDEEMED_AMOUNT + "+?", getGiftCardRedeemedAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_REDEEMED_COUNT + "=" + FIELD_STORE_GIFT_CARD_REDEEMED_COUNT + "+?", getGiftCardRedeemedCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_ISSUE_VOIDED_AMOUNT + "=" + FIELD_STORE_GIFT_CARD_ISSUE_VOIDED_AMOUNT + "+?", getGiftCardIssueVoidedAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_ISSUE_VOIDED_COUNT + "=" + FIELD_STORE_GIFT_CARD_ISSUE_VOIDED_COUNT + "+?", getGiftCardIssueVoidedCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_RELOAD_VOIDED_AMOUNT + "=" + FIELD_STORE_GIFT_CARD_RELOAD_VOIDED_AMOUNT + "+?", getGiftCardReloadVoidedAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_RELOAD_VOIDED_COUNT + "=" + FIELD_STORE_GIFT_CARD_RELOAD_VOIDED_COUNT + "+?", getGiftCardReloadVoidedCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_REDEEM_VOIDED_AMOUNT + "=" + FIELD_STORE_GIFT_CARD_REDEEM_VOIDED_AMOUNT + "+?", getGiftCardRedeemVoidedAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GIFT_CARD_REDEEM_VOIDED_COUNT + "=" + FIELD_STORE_GIFT_CARD_REDEEM_VOIDED_COUNT + "+?", getGiftCardRedeemVoidedCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_HOUSE_ACCOUNT_ENROLLMENT_APPROVED_COUNT + "=" + FIELD_STORE_HOUSE_ACCOUNT_ENROLLMENT_APPROVED_COUNT + "+?", getHouseCardEnrollmentsApprovalCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_HOUSE_ACCOUNT_ENROLLMENT_DECLINED_COUNT + "=" + FIELD_STORE_HOUSE_ACCOUNT_ENROLLMENT_DECLINED_COUNT + "+?", getHouseCardEnrollmentsDeclinedCount(totals)));

        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CARD_ITEM_CREDIT_AMOUNT + "=" + FIELD_STORE_GROSS_GIFT_CARD_ITEM_CREDIT_AMOUNT + "+?", getAmountGrossGiftCardItemCredit(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CARD_ITEM_CREDIT_UNITS + "=" + FIELD_STORE_GROSS_GIFT_CARD_ITEM_CREDIT_UNITS + "+?", getUnitsGrossGiftCardItemCredit(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CARD_ITEM_CREDIT_VOIDED_AMOUNT + "=" + FIELD_STORE_GROSS_GIFT_CARD_ITEM_CREDIT_VOIDED_AMOUNT + "+?", getAmountGrossGiftCardItemCreditVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CARD_ITEM_CREDIT_VOIDED_UNITS + "=" + FIELD_STORE_GROSS_GIFT_CARD_ITEM_CREDIT_VOIDED_UNITS + "+?", getUnitsGrossGiftCardItemCreditVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CERTIFICATE_REDEEMED_AMOUNT + "=" + FIELD_STORE_GROSS_GIFT_CERTIFICATE_REDEEMED_AMOUNT + "+?", getAmountGrossGiftCertificatesRedeemed(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CERTIFICATE_REDEEMED_UNITS + "=" + FIELD_STORE_GROSS_GIFT_CERTIFICATE_REDEEMED_UNITS + "+?", getUnitsGrossGiftCertificatesRedeemed(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CERTIFICATE_REDEEMED_VOIDED_AMOUNT + "=" + FIELD_STORE_GROSS_GIFT_CERTIFICATE_REDEEMED_VOIDED_AMOUNT + "+?", getAmountGrossGiftCertificatesRedeemedVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CERTIFICATE_REDEEMED_VOIDED_UNITS + "=" + FIELD_STORE_GROSS_GIFT_CERTIFICATE_REDEEMED_VOIDED_UNITS + "+?", getUnitsGrossGiftCertificatesRedeemedVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_STORE_CREDITS_ISSUED_AMOUNT + "=" + FIELD_STORE_GROSS_STORE_CREDITS_ISSUED_AMOUNT + "+?", getAmountGrossStoreCreditsIssued(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_STORE_CREDITS_ISSUED_UNITS + "=" + FIELD_STORE_GROSS_STORE_CREDITS_ISSUED_UNITS + "+?", getUnitsGrossStoreCreditsIssued(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_STORE_CREDITS_ISSUED_VOIDED_AMOUNT + "=" + FIELD_STORE_GROSS_STORE_CREDITS_ISSUED_VOIDED_AMOUNT + "+?", getAmountGrossStoreCreditsIssuedVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_STORE_CREDITS_ISSUED_VOIDED_UNITS + "=" + FIELD_STORE_GROSS_STORE_CREDITS_ISSUED_VOIDED_UNITS + "+?", getUnitsGrossStoreCreditsIssuedVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_STORE_CREDITS_REDEEMED_AMOUNT + "=" + FIELD_STORE_GROSS_STORE_CREDITS_REDEEMED_AMOUNT + "+?", getAmountGrossStoreCreditsRedeemed(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_STORE_CREDITS_REDEEMED_UNITS + "=" + FIELD_STORE_GROSS_STORE_CREDITS_REDEEMED_UNITS + "+?", getUnitsGrossStoreCreditsRedeemed(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_STORE_CREDITS_REDEEMED_VOIDED_AMOUNT + "=" + FIELD_STORE_GROSS_STORE_CREDITS_REDEEMED_VOIDED_AMOUNT + "+?", getAmountGrossStoreCreditsRedeemedVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_STORE_CREDITS_REDEEMED_VOIDED_UNITS + "=" + FIELD_STORE_GROSS_STORE_CREDITS_REDEEMED_VOIDED_UNITS + "+?", getUnitsGrossStoreCreditsRedeemedVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_ITEM_EMPLOYEE_DISCOUNT_AMOUNT + "=" + FIELD_STORE_GROSS_ITEM_EMPLOYEE_DISCOUNT_AMOUNT + "+?", getAmountGrossItemEmployeeDiscount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_ITEM_EMPLOYEE_DISCOUNT_UNITS + "=" + FIELD_STORE_GROSS_ITEM_EMPLOYEE_DISCOUNT_UNITS + "+?", getUnitsGrossItemEmployeeDiscount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_ITEM_EMPLOYEE_DISCOUNT_VOIDED_AMOUNT + "=" + FIELD_STORE_GROSS_ITEM_EMPLOYEE_DISCOUNT_VOIDED_AMOUNT + "+?", getAmountGrossItemEmployeeDiscountVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_ITEM_EMPLOYEE_DISCOUNT_VOIDED_UNITS + "=" + FIELD_STORE_GROSS_ITEM_EMPLOYEE_DISCOUNT_VOIDED_UNITS + "+?", getUnitsGrossItemEmployeeDiscountVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_AMOUNT + "=" + FIELD_STORE_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_AMOUNT + "+?", getAmountGrossTransactionEmployeeDiscount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_UNITS + "=" + FIELD_STORE_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_UNITS + "+?", getUnitsGrossTransactionEmployeeDiscount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_VOIDED_AMOUNT + "=" + FIELD_STORE_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_VOIDED_AMOUNT + "+?", getAmountGrossTransactionEmployeeDiscountVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_VOIDED_UNITS + "=" + FIELD_STORE_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_VOIDED_UNITS + "+?", getUnitsGrossTransactionEmployeeDiscountVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CERTIFICATE_ISSUED_VOIDED_AMOUNT + "=" + FIELD_STORE_GROSS_GIFT_CERTIFICATE_ISSUED_VOIDED_AMOUNT + "+?", getAmountGrossGiftCertificateIssuedVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CERTIFICATE_ISSUED_VOIDED_COUNT + "=" + FIELD_STORE_GROSS_GIFT_CERTIFICATE_ISSUED_VOIDED_COUNT + "+?", getUnitsGrossGiftCertificateIssuedVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CERTIFICATE_TENDERED_AMOUNT + "=" + FIELD_STORE_GROSS_GIFT_CERTIFICATE_TENDERED_AMOUNT + "+?", getAmountGrossGiftCertificateTendered(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CERTIFICATE_TENDERED_COUNT + "=" + FIELD_STORE_GROSS_GIFT_CERTIFICATE_TENDERED_COUNT + "+?", getUnitsGrossGiftCertificateTendered(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CERTIFICATE_TENDERED_VOIDED_AMOUNT + "=" + FIELD_STORE_GROSS_GIFT_CERTIFICATE_TENDERED_VOIDED_AMOUNT + "+?", getAmountGrossGiftCertificateTenderedVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_GIFT_CERTIFICATE_TENDERED_VOIDED_COUNT + "=" + FIELD_STORE_GROSS_GIFT_CERTIFICATE_TENDERED_VOIDED_COUNT + "+?", getUnitsGrossGiftCertificateTenderedVoided(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_EMPLOYEE_DISCOUNTS_AMOUNT + "=" + FIELD_STORE_GROSS_EMPLOYEE_DISCOUNTS_AMOUNT + "+?", getAmountEmployeeDiscounts(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_EMPLOYEE_DISCOUNTS_COUNT + "=" + FIELD_STORE_GROSS_EMPLOYEE_DISCOUNTS_COUNT + "+?", getUnitsEmployeeDiscounts(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_CUSTOMER_DISCOUNTS_AMOUNT + "=" + FIELD_STORE_GROSS_CUSTOMER_DISCOUNTS_AMOUNT + "+?", getAmountCustomerDiscounts(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_GROSS_CUSTOMER_DISCOUNTS_COUNT + "=" + FIELD_STORE_GROSS_CUSTOMER_DISCOUNTS_COUNT + "+?", getUnitsCustomerDiscounts(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_PRICE_OVERRIDES_AMOUNT + "=" + FIELD_STORE_PRICE_OVERRIDES_AMOUNT + "+?", getAmountPriceOverrides(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_PRICE_OVERRIDES_COUNT + "=" + FIELD_STORE_PRICE_OVERRIDES_COUNT + "+?", getUnitsPriceOverrides(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_PRICE_ADJUSTMENTS_COUNT + "=" + FIELD_STORE_PRICE_ADJUSTMENTS_COUNT + "+?", getUnitsPriceAdjustments(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TRANSACTIONS_WITH_RETURNED_ITEMS_COUNT + "=" + FIELD_STORE_TRANSACTIONS_WITH_RETURNED_ITEMS_COUNT + "+?", getCountTransactionsWithReturnedItems(totals)));


        if (totals != null)
        {
            sql.addColumn(new SQLParameterValue(FIELD_WORKSTATION_TOTAL_LAYAWAY_PAYMENTS_COLLECTED_COUNT + "=" + FIELD_WORKSTATION_TOTAL_LAYAWAY_PAYMENTS_COLLECTED_COUNT + "+?", totals.getCountLayawayPayments()));
            sql.addColumn(new SQLParameterValue(FIELD_WORKSTATION_LAYAWAY_PAYMENTS_COLLECTED_TOTAL_AMOUNT + "=" + FIELD_WORKSTATION_LAYAWAY_PAYMENTS_COLLECTED_TOTAL_AMOUNT + "+?", totals.getAmountLayawayPayments()));
            sql.addColumn(new SQLParameterValue(FIELD_WORKSTATION_LAYAWAY_NEW_TOTAL_AMOUNT + "=" + FIELD_WORKSTATION_LAYAWAY_NEW_TOTAL_AMOUNT + "+?", totals.getAmountLayawayNew()));
            sql.addColumn(new SQLParameterValue(FIELD_WORKSTATION_LAYAWAY_PICKUP_TOTAL_AMOUNT + "=" + FIELD_WORKSTATION_LAYAWAY_PICKUP_TOTAL_AMOUNT + "+?", totals.getAmountLayawayPickup()));
            sql.addColumn(new SQLParameterValue(FIELD_WORKSTATION_TOTAL_LAYAWAY_DELETIONS_DISBURSEMENT_COUNT + "=" + FIELD_WORKSTATION_TOTAL_LAYAWAY_DELETIONS_DISBURSEMENT_COUNT + "+?", totals.getCountLayawayDeletions()));
            sql.addColumn(new SQLParameterValue(FIELD_WORKSTATION_LAYAWAY_DELETIONS_DISBURSEMENT_TOTAL_AMOUNT + "=" + FIELD_WORKSTATION_LAYAWAY_DELETIONS_DISBURSEMENT_TOTAL_AMOUNT + "+?", totals.getAmountLayawayDeletions()));
            sql.addColumn(new SQLParameterValue(FIELD_WORKSTATION_LAYAWAY_INITIATION_FEES_COLLECTED_TOTAL_AMOUNT + "=" + FIELD_WORKSTATION_LAYAWAY_INITIATION_FEES_COLLECTED_TOTAL_AMOUNT + "+?", totals.getAmountLayawayInitiationFees()));
            sql.addColumn(new SQLParameterValue(FIELD_WORKSTATION_TOTAL_LAYAWAY_INITIATION_FEES_COLLECTED_COUNT + "=" + FIELD_WORKSTATION_TOTAL_LAYAWAY_INITIATION_FEES_COLLECTED_COUNT + "+?", totals.getCountLayawayInitiationFees()));
            sql.addColumn(new SQLParameterValue(FIELD_WORKSTATION_LAYAWAY_DELETION_FEES_COLLECTED_TOTAL_AMOUNT + "=" + FIELD_WORKSTATION_LAYAWAY_DELETION_FEES_COLLECTED_TOTAL_AMOUNT + "+?", totals.getAmountLayawayDeletionFees()));
            sql.addColumn(new SQLParameterValue(FIELD_WORKSTATION_TOTAL_LAYAWAY_DELETION_FEES_COUNT + "=" + FIELD_WORKSTATION_TOTAL_LAYAWAY_DELETION_FEES_COUNT + "+?", totals.getCountLayawayDeletionFees()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_SPECIAL_ORDER_NEW_TOTAL_AMOUNT + "=" + FIELD_STORE_SPECIAL_ORDER_NEW_TOTAL_AMOUNT + "+?", totals.getAmountSpecialOrderNew()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_SPECIAL_ORDER_PARTIAL_TOTAL_AMOUNT + "=" + FIELD_STORE_SPECIAL_ORDER_PARTIAL_TOTAL_AMOUNT + "+?", totals.getAmountSpecialOrderPartial()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_ORDER_PAYMENTS_COLLECTED_COUNT + "=" + FIELD_STORE_TOTAL_ORDER_PAYMENTS_COLLECTED_COUNT + "+?", totals.getCountOrderPayments()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_ORDER_PAYMENTS_COLLECTED_TOTAL_AMOUNT + "=" + FIELD_STORE_ORDER_PAYMENTS_COLLECTED_TOTAL_AMOUNT + "+?", totals.getAmountOrderPayments()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_ORDER_CANCELS_DISBURSEMENT_COUNT + "=" + FIELD_STORE_TOTAL_ORDER_CANCELS_DISBURSEMENT_COUNT + "+?", totals.getCountOrderCancels()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_ORDER_CANCELS_DISBURSEMENT_TOTAL_AMOUNT + "=" + FIELD_STORE_ORDER_CANCELS_DISBURSEMENT_TOTAL_AMOUNT + "+?", totals.getAmountOrderCancels()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_FUNDS_RECEIVED_IN_TOTAL_AMOUNT + "=" + FIELD_STORE_FUNDS_RECEIVED_IN_TOTAL_AMOUNT + "+?", totals.getAmountTillPayIns()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_FUNDS_RECEIVED_OUT_TOTAL_AMOUNT + "=" + FIELD_STORE_FUNDS_RECEIVED_OUT_TOTAL_AMOUNT + "+?", totals.getAmountTillPayOuts()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_FUNDS_RECEIVED_IN_UNIT_COUNT + "=" + FIELD_STORE_FUNDS_RECEIVED_IN_UNIT_COUNT + "+?", totals.getCountTillPayIns()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_FUNDS_RECEIVED_OUT_UNIT_COUNT + "=" + FIELD_STORE_FUNDS_RECEIVED_OUT_UNIT_COUNT + "+?", totals.getCountTillPayOuts()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_BILLPAYMENT + "=" + FIELD_STORE_TOTAL_BILLPAYMENT + "+?", totals.getAmountBillPayments()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_BILLPAYMENT_COUNT + "=" + FIELD_STORE_TOTAL_BILLPAYMENT_COUNT + "+?", totals.getCountBillPayments()));
            sql.addColumn(new SQLParameterValue(FIELD_TOTAL_AMOUNT_CHANGE_ROUNDED_IN  + "=" + FIELD_TOTAL_AMOUNT_CHANGE_ROUNDED_IN  + "+?", totals.getAmountChangeRoundedIn()));
            sql.addColumn(new SQLParameterValue(FIELD_TOTAL_AMOUNT_CHANGE_ROUNDED_OUT + "=" + FIELD_TOTAL_AMOUNT_CHANGE_ROUNDED_OUT + "+?", totals.getAmountChangeRoundedOut()));
            
        }

        // Tax
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TAX_TOTAL_AMOUNT + "=" + FIELD_STORE_TAX_TOTAL_AMOUNT + "+?", getNetTaxAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_INCLUSIVE_TAX_TOTAL_AMOUNT + "=" + FIELD_STORE_INCLUSIVE_TAX_TOTAL_AMOUNT + "+?", getNetInclusiveTaxAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_REFUND_TAX_TOTAL_AMOUNT + "=" + FIELD_STORE_REFUND_TAX_TOTAL_AMOUNT + "+?", getTaxRefundedAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_REFUND_INCLUSIVE_TAX_TOTAL_AMOUNT + "=" + FIELD_STORE_REFUND_INCLUSIVE_TAX_TOTAL_AMOUNT + "+?", getInclusiveTaxRefundedAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_RETURN_TAX_TOTAL_AMOUNT + "=" + FIELD_STORE_RETURN_TAX_TOTAL_AMOUNT + "+?", getTaxReturnedAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_RETURN_INCLUSIVE_TAX_TOTAL_AMOUNT + "=" + FIELD_STORE_RETURN_INCLUSIVE_TAX_TOTAL_AMOUNT + "+?", getInclusiveTaxReturnedAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_ITEM_SALES_TAX_AMOUNT + "=" + FIELD_STORE_ITEM_SALES_TAX_AMOUNT + "+?", getItemTaxAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_ITEM_SALES_INCLUSIVE_TAX_AMOUNT + "=" + FIELD_STORE_ITEM_SALES_INCLUSIVE_TAX_AMOUNT + "+?", getItemSalesInclusiveTaxAmount(totals)));
        if (totals != null)
        {
            sql.addColumn(new SQLParameterValue(FIELD_STORE_TRANSACTION_SALES_INCLUSIVE_TAX_AMOUNT + "=" + FIELD_STORE_TRANSACTION_SALES_INCLUSIVE_TAX_AMOUNT + "+?", totals.getAmountInclusiveTaxTransactionSales()));
            sql.addColumn(new SQLParameterValue(FIELD_STORE_TRANSACTION_SALES_TAX_AMOUNT + "=" + FIELD_STORE_TRANSACTION_SALES_TAX_AMOUNT + "+?", totals.getAmountTaxTransactionSales()));
        }
        
        // Misc
        // StoreCouponDiscounts
        sql.addColumn(new SQLParameterValue(FIELD_STORE_ITEM_DISCOUNT_STORE_COUPON_AMOUNT + "=" + FIELD_STORE_ITEM_DISCOUNT_STORE_COUPON_AMOUNT             + "+?", getItemDiscStoreCouponAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_ITEM_DISCOUNT_STORE_COUPON_COUNT + "=" + FIELD_STORE_ITEM_DISCOUNT_STORE_COUPON_COUNT              + "+?", getItemDiscStoreCouponCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TRANSACTION_DISCOUNT_STORE_COUPON_AMOUNT + "=" + FIELD_STORE_TRANSACTION_DISCOUNT_STORE_COUPON_AMOUNT             + "+?", getTransactionDiscStoreCouponAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TRANSACTION_DISCOUNT_STORE_COUPON_COUNT + "=" + FIELD_STORE_TRANSACTION_DISCOUNT_STORE_COUPON_COUNT              + "+?", getTransactionDiscStoreCouponCount(totals)));
            //
        sql.addColumn(new SQLParameterValue(FIELD_STORE_MISCELLANEOUS_DISCOUNT_TOTAL_AMOUNT + "=" + FIELD_STORE_MISCELLANEOUS_DISCOUNT_TOTAL_AMOUNT + "+?", getTransactionDiscountAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_MISCELLANEOUS_DISCOUNT_COUNT + "=" + FIELD_STORE_TOTAL_MISCELLANEOUS_DISCOUNT_COUNT + "+?", getTransactionDiscountCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_DISCOUNT_TOTAL_AMOUNT + "=" + FIELD_STORE_DISCOUNT_TOTAL_AMOUNT + "+?", getDiscountAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_DISCOUNT_COUNT + "=" + FIELD_STORE_TOTAL_DISCOUNT_COUNT + "+?", getDiscountCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_MARKDOWN_TOTAL_AMOUNT + "=" + FIELD_STORE_MARKDOWN_TOTAL_AMOUNT + "+?", getMarkdownAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_MARKDOWN_COUNT + "=" + FIELD_STORE_TOTAL_MARKDOWN_COUNT + "+?", getMarkdownCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_POST_TRANSACTION_VOID_TOTAL_AMOUNT + "=" + FIELD_STORE_POST_TRANSACTION_VOID_TOTAL_AMOUNT + "+?", getPostVoidAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_POST_TRANSACTION_VOID_COUNT + "=" + FIELD_STORE_TOTAL_POST_TRANSACTION_VOID_COUNT + "+?", getPostVoidCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_LINE_ITEM_VOID_TOTAL_AMOUNT + "=" + FIELD_STORE_LINE_ITEM_VOID_TOTAL_AMOUNT + "+?", getLineVoidAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_LINE_ITEM_VOID_COUNT + "=" + FIELD_STORE_TOTAL_LINE_ITEM_VOID_COUNT + "+?", getLineVoidCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TRANSACTION_VOID_TOTAL_AMOUNT + "=" + FIELD_STORE_TRANSACTION_VOID_TOTAL_AMOUNT + "+?", getVoidAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TRANSACTION_VOID_COUNT + "=" + FIELD_STORE_TOTAL_TRANSACTION_VOID_COUNT + "+?", getVoidCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_NO_SALE_TRANSACTION_COUNT + "=" + FIELD_STORE_TOTAL_NO_SALE_TRANSACTION_COUNT + "+?", getNoSaleCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TENDER_PICKUP_COUNT + "=" + FIELD_STORE_TOTAL_TENDER_PICKUP_COUNT + "+?", getTenderPickupCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TENDER_PICKUP_TOTAL_AMOUNT + "=" + FIELD_STORE_TENDER_PICKUP_TOTAL_AMOUNT + "+?", getTenderPickupAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TENDER_LOAN_COUNT + "=" + FIELD_STORE_TOTAL_TENDER_LOAN_COUNT + "+?", getTenderLoanCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TENDER_LOAN_TOTAL_AMOUNT + "=" + FIELD_STORE_TENDER_LOAN_TOTAL_AMOUNT + "+?", getTenderLoanAmount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_GROSS_TAXABLE_SALES_VOID_COUNT + "=" + FIELD_GROSS_TAXABLE_SALES_VOID_COUNT + "+?", getTaxableSalesVoidCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_GROSS_TAXABLE_RETURNS_VOID_COUNT + "=" + FIELD_GROSS_TAXABLE_RETURNS_VOID_COUNT + "+?", getTaxableReturnsVoidCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_GROSS_NONTAXABLE_SALES_VOID_COUNT + "=" + FIELD_GROSS_NONTAXABLE_SALES_VOID_COUNT + "+?", getNonTaxableSalesVoidCount(totals)));
        sql.addColumn(new SQLParameterValue(FIELD_GROSS_NONTAXABLE_RETURNS_VOID_COUNT + "=" + FIELD_GROSS_NONTAXABLE_RETURNS_VOID_COUNT + "+?", getNonTaxableReturnsVoidCount(totals)));
        // set timestamp
        sql.addColumn(new SQLParameterFunction(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction()));
    }

    /**
     * Adds the values from the tender financial totals to the existing values
     * in the database.
     * 
     * @param sql The SQL statement
     * @param totals The financial totals to draw the values from
     * @param tenderType The type of tender
     * @param loans The combined list of tender loans
     * @param pickups The combined list of tender pickups
     * @param payIns the combined list of till pay-ins
     * @param payOuts the combined list of till pay-outs
     */
    protected void addTenderTotals(SQLUpdatableStatementIfc sql,
            FinancialTotalsIfc totals,
            FinancialCountTenderItemIfc tenderItem,
            ReconcilableCountIfc loans,
            ReconcilableCountIfc pickups,
            ReconcilableCountIfc payIns,
            ReconcilableCountIfc payOuts)
    {
        TenderDescriptorIfc tenderDescriptor = tenderItem.getTenderDescriptor();

        sql.addColumn(new SQLParameterValue(FIELD_STORE_TENDER_LOAN_MEDIA_TOTAL_AMOUNT + "=" + FIELD_STORE_TENDER_LOAN_MEDIA_TOTAL_AMOUNT + "+?", getTenderLoanAmount(loans, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TENDER_OVER_TOTAL_AMOUNT + "=" + FIELD_STORE_TENDER_OVER_TOTAL_AMOUNT + "+?", getTenderOverAmount(totals, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TENDER_PICKUP_MEDIA_TOTAL_AMOUNT + "=" + FIELD_STORE_TENDER_PICKUP_MEDIA_TOTAL_AMOUNT + "+?", getTenderPickupAmount(pickups, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TENDER_SHORT_TOTAL_AMOUNT + "=" + FIELD_STORE_TENDER_SHORT_TOTAL_AMOUNT + "+?", getTenderShortAmount(totals, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_BEGINNING_TENDER_MEDIA_UNIT_COUNT + "=" + FIELD_STORE_TOTAL_BEGINNING_TENDER_MEDIA_UNIT_COUNT + "+?", getTenderBeginningCount(totals, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TENDER_LOAN_MEDIA_UNIT_COUNT + "=" + FIELD_STORE_TOTAL_TENDER_LOAN_MEDIA_UNIT_COUNT + "+?", getTenderLoanCount(loans, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TENDER_MEDIA_COUNT + "=" + FIELD_STORE_TOTAL_TENDER_MEDIA_COUNT + "+?", getTenderCount(totals, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TENDER_MEDIA_OVER_COUNT + "=" + FIELD_STORE_TOTAL_TENDER_MEDIA_OVER_COUNT + "+?", getTenderOverCount(totals, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TENDER_MEDIA_SHORT_COUNT + "=" + FIELD_STORE_TOTAL_TENDER_MEDIA_SHORT_COUNT + "+?", getTenderShortCount(totals, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TENDER_PICKUP_MEDIA_UNIT_COUNT + "=" + FIELD_STORE_TOTAL_TENDER_PICKUP_MEDIA_UNIT_COUNT + "+?", getTenderPickupCount(pickups, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TENDER_REFUND_MEDIA_UNIT_COUNT + "=" + FIELD_STORE_TOTAL_TENDER_REFUND_MEDIA_UNIT_COUNT + "+?", getTenderRefundCount(totals, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TENDER_REFUND_TOTAL_AMOUNT + "=" + FIELD_STORE_TENDER_REFUND_TOTAL_AMOUNT + "+?", getTenderRefundAmount(totals, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TENDER_TOTAL_AMOUNT + "=" + FIELD_STORE_TENDER_TOTAL_AMOUNT + "+?", getTenderTotalAmount(totals, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TENDER_OPEN_AMOUNT + "=" + FIELD_STORE_TENDER_OPEN_AMOUNT + "+?", getTenderOpenAmount(totals, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TENDER_CLOSE_AMOUNT + "=" + FIELD_STORE_TENDER_CLOSE_AMOUNT + "+?", getTenderCloseAmount(totals, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TENDER_MEDIA_CLOSE_COUNT + "=" + FIELD_STORE_TENDER_MEDIA_CLOSE_COUNT + "+?", getTenderCloseCount(totals, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_FUNDS_RECEIVED_IN_MEDIA_TOTAL_AMOUNT + "=" + FIELD_STORE_FUNDS_RECEIVED_IN_MEDIA_TOTAL_AMOUNT + "+?", getTillPayInAmount(payIns, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_FUNDS_RECEIVED_OUT_MEDIA_TOTAL_AMOUNT + "=" + FIELD_STORE_FUNDS_RECEIVED_OUT_MEDIA_TOTAL_AMOUNT + "+?", getTillPayOutAmount(payOuts, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_FUNDS_RECEIVED_IN_MEDIA_UNIT_COUNT + "=" + FIELD_STORE_FUNDS_RECEIVED_IN_MEDIA_UNIT_COUNT + "+?", getTillPayInCount(payIns, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_FUNDS_RECEIVED_OUT_MEDIA_UNIT_COUNT + "=" + FIELD_STORE_FUNDS_RECEIVED_OUT_MEDIA_UNIT_COUNT + "+?", getTillPayOutCount(payOuts, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_RECONCILE_AMOUNT + "=" + FIELD_RECONCILE_AMOUNT + "+?", getTillReconcileAmount(totals, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_RECONCILE_MEDIA_UNIT_COUNT + "=" + FIELD_RECONCILE_MEDIA_UNIT_COUNT + "+?", getTillReconcileCount(totals, tenderDescriptor)));
    }

    /**
     * Adds the values from the tender financial totals to the existing values
     * in the database.
     * 
     * @param sql The SQL statement
     * @param totals The financial totals to draw the values from
     * @param tenderType The type of tender
     * @param loans The combined list of tender loans
     * @param pickups The combined list of tender pickups
     * @param payIns combined list of till pay-ins
     * @param payOuts combined list of till pay-outs
     */
    protected void updateTenderTotals(SQLUpdatableStatementIfc sql,
            FinancialTotalsIfc totals,
            FinancialCountTenderItemIfc tenderItem,
            ReconcilableCountIfc loans,
            ReconcilableCountIfc pickups,
            ReconcilableCountIfc payIns,
            ReconcilableCountIfc payOuts)
    {
        TenderDescriptorIfc tenderDescriptor = tenderItem.getTenderDescriptor();

        sql.addColumn(new SQLParameterValue(FIELD_STORE_TENDER_LOAN_MEDIA_TOTAL_AMOUNT, getTenderLoanAmount(loans, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TENDER_OVER_TOTAL_AMOUNT, getTenderOverAmount(totals, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TENDER_PICKUP_MEDIA_TOTAL_AMOUNT, getTenderPickupAmount(pickups, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TENDER_SHORT_TOTAL_AMOUNT, getTenderShortAmount(totals, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_BEGINNING_TENDER_MEDIA_UNIT_COUNT, getTenderBeginningCount(totals, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TENDER_LOAN_MEDIA_UNIT_COUNT, getTenderLoanCount(loans, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TENDER_MEDIA_COUNT, getTenderCount(totals, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TENDER_MEDIA_OVER_COUNT, getTenderOverCount(totals, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TENDER_MEDIA_SHORT_COUNT, getTenderShortCount(totals, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TENDER_PICKUP_MEDIA_UNIT_COUNT, getTenderPickupCount(pickups, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TOTAL_TENDER_REFUND_MEDIA_UNIT_COUNT, getTenderRefundCount(totals, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TENDER_REFUND_TOTAL_AMOUNT, getTenderRefundAmount(totals, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TENDER_TOTAL_AMOUNT, getTenderTotalAmount(totals, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TENDER_OPEN_AMOUNT, getTenderOpenAmount(totals, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TENDER_CLOSE_AMOUNT, getTenderCloseAmount(totals, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_TENDER_MEDIA_CLOSE_COUNT, getTenderCloseCount(totals, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_FUNDS_RECEIVED_IN_MEDIA_TOTAL_AMOUNT, getTillPayInAmount(payIns, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_FUNDS_RECEIVED_OUT_MEDIA_TOTAL_AMOUNT, getTillPayOutAmount(payOuts, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_FUNDS_RECEIVED_IN_MEDIA_UNIT_COUNT, getTillPayInCount(payIns, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_STORE_FUNDS_RECEIVED_OUT_MEDIA_UNIT_COUNT, getTillPayOutCount(payOuts, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_RECONCILE_AMOUNT, getTillReconcileAmount(totals, tenderDescriptor)));
        sql.addColumn(new SQLParameterValue(FIELD_RECONCILE_MEDIA_UNIT_COUNT, getTillReconcileCount(totals, tenderDescriptor)));
    }

    /**
     * Returns a count with combined totals
     * 
     * @param counts An array of counts to combine
     * @return a combined count
     */
    protected ReconcilableCountIfc combineByTenderType(ReconcilableCountIfc[] counts)
    {
        ReconcilableCountIfc combined = instantiateReconcilableCount();
        combined.resetTotals();

        if (counts != null)
        {
            for (int i = 0; i < counts.length; ++i)
            {
                combined.add(counts[i]);
            }
        }

        return(combined);
    }

    /**
     * Returns a new instance of ReconcilableCountIfc.
     * 
     * @return a new instance of ReconcilableCountIfc.
     */
    protected ReconcilableCountIfc instantiateReconcilableCount()
    {
        return (new ReconcilableCount());
    }

    /**
     * Returns the reporting period
     * 
     * @param status The store status object
     * @return the reporting period
     */
    protected EYSDate getReportingPeriod(StoreStatusIfc status)
    {
        /*
         * Use the business date for store level stuff
         */
        return (status.getBusinessDate());
    }

    /**
     * Returns the store status code
     * 
     * @param status The store status object
     * @return the store status code
     */
    protected int getStoreStatusCode(StoreStatusIfc status)
    {
        return status.getStatus();
    }

    /**
     * Returns the store id
     * 
     * @param status The store status object
     * @return the store id
     */
    protected String getStoreID(StoreStatusIfc status)
    {
        return status.getStore().getStoreID();
    }

    /**
     * Returns the store id
     * 
     * @param storeID The store id
     * @return the store id
     * @deprecated as of 13.1.1 use storeID instead
     */
    protected String getStoreID(String storeID)
    {
        return storeID;
    }

    /**
     * Returns the fiscal year. Assumes fiscal year and calendar year are the
     * same.
     * <p>
     * Replaces {@link #getFiscalYear(EYSDate)} to avoid quotes around the result.
     * 
     * @param businessDate the business date
     * @return the fiscal year
     */
    protected String getFiscalYearCode(EYSDate businessDate)
    {
        return Integer.toString(businessDate.getYear());
    }

    /**
     * Returns the reporting period type.
     * <p>
     * Replaces {@link #getReportingPeriodType(EYSDate)} to avoid quotes around the result.
     * 
     * @param businessDate the business date
     * @return the reporting period type
     */
    protected String getReportingPeriodTypeCode(EYSDate businessDate)
    {
        int code = ReportingPeriodIfc.TYPE_BUSINESS_DAY;
        return ReportingPeriodIfc.REPORTING_PERIOD_CODES[code];
    }

    /**
     * Returns the reporting period id.
     * <p>
     * Replaces {@link #getReportingPeriodID(EYSDate)}
     * 
     * @param businessDate the business date
     * @return the reporting period id
     */
    protected int getReportingPeriod(EYSDate businessDate)
    {
        Calendar c = businessDate.calendarValue();
        return c.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Returns the tender type
     * 
     * @param tenderType The type of tender
     * @return the tender type
     */
    protected String getTenderType(String tenderType)
    {
        String value = tenderTypeMap.getCode(tenderTypeMap.getTypeFromDescriptor(tenderType));

        return value;
    }

    /**
     * Returns the operator id
     * 
     * @param status The store status object
     * @return the operator id
     */
    protected String getOperatorID(StoreStatusIfc status)
    {
        return status.getSignOnOperator().getEmployeeID();
    }

    /**
     * Returns the starting timestamp
     * 
     * @param status The store status object
     * @return the starting timestamp
     */
    protected Date getStartTime(StoreStatusIfc status)
    {
        return status.getOpenTime().dateValue();
    }

    /**
     * Returns the number of transactions
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of transactions
     */
    protected int getTransactionCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getTransactionCount();
        }
        return value;
    }

    /**
     * Returns the monetary amount of line item voids
     * 
     * @param totals The financial totals to extract the information from
     * @return the monetary amount of line item voids
     */
    protected CurrencyIfc getLineVoidAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountLineVoids();
        }
        return value;
    }

    /**
     * Returns the number of line item voids
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of line item voids
     */
    protected BigDecimal getLineVoidCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsLineVoids();
        }
        return value;
    }

    /**
     * Returns the monetary amount of post transaction voids
     * 
     * @param totals The financial totals to extract the information from
     * @return the monetary amount of post transaction voids
     */
    protected CurrencyIfc getPostVoidAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountPostVoids();
        }
        return value;
    }

    /**
     * Returns the number of post transaction voids
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of post transaction voids
     */
    protected int getPostVoidCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getNumberPostVoids();
        }
        return value;
    }

    /**
     * Returns the monetary amount of transaction voids
     * 
     * @param totals The financial totals to extract the information from
     * @return the monetary amount of transaction voids
     */
    protected CurrencyIfc getVoidAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountCancelledTransactions();
        }
        return value;
    }

    /**
     * Returns the number of transaction voids
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of transaction voids
     */
    protected int getVoidCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getNumberCancelledTransactions();
        }
        return value;
    }

    /**
     * Returns the number of no sale transactions
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of no sale transactions
     */
    protected int getNoSaleCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getNumberNoSales();
        }
        return value;
    }

    /**
     * Returns the monetary amount of transaction discounts
     * 
     * @param totals The financial totals to extract the information from
     * @return the monetary amount of transaction discounts
     */
    protected CurrencyIfc getTransactionDiscountAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountTransactionDiscounts();
        }
        return value;
    }

    /**
     * Returns the number of transaction discounts
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of transaction discounts
     */
    protected int getTransactionDiscountCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getNumberTransactionDiscounts();
        }
        return value;
    }

    /**
     * Returns the monetary amount of price discounts
     * 
     * @param totals The financial totals to extract the information from
     * @return the monetary amount of price discounts
     */
    protected CurrencyIfc getDiscountAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountItemDiscounts();
        }
        return value;
    }

    /**
     * Returns the number of price discounts
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of price discounts
     */
    protected int getDiscountCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getNumberItemDiscounts();
        }
        return value;
    }

    /**
     * Returns the monetary amount of price markdowns
     * 
     * @param totals The financial totals to extract the information from
     * @return the monetary amount of price markdowns
     */
    protected CurrencyIfc getMarkdownAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountItemMarkdowns();
        }
        return value;
    }

    /**
     * Returns the number of price markdowns
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of price markdowns
     */
    protected int getMarkdownCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getNumberItemMarkdowns();
        }
        return value;
    }

    /**
     * Returns the monetary amount of TransactionDiscStoreCoupon
     * 
     * @param totals The financial totals to extract the information from
     * @return the monetary amount of TransactionDiscStoreCoupons
     */
    protected CurrencyIfc getTransactionDiscStoreCouponAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountTransactionDiscStoreCoupons();
        }
        return value;
    }

    /**
     * Returns the number of TransactionDiscStoreCoupon
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of TransactionDiscStoreCoupons
     */
    protected int getTransactionDiscStoreCouponCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getNumberTransactionDiscStoreCoupons();
        }
        return value;
    }

    /**
     * Returns the monetary amount of ItemDiscStoreCoupon
     * 
     * @param totals The financial totals to extract the information from
     * @return the monetary amount of ItemDiscStoreCoupons
     */
    protected CurrencyIfc getItemDiscStoreCouponAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountItemDiscStoreCoupons();
        }
        return value;
    }

    /**
     * Returns the number of ItemDiscStoreCoupon
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of ItemDiscStoreCoupons
     */
    protected int getItemDiscStoreCouponCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getNumberItemDiscStoreCoupons();
        }
        return value;
    }

    /**
     * Returns the sales amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the sales amount
     */
    protected CurrencyIfc getSalesAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            CurrencyIfc nonTaxable = totals.getAmountGrossNonTaxableTransactionSales().subtract(
                    totals.getAmountGrossNonTaxableTransactionSalesVoided());
            value = totals.getAmountGrossTaxableTransactionSales().subtract(
                    totals.getAmountGrossTaxableTransactionSalesVoided()).add(nonTaxable);
        }
        return value;
    }

    /**
     * Returns the net nontaxable amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the net nontaxable amount
     */
    protected CurrencyIfc getNetNontaxableAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            CurrencyIfc refunds = totals.getAmountGrossNonTaxableTransactionReturns().subtract(
                    totals.getAmountGrossNonTaxableTransactionReturnsVoided()).subtract(
                    totals.getNetAmountGiftCardItemRedeemed());
            value = totals.getAmountGrossNonTaxableTransactionSales().subtract(
                    totals.getAmountGrossNonTaxableTransactionSalesVoided()).subtract(refunds);
        }
        return value;
    }

    /**
     * Returns the nontaxable count
     * 
     * @param totals The financial totals to extract the information from
     * @return the nontaxable count
     */
    protected int getNontaxableCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getCountGrossNonTaxableTransactionSales()
                    + totals.getCountGrossNonTaxableTransactionSalesVoided()
                    + totals.getCountGrossNonTaxableTransactionReturns()
                    + totals.getCountGrossNonTaxableTransactionReturnsVoided();
        }
        return value;
    }

    /**
     * Returns the taxable transaction count
     * 
     * @param totals The financial totals to extract the information from
     * @return the taxable count
     */
    protected int getTaxableCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getCountGrossTaxableTransactionSales()
                    + totals.getCountGrossTaxableTransactionSalesVoided()
                    + totals.getCountGrossTaxableTransactionReturns()
                    + totals.getCountGrossTaxableTransactionReturnsVoided();
        }
        return value;
    }

    /**
     * Returns the net tax exempt amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the net tax exempt amount
     */
    protected CurrencyIfc getNetTaxExemptAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            CurrencyIfc refunds = totals.getAmountGrossTaxExemptTransactionReturns().subtract(
                    totals.getAmountGrossTaxExemptTransactionReturnsVoided());
            value = totals.getAmountGrossTaxExemptTransactionSales().subtract(
                    totals.getAmountGrossTaxExemptTransactionSalesVoided()).subtract(refunds);
        }
        return value;
    }

    /**
     * Returns the number of tax exempt transactions
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of tax exempt transactions
     */
    protected int getTaxExemptCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getCountGrossTaxExemptTransactionSales()
                    - totals.getCountGrossTaxExemptTransactionSalesVoided()
                    + totals.getCountGrossTaxExemptTransactionReturns()
                    - totals.getCountGrossTaxExemptTransactionReturnsVoided();
        }
        return value;
    }

    /**
     * Returns the refund amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the refund amount
     */
    protected CurrencyIfc getRefundAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            CurrencyIfc nonTaxable = totals.getAmountGrossNonTaxableTransactionReturns().subtract(
                    totals.getAmountGrossNonTaxableTransactionReturnsVoided());
            value = totals.getAmountGrossTaxableTransactionReturns().subtract(
                    totals.getAmountGrossTaxableTransactionReturnsVoided()).add(nonTaxable);
        }
        return value;
    }

    /**
     * Returns the number of refunds
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of refunds
     */
    protected int getRefundCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getCountGrossTaxableTransactionReturns()
                    - totals.getCountGrossTaxableTransactionReturnsVoided()
                    + totals.getCountGrossNonTaxableTransactionReturns()
                    - totals.getCountGrossNonTaxableTransactionReturnsVoided();
        }
        return value;
    }

    /**
     * Returns the nontaxable refund amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the nontaxable refund amount
     */
    protected CurrencyIfc getRefundNontaxableAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossNonTaxableTransactionReturns().subtract(
                    totals.getAmountGrossNonTaxableTransactionReturnsVoided());
        }
        return value;
    }

    /**
     * Returns the number of nontaxable refunds
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of nontaxable refunds
     */
    protected int getRefundNontaxableCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getCountGrossNonTaxableTransactionReturns()
                    - totals.getCountGrossNonTaxableTransactionReturnsVoided();
        }
        return value;
    }

    /**
     * Returns the tax exempt refund amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the tax exempt refund amount
     */
    protected CurrencyIfc getRefundTaxExemptAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossTaxExemptTransactionReturns().subtract(
                    totals.getAmountGrossTaxExemptTransactionReturnsVoided());
        }
        return value;
    }

    /**
     * Returns the number of tax exempt refunds
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of tax exempt refunds
     */
    protected int getRefundTaxExemptCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getCountGrossTaxExemptTransactionReturns()
                    - totals.getCountGrossTaxExemptTransactionReturnsVoided();
        }
        return value;
    }

    /**
     * Returns the item sales amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the item sales amount
     */
    protected CurrencyIfc getItemSalesAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            CurrencyIfc nonTaxable = totals.getAmountGrossNonTaxableItemSales().subtract(
                    totals.getAmountGrossNonTaxableItemSalesVoided());
            value = totals.getAmountGrossTaxableItemSales().subtract(totals.getAmountGrossTaxableItemSalesVoided())
                    .add(nonTaxable);
        }
        return value;
    }

    /**
     * Returns the number of line items sold or returned
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of line items sold or returned
     */
    protected BigDecimal getItemSalesCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossTaxableItemSales().subtract(
                    totals.getUnitsGrossTaxableItemSalesVoided()).add(
                    totals.getUnitsGrossNonTaxableItemSales().subtract(
                    totals.getUnitsGrossNonTaxableItemSalesVoided()));
        }
        return value;
    }

    /**
     * Returns the nontaxable item sales amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the nontaxable item sales amount
     */
    protected CurrencyIfc getItemNontaxableAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossNonTaxableItemSales().subtract(
                    totals.getAmountGrossNonTaxableItemSalesVoided());
        }
        return value;
    }

    /**
     * Returns the number of nontaxable line items sold
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of nontaxale line items sold
     */
    protected BigDecimal getItemNontaxableCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossNonTaxableItemSales().subtract(totals.getUnitsGrossNonTaxableItemSalesVoided());
        }
        return value;
    }

    /**
     * Returns the tax exempt item sales amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the tax exempt item sales amount
     */
    protected CurrencyIfc getItemTaxExemptAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossTaxExemptItemSales().subtract(totals.getAmountGrossTaxExemptItemSalesVoided());
        }
        return value;
    }

    /**
     * Returns the number of tax exempt line items sold
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of tax exempt line items sold
     */
    protected BigDecimal getItemTaxExemptCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossTaxExemptItemSales().subtract(totals.getUnitsGrossTaxExemptItemSalesVoided())
                    ;
        }
        return value;
    }

    /**
     * Returns the return amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the return amount
     */
    protected CurrencyIfc getReturnAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            CurrencyIfc nonTaxable = totals.getAmountGrossNonTaxableItemReturns().subtract(
                    totals.getAmountGrossNonTaxableItemReturnsVoided());
            value = totals.getAmountGrossTaxableItemReturns().subtract(totals.getAmountGrossTaxableItemReturnsVoided())
                    .add(nonTaxable);
        }
        return value;
    }

    /**
     * Returns the number of returns
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of returns
     */
    protected BigDecimal getReturnCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossTaxableItemReturns().subtract(totals.getUnitsGrossTaxableItemReturnsVoided())
                    .add(
                            totals.getUnitsGrossNonTaxableItemReturns().subtract(
                                    totals.getUnitsGrossNonTaxableItemReturnsVoided()));
        }
        return value;
    }

    /**
     * Returns the nontaxable item return amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the nontaxable item return amount
     */
    protected CurrencyIfc getReturnNontaxableAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossNonTaxableItemReturns().subtract(
                    totals.getAmountGrossNonTaxableItemReturnsVoided());
        }
        return value;
    }

    /**
     * Returns the number of nontaxable line items returned
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of nontaxale line items returned
     */
    protected BigDecimal getReturnNontaxableCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossNonTaxableItemReturns().subtract(
                    totals.getUnitsGrossNonTaxableItemReturnsVoided());
        }
        return value;
    }

    /**
     * Returns the tax exempt item return amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the tax exempt item return amount
     */
    protected CurrencyIfc getReturnTaxExemptAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossTaxExemptItemReturns().subtract(
                    totals.getAmountGrossTaxExemptItemReturnsVoided());
        }
        return value;
    }

    /**
     * Returns the number of tax exempt line items returned
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of tax exempt line items returned
     */
    protected BigDecimal getReturnTaxExemptCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossTaxExemptItemReturns().subtract(
                    totals.getUnitsGrossTaxExemptItemReturnsVoided());
        }
        return value;
    }

    /**
     * Returns the string value of non merchandise non tax amount
     * 
     * @param totals FinancialTotalsIfc container of value to be converted
     * @return String the value as a string
     */
    protected CurrencyIfc getNonMerchNonTaxAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountNetNonTaxableNonMerchandiseSales();
        }
        return value;
    }

    /**
     * Returns the string value of non merchandise non tax count
     * 
     * @param totals FinancialTotalsIfc container of value to be converted
     * @return String the value as a string
     */
    protected BigDecimal getNonMerchNonTaxCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsNetNonTaxableNonMerchandiseSales();
        }
        return value;
    }

    /**
     * Returns the string value of non merchandise non tax return amount
     * 
     * @param totals FinancialTotalsIfc container of value to be converted
     * @return String the value as a string
     */
    protected CurrencyIfc getNonMerchNonTaxReturnAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossNonTaxableNonMerchandiseReturns().subtract(
                    totals.getAmountGrossNonTaxableNonMerchandiseReturnsVoided());
        }
        return value;
    }

    /**
     * Returns the string value of non merchandise non tax return count
     * 
     * @param totals FinancialTotalsIfc container of value to be converted
     * @return String the value as a string
     */
    protected BigDecimal getNonMerchNonTaxReturnCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossNonTaxableNonMerchandiseReturns().subtract(
                    totals.getUnitsGrossNonTaxableNonMerchandiseReturnsVoided());
        }
        return value;
    }

    /**
     * Returns the string value of non merchandise tax amount
     * 
     * @param totals FinancialTotalsIfc container of value to be converted
     * @return String the value as a string
     */
    protected CurrencyIfc getNonMerchAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountNetTaxableNonMerchandiseSales();
        }
        return value;
    }

    /**
     * Returns the string value of non merchandise tax count
     * 
     * @param totals FinancialTotalsIfc container of value to be converted
     * @return String the value as a string
     */
    protected BigDecimal getNonMerchCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsNetTaxableNonMerchandiseSales();
        }
        return value;
    }

    /**
     * Returns the string value of non merchandise tax return amount
     * 
     * @param totals FinancialTotalsIfc container of value to be converted
     * @return String the value as a string
     */
    protected CurrencyIfc getNonMerchReturnAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossTaxableNonMerchandiseReturns().subtract(
                    totals.getAmountGrossTaxableNonMerchandiseReturnsVoided());
        }
        return value;
    }

    /**
     * Returns the string value of non merchandise tax return count
     * 
     * @param totals FinancialTotalsIfc container of value to be converted
     * @return String the value as a string
     */
    protected BigDecimal getNonMerchReturnCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossTaxableNonMerchandiseReturns().subtract(
                    totals.getUnitsGrossTaxableNonMerchandiseReturnsVoided());
        }
        return value;
    }

    /**
     * Returns the string value of gift card amount
     * 
     * @param totals FinancialTotalsIfc container of value to be converted
     * @return String the value as a string
     */
    protected CurrencyIfc getGiftCardAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossGiftCardItemSales().subtract(totals.getAmountGrossGiftCardItemSalesVoided());
        }
        return value;
    }

    /**
     * Returns the string value of gift card count
     * 
     * @param totals FinancialTotalsIfc container of value to be converted
     * @return String the value as a string
     */
    protected BigDecimal getGiftCardCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsNetGiftCardItemSales();
        }
        return value;
    }

    /**
     * Returns the string value of gift card return amount
     * 
     * @param totals FinancialTotalsIfc container of value to be converted
     * @return String the value as a string
     */
    protected CurrencyIfc getGiftCardReturnAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossGiftCardItemReturns().subtract(
                    totals.getAmountGrossGiftCardItemReturnsVoided());
        }
        return value;
    }

    /**
     * Returns the string value of gift card return count
     * 
     * @param totals FinancialTotalsIfc container of value to be converted
     * @return String the value as a string
     */
    protected BigDecimal getGiftCardReturnCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossGiftCardItemReturns().subtract(totals.getUnitsGrossGiftCardItemReturnsVoided())
                    ;
        }
        return value;
    }

    /**
     * Returns the amount of house account payments
     * 
     * @param totals The financial totals to extract the information from
     * @return the amount of house account payments
     */
    protected CurrencyIfc getHousePaymentAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountHousePayments();
        }

        return value;
    }

    /**
     * Returns the number of house account payments
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of house account payments
     */
    protected int getHousePaymentCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getCountHousePayments();
        }

        return value;
    }

    /**
     * Returns the number of restocking fees
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of restocking fees
     */
    protected BigDecimal getRestockingFeeCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsRestockingFees();
        }

        return value;
    }

    /**
     * Returns the amount of restocking fees
     * 
     * @param totals The financial totals to extract the information from
     * @return the amount of restocking fees
     */
    protected CurrencyIfc getRestockingFeeAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountRestockingFees();
        }

        return value;
    }

    /**
     * Returns the number of restocking fees from non taxable items
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of restocking fees
     */
    protected BigDecimal getRestockingFeeFromNonTaxableCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsRestockingFeesFromNonTaxableItems();
        }

        return value;
    }

    /**
     * Returns the amount of restocking fees from non taxable items
     * 
     * @param totals The financial totals to extract the information from
     * @return the amount of restocking fees
     */
    protected CurrencyIfc getRestockingFeeFromNonTaxableAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountRestockingFeesFromNonTaxableItems();
        }

        return value;
    }

    /**
     * Returns the number of shipping charges
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of shipping charges
     */
    protected int getShippingChargeCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getNumberShippingCharges();
        }

        return value;
    }

    /**
     * Returns the amount of shipping charges
     * 
     * @param totals The financial totals to extract the information from
     * @return the amount of shipping charges
     */
    protected CurrencyIfc getShippingChargeAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountShippingCharges();
        }

        return value;
    }

    /**
     * Returns the amount of shipping charges tax
     * 
     * @param totals The financial totals to extract the information from
     * @return the amount of shipping charges tax
     */
    protected CurrencyIfc getShippingChargeTax(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountTaxShippingCharges();
        }

        return value;
    }

    /**
     * Returns the amount of shipping charges inclusive tax
     * 
     * @param totals The financial totals to extract the information from
     * @return the amount of shipping charges inclusive tax
     */
    protected CurrencyIfc getShippingChargeInclusiveTax(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountInclusiveTaxShippingCharges();
        }

        return value;
    }

    /**
     * Returns the net tax amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the net tax amount
     */
    protected CurrencyIfc getNetTaxAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            CurrencyIfc taxTransactionReturns = totals.getAmountTaxTransactionReturns();
            value = totals.getAmountTaxTransactionSales().subtract(taxTransactionReturns);
        }
        return value;
    }

    /**
     * Returns the net inclusive tax amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the net inclusive tax amount
     */
    protected CurrencyIfc getNetInclusiveTaxAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            CurrencyIfc taxTransactionReturns = totals.getAmountInclusiveTaxTransactionReturns();
            value = totals.getAmountInclusiveTaxTransactionSales().subtract(taxTransactionReturns);
        }
        return value;
    }

    /**
     * Returns the tax refunded amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the tax refunded amount
     */
    protected CurrencyIfc getTaxRefundedAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountTaxTransactionReturns();
        }
        return value;
    }

    /**
     * Returns the inclusive tax refunded amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the inclusive tax refunded amount
     */
    protected CurrencyIfc getInclusiveTaxRefundedAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountInclusiveTaxTransactionReturns();
        }
        return value;
    }

    /**
     * Returns the tax returned amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the tax returned amount
     */
    protected CurrencyIfc getTaxReturnedAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountTaxItemReturns();
        }
        return value;
    }

    /**
     * Returns the inclusive tax returned amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the inclusive tax returned amount
     */
    protected CurrencyIfc getInclusiveTaxReturnedAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountInclusiveTaxItemReturns();
        }
        return value;
    }

    /**
     * Returns the number of tender pickups
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of tender pickups
     */
    protected int getTenderPickupCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getCountTillPickups();
        }
        return value;
    }

    /**
     * Returns the number of till pay-outs
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of till pay-outs
     */
    protected int getTillPayOutCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getCountTillPayOuts();
        }
        return value;
    }

    /**
     * Returns the number of till pay-ins
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of till pay-ins
     */
    protected int getTillPayInCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getCountTillPayIns();
        }
        return value;
    }

    /**
     * Returns the amount of tender pickups
     * 
     * @param totals The financial totals to extract the information from
     * @return the amount of tender pickups
     */
    protected CurrencyIfc getTenderPickupAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountTillPickups();
        }
        return value;
    }

    /**
     * Returns the amount of till pay-outs
     * 
     * @param totals The financial totals to extract the information from
     * @return the amount of till pay-outs
     */
    protected CurrencyIfc getTillPayOutAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountTillPayOuts();
        }
        return value;
    }

    /**
     * Returns the amount of till pay-ins
     * 
     * @param totals The financial totals to extract the information from
     * @return the amount of till pay-ins
     */
    protected CurrencyIfc getTillPayInAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountTillPayIns();
        }
        return value;
    }

    /**
     * Returns the number of tender loans
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of tender loans
     */
    protected int getTenderLoanCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getCountTillLoans();
        }
        return value;
    }

    /**
     * Returns the amount of tender loans
     * 
     * @param totals The financial totals to extract the information from
     * @return the amount of tender loans
     */
    protected CurrencyIfc getTenderLoanAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountTillLoans();
        }
        return value;
    }

    /**
     * Returns the tender loan amount
     * 
     * @param loans The till loans
     * @param tenderType The type of tender
     * @return the tender loan amount
     */
    protected CurrencyIfc getTenderLoanAmount(ReconcilableCountIfc loans, TenderDescriptorIfc tenderType)
    {
        CurrencyIfc value = ZERO;

        FinancialCountIfc count = loans.getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = item.getAmountIn().subtract(item.getAmountOut());
        }

        return value;
    }

    /**
     * Returns the tender over amount
     * 
     * @param totals The financial totals information
     * @param tenderType The type of tender
     * @return the tender over amount
     */
    protected CurrencyIfc getTenderOverAmount(FinancialTotalsIfc totals, TenderDescriptorIfc tenderType)
    {
        FinancialCountIfc expectedCount = totals.getCombinedCount().getExpected();
        FinancialCountTenderItemIfc expectedTender = expectedCount.getSummaryTenderItemByDescriptor(tenderType);
        FinancialCountIfc enteredCount = totals.getCombinedCount().getEntered();
        FinancialCountTenderItemIfc enteredTender = enteredCount.getSummaryTenderItemByDescriptor(tenderType);

        CurrencyIfc zero = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc amount;

        if (expectedTender != null && enteredTender != null)
        {
            amount = enteredTender.getAmountTotal().subtract(expectedTender.getAmountTotal());
        }
        else if (enteredTender == null)
        {
            if (expectedTender != null)
            {
                amount = expectedTender.getAmountTotal().negate();
            }
            else
            // both are null
            {
                amount = zero;
            }
        }
        else
        // expectedTender == null
        {
            amount = enteredTender.getAmountTotal();
        }

        if (amount.compareTo(zero) == CurrencyIfc.LESS_THAN)
        {
            amount = zero;
        }

        return amount;
    }

    /**
     * Returns the tender pickup amount
     * 
     * @param pickups The till pickups
     * @param tenderType The type of tender
     * @return the tender pickup amount
     */
    protected CurrencyIfc getTenderPickupAmount(ReconcilableCountIfc pickups, TenderDescriptorIfc tenderType)
    {
        CurrencyIfc value = ZERO;

        FinancialCountIfc count = pickups.getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = item.getAmountOut().subtract(item.getAmountIn());
        }

        return value;
    }

    /**
     * Returns the till pay-outs amount
     * 
     * @param payOuts The till payOuts
     * @param tenderType The type of tender
     * @return the tender pay-outs amount
     */
    protected CurrencyIfc getTillPayOutAmount(ReconcilableCountIfc payOuts, TenderDescriptorIfc tenderType)
    {
        CurrencyIfc value = ZERO;

        FinancialCountIfc count = payOuts.getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = item.getAmountOut().subtract(item.getAmountIn());
        }

        return value;
    }

    /**
     * Returns the till pay-ins amount
     * 
     * @param payIns The till payIns
     * @param tenderType The type of tender
     * @return the tender pay-ins amount
     */
    protected CurrencyIfc getTillPayInAmount(ReconcilableCountIfc payIns, TenderDescriptorIfc tenderType)
    {
        CurrencyIfc value = ZERO;

        FinancialCountIfc count = payIns.getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = item.getAmountIn().subtract(item.getAmountOut());
        }

        return value;
    }

    /**
     * Returns the tender short amount
     * 
     * @param totals The financial totals information
     * @param tenderType The type of tender
     * @return the tender short amount
     */
    protected CurrencyIfc getTenderShortAmount(FinancialTotalsIfc totals, TenderDescriptorIfc tenderType)
    {
        FinancialCountIfc expectedCount = totals.getCombinedCount().getExpected();
        FinancialCountTenderItemIfc expectedTender = expectedCount.getSummaryTenderItemByDescriptor(tenderType);

        FinancialCountIfc enteredCount = totals.getCombinedCount().getEntered();
        FinancialCountTenderItemIfc enteredTender = enteredCount.getSummaryTenderItemByDescriptor(tenderType);

        CurrencyIfc zero = DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc amount;

        if (expectedTender != null && enteredTender != null)
        {
            amount = enteredTender.getAmountTotal().subtract(expectedTender.getAmountTotal());
        }
        else if (enteredTender == null)
        {
            if (expectedTender != null)
            {
                amount = expectedTender.getAmountTotal().negate();
            }
            else
            // both are null
            {
                amount = zero;
            }
        }
        else
        // expectedTender == null
        {
            amount = enteredTender.getAmountTotal();
        }

        if (amount.compareTo(zero) == CurrencyIfc.LESS_THAN)
        {
            amount = amount.negate();
        }
        else
        {
            amount = zero;
        }

        return amount;
    }

    /**
     * Returns the tender beginning count
     * 
     * @param totals The financial totals information
     * @param tenderType The type of tender
     * @return the tender beginning count
     */
    protected int getTenderBeginningCount(FinancialTotalsIfc totals, TenderDescriptorIfc tenderType)
    {
        int value = 0;

        FinancialCountIfc count = totals.getStartingFloatCount().getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = item.getNumberItemsIn();
        }

        return value;
    }

    /**
     * Returns the tender loan count
     * 
     * @param loans The till loans
     * @param tenderType The type of tender
     * @return the tender loan count
     */
    protected int getTenderLoanCount(ReconcilableCountIfc loans, TenderDescriptorIfc tenderType)
    {
        int value = 0;

        FinancialCountIfc count = loans.getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = item.getNumberItemsIn() - item.getNumberItemsOut();
        }

        return value;
    }

    /**
     * Returns the tender count
     * 
     * @param totals The financial totals information
     * @param tenderType The type of tender
     * @return the tender count
     */
    protected int getTenderCount(FinancialTotalsIfc totals, TenderDescriptorIfc tenderType)
    {
        int value = 0;

        FinancialCountIfc count = totals.getTenderCount();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = item.getNumberItemsTotal();
        }

        return value;
    }

    /**
     * Returns the tender over count
     * 
     * @param totals The financial totals information
     * @param tenderType The type of tender
     * @return the tender over count
     */
    protected int getTenderOverCount(FinancialTotalsIfc totals, TenderDescriptorIfc tenderType)
    {
        FinancialCountIfc expectedCount = totals.getCombinedCount().getExpected();
        FinancialCountTenderItemIfc expectedTender = expectedCount.getSummaryTenderItemByDescriptor(tenderType);

        FinancialCountIfc enteredCount = totals.getCombinedCount().getEntered();
        FinancialCountTenderItemIfc enteredTender = enteredCount.getSummaryTenderItemByDescriptor(tenderType);

        int count;

        if (expectedTender != null && enteredTender != null)
        {
            count = enteredTender.getNumberItemsTotal() - expectedTender.getNumberItemsTotal();
        }
        else if (enteredTender == null)
        {
            if (expectedTender != null)
            {
                count = -expectedTender.getNumberItemsTotal();
            }
            else
            // both are null
            {
                count = 0;
            }
        }
        else
        // expectedTender == null
        {
            count = enteredTender.getNumberItemsTotal();
        }

        if (count < 0)
        {
            count = 0;
        }

        return count;
    }

    /**
     * Returns the tender short count
     * 
     * @param totals The financial totals information
     * @param tenderType The type of tender
     * @return the tender short count
     */
    protected int getTenderShortCount(FinancialTotalsIfc totals, TenderDescriptorIfc tenderType)
    {
        FinancialCountIfc expectedCount = totals.getCombinedCount().getExpected();
        FinancialCountTenderItemIfc expectedTender = expectedCount.getSummaryTenderItemByDescriptor(tenderType);

        FinancialCountIfc enteredCount = totals.getCombinedCount().getEntered();
        FinancialCountTenderItemIfc enteredTender = enteredCount.getSummaryTenderItemByDescriptor(tenderType);

        int count;

        if (expectedTender != null && enteredTender != null)
        {
            count = enteredTender.getNumberItemsTotal() - expectedTender.getNumberItemsTotal();
        }
        else if (enteredTender == null)
        {
            if (expectedTender != null)
            {
                count = -expectedTender.getNumberItemsTotal();
            }
            else
            // both are null
            {
                count = 0;
            }
        }
        else
        // expectedTender == null
        {
            count = enteredTender.getNumberItemsTotal();
        }

        if (count < 0)
        {
            count = -count;
        }
        else
        {
            count = 0;
        }

        return count;
    }

    /**
     * Returns the tender pickup count
     * 
     * @param pickups The till pickups
     * @param tenderType The type of tender
     * @return the tender pickup count
     */
    protected int getTenderPickupCount(ReconcilableCountIfc pickups, TenderDescriptorIfc tenderType)
    {
        int value = 0;

        FinancialCountIfc count = pickups.getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = item.getNumberItemsOut() - item.getNumberItemsIn();
        }

        return value;
    }

    /**
     * Returns the till pay-out count
     * 
     * @param payOuts The till pay-outs
     * @param tenderType The type of tender
     * @return the till pay-out count
     */
    protected int getTillPayOutCount(ReconcilableCountIfc payOuts, TenderDescriptorIfc tenderType)
    {
        int value = 0;

        FinancialCountIfc count = payOuts.getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = item.getNumberItemsOut() - item.getNumberItemsIn();
        }

        return value;
    }

    /**
     * Returns the till pay-in count
     * 
     * @param payIns The till pay-ins
     * @param tenderType The type of tender
     * @return the till pay-in count
     */
    protected int getTillPayInCount(ReconcilableCountIfc payIns, TenderDescriptorIfc tenderType)
    {
        int value = 0;

        FinancialCountIfc count = payIns.getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = item.getNumberItemsIn() - item.getNumberItemsOut();
        }

        return value;
    }

    /**
     * Returns the tender refund count
     * 
     * @param totals The financial totals information
     * @param tenderType The type of tender
     * @return the tender refund count
     */
    protected int getTenderRefundCount(FinancialTotalsIfc totals, TenderDescriptorIfc tenderType)
    {
        int value = 0;

        FinancialCountIfc count = totals.getTenderCount();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = item.getNumberItemsOut();
        }

        return value;
    }

    /**
     * Returns the tender refund amount
     * 
     * @param totals The financial totals information
     * @param tenderType The type of tender
     * @return the tender refund amount
     */
    protected CurrencyIfc getTenderRefundAmount(FinancialTotalsIfc totals, TenderDescriptorIfc tenderType)
    {
        CurrencyIfc value = ZERO;

        FinancialCountIfc count = totals.getTenderCount();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = item.getAmountOut();
        }

        return value;
    }

    /**
     * Returns the tender total amount
     * 
     * @param totals The financial totals information
     * @param tenderType The type of tender
     * @return the tender total amount
     */
    protected CurrencyIfc getTenderTotalAmount(FinancialTotalsIfc totals, TenderDescriptorIfc tenderType)
    {
        CurrencyIfc value = ZERO;

        FinancialCountIfc count = totals.getTenderCount();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = item.getAmountTotal();
        }

        return value;
    }

    /**
     * Returns the tender open amount
     * 
     * @param totals The financial totals information
     * @param tenderType The type of tender
     * @return the tender open amount
     */
    protected CurrencyIfc getTenderOpenAmount(FinancialTotalsIfc totals, TenderDescriptorIfc tenderType)
    {
        CurrencyIfc value = ZERO;

        FinancialCountIfc count = totals.getStartingFloatCount().getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = item.getAmountIn();
        }

        return value;
    }

    /**
     * Returns the tender close amount
     * 
     * @param totals The financial totals information
     * @param tenderType The type of tender
     * @return the tender close amount
     */
    protected CurrencyIfc getTenderCloseAmount(FinancialTotalsIfc totals, TenderDescriptorIfc tenderType)
    {
        CurrencyIfc value = ZERO;

        FinancialCountIfc count = totals.getEndingFloatCount().getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = item.getAmountOut();
        }

        return value;
    }

    /**
     * Returns the tender close count
     * 
     * @param totals The financial totals information
     * @param tenderType The type of tender
     * @return the tender close count
     */
    protected int getTenderCloseCount(FinancialTotalsIfc totals, TenderDescriptorIfc tenderType)
    {
        int value = 0;

        FinancialCountIfc count = totals.getEndingFloatCount().getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = item.getNumberItemsOut();
        }

        return value;
    }

    /**
     * Returns the taxable item sales amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the taxable item sales amount
     */
    protected CurrencyIfc getTaxableItemSalesAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossTaxableItemSales().subtract(
                    totals.getAmountGrossTaxableItemSalesVoided());
        }
        return value;
    }

    /**
     * Returns the taxable item returns amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the taxable item returns amount
     */
    protected CurrencyIfc getTaxableItemReturnsAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossTaxableItemReturns().subtract(
                    totals.getAmountGrossTaxableItemReturnsVoided());
        }
        return value;
    }

    /**
     * Returns the number of taxable line items sold
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of taxable line items sold
     */
    protected BigDecimal getTaxableItemSalesCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossTaxableItemSales().subtract(
                    totals.getUnitsGrossTaxableItemSalesVoided());
        }
        return value;
    }

    /**
     * Returns the number of taxable line items returned
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of taxable line items returned
     */
    protected BigDecimal getTaxableItemReturnsCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossTaxableItemReturns().subtract(
                    totals.getUnitsGrossTaxableItemReturnsVoided());
        }
        return value;
    }

    /**
     * Returns the taxable transaction sales amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the taxable transaction sales amount
     */
    protected CurrencyIfc getTaxableTransactionSalesAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossTaxableTransactionSales().subtract(
                    totals.getAmountGrossTaxableTransactionSalesVoided());
        }
        return value;
    }

    /**
     * Returns the number of taxable transaction sales
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of taxable transaction sales
     */
    protected int getTaxableTransactionSalesCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getCountGrossTaxableTransactionSales()
                    - totals.getCountGrossTaxableTransactionSalesVoided();
        }
        return value;
    }

    /**
     * Returns the taxable non merchandise sales amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the taxable non merchandise sales amount
     */
    protected CurrencyIfc getTaxableNonMerchandiseSalesAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossTaxableNonMerchandiseSales().subtract(
                    totals.getAmountGrossTaxableNonMerchandiseSalesVoided());
        }
        return value;
    }

    /**
     * Returns the numbers of taxable non merchandise sales
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of taxable non merchandise sales
     */
    protected BigDecimal getTaxableNonMerchandiseSalesCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossTaxableNonMerchandiseSales().subtract(
                    totals.getUnitsGrossTaxableNonMerchandiseSalesVoided());
        }
        return value;
    }

    /**
     * Returns the taxable transaction returns amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the taxable transaction returns amount
     */
    protected CurrencyIfc getTaxableTransactionReturnsAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossTaxableTransactionReturns().subtract(
                    totals.getAmountGrossTaxableTransactionReturnsVoided());
        }
        return value;
    }

    /**
     * Returns the number of taxable transaction returned
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of taxable transaction returned
     */
    protected int getTaxableTransactionReturnsCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getCountGrossTaxableTransactionReturns()
                    - totals.getCountGrossTaxableTransactionReturnsVoided();
        }
        return value;
    }

    /**
     * Returns the non taxable non merchandise sales amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the non taxable non merchandise sales amount
     */
    protected CurrencyIfc getNonTaxableNonMerchandiseSalesAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossNonTaxableNonMerchandiseSales().subtract(
                    totals.getAmountGrossNonTaxableNonMerchandiseSalesVoided());
        }
        return value;
    }

    /**
     * Returns the number of non taxable non merchandise sales
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of non taxable non merchandise sales
     */
    protected BigDecimal getNonTaxableNonMerchandiseSalesCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossNonTaxableNonMerchandiseSales().subtract(
                    totals.getUnitsGrossNonTaxableNonMerchandiseSalesVoided());
        }
        return value;
    }

    /**
     * Returns the non taxable transaction sales amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the non taxable transaction sales amount
     */
    protected CurrencyIfc getNonTaxableTransactionSalesAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossNonTaxableTransactionSales().subtract(
                    totals.getAmountGrossNonTaxableTransactionSalesVoided());
        }
        return value;
    }

    /**
     * Returns the number of non taxable transaction sales
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of non taxable tranaction sales
     */
    protected int getNonTaxableTransactionSalesCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getCountGrossNonTaxableTransactionSales()
                    - totals.getCountGrossNonTaxableTransactionSalesVoided();
        }
        return value;
    }

    /**
     * Returns the tax exempt transaction sales amount
     * 
     * @param totals The financial totals to extract the information from
     * @return the tax exempt transaction sales amount
     */
    protected CurrencyIfc getTaxExemptTransactionSalesAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossTaxExemptTransactionSales().subtract(
                    totals.getAmountGrossTaxExemptTransactionSalesVoided());
        }
        return value;
    }

    /**
     * Returns the number of tax exempt transaction sales
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of tax exempt tranaction sales
     */
    protected int getTaxExemptTransactionSalesCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getCountGrossTaxExemptTransactionSales()
                    - totals.getCountGrossTaxExemptTransactionSalesVoided();
        }
        return value;
    }

    /**
     * Returns the amount of item sales tax
     * 
     * @param totals The financial totals to extract the information from
     * @return the amount of item sales tax
     */
    protected CurrencyIfc getItemTaxAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountTaxItemSales().subtract(totals.getAmountTaxItemReturns());
        }
        return value;
    }

    /**
     * Returns the amount of item sales inclusive tax
     * 
     * @param totals The financial totals to extract the information from
     * @return the amount of item sales inclusive tax
     */
    protected CurrencyIfc getItemSalesInclusiveTaxAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountInclusiveTaxItemSales();
        }
        return value;
    }

    /**
     * Returns the number of taxable sales voids
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of taxable sales void
     */
    protected int getTaxableSalesVoidCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getCountGrossTaxableTransactionSalesVoided();
        }

        return value;
    }

    /**
     * Returns the number of taxable returns voids
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of taxable returns void
     */
    protected int getTaxableReturnsVoidCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getCountGrossTaxableTransactionReturnsVoided();
        }

        return value;
    }

    /**
     * Returns the number of non-taxable sales voids
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of non-taxable sales void
     */
    protected int getNonTaxableSalesVoidCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getCountGrossNonTaxableTransactionSalesVoided();
        }

        return value;
    }

    /**
     * Returns the number of non-taxable returns voids
     * 
     * @param totals The financial totals to extract the information from
     * @return the number of non-taxable returns void
     */
    protected int getNonTaxableReturnsVoidCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getCountGrossNonTaxableTransactionReturnsVoided();
        }

        return value;
    }

    /**
     * Returns the string value of gift certificate issued amount
     * 
     * @param totals FinancialTotalsIfc container of value to be converted
     * @return String the value as a string
     */
    protected CurrencyIfc getGiftCertificateIssuedAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossGiftCertificateIssued();
        }
        return value;
    }

    /**
     * Returns the string value of gift certificate issued count
     * 
     * @param totals FinancialTotalsIfc container of value to be converted
     * @return String the value as a string
     */
    protected BigDecimal getGiftCertificateIssuedCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsNetGiftCertificateIssued();
        }
        return value;
    }

    /**
     * Returns the string value of gift card issued amount
     * 
     * @param totals FinancialTotalsIfc container of value to be converted
     * @return String the value as a string
     */
    protected CurrencyIfc getGiftCardIssuedAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossGiftCardItemIssued();
        }
        return value;
    }

    /**
     * Returns the string value of gift card issued count
     * 
     * @param totals FinancialTotalsIfc container of value to be converted
     * @return String the value as a string
     */
    protected BigDecimal getGiftCardIssuedCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossGiftCardItemIssued();
        }
        return value;
    }

    /**
     * Returns the string value of gift card issued amount
     * 
     * @param totals FinancialTotalsIfc container of value to be converted
     * @return String the value as a string
     */
    protected CurrencyIfc getGiftCardReloadedAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossGiftCardItemReloaded();
        }
        return value;
    }

    /**
     * Returns the string value of gift card issued count
     * 
     * @param totals FinancialTotalsIfc container of value to be converted
     * @return String the value as a string
     */
    protected BigDecimal getGiftCardReloadedCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossGiftCardItemReloaded();
        }
        return value;
    }

    /**
     * Returns the string value of gift card issued amount
     * 
     * @param totals FinancialTotalsIfc container of value to be converted
     * @return String the value as a string
     */
    protected CurrencyIfc getGiftCardRedeemedAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossGiftCardItemRedeemed();
        }
        return value;
    }

    /**
     * Returns the string value of gift card issued count
     * 
     * @param totals FinancialTotalsIfc container of value to be converted
     * @return String the value as a string
     */
    protected BigDecimal getGiftCardRedeemedCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossGiftCardItemRedeemed();
        }
        return value;
    }

    /**
     * Returns the string value of gift card issue voided amount
     * 
     * @param totals FinancialTotalsIfc container of value to be converted
     * @return String the value as a string
     */
    protected CurrencyIfc getGiftCardIssueVoidedAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossGiftCardItemIssueVoided();
        }
        return value;
    }

    /**
     * Returns the string value of gift card issue void count
     * 
     * @param totals FinancialTotalsIfc container of value to be converted
     * @return String the value as a string
     */
    protected BigDecimal getGiftCardIssueVoidedCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossGiftCardItemIssueVoided();
        }
        return value;
    }

    /**
     * Returns the string value of gift card reload void amount
     * 
     * @param totals FinancialTotalsIfc container of value to be converted
     * @return String the value as a string
     */
    protected CurrencyIfc getGiftCardReloadVoidedAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossGiftCardItemReloadVoided();
        }
        return value;
    }

    /**
     * Returns the string value of gift card reload voided count
     * 
     * @param totals FinancialTotalsIfc container of value to be converted
     * @return String the value as a string
     */
    protected BigDecimal getGiftCardReloadVoidedCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossGiftCardItemReloadVoided();
        }
        return value;
    }

    /**
     * Returns the string value of gift card redeem voided amount
     * 
     * @param totals FinancialTotalsIfc container of value to be converted
     * @return String the value as a string
     */
    protected CurrencyIfc getGiftCardRedeemVoidedAmount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossGiftCardItemRedeemedVoided();
        }
        return value;
    }

    /**
     * Returns the string value of gift card redeem voided count
     * 
     * @param totals FinancialTotalsIfc container of value to be converted
     * @return String the value as a string
     */
    protected BigDecimal getGiftCardRedeemVoidedCount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossGiftCardItemRedeemedVoided();
        }
        return value;
    }

    /**
     * Get the number of house card enrollment requests that were approved
     * 
     * @param totals Totals object containing the data
     * @return number of approved requests, as a string
     */
    protected int getHouseCardEnrollmentsApprovalCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getHouseCardEnrollmentsApproved();
        }
        return value;
    }

    /**
     * Get the number of house card enrollment requests that were declined
     * 
     * @param totals Totals object containing the data
     * @return number of declined requests, as a string
     */
    protected int getHouseCardEnrollmentsDeclinedCount(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getHouseCardEnrollmentsDeclined();
        }
        return value;
    }

    /**
     * Get the AmountGrossGiftCardItemCredit as a String
     * 
     * @param totals Totals object containing the data
     * @return AmountGrossGiftCardItemCredit converted to a String
     */
    protected CurrencyIfc getAmountGrossGiftCardItemCredit(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossGiftCardItemCredit();
        }
        return value;
    }

    /**
     * Get the UnitsGrossGiftCardItemCredit as a String
     * 
     * @param totals Totals object containing the data
     * @return UnitsGrossGiftCardItemCredit converted to a String
     */
    protected BigDecimal getUnitsGrossGiftCardItemCredit(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossGiftCardItemCredit();
        }
        return value;
    }

    /**
     * Get the AmountGrossGiftCardItemCreditVoided as a String
     * 
     * @param totals Totals object containing the data
     * @return AmountGrossGiftCardItemCreditVoided converted to a String
     */
    protected CurrencyIfc getAmountGrossGiftCardItemCreditVoided(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossGiftCardItemCreditVoided();
        }
        return value;
    }

    /**
     * Get the UnitsGrossGiftCardItemCreditVoided as a String
     * 
     * @param totals Totals object containing the data
     * @return UnitsGrossGiftCardItemCreditVoided converted to a String
     */
    protected BigDecimal getUnitsGrossGiftCardItemCreditVoided(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossGiftCardItemCreditVoided();
        }
        return value;
    }

    /**
     * Get the AmountGrossGiftCertificatesRedeemed as a String
     * 
     * @param totals Totals object containing the data
     * @return AmountGrossGiftCertificatesRedeemed converted to a String
     */
    protected CurrencyIfc getAmountGrossGiftCertificatesRedeemed(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossGiftCertificatesRedeemed();
        }
        return value;
    }

    /**
     * Get the UnitsGrossGiftCertificatesRedeemed as a String
     * 
     * @param totals Totals object containing the data
     * @return UnitsGrossGiftCertificatesRedeemed converted to a String
     */
    protected BigDecimal getUnitsGrossGiftCertificatesRedeemed(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossGiftCertificatesRedeemed();
        }
        return value;
    }

    /**
     * Get the AmountGrossGiftCertificatesRedeemedVoided as a String
     * 
     * @param totals Totals object containing the data
     * @return AmountGrossGiftCertificatesRedeemedVoided converted to a String
     */
    protected CurrencyIfc getAmountGrossGiftCertificatesRedeemedVoided(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossGiftCertificatesRedeemedVoided();
        }
        return value;
    }

    /**
     * Get the UnitsGrossGiftCertificatesRedeemedVoided as a String
     * 
     * @param totals Totals object containing the data
     * @return UnitsGrossGiftCertificatesRedeemedVoided converted to a String
     */
    protected BigDecimal getUnitsGrossGiftCertificatesRedeemedVoided(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossGiftCertificatesRedeemedVoided();
        }
        return value;
    }

    /**
     * Get the AmountGrossStoreCreditsIssued as a String
     * 
     * @param totals Totals object containing the data
     * @return AmountGrossStoreCreditsIssued converted to a String
     */
    protected CurrencyIfc getAmountGrossStoreCreditsIssued(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossStoreCreditsIssued();
        }
        return value;
    }

    /**
     * Get the UnitsGrossStoreCreditsIssued as a String
     * 
     * @param totals Totals object containing the data
     * @return UnitsGrossStoreCreditsIssued converted to a String
     */
    protected BigDecimal getUnitsGrossStoreCreditsIssued(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossStoreCreditsIssued();
        }
        return value;
    }

    /**
     * Get the AmountGrossStoreCreditsIssuedVoided as a String
     * 
     * @param totals Totals object containing the data
     * @return AmountGrossStoreCreditsIssuedVoided converted to a String
     */
    protected CurrencyIfc getAmountGrossStoreCreditsIssuedVoided(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossStoreCreditsIssuedVoided();
        }
        return value;
    }

    /**
     * Get the UnitsGrossStoreCreditsIssuedVoided as a String
     * 
     * @param totals Totals object containing the data
     * @return UnitsGrossStoreCreditsIssuedVoided converted to a String
     */
    protected BigDecimal getUnitsGrossStoreCreditsIssuedVoided(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossStoreCreditsIssuedVoided();
        }
        return value;
    }

    /**
     * Get the AmountGrossStoreCreditsRedeemed as a String
     * 
     * @param totals Totals object containing the data
     * @return AmountGrossStoreCreditsRedeemed converted to a String
     */
    protected CurrencyIfc getAmountGrossStoreCreditsRedeemed(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossStoreCreditsRedeemed();
        }
        return value;
    }

    /**
     * Get the UnitsGrossStoreCreditsRedeemed as a String
     * 
     * @param totals Totals object containing the data
     * @return UnitsGrossStoreCreditsRedeemed converted to a String
     */
    protected BigDecimal getUnitsGrossStoreCreditsRedeemed(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossStoreCreditsRedeemed();
        }
        return value;
    }

    /**
     * Get the AmountGrossStoreCreditsRedeemedVoided as a String
     * 
     * @param totals Totals object containing the data
     * @return AmountGrossStoreCreditsRedeemedVoided converted to a String
     */
    protected CurrencyIfc getAmountGrossStoreCreditsRedeemedVoided(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossStoreCreditsRedeemedVoided();
        }
        return value;
    }

    /**
     * Get the UnitsGrossStoreCreditsRedeemedVoided as a String
     * 
     * @param totals Totals object containing the data
     * @return UnitsGrossStoreCreditsRedeemedVoided converted to a String
     */
    protected BigDecimal getUnitsGrossStoreCreditsRedeemedVoided(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossStoreCreditsRedeemedVoided();
        }
        return value;
    }

    /**
     * Get the AmountGrossItemEmployeeDiscount as a String
     * 
     * @param totals Totals object containing the data
     * @return AmountGrossItemEmployeeDiscount converted to a String
     */
    protected CurrencyIfc getAmountGrossItemEmployeeDiscount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossItemEmployeeDiscount();
        }
        return value;
    }

    /**
     * Get the UnitsGrossItemEmployeeDiscount as a String
     * 
     * @param totals Totals object containing the data
     * @return UnitsGrossItemEmployeeDiscount converted to a String
     */
    protected BigDecimal getUnitsGrossItemEmployeeDiscount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossItemEmployeeDiscount();
        }
        return value;
    }

    /**
     * Get the AmountGrossItemEmployeeDiscountVoided as a String
     * 
     * @param totals Totals object containing the data
     * @return AmountGrossItemEmployeeDiscountVoided converted to a String
     */
    protected CurrencyIfc getAmountGrossItemEmployeeDiscountVoided(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossItemEmployeeDiscountVoided();
        }
        return value;
    }

    /**
     * Get the UnitsGrossItemEmployeeDiscountVoided as a String
     * 
     * @param totals Totals object containing the data
     * @return UnitsGrossItemEmployeeDiscountVoided converted to a String
     */
    protected BigDecimal getUnitsGrossItemEmployeeDiscountVoided(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossItemEmployeeDiscountVoided();
        }
        return value;
    }

    /**
     * Get the AmountGrossTransactionEmployeeDiscount as a String
     * 
     * @param totals Totals object containing the data
     * @return AmountGrossTransactionEmployeeDiscount converted to a String
     */
    protected CurrencyIfc getAmountGrossTransactionEmployeeDiscount(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossTransactionEmployeeDiscount();
        }
        return value;
    }

    /**
     * Get the UnitsGrossTransactionEmployeeDiscount as a String
     * 
     * @param totals Totals object containing the data
     * @return UnitsGrossTransactionEmployeeDiscount converted to a String
     */
    protected BigDecimal getUnitsGrossTransactionEmployeeDiscount(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossTransactionEmployeeDiscount();
        }
        return value;
    }

    /**
     * Get the AmountGrossTransactionEmployeeDiscountVoided as a String
     * 
     * @param totals Totals object containing the data
     * @return AmountGrossTransactionEmployeeDiscountVoided converted to a
     *         String
     */
    protected CurrencyIfc getAmountGrossTransactionEmployeeDiscountVoided(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossTransactionEmployeeDiscountVoided();
        }
        return value;
    }

    /**
     * Get the UnitsGrossTransactionEmployeeDiscountVoided as a String
     * 
     * @param totals Totals object containing the data
     * @return UnitsGrossTransactionEmployeeDiscountVoided converted to a String
     */
    protected BigDecimal getUnitsGrossTransactionEmployeeDiscountVoided(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossTransactionEmployeeDiscountVoided();
        }
        return value;
    }

    /**
     * Get the AmountGrossGiftCertificateIssuedVoided as a String
     * 
     * @param totals Totals object containing the data
     * @return AmountGrossGiftCertificateIssuedVoided converted to a String
     */
    protected CurrencyIfc getAmountGrossGiftCertificateIssuedVoided(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossGiftCertificateIssuedVoided();
        }
        return value;
    }

    /**
     * Get the UnitsGrossGiftCertificateIssuedVoided as a String
     * 
     * @param totals Totals object containing the data
     * @return UnitsGrossGiftCertificateIssuedVoided converted to a String
     */
    protected BigDecimal getUnitsGrossGiftCertificateIssuedVoided(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossGiftCertificateIssuedVoided();
        }
        return value;
    }

    /**
     * Get the AmountGrossGiftCertificateTendered as a String
     * 
     * @param totals Totals object containing the data
     * @return AmountGrossGiftCertificateTendered converted to a String
     */
    protected CurrencyIfc getAmountGrossGiftCertificateTendered(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossGiftCertificateTendered();
        }
        return value;
    }

    /**
     * Get the UnitsGrossGiftCertificateTendered as a String
     * 
     * @param totals Totals object containing the data
     * @return UnitsGrossGiftCertificateTendered converted to a String
     */
    protected BigDecimal getUnitsGrossGiftCertificateTendered(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossGiftCertificateTendered();
        }
        return value;
    }

    /**
     * Get the AmountGrossGiftCertificateTenderedVoided as a String
     * 
     * @param totals Totals object containing the data
     * @return AmountGrossGiftCertificateTenderedVoided converted to a String
     */
    protected CurrencyIfc getAmountGrossGiftCertificateTenderedVoided(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossGiftCertificateTenderedVoided();
        }
        return value;
    }

    /**
     * Get the UnitsGrossGiftCertificateTenderedVoided as a String
     * 
     * @param totals Totals object containing the data
     * @return UnitsGrossGiftCertificateTenderedVoided converted to a String
     */
    protected BigDecimal getUnitsGrossGiftCertificateTenderedVoided(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossGiftCertificateTenderedVoided();
        }
        return value;
    }

    /**
     * Get the AmountEmployeeDiscounts as a String
     * 
     * @param totals Totals object containing the data
     * @return AmountEmployeeDiscounts converted to a String
     */
    protected CurrencyIfc getAmountEmployeeDiscounts(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountGrossTransactionEmployeeDiscount();
        }
        return value;
    }

    /**
     * Get the UnitsEmployeeDiscounts as a String
     * 
     * @param totals Totals object containing the data
     * @return UnitsEmployeeDiscounts converted to a String
     */
    protected BigDecimal getUnitsEmployeeDiscounts(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsGrossTransactionEmployeeDiscount();
        }
        return value;
    }

    /**
     * Get the AmountCustomerDiscounts as a String
     * 
     * @param totals Totals object containing the data
     * @return AmountCustomerDiscounts converted to a String
     */
    protected CurrencyIfc getAmountCustomerDiscounts(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountCustomerDiscounts();
        }
        return value;
    }

    /**
     * Get the UnitsCustomerDiscounts as a String
     * 
     * @param totals Totals object containing the data
     * @return UnitsCustomerDiscounts converted to a String
     */
    protected BigDecimal getUnitsCustomerDiscounts(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsCustomerDiscounts();
        }
        return value;
    }

    /**
     * Get the AmountPriceOverrides as a String
     * 
     * @param totals Totals object containing the data
     * @return AmountPriceOverrides converted to a String
     */
    protected CurrencyIfc getAmountPriceOverrides(FinancialTotalsIfc totals)
    {
        CurrencyIfc value = ZERO;
        if (totals != null)
        {
            value = totals.getAmountPriceOverrides();
        }
        return value;
    }

    /**
     * Get the UnitsPriceOverrides as a String
     * 
     * @param totals Totals object containing the data
     * @return UnitsPriceOverrides converted to a String
     */
    protected BigDecimal getUnitsPriceOverrides(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsPriceOverrides();
        }
        return value;
    }

    /**
     * Get the UnitsPriceAdjustments as a String
     * 
     * @param totals Totals object containing the data
     * @return UnitsPriceAdjustments converted to a String
     */
    protected BigDecimal getUnitsPriceAdjustments(FinancialTotalsIfc totals)
    {
        BigDecimal value = null;
        if (totals != null)
        {
            value = totals.getUnitsPriceAdjustments();
        }
        return value;
    }

    /**
     * Get the CountTransactionsWithReturnedItems as a String
     * 
     * @param totals Totals object containing the data
     * @return CountTransactionsWithReturnedItems converted to a String
     */
    protected int getCountTransactionsWithReturnedItems(FinancialTotalsIfc totals)
    {
        int value = 0;
        if (totals != null)
        {
            value = totals.getTransactionsWithReturnedItemsCount();
        }
        return value;
    }

    /**
     * Get the till reconciled amount from the entered FinancialCountTenderItem
     * 
     * @param till
     * @param tenderDescriptor
     * @return
     */
    protected CurrencyIfc getTillReconcileAmount(FinancialTotalsIfc totals, TenderDescriptorIfc tenderDesc)
    {
        FinancialCountIfc enteredCount = totals.getCombinedCount().getEntered();
        FinancialCountTenderItemIfc enteredTender = enteredCount.getSummaryTenderItemByDescriptor(tenderDesc);

        CurrencyIfc amount = ZERO;

        if (enteredTender != null)
        {
            amount = enteredTender.getAmountTotal();
        }

        return amount;
    }

    /**
     * Returns the tender loan count
     * 
     * @param loans The till loans
     * @param tenderDesc The TenderDescriptor
     * @return the tender loan count
     */
    protected int getTillReconcileCount(FinancialTotalsIfc totals, TenderDescriptorIfc tenderDesc)
    {
        int value = 0;

        FinancialCountIfc enteredCount = totals.getCombinedCount().getEntered();
        FinancialCountTenderItemIfc enteredTender = enteredCount.getSummaryTenderItemByDescriptor(tenderDesc);

        if (enteredTender != null)
        {
            value = enteredTender.getNumberItemsIn() - enteredTender.getNumberItemsOut();
        }

        return value;
    }
}
