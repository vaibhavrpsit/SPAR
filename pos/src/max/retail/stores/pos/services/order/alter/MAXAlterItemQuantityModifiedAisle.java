/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAX, Inc.    All Rights Reserved.
  Rev 1.1	Prateek		01/07/2013		Change done for BUG 6616 
  Rev 1.0	Tanmaya		29/04/2013		Initial Draft: Home Delivery : Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.order.alter;

import java.math.BigDecimal;

import max.retail.stores.domain.order.MAXOrderIfc;
import max.retail.stores.pos.services.order.common.MAXOrderCargo;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAudit;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.services.modifyitem.ItemQuantityModifiedAisle;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXAlterItemQuantityModifiedAisle extends ItemQuantityModifiedAisle {

	private static final long serialVersionUID = 5049149569462764868L;

    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        String quantity = ui.getInput();
        BigDecimal tmp = null;
        JournalFormatterManagerIfc formatter =
            (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(JournalFormatterManagerIfc.TYPE);

        try
        {
            tmp = new BigDecimal(quantity);
            if (tmp.scale() == 0)
            {
                tmp = tmp.multiply(new BigDecimal("1.00"));
            }
            else if (tmp.scale() == 1)
            {
                tmp = tmp.multiply(Util.I_BIG_DECIMAL_ONE);
            }
        }
        catch  (Exception e)
        {
            tmp = new BigDecimal("1.00");//what do we do when invalid???
        }

        // if quantity entered is zero, reenter quantity.
        if(tmp.compareTo(BigDecimalConstants.ZERO_AMOUNT) == 0)
        {
            DialogBeanModel dModel = new DialogBeanModel();
            dModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            dModel.setResourceID("QuantityCannotBeZero");
            dModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Loop");
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dModel);
        }
        else
        {
        	MAXOrderCargo cargo = (MAXOrderCargo)bus.getCargo();
            

            //save original item in stringbuffer for journal
            StringBuffer sb = new StringBuffer();
           OrderLineItemIfc item = cargo.getLineItem();
           item.setQuantityPicked(tmp);
		   /**MAX Rev 1.1 Change : Start**/
            //sb.append(formatter.toJournalRemoveString(item));
		   /**MAX Rev 1.1 Change : End**/
            ItemDiscountStrategyIfc[] itemDiscounts =
                item.getItemPrice().getItemDiscounts();
            if((itemDiscounts != null) && (itemDiscounts.length > 0))
            {
                for(int i = 0; i < itemDiscounts.length; i++)
                {
                    if (!(itemDiscounts[i] instanceof ItemTransactionDiscountAudit))
                    {
                        sb.append(Util.EOL);
						/**MAX Rev 1.1 Change : Start**/
                        //sb.append(formatter.toJournalManualDiscount(item, itemDiscounts[i],true));
						/**MAX Rev 1.1 Change : End**/
                    }
                }
            }

            //set the quantity of the line item
            item.modifyItemQuantity(tmp);

            // CR28143: For VAT the line item's tax amounts are reported in the EJournal.
            // In order to retrieve the right values, they must be recalculated.  The
            // actual update to the transaction doesn't occur until leaving ModifyItemReturnShuttle.
            // To fix this calculate the tax forthe price overriden items in a clone of the transaction.
            // This fix is copied from ItemPriceModifiedAisle - Mani

            

            cargo.getOrder().replaceLineItem(item, item.getLineNumber());
            
            
            
            
                cargo.setItemQuantity(tmp);
                cargo.setLineItem(null);
                if(cargo.getOrder() instanceof MAXOrderIfc)
                {
                	((MAXOrderIfc)cargo.getOrder()).setAlterOrder(true);
                }
                bus.mail(new Letter("ShippingMethod"), BusIfc.CURRENT);
           
        }

    }
}
