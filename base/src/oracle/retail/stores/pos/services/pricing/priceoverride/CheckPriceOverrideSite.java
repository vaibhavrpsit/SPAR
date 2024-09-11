/* ===========================================================================
* Copyright (c) 2000, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/priceoverride/CheckPriceOverrideSite.java /main/7 2014/07/07 16:57:30 vineesin Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vineesin  07/07/14 - Adding condition to throw error when lineitem is
 *                         order pickup or cancel but not re-priced item
 *    aariyer   07/30/10 - For the price override of a non returnable item
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    2    360Commerce 1.1         3/18/2008 2:10:25 PM   Siva Papenini
 *         CR-30596
 *    1    360Commerce 1.0         3/18/2008 2:09:13 PM   Siva Papenini
 *         CR-30596: Added Site for Invalid Price Override Dialog
 *
 *Revision: /main/2 $
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing.priceoverride;
// foundation imports
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.pricing.DiscountCargoIfc;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This site checks to see if the item requires a price Override.
    <p>
    @version $Revision: /main/7 $
**/
//--------------------------------------------------------------------------
public class CheckPriceOverrideSite extends PosSiteActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/7 $";

    //----------------------------------------------------------------------
    /**
        Checks the item to see if price override is required.
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        //CR 30596 :Display error message "INVALID_PRICE_OVERRIDE_DIALOG" when item is a returned item.
    	// get the pricing cargo
		PricingCargo pc = (PricingCargo) bus.getCargo();

		// returned item is from Receipt.
		SaleReturnLineItemIfc[] lineItemList = pc.getItems();

		if (lineItemList != null && lineItemList.length > 0 &&
            (lineItemList[0].isReturnLineItem() &&
             lineItemList[0].getReturnItem() != null &&
             lineItemList[0].getReturnItem().isFromRetrievedTransaction()) ||
            (lineItemList[0].isPickupCancelLineItem() && 
             !lineItemList[0].isInStorePriceDuringPickup()))
		{
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			DialogBeanModel dialogModel = new DialogBeanModel();
			dialogModel.setResourceID(DiscountCargoIfc.INVALID_PRICE_OVERRIDE_DIALOG);
			dialogModel.setType(DialogScreensIfc.ERROR);
			dialogModel.setButtonLetter(0, DialogScreensIfc.DIALOG_BUTTON_LABELS[5]);
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		 }
		else
        {
            bus.mail(new Letter("PriceOverride"), BusIfc.CURRENT);
        }
    }
}
