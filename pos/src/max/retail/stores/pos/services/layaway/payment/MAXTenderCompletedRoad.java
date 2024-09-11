/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
    Rev 1.1  08/08/2013     Jyoti Rawal, Changed the Gift Card Tender flow
  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.layaway.payment;

// foundation imports
import java.util.HashMap;
import java.util.Vector;

import com.qwikcilver.clientapi.svpos.GCPOS;

import max.retail.stores.domain.utility.MAXGiftCard;
import max.retail.stores.pos.services.qc.MAXGiftCardUtilitiesQC;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.financial.PaymentIfc;
import oracle.retail.stores.domain.tender.TenderGiftCard;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.OfflinePaymentBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//------------------------------------------------------------------------------
/**
    This class updates the status of the layaway, adds the payment, and sets the 
    cargo. <P>
    @version $Revision: 4$
**/
//------------------------------------------------------------------------------
public class MAXTenderCompletedRoad extends PosLaneActionAdapter
{                                       // begin class TenderCompletedRoad
    /**
        lane name constant
    **/
    public static final String LANENAME = "TenderCompletedRoad";
    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: 4$";
    //--------------------------------------------------------------------------
    /**
        Performs the traversal functionality for the aisle.  In this case,
        The status of the layaway is checked and updated accordingly. <P>
        @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {                                   // begin traverse()
        LayawayCargo layawayCargo = (LayawayCargo)bus.getCargo();     
        POSUIManagerIfc ui =
                        (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
                        
        // Gets Layaway and Payment after Tender
        LayawayIfc layaway = layawayCargo.getLayaway();
        PaymentIfc payment = layawayCargo.getPayment();
        TenderableTransactionIfc transaction = layawayCargo.getTransaction();
        
        // Changes status accordingly...
        if (layaway.getBalanceDue().equals(payment.getPaymentAmount())) // BalanceDue == PaymentAmount
        {
            layaway.changeStatus(LayawayConstantsIfc.STATUS_COMPLETED);
        }
        else
        if (layaway.getStatus() == LayawayConstantsIfc.STATUS_UNDEFINED)
        {
            layaway.changeStatus(LayawayConstantsIfc.STATUS_NEW);
        }
        else
        {
            layaway.changeStatus(LayawayConstantsIfc.STATUS_ACTIVE);
        }
                
        payment.setTransactionID(transaction.getTransactionIdentifier());
        layaway.addPaymentAmount(payment.getPaymentAmount());
        
        // sets payment balance due after payment has been accounted for
        payment.setBalanceDue( layaway.getBalanceDue() );
        
        //  Journal entry for payment
        JournalManagerIfc journal = (JournalManagerIfc) Gateway.getDispatcher()
            .getManager(JournalManagerIfc.TYPE);
        
        //  Sets Total amount paid to current payment and allows
        //  the database to accumulate this total.  This is done 
        //  for offline payments when the application does not have
        //  the previous amount paid.  Layaway Delete handles this after printing.
        if ( transaction.getTransactionType() != TransactionIfc.TYPE_LAYAWAY_DELETE )
        {
            layaway.setTotalAmountPaid( payment.getPaymentAmount() );
        }   
        if(transaction instanceof LayawayTransactionIfc)
        {
        	Vector v = transaction.getTenderLineItemsVector();
        	 HashMap balanceEnquiryMap =  null;
        	
        	for (int i = 0; i< v.size(); i++)
        	{
        		if(v.get(i) instanceof TenderGiftCard)
        		{
      			
        			MAXGiftCardUtilitiesQC utilObj = new MAXGiftCardUtilitiesQC();
        			GCPOS pos = utilObj.getInstance();
        			TenderGiftCard  tgc = (TenderGiftCard) v.get(i);
        			MAXGiftCard giftCard = (MAXGiftCard) tgc.getGiftCard();
        			String cardNum = tgc.getCardNumber();
    				String cardNumber = null;
    				String var1 = ";";
    				String var2 = "=";
    				String var3 = "?";
    				String trackData = "";
    				boolean isSwiped1 = false;
    				String cardTrackData = ""; 
    				//changes start for issue if 26 digit barcode scan ,trackto data for 26 digit barcode to be passed
    				if(cardNum.length()> 16){
    					//TrackData = var1+cardNum.substring(0,16)+var2+cardNum.substring(16)+var3;
    					cardTrackData = var1+cardNum.substring(0,16)+var2+cardNum.substring(16)+var3;
    					//cardNumber = cardNum.substring(0,16);
    					if(cardNum.length()==26){
    						cardNumber= utilObj.getCardNumberFromTrackData(cardNum,true);
    						trackData=cardNum;
    						//((TenderGiftCard) tenders[i]).setCardNumber(cardNumber); //code for issue Card Number was wrong in DB when scanned 26 digit barcode (it took 1st 16 digit) 
    						
    					}else{
    						cardNumber = cardNum.substring(0,16);
    						trackData= cardTrackData;
    					}
    					//change End 
    				isSwiped1 = true;
    					
    				}else{
    					cardNumber = cardNum;
    				}
					//Rev 1.1 change
//        			if(tgc.getAmountTender().signum()== CurrencyIfc.POSITIVE)        			
//        				if(!isSwiped1)
//        					balanceEnquiryMap = utilObj.redeemCard(pos, tgc.getCardNumber(), tgc.getAmountTender().abs().toString(),transaction.getTransactionTotals().getSaleSubtotal().toString(),transaction.getTransactionID());
//        					else
//        						balanceEnquiryMap = utilObj.redeemCardUsingTrackData(pos, cardNumber, tgc.getAmountTender().abs().toString(),transaction.getTransactionTotals().getSaleSubtotal().toString(),transaction.getTransactionID(),trackData);
//        			
//        			if (balanceEnquiryMap.get("Amount") != null && balanceEnquiryMap != null && balanceEnquiryMap.size() != 0
//    						&& ("0").equals(balanceEnquiryMap.get("ResponseCode").toString())) {
//        				MAXGiftCardIfc gc = (MAXGiftCardIfc) tgc.getGiftCard();
//    								
//    					gc.setQcApprovalCode(balanceEnquiryMap.get("ApprovalCode").toString());
//    					gc.setQcCardType(balanceEnquiryMap.get("CardType").toString());
//    					gc.setExpirationDate(calculateEYSDate(balanceEnquiryMap.get("Expiry").toString()));
//    					gc.setQcTransactionId(balanceEnquiryMap.get("TransactionId").toString());
//    					if(balanceEnquiryMap.get("InvoiceNumber")!= null && !(("null").equals(balanceEnquiryMap.get("InvoiceNumber"))))
//    					gc.setQcInvoiceNumber(balanceEnquiryMap.get("InvoiceNumber").toString());  	
//    					
//    					
//						utilObj.ShowValidCard("GIFTCARD_ENQUIRYQC", balanceEnquiryMap, giftCard, bus);
//						
//
//    				}
//        			if (balanceEnquiryMap.size()!= 0 && (balanceEnquiryMap.get("Amount") == null)) {
//
//						utilObj.ShowInvalidCard("GIFTCARD_ENQUIRYQC", balanceEnquiryMap, giftCard, bus);
//						break;
//					}
        		}
        	}
        	
        }
        
        //  Journal balance due on a layaway if initial layaway
        if (transaction.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_INITIATE)
        {
            String balanceString = layaway.getBalanceDue().toGroupFormattedString(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
            StringBuffer sb = new StringBuffer();
            sb.append(Util.EOL)
            .append("Balance Due");
            int numSpaces = 27;
            sb.append(Util.SPACES.substring(balanceString.length(), numSpaces))
            .append(balanceString);
            
            if (journal != null)
            {
                journal.journal(transaction.getCashier().getLoginID(),
                                transaction.getTransactionID(),
                                sb.toString());
            }
            else
            {
                logger.error( "No JournalManager found");
            }
        }
        // Resets the beanModel for offline flow
        POSBaseBeanModel model = ( POSBaseBeanModel )ui.getModel(POSUIManagerIfc.LAYAWAY_OFFLINE);
        if ( model != null )
        {
            if ( model instanceof OfflinePaymentBeanModel)
            {
                ( (OfflinePaymentBeanModel)model ).resetModel();
            }                           // end model instanceof OfflinePaymentBeanModel                
        }                           // end model != null        
    }      
    // end traverse()
    public EYSDate calculateEYSDate(String strDate) {
    	EYSDate expDate =null;
    	try{
		expDate = DomainGateway.getFactory().getEYSDateInstance();
		expDate.initialize();
		expDate.setDay(Integer.parseInt(strDate.substring(0, 2)));
		expDate.setMonth(Integer.parseInt(strDate.substring(2, 4)));
		expDate.setYear(Integer.parseInt(strDate.substring(4, strDate.length())));
    	}catch(Exception e){
    		
    	}
		return expDate;

	}
}                           // end class TenderCompletedRoad

