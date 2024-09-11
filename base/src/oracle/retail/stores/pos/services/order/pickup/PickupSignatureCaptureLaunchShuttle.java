/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/pickup/PickupSignatureCaptureLaunchShuttle.java /main/6 2012/09/19 14:27:13 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       09/19/12 - set register
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  03/19/09 - Fixed signature capture dialog issue for PDO
 *                         transaction
 *    aphulamb  11/24/08 - Checking files after code review by amrish
 *    aphulamb  11/23/08 - transfer the tender amount
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.order.pickup;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.signaturecapture.SignatureCaptureCargo;

public class PickupSignatureCaptureLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -7039998886733837781L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/6 $";

    /**
     * Pickup Order cargo
     */
    protected PickupOrderCargo pickupCargo = null;

    // ----------------------------------------------------------------------
    /**
     * load pickup order cargo
     *
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    // ----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        pickupCargo = (PickupOrderCargo)bus.getCargo();
    }

    // ----------------------------------------------------------------------
    /**
     * load pickup order cargo data into signature capture cargo
     *
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    // ----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        SignatureCaptureCargo sigCargo = (SignatureCaptureCargo)bus.getCargo();
        sigCargo.setAuthAmount(pickupCargo.getTransaction().getPayment().getPaymentAmount());
        sigCargo.setOrderTransactionFlag(true);
        sigCargo.setRegister(pickupCargo.getRegister());
    }

}
