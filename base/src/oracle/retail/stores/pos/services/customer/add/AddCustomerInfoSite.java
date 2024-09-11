/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/add/AddCustomerInfoSite.java /main/21 2013/02/01 10:48:26 abhineek Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    abhine 01/31/13 - Added preference field to CusotmerInfo screen
 *    acadar 05/29/12 - changes for cross channel
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    sgu    11/03/11 - fix nullpointer in previous customer
 *    npoola 12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/28/10 - updating deprecated names
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    npoola 03/16/09 - fixed Pricing Groups to display as per the user locale
 *    mahisi 02/27/09 - clean up code after code review by jack for PDO
 *    aphula 11/27/08 - fixed merge issue
 *    aphula 11/22/08 - Checking files after code review by Naga
 *    aphula 11/17/08 - Pickup Delivery Order
 *    aphula 11/17/08 - Pickup Delivery order
 *    mahisi 11/21/08 - fixed issue of pricing group
 *    mahisi 11/20/08 - update for customer
 *    mahisi 11/19/08 - Updated for review comments
 *    mahisi 11/13/08 - Added for Customer module for both ORPOS and ORCO
 *    acadar 10/23/08 - updates from code review
 * =========================================================================== |

     $Log:
      3    360Commerce 1.2         3/31/2005 4:27:09 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:19:31 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:09:23 PM  Robert Pearse
     $
     Revision 1.4  2004/03/03 23:15:11  bwf
     @scr 0 Fixed CommonLetterIfc deprecations.

     Revision 1.3  2004/02/12 16:49:10  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:41:08  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:54:56   CSchellenger
 * Initial revision.
 *
 *    Rev 1.7   06 Jul 2003 01:12:24   baa
 * missing info on customr screens
 *
 *    Rev 1.6   May 27 2003 08:47:58   baa
 * rework customer offline flow
 * Resolution for 2387: Deleteing Busn Customer Lock APP- & Inc. Customer.
 *
 *    Rev 1.5   May 11 2003 22:46:26   baa
 * modify default button settings
 *
 *    Rev 1.4   May 09 2003 12:50:44   baa
 * more fixes to business customer
 * Resolution for POS SCR-2366: Busn Customer - Tax Exempt- Does not display Tax Cert #
 *
 *    Rev 1.3   Mar 26 2003 10:42:44   baa
 * add changes from acceptance test
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.2   Mar 20 2003 18:17:20   baa
 * customer screens refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.1   Sep 23 2002 16:43:44   baa
 * retrieve descriptor text from bundles
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:34:06   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:10:56   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:23:40   msg
 * Initial revision.
 *
 *    Rev 1.4   11 Jan 2002 18:08:00   baa
 * update phone field
 * Resolution for POS SCR-561: Changing the Type on Add Customer causes the default to change
 * Resolution for POS SCR-567: Customer Select Add, Find, Delete display telephone type as Home/Work
 *
 *    Rev 1.3   07 Jan 2002 13:20:38   baa
 * fix journal problems and adding offline
 * Resolution for POS SCR-506: Customer Find prints 'Add Custumer: ' in EJ
 *
 *    Rev 1.2   16 Nov 2001 10:31:42   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.1   24 Oct 2001 15:04:46   baa
 * customer history feature
 * Resolution for POS SCR-209: Customer History
 * Resolution for POS SCR-229: Disable Add/Delete buttons when calling Customer for Find only
 *
 *    Rev 1.0   Sep 21 2001 11:14:36   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:42   msg
 * header update
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.customer.add;

// foundation imports
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.PricingGroupIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
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
 * Put up Customer Info screen for input of customer name and address
 * information. This screen begins the Customer Add flow.
 * <p>
 * $Revision: /main/21 $
 **/
// --------------------------------------------------------------------------
public class AddCustomerInfoSite extends EnterCustomerInfoSite
{
    
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 4663244332514639468L;

    // ----------------------------------------------------------------------
    /**
     * Displays the Customer Info screen for input of customer name and address
     * information.
     * <p>
     *
     * @param bus Service Bus
     **/
    // ----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        // model to use for the UI
        CustomerInfoBeanModel model = getCustomerInfoBeanModel(bus);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

        PricingGroupIfc[] groups = cargo.getPricingGroup();
        // get customer pricing group names
        String[] pricingGroups = CustomerUtilities.getPricingGroups(groups, locale);
        if (pricingGroups != null)
        {
            model.setPricingGroups(pricingGroups);
            model.setCustomerPricingGroups(groups);
        }

        // Disable History button when adding new customer
        cargo.setHistoryMode(false);

        // allow the customer to edit in add.
        // set the link done switch
        int linkOrDone = cargo.getLinkDoneSwitch();
        model.setLinkDoneSwitch(linkOrDone);

        NavigationButtonBeanModel nModel = new NavigationButtonBeanModel();
        if (linkOrDone == CustomerMainCargo.LINKANDDONE)
        {
            // enable done
            nModel.setButtonEnabled(CommonActionsIfc.DONE, true);

            // enable link
            nModel.setButtonEnabled(CommonActionsIfc.LINK, true);
        }
        if (linkOrDone == CustomerMainCargo.LINK)
        {
            // disable done
            nModel.setButtonEnabled(CommonActionsIfc.DONE, false);

            // enable link
            nModel.setButtonEnabled(CommonActionsIfc.LINK, true);
        }
        if (linkOrDone == CustomerMainCargo.DONE)
        {
            // disable Link
            nModel.setButtonEnabled(CommonActionsIfc.LINK, false);

            // enable done
            nModel.setButtonEnabled(CommonActionsIfc.DONE, true);
        }

        model.setLocalButtonBeanModel(nModel);

        // Check if History button should be enabled
        nModel.setButtonEnabled(CustomerCargo.HISTORY, cargo.isHistoryModeEnabled());
        // check if only link is allowed
        boolean linkOnly = false;
        if (cargo.getLinkDoneSwitch() == CustomerCargo.LINK)
        {
            linkOnly = true;
        }
        nModel.setButtonEnabled(CommonActionsIfc.DONE, !linkOnly);
        model.setEditableFields(true);
        model.setTelephoneType(PhoneConstantsIfc.PHONE_TYPE_HOME);
        model.setSelectedReceiptMode(cargo.getCustomer().getReceiptPreference());
        // show the screen
        POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        uiManager.showScreen(POSUIManagerIfc.ADD_CUSTOMER, model);
    }

    // ----------------------------------------------------------------------
    /**
     * Captures input from on Customer Info screen
     *
     * @param bus Service Bus
     **/
    // ----------------------------------------------------------------------
    public void depart(BusIfc bus)
    {
        // If sent letter is not Cancel or Undo
        // save data from screen to cargo
        if (!CommonLetterIfc.CANCEL.equals(bus.getCurrentLetter().getName())
                && !CommonLetterIfc.UNDO.equals(bus.getCurrentLetter().getName()))
        {

            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            CustomerInfoBeanModel model = (CustomerInfoBeanModel)ui.getModel(POSUIManagerIfc.ADD_CUSTOMER);

            CustomerCargo cargo = (CustomerCargo)bus.getCargo();
            CustomerIfc customer = cargo.getCustomer();

            CustomerIfc newCustomer = CustomerUtilities.updateCustomer(customer, model);

            int index = model.getSelectedCustomerGroupIndex();

            cargo.setSelectedCustomerGroup(index);

            cargo.setCustomer(newCustomer);
            cargo.setNewCustomer(true);
            cargo.setOriginalCustomer(newCustomer);
            // set dialog name ahead of customer lookup
            cargo.setDialogName(CustomerCargo.TOO_MANY_CUSTOMERS); // handle
                                                                   // possible
                                                                   // change in
                                                                   // customer
                                                                   // group
        }
    }

}
