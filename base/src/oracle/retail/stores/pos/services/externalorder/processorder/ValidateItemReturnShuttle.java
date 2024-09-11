/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/processorder/ValidateItemReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:59 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    06/02/10 - refactoring
 *    acadar    06/02/10 - signature capture changes
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    05/21/10 - renamed from _externalorder to externalorder
 *    acadar    05/17/10 - temporarily rename the package
 *    acadar    05/14/10 - initial version for external order processing
 *    acadar    05/14/10 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.externalorder.processorder;

import oracle.retail.stores.domain.externalorder.ExternalOrderSaleItemIfc;

import oracle.retail.stores.domain.stock.PLUItemIfc;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;


/**
 * This is the shuttle that return the information from the item validation
 * station to the process order station
 * @author acadar
 *
 */
public class ValidateItemReturnShuttle implements ShuttleIfc
{


    /**
     * The serial version UID
     */
    private static final long serialVersionUID = 2016326100855282276L;


    protected ItemInquiryCargo cargo = null;

    /**
     * Loads the cargo from the item validation service
     */
    public void load(BusIfc bus)
    {
        this.cargo = (ItemInquiryCargo)bus.getCargo();
    }

   /**
    * Unloads the cargo into the process order service
    */
    public void unload(BusIfc bus)
    {
        ProcessOrderCargo processOrderCargo = (ProcessOrderCargo)bus.getCargo();

        PLUItemIfc itemList[] = this.cargo.getItemList();
        PLUItemIfc item = this.cargo.getPLUItem();

        ExternalOrderSaleItemIfc orderItem = processOrderCargo.getCurrentExternalOrderItem();
        orderItem.setPLUItem(item);
        orderItem.setPLUItems(itemList);

    }



}
