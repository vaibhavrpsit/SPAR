/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.order.alter;

import max.retail.stores.pos.services.order.common.MAXOrderCargo;
import max.retail.stores.pos.services.order.common.MAXOrderCargoIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;


//--------------------------------------------------------------------------
/**
    $Revision: 4$
**/
//--------------------------------------------------------------------------
public class MAXAlterOrderItemLookupLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 286725333388100988L;

    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: 4$";

    // Calling service's cargo
    protected MAXOrderCargoIfc orderCargo = null;

    //----------------------------------------------------------------------
    /**
        Loads the item cargo.
        <P>
        @param  bus     Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // load the financial cargo
        super.load(bus);

        orderCargo = (MAXOrderCargoIfc) bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
        Transfers the item cargo to the item inquiry cargo for the item inquiry service.
        <P>
        @param  bus     Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        // unload the financial cargo
        super.unload(bus);

        ItemInquiryCargo inquiryCargo = (ItemInquiryCargo) bus.getCargo();
        inquiryCargo.setRegister(((MAXOrderCargo)orderCargo).getRegister());
       /* inquiryCargo.setTransaction(orderCargo.getTransaction());*/
        inquiryCargo.setModifiedFlag(true);
        inquiryCargo.setStoreStatus(((MAXOrderCargo)orderCargo).getStoreStatus());
        inquiryCargo.setRegister(((MAXOrderCargo)orderCargo).getRegister());
        inquiryCargo.setOperator(((MAXOrderCargo)orderCargo).getOperator());
        inquiryCargo.setCustomerInfo(((MAXOrderCargo)orderCargo).getCustomerInfo());
        inquiryCargo.setTenderLimits(((MAXOrderCargo)orderCargo).getTenderLimits());
        /*inquiryCargo.setPLUItem(((MAXOrderCargo)orderCargo).getPLUItem());
        inquiryCargo.setSalesAssociate(((MAXOrderCargo)orderCargo).getEmployee());*/

        String geoCode = null;
        if(((MAXOrderCargo)orderCargo).getStoreStatus() != null &&
        		((MAXOrderCargo)orderCargo).getStoreStatus().getStore() != null)
        {
            geoCode = ((MAXOrderCargo)orderCargo).getStoreStatus().getStore().getGeoCode();
        }
        POSUIManagerIfc     ui          = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        
        String input = ui.getInput();
        inquiryCargo.setInquiry(input, "", "", geoCode);
      
        
        
        
        inquiryCargo.setIsRequestForItemLookup(true);
    }

    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object.
        <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        return "Class:  InquiryOptionsLaunchShuttle (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode();
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }
}
