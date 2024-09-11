/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/AbstractPercentEnteredAisle.java /main/17 2011/12/05 12:16:21 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    cgreen 11/30/11 - XbranchMerge cgreene_fix_subtotals_decimal_error from
 *                      rgbustores_13.4x_generic_branch
 *    cgreen 11/28/11 - correctly initiale BigDecimals to constant values
 *    dwfung 09/14/10 - fixed overriding discount flow when allow only 1 discount
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/27/10 - updating deprecated names
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    atirke 01/28/09 - forward porting bug#7828237
 *    acadar 10/31/08 - minor fixes for manual discounts localization
 *    acadar 10/30/08 - localization of reason codes for item and transaction
 *                      discounts
 * ===========================================================================
     $Log:
      5    360Commerce 1.4         4/24/2008 3:40:25 PM   Alan N. Sinton  CR
           29873: Set the reason code on the ItemDiscountByPercentageIfc for
           damage discounted items.  Code changes reviewed by Brett Larsen.
      4    360Commerce 1.3         1/22/2006 11:45:15 AM  Ron W. Haight
           removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:27:07 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:19:27 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:09:20 PM  Robert Pearse
     $
     Revision 1.4.4.1  2004/11/12 17:27:14  cdb
     @scr 7693 Updated to make app more robust DB is missing required data.

     Revision 1.4  2004/03/22 18:35:05  cdb
     @scr 3588 Corrected some javadoc

     Revision 1.3  2004/03/18 23:37:34  dcobb
     @scr 3911 Feature Enhancement: Markdown
     Code review modifications.

     Revision 1.2  2004/03/15 15:30:40  dcobb
     @scr 3911 Feature Enhancement: Markdown
     Corrected comments.

     Revision 1.1  2004/03/12 23:12:53  dcobb
     @scr 3911 Feature Enhancement: Markdown
     Extracted common code to abstract class.

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.pricing;

// java imports
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Locale;

import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByPercentageIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import java.math.BigDecimal;

//----------------------------------------------------------------------
/**
 *   This aisle validates the percent entered to make sure it doesn't
 *   exceed the maximum discount amount/percent parameter and
 *   that there are no items that would have a negative price after the
 *   discount is applied without a warning or error dialog.
 *   <P>
 *   Can show one of three dialogs:
 *   MultiItemInvalidDiscount - If more than one item has been selected and
 *      at least one of the item's prices would be negative after the
 *      discount. Allows you to apply the discount to only the items
 *      that don't go negative.
 *   InvalidDiscount - If there was only one item selected and it's
 *      price would be negative after the discount. Returns to entry screen.
 *   InvalidItemDiscount - if at least one item would violate the maximum
 *      discount amount parameter. Returns to entry screen.
    @version $Revision: /main/17 $
**/
//--------------------------------------------------------------------------
public abstract class AbstractPercentEnteredAisle extends PosLaneActionAdapter
{
    /** Revision Number */
    public static final String revisionNumber = "$Revision: /main/17 $";
    /** Item tag */
    public static final String ITEM_TAG = "Item";
    /** Item text */
    public static final String ITEM_TEXT = "item";


    //----------------------------------------------------------------------
    /**
     *   This aisle validates the percent entered to make sure it doesn't
     *   exceed the maximum discount amount/percent parameter and
     *   that there are no items that would have a negative price after the
     *   discount is applied without a warning or error dialog.
     *   <P>
     *   Can show one of three dialogs:
     *   MultiItemInvalidDiscount - If more than one item has been selected and
     *      at least one of the item's prices would be negative after the
     *      discount. Allows you to apply the discount to only the items
     *      that don't go negative.
     *   InvalidDiscount - If there was only one item selected and it's
     *      price would be negative after the discount. Returns to entry screen.
     *   InvalidItemDiscount - if at least one item would violate the maximum
     *      discount amount/percent parameter. Returns to entry screen.
     *   <P>
     *   Mails the Contiunue letter if no dialog is shown.
     *   <P>
     *   @param  bus     Service Bus
     */
    //----------------------------------------------------------------------
    public abstract void traverse(BusIfc bus);

    //----------------------------------------------------------------------
    /**
     * Determines if the item is eligible for the discount
     * @param srli  The line item
     * @return true if the item is eligible for the discount
     */
    //----------------------------------------------------------------------
    protected abstract boolean isEligibleForDiscount(SaleReturnLineItemIfc srli);

    //----------------------------------------------------------------------
    /**
     * Returns the parameter name
     * @return The name of the parameter for the maximum percent
     */
    //----------------------------------------------------------------------
    protected abstract String getMaxPercentParameterName();

    //----------------------------------------------------------------------
    /**
     * Clears the discounts by percentage from the line item
     * @param srli  The line item
     */
    //----------------------------------------------------------------------
    protected void clearDiscountsByPercentage(SaleReturnLineItemIfc srli)
    {
        srli.clearItemDiscountsByPercentage(DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL, false);
    }

    //----------------------------------------------------------------------
    /**
     * Determines if this is a damage discount.
     * @return true is the discount is a damage discount
     */
    //----------------------------------------------------------------------
    protected boolean isDamageDiscount()
    {
        return false;
    }

    //----------------------------------------------------------------------
    /**
     * Determines if this is a markdown.
     * @return true is the discount is a markdown
     */
    //----------------------------------------------------------------------
    protected boolean isMarkdown()
    {
        return false;
    }

    //----------------------------------------------------------------------
    /**
     * Determines if this is an employee discount.
     * @return true is the discount is an employee discount
     */
    //----------------------------------------------------------------------
    protected boolean isEmployeeDiscount()
    {
        return false;
    }


    /**
     * Returns the reason code list for percentage discount.
     * @param cargo The Pricing Cargo containing the code lists
     * @return the reason code list
    */
    protected CodeListIfc getLocalizedPercentCodeList(PricingCargo cargo)
    {
        return cargo.getLocalizedDiscountPercentCodeList();
    }

    //----------------------------------------------------------------------
    /**
     * Determines the accounting method.
     * @return the accounting method
     */
    //----------------------------------------------------------------------
    protected int getAccountingMethod()
    {
        return DiscountRuleConstantsIfc.ACCOUNTING_METHOD_DISCOUNT;
    }


    /**
     *   Ensures the discount is valid for all line items. <P>
     *   @param  bus       Service Bus
     *   @param  lineItems The selected sale return line items
     *   @param  percent   The discount percent
     *   @param  reason    The discount reason code
     *   @return  The dialog to display in the case of an invalid discount. Null if the discount is valid
    */
    protected String validateLineItemDiscounts(BusIfc bus, SaleReturnLineItemIfc[] lineItems, BigDecimal percent, LocalizedCodeIfc reason)
    {
        PricingCargo cargo = (PricingCargo)bus.getCargo();
        ItemDiscountByPercentageIfc sgy = null;
        String dialog = null;
        ParameterManagerIfc pm  = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);

        // convert beanModel percent value to BigInteger
        // fractional portion of BigDecimal throws off the comparison to parameter
        //BigInteger  percentInt  = percent.movePointRight(2).toBigInteger();
        BigDecimal  percentEntered  = percent.movePointRight(2);

        // get maximum discount % allowed from parameter file
       // BigInteger maxDiscountPct = getMaximumDiscountPercent(pm, getMaxPercentParameterName());
        BigDecimal maxDiscountPct = new BigDecimal(getMaximumDiscountPercent(pm, getMaxPercentParameterName()).toString());


        HashMap discountHash = cargo.getValidDiscounts();
        discountHash.clear();
        boolean hasInvalidDiscounts = false;
        for(int i=0; i < lineItems.length && dialog == null; i++)
        {
            SaleReturnLineItemIfc srli = lineItems[i];

            // If the item is not discount eligible, go on to
            // next item.
            if (!(isEligibleForDiscount(srli)))
            {
                continue;
            }

            // Ensure the discount doesn't exceed the MaximumItemDiscountAmountPercent parameter
            if (percentEntered.compareTo(maxDiscountPct) > 0)
            {
                dialog = PricingCargo.INVALID_ITEM_DISC;
            }
            else
            {
                sgy = createDiscountStrategy(cargo, percent, reason);
                //check to see if adding this discount will make the item's price go
                //negative (or positive if it is a return item)
                SaleReturnLineItemIfc clone = (SaleReturnLineItemIfc)srli.clone();
                if (cargo.isOnlyOneDiscountAllowed(pm, logger))
                {
                    cargo.removeAllManualDiscounts(clone, null);
                }
                else
                {
                    clearDiscountsByPercentage(clone);
                }
                clone.addItemDiscount(sgy);
                clone.calculateLineItemPrice();

                if ((clone.isSaleLineItem() &&
                        clone.getExtendedDiscountedSellingPrice().signum() < 0) ||
                        (clone.isReturnLineItem() &&
                                clone.getExtendedDiscountedSellingPrice().signum() > 0))
                {
                    hasInvalidDiscounts = true;
                }
                else
                {
                    discountHash.put(new Integer(i), sgy);
                }
            }
        }

        //check to see if adding this discount will make the item's price go
        //negative (or positive if it is a return item)
        if (dialog == null && hasInvalidDiscounts)
        {
            if (lineItems.length > 1)
            {
                dialog = PricingCargo.MULTI_ITEM_INVALID_DISC;
            }
            else if (lineItems.length == 1)
            {
                dialog = PricingCargo.INVALID_DISC;
            }
        }

        return dialog;
    }


    //----------------------------------------------------------------------
    /**
     *   Displays the selected dialog screen. <P>
     *   @param  dialog       The selected dialog
     *   @param  discountTag  The tag for the discount argument
     *   @param  discountText The default text for the discount argument
     *   @param  bus          The sevice bus
     */
    //----------------------------------------------------------------------
    protected void showDialog(String dialog, String discountTag, String discountText, BusIfc bus)
    {
        POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        // Get the locale for the user interface.
        Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

        // show multiple selection with some invalid discounts confirmation dialog screen
        if (dialog.equals(PricingCargo.MULTI_ITEM_INVALID_DISC))
        {
            String arg = utility.retrieveCommonText(discountTag, discountText, uiLocale);
            // display the invalid discount error screen
            String[] msg = new String[4];
            msg[0] = msg[1] = msg[2] = msg[3] = arg;
            showMultiInvalidDiscountDialog(ui, msg);
        }
        // show no valid discounts error dialog screen
        else if (dialog.equals(PricingCargo.INVALID_DISC))
        {
            // display the invalid discount error screen
            String[] msg = new String[1];
            msg[0] = utility.retrieveCommonText(discountTag, discountText, uiLocale);
            showInvalidDiscountDialog(ui, msg);
        }
        // show amount exceeds maximum amount error dialog screen
        else if (dialog.equals(PricingCargo.INVALID_ITEM_DISC))
        {
            ParameterManagerIfc pm  = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            // get maximum discount % allowed from parameter file
            BigInteger maxDiscountPct = getMaximumDiscountPercent(pm, getMaxPercentParameterName());

            // Build and display message that the discount is out of range
            String[] msg = new String[3];
            msg[0] = utility.retrieveCommonText(discountTag, discountText, uiLocale);
            msg[1] = maxDiscountPct.toString();
            msg[2] = utility.retrieveCommonText(ITEM_TAG, ITEM_TEXT, uiLocale).toLowerCase(uiLocale);
            showInvalidItemDiscountDialog(ui, msg);
        }
        // Unexpected problem
        else
        {
            logger.error("Unexpected dialog requested: " + dialog);
        }
    }

    //----------------------------------------------------------------------
    /**
     *   Displays the multiple selection with some invalid discounts confirmation dialog screen. <P>
     *   @param  ui       The POSUIManager
     *   @param  msg      The string array representing the arguments for
     *                    the dialog
     */
    //----------------------------------------------------------------------
    protected void showMultiInvalidDiscountDialog(POSUIManagerIfc ui, String[] msg)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(PricingCargo.MULTI_ITEM_INVALID_DISC);
        dialogModel.setType(DialogScreensIfc.CONFIRMATION);
        dialogModel.setArgs(msg);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, CommonLetterIfc.CONTINUE);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO,CommonLetterIfc.CANCEL);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    //----------------------------------------------------------------------
    /**
     *   Displays the no valid discounts error dialog screen. <P>
     *   @param  ui       The POSUIManager
     *   @param  msg      The string array representing the arguments for
     *                    the dialog
     */
    //----------------------------------------------------------------------
    protected void showInvalidDiscountDialog(POSUIManagerIfc ui, String[] msg)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(PricingCargo.INVALID_DISC);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(msg);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    //----------------------------------------------------------------------
    /**
     *   Displays the amount exceeds maximum amount error dialog screen. <P>
     *   @param  ui       The POSUIManager
     *   @param  msg      The string array representing the arguments for
     *                    the dialog
     */
    //----------------------------------------------------------------------
    protected void showInvalidItemDiscountDialog(POSUIManagerIfc ui, String[] msg)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(PricingCargo.INVALID_ITEM_DISC);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(msg);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }


    /**
     *   Creates discount
     *   @param  cargo   The pricing cargo
     *   @param  percent The discount percent
     *   @param  reason  The reason code
     *   @return the discount
    */
    protected ItemDiscountByPercentageIfc createDiscountStrategy(PricingCargo cargo, BigDecimal percent, LocalizedCodeIfc reason)
    {
        ItemDiscountByPercentageIfc sgy = DomainGateway.getFactory().getItemDiscountByPercentageInstance();
        sgy.setReason(reason);
        //for manual discounts the discount name and reason code description are the same
        sgy.setLocalizedNames(reason.getText());
        sgy.setDiscountRate(percent);
        sgy.setMarkdownFlag(isMarkdown());
        sgy.setDamageDiscount(isDamageDiscount());
        sgy.setAccountingMethod(getAccountingMethod());

        sgy.setAssignmentBasis(DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
        if (isEmployeeDiscount())
        {
            sgy.setAssignmentBasis(DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE);
            sgy.setDiscountEmployee(cargo.getEmployeeDiscountID());
        }

        return sgy;
    }

    //----------------------------------------------------------------------
    /**
     *   Returns a BigInteger, the maximum discount % allowed from
     *   the parameter file. <P>
     *   @param pm The parameter manager
     *   @param percentParameterName The name of the maximum discount percent parameter
     *   @return maximum discount percent allowed as BigInteger
     */
    //----------------------------------------------------------------------
    protected BigInteger getMaximumDiscountPercent(ParameterManagerIfc pm,
            String percentParameterName)
    {
        BigInteger maximum = BigInteger.valueOf(100);
        try
        {
            String s = pm.getStringValue(percentParameterName);
            s.trim();
            maximum = new BigInteger(s);
            if (logger.isInfoEnabled())
            {
                StringBuilder message = new StringBuilder("Parameter read: ")
                    .append(percentParameterName)
                    .append("=[")
                    .append(maximum)
                    .append("]");
                logger.info(message.toString());
            }
        }
        catch (ParameterException e)
        {
            logger.error(Util.throwableToString(e));
        }
        return(maximum);
    }

}
