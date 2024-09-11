/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcDataOperation.java /main/16 2013/09/05 10:36:14 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    rgour     05/15/13 - adding methods to retrieve employee record without
 *                         roles or fingerprints
 *    cgreene   12/01/10 - use StringBuilder
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/15/10 - remove deprecate string-to-timestamp methods
 *    abondala  01/03/10 - update header date
 *    ddbaker   10/09/08 - Update based on code review
 *
     $Log:
      9    360Commerce 1.8         1/28/2008 4:31:09 PM   Sandy Gu
           Export foreign currency id, code and exchange rate for store credit
            and gift certificate foreign tender.
      8    360Commerce 1.7         4/25/2007 10:01:17 AM  Anda D. Cadar   I18N
           merge
      7    360Commerce 1.6         11/9/2006 7:28:30 PM   Jack G. Swan
           Modifided for XML Data Replication and CTR.
      6    360Commerce 1.5         9/28/2006 4:45:50 PM   Brett J. Larsen
           removing unreferenced method - i conflicted w/ a new method in
           JdbcUtilities
      5    360Commerce 1.4         7/25/2006 7:05:47 PM   Robert Zurga    .v7x
                1.2.1.0     6/22/2006 3:52:06 PM   Michael Wisbauer added
           setting flag for getting all employees.
           
      4    360Commerce 1.3         7/25/2006 6:40:17 PM   Robert Zurga    Merge
            from JdbcDataOperation.java, Revision 1.2.1.0 
      3    360Commerce 1.2         3/31/2005 4:28:36 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:22:36 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:11:53 PM  Robert Pearse   
     $
     Revision 1.7  2004/09/23 00:30:50  kmcbride
     @scr 7211: Inserting serialVersionUIDs in these Serializable classes

     Revision 1.6  2004/04/09 16:55:46  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.5  2004/02/17 17:57:36  bwf
     @scr 0 Organize imports.

     Revision 1.4  2004/02/17 16:18:46  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:13  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:23  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
     updating to pvcs 360store-current


 * 
 *    Rev 1.1   Jan 13 2004 15:57:36   jriggins
 * Removed deprecated calls including calls to ResultSet.getBigDecimal(index, scale).
 * Resolution for 3578: Remove hardcoded scale in JdbcDataOperation
 * 
 *    Rev 1.0   Aug 29 2003 15:30:30   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.11   Jun 12 2003 16:48:22   jgs
 * Fixed various date conversion problems.
 * 
 *    Rev 1.10   Jun 11 2003 12:52:02   jgs
 * Moved functionality to commerce services for use by both old and new technology.
 * 
 *    Rev 1.9   May 18 2003 09:06:24   mpm
 * Merged 5.1 changes into 6.0
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.8   Apr 07 2003 19:20:42   baa
 * database I18n support
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.7   Mar 28 2003 11:17:58   RSachdeva
 * Introduced dbResultsKeyValue new method
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.6   Mar 24 2003 16:34:06   baa
 * add multiple item descriptions for supported locales
 * Resolution for POS SCR-1843: Multilanguage support
 *
 *    Rev 1.5   Jan 14 2003 15:57:26   sfl
 * Introduced a new helper class method in the MakeSafeString method to handle back slash for different database.
 * Resolution for POS SCR-1909: Update JdbcDataOperation to handle backslashes across all databases
 *
 *    Rev 1.4   27 Sep 2002 13:35:56   sfl
 * Added backslash handling in the MakeSafeString method.
 * Added new methods to support reading longer precision CurrencyIfc
 * object from the database.
 *
 *    Rev 1.3   Aug 14 2002 18:12:02   baa
 * create global instances for number format and date format objects
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Jun 27 2002 15:09:14   pdd
 * updated javadoc for safeSQLCast()
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.1   Jun 10 2002 11:14:52   epd
 * Merged in changes for Oracle
 * Resolution for Domain SCR-83: Merging database fixes into base code
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.common.data.JdbcUtilities;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.common.utility._360DateIfc;
import oracle.retail.stores.common.utility._360TimeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSTime;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataOperationIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This class provides methods commonly used by data operations.
 * 
 * @version $Revision: /main/16 $
 */
public abstract class JdbcDataOperation extends JdbcUtilities implements DataOperationIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -3377770601350479447L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(JdbcDataOperation.class);

    /*
     * These constants should reference those in DataException.
     */
    protected static final int UNKNOWN = DataException.UNKNOWN;
    protected static final int SQL_ERROR = DataException.SQL_ERROR;
    protected static final int WARNING = DataException.WARNING;
    protected static final int CONNECTION_ERROR = DataException.CONNECTION_ERROR;
    protected static final int TRANSACTION_SEQUENCE = DataException.TRANSACTION_SEQUENCE;
    protected static final int CONFIG_ERROR = DataException.CONFIG_ERROR;
    protected static final int NO_DATA = DataException.NO_DATA;
    protected static final int DATA_FORMAT = DataException.DATA_FORMAT;

    /** The DataOperation name **/
    protected String name = null;

    /**
     * Class constructor.
     */
    public JdbcDataOperation()
    {
    }

    /**
     * Set all data members should be set to their initial state.
     * 
     * @exception DataException
     */
    public void initialize() throws DataException
    {
        // do nothing
    }

    /**
     * Returns an EYSDate object pulled from a timestamp column.
     * 
     * @param rs ResultSet
     * @param index result set index
     * @return the equivelant EYSDate object
     */
    public static EYSDate timestampToEYSDate(ResultSet rs, int index) throws SQLException
    {
        Timestamp ts = rs.getTimestamp(index);
        return (timestampToEYSDate(ts));
    }

    /**
     * Convert a java.sql.Timestamp to a Date.
     * 
     * @param timestamp The timestamp to convert
     * @return the equivalent Date object
     */
    public static EYSDate timestampToEYSDate(Timestamp timestamp)
    {
        _360DateIfc date = jdbcHelperClass.timestampTo_360Date(timestamp);
        if (date == null)
        {
            return null;
        }
        return (new EYSDate(date.dateValue()));
    }

    /**
     * Returns EYSTime object for time string retrieved via SQL.
     * 
     * @param rs ResultSet
     * @param index index into result set
     * @return EYSTime object
     * @exception SQLException thrown if error occurs parsing data from result
     *                set
     */
    public static EYSTime timeToEYSTime(ResultSet rs, int index) throws SQLException
    {
        _360TimeIfc time = jdbcHelperClass.timeTo_360Time(rs, index);
        if (time == null)
        {
            return null;
        }
        return (new EYSTime(time.dateValue()));
    }

    /**
     * Returns EYSDate object for time string retrieved via SQL.
     * 
     * @param rs ResultSet
     * @param index index into result set
     * @return EYSDate object
     * @exception SQLException thrown if error occurs parsing data from result
     *                set
     */
    public static EYSDate dateToEYSDate(ResultSet rs, int index) throws SQLException
    {
        EYSDate returnDate = timestampToEYSDate(rs, index);
        return (returnDate);
    } 

    /**
     * Returns time string for use in SQL operation, converted from EYSTime
     * object.
     * 
     * @param eTime EYSTime object
     * @return SQL-ready string (with quotes)
     */
    public static String EYSTimetoSQLTimeString(EYSTime eTime)
    {
        return _360TimetoSQLTimeString((_360TimeIfc) eTime);
    }

    /**
     * Parse date field.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * 
     * @param rs ResultSet
     * @param index index into result set
     * @return EYSDate object built from result set
     */
    public static EYSDate getEYSDateFromString(ResultSet rs, int index) throws SQLException, DataException
    {
        _360DateIfc date = get_360DateFromString(rs, index);
        if (date == null)
        {
            return null;
        }
        return new EYSDate(date.dateValue());
    }

    /**
     * Returns the employee for the given employeeID. Uses the
     * <code>JdbcEmployeeLookupOperation</code> data operation to read the
     * employee from the data source.
     * 
     * @param dataConnection The connection to the data source
     * @param employeeID The employee id
     * @return the employee
     * @exception DataException upon error.
     */
    protected EmployeeIfc getEmployee(JdbcDataConnection dataConnection, String employeeID) throws DataException
    {
        /*
         * Design-wise, this is probably not the best way to do this, but it's
         * fast and effective (at least for now).
         */
        EmployeeIfc employee = null;
        try
        {
            JdbcEmployeeLookupOperation dbOp = new JdbcEmployeeLookupOperation();
            employee = dbOp.selectEmployeeByNumber(dataConnection, employeeID);
        }
        catch (DataException de)
        {
            if (de.getErrorCode() == DataException.NO_DATA)
            {
                employee = DomainGateway.getFactory().getEmployeeInstance();
                employee.setEmployeeID(employeeID);
            }
            else
            {
                throw de;
            }
        }

        return employee;
    }

    /**
     * Returns the employee details for the given employeeID. Uses the
     * <code>JdbcEmployeeLookupOperation</code> data operation to read the
     * employee header from the data source.
     * 
     * @param dataConnection The connection to the data source
     * @param employeeID The employee id
     * @return the employee
     * @exception DataException upon error.
     */
    protected EmployeeIfc getEmployeeHeader(JdbcDataConnection dataConnection, String employeeID) throws DataException
    {      
        EmployeeIfc employee = null;
        try
        {
            JdbcEmployeeLookupOperation dbOp = new JdbcEmployeeLookupOperation();            
            employee = dbOp.selectEmployeeHeader(dataConnection, employeeID);
        }
        catch (DataException de)
        {
            if (de.getErrorCode() == DataException.NO_DATA)
            {
                employee = DomainGateway.getFactory().getEmployeeInstance();
                employee.setEmployeeID(employeeID);
            }
            else
            {
                throw de;
            }
        }

        return employee;
    }
    
    /**
     * Retrieves CurrencyIfc reference from database field stored as decimal
     * 
     * @param rs ResultSet
     * @param index index into result set
     * @return CurrencyIfc object built from result set
     */
    public static CurrencyIfc getCurrencyFromDecimal(ResultSet rs, int index) throws SQLException
    {
        return getCurrencyFromDecimal(rs, index, DomainGateway.getBaseCurrencyType().getCountryCode());
    }

    /**
     * Retrieves CurrencyIfc reference from database field stored as decimal
     * with 5 digits after decimal point. This is only used for tax amount
     * 
     * @param rs ResultSet
     * @param index index into result set
     * @return CurrencyIfc object built from result set
     */
    public static CurrencyIfc getLongerCurrencyFromDecimal(ResultSet rs, int index) throws SQLException
    {
        return getLongerCurrencyFromDecimal(rs, index, DomainGateway.getBaseCurrencyType().getCountryCode());
    }

    /**
     * Retrieves CurrencyIfc reference of the appropriate currency from database
     * field stored as decimal
     * 
     * @param rs ResultSet
     * @param index index into result set
     * @param currencyCode String
     * @return CurrencyIfc object built from result set
     */
    public static CurrencyIfc getCurrencyFromDecimal(ResultSet rs, int index, String currencyCode) throws SQLException
    {
        /*
         * Use the scale from the database to initialize the BigDecimal
         */
        CurrencyIfc c = null;
        java.math.BigDecimal d = rs.getBigDecimal(index);
        if (d == null)
        {
            c = DomainGateway.getCurrencyInstance(currencyCode);
        }
        else
        {
            c = DomainGateway.getCurrencyInstance(currencyCode, d.toString());
        }
        return (c);
    }

    /**
     * Retrieves CurrencyIfc reference of the appropriate currency from database
     * field stored as decimal with 5 digits after decimal point. This is only
     * used for tax amount
     * 
     * @param rs ResultSet
     * @param index index into result set
     * @param currencyCode String
     * @return CurrencyIfc object built from result set
     */
    public static CurrencyIfc getLongerCurrencyFromDecimal(ResultSet rs, int index, String currencyCode)
            throws SQLException
    {
        /*
         * Use the scale from the database to initialize the BigDecimal
         */
        CurrencyIfc c = null;
        java.math.BigDecimal d = rs.getBigDecimal(index);
        if (d == null)
        {
            c = DomainGateway.getCurrencyInstance(currencyCode);
        }
        else
        {
            c = DomainGateway.getCurrencyInstance(currencyCode, d.toString());
        }
        return (c);
    }

    /**
     * Finds currency type matching id.
     * 
     * @param id currency id
     * @return currency type
     */
    protected static CurrencyTypeIfc getCurrencyType(int id)
    {
        CurrencyTypeIfc baseType = DomainGateway.getBaseCurrencyType();
        if (baseType.getCurrencyId() == id)
        {
            return baseType;
        }
        CurrencyTypeIfc[] alternateTypes = DomainGateway.getAlternateCurrencyTypes();
        for (int i = 0; i < alternateTypes.length; i++)
        {
            if (alternateTypes[i].getCurrencyId() == id)
            {
                return alternateTypes[i];
            }
        }

        return null;
    }

    /**
     * Set the name.
     * 
     * @param name The name to assign the operation.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Return the name of the operation.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Retrieves Alternate CurrencyIfc reference from database field stored as
     * decimal
     * 
     * @param rs ResultSet
     * @param index index into result set
     * @return Alternate CurrencyIfc object built from result set
     */
    public static CurrencyIfc getAltCurrencyFromCountryCode(String value, String code)
    {
        CurrencyIfc altCurrency = null;
        try
        {
            altCurrency = DomainGateway.getAlternateCurrencyInstance(code, value);
        }
        catch (IllegalArgumentException e)
        {
            logger.error("Unable to retrieve currency: " + code + "");
        }
        return (altCurrency);
    }

    /**
     * Returns string with prepended and appended single quotes.
     * 
     * @param input input string
     * @return string with prepended and appended single quotes
     */
    public static String inQuotes(String inputString)
    {
        StringBuilder builder = new StringBuilder("'");
        builder.append(inputString);
        builder.append("'");
        return builder.toString();
    }

    /**
     * Returns string with prepended and appended single quotes for integer
     * value.
     * 
     * @param input input integer
     * @return string with prepended and appended single quotes
     */
    public static String inQuotes(int inputInt)
    {
        return inQuotes(Integer.toString(inputInt));
    }

    /**
     * Returns an IN clause that contains all the locales
     * 
     * @param bestMatches
     * @return String
     */
    public static String buildINClauseString(Set<Locale> bestMatches)
    {
        StringBuilder inString = new StringBuilder(" IN (");
        Iterator<Locale> it = bestMatches.iterator();
        Locale bestMatch;
        while (it.hasNext())
        {
            bestMatch = it.next();
            inString.append(inQuotes(bestMatch.toString()));
            if (it.hasNext())
            {
                inString.append(",");
            }
        }
        inString.append(")");
        return inString.toString();
    }

    /**
     * Stores Database Results as key-value pair using Specified Qualifier.
     * 
     * @param dataConnection data connection
     * @param tableName name of the database table
     * @param aliasName alias name for the database table
     * @param keyColumn coloumn name used for key
     * @param valueColumns coma delimited strings used for values
     * @param qualifier Qualifier to be used for query
     * @return Map Stores the DB results as key-value
     * @exception SQLException thrown if result set cannot be parsed
     * @exception DataException thrown if no records in result set
     */
    public Map<String,Object> dbResultsKeyValue(JdbcDataConnection dataConnection, String tableName, String aliasName,
            String keyColumn, String valueColumns, String qualifier) throws SQLException, DataException
    {
        Map<String,Object> dbResults = new HashMap<String,Object>(0);
        SQLSelectStatement sql = new SQLSelectStatement();
        StringTokenizer columnList = new StringTokenizer(valueColumns, "'", false);

        if (Util.isEmpty(aliasName))
        {
            // add table
            sql.addTable(tableName);
            // add columns
            sql.addColumn(keyColumn);

            while (columnList.hasMoreTokens())
            {
                sql.addColumn(columnList.nextToken());
            }
        }
        else
        {
            // add table
            sql.addTable(tableName, aliasName);
            // add columns
            sql.addColumn(aliasName + "." + keyColumn);
            while (columnList.hasMoreTokens())
            {
                sql.addColumn(aliasName + "." + columnList.nextToken());
            }

        }

        // add qualifer
        if (!Util.isEmpty(qualifier))
        {
            sql.addQualifier(qualifier);
        }
        dataConnection.execute(sql.getSQLString());
        ResultSet rs = (ResultSet) dataConnection.getResult();

        String key = null;
        ArrayList<String> value = null;

        while (rs.next())
        {
            int valueCount = rs.getMetaData().getColumnCount();
            value = new ArrayList<String>();
            key = getSafeString(rs, 1);

            for (int index = 2; index <= valueCount; index++)
            {
                value.add(index - 2, getSafeString(rs, index));
            }
            // storing results in map
            if (value.size() == 1)
            {
                dbResults.put(key, value.get(0));
            }
            else
            {
                dbResults.put(key, value);
            }
        }
        rs.close();
        return dbResults;
    }

    /**
     * Parses resultSet from localized text query and adds localized name and
     * locale to supplied localizedText object. The query must contain columns
     * for locale and localized name.
     * <p>
     * <i>This method will not advance the cursor. It is the responsibility of
     * the calling method to make sure the cursor is in a valid position to
     * obtain results</i>
     * 
     * @param rs the resultSet with the cursor in the desired position
     * @param localizedNames the localized text to which localized name and
     *            locale are to be added
     * @param localeIndex the index to obtain a String value from the resultSet
     *            representing the locale
     * @param nameIndex the index to obtain a String value from the resultSet
     *            representing the localized name
     * @return the supplied instance of LocalizedText with added localizedName
     * @throws SQLException
     */
    public static LocalizedTextIfc parseLocalizedNameResults(ResultSet rs, LocalizedTextIfc localizedNames,
            int localeIndex, int nameIndex) throws SQLException
    {
        String locationName = getSafeString(rs, nameIndex);
        Locale locale = LocaleUtilities.getLocaleFromString(getSafeString(rs, localeIndex));
        localizedNames.putText(locale, locationName);

        return localizedNames;
    }
}
