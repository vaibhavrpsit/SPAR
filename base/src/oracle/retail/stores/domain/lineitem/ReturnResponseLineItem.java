/* =============================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * =============================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/lineitem/ReturnResponseLineItem.java /main/5 2013/11/05 17:41:09 cgreene Exp $
 * =============================================================================
 * NOTES
 *   Written by Lucy Zhao (Oracle Consulting) for POS to Returns Management
 *   integration.
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 11/05/13 - refactor to pass ItemPrice to returnResponseLineItem
 *                      for displaying on Return Response screen.
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    rkar   11/04/08 - Added for POS-RM integration
 *
 * =============================================================================
 */
package oracle.retail.stores.domain.lineitem;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;

/**
 * Represents a line item in the Return Response message from a Returns
 * Management server.
 */
public class ReturnResponseLineItem extends SaleReturnLineItem implements ReturnResponseLineItemIfc
{
    private static final long serialVersionUID = 6355248973389635039L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static Logger logger = Logger.getLogger(ReturnResponseLineItem.class);

    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /main/5 $";

    /**
     * approve or deny code from RM server
     */
    protected String approveDenyCode;

    /**
     * Description of the response code
     */
    protected String responseDescription;

    /**
     * Did the manager override RM's approve/deny code
     */
    protected boolean managerOverride = false;

    /**
     * the index of sale return line items of the transaction. used for removing
     * the item when denied from RM
     */
    protected int saleReturnLineItemIndex = -1;

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.ReturnResponseLineItemIfc#initialize(java.lang.String, java.lang.String, oracle.retail.stores.domain.stock.PLUItemIfc, java.math.BigDecimal, oracle.retail.stores.domain.lineitem.ItemTaxIfc, oracle.retail.stores.domain.employee.EmployeeIfc, oracle.retail.stores.domain.registry.RegistryIDIfc, oracle.retail.stores.domain.lineitem.ReturnItemIfc)
     */
    @Deprecated
    public void initialize(String approveDenyCode, String responseDescription, PLUItemIfc item, BigDecimal quantity,
            ItemTaxIfc tax, EmployeeIfc pSalesAssociate, RegistryIDIfc pRegistry, ReturnItemIfc pReturnItem)
    {
        initialize(item, quantity, tax, pSalesAssociate, pRegistry, pReturnItem);
        this.approveDenyCode = approveDenyCode;
        this.responseDescription = responseDescription;
        this.setManagerOverride(false);
    }

    
    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.ReturnResponseLineItemIfc#initialize(java.lang.String, java.lang.String, oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc)
     */
    @Override
    public void initialize(String approveDenyCode, String responseDescription, SaleReturnLineItemIfc returnLineItem)
    {
        initialize(returnLineItem.getPLUItem(), returnLineItem.getItemQuantityDecimal(),
                returnLineItem.getItemTax(), returnLineItem.getSalesAssociate(),
                returnLineItem.getRegistry(), returnLineItem.getReturnItem());
        this.itemPrice = (ItemPriceIfc)returnLineItem.getItemPrice().clone();
        this.approveDenyCode = approveDenyCode;
        this.responseDescription = responseDescription;
        setSaleReturnLineItemIndex(returnLineItem.getLineNumber());
        setLineNumber(returnLineItem.getLineNumber());
        setManagerOverride(false);
    }


    /**
     * Copies object.
     * 
     * @return generic object copy of this ReturnResponseLineItem object
     */
    @Override
    public Object clone()
    {
        ReturnResponseLineItem newRrli = new ReturnResponseLineItem();
        setCloneAttributes(newRrli);
        return newRrli;
    }

    /**
     * Clones the attributes of this class attributes. This is to be called by
     * the clone of the children with an new instance of this class.
     * 
     * @param newSrli new ReturnResponseLineItem instance
     */
    protected void setCloneAttributes(ReturnResponseLineItem newRrli)
    {
        // clone superclass attributes
        super.setCloneAttributes(newRrli);

        // clone PLU item, if valid
        newRrli.approveDenyCode = approveDenyCode;
        newRrli.responseDescription = responseDescription;
        newRrli.managerOverride = managerOverride;
        newRrli.saleReturnLineItemIndex = saleReturnLineItemIndex;
    }

    /**
     * Determines if two objects are identical.
     * 
     * @param obj object to compare with
     * @return true if the objects are identical, false otherwise
     */
    @Override
    public boolean equals(Object obj)
    {
        boolean isEqual = false;

        try
        {
            ReturnResponseLineItem c = (ReturnResponseLineItem)obj;

            // compare all the attributes of SaleReturnLineItem
            if (!super.equals(obj))
            {
                isEqual = false;
            }
            else if (!approveDenyCode.equals(c.approveDenyCode))
            {
                isEqual = false;
            }
            else if (!responseDescription.equals(c.responseDescription))
            {
                isEqual = false;
            }
            else if (!managerOverride == c.managerOverride)
            {
                isEqual = false;
            }
            else if (saleReturnLineItemIndex != c.saleReturnLineItemIndex)
            {
                isEqual = false;
            }
        }
        catch (Exception e)
        {
            isEqual = false;
        }
        return (isEqual);
    }

    /**
     * Returns string representation of object.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        StringBuilder strResult = new StringBuilder("Class:  ReturnResponseLineItem (Revision " + getRevisionNumber() + ") @"
                + hashCode());
        strResult.append("\n").append(super.toString());

        strResult.append("\n\tApproveDenyCode:      [").append(approveDenyCode).append("]");
        strResult.append("\n\tEesponse Description: [").append(responseDescription).append("]");
        strResult.append("\n\tGift Receipt Flag: [").append(managerOverride).append("]");

        return strResult.toString();
    }

    /**
     * @return code for RM's recommendation whether to approve/deny return
     * @see oracle.retail.stores.domain.lineitem.ReturnResponseLineItemIfc#
     *      getApproveDenyCode()
     */
    public String getApproveDenyCode()
    {
        return approveDenyCode;
    }

    /**
     * @param approveDenyCode RM's recommendation whether to approve/deny return
     * @see oracle.retail.stores.domain.lineitem.ReturnResponseLineItemIfc#
     *      setApproveDenyCode(java.lang.String)
     */
    public void setApproveDenyCode(String approveDenyCode)
    {
        this.approveDenyCode = approveDenyCode;
    }

    /**
     * @return Description of RM's response code for this line-item
     * @see oracle.retail.stores.domain.lineitem.ReturnResponseLineItemIfc#
     *      getResponseDescription()
     */
    public String getResponseDescription()
    {
        return responseDescription;
    }

    /**
     * @param responseDescription Description of RM's response code for this
     *            line-item
     * @see oracle.retail.stores.domain.lineitem.ReturnResponseLineItemIfc#
     *      setResponseDescription(java.lang.String)
     */
    public void setResponseDescription(String responseDescription)
    {
        this.responseDescription = responseDescription;
    }

    /**
     * @return true if manager overrode RM's approve/deny recommendation
     * @see oracle.retail.stores.domain.lineitem.ReturnResponseLineItemIfc#
     *      isManagerOverride()
     */
    public boolean isManagerOverride()
    {
        return managerOverride;
    }

    /**
     * @param managerOverride true if manager overrode RM's approve/deny
     *            recommendation
     * @see oracle.retail.stores.domain.lineitem.ReturnResponseLineItemIfc#
     *      setManagerOverride(boolean)
     */
    public void setManagerOverride(boolean managerOverride)
    {
        this.managerOverride = managerOverride;
    }

    /**
     * Get the index of current selected line item
     * 
     * @see oracle.retail.stores.domain.lineitem.ReturnResponseLineItemIfc#
     *      getSaleReturnLineItemIndex()
     */
    public int getSaleReturnLineItemIndex()
    {
        return saleReturnLineItemIndex;
    }

    /**
     * Set the index of current selected line item
     * 
     * @see oracle.retail.stores.domain.lineitem.ReturnResponseLineItemIfc#
     *      setSaleReturnLineItemIndex(int)
     */
    public void setSaleReturnLineItemIndex(int saleReturnLineItemIndex)
    {
        this.saleReturnLineItemIndex = saleReturnLineItemIndex;
    }
}
