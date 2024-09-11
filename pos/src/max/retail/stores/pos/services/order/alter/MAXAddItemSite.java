/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.order.alter;

import max.retail.stores.domain.transaction.MAXTransactionTotalsIfc;
import max.retail.stores.pos.services.order.common.MAXOrderCargoIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.appmanager.ManagerException;
import oracle.retail.stores.pos.appmanager.ManagerFactory;
import oracle.retail.stores.pos.appmanager.send.SendManager;
import oracle.retail.stores.pos.appmanager.send.SendManagerIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.device.cidscreens.CIDAction;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

//--------------------------------------------------------------------------
/**
    This site adds an item to the transaction.
    @version $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MAXAddItemSite extends PosSiteActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -1994629506131840857L;
	/**
       revision number
    **/
    public static final String revisionNumber = "$Revision: 3$";

    //----------------------------------------------------------------------
    /**
       Adds the item to the transaction. Mails Continue letter is special order to not
       ask for serial numbers, else mails GetSerialNumbers letter to possibly ask for
       serial numbers.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Grab the item from the cargo
    	MAXOrderCargoIfc cargo = (MAXOrderCargoIfc)bus.getCargo();
        
        String letter = CommonLetterIfc.CONTINUE;
        OrderIfc order = cargo.getOrder();
        OrderLineItemIfc item = cargo.getLineItem();

        if (((MAXTransactionTotalsIfc)(order.getTotals())).isTransactionLevelSendAssigned())
        {
            SendManagerIfc sendMgr = null;
            try
            {
                sendMgr = (SendManagerIfc)ManagerFactory.create(SendManagerIfc.MANAGER_NAME);
            }
            catch (ManagerException e)
            {
                // default to product version
                sendMgr = new SendManager();
            }   
            if(sendMgr.checkValidSendItem(item))
            {
                item.setItemSendFlag(true);
                //this value is always 1 since multiple sends are not allowed
                item.setSendLabelCount(1);
            }
        }
        
            
       
            if (cargo.getPLUItem().isSpecialOrderEligible() ==  false)
            {    
                letter = "NotValid";
            }
        
        
        if (letter != "NotValid")
        {  

            // set issue gift card items to gift receipt
            if (item.isGiftCardIssue() || item.isGiftCardReload() )
            {
                try
                {
                    ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
                    boolean autoPrintGiftReceiptGiftCardIssue =
                        pm.getStringValue("AutoPrintGiftReceiptForGiftCardIssue").equalsIgnoreCase("Y");
                    if (autoPrintGiftReceiptGiftCardIssue)
                    {                
                        item.setGiftReceiptItem(true);
                    }
                }
                catch (ParameterException pe)
                {
                    logger.warn("Unable to retrieve parameter" + pe.getMessage());
                }

            }
            
            
            cargo.setLineItem(item);
            
            
            
            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc)bus);            
            try
            {
                pda.lineDisplayItem(item);
            }
            catch (DeviceException e)
            {
                logger.warn("Unable to use Line Display: " + e.getMessage() + "");
            }
            try
            {
            	// Changes starts for code merging(commenting below line as IngenicoItems class is not present in base 14)
                //CIDAction action = new CIDAction(MAXIngenicoItems.SCREEN_NAME, CIDAction.ADD_ITEM);
                CIDAction action = new CIDAction(CIDAction.CPOI_TWO_BUTTON_SCREEN_NAME, CIDAction.ADD_ITEM);
                // Changes ends for code merging
                action.setLineItem(item);
                pda.cidScreenPerformAction(action);
            }
            catch (DeviceException e)
            {
                logger.warn("Unable to use CPOI: " + e.getMessage() + "");
            }
        }                
        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }
    
}
