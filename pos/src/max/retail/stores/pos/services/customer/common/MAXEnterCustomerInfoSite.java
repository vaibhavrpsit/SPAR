/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
*
*
*
*	Rev 1.0     Oct 19, 2016		Mansi Goel			Changes for Customer FES
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.customer.common;

import max.retail.stores.pos.ui.beans.MAXCustomerInfoBeanModel;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.PricingGroupIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;
//--------------------------------------------------------------------------
/**
    Put up Customer Info screen for input of customer name and address information.
    <p>
    @version $Revision: 3$
**/
//--------------------------------------------------------------------------
abstract public class MAXEnterCustomerInfoSite  extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 752349929717334978L;

    /**
     * Initializes the CustomerInfoBeanModel from the cargo.
     * 
     * @param bus Service Bus
     * @return CustomerInfoBeanModel the beanmodel
     */
    public CustomerInfoBeanModel getCustomerInfoBeanModel(BusIfc bus)
    {

        CustomerCargo cargo = (CustomerCargo) bus.getCargo();
        CustomerIfc customer = cargo.getCustomer();
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        LocaleRequestor locale = utility.getRequestLocales();

        // Enable display of History button
        cargo.setHistoryMode(true);
        //Changes for Rev 1.0 : Starts
        // model to use for the UI
        MAXCustomerInfoBeanModel model = new MAXCustomerInfoBeanModel();
        model = MAXCustomerUtilities.populateCustomerInfoBeanModel(customer, utility, pm, model);

        // set boolean customerSearchSpec in model if
        // it is customer search flow
        if (cargo.isCustomerSearchSpec())
        {
            model.setCustomersearchSpec(true);
        }

        PricingGroupIfc[] pricingGroups = null;
        // set customer pricing group
        if (customer != null)
        {
            // setting tax id
            if (customer.getEncipheredTaxID() != null)
                model.setTaxID(customer.getEncipheredTaxID());

            if (cargo.getPricingGroup() == null)
            {
                // get customer pricing group
                pricingGroups = CustomerUtilities.getCustomerPricingGroups();
                if (pricingGroups == null)
                {
                    // if pricing groups is null, setting none value
                    cargo.setPricingGroup(pricingGroups);
                    String[] pricingGroupNames = new String[1];
                    pricingGroupNames[0] = CustomerUtilities.NONE_SELECTED;
                    model.setPricingGroups(pricingGroupNames);
                    // set selected index as 0
                    model.setSelectedCustomerPricingGroup(CustomerUtilities.DEFAULT_SELECTED);
                }
                else
                {
                    // set pricing group in cargo
                    cargo.setPricingGroup(pricingGroups);
                    model.setCustomerPricingGroups(pricingGroups);
                    // set pricing group name
                    String[] pricingGroupNames = new String[pricingGroups.length];
                    for (int i = 0; i < pricingGroups.length; i++)
                    {
                        pricingGroupNames[i] = pricingGroups[i].getPricingGroupName(LocaleMap
                                .getLocale(LocaleConstantsIfc.USER_INTERFACE));
                    }
                    cargo.setPricingGroupNames(pricingGroupNames);

                    String[] pGroups = cargo.getPricingGroupNames();
                    String[] useArray = new String[pGroups.length + 1];
                    // use default selected as none
                    useArray[0] = CustomerUtilities.NONE_SELECTED;
                    for (int i = 0; i < pGroups.length; i++)
                    {
                        useArray[i + 1] = pGroups[i];
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
            else
            {
                // get pricing group
                pricingGroups = cargo.getPricingGroup();
                String[] pGroups = cargo.getPricingGroupNames();
                String[] useArray = new String[pGroups.length + 1];
                // use default selected as none
                useArray[0] = CustomerUtilities.NONE_SELECTED;
                for (int i = 0; i < pGroups.length; i++)
                {
                    useArray[i + 1] = pGroups[i];
                }
                model.setCustomerPricingGroups(pricingGroups);
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
        // get available customer groups
        if (cargo.getCustomerGroups() == null)
        {
            CustomerGroupIfc[] groups = CustomerUtilities.getCustomerGroups(locale);
            // append retrieved array -- if it exists -- to "none"
            if (groups == null)
            {
                // use no-discount entry as array
                cargo.setCustomerGroups(cargo.getNoCustomerGroup());
                model.setCustomerGroups(cargo.getNoCustomerGroup());
                // set the customer group index to 0
                model.setSelectedCustomerGroupIndex(0);
                cargo.setSelectedCustomerGroup(0);
            }
            else
            {

                CustomerGroupIfc[] useArray = new CustomerGroupIfc[groups.length + 1];
                System.arraycopy(cargo.getNoCustomerGroup(), 0, useArray, 0, 1);
                System.arraycopy(groups, 0, useArray, 1, groups.length);
                cargo.setCustomerGroups(useArray);
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
                //Changes for Rev 1.0 : Ends
                model.setSelectedCustomerGroupIndex(index);
                cargo.setSelectedCustomerGroup(index);
            }
        }
        else
        {
             model.setCustomerGroups(cargo.getCustomerGroups());
            // set the customer group index to 0
             model.setSelectedCustomerGroupIndex(cargo.getSelectedCustomerGroup());
        }
         return(model);
    }
}
