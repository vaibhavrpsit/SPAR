/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/common/OrderCargo.java /main/23 2013/08/08 16:51:15 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   11/17/14 - Add fromFulfillment attribute, the getter and setter.
 *    yiqzhao   07/02/13 - Disable sliding order buttons for Fulfillment at
 *                         login in main menu.
 *    jswan     04/29/13 - When transaction lookup for returns (by trans id,
 *                         customer number, etc.) fails to find a match, the
 *                         applicaiton gives the user the options to return by
 *                         item. The modification supports the same behavior
 *                         for lookup by Order.
 *    mkutiana  01/10/13 - implementing Item Age Verification for order pickup
 *    jswan     10/25/12 - Modified to support returns by order.
 *    sgu       10/04/12 - split order item for pickup
 *    yiqzhao   08/01/12 - Update flow for order search by credit/debit card.
 *    yiqzhao   07/27/12 - modify order search flow and populate order cargo
 *                         for searching
 *    yiqzhao   07/23/12 - modify order search flow for xchannel order and
 *                         special order
 *    sgu       07/12/12 - remove retrieve order summary by status or by
 *                         emessage
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    abondala  02/27/09 - LayawayLocation and OrderLocation parameters are
 *                         changed to ReasonCodes.
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/4/2006 5:11:51 PM    Brendan W. Farrell
 *         Remove inventory.
 *    4    360Commerce 1.3         1/25/2006 4:11:33 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:29:13 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:51 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:51 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/15/2005 14:57:25    Jason L. DeLeau 4204:
 *         Remove duplicate instances of UserAccessCargoIfc
 *    3    360Commerce1.2         3/31/2005 15:29:13     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:23:51     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:51     Robert Pearse
 *
 *   Revision 1.7  2004/10/06 02:44:25  mweis
 *   @scr 7012 Special and Web Orders now have Inventory.
 *
 *   Revision 1.6  2004/08/04 14:38:33  jdeleau
 *   @scr 2473 Fix e-journal for filled special orders
 *
 *   Revision 1.5  2004/07/22 13:05:22  khassen
 *   @scr 2473 When Filling an  Order, the Location is missing on E journal
 *
 *   Revision 1.4  2004/03/15 21:43:29  baa
 *   @scr 0 continue moving out deprecated files
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
 *    Rev 1.1   Aug 28 2002 10:08:20   jriggins
 * Introduced the OrderCargo.serviceType property complete with accessor and mutator methods.  Replaced places where service names were being compared (via String.equals()) to String constants in OrderCargoIfc with comparisons to the newly-created serviceType constants which are ints.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:13:00   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:41:04   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 24 2001 13:01:04   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.common;

import java.util.Locale;
import java.util.Vector;

import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.emessage.EMessageIfc;
import oracle.retail.stores.domain.lineitem.SplitOrderItemIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.domain.utility.CardDataIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.EMessageCargoIfc;
import oracle.retail.stores.pos.services.common.ServiceNameCargoIfc;

/**
 * Carries data common to order services.
 * 
 * @version $Revision: /main/23 $
 */
public class OrderCargo extends AbstractFinancialCargo
                        implements OrderCargoIfc,
                                   OrderSummaryCargoIfc,
                                   OrderViewCargoIfc,
                                   ServiceNameCargoIfc,
                                   UserAccessCargoIfc,
                                   EMessageCargoIfc,
                                   OrderSearchCargoIfc //<-extends CustomerSelectCargoIfc,DBErrorCargoIfc
{
    // serialVersionUID
    private static final long serialVersionUID = 8228089202497235087L;

    /** revision number supplied by source-code-control system */
    public static String revisionNumber = "$Revision: /main/23 $";

    /** OrderSummaryCargoIfc attributes */
    protected     Vector<OrderSummaryEntryIfc> summaries  = new Vector<OrderSummaryEntryIfc>(10);
    protected     OrderSummaryEntryIfc    selectedSummary = null;

    /** OrderCargoIfc attribute */
    protected     OrderIfc                order                = null;
    protected     boolean                 readOrderFromSummary = false;

    /** ViewOrderCargoIfc attribute */
    protected     boolean                 viewOrder       = false;

    /** ServiceNameCargoIfc attributes */
    protected     String                  serviceName     = null;

    /** OrderSearchCargoIfc attributes */
    protected     int                     dataExceptionErrorCode = DataException.NONE;
    protected     int                     searchType             = -1;   // not set
    protected     int                     searchStatus           = -1;   // unknown
    protected     EYSDate                 searchStartDate        = null;
    protected     EYSDate                 searchEndDate          = null;
    protected     String                  searchOrderID          = null; // order number
    protected     boolean                 useDateRange           = false;

    /** CustomerSelectCargoIfc attribute */
    protected     CustomerIfc             customer        = null;

    /** EMessage from email detail - used to find order with luggage tag */
    protected     EMessageIfc             message         = null;

    /** CardData */
    protected     CardDataIfc			  cardData 		  = null;

    /** Item number for search */
    protected     String 				  itemNumber      = null;

	/** Service type attribute */
    protected     int                     serviceType     = OrderCargoIfc.SERVICE_TYPE_NOT_SET;

    /**
     * This date is used to hold the DOB for the individual picking up the
     * orders.
     */
    protected EYSDate pickupDOB = null;

    /**
     * order locations from the reason codes list
     */
    protected CodeListIfc orderLocationsList;

    /**
     * order transaction
     */
    protected OrderTransactionIfc orderTransaction = null;

    /**
     * an array of split order items
     */
    protected SplitOrderItemIfc[] splitOrderItems;

    /**
     * Indicates if the order is being retrieved as the basis for a return.
     */
    protected boolean retrieveForReturn = false;

    /**
     * Indicates if it requires popup menu.
     */
    protected boolean popupMenuNeeded = true;
    
    /**
     * From fulfillment flag.  Default is false.
     */
    protected boolean fromFulfillment = false;


    /**
     * Sets the OrderSummaries.
     * 
     * @param OrderSummaryEntryIfc[] - the new value for the property.
     */
    public void setOrderSummaries(OrderSummaryEntryIfc[] newSummaries)
    {
        if (newSummaries != null)
        {
            clearSummaries();
            for (int i = 0; i < newSummaries.length; i++)
            {
                summaries.addElement(newSummaries[i]);
            }
        }
    }

    /**
     * Returns the OrderSummaries as an array.
     * 
     * @return OrderSummaryEntryIfc[] property.
     */
    public OrderSummaryEntryIfc[] getOrderSummaries()
    {
        OrderSummaryEntryIfc[] temp = null;

        summaries.trimToSize();
        temp = new OrderSummaryEntryIfc[summaries.size()];
        summaries.copyInto(temp);

        return temp;
    }

    /**
     * Removes an OrderSummaryEntryIfc reference from the cargo.
     * 
     * @param OrderSummaryEntryIfc to remove
     * @return true if object was in the cargo
     */
    public boolean removeSummary(OrderSummaryEntryIfc summary)
    {
        return summaries.removeElement(summary);
    }

    /**
     * Indicates whether cargo contains a given order summary reference.
     * 
     * @param Order to test
     * @return true if order was in the cargo
     */
    public boolean containsSummary(OrderSummaryEntryIfc summary)
    {
        return summaries.contains(summary);
    }

    /**
     * Removes all OrderSummaryEntryIfc references from the cargo.
     */
    public void clearSummaries()
    {
        summaries.removeAllElements();
    }

    /**
     * Indicates whether cargo contains one or more order summary entries.
     * 
     * @return true if cargo contains summaries
     */
    public boolean hasSummaries()
    {
        return !summaries.isEmpty();
    }

    /**
     * Returns the number of order summary references held in the cargo.
     * 
     * @return integer # of order summaries held by the cargo
     */
    public int countSummaries()
    {
        return summaries.size();
    }

    /**
     * Gets the selected OrderSummaryEntryIfc.
     */
    public OrderSummaryEntryIfc getSelectedSummary()
    {
        return selectedSummary;
    }

    /**
     * Sets the selected OrderSummaryEntry.
     */
    public void setSelectedSummary(OrderSummaryEntryIfc value)
    {
        if (summaries.contains(value))
        {
            selectedSummary = value;
        }
        else
            throw new IllegalArgumentException("OrderSummaryEntryIfc not contained in OrderCargo summaries storage.");
    }

    /**
     * Sets selected OrderSummaryEntryIfc attribute to null.
     * 
     * @param removeFromList flags whether to also remove the summary from the
     *            container vector
     */
    public void clearSelectedSummary(boolean removeFromList)
    {
        if (removeFromList && selectedSummary != null)
        {
            removeSummary(selectedSummary);
        }

        selectedSummary = null;
    }

    /**
     * Adds a single OrderIfc to the cargo.
     * 
     * @param the OrderIfc to add
     */
    public void setOrder(OrderIfc newOrder)
    {
        order = newOrder;
    }

    /**
     * Returns the Order held in cargo.
     * 
     * @return OrderIfc
     */
    public OrderIfc getOrder()
    {
        return order;
    }

    /**
     * Sets the flag that indicates to the PrintOrder service whether an Order
     * detail screen should be displayed.
     */
    public void setViewOrder(boolean value)
    {
        viewOrder = value;
    }

    /**
     * Returns the flag that indicates to the PrintOrder service whether an
     * Order detail screen should be displayed.
     */
    public boolean viewOrder()
    {
        return viewOrder;
    }

    /**
     * Gets the selected customer. This is the customer whose orders we wish to
     * query.
     * 
     * @return Customer
     */
    public CustomerIfc getSelectedCustomer()
    {
        return customer;
    }

    /**
     * Sets the customer selected via Customer Find. This is the customer whose
     * orders we wish to query.
     * 
     * @param Customer
     */
    public void setSelectedCustomer(CustomerIfc value)
    {
        customer = value;
    }

    /**
     * Sets the name of a service which the cargo will be used for.
     * 
     * @param name the service name
     */
    public void setServiceName(String name)
    {
        serviceName = name;
    }

    /**
     * Sets the type of a service which the cargo will be used for. Calling this
     * method will also cause the service name string to attempt to be updated
     * to it's internationalized setting for the user interface subsystem.
     * 
     * @see oracle#retail#stores#pos#services#order#common#OrderCargoIfc
     */
    public void setServiceType(int serviceType)
    {
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        setServiceType(serviceType, locale);
    }

    /**
     * Sets the type of a service which the cargo will be used for. Calling this
     * method will also cause the service name string to attempt to be updated
     * to it's internationalized setting the locale specified.
     * 
     * @param serviceType short indicating the service type. OrderCargoIfc has a
     *            list of service type constants.
     * @param locale the Locale for which the service name needs to be displayed
     * @see oracle#retail#stores#pos#services#order#common#OrderCargoIfc
     */
    public void setServiceType(int serviceType, Locale locale)
    {
        this.serviceType = serviceType;

        if(this.serviceType != OrderCargoIfc.SERVICE_TYPE_NOT_SET)
        {
            UtilityManagerIfc utility =
                (UtilityManagerIfc)Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
            String serviceNameText =
                utility.retrieveText("Common",
                        BundleConstantsIfc.ORDER_BUNDLE_NAME,
                        OrderCargoIfc.SERVICE_NAME_TAG_LIST[serviceType],
                        OrderCargoIfc.SERVICE_NAME_TEXT_LIST[serviceType]);
            setServiceName(serviceNameText);
        }
        else
        {
            setServiceName(null);
        }
    }

    /**
     * Gets the name of a service which the cargo will be used for.
     * 
     * @return String name of the service
     */
    public String getServiceName()
    {
        return serviceName;
    }

    /**
     * Gets the type of a service which the cargo will be used for.
     * 
     * @return short constant indicating the type of service
     * @see oracle#retail#stores#pos#services#order#common#OrderCargoIfc
     */
    public int getServiceType()
    {
        return serviceType;
    }

    /**
     * Returns the error code returned with a DataException.
     * 
     * @return the integer value
     */
    public int getDataExceptionErrorCode()
    {
        return dataExceptionErrorCode;
    }

    /**
     * Sets the error code returned with a DataException.
     * 
     * @param the integer value
     */
    public void setDataExceptionErrorCode(int value)
    {
        dataExceptionErrorCode = value;
    }

    /**
     * Sets the search method which the cargo will be used for.
     * 
     * @param name int value of the searchMethod
     */
    public void setSearchMethod(int name)
    {
        searchType = name;
    }

    /**
     * Gets the name of a search method which the cargo will be used for.
     * 
     * @return int representation of the searchMethod
     */
    public int getSearchMethod()
    {
        return searchType;
    }

    /**
     * Sets the summaryStatus search status which the cargo will be used for.
     * 
     * @param name int value of the searchStatus
     */
    public void setStatus(int name)
    {
        searchStatus = name;
    }

    /**
     * Gets the value of searchStatus, a status search method which the cargo
     * will be used for.
     * 
     * @return int representation of the searchStatus
     */
    public int getStatus()
    {
        return searchStatus;
    }

    /**
     * Sets the summaryStart search date which the cargo will be used for.
     * 
     * @param name int value of the summaryStatus
     */
    public void setStartDate(EYSDate date)
    {
        searchStartDate = date;
    }

    /**
     * Gets the value of summaryStart, search date method which the cargo will
     * be used for.
     * 
     * @return int representation of the summaryStatus
     */
    public EYSDate getStartDate()
    {
        return searchStartDate;
    }

    /**
     * Sets the orderID search criteria which the cargo will be used for.
     * 
     * @param name int value of the summaryStatus
     */
    public void setOrderID(String id)
    {
        searchOrderID = id;
    }

    /**
     * Gets the value of orderID, search orderID method which the cargo will be
     * used for.
     * 
     * @return int representation of the summaryStatus
     */
    public String getOrderID()
    {
        return searchOrderID;
    }

    /**
     * Sets the summaryEnd search date which the cargo will be used for.
     * 
     * @param name int value of the summaryStatus
     */
    public void setEndDate(EYSDate date)
    {
        searchEndDate = date;
    }

    /**
     * Gets the value of summaryEnd, search date method which the cargo will be
     * used for.
     * 
     * @return int representation of the summaryStatus
     */
    public EYSDate getEndDate()
    {
        return searchEndDate;
    }

    /**
     * Sets the useDateRange flag for date range searches.
     * 
     * @param name boolean flag valud of useDateRange
     */
    public void setDateRange(boolean flag)
    {
        useDateRange = flag;
    }

    /**
     * Gets the value of useDateRange, whether to use date range in searches.
     * 
     * @return boolean representation of the useDateRange
     */
    public boolean getDateRange()
    {
        return useDateRange;
    }

    /**
     * Sets the emessage object for order searches.
     * 
     * @param emessage object
     */
    public void setEMessage(EMessageIfc emsg)
    {
        message = emsg;
    }

    /**
     * Gets the value of emessage,
     * 
     * @return EMessageIfc object
     */
    public EMessageIfc getEMessage()
    {
        return message;
    }

    /**
     * Gets the value of credit/debit card data, whether to use card data in
     * searches.
     * 
     * @return
     */
    public CardDataIfc getCardData()
    {
        return cardData;
    }

    /**
     * Sets the card data for card searches.
     * 
     * @param cardData
     */
    public void setCardData(CardDataIfc cardData)
    {
        this.cardData = cardData;
    }

    /**
     * Gets the value of item number, whether to use item number in searches.
     * 
     * @return
     */
    public String getItemNumber()
    {
        return itemNumber;
    }

    /**
     * Sets the item number flag for card search searches.
     * 
     * @param itemNumber
     */
    public void setItemNumber(String itemNumber)
    {
        this.itemNumber = itemNumber;
    }

    /**
     * Gets the order locations
     * 
     * @return CodeListIfc
     */
    public CodeListIfc getOrderLocationsList()
    {
        return orderLocationsList;
    }

    /**
     * Sets the order Locations.
     * 
     * @param CodeListIfc
     */
    public void setOrderLocationsList(CodeListIfc orderLocationsList)
    {
        this.orderLocationsList = orderLocationsList;
    }

    /**
     * Gets the current order transaction.
     * 
     * @return order transaction
     */
    public OrderTransactionIfc getOrderTransaction()
    {
        return orderTransaction;
    }

    /**
     * Sets the order transaction.
     * 
     * @param order transaction
     */
    public void setOrderTransaction(OrderTransactionIfc value)
    {
        orderTransaction = value;
    }

    public SplitOrderItemIfc[] getSplitOrderItems()
    {
        return splitOrderItems;
    }

    public void setSplitOrderItems(SplitOrderItemIfc[] splitOrderItems)
    {
        this.splitOrderItems = splitOrderItems;
    }

    /**
     * @return the readOrderFromSummary
     */
    public boolean isReadOrderFromSummary()
    {
        return readOrderFromSummary;
    }

    /**
     * @param readOrderFromSummary the readOrderFromSummary to set
     */
    public void setReadOrderFromSummary(boolean readOrderFromSummary)
    {
        this.readOrderFromSummary = readOrderFromSummary;
    }

    /**
     * This method returns the DOB of the 'customer/person' picking up the
     * order.
     * 
     * @return Returns The individual picking up the orders DOB.
     */
    public EYSDate getPickupDOB()
    {
        return pickupDOB;
    }

    /**
     * This method sets the DOB of 'customer/person' picking up the order.
     * 
     * @param pickupDOB The individual picking up the orders DOB.
     */
    public void setPickupDOB(EYSDate pickupDOB)
    {
        this.pickupDOB = pickupDOB;
    }

    /**
     * @return the retrieveForReturn
     */
    public boolean isRetrieveForReturn()
    {
        return retrieveForReturn;
    }

    /**
     * @param retrieveForReturn the retrieveForReturn to set
     */
    public void setRetrieveForReturn(boolean retrieveForReturn)
    {
        this.retrieveForReturn = retrieveForReturn;
    }

    /**
     * @return
     */
    public boolean isPopupMenuNeeded()
    {
        return popupMenuNeeded;
    }

    /**
     * @param isFromServiceAlert
     */
    public void setPopupMenuNeeded(boolean popupMenuNeeded)
    {
        this.popupMenuNeeded = popupMenuNeeded;
    }

    /**
     * Sets the <code>fromFulfillment</code> flag.
     * @param value the new value for <code>fromFulfillment</code>
     */
    public void setFromFulfillment(boolean value)
    {
        this.fromFulfillment = value;
    }

    /**
     * Returns the <code>fromFulfillment</code> value.
     * @return the <code>fromFulfillment</code> value
     */
    public boolean isFromFulfillment()
    {
        return this.fromFulfillment;
    }
    /**
     * Returns a string representation of this object.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        StringBuilder strResult = new StringBuilder(getClass().getName());
        strResult.append(" (Revision ").append(getRevisionNumber()).append(")")
                .append(hashCode());

        // pass back result
        return strResult.toString();
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}
