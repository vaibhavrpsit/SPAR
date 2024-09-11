/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/lookup/CustomerSelectedAisle.java /main/13 2014/06/17 08:04:17 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/17/14 - limit the number of Customer Gift Lists retrieved by ICE.
 *    asinton   06/16/14 - refetch the customer when customer is selected from
 *                         a list and RetrieveExtendedData property is true
 *    abondala  12/13/12 - customer search criteria fields are all optional.
 *    acadar    05/23/12 - CustomerManager refactoring
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:38 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:24 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:55:56   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   06 Jul 2003 01:44:06   baa
 * missing tex
 * 
 *    Rev 1.0   Apr 29 2002 15:32:22   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:12:46   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:25:30   msg
 * Initial revision.
 * 
 *    Rev 1.2   25 Jan 2002 21:02:34   baa
 * partial fix ui problems
 * Resolution for POS SCR-824: Application crashes on Customer Add screen after selecting Enter
 *
 *    Rev 1.1   16 Nov 2001 10:33:40   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.0   Sep 21 2001 11:15:52   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:10   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.lookup;

// foundation imports
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteria;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DualListBeanModel;
//--------------------------------------------------------------------------
/**
    Aisle that is traversed when the user selects a customer from the
    Customer List screen.
    <p>
    @version $Revision: /main/13 $
**/
//--------------------------------------------------------------------------
public class CustomerSelectedAisle extends LaneActionAdapter
{
    /**
     * 
     */
    private static final long serialVersionUID = -8394150066144190641L;
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";

    //----------------------------------------------------------------------
    /**
        Stores the selected customer in the cargo and mails a Continue
        letter. <p>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // get the cargo for the service
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();

        // get the index of the selected customer
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        DualListBeanModel model = (DualListBeanModel)ui.getModel(cargo.getScreen());
        int selection = model.getSelectedRow();

        // get the customer list from the cargo
        CustomerIfc[] customerList = ((CustomerIfc[])cargo.getCustomerList().toArray());

        // set the selected customer in the cargo. If nothing selected, to avoid AIOB exception, select the top row.
        if(selection == -1)
        {
             selection = 0;
        }
        CustomerIfc customer = fetchExtendedDataCustomer(bus, customerList[selection]);
        cargo.setCustomer(customer);
        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }

    /**
     * Fetches the customer to include extended data if RetrieveExtendedData property is true and
     * fetch succeeds.
     * <p/>
     * @param bus The tour bus.
     * @param customer the customer to re-fetch by ID for the extended data.
     * @return The customer with extended data or the original given customer if extended data customer cannot be fetched.
     */
    protected CustomerIfc fetchExtendedDataCustomer(BusIfc bus, CustomerIfc customer)
    {
        CustomerIfc extendedCustomer = customer;
        boolean retrieveExternalData = Gateway.getBooleanProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "RetrieveExtendedData", false);
        if(customer != null && retrieveExternalData)
        {
            CustomerCargo cargo = (CustomerCargo)bus.getCargo();
            EmployeeIfc operator = cargo.getOperator();
            Locale locale = null;
            if(operator != null && operator.getPreferredLocale() != null)
            {
                locale = operator.getPreferredLocale();
            }
            else
            {
                locale = LocaleMap.getLocale(LocaleMap.DEFAULT);
            }
            CustomerManagerIfc customerManager = (CustomerManagerIfc)bus.getManager(CustomerManagerIfc.TYPE);
            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            CustomerSearchCriteriaIfc searchCriteria = new CustomerSearchCriteria(CustomerSearchCriteriaIfc.SearchType.SEARCH_BY_CUSTOMER_ID, customer.getCustomerID(), utility.getRequestLocales());
            searchCriteria.setExtendedDataRequestLocale(locale);
            int maxCustomerItemsPerListSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxCustomerItemsPerListSize", "10"));
            searchCriteria.setMaxCustomerItemsPerListSize(maxCustomerItemsPerListSize);
            int maxTotalCustomerItemsSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxTotalCustomerItemsSize", "40"));
            searchCriteria.setMaxTotalCustomerItemsSize(maxTotalCustomerItemsSize);
            int maxNumberCustomerGiftLists = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxNumberCustomerGiftLists", "4"));
            searchCriteria.setMaxNumberCustomerGiftLists(maxNumberCustomerGiftLists);
            try
            {
                extendedCustomer = customerManager.getCustomer(searchCriteria);
            }
            catch (DataException e)
            {
                logger.warn("Could not retrieve customer. DataException Error Code: " + e.getErrorCode());
            }
        }
        return extendedCustomer;
    }


}
