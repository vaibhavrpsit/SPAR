/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/common/OrderShuttle.java /main/17 2013/07/02 16:32:12 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   07/02/13 - Disable sliding order buttons for Fulfillment at
 *                         login in main menu.
 *    jswan     04/29/13 - When transaction lookup for returns (by trans id,
 *                         customer number, etc.) fails to find a match, the
 *                         applicaiton gives the user the options to return by
 *                         item. The modification supports the same behavior
 *                         for lookup by Order.
 *    jswan     10/25/12 - Modified to support returns by order.
 *    yiqzhao   08/01/12 - Update flow for order search by credit/debit card.
 *    cgreene   08/29/11 - set function id in shuttle, not its own site
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:52 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:53 PM  Robert Pearse   
 *
 *   Revision 1.8  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.7  2004/08/04 14:38:33  jdeleau
 *   @scr 2473 Fix e-journal for filled special orders
 *
 *   Revision 1.6  2004/08/01 16:31:04  crain
 *   @scr 6647 Service Alert: Selecting Orders crashes the register
 *
 *   Revision 1.5  2004/07/22 13:05:21  khassen
 *   @scr 2473 When Filling an  Order, the Location is missing on E journal
 *
 *   Revision 1.4  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
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
 *    Rev 1.0   Aug 29 2003 16:03:32   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:13:06   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:41:08   msg
 * Initial revision.
 * 
 *    Rev 1.1   06 Mar 2002 16:29:34   baa
 * Replace get/setAccessEmployee with get/setOperator
 * Resolution for POS SCR-802: Security Access override for Reprint Receipt does not journal to requirements
 *
 *    Rev 1.0   Sep 24 2001 13:01:06   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.common;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.emessage.EMessageIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.domain.utility.CardDataIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.common.EMessageCargoIfc;
import oracle.retail.stores.pos.services.common.ServiceNameCargoIfc;
import oracle.retail.stores.pos.services.servicealert.ServiceAlertCargo;

/**
 * Shuttle used to transfer Order related data.
 * 
 * @version $Revision: /main/17 $
 */
public class OrderShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 628193609343278414L;

    /** The logger to which log messages will be sent. */
    protected static final Logger logger = Logger.getLogger(OrderShuttle.class);

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/17 $";

    /** Service name of order cancel tour that requires function point access. */
    public static final String ORDER_CANCEL = "CancelOrder";

    //OrderCargoIfc attribute
    protected   OrderIfc                order                =   null;
    protected   boolean                 readOrderFromSummary = false;

    //OrderSummaryCargoIfc attributes
    protected   OrderSummaryEntryIfc[]  summaries   =   null;

    //OrderViewCargoIfc attributes
    protected   boolean                 viewOrder   =   false;

    //ServiceNameCargoIfc attributes
    protected   String                  serviceName =   null;
    protected   int                     serviceType =   -1;

    //OrderSearchCargoIfc attributes
    protected   int                     searchMethod    = -1;
    protected   int                     status          = -1;
    protected   EYSDate                 startDate       = null;
    protected   EYSDate                 endDate         = null;
    protected   String                  orderID         = null;
    protected   boolean                 useDateRange    = false;
    protected   CardDataIfc				cardData		= null;
    protected   String 					itemNumber		= "";
    protected boolean                   retrieveForReturn = false;

    //CustomerSelectCargoIfc attribute
    protected   CustomerIfc             customer        = null;

    //UserAccessCargoIfc attribute
    protected   EmployeeIfc             employee        = null;

    //EMessage from email detail - used to find order with luggage tag
    protected   EMessageIfc             message        = null;
    
    /**
     * Indicates if it is coming from Service Alert
     */
    protected boolean popupMenuNeeded = true;
    
    /**
     * Loads the shuttle data from the parent service's cargo into this shuttle.
     * The data elements to transfer are determined by the interfaces that the
     * parent cargo implements. For example, none of the OrderSearchCargo data
     * elements will be transferred if the calling service's cargo implements
     * OrderSummaryCargoIfc.
     * 
     * @param bus Service Bus
     */
    @Override
    public void load(BusIfc bus)
    {

        super.load(bus);

        CargoIfc cargo = bus.getCargo();

        if (cargo instanceof OrderCargoIfc)
        {
            order                = ((OrderCargoIfc)cargo).getOrder();
            readOrderFromSummary = ((OrderCargoIfc)cargo).isReadOrderFromSummary();
        }

        if (cargo instanceof EMessageCargoIfc)
        {
            message = ((EMessageCargoIfc)cargo).getEMessage();
        }

        //used by various services to access multiple OrderSummaryEntryIfc
        if (cargo instanceof OrderSummaryCargoIfc)
        {
            summaries = ((OrderSummaryCargoIfc)cargo).getOrderSummaries();
        }

        //used by Print Order service to flag Order detail display before printing
        if (cargo instanceof OrderViewCargoIfc)
        {
            viewOrder   = ((OrderViewCargoIfc)cargo).viewOrder();
        }

        //used by multiple services to display different prompt messages
        if (cargo instanceof ServiceNameCargoIfc)
        {
            serviceName   = ((ServiceNameCargoIfc)cargo).getServiceName();
            serviceType   = ((ServiceNameCargoIfc)cargo).getServiceType();
        }

        //used by the Order Lookup service to determine Order search criteria
        if (cargo instanceof OrderSearchCargoIfc)
        {
            useDateRange    = ((OrderSearchCargoIfc)cargo).getDateRange();
            startDate		= ((OrderSearchCargoIfc)cargo).getStartDate();
            endDate			= ((OrderSearchCargoIfc)cargo).getEndDate();
            searchMethod    = ((OrderSearchCargoIfc)cargo).getSearchMethod();
            orderID         = ((OrderSearchCargoIfc)cargo).getOrderID();
            status          = ((OrderSearchCargoIfc)cargo).getStatus();
            cardData		= ((OrderSearchCargoIfc)cargo).getCardData();
            itemNumber		= ((OrderSearchCargoIfc)cargo).getItemNumber();
            retrieveForReturn = ((OrderSearchCargoIfc)cargo).isRetrieveForReturn();
        }

        //used by View Order service to return a Customer from Customer service
        if (cargo instanceof CustomerSelectCargoIfc)
        {
            customer        = ((CustomerSelectCargoIfc)cargo).getSelectedCustomer();
        }

        //used for security access
        if (cargo instanceof UserAccessCargoIfc)
        {
            employee        = ((UserAccessCargoIfc)cargo).getOperator();
        }

        if (cargo instanceof ServiceAlertCargo)
        {
            popupMenuNeeded = false;
        }
    }

    /**
     * Unloads the shuttle data into the cargo. If used as both a launch and
     * return shuttle, this cargo will reset the references in the calling
     * service to refer to the same objects they originally transferred.
     * 
     * @param bus Service Bus
     */
    @Override
    public void unload(BusIfc bus)
    {
        super.unload(bus);

        CargoIfc cargo = bus.getCargo();

        if (cargo instanceof OrderCargoIfc)
        {
            ((OrderCargoIfc)cargo).setOrder(order);
            ((OrderCargoIfc)cargo).setReadOrderFromSummary(readOrderFromSummary);
        }
        
        if (cargo instanceof OrderCargo)
        {
            ((OrderCargo)cargo).setPopupMenuNeeded(popupMenuNeeded);
        }

        if (cargo instanceof EMessageCargoIfc)
        {
            ((EMessageCargoIfc)cargo).setEMessage(message);
        }

        if (cargo instanceof OrderSummaryCargoIfc)
        {
            ((OrderSummaryCargoIfc)cargo).setOrderSummaries(summaries);
        }

        if (cargo instanceof OrderViewCargoIfc)
        {
            ((OrderViewCargoIfc)cargo).setViewOrder(viewOrder);
        }

        if (cargo instanceof ServiceNameCargoIfc)
        {
            ((ServiceNameCargoIfc)cargo).setServiceName(serviceName);
            ((ServiceNameCargoIfc)cargo).setServiceType(serviceType);
        }

        if (cargo instanceof OrderSearchCargoIfc)
        {
            ((OrderSearchCargoIfc)cargo).setDateRange(useDateRange);
            ((OrderSearchCargoIfc)cargo).setStartDate(startDate);
            ((OrderSearchCargoIfc)cargo).setEndDate(endDate);            
            ((OrderSearchCargoIfc)cargo).setSearchMethod(searchMethod);
            ((OrderSearchCargoIfc)cargo).setOrderID(orderID);
            ((OrderSearchCargoIfc)cargo).setStatus(status);
            ((OrderSearchCargoIfc)cargo).setCardData(cardData);
            ((OrderSearchCargoIfc)cargo).setItemNumber(itemNumber);
            ((OrderSearchCargoIfc)cargo).setRetrieveForReturn(retrieveForReturn);
        }

        if (cargo instanceof CustomerSelectCargoIfc)
        {
            ((CustomerSelectCargoIfc)cargo).setSelectedCustomer(customer);
        }

        if (cargo instanceof UserAccessCargoIfc)
        {
            ((UserAccessCargoIfc)cargo).setOperator(employee);
            if (getOrderCancelTourName().equals(bus.getServiceName()))
            {
                ((UserAccessCargoIfc)cargo).setAccessFunctionID(RoleFunctionIfc.CANCEL_ORDER);
            }
            else if (getOrderOptionsTourName().equals(bus.getServiceName()))
            {
                ((UserAccessCargoIfc)cargo).setAccessFunctionID(RoleFunctionIfc.ORDERS);
            }
        }
    }


    /**
     * Return the name of the tour that is traveled for canceling an order.
     * 
     * @return
     */
    protected String getOrderCancelTourName()
    {
        return ORDER_CANCEL;
    }

    /**
     * Returns a string representation of this object.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class: " + getClass().getName() + "(Revision " + getRevisionNumber() + ") @"
                + hashCode());

        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
