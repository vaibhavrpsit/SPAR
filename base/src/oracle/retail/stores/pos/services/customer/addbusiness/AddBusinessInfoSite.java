/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/addbusiness/AddBusinessInfoSite.java /main/20 2012/11/30 15:37:04 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  11/30/12 - Added customer receipt preference
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    sgu       11/03/11 - fix nullpointer in previous customer
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    npoola    03/16/09 - fixed Pricing Groups to display as per the user
 *                         locale
 *    abondala  03/05/09 - get reasoncode text entries from the database, not
 *                         from the bundles.
 *    mahising  01/19/09 - fixed reason code issue
 *    mahising  11/21/08 - fixed issue of pricing group
 *    mahising  11/20/08 - update for customer
 *    mahising  11/19/08 - Updated for review comments
 *    mahising  11/13/08 - Added for Customer module for both ORPOS and ORCO
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.addbusiness;

// foundation imports
import java.util.Locale;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.PricingGroupIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.customer.common.EnterCustomerInfoSite;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;


//--------------------------------------------------------------------------
/**
    Displays the Business Customer Info screen for input of business
    information during add business customer service. <p>
**/
//--------------------------------------------------------------------------
public class AddBusinessInfoSite extends EnterCustomerInfoSite
{
    /**
        revision number
    **/
    public static final String revisionNumber = "";

    /**
     * No Selection value
     */
    public static final String NONE_SELECTED = "None";

    //----------------------------------------------------------------------
    /**
        Displays the Business Customer Info screen for input of business
        information during add business customer service. <p>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        UtilityManagerIfc utility = (UtilityManagerIfc)Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        cargo.setHistoryMode(false);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

        CustomerInfoBeanModel model = getCustomerInfoBeanModel(bus);

        // Get pricing group from cargo
        PricingGroupIfc[] groups = cargo.getPricingGroup();
        String[] pricingGroups = CustomerUtilities.getPricingGroups(groups, locale);
        // Set pricing group into model and customer
        if (pricingGroups != null)
        {
            model.setPricingGroups(pricingGroups);
            model.setCustomerPricingGroups(groups);
        }
        model.setBusinessCustomer(true);
        model.setEditableFields(true);
        model.setTelephoneType(PhoneConstantsIfc.PHONE_TYPE_WORK);
        CodeListIfc reasons = utility.getReasonCodes(cargo.getOperator().getStoreID(), CodeConstantsIfc.CODE_LIST_TAX_EXEMPT_REASON_CODES);
        cargo.setLocalizedTaxExemptReasonCodes(reasons);
        model.setReasonCodes(reasons.getTextEntries(locale));
        model.setReasonCodeTags(CustomerUtilities.getTaxExemptionsTags(reasons, cargo.getOperator().getStoreID()));
        model.setReasonCodeKeys(reasons.getKeyEntries());
        model.setReceiptModes(CustomerUtilities.getReceiptPreferenceTypes(utility));
        //model.inject(reasons, reasons.getDefaultOrEmptyString(locale), locale);

        cargo.setHistoryMode(false);

        // allow the customer to edit in add.
        // set the link done switch
        int linkOrDone = cargo.getLinkDoneSwitch();
        model.setLinkDoneSwitch(linkOrDone);

        NavigationButtonBeanModel nModel = new NavigationButtonBeanModel();

        if (linkOrDone == CustomerMainCargo.LINKANDDONE)
        {
            //enable done
            nModel.setButtonEnabled(CommonActionsIfc.DONE, true);

            // enable link
            nModel.setButtonEnabled(CommonActionsIfc.LINK, true);
        }
        if (linkOrDone == CustomerMainCargo.LINK)
        {
            //disable done
            nModel.setButtonEnabled(CommonActionsIfc.DONE, false);

            // enable link
            nModel.setButtonEnabled(CommonActionsIfc.LINK, true);
        }
        if (linkOrDone == CustomerMainCargo.DONE)
        {
            //disable Link
            nModel.setButtonEnabled(CommonActionsIfc.LINK, false);

            // enable done
            nModel.setButtonEnabled(CommonActionsIfc.DONE, true);
        }

        model.setLocalButtonBeanModel(nModel);

        //Check if History button should be enabled
        nModel.setButtonEnabled(CommonActionsIfc.HISTORY, cargo.isHistoryModeEnabled());

        model.setLocalButtonBeanModel(nModel);
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.ADD_BUSINESS, model);
    }

      //----------------------------------------------------------------------
    /**
        Captures input from  on Customer Info screen
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void depart(BusIfc bus)
    {
        //If sent letter is not Cancel or Undo
        //save data from screen to cargo
        if (!CommonLetterIfc.CANCEL.equals(bus.getCurrentLetter().getName()) &&
            !CommonLetterIfc.UNDO.equals(bus.getCurrentLetter().getName()))
        {

            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            CustomerInfoBeanModel model = (CustomerInfoBeanModel)ui.getModel(POSUIManagerIfc.ADD_BUSINESS);

            CustomerCargo cargo = (CustomerCargo)bus.getCargo();
            CustomerIfc customer = cargo.getCustomer();

            model.setBusinessCustomer(true);
            CustomerIfc newCustomer = CustomerUtilities.updateCustomer(customer, model);
            int index = model.getSelectedCustomerGroupIndex();
            cargo.setSelectedCustomerGroup(index);
            // update the customer from the model
            cargo.setCustomer(newCustomer);
            cargo.setNewCustomer(true);
            cargo.setOriginalCustomer(newCustomer);
            //set dialog name ahead of customer lookup
            cargo.setDialogName(CustomerCargo.TOO_MANY_CUSTOMERS);       // handle possible change in customer group
       }
    }

   }
