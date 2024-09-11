/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/AbstractFindTransactionCargo.java /main/18 2014/07/17 15:09:42 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vineesin  11/19/14 - Added Business Date 
 *    yiqzhao   07/17/14 - Move same original transaction check to utility
 *                         class and make regular transaction and order
 *                         transaction call the same method.
 *    sgu       04/24/14 - update logic to get returnable quantity
 *    jswan     10/25/12 - Modified to support returns by order.
 *    jswan     09/21/10 - Fixed issues with pressing escape on serial number
 *                         entry screen while performing a transaction return.
 *    jswan     09/14/10 - Modified to support verification that serial number
 *                         entered by operator are contained in the external
 *                         order.
 *    sgu       08/03/10 - reject a partially used gift card
 *    jswan     07/16/10 - Modifications to support the escape/undo
 *                         functionality on the ReturnItemInformation screen in
 *                         the retrieved transaction context.
 *    jswan     07/14/10 - Modifications to support pressing the escape key in
 *                         the EnterItemInformation screen during retrieved
 *                         transaction screen for external order integration.
 *    jswan     07/07/10 - Code review changes and fixes for Cancel button in
 *                         External Order integration.
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     06/01/10 - Checked in for refresh to latest lable.
 *    jswan     05/27/10 - First pass changes to return item for external order
 *                         project.
 *    jswan     05/14/10 - ExternalOrder mods checkin for refresh to tip.
 *    jswan     05/12/10 - Modify cargos for external order items return.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:06 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:19:26 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:19 PM  Robert Pearse
 *
 *   Revision 1.8  2004/04/14 20:50:01  tfritz
 *   @scr 4367 - Renamed moveTransactionToOrigninal() method to moveTransactionToOriginal() method and added a call to setOriginalTransactionId() in this method.
 *
 *   Revision 1.7  2004/03/15 15:16:51  baa
 *   @scr 3561 refactor/clean item size code, search by tender changes
 *
 *   Revision 1.6  2004/02/17 20:40:28  baa
 *   @scr 3561 returns
 *
 *   Revision 1.5  2004/02/16 13:37:30  baa
 *   @scr  3561 returns enhancements
 *
 *   Revision 1.4  2004/02/12 20:41:40  baa
 *   @scr 0 fixjavadoc
 *
 *   Revision 1.3  2004/02/12 16:51:45  mcs
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
 *    Rev 1.3   05 Feb 2004 23:16:26   baa
 * returs - multi items
 *
 *    Rev 1.2   Dec 19 2003 13:22:26   baa
 * more return enhancements
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 *
 *    Rev 1.1   Dec 17 2003 11:19:56   baa
 * return enhancements
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 *
 *    Rev 1.0   Aug 29 2003 16:05:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:06:26   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:45:00   msg
 * Initial revision.
 *
 *    Rev 1.1   10 Dec 2001 12:28:42   jbp
 * Added getTransaction to ReturnTransactionCargoIfc and abstracted the functionalty to AbstractFindTransactionCargo
 * Resolution for POS SCR-418: Return Updates
 *
 *    Rev 1.0   Sep 21 2001 11:24:36   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncommon;

// foundation imports
import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.commerceservices.externalorder.ExternalOrderItemIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.returns.ReturnTenderDataElementIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.sale.SaleCargo;
import oracle.retail.stores.pos.utility.TransactionUtility;

import org.apache.commons.lang3.StringUtils;

//--------------------------------------------------------------------------
/**
    The base class for find transaction cargoes.
    <p>
    @version $Revision: /main/18 $
**/
//--------------------------------------------------------------------------
public abstract class AbstractFindTransactionCargo
extends AbstractFinancialCargo
implements ReturnExternalOrderItemsCargoIfc
{
    /** serialVersionUID */
    private static final long serialVersionUID = 8222661109725192873L;

    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /main/18 $";

    /**
        The original customer transaction
    **/
    protected SaleReturnTransactionIfc originalTransaction = null;

    /**
        This array contains a list of SaleReturnTransacions on which
        returns have been completed.
    **/
    protected SaleReturnTransactionIfc[] originalReturnTransactions;

    /**
        The current transaction from the POS service
    **/
    protected SaleReturnTransactionIfc transaction = null;

    /**
        The search criteria container
    **/
    protected SearchCriteriaIfc searchCriteria = null;

    /**
       Flag to note if the transaction was retrieved
    **/
    protected boolean isTransactionFound = false;

    /**
     * flag that indicates if items returned are from gift receipt
     */
    protected boolean giftReceiptSelected = false;

    /**
     *  flag that indicates if items were returned with a receipt
     */
    protected boolean haveReceipt = false;

    /**
     * flag that indicates if transaction was retrieved
     */
    protected boolean fromRetrievedTransaction = false;

    /**
     The original Transaction ID - entered by the user
     **/
    protected TransactionIDIfc originalTransactionId = null;

    /**
        The list of tenders for the original transaction
     **/
    protected ReturnTenderDataElementIfc[] originalTenders;

    /**
     *  set to true if a tender type was use for search
     */
    protected boolean isSearchByTender = false;

    /**
     * A list of external order items that will be processed for return
     */
    protected ArrayList<ExternalOrderItemReturnStatusElement> externalOrderItemReturnStatusElements = null;

    /**
     * The external order item associated with the current return item.
     */
    protected ExternalOrderItemReturnStatusElement currentExternalOrderItemReturnStatusElement = null;

    /**
     * If the calling service sets this value to true, the return tour
     * will process the list of externalOrderItems.  The calling service
     * must also set a value on externalOrderItems data member.
     */
    protected boolean externalOrder = false;

    /**
     * Holds the original return transactions that are generated by external orders.
     * Since an external order can lookup multiple transactions during a single
     * return session, it must be able to store multiple original transactions.
     */
    protected ArrayList<SaleReturnTransactionIfc> originalExternalOrderReturnTransactions = null;
    
    /**
     * The single transaction read by the application or selected by the operator
     * contains a order ID.  The order must be read and the read and the return
     * executed based on the order. 
     **/
    protected String selectedTransactionOrderID = null;
    
    /**
     * Original Business date of transaction 
     */
    protected EYSDate originalBusinessDate;

    //----------------------------------------------------------------------
    /**
        Class Constructor.
    **/
    //----------------------------------------------------------------------
    public AbstractFindTransactionCargo()
    {
    }

    //----------------------------------------------------------------------
    /**
        Returns the original transaction.
        @return The transaction
    **/
    //----------------------------------------------------------------------
    public SaleReturnTransactionIfc getOriginalTransaction()
    {
        return originalTransaction;
    }

    //----------------------------------------------------------------------
    /**
        Sets the orginal transaction.
        @param value The transaction
    **/
    //----------------------------------------------------------------------
    public void setOriginalTransaction(SaleReturnTransactionIfc value)
    {
        originalTransaction = value;
    }

    //----------------------------------------------------------------------
    /**
        Moves the found transaction to the original transaction;
        If the transaction is already in the original return transaction array,
        it uses a copy from the array.
        @ param transaction the found transaction
    **/
    //----------------------------------------------------------------------
    public void moveTransactionToOriginal(SaleReturnTransactionIfc transaction)
    {
        // Check to see it is already in the original array, if so use that one.
        if (originalReturnTransactions != null)
        {
            // Check to see if this transaction is already in the array.
            // if so, make a copy of the transaction in the array and use that.
            // Make clone in case the return is canceled.
            for (int i = 0; i < originalReturnTransactions.length; i++)
            {
                SaleReturnTransactionIfc temp = originalReturnTransactions[i];
                
                if (TransactionUtility.isOfSameOriginalReturnTransaction(temp, transaction))
                {
                    transaction = (SaleReturnTransactionIfc) temp.clone();
                    // Stop the loop.
                    i = originalReturnTransactions.length;
                }
            }
        }

        // Or it might also be in this list; if so use that one.
        if (isExternalOrder())
        {
            if (originalExternalOrderReturnTransactions != null)
            {
                for(SaleReturnTransactionIfc temp: originalExternalOrderReturnTransactions)
                {
                    if (TransactionUtility.isOfSameOriginalReturnTransaction(temp, transaction))
                    {
                        transaction = (SaleReturnTransactionIfc) temp.clone();
                        // Stop the loop.
                        break;
                    }
                }
            }
            addOriginalExternalOrderTransaction(transaction);
        }

        setOriginalTransaction(transaction);
        setOriginalTransactionId(transaction.getTransactionIdentifier());
        setSelectedTransactionOrderID(transaction.getOrderID());
    }

    //--------------------------------------------------------------------------
    /**
        Retrieve the array of transactions on which items have been returned.
        This cargo does not track this data.

        @return SaleReturnTransactionIfc[]
    **/
    //--------------------------------------------------------------------------
    public SaleReturnTransactionIfc[] getOriginalReturnTransactions()
    {
        return originalReturnTransactions;
    }

    //--------------------------------------------------------------------------
    /**
        Set the array of transactions on which items have been returned.
        This cargo does not track this data.

        @param value list of  transactions
    **/
    //--------------------------------------------------------------------------
    public void setOriginalReturnTransactions(SaleReturnTransactionIfc[] value)
    {
        originalReturnTransactions = value;
    }

    /**
     * Add a transaction to the array of transactions on which items have been returned.
     * @param transaction SaleReturnTransactionIfc
     */
    public void addOriginalReturnTransaction(SaleReturnTransactionIfc transaction)
    {
        ArrayList<SaleReturnTransactionIfc> srliList = new ArrayList<SaleReturnTransactionIfc>();
        if (originalReturnTransactions == null || originalReturnTransactions.length == 0)
        {
            srliList.add(transaction);
        }
        else
        {
            boolean transAdded = false;
            for(SaleReturnTransactionIfc transFromList: originalReturnTransactions)
            {
                // If the current transaction has the same is one being added, do
                // not add it to the list; newer version will be added later.
                if (TransactionUtility.isOfSameOriginalReturnTransaction(transFromList, transaction))
                {
                    srliList.add(transaction);
                    transAdded = true;
                }
                else
                {
                    srliList.add(transFromList);
                }
            }

            if (!transAdded)
            {
                srliList.add(transaction);
            }
        }

        // Convert the list to an array and set it on the data member.
        originalReturnTransactions = new SaleReturnTransactionIfc[srliList.size()];
        srliList.toArray(originalReturnTransactions);
    }

    //----------------------------------------------------------------------
    /**
        Returns the Current transaction. <P>
        @return SaleReturnTransactionIfc
    **/
    //----------------------------------------------------------------------
    public SaleReturnTransactionIfc getTransaction()
    {
        return transaction;
    }

    //----------------------------------------------------------------------
    /**
        Sets the Current transaction. <P>
        @param value  The transaction
    **/
    //----------------------------------------------------------------------
    public void setTransaction(SaleReturnTransactionIfc value)
    {
        transaction = value;
    }

    //----------------------------------------------------------------------
    /**
        Returns the search criteria to  retrieve transactions
        @return SearchCriteriaIfc  the search criteria
    **/
    //----------------------------------------------------------------------
    public SearchCriteriaIfc getSearchCriteria()
    {
        return searchCriteria;
    }

    //----------------------------------------------------------------------
    /**
        Sets the search criteria to  retrieve transactions
        @param criteria  the search criteria
    **/
    //----------------------------------------------------------------------
    public void setSearchCriteria(SearchCriteriaIfc criteria)
    {
        searchCriteria = criteria;
    }

    //----------------------------------------------------------------------
    /**
        Returns a flag that indicates if a transaction was retrieved
        @return boolean transactionfound flag
    **/
    //----------------------------------------------------------------------
    public boolean isTransactionFound()
    {
        return isTransactionFound;
    }

    //----------------------------------------------------------------------
    /**
        Sets transactionfound flag
        @param value transactionfound flag
    **/
    //----------------------------------------------------------------------
    public void setTransactionFound(boolean value)
    {
        isTransactionFound = value;
    }

    //----------------------------------------------------------------------
    /**
     Sets the return transaction id.
     <P>
     @param value the return transaction id.
     **/
    //----------------------------------------------------------------------
    public void setOriginalTransactionId(TransactionIDIfc value)
    {
        originalTransactionId = value;
    }

    //----------------------------------------------------------------------
    /**
     Gets the return transaction id.
     <P>
     @return String the return transaction id.
     **/
    //----------------------------------------------------------------------
    public TransactionIDIfc getOriginalTransactionId()
    {
        return (originalTransactionId);
    }

    //--------------------------------------------------------------------------
    /**
     * @param items
     */
    //--------------------------------------------------------------------------
    public void setOriginalTenders(ReturnTenderDataElementIfc[] items)
    {
        originalTenders = items;
    }

    /**
     * Return an array from originalTendersList.
     */
    public ReturnTenderDataElementIfc[] getOriginalTenders()
    {
        return originalTenders;
    }

    /**
     * Append the array of ReturnTenderDataElementIfc object to the list of
     * originalExternalOrderTenders
     * @param returnTenderDataElement
     */
    public void appendOriginalTenders(ReturnTenderDataElementIfc[] returnTenderDataElement)
    {
        ArrayList<ReturnTenderDataElementIfc> originalTendersList = new ArrayList<ReturnTenderDataElementIfc>();
        if (originalTenders != null)
        {
            for(ReturnTenderDataElementIfc originalTender: originalTenders)
            {
                originalTendersList.add(originalTender);
            }
        }

        if (returnTenderDataElement != null)
        {
            for(ReturnTenderDataElementIfc originalTender: returnTenderDataElement)
            {
                originalTendersList.add(originalTender);
            }
        }

        originalTenders = new ReturnTenderDataElementIfc[originalTendersList.size()];
        originalTendersList.toArray(originalTenders);
    }

    //----------------------------------------------------------------------
    /**
     * Sets flag to indicate if return items comes from  a receipt
     * @param value boolean flag
     */
    //----------------------------------------------------------------------
    public void setHaveReceipt(boolean value)
    {
        haveReceipt = value;
    }

    //----------------------------------------------------------------------
    /**
     * Indicates if return items comes from  gift receipt
     * @return boolean flag
     */
    //----------------------------------------------------------------------
    public boolean haveReceipt()
    {
        return haveReceipt;
    }

    //----------------------------------------------------------------------
    /**
     * sets the gift receipt flag.
     * @param value the gift receipt flag
     */
    //----------------------------------------------------------------------
    public void setGiftReceiptSelected(boolean value)
    {
        giftReceiptSelected = value;
    }

    //----------------------------------------------------------------------
    /**
     * Gets the gift receipt flag.
     * @return boolean the gift receipt flag
     */
    //----------------------------------------------------------------------
    public boolean isGiftReceiptSelected()
    {
        return (giftReceiptSelected);
    }
    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object.
        <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult =
            new String("Class:  AbstractFindTransactionCargo (Revision " + getRevisionNumber() + ")" + hashCode());
        return (strResult);
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }

    /**
     * Gets flag that indicates if search was done by tender
     * @return Returns the isSearchByTender.
     */
    public boolean isSearchByTender()
    {
        return isSearchByTender;
    }

    /**
     * Sets flag that indicates if search was done by tender
     * @param value The isSearchByTender to set.
     */
    public void setSearchByTender(boolean value)
    {
        isSearchByTender = value;
    }

    /**
     * @return Returns the externalOrderItems.
     */
    public ArrayList<ExternalOrderItemReturnStatusElement> getExternalOrderItemReturnStatusElements()
    {
        return externalOrderItemReturnStatusElements;
    }

    /**
     * This method should only be called by the launch shuttle that
     * starts up the returns process.
     * @param externalOrderItems The externalOrderItems to set.
     */
    public void setExternalOrderItemReturnStatusElements(ArrayList<ExternalOrderItemReturnStatusElement> externalOrderItems)
    {
        this.externalOrderItemReturnStatusElements = externalOrderItems;
    }

    /**
     * This method should only be called by the launch shuttle that
     * starts up the returns process.
     * @param externalOrderItems The externalOrderItems to set.
     */
    public void setExternalOrderItems(List<ExternalOrderItemIfc> externalOrderItems)
    {
        externalOrderItemReturnStatusElements = new ArrayList<ExternalOrderItemReturnStatusElement>();
        ExternalOrderItemReturnStatusElement statusElement;
        for(ExternalOrderItemIfc eoi: externalOrderItems)
        {
            statusElement = new ExternalOrderItemReturnStatusElement();
            statusElement.setExternalOrderItem((ExternalOrderItemIfc)eoi.clone());
            externalOrderItemReturnStatusElements.add(statusElement);
        }
    }

    /**
     * @return Returns the externalOrder.
     */
    public boolean isExternalOrder()
    {
        return externalOrder;
    }

    /**
     * @param externalOrder The externalOrder to set.
     */
    public void setExternalOrder(boolean externalOrder)
    {
        this.externalOrder = externalOrder;
    }

    /**
     * @return Returns the externalOrderItems.
     */
    public ExternalOrderItemReturnStatusElement getExternalOrderItemeReturnStatusElement(int index)
    {
        return externalOrderItemReturnStatusElements.get(index);
    }

    /**
     * @return Returns the currentExternalOrderItemReturnStatusElement.
     */
    public ExternalOrderItemReturnStatusElement getCurrentExternalOrderItemReturnStatusElement()
    {
        return currentExternalOrderItemReturnStatusElement;
    }

    /**
     * @param currentExternalOrderItemReturnStatusElement The currentExternalOrderItemReturnStatusElement to set.
     */
    public void setCurrentExternalOrderItemReturnStatusElement(
            ExternalOrderItemReturnStatusElement currentExternalOrderItemReturnStatusElement)
    {
        this.currentExternalOrderItemReturnStatusElement = currentExternalOrderItemReturnStatusElement;
    }

    /**
     * This method finds the external order item status element associated
     * with the external order item parameter and updates the returned status.
     * @param externalOrderItem
     * @param returned
     */
    public void setAssociatedExternalOrderItemReturnedStatus(ExternalOrderItemIfc externalOrderItem,
            boolean returned)
    {
        for(ExternalOrderItemReturnStatusElement elementStatus: externalOrderItemReturnStatusElements)
        {
            // We looking for identity here (i.e. is it the same external order item?);
            // as result the "equals()" method was purposely not used.
            if (externalOrderItem == elementStatus.getExternalOrderItem())
            {
                elementStatus.setReturned(returned);
            }
        }
    }

    /**
     * This method resets all elements in the list of ExternalOrderItemReturnStatusElement which
     * have not been returned to not select for return, so that they can be processes again.
     */
    public void resetExternalOrderItemsSelectForReturn()
    {
        if (externalOrderItemReturnStatusElements != null)
        {
            for(ExternalOrderItemReturnStatusElement elementStatus: externalOrderItemReturnStatusElements)
            {
                elementStatus.setSelected(elementStatus.isReturned());
                // The SerialNumberMatched boolean is a little more tricky than Selected;
                // It is necessarily true if the Returned boolean is true; however, it
                // it should be false if the Returned boolean is false.
                if (!elementStatus.isReturned() && elementStatus.isSerialNumberMatched())
                {
                    elementStatus.setSerialNumberMatched(false);
                }
            }
        }
    }

    /**
     * There is already a list of original transactions in this cargo; however,
     * that list is used to update the cargo from the calling tour, so that list
     * cannot be updated until we are sure its items will be included in the return
     * transaction.
     * @param transaction
     */
    public void addOriginalExternalOrderTransaction(SaleReturnTransactionIfc transaction)
    {
        // check to see if the list already exist; if not make one.
        if (originalExternalOrderReturnTransactions == null)
        {
            originalExternalOrderReturnTransactions = new ArrayList<SaleReturnTransactionIfc>();
        }
        else
        {
            // Check to see if this transaction is already in the array.
            // if so, remove the current reference.
            int size = originalExternalOrderReturnTransactions.size();
            for(int i = 0; i < size; i++)
            {
                SaleReturnTransactionIfc temp = originalExternalOrderReturnTransactions.get(i);
                if (TransactionUtility.isOfSameOriginalReturnTransaction(temp, transaction))
                {
                    originalExternalOrderReturnTransactions.remove(i);
                    // Stop the loop.
                    i = size;
                }
            }
        }

        // Now add the transaction from the parameter to the list.
        originalExternalOrderReturnTransactions.add(transaction);
    }

    /**
     * Get the list of original transaction for an external order.
     * @return originalExternalOrderReturnTransactions
     */
    public ArrayList<SaleReturnTransactionIfc> getOriginalExternalOrderReturnTransactions()
    {
        return originalExternalOrderReturnTransactions;
    }

    /**
     * Set the list of original transaction for an external order.
     * @param array list of SaleReturnTransactionIfc
     */
    public void setOriginalExternalOrderReturnTransactions(ArrayList<SaleReturnTransactionIfc>
        originalExternalOrderReturnTransactions)
    {
        this.originalExternalOrderReturnTransactions = originalExternalOrderReturnTransactions;
    }

    /**
     * @return the selectedTransactionOrderID
     */
    public String getSelectedTransactionOrderID()
    {
        return selectedTransactionOrderID;
    }

    /**
     * @param selectedTransactionOrderID the selectedTransactionOrderID to set
     */
    public void setSelectedTransactionOrderID(String selectedTransactionOrderID)
    {
        this.selectedTransactionOrderID = selectedTransactionOrderID;
    }

    /**
     * This method looks for a order item in the status list that contains a matching
     * PLUItemID and serial number. 
     * @param pluItemID
     * @param serialNumber
     * @return true if a match is found
     */
    public boolean matchSerialNumberToOrderItem(String pluItemID, String serialNumber)
    {
        boolean orderItemMatch = false;
        
        for(ExternalOrderItemReturnStatusElement elementStatus: externalOrderItemReturnStatusElements)
        {
            ExternalOrderItemIfc orderItem = elementStatus.getExternalOrderItem();
            if (orderItem.getPOSItemId().equals(pluItemID) &&
                    orderItem.getSerial().equals(serialNumber))
            {
                if (!elementStatus.isSerialNumberMatched())
                {
                    elementStatus.setSerialNumberMatched(true);
                    setCurrentExternalOrderItemReturnStatusElement(elementStatus);
                    orderItemMatch = true;
                }
                break;
            }
        }        
        
        return orderItemMatch;
    }
    
    /**
     * If the return is for an external order and the data member returnData
     * is not null, then combine the two return data objects.
     */
    public ReturnData addReturnData(ReturnData returnData1, ReturnData returnData2)
    {
        if (returnData1 == null && returnData2 == null)
        {
            return null;
        }
        if (returnData1 == null)
        {
            return returnData2;
        }
        if (returnData2 == null)
        {
            return returnData1;
        }

        // Initialize lists
        ArrayList<PLUItemIfc> pluList = new ArrayList<PLUItemIfc>();
        ArrayList<ReturnItemIfc> rList = new ArrayList<ReturnItemIfc>();
        ArrayList<SaleReturnLineItemIfc> srliList = new ArrayList<SaleReturnLineItemIfc>();

        // Copy the ReturnData objects to the lists
        addReturnDataToItemLists(returnData1, pluList, rList, srliList);
        addReturnDataToItemLists(returnData2, pluList, rList, srliList);

        // Set the lists on the ReturnData class member.
        PLUItemIfc[] pluItems = new PLUItemIfc[pluList.size()];
        ReturnItemIfc[] rItems = new ReturnItemIfc[rList.size()];
        SaleReturnLineItemIfc[] srlItems = new SaleReturnLineItemIfc[srliList.size()];
        pluList.toArray(pluItems);
        rList.toArray(rItems);
        srliList.toArray(srlItems);
        ReturnData returnData = new ReturnData();
        returnData.setPLUItems(pluItems);
        returnData.setReturnItems(rItems);
        returnData.setSaleReturnLineItems(srlItems);
        returnData.setOriginalTransaction(returnData.getOriginalTransaction());

        return returnData;
    }

    /*
     * Adds the data from the ReturnData to each of the ArrayList objects
     */
    private void addReturnDataToItemLists(ReturnData returnData, ArrayList<PLUItemIfc> pluList,
            ArrayList<ReturnItemIfc> rList, ArrayList<SaleReturnLineItemIfc> srliList)
    {
        PLUItemIfc[] pluItems = returnData.getPLUItems();
        ReturnItemIfc[] rItems = returnData.getReturnItems();
        SaleReturnLineItemIfc[] srlItems = returnData.getSaleReturnLineItems();

        for(int i = 0; i < srlItems.length; i++)
        {
            pluList.add(pluItems[i]);
            rList.add(rItems[i]);
            srliList.add(srlItems[i]);
        }
    }

    /**
     * Generates a ReturnData object from arrays of plu items, sale return line
     * items and return items.
     * @param pluItems
     * @param returnLineItems
     * @param returnItems
     * @return
     */
    public ReturnData buildReturnData(PLUItemIfc[] pluItems,
            SaleReturnLineItemIfc[] returnLineItems, ReturnItemIfc[] returnItems)
    {
        ReturnData returnData = new ReturnData();

        ArrayList<Integer> indexes = getInvalidReturnItemIndexes(returnItems);

        if (indexes.size() > 0)
        {
            ArrayList<Object> list = getReturnArray(returnItems, indexes);
            int size = list.size();
            ReturnItemIfc[] rItems = new ReturnItemIfc[size];
            list.toArray(rItems);
            returnData.setReturnItems(rItems);

            list = getReturnArray(pluItems, indexes);
            PLUItemIfc[] pItems = new PLUItemIfc[size];
            list.toArray(pItems);
            returnData.setPLUItems(pItems);

            list = getReturnArray(returnLineItems, indexes);
            SaleReturnLineItemIfc[] sItems = new SaleReturnLineItemIfc[size];
            list.toArray(sItems);
            returnData.setSaleReturnLineItems(sItems);
        }
        else
        {
            returnData.setReturnItems(returnItems);
            returnData.setPLUItems(pluItems);
            returnData.setSaleReturnLineItems(returnLineItems);
        }

        return returnData;
    }

    /*
     * Returns the indexes of return items that have not yet been filled in.
     */
    private ArrayList<Integer> getInvalidReturnItemIndexes(ReturnItemIfc[] returnItems)
    {
        ArrayList<Integer> indexes = new ArrayList<Integer>();

        for (int i = 0; i < returnItems.length; i++)
        {
            if (returnItems[i] == null)
            {
               indexes.add(new Integer(i));
            }
        }

        return indexes;
    }

    /*
     *  Removes from the array the objects with the specified indexes
     */
    private ArrayList<Object> getReturnArray(Object[] returnArray, ArrayList<Integer> indexes)
    {
      //Vector list = (Vector)Arrays.asList(returnArray);
        ArrayList<Object> list = new ArrayList<Object>();
        for (int i = 0; i < returnArray.length; i++)
        {
            list.add(returnArray[i]);
        }

        for (int i = indexes.size() - 1; i >= 0; i--)
        {
            list.remove(indexes.get(i).intValue());
        }

        return list;
    }

    /**
     * Returns Business Date of Transaction 
     * @return the businessDate
     */
    public EYSDate getOriginalBusinessDate()
    {
        return originalBusinessDate;
    }

    /**
     * Sets Business Date
     * 
     * @param businessDate the businessDate to set
     */
    public void setOriginalBusinessDate(EYSDate originalBusinessDate)
    {
        this.originalBusinessDate = originalBusinessDate;
    }
    

}
