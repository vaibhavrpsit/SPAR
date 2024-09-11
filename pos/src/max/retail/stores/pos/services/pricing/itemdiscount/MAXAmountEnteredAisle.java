package max.retail.stores.pos.services.pricing.itemdiscount;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.ItemDiscountByAmountIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.DiscountUtility;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.pricing.AbstractAmountEnteredAisle;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel;

public class MAXAmountEnteredAisle extends AbstractAmountEnteredAisle
{
    private static final long serialVersionUID = 54482482001783217L;

    /** Revision Number furnished by cvs. */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /** Discount tag */
    public static final String DISCOUNT_TAG = "Discount";
    /** Discount text */
    public static final String DISCOUNT_TEXT = "discount";

    //----------------------------------------------------------------------
    /**
     *   This aisle validates the amount entered to make sure it doesn't
     *   exceed the maximum item discount amount/percent parameter and
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
        String dialog = null;
        String letter = CommonLetterIfc.CONTINUE;
        String codeName=null;

        DecimalWithReasonBeanModel beanModel =
            (DecimalWithReasonBeanModel) ((POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE)).getModel(POSUIManagerIfc.ITEM_DISC_AMT);
        String discountAmount = beanModel.getValue().toString();


        SaleReturnLineItemIfc[] lineItems = cargo.getItems();
        if (lineItems == null)
        {
            lineItems = new SaleReturnLineItemIfc[1];
            lineItems[0] = cargo.getItem();
        }

        // Create the discount strategy
        CurrencyIfc discount = DomainGateway.getBaseCurrencyInstance(discountAmount);

        LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
        CodeListIfc rcl = cargo.getLocalizedDiscountAmountCodeList();
        String  reason = beanModel.getSelectedReasonKey();

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
        // Ensure discount doesn't make any individual prices negative or exceeds MaximumItemDiscountAmountPercent
        dialog = validateLineItemDiscounts(bus, lineItems, discount, localizedCode,codeName);

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
     * Returns the parameter name for the maximim discount percent
     * @return PricingCargo.MAX_DISC_PCT
     */
    //----------------------------------------------------------------------
    protected String getMaxPercentParameterName()
    {
        return PricingCargo.MAX_DISC_PCT;
    }

    
    protected String validateLineItemDiscounts(BusIfc bus, SaleReturnLineItemIfc[] lineItems, CurrencyIfc discount, LocalizedCodeIfc reason,String codeName)
    {
        PricingCargo cargo = (PricingCargo)bus.getCargo();
      
         String dialog = null;
         String itemAmtParameter=null;
         int  pmValue = 0;
         CurrencyIfc paramvalue=null;
      //   BigDecimal multiple = new BigDecimal("100.00");
         BigDecimal response1 = new BigDecimal("0.00");
      
  
         ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager("ParameterManager");
         String percentParameterName = getMaxPercentParameterName();
         BigDecimal maxDiscountPct = getMaximumDiscountPercent(pm, percentParameterName);
         itemAmtParameter=codeName.concat("ItemAmtValue");
         ParameterManagerIfc pm2 = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
 	       
 		try {
 			pmValue = pm2.getIntegerValue(itemAmtParameter).intValue();
 		} catch (ParameterException e) {
 			if (logger.isInfoEnabled())
 				logger.info("MAXPercentEnteredAisle.validateLineItemDiscounts(), cannot find itemAmtParameter paremeter.");
 		}
 		
 		response1 = BigDecimal.valueOf(pmValue);
 		
 		
 	    
  
         int totalDiscountableItems = 0;
      
         HashMap discountHash = cargo.getValidDiscounts();
         discountHash.clear();
         for (int i = 0; (i < lineItems.length) && (dialog == null); i++)
      {
           SaleReturnLineItemIfc srli = lineItems[i];
           if (isEligibleForDiscount(srli))
  
        {
             totalDiscountableItems++;
             discountHash.put(new Integer(i), null);
       }
      }
         if (cargo.hasInvalidDiscounts(maxDiscountPct, lineItems, discount, cargo.isOnlyOneDiscountAllowed(pm, logger), getAssignment(), reason, isDamageDiscount(), isMarkdown())) {
  
  
           if (lineItems.length > 1) {
  
             dialog = "MultiItemInvalidDiscount";
  
           } else if (lineItems.length == 1) {
  
             dialog = "InvalidDiscount";
  
        }
      }
         boolean maxDiscountAmountFailure = false;
         Set keys = discountHash.keySet();
         Integer indexInteger = null;
         int index = -1;
         for (Iterator i = keys.iterator(); (!maxDiscountAmountFailure) && (i.hasNext());)
      {
           indexInteger = (Integer)i.next();
           index = indexInteger.intValue();
           ItemDiscountByAmountIfc currentDiscountStrategy = (ItemDiscountByAmountIfc)discountHash.get(indexInteger);
           SaleReturnLineItemIfc srli = lineItems[index];
           BigDecimal response=currentDiscountStrategy.getDiscountAmount().getDecimalValue();
        
           CurrencyIfc maxDiscountAmt = srli.getExtendedSellingPrice().multiply(maxDiscountPct.movePointLeft(2));
           if (currentDiscountStrategy.getDiscountAmount().abs().compareTo(maxDiscountAmt.abs()) > 0 || response.compareTo(response1)>1  )
        {
             dialog = "InvalidItemDiscount";
             maxDiscountAmountFailure = true;
  
        }
      }
         return dialog;
    }
  

}
