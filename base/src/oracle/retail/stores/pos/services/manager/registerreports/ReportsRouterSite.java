/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/manager/registerreports/ReportsRouterSite.java /main/15 2013/01/15 18:46:26 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       01/15/13 - add back order status report
 *    sgu       07/12/12 - remove retrieve order summary by status or by
 *                         emessage
 *    vtemker   03/08/11 - Print Preview Quickwin for Reports
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    nkgautam  07/02/10 - bill pay report changes
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:$
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.manager.registerreports;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.reports.QueuedTransactionReportItem;
import oracle.retail.stores.pos.reports.QueuedTransactionsReport;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Routes flow of execution (after access checking) based on report selected at
 * reports option site.
 *
 * @version $Revision: /main/15 $
 */
public class ReportsRouterSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 858154045238447450L;

    public static final String SITENAME = "ReportsRouterSite";

    /**
     * Mail the correct letter based on report type.
     *
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        RegisterReportsCargo cargo = (RegisterReportsCargo) bus.getCargo();
        int reportType = cargo.getReportType();
        LetterIfc letter = null;

        switch(reportType)
        {
            case RegisterReportsCargo.REPORT_DEPTSALES:
            case RegisterReportsCargo.REPORT_HOURSALES:
            case RegisterReportsCargo.REPORT_ASSOCPROD:
                letter = new Letter("DateRange");
                break;

            case RegisterReportsCargo.REPORT_SUMMARY:
                letter = new Letter("Summary");
                break;

            case RegisterReportsCargo.REPORT_QUETRANS:
                cargo.setReport(getQueuedTransactionsReport(cargo));
                letter = new Letter("PrintPreview");
                break;
                
            case RegisterReportsCargo.REPORT_ORDER_STATUS:
                letter = new Letter("OrderStatus");
                break;

            case RegisterReportsCargo.REPORT_ORDERS_SUMMARY:
                letter = new Letter("OrdersSummary");
                break;

            case RegisterReportsCargo.REPORT_SUSPENDED_TRANSACTION:
                letter = new Letter("SuspendedTransactions");
                break;

            case RegisterReportsCargo.REPORT_BILL_PAY_TRANSACTION:
                letter = new Letter("BillPayTransactionsReport");
                break;

            default:
                break;

        }
        bus.mail(letter, BusIfc.CURRENT);
    }

    /**
     * @param cargo
     * @return
     */
    protected QueuedTransactionsReport getQueuedTransactionsReport(RegisterReportsCargo cargo)
    {
        QueuedTransactionReportItem[] reportItems = QueuedTransactionsReport.getReportableQueuedTransactions();

        return new QueuedTransactionsReport(reportItems,
                    cargo.getStoreStatus().getStore().getStoreID(),
                    cargo.getRegister().getWorkstation().getWorkstationID(),
                    cargo.getOperator().getEmployeeID());
    }
}
