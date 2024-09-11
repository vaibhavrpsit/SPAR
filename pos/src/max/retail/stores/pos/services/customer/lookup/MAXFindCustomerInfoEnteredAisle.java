/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 *
 * Rev 1.0     Feb 08, 2017		Hitesh Dua			Bug_fix: Unexpected exception while searching through customer name
 * ===========================================================================
 */
package max.retail.stores.pos.services.customer.lookup;

import java.util.ArrayList;
import java.util.List;

// foundation imports
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.common.CustomerInfoEnteredAisle;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
//--------------------------------------------------------------------------
/**
    The CustomerInfoEnteredAisle calls the validate postal code method in
    the Address domain object.  If the postal code is valid, the
    information is stored and a continue letter is mailed. Otherwise an
    error dialog screen is displayed stating that the postal code is
    invalid.
    <p>
    $Revision: /main/11 $
**/
//--------------------------------------------------------------------------
public class MAXFindCustomerInfoEnteredAisle extends CustomerInfoEnteredAisle
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";

    //----------------------------------------------------------------------
    /**
        Saves the customer information in the cargo and mails
        a Continue letter.<p>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        
        String firstName = "";
        String lastName = "";
        String phoneNumber = "";
        String postalCode = "";
        
        // Store the search criteria in cargo.
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        CustomerInfoBeanModel model = (CustomerInfoBeanModel)ui.getModel(POSUIManagerIfc.FIND_CUSTOMER_INFO);

        if ( model.getTelephoneNumber() != null )
        {
            phoneNumber = model.getTelephoneNumber();
        }
        if ( model.getFirstName() != null )
        {
            firstName = model.getFirstName();
        }
        if (model.getLastName() != null)
        {
            lastName = model.getLastName();
        }
        if (model.getPostalCode() != null)
        {
           postalCode = model.getPostalCode();
        }
        
        if ( phoneNumber.isEmpty() && firstName.isEmpty() && lastName.isEmpty() && postalCode.isEmpty() )
        {
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("InvalidCustomerInfo");
            dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.ERROR);
            // Display the dialog.
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else
        {
            CustomerCargo cargo = (CustomerCargo)bus.getCargo();
            CustomerIfc customer = cargo.getCustomer();

            // update the customer from the model
            cargo.setCustomer(updateCustomer(customer, model));
            if(customer.getPhones().isEmpty()){
            	updatePhoneInfo(customer,model);
            }
            cargo.setOriginalCustomer(cargo.getCustomer());
           //changes for rev 1.0 to validate entered details.
            bus.mail(new Letter("validate"), BusIfc.CURRENT);
        }
        
    }

    //added new method for rev 1.0
	public static void updatePhoneInfo(CustomerIfc customer, CustomerInfoBeanModel model) {
		// Get customer's telephone information.

		  PhoneIfc phones[] = model.getPhoneList();

	        if (phones != null)
	        {
	            List<PhoneIfc> phoneList = new ArrayList<PhoneIfc>();
	            for (PhoneIfc phone: phones)
	            {
	                if (phone != null)
	                {
	                    phoneList.add(phone);
	                }
	            }
	            // convert to list;
	            customer.setPhoneList(phoneList);
	        }
	}

}
