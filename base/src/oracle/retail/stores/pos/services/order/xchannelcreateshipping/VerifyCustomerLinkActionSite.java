/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreateshipping/VerifyCustomerLinkActionSite.java /main/4 2013/07/26 10:23:23 sgu Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* sgu         07/25/13 - allow 0 addreee book entry
* yiqzhao     03/18/13 - Read customer name and phone number form delivery
*                        detail instead of address book.
* yiqzhao     10/19/12 - Refine for getting customer information.
* yiqzhao     06/07/12 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.services.order.xchannelcreateshipping;

import java.util.List;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.order.OrderDeliveryDetailIfc;
import oracle.retail.stores.domain.utility.AddressBookEntryIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Link customer if it has not
 * @author yiqzhao
 *
 */
public class VerifyCustomerLinkActionSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 1L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
    	XChannelShippingCargo cargo = (XChannelShippingCargo)bus.getCargo();
    	CustomerIfc customer = cargo.getCustomer();
        if ( customer != null )
        {
        	List<AddressBookEntryIfc> addressEntries = customer.getAddressBookEntries();
        	if ( addressEntries.size() <= 1 )
        	{
                OrderDeliveryDetailIfc deliveryDetail = cargo.getDeliveryDetail();
                if ( deliveryDetail == null )
                {
                	deliveryDetail = DomainGateway.getFactory().getOrderDeliveryDetailInstance();
                }
                	
                if (addressEntries.size() == 1)
                {
                    AddressBookEntryIfc addressEntry = addressEntries.get(0);
                    AddressIfc address = addressEntry.getAddress();
                    deliveryDetail.setDeliveryAddress(address);
                }
              
                deliveryDetail.setFirstName(customer.getFirstName());
                deliveryDetail.setLastName(customer.getLastName());
                if ( customer.getPhoneList()!=null && customer.getPhoneList().size()>0 ) 
                {
                    deliveryDetail.setContactPhone(customer.getPhoneList().get(0));
                }
 
        	   	cargo.setDeliveryID(cargo.getDeliveryID() + 1);
        	   	deliveryDetail.setDeliveryDetailID(cargo.getDeliveryID());       	   	
        	   	 
        	   	cargo.setDeliveryDetail(deliveryDetail);
        	   	
        	   	bus.mail(new Letter("Ship"), BusIfc.CURRENT);
        	}
        	else // multiple addresses, display customer address book
        	{
        		bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        	}
        }
        else // no customer info
        {            
        	// customer is not linked, go to enter shipping information site
            bus.mail(new Letter("Ship"), BusIfc.CURRENT);
        }
    }    
}
