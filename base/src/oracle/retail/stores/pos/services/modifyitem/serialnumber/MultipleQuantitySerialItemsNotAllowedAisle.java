/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/serialnumber/MultipleQuantitySerialItemsNotAllowedAisle.java /rgbustores_13.4x_generic_branch/1 2011/08/10 12:32:12 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   08/10/11 - quickwin - implement dialog for trying to enter
 *                         multiple qty of serialized item
 *    cgreene   08/10/11 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem.serialnumber;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.sale.SaleCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * A lane to show the SERIAL_NOT_ALLOWED_W_MLTY_QTY dialog.
 *
 * @author cgreene
 * @since 13.4
 */
public class MultipleQuantitySerialItemsNotAllowedAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -854255340100754746L;

    public static final String DIALOG_RESOURCE_ID = "SerialNotAllowedWithMultipleQuantity";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        SaleCargo cargo = (SaleCargo)bus.getCargo();
        SaleReturnLineItemIfc srli = cargo.getLineItem();
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(DIALOG_RESOURCE_ID);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(new String[] { srli.getItemID() });
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);        
    }

}
