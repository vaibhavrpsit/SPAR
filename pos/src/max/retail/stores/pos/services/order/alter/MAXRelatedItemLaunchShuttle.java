/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.order.alter;

import org.apache.log4j.Logger;

import max.retail.stores.pos.services.order.common.MAXOrderCargoIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.common.TimedCargoIfc;
import oracle.retail.stores.pos.services.modifyitem.relateditem.RelatedItemCargo;

//--------------------------------------------------------------------------
/**

 $Revision: 1$
 **/
//--------------------------------------------------------------------------
public class MAXRelatedItemLaunchShuttle extends FinancialCargoShuttle
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -7224181884585795067L;

	/**
     The logger to which log messages will be sent.
     **/
    protected static Logger logger = Logger.getLogger(max.retail.stores.pos.services.order.alter.MAXRelatedItemLaunchShuttle.class);
    
    /**
     revision number supplied by Team Connection
     **/
    public static final String revisionNumber = "$Revision: 1$";
    
    protected MAXOrderCargoIfc orderCargo;
//  ----------------------------------------------------------------------
    /**
     Loads cargo from modifyitem service. <P>
     <B>Pre-Condition(s)</B>
     <UL>
     <LI>Cargo will contain the selected item
     </UL>
     <B>Post-Condition(s)</B>
     <UL>
     <LI>
     </UL>
     @param  bus     Service Bus
     **/
//  ----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // load financial cargo
        super.load(bus);
        
        // retrieve cargo from the parent
        orderCargo = (MAXOrderCargoIfc)bus.getCargo();
        
    }
    
    //----------------------------------------------------------------------
    /**
       Loads data into alterations service. <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>Cargo will contain the selected item
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        // unload financial cargo
        super.unload(bus);

        // retrieve cargo from the child
        RelatedItemCargo cargo = (RelatedItemCargo)bus.getCargo();
        //cargo.setAccessFunctionID(orderCargo.getAccessFunctionID());
       // cargo.setPasswordRequired(orderCargo.isPasswordRequired());
       // cargo.setCustomerInfo(orderCargo.getCustomerInfo());
       // cargo.setEmployee(orderCargo.getEmployee());
        cargo.setLineItem(orderCargo.getLineItem());
       // cargo.setOperator(orderCargo.getOperator());
        cargo.setPLUItem(orderCargo.getPLUItem());
        cargo.setPrimaryItemSequenceNumber(orderCargo.getLineItem().getLineNumber());
       // cargo.setTransaction(orderCargo.getTransaction());

        /*SaleReturnTransactionIfc[] originalTxns = orderCargo.getOriginalReturnTransactions();
        for (int i = 0; (originalTxns != null) && (i < originalTxns.length); i++)
        {
            cargo.addOriginalReturnTransaction(originalTxns[i]);
        }*/

        // Record the timeout status in the cargo, so called services will know
        if(cargo instanceof TimedCargoIfc && orderCargo instanceof TimedCargoIfc)
        {
            ((TimedCargoIfc)cargo).setTimeout(((TimedCargoIfc)orderCargo).isTimeout());  
        }
    }
}
