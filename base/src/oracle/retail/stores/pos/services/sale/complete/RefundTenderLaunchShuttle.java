/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/complete/RefundTenderLaunchShuttle.java /main/1 2014/02/10 15:44:38 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   02/10/14 - reworked flow for gift card activation error
 *                         scenarios
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.sale.complete;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.services.sale.validate.TenderLaunchShuttle;
import oracle.retail.stores.pos.services.tender.TenderCargo;

/**
 * This shuttle extends the {@link TenderLaunchShuttle} in order to call {@link TenderCargo#setGiftCardActivationsCanceled(boolean)}.
 * @since 14.0.1
 *
 */
public class RefundTenderLaunchShuttle extends TenderLaunchShuttle
{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5394203670764722351L;

    /**
     * Flag to indicate if gift card activations have been canceled.
     */
    protected boolean giftCardActivationsCanceled = false;

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.sale.validate.TenderLaunchShuttle#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
        super.load(bus);
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        giftCardActivationsCanceled = cargo.isGiftCardActivationsCanceled();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.sale.validate.TenderLaunchShuttle#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        TenderCargo tenderCargo = (TenderCargo)bus.getCargo();
        tenderCargo.setGiftCardActivationsCanceled(giftCardActivationsCanceled);
    }

    
}
