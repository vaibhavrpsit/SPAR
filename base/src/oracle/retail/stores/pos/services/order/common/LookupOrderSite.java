/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/common/LookupOrderSite.java /main/31 2014/06/03 17:06:11 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/17/14 - limit the number of Customer Gift Lists retrieved by ICE.
 *    asinton   06/03/14 - added extended customer data locale to the
 *                         CustomerSearchCriteriaIfc.
 *    jswan     04/29/13 - When transaction lookup for returns (by trans id,
 *                         customer number, etc.) fails to find a match, the
 *                         applicaiton gives the user the options to return by
 *                         item. The modification supports the same behavior
 *                         for lookup by Order.
 *    sgu       02/11/13 - disallow pickup/fill for cross border order
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    sgu       10/04/12 - split order item for pickup
 *    sgu       08/29/12 - check xc order currency
 *    sgu       08/29/12 - perform currency check
 *    acadar    08/03/12 - moved customer search criteria
 *    acadar    07/20/12 - fix XC issues
 *    sgu       07/13/12 - clean up order manager api
 *    acadar    06/26/12 - Cross Channel changes
 *    acadar    05/30/12 - merge to tip
 *    acadar    05/29/12 - changes for cross channel
 *    acadar    05/23/12 - CustomerManager refactoring
 *    sgu       05/14/12 - check order customer nullpointer
 *    asinton   03/19/12 - Retrieve customer data for retrieved transaction
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    jswan     04/24/09 - Code review changes.
 *    jswan     04/24/09 - Modified to ensure that orders created in training
 *                         mode can only retrieve in training mode, and
 *                         non-training mode orders can only be retrieved in
 *                         non-training mode.
 *    npoola    04/04/09 - fix to save the Recipient details of PDO from
 *                         service alerts
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         7/3/2007 2:29:23 PM    Ashok.Mondal    CR
 *         27435 :Print the date format on the receipt based on the store
 *         server locale.
 *    3    360Commerce 1.2         3/31/2005 4:28:58 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:21 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:28 PM  Robert Pearse
 *
 *   Revision 1.6.2.1  2004/11/05 21:35:40  cdb
 *   @scr 7527 Modified so that order retrieval would make use of customer in setting locale.
 *
 *   Revision 1.6  2004/06/03 14:47:44  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.5  2004/04/19 14:53:47  tmorris
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/03/03 23:15:09  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:51:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:45  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:03:30   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Mar 10 2003 09:45:54   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.0   Apr 29 2002 15:12:58   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:41:04   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Jan 2002 18:36:10   cir
 * Use OrderReadDataTransaction().readOrder(orderID)
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Sep 24 2001 13:00:14   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.common;

//java imports
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.domain.manager.order.OrderManagerIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteria;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc.SearchType;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

import org.apache.commons.lang3.StringUtils;




/**
 * Retrieves an OrderIfc based upon the OrderSummaryEntryIfc reference.
 */
@SuppressWarnings("serial")
public class LookupOrderSite extends PosSiteActionAdapter
{
    /**
     * class name constant
     */
    public static final String SITENAME = "LookupOrderSite";

    /**
     * Retrieves the order detail object based upon the order summary set
     * in cargo.
     * @param bus the bus arriving at this site
     */
    public void arrive(BusIfc bus)
    {
    	// get utility manager
        UtilityManagerIfc utility =
            (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        OrderCargo cargo = (OrderCargo) bus.getCargo();

        Letter      result  = new Letter (CommonLetterIfc.SUCCESS);

        //get the selected summary from cargo
        OrderSummaryEntryIfc selectedSummary = cargo.getSelectedSummary();

        try
        {
            //lookup order
            OrderManagerIfc orderManager = (OrderManagerIfc)bus.getManager(OrderManagerIfc.TYPE);
            OrderIfc order = orderManager.getOrder(
                    selectedSummary.getOrderID(),
                    utility.getRequestLocales(),
                    cargo.getRegister().getWorkstation().isTrainingMode());

            cargo.setOrder(order);
            cargo.setSplitOrderItems(null); // set it to null so it will be recalcualted since the order has been reset

            CustomerIfc customer = null;
            if(order.getOriginalTransaction() instanceof TenderableTransactionIfc &&
                    StringUtils.isNotBlank(((TenderableTransactionIfc)order.getOriginalTransaction()).getCustomerId()))
            {
                TenderableTransactionIfc tenderableTransaction = (TenderableTransactionIfc)order.getOriginalTransaction();
                CustomerManagerIfc customerManager = (CustomerManagerIfc)bus.getManager(CustomerManagerIfc.TYPE);

                CustomerSearchCriteriaIfc customerCriteria = new CustomerSearchCriteria(SearchType.SEARCH_BY_CUSTOMER_ID,tenderableTransaction.getCustomerId(), utility.getRequestLocales());
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

                try
                {
                    customer = customerManager.getCustomer(customerCriteria);
                }
                catch (DataException de)
                {
                    throw new DataException(DataException.CUSTOMER_INFO_NOT_FOUND_ERROR, "Customer Info Not Found");
                }
                if (customer == null)
                {
                    throw new DataException(DataException.CUSTOMER_INFO_NOT_FOUND_ERROR, "Customer Info Not Found");
                }
                tenderableTransaction.setCustomer(customer);
                order.setCustomer(customer);
            }
            // Use customer locale preferrences for the
            // pole display and receipt  subsystems
            if (customer != null)
            {
                Locale customerLocale = customer.getPreferredLocale();

                if (customerLocale != null)
                {
                    if (!customerLocale.equals(LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT)))
                    {
                        //Do not print date format on the receipt based on the customer locale.
                        //Print the date format as per the store server locale for the picklist order.
                        //LocaleMap.putLocale(LocaleConstantsIfc.RECEIPT, customerLocale);
                        UIUtilities.setUILocaleForCustomer(customerLocale);
                    }
                }
            }
        }
        catch (DataException de)
        {
            if (cargo.isRetrieveForReturn() && de.getErrorCode() == DataException.NO_DATA)
            {
                result = new Letter(CommonLetterIfc.NOT_FOUND);
                cargo.setDataExceptionErrorCode(de.getErrorCode());
            }
            else
            {
                result = new Letter(CommonLetterIfc.DB_ERROR);
                cargo.setDataExceptionErrorCode(de.getErrorCode());
                logger.error( " DB error: " + de.getMessage());
            }
        }

        bus.mail(result,BusIfc.CURRENT);

    }
}
