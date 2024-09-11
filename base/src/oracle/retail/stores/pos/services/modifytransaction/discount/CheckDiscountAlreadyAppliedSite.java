/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/discount/CheckDiscountAlreadyAppliedSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:32 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:24 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:06 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:54 PM  Robert Pearse   
 *
 *   Revision 1.3.4.1  2004/11/09 20:34:40  jdeleau
 *   @scr 7661 Make sure the discount already applied screen appears when
 *   the user first does an item discount, followed by a transaction discount
 *   on an item selected other than the one that had the item discount applied.
 *
 *   Revision 1.3  2004/03/22 03:49:28  cdb
 *   @scr 3588 Code Review Updates
 *
 *   Revision 1.2  2004/02/24 16:21:31  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *   Revision 1.1  2004/02/23 21:43:15  cdb
 *   @scr 3588 Added Discount Already Applied dialog that is
 *   sensitive to if it's already been displayed because only
 *   one discount is allowed per transaction.
 *
 *   Revision 1.6  2004/02/20 17:34:57  cdb
 *   @scr 3588 Removed "developmental" log entries from file header.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.discount;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
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
public class CheckDiscountAlreadyAppliedSite extends PosSiteActionAdapter
{
    /** revision number supplied by version control **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     constant for discount already applied confirmation dialog screen
     **/
    public static final String DISCOUNT_ALREADY_APPLIED = "DiscountAlreadyApplied";
    /**
     constant for maximum number of discount allowed parameter name
     **/
    public static final String MAX_DISCOUNTS_ALLOWED = "MaxDiscountsAllowed";
    /**
     constant for parameter value representing only one discount allowed
     **/
    public static final String ONE_TOTAL = "OneTotal";
    /**
     constant for parameter value representing one of each type of discount allowed
     **/
    public static final String ONE_OF_EACH_TYPE = "OneOfEachType";
    
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
        
        if (discountAlreadyAppliedDialogRequired((ModifyTransactionDiscountCargo)bus.getCargo(),
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
        dialogModel.setResourceID(DISCOUNT_ALREADY_APPLIED);
        dialogModel.setType(DialogScreensIfc.CONFIRMATION);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, CommonLetterIfc.CONTINUE);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO,CommonLetterIfc.CANCEL);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    //----------------------------------------------------------------------
    /**
     *   Returns true if the Discount Already Applied Dialog should be displayed.
     *   @param cargo The bus cargo
     *   @param pm The Parameter Manager
     *   @return true if dialog should be displayed
     */
    //----------------------------------------------------------------------
    private boolean discountAlreadyAppliedDialogRequired(ModifyTransactionDiscountCargo cargo,
                                                         ParameterManagerIfc pm)
    {
        boolean showDialogRequired = false;
        
        // If the parameter isn't only one of each discount type allowed,
        // then the warning dialog that a discount has been applied
        // hasn't been displayed yet.
        if (!isOnlyOneDiscountAllowed(pm, logger))
        {
            TransactionDiscountStrategyIfc discount = cargo.getDiscount();
            showDialogRequired = (discount != null);
        }
        // For transaction discounts, there may be items not selected that have
        // item discounts, we need to check if those should be overridden.  CheckOneDiscountSite
        // would not have caught this, because it only checks for selected items.
        else if(cargo.getTransaction() != null)
        {
            // For transaction discounts, all items on the transaction must
            // be checked for discounts, not just the selected item.
            AbstractTransactionLineItemIfc[] lineItems  = cargo.getTransaction().getLineItems();
            for(int i=0; i<lineItems.length; i++)
            {
                if(lineItems[i] instanceof SaleReturnLineItemIfc)
                {
                    ItemDiscountStrategyIfc[] discounts = ((SaleReturnLineItemIfc)lineItems[i]).getItemPrice().getItemDiscounts();
                    if(discounts != null && discounts.length > 0)
                    {
                        showDialogRequired = true;
                        break;
                    }
                }
                else
                {
                    logger.warn("Could not check for discounts already applied, for items of type "+lineItems[i].getClass().getName());
                }
            }
            
        }
        return showDialogRequired;
    }


    //----------------------------------------------------------------------
    /**
     *   Returns true if only one discount is allowed per item.
     *   @param pm The parameter manager
     *   @param logger The logger
     *   @return true if only one discount is allowed per item
     */
    //----------------------------------------------------------------------
    public boolean isOnlyOneDiscountAllowed(ParameterManagerIfc pm, Logger logger)
    {
        boolean isOnlyOneDiscount = false;
        String parameterValue = "";

        // retrieve Maximum Number of Discounts allowed from parameter file
        try
        {
            parameterValue = pm.getStringValue(MAX_DISCOUNTS_ALLOWED);
            parameterValue.trim();
            if (ONE_TOTAL.equals(parameterValue))
            {
                isOnlyOneDiscount = true; 
            }
            else if (!ONE_OF_EACH_TYPE.equals(parameterValue))
            {
                logger.error(
                        "Parameter read: "
                        + "" + MAX_DISCOUNTS_ALLOWED + ""                        + "=["
                        + "" + parameterValue + "]");
            }
        }
        catch (ParameterException e)
        {
            logger.error( "" + Util.throwableToString(e) + "");
        }
        
        return isOnlyOneDiscount;
    }
}
