/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
*	
*   Rev 1.3     Apr 10, 2017        Nitika Arora            Changes for displaying the gift card no on the tender line item.
*	Rev 1.2     Feb 18, 2017		Ashish Yadav			Coupon Description
*	Rev 1.1     Dec 22, 2016		Ashish Yadav			Credit Card FES
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */



package max.retail.stores.pos.ui.beans;

import max.retail.stores.domain.tender.MAXTenderCharge;
import max.retail.stores.domain.tender.MAXTenderLoyaltyPoints;
import max.retail.stores.domain.tender.MAXTenderPurchaseOrder;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.tender.AuthorizableTenderIfc;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderCash;
import oracle.retail.stores.domain.tender.TenderCashIfc;
import oracle.retail.stores.domain.tender.TenderCheckIfc;
import oracle.retail.stores.domain.tender.TenderCoupon;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.tender.TenderTravelersCheckIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.TenderLineItemRenderer;

public class MAXTenderLineItemRenderer extends TenderLineItemRenderer {

	public void setData(Object value)
    {
        if (!(value instanceof TenderLineItemIfc))
        {
            throw new ClassCastException("Expected a TenderLineItemIfc");
        }

        TenderLineItemIfc lineItem = (TenderLineItemIfc) value;

        String tempDesc = lineItem.getTypeDescriptorString();
        labels[TYPE].setText(UIUtilities.retrieveCommonText(tempDesc));
        if (lineItem instanceof TenderCheckIfc)
		{
            labels[NUM].setText(getNumberSnippet(((TenderCheckIfc)lineItem).getAccountNumber()));
		}
        else if (lineItem instanceof TenderGiftCertificateIfc)
		{
            labels[NUM].setText(getNumberSnippet(((TenderGiftCertificateIfc)lineItem).getGiftCertificateNumber()));
		}
        else if (lineItem instanceof TenderStoreCreditIfc)
		{
            labels[NUM].setText(getNumberSnippet(((TenderStoreCreditIfc)lineItem).getStoreCreditID()));
		}
        else if (lineItem instanceof MAXTenderLoyaltyPoints)
		{
            labels[NUM].setText(((MAXTenderLoyaltyPoints)lineItem).getLoyaltyCardNumber());
		}
        // added by atul shukla
        // Changes starts for Rev 1.1 (Ashish : Credit Card)
        else if (lineItem instanceof MAXTenderCharge)
		{
        	EncipheredCardDataIfc cardData = ((MAXTenderCharge) lineItem).getEncipheredCardData();
			String last4Digits = cardData != null ? cardData.getLastFourAcctNumber() : "";
			if (((MAXTenderCharge) lineItem).getCardType().startsWith("L-")) {
				labels[TYPE].setText(UIUtilities.retrieveCommonText("Loyalty"));
			labels[NUM].setText(last4Digits);
			} else if (((MAXTenderCharge) lineItem).getCardType().startsWith("C-")) {
				labels[TYPE].setText(UIUtilities.retrieveCommonText("Cash Back"));
				labels[NUM].setText("");
			} else if (((MAXTenderCharge) lineItem).getCardType().startsWith("SODEXO")) {
				labels[TYPE].setText(UIUtilities.retrieveCommonText("Sodexo"));
				labels[NUM].setText(last4Digits);
			}
			else {
				labels[NUM].setText(last4Digits);
			}
		}
        // Changes starts for Rev 1.2 (Ashish : Bug Fix)
        else if (lineItem instanceof TenderCoupon)
		{
        	
			labels[NUM].setText(((TenderCoupon) lineItem).getCouponNumber());
		}
        else if (lineItem instanceof MAXTenderPurchaseOrder)
		{
        	
			labels[NUM].setText(((MAXTenderPurchaseOrder) lineItem).getAgencyName());
		}
        else if (lineItem instanceof TenderCash)
		{
        	
			labels[NUM].setText("");
		}
        else
        {
            labels[NUM].setText(new String(lineItem.getNumber()));
        }
     // Changes starts for Rev 1.2 (Ashish : Bug Fix)
        
     // Changes starts for Rev 1.1 (Ashish : Credit Card)
        /*else
        {
        	// changes starts for code merging(commenting below line as per MAX)
            //labels[NUM].setText(lineItem.getNumber());
        	labels[NUM].setText(last4Digits);
        }*/

        if (lineItem.getAmountTender().signum() == CurrencyIfc.ZERO)
        {
            labels[AMOUNT].setText("");
        }
        else
        {
            labels[AMOUNT].setText(lineItem.getAmountTender().toGroupFormattedString(getLocale()));
        }

        // possibly set alternate tender field with currency and amount
        if ((lineItem instanceof TenderCashIfc ||
             lineItem instanceof TenderTravelersCheckIfc ||
             lineItem instanceof TenderCheckIfc)
            && ((TenderAlternateCurrencyIfc)lineItem).getAlternateCurrencyTendered() != null)
        {
            CurrencyIfc tliAlt = ((TenderAlternateCurrencyIfc) lineItem).getAlternateCurrencyTendered();
            String currencyString = tliAlt.toFormattedString(getLocale());
            String[] parms = {tliAlt.getDescription(), currencyString};
            String finalText = LocaleUtilities.formatComplexMessage(altText,parms);
            labels[ALT].setText(finalText);
        }
        else   // reset alternate field to empty string...
        {
            labels[ALT].setText("");
        }

        // if authorizable and authorized
        if (lineItem instanceof AuthorizableTenderIfc &&
            ((AuthorizableTenderIfc)lineItem).getAuthorizationResponse() != null)
        {
            labels[AUTH].setText(authText);
        }
        else
        {
            labels[AUTH].setText("");
        }
    }

}
