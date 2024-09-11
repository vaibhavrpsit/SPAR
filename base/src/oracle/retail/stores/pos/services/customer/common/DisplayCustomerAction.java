/* ===========================================================================
* Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/common/DisplayCustomerAction.java /main/7 2013/05/08 16:09:44 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  05/08/13 - display customer info in the status bar.
 *    mchellap  02/19/13 - Added timer model for screen timeout
 *    vbongu    10/02/12 - do not display dialog for buses that are already at
 *                         the CustomerInfo site
 *    mjwallac  04/25/12 - Fixes for Fortify redundant null check, take2
 *    cgreene   12/16/11 - fix error in search for buses loop
 *    cgreene   10/27/11 - implement popup dialog for customer info
 *    cgreene   10/27/11 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.common;

import java.awt.event.ActionEvent;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.PricingGroupIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.TierTechnicianIfc;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.foundation.tour.gate.TechnicianNotFoundException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PopupDialogAction;
import oracle.retail.stores.pos.services.order.pickup.PickupOrderCargo;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBean;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;

import org.apache.log4j.Logger;

/**
 * An action that will display the {@link CustomerInfoBean} in a popup
 * dialog. This action expects that the {@link TierTechnicianIfc} is running
 * as one of the {@link Dispatcher}'s local technicians and that one of the
 * buses running has a {@link SaleCargoIfc}.
 *
 * @author cgreene
 * @since 13.4
 */
public class DisplayCustomerAction extends PopupDialogAction
{
    private static final Logger logger = Logger.getLogger(DisplayCustomerAction.class);

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        CustomerIfc customer = null;
        
        try
        {
            TierTechnicianIfc tierTechnician = (TierTechnicianIfc)Dispatcher.getDispatcher().getLocalTechnician("APPLICATION");
            if (tierTechnician != null)
            {
                BusIfc[] buses = tierTechnician.getBuses();
                
                BusIfc bus = null;
                if (!isAlreadyDisplayingCustomerInfo(buses))
                {
                    for (int i = 0; i < buses.length; i++)
                    {
                        bus = buses[i];
                        
                        if (bus.getCargo() instanceof SaleCargoIfc)
                        {
                            SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
                            customer = cargo.getTransaction() != null ? cargo.getTransaction().getCustomer() : null;
                            if(customer != null)
                                break;
                        }
                        if (bus.getCargo() instanceof PickupOrderCargo)
                        {
                            PickupOrderCargo poc = (PickupOrderCargo)bus.getCargo();
                            customer = poc.getOrder() != null ? poc.getOrder().getCustomer() : null;
                            if(customer != null)
                                break;
                        }
                    }
                    
                    if(customer == null)
                    {
                        logger.warn("Could not find an active bus to display customer info with.");
                        return;
                    }
                    
                    // Setup bean model information for the UI to display
                    CustomerInfoBeanModel beanModel = getCustomerInfoBeanModel(bus, customer);
                    showDialog(bus, POSUIManagerIfc.CUSTOMER_INFO_DIALOG, beanModel);
                }
                else
                {
                    logger.warn("Site is already displaying the customer info. Canceling dialog.");
                }
            }
            else
            {
                logger.error("TierTechnician is not configured for \"APPLICATION\" for the Dispatcher.");
            }
        }
        catch (TechnicianNotFoundException ex)
        {
            logger.error("TierTechnician is not configured for \"APPLICATION\" for the Dispatcher.", ex);
        }
    }

    /**
     * Returns true if any of the buses are already at the CustomerInfo site.
     *
     * @param buses
     * @return
     */
    protected boolean isAlreadyDisplayingCustomerInfo(BusIfc[] buses)
    {
        for (BusIfc bus : buses)
        {
            if ("CustomerInfo".equals(bus.getCurrentSiteName()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Initializes the CustomerInfoBeanModel to use in dialog.
     *
     * @param bus Service Bus
     * @return CustomerInfoBeanModel the beanmodel
     */
    public CustomerInfoBeanModel getCustomerInfoBeanModel(BusIfc bus, CustomerIfc customer)
    {
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        LocaleRequestor locale = utility.getRequestLocales();

        // model to use for the UI
        CustomerInfoBeanModel model = new CustomerInfoBeanModel();
        model = CustomerUtilities.populateCustomerInfoBeanModel(customer, utility, pm, model);

        model.setCustomersearchSpec(false);

        PricingGroupIfc[] pricingGroups = null;
        // set customer pricing group
        if (customer != null)
        {
            // setting tax id
            if (customer.getEncipheredTaxID() != null)
                model.setTaxID(customer.getEncipheredTaxID());

            if (true)
            {
                // get customer pricing group
                pricingGroups = CustomerUtilities.getCustomerPricingGroups();
                if (pricingGroups == null)
                {
                    // if pricing groups is null, setting none value
                    String[] pricingGroupNames = new String[1];
                    pricingGroupNames[0] = CustomerUtilities.NONE_SELECTED;
                    model.setPricingGroups(pricingGroupNames);
                    // set selected index as 0
                    model.setSelectedCustomerPricingGroup(CustomerUtilities.DEFAULT_SELECTED);
                }
                else
                {
                    // set pricing group in cargo
                    model.setCustomerPricingGroups(pricingGroups);
                    // set pricing group name
                    String[] pricingGroupNames = new String[pricingGroups.length];
                    for (int i = 0; i < pricingGroups.length; i++)
                    {
                        pricingGroupNames[i] = pricingGroups[i].getPricingGroupName(LocaleMap
                                .getLocale(LocaleConstantsIfc.USER_INTERFACE));
                    }

                    String[] useArray = new String[pricingGroupNames.length + 1];
                    // use default selected as none
                    useArray[0] = CustomerUtilities.NONE_SELECTED;
                    for (int i = 0; i < pricingGroupNames.length; i++)
                    {
                        useArray[i + 1] = pricingGroupNames[i];
                    }
                    model.setSelectedCustomerPricingGroup(CustomerUtilities.DEFAULT_SELECTED);
                    model.setPricingGroups(useArray);

                    for (int i = 0; i < pricingGroups.length; i++)
                    {
                        if (Util.isObjectEqual(pricingGroups[i].getPricingGroupID(), customer.getPricingGroupID()))
                        {
                            model.setPricingGroups(useArray);
                            model.setSelectedCustomerPricingGroup(i + 1);
                        }
                    }
                }
            }
        }
        // get available customer groups
        if (true)
        {
            CustomerGroupIfc[] groups = CustomerUtilities.getCustomerGroups(locale);
            // append retrieved array -- if it exists -- to "none"
            if (groups == null)
            {
                // use no-discount entry as array
                model.setCustomerGroups(new CustomerCargo().getNoCustomerGroup());
                // set the customer group index to 0
                model.setSelectedCustomerGroupIndex(0);
            }
            else
            {
                CustomerGroupIfc[] useArray = new CustomerGroupIfc[groups.length + 1];
                // use default selected as none
                System.arraycopy(new CustomerCargo().getNoCustomerGroup(), 0, useArray, 0, 1);
                System.arraycopy(groups, 0, useArray, 1, groups.length);
                model.setCustomerGroups(useArray);

                // set index based on existing discount
                int index = 0;
                if (customer != null)
                {
                    groups = customer.getCustomerGroups();
                    if (groups != null && groups.length > 0)
                    {
                        // find match in used array
                        for (int i = 1; i < useArray.length; i++)
                        {
                            // once customer's discount group is
                            // found in array, set index and end search
                            if (useArray[i].getGroupID().equals(groups[0].getGroupID()))
                            {
                                index = i;
                                i = useArray.length;
                            }
                        }
                    }
                }
                model.setSelectedCustomerGroupIndex(index);
            }
        }

        model.setEditableFields(false);
        return(model);
    }

}
