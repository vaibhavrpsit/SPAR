/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeeadd/EmployeeAddTemporaryAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:09 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:27:56 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:21:16 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:10:48 PM  Robert Pearse   
 *
 * Revision 1.6  2004/09/23 00:07:14  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.5  2004/04/08 20:33:03  cdb
 * @scr 4206 Cleaned up class headers for logs and revisions.
 *
 * 
 * Rev 1.2 19 Jan 2004 12:50:56 Tim Fritz Employee data is now being reset.
 * 
 * Rev 1.1 Dec 16 2003 15:26:00 jriggins Making use of the EmployeeTypeEnum
 * class. Resolution for 3597: Employee 7.0 Updates
 * 
 * Rev 1.0 12 Dec 2003 14:02:34 jriggins Initial revision. 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeeadd;
// Domain imports
import oracle.retail.stores.domain.employee.EmployeeTypeEnum;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeCargo;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
//------------------------------------------------------------------------------
/**
 * Sets the type of employee to temporary in the cargo.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------
public class EmployeeAddTemporaryAisle extends PosLaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -836330141319209658L;

    //--------------------------------------------------------------------------
    /**
     * Sets the type of employee to temporary in the cargo.
     * 
     * @param bus
     *            the bus traversing this lane
     */
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        EmployeeCargo cargo = (EmployeeCargo) bus.getCargo();
        cargo.setAddEmployeeType(EmployeeTypeEnum.TEMPORARY);
        // When entering the Employee master screen from
        // the Add Options screen, all employee data should
        // be set to default values.
        cargo.setAccessFunctionID(RoleFunctionIfc.EMPLOYEE_ADD_FIND);
        cargo.setEmployee(null);
        // Mail the Add letter to move on.
        bus.mail(EmployeeCargo.ADD);
    }
}
