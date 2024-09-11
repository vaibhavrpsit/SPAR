/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
*	
*	Rev 1.0     Oct 19, 2016		Mansi Goel			Changes for Customer FES
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package max.retail.stores.pos.services.customer.common;
import java.util.Enumeration;
import java.util.Vector;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.arts.DataManagerMsgIfc;
import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.EmailAddressIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.journal.JournalableIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

//--------------------------------------------------------------------------
/**
    Site to save customer information to the database.
    <p>
    $Revision: 7$
**/
//--------------------------------------------------------------------------
public class MAXSaveCustomerSite extends PosSiteActionAdapter
{


    /**
	 * 
	 */
	private static final long serialVersionUID = -1306451859085501033L;

    /**
     * Saves customer data to the database.
     *
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        String letterName = CommonLetterIfc.CONTINUE;
        boolean noErrors = true;
        // get the customer to save to the database, don't save in training mode.
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        if (cargo.getRegister().getWorkstation().isTrainingMode() == false)
        {
            CustomerIfc customer = cargo.getCustomer();
            CustomerIfc originalCustomer = cargo.getOriginalCustomer();

            // attempt to do the database update
            try
            {
                StringBuilder jString = new StringBuilder();
                Object[] dataArgs = new Object[2];

                boolean isNewCustomer = cargo.isNewCustomer();
                CustomerManagerIfc customerManager = (CustomerManagerIfc)bus.getManager(CustomerManagerIfc.TYPE);
              
                if (isNewCustomer)
                {
                    customer.setCustomerID(customerManager.getNewCustomerID(cargo.getRegister()));
                    // Journal Customer information
                    //Changes for Rev 1.0 : Starts
                    dataArgs[0] = getNewCustomerJournalString(customer);
                    //Changes for Rev 1.0 : Ends
                    jString.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.ADD_CUSTOMER_LABEL, dataArgs));
                    letterName = "NewCustomerAdded";

                }
                else
                // This is an update.
                {
                    dataArgs[0] = customer.getCustomerID().trim();
                    jString.append(Util.EOL);
                    jString.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.FIND_CUSTOMER_LABEL, dataArgs));
                   
                    if (originalCustomer != null)
                    {
                        jString.append(Util.EOL);
                        jString.append(CustomerUtilities.getChangedCustomerData(originalCustomer, customer));
                    }
                }

                
                customerManager.saveCustomer(customer);
                cargo.setCustomer(customer);

                // if new customer update customer sequence number
                if (cargo.isNewCustomer())
                {
                    customerManager.saveLastSequenceNumber(cargo.getRegister());
                }


                // get the Journal manager
                JournalManagerIfc jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
                if (jmi != null)
                {
                    if (jString.toString().length() > 0)
                    {
                        jmi.setEntryType(JournalableIfc.ENTRY_TYPE_CUST);
                        jmi.journal(cargo.getEmployeeID(), cargo.getTransactionID(), jString.toString());
                    }
                }
                else
                {
                    logger.error("No journal manager found!");
                }

            }
            catch (DataException e)
            {
                logger.error("Unable to save customer \"" + customer.getCustomerID() + "\".", e);

                // check for database connection error
                int errorCode = e.getErrorCode();
                noErrors = false;
                // cannot link if customer was not added to the database
                cargo.setLink(false);
                cargo.setDataExceptionErrorCode(errorCode);
                showErrorDialog(bus, errorCode);
            }

        }// end (isTrainingMode() == false)
        if (noErrors)
        {
          bus.mail(new Letter(letterName), BusIfc.CURRENT);
        }
    }


    /**
     * Show error screen
     *
     * @param bus the bus
     * @param error the error code
     */
    public void showErrorDialog(BusIfc bus, int error)
    {

        String msg[] = new String[2];
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        msg[0] = utility.getErrorCodeString(error);
        msg[1] = utility.retrieveDialogText("DATABASE_ERROR.Contact", DataManagerMsgIfc.CONTACT);

        // display dialog
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        // set  model and display error msg
        UIUtilities.setDialogModel(ui,DialogScreensIfc.ERROR,"CustomerError",msg,CommonLetterIfc.CANCEL);
    }

	
	
	//---------------------------------------------------------------------
    /**
       Returns all changes made to customer object
     @param customer the new customer information
     @return string with data
     **/
    //---------------------------------------------------------------------
    public String getNewCustomerJournalString(CustomerIfc customer)
    {
        StringBuffer jString = new StringBuffer("");

        // test every field
        if (customer.getCustomerID() != null && !customer.getCustomerID().equals(""))
        {
            jString.append("\n" + "Customer ID: ").append(customer.getCustomerID());
        }
        if (customer.getEmployeeID() != null && !customer.getEmployeeID().equals(""))
        {
            jString.append("\n" + "Employee ID: ").append(customer.getEmployeeID());
        }
        if (customer.getFirstName() != null && !customer.getFirstName().equals(""))
        {
            jString.append("\n" + "First Name: ").append(customer.getFirstName());
        }
        if (customer.getLastName() != null && !customer.getLastName().equals(""))
        {
            jString.append("\n" + "Last Name: ").append(customer.getLastName());
        }
        if (customer.getAddresses() != null)
        {
            Enumeration addressEnum = customer.getAddresses().elements();
            while(addressEnum.hasMoreElements())
            {
                AddressIfc address = (AddressIfc)addressEnum.nextElement();
                Vector lines = address.getLines();
                Enumeration linesEnum = lines.elements();
                int lineCount = 0;
                // loop through all address lines
                while(linesEnum.hasMoreElements())
                {
                    lineCount++;
                    String addrLine = (String)linesEnum.nextElement();
                    if (addrLine != null && !addrLine.trim().equals(""))
                    {
                        jString.append("\n" + "Address Line " + lineCount + ": " + addrLine);
                    }
                }
                jString.append("\n" + "City: " + address.getCity());
                jString.append("\n" + "Country: " + address.getCountry());
                jString.append("\n" + "State: " + address.getState());
                jString.append("\n" + "Postal Code: " + address.getPostalCode());
                if (address.getPostalCodeExtension() != null && !address.getPostalCodeExtension().trim().equals(""))
                    jString.append(" - " + address.getPostalCodeExtension());

            }
        }

        // loop through all the phone numbers
		for (int i = 0; i < PhoneConstantsIfc.PHONE_TYPE_DESCRIPTOR.length; i++) {
			PhoneIfc phone = customer.getPhoneByType(i);

			if (phone != null) {
				phone.setExtension("");
				jString.append(phone.toJournalString(LocaleMap
						.getLocale(LocaleConstantsIfc.JOURNAL)));
			}
		}

        // find email address
        EmailAddressIfc emailAddress = customer.getEmailAddress(0);
        if (emailAddress != null)
        {
            String email = emailAddress.getEmailAddress();
            if (email != null && !email.trim().equals(""))
                jString.append("\n" + "Email Address: " + email);
        }

        // Get associated discount
        CustomerGroupIfc[] groups = customer.getCustomerGroups();
        DiscountRuleIfc[] rules = null;
        if (groups != null)
        {
            for (int i = 0; i < groups.length; i++)
            {
                rules = groups[i].getDiscountRules();
                if (rules != null && rules.length > 0)
                {
                    String discount = rules[0].getName(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
                    if (discount != null && !discount.trim().equals(""))
                        jString.append("\n" + "Discount: " + discount);
                }
            }
        }

        return (jString.toString());
    }    
    


}
