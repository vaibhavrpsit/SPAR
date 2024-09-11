/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev 1.4		Apr 06, 2017		Mansi Goel			Changes to resolve yes button at Redeem landmark coupon screen is
 *														iterated number of times items scanned in the transaction
 *	Rev 1.3     Mar 15, 2017		Ashish Yadav		Fix for not  showing coupon redeem screen when reentry mode
 *  Rev 1.2     Mar 03, 2017        Nitika Arora        Fix for issue : Enter some items, transaction level amt discount,
 *  													go to more and  make a return without receipt, application hang.
 *  Rev 1.1     Feb 15, 2017		Nadia Arora			Fix for issue : Add the local customer and suspend the transaction 
 *  													and retrieve the same and click on tender application is coming out to main screen
 *	Rev 1.0     Dec 28, 2016		Ashish Yadav		Online Points Redemption FES	
 *
 ********************************************************************************/

package max.retail.stores.pos.services.sale.validate;

import java.util.Iterator;
import java.util.Vector;

import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.customer.MAXTICCustomer;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItem;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.pos.services.sale.MAXSaleCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItem;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;


/**
* This site displays a prompt for Capillary Coupons Redemption if the current transaction
* abides by numerous stipulations.
*/
public class MAXPromptForCapillaryCouponSite extends PosSiteActionAdapter{
	/**
	 * serialVersionUID long
	 */
	private static final long serialVersionUID = -8167871541696228104L;

	public void arrive(BusIfc bus) {
		String currentLetter = bus.getCurrentLetter().getName();
		POSUIManagerIfc uiManager = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		 //changes start for Rev 1.0
		 /**if (currentLetter.equalsIgnoreCase("LaunchTender") || currentLetter.equalsIgnoreCase("CapillaryCoupon") || currentLetter.equalsIgnoreCase("CapillaryCouponNonTIC")){ // Check for
																// conditions
																// only the
																// first time.**/
		//Rev 1.1 Changes for Bug 16347
		 if (currentLetter.equalsIgnoreCase("LaunchTender") || currentLetter.equalsIgnoreCase("CapillaryCoupon") || currentLetter.equalsIgnoreCase("CapillaryCouponNonTIC") || currentLetter.equalsIgnoreCase("CapillaryCouponWithWrongTIC")){ // Check for
																// conditions
																// only the
																// first time.
	     //changes end for Rev 1.0
			SaleCargoIfc saleCargo = (SaleCargoIfc) bus.getCargo();
			TransactionIfc transaction = saleCargo.getTransaction();
			int transactionType = transaction.getTransactionType();

			/*
			 * Skip the prompt for capillary coupon redemption in following scenarios.
			 * 1.Re-Entry Mode
			 * 2.Training Mode
			 * 3.Layaway Transaction
			 * 4.Transaction having at least one Return Item 
			 */
			// Changes starts for Rev 1.3 (Ashish : Coupon screen)
			if (transaction.getWorkstation().isTransReentryMode()||
							transaction.isTrainingMode()||
							transactionType==TransactionConstantsIfc.TYPE_LAYAWAY_INITIATE ||
							isReturnTransaction(transaction))
					/*bus.mail(CommonLetterIfc.NO);*/bus.mail("DoNotRedeem");
			// Changes ends for Rev 1.3 (Ashish : Coupon screen)
			else
				validateCustomerAndPrompt(bus,transaction);								
		}else{
			String[] args=new String[]{"more"};
			promptForCouponRedemption(uiManager,CommonLetterIfc.YES,args, bus);
		}
	}
	
	/**
	 * This method checks whether a Customer(TIC or NON-TIC) is linked in this transaction or not and
	 * displays Capillary Coupons Redemption prompt accordingly.If it is a NON-TIC customer,it also 
	 * checks for <code>RedemptionForNonTICCustomer</code> property from <code>application.properties</code>.
	 * Value "Y" suggests displaying the prompt.
	 * @param bus
	 * @param transaction
	 */
	public void validateCustomerAndPrompt(BusIfc bus, TransactionIfc transaction) {
		POSUIManagerIfc uiManager = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		String[] args = new String[] { "" };
		MAXCustomerIfc customer = null;
		MAXCustomerIfc ticCustomer = null;
		CustomerInfoIfc customerInfo = null;
		String currentLetter = bus.getCurrentLetter().getName();
		MAXSaleReturnTransactionIfc saleReturnTransaction = (MAXSaleReturnTransactionIfc) transaction;
		Vector vector = saleReturnTransaction.getItemContainerProxy().getLineItemsVector();
		MAXSaleReturnLineItem firstItem=(MAXSaleReturnLineItem)vector.firstElement();
		// Changes starts for Rev 1.0 (Ashish : Online points redemption)
		if (saleReturnTransaction.getTicCustomer() instanceof MAXCustomerIfc) {
			ticCustomer = (MAXCustomerIfc) saleReturnTransaction.getTicCustomer();
			saleReturnTransaction.setCustomerId(ticCustomer.getCustomerID());
			saleReturnTransaction.setCustomer(ticCustomer);
		}
		// Changes starts for Rev 1.0 (Ashish : Online points redemption)
		if (saleReturnTransaction.getCustomer() instanceof MAXCustomerIfc) {
			customer = (MAXCustomerIfc) saleReturnTransaction.getCustomer();
			customerInfo = saleReturnTransaction.getCustomerInfo(); // Guest/Non-TIC

		}
		if (customer != null && !(firstItem.getPLUItem() instanceof GiftCardPLUItem)) { // If a customer is linked in the transaction.
			MAXTICCustomer maxTICCustomer = (MAXTICCustomer)customer.getMAXTICCustomer();
			
			if ((customer.getCustomerType().equalsIgnoreCase("T") && !customer.getCustomerName().equals("") ) 
					|| (maxTICCustomer != null && maxTICCustomer.getCustomerType() != null && maxTICCustomer.getCustomerType().equalsIgnoreCase("T"))) 
			{ // If it is
				//akanksha													// a TIC
																	// customer
				
                   if(transaction!=null){
					
				    MAXSaleCargo cargo = (MAXSaleCargo)bus.getCargo();
				    MAXSaleReturnLineItemIfc item = (MAXSaleReturnLineItemIfc) cargo.getLineItem();
		    		Iterator itr = cargo.getTransaction().getLineItemsIterator();
		    		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
					while (itr.hasNext()) {
						MAXSaleReturnLineItemIfc itemObj = (MAXSaleReturnLineItemIfc) itr.next();
						if (itemObj.getPLUItem().getProductGroupID()
								.equals(ProductGroupConstantsIfc.PRODUCT_GROUP_GIFT_CARD)
								&& ((item.getPLUItemID().equals("70071000")))) { // customer
							bus.mail(CommonLetterIfc.NO);
						} else {
							promptForCouponRedemption(uiManager, CommonLetterIfc.YES, args, bus);
							// Changes for Rev 1.4 : Starts
							break;
							// Changes for Rev 1.4 : Ends
						}

					}
				}
				//akanksha
			}
			else if (ticCustomer != null && ticCustomer.getCustomerType().equalsIgnoreCase("T")) 
			{ 
             promptForCouponRedemption(uiManager, CommonLetterIfc.YES, args,bus);
            }
			else if (maxTICCustomer != null && maxTICCustomer.getCustomerType().equalsIgnoreCase("T")) 
			{ 
             promptForCouponRedemption(uiManager, CommonLetterIfc.YES, args,bus);
            }
			else if (!isNonTICCustomerCouponProperty() && customer.getCustomerName().equals("")
					&& customer.getRecordID().equals("")&&(currentLetter.equalsIgnoreCase("CapillaryCoupon")))
			{
			      promptForCouponRedemption(uiManager, CommonLetterIfc.YES, args,bus);
			
			}
			else if (isNonTICCustomerCouponProperty() && customer.getCustomerType().equalsIgnoreCase("L") && !customer.getCustomerName().equals("")
					&& !customer.getRecordID().equals("") && (currentLetter.equalsIgnoreCase("CapillaryCoupon")))
			{
			      promptForCouponRedemption(uiManager, CommonLetterIfc.YES, args,bus);
			
			}
			else if (isNonTICCustomerCouponProperty() && customer.getCustomerType().equalsIgnoreCase("L")&& customer.getRecordID().equalsIgnoreCase("") 
					&& customer.getCustomerID().equalsIgnoreCase("") && customerInfo.getPhoneNumber() !=null)
			{
			      promptForCouponRedemption(uiManager, CommonLetterIfc.YES, args,bus);
			
			}
			//cHANGE to break the loop of tic prompt when provide the tic mobile at time of add normal customer.---By Arif
			else if (isNonTICCustomerCouponProperty() && customer.getCustomerType().equalsIgnoreCase("L")&& customer.getRecordID().equalsIgnoreCase("") 
					&& !customer.getCustomerID().equalsIgnoreCase("") && customerInfo.getPhoneNumber() !=null && !customer.getCustomerName().equals("")
					&& currentLetter.equalsIgnoreCase("CapillaryCoupon"))
			{
			      promptForCouponRedemption(uiManager, CommonLetterIfc.YES, args,bus);
			
			}
			
			 else if (isNonTICCustomerCouponProperty()) { // If the property
															// set to true for
															// Non-TIC customer.
				 
				 // Changes starts for code merging(commenting getAreaCode() as it is not present in base 14)
				/*String guestCustomerPhone = customerInfo.getPhoneNumber()
						.getAreaCode()
						+ customerInfo.getPhoneNumber().getPhoneNumber();*/
				 /* CHanges for Rev 1.1 starts*/
				 String guestCustomerPhone = "";
				 if(customerInfo != null && customerInfo.getPhoneNumber() != null && customerInfo.getPhoneNumber().getPhoneNumber() != null)
				 guestCustomerPhone = customerInfo.getPhoneNumber()
							+ customerInfo.getPhoneNumber().getPhoneNumber();
				 // Changes ends for code merging
				 /* CHanges for Rev 1.1 ends*/
				if (guestCustomerPhone.length() != 0 && !(bus.getCurrentLetter().getName().equalsIgnoreCase("CapillaryCouponNonTIC")) 
						&& !(bus.getCurrentLetter().getName().equalsIgnoreCase("CapillaryCouponWithWrongTIC")))
				{
				/*deepti	if (guestCustomerPhone.length() == 0) // If guest/NON-TIC
														// customer's number is
													// not entered
					promptForCouponRedemption(uiManager, "Mobile", args);
				else
					promptForCouponRedemption(uiManager, CommonLetterIfc.YES,
							args);*/
					//Rev 1.3 : Start
					if(bus.getCurrentLetter().getName().equalsIgnoreCase("CapillaryCouponWithWrongTIC")){
						promptForCouponRedemption(uiManager, "Mobile", args,bus);
					}
					//Rev 1.3 : End
					else
						bus.mail(CommonLetterIfc.NO);
				}
				/*else if(customer.getCustomerType().equalsIgnoreCase("L") && !customer.getCustomerName().equals("") && !customer.getRecordID().equals(""))
				{
					//Bug ID 16369 : Start
					if(bus.getCurrentLetter().getName().equalsIgnoreCase("CapillaryCouponWithWrongTIC")){
						promptForCouponRedemption(uiManager, "Mobile", args);
					}
						
					//Bug ID 16369 : End
					else if(!bus.getCurrentLetter().getName().equalsIgnoreCase("CapillaryCouponNonTIC")){
						bus.mail(CommonLetterIfc.NO);
					}
						
					
				}*/
				
				else if(customer.getCustomerType().equalsIgnoreCase("L") && !customer.getCustomerName().equals("") &&
						!customer.getRecordID().equals("") && bus.getCurrentLetter().getName().equalsIgnoreCase("CapillaryCouponWithWrongTIC"))
				{
					if(guestCustomerPhone.length() != 0)
					{
						promptForCouponRedemption(uiManager, CommonLetterIfc.YES, args,bus);
					}
					else
					{
					//Bug ID 16369 : Start
						promptForCouponRedemption(uiManager, "Mobile", args,bus);
					//Bug ID 16369 : End
					}
				}
						
				else if(customer.getCustomerType().equalsIgnoreCase("L") && !customer.getCustomerName().equals("") &&
							!customer.getRecordID().equals("") && !bus.getCurrentLetter().getName().equalsIgnoreCase("CapillaryCouponNonTIC"))
					{
						bus.mail(CommonLetterIfc.NO);
					}				
				else 
				{
					promptForCouponRedemption(uiManager, "Mobile", args,bus);
				}
				
			} else {
				bus.mail(CommonLetterIfc.NO);
			}
		}
		/*
		 * If no customer is linked then check the property for NON-TIC Customer
		 * and display Coupon Prompt for its "Y" value.
		 */
		
		else if (bus.getCurrentLetter().getName().equalsIgnoreCase("CapillaryCouponNonTIC")||bus.getCurrentLetter().getName().equalsIgnoreCase("CapillaryCouponWithWrongTIC")) {
			/*Rev 1.4 start*/
			if(firstItem.getPLUItem() instanceof GiftCardPLUItem){
				bus.mail("DoNotRedeem");
			}else{
				promptForCouponRedemption(uiManager, "Mobile", args,bus);
			}
			/*Rev 1.4 End*/
		} else {
				bus.mail(CommonLetterIfc.NO);
		}
	}

	/**
	 * This method reads <code>RedemptionForNonTICCustomer</code> property from <code>application.properties</code> and
	 *  returns corresponding boolean value.
	 * @return boolean
	 */
	public boolean isNonTICCustomerCouponProperty(){
		String property=Gateway.getProperty("application","RedemptionForNonTICCustomer","N").trim();
		return property.equalsIgnoreCase("Y");
	}
	
	/**
	 * Returns true if transaction contains at least one return item;false otherwise.
	 *  
	 * @param transaction
	 * @return boolean
	 */
	public boolean isReturnTransaction(TransactionIfc transaction){
		//LOGIC 1: To be used until discussions for LOGIC 2 conclude. 
		if(transaction instanceof SaleReturnTransactionIfc){
			SaleReturnTransactionIfc saleTransaction=(SaleReturnTransactionIfc)transaction;
			AbstractTransactionLineItemIfc lineItems[]=saleTransaction.getLineItems();
			for(int i=0;i<lineItems.length;i++)
				if(lineItems[i] instanceof SaleReturnLineItemIfc)
					if(((SaleReturnLineItemIfc)lineItems[i]).isReturnLineItem()==true)
						return true;
			return false;
		}
		return false;
		
		//LOGIC 2: Open for discussion with client since April 8,2015.
		
		/*if(transaction instanceof SaleReturnTransactionIfc)
			if(((SaleReturnTransactionIfc) transaction).isExchange())
				return false;
			else if(transaction.getTransactionType()==TransactionConstantsIfc.TYPE_RETURN)
				return true;
		return false;*/
	}
	
	/**
	 * Displays the prompt for redemption of capillary coupon
	 * 
	 * @param uiManager
	 * @param letterForYesButton
	 * @param args
	 */
	public void promptForCouponRedemption(POSUIManagerIfc uiManager,String letterForYesButton,String[] args, BusIfc bus){
		DialogBeanModel model=new DialogBeanModel();
		model.setType(DialogScreensIfc.CONFIRMATION);
		model.setResourceID("CapillaryCoupon");
		model.setArgs(args);
		model.setButtonLetter(DialogScreensIfc.BUTTON_YES, letterForYesButton);
		//model.setButtonLetter(DialogScreensIfc.BUTTON_NO, CommonLetterIfc.NO);
		model.setButtonLetter(DialogScreensIfc.BUTTON_NO, "DoNotRedeem");
		PromptAndResponseModel parModel = ((POSBaseBeanModel) uiManager.getModel(MAXPOSUIManagerIfc.ENTER_COUPON_NUMBER)).getPromptAndResponseModel();
		if(parModel != null)
		{
		parModel.setResponseText("");
		}
		uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
		
		//bus.mail("DoNotRedeem");
	}
	
}