/*===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreateshipping/ShippingInformationSite.java /main/11 2014/01/22 15:19:50 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     08/14/14 - Fix the issue of unable to find customer default
*                        state.
* yiqzhao     01/22/14 - Avoid NullPointerException and setShipToCustomer to
*                        true in case clicking on ShipToStore and then undo.
* vtemker     10/21/13 - EIT Defect - Ship to Customer has null
*                        CapturedCustomer details and throws NPE
* yiqzhao     10/04/13 - Update status bar if there is a linked customer.
* abhinavs    12/10/12 - Fixing HP Fortify redundant null check issues
* yiqzhao     11/09/12 - Enable cancel and delete buttons on global navigation
*                        panel.
* yiqzhao     10/22/12 - Populate delivery info.
* yiqzhao     10/22/12 - Destination tax rules is calculated in
*                        DestinationTaxRule station. Remove unnecessary code.
* yiqzhao     10/18/12 - Get tax rules from destination tax rules station.
* yiqzhao     10/15/12 - modify shipping item tax rate based on destination.
* yiqzhao     09/17/12 - fix the issue with multiple order delivery details for
*                        a given item group.
* yiqzhao     07/30/12 - remove id type from shipping customer info screen
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
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.order.OrderDeliveryDetailIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.CountryIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CaptureCustomerInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

public class ShippingInformationSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 1L;

    /**
     * revision number for this class
     */
    public static final String revisionNumber = "$Revision: /main/11 $";	
    

    /**
     * 
     */
    public void arrive(BusIfc bus)
    {
        XChannelShippingCargo cargo = (XChannelShippingCargo)bus.getCargo();
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        CaptureCustomerInfoBeanModel model = new CaptureCustomerInfoBeanModel();
        model.setXchannelShipping(true);

        CountryIfc[] countries = utility.getCountriesAndStates(pm);
        model.setCountries(countries);
        
        String storeState   = CustomerUtilities.getStoreState(pm);
        String storeCountry = CustomerUtilities.getStoreCountry(pm);
        int countryIndx = utility.getCountryIndex(storeCountry, pm);
        model.setCountryIndex(countryIndx);
        model.setStateIndex(utility.getStateIndex(countryIndx,
                			storeState.substring(3,storeState.length()), pm));
                
        String[] phoneTypes = CustomerUtilities.getPhoneTypes(utility);
        model.setPhoneTypes(phoneTypes);
        
        if ( cargo.getTransaction() != null && cargo.getTransaction().getCustomer() != null )
        {
            EmployeeIfc operator = cargo.getOperator();
            if (operator != null)
            {
                StatusBeanModel sModel = new StatusBeanModel();
                sModel.setCashierName(operator.getPersonName().getFirstLastName());
                sModel.setSalesAssociateName(operator.getPersonName().getFirstLastName());
                sModel.setRegister(cargo.getRegister());
                // If training mode is turned on, then put Training Mode
                // indication in status panel. Otherwise, return status
                // to online/offline status.
                sModel.setStatus(POSUIManagerIfc.TRAINING_MODE_STATUS, cargo.getTransaction().isTrainingMode());

                sModel.setCustomerName(cargo.getTransaction().getCustomer().getFirstLastName());
 
                model.setStatusBeanModel(sModel);
            }
        
        }
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE); 
        if ( bus.getCurrentLetter().getName().equals("New"))
        {
       		//New customer shipping address button clicked.
           	ui.showScreen(POSUIManagerIfc.NEW_CUSTOMER_SHIP_ADDRESS, model);
        }
        
        else  
       	{
        	if ( cargo.getDeliveryDetail() != null)
        	{
            	String firstName = cargo.getDeliveryDetail().getFirstName();
               	boolean isCustomerLinked = "".equals(firstName);
               	
               	if(!isCustomerLinked)
            	{
	       		//populate customer information in case of linked customer
	       		populateCustomerInfo(utility, pm, model, cargo.getDeliveryDetail());
            	}
        	}
        	ui.showScreen(POSUIManagerIfc.SHIP_TO_OPTIONS, model);
       	}
    }
    
    /**
     * 
     */
    public void depart(BusIfc bus)
    {
        XChannelShippingCargo cargo = (XChannelShippingCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UIModelIfc baseModel = ui.getModel();
        if (baseModel instanceof CaptureCustomerInfoBeanModel)
        {
            CaptureCustomerInfoBeanModel model = (CaptureCustomerInfoBeanModel)baseModel;

            OrderDeliveryDetailIfc deliveryDetail = DomainGateway.getFactory().getOrderDeliveryDetailInstance();

            deliveryDetail.setFirstName(model.getFirstName());
            deliveryDetail.setLastName(model.getLastName());

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
        
        cargo.setShipToCustomer(true);
    }
    
    /**
     * 
     * @param utility
     * @param pm
     * @param model
     * @param deliveryDetail
     */
    protected void populateCustomerInfo(UtilityManagerIfc utility,
								   		 ParameterManagerIfc pm,
								   		 CaptureCustomerInfoBeanModel model, 
								   		 OrderDeliveryDetailIfc deliveryDetail)
    {	
    	model.setFirstName(deliveryDetail.getFirstName());
    	model.setLastName(deliveryDetail.getLastName());
        
    	AddressIfc address = deliveryDetail.getDeliveryAddress();
        if (deliveryDetail.getDeliveryAddress()!=null)
        {    
        	model.setAddressLine1(address.getLine1());
        	model.setAddressLine2(address.getLine2());
        	
        	model.setCity(address.getCity());

        	int countryIndex = utility.getCountryIndex(address.getCountry(), pm);
        	model.setCountryIndex(countryIndex);
        	
        	String state = address.getState();
        	if (state!=null && state.length()<=3)
        	{
        		model.setStateIndex(utility.getStateIndex(countryIndex, state, pm));
        	}
        	else if(state != null && state.length() > 3)
        	{
            	model.setStateIndex(utility.getStateIndex(countryIndex, state.substring(3, state.length()), pm));
        	}
        	
        	model.setPostalCode(address.getPostalCode());
        	
	        PhoneIfc phone = deliveryDetail.getContactPhone();
	        if ( phone != null )
	        {
	        	model.setPhoneNumber(phone.getPhoneNumber(), phone.getPhoneType());
        	}     	
        }    	
    }    
}