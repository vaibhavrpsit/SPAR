/* ===========================================================================
* Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/reprintreceipt/VATPrintGiftReceiptAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:30 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   06/25/10 - Adding VAT classes back into source tree.
 *    asinton   06/25/10 - Adding VAT classes back into source tree.
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

import oracle.retail.stores.pos.ui.POSUIManagerIfc;

/**
 * Subclass of PrintGiftReceiptAisle to support tax inclusive (VAT)
 * environment.
 */
public class VATPrintGiftReceiptAisle extends PrintGiftReceiptAisle
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5356311996485423095L;

    /**
     * Override of method getScreenID() in DisplayReprintSelectSite
     * to support tax inclusive (VAT) environment.  Returns "VAT_REPRINT_SELECT".
     *
     * @return
     * @see oracle.retail.stores.pos.services.reprintreceipt.PrintGiftReceiptAisle#getScreenID()
     */
    protected String getScreenID()
    {
        return POSUIManagerIfc.VAT_REPRINT_SELECT;
    }

}
