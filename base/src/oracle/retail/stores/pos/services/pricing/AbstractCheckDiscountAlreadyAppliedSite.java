/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/AbstractCheckDiscountAlreadyAppliedSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:10 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - updating deprecated names
 *
 * ===========================================================================
 * $Log:$
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
 This site determines if discounts of this type have already been applied.
 @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 **/
//--------------------------------------------------------------------------
abstract public class AbstractCheckDiscountAlreadyAppliedSite extends PosSiteActionAdapter
{
    /** revision number supplied by version control **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    //----------------------------------------------------------------------
    /**
     Determines if discounts of this type have already been applied. Displays dialog
     if discounts have been applied and one of each type of discount is allowed.
     @param  bus     Service Bus
     **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {        
        // Get required managers                                             
        POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        
        if (discountAlreadyAppliedDialogRequired((PricingCargo)bus.getCargo(),
                (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE)))
        {            
            showDiscountAlreadyAppliedDialog(ui);
        }
        else
        {
            // Send the continue or success letter
            bus.mail(CommonLetterIfc.CONTINUE, BusIfc.CURRENT);
        }
    }
    
    //----------------------------------------------------------------------
    /**
     *   Displays the discount already applied confirmation screen. <P>
     *   @param  ui       The POSUIManager
     */
    //----------------------------------------------------------------------
    protected void showDiscountAlreadyAppliedDialog(POSUIManagerIfc ui)
    {
        // display the invalid discount error screen
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(PricingCargo.DISCOUNT_ALREADY_APPLIED);
        dialogModel.setType(DialogScreensIfc.CONFIRMATION);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, CommonLetterIfc.CONTINUE);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO,CommonLetterIfc.CANCEL);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    //----------------------------------------------------------------------
    /**
     *   Returns true if the Discount Already Applied Dialog should be displayed.
     *   @param cargo The PricingCargo
     *   @param pm The Parameter Manager
     *   @return true if dialog should be displayed, false otherwise
     */
    //----------------------------------------------------------------------
    private boolean discountAlreadyAppliedDialogRequired(PricingCargo cargo,
            ParameterManagerIfc pm)
    {
        boolean showDialogRequired = false;
        
        // If the parameter isn't only one of each discount type allowed,
        // then the warning dialog that a discount has been applied
        // hasn't been displayed yet.
        if (!cargo.isOnlyOneDiscountAllowed(pm, logger))
        {
            // Get the line items from cargo
            SaleReturnLineItemIfc[] lineItems = cargo.getItems();
            if (lineItems == null)
            {
                lineItems = new SaleReturnLineItemIfc[1];
                lineItems[0] = cargo.getItem();
            }
            
            for (int x = 0; x < lineItems.length && !showDialogRequired; x++)
            {    
                SaleReturnLineItemIfc item = lineItems[x];            
                // We only display the dialog if the previous discount is the same type
                // for at least one item
                if(cargo.getDiscountType() == DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT)
                {
                    showDialogRequired = hasSameTypeDiscount(item.getItemDiscountsByAmount());
                }
                else if (cargo.getDiscountType() == DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE)
                {
                    showDialogRequired = hasSameTypeDiscount(item.getItemDiscountsByPercentage());
                }
            }
        }
        return showDialogRequired;
    }

    //----------------------------------------------------------------------
    /**
     *   Returns true if item discount array has a damage item discount.
     *   @param sgyArray The array of ItemDiscountStrategy's to search
     *   @return true if item discount array has a damage item discount.
     */
    //----------------------------------------------------------------------
    abstract public boolean hasSameTypeDiscount(ItemDiscountStrategyIfc[] sgyArray);

}
