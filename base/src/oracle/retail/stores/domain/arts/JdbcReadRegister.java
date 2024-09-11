/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadRegister.java /main/24 2013/09/05 10:36:19 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     02/20/13 - Modified for Currency Rounding.
 *    cgreene   05/21/12 - XbranchMerge cgreene_bug-13951397 from
 *                         rgbustores_13.5x_generic
 *    cgreene   05/16/12 - arrange order of businessDay column to end of
 *                         primary key to improve performance since most
 *                         receipt lookups are done without the businessDay
 *    nkgautam  08/02/10 - bill payment changes
 *    cgreene   06/23/10 - synchd column order with primary key
 *    npoola    06/02/10 - removed the training mode increment id dependency
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    cgreene   10/09/09 - change searching for most recent business day to
 *                         search in CA_DY_BSN
 *    jswan     08/13/09 - Added the till reconcile amount and count to the
 *                         till, workstation and store tender history tables
 *                         and to code that reads from and writes to them.
 *    mahising  12/09/08 - rework of base issue
 *    mahising  11/13/08 - Added for Customer module for both ORPOS and ORCO
 *
 * ===========================================================================
 * $Log:
 *    11   360Commerce 1.10        5/16/2007 7:55:27 PM   Brett J. Larsen
 *         CR 26903 - 8.0.1 merge to trunk
 *
 *         BackOffice <ARG> Summary Report overhaul (many CRs fixed)
 *
 *    10   360Commerce 1.9         5/14/2007 6:08:34 PM   Sandy Gu
 *         update inclusive information in financial totals and history tables
 *    9    360Commerce 1.8         4/25/2007 10:01:14 AM  Anda D. Cadar   I18N
 *         merge
 *    8    360Commerce 1.7         2/6/2007 11:05:37 AM   Anil Bondalapati
 *         Merge from JdbcReadRegister.java, Revision 1.5.1.0
 *    7    360Commerce 1.6         12/8/2006 5:01:14 PM   Brendan W. Farrell
 *         Read the tax history when creating pos log for openclosetill
 *         transactions.  Rewrite of some code was needed.
 *    6    360Commerce 1.5         7/21/2006 2:27:37 PM   Brendan W. Farrell
 *         Merge fixes from v7.x.  These changes let services extend tax.
 *    5    360Commerce 1.4         1/25/2006 4:11:17 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         1/22/2006 11:41:18 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:45 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:59 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:26:05    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:41     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:45     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:59     Robert Pearse
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
 *   Revision 1.13  2004/04/28 19:41:39  jdeleau
 *   @scr 4218 Add StoreCreditsIssued (count/amount and voided counts and amounts)
 *   to Financial Totals.
 *
 *   Revision 1.12  2004/04/27 20:01:16  jdeleau
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
 *   Revision 1.6  2004/02/25 23:17:41  crain
 *   @scr 3814 Issue Gift Certificate
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
 *    Rev 1.0   Aug 29 2003 15:32:04   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Jul 15 2003 11:17:16   sfl
 * Read the stored void counts from workstationHistory table for calculating the Net Trans. count in register report.
 * Resolution for POS SCR-3164: Register Reports -  Register "Net Trans Tax Count " line is not correct.
 *
 *    Rev 1.3   Jul 01 2003 13:27:18   jgs
 * Fixed problem which doubled pickup counts.
 * Resolution for 1157: Add task for Importing IX Retail Transactions.
 *
 *    Rev 1.2   24 Jun 2002 11:48:32   jbp
 * merge from 5.1 SCR 1726
 * Resolution for POS SCR-1726: Void - Void of new special order gets stuck in the queue in DB2
 *
 *    Rev 1.1   11 Jun 2002 16:25:00   jbp
 * changes to report markdowns
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.0   Jun 03 2002 16:37:52   msg
 * Initial revision.
 *
 *    Rev 1.4   31 May 2002 16:05:56   adc
 * changes for tillReconcile flag
 * Resolution for Backoffice SCR-1026: TLog changes
 *
 *    Rev 1.3   31 May 2002 12:52:12   adc
 * Changed the read of the till reconcile flag
 * Resolution for Backoffice SCR-1026: TLog changes
 *
 *    Rev 1.2   19 Apr 2002 17:48:06   sfl
 * Addded 20 new data columns in the WorkstationHistory
 * table. The original approach to get gross values by
 * calculation is now replaced by reading the gross values
 * directly from the newly added data columns.
 * Resolution for POS SCR-1579: Store gross figures in the DB (financials)
 *
 *    Rev 1.1   Mar 18 2002 22:47:38   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:07:38   msg
 * Initial revision.
 *
 *    Rev 1.14   01 Mar 2002 15:17:14   pdd
 * Converted to use TenderTypeMapIfc for tender codes and descriptors.
 * Resolution for POS SCR-627: Make the Tender type list extendible.
 *
 *    Rev 1.13   22 Feb 2002 10:26:22   sfl
 * Applied the new methods to resolve the problem in Oracle8i
 * when inserting empty string value into not null database
 * table column.
 * Resolution for Domain SCR-33: Port POS 5.0 to Oracle8i
 *
 *    Rev 1.12   Feb 05 2002 16:33:38   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 *
 *    Rev 1.11   17 Jan 2002 19:03:50   pdd
 * Added pickup count by currency.
 * Resolution for POS SCR-370: 5.0 Summary report tender summary updates
 *
 *    Rev 1.10   17 Jan 2002 16:35:58   pdd
 * Set the summary description for charge tender and the nationality in the description as needed.
 * Resolution for POS SCR-370: 5.0 Summary report tender summary updates
 *
 *    Rev 1.9   10 Jan 2002 19:15:54   pdd
 * Corrected problems with tender history data.
 * Resolution for POS SCR-370: 5.0 Summary report tender summary updates
 *
 *    Rev 1.8   08 Jan 2002 15:08:54   pdd
 * Added tender subtype and currency country code.
 * Resolution for POS SCR-370: 5.0 Summary report tender summary updates
 *
 *    Rev 1.7   17 Dec 2001 17:10:42   sfl
 * Included the ShippingChargeTotalAmount and
 * TotalShippingChargeCount columns in the
 * database table read on the WorkstationHistory table.
 * This is to support displaying the shipping charge
 * information in the summary report on register.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.6   02 Dec 2001 12:47:58   mpm
 * Implemented financials, voids for special order domain objects.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.5   12 Nov 2001 09:44:36   epd
 * Removed drawer status from db persistent data
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.4   08 Nov 2001 09:48:56   epd
 * Added new field/attribute for Register drawer status
 * whether is is occupied or unoccupied
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.3   06 Nov 2001 14:03:56   epd
 * Changed some attributes and methods to reference till reconcile instead of till close
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.2   29 Oct 2001 16:12:06   epd
 * Added column to workstation table.  Added code to accommodate new column.  The new column is a boolean register setting determining whether the POS will allow Till reconciliation or not at the Register.
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.1   26 Oct 2001 09:51:32   epd
 * Added 7 new fields to accommodate the moving of Till related parameters to database
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 20 2001 16:00:02   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;


import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.utility.CountryCodeMap;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.common.utility.Util;

/**
    Abstract class that contains the database calls for reading registers.
    <P>
    @version $Revision: /main/24 $
**/
//-------------------------------------------------------------------------
public abstract class JdbcReadRegister extends JdbcReadReportingPeriod
                                       implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 3029042422021254878L;
    /** The logger to which log messages will be sent. */
    private static final Logger logger = Logger.getLogger(JdbcReadRegister.class);
    /** revision number supplied by source-code-control system */
    public static final String revisionNumber = "$Revision: /main/24 $";
    // Tender type map
    protected static TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();

    /**
     * Reads the information from the Workstation table.
     * 
     * @param dataConnection connection to the db
     * @param workstation the workstation
     * @return register status
     * @exception DataException upon error
     */
    public RegisterIfc selectWorkstation(JdbcDataConnection dataConnection,
                                         WorkstationIfc workstation)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Add the desired tables (and aliases)
         */
        sql.addTable(TABLE_WORKSTATION);

        /*
         * Add desired columns
         */
        sql.addColumn(FIELD_WORKSTATION_TERMINAL_STATUS_CODE);
        sql.addColumn(FIELD_WORKSTATION_TRAINING_MODE_FLAG);
        sql.addColumn(FIELD_BUSINESS_DAY_DATE);
        sql.addColumn(FIELD_WORKSTATION_SEQUENCE_NUMBER);
        sql.addColumn(FIELD_CUSTOMER_SEQUENCE_NUMBER);
        sql.addColumn(FIELD_WORKSTATION_START_DATE_TIMESTAMP);
        sql.addColumn(FIELD_UNIQUE_IDENTIFIER_EXTENSION);
        sql.addColumn(FIELD_WORKSTATION_CURRENT_TILL_ID);
        sql.addColumn(FIELD_WORKSTATION_ACCOUNTABILITY);
        sql.addColumn(FIELD_WORKSTATION_TILL_FLOAT_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_COUNT_TILL_AT_RECONCILE);
        sql.addColumn(FIELD_WORKSTATION_COUNT_FLOAT_AT_OPEN);
        sql.addColumn(FIELD_WORKSTATION_COUNT_FLOAT_AT_RECONCILE);
        sql.addColumn(FIELD_WORKSTATION_COUNT_CASH_LOAN);
        sql.addColumn(FIELD_WORKSTATION_COUNT_CASH_PICKUP);
        sql.addColumn(FIELD_WORKSTATION_COUNT_CHECK_PICKUP);
        sql.addColumn(FIELD_WORKSTATION_TILL_RECONCILE);

        /*
         * Add Qualifier(s)
         */
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(workstation));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(workstation));


        RegisterIfc register = null;
        try
        {
            dataConnection.execute(sql.getSQLString());

            EYSDate openTime = null;
            ResultSet rs = (ResultSet) dataConnection.getResult();

            if (rs.next())
            {
                int index = 0;
                int statusCode = rs.getInt(++index);
                /*boolean trainingFlag =*/ rs.getBoolean(++index);
                EYSDate businessDate = getEYSDateFromString(rs, ++index);
                int sequenceNumber = rs.getInt(++index);
                int customerSequenceNumber = rs.getInt(++index);
                Timestamp startTime = rs.getTimestamp(++index);
                String uniqueExtension = getSafeString(rs, ++index);
                String currentTillID = getSafeString(rs, ++index);
                String accountability = getSafeString(rs, ++index);
                CurrencyIfc tillFloatAmount = getCurrencyFromDecimal(rs, ++index);
                String countTillAtReconcile = getSafeString(rs, ++index);
                String countFloatAtOpen = getSafeString(rs, ++index);
                String countFloatAtReconcile = getSafeString(rs, ++index);
                String countCashLoan = getSafeString(rs, ++index);
                String countCashPickup = getSafeString(rs, ++index);
                String countCheckPickup = getSafeString(rs, ++index);
                boolean tillReconcile = getBooleanFromString(rs, ++index);

                /*
                 * Need to handle timestamps this way.
                 * getEYSDateFromString() returns a DATE_ONLY EYSDate.
                 */
                if (startTime != null)
                {
                    openTime = new EYSDate(timestampToDate(startTime));
                }

                register = instantiateRegister();

                /*
                 * Initialize register object
                 */
                register.resetTotals();
                register.setWorkstation((WorkstationIfc)workstation.clone()); // cheat
                register.setStatus(statusCode);
                register.setBusinessDate(businessDate);
                register.setLastTransactionSequenceNumber(sequenceNumber);
                register.setLastCustomerSequenceNumber(customerSequenceNumber);
                register.setOpenTime(openTime);
                register.setCurrentUniqueID(uniqueExtension);
                register.setCurrentTillID(currentTillID);
                register.setAccountability(Integer.parseInt(accountability));
                register.setTillFloatAmount(tillFloatAmount);
                register.setTillCountTillAtReconcile(Integer.parseInt(countTillAtReconcile));
                register.setTillCountFloatAtOpen(Integer.parseInt(countFloatAtOpen));
                register.setTillCountFloatAtReconcile(Integer.parseInt(countFloatAtReconcile));
                register.setTillCountCashLoan(Integer.parseInt(countCashLoan));
                register.setTillCountCashPickup(Integer.parseInt(countCashPickup));
                register.setTillCountCheckPickup(Integer.parseInt(countCheckPickup));
                register.setTillReconcile(tillReconcile);

            }
            else
            {
                throw new DataException(DataException.NO_DATA,
                                        "selectWorkstation: Workstation not found.");
            }

            rs.close();
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR,
                                    "selectWorkstation: Workstation table",
                                    se);
        }
        catch (Exception e)
        {
            logger.error(Util.throwableToString(e));
            throw new DataException(DataException.UNKNOWN,
                                    "selectWorkstation: Workstation table",
                                    e);
        }

        return(register);
    }

    /**
     * Reads additional register information.
     * 
     * @param dataConnection connection to the db
     * @param register the register
     * @return List of registers for the workstation on the business day
     * @exception DataException upon error
     */
    public Vector<RegisterIfc> selectWorkstationHistory(JdbcDataConnection dataConnection,
                                           RegisterIfc register)
        throws DataException
    {
        Vector<RegisterIfc> registerVector = new Vector<RegisterIfc>(2);

        if (register.getWorkstation().isTrainingMode())
        {
            /*
             * Don't attempt to read accumulator tables for training mode
             */
            return(registerVector);
        }

        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Add the desired tables (and aliases)
         */
        sql.addTable(TABLE_WORKSTATION_HISTORY, ALIAS_WORKSTATION_HISTORY);
        sql.addTable(TABLE_REPORTING_PERIOD, ALIAS_REPORTING_PERIOD);
        sql.addTable(TABLE_BUSINESS_DAY, ALIAS_BUSINESS_DAY);

        /*
         * Add desired columns
         */
        sql.addColumn(FIELD_WORKSTATION_HISTORY_STATUS_CODE);
        sql.addColumn(FIELD_WORKSTATION_START_DATE_TIMESTAMP);
        sql.addColumn(FIELD_CURRENCY_ID); //I18N

        // These are in the same order as Store History
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NO_SALE_TRANSACTION_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_SALE_LINE_ITEM_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TAX_EXEMPT_TRANSACTION_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TAX_EXEMPT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_INCLUSIVE_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_SALES_EX_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_DISCOUNT_COUNT);
        sql.addColumn(FIELD_WORKSTATION_DISCOUNT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_MARKDOWN_COUNT);
        sql.addColumn(FIELD_WORKSTATION_MARKDOWN_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_MISCELLANEOUS_DISCOUNT_COUNT);
        sql.addColumn(FIELD_WORKSTATION_MISCELLANEOUS_DISCOUNT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_REFUND_COUNT);
        sql.addColumn(FIELD_WORKSTATION_REFUND_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_RETURN_COUNT);
        sql.addColumn(FIELD_WORKSTATION_RETURN_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TENDER_PICKUP_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TENDER_LOAN_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_PICKUP_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TRANSACTION_VOID_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_POST_TRANSACTION_VOID_COUNT);
        sql.addColumn(FIELD_WORKSTATION_POST_TRANSACTION_VOID_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_LINE_ITEM_VOID_COUNT);
        sql.addColumn(FIELD_WORKSTATION_LINE_ITEM_VOID_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TRANSACTION_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_LOAN_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TRANSACTION_VOID_TOTAL_AMOUNT);
        // Additional fields not defined in ARTS
            //StoreCoupon Discounts
        sql.addColumn(FIELD_WORKSTATION_ITEM_DISCOUNT_STORE_COUPON_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_ITEM_DISCOUNT_STORE_COUPON_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TRANSACTION_DISCOUNT_STORE_COUPON_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TRANSACTION_DISCOUNT_STORE_COUPON_COUNT);

        sql.addColumn(FIELD_WORKSTATION_NONTAXABLE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONTAXABLE_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TAXABLE_COUNT);
        sql.addColumn(FIELD_WORKSTATION_REFUND_NONTAXABLE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONTAXABLE_REFUND_COUNT);
        sql.addColumn(FIELD_WORKSTATION_REFUND_TAX_EXEMPT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TAX_EXEMPT_REFUND_COUNT);
        sql.addColumn(FIELD_WORKSTATION_LINE_ITEM_SALES_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_LINE_ITEM_NONTAXABLE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONTAXABLE_LINE_ITEM_COUNT);
        sql.addColumn(FIELD_WORKSTATION_LINE_ITEM_TAX_EXEMPT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TAX_EXEMPT_LINE_ITEM_COUNT);
        sql.addColumn(FIELD_WORKSTATION_RETURN_NONTAXABLE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONTAXABLE_RETURN_COUNT);
        sql.addColumn(FIELD_WORKSTATION_RETURN_TAX_EXEMPT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TAX_EXEMPT_RETURN_COUNT);
        sql.addColumn(FIELD_WORKSTATION_REFUND_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_REFUND_INCLUSIVE_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_RETURN_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_RETURN_INCLUSIVE_TAX_TOTAL_AMOUNT);

        sql.addColumn(FIELD_WORKSTATION_NONMERCH_NONTAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONMERCH_NONTAX_COUNT);
        sql.addColumn(FIELD_WORKSTATION_RETURN_NONMERCH_NONTAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONMERCH_NONTAX_RETURN_COUNT);
        sql.addColumn(FIELD_WORKSTATION_NONMERCH_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONMERCH_TAX_COUNT);
        sql.addColumn(FIELD_WORKSTATION_RETURN_NONMERCH_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_NONMERCH_TAX_RETURN_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_COUNT);
        sql.addColumn(FIELD_WORKSTATION_RETURN_GIFT_CARD_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GIFT_CARD_RETURN_COUNT);
        sql.addColumn(FIELD_WORKSTATION_HOUSE_PAYMENT_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_HOUSE_PAYMENT_COUNT);
        sql.addColumn(FIELD_WORKSTATION_RESTOCKING_FEE_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_RESTOCKING_FEE_COUNT);
        sql.addColumn(FIELD_WORKSTATION_RESTOCKING_FEE_AMOUNT_FROM_NON_TAXABLE);
        sql.addColumn(FIELD_WORKSTATION_RESTOCKING_FEE_COUNT_FROM_NON_TAXABLE);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_LAYAWAY_PAYMENTS_COLLECTED_COUNT);
        sql.addColumn(FIELD_WORKSTATION_LAYAWAY_PAYMENTS_COLLECTED_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_LAYAWAY_NEW_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_LAYAWAY_PICKUP_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_LAYAWAY_DELETIONS_DISBURSEMENT_COUNT);
        sql.addColumn(FIELD_WORKSTATION_LAYAWAY_DELETIONS_DISBURSEMENT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_LAYAWAY_INITIATION_FEES_COLLECTED_COUNT);
        sql.addColumn(FIELD_WORKSTATION_LAYAWAY_INITIATION_FEES_COLLECTED_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_LAYAWAY_DELETION_FEES_COUNT);
        sql.addColumn(FIELD_WORKSTATION_LAYAWAY_DELETION_FEES_COLLECTED_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_SPECIAL_ORDER_NEW_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_SPECIAL_ORDER_PARTIAL_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_ORDER_PAYMENTS_COLLECTED_COUNT);
        sql.addColumn(FIELD_WORKSTATION_ORDER_PAYMENTS_COLLECTED_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_ORDER_CANCELS_DISBURSEMENT_COUNT);
        sql.addColumn(FIELD_WORKSTATION_ORDER_CANCELS_DISBURSEMENT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_SHIPPING_CHARGE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_SHIPPING_CHARGE_COUNT);
        sql.addColumn(FIELD_WORKSTATION_SHIPPING_CHARGE_TAX_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_SHIPPING_CHARGE_INCLUSIVE_TAX_AMOUNT);

        sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_IN_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_OUT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_IN_UNIT_COUNT);
        sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_OUT_UNIT_COUNT);

        // Gross value related columns
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_ITEM_SALES_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_ITEM_SALES_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_ITEM_RETURNS_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_ITEM_RETURNS_COUNT);
        sql.addColumn(FIELD_WORKSTATION_ITEM_SALES_TAX_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_ITEM_SALES_INCLUSIVE_TAX_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TRANSACTION_SALES_TAX_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TRANSACTION_SALES_INCLUSIVE_TAX_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_TRANSACTION_SALES_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_NON_TAXABLE_TRANSACTION_SALES_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAX_EXEMPT_TRANSACTION_SALES_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_NON_TAXABLE_TRANSACTION_SALES_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_TRANSACTION_SALES_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAX_EXEMPT_TRANSACTION_SALES_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_TRANSACTION_RETURNS_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_TRANSACTION_RETURNS_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_NON_MERCHANDISE_SALES_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TAXABLE_NON_MERCHANDISE_SALES_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_SALES_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_SALES_COUNT);
        sql.addColumn(FIELD_GROSS_TAXABLE_SALES_VOID_COUNT);
        sql.addColumn(FIELD_GROSS_TAXABLE_RETURNS_VOID_COUNT);
        sql.addColumn(FIELD_GROSS_NONTAXABLE_SALES_VOID_COUNT);
        sql.addColumn(FIELD_GROSS_NONTAXABLE_RETURNS_VOID_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GIFT_CERTIFICATE_ISSUED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GIFT_CERTIFICATE_ISSUED_COUNT);

        sql.addColumn(ALIAS_WORKSTATION_HISTORY + "." + FIELD_WORKSTATION_GIFT_CARD_ISSUED_AMOUNT);
        sql.addColumn(ALIAS_WORKSTATION_HISTORY + "." + FIELD_WORKSTATION_GIFT_CARD_ISSUED_COUNT);
        sql.addColumn(ALIAS_WORKSTATION_HISTORY + "." + FIELD_WORKSTATION_GIFT_CARD_RELOADED_AMOUNT);
        sql.addColumn(ALIAS_WORKSTATION_HISTORY + "." + FIELD_WORKSTATION_GIFT_CARD_RELOADED_COUNT);
        sql.addColumn(ALIAS_WORKSTATION_HISTORY + "." + FIELD_WORKSTATION_GIFT_CARD_REDEEMED_AMOUNT);
        sql.addColumn(ALIAS_WORKSTATION_HISTORY + "." + FIELD_WORKSTATION_GIFT_CARD_REDEEMED_COUNT);
        sql.addColumn(ALIAS_WORKSTATION_HISTORY + "." + FIELD_WORKSTATION_GIFT_CARD_ISSUE_VOIDED_AMOUNT);
        sql.addColumn(ALIAS_WORKSTATION_HISTORY + "." + FIELD_WORKSTATION_GIFT_CARD_ISSUE_VOIDED_COUNT);
        sql.addColumn(ALIAS_WORKSTATION_HISTORY + "." + FIELD_WORKSTATION_GIFT_CARD_RELOAD_VOIDED_AMOUNT);
        sql.addColumn(ALIAS_WORKSTATION_HISTORY + "." + FIELD_WORKSTATION_GIFT_CARD_RELOAD_VOIDED_COUNT);
        sql.addColumn(ALIAS_WORKSTATION_HISTORY + "." + FIELD_WORKSTATION_GIFT_CARD_REDEEM_VOIDED_AMOUNT);
        sql.addColumn(ALIAS_WORKSTATION_HISTORY + "." + FIELD_WORKSTATION_GIFT_CARD_REDEEM_VOIDED_COUNT);

        sql.addColumn(FIELD_WORKSTATION_HOUSE_ACCOUNT_ENROLLMENT_APPROVED_COUNT);
        sql.addColumn(FIELD_WORKSTATION_HOUSE_ACCOUNT_ENROLLMENT_DECLINED_COUNT);

        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_CREDIT_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_CREDIT_UNITS);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_CREDIT_VOIDED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CARD_ITEM_CREDIT_VOIDED_UNITS);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_REDEEMED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_REDEEMED_UNITS);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_REDEEMED_VOIDED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_REDEEMED_VOIDED_UNITS);
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_ISSUED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_ISSUED_UNITS);
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_ISSUED_VOIDED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_ISSUED_VOIDED_UNITS);
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_REDEEMED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_REDEEMED_UNITS);
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_REDEEMED_VOIDED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_STORE_CREDITS_REDEEMED_VOIDED_UNITS);
        sql.addColumn(FIELD_WORKSTATION_GROSS_ITEM_EMPLOYEE_DISCOUNT_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_ITEM_EMPLOYEE_DISCOUNT_UNITS);
        sql.addColumn(FIELD_WORKSTATION_GROSS_ITEM_EMPLOYEE_DISCOUNT_VOIDED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_ITEM_EMPLOYEE_DISCOUNT_VOIDED_UNITS);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_UNITS);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_VOIDED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_VOIDED_UNITS);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_ISSUED_VOIDED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_ISSUED_VOIDED_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_TENDERED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_TENDERED_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_TENDERED_VOIDED_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_GIFT_CERTIFICATE_TENDERED_VOIDED_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_EMPLOYEE_DISCOUNTS_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_EMPLOYEE_DISCOUNTS_COUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_CUSTOMER_DISCOUNTS_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_GROSS_CUSTOMER_DISCOUNTS_COUNT);
        sql.addColumn(FIELD_WORKSTATION_PRICE_OVERRIDES_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_PRICE_OVERRIDES_COUNT);
        sql.addColumn(FIELD_WORKSTATION_PRICE_ADJUSTMENTS_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TRANSACTIONS_WITH_RETURNED_ITEMS_COUNT);
        sql.addColumn(ALIAS_WORKSTATION_HISTORY + "." + FIELD_STORE_TOTAL_BILLPAYMENT);
        sql.addColumn(ALIAS_WORKSTATION_HISTORY + "." + FIELD_STORE_TOTAL_BILLPAYMENT_COUNT);
        sql.addColumn(ALIAS_WORKSTATION_HISTORY + "." + FIELD_TOTAL_AMOUNT_CHANGE_ROUNDED_IN);
        sql.addColumn(ALIAS_WORKSTATION_HISTORY + "." + FIELD_TOTAL_AMOUNT_CHANGE_ROUNDED_OUT);

        sql.addColumn(ALIAS_WORKSTATION_HISTORY + "." + FIELD_FISCAL_YEAR);
        sql.addColumn(ALIAS_WORKSTATION_HISTORY + "." + FIELD_REPORTING_PERIOD_ID);

         /*
         * Add Qualifier(s)
         */

        // For the specified workstation only
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(register));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(register));

        // Join Store History and Reporting Period
        sql.addQualifier(ALIAS_WORKSTATION_HISTORY + "." + FIELD_FISCAL_YEAR
                         + " = " + ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_YEAR);
        sql.addQualifier(ALIAS_WORKSTATION_HISTORY + "." + FIELD_REPORTING_PERIOD_TYPE_CODE
                         + " = " + ALIAS_REPORTING_PERIOD + "." + FIELD_REPORTING_PERIOD_TYPE_CODE);
        sql.addQualifier(ALIAS_WORKSTATION_HISTORY + "." + FIELD_REPORTING_PERIOD_ID
                         + " = " + ALIAS_REPORTING_PERIOD + "." + FIELD_REPORTING_PERIOD_ID);

        // Join Reporting Period and Business Day
        sql.addQualifier(ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_YEAR
                         + " = " + ALIAS_BUSINESS_DAY + "." + FIELD_FISCAL_YEAR);
        sql.addQualifier(ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_WEEK_NUMBER
                         + " = " + ALIAS_BUSINESS_DAY + "." + FIELD_FISCAL_WEEK_NUMBER);
        sql.addQualifier(ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_DAY_NUMBER
                         + " = " + ALIAS_BUSINESS_DAY + "." + FIELD_FISCAL_DAY_NUMBER);

        // For the specified business day only
        sql.addQualifier(ALIAS_BUSINESS_DAY + "." + FIELD_BUSINESS_DAY_DATE
                         + " = " + getBusinessDay(register.getBusinessDate()));

        /*
         * Add Ordering(s)
         */
        // Sort most recent first
        sql.addOrdering(ALIAS_WORKSTATION_HISTORY + "." + FIELD_FISCAL_YEAR + " DESC,"
                        + ALIAS_WORKSTATION_HISTORY + "." + FIELD_REPORTING_PERIOD_ID + " DESC");

        try
        {
            dataConnection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet)dataConnection.getResult();

            while (rs.next())
            {
                /*
                 * Grab the fields selected from the database
                 */
                int index = 0;
                int statusCode = rs.getInt(++index);
                Timestamp startTime = rs.getTimestamp(++index);
                int currencyID = rs.getInt(++index); //I18N

                int noSaleCount = rs.getInt(++index);
                BigDecimal itemSalesCount = getBigDecimal(rs, ++index);
                int taxExemptTransactionCount = rs.getInt(++index);
                CurrencyIfc netTaxExemptAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc netTaxAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc netInclusiveTaxAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc transactionSalesAmount = getCurrencyFromDecimal(rs, ++index);
                int discountCount = rs.getInt(++index);
                CurrencyIfc discountAmount = getCurrencyFromDecimal(rs, ++index);
                int markdownCount = rs.getInt(++index);
                CurrencyIfc markdownAmount = getCurrencyFromDecimal(rs, ++index);
                int miscDiscountCount = rs.getInt(++index);
                CurrencyIfc miscDiscountAmount = getCurrencyFromDecimal(rs, ++index);
                int refundCount = rs.getInt(++index);
                CurrencyIfc refundAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal returnCount = getBigDecimal(rs, ++index);
                CurrencyIfc returnAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc pickupAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc loanAmount = getCurrencyFromDecimal(rs, ++index);
                int pickupCount = rs.getInt(++index);
                int voidTransactionCount = rs.getInt(++index);
                int postVoidCount = rs.getInt(++index);
                CurrencyIfc postVoidAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal itemVoidCount = getBigDecimal(rs, ++index);
                CurrencyIfc itemVoidAmount = getCurrencyFromDecimal(rs, ++index);
                int transactionCount = rs.getInt(++index);
                int loanCount = rs.getInt(++index);
                CurrencyIfc voidTransactionAmount = getCurrencyFromDecimal(rs, ++index);

                //StoreCoupon Discounts
                CurrencyIfc itemDiscStoreCouponAmount  = getCurrencyFromDecimal(rs, ++index);
                int  itemDiscStoreCouponCount = rs.getInt(++index);
                CurrencyIfc transactionDiscStoreCouponAmount  = getCurrencyFromDecimal(rs, ++index);
                int  transactionDiscStoreCouponCount = rs.getInt(++index);

                CurrencyIfc netNontaxableAmount = getCurrencyFromDecimal(rs, ++index);
                int nontaxableTransactionCount = rs.getInt(++index);
                int taxableTransactionCount = rs.getInt(++index);
                CurrencyIfc nontaxableRefundAmount = getCurrencyFromDecimal(rs, ++index);
                int nontaxableRefundCount = rs.getInt(++index);
                CurrencyIfc taxExemptRefundAmount = getCurrencyFromDecimal(rs, ++index);
                int taxExemptRefundCount = rs.getInt(++index);
                CurrencyIfc itemSalesAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc itemNontaxableAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal itemNontaxableCount = getBigDecimal(rs, ++index);
                CurrencyIfc itemTaxExemptAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal itemTaxExemptCount = getBigDecimal(rs, ++index);
                CurrencyIfc itemNontaxableReturnAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal itemNontaxableReturnCount = getBigDecimal(rs, ++index);
                CurrencyIfc itemTaxExemptReturnAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal itemTaxExemptReturnCount = getBigDecimal(rs, ++index);
                CurrencyIfc taxRefundAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc inclusiveTaxRefundAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc taxReturnAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc inclusiveTaxReturnAmount = getCurrencyFromDecimal(rs, ++index);

                CurrencyIfc nonMerchNonTaxAmount        = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  nonMerchNonTaxCount         = getBigDecimal(rs, ++index);
                CurrencyIfc nonMerchNonTaxReturnAmount  = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  nonMerchNonTaxReturnCount   = getBigDecimal(rs, ++index);
                CurrencyIfc nonMerchTaxAmount           = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  nonMerchTaxCount            = getBigDecimal(rs, ++index);
                CurrencyIfc nonMerchTaxReturnAmount     = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  nonMerchTaxReturnCount      = getBigDecimal(rs, ++index);
                CurrencyIfc giftCardAmount              = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  giftCardCount               = getBigDecimal(rs, ++index);
                CurrencyIfc giftCardReturnAmount        = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  giftCardReturnCount         = getBigDecimal(rs, ++index);
                CurrencyIfc housePaymentsAmount         = getCurrencyFromDecimal(rs, ++index);
                int         housePaymentsCount          = rs.getInt(++index);
                CurrencyIfc restockingFeeAmount         = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  restockingFeeCount          = getBigDecimal(rs, ++index);
                CurrencyIfc restockingFeeAmountNonTax   = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  restockingFeeCountNonTax    = getBigDecimal(rs, ++index);
                int         layawayPaymentsCount        = rs.getInt(++index);
                CurrencyIfc layawayPaymentsAmount       = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc layawayNewAmount       = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc layawayPickupAmount       = getCurrencyFromDecimal(rs, ++index);
                int         layawayDeletionsCount       = rs.getInt(++index);
                CurrencyIfc layawayDeletionsAmount      = getCurrencyFromDecimal(rs, ++index);
                int         layawayInitiationFeesCount  = rs.getInt(++index);
                CurrencyIfc layawayInitiationFeesAmount = getCurrencyFromDecimal(rs, ++index);
                int         layawayDeletionFeesCount    = rs.getInt(++index);
                CurrencyIfc layawayDeletionFeesAmount   = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc specialOrderNewAmount         = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc specialOrderPartialAmount         = getCurrencyFromDecimal(rs, ++index);
                int         orderPaymentsCount          = rs.getInt(++index);
                CurrencyIfc orderPaymentsAmount         = getCurrencyFromDecimal(rs, ++index);
                int         orderCancelsCount           = rs.getInt(++index);
                CurrencyIfc orderCancelsAmount          = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc shippingChargesAmount       = getCurrencyFromDecimal(rs, ++index);
                int         shippingChargesCount        = rs.getInt(++index);
                CurrencyIfc shippingChargesTaxAmount    = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc shippingChargesInclusiveTaxAmount    = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc tillPayInsAmount            = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc tillPayOutsAmount           = getCurrencyFromDecimal(rs, ++index);
                int         tillPayInsCount             = rs.getInt(++index);
                int         tillPayOutsCount            = rs.getInt(++index);

                CurrencyIfc grossTaxableItemSalesAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal grossTaxableItemSalesCount          = getBigDecimal(rs, ++index);
                CurrencyIfc grossTaxableItemReturnsAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal grossTaxableItemReturnsCount        = getBigDecimal(rs, ++index);
                CurrencyIfc itemSalesTaxAmount          = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc itemSalesInclusiveTaxAmount          = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc transactionSalesTaxAmount   = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc transactionSalesInclusiveTaxAmount   = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc grossTaxableTransactionSalesAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc grossNonTaxableTransactionSalesAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc grossTaxExemptTransactionSalesAmount  = getCurrencyFromDecimal(rs, ++index);
                int grossNonTaxableTransactionSalesCount = rs.getInt(++index);
                int grossTaxableTransactionSalesCount   = rs.getInt(++index);
                int grossTaxExemptTransactionSalesCount  = rs.getInt(++index);
                CurrencyIfc grossTaxableTransactionReturnsAmount = getCurrencyFromDecimal(rs, ++index);
                int grossTaxableTransactionReturnsCount  = rs.getInt(++index);
                CurrencyIfc grossTaxableNonMerchandiseSalesAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal grossTaxableNonMerchandiseSalesCount     = getBigDecimal(rs, ++index);
                CurrencyIfc grossNonTaxableNonMerchandiseSalesAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal grossNonTaxableNonMerchandiseSalesCount = getBigDecimal(rs, ++index);
                CurrencyIfc grossGiftCardItemSalesAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal grossGiftCardItemSalesCount = getBigDecimal(rs, ++index);

                int taxableSalesVoidCount = rs.getInt(++index);
                int taxableReturnsVoidCount = rs.getInt(++index);
                int nonTaxableSalesVoidCount = rs.getInt(++index);
                int nonTaxableReturnsVoidVount = rs.getInt(++index);
                CurrencyIfc giftCertificateIssuedAmount    = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  giftCertificateIssuedCount     = getBigDecimal(rs, ++index);

                CurrencyIfc grossGiftCardIssuedAmount  = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  grossGiftCardIssuedCount   = getBigDecimal(rs, ++index);
                CurrencyIfc grossGiftCardReloadedAmount  = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  grossGiftCardReloadedCount   = getBigDecimal(rs, ++index);
                CurrencyIfc grossGiftCardRedeemedAmount  = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  grossGiftCardRedeemedCount   = getBigDecimal(rs, ++index);
                CurrencyIfc grossGiftCardIssueVoidedAmount  = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  grossGiftCardIssueVoidedCount   = getBigDecimal(rs, ++index);
                CurrencyIfc grossGiftCardReloadVoidedAmount  = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  grossGiftCardReloadVoidedCount   = getBigDecimal(rs, ++index);
                CurrencyIfc grossGiftCardRedeemVoidedAmount  = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  grossGiftCardRedeemVoidedCount   = getBigDecimal(rs, ++index);

                int houseAccountsApproved            = rs.getInt(++index);
                int houseAccountsDeclined            = rs.getInt(++index);

                CurrencyIfc amountGrossGiftCardItemCredit = getCurrencyFromDecimal(rs, ++index);
                BigDecimal unitsGrossGiftCardItemCredit = getBigDecimal(rs, ++index);
                CurrencyIfc amountGrossGiftCardItemCreditVoided = getCurrencyFromDecimal(rs, ++index);
                BigDecimal unitsGrossGiftCardItemCreditVoided = getBigDecimal(rs, ++index);

                CurrencyIfc amountGrossGiftCertificatesRedeemed = getCurrencyFromDecimal(rs, ++index);
                BigDecimal unitsGrossGiftCertificatesRedeemed = getBigDecimal(rs, ++index);
                CurrencyIfc amountGrossGiftCertificatesRedeemedVoided = getCurrencyFromDecimal(rs, ++index);
                BigDecimal unitsGrossGiftCertificatesRedeemedVoided = getBigDecimal(rs, ++index);

                CurrencyIfc amountGrossStoreCreditsIssued = getCurrencyFromDecimal(rs, ++index);
                BigDecimal unitsGrossStoreCreditsIssued = getBigDecimal(rs, ++index);
                CurrencyIfc amountGrossStoreCreditsIssuedVoided = getCurrencyFromDecimal(rs, ++index);
                BigDecimal unitsGrossStoreCreditsIssuedVoided = getBigDecimal(rs, ++index);

                CurrencyIfc amountGrossStoreCreditsRedeemed = getCurrencyFromDecimal(rs, ++index);
                BigDecimal unitsGrossStoreCreditsRedeemed = getBigDecimal(rs, ++index);
                CurrencyIfc amountGrossStoreCreditsRedeemedVoided = getCurrencyFromDecimal(rs, ++index);
                BigDecimal unitsGrossStoreCreditsRedeemedVoided = getBigDecimal(rs, ++index);

                CurrencyIfc amountGrossItemEmployeeDiscount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal unitsGrossItemEmployeeDiscount = getBigDecimal(rs, ++index);
                CurrencyIfc amountGrossItemEmployeeDiscountVoided = getCurrencyFromDecimal(rs, ++index);
                BigDecimal unitsGrossItemEmployeeDiscountVoided = getBigDecimal(rs, ++index);

                CurrencyIfc amountGrossTransactionEmployeeDiscount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal unitsGrossTransactionEmployeeDiscount = getBigDecimal(rs, ++index);
                CurrencyIfc amountGrossTransactionEmployeeDiscountVoided = getCurrencyFromDecimal(rs, ++index);
                BigDecimal unitsGrossTransactionEmployeeDiscountVoided = getBigDecimal(rs, ++index);

                CurrencyIfc grossGiftCertificateIssuedVoidedAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal grossGiftCertificateIssuedVoidedUnits = getBigDecimal(rs, ++index);
                CurrencyIfc grossGiftCertificatedTenderedAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal grossGiftCertificatedTenderedUnits = getBigDecimal(rs, ++index);
                CurrencyIfc grossGiftCertificatedTenderedVoidedAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal grossGiftCertificatedTenderedVoidedUnits = getBigDecimal(rs, ++index);
                CurrencyIfc grossEmployeeDiscountAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal grossEmployeeDiscountUnits = getBigDecimal(rs, ++index);
                CurrencyIfc grossCustomerDiscountAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal grossCustomerDiscountUnits = getBigDecimal(rs, ++index);
                CurrencyIfc priceOverridesAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal priceOverridesUnits = getBigDecimal(rs, ++index);
                BigDecimal priceAdjustmentsUnits = getBigDecimal(rs, ++index);
                int transactionsWithReturnedItemsCount = rs.getInt(++index);
                
                CurrencyIfc totalBillPaymentAmount = getCurrencyFromDecimal(rs, ++index);
                int totalBillPaymentCount = rs.getInt(++index);
                
                CurrencyIfc amountChangeRoundedIn = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc amountChangeRoundedOut = getCurrencyFromDecimal(rs, ++index);

                /*
                 * Timestamps need to be initialized this way,
                 * using getEYSDateFromString() returns a DATE_ONLY EYSDate.
                 */
                EYSDate openTime = new EYSDate(timestampToDate(startTime));

                /*
                 * AbstractFinancialEntity
                 */
                RegisterIfc newRegister = (RegisterIfc)register.clone();
                newRegister.setStatus(statusCode);
                newRegister.setOpenTime(openTime);

                /*
                 * FinancialTotals
                 */
                FinancialTotalsIfc totals = instantiateFinancialTotals();
                totals.setCurrencyID(currencyID); //I18N

                // Item Sales
                //totals.setAmountGrossTaxableItemSales(itemSalesAmount.subtract(itemNontaxableAmount));
                totals.setAmountGrossTaxableItemSales(grossTaxableItemSalesAmount);
                //totals.setUnitsGrossTaxableItemSales(itemSalesCount.subtract(itemNontaxableCount));
                totals.setUnitsGrossTaxableItemSales(grossTaxableItemSalesCount);
                totals.setAmountGrossNonTaxableItemSales(itemNontaxableAmount);
                totals.setUnitsGrossNonTaxableItemSales(itemNontaxableCount);
                totals.setAmountGrossTaxExemptItemSales(itemTaxExemptAmount);
                totals.setUnitsGrossTaxExemptItemSales(itemTaxExemptCount);
                // Item Returns
                //totals.setAmountGrossTaxableItemReturns(returnAmount.subtract(itemNontaxableReturnAmount));
                totals.setAmountGrossTaxableItemReturns(grossTaxableItemReturnsAmount);
                //totals.setUnitsGrossTaxableItemReturns(returnCount.subtract(itemNontaxableReturnCount));
                totals.setUnitsGrossTaxableItemReturns(grossTaxableItemReturnsCount);
                totals.setAmountGrossNonTaxableItemReturns(itemNontaxableReturnAmount);
                totals.setUnitsGrossNonTaxableItemReturns(itemNontaxableReturnCount);
                totals.setAmountGrossTaxExemptItemReturns(itemTaxExemptReturnAmount);
                totals.setUnitsGrossTaxExemptItemReturns(itemTaxExemptReturnCount);
                // Tax
                //totals.setAmountTaxItemSales(netTaxAmount.add(taxReturnAmount));
                totals.setAmountTaxItemSales(itemSalesTaxAmount);
                totals.setAmountInclusiveTaxItemSales(itemSalesInclusiveTaxAmount);
                totals.setAmountTaxItemReturns(taxReturnAmount);
                totals.setAmountInclusiveTaxItemReturns(inclusiveTaxReturnAmount);
                //totals.setAmountTaxTransactionSales(netTaxAmount.add(taxRefundAmount));
                totals.setAmountTaxTransactionSales(transactionSalesTaxAmount);
                totals.setAmountInclusiveTaxTransactionSales(transactionSalesInclusiveTaxAmount);
                totals.setAmountTaxTransactionReturns(taxRefundAmount);
                totals.setAmountInclusiveTaxTransactionReturns(inclusiveTaxRefundAmount);
                // Transaction Sales
                //CurrencyIfc nontaxableTransactionAmount = netNontaxableAmount.add(nontaxableRefundAmount);
                //CurrencyIfc taxExemptTransactionAmount = netTaxExemptAmount.add(taxExemptRefundAmount);
                //totals.setAmountGrossTaxableTransactionSales(transactionSalesAmount.subtract(nontaxableTransactionAmount));
                totals.setAmountGrossTaxableTransactionSales(grossTaxableTransactionSalesAmount);
                //int nontaxableTransactionSalesCount = nontaxableTransactionCount - nontaxableRefundCount;
                //int taxExemptTransactionSalesCount = taxExemptTransactionCount - taxExemptRefundCount;

                // taxable transaction sales = taxable transactions - taxable refund transactions
                //totals.setCountGrossTaxableTransactionSales
                //  (taxableTransactionCount - (refundCount - nontaxableRefundCount));
                totals.setCountGrossTaxableTransactionSales(grossTaxableTransactionSalesCount);
                //totals.setAmountGrossNonTaxableTransactionSales(nontaxableTransactionAmount);
                totals.setAmountGrossNonTaxableTransactionSales(grossNonTaxableTransactionSalesAmount);
                //totals.setCountGrossNonTaxableTransactionSales(nontaxableTransactionSalesCount);
                totals.setCountGrossNonTaxableTransactionSales(grossNonTaxableTransactionSalesCount);
                //totals.setAmountGrossTaxExemptTransactionSales(taxExemptTransactionAmount);
                totals.setAmountGrossTaxExemptTransactionSales(grossTaxExemptTransactionSalesAmount);
                //totals.setCountGrossTaxExemptTransactionSales(taxExemptTransactionSalesCount);
                totals.setCountGrossTaxExemptTransactionSales(grossTaxExemptTransactionSalesCount);
                // Transaction Returns
                //totals.setAmountGrossTaxableTransactionReturns(refundAmount.subtract(nontaxableRefundAmount));
                totals.setAmountGrossTaxableTransactionReturns(grossTaxableTransactionReturnsAmount);
                //totals.setCountGrossTaxableTransactionReturns(refundCount - nontaxableRefundCount);
                totals.setCountGrossTaxableTransactionReturns(grossTaxableTransactionReturnsCount);
                totals.setAmountGrossNonTaxableTransactionReturns(nontaxableRefundAmount);
                totals.setCountGrossNonTaxableTransactionReturns(nontaxableRefundCount);
                totals.setAmountGrossTaxExemptTransactionReturns(taxExemptRefundAmount);
                totals.setCountGrossTaxExemptTransactionReturns(taxExemptRefundCount);
                // Misc
                    //StoreCouponDiscounts
                totals.setAmountItemDiscStoreCoupons(itemDiscStoreCouponAmount);
                totals.setNumberItemDiscStoreCoupons(itemDiscStoreCouponCount);
                totals.setAmountTransactionDiscStoreCoupons(transactionDiscStoreCouponAmount);
                totals.setNumberTransactionDiscStoreCoupons(transactionDiscStoreCouponCount);

                totals.setAmountTransactionDiscounts(miscDiscountAmount);
                totals.setNumberTransactionDiscounts(miscDiscountCount);
                totals.setAmountItemDiscounts(discountAmount);
                totals.setNumberItemDiscounts(discountCount);
                totals.setAmountItemMarkdowns(markdownAmount);
                totals.setNumberItemMarkdowns(markdownCount);
                totals.setAmountPostVoids(postVoidAmount);
                totals.setNumberPostVoids(postVoidCount);
                totals.setNumberNoSales(noSaleCount);
                totals.setAmountLineVoids(itemVoidAmount);
                totals.setUnitsLineVoids(itemVoidCount);
                totals.setAmountCancelledTransactions(voidTransactionAmount);
                totals.setNumberCancelledTransactions(voidTransactionCount);

                //totals.setAmountGrossTaxableNonMerchandiseSales(nonMerchTaxAmount.add(nonMerchTaxReturnAmount));
                totals.setAmountGrossTaxableNonMerchandiseSales(grossTaxableNonMerchandiseSalesAmount);
                //totals.setUnitsGrossTaxableNonMerchandiseSales(nonMerchTaxCount.add(nonMerchTaxReturnCount));
                totals.setUnitsGrossTaxableNonMerchandiseSales(grossTaxableNonMerchandiseSalesCount);
                //totals.setAmountGrossNonTaxableNonMerchandiseSales(nonMerchNonTaxAmount.add(nonMerchNonTaxReturnAmount));
                totals.setAmountGrossNonTaxableNonMerchandiseSales(grossNonTaxableNonMerchandiseSalesAmount);
                //totals.setUnitsGrossNonTaxableNonMerchandiseSales(nonMerchNonTaxCount.add(nonMerchNonTaxReturnCount));
                totals.setUnitsGrossNonTaxableNonMerchandiseSales(grossNonTaxableNonMerchandiseSalesCount);
                totals.setAmountGrossTaxableNonMerchandiseReturns(nonMerchTaxReturnAmount);
                totals.setUnitsGrossTaxableNonMerchandiseReturns(nonMerchTaxReturnCount);
                totals.setAmountGrossNonTaxableNonMerchandiseReturns(nonMerchNonTaxReturnAmount);
                totals.setUnitsGrossNonTaxableNonMerchandiseReturns(nonMerchNonTaxReturnCount);
                //totals.setAmountGrossGiftCardItemSales(giftCardAmount.add(giftCardReturnAmount));
                totals.setAmountGrossGiftCardItemSales(grossGiftCardItemSalesAmount);
                //totals.setUnitsGrossGiftCardItemSales(giftCardCount.add(giftCardReturnCount));
                totals.setUnitsGrossGiftCardItemSales(grossGiftCardItemSalesCount);
                totals.setAmountGrossGiftCardItemReturns(giftCardReturnAmount);
                totals.setUnitsGrossGiftCardItemReturns(giftCardReturnCount);
                totals.setAmountHousePayments(housePaymentsAmount);
                totals.setCountHousePayments(housePaymentsCount);
                totals.setAmountRestockingFees(restockingFeeAmount);
                totals.setUnitsRestockingFees(restockingFeeCount);
                totals.setAmountRestockingFeesFromNonTaxableItems(restockingFeeAmountNonTax);
                totals.setUnitsRestockingFeesFromNonTaxableItems(restockingFeeCountNonTax);
                totals.setAmountLayawayPayments(layawayPaymentsAmount);
                totals.setCountLayawayPayments(layawayPaymentsCount);
                totals.setAmountLayawayNew(layawayNewAmount);
                totals.setAmountLayawayPickup(layawayPickupAmount);
                totals.setAmountLayawayDeletions(layawayDeletionsAmount);
                totals.setCountLayawayDeletions(layawayDeletionsCount);
                totals.setAmountLayawayInitiationFees(layawayInitiationFeesAmount);
                totals.setCountLayawayInitiationFees(layawayInitiationFeesCount);
                totals.setAmountLayawayDeletionFees(layawayDeletionFeesAmount);
                totals.setCountLayawayDeletionFees(layawayDeletionFeesCount);
                totals.setAmountSpecialOrderNew(specialOrderNewAmount);
                totals.setAmountSpecialOrderPartial(specialOrderPartialAmount);
                totals.setAmountOrderPayments(orderPaymentsAmount);
                totals.setCountOrderPayments(orderPaymentsCount);
                totals.setAmountOrderCancels(orderCancelsAmount);
                totals.setCountOrderCancels(orderCancelsCount);
                totals.setAmountShippingCharges(shippingChargesAmount);
                totals.setNumberShippingCharges(shippingChargesCount);
                totals.setAmountTaxShippingCharges(shippingChargesTaxAmount);
                totals.setAmountInclusiveTaxShippingCharges(shippingChargesInclusiveTaxAmount);
                totals.setAmountTillPayIns(tillPayInsAmount);
                totals.setAmountTillPayOuts(tillPayOutsAmount);
                totals.setCountTillPayIns(tillPayInsCount);
                totals.setCountTillPayOuts(tillPayOutsCount);
                totals.setCountTillLoans(loanCount);
                totals.setCountGrossTaxableTransactionSalesVoided(taxableSalesVoidCount);
                totals.setCountGrossTaxableTransactionReturnsVoided(taxableReturnsVoidCount);
                totals.setCountGrossNonTaxableTransactionSalesVoided(nonTaxableSalesVoidCount);
                totals.setCountGrossNonTaxableTransactionReturnsVoided(nonTaxableReturnsVoidVount);
                totals.setAmountGrossGiftCertificateIssued(giftCertificateIssuedAmount);
                totals.setUnitsGrossGiftCertificateIssued(giftCertificateIssuedCount);

                totals.setAmountGrossGiftCardItemIssued(grossGiftCardIssuedAmount);
                totals.setUnitsGrossGiftCardItemIssued(grossGiftCardIssuedCount);
                totals.setAmountGrossGiftCardItemReloaded(grossGiftCardReloadedAmount);
                totals.setUnitsGrossGiftCardItemReloaded(grossGiftCardReloadedCount);
                totals.setAmountGrossGiftCardItemRedeemed(grossGiftCardRedeemedAmount);
                totals.setUnitsGrossGiftCardItemRedeemed(grossGiftCardRedeemedCount);
                totals.setAmountGrossGiftCardItemIssueVoided(grossGiftCardIssueVoidedAmount);
                totals.setUnitsGrossGiftCardItemIssueVoided(grossGiftCardIssueVoidedCount);
                totals.setAmountGrossGiftCardItemReloadVoided(grossGiftCardReloadVoidedAmount);
                totals.setUnitsGrossGiftCardItemReloadVoided(grossGiftCardReloadVoidedCount);
                totals.setAmountGrossGiftCardItemRedeemedVoided(grossGiftCardRedeemVoidedAmount);
                totals.setUnitsGrossGiftCardItemRedeemedVoided(grossGiftCardRedeemVoidedCount);

                totals.setHouseCardEnrollmentsApproved(houseAccountsApproved);
                totals.setHouseCardEnrollmentsDeclined(houseAccountsDeclined);

                totals.setAmountGrossGiftCardItemCredit(amountGrossGiftCardItemCredit);
                totals.setUnitsGrossGiftCardItemCredit(unitsGrossGiftCardItemCredit);
                totals.setAmountGrossGiftCardItemCreditVoided(amountGrossGiftCardItemCreditVoided);
                totals.setUnitsGrossGiftCardItemCreditVoided(unitsGrossGiftCardItemCreditVoided);
                totals.setAmountGrossGiftCertificatesRedeemed(amountGrossGiftCertificatesRedeemed);
                totals.setUnitsGrossGiftCertificatesRedeemed(unitsGrossGiftCertificatesRedeemed);
                totals.setAmountGrossGiftCertificatesRedeemedVoided(amountGrossGiftCertificatesRedeemedVoided);
                totals.setUnitsGrossGiftCertificatesRedeemedVoided(unitsGrossGiftCertificatesRedeemedVoided);
                totals.setAmountGrossStoreCreditsIssued(amountGrossStoreCreditsIssued);
                totals.setUnitsGrossStoreCreditsIssued(unitsGrossStoreCreditsIssued);
                totals.setAmountGrossStoreCreditsIssuedVoided(amountGrossStoreCreditsIssuedVoided);
                totals.setUnitsGrossStoreCreditsIssuedVoided(unitsGrossStoreCreditsIssuedVoided);
                totals.setAmountGrossStoreCreditsRedeemed(amountGrossStoreCreditsRedeemed);
                totals.setUnitsGrossStoreCreditsRedeemed(unitsGrossStoreCreditsRedeemed);
                totals.setAmountGrossStoreCreditsRedeemedVoided(amountGrossStoreCreditsRedeemedVoided);
                totals.setUnitsGrossStoreCreditsRedeemedVoided(unitsGrossStoreCreditsRedeemedVoided);
                totals.setAmountGrossItemEmployeeDiscount(amountGrossItemEmployeeDiscount);
                totals.setUnitsGrossItemEmployeeDiscount(unitsGrossItemEmployeeDiscount);
                totals.setAmountGrossItemEmployeeDiscountVoided(amountGrossItemEmployeeDiscountVoided);
                totals.setUnitsGrossItemEmployeeDiscountVoided(unitsGrossItemEmployeeDiscountVoided);
                totals.setAmountGrossTransactionEmployeeDiscount(amountGrossTransactionEmployeeDiscount);
                totals.setUnitsGrossTransactionEmployeeDiscount(unitsGrossTransactionEmployeeDiscount);
                totals.setAmountGrossTransactionEmployeeDiscountVoided(amountGrossTransactionEmployeeDiscountVoided);
                totals.setUnitsGrossTransactionEmployeeDiscountVoided(unitsGrossTransactionEmployeeDiscountVoided);
                totals.setAmountGrossGiftCertificateIssuedVoided(grossGiftCertificateIssuedVoidedAmount);
                totals.setUnitsGrossGiftCertificateIssuedVoided(grossGiftCertificateIssuedVoidedUnits);
                totals.setAmountGrossGiftCertificateTendered(grossGiftCertificatedTenderedAmount);
                totals.setUnitsGrossGiftCertificateTendered(grossGiftCertificatedTenderedUnits);
                totals.setAmountGrossGiftCertificateTenderedVoided(grossGiftCertificatedTenderedVoidedAmount);
                totals.setUnitsGrossGiftCertificateTenderedVoided(grossGiftCertificatedTenderedVoidedUnits);
                totals.setAmountEmployeeDiscounts(grossEmployeeDiscountAmount);
                totals.setUnitsEmployeeDiscounts(grossEmployeeDiscountUnits);
                totals.setAmountCustomerDiscounts(grossCustomerDiscountAmount);
                totals.setUnitsCustomerDiscounts(grossCustomerDiscountUnits);
                totals.setAmountPriceOverrides(priceOverridesAmount);
                totals.setUnitsPriceOverrides(priceOverridesUnits);
                totals.setUnitsPriceAdjustments(priceAdjustmentsUnits);
                totals.setTransactionsWithReturnedItemsCount(transactionsWithReturnedItemsCount);
                totals.setAmountBillPayments(totalBillPaymentAmount);
                totals.setCountBillPayments(totalBillPaymentCount);
                totals.setAmountChangeRoundedIn(amountChangeRoundedIn);
                totals.setAmountChangeRoundedOut(amountChangeRoundedOut);

                newRegister.setTotals(totals);
                registerVector.add(newRegister);
            }

            if (registerVector.isEmpty())
            {
                throw new DataException(NO_DATA, "selectWorkstationHistory");
            }

            /*
             * Get the workstation tender history records
             */
            for (RegisterIfc newRegister : registerVector)
            {
                selectWorkstationTenderHistory(dataConnection, newRegister);
            }

            rs.close();

        }
        catch (SQLException se)
        {
            throw new DataException(SQL_ERROR, "selectWorkstationHistory", se);
        }
        catch (DataException de)
        {
            logger.warn(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(UNKNOWN, "selectWorkstationHistory", e);
        }

        return registerVector;
    }

    /**
     * Fills in the workstation tender information for the given register
     * 
     * @param dataConnection connection to the db
     * @param register The register
     * @exception DataException upon error
     */
    public void selectWorkstationTenderHistory(JdbcDataConnection dataConnection,
                                               RegisterIfc register)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        /*
         * Add the desired table(s)
         */
        sql.addTable(TABLE_WORKSTATION_TENDER_HISTORY);
        /*
         * Add desired columns
         */
        sql.addColumn(FIELD_TENDER_TYPE_CODE);
        sql.addColumn(FIELD_TENDER_SUBTYPE);
        sql.addColumn(FIELD_CURRENCY_ISSUING_COUNTRY_CODE);
        sql.addColumn(FIELD_CURRENCY_ID); //I18N
        sql.addColumn(FIELD_WORKSTATION_TENDER_LOAN_MEDIA_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TENDER_OVER_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TENDER_PICKUP_MEDIA_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TENDER_SHORT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_BEGINNING_TENDER_MEDIA_UNIT_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_LOAN_MEDIA_UNIT_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_MEDIA_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_MEDIA_OVER_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_MEDIA_SHORT_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_PICKUP_MEDIA_UNIT_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TOTAL_TENDER_REFUND_MEDIA_UNIT_COUNT);
        sql.addColumn(FIELD_WORKSTATION_TENDER_REFUND_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TENDER_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TENDER_OPEN_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TENDER_CLOSE_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_TENDER_MEDIA_CLOSE_COUNT);
        sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_IN_MEDIA_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_OUT_MEDIA_TOTAL_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_IN_MEDIA_UNIT_COUNT);
        sql.addColumn(FIELD_WORKSTATION_FUNDS_RECEIVED_OUT_MEDIA_UNIT_COUNT);
        sql.addColumn(FIELD_RECONCILE_AMOUNT);
        sql.addColumn(FIELD_RECONCILE_MEDIA_UNIT_COUNT);
        /*
         * Add qualifiers
         */
        // For the specified register only
        EYSDate rp = getReportingPeriod(register);
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(register));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(register));
        sql.addQualifier(FIELD_FISCAL_YEAR + " = " + getFiscalYear(rp));
        sql.addQualifier(FIELD_REPORTING_PERIOD_TYPE_CODE + " = " + getReportingPeriodType(rp));
        sql.addQualifier(FIELD_REPORTING_PERIOD_ID + " = " + getReportingPeriodID(rp));

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            while (rs.next())
            {
                /*
                 * Grab the fields selected from the database
                 */
                int index = 0;
                String tenderCode = getSafeString(rs, ++index);
                String tenderSubType = spaceStringToEmptyString(getSafeString(rs, ++index));
                String currencyCode = getSafeString(rs, ++index);
                int currencyID = rs.getInt(++index); //I18N
                CurrencyIfc loanAmount = getCurrencyFromDecimal(rs, ++index, currencyCode);
                CurrencyIfc overAmount = getCurrencyFromDecimal(rs, ++index, currencyCode);
                CurrencyIfc pickupAmount = getCurrencyFromDecimal(rs, ++index, currencyCode);
                CurrencyIfc shortAmount = getCurrencyFromDecimal(rs, ++index, currencyCode);
                int openCount = rs.getInt(++index);
                int loanCount = rs.getInt(++index);
                int tenderCount = rs.getInt(++index);
                int overCount = rs.getInt(++index);
                int shortCount = rs.getInt(++index);
                int pickupCount = rs.getInt(++index);
                int refundCount = rs.getInt(++index);
                CurrencyIfc refundAmount = getCurrencyFromDecimal(rs, ++index, currencyCode);
                CurrencyIfc tenderAmount = getCurrencyFromDecimal(rs, ++index, currencyCode);
                CurrencyIfc openAmount = getCurrencyFromDecimal(rs, ++index, currencyCode);
                CurrencyIfc closeAmount = getCurrencyFromDecimal(rs, ++index, currencyCode);
                int closeCount = rs.getInt(++index);
                CurrencyIfc tillPayInsAmount            = getCurrencyFromDecimal(rs, ++index, currencyCode);
                CurrencyIfc tillPayOutsAmount           = getCurrencyFromDecimal(rs, ++index, currencyCode);
                int         tillPayInsCount             = rs.getInt(++index);
                int         tillPayOutsCount            = rs.getInt(++index);
                CurrencyIfc reconcileAmount             = getCurrencyFromDecimal(rs, ++index, currencyCode);
                int         reconcileCount              = rs.getInt(++index);
                /*
                 * Fill in the values
                 */
                TenderDescriptorIfc descriptor = DomainGateway.getFactory().getTenderDescriptorInstance();
                descriptor.setCountryCode(currencyCode);
                descriptor.setCurrencyID(currencyID); //I18N
                descriptor.setTenderSubType(tenderSubType);
                int tenderType = getTenderType(tenderCode);
                descriptor.setTenderType(tenderType);
                String tenderTypeDesc = tenderTypeMap.getDescriptor(tenderType);
                String summaryDesc = "";

                if (tenderType == TenderLineItemIfc.TENDER_TYPE_CHARGE)
                {
                    summaryDesc = tenderTypeDesc;
                    tenderTypeDesc = tenderSubType;
                }
                else // Assuming Charge is always in the local currency
                if (!currencyCode.equals(DomainGateway.getBaseCurrencyType().getCountryCode()))
                {
                    tenderTypeDesc = CountryCodeMap.getCountryDescriptor(currencyCode) + " " + tenderTypeDesc;
                }

                FinancialTotalsIfc totals = register.getTotals();

                if (openAmount.signum() != CurrencyIfc.ZERO)
                {
                    FinancialCountIfc startFloat = totals.getStartingFloatCount().getEntered();
                    startFloat.addTenderItemIn(tenderType, currencyCode, openCount, openAmount);
                }

                if (closeAmount.signum() != CurrencyIfc.ZERO)
                {
                    FinancialCountIfc endFloat = totals.getEndingFloatCount().getEntered();
                    endFloat.addTenderItemOut(tenderType, currencyCode, closeCount, closeAmount);
                }

                if (loanCount != 0)
                {
                    ReconcilableCountIfc loans = totals.instantiateReconcilableCountIfc();
                    loans.getEntered().addTenderItemIn(tenderType, currencyCode, loanCount, loanAmount);
                    totals.addTillLoans(loans);
                }

                if (pickupCount != 0)
                {
                    ReconcilableCountIfc pickups = totals.instantiateReconcilableCountIfc();
                    pickups.getEntered().addTenderItemOut(tenderType, currencyCode, pickupCount, pickupAmount);
                    totals.addTillPickups(pickups);
                    totals.addCountTillPickups(currencyCode, pickupCount);
                }

                if (tillPayInsCount != 0)
                {
                    ReconcilableCountIfc payIns = totals.instantiateReconcilableCountIfc();
                    payIns.getEntered().addTenderItemIn(tenderType, currencyCode, tillPayInsCount, tillPayInsAmount);
                    totals.addTillPayIns(payIns);
                }

                if (tillPayOutsCount != 0)
                {
                    ReconcilableCountIfc payOuts = totals.instantiateReconcilableCountIfc();
                    payOuts.getEntered().addTenderItemOut(tenderType, currencyCode, tillPayOutsCount, tillPayOutsAmount);
                    totals.addTillPayOuts(payOuts);
                }

                /*
                 * Sale/Return information
                 */
                FinancialCountIfc tender = totals.getTenderCount();
                tender.addTenderItem(descriptor,
                                     tenderCount + refundCount,
                                     refundCount,
                                     tenderAmount.add(refundAmount),
                                     refundAmount,
                                     tenderTypeDesc,
                                     summaryDesc,
                                     false);
                
                /*
                 * Add the reconcile amount and count to the combined count.
                 */
                ReconcilableCountIfc combined = totals.getCombinedCount();
                combined.getEntered().addTenderItemIn(descriptor, reconcileCount, reconcileAmount);
            }

            rs.close();
        }
        catch (SQLException se)
        {
            throw new DataException(SQL_ERROR, "selectWorkstationTenderHistory", se);
        }
        catch (DataException de)
        {
            logger.warn(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(UNKNOWN, "selectWorkstationTenderHistory", e);
        }
    }

    /**
     * Get last training mode customer sequence number. The number should be the
     * biggest sequence number in the latest business day.
     *
     * @param dataConnection jdbc data connection
     * @param register the register for running training mode
     * @return Integer sequence number
     * @throws DataException upon error
     * @deprecated as of 13.3 use non-training mode sequence instead 
     */
    public Integer getLastTrainingCustomerSequenceNumber(JdbcDataConnection dataConnection, RegisterIfc register)
            throws DataException
    {

        if (logger.isDebugEnabled())
        {
            logger.debug("JdbcReadRegister.getLastTrainingCustomerSequenceNumber()");
        }

        int seqNumber = 0;

        ResultSet rs  = null;

        // get latest business day
        String latestBSN = getLatestBusinessDay(dataConnection);

        if (latestBSN.length() != 0)
        {
            SQLSelectStatement sql = new SQLSelectStatement();

            // add tables
            sql.addTable(TABLE_WORKSTATION);

            // add max
            sql.addMaxFunction(FIELD_CUSTOMER_SEQUENCE_NUMBER);

            // add qualifiers
            if (register.getWorkstation() != null)
            {
                if (Util.isEmpty(register.getWorkstation().getStoreID()))
                {
                    sql.addQualifier(TABLE_WORKSTATION + "." + FIELD_STORE_ID + " = "
                            + register.getWorkstation().getStoreID());
                }
                if (Util.isEmpty(register.getWorkstation().getWorkstationID()))
                {
                    sql.addQualifier(TABLE_WORKSTATION + "." + FIELD_WORKSTATION_ID + " = "
                            + register.getWorkstation().getWorkstationID());
                }
            }
            sql.addQualifier(TABLE_WORKSTATION + "." + FIELD_BUSINESS_DAY_DATE + " = " + inQuotes(latestBSN));
            sql.addQualifier(TABLE_WORKSTATION + "." + FIELD_WORKSTATION_TRAINING_MODE_FLAG + " = " + inQuotes("1"));

            try
            {
                dataConnection.execute(sql.getSQLString());

                rs = (ResultSet)dataConnection.getResult();

                if (rs != null)
                {
                    if (rs.next())
                    {
                        seqNumber = rs.getInt(1);
                    }
                }
 
            }
            catch (DataException de)
            {
                logger.error(de.toString());
                throw de;
            }
            catch (SQLException se)
            {
                dataConnection.logSQLException(se, "workstation table");
                throw new DataException(DataException.SQL_ERROR, "workstation table", se);
            }
            finally
            {
                if (rs != null)
                {
                    try
                    {
                        rs.close();
                    }
                    catch (SQLException se)
                    {
                        throw new DataException(DataException.SQL_ERROR, "workstation table", se);
                    }
                }
            }
        }
        else
        {
            logger.error("Cannot find business day in getLastTrainingCustomerSequenceNumber()");
        }

        return new Integer(seqNumber);
    }

    /**
     * Get latest business day from CA_DY_BSN table.
     *
     * @param dataConnection jdbc data connection
     * @return String latest business day
     * @throws DataException upon error
     */
    public String getLatestBusinessDay(JdbcDataConnection dataConnection) throws DataException
    {
        String latestBSN = "";

        if (logger.isDebugEnabled())
        {
            logger.debug("JdbcReadTransaction.getLastestBSN()");
        }

        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables
        sql.addTable(TABLE_BUSINESS_DAY);

        // add max
        sql.addMaxFunction(FIELD_BUSINESS_DAY_DATE);

        try
        {
            dataConnection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (rs != null)
            {
                if (rs.next())
                {
                    latestBSN = getSafeString(rs, 1);
                }
            }
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "transaction table");
            throw new DataException(DataException.SQL_ERROR, "transaction table", se);
        }

        return latestBSN;
    }

    /**
     * Returns the tender type
     * 
     * @param tenderTypeCode String {@link TenderLineItemIfc}
     * @return int tender type
     */
    protected int getTenderType(String tenderTypeCode)
    {
        int value = tenderTypeMap.getTypeFromCode(tenderTypeCode);

        if (value == -1)
        {
            value = TenderLineItemIfc.TENDER_TYPE_CASH;
        }

        return value;
    }

    /**
     * Returns the tender type
     * 
     * @param tenderTypeCode The type of tender
     * @return the tender type
     */
    protected String getTenderTypeDesc(String tenderTypeCode)
    {
        return tenderTypeMap.getDescriptor(tenderTypeMap.getTypeFromCode(tenderTypeCode));
    }

    /**
     * Returns the reporting period
     * 
     * @param register The register object
     * @return the reporting period
     */
    protected EYSDate getReportingPeriod(RegisterIfc register)
    {
        /*
         * Use the business date for store level stuff
         */
        return (register.getBusinessDate());
    }

    /**
     * Returns the store ID for a workstation
     * 
     * @param workstation The Workstation
     * @return The store ID
     */
    protected String getStoreID(WorkstationIfc workstation)
    {
        return ("'" + workstation.getStoreID() + "'");
    }

    /**
     * Returns the workstation ID
     * 
     * @param workstation The workstation
     * @return The workstation ID
     */
    protected String getWorkstationID(WorkstationIfc workstation)
    {
        return ("'" + workstation.getWorkstationID() + "'");
    }

    /**
     * Returns the store ID
     * 
     * @param register A register
     * @return The store ID
     */
    protected String getStoreID(RegisterIfc register)
    {
        return ("'" + register.getWorkstation().getStoreID() + "'");
    }

    /**
     * Returns the workstation ID
     * 
     * @param register A Register
     * @return The workstation ID
     */
    protected String getWorkstationID(RegisterIfc register)
    {
        return ("'" + register.getWorkstation().getWorkstationID() + "'");
    }

    /**
     * Returns the last transaction sequence number.
     * 
     * @param register A register
     * @return The last transaction sequence number
     */
    protected int getLastSequenceNumber(RegisterIfc register)
    {
        return (register.getLastTransactionSequenceNumber());
    }

    /**
     * Returns the database string for the start timestamp.
     * 
     * @param startTime The starting timestamp
     * @return The starting timestamp for the register session
     */
    protected String getStartTime(EYSDate startTime)
    {
        return (dateToSQLTimestampString(startTime.dateValue()));
    }

    /**
     * Instantiates a register object.
     * 
     * @return new RegisterIfc object
     */
    protected RegisterIfc instantiateRegister()
    {
        return (DomainGateway.getFactory().getRegisterInstance());
    }

    /**
     * Instantiates a financial totals object.
     * 
     * @return new FinancialTotalsIfc object
     */
    protected FinancialTotalsIfc instantiateFinancialTotals()
    {
        return (DomainGateway.getFactory().getFinancialTotalsInstance());
    }

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}
