/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/security/RoleLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:07 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:47 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:56 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:58 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:01  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:49:01  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:37:32  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:38   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:36:56   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:07:40   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:21:22   msg
 * Initial revision.
 * 
 *    Rev 1.1   22 Jan 2002 17:23:26   baa
 * set operator
 * Resolution for POS SCR-309: Convert to new Security Override design.
 *
 *    Rev 1.0   Sep 21 2001 11:10:46   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:13:16   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.security;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.admin.role.RoleMainCargo;

//------------------------------------------------------------------------------
/**
    The RoleLaunchShuttle moves data from
    the Security service to the RoleMain service.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class RoleLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 221965081422130987L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.admin.security.RoleLaunchShuttle.class);

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       class name constant
    **/
    public static final String SHUTTLENAME = "RoleLaunchShuttle";

    /**
       Source cargo
    **/
    protected SecurityCargo sCargo = null;

    //--------------------------------------------------------------------------
    /**

       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        sCargo = (SecurityCargo) bus.getCargo();

    }

    //--------------------------------------------------------------------------
    /**
       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        RoleMainCargo cargo = (RoleMainCargo) bus.getCargo();

        cargo.setRegister(sCargo.getRegister());
        cargo.setEmployeeID(sCargo.getOperator().getEmployeeID());
        cargo.setOperator(sCargo.getOperator());
        cargo.setStoreStatus(sCargo.getStoreStatus());

    }


    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @param none
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        // result string
        String strResult = new String("Class: " + SHUTTLENAME
                                      + " (Revision " + getRevisionNumber()
                                      + ")" + hashCode());

        // pass back result
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @param none
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        // return string
        return(revisionNumber);
    }
}
