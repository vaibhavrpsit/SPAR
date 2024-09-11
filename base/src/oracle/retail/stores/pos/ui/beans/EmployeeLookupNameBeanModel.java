/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EmployeeLookupNameBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:45 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:27:57 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:21:19 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:49 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;


//----------------------------------------------------------------------------
/**
    This is model for the Looking up the employee by name.
    @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
**/
//----------------------------------------------------------------------------
public class EmployeeLookupNameBeanModel extends POSBaseBeanModel
{
    /**
        Revision number
    */
    public static String revisionNumber = "$KW=@(#); $Ver=;  $EKW;";
    /**
        First Name data
    */
    String fieldFirstName = "";
    /**
        Last Name data
    */
    String fieldLastName = "";

    //----------------------------------------------------------------------------
    /**
        Get the value of the FirstName field
        @return the value of FirstName
    **/
    //----------------------------------------------------------------------------
    public String getFirstName()
    {
        return fieldFirstName;
    }
    //----------------------------------------------------------------------------
    /**
        Get the value of the LastName field
        @return the value of LastName
    **/
    //----------------------------------------------------------------------------
    public String getLastName()
    {
        return fieldLastName;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the FirstName field
        @param firstName the value to be set for FirstName
    **/
    //----------------------------------------------------------------------------
    public void setFirstName(String firstName)
    {
        fieldFirstName = firstName;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the LastName field
        @param lastName the value to be set for LastName
    **/
    //----------------------------------------------------------------------------
    public void setLastName(String lastName)
    {
        fieldLastName = lastName;
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

        buff.append("Class: EmployeeLookupNameBeanModel Revision: " + revisionNumber + "\n");
        buff.append("FirstName [" + fieldFirstName + "]\n");
        buff.append("LastName [" + fieldLastName + "]\n");

        return(buff.toString());
    }
}
