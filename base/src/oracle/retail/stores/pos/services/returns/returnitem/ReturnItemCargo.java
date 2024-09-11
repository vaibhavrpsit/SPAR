/* ===========================================================================
* Copyright (c) 2007, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/ReturnItemCargo.java /main/20 2014/06/10 15:26:15 arabalas Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   11/14/14 - handle nullpointerexception when running on mpos.
 *    jswan     11/03/14 - added UIN (SIM) lookup to the non-receipted returns flow.
 *    arabalas  05/15/14 - added ExitAfterItemAddition related variables
 *    rabhawsa  07/30/13 - pluitem for different size should be identified by
 *                         size.
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    npoola    12/01/10 - set the current item to 0 when the deleted item is
 *                         added back to cargo
 *    sgu       09/14/10 - fix indentation
 *    sgu       09/14/10 - increment current index
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     06/01/10 - Checked in for refresh to latest lable.
 *    jswan     05/27/10 - First pass changes to return item for external order
 *                         project.
 *    jswan     05/14/10 - ExternalOrder mods checkin for refresh to tip.
 *    jswan     05/12/10 - Modify cargos for external order items return.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mchellap  01/08/09 - Setting Original Transaction Business Date to return
 *                         item
 *
 * ===========================================================================
 * $Log:
 *    7    360Commerce 1.6         3/25/2008 6:21:36 AM   Vikram Gopinath CR
 *         #30683, porting changes from v12x. Save the correct pos department
 *         id for an unknown item.
 *    6    360Commerce 1.5         3/10/2008 3:51:48 PM   Sandy Gu
 *         Specify store id for non receipted return item query.
 *    5    360Commerce 1.4         4/25/2007 8:52:14 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    4    360Commerce 1.3         1/22/2006 11:45:18 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:29:44 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:51 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:53 PM  Robert Pearse
 *
 *   Revision 1.24  2004/07/30 14:52:30  jdeleau
 *   @scr 6530 Update quantity for return without receipt on
 *   an unknown item.
 *
 *   Revision 1.23  2004/07/29 19:10:14  rsachdeva
 *   @scr 5442 Item Not Found Cancel for Returns
 *
 *   Revision 1.22  2004/07/19 19:55:59  mweis
 *   @scr 5387 Return an unknown item with a serial number forces all other return items to have its serial number.
 *
 *   Revision 1.21  2004/06/24 21:34:46  mweis
 *   @scr 5792 Return of item w/out receipt, no wait as a gift receipt, no wait w/out receipt crashes app
 *
 *   Revision 1.20  2004/06/23 20:03:50  mweis
 *   @scr 5385 Return of UnknownItem with serial and size blows up app
 *
 *   Revision 1.19  2004/06/07 19:59:00  mkp1
 *   @scr 2775 Put correct header on files
 *
 *   Revision 1.18  2004/06/02 20:57:16  mweis
 *   @scr 5354 Returns: removing an invalid return item blows up the application
 *
 *   Revision 1.17  2004/03/24 20:24:32  epd
 *   @scr 3561 added code to properly adjust "currentItem" when modifying the list of items, any of which could be current
 *
 *   Revision 1.16  2004/03/24 20:06:52  epd
 *   @scr 3561 fixed bug in removing item routines due to wrong equality check.  Should be using != instead of !equals()
 *
 *   Revision 1.15  2004/03/22 22:39:45  epd
 *   @scr 3561 Refactored cargo to get rid of itemQuantities attribute.  Added it to ReturnItemIfc instead.  Refactored to reduce code complexity and confusion.
 *
 *   Revision 1.14  2004/03/22 06:17:50  baa
 *   @scr 3561 Changes for handling deleting return items
 *
 *   Revision 1.13  2004/03/18 23:01:56  baa
 *   @scr 3561 returns fixes for gift card
 *
 *   Revision 1.12  2004/03/12 19:36:48  epd
 *   @scr 3561 Updates for handling kit items in non-retrieved no receipt returns
 *
 *   Revision 1.11  2004/03/10 14:16:46  baa
 *   @scr 0 fix javadoc warnings
 *
 *   Revision 1.10  2004/03/08 22:54:55  epd
 *   @scr 3561 Updates for entering detailed return item info
 *
 *   Revision 1.9  2004/03/05 22:43:44  aarvesen
 *   @scr 3561 fixed itemQuantity to index into the current array
 *
 *   Revision 1.8  2004/03/02 18:49:54  baa
 *   @scr 3561 Returns add size info to journal and receipt
 *
 *   Revision 1.7  2004/03/01 21:29:04  aarvesen
 *   @scr 3561  Fix so that receipt-less returns show the overridden return price
 *
 *   Revision 1.6  2004/03/01 19:35:28  epd
 *   @scr 3561 Updates for Returns.  Items now have tax rates applied based on entered store #
 *
 *   Revision 1.5  2004/02/16 13:37:14  baa
 *   @scr  3561 returns enhancements
 *
 *   Revision 1.4  2004/02/13 13:57:20  baa
 *   @scr 3561  Returns enhancements
 *
 *   Revision 1.3  2004/02/12 16:51:49  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   05 Feb 2004 23:22:10   baa
 * returns multi items
 *
 *    Rev 1.1   Dec 17 2003 11:20:46   baa
 * return enhancements
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 *
 *    Rev 1.0   Aug 29 2003 16:06:08   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Feb 16 2003 10:43:32   mpm
 * Merged 5.1 changes.
 * Resolution for POS SCR-2053: Merge 5.1 changes into 6.0
 *
 *    Rev 1.0   Apr 29 2002 15:05:36   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

import java.math.BigDecimal;
import java.util.ArrayList;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.ObjectRestoreException;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.returns.returncommon.AbstractReturnItemCargo;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnData;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnItemCargoIfc;
import oracle.retail.stores.pos.utility.QuarrySnapshot;

import org.apache.log4j.Logger;

/**
 * Cargo for the Return Item service.
 * 
 * @version $Revision: /main/20 $
 */
public class ReturnItemCargo extends AbstractReturnItemCargo
    implements ReturnItemCargoIfc, TourCamIfc
{
    /** serialVersionUID */
    private static final long serialVersionUID = -2189117860017122161L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(ReturnItemCargo.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/20 $";

    /**
     * PLU item ID to lookup
     */
    protected String PLUItemID = null;

    /**
     * PLU item returned
     */
    protected PLUItemIfc pluItem = null;

    /**
     * Return Item information
     */
    protected ReturnItemIfc returnItem = null;

    /**
     * The index into the current element of the arrays.
     */
    protected int currentItem = -1;

    /**
     * The index into the current element of the arrays.
     */
    protected int[] selectedItems = null;

    /**
     * this flag is set when the child should transfer its Cargo to the parent's
     * Cargo
     */
    protected boolean transferCargo = false;

    /**
     * Access employee
     */
    protected EmployeeIfc accessEmployee = null;

    /**
     * Return data - this field is used when collecting kit component items for
     * manual return via returntransaction/returntransaction.xml.
     */
    protected ReturnData returnData = null;

    /*
     * Gift Receipt Flag
     */
    // Note: see superclass for "giftReceiptSelected"
    /**
     * CurrencyIfc Price
     */
    protected CurrencyIfc price = null;

    /**
     * department name
     */
    protected String department = null;

    /**
     * department id
     */
    protected String departmentID = null;

    /**
     * The sale return line item selected from the list. These contain the
     * modifications from the return item info screen.
     */
    protected SaleReturnLineItemIfc[] returnSaleLineItems = null;

    /**
     * The return items for line items selected from the list.
     */
    protected ReturnItemIfc[] returnItems = null;

    /**
     * Flag indicates that the tax rates unavailable dialog was displayed for
     * the current receipt
     */
    protected boolean displayedTaxRatesUnavailableDialog = false;

    /**
     * The geoCode used for taxes
     */
    protected String geoCode;

    /**
     * The store ID
     */
    protected String storeID;

    /**
     * enable cancel if goes to item not found through return
     */
    protected boolean enableCancelItemNotFoundFromReturns;

    protected BigDecimal unknownItemQuantity;

    /**
     * The return item tour retrieves all PLU items before processing them. This
     * index tracks the index of the last returnSaleLineItem that completed the
     * returns process.
     */
    protected int lastLineItemReturnedIndex = -1;

    /**
     * flag indicating whether the service needs to be exited after adding a return item
     */
    protected boolean exitAfterItemAddition = false;
    
    /**
     * flag indicating whether the service needs to be exited after deleting a return item
     */
    protected boolean exitAfterItemDeletion = false;

    /**
     * flag indicating whether all the items to returned are added
     */
    protected boolean returnItemsAdded = true;

    /**
     * Determines which location the item should be read from.
     */
    protected ItemLookupType itemLookupLocaction = ItemLookupType.STORE;

    /**
     * Class Constructor.
     * <p>
     * Initializes the reason code list for item returns.
     */
    public ReturnItemCargo()
    {
        super();
    }

    /**
     * Sets the item quantity for the current return item
     * 
     * @param value the item quantity
     */
    public void setItemQuantity(BigDecimal value)
    {
        ReturnItemIfc returnItem = getReturnItem();
        returnItem.setItemQuantity(value);
    }

    /**
     * Gets the item quantity for the current item.
     * 
     * @return the item quantity
     */
    public BigDecimal getItemQuantity()
    {
        return super.getItemQuantity();
    }

    /**
     * Get the quantity of unknown items being returned
     * 
     * @return quantity of unknown items
     */
    public BigDecimal getUnknownItemQuantity()
    {
        return this.unknownItemQuantity;
    }

    /**
     * Set the quantity of unknown items being returned
     * 
     * @param value
     */
    public void setUnknownItemQuantity(BigDecimal value)
    {
        this.unknownItemQuantity = value;
    }

    /**
     * Returns the price.
     * 
     * @return CurrencyIfc
     */
    public CurrencyIfc getPrice()
    {
        return price;
    }

    /**
     * Sets the price.
     * 
     * @param value teh price
     */
    public void setPrice(CurrencyIfc value)
    {
        price = value;
    }

    /**
     * Returns whether we have a current item.
     * 
     * @return Whether we have a current item.
     */
    public boolean hasCurrentItem()
    {
        return (currentItem > -1);
    }

    /**
     * Sets the current item index.
     * 
     * @param value the index
     */
    public void setCurrentItem(int value)
    {
        currentItem = value;
    }

    /**
     * Returns the selected item indexes.
     * 
     * @return int[]
     */
    public int[] getSelectedItems()
    {
        return selectedItems;
    }

    /**
     * Sets the selected item indexes.
     * 
     * @param value the indexes
     */
    public void setSelectedItems(int[] value)
    {
        selectedItems = value;
    }

    /**
     * Returns the current item index.
     * 
     * @return int
     */
    public int getCurrentItem()
    {
        return currentItem;
    }

    /**
     * Retrieves PLU item.
     * 
     * @return PLU item
     */
    public PLUItemIfc getPLUItem()
    {
        return pluItem;
    }

    /**
     * Retrieves PLU item.
     * 
     * @param value PLU item
     */
    public void setPLUItem(PLUItemIfc value)
    {
        pluItem = value;

    }

    /**
     * Retrieves PLU item array.
     * 
     * @return PLUItemIfc[]
     */
    public PLUItemIfc[] getPLUItems()
    {
        ArrayList<PLUItemIfc> items = null;
        if (returnSaleLineItems != null)
        {
            items = new ArrayList<PLUItemIfc>();
            for (int i = 0; i < returnSaleLineItems.length; i++)
            {
                if (returnSaleLineItems[i] != null)
                {
                    items.add(returnSaleLineItems[i].getPLUItem());
                }
            }
        }

        // copy data to array
        PLUItemIfc[] itemList = null;
        if (items != null)
        {
            itemList = new PLUItemIfc[items.size()];
            items.toArray(itemList);
        }

        return itemList;
    }

    /**
     * Returns the item's Size code
     * 
     * @return The item's Size code
     */
    public String getItemSizeCode()
    {
        String sizeCode = null;
        SaleReturnLineItemIfc currentLineItem = getSaleLineItem();

        if (currentLineItem != null)
        {
            sizeCode = currentLineItem.getItemSizeCode();
        }
        else
        {
            sizeCode = super.getItemSizeCode();
        }

        return sizeCode;
    }

    /**
     * Sets the item's Size code
     * 
     * @param code item's Size code
     */
    public void setItemSizeCode(String code)
    {
        SaleReturnLineItemIfc currentLineItem = getSaleLineItem();
        if (currentLineItem != null)
        {
            currentLineItem.setItemSizeCode(code);
        }
        else
        {
            super.setItemSizeCode(code);
        }
    }

    /**
     * Sets the PLU item ID.
     * 
     * @param value the PLU item ID.
     */
    public void setPLUItemID(String value)
    {
        PLUItemID = value;
    }

    /**
     * Gets the PLU item ID.
     * 
     * @return the PLU item ID.
     */
    public String getPLUItemID()
    {
        return (PLUItemID);
    }

    /**
     * Gets the return item.
     * 
     * @return the return item.
     */
    public ReturnItemIfc getReturnItem()
    {
        ReturnItemIfc theItem = null;

        if (returnItems != null && currentItem > -1 && currentItem < returnItems.length)
        {
            theItem = returnItems[currentItem];
        }

        return theItem;
    }

    /**
     * Sets the current return items. The selectedItems is the array or integers
     * that points to the selected items out the array of sale line items.
     * 
     * @param value
     */
    public void setReturnItem(ReturnItemIfc value)
    {
        returnItems[currentItem] = value;
    }

    /**
     * Sets the transfer cargo flag.
     * 
     * @param value the transfer cargo flag.
     */
    public void setTransferCargo(boolean value)
    {
        transferCargo = value;
    }

    /**
     * Gets the transfer cargo flag.
     * 
     * @return the transfer cargo flag.
     */
    public boolean getTransferCargo()
    {
        return (transferCargo);
    }

    /**
     * Returns the returnData.
     * 
     * @return ReturnData
     */
    public ReturnData getReturnData()
    {
        return returnData;
    }

    /**
     * Sets the returnData.
     * 
     * @param value the return data
     */
    public void setReturnData(ReturnData value)
    {
        returnData = value;
    }

    /**
     * Returns the array of sale items to be returned.
     * 
     * @return SaleReturnLineItemIfc[]
     */
    public SaleReturnLineItemIfc[] getReturnSaleLineItems()
    {
        return returnSaleLineItems;
    }

    /**
     * Sets the array of sale items to be returned.
     * 
     * @param value
     */
    public void setReturnSaleLineItems(SaleReturnLineItemIfc[] value)
    {
        returnSaleLineItems = value;
    }

    /**
     * Sets the item selected.
     * 
     * @param value the item
     */
    public void setSaleLineItem(SaleReturnLineItemIfc value)
    {
        if (returnSaleLineItems != null)
        {
            returnSaleLineItems[currentItem] = value;
        }
    }

    /**
     * Sets the array of sale items to be returned.
     * 
     * @param value return item
     */
    public void addReturnSaleLineItem(SaleReturnLineItemIfc value)
    {
        if (returnSaleLineItems == null)
        {
            returnSaleLineItems = new SaleReturnLineItemIfc[1];
            returnSaleLineItems[0] = value;
        }
        else
        {
            SaleReturnLineItemIfc[] result = new SaleReturnLineItemIfc[returnSaleLineItems.length + 1];
            System.arraycopy(returnSaleLineItems, 0, result, 0, returnSaleLineItems.length);
            result[returnSaleLineItems.length] = value;
            returnSaleLineItems = result;

            // Add return item to list

        }
    }

    /**
     * Sets the array of sale items to be returned.
     * 
     * @param value return item
     */
    public void removeReturnSaleLineItem(SaleReturnLineItemIfc value)
    {
        if (returnSaleLineItems == null)
        {
            return;
        }

        SaleReturnLineItemIfc[] result = new SaleReturnLineItemIfc[returnSaleLineItems.length -1];
        int resultIndex = 0;
        for (int i = 0; i < returnSaleLineItems.length; i++)
        {
            SaleReturnLineItemIfc selectedItem = returnSaleLineItems[i];
            // we do not want to use equals() here.  We are looking for object
            // instance inequality, not data inequality
            if (selectedItem != null && selectedItem != value)
            {
                result[resultIndex] = selectedItem;
                resultIndex++;
            }
        }
        if ( result.length >0)
        {
          returnSaleLineItems = result;
          // set current item to last one in list
          setCurrentItem(returnItems.length-1);

        }
        else
        {
            returnSaleLineItems = null;
            // set current item to no selection
            setCurrentItem(-1);
        }
    }

    /**
     * Sets the array of return items.
     * 
     * @param value return items
     */
    public void setReturnItems(ReturnItemIfc[] value)
    {
        returnItems = value;
    }

    /**
     * Returns the array of return items.
     * 
     * @return ReturnItemIfc[]
     */
    public ReturnItemIfc[] getReturnItems()
    {
        return returnItems;
    }

    /**
     * Add return item to list
     * 
     * @param value
     */
    public void addReturnItem(ReturnItemIfc value)
    {
        value.setFromGiftReceipt(this.isGiftReceiptSelected());
        if (haveReceipt() || isGiftReceiptSelected())
        {
            value.setFromGiftReceipt(isGiftReceiptSelected());
            value.setHaveReceipt(haveReceipt());
            TransactionIDIfc orgTranID = getOriginalTransactionId();
            value.setOriginalTransactionID(orgTranID);
            
            if (getOriginalTransaction() != null)
            {
                // Set the original transaction  business date, this is used in receipt printing
                value.setOriginalTransactionBusinessDate(orgTranID.getBusinessDate());
                value.setFromRetrievedTransaction(true);
            }
        }
        if (returnItems == null)
        {
            returnItems = new ReturnItemIfc[1];
            returnItems[0] = value;
            currentItem = 0;
        }
        else
        {
            ReturnItemIfc[] result = new ReturnItemIfc[returnItems.length + 1];
            System.arraycopy(returnItems, 0, result, 0, returnItems.length);
            result[returnItems.length] = value;
            returnItems = result;
        }
    }

    /**
     * Remove return item from list
     * 
     * @param value
     */
    public void removeReturnItem(ReturnItemIfc value)
    {
        // Sanity #1.
        if (returnItems == null  || returnItems.length == 0)
        {
            return;
        }

        // Sanity #2.
        ReturnItemIfc[] result = new ReturnItemIfc[returnItems.length - 1];
        if (result.length == 0)
        {
            returnItems = null;
            // set current item to no selection
            setCurrentItem(-1);
            return;
        }

        // Sanity #3.
        if (value == null)
        {
            return;
        }

        // Do the actual work.
        int resultIndex = 0;
        for (int i = 0; i < returnItems.length; i++)
        {
            ReturnItemIfc selectedItem = returnItems[i];
            // we do not want to use equals() here.  We are looking for object
            // instance inequality, not data inequality
            if (selectedItem != null && selectedItem != value)
            {
                result[resultIndex] = selectedItem;
                resultIndex++;
            }
        }

        returnItems = result;
        // set current item to last one in list
        setCurrentItem(returnItems.length-1);
    }

    /**
     * Returns the department name.
     * 
     * @return The department name.
     */
    public String getDepartmentName()
    {
        return department;
    }

    /**
     * Sets the department name.
     * 
     * @param dept The department name.
     */
    public void setDepartmentName(String dept)
    {
        department = dept;
    }

    /**
     * Returns the department id for items not found.
     * 
     * @return The department id
     */
    public String getDepartmentID()
    {
        return (departmentID);
    }

    /**
     * Sets the department id for items not found.
     * 
     * @param deptID The department id
     */
    public void setDepartmentID(String deptID)
    {
        departmentID = deptID;
    }

    /**
     * Some classes have some clean up to do.
     */
    public void completeItemNotFound(BusIfc bus)
    {
        // Not implemented
    }

    /**
     * Creates a snapshot of the cargo
     * 
     * @return A snapshot of the cargo
     */
    public SnapshotIfc makeSnapshot()
    {
        SnapshotIfc snapshot = new QuarrySnapshot(this);
        return (snapshot);
    }

    /**
     * Restores the cargo from a snapshot
     * 
     * @param s The snapshot of the cargo to restore from.
     * @throws ObjectRestoreException
     */
    public void restoreSnapshot(SnapshotIfc s) throws ObjectRestoreException
    {
        // Get a copy of the cargo from the snapshot
        QuarrySnapshot snapshot;
        snapshot = (QuarrySnapshot) s;

        try
        {
            ReturnItemCargo cargo = (ReturnItemCargo) snapshot.restoreObject();

            //Copy elements back. Developer must implement this.
            setPLUItem(cargo.getPLUItem());
            setReturnItem(cargo.getReturnItem());
            setSalesAssociateID(cargo.getSalesAssociateID());
        }
        catch (ObjectRestoreException e)
        {
            logger.error("Can't restore snapshot.", e);
            throw e;
        }
    }

    /**
     * Sets flag that inicates if the TaxRatesUnavailable dialog has been
     * displayed
     * 
     * @see oracle.retail.stores.pos.services.returns.returncommon.ReturnItemCargoIfc#setDisplayedTaxRatesUnavailableDialog(boolean)
     * @param value flag to indicate if this dialog has already been displayed
     */
    public void setDisplayedTaxRatesUnavailableDialog(boolean value)
    {
        displayedTaxRatesUnavailableDialog = value;
    }

    /**
     * Gets flag that inicates if the TaxRatesUnavailable dialog has been
     * displayed
     * 
     * @return boolean flag to indicate if this dialog has already been
     *         displayed
     * @see oracle.retail.stores.pos.services.returns.returncommon.ReturnItemCargoIfc#isDisplayedTaxRatesUnavailableDialog()
     */
    public boolean isDisplayedTaxRatesUnavailableDialog()
    {
        return displayedTaxRatesUnavailableDialog;
    }

    /**
     * Returns a string representation of this object.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        // result string
        String strResult = new String("Class:  ReturnItemCargo (Revision " + getRevisionNumber() + ")" + hashCode());

        // pass back result
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * Returns the selected item.
     * 
     * @return the item, or <code>null</code> if there is no current item.
     * @see #getCurrentItem()
     */
    public SaleReturnLineItemIfc getSaleLineItem()
    {
        SaleReturnLineItemIfc theItem = null;

        // I need several things to return the current SaleReturnLineItem:
        //   -- a set of items
        //   -- the 'currentItem' to be somewhere in the range of the set of items
        if ( returnSaleLineItems != null && currentItem > -1 && currentItem < returnSaleLineItems.length)
        {
            theItem = returnSaleLineItems[currentItem];
        }

        return theItem;
    }

    /**
     * @return Returns the geoCode.
     */
    public String getGeoCode()
    {
        return geoCode;
    }
    /**
     * @param value The geoCode to set.
     */
    public void setGeoCode(String value)
    {
        geoCode = value;
    }

    /**
     * Get the store ID
     * @return store ID
     */
    public String getStoreID()
    {
        if (storeID==null)
        {
            // Get StoreID from application.properties file
            storeID = Gateway.getProperty("application", "StoreID", "");
        }
        return storeID;
    }

    /**
     * Set the store ID
     * @param value the store ID
     */
    public void setStoreID(String value)
    {
        storeID = value;
    }

    /**
     * Returns the current item's serial number.
     * If there is not a current item, returns the serial number being held in this cargo.
     *
     * @return The serial number for the current item.  Might return <code>null</code>.
     * @see #getCurrentItem()
     */
    public String getItemSerial()
    {
        String serial = null;
        SaleReturnLineItemIfc sli = getSaleLineItem();
        if (sli != null)
        {
            serial = sli.getItemSerial();
        }
        else
        {
            serial = super.getItemSerial();
        }
        return serial;
    }

    /**
     * Checks if Enable Cancel for ItemNotFound
     * 
     * @return boolean true if cancel to be enabled
     */
    public boolean isEnableCancelItemNotFoundFromReturns()
    {
        return enableCancelItemNotFoundFromReturns;
    }

    /**
     * Enable Cancel for ItemNotFound
     * 
     * @param enableCancel The enableCancel to set.
     */
    public void setEnableCancelItemNotFoundFromReturns(boolean enableCancel)
    {
        this.enableCancelItemNotFoundFromReturns = enableCancel;
    }

    /**
     * @return Returns the lastLineItemReturnedIndex.
     */
    public int getLastLineItemReturnedIndex()
    {
        return lastLineItemReturnedIndex;
    }

    /**
     * @param lastLineItemReturnedIndex The lastLineItemReturnedIndex to set.
     */
    public void setLastLineItemReturnedIndex(int lastLineItemReturnedIndex)
    {
        this.lastLineItemReturnedIndex = lastLineItemReturnedIndex;
    }
    
    /*
     * (non-Javadoc)
     * @see
     * oracle.retail.stores.pos.services.common.PLUCargoIfc#getPLUItemForSizePrompt
     * ()
     */
    public PLUItemIfc getPLUItemForSizePrompt()
    {
        return pluItem;
    }
    
    /**
     * @return the exitAfterItemAddition
     */
    public boolean isExitAfterItemAddition()
    {
        return exitAfterItemAddition;
    }

    /**
     * @param exitAfterItemAddition the exitAfterItemAddition to set
     */
    public void setExitAfterItemAddition(boolean exitAfterItemAddition)
    {
        this.exitAfterItemAddition = exitAfterItemAddition;
    }

    /**
     * @return the exitAfterItemDeletion
     */
    public boolean isExitAfterItemDeletion()
    {
        return exitAfterItemDeletion;
    }

    /**
     * @param exitAfterItemDeletion the exitAfterItemDeletion to set
     */
    public void setExitAfterItemDeletion(boolean exitAfterItemDeletion)
    {
        this.exitAfterItemDeletion = exitAfterItemDeletion;
    }

    /**
     * @return if returnItemsAdded
     */
    public boolean areReturnItemsAdded()
    {
        return returnItemsAdded;
    }

    /**
     * @param returnItemsAdded the returnItemsAdded to set
     */
    public void returnItemsAdded(boolean returnItemsAdded)
    {
        this.returnItemsAdded = returnItemsAdded;
    }

    /**
     * @return the itemLookupLocaction
     */
    public ItemLookupType getItemLookupLocaction()
    {
        return itemLookupLocaction;
    }

    /**
     * @param itemLookupLocaction the itemLookupLocaction to set
     */
    public void setItemLookupLocaction(ItemLookupType itemLookupLocaction)
    {
        this.itemLookupLocaction = itemLookupLocaction;
    }
}
