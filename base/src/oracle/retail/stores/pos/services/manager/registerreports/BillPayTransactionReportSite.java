/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    nkgautam  07/02/10 - bill pay report changes
 *    nkgautam  06/30/10 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.manager.registerreports;

import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DateRangeReportBeanModel;

public class BillPayTransactionReportSite extends PosSiteActionAdapter
{

  private static final long serialVersionUID = -7560378817246303784L;
  
  public void arrive(BusIfc bus)
  {
    RegisterReportsCargo cargo = (RegisterReportsCargo) bus.getCargo();
    cargo.setReportType(RegisterReportsCargo.REPORT_BILL_PAY_TRANSACTION);
    POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
    DateRangeReportBeanModel model = new DateRangeReportBeanModel();
    StoreStatusIfc store = cargo.getStoreStatus();

    if (store != null)
    {
        EYSDate startDate = store.getBusinessDate();
        
        if (startDate != null)
        {
            model.setStartBusinessDate(startDate);
            model.setEndBusinessDate(startDate);
        }
    }

    ui.showScreen(POSUIManagerIfc.DATE_RANGE_REPORT, model);

  }

}
