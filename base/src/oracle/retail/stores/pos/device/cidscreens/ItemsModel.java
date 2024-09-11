/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/cidscreens/ItemsModel.java /main/11 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *4    360Commerce 1.3         4/25/2007 8:52:41 AM   Anda D. Cadar   I18N
 *     merge
 *     
 *3    360Commerce 1.2         3/31/2005 4:28:34 PM   Robert Pearse   
 *2    360Commerce 1.1         3/10/2005 10:22:32 AM  Robert Pearse   
 *1    360Commerce 1.0         2/11/2005 12:11:42 PM  Robert Pearse   
 *
 Revision 1.7  2004/07/30 21:40:10  rzurga
 @scr 6546 Cannot invoke the Price Adjusted Items screen on CPOI device
 Added price adjustment items as return/purchase items with appropriate messages to CPOI
 Also changed the discount, taxes and total values to conform to locale settings
 *
 Revision 1.6  2004/07/29 22:49:49  rzurga
 @scr 6604 CPOI displays discount from previous transaction on blank Sale items screen
 Reset to show zero on start of a new transaction
 *
 Revision 1.5  2004/07/24 18:16:25  rzurga
 @scr 6466 CPOI displays "Units Sold" number from previous transaction
 Modified the way the Units Sold is set and added a reset at the end of transaction
 *
 Revision 1.4  2004/07/01 22:22:17  rzurga
 @scr 5107 Customer Point of Interaction- Elements missing from CPOI screen
 *
 Units sold and discount added to the bottom of the CPOI screen along with the taxes and total.
 *
 Revision 1.3  2004/03/25 20:25:15  jdeleau
 @scr 4090 Deleted items appearing on Ingenico, I18N, perf improvements.
 See the scr for more info.
 *
 Revision 1.2  2004/02/12 16:48:35  mcs
 Forcing head revision
 *
 Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Sep 03 2003 13:11:58   RSachdeva
 * Initial revision.
 * Resolution for POS SCR-3355: Add CIDScreen support
 * ===========================================================================
 */
package oracle.retail.stores.pos.device.cidscreens;

import java.util.Collection;
import java.util.Iterator;
import java.util.HashSet;
import java.util.ArrayList;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.ado.context.ADOContextIfc;
import oracle.retail.stores.pos.ado.context.ContextFactory;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.pos.device.POSDeviceActionGroup;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.domain.lineitem.PriceAdjustmentLineItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.TourContext;

/**
 * This is the data model for the Ingenico LineItems Device screen
 *
 * $Revision: /main/11 $
 */
public class ItemsModel extends POSDeviceActionGroup
{
    /**
     * revision number supplied by CVS
     */
    public static final String revisionNumber = "$Revision: /main/11 $";
    
    /**
     * List containing Strings of items to display
     * on the Ingenico device.  
     */
    private ArrayList items = new ArrayList();
    /**
     * int representation of tax
     */
    private String unitsSold /*= ""*/;
    /**
     * floating point representation of tax
     */
    private CurrencyIfc discountAmount;
    /**
     * floating point representation of tax
     */
    private CurrencyIfc taxCur;
    /**
     * floating point representation of totals
     */
    private CurrencyIfc totalCur;
    /**
     * floating point representation of subTotals
     */
    private CurrencyIfc subTotalCur;
    /**
     * floating point representation of amountTendered
     */
    private CurrencyIfc amountTenderedCur;
    /**
     * tax as string 
     * @deprecated 7.0 use CurrencyIfc attribute
     */
    private String tax;
    /**
     * total as string
     * @deprecated 7.0 use CurrencyIfc attribute
     */
    private String total;
    /**
     * subtotal as string
     * @deprecated 7.0 use CurrencyIfc attribute
     */
    private String subTotal;
    /**
     * amount tendered as string
     * @deprecated 7.0 use CurrencyIfc attribute
     */
    private String amountTendered;
    
    /**
     * Message to appear at the bottom of the device screen
     */
    private String message = "";

    /**
     * All listeners for model changes
     */
    private HashSet listeners = new HashSet();

    /**
     * Add a line item to the model
     *  
     *  @param lineItem
     */
    public void addItem(LineItemDisplayIfc lineItem)
    {
        items.add(lineItem);
        
        LineItemDataChangedEvent event = new LineItemDataChangedEvent(LineItemDataChangedEvent.ITEMS_ADDED);
        event.setRowStart(new Integer(items.size()-1));
        event.setRowEnd(new Integer(items.size() - 1));
        fireDataModelChanged(event);
    }
    
    /**
     * Add an item
     *  
     *  @param s string to add
     *  @deprecated 7.0 use addItem(LineItemDisplayIfc)
     */
    public void addItem(String s)
    {
        items.add(s);
        
        LineItemDataChangedEvent event = new LineItemDataChangedEvent(LineItemDataChangedEvent.ITEMS_ADDED);
        event.setRowStart(new Integer(items.size()-1));
        event.setRowEnd(new Integer(items.size() - 1));
        fireDataModelChanged(event);
    }

    /**
     * Get the line item at the given row
     *  
     *  @param i Row to retrieve line item from
     *  @return LineItem
     *  @deprecated 7.0, use getItemAt(int)
     */
    public String getItem(int i)
    {
        return items.get(i).toString();
    }
    
    /**
     * Get the line item at the given row.  If its
     * out of bounds, a value of null is returned
     *  
     *  @param i Row to retrieve line item from
     *  @return LineItem
     */
    public Object getItemAt(int i)
    {
        if(i > (items.size()-1))
            return null;
        
        return items.get(i);
    }
    
    /**
     * Remove an item from the given position in the list
     *  
     *  @param i Position in the list to remove an item from
     */
    public void removeItem(int i)
    {
        LineItemDataChangedEvent event = new LineItemDataChangedEvent(LineItemDataChangedEvent.ITEMS_REMOVED);
        event.setRowStart(new Integer(i));
        event.setRowEnd(new Integer(items.size()));
        
        items.remove(i);
        fireDataModelChanged(event);
    }
    
    /**
     * Remove an item from the list
     *  
     *  @param lineItem The lineitem to remove
     */
    public void removeItem(Object lineItem)
    {
        if(lineItem instanceof Integer)
        {
            removeItem(((Integer)lineItem).intValue());
        }
        else
        {
            for(int i=0; i<items.size(); i++)
            {
                Object lid = items.get(i);
                if(lid.equals(lineItem))
                {
                    removeItem(i);
                    break;
                }
            }
        }
    }
    
    /**
     * Remove a collection of LineItemDisplayIfc objects.
     *  
     *  @param lineItems
     */
    public void removeItems(Collection lineItems)
    {
        LineItemDataChangedEvent event = new LineItemDataChangedEvent(LineItemDataChangedEvent.ITEMS_REMOVED);
        event.setRowEnd(new Integer(items.size()));
        
        Iterator iter = lineItems.iterator();
        int minIndex = items.size();
        ArrayList objsToRemove = new ArrayList();
        while(iter.hasNext())
        {
            Object objToRemove = iter.next();
            for(int i=0; i<items.size(); i++)
            {
                if(items.get(i).equals(objToRemove))
                {
                    minIndex = Math.min(minIndex, i);
                    objsToRemove.add(items.get(i));
                }
                    
            }
        }
        items.removeAll(objsToRemove);
        
        event.setRowStart(new Integer(minIndex));
        fireDataModelChanged(event);
    }

    /**
     * Refresh the list of items with this new list
     *  
     *  @param lineItems New list of line items
     */
    public void refreshItems(Collection lineItems)
    {
        items.clear();
        Iterator iter =lineItems.iterator();
        while(iter.hasNext())
        {
            Object obj = iter.next();
            if(obj instanceof PriceAdjustmentLineItemIfc)
            {
                BusIfc bus = TourContext.getInstance().getTourBus();
                UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
                items.add(new LineItemDisplay((SaleReturnLineItemIfc)((PriceAdjustmentLineItemIfc)obj).getPriceAdjustReturnItem()));
                items.add(new LineItemDisplay("  " + utility.retrieveCommonText("PrcAdjPrchPrc", "Price Adjust - Purchase Price")));
                items.add(new LineItemDisplay((SaleReturnLineItemIfc)((PriceAdjustmentLineItemIfc)obj).getPriceAdjustSaleItem()));
                items.add(new LineItemDisplay("  " + utility.retrieveCommonText("PrcAdjCurrPrc","Price Adjust - Current Price")));
            }
            else if(obj instanceof SaleReturnLineItemIfc)
                items.add(new LineItemDisplay((SaleReturnLineItemIfc) obj));
            else if(obj instanceof PLUItemIfc)
                items.add(new LineItemDisplay((PLUItemIfc) obj));
            else if(obj instanceof LineItemDisplayIfc)
                items.add(obj);
            // This is deprecated, remove it when deprecated code goes away
            else if(obj instanceof String)
                items.add(obj);
        }
        fireDataModelChanged(LineItemDataChangedEvent.ALL);
    }

    /**
     * Update an item at position i In the list
     *  
     *  @param i Item to update
     *  @param lineItem New item that takes its place
     */
    public void updateItem(int i, LineItemDisplayIfc lineItem)
    {
        items.set(i, lineItem);
        LineItemDataChangedEvent event = new LineItemDataChangedEvent(LineItemDataChangedEvent.EXISTING_ITEM);
        event.setRowStart(new Integer(i));
        event.setRowEnd(new Integer(i));
        fireDataModelChanged(event);
    }
    
    /**
     * Update an item at position i In the list
     *  
     *  @param i Item to update
     *  @param lineItem that takes its place
     *  @deprecated 7.0 use updateItem(int i, LineItemDisplayIfc lineItem)
     */
    public void updateItem(int i, String lineItem)
    {
        items.set(i, lineItem);
    }

    /**
     * Remove all line items from the model
     */
    public void resetItems()
    {
        items.clear();
        fireDataModelChanged(LineItemDataChangedEvent.ALL);
    }

    /**
     * Get the number of line items to display
     *  
     *  @return Number of Items
     */
    public int getNumItems()
    {
        return items.size();
    }

    /**
     * Set the tax value 
     *  
     *  @param tax The tax amount
     *  @deprecated 7.0 use setTotal(CurrencyIfc amountTendered)
     */
    public void setTax(String tax)
    {
        this.tax = tax;
        setTax(DomainGateway.getBaseCurrencyInstance(tax));
    }
    
    /**
     * Set the tax value 
     *  
     *  @param tax The tax amount
     */
    public void setTax(CurrencyIfc tax)
    {
        this.taxCur = tax;
        fireDataModelChanged(LineItemDataChangedEvent.TAX);
    }
    
    /**
     * Set the units sold value 
     *  
     *  @param unitsSold The units sold
     */
    public void setUnitsSold(String unitsSold)
    {
        this.unitsSold = unitsSold;
        fireDataModelChanged(LineItemDataChangedEvent.UNITS_SOLD);
    }
    
    /**
     * Set the discount value 
     *  
     *  @param discount The tax amount
     */
    public void setDiscount(CurrencyIfc discount)
    {
        this.discountAmount = discount;
        fireDataModelChanged(LineItemDataChangedEvent.DISCOUNT);
    }

    /**
     * Get the units sold count
     *  
     *  @return discount amount
     */
    public String getUnitsSold()
    {
        return unitsSold;
    }

    /**
     * Get the discount amount value
     *  
     *  @return discount amount
     */
    public CurrencyIfc getDiscountAmount()
    {
        return discountAmount;
    }

    /**
     * Get the tax value
     *  
     *  @return tax
     */
    public CurrencyIfc getTaxAmount()
    {
        return taxCur;
    }
    
    /**
     * Get the tax value
     *  
     *  @return tax
     *  @deprecated 7.0 use getTaxAmount()
     */
    public String getTax()
    {
        return tax;
    }

    /**
     * Set the total
     *  
     *  @param total
     *  @deprecated 7.0 use setTotal(CurrencyIfc amountTendered)
     */
    public void setTotal(String total)
    {
        this.total = total;
        setTotal(DomainGateway.getBaseCurrencyInstance(total));
    }
    
    /**
     * Set the total
     *  
     *  @param total
     */
    public void setTotal(CurrencyIfc total)
    {
        this.totalCur = total;
        fireDataModelChanged(LineItemDataChangedEvent.TOTAL);
    }

    /**
     * Return the total
     *  
     *  @return total
     */
    public CurrencyIfc getTotalAmount()
    {
        return this.totalCur;
    }

    /**
     * Get the total value
     *  
     *  @return total
     *  @deprecated 7.0 use getTotalAmount()
     */
    public String getTotal()
    {
        return total.toString();
    }
    
    /**
     * Set the subTotal
     *  
     *  @param subTotal 
     *  @deprecated 7.0 use setAmountTendered(CurrencyIfc amountTendered)
     */
    public void setSubTotal(String subTotal)
    {
        this.subTotal = subTotal;
        setSubTotal(DomainGateway.getBaseCurrencyInstance(subTotal));
    }
    
    /**
     * Set the subTotal
     *  
     *  @param subTotal 
     */
    public void setSubTotal(CurrencyIfc subTotal)
    {
        this.subTotalCur = subTotal;
        fireDataModelChanged(LineItemDataChangedEvent.SUBTOTAL);
    }

    /**
     * Get the subTotal
     *  
     *  @return subTotal
     */
    public CurrencyIfc getSubTotalAmount()
    {
        return subTotalCur;
    }

    /**
     * Get the subTotal value
     *  
     *  @return subTotal
     *  @deprecated 7.0 use getSubTotalAmount()
     */
    public String getSubTotal()
    {
        return subTotal;
    }
    
    /**
     * Set the total amount tendered
     *  
     *  @param amountTendered
     *  @deprecated 7.0 use setAmountTendered(CurrencyIfc amountTendered)
     */
    public void setAmountTendered(String amountTendered)
    {
        this.amountTendered = amountTendered;
        setAmountTendered(DomainGateway.getBaseCurrencyInstance(amountTendered));
    }
    
    /**
     * Set the total amount tendered
     *  
     *  @param amountTendered
     */
    public void setAmountTendered(CurrencyIfc amountTendered)
    {
        this.amountTenderedCur = amountTendered;
        fireDataModelChanged(LineItemDataChangedEvent.AMOUNT_TENDERED);
    }

    /**
     * Get the total amount tendered
     *  
     *  @return amountTendered
     */
    public CurrencyIfc getAmountTenderedAmount()
    {
        return amountTenderedCur;
    }
    
    /**
     * Get the amountTendered value
     *  
     *  @return amountTendered
     *  @deprecated 7.0 use getAmountTenderedAmount()
     */
    public String getAmountTendered()
    {
        return amountTendered;
    }

    /**
     * Clear the line items, and all the numeric
     * totals
     *
     */
    public void reset()
    {
        items.clear();
        if(getTaxAmount() != null)
            getTaxAmount().setZero();
        //setTax("0.00");
        if(getTotalAmount() != null)
            getTotalAmount().setZero();
        //setTotal("0.00");
        if(getSubTotalAmount() != null)
            getSubTotalAmount().setZero();
        //setSubTotal("0.00");
        if(getAmountTenderedAmount() != null)
            getAmountTenderedAmount().setZero();
        //setAmountTendered("0.00");
        if(getDiscountAmount() != null)
            getDiscountAmount().setZero();
        //setDiscountAmount("0.00");
        if (getUnitsSold() != null)
        unitsSold = "0";
        //setUnitsSold("0");
        fireDataModelChanged(LineItemDataChangedEvent.ALL);
    }
    
    /**
     * Set the message that will appear on the bottom of
     * the Igenico device
     *  
     *  @param aMessage message to set the bottom line to
     */
    public void setMessage(String aMessage)
    {
        message = aMessage;
        fireDataModelChanged(LineItemDataChangedEvent.MESSAGE);
    }
    
    /**
     * Retrieve the message that will appear on 
     * the bottom of the ingenico device
     *  
     *  @return message
     */
    public String getMessage()
    {
        return message;
    }
    
    /**
     * Notify all listeners that the data model has
     * changed.  
     * 
     * @param changedDataType The type of change, a constant
     * defined in LineItemDataModelChangedEvent.  
     */
    protected void fireDataModelChanged(int changedDataType)
    {
        LineItemDataChangedEvent event = new LineItemDataChangedEvent(changedDataType);
        fireDataModelChanged(event);
    }
    
    /**
     * Notify all listeners that the data model has
     * changed.  
     * 
     * @param event The event to fire
     */
    protected void fireDataModelChanged(LineItemDataChangedEvent event)
    {
        event.setDataModel(this);
        Iterator iterator = listeners.iterator();
        while(iterator.hasNext())
        {
            ((LineItemDataChangedListener) iterator.next()).dataModelChanged(event);
        }
        
    }
 
    /**
     * Add a listener to the list of listeners wanting to be
     * notified when the data model changed.
     *  
     *  @param listener
     */
    public void addDataModelChangedListener(LineItemDataChangedListener listener)
    {
        listeners.add(listener);
    }
    
    /**
     * Remove the specified listener from the list of listeners
     *  
     *  @param listener
     */
    public void removeDataModelChangedListener(LineItemDataChangedListener listener)
    {
        listeners.remove(listener);
    }
}
