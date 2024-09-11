/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreateshipping/XChannelShippingCargo.java /main/7 2013/05/16 14:08:36 mkutiana Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* mkutiana    05/16/13 - retaining the values of the ShippingBeanModel upon
*                        error on the SelectShippingMethodSite
* yiqzhao     05/01/13 - Save the reason code(id_lu_cd.LU_CD_ENT) rather than
*                        the description to retail price
*                        modifier(CO_MDFR_RTL_PRC.RC_MDFR_RT_PRC).
* yiqzhao     10/19/12 - Refactor to use DestinationTaxRule station to get line
*                        item tax from xchannel shipping destination postal
*                        code.
* yiqzhao     09/17/12 - fix the issue with multiple order delivery details for
*                        a given item group.
* yiqzhao     07/05/12 - Add ship item list on DisplayShippingMethod screen.
* yiqzhao     06/29/12 - handle mutiple shipping packages in one transaction
*                        while delete one or more shipping items
* yiqzhao     06/04/12 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.services.order.xchannelcreateshipping;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderDeliveryDetailIfc;
import oracle.retail.stores.domain.shipping.ShippingOptionIfc;
import oracle.retail.stores.domain.shipping.ShippingRequestIfc;
import oracle.retail.stores.domain.shipping.ShippingResponseIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.tax.TaxRulesVO;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;

import oracle.retail.stores.pos.services.sale.SaleCargo;
import oracle.retail.stores.pos.ui.beans.ShippingMethodBeanModel;

public class XChannelShippingCargo extends SaleCargo implements CargoIfc
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(XChannelShippingCargo.class);


    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision";

    /** The list of stores used to lookup available inventory. */
    protected StoreIfc[] storeGroup = null;

    /** The map of stores keyed by the sale return line item number. */
    protected StoreIfc store = null;
    
    /** Indicates if ship to customer or ship to store */
    protected boolean shipToCustomer = true;
    
    /** the store to ship to */
    protected StoreIfc storeToShip = null;

	/** the shipping option to be displayed in SelectShippingMethod screen */
    protected int currentOptionIndex = 0;
    
    /** customer linked at create shipping station */
    protected CustomerIfc customer = null;

    /** the shipping request send to the web service */
    protected ShippingRequestIfc request = null;

	/** the shipping response retrieved from the web service */
    protected ShippingResponseIfc response = null;
    
    /** shippingID/deliveryID in the transaction */
    protected int deliveryID = 0;

	/** shipping to customer delivery detail */
    protected OrderDeliveryDetailIfc deliveryDetail = null;
    
    /** save the brief customer info when doing ship to store */
    protected CaptureCustomerIfc captureCustomer = null;

    /** save the line items for selected delivery address */
    protected List<SaleReturnLineItemIfc> lineItemsForDelivery = new ArrayList<SaleReturnLineItemIfc>();
    
    /** destination tax rule found from shipping destination postal code */
    protected TaxRulesVO destinationTaxRule = null;
    
    protected CodeListIfc shippingChargeReasonCodes = null;
    
    /** Shipping Bean Model */
    protected ShippingMethodBeanModel shippingBeanModel = null;
	// ---------------------------------------------------------------------
    /**
     * Constructs InquiryOptionsCargo object.
     * <P>
     */
    // ---------------------------------------------------------------------
    public XChannelShippingCargo()
    {
        super();
    }

    /**
     * Sets the store group array
     * @param stores
     */
    public void setStoreGroup(StoreIfc[] stores)
    {
        this.storeGroup = stores;
    }

    /**
     * Gets the store group array
     * @return stores
     */
    public StoreIfc[] getStoreGroup()
    {
        return storeGroup;
    }

    /**
     * Gets itemAvailablityList
     * @return itemAvailablityList
     */
    public List<ShippingOptionIfc> getShippingOptionList()
    {
    	if ( response != null )
    		return response.getShippingOptions();
    	else
    		return null;
    }

    public StoreIfc getStore() {
		return store;
	}

	public void setStore(StoreIfc store) {
		this.store = store;
	}

	/**
     * get the current shipping option index
     * @return
     */
    public int getCurrentOptionIndex() {
		return currentOptionIndex;
	}

    /**
     * set the current shipping option index
     * @param currentOptionIndex
     */
	public void setCurrentOptionIndex(int currentOptionIndex) {
		this.currentOptionIndex = currentOptionIndex;
	}
	
	/**
	 * get shipping request
	 * @return
	 */
    public ShippingRequestIfc getRequest() {
		return request;
	}
    
    /**
     * set shippint request
     * @param request
     */
	public void setRequest(ShippingRequestIfc request) {
		this.request = request;
	}
    
	/**
	 * get shipping response
	 * @return
	 */
    public ShippingResponseIfc getResponse() {
		return response;
	}

    /**
     * set ShippingResponse
     * @param response
     */
	public void setResponse(ShippingResponseIfc response) {
		this.response = response;
	}

	/**
     * Returns a string representation of this object.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class:  XChannelShippingCargo (Revision " +
                                      getRevisionNumber() +
                                      ") @" + hashCode());
        return(strResult);
    }

    /**
     * set the linked customer
     * @param customer
     */
	public void setCustomer(CustomerIfc customer) {
		// TODO Auto-generated method stub
		this.customer = customer;
	}
	
	/**
	 * get the linked customer
	 * @return
	 */
	public CustomerIfc getCustomer() {
		// TODO Auto-generated method stub
		return customer;
	}
	
	/**
	 * get order delivery id from the transaction
	 * @return
	 */
    public int getDeliveryID() {
		return deliveryID;
	}

    /**
     * set order delivery id from the transaction
     * @param deliveryID
     */
	public void setDeliveryID(int deliveryID) {
		this.deliveryID = deliveryID;
	}

	/**
	 * get order delivery detail
	 * @return
	 */
	public OrderDeliveryDetailIfc getDeliveryDetail() {
		// TODO Auto-generated method stub
		return deliveryDetail;
	}
	
	/**
	 * set order delivery detail
	 * @param deliveryDetail
	 */
	public void setDeliveryDetail(OrderDeliveryDetailIfc deliveryDetail) {
		// TODO Auto-generated method stub
		this.deliveryDetail = deliveryDetail;
	}  
	
	/**
	 * 
	 * @return
	 */
    public boolean isShipToCustomer() {
		return shipToCustomer;
	}

	public void setShipToCustomer(boolean shipToCustomer) {
		this.shipToCustomer = shipToCustomer;
	}	
	
    
	public StoreIfc getStoreToShip() {
		return storeToShip;
	}

	public void setStoreToShip(StoreIfc storeToShip) {
		this.storeToShip = storeToShip;
	}
	
	public CaptureCustomerIfc getCaptureCustomer() {
		return captureCustomer;
	}

	public void setCaptureCustomer(CaptureCustomerIfc captureCustomer) {
		this.captureCustomer = captureCustomer;
	}
	
	public void addLineItemForDelivery(SaleReturnLineItemIfc srli)
	{
		lineItemsForDelivery.add(srli);
	}
	
	public List<SaleReturnLineItemIfc> getLineItemsForDelivery()
	{
		return lineItemsForDelivery;
	}
	
	public void removeAllLineItemsForDelivery()
	{
		lineItemsForDelivery.clear();
	}
	
	/**
	 * get shipping destination tax rule value object
	 * @return
	 */
    public TaxRulesVO getDestinationTaxRule() 
    {
		return destinationTaxRule;
	}

    /**
     * set destination tax rule value object
     * @param destinationTaxRule
     */
	public void setDestinationTaxRule(TaxRulesVO destinationTaxRule) 
	{
		this.destinationTaxRule = destinationTaxRule;
	}
	
	/**
	 * Set reason codes for modifying shipping charge
	 * @param codeList
	 */
	public void setShippingChargeReasonCodes(CodeListIfc codeList)
	{
	    this.shippingChargeReasonCodes = codeList;
	}
	
	/**
	 * Get reason codes for modifying shipping charge
	 * @return
	 */
	public CodeListIfc getShippingChargeReasonCodes()
	{
	    return this.shippingChargeReasonCodes;
	}

    /**
     * Returns the Shipping Bean Model
     * @return the savedShippingBeanModel
     */
    public ShippingMethodBeanModel getShippingBeanModel()
    {
        return shippingBeanModel;
    }

    /**
     * Saves the Shipping bean Model
     * @param savedShippingBeanModel the savedShippingBeanModel to set
     */
    public void setShippingBeanModel(ShippingMethodBeanModel shippingBeanModel)
    {
        this.shippingBeanModel = shippingBeanModel;
    }	
}
