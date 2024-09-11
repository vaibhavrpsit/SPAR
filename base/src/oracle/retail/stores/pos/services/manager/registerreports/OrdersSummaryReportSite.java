/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/manager/registerreports/OrdersSummaryReportSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:11 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:52 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:53 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/05/04 22:26:33  jdeleau
 *   @scr 1685 Business Date was not appearing as starting day for order summary reports.
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
 *    Rev 1.0   Aug 29 2003 16:01:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 07 2002 19:33:58   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:19:00   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:36:32   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:24:12   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:12   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.manager.registerreports;

// Foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DateRangeReportBeanModel;

/**
 * Produce the Orders Summary Report
 * 
 *  @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class OrdersSummaryReportSite extends PosSiteActionAdapter
{

    /**
     * Name of the Site
     */
    public static final String SITENAME = "OrdersSummaryReportSite";

    /**
     * Put up the Date Range form to get the required input.
     * 
     * @param bus the bus arriving at this site
     */
    public void arrive(BusIfc bus)
    {

        RegisterReportsCargo cargo = (RegisterReportsCargo) bus.getCargo();
        cargo.setReportType(RegisterReportsCargo.REPORT_ORDERS_SUMMARY);

        DateRangeReportBeanModel model = new DateRangeReportBeanModel();
        model.setStartBusinessDate(cargo.getRegister().getBusinessDate());
        model.setEndBusinessDate(cargo.getRegister().getBusinessDate());
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.ORDER_SUM_RPT, model);
    }
}
