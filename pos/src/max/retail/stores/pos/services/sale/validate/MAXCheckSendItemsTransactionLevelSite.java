/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *	Rev 1.0     Dec 14, 2016		Ashish Yadav		Home Delivery Send FES
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.sale.validate;
import max.retail.stores.domain.transaction.MAXTransactionTotalsIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
/**
 * 
 * @author dipak.goit
 *
 */
public class MAXCheckSendItemsTransactionLevelSite extends PosSiteActionAdapter
{
    /**
        Revision number of this class
    **/
    public static final String revisionNumber = "$Revision: 3$";
	/**
    Letter send items
**/
public static final String SEND_ITEMS = "SendItems";
/**
    Letter continue
**/
public static final String CONTINUE = "Continue";

//----------------------------------------------------------------------
/**
    Check if send items are there for send level as transaction
    <P>
    @param  bus     Service Bus
**/
//----------------------------------------------------------------------
public void arrive(BusIfc bus)
{
    //Default the letter value to Continue
    String letter = CONTINUE;
    //added by dipak 
   if(bus.getCurrentLetter().getName().equalsIgnoreCase("DoNotRedeem")){
	   letter = "DoNotRedeem";
   }
    boolean transactionLevelSend = false;
    
    SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
    
    SaleReturnTransactionIfc  transaction = cargo.getTransaction();
    
    AbstractTransactionLineItemIfc[] lineItems = null;     
    // Changes starts for Rev 1.0 (Ashish : Send)
    
    if (transaction != null && ((SaleReturnTransaction) transaction).isTransactionLevelSendAssigned())
    {
    	 // Changes starts for Rev 1.0 (Ashish : Send)
        lineItems = transaction.getSendItemBasedOnIndex(1);
        
        if (lineItems != null  && 
                lineItems.length > 0)
        {
            letter = SEND_ITEMS;                                
        }                         
    }
    bus.mail(new Letter(letter), BusIfc.CURRENT); 
}
    
}
