package oracle.retail.stores.pos.services.modifyitem;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.modifyitem.relateditem.RelatedItemCargo;

import org.apache.log4j.Logger;

public class RelatedItemReturnShuttle implements ShuttleIfc {

    /**
    The logger to which log messages will be sent.
    **/
   protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.modifyitem.RelatedItemReturnShuttle.class);
   
   /**
    revision number supplied by Team Connection
    **/
   public static final String revisionNumber = "$Revision: /main/1 $";
   
   protected RelatedItemCargo relatedItemCargo;
   

// ----------------------------------------------------------------------
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
// ----------------------------------------------------------------------
   public void load(BusIfc bus)
   {
       // retrieve cargo from the parent
       relatedItemCargo = (RelatedItemCargo)bus.getCargo();
       
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

       // retrieve cargo from the child
       ItemCargo cargo = (ItemCargo)bus.getCargo();
       
       for (SaleReturnLineItemIfc orderLineItem: relatedItemCargo.getOrderLineItems() )
       {
           cargo.addOrderLineItem(orderLineItem);
       }
   }
}
