/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveTill.java /main/19 2013/02/27 15:01:18 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     02/20/13 - Modified for Currency Rounding.
 *    nkgautam  08/02/10 - bill payment changes
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
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
 *    miparek   11/14/08 - Forward port 7330439 REPORTS-RECONCILE TILL COUNT
 *                         REPORT CASHIER NAME ISSUE
 *
 * ===========================================================================
 * $Log:
 *    9    360Commerce 1.8         5/16/2007 7:55:27 PM   Brett J. Larsen
 *         CR 26903 - 8.0.1 merge to trunk
 *
 *         BackOffice <ARG> Summary Report overhaul (many CRs fixed)
 *
 *    8    360Commerce 1.7         5/14/2007 6:08:34 PM   Sandy Gu
 *         update inclusive information in financial totals and history tables
 *    7    360Commerce 1.6         4/25/2007 10:01:09 AM  Anda D. Cadar   I18N
 *         merge
 *    6    360Commerce 1.5         3/17/2006 1:00:19 AM   Venkat Reddy    ***
 *         CR 8357
 *         Venkat Reddy
 *         03/17/2006
 *         Added method "updateTillHistoryUtil()" to update status
 *         ***
 *    5    360Commerce 1.4         1/25/2006 4:11:24 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         1/22/2006 11:41:23 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:44 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:50 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:04 PM  Robert Pearse
 *:
 *    6    .v700     1.2.1.2     11/23/2005 16:58:20    Rohit Sachdeva
 *         CR6708:For Till Summary Drill Down report in BO: Transaction
 *         Statistics Section displaying "Returns" Line in Description. Shows
 *         Incorrect Trans#.
 *    5    .v700     1.2.1.1     11/16/2005 16:26:35    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    4    .v700     1.2.1.0     11/2/2005 17:17:49     Rohit Sachdeva  6640:
 *         For Backoffice, Total Employee Discount for Employee Discounts
 *    3    360Commerce1.2         3/31/2005 15:28:44     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:50     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:04     Robert Pearse
 *
 *   Revision 1.22  2004/07/27 23:02:23  dcobb
 *   @scr 6462 Financial Totals are not correct for the detail count during Till Open/Reconcile
 *   Replaced all instances of FinancialCountIfc.getTenderItem(int, String) with getSummaryTenderItemByDescriptor(TenderDescriptorIfc).
 *   Also replaced deprecated call with FinancialCountIfc.getAmountGrossTransactionEmployeeDiscount() and getUnitsGrossTransactionEmployeeDiscount().
 *
 *   Revision 1.21  2004/06/18 23:22:30  cdb
 *   @scr 4180 Removed methods deprecated in release 5.0.
 *
 *   Revision 1.20  2004/06/18 22:56:43  cdb
 *   @scr 4205 Corrected problems caused by searching financial counts
 *   by tender description rather than tender descriptor - which caused problems
 *   with foreign currencies.
 *
 *   Revision 1.19  2004/06/18 15:49:57  cdb
 *   @scr 4205 Corrected problem caused by searching for entered count
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
 *   Revision 1.14  2004/04/28 19:41:39  jdeleau
 *   @scr 4218 Add StoreCreditsIssued (count/amount and voided counts and amounts)
 *   to Financial Totals.
 *
 *   Revision 1.13  2004/04/27 20:01:16  jdeleau
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
 *   Revision 1.8  2004/04/02 23:07:33  jdeleau
 *   @scr 4218 Register Reports - House Account and initial changes to
 *   the way SummaryReports are built.
 *
 *   Revision 1.7  2004/02/25 22:51:13  crain
 *   @scr 3814 Issue Gift Certificate
 *
 *   Revision 1.6  2004/02/25 04:18:44  crain
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
 *    Rev 1.0   Aug 29 2003 15:33:02   CSchellenger
 * Initial revision.
 *
 *    Rev 1.5   Jul 14 2003 18:49:40   sfl
 * Added the code to store voided transaction counts in the TillHistory table.
 * Resolution for POS SCR-2764: Till Summary Report - Net Trans. Taxable and Tax line items count fields incorrect
 *
 *    Rev 1.4   Jun 25 2003 17:08:46   sfl
 * Use absolute value for line item void amount due to BA request.
 * Resolution for POS SCR-2911: Line Item Delete during a Return incorrectly updates Line Item Delete line on Summary Reports
 *
 *    Rev 1.3   Feb 15 2003 17:26:02   mpm
 * Merged 5.1 changes.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.2   Dec 20 2002 11:15:26   DCobb
 * Add floating till.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.1   11 Jun 2002 16:25:08   jbp
 * changes to report markdowns
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.0   Jun 03 2002 16:40:20   msg
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.util.Date;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.ReconcilableCount;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.common.sql.SQLUpdatableStatementIfc;
import oracle.retail.stores.common.sql.SQLInsertStatement;

/**
 * This operation performs inserts into the till history and till history tender
 * tables.
 * 
 * @version $Revision: /main/19 $
 */
public abstract class JdbcSaveTill extends JdbcSaveReportingPeriod implements ARTSDatabaseIfc
{
    /** serialVersionUID */
    private static final long serialVersionUID = 2488538280002281790L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveTill.class);

    // Tender type map
    protected static TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();

    /**
     * This value should be set EVERY time a SQL statement reference is created
     * (i.e. SQLInsertStatement). The value will be used in differentiating
     * between Insert and Update statements and adjust the generated SQL
     * accordingly. This will solve the problem that in an insert statement:
     * <column_name> = <column_name> + <value> results with a NULL regardless of
     * what <value> is. This is true even with columns that have a default
     * value. The default value is not applied.
     */
    protected boolean isUpdateStatement = false;

    /**
     * Adds a record to the till table.
     * 
     * @param dataConnection connection to the db
     * @param till the till information
     * @param register the register associated with the till
     * @return true if successful
     * @exception DataException upon error
     */
    public boolean insertTill(JdbcDataConnection dataConnection,
                              TillIfc till,
                              RegisterIfc register)
                              throws DataException
    {
        boolean returnCode = false;

        SQLInsertStatement sql = new SQLInsertStatement();
        isUpdateStatement = false;

        /*
         * Define table
         */
        sql.setTable(TABLE_TILL);

        /*
         * Add columns and their values
         */
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(register));
        sql.addColumn(FIELD_TENDER_REPOSITORY_ID, getTillID(till));
        sql.addColumn(FIELD_TILL_SIGNON_OPERATOR, makeSafeString(till.getSignOnOperator().getEmployeeID()));
        if (till.getSignOffOperator() != null)
        {
            sql.addColumn(FIELD_TILL_SIGNOFF_OPERATOR, makeSafeString(till.getSignOffOperator().getEmployeeID()));
        }
        sql.addColumn(FIELD_TILL_STATUS_CODE, getStatusCode(till));
        sql.addColumn(FIELD_TILL_STATUS_DATE_TIME_STAMP, dateToSQLTimestampString(new Date()));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(register));
        sql.addColumn(FIELD_TILL_START_DATE_TIMESTAMP, getStartTimestamp(till));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDay(till.getBusinessDate()));
        sql.addColumn(FIELD_WORKSTATION_ACCOUNTABILITY, "'" + till.getRegisterAccountability() + "'");
        sql.addColumn(FIELD_TILL_TYPE, "'" + till.getTillType() + "'");

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.warn(de);
            updateTill(dataConnection, till, register); // offline ?
        }

        if (0 < dataConnection.getUpdateCount())
        {
            returnCode = true;
        }

        return(returnCode);
    }

    /**
     * Updates a record in the till table.
     * 
     * @param dataConnection connection to the db
     * @param till the till information
     * @param register the register associated with the till
     * @return true if successful
     * @exception DataException upon error
     */
    public boolean updateTill(JdbcDataConnection dataConnection,
                              TillIfc till,
                              RegisterIfc register)
                              throws DataException
    {
        boolean returnCode = false;
        SQLUpdateStatement sql = new SQLUpdateStatement();
        isUpdateStatement = true;

        /*
         * Define the table
         */
        sql.setTable(TABLE_TILL);

        /*
         * Add columns and their values
         */
        sql.addColumn(FIELD_TILL_SIGNON_OPERATOR, makeSafeString(till.getSignOnOperator().getEmployeeID()));
        if (till.getSignOffOperator() != null)
        {
            sql.addColumn(FIELD_TILL_SIGNOFF_OPERATOR, makeSafeString(till.getSignOffOperator().getEmployeeID()));
        }
        sql.addColumn(FIELD_TILL_STATUS_CODE, getStatusCode(till));
        sql.addColumn(FIELD_TILL_STATUS_DATE_TIME_STAMP, dateToSQLTimestampString(new Date()));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(register));
        sql.addColumn(FIELD_TILL_START_DATE_TIMESTAMP, getStartTimestamp(till));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDay(till.getBusinessDate()));
        sql.addColumn(FIELD_WORKSTATION_ACCOUNTABILITY, "'" + till.getRegisterAccountability() + "'");
        sql.addColumn(FIELD_TILL_TYPE, "'" + till.getTillType() + "'");
        /*
         * Add Qualifier(s)
         */
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(register));
        sql.addQualifier(FIELD_TENDER_REPOSITORY_ID + " = " + getTillID(till));

        dataConnection.execute(sql.getSQLString());

        if (0 < dataConnection.getUpdateCount())
        {
            returnCode = true;
        }

        return(returnCode);
    }

    /**
     * Adds a record to the till history table.
     * 
     * @param dataConnection connection to the db
     * @param till the till information
     * @param register the register associated with the till
     * @return true if successful
     * @exception DataException upon error
     */
    public boolean insertTillHistory(JdbcDataConnection dataConnection,
                                     TillIfc till,
                                     RegisterIfc register)
                                     throws DataException
    {
        boolean returnCode = false;
        SQLInsertStatement sql = new SQLInsertStatement();
        isUpdateStatement = false;

        /*
         * Define table
         */
        sql.setTable(TABLE_TILL_HISTORY);
        /*
         * Add columns and their values
         */
        sql.addColumn(FIELD_TENDER_REPOSITORY_ID, getTillID(till));
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(register));
        sql.addColumn(FIELD_TILL_START_DATE_TIMESTAMP, getStartTimestamp(till));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(register));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDay(till.getBusinessDate()));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(register));
        sql.addColumn(FIELD_TILL_HISTORY_STATUS_CODE, getStatusCode(till));
        // inserting operator id
        sql.addColumn(FIELD_OPERATOR_ID, getOperatorID(till));

        // set timestamp
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
        //+I18N
        sql.addColumn(FIELD_CURRENCY_ID, register.getTillFloatAmount().getType().getCurrencyId());
        //-I18N
        addFinancialTotals(sql, till.getTotals());

        dataConnection.execute(sql.getSQLString());

        if (0 < dataConnection.getUpdateCount() && insertTillTenders(dataConnection, till, register))
        {
            returnCode = true;
        }

        return(returnCode);
    }

    /**
     * Updates a record in the till history table.
     * 
     * @param dataConnection connection to the db
     * @param till the till information
     * @param register the register information
     * @return true if successful
     * @exception DataException upon error
     */
    public boolean updateTillHistory(JdbcDataConnection dataConnection,
                                     TillIfc till,
                                     RegisterIfc register)
                                     throws DataException
    {
        boolean returnCode = false;

        SQLUpdateStatement sql = new SQLUpdateStatement();
        isUpdateStatement = true;

        /*
         * Define table
         */
        sql.setTable(TABLE_TILL_HISTORY);

        /*
         * Add columns and their values
         */
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(register));
        sql.addColumn(FIELD_TILL_HISTORY_STATUS_CODE, getStatusCode(till));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDay(till.getBusinessDate()));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(register));
        //+I18N
        sql.addColumn(FIELD_CURRENCY_ID, register.getTillFloatAmount().getType().getCurrencyId());
        //-I18N

        addFinancialTotals(sql, till.getTotals());

        /*
         * Add Qualifiers
         */
        sql.addQualifier(FIELD_TENDER_REPOSITORY_ID + " = " + getTillID(till));
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(register));
        sql.addQualifier(FIELD_TILL_START_DATE_TIMESTAMP + " = " + getStartTimestamp(till));

        dataConnection.execute(sql.getSQLString());

        if (0 < dataConnection.getUpdateCount()
            && updateTillTenders(dataConnection, till, register))
        {
            returnCode = true;
        }

        return(returnCode);
    }

    /**
     * Updates a selected columns of a record in the till history table.
     * 
     * @param dataConnection connection to the db
     * @param till the till information
     * @param register the register information
     * @exception DataException upon error
     */
    public void updateTillHistoryUtil(JdbcDataConnection dataConnection,
            TillIfc till,
            RegisterIfc register)
            throws DataException
    {
           SQLUpdateStatement sql = new SQLUpdateStatement();

           sql.setTable(TABLE_TILL_HISTORY);

           sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(register));
           sql.addColumn(FIELD_TILL_HISTORY_STATUS_CODE, getStatusCode(till));
           sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDay(till.getBusinessDate()));
           sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(register));
           //+I18N
           sql.addColumn(FIELD_CURRENCY_ID, register.getTillFloatAmount().getType().getCurrencyId());
           //-I18N

           sql.addQualifier(FIELD_TENDER_REPOSITORY_ID + " = " + getTillID(till));
           sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(register));
           sql.addQualifier(FIELD_TILL_START_DATE_TIMESTAMP + " = " + getStartTimestamp(till));

           dataConnection.execute(sql.getSQLString());
    }

    /**
     * Inserts a record in the till tender history table.
     * 
     * @param dataConnection connection to the db
     * @param till the till information
     * @param register the register information
     * @return true if successful
     * @exception DataException upon error
     */
    public boolean insertTillTenders(JdbcDataConnection dataConnection,
                                     TillIfc till,
                                     RegisterIfc register)
                                     throws DataException
    {
        boolean returnCode = true;

        /*
         * Combine loans and pickups once for each till
         */
        ReconcilableCountIfc loans = combineByTenderType(till.getTotals().getTillLoans());
        ReconcilableCountIfc pickups = combineByTenderType(till.getTotals().getTillPickups());
        ReconcilableCountIfc payIns = combineByTenderType(till.getTotals().getTillPayIns());
        ReconcilableCountIfc payOuts = combineByTenderType(till.getTotals().getTillPayOuts());
        /*
         * Walk through each tender item and save it
         */
        FinancialCountTenderItemIfc[] tenderTypes = getTenderTypes(till.getTotals().getCombinedCount());
        for (int i = 0; i < tenderTypes.length; ++i)
        {
            if (!insertTillTenderHistory(dataConnection, tenderTypes[i], till, register, loans, pickups, payIns, payOuts))
            {
                returnCode = false;
            }
        }

        return(returnCode);
    }

    /**
     * Inserts a record in the till tender history table.
     * 
     * @param dataConnection connection to the db
     * @param tenderItem the tender item
     * @param till the till information
     * @param register the register information
     * @param loans loans
     * @param pickups pickups
     * @param payIns pay ins
     * @param payOuts pay outs
     * @return true if successful
     * @exception DataException upon error
     */
    public boolean insertTillTenderHistory(JdbcDataConnection dataConnection,
                                           FinancialCountTenderItemIfc tenderItem,
                                           TillIfc till,
                                           RegisterIfc register,
                                           ReconcilableCountIfc loans,
                                           ReconcilableCountIfc pickups,
                                           ReconcilableCountIfc payIns,
                                           ReconcilableCountIfc payOuts)
                                           throws DataException
    {
        boolean returnCode = true;
        String tenderType = tenderTypeMap.getCode(tenderItem.getTenderType());
        String tenderSubType = tenderItem.getTenderSubType();
        String tenderDesc = tenderItem.getDescription();
        TenderDescriptorIfc tenderDescriptor = tenderItem.getTenderDescriptor();

        SQLInsertStatement sql = new SQLInsertStatement();
        isUpdateStatement = false;

        /*
         * Define table
         */
        sql.setTable(TABLE_TILL_TENDER_HISTORY);
        /*
         * Add columns and their values
         */
        sql.addColumn(FIELD_TENDER_REPOSITORY_ID, getTillID(till));
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(register));
        sql.addColumn(FIELD_TILL_START_DATE_TIMESTAMP, getStartTimestamp(till));
        sql.addColumn(FIELD_TENDER_TYPE_CODE, inQuotes(tenderType));
        sql.addColumn(FIELD_TENDER_SUBTYPE, inQuotes(emptyStringToSpaceString(tenderSubType)));
        sql.addColumn(FIELD_CURRENCY_ISSUING_COUNTRY_CODE, inQuotes(tenderItem.getCurrencyCode()));
        sql.addColumn(FIELD_CURRENCY_ID, tenderItem.getCurrencyID());
        sql.addColumn(FIELD_TILL_TENDER_DEPOSIT_TOTAL_AMOUNT,
                      getTenderDepositAmount(till, tenderDesc));
        sql.addColumn(FIELD_TILL_TENDER_LOAN_MEDIA_TOTAL_AMOUNT,
                      getTenderLoanAmount(loans, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TENDER_OVER_TOTAL_AMOUNT,
                      getTenderOverAmount(till, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TENDER_PICKUP_MEDIA_TOTAL_AMOUNT,
                      getTenderPickupAmount(pickups, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TENDER_SHORT_TOTAL_AMOUNT,
                      getTenderShortAmount(till, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TOTAL_BEGINNING_TENDER_MEDIA_UNIT_COUNT,
                      getTenderBeginningCount(till, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TOTAL_TENDER_DEPOSIT_MEDIA_UNIT_COUNT,
                      getTenderDepositCount(till, tenderDesc));
        sql.addColumn(FIELD_TILL_TOTAL_TENDER_LOAN_MEDIA_UNIT_COUNT,
                      getTenderLoanCount(loans, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TOTAL_TENDER_MEDIA_COUNT,
                      getTenderCount(till, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TOTAL_TENDER_MEDIA_OVER_COUNT,
                      getTenderOverCount(till, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TOTAL_TENDER_MEDIA_SHORT_COUNT,
                      getTenderShortCount(till, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TOTAL_TENDER_PICKUP_MEDIA_UNIT_COUNT,
                      getTenderPickupCount(pickups, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TOTAL_TENDER_REFUND_MEDIA_UNIT_COUNT,
                      getTenderRefundCount(till, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TENDER_REFUND_TOTAL_AMOUNT,
                      getTenderRefundAmount(till, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TENDER_TOTAL_AMOUNT,
                      getTenderTotalAmount(till, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TENDER_OPEN_AMOUNT,
                      getTenderOpenAmount(till, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TENDER_CLOSE_AMOUNT,
                      getTenderCloseAmount(till, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TENDER_MEDIA_CLOSE_COUNT,
                      getTenderCloseCount(till, tenderDescriptor));
        sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_IN_MEDIA_TOTAL_AMOUNT,
                      getTillPayInAmount(payIns, tenderDescriptor));
        sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_OUT_MEDIA_TOTAL_AMOUNT,
                      getTillPayOutAmount(payOuts, tenderDescriptor));
        sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_IN_MEDIA_UNIT_COUNT,
                      getTillPayInCount(payIns, tenderDescriptor));
        sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_OUT_MEDIA_UNIT_COUNT,
                      getTillPayOutCount(payOuts, tenderDescriptor));
        sql.addColumn(FIELD_RECONCILE_AMOUNT, 
                      getTillReconcileAmount(till, tenderDescriptor));
        sql.addColumn(FIELD_RECONCILE_MEDIA_UNIT_COUNT, 
                      getTillReconcileCount(till, tenderDescriptor));
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

        dataConnection.execute(sql.getSQLString());

        if (0 >= dataConnection.getUpdateCount())
        {
            returnCode = false;
        }

        return(returnCode);
    }

    /**
        Updates all of the tender entries in the till financial totals.
        <P>
        @param  dataConnection  connection to the db
        @param  till            the till information
        @param  register        the register information
        @return true if successful
        @exception DataException upon error
     */
    public boolean updateTillTenders(JdbcDataConnection dataConnection,
                                     TillIfc till,
                                     RegisterIfc register)
                                     throws DataException
    {
        boolean returnCode = true;

        /*
         * Do this once for each till
         */
        ReconcilableCountIfc loans = combineByTenderType(till.getTotals().getTillLoans());
        ReconcilableCountIfc pickups = combineByTenderType(till.getTotals().getTillPickups());
        ReconcilableCountIfc payIns = combineByTenderType(till.getTotals().getTillPayIns());
        ReconcilableCountIfc payOuts = combineByTenderType(till.getTotals().getTillPayOuts());

        /*
         * Walk through each tender item and save it
         */
        FinancialCountTenderItemIfc[] tenderTypes = getTenderTypes(till.getTotals().getCombinedCount());
        for (int i = 0; i < tenderTypes.length; ++i)
        {
            if (!updateTillTenderHistory(dataConnection, tenderTypes[i], till, register, loans, pickups, payIns, payOuts))
            {
                if (!insertTillTenderHistory(dataConnection, tenderTypes[i], till, register, loans, pickups, payIns, payOuts))
                {
                    returnCode = false;
                }
            }
        }

        return(returnCode);
    }

    /**
        Updates a record in the till tender history table.
        <P>
        @param  dataConnection  connection to the db
        @param  tenderItem      the tender item
        @param  till            the till information
        @param  register        the register information
        @param  loans           loans
        @param  pickups         pickups
        @param  payIns          pay ins
        @param payOuts          pay outs
        @return true if successful
        @exception DataException upon error
     */
    public boolean updateTillTenderHistory(JdbcDataConnection dataConnection,
                                           FinancialCountTenderItemIfc tenderItem,
                                           TillIfc till,
                                           RegisterIfc register,
                                           ReconcilableCountIfc loans,
                                           ReconcilableCountIfc pickups,
                                           ReconcilableCountIfc payIns,
                                           ReconcilableCountIfc payOuts)
                                           throws DataException
    {
        boolean returnCode = true;
        String tenderType = tenderTypeMap.getCode(tenderItem.getTenderType());
        String tenderSubType = tenderItem.getTenderSubType();
        String tenderDesc = tenderItem.getDescription();
        TenderDescriptorIfc tenderDescriptor = tenderItem.getTenderDescriptor();

        SQLUpdateStatement sql = new SQLUpdateStatement();
        isUpdateStatement = true;

        /*
         * Define table
         */
        sql.setTable(TABLE_TILL_TENDER_HISTORY);
        /*
         * Add columns and their values
         */
        sql.addColumn(FIELD_TILL_TENDER_DEPOSIT_TOTAL_AMOUNT,
                      getTenderDepositAmount(till, tenderDesc));
        sql.addColumn(FIELD_TILL_TENDER_LOAN_MEDIA_TOTAL_AMOUNT,
                      getTenderLoanAmount(loans, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TENDER_OVER_TOTAL_AMOUNT,
                      getTenderOverAmount(till, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TENDER_PICKUP_MEDIA_TOTAL_AMOUNT,
                      getTenderPickupAmount(pickups, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TENDER_SHORT_TOTAL_AMOUNT,
                      getTenderShortAmount(till, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TOTAL_BEGINNING_TENDER_MEDIA_UNIT_COUNT,
                      getTenderBeginningCount(till, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TOTAL_TENDER_DEPOSIT_MEDIA_UNIT_COUNT,
                      getTenderDepositCount(till, tenderDesc));
        sql.addColumn(FIELD_TILL_TOTAL_TENDER_LOAN_MEDIA_UNIT_COUNT,
                      getTenderLoanCount(loans, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TOTAL_TENDER_MEDIA_COUNT,
                      getTenderCount(till, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TOTAL_TENDER_MEDIA_OVER_COUNT,
                      getTenderOverCount(till, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TOTAL_TENDER_MEDIA_SHORT_COUNT,
                      getTenderShortCount(till, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TOTAL_TENDER_PICKUP_MEDIA_UNIT_COUNT,
                      getTenderPickupCount(pickups, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TOTAL_TENDER_REFUND_MEDIA_UNIT_COUNT,
                      getTenderRefundCount(till, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TENDER_REFUND_TOTAL_AMOUNT,
                      getTenderRefundAmount(till, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TENDER_TOTAL_AMOUNT,
                      getTenderTotalAmount(till, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TENDER_OPEN_AMOUNT,
                      getTenderOpenAmount(till, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TENDER_CLOSE_AMOUNT,
                      getTenderCloseAmount(till, tenderDescriptor));
        sql.addColumn(FIELD_TILL_TENDER_MEDIA_CLOSE_COUNT,
                      getTenderCloseCount(till, tenderDescriptor));
        sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_IN_MEDIA_TOTAL_AMOUNT,
                      getTillPayInAmount(payIns, tenderDescriptor));
        sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_OUT_MEDIA_TOTAL_AMOUNT,
                      getTillPayOutAmount(payOuts, tenderDescriptor));
        sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_IN_MEDIA_UNIT_COUNT,
                      getTillPayInCount(payIns, tenderDescriptor));
        sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_OUT_MEDIA_UNIT_COUNT,
                      getTillPayOutCount(payOuts, tenderDescriptor));
        sql.addColumn(FIELD_RECONCILE_AMOUNT, 
                      getTillReconcileAmount(till, tenderDescriptor));
        sql.addColumn(FIELD_RECONCILE_MEDIA_UNIT_COUNT, 
                      getTillReconcileCount(till, tenderDescriptor));
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                      getSQLCurrentTimestampFunction());

        /*
         * Add Qualifiers
         */
        sql.addQualifier(FIELD_TENDER_REPOSITORY_ID + " = " + getTillID(till));
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(register));
        sql.addQualifier(FIELD_TILL_START_DATE_TIMESTAMP + " = " + getStartTimestamp(till));
        sql.addQualifier(FIELD_TENDER_TYPE_CODE + " = " + inQuotes(tenderType));
        sql.addQualifier(FIELD_TENDER_SUBTYPE + " = " + inQuotes(emptyStringToSpaceString(tenderSubType)));
        sql.addQualifier(FIELD_CURRENCY_ISSUING_COUNTRY_CODE + " = " + inQuotes(tenderItem.getCurrencyCode()));
        //sql.addQualifier(FIELD_CURRENCY_ID + " = " + tenderItem.getCurrencyID());

        dataConnection.execute(sql.getSQLString());

        if (0 >= dataConnection.getUpdateCount())
        {
            returnCode = false;
        }

        return(returnCode);
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
        sql.addColumn(FIELD_TILL_TOTAL_TRANSACTION_COUNT, getTransactionCount(totals));
        sql.addColumn(FIELD_TILL_NONTAXABLE_TOTAL_AMOUNT, getNetNontaxableAmount(totals));
        sql.addColumn(FIELD_TILL_TOTAL_NONTAXABLE_COUNT, getNontaxableCount(totals));
        sql.addColumn(FIELD_TILL_TOTAL_TAXABLE_COUNT, getTaxableCount(totals));
        sql.addColumn(FIELD_TILL_TAX_EXEMPT_TOTAL_AMOUNT, getNetTaxExemptAmount(totals));
        sql.addColumn(FIELD_TILL_TOTAL_TAX_EXEMPT_TRANSACTION_COUNT, getTaxExemptCount(totals));
        sql.addColumn(FIELD_TILL_REFUND_TOTAL_AMOUNT, getRefundAmount(totals));
        // Transaction Sales/Refunds
        sql.addColumn(FIELD_TILL_GROSS_SALES_EX_TAX_TOTAL_AMOUNT, getSalesAmount(totals));
        sql.addColumn(FIELD_TILL_TOTAL_REFUND_COUNT, getRefundCount(totals));
        sql.addColumn(FIELD_TILL_REFUND_NONTAXABLE_TOTAL_AMOUNT, getRefundNontaxableAmount(totals));
        sql.addColumn(FIELD_TILL_TOTAL_NONTAXABLE_REFUND_COUNT, getRefundNontaxableCount(totals));
        sql.addColumn(FIELD_TILL_REFUND_TAX_EXEMPT_TOTAL_AMOUNT, getRefundTaxExemptAmount(totals));
        sql.addColumn(FIELD_TILL_TOTAL_TAX_EXEMPT_REFUND_COUNT, getRefundTaxExemptCount(totals));
        sql.addColumn(FIELD_TILL_GROSS_TAXABLE_TRANSACTION_SALES_AMOUNT, getTaxableTransactionSalesAmount(totals));
        sql.addColumn(FIELD_TILL_GROSS_TAXABLE_TRANSACTION_SALES_COUNT, getTaxableTransactionSalesCount(totals));
        sql.addColumn(FIELD_TILL_GROSS_NON_TAXABLE_TRANSACTION_SALES_AMOUNT, getNonTaxableTransactionSalesAmount(totals));
        sql.addColumn(FIELD_TILL_GROSS_NON_TAXABLE_TRANSACTION_SALES_COUNT, getNonTaxableTransactionSalesCount(totals));
        sql.addColumn(FIELD_TILL_GROSS_TAX_EXEMPT_TRANSACTION_SALES_AMOUNT, getTaxExemptTransactionSalesAmount(totals));
        sql.addColumn(FIELD_TILL_GROSS_TAX_EXEMPT_TRANSACTION_SALES_COUNT, getTaxExemptTransactionSalesCount(totals));
        sql.addColumn(FIELD_TILL_GROSS_TAXABLE_TRANSACTION_RETURNS_AMOUNT, getTaxableTransactionReturnsAmount(totals));
        sql.addColumn(FIELD_TILL_GROSS_TAXABLE_TRANSACTION_RETURNS_COUNT, getTaxableTransactionReturnsCount(totals));
        // Item Sales/Returns
        sql.addColumn(FIELD_TILL_LINE_ITEM_SALES_TOTAL_AMOUNT, getItemSalesAmount(totals));
        sql.addColumn(FIELD_TILL_TOTAL_SALE_LINE_ITEM_COUNT, getItemSalesCount(totals));
        sql.addColumn(FIELD_TILL_LINE_ITEM_NONTAXABLE_TOTAL_AMOUNT, getItemNontaxableAmount(totals));
        sql.addColumn(FIELD_TILL_TOTAL_NONTAXABLE_LINE_ITEM_COUNT, getItemNontaxableCount(totals));
        sql.addColumn(FIELD_TILL_LINE_ITEM_TAX_EXEMPT_TOTAL_AMOUNT, getItemTaxExemptAmount(totals));
        sql.addColumn(FIELD_TILL_TOTAL_TAX_EXEMPT_LINE_ITEM_COUNT, getItemTaxExemptCount(totals));
        sql.addColumn(FIELD_TILL_RETURN_TOTAL_AMOUNT, getReturnAmount(totals));
        sql.addColumn(FIELD_TILL_TOTAL_RETURN_COUNT, getReturnCount(totals));
        sql.addColumn(FIELD_TILL_RETURN_NONTAXABLE_TOTAL_AMOUNT, getReturnNontaxableAmount(totals));
        sql.addColumn(FIELD_TILL_TOTAL_NONTAXABLE_RETURN_COUNT, getReturnNontaxableCount(totals));
        sql.addColumn(FIELD_TILL_RETURN_TAX_EXEMPT_TOTAL_AMOUNT, getReturnTaxExemptAmount(totals));
        sql.addColumn(FIELD_TILL_TOTAL_TAX_EXEMPT_RETURN_COUNT, getReturnTaxExemptCount(totals));

        sql.addColumn(FIELD_TILL_NONMERCH_NONTAX_TOTAL_AMOUNT, getNonMerchNonTaxAmount(totals));
        sql.addColumn(FIELD_TILL_TOTAL_NONMERCH_NONTAX_COUNT, getNonMerchNonTaxCount(totals));
        sql.addColumn(FIELD_TILL_RETURN_NONMERCH_NONTAX_TOTAL_AMOUNT, getNonMerchNonTaxReturnAmount(totals));
        sql.addColumn(FIELD_TILL_TOTAL_NONMERCH_NONTAX_RETURN_COUNT, getNonMerchNonTaxReturnCount(totals));
        sql.addColumn(FIELD_TILL_NONMERCH_TAX_TOTAL_AMOUNT, getNonMerchAmount(totals));
        sql.addColumn(FIELD_TILL_TOTAL_NONMERCH_TAX_COUNT, getNonMerchCount(totals));
        sql.addColumn(FIELD_TILL_RETURN_NONMERCH_TAX_TOTAL_AMOUNT, getNonMerchReturnAmount(totals));
        sql.addColumn(FIELD_TILL_TOTAL_NONMERCH_TAX_RETURN_COUNT, getNonMerchReturnCount(totals));
        sql.addColumn(FIELD_TILL_GIFT_CARD_AMOUNT, getGiftCardAmount(totals));
        sql.addColumn(FIELD_TILL_GIFT_CARD_COUNT, getGiftCardCount(totals));
        sql.addColumn(FIELD_TILL_RETURN_GIFT_CARD_AMOUNT, getGiftCardReturnAmount(totals));
        sql.addColumn(FIELD_TILL_GIFT_CARD_RETURN_COUNT, getGiftCardReturnCount(totals));
        sql.addColumn(FIELD_TILL_GROSS_TAXABLE_ITEM_SALES_AMOUNT, getTaxableItemSalesAmount(totals));
        sql.addColumn(FIELD_TILL_GROSS_TAXABLE_ITEM_SALES_COUNT, getTaxableItemSalesCount(totals));
        sql.addColumn(FIELD_TILL_GROSS_TAXABLE_ITEM_RETURNS_AMOUNT, getTaxableItemReturnsAmount(totals));
        sql.addColumn(FIELD_TILL_GROSS_TAXABLE_ITEM_RETURNS_COUNT, getTaxableItemReturnsCount(totals));
        sql.addColumn(FIELD_TILL_GROSS_TAXABLE_NON_MERCHANDISE_SALES_AMOUNT, getTaxableNonMerchandiseSalesAmount(totals));
        sql.addColumn(FIELD_TILL_GROSS_TAXABLE_NON_MERCHANDISE_SALES_COUNT, getTaxableNonMerchandiseSalesCount(totals));
        sql.addColumn(FIELD_TILL_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES_AMOUNT, getNonTaxableNonMerchandiseSalesAmount(totals));
        sql.addColumn(FIELD_TILL_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES_COUNT, getNonTaxableNonMerchandiseSalesCount(totals));
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CARD_ITEM_SALES_AMOUNT, getGiftCardItemSalesAmount(totals));
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CARD_ITEM_SALES_COUNT, getGiftCardItemSalesCount(totals));

        // Tax
        sql.addColumn(FIELD_TILL_TAX_TOTAL_AMOUNT, getNetTaxAmount(totals));
        sql.addColumn(FIELD_TILL_INCLUSIVE_TAX_TOTAL_AMOUNT, getNetInclusiveTaxAmount(totals));
        sql.addColumn(FIELD_TILL_REFUND_TAX_TOTAL_AMOUNT, getTaxRefundedAmount(totals));
        sql.addColumn(FIELD_TILL_REFUND_INCLUSIVE_TAX_TOTAL_AMOUNT, getInclusiveTaxRefundedAmount(totals));
        sql.addColumn(FIELD_TILL_RETURN_TAX_TOTAL_AMOUNT, getTaxReturnedAmount(totals));
        sql.addColumn(FIELD_TILL_RETURN_INCLUSIVE_TAX_TOTAL_AMOUNT, getInclusiveTaxReturnedAmount(totals));
        sql.addColumn(FIELD_TILL_ITEM_SALES_TAX_AMOUNT, getItemSalesTaxAmount(totals));
        sql.addColumn(FIELD_TILL_ITEM_SALES_INCLUSIVE_TAX_AMOUNT, getItemSalesInclusiveTaxAmount(totals));
        sql.addColumn(FIELD_TILL_TRANSACTION_SALES_TAX_AMOUNT, getTransactionSalesTaxAmount(totals));
        sql.addColumn(FIELD_TILL_TRANSACTION_SALES_INCLUSIVE_TAX_AMOUNT, getTransactionSalesInclusiveTaxAmount(totals));
        // Misc
            // StoreCouponDiscounts
        sql.addColumn(FIELD_TILL_ITEM_DISCOUNT_STORE_COUPON_AMOUNT,
                      getItemDiscStoreCouponAmount(totals));
        sql.addColumn(FIELD_TILL_ITEM_DISCOUNT_STORE_COUPON_COUNT,
                      getItemDiscStoreCouponCount(totals));
        sql.addColumn(FIELD_TILL_TRANSACTION_DISCOUNT_STORE_COUPON_AMOUNT,
                      getTransactionDiscStoreCouponAmount(totals));
        sql.addColumn(FIELD_TILL_TRANSACTION_DISCOUNT_STORE_COUPON_COUNT,
                      getTransactionDiscStoreCouponCount(totals));
            //

        sql.addColumn(FIELD_TILL_MISCELLANEOUS_DISCOUNT_TOTAL_AMOUNT,
                      getTransactionDiscountAmount(totals));
        sql.addColumn(FIELD_TILL_TOTAL_MISCELLANEOUS_DISCOUNT_COUNT,
                      getTransactionDiscountCount(totals));
        sql.addColumn(FIELD_TILL_DISCOUNT_TOTAL_AMOUNT, getDiscountAmount(totals));
        sql.addColumn(FIELD_TILL_MARKDOWN_TOTAL_AMOUNT, getMarkdownAmount(totals));
        sql.addColumn(FIELD_TILL_TOTAL_DISCOUNT_COUNT, getDiscountCount(totals));
        sql.addColumn(FIELD_TILL_TOTAL_MARKDOWN_COUNT, getMarkdownCount(totals));
        sql.addColumn(FIELD_TILL_POST_TRANSACTION_VOID_TOTAL_AMOUNT, getPostVoidAmount(totals));
        sql.addColumn(FIELD_TILL_TOTAL_POST_TRANSACTION_VOID_COUNT, getPostVoidCount(totals));
        sql.addColumn(FIELD_TILL_LINE_ITEM_VOID_TOTAL_AMOUNT, getLineVoidAmount(totals));
        sql.addColumn(FIELD_TILL_TOTAL_LINE_ITEM_VOID_COUNT, getLineVoidCount(totals));
        sql.addColumn(FIELD_TILL_TRANSACTION_VOID_TOTAL_AMOUNT, getVoidAmount(totals));
        sql.addColumn(FIELD_TILL_TOTAL_TRANSACTION_VOID_COUNT, getVoidCount(totals));
        sql.addColumn(FIELD_TILL_TOTAL_NO_SALE_TRANSACTION_COUNT, getNoSaleCount(totals));
        sql.addColumn(FIELD_TILL_TOTAL_TENDER_PICKUP_COUNT, getTenderPickupCount(totals));
        sql.addColumn(FIELD_TILL_TENDER_PICKUP_TOTAL_AMOUNT, getTenderPickupAmount(totals));
        sql.addColumn(FIELD_TILL_TOTAL_TENDER_LOAN_COUNT, getTenderLoanCount(totals));
        sql.addColumn(FIELD_TILL_TENDER_LOAN_TOTAL_AMOUNT, getTenderLoanAmount(totals));
        sql.addColumn(FIELD_TILL_HOUSE_PAYMENT_AMOUNT, getHousePaymentAmount(totals));
        sql.addColumn(FIELD_TILL_HOUSE_PAYMENT_COUNT, getHousePaymentCount(totals));
        sql.addColumn(FIELD_TILL_RESTOCKING_FEE_AMOUNT, getRestockingFeeAmount(totals));
        sql.addColumn(FIELD_TILL_RESTOCKING_FEE_COUNT, getRestockingFeeCount(totals));
        sql.addColumn(FIELD_TILL_RESTOCKING_FEE_AMOUNT_FROM_NON_TAXABLE, getRestockingFeeFromNonTaxableAmount(totals));
        sql.addColumn(FIELD_TILL_RESTOCKING_FEE_COUNT_FROM_NON_TAXABLE, getRestockingFeeFromNonTaxableCount(totals));
        sql.addColumn(FIELD_TILL_GIFT_CERTIFICATE_ISSUED_AMOUNT, getGiftCertificateIssuedAmount(totals));
        sql.addColumn(FIELD_TILL_GIFT_CERTIFICATE_ISSUED_COUNT, getGiftCertificateIssuedCount(totals));
        sql.addColumn(FIELD_TILL_GIFT_CARD_ISSUED_AMOUNT, getGiftCardIssuedAmount(totals));
        sql.addColumn(FIELD_TILL_GIFT_CARD_ISSUED_COUNT, getGiftCardIssuedCount(totals));
        sql.addColumn(FIELD_TILL_GIFT_CARD_RELOADED_AMOUNT, getGiftCardReloadedAmount(totals));
        sql.addColumn(FIELD_TILL_GIFT_CARD_RELOADED_COUNT, getGiftCardReloadedCount(totals));
        sql.addColumn(FIELD_TILL_GIFT_CARD_REDEEMED_AMOUNT, getGiftCardRedeemedAmount(totals));
        sql.addColumn(FIELD_TILL_GIFT_CARD_REDEEMED_COUNT, getGiftCardRedeemedCount(totals));
        sql.addColumn(FIELD_TILL_GIFT_CARD_ISSUE_VOIDED_AMOUNT, getGiftCardIssueVoidedAmount(totals));
        sql.addColumn(FIELD_TILL_GIFT_CARD_ISSUE_VOIDED_COUNT, getGiftCardIssueVoidedCount(totals));
        sql.addColumn(FIELD_TILL_GIFT_CARD_RELOAD_VOIDED_AMOUNT, getGiftCardReloadVoidedAmount(totals));
        sql.addColumn(FIELD_TILL_GIFT_CARD_RELOAD_VOIDED_COUNT, getGiftCardReloadVoidedCount(totals));
        sql.addColumn(FIELD_TILL_GIFT_CARD_REDEEM_VOIDED_AMOUNT, getGiftCardRedeemVoidedAmount(totals));
        sql.addColumn(FIELD_TILL_GIFT_CARD_REDEEM_VOIDED_COUNT, getGiftCardRedeemVoidedCount(totals));

        sql.addColumn(FIELD_TILL_HOUSE_ACCOUNT_ENROLLMENT_APPROVED_COUNT,
          safeSQLCast(getHouseCardEnrollmentsApprovalCount(totals)));
        sql.addColumn(FIELD_TILL_HOUSE_ACCOUNT_ENROLLMENT_DECLINED_COUNT,
          safeSQLCast(getHouseCardEnrollmentsDeclinedCount(totals)));

        sql.addColumn(FIELD_TILL_GROSS_GIFT_CARD_ITEM_CREDIT_AMOUNT, getAmountGrossGiftCardItemCredit(totals));
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CARD_ITEM_CREDIT_UNITS, getUnitsGrossGiftCardItemCredit(totals));
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CARD_ITEM_CREDIT_VOIDED_AMOUNT, getAmountGrossGiftCardItemCreditVoided(totals));
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CARD_ITEM_CREDIT_VOIDED_UNITS, getUnitsGrossGiftCardItemCreditVoided(totals));
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CERTIFICATE_REDEEMED_AMOUNT, getAmountGrossGiftCertificatesRedeemed(totals));
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CERTIFICATE_REDEEMED_UNITS, getUnitsGrossGiftCertificatesRedeemed(totals));
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CERTIFICATE_REDEEMED_VOIDED_AMOUNT, getAmountGrossGiftCertificatesRedeemedVoided(totals));
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CERTIFICATE_REDEEMED_VOIDED_UNITS, getUnitsGrossGiftCertificatesRedeemedVoided(totals));
        sql.addColumn(FIELD_TILL_GROSS_STORE_CREDITS_ISSUED_AMOUNT, getAmountGrossStoreCreditsIssued(totals));
        sql.addColumn(FIELD_TILL_GROSS_STORE_CREDITS_ISSUED_UNITS, getUnitsGrossStoreCreditsIssued(totals));
        sql.addColumn(FIELD_TILL_GROSS_STORE_CREDITS_ISSUED_VOIDED_AMOUNT, getAmountGrossStoreCreditsIssuedVoided(totals));
        sql.addColumn(FIELD_TILL_GROSS_STORE_CREDITS_ISSUED_VOIDED_UNITS, getUnitsGrossStoreCreditsIssuedVoided(totals));
        sql.addColumn(FIELD_TILL_GROSS_STORE_CREDITS_REDEEMED_AMOUNT, getAmountGrossStoreCreditsRedeemed(totals));
        sql.addColumn(FIELD_TILL_GROSS_STORE_CREDITS_REDEEMED_UNITS, getUnitsGrossStoreCreditsRedeemed(totals));
        sql.addColumn(FIELD_TILL_GROSS_STORE_CREDITS_REDEEMED_VOIDED_AMOUNT, getAmountGrossStoreCreditsRedeemedVoided(totals));
        sql.addColumn(FIELD_TILL_GROSS_STORE_CREDITS_REDEEMED_VOIDED_UNITS, getUnitsGrossStoreCreditsRedeemedVoided(totals));
        sql.addColumn(FIELD_TILL_GROSS_ITEM_EMPLOYEE_DISCOUNT_AMOUNT, getAmountGrossItemEmployeeDiscount(totals));
        sql.addColumn(FIELD_TILL_GROSS_ITEM_EMPLOYEE_DISCOUNT_UNITS, getUnitsGrossItemEmployeeDiscount(totals));
        sql.addColumn(FIELD_TILL_GROSS_ITEM_EMPLOYEE_DISCOUNT_VOIDED_AMOUNT, getAmountGrossItemEmployeeDiscountVoided(totals));
        sql.addColumn(FIELD_TILL_GROSS_ITEM_EMPLOYEE_DISCOUNT_VOIDED_UNITS, getUnitsGrossItemEmployeeDiscountVoided(totals));
        sql.addColumn(FIELD_TILL_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_AMOUNT, getAmountGrossTransactionEmployeeDiscount(totals));
        sql.addColumn(FIELD_TILL_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_UNITS, getUnitsGrossTransactionEmployeeDiscount(totals));
        sql.addColumn(FIELD_TILL_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_VOIDED_AMOUNT, getAmountGrossTransactionEmployeeDiscountVoided(totals));
        sql.addColumn(FIELD_TILL_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_VOIDED_UNITS, getUnitsGrossTransactionEmployeeDiscountVoided(totals));
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CERTIFICATE_ISSUED_VOIDED_AMOUNT, getAmountGrossGiftCertificateIssuedVoided(totals));
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CERTIFICATE_ISSUED_VOIDED_COUNT, getUnitsGrossGiftCertificateIssuedVoided(totals));
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CERTIFICATE_TENDERED_AMOUNT, getAmountGrossGiftCertificateTendered(totals));
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CERTIFICATE_TENDERED_COUNT, getUnitsGrossGiftCertificateTendered(totals));
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CERTIFICATE_TENDERED_VOIDED_AMOUNT, getAmountGrossGiftCertificateTenderedVoided(totals));
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CERTIFICATE_TENDERED_VOIDED_COUNT, getUnitsGrossGiftCertificateTenderedVoided(totals));
        sql.addColumn(FIELD_TILL_EMPLOYEE_DISCOUNT_TOTAL_AMOUNT, getAmountEmployeeDiscounts(totals));
        sql.addColumn(FIELD_TILL_TOTAL_EMPLOYEE_DISCOUNT_COUNT, getUnitsEmployeeDiscounts(totals));
        sql.addColumn(FIELD_TILL_GROSS_CUSTOMER_DISCOUNTS_AMOUNT, getAmountCustomerDiscounts(totals));
        sql.addColumn(FIELD_TILL_GROSS_CUSTOMER_DISCOUNTS_COUNT, getUnitsCustomerDiscounts(totals));
        sql.addColumn(FIELD_TILL_PRICE_OVERRIDES_AMOUNT, getAmountPriceOverrides(totals));
        sql.addColumn(FIELD_TILL_PRICE_OVERRIDES_COUNT, getUnitsPriceOverrides(totals));
        sql.addColumn(FIELD_TILL_PRICE_ADJUSTMENTS_COUNT, getUnitsPriceAdjustments(totals));
        sql.addColumn(FIELD_TILL_TRANSACTIONS_WITH_RETURNED_ITEMS_COUNT, getCountTransactionsWithReturnedItems(totals));

        if (totals != null)
        {
            sql.addColumn(FIELD_TILL_TOTAL_LAYAWAY_PAYMENTS_COLLECTED_COUNT,
                          getColumnValue(totals.getCountLayawayPayments(),
                                         FIELD_TILL_TOTAL_LAYAWAY_PAYMENTS_COLLECTED_COUNT));
            sql.addColumn(FIELD_TILL_LAYAWAY_PAYMENTS_COLLECTED_TOTAL_AMOUNT,
                          getColumnValue(totals.getAmountLayawayPayments(),
                                         FIELD_TILL_LAYAWAY_PAYMENTS_COLLECTED_TOTAL_AMOUNT));
            sql.addColumn(FIELD_TILL_LAYAWAY_NEW_TOTAL_AMOUNT,
                    	  getColumnValue(totals.getAmountLayawayNew(),
                    			  FIELD_TILL_LAYAWAY_NEW_TOTAL_AMOUNT));
            sql.addColumn(FIELD_TILL_LAYAWAY_PICKUP_TOTAL_AMOUNT,
            			  getColumnValue(totals.getAmountLayawayPickup(),
            					  FIELD_TILL_LAYAWAY_PICKUP_TOTAL_AMOUNT));
            sql.addColumn(FIELD_TILL_TOTAL_LAYAWAY_DELETIONS_DISBURSEMENT_COUNT,
                          getColumnValue(totals.getCountLayawayDeletions(),
                                         FIELD_TILL_TOTAL_LAYAWAY_DELETIONS_DISBURSEMENT_COUNT));
            sql.addColumn(FIELD_TILL_LAYAWAY_DELETIONS_DISBURSEMENT_TOTAL_AMOUNT,
                          getColumnValue(totals.getAmountLayawayDeletions(),
                                         FIELD_TILL_LAYAWAY_DELETIONS_DISBURSEMENT_TOTAL_AMOUNT));
            sql.addColumn(FIELD_TILL_LAYAWAY_INITIATION_FEES_COLLECTED_TOTAL_AMOUNT,
                          getColumnValue(totals.getAmountLayawayInitiationFees(),
                                         FIELD_TILL_LAYAWAY_INITIATION_FEES_COLLECTED_TOTAL_AMOUNT));
            sql.addColumn(FIELD_TILL_TOTAL_LAYAWAY_INITIATION_FEES_COLLECTED_COUNT,
                          getColumnValue(totals.getCountLayawayInitiationFees(),
                                         FIELD_TILL_TOTAL_LAYAWAY_INITIATION_FEES_COLLECTED_COUNT));
            sql.addColumn(FIELD_TILL_LAYAWAY_DELETION_FEES_COLLECTED_TOTAL_AMOUNT,
                          getColumnValue(totals.getAmountLayawayDeletionFees(),
                                         FIELD_TILL_LAYAWAY_DELETION_FEES_COLLECTED_TOTAL_AMOUNT));
            sql.addColumn(FIELD_TILL_TOTAL_LAYAWAY_DELETION_FEES_COUNT,
                          getColumnValue(totals.getCountLayawayDeletionFees(),
                                         FIELD_TILL_TOTAL_LAYAWAY_DELETION_FEES_COUNT));
            sql.addColumn(FIELD_STORE_SPECIAL_ORDER_NEW_TOTAL_AMOUNT,
            			  getColumnValue(totals.getAmountSpecialOrderNew(),
            					  		 FIELD_STORE_SPECIAL_ORDER_NEW_TOTAL_AMOUNT));
            sql.addColumn(FIELD_STORE_SPECIAL_ORDER_PARTIAL_TOTAL_AMOUNT,
            			  getColumnValue(totals.getAmountSpecialOrderPartial(),
            					  		 FIELD_STORE_SPECIAL_ORDER_PARTIAL_TOTAL_AMOUNT));
            sql.addColumn(FIELD_TILL_TOTAL_ORDER_PAYMENTS_COLLECTED_COUNT,
                          getColumnValue(totals.getCountOrderPayments(),
                                         FIELD_TILL_TOTAL_ORDER_PAYMENTS_COLLECTED_COUNT));
            sql.addColumn(FIELD_TILL_ORDER_PAYMENTS_COLLECTED_TOTAL_AMOUNT,
                          getColumnValue(totals.getAmountOrderPayments(),
                                         FIELD_TILL_ORDER_PAYMENTS_COLLECTED_TOTAL_AMOUNT));
            sql.addColumn(FIELD_TILL_TOTAL_ORDER_CANCELS_DISBURSEMENT_COUNT,
                          getColumnValue(totals.getCountOrderCancels(),
                                         FIELD_TILL_TOTAL_ORDER_CANCELS_DISBURSEMENT_COUNT));
            sql.addColumn(FIELD_TILL_ORDER_CANCELS_DISBURSEMENT_TOTAL_AMOUNT,
                          getColumnValue(totals.getAmountOrderCancels(),
                                         FIELD_TILL_ORDER_CANCELS_DISBURSEMENT_TOTAL_AMOUNT));
            sql.addColumn(FIELD_TILL_SHIPPING_CHARGE_TOTAL_AMOUNT,
                          getColumnValue(totals.getAmountShippingCharges(),
                                         FIELD_TILL_SHIPPING_CHARGE_TOTAL_AMOUNT));
            sql.addColumn(FIELD_TILL_TOTAL_SHIPPING_CHARGE_COUNT,
                          getColumnValue(totals.getNumberShippingCharges(),
                                         FIELD_TILL_TOTAL_SHIPPING_CHARGE_COUNT));
            sql.addColumn(FIELD_TILL_SHIPPING_CHARGE_TAX_AMOUNT,
                    getColumnValue(totals.getAmountTaxShippingCharges(),
                    					 FIELD_TILL_SHIPPING_CHARGE_TAX_AMOUNT));
            sql.addColumn(FIELD_TILL_SHIPPING_CHARGE_INCLUSIVE_TAX_AMOUNT,
                    getColumnValue(totals.getAmountInclusiveTaxShippingCharges(),
                    					 FIELD_TILL_SHIPPING_CHARGE_INCLUSIVE_TAX_AMOUNT));
            sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_IN_TOTAL_AMOUNT,
                          getColumnValue(totals.getAmountTillPayIns(),
                                         FIELD_TILL_FUNDS_RECEIVED_IN_TOTAL_AMOUNT));
            sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_OUT_TOTAL_AMOUNT,
                          getColumnValue(totals.getAmountTillPayOuts(),
                                         FIELD_TILL_FUNDS_RECEIVED_OUT_TOTAL_AMOUNT));
            sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_IN_UNIT_COUNT,
                          getColumnValue(totals.getCountTillPayIns(),
                                         FIELD_TILL_FUNDS_RECEIVED_IN_UNIT_COUNT));
            sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_OUT_UNIT_COUNT,
                          getColumnValue(totals.getCountTillPayOuts(),
                                         FIELD_TILL_FUNDS_RECEIVED_OUT_UNIT_COUNT));
            sql.addColumn(FIELD_GROSS_TAXABLE_SALES_VOID_COUNT, getTaxableSalesVoidCount(totals));
            sql.addColumn(FIELD_GROSS_TAXABLE_RETURNS_VOID_COUNT, getTaxableReturnsVoidCount(totals));
            sql.addColumn(FIELD_GROSS_NONTAXABLE_SALES_VOID_COUNT, getNonTaxableSalesVoidCount(totals));
            sql.addColumn(FIELD_GROSS_NONTAXABLE_RETURNS_VOID_COUNT, getNonTaxableReturnsVoidCount(totals));
            
            sql.addColumn(FIELD_STORE_TOTAL_BILLPAYMENT, 
                          getColumnValue(totals.getAmountBillPayments(), 
                                  FIELD_STORE_TOTAL_BILLPAYMENT));
            sql.addColumn(FIELD_STORE_TOTAL_BILLPAYMENT_COUNT, 
                          getColumnValue(totals.getCountBillPayments(),
                                  FIELD_STORE_TOTAL_BILLPAYMENT_COUNT));

            sql.addColumn(FIELD_TOTAL_AMOUNT_CHANGE_ROUNDED_IN,
                    getColumnValue(totals.getAmountChangeRoundedIn(),
                    FIELD_TOTAL_AMOUNT_CHANGE_ROUNDED_IN));
            
            sql.addColumn(FIELD_TOTAL_AMOUNT_CHANGE_ROUNDED_OUT,
                    getColumnValue(totals.getAmountChangeRoundedOut(),
                    FIELD_TOTAL_AMOUNT_CHANGE_ROUNDED_OUT));
        }
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
        Returns the till id
        <p>
        @param  till  The till object
        @return the till id
     */
    protected String getTillID(TillIfc till)
    {
        return("'" + till.getTillID() + "'");
    }

    /**
        Returns the operator id
        <p>
        @param  till  The till object
        @return the operator id
     */
    protected String getOperatorID(TillIfc till)
    {
        return("'" + till.getSignOnOperator().getEmployeeID() + "'");
    }

    /**
        Returns the string representation of the start time
        <p>
        @param  till  A till
        @return the string representation of the start time
     */
    protected String getStartTimestamp(TillIfc till)
    {
        return(dateToSQLTimestampString(till.getOpenTime().dateValue()));
    }

    /**
        Returns the status of the till
        <p>
        @param  till  A till
        @return the status of the till
     */
    protected String getStatusCode(TillIfc till)
    {
        return(Integer.toString(till.getStatus()));
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_TRANSACTION_COUNT + " + " + safeSQLCast(value);
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
            value = totals.getAmountLineVoids().abs().getStringValue();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_LINE_ITEM_VOID_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_LINE_ITEM_VOID_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_POST_TRANSACTION_VOID_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_POST_TRANSACTION_VOID_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TRANSACTION_VOID_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_TRANSACTION_VOID_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_NO_SALE_TRANSACTION_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_MISCELLANEOUS_DISCOUNT_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_MISCELLANEOUS_DISCOUNT_COUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
        Returns the monetary amount of TransactionDiscStoreCoupons
        <p>
        @param  totals  The financial totals to extract the information from
        @return the monetary amount of TransactionDiscStoreCoupons
     */
    protected String getTransactionDiscStoreCouponAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountTransactionDiscStoreCoupons().getStringValue();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TRANSACTION_DISCOUNT_STORE_COUPON_AMOUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
        Returns the number of TransactionDiscStoreCoupons
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of TransactionDiscStoreCoupons
     */
    protected String getTransactionDiscStoreCouponCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getNumberTransactionDiscStoreCoupons());
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TRANSACTION_DISCOUNT_STORE_COUPON_COUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
        Returns the monetary amount of ItemDiscStoreCoupons
        <p>
        @param  totals  The financial totals to extract the information from
        @return the monetary amount of ItemDiscStoreCoupons
     */
    protected String getItemDiscStoreCouponAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountItemDiscStoreCoupons().getStringValue();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_ITEM_DISCOUNT_STORE_COUPON_AMOUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
        Returns the number of ItemDiscStoreCoupons
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of ItemDiscStoreCoupons
     */
    protected String getItemDiscStoreCouponCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = String.valueOf(totals.getNumberItemDiscStoreCoupons());
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_ITEM_DISCOUNT_STORE_COUPON_COUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
        Returns the monetary amount of price discounts
        @param  totals  The financial totals to extract the information from
        @return the monetary amount of price markdowns
     */
    protected String getDiscountAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountItemDiscounts().getStringValue();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_DISCOUNT_TOTAL_AMOUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
        Returns the number of price discounts
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_DISCOUNT_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_MARKDOWN_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_MARKDOWN_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_SALES_EX_TAX_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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
            value = totals.getAmountNetNonTaxableTransactionSales().getStringValue();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_NONTAXABLE_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_NONTAXABLE_COUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
        Returns the taxable count
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_TAXABLE_COUNT + " + " + safeSQLCast(value);
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
            value = totals.getAmountGrossTaxExemptTransactionSales()
                          .subtract(totals.getAmountGrossTaxExemptTransactionSalesVoided())
                          .subtract(totals.getAmountGrossTaxExemptTransactionReturns()
                               .subtract(totals.getAmountGrossTaxExemptTransactionReturnsVoided())).getStringValue();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TAX_EXEMPT_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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
                                   + (totals.getCountGrossTaxExemptTransactionReturns()
                                      - totals.getCountGrossTaxExemptTransactionReturnsVoided()));
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_TAX_EXEMPT_TRANSACTION_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_REFUND_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_REFUND_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_REFUND_NONTAXABLE_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_NONTAXABLE_REFUND_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_REFUND_TAX_EXEMPT_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_TAX_EXEMPT_REFUND_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_LINE_ITEM_SALES_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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
            BigDecimal total = totals.getUnitsGrossTaxableItemSales()
                                     .subtract(totals.getUnitsGrossTaxableItemSalesVoided());
            total = total.add(totals.getUnitsGrossNonTaxableItemSales()
                                    .subtract(totals.getUnitsGrossNonTaxableItemSalesVoided()));
            value = total.toString();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_SALE_LINE_ITEM_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_LINE_ITEM_NONTAXABLE_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_NONTAXABLE_LINE_ITEM_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_LINE_ITEM_TAX_EXEMPT_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_TAX_EXEMPT_LINE_ITEM_COUNT + " + " + safeSQLCast(value);
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
            CurrencyIfc nonTaxable = totals.getAmountGrossNonTaxableTransactionReturns()
                                           .subtract(totals.getAmountGrossNonTaxableTransactionReturnsVoided());
            value = totals.getAmountGrossTaxableTransactionReturns()
                          .subtract(totals.getAmountGrossTaxableTransactionReturnsVoided()).add(nonTaxable).getStringValue();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_RETURN_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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
            value = String.valueOf((totals.getCountGrossTaxableTransactionReturns() - totals.getCountGrossTaxableTransactionReturnsVoided())
                                  + (totals.getCountGrossNonTaxableTransactionReturns() - totals.getCountGrossNonTaxableTransactionReturnsVoided()));
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_RETURN_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_RETURN_NONTAXABLE_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_NONTAXABLE_RETURN_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_RETURN_TAX_EXEMPT_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_TAX_EXEMPT_RETURN_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_NONMERCH_NONTAX_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_NONMERCH_NONTAX_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_RETURN_NONMERCH_NONTAX_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_NONMERCH_NONTAX_RETURN_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_NONMERCH_TAX_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_NONMERCH_TAX_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_RETURN_NONMERCH_TAX_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_NONMERCH_TAX_RETURN_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GIFT_CARD_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GIFT_CARD_COUNT + " + " + safeSQLCast(value);
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
            value = totals.getAmountGrossGiftCardItemReturns().getStringValue();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_RETURN_GIFT_CARD_AMOUNT + " + " + safeSQLCast(value);
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
            value = totals.getUnitsGrossGiftCardItemReturns().toString();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GIFT_CARD_RETURN_COUNT + " + " + safeSQLCast(value);
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
            CurrencyIfc taxReturns = totals.getAmountTaxTransactionReturns();
            value = totals.getAmountTaxTransactionSales().subtract(taxReturns).getStringValue();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TAX_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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
            CurrencyIfc taxReturns = totals.getAmountInclusiveTaxTransactionReturns();
            value = totals.getAmountInclusiveTaxTransactionSales().subtract(taxReturns).getStringValue();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_INCLUSIVE_TAX_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_REFUND_TAX_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_REFUND_INCLUSIVE_TAX_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_RETURN_TAX_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_RETURN_INCLUSIVE_TAX_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_TENDER_PICKUP_COUNT + " + " + safeSQLCast(value);
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
        String countryCode = DomainGateway.getBaseCurrencyType().getCountryCode();
        return getTenderPickupAmount(totals, countryCode);
    }

    /**
        Returns the amount of tender pickups
        <p>
        @param  totals  The financial totals to extract the information from
        @param countryCode String
        @return the amount of tender pickups
     */
    protected String getTenderPickupAmount(FinancialTotalsIfc totals, String countryCode)
    {
        String value = "0";

        if (totals != null)
        {
            value = totals.getAmountTillPickups(countryCode).getStringValue();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TENDER_PICKUP_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_TENDER_LOAN_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TENDER_LOAN_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_HOUSE_PAYMENT_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_HOUSE_PAYMENT_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_RESTOCKING_FEE_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_RESTOCKING_FEE_AMOUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
        Returns the number of restocking fees from non taxable items
        <p>
        @param  totals  The financial totals to extract the information from
        @return the number of restocking fees
     */
    protected String getRestockingFeeFromNonTaxableCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsRestockingFeesFromNonTaxableItems().toString();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_RESTOCKING_FEE_COUNT_FROM_NON_TAXABLE + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
        Returns the amount of restocking fees from non taxable items
        <p>
        @param  totals  The financial totals to extract the information from
        @return the amount of restocking fees
     */
    protected String getRestockingFeeFromNonTaxableAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountRestockingFeesFromNonTaxableItems().getStringValue();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_RESTOCKING_FEE_AMOUNT_FROM_NON_TAXABLE + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
        Returns the proper column value for the given CurrencyIfc value,
        column name. <P>
        @param amount amount to be set in the column
        @param columnName name of the column
        @return columnValue
     */
    protected String getColumnValue(CurrencyIfc amount,
                                    String columnName)
    {
        String value = amount.getStringValue();

        if (isUpdateStatement)
        {
            value = columnName + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
        Returns the proper column value for the given int value,
        column name. <P>
        @param count count to be set in the column
        @param columnName name of the column
        @return columnValue
     */
    protected String getColumnValue(int count,
                                    String columnName)
    {
        String value = Integer.toString(count);

        if (isUpdateStatement)
        {
            value = columnName + " + " + safeSQLCast(value);
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
        @param  till        The till
        @param  tenderType  The type of tender
        @return the tender deposit amount
     */
    protected String getTenderDepositAmount(TillIfc till, String tenderType)
    {
        // Not tracking this yet.
        return("0");
    }

    /**
     Returns the tender loan amount
     <p>
     @param  loans       The till loans
     @param  tenderDesc  The TenderDescriptor
     @return the tender loan amount
     */
    protected String getTenderLoanAmount(ReconcilableCountIfc loans, TenderDescriptorIfc tenderDesc)
    {
        String value = "0";

        FinancialCountIfc count = loans.getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderDesc);

        if (item != null)
        {
            value = item.getAmountIn().subtract(item.getAmountOut()).getStringValue();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TENDER_LOAN_MEDIA_TOTAL_AMOUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
     Returns the till pay-in amount
     <p>
     @param  payIns       The till pay-ins
     @param  tenderDesc  The TenderDescriptor
     @return the tender payIn amount
     */
    protected String getTillPayInAmount(ReconcilableCountIfc payIns,
            TenderDescriptorIfc tenderDesc)
    {
        String value = "0";

        FinancialCountIfc count = payIns.getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderDesc);

        if (item != null)
        {
            value = item.getAmountIn().subtract(item.getAmountOut()).getStringValue();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_FUNDS_RECEIVED_IN_MEDIA_TOTAL_AMOUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
     Returns the till pay-out amount
     <p>
     @param  payOuts       The till pay-outs
     @param  tenderDesc  The TenderDescriptor
     @return the tender payOut amount
     */
    protected String getTillPayOutAmount(ReconcilableCountIfc payOuts,
            TenderDescriptorIfc tenderDesc)
    {
        String value = "0";

        FinancialCountIfc count = payOuts.getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderDesc);

        if (item != null)
        {
            value = item.getAmountOut().subtract(item.getAmountIn()).getStringValue();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_FUNDS_RECEIVED_OUT_MEDIA_TOTAL_AMOUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
     Returns the tender over amount
     <p>
     @param  till        The till
     @param  tenderDesc  The TenderDescriptor
     @return the tender over amount
     */
    protected String getTenderOverAmount(TillIfc till, TenderDescriptorIfc tenderDesc)
    {
        FinancialCountIfc expectedCount = till.getTotals().getCombinedCount().getExpected();
        FinancialCountTenderItemIfc expectedTender = expectedCount.getSummaryTenderItemByDescriptor(tenderDesc);

        FinancialCountIfc enteredCount = till.getTotals().getCombinedCount().getEntered();
        FinancialCountTenderItemIfc enteredTender = enteredCount.getSummaryTenderItemByDescriptor(tenderDesc);

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

        String value = amount.getStringValue();

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TENDER_OVER_TOTAL_AMOUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
     Returns the tender pickup amount
     <p>
     @param  pickups     The till pickups
     @param  tenderDesc  The TenderDescriptor
     @return the tender pickup amount
     */
    protected String getTenderPickupAmount(ReconcilableCountIfc pickups, TenderDescriptorIfc tenderDesc)
    {
        String value = "0";

        FinancialCountIfc count = pickups.getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderDesc);

        if (item != null)
        {
            value = item.getAmountOut().subtract(item.getAmountIn()).getStringValue();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TENDER_PICKUP_MEDIA_TOTAL_AMOUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
     Returns the tender short amount
     <p>
     @param  till        The till
     @param  tenderDesc  The TenderDescriptor
     @return the tender short amount
     */
    protected String getTenderShortAmount(TillIfc till, TenderDescriptorIfc tenderDesc)
    {
        FinancialCountIfc expectedCount = till.getTotals().getCombinedCount().getExpected();
        FinancialCountTenderItemIfc expectedTender = expectedCount.getSummaryTenderItemByDescriptor(tenderDesc);

        FinancialCountIfc enteredCount = till.getTotals().getCombinedCount().getEntered();
        FinancialCountTenderItemIfc enteredTender = enteredCount.getSummaryTenderItemByDescriptor(tenderDesc);

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

        String value = amount.getStringValue();

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TENDER_SHORT_TOTAL_AMOUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
     Returns the tender beginning count
     <p>
     @param  till        The till
     @param  tenderDesc  The TenderDescriptor
     @return the tender beginning count
     */
    protected String getTenderBeginningCount(TillIfc till, TenderDescriptorIfc tenderDesc)
    {
        String value = "0";

        FinancialCountIfc count = till.getTotals().getStartingFloatCount().getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderDesc);

        if (item != null)
        {
            value = String.valueOf(item.getNumberItemsIn());
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_BEGINNING_TENDER_MEDIA_UNIT_COUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
        Returns the tender deposit count
        <p>
        @param  till        The till
        @param  tenderType  The type of tender
        @return the tender deposit count
     */
    protected String getTenderDepositCount(TillIfc till, String tenderType)
    {
        // Not tracking this yet
        return("0");
    }

    /**
     Returns the tender loan count
     <p>
     @param  loans       The till loans
     @param  tenderDesc  The TenderDescriptor
     @return the tender loan count
     */
    protected String getTenderLoanCount(ReconcilableCountIfc loans, TenderDescriptorIfc tenderDesc)
    {
        String value = "0";

        FinancialCountIfc count = loans.getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderDesc);

        if (item != null)
        {
            value = String.valueOf(item.getNumberItemsIn() - item.getNumberItemsOut());
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_TENDER_LOAN_MEDIA_UNIT_COUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
     Returns the tender pay-in count
     <p>
     @param  payIns       The till pay-ins
     @param  tenderDesc   The TenderDescriptor
     @return the tender payIn count
     */
    protected String getTenderPayInCount(ReconcilableCountIfc payIns,
            TenderDescriptorIfc tenderDesc)
    {
        String value = "0";

        FinancialCountIfc count = payIns.getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderDesc);

        if (item != null)
        {
            value = String.valueOf(item.getNumberItemsIn());
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_FUNDS_RECEIVED_IN_MEDIA_UNIT_COUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
     Returns the tender pay-out count
     <p>
     @param  payOuts       The till pay-outs
     @param  tenderDesc  The TenderDescriptor
     @return the tender payOut count
     */
    protected String getTenderPayOutCount(ReconcilableCountIfc payOuts,
            TenderDescriptorIfc tenderDesc)
    {
        String value = "0";

        FinancialCountIfc count = payOuts.getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderDesc);

        if (item != null)
        {
            value = String.valueOf(item.getNumberItemsOut());
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_FUNDS_RECEIVED_OUT_MEDIA_UNIT_COUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
     Returns the tender count
     <p>
     @param  till        The till
     @param  tenderDesc  The TenderDescriptor
     @return the tender count
     */
    protected String getTenderCount(TillIfc till, TenderDescriptorIfc tenderDesc)
    {
        String value = "0";

        FinancialCountIfc count = till.getTotals().getTenderCount();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderDesc);

        if (item != null)
        {
            value = String.valueOf(item.getNumberItemsTotal());
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_TENDER_MEDIA_COUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
     Returns the tender over count
     <p>
     @param  till        The till
     @param  tenderDesc  The TenderDescriptor
     @return the tender over count
     */
    protected String getTenderOverCount(TillIfc till, TenderDescriptorIfc tenderDesc)
    {
        FinancialCountIfc expectedCount = till.getTotals().getCombinedCount().getExpected();
        FinancialCountTenderItemIfc expectedTender = expectedCount.getSummaryTenderItemByDescriptor(tenderDesc);

        FinancialCountIfc enteredCount = till.getTotals().getCombinedCount().getEntered();
        FinancialCountTenderItemIfc enteredTender = enteredCount.getSummaryTenderItemByDescriptor(tenderDesc);

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

        String value = String.valueOf(count);

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_TENDER_MEDIA_OVER_COUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
     Returns the tender short count
     <p>
     @param  till        The till
     @param  tenderDesc  The TenderDescriptor
     @return the tender short count
     */
    protected String getTenderShortCount(TillIfc till, TenderDescriptorIfc tenderDesc)
    {
        FinancialCountIfc expectedCount = till.getTotals().getCombinedCount().getExpected();
        FinancialCountTenderItemIfc expectedTender = expectedCount.getSummaryTenderItemByDescriptor(tenderDesc);

        FinancialCountIfc enteredCount = till.getTotals().getCombinedCount().getEntered();
        FinancialCountTenderItemIfc enteredTender = enteredCount.getSummaryTenderItemByDescriptor(tenderDesc);

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

        String value = String.valueOf(count);

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_TENDER_MEDIA_SHORT_COUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
        Returns the tender pickup count
        <p>
        @param  pickups     The till pickups
        @param  descriptor  The tender descriptor
        @return the tender pickup count
     */
    protected String getTenderPickupCount(ReconcilableCountIfc pickups, TenderDescriptorIfc descriptor)
    {
        String value = "0";
        FinancialCountIfc count = pickups.getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(descriptor);

        if (item != null)
        {
            value = String.valueOf(item.getNumberItemsOut() - item.getNumberItemsIn());
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_TENDER_PICKUP_MEDIA_UNIT_COUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
     Returns the till pay-out count
     <p>
     @param  payOuts     The till pay-outs
     @param  tenderDesc  The TenderDescriptor
     @return the till payout count
     */
    protected String getTillPayOutCount(ReconcilableCountIfc payOuts, TenderDescriptorIfc tenderDesc)
    {
        String value = "0";

        FinancialCountIfc count = payOuts.getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderDesc);

        if (item != null)
        {
            value = String.valueOf(item.getNumberItemsOut() - item.getNumberItemsIn());
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_FUNDS_RECEIVED_OUT_MEDIA_UNIT_COUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
     Returns the till pay-out count
     <p>
     @param  payIns     The till pay-ins
     @param  tenderDesc  The TenderDescriptor
     @return the till payin count
     */
    protected String getTillPayInCount(ReconcilableCountIfc payIns, TenderDescriptorIfc tenderDesc)
    {
        String value = "0";

        FinancialCountIfc count = payIns.getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderDesc);

        if (item != null)
        {
            value = String.valueOf(item.getNumberItemsIn() - item.getNumberItemsOut());
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_FUNDS_RECEIVED_IN_MEDIA_UNIT_COUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
     Returns the tender refund count
     <p>
     @param  till        The till
     @param  tenderDesc  The TenderDescriptor
     @return the tender refund count
     */
    protected String getTenderRefundCount(TillIfc till, TenderDescriptorIfc tenderDesc)
    {
        String value = "0";

        FinancialCountIfc count = till.getTotals().getTenderCount();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderDesc);

        if (item != null)
        {
            value = String.valueOf(item.getNumberItemsOut());
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_TENDER_REFUND_MEDIA_UNIT_COUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
     Returns the tender refund amount
     <p>
     @param  till        The till
     @param  tenderDesc  The TenderDescriptor
     @return the tender refund amount
     */
    protected String getTenderRefundAmount(TillIfc till, TenderDescriptorIfc tenderDesc)
    {
        String value = "0";

        FinancialCountIfc count = till.getTotals().getTenderCount();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderDesc);

        if (item != null)
        {
            value = item.getAmountOut().getStringValue();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TENDER_REFUND_TOTAL_AMOUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
     Returns the tender total amount
     <p>
     @param  till        The till
     @param  tenderDesc  The TenderDescriptor
     @return the tender total amount
     */
    protected String getTenderTotalAmount(TillIfc till, TenderDescriptorIfc tenderDesc)
    {
        String value = "0";

        FinancialCountIfc count = till.getTotals().getTenderCount();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderDesc);

        if (item != null)
        {
            value = item.getAmountTotal().getStringValue();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TENDER_TOTAL_AMOUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
     Returns the tender open amount
     <p>
     @param  till        The till
     @param  tenderDesc  The TenderDescriptor
     @return the tender open amount
     */
    protected String getTenderOpenAmount(TillIfc till, TenderDescriptorIfc tenderDesc)
    {
        String value = "0";

        FinancialCountIfc count = till.getTotals().getStartingFloatCount().getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderDesc);

        if (item != null)
        {
            value = item.getAmountIn().getStringValue();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TENDER_OPEN_AMOUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
     Returns the tender close amount
     <p>
     @param  till        The till
     @param  tenderType  The type of tender
     @return the tender close amount
     */
    protected String getTenderCloseAmount(TillIfc till, TenderDescriptorIfc tenderDesc)
    {
        String value = "0";

        FinancialCountIfc count = till.getTotals().getEndingFloatCount().getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderDesc);
        if (item != null)
        {
            value = item.getAmountOut().getStringValue();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TENDER_CLOSE_AMOUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
     Returns the tender close count
     <p>
     @param  till        The till
     @param  tenderDesc  The TenderDescriptor
     @return the tender close count
     */
    protected String getTenderCloseCount(TillIfc till, TenderDescriptorIfc tenderDesc)
    {
        String value = "0";

        FinancialCountIfc count = till.getTotals().getEndingFloatCount().getEntered();
        FinancialCountTenderItemIfc item = count.getSummaryTenderItemByDescriptor(tenderDesc);

        if (item != null)
        {
            value = String.valueOf(item.getNumberItemsOut());
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TENDER_MEDIA_CLOSE_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_TAXABLE_ITEM_SALES_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_TAXABLE_ITEM_RETURNS_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_TAXABLE_ITEM_SALES_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_TAXABLE_ITEM_RETURNS_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_TAXABLE_TRANSACTION_SALES_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_TAXABLE_TRANSACTION_SALES_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_GIFT_CARD_ITEM_SALES_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_GIFT_CARD_ITEM_SALES_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_TAXABLE_NON_MERCHANDISE_SALES_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_TAXABLE_NON_MERCHANDISE_SALES_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_TAXABLE_TRANSACTION_RETURNS_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_TAXABLE_TRANSACTION_RETURNS_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_NON_TAXABLE_TRANSACTION_SALES_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_NON_TAXABLE_TRANSACTION_SALES_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_TAX_EXEMPT_TRANSACTION_SALES_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_TAX_EXEMPT_TRANSACTION_SALES_COUNT + " + " + safeSQLCast(value);
        }

        return value;
    }

         /**
        Returns the item sales tax amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the item sales tax amount
     */
    protected String getItemSalesTaxAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountTaxTransactionSales()
                          .subtract(totals.getAmountTaxTransactionReturns())
                          .add(totals.getAmountTaxItemReturns()).toString();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_ITEM_SALES_TAX_AMOUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
        Returns the item sales inclusive tax amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the item sales inclusive tax amount
     */
    protected String getItemSalesInclusiveTaxAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountInclusiveTaxItemSales().toString();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_ITEM_SALES_INCLUSIVE_TAX_AMOUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
        Returns the transaction sales tax amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the tranaction sales tax amount
     */
    protected String getTransactionSalesTaxAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountTaxTransactionSales().getStringValue();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TRANSACTION_SALES_TAX_AMOUNT + " + " + safeSQLCast(value);
        }

        return value;
    }

    /**
        Returns the transaction sales inclusive tax amount
        <p>
        @param  totals  The financial totals to extract the information from
        @return the tranaction sales inclusive tax amount
     */
    protected String getTransactionSalesInclusiveTaxAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountInclusiveTaxTransactionSales().getStringValue();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_TRANSACTION_SALES_INCLUSIVE_TAX_AMOUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
           {
            value = FIELD_TILL_GIFT_CERTIFICATE_ISSUED_AMOUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }
    /**
     Returns the numbers of gift certificate issued count
     <p>
     @param  totals  The financial totals to extract the information from
     @return the number of gift certificate issued
     */
    protected String getGiftCertificateIssuedCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
           {
            value = totals.getUnitsNetGiftCertificateIssued().toString();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GIFT_CERTIFICATE_ISSUED_COUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
     Returns the string value of gift card issued amount
     <p>
     @param totals FinancialTotalsIfc container of value to be converted
     @return String the value as a string
     */
    protected String getGiftCardIssuedAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getAmountGrossGiftCardItemIssued().getStringValue();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GIFT_CARD_ISSUED_AMOUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }
    /**
     Returns the numbers of gift card issued count
     <p>
     @param  totals  The financial totals to extract the information from
     @return the number of gift card issued
     */
    protected String getGiftCardIssuedCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
           {
            value = totals.getUnitsGrossGiftCardItemIssued().toString();
        }

        if (isUpdateStatement)
           {
            value = FIELD_TILL_GIFT_CARD_ISSUED_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
           {
            value = FIELD_TILL_GIFT_CARD_RELOADED_AMOUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }
    /**
     Returns the numbers of gift card issued count
     <p>
     @param  totals  The financial totals to extract the information from
     @return the number of gift card issued
     */
    protected String getGiftCardReloadedCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsGrossGiftCardItemReloaded().toString();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GIFT_CARD_RELOADED_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
           {
            value = FIELD_TILL_GIFT_CARD_REDEEMED_AMOUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }
    /**
     Returns the numbers of gift card issued count
     <p>
     @param  totals  The financial totals to extract the information from
     @return the number of gift card issued
     */
    protected String getGiftCardRedeemedCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
           {
            value = totals.getUnitsGrossGiftCardItemRedeemed().toString();
        }

        if (isUpdateStatement)
           {
            value = FIELD_TILL_GIFT_CARD_REDEEMED_COUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    //start from here
    /**
     Returns the string value of gift card issue void amount
     <p>
     @param totals FinancialTotalsIfc container of value to be converted
     @return String the value as a string
     */
    protected String getGiftCardIssueVoidedAmount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
           {
            value = totals.getAmountGrossGiftCardItemIssueVoided().getStringValue();
        }

        if (isUpdateStatement)
           {
            value = FIELD_TILL_GIFT_CARD_ISSUE_VOIDED_AMOUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }
    /**
     Returns the numbers of gift card issue voided count
     <p>
     @param  totals  The financial totals to extract the information from
     @return the number of gift card issue voided
     */
    protected String getGiftCardIssueVoidedCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
           {
            value = totals.getUnitsGrossGiftCardItemIssueVoided().toString();
        }

        if (isUpdateStatement)
           {
            value = FIELD_TILL_GIFT_CARD_ISSUE_VOIDED_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
           {
            value = FIELD_TILL_GIFT_CARD_RELOAD_VOIDED_AMOUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }
    /**
     Returns the numbers of gift card reload voided count
     <p>
     @param  totals  The financial totals to extract the information from
     @return the number of gift card issue voided
     */
    protected String getGiftCardReloadVoidedCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsGrossGiftCardItemReloadVoided().toString();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GIFT_CARD_RELOAD_VOIDED_COUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
     Returns the string value of gift card redeemed amount
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GIFT_CARD_REDEEM_VOIDED_AMOUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }
    /**
     Returns the numbers of gift card redeem voided count
     <p>
     @param  totals  The financial totals to extract the information from
     @return the number of gift card redeem voided
     */
    protected String getGiftCardRedeemVoidedCount(FinancialTotalsIfc totals)
    {
        String value = "0";
        if (totals != null)
        {
            value = totals.getUnitsGrossGiftCardItemRedeemedVoided().toString();
        }

        if (isUpdateStatement)
        {
            value = FIELD_TILL_GIFT_CARD_REDEEM_VOIDED_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_HOUSE_ACCOUNT_ENROLLMENT_APPROVED_COUNT + " + " + safeSQLCast(value);
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

        if (isUpdateStatement)
        {
            value = FIELD_TILL_HOUSE_ACCOUNT_ENROLLMENT_DECLINED_COUNT + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_GIFT_CARD_ITEM_CREDIT_AMOUNT + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_GIFT_CARD_ITEM_CREDIT_UNITS + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_GIFT_CARD_ITEM_CREDIT_VOIDED_AMOUNT + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_GIFT_CARD_ITEM_CREDIT_VOIDED_UNITS + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_GIFT_CERTIFICATE_REDEEMED_AMOUNT + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_GIFT_CERTIFICATE_REDEEMED_UNITS + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value =  FIELD_TILL_GROSS_GIFT_CERTIFICATE_REDEEMED_VOIDED_AMOUNT + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value =  FIELD_TILL_GROSS_GIFT_CERTIFICATE_REDEEMED_VOIDED_UNITS + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value =  FIELD_TILL_GROSS_STORE_CREDITS_ISSUED_AMOUNT + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value =  FIELD_TILL_GROSS_STORE_CREDITS_ISSUED_UNITS + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value =  FIELD_TILL_GROSS_STORE_CREDITS_ISSUED_VOIDED_AMOUNT + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value =  FIELD_TILL_GROSS_STORE_CREDITS_ISSUED_VOIDED_UNITS + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value =  FIELD_TILL_GROSS_STORE_CREDITS_REDEEMED_AMOUNT + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value =  FIELD_TILL_GROSS_STORE_CREDITS_REDEEMED_UNITS + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value =  FIELD_TILL_GROSS_STORE_CREDITS_REDEEMED_VOIDED_AMOUNT + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value =  FIELD_TILL_GROSS_STORE_CREDITS_REDEEMED_VOIDED_UNITS + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value =  FIELD_TILL_GROSS_ITEM_EMPLOYEE_DISCOUNT_AMOUNT + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value =  FIELD_TILL_GROSS_ITEM_EMPLOYEE_DISCOUNT_UNITS + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value =  FIELD_TILL_GROSS_ITEM_EMPLOYEE_DISCOUNT_VOIDED_AMOUNT + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value =  FIELD_TILL_GROSS_ITEM_EMPLOYEE_DISCOUNT_VOIDED_UNITS + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_AMOUNT + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_UNITS + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_VOIDED_AMOUNT + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_VOIDED_UNITS + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_GIFT_CERTIFICATE_ISSUED_VOIDED_AMOUNT + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_GIFT_CERTIFICATE_ISSUED_VOIDED_COUNT + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_GIFT_CERTIFICATE_TENDERED_AMOUNT + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_GIFT_CERTIFICATE_TENDERED_COUNT + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_GIFT_CERTIFICATE_TENDERED_VOIDED_AMOUNT + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_GIFT_CERTIFICATE_TENDERED_VOIDED_COUNT + " + " + safeSQLCast(value);
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
            value = ((totals.getAmountGrossTransactionEmployeeDiscount()).add(totals.getAmountGrossItemEmployeeDiscount())).toString();
        }
        if (isUpdateStatement)
        {
            value = FIELD_TILL_EMPLOYEE_DISCOUNT_TOTAL_AMOUNT + " + " + safeSQLCast(value);
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
            value = ((totals.getUnitsGrossTransactionEmployeeDiscount()).add(totals.getUnitsGrossItemEmployeeDiscount())).toString();
        }
        if (isUpdateStatement)
        {
            value = FIELD_TILL_TOTAL_EMPLOYEE_DISCOUNT_COUNT + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_CUSTOMER_DISCOUNTS_AMOUNT + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value = FIELD_TILL_GROSS_CUSTOMER_DISCOUNTS_COUNT + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value = FIELD_TILL_PRICE_OVERRIDES_AMOUNT + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value = FIELD_TILL_PRICE_OVERRIDES_COUNT + " + " + safeSQLCast(value);
        }
        return value;
    }


    /**
     * Get the UnitsPriceAdjustments as a String
     *
     * @param totals Totals object containing the data
     * @return UnitsPriceAdjustments converted to a String
     */
    protected String getUnitsPriceAdjustments(FinancialTotalsIfc totals)
    {
        String value = "0";
        if(totals != null)
        {
            value = totals.getUnitsPriceAdjustments().toString();
        }
        if (isUpdateStatement)
        {
            value = FIELD_TILL_PRICE_ADJUSTMENTS_COUNT + " + " + safeSQLCast(value);
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
        if (isUpdateStatement)
        {
            value = FIELD_TILL_TRANSACTIONS_WITH_RETURNED_ITEMS_COUNT + " + " + safeSQLCast(value);
        }
        return value;
    }
    
    /**
     * Get the till reconciled amount from the entered FinancialCountTenderItem
     * @param till
     * @param tenderDescriptor
     * @return
     */
    protected String getTillReconcileAmount(TillIfc till, TenderDescriptorIfc tenderDesc)
    {
        FinancialCountIfc enteredCount = till.getTotals().getCombinedCount().getEntered();
        FinancialCountTenderItemIfc enteredTender = enteredCount.getSummaryTenderItemByDescriptor(tenderDesc);

        CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance();;

        if (enteredTender != null)
        {
            amount = enteredTender.getAmountTotal();
        }

        String value = amount.getStringValue();

        if (isUpdateStatement)
        {
            value = FIELD_RECONCILE_AMOUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }

    /**
     Returns the tender loan count
     <p>
     @param  loans       The till loans
     @param  tenderDesc  The TenderDescriptor
     @return the tender loan count
     */
    protected String getTillReconcileCount(TillIfc till, TenderDescriptorIfc tenderDesc)
    {
        String value = "0";

        FinancialCountIfc enteredCount = till.getTotals().getCombinedCount().getEntered();
        FinancialCountTenderItemIfc enteredTender = enteredCount.getSummaryTenderItemByDescriptor(tenderDesc);

        if (enteredTender != null)
        {
            value = String.valueOf(enteredTender.getNumberItemsIn() - enteredTender.getNumberItemsOut());
        }

        if (isUpdateStatement)
        {
            value = FIELD_RECONCILE_MEDIA_UNIT_COUNT + " + " + safeSQLCast(value);
        }

        return(value);
    }
}
