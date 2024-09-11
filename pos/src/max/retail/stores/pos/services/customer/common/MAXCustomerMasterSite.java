/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  7/June/2013               Izhar                                       MAX-POS-Customer-FES_v1.2.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.customer.common;

// foundation imports
import java.util.Locale;

import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.pos.manager.ifc.MAXUtilityManagerIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.Gender;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.PersonConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.common.CustomerMasterSite;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;
//--------------------------------------------------------------------------
/**
    Display the main information about  a Customer
    $Log:
     3    360Commerce 1.2         3/31/2005 4:27:37 PM   Robert Pearse   
     2    360Commerce 1.1         3/10/2005 10:20:41 AM  Robert Pearse   
     1    360Commerce 1.0         2/11/2005 12:10:23 PM  Robert Pearse   
    $
    Revision 1.4  2004/07/08 13:04:21  bvanschyndel
    @scr 5230 Customer language defaults to the first supported language.
    Now defaults to default store language.

    Revision 1.3  2004/02/12 16:49:25  mcs
    Forcing head revision

    Revision 1.2  2004/02/11 21:40:12  rhafernik
    @scr 0 Log4J conversion and code cleanup

    Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
    updating to pvcs 360store-current


**/
//--------------------------------------------------------------------------
public class MAXCustomerMasterSite extends CustomerMasterSite
{

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: 3$";



    //----------------------------------------------------------------------
    /**
       Displays the Customer Master Screen. <p>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // get the cargo for the service
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        CustomerIfc customer = cargo.getCustomer();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        MAXUtilityManagerIfc utility = (MAXUtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        // save the original employee ID
        cargo.setEmployeeID(customer.getEmployeeID());

        // instantiate the bean model for the UI bean
        CustomerInfoBeanModel model = new CustomerInfoBeanModel();
        
        // set the properties of the UI bean model based on the Customer in the cargo
        EYSDate dob = customer.getBirthdate();
        
        // if there is a dob established
        if (dob != null)
        {
            model.setBirthdate(dob);
            model.setBirthdateValid(true);
        }

        // if there is a year of birth established
        if (customer.getYearOfBirth() != PersonConstantsIfc.YEAR_OF_BIRTH_UNSPECIFIED)
        {
            model.setBirthYear(customer.getYearOfBirth());
            model.setBirthYearValid(true);
        }

        model.setCustomerID(customer.getCustomerID());
        model.setEmployeeID(customer.getEmployeeID());
 
        if (Util.isEmpty(customer.getCustomerName()))
        {
            model.setCustomerName(customer.getFirstLastName());
        }
        else
        {
          model.setCustomerName(customer.getCustomerName());
        }

        model.setSalutation(customer.getSalutation());
        String[] genderValues = new String[Gender.getGenderValues().length];
        for (int i=0; i < Gender.getGenderValues().length; i++)
        {
            genderValues[i] = utility.retrieveCommonText(Gender.GENDER_DESCRIPTOR[i]);
        }
        model.setGenderTypes(genderValues);
        model.setGenderIndex(customer.getGenderCode());
        model.setMailPrivacy(customer.getMailPrivacy());
        model.setTelephonePrivacy(customer.getTelephonePrivacy());
        model.setEmailPrivacy(customer.getEMailPrivacy());
// Changes start for code merging(commenting below line as in 14base there is LocaleMap intead of utility)
        //Locale supportedLocales[] = utility.getSupportedLocales();
        Locale supportedLocales[] = LocaleMap.getSupportedLocales();
// Changes ends for code merging
        String languages[] = new String[supportedLocales.length];
        Locale UILocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        Locale defaultLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
        // get list of available locales
        int languageIndex = 0;

        for (int i=0; i < supportedLocales.length; i++)
        {
            languages[i] = supportedLocales[i].getDisplayName(UILocale);
            
            if (customer.getPreferredLocale() != null &&
                supportedLocales[i].toString().equals(customer.getPreferredLocale().toString()))
            {
                languageIndex = i;
            }
            else if (customer.getPreferredLocale() == null &&
                     supportedLocales[i].toString().equals(defaultLocale.toString()))
            {
                languageIndex = i;
            }
        }
        model.setLanguages(languages);

        model.setSelectedLanguage(languageIndex);
        /**MAX Rev 1.0 Change : Start**/
        if(((MAXCustomer)customer).getCustomerType().equalsIgnoreCase("T"))
        model.setEditableFields(false);
        else
        	 model.setEditableFields(true);
        /**MAX Rev 1.0 Change : Start**/
        // display the UI screen
  
        ui.showScreen(POSUIManagerIfc.CUSTOMER_DETAILS, model);
    }


}
