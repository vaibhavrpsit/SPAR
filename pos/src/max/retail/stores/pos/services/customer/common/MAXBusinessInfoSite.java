/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  11/April/2013               Izhar                                       MAX-POS-Customer-FES_v1.2.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.customer.common;

import max.retail.stores.pos.ui.beans.MAXCustomerInfoBeanModel;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

//--------------------------------------------------------------------------
/**
    Display the business customer information
    @version $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MAXBusinessInfoSite extends MAXEnterCustomerInfoSite
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: 3$";

    /**
        Constants for actions name
    **/
    public static final String DONE = "Done";
    public static final String LINK = "Link";
    
    //----------------------------------------------------------------------
    /**
       Displays the Busines Customer Info Screen. <p>
       @param  bus Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // get the cargo for the service
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        
        if (!cargo.isNewCustomer())
        {
            CustomerIfc customer = cargo.getCustomer();
            if (customer != null)
            {
                cargo.setOriginalCustomer(customer);
            }
        }
        // //  MAX Rev 1.0 Change : Start 
        MAXCustomerInfoBeanModel model = (MAXCustomerInfoBeanModel) getCustomerInfoBeanModel(bus);   
 //  MAX Rev 1.0 Change : end		
        model.setEditableFields(true);       

        model.setTelephoneType(PhoneConstantsIfc.PHONE_TYPE_WORK);
        // set global buttons
        NavigationButtonBeanModel globalButton = 
            new NavigationButtonBeanModel();
        globalButton.setButtonEnabled("Next", false);
        model.setGlobalButtonBeanModel(globalButton);
        
        // set local buttons
        int linkOrDone = cargo.getLinkDoneSwitch();
        model.setLinkDoneSwitch(linkOrDone) ;

        NavigationButtonBeanModel localButton = 
            new NavigationButtonBeanModel();

        switch (linkOrDone)
        {
            case CustomerMainCargo.LINKANDDONE:
                localButton.setButtonEnabled(DONE, true);
                localButton.setButtonEnabled(LINK, true);
                break;
            case CustomerMainCargo.LINK:
                localButton.setButtonEnabled(DONE, false);
                localButton.setButtonEnabled(LINK, true);
                break;
            case CustomerMainCargo.DONE:
                localButton.setButtonEnabled(DONE, true);
                localButton.setButtonEnabled(LINK, false);
                break;            
        }
        
        //Check if History button should be enabled
        localButton.setButtonEnabled(CustomerCargo.HISTORY, cargo.isHistoryModeEnabled());
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
               MAXCustomerInfoBeanModel model = (MAXCustomerInfoBeanModel)ui.getModel(POSUIManagerIfc.BUSINESS_CUSTOMER);
             
               CustomerCargo cargo = (CustomerCargo)bus.getCargo();
               CustomerIfc customer = cargo.getCustomer();
               cargo.setOriginalCustomer(customer);
 
               
               // update the customer from the model           
               CustomerIfc newCustomer = MAXCustomerUtilities.updateCustomer(customer, model);
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
          customer.setMailPrivacy(true);
          customer.setEMailPrivacy(true);
          customer.setTelephonePrivacy(true);

          customer.setBusinessCustomer(true);
          return customer;
      }   


}