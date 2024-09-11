/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/complete/ContractSignatureCaptureReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:01 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    06/08/10 - changes for signature capture, disable txn send, and
 *                         discounts
 *    acadar    06/07/10 - changes for signature capture
 *    acadar    06/03/10 - changes for signature capture and refactoring
 *    acadar    06/03/10 - signature capture for external order contract
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.complete;

import oracle.retail.stores.domain.externalorder.LegalDocumentIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.services.signaturecapture.SignatureCaptureCargo;

/**
 *  Set's the captured signature on the credit tender
 */
public class ContractSignatureCaptureReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    private static final long serialVersionUID = -2223170586490716782L;

    /** Signature capture cargo */
    SignatureCaptureCargo sigCargo = null;

    /**
     * Loads the shuttle
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
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        LegalDocumentIfc legalDocument = sigCargo.getLegalDocument();

        cargo.setLegalDocument(legalDocument);

    }
}
