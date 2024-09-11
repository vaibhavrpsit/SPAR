/* ===========================================================================
* Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/InstantCreditUtilities.java /main/14 2013/10/15 14:16:21 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   10/10/13 - removed references to social security number and
 *                         replaced with locale agnostic government id
 *    asinton   03/21/12 - update CustomerIfc to use collections generics (i.e.
 *                         List<AddressIfc>) and remove old deprecated methods
 *                         and references to them
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:23 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:08 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:26 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/07/02 20:32:12  dfierling
 *   @scr 5919 - Corrected default country/state for house enrollment
 *
 *   Revision 1.3  2004/02/12 16:50:40  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Dec 23 2003 11:55:20   nrao
 * Display USA as the default country.
 * 
 *    Rev 1.1   Nov 24 2003 19:27:34   nrao
 * Changed copyright message.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.AddressConstantsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.parameter.ParameterIfc;
import oracle.retail.stores.foundation.manager.parameter.EnumeratedListValidator;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.beans.InstantCreditCustomerBeanModel;

/**
 * InstantCreditUtilites contains methods that extends CustomerUtilites
 * It is used to create the InstantCreditCustomerBeanModel.
 * 
 */
public class InstantCreditUtilities extends CustomerUtilities
{
    /**
     * Initialzes the InstantCreditCustomerBeanModel from the model. <p>
     * @param  customer     the customer object containing the data to be
     *                      used for populating the instant credit customer
     *                      model.
     * @param  utility     a reference to the UtilityManager
     * @param  pm            a reference to the parameter manager 
     */
    public static InstantCreditCustomerBeanModel  getInstantCreditCustomerInfo(CustomerIfc customer, 
                                                  UtilityManagerIfc utility,
                                                  ParameterManagerIfc pm)
    {

        // model to use for the UI
        InstantCreditCustomerBeanModel model = new InstantCreditCustomerBeanModel();
       
        if (customer != null )
        {

               model.setFirstName(customer.getFirstName());
               model.setLastName(customer.getLastName());

            // set the address in the model
            List<AddressIfc >addressVector = customer.getAddressList();

            // 3 line address is always true as of 13.1
            model.setThreeLineAddress(true);

            if (!addressVector.isEmpty())
            {
                AddressIfc addr = null;
                int index = 0;
                // look for the first available address
                while (addr == null && index < AddressConstantsIfc.ADDRESS_TYPE_DESCRIPTOR.length)
                {
                   addr = customer.getAddressByType(index);
                   index++;
                }
                
                if (addr != null)
                {
                    Vector lines = addr.getLines();
                    if (lines.size() >= 1)
                    {
                        model.setStreet1((String) lines.elementAt(0));
                    }
        
                    if (lines.size() >= 2)
                    {
                        model.setStreet2((String) lines.elementAt(1));
                    }
        
                    if (lines.size() >= 3)
                    {
                        String line3 = (String) lines.elementAt(2);
                           model.setStreet3(line3);
                           
                           if (!Util.isEmpty(line3) && line3.trim().length() > 0)
                           {
                           model.setThreeLineAddress(true);
                           }
                       
                    }
                    
                    model.setCity(addr.getCity());
        
                     // get list of all available states and selected country and state
                    int countryIndx = utility.getCountryIndex(addr.getCountry(), pm); 
                    model.setCountryIndex(countryIndx);
                    if (Util.isEmpty(addr.getState()))
                    {
                        model.setStateIndex(-1);
                    }
                    else
                    {
                        model.setStateIndex(utility.getStateIndex(countryIndx,addr.getState(),pm));
                    }
               
                    
                    model.setZipCode(addr.getPostalCode());
                }
            }
            else
            {
                // if the address vector was empty, set the state and the country
                // to the store's state and country from parameters
                try
                {    
                    // get country list from parameter
                    ParameterIfc countryList = pm.getSource().getParameter("StoreCountry");
            
                    Serializable[] cList = null;
                    if (countryList.getValidator() instanceof EnumeratedListValidator)
                    {
                        cList = ((EnumeratedListValidator)countryList.getValidator()).getAllowableValues();
                    }
                    
                    // create an ArrayList of countries
                    ArrayList ctrList = new ArrayList(Arrays.asList(cList));
                
                    String storeState = CustomerUtilities.getStoreState(pm);
                    // set country value to USA
                    String storeCountry = (String) ctrList.get(0);
                    int countryIndx = utility.getCountryIndex(storeCountry, pm); 
                    model.setCountryIndex(countryIndx);
                    model.setStateIndex(utility.getStateIndex(countryIndx,
                                                              storeState.substring(3,storeState.length()),
                                                              pm));
                }
                catch (ParameterException e)
                {
                    logger.error( 
                                    "InstantCreditUtilities: Parameter exception: " + e.getMessage() + "");               
                }
            }
                                    
        }
        else
        {
            // if customer information is not available setup default fields on the screen
            try
            {    
                // get country list from parameter
                ParameterIfc countryList = pm.getSource().getParameter("StoreCountry");
            
                Serializable[] cList = null;
                if (countryList.getValidator() instanceof EnumeratedListValidator)
                {
                    cList = ((EnumeratedListValidator)countryList.getValidator()).getAllowableValues();
                }

                // create an ArrayList of countries
                ArrayList ctrList = new ArrayList(Arrays.asList(cList));
                
                String storeState = CustomerUtilities.getStoreState(pm);
                // set country value to USA
                String storeCountry = (String) ctrList.get(1);
                int countryIndx = utility.getCountryIndex(storeCountry, pm); 
                model.setCountryIndex(countryIndx);
                model.setStateIndex(utility.getStateIndex(countryIndx,
                                                          storeState.substring(3,storeState.length()),
                                                          pm));
            }
            catch (ParameterException e)
            {
                logger.error( 
                                "InstantCreditUtilities: Parameter exception: " + e.getMessage() + "");               
            }
        }
        model.setCountries(utility.getCountriesAndStates(pm));  
       
        return model;
    }  
}
