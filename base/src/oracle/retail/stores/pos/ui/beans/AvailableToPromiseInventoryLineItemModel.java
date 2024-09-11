/* ===========================================================================
* Copyright (c) 2001, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/AvailableToPromiseInventoryLineItemModel.java /main/4 2013/03/01 09:01:43 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  02/28/13 - Fix to enable and disable pickup buttons on qty and
 *                         date availability
 *    sgu       01/11/13 - set xchannel order item flag based on if item is
 *                         avilable in the store inventory
 *    abhinavs  12/28/12 - Fix to enable and disable store pickup and print
 *                         buttons
 *    jswan     04/18/12 - Added to support cross channel create pickup order
 *                         feature.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.io.Serializable;
import java.math.BigDecimal;

import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;

//---------------------------------------------------------------------
/**
        This class is used to setup the table model for a JTable
        @return String representation of object
**/
//---------------------------------------------------------------------
public class AvailableToPromiseInventoryLineItemModel implements Serializable
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 2620316661492573364L;

    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:49; $EKW;";

    /**
        constant for class name
    **/
    public static final String CLASSNAME = "StoreInventoryLineItemModel";

    /** The store number associated with the inventory  */
    protected String storeID = null;

    /** The store name associated with the inventory  */
    protected String storeName = null;

    /** The address associated with the store  */
    protected AddressIfc address = null;

    /** The phone number associated with the store  */
    protected PhoneIfc storePhone = null;

    /** The inventory quantity associated with the store  */
    protected String quantityAvailable = null;

    /** The date available associated with the quantity  */
    protected String dateAvailable = null;
    
    /** The flag indicating if the item is available as of now */
    protected boolean isAvailableNow = false;
    
    /** The quantity required associated with the transaction */
    protected BigDecimal quantityRequired=null; 
    
    /** The flag indicating if the item is available in future */
    protected boolean isAvailableInFuture = false;

 
//---------------------------------------------------------------------
   /**
        Constructor for class.
   **/
   //---------------------------------------------------------------------
    public AvailableToPromiseInventoryLineItemModel()
    {
    }

    /**
     * @return the storeNumber
     */
    public String getStoreID()
    {
        return storeID;
    }
    
    /**
     * @param storeNumber the storeNumber to set
     */
    public void setStoreID(String storeNumber)
    {
        this.storeID = storeNumber;
    }
    
    /**
     * @return the storeName
     */
    public String getStoreName()
    {
        return storeName;
    }
    
    /**
     * @param storeName the storeName to set
     */
    public void setStoreName(String storeName)
    {
        this.storeName = storeName;
    }
    
    /**
     * @return the address
     */
    public AddressIfc getAddress()
    {
        return address;
    }
    
    /**
     * @param phoneIfc the address to set
     */
    public void setAddress(AddressIfc address)
    {
        this.address = address;
    }
    
    /**
     * @return the storePhone
     */
    public PhoneIfc getStorePhone()
    {
        return storePhone;
    }
    
    /**
     * @param storePhone the storePhone to set
     */
    public void setStorePhone(PhoneIfc storePhone)
    {
        this.storePhone = storePhone;
    }
    
    /**
     * @return the quantityAvailable
     */
    public String getQuantityAvailable()
    {
        return quantityAvailable;
    }
    
    /**
     * @param quantityAvailable the quantityAvailable to set
     */
    public void setQuantityAvailable(String quantityAvailable)
    {
        this.quantityAvailable = quantityAvailable;
    }
    
    /**
     * @return the dateAvailable
     */
    public String getDateAvailable()
    {
        return dateAvailable;
    }
    
    /**
     * @param dateAvailable the dateAvailable to set
     */
    public void setDateAvailable(String dateAvailable)
    {
        this.dateAvailable = dateAvailable;
    }
    
    /**
     * @return the isAvailableNow flag
     */
    public boolean isAvailableNow() 
    {
		return isAvailableNow;
	}

    /**
     * @param isAvailableNow the isAvailableNow flag to set
     */
	public void setAvailableNow(boolean isAvailableNow) 
	{
		this.isAvailableNow = isAvailableNow;
	}

	/**
     * @return the quantityRequired
     */
    public BigDecimal getQuantityRequired() {
    	return quantityRequired;
    }

    /**
     * @param quantityRequired the quantityRequired to set
     */
    public void setQuantityRequired(BigDecimal quantityRequired) {
    	this.quantityRequired = quantityRequired;
    }
    
    /**
     * @return the isAvailableInFuture flag
     */
    public boolean isAvailableInFuture() 
    {
    	return isAvailableInFuture;
    }

    /**
     * @param isAvailableInFuture the isAvailableInFuture flag to set
     */ 
    public void setAvailableInFuture(boolean isAvailableInFuture)
    {
    	this.isAvailableInFuture = isAvailableInFuture;
    }
 }
