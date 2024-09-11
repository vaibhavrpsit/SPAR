/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/manager/registerreports/BillPaymentsReportSelectedAisle.java /main/3 2011/05/13 10:03:40 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    nkgautam  07/02/10 - bill pay report changes
 *    nkgautam  06/29/10 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.manager.registerreports;

import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

public class BillPaymentsReportSelectedAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -5821382185542906939L;

    /**
     * Set the report type in the cargo and mail a letter.
     */
    @Override
    public void traverse(BusIfc bus)
    {
        RegisterReportsCargo cargo = (RegisterReportsCargo) bus.getCargo();
        cargo.setAccessFunctionID(RoleFunctionIfc.REPORT_DEPARTMENT_SALES);
        cargo.setReportType(RegisterReportsCargo.REPORT_BILL_PAY_TRANSACTION);
        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }
}
