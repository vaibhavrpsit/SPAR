/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadTransactionHistory.java /main/44 2014/05/23 16:50:51 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/23/14 - do not use descriptions that are deleted
 *    cgreen 05/16/14 - sort suspeded results by most expensive lineitem
 *    abhina 07/10/13 - Fix to display first item description for each
 *                      suspended txns
 *    rabhaw 06/18/13 - Updated searchCheckTransactions method to take the
 *                      correct micr column so that check tender transactions
 *                      can be retrieved.
 *    rgour  04/01/13 - CBR cleanup
 *    jswan  09/13/12 - Modified to support deprecation of JdbcPLUOperation.
 *    vtemke 09/10/12 - Fixed empty currency code issue (BugDB ID 14407856)
 *    cgreen 05/21/12 - XbranchMerge cgreene_bug-13951397 from
 *                      rgbustores_13.5x_generic
 *    cgreen 05/16/12 - arrange order of businessDay column to end of primary
 *                      key to improve performance since most receipt lookups
 *                      are done without the businessDay
 *    ohorne 01/26/12 - XbranchMerge ohorne_bug-13619784 from
 *                      rgbustores_13.4x_generic_branch
 *    ohorne 01/26/12 - Fix for Check tender search by masked MICR
 *    jswan  01/05/12 - Refactor the status change of suspended transaction to
 *                      occur in a transaction so that status change can be
 *                      sent to CO as part of DTM.
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    cgreen 09/15/11 - removed deprecated methods and changed static methods
 *                      to non-static
 *    cgreen 09/12/11 - revert aba number encryption, which is not sensitive
 *    tkshar 08/19/11 - Made column names consistent for Encryption CR
 *    ohorne 08/10/11 - masked aba and account number for check
 *    cgreen 06/29/11 - add token column and remove encrypted/hashed account
 *                      number column in credit-debit tender table.
 *    asinto 12/20/10 - XbranchMerge asinton_bug-10407292 from
 *                      rgbustores_13.3x_generic_branch
 *    asinto 12/17/10 - deprecated hashed account ID.
 *    acadar 10/27/10 - changes for resetting the order status in Siebel
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 05/11/10 - convert Base64 from axis
 *    cgreen 05/11/10 - updating deprecated names
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    dwfung 02/24/10 - Fixed training mode not able to retrieve suspended txns
 *    abonda 01/03/10 - update header date
 *    mchell 12/15/09 - Fixed the copyright header
 *    mchell 12/15/09 - Serialisation return without receipt changes
 *    sgu    12/03/09 - Change references to use the new/improved PLU apis
 *    cgreen 09/25/09 - XbranchMerge cgreene_bug-8931126 from
 *                      rgbustores_13.1x_branch
 *    cgreen 09/24/09 - refactor SQL statements up support preparedStatements
 *                      for updates and inserts to improve dept hist perf
 *    asinto 06/22/09 - Corrected the database type of the SC_TRN value in the
 *                      excludeOtherSQL query builder method.
 *    mchell 04/09/09 - Retrieving only completed transactions for return
 *    mchell 03/20/09 - Filtering cancelled transactions from customer history
 *    acadar 02/25/09 - use application default locale instead of jvm locale
 *    mahisi 02/25/09 - Fixed issue for suspended transaction
 *    mahisi 02/18/09 - fixed PDO return issue in case of no receipt
 *    cgreen 03/01/09 - upgrade to using prepared statements for PLU
 *    deghos 11/14/08 - Forward port bug# 7292467
 *    sgu    10/30/08 - check in after refresh
 *    sgu    10/30/08 - refactor layaway and transaction summary object to take
 *                      localized text
 *    mdecam 10/28/08 - I18N - Refactoring Transaction Suspend Reasons
 *    ddbake 10/24/08 - Updates due to merge
 *    ohorne 10/22/08 - I18N StoreInfo-related changes
 *    ddbake 10/22/08 - Updating to use localized item descriptoins
 *    ohorne 10/08/08 - deprecated methods per I18N Database Technical
 *                      Specification
 *
 * ===========================================================================

     $Log:
      17   360Commerce 1.16        4/3/2008 6:44:15 PM    Sharma Yanamandra CR
           30,756
      16   360Commerce 1.15        3/31/2008 7:07:09 PM   Alan N. Sinton  CR
           30913: Updated code to handle search transaction by gift card
           number as encrypted account number.  Changes reviewed by Anil
           Bondalapati.
      15   360Commerce 1.14        2/18/2008 8:39:33 AM   Charles D. Baker CR
           30333 - Added "Distinct" to query to avoid multiple records for the
            same transaction for multiple quantity items. This fix should
           prevent this problem from any similar circumstances that haven't
           yet been discovered. Code review by Sandy Gu.
      14   360Commerce 1.13        12/17/2007 6:21:32 PM  Alan N. Sinton  CR
           29598: Fixed some flow issues.
      13   360Commerce 1.12        12/12/2007 5:37:32 PM  Michael P. Barnett In
            checkCardNumberMatches(), ensure decrypted card number buffers are
            cleared after use.
      12   360Commerce 1.11        12/4/2007 8:18:27 PM   Alan N. Sinton  CR
           29598: code changes per code review.
      11   360Commerce 1.10        11/21/2007 2:01:51 AM  Deepti Sharma   CR
           29598: changes for credit/debit PABP
      10   360Commerce 1.9         11/12/2007 2:14:22 PM  Tony Zgarba
           Deprecated all existing encryption APIs and migrated the code to
           the new encryption API.
      9    360Commerce 1.8         5/3/2007 11:57:43 PM   Sandy Gu
           Enhance transaction persistence layer to store inclusive tax
      8    360Commerce 1.7         4/25/2007 10:01:11 AM  Anda D. Cadar   I18N
           merge
      7    360Commerce 1.6         7/19/2006 12:51:40 PM  Brendan W. Farrell
           Create wrapper around encryption manager and service so that this
           can be used in either store server environment.
      6    360Commerce 1.5         1/25/2006 4:11:19 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      5    360Commerce 1.4         1/22/2006 11:41:22 AM  Ron W. Haight
           Removed references to com.ibm.math.BigDecimal
      4    360Commerce 1.3         12/13/2005 4:43:44 PM  Barry A. Pape
           Base-lining of 7.1_LA
      3    360Commerce 1.2         3/31/2005 4:28:42 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:22:46 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:00 PM  Robert Pearse
     $:
      7    .v710     1.2.3.0     9/21/2005 13:39:47     Brendan W. Farrell
           Initial Check in merge 67.
      6    .v700     1.2.4.2     12/14/2005 13:01:55    Deepanshu       CR
           4896: Exclude PostVoid transactions from retreived transactions
      5    .v700     1.2.4.1     11/16/2005 16:27:56    Jason L. DeLeau 4215:
           Get rid of redundant ArtsDatabaseifc class
      4    .v700     1.2.4.0     11/11/2005 15:08:35    Rohit Sachdeva  4832:
           Using Domain Object Factory
      3    360Commerce1.2         3/31/2005 15:28:42     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:22:46     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:12:00     Robert Pearse
     $
     Revision 1.18  2004/08/09 19:22:17  jriggins
     @scr 6652 Added logic to also check POS item ID when retrieving items for return

     Revision 1.17  2004/07/30 22:41:55  jdeleau
     @scr 2392 Make sure transactions are sorted as they should be.

     Revision 1.16  2004/06/18 21:45:59  mweis
     @scr 5644 Lookup of return w/o receipt fails when date is part of transaction

     Revision 1.15  2004/06/17 14:33:21  mweis
     @scr 5644 NPE in JdbcReadTransactionHistory addDescriptions method

     Revision 1.14  2004/04/09 16:55:47  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.13  2004/03/26 16:38:19  aschenk
     @scr 4012 - made change to parsing function b/c suspended transactions were not being retrieved in training mode.

     Revision 1.12  2004/03/25 16:21:10  baa
     @scr 3461 return enhacements

     Revision 1.11  2004/03/18 15:53:58  baa
     @scr 3561  modify search by tender query to exclude transactions where the search item has not return qty available or is a return item itself

     Revision 1.10  2004/03/15 16:49:03  aarvesen
     @scr 3561 removed TO-DO tag

     Revision 1.9  2004/03/15 15:15:16  baa
     @scr 3561 refactoring/ cleaning item size

     Revision 1.8  2004/03/04 20:48:14  baa
     @scr 3561Returns add units sold

     Revision 1.7  2004/02/19 21:55:45  aarvesen
     @scr 3561 Combined join code into one method
     Added item size and ID criteria

     Revision 1.6  2004/02/17 22:31:36  aarvesen
     @scr 0
     Initial check in for the searching on trasnactions by item size

     Revision 1.5  2004/02/17 17:57:39  bwf
     @scr 0 Organize imports.

     Revision 1.4  2004/02/17 16:18:50  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:17  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:26  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.2   Dec 30 2003 14:54:56   baa
 * use correct column to retrieve gift card tenders
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 *
 *    Rev 1.1   Dec 29 2003 15:32:22   baa
 * return enhancements
 *
 *    Rev 1.0   Aug 29 2003 15:32:16   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Feb 21 2003 15:00:36   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.1   Aug 06 2002 17:44:20   baa
 * use sqlhelper to format the date for range search
 * Resolution for POS SCR-1756: Databse Error when trying to view customer history after PromptForCustomerInfo set to Phone #
 *
 *    Rev 1.4   Aug 06 2002 17:42:14   baa
 * use sqlhelper to format date
 * Resolution for POS SCR-1756: Databse Error when trying to view customer history after PromptForCustomerInfo set to Phone #
 *
 *    Rev 1.0   Jun 03 2002 16:38:32   msg
 * Initial revision.
 *
 *    Rev 1.3   15 May 2002 17:14:08   vxs
 * Removed concatenations from logging statements
 * Resolution for POS SCR-1632: Updates for Gap
 *
 *    Rev 1.2   02 Apr 2002 14:53:56   dfh
 * added order complete and order partial to list of trans to retrieve the 1st item description
 * Resolution for POS SCR-1567: Picked up Orders/ return of picked up orders missing discounts in EJ
 *
 *    Rev 1.1   Mar 18 2002 22:47:54   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:07:50   msg
 * Initial revision.
 *
 *    Rev 1.10   18 Jan 2002 17:45:46   vxs
 * In executeAndParse(), added summary.setTrainingMode()
 * Resolution for POS SCR-144: Training mode susp transactions are on the susp trans report
 *
 *    Rev 1.9   Dec 30 2001 21:38:26   dfh
 * added special order initiate to read description of first item
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.8   27 Dec 2001 12:43:06   vxs
 * Corrected logic within executeAndParse() and added comment on what line to add in order to not retrieve training mode transactions.
 * Resolution for POS SCR-462: Unable to retrieve a TM suspended trans using scan or manually entering data
 *
 *    Rev 1.7   Dec 23 2001 17:22:24   dfh
 * exclude special order initiate, cancel
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.6   10 Dec 2001 16:08:20   baa
 * retrieve suspended transactions
 * Resolution for POS SCR-99: Data on Customer Delete screen should be non-editable
 *
 *    Rev 1.5   06 Dec 2001 14:32:22   baa
 * exclude suspended transactions from customer history
 * Resolution for POS SCR-313: Nothing happens when selecting Enter on Cust History when Offline
 *
 *    Rev 1.4   05 Nov 2001 17:34:00   baa
 * Implement code review changes. Customer & Inquiry Options
 * Resolution for POS SCR-244: Code Review  changes
 *
 *    Rev 1.3   24 Oct 2001 17:12:20   baa
 * customer history
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.1   23 Oct 2001 17:03:24   baa
 * customer history feature
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.0   Sep 20 2001 16:00:10   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:16   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.domain.arts;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.sql.SQLSelectStatement.SortOrder;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleMapConstantsIfc;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.UnknownItemIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.transaction.ItemSummaryIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

/**
 * This operation reads all of the transactions for a given search criteria.
 * 
 * @version $Revision: /main/44 $
 */
public class JdbcReadTransactionHistory extends JdbcReadlocalizedDescription implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -7547850596601527706L;

    /** The logger to which log messages will be sent. */
    private static final Logger logger = Logger.getLogger(JdbcReadTransactionHistory.class);

    /** revision number supplied by source-code-control system. */
    public static final String revisionNumber = "$Revision: /main/44 $";

    /**
     * Class constructor.
     */
    public JdbcReadTransactionHistory()
    {
        super();
        setName("JdbcReadTransactionHistory");
    }

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction
     * @param dataConnection
     * @param action
     */
    @Override
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        Vector<TransactionSummaryIfc>  summaryVector = null;

        // grab arguments and call readTransactions()
        Object dataObject = action.getDataObject();
        if (dataObject instanceof String)
        {
            //this block is deprecated as of Release 13.1
            String customerID = (String) dataObject;
            summaryVector = readTransactionHistory(connection,
                                                   customerID,
                                                   new LocaleRequestor(LocaleMap.getLocale(LocaleMapConstantsIfc.DEFAULT)));
        }
        else if (dataObject instanceof SearchCriteriaIfc)
        {
            SearchCriteriaIfc searchCriteria = (SearchCriteriaIfc) dataObject;
            summaryVector = readTransactionHistory(connection, searchCriteria);
        }
        else
        {
            logger.error("JdbcReadTransactionHistory.execute: Invalid search object");
            throw new DataException("Invalid search object");
        }

        // copy result into an array
        TransactionSummaryIfc[] summary = new TransactionSummaryIfc[summaryVector.size()];
        summaryVector.copyInto(summary);

        // return array
        dataTransaction.setResult(summary);
    }

    /**
     * Reads all transactions for a given customer.
     *
     * @param dataConnection      a connection to the database
     * @param customerID customer identifier
     * @param localeRequestor requested Locales
     * @return Vector of transactions
     * @throws DataException thrown when an error occurs executing the
     * SQL against the DataConnection, or when processing the ResultSet
     */
    protected Vector<TransactionSummaryIfc> readTransactionHistory(JdbcDataConnection dataConnection,
                                            String customerID,
                                            LocaleRequestor localeRequestor)
        throws DataException
    {
        logger.debug("JdbcReadTransactionHistory.readTransactionHistory()");
        Vector<TransactionSummaryIfc>  transVector = new Vector<TransactionSummaryIfc> (2);
        Vector<Integer> postVoidTransVector = new Vector<Integer>(2);

        SQLSelectStatement sql = buildBaseSQL();
        SQLSelectStatement postVoidSql = buildPostVoidSQL();

        // add customer ID qualifier
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_CUSTOMER_ID +
                         " = '" + customerID + "'");

        // add ordering clauses
        addOrdering(sql);

        try
        {
            // build sub-select to exclude post voided transaction
            postVoidTransVector = executePostVoidSQL(dataConnection, postVoidSql);
            ARTSExcludePostVoidSQL.buildSQL(sql, postVoidTransVector);
            excludeOtherSQL(sql);
            transVector = executeAndParse(dataConnection, sql, localeRequestor);
        }
        catch (DataException de)
        {
            logger.warn( "" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "transaction table");
            throw new DataException(DataException.SQL_ERROR, "transaction table", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "transaction table", e);
        }

        if (transVector.isEmpty())
        {
            logger.warn( "No transactions found");
            throw new DataException(DataException.NO_DATA, "No transactions found");
        }
        
        if (logger.isInfoEnabled()) logger.info("Transactions found: " + transVector.size());

        logger.debug("JdbcReadTransactionHistory.readTransactionHistory()");

        return(transVector);
    }

    /**
     * Reads all transactions for a given search criteria.
     * 
     * @param dataConnection a connection to the database
     * @param criteria as SearchCriteria
     * @return Vector of transactions
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected Vector<TransactionSummaryIfc> readTransactionHistory(JdbcDataConnection dataConnection,
                                            SearchCriteriaIfc criteria)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcReadTransactionHistory.readTransactionHistory" + "(JdbcDataConnection dataConnection,SearchCriteriaIfc criteria)" + "");
        Vector<TransactionSummaryIfc> transVector = new Vector<TransactionSummaryIfc>(2);
        Vector<Integer> postVoidTransVector = new Vector<Integer>(2);

        SQLSelectStatement sql = buildBaseSQL();
        SQLSelectStatement postVoidSql = buildPostVoidSQL();

        CustomerIfc customer = criteria.getCustomer();
        String customerID = null;
        if (customer != null)
        {
            customerID = customer.getCustomerID();
        }
        else
        {
            customerID = criteria.getCustomerID();
        }
        EYSDate[] dateRange = criteria.getDateRange();
        String storeNumber = criteria.getStoreNumber();
        String trainingMode = criteria.getTrainingMode();
        int  maxMatches =   criteria.getMaximumMatches();
        String maskedAccountNumber = criteria.getMaskedAccountNumber();
        String accountNumberToken = criteria.getAccountNumberToken();
        String maskedGiftcardNumber = criteria.getMaskedGiftCardNumber();
        String maskedMICRNumber = criteria.getMaskedMICRNumber();

        String itemSizeCode = criteria.getItemSizeCode();
        String itemID = criteria.getItemID();
        String serialNumber = criteria.getItemSerialNumber();
         // Boolean flag indicating we should filter the results by
         // exact matches to decrypted card number.

        if (customerID != null)
        {
            // add customer ID qualifier
            sql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_CUSTOMER_ID +
                             " = '" + customerID + "'");
            postVoidSql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_CUSTOMER_ID +
                    " = '" + customerID + "'");
        }

        if (storeNumber != null)
        {
            // add store number qualifier
            sql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_RETAIL_STORE_ID +
                             " = '" + storeNumber + "'");
            postVoidSql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_RETAIL_STORE_ID +
                             " = '" + storeNumber + "'");
        }

        if (trainingMode != null)
        {
            // add store number qualifier
            sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_TRAINING_FLAG +
                             " ='" + trainingMode +"'");
        }
        if (dateRange != null)
        {
            if (dateRange[0] != null)
            {
                // add begin business date qualifier
                sql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE +
                                 " >= " + dateToSQLDateString(dateRange[0]));
                postVoidSql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE +
                        " >= " + dateToSQLDateString(dateRange[0]));
            }

            if (dateRange[1] != null)
            {
                // add end business date qualifier
                sql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE +
                                 " <= " + dateToSQLDateString(dateRange[1]));
                postVoidSql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE +
                                 " <= " + dateToSQLDateString(dateRange[1]));
            }
        }

        if (!Util.isEmpty(accountNumberToken))
        {
            searchCreditDebitTransactionsByToken(sql, accountNumberToken);
        }
        else if (!Util.isEmpty(maskedMICRNumber))
        {
            searchCheckTransactions(sql, maskedMICRNumber);
        }
        else if (!Util.isEmpty(maskedAccountNumber))
        {
            maskedAccountNumber = replaceMaskedCharacters(maskedAccountNumber);
            searchCreditDebitTransactions(sql, maskedAccountNumber);
        }
        else if (!Util.isEmpty(maskedGiftcardNumber))
        {
            maskedGiftcardNumber = replaceMaskedCharacters(maskedGiftcardNumber);
            searchGiftCardTransactions(sql, maskedGiftcardNumber);
        }

        searchForLineItems(sql, itemSizeCode, itemID, serialNumber);

        // add ordering clauses
        addOrdering(sql);

        try
        {
            // build sub-select to exclude post voided transaction
        	postVoidTransVector = executePostVoidSQL(dataConnection, postVoidSql);
            ARTSExcludePostVoidSQL.buildSQL(sql, postVoidTransVector);
            if (criteria.getExclusionMode())
            {
               excludeOtherSQL(sql);
            }

            transVector = executeAndParse(dataConnection, sql, criteria.getLocaleRequestor());
            ArrayList<Integer>arrOfSpOrderInitiateTransNo= getSpecialOrderInitiateTransNo(transVector,dataConnection);
            if (arrOfSpOrderInitiateTransNo.size() > 0)
            {
                for (int i = 0; i < transVector.size(); i++)
                {
                    int transactionNO = (int)transVector.get(i).getTransactionID().getSequenceNumber();
                    if (arrOfSpOrderInitiateTransNo.contains(new Integer(transactionNO)))
                    {
                        transVector.remove(i);
                    }
                }
            }
        }
        catch (DataException de)
        {
            logger.warn( "" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "transaction table");
            throw new DataException(DataException.SQL_ERROR, "transaction table", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "transaction table", e);
        }

        if (transVector.isEmpty())
        {
            logger.warn( "No transactions found");
            throw new DataException(DataException.NO_DATA, "No transactions found");
        }

        if (logger.isInfoEnabled()) logger.info("Transactions found:  " + transVector.size());

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcReadTransactionHistory.readTransactionHistory" + "(JdbcDataConnection dataConnection,SearchCriteriaIfc criteria)" + "");

        if ((maxMatches > 0) && (transVector.size() > maxMatches))
        {
            throw new DataException(DataException.RESULT_SET_SIZE, "Too many matches");
        }
        return(transVector);
    }

    /**
     * Setup sql for searching transactions tender with a check by masked
     * MICR Number.
     *
     * @param sql
     * @param maskedMICRNumber the masked MICR number
     */
    protected void searchCheckTransactions(SQLSelectStatement sql, String maskedMICRNumber)
    {
        // set up qualifier for check tender table
        sql.addQualifier(ARTSDatabaseIfc.ALIAS_CHECK_TENDER_LINE_ITEM,
                         ARTSDatabaseIfc.FIELD_TENDER_MASKED_CHECK_MICR_NUMBER,
                         inQuotes(maskedMICRNumber));

        joinToTransaction(sql,
                ARTSDatabaseIfc.TABLE_CHECK_TENDER_LINE_ITEM,
                ARTSDatabaseIfc.ALIAS_CHECK_TENDER_LINE_ITEM);
    }

    /**
     * Convenience method to join the composite key of transaction to another
     * table
     * 
     * @param sql SQL object to use
     * @param tableToJoinTo Table you want to joing to (e.g.
     *            ARTSDatabaseIfc.TABLE_CHECK_TENDER_LINE_ITEM)
     * @param aliasToUse Table alias to use (e.g.
     *            ARTSDatabaseIfc.ALIAS_CHECK_TENDER_LINE_ITEM)
     */
    protected void joinToTransaction(SQLSelectStatement sql, String tableToJoinTo, String aliasToUse)
    {

        sql.addTable(tableToJoinTo, aliasToUse);

        sql.addJoinQualifier(ARTSDatabaseIfc.ALIAS_TRANSACTION,
                ARTSDatabaseIfc.FIELD_RETAIL_STORE_ID,
                aliasToUse,
                ARTSDatabaseIfc.FIELD_RETAIL_STORE_ID);
        sql.addJoinQualifier(ARTSDatabaseIfc.ALIAS_TRANSACTION,
                ARTSDatabaseIfc.FIELD_WORKSTATION_ID,
                aliasToUse,
                ARTSDatabaseIfc.FIELD_WORKSTATION_ID);
        sql.addJoinQualifier(ARTSDatabaseIfc.ALIAS_TRANSACTION,
                ARTSDatabaseIfc.FIELD_TRANSACTION_SEQUENCE_NUMBER,
                aliasToUse,
                ARTSDatabaseIfc.FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addJoinQualifier(ARTSDatabaseIfc.ALIAS_TRANSACTION,
                ARTSDatabaseIfc.FIELD_BUSINESS_DAY_DATE,
                aliasToUse,
                ARTSDatabaseIfc.FIELD_BUSINESS_DAY_DATE);
    }

    /**
     * Setup sql for searching transactions tender with a giftcard
     * 
     * @param sql
     * @param maskedAccountNumber
     */
    protected void searchGiftCardTransactions(SQLSelectStatement sql, String maskedAccountNumber)
     {
         // set up qualifier for credit tender table
         sql.addQualifier(ALIAS_GIFT_CERTIFICATE_TENDER_LINE_ITEM + "." + 
                 FIELD_MASKED_GIFT_CARD_SERIAL_NUMBER + " LIKE (" +  inQuotes(maskedAccountNumber) + ")");

         //sql.addQualifier(ARTSDatabaseIfc.ALIAS_GIFT_CERTIFICATE_TENDER_LINE_ITEM,
         //                 ARTSDatabaseIfc.FIELD_MASKED_GIFT_CARD_SERIAL_NUMBER,
         //                 inQuotes(maskedAccountNumber));


         joinToTransaction(sql,
              ARTSDatabaseIfc.TABLE_GIFT_CARD_TENDER_LINE_ITEM,
              ARTSDatabaseIfc.ALIAS_GIFT_CERTIFICATE_TENDER_LINE_ITEM);

    }

    /**
     * This method replaces the non numeric (masked characters) with a single
     * '%' character;
     */
    private String replaceMaskedCharacters(String number)
    {
        StringBuilder builder = new StringBuilder(number.length());
        int len = number.length();
        boolean digitFound = true;
        for(int i = 0; i < len; i++)
        {
            char ch = number.charAt(i);
            if (!Character.isDigit(ch) && digitFound)
            {
                builder.append('%');
                digitFound = false;
            }
            else if (Character.isDigit(ch))
            {
                builder.append(ch);
                digitFound = true;
            }
        }
        
        return builder.toString();
    }

    /**
     * if you have itemSizes or item ID's, then join in and search on sales
     * returns line items
     * 
     * @param sql
     * @param item size code
     * @param item id
     */
    protected void searchForLineItems(SQLSelectStatement sql, String itemSizeCode, String itemID, String serialNumber)
    {

        boolean addJoin = false;

        if (!Util.isEmpty(itemSizeCode )) {
            addJoin = true;
            sql.addQualifier(ARTSDatabaseIfc.ALIAS_SALE_RETURN_LINE_ITEM,
                    ARTSDatabaseIfc.FIELD_SIZE_CODE,
                    makeSafeString(itemSizeCode));
        }

        // Match either on the Item ID or the POS Item ID
        if (itemID != null) {
            addJoin = true;
            sql.addQualifier(
                "(" + ARTSDatabaseIfc.ALIAS_SALE_RETURN_LINE_ITEM + "." + ARTSDatabaseIfc.FIELD_ITEM_ID + "=" +
                makeSafeString(itemID) + " OR " +
                ARTSDatabaseIfc.ALIAS_SALE_RETURN_LINE_ITEM + "." + ARTSDatabaseIfc.FIELD_POS_ITEM_ID + "=" +
                makeSafeString(itemID) + ")");

            // and qualifier to indicate return qty available is > 0
            sql.addQualifier(ARTSDatabaseIfc.ALIAS_SALE_RETURN_LINE_ITEM +"."+
                    ARTSDatabaseIfc.FIELD_SALE_RETURN_LINE_ITEM_QUANTITY +" - " +
                    ARTSDatabaseIfc.ALIAS_SALE_RETURN_LINE_ITEM +"."+
                    ARTSDatabaseIfc.FIELD_SALE_RETURN_LINE_ITEM_RETURN_QUANTITY + " > 0" );

            // add qualifier to exclude return item
            sql.addQualifier(ARTSDatabaseIfc.ALIAS_SALE_RETURN_LINE_ITEM + "." +
                    ARTSDatabaseIfc.FIELD_MERCHANDISE_RETURN_FLAG +
                    " = '0'" );

            // add qualifier to exclude deleted item
            sql.addQualifier(ARTSDatabaseIfc.ALIAS_SALE_RETURN_LINE_ITEM + "." +
                    ARTSDatabaseIfc.FIELD_SALE_RETURN_LINE_ITEM_VOID_FLAG +
                    " = '0'" );

        }

        if (!Util.isEmpty(serialNumber)) {
            addJoin = true;
            sql.addQualifier(ARTSDatabaseIfc.ALIAS_SALE_RETURN_LINE_ITEM,
                    ARTSDatabaseIfc.FIELD_SERIAL_NUMBER,
                    makeSafeString(serialNumber));
        }

        if (addJoin) {
            joinToTransaction(sql,
                ARTSDatabaseIfc.TABLE_SALE_RETURN_LINE_ITEM,
                ARTSDatabaseIfc.ALIAS_SALE_RETURN_LINE_ITEM);
        }
    }

    /**
     * Setup sql for searching transactions tender with a credit or debit card
     * by the card number token provided by the payment service
     * 
     * @param sql
     * @param cardNumberToken
     */
     protected void searchCreditDebitTransactionsByToken(SQLSelectStatement sql, String cardNumberToken)
     {
         // set up qualifier for credit tender table
         sql.addQualifier(ARTSDatabaseIfc.ALIAS_CREDIT_DEBIT_CARD_TENDER_LINE_ITEM,
                          ARTSDatabaseIfc.FIELD_TENDER_AUTHORIZATION_ACCOUNT_NUMBER_TOKEN,
                          inQuotes(cardNumberToken));

         joinToTransaction(sql,
                          ARTSDatabaseIfc.TABLE_CREDIT_DEBIT_CARD_TENDER_LINE_ITEM,
                          ARTSDatabaseIfc.ALIAS_CREDIT_DEBIT_CARD_TENDER_LINE_ITEM);

    }

    /**
     * Setup sql for searching transactions tender with a credit or debit card
     * 
     * @param sql
     * @param accountNumber
     */
    protected void searchCreditDebitTransactions(SQLSelectStatement sql, String cardNumber)
    {
        // set up qualifier for credit tender table
        sql.addQualifier(ALIAS_CREDIT_DEBIT_CARD_TENDER_LINE_ITEM + "." + 
                FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_CARD_ACCOUNT_MASKED + " LIKE (" +  inQuotes(cardNumber) + ")");
        //sql.addQualifier(ARTSDatabaseIfc.ALIAS_CREDIT_DEBIT_CARD_TENDER_LINE_ITEM,
        //                 ARTSDatabaseIfc.FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_CARD_ACCOUNT_MASKED,
        //                 inQuotes(cardNumber));

        joinToTransaction(sql,
                         ARTSDatabaseIfc.TABLE_CREDIT_DEBIT_CARD_TENDER_LINE_ITEM,
                         ARTSDatabaseIfc.ALIAS_CREDIT_DEBIT_CARD_TENDER_LINE_ITEM);

    }

    /**
     * Builds SQL select statement for transaction history. This method does not
     * completely build the predicate, leaving that for the calling method. This
     * method builds the joins necessary for the transaction summary.
     * 
     * @return SQL statement for select
     */
    protected SQLSelectStatement buildBaseSQL()
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        sql.setDistinctFlag(true);

        // add tables
        sql.addTable(TABLE_TRANSACTION, ALIAS_TRANSACTION);
        sql.addTable(TABLE_RETAIL_TRANSACTION, ALIAS_RETAIL_TRANSACTION);
        sql.addTable(TABLE_TAX_LINE_ITEM, ALIAS_TAX_LINE_ITEM);

        // add columns
        sql.addColumn(FIELD_TRANSACTION_TRAINING_FLAG);
        sql.addColumn(FIELD_TRANSACTION_TYPE_CODE);
        sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_WORKSTATION_ID);
        sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_RETAIL_STORE_ID);
        sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE);
        sql.addColumn(FIELD_TRANSACTION_STATUS_CODE);
        sql.addColumn(FIELD_TRANSACTION_NET_TOTAL);
        sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_TENDER_REPOSITORY_ID);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_CUSTOMER_ID);
        sql.addColumn(ALIAS_TAX_LINE_ITEM + "." + FIELD_TAX_AMOUNT);
        sql.addColumn(ALIAS_TAX_LINE_ITEM + "." + FIELD_TAX_INC_AMOUNT);
        sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_END_DATE_TIMESTAMP);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_SUSPENDED_TRANSACTION_REASON_CODE);
        sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_OPERATOR_ID);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_EXTERNAL_ORDER_ID);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_ORDER_ID);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_LAYAWAY_ID);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_CURRENCY_ISO_CODE);

        // add qualifiers
        // join transaction and retail transaction through business date, transaction ID fields
        sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE
                         + " = " + ALIAS_RETAIL_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE);
        sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER
                         + " = " + ALIAS_RETAIL_TRANSACTION + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_WORKSTATION_ID
                         + " = " + ALIAS_RETAIL_TRANSACTION + "." + FIELD_WORKSTATION_ID);
        sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_RETAIL_STORE_ID
                         + " = " + ALIAS_RETAIL_TRANSACTION + "." + FIELD_RETAIL_STORE_ID);
        // join transaction and tax line item through business date, transaction ID fields
        sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE
                         + " = " + ALIAS_TAX_LINE_ITEM + "." + FIELD_BUSINESS_DAY_DATE);
        sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER
                         + " = " + ALIAS_TAX_LINE_ITEM + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_WORKSTATION_ID
                         + " = " + ALIAS_TAX_LINE_ITEM + "." + FIELD_WORKSTATION_ID);
        sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_RETAIL_STORE_ID
                         + " = " + ALIAS_TAX_LINE_ITEM + "." + FIELD_RETAIL_STORE_ID);
       // sql.addQualifier(FIELD_TRANSACTION_STATUS_CODE + " = '" + TransactionIfc.STATUS_COMPLETED + "'");

        return(sql);
    }

    /**
     * Adds clauses to specified SQL statement for ordering of the results.
     * 
     * @param sql SQLSelectStatement object
     */
    protected void addOrdering(SQLSelectStatement sql)
    {
        // add ordering by business date, store, workstation, sequence number
        sql.addOrdering(ALIAS_TRANSACTION, FIELD_BUSINESS_DAY_DATE, SortOrder.DESC);
        sql.addOrdering(ALIAS_TRANSACTION, FIELD_RETAIL_STORE_ID);
        sql.addOrdering(ALIAS_TRANSACTION, FIELD_WORKSTATION_ID);
        sql.addOrdering(ALIAS_TRANSACTION, FIELD_TRANSACTION_SEQUENCE_NUMBER);
    }

    /**
     * Executes SQL and parses results.
     * 
     * @param connection JDBC data connection
     * @param sql SQLSelectStatement object
     * @return vector of transactions
     * @throws DataException DataException thrown if error occurs
     */
    protected Vector<TransactionSummaryIfc> executeAndParse(JdbcDataConnection dataConnection,
                                                            SQLSelectStatement sql,
                                                            LocaleRequestor localeRequestor)
        throws DataException
    {

        Vector<TransactionSummaryIfc> transVector = new Vector<TransactionSummaryIfc>();

        ResultSet rs = null;
        try
        {
            dataConnection.execute(sql.getSQLString());

            rs = (ResultSet) dataConnection.getResult();

            int trainingFlag = 0;
            int transType = 0;
            int sequenceNumber = 0;
            String workstationID = null;
            String storeID = null;
            EYSDate businessDate = null;
            int statusCode = 0;
            CurrencyIfc total = null;
            String tillID = null;
            String customerID = null;
            CurrencyIfc taxTotal = null;
            CurrencyIfc inclusiveTaxTotal = null;
            EYSDate transactionTimestamp = null;
            int suspendReasonCode = CodeConstantsIfc.CODE_INTEGER_UNDEFINED;
            String cashierID = null;
            String externalOrderId = null;
            String orderID = null;
            String layawayID = null;

            while (rs.next())
            {
                /*
                 * Grab the fields selected from the database
                 */
                int index = 0;
                trainingFlag = rs.getInt(++index);
                transType = rs.getInt(++index);
                sequenceNumber = rs.getInt(++index);
                workstationID = getSafeString(rs, ++index);
                storeID = getSafeString(rs, ++index);
                businessDate = getEYSDateFromString(rs, ++index);
                statusCode = rs.getInt(++index);
                total = getCurrencyFromDecimal(rs, ++index);
                tillID = getSafeString(rs, ++index);
                customerID = getSafeString(rs, ++index);
                taxTotal = getCurrencyFromDecimal(rs, ++index);
                inclusiveTaxTotal = getCurrencyFromDecimal(rs, ++index);
                transactionTimestamp = timestampToEYSDate(rs, ++index);
                suspendReasonCode = getReasonCode(rs, ++index);
                cashierID = getSafeString(rs, ++index);
                externalOrderId = getSafeString(rs, ++index);
                orderID = getSafeString(rs, ++index);
                layawayID = getSafeString(rs, ++index);

                // Instantiate Store
                StoreIfc store = instantiateStore();
                store.setStoreID(storeID);

                // Instantiate Transaction Summary and load values
                TransactionSummaryIfc summary = createTransactionSummary();
                TransactionIDIfc tid = DomainGateway.getFactory().getTransactionIDInstance();
                tid.setTransactionID(storeID, workstationID, sequenceNumber);
                tid.setBusinessDate(businessDate);
                summary.setTransactionID(tid);
                summary.setBusinessDate(businessDate);
                summary.setTrainingMode((trainingFlag==0)?false:true);
                summary.setTransactionType(transType);
                summary.setTransactionStatus(statusCode);
                summary.setStore(store);
                summary.setTransactionGrandTotal(total);
                summary.setRegisterID(workstationID);
                summary.setTillID(tillID);
                summary.setCustomerID(customerID);
                summary.setTransactionTaxTotal(taxTotal);
                summary.setTransactionInclusiveTaxTotal(inclusiveTaxTotal);
                summary.setTransactionTimestamp(transactionTimestamp);
                summary.getSuspendReason().setCode(Integer.toString(suspendReasonCode));
                summary.setCashierID(cashierID);
                summary.setExternalOrderID(externalOrderId);
                summary.setInternalOrderID(orderID);
                summary.setLayawayID(layawayID);
            
               
                //The search criteria should pick txns from training or production modes
                //Do not filter result here.
                transVector.addElement(summary);
            }

            fillAdditionalAttributes(dataConnection, transVector, localeRequestor);
        }
        catch (DataException de)
        {
            logger.warn( "" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            logger.error( "" + Util.throwableToString(se) + "");
            dataConnection.logSQLException(se, "transaction table");
            throw new DataException(DataException.SQL_ERROR, "transaction table", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "transaction table", e);
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
                    throw new DataException(DataException.SQL_ERROR, "error closing ResultSet", se);
                }
            }
        }

        return(transVector);
    }

    /**
     * Fills additional attributes in transaction history.
     * 
     * @param dataConnection JDBC data connection
     * @param transVector Vector of transaction summaries
     * @param localeRequestor the requested locales
     * @throws DataException if error occurs
     */
    protected void fillAdditionalAttributes(JdbcDataConnection dataConnection,
                                            Vector<TransactionSummaryIfc> transVector,
                                            LocaleRequestor localeRequestor)
        throws DataException
    {
        for (TransactionSummaryIfc summary : transVector)
        {
            // fill additional fields as needed
            readDescription(dataConnection, summary, localeRequestor);
            readStoreLocation(dataConnection, summary, localeRequestor);
            readSuspendReason(dataConnection, summary, localeRequestor);
        }
    }

    /**
     * Instantiates an object implementing the TransactionSummaryIfc interface.
     * 
     * @return object implementing TransactionSummaryIfc
     */
    protected TransactionSummaryIfc createTransactionSummary()
    {
        return (DomainGateway.getFactory().getTransactionSummaryInstance());
    }

    /**
     * Gets the first item summary description for the summarized transaction.
     * 
     * @param dataConnection a connection to the database
     * @param summary the base summary
     * @param sqlLocale Locale used for SQL
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected TransactionSummaryIfc readDescription(JdbcDataConnection dataConnection,
                                                    TransactionSummaryIfc summary,
                                                    LocaleRequestor localeRequestor)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcReadTransactionHistory.readDescription()");
        switch(summary.getTransactionType())
        {
            case TransactionIfc.TYPE_SALE:
            case TransactionIfc.TYPE_RETURN:
            case TransactionIfc.TYPE_LAYAWAY_INITIATE:
            case TransactionIfc.TYPE_LAYAWAY_COMPLETE:
            case TransactionIfc.TYPE_ORDER_INITIATE:
            case TransactionIfc.TYPE_ORDER_COMPLETE:
            case TransactionIfc.TYPE_ORDER_PARTIAL:
                readItemSummaries(dataConnection,
                                         summary,
                                         localeRequestor);
                break;
            case TransactionIfc.TYPE_VOID:
                break;
            default:
                logger.error(
                            "JdbcReadTransaction: Unsupported transaction type " + Integer.toString(summary.getTransactionType()) + "");
                break;
        }

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcReadTransactionHistory.readDescription()");

        return(summary);
    }

    /**
     * Retrieves Item summaries for the items within a transaction
     * 
     * @param dataConnection a connection to the database
     * @param sqlLocale Locale used for SQL
     * @return description of first line item
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected LocalizedTextIfc readItemSummaries(JdbcDataConnection dataConnection,
                                              TransactionSummaryIfc summary,
                                              LocaleRequestor localeRequestor)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug("JdbcReadTransaction.readItemSummaries()");

        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables
        sql.addTable(TABLE_SALE_RETURN_LINE_ITEM);

        // add columns
        sql.addColumn(FIELD_POS_ITEM_ID);
        sql.addColumn(FIELD_ITEM_ID);
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_QUANTITY);
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_RECEIPT_DESCRIPTION);
        // add qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID, getStoreID(summary));
        sql.addQualifier(FIELD_WORKSTATION_ID, getWorkstationID(summary));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER, getSequenceNumber(summary));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE, getBusinessDate(summary));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_VOID_FLAG, "'0'");

        // add ordering
        sql.addOrdering(FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_AMOUNT, SortOrder.DESC);

        try
        {
            dataConnection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet)dataConnection.getResult();

            String itemID = null;
            String posItemID = null;
            int lineItemSequenceNumber = -1;
            BigDecimal unitsSold;
            String pDescription=null;


            // loop through the items
            while  (rs.next())
            {
                int index = 0;
                posItemID = getSafeString(rs, ++index);
                itemID = getSafeString(rs, ++index);
                lineItemSequenceNumber = rs.getInt(++index);
                unitsSold = getBigDecimal(rs, ++index);
                pDescription=getSafeString(rs, ++index);
                if (summary == null)
                {
                    summary = DomainGateway.getFactory().getTransactionSummaryInstance();
                }

                summary.addItemSummary(itemID, posItemID, lineItemSequenceNumber, unitsSold, pDescription);
            }
            rs.close();

            // add description to summary
            addDescriptions(dataConnection, summary, localeRequestor);

        }
        catch (SQLException exc)
        {
            dataConnection.logSQLException(exc, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR,
                                    "error processing sale return line items",
                                    exc);
        }

        if (logger.isDebugEnabled()) logger.debug("JdbcReadTransaction.readItemSummaries");
        return(summary.getLocalizedDescriptions());
    }

    /**
     * Adds descriptions to summary.
     * 
     * @param dataConnection database connection
     * @param summary transaction summary
     * @param sqlLocale locale
     */
    protected void addDescriptions(JdbcDataConnection dataConnection,
                                   TransactionSummaryIfc summary,
                                   LocaleRequestor localeRequestor)
    {
        ItemSummaryIfc[] items = null;

        // add description to first item
        items = summary.getItemSummaries();
        if (items != null && items.length > 0)
        {
            LocalizedTextIfc itemDescriptions = getDescription(dataConnection,
                    items[0].getItemID(),
                    summary,
                    items[0].getLineItemSequenceNumber(),
                    localeRequestor);
            if(!StringUtils.isEmpty(itemDescriptions.getText()))
            {
                summary.setLocalizedDescriptions(itemDescriptions);
            }
            else
            {
                summary.setLocalizedDescriptions(items[0].getLocalizedDescription()); 
            }
        }
    }

    /**
     * Retrieves description for a specified item and summary.
     * 
     * @param dataConnection database connection
     * @param itemID item identifier
     * @param summary transaction summary
     * @param sequenceNumber line item sequence number
     * @param sqlLocale locale
     * @return description for item
     */
    protected LocalizedTextIfc getDescription(JdbcDataConnection dataConnection,
                                    String itemID,
                                    TransactionSummaryIfc summary,
                                    int sequenceNumber,
                                    LocaleRequestor localeRequestor)
    {
        PLUItemIfc pluItem = null;
        LocalizedTextIfc descriptions = DomainGateway.getFactory().getLocalizedText();
        try
        {
            pluItem = DomainGateway.getFactory().getPLUItemInstance();
            pluItem.setItemID(itemID);
            readLocalizedItemDescriptions(dataConnection, pluItem, localeRequestor);
        }
        catch (DataException de)
        {
            try
            {
                //If there's no PLUItem, then check UnknownItem
                // We're only interested in finding a single description as a descriptor for the transaction summary
                pluItem = selectUnknownItem(dataConnection, summary, sequenceNumber, localeRequestor);
            }
            catch (DataException e)
            {
                // do nothing
            }
        }

        if (pluItem != null)
        {
            descriptions = pluItem.getLocalizedDescriptions();
        }

        return(descriptions);
    }

    /**
     * Gets the store location for the summarized transaction.
     * 
     * @param dataConnection a connection to the database
     * @param summary the base summary
     * @param localeRequestor requested locales
     * @return
     * @throws DataException thrown when an error occurs executing the SQL
     *             against the DataConnection, or when processing the ResultSet
     */
    protected TransactionSummaryIfc readStoreLocation(JdbcDataConnection dataConnection,
                                                      TransactionSummaryIfc summary,
                                                      LocaleRequestor localeRequestor)
        throws DataException
    {
        logger.debug( "JdbcReadTransactionHistory.readStoreLocation()");

        // get sql for store read
        String storeID = summary.getStore().getStoreID();
        SQLSelectStatement sql = ReadStoreSQL.getStoreLocationSQL(storeID, localeRequestor);

        ResultSet rs = null;
        try
        {
            dataConnection.execute(sql.getSQLString());

            rs = (ResultSet)dataConnection.getResult();

            // we're only interested in the first one
            if (rs.next())
            {
                ReadStoreSQL.parseStoreLocationResults(rs, summary.getStore(), 1, 2, localeRequestor.getDefaultLocale());
            }
        }
        catch (SQLException exc)
        {
            dataConnection.logSQLException(exc, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR, "error reading store data", exc);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readStoreLocation", e);
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
                    throw new DataException(DataException.SQL_ERROR, "error closing ResultSet", se);
                }
            }
        }

        logger.debug( "JdbcReadTransactionHistory.readStoreLocation()");

        return(summary);
    }

    protected void excludeOtherSQL(SQLSelectStatement sql) throws SQLException
    {
        /*
         * exclude transactions of these types:
         * TYPE_LAYAWAY_INITIATE, TYPE_LAYAWAY_DELETE, TYPE_ORDER_CANCEL, and TYPE_VOID
         */
        sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_TYPE_CODE +
                         " not in (" + inQuotes(TransactionIfc.TYPE_LAYAWAY_INITIATE) + ", " +
                                       inQuotes(TransactionIfc.TYPE_LAYAWAY_DELETE)   + ", " +
                                       inQuotes(TransactionIfc.TYPE_ORDER_CANCEL)     + ", " +
                                       inQuotes(TransactionIfc.TYPE_VOID)             + ")");
        // include all transactions where STATUS_COMPLETED
        sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_STATUS_CODE +
                         " = " + TransactionIfc.STATUS_COMPLETED);
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
     **/
    public UnknownItemIfc selectUnknownItem(JdbcDataConnection dataConnection,
                                            TransactionSummaryIfc summary,
                                            int lineItemSequenceNumber,
                                            LocaleRequestor localeRequestor)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadTransactionHistory.selectUnknownItem()");

        /*
         * build a skeleton transaction and lineItem,
         * then call the real selectUnknownItem()
         */
        TransactionIfc transaction = DomainGateway.getFactory().getTransactionInstance();
        transaction.initialize(summary.getTransactionID().getTransactionIDString());
        transaction.setBusinessDay(summary.getBusinessDate());
        AbstractTransactionLineItemIfc lineItem = DomainGateway.getFactory().getSaleReturnLineItemInstance();
        lineItem.setLineNumber(lineItemSequenceNumber);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadTransactionHistory.selectUnknownItem()");
        return(new JdbcReadTransaction().selectUnknownItem(dataConnection, transaction, lineItem, localeRequestor));
    }

    /**
     * Instantiates a Store object.
     * 
     * @return new Store object
     */
    protected StoreIfc instantiateStore()
    {
        return (DomainGateway.getFactory().getStoreInstance());
    }

    /**
     * Returns the formatted store identifier from the summary.
     * 
     * @param summary TransactionSummaryIfc object
     * @return formatted store identifier
     */
    protected String getStoreID(TransactionSummaryIfc summary)
    {
        return ("'" + summary.getStore().getStoreID() + "'");
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
     * Retrieves reason code from result set. If result-set string is empty or
     * null, reason code of default value
     * CodeConstantsIfc.CODE_INTEGER_UNDEFINED is set.
     * 
     * @param rs ResultSet read from database
     * @param index index into result set
     * @return reasonCode reason code
     * @exception SQLException thrown if can't parse from result set
     */
    protected int getReasonCode(ResultSet rs,
                                int index)
        throws SQLException
    {
        String reasonCodeString = rs.getString(index);
        // if string is empty, set as undefined
        int reasonCode = CodeConstantsIfc.CODE_INTEGER_UNDEFINED;
        try
        {
            if (!(Util.isEmpty(reasonCodeString)))
            {
                reasonCode = Integer.parseInt(reasonCodeString);
            }
        }
        // if not parseable, leave as undefined
        catch (NumberFormatException e)
        {
        }

        return(reasonCode);

    }

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * Builds SQL select statement for post voided transaction . This method
     * does not completely build the predicate, leaving that for the calling
     * method. This method builds the joins necessary for the post voided
     * transaction.
     * 
     * @return SQL statement for select
     */
    protected SQLSelectStatement buildPostVoidSQL()
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        // add tables
        sql.addTable(TABLE_POST_VOID_TRANSACTION, ALIAS_POST_VOID_TRANSACTION);
        sql.addTable(TABLE_RETAIL_TRANSACTION, ALIAS_RETAIL_TRANSACTION);
        // add columns
        sql.addColumn(FIELD_VOIDED);
        sql.setDistinctFlag(true);
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_RETAIL_STORE_ID
                            + " = " + ALIAS_POST_VOID_TRANSACTION + "." + FIELD_RETAIL_STORE_ID);
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_WORKSTATION_ID
                + " = " + ALIAS_POST_VOID_TRANSACTION + "." + FIELD_WORKSTATION_ID);
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER +
                        " = " + ALIAS_POST_VOID_TRANSACTION + "." + FIELD_VOIDED);
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE
                + " = " + ALIAS_POST_VOID_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE);
        return(sql);
    }

    /**
     * Executes SQL and parses results.
     * 
     * @param connection JDBC data connection
     * @param sql SQLSelectStatement object
     * @return vector of transactions
     * @exception DataException thrown if error occurs
     */
    protected Vector<Integer> executePostVoidSQL(JdbcDataConnection dataConnection,
                                     SQLSelectStatement sql)
        throws DataException
    {
        Vector<Integer> postVoidTransVector = new Vector<Integer>();
        try
        {
            // execute transaction
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet) dataConnection.getResult();
            int sequenceNumber = 0;
            while (rs.next())
            {
                /*
                 * Grab the fields selected from the database
                 */
                int index = 0;
                sequenceNumber = rs.getInt(++index);
                postVoidTransVector.addElement(new Integer(sequenceNumber));
            }
            rs.close();
        }
        catch (DataException de)
        {
            logger.warn( "" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            logger.error( "" + Util.throwableToString(se) + "");
            dataConnection.logSQLException(se, "transaction table");
            throw new DataException(DataException.SQL_ERROR, "transaction table", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "transaction table", e);
        }
        return(postVoidTransVector);
    }

    /**
     * Read localized Suspend Transaction Reason Codes
     * @param dataConnection
     * @param summary
     * @param localeRequestor
     */
    protected void readSuspendReason(JdbcDataConnection dataConnection, TransactionSummaryIfc summary, LocaleRequestor localeRequestor)
    {
        String suspendReasonCode = summary.getSuspendReason().getCode();

        if(!suspendReasonCode.equals(CodeConstantsIfc.CODE_UNDEFINED))
        {
            // Read Localized Reason Code
            CodeSearchCriteriaIfc criteria = DomainGateway.getFactory().getCodeSearchCriteriaInstance();
            criteria.setStoreID(summary.getStore().getStoreID());
            criteria.setListID(CodeConstantsIfc.CODE_LIST_TRANSACTION_SUSPEND_REASON_CODES);
            criteria.setLocaleRequestor(localeRequestor);
            criteria.setCode(suspendReasonCode);

            JdbcReadCodeList readCodeList = new JdbcReadCodeList();
            try
            {
                summary.setSuspendReason(readCodeList.readCode(dataConnection, criteria));
            }
            catch (DataException e)
            {
                // Localized Reason Not found. Exception already logged.
            }
        }
    }

    /**
     * Check wheather the summary contain SpecialOrder initiate Order transaction
     * @param dataConnection
     * @param summary
     */
    protected ArrayList<Integer> getSpecialOrderInitiateTransNo(Vector<TransactionSummaryIfc> transVector,
            JdbcDataConnection dataConnection)
    {
        Iterator<TransactionSummaryIfc> iteratorTrans = transVector.iterator();
        ArrayList<Integer> arrOfSpOrderInitiateTransNo = new ArrayList<Integer>();
        while (iteratorTrans.hasNext())
        {
            TransactionSummaryIfc summary = iteratorTrans.next();
            SQLSelectStatement sql = new SQLSelectStatement();
            ResultSet rs = null;
            boolean isOrderInitiateTrans = false;
            if (summary.getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE)
            {
                isOrderInitiateTrans = true;
                int transactionNo = (int)summary.getTransactionID().getSequenceNumber();
                sql.setDistinctFlag(true);
                sql.addTable(TABLE_ORDER);
                sql.addColumn(TABLE_ORDER + "." + FIELD_ORDER_ORIGINAL_TRANSACTION_SEQUENCE_NUMBER);
                sql.addQualifier(TABLE_ORDER + "." + FIELD_ORDER_ORIGINAL_TRANSACTION_SEQUENCE_NUMBER, transactionNo);
                sql.addQualifier(TABLE_ORDER + "." + FIELD_ORDER_TYPE, OrderConstantsIfc.ORDER_TYPE_SPECIAL);

            }
            try
            {
                if (isOrderInitiateTrans)
                {
                    dataConnection.execute(sql.getSQLString());
                    rs = (ResultSet)dataConnection.getResult();

                    while (rs.next())
                    {
                        int index = 0;
                        arrOfSpOrderInitiateTransNo.add(new Integer(rs.getInt(++index)));
                    }
                }

            }
            catch (DataException e)
            {
                e.printStackTrace();
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }

        }
        return arrOfSpOrderInitiateTransNo;
    }
}
