/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/serialnumber/SerializedItemCargo.java /main/15 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/08/12 - implement force use of entered serial number instead
 *                         of checking with SIM
 *    sgu       12/17/10 - check in all
 *    sgu       12/17/10 - XbranchMerge sgu_bug-10373675 from
 *                         rgbustores_13.3x_generic_branch
 *    sgu       12/17/10 - add an array of sale items to be returned
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/15/09 - Added new attributes to cargo to accomodate pickup
 *                         and delivery from Serialisation tour
 *    nkgautam  11/17/09 - Added new attributes to cargo to accomodate pickup
 *                         and delivery from Serialisation tour
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:56 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:12 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:10 PM  Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:51:06  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:01:58   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:18:26   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:37:56   msg
 * Initial revision.
 *
 *    Rev 1.2   16 Jan 2002 13:01:44   baa
 * allow for adding serial item to non serialized items
 * Resolution for POS SCR-579: Unable to manually enter a serial number to an item
 *
 *    Rev 1.1   07 Dec 2001 12:51:56   pjf
 * Code review updates.
 * Resolution for POS SCR-8: Item Kits
 *
 *    Rev 1.0   14 Nov 2001 06:44:42   pjf
 * Initial revision.
 * Resolution for POS SCR-8: Item Kits
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem.serialnumber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;

/**
 * Cargo needed by the serialized item service to retrieve and set serial
 * numbers for items requiring them.
 * 
 * @version $Revision: /main/15 $
 */
public class SerializedItemCargo extends AbstractFinancialCargo implements CargoIfc
{
    private static final long serialVersionUID = -1732467709700494245L;
    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/15 $";
    /**
     * A line item
     */
    protected SaleReturnLineItemIfc item;
    /**
     * A collection of line items
     */
    protected ArrayList<SaleReturnLineItemIfc> lineItems = new ArrayList<SaleReturnLineItemIfc>();
    /**
     * A flag to indicate if item is kit header
     */
    protected boolean kitHeaderFlag = false;
    /**
     * An iterator over the collection of line items to check
     */
    Iterator<SaleReturnLineItemIfc> lineItemIterator;

    /**
     * transaction type - sale or return
     */
    protected RetailTransactionIfc transaction;

    /**
     * Customer to be linked
     */
    protected CustomerIfc customer;

    /**
     * Indicates that pickup or delivery was executed
     */
    protected boolean pickupOrDeliveryExecuted = false;

    /**
     * The item that we are looking for
     */
    protected SearchCriteriaIfc    inquiryItem   = null;

    /**
     * Indicates serial number is being collected for a return.
     */
    protected boolean forReturn = false;

    /**
     * An array of sale items to be returned
     */
    protected SaleReturnLineItemIfc[] returnSaleLineItems = null;

    /**
     * Gets the transaction type.
     * @return transaction type value
     */
    public int getTransactionType()
    {
        int type = TransactionIfc.TYPE_UNKNOWN;

        if (transaction != null)
        {
            type = transaction.getTransactionType();
        }
        return (type);
    }

    /**
     *  Sets the line item collection and initializes an iterator. <P>
     *  @param item The current line item
     */
    public void setLineItems(SaleReturnLineItemIfc[] items)
    {
        lineItems.clear();
        lineItems.addAll(Arrays.asList(items));
        lineItemIterator = lineItems.iterator();
    }
    /**
     *  Returns the current line item.
     *  @return The current line item
     */
    public boolean hasMoreLineItems()
    {
        return lineItemIterator.hasNext();
    }
    /**
       Returns the current line item. <P>
       @return The current line item
     **/
    public SaleReturnLineItemIfc nextLineItem()
    {
        item = lineItemIterator.next();
        return item;
    }
    /**
       Sets the a flag to indicate item is a kit header. <P>
       @param flag  to indicate if item is a kit header
     **/
    public void setKitHeader(boolean flag)
    {
        kitHeaderFlag = flag;
    }
    /**
       Returns the a flag that indicates if item is a kit header. <P>
       @returns boolean flag  to indicate if item is a kit header
     **/
    public boolean isKitHeader()
    {
        return (kitHeaderFlag);
    }

    /**
     * Gets the list of line items
     * @return Arraylist
     */
    public ArrayList<SaleReturnLineItemIfc> getLineItems()
    {
        return lineItems;
    }

    /**
     * sets the list of line items
     * @param lineItems
     */
    public void setLineItems(ArrayList<SaleReturnLineItemIfc> lineItems)
    {
        this.lineItems = lineItems;
    }

    /**
     * gets the SaleReturn Line Item
     * @return SaleReturnLineItemIfc
     */
    public SaleReturnLineItemIfc getItem()
    {
        return item;
    }

    /**
     * Sets the saleReturn Line item
     * @param item
     */
    public void setItem(SaleReturnLineItemIfc item)
    {
        this.item = item;
    }

    /**
     * gets teh pickup or delivery executed boolean
     * @return boolean
     */
    public boolean isPickupOrDeliveryExecuted()
    {
        return pickupOrDeliveryExecuted;
    }

    /**
     * Sets the pickup or delivery executed boolean
     * @param pickupOrDeliveryExecuted
     */
    public void setPickupOrDeliveryExecuted(boolean pickupOrDeliveryExecuted)
    {
        this.pickupOrDeliveryExecuted = pickupOrDeliveryExecuted;
    }

    /**
     * Gets the customer
     * @return CustomerIfc
     */
    public CustomerIfc getCustomer()
    {
        return customer;
    }

    /**
     * Sets the Customer Object
     * @param customer
     */
    public void setCustomer(CustomerIfc customer)
    {
        this.customer = customer;
    }

    /**
     * Gets the Transaction
     * @return RetailTransactionIfc
     */
    public RetailTransactionIfc getTransaction()
    {
        return transaction;
    }

    /**
     * Sets the Transaction
     * @param transaction
     */
    public void setTransaction(RetailTransactionIfc transaction)
    {
        this.transaction = transaction;
    }

    /**
     * Returns the item number.
     * @return String item number.
     */
    public SearchCriteriaIfc getInquiry()
    {
        return(inquiryItem);
    }

    /**
     *  Sets the search criteria.
     *  @param value  search certeria.
     */
    public void  setInquiry(SearchCriteriaIfc inquiry)
    {
        inquiryItem = inquiry;
    }

    /**
     * Sets the Seearch criteria
     * @param itemNo
     * @param itemDesc
     * @param manufacturer
     * @param deptID
     */
    public void  setInquiry(Locale searchLocale, String itemNo, String itemDesc, String deptID, String geoCode)
    {
        if (inquiryItem == null)
        {
            inquiryItem = DomainGateway.getFactory().getSearchCriteriaInstance();
        }
        inquiryItem.setItemID(itemNo);
        inquiryItem.setSearchLocale(searchLocale);
        inquiryItem.setDescription(itemDesc);
        inquiryItem.setGeoCode(geoCode);
        inquiryItem.setDepartmentID(deptID);
    }

    /**
     * @return Returns the forReturn.
     */
    public boolean isForReturn()
    {
        return forReturn;
    }

    /**
     * @param forReturn The forReturn to set.
     */
    public void setForReturn(boolean forReturn)
    {
        this.forReturn = forReturn;
    }

    //----------------------------------------------------------------------
    /**
        Returns the array of sale items to be returned.
        <P>
        @return SaleReturnLineItemIfc[]
    **/
    //----------------------------------------------------------------------
    public SaleReturnLineItemIfc[] getReturnSaleLineItems()
    {
        return returnSaleLineItems;
    }

    //----------------------------------------------------------------------
    /**
        Sets the array of sale items to be returned.
        <P>
        @param  value
    **/
    //----------------------------------------------------------------------
    public void setReturnSaleLineItems(SaleReturnLineItemIfc[] value)
    {
        returnSaleLineItems = value;
    }
}
