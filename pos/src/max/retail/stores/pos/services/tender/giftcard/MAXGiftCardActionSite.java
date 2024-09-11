/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
    Rev 1.1  08/08/2013     Jyoti Rawal, Changed the Gift Card Tender flow
  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.giftcard;

import java.util.HashMap;

import max.retail.stores.domain.utility.MAXGiftCard;
import max.retail.stores.domain.utility.MAXGiftCardIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderGiftCard;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.services.tender.giftcard.GiftCardActionSite;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * Add Gift Card tender to the transaction or display error dialog. 
 */
public class MAXGiftCardActionSite extends GiftCardActionSite
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -4111337560410509150L;
	/* Add gift card tender to the transaction.
     * @see com.extendyourstore.foundation.tour.application.SiteActionAdapter#arrive(com.extendyourstore.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        
        // add tender type to attributes
        HashMap tenderAttributes = cargo.getTenderAttributes();
        tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.GIFT_CARD);
        
        String amountStr = (String)tenderAttributes.get(TenderConstants.AMOUNT);
       // amountStr="-1160";
        if (amountStr.startsWith("-"))
        {
            tenderAttributes.put(TenderConstants.GIFT_CARD_CREDIT_FLAG, TenderConstants.TRUE);
        }
        else
        {
            tenderAttributes.put(TenderConstants.GIFT_CARD_CREDIT_FLAG, TenderConstants.FALSE);
        }
        
        // create the gift card tender
        TenderADOIfc giftCardTender = null;
        try
        {
            GiftCardIfc giftCard = cargo.getGiftCard();
           
			String cardNum = ((String)cargo.getTenderAttributes().get(TenderConstants.NUMBER));
//            String cardNumber = "";
//            if(cardNum.length()>16){
//            	tenderAttributes.put("TrackData",cardNum);
//            	cardNumber = cardNum.substring(0, 16);
//            	
          //  }
            
//            tenderAttributes.put(TenderConstants.NUMBER,cardNumber);
//            tenderAttributes.put("IsSwipe","true");
			
            tenderAttributes.put(
                    TenderConstants.ORIGINAL_BALANCE,
                    giftCard.getInitialBalance().toFormattedString(
                            LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));
            tenderAttributes.put(
                    TenderConstants.REMAINING_BALANCE,
                    getGiftCardRemainingBalance(giftCard.getCurrentBalance(), tenderAttributes.get(TenderConstants.AMOUNT).toString()).toFormattedString(
                            LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));
            //Rev 1.1 changes
//            if (!amountStr.startsWith("-"))
//                tenderAttributes.put(TenderConstants.AMOUNT, updateGiftCardTenderAmount(giftCard.getCurrentBalance(),amountStr));
            
            TenderFactoryIfc factory = (TenderFactoryIfc)ADOFactoryComplex.getFactory("factory.tender");
            giftCardTender = factory.createTender(tenderAttributes);
            TenderGiftCard tgc = (TenderGiftCard) giftCardTender.toLegacy();
            if(cardNum.length()>16){
            tgc.setCardNumber(cardNum.substring(0, 16));
            tgc.setCardType(cardNum);
            tgc.setEntryMethod(EntryMethod.Swipe);
            
            }else{
            	tgc.setCardNumber(cardNum);
            	tgc.setCardType(cardNum);
            }
            MAXGiftCardIfc gc = (MAXGiftCardIfc) tgc.getGiftCard();
             gc.setQcCardType(((MAXGiftCard)giftCard).getQcCardType());
            gc.setQcApprovalCode(((MAXGiftCard)giftCard).getQcApprovalCode());            
            gc.setQcInvoiceNumber(((MAXGiftCard)giftCard).getQcInvoiceNumber());
            gc.setQcBatchNumber(((MAXGiftCard)giftCard).getQcBatchNumber());
            gc.setExpirationDate(((MAXGiftCard)giftCard).getExpirationDate());
            gc.setQcTransactionId(((MAXGiftCard)giftCard).getQcTransactionId());
		} catch (ADOException adoe) {
            adoe.printStackTrace();
        }
        catch (TenderException e)
        {
            TenderErrorCodeEnum error = e.getErrorCode();
            if (error == TenderErrorCodeEnum.INVALID_AMOUNT)
            {
               // assert(false) : "This should never happen, because UI enforces proper format";
            }
        }
        
        // add the tender to the transaction
        try
        {           
            RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
            txnADO.addTender(giftCardTender);
            cargo.setLineDisplayTender(giftCardTender);
            
            // journal the added tender
            JournalFactoryIfc jrnlFact = null;
            try
            {
                jrnlFact = JournalFactory.getInstance();
            }
            catch (ADOException e)
            {
                logger.error(JournalFactoryIfc.INSTANTIATION_ERROR, e);
                throw new RuntimeException(JournalFactoryIfc.INSTANTIATION_ERROR, e);
            }
            RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();
            registerJournal.journal(giftCardTender, JournalFamilyEnum.TENDER, JournalActionEnum.ADD);
        
            // mail a letter
            bus.mail(new Letter("Success"), BusIfc.CURRENT);
        }
        catch (TenderException e)
        {
            TenderErrorCodeEnum error = e.getErrorCode();
            
            // save tender in cargo
            cargo.setTenderADO(giftCardTender);
            if (error == TenderErrorCodeEnum.INVALID_QUANTITY)
            { 
                displayDialog(bus, "InvalidGiftCardQuantity", null);
                return;  
            }
            
            if (error == TenderErrorCodeEnum.INVALID_CARD_NUMBER)
            {
                String[] args = {(String)tenderAttributes.get(TenderConstants.NUMBER)};
                displayDialog(bus, "InvalidGiftCard", args);
                return;
            }
        }    
    }
    
    //----------------------------------------------------------------------
    /**
        This depart method just removes the swipe any time msr incase
        there is a problem.
        @param bus
        @see com.extendyourstore.foundation.tour.ifc.SiteActionIfc#depart(com.extendyourstore.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
    public void depart(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        cargo.setPreTenderMSRModel(null);
    }
    
    //----------------------------------------------------------------------
    /**
        This method displays the error messages.
        @param bus
        @param message
        @param args
    **/
    //----------------------------------------------------------------------
    protected void displayDialog(BusIfc bus, String message, String[] args)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UIUtilities.setDialogModel(ui, DialogScreensIfc.ACKNOWLEDGEMENT, message, args);
    }
    
    //----------------------------------------------------------------------
    /**
     * This method is used to calculate the Gift Card remaining balance.
     * @return CurrencyIfc remaining balance of Gift Card
     * @param giftCardCurrentBalance CurrencyIfc current balance of Gift Card
     * @param giftCardTenderAmount String tendered amount via Gift Card
     **/
    //----------------------------------------------------------------------
    protected CurrencyIfc getGiftCardRemainingBalance(CurrencyIfc giftCardCurrentBalance, String giftCardTenderAmount)
    {
    	CurrencyIfc tenderAmount = DomainGateway.getBaseCurrencyInstance(giftCardTenderAmount);
    	return giftCardCurrentBalance;//.subtract(tenderAmount); //Rev 1.1 change
    }
    
    /*
     * This method will check to see if the current balance on the card is less than or equal to the current tender amount and 
     * if so it will return the current balance .  This will cause the app to handle a remiaining balance on the transaction.
     * before it would complete the transaction even though the gift card did not have enough of a balance to cover the tender.
     * 
     */
    protected String updateGiftCardTenderAmount(CurrencyIfc giftCardCurrentBalance, String currentTenderAmount)
    {
    	CurrencyIfc tenderAmount = DomainGateway.getBaseCurrencyInstance(currentTenderAmount);
    	
    	{
	    	if(giftCardCurrentBalance.compareTo(tenderAmount) != 1)
	    		tenderAmount = giftCardCurrentBalance;
    	}
    	return tenderAmount.toFormattedString(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
    }
    
    public boolean evaluate(String cardNumber)
    {
    	
    	return true;
    }
    protected void validateBinRange() throws TenderException
    {
       System.out.println("yes");
    }
}
