/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeemain/SelectEmployeeOptionAddTempRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:09 mszekely Exp $
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
 */
package oracle.retail.stores.pos.services.employee.employeemain;

import oracle.retail.stores.domain.employee.EmployeeTypeEnum;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeCargo;

//--------------------------------------------------------------------------
/**
 Store up employee type as temporary in cargo
 <p>
 $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class SelectEmployeeOptionAddTempRoad extends PosLaneActionAdapter
{
    public static final String LANENAME = "SelectEmployeeOptionAddTempRoad";

    public void traverse(BusIfc bus)
    {
        EmployeeCargo cargo = (EmployeeCargo) bus.getCargo();
        cargo.setAddEmployeeType(EmployeeTypeEnum.TEMPORARY);
    }
}
