/*===========================================================================
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/pickup/ValidateAgeRestrictionItemsSite.java /main/2 2013/04/16 13:32:50 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 * vtemker     09/01/14 - Fixed Nullpointer exception in related items (Bug # 19518323 )      
 * abhinavs    07/31/14 - Allow user to enter dob again when age verification
 *                        failed during order pickup
 * vtemker     04/16/13 - Moved constants in OrderLineItemIfc to
 *                        OrderConstantsIfc in common project
 * mkutiana    01/09/13 - Validate items restrictive age with DOB and reset pickup status
 * mkutiana    01/09/13 - Creation
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.order.pickup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.SplitOrderItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.order.common.OrderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.AgeRestrictionBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site Validates the age restriction of the items being picked up in the order.
 * If the validation is not met, the items are not allowed to be picked up. Flow returns
 * to the Edit Item status screen with the items returned to their previous status 
 * $Revision: /main/2 $
 */
public class ValidateAgeRestrictionItemsSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -6809990757120702297L;

    /**
     * This site Validates the age restriction of the items being picked up in the order.
     *  If the validation is not met, the items are not allowed to be picked up. Flow returns
     *  to the Edit Item status screen with the items returned to their previous status 
     */
    @Override
    public void arrive(BusIfc bus)
    {
        //Loop through the items selected for pickup and if any are age restrictive then display the DOBPrompt dialog
        LetterIfc letter = new Letter(CommonLetterIfc.CONTINUE);
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);  
        OrderCargo cargo = (OrderCargo) bus.getCargo();
        boolean ageRestrictionDialogNeeded = false;
        
        SplitOrderItemIfc[] splitOrderItems = cargo.getSplitOrderItems();
        SaleReturnLineItemIfc orderLineItem = null;
        OrderItemStatusIfc orderItemStatus = null;
        List<SplitOrderItemIfc> orderitemsFailedAgeValidation = new ArrayList<SplitOrderItemIfc>();
        
        if (cargo.getPickupDOB() == null)
        {
            AgeRestrictionBeanModel model = (AgeRestrictionBeanModel)ui.getModel();
            cargo.setPickupDOB(model.getDateOfBirth());
        }
                
        for (SplitOrderItemIfc lineItem : splitOrderItems)
        {
            int ageNeeded = lineItem.getOriginalOrderLineItem().getPLUItem().getRestrictiveAge();
            if (lineItem.getStatus().getStatus() == OrderConstantsIfc.ORDER_ITEM_STATUS_PICK_UP && ageNeeded > 0)                
            {
                EYSDate today = new EYSDate();
                int yearsBetween = cargo.getPickupDOB().yearsBetween(today);
                // if not old enough then display problem
                if (yearsBetween < ageNeeded)
                {
                    ageRestrictionDialogNeeded = true;
                    orderitemsFailedAgeValidation.add(lineItem);
                    //Change the status of the item back to the previous status (filled)
                    orderLineItem = lineItem.getOriginalOrderLineItem();
                    orderItemStatus = orderLineItem.getOrderItemStatus();
                    orderItemStatus.setQuantityPickup(BigDecimal.ZERO);
                    lineItem.getStatus().setStatus(lineItem.getStatus().getPreviousStatus());
                    cargo.setPickupDOB(null);
                    break;
                }
            }
        }
        
        if (ageRestrictionDialogNeeded)
        {
            // create array from list
            SplitOrderItemIfc[] orderitemsFailedAgeValidationList = new SplitOrderItemIfc[orderitemsFailedAgeValidation.size()];
            orderitemsFailedAgeValidation.toArray(orderitemsFailedAgeValidationList);
            dialogForAgeRestriction(bus, orderitemsFailedAgeValidationList);
        }
        else
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    }
    
    /**
     * This displays the Dialog for the Items that failed Age Validation at pickup
     * 
     * @param bus
     * @param soList List of Items that have failed the Age Verification
     */
    private void dialogForAgeRestriction(BusIfc bus, SplitOrderItemIfc[] orderitemsFailedAgeValidationList)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("PickupCustomerAgeInvalidWarning");
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Invalid");
        
        String failedItemsDescription[] = new String[1];
        failedItemsDescription[0] = getFailedItemsDescription(orderitemsFailedAgeValidationList);
        dialogModel.setArgs(failedItemsDescription);
        
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
    /**
     * Returns a string, list of item descriptions that have failed the age verification. A max of 10 items are returned.
     * @param orderitemsFailedAgeValidationList
     * @return String Returns a string, list of item descriptions that have failed the age verification.
     */
    private String getFailedItemsDescription(SplitOrderItemIfc[] orderitemsFailedAgeValidationList)
    {
        String failedItemsDescription = "\n";
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
        int itemCount = 0;
        for (SplitOrderItemIfc foi : orderitemsFailedAgeValidationList)
        {
            itemCount++;
            failedItemsDescription = failedItemsDescription + foi.getOriginalOrderLineItem().getItemID() +", " + foi.getOriginalOrderLineItem().getItemDescription(locale) + "\n";
            if (itemCount > 10) 
            {
                failedItemsDescription = failedItemsDescription + " ..... \n"; 
            }
        }
        return failedItemsDescription;
    }
}