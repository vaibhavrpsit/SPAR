/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/common/BusinessInfoSite.java /main/14 2013/09/18 08:53:42 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  09/17/13 - Fix to initialize privacy options field with false
 *                         to make it consistent with UI
 *    abondala  12/13/12 - customer search criteria fields are all optional.
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:27:18 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:19:54 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:09:42 PM  Robert Pearse   
 * $
 * Revision 1.4  2004/03/03 23:15:06  bwf
 * @scr 0 Fixed CommonLetterIfc deprecations.
 *
 * Revision 1.3  2004/02/12 16:49:25  mcs
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 21:40:12  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 * updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:55:06   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jun 27 2003 18:14:32   baa
 * fix discounts for business customer
 * Resolution for 2728: Linking a Business Customer with a Discount (Gold) Discount is not applied.
 * Resolution for 2741: Return layaway item with receipt, customer discount is not counted for refund
 * 
 *    Rev 1.1   May 27 2003 08:48:00   baa
 * rework customer offline flow
 * Resolution for 2387: Deleteing Busn Customer Lock APP- & Inc. Customer.
 * 
 *    Rev 1.0   Apr 03 2003 15:22:20   baa
 * Initial revision.
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.4   Mar 26 2003 16:41:42   baa
 * fix minor bugs with customer refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.3   Mar 26 2003 10:42:46   baa
 * add changes from acceptance test
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.2   Mar 20 2003 18:18:42   baa
 * customer screens refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.1   Oct 09 2002 11:26:46   kmorneau
 * fix null pointer exception when returning from error screen
 * Resolution for 1814: Customer find by BusinessInfo crashes POS
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.common;

// java imports

// foundation imports
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

//--------------------------------------------------------------------------
/**
    Display the business customer information
    @version $Revision: /main/14 $
**/
//--------------------------------------------------------------------------
public class BusinessInfoSite extends EnterCustomerInfoSite
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
       Displays the Busines Customer Info Screen. <p>
       @param  bus Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // get the cargo for the service
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        
        UtilityManagerIfc utility = (UtilityManagerIfc)Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        
        if (!cargo.isNewCustomer())
        {
            CustomerIfc customer = cargo.getCustomer();
            if (customer != null)
            {
                cargo.setOriginalCustomer(customer);
            }
        }
        
        CustomerInfoBeanModel model = getCustomerInfoBeanModel(bus);        
        model.setEditableFields(true);       

        model.setTelephoneType(PhoneConstantsIfc.PHONE_TYPE_WORK);
        
        CodeListIfc reasons = utility.getReasonCodes(cargo.getOperator().getStoreID(), CodeConstantsIfc.CODE_LIST_TAX_EXEMPT_REASON_CODES);
        cargo.setLocalizedTaxExemptReasonCodes(reasons);
        model.setReasonCodes(reasons.getTextEntries(locale));
        model.setReasonCodeTags(CustomerUtilities.getTaxExemptionsTags(reasons, cargo.getOperator().getStoreID()));
        model.setReasonCodeKeys(reasons.getKeyEntries());
        model.setReceiptModes(CustomerUtilities.getReceiptPreferenceTypes(utility));

        // set global buttons
        NavigationButtonBeanModel globalButton = 
            new NavigationButtonBeanModel();
        globalButton.setButtonEnabled(CommonActionsIfc.NEXT, false);
        model.setGlobalButtonBeanModel(globalButton);
        
        // set local buttons
        int linkOrDone = cargo.getLinkDoneSwitch();
        model.setLinkDoneSwitch(linkOrDone) ;

        NavigationButtonBeanModel localButton = 
            new NavigationButtonBeanModel();

        switch (linkOrDone)
        {
            case CustomerMainCargo.LINKANDDONE:
                localButton.setButtonEnabled(CommonActionsIfc.DONE, true);
                localButton.setButtonEnabled(CommonActionsIfc.LINK, true);
                break;
            case CustomerMainCargo.LINK:
                localButton.setButtonEnabled(CommonActionsIfc.DONE, false);
                localButton.setButtonEnabled(CommonActionsIfc.LINK, true);
                break;
            case CustomerMainCargo.DONE:
                localButton.setButtonEnabled(CommonActionsIfc.DONE, true);
                localButton.setButtonEnabled(CommonActionsIfc.LINK, false);
                break;            
        }
        
        //Check if History button should be enabled
        localButton.setButtonEnabled(CommonActionsIfc.HISTORY, cargo.isHistoryModeEnabled());
        model.setLocalButtonBeanModel(localButton);
               
        // Display customer if linked
        cargo.displayCustomer(bus);
        
        // display the UI screen
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.BUSINESS_CUSTOMER, model);
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
               CustomerInfoBeanModel model = (CustomerInfoBeanModel)ui.getModel(POSUIManagerIfc.BUSINESS_CUSTOMER);
             
               CustomerCargo cargo = (CustomerCargo)bus.getCargo();
               CustomerIfc customer = cargo.getCustomer();
               cargo.setOriginalCustomer(customer);
 
               
               // update the customer from the model           
               CustomerIfc newCustomer = CustomerUtilities.updateCustomer(customer, model);
               int index = model.getSelectedCustomerGroupIndex();
               cargo.setSelectedCustomerGroup(index);

                // initialize remaining customer fields
               cargo.setCustomer(initNonBusinessFields(newCustomer));

            
               //set dialog name ahead of customer lookup
               cargo.setDialogName(CustomerCargo.TOO_MANY_CUSTOMERS);       // handle possible change in customer group
          }
       }
 
    //----------------------------------------------------------------------
      /**
          Initialize Customer object fields not related to business
          @param  bus     Service Bus
      **/
      //----------------------------------------------------------------------
      public CustomerIfc initNonBusinessFields(CustomerIfc customer)
      {
          customer.setMailPrivacy(false);
          customer.setEMailPrivacy(false);
          customer.setTelephonePrivacy(false);

          customer.setBusinessCustomer(true);
          return customer;
      }   


}
