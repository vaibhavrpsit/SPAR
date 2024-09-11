/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EditRoleBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:44 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:27:53 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:21:13 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:44 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.foundation.utility.Util;

//--------------------------------------------------------------------------
/** 
    This is the bean model that is used by the EditRoleBean. <P>
    @see oracle.retail.stores.pos.ui.beans.EditRoleBean
    @version $KW=; $Ver=; $EKW;
**/
//--------------------------------------------------------------------------
public class EditRoleBeanModel extends POSBaseBeanModel
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$KW=; $Ver; $EKW;";

    /**
        text field role name
    **/
    protected String roleName = new String();

    //----------------------------------------------------------------------------
    /**
        EditRoleBeanModel constructor comment.
    **/
    //----------------------------------------------------------------------------    
    public EditRoleBeanModel() 
    {
        super();
    }

    //----------------------------------------------------------------------------
    /**
        Gets the role name property (java.lang.String) value.
        @return String the role name property value.
        @see #setRoleName
    **/
    //----------------------------------------------------------------------------
    public String getRoleName() 
    {
        return roleName;
    }

    //----------------------------------------------------------------------------
    /**
        Sets the roleName property (java.lang.String) value.
        @param value the role name property value.
        @see #getRoleName
    **/
    //----------------------------------------------------------------------------
    public void setRoleName(String value) 
    {
        roleName = value;
    }
    
    //---------------------------------------------------------------------
    /**
        Method to default display string function. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                
        // result string
        String strResult = new String("Class: EditRoleBeanModel (Revision "
            + getRevisionNumber() + ")" + hashCode());
            
        // pass back result
        return(strResult);
    }                                  

    //---------------------------------------------------------------------
    /**
        Retrieves the Team Connection revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                  
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }                                  
}
