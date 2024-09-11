/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *	Rev 1.0     Jan 06, 2016		Ashish Yadav		Online Points Redemption FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.loyaltypoints;

import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXSaveManagerOverrideTransaction;
import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import max.retail.stores.domain.manageroverride.MAXManagerOverride;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;


public class MAXLoyaltyOtpOverrideAisle extends PosLaneActionAdapter
{
    /**
	 * This site For set key of Manager override OTP cancel
	 */

	private static final long serialVersionUID = 1L;

    public void traverse(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
cargo.setAccessFunctionID(MAXRoleFunctionIfc.OTP_CANCEL);
        //Changes for Manager Override Report Requirement - Start
    RetailTransactionIfc trans = cargo.getRetailTransaction();
	MAXManagerOverride mgo= new MAXManagerOverride();
	mgo.setBusinessDay(cargo.getTransaction().getBusinessDay());
	mgo.setStoreCreditId("LMR Redemption OTP");
	mgo.setFeatureId("326");
	mgo.setItemId(null);
	mgo.setManagerId(cargo.getOverrideOperator().getEmployeeID());
	mgo.setSequenceNumber("-1");
	mgo.setTransactionID(cargo.getRetailTransaction().getTransactionID());
	mgo.setWsID(trans.getWorkstation().getWorkstationID().toString());
	mgo.setStoreID(trans.getWorkstation().getStoreID().toString());
	//System.out.println("line 44");
	//mgo.setCashierID(cargo.getEmployee().getLoginID().toString());
	mgo.setCashierID(cargo.getOperator().getLoginID().toString());
	mgo.setAmountMO(trans.getTransactionTotals().getGrandTotal().toString());
	//System.out.println("line 49 lmr_id");
	mgo.setLMR_ID(cargo.getCustomer().getCustomerID().toString());
	//System.out.println("line 51 lmr_id");
	MAXSaveManagerOverrideTransaction dbTrans = null;
	dbTrans = (MAXSaveManagerOverrideTransaction) DataTransactionFactory.create(MAXDataTransactionKeys.SAVE_MANAGER_OVERRIDE_TRANSACTION);
	
	try {
		dbTrans.saveManagerOverride(mgo);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}


  	//Changes for Manager Override Report Requirement - End
 
    }
}
