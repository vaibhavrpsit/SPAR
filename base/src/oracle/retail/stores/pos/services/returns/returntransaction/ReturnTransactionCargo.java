/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/ReturnTransactionCargo.java /main/22 2014/06/03 13:25:29 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  05/09/14 - Added flag to exit the tour after adding blind
 *                         return item
 *    abananan  02/11/14 - Added function to check if an item is present in the
 *                         itemsToDisplayList
 *    rabhawsa  07/30/13 - pluitem for different size should be identified by
 *                         size.
 *    rgour     10/16/12 - CBR fix if item is not available in current store
 *    rrkohli   05/11/11 - exception fixed when search is through UPC number in
 *                         return
 *    jkoppolu  11/04/10 - DEFECT#965, Serial number is longer matched with the
 *                         item id to get the matching items list.
 *    jswan     09/14/10 - Modified to support verification that serial number
 *                         entered by operator are contained in the external
 *                         order.
 *    jswan     08/20/10 - Provide a more descriptive error message where an
 *                         item is not found in the retrieved transaction.
 *    jswan     07/14/10 - Modifications to support pressing the escape key in
 *                         the EnterItemInformation screen during retrieved
 *                         transaction screen for external order integration.
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/26/10 - Fixed warning messages.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    nkgautam  03/22/10 - Added check to handle imei/serial number entry to
 *                         find the items in the return transactions
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/4/2008 3:12:27 AM    Sujay Beesnalli
 *         Forward porting CR# 30354 from v12x. Added flags to determine
 *         highlighting of rows.
 *    4    360Commerce 1.3         4/25/2007 8:52:14 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:29:46 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:54 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:56 PM  Robert Pearse
 *
 *   Revision 1.24  2004/08/09 19:22:17  jriggins
 *   @scr 6652 Added logic to also check POS item ID when retrieving items for return
 *
 *   Revision 1.23  2004/03/25 15:07:16  baa
 *   @scr 3561 returns bug fixes
 *
 *   Revision 1.22  2004/03/22 22:39:46  epd
 *   @scr 3561 Refactored cargo to get rid of itemQuantities attribute.  Added it to ReturnItemIfc instead.  Refactored to reduce code complexity and confusion.
 *
 *   Revision 1.21  2004/03/22 06:17:49  baa
 *   @scr 3561 Changes for handling deleting return items
 *
 *   Revision 1.20  2004/03/19 19:13:57  epd
 *   @scr 3561 fixed non-returnable items staying selected
 *
 *   Revision 1.19  2004/03/18 23:01:56  baa
 *   @scr 3561 returns fixes for gift card
 *
 *   Revision 1.18  2004/03/08 22:54:55  epd
 *   @scr 3561 Updates for entering detailed return item info
 *
 *   Revision 1.17  2004/03/05 21:46:58  epd
 *   @scr 3561 Updates to implement select highest price item
 *
 *   Revision 1.16  2004/03/05 16:01:17  epd
 *   @scr 3561 code reformatting and slight refactoring
 *
 *   Revision 1.15  2004/03/04 20:52:46  epd
 *   @scr 3561 Returns.  Updates for highest price item functionality and code cleanup
 *
 *   Revision 1.14  2004/03/04 14:55:51  baa
 *   @scr 3561 return add flow to check for returnable items
 *
 *   Revision 1.13  2004/03/03 22:31:23  epd
 *   @scr 3561 Returns updates - select highest price item
 *
 *   Revision 1.12  2004/03/01 22:50:46  epd
 *   @scr 3561 Updates for returns - tax related
 *
 *   Revision 1.11  2004/02/27 19:51:16  baa
 *   @scr 3561 Return enhancements
 *
 *   Revision 1.10  2004/02/25 15:20:30  baa
 *   @scr 3561 Allow for selected items on blind return to be highlighted on the transaction detail screen
 *
 *   Revision 1.9  2004/02/24 22:08:14  baa
 *   @scr 3561 continue returns dev
 *
 *   Revision 1.8  2004/02/23 13:54:52  baa
 *   @scr 3561 Return Enhancements to support item size
 *
 *   Revision 1.7  2004/02/19 15:37:27  baa
 *   @scr 3561 returns
 *
 *   Revision 1.6  2004/02/16 13:36:33  baa
 *   @scr 3561 returns enhancements
 *
 *   Revision 1.5  2004/02/13 14:02:48  baa
 *   @scr 3561 returns enhancements
 *
 *   Revision 1.4  2004/02/12 20:41:41  baa
 *   @scr 0 fixjavadoc
 *
 *   Revision 1.3  2004/02/12 16:51:53  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:30  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.4   Feb 09 2004 12:45:24   baa
 * move transaction not found road to return options
 *
 *    Rev 1.3   05 Feb 2004 23:31:44   baa
 * return multiple items
 *
 *    Rev 1.2   26 Jan 2004 00:57:58   baa
 * return development
 *
 *    Rev 1.1   26 Jan 2004 00:14:02   baa
 * continue return development
 *
 *    Rev 1.0   Aug 29 2003 16:06:28   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:04:34   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:46:46   msg
 * Initial revision.
 *
 *    Rev 1.2   10 Mar 2002 11:48:14   pjf
 * Maintain kit inventory at header level.
 * Resolution for POS SCR-1444: Selling then returning a kit does not upadate the inventory count
 * Resolution for POS SCR-1503: When all kit items are returned and attempt to retrieve trans no error displays
 *
 *    Rev 1.1   Feb 05 2002 16:43:26   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.0   Sep 21 2001 11:25:36   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returntransaction;

// java imports
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderItemIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ItemIndexContainer;
import oracle.retail.stores.pos.services.returns.returncommon.ItemIndexContainerComparator;
import oracle.retail.stores.pos.services.returns.returnitem.ReturnItemCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    Cargo for the Transaction Return service.
**/
//--------------------------------------------------------------------------
public class ReturnTransactionCargo extends ReturnItemCargo
{
    /** serialVersionUID */
    private static final long serialVersionUID = -536366081242402651L;

    // Item not found in transaction message ids
    public final static String SERIAL_ITEM_NOT_IN_TRANS         = "SERIAL_ITEM_NOT_IN_TRANS";
    public final static String ITEM_NOT_IN_TRANS                = "ITEM_NOT_IN_TRANS";
    public final static String SIZE_SERIAL_ITEM_NOT_IN_TRANS    = "SIZE_SERIAL_ITEM_NOT_IN_TRANS";
    public final static String SIZE_ITEM_NOT_IN_TRANS           = "SIZE_ITEM_NOT_IN_TRANS";
    public final static String SERIAL_ITEM_NOT_IN_EX_ORDER      = "SERIAL_ITEM_NOT_IN_EX_ORDER";
    public final static String INVALID_RETURN                   = "InvalidReturnItems";
    
    // Item not found in transaction message types
    public final static int SERIAL_ITEM_NOT_IN_TRANS_MSG        = 0;
    public final static int ITEM_NOT_IN_TRANS_MSG               = 1;
    public final static int SIZE_SERIAL_ITEM_NOT_IN_TRANS_MSG   = 2;
    public final static int SIZE_ITEM_NOT_IN_TRANS_MSG          = 3;
    public final static int SERIAL_ITEM_NOT_IN_EX_ORDER_MSG     = 4;
    public final static int INVALID_RETURN_MSG                   =5;
    /**
        The sale return line item selected from the list.  These are always
        Used to populate the return item info screen.
    **/
    protected SaleReturnLineItemIfc[] originalSaleLineItems = null;

    /**
        Item serial number
    **/
    protected String serialNumber = null;
    /**
        Kit Header items
    **/
    protected ArrayList<KitHeaderLineItemIfc> kitHeaderItems = new ArrayList<KitHeaderLineItemIfc>();

    /**
     *  lisf of Items to display
     */
    protected List<SaleReturnLineItemIfc> itemsToDisplay;

    /**
     *  List of items not selected  to be returned
     */
    protected List<SaleReturnLineItemIfc> itemsNotDisplayed = null;

    /**
     *  Flag that tells if the transaction is displayed or not
     */
    protected boolean transactionDetailsDisplayed = false;

    /**
     *  List of original transaction items
     */
    protected LinkedList<SaleReturnLineItemIfc> origItems;

    /**
     *  current selected item index
     */
    protected int selectedItemIndex;

    /**
     *  list of selected indexes
     */
    protected int[] selectedIndexes;

    /**
     * flag indicating we are done entering return items
     */
    protected boolean doneSelectingDetailItems = false;

    /**
     * flag indicating whether the items needs to be highlighted or not
     */
    protected boolean highlightItem = true;

    /**
     * flag indicating whether it is the fresh visit to transaction detail screen
     */
    protected boolean transDetailFreshVisit = true;
    
    /**
     * flag indicating whether the service needs to be exited after adding a blind return item
     */
    protected boolean exitAfterItemAddition = false;

    //----------------------------------------------------------------------
    /**
        Class Constructor.
        <p>
        Returns Transaction Cargo
    **/
    //----------------------------------------------------------------------
    public ReturnTransactionCargo()
    {
    }

    //----------------------------------------------------------------------
    /**
        Returns the transaction. <P>
        @return The transaction
    **/
    //----------------------------------------------------------------------
    public SaleReturnTransactionIfc getTransaction()
    {
        return transaction;
    }



    //----------------------------------------------------------------------
    /**
        Gets the sales associate for the item selected.
        <P>
        @return EmployeeIfc
    **/
    //----------------------------------------------------------------------
    public EmployeeIfc getSaleLineItemSalesAssociate()
    {
        return (returnSaleLineItems[currentItem].getSalesAssociate());
    }

    //----------------------------------------------------------------------
    /**
        Gets the PLUItem from the return array.
        <P>
        @return PLUItem.
    **/
    //----------------------------------------------------------------------
    public PLUItemIfc getReturnPLUItem()
    {
        return returnSaleLineItems[currentItem].getPLUItem();
    }

    //---------------------------------------------------------------------
    /**
        Retrieves PLU item. <P>
        @return PLU item
    **/
    //---------------------------------------------------------------------
    public PLUItemIfc getPLUItem()
    {
        PLUItemIfc pluItem = null;
        if (currentItem > -1 && returnSaleLineItems != null)
        {
            pluItem = returnSaleLineItems[currentItem].getPLUItem();
        }

        if (pluItem == null && !Util.isEmpty(getPLUItemID()))    
        {
            List<SaleReturnLineItemIfc> matchedItemList = 
                getMatchingItemsFromItemsNotDisplayed(getPLUItemID());
            if (matchedItemList.size() > 0)
            {
                pluItem = matchedItemList.get(0).getPLUItem();
            }
        }
        
        return pluItem;
    }

    //---------------------------------------------------------------------
    /**
        Retrieves PLU item. <P>
        @param item
    **/
    //---------------------------------------------------------------------
    public void setPLUItem(PLUItemIfc item)
    {
        returnSaleLineItems[currentItem].setPLUItem(item);
    }

    //---------------------------------------------------------------------
    /**
        Retrieves item serial number. <P>
        @return String
    **/
    //---------------------------------------------------------------------
    public String getItemSerial()
    {
        return returnSaleLineItems[currentItem].getItemSerial();
    }

    //---------------------------------------------------------------------
    /**
        Sets the item serial number. <P>
        @param sn serial number
    **/
    //---------------------------------------------------------------------
    public void setItemSerial(String sn)
    {
        returnSaleLineItems[currentItem].setItemSerial(sn);
    }

    //----------------------------------------------------------------------
    /**
        Returns the array of sale items.
        <P>
        @return SaleReturnLineItemIfc[]
    **/
    //----------------------------------------------------------------------
    public SaleReturnLineItemIfc[] getOriginalSaleLineItems()
    {
        return originalSaleLineItems;
    }

    //----------------------------------------------------------------------
    /**
        Sets the array of sale items.
        <P>
        @param  value
    **/
    //----------------------------------------------------------------------
    public void setOriginalSaleLineItems(SaleReturnLineItemIfc[] value)
    {
        originalSaleLineItems = value;
        if (originalSaleLineItems != null)
        {
            origItems = new LinkedList<SaleReturnLineItemIfc>();
            for (int i = 0; i < originalSaleLineItems.length; i++)
            {
                origItems.add(originalSaleLineItems[i]);
            }
        }
    }

     //----------------------------------------------------------------------
    /**
        Returns the price. <P>
        @return CurrencyIfc
    **/
    //----------------------------------------------------------------------
    public CurrencyIfc getPrice()
    {
        return (returnSaleLineItems[currentItem].getItemPrice().getSellingPrice());
    }

    //----------------------------------------------------------------------
    /**
        Sets the price. <P>
        @param price
    **/
    //----------------------------------------------------------------------
    public void setPrice(CurrencyIfc price)
    {
        returnSaleLineItems[currentItem].getItemPrice().setSellingPrice(price);
    }




    //----------------------------------------------------------------------
    /**
        Returns the item's Size code
        <P>
        @return The  item's Size code
    **/
    //----------------------------------------------------------------------
    public String getItemSizeCode()
    {
        return returnSaleLineItems[currentItem].getItemSizeCode();
    }

    //----------------------------------------------------------------------
    /**
        Sets the item's Size code
        <P>
        @param  code item's Size code
    **/
    //----------------------------------------------------------------------
    public void setItemSizeCode(String code)
    {
        returnSaleLineItems[currentItem].setItemSizeCode(code);
    }
    //----------------------------------------------------------------------
    /**
        Gets the ItemTax object for the return item.
        <P>
        @return ItemTax.
    **/
    //----------------------------------------------------------------------
    public ItemTaxIfc getItemTax()
    { // external tax mgr
        return (returnSaleLineItems[currentItem].getItemPrice().getItemTax());
    }
    //----------------------------------------------------------------------
    /**
        Sets the vector of kit header items.
        <P>
        @param value a vector of kit items
    **/
    //----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void setKitHeaderItems(Collection value)
    {
        kitHeaderItems = new ArrayList<KitHeaderLineItemIfc>();
        for(Object header: value)
        {
            kitHeaderItems.add((KitHeaderLineItemIfc)header);
        }
    }

    //----------------------------------------------------------------------
    /**
        Returns the vector of kit header items.
        <P>
        @return Vector
    **/
    //----------------------------------------------------------------------
    public Collection<KitHeaderLineItemIfc> getKitHeaderItems()
    {
        return kitHeaderItems;
    }

    //----------------------------------------------------------------------
    /**
    Returns the array of sale items to display.
    <P>
    @return SaleReturnLineItemIfc[]
    **/
    //----------------------------------------------------------------------
    public SaleReturnLineItemIfc[] getLineItemsToDisplay()
    {

        if (itemsToDisplay == null)
            return null;
        else
            return (SaleReturnLineItemIfc[]) itemsToDisplay.toArray(new SaleReturnLineItemIfc[0]);
    }

    //----------------------------------------------------------------------
    /**
    Returns the array list of sale items to display.  This will return empty
    list if null.
    <P>
    @return List
    **/
    //----------------------------------------------------------------------
    public List<SaleReturnLineItemIfc> getLineItemsToDisplayList()
    {
        return itemsToDisplay;
    }

    //----------------------------------------------------------------------
    /**
    Returns the array list of sale items to display.  This will return empty
    list if null.
    @param items list of items
    **/
    //----------------------------------------------------------------------
    public void setLineItemsToDisplayList(List<SaleReturnLineItemIfc> items)
    {
        itemsToDisplay = items;
    }

    //----------------------------------------------------------------------
    /**
      Sets the array of sale items to display.
      <P>
      @param  value array of line items
      **/
    //----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void setLineItemsToDisplay(SaleReturnLineItemIfc[] value)
    {
        if (value != null)
        {
            itemsToDisplay = new LinkedList<SaleReturnLineItemIfc>(Arrays.asList(value));
            itemsNotDisplayed.removeAll(itemsToDisplay);
        }
        else
        {
            itemsToDisplay = null;
            itemsNotDisplayed = (List<SaleReturnLineItemIfc>)origItems.clone();
        }
    }

    //----------------------------------------------------------------------
    /**
      Sets the array of sale items to display.
      <P>
      @param  value line item
      **/
    //----------------------------------------------------------------------
    public void addLineItemToDisplay(SaleReturnLineItemIfc value)
    {
        if (itemsToDisplay == null)
            itemsToDisplay = new LinkedList<SaleReturnLineItemIfc>();
        itemsToDisplay.add(value);
        itemsNotDisplayed.remove(value);
    }

    //----------------------------------------------------------------------
    /**
      Sets the array of sale items to display.
      <P>
      @param  value line item
      **/
    //----------------------------------------------------------------------
    public void removeLineItemToDisplay(SaleReturnLineItemIfc value)
    {
        if (itemsToDisplay != null && itemsToDisplay.contains(value))
        {
           itemsToDisplay.remove(value);
           itemsNotDisplayed.add(value);
        }
    }
    //----------------------------------------------------------------------
    /**
     * Creates a list of items matching from the original items
     * that match the supplied item number and size code
     * @param itemNum
     * @param sizeCode
     * @return List of ItemIndexContainer objects.
     */
    //----------------------------------------------------------------------
    protected List<ItemIndexContainer> createSortedListOfMatchingItems(String itemNum, String sizeCode)
    {

        List<ItemIndexContainer> matchedItemList = new LinkedList<ItemIndexContainer>();
        if (origItems != null)
        {
            SaleReturnLineItemIfc item;
            // first find all matching items
            for (int i = 0; i < origItems.size(); ++i)
            {
                item = (SaleReturnLineItemIfc) origItems.get(i);
                boolean matchFound = false;
                // consider size info only if required
                if (item.getPLUItem().isItemSizeRequired())
                {
                    matchFound = item.getItemID().equalsIgnoreCase(itemNum) && item.getItemSizeCode().equalsIgnoreCase(sizeCode);
                }
                else
                {
                    matchFound = item.getItemID().equalsIgnoreCase(itemNum);
                }

                if (matchFound)
                {
                    ItemIndexContainer itemIndexContainer = new ItemIndexContainer();
                    itemIndexContainer.setItem(item);
                    itemIndexContainer.setIndex(i);
                    matchedItemList.add(itemIndexContainer);
                }
            }
        }

        // sort the list based on final selling price
        Collections.sort(matchedItemList, new ItemIndexContainerComparator());

        return matchedItemList;
    }

    //----------------------------------------------------------------------
    /**
     * Creates a list of items matching from the original items
     * that match the supplied item number and size code
     * @param itemNum
     * @return List of ItemIndexContainer objects.
     */
    //----------------------------------------------------------------------
    protected List<ItemIndexContainer> createSortedListOfMatchingItems(String itemNum)
    {

        List<ItemIndexContainer> matchedItemList = new LinkedList<ItemIndexContainer>();
        if (origItems != null)
        {
            SaleReturnLineItemIfc item;
            // first find all matching items
            for (int i = 0; i < origItems.size(); ++i)
            {
                item = (SaleReturnLineItemIfc) origItems.get(i);
                if (item.getItemID().equalsIgnoreCase(itemNum))
                {
                    ItemIndexContainer itemIndexContainer = new ItemIndexContainer();
                    itemIndexContainer.setItem(item);
                    itemIndexContainer.setIndex(i);
                    matchedItemList.add(itemIndexContainer);
                }
            }
        }

        // sort the list based on final selling price
        Collections.sort(matchedItemList, new ItemIndexContainerComparator());

        return matchedItemList;
    }

    //----------------------------------------------------------------------
    /**
     * Creates a list of items matching from the original items
     * that match the supplied item number and size code
     * @param itemNum
     * @return List of ItemIndexContainer objects.
     */
    //----------------------------------------------------------------------
    protected List<ItemIndexContainer> getListOfMatchingSerialNumberItems(
            List<ItemIndexContainer> containers, String serialNumber)
    {
        List<ItemIndexContainer> matchedItemList = new LinkedList<ItemIndexContainer>();
        for (ItemIndexContainer container: containers)
        {
            if (serialNumber.equals(container.getItem().getItemSerial()))
            {
                matchedItemList.add(container);
            }
        }

        return matchedItemList;
    }

    //----------------------------------------------------------------------
    /**
     * mark items selected on original transaction
     * @param selectedItem
     */
    //----------------------------------------------------------------------
    public void markItemSelected(SaleReturnLineItemIfc selectedItem)
    {
        if (origItems != null)
        {
            int size = origItems.size();
            SaleReturnLineItemIfc item;
            for (int i = 0; i < size; ++i)
            {
                item = (SaleReturnLineItemIfc) origItems.get(i);
                if (item.equals(selectedItem))
                {
                    addSelectedItemIndex(i);
                    break;
                }
            }
        }
    }
    //----------------------------------------------------------------------
    /**
     * Checks is the item number is in the current transaction
     * @param itemNum
     * @return
     */
    //----------------------------------------------------------------------
    public PLUItemIfc getItemFromTransaction(String itemNum)
    {
        PLUItemIfc pluItem = null;
        if (origItems != null)
        {
            int size = origItems.size();
            SaleReturnLineItemIfc item;

            for (int i = 0; i < size; ++i)
            {
                item = (SaleReturnLineItemIfc) origItems.get(i);
                if (item.getItemID().equalsIgnoreCase(itemNum) || itemNum.equalsIgnoreCase(item.getPosItemID()))
                {
                    pluItem = item.getPLUItem();
                    break;
                }
            }
        }
        return pluItem;
    }

    /**
     * Gets matching Sale Return Line Items from the list of items not yet displayed.
     * Both the itemID and the POS item ID are checked.
     * @param itemID
     * @return
     */
    public List<SaleReturnLineItemIfc> getMatchingItemsFromItemsNotDisplayed(String itemID)
    {
        List<SaleReturnLineItemIfc> notDisplayedList = getItemsNotDisplayed();
        Iterator<SaleReturnLineItemIfc> iter = notDisplayedList.iterator();
        List<SaleReturnLineItemIfc> matchedItemList = new LinkedList<SaleReturnLineItemIfc>();
        while (iter.hasNext())
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) iter.next();
            if (srli.getPLUItem().getItemID().equalsIgnoreCase(itemID) ||
                    itemID.equalsIgnoreCase(srli.getPosItemID()))
            {
                matchedItemList.add(srli);
            }
        }
        return matchedItemList;
    }

    /**
     * Finds all matchine Sale Return line items from the list of items not yet displayed
     * @param itemID
     * @param sizeCode
     * @return
     */
    public List<SaleReturnLineItemIfc> getMatchingItemsFromItemsNotDisplayed(String itemID, String sizeCode)
    {
        List<SaleReturnLineItemIfc> notDisplayedList = getItemsNotDisplayed();
        Iterator<SaleReturnLineItemIfc> iter = notDisplayedList.iterator();
        List<SaleReturnLineItemIfc> matchingItemList = new LinkedList<SaleReturnLineItemIfc>();
        while (iter.hasNext())
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) iter.next();
            if (srli.getPLUItem().getItemID().equalsIgnoreCase(itemID) &&
                srli.getItemSizeCode().equalsIgnoreCase(sizeCode))
            {
                matchingItemList.add(srli);
            }

        }
        return matchingItemList;
    }

    /**
     * Finds all matchine Sale Return line items from the list of items not yet displayed
     * @param itemID
     * @param sizeCode
     * @return
     */
    public List<SaleReturnLineItemIfc> getMatchingSerialNumberItems(
            List<SaleReturnLineItemIfc> items, String serialNumber)
    {
        List<SaleReturnLineItemIfc> matchingItemList = new LinkedList<SaleReturnLineItemIfc>();
        for (SaleReturnLineItemIfc srli: items)
        {
            if (srli.getItemSerial().equalsIgnoreCase(serialNumber))
            {
                matchingItemList.add(srli);
            }
        }
        return matchingItemList;
    }
    
    

    //----------------------------------------------------------------------
    /**
     * Checks if transactions details are displayed
     * @return
     */
    //----------------------------------------------------------------------
    public boolean areTransactionDetailsDisplayed()
    {
        return transactionDetailsDisplayed;
    }

    //----------------------------------------------------------------------
    /**
     * Set boolean to determine if the transaction is displayed or not
     * @param value
     */
    //----------------------------------------------------------------------
    public void setTransactionDetailsDisplayed(boolean value)
    {
        transactionDetailsDisplayed = value;
    }

    //----------------------------------------------------------------------
    /**
     * Get list of items not displayed
     * @return
     */
    //----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public List<SaleReturnLineItemIfc> getItemsNotDisplayed()
    {
        if (itemsNotDisplayed == null)
        {
            itemsNotDisplayed = (List<SaleReturnLineItemIfc>)origItems.clone();
        }
        return itemsNotDisplayed;
    }

    //----------------------------------------------------------------------
    /**
     Returns the original transaction. <P>
     @return The transaction
     **/
    //----------------------------------------------------------------------
    public SaleReturnTransactionIfc getOriginalTransaction()
    {
        return originalTransaction;
    }

    //----------------------------------------------------------------------
    /**
     Sets the orginal transaction. <P>
     @param transaction The transaction
     **/
    //----------------------------------------------------------------------
    public void setOriginalTransaction(SaleReturnTransactionIfc transaction)
    {
        originalTransaction = transaction;
    }
    //----------------------------------------------------------------------
    /**
     * Gets the index of the currently selected item
     * @return
     */
    //----------------------------------------------------------------------
    public int getSelectedItemIndex()
    {
        return selectedItemIndex;
    }

    //----------------------------------------------------------------------
    /**
     * Sets the current selected item index
     * @param value
     */
    //----------------------------------------------------------------------
    public void setSelectedItemIndex(int value)
    {
        selectedItemIndex = value;
    }

    //----------------------------------------------------------------------
    /**
     * Add the index of a selected item
     * @param value
     */
    //----------------------------------------------------------------------
    public void addSelectedItemIndex(int value)
    {
        if (selectedIndexes == null)
        {
            selectedIndexes = new int[1];
            selectedIndexes[0] = value;
        }
        else
        {
            int[] result = new int[selectedIndexes.length + 1];
            System.arraycopy(selectedIndexes, 0, result, 0, selectedIndexes.length);
            result[selectedIndexes.length] = value;
            selectedIndexes = result;
        }
    }

    /**
     * Removes the specified value from the list of selected items.
     * This method does some boundary checking.
     * @param value The item to remove
     */
    public void removeSelectedItemIndex(int value)
    {
        if (selectedIndexes == null ||
            selectedIndexes.length == 0)
        {
            return;
        }

        int[] result = new int[selectedIndexes.length-1];
        int resultIndex = 0;
        for (int i = 0; i < selectedIndexes.length; i++)
        {
            int selectedIndex = selectedIndexes[i];
            if (selectedIndex != value)
            {
                result[resultIndex++] = selectedIndex;
            }
        }
        selectedIndexes = result;
    }

    //----------------------------------------------------------------------
    /**
     * Get list of indexes of selected items
     * @return
     */
    //----------------------------------------------------------------------
    public int[] getSelectedIndexes()
    {
        return selectedIndexes;
    }

    //----------------------------------------------------------------------
    /**
     * Sets the list of selected indexes
     * @param values
     */
    //----------------------------------------------------------------------
    public void setSelectedIndexes(int[] values)
    {
        selectedIndexes = values;
    }

    //----------------------------------------------------------------------
    /**
     Returns the selected item.
     <P>
     @param index the selected index
     @return SaleReturnLineItemIfc the line item
     **/
    //----------------------------------------------------------------------
    public SaleReturnLineItemIfc getSelectedSaleLineItem(int index)
    {
        return (SaleReturnLineItemIfc) origItems.get(index);
    }
    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object.
        <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    { // begin toString()
        // result string
        String strResult =
            new String("Class:  ReturnTransactionCargo (Revision " + getRevisionNumber() + ")" + hashCode());
        return (strResult);
    } // end toString()

    /**
     * @return Returns the doneSelectingDetailItems.
     */
    public boolean isDoneSelectingDetailItems()
    {
        return doneSelectingDetailItems;
    }
    /**
     * @param doneSelectingDetailItems The doneSelectingDetailItems to set.
     */
    public void setDoneSelectingDetailItems(boolean doneSelectingDetailItems)
    {
        this.doneSelectingDetailItems = doneSelectingDetailItems;
    }

    //----------------------------------------------------------------------
    /**
     * @return Returns the highlightItem.
     */
    //----------------------------------------------------------------------
	public boolean isHighlightItem()
	{
		return highlightItem;
	}

    //----------------------------------------------------------------------
    /**
     * Sets the highlightItem flag
     * @param highlightItem
     */
    //----------------------------------------------------------------------
	public void setHighlightItem(boolean highlightItem)
	{
		this.highlightItem = highlightItem;
	}

	//----------------------------------------------------------------------
    /**
     * @return Returns the transDetailFreshVisit.
     */
	//----------------------------------------------------------------------
	public boolean isTransDetailFreshVisit()
	{
		return transDetailFreshVisit;
	}

    //----------------------------------------------------------------------
    /**
     * Sets the transDetailFreshVisit flag
     * @param transDetailFreshVisit
     */
    //----------------------------------------------------------------------
	public void setTransDetailFreshVisit(boolean transDetailFreshVisit)
	{
		this.transDetailFreshVisit = transDetailFreshVisit;
	}

	/**
     * Sets the transDetailFreshVisit flag
     * @param transDetailFreshVisit
     */
    //----------------------------------------------------------------------
    public boolean isSerialNumberRequired(List<SaleReturnLineItemIfc> matchedItemList)
    {
        boolean required = false;
        for(SaleReturnLineItemIfc srli: matchedItemList)
        {
            if (!Util.isEmpty(srli.getItemSerial()))
            {
                required = true;
            }
        }
        
        return required;
    }
    // ----------------------------------------------------------------------
    /**
       This method sets up the cargo to process the next item in the list. If this
       is an external order return, marks the external order as returned.
     * <P>
     * 
     * @param bus Service Bus
     **/
    // ----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void completeReturnProcess()
    {
        ReturnItemIfc[] returnItems = getReturnItems();
        SaleReturnLineItemIfc[] returned = getReturnSaleLineItems();
        Vector original = getOriginalTransaction().getLineItemsVector();
        
        // Update the Original Transaction line items with the number of items returned.
        for(int i = 0; i < returnItems.length; i++)
        {
            // make sure that the element in the returned list is still valid
            // gift card items that are not returnable get null out
            if ( returnItems[i] != null)
            {
                int index = returned[i].getLineNumber();
                SaleReturnLineItemIfc originalItem = getOriginalLineItem(original,index);
                BigDecimal oldQuantity = originalItem.getQuantityReturnedDecimal();
                BigDecimal addQuantity = returnItems[i].getItemQuantity();
                originalItem.setQuantityReturned(oldQuantity.add(addQuantity));
                if (isExternalOrder())
                {
                    ExternalOrderItemIfc eoi = returned[i].getPLUItem().getReturnExternalOrderItem();
                    returned[i].setExternalOrderItemID(eoi.getId());
                    returned[i].setExternalOrderParentItemID(eoi.getParentId());
                }
            }
        }
    }
    
    /*
     * Find the line item in the original transaction which has the matching
     * line item number.
     */
    @SuppressWarnings("unchecked")
    private SaleReturnLineItemIfc getOriginalLineItem(Vector original, int index)
    {
        SaleReturnLineItemIfc originalItem = null;
        
        for(int i=0; i< original.size();i++)
        {
            originalItem = (SaleReturnLineItemIfc)original.elementAt(i);
            if(originalItem.getLineNumber() == index)
                break;
            
        }
        return originalItem;
        
    }
    
    public SaleReturnLineItemIfc getSaleLineItem()
    {
    	SaleReturnLineItemIfc tempItem = null;  	
    	SaleReturnLineItemIfc returnItem = null;
        
        if ( originalSaleLineItems != null && currentItem > -1 && currentItem < originalSaleLineItems.length)
        {
        	tempItem  = originalSaleLineItems[currentItem];
        }        
        returnItem=super.getSaleLineItem();        
        if(tempItem!=null && tempItem.getPLUItem().isAvailableInCurrentStore()==false)
        {
        	returnItem.getPLUItem().setAvailableInCurrentStore(false);        	
        }  
        
        return returnItem;    	
    }
    
    /**
     * Display the appropriate error message.
     * @param msgType
     * @param ui
     * @param itemID
     * @param itemSizeCode
     * @param itemSerialNumber
     */
    public DialogBeanModel buildItemNotFoundDialogModel(int msgType,
            String itemID, String itemSizeCode, String itemSerialNumber)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Retry");
        String[] args = null;

        switch(msgType)
        {
            case INVALID_RETURN_MSG:
                args = new String[1];
                args[0] = itemID;
                dialogModel.setResourceID(INVALID_RETURN);
                break;
                
            case SERIAL_ITEM_NOT_IN_TRANS_MSG:
                args = new String[2];
                args[0] = itemID;
                args[1] = itemSerialNumber;
                dialogModel.setResourceID(SERIAL_ITEM_NOT_IN_TRANS);
                break;
                
            case SERIAL_ITEM_NOT_IN_EX_ORDER_MSG:
                args = new String[2];
                args[0] = itemID;
                args[1] = itemSerialNumber;
                dialogModel.setResourceID(SERIAL_ITEM_NOT_IN_EX_ORDER);
                break;
                
            case ITEM_NOT_IN_TRANS_MSG:
                args = new String[1];
                args[0] = itemID;
                dialogModel.setResourceID(ITEM_NOT_IN_TRANS);
                break;
                
            case SIZE_SERIAL_ITEM_NOT_IN_TRANS_MSG:
                args = new String[3];
                args[0] = itemID;
                args[1] = itemSizeCode;
                args[2] = itemSerialNumber;
                dialogModel.setResourceID(SIZE_SERIAL_ITEM_NOT_IN_TRANS);
                break;
                
            case SIZE_ITEM_NOT_IN_TRANS_MSG:
                args = new String[2];
                args[0] = itemID;
                args[1] = itemSizeCode;
                dialogModel.setResourceID(SIZE_ITEM_NOT_IN_TRANS);
                break;
        }

        dialogModel.setArgs(args);
        return dialogModel;
    }
    
    /*
     * (non-Javadoc)
     * @see
     * oracle.retail.stores.pos.services.returns.returnitem.ReturnItemCargo#
     * getPLUItemForSizePrompt()
     */
    public PLUItemIfc getPLUItemForSizePrompt()
    {
        PLUItemIfc pluItem = null;
        if (!Util.isEmpty(getPLUItemID()))
        {
            List<SaleReturnLineItemIfc> matchedItemList = getMatchingItemsFromItemsNotDisplayed(getPLUItemID());
            if (matchedItemList.size() > 0)
            {
                pluItem = matchedItemList.get(0).getPLUItem();
            }
        }
        return pluItem;
    }

    /**
     * check if an item is in the display list
     * 
     * @param itemID
     * @return
     */
    public boolean isItemInDisplayList(String itemID)
    {
        boolean inDisplayList = false;
        if (itemsToDisplay != null)
        {
            for (SaleReturnLineItemIfc srli : itemsToDisplay)
            {
                if (srli.getPLUItemID().equalsIgnoreCase(itemID)
                        || itemID.equalsIgnoreCase(srli.getPosItemID()))
                {
                    inDisplayList = true;
                    break;
                }
        } 
    }
        return inDisplayList;
    }

    /**
     * Returns true if the service needs to be exited after adding every return
     * item.
     * 
     * @return the exitAfterItemAddition
     */
    public boolean isExitAfterItemAddition()
    {
        return exitAfterItemAddition;
    }

    /**
     * Sets flag for exiting the service after adding every return item.
     * 
     * @param exitAfterItemAddition the exitAfterItemAddition to set
     */
    public void setExitAfterItemAddition(boolean exitAfterItemAddition)
    {
        this.exitAfterItemAddition = exitAfterItemAddition;
    }
}
