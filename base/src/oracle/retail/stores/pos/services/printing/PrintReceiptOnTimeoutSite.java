/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * 
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    subrdey   06/14/12 - Site for printing the receipt when
 *                         eReceiptEmailAddress screen timed out.
 * 
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.printing;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

/**
 * 
 * Print the receipt when eReceiptEmailAddress screen gets timeout.
 * 
 */

public class PrintReceiptOnTimeoutSite extends PosSiteActionAdapter
{

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * <p>
     * 
     * @param bus the bus arriving at this site
     */
    public void arrive(BusIfc bus)
    {
        String currentLetter = bus.getCurrentLetter().getName();
        // get transaction from cargo
        PrintingCargo cargo = (PrintingCargo)bus.getCargo();
        TenderableTransactionIfc trans = cargo.getTransaction();
        if ((CommonLetterIfc.TIMEOUT).equals(currentLetter))
        {
            // if SaleReturnTransaction then set flag to print paper copy.
            if (trans instanceof SaleReturnTransactionIfc)
            {
                cargo.setPrintPaperReceipt(true);
                cargo.setPrintEreceipt(false);
            }
        }
        // mail the Timeout Letter
        bus.mail(new Letter(CommonLetterIfc.TIMEOUT), BusIfc.CURRENT);
    }
}
