/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/role/EditToSetAccessLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:06 mszekely Exp $
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
 |    4    360Commerce 1.3         6/18/2008 12:38:48 PM  Charles D. Baker CR
 |         32107 - Updated to persist Operator in cargo for the edit access
 |         flow. Also updated to persist StoreStatus in cargo for add access
 |         flow for consistency with edit access flow. Code reviewed by Jack
 |         Swan.
 |    3    360Commerce 1.2         3/31/2005 4:27:54 PM   Robert Pearse   
 |    2    360Commerce 1.1         3/10/2005 10:21:13 AM  Robert Pearse   
 |    1    360Commerce 1.0         2/11/2005 12:10:44 PM  Robert Pearse   
 |   $
 |   Revision 1.5  2004/09/23 00:07:15  kmcbride
 |   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 |
 |   Revision 1.4  2004/04/09 16:56:01  cdb
 |   @scr 4302 Removed double semicolon warnings.
 |
 |   Revision 1.3  2004/02/12 16:48:56  mcs
 |   Forcing head revision
 |
 |   Revision 1.2  2004/02/11 20:54:19  rhafernik
 |   @scr 0 Log4J conversion and code cleanup
 |
 |   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 |   updating to pvcs 360store-current
 |
 |    Rev 1.0   Aug 29 2003 15:53:22   CSchellenger
 | Initial revision.
 | 
 |    Rev 1.0   Apr 29 2002 15:37:30   msg
 | Initial revision.
 | 
 |    Rev 1.1   Mar 18 2002 23:06:48   msg
 | - updated copyright
 | 
 |    Rev 1.0   Mar 18 2002 11:20:48   msg
 | Initial revision.
 | 
 |    Rev 1.0   Sep 21 2001 11:12:38   msg
 | Initial revision.
 | 
 |    Rev 1.1   Sep 17 2001 13:13:04   msg
 | header update
 |
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.role;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.admin.role.roleedit.RoleEditCargo;
import oracle.retail.stores.pos.services.admin.role.setaccess.SetAccessCargo;

//------------------------------------------------------------------------------
/**
    This shuttle will transfer the specific Role from the
    RoleEdit cargo to the shuttle, and then from the shuttle
    to the SetAccess cargo.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class EditToSetAccessLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 9155317943047870103L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.admin.role.EditToSetAccessLaunchShuttle.class);

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       constant for the shuttle name
    **/
    public static final String SHUTTLENAME = "EditToSetAccessLaunchShuttle";

    /**
       role object
    **/
    public RoleIfc role = null;

    /**
       old role title
    **/
    protected String oldRoleTitle = "";

    /**
       register
    **/
    protected RegisterIfc register = null;

    /**
       employee ID
    **/
    protected String employeeID = "";

    /**
       The Store Status Object
    **/
    protected StoreStatusIfc storeStatus = null;

    /**
       The Operator Object
    **/
    protected EmployeeIfc operator = null;

    //--------------------------------------------------------------------------
    /**
       This method is used to transfer the specific Role from the
       RoleEdit cargo to the shuttle.
       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        RoleEditCargo cargo = (RoleEditCargo)bus.getCargo();
        role                = cargo.getRoleSelected();
        oldRoleTitle        = cargo.getOldRoleTitle();
        register            = cargo.getRegister();
        employeeID          = cargo.getEmployeeID();
        storeStatus         = cargo.getStoreStatus();
        operator            = cargo.getOperator();
    }

    //--------------------------------------------------------------------------
    /**
       This method is used to transfer the specific Role from the
       shuttle to the SetAccess cargo.
       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        SetAccessCargo cargo = (SetAccessCargo)bus.getCargo();
        cargo.setRoleSelected(role);
        cargo.setOldRoleTitle(oldRoleTitle);
        cargo.setRegister(register);
        cargo.setEmployeeID(employeeID);
        cargo.setStoreStatus(storeStatus);
        cargo.setOperator(operator);
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
        String strResult = new String("Class: EditToSetAccessLaunchShuttle"
                                      + " (Revision " + getRevisionNumber() + ")" + hashCode());

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
        return(revisionNumber);
    }
}
