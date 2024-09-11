/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/relateditem/RelatedItemCargo.java /main/2 2013/02/15 10:23:11 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     02/14/13 - moving DOB from relateditemstation to iteminquiry
*                        station
* yiqzhao     09/26/12 - refactor related item to add cross sell, upsell and
*                        substitute, remove pick one and pick many
* yiqzhao     09/20/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.modifyitem.relateditem;

import java.util.HashMap;

import oracle.retail.stores.pos.services.sale.SaleCargo;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.RelatedItemGroupIfc;
import oracle.retail.stores.domain.stock.RelatedItemIfc;
import oracle.retail.stores.domain.utility.EYSDate;

//--------------------------------------------------------------------------
/**
     This cargo extends the sale cargo and contains the necessary information
     for completing the related item work.
     $Revision: /main/2 $
 **/
//--------------------------------------------------------------------------
public class RelatedItemCargo extends SaleCargo
{    
    // related items chosen from container
    RelatedItemIfc[] toBeAddRelatedItems;
    
    // current related item performing lookup on
    RelatedItemIfc relatedItem;
    
    // current related item group (auto, some, one)
    String relatedItemGroupName;    
    
    // primary item sequence number 
    int primaryItemSequenceNumber = 0;    
    
    // next related item number
    int nextRelatedItem = 0;
    
    // flag for adding AUTO related item
    boolean addAutoRelatedItem = false;
    
    /**
     * This date is used to store the dob of the customer for age restricted
     * items.
     */
    protected EYSDate restrictedDOB = null;


	//----------------------------------------------------------------------
    /**
         This method gets the primary item's sequence number.
         @return Returns the primaryItemSequenceNumber.
     **/
    //----------------------------------------------------------------------
    public int getPrimaryItemSequenceNumber()
    {
        return primaryItemSequenceNumber;
    }
    //----------------------------------------------------------------------
    /**
         This method sets the primary items sequence number.
         @param primaryItemSequenceNumber The primaryItemSequenceNumber to set.
     **/
    //----------------------------------------------------------------------
    public void setPrimaryItemSequenceNumber(int primaryItemSequenceNumber)
    {
        this.primaryItemSequenceNumber = primaryItemSequenceNumber;
    }
    //----------------------------------------------------------------------
    /**
         This method gets the related item container
         @return Returns the relatedItemContainer.
     **/
    //----------------------------------------------------------------------
    public HashMap<String, RelatedItemGroupIfc> getRelatedItemContainer()
    {
        return ((SaleReturnLineItemIfc)transaction.getLineItems()[primaryItemSequenceNumber]).getPLUItem().getRelatedItemContainer();
    }

    //----------------------------------------------------------------------
    /**
         This method gets the current related item.
         @return Returns the relatedItem.
     **/
    //----------------------------------------------------------------------
    public RelatedItemIfc getRelatedItem()
    {
        return relatedItem;
    }
    //----------------------------------------------------------------------
    /**
         This method sets the current related item.
         @param relatedItem The relatedItem to set.
     **/
    //----------------------------------------------------------------------
    public void setRelatedItem(RelatedItemIfc relatedItem)
    {
        this.relatedItem = relatedItem;
    }
    //----------------------------------------------------------------------
    /**
         This method gets the related item group type string
         @return Returns the relatedItemGroup.
     **/
    //----------------------------------------------------------------------
    public String getRelatedItemGroupName()
    {
        return relatedItemGroupName;
    }
    //----------------------------------------------------------------------
    /**
         This method sets the related item group type string.
         @param relatedItemGroup The relatedItemGroup to set.
     **/
    //----------------------------------------------------------------------
    public void setRelatedItemGroupName(String relatedItemGroupName)
    {
        this.relatedItemGroupName = relatedItemGroupName;
    }
    //----------------------------------------------------------------------
    /**
         This method gets the related Item array.
         @return Returns the relatedItems.
     **/
    //----------------------------------------------------------------------
    public RelatedItemIfc[] getToBeAddRelatedItems()
    {
        return toBeAddRelatedItems;
    }
    //----------------------------------------------------------------------
    /**
         This method sets the related items array.
         @param relatedItems The relatedItems to set.
     **/
    //----------------------------------------------------------------------
    public void setToBeAddRelatedItems(RelatedItemIfc[] relatedItems)
    {
        this.toBeAddRelatedItems = relatedItems;
    }
    //----------------------------------------------------------------------
    /**
         This method gets the next related item.
         @return Returns the nextRelatedItem.
     **/
    //----------------------------------------------------------------------
    public int getNextRelatedItem()
    {
        return nextRelatedItem;
    }
    //----------------------------------------------------------------------
    /**
         This sets the next related item.
         @param nextRelatedItem The nextRelatedItem to set.
     **/
    //----------------------------------------------------------------------
    public void setNextRelatedItem(int nextRelatedItem)
    {
        this.nextRelatedItem = nextRelatedItem;
    }

    /**
     * 
     * @return
     */
    public boolean isAddAutoRelatedItem() {
		return addAutoRelatedItem;
	}
    
    /**
     * 
     * @param addAutoRelatedItem
     */
	public void setAddAutoRelatedItem(boolean addAutoRelatedItem) {
		this.addAutoRelatedItem = addAutoRelatedItem;
	}
	
    /**
     * This method returns the restricted dob.
     *
     * @return Returns the restrictedDOB.
     */
    public EYSDate getRestrictedDOB()
    {
        return restrictedDOB;
    }

    /**
     * This method sets the restricted dob.
     *
     * @param restrictedDOB The restrictedDOB to set.
     */
    public void setRestrictedDOB(EYSDate restrictedDOB)
    {
        this.restrictedDOB = restrictedDOB;
    }

}
