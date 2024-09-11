/* ===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreatepickup/EnterPickupCustomerInfoSite.java /main/2 2013/04/15 11:54:01 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   04/15/13 - Set default values.
 *    jswan     04/29/12 - Added to support cross channel create pickup order
 *                         feature.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.xchannelcreatepickup;

import java.util.Iterator;
import java.util.List;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CountryModel;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

//--------------------------------------------------------------------------
/**
    This site calls the UI manager to display the enter pickup customer data
    screen.
    @version $Revision: /main/2 $
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class EnterPickupCustomerInfoSite extends PosSiteActionAdapter
{
    //----------------------------------------------------------------------
    /**
        This method calls the UI manager to display the enter pickup customer data
        screen.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        
        // Display the customer in the status panel, if the cargo has one.  If a customer
        // has been linked to the transaction during the course of this tour, this is
        // the first opportunity to display this data on the screen.
        displayCustomerStatus(uiManager, bus);
        
        XChannelCreatePickupOrderCargo cargo = (XChannelCreatePickupOrderCargo)bus.getCargo();
        CaptureCustomerIfc customer = getCustomer(cargo);

        DataInputBeanModel model = new DataInputBeanModel();

        String firstName = "";
        String lastName = "";
        String phoneNo = "";
        if (customer != null)
        {
            firstName = customer.getFirstName();
            lastName = customer.getLastName();
            List<PhoneIfc> phones = customer.getPhoneList();
            if ( phones != null && phones.size()>0 )
            {
                phoneNo = phones.get(0).getPhoneNumber();
            }

        }
    
        model.setValue("firstNameField", firstName);
        model.setValue("lastNameField", lastName);
        model.setValue("telephoneNumberField", phoneNo);       
        
        model.setValue("countryField", getCountryModel(bus));
        uiManager.showScreen(POSUIManagerIfc.XC_PICKUP_CUSTOMER, model);
    }  
      
    /**
     * Get the first customer from the list of customer data already entered
     * or the customer in the cargo.  The return can be null.
     * @param cargo
     * @return CustomerIfc or null
     */
    private CaptureCustomerIfc getCustomer(XChannelCreatePickupOrderCargo cargo)
    {
        CaptureCustomerIfc captureCustomer = null;
        
        if (!cargo.getCustomerForPickupByLineNum().isEmpty())
        {
            Iterator<Integer> iterator = cargo.getCustomerForPickupByLineNum().keySet().iterator();
            captureCustomer = cargo.getCustomerForPickupByLineNum().get(iterator.next());
        }
        else
        {
            CustomerIfc customer = cargo.getCustomer();
            if (customer != null)
            {
                captureCustomer = DomainGateway.getFactory().getCaptureCustomerInstance();
                captureCustomer.setFirstName(customer.getFirstName());
                captureCustomer.setLastName(customer.getLastName());
                if (customer.getPrimaryPhone() != null && !Util.isEmpty(customer.getPrimaryPhone().getPhoneNumber()))
                {
                    captureCustomer.setPhoneNumber(customer.getPrimaryPhone().getPhoneNumber());
                }
            }
        }

        return captureCustomer;
    }

    /*
     * Create the combo box model for the available countries
     */
    protected CountryModel getCountryModel(BusIfc bus)
    {
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

        CountryModel model = new CountryModel();
        String storeState   = CustomerUtilities.getStoreState(pm);
        String storeCountry = CustomerUtilities.getStoreCountry(pm);

        int countryIndx = utility.getCountryIndex(storeCountry, pm);
        model.setCountryIndex(countryIndx);
        model.setStateIndex(utility.getStateIndex(countryIndx,
                storeState.substring(3,storeState.length()), pm));
        model.setCountries(utility.getCountriesAndStates(pm));

        return model;
    }

    /**
     * Display the customer name in the status panel.
     * @param uiManager
     * @param bus
     */
    protected void displayCustomerStatus(POSUIManagerIfc uiManager, BusIfc bus)
    {
        XChannelCreatePickupOrderCargo cargo = (XChannelCreatePickupOrderCargo)bus.getCargo();
        CustomerIfc customer = cargo.getCustomer();
        if (customer != null)
        {
            if (customer.getAddressList().size() > 0)
            {
                ((AddressIfc)customer.getAddressList().get(0)).setAddressType(0);
            }

            // set the customer's name in the status area
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            StatusBeanModel statusModel = new StatusBeanModel();
            ui.customerNameChanged(customer.getFirstLastName());
            POSBaseBeanModel baseModel = null;
            baseModel = (POSBaseBeanModel)ui.getModel();

            if (baseModel == null)
            {
                baseModel = new POSBaseBeanModel();
            }
            baseModel.setStatusBeanModel(statusModel);
        }        
    }
}