/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     11/03/14 - added UIN (SIM) lookup to the non-receipted returns flow.
 *    rsnayak   09/28/11 - copied plu item from uin cargo to return item cargo
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mchellap  12/15/09 - Serialisation changes
 *    mchellap  12/15/09 - Return shuttle for UINLookup service
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.common.UINLookUpCargo;

@SuppressWarnings("serial")
public class UINLookUpReturnShuttle implements ShuttleIfc
{

    /**
     * UIN Lookup Cargo class
     */
    protected UINLookUpCargo uinCargo;

    /**
     * Loads the UIN LookUp cargo.
     *
     * @param bus
     */
    public void load(BusIfc bus)
    {
        uinCargo = (UINLookUpCargo) bus.getCargo();
    }

    /**
     * Transfers the UIN Lookup cargo to the Return Item cargo.
     *
     * @param bus
     */
    public void unload(BusIfc bus)
    {
        ReturnItemCargo returnItemCargo = (ReturnItemCargo) bus.getCargo();

        SearchCriteriaIfc criteria = uinCargo.getInquiry();
        String serialInput = criteria.getItemID();
        returnItemCargo.setItemSerial(serialInput);
        if(uinCargo.getPluItem() != null)
        {
            returnItemCargo.setPLUItem(uinCargo.getPluItem());
        }
    }
}
