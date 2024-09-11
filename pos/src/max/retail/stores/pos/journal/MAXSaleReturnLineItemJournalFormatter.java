/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2016 MAX Hyper Market Inc.    All Rights Reserved.
*
*	Rev 1.1			Jun 19, 2017		Jyoti Yadav		EJ is saving with VAT as tax type
*	Rev	1.0		 	01feb,2017			Hitesh.Dua			
*	Bug_fix: Unexpected error is displayed when click on Next once quantity of weighted item is entered on Unit of measure screen.
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 **/
package max.retail.stores.pos.journal;

import java.math.BigDecimal;
import java.util.Locale;
import max.retail.stores.common.data.MAXTAXUtils;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItem;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItem;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.GiftCertificateItem;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.journal.SaleReturnLineItemJournalFormatter;
import max.retail.stores.domain.stock.MAXPLUItemIfc;

public class MAXSaleReturnLineItemJournalFormatter extends
		SaleReturnLineItemJournalFormatter {
	// Changes starts ofr code merging(added below variable in order to make it max version)
	//commented to take parent saleReturnLineItem value for rev 1.0
			//MAXSaleReturnLineItemIfc saleReturnLineItem;
			// Changes ends
	public String toJournalString()
    {
		
        Locale locale = getEJournalLocale();
        StringBuffer strResult = new StringBuffer();
        ItemPriceIfc ip = saleReturnLineItem.getItemPrice();

        int taxMode = ip.getItemTax().getTaxMode();
        int taxScope = ip.getItemTax().getTaxScope();

        // Item number
        CurrencyIfc itemPrice = ip.getExtendedSellingPrice();        
        int signum = itemPrice.getDecimalValue().signum();
        String priceString  = itemPrice.toGroupFormattedString(locale);

        //This EOL is responsible for separating each line item
        //Do not remove it for the sake of removing spare line
        //    between header and line items.
        strResult.append(Util.EOL);
        strResult.append("ITEM: ");
        strResult.append(saleReturnLineItem.getPLUItem().getItemID());
        if(saleReturnLineItem.getPLUItem().getItemID() != null)
        {
            strResult.append(Util.SPACES.substring(
                            saleReturnLineItem.getPLUItem().getItemID().length(),
                            SaleReturnLineItemIfc.ITEM_NUMBER_LENGTH));
        }

        // Assume quantity is in decimals.
        BigDecimal quantity = saleReturnLineItem.getItemQuantityDecimal();
        quantity = quantity.setScale(3);
        if (!saleReturnLineItem.isUnitOfMeasureItem())
        {
            // However, if we aren't a UoM item, display quantity as an integer
            if (quantity.intValue() == quantity.doubleValue())
            {
                quantity = quantity.setScale(0);
            }
        }
        String quantityString = quantity.toString();

        // price -part 1
        int whiteSpace = SaleReturnLineItemIfc.ITEM_PRICE_LENGTH;
        if (quantityString.startsWith("-"))
        {
            quantityString = quantityString.replace('-', '(');
            quantityString = quantityString + ")";
            if (signum > CurrencyIfc.NEGATIVE)
            {
                priceString  = itemPrice.negate().toGroupFormattedString(locale);
            }
        }

        // price -part 2
        if (priceString.length() < whiteSpace)
        {
            strResult.append(Util.SPACES.substring(priceString.length(), whiteSpace));
        }
        strResult.append(priceString);

        // Tax Mode
        String taxFlag = getTaxFlag(taxMode);
        strResult.append(" ");
        strResult.append(taxFlag);

        // Item description
        strResult.append(Util.EOL);
        strResult.append("  ");
        strResult.append(saleReturnLineItem.getPLUItem().getDescription(locale));

        // Item size
        if (!Util.isEmpty(saleReturnLineItem.getItemSizeCode()))
        {
            strResult.append(Util.EOL);
            strResult.append("  Size: ");
            strResult.append(saleReturnLineItem.getItemSizeCode());
        }

        // Item Quantity and Unit Price
        strResult.append(Util.EOL);
        strResult.append("  Qty: ");
        strResult.append(quantityString);
        strResult.append(" @ ");
        String sellingPriceString = ip.getSellingPrice().toGroupFormattedString(locale);

        
        strResult.append(sellingPriceString);     
        
        //India Localization changes starts here
        strResult.append(Util.EOL);
        strResult.append("  MRP: ");
        if(!(saleReturnLineItem.getPLUItem() instanceof GiftCertificateItem))
        	strResult.append(((MAXPLUItemIfc) saleReturnLineItem.getPLUItem()).getMaximumRetailPrice().getStringValue());
        //India Localization changes ends here        
        
        // If we have a price override, use the override marker.
        String overrideMarker = "";
        if (ip.isPriceOverride())
        {
            overrideMarker = SaleReturnLineItem.OVERRIDE_MARKER;
        }
        strResult.append(overrideMarker);

        // Item serial number
        if (!Util.isEmpty(saleReturnLineItem.getItemSerial()))
        {
            strResult.append(Util.EOL);
            strResult.append("  Serial Number: ");
            strResult.append(Util.EOL);
            strResult.append("  ");
            strResult.append(saleReturnLineItem.getItemSerial());
        }

        // if the PLUItem is a GiftCardPLUItem journal gift card information
        if (saleReturnLineItem.getPLUItem() instanceof GiftCardPLUItemIfc)
        {
            strResult.append(((GiftCardPLUItemIfc) saleReturnLineItem.getPLUItem()).getGiftCard().toJournalString());
        }

        // journal non-standard tax
        journalTax(strResult, ip, taxMode, taxScope);

        // Journal return items specific info
        if (saleReturnLineItem.isReturnLineItem())
        {
            // journal original Trans.
            ReturnItemIfc returnItem = saleReturnLineItem.getReturnItem();
            if (returnItem.getOriginalTransactionID() != null)
            {
                strResult.append(Util.EOL);
                strResult.append("  Orig. Trans: ");
                strResult.append(returnItem.getOriginalTransactionID().getTransactionIDString());
                if(returnItem.getOriginalTransactionBusinessDate() != null)
                {
                	strResult.append(" ");
                	strResult.append(returnItem.getOriginalTransactionBusinessDate().toFormattedString(locale));
                }
                strResult.append(Util.EOL);
                strResult.append("  Retrieved: ");
                if (returnItem.isFromRetrievedTransaction())
                {
                    strResult.append("Yes");
                }
                else
                {
                    strResult.append("No");
                }

                if (returnItem.isFromGiftReceipt())
                {
                    strResult.append(Util.EOL);
                    strResult.append("  Gift Receipt");
                }

            }
            strResult.append(Util.EOL).append("  Item Returned");
        }

        if (saleReturnLineItem.getRegistry() != null)
        {
            strResult.append(Util.EOL);
            strResult.append("  Gift Reg.: ");
            strResult.append(saleReturnLineItem.getRegistry().getID());
        }

        // if sales associate modified, write it
        if (saleReturnLineItem.getSalesAssociateModifiedFlag() && saleReturnLineItem.getSalesAssociate() != null)
        {
            strResult.append(Util.EOL);
            strResult.append("  Sales Assoc. ");
            strResult.append(saleReturnLineItem.getSalesAssociate().getEmployeeID());
        }
        else
        {
            ReturnItemIfc ri = saleReturnLineItem.getReturnItem();
            // if return, get sales associate
            if (saleReturnLineItem.getItemQuantityDecimal().signum() < 0 && ri != null && ri.getSalesAssociate() != null)
            {
                strResult.append(Util.EOL);
                strResult.append("  Sales Assoc. ");
                strResult.append(ri.getSalesAssociate().getEmployeeID());
            }
        }
        // pass back result

        return (strResult.toString());
    }
	/*Change for Rev 1.1: Start*/
	protected String getTaxFlag(int taxMode){
		String taxFlag = new String("T");
		if(Gateway.getBooleanProperty("application","GSTEnabled", true)){
			if(saleReturnLineItem instanceof MAXSaleReturnLineItem){
				taxFlag = MAXTAXUtils.getLineItemTaxType((MAXSaleReturnLineItem)saleReturnLineItem);	
			}
		}else{
		     if ((taxMode == 0) && (!(this.saleReturnLineItem.getPLUItem().getTaxable())))
		     {
		       taxFlag = oracle.retail.stores.domain.tax.TaxIfc.TAX_MODE_CHAR[6];
		     }
		     else
		     {
		       taxFlag = oracle.retail.stores.domain.tax.TaxIfc.TAX_MODE_CHAR[taxMode];
		     }
		}
	     return taxFlag;
	}
	/*Change for Rev 1.1: End*/
}
