/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved. 

 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/max/retail/stores/domain/arts/MAXHotKeysTransaction.java /main/32 2014/06/17 15:26:38 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * Rev 1.1	Sep 12,2022		Kamlesh Pant	CapLimit Enforcement for Liquor
 * Rev 1.0	Aug 26,2016		Nitesh Kumar	changes for code merging 
 * ===========================================================================
 */
package max.retail.stores.domain.arts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import max.retail.stores.domain.MaxLiquorDetails;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.pos.services.modifytransaction.discount.ModifyTransactionDiscountCargo;

public class MAXHotKeysTransaction extends DataTransaction implements DataTransactionIfc {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8303822409045831573L;

	public static String dataCommandName = "MAXHotKeysTransaction";

	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public MAXHotKeysTransaction() {
		super(dataCommandName);
	}

	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 * <P>
	 * 
	 * @param name
	 *            transaction name
	 **/
	// ---------------------------------------------------------------------
	public MAXHotKeysTransaction(String name) {
		super(name);
	}

	public String getItemIdFromHotKey(String hotKeyID) throws DataException {
		DataAction dataAction = new DataAction();
		dataAction.setDataObject(hotKeyID);
		dataAction.setDataOperationName("HotKeyItemIdLookup");

		DataActionIfc[] dataActions = new DataActionIfc[1];
		dataActions[0] = dataAction;

		setDataActions(dataActions);
		String itemId = (String) getDataManager().execute(this);

		return itemId; 

	}
	//Rev 1.1 Starts
	public MaxLiquorDetails getLiquorUMAndCategory(String itemid) throws DataException {
		DataAction dataAction = new DataAction();
		MaxLiquorDetails output =null;
		dataAction.setDataObject(itemid);
		dataAction.setDataOperationName("MAXJdbcLiquorUMAndCategoryLookup");

		DataActionIfc[] dataActions = new DataActionIfc[1];
		dataActions[0] = dataAction;

		setDataActions(dataActions);
		try{
		output =(MaxLiquorDetails) getDataManager().execute(this);
		//System.out.println("81 :"+output.toString());
		}
		catch(DataException e) {
	e.printStackTrace();
		}
		return output;

	}
	
	//changes by shyvanshu mehra...
	
	public BigDecimal getCustomerTransactionDetails(TenderableTransactionIfc trans) throws DataException {
		//System.out.println("Inside getCustomerTransactionDetails");
		DataAction dataAction = new DataAction();
		BigDecimal output =null;
		dataAction.setDataObject(trans);
		dataAction.setDataOperationName("ReadTransactionForCustomer");

		DataActionIfc[] dataActions = new DataActionIfc[1];
		dataActions[0] = dataAction;

		setDataActions(dataActions);
		try{
			//System.out.println("khgfdserdxtfcgyvh====== "+(BigDecimal) getDataManager().execute(this));
		output = (BigDecimal) getDataManager().execute(this);
		//System.out.println("81 :"+output.toString());
		}
		catch(DataException e) {
	e.printStackTrace();
		}
		return output;

	}

	
	public int getParkingCertTransactionDetails(TenderableTransactionIfc trans) throws DataException {
       // System.out.println("Inside getCustomerTransactionDetails");
        DataAction dataAction = new DataAction();
        //BigDecimal output =null;
        int output=0;
        dataAction.setDataObject(trans);
        dataAction.setDataOperationName("ReadParkingTransactionForCustomer");
        
        //System.out.println("ReadMallCrtfTransactionForCustomer");

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;
        
       // System.out.println(dataActions[0].getDataOperationName());
        
        setDataActions(dataActions);
        try{
        	//System.out.println("====================113 :: "+(String)getDataManager().execute(this));
         output = (int)getDataManager().execute(this);
        //System.out.println("81 :"+output.toString());
        }
        catch(DataException e) {
    e.printStackTrace();
        }
        return output;

    }
	
	public String getBillBusterAmtDetails(RetailTransactionIfc retailTransactionIfc) throws DataException {
       // System.out.println("Inside getCustomerTransactionDetails");
        DataAction dataAction = new DataAction();
        //BigDecimal output =null;
        String output = null;
        dataAction.setDataObject(retailTransactionIfc);
        dataAction.setDataOperationName("ReadBillBusterAmt");
        
        //System.out.println("ReadMallCrtfTransactionForCustomer");

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = dataAction;
        
       // System.out.println(dataActions[0].getDataOperationName());
        
        setDataActions(dataActions);
        try{
        	//System.out.println("====================113 :: "+(String)getDataManager().execute(this));
         output = (String) getDataManager().execute(this);
        //System.out.println("81 :"+output.toString());
        }
        catch(DataException e) {
    e.printStackTrace();
        }
        return output;

    }
	
	public String getBillBusterPctDetails(RetailTransactionIfc retailTransactionIfc) throws DataException {
	       // System.out.println("Inside getCustomerTransactionDetails");
	        DataAction dataAction = new DataAction();
	        //BigDecimal output =null;
	        String output = null;
	        dataAction.setDataObject(retailTransactionIfc);
	        dataAction.setDataOperationName("ReadBillBusterPct");
	        
	        //System.out.println("ReadMallCrtfTransactionForCustomer");

	        DataActionIfc[] dataActions = new DataActionIfc[1];
	        dataActions[0] = dataAction;
	        
	       // System.out.println(dataActions[0].getDataOperationName());
	        
	        setDataActions(dataActions);
	        try{
	        	//System.out.println("====================113 :: "+(String)getDataManager().execute(this));
	         output = (String) getDataManager().execute(this);
	        //System.out.println("81 :"+output.toString());
	        }
	        catch(DataException e) {
	    e.printStackTrace();
	        }
	        return output;
	    }
	
	
	public String getBillBusterPctItemDetails(RetailTransactionIfc retailTransactionIfc,String ruleID) throws DataException {
	       // System.out.println("Inside getCustomerTransactionDetails");
	        DataAction dataAction = new DataAction();
	        HashMap<String,Object> hm=new HashMap();
	        //BigDecimal output =null;
	        String output = null;
	        hm.put("ruleid",ruleID);
	        hm.put("retailTransactionIfc",retailTransactionIfc);
	        dataAction.setDataObject(hm);
	        dataAction.setDataOperationName("ReadBillBusterPctRuleID");
	        
	        //System.out.println("ReadMallCrtfTransactionForCustomer");

	        DataActionIfc[] dataActions = new DataActionIfc[1];
	        dataActions[0] = dataAction;
	        
	       // System.out.println(dataActions[0].getDataOperationName());
	        
	        setDataActions(dataActions);
	        try{
	        	//System.out.println("====================113 :: "+(String)getDataManager().execute(this));
	         output = (String) getDataManager().execute(this);
	        //System.out.println("81 :"+output.toString());
	        }
	        catch(DataException e) {
	    e.printStackTrace();
	        }
	        return output;
	    }
	
	
	public String getBillBusterAmtItemDetails(RetailTransactionIfc retailTransactionIfc,String ruleID) throws DataException {
	       // System.out.println("Inside getCustomerTransactionDetails");
	        DataAction dataAction = new DataAction();
	        HashMap<String,Object> hm=new HashMap();
	        //BigDecimal output =null;
	        String output = null;
	        hm.put("ruleid",ruleID);
	        hm.put("retailTransactionIfc",retailTransactionIfc);
	        dataAction.setDataObject(hm);
	        dataAction.setDataOperationName("ReadBillBusterAmtRuleID");
	        
	        //System.out.println("ReadMallCrtfTransactionForCustomer");

	        DataActionIfc[] dataActions = new DataActionIfc[1];
	        dataActions[0] = dataAction;
	        
	       // System.out.println(dataActions[0].getDataOperationName());
	        
	        setDataActions(dataActions);
	        try{
	        	//System.out.println("====================113 :: "+(String)getDataManager().execute(this));
	         output = (String) getDataManager().execute(this);
	        //System.out.println("81 :"+output.toString());
	        }
	        catch(DataException e) {
	    e.printStackTrace();
	        }
	        return output;
	    }
}


