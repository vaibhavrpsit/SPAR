/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev	1.0 	Jan 06, 2017		Ashish Yadav		Changes for Online redemption loyalty OTP FES	
 *
 ********************************************************************************/

package max.retail.stores.pos.services.sale;

import org.apache.log4j.Logger;

import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXSaveManagerOverrideTransaction;
import max.retail.stores.domain.manageroverride.MAXManagerOverride;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.returns.returnoptions.ReturnOptionsCargo;
import oracle.retail.stores.pos.services.sale.ReturnLaunchShuttle;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This shuttle transfers data from the POS service to the Return service.

    <p>
    @version $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MAXReturnLaunchShuttle extends ReturnLaunchShuttle
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -1448520201566128342L;

	/**
	 * 
	 */

	/**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(max.retail.stores.pos.services.sale.MAXReturnLaunchShuttle.class);

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: 3$";

    // Parent Cargo
    SaleCargoIfc pCargo = null;

    //----------------------------------------------------------------------
    /**
       <P>
       @param  bus     Parent Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // Call load on ReturnLaunchShuttle
    	//System.out.println("Return 56:");
        super.load(bus);

        // retrieve cargo from the parent(Sales Cargo)
        pCargo = (SaleCargoIfc)bus.getCargo();
      //Changes for Manager Override Report Requirement - Start
        //System.out.println("Going inside MAXReturnLaunchShuttle");
        POSUIManagerIfc ui=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        RetailTransactionIfc trans =  pCargo.getTransaction();
       // System.out.println("Return 65::"+pCargo.toString());
       // System.out.println("Return 66::"+trans.toString());
        if(trans==null)
        {
        	 DialogBeanModel dModel = new DialogBeanModel();
             dModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
             dModel.setResourceID("CustomerIsNotLinked");
             dModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "NoLink");
             ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dModel);
             return;
        }
		/*
		 * else { MAXManagerOverride mgo= new MAXManagerOverride(); mgo.setBusinessDay(
		 * pCargo.getTransaction().getBusinessDay()); mgo.setStoreCreditId(null);
		 * mgo.setFeatureId("119"); mgo.setItemId(null); mgo.setManagerId(
		 * pCargo.getOverrideOperator().getEmployeeID()); mgo.setSequenceNumber("-1");
		 * mgo.setTransactionID( pCargo.getRetailTransaction().getTransactionID());
		 * mgo.setWsID(trans.getWorkstation().getWorkstationID().toString());
		 * mgo.setStoreID(trans.getWorkstation().getStoreID().toString()); //
		 * System.out.println("3rd"); mgo.setCashierID(
		 * pCargo.getEmployee().getLoginID().toString());
		 * mgo.setAmountMO(trans.getTransactionTotals().getGrandTotal().toString());
		 * 
		 * MAXSaveManagerOverrideTransaction dbTrans = null; dbTrans =
		 * (MAXSaveManagerOverrideTransaction)
		 * DataTransactionFactory.create(MAXDataTransactionKeys.
		 * SAVE_MANAGER_OVERRIDE_TRANSACTION);
		 * 
		 * 
		 * 
		 * try { dbTrans.saveManagerOverride(mgo); } catch (Exception e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 * 
		 * }
		 *///Changes for Manager Override Report Requirement - End
       
    }

    //----------------------------------------------------------------------
    /**
       ##COMMENT-UNLOAD##
       <P>
       @param  bus     Child Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        // Call unload on ReturnLaunchShuttle
    	//System.out.println("Return 106:");
        super.unload(bus);

        // retrieve cargo from the child(ReturnOptions Cargo)
        ReturnOptionsCargo cargo = (ReturnOptionsCargo) bus.getCargo();
        //System.out.println("Cargo 109 ::"+cargo);
    
        if (pCargo.getTransaction() != null && pCargo.getTransaction().getCustomer() != null)
        {
            cargo.getTransaction().setCustomer(pCargo.getTransaction().getCustomer());
        }      
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
        String strResult = new String("Class:  MAXReturnLaunchShuttle (Revision " +getRevisionNumber() +")" + hashCode());
       //System.out.println("118 Modifytransaction :"+strResult);
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
    	//System.out.println(revisionNumber);
        return(revisionNumber);
    }
}
