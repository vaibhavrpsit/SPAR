/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreateshipping/NewCustomerAddressSite.java /main/3 2012/07/02 14:31:54 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     07/02/12 - Read text from orderText bundle file and define screen
*                        names
* yiqzhao     06/29/12 - handle mutiple shipping packages in one transaction
*                        while delete one or more shipping items
* yiqzhao     06/04/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.order.xchannelcreateshipping;

import java.util.Vector;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.order.OrderDeliveryDetailIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.CountryIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CaptureCustomerInfoBeanModel;

public class NewCustomerAddressSite extends PosSiteActionAdapter
{
    /**
        class name constant
    **/
    public static final String SITENAME = "CheckForLinkedCustomerSite";
    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/3 $";

    //--------------------------------------------------------------------------
    /**
       <p>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        CaptureCustomerInfoBeanModel model = new CaptureCustomerInfoBeanModel();

        CountryIfc[] countries = utility.getCountriesAndStates(pm);
        model.setCountries(countries);
        
        String[] phoneTypes = CustomerUtilities.getPhoneTypes(utility);
        model.setPhoneTypes(phoneTypes);
        
        String storeState = CustomerUtilities.getStoreState(pm);
        String storeCountry = CustomerUtilities.getStoreCountry(pm);
        
        int countryIndex = utility.getCountryIndex(storeCountry, pm);
	    model.setCountryIndex(countryIndex);	       
	    model.setStateIndex(utility.getStateIndex(countryIndex, storeState.substring(3, storeState.length()), pm));

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.NEW_CUSTOMER_SHIP_ADDRESS, model);
    }
    
    public void depart(BusIfc bus)
    {
    	 XChannelShippingCargo cargo = (XChannelShippingCargo)bus.getCargo();
    	 POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
    	 CaptureCustomerInfoBeanModel model = (CaptureCustomerInfoBeanModel)ui.getModel();
    	 
    	 OrderDeliveryDetailIfc deliveryDetail = DomainGateway.getFactory().getOrderDeliveryDetailInstance();
    	 deliveryDetail.setFirstName(model.getFirstName());
    	 deliveryDetail.setLastName(model.getLastName());
    	 cargo.setDeliveryID(cargo.getDeliveryID() + 1);
    	 deliveryDetail.setDeliveryDetailID(cargo.getDeliveryID());
    	 
    	 AddressIfc address = DomainGateway.getFactory().getAddressInstance();
    	 address.setAddressType(0);
    	 address.setCity(model.getCity());
    	 address.setCountry(model.getCountry());
    	 Vector<String> lines = new Vector<String>();
    	 lines.add(model.getAddressLine1());
    	 lines.add(model.getAddressLine2());
    	 address.setLines(lines);
    	 address.setPostalCode(model.getPostalCode());
    	 address.setState(model.getState());
    	 deliveryDetail.setDeliveryAddress(address);
    	 
    	 PhoneIfc phone = DomainGateway.getFactory().getPhoneInstance();
    	 phone.setPhoneNumber(model.getPhoneNumber());
    	 deliveryDetail.setContactPhone(phone);
    	 
    	 cargo.setDeliveryDetail(deliveryDetail);
    }
}