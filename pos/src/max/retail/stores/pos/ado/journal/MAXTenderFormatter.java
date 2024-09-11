/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
*   Rev 1.1  28/June/2013	Jyoti Rawal, Approval code going null in EJ
*  	Rev 1.0  01/June/2013	Jyoti Rawal, Initial Draft: Changes for Bug 6090 :Incorrect EJ of the transaction in which Hire
*  	Purchase is used as a tender type 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.ado.journal;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Map;

import max.retail.stores.domain.tender.MAXTenderChargeIfc;
import max.retail.stores.pos.ado.tender.MAXTenderConstants;
import max.retail.stores.pos.ado.tender.MAXTenderCreditADO;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditConstantsIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ado.journal.JournalConstants;
import oracle.retail.stores.pos.ado.journal.JournalTemplateIfc;
import oracle.retail.stores.pos.ado.journal.JournalableADOIfc;
import oracle.retail.stores.pos.ado.journal.TenderFormatter;
import oracle.retail.stores.pos.ado.tender.AuthorizableADOIfc;
import oracle.retail.stores.pos.ado.tender.CreditTypeEnum;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderCashADO;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderCreditADO;
import oracle.retail.stores.pos.ado.tender.TenderStoreCreditADO;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;


public class MAXTenderFormatter extends TenderFormatter {
	// ----------------------------------------------------------------------

	
	protected void formatPurchaseOrderInfo(JournalTemplateIfc template, JournalableADOIfc journalable, StringBuffer sb)
    {
        Map memento = journalable.getJournalMemento();
        String agencyName = (String) memento.get(TenderConstants.AGENCY_NAME);

        if (agencyName == null)
        {
            agencyName = "";
        }
		//Rev 1.1 start
        for(int i = 0;i<sb.length();i++){
        	if(sb!=null && sb.indexOf("PurchaseOrder")!= -1){
        		sb.replace(0, 14, "HirePurchase");
        	}
        }
		//Rev 1.1 end
        sb.append(Util.EOL).append(agencyName);
        
        String faceValueAmount = (String) memento.get(TenderConstants.FACE_VALUE_AMOUNT);
        CurrencyIfc amt = DomainGateway.getBaseCurrencyInstance();
        amt.setStringValue(faceValueAmount);
        String formattedFaceValueAmount = amt.toGroupFormattedString(getJournalLocale());
        sb.append(Util.EOL).append("  PO Tender Amount: ").append(formattedFaceValueAmount);

//        sb.append(Util.EOL).append("       # ").append(((String) memento.get(TenderConstants.NUMBER)));
        sb.append(Util.EOL).append("  Approval Code: ").append(((String) memento.get(MAXTenderConstants.APPROVAL_CODE))); //Rev 1.1

        // journal the transaction status
        sb.append(Util.EOL).append("Transaction Status: ");
        sb.append((String) memento.get(TenderConstants.TAXABLE_STATUS)).append(Util.EOL);
    }

	@Override
	protected void formatGeneralTenderInfo(JournalTemplateIfc template, TenderADOIfc tender, StringBuilder sb)
	    {
	        Map journalMemento = tender.getJournalMemento();
	        TenderStoreCreditADO tscADO = null;
	        TenderStoreCreditIfc tsc = null;
	        String cardType = null;
	        String descriptor = null;
	        //System.out.println("formatGeneralTenderInfo :"+journalMemento);
	        if (tender instanceof MAXTenderCreditADO)
	        {
	        	MAXTenderCreditADO creditADO = (MAXTenderCreditADO) tender;
	        	MAXTenderChargeIfc tenderCharge = (MAXTenderChargeIfc) creditADO.toLegacy();
	        	cardType = tenderCharge.getCardType(); 	        	
	        }
	        if (tender.getTenderType() == TenderTypeEnum.STORE_CREDIT)
	        {
	            tscADO = (TenderStoreCreditADO) tender;
	            tsc = (TenderStoreCreditIfc) tscADO.toLegacy();
	        }
	        if (tsc != null && tsc.getState() == TenderStoreCreditConstantsIfc.ISSUE)
	        {
	        }
	        else if (tender.getTenderType() == TenderTypeEnum.CASH &&
	                 tenderAction.equals(ISSUED)  &&
	                 !((TenderCashADO)tender).isRefundCash())
	        {
	        }
	        else
	        {// get tender descriptor
	        	if (cardType != null && cardType.startsWith("C-"))
		        {
	        		descriptor = "Cash Back Amount";
	        		 if (this.tenderAction.equals("Tendered"))
	        		 {
	        			 sb.append(template.getEndOfLine()).append(descriptor);
	        		 }
	        		 else
	        		 {
	        			 sb.append(template.getEndOfLine()).append(descriptor).append(" ").append(this.tenderAction);
	        		 }
		        } 
	        	else
	        	{
	        		descriptor = (String) journalMemento.get(JournalConstants.DESCRIPTOR);
	        	    sb.append(template.getEndOfLine()).append(descriptor).append(" ").append(this.tenderAction);
	        	}	            
	            String amountString = tender.getAmount().toGroupFormattedString(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
	            int offset =-1;
	            if (cardType != null && cardType.startsWith("C-") && this.tenderAction.equals("Tendered")) //CSHBK
	            {
	            	 offset = descriptor.length() + amountString.length();
	            }
	            else
	            {
	            	 offset = descriptor.length() + this.tenderAction.length() + amountString.length();
	            }
	            int numSpaces = 37;
	            if (offset < numSpaces)
	            {
	                sb.append(Util.SPACES.substring(offset, numSpaces));
	            }
	            sb.append(amountString);
	
	            if (tender.toLegacy() instanceof TenderAlternateCurrencyIfc)
	            {
	                CurrencyIfc foreignAmount = ((TenderAlternateCurrencyIfc)tender.toLegacy()).getAlternateCurrencyTendered();
	                if (foreignAmount != null)
	                {
	                    sb.append(template.getEndOfLine()).append("  ").append(foreignAmount.getDescription());
	                    sb.append(" Amt. Recd.: ").append(foreignAmount.toGroupFormattedString(getJournalLocale())).append(template.getEndOfLine());
	                    DecimalFormat df = new DecimalFormat("0.000000");
	                    BigDecimal rate = foreignAmount.getBaseConversionRate();
	                    sb.append("  Exchange Rate: ").append(df.format(rate.doubleValue()));
	                }
	            }
	        }
	    }
	@Override
	protected void formatCreditInfo(JournalTemplateIfc template, JournalableADOIfc journalable, StringBuilder sb)
    {
        Map memento = journalable.getJournalMemento();
        //System.out.println("journalable.getJournalMemento() ::"+journalable.getJournalMemento().toString());
        String cardType = (String) memento.get(JournalConstants.CARD_TYPE);
       // System.out.println("cardType :"+JournalConstants.CARD_TYPE);
        if (!cardType.startsWith("C-"))
        {
	       // System.out.println(TenderConstants.NUMBER);
	        String cardNumber = (String)memento.get(TenderConstants.NUMBER);
	       // System.out.println(cardNumber);
	        int len = cardNumber.length();
	        String lastFourCreditCardNum = cardNumber.substring(len-4, len);
	        if(cardType != null && cardType.startsWith("L-"))
	        {
	        sb.append(Util.EOL).append("  Type:  ").append(cardType.substring(2)).append(
	                Util.EOL).append("  Acct. #:  ").append(lastFourCreditCardNum)/*.append(Util.EOL)
	                .append("  Expiration Date:  ")*/;
	        }else {
	        	sb.append(Util.EOL).append("  Type:  ").append((String) memento.get(JournalConstants.CARD_TYPE)).append(
		                Util.EOL).append("  Acct. #:  ").append(lastFourCreditCardNum)/*.append(Util.EOL)
		                .append("  Expiration Date:  ")*/;
	        }
	        if (memento.get(JournalConstants.CARD_TYPE).equals(CreditTypeEnum.HOUSECARD.toString()))
	        {
	            sb.append("N/A");
	        }
	        else if(((TenderCreditADO)journalable).isCardSwiped())
	        {
	            EYSDate dateEYS = EYSDate.getEYSDate(EYSDate.FORMAT_YYMM, (String) memento.get(TenderConstants.EXPIRATION_DATE));
	            sb.append(dateEYS.toFormattedString(EYSDate.CARD_FORMAT_MMYYYY));
	        }
	        if(memento.get(TenderConstants.AUTH_STATUS)!= null && memento.get(TenderConstants.AUTH_STATUS).equals(0))
	        {
	        sb.append(Util.EOL).append("  Entry:  ").append("Swipe");
	        }
	        else{
	        	 sb.append(Util.EOL).append("  Entry:  ").append(memento.get(JournalConstants.ENTRY_METHOD).toString());
	        }
	        if(!((AuthorizableADOIfc) journalable).isAuthorized() && (tenderAction != "Deleted"))
	        {
	            sb.append(Util.EOL).append("  Auth. Status: Approved");
	        }
	        String authorizationCode = (String) memento.get("AUTH_CODE");
	        if(authorizationCode !=null && !authorizationCode.equalsIgnoreCase(""))
	        {
	            sb.append(Util.EOL).append("Authorization Code: " + authorizationCode);
	        }
        }
    }
}
