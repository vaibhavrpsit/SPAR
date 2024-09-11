/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadEmployee.java /main/26 2014/01/28 11:05:42 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   01/24/14 - Fortify: Prevent heap inspection of passwords by
 *                         avoiding using Strings
 *    abondala  09/04/13 - initialize collections
 *    abhineek  02/06/13 - fix for update employee's first & last name
 *    mkutiana  09/12/12 - Modifications to support Biometrics Quickwin -
 *                         support for multiple FP per employee
 *    cgreene   05/21/12 - XbranchMerge cgreene_bug-13951397 from
 *                         rgbustores_13.5x_generic
 *    cgreene   05/16/12 - arrange order of businessDay column to end of
 *                         primary key to improve performance since most
 *                         receipt lookups are done without the businessDay
 *    masahu    07/07/11 - FORTIFY FIX: The sensitive SQLs get logged
 *    abondala  04/11/11 - XbranchMerge abondala_bug11827952-salting_passwords
 *                         from main
 *    abondala  03/25/11 - merging
 *    abondala  03/23/11 - Implemented salting for the passwords
 *    blarsen   03/01/11 - Removing change from original biometric POC merge
 *                         which is no longer needed. The new
 *                         selectEmployeesForBiometrics() handles this case
 *                         more efficiently.
 *    hyin      02/28/11 - convert biometrics column to blob
 *    hyin      02/21/11 - fix read employee for biometrics method
 *    hyin      02/17/11 - add jdbc call to only get very few columns from
 *                         employee table for biometrics
 *    hyin      01/28/11 - change fingerprint column to varchar to accommodate
 *                         with derby limitation
 *    blarsen   01/18/11 - Integrated changes to BlobFactory.
 *    blarsen   05/26/10 - Added support for new fingerprint biometrics blob
 *                         column. Also changed selectEmployees() method to
 *                         support return all employees if no employee is
 *                         requested.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    abondala  01/29/09 - updated files related to hashing algorithm which can
 *                         be configured through properties file.
 *
 * ===========================================================================
 * $Log:
 *  6    360Commerce 1.5         2/26/2008 10:59:10 PM  Chengegowda Venkatesh
 *       Added the Employee Password Creation Date field to employee select
 *       method.
 *  5    360Commerce 1.4         10/12/2006 8:17:52 AM  Christian Greene Adding
 *        new functionality for PasswordPolicy.  Employee password will now be
 *        persisted as a byte[] in hexadecimal.  Updates include UI changes,
 *       persistence changes, and AppServer configuration changes.  A database
 *        rebuild with the new SQL scripts will be required.
 *  4    360Commerce 1.3         1/25/2006 4:11:15 PM   Brett J. Larsen merge
 *       7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *  3    360Commerce 1.2         3/31/2005 4:28:40 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:22:43 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:11:58 PM  Robert Pearse
 * $:
 *  4    .v700     1.2.1.0     11/16/2005 16:27:21    Jason L. DeLeau 4215: Get
 *       rid of redundant ArtsDatabaseifc class
 *  3    360Commerce1.2         3/31/2005 15:28:40     Robert Pearse
 *  2    360Commerce1.1         3/10/2005 10:22:43     Robert Pearse
 *  1    360Commerce1.0         2/11/2005 12:11:58     Robert Pearse
 * $
 * Revision 1.11  2004/08/05 23:13:24  cdb
 * 6582 Made integer a string to match column definition for sc_em (Employee Status Code)
 *
 * Revision 1.10  2004/08/05 21:19:56  cdb
 * @scr 6582 Removed invalid UPPER call on integer valued column.
 *
 * Revision 1.9  2004/07/31 15:29:39  epd
 * @scr 6641 made default access to methods protected
 *
 * Revision 1.8  2004/07/24 17:56:27  jriggins
 * @scr 6435 Added expired employee filtering
 *
 * Revision 1.7  2004/07/24 16:38:31  jriggins
 * @scr 6435 Added a filter for only active employees
 *
 * Revision 1.6  2004/02/19 23:36:46  jriggins
 * @scr 3782 this commit mainly deals with the database modifications needed for Enter New Password feature in Operator ID
 * Revision 1.5 2004/02/17 17:57:37 bwf @scr 0
 * Organize imports.
 *
 * Revision 1.4 2004/02/17 16:18:46 rhafernik @scr 0 log4j conversion
 *
 * Revision 1.3 2004/02/12 17:13:17 mcs Forcing head revision
 *
 * Revision 1.2 2004/02/11 23:25:23 bwf @scr 0 Organize imports.
 *
 * Revision 1.1.1.1 2004/02/11 01:04:27 cschellenger updating to pvcs
 * 360store-current
 *
 *
 *
 * Rev 1.2 Jan 26 2004 09:55:38 jriggins Refactored this class somewhat using
 * method overloading on selectEmployees() also added selectEmployeesByRole()
 * method Resolution for 3597: Employee 7.0 Updates
 *
 * Rev 1.1 20 Jan 2004 09:13:58 Tim Fritz Added code to try and set preferred
 * locale.
 *
 * Rev 1.0 Aug 29 2003 15:31:44 CSchellenger Initial revision.
 *
 * Rev 1.4 Apr 11 2003 12:55:50 baa remove deprecation for get/setName methods
 * in EmployeeIfc Resolution for POS SCR-2155: Deprecation warnings -
 * EmployeeIfc
 *
 * Rev 1.3 Mar 26 2003 15:56:50 sfl Use simpler way to apply makeSafeString
 * method Resolution for POS SCR-2072: Using an apostrophe in Employee Find
 * returns a database error
 *
 * Rev 1.2 Mar 26 2003 15:12:54 sfl When building query qualifiers, need to
 * apply makeSafeString mehtod to the employee first name, middle name, and
 * last name to handle the potential apostrophe or backslash type of characters
 * in the input name string. Resolution for POS SCR-2072: Using an apostrophe
 * in Employee Find returns a database error
 *
 * Rev 1.1 Feb 26 2003 11:30:24 bwf Database Internationalization Resolution
 * for 1866: I18n Database support
 *
 * Rev 1.0 Jun 03 2002 16:37:10 msg Initial revision.
 *
 * Rev 1.2 18 May 2002 17:14:06 sfl Using upper case in LIKE qualifier to
 * handle the matching search situations when database sever to be configured
 * either case-sensitive or case-insensitive. Resolution for POS SCR-1666:
 * Employee - Search by employee name cannot find existing employees
 *
 * Rev 1.1 Mar 18 2002 22:45:38 msg - updated copyright
 *
 * Rev 1.0 Mar 18 2002 12:05:36 msg Initial revision.
 *
 * Rev 1.2 02 Nov 2001 17:09:06 dlr Implementing employee status functionality
 * 1 is active 2 is inactive Resolution for Domain SCR-12: Implement employee
 * status functionality
 *
 * Rev 1.1 27 Oct 2001 08:33:38 mpm Merged employee changes from Virginia ABC
 * demonstration.
 *
 * Rev 1.0 Sep 20 2001 15:58:30 msg Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import oracle.retail.stores.common.data.JdbcUtilities;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.EmployeeTypeEnum;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;
import oracle.retail.stores.persistence.utility.DatabaseBlobHelperFactory;

import org.apache.log4j.Logger;

/**
 * This operation reads the employee table.
 *
 * @version $Revision: /main/26 $
 */
public abstract class JdbcReadEmployee extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 1397732073191155352L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadEmployee.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/26 $";

    /**
     * Class constructor.
     */
    public JdbcReadEmployee()
    {
    }

    /**
     * Selects employees based on name.
     *
     * @param dataConnection connection to the db
     * @param employee the employee information
     * @return Vector of EmployeIfc which match the supplied criteria
     * @exception DataException upon error
     */
    public Vector<EmployeeIfc> selectEmployees(
        JdbcDataConnection dataConnection,
        EmployeeIfc employee)
        throws DataException
    {
        // Call the overloaded method
        return selectEmployees(dataConnection, employee, (LocaleRequestor) null);
    }

    /**
     * Selects employees based on name.
     * <P>
     *
     * @param dataConnection
     *          connection to the db
     * @param employee
     *          the employee information
     * @param sqlLocale
     *          locale of the employee; can be null
     * @return Vector of EmployeIfc which match the supplied criteria
     * @exception DataException
     *              upon error
     * @deprecated deprecated as of 13.1 - locales are now specified using LocaleRequestor
     */
    public Vector<EmployeeIfc> selectEmployees(
        JdbcDataConnection dataConnection,
        EmployeeIfc employee,
        Locale sqlLocale)
        throws DataException
    {
        /*
         * Add Qualifier(s)...ripped from original selectEmployees() method.
         */
        SQLSelectStatement whereClause = new SQLSelectStatement();
        whereClause.addQualifier(
            "UPPER("
                + FIELD_EMPLOYEE_FIRST_NAME
                + ")"
                + " like "
                + getEmployeeFirstName(employee));
        whereClause.addQualifier(
            "UPPER("
                + FIELD_EMPLOYEE_LAST_NAME
                + ")"
                + " like "
                + getEmployeeLastName(employee));

        // Call the overloaded method
        return selectEmployees(dataConnection, whereClause, sqlLocale);
    }

    /**
     * Selects employees based on name.
     * <P>
     *
     * @param dataConnection
     *          connection to the db
     * @param employee
     *          the employee information
     * @param localeRequestor
     *          locale of the employee; can be null
     * @return Vector of EmployeIfc which match the supplied criteria
     * @exception DataException
     *              upon error
     */
    public Vector<EmployeeIfc> selectEmployees(
        JdbcDataConnection dataConnection,
        EmployeeIfc employee,
        LocaleRequestor localeRequestor)
        throws DataException
    {
        /*
         * Add Qualifier(s)...ripped from original selectEmployees() method.
         */
        SQLSelectStatement whereClause = new SQLSelectStatement();

        whereClause.addQualifier(
            "UPPER("
                + FIELD_EMPLOYEE_FIRST_NAME
                + ")"
                + " like "
                + getEmployeeFirstName(employee));
        whereClause.addQualifier(
            "UPPER("
                + FIELD_EMPLOYEE_LAST_NAME
                + ")"
                + " like "
                + getEmployeeLastName(employee));

        // Call the overloaded method
        return selectEmployees(dataConnection, whereClause, localeRequestor);
    }

    /**
     * Selects employees based on role.
     * <P>
     *
     * @param dataConnection
     *          connection to the db
     * @param role
     *          the role corresponding to the employees to retrieve
     * @return Vector of EmployeIfc which match the supplied criteria
     * @exception DataException
     *              upon error
     */
    protected Vector<EmployeeIfc> selectEmployeesByRole(
        JdbcDataConnection dataConnection,
        RoleIfc role)
        throws DataException
    {
        // Call the overloaded method
        return selectEmployeesByRole(dataConnection, role, (Locale) null);
    }

    /**
     * Selects employees based on role.
     * <P>
     *
     * @param dataConnection
     *          connection to the db
     * @param role
     *          the role corresponding to the employees to retrieve
     * @param sqlLocale
     *          locale of the employee; can be null
     * @return Vector of EmployeIfc which match the supplied criteria
     * @exception DataException
     *              upon error
     * @deprecated deprecated as of 13.1 - locales are now specified using LocaleRequestor
     */
    protected Vector<EmployeeIfc> selectEmployeesByRole(
        JdbcDataConnection dataConnection,
        RoleIfc role,
        Locale sqlLocale)
        throws DataException
    {
        /*
         * Add Qualifier(s)...ripped from original selectEmployees() method.
         */
        SQLSelectStatement whereClause = new SQLSelectStatement();
        whereClause.addQualifier(FIELD_EMPLOYEE_ROLE_ID, Integer.toString(role.getRoleID()));

        // Call the overloaded method
        return selectEmployees(dataConnection, whereClause, sqlLocale);
    }

    /**
     * Selects employees based on role.
     * <P>
     *
     * @param dataConnection
     *          connection to the db
     * @param role
     *          the role corresponding to the employees to retrieve
     * @param localeRequestor
     *          locale of the employee; can be null
     * @return Vector of EmployeIfc which match the supplied criteria
     * @exception DataException
     *              upon error
     */
    protected Vector<EmployeeIfc> selectEmployeesByRole(
        JdbcDataConnection dataConnection,
        RoleIfc role,
        LocaleRequestor localeRequestor)
        throws DataException
    {
        /*
         * Add Qualifier(s)...ripped from original selectEmployees() method.
         */
        SQLSelectStatement whereClause = new SQLSelectStatement();
        whereClause.addQualifier(FIELD_EMPLOYEE_ROLE_ID, Integer.toString(role.getRoleID()));

        // Call the overloaded method
        return selectEmployees(dataConnection, whereClause, localeRequestor);
    }


    /**
     * Selects employees based on the qualifier data in the SQLSelectStement
     * <P>
     *
     * @param dataConnection
     *          connection to the db
     * @param whereClause
     *          contains data by which to filter the employee list
     * @param sqlLocale
     *          locale of the employee; can be null
     * @return Vector of EmployeIfc which match the supplied criteria
     * @exception DataException
     *              upon error
     * @deprecated deprecated as of 13.1 - locales are now specified using LocaleRequestor
     */
    protected Vector<EmployeeIfc> selectEmployees(
        JdbcDataConnection dataConnection,
        SQLSelectStatement whereClause,
        Locale sqlLocale)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        /*
         * Define table
         */
        sql.setTable(TABLE_EMPLOYEE);
        /*
         * Add columns and their values
         */
        sql.addColumn(FIELD_EMPLOYEE_ID); //1
        sql.addColumn(FIELD_EMPLOYEE_ID_LOGIN); //2
        sql.addColumn(FIELD_EMPLOYEE_ID_ALT); //3
        sql.addColumn(FIELD_EMPLOYEE_ACCESS_PASSWORD); //4
        sql.addColumn(FIELD_EMPLOYEE_FIRST_NAME); //5
        sql.addColumn(FIELD_EMPLOYEE_MIDDLE_NAME); //6
        sql.addColumn(FIELD_EMPLOYEE_LAST_NAME); //7
        sql.addColumn(FIELD_EMPLOYEE_ROLE_ID); //8
        sql.addColumn(FIELD_EMPLOYEE_STATUS_CODE); //9
        sql.addColumn(FIELD_LOCALE); //10
        sql.addColumn(FIELD_EMPLOYEE_NUMBER_OF_DAYS_VALID); //11
        sql.addColumn(FIELD_EMPLOYEE_EXPIRATION_DATE); //12
        sql.addColumn(FIELD_EMPLOYEE_TYPE); //13
        sql.addColumn(FIELD_RETAIL_STORE_ID); //14
        sql.addColumn(FIELD_EMPLOYEE_NEW_PASSWORD_REQUIRED); //15
        sql.addColumn(FIELD_EMPLOYEE_PASSWORD_CREATED_TIMESTAMP); //16
        sql.addColumn(FIELD_EMPLOYEE_LAST_LOGIN_TIMESTAMP); //17
        sql.addColumn(FIELD_EMPLOYEE_PASSWORD_SALT); //18
        

        /*
         * Add qualifer data
         */
        if (whereClause != null)
            sql.setQualifierList(whereClause.getQualifierList());

        // Only active and unexpired employees
        sql.addQualifier(FIELD_EMPLOYEE_STATUS_CODE, inQuotes(String.valueOf(EmployeeIfc.LOGIN_STATUS_ACTIVE)));

        // Extract data from the result set.
        EmployeeIfc foundEmployee = null;
        PersonNameIfc employeeName = null;
        Vector<EmployeeIfc> employeeVector = new Vector<EmployeeIfc>(2);
        Vector roleIDVector = new Vector(2);
        try
        {
            dataConnection.execute(sql.getSQLString(), false);
            ResultSet rs = (ResultSet) dataConnection.getResult();
            EYSDate systemDate = DomainGateway.getFactory().getEYSDateInstance();
            while (rs.next())
            {
                // Filter out expired employees
                EYSDate expirationDate = getEYSDateFromString(rs, 12);
                if (expirationDate == null || systemDate.before(expirationDate))
                {
                    /*
                     * Grab the fields selected from the database
                     */
                    foundEmployee =
                        DomainGateway.getFactory().getEmployeeInstance();
                    employeeName =
                        DomainGateway.getFactory().getPersonNameInstance();
                    // set data elements for employee, numbers are the order in the
                    // columns listed above.
                    // If the number of ResultString elements exceeded the number
                    // of
                    // columns, an invalid column error will be returned.
                    foundEmployee.setEmployeeID(getSafeString(rs, 1));
                    foundEmployee.setLoginID(getSafeString(rs, 2));
                    foundEmployee.setAlternateID(getSafeString(rs, 3));
                    String hex = rs.getString(4);
                    foundEmployee.setPasswordBytes(JdbcUtilities.base64decode(hex));
                    employeeName.setFirstName(getSafeString(rs, 5));
                    employeeName.setMiddleName(getSafeString(rs, 6));
                    employeeName.setLastName(getSafeString(rs, 7));
                    int roleID = rs.getInt(8);
                    roleIDVector.addElement(Integer.valueOf(roleID));
                    foundEmployee.setLoginStatus(
                        Integer.parseInt(getSafeString(rs, 9)));

                    String language = getSafeString(rs, 10);
                    if (!Util.isEmpty(language))
                    {
                        try
                        {
                            foundEmployee.setPreferredLocale(
                                LocaleUtilities.getLocaleFromString(language));
                        }
                        catch (IllegalArgumentException e)
                        {
                            logger.warn(
                                "JdbcEmployeeLookupOperation.execute(): Employee preferredLocale is not valid");
                        }
                    }

                    foundEmployee.setDaysValid(rs.getInt(11));
                    foundEmployee.setExpirationDate(expirationDate);
                    foundEmployee.setType(
                        EmployeeTypeEnum.getEnumForDBVal(rs.getInt(13)));
                    foundEmployee.setStoreID(getSafeString(rs, 14));
                    foundEmployee.setPasswordChangeRequired(
                        getBooleanFromString(rs, 15));
                    foundEmployee.setPasswordCreationDate(timestampToDate(rs.getTimestamp(16)));
                    //foundEmployee.setFingerprintBiometrics(getFingerPrintsForEmployee(foundEmployee.getEmployeeID(),dataConnection));
                    foundEmployee.setLastLoginTime(timestampToDate(rs.getTimestamp(17)));
                    foundEmployee.setEmployeePasswordSalt(getSafeString(rs, 18));
                    foundEmployee.setPersonName(employeeName);
                    // check for training mode here
                    employeeVector.addElement(foundEmployee);
                }
            }

            rs.close();
            
            addFingerPrintsToEmployees(dataConnection, employeeVector);

            for (int i = 0; i < employeeVector.size(); i++)
            {
                EmployeeIfc emp = (EmployeeIfc) employeeVector.elementAt(i);
                Integer roleInt = (Integer) roleIDVector.elementAt(i);
                RoleIfc role = null;
                if (sqlLocale == null)
                    role =
                        JdbcEmployeeLookupOperation.getRole(
                            roleInt.intValue(),
                            dataConnection);
                else
                    role =
                        JdbcEmployeeLookupOperation.getRole(
                            roleInt.intValue(),
                            dataConnection,
                            sqlLocale);
                emp.setRole(role);
            }
        }
        catch (SQLException se)
        {
            logger.error("" + se + "");
            throw new DataException(
                DataException.SQL_ERROR,
                "SelectEmployees",
                se);
        }

        if (employeeVector.isEmpty())
        {
            logger.warn("No employees found");
            throw new DataException(
                DataException.NO_DATA,
                "No employees found");
        }
        return (employeeVector);
    }
    /**
     * Selects employees based on the qualifier data in the SQLSelectStement
     * <P>
     *
     * @param dataConnection
     *          connection to the db
     * @param whereClause
     *          contains data by which to filter the employee list
     * @param localeRequestor
     *          locale of the employee; can be null
     * @return Vector of EmployeIfc which match the supplied criteria
     * @exception DataException
     *              upon error
     */
    protected Vector<EmployeeIfc> selectEmployees(
        JdbcDataConnection dataConnection,
        SQLSelectStatement whereClause,
        LocaleRequestor localeRequestor)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        /*
         * Define table
         */
        sql.setTable(TABLE_EMPLOYEE);
        /*
         * Add columns and their values
         */
        sql.addColumn(FIELD_EMPLOYEE_ID); //1
        sql.addColumn(FIELD_EMPLOYEE_ID_LOGIN); //2
        sql.addColumn(FIELD_EMPLOYEE_ID_ALT); //3
        sql.addColumn(FIELD_EMPLOYEE_ACCESS_PASSWORD); //4
        sql.addColumn(FIELD_EMPLOYEE_FIRST_NAME); //5
        sql.addColumn(FIELD_EMPLOYEE_MIDDLE_NAME); //6
        sql.addColumn(FIELD_EMPLOYEE_LAST_NAME); //7
        sql.addColumn(FIELD_EMPLOYEE_ROLE_ID); //8
        sql.addColumn(FIELD_EMPLOYEE_STATUS_CODE); //9
        sql.addColumn(FIELD_LOCALE); //10
        sql.addColumn(FIELD_EMPLOYEE_NUMBER_OF_DAYS_VALID); //11
        sql.addColumn(FIELD_EMPLOYEE_EXPIRATION_DATE); //12
        sql.addColumn(FIELD_EMPLOYEE_TYPE); //13
        sql.addColumn(FIELD_RETAIL_STORE_ID); //14
        sql.addColumn(FIELD_EMPLOYEE_NEW_PASSWORD_REQUIRED); //15
        sql.addColumn(FIELD_EMPLOYEE_PASSWORD_CREATED_TIMESTAMP); //16
        sql.addColumn(FIELD_EMPLOYEE_LAST_LOGIN_TIMESTAMP); //17
        sql.addColumn(FIELD_EMPLOYEE_PASSWORD_SALT); //18

        /*
         * Add qualifer data
         */
        if (whereClause != null)
            sql.setQualifierList(whereClause.getQualifierList());

        // Only active and unexpired employees
        sql.addQualifier(FIELD_EMPLOYEE_STATUS_CODE, inQuotes(String.valueOf(EmployeeIfc.LOGIN_STATUS_ACTIVE)));

        // Extract data from the result set.
        EmployeeIfc foundEmployee = null;
        PersonNameIfc employeeName = null;
        Vector<EmployeeIfc> employeeVector = new Vector<EmployeeIfc>(2);
        Vector roleIDVector = new Vector(2);
        try
        {
            dataConnection.execute(sql.getSQLString(), false);
            ResultSet rs = (ResultSet) dataConnection.getResult();
            EYSDate systemDate = DomainGateway.getFactory().getEYSDateInstance();
            while (rs.next())
            {
                // Filter out expired employees
                EYSDate expirationDate = getEYSDateFromString(rs, 12);
                if (expirationDate == null || systemDate.before(expirationDate))
                {
                    /*
                     * Grab the fields selected from the database
                     */
                    foundEmployee =
                        DomainGateway.getFactory().getEmployeeInstance();
                    employeeName =
                        DomainGateway.getFactory().getPersonNameInstance();
                    // set data elements for employee, numbers are the order in the
                    // columns listed above.
                    // If the number of ResultString elements exceeded the number
                    // of
                    // columns, an invalid column error will be returned.
                    foundEmployee.setEmployeeID(getSafeString(rs, 1));
                    foundEmployee.setLoginID(getSafeString(rs, 2));
                    foundEmployee.setAlternateID(getSafeString(rs, 3));
                    String hex = rs.getString(4);
                    foundEmployee.setPasswordBytes(JdbcUtilities.base64decode(hex));
                    employeeName.setFirstName(getSafeString(rs, 5));
                    employeeName.setMiddleName(getSafeString(rs, 6));
                    employeeName.setLastName(getSafeString(rs, 7));
                    int roleID = rs.getInt(8);
                    roleIDVector.addElement(Integer.valueOf(roleID));
                    foundEmployee.setLoginStatus(
                        Integer.parseInt(getSafeString(rs, 9)));

                    String language = getSafeString(rs, 10);
                    if (!Util.isEmpty(language))
                    {
                        try
                        {
                            foundEmployee.setPreferredLocale(
                                LocaleUtilities.getLocaleFromString(language));
                        }
                        catch (IllegalArgumentException e)
                        {
                            logger.warn(
                                "JdbcEmployeeLookupOperation.execute(): Employee preferredLocale is not valid");
                        }
                    }

                    foundEmployee.setDaysValid(rs.getInt(11));
                    foundEmployee.setExpirationDate(expirationDate);
                    foundEmployee.setType(
                        EmployeeTypeEnum.getEnumForDBVal(rs.getInt(13)));
                    foundEmployee.setStoreID(getSafeString(rs, 14));
                    foundEmployee.setPasswordChangeRequired(
                        getBooleanFromString(rs, 15));
                    foundEmployee.setPasswordCreationDate(timestampToDate(rs.getTimestamp(16)));
                    //foundEmployee.setFingerprintBiometrics(getFingerPrintsForEmployee(foundEmployee.getEmployeeID(),dataConnection));
                    foundEmployee.setLastLoginTime(timestampToDate(rs.getTimestamp(17)));
                    foundEmployee.setPersonName(employeeName);
                    foundEmployee.setEmployeePasswordSalt(getSafeString(rs, 18));
                    // check for training mode here
                    employeeVector.addElement(foundEmployee);
                }
            }

            rs.close();
            addFingerPrintsToEmployees(dataConnection, employeeVector);

            for (int i = 0; i < employeeVector.size(); i++)
            {
                EmployeeIfc emp = (EmployeeIfc) employeeVector.elementAt(i);
                Integer roleInt = (Integer) roleIDVector.elementAt(i);
                RoleIfc role =  JdbcReadRoles.getRole(
                            roleInt.intValue(),
                            dataConnection,
                            localeRequestor);
                emp.setRole(role);
            }
        }
        catch (SQLException se)
        {
            logger.error("" + se + "");
            throw new DataException(
                DataException.SQL_ERROR,
                "SelectEmployees",
                se);
        }

        if (employeeVector.isEmpty())
        {
            logger.warn("No employees found");
            throw new DataException(
                DataException.NO_DATA,
                "No employees found");
        }
        return (employeeVector);
    }

    /**
     * Selects employees based on the qualifier data in the SQLSelectStement
     * This only returns emp_id, emp_loginid, fingerprint, last login
     * timestamp and expirationDate for each employee.
     * <P>
     *
     * @param dataConnection
     *          connection to the db
     * @param whereClause
     *          contains data by which to filter the employee list
     * @param localeRequestor
     *          locale of the employee; can be null
     * @return Vector of EmployeIfc which match the supplied criteria
     * @exception DataException
     *              upon error
     */

    protected Vector<EmployeeIfc> selectEmployeesForBiometrics(
        JdbcDataConnection dataConnection,
        LocaleRequestor localeRequestor)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        /*
         * Define table
         */
        sql.addTable(TABLE_EMPLOYEE, ALIAS_EMPLOYEE);
        sql.addTable(TABLE_BIOMETRIC_DATA, ALIAS_BIOMETRIC_DATA);
        
        /*
         * Add columns and their values
         */
        sql.addColumn(ALIAS_EMPLOYEE, FIELD_EMPLOYEE_ID); //1
        sql.addColumn(ALIAS_EMPLOYEE, FIELD_EMPLOYEE_ID_LOGIN); //2        
        sql.addColumn(ALIAS_EMPLOYEE, FIELD_EMPLOYEE_LAST_LOGIN_TIMESTAMP); //3
        sql.addColumn(ALIAS_EMPLOYEE, FIELD_EMPLOYEE_EXPIRATION_DATE); //4
        
        // add join qualifiers
        // join on employee IDs
        sql.addJoinQualifier(ALIAS_EMPLOYEE,
                FIELD_EMPLOYEE_ID,
                ALIAS_BIOMETRIC_DATA,
                FIELD_BIOMETRICDATA_EMPLOYEE_ID);
        
        /*
         * Add qualifer data
         */
        // Only active and unexpired employees
        sql.addQualifier(ALIAS_EMPLOYEE,FIELD_EMPLOYEE_STATUS_CODE, inQuotes(String.valueOf(EmployeeIfc.LOGIN_STATUS_ACTIVE)));
        
        List<String> groupingList = new ArrayList<String>();
        //group by  EM.ID_EM, EM.ID_LOGIN, EM.TS_LOGIN_LST, EM.DC_EXP_TMP 
        groupingList.add(ALIAS_EMPLOYEE + "." + FIELD_EMPLOYEE_ID);
        groupingList.add(ALIAS_EMPLOYEE + "." + FIELD_EMPLOYEE_ID_LOGIN);
        groupingList.add(ALIAS_EMPLOYEE + "." + FIELD_EMPLOYEE_LAST_LOGIN_TIMESTAMP);
        groupingList.add(ALIAS_EMPLOYEE + "." + FIELD_EMPLOYEE_EXPIRATION_DATE);
        
        sql.setGroupingList(groupingList);
        
        //sort in desc order on last login time column
        sql.addOrdering(ALIAS_EMPLOYEE, FIELD_EMPLOYEE_LAST_LOGIN_TIMESTAMP + " DESC ");

        // Extract data from the result set.
        EmployeeIfc foundEmployee = null;
        Vector<EmployeeIfc> employeeVector = new Vector<EmployeeIfc>(2);
        try
        {
            dataConnection.execute(sql.getSQLString(), false);
            ResultSet rs = (ResultSet) dataConnection.getResult();
            EYSDate systemDate = DomainGateway.getFactory().getEYSDateInstance();
            while (rs.next())
            {
                // Filter out expired employees
                EYSDate expirationDate = getEYSDateFromString(rs, 4);
                if (expirationDate == null || systemDate.before(expirationDate))
                {
                    foundEmployee = DomainGateway.getFactory().getEmployeeInstance();
                    foundEmployee.setEmployeeID(getSafeString(rs, 1));
                    foundEmployee.setLoginID(getSafeString(rs, 2));                    
                    foundEmployee.setLastLoginTime(timestampToDate(rs.getTimestamp(FIELD_EMPLOYEE_LAST_LOGIN_TIMESTAMP)));
                    employeeVector.addElement(foundEmployee);
                }
            }
            rs.close();
            
        }
        catch (SQLException se)
        {
            logger.error("" + se + "");
            throw new DataException(
                DataException.SQL_ERROR,
                "selectEmployeesForBiometrics",
                se);
        }
        
        if (employeeVector.isEmpty())
        {
            logger.warn("No employees found");
            throw new DataException(
                DataException.NO_DATA,
                "No employees found");
        }
        else
        {
            addFingerPrintsToEmployees(dataConnection, employeeVector);
        }
        
        return (employeeVector);
    }


    /**
    * Returns all the fingerprints for a specified employee
    *
    * @return List<byte[]> for a given employee ID
    * @param connection data connection interface object
    * @param String employeeID Employee ID for whom to get the fingerprint information
    * @exception DataException
    */
   static public List<byte[]> getFingerPrintsForEmployee(
       String employeeID,
       DataConnectionIfc connection
       )
       throws DataException
   {
       SQLSelectStatement sql = new SQLSelectStatement();
       List<byte[]> employeeFingerPrints= new ArrayList<byte[]>();
       /*
        * Define table
        */
       sql.setTable(TABLE_BIOMETRIC_DATA);
       /*
        * Add columns and their values
        */
       sql.addColumn(FIELD_BIOMETRICDATA_EMPLOYEE_ID);
       sql.addColumn(FIELD_BIOMETRICDATA_FINGERPRINT_BIOMETRICS);
       /*
        * Add Qualifiers
        */
       sql.addQualifier(FIELD_BIOMETRICDATA_EMPLOYEE_ID , makeSafeString(employeeID));
       try
       {
           connection.execute(sql.getSQLString(), true);
           ResultSet rs = (ResultSet) connection.getResult();
           while (rs.next())
           {
               byte[] fingerprintBiometrics = DatabaseBlobHelperFactory.getInstance().getDatabaseBlobHelper(
                   ((JdbcDataConnection)connection).getConnection()).loadBlob(rs, FIELD_BIOMETRICDATA_FINGERPRINT_BIOMETRICS);
               employeeFingerPrints.add(fingerprintBiometrics);
           }
       }
       catch (SQLException e)
       {
           ((JdbcDataConnection) connection).logSQLException(
               e,
               "Processing result set.");
           throw new DataException(
               DataException.SQL_ERROR,
               "An SQL Error occurred proccessing the result set from selecting an employee's Biometric Data in JdbcEmployeeLookupOperation.",
               e);
       }
       return employeeFingerPrints;
   }
   
   
   
   /**
   * Returns all the employees who have fingerprint data with their fingerprints
   *
   * @return Map<String,List<byte[]>> Map Containing employee Ids and thier fingerprints
   * @param connection data connection interface object
   * @exception DataException
   */
  public Map<String,List<byte[]>> getFingerPrintsForAllEmployees(DataConnectionIfc connection)
      throws DataException
  {
      SQLSelectStatement sql = new SQLSelectStatement();
      Map<String,List<byte[]>> employeeFingerPrints = new HashMap<String,List<byte[]>>(1);      
      List<byte[]> tempfingerPrints = null;
      /*
       * Define table
       */
      sql.setTable(TABLE_BIOMETRIC_DATA);
      /*
       * Add columns and their values
       */
      sql.addColumn(FIELD_BIOMETRICDATA_EMPLOYEE_ID);
      sql.addColumn(FIELD_BIOMETRICDATA_FINGERPRINT_BIOMETRICS);

      try
      {
          connection.execute(sql.getSQLString(), false);
          ResultSet rs = (ResultSet) connection.getResult();
          
          while (rs.next())
          {
              if (employeeFingerPrints.containsKey(getSafeString(rs, 1))){
                  tempfingerPrints = employeeFingerPrints.get(getSafeString(rs, 1));
              }
              else{
                  tempfingerPrints = new ArrayList<byte[]>();
                  employeeFingerPrints.put(getSafeString(rs, 1), tempfingerPrints);
              }              
              byte[] fingerprintBiometrics = DatabaseBlobHelperFactory.getInstance().getDatabaseBlobHelper(
                  ((JdbcDataConnection)connection).getConnection()).loadBlob(rs, FIELD_BIOMETRICDATA_FINGERPRINT_BIOMETRICS);
              tempfingerPrints.add(fingerprintBiometrics);
          }
      }
      catch (SQLException e)
      {
          ((JdbcDataConnection) connection).logSQLException(
              e,
              "Processing result set.");
          throw new DataException(
              DataException.SQL_ERROR,
              "An SQL Error occurred proccessing the result set from selecting an employee's Biometric Data in JdbcEmployeeLookupOperation.",
              e);
      }
      return employeeFingerPrints;
  }
  
  
  /**
  * Executes the Adds the Biometric Fingerprint Data to an employee Vector
  *
  * @return Vector The Employee Vector that held the employee
  * @param connection data connection interface object
  * @param employee Vector A vector containing employees to add fingerprints too.
  * @exception DataException
  */
 public Vector<EmployeeIfc> addFingerPrintsToEmployees(DataConnectionIfc connection, Vector<EmployeeIfc> employeeVector)
     throws DataException
 {
     Map<String,List<byte[]>> employeeFingerPrints = getFingerPrintsForAllEmployees(connection);
     for(EmployeeIfc emp : employeeVector)
     {
         emp.setFingerprintBiometrics(employeeFingerPrints.get(emp.getEmployeeID()));
     }
     return employeeVector;
 }
 

    
    /**
     * Returns the employee login id
     * <p>
     *
     * @param employee
     *          An employee
     * @return the employee login id
     */
    protected String getEmployeeLoginID(EmployeeIfc employee)
    {
        return ("'" + employee.getLoginID() + "'");
    }

    /**
     * Returns the employee id
     * <p>
     *
     * @param employee
     *          An employee
     * @return the employee id
     */
    protected String getEmployeeID(EmployeeIfc employee)
    {
        return ("'" + employee.getEmployeeID() + "'");
    }

    /**
     * Returns the employee alternate id
     * <p>
     *
     * @param employee
     *          An employee
     * @return the employee alternate id
     */
    protected String getEmployeeAlternateID(EmployeeIfc employee)
    {
        return ("'" + employee.getAlternateID() + "'");
    }

    /**
     * Returns the employee first name with a "%" appended since all reads of
     * name strings should include a wildcard.
     * <p>
     *
     * @param employee
     *          An employee
     * @return the employee first name with "%" appended
     */
    protected String getEmployeeFirstName(EmployeeIfc employee)
    {
        return (
            "UPPER("
                + makeSafeString(employee.getPersonName().getFirstName() + "%")
                + ")");
    }

    /**
     * Returns the employee middle name with a "%" appended since all reads of
     * name strings should include a wildcard
     * <p>
     *
     * @param employee
     *          An employee
     * @return the employee middle name with '%' appended
     */
    protected String getEmployeeMiddleName(EmployeeIfc employee)
    {
        return (makeSafeString(employee.getPersonName().getMiddleName() + "%"));
    }

    /**
     * Returns the employee last name with a "%" appended since all reads of
     * name strings should include a wildcard
     * <p>
     *
     * @param employee
     *          An employee
     * @return the employee last name with '%' appended
     */
    protected String getEmployeeLastName(EmployeeIfc employee)
    {
        return (
            "UPPER("
                + makeSafeString(employee.getPersonName().getLastName() + "%")
                + ")");
    }
    /**
     * Returns a string representation of this object.
     * <P>
     *
     * @param none
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        String strResult =
            new String(
                "Class:  JdbcReadEmployee (Revision "
                    + getRevisionNumber()
                    + ")"
                    + hashCode());

        // pass back result
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * <P>
     *
     * @param none
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

}
