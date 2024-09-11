/* ===========================================================================
* Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/reprintreceipt/SetPrintVATSummaryRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:30 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  2    360Commerce 1.1         6/4/2007 12:50:12 PM   Alan N. Sinton  CR
 *       26484 - Changes per review comments.
 *  1    360Commerce 1.0         4/30/2007 4:55:58 PM   Alan N. Sinton  CR
 *       26484 - Merge from v12.0_temp.
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.reprintreceipt;

import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;

/**
 * Sets the options for printing VAT Summary.
 * $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class SetPrintVATSummaryRoad extends LaneActionAdapter
{
    public void traverse(BusIfc bus)
    {
        ReprintReceiptCargo cargo = (ReprintReceiptCargo)bus.getCargo();
        cargo.setReceiptStyle(PrintableDocumentManagerIfc.STYLE_VAT_TYPE_2);
    }

}
