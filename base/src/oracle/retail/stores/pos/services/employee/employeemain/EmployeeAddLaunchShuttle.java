/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeemain/EmployeeAddLaunchShuttle.java /main/11 2013/03/22 16:31:09 mkutiana Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:56 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:16 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:48 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/09/23 00:07:15  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.6  2004/04/30 19:47:16  tfritz
 *   @scr 4457 Added the Add Temp option to the employee main options screen.
 *
 *   Revision 1.5  2004/04/09 16:56:01  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.4  2004/03/14 21:24:26  tfritz
 *   @scr 3884 - New Training Mode Functionality
 *
 *   Revision 1.3  2004/02/12 16:50:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:49:04  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:59:28   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:23:22   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:32:34   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:23:44   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:58   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeemain;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
// quarry imports

//------------------------------------------------------------------------------
/**

    This station transfers data to the EmployeeAdd service.

    @version $Revision: /main/11 $
**/
//------------------------------------------------------------------------------
public class EmployeeAddLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 9039283429640279248L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.employee.employeemain.EmployeeAddLaunchShuttle.class);

    /**
       class name constant
    **/
    public static final String SHUTTLENAME = "EmployeeAddLaunchShuttle";

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";

    /**
       The cargo
    **/
    protected EmployeeCargo pCargo = null;

    //--------------------------------------------------------------------------
    /**
       load copys the data which needs to be passed to the EmployeeAdd service.
       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        // Get information from service
        pCargo = (EmployeeCargo) bus.getCargo();

    }

    //--------------------------------------------------------------------------
    /**
       unload copys the data which needs to be passed back from the
       EmployeeAdd service.
       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        // unload into the receiving service's cargo
        EmployeeCargo cCargo = (EmployeeCargo) bus.getCargo();
        cCargo.setOperator(pCargo.getOperator());
        cCargo.setOperatorID(pCargo.getOperatorID());
        cCargo.setRoleTitles(pCargo.getRoleTitles());
        cCargo.setRoles(pCargo.getRoles());
        cCargo.setRegister(pCargo.getRegister());
        cCargo.setAddEmployeeType(pCargo.getAddEmployeeType());
    }

    /**
       Returns a string representation of this object.
       <P>
       @param none
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  EmployeeAddLaunchShuttle (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @param none
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

} // end class EmployeeAddLaunchShuttle
