/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadLayaway.java /main/18 2013/05/16 13:14:30 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     05/16/13 - Modified to prevent retrieval of suspended Layaways
 *                         for payments or completion.
 *    mchellap  10/09/12 - Added previousTotalAmountPaid attribute for fiscal
 *                         printing
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    acadar    04/13/09 - make layaway location required; refactor the way we
 *                         handle layaway reason codes
 *    cgreene   03/30/09 - implement printing of layaway location on receipt by
 *                         adding new location code to layaway object and
 *                         deprecating the old string
 *    sgu       10/30/08 - check in after refresh
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/27/2006 7:26:57 PM   Brett J. Larsen CR
 *         17307 - remove inventory functionality - stage 2
 *    4    360Commerce 1.3         1/25/2006 4:11:16 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:44 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:59 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:28:02    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:41     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:44     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:59     Robert Pearse
 *
 *   Revision 1.7  2004/06/29 21:58:58  aachinfiev
 *   Merge the changes for inventory & POS integration
 *
 *   Revision 1.6  2004/04/09 16:55:44  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:35  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:44  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:21  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:31:58   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Jan 28 2003 14:17:04   crain
 * Added ordering for the sql
 * Resolution for 1860: Layaway -- different results when searching for a completed layway by customer and by layaway number
 *
 *    Rev 1.1   Jun 10 2002 11:14:54   epd
 * Merged in changes for Oracle
 * Resolution for Domain SCR-83: Merging database fixes into base code
 *
 *    Rev 1.2   Jun 07 2002 17:47:40   epd
 * Merging in fixes made for McDonald's Oracle demo
 * Resolution for Domain SCR-83: Merging database fixes into base code
 *
 *    Rev 1.1   Mar 18 2002 22:47:24   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:07:26   msg
 * Initial revision.
 *
 *    Rev 1.2   Mar 01 2002 21:43:44   dfh
 * reads all 4 pieces of the layaway legal statement and stitches them together to include newline chars
 * Resolution for POS SCR-1414: Layaway does not support multi-line legal statement
 *
 *    Rev 1.1   Feb 05 2002 16:33:36   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 *
 *    Rev 1.0   Sep 20 2001 15:58:26   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:28   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;

/**
 * JdbcReadLayaway implements the layaway lookup JDBC data store operation.
 */
public class JdbcReadLayaway extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 1889085719014316439L;
    /** The logger to which log messages will be sent. */
    private static final Logger logger = Logger.getLogger(JdbcReadLayaway.class);
    /** revision number of this class */
    public static final String revisionNumber = "$Revision: /main/18 $";

    /**
     * Class constructor.
     */
    public JdbcReadLayaway()
    {
    }

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
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadLayaway.execute");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // look up the layaway
        LayawayIfc inputLayaway = (LayawayIfc) action.getDataObject();

        LayawayIfc layaway = readLayaway(connection, inputLayaway, inputLayaway.getLocaleRequestor());
        dataTransaction.setResult(layaway);

        if (logger.isDebugEnabled()) logger.debug( "JdbcReadLayaway.execute");
    }

    /**
     * Selects a layaway from the layaway table.
     *
     * @param dataConnection a connection to the database
     * @param inputLayaway layaway containing key values
     * @return selected layaway
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     * @deprecated as of 13.1. Use {@link #readLayaway(JdbcDataConnection, LayawayIfc, LocaleRequestor)}
     */
    protected LayawayIfc readLayaway(JdbcDataConnection dataConnection,
                                     LayawayIfc inputLayaway)
                                     throws DataException
    {
       return readLayaway(dataConnection, inputLayaway, new LocaleRequestor(LocaleMap.getLocale(LocaleMap.DEFAULT)));
    }


    /**
     * Selects a layaway from the layaway table.
     *
     * @param dataConnection a connection to the database
     * @param inputLayaway layaway containing key values
     * @param localeRequestor LocaleRequestor with all supported locales
     * @return selected layaway
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected LayawayIfc readLayaway(JdbcDataConnection dataConnection, LayawayIfc inputLayaway, LocaleRequestor localeRequestor)
                                     throws DataException
    {
        LayawayIfc layaway = null;
        // build SQL statement, execute and parse
        SQLSelectStatement sql = buildLayawaySQLStatement(inputLayaway);

        try
        {
            ResultSet rs = execute(dataConnection, sql);
            layaway = parseLayawayResultSet(rs);

            readLocationCode(dataConnection, layaway, localeRequestor);
        }
        catch (DataException de)
        {
            logger.warn(de.toString());
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "Layaway lookup");
            throw new DataException(DataException.SQL_ERROR, "Layaway lookup", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "Layaway lookup", e);
        }

        return(layaway);
    }

    /**
     * Builds and returns SQL statement for retrieving a layaway.
     *
     * @param layaway input layaway
     * @return SQL select statement for retrieving a layaway
     */
    protected SQLSelectStatement buildLayawaySQLStatement(LayawayIfc layaway)
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        // add tables
        sql.addTable(TABLE_LAYAWAY);
        // add select columns
        addLayawaySelectColumns(sql);
        // add qualifiers
        addLayawayQualifierColumns(sql, layaway);
        // order the results in descending order by creation date
        sql.addOrdering(FIELD_LAYAWAY_ORIGINAL_TRANSACTION_BUSINESS_DATE + " DESC");

        return(sql);
    }

    /**
     * Adds select columns for layaway lookup to SQL statement.
     *
     * @param sql SQLSelectStatement object
     */
    protected void addLayawaySelectColumns(SQLSelectStatement sql)
    {
        sql.addColumn(FIELD_RETAIL_STORE_ID);
        sql.addColumn(FIELD_LAYAWAY_ORIGINAL_WORKSTATION_ID);
        sql.addColumn(FIELD_LAYAWAY_ORIGINAL_TRANSACTION_BUSINESS_DATE);
        sql.addColumn(FIELD_LAYAWAY_ORIGINAL_TRANSACTION_SEQUENCE_NUMBER);
        sql.addColumn(FIELD_LAYAWAY_ID);
        sql.addColumn(FIELD_CUSTOMER_ID);
        sql.addColumn(FIELD_LAYAWAY_STATUS);
        sql.addColumn(FIELD_LAYAWAY_PREVIOUS_STATUS);
        sql.addColumn(FIELD_LAYAWAY_TIMESTAMP_LAST_STATUS_CHANGE);
        sql.addColumn(FIELD_LAYAWAY_EXPIRATION_DATE);
        sql.addColumn(FIELD_LAYAWAY_GRACE_PERIOD);
        sql.addColumn(FIELD_LAYAWAY_MINIMUM_DOWN_PAYMENT);
        sql.addColumn(FIELD_LAYAWAY_STORAGE_LOCATION);
        sql.addColumn(FIELD_LAYAWAY_TOTAL_AMOUNT);
        sql.addColumn(FIELD_LAYAWAY_CREATION_FEE);
        sql.addColumn(FIELD_LAYAWAY_DELETION_FEE);
        sql.addColumn(FIELD_LAYAWAY_TOTAL_PAYMENTS_COLLECTED);
        sql.addColumn(FIELD_LAYAWAY_COUNT_PAYMENTS_COLLECTED);
        sql.addColumn(FIELD_LAYAWAY_BALANCE_DUE);
        sql.addColumn(FIELD_LAYAWAY_LEGAL_STATEMENT);
        sql.addColumn(FIELD_LAYAWAY_LEGAL_STATEMENT1);
        sql.addColumn(FIELD_LAYAWAY_LEGAL_STATEMENT2);
        sql.addColumn(FIELD_LAYAWAY_LEGAL_STATEMENT3);
        sql.addColumn(FIELD_TRAINING_MODE);
    }

    /**
     * Adds qualifiers for layaway lookup to SQL statement.
     *
     * @param sql SQLSelectStatement object
     * @param layaway input layaway
     */
    protected void addLayawayQualifierColumns(SQLSelectStatement sql,
                                              LayawayIfc layaway)
    {
        // add layaway ID to predicate
        sql.addQualifier(FIELD_LAYAWAY_ID, "UPPER(" + makeSafeString(layaway.getLayawayID()) + ")");
        sql.addQualifier(FIELD_TRAINING_MODE, makeStringFromBoolean(layaway.getTrainingMode()));
        sql.addQualifier(FIELD_LAYAWAY_STATUS + " != " +
                         Integer.toString(LayawayConstantsIfc.STATUS_UNDEFINED));
    }

    /**
     * Parse layaway result set and returns layaway object.
     *
     * @param rs ResultSet object
     * @return LayawayIfc object
     * @exception thrown if error parsing result set
     */
    protected LayawayIfc parseLayawayResultSet(ResultSet rs) throws DataException, SQLException
    {
        LayawayIfc layaway = null;
        try
        {
            // begin parse-result-set try block
            if (rs.next())
            {
                // begin handle result set
                layaway = DomainGateway.getFactory().getLayawayInstance();
                int index = 0;
                // parse result set fields
                layaway.setStoreID(getSafeString(rs, ++index));
                String workstationID = getSafeString(rs, ++index);
                layaway.setInitialTransactionBusinessDate(getEYSDateFromString(rs, ++index));
                int sequenceNumber = rs.getInt(++index);
                layaway.setLayawayID(getSafeString(rs, ++index));
                String customerID = getSafeString(rs, ++index);
                layaway.setStatus(rs.getInt(++index));
                layaway.setPreviousStatus(rs.getInt(++index));
                layaway.setLastStatusChange(timestampToEYSDate(rs, ++index));
                layaway.setExpirationDate(dateToEYSDate(rs, ++index));
                layaway.setGracePeriodDate(dateToEYSDate(rs, ++index));
                layaway.setMinimumDownPayment(getCurrencyFromDecimal(rs, ++index));
                String locationCode = getSafeString(rs, ++index);
                LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
                localizedCode.setCode(locationCode);
                layaway.setLocationCode(localizedCode);
                layaway.setTotal(getCurrencyFromDecimal(rs, ++index));
                layaway.setCreationFee(getCurrencyFromDecimal(rs, ++index));
                layaway.setDeletionFee(getCurrencyFromDecimal(rs, ++index));
                layaway.setTotalAmountPaid(getCurrencyFromDecimal(rs, ++index));
                layaway.setPreviousTotalAmountPaid(layaway.getTotalAmountPaid());
                layaway.setPaymentCount(rs.getInt(++index));
                layaway.setBalanceDue(getCurrencyFromDecimal(rs, ++index));
                String legal = getSafeStringNoTrim(rs, ++index).concat(getSafeStringNoTrim(rs, ++index)).concat(
                    getSafeStringNoTrim(rs, ++index)).concat(getSafeStringNoTrim(rs, ++index));
                layaway.setLegalStatement(legal);
                layaway.setTrainingMode(getBooleanFromString(rs, ++index));
                // set transaction ID
                TransactionIDIfc transactionID =
                  DomainGateway.getFactory().getTransactionIDInstance();
                transactionID.setWorkstationID(workstationID);
                transactionID.setSequenceNumber(sequenceNumber);
                transactionID.setStoreID(layaway.getStoreID());
                layaway.setInitialTransactionID(transactionID);

                // set customer, if needed
                if (!(Util.isEmpty(customerID)))
                {
                    CustomerIfc customer =
                      DomainGateway.getFactory().getCustomerInstance();
                    customer.setCustomerID(customerID);
                    layaway.setCustomer(customer);
                }

                // Switch status to Expired if status is active
                // and grace period has passed
                checkForExpiredLayaway(layaway);
            }
        }
        catch (SQLException se)
        {
            throw se;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "Layaway lookup", e);
        }
        finally
        {
            if (rs != null)
            {
                rs.close();
            }
        }

        if (layaway == null)
        {
            throw new DataException(DataException.NO_DATA,
                "No layaway was found processing the result set in JdbcReadLayaway.");
        }

        return(layaway);

    }

    /**
     * Executes the SQL Statement.
     *
     * @param dataConnection a connection to the database
     * @param sql the SQl statement
     * @param int id comparison basis type
     * @return result set
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected ResultSet execute(JdbcDataConnection dataConnection,
                                SQLSelectStatement sql)
        throws DataException
    {

        ResultSet rs;
        String sqlString = sql.getSQLString();
        dataConnection.execute(sqlString);
        rs = (ResultSet) dataConnection.getResult();
        return rs;
    }

    /**
     * Switches the status to Expired if the status is active and the grace
     * period has passed.
     *
     * @param summary LayawaySummaryEntryIfc to check
     * @param gracePeriodDate Date of end of grace period
     */
    protected void checkForExpiredLayaway(LayawayIfc layaway)
    {
        // get current date and set to date-type-only
        EYSDate currentDate = DomainGateway.getFactory().getEYSDateInstance();
        currentDate.initialize(EYSDate.TYPE_DATE_ONLY);
        EYSDate gracePeriodDate = layaway.getGracePeriodDate();
        int layawayStatus = layaway.getStatus();

        if ( (layawayStatus == LayawayConstantsIfc.STATUS_NEW ||
              layawayStatus == LayawayConstantsIfc.STATUS_ACTIVE) &&
              gracePeriodDate.before(currentDate) )
        {
            layaway.setStatus(LayawayConstantsIfc.STATUS_EXPIRED);
        }
        // Include if we want to change an expired back to active due
        // to changes in system time.
        else if (layawayStatus == LayawayConstantsIfc.STATUS_EXPIRED &&
                 gracePeriodDate.after(currentDate))
        {
            if (layaway.getPreviousStatus() == LayawayConstantsIfc.STATUS_NEW)
            {
                layaway.setStatus(LayawayConstantsIfc.STATUS_NEW);
            }
            else
            {
                layaway.setStatus(LayawayConstantsIfc.STATUS_ACTIVE);
            }
        }
    }

    /**
     * Read localized location code
     *
     * @param dataConnection
     * @param lawayay
     * @deprecated as of 13.1. Use {@link #readLocationCode(JdbcDataConnection, LayawayIfc, LocaleRequestor)}
     */
    protected void readLocationCode(JdbcDataConnection dataConnection, LayawayIfc layaway)
    {
        readLocationCode(dataConnection, layaway, new LocaleRequestor(LocaleMap.getLocale(LocaleMap.DEFAULT)));
    }

    /**
     * Read localized location code
     *
     * @param dataConnection
     * @param lawayay
     * @param LocaleRequestor
     */
    protected void readLocationCode(JdbcDataConnection dataConnection, LayawayIfc layaway, LocaleRequestor localeRequestor)
    {
        String locationCode = layaway.getLocationCode().getCode();

        if(locationCode != null && !CodeConstantsIfc.CODE_UNDEFINED.equals(locationCode))
        {
            // Read Localized Reason Code
            CodeSearchCriteriaIfc criteria = DomainGateway.getFactory().getCodeSearchCriteriaInstance();
            criteria.setStoreID(layaway.getStoreID());
            criteria.setListID(CodeConstantsIfc.CODE_LIST_LAYAWAY_LOCATION_REASON_CODES);
            criteria.setLocaleRequestor(localeRequestor);
            criteria.setCode(locationCode);

            JdbcReadCodeList readCodeList = new JdbcReadCodeList();
            try
            {
                LocalizedCodeIfc code = readCodeList.readCode(dataConnection, criteria);
                layaway.setLocationCode(code);
            }
            catch (DataException e)
            {
                // Localized Reason Not found. Exception already logged.
            }
        }
    }

    /**
     * Returns string pulled from result set. If string is null, string is set
     * to empty string.
     *
     * @param rs ResultSet
     * @param index index into result set
     * @return str data from result set
     * @exception throws SQLException if result set getString() fails
     */
    public static String getSafeStringNoTrim(ResultSet rs,
                                       int index)
        throws SQLException
    {
        // retrieve string
        String str = rs.getString(index);
        // set empty string if null
        if (str == null)
        {
            str = "";
        }
        return (str); // return string
    }
}
