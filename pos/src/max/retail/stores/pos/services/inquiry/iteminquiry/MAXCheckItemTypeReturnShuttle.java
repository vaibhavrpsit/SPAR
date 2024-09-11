package max.retail.stores.pos.services.inquiry.iteminquiry;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.CheckItemTypeReturnShuttle;

public class MAXCheckItemTypeReturnShuttle extends CheckItemTypeReturnShuttle {

	protected MAXItemInquiryCargo itemInquiryCargo = null;
	 public void load(BusIfc bus)
	    {
	        super.load(bus);
	        
	        itemInquiryCargo = (MAXItemInquiryCargo) bus.getCargo();
	    }

	    //----------------------------------------------------------------------
	    /**
	       Loads data into validate item service. <P>
	       <B>Pre-Condition(s)</B>
	       <UL>
	       <LI>ItemInquiryCargo in the itemcheck service's bus has been modified as 
	       appropriate for the type of item it maintains.
	       </UL>
	       <B>Post-Condition(s)</B>
	       <UL>
	       <LI>ItemInquiryCargo instance of the calling service will be modified to reflect the 
	       changes made by the itemcheck service
	       </UL>
	       @param  bus     Service Bus
	    **/
	    //----------------------------------------------------------------------
	    public void unload(BusIfc bus)
	    {
	        super.unload(bus);
	        MAXItemInquiryCargo cargo = (MAXItemInquiryCargo) bus.getCargo();
	        
	        cargo.setPLUItem(itemInquiryCargo.getPLUItem());
	        cargo.setItemQuantity(itemInquiryCargo.getItemQuantity());
	        cargo.setTransaction(itemInquiryCargo.getTransaction());
	        cargo.setModifiedFlag(itemInquiryCargo.getModifiedFlag());
	        cargo.setSingleBarCodeData(itemInquiryCargo.getSingleBarCodeData());
	    }

}
