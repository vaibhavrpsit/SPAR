/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/common/LookupTaxIDCustomersSite.java /main/11 2014/06/03 17:06:11 asinton Exp $
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
 *    asinton   03/07/12 - Use new CustomerManager instead of DataTransaction
 *                         method to access customer data.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  11/19/08 - Updated for review comments
 *    mahising  11/13/08 - Added for Customer module for both ORPOS and ORCO
 *    mahising  11/12/08 - added for customer
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.customer.common;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.ResultList;
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
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * Look up customer on the basis of tax ID.
 */
public class LookupTaxIDCustomersSite extends PosSiteActionAdapter
{
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -979100454549239177L;

    /**
     * Look up for customer by tax ID
     *
     * @param bus the bus traversing this lane
     */
    @SuppressWarnings("unchecked")
    public void arrive(BusIfc bus)
    {
        String letterName = CommonLetterIfc.SUCCESS;
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        boolean mailLetter = true;
        // attempt to do the database lookup
        try
        {
            // retrieve the customers by tax ID
            CustomerManagerIfc customerManager = (CustomerManagerIfc)bus.getManager(CustomerManagerIfc.TYPE);
            UtilityManagerIfc utilityManager = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            String taxID = cargo.getTaxID();
            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            int maximumMatches = CustomerUtilities.getMaximumMatches(pm);
           
            LocaleRequestor locale = utilityManager.getRequestLocales();
            
            CustomerSearchCriteriaIfc criteria = new CustomerSearchCriteria(SearchType.SEARCH_BY_TAX_ID, taxID, locale);
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
            cargo.setDataExceptionErrorCode(e.getErrorCode());
            letterName = CommonLetterIfc.FAILURE;
        }
        if (mailLetter)
        {
            bus.mail(new Letter(letterName), BusIfc.CURRENT);
        }
    }
}
