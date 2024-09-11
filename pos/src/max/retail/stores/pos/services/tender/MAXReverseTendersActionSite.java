/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*	Rev 1.0		Ashish Yadav	13/02/2017	Changes starts for reversing Shipping charges during transaction send
    Rev.2.0     Kumar Vaibhav   15/05/2023  CN lock ahnages
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender;

import java.util.Iterator;

import max.retail.stores.domain.discount.MAXDiscountRuleConstantsIfc;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.tender.MAXTenderChargeIfc;
import max.retail.stores.domain.tender.MAXTenderGiftCardIfc;
import max.retail.stores.domain.tender.MAXTenderStoreCreditIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.pos.ado.tender.MAXTenderStoreCreditADO;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByAmountStrategy;
import oracle.retail.stores.domain.discount.ItemDiscountByPercentageStrategy;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.lineitem.TenderLineItemCategoryEnum;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 *  Sets up all reversible tenders for a reversal
 */
public class MAXReverseTendersActionSite extends PosSiteActionAdapter
{
    /* (non-Javadoc)
     * @see com.extendyourstore.foundation.tour.application.SiteActionAdapter#arrive(com.extendyourstore.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
     
        TenderADOIfc[] tenders1 = cargo.getCurrentTransactionADO().getTenderLineItems(TenderLineItemCategoryEnum.ALL);
        if(tenders1.length==0)
        {
	        TenderADOIfc[] tenders = cargo.getCurrentTransactionADO()
	                   .getTenderLineItems(TenderLineItemCategoryEnum.REVERSAL_PENDING);
					   
//Rev 1.4 start --copied Pooja's code here from delete tenders site.
	        
	        
		TenderADOIfc[] allTenders = cargo.getCurrentTransactionADO().getTenderLineItems(TenderLineItemCategoryEnum.ALL);
		//Rev 1.4 end --copied Pooja's code here from delete tenders site.
					   // Changes starts for Rev 1.0 (Ashish : Send)
	      //when undo selected from the tender option screen, remove transaction level shipping charge line item if it exists.
	        TenderableTransactionIfc txn = cargo.getTransaction();
	        if ( txn instanceof SaleReturnTransaction )
	        {
	        	SaleReturnTransaction transaction = (SaleReturnTransaction)txn;
	        	if ( transaction.isTransactionLevelSendAssigned() )
	        	{
	        		AbstractTransactionLineItemIfc lineItems[] = transaction.getLineItems();
	        		for (int i = 0; i < lineItems.length; i++)
	        		{
	        			if ( lineItems[i] instanceof SaleReturnLineItemIfc )
	        			{
	        				SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)lineItems[i];
	        				if ( lineItem.isShippingCharge() )
	        				{
	        					//remove the shipping charge line item, keep the send package line item for later update
	        					transaction.removeLineItem(lineItem.getLineNumber());
	        					break;
	        				}
	        			}
	        		}
	        	}
	        }
			// Changes ends for Rev 1.0 (Ashish : Send)
			boolean isGiftCardTenderlineitemExists=false;
       boolean edcFlag=false;
       boolean paytmFlag=false;
       boolean storeCreditFlag=false;
       /*Change for Rev 1.7: End*/
       
       /*Change for Rev 1.7: Start*/
      
		
		 /*Change for Rev 1.7: Start*/
		for (int i = 0; i < allTenders.length; i++) {
			
			if (allTenders[i].toLegacy() instanceof MAXTenderGiftCardIfc) {
			
				isGiftCardTenderlineitemExists=true;
					break;
			}
			//Rev 1.5 start
			//Changes starts for Rev 1.1 (Ashish : PineLab)
			else if (allTenders[i].toLegacy() instanceof MAXTenderChargeIfc && (((MAXTenderChargeIfc) allTenders[i].toLegacy()).getResponseDate() != null)
					&& (("00").equals(((MAXTenderChargeIfc) allTenders[i].toLegacy()).getResponseDate().get("HostResponseCode")) ||
							(("APPROVED").equals(((MAXTenderChargeIfc) allTenders[i].toLegacy()).getResponseDate().get("HostResponse")) || 
									(("APPROVED").equals(((MAXTenderChargeIfc) allTenders[i].toLegacy()).getResponseDate().get("HostResponseCode")))))) {
				edcFlag = true;
				break;
			
			}	
			//Changes ends for Rev 1.1 (Ashish : PineLab)
			else if(allTenders[i].toLegacy() instanceof MAXTenderChargeIfc && (((MAXTenderChargeIfc) allTenders[i].toLegacy()).getCardType() != null)
					&& ("PAYTM").equals(((MAXTenderChargeIfc) allTenders[i].toLegacy()).getCardType()))
			{
				paytmFlag = true;
				break;
			}
			//Rev 1.5 end
			//Added by Vaibhav LS credit note code merging start--Rev.2.0
			else if(allTenders[i].toLegacy() instanceof MAXTenderStoreCreditIfc)
			{
				storeCreditFlag = true;
				break;
			}
		}   		
		//end Rev.2.0
		
		if (isGiftCardTenderlineitemExists ) {
			//last added store credit should not be included in transaction as it was cancelled. --BASE BUG. Oracle did not resolve it so we had to.
			if(allTenders[allTenders.length-1] instanceof MAXTenderStoreCreditADO)
			cargo.getCurrentTransactionADO().removeTender(allTenders[allTenders.length-1]);
			
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			DialogBeanModel beanModel = new DialogBeanModel();
			beanModel.setResourceID("GiftCardDeleteTenderAfterTimeout");
			beanModel.setType(DialogScreensIfc.ERROR);
			beanModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.FAILURE);
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel);
		    return;
		}
		 /*Change for Rev 1.7: End*/
		
		//Rev 1.4 ends---copied Pooja's code here from delete tenders site.
		

		//Rev 1.5 start
		if (edcFlag) {
			
			//last added store credit should not be included in transaction as it was cancelled. --BASE BUG. Oracle did not resolve it so we had to.
			if(allTenders[allTenders.length-1] instanceof MAXTenderStoreCreditADO)
			cargo.getCurrentTransactionADO().removeTender(allTenders[allTenders.length-1]);
			
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			DialogBeanModel beanModel = new DialogBeanModel();
			String msg[] = new String[4];
			beanModel.setResourceID("EDC_TENDER_REVERSAL");
			msg[0] = "<<--||--:: Reversal Blocked ::--||-->>";
			msg[1] = "Here Tendering is done using Online Unipay System";
			msg[2] = "Please firstly Line Void the Online EDC tenders and Try Again";
			msg[3] = "::Thanks::";
			beanModel.setArgs(msg);
			beanModel.setType(DialogScreensIfc.ERROR);
			beanModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.FAILURE);
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel);
		    return;
		}		
		 if(paytmFlag)
		{
			//last added store credit should not be included in transaction as it was cancelled. --BASE BUG. Oracle did not resolve it so we had to.
				if(allTenders[allTenders.length-1] instanceof MAXTenderStoreCreditADO)
				cargo.getCurrentTransactionADO().removeTender(allTenders[allTenders.length-1]);
			 
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			DialogBeanModel beanModel = new DialogBeanModel();
			beanModel.setResourceID("PAYTM_TENDER_REVERSAL");
			beanModel.setType(DialogScreensIfc.ERROR);
			beanModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.FAILURE);
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel);
		    return;
		}
		 //Added by Vaibhav LS credit note code merging start--Rev.2.0
		 
		 if(storeCreditFlag)
			{
				POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
				DialogBeanModel beanModel = new DialogBeanModel();
				beanModel.setResourceID("StoreCreditReversal");
				beanModel.setType(DialogScreensIfc.ERROR);
				beanModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.FAILURE);
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel);
			    return;
			}
		 //end  Rev.2.0
		//Rev 1.5 end
	        // if we marked any tenders for reversal, go to authorization
	        // otherwise go on to delete tenders
	        String letter = "Authorize";
	        if (tenders.length == 0)
	        {
	            letter = "Continue";
	        }  
	      //Capillary Coupon Changes For Rev 1.1 changes :Starts
	        //changes start for rev 1.3
	        if(cargo.getCurrentTransactionADO().toLegacy() instanceof MAXSaleReturnTransactionIfc )
	        {
	        //changes end for rev 1.3
	        MAXSaleReturnTransactionIfc trans = (MAXSaleReturnTransactionIfc) cargo
			.getCurrentTransactionADO().toLegacy();
	        /*Rev. 1.2 start*/
			if (cargo.getCurrentTransactionADO().toLegacy() instanceof MAXSaleReturnTransaction && trans.getTransactionType()!=2)
			{
					/*Rev. 1.2 end*/
					
					trans.clearTransactionDiscounts(
							DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT,
							MAXDiscountRuleConstantsIfc.ASSIGNMENT_CAPILLARYCOUPON);
					trans.clearTransactionDiscounts(
							DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE,
							MAXDiscountRuleConstantsIfc.ASSIGNMENT_CAPILLARYCOUPON);
				
				MAXSaleReturnLineItemIfc lineItem = null;
				Iterator i = trans.getItemContainerProxy().getLineItemsIterator();
				while (i.hasNext()) 
				{
					lineItem = (MAXSaleReturnLineItemIfc) i.next();
					ItemDiscountByAmountStrategy[] igy= new ItemDiscountByAmountStrategy[(lineItem
							.getItemPrice()).getItemDiscountsByAmount().length];
					if (igy.length > 0) 
					{
						for (int k = 0; k < igy.length; k++) 
						{
							lineItem.clearItemDiscountsByAmount(
									DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT,
									MAXDiscountRuleConstantsIfc.ASSIGNMENT_CAPILLARYCOUPON,
									false);
						}
					}
					ItemDiscountByPercentageStrategy[] pgy = new ItemDiscountByPercentageStrategy[((lineItem
							.getItemPrice()).getItemDiscountsByPercentage().length)];
					if (pgy.length > 0) 
					{
						for (int k = 0; k < pgy.length; k++) 
						{
							lineItem.clearItemDiscountsByPercentage(
									DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE,
									MAXDiscountRuleConstantsIfc.ASSIGNMENT_CAPILLARYCOUPON,
									false);
						}
					}
				}
				trans.removeCapillaryCouponsApplied();
				cargo.getTransaction().getTransactionTotals().updateTransactionTotals(trans.getItemContainerProxy().getLineItems(), trans.getItemContainerProxy().getTransactionDiscounts(), trans.getItemContainerProxy().getTransactionTax());
			}
			
	        
	      //Capillary Coupon Changes For Rev 1.1 changes :End
	        }   
	        bus.mail(new Letter(letter), BusIfc.CURRENT);
        }
        else
        	showConfirmationDialog(bus);
    }
    private void showConfirmationDialog(BusIfc bus)
	{
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("DeleteTender");        
        model.setType(DialogScreensIfc.ERROR);  
        model.setButtonLetter(DialogScreensIfc.ACKNOWLEDGEMENT, CommonLetterIfc.OK);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
	}
}
