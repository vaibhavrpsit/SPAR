/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeeadd/ProcessOkAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:09 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:31 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:25 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:27 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:14  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/03/03 23:15:10  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:49:24  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:59:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:24:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:32:14   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:23:14   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:44   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeeadd;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeCargo;

//------------------------------------------------------------------------------
/**
    The ProcessOkAisle is traversed when an Ok letter is received.  It checks
    the cargo to see if the error was fatal or non-fatal.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class ProcessOkAisle extends PosLaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -3791154850611357186L;


    public static final String LANENAME = "ProcessOkAisle";

    //--------------------------------------------------------------------------
    /**
       The ProcessOkAisle is traversed when an Ok letter is received.  It checks
       the cargo to see if the error was fatal or non-fatal.
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {

        EmployeeCargo cargo = (EmployeeCargo) bus.getCargo();
        Letter letter;

        if (cargo.getFatalError())
        {
            // Any error is fatal at this point
            letter = new Letter(CommonLetterIfc.FAILURE);
        }
        else
        {
            // Any error is fatal at this point
            letter = new Letter(EmployeeCargo.NOT_FATAL);

        }
        bus.mail(letter, BusIfc.CURRENT);


    }
} // end class ProcessOKAisle
