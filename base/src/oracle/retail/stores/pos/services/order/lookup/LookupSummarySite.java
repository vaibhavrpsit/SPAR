/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/lookup/LookupSummarySite.java /main/22 2013/05/29 18:20:51 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  05/28/13 - add business name field to the order search screen
 *    yiqzhao   01/20/13 - Use java.util.Date to replace EYSDate in
 *                         OrderSearchCriteria for CO store order search.
 *    yiqzhao   01/15/13 - Convert CustomerInfo to CustomerData and set it to
 *                         OrderSearchCriteria.
 *    sgu       01/09/13 - add iniitating store id to order status search
 *    sgu       01/08/13 - add search by status
 *    sgu       01/07/13 - added order search by status
 *    sgu       01/15/13 - add back order status report
 *    sgu       01/09/13 - add iniitating store id to order status search
 *    sgu       01/08/13 - add search by status
 *    sgu       01/07/13 - added order search by status
 *    sgu       11/09/12 - optimize order lookup flow
 *    yiqzhao   09/14/12 - Set endDate time to 23hour, 59minute and 59 second
 *    yiqzhao   08/01/12 - Modify log info.
 *    yiqzhao   08/01/12 - Update flow for order search by credit/debit card.
 *    sgu       07/17/12 - add order summary search by card token or masked
 *                         number
 *    sgu       07/17/12 - add item serach criteria
 *    sgu       07/16/12 - remove result type from order search criteria
 *    sgu       07/13/12 - clean up order manager api
 *    sgu       07/12/12 - remove retrieve order summary by status or by
 *                         emessage
 *    ohorne    05/17/12 - now using OrderManager
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    jswan     04/24/09 - Modified to ensure that orders created in training
 *                         mode can only retrieve in training mode, and
 *                         non-training mode orders can only be retrieved in
 *                         non-training mode.
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:28:58 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:23:22 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:12:29 PM  Robert Pearse
 * $
 * Revision 1.7  2004/06/03 14:47:44  epd
 * @scr 5368 Update to use of DataTransactionFactory
 *
 * Revision 1.6  2004/04/20 13:17:06  tmorris
 * @scr 4332 -Sorted imports
 *
 * Revision 1.5  2004/04/14 15:17:10  pkillick
 * @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 * Revision 1.4  2004/03/03 23:15:07  bwf
 * @scr 0 Fixed CommonLetterIfc deprecations.
 *
 * Revision 1.3  2004/02/12 16:51:24  mcs
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 21:51:48  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 * Revision 1.1.1.1 2004/02/11 01:04:18
 * cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.0 Aug 29 2003 16:03:40 CSchellenger Initial revision.
 *
 * Rev 1.0 Apr 29 2002 15:12:16 msg Initial revision.
 *
 * Rev 1.0 Mar 18 2002 11:41:24 msg Initial revision.
 *
 * Rev 1.0 Sep 24 2001 13:01:14 MPM Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.lookup;
//foundation imports
import java.util.Date;
import java.util.List;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.manager.order.OrderManagerIfc;
import oracle.retail.stores.domain.order.OrderSearchCriteriaIfc;
import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.domain.utility.CardDataIfc;
import oracle.retail.stores.domain.utility.CustomerData;
import oracle.retail.stores.domain.utility.CustomerDataIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.order.common.OrderCargo;
import oracle.retail.stores.pos.services.order.common.OrderSearchCargoIfc;

//------------------------------------------------------------------------------
/**
 *
 * Retrieves order summarie(s) based upon the search method specified in cargo.
 *
 * @version $Revision: /main/22 $
 */
//------------------------------------------------------------------------------

public class LookupSummarySite extends PosSiteActionAdapter
{

    /** serialVersionUID */
    private static final long serialVersionUID = -7166563720931808130L;

    /**
     * class name constant
     */
    public static final String SITENAME = "LookupSummarySite";

    /**
     * revision number for this class
     */
    public static final String revisionNumber = "$Revision: /main/22 $";

    /**
     * order maximum matches parameter default value if retrieval fails
     */
    protected static final int ORDER_MAXIMUM_MATCHES_DEFAULT = 50;

    /**
     * Determines which type of order retrieval to perform and calls the
     * appropriate method. Mails SUCCESS, DB_ERROR, TOO_MANY,or NOT_FOUND
     * letters based upon the search results.
     *
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        Letter result = new Letter(CommonLetterIfc.DB_ERROR); // default value
        OrderCargo cargo = (OrderCargo) bus.getCargo();

        ParameterManagerIfc pm1 =
            (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

        OrderManagerIfc orderManager = (OrderManagerIfc)bus.getManager(OrderManagerIfc.TYPE);
        switch (cargo.getSearchMethod())
        {
            // order id search
            case OrderSearchCargoIfc.SEARCH_BY_ORDER_ID :
                logger.debug("LookupSummarySite: SEARCH_BY_ORDER_ID");
                result =
                    searchOrderID(
                        pm1,
                        cargo,
                        orderManager);
                break;

            // customer search
            case OrderSearchCargoIfc.SEARCH_BY_CUSTOMER :
                logger.debug("LookupSummarySite: SEARCH_BY_CUSTOMER");
                result =
                    searchOrderCustomer(
                        pm1,
                        cargo,
                        orderManager);
                break;

            case OrderSearchCargoIfc.SEARCH_BY_CREDIT_DEBIT_CARD :
                logger.debug("LookupSummarySite: SEARCH_BY_CREDIT_DEBIT_CARD");
                result =
                    searchOrderCard(
                        pm1,
                        cargo,
                        orderManager);
                break;

            // picklist
            case OrderSearchCargoIfc.SEARCH_FOR_NEW_ORDERS :
                logger.debug("LookupSummarySite: SEARCH_FOR_NEW_ORDERS");
                result =
                    searchNewOrders(
                            pm1,
                            cargo,
                            orderManager);
                break;

        }

        bus.mail(result, BusIfc.CURRENT);

    } // end arrive

    //--------------------------------------------------------------------------
    /**
     * Performs a search for orders with a specified order id, within a date
     * range . if provided, with a store id. Sets cargo for order summaries
     * found or db error upon failure. Determines if too many or no orders have
     * been returned. Sets cargo for order summaries found or db error upon
     * failure.
     * <P>
     * @param pm1
     * @param cargo
     * @param orderManager
     * @return SUCCESS, DB_ERROR, TOO_MANY, or NOT_FOUND letters
     */
    //--------------------------------------------------------------------------
    protected Letter searchOrderID(
        ParameterManagerIfc pm1,
        OrderCargo cargo,
        OrderManagerIfc orderManager)
    {
        // If the order search is based on order id only, do this to go directly to order detail
        // search by skipping order summary search to improve performance.
        OrderSummaryEntryIfc orderSummary = DomainGateway.getFactory().getOrderSummaryEntryInstance();
        orderSummary.setOrderID(cargo.getOrderID());
        cargo.setOrderSummaries(new OrderSummaryEntryIfc[]{orderSummary});
        cargo.setSelectedSummary(orderSummary);

        Letter result = new Letter(CommonLetterIfc.SUCCESS);
        return result;

    } // end searchOrderID

    //--------------------------------------------------------------------------
    /**
     * Performs a search for orders with a specified customer, within a date
     * range . if provided, with a store id. Sets cargo for order summaries
     * found or db error upon failure. Determines if too many or no orders have
     * been returned. Sets cargo for order summaries found or db error upon
     * failure.
     * <P>
     * @param pm1
     * @param cargo
     * @param orderManager
     * @return SUCCESS, DB_ERROR, TOO_MANY, or NOT_FOUND letters
     */
    //--------------------------------------------------------------------------
    protected Letter searchOrderCustomer(
        ParameterManagerIfc pm1,
        OrderCargo cargo,
        OrderManagerIfc orderManager)
    {
        CustomerIfc customer = DomainGateway.getFactory().getCustomerInstance();
        customer = cargo.getSelectedCustomer();
        EYSDate beginDate = null;
        EYSDate endDate = null;
        if (cargo.getDateRange() == true)
        {
            beginDate = cargo.getStartDate();
            if (beginDate != null)
            {
                beginDate.initialize(
                    beginDate.getYear(),
                    beginDate.getMonth(),
                    beginDate.getDay(),
                    00,
                    00,
                    00);
            }
            endDate = cargo.getEndDate();
        }

        boolean trainingMode = cargo.getRegister().getWorkstation().isTrainingMode();

        OrderSearchCriteriaIfc criteria = DomainGateway.getFactory().getOrderSearchCriteriaInstance();
        CustomerDataIfc customerData = new CustomerData();
        customerData.setCustomerID(customer.getCustomerID());
        customerData.setFirstName(customer.getFirstName());
        customerData.setLastName(customer.getLastName());
        customerData.setCompanyName(customer.getCompanyName());
        
        if (customer.getPrimaryPhone()!=null)
        {
            customerData.setPhoneNumber(customer.getPrimaryPhone().getPhoneNumber());
        }

        criteria.configure(customerData,
                           beginDate==null? null:beginDate.toDate(),
                           endDate==null? null:endDate.toDate(),
                           null, /* no item ID specified */
                           trainingMode);

        Letter result = this.search(orderManager, criteria, pm1, cargo);
        return result;
    }

    //--------------------------------------------------------------------------
    /**
     * Performs a search for orders with a specified customer, within a date
     * range . if provided, with a store id. Sets cargo for order summaries
     * found or db error upon failure. Determines if too many or no orders have
     * been returned. Sets cargo for order summaries found or db error upon
     * failure.
     * <P>
     * @param pm1
     * @param cargo
     * @param orderManager
     * @return SUCCESS, DB_ERROR, TOO_MANY, or NOT_FOUND letters
     */
    //--------------------------------------------------------------------------
    protected Letter searchOrderCard(
        ParameterManagerIfc pm1,
        OrderCargo cargo,
        OrderManagerIfc orderManager)
    {
        CardDataIfc cardData = cargo.getCardData();
        EYSDate beginDate = null;
        EYSDate endDate = null;
        Date beginInDateFormat = null;
        Date endInDateFormat = null;
        if (cargo.getDateRange() == true)
        {
            beginDate = cargo.getStartDate();
            if (beginDate != null)
            {
                beginInDateFormat = beginDate.toDate();
                beginDate.initialize(
                    beginDate.getYear(),
                    beginDate.getMonth(),
                    beginDate.getDay(),
                    00,
                    00,
                    00);
            }
            endDate = cargo.getEndDate();
            if (endDate != null)
            {
                endInDateFormat = endDate.toDate();
            	endDate.initialize(
            		endDate.getYear(),
            		endDate.getMonth(),
            		endDate.getDay(),
                    23,
                    59,
                    59);
            }
        }

        boolean trainingMode = cargo.getRegister().getWorkstation().isTrainingMode();

        OrderSearchCriteriaIfc criteria = DomainGateway.getFactory().getOrderSearchCriteriaInstance();

        criteria.configure(cardData, beginInDateFormat, endInDateFormat, cargo.getItemNumber(), trainingMode);

        Letter result = this.search(orderManager, criteria, pm1, cargo);
        return result;
    }

    //--------------------------------------------------------------------------
    /**
     * Performs a search for all orders with status New. Used by pick list
     * service. Sets cargo for order summaries found or db error upon failure.
     * <P>
     * @param cargo
     * @param orderManager
     * @return SUCCESS, DB_ERROR, or NOT_FOUND letters
     */
    //--------------------------------------------------------------------------
    protected Letter searchNewOrders(
        ParameterManagerIfc pm1,
        OrderCargo cargo,
        OrderManagerIfc orderManager)
    {
        int[] status = new int[1]; // order summary stati
        status[0] = cargo.getStatus(); // from calling service
        String storeId = cargo.getStoreStatus().getStore().getStoreID();
        boolean trainingMode = cargo.getRegister().getWorkstation().isTrainingMode();

        OrderSearchCriteriaIfc criteria = DomainGateway.getFactory().getOrderSearchCriteriaInstance();
        criteria.configure(status, null, null, storeId, trainingMode);

        Letter result = this.search(orderManager, criteria, pm1, cargo);
        return result;
    }

    //----------------------------------------------------------------------
    /**
     * Returns a string representation of this object.
     * <P>
     *
     * @return String representation of object
     */
    //----------------------------------------------------------------------

    public String toString()
    { // begin toString()
        // result string
        String strResult =
            new String(
                "Class:  LookupSummarySite (Revision "
                    + getRevisionNumber()
                    + ")"
                    + hashCode());
        // pass back result
        return (strResult);
    } // end toString()

    //----------------------------------------------------------------------
    /**
     * Returns the revision number of the class.
     * <P>
     *
     * @return String representation of revision number
     */
    //----------------------------------------------------------------------

    public String getRevisionNumber()
    { // begin getRevisionNumber()
        // return string
        return (revisionNumber);
    } // end getRevisionNumber()

    //----------------------------------------------------------------------
    /**
     * Perform Order Search using supplied Criteria
     * @param orderManager the OrderManager
     * @param criteria the search Criteria
     * @param pm1 parameter manager (if null then Maximum Matches parameter ignored)
     * @param cargo cargo
     * @return the result letter
     */
    //----------------------------------------------------------------------
    protected Letter search(OrderManagerIfc orderManager,
                            OrderSearchCriteriaIfc criteria,
                            ParameterManagerIfc pm1,
                            OrderCargo cargo)
    {
        Letter result = new Letter(CommonLetterIfc.SUCCESS);

        int maxMatchesInt = getOrderMaximumMatchesValue(pm1);

        try
        {
            List<OrderSummaryEntryIfc> summaries = orderManager.getOrderSummaries(criteria);
            if (summaries.isEmpty())
            {
                result = new Letter(CommonLetterIfc.NOT_FOUND);
                logger.warn(cargo.getSearchMethod() + " - - No MATCHES !!!");
            }
            else if (pm1 != null && summaries.size() > maxMatchesInt)
            {
                result = new Letter(CommonLetterIfc.TOO_MANY);

                logger.warn(cargo.getSearchMethod() + " - Too Many MATCHES > "
                        + Integer.toString(maxMatchesInt)
                        + "");
            }
            else
            {
                OrderSummaryEntryIfc[] orderSumArray = (OrderSummaryEntryIfc[])summaries.toArray(new OrderSummaryEntryIfc[summaries.size()]);
                cargo.setOrderSummaries(orderSumArray);
            }
        }
        catch (DataException de)
        {
            if (de.getErrorCode() == DataException.NO_DATA)
            {
                result = new Letter(CommonLetterIfc.NOT_FOUND);
                cargo.setDataExceptionErrorCode(de.getErrorCode());
            }
            else
            {
                result = new Letter(CommonLetterIfc.DB_ERROR);
                logger.error(cargo.getSearchMethod() + " - DB error: "
                        + de.getMessage()
                        + "");
                 cargo.setDataExceptionErrorCode(de.getErrorCode());
            }
        }
        return result;
    }

    //----------------------------------------------------------------------
    /**
     * Returns the OrderMaximumMatches parameter value.
     * @param pm1 the parameter manager.  If null, then the default is returned
     * @return the parameter value or the default value (50) if parameter manager
     * is null or an error occurs during lookup.
     */
    //----------------------------------------------------------------------
    protected int getOrderMaximumMatchesValue(ParameterManagerIfc pm1)
    {
        int maxMatchesInt = ORDER_MAXIMUM_MATCHES_DEFAULT;
        try
        {
            if( pm1 != null)
            {
                Integer numMaxOrders = pm1.getIntegerValue(ParameterConstantsIfc.ORDER_OrderMaximumMatches);
                maxMatchesInt = numMaxOrders.intValue();
            }
        }
        catch (ParameterException pe)
        {
            logger.warn("Parameter Exception: "
                    + pe.getMessage()
                    + " using default value : "
                    + Integer.toString(ORDER_MAXIMUM_MATCHES_DEFAULT));
        }
        return maxMatchesInt;
    }


} // LookupSummarySite
