package oracle.retail.stores.pos.services.sale.validate;

/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/validate/TenderCompletedRoad.java /main/5 2014/07/17 15:09:41 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 *
 * ===========================================================================
 * $Log$
 * ===========================================================================
 */

// foundation imports


import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.StringUtils;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderItemDiscountStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderItemTaxStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderStatusIfc;
import oracle.retail.stores.domain.tax.TaxInformationIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.sale.SaleCargo;

// ------------------------------------------------------------------------------
/**
 * This class take with item status when doing order pickup or order cancel
 * <P>
 *
 */
// ------------------------------------------------------------------------------
@SuppressWarnings("serial")
public class TenderCompletedRoad extends PosLaneActionAdapter
{ // begin class TenderCompletedRoad
    /**
     * lane name constant
     */
    public static final String LANENAME = "TenderCompletedRoad";

    /**
     * revision number supplied by source-code-control system
     */
    public static String revisionNumber = "$Revision: /main/5 $";

    // --------------------------------------------------------------------------
    /**
     * Performs the traversal functionality for the aisle. In this case,  
     * original transaction id will be set for take with item;
     * initial transaction id will be set for pickup or cancel order item;
     * store order status will be set for take with item;
     * order item status will be set for take with item;
     * etc.
     * <P>
     *
     * @param bus the bus traversing this lane
     */
    // --------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    { // begin traverse()
        SaleCargo saleCargo = (SaleCargo)bus.getCargo();
  
        // Gets order transaction and its components to update
        SaleReturnTransactionIfc transaction = saleCargo.getTransaction();       
        if (transaction instanceof OrderTransactionIfc )
        {
            OrderTransactionIfc orderTransaction = (OrderTransactionIfc)transaction;
           
            setTransactionIdInOrderStatus(orderTransaction);
            
            if (orderTransaction.isOrderPickupOrCancel())
            {
                if (containsInStorePriceDuringPickup(orderTransaction))
                {
                    //set order status
                    updateOrderStatusByInStorePriceItems(orderTransaction);
                    //set order item status
                    
                    updateOrderItemStatusByInStorePriceItems(orderTransaction);
                }
                if (containsTakeWithItem(orderTransaction))
                {
                    //set order status
                    updateOrderStatusBySaleItems(orderTransaction);
                    //set order item status
                    
                    updateOrderItemStatusBySaleItems(orderTransaction);
                }
            }            
        }
    }
     
    /**
     * Check the pickup or cancel order contains take with item or not. Set original transaction id for line items. 
     * Set take with item status.
     * @param orderTransaction
     * @return hasTakeWithItem
     */
    protected boolean containsTakeWithItem(OrderTransactionIfc orderTransaction)
    {
        boolean hasTakeWithItem = false;
      
        for (AbstractTransactionLineItemIfc line : orderTransaction.getLineItems())
        {
            if (line instanceof SaleReturnLineItemIfc)
            {
                SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)line;
                OrderItemStatusIfc orderItemStatus = lineItem.getOrderItemStatus();
                if (orderItemStatus.getItemDispositionCode()==OrderConstantsIfc.ORDER_ITEM_DISPOSITION_SALE)
                {                            
                    hasTakeWithItem = true;
                    break;
                }
            }
        }
        return hasTakeWithItem;
    }    
    
    protected boolean containsInStorePriceDuringPickup(OrderTransactionIfc orderTransaction)
    {
        boolean hasInStorePriceDuringPickup = false;
        
        for (AbstractTransactionLineItemIfc line : orderTransaction.getLineItems())
        {
            if (line instanceof OrderLineItemIfc)
            {
                OrderLineItemIfc lineItem = (OrderLineItemIfc)line;
                if (lineItem.isInStorePriceDuringPickup())
                {                            
                    hasInStorePriceDuringPickup = true;
                    break;
                }
            }
        }
        return hasInStorePriceDuringPickup;        
    }
    /**
     * Update status for the order which include amounts, status, original transaction id, etc
     * @param orderTransaction
     */
    protected void updateOrderStatusBySaleItems(OrderTransactionIfc orderTransaction)
    {
        OrderStatusIfc orderStatus = orderTransaction.getOrderStatus();
        
        CurrencyIfc saleTotalItemsDue = DomainGateway.getBaseCurrencyInstance(); // take with item totals due
        boolean hasSaleItems = false;
        
        for (AbstractTransactionLineItemIfc lineItem : orderTransaction.getLineItems())
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)lineItem;
      
            if (srli.getOrderItemStatus().getItemDispositionCode()==OrderConstantsIfc.ORDER_ITEM_DISPOSITION_SALE)
            {
                // take with items totals 
                saleTotalItemsDue = saleTotalItemsDue.add(srli.getItemPrice().getItemTotal());
                hasSaleItems = true;
            }            
        }
        
        //Update store order total and deposit amount to include take with items 
        orderStatus.setStoreOrderTotal(orderStatus.getStoreOrderTotal().add(saleTotalItemsDue));
        orderStatus.setStoreOrderDepositAmount(orderStatus.getStoreOrderDepositAmount().add(saleTotalItemsDue));
        orderStatus.setSaleAmount(orderStatus.getSaleAmount().add(saleTotalItemsDue));

        //update store order status to reflect the addtion of take with items
        if (hasSaleItems)
        {
            // If this take with item is the first store order item added, set the store order status to completed 
            if (orderStatus.getStoreOrderStatus().getStatus() == OrderConstantsIfc.ORDER_STATUS_UNDEFINED)
            {
                orderStatus.getStoreOrderStatus().changeStatus(OrderConstantsIfc.ORDER_STATUS_COMPLETED);
            }
            // If the store order status is not completed, set it to partial with addtion of the take with item
            else if (orderStatus.getStoreOrderStatus().getStatus() != OrderConstantsIfc.ORDER_STATUS_COMPLETED)
            {
                orderStatus.getStoreOrderStatus().changeStatus(OrderConstantsIfc.ORDER_STATUS_PARTIAL);
            }
        }
    }
    
    /**
     * set status for take with order item which includes original transaction id, order amount and quantity.
     * @param orderTransaction
     * @return hasTakeWithItem
     */
    protected void updateOrderItemStatusBySaleItems(OrderTransactionIfc orderTransaction)
    {       
        int maxStoreOrderLineReference = orderTransaction.getMaxStoreOrderLineReference();
        for (AbstractTransactionLineItemIfc line : orderTransaction.getLineItems())
        {
            if (line instanceof SaleReturnLineItemIfc)
            {
                SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)line;
                OrderItemStatusIfc orderItemStatus = lineItem.getOrderItemStatus();
                if (orderItemStatus.getItemDispositionCode()==OrderConstantsIfc.ORDER_ITEM_DISPOSITION_SALE)
                {                            
                    CurrencyIfc itemTotal = lineItem.getItemPrice().getItemTotal();
                    orderItemStatus.getStatus().setStatus(OrderConstantsIfc.ORDER_ITEM_STATUS_PICKED_UP);
                    orderItemStatus.getStatus().setPreviousStatus(OrderConstantsIfc.ORDER_ITEM_STATUS_NEW);
                    
                    orderItemStatus.getStatus().setPreviousStatusChange(orderItemStatus.getStatus().getLastStatusChange());
                    orderItemStatus.getStatus().setLastStatusChange(orderTransaction.getBusinessDay());
                    
                    orderItemStatus.setQuantityPickedUp(lineItem.getItemQuantityDecimal());
                    
                    orderItemStatus.setQuantityOrdered(lineItem.getItemQuantityDecimal());
                    orderItemStatus.setOrderedAmount(itemTotal);
                    orderItemStatus.setCompletedAmount(itemTotal);
                    orderItemStatus.setDepositAmount(itemTotal);
                    orderItemStatus.setPickupStoreID(orderTransaction.getWorkstation().getStoreID());
                    
                    orderItemStatus.setOriginalTransactionId((TransactionIDIfc)orderTransaction.getTransactionIdentifier().clone());
                    orderItemStatus.setOriginalBusinessDate((EYSDate)orderTransaction.getBusinessDay().clone());
                    orderItemStatus.setOriginalLineNumber(lineItem.getLineNumber());
                    
                    lineItem.setOrderLineReference(++maxStoreOrderLineReference);
                    
                    setLineItemDiscountAndTaxInfo(lineItem);
                }
            }
        }
    }
    
    //set order status
    /**
     * Update status for the order which include amounts, status, original transaction id, etc
     * @param orderTransaction
     */
    protected void updateOrderStatusByInStorePriceItems(OrderTransactionIfc orderTransaction)
    {
        OrderStatusIfc orderStatus = orderTransaction.getOrderStatus();
        
        CurrencyIfc inStorePriceItemsTotalItemsDue = DomainGateway.getBaseCurrencyInstance(); // in store price item totals due
        CurrencyIfc inPriceCancelledItemsTotalItemsDue = DomainGateway.getBaseCurrencyInstance(); // in price cancelled item totals due
        
        for (AbstractTransactionLineItemIfc lineItem : orderTransaction.getLineItems())
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)lineItem;
            
            if (srli.isPickupCancelLineItem())
            {
                OrderLineItemIfc oli = (OrderLineItemIfc)srli;
                if (oli.isInStorePriceDuringPickup())
                {
                    inStorePriceItemsTotalItemsDue = inStorePriceItemsTotalItemsDue.add(oli.getItemPrice().getItemTotal());
                }
                else if (oli.isPriceCancelledDuringPickup())
                {
                    inPriceCancelledItemsTotalItemsDue = inPriceCancelledItemsTotalItemsDue.add(oli.getItemPrice().getItemTotal());
                }                
            }            
        }
        
        CurrencyIfc updateAmount = inStorePriceItemsTotalItemsDue.subtract(inPriceCancelledItemsTotalItemsDue);
        
        //Update xchannel order total and deposit amount to include take with items 
        orderStatus.setXChannelTotal(orderStatus.getXChannelTotal().add(updateAmount));
        orderStatus.setXChannelDepositAmount(orderStatus.getXChannelDepositAmount().add(updateAmount));
    }
    
    //set order item status
    /**
     * 
     * @param orderTransaction
     */
    protected void updateOrderItemStatusByInStorePriceItems(OrderTransactionIfc orderTransaction)
    {
        int maxXChannelOrderLineReference = orderTransaction.getMaxXChannelOrderLineReference();
        for (AbstractTransactionLineItemIfc lineItem : orderTransaction.getLineItems())
        {
            if (lineItem instanceof OrderLineItemIfc)
            {
                OrderLineItemIfc oli = (OrderLineItemIfc)lineItem;
                if (oli.isPickupCancelLineItem())
                {
                    if (oli.isInStorePriceDuringPickup())
                    {
                        CurrencyIfc itemTotal = oli.getItemPrice().getItemTotal();
                        BigDecimal itemQuantity = oli.getItemQuantityDecimal();
                        
                        OrderItemStatusIfc orderItemStatus = oli.getOrderItemStatus();
                        orderItemStatus.setQuantityPickedUp(itemQuantity);
                        orderItemStatus.setQuantityOrdered(itemQuantity);
                        orderItemStatus.setOrderedAmount(itemTotal);
                        orderItemStatus.setCompletedAmount(itemTotal);
                        orderItemStatus.setDepositAmount(itemTotal);
                        
                        oli.setOrderLineReference(++maxXChannelOrderLineReference);
                        
                        setLineItemDiscountAndTaxInfo(oli);
                    }
                }
            }
        }
    }
    
 // --------------------------------------------------------------------------
    /**
     * Returns an order item discount status object
     * @param discountLineNo discount line number
     * @param srln the sale return line item
     * @param itemDiscount the item discount
     * @return OrderItemDiscountStatusIfc
     */
    // --------------------------------------------------------------------------
    protected OrderItemDiscountStatusIfc getOrderItemDiscountStatus(
            SaleReturnLineItemIfc srln, ItemDiscountStrategyIfc itemDiscount)
    {
        OrderItemDiscountStatusIfc discountStatus = DomainGateway.getFactory().getOrderItemDiscountStatusInstance();
        discountStatus.setLineNumber(itemDiscount.getOrderItemDiscountLineReference());
        discountStatus.setTotalAmount(itemDiscount.getDiscountAmount());
        discountStatus.setCompletedAmount(itemDiscount.getDiscountAmount());

        return discountStatus;
    }

    // --------------------------------------------------------------------------
    /**
     * Returns an order item tax status object
     * @param taxLineNo tax line number
     * @param srln the sale return line item
     * @param taxInformation the tax information
     * @return OrderItemTaxStatusIfc
     */
    // --------------------------------------------------------------------------
    protected OrderItemTaxStatusIfc getOrderItemTaxStatus(
            SaleReturnLineItemIfc srln, TaxInformationIfc taxInformation)
    {
        OrderItemTaxStatusIfc taxStatus = DomainGateway.getFactory().getOrderItemTaxStatusInstance();
        taxStatus.setAuthorityID(taxInformation.getTaxAuthorityID());
        taxStatus.setTaxGroupID(taxInformation.getTaxGroupID());
        taxStatus.setTypeCode(taxInformation.getTaxTypeCode());
        taxStatus.setTotalAmount(taxInformation.getTaxAmount());
        taxStatus.setCompletedAmount(taxInformation.getTaxAmount());

        return taxStatus;
    }
    
    /**
     * Set initialTransactionID, recordingTransaction and order beging time
     * @param orderTransaction
     */
    protected void setTransactionIdInOrderStatus(OrderTransactionIfc orderTransaction)
    {
        OrderStatusIfc orderStatus = orderTransaction.getOrderStatus();
        
        //set original store id, workstation id, register id and sequence number and business date
        TransactionIDIfc originalTransactionID = orderTransaction.getOrderStatus().getInitialTransactionID();
        if (originalTransactionID==null || StringUtils.isBlank(originalTransactionID.getStoreID()))
        {
            orderStatus.getInitialTransactionID().setStoreID(orderTransaction.getWorkstation().getStoreID());
            orderStatus.getInitialTransactionID().setWorkstationID(orderTransaction.getWorkstation().getWorkstationID());
            orderStatus.getInitialTransactionID().setSequenceNumber(orderTransaction.getTransactionSequenceNumber());           
            orderStatus.setInitialTransactionBusinessDate((EYSDate)orderTransaction.getBusinessDay().clone());
        }
                
        //set recorded store id, workstation id, register id and sequence number and business date
        TransactionIDIfc recordingTransactionID = orderStatus.getRecordingTransactionID();
        if (recordingTransactionID==null || StringUtils.isBlank(recordingTransactionID.getStoreID()))
        {
            orderStatus.setRecordingTransactionID((TransactionIDIfc)orderTransaction.getTransactionIdentifier().clone());
            orderStatus.setRecordingTransactionBusinessDate((EYSDate)orderTransaction.getBusinessDay().clone());
        }
        
        orderStatus.setTimestampBegin(); 
    }
    
    /**
     * Set line discount and tax info for order line item status.
     * @param lineItem
     */
    protected void setLineItemDiscountAndTaxInfo(SaleReturnLineItemIfc lineItem)
    {
        // initialize order item discount status
        ItemDiscountStrategyIfc[] itemDiscounts = lineItem.getItemPrice().getItemDiscounts();
        int orderItemDiscountLineNo = 0;
        lineItem.getOrderItemStatus().clearDiscountStatus();
        for (ItemDiscountStrategyIfc itemDiscount : itemDiscounts)
        {
            itemDiscount.setOrderItemDiscountLineReference(orderItemDiscountLineNo++);
            OrderItemDiscountStatusIfc itemDiscountStatus = getOrderItemDiscountStatus(lineItem, itemDiscount);
            lineItem.getOrderItemStatus().addDiscountStatus(itemDiscountStatus);

        }

        // initialize order item tax line reference id
        TaxInformationIfc[] taxInformations = lineItem.getItemTax().getTaxInformationContainer().getTaxInformation();
        int orderItemTaxLineNo = 0;
        lineItem.getOrderItemStatus().clearTaxStatus();
        for (TaxInformationIfc taxInformation : taxInformations)
        {
            taxInformation.setOrderItemTaxLineReference(orderItemTaxLineNo++);
            OrderItemTaxStatusIfc itemTaxStatus = getOrderItemTaxStatus(lineItem, taxInformation);
            lineItem.getOrderItemStatus().addTaxStatus(itemTaxStatus);
        }
    }
} // end class TenderCompletedRoad
