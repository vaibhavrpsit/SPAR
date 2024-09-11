/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/15/09 - Decrements the item counter in case when serial
 *                         validation fails for pick up transaction item
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.pickup;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.order.pickup.PickupOrderCargo;

/**
 * This site decremets the item counter in case of
 * inavlid serial number.
 * @author nkgautam
 *
 */
public class ModifySerialisedItemCounterSite extends PosSiteActionAdapter
{

    /**
     * Decrements the item counter
     * @param bus
     */
    public void arrive(BusIfc bus)
    {
        PickupOrderCargo cargo = (PickupOrderCargo)bus.getCargo();
        AbstractTransactionLineItemIfc[] lineItems = cargo.getSerializedItems();
        int counter = cargo.getSerializedItemsCounter();
        if(counter <= lineItems.length && counter !=0)
        {
            cargo.setSerializedItemsCounter(counter - 1);
        }
        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }

}
