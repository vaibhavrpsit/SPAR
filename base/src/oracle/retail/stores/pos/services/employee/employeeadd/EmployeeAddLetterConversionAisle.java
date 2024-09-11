/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeeadd/EmployeeAddLetterConversionAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:09 mszekely Exp $
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
 *    1    360Commerce 1.0         3/24/2008 12:36:09 PM  Deepti Sharma   merge
 *          from v12.x to trunk
 *
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeeadd;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * Aisle converts whatever incoming letter to a "Continue" outgoing letter.
 *
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class EmployeeAddLetterConversionAisle extends PosLaneActionAdapter implements LaneActionIfc
{
    /**
     * Serialization Version UID
     */
    private static final long serialVersionUID = -3692110117145955874L;

    /**
     * Traverse method.  Mails a "Continue" letter.
     * @param bus
     */
    public void traverse(BusIfc bus)
    {
        bus.mail(CommonLetterIfc.CONTINUE);
    }
}
