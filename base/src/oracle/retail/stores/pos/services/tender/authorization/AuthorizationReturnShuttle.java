/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/AuthorizationReturnShuttle.java /main/3 2014/07/01 13:33:27 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   06/05/14 - XbranchMerge
 *                         blarsen_bug18854403-ajb-call-ref-trans-cancel-bad-invoiceid
 *                         from rgbustores_14.0x_generic_branch
 *    blarsen   06/03/14 - Refactor: Moving call referral fields into their new
 *                         class.
 *    blarsen   02/04/14 - AJB requires original auth response for call
 *                         referrals. Adding this to appropriate
 *                         shuttles/cargos.
 *    blarsen   02/04/14 - Changed unload() to use getters/setters instead of
 *                         accessing data fields directly.
 *    asinton   08/02/12 - Call referral refactor
 *    cgreene   03/21/12 - refactor referral into separate tour
 *    cgreene   03/21/12 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.authorization;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;

/**
 * A shuttle that unloads the {@link AuthorizationCargo} directly.
 *
 * @author cgreene
 * @since 13.4.1
 */
public class AuthorizationReturnShuttle extends FinancialCargoShuttle
{
    private static final long serialVersionUID = 3626531416731775989L;

    protected AuthorizationCargo cargo;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
        super.load(bus);
        cargo = (AuthorizationCargo)bus.getCargo();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        AuthorizationCargo cargo = (AuthorizationCargo)bus.getCargo();
        cargo.setRequestList(this.cargo.getRequestList());
        cargo.setResponseList(this.cargo.getResponseList());
        cargo.setCurrentIndex(this.cargo.getCurrentIndex());
        cargo.setCallReferralData(this.cargo.getCallReferralData());
    }

}
