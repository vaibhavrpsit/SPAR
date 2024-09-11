/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *  Rev 1.2		May 04, 2017		Kritica Agarwal GST Changes
 *  Rev 1.1		Feb 17, 2017		Nadia Arora		Carry bags quantity going of the same as of weighted items
 *
 ********************************************************************************/
package max.retail.stores.pos.services.sale;

import java.math.BigDecimal;

import max.retail.stores.pos.services.inquiry.iteminquiry.MAXItemInquiryCargo;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.sale.ItemLookupLaunchShuttle;

public class MAXItemLookupLaunchShuttle extends ItemLookupLaunchShuttle {

	 /**
	 * 
	 */
	private static final long serialVersionUID = -4059732906843704584L;
	public void load(BusIfc bus)
	 {
		 super.load(bus);
	 }
	 public void unload(BusIfc bus)
	 {
	        // unload the financial cargo
	        super.unload(bus);

	        MAXItemInquiryCargo inquiryCargo = (MAXItemInquiryCargo) bus.getCargo();
	        /* Rev 1.1 changes starts*/
	        inquiryCargo.setItemQuantity(new BigDecimal(1));
	        /* Rev 1.1 changes ends*/
	        inquiryCargo.setRegister(saleCargo.getRegister());
	        inquiryCargo.setTransaction(saleCargo.getTransaction());
	        inquiryCargo.setModifiedFlag(true);
	        inquiryCargo.setStoreStatus(saleCargo.getStoreStatus());
	        inquiryCargo.setRegister(saleCargo.getRegister());
	        inquiryCargo.setOperator(saleCargo.getOperator());
	        inquiryCargo.setCustomerInfo(saleCargo.getCustomerInfo());
	        inquiryCargo.setTenderLimits(saleCargo.getTenderLimits());
	        inquiryCargo.setPLUItem(saleCargo.getPLUItem());
	        inquiryCargo.setSalesAssociate(saleCargo.getEmployee());
	        //Change for Rev 1.2 : Starts
	        inquiryCargo.setInterStateDelivery(((MAXSaleCargoIfc)saleCargo).getInterStateDelivery());
	        inquiryCargo.setFromRegion(((MAXSaleCargoIfc)saleCargo).getFromRegion());
	        inquiryCargo.setToRegion(((MAXSaleCargoIfc)saleCargo).getToRegion());
	      //Change for Rev 1.2 : Ends
	        inquiryCargo.setEmpID(((MAXSaleCargoIfc)saleCargo).getEmpID());
	       // System.out.println("MAXItemLookupLaunchShuttle 48 :"+inquiryCargo.getEmpID());
	       inquiryCargo.setLiqBarCode(((MAXSaleCargoIfc)saleCargo).getLiqBarCode());
	        
	        String geoCode = null;
	        if(saleCargo.getStoreStatus() != null &&
	                saleCargo.getStoreStatus().getStore() != null)
	        {
	            geoCode = saleCargo.getStoreStatus().getStore().getGeoCode();
	        }

	        inquiryCargo.setInquiry(saleCargo.getPLUItemID(), "", "", geoCode);
	        inquiryCargo.setIsRequestForItemLookup(true);
	        if(saleCargo instanceof MAXSaleCargo){
	        	inquiryCargo.setSingleBarCodeData(((MAXSaleCargoIfc)saleCargo).getSingleBarCodeData());
	        	inquiryCargo.setNecBarCode(((MAXSaleCargoIfc)saleCargo).getNecBarCode());
	        }
	 }
}
