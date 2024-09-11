/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/AbstractAmountEnteredAisle.java /main/16 2011/12/05 12:16:21 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    cgreen 05/26/10 - convert to oracle packaging
 *    glwang 01/28/09 - Show invalid item disc scrren instead of invalid
 *                      discount screen when discount amount is greater the
 *                      maximum discount acmount.
 *    nkgaut 01/27/09 - forward port changes for incorrect error message for
 *                      markdown amt discount
 *    acadar 11/02/08 - cleanup
 *    acadar 10/30/08 - cleanup
 *    acadar 10/30/08 - localization of reason codes for item and transaction
 *                      discounts
 * ===========================================================================
  $Log:
   6    360Commerce 1.5         4/25/2007 8:52:18 AM   Anda D. Cadar   I18N
        merge

   5    360Commerce 1.4         1/25/2006 4:10:47 PM   Brett J. Larsen merge
        7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
   4    360Commerce 1.3         1/22/2006 11:45:15 AM  Ron W. Haight   removed
        references to com.ibm.math.BigDecimal
   3    360Commerce 1.2         3/31/2005 4:27:06 PM   Robert Pearse
   2    360Commerce 1.1         3/10/2005 10:19:25 AM  Robert Pearse
   1    360Commerce 1.0         2/11/2005 12:09:18 PM  Robert Pearse
  $:
   4    .v700     1.2.1.0     10/27/2005 14:25:19    Deepanshu       CR 6157:
        Retreived the appropriate dialog property
   3    360Commerce1.2         3/31/2005 15:27:06     Robert Pearse
   2    360Commerce1.1         3/10/2005 10:19:25     Robert Pearse
   1    360Commerce1.0         2/11/2005 12:09:18     Robert Pearse
  $
  Revision 1.8  2004/06/08 17:08:42  dfierling
  @scr 4411 - rollback changes

  Revision 1.7  2004/05/28 19:00:18  dfierling
  @scr 4411 - Fix for misleading error Dialog msg

  Revision 1.6  2004/03/18 23:37:34  dcobb
  @scr 3911 Feature Enhancement: Markdown
  Code review modifications.

  Revision 1.5  2004/03/17 23:03:10  dcobb
  @scr 3911 Feature Enhancement: Markdown
  Code review modifications.

  Revision 1.4  2004/03/15 15:30:40  dcobb
  @scr 3911 Feature Enhancement: Markdown
  Corrected comments.

  Revision 1.3  2004/03/12 23:12:53  dcobb
  @scr 3911 Feature Enhancement: Markdown
  Extracted common code to abstract class.
 */
package oracle.retail.stores.pos.services.pricing;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.ItemDiscountByAmountIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 *   This aisle validates the amount entered to make sure it doesn't
 *   exceed the maximum damage discount amount/percent parameter and
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
 *   <P>
 *   @version $Revision: /main/16 $
 */
public abstract class AbstractAmountEnteredAisle extends PosLaneActionAdapter
{
    /**  Revision Number furnished by CVS. */
    public static final String revisionNumber = "$Revision: /main/16 $";
    /** Item tag */
    public static final String ITEM_TAG = "Item";
    /** Item text */
    public static final String ITEM_TEXT = "item";

    //----------------------------------------------------------------------
    /**
     *   This aisle validates the amount entered to make sure it doesn't
     *   exceed the maximum damage discount amount/percent parameter and
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
     * Returns the assignment specification.
     * @return The assignment
     */
    //----------------------------------------------------------------------
    protected int getAssignment()
    {
        return DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL;
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

    /**
     *   Ensures the discount is valid for all line items. <P>
     *   @param  bus        Service Bus
     *   @param  lineItems  The selected sale return line items
     *   @param  discount   The discount amount
     *   @param  reason     The reason code; null if not required.
     *   @return The dialog to display in the case of an invalid discount.
     *           Null if the discount is valid
     *   @deprecated as of 13.1. Use {@link validateLineItemDiscounts(BusIfc bus, SaleReturnLineItemIfc[] lineItems, CurrencyIfc discount, LocalizedCodeIfc reasonCode)}
    */
    protected String validateLineItemDiscounts(BusIfc bus, SaleReturnLineItemIfc[] lineItems, CurrencyIfc discount, int reasonCode)
    {
        LocalizedCodeIfc reason = DomainGateway.getFactory().getLocalizedCode();
        reason.setCode(Integer.toString(reasonCode));
        return validateLineItemDiscounts(bus, lineItems, discount, reason);

    }


    /**
     *   Ensures the discount is valid for all line items. <P>
     *   @param  bus        Service Bus
     *   @param  lineItems  The selected sale return line items
     *   @param  discount   The discount amount
     *   @param  reason     The reason code; null if not required.
     *   @return The dialog to display in the case of an invalid discount.
     *           Null if the discount is valid
    */
    protected String validateLineItemDiscounts(BusIfc bus, SaleReturnLineItemIfc[] lineItems, CurrencyIfc discount, LocalizedCodeIfc reason)
    {
        PricingCargo cargo = (PricingCargo) bus.getCargo();

        String dialog = null;

        // get maximum discount % allowed from parameter file
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        String percentParameterName = getMaxPercentParameterName();
        BigDecimal maxDiscountPct = getMaximumDiscountPercent(pm, percentParameterName);

        // Count the items actually available for discount
        int totalDiscountableItems = 0;

        HashMap discountHash = cargo.getValidDiscounts();
        discountHash.clear();
        for(int i=0; i < lineItems.length && dialog == null; i++)
        {
            SaleReturnLineItemIfc srli = lineItems[i];
            if (!isEligibleForDiscount(srli))
            {
                continue;
            }
            else
            {
                totalDiscountableItems++;
                discountHash.put(new Integer(i), null);
            }
        }

        // This method will update the discountHash with valid discounts
        // whether or not the prorating is turned on or not
        if (cargo.hasInvalidDiscounts(maxDiscountPct,
                lineItems,
                discount,
                cargo.isOnlyOneDiscountAllowed(pm, logger),
                getAssignment(),
                reason,
                isDamageDiscount(),
                isMarkdown()))
        {
            // Depending on how many line items, show the appropriate
            // dialog when there are some invalid discounts
            if (lineItems.length > 1)
            {
                dialog = PricingCargo.MULTI_ITEM_INVALID_DISC;
            }
            else if (lineItems.length == 1)
            {
                dialog = PricingCargo.INVALID_DISC;
            }
        }

        // Violation of the max percent amount paramter trumps invalid discounts,
        // but we have to validate on the discounts that were considered valid -
        // those that don't cause the price to go negative.
        boolean maxDiscountAmountFailure = false;
        Set keys = discountHash.keySet();
        Integer indexInteger = null;
        int index = -1;
        for (Iterator i = keys.iterator(); !maxDiscountAmountFailure && i.hasNext();)
        {
            indexInteger = (Integer)i.next();
            index = indexInteger.intValue();
            ItemDiscountByAmountIfc currentDiscountStrategy = (ItemDiscountByAmountIfc)discountHash.get(indexInteger);
            SaleReturnLineItemIfc srli = lineItems[index];

            CurrencyIfc maxDiscountAmt = srli.getExtendedSellingPrice().multiply(maxDiscountPct.movePointLeft(2));
            if (currentDiscountStrategy.getDiscountAmount().abs().compareTo(maxDiscountAmt.abs()) > 0 )
            {
                dialog = PricingCargo.INVALID_ITEM_DISC;
                maxDiscountAmountFailure = true;
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
            // get maximum discount % allowed from parameter file
            ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
            BigDecimal maxDiscountPct = getMaximumDiscountPercent(pm, getMaxPercentParameterName());

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

    //----------------------------------------------------------------------
    /**
     *   Returns BigDecimal, the maximum discount % allowed.
     *   @param pm the parameter manager
     *   @param percentParameterName the name of the parameter for maximum discount percent
     *   @return maximum discount %
     */
    //----------------------------------------------------------------------
    protected BigDecimal getMaximumDiscountPercent(ParameterManagerIfc pm, String percentParameterName)
    {
        // retrieve maximum discount % from parameter file
        BigDecimal maximum = new BigDecimal(100.0);  // default
        try
        {
            String s = pm.getStringValue(percentParameterName);
            s.trim();
            maximum = new BigDecimal(s);
            if (logger.isInfoEnabled()) logger.info(
                    "Parameter read: "
                    + percentParameterName
                    + "=[" + maximum + "]");
        }
        catch (ParameterException e)
        {
            logger.error(Util.throwableToString(e));
        }
        return (maximum);
    }

}
