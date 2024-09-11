/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/HideOnScreenKeyboardAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:54 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   01/14/10 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

/**
 * A common aisle used to call the on screen keyboard.
 * 
 * @see UISubsystem#showOnScreenKeyboard(boolean)
 * $Revision:
 */
public class HideOnScreenKeyboardAisle extends LaneActionAdapter
{
    private static final long serialVersionUID = 2228419100137350367L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void traverse(BusIfc bus)
    {
        // grab the transaction
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        SaleReturnTransactionIfc transaction = cargo.getTransaction();
        // only hide the keyboard if transaction not started
        if (transaction == null)
        {
            UISubsystem ui = UISubsystem.getInstance();
            try
            {
                ui.showOnScreenKeyboard(false);
            }
            catch (UIException e)
            {
                logger.error("Unable to hide popup keyboard dialog.", e);
            }
        }
    }
}
