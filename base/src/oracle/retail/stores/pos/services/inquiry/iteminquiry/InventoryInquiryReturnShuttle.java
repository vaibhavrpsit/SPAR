/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/InventoryInquiryReturnShuttle.java /main/7 2013/04/10 15:29:43 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  03/29/13 - Fixed unload method to create SearchCriteria
 *                         instance
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    2    360Commerce 1.1         2/26/2008 7:33:26 AM   Naveen Ganesh   Item
 *         getting added to the transaction, has been avoided to get added.
 *    1    360Commerce 1.0         11/22/2007 10:57:24 PM Naveen Ganesh   
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import oracle.retail.stores.domain.transaction.SearchCriteria;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.inventoryinquiry.InventoryInquiryCargo;

/**
 * @version $Revision: /main/7 $
 **/

public class InventoryInquiryReturnShuttle implements ShuttleIfc
{

    private static final long serialVersionUID = 2192694313947081783L;

    protected InventoryInquiryCargo inventoryInquiryCargo = null;

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc.load(BusIfc b)
     */
    @Override
    public void load(BusIfc bus)
    {
        inventoryInquiryCargo = (InventoryInquiryCargo)bus.getCargo();
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc.unload(BusIfc b)
     */
    public void unload(BusIfc bus)
    {
        if (bus.getCargo() instanceof ItemInquiryCargo)
        {
            ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
            if (inventoryInquiryCargo.getInquiry() == null)
            {
                SearchCriteriaIfc inquiry = new SearchCriteria();
                inquiry.setItemID(inventoryInquiryCargo.getItemInfo().getItemID());
                cargo.setInquiry(inquiry);
            }

        }
    }
}
