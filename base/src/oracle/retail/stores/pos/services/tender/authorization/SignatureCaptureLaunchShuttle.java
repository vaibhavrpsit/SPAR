/* ===========================================================================
* Copyright (c) 2011, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/SignatureCaptureLaunchShuttle.java /main/13 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   04/25/12 - implement locking mechanism for concurrent users
 *                         using a single CPOI device.
 *    jswan     09/21/11 - Fixed failure to display signature on signature
 *                         verification screen after 1st time.
 *    ohorne    08/09/11 - APF:foreign currency support
 *    jswan     06/22/11 - Modified to support signature capture in APF.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.authorization;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.signaturecapture.SignatureCaptureCargo;

/**
 *  Transfers the tender amount 

    @version $Revision: /main/13 $
**/
public class SignatureCaptureLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -7039998886733837781L;


    /**
       Cargo carrying the tender amount 
    **/
    protected AuthorizationCargo tenderCargo = null;

    //----------------------------------------------------------------------
    /**
        
        @param bus
        @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        tenderCargo = (AuthorizationCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
        
        @param bus
        @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        SignatureCaptureCargo sigCargo = (SignatureCaptureCargo)bus.getCargo();
        sigCargo.setAuthAmount(tenderCargo.getCurrentResponse().getBaseAmount());
        sigCargo.setVerifySignature(true);
        sigCargo.setRegister(tenderCargo.getRegister());
    }

}
