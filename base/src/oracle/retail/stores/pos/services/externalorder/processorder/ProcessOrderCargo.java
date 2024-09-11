/* ===========================================================================
* Copyright (c) 2010, 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/processorder/ProcessOrderCargo.java /main/19 2012/10/22 15:36:22 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED (MM/DD/YY)
*    yiqzha 10/19/12 - Refactor by using DestinationTaxRule station to get new
*                      tax rules from shipping destination postal code.
*    yiqzha 04/03/12 - refactor store send for cross channel
*    acadar 08/31/10 - changes for external orders to not filter by action
*                      codes
*    acadar 08/30/10 - do not filter external order items based on action code
*    acadar 08/20/10 - remove line items by line item number not index
*    ohorne 08/09/10 - Siebel shipping address now printed on receipts
*    acadar 07/29/10 - fix dialog message description
*    sgu    07/09/10 - cbeck in after refresh
*    sgu    07/09/10 - donot promot for serial# if the send package id is blank
*    ohorne 07/08/10 - getExternalOrderReturnItems() fix
*    sgu    06/22/10 - added the logic to process multiple send package instead
*                      of just on per order
*    sgu    06/21/10 - check in after merge
*    sgu    06/21/10 - fix tabs
*    sgu    06/21/10 - added site declaration
*    acadar 06/18/10 - poslog changes for external order
*    sgu    06/10/10 - check for null if the item has no related itesm
*    acadar 06/08/10 - changes for signature capture, disable txn send, and
*                      discounts
*    acadar 06/03/10 - changes for signature capture
*    acadar 06/02/10 - refactoring
*    acadar 06/02/10 - signature capture changes
*    acadar 05/28/10 - merged with tip
*    acadar 05/27/10 - added code for displaying a different error message;
*                      additional fixes
*    acadar 05/26/10 - refactor shipping code
*    cgreen 05/26/10 - convert to oracle packaging
*    acadar 05/25/10 - additional fixes for the process order flow
*    acadar 05/21/10 - renamed from _externalorder to externalorder
*    acadar 05/21/10 - additional changes for process order flow
*    acadar 05/17/10 - temporarily rename the package
*    acadar 05/17/10 - added call to ExternalOrderMAnager; additional fixes
*    acadar 05/17/10 - additional logic added for processing orders
*    acadar 05/14/10 - initial version for external order processing
*    acadar 05/14/10 - initial version
* ===========================================================================
*/
package oracle.retail.stores.pos.services.externalorder.processorder;


// domain imports
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import oracle.retail.stores.commerceservices.externalorder.ExternalOrderIfc;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderItemIfc;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderSendPackageIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.externalorder.ExternalOrderSaleItemIfc;
import oracle.retail.stores.domain.externalorder.ExternalOrderSendPackageItemIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.pos.services.sale.SaleCargo;




/**
    This is the cargo used by the external order search service. <p>
**/
public class ProcessOrderCargo extends SaleCargo
{

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 1L;


    /**
     * External Order
     */
    protected ExternalOrderIfc externalOrder;

    /**
     * External Order
     */
    protected List<ExternalOrderItemIfc> externalOrderItems;

    /**
     *  ExternalOrder line items marked for return
     */
    protected List<ExternalOrderItemIfc> externalOrderReturnItems;

    /**
     * External Order Items marked for sale
     */
    protected List<ExternalOrderSaleItemIfc> externalOrderSaleItems;

    /**
     * External order send package line items
     */
    protected List<ExternalOrderSendPackageItemIfc> externalOrderSendPackageItems = new ArrayList<ExternalOrderSendPackageItemIfc>();

    /**
     * Current external order sale item to process
     */
    protected ExternalOrderSaleItemIfc currentExternalOrderItem;

    /**
     * Index of next order item to process
     */
    protected int nextRecord = 0;

    /**
     * Flag that indicates if we need to start iterating over the items in cargo
     * Value is set to true by default
     */
    protected boolean beginIterationOverItems = true;

    /**
     * Current external order send package
     */
    protected ExternalOrderSendPackageItemIfc currentExternalOrderSendPackageItem;

    /**
     * Index of next order send package to process
     */
    protected int nextSendPackage = 0;


    /**
     * Indicates if an external order needs to be locked or not
     */
    protected boolean lockOrder = true;
    
    /**
     * ORPOS shipping method
     */
    protected ShippingMethodIfc shippingMethod;

    /**
     * @return the externalOrder
     */
    public ExternalOrderIfc getExternalOrder()
    {
        return externalOrder;
    }

    /**
     * @param externalOrder the externalOrder to set
     */
    public void setExternalOrder(ExternalOrderIfc externalOrder)
    {
        this.externalOrder = externalOrder;
    }

    /**
     * @return the externalOrderReturnItems
     */
    public List<ExternalOrderItemIfc> getExternalOrderReturnItems()
    {
        if(externalOrderReturnItems == null)
        {
            externalOrderReturnItems = new ArrayList<ExternalOrderItemIfc>();
            ExternalOrderItemIfc item = null;
            Iterator<ExternalOrderItemIfc> itemIt = this.externalOrderItems.iterator();
            while (itemIt.hasNext())
            {
                item = itemIt.next();
                if(!item.isSellItem())
                {
                    externalOrderReturnItems.add(item);
                }
            }
        }

        return externalOrderReturnItems;
    }

    /**
     * @param externalOrderReturnItems the externalOrderReturnItems to set
     */
    public void setExternalOrderReturnItems(List<ExternalOrderItemIfc> externalOrderReturnItems)
    {
        this.externalOrderReturnItems = externalOrderReturnItems;
    }

    /**
     * @return the externalOrderSaleItems
     */
    public List<ExternalOrderSaleItemIfc> getExternalOrderSaleItems()
    {
        if(this.externalOrderSaleItems == null)
        {
            externalOrderSaleItems = new ArrayList<ExternalOrderSaleItemIfc>();
            ExternalOrderItemIfc item = null;
            ExternalOrderSaleItemIfc saleItem = null;
            Iterator<ExternalOrderItemIfc> itemIt = this.externalOrderItems.iterator();
            while (itemIt.hasNext())
            {
                item = itemIt.next();
                if(item.isSellItem())
                {
                    saleItem = DomainGateway.getFactory().getExternalOrderSaleItemInstance();
                    saleItem.setDescription(item.getDescription());
                    saleItem.setId(item.getId());
                    saleItem.setParentId(item.getParentId());
                    saleItem.setPOSItemId(item.getPOSItemId());
                    saleItem.setPrice(item.getPrice());
                    saleItem.setQuantity(item.getQuantity());
                    saleItem.setSellItem(item.isSellItem());
                    saleItem.setSequence(item.getSequence());
                    saleItem.setSendPackageId(item.getSendPackageId());
                    saleItem.setUpdateSourceFlag(item.isUpdateSourceFlag());
                    externalOrderSaleItems.add(saleItem);
                }
            }
        }
        return externalOrderSaleItems;
    }

    /**
     * @param externalOrderSaleItems the externalOrderSaleItems to set
     */
    public void setExternalOrderSaleItems(List<ExternalOrderSaleItemIfc> externalOrderSaleItems)
    {
        this.externalOrderSaleItems = externalOrderSaleItems;
    }

    /**
     * @return the currentExternalOrderItem
     */
    public ExternalOrderSaleItemIfc getCurrentExternalOrderItem()
    {
        return currentExternalOrderItem;
    }

    /**
     * @param currentExternalOrderItem the currentExternalOrderItem to set
     */
    public void setCurrentExternalOrderItem(ExternalOrderSaleItemIfc currentExternalOrderItem)
    {
        this.currentExternalOrderItem = currentExternalOrderItem;
    }

    /**
     * @return the nextRecord
     */
    public int getNextRecord()
    {
        return nextRecord;
    }

    /**
     * @param nextRecord the nextRecord to set
     */
    public void setNextRecord(int nextRecord)
    {
        this.nextRecord = nextRecord;
    }

    /**
     * @return the externalOrderItems
     */
    public List<ExternalOrderItemIfc> getExternalOrderItems()
    {
        return externalOrderItems;
    }

    /**
     * @param externalOrderItems the externalOrderItems to set
     */
    public void setExternalOrderItems(List<ExternalOrderItemIfc> externalOrderItems)
    {
        this.externalOrderItems = externalOrderItems;
    }



    /**
     * @return the beginIterationOverItems
     */
    public boolean isBeginIterationOverItems()
    {
        return beginIterationOverItems;
    }

    /**
     * @param beginIterationOverItems the beginIterationOverItems to set
     */
    public void setBeginIterationOverItems(boolean beginIterationOverItems)
    {
        this.beginIterationOverItems = beginIterationOverItems;
    }

    /**
     * Remove sale items from transaction that belong to an external order
     */
    public void removeExternalOrderItemsFromTransaction()
    {

      Vector<AbstractTransactionLineItemIfc> saleLineItems = transaction.getLineItemsVector();
      List<SaleReturnLineItemIfc> itemsForRemoval = new ArrayList<SaleReturnLineItemIfc>(saleLineItems.size());

      SaleReturnLineItemIfc saleItem = null;
      for (int i = 0; i < saleLineItems.size(); i++)
      {
          saleItem = (SaleReturnLineItemIfc) saleLineItems.elementAt(i);

          if (saleItem.isFromExternalOrder() )
          {
              //remove sale items from external order and any related items associated with the sale item
              SaleReturnLineItemIfc[] relatedItems = saleItem.getRelatedItemLineItems();

              itemsForRemoval.add(saleItem);
              if (relatedItems != null)
              {
            	  itemsForRemoval.addAll(Arrays.asList(relatedItems));
              }
          }
      }

      for (int i= 0; i < itemsForRemoval.size(); i++)
      {
          SaleReturnLineItemIfc lineToRemove = itemsForRemoval.get(i);
          ((SaleReturnTransactionIfc)transaction).removeLineItem(lineToRemove.getLineNumber());
      }


    }

    /**
     * @return the external order send package items
     */
    public List<ExternalOrderSendPackageItemIfc> getExternalOrderSendPackageItems()
    {
    	return externalOrderSendPackageItems;
    }

    /**
     * Add external order send package item
     * @param shippingMethod
     * @param sendPackage
     * @return
     */
    public List<ExternalOrderSendPackageItemIfc> addExternalOrderSendPackageItem(
    		String externalOrderSendPackageId, ShippingMethodIfc shippingMethod, ExternalOrderSendPackageIfc sendPackage)
    {
    	ExternalOrderSendPackageItemIfc sendPackageLineItem = DomainGateway.getFactory().getExternalOrderSendPackageItemInstance();
    	sendPackageLineItem.setExternalOrderSendPackageId(externalOrderSendPackageId);
    	sendPackageLineItem.setShippingMethod(shippingMethod);

    	CustomerIfc dummyCustomer = DomainGateway.getFactory().getCustomerInstance();
    	AddressIfc address = DomainGateway.getFactory().getAddressInstance();
    	address.addAddressLine(sendPackage.getDestinationAddressLine1());
    	address.addAddressLine(sendPackage.getDestinationAddressLine2());
    	address.setCity(sendPackage.getDestinationCity());
    	address.setState(sendPackage.getDestinationState());
    	address.setPostalCode(sendPackage.getDestinationPostalCode());
    	address.setCountry(sendPackage.getDestinationCountry());
    	PhoneIfc phone = DomainGateway.getFactory().getPhoneInstance();
    	dummyCustomer.addAddress(address);
    	dummyCustomer.addPhone(phone);
    	sendPackageLineItem.setCustomer(dummyCustomer);

    	externalOrderSendPackageItems.add(sendPackageLineItem);

    	return externalOrderSendPackageItems;
    }

    /**
     * @return the current external order send package
     */
    public ExternalOrderSendPackageItemIfc getCurrentExternalOrderSendPackage()
    {
		return currentExternalOrderSendPackageItem;
	}

    /**
     * Set the current external order send package
     * @param currentExternalOrderSendPackage
     */
    public void setCurrentExternalOrderSendPackage(
    		ExternalOrderSendPackageItemIfc currentExternalOrderSendPackageItem)
    {
    	this.currentExternalOrderSendPackageItem = currentExternalOrderSendPackageItem;
    }

    /**
     * @return the next send package to process
     */
    public int getNextSendPackage()
    {
    	return nextSendPackage;
    }

    /**
     * Set the next send package to process
     * @param nextSendPackage
     */
    public void setNextSendPackage(int nextSendPackage)
    {
    	this.nextSendPackage = nextSendPackage;
    }

    /**
     * Removes all the references to external order
     * from the transaction
     *
     */
    public void cleanUpTransaction()
    {

        removeExternalOrderItemsFromTransaction();
        ((SaleReturnTransactionIfc)transaction).setExternalOrderID("");
        ((SaleReturnTransactionIfc)transaction).setExternalOrderNumber("");
        ((SaleReturnTransactionIfc)transaction).setRequireServiceContractFlag(false);
   }

    /**
     * @return the lockOrder
     */
    public boolean isLockOrder()
    {
        return lockOrder;
    }

    /**
     * @param lockOrder the lockOrder to set
     */
    public void setLockOrder(boolean lockOrder)
    {
        this.lockOrder = lockOrder;
    }


    /**
     * @return the shippingMethod
     */
    public ShippingMethodIfc getShippingMethod()
    {
        return shippingMethod;
    }

    /**
     * @param shippingMethod the shippingMethod to set
     */
    public void setShippingMethod(ShippingMethodIfc shippingMethod)
    {
        this.shippingMethod = shippingMethod;
    }
}


