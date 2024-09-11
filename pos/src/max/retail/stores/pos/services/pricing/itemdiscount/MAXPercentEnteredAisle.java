
package max.retail.stores.pos.services.pricing.itemdiscount;

import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByPercentageIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.DiscountUtility;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.pricing.AbstractPercentEnteredAisle;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel;
import java.math.BigDecimal;
import java.util.HashMap;

//--------------------------------------------------------------------------
/**
    This aisle validates the percent entered to make sure it doesn't
    exceed the maximum discount amount/percent parameter and
    that there are no items that would have a negative price after the
    discount is applied without a warning or error dialog.
    <P>
    Can show one of three dialogs:
    MultiItemInvalidDiscount - If more than one item has been selected and
        at least one of the item's prices would be negative after the
        discount. Allows you to apply the discount to only the items
        that don't go negative.
    InvalidDiscount - If there was only one item selected and it's
        price would be negative after the discount. Returns to entry screen.
    InvalidItemDiscount - if at least one item would violate the maximum
        discount amount parameter. Returns to entry screen.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class MAXPercentEnteredAisle extends AbstractPercentEnteredAisle
{
    private static final long serialVersionUID = 6917439999001566632L;

    /** Revision Number */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /** Discount tag */
    public static final String DISCOUNT_TAG = "Discount";
    /** Discount text */
    public static final String DISCOUNT_TEXT = "discount";

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
     *   <P>
     *   @param  bus     Service Bus
     */
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        PricingCargo cargo = (PricingCargo) bus.getCargo();
        POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        String dialog = null;
        String letter = CommonLetterIfc.CONTINUE;
        String codeName=null;

        DecimalWithReasonBeanModel beanModel =
            (DecimalWithReasonBeanModel) ui.getModel(POSUIManagerIfc.ITEM_DISC_PCNT);
        BigDecimal  response     = beanModel.getValue();


        SaleReturnLineItemIfc[] lineItems = cargo.getItems();

        if (lineItems == null)
        {
            lineItems = new SaleReturnLineItemIfc[1];
            lineItems[0] = cargo.getItem();
        }

        // Chop off the potential long values caused by BigDecimal.
        if (response.toString().length() > 5)
        {
            BigDecimal scaleOne = new BigDecimal(1);
            response = response.divide(scaleOne, 2);
        }

        LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
        CodeListIfc rcl = cargo.getLocalizedDiscountPercentCodeList();
        String  reason      = beanModel.getSelectedReasonKey();

        if (rcl != null)
        {
            CodeEntryIfc entry = rcl.findListEntryByCode(reason);
            localizedCode.setCode(reason);
            localizedCode.setText(entry.getLocalizedText());
            codeName=entry.getCodeName();
            codeName=codeName.replaceAll("\\s","");

        }
        else
        {
            localizedCode.setCode(CodeConstantsIfc.CODE_UNDEFINED);
        }
        dialog = validateLineItemDiscounts(bus, lineItems, response, localizedCode,codeName);

        // No dialogs required
        if (dialog == null)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
        // show dialog screen
        else
        {
            showDialog(dialog, DISCOUNT_TAG, DISCOUNT_TEXT, bus);
        }
    }

    //----------------------------------------------------------------------
    /**
     * Determines if the item is eligible for the discount
     * @param srli  The line item
     * @return true if the item is eligible for the discount
     */
    //----------------------------------------------------------------------
    protected boolean isEligibleForDiscount(SaleReturnLineItemIfc srli)
    {
    	return DiscountUtility.isDiscountEligible(srli);
    }

    //----------------------------------------------------------------------
    /**
     * Returns the parameter name for the maximum discount percent
     * @return PricingCargo.MAX_DISC_PCT
     */
    //----------------------------------------------------------------------
    protected String getMaxPercentParameterName()
    {
        return PricingCargo.MAX_DISC_PCT;
    }

    //----------------------------------------------------------------------
    /**
     * Clears the discounts by percentage from the line item
     * @param srli    The line item
     */
    //----------------------------------------------------------------------
    protected void clearDiscountsByPercentage(SaleReturnLineItemIfc srli)
    {
        srli.clearItemDiscountsByPercentage(DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL, false);
    }

    /**
     *   Ensures the discount is valid for all line items. <P>
     *   @param  bus       Service Bus
     *   @param  lineItems The selected sale return line items
     *   @param  percent   The discount percent
     *   @param  reason    The discount reason code
     *   @return  The dialog to display in the case of an invalid discount. Null if the discount is valid
    */
    protected String validateLineItemDiscounts(BusIfc bus, SaleReturnLineItemIfc[] lineItems, BigDecimal percent, LocalizedCodeIfc reason,String codeName)
    {
        PricingCargo cargo = (PricingCargo)bus.getCargo();
        ItemDiscountByPercentageIfc sgy = null;
        String dialog = null;
        String itemPercParameter=null;
        int   pmValue = 0;
        BigDecimal response1 = new BigDecimal("0.00");
        ParameterManagerIfc pm  = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);

        // convert beanModel percent value to BigInteger
        // fractional portion of BigDecimal throws off the comparison to parameter
        //BigInteger  percentInt  = percent.movePointRight(2).toBigInteger();
        BigDecimal  percentEntered  = percent.movePointRight(2);

        // get maximum discount % allowed from parameter file
       // BigInteger maxDiscountPct = getMaximumDiscountPercent(pm, getMaxPercentParameterName());
        BigDecimal maxDiscountPct = new BigDecimal(getMaximumDiscountPercent(pm, getMaxPercentParameterName()).toString());

        itemPercParameter=codeName.concat("ItemPercValue");
        ParameterManagerIfc pm2 = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
	       
		try {
			pmValue = pm2.getIntegerValue(itemPercParameter).intValue();
		} catch (ParameterException e) {
			if (logger.isInfoEnabled())
				logger.info("MAXPercentEnteredAisle.validateLineItemDiscounts(), cannot find itemPercParameter paremeter.");
		}
		
		response1 = new BigDecimal(pmValue).setScale(2);
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
            if (percentEntered.compareTo(maxDiscountPct) > 0  || percentEntered.compareTo(response1) > 0)
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



}
