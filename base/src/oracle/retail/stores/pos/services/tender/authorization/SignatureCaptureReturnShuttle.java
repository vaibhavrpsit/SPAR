/* ===========================================================================
* Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/SignatureCaptureReturnShuttle.java /main/12 2013/12/06 15:21:38 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   11/23/13 - Set signature for all the responses.
 *    jswan     06/29/11 - Moved from tenderauth tour for APF.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.authorization;

import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.signaturecapture.SignatureCaptureCargo;

/**
 *  Set's the captured signature on the authorization cargo  
 */
public class SignatureCaptureReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 5657604093747456851L;

    /** The child service cargo */
    SignatureCaptureCargo sigCargo = null;
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void load(BusIfc bus)
    {
        sigCargo = (SignatureCaptureCargo)bus.getCargo();
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void unload(BusIfc bus)
    {
        AuthorizationCargo cargo = (AuthorizationCargo)bus.getCargo();
        int count = cargo.getResponseList().size(); 
        for (int i=0; i<count; i++)
        {
            AuthorizeTransferResponseIfc response = cargo.getResponseList().get(i);
            response.setSignature(sigCargo.getSignature());
        }  
    }   
}
