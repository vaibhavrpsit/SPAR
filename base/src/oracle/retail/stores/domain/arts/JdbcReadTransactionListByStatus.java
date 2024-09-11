/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadTransactionListByStatus.java /main/17 2013/09/05 10:36:19 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    jswan  01/05/12 - Refactor the status change of suspended transaction to
 *                      occur in a transaction so that status change can be
 *                      sent to CO as part of DTM.
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    cgreen 09/15/11 - removed deprecated methods and changed static methods
 *                      to non-static
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    sgu    10/30/08 - check in after refresh
 *    sgu    10/30/08 - refactor layaway and transaction summary object to take
 *                      localized text
 *    mdecam 10/28/08 - I18N - Refactoring Transaction Suspend Reasons
 *    ohorne 10/22/08 - I18N StoreInfo-related changes
 *
 * ===========================================================================

     $Log:
      5    360Commerce 1.4         7/19/2007 7:25:26 PM   Maisa De Camargo
           Fixed Logic to Exclude Void Transactions when retrieving Suspended
           Transactions.
      4    360Commerce 1.3         1/25/2006 4:11:19 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      3    360Commerce 1.2         3/31/2005 4:28:42 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:22:46 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:00 PM  Robert Pearse
     $:
      4    .v700     1.2.1.0     11/16/2005 16:26:16    Jason L. DeLeau 4215:
           Get rid of redundant ArtsDatabaseifc class
      3    360Commerce1.2         3/31/2005 15:28:42     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:22:46     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:12:00     Robert Pearse
     $
     Revision 1.8  2004/04/09 16:55:46  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.7  2004/04/03 00:17:03  cdb
     @scr 4166 Unit testing updates. Added method including sqlLocale so that it wouldn't
     be overriden by what's in JdbcReadTransactionHistory. Problem uncovered in unit testing.

     Revision 1.6  2004/02/19 22:04:46  aarvesen
     @scr 0 use the newer style of SQLStatement

     Revision 1.5  2004/02/17 17:57:37  bwf
     @scr 0 Organize imports.

     Revision 1.4  2004/02/17 16:18:47  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:17  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:25  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:32:16   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Apr 07 2003 10:27:50   bwf
 * Database Internationalization
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.0   Jun 03 2002 16:38:36   msg
 * Initial revision.
 *
 *    Rev 1.2   May 14 2002 21:05:40   mpm
 * Made corrections for DB2.
 * Resolution for Domain SCR-50: db2 port fixes
 *
 *    Rev 1.1   Mar 18 2002 22:47:56   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:07:52   msg
 * Initial revision.
 *
 *    Rev 1.1   18 Jan 2002 17:47:02   vxs
 * In readTransactionListByStatus(), added sql.addQualifier(FIELD_TRANSACTION_TRAINING_FLAG...)
 * Resolution for POS SCR-144: Training mode susp transactions are on the susp trans report
 *
 *    Rev 1.0   Sep 20 2001 15:57:32   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:16   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.domain.arts;
// java imports
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleMapConstantsIfc;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * This operation lists all the suspended transactions for a given store and
 * business date. It returns an array of transaction summaries and is extended
 * from JdbcReadTransactionHistory.
 * 
 * @see oracle.retail.stores.domain.arts.JdbcReadTransactionHistory
 * @see oracle.retail.stores.domain.transaction.TransactionSummaryIfc
 * @version $Revision: /main/17 $
 */
public class JdbcReadTransactionListByStatus extends JdbcReadTransactionHistory implements ARTSDatabaseIfc
{

    private static final long serialVersionUID = 8442792282693458418L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadTransactionListByStatus.class);

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/17 $";

    /**
     * Class constructor.
     */
    public JdbcReadTransactionListByStatus()
    {
        setName("JdbcReadTransactionListByStatus");
    }

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction
     * @param dataConnection
     * @param action
     */
    @Override
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        logger.debug("JdbcReadTransactionListByStatus.execute");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // grab arguments and call readTransactions()
        Vector<TransactionSummaryIfc> summaryVector = null;

        LocaleRequestor localeRequestor = null;
        TransactionSummaryIfc key = null;

        if(action.getDataObject() instanceof SearchCriteriaIfc)
        {
            SearchCriteriaIfc inquiry = (SearchCriteriaIfc) action.getDataObject();

            localeRequestor = inquiry.getLocaleRequestor();
            key = inquiry.getTransactionSummary();
            summaryVector = readTransactionListByStatus(connection, key, localeRequestor);
        }
        else if(action.getDataObject() instanceof TransactionSummaryIfc)
        {
            //this block is deprecated as of release 13.1.
            key = (TransactionSummaryIfc) action.getDataObject();
            summaryVector = readTransactionListByStatus(connection, key, new LocaleRequestor(LocaleMap.getLocale(LocaleMapConstantsIfc.DEFAULT)));
        }
        else
        {
            logger.error(getName() + "Unknown data object: " + action.getDataObject());
            throw new DataException("Unknown data object");
        }

        // copy result into an array
        TransactionSummaryIfc[] summary = new TransactionSummaryIfc[summaryVector.size()];
        summaryVector.copyInto(summary);

        // return array
        dataTransaction.setResult(summary);
        logger.debug("JdbcReadTransactionListByStatus.execute");
    }

    /**
     * Reads all transactions for a given status, store and business date. <P>
     * @param dataConnection      a connection to the database
     * @param key TransactionSummaryIfc object with key values set
     * @param locale
     * @return Vector of transactions
     * @throws DataException thrown when an error occurs executing the SQL against the DataConnection, or when
     * processing the ResultSet
     */
    protected Vector<TransactionSummaryIfc> readTransactionListByStatus (JdbcDataConnection dataConnection,
                                                                         TransactionSummaryIfc key,
                                                                         LocaleRequestor localeRequestor)
                                                                         throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcReadTransactionListByStatus.readTransactionListByStatus()");
        Vector<TransactionSummaryIfc> transVector = new Vector<TransactionSummaryIfc>(2);

        SQLSelectStatement sql = buildBaseSQL();

        // add customer ID qualifier
        sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_RETAIL_STORE_ID
                         + " = " + getStoreID(key));
        // add business date qualifier, if it exists
        if (key.getBusinessDate() != null)
        {
            sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE
                             + " = " + getBusinessDate(key));
        }
        // add status code qualifier
        sql.addQualifier(FIELD_TRANSACTION_STATUS_CODE
                         + " = " + key.getTransactionStatus());
        // add optional till identifier
        if (!(Util.isEmpty(key.getTillID())))
        {
            sql.addQualifier(FIELD_TENDER_REPOSITORY_ID +
                             " = '" + key.getTillID() + "'");
        }

        if (key.isTrainingModeUsedInQuery())
        {
            //add training mode qualifier
            sql.addQualifier(FIELD_TRANSACTION_TRAINING_FLAG,
                             makeStringFromBoolean(key.isTrainingMode()));
        }
        
        // add ordering clauses
        addOrdering(sql);

        try
        {
            // build sub-select to exclude post voided transaction
            Vector<Integer> postVoidTransVector = executePostVoidSQL(dataConnection, sql);
            ARTSExcludePostVoidSQL.buildSQL(sql, postVoidTransVector);

            transVector = executeAndParse(dataConnection,
                                          sql,
                                          localeRequestor);
        }
        catch (DataException de)
        {
            logger.warn(de.toString());
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "transaction table");
            throw new DataException(DataException.SQL_ERROR,
                                    "transaction table",
                                    se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN,
                                    "transaction table",
                                    e);
        }

        if (transVector.isEmpty())
        {
            logger.warn("No transactions found");
            throw new DataException(DataException.NO_DATA,
                                    "No transactions found");
        }
        
        if (logger.isInfoEnabled()) logger.info(
                        "Transactions found: " + transVector.size());

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcReadTransactionListByStatus.readTransactionListByStatus()");

        return(transVector);
    }

    /**
     * Adds clauses to specified SQL statement for ordering of the results.
     * 
     * @param sql SQLSelectStatement object
     */
    protected void addOrdering(SQLSelectStatement sql)
    {
        // add ordering by workstation ID and suspend timestamp
        sql.addOrdering(ALIAS_TRANSACTION + "." + FIELD_WORKSTATION_ID);
        sql.addOrdering(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_END_DATE_TIMESTAMP + " DESC");
    }

    /**
     * Fills additional attributes in transaction history.
     * 
     * @param dataConnection JDBC data connection
     * @param transVector Vector of transaction summaries
     * @param localeRequestor requested locales
     * @exception DataException if error occurs
     */
    protected void fillAdditionalAttributes(JdbcDataConnection dataConnection,
                                            Vector<TransactionSummaryIfc> transVector,
                                            LocaleRequestor localeRequestor)
            throws DataException
    {
        // fill additional fields as needed
        for (TransactionSummaryIfc summary : transVector)
        {
            // read first-item description
            readDescription(dataConnection, summary, localeRequestor);

            // read customer name
            if (summary.getCustomerID() != null &&
                summary.getCustomerID().length() > 0)
            {
                readCustomerName(dataConnection, summary);
            }

            // Read Localized Transaction Suspend Reason
            readSuspendReason(dataConnection, summary, localeRequestor);
        }
    }

    /**
     * Reads customer name from database. If customer is not found, no exception
     * is thrown.
     * 
     * @param dataConnection JDBC data connection
     * @param summary TransactionSummaryIfc object
     * @return PersonNameIfc object
     * @exception DataException if error occurs
     */
    public PersonNameIfc readCustomerName(JdbcDataConnection dataConnection, TransactionSummaryIfc summary)
            throws DataException
    {
        SQLSelectStatement sql = ReadARTSCustomerSQL.buildReadCustomerNameSQL(summary.getCustomerID());

        PersonNameIfc personName = null;
        try
        {   
            // begin execute and parse try block
            // execute transaction
            dataConnection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet) dataConnection.getResult();
            personName = ReadARTSCustomerSQL.readCustomerNameResults(rs);
            summary.setCustomerName(personName);
        }                                                               // end execute and parse try block
        catch (DataException de)
        {
            logger.warn(de.toString());
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se,
                                           "transaction customer name lookup");
            throw new DataException(DataException.SQL_ERROR,
                                    "transaction customer name lookup",
                                    se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN,
                                    "transaction customer name lookup", e);
        }

        return(personName);
    }

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
