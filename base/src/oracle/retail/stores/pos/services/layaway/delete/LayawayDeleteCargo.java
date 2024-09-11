/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/delete/LayawayDeleteCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:14 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         5/4/2006 5:11:50 PM    Brendan W. Farrell
 *         Remove inventory.
 *    3    360Commerce 1.2         3/31/2005 4:28:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:02 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:16 PM  Robert Pearse   
 *
 *   Revision 1.6.2.1  2004/10/15 18:50:30  kmcbride
 *   Merging in trunk changes that occurred during branching activity
 *
 *   Revision 1.7  2004/10/11 18:01:12  mweis
 *   @scr 7012 Rename get/setSetItemLocationIndex to get/setItemLocationIndex
 *
 *   Revision 1.6  2004/09/21 20:29:40  mweis
 *   @scr 7012 Enable correct inventory accounting for kits w.r.t. Layaways
 *
 *   Revision 1.5  2004/08/23 16:15:57  cdb
 *   @scr 4204 Removed tab characters
 *
 *   Revision 1.4  2004/06/29 22:03:31  aachinfiev
 *   Merge the changes for inventory & POS integration
 *
 *   Revision 1.3.2.1  2004/06/07 16:27:07  aachinfiev
 *   Added ability to prompt for inventory location as part of inventory & pos
 *   integration requirements.
 *
 *   Revision 1.3  2004/02/12 16:50:48  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   13 Jan 2004 14:57:58   aschenk
 * Defect fixes for 3572.
 * 
 *    Rev 1.0   Aug 29 2003 16:00:30   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:21:02   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:34:52   msg
 * Initial revision.
 * 
 *    Rev 1.2   16 Jan 2002 17:16:28   jbp
 * Deprecated access employee methods.
 * Resolution for POS SCR-638: Manager Override not working for Layaway Delete
 *
 *    Rev 1.0   Sep 21 2001 11:21:10   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.delete;

// java imports
import java.lang.reflect.Field;

import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;
import oracle.retail.stores.pos.services.layaway.LayawayCargoIfc;
//--------------------------------------------------------------------------
/**
    This is the cargo object for the NoSale service.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LayawayDeleteCargo extends LayawayCargo
                                implements LayawayCargoIfc
{
    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     false if no override is requested, true is override is needed
    **/
    protected boolean securityOverrideFlag = false;

    /**
        employee granting Security override
    **/
    protected EmployeeIfc securityOverrideEmployee;

    /**
        employee attempting Security override
    **/
    protected EmployeeIfc securityOverrideRequestEmployee;

    /**
        employee attempting Access
    **/
    protected EmployeeIfc accessEmployee;

    /**
        Security override Return Letter
    **/

    protected String securityOverrideReturnLetter;
    
    /**
        functon ID (default must be set to LAYAWAY_DELETE for first checkAccessSite)
    **/
    
    protected int functionID = RoleFunctionIfc.LAYAWAY_DELETE;

    //--------------------------------------------------------------------------
    /**
        Returns the securityOverrideFlag boolean. <P>
        @return The securityOverrideFlag boolean.
    **/
    //----------------------------------------------------------------------
    public boolean getSecurityOverrideFlag()
    {                                   // begin getSecurityOverrideFlag()
        return securityOverrideFlag;
    }                                   // end getSecurityOverrideFlag()

    //----------------------------------------------------------------------
    /**
        Sets the securityOverrideFlag boolean. <P>
        @param  value  The ssecurityOverrideFlag boolean.
    **/
    //----------------------------------------------------------------------
    public void setSecurityOverrideFlag(boolean value)
    {                                   // begin setSecurityOverrideFlag()
        securityOverrideFlag = value;
                                        // end setSecurityOverrideFlag()
    }
    //----------------------------------------------------------------------
    /**
        Returns the securityOverrideEmployee object. <P>
        @return The securityOverrideEmployee object.
    **/
    //----------------------------------------------------------------------
    public EmployeeIfc getSecurityOverrideEmployee()
    {                                   // begin getSecurityOverrideEmployee()
        return securityOverrideEmployee;
    }                                   // end getSecurityOverrideEmployee()

    //----------------------------------------------------------------------
    /**
        Sets the security override employee object. <P>
        @param  value  The security override employee object.
    **/
    //----------------------------------------------------------------------
    public void setSecurityOverrideEmployee(EmployeeIfc value)
    {                                   // begin setSecurityOverrideEmployee()
        securityOverrideEmployee = value;
    }                                   // end setSecurityOverrideEmployee()
    //----------------------------------------------------------------------
    /**
        Returns the securityOverrideRequestEmployee object. <P>
        @return The securityOverrideRequestEmployee object.
    **/
    //----------------------------------------------------------------------
    public EmployeeIfc getSecurityOverrideRequestEmployee()
    {                                   // begin getSecurityOverrideRequestEmployee()
        return securityOverrideRequestEmployee;
    }                                   // end getSecurityOverrideRequestEmployee()

    //----------------------------------------------------------------------
    /**
        Sets the securityOverrideRequestEmployee object. <P>
        @param  value  securityOverrideRequestEmployee object.
    **/
    //----------------------------------------------------------------------
    public void setSecurityOverrideRequestEmployee(EmployeeIfc value)
    {                                   // begin setSecurityOverrideRequestEmployee()
        securityOverrideRequestEmployee = value;
    }                                   // end setSecurityOverrideRequestEmployee()
    //----------------------------------------------------------------------
    /**
        The access employee returned by this cargo is the currently
        logged on cashier or an Override Security Employee
        <P>
        @return the void
        @deprecated Deprecated in release 5.0.0. Obsolete with new security implementation.
    **/
    //----------------------------------------------------------------------
    public void setAccessEmployee(EmployeeIfc value)
    {
        accessEmployee = value;
    }
    //----------------------------------------------------------------------
    /**
        The access employee returned by this cargo is the currently
        logged on cashier or an Override Security Employee
        <P>
        @return the EmployeeIfc value
        @deprecated Deprecated in release 5.0.0. Obsolete with new security implementation.
    **/
    //----------------------------------------------------------------------
    public EmployeeIfc getAccessEmployee()
    {
        return accessEmployee;
    }

    //----------------------------------------------------------------------
    /**
        The securityOverrideReturnLetter returned by this cargo is to indecated
        where the security override will return
        <P>
        @return the void
    **/
    //----------------------------------------------------------------------
    public void setSecurityOverrideReturnLetter(String value)
    {
        securityOverrideReturnLetter = value;
    }
    //----------------------------------------------------------------------
    /**
        The securityOverrideReturnLetter returned by this cargo is to indecated
        where the security override will return
        <P>
        @return the String value
    **/
    //----------------------------------------------------------------------
    public String getSecurityOverrideReturnLetter()
    {
        return securityOverrideReturnLetter;
    }

    //----------------------------------------------------------------------
    /**
        Returns the function ID whose access is to be checked.
        @return int Role Function ID
    **/
    //----------------------------------------------------------------------
    public int getAccessFunctionID()
    {
        return functionID;
    }

    //----------------------------------------------------------------------
    /**
        Returns the function ID whose access is to be checked.
        @return int Role Function ID
    **/
    //----------------------------------------------------------------------

    public void setAccessFunctionID(int id)
    {
        functionID = id;
    }
    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object.
        <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        // verbose flag
        boolean bVerbose = false;
        // result string
        String strResult = new String("Class:  NoSaleCargo (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // if verbose mode, do inspection gig
        if (bVerbose)
        {                               // begin verbose mode
            // theClass will ascend through the inheritance hierarchy
            Class theClass = getClass();
            // type of the field currently being examined
            Class fieldType = null;
            // name of the field currently being examined
            String fieldName = "";
            // value of the field currently being examined
            Object fieldValue = null;

            // Ascend through the class hierarchy, capturing field information
            while (theClass != null)
            {                           // begin loop through fields
                // fields contains all noninherited field information
                Field[] fields = theClass.getDeclaredFields();

                // Go through each field, capturing information
                for (int i = 0; i < fields.length; i++)
                {
                    fieldType = fields[i].getType();
                    fieldName = fields[i].getName();

                    // get the field's value, if possible
                    try
                    {
                        fieldValue = fields[i].get(this);
                    }
                    // if the value can't be gotten, say so
                    catch (IllegalAccessException ex)
                    {
                        fieldValue = "*no access*";
                    }

                    // If it is a "simple" field, use the value
                    if (Util.isSimpleClass(fieldType))
                    {
                        strResult += "\n\t" + fieldName + ":\t" + fieldValue;
                    }       // if simple
                    // If it is a null value, say so
                    else if (fieldValue == null)
                    {
                        strResult += "\n\t" + fieldName + ":\t(null)";
                    }
                    // Otherwise, use <type<hashCode>
                    else
                    {
                        strResult += "\n\t" + fieldName + ":\t" +
                                     fieldType.getName() + "@" +
                                     fieldValue.hashCode();
                    }
                }   // for each field
                theClass = theClass.getSuperclass();
            }                           // end loop through fields
        }                               // end verbose mode

        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
