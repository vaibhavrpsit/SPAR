/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/lineitem/OrderItemStatus.java /main/39 2014/07/17 15:09:41 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   07/15/14 - Add originalLineNumber attribute.
 *    sgu       06/22/14 - insert discount and tax status for take with items
 *                         added during pickup
 *    yiqzhao   06/11/14 - Add originalTransactionId attribute for order line
 *                         item status.
 *    cgreene   03/11/14 - add support for returning ASA ordered items
 *    abondala  09/04/13 - initialize collections
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    abhineek  03/22/13 - fix for picked up status missing in receipt
 *    sgu       01/14/13 - process pickup or cancel for store order items
 *    sgu       01/07/13 - add quantity pending
 *    sgu       12/12/12 - prorate tax for order pickup, cancel, and return
 *    sgu       11/21/12 - update discount and tax status for order pickup or
 *                         cancel
 *    sgu       11/21/12 - added support for order item discount and tax status
 *    sgu       10/25/12 - add filled status for order and order item
 *    sgu       10/16/12 - clean up order item quantities
 *    sgu       10/15/12 - add ordered amount
 *    sgu       10/09/12 - use new createOrderTransaction from UI flow
 *    sgu       10/09/12 - create pickup cancel order transaction from an order
 *    sgu       10/05/12 - collect item quantity to pickup or cancel for
 *                         partial pickup/cancel
 *    sgu       09/19/12 - add completed and cancelled amount at order line
 *                         item level
 *    sgu       07/03/12 - added xc order ship delivery date, carrier code and
 *                         type code
 *    sgu       05/22/12 - remove order filled status
 *    sgu       04/25/12 - fixed indentation
 *    sgu       04/18/12 - enhance order item tables to support xc
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  03/06/09 - fixed issue for suspended transaction
 *    mahising  03/03/09 - Fixed issue for delivery item search at service
 *                         alert
 *    jswan     02/28/09 - Second code review changes.
 *    mahising  02/27/09 - clean up code after code review by jack for PDO
 *    mahising  02/26/09 - Rework for PDO functionality
 *    jswan     02/21/09 - Initialized delivery detail ID to none positive
 *                         integer so that the app can tell when a order does
 *                         not have delivery details.
 *    aphulamb  12/23/08 - Mock padding fix and PDO flow related changes for
 *                         buttons enable/disable
 *    aphulamb  11/27/08 - checking files after merging code for receipt
 *                         printing by Amrish
 *    mchellap  11/13/08 - Inventory Reservation Module
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         4/12/2008 5:44:57 PM   Christian Greene
 *         Upgrade StringBuffer to StringBuilder
 *    5    360Commerce 1.4         4/25/2007 10:00:40 AM  Anda D. Cadar   I18N
 *         merge
 *    4    360Commerce 1.3         1/22/2006 11:41:40 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:29:13 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:51 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:52 PM  Robert Pearse
 *
 *   Revision 1.4  2004/09/23 00:30:54  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/02/12 17:13:57  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:26:32  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:32  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:38:02   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:58:30   msg
 * Initial revision.
 *
 *    Rev 1.2   25 Apr 2002 10:28:04   pdd
 * Removed unnecessary BigDecimal instantiations.
 * Resolution for POS SCR-1610: Remove inefficient instantiations of BigDecimal
 *
 *    Rev 1.1   Mar 18 2002 23:04:30   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:24:16   msg
 * Initial revision.
 *
 *    Rev 1.5   Feb 05 2002 16:35:56   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 *
 *    Rev 1.4   26 Jan 2002 18:40:28   cir
 * Added reference to setCloneAttributes and equals
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.3   15 Jan 2002 18:23:16   cir
 * Add reference attribute
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.2   10 Jan 2002 17:37:22   cir
 * Use line item status descriptors instead of order ones
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   Dec 26 2001 17:25:38   mpm
 * Modified to properly handle order transaction retrieval.
 * Resolution for Domain SCR-14: Special Order modifications
 *
 *    Rev 1.0   13 Dec 2001 06:37:16   mpm
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.domain.lineitem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderConstantsIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderDeliveryDetailIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.TransactionID;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSStatus;
import oracle.retail.stores.domain.utility.EYSStatusIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;

/**
 * This class describes the status of an ordered item.
 *
 * @version $Revision: /main/39 $
 */
public class OrderItemStatus implements OrderItemStatusIfc
{
    // This id is used to tell  the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -20954675560725824L;

    /**
     * revision number supplied by source-code-control system
     */
    public static String revisionNumber = "$Revision: /main/39 $";

    /**
     * A boolean flag indicating if the order item is an cross channel item
     */
    protected boolean isCrossChannelItem = false;

    /**
     * Indicator of what system is managing the external order. e.g. ATG or
     * Siebel. Defaults to ExternalOrderConstantsIfc#TYPE_UNKNOWN
     * @see SaleReturnTransaction#externalOrderType
     */
    protected int externalOrderType = ExternalOrderConstantsIfc.TYPE_UNKNOWN;

    /**
     * status of ordered item
     */
    protected EYSStatusIfc status = null;

    /**
     * quantity of item ordered
     */
    protected BigDecimal quantityOrdered = null;

    /**
     * quantity of ordered item picked up
     */
    protected BigDecimal quantityPickedUp = null;

    /**
     * quantity of ordered item new (not yet processed)
     */
    protected BigDecimal quantityNew = null;

    /**
     * quantity of ordered item pending to be filled
     */
    protected BigDecimal quantityPending = null;

    /**
     * quantity of ordered item available to be picked up or shipped
     */
    protected BigDecimal quantityPicked = null;

    /**
     * quantity of ordered item shipped
     */
    protected BigDecimal quantityShipped = null;

    /**
     * quantity of ordered item returned
     */
    protected BigDecimal quantityReturned = null;

    /**
     * quantity of ordered item cancelled
     */
    protected BigDecimal quantityCancelled = null;

    /**
     * quantity of ordered item to pickup
     */
    protected BigDecimal quantityPickup = null;

    /**
     * A hash table with order item status as its key. It records quantity to
     * pick up from a given order item status.
     */
    protected Hashtable<Integer, BigDecimal> quantityPickupFrom = null;

    /**
     * quantity of ordered item to cancel
     */
    protected BigDecimal quantityCancel = null;

    /**
     * A hash table with order item status as its key. It records quantity to
     * cancel from a given order item status.
     */
    protected Hashtable<Integer, BigDecimal> quantityCancelFrom = null;

    /**
     * total ordered amount
     */
    protected CurrencyIfc orderedAmount = null;

    /**
     * completed (picked up or shipped) amount
     */
    protected CurrencyIfc completedAmount = null;

    /**
     * cancelled amount
     */
    protected CurrencyIfc cancelledAmount = null;

    /**
     * returned amount
     */
    protected CurrencyIfc returnedAmount = null;

    /**
     * deposit amount
     */
    protected CurrencyIfc depositAmount = null;

    /**
     * reference
     */
    protected String reference = "";

    /**
     * value being used for Order Item Disposition
     */
    protected int itemDispositionCode = OrderConstantsIfc.ORDER_ITEM_DISPOSITION_SALE;

    /**
     * The store id where the item is to be picked up
     */
    protected String pickupStoreID = null;

    /**
     * pickup date
     */
    protected EYSDate pickupDate = null;

    /**
     * pickup first name
     */
    protected String pickupFirstName = null;

    /**
     * pickup last name
     */
    protected String pickupLastName = null;

    /**
     * pickup contact phone number
     */
    protected PhoneIfc pickupContact = null;

    /**
     * A boolean flag indicating if the item needs to be shipped to the store
     * for pickup
     */
    protected boolean isShipToStoreForPickup = false;

    /**
     * Order Delivery Detail
     */
    protected OrderDeliveryDetailIfc orderDeliveryDetail = null;

    /**
     * The fulfillment order id of this item
     */
    protected String fulfillmentOrderID = null;

    /**
     * The list of discount status
     */
    protected List<OrderItemDiscountStatusIfc> discountStatusList;

    /**
     * The list of tax status
     */
    protected List<OrderItemTaxStatusIfc> taxStatusList;
    
    /**
     * Original transaction id
     */
    protected TransactionIDIfc originalTransactionId;
    
    /**
     * Original business date
     */ 
    protected EYSDate originalBusinessDate;
    
    /**
     * Original line number of this line item. This member was introduced in
     * order for return take with item in order transaction.
     */
    protected int originalLineNumber = -1;

    /**
     * Constructs OrderItemStatus object.
     * <P>
     */
    public OrderItemStatus()
    {
        clearQuantityAndAmount();
        orderDeliveryDetail=DomainGateway.getFactory().getOrderDeliveryDetailInstance();
        pickupContact = DomainGateway.getFactory().getPhoneInstance();
        discountStatusList = new ArrayList<OrderItemDiscountStatusIfc>();
        taxStatusList = new ArrayList<OrderItemTaxStatusIfc>();
        originalTransactionId = new TransactionID();
        originalBusinessDate = new EYSDate();
        initializeStatus();
    }

    /**
     * Creates clone of this object.
     * 
     * @return Object clone of this object
     */
    public Object clone()
    {
      // instantiate new object
        OrderItemStatus c = new OrderItemStatus();

        // set values
        setCloneAttributes(c);

        // pass back Object
        return ((Object)c);
    }

    /**
     * Sets attributes in clone of this object.
     * 
     * @param newClass new instance of object
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void setCloneAttributes(OrderItemStatus newClass)
    {
        newClass.setCrossChannelItem(isCrossChannelItem());
        newClass.setExternalOrderType(getExternalOrderType());
        if (status != null)
        {
            newClass.setStatus((EYSStatusIfc)getStatus().clone());
        }
        newClass.setQuantityOrdered(getQuantityOrdered());
        newClass.setQuantityPickedUp(getQuantityPickedUp());
        newClass.setQuantityNew(getQuantityNew());
        newClass.setQuantityPending(getQuantityPending());
        newClass.setQuantityPicked(getQuantityPicked());
        newClass.setQuantityShipped(getQuantityShipped());
        newClass.setQuantityCancelled(getQuantityCancelled());
        newClass.setQuantityReturned(getQuantityReturned());
        newClass.setQuantityPickup(getQuantityPickup());
        if (getQuantityPickupFrom() != null)
        {
            newClass.setQuantityPickupFrom((Hashtable)getQuantityPickupFrom().clone());
        }
        else
        {
        	newClass.setQuantityPickupFrom(null);
        }
        newClass.setQuantityCancel(getQuantityCancel());
        if (getQuantityCancelFrom() != null)
        {
            newClass.setQuantityCancelFrom((Hashtable)getQuantityCancelFrom().clone());
        }
        else
        {
        	newClass.setQuantityCancelFrom(null);
        }
        newClass.setOrderedAmount((CurrencyIfc)getOrderedAmount().clone());
        newClass.setCompletedAmount((CurrencyIfc)getCompletedAmount().clone());
        newClass.setCancelledAmount((CurrencyIfc)getCancelledAmount().clone());
        newClass.setReturnedAmount((CurrencyIfc)getReturnedAmount().clone());
        newClass.setDepositAmount((CurrencyIfc)getDepositAmount().clone());
        newClass.setReference(new String(reference));
        newClass.setItemDispositionCode(getItemDispositionCode());
        newClass.setPickupStoreID(getPickupStoreID());
        if (getPickupDate() != null)
        {
            newClass.setPickupDate((EYSDate)getPickupDate().clone());
        }
        newClass.setPickupFirstName(getPickupFirstName());
        newClass.setPickupLastName(getPickupLastName());
        if (getPickupContact() != null)
        {
        	newClass.setPickupContact((PhoneIfc)getPickupContact().clone());
        }
        newClass.setShipToStoreForPickup(isShipToStoreForPickup());
        if (getDeliveryDetails() != null)
        {
            newClass.setDeliveryDetails((OrderDeliveryDetailIfc)getDeliveryDetails().clone());
        }
        newClass.setFulfillmentOrderID(getFulfillmentOrderID());

        // clone discount status list
        List<OrderItemDiscountStatusIfc> newDiscountStatusList = new ArrayList<OrderItemDiscountStatusIfc>();
        if (getDiscountStatusList() != null)
        {
            for (OrderItemDiscountStatusIfc discountStatus : getDiscountStatusList())
            {
                newDiscountStatusList.add((OrderItemDiscountStatusIfc)discountStatus.clone());
            }
        }
        newClass.setDiscountStatusList(newDiscountStatusList);

        // clone tax status list
        List<OrderItemTaxStatusIfc> newTaxStatusList = new ArrayList<OrderItemTaxStatusIfc>();
        if (getTaxStatusList() != null)
        {
            for (OrderItemTaxStatusIfc taxStatus : getTaxStatusList())
            {
                newTaxStatusList.add((OrderItemTaxStatusIfc)taxStatus.clone());
            }
        }
        newClass.setTaxStatusList(newTaxStatusList);

        if (getOriginalTransactionId()!=null)
        {
            newClass.setOriginalTransactionId((TransactionIDIfc)getOriginalTransactionId().clone());
        }
        newClass.setOriginalBusinessDate((EYSDate)getOriginalBusinessDate());
        
        newClass.setOriginalLineNumber(getOriginalLineNumber());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        boolean isEqual = true;
        // confirm object instanceof this object
        if (obj instanceof OrderItemStatus)
        {

            OrderItemStatus c = (OrderItemStatus)obj; // downcast the input object

            // compare all the attributes of OrderItemStatus
            if ((isCrossChannelItem() == c.isCrossChannelItem())
                    && getExternalOrderType() == c.getExternalOrderType()
                    && Util.isObjectEqual(getStatus(), c.getStatus())
                    && Util.isObjectEqual(getQuantityOrdered(), c.getQuantityOrdered())
                    && Util.isObjectEqual(getQuantityPickedUp(), c.getQuantityPickedUp())
                    && Util.isObjectEqual(getQuantityNew(), c.getQuantityNew())
                    && Util.isObjectEqual(getQuantityPending(), c.getQuantityPending())
                    && Util.isObjectEqual(getQuantityPicked(), c.getQuantityPicked())
                    && Util.isObjectEqual(getQuantityShipped(), c.getQuantityShipped())
                    && Util.isObjectEqual(getQuantityCancelled(), c.getQuantityCancelled())
                    && Util.isObjectEqual(getQuantityReturned(), c.getQuantityReturned())
                    && Util.isObjectEqual(getQuantityPickup(), c.getQuantityPickup())
                    && Util.isObjectEqual(getQuantityPickupFrom(), c.getQuantityPickupFrom())
                    && Util.isObjectEqual(getQuantityCancel(), c.getQuantityCancel())
                    && Util.isObjectEqual(getQuantityCancelFrom(), c.getQuantityCancelFrom())
                    && Util.isObjectEqual(getOrderedAmount(), c.getOrderedAmount())
                    && Util.isObjectEqual(getCompletedAmount(), c.getCompletedAmount())
                    && Util.isObjectEqual(getCancelledAmount(), c.getCancelledAmount())
                    && Util.isObjectEqual(getReturnedAmount(), c.getReturnedAmount())
                    && Util.isObjectEqual(getDepositAmount(), c.getDepositAmount())
                    && Util.isObjectEqual(reference, c.getReference())
                    && Util.isObjectEqual(getItemDispositionCode(), c.getItemDispositionCode())
                    && Util.isObjectEqual(getPickupStoreID(), c.getPickupStoreID())
                    && Util.isObjectEqual(getPickupDate(), c.getPickupDate())
                    && Util.isObjectEqual(getPickupFirstName(), c.getPickupFirstName())
                    && Util.isObjectEqual(getPickupLastName(), c.getPickupLastName())
                    && Util.isObjectEqual(getPickupContact(), c.getPickupContact())
                    && (isShipToStoreForPickup() == c.isShipToStoreForPickup())
                    && Util.isObjectEqual(getDeliveryDetails(), c.getDeliveryDetails())
                    && Util.isObjectEqual(getFulfillmentOrderID(), c.getFulfillmentOrderID())
                    && Util.isObjectEqual(getDiscountStatusList(), c.getDiscountStatusList())
                    && Util.isObjectEqual(getTaxStatusList(), c.getTaxStatusList())
                    && Util.isObjectEqual(getOriginalTransactionId(), c.getOriginalTransactionId())
                    && Util.isObjectEqual(getOriginalBusinessDate(), c.getOriginalBusinessDate())
                    && (getOriginalLineNumber()==c.getOriginalLineNumber()))
            {
                isEqual = true; // set the return code to true
            }
            else
            {
                isEqual = false; // set the return code to false
            }
        }
        else
        {
            isEqual = false;
        }
        return (isEqual);
    }

    /**
     * @return the boolean flag indicating if the order item is a cross channel
     *         item
     */
    public boolean isCrossChannelItem()
    {
        return isCrossChannelItem;
    }

    /**
     * Set the boolean flag indicating if the order item is a cross channel item
     * 
     * @param isCrossChannelItem
     */
    public void setCrossChannelItem(boolean isCrossChannelItem)
    {
        this.isCrossChannelItem = isCrossChannelItem;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc#getExternalOrderType()
     */
    @Override
    public int getExternalOrderType()
    {
        return externalOrderType;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc#setExternalOrderType(int)
     */
    @Override
    public void setExternalOrderType(int externalOrderType)
    {
        this.externalOrderType = externalOrderType;
    }

    /**
     * Retrieves status of ordered item.
     * 
     * @return status of ordered item
     */
    public EYSStatusIfc getStatus()
    {
        return (status);
    }

    /**
     * Initializes order status object.
     * <P>
     */
    public void initializeStatus()
    {
        if (getStatus() == null)
        {
            status = DomainGateway.getFactory().getEYSStatusInstance();
        }
        status.setDescriptors(OrderConstantsIfc.ORDER_ITEM_STATUS_DESCRIPTORS);
    }

    /**
     * Sets status of ordered item.
     * 
     * @param value status of ordered item
     */
    public void setStatus(EYSStatusIfc value)
    {
        status = value;
    }
    
    /**
     * Clear all quantity and amount
     */
    public void clearQuantityAndAmount()
    {
        quantityOrdered = BigDecimal.ZERO;
        quantityPickedUp = BigDecimal.ZERO;
        quantityNew = BigDecimal.ZERO;
        quantityPending = BigDecimal.ZERO;
        quantityPicked = BigDecimal.ZERO;
        quantityPickup = BigDecimal.ZERO;
        quantityPickupFrom = new Hashtable<Integer, BigDecimal>(1);
        quantityShipped = BigDecimal.ZERO;
        quantityCancelled = BigDecimal.ZERO;
        quantityCancel = BigDecimal.ZERO;
        quantityCancelFrom = new Hashtable<Integer, BigDecimal>(1);
        quantityReturned = BigDecimal.ZERO;
        orderedAmount = DomainGateway.getBaseCurrencyInstance();
        completedAmount = DomainGateway.getBaseCurrencyInstance();
        cancelledAmount = DomainGateway.getBaseCurrencyInstance();
        returnedAmount = DomainGateway.getBaseCurrencyInstance();
        depositAmount = DomainGateway.getBaseCurrencyInstance();
    }

    /**
     * @return item quantity ordered
     */
    public BigDecimal getQuantityOrdered()
    {
        return quantityOrdered;
    }

    /**
     * Set item quantity ordered
     * 
     * @param quantityOrdered
     */
    public void setQuantityOrdered(BigDecimal quantityOrdered)
    {
        this.quantityOrdered = quantityOrdered;
    }

    /**
     * Retrieves quantity of ordered item picked up.
     * 
     * @return quantity of ordered item picked up
     */
    public BigDecimal getQuantityPickedUp()
    {
        return (quantityPickedUp);
    }

    /**
     * Sets quantity of ordered item picked up.
     * 
     * @param value quantity of ordered item picked up
     */
    public void setQuantityPickedUp(BigDecimal value)
    {
        quantityPickedUp = value;
    }

    /**
     * Retrieves quantity of ordered item new (not yet processed).
     * 
     * @return quantity of ordered item new
     */
    public BigDecimal getQuantityNew()
    {
        return quantityNew;
    }

    /**
     * Sets quantity of ordered item new (not yet processed).
     * 
     * @param value quantity of ordered item new
     */
    public void setQuantityNew(BigDecimal quantityNew)
    {
        this.quantityNew = quantityNew;
    }

    /**
     * Retrieves quantity of ordered item pending.
     * 
     * @return quantity of ordered item pending
     */
    public BigDecimal getQuantityPending()
    {
        return quantityPending;
    }

    /**
     * Sets quantity of ordered item pending.
     * 
     * @param value quantity of ordered item pending
     */
    public void setQuantityPending(BigDecimal quantityPending)
    {
        this.quantityPending = quantityPending;
    }

    /**
     * Retrieves quantity of ordered item picked.
     * 
     * @return quantity of ordered item picked
     */
    public BigDecimal getQuantityPicked()
    {
        return (quantityPicked);
    }

    /**
     * Sets quantity of ordered item picked.
     * 
     * @param value quantity of ordered item picked
     */
    public void setQuantityPicked(BigDecimal value)
    {
        quantityPicked = value;
    }

    /**
     * Retrieves quantity of ordered item shipped.
     * 
     * @return quantity of ordered item shipped
     */
    public BigDecimal getQuantityShipped()
    {
        return (quantityShipped);
    }

    /**
     * Sets quantity of ordered item shipped.
     * 
     * @param value quantity of ordered item shipped
     */
    public void setQuantityShipped(BigDecimal value)
    {
        quantityShipped = value;
    }

    /**
     * @return quantity cancelled
     */
    public BigDecimal getQuantityCancelled()
    {
        return quantityCancelled;
    }

    /**
     * Set quantity cancelled
     * 
     * @param quantityCancelled
     */
    public void setQuantityCancelled(BigDecimal quantityCancelled)
    {
        this.quantityCancelled = quantityCancelled;
    }

    /**
     * @return quantity returned
     */
    public BigDecimal getQuantityReturned()
    {
        return quantityReturned;
    }

    /**
     * Set the quantity returned
     * 
     * @param quantityReturned
     */
    public void setQuantityReturned(BigDecimal quantityReturned)
    {
        this.quantityReturned = quantityReturned;
    }

    /**
     * @return the quantity of ordered items to cancel
     */
    public BigDecimal getQuantityCancel()
    {
        return quantityCancel;
    }

    /**
     * Set the quantity of ordered items to cancel
     * 
     * @param quantityCancel
     */
    public void setQuantityCancel(BigDecimal quantityCancel)
    {
        this.quantityCancel = quantityCancel;
    }

    /**
     * @return a hashtable of quantity to cancel from for each order item
     *         status.
     */
    public Hashtable<Integer, BigDecimal> getQuantityCancelFrom()
    {
        return quantityCancelFrom;
    }

    /**
     * Set a hash table of quantity to cancel from for each order item status.
     * 
     * @param quantityCancelFrom
     */
    public void setQuantityCancelFrom(Hashtable<Integer, BigDecimal> quantityCancelFrom)
    {
        this.quantityCancelFrom = quantityCancelFrom;
    }

    /**
     * @return the quanityt of ordered items to pickup
     */
    public BigDecimal getQuantityPickup()
    {
        return quantityPickup;
    }

    /**
     * Set the quantity of ordered items to pickup
     * 
     * @param quantityPickup
     */
    public void setQuantityPickup(BigDecimal quantityPickup)
    {
        this.quantityPickup = quantityPickup;
    }

    /**
     * @return a hash table of quantity to pick up from for each order item
     *         status.
     */
    public Hashtable<Integer, BigDecimal> getQuantityPickupFrom()
    {
        return quantityPickupFrom;
    }

    /**
     * Set a hash table of quantity to pickup from for each order item status.
     * 
     * @param quantityPickupFrom
     */
    public void setQuantityPickupFrom(Hashtable<Integer, BigDecimal> quantityPickupFrom)
    {
        this.quantityPickupFrom = quantityPickupFrom;
    }

    /**
     * @return total ordered amount
     */
    public CurrencyIfc getOrderedAmount()
    {
        return orderedAmount;
    }

    /**
     * Set total ordered amount
     *
     * @param orderedAmount
     */
    public void setOrderedAmount(CurrencyIfc orderedAmount)
    {
        this.orderedAmount = orderedAmount;
    }

    /**
     * @return completed amount
     */
    public CurrencyIfc getCompletedAmount()
    {
        return completedAmount;
    }

    /**
     * Set completed amount
     *
     * @param completedAmount
     */
    public void setCompletedAmount(CurrencyIfc completedAmount)
    {
        this.completedAmount = completedAmount;
    }

    /**
     * @return cancelled amount
     */
    public CurrencyIfc getCancelledAmount()
    {
        return cancelledAmount;
    }

    /**
     * Set cancelled amount
     *
     * @param cancelledAmount
     */
    public void setCancelledAmount(CurrencyIfc cancelledAmount)
    {
        this.cancelledAmount = cancelledAmount;
    }

    /**
     * @return the returned amount
     */
    public CurrencyIfc getReturnedAmount()
    {
        return returnedAmount;
    }

    /**
     * Set returned amount
     *
     * @param returnedAmount the returned amount
     */
    public void setReturnedAmount(CurrencyIfc returnedAmount)
    {
        this.returnedAmount = returnedAmount;
    }

    /**
     * Retrieves deposit amount.
     * 
     * @return deposit amount
     */
    public CurrencyIfc getDepositAmount()
    {
        return (depositAmount);
    }

    /**
     * Sets deposit amount.
     * 
     * @param value deposit amount
     */
    public void setDepositAmount(CurrencyIfc value)
    {
        depositAmount = value;
    }

    /**
     * Sets the reference string.
     * 
     * @param value reference as String
     */
    public void setReference(String value)
    {
        reference = value;
    }

    /**
     * Gets the reference string.
     * 
     * @return reference as String
     */
    public String getReference()
    {
        return (reference);
    }

    /**
     * Gets the ItemDispositionCode
     *
     * @param value as ItemDispositionCode
     */
    public int getItemDispositionCode()
    {
        return itemDispositionCode;
    }

    /**
     * Sets the ItemDispositionCode.
     *
     * @return The value of ItemDispositionCode.
     */
    public void setItemDispositionCode(int itemDispositionCode)
    {
        this.itemDispositionCode = itemDispositionCode;
    }

    /**
     * @return the store id where the item is to be picked up.
     */
    public String getPickupStoreID()
    {
        return pickupStoreID;
    }

    /**
     * The store id where the item is to be picked up
     * 
     * @param pickupStoreID
     */
    public void setPickupStoreID(String pickupStoreID)
    {
        this.pickupStoreID = pickupStoreID;
    }

    /**
     * Retrieves pickup date.
     * 
     * @return pickup date
     */
    public EYSDate getPickupDate()
    {
        return pickupDate;
    }

    /**
     * Sets pickup date.
     * 
     * @param value pickup date
     */
    public void setPickupDate(EYSDate pickupDate)
    {
        this.pickupDate = pickupDate;
    }

    /**
     * @return the pickup person's first name
     */
    public String getPickupFirstName()
    {
        return pickupFirstName;
    }

    /**
     * Set the pickup person's first name
     * 
     * @param pickupFirstName
     */
    public void setPickupFirstName(String pickupFirstName)
    {
        this.pickupFirstName = pickupFirstName;
    }

    /**
     * @return the pickup person's last name
     */
    public String getPickupLastName()
    {
        return pickupLastName;
    }

    /**
     * Set the pickup person's last name
     * 
     * @param pickupLastName
     */
    public void setPickupLastName(String pickupLastName)
    {
        this.pickupLastName = pickupLastName;
    }

    /**
     * @return the pickup person's phone #
     */
    public PhoneIfc getPickupContact()
    {
        return pickupContact;
    }

    /**
     * Set the pickup person's phone #
     * 
     * @param pickupContact
     */
    public void setPickupContact(PhoneIfc pickupContact)
    {
        this.pickupContact = pickupContact;
    }

    /**
     * @return the boolean flag indicating if the item needs to be shipped to
     *         the store for pickup.
     */
    public boolean isShipToStoreForPickup()
    {
        return isShipToStoreForPickup;
    }

    /**
     * Set the boolean flag indicating if the item needs to be shipped to the
     * store for pickup.
     * 
     * @param isShipToStoreForPickup
     */
    public void setShipToStoreForPickup(boolean isShipToStoreForPickup)
    {
        this.isShipToStoreForPickup = isShipToStoreForPickup;
    }
    
    /**
     * Get original transaction id
     * @return
     */
    public TransactionIDIfc getOriginalTransactionId() {
        return originalTransactionId;
    }

    /**
     * Set original originalTransactionId id
     * @param originalTransactionId
     */
    public void setOriginalTransactionId(TransactionIDIfc originalTransactionId) {
        this.originalTransactionId = originalTransactionId;
    }

    /**
     * Get original business date
     * @return
     */
    public EYSDate getOriginalBusinessDate() {
        return originalBusinessDate;
    }

    /**
     * Set original business date
     * @param originalBusinessDate
     */
    public void setOriginalBusinessDate(EYSDate originalBusinessDate) {
        this.originalBusinessDate = originalBusinessDate;
    }
    
    /**
     * Get original line item number
     * @return
     */
    public int getOriginalLineNumber() {
        return originalLineNumber;
    }

    /**
     * Set original line item number
     * @param originalLineNumber
     */
    public void setOriginalLineNumber(int originalLineNumber) {
        this.originalLineNumber = originalLineNumber;
    }    
    
    /**
     * Returns default display string.
     * 
     * @return String representation of object
     */
    public String toString()
    {
      // build result string
        StringBuilder strResult = Util.classToStringHeader("OrderItemStatus", getRevisionNumber(), hashCode());
        // add attributes to string
        strResult.append(Util.formatToStringEntry("status", getStatus())).append(
                Util.formatToStringEntry("quantityPickedUp", getQuantityPickedUp())).append(
                Util.formatToStringEntry("quantityNew", getQuantityNew())).append(
                Util.formatToStringEntry("quantityPending", getQuantityPending())).append(
                Util.formatToStringEntry("quantityPicked", getQuantityPicked())).append(
                Util.formatToStringEntry("quantityShipped", getQuantityShipped())).append(
                Util.formatToStringEntry("completedAmount", getCompletedAmount())).append(
                Util.formatToStringEntry("cancelledAmount", getCancelledAmount())).append(
                Util.formatToStringEntry("returnedAmount", getReturnedAmount())).append(
                Util.formatToStringEntry("depositAmount", getDepositAmount())).append(
                Util.formatToStringEntry("itemDispositionCode", getItemDispositionCode())).append(
                Util.formatToStringEntry("pickupDate", getPickupDate())).append(
                Util.formatToStringEntry("orderDeliveryDetail", getDeliveryDetails())).append(
                Util.formatToStringEntry("discountStatusList", getDiscountStatusList())).append(
                Util.formatToStringEntry("taxStatusList", getTaxStatusList()));

        
        if (originalTransactionId!=null)
        {
            strResult.append(Util.formatToStringEntry("originalStoreId", getOriginalTransactionId()));
        }
        
        if (originalBusinessDate!=null)
        {
            strResult.append(Util.formatToStringEntry("originalBusinessDate", getOriginalBusinessDate()));
        }
        
        if (originalLineNumber!=-1)
        {
            strResult.append(Util.formatToStringEntry("originalLineNumber", getOriginalLineNumber()));
        }
        // pass back result
        return (strResult.toString());
    }

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

    /**
     * Set order delivery detail collection.
     *
     * @param Object OrderDeliveryDetailIfc
     */
    public void setDeliveryDetails(OrderDeliveryDetailIfc deliveryDetails)
    {
        this.orderDeliveryDetail = deliveryDetails;

    }

    /**
     * Gets the delivery detail
     *
     * @return Object of delivery detail
     */
    public OrderDeliveryDetailIfc getDeliveryDetails()
    {
        return orderDeliveryDetail;
    }

    /**
     * @return the fulfillment order id
     */
    public String getFulfillmentOrderID()
    {
        return fulfillmentOrderID;
    }

    /**
     * Set the fulfillment order id
     * 
     * @param fulfillmentOrderID
     */
    public void setFulfillmentOrderID(String fulfillmentOrderID)
    {
        this.fulfillmentOrderID = fulfillmentOrderID;
    }

    /**
     * Retrieve discount status based on discount line number
     * 
     * @param discountLineNumber the discount line number
     * @return the discount status
     */
    public OrderItemDiscountStatusIfc getDiscountStatus(int discountLineNumber)
    {
        OrderItemDiscountStatusIfc result = null;
        if (getDiscountStatusList() != null)
        {
            for (OrderItemDiscountStatusIfc discountStatus : getDiscountStatusList())
            {
                if (discountStatus.getLineNumber() == discountLineNumber)
                {
                    result = discountStatus;
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Retrieve a list of order item discount status
     * 
     * @return the discount status list
     */
    public List<OrderItemDiscountStatusIfc> getDiscountStatusList()
    {
        return discountStatusList;
    }

    /**
     * Set a list order item discount status
     * 
     * @param discountStatusList the discount status list
     */
    public void setDiscountStatusList(List<OrderItemDiscountStatusIfc> discountStatusList)
    {
        this.discountStatusList = discountStatusList;
    }

    /**
     * Add an order item discount status
     * 
     * @param discountStatus a discount status
     */
    public void addDiscountStatus(OrderItemDiscountStatusIfc discountStatus)
    {
        List<OrderItemDiscountStatusIfc> discountStatusList = getDiscountStatusList();
        if (discountStatusList == null)
        {
            discountStatusList = new ArrayList<OrderItemDiscountStatusIfc>();
            setDiscountStatusList(discountStatusList);
        }
        discountStatusList.add(discountStatus);
    }
    
    /**
     * Clear order item discount status list
     */
    public void clearDiscountStatus()
    {
        List<OrderItemDiscountStatusIfc> discountStatusList = getDiscountStatusList();
        if (discountStatusList != null)
        {
            discountStatusList.clear();
        }
    }

    /**
     * Retrieve an order item tax status by its authority ID, tax group ID, and
     * type code.
     * 
     * @param authorityID the tax authority ID
     * @param taxGroupID the tax group ID
     * @param typeCode the tax type code
     * @return the order item tax status
     */
    public OrderItemTaxStatusIfc getTaxStatus(int authorityID, int taxGroupID, int typeCode)
    {
        OrderItemTaxStatusIfc result = null;
        if (getTaxStatusList() != null)
        {
            for (OrderItemTaxStatusIfc taxStatus : getTaxStatusList())
            {
                if ((taxStatus.getAuthorityID() == authorityID) && (taxStatus.getTaxGroupID() == taxGroupID)
                        && (taxStatus.getTypeCode() == typeCode))
                {
                    result = taxStatus;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Retrieve a list of order item tax status
     * 
     * @return the tax status list
     */
    public List<OrderItemTaxStatusIfc> getTaxStatusList()
    {
        return taxStatusList;
    }

    /**
     * Set a list order item tax status
     * 
     * @param taxStatusList the tax status list
     */
    public void setTaxStatusList(List<OrderItemTaxStatusIfc> taxStatusList)
    {
        this.taxStatusList = taxStatusList;
    }

    /**
     * Add an order item tax status
     * 
     * @param taxStatus a tax status
     */
    public void addTaxStatus(OrderItemTaxStatusIfc taxStatus)
    {
        List<OrderItemTaxStatusIfc> taxStatusList = getTaxStatusList();
        if (taxStatusList == null)
        {
            taxStatusList = new ArrayList<OrderItemTaxStatusIfc>();
            setTaxStatusList(taxStatusList);
        }
        taxStatusList.add(taxStatus);
    }
    
    /**
     * Clear order item tax status list
     */
    public void clearTaxStatus()
    {
        List<OrderItemTaxStatusIfc> taxStatusList = getTaxStatusList();
        if (taxStatusList != null)
        {
            taxStatusList.clear();
        }
    }

    /**
     * Calculate order item status based on quantities
     * 
     * @param orderItemStatus the order item status
     */
    public void setStatusByQuantity()
    {
        int status = OrderConstantsIfc.ORDER_ITEM_STATUS_PARTIAL;
        if (getQuantityCancelled().add(getQuantityCancel()).compareTo(getQuantityOrdered()) == 0)
        {
            status = OrderConstantsIfc.ORDER_ITEM_STATUS_CANCELED;
        }
        else if (getQuantityNew().add(getQuantityCancelled()).add(getQuantityCancel()).compareTo(getQuantityOrdered()) == 0)
        {
            status = OrderConstantsIfc.ORDER_ITEM_STATUS_NEW;
        }
        else if (getQuantityPending().add(getQuantityCancelled()).add(getQuantityCancel())
                .compareTo(getQuantityOrdered()) == 0)
        {
            status = OrderConstantsIfc.ORDER_ITEM_STATUS_PENDING;
        }
        else if (getQuantityPicked().add(getQuantityCancelled()).add(getQuantityCancel())
                .compareTo(getQuantityOrdered()) == 0)
        {
            status = OrderConstantsIfc.ORDER_ITEM_STATUS_FILLED;
        }
        else if (getQuantityPickedUp().add(getQuantityPickup()).add(getQuantityCancelled()).add(getQuantityCancel())
                .compareTo(getQuantityOrdered()) == 0)
        {
            status = OrderConstantsIfc.ORDER_ITEM_STATUS_PICKED_UP;
        }
        else if (getQuantityShipped().add(getQuantityCancelled()).add(getQuantityCancel())
                .compareTo(getQuantityOrdered()) == 0)
        {
            status = OrderConstantsIfc.ORDER_ITEM_STATUS_SHIPPED;
        }

        EYSStatusIfc eysStatus = new EYSStatus();
        eysStatus.setStatus(status);
        eysStatus.setLastStatusChange();
        eysStatus.setDescriptors(OrderConstantsIfc.ORDER_ITEM_STATUS_DESCRIPTORS);

        setStatus(eysStatus);
    }

}
