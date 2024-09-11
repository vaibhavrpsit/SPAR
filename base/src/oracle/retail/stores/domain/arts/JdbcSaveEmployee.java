/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveEmployee.java /main/20 2014/01/28 11:05:42 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   01/24/14 - Fortify: Prevent heap inspection of passwords by
 *                         avoiding using Strings
 *    masahu    07/07/11 - FORTIFY FIX: The sensitive SQLs get logged
 *    cgreene   10/05/10 - merge tmp employee sequence into regular employee
 *                         sequence to avoid primary key clash
 *    npoola    08/25/10 - passed the connection object to the
 *                         IdentifierService getNextID method to use right
 *                         connection
 *    rsnayak   08/11/10 - Employee Id generation Fix
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  12   360Commerce 1.11        11/2/2006 10:34:33 AM  Christian Greene Switch
 *        to from ISOBean to oracle.retail.stores.domain.data.DAOFactory
 *  11   360Commerce 1.10        10/20/2006 11:36:27 AM Rohit Sachdeva  21237:
 *       Password Policy Service Persistence Updates
 *  10   360Commerce 1.9         10/16/2006 5:41:34 PM  Christian Greene switch
 *        employee dao lookup to Spring
 *  9    360Commerce 1.8         10/12/2006 8:17:52 AM  Christian Greene Adding
 *        new functionality for PasswordPolicy.  Employee password will now be
 *        persisted as a byte[] in hexadecimal.  Updates include UI changes,
 *       persistence changes, and AppServer configuration changes.  A database
 *        rebuild with the new SQL scripts will be required.
 *  8    360Commerce 1.7         9/29/2006 12:15:38 PM  Rohit Sachdeva  21237:
 *       Password Policy Service Persistence Updates
 *  7    360Commerce 1.6         9/28/2006 4:03:02 PM   Christian Greene
 *       Oracle does not want the String '2010-12-25' inserted into a Date
 *       column.  Use the to_date('2010-12-25', 'yyyy-MM-dd') function
 *       instead.
 *  6    360Commerce 1.5         6/1/2006 12:28:42 PM   Brendan W. Farrell
 *       Update comments.
 *  5    360Commerce 1.4         5/31/2006 5:04:01 PM   Brendan W. Farrell Move
 *        from party to id gen.
 *
 *  4    360Commerce 1.3         1/25/2006 4:11:21 PM   Brett J. Larsen merge
 *       7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *  3    360Commerce 1.2         3/31/2005 4:28:43 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:22:48 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:12:02 PM  Robert Pearse
 * $:
 *  4    .v700     1.2.2.0     11/16/2005 16:27:38    Jason L. DeLeau 4215: Get
 *       rid of redundant ArtsDatabaseifc class
 *  3    360Commerce1.2         3/31/2005 15:28:43     Robert Pearse
 *  2    360Commerce1.1         3/10/2005 10:22:48     Robert Pearse
 *  1    360Commerce1.0         2/11/2005 12:12:02     Robert Pearse
 * $
 * Revision 1.8  2004/07/26 23:47:06  jriggins
 * @scr 5759 Added logic to display a dialog in the event that a user is attempting to add a standard employee using an ID that is in the temporary employee range.
 *
 * Revision 1.7  2004/02/26 20:12:44  jriggins
 * @scr 3782 created setExpirationDateColumn() so that nulls can be placed in the column
 *
 * Revision 1.6  2004/02/19 23:36:46  jriggins
 * @scr 3782 this commit mainly deals with the database modifications needed for Enter New Password feature in Operator ID
 * Revision 1.5 2004/02/17 17:57:38 bwf @scr 0
 * Organize imports.
 *
 * Revision 1.4 2004/02/17 16:18:47 rhafernik @scr 0 log4j conversion
 *
 * Revision 1.3 2004/02/12 17:13:18 mcs Forcing head revision
 *
 * Revision 1.2 2004/02/11 23:25:26 bwf @scr 0 Organize imports.
 *
 * Revision 1.1.1.1 2004/02/11 01:04:28 cschellenger updating to pvcs
 * 360store-current
 *
 *
 *
 * Rev 1.3 Jan 28 2004 15:53:40 jriggins Code review followup/rework Resolution
 * for 3597: Employee 7.0 Updates
 *
 * Rev 1.2 Jan 15 2004 14:09:02 mrm added makeSafeString for store ID
 * Resolution for 3713: Change Employee Status Confirm = No, does not return
 * the status to the previous state.
 *
 * Rev 1.1 Dec 22 2003 16:24:30 jriggins Added logic for generating IDs for
 * temporary employees. Resolution for 3597: Employee 7.0 Updates
 *
 * Rev 1.0 Aug 29 2003 15:32:40 CSchellenger Initial revision.
 *
 * Rev 1.2 Jan 30 2003 16:31:42 baa add employee locale preferences to flat
 * files Resolution for POS SCR-1843: Multilanguage support
 *
 * Rev 1.1 Dec 18 2002 16:58:48 baa Add employee/customer language preferrence
 * support Resolution for POS SCR-1843: Multilanguage support
 *
 * Rev 1.0 Jun 03 2002 16:39:32 msg Initial revision.
 *
 * Rev 1.2 16 May 2002 15:23:52 adc Db2 fixes Resolution for Domain SCR-50: db2
 * port fixes
 *
 * Rev 1.1 Mar 18 2002 22:48:14 msg - updated copyright
 *
 * Rev 1.0 Mar 18 2002 12:08:06 msg Initial revision.
 *
 * Rev 1.3 05 Nov 2001 11:02:22 dlr Employee Active/Inactive Functionality
 * Resolution for Domain SCR-12: Implement employee status functionality
 *
 * Rev 1.2 02 Nov 2001 17:09:06 dlr Implementing employee status functionality
 * 1 is active 2 is inactive Resolution for Domain SCR-12: Implement employee
 * status functionality
 *
 * Rev 1.1 27 Oct 2001 08:33:40 mpm Merged employee changes from Virginia ABC
 * demonstration.
 *
 * Rev 1.0 Sep 20 2001 15:59:22 msg Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import oracle.retail.stores.common.identifier.IdentifierConstantsIfc;
import oracle.retail.stores.common.identifier.IdentifierServiceIfc;
import oracle.retail.stores.common.identifier.IdentifierServiceLocator;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.arts.dao.EmployeeDAOIfc;
import oracle.retail.stores.domain.data.DAOFactory;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.EmployeeTypeEnum;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * This operation performs inserts and updates into the employee table.
 *
 * @version $Revision: /main/20 $
 */
public abstract class JdbcSaveEmployee
    extends JdbcDataOperation
    implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 2009188271535195929L;

    /** The logger to which log messages will be sent. */
    private static final Logger logger = Logger.getLogger(JdbcSaveEmployee.class);

    public static final String PRTY_ID_EMPLOYEE = "1";

    /** Set new employee status code to A in employee table. */
    public static final String ACTIVE_EMPLOYEE_STATUS_CODE = "\'1\'";

    /** Set new employee status code to A in employee table. */
    public static final String INACTIVE_EMPLOYEE_STATUS_CODE = "\'2\'";

    /**
     * Delegates to DAO instance to insert the Employee object into the database.
     *
     * @param dataConnection connection to the db
     * @param employee the employee information
     * @return true if successful
     * @exception DataException upon error
     */
    public boolean insertEmployee(
        JdbcDataConnection dataConnection,
        EmployeeIfc employee)
        throws DataException
    {
        Connection connection = dataConnection.getConnection();

        // Commented this part of code. Employee Id generation is handled in
        // JDBCGenerateEmployeeId

        // Generate a new employee ID only for non-temporary employees
        // Temp. employees will already have an ID.
        /*
         * int employeeID = -1; String empID = employee.getEmployeeID(); if
         * (!(EmployeeTypeEnum.TEMPORARY.equals(employee.getType()))) {
         * employeeID = generateEmployeeID(dataConnection, employee.getType());
         * empID = String.valueOf(employeeID); employee.setEmployeeID(empID); }
         */

        //load from Spring
        EmployeeDAOIfc employeeDAO = (EmployeeDAOIfc)DAOFactory.createBean(EmployeeDAOIfc.EMPLOYEE_DAO_BEAN_KEY);
        employeeDAO.setConnection(connection);
        return employeeDAO.insert(employee);
    }

    /**
     * Updates a record in the employee table.
     *
     * @param dataConnection connection to the db
     * @param employee the employee information
     * @return true if successful
     * @exception DataException upon error
     */
    public boolean updateEmployee(
        JdbcDataConnection dataConnection,
        EmployeeIfc employee)
        throws DataException
    {
        Connection connection = dataConnection.getConnection();

        //load from Spring
        EmployeeDAOIfc employeeDAO = (EmployeeDAOIfc)DAOFactory.createBean(EmployeeDAOIfc.EMPLOYEE_DAO_BEAN_KEY);
        employeeDAO.setConnection(connection);
        return employeeDAO.update(employee);
    }


    /**
     * Updates a record in the employee table.
     *
     * @param dataConnection connection to the db
     * @param employee the employee information
     * @return true if successful
     * @exception DataException  upon error
     */
    public boolean updateEmployeeNumberFailedAttempts(
        JdbcDataConnection dataConnection,
        EmployeeIfc employee)
        throws DataException
    {
        boolean returnCode = false;

        SQLUpdateStatement sql = new SQLUpdateStatement();

        /*
         * Define table
         */
        sql.setTable(TABLE_EMPLOYEE);
        sql.addColumn(
        		FIELD_EMPLOYEE_NUMBER_FAILED_PASSWORDS,
        		getNumberFailedPasswords(employee));

        /*
         * Add Qualifiers
         */
        sql.addQualifier(
            FIELD_EMPLOYEE_ID_LOGIN + " = " + makeSafeString(employee.getLoginID()));
        sql.addQualifier(FIELD_EMPLOYEE_STATUS_CODE, getEmployeeLoginStatus(employee));

        dataConnection.execute(sql.getSQLString(), false);

        if (0 < dataConnection.getUpdateCount())
        {
            returnCode = true;
        }

        return (returnCode);
    }

    /**
     * Returns the employee login id
     *
     * @param employee
     *          An employee
     * @return the employee login id
     * @deprecated as of release 6.0 replaced by makeSafeString()
     */
    protected String getEmployeeLoginID(EmployeeIfc employee)
    {
        return ("'" + employee.getLoginID() + "'");
    }

    /**
     * Returns the employee id
     *
     * @param employee
     *          An employee
     * @return the employee id
     */
    protected String getRoleID(EmployeeIfc employee)
    {
        int id = employee.getRole().getRoleID();
        return (Integer.toString(id));
    }

    /**
     * Returns the employee role id
     *
     * @param employee
     *          An employee
     * @return the role id
     * @deprecated as of release 6.0 replaced by makeSafeString()
     */
    protected String getEmployeeID(EmployeeIfc employee)
    {
        return ("'" + employee.getEmployeeID() + "'");
    }

    /**
     * Returns the employee role id
     *
     * @param employee
     *          An employee
     * @return the role id
     */
    protected String getEmployeeLoginStatus(EmployeeIfc employee)
    {
        if (employee.getLoginStatus() == EmployeeIfc.LOGIN_STATUS_INACTIVE)
        {
            return (INACTIVE_EMPLOYEE_STATUS_CODE);
        }

        return (ACTIVE_EMPLOYEE_STATUS_CODE);
    }

    /**
     * Returns the employee alternate id
     *
     * @param employee
     *          An employee
     * @return the employee alternate id
     * @deprecated as of release 6.0 replaced by makeSafeString()
     */
    protected String getEmployeeAlternateID(EmployeeIfc employee)
    {
        return ("'" + employee.getAlternateID() + "'");
    }

    /**
     * Returns the employee first name
     *
     * @param employee
     *          An employee
     * @return the employee first name
     * @deprecated as of release 6.0 replaced by makeSafeString()
     */
    protected String getEmployeeFirstName(EmployeeIfc employee)
    {
        return ("'" + employee.getPersonName().getFirstName() + "'");
    }

    /**
     * Returns the employee middle name
     *
     * @param employee
     *          An employee
     * @return the employee middle name
     * @deprecated as of release 6.0 replaced by makeSafeString()
     */
    protected String getEmployeeMiddleName(EmployeeIfc employee)
    {
        return ("'" + employee.getPersonName().getMiddleName() + "'");
    }

    /**
     * Returns the employee last name
     *
     * @param employee
     *          An employee
     * @return the employee last name
     * @deprecated as of release 6.0 replaced by makeSafeString()
     */
    protected String getEmployeeLastName(EmployeeIfc employee)
    {
        return ("'" + employee.getPersonName().getLastName() + "'");
    }

    /**
     * Returns the employee full name made up of 1st, middle, last.
     *
     * @param employee
     *          An employee
     * @return the employee full name
     */
    protected String getEmployeeFullName(EmployeeIfc employee)
    {
        String employeeMiddleName = employee.getPersonName().getMiddleName();
        if ((employeeMiddleName != null) && (!employeeMiddleName.equals("")))
        {
            return makeSafeString(employee.getPersonName().getFirstMiddleLastName());
        }

        return makeSafeString(employee.getPersonName().getFirstLastName());
    }

    /**
     * Generates a unique party id. Uses {@link IdentifierServiceIfc}.
     *
     * @param dataConnection
     *          The connection to the data source
     * @return the party_id
     * @exception DataException
     *              upon error
     */
    protected int generateEmployeeID(DataConnectionIfc dataConnection)
        throws DataException
    {
        return generateEmployeeID(dataConnection, EmployeeTypeEnum.STANDARD);
    }

    /**
     * Generates a unique party id. Uses {@link IdentifierServiceIfc}.
     *
     * @param dataConnection
     *          The connection to the data source
     * @return the party_id
     * @exception DataException
     *              upon error
     */
    protected int generateEmployeeID(
        DataConnectionIfc dataConnection,
        EmployeeTypeEnum employeeType)
        throws DataException
    {
        return IdentifierServiceLocator.getIdentifierService().getNextID(((JdbcDataConnection) dataConnection).getConnection(), IdentifierConstantsIfc.COUNTER_EMPLOYEE);
    }

    /**
     * When the application reaches the maximum generated ID value, it attempts
     * to find an unused value between 0 and the maximum.
     *
     * @param dataConnection
     *          The connection to the data source
     * @return the party_id
     * @exception DataException
     *              upon error
     */
    protected int findFirstAvailableID(DataConnectionIfc dataConnection)
        throws DataException
    {
        ArrayList<String> idList = new ArrayList<String>(EmployeeIfc.MAXIMUM_TEMP_EMPOYEE_ID + 10);
        int employeeID = 1;
        int expectedID = 1;
        SQLSelectStatement sql = new SQLSelectStatement();

        // Define table, columns and qualifiers
        sql.setTable(TABLE_EMPLOYEE);
        sql.addColumn(FIELD_EMPLOYEE_ID);
        sql.addQualifier(
            FIELD_EMPLOYEE_ID
                + " < "
                + Integer.toString(EmployeeIfc.MAXIMUM_TEMP_EMPOYEE_ID + 1));

        // Execute the SQL
        try
        {
            dataConnection.execute(sql.getSQLString(), false);
            ResultSet rs = (ResultSet) dataConnection.getResult();
            while (rs.next())
            {
                idList.add(getSafeString(rs, 1));
            }

            // Sort the list in interger seqence.
            Collections.sort(idList);

            for (int i = 0; i < idList.size(); i++)
            {
                //int currentID = ((Integer) idList.get(i)).intValue();
                int currentID = (Integer.valueOf(idList.get(i))).intValue();

                if (currentID >= EmployeeIfc.MAXIMUM_TEMP_EMPOYEE_ID)
                {
                    String errorStr =
                        "All Employee IDs between 0 and"
                            + EmployeeIfc.MAXIMUM_TEMP_EMPOYEE_ID
                            + "have been allocated.";

                    logger.error(errorStr);
                    throw new DataException(
                        DataException.KEY_VIOLATION_ERROR,
                        errorStr);
                }

                if (expectedID != currentID)
                {
                    employeeID = expectedID;
                    break;
                }

                expectedID++;
            }
        }
        catch (SQLException se)
        {
            logger.error(se.toString());
            throw new DataException(
                DataException.SQL_ERROR,
                "JdbcSaveEmployee.SelectEmployees()",
                se);
        }

        return employeeID;
    }

    /**
     * Retrieved Password Attempts
     *
     * @param employee reference to employee
     * @return String Failed Password Attempts
     */
    public String getNumberFailedPasswords(EmployeeIfc employee)
    {
        return(String.valueOf(employee.getNumberFailedPasswords()));
    }

}
