/* ===========================================================================
* Copyright (c) 2011, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/printing/GiftReceiptOptionSelectionAisle.java /main/1 2012/11/23 12:52:12 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  11/23/12 - Receipt enhancement quickwin changes
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.printing;

import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

import oracle.retail.stores.pos.services.PosLaneActionAdapter;

import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Display a dialog that shows a printer error has occurred.
 */
public class GiftReceiptOptionSelectionAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 1146256229885027972L;

    public static final String LANENAME = "GiftReceiptOptionSelectionAisle";

    /*
     * (non-Javadoc)
     * @see
     * oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse
     * (oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        PrintingCargo cargo = (PrintingCargo) bus.getCargo();
        TenderableTransactionIfc trans = cargo.getTransaction();

        // Get the lineItems from the transaction.
        if (trans instanceof SaleReturnTransactionIfc)
        {
            SaleReturnTransactionIfc srTrans = (SaleReturnTransactionIfc) trans;
            AbstractTransactionLineItemIfc[] lineItems = srTrans.getLineItems();
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

            int giftReceiptItems = 0;

            for (int i = 0; i < lineItems.length; i++)
            {
                if (lineItems[i] instanceof SaleReturnLineItemIfc
                        && !((SaleReturnLineItemIfc) lineItems[i]).isReturnLineItem()
                        && ((SaleReturnLineItemIfc) lineItems[i]).isGiftReceiptItem())
                {
                    giftReceiptItems++;
                }
            }

            if (giftReceiptItems > 1)
            {
                DialogBeanModel model = new DialogBeanModel();
                model.setResourceID("GiftReceiptPrompt");
                model.setType(DialogScreensIfc.YES_NO);

                // display dialog
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            }

        }

    }
}
