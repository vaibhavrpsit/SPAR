/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/role/RoleAddReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:06 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:47 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:55 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:58 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:15  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:01  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:48:56  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:54:19  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:37:34   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:06:58   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:20:52   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:12:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:13:04   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.role;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.admin.role.roleadd.RoleAddCargo;

//------------------------------------------------------------------------------
/**
    This shuttle will transfer the Role to be modified from the
    RoleAdd cargo to the shuttle, and then from the
    shuttle to the RoleMain cargo.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class RoleAddReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -8063016767272668689L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.admin.role.RoleAddReturnShuttle.class);

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       constant for the shuttle name
    **/
    public static final String SHUTTLENAME = "RoleAddReturnShuttle";

    /**
       role selected
    **/
    public RoleIfc roleSelected = null;

    /**
       old role title
    **/
    String oldRoleTitle = "";

    //--------------------------------------------------------------------------
    /**
       This method is used to load the shuttle with data from
       the RoleOptions cargo.
       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        RoleAddCargo cargo = (RoleAddCargo)bus.getCargo();
        roleSelected = cargo.getRoleSelected();
        oldRoleTitle = cargo.getOldRoleTitle();

    }

    //--------------------------------------------------------------------------
    /**
       This method is used to unload the shuttle data into
       the main role service's RoleMain cargo.
       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        RoleMainCargo cargo = (RoleMainCargo)bus.getCargo();
        cargo.setRoleSelected(roleSelected);
        cargo.setOldRoleTitle(oldRoleTitle);

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
        String strResult = new String("Class " + SHUTTLENAME
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

