/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/ProratedTaxCalculator.java /main/3 2012/12/18 14:07:41 sgu Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* sgu         12/18/12 - calculate prorated order item tax using original order
*                        item status
* sgu         12/12/12 - prorate tax for order pickup, cancel, and return
* sgu         10/17/12 - prorate item tax for partial pickup or cancellation
* sgu         10/17/12 - add new tax calculator class
* sgu         10/17/12 - Creation
* ===========================================================================
*/
package oracle.retail.stores.domain.tax;

import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderItemTaxStatusIfc;
import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;

/**
 * @author sgu
 * 
 * This is a tax calculator used to calculate prorated tax for order 
 * item pickup, cancel, or return. 
 */
public class ProratedTaxCalculator extends ReverseTaxCalculator implements ProratedTaxCalculatorIfc
{
    private static final long serialVersionUID = 4025292070902733163L;

    private BigDecimal quantityToProrate;
    private OrderItemStatusIfc orderItemStatus;
    private Type type;

    /**
     * @return quantity to prorate
     */
    public BigDecimal getQuantityToProrate()
    {
        return quantityToProrate;
    }
    
    /**
     * Set quantity to prorate
     * 
     * @param quantityToProrate quantity to prorate
     */
    public void setQuantityToProrate(BigDecimal quantityToProrate)
    {
        this.quantityToProrate = quantityToProrate;
    }
    /**
     * @return order item status
     */
    public OrderItemStatusIfc getOrderItemStatus() 
    {
        return orderItemStatus;
    }
    /**
     * Set order item status
     * 
     * @param orderItemStatus the order item status
     */
    public void setOrderItemStatus(OrderItemStatusIfc orderItemStatus) 
    {
        this.orderItemStatus = orderItemStatus;
    }
    
    /**
     * @return calculator type
     */
    public Type getType() 
    {
        return type;
    }

    /**
     * Sets calculator type
     * 
     * @param type calculator type
     */
    public void setType(Type type) 
    {
        this.type = type;
    }

    /**
     * Calculate the tax amount for a return
     * @param amount Not used, for returns all amounts are in the TaxInformationIfc[] array.
     * @return Tax charged
     * @see oracle.retail.stores.domain.tax.TaxCalculatorIfc#calculateTaxAmount(oracle.retail.stores.domain.currency.CurrencyIfc)
     */
    public CurrencyIfc calculateTaxAmount(CurrencyIfc amount, TaxLineItemInformationIfc[] lineItems)
    {
        CurrencyIfc totalTaxCharged = null;
        BigDecimal quantityToProrate = getQuantityToProrate();
        BigDecimal quantityProrated = getQuantityProrated();
        BigDecimal quantityTotal = getQuantityTotal();

        // Clone our baseline object, since it should be immutable we cant set values on it.
        TaxInformationIfc cloneArray[] = cloneTaxInformation(this.taxInformationArray);
        CurrencyIfc taxableAmount = getTotalTaxableAmount().prorate(quantityToProrate, quantityProrated, quantityTotal);

        for(int i=0; i<cloneArray.length; i++)
        {
            CurrencyIfc taxAmount = getTotalTaxAmount(cloneArray[i]).prorate(quantityToProrate, quantityProrated, quantityTotal);
           
            cloneArray[i].setEffectiveTaxableAmount(taxableAmount);
            cloneArray[i].setTaxableAmount(taxableAmount);
            cloneArray[i].setTaxAmount(taxAmount);

            if(totalTaxCharged == null)
            {
                totalTaxCharged = cloneArray[i].getTaxAmount();
            }
            else
            {
                totalTaxCharged = totalTaxCharged.add(cloneArray[i].getTaxAmount());
            }
        }
        setCalculatedTaxInformation(cloneArray);
        if(totalTaxCharged == null)
        {
            totalTaxCharged = DomainGateway.getBaseCurrencyInstance();
        }
        return totalTaxCharged;
    }
    
    /**
     * Get the total taxable amount
     * 
     * @return the total taxable amount
     */
    protected CurrencyIfc getTotalTaxableAmount()
    {
        CurrencyIfc taxableAmount = null;
        
        OrderItemStatusIfc orderItemStatus = getOrderItemStatus();
        if (getType().equals(Type.RETURN)) // for order item return
        {
            taxableAmount = orderItemStatus.getCompletedAmount().subtract(
                    orderItemStatus.getReturnedAmount());
        }
        else // for order item pickup or cancel
        {
            taxableAmount = orderItemStatus.getOrderedAmount().subtract(
                    orderItemStatus.getCompletedAmount()).subtract(
                    orderItemStatus.getCancelledAmount());
        }
        return taxableAmount;
    }
    
    /**
     * Get the total tax amount for the tax information passed in
     * 
     * @param taxInfo the tax information
     * @return the total tax amount
     */
    protected CurrencyIfc getTotalTaxAmount(TaxInformationIfc taxInfo)
    {
        CurrencyIfc taxAmount = null;
        
        OrderItemStatusIfc orderItemStatus = getOrderItemStatus();
        OrderItemTaxStatusIfc taxStatus = orderItemStatus.getTaxStatus(taxInfo.getTaxAuthorityID(), 
                taxInfo.getTaxGroupID(), taxInfo.getTaxTypeCode());
        if (getType().equals(Type.RETURN)) // for order item return
        {
            taxAmount = taxStatus.getCompletedAmount().subtract(
                    taxStatus.getReturnedAmount());
        }
        else // for order item pickup or cancel
        {
            taxAmount = taxStatus.getTotalAmount().subtract(
                    taxStatus.getCompletedAmount()).subtract(
                    taxStatus.getCancelledAmount());
        }
        
        return taxAmount;
    }
    
    /**
     * Get the quantity already prorated among the total quantities
     * 
     * @return the quantity prorated
     */
    protected BigDecimal getQuantityProrated()
    {
        BigDecimal quantityProrated = BigDecimal.ZERO;
        if (getType().equals(Type.CANCEL))
        {
            quantityProrated = getOrderItemStatus().getQuantityPickup();
        }
        
        return quantityProrated;
    }
    
    /**
     * Get the total quantity
     * 
     * @return total quantity 
     */
    protected BigDecimal getQuantityTotal()
    {
        BigDecimal quantityTotal = null;
        OrderItemStatusIfc orderItemStatus = getOrderItemStatus();
        if (getType().equals(Type.RETURN)) // for order item return
        {
            quantityTotal = orderItemStatus.getQuantityPickedUp().add(
                    orderItemStatus.getQuantityShipped()).subtract(
                    orderItemStatus.getQuantityReturned());
        }
        else // for order item pickup or cancel
        {
            quantityTotal = orderItemStatus.getQuantityOrdered().subtract(
                    orderItemStatus.getQuantityPickedUp()).subtract(
                    orderItemStatus.getQuantityShipped()).subtract(
                    orderItemStatus.getQuantityCancelled());
        }
        return quantityTotal;
    }
}
