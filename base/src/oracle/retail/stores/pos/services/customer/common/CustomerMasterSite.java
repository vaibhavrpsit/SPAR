/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/common/CustomerMasterSite.java /main/16 2012/11/23 12:51:53 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  11/23/12 - Receipt enhancement quickwin changes
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    acadar    09/07/10 - externalize supported localesz
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    mkochumm  02/17/09 - fix locale comparison
 *    mkochumm  02/17/09 - fix locale comparison
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:37 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:41 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:23 PM  Robert Pearse
 *
 *   Revision 1.4  2004/07/08 13:04:21  bvanschyndel
 *   @scr 5230 Customer language defaults to the first supported language.
 *   Now defaults to default store language.
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
 *    Rev 1.0   Aug 29 2003 15:55:16   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Apr 02 2003 17:50:44   baa
 * customer and screen changes
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.3   Mar 24 2003 15:33:34   baa
 * fix null pointer with locale list
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.2   Mar 20 2003 18:18:46   baa
 * customer screens refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.1   Aug 07 2002 19:33:56   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:33:38   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:11:26   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:24:12   msg
 * Initial revision.
 *
 *    Rev 1.5   07 Jan 2002 13:20:46   baa
 * fix journal problems and adding offline
 * Resolution for POS SCR-506: Customer Find prints 'Add Custumer: ' in EJ
 *
 *    Rev 1.4   17 Dec 2001 10:42:36   baa
 * updates to print customer name on status bar
 * Resolution for POS SCR-199: Cust Offline screen returns to Sell Item instead of Cust Opt's
 *
 *    Rev 1.3   16 Nov 2001 10:32:06   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.2   24 Oct 2001 15:04:52   baa
 * customer history feature
 * Resolution for POS SCR-209: Customer History
 * Resolution for POS SCR-229: Disable Add/Delete buttons when calling Customer for Find only
 *
 *    Rev 1.1   23 Oct 2001 16:53:00   baa
 * updates for customer history and for getting rid of CustomerMasterCargo.
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.0   Sep 21 2001 11:14:50   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.common;

import java.util.Locale;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.Gender;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.PersonConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;

/**
 * Display the main information about a Customer
 */
public class CustomerMasterSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 6289092369156645966L;
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/16 $";

    /**
     * Displays the Customer Master Screen.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // get the cargo for the service
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        CustomerIfc customer = cargo.getCustomer();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
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

        Locale supportedLocales[] = LocaleMap.getSupportedLocales();
        String languages[] = new String[supportedLocales.length];
        Locale UILocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        Locale defaultLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
        // get list of available locales
        int languageIndex = 0;

        for (int i=0; i < supportedLocales.length; i++)
        {
            String defaultValue = supportedLocales[i].getDisplayName(UILocale);
            languages[i] = utility.getLocaleDisplayName(supportedLocales[i].toString(), defaultValue, UILocale);

        }

        //if the customer has a preferred language already set, then get that one.
        if (customer.getPreferredLocale() != null)
        {
        	for (int i=0; i < supportedLocales.length; i++)
        	{
        		if (supportedLocales[i].toString().equals(customer.getPreferredLocale().toString()))
        		{
        			languageIndex = i;
        		}
        	}
        }
        else
        {
        	boolean found = false;
        	//Default locales are always 5 chars. Supported locales can be 5 chars or 2 chars.
        	//First look for an exact match..this is to account for supported locales that occur in
        	//the form of en_US and default locale of en_US.
        	for (int i=0; i < supportedLocales.length && !found; i++)
        	{
        		if (supportedLocales[i].toString().length()==5 && supportedLocales[i].toString().equals(defaultLocale.toString()))
        		{
        			languageIndex = i;
        			found = true;
        		}
        	}
        	//if no exact match found, then look for the closest match...this will match supported locale of en with
        	//a default locale of en_US
        	if (!found)
        	{
            	for (int i=0; i < supportedLocales.length && !found; i++)
            	{
            		if (supportedLocales[i].toString().equals(defaultLocale.toString().substring(0,2)))
            		{
            			languageIndex = i;
            			found = true ;
            		}
            	}
        	}

        }

        model.setLanguages(languages);

        model.setSelectedLanguage(languageIndex);
        
        String receiptModes[] = CustomerUtilities.getReceiptPreferenceTypes(utility);        
        
        model.setReceiptModes(receiptModes);
        
        model.setSelectedReceiptMode(customer.getReceiptPreference());

        // display the UI screen
        ui.showScreen(POSUIManagerIfc.CUSTOMER_DETAILS, model);
    }


}
