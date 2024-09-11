/* ===========================================================================
* Copyright (c) 2007, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/lineitem/SendPackageLineItem.java /main/11 2012/04/30 15:55:33 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   04/26/12 - remove unused methods
 *    yiqzhao   04/03/12 - refactor store send for cross channel
 *    sgu       08/25/11 - fix taxable amount calculation in threshold tax rule
 *                         and the proration algorithm
 *    cgreene   05/26/10 - convert to oracle packaging
 *    sgu       05/18/10 - enhance SaleReturnLineItem class to store external
 *                         order info
 *    abondala  01/03/10 - update header date
 *    cgreene   01/08/09 - added packageNumber
 *
 * ===========================================================================
 * $Log:
 *  5    360Commerce 1.4         6/4/2007 1:44:23 PM    Sandy Gu        rework
 *       based on review comments
 *  4    360Commerce 1.3         5/31/2007 5:58:47 PM   Sandy Gu        added
 *       test cases
 *  3    360Commerce 1.2         5/22/2007 9:11:26 AM   Sandy Gu        Enhance
 *        financial totals for VAT
 *  2    360Commerce 1.1         5/14/2007 6:08:34 PM   Sandy Gu        update
 *       inclusive information in financial totals and history tables
 *  1    360Commerce 1.0         4/30/2007 5:38:06 PM   Sandy Gu        added
 *       send package line items and inclusive tax calculator
 * $
 * ===========================================================================
 */
package oracle.retail.stores.domain.lineitem;

import java.math.BigInteger;


import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.foundation.utility.Util;

import org.apache.log4j.Logger;

/**
 * Class to provide send package line item
 *
 */
public class SendPackageLineItem implements SendPackageLineItemIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 6853776054451915208L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(SendPackageLineItem.class);

    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /main/11 $";

    /**
     * shipping method
     */
    protected ShippingMethodIfc shippingMethod;

    /**
     * customer
     */
    protected CustomerIfc customer;

    /**
     * Keep track of whether or not this line item is from a transaction. If it
     * is, the tax amounts will be preserved and not recalculated.
     */
    protected boolean fromTransaction = false;

    /**
     * A 1-based index of which number this send package is within the
     * transaction. Defaults to 1.
     */
    protected int packageNumber = 1;

    /**
     * A flag indicating if the send is set by an external system (For ex Siebel)
     */
    protected boolean externalSendFlag = false;

    /**
     * Constructor
     */
    public SendPackageLineItem()
    {
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.SendPackageLineItemIfc#initialize(oracle.retail.stores.domain.financial.ShippingMethodIfc, oracle.retail.stores.domain.customer.CustomerIfc, oracle.retail.stores.domain.lineitem.ItemTaxIfc)
     */
    public void initialize(ShippingMethodIfc shippingMethod, CustomerIfc customer)
    {
        this.shippingMethod = shippingMethod;
        this.customer = customer;
        //this.itemTax = itemTax;
    }

    /**
     * @return the packageNumber
     */
    public int getPackageNumber()
    {
        return packageNumber;
    }

    /**
     * @param packageNumber the packageNumber to set
     */
    public void setPackageNumber(int packageNumber)
    {
        this.packageNumber = packageNumber;
    }

    /**
     * @return shipping method
     */
    public ShippingMethodIfc getShippingMethod()
    {
        return shippingMethod;
    }

    /**
     * @param shippingMethod shipping method to set
     */
    public void setShippingMethod(ShippingMethodIfc shippingMethod)
    {
        this.shippingMethod = shippingMethod;
    }

    /**
     * @return customer linked to the send
     */
    public CustomerIfc getCustomer()
    {
        return customer;
    }

    /**
     * @param customer customer linked to the send
     */
    public void setCustomer(CustomerIfc customer)
    {
        this.customer = customer;
    }

    /**
     * Tell whether or not this line item is a KitHeader.
     *
     * @return
     */
    public boolean isKitHeader()
    {
        return false; // shipping charge will not be treated as a kit header
    }


    /**
     * Set whether or not this sale item came from an already tendered
     * transaction.
     *
     * @param val true or false
     */
    public void setFromTransaction(boolean val)
    {
        this.fromTransaction = val;
    }

    /**
     * Get whether or not this line item came from an already tendered
     * transaction.
     *
     * @return true or false
     */
    public boolean isFromTransaction()
    {
        return this.fromTransaction;
    }
    
    /**
     * @return the flag indicating if the send is set by an external system
     */
    public boolean isExternalSend()
    {
        return externalSendFlag;
    }

    /**
     * Set the external send flag indicating if the send is set by an external system
     * @param externalSendFlag
     */
    public void setExternalSendFlag(boolean externalSendFlag)
    {
        this.externalSendFlag = externalSendFlag;
    }

    /**
     * Retrieves item quantity. <P>
     * @return item quantity
     **/
    //---------------------------------------------------------------------
    public Number getItemQuantity()
    {
        return BigInteger.ONE;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone()
    {
        SendPackageLineItem newSdLi = new SendPackageLineItem();

        setCloneAttributes(newSdLi);

        return newSdLi;
    }

    /**
     * Clones the attributes of this class attributes. This is to be called by
     * the clone of the children with an new instance of this class.
     * <p>
     *
     * @param newSrli new SaleReturnLineItem instance
     */
    protected void setCloneAttributes(SendPackageLineItem newSdLi)
    {
        if (shippingMethod != null)
        {
            newSdLi.setShippingMethod((ShippingMethodIfc)shippingMethod.clone());
        }
        if (customer != null)
        {
            newSdLi.setCustomer((CustomerIfc)customer.clone());
        }

        newSdLi.setFromTransaction(fromTransaction);
        newSdLi.setExternalSendFlag(externalSendFlag);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        boolean isEqual = false;

        try
        {
            SendPackageLineItem c = (SendPackageLineItem)obj;

            // compare all the attributes of SaleReturnLineItem
            if (!Util.isObjectEqual(getShippingMethod(), c.getShippingMethod()))
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(getCustomer(), c.getCustomer()))
            {
                isEqual = false;
            }
           /* else if (!Util.isObjectEqual(getItemTax(), c.getItemTax()))
            {
                isEqual = false;
            }*/
            else if (isFromTransaction() != c.isFromTransaction())
            {
                isEqual = false;
            }
            else if (isExternalSend() != c.isExternalSend())
            {
            	isEqual = false;
            }
            else
            {
                isEqual = true;
            }
        }
        catch (Exception e)
        {
            isEqual = false;
        }
        return (isEqual);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder strResult = new StringBuilder("Class:  SendPackageLineItem (Revision ");
        strResult.append(getRevisionNumber()).append(") @").append(hashCode());
        strResult.append("\n").append(super.toString());

        strResult.append("\n\tShippingMethod: [").append(getShippingMethod() != null ? getShippingMethod().toString() : null).append("]");
        strResult.append("\n\tCustomer: [").append(getCustomer() != null ? getCustomer().toString() : null).append("]");
        //strResult.append("\n\tItemTax: [").append(getItemTax() != null ? getItemTax().toString() : null).append("]");
        strResult.append("\n\tFromTransaction: [").append(isFromTransaction()).append("]");
        strResult.append("\n\tExternalSend: [").append(isExternalSend()).append("]");

        return strResult.toString();
    }

    /**
     * Returns the revision number.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return revisionNumber;
    }

}
