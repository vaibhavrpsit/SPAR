/*===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/pickup/CheckAgeRestrictionItemsSite.java /main/2 2013/04/16 13:32:29 vtemker Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* vtemker     04/16/13 - Moved constants in OrderLineItemIfc to
*                        OrderConstantsIfc in common project
* mkutiana    01/08/13 - This site determines if the DOB prompt is needed for 
*                           age verifiable items, and displays it.
* mkutiana    01/08/13 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.services.order.pickup;

import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SplitOrderItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.order.common.OrderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.AgeRestrictionBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * This site determines if the DOB prompt is needed for age verifiable items, and displays it.
 * $Revision: /main/2 $
 */
public class CheckAgeRestrictionItemsSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -4861176476557151240L;

    /**
     * This method determines if the DOB prompt is needed for age verifiable items, and displays it..
     *
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        LetterIfc letter = new Letter(CommonLetterIfc.SKIP);
        OrderCargo cargo = (OrderCargo) bus.getCargo();
        boolean itemNeedsAgeValidation = false;
        SplitOrderItemIfc[] splitOrderItems = cargo.getSplitOrderItems();
        //Loop through the items selected for pickup and if any are age restrictive then display the DOBPrompt dialog
        for (SplitOrderItemIfc lineItem : splitOrderItems)
        {
            int ageNeeded = lineItem.getOriginalOrderLineItem().getPLUItem().getRestrictiveAge();
            if (lineItem.getStatus().getStatus() == OrderConstantsIfc.ORDER_ITEM_STATUS_PICK_UP && ageNeeded > 0)
            {
                itemNeedsAgeValidation = true;
                break;
            }
        }

        
        if (itemNeedsAgeValidation && cargo.getPickupDOB() == null)
        {
            displayDOBPrompt(bus);
        }
        else if (itemNeedsAgeValidation)
        {
            //items need validation and DOB is present - this must be the 2nd cycle through here
            letter = new Letter(CommonLetterIfc.NEXT);
            bus.mail(letter, BusIfc.CURRENT);
        }
        else
        {
            //SKIP - AllowDateOfBirthPromptSkip parameter is true and Skip button is pressed OR items do not need validation
            letter = new Letter(CommonLetterIfc.SKIP);
            bus.mail(letter, BusIfc.CURRENT);
        }
   }

    /**
     * This method displays the Input DOB screen, The screen type is based on the 
     * parameter 'AllowDateOfBirthPromptSkip'
     *
     * @param bus
     */
    public void displayDOBPrompt(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        AgeRestrictionBeanModel model = new AgeRestrictionBeanModel();

        model.setBirthdateValid(true);
        model.setBirthYearValid(true);

        PromptAndResponseModel parModel = new PromptAndResponseModel();
        model.setPromptAndResponseModel(parModel);
       
        Boolean DOBPromptSkip = getDOBPromptSkipParameter(bus);
        if (DOBPromptSkip)
        {
            ui.showScreen(POSUIManagerIfc.ENTER_PICKUP_DOB, model);
        }
        else
        {
            ui.showScreen(POSUIManagerIfc.ENTER_PICKUP_DOB_NO_SKIP, model);
        }
    }
    
    /**
     * Returns a Boolean to indicate if or not the 'AllowDateOfBirthPromptSkip' parameter enabled
     * @param bus
     * @return Boolean Is the 'AllowDateOfBirthPromptSkip' parameter enabled
     */
    private Boolean getDOBPromptSkipParameter(BusIfc bus)
    {
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        Boolean DOBPromptSkip = Boolean.TRUE;
        String skipDOBParameter = "AllowDateOfBirthPromptSkip";
        try
        {
            DOBPromptSkip = pm.getBooleanValue(skipDOBParameter);
        }
        catch (ParameterException e)
        {
            logger.error("Unable to get parameter " + skipDOBParameter + "Error :" + e);
        }
        
        return DOBPromptSkip;
    }
}