/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/common/CustomerContactSite.java /main/16 2013/02/06 14:07:49 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     02/06/13 - Removed System.out.println
 *    icole     02/06/13 - Change to journal address etc changes when detail is
 *                         selected and enter without any changes.
 *    icole     02/06/13 - Change to journal detail changes.
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    vapartha  02/12/10 - Added code to reset the Customer Discount when the
 *                         user doesnt have Access for the setting the customer
 *                         discount.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:35 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:35 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:19 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/03/03 23:15:06  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.4  2004/02/16 14:40:13  blj
 *   @scr 3838 - cleanup code
 *
 *   Revision 1.3  2004/02/12 16:49:25  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:40:12  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:55:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.7   Jul 09 2003 09:53:48   baa
 * set customer home phone as default
 * Resolution for 3061: Customer Add not Saving Phone Number
 * 
 *    Rev 1.6   May 06 2003 13:41:06   baa
 * updates for business customer
 * Resolution for POS SCR-2203: Business Customer- unable to Find previous entered Busn Customer
 * 
 *    Rev 1.5   Mar 20 2003 18:18:44   baa
 * customer screens refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.4   Feb 21 2003 09:35:30   baa
 * Changes for contries.properties refactoring
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.3   Sep 18 2002 17:15:20   baa
 * country/state changes
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.2   Aug 14 2002 11:13:04   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Jul 18 2002 15:25:38   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:33:32   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:11:20   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:24:08   msg
 * Initial revision.
 * 
 *    Rev 1.6   25 Jan 2002 21:02:16   baa
 * partial fix ui problems
 * Resolution for POS SCR-824: Application crashes on Customer Add screen after selecting Enter
 *
 *    Rev 1.5   15 Jan 2002 17:17:32   baa
 * fix defects
 * Resolution for POS SCR-561: Changing the Type on Add Customer causes the default to change
 * Resolution for POS SCR-567: Customer Select Add, Find, Delete display telephone type as Home/Work
 *
 *    Rev 1.4   11 Jan 2002 18:08:08   baa
 * update phone field
 * Resolution for POS SCR-561: Changing the Type on Add Customer causes the default to change
 * Resolution for POS SCR-567: Customer Select Add, Find, Delete display telephone type as Home/Work
 *
 *    Rev 1.3   16 Nov 2001 10:32:00   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.2   24 Oct 2001 15:04:50   baa
 * customer history feature
 * Resolution for POS SCR-209: Customer History
 * Resolution for POS SCR-229: Disable Add/Delete buttons when calling Customer for Find only
 *
 *    Rev 1.1   23 Oct 2001 16:52:56   baa
 * updates for customer history and for getting rid of CustomerMasterCargo.
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.0   Sep 21 2001 11:14:58   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.common;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

/**
 * Displays Customer Contact screen.
 * 
 * @version $Revision: /main/16 $
 */
@SuppressWarnings("serial")
public class CustomerContactSite extends EnterCustomerInfoSite
{
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/16 $";

    /**
     * Displays the Customer Contact screen.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // get the cargo for the service
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        if (!cargo.isNewCustomer())
        {
            CustomerIfc customer = cargo.getCustomer();
            if (customer != null && cargo.getOriginalCustomer() == null)
            {
                cargo.setOriginalCustomer(customer);
            }
        }

        // instantiate the bean model for the UI bean
        CustomerInfoBeanModel model = getCustomerInfoBeanModel(bus);

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
        if (cargo.isHistoryModeEnabled())
        {
            nModel.setButtonEnabled(CustomerCargo.HISTORY, true);
        }
        else
        {
            nModel.setButtonEnabled(CustomerCargo.HISTORY, false);
        }

        // Display customer if linked
        cargo.displayCustomer(bus);
        model.setEditableFields(true);

        // setup default phone type
        if (!model.isBusinessCustomer())
        {
            model.setTelephoneType(PhoneConstantsIfc.PHONE_TYPE_HOME);
        }

        // display the UI screen
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.CUSTOMER_INFO, model);
    }

    /**
     * Captures input from on Customer Info screen
     * 
     * @param bus Service Bus
     */
    @Override
    public void depart(BusIfc bus)
    {
        // If sent letter is not Cancel or Undo
        // save data from screen to cargo
        if (!CommonLetterIfc.CANCEL.equals(bus.getCurrentLetter().getName())
                && !CommonLetterIfc.UNDO.equals(bus.getCurrentLetter().getName()))
        {

            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            CustomerInfoBeanModel model = (CustomerInfoBeanModel)ui.getModel(POSUIManagerIfc.CUSTOMER_INFO);

            CustomerCargo cargo = (CustomerCargo)bus.getCargo();
            CustomerIfc customer = cargo.getCustomer();
            if(cargo.getOriginalCustomer() == null)
            {
                cargo.setOriginalCustomer(customer);
            }    
            CustomerIfc newCustomer = CustomerUtilities.updateCustomer(customer, model);
            int index = model.getSelectedCustomerGroupIndex();
            cargo.setSelectedCustomerGroup(index);
            // update the customer from the model
            cargo.setCustomer(newCustomer);

            // set AddFind flag to true as its a find operation
            cargo.setAddFind(true);

            // set dialog name ahead of customer lookup
            cargo.setDialogName(CustomerCargo.TOO_MANY_CUSTOMERS); // handle possible change in customer group
        }
    }
}
