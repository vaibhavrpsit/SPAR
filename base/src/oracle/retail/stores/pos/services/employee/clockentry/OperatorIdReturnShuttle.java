/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/clockentry/OperatorIdReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:09 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:12 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:49 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:50 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:15  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:02  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:50:15  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:49:15  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:59:04   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   May 07 2003 14:56:12   adc
 * Added security access check 
 * Resolution for 2327: jkl (no access employee) able to enter Clock In/Out, never prompted for Manger Override
 * 
 *    Rev 1.0   Apr 29 2002 15:24:22   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:32:00   msg
 * Initial revision.
 * 
 *    Rev 1.0   28 Oct 2001 17:55:52   mpm
 * Initial revision.
 * Resolution for POS SCR-235: Employee clock-in, clock-out
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.clockentry;
// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.operatorid.OperatorIdCargo;

//------------------------------------------------------------------------------
/**
    This shuttle carries the required contents from
    the OperatorId service to the calling service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class OperatorIdReturnShuttle implements ShuttleIfc
{                                       // begin class OperatorIdReturnShuttle
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -3780917649009937683L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.employee.clockentry.OperatorIdReturnShuttle.class);
    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       class name constant
    **/
    public static final String SHUTTLENAME = "OperatorIdReturnShuttle";
    /**
        Child service cargo
    **/
    protected OperatorIdCargo opIDCargo = null;

    //--------------------------------------------------------------------------
    /**
       Gets the cargo from the Operator ID service into the shuttle.
       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        opIDCargo = (OperatorIdCargo) bus.getCargo();
    }

    //--------------------------------------------------------------------------
    /**
        Unloads the data from the shuttle to the calling service's cargo.
       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        ClockEntryCargo cargo = (ClockEntryCargo) bus.getCargo();
        cargo.setClockingEmployee(opIDCargo.getSelectedEmployee());
        cargo.setOperator(opIDCargo.getSelectedEmployee());
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the source-code-control system revision number. <P>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                  // end getRevisionNumber()
}                                       // end class OperatorIdReturnShuttle
