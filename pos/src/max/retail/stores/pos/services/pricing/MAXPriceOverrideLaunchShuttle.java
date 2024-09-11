/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAX, Inc.    All Rights Reserved.
  Rev 1.0	Jyoti Rawal		09/04/2013		Initial Draft: Changes for Employee Discount
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.pricing;

// foundation imports
import org.apache.log4j.Logger;

import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXSaveManagerOverrideTransaction;
import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import max.retail.stores.domain.manageroverride.MAXManagerOverride;
import max.retail.stores.pos.manager.utility.MAXUtilityManager;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.pricing.PricingCargo;

//------------------------------------------------------------------------------
/**
    This shuttle carries the required contents from
    the pricing service to the price override service. <P>
    @version $Revision: 3$
**/
//------------------------------------------------------------------------------
public class MAXPriceOverrideLaunchShuttle extends FinancialCargoShuttle
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -6879712828179074368L;

	/** The logger to which log messages will be sent. **/
    protected static Logger logger = Logger.getLogger(max.retail.stores.pos.services.pricing.MAXPriceOverrideLaunchShuttle.class);

    /** revision number supplied by source-code-control system **/
    public static String revisionNumber = "$Revision: 3$";

    /** class name constant **/
    public static final String SHUTTLENAME = "MAXPriceOverrideLaunchShuttle";

    /** The Pricing Cargo of the source service **/
    protected MAXPricingCargo pricingCargo = null;  //Rev 1.0 changes 

    //--------------------------------------------------------------------------
    /**
       Copies information from the cargo used in the pricing service. <P>
       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        super.load(bus);
        pricingCargo = (MAXPricingCargo)bus.getCargo();   //Rev 1.0 changes 
       // System.out.println("Going inside MAXpriceOverrideLaunchShuttle");

        //Changes for Manager Override Report Requirement - Start
    	
		/*
		 * RetailTransactionIfc trans = pricingCargo.getTransaction();
		 * MAXManagerOverride mgo= new MAXManagerOverride();
		 * mgo.setBusinessDay(pricingCargo.getTransaction().getBusinessDay());
		 * mgo.setStoreCreditId(null); mgo.setFeatureId("5"); mgo.setItemId(null);
		 * mgo.setManagerId(pricingCargo.getOverrideOperator().getEmployeeID());
		 * mgo.setSequenceNumber("-1");
		 * mgo.setTransactionID(pricingCargo.getTransaction().getTransactionID());
		 * mgo.setWsID(trans.getWorkstation().getWorkstationID().toString());
		 * mgo.setStoreID(trans.getWorkstation().getStoreID().toString());
		 * mgo.setCashierID(pricingCargo.getOperator().getLoginID().toString());
		 * mgo.setAmountMO(trans.getTransactionTotals().getGrandTotal().toString());
		 * 
		 * // System.out.println("3rd"); MAXSaveManagerOverrideTransaction dbTrans =
		 * null; dbTrans = (MAXSaveManagerOverrideTransaction)
		 * DataTransactionFactory.create(MAXDataTransactionKeys.
		 * SAVE_MANAGER_OVERRIDE_TRANSACTION); try { dbTrans.saveManagerOverride(mgo); }
		 * catch (Exception e) { // TODO Auto-generated catch block e.printStackTrace();
		 * }
		 */
      	//Changes for Manager Override Report Requirement - End

    }

    //--------------------------------------------------------------------------
    /**
       Copies information to the cargo used in the pricing service. <P>
       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        PricingCargo pc =(PricingCargo) bus.getCargo();   
        pc.setItems(pricingCargo.getItems());
        pc.setTransaction(pricingCargo.getTransaction());
        pc.setAccessFunctionID(RoleFunctionIfc.PRICE_OVERRIDE);

        SaleReturnLineItemIfc[] MAXSaleReturnLineItem=pc.getItems();
	
       
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the source-code-control system revision number. <P>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
