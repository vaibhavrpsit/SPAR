/*===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreateshipping/EnterShipCustomerInfoSite.java /main/6 2014/06/10 12:04:11 abhinavs Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* abhinavs    06/10/14 - CAE add available date during order create enhancement
*                        phase II
* yiqzhao     10/19/12 - Refine for getting customer information.
* yiqzhao     09/17/12 - Populate customer info from linked customer.
* yiqzhao     07/02/12 - Read text from orderText bundle file and define screen
*                        names
* yiqzhao     06/05/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.order.xchannelcreateshipping;


import java.util.List;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
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

//--------------------------------------------------------------------------
/**
    This site calls the UI manager to display the enter ship customer data
    screen.
    @version $Revision: /main/6 $
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class EnterShipCustomerInfoSite extends PosSiteActionAdapter
{
    // ----------------------------------------------------------------------
    /**
     * This method calls the UI manager to display the enter ship customer data
     * screen.
     * 
     * @param bus Service Bus
     **/
    // ----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        XChannelShippingCargo cargo = (XChannelShippingCargo)bus.getCargo();

        String firstName = "";
        String lastName = "";
        String phoneNo = "";
        // Show the pick up custom info screen.
        DataInputBeanModel model = new DataInputBeanModel();

        model.setValue("firstNameField", "");
        model.setValue("lastNameField", "");
        model.setValue("telephoneNumberField", "");

        if (cargo.getCustomer() != null)
        {
            CustomerIfc customer = cargo.getCustomer();
            firstName = customer.getFirstName();
            lastName = customer.getLastName();
            List<PhoneIfc> phones = customer.getPhoneList();
            if (phones != null && phones.size() > 0)
            {
                phoneNo = phones.get(0).getPhoneNumber();
            }
            model.setValue("firstNameField", firstName);
            model.setValue("lastNameField", lastName);
            model.setValue("telephoneNumberField", phoneNo);
        }

        model.setValue("countryField", getCountryModel(bus));
        // Initialize the model
        uiManager.showScreen(POSUIManagerIfc.XC_SHIP_TO_STORE_CUSTOMER, model);
    }

    // ----------------------------------------------------------------------
    /**
     * This method calls the UI manager to retrieve ship customer data screen.
     * 
     * @param bus Service Bus
     **/
    // ----------------------------------------------------------------------
    public void depart(BusIfc bus)
    {
        XChannelShippingCargo cargo = (XChannelShippingCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        DataInputBeanModel model = (DataInputBeanModel)ui.getModel("XC_SHIP_TO_STORE_CUSTOMER");

        CaptureCustomerIfc customer = DomainGateway.getFactory().getCaptureCustomerInstance();
        customer.setFirstName((String)model.getValue("firstNameField"));
        customer.setLastName((String)model.getValue("lastNameField"));

        // parse phone number to remove formatting characters
        PhoneIfc phone = DomainGateway.getFactory().getPhoneInstance();
        phone.parseString((String)model.getValue("telephoneNumberField"));
        customer.setPhoneNumber(phone.getPhoneNumber());

        cargo.setCaptureCustomer(customer);
        cargo.setShipToCustomer(false);
    }

    /*
     * Create the combo box model for the available countries
     */
    protected CountryModel getCountryModel(BusIfc bus)
    {
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);

        CountryModel model = new CountryModel();
        String storeState = CustomerUtilities.getStoreState(pm);
        String storeCountry = CustomerUtilities.getStoreCountry(pm);

        int countryIndx = utility.getCountryIndex(storeCountry, pm);
        model.setCountryIndex(countryIndx);
        model.setStateIndex(utility.getStateIndex(countryIndx, storeState.substring(3, storeState.length()), pm));
        model.setCountries(utility.getCountriesAndStates(pm));

        return model;
    }
}