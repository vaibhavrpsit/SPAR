/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/EmployeeWriteTransaction.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:06 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         10/24/2006 9:37:06 AM  Rohit Sachdeva
 *         21237: Login Updates to Handle Impacts of Password Policy
 *    5    360Commerce 1.4         10/20/2006 11:51:10 AM Rohit Sachdeva
 *         21237: Password Policy Service Save Employee and Password History
 *    4    360Commerce 1.3         9/29/2006 12:49:30 PM  Rohit Sachdeva
 *         21237: Password Policy Service Persistence Updates 
 *    3    360Commerce 1.2         3/31/2005 4:27:59 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:21 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:51 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/09/23 00:30:50  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.6  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:39  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:50  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:13  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:27  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Jan 28 2004 15:53:38   jriggins
 * Code review followup/rework
 * Resolution for 3597: Employee 7.0 Updates
 * 
 *    Rev 1.1   Dec 22 2003 16:24:54   jriggins
 * Added generateEmployeeID()
 * Resolution for 3597: Employee 7.0 Updates
 * 
 *    Rev 1.0   Aug 29 2003 15:30:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:34:40   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:45:06   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:04:54   msg
 * Initial revision.
 * 
 *    Rev 1.2   28 Oct 2001 17:51:36   mpm
 * Added transaction name to facilitate queueing time clock entries.
 *
 *    Rev 1.1   28 Oct 2001 11:44:26   mpm
 * Added data operations for employee clock entries.
 *
 *    Rev 1.0   Sep 20 2001 15:58:42   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:35:00   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;
// java imports
import java.io.Serializable;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.employee.EmployeeClockEntryIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.EmployeeTypeEnum;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

//------------------------------------------------------------------------------
/**
    The EmployeeWriteTransaction implements the employee lookup operations
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class EmployeeWriteTransaction extends DataTransaction
                                 implements DataTransactionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 7145569805773653630L;

    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.EmployeeWriteTransaction.class);

    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       The name that links this transaction to a command within the
       DataScript.
    **/
    public static String transactionName="EmployeeWriteTransaction";

    /**
        name for writing time clock entries
    **/
    public static String timeClockTransactionName = "EmployeeTimeClockDataTransaction";
    /**
       The loginID is the id of the employee for whom the system is searching
    **/
    protected String loginID;

    /**
       The  name indicates that an employee is to be searched for by name
    **/
    protected EmployeeIfc employee;

    /**
       The  name indicates that an employee is to be searched for by name
    **/
    protected String name;

    /**
       The queryType indicates the type of query.
    **/
    protected String queryType;

    //---------------------------------------------------------------------
    /**
       DataCommand constructor.  Initializes dataOperations and
       dataConnectionPool.
    **/
    //---------------------------------------------------------------------
    public EmployeeWriteTransaction()
    {
        super(transactionName);
    }

    //---------------------------------------------------------------------
    /**
       DataCommand constructor.  Initializes dataOperations and
       dataConnectionPool.
       @param name transaction name
    **/
    //---------------------------------------------------------------------
    public EmployeeWriteTransaction(String name)
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
    public void updateEmployee(EmployeeIfc employee) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "EmployeeWriteTransaction.updateEmployee");

        this.employee = employee;

        // creates an anynonmous DataActionIfc object.
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = new DataActionIfc()
        {
            public Serializable getDataObject()
            {
                return EmployeeWriteTransaction.this.employee;
            }
            public String getDataOperationName()
            {
                // this name corresponds to an operation in the quarry datascript.xml
                return "UpdateEmployee";
            }
        };

        setDataActions(dataActions);
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "EmployeeWriteTransaction.updateEmployee");
    }
    

   //---------------------------------------------------------------------
    /**
      * This updates the number of Failed Password Attempts.
      * It only updates the attempts column. This was made for Performance reasons
      * so that we don't update all attributes when employee makes for example 5 invalid
      * password attempts.
       <p>
       @param employee reference to employee.
       @exception DataException is thrown if the Employee cannot be found.
    **/
    //---------------------------------------------------------------------
    public void updateEmployeeNumberFailedAttempts(EmployeeIfc employee) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "EmployeeWriteTransaction.updateEmployeeNumberFailedAttempts starts");

        this.employee = employee;

        // creates an anynonmous DataActionIfc object.
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = new DataActionIfc()
        {
            public Serializable getDataObject()
            {
                return EmployeeWriteTransaction.this.employee;
            }
            public String getDataOperationName()
            {
                // this name corresponds to an operation in the quarry datascript.xml
                return "UpdateEmployeeNumberFailedAttempts";
            }
        };

        setDataActions(dataActions);
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "EmployeeWriteTransaction.updateEmployeeNumberFailedAttempts starts exits");
    }
    
    
    //---------------------------------------------------------------------
    /**
       Updates Employee and Inserts Employee Password History
       <p>
       @param employee reference to employee.
       @exception DataException is thrown if the Employee cannot be found.
    **/
    //---------------------------------------------------------------------
    public void saveEmployeeAndPasswordHistory(EmployeeIfc employee) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "EmployeeWriteTransaction.saveEmployeeAndPasswordHistory starts");

        this.employee = employee;

        DataActionIfc[] dataActions = new DataActionIfc[2];
        dataActions[0] = createDataAction(this.employee,
                                           "UpdateEmployee");
        dataActions[1] = createDataAction(this.employee, 
                                          "SavePasswordHistory");

        setDataActions(dataActions);
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "EmployeeWriteTransaction.saveEmployeeAndPasswordHistory exits");
    }

    //---------------------------------------------------------------------
    /**
       Obtains an Employee given a login ID.
       <p>
       @param loginID The String lookup key.
       @exception DataException is thrown if the Employee cannot be found.
    **/
    //---------------------------------------------------------------------
    public void insertEmployee(EmployeeIfc employee) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "EmployeeWriteTransaction.insertEmployee");

        this.employee = employee;

        // creates an anynonmous DataActionIfc object.
        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = new DataActionIfc()
        {
            public Serializable getDataObject()
            {
                return EmployeeWriteTransaction.this.employee;
            }
            public String getDataOperationName()
            {
                // this name corresponds to an operation in the quarry datascript.xml
                return "insertEmployee";
            }
        };

        setDataActions(dataActions);
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "EmployeeWriteTransaction.insertEmployee");
    }

    //---------------------------------------------------------------------
    /**
       Inserts an employee clock entry.
       @param clockEntry clock entry to be inserted
       @exception  DataException when an error occurs.
    **/
    //---------------------------------------------------------------------
    public void insertEmployeeClockEntry(EmployeeClockEntryIfc clockEntry)
       throws DataException
    {                                   // begin insertEmployeeClockEntry()
        if (logger.isDebugEnabled()) logger.debug(
                     "EmployeeWriteTransaction.insertEmployeeClockEntry");

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("InsertEmployeeClockEntry");
        da.setDataObject(clockEntry);
        dataActions[0] = da;
        setDataActions(dataActions);

        // insert the entry
        getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "EmployeeWriteTransaction.insertEmployeeClockEntry");
    }                                   // end insertEmployeeClockEntry()

    //---------------------------------------------------------------------
    /**
       Generates a unique employee ID
       @param employeeType type of employee
       @exception  DataException when an error occurs.
       @return Integer representing a new employee ID
    **/
    //---------------------------------------------------------------------
    public Integer generateEmployeeID(EmployeeTypeEnum employeeType)
       throws DataException
    {                                   // begin generateEmployeeID()
        if (logger.isDebugEnabled()) logger.debug(
                     "EmployeeWriteTransaction.generateEmployeeID");

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        da.setDataOperationName("GenerateEmployeeID");
        da.setDataObject(employeeType);
        dataActions[0] = da;
        setDataActions(dataActions);

        // generate the ID
        Integer employeeID = (Integer)getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "EmployeeWriteTransaction.generateEmployeeID");
        
        return employeeID;
    }                                   // end generateEmployeeID()
    
//  ---------------------------------------------------------------------
    /**
        Creates a data transaction.
        @param object the serialized object to be used in the data operation.
        @param name the name of the data action and operation.
        @return the new data action.
    **/
    //---------------------------------------------------------------------
    protected DataAction createDataAction(Serializable object, String name)
    {
        DataAction dataAction = new DataAction();
        dataAction.setDataObject(object);
        dataAction.setDataOperationName(name);
        return dataAction;
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
        String strResult = new String("Class: EmployeeWriteTransaction (Revision "
                                      + getRevisionNumber() + ") @"
                                      + hashCode());
        return(strResult);
    }

} // end class EmployeeWriteTransaction
