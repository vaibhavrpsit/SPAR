/* ===========================================================================
* Copyright (c) 2011, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/printing/MultipleGiftReceiptOptionSelectedRoad.java /main/1 2012/11/23 12:52:14 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  11/23/12 - Receipt enhancement quickwin changes
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.printing;

import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

/**
 * Display a dialog that shows a printer error has occurred.
 */
public class MultipleGiftReceiptOptionSelectedRoad extends LaneActionAdapter
{
    private static final long serialVersionUID = 1146256229885027972L;

    public static final String LANENAME = "GiftReceiptOptionSelectionAisle";

    /*
     * (non-Javadoc)
     * @see
     * oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse
     * (oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        PrintingCargo cargo = (PrintingCargo) bus.getCargo();
        cargo.setPrintMultipleGiftReceipt(true);

    }
}
