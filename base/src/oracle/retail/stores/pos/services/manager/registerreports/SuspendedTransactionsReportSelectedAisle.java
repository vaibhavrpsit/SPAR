/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/manager/registerreports/SuspendedTransactionsReportSelectedAisle.java /main/10 2011/02/16 09:13:27 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:17 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:36 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:50:59  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:46  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Nov 04 2003 13:53:08   rrn
 * Initial revision.
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.manager.registerreports;

import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * The user has selected the Summary report
 * 
 * @version $Revision: /main/10 $
 */
public class SuspendedTransactionsReportSelectedAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 6594252955427379865L;

    public static final String LANENAME = "SuspendedTransactionsReportSelectedAisle";

    /**
     * Set the report type in the cargo and mail a letter.
     * 
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
        RegisterReportsCargo cargo = (RegisterReportsCargo) bus.getCargo();
        cargo.setAccessFunctionID(RoleFunctionIfc.REPORT_SUSPEND_TRANSACTION);
        cargo.setReportType(RegisterReportsCargo.REPORT_SUSPENDED_TRANSACTION);
        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }
}
