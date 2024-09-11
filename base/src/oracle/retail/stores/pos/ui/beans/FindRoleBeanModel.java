/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/FindRoleBeanModel.java /main/16 2011/12/05 12:16:23 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    tzgarba   11/05/08 - Merged with tip
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:28:11 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:21:43 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:11:06 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/04/09 13:59:07  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;
// foundation imports
import java.util.Locale;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;

//--------------------------------------------------------------------------
/** 
    This is the bean model that is used by the FindRoleBean. <P>
    @see oracle.retail.stores.pos.ui.beans.FindRoleBean
    @version $KW=; $Ver=; $EKW;
**/
//--------------------------------------------------------------------------
public class FindRoleBeanModel  extends POSBaseBeanModel
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$KW=; $Ver; $EKW;";

    /**
        role titles
    **/        
    protected String[] roleTitles = null;
    
    /**
        role selected index
    **/        
    protected int roleSelectedIndex = 0;

    //----------------------------------------------------------------------------
    /**
       FindRoleBeanModel constructor.
    **/
    //----------------------------------------------------------------------------    
    public FindRoleBeanModel()
    {
    }

    //----------------------------------------------------------------------------
    /**
        Gets the roleTitles property  value.
        @return String[] of the roleTitles property value.
        @see #setRoleTitles
    **/
    //----------------------------------------------------------------------------
    public String[] getRoleTitles() 
    {
        return roleTitles;
    }

    //----------------------------------------------------------------------------
    /**
        Sets the roleTitles property value.
        @param roles array of roles.
        @param roleArraySize size of the role array.
        @see #getRoleTitles
    **/
    //----------------------------------------------------------------------------
    public void setRoleTitles(RoleIfc[] roles, int roleArraySize) 
    {
        // create a new array with the correct size for the new roles list
        roleTitles = new String[roleArraySize];
        
        // get the user's locale
        Locale userLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

        // loop through the roles array and
        // add the titles only into the new roleTitles array
        for (int i = 0; i < roleArraySize; i++)
        {
            roleTitles[i] = roles[i].getTitle(userLocale);            
        }
    }

    //----------------------------------------------------------------------------
    /**
        Gets the role index selected property value.
        @return int representing the role index property value.
        @see #setRoleSelectedIndex
    **/
    //----------------------------------------------------------------------------
    public int getRoleSelectedIndex() 
    {
        return roleSelectedIndex;
    }

    //----------------------------------------------------------------------------
    /**
        Sets the role title selected index property value.
        @param roleIndex int value for the role index property.
        @see #getRoleSelectedIndex
    */
    //----------------------------------------------------------------------------
    public void setRoleSelectedIndex(int roleIndex) 
    {
        roleSelectedIndex = roleIndex;
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
        String strResult = new String("Class: FindRoleBeanModel (Revision "
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
