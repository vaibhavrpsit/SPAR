/* ===========================================================================
* Copyright (c) 2011, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/validate/GetSerialNumberFromUIAisle.java /main/1 2012/02/14 13:47:24 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   02/13/12 - prompt for serial numbers when entering tender if
 *                         items are missing this data
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.sale.validate;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.sale.SaleCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

/**
 * Sets the item's serial number from the UI entry.
 * @author asinton
 * @since 13.4.1
 */
@SuppressWarnings("serial")
public class GetSerialNumberFromUIAisle extends LaneActionAdapter
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // get the item
        SaleCargo cargo = (SaleCargo)bus.getCargo();
        SaleReturnTransactionIfc transaction = cargo.getTransaction();
        int index = cargo.getSerializedItemIndex();
        SaleReturnLineItemIfc item = (SaleReturnLineItemIfc)transaction.getLineItems()[index];
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        // get the serial number
        String serial = ui.getInput();
        // set serial number on the item
        item.setItemSerial(serial);
        // increment the serialized item index on the cargo
        cargo.setSerializedItemIndex(index + 1);
        // mail letter to continue
        bus.mail(CommonLetterIfc.CONTINUE, BusIfc.CURRENT);
    }

}
