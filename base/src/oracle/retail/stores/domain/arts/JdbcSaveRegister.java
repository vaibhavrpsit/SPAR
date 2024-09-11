/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveRegister.java /main/20 2014/07/09 13:10:48 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     06/26/14 - Forward port fix for handling the condition of two
 *                         registers opened with same till with one or both
 *                         offline at time of open.
 *    jswan     02/20/13 - Modified for Currency Rounding.
 *    cgreene   05/21/12 - XbranchMerge cgreene_bug-13951397 from
 *                         rgbustores_13.5x_generic
 *    cgreene   05/16/12 - arrange order of businessDay column to end of
 *                         primary key to improve performance since most
 *                         receipt lookups are done without the businessDay
 *    mjwallac  05/01/12 - Fortify: fix redundant null checks, part 3
 *    nkgautam  08/02/10 - bill payment changes
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    jswan     08/13/09 - Added the till reconcile amount and count to the
 *                         till, workstation and store tender history tables
 *                         and to code that reads from and writes to them.
 *    mahising  11/13/08 - Added for Customer module for both ORPOS and ORCO
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         5/16/2007 7:55:27 PM   Brett J. Larsen
 *         CR 26903 - 8.0.1 merge to trunk
 *
 *         BackOffice <ARG> Summary Report overhaul (many CRs fixed)
 *         
 *    7    360Commerce 1.6         5/14/2007 6:08:34 PM   Sandy Gu
 *         update inclusive information in financial totals and history tables
 *    6    360Commerce 1.5         4/25/2007 10:01:10 AM  Anda D. Cadar   I18N
 *         merge
 *    5    360Commerce 1.4         12/26/2006 3:18:21 PM  Charles D. Baker CR
 *         23955 - Updated the sequence number for store credit which was
 *         being
 *         lost when the application is stopped. This number is stored in the
 *         register
 *         object which isn't refreshed with each transaction. Corrected some
 *         other
 *         minor issues which had the potential to cause problems.
 *    4    360Commerce 1.3         1/25/2006 4:11:23 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:44 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:49 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:03 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:26:15    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:44     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:49     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:03     Robert Pearse
 *
 *   Revision 1.20  2004/07/30 21:01:30  dcobb
 *   @scr 6462 Financial Totals are not correct for the detail count during Till Open/Reconcile
 *   Replaced all instances of FinancialCountIfc.getTenderItem(int, String) with getSummaryTenderItemByDescriptor(TenderDescriptorIfc).
 *   Also replaced deprecated call with FinancialCountIfc.getAmountGrossTransactionEmployeeDiscount() and getUnitsGrossTransactionEmployeeDiscount().
 *
 *   Revision 1.19  2004/06/18 22:56:43  cdb
 *   @scr 4205 Corrected problems caused by searching financial counts
 *   by tender description rather than tender descriptor - which caused problems
 *   with foreign currencies.
 *
 *   Revision 1.18  2004/06/15 00:44:30  jdeleau
 *   @scr 2775 Support register reports and financial totals with the new
 *   tax engine.
 *
 *   Revision 1.17  2004/05/12 15:03:57  jdeleau
 *   @scr 4218 Remove GrossTransactionDiscount Amounts, Units, UnitsVoid,
 *   and AmountVoids in favor of the already existing AmountTransactionDiscounts
 *   and NumberTransactionDiscounts, which end up already being NET totals.
 *
 *   Revision 1.16  2004/05/11 23:03:01  jdeleau
 *   @scr 4218 Backout recent changes to remove TransactionDiscounts,
 *   going to go a different route and remove the newly added
 *   voids and grosses instead.
 *
 *   Revision 1.14  2004/04/28 19:41:40  jdeleau
 *   @scr 4218 Add StoreCreditsIssued (count/amount and voided counts and amounts)
 *   to Financial Totals.
 *
 *   Revision 1.13  2004/04/27 20:01:17  jdeleau
 *   @scr 4218 Add in the concrete calls for register reports data, refactor
 *   the houseCardEnrollment methods to be in line with other FinancialTotals
 *   methods.
 *
 *   Revision 1.12  2004/04/26 21:07:22  jdeleau
 *   @scr 4218 Put calls to new Financial Totals data in register reports,
 *   correct error in ArtsDatabaseIfc
 *
 *   Revision 1.11  2004/04/26 18:23:40  jdeleau
 *   @scr 4128 JDBC changes to support new data required for register reports
 *
 *   Revision 1.10  2004/04/07 20:56:49  lzhao
 *   @scr 4218: add gift card info for summary report.
 *
 *   Revision 1.9  2004/04/05 23:03:01  jdeleau
 *   @scr 4218 JavaDoc fixes associated with RegisterReports changes
 *
 *   Revision 1.8  2004/04/02 23:07:34  jdeleau
 *   @scr 4218 Register Reports - House Account and initial changes to
 *   the way SummaryReports are built.
 *
 *   Revision 1.7  2004/03/03 20:16:04  crain
 *   @scr 3814 Issue Gift Certificate
 *
 *   Revision 1.6  2004/02/25 23:25:24  crain
 *   @scr 3814 Issue Gift Certificate
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:45  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:18  mcs
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
 *    Rev 1.0   Aug 29 2003 15:32:54   CSchellenger
 * Initial revision.
 *
 *    Rev 1.7   Jul 15 2003 11:20:40   sfl
 * Save the void counts into database to support the register report.
 * Resolution for POS SCR-3164: Register Reports -  Register "Net Trans Tax Count " line is not correct.
 *
 *    Rev 1.6   May 23 2003 12:12:26   cdb
 * Updated to avoid saving system settings when register is saved. To save with settings use updateWorkstationAll instead of updateWorkstation.
 * Resolution for 1930: RE-FACTORING AND FEATURE ENHANCEMENTS TO PARAMETER SUBSYSTEM
 *
 *    Rev 1.5   May 18 2003 09:06:26   mpm
 * Merged 5.1 changes into 6.0
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.4   Feb 15 2003 17:25:58   mpm
 * Merged 5.1 changes.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.3   24 Jun 2002 11:48:34   jbp
 * merge from 5.1 SCR 1726
 * Resolution for POS SCR-1726: Void - Void of new special order gets stuck in the queue in DB2
 *
 *    Rev 1.2   11 Jun 2002 16:25:04   jbp
 * changes to report markdowns
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.1   Jun 10 2002 11:14:56   epd
 * Merged in changes for Oracle
 * Resolution for Domain SCR-83: Merging database fixes into base code
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdatableStatementIfc;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.ReconcilableCount;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;

/**
 * This operation performs inserts into the workstation history and workstation
 * history tender tables.
 * 
 * @version $Revision: /main/20 $
 */
public abstract class JdbcSaveRegister extends JdbcSaveReportingPeriod implements ARTSDatabaseIfc
{
    /**  */
    private static final long serialVersionUID = 6313500594962406526L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveRegister.class);

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/20 $";

    // Tender type map
    protected static TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();

    protected boolean isUpdateStatement = false;

    /**
     * Updates a record in the workstation table, including associated register
     * settings.
     * 
     * @param dataConnection connection to the db
     * @param register the register information
     * @exception DataException upon error
     */
    public void updateWorkstationAll(JdbcDataConnection dataConnection, RegisterIfc register) throws DataException
    {
        boolean updateSettings = true;
        updateWorkstation(dataConnection, register, updateSettings);
    }

    /**
     * Updates a record in the workstation table, but not associated register
     * settings.
     * 
     * @param dataConnection connection to the db
     * @param register the register information
     * @exception DataException upon error
     */
    public void updateWorkstation(JdbcDataConnection dataConnection, RegisterIfc register) throws DataException
    {
        boolean updateSettings = false;
        updateWorkstation(dataConnection, register, updateSettings);
    }

    /**
     * Updates a record in the workstation table.
     * 
     * @param dataConnection connection to the db
     * @param register the register information
     * @param updateSettings if true, include register settings
     * @exception DataException upon error
     */
    public void updateWorkstation(JdbcDataConnection dataConnection,
                                  RegisterIfc register,
                                  boolean updateSettings)
                                  throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();
        /*
         * Define the table
         */
        sql.setTable(TABLE_WORKSTATION);
        /*
         * Add columns and their values
         */
        sql.addColumn(FIELD_WORKSTATION_TERMINAL_STATUS_CODE, getStatusCode(register));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDay(register.getBusinessDate()));
        sql.addColumn(FIELD_WORKSTATION_SEQUENCE_NUMBER, getTransactionSequenceNumber(register));
        sql.addColumn(FIELD_UNIQUE_IDENTIFIER_EXTENSION, getUniqueIdentifierExtension(register));
        if(register.getOpenTime() != null)
        {
            sql.addColumn(FIELD_WORKSTATION_START_DATE_TIMESTAMP, getStartTimestamp(register));
        }
        sql.addColumn(FIELD_WORKSTATION_TRAINING_MODE_FLAG, getTrainingMode(register));
        sql.addColumn(FIELD_WORKSTATION_CURRENT_TILL_ID, "'" + register.getCurrentTillID() + "'");

        if (updateSettings)
        {
            addSettingsColumns(sql, register);
        }

        /*
         * Add Qualifier(s)
         */
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(register));
        if(register.isTillClose())
        {
            sql.addQualifier(FIELD_WORKSTATION_CURRENT_TILL_ID + " = " + makeSafeString(register.getClosingTillID()));
        }
        else
        {
            sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(register));
        }        

        dataConnection.execute(sql.getSQLString());

        if (0 >= dataConnection.getUpdateCount())
        {
            throw new DataException(DataException.NO_DATA, "Update Workstation");
        }

    }

    /**
     * Adds "settings" columns in the workstation table.
     * 
     * @param sql Update SQL being generated
     * @param register the register information
     */
    public void addSettingsColumns(SQLUpdateStatement sql, RegisterIfc register)
    {
        sql.addColumn(FIELD_WORKSTATION_ACCOUNTABILITY, "'" + register.getAccountability() + "'");
        sql.addColumn(FIELD_WORKSTATION_TILL_FLOAT_AMOUNT, register.getTillFloatAmount().toString());
        sql.addColumn(FIELD_WORKSTATION_COUNT_TILL_AT_RECONCILE, "'" + register.getTillCountTillAtReconcile() + "'");
        sql.addColumn(FIELD_WORKSTATION_COUNT_FLOAT_AT_OPEN, "'" + register.getTillCountFloatAtOpen() + "'");
        sql.addColumn(FIELD_WORKSTATION_COUNT_FLOAT_AT_RECONCILE, "'" + register.getTillCountFloatAtReconcile() + "'");
        sql.addColumn(FIELD_WORKSTATION_COUNT_CASH_LOAN, "'" + register.getTillCountCashLoan() + "'");
        sql.addColumn(FIELD_WORKSTATION_COUNT_CASH_PICKUP, "'" + register.getTillCountCashPickup() + "'");
        sql.addColumn(FIELD_WORKSTATION_COUNT_CHECK_PICKUP, "'" + register.getTillCountCheckPickup() + "'");
        sql.addColumn(FIELD_WORKSTATION_TILL_RECONCILE, getTillReconciledFlag(register));
    }

    /**
     * Adds a record in the workstation history table.
     * 
     * @param dataConnection connection to the db
     * @param register the register information
     * @exception DataException upon error
     */
    public void insertWorkstationHistory(JdbcDataConnection dataConnection,
                                         RegisterIfc register)
                                         throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();
        isUpdateStatement = false;
        /*
         * Define table
         */
        sql.setTable(TABLE_WORKSTATION_HISTORY);
        EYSDate rp = getReportingPeriod(register);
        /*
         * Add columns and their values
         */
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(register));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(register));
        sql.addColumn(FIELD_FISCAL_YEAR, getFiscalYear(register.getBusinessDate()));
        sql.addColumn(FIELD_REPORTING_PERIOD_TYPE_CODE, getReportingPeriodType(rp));
        sql.addColumn(FIELD_REPORTING_PERIOD_ID, getReportingPeriodID(rp));
        sql.addColumn(FIELD_WORKSTATION_START_DATE_TIMESTAMP, getStartTimestamp(register));
        sql.addColumn(FIELD_WORKSTATION_HISTORY_STATUS_CODE, getStatusCode(register));
        //+I18N
        sql.addColumn(FIELD_CURRENCY_ID, register.getTillFloatAmount().getType().getCurrencyId());
        //-I18N
        insertFinancialTotals(sql, register.getTotals());

        dataConnection.execute(sql.getSQLString());

        if (updateWorkstationTenders(dataConnection, register) == false)
        {
            insertWorkstationTenders(dataConnection, register);
        }
    }

    /**
     * Inserts records in the workstation tender history table.
     * 
     * @param dataConnection connection to the db
     * @param register the register information
     * @return true if successful, otherwise false
     * @exception DataException upon error
     */
    public boolean insertWorkstationTenders(JdbcDataConnection dataConnection,
                                         RegisterIfc register)
                                         throws DataException
    {
        boolean returnCode = true;
        /*
         * Do this once for each register
         */
        ReconcilableCountIfc loans = combineByTenderType(register.getTotals().getTillLoans());
        ReconcilableCountIfc pickups = combineByTenderType(register.getTotals().getTillPickups());
        ReconcilableCountIfc payIns = combineByTenderType(register.getTotals().getTillPayIns());
        ReconcilableCountIfc payOuts = combineByTenderType(register.getTotals().getTillPayOuts());
        /*
         * Walk through each tender item and save it
         */
        FinancialCountTenderItemIfc[] tenderTypes = getTenderTypes(register.getTotals().getCombinedCount());

        for (int i = 0; i < tenderTypes.length; ++i)
        {
            if (!insertWorkstationTenderHistory(dataConnection, tenderTypes[i], register, loans, pickups, payIns, payOuts))
            {
                returnCode = false;
            }
        }

        return(returnCode);
    }

    /**
     * Inserts a record in the workstation tender history table.
     * 
     * @param dataConnection connection to the db
     * @param tenderItem FinancialCountTenderItemIfc the type of tender
     * @param register the register information
     * @param loans the combined till loans for the register
     * @param pickups the combined till pickups for the register
     * @param payIns the combined till pay-ins for the register
     * @param payOuts the combined till pay-outs for the register
     * @return true if successful, otherwise false
     * @exception DataException upon error
     */
    public boolean insertWorkstationTenderHistory(JdbcDataConnection dataConnection,
                                               FinancialCountTenderItemIfc tenderItem,
                                               RegisterIfc register,
                                               ReconcilableCountIfc loans,
                                               ReconcilableCountIfc pickups,
                                               ReconcilableCountIfc payIns,
                                               ReconcilableCountIfc payOuts)
                                               throws DataException
    {
        boolean returnCode = false;
        String tenderDesc = tenderItem.getDescription();
        String tenderType = tenderTypeMap.getCode(tenderItem.getTenderType());
        TenderDescriptorIfc tenderDescriptor = tenderItem.getTenderDescriptor();

        SQLInsertStatement sql = new SQLInsertStatement();
        /*
         * Define table
         */
        sql.setTable(TABLE_WORKSTATION_TENDER_HISTORY);
        /*
         * Add columns and their values
         */
        EYSDate rp = getReportingPeriod(register);
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(register));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(register));
        sql.addColumn(FIELD_FISCAL_YEAR, getFiscalYear(register.getBusinessDate()));
        sql.addColumn(FIELD_REPORTING_PERIOD_TYPE_CODE, getReportingPeriodType(rp));
        sql.addColumn(FIELD_REPORTING_PERIOD_ID, getReportingPeriodID(rp));
        sql.addColumn(FIELD_TENDER_TYPE_CODE, inQuotes(tenderType));
        sql.addColumn(FIELD_TENDER_SUBTYPE, inQuotes(emptyStringToSpaceString(tenderItem.getTenderSubType())));
        sql.addColumn(FIELD_CURRENCY_ISSUING_COUNTRY_CODE, inQuotes(tenderItem.getCurrencyCode()));
        //+I18N
        sql.addColumn(FIELD_CURRENCY_ID, tenderItem.getCurrencyID());
        //-I18N
        sql.addColumn(FIELD_WORKSTATION_TENDER_DEPOSIT_TOTAL_AMOUNT,
                      safeSQLCast(getTenderDepositAmount(register, tenderDesc)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_LOAN_MEDIA_TOTAL_AMOUNT,
                      safeSQLCast(getTenderLoanAmount(loans, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_OVER_TOTAL_AMOUNT,
                      safeSQLCast(getTenderOverAmount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_PICKUP_MEDIA_TOTAL_AMOUNT,
                      safeSQLCast(getTenderPickupAmount(pickups, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_SHORT_TOTAL_AMOUNT,
                      safeSQLCast(getTenderShortAmount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_BEGINNING_TENDER_MEDIA_UNIT_COUNT,
                      safeSQLCast(getTenderBeginningCount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_DEPOSIT_MEDIA_UNIT_COUNT,
                      safeSQLCast(getTenderDepositCount(register, tenderDesc)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_LOAN_MEDIA_UNIT_COUNT,
                      safeSQLCast(getTenderLoanCount(loans, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_MEDIA_COUNT,
                      safeSQLCast(getTenderCount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_MEDIA_OVER_COUNT,
                      safeSQLCast(getTenderOverCount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_MEDIA_SHORT_COUNT,
                      getTenderShortCount(register, tenderDescriptor));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_PICKUP_MEDIA_UNIT_COUNT,
                      safeSQLCast(getTenderPickupCount(pickups, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_REFUND_MEDIA_UNIT_COUNT,
                      safeSQLCast(getTenderRefundCount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_REFUND_TOTAL_AMOUNT,
                      safeSQLCast(getTenderRefundAmount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_TOTAL_AMOUNT,
                      safeSQLCast(getTenderTotalAmount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_OPEN_AMOUNT,
                      safeSQLCast(getTenderOpenAmount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_CLOSE_AMOUNT,
                      safeSQLCast(getTenderCloseAmount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_MEDIA_CLOSE_COUNT,
                      safeSQLCast(getTenderCloseCount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_IN_MEDIA_TOTAL_AMOUNT,
                      safeSQLCast(getTillPayInAmount(payIns, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_OUT_MEDIA_TOTAL_AMOUNT,
                      safeSQLCast(getTillPayOutAmount(payOuts, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_IN_MEDIA_UNIT_COUNT,
                      safeSQLCast(getTillPayInCount(payIns, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_OUT_MEDIA_UNIT_COUNT,
                      safeSQLCast(getTillPayOutCount(payOuts, tenderDescriptor)));
        sql.addColumn(FIELD_RECONCILE_AMOUNT, 
                      safeSQLCast(getTillReconcileAmount(register, tenderDescriptor)));
        sql.addColumn(FIELD_RECONCILE_MEDIA_UNIT_COUNT, 
                      safeSQLCast(getTillReconcileCount(register, tenderDescriptor)));
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

        dataConnection.execute(sql.getSQLString());
        
        if (0 < dataConnection.getUpdateCount())
        {
            returnCode = true;
        }

        return(returnCode);
    }

    /**
     * Updates a record in the workstation history table.
     * 
     * @param dataConnection connection to the db
     * @param register the register information
     * @exception DataException upon error
     */
    public void updateWorkstationHistory(JdbcDataConnection dataConnection,
                                         RegisterIfc register)
                                         throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();
        isUpdateStatement = true;
        /*
         * Define table
         */
        sql.setTable(TABLE_WORKSTATION_HISTORY);
        EYSDate rp = getReportingPeriod(register);
        /*
         * Add columns and their values
         */
        sql.addColumn(FIELD_WORKSTATION_HISTORY_STATUS_CODE, getStatusCode(register));
        insertFinancialTotals(sql, register.getTotals());

        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(register));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(register));
        sql.addQualifier(FIELD_FISCAL_YEAR + " = " + getFiscalYear(register.getBusinessDate()));
        sql.addQualifier(FIELD_REPORTING_PERIOD_TYPE_CODE + " = " + getReportingPeriodType(rp));
        sql.addQualifier(FIELD_REPORTING_PERIOD_ID + " = " + getReportingPeriodID(rp));

        dataConnection.execute(sql.getSQLString());

        if (0 >= dataConnection.getUpdateCount())
        {
            throw new DataException(DataException.NO_DATA, "Update Workstation History");
        }

        if (updateWorkstationTenders(dataConnection, register) == false)
        {
            insertWorkstationTenders(dataConnection, register);
        }
    }

    /**
     * Updates all of the tender entries in the register financial totals.
     * 
     * @param dataConnection connection to the db
     * @param register the register information
     * @return true if successful, otherwise false
     * @exception DataException upon error
     */
    public boolean updateWorkstationTenders(JdbcDataConnection dataConnection,
                                         RegisterIfc register)
                                         throws DataException
    {
        boolean returnCode = true;
        /*
         * Do this once for each register
         */
        ReconcilableCountIfc loans = combineByTenderType(register.getTotals().getTillLoans());
        ReconcilableCountIfc pickups = combineByTenderType(register.getTotals().getTillPickups());
        ReconcilableCountIfc payIns = combineByTenderType(register.getTotals().getTillPayIns());
        ReconcilableCountIfc payOuts = combineByTenderType(register.getTotals().getTillPayOuts());
         /*
         * Walk through each tender item and save it
         */
        FinancialCountTenderItemIfc[] tenderTypes = getTenderTypes(register.getTotals().getCombinedCount());

        for (int i = 0; i < tenderTypes.length; ++i)
        {
            if (!updateWorkstationTenderHistory(dataConnection, tenderTypes[i], register, loans, pickups, payIns, payOuts))
            {
                if (!insertWorkstationTenderHistory(dataConnection, tenderTypes[i], register, loans, pickups, payIns, payOuts))
                {
                    returnCode = false;
                }
            }
        }

        return(returnCode);
    }

    /**
     * Updates a record in the workstation tender history table.
     * 
     * @param dataConnection connection to the db
     * @param tenderItem the type of tender
     * @param register the register information
     * @param loans the combined till loans for the register
     * @param pickups the combined till pickups for the register
     * @param payIns the combined till pay-ins for the register
     * @param payOuts the combined till pay-outs for the register
     * @return true if successful, otherwise false
     * @exception DataException upon error
     */
    public boolean updateWorkstationTenderHistory(JdbcDataConnection dataConnection,
                                               FinancialCountTenderItemIfc tenderItem,
                                               RegisterIfc register,
                                               ReconcilableCountIfc loans,
                                               ReconcilableCountIfc pickups,
                                               ReconcilableCountIfc payIns,
                                               ReconcilableCountIfc payOuts)
                                               throws DataException
    {
        boolean returnCode= false;
        String tenderType = tenderTypeMap.getCode(tenderItem.getTenderType());
        String tenderDesc = tenderItem.getDescription();
        TenderDescriptorIfc tenderDescriptor = tenderItem.getTenderDescriptor();

        SQLUpdateStatement sql = new SQLUpdateStatement();
        /*
         * Define table
         */
        sql.setTable(TABLE_WORKSTATION_TENDER_HISTORY);
        /*
         * Add columns and their values
         */
        sql.addColumn(FIELD_WORKSTATION_TENDER_DEPOSIT_TOTAL_AMOUNT,
                      safeSQLCast(getTenderDepositAmount(register, tenderDesc)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_LOAN_MEDIA_TOTAL_AMOUNT,
                      safeSQLCast(getTenderLoanAmount(loans, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_OVER_TOTAL_AMOUNT,
                      safeSQLCast(getTenderOverAmount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_PICKUP_MEDIA_TOTAL_AMOUNT,
                      safeSQLCast(getTenderPickupAmount(pickups, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_SHORT_TOTAL_AMOUNT,
                      safeSQLCast(getTenderShortAmount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_BEGINNING_TENDER_MEDIA_UNIT_COUNT,
                      safeSQLCast(getTenderBeginningCount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_DEPOSIT_MEDIA_UNIT_COUNT,
                      safeSQLCast(getTenderDepositCount(register, tenderDesc)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_LOAN_MEDIA_UNIT_COUNT,
                      safeSQLCast(getTenderLoanCount(loans, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_MEDIA_COUNT,
                      safeSQLCast(getTenderCount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_MEDIA_OVER_COUNT,
                      safeSQLCast(getTenderOverCount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_MEDIA_SHORT_COUNT,
                      safeSQLCast(getTenderShortCount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_PICKUP_MEDIA_UNIT_COUNT,
                      safeSQLCast(getTenderPickupCount(pickups, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_REFUND_MEDIA_UNIT_COUNT,
                      safeSQLCast(getTenderRefundCount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_REFUND_TOTAL_AMOUNT,
                      safeSQLCast(getTenderRefundAmount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_TOTAL_AMOUNT,
                      safeSQLCast(getTenderTotalAmount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_OPEN_AMOUNT,
                      safeSQLCast(getTenderOpenAmount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_CLOSE_AMOUNT,
                      safeSQLCast(getTenderCloseAmount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_MEDIA_CLOSE_COUNT,
                      safeSQLCast(getTenderCloseCount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_IN_MEDIA_TOTAL_AMOUNT,
                      safeSQLCast(getTillPayInAmount(payIns, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_OUT_MEDIA_TOTAL_AMOUNT,
                      safeSQLCast(getTillPayOutAmount(payOuts, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_IN_MEDIA_UNIT_COUNT,
                      safeSQLCast(getTillPayInCount(payIns, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_OUT_MEDIA_UNIT_COUNT,
                      safeSQLCast(getTillPayOutCount(payOuts, tenderDescriptor)));
        sql.addColumn(FIELD_RECONCILE_AMOUNT, 
                      safeSQLCast(getTillReconcileAmount(register, tenderDescriptor)));
        sql.addColumn(FIELD_RECONCILE_MEDIA_UNIT_COUNT, 
                      safeSQLCast(getTillReconcileCount(register, tenderDescriptor)));
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

        /*
         * Add Qualifiers
         */
        EYSDate rp = getReportingPeriod(register);
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(register));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(register));
        sql.addQualifier(FIELD_FISCAL_YEAR + " = " + getFiscalYear(register.getBusinessDate()));
        sql.addQualifier(FIELD_REPORTING_PERIOD_TYPE_CODE + " = " + getReportingPeriodType(rp));
        sql.addQualifier(FIELD_REPORTING_PERIOD_ID + " = " + getReportingPeriodID(rp));
        sql.addQualifier(FIELD_TENDER_TYPE_CODE + " = " + inQuotes(tenderType));
        sql.addQualifier(FIELD_TENDER_SUBTYPE + " = " + inQuotes(emptyStringToSpaceString(tenderItem.getTenderSubType())));
        sql.addQualifier(FIELD_CURRENCY_ISSUING_COUNTRY_CODE + " = " + inQuotes(tenderItem.getCurrencyCode()));

        dataConnection.execute(sql.getSQLString());

        if (0 < dataConnection.getUpdateCount())
        {
            returnCode = true;
        }

        return(returnCode);
    }

    /**
     * Adds to a record in the workstation history table.
     * 
     * @param dataConnection connection to the db
     * @param register the register information
     * @exception DataException upon error
     */
    public void addWorkstationHistory(JdbcDataConnection dataConnection,
                                      RegisterIfc register)
                                      throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();
        isUpdateStatement = true;
        /*
         * Define table
         */
        sql.setTable(TABLE_WORKSTATION_HISTORY);
        EYSDate rp = getReportingPeriod(register);
        /*
         * Add columns and their values
         */
        sql.addColumn(FIELD_WORKSTATION_HISTORY_STATUS_CODE, getStatusCode(register));
        addFinancialTotals(sql, register.getTotals());

        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(register));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(register));
        sql.addQualifier(FIELD_FISCAL_YEAR + " = " + getFiscalYear(register.getBusinessDate()));
        sql.addQualifier(FIELD_REPORTING_PERIOD_TYPE_CODE + " = " + getReportingPeriodType(rp));
        sql.addQualifier(FIELD_REPORTING_PERIOD_ID + " = " + getReportingPeriodID(rp));

        dataConnection.execute(sql.getSQLString());

        if (0 >= dataConnection.getUpdateCount())
        {
            throw new DataException(DataException.NO_DATA, "Add Workstation History");
        }

        addWorkstationTenders(dataConnection, register);
        updateUniqueIdentifier(dataConnection, register);
    }

    /**
     * Updates the unique identifier for the workstation - aka the store credit
     * sequence number.
     * 
     * @param dataConnection connection to database
     * @param register register object
     * @exception DataException thrown if error occurs
     */
    protected void updateUniqueIdentifier(JdbcDataConnection dataConnection, RegisterIfc register)
        throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();
        sql.setTable(TABLE_WORKSTATION);
        sql.addColumn(FIELD_UNIQUE_IDENTIFIER_EXTENSION, getUniqueIdentifierExtension(register));
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(register));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(register));
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error("" + de + "");
            throw de;
        }
        catch (Exception e)
        {
            logger.error("" + e + "");
            throw new DataException(DataException.UNKNOWN, "update transaction sequence number", e);
        }
    }

    /**
     * Adds/updates all of the tender entries in the register financial totals.
     * 
     * @param dataConnection connection to the db
     * @param register the register information
     * @return true if successful, otherwise false
     * @exception DataException upon error
     */
    public boolean addWorkstationTenders(JdbcDataConnection dataConnection,
                                      RegisterIfc register)
                                      throws DataException
    {
        boolean returnCode = true;
        /*
         * Do this once for each register
         */
        ReconcilableCountIfc loans = combineByTenderType(register.getTotals().getTillLoans());
        ReconcilableCountIfc pickups = combineByTenderType(register.getTotals().getTillPickups());
        ReconcilableCountIfc payIns = combineByTenderType(register.getTotals().getTillPayIns());
        ReconcilableCountIfc payOuts = combineByTenderType(register.getTotals().getTillPayOuts());
        /*
         * Walk through the tender items
         */
        FinancialCountTenderItemIfc[] tenderTypes = getTenderTypes(register.getTotals().getCombinedCount());

        for (int i = 0; i < tenderTypes.length; ++i)
        {
            if (!addWorkstationTenderHistory(dataConnection, tenderTypes[i], register, loans, pickups, payIns, payOuts))
            {
                if (!insertWorkstationTenderHistory(dataConnection, tenderTypes[i], register, loans, pickups, payIns, payOuts))
                {
                    returnCode = false;
                }
            }
        }

        return(returnCode);
    }

    /**
        Adds to a record in the workstation tender history table.
        <P>
        @param  dataConnection  connection to the db
        @param  tenderItem      the type of tender
        @param  register        the register information
        @param  loans           the combined till loans for the register
        @param  pickups         the combined till pickups for the register
        @param  payIns the combined till pay-ins for the register
        @param payOuts the combined till pay-outs for the register
        @return true if successful, otherwise false
        @exception DataException upon error
     */
    public boolean addWorkstationTenderHistory(JdbcDataConnection dataConnection,
                                            FinancialCountTenderItemIfc tenderItem,
                                            RegisterIfc register,
                                            ReconcilableCountIfc loans,
                                            ReconcilableCountIfc pickups,
                                            ReconcilableCountIfc payIns,
                                            ReconcilableCountIfc payOuts)
                                            throws DataException
    {
        boolean returnCode = false;
        String tenderDesc = tenderItem.getDescription();
        String tenderType = tenderTypeMap.getCode(tenderItem.getTenderType());
        TenderDescriptorIfc tenderDescriptor = tenderItem.getTenderDescriptor();

        SQLUpdateStatement sql = new SQLUpdateStatement();
        /*
         * Define table
         */
        sql.setTable(TABLE_WORKSTATION_TENDER_HISTORY);
        /*
         * Add columns and their values
         */
        sql.addColumn(FIELD_WORKSTATION_TENDER_DEPOSIT_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_TENDER_DEPOSIT_TOTAL_AMOUNT + "+" + safeSQLCast(getTenderDepositAmount(register, tenderDesc)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_LOAN_MEDIA_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_TENDER_LOAN_MEDIA_TOTAL_AMOUNT + "+" + safeSQLCast(getTenderLoanAmount(loans, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_OVER_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_TENDER_OVER_TOTAL_AMOUNT + "+" + safeSQLCast(getTenderOverAmount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_PICKUP_MEDIA_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_TENDER_PICKUP_MEDIA_TOTAL_AMOUNT + "+" + safeSQLCast(getTenderPickupAmount(pickups, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_SHORT_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_TENDER_SHORT_TOTAL_AMOUNT + "+" + safeSQLCast(getTenderShortAmount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_BEGINNING_TENDER_MEDIA_UNIT_COUNT,
                      FIELD_WORKSTATION_TOTAL_BEGINNING_TENDER_MEDIA_UNIT_COUNT + "+" + safeSQLCast(getTenderBeginningCount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_DEPOSIT_MEDIA_UNIT_COUNT,
                      FIELD_WORKSTATION_TOTAL_TENDER_DEPOSIT_MEDIA_UNIT_COUNT + "+" + safeSQLCast(getTenderDepositCount(register, tenderDesc)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_LOAN_MEDIA_UNIT_COUNT,
                      FIELD_WORKSTATION_TOTAL_TENDER_LOAN_MEDIA_UNIT_COUNT + "+" + safeSQLCast(getTenderLoanCount(loans, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_MEDIA_COUNT,
                      FIELD_WORKSTATION_TOTAL_TENDER_MEDIA_COUNT + "+" + safeSQLCast(getTenderCount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_MEDIA_OVER_COUNT,
                      FIELD_WORKSTATION_TOTAL_TENDER_MEDIA_OVER_COUNT + "+" + safeSQLCast(getTenderOverCount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_MEDIA_SHORT_COUNT,
                      FIELD_WORKSTATION_TOTAL_TENDER_MEDIA_SHORT_COUNT + "+" + safeSQLCast(getTenderShortCount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_PICKUP_MEDIA_UNIT_COUNT,
                      FIELD_WORKSTATION_TOTAL_TENDER_PICKUP_MEDIA_UNIT_COUNT + "+" + safeSQLCast(getTenderPickupCount(pickups, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_REFUND_MEDIA_UNIT_COUNT,
                      FIELD_WORKSTATION_TOTAL_TENDER_REFUND_MEDIA_UNIT_COUNT + "+" + safeSQLCast(getTenderRefundCount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_REFUND_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_TENDER_REFUND_TOTAL_AMOUNT + "+" + safeSQLCast(getTenderRefundAmount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_TENDER_TOTAL_AMOUNT + "+" + safeSQLCast(getTenderTotalAmount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_OPEN_AMOUNT,
                      FIELD_WORKSTATION_TENDER_OPEN_AMOUNT + "+" + safeSQLCast(getTenderOpenAmount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_CLOSE_AMOUNT,
                      FIELD_WORKSTATION_TENDER_CLOSE_AMOUNT + "+" + safeSQLCast(getTenderCloseAmount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_MEDIA_CLOSE_COUNT,
                      FIELD_WORKSTATION_TENDER_MEDIA_CLOSE_COUNT + "+" + safeSQLCast(getTenderCloseCount(register, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_IN_MEDIA_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_FUNDS_RECEIVED_IN_MEDIA_TOTAL_AMOUNT + " + " +
                        safeSQLCast(getTillPayInAmount(payIns, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_OUT_MEDIA_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_FUNDS_RECEIVED_OUT_MEDIA_TOTAL_AMOUNT + " + " +
                        safeSQLCast(getTillPayOutAmount(payOuts, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_IN_MEDIA_UNIT_COUNT,
                      FIELD_WORKSTATION_FUNDS_RECEIVED_IN_MEDIA_UNIT_COUNT + " + " +
                        safeSQLCast(getTillPayInCount(payIns, tenderDescriptor)));
        sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_OUT_MEDIA_UNIT_COUNT,
                      FIELD_WORKSTATION_FUNDS_RECEIVED_OUT_MEDIA_UNIT_COUNT + " + " +
                        safeSQLCast(getTillPayOutCount(payOuts, tenderDescriptor)));
        sql.addColumn(FIELD_RECONCILE_AMOUNT,
                      FIELD_RECONCILE_AMOUNT + " + " +
                        safeSQLCast(getTillReconcileAmount(register, tenderDescriptor)));
        sql.addColumn(FIELD_RECONCILE_MEDIA_UNIT_COUNT, 
                      FIELD_RECONCILE_MEDIA_UNIT_COUNT + " + " +
                        safeSQLCast(getTillReconcileCount(register, tenderDescriptor)));
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
        /*
         * Add Qualifiers
         */
        EYSDate rp = getReportingPeriod(register);
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(register));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(register));
        sql.addQualifier(FIELD_FISCAL_YEAR + " = " + getFiscalYear(register.getBusinessDate()));
        sql.addQualifier(FIELD_REPORTING_PERIOD_TYPE_CODE + " = " + getReportingPeriodType(rp));
        sql.addQualifier(FIELD_REPORTING_PERIOD_ID + " = " + getReportingPeriodID(rp));
        sql.addQualifier(FIELD_TENDER_TYPE_CODE + " = " + inQuotes(tenderType));
        sql.addQualifier(FIELD_TENDER_SUBTYPE + " = " + inQuotes(emptyStringToSpaceString(tenderItem.getTenderSubType())));
        sql.addQualifier(FIELD_CURRENCY_ISSUING_COUNTRY_CODE + " = " + inQuotes(tenderItem.getCurrencyCode()));

        dataConnection.execute(sql.getSQLString());

        if (0 < dataConnection.getUpdateCount())
        {
            returnCode=true;
        }
        return returnCode;
    }

    /**
        Adds the columns and values for the financial totals fields
        <p>
        @param  sql     The SQL statement to add the column-value pairs to
        @param  totals  The financial totals to draw the values from
     */
    protected void insertFinancialTotals(SQLUpdatableStatementIfc sql, FinancialTotalsIfc totals)
    {
        // Totals
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TRANSACTION_COUNT, safeSQLCast(getTransactionCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_NONTAXABLE_TOTAL_AMOUNT, safeSQLCast(getNetNontaxableAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONTAXABLE_COUNT, safeSQLCast(getNontaxableCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TAXABLE_COUNT, safeSQLCast(getTaxableCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TAX_EXEMPT_TOTAL_AMOUNT, safeSQLCast(getNetTaxExemptAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TAX_EXEMPT_TRANSACTION_COUNT, safeSQLCast(getTaxExemptCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_REFUND_TOTAL_AMOUNT, safeSQLCast(getRefundAmount(totals)));
        // Transaction Sales/Refunds
        sql.addColumn(FIELD_WORKSTATION_GROSS_SALES_EX_TAX_TOTAL_AMOUNT, safeSQLCast(getSalesAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_REFUND_COUNT, safeSQLCast(getRefundCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_REFUND_NONTAXABLE_TOTAL_AMOUNT, safeSQLCast(getRefundNontaxableAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONTAXABLE_REFUND_COUNT, safeSQLCast(getRefundNontaxableCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_REFUND_TAX_EXEMPT_TOTAL_AMOUNT, safeSQLCast(getRefundTaxExemptAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TAX_EXEMPT_REFUND_COUNT, safeSQLCast(getRefundTaxExemptCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_TRANSACTION_SALES_AMOUNT, safeSQLCast(getTaxableTransactionSalesAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_TRANSACTION_SALES_COUNT, safeSQLCast(getTaxableTransactionSalesCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_NON_TAXABLE_TRANSACTION_SALES_AMOUNT, safeSQLCast(getNonTaxableTransactionSalesAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_NON_TAXABLE_TRANSACTION_SALES_COUNT, safeSQLCast(getNonTaxableTransactionSalesCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAX_EXEMPT_TRANSACTION_SALES_AMOUNT, safeSQLCast(getTaxExemptTransactionSalesAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAX_EXEMPT_TRANSACTION_SALES_COUNT, safeSQLCast(getTaxExemptTransactionSalesCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_TRANSACTION_RETURNS_AMOUNT, safeSQLCast(getTaxableTransactionReturnsAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_TRANSACTION_RETURNS_COUNT, safeSQLCast(getTaxableTransactionReturnsCount(totals)));

        // Item Sales/Returns
        sql.addColumn(FIELD_WORKSTATION_LINE_ITEM_SALES_TOTAL_AMOUNT, safeSQLCast(getItemSalesAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_SALE_LINE_ITEM_COUNT, safeSQLCast(getItemSalesCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_LINE_ITEM_NONTAXABLE_TOTAL_AMOUNT, safeSQLCast(getItemNontaxableAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONTAXABLE_LINE_ITEM_COUNT, safeSQLCast(getItemNontaxableCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_LINE_ITEM_TAX_EXEMPT_TOTAL_AMOUNT, safeSQLCast(getItemTaxExemptAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TAX_EXEMPT_LINE_ITEM_COUNT, safeSQLCast(getItemTaxExemptCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_RETURN_TOTAL_AMOUNT, safeSQLCast(getReturnAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_RETURN_COUNT, safeSQLCast(getReturnCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_RETURN_NONTAXABLE_TOTAL_AMOUNT, safeSQLCast(getReturnNontaxableAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONTAXABLE_RETURN_COUNT, safeSQLCast(getReturnNontaxableCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_RETURN_TAX_EXEMPT_TOTAL_AMOUNT, safeSQLCast(getReturnTaxExemptAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TAX_EXEMPT_RETURN_COUNT, safeSQLCast(getReturnTaxExemptCount(totals)));

        sql.addColumn(FIELD_WORKSTATION_NONMERCH_NONTAX_TOTAL_AMOUNT, safeSQLCast(getNonMerchNonTaxAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONMERCH_NONTAX_COUNT, safeSQLCast(getNonMerchNonTaxCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_RETURN_NONMERCH_NONTAX_TOTAL_AMOUNT, safeSQLCast(getNonMerchNonTaxReturnAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONMERCH_NONTAX_RETURN_COUNT, safeSQLCast(getNonMerchNonTaxReturnCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_NONMERCH_TAX_TOTAL_AMOUNT, safeSQLCast(getNonMerchAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONMERCH_TAX_COUNT, safeSQLCast(getNonMerchCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_RETURN_NONMERCH_TAX_TOTAL_AMOUNT, safeSQLCast(getNonMerchReturnAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONMERCH_TAX_RETURN_COUNT, safeSQLCast(getNonMerchReturnCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_AMOUNT, safeSQLCast(getGiftCardAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_COUNT, safeSQLCast(getGiftCardCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_RETURN_GIFT_CARD_AMOUNT, safeSQLCast(getGiftCardReturnAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_RETURN_COUNT, safeSQLCast(getGiftCardReturnCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_HOUSE_PAYMENT_AMOUNT, safeSQLCast(getHousePaymentAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_HOUSE_PAYMENT_COUNT, safeSQLCast(getHousePaymentCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_RESTOCKING_FEE_AMOUNT, safeSQLCast(getRestockingFeeAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_RESTOCKING_FEE_COUNT, safeSQLCast(getRestockingFeeCount(totals)));
        sql.addColumn(FIELD_STORE_RESTOCKING_FEE_AMOUNT_FROM_NON_TAXABLE, safeSQLCast(getRestockingFeeFromNonTaxableAmount(totals)));
        sql.addColumn(FIELD_STORE_RESTOCKING_FEE_COUNT_FROM_NON_TAXABLE, safeSQLCast(getRestockingFeeFromNonTaxableCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_ITEM_SALES_AMOUNT, safeSQLCast(getTaxableItemSalesAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_ITEM_SALES_COUNT, safeSQLCast(getTaxableItemSalesCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_ITEM_RETURNS_AMOUNT, safeSQLCast(getTaxableItemReturnsAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_ITEM_RETURNS_COUNT, safeSQLCast(getTaxableItemReturnsCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_NON_MERCHANDISE_SALES_AMOUNT, safeSQLCast(getTaxableNonMerchandiseSalesAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_NON_MERCHANDISE_SALES_COUNT, safeSQLCast(getTaxableNonMerchandiseSalesCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES_AMOUNT, safeSQLCast(getNonTaxableNonMerchandiseSalesAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES_COUNT, safeSQLCast(getNonTaxableNonMerchandiseSalesCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_SALES_AMOUNT, safeSQLCast(getGiftCardItemSalesAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_SALES_COUNT, safeSQLCast(getGiftCardItemSalesCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CERTIFICATE_ISSUED_AMOUNT, safeSQLCast(getGiftCertificateIssuedAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CERTIFICATE_ISSUED_COUNT, safeSQLCast(getGiftCertificateIssuedCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_ISSUED_AMOUNT, safeSQLCast(getGiftCardIssuedAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_ISSUED_COUNT, safeSQLCast(getGiftCardIssuedCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_RELOADED_AMOUNT, safeSQLCast(getGiftCardReloadedAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_RELOADED_COUNT, safeSQLCast(getGiftCardReloadedCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_REDEEMED_AMOUNT, safeSQLCast(getGiftCardRedeemedAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_REDEEMED_COUNT, safeSQLCast(getGiftCardRedeemedCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_ISSUE_VOIDED_AMOUNT, safeSQLCast(getGiftCardIssueVoidedAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_ISSUE_VOIDED_COUNT, safeSQLCast(getGiftCardIssueVoidedCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_RELOAD_VOIDED_AMOUNT, safeSQLCast(getGiftCardReloadVoidedAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_RELOAD_VOIDED_COUNT, safeSQLCast(getGiftCardReloadVoidedCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_REDEEM_VOIDED_AMOUNT, safeSQLCast(getGiftCardRedeemVoidedAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_REDEEM_VOIDED_COUNT, safeSQLCast(getGiftCardRedeemVoidedCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_HOUSE_ACCOUNT_ENROLLMENT_APPROVED_COUNT, safeSQLCast(getHouseCardEnrollmentsApprovalCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_HOUSE_ACCOUNT_ENROLLMENT_DECLINED_COUNT, safeSQLCast(getHouseCardEnrollmentsDeclinedCount(totals)));

        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_CREDIT_AMOUNT,
          safeSQLCast(getAmountGrossGiftCardItemCredit(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_CREDIT_UNITS,
          safeSQLCast(getUnitsGrossGiftCardItemCredit(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_CREDIT_VOIDED_AMOUNT,
          safeSQLCast(getAmountGrossGiftCardItemCreditVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_CREDIT_VOIDED_UNITS,
          safeSQLCast(getUnitsGrossGiftCardItemCreditVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_REDEEMED_AMOUNT,
          safeSQLCast(getAmountGrossGiftCertificatesRedeemed(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_REDEEMED_UNITS,
          safeSQLCast(getUnitsGrossGiftCertificatesRedeemed(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_REDEEMED_VOIDED_AMOUNT,
          safeSQLCast(getAmountGrossGiftCertificatesRedeemedVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_REDEEMED_VOIDED_UNITS,
          safeSQLCast(getUnitsGrossGiftCertificatesRedeemedVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_ISSUED_AMOUNT,
          safeSQLCast(getAmountGrossStoreCreditsIssued(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_ISSUED_UNITS,
          safeSQLCast(getUnitsGrossStoreCreditsIssued(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_ISSUED_VOIDED_AMOUNT,
          safeSQLCast(getAmountGrossStoreCreditsIssuedVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_ISSUED_VOIDED_UNITS,
          safeSQLCast(getUnitsGrossStoreCreditsIssuedVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_REDEEMED_AMOUNT,
          safeSQLCast(getAmountGrossStoreCreditsRedeemed(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_REDEEMED_UNITS,
          safeSQLCast(getUnitsGrossStoreCreditsRedeemed(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_REDEEMED_VOIDED_AMOUNT,
          safeSQLCast(getAmountGrossStoreCreditsRedeemedVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_REDEEMED_VOIDED_UNITS,
          safeSQLCast(getUnitsGrossStoreCreditsRedeemedVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_ITEM_EMPLOYEE_DISCOUNT_AMOUNT,
          safeSQLCast(getAmountGrossItemEmployeeDiscount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_ITEM_EMPLOYEE_DISCOUNT_UNITS,
          safeSQLCast(getUnitsGrossItemEmployeeDiscount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_ITEM_EMPLOYEE_DISCOUNT_VOIDED_AMOUNT,
          safeSQLCast(getAmountGrossItemEmployeeDiscountVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_ITEM_EMPLOYEE_DISCOUNT_VOIDED_UNITS,
          safeSQLCast(getUnitsGrossItemEmployeeDiscountVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_AMOUNT,
          safeSQLCast(getAmountGrossTransactionEmployeeDiscount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_UNITS,
          safeSQLCast(getUnitsGrossTransactionEmployeeDiscount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_VOIDED_AMOUNT,
          safeSQLCast(getAmountGrossTransactionEmployeeDiscountVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_VOIDED_UNITS,
          safeSQLCast(getUnitsGrossTransactionEmployeeDiscountVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_ISSUED_VOIDED_AMOUNT,
          safeSQLCast(getAmountGrossGiftCertificateIssuedVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_ISSUED_VOIDED_COUNT,
          safeSQLCast(getUnitsGrossGiftCertificateIssuedVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_TENDERED_AMOUNT,
          safeSQLCast(getAmountGrossGiftCertificateTendered(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_TENDERED_COUNT,
          safeSQLCast(getUnitsGrossGiftCertificateTendered(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_TENDERED_VOIDED_AMOUNT,
          safeSQLCast(getAmountGrossGiftCertificateTenderedVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_TENDERED_VOIDED_COUNT,
          safeSQLCast(getUnitsGrossGiftCertificateTenderedVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_EMPLOYEE_DISCOUNTS_AMOUNT,
          safeSQLCast(getAmountEmployeeDiscounts(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_EMPLOYEE_DISCOUNTS_COUNT,
          safeSQLCast(getUnitsEmployeeDiscounts(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_CUSTOMER_DISCOUNTS_AMOUNT,
          safeSQLCast(getAmountCustomerDiscounts(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_CUSTOMER_DISCOUNTS_COUNT,
          safeSQLCast(getUnitsCustomerDiscounts(totals)));
        sql.addColumn(FIELD_WORKSTATION_PRICE_OVERRIDES_AMOUNT,
          safeSQLCast(getAmountPriceOverrides(totals)));
        sql.addColumn(FIELD_WORKSTATION_PRICE_OVERRIDES_COUNT,
          safeSQLCast(getUnitsPriceOverrides(totals)));
        sql.addColumn(FIELD_WORKSTATION_PRICE_ADJUSTMENTS_COUNT,
          safeSQLCast(getUnitsPriceAdjustments(totals)));
        sql.addColumn(FIELD_WORKSTATION_TRANSACTIONS_WITH_RETURNED_ITEMS_COUNT,
          safeSQLCast(getCountTransactionsWithReturnedItems(totals)));
        if (totals != null)
        {
            sql.addColumn(FIELD_WORKSTATION_TOTAL_LAYAWAY_PAYMENTS_COLLECTED_COUNT,
                          safeSQLCast(Integer.toString(totals.getCountLayawayPayments())));
            sql.addColumn(FIELD_WORKSTATION_LAYAWAY_PAYMENTS_COLLECTED_TOTAL_AMOUNT,
                          safeSQLCast(totals.getAmountLayawayPayments().getStringValue()));
        	sql.addColumn(FIELD_WORKSTATION_LAYAWAY_NEW_TOTAL_AMOUNT,
                    	  safeSQLCast(totals.getAmountLayawayNew().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_LAYAWAY_PICKUP_TOTAL_AMOUNT,
              	  		  safeSQLCast(totals.getAmountLayawayPickup().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TOTAL_LAYAWAY_DELETIONS_DISBURSEMENT_COUNT,
                          safeSQLCast(Integer.toString(totals.getCountLayawayDeletions())));
            sql.addColumn(FIELD_WORKSTATION_LAYAWAY_DELETIONS_DISBURSEMENT_TOTAL_AMOUNT,
                          safeSQLCast(totals.getAmountLayawayDeletions().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_LAYAWAY_INITIATION_FEES_COLLECTED_TOTAL_AMOUNT,
                          safeSQLCast(totals.getAmountLayawayInitiationFees().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TOTAL_LAYAWAY_INITIATION_FEES_COLLECTED_COUNT,
                          safeSQLCast(Integer.toString(totals.getCountLayawayInitiationFees())));
            sql.addColumn(FIELD_WORKSTATION_LAYAWAY_DELETION_FEES_COLLECTED_TOTAL_AMOUNT,
                          safeSQLCast(totals.getAmountLayawayDeletionFees().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TOTAL_LAYAWAY_DELETION_FEES_COUNT,
                          safeSQLCast(Integer.toString(totals.getCountLayawayDeletionFees())));
            sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_IN_TOTAL_AMOUNT,
                          safeSQLCast(totals.getAmountTillPayIns().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_OUT_TOTAL_AMOUNT,
                          safeSQLCast(totals.getAmountTillPayOuts().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_IN_UNIT_COUNT,
                          safeSQLCast(Integer.toString(totals.getCountTillPayIns())));
            sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_OUT_UNIT_COUNT,
                          safeSQLCast(Integer.toString(totals.getCountTillPayOuts())));
            sql.addColumn(FIELD_WORKSTATION_TOTAL_ORDER_PAYMENTS_COLLECTED_COUNT,
                          safeSQLCast(Integer.toString(totals.getCountOrderPayments())));
            sql.addColumn(FIELD_WORKSTATION_ORDER_PAYMENTS_COLLECTED_TOTAL_AMOUNT,
                          safeSQLCast(totals.getAmountOrderPayments().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TOTAL_ORDER_CANCELS_DISBURSEMENT_COUNT,
                          safeSQLCast(Integer.toString(totals.getCountOrderCancels())));
            sql.addColumn(FIELD_WORKSTATION_ORDER_CANCELS_DISBURSEMENT_TOTAL_AMOUNT,
                          safeSQLCast(totals.getAmountOrderCancels().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_SHIPPING_CHARGE_TOTAL_AMOUNT,
                          safeSQLCast(totals.getAmountShippingCharges().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TOTAL_SHIPPING_CHARGE_COUNT,
                          safeSQLCast(Integer.toString(totals.getNumberShippingCharges())));
            sql.addColumn(FIELD_WORKSTATION_SHIPPING_CHARGE_TAX_AMOUNT,
                    safeSQLCast(totals.getAmountTaxShippingCharges().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_SHIPPING_CHARGE_INCLUSIVE_TAX_AMOUNT,
                    safeSQLCast(totals.getAmountInclusiveTaxShippingCharges().getStringValue()));
        }


        // Tax
        sql.addColumn(FIELD_WORKSTATION_TAX_TOTAL_AMOUNT, safeSQLCast(getNetTaxAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_INCLUSIVE_TAX_TOTAL_AMOUNT, safeSQLCast(getNetInclusiveTaxAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_REFUND_TAX_TOTAL_AMOUNT, safeSQLCast(getTaxRefundedAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_REFUND_INCLUSIVE_TAX_TOTAL_AMOUNT, safeSQLCast(getInclusiveTaxRefundedAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_RETURN_TAX_TOTAL_AMOUNT, safeSQLCast(getTaxReturnedAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_RETURN_INCLUSIVE_TAX_TOTAL_AMOUNT, safeSQLCast(getInclusiveTaxReturnedAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_ITEM_SALES_TAX_AMOUNT, safeSQLCast(getItemTaxAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_ITEM_SALES_INCLUSIVE_TAX_AMOUNT, safeSQLCast(getItemSalesInclusiveTaxAmount(totals)));
        if (totals != null)
        {
            sql.addColumn(FIELD_WORKSTATION_TRANSACTION_SALES_TAX_AMOUNT, safeSQLCast(totals.getAmountTaxTransactionSales().toString()));
            sql.addColumn(FIELD_WORKSTATION_TRANSACTION_SALES_INCLUSIVE_TAX_AMOUNT, safeSQLCast(totals.getAmountInclusiveTaxTransactionSales().toString()));
        }
        // Misc
            //StoreCouponDiscounts
        sql.addColumn(FIELD_WORKSTATION_ITEM_DISCOUNT_STORE_COUPON_AMOUNT,
                      safeSQLCast(getItemDiscStoreCouponAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_ITEM_DISCOUNT_STORE_COUPON_COUNT,
                      safeSQLCast(getItemDiscStoreCouponCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TRANSACTION_DISCOUNT_STORE_COUPON_AMOUNT,
                      safeSQLCast(getTransactionDiscStoreCouponAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TRANSACTION_DISCOUNT_STORE_COUPON_COUNT,
                      safeSQLCast(getTransactionDiscStoreCouponCount(totals)));
        //
        sql.addColumn(FIELD_WORKSTATION_MISCELLANEOUS_DISCOUNT_TOTAL_AMOUNT,
                      safeSQLCast(getTransactionDiscountAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_MISCELLANEOUS_DISCOUNT_COUNT,
                      safeSQLCast(getTransactionDiscountCount(totals)));

        sql.addColumn(FIELD_WORKSTATION_DISCOUNT_TOTAL_AMOUNT, safeSQLCast(getDiscountAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_DISCOUNT_COUNT, safeSQLCast(getDiscountCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_MARKDOWN_TOTAL_AMOUNT, safeSQLCast(getMarkdownAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_MARKDOWN_COUNT, safeSQLCast(getMarkdownCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_POST_TRANSACTION_VOID_TOTAL_AMOUNT, safeSQLCast(getPostVoidAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_POST_TRANSACTION_VOID_COUNT, safeSQLCast(getPostVoidCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_LINE_ITEM_VOID_TOTAL_AMOUNT, safeSQLCast(getLineVoidAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_LINE_ITEM_VOID_COUNT, safeSQLCast(getLineVoidCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TRANSACTION_VOID_TOTAL_AMOUNT, safeSQLCast(getVoidAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TRANSACTION_VOID_COUNT, safeSQLCast(getVoidCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NO_SALE_TRANSACTION_COUNT, safeSQLCast(getNoSaleCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_PICKUP_COUNT, safeSQLCast(getTenderPickupCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_PICKUP_TOTAL_AMOUNT, safeSQLCast(getTenderPickupAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_LOAN_COUNT, safeSQLCast(getTenderLoanCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_LOAN_TOTAL_AMOUNT, safeSQLCast(getTenderLoanAmount(totals)));
        sql.addColumn(FIELD_GROSS_TAXABLE_SALES_VOID_COUNT, getTaxableSalesVoidCount(totals));
        sql.addColumn(FIELD_GROSS_TAXABLE_RETURNS_VOID_COUNT, getTaxableReturnsVoidCount(totals));
        sql.addColumn(FIELD_GROSS_NONTAXABLE_SALES_VOID_COUNT, getNonTaxableSalesVoidCount(totals));
        sql.addColumn(FIELD_GROSS_NONTAXABLE_RETURNS_VOID_COUNT, getNonTaxableReturnsVoidCount(totals));
        // set timestamps
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
    }

    /**
        Adds the columns and values for the financial totals fields
        <p>
        @param  sql     The SQL statement to add the column-value pairs to
        @param  totals  The financial totals to draw the values from
     */
    protected void addFinancialTotals(SQLUpdatableStatementIfc sql, FinancialTotalsIfc totals)
    {
        // Totals
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TRANSACTION_COUNT,
                      FIELD_WORKSTATION_TOTAL_TRANSACTION_COUNT + "+" + safeSQLCast(getTransactionCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_NONTAXABLE_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_NONTAXABLE_TOTAL_AMOUNT + "+" + safeSQLCast(getNetNontaxableAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONTAXABLE_COUNT,
                      FIELD_WORKSTATION_TOTAL_NONTAXABLE_COUNT + "+" + safeSQLCast(getNontaxableCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TAXABLE_COUNT,
                      FIELD_WORKSTATION_TOTAL_TAXABLE_COUNT + "+" + safeSQLCast(getTaxableCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TAX_EXEMPT_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_TAX_EXEMPT_TOTAL_AMOUNT + "+" + safeSQLCast(getNetTaxExemptAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TAX_EXEMPT_TRANSACTION_COUNT,
                      FIELD_WORKSTATION_TOTAL_TAX_EXEMPT_TRANSACTION_COUNT + "+" + safeSQLCast(getTaxExemptCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_REFUND_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_REFUND_TOTAL_AMOUNT + "+" + safeSQLCast(getRefundAmount(totals)));
        // Transaction Sales/Refunds
        sql.addColumn(FIELD_WORKSTATION_GROSS_SALES_EX_TAX_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_GROSS_SALES_EX_TAX_TOTAL_AMOUNT + "+" + safeSQLCast(getSalesAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_REFUND_COUNT,
                      FIELD_WORKSTATION_TOTAL_REFUND_COUNT + "+" + safeSQLCast(getRefundCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_REFUND_NONTAXABLE_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_REFUND_NONTAXABLE_TOTAL_AMOUNT + "+" + safeSQLCast(getRefundNontaxableAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONTAXABLE_REFUND_COUNT,
                      FIELD_WORKSTATION_TOTAL_NONTAXABLE_REFUND_COUNT + "+" + safeSQLCast(getRefundNontaxableCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_REFUND_TAX_EXEMPT_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_REFUND_TAX_EXEMPT_TOTAL_AMOUNT + "+" + safeSQLCast(getRefundTaxExemptAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TAX_EXEMPT_REFUND_COUNT,
                      FIELD_WORKSTATION_TOTAL_TAX_EXEMPT_REFUND_COUNT + "+" + safeSQLCast(getRefundTaxExemptCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_TRANSACTION_SALES_AMOUNT,
                      FIELD_WORKSTATION_GROSS_TAXABLE_TRANSACTION_SALES_AMOUNT + "+" + safeSQLCast(getTaxableTransactionSalesAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_TRANSACTION_SALES_COUNT,
                      FIELD_WORKSTATION_GROSS_TAXABLE_TRANSACTION_SALES_COUNT + "+" + safeSQLCast(getTaxableTransactionSalesCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_NON_TAXABLE_TRANSACTION_SALES_AMOUNT,
                      FIELD_WORKSTATION_GROSS_NON_TAXABLE_TRANSACTION_SALES_AMOUNT + "+" + safeSQLCast(getNonTaxableTransactionSalesAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_NON_TAXABLE_TRANSACTION_SALES_COUNT,
                      FIELD_WORKSTATION_GROSS_NON_TAXABLE_TRANSACTION_SALES_COUNT + "+" + safeSQLCast(getNonTaxableTransactionSalesCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAX_EXEMPT_TRANSACTION_SALES_AMOUNT,
                      FIELD_WORKSTATION_GROSS_TAX_EXEMPT_TRANSACTION_SALES_AMOUNT + "+" + safeSQLCast(getTaxExemptTransactionSalesAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAX_EXEMPT_TRANSACTION_SALES_COUNT,
                      FIELD_WORKSTATION_GROSS_TAX_EXEMPT_TRANSACTION_SALES_COUNT + "+" + safeSQLCast(getTaxExemptTransactionSalesCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_TRANSACTION_RETURNS_AMOUNT,
                      FIELD_WORKSTATION_GROSS_TAXABLE_TRANSACTION_RETURNS_AMOUNT + "+" + safeSQLCast(getTaxableTransactionReturnsAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_TRANSACTION_RETURNS_COUNT,
                      FIELD_WORKSTATION_GROSS_TAXABLE_TRANSACTION_RETURNS_COUNT + "+" + safeSQLCast(getTaxableTransactionReturnsCount(totals)));

        // Item Sales/Returns
        sql.addColumn(FIELD_WORKSTATION_LINE_ITEM_SALES_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_LINE_ITEM_SALES_TOTAL_AMOUNT + "+" + safeSQLCast(getItemSalesAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_SALE_LINE_ITEM_COUNT,
                      FIELD_WORKSTATION_TOTAL_SALE_LINE_ITEM_COUNT + "+" + safeSQLCast(getItemSalesCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_LINE_ITEM_NONTAXABLE_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_LINE_ITEM_NONTAXABLE_TOTAL_AMOUNT + "+" + safeSQLCast(getItemNontaxableAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONTAXABLE_LINE_ITEM_COUNT,
                      FIELD_WORKSTATION_TOTAL_NONTAXABLE_LINE_ITEM_COUNT + "+" + safeSQLCast(getItemNontaxableCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_LINE_ITEM_TAX_EXEMPT_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_LINE_ITEM_TAX_EXEMPT_TOTAL_AMOUNT + "+" + safeSQLCast(getItemTaxExemptAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TAX_EXEMPT_LINE_ITEM_COUNT,
                      FIELD_WORKSTATION_TOTAL_TAX_EXEMPT_LINE_ITEM_COUNT + "+" + safeSQLCast(getItemTaxExemptCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_RETURN_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_RETURN_TOTAL_AMOUNT + "+" + safeSQLCast(getReturnAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_RETURN_COUNT,
                      FIELD_WORKSTATION_TOTAL_RETURN_COUNT + "+" + safeSQLCast(getReturnCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_RETURN_NONTAXABLE_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_RETURN_NONTAXABLE_TOTAL_AMOUNT + "+" + safeSQLCast(getReturnNontaxableAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONTAXABLE_RETURN_COUNT,
                      FIELD_WORKSTATION_TOTAL_NONTAXABLE_RETURN_COUNT + "+" + safeSQLCast(getReturnNontaxableCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_RETURN_TAX_EXEMPT_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_RETURN_TAX_EXEMPT_TOTAL_AMOUNT + "+" + safeSQLCast(getReturnTaxExemptAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TAX_EXEMPT_RETURN_COUNT,
                      FIELD_WORKSTATION_TOTAL_TAX_EXEMPT_RETURN_COUNT + "+" + safeSQLCast(getReturnTaxExemptCount(totals)));

        sql.addColumn(FIELD_WORKSTATION_NONMERCH_NONTAX_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_NONMERCH_NONTAX_TOTAL_AMOUNT + "+" + safeSQLCast(getNonMerchNonTaxAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONMERCH_NONTAX_COUNT,
                      FIELD_WORKSTATION_TOTAL_NONMERCH_NONTAX_COUNT + "+" + safeSQLCast(getNonMerchNonTaxCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_RETURN_NONMERCH_NONTAX_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_RETURN_NONMERCH_NONTAX_TOTAL_AMOUNT + "+" + safeSQLCast(getNonMerchNonTaxReturnAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONMERCH_NONTAX_RETURN_COUNT,
                      FIELD_WORKSTATION_TOTAL_NONMERCH_NONTAX_RETURN_COUNT + "+" + safeSQLCast(getNonMerchNonTaxReturnCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_NONMERCH_TAX_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_NONMERCH_TAX_TOTAL_AMOUNT + "+" + safeSQLCast(getNonMerchAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONMERCH_TAX_COUNT,
                      FIELD_WORKSTATION_TOTAL_NONMERCH_TAX_COUNT + "+" + safeSQLCast(getNonMerchCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_RETURN_NONMERCH_TAX_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_RETURN_NONMERCH_TAX_TOTAL_AMOUNT + "+" + safeSQLCast(getNonMerchReturnAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONMERCH_TAX_RETURN_COUNT,
                      FIELD_WORKSTATION_TOTAL_NONMERCH_TAX_RETURN_COUNT + "+" + safeSQLCast(getNonMerchReturnCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_AMOUNT,
                      FIELD_WORKSTATION_GIFT_CARD_AMOUNT + "+" + safeSQLCast(getGiftCardAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_COUNT,
                      FIELD_WORKSTATION_GIFT_CARD_COUNT + "+" + safeSQLCast(getGiftCardCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_RETURN_GIFT_CARD_AMOUNT,
                      FIELD_WORKSTATION_RETURN_GIFT_CARD_AMOUNT + "+" + safeSQLCast(getGiftCardReturnAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_RETURN_COUNT,
                      FIELD_WORKSTATION_GIFT_CARD_RETURN_COUNT + "+" + safeSQLCast(getGiftCardReturnCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_HOUSE_PAYMENT_AMOUNT,
                      FIELD_WORKSTATION_HOUSE_PAYMENT_AMOUNT + "+" + safeSQLCast(getHousePaymentAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_HOUSE_PAYMENT_COUNT,
                      FIELD_WORKSTATION_HOUSE_PAYMENT_COUNT + "+" + safeSQLCast(getHousePaymentCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_RESTOCKING_FEE_AMOUNT,
                      FIELD_WORKSTATION_RESTOCKING_FEE_AMOUNT + "+" + safeSQLCast(getRestockingFeeAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_RESTOCKING_FEE_COUNT,
                      FIELD_WORKSTATION_RESTOCKING_FEE_COUNT + "+" + safeSQLCast(getRestockingFeeCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_RESTOCKING_FEE_AMOUNT_FROM_NON_TAXABLE,
                FIELD_WORKSTATION_RESTOCKING_FEE_AMOUNT_FROM_NON_TAXABLE    + "+" + safeSQLCast(getRestockingFeeFromNonTaxableAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_RESTOCKING_FEE_COUNT_FROM_NON_TAXABLE,
                      FIELD_WORKSTATION_RESTOCKING_FEE_COUNT_FROM_NON_TAXABLE + "+" + safeSQLCast(getRestockingFeeFromNonTaxableCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_NON_MERCHANDISE_SALES_AMOUNT,
                      FIELD_WORKSTATION_GROSS_TAXABLE_NON_MERCHANDISE_SALES_AMOUNT + "+" + safeSQLCast(getTaxableNonMerchandiseSalesAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_NON_MERCHANDISE_SALES_COUNT,
                      FIELD_WORKSTATION_GROSS_TAXABLE_NON_MERCHANDISE_SALES_COUNT + "+" + safeSQLCast(getTaxableNonMerchandiseSalesCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES_AMOUNT,
                      FIELD_WORKSTATION_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES_AMOUNT + "+" + safeSQLCast(getNonTaxableNonMerchandiseSalesAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES_COUNT,
                      FIELD_WORKSTATION_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES_COUNT + "+" + safeSQLCast(getNonTaxableNonMerchandiseSalesCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_SALES_AMOUNT,
                      FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_SALES_AMOUNT + "+" + safeSQLCast(getGiftCardItemSalesAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_SALES_COUNT,
                      FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_SALES_COUNT + "+" + safeSQLCast(getGiftCardItemSalesCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CERTIFICATE_ISSUED_AMOUNT,
                      FIELD_WORKSTATION_GIFT_CERTIFICATE_ISSUED_AMOUNT + "+" + safeSQLCast(getGiftCertificateIssuedAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CERTIFICATE_ISSUED_COUNT,
                      FIELD_WORKSTATION_GIFT_CERTIFICATE_ISSUED_COUNT + "+" + safeSQLCast(getGiftCertificateIssuedCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_ISSUED_AMOUNT,
                FIELD_WORKSTATION_GIFT_CARD_ISSUED_AMOUNT + "+" + safeSQLCast(getGiftCardIssuedAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_ISSUED_COUNT,
                FIELD_WORKSTATION_GIFT_CARD_ISSUED_COUNT + "+" + safeSQLCast(getGiftCardIssuedCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_RELOADED_AMOUNT,
                FIELD_WORKSTATION_GIFT_CARD_RELOADED_AMOUNT + "+" + safeSQLCast(getGiftCardReloadedAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_RELOADED_COUNT,
                FIELD_WORKSTATION_GIFT_CARD_RELOADED_COUNT + "+" + safeSQLCast(getGiftCardReloadedCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_REDEEMED_AMOUNT,
                FIELD_WORKSTATION_GIFT_CARD_REDEEMED_AMOUNT + "+" + safeSQLCast(getGiftCardRedeemedAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_REDEEMED_COUNT,
                FIELD_WORKSTATION_GIFT_CARD_REDEEMED_COUNT + "+" + safeSQLCast(getGiftCardRedeemedCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_ISSUE_VOIDED_AMOUNT,
                FIELD_WORKSTATION_GIFT_CARD_ISSUE_VOIDED_AMOUNT + "+" + safeSQLCast(getGiftCardIssueVoidedAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_ISSUE_VOIDED_COUNT,
                FIELD_WORKSTATION_GIFT_CARD_ISSUE_VOIDED_COUNT + "+" + safeSQLCast(getGiftCardIssueVoidedCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_RELOAD_VOIDED_AMOUNT,
                FIELD_WORKSTATION_GIFT_CARD_RELOAD_VOIDED_AMOUNT + "+" + safeSQLCast(getGiftCardReloadVoidedAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_RELOAD_VOIDED_COUNT,
                FIELD_WORKSTATION_GIFT_CARD_RELOAD_VOIDED_COUNT + "+" + safeSQLCast(getGiftCardReloadVoidedCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_REDEEM_VOIDED_AMOUNT,
                FIELD_WORKSTATION_GIFT_CARD_REDEEM_VOIDED_AMOUNT + "+" + safeSQLCast(getGiftCardRedeemVoidedAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_REDEEM_VOIDED_COUNT,
                FIELD_WORKSTATION_GIFT_CARD_REDEEM_VOIDED_COUNT + "+" + safeSQLCast(getGiftCardRedeemVoidedCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_HOUSE_ACCOUNT_ENROLLMENT_APPROVED_COUNT,
                FIELD_WORKSTATION_HOUSE_ACCOUNT_ENROLLMENT_APPROVED_COUNT + "+" + safeSQLCast(getHouseCardEnrollmentsApprovalCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_HOUSE_ACCOUNT_ENROLLMENT_DECLINED_COUNT,
                FIELD_WORKSTATION_HOUSE_ACCOUNT_ENROLLMENT_DECLINED_COUNT+ "+" + safeSQLCast(getHouseCardEnrollmentsDeclinedCount(totals)));

        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_CREDIT_AMOUNT,
          FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_CREDIT_AMOUNT + "+" + safeSQLCast(getAmountGrossGiftCardItemCredit(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_CREDIT_UNITS,
          FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_CREDIT_UNITS + "+" + safeSQLCast(getUnitsGrossGiftCardItemCredit(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_CREDIT_VOIDED_AMOUNT,
          FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_CREDIT_VOIDED_AMOUNT + "+" + safeSQLCast(getAmountGrossGiftCardItemCreditVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_CREDIT_VOIDED_UNITS,
          FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_CREDIT_VOIDED_UNITS + "+" + safeSQLCast(getUnitsGrossGiftCardItemCreditVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_REDEEMED_AMOUNT,
          FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_REDEEMED_AMOUNT + "+" + safeSQLCast(getAmountGrossGiftCertificatesRedeemed(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_REDEEMED_UNITS,
          FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_REDEEMED_UNITS + "+" + safeSQLCast(getUnitsGrossGiftCertificatesRedeemed(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_REDEEMED_VOIDED_AMOUNT,
          FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_REDEEMED_VOIDED_AMOUNT + "+" + safeSQLCast(getAmountGrossGiftCertificatesRedeemedVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_REDEEMED_VOIDED_UNITS,
          FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_REDEEMED_VOIDED_UNITS + "+" + safeSQLCast(getUnitsGrossGiftCertificatesRedeemedVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_ISSUED_AMOUNT,
          FIELD_WORKSTATION_GROSS_STORE_CREDITS_ISSUED_AMOUNT + "+" + safeSQLCast(getAmountGrossStoreCreditsIssued(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_ISSUED_UNITS,
          FIELD_WORKSTATION_GROSS_STORE_CREDITS_ISSUED_UNITS + "+" + safeSQLCast(getUnitsGrossStoreCreditsIssued(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_ISSUED_VOIDED_AMOUNT,
          FIELD_WORKSTATION_GROSS_STORE_CREDITS_ISSUED_VOIDED_AMOUNT + "+" + safeSQLCast(getAmountGrossStoreCreditsIssuedVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_ISSUED_VOIDED_UNITS,
          FIELD_WORKSTATION_GROSS_STORE_CREDITS_ISSUED_VOIDED_UNITS + "+" + safeSQLCast(getUnitsGrossStoreCreditsIssuedVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_REDEEMED_AMOUNT,
          FIELD_WORKSTATION_GROSS_STORE_CREDITS_REDEEMED_AMOUNT + "+" + safeSQLCast(getAmountGrossStoreCreditsRedeemed(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_REDEEMED_UNITS,
          FIELD_WORKSTATION_GROSS_STORE_CREDITS_REDEEMED_UNITS + "+" + safeSQLCast(getUnitsGrossStoreCreditsRedeemed(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_REDEEMED_VOIDED_AMOUNT,
          FIELD_WORKSTATION_GROSS_STORE_CREDITS_REDEEMED_VOIDED_AMOUNT + "+" + safeSQLCast(getAmountGrossStoreCreditsRedeemedVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_REDEEMED_VOIDED_UNITS,
          FIELD_WORKSTATION_GROSS_STORE_CREDITS_REDEEMED_VOIDED_UNITS + "+" + safeSQLCast(getUnitsGrossStoreCreditsRedeemedVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_ITEM_EMPLOYEE_DISCOUNT_AMOUNT,
          FIELD_WORKSTATION_GROSS_ITEM_EMPLOYEE_DISCOUNT_AMOUNT + "+" + safeSQLCast(getAmountGrossItemEmployeeDiscount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_ITEM_EMPLOYEE_DISCOUNT_UNITS,
          FIELD_WORKSTATION_GROSS_ITEM_EMPLOYEE_DISCOUNT_UNITS + "+" + safeSQLCast(getUnitsGrossItemEmployeeDiscount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_ITEM_EMPLOYEE_DISCOUNT_VOIDED_AMOUNT,
          FIELD_WORKSTATION_GROSS_ITEM_EMPLOYEE_DISCOUNT_VOIDED_AMOUNT + "+" + safeSQLCast(getAmountGrossItemEmployeeDiscountVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_ITEM_EMPLOYEE_DISCOUNT_VOIDED_UNITS,
          FIELD_WORKSTATION_GROSS_ITEM_EMPLOYEE_DISCOUNT_VOIDED_UNITS + "+" + safeSQLCast(getUnitsGrossItemEmployeeDiscountVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_AMOUNT,
          FIELD_WORKSTATION_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_AMOUNT + "+" + safeSQLCast(getAmountGrossTransactionEmployeeDiscount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_UNITS,
          FIELD_WORKSTATION_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_UNITS + "+" + safeSQLCast(getUnitsGrossTransactionEmployeeDiscount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_VOIDED_AMOUNT,
          FIELD_WORKSTATION_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_VOIDED_AMOUNT + "+" + safeSQLCast(getAmountGrossTransactionEmployeeDiscountVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_VOIDED_UNITS,
          FIELD_WORKSTATION_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_VOIDED_UNITS + "+" + safeSQLCast(getUnitsGrossTransactionEmployeeDiscountVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_ISSUED_VOIDED_AMOUNT,
          FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_ISSUED_VOIDED_AMOUNT + "+" + safeSQLCast(getAmountGrossGiftCertificateIssuedVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_ISSUED_VOIDED_COUNT,
          FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_ISSUED_VOIDED_COUNT + "+" + safeSQLCast(getUnitsGrossGiftCertificateIssuedVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_TENDERED_AMOUNT,
          FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_TENDERED_AMOUNT + "+" + safeSQLCast(getAmountGrossGiftCertificateTendered(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_TENDERED_COUNT,
          FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_TENDERED_COUNT + "+" + safeSQLCast(getUnitsGrossGiftCertificateTendered(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_TENDERED_VOIDED_AMOUNT,
          FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_TENDERED_VOIDED_AMOUNT + "+" + safeSQLCast(getAmountGrossGiftCertificateTenderedVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_TENDERED_VOIDED_COUNT,
          FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_TENDERED_VOIDED_COUNT + "+" + safeSQLCast(getUnitsGrossGiftCertificateTenderedVoided(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_EMPLOYEE_DISCOUNTS_AMOUNT,
          FIELD_WORKSTATION_GROSS_EMPLOYEE_DISCOUNTS_AMOUNT + "+" + safeSQLCast(getAmountEmployeeDiscounts(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_EMPLOYEE_DISCOUNTS_COUNT,
          FIELD_WORKSTATION_GROSS_EMPLOYEE_DISCOUNTS_COUNT + "+" + safeSQLCast(getUnitsEmployeeDiscounts(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_CUSTOMER_DISCOUNTS_AMOUNT,
          FIELD_WORKSTATION_GROSS_CUSTOMER_DISCOUNTS_AMOUNT + "+" + safeSQLCast(getAmountCustomerDiscounts(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_CUSTOMER_DISCOUNTS_COUNT,
          FIELD_WORKSTATION_GROSS_CUSTOMER_DISCOUNTS_COUNT + "+" + safeSQLCast(getUnitsCustomerDiscounts(totals)));
        sql.addColumn(FIELD_WORKSTATION_PRICE_OVERRIDES_AMOUNT,
          FIELD_WORKSTATION_PRICE_OVERRIDES_AMOUNT + "+" + safeSQLCast(getAmountPriceOverrides(totals)));
        sql.addColumn(FIELD_WORKSTATION_PRICE_OVERRIDES_COUNT,
          FIELD_WORKSTATION_PRICE_OVERRIDES_COUNT + "+" + safeSQLCast(getUnitsPriceOverrides(totals)));
        sql.addColumn(FIELD_WORKSTATION_PRICE_ADJUSTMENTS_COUNT,
                FIELD_WORKSTATION_PRICE_ADJUSTMENTS_COUNT + "+" + safeSQLCast(getUnitsPriceAdjustments(totals)));
        sql.addColumn(FIELD_WORKSTATION_TRANSACTIONS_WITH_RETURNED_ITEMS_COUNT,
          FIELD_WORKSTATION_TRANSACTIONS_WITH_RETURNED_ITEMS_COUNT + "+" + safeSQLCast(getCountTransactionsWithReturnedItems(totals)));


        if (totals != null)
        {
            sql.addColumn(FIELD_WORKSTATION_TOTAL_LAYAWAY_PAYMENTS_COLLECTED_COUNT,
                          FIELD_WORKSTATION_TOTAL_LAYAWAY_PAYMENTS_COLLECTED_COUNT + "+" +
                          totals.getCountLayawayPayments());
            sql.addColumn(FIELD_WORKSTATION_LAYAWAY_PAYMENTS_COLLECTED_TOTAL_AMOUNT,
                          FIELD_WORKSTATION_LAYAWAY_PAYMENTS_COLLECTED_TOTAL_AMOUNT + "+" +
                          safeSQLCast(totals.getAmountLayawayPayments().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_LAYAWAY_NEW_TOTAL_AMOUNT,
            			  FIELD_WORKSTATION_LAYAWAY_NEW_TOTAL_AMOUNT + "+" +
                          safeSQLCast(totals.getAmountLayawayNew().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_LAYAWAY_PICKUP_TOTAL_AMOUNT,
        				  FIELD_WORKSTATION_LAYAWAY_PICKUP_TOTAL_AMOUNT + "+" +
            			  safeSQLCast(totals.getAmountLayawayPickup().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TOTAL_LAYAWAY_DELETIONS_DISBURSEMENT_COUNT,
                          FIELD_WORKSTATION_TOTAL_LAYAWAY_DELETIONS_DISBURSEMENT_COUNT + "+" +
                          totals.getCountLayawayDeletions());
            sql.addColumn(FIELD_WORKSTATION_LAYAWAY_DELETIONS_DISBURSEMENT_TOTAL_AMOUNT,
                          FIELD_WORKSTATION_LAYAWAY_DELETIONS_DISBURSEMENT_TOTAL_AMOUNT + "+" +
                          safeSQLCast(totals.getAmountLayawayDeletions().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_LAYAWAY_INITIATION_FEES_COLLECTED_TOTAL_AMOUNT,
                          FIELD_WORKSTATION_LAYAWAY_INITIATION_FEES_COLLECTED_TOTAL_AMOUNT + "+" +
                          safeSQLCast(totals.getAmountLayawayInitiationFees().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TOTAL_LAYAWAY_INITIATION_FEES_COLLECTED_COUNT,
                          FIELD_WORKSTATION_TOTAL_LAYAWAY_INITIATION_FEES_COLLECTED_COUNT + "+" +
                          totals.getCountLayawayInitiationFees());
            sql.addColumn(FIELD_WORKSTATION_LAYAWAY_DELETION_FEES_COLLECTED_TOTAL_AMOUNT,
                          FIELD_WORKSTATION_LAYAWAY_DELETION_FEES_COLLECTED_TOTAL_AMOUNT + "+" +
                          safeSQLCast(totals.getAmountLayawayDeletionFees().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TOTAL_LAYAWAY_DELETION_FEES_COUNT,
                          FIELD_WORKSTATION_TOTAL_LAYAWAY_DELETION_FEES_COUNT + "+" +
                          totals.getCountLayawayDeletionFees());
            sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_IN_TOTAL_AMOUNT,
                          FIELD_WORKSTATION_FUNDS_RECEIVED_IN_TOTAL_AMOUNT + " + " +
                          safeSQLCast(totals.getAmountTillPayIns().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_OUT_TOTAL_AMOUNT,
                          FIELD_WORKSTATION_FUNDS_RECEIVED_OUT_TOTAL_AMOUNT + " + " +
                          safeSQLCast(totals.getAmountTillPayOuts().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_IN_UNIT_COUNT,
                          FIELD_WORKSTATION_FUNDS_RECEIVED_IN_UNIT_COUNT + " + " +
                          safeSQLCast(Integer.toString(totals.getCountTillPayIns())));
            sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_OUT_UNIT_COUNT,
                          FIELD_WORKSTATION_FUNDS_RECEIVED_OUT_UNIT_COUNT + " + " +
                          safeSQLCast(Integer.toString(totals.getCountTillPayOuts())));
            sql.addColumn(FIELD_WORKSTATION_SPECIAL_ORDER_NEW_TOTAL_AMOUNT,
		      			  FIELD_WORKSTATION_SPECIAL_ORDER_NEW_TOTAL_AMOUNT + " + " +
		      			  safeSQLCast(totals.getAmountSpecialOrderNew().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_SPECIAL_ORDER_PARTIAL_TOTAL_AMOUNT,
				  		  FIELD_WORKSTATION_SPECIAL_ORDER_PARTIAL_TOTAL_AMOUNT + " + " +
				  		  safeSQLCast(totals.getAmountSpecialOrderPartial().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TOTAL_ORDER_PAYMENTS_COLLECTED_COUNT,
                          FIELD_WORKSTATION_TOTAL_ORDER_PAYMENTS_COLLECTED_COUNT + " + " +
                          safeSQLCast(Integer.toString(totals.getCountOrderPayments())));
            sql.addColumn(FIELD_WORKSTATION_ORDER_PAYMENTS_COLLECTED_TOTAL_AMOUNT,
                          FIELD_WORKSTATION_ORDER_PAYMENTS_COLLECTED_TOTAL_AMOUNT + " + " +
                          safeSQLCast(totals.getAmountOrderPayments().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TOTAL_ORDER_CANCELS_DISBURSEMENT_COUNT,
                          FIELD_WORKSTATION_TOTAL_ORDER_CANCELS_DISBURSEMENT_COUNT + " + " +
                          safeSQLCast(Integer.toString(totals.getCountOrderCancels())));
            sql.addColumn(FIELD_WORKSTATION_ORDER_CANCELS_DISBURSEMENT_TOTAL_AMOUNT,
                          FIELD_WORKSTATION_ORDER_CANCELS_DISBURSEMENT_TOTAL_AMOUNT + " + " +
                          safeSQLCast(totals.getAmountOrderCancels().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_SHIPPING_CHARGE_TOTAL_AMOUNT,
                          FIELD_WORKSTATION_SHIPPING_CHARGE_TOTAL_AMOUNT + " + " +
                          safeSQLCast(totals.getAmountShippingCharges().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_TOTAL_SHIPPING_CHARGE_COUNT,
                          FIELD_WORKSTATION_TOTAL_SHIPPING_CHARGE_COUNT + " + " +
                          safeSQLCast(Integer.toString(totals.getNumberShippingCharges())));
            sql.addColumn(FIELD_WORKSTATION_SHIPPING_CHARGE_TAX_AMOUNT,
            			  FIELD_WORKSTATION_SHIPPING_CHARGE_TAX_AMOUNT + " + " +
						  safeSQLCast(totals.getAmountTaxShippingCharges().getStringValue()));
            sql.addColumn(FIELD_WORKSTATION_SHIPPING_CHARGE_INCLUSIVE_TAX_AMOUNT,
            			  FIELD_WORKSTATION_SHIPPING_CHARGE_INCLUSIVE_TAX_AMOUNT + " + " +
						  safeSQLCast(totals.getAmountInclusiveTaxShippingCharges().getStringValue()));
            
            sql.addColumn(FIELD_STORE_TOTAL_BILLPAYMENT,
                    FIELD_STORE_TOTAL_BILLPAYMENT + " + " +
                    safeSQLCast(totals.getAmountBillPayments().getStringValue()));
            
            sql.addColumn(FIELD_STORE_TOTAL_BILLPAYMENT_COUNT,
                    FIELD_STORE_TOTAL_BILLPAYMENT_COUNT + " + " +
                    safeSQLCast(Integer.toString(totals.getCountBillPayments())));
            
            sql.addColumn(FIELD_TOTAL_AMOUNT_CHANGE_ROUNDED_IN,
                    FIELD_TOTAL_AMOUNT_CHANGE_ROUNDED_IN + " + " +
                    safeSQLCast(totals.getAmountChangeRoundedIn().getStringValue()));
            
            sql.addColumn(FIELD_TOTAL_AMOUNT_CHANGE_ROUNDED_OUT,
                    FIELD_TOTAL_AMOUNT_CHANGE_ROUNDED_OUT + " + " +
                    safeSQLCast(totals.getAmountChangeRoundedOut().getStringValue()));
        }

        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_ITEM_SALES_AMOUNT,
                      FIELD_WORKSTATION_GROSS_TAXABLE_ITEM_SALES_AMOUNT + "+" + safeSQLCast(getTaxableItemSalesAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_ITEM_SALES_COUNT,
                      FIELD_WORKSTATION_GROSS_TAXABLE_ITEM_SALES_COUNT + "+" + safeSQLCast(getTaxableItemSalesCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_ITEM_RETURNS_AMOUNT,
                      FIELD_WORKSTATION_GROSS_TAXABLE_ITEM_RETURNS_AMOUNT + "+" + safeSQLCast(getTaxableItemReturnsAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_ITEM_RETURNS_COUNT,
                      FIELD_WORKSTATION_GROSS_TAXABLE_ITEM_RETURNS_COUNT + "+" + safeSQLCast(getTaxableItemReturnsCount(totals)));

        // Tax
        sql.addColumn(FIELD_WORKSTATION_TAX_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_TAX_TOTAL_AMOUNT + "+" + safeSQLCast(getNetTaxAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_INCLUSIVE_TAX_TOTAL_AMOUNT,
        		FIELD_WORKSTATION_INCLUSIVE_TAX_TOTAL_AMOUNT + "+" + safeSQLCast(getNetInclusiveTaxAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_REFUND_TAX_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_REFUND_TAX_TOTAL_AMOUNT + "+" + safeSQLCast(getTaxRefundedAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_REFUND_INCLUSIVE_TAX_TOTAL_AMOUNT,
        		FIELD_WORKSTATION_REFUND_INCLUSIVE_TAX_TOTAL_AMOUNT + "+" + safeSQLCast(getInclusiveTaxRefundedAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_RETURN_TAX_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_RETURN_TAX_TOTAL_AMOUNT + "+" + safeSQLCast(getTaxReturnedAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_RETURN_INCLUSIVE_TAX_TOTAL_AMOUNT,
        			  FIELD_WORKSTATION_RETURN_INCLUSIVE_TAX_TOTAL_AMOUNT + "+" + safeSQLCast(getInclusiveTaxReturnedAmount(totals)));
           sql.addColumn(FIELD_WORKSTATION_ITEM_SALES_TAX_AMOUNT,
                          FIELD_WORKSTATION_ITEM_SALES_TAX_AMOUNT + "+" + safeSQLCast(getItemTaxAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_ITEM_SALES_INCLUSIVE_TAX_AMOUNT,
        			  FIELD_WORKSTATION_ITEM_SALES_INCLUSIVE_TAX_AMOUNT + "+" + safeSQLCast(getItemSalesInclusiveTaxAmount(totals)));
        if (totals != null)
        {
            sql.addColumn(FIELD_WORKSTATION_TRANSACTION_SALES_TAX_AMOUNT,
                          FIELD_WORKSTATION_TRANSACTION_SALES_TAX_AMOUNT + "+" + safeSQLCast(totals.getAmountTaxTransactionSales().toString()));
            sql.addColumn(FIELD_WORKSTATION_TRANSACTION_SALES_INCLUSIVE_TAX_AMOUNT,
                          FIELD_WORKSTATION_TRANSACTION_SALES_INCLUSIVE_TAX_AMOUNT + "+" + safeSQLCast(totals.getAmountInclusiveTaxTransactionSales().toString()));
        }
        // Misc
            // StoreCouponDiscounts
        sql.addColumn(FIELD_WORKSTATION_ITEM_DISCOUNT_STORE_COUPON_AMOUNT,
                      FIELD_WORKSTATION_ITEM_DISCOUNT_STORE_COUPON_AMOUNT + "+" + safeSQLCast(getItemDiscStoreCouponAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_ITEM_DISCOUNT_STORE_COUPON_COUNT,
                      FIELD_WORKSTATION_ITEM_DISCOUNT_STORE_COUPON_COUNT + "+" + safeSQLCast(getItemDiscStoreCouponCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TRANSACTION_DISCOUNT_STORE_COUPON_AMOUNT,
                      FIELD_WORKSTATION_TRANSACTION_DISCOUNT_STORE_COUPON_AMOUNT + "+" + safeSQLCast(getTransactionDiscStoreCouponAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TRANSACTION_DISCOUNT_STORE_COUPON_COUNT,
                      FIELD_WORKSTATION_TRANSACTION_DISCOUNT_STORE_COUPON_COUNT + "+" + safeSQLCast(getTransactionDiscStoreCouponCount(totals)));
            //
        sql.addColumn(FIELD_WORKSTATION_MISCELLANEOUS_DISCOUNT_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_MISCELLANEOUS_DISCOUNT_TOTAL_AMOUNT + "+" + safeSQLCast(getTransactionDiscountAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_MISCELLANEOUS_DISCOUNT_COUNT,
                      FIELD_WORKSTATION_TOTAL_MISCELLANEOUS_DISCOUNT_COUNT + "+" + safeSQLCast(getTransactionDiscountCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_DISCOUNT_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_DISCOUNT_TOTAL_AMOUNT + "+" + safeSQLCast(getDiscountAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_DISCOUNT_COUNT,
                      FIELD_WORKSTATION_TOTAL_DISCOUNT_COUNT + "+" + safeSQLCast(getDiscountCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_MARKDOWN_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_MARKDOWN_TOTAL_AMOUNT + "+" + safeSQLCast(getMarkdownAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_MARKDOWN_COUNT,
                      FIELD_WORKSTATION_TOTAL_MARKDOWN_COUNT + "+" + safeSQLCast(getMarkdownCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_POST_TRANSACTION_VOID_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_POST_TRANSACTION_VOID_TOTAL_AMOUNT + "+" + safeSQLCast(getPostVoidAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_POST_TRANSACTION_VOID_COUNT,
                      FIELD_WORKSTATION_TOTAL_POST_TRANSACTION_VOID_COUNT + "+" + safeSQLCast(getPostVoidCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_LINE_ITEM_VOID_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_LINE_ITEM_VOID_TOTAL_AMOUNT + "+" + safeSQLCast(getLineVoidAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_LINE_ITEM_VOID_COUNT,
                      FIELD_WORKSTATION_TOTAL_LINE_ITEM_VOID_COUNT + "+" + safeSQLCast(getLineVoidCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TRANSACTION_VOID_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_TRANSACTION_VOID_TOTAL_AMOUNT + "+" + safeSQLCast(getVoidAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TRANSACTION_VOID_COUNT,
                      FIELD_WORKSTATION_TOTAL_TRANSACTION_VOID_COUNT + "+" + safeSQLCast(getVoidCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NO_SALE_TRANSACTION_COUNT,
                      FIELD_WORKSTATION_TOTAL_NO_SALE_TRANSACTION_COUNT + "+" + safeSQLCast(getNoSaleCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_PICKUP_COUNT,
                      FIELD_WORKSTATION_TOTAL_TENDER_PICKUP_COUNT + "+" + safeSQLCast(getTenderPickupCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_PICKUP_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_TENDER_PICKUP_TOTAL_AMOUNT + "+" + safeSQLCast(getTenderPickupAmount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_LOAN_COUNT,
                      FIELD_WORKSTATION_TOTAL_TENDER_LOAN_COUNT + "+" + safeSQLCast(getTenderLoanCount(totals)));
        sql.addColumn(FIELD_WORKSTATION_TENDER_LOAN_TOTAL_AMOUNT,
                      FIELD_WORKSTATION_TENDER_LOAN_TOTAL_AMOUNT + "+" + safeSQLCast(getTenderLoanAmount(totals)));
        sql.addColumn(FIELD_GROSS_TAXABLE_SALES_VOID_COUNT, getTaxableSalesVoidCount(totals));
        sql.addColumn(FIELD_GROSS_TAXABLE_RETURNS_VOID_COUNT, getTaxableReturnsVoidCount(totals));
        sql.addColumn(FIELD_GROSS_NONTAXABLE_SALES_VOID_COUNT, getNonTaxableSalesVoidCount(totals));
        sql.addColumn(FIELD_GROSS_NONTAXABLE_RETURNS_VOID_COUNT, getNonTaxableReturnsVoidCount(totals));
        // set timestamp
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
    }

    /**
        Returns a new instance of ReconcilableCountIfc.
        <p>
        @return a new instance of ReconcilableCountIfc
     */
    public ReconcilableCountIfc instantiateReconcilableCount()
    {
        return(new ReconcilableCount());
    }

    /**
        Returns a count with combined totals
        <p>
        @param  counts  An array of count to combine
        @return a combined count
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
        Returns the reporting period
        <p>
        @param  register  The register object
        @return the reporting period
     */
    protected EYSDate getReportingPeriod(RegisterIfc register)
    {
        /*
         * Use the business date for store level stuff
         */
        return(register.getBusinessDate());
    }

    /**
        Returns the store id
        <p>
        @param  register  A register
        @return the store id
     */
    protected String getStoreID(RegisterIfc register)
    {
        return("'" + register.getWorkstation().getStoreID() + "'");
    }

    /**
        Returns the workstation id
        <p>
        @param  register  A register
        @return the workstation id
     */
    protected String getWorkstationID(RegisterIfc register)
    {
        return("'" + register.getWorkstation().getWorkstationID() + "'");
    }

    /**
        Returns the transaction sequence number
        <p>
        @param  register  The register object
        @return the transaction sequence number
     */
    protected String getTransactionSequenceNumber(RegisterIfc register)
    {
        return(String.valueOf(register.getLastTransactionSequenceNumber()));
    }

    /**
        Returns the customer sequence number
        <p>
        @param  register  The register object
        @return the customer sequence number
     */
    protected String getCustomerSequenceNumber(RegisterIfc register)
    {
        return(String.valueOf(register.getLastCustomerSequenceNumber()));
    }

    /**
        Returns the register unique identifier extension
        <p>
        @param  register  The register object
        @return the unique identifier extension 4 chars
     */
    protected String getUniqueIdentifierExtension(RegisterIfc register)
    {
        return("'" + register.getCurrentUniqueID() + "'");
    }

    /**
        Returns the string representation of the start time
        <p>
        @param  register  A register
        @return the string representation of the start time
     */
    protected String getStartTimestamp(RegisterIfc register)
    {
        return(dateToSQLTimestampString(register.getOpenTime().dateValue()));
    }

    /**
        Returns the status of the register
        <p>
        @param  register  A register
        @return the status of the register
     */
    protected String getStatusCode(RegisterIfc register)
    {
        return(Integer.toString(register.getStatus()));
    }

    /**
        Returns the training mode of the register
        <p>
        @param  register  A register
        @return the training mode of the register
     */
    protected String getTrainingMode(RegisterIfc register)
    {
        String rc = "'0'";

        if (register.getWorkstation().isTrainingMode())
        {
            rc = "'1'";
        }

        return(rc);
    }

    /**
        Returns the value of the till reconciled flag
        <p>
        @param  register  A register
        @return the reconcile flag
     */
    protected String getTillReconciledFlag(RegisterIfc register)
    {
        String rc = "'0'";

        if (register.isTillReconcile())
        {
            rc = "'1'";
        }

        return(rc);
    }
    /**
        Returns the number of transactions
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of transactions
     */
    protected String getTransactionCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getTransactionCount());
        }
        return(value);
    }

    /**
        Returns the monetary amount of line item voids
        <p>
        @param  totals  The financial totals to extract the information from
        @return the monetary amount of line item voids
     */
    protected String getLineVoidAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountLineVoids().getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of line item voids
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of line item voids
     */
    protected String getLineVoidCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsLineVoids().toString();
        }
        return(value);
    }

    /**
        Returns the monetary amount of post transaction voids
        <p>
        @param  totals  The financial totals to extract the information from
        @return the monetary amount of post transaction voids
     */
    protected String getPostVoidAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountPostVoids().getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of post transaction voids
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of post transaction voids
     */
    protected String getPostVoidCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getNumberPostVoids());
        }
        return(value);
    }

    /**
        Returns the monetary amount of transaction voids
        <p>
        @param  totals  The financial totals to extract the information from
        @return the monetary amount of transaction voids
     */
    protected String getVoidAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountCancelledTransactions().getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of transaction voids
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of transaction voids
     */
    protected String getVoidCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getNumberCancelledTransactions());
        }
        return(value);
    }

    /**
        Returns the number of no sale transactions
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of no sale transactions
     */
    protected String getNoSaleCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getNumberNoSales());
        }
        return(value);
    }

    /**
        Returns the monetary amount of transaction discounts
        <p>
        @param  totals  The financial totals to extract the information from
        @return the monetary amount of transaction discounts
     */
    protected String getTransactionDiscountAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountTransactionDiscounts().getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of transaction discounts
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of transaction discounts
     */
    protected String getTransactionDiscountCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getNumberTransactionDiscounts());
        }
        return(value);
    }

    /**
        Returns the monetary amount of price discounts
        <p>
        @param  totals  The financial totals to extract the information from
        @return the monetary amount of price discount
     */
    protected String getDiscountAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountItemDiscounts().getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of price Discounts
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of price discounts
     */
    protected String getDiscountCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getNumberItemDiscounts());
        }
        return(value);
    }

    /**
        Returns the monetary amount of price markdowns
        <p>
        @param  totals  The financial totals to extract the information from
        @return the monetary amount of price markdowns
     */
    protected String getMarkdownAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountItemMarkdowns().getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of price markdowns
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of price markdowns
     */
    protected String getMarkdownCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getNumberItemMarkdowns());
        }
        return(value);
    }

    /**
        Returns the sales amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the sales amount
     */
    protected String getSalesAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            CurrencyIfc nonTaxable = totals.getAmountGrossNonTaxableTransactionSales()
                                           .subtract(totals.getAmountGrossNonTaxableTransactionSalesVoided());
            value = totals.getAmountGrossTaxableTransactionSales()
                          .subtract(totals.getAmountGrossTaxableTransactionSalesVoided())
                       .add(nonTaxable).getStringValue();
        }
        return(value);
    }

    /**
        Returns the net nontaxable amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the net nontaxable amount
     */
    protected String getNetNontaxableAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            CurrencyIfc refunds = totals.getAmountGrossNonTaxableTransactionReturns()
                                        .subtract(totals.getAmountGrossNonTaxableTransactionReturnsVoided())
                                        .subtract(totals.getNetAmountGiftCardItemRedeemed());
            value = totals.getAmountGrossNonTaxableTransactionSales()
                          .subtract(totals.getAmountGrossNonTaxableTransactionSalesVoided())
                       .subtract(refunds).getStringValue();
        }
        return(value);
    }

    /**
        Returns the nontaxable count
        <p>
        @param  totals  The financial totals to extract the information from
        @return the nontaxable count
     */
    protected String getNontaxableCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getCountGrossNonTaxableTransactionSales()
                                   + totals.getCountGrossNonTaxableTransactionSalesVoided()
                                   + totals.getCountGrossNonTaxableTransactionReturns()
                                   + totals.getCountGrossNonTaxableTransactionReturnsVoided());
        }
        return(value);
    }

    /**
        Returns the taxable transaction count
        <p>
        @param  totals  The financial totals to extract the information from
        @return the taxable count
     */
    protected String getTaxableCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getCountGrossTaxableTransactionSales()
                                   + totals.getCountGrossTaxableTransactionSalesVoided()
                                   + totals.getCountGrossTaxableTransactionReturns()
                                   + totals.getCountGrossTaxableTransactionReturnsVoided());
        }
        return(value);
    }

    /**
        Returns the net tax exempt amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the net tax exempt amount
     */
    protected String getNetTaxExemptAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            CurrencyIfc refunds = totals.getAmountGrossTaxExemptTransactionReturns()
                                        .subtract(totals.getAmountGrossTaxExemptTransactionReturnsVoided());
            value = totals.getAmountGrossTaxExemptTransactionSales()
                          .subtract(totals.getAmountGrossTaxExemptTransactionSalesVoided())
                       .subtract(refunds).getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of tax exempt transactions
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of tax exempt transactions
     */
    protected String getTaxExemptCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getCountGrossTaxExemptTransactionSales()
                                   - totals.getCountGrossTaxExemptTransactionSalesVoided()
                                   + totals.getCountGrossTaxExemptTransactionReturns()
                                   - totals.getCountGrossTaxExemptTransactionReturnsVoided());
        }
        return(value);
    }

    /**
        Returns the refund amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the refund amount
     */
    protected String getRefundAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            CurrencyIfc nonTaxable = totals.getAmountGrossNonTaxableTransactionReturns()
                                           .subtract(totals.getAmountGrossNonTaxableTransactionReturnsVoided());
            value = totals.getAmountGrossTaxableTransactionReturns()
                          .subtract(totals.getAmountGrossTaxableTransactionReturnsVoided())
                       .add(nonTaxable).getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of refunds
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of refunds
     */
    protected String getRefundCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getCountGrossTaxableTransactionReturns()
                                   - totals.getCountGrossTaxableTransactionReturnsVoided()
                                   + totals.getCountGrossNonTaxableTransactionReturns()
                                   - totals.getCountGrossNonTaxableTransactionReturnsVoided());
        }
        return(value);
    }

    /**
        Returns the nontaxable refund amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the nontaxable refund amount
     */
    protected String getRefundNontaxableAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountGrossNonTaxableTransactionReturns()
                          .subtract(totals.getAmountGrossNonTaxableTransactionReturnsVoided()).getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of nontaxable refunds
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of nontaxable refunds
     */
    protected String getRefundNontaxableCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getCountGrossNonTaxableTransactionReturns()
                                   - totals.getCountGrossNonTaxableTransactionReturnsVoided());
        }
        return(value);
    }

    /**
        Returns the tax exempt refund amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the tax exempt refund amount
     */
    protected String getRefundTaxExemptAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountGrossTaxExemptTransactionReturns()
                          .subtract(totals.getAmountGrossTaxExemptTransactionReturnsVoided()).getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of tax exempt refunds
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of tax exempt refunds
     */
    protected String getRefundTaxExemptCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getCountGrossTaxExemptTransactionReturns()
                                   - totals.getCountGrossTaxExemptTransactionReturnsVoided());
        }
        return(value);
    }

    /**
        Returns the item sales amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the item sales amount
     */
    protected String getItemSalesAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            CurrencyIfc nonTaxable = totals.getAmountGrossNonTaxableItemSales()
                                           .subtract(totals.getAmountGrossNonTaxableItemSalesVoided());
            value = totals.getAmountGrossTaxableItemSales()
                          .subtract(totals.getAmountGrossTaxableItemSalesVoided()).add(nonTaxable).getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of line items sold or returned
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of line items sold or returned
     */
    protected String getItemSalesCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsGrossTaxableItemSales()
                          .subtract(totals.getUnitsGrossTaxableItemSalesVoided())
                        .add(totals.getUnitsGrossNonTaxableItemSales()
                                   .subtract(totals.getUnitsGrossNonTaxableItemSalesVoided())).toString();
        }
        return(value);
    }

    /**
        Returns the nontaxable item sales amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the nontaxable item sales amount
     */
    protected String getItemNontaxableAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountGrossNonTaxableItemSales()
                          .subtract(totals.getAmountGrossNonTaxableItemSalesVoided()).getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of nontaxable line items sold
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of nontaxale line items sold
     */
    protected String getItemNontaxableCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsGrossNonTaxableItemSales()
                          .subtract(totals.getUnitsGrossNonTaxableItemSalesVoided()).toString();
        }
        return(value);
    }

    /**
        Returns the tax exempt item sales amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the tax exempt item sales amount
     */
    protected String getItemTaxExemptAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountGrossTaxExemptItemSales()
                          .subtract(totals.getAmountGrossTaxExemptItemSalesVoided()).getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of tax exempt line items sold
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of tax exempt line items sold
     */
    protected String getItemTaxExemptCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsGrossTaxExemptItemSales()
                          .subtract(totals.getUnitsGrossTaxExemptItemSalesVoided()).toString();
        }
        return(value);
    }

    /**
        Returns the return amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the return amount
     */
    protected String getReturnAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            CurrencyIfc nonTaxable = totals.getAmountGrossNonTaxableItemReturns()
                                           .subtract(totals.getAmountGrossNonTaxableItemReturnsVoided());
            value = totals.getAmountGrossTaxableItemReturns()
                          .subtract(totals.getAmountGrossTaxableItemReturnsVoided()).add(nonTaxable).getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of returns
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of returns
     */
    protected String getReturnCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsGrossTaxableItemReturns()
                          .subtract(totals.getUnitsGrossTaxableItemReturnsVoided())
                        .add(totals.getUnitsGrossNonTaxableItemReturns()
                                   .subtract(totals.getUnitsGrossNonTaxableItemReturnsVoided())).toString();
        }
        return(value);
    }

    /**
        Returns the nontaxable item return amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the nontaxable item return amount
     */
    protected String getReturnNontaxableAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountGrossNonTaxableItemReturns()
                          .subtract(totals.getAmountGrossNonTaxableItemReturnsVoided()).getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of nontaxable line items returned
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of nontaxale line items returned
     */
    protected String getReturnNontaxableCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsGrossNonTaxableItemReturns()
                          .subtract(totals.getUnitsGrossNonTaxableItemReturnsVoided()).toString();
        }
        return(value);
    }

    /**
        Returns the tax exempt item return amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the tax exempt item return amount
     */
    protected String getReturnTaxExemptAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountGrossTaxExemptItemReturns()
                          .subtract(totals.getAmountGrossTaxExemptItemReturnsVoided()).getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of tax exempt line items returned
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of tax exempt line items returned
     */
    protected String getReturnTaxExemptCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsGrossTaxExemptItemReturns()
                          .subtract(totals.getUnitsGrossTaxExemptItemReturnsVoided()).toString();
        }
        return(value);
    }

    /**
        Returns the string value of non merchandise non tax amount
        <p>
        @param totals container of value to be converted
        @return String the value as a string
     */
    protected String getNonMerchNonTaxAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountNetNonTaxableNonMerchandiseSales().getStringValue();
        }
        return(value);
    }

    /**
        Returns the string value of non merchandise non tax count
        <p>
        @param totals container of value to be converted
        @return String the value as a string
     */
    protected String getNonMerchNonTaxCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsNetNonTaxableNonMerchandiseSales().toString();
        }
        return(value);
    }

    /**
        Returns the string value of non merchandise non tax return amount
        <p>
        @param totals container of value to be converted
        @return String the value as a string
     */
    protected String getNonMerchNonTaxReturnAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountGrossNonTaxableNonMerchandiseReturns()
                          .subtract(totals.getAmountGrossNonTaxableNonMerchandiseReturnsVoided()).getStringValue();
        }
        return(value);
    }

    /**
        Returns the string value of non merchandise non tax return count
        <p>
        @param totals container of value to be converted
        @return String the value as a string
     */
    protected String getNonMerchNonTaxReturnCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsGrossNonTaxableNonMerchandiseReturns()
                          .subtract(totals.getUnitsGrossNonTaxableNonMerchandiseReturnsVoided()).toString();
        }
        return(value);
    }

    /**
        Returns the string value of non merchandise tax amount
        <p>
        @param totals container of value to be converted
        @return String the value as a string
     */
    protected String getNonMerchAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountNetTaxableNonMerchandiseSales().getStringValue();
        }
        return(value);
    }

    /**
        Returns the string value of non merchandise tax count
        <p>
        @param totals container of value to be converted
        @return String the value as a string
     */
    protected String getNonMerchCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsNetTaxableNonMerchandiseSales().toString();
        }
        return(value);
    }

    /**
        Returns the string value of non merchandise tax return amount
        <p>
        @param totals container of value to be converted
        @return String the value as a string
     */
    protected String getNonMerchReturnAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountGrossTaxableNonMerchandiseReturns()
                          .subtract(totals.getAmountGrossTaxableNonMerchandiseReturnsVoided()).getStringValue();
        }
        return(value);
    }

    /**
        Returns the string value of non merchandise tax return count
        <p>
        @param totals container of value to be converted
        @return String the value as a string
     */
    protected String getNonMerchReturnCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsGrossTaxableNonMerchandiseReturns()
                          .subtract(totals.getUnitsGrossTaxableNonMerchandiseReturnsVoided()).toString();
        }
        return(value);
    }

    /**
        Returns the string value of gift card amount
        <p>
        @param totals container of value to be converted
        @return String the value as a string
     */
    protected String getGiftCardAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountNetGiftCardItemSales().getStringValue();
        }
        return(value);
    }

    /**
        Returns the string value of gift card count
        <p>
        @param totals container of value to be converted
        @return String the value as a string
     */
    protected String getGiftCardCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsNetGiftCardItemSales().toString();
        }
        return(value);
    }
    /**
        Returns the string value of TransactionDiscStoreCoupon Amount
        <p>
        @param totals container of value to be converted
        @return String the value as a string
     */
    protected String getTransactionDiscStoreCouponAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountTransactionDiscStoreCoupons().getStringValue();
        }
        return(value);
    }

    /**
        Returns the string value of TransactionDiscStoreCoupon count
        <p>
        @param totals container of value to be converted
        @return String the value as a string
     */
    protected String getTransactionDiscStoreCouponCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getNumberTransactionDiscStoreCoupons());
        }
        return(value);
    }


    /**
        Returns the string value of ItemDiscStoreCoupon Amount
        <p>
        @param totals container of value to be converted
        @return String the value as a string
     */
    protected String getItemDiscStoreCouponAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountItemDiscStoreCoupons().getStringValue();
        }
        return(value);
    }

    /**
        Returns the string value of ItemDiscStoreCoupon count
        <p>
        @param totals container of value to be converted
        @return String the value as a string
     */
    protected String getItemDiscStoreCouponCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getNumberItemDiscStoreCoupons());
        }
        return(value);
    }

    /**
        Returns the string value of gift card return amount
        <p>
        @param totals container of value to be converted
        @return String the value as a string
     */
    protected String getGiftCardReturnAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountGrossGiftCardItemReturns()
                          .subtract(totals.getAmountGrossGiftCardItemReturnsVoided()).getStringValue();
        }
        return(value);
    }

    /**
        Returns the string value of gift card return count
        <p>
        @param totals container of value to be converted
        @return String the value as a string
     */
    protected String getGiftCardReturnCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsGrossGiftCardItemReturns()
                          .subtract(totals.getUnitsGrossGiftCardItemReturnsVoided()).toString();
        }
        return(value);
    }

    /**
        Returns the amount of house account payments
        <p>
        @param  totals  The financial totals to extract the information from
        @return the amount of house account payments
     */
    protected String getHousePaymentAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountHousePayments().getStringValue();
        }

        return(value);
    }

    /**
        Returns the number of house account payments
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of house account payments
     */
    protected String getHousePaymentCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getCountHousePayments());
        }

        return(value);
    }

    /**
        Returns the number of restocking fees
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of restocking fees
     */
    protected String getRestockingFeeCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsRestockingFees().toString();
        }

        return(value);
    }

    /**
        Returns the amount of restocking fees
        <p>
        @param  totals  The financial totals to extract the information from
        @return the amount of restocking fees
     */
    protected String getRestockingFeeAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountRestockingFees().getStringValue();
        }

        return(value);
    }

    /**
        Returns the number of restocking fees from non taxable items
        <p>
        @param totals  The financial totals to extract the information from
        @return the number of restocking fees
     */
    protected String getRestockingFeeFromNonTaxableCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsRestockingFeesFromNonTaxableItems().toString();
        }

        return(value);
    }

    /**
        Returns the amount of restocking fees  from non taxable items
        <p>
        @param totals  The financial totals to extract the information from
        @return the amount of restocking fees
     */
    protected String getRestockingFeeFromNonTaxableAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountRestockingFeesFromNonTaxableItems().getStringValue();
        }

        return(value);
    }

    /**
        Returns the net tax amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the net tax amount
     */
    protected String getNetTaxAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            CurrencyIfc taxTransactionReturns = totals.getAmountTaxTransactionReturns();
            value = totals.getAmountTaxTransactionSales().subtract(taxTransactionReturns).getStringValue();
        }
        return(value);
    }
    
    /**
        Returns the net inclusive tax amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the net inclusive tax amount
     */
    protected String getNetInclusiveTaxAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            CurrencyIfc taxTransactionReturns = totals.getAmountInclusiveTaxTransactionReturns();
            value = totals.getAmountInclusiveTaxTransactionSales().subtract(taxTransactionReturns).getStringValue();
        }
        return(value);
    }

    /**
        Returns the tax refunded amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the tax refunded amount
     */
    protected String getTaxRefundedAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountTaxTransactionReturns().getStringValue();
        }
        return(value);
    }
    
    /**
        Returns the inclusive tax refunded amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the inclusive tax refunded amount
     */
    protected String getInclusiveTaxRefundedAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountInclusiveTaxTransactionReturns().getStringValue();
        }
        return(value);
    }

    /**
        Returns the tax returned amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the tax returned amount
     */
    protected String getTaxReturnedAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountTaxItemReturns().getStringValue();
        }
        return(value);
    }
    
    /**
        Returns the inclusive tax returned amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the inclusive tax returned amount
     */
    protected String getInclusiveTaxReturnedAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountInclusiveTaxItemReturns().getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of tender pickups
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of tender pickups
     */
    protected String getTenderPickupCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getCountTillPickups());
        }
        return(value);
    }

    /**
        Returns the amount of tender pickups
        <p>
        @param  totals  The financial totals to extract the information from
        @return the amount of tender pickups
     */
    protected String getTenderPickupAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountTillPickups().getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of till payOuts
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of till payOuts
     */
    protected String getTillPayOutCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getCountTillPayOuts());
        }
        return(value);
    }

    /**
        Returns the amount of till payOuts
        <p>
        @param  totals  The financial totals to extract the information from
        @return the amount of till payOuts
     */
    protected String getTillPayOutAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountTillPayOuts().getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of till payIns
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of till payIns
     */
    protected String getTillPayInCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getCountTillPayIns());
        }
        return(value);
    }

    /**
        Returns the amount of till payIns
        <p>
        @param  totals  The financial totals to extract the information from
        @return the amount of till payIns
     */
    protected String getTillPayInAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountTillPayIns().getStringValue();
        }
        return(value);
    }

    /**
        Returns the number of tender loans
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of tender loans
     */
    protected String getTenderLoanCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getCountTillLoans());
        }
        return(value);
    }

    /**
        Returns the amount of tender loans
        <p>
        @param  totals  The financial totals to extract the information from
        @return the amount of tender loans
     */
    protected String getTenderLoanAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountTillLoans().getStringValue();
        }
        return(value);
    }

    /**
        Returns the tender type
        <p>
        @param  tenderType  The type of tender
        @return the tender type
     */
    protected String getTenderType(String tenderType)
    {
        String value = tenderTypeMap.getCode(tenderTypeMap.getTypeFromDescriptor(tenderType));

        return("'" + value + "'");
    }

    /**
        Returns the tender deposit amount
        <p>
        @param  register    The register
        @param  tenderType  The type of tender
        @return the tender deposit amount
     */
    protected String getTenderDepositAmount(RegisterIfc register, String tenderType)
    {
        // Not tracking this yet.
        return("0");
    }

    /**
     Returns the tender pay-in amount
     <p>
     @param  payIns       The till payIns
     @param  tenderType  The type of tender
     @return the till payIn amount
     */
    protected String getTillPayInAmount(ReconcilableCountIfc payIns, TenderDescriptorIfc tenderType)
    {
        String value = "0";

        FinancialCountIfc count = payIns.getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = item.getAmountIn().subtract(item.getAmountOut()).getStringValue();
        }

        return(value);
    }

    /**
     Returns the tender loan amount
     <p>
     @param  loans       The till loans
     @param  tenderType  The type of tender
     @return the tender loan amount
     */
    protected String getTenderLoanAmount(ReconcilableCountIfc loans, TenderDescriptorIfc tenderType)
    {
        String value = "0";

        FinancialCountIfc count = loans.getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = item.getAmountIn().subtract(item.getAmountOut()).getStringValue();
        }

        return(value);
    }

    /**
     Returns the tender over amount
     <p>
     @param  register    The register
     @param  tenderType  The type of tender
     @return the tender over amount
     */
    protected String getTenderOverAmount(RegisterIfc register, TenderDescriptorIfc tenderType)
    {
        FinancialCountIfc expectedCount = register.getTotals().getCombinedCount().getExpected();
        FinancialCountTenderItemIfc expectedTender = expectedCount.getSummaryTenderItemByDescriptor(tenderType);

        FinancialCountIfc enteredCount = register.getTotals().getCombinedCount().getEntered();
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
            else // both are null
            {
                amount = zero;
            }
        }
        else // expectedTender == null
        {
            amount = enteredTender.getAmountTotal();
        }

        if (amount.compareTo(zero) == CurrencyIfc.LESS_THAN)
        {
            amount = zero;
        }

        return(amount.getStringValue());

    }

    /**
        Returns the tender pickup amount
        <p>
        @param  pickups     The till pickups
        @param  tenderType  The type of tender
        @return the tender pickup amount
     */
    protected String getTenderPickupAmount(ReconcilableCountIfc pickups, TenderDescriptorIfc tenderType)
    {
        String value = "0";

        FinancialCountIfc count = pickups.getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = item.getAmountOut().subtract(item.getAmountIn()).getStringValue();
        }

        return(value);
    }

    /**
     Returns the till payOuts amount
     <p>
     @param  payOuts     The till payOuts
     @param  tenderType  The type of tender
     @return the till payOuts amount
     */
    protected String getTillPayOutAmount(ReconcilableCountIfc payOuts, TenderDescriptorIfc tenderType)
    {
        String value = "0";

        FinancialCountIfc count = payOuts.getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = item.getAmountOut().subtract(item.getAmountIn()).getStringValue();
        }

        return(value);
    }

    /**
     Returns the tender short amount
     <p>
     @param  register    The register
     @param  tenderType  The type of tender
     @return the tender short amount
     */
    protected String getTenderShortAmount(RegisterIfc register, TenderDescriptorIfc tenderType)
    {
        FinancialCountIfc expectedCount = register.getTotals().getCombinedCount().getExpected();
        FinancialCountTenderItemIfc expectedTender = expectedCount.getSummaryTenderItemByDescriptor(tenderType);
        FinancialCountIfc enteredCount = register.getTotals().getCombinedCount().getEntered();
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
            else // both are null
            {
                amount = zero;
            }
        }
        else // expectedTender == null
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

        return(amount.getStringValue());

    }

    /**
     Returns the tender beginning count
     <p>
     @param  register    The register
     @param  tenderType  The type of tender
     @return the tender beginning count
     */
    protected String getTenderBeginningCount(RegisterIfc register, TenderDescriptorIfc tenderType)
    {
        String value = "0";

        FinancialCountIfc count = register.getTotals().getStartingFloatCount().getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = String.valueOf(item.getNumberItemsIn());
        }

        return(value);
    }

    /**
        Returns the tender deposit count
        <p>
        @param  register    The register
        @param  tenderType  The type of tender
        @return the tender deposit count
     */
    protected String getTenderDepositCount(RegisterIfc register, String tenderType)
    {
        // Not tracking this yet
        return("0");
    }

    /**
     Returns the tender loan count
     <p>
     @param  loans       The till loans
     @param  tenderType  The type of tender
     @return the tender loan count
     */
    protected String getTenderLoanCount(ReconcilableCountIfc loans, TenderDescriptorIfc tenderType)
    {
        String value = "0";

        FinancialCountIfc count = loans.getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = String.valueOf(item.getNumberItemsIn() - item.getNumberItemsOut());
        }

        return(value);
    }

    /**
     Returns the till payIn count
     <p>
     @param  payIns       The till payIns
     @param  tenderType  The type of tender
     @return the tender payIn count
     */
    protected String getTillPayInCount(ReconcilableCountIfc payIns, TenderDescriptorIfc tenderType)
    {
        String value = "0";

        FinancialCountIfc count = payIns.getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = String.valueOf(item.getNumberItemsIn() - item.getNumberItemsOut());
        }

        return(value);
    }

    /**
     Returns the tender count
     <p>
     @param  register    The register
     @param  tenderType  The type of tender
     @return the tender count
     */
    protected String getTenderCount(RegisterIfc register, TenderDescriptorIfc tenderType)
    {
        String value = "0";

        FinancialCountIfc count = register.getTotals().getTenderCount();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = String.valueOf(item.getNumberItemsTotal());
        }

        return(value);
    }

    /**
     Returns the tender over count
     <p>
     @param  register    The register
     @param  tenderType  The type of tender
     @return the tender over count
     */
    protected String getTenderOverCount(RegisterIfc register, TenderDescriptorIfc tenderType)
    {
        FinancialCountIfc expectedCount = register.getTotals().getCombinedCount().getExpected();
        FinancialCountTenderItemIfc expectedTender = expectedCount.getSummaryTenderItemByDescriptor(tenderType);

        FinancialCountIfc enteredCount = register.getTotals().getCombinedCount().getEntered();
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
                count = - expectedTender.getNumberItemsTotal();
            }
            else // both are null
            {
                count = 0;
            }
        }
        else // expectedTender == null
        {
            count = enteredTender.getNumberItemsTotal();
        }

        if (count < 0)
        {
            count = 0;
        }

        return(String.valueOf(count));
    }

    /**
     Returns the tender short count
     <p>
     @param  register    The register
     @param  tenderType  The type of tender
     @return the tender short count
     */
    protected String getTenderShortCount(RegisterIfc register, TenderDescriptorIfc tenderType)
    {
        FinancialCountIfc expectedCount = register.getTotals().getCombinedCount().getExpected();
        FinancialCountTenderItemIfc expectedTender = expectedCount.getSummaryTenderItemByDescriptor(tenderType);

        FinancialCountIfc enteredCount = register.getTotals().getCombinedCount().getEntered();
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
                count = - expectedTender.getNumberItemsTotal();
            }
            else // both are null
            {
                count = 0;
            }
        }
        else // expectedTender == null
        {
            count = enteredTender.getNumberItemsTotal();
        }

        if (count < 0)
        {
            count = - count;
        }
        else
        {
            count = 0;
        }

        return(String.valueOf(count));
    }

    /**
     Returns the tender pickup count
     <p>
     @param  pickups     The till pickups
     @param  tenderType  The type of tender
     @return the tender pickup count
     */
    protected String getTenderPickupCount(ReconcilableCountIfc pickups, TenderDescriptorIfc tenderType)
    {
        String value = "0";

        FinancialCountIfc count = pickups.getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = String.valueOf(item.getNumberItemsOut() - item.getNumberItemsIn());
        }

        return(value);
    }

    /**
     Returns the till pay-out count
     <p>
     @param  payOuts     The till pay-outs
     @param  tenderType  The type of tender
     @return the tender pay-out count
     */
    protected String getTillPayOutCount(ReconcilableCountIfc payOuts, TenderDescriptorIfc tenderType)
    {
        String value = "0";

        FinancialCountIfc count = payOuts.getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = String.valueOf(item.getNumberItemsOut() - item.getNumberItemsIn());
        }

        return(value);
    }

    /**
     Returns the tender refund count
     <p>
     @param  register    The register
     @param  tenderType  The type of tender
     @return the tender refund count
     */
    protected String getTenderRefundCount(RegisterIfc register, TenderDescriptorIfc tenderType)
    {
        String value = "0";

        FinancialCountIfc count = register.getTotals().getTenderCount();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = String.valueOf(item.getNumberItemsOut());
        }

        return(value);
    }

    /**
     Returns the tender refund amount
     <p>
     @param  register    The register
     @param  tenderType  The type of tender
     @return the tender refund amount
     */
    protected String getTenderRefundAmount(RegisterIfc register, TenderDescriptorIfc tenderType)
    {
        String value = "0";

        FinancialCountIfc count = register.getTotals().getTenderCount();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = item.getAmountOut().getStringValue();
        }

        return(value);
    }

    /**
     Returns the tender total amount
     <p>
     @param  register    The register
     @param  tenderType  The type of tender
     @return the tender total amount
     */
    protected String getTenderTotalAmount(RegisterIfc register, TenderDescriptorIfc tenderType)
    {
        String value = "0";

        FinancialCountIfc count = register.getTotals().getTenderCount();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = item.getAmountTotal().getStringValue();
        }

        return(value);
    }

    /**
     Returns the tender open amount
     <p>
     @param  register    The register
     @param  tenderType  The type of tender
     @return the tender open amount
     */
    protected String getTenderOpenAmount(RegisterIfc register, TenderDescriptorIfc tenderType)
    {
        String value = "0";

        FinancialCountIfc count = register.getTotals().getStartingFloatCount().getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = item.getAmountIn().getStringValue();
        }

        return(value);
    }

    /**
     Returns the tender close amount
     <p>
     @param  register    The register
     @param  tenderType  The type of tender
     @return the tender close amount
     */
    protected String getTenderCloseAmount(RegisterIfc register, TenderDescriptorIfc tenderType)
    {
        String value = "0";

        FinancialCountIfc count = register.getTotals().getEndingFloatCount().getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = item.getAmountOut().getStringValue();
        }

        return(value);
    }

    /**
     Returns the tender close count
     <p>
     @param  register    The register
     @param  tenderType  The type of tender
     @return the tender close count
     */
    protected String getTenderCloseCount(RegisterIfc register, TenderDescriptorIfc tenderType)
    {
        String value = "0";

        FinancialCountIfc count = register.getTotals().getEndingFloatCount().getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderType);

        if (item != null)
        {
            value = String.valueOf(item.getNumberItemsOut());
        }

        return(value);
    }

    /**
        Returns the taxable item sales amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the taxable item sales amount
     */
    protected String getTaxableItemSalesAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountGrossTaxableItemSales()
                          .subtract(totals.getAmountGrossTaxableItemSalesVoided()).toString();
        }
        return(value);
    }

    /**
        Returns the taxable item returns amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the taxable item returns amount
     */
    protected String getTaxableItemReturnsAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountGrossTaxableItemReturns()
                          .subtract(totals.getAmountGrossTaxableItemReturnsVoided()).toString();
        }
        return(value);
    }

    /**
        Returns the number of taxable line items sold
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of taxable line items sold
     */
    protected String getTaxableItemSalesCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsGrossTaxableItemSales()
                          .subtract(totals.getUnitsGrossTaxableItemSalesVoided()).toString();
        }
        return value;
    }

    /**
        Returns the number of taxable line items returned
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of taxable line items returned
     */
    protected String getTaxableItemReturnsCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsGrossTaxableItemReturns()
                          .subtract(totals.getUnitsGrossTaxableItemReturnsVoided()).toString();
        }
        return value;
    }

    /**
        Returns the taxable transaction sales amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the taxable transaction sales amount
     */
    protected String getTaxableTransactionSalesAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountGrossTaxableTransactionSales()
                          .subtract(totals.getAmountGrossTaxableTransactionSalesVoided()).toString();
        }
        return(value);
    }

    /**
        Returns the number of taxable transaction sales
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of taxable transaction sales
     */
    protected String getTaxableTransactionSalesCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getCountGrossTaxableTransactionSales() -
                          totals.getCountGrossTaxableTransactionSalesVoided());
        }
        return(value);
    }

    /**
        Returns the gift card item sales amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the gift card item sales amount
     */
    protected String getGiftCardItemSalesAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountGrossGiftCardItemSales()
                          .subtract(totals.getAmountGrossGiftCardItemSalesVoided()).toString();
        }
        return(value);
    }

    /**
        Returns the numbers of gift card item sales
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of gift card item sales
     */
    protected String getGiftCardItemSalesCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsGrossGiftCardItemSales()
                          .subtract(totals.getUnitsGrossGiftCardItemSalesVoided()).toString();
        }
        return(value);
    }

    /**
        Returns the taxable non merchandise sales amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the taxable non merchandise sales amount
     */
    protected String getTaxableNonMerchandiseSalesAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountGrossTaxableNonMerchandiseSales()
                          .subtract(totals.getAmountGrossTaxableNonMerchandiseSalesVoided()).toString();
        }
        return(value);
    }

         /**
        Returns the numbers of taxable non merchandise sales
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of taxable non merchandise sales
     */
    protected String getTaxableNonMerchandiseSalesCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsGrossTaxableNonMerchandiseSales()
                          .subtract(totals.getUnitsGrossTaxableNonMerchandiseSalesVoided()).toString();
        }
        return(value);
    }

    /**
        Returns the taxable transaction returns amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the taxable transaction returns amount
     */
    protected String getTaxableTransactionReturnsAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountGrossTaxableTransactionReturns()
                          .subtract(totals.getAmountGrossTaxableTransactionReturnsVoided()).toString();
        }
        return(value);
    }

    /**
        Returns the number of taxable transaction returned
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of taxable transaction returned
     */
    protected String getTaxableTransactionReturnsCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getCountGrossTaxableTransactionReturns() -
                                   totals.getCountGrossTaxableTransactionReturnsVoided());
        }
        return value;
    }

    /**
        Returns the non taxable non merchandise sales amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the non taxable non merchandise sales amount
     */
    protected String getNonTaxableNonMerchandiseSalesAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountGrossNonTaxableNonMerchandiseSales()
                          .subtract(totals.getAmountGrossNonTaxableNonMerchandiseSalesVoided()).toString();
        }
        return(value);
    }

    /**
        Returns the number of non taxable non merchandise sales
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of non taxable non merchandise sales
     */
    protected String getNonTaxableNonMerchandiseSalesCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsGrossNonTaxableNonMerchandiseSales()
                    .subtract(totals.getUnitsGrossNonTaxableNonMerchandiseSalesVoided()).toString();
        }
        return value;
    }

    /**
        Returns the non taxable transaction sales amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the non taxable transaction sales amount
     */
    protected String getNonTaxableTransactionSalesAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountGrossNonTaxableTransactionSales()
                          .subtract(totals.getAmountGrossNonTaxableTransactionSalesVoided()).toString();
        }
        return(value);
    }

    /**
        Returns the number of non taxable transaction sales
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of non taxable tranaction sales
     */
    protected String getNonTaxableTransactionSalesCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getCountGrossNonTaxableTransactionSales() -
                                   totals.getCountGrossNonTaxableTransactionSalesVoided());
        }
        return value;
    }

    /**
        Returns the tax exempt transaction sales amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the tax exempt transaction sales amount
     */
    protected String getTaxExemptTransactionSalesAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountGrossTaxExemptTransactionSales()
                          .subtract(totals.getAmountGrossTaxExemptTransactionSalesVoided()).toString();
        }
        return(value);
    }

    /**
        Returns the number of tax exempt transaction sales
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of tax exempt tranaction sales
     */
    protected String getTaxExemptTransactionSalesCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getCountGrossTaxExemptTransactionSales() -
                                   totals.getCountGrossTaxExemptTransactionSalesVoided());
        }
        return value;
    }

    /**
        Returns the amount of item sales tax
        <p>
        @param  totals  The financial totals to extract the information from
        @return the amount of item sales tax
     */
    protected String getItemTaxAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountTaxItemSales()
                          .subtract(totals.getAmountTaxItemReturns()).toString();
        }
        return value;
    }
    
    /**
        Returns the amount of item sales inclusive tax
        <p>
        @param  totals  The financial totals to extract the information from
        @return the amount of item sales inclusive tax
     */
    protected String getItemSalesInclusiveTaxAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountInclusiveTaxItemSales().toString();
        }
        return value;
    }

    /**
        Returns the number of taxable sales voids
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of taxable sales void
     */
    protected String getTaxableSalesVoidCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getCountGrossTaxableTransactionSalesVoided());
        }

        if (isUpdateStatement)
        {
            value = FIELD_GROSS_TAXABLE_SALES_VOID_COUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
        Returns the number of taxable returns voids
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of taxable returns void
     */
    protected String getTaxableReturnsVoidCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getCountGrossTaxableTransactionReturnsVoided());
        }

        if (isUpdateStatement)
        {
            value = FIELD_GROSS_TAXABLE_RETURNS_VOID_COUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
        Returns the number of non-taxable sales voids
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of non-taxable sales void
     */
    protected String getNonTaxableSalesVoidCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getCountGrossNonTaxableTransactionSalesVoided());
        }

        if (isUpdateStatement)
        {
            value = FIELD_GROSS_NONTAXABLE_SALES_VOID_COUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
        Returns the number of non-taxable returns voids
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of non-taxable returns void
     */
    protected String getNonTaxableReturnsVoidCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getCountGrossNonTaxableTransactionReturnsVoided());
        }

        if (isUpdateStatement)
        {
            value = FIELD_GROSS_NONTAXABLE_RETURNS_VOID_COUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }
    /**
     Returns the string value of gift certificate issued amount
     <p>
     @param totals container of value to be converted
     @return String the value as a string
     */
    protected String getGiftCertificateIssuedAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
           {
            value = totals.getAmountGrossGiftCertificateIssued().getStringValue();
           }
        
        return(value);
    }
    /**
     Returns the string value of gift certificate issued count
     <p>
     @param totals container of value to be converted
     @return String the value as a string
     */
    protected String getGiftCertificateIssuedCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
           {
            value = totals.getUnitsNetGiftCertificateIssued().toString();
        }
        return(value);
    }
    /**
     Returns the string value of gift card issued amount
     <p>
     @param  totals FinancialTotalsIfc container of value to be converted
     @return String the value as a string
     */
    protected String getGiftCardIssuedAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
           {
            value = totals.getAmountGrossGiftCardItemIssued().getStringValue();
        }
        return(value);
    }
    /**
     Returns the string value of gift card issued count
     <p>
     @param  totals FinancialTotalsIfc container of value to be converted
     @return String the value as a string
     */
    protected String getGiftCardIssuedCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
           {
            value = totals.getUnitsGrossGiftCardItemIssued().toString();
        }
        return(value);
    }
    /**
     Returns the string value of gift card issued amount
     <p>
     @param totals FinancialTotalsIfc container of value to be converted
     @return String the value as a string
     */
    protected String getGiftCardReloadedAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
           {
            value = totals.getAmountGrossGiftCardItemReloaded().getStringValue();
        }
        return(value);
    }
    /**
     Returns the string value of gift card issued count
     <p>
     @param totals FinancialTotalsIfc container of value to be converted
     @return String the value as a string
     */
    protected String getGiftCardReloadedCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
           {
            value = totals.getUnitsGrossGiftCardItemReloaded().toString();
        }
        return(value);
    }
    /**
     Returns the string value of gift card issued amount
     <p>
     @param totals FinancialTotalsIfc container of value to be converted
     @return String the value as a string
     */
    protected String getGiftCardRedeemedAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
           {
            value = totals.getAmountGrossGiftCardItemRedeemed().getStringValue();
        }
        return(value);
    }
    /**
     Returns the string value of gift card issued count
     <p>
     @param totals FinancialTotalsIfc container of value to be converted
     @return String the value as a string
     */
    protected String getGiftCardRedeemedCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
           {
            value = totals.getUnitsGrossGiftCardItemRedeemed().toString();
        }
        return(value);
    }
    /**
     Returns the string value of gift card issue voided amount
     <p>
     @param  totals FinancialTotalsIfc container of value to be converted
     @return String the value as a string
     */
    protected String getGiftCardIssueVoidedAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
           {
            value = totals.getAmountGrossGiftCardItemIssueVoided().getStringValue();
        }
        return(value);
    }
    /**
     Returns the string value of gift card issue voided count
     <p>
     @param totals FinancialTotalsIfc container of value to be converted
     @return String the value as a string
     */
    protected String getGiftCardIssueVoidedCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
           {
            value = totals.getUnitsGrossGiftCardItemIssueVoided().toString();
        }
        return(value);
    }
    /**
     Returns the string value of gift card reload voided amount
     <p>
     @param totals FinancialTotalsIfc container of value to be converted
     @return String the value as a string
     */
    protected String getGiftCardReloadVoidedAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
           {
            value = totals.getAmountGrossGiftCardItemReloadVoided().getStringValue();
        }
        return(value);
    }
    /**
     Returns the string value of gift card reload voided count
     <p>
     @param totals FinancialTotalsIfc container of value to be converted
     @return String the value as a string
     */
    protected String getGiftCardReloadVoidedCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
           {
            value = totals.getUnitsGrossGiftCardItemReloadVoided().toString();
        }
        return(value);
    }
    /**
     Returns the string value of gift card redeem voided amount
     <p>
     @param totals FinancialTotalsIfc container of value to be converted
     @return String the value as a string
     */
    protected String getGiftCardRedeemVoidedAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
           {
            value = totals.getAmountGrossGiftCardItemRedeemedVoided().getStringValue();
        }
        return(value);
    }
    /**
     Returns the string value of gift card redeem voided count
     <p>
     @param totals FinancialTotalsIfc container of value to be converted
     @return String the value as a string
     */
    protected String getGiftCardRedeemVoidedCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
           {
            value = totals.getUnitsGrossGiftCardItemRedeemed().toString();
        }
        return(value);
    }


    /**
     * Get the number of house card enrollment requests that were approved
     *
     *  @param totals Totals object containing the data
     *  @return number of approved requests, as a string
     */
    protected String getHouseCardEnrollmentsApprovalCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = String.valueOf(totals.getHouseCardEnrollmentsApproved());
        }
        return value;
    }
    /**
     * Get the number of house card enrollment requests that were declined
     *
     *  @param totals Totals object containing the data
     *  @return number of declined requests, as a string
     */
    protected String getHouseCardEnrollmentsDeclinedCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = String.valueOf(totals.getHouseCardEnrollmentsDeclined());
        }
        return value;
    }

    /**
     * Get the AmountGrossGiftCardItemCredit as a String
     *
     * @param totals Totals object containing the data
     * @return AmountGrossGiftCardItemCredit converted to a String
     */
    protected String getAmountGrossGiftCardItemCredit(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getAmountGrossGiftCardItemCredit().toString();
        }
        return value;
    }

    /**
     * Get the UnitsGrossGiftCardItemCredit as a String
     *
     * @param totals Totals object containing the data
     * @return UnitsGrossGiftCardItemCredit converted to a String
     */
    protected String getUnitsGrossGiftCardItemCredit(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getUnitsGrossGiftCardItemCredit().toString();
        }
        return value;
    }
    /**
     * Get the AmountGrossGiftCardItemCreditVoided as a String
     *
     * @param totals Totals object containing the data
     * @return AmountGrossGiftCardItemCreditVoided converted to a String
     */
    protected String getAmountGrossGiftCardItemCreditVoided(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getAmountGrossGiftCardItemCreditVoided().toString();
        }
        return value;
    }

    /**
     * Get the UnitsGrossGiftCardItemCreditVoided as a String
     *
     * @param totals Totals object containing the data
     * @return UnitsGrossGiftCardItemCreditVoided converted to a String
     */
    protected String getUnitsGrossGiftCardItemCreditVoided(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getUnitsGrossGiftCardItemCreditVoided().toString();
        }
        return value;
    }
    /**
     * Get the AmountGrossGiftCertificatesRedeemed as a String
     *
     * @param totals Totals object containing the data
     * @return AmountGrossGiftCertificatesRedeemed converted to a String
     */
    protected String getAmountGrossGiftCertificatesRedeemed(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getAmountGrossGiftCertificatesRedeemed().toString();
        }
        return value;
    }

    /**
     * Get the UnitsGrossGiftCertificatesRedeemed as a String
     *
     * @param totals Totals object containing the data
     * @return UnitsGrossGiftCertificatesRedeemed converted to a String
     */
    protected String getUnitsGrossGiftCertificatesRedeemed(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getUnitsGrossGiftCertificatesRedeemed().toString();
        }
        return value;
    }
    /**
     * Get the AmountGrossGiftCertificatesRedeemedVoided as a String
     *
     * @param totals Totals object containing the data
     * @return AmountGrossGiftCertificatesRedeemedVoided converted to a String
     */
    protected String getAmountGrossGiftCertificatesRedeemedVoided(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getAmountGrossGiftCertificatesRedeemedVoided().toString();
        }
        return value;
    }

    /**
     * Get the UnitsGrossGiftCertificatesRedeemedVoided as a String
     *
     * @param totals Totals object containing the data
     * @return UnitsGrossGiftCertificatesRedeemedVoided converted to a String
     */
    protected String getUnitsGrossGiftCertificatesRedeemedVoided(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getUnitsGrossGiftCertificatesRedeemedVoided().toString();
        }
        return value;
    }
    /**
     * Get the AmountGrossStoreCreditsIssued as a String
     *
     * @param totals Totals object containing the data
     * @return AmountGrossStoreCreditsIssued converted to a String
     */
    protected String getAmountGrossStoreCreditsIssued(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getAmountGrossStoreCreditsIssued().toString();
        }
        return value;
    }

    /**
     * Get the UnitsGrossStoreCreditsIssued as a String
     *
     * @param totals Totals object containing the data
     * @return UnitsGrossStoreCreditsIssued converted to a String
     */
    protected String getUnitsGrossStoreCreditsIssued(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getUnitsGrossStoreCreditsIssued().toString();
        }
        return value;
    }
    /**
     * Get the AmountGrossStoreCreditsIssuedVoided as a String
     *
     * @param totals Totals object containing the data
     * @return AmountGrossStoreCreditsIssuedVoided converted to a String
     */
    protected String getAmountGrossStoreCreditsIssuedVoided(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getAmountGrossStoreCreditsIssuedVoided().toString();
        }
        return value;
    }

    /**
     * Get the UnitsGrossStoreCreditsIssuedVoided as a String
     *
     * @param totals Totals object containing the data
     * @return UnitsGrossStoreCreditsIssuedVoided converted to a String
     */
    protected String getUnitsGrossStoreCreditsIssuedVoided(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getUnitsGrossStoreCreditsIssuedVoided().toString();
        }
        return value;
    }

    /**
     * Get the AmountGrossStoreCreditsRedeemed as a String
     *
     * @param totals Totals object containing the data
     * @return AmountGrossStoreCreditsRedeemed converted to a String
     */
    protected String getAmountGrossStoreCreditsRedeemed(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getAmountGrossStoreCreditsRedeemed().toString();
        }
        return value;
    }

    /**
     * Get the UnitsGrossStoreCreditsRedeemed as a String
     *
     * @param totals Totals object containing the data
     * @return UnitsGrossStoreCreditsRedeemed converted to a String
     */
    protected String getUnitsGrossStoreCreditsRedeemed(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getUnitsGrossStoreCreditsRedeemed().toString();
        }
        return value;
    }
    /**
     * Get the AmountGrossStoreCreditsRedeemedVoided as a String
     *
     * @param totals Totals object containing the data
     * @return AmountGrossStoreCreditsRedeemedVoided converted to a String
     */
    protected String getAmountGrossStoreCreditsRedeemedVoided(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getAmountGrossStoreCreditsRedeemedVoided().toString();
        }
        return value;
    }

    /**
     * Get the UnitsGrossStoreCreditsRedeemedVoided as a String
     *
     * @param totals Totals object containing the data
     * @return UnitsGrossStoreCreditsRedeemedVoided converted to a String
     */
    protected String getUnitsGrossStoreCreditsRedeemedVoided(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getUnitsGrossStoreCreditsRedeemedVoided().toString();
        }
        return value;
    }
    /**
     * Get the AmountGrossItemEmployeeDiscount as a String
     *
     * @param totals Totals object containing the data
     * @return AmountGrossItemEmployeeDiscount converted to a String
     */
    protected String getAmountGrossItemEmployeeDiscount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getAmountGrossItemEmployeeDiscount().toString();
        }
        return value;
    }

    /**
     * Get the UnitsGrossItemEmployeeDiscount as a String
     *
     * @param totals Totals object containing the data
     * @return UnitsGrossItemEmployeeDiscount converted to a String
     */
    protected String getUnitsGrossItemEmployeeDiscount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getUnitsGrossItemEmployeeDiscount().toString();
        }
        return value;
    }
    /**
     * Get the AmountGrossItemEmployeeDiscountVoided as a String
     *
     * @param totals Totals object containing the data
     * @return AmountGrossItemEmployeeDiscountVoided converted to a String
     */
    protected String getAmountGrossItemEmployeeDiscountVoided(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getAmountGrossItemEmployeeDiscountVoided().toString();
        }
        return value;
    }

    /**
     * Get the UnitsGrossItemEmployeeDiscountVoided as a String
     *
     * @param totals Totals object containing the data
     * @return UnitsGrossItemEmployeeDiscountVoided converted to a String
     */
    protected String getUnitsGrossItemEmployeeDiscountVoided(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getUnitsGrossItemEmployeeDiscountVoided().toString();
        }
        return value;
    }
    /**
     * Get the AmountGrossTransactionEmployeeDiscount as a String
     *
     * @param totals Totals object containing the data
     * @return AmountGrossTransactionEmployeeDiscount converted to a String
     */
    protected String getAmountGrossTransactionEmployeeDiscount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getAmountGrossTransactionEmployeeDiscount().toString();
        }
        return value;
    }

    /**
     * Get the UnitsGrossTransactionEmployeeDiscount as a String
     *
     * @param totals Totals object containing the data
     * @return UnitsGrossTransactionEmployeeDiscount converted to a String
     */
    protected String getUnitsGrossTransactionEmployeeDiscount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getUnitsGrossTransactionEmployeeDiscount().toString();
        }
        return value;
    }
    /**
     * Get the AmountGrossTransactionEmployeeDiscountVoided as a String
     *
     * @param totals Totals object containing the data
     * @return AmountGrossTransactionEmployeeDiscountVoided converted to a String
     */
    protected String getAmountGrossTransactionEmployeeDiscountVoided(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getAmountGrossTransactionEmployeeDiscountVoided().toString();
        }
        return value;
    }

    /**
     * Get the UnitsGrossTransactionEmployeeDiscountVoided as a String
     *
     * @param totals Totals object containing the data
     * @return UnitsGrossTransactionEmployeeDiscountVoided converted to a String
     */
    protected String getUnitsGrossTransactionEmployeeDiscountVoided(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getUnitsGrossTransactionEmployeeDiscountVoided().toString();
        }
        return value;
    }

    /**
     * Get the AmountGrossGiftCertificateIssuedVoided as a String
     *
     * @param totals Totals object containing the data
     * @return AmountGrossGiftCertificateIssuedVoided converted to a String
     */
    protected String getAmountGrossGiftCertificateIssuedVoided(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getAmountGrossGiftCertificateIssuedVoided().toString();
        }
        return value;
    }

    /**
     * Get the UnitsGrossGiftCertificateIssuedVoided as a String
     *
     * @param totals Totals object containing the data
     * @return UnitsGrossGiftCertificateIssuedVoided converted to a String
     */
    protected String getUnitsGrossGiftCertificateIssuedVoided(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getUnitsGrossGiftCertificateIssuedVoided().toString();
        }
        return value;
    }

    /**
     * Get the AmountGrossGiftCertificateTendered as a String
     *
     * @param totals Totals object containing the data
     * @return AmountGrossGiftCertificateTendered converted to a String
     */
    protected String getAmountGrossGiftCertificateTendered(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getAmountGrossGiftCertificateTendered().toString();
        }
        return value;
    }

    /**
     * Get the UnitsGrossGiftCertificateTendered as a String
     *
     * @param totals Totals object containing the data
     * @return UnitsGrossGiftCertificateTendered converted to a String
     */
    protected String getUnitsGrossGiftCertificateTendered(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getUnitsGrossGiftCertificateTendered().toString();
        }
        return value;
    }


    /**
     * Get the AmountGrossGiftCertificateTenderedVoided as a String
     *
     * @param totals Totals object containing the data
     * @return AmountGrossGiftCertificateTenderedVoided converted to a String
     */
    protected String getAmountGrossGiftCertificateTenderedVoided(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getAmountGrossGiftCertificateTenderedVoided().toString();
        }
        return value;
    }

    /**
     * Get the UnitsGrossGiftCertificateTenderedVoided as a String
     *
     * @param totals Totals object containing the data
     * @return UnitsGrossGiftCertificateTenderedVoided converted to a String
     */
    protected String getUnitsGrossGiftCertificateTenderedVoided(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getUnitsGrossGiftCertificateTenderedVoided().toString();
        }
        return value;
    }

    /**
     * Get the AmountEmployeeDiscounts as a String
     *
     * @param totals Totals object containing the data
     * @return AmountEmployeeDiscounts converted to a String
     */
    protected String getAmountEmployeeDiscounts(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getAmountGrossTransactionEmployeeDiscount().toString();
        }
        return value;
    }

    /**
     * Get the UnitsEmployeeDiscounts as a String
     *
     * @param totals Totals object containing the data
     * @return UnitsEmployeeDiscounts converted to a String
     */
    protected String getUnitsEmployeeDiscounts(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getUnitsGrossTransactionEmployeeDiscount().toString();
        }
        return value;
    }

    /**
     * Get the AmountCustomerDiscounts as a String
     *
     * @param totals Totals object containing the data
     * @return AmountCustomerDiscounts converted to a String
     */
    protected String getAmountCustomerDiscounts(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getAmountCustomerDiscounts().toString();
        }
        return value;
    }

    /**
     * Get the UnitsCustomerDiscounts as a String
     *
     * @param totals Totals object containing the data
     * @return UnitsCustomerDiscounts converted to a String
     */
    protected String getUnitsCustomerDiscounts(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getUnitsCustomerDiscounts().toString();
        }
        return value;
    }

    /**
     * Get the AmountPriceOverrides as a String
     *
     * @param totals Totals object containing the data
     * @return AmountPriceOverrides converted to a String
     */
    protected String getAmountPriceOverrides(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getAmountPriceOverrides().toString();
        }
        return value;
    }

    /**
     * Get the UnitsPriceOverrides as a String
     *
     * @param totals Totals object containing the data
     * @return UnitsPriceOverrides converted to a String
     */
    protected String getUnitsPriceOverrides(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getUnitsPriceOverrides().toString();
        }
        return value;
    }

    /**
     * Get the UnitsPriceAdjustments as a String
     *
     * @param totals Totals object containing the data
     * @return UnitsPriceOverrides converted to a String
     */
    protected String getUnitsPriceAdjustments(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getUnitsPriceAdjustments().toString();
        }
        return value;
    }


    /**
     * Get the CountTransactionsWithReturnedItems as a String
     *
     * @param totals Totals object containing the data
     * @return CountTransactionsWithReturnedItems converted to a String
     */
    protected String getCountTransactionsWithReturnedItems(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = String.valueOf(totals.getTransactionsWithReturnedItemsCount());
        }
        return value;
    }
    /**
     * Get the till reconciled amount from the entered FinancialCountTenderItem
     * @param till
     * @param tenderDescriptor
     * @return
     */
    protected String getTillReconcileAmount(RegisterIfc register, TenderDescriptorIfc tenderDesc)
    {
        FinancialCountIfc enteredCount = register.getTotals().getCombinedCount().getEntered();
        FinancialCountTenderItemIfc enteredTender = enteredCount.getSummaryTenderItemByDescriptor(tenderDesc);

        CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance();;

        if (enteredTender != null)
        {
            amount = enteredTender.getAmountTotal();
        }

        String value = amount.getStringValue();
        return(value);
    }

    /**
     Returns the tender loan count
     <p>
     @param  loans       The till loans
     @param  tenderDesc  The TenderDescriptor
     @return the tender loan count
     */
    protected String getTillReconcileCount(RegisterIfc register, TenderDescriptorIfc tenderDesc)
    {
        String value = "0";

        FinancialCountIfc enteredCount = register.getTotals().getCombinedCount().getEntered();
        FinancialCountTenderItemIfc enteredTender = enteredCount.getSummaryTenderItemByDescriptor(tenderDesc);

        if (enteredTender != null)
        {
            value = String.valueOf(enteredTender.getNumberItemsIn() - enteredTender.getNumberItemsOut());
        }

        return(value);
    }
}
