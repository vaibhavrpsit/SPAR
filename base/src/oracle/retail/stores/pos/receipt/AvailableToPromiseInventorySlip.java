/* ===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/receipt/AvailableToPromiseInventorySlip.java /main/3 2013/04/05 17:58:59 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    jswan     05/01/12 - Added to support the cross channel feature create
 *                         pickup order.
 *    ohorne    04/26/12 - initial version
 *
 * ==============================================================================
 */
package oracle.retail.stores.pos.receipt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.inventoryinquiry.promise.AvailableToPromiseInventoryIfc;
import oracle.retail.stores.domain.inventoryinquiry.promise.StoreItemAvailableToPromiseInventoryIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


/**
 * This class represents the Available to Promise inventory slip. The slip only
 * prints stores having availability today. 
 */
public class AvailableToPromiseInventorySlip extends PrintableDocumentParameterBean
{
    private static final long serialVersionUID = -4685389501727355816L;

    /**
     * logger instance to send log messages to
     */
    private static final Logger logger = Logger.getLogger(AvailableToPromiseInventorySlip.class);

    /** Register of the report being run. */
    protected RegisterIfc register;
    /** The cashier running the report */
    protected EmployeeIfc cashier;
    /** The item on which the report is being run. */
    protected SaleReturnLineItemIfc item;
    /** The Available to Promise Inventories for each store being reported. */
    protected StoreItemAvailableToPromiseInventoryIfc[] inventories;

    /** Comparator for sorting StoreItemAvailabiltyIfc by Store name*/
    protected static Comparator<StoreItemAvailableToPromiseInventoryIfc> COMPARATOR = new Comparator<StoreItemAvailableToPromiseInventoryIfc>()
    {
        Locale receiptLocale = LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT);
        public int compare(StoreItemAvailableToPromiseInventoryIfc inv1, StoreItemAvailableToPromiseInventoryIfc inv2)
        {
            String store1 = StringUtils.trimToEmpty(inv1.getStore().getLocationName(receiptLocale));
            String store2 = StringUtils.trimToEmpty(inv2.getStore().getLocationName(receiptLocale));
            return store1.compareToIgnoreCase(store2);
        }
    };

    /** Todays date*/
    private EYSDate today;

    /**
     * Constructor
     */
    public AvailableToPromiseInventorySlip()
    {
        this(null, null, null, null);
        today = new EYSDate();
    }

    /**
     * Constructor InventoryInquirySlip.
     *
     * @param register the register
     * @param cashier the cashier
     * @param item the sale return line item
     * @param list an array of StoreItemAvailableToPromiseInventory
     */
    public AvailableToPromiseInventorySlip(RegisterIfc register, 
                                         EmployeeIfc cashier,
                                         SaleReturnLineItemIfc item,
                                         StoreItemAvailableToPromiseInventoryIfc[] list)
    {
        this.register = register;
        this.cashier = cashier;
        this.item = item;
        this.inventories = list;
        setDocumentType(ReceiptTypeConstantsIfc.AVAILABLE_TO_PROMISE_INVENTORY);
    }

    /**
     * @return the register
     */
    public RegisterIfc getRegister()
    {
        return register;
    }

    /**
     * @return the cashier
     */
    public EmployeeIfc getCashier()
    {
        return cashier;
    }

    /**
     * @return the item
     */
    public SaleReturnLineItemIfc getItem()
    {
        return item;
    }

    /**
     * @return the list of Available to Promise Inventories for each store being reported.
     */
    public StoreItemAvailableToPromiseInventoryIfc[] getStoreItemAvailableToPromiseInventoryList()
    {
        return inventories;
    }

    /**
     * @param register the register to set
     */
    public void setRegister(RegisterIfc register)
    {
        this.register = register;
    }

    /**
     * @param cashier the cashier to set
     */
    public void setCashier(EmployeeIfc cashier)
    {
        this.cashier = cashier;
    }

    /**
     * @param item the item to set
     */
    public void setItem(SaleReturnLineItemIfc item)
    {
        this.item = item;
    }

    /**
     * Set the list of Available to Promise Inventories for each store being reported.
     * This slip only lists Stores where an Item can be promised to have availability today. 
     * Inventory available on any date other than today will be ignored.
     * @param list the list of Available to Promise Inventories for each store being reported.
     */
    public void setStoreItemAvailableToPromiseInventoryList(StoreItemAvailableToPromiseInventoryIfc[] list)
    {
        List<StoreItemAvailableToPromiseInventoryIfc> availableToday = new ArrayList<StoreItemAvailableToPromiseInventoryIfc>(list.length); 
        for (int i = 0; i < list.length; i++)
        {
            StoreItemAvailableToPromiseInventoryIfc inventory = list[i];
            if (isToday(inventory.getAvailableToPromiseInventory()))
            {
                availableToday.add(inventory);
            }
            else
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Ignored: availability date is not today. "
                            + inventory.getAvailableToPromiseInventory().getStoreID() 
                            + "(" 
                            +  inventory.getAvailableToPromiseInventory().getDate()
                            + ")"); 
                }
            }
        }

        //sort the list
        Collections.sort(availableToday, COMPARATOR);

        this.inventories = availableToday.toArray(new StoreItemAvailableToPromiseInventoryIfc[availableToday.size()]);
    }

    /**
     * Utility method to determine if an EYSDate is the same
     * as the System Month, Day and Year.
     * @param eysDate the date to test.  Must not be null.
     * @return true if supplied date occurs today 
     */
    public boolean isToday(AvailableToPromiseInventoryIfc availableToPromiseInventory)
    {
        if (availableToPromiseInventory == null)
        {
            return false;
        }
        EYSDate date = availableToPromiseInventory.getDate();
        if (date == null)
        {
            return false;
        }
        return (today.getDay() == date.getDay() &&
                today.getMonth()== date.getMonth() &&
                today.getYear() == date.getYear());
    }
}