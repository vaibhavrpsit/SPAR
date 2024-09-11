/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *  Rev 1.1     Jan 17, 2016		Ashish Yadav		Intial draft for bug fixing during searching customer through customer button
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.customer.common;

// foundation imports
import java.util.Locale;

import max.retail.stores.domain.utility.MAXCustomerSearchCriteria;
import max.retail.stores.domain.utility.MAXCustomerSearchCriteriaIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc.SearchType;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;

//--------------------------------------------------------------------------
/**
    Lookup the customer ID.
    $Revision: /main/18 $
**/
//--------------------------------------------------------------------------
public class MAXLookupCustomerIDsSite extends PosSiteActionAdapter
{

    private static final long serialVersionUID = -8458849959324199242L;

    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/18 $";

    //----------------------------------------------------------------------
    /**
        Checks for a customer with the given customer ID. <p>
        @param bus the bus arriving at this site
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        String letter = CommonLetterIfc.SUCCESS;
        // do the database lookup
        try
        {
            UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            CustomerManagerIfc customerManager = (CustomerManagerIfc)bus.getManager(CustomerManagerIfc.TYPE);
            LocaleRequestor localeRequestor = utility.getRequestLocales();
            CustomerIfc customer = cargo.getCustomer();
            String customerID = customer.getCustomerID();
            if (Util.isEmpty(customerID))
            {
                cargo.setNewCustomer(true);
                letter = CommonLetterIfc.FAILURE;
            }
            else
            {
                MAXCustomerSearchCriteriaIfc criteria = new MAXCustomerSearchCriteria(SearchType.SEARCH_BY_CUSTOMER_ID, customerID, localeRequestor);
                Locale extendedDataRequestLocale = cargo.getOperator().getPreferredLocale();
                if(extendedDataRequestLocale == null)
                {
                    extendedDataRequestLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);
                }
                criteria.setExtendedDataRequestLocale(extendedDataRequestLocale);
                int maxCustomerItemsPerListSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxCustomerItemsPerListSize", "10"));
                criteria.setMaxCustomerItemsPerListSize(maxCustomerItemsPerListSize);
                int maxTotalCustomerItemsSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxTotalCustomerItemsSize", "40"));
                criteria.setMaxTotalCustomerItemsSize(maxTotalCustomerItemsSize);
                int maxNumberCustomerGiftLists = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxNumberCustomerGiftLists", "4"));
                criteria.setMaxNumberCustomerGiftLists(maxNumberCustomerGiftLists);
                customer = customerManager.getCustomer(criteria);
                
                cargo.setCustomer(customer);
                
                // HPQC 13_1 4007: duplicate customer added when customer looked up after adding a customer
                // clearing flag so looked-up customer is not considered "new" by SaveCustomerSite
                cargo.setNewCustomer(false);
            }
        }
        catch (DataException ce)
        {
            int error = ce.getErrorCode();
            cargo.setDataExceptionErrorCode(error);
           
            letter = CommonLetterIfc.FAILURE;
        }
        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }

}
