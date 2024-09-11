/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/StoreLineItemModel.java /main/1 2012/06/21 12:42:41 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     06/07/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.ui.beans;

import java.io.Serializable;

import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;

//---------------------------------------------------------------------
/**
        This class is used to setup the table model for a JTable
        @return String representation of object
**/
//---------------------------------------------------------------------
public class StoreLineItemModel implements Serializable
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:49; $EKW;";

    /**
        constant for class name
    **/
    public static final String CLASSNAME = "StoreLineItemModel";

    /** The store number associated with the inventory  */
    protected StoreIfc store = null;

	/** The store number associated with the inventory  */
    protected String storeID = null;

    /** The store name associated with the inventory  */
    protected String storeName = null;

    /** The address associated with the store  */
    protected AddressIfc address = null;

    /** The phone number associated with the store  */
    protected PhoneIfc storePhone = null;

   //---------------------------------------------------------------------
   /**
        Constructor for class.
   **/
   //---------------------------------------------------------------------
    public StoreLineItemModel()
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
    
    
    public StoreIfc getStore() {
		return store;
	}

	public void setStore(StoreIfc store) {
		this.store = store;
	}
 }
