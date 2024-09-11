/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/manager/ManagerCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:11 mszekely Exp $
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
 * 3    360Commerce 1.2         3/31/2005 4:29:00 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:23:26 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:12:32 PM  Robert Pearse   
 *
 *Revision 1.4  2004/09/27 22:32:05  bwf
 *@scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *Revision 1.3  2004/02/12 16:50:58  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:51:37  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:01:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:18:40   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:36:18   msg
 * Initial revision.
 * 
 *    Rev 1.2   Mar 06 2002 09:53:44   mpm
 * Added TillStatus screen.
 * Resolution for POS SCR-1513: Add Till Status screen
 *
 *    Rev 1.1   16 Nov 2001 13:08:04   pdd
 * Deprecated obsolete Security Override code.
 * Resolution for POS SCR-291: Device/DB Status updates
 *
 *    Rev 1.0   Sep 21 2001 11:23:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:58   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.manager;

// Foundation imports
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.utility.Util;

//------------------------------------------------------------------------------
/**
    Manager service data.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class ManagerCargo extends AbstractFinancialCargo
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
     false if no override is requested, true is override is needed
        @deprecated in 5.0. Rendered obsolete by the new Security Override design.
    **/
    protected boolean securityOverrideFlag = false;
    /**
        employee granting Security override
        @deprecated in 5.0. Rendered obsolete by the new Security Override design.
    **/
    protected EmployeeIfc securityOverrideEmployee;
    /**
        employee attempting Security override
        @deprecated in 5.0. Rendered obsolete by the new Security Override design.
    **/
    protected EmployeeIfc securityOverrideRequestEmployee;
    /**
        employee attempting Access
        @deprecated in 5.0. Rendered obsolete by the new Security Override design.
    **/
    protected EmployeeIfc accessEmployee;
    /**
        Security override Return Letter
        @deprecated in 5.0. Rendered obsolete by the new Security Override design.
    **/
    protected String securityOverrideReturnLetter;
    /**
        register status
    **/
    protected RegisterIfc registerStatus = null;

    //--------------------------------------------------------------------------
    /**
        Returns the securityOverrideFlag boolean. <P>
        @return The securityOverrideFlag boolean.
        @deprecated in 5.0. Rendered obsolete by the new Security Override design.
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
        @deprecated in 5.0. Rendered obsolete by the new Security Override design.
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
        @deprecated in 5.0. Rendered obsolete by the new Security Override design.
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
        @deprecated in 5.0. Rendered obsolete by the new Security Override design.
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
        @deprecated in 5.0. Rendered obsolete by the new Security Override design.
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
        @deprecated in 5.0. Rendered obsolete by the new Security Override design.
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
        @deprecated in 5.0. Rendered obsolete by the new Security Override design.
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
        @deprecated in 5.0. Rendered obsolete by the new Security Override design.
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
        @deprecated in 5.0. Rendered obsolete by the new Security Override design.
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
        @deprecated in 5.0. Rendered obsolete by the new Security Override design.
    **/
    //----------------------------------------------------------------------
    public String getSecurityOverrideReturnLetter()
    {
        return securityOverrideReturnLetter;
    }

    //---------------------------------------------------------------------
    /**
       Sets register status object.
       @param value register status object
    **/
    //---------------------------------------------------------------------
    public void setRegisterStatus(RegisterIfc value)
    {                                   // begin setRegisterStatus()
        registerStatus = value;
    }                                   // end setRegisterStatus()

    //---------------------------------------------------------------------
    /**
       Gets register status object.
       @return register status object
    **/
    //---------------------------------------------------------------------
    public RegisterIfc getRegisterStatus()
    {                                   // begin getRegisterStatus()
        return(registerStatus);
    }                                   // end getRegisterStatus()

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        return(Util.parseRevisionNumber(revisionNumber));
    }                                  // end getRevisionNumber()
}
