/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/lookup/LookupBusinessSite.java /main/19 2014/06/03 17:06:11 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/17/14 - limit the number of Customer Gift Lists retrieved by ICE.
 *    asinton   06/03/14 - added extended customer data locale to the
 *                         CustomerSearchCriteriaIfc.
 *    abondala  12/14/12 - enhancements to the customer search
 *    abondala  12/13/12 - customer search criteria fields are all optional.
 *    acadar    08/03/12 - moved customer search criteria
 *    acadar    06/28/12 - changes for XChannel
 *    acadar    05/29/12 - changes for cross channel
 *    acadar    05/23/12 - CustomerManager refactoring
 *    hyin      05/23/12 - enable phone search field
 *    asinton   03/21/12 - update CustomerIfc to use collections generics (i.e.
 *                         List<AddressIfc>) and remove old deprecated methods
 *                         and references to them
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:58 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:19 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:27 PM  Robert Pearse
 *
 *   Revision 1.8  2004/07/28 19:54:29  dcobb
 *   @scr 6355 Can still search on original business name after it was changed
 *   Modified JdbcSelectBusiness to search for name from pa_cnct table.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.lookup;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.ResultList;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteria;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc.SearchType;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * Query the database for business customers based on the search
 * criteria entered by the user.
 */
@SuppressWarnings("serial")
public class LookupBusinessSite extends PosSiteActionAdapter
{
    /**
     * Sends a business customer lookup inquiry to the database manager.
     * @param  bus Service Bus
     */
    @SuppressWarnings("unchecked")
    public void arrive(BusIfc bus)
    {
        String letterName = CommonLetterIfc.SUCCESS;
        boolean mailLetter = true;
        
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        CustomerIfc customer = cargo.getCustomer();

        // attempt to do the database lookup
        try
        {

            CustomerManagerIfc customerManager = (CustomerManagerIfc)bus.getManager(CustomerManagerIfc.TYPE);
            
            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            int maximumMatches = CustomerUtilities.getMaximumMatches(pm);            
            
            UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            LocaleRequestor locale = utility.getRequestLocales();
            //set the LocaleRequestor in the Customer object
            customer.setLocaleRequestor(locale);
            CustomerSearchCriteriaIfc criteria = new CustomerSearchCriteria(SearchType.SEARCH_BY_BUSINESS_INFO, locale);
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
            CustomerUtilities.configureCustomerSearchCriteria(criteria, customer);
            criteria.setMaximumMatches(maximumMatches);
            
            ResultList resultList = customerManager.getCustomers(criteria);

            cargo.setCustomerList(resultList.getList());

            if (resultList.getTotalRecords() > maximumMatches)
            {
                POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
                
                String msg[] = new String[2];
                msg[0] = String.valueOf(maximumMatches);
                msg[1] = String.valueOf(resultList.getTotalRecords());
                UIUtilities.setDialogModel(ui, DialogScreensIfc.RETRY_CONTINUE, "MaxMatchReachedRetryContinue", msg, new int[] {
                        DialogScreensIfc.BUTTON_RETRY, DialogScreensIfc.BUTTON_CONTINUE },
                        new String[] { CommonLetterIfc.RETRY, CommonLetterIfc.SUCCESS });

                mailLetter = false;
            }
            
        }
        catch (DataException e)
        {
            logger.warn( "" + e + "");
            cargo.setDataExceptionErrorCode(e.getErrorCode());
            letterName = CommonLetterIfc.FAILURE;
        }

        if (mailLetter)
        {
          bus.mail(new Letter(letterName), BusIfc.CURRENT);
        }

    }
    

}
