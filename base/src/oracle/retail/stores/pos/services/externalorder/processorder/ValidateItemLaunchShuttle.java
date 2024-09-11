/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/processorder/ValidateItemLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:59 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    05/14/10 - initial version for external order processing
 *    acadar    05/14/10 - initial version
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.externalorder.processorder;

import oracle.retail.stores.common.utility.LocaleMap;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;

/**
 * Shuttle from process order service to item validation service
 * @author acadar
 *
 */
public class ValidateItemLaunchShuttle implements ShuttleIfc
{

    /**
     * This id is used to tell
     * the compiler not to generate a
     * new serialVersionUID.
     */
    private static final long serialVersionUID = -1528294143135041614L;

    protected ProcessOrderCargo cargo = null;

   /**
    * Loads the shuttle
    */
    public void load(BusIfc bus)
    {
        this.cargo = (ProcessOrderCargo)bus.getCargo();
    }

   /**
    * Unloads data into the item validation service
    *
    */
    public void unload(BusIfc bus)
    {
        ItemInquiryCargo itemCargo = (ItemInquiryCargo)bus.getCargo();
        itemCargo.setRegister(this.cargo.getRegister());

        // Set Flag for just doing a PLU lookup
        itemCargo.setIsRequestForItemLookup(true);
        // set whether or not it is an related item lookup
        itemCargo.setRelatedItem(false);

        String geoCode = null;
        if(cargo.getStoreStatus() != null && cargo.getStoreStatus().getStore() != null)
        {
            geoCode = cargo.getStoreStatus().getStore().getGeoCode();
        }

        itemCargo.setInquiry(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE), cargo.getCurrentExternalOrderItem().getPOSItemId(), "", "", geoCode);

    }



}
