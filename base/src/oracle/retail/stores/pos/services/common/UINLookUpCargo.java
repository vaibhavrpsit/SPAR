/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     11/03/14 - added UIN (SIM) lookup to the non-receipted returns flow.
 *    rrkohli   12/01/10 - added comments
 *    rrkohli   12/01/10 - fix to handle when same serial number is entered
 *                         more than once
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    nkgautam  01/28/10 - changed boolean checkForNotSellable default value to
 *                         false
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/15/09 - Cargo class for UIN lookup tour
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;

/**
 * Cargo class for UIN lookup tour
 * @author nkgautam
 */
@SuppressWarnings("serial")
public class UINLookUpCargo extends AbstractFinancialCargo implements CargoIfc
{

    /**
     * Validation Response Object
     */
    protected SerialValidationResponseIfc validationResponse;

    /**
     * boolean to check for not sellable response
     */
    protected boolean CheckForNotSellable = false;

    /**
     * List of Item IDs
     */
    protected ArrayList itemIDList ;

    /**
     * HashMap of PLU Item
     */
    HashMap<String,PLUItemIfc> pluItemsMap = null;

    /**
     * PLU Item
     */
    protected PLUItemIfc    pluItem   = null;

    /**
     * The item that we are looking for
     */
    protected SearchCriteriaIfc    inquiryItem   = null;

    protected RetailTransactionIfc transaction =null;
    
    protected boolean returnWhenItemNotFound = false;

    /**
     * Gets the Check for Not Sellable boolean
     * @return boolean
     */
    public boolean isCheckForNotSellable()
    {
        return CheckForNotSellable;
    }

    /**
     * Sets the Check for Not Sellable boolean
     * @param checkForNotSellable
     */
    public void setCheckForNotSellable(boolean checkForNotSellable)
    {
        CheckForNotSellable = checkForNotSellable;
    }

    /**
     * Gets the Validation Response
     * @return SerialValidationResponseIfc
     */
    public SerialValidationResponseIfc getValidationResponse()
    {
        return validationResponse;
    }

    /**
     * Sets the validation Response
     * @param validationResponse
     */
    public void setValidationResponse(SerialValidationResponseIfc validationResponse)
    {
        this.validationResponse = validationResponse;
    }

    public ArrayList getItemIDList()
    {
        return itemIDList;
    }

    public void setItemIDList(ArrayList itemIDList)
    {
        this.itemIDList = itemIDList;
    }

    /**
      Returns the item number.
      @return String item number.
     **/
    public SearchCriteriaIfc getInquiry()
    {
        return(inquiryItem);
    }

    /**
   Sets the search criteria.
   @param value  search certeria.
     **/
    public void  setInquiry(SearchCriteriaIfc inquiry)
    {
        inquiryItem = inquiry;
    }

    /**
     * Gets the PluItem Array from Hashmap.
     * @param pluItemsMap
     * @return PLUItemIfc[]
     */
    public PLUItemIfc[] getItemList(HashMap pluItemsMap)
    {
        PLUItemIfc[] items = new PLUItemIfc[pluItemsMap.size()];
        Collection<PLUItemIfc> tCollection = pluItemsMap.values();
        int i = 0;
        for (PLUItemIfc item : tCollection)
        {
            items[i++] = item;
        }
        return items;
    }

    /**
     * Gets the current PLU Item
     * @return
     */
    public PLUItemIfc getPluItem()
    {
        return pluItem;
    }

    /**
     * Sets the PLU Item
     * @param pluItem
     */
    public void setPluItem(PLUItemIfc pluItem)
    {
        this.pluItem = pluItem;
    }

    /**
     * Gets the hashmap of PLU Items
     * @return
     */
    public HashMap<String, PLUItemIfc> getPluItemsMap()
    {
        return pluItemsMap;
    }

    /**
     * Sets the hashmap of PLU Items
     * @param pluItemsMap
     */
    public void setPluItemsMap(HashMap<String, PLUItemIfc> pluItemsMap)
    {
        this.pluItemsMap = pluItemsMap;
    }
    
    /**
     * Sets the transaction
     * @param transaction
     */
    public void setTransaction(RetailTransactionIfc value)
    { 
      transaction  = value;
    } 

    /**
     * Gets the transaction
     * @param transaction
     */
    public RetailTransactionIfc getTransaction()
    {
      return transaction;
    }

    /**
     * @return the returnWhenItemNotFound
     */
    public boolean isReturnWhenItemNotFound()
    {
        return returnWhenItemNotFound;
    }

    /**
     * @param returnWhenItemNotFound the returnWhenItemNotFound to set
     */
    public void setReturnWhenItemNotFound(boolean returnWhenItemNotFound)
    {
        this.returnWhenItemNotFound = returnWhenItemNotFound;
    } 

}
