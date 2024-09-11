/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/common/EnterCustomerInfoSite.java /main/22 2012/04/26 14:39:35 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  04/25/12 - Fixes for Fortify redundant null check, take2
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    asinton   07/01/09 - In metohd getCustomerInfoBeanModel() setting the
 *                         pricingGroups in
 *                         CustomerInfoBeanModel.setCustomerPricingGroups()
 *                         where cargo.getPricingGroups() is not null. Not
 *                         setting the customerPricingGroups here causes the
 *                         customer to lose it's pricing group.
 *    cgreene   04/14/09 - convert pricingGroupID to integer instead of string
 *    mahising  04/02/09 - Fixed customer pricing group display issue if
 *                         customer added and searched
 *    mahising  01/20/09 - fix ejournal issue for customer
 *    mahising  12/04/08 - JUnit fix and SQL fix
 *    mahising  11/21/08 - fixed issue of pricing group
 *    mahising  11/20/08 - update for customer
 *    mahising  11/19/08 - Updated for review comments
 *    mahising  11/17/08 - Updated for Customer
 *    mahising  11/15/08 - Update for Customer Module
 *    mahising  11/13/08 - Added for Customer module for both ORPOS and ORCO
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:01 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:24 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:53 PM  Robert Pearse
 *
 *   Revision 1.2  2004/02/12 16:49:25  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:55:20   CSchellenger
 * Initial revision.
 *
 *    Rev 1.6   Apr 28 2003 09:47:24   baa
 * updates to for business customer
 * Resolution for POS SCR-2217: System crashes if new business customer is created and Return is selected
 *
 *    Rev 1.5   Mar 26 2003 16:41:44   baa
 * fix minor bugs with customer refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.4   Mar 26 2003 10:42:46   baa
 * add changes from acceptance test
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.3   Mar 20 2003 18:18:46   baa
 * customer screens refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.2   Feb 21 2003 09:35:32   baa
 * Changes for contries.properties refactoring
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Sep 18 2002 17:15:20   baa
 * country/state changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:33:50   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:11:32   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:24:18   msg
 * Initial revision.
 *
 *    Rev 1.5   31 Jan 2002 14:13:24   baa
 * journal email address changes
 * Resolution for POS SCR-769: Changing email address on Customer Contact during Customer Find does not journal
 *
 *    Rev 1.4   15 Jan 2002 17:17:38   baa
 * fix defects
 * Resolution for POS SCR-561: Changing the Type on Add Customer causes the default to change
 * Resolution for POS SCR-567: Customer Select Add, Find, Delete display telephone type as Home/Work
 *
 *    Rev 1.3   11 Jan 2002 18:08:14   baa
 * update phone field
 * Resolution for POS SCR-561: Changing the Type on Add Customer causes the default to change
 * Resolution for POS SCR-567: Customer Select Add, Find, Delete display telephone type as Home/Work
 *
 *    Rev 1.2   16 Nov 2001 10:32:08   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.1   24 Oct 2001 15:04:54   baa
 * customer history feature
 * Resolution for POS SCR-209: Customer History
 * Resolution for POS SCR-229: Disable Add/Delete buttons when calling Customer for Find only
 *
 *    Rev 1.0   Sep 21 2001 11:15:00   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:48   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.common;

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
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;

/**
 * Put up Customer Info screen for input of customer name and address
 * information.
 * 
 * @version $Revision: /main/22 $
 */
abstract public class EnterCustomerInfoSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 752349929717334978L;

    /** revision number */
    public static final String revisionNumber = "$Revision: /main/22 $";

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

        // model to use for the UI
        CustomerInfoBeanModel model = new CustomerInfoBeanModel();
        model = CustomerUtilities.populateCustomerInfoBeanModel(customer, utility, pm, model);

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
