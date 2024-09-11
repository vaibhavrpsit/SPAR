/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreateshipping/DisplayCustomerAddressBookSite.java /main/5 2013/05/13 10:44:52 abhinavs Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* abhinavs    05/10/13 - Fix to set and display correct address type
* yiqzhao     10/22/12 - A minor change
* yiqzhao     10/22/12 - Set customer's phone number if address phone number
*                        does not exist.
* yiqzhao     10/19/12 - Add phone number from customer address contact
*                        information.
* yiqzhao     07/02/12 - Read text from orderText bundle file and define screen
*                        names
* yiqzhao     06/29/12 - handle mutiple shipping packages in one transaction
*                        while delete one or more shipping items
* yiqzhao     06/04/12 - Creation
* ===========================================================================
*/


package oracle.retail.stores.pos.services.order.xchannelcreateshipping;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.order.OrderDeliveryDetailIfc;
import oracle.retail.stores.domain.utility.AddressBookEntryIfc;
import oracle.retail.stores.domain.utility.AddressConstantsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.ContactIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CaptureCustomerInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

import org.apache.commons.lang3.StringUtils;

public class DisplayCustomerAddressBookSite extends PosSiteActionAdapter
{
    /**
        class name constant
    **/
    public static final String SITENAME = "DisplayCustomerAddressBook";
    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/5 $";

    //--------------------------------------------------------------------------
    /**
       <p>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        XChannelShippingCargo cargo = (XChannelShippingCargo)bus.getCargo();
        ListBeanModel model = getListModel(bus);

        // Set the item number and description on the prompt and response model
        PromptAndResponseModel responseModel = new PromptAndResponseModel();

        // Set the prompt and response model list bean model
        model.setPromptAndResponseModel(responseModel);

        POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        uiManager.showScreen(POSUIManagerIfc.CUSTOMER_ADDRESS_BOOK, model);
    }
    
    public void depart(BusIfc bus)
    {
        XChannelShippingCargo cargo = (XChannelShippingCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        
        ListBeanModel listModel = (ListBeanModel)ui.getModel("CUSTOMER_ADDRESS_BOOK");
        CaptureCustomerInfoBeanModel model = (CaptureCustomerInfoBeanModel)listModel.getSelectedValue();
        
        OrderDeliveryDetailIfc deliveryDetail = cargo.getDeliveryDetail();
        if ( deliveryDetail == null )
        {
        	deliveryDetail = DomainGateway.getFactory().getOrderDeliveryDetailInstance();
        }
        	
	   	deliveryDetail.setFirstName(model.getFirstName());
	   	deliveryDetail.setLastName(model.getLastName());
	   	cargo.setDeliveryID(cargo.getDeliveryID() + 1);
	   	deliveryDetail.setDeliveryDetailID(cargo.getDeliveryID());
	   	 
	   	AddressIfc address = DomainGateway.getFactory().getAddressInstance();

	   	if(!StringUtils.isEmpty(model.getAddressType()))
	   	{
	   		address.setAddressType(Arrays.asList(AddressConstantsIfc.ADDRESS_TYPE_DESCRIPTOR).indexOf(model.getAddressType()));
	   	}
	   	else
	   	{
	   		address.setAddressType(AddressConstantsIfc.ADDRESS_TYPE_HOME);
	   	}
	   	Vector<String> lines = new Vector<String>();
	   	lines.add(model.getAddressLine1());
	   	lines.add(model.getAddressLine2());
	   	address.setLines(lines);
	   	address.setCity(model.getCity());
	   	address.setState(model.getStateNames()[0]);
	   	address.setCountry(model.getCountryNames()[0]);
	   	address.setPostalCode(model.getPostalCode());

	   	deliveryDetail.setDeliveryAddress(address);
	   	 
	   	PhoneIfc phone = DomainGateway.getFactory().getPhoneInstance();
	   	phone.setPhoneNumber(model.getPhoneNumber());
	   	deliveryDetail.setContactPhone(phone);
	   	 
	   	cargo.setDeliveryDetail(deliveryDetail);
    }
    
    /**
     * Create the ListBeanModel from the list of store and available item inventory
     * @param bus
     * @return
     */
    @SuppressWarnings ("unchecked")
    protected ListBeanModel getListModel(BusIfc bus)
    {      
    	XChannelShippingCargo cargo = (XChannelShippingCargo)bus.getCargo();

    	CustomerIfc customer = cargo.getCustomer();
    	//List<AvailableToPromiseInventoryIfc> iaList = cargo.getItemAvailablityList();
    	ListBeanModel listBeanModel = new ListBeanModel();

    	Vector<CaptureCustomerInfoBeanModel> modelList = new Vector<CaptureCustomerInfoBeanModel>();
    	List<AddressBookEntryIfc> addressEntries = customer.getAddressBookEntries();

    	for(AddressBookEntryIfc addressEntry: addressEntries)
    	{
    		CaptureCustomerInfoBeanModel model = new CaptureCustomerInfoBeanModel();

    		AddressIfc address = addressEntry.getAddress();
    		model.setAddressLine1(address.getLine1());
    		model.setAddressLine2(address.getLine2());
    		if(!StringUtils.isEmpty(AddressConstantsIfc.ADDRESS_TYPE_DESCRIPTOR[address.getAddressType()]))
    		{
    			model.setAddressType(AddressConstantsIfc.ADDRESS_TYPE_DESCRIPTOR[address.getAddressType()]);
    		}
    		else
    		{
    			model.setAddressType(AddressConstantsIfc.ADDRESS_TYPE_DESCRIPTOR[AddressConstantsIfc.ADDRESS_TYPE_HOME]);
    		}
    		model.setCity(address.getCity());

    		String countries[] = new String[1];
    		countries[0] = address.getCountry();
    		model.setCountryNames(countries);

    		String states[] = new String[1];
    		states[0] =address.getState();
    		model.setStateNames(states);

    		model.setPostalCode(address.getPostalCode());

    		ContactIfc contact = addressEntry.getContact();
    		if ( contact != null )
    		{
    			List<PhoneIfc> phones = contact.getPhoneList();
    			if ( phones != null && phones.size()>=1 )
    			{
    				PhoneIfc phone = phones.get(0);
    				model.setPhoneNumber(phone.getPhoneNumber(), phone.getPhoneType());
    			}
    			else
    			{
    				if ( customer.getContact() != null )
    				{
    					if(customer.isBusinessCustomer())
    					{
    						model.setOrgName(customer.getCustomerName());
    						model.setLastName(customer.getContact().getLastName());
    					}
    					else
    					{
    						model.setFirstName(customer.getContact().getFirstName());
    						model.setLastName(customer.getContact().getLastName());
    					}
    					phones = customer.getContact().getPhoneList();
    					if ( phones != null && phones.size()>=1 )
    					{
    						PhoneIfc phone = phones.get(0);
    						model.setPhoneNumber(phone.getPhoneNumber(), phone.getPhoneType());
    					}
    				}
    			}
    		}     	
    		modelList.addElement(model);
    	}

    	// Add the list of store available inventory to the ListBeanModel
    	listBeanModel.setListModel(modelList);

    	return listBeanModel;
    }  
}