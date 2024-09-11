/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/EmployeeTransaction.java /main/13 2013/10/17 15:50:16 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    rabhaw 10/17/13 - added property retreiveAllEmployees to get better
 *                      control on the
 *                      JdbcEmployeeLookupOperation.retreiveAllEmployees
 *                      propery.
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    glwang 12/01/08 - deprecated employee full name column
 *    ohorne 10/08/08 - deprecated methods per I18N Database Technical
 *                      Specification
 *    ohorne 10/07/08 - Deprecated unused classes
 *
 * ===========================================================================

     $Log:
      4    360Commerce 1.3         9/29/2006 12:45:09 PM  Rohit Sachdeva
           21237: Password Policy Service Persistence Updates
      3    360Commerce 1.2         3/31/2005 4:27:59 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:21:21 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:10:51 PM  Robert Pearse
     $
     Revision 1.7  2004/09/23 00:30:50  kmcbride
     @scr 7211: Inserting serialVersionUIDs in these Serializable classes

     Revision 1.6  2004/04/09 16:55:47  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.5  2004/02/17 17:57:37  bwf
     @scr 0 Organize imports.

     Revision 1.4  2004/02/17 16:18:47  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:13  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:26  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.1   Jan 26 2004 09:56:56   jriggins
 * Added support for select employee by role
 * Resolution for 3597: Employee 7.0 Updates
 *
 *    Rev 1.0   Aug 29 2003 15:30:00   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Feb 26 2003 11:42:46   bwf
 * Database Internationalization
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.0   Jun 03 2002 16:34:38   msg
 * Initial revision.
 *
 *    Rev 1.2   15 May 2002 07:57:56   dal
 * Changes made for BackOffice employee time maintenance
 * Resolution for Backoffice SCR-867: Time Maintenance Initial checkin
 *
 *    Rev 1.1   Mar 18 2002 22:45:06   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:04:52   msg
 * Initial revision.
 *
 *    Rev 1.1   28 Oct 2001 11:44:26   mpm
 * Added data operations for employee clock entries.
 *
 *    Rev 1.0   Sep 20 2001 15:58:46   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:35:00   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.domain.arts;
// java imports
import java.io.Serializable;
import java.util.Stack;
import java.util.Vector;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.security.PasswordPolicyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeClockEntryIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

//------------------------------------------------------------------------------
/**
    The EmployeeTransaction implements the employee lookup operations
**/
//------------------------------------------------------------------------------
public class EmployeeTransaction extends DataTransaction
                                 implements DataTransactionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 2898953258278865990L;

    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.EmployeeTransaction.class);

    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";
    /**
       The transactionName name links this transaction to a command within the
       DataScript.
    **/
    public static String findForLoginName = "EmployeeFindForLoginTransaction";
    /**
       The transactionName name links this transaction to a command within the
       DataScript.
    **/
    public static String findForUpdateName = "EmployeeFindForUpdateTransaction";
    /**
     The transactionName name links this transaction to a command within the
     DataScript.
     **/
    public static String findForUpdateRole = "EmployeeFindForUpdateTransaction";
    /**
       Holds the employee id, or number as String, name as PersonNameIfc
    **/
    protected Object lookupParm;
    /**
       The queryType indicates the type of query.
    **/
    protected int queryType;
    /**
       The employee is the employee used for a name search
    **/
    protected EmployeeIfc employee = null;
    /**
     * Used for employee search by role
     */
    private RoleIfc role;
    
    /**
     * If all employees needs to be retrieve.
     */
    private boolean retreiveAllEmployees = false;
    

    //---------------------------------------------------------------------
    /**
       DataCommand constructor.  Initializes dataOperations and
       dataConnectionPool.
    **/
    //---------------------------------------------------------------------
    public EmployeeTransaction()
    {
        super(findForLoginName);
    }

    //---------------------------------------------------------------------
    /**
       DataCommand constructor.  Initializes dataOperations and
       dataConnectionPool.
    **/
    //---------------------------------------------------------------------
    public EmployeeTransaction(String name)
    {
        super(name);
    }

    //---------------------------------------------------------------------
    /**
       Obtains an Employee given a login ID.
       <p>
       @param loginID The String lookup key.
       @exception DataException is thrown if the Employee cannot be found.
    **/
    //---------------------------------------------------------------------
    public EmployeeIfc getEmployee(String loginID) throws DataException
    {
        return (getEmployeeID(loginID));
    }

    //---------------------------------------------------------------------
    /**
       Select a list of Employees from the data store based on
       arbitrary key criteria.
       <p>
       @param  employee An Employee that contains first name and last name.

       @return  A Vector of Employees that match the criteria.
       @exception  DataException when an error occurs
       @deprecated As of release 13.1
    **/
    //---------------------------------------------------------------------
    public EmployeeIfc[] selectEmployees(EmployeeIfc employee) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "EmployeeTransaction.selectEmployees");

        EmployeeIfc[] retrievedEmployees = null;

        this.employee = employee;

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = new DataActionIfc()
        {
            //--------------------------------------------------------------
            /*
              The is the anonymous DataAction to read a collection of
              Employees that match some criteria.
            */
            //--------------------------------------------------------------
            public Serializable getDataObject()
            {
                return EmployeeTransaction.this.employee;
            }

            public String getDataOperationName()
            {
                return "SelectEmployees";
            }
        };

        setDataActions(dataActions);
        Vector empVector = (Vector)getDataManager().execute(this);
        retrievedEmployees = new EmployeeIfc[empVector.size()];
        empVector.copyInto(retrievedEmployees);

        if (logger.isDebugEnabled()) logger.debug(
                    "EmployeeTransaction.selectEmployees");

        return(retrievedEmployees);
    }

    //  ---------------------------------------------------------------------
    /**
       Select a list of Employees from the data store based on
       arbitrary key criteria.
       <p>
       @param  inquiry SearchInquiry.
        @return  A Vector of Employees that match the criteria.
       @exception  DataException when an error occurs
    **/
    //---------------------------------------------------------------------
    public EmployeeIfc[] selectEmployees(SearchCriteriaIfc inquiry) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "EmployeeTransaction.selectEmployees");
        EmployeeIfc[] retrievedEmployees = null;
        DataAction[] dataActions = new DataAction[1];
        dataActions[0] = new DataAction();
        dataActions[0].setDataObject(inquiry);
        dataActions[0].setDataOperationName("SelectEmployees");
        setDataActions(dataActions);
        Vector empVector = (Vector)getDataManager().execute(this);
        retrievedEmployees = new EmployeeIfc[empVector.size()];
        empVector.copyInto(retrievedEmployees);
        if (logger.isDebugEnabled()) logger.debug(
                    "EmployeeTransaction.selectEmployees");
        return(retrievedEmployees);
    }

    //---------------------------------------------------------------------
    /**
     Select a list of Employees from the data store based on the role specified
     <p>
     @param  role a RoleIfc by which the matching employees will be returned

     @return  A Vector of Employees that match the criteria.
     @exception  DataException when an error occurs
     **/
    //---------------------------------------------------------------------
    public Vector selectEmployeesByRole(RoleIfc role) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
        "EmployeeTransaction.selectEmployees");

        EmployeeIfc[] retrievedEmployees = null;

        this.role = role;

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = new DataActionIfc()
        {
            //--------------------------------------------------------------
            /*
             The is the anonymous DataAction to read a collection of
             Employees that match some criteria.
             */
            //--------------------------------------------------------------
            public Serializable getDataObject()
            {
                return EmployeeTransaction.this.role;
            }

            public String getDataOperationName()
            {
                return "SelectEmployees";
            }
        };

        setDataActions(dataActions);
        Vector empVector = (Vector)getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
        "EmployeeTransaction.selectEmployees");

        return(empVector);
    }

    //---------------------------------------------------------------------
    /**
       Obtains a Employee given an employee ID.
       <p>
       @param loginID The String lookup key.
       @exception DataException is thrown if the Employee cannot be found.
    **/
    //---------------------------------------------------------------------
    public EmployeeIfc getEmployeeID(String loginID) throws DataException
    {
        lookupParm = loginID;

        // creates an anynonmous DataActionIfc object.
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = new DataActionIfc()
        {
            public Serializable getDataObject()
            {
                return (String)EmployeeTransaction.this.lookupParm;
            }
            public String getDataOperationName()
            {
                // this name corresponds to an operation in the quarry datascript.xml
                return "employeelookup";
            }
        };

        setQueryType(QueryTypeIfc.LOGINID);

        setDataActions(dataActions);
        EmployeeIfc employee = (EmployeeIfc) getDataManager().execute(this);

        return(employee);
    }

    //---------------------------------------------------------------------
    /**
       Obtains a Employee given an employee ID.
       <p>
       @param loginID The String lookup key.
       @exception DataException is thrown if the Employee cannot be found.
    **/
    //---------------------------------------------------------------------
    public EmployeeIfc getEmployeeNumber(String number) throws DataException
    {
        lookupParm = number;

        // creates an anynonmous DataActionIfc object.
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = new DataActionIfc()
        {
            public Serializable getDataObject()
            {
                return (String)EmployeeTransaction.this.lookupParm;
            }
            public String getDataOperationName()
            {
                // this name corresponds to an operation in the quarry datascript.xml
                return "employeelookup";
            }
        };

        setQueryType(QueryTypeIfc.NUMBER);

        setDataActions(dataActions);
        EmployeeIfc employee = (EmployeeIfc) getDataManager().execute(this);

        return(employee);
    }

    //---------------------------------------------------------------------
    /**
       Obtains an Employee given a name.
       <p>
       @param name The String lookup key.
       @exception DataException is thrown if the Employee cannot be found.
    **/
    //---------------------------------------------------------------------
    public EmployeeIfc getEmployeeName(PersonNameIfc name) throws DataException
    {
        lookupParm = name;

        // creates an anynonmous DataActionIfc object.
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = new DataActionIfc()
        {
            public Serializable getDataObject()
            {
                return (PersonNameIfc)EmployeeTransaction.this.lookupParm;
            }
            public String getDataOperationName()
            {
                // this name corresponds to an operation in the quarry datascript.xml
                return "employeelookup";
            }
        };
        setQueryType(QueryTypeIfc.NAME);

        setDataActions(dataActions);
        EmployeeIfc employee = (EmployeeIfc) getDataManager().execute(this);

        return(employee);
    }

    //---------------------------------------------------------------------
    /**
     Obtains an Employee given a name.
     <p>
     @param name The String lookup key.
     @exception DataException is thrown if the Employee cannot be found.
     **/
    //---------------------------------------------------------------------
    public EmployeeIfc getEmployeesByRole(RoleIfc role) throws DataException
    {
        lookupParm = Integer.toString(role.getRoleID());

        // creates an anynonmous DataActionIfc object.
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = new DataActionIfc()
        {
            public Serializable getDataObject()
            {
                return (String)EmployeeTransaction.this.lookupParm;
            }
            public String getDataOperationName()
            {
                // this name corresponds to an operation in the quarry datascript.xml
                return "employeelookup";
            }
        };
        setQueryType(QueryTypeIfc.ROLE);

        setDataActions(dataActions);
        EmployeeIfc employee = (EmployeeIfc) getDataManager().execute(this);

        return(employee);
    }



    //---------------------------------------------------------------------
    /**
       Retrieves latest employee clock entry for a given employee and store.
       @param storeID store identifier
       @param employeeLoginID employee login identifier
       @return latest employee clock entry for given employee and store
       @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public EmployeeClockEntryIfc readEmployeeLastClockEntry
      (String storeID,
       String employeeLoginID) throws DataException
    {                                   // begin readEmployeeLastClockEntry()
        if (logger.isDebugEnabled()) logger.debug(
                     "EmployeeTransaction.readEmployeeLastClockEntry");

        // prime search key
        EmployeeClockEntryIfc searchEntry =
          DomainGateway.getFactory().getEmployeeClockEntryInstance();
        EmployeeIfc searchEmployee =
          DomainGateway.getFactory().getEmployeeInstance();
        searchEntry.setStoreID(storeID);
        searchEmployee.setLoginID(employeeLoginID);
        searchEntry.setEmployee(searchEmployee);

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("ReadEmployeeLastClockEntry");
        da.setDataObject(searchEntry);
        dataActions[0] = da;
        setDataActions(dataActions);

        // execute data request
        EmployeeClockEntryIfc clockEntry =
          (EmployeeClockEntryIfc) getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "EmployeeTransaction.readEmployeeLastClockEntry");

        return(clockEntry);
    }                                   // end readEmployeeLastClockEntry()

    //---------------------------------------------------------------------
    /**
        Get all this employee's clock entries from the DB.
        @param String store ID
        @param String employee login ID
        @return Stack the clock entries
        @exception DataException
    **/
    //---------------------------------------------------------------------
    public Stack readEmployeeClockEntries(String storeID, String employeeLoginID)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "EmployeeTransaction.readEmployeeClockEntries");

        // prime search key
        EmployeeClockEntryIfc searchEntry =
          DomainGateway.getFactory().getEmployeeClockEntryInstance();
        EmployeeIfc searchEmployee =
          DomainGateway.getFactory().getEmployeeInstance();
        searchEntry.setStoreID(storeID);
        searchEmployee.setLoginID(employeeLoginID);
        searchEntry.setEmployee(searchEmployee);

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("ReadEmployeeClockEntries");
        da.setDataObject(searchEntry);
        dataActions[0] = da;
        setDataActions(dataActions);

        // execute data request
        Stack entries = (Stack) getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "EmployeeTransaction.readEmployeeClockEntries");

        return(entries);
    }

    //---------------------------------------------------------------------
    /**
       Retrieves queryType. <P>
       @return int see QuerryTypeIfc.
    **/
    //---------------------------------------------------------------------
    public int getQueryType()
    {
        return(queryType);
    };


    //---------------------------------------------------------------------
    /**
       Sets queryTypoe. <P>
       @return int see QuerryTypeIfc.
    **/
    //---------------------------------------------------------------------
    public void setQueryType(int value)
    {
        queryType = value;
    }


    //---------------------------------------------------------------------
    /**
       Retrieve the password policy and its criteria
       @return  PasswordPolicyIfc password policy reference
       @exception  DataException when an error occurs
    **/
    //---------------------------------------------------------------------
    public PasswordPolicyIfc readPasswordPolicy() throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "ReadPasswordPolicy.readPasswordPolicy starts");


        // set data actions and execute

        DataAction da = new DataAction();
        da.setDataOperationName("ReadPasswordPolicy");

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = da;

        setDataActions(dataActions);
        PasswordPolicyIfc passwordPolicy = (PasswordPolicyIfc) getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "ReadPasswordPolicy.readPasswordPolicy exits");

        return(passwordPolicy);
    }

//  ---------------------------------------------------------------------
    /**
       Retrieve password history for the employee
       @param employee reference to employee
       @return  EmployeeIfc employee reference
       @exception  DataException when an error occurs
    **/
    //---------------------------------------------------------------------
    public EmployeeIfc readPasswordHistory(EmployeeIfc employee) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "ReadPasswordHistory.readPasswordHistory starts");


        // set data actions and execute
        DataAction da = new DataAction();
        da.setDataObject(employee);
        da.setDataOperationName("ReadPasswordHistory");

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = da;

        setDataActions(dataActions);
        EmployeeIfc employeeWithCurrentPasswordHistoy = (EmployeeIfc) getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "ReadPasswordHistory.readPasswordHistory exits");

        return(employeeWithCurrentPasswordHistoy);
    }

    /**
     * @return retriveAllEmployees
     */
    public boolean isRetreiveAllEmployees()
    {
        return retreiveAllEmployees;
    }

    /**
     * Set the flag if all employees needs to retrieve
     * 
     * @param retreiveAllEmployees
     */
    public void setRetreiveAllEmployees(boolean retreiveAllEmployees)
    {
        this.retreiveAllEmployees = retreiveAllEmployees;
    }

    //---------------------------------------------------------------------
    /**
       Returns the revision number of this class.
       <P>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }

    //---------------------------------------------------------------------
    /**
       Returns the string representation of this object.
       <P>
       @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: EmployeeTransaction (Revision "
                                      + getRevisionNumber() + ") @"
                                      + hashCode());


        strResult += "\nlookupParm = " + lookupParm + "\n";

        strResult += "\nqueryType =" + queryType + "\n";


        if (employee !=null)
        {

            strResult += "\nemployee = \n";
            strResult +=  employee + "\n";
        }
        else
        {
            strResult += "\nemployee = null\n";
        }
        return(strResult);
    }
} // end class EmployeeTransaction

