/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/lookup/LookupEmployeeIDCustomersSite.java /main/18 2014/06/03 17:06:11 asinton Exp $
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
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:58 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:20 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:28 PM  Robert Pearse
 *
 *   Revision 1.7  2004/06/03 14:47:43  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.6  2004/04/20 13:13:10  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.5  2004/04/12 18:49:35  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/03/03 23:15:09  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:32  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:00  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:56:02   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Mar 20 2003 18:18:52   baa
 * customer screens refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.1   Feb 03 2003 14:28:44   bwf
 * Checked if amount of customers returned exceeded maximum number allowed.  Then displayed dialog screen if it did.
 * Resolution for 1945: Too Many Matches does not appear when conditions warrant it
 *
 *    Rev 1.0   Apr 29 2002 15:32:36   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:12:58   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:25:44   msg
 * Initial revision.
 *
 *    Rev 1.1   16 Nov 2001 10:34:06   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.0   Sep 21 2001 11:15:54   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:06   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.lookup;

// java imports
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
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
 * Query database for employee ID's associated customers.
 */
@SuppressWarnings("serial")
public class LookupEmployeeIDCustomersSite extends PosSiteActionAdapter
{

    /**
     * Queries the database for customers associated with an employee ID.
     * @param bus the bus arriving at this site
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
            CustomerManagerIfc customerManager = (CustomerManagerIfc)bus.getManager(CustomerManagerIfc.TYPE);
            UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            int maximumMatches = CustomerUtilities.getMaximumMatches(pm);

            CustomerIfc customer =  cargo.getCustomer();
            String employeeID = customer.getEmployeeID();
           
            CustomerSearchCriteriaIfc criteria = new CustomerSearchCriteria(SearchType.SEARCH_BY_EMPLOYEE_ID, employeeID, utility.getRequestLocales());
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
            criteria.setMaximumMatches(maximumMatches);
            int maxNumberCustomerGiftLists = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxNumberCustomerGiftLists", "4"));
            criteria.setMaxNumberCustomerGiftLists(maxNumberCustomerGiftLists);
            
            // array of customers found in db
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
        catch (DataException de)
        {
            cargo.setDataExceptionErrorCode(de.getErrorCode());          
            letterName = CommonLetterIfc.FAILURE;
        }

        if (mailLetter)
        {
          bus.mail(new Letter(letterName), BusIfc.CURRENT);
        }

    }

}
