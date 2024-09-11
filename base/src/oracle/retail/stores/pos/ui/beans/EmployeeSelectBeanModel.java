/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EmployeeSelectBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:54 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:27:58 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:21:21 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:51 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.Vector;

import oracle.retail.stores.domain.employee.EmployeeIfc;

//----------------------------------------------------------------------------
/**
    This class is a model that contains a list of Employees that was
    querried from the DB.
    @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
    @deprecated as of release 5.0.0
**/
//----------------------------------------------------------------------------
public class EmployeeSelectBeanModel extends POSBaseBeanModel
{

    /**
        Revision number
    */
    public static String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:2; $EKW;";

    /**
        Employee information that was provided to the query
    */
    EmployeeIfc fieldQueryEmployee = null;
    /**
        The list of employees that resulted from the list
    */
    Vector fieldMatchlist = new Vector();
    /**
        The index in the list of the employee that was selected.
    */
    int fieldSelectedEmployee = -1;

    //----------------------------------------------------------------------------
    /**
        Get the value of the QueryEmployee field
        @return the value of QueryEmployee
    **/
    //----------------------------------------------------------------------------
    public EmployeeIfc getQueryEmployee()
    {
        return fieldQueryEmployee;
    }
    //----------------------------------------------------------------------------
    /**
        Get the value of the Matchlist field
        @return the value of Matchlist
    **/
    //----------------------------------------------------------------------------
    public Vector getMatchlist()
    {
        return fieldMatchlist;
    }
    //----------------------------------------------------------------------------
    /**
        Get the value of the SelectedEmployee field
        @return the value of SelectedEmployee
    **/
    //----------------------------------------------------------------------------
    public int getSelectedEmployee()
    {
        return fieldSelectedEmployee;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the QueryEmployee field
        @param queryEmployee the value to be set for QueryEmployee
    **/
    //----------------------------------------------------------------------------
    public void setQueryEmployee(EmployeeIfc queryEmployee)
    {
        fieldQueryEmployee = queryEmployee;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the Matchlist field
        @param matchlist the value to be set for Matchlist
    **/
    //----------------------------------------------------------------------------
    public void setMatchlist(Vector matchlist)
    {
        fieldMatchlist = matchlist;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the SelectedEmployee field
        @param selectedEmployee the value to be set for SelectedEmployee
    **/
    //----------------------------------------------------------------------------
    public void setSelectedEmployee(int selectedEmployee)
    {
        fieldSelectedEmployee = selectedEmployee;
    }
    //----------------------------------------------------------------------------
    /**
        Converts to a string representing the data in this Object
        @returns string representing the data in this Object
    **/
    //----------------------------------------------------------------------------
    public String toString()
    {
        StringBuffer buff = new StringBuffer();

        buff.append("Class: EmployeeSelectBeanModel Revision: " + revisionNumber + "\n");
        buff.append("QueryEmployee [" + fieldQueryEmployee + "]\n");
        buff.append("Matchlist [" + fieldMatchlist + "]\n");
        buff.append("SelectedEmployee [" + fieldSelectedEmployee + "]\n");

        return(buff.toString());
    }
}
