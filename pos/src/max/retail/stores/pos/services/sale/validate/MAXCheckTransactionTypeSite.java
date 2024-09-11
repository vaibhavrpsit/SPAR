/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 

 *  Rev 1.4  11/02/2016    Changed by   Akanksha Chauhan				 Bug ID 16655
 *  Rev 1.3  01/09/2015                 Gaurav Bawa				 Bug ID 16427 
 *  Rev 1.2  25/08/2015                 Gaurav Bawa				 Bug ID 16369 
 *  Rev 1.1  20/Aug/2015                Deepshikha Singh        Changes for capillary coupon bugs:16357
 *  Rev 1.0  13/Aug/2015                Mohd Arif               Changes for to go tender screen after apply coupon in case of send  sale
 *  Bug id (16334) and pos get hanged when clicked tender button in case of sale return .
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.sale.validate;

// foundation imports
import java.util.Iterator;
import java.util.Vector;

import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.customer.MAXTICCustomer;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItem;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.domain.transaction.MAXTransactionTotalsIfc;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItem;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.domain.transaction.LayawayTransaction;
import oracle.retail.stores.domain.transaction.OrderTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//--------------------------------------------------------------------------
/**
 * This site checks the transaction types.
 * 
 * @version $Revision: 3$
 **/
// --------------------------------------------------------------------------
public class MAXCheckTransactionTypeSite extends PosSiteActionAdapter {
	/**
	 * Revision number of this class
	 **/
	public static final String revisionNumber = "$Revision: 3$";

	// ----------------------------------------------------------------------
	/**
	 * Check the transaction types and mail a proper letter
	 * <P>
	 * 
	 * @param bus
	 *            Service Bus
	 **/
	// ----------------------------------------------------------------------
	
	public static final String SEND_ITEMS = "SendItems";
	
	public void arrive(BusIfc bus) {
		// Default the letter value to SaleReturn
		String letter = "SaleReturn";
		String flagForTIC = Gateway.getProperty("application",
				"RedemptionForNonTICCustomer", "N").trim();
		SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();
		String currentLetter = bus.getCurrentLetter().getName();
		SaleReturnTransactionIfc transaction = cargo.getTransaction();
		Vector vector = transaction.getItemContainerProxy().getLineItemsVector();
		MAXSaleReturnLineItem firstItem=(MAXSaleReturnLineItem)vector.firstElement();

		MAXCustomerIfc customer = (MAXCustomerIfc) transaction.getCustomer();
		CustomerInfoIfc customerInfo = null;
		if (transaction.getCustomer() instanceof MAXCustomerIfc)
		{
	         customerInfo = transaction.getCustomerInfo();
		}
		MAXTICCustomer maxTICCustomer = null;
		/**Changes start for rev 1.1**/
		if(customer != null)
		{
	         maxTICCustomer = (MAXTICCustomer)customer.getMAXTICCustomer();
		}
		/**Changes end for rev 1.1**/
		if (transaction != null) {
			int transType = transaction.getTransactionType();

			if ((transType == TransactionIfc.TYPE_LAYAWAY_INITIATE)
					|| (transaction instanceof LayawayTransaction)) {
				letter = "Layaway";
			} else if ((transType == TransactionIfc.TYPE_ORDER_INITIATE)
					|| (transaction instanceof OrderTransaction)) {
				letter = "SpecialOrder";
			}
			//Changes start for Rev 1.1
			// if you do not want to redeem more capillary coupon
			else if (bus.getCurrentLetter().getName()
					.equalsIgnoreCase("DoNotReedem") || transaction.getTransactionType()==2 /*|| bus.getCurrentLetter().getName()
					.equalsIgnoreCase("Success")*/) {
				letter = "SaleReturn";
			}
			// if flag for NonTIC is 'N' and the value for the loyalty customer
			// is wrong
			/*else if (flagForTIC.equalsIgnoreCase("N")
					&& customer.getCustomerName().equals("")
					&& customer.getRecordID().equals("")) {
				letter = "SaleReturn";
			}*/
			else if (customer != null && flagForTIC.equalsIgnoreCase("N")&&(currentLetter.equalsIgnoreCase("Next"))) {
				letter = "CapillaryCoupon";
				
			}
			else if(maxTICCustomer != null && flagForTIC.equalsIgnoreCase("N") && maxTICCustomer.getCustomerType().equalsIgnoreCase("T"))
			{
				letter = "CapillaryCoupon";
			}
			else if (customer != null && flagForTIC.equalsIgnoreCase("N") && customer.getCustomerType().equalsIgnoreCase("L")) {
				//Rev 1.3 Bug : 16427 - Start
				AbstractTransactionLineItemIfc[] lineItems = null; 
				if (transaction != null &&  transaction.getTransactionTotals() != null && ((MAXTransactionTotalsIfc) transaction.getTransactionTotals()).isTransactionLevelSendAssigned())
			    {
			        lineItems = transaction.getSendItemBasedOnIndex(1);
			        
			        if (lineItems != null  && lineItems.length > 0 && !((MAXSaleReturnTransactionIfc)transaction).isSendTransaction())
			        {
			            letter = SEND_ITEMS;                                
			        }
			        else
			        {
			        	letter = "SaleReturn";    	
			        }
			    }
				else
				{
					letter = "SaleReturn";
				}
				//Rev 1.3 Bug : 16427 - End
			}
			else if (customer == null && flagForTIC.equalsIgnoreCase("N"))
					 {
				letter = "SaleReturn";
			}
			else if (customer != null && customer.getCustomerType().equalsIgnoreCase("T") || bus.getCurrentLetter().getName().equalsIgnoreCase("Success") 
					|| bus.getCurrentLetter().getName().equalsIgnoreCase("Next"))
			{
				//Rev 1.2 : Start
				if(((MAXSaleReturnTransactionIfc)transaction).isSendTransaction())
					letter = "SaleReturn"; 
				else
				{
					
				//Rev 1.2 : End
				//akanksha
                    if(transaction!=null){
                    	//Rev 1.4 Start
                    	  boolean transactionReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();
                		  boolean traningMode = cargo.getRegister().getWorkstation().isTrainingMode();
                			//Rev 1.4 End
					    cargo = (SaleCargoIfc) bus.getCargo();
					    MAXSaleReturnLineItemIfc item = (MAXSaleReturnLineItemIfc) cargo.getLineItem();
			    		Iterator itr = cargo.getTransaction().getLineItemsIterator();
			    		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			       		while(itr.hasNext())
			    		{ 
			    			MAXSaleReturnLineItemIfc itemObj = (MAXSaleReturnLineItemIfc)itr.next();
			    			if(itemObj.getPLUItem().getProductGroupID().equals(ProductGroupConstantsIfc.PRODUCT_GROUP_GIFT_CARD)&&( (item.getPLUItemID().equals("70071000"))))
			    			{													// customer
			    				letter = "SaleReturn"; 
			    			}
			    			//akanksha	
			    			else if(transactionReentryMode||traningMode){
			    				letter = "SaleReturn";
			    			}
			    			else{
			    				letter = "CapillaryCoupon";
			    			}
			    			}
					
			   //akanksha		
				}
			}}
			else if (customer != null && flagForTIC.equalsIgnoreCase("Y") && customer.getCustomerType().equalsIgnoreCase("L") &&
					 customer.getRecordID().equalsIgnoreCase("") && customerInfo.getPhoneNumber() !=null) {
				//skip coupon prompt for giftcard
				if(!(firstItem.getPLUItem() instanceof GiftCardPLUItem)){
					letter = "CapillaryCoupon";
				}
				
			}
			//Changes end for Rev 1.1
			else if (customer != null
					 /*Rev 1.0  start*/
					&& customer.getCustomerType().equalsIgnoreCase("T") && !cargo.getTransaction().hasSendItems() && !(cargo.getTransaction().getTransactionType()==2)) {
				/*Rev 1.0  end*/
				letter = "CapillaryCoupon";
			}
			
/*added for: 
 * Condition 1:
 *1. if redeemption for tic is true.
 *2. and input for tic is wrong.
 *3. and if you select the option NO.
 *--> in this case, you will go through Non-TIC flow exactly same whether you select YES
 *   
 */
			else if ((flagForTIC.equalsIgnoreCase("Y")) && (bus.getCurrentLetter().getName().equalsIgnoreCase("NO") || 
					bus.getCurrentLetter().getName().equalsIgnoreCase("DONOTCaptureTICCustomer"))) {
				letter = "CapillaryCouponWithWrongTIC";
			}
			bus.mail(new Letter(letter), BusIfc.CURRENT);
		}
	}

}
