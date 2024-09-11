/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/printing/PromptForGiftReceiptPrintingSite.java /main/5 2013/02/28 08:44:23 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  02/27/13 - Do not show the gift receipt dialog for transaction
 *                         level gift receipt
 *    mchellap  02/14/13 - Disable gift receipt prompt for layaway initiate
 *    mchellap  02/13/13 - Modified gift receipt printing dialog message
 *    mchellap  11/30/12 - Added customer receipt preference
 *    mchellap  11/23/12 - Receipt enhancement quickwin changes
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.printing;

import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

import oracle.retail.stores.pos.ui.beans.DialogBeanModel;


/**
 * Display the printing options screen if configured. * 
 */
public class PromptForGiftReceiptPrintingSite extends PosSiteActionAdapter
{

    private static final long serialVersionUID = -2976187454602810274L;

    /**
     * Display the printing options screen if configured.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        PrintingCargo cargo = (PrintingCargo) bus.getCargo();
        TenderableTransactionIfc trans = cargo.getTransaction();
        

        // Get the lineItems from the transaction.
        // Don't display the promt for transaction level gift receipts.For
        // layaway transactions the prompt will appear only during layaway
        // complete.
        if ((trans instanceof SaleReturnTransactionIfc && !((SaleReturnTransactionIfc) trans).isTransactionGiftReceiptAssigned())
                && !(trans instanceof LayawayTransactionIfc && ((LayawayTransactionIfc) trans).getStatus() == LayawayConstantsIfc.STATUS_NEW))
        {
            SaleReturnTransactionIfc srTrans = (SaleReturnTransactionIfc) trans;
            AbstractTransactionLineItemIfc[] lineItems = srTrans.getLineItems();
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
 
            int giftReceiptItems = 0;
            
            // Don't prompt for send and gift registry transactions
            if(srTrans.getSendPackages() == null)
            {
                for (int i = 0; i < lineItems.length; i++)
                {
                    if (lineItems[i] instanceof SaleReturnLineItemIfc
                            && ((SaleReturnLineItemIfc) lineItems[i]).isGiftReceiptItem()
                            && !((SaleReturnLineItemIfc) lineItems[i]).isReturnLineItem()
                            && ((SaleReturnLineItemIfc) lineItems[i]).getRegistry() == null)
                    {
                        giftReceiptItems++;
                    }
                }
            }

            if (giftReceiptItems > 1)
            {
                DialogBeanModel model = new DialogBeanModel();
                model.setResourceID("GiftReceiptPrompt");
                model.setType(DialogScreensIfc.ONE_OR_MULTIPLE);

                // display dialog
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            }
            else
            { 
                // mail the letter
                bus.mail(bus.getCurrentLetter(), BusIfc.CURRENT);                
            }

        }
        else
        {
            // mail the letter
            bus.mail(bus.getCurrentLetter(), BusIfc.CURRENT);
        }
        
    }
}
