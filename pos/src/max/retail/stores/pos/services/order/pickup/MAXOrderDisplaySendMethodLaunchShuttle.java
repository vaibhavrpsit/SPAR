/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.1	13/Aug/2013	  	Prateek, Changes done for Special Order CR - Suggested Tender Type
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.order.pickup;


import java.util.Iterator;
import java.util.Vector;

import max.retail.stores.domain.order.MAXOrderIfc;
import max.retail.stores.domain.transaction.MAXOrderTransactionIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.order.pickup.PickupOrderCargo;
import oracle.retail.stores.pos.services.send.address.SendCargo;
//------------------------------------------------------------------------------
/**
    Launch shuttle class for send.displaysendmethod service. <P>
    @version $Revision: 4$
**/
//------------------------------------------------------------------------------
public class MAXOrderDisplaySendMethodLaunchShuttle implements ShuttleIfc
{                                       // begin class DisplaySendMethodLaunchShuttle
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -4408989349772150598L;

    /**
       revision number supplied by Team Connection
    **/
    public static String revisionNumber = "$Revision: 4$";
    /**
       Cargo from the Item service.
    **/
    protected PickupOrderCargo pickupCargo  = null;
    /**
       send level in progress
    **/
    protected boolean sendLevelInProgress = false;

    //---------------------------------------------------------------------
    /**
       Load parent (Item) cargo class. <P>
       <B>Pre-Condition</B>
       <UL>
       <LI>Cargo in bus is instance of ItemCargo class
       </UL>
       <B>Post-Condition</B>
       <UL>
       <LI>Cargo loaded
       </UL>
       @param  bus interface
    **/
    //---------------------------------------------------------------------
    public void load(BusIfc bus)
    {                                   // begin load()
        // retrieve cargo
    	pickupCargo =  (PickupOrderCargo) bus.getCargo();
        sendLevelInProgress = true;
    }                                   // end load()

    //---------------------------------------------------------------------
    /**
       Unload to child (Send) cargo class. <P>
       <B>Pre-Condition</B>
       <UL>
       <LI>Cargo in bus is instance of SendCargo class
       </UL>
       <B>Post-Condition</B>
       <UL>
       <LI>Cargo unloaded
       </UL>
       @param bus service bus interface
    **/
    //---------------------------------------------------------------------
    public void unload(BusIfc bus)
    {                                   // begin unload()
        // retrieve cargo
        SendCargo cargo = (SendCargo) bus.getCargo();

        cargo.setTransactionLevelSendInProgress(sendLevelInProgress);


            cargo.setShipToInfo(pickupCargo.getOrder().getCustomer());

            Vector lineItemsVector = pickupCargo.getOrder().getItemContainerProxy().getLineItemsVector();
            
            SaleReturnLineItemIfc[] lineItems = new SaleReturnLineItemIfc[lineItemsVector.size()];
            
            
            Iterator iter = lineItemsVector.iterator();
            int i=0;
            while(iter.hasNext())
            {
            	SaleReturnLineItemIfc next = (SaleReturnLineItemIfc)iter.next();
            	lineItems[i++] = next;
            }
            	
        cargo.setLineItems(lineItems);
        cargo.setOperator(pickupCargo.getOperator());
        cargo.setStoreStatus(pickupCargo.getStoreStatus());
        OrderTransactionIfc transaction = DomainGateway.getFactory().getOrderTransactionInstance();
        transaction.setTransactionTotals(pickupCargo.getOrder().getTotals());
        transaction.setSalesAssociate(cargo.getOperator());
		/**MAX Rev 1.1 Change : Start**/
		((MAXOrderTransactionIfc)transaction).setSuggestedTender(((MAXOrderIfc)pickupCargo.getOrder()).getSuggestedTender());
		/**MAX Rev 1.1 Change : End**/
        //transaction.getPayment().setBusinessDate(cargo.getRegister().getBusinessDate());
        /*UtilityManagerIfc utility =
            (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);*/
        //utility.initializeTransaction(transaction, bus, -1);
        cargo.setTransaction(transaction);

        cargo.setCustomer(pickupCargo.getOrder().getCustomer());

        
    }                                   // end unload()

    //---------------------------------------------------------------------
    /**
       Method to default display string function. <P>
       @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  DisplaySendMethodLaunchShuttle (Revision " +
                                      getRevisionNumber() +
                                      ")" +
                                      hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

}                                       // end class DisplaySendMethodLaunchShuttle

