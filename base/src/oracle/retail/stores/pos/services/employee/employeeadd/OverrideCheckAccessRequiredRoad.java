/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeeadd/OverrideCheckAccessRequiredRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:09 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:16 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:56 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:56 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/04/06 18:41:24  awilliam
 *   @scr 4042 Security: Add Temp Employee: User with Manager Override and No Access priv does NOT invoke SECURITY_ERROR
 *
 *   Revision 1.1  2004/04/06 18:40:44  awilliam
 *   @scr 4042 Security: Add Temp Employee: User with Manager Override and No Access priv does NOT invoke SECURITY_ERROR
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeeadd;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeCargo;


//--------------------------------------------------------------------------
/**

    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class OverrideCheckAccessRequiredRoad extends PosLaneActionAdapter
{
    public static final String LANENAME = "OverrideCheckAccessRequiredRoad";

    public void traverse(BusIfc bus)
{
    EmployeeCargo cargo = ( EmployeeCargo)bus.getCargo();
    cargo.setAccessFunctionID(RoleFunctionIfc.ADD_TEMP_EMPLOYEE);
    
}
}
