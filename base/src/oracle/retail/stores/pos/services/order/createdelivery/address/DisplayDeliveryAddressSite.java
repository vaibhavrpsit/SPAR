/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/createdelivery/address/DisplayDeliveryAddressSite.java /main/11 2013/05/21 11:37:00 vbongu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vbongu    05/21/13 - Use StringUtils to compare strings
 *    abhinavs  12/07/12 - Fixing HP fortify redundant null check issues
 *    icole     05/29/12 - Forward port fix for delivery details of customer
 *                         not retained.
 *    jswan     04/13/12 - Moved to the ‘order’ package during the cross
 *                         channel project to provide better organization for
 *                         the order create process.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    npoola    02/11/09 - fix offline issue for customer
 *    aphulamb  01/02/09 - fix delivery issues
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    aphulamb  11/13/08 - Display Delivery address
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.createdelivery.address;

import org.apache.commons.lang3.StringUtils;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderDeliveryDetailIfc;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.ifc.DataManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.order.createpickup.PickupDeliveryOrderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.MailBankCheckInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

@SuppressWarnings("serial")
public class DisplayDeliveryAddressSite extends PosSiteActionAdapter
{
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/11 $";

    /**
     * invalid postal code resource id
     */
    public static final String INVALID_POSTAL_CODE = "InvalidPostalCode";

    // --------------------------------------------------------------------------
    /**
     * Displays the delivery address detail screen. if cusotmer is already
     * linked with transaction then show the screen with populated customer
     * info.
     * <P>
     *
     * @param bus the bus arriving at this site
     */
    // --------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        PickupDeliveryOrderCargo pickupDeliveryOrderCargo = (PickupDeliveryOrderCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        SaleReturnLineItemIfc[] lineItems = pickupDeliveryOrderCargo.getLineItems();
        //there will only ever be one line item in this array
        OrderDeliveryDetailIfc deliveryDetail = lineItems[0].getOrderItemStatus().getDeliveryDetails(); 
        DataManagerIfc dm = (DataManagerIfc)bus.getManager(DataManagerIfc.TYPE);
        CustomerIfc customer = pickupDeliveryOrderCargo.getCustomer();
        boolean isOfflineCustomer = false;
        //during offline scenario customerID is set to customerName and lastName ...Nilesh
        if (( null != customer && (StringUtils.equals(customer.getCustomerName(),customer.getCustomerID())) && (StringUtils.equals(customer.getLastName(),customer
                .getCustomerID())))
                && (dm.getOnlineState() == false))
        {
            isOfflineCustomer = true;
        }
        MailBankCheckInfoBeanModel model = CustomerUtilities.copyCustomerToModel(customer,utility,pm);
        model.setIsDeliveryAddress(true);
        
        if (customer != null && deliveryDetail.getFirstName() != null)
        {
            // We have previously entered delivery details to display instead of the default customer info
            if(model.isBusinessCustomer())
            {
                model.setOrgName(deliveryDetail.getBusinessName());
            }
            else
            {
                model.setFirstName(deliveryDetail.getFirstName());
                if (isOfflineCustomer)
                {
                    model.setLastName("");
                }
                else
                {
                    model.setLastName(deliveryDetail.getLastName());
                }
            }
            model.setAddressLine1(deliveryDetail.getDeliveryAddress().getLine1());
            model.setAddressLine2(deliveryDetail.getDeliveryAddress().getLine2());
            model.setAddressLine3(deliveryDetail.getDeliveryAddress().getLine3());
            int countryIndex = CustomerUtilities.getCountryIndex(deliveryDetail.getDeliveryAddress().getCountry(),utility,pm);
            model.setCountryIndex(countryIndex);
            int stateIndex = utility.getStateIndex(countryIndex, deliveryDetail.getDeliveryAddress().getState(), pm);
            model.setStateIndex(stateIndex);
            model.setCity(deliveryDetail.getDeliveryAddress().getCity());
            model.setPostalCode(deliveryDetail.getDeliveryAddress().getPostalCode());
            PhoneIfc[] phoneList = new PhoneIfc[PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR.length];
            int phoneType = deliveryDetail.getContactPhone().getPhoneType();
            phoneList[phoneType] = deliveryDetail.getContactPhone();
            model.setPhoneList(phoneList);
        }
        // set the customer's name in the status area
        CustomerIfc billingCustomer = pickupDeliveryOrderCargo.getCustomer();
        StatusBeanModel statusModel = new StatusBeanModel();
        statusModel.setCustomerName(billingCustomer.getFirstLastName());
        model.setStatusBeanModel(statusModel);
        NavigationButtonBeanModel globalModel = new NavigationButtonBeanModel();
        model.setGlobalButtonBeanModel(globalModel);
        ui.showScreen(POSUIManagerIfc.GET_DELIVERY_ADDRESS_SCREEN, model);
    }
}
