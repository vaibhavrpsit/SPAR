/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadTill.java /main/21 2014/07/09 13:10:48 icole Exp $
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
 *    nkgautam  08/02/10 - bill payment changes
 *    cgreene   06/23/10 - synchd column order with primary key
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    jswan     08/13/09 - Added the till reconcile amount and count to the
 *                         till, workstation and store tender history tables
 *                         and to code that reads from and writes to them.
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
 *    9    360Commerce 1.8         4/25/2007 10:01:12 AM  Anda D. Cadar   I18N
 *         merge
 *    8    360Commerce 1.7         2/6/2007 11:04:52 AM   Anil Bondalapati
 *         Merge from JdbcReadTill.java, Revision 1.5.1.0 
 *    7    360Commerce 1.6         12/8/2006 5:01:15 PM   Brendan W. Farrell
 *         Read the tax history when creating pos log for openclosetill
 *         transactions.  Rewrite of some code was needed.
 *    6    360Commerce 1.5         7/21/2006 2:27:37 PM   Brendan W. Farrell
 *         Merge fixes from v7.x.  These changes let services extend tax.
 *    5    360Commerce 1.4         1/25/2006 4:11:18 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         1/22/2006 11:41:21 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:42 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:46 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:00 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:18    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:42     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:46     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:00     Robert Pearse
 *
 *   Revision 1.18  2004/08/26 20:22:25  lzhao
 *   @scr 6882: add set open/close/lastStatusChange time for till.
 *
 *   Revision 1.17  2004/06/15 00:44:31  jdeleau
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
 *   Revision 1.6  2004/02/25 04:42:30  crain
 *   @scr 3814 Gift Certificate Issued
 *
 *   Revision 1.5  2004/02/17 17:57:39  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:50  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:27  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:14   CSchellenger
 * Initial revision.
 *
 *    Rev 1.5   Jul 14 2003 18:51:48   sfl
 * Added the code to retrieve the counts of voided transactions from the TillHistory table.
 * Resolution for POS SCR-2764: Till Summary Report - Net Trans. Taxable and Tax line items count fields incorrect
 *
 *    Rev 1.4   Jul 01 2003 13:27:20   jgs
 * Fixed problem which doubled pickup counts.
 * Resolution for 1157: Add task for Importing IX Retail Transactions.
 *
 *    Rev 1.3   Feb 12 2003 18:54:58   DCobb
 * Set signOn/Off operator in selectStoreTills().
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.2   Dec 20 2002 11:15:26   DCobb
 * Add floating till.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.1   11 Jun 2002 16:25:04   jbp
 * changes to report markdowns
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.0   Jun 03 2002 16:38:24   msg
 * Initial revision.
 *
 *    Rev 1.2   22 Apr 2002 15:43:12   sfl
 * Added new database table columns when
 * reading data from the TillHistory table so
 * that financial totals gross values will be
 * read and used directly instead of having
 * further computation based on the net values.
 * Resolution for POS SCR-1579: Store gross figures in the DB (financials)
 *
 *    Rev 1.1   Mar 18 2002 22:47:46   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:07:44   msg
 * Initial revision.
 *
 *    Rev 1.13   01 Mar 2002 15:17:18   pdd
 * Converted to use TenderTypeMapIfc for tender codes and descriptors.
 * Resolution for POS SCR-627: Make the Tender type list extendible.
 *
 *    Rev 1.12   22 Feb 2002 10:28:26   sfl
 * Applied the new methods to resolve the problem in Oracle8i
 * when inserting empty string value into not null database
 * table column.
 * Resolution for Domain SCR-33: Port POS 5.0 to Oracle8i
 *
 *    Rev 1.11   19 Feb 2002 16:35:04   epd
 * fixed problem in reading till and calculating combined entered amount
 * Resolution for POS SCR-1339: Reconcile Till over-value over doubles in Till Summary Report
 *
 *    Rev 1.10   Feb 05 2002 16:33:42   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 *
 *    Rev 1.9   17 Jan 2002 15:41:42   pdd
 * Set the nationality in the description for tenders as needed and the summary description for charge.
 * Resolution for POS SCR-370: 5.0 Summary report tender summary updates
 *
 *    Rev 1.8   15 Jan 2002 16:58:40   pdd
 * Added the pickup count by currency.
 * Resolution for POS SCR-370: 5.0 Summary report tender summary updates
 *
 *    Rev 1.7   09 Jan 2002 13:13:30   pdd
 * Using addTenderItemIn(descriptor, ...) in selectTillTenderHistory().
 * Resolution for POS SCR-370: 5.0 Summary report tender summary updates
 *
 *    Rev 1.6   04 Jan 2002 10:29:28   pdd
 * Added tender subtype.
 * Resolution for POS SCR-370: 5.0 Summary report tender summary updates
 *
 *    Rev 1.5   21 Dec 2001 10:09:10   sfl
 * Included the shipping total amount and total shipping
 * count information when read data from TillHistory table.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.4   20 Dec 2001 22:17:58   pdd
 * Added support for tender type and currency code.
 * Resolution for POS SCR-370: 5.0 Summary report tender summary updates
 *
 *    Rev 1.3   02 Dec 2001 12:48:00   mpm
 * Implemented financials, voids for special order domain objects.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.2   20 Nov 2001 08:56:12   epd
 * Added check for zero length string
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.1   26 Oct 2001 09:56:52   epd
 * The TIll table now has two new columns for sign-on and
 * sign-off operators.  This Data Operation has been updated
 * to use these new operators.  Also,  basic operator_id field
 * is no longer used.
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 20 2001 16:00:12   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:18   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;
// java imports
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Vector;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntity;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.CountryCodeMap;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;

//-------------------------------------------------------------------------
/**
    Abstract class that contains the database calls for reading tills.
    <P>
    @version $Revision: /main/21 $
**/
//-------------------------------------------------------------------------
public abstract class JdbcReadTill extends JdbcDataOperation
                                   implements ARTSDatabaseIfc
{
    /** serialVersionUID */
    private static final long serialVersionUID = -7637749684645034927L;
    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcReadTill.class);
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /main/21 $";
    // Tender type map
    protected static TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();

    //---------------------------------------------------------------------
    /**
       Reads the status information from the Till table.
       <p>
       @param  dataConnection  connection to the db
       @param  storeID         The store ID
       @param  tillID          The till ID
       @return till status
       @exception DataException upon error
    **/
    //---------------------------------------------------------------------
    public TillIfc selectTill(JdbcDataConnection dataConnection,
                              String storeID,
                              String tillID)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Add the desired tables (and aliases)
         */
        sql.addTable(TABLE_TILL, ALIAS_TILL);

        /*
         * Add desired columns
         */
        sql.addColumn(FIELD_TILL_SIGNON_OPERATOR);
        sql.addColumn(FIELD_TILL_SIGNOFF_OPERATOR);
        sql.addColumn(FIELD_TILL_STATUS_CODE);
        sql.addColumn(FIELD_WORKSTATION_ID);
        sql.addColumn(FIELD_BUSINESS_DAY_DATE);
        sql.addColumn(FIELD_WORKSTATION_ACCOUNTABILITY);
        sql.addColumn(FIELD_TILL_TYPE);
        
        /*
         * Add Qualifier(s)
         */
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(storeID));
        sql.addQualifier(FIELD_TENDER_REPOSITORY_ID + " = " + getTillID(tillID));

        TillIfc till = null;
        try
        {
            dataConnection.execute(sql.getSQLString());

            String signOnOperatorID = null;
            String signOffOperatorID = null;
            ResultSet rs = (ResultSet) dataConnection.getResult();

            if (rs.next())
            {
                int index = 0;
                signOnOperatorID = getSafeString(rs,++index);
                signOffOperatorID = getSafeString(rs,++index);
                int statusCode = rs.getInt(++index);
                String registerID = getSafeString(rs, ++index);
                EYSDate businessDate = getEYSDateFromString(rs, ++index);
                String accountability = getSafeString(rs, ++index);
                String tillType = getSafeString(rs, ++index);
                
                /*
                 * Initialize till object
                 */
                till = instantiateTill();
                till.resetTotals();
                till.setTillID(tillID);
                till.setStatus(statusCode);
                till.setBusinessDate(businessDate);
                till.setRegisterAccountability(Integer.parseInt(accountability));
                till.setTillType(Integer.parseInt(tillType));
                till.setRegisterID(registerID);

                EYSDate openTime = getTillOpenCloseTime(dataConnection, storeID, tillID, TransactionIfc.TYPE_OPEN_TILL);
                EYSDate closeTime = getTillOpenCloseTime(dataConnection, storeID, tillID, TransactionIfc.TYPE_CLOSE_TILL);
                EYSDate lastStatusChangeTime = getLastStatusChangeTime(dataConnection, storeID, tillID);
                if ( openTime != null )
                {
                    till.setOpenTime(openTime);
                }
                if ( closeTime != null )
                {
                    till.setCloseTime(closeTime);
                }
                if ( lastStatusChangeTime != null && till instanceof AbstractStatusEntity )
                {
                    ((AbstractStatusEntity)till).setLastStatusChangeTime(lastStatusChangeTime);
                }
            }
            else
            {
                throw new DataException(DataException.NO_DATA, "selectTill: Till not found.");
            }

            if (signOnOperatorID != null && signOnOperatorID.length() > 0)
            {
                till.setSignOnOperator(getEmployee(dataConnection, signOnOperatorID));
            }
            if (signOffOperatorID != null && signOffOperatorID.length() > 0)
            {
                till.setSignOffOperator(getEmployee(dataConnection, signOffOperatorID));
            }


            rs.close();
        }
        catch (DataException de)
        {
            if (de.getErrorCode() == DataException.NO_DATA)
            {
                logger.warn(de.toString());
            }
            else
            {
                logger.error("Could not read till " + tillID, de);
            }
            throw de;
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "selectTill: Till table", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "selectTill: Till table", e);
        }

        return(till);
    }

    //---------------------------------------------------------------------
    /**
       Returns a list of reconciled tills.
       <P>
       @param  dataConnection  connection to the db
       @param  storeID         The store ID
       @param  tillID          The till ID
       @param  businessDate    The business day
       @return List of tills for the business day
       @exception DataException upon error
    **/
    //---------------------------------------------------------------------
    public Vector selectTillHistory(JdbcDataConnection dataConnection,
                                    String storeID,
                                    String tillID,
                                    EYSDate businessDate)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Add the desired tables (and aliases)
         */
        sql.addTable(TABLE_TILL_HISTORY, ALIAS_TILL_HISTORY);

        /*
         * Add desired columns
         */
        sql.addColumn(FIELD_TILL_HISTORY_STATUS_CODE);
        sql.addColumn(FIELD_TILL_START_DATE_TIMESTAMP);
        sql.addColumn(FIELD_CURRENCY_ID); //I18N

        // These are in the same order as Store History
        sql.addColumn(FIELD_TILL_TOTAL_NO_SALE_TRANSACTION_COUNT);
        sql.addColumn(FIELD_TILL_TOTAL_SALE_LINE_ITEM_COUNT);
        sql.addColumn(FIELD_TILL_TOTAL_TAX_EXEMPT_TRANSACTION_COUNT);
        sql.addColumn(FIELD_TILL_TAX_EXEMPT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_INCLUSIVE_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_SALES_EX_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TOTAL_DISCOUNT_COUNT);
        sql.addColumn(FIELD_TILL_DISCOUNT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TOTAL_MARKDOWN_COUNT);
        sql.addColumn(FIELD_TILL_MARKDOWN_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TOTAL_MISCELLANEOUS_DISCOUNT_COUNT);
        sql.addColumn(FIELD_TILL_MISCELLANEOUS_DISCOUNT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TOTAL_REFUND_COUNT);
        sql.addColumn(FIELD_TILL_REFUND_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TOTAL_RETURN_COUNT);
        sql.addColumn(FIELD_TILL_RETURN_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TENDER_PICKUP_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TENDER_LOAN_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TOTAL_TENDER_PICKUP_COUNT);
        sql.addColumn(FIELD_TILL_TOTAL_TRANSACTION_VOID_COUNT);
        sql.addColumn(FIELD_TILL_TOTAL_POST_TRANSACTION_VOID_COUNT);
        sql.addColumn(FIELD_TILL_POST_TRANSACTION_VOID_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TOTAL_LINE_ITEM_VOID_COUNT);
        sql.addColumn(FIELD_TILL_LINE_ITEM_VOID_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TOTAL_TRANSACTION_COUNT);
        sql.addColumn(FIELD_TILL_TOTAL_TENDER_LOAN_COUNT);
        sql.addColumn(FIELD_TILL_TRANSACTION_VOID_TOTAL_AMOUNT);
        // Additional fields not defined in ARTS
            // StoreCouponDiscounts
        sql.addColumn(FIELD_TILL_ITEM_DISCOUNT_STORE_COUPON_AMOUNT);
        sql.addColumn(FIELD_TILL_ITEM_DISCOUNT_STORE_COUPON_COUNT);
        sql.addColumn(FIELD_TILL_TRANSACTION_DISCOUNT_STORE_COUPON_AMOUNT);
        sql.addColumn(FIELD_TILL_TRANSACTION_DISCOUNT_STORE_COUPON_COUNT);
            //
        sql.addColumn(FIELD_TILL_NONTAXABLE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TOTAL_NONTAXABLE_COUNT);
        sql.addColumn(FIELD_TILL_TOTAL_TAXABLE_COUNT);
        sql.addColumn(FIELD_TILL_REFUND_NONTAXABLE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TOTAL_NONTAXABLE_REFUND_COUNT);
        sql.addColumn(FIELD_TILL_REFUND_TAX_EXEMPT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TOTAL_TAX_EXEMPT_REFUND_COUNT);
        sql.addColumn(FIELD_TILL_LINE_ITEM_SALES_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_LINE_ITEM_NONTAXABLE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TOTAL_NONTAXABLE_LINE_ITEM_COUNT);
        sql.addColumn(FIELD_TILL_LINE_ITEM_TAX_EXEMPT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TOTAL_TAX_EXEMPT_LINE_ITEM_COUNT);
        sql.addColumn(FIELD_TILL_RETURN_NONTAXABLE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TOTAL_NONTAXABLE_RETURN_COUNT);
        sql.addColumn(FIELD_TILL_RETURN_TAX_EXEMPT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TOTAL_TAX_EXEMPT_RETURN_COUNT);
        sql.addColumn(FIELD_TILL_REFUND_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_REFUND_INCLUSIVE_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_RETURN_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_RETURN_INCLUSIVE_TAX_TOTAL_AMOUNT);

        sql.addColumn(FIELD_TILL_NONMERCH_NONTAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TOTAL_NONMERCH_NONTAX_COUNT);
        sql.addColumn(FIELD_TILL_RETURN_NONMERCH_NONTAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TOTAL_NONMERCH_NONTAX_RETURN_COUNT);
        sql.addColumn(FIELD_TILL_NONMERCH_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TOTAL_NONMERCH_TAX_COUNT);
        sql.addColumn(FIELD_TILL_RETURN_NONMERCH_TAX_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TOTAL_NONMERCH_TAX_RETURN_COUNT);
        sql.addColumn(FIELD_TILL_GIFT_CARD_AMOUNT);
        sql.addColumn(FIELD_TILL_GIFT_CARD_COUNT);
        sql.addColumn(FIELD_TILL_RETURN_GIFT_CARD_AMOUNT);
        sql.addColumn(FIELD_TILL_GIFT_CARD_RETURN_COUNT);
        sql.addColumn(FIELD_TILL_HOUSE_PAYMENT_AMOUNT);
        sql.addColumn(FIELD_TILL_HOUSE_PAYMENT_COUNT);
        sql.addColumn(FIELD_TILL_RESTOCKING_FEE_AMOUNT);
        sql.addColumn(FIELD_TILL_RESTOCKING_FEE_COUNT);
        sql.addColumn(FIELD_TILL_RESTOCKING_FEE_AMOUNT_FROM_NON_TAXABLE);
        sql.addColumn(FIELD_TILL_RESTOCKING_FEE_COUNT_FROM_NON_TAXABLE);
        sql.addColumn(FIELD_TILL_TOTAL_LAYAWAY_PAYMENTS_COLLECTED_COUNT);
        sql.addColumn(FIELD_TILL_LAYAWAY_PAYMENTS_COLLECTED_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_LAYAWAY_NEW_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_LAYAWAY_PICKUP_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TOTAL_LAYAWAY_DELETIONS_DISBURSEMENT_COUNT);
        sql.addColumn(FIELD_TILL_LAYAWAY_DELETIONS_DISBURSEMENT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TOTAL_LAYAWAY_INITIATION_FEES_COLLECTED_COUNT);
        sql.addColumn(FIELD_TILL_LAYAWAY_INITIATION_FEES_COLLECTED_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TOTAL_LAYAWAY_DELETION_FEES_COUNT);
        sql.addColumn(FIELD_TILL_LAYAWAY_DELETION_FEES_COLLECTED_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_SPECIAL_ORDER_NEW_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_SPECIAL_ORDER_PARTIAL_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TOTAL_ORDER_PAYMENTS_COLLECTED_COUNT);
        sql.addColumn(FIELD_TILL_ORDER_PAYMENTS_COLLECTED_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TOTAL_ORDER_CANCELS_DISBURSEMENT_COUNT);
        sql.addColumn(FIELD_TILL_ORDER_CANCELS_DISBURSEMENT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_SHIPPING_CHARGE_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TOTAL_SHIPPING_CHARGE_COUNT);
        sql.addColumn(FIELD_TILL_SHIPPING_CHARGE_TAX_AMOUNT);
        sql.addColumn(FIELD_TILL_SHIPPING_CHARGE_INCLUSIVE_TAX_AMOUNT);
        sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_IN_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_OUT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_IN_UNIT_COUNT);
        sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_OUT_UNIT_COUNT);

        // Gross value related columns

        sql.addColumn(FIELD_TILL_GROSS_TAXABLE_ITEM_SALES_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_TAXABLE_ITEM_SALES_COUNT);
        sql.addColumn(FIELD_TILL_GROSS_TAXABLE_ITEM_RETURNS_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_TAXABLE_ITEM_RETURNS_COUNT);
        sql.addColumn(FIELD_TILL_ITEM_SALES_TAX_AMOUNT);
        sql.addColumn(FIELD_TILL_ITEM_SALES_INCLUSIVE_TAX_AMOUNT);
        sql.addColumn(FIELD_TILL_TRANSACTION_SALES_TAX_AMOUNT);
        sql.addColumn(FIELD_TILL_TRANSACTION_SALES_INCLUSIVE_TAX_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_TAXABLE_TRANSACTION_SALES_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_NON_TAXABLE_TRANSACTION_SALES_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_TAX_EXEMPT_TRANSACTION_SALES_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_NON_TAXABLE_TRANSACTION_SALES_COUNT);
        sql.addColumn(FIELD_TILL_GROSS_TAXABLE_TRANSACTION_SALES_COUNT);
        sql.addColumn(FIELD_TILL_GROSS_TAX_EXEMPT_TRANSACTION_SALES_COUNT);
        sql.addColumn(FIELD_TILL_GROSS_TAXABLE_TRANSACTION_RETURNS_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_TAXABLE_TRANSACTION_RETURNS_COUNT);
        sql.addColumn(FIELD_TILL_GROSS_TAXABLE_NON_MERCHANDISE_SALES_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_TAXABLE_NON_MERCHANDISE_SALES_COUNT);
        sql.addColumn(FIELD_TILL_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_NON_TAXABLE_NON_MERCHANDISE_SALES_COUNT);
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CARD_ITEM_SALES_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CARD_ITEM_SALES_COUNT);
        sql.addColumn(FIELD_GROSS_TAXABLE_SALES_VOID_COUNT);
        sql.addColumn(FIELD_GROSS_TAXABLE_RETURNS_VOID_COUNT);
        sql.addColumn(FIELD_GROSS_NONTAXABLE_SALES_VOID_COUNT);
        sql.addColumn(FIELD_GROSS_NONTAXABLE_RETURNS_VOID_COUNT);
        sql.addColumn(FIELD_TILL_GIFT_CERTIFICATE_ISSUED_AMOUNT);
        sql.addColumn(FIELD_TILL_GIFT_CERTIFICATE_ISSUED_COUNT);
        sql.addColumn(FIELD_TILL_GIFT_CARD_ISSUED_AMOUNT);
        sql.addColumn(FIELD_TILL_GIFT_CARD_ISSUED_COUNT);
        sql.addColumn(FIELD_TILL_GIFT_CARD_RELOADED_AMOUNT);
        sql.addColumn(FIELD_TILL_GIFT_CARD_RELOADED_COUNT);
        sql.addColumn(FIELD_TILL_GIFT_CARD_REDEEMED_AMOUNT);
        sql.addColumn(FIELD_TILL_GIFT_CARD_REDEEMED_COUNT);
        sql.addColumn(FIELD_TILL_GIFT_CARD_ISSUE_VOIDED_AMOUNT);
        sql.addColumn(FIELD_TILL_GIFT_CARD_ISSUE_VOIDED_COUNT);
        sql.addColumn(FIELD_TILL_GIFT_CARD_RELOAD_VOIDED_AMOUNT);
        sql.addColumn(FIELD_TILL_GIFT_CARD_RELOAD_VOIDED_COUNT);
        sql.addColumn(FIELD_TILL_GIFT_CARD_REDEEM_VOIDED_AMOUNT);
        sql.addColumn(FIELD_TILL_GIFT_CARD_REDEEM_VOIDED_COUNT);

        sql.addColumn(FIELD_TILL_HOUSE_ACCOUNT_ENROLLMENT_APPROVED_COUNT);
        sql.addColumn(FIELD_TILL_HOUSE_ACCOUNT_ENROLLMENT_DECLINED_COUNT);

        sql.addColumn(FIELD_TILL_GROSS_GIFT_CARD_ITEM_CREDIT_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CARD_ITEM_CREDIT_UNITS);
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CARD_ITEM_CREDIT_VOIDED_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CARD_ITEM_CREDIT_VOIDED_UNITS);
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CERTIFICATE_REDEEMED_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CERTIFICATE_REDEEMED_UNITS);
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CERTIFICATE_REDEEMED_VOIDED_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CERTIFICATE_REDEEMED_VOIDED_UNITS);
        sql.addColumn(FIELD_TILL_GROSS_STORE_CREDITS_ISSUED_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_STORE_CREDITS_ISSUED_UNITS);
        sql.addColumn(FIELD_TILL_GROSS_STORE_CREDITS_ISSUED_VOIDED_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_STORE_CREDITS_ISSUED_VOIDED_UNITS);
        sql.addColumn(FIELD_TILL_GROSS_STORE_CREDITS_REDEEMED_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_STORE_CREDITS_REDEEMED_UNITS);
        sql.addColumn(FIELD_TILL_GROSS_STORE_CREDITS_REDEEMED_VOIDED_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_STORE_CREDITS_REDEEMED_VOIDED_UNITS);
        sql.addColumn(FIELD_TILL_GROSS_ITEM_EMPLOYEE_DISCOUNT_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_ITEM_EMPLOYEE_DISCOUNT_UNITS);
        sql.addColumn(FIELD_TILL_GROSS_ITEM_EMPLOYEE_DISCOUNT_VOIDED_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_ITEM_EMPLOYEE_DISCOUNT_VOIDED_UNITS);
        sql.addColumn(FIELD_TILL_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_UNITS);
        sql.addColumn(FIELD_TILL_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_VOIDED_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_TRANSACTION_EMPLOYEE_DISCOUNT_VOIDED_UNITS);
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CERTIFICATE_ISSUED_VOIDED_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CERTIFICATE_ISSUED_VOIDED_COUNT);
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CERTIFICATE_TENDERED_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CERTIFICATE_TENDERED_COUNT);
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CERTIFICATE_TENDERED_VOIDED_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_GIFT_CERTIFICATE_TENDERED_VOIDED_COUNT);
        sql.addColumn(FIELD_TILL_GROSS_EMPLOYEE_DISCOUNTS_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_EMPLOYEE_DISCOUNTS_COUNT);
        sql.addColumn(FIELD_TILL_GROSS_CUSTOMER_DISCOUNTS_AMOUNT);
        sql.addColumn(FIELD_TILL_GROSS_CUSTOMER_DISCOUNTS_COUNT);
        sql.addColumn(FIELD_TILL_PRICE_OVERRIDES_AMOUNT);
        sql.addColumn(FIELD_TILL_PRICE_OVERRIDES_COUNT);
        sql.addColumn(FIELD_TILL_PRICE_ADJUSTMENTS_COUNT);
        sql.addColumn(FIELD_TILL_TRANSACTIONS_WITH_RETURNED_ITEMS_COUNT);
        sql.addColumn(FIELD_STORE_TOTAL_BILLPAYMENT);
        sql.addColumn(FIELD_STORE_TOTAL_BILLPAYMENT_COUNT);
        sql.addColumn(FIELD_TOTAL_AMOUNT_CHANGE_ROUNDED_IN);
        sql.addColumn(FIELD_TOTAL_AMOUNT_CHANGE_ROUNDED_OUT);
        /*
         * Add Qualifier(s)
         */

        // For the specified till only
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(storeID));
        sql.addQualifier(FIELD_TENDER_REPOSITORY_ID + " = " + getTillID(tillID));

        // For the specified business day only
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDate(businessDate));

        /*
         * Add Ordering(s)
         */

        // Reverse sort on the starting timestamp
        sql.addOrdering(FIELD_TILL_START_DATE_TIMESTAMP + " DESC");

        Vector tillVector = new Vector(2);
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
                // StoreCouponDiscounts
                CurrencyIfc itemDiscStoreCouponAmount = getCurrencyFromDecimal(rs, ++index);
                int  itemDiscStoreCouponCount = rs.getInt(++index);
                CurrencyIfc transactionDiscStoreCouponAmount = getCurrencyFromDecimal(rs, ++index);
                int  transactionDiscStoreCouponCount = rs.getInt(++index);
                //
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
                CurrencyIfc grossGiftCertificateIssuedAmount  = getCurrencyFromDecimal(rs, ++index);
                BigDecimal  grossGiftCertificateIssuedCount   = getBigDecimal(rs, ++index);
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
                TillIfc till = instantiateTill();
                till.setTillID(tillID);
                till.setStatus(statusCode);
                till.setOpenTime(openTime);
                till.setBusinessDate(businessDate);

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
                    //
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
                totals.setAmountGrossGiftCertificateIssued(grossGiftCertificateIssuedAmount);
                totals.setUnitsGrossGiftCertificateIssued(grossGiftCertificateIssuedCount);
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
                till.setTotals(totals);
                totals.setAmountChangeRoundedIn(amountChangeRoundedIn);
                totals.setAmountChangeRoundedOut(amountChangeRoundedOut);
                tillVector.addElement(till);
            }

            if (tillVector.isEmpty())
            {
                throw new DataException(NO_DATA, "selectTillHistory");
            }

            /*
             * Get the till tender history records
             */
            Enumeration e = tillVector.elements();
            while (e.hasMoreElements())
            {
                TillIfc till = (TillIfc)e.nextElement();
                selectTillTenderHistory(dataConnection, storeID, till);
            }

            rs.close();


            /*
             * Should get the operator(s) here
             */
        }
        catch (SQLException se)
        {
            throw new DataException(SQL_ERROR, "selectTillHistory", se);
        }
        catch (DataException de)
        {
            logger.warn( "" + de + "");
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(UNKNOWN, "selectTillHistory", e);
        }

        return(tillVector);
    }

    //---------------------------------------------------------------------
    /**
       Fills in the till tender information for the given till
       <p>
       @param  dataConnection  connection to the db
       @param  storeID         The store ID
       @param  till            The till
       @exception DataException upon error
    **/
    //---------------------------------------------------------------------
    public void selectTillTenderHistory(JdbcDataConnection dataConnection,
                                        String storeID,
                                        TillIfc till)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        /*
         * Add the desired table(s)
         */
        sql.addTable(TABLE_TILL_TENDER_HISTORY);
        /*
         * Add desired columns
         */
        sql.addColumn(FIELD_TENDER_TYPE_CODE);
        sql.addColumn(FIELD_TENDER_SUBTYPE);
        sql.addColumn(FIELD_CURRENCY_ISSUING_COUNTRY_CODE);
        sql.addColumn(FIELD_CURRENCY_ID); //I18N
        //sql.addColumn(FIELD_TILL_TENDER_DEPOSIT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TENDER_LOAN_MEDIA_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TENDER_OVER_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TENDER_PICKUP_MEDIA_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TENDER_SHORT_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TOTAL_BEGINNING_TENDER_MEDIA_UNIT_COUNT);
        //sql.addColumn(FIELD_TILL_TOTAL_TENDER_DEPOSIT_MEDIA_UNIT_COUNT);
        sql.addColumn(FIELD_TILL_TOTAL_TENDER_LOAN_MEDIA_UNIT_COUNT);
        sql.addColumn(FIELD_TILL_TOTAL_TENDER_MEDIA_COUNT);
        sql.addColumn(FIELD_TILL_TOTAL_TENDER_MEDIA_OVER_COUNT);
        sql.addColumn(FIELD_TILL_TOTAL_TENDER_MEDIA_SHORT_COUNT);
        sql.addColumn(FIELD_TILL_TOTAL_TENDER_PICKUP_MEDIA_UNIT_COUNT);
        sql.addColumn(FIELD_TILL_TOTAL_TENDER_REFUND_MEDIA_UNIT_COUNT);
        sql.addColumn(FIELD_TILL_TENDER_REFUND_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TENDER_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_TENDER_OPEN_AMOUNT);
        sql.addColumn(FIELD_TILL_TENDER_CLOSE_AMOUNT);
        sql.addColumn(FIELD_TILL_TENDER_MEDIA_CLOSE_COUNT);
        sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_IN_MEDIA_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_OUT_MEDIA_TOTAL_AMOUNT);
        sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_IN_MEDIA_UNIT_COUNT);
        sql.addColumn(FIELD_TILL_FUNDS_RECEIVED_OUT_MEDIA_UNIT_COUNT);
        sql.addColumn(FIELD_RECONCILE_AMOUNT);
        sql.addColumn(FIELD_RECONCILE_MEDIA_UNIT_COUNT);
        /*
         * Add qualifiers
         */
        // For the specified till only
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(storeID));
        sql.addQualifier(FIELD_TENDER_REPOSITORY_ID + " = " + getTillID(till.getTillID()));
        sql.addQualifier(FIELD_TILL_START_DATE_TIMESTAMP + " = "
                         + dateToSQLTimestampString(till.getOpenTime().dateValue()));

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
                //CurrencyIfc depositAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc loanAmount = getCurrencyFromDecimal(rs, ++index, currencyCode);
                CurrencyIfc overAmount = getCurrencyFromDecimal(rs, ++index, currencyCode);
                CurrencyIfc pickupAmount = getCurrencyFromDecimal(rs, ++index, currencyCode);
                CurrencyIfc shortAmount = getCurrencyFromDecimal(rs, ++index, currencyCode);
                int openCount = rs.getInt(++index);
                //int depositCount = rs.getInt(++index);
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
                descriptor.setTenderSubType(tenderSubType);
                descriptor.setCurrencyID(currencyID); //I18N
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
                FinancialTotalsIfc totals = till.getTotals();

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
            throw new DataException(SQL_ERROR, "selectTillTenderHistory", se);
        }
        catch (DataException de)
        {
            logger.warn( "" + de + "");
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(UNKNOWN, "selectTillTenderHistory", e);
        }
    }

    //---------------------------------------------------------------------
    /**
       Reads all the store tills for the business day.
       <p>
       @param  dataConnection  connection to the db
       @param  store The store
       @return List of tills for the store the given business day.
       @exception DataException upon error
    **/
    //---------------------------------------------------------------------
    public Vector selectStoreTills(JdbcDataConnection dataConnection,
                                   ARTSStore store)
        throws DataException
    {

        String storeID = store.getPosStore().getStoreID();
        EYSDate businessDate = store.getBusinessDate();

        SQLSelectStatement sql = new SQLSelectStatement();

        //Add the desired tables (and aliases)
        sql.addTable(TABLE_TILL, ALIAS_TILL);

        //Add desired columns
        sql.addColumn(FIELD_TENDER_REPOSITORY_ID);
        sql.addColumn(FIELD_TILL_SIGNON_OPERATOR);
        sql.addColumn(FIELD_TILL_SIGNOFF_OPERATOR);
        sql.addColumn(FIELD_TILL_STATUS_CODE);
        sql.addColumn(FIELD_WORKSTATION_ACCOUNTABILITY);
        sql.addColumn(FIELD_TILL_TYPE);

        //Add Qualifier(s)
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(storeID));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDate(businessDate));

        Vector tillVector = new Vector(2);

        try
        {
            dataConnection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet) dataConnection.getResult();

            while (rs.next())
            {
                int index = 0;
                String tillID = getSafeString(rs,++index);
                String signOnOperatorID = getSafeString(rs,++index);
                String signOffOperatorID = getSafeString(rs,++index);
                int statusCode = rs.getInt(++index);
                String accountability = getSafeString(rs, ++index);
                String tillType = getSafeString(rs, ++index);

                TillIfc till = instantiateTill();
                till.resetTotals();
                till.setTillID(tillID);

                if (signOnOperatorID != null && signOnOperatorID.length() > 0)
                {
                    // temporary holder for signOnOperatorID
                    EmployeeIfc signOnOperator = DomainGateway.getFactory().getEmployeeInstance();
                    signOnOperator.setEmployeeID(signOnOperatorID);
                    till.setSignOnOperator(signOnOperator);
                }
                if (signOffOperatorID != null && signOffOperatorID.length() > 0)
                {
                    // temporary holder for SignOffOperatorID
                    EmployeeIfc signOffOperator = DomainGateway.getFactory().getEmployeeInstance();
                    signOffOperator.setEmployeeID(signOffOperatorID);
                    till.setSignOnOperator(signOffOperator);
                }

                till.setStatus(statusCode);
                till.setBusinessDate(businessDate);
                till.setRegisterAccountability(Integer.parseInt(accountability));
                till.setTillType(Integer.parseInt(tillType));

                tillVector.addElement(till);
            }
            rs.close();
        }
        catch (DataException de)
        {
            logger.error( "" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "selectStoreTills: Till table", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "selectStoreTills: Till table", e);
        }

        // set SignOnOperator and SignOffOperator for each till in till vector
        try
        {
            int tillCount = tillVector.size();
            for (int i = 0; i < tillCount; i++)
            {
                TillIfc till = (TillIfc)tillVector.elementAt(i);
                // set operator
                EmployeeIfc operator = till.getSignOnOperator();
                String operatorID = null;
                if (operator != null)
                {
                    operatorID = operator.getEmployeeID();
                    till.setSignOnOperator(getEmployee(dataConnection, operatorID));
                }

                // set SignOffOperator
                operator = till.getSignOffOperator();
                if (operator != null)
                {
                    operatorID = operator.getEmployeeID();
                    till.setSignOffOperator(getEmployee(dataConnection, operatorID));
                }
            }
        }
        catch (DataException de)
        {
            logger.error( "Unable to set SignOn/Off operators for store tills. " + de + "");
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "selectStoreTills: Till table", e);
        }

        return(tillVector);
    }

    //---------------------------------------------------------------------
    /**
       Returns the tender type
       <p>
       @param  tenderTypeCode  The type of tender
       @return the tender type
    **/
    //---------------------------------------------------------------------
    protected String getTenderTypeDesc(String tenderTypeCode)
    {
        return tenderTypeMap.getDescriptor(tenderTypeMap.getTypeFromCode(tenderTypeCode));
    }

    //---------------------------------------------------------------------
    /**
       Returns the tender type
       <p>
       @param tenderTypeCode String {@link TenderLineItemIfc}
       @return int tender type
    **/
    //---------------------------------------------------------------------
    protected int getTenderType(String tenderTypeCode)
    {
        int value = tenderTypeMap.getTypeFromCode(tenderTypeCode);

        if (value == -1)
        {
            value = TenderLineItemIfc.TENDER_TYPE_CASH;
        }

        return value;
    }

    //---------------------------------------------------------------------
    /**
       Returns the SQL string representation of an EYSDate.
       <p>
       @param  date  An EYSDate
       @return The SQL string representation of an EYSDate.
    **/
    //---------------------------------------------------------------------
    protected String getBusinessDate(EYSDate date)
    {
        return(dateToSQLDateString(date.dateValue()));
    }

    //---------------------------------------------------------------------
    /**
       Returns the store ID
       <p>
       @param  storeID     The store id
       @return The store ID
    **/
    //---------------------------------------------------------------------
    protected String getStoreID(String storeID)
    {
        return("'" + storeID + "'");
    }

    //---------------------------------------------------------------------
    /**
       Returns the till ID
       <p>
       @param  tillID  The till ID
       @return The till ID
    **/
    //---------------------------------------------------------------------
    protected String getTillID(String tillID)
    {
        return("'" + tillID + "'");
    }

    //---------------------------------------------------------------------
    /**
       Instantiates a till object.
       <p>
       @return new TillIfc object
    **/
    //---------------------------------------------------------------------
    protected TillIfc instantiateTill()
    {
        return(DomainGateway.getFactory().getTillInstance());
    }

    //---------------------------------------------------------------------
    /**
       Instantiates a financial totals object.
       <p>
       @return new FinancialTotalsIfc object
    **/
    //---------------------------------------------------------------------
    protected FinancialTotalsIfc instantiateFinancialTotals()
    {
        return(DomainGateway.getFactory().getFinancialTotalsInstance());
    }


    //---------------------------------------------------------------------
    /**
     Gets till open-close transaction time. <P>
     @param connection connection to database
     @param storeID store id
     @param tillID  tillID
     @param transactionType transaction type, can be till open or till close
     @exception DataException thrown if error occurs
     **/
    //---------------------------------------------------------------------
    protected EYSDate getTillOpenCloseTime(JdbcDataConnection connection,
                                              String storeID,
                                              String tillID,
                                              int transactionType )
    throws DataException
    {
        EYSDate timestamp = null;

        SQLSelectStatement sql = new SQLSelectStatement();
        // set table
        sql.addTable(TABLE_TILL_OPEN_CLOSE_TRANSACTION);

        sql.addMaxFunction(FIELD_TRANSACTION_TIMESTAMP);

        // set qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID, inQuotes(storeID));
        sql.addQualifier(FIELD_TENDER_REPOSITORY_ID, inQuotes(tillID));
        sql.addQualifier(FIELD_TRANSACTION_TYPE_CODE, inQuotes(transactionType));
        try
        {
            // execute sql and get result set
            connection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet) connection.getResult();

            if (rs.next())
            {
                int index = 0;
                timestamp = timestampToEYSDate(rs, ++index);
            }
            else
            {
                throw new DataException(DataException.NO_DATA,
                        "Till open-close transaction time not found.");
            }
            rs.close();
        }
        catch(DataException de)
        {
            throw de;
        }
        catch(Exception e)
        {
            logger.error(
                    Util.throwableToString(e));
            throw new DataException(DataException.UNKNOWN,
                    "Error reading till open-close transaction time",
                    e);
        }

        return timestamp;

    }                                   // end getTillOpenCloseTime()

    //---------------------------------------------------------------------
    /**
     Gets till last status change time. <P>
     @param connection connection to database
     @param storeID store id
     @param tillID  tillID
     @exception DataException thrown if error occurs
     **/
    //---------------------------------------------------------------------
    protected EYSDate getLastStatusChangeTime(JdbcDataConnection connection,
                                              String storeID,
                                              String tillID )
    throws DataException
    {
        EYSDate timestamp = null;

        SQLSelectStatement sql = new SQLSelectStatement();
        // set table
        sql.addTable(TABLE_TILL_OPEN_CLOSE_TRANSACTION);

        sql.addMaxFunction(FIELD_TRANSACTION_TIMESTAMP);

        // set qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID, inQuotes(storeID));
        sql.addQualifier(FIELD_TENDER_REPOSITORY_ID, inQuotes(tillID));
        try
        {
            // execute sql and get result set
            connection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet) connection.getResult();

            if (rs.next())
            {
                int index = 0;
                timestamp = timestampToEYSDate(rs, ++index);
            }
            else
            {
                throw new DataException(DataException.NO_DATA,
                        "Till open-close transaction last status change time not found.");
            }
            rs.close();
        }
        catch(DataException de)
        {
            throw de;
        }
        catch(Exception e)
        {
            logger.error(
                    Util.throwableToString(e));
            throw new DataException(DataException.UNKNOWN,
                    "Error reading till open-close transaction time",
                    e);
        }

        return timestamp;

    }
                                   // end getLastStatusChangeTime()
    //---------------------------------------------------------------------
    /**
       Retrieves the source-code-control system revision number. <P>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

}
