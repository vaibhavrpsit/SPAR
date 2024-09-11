/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/servicealert/LoadSelectedOrderAisle.java /main/23 2014/06/03 17:06:12 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     11/10/14 - limit the number of Customer Gift Lists retrieved by ICE.
 *    asinton   06/03/14 - added extended customer data locale to the
 *                         CustomerSearchCriteriaIfc.
 *    sgu       03/18/13 - remove check for cross currency
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    sgu       08/29/12 - check xc order currency
 *    sgu       08/29/12 - perform currency check
 *    acadar    08/03/12 - moved customer search criteria
 *    acadar    07/20/12 - fix XC issues
 *    sgu       07/13/12 - clean up order manager api
 *    acadar    06/26/12 - Cross Channel changes
 *    acadar    05/30/12 - merge to tip
 *    acadar    05/29/12 - changes for cross channel
 *    acadar    05/23/12 - CustomerManager refactoring
 *    asinton   03/19/12 - Retrieve customer data for retrieved transaction.
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    jswan     04/24/09 - Modified to ensure that orders created in training
 *                         mode can only retrieve in training mode, and
 *                         non-training mode orders can only be retrieved in
 *                         non-training mode.
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:52 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:08 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:20 PM  Robert Pearse
 *
 *   Revision 1.6  2004/06/03 14:47:44  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.5  2004/04/20 13:17:06  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.4  2004/04/14 15:17:11  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.3  2004/02/12 16:51:58  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:06:56   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:03:04   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:47:30   msg
 * Initial revision.
 *
 *    Rev 1.1   Jan 17 2002 21:11:54   dfh
 * use new domain/db
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Sep 24 2001 13:05:28   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:13:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.servicealert;

// imports


import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.domain.manager.order.OrderManagerIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteria;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc.SearchType;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

import org.apache.commons.lang3.StringUtils;

/**
 * This aisle retrieves the complete pick up order object when the user selects
 * a pickup order item from the Service Alert List screen and presses the next
 * button.
 */
@SuppressWarnings("serial")
public class LoadSelectedOrderAisle extends PosLaneActionAdapter
{
    /**
     * LANENAME constant
     */
    public static final String LANENAME = "LoadSelectedOrderAisle";

    /**
     * Retrieve the selected order from the database and load it into cargo.
     *
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
    	 // get utility manager
        UtilityManagerIfc utility =
            (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        ServiceAlertCargo cargo = (ServiceAlertCargo) bus.getCargo();
        Letter letter = new Letter("PrintOrder");

        // Retrieve the selected order and put it in the cargo
        OrderManagerIfc orderManager = (OrderManagerIfc)bus.getManager(OrderManagerIfc.TYPE);

        try
        {
            OrderIfc order = orderManager.getOrder(
                    cargo.getSelectedEntry().getItemID(), //the item identifier is an ORDER ID
                    utility.getRequestLocales(),
                    cargo.getRegister().getWorkstation().isTrainingMode());
            cargo.setOrder(order);
            
            if(order.getOriginalTransaction() instanceof TenderableTransactionIfc &&
                    StringUtils.isNotBlank(((TenderableTransactionIfc)order.getOriginalTransaction()).getCustomerId()))
            {
                TenderableTransactionIfc tenderableTransaction = (TenderableTransactionIfc)order.getOriginalTransaction();
                try
                {
                    CustomerManagerIfc customerManager = (CustomerManagerIfc)bus.getManager(CustomerManagerIfc.TYPE);

                    CustomerSearchCriteriaIfc customerCriteria = new CustomerSearchCriteria(SearchType.SEARCH_BY_CUSTOMER_ID, tenderableTransaction.getCustomerId(), utility.getRequestLocales());
                    Locale extendedDataRequestLocale = cargo.getOperator().getPreferredLocale();
                    if(extendedDataRequestLocale == null)
                    {
                        extendedDataRequestLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);
                    }
                    customerCriteria.setExtendedDataRequestLocale(extendedDataRequestLocale);
                    int maxCustomerItemsPerListSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxCustomerItemsPerListSize", "10"));
                    customerCriteria.setMaxCustomerItemsPerListSize(maxCustomerItemsPerListSize);
                    int maxTotalCustomerItemsSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxTotalCustomerItemsSize", "40"));
                    customerCriteria.setMaxTotalCustomerItemsSize(maxTotalCustomerItemsSize);
                    int maxNumberCustomerGiftLists = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxNumberCustomerGiftLists", "4"));
                    customerCriteria.setMaxNumberCustomerGiftLists(maxNumberCustomerGiftLists);

                    CustomerIfc customer = customerManager.getCustomer(customerCriteria);
                    order.setCustomer(customer);
                }
                catch (DataException de)
                {
                    logger.warn("Could not retrieve customer: " + tenderableTransaction.getCustomerId(), de);
                }
            }
        }
        catch (DataException de)
        {
            cargo.setDataExceptionErrorCode(de.getErrorCode());
            letter = new Letter(CommonLetterIfc.FAILURE);
        }

        bus.mail(letter, BusIfc.CURRENT);
    }
}
