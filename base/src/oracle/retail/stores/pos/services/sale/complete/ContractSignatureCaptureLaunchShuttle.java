/* ===========================================================================
* Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/complete/ContractSignatureCaptureLaunchShuttle.java /main/4 2013/04/25 13:47:18 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     04/25/13 - Modified to prevent NullPointerException in
 *                         CaptureSignatureSite.
 *    acadar    06/08/10 - changes for signature capture, disable txn send, and
 *                         discounts
 *    acadar    06/03/10 - changes for signature capture and refactoring
 *    acadar    06/03/10 - signature capture for external order contract
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.complete;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.services.signaturecapture.SignatureCaptureCargo;

/**
 *  Transfers legal documents to the Signature Capture Site
 *
**/
public class ContractSignatureCaptureLaunchShuttle implements ShuttleIfc
{

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 8239807826951479281L;

    /**
     * Sale Cargo
     */
    protected  SaleCargoIfc saleCargo;


    //----------------------------------------------------------------------
    /**

        @param bus
        @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        saleCargo = (SaleCargoIfc) bus.getCargo();
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
        // set the legal document in the cargo
        sigCargo.setLegalDocument(saleCargo.getLegalDocument());
        sigCargo.setVerifySignature(false);
        sigCargo.setRegister(saleCargo.getRegister());
    }

}
