package max.retail.stores.pos.services.sale;


import java.util.Vector;

import com.rsa.cryptoj.c.of;

import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXSaveManagerOverrideTransaction;
import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import max.retail.stores.domain.manageroverride.MAXManagerOverride;
import max.retail.stores.domain.manageroverride.MAXManagerOverrideIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.pos.manager.ifc.MAXUtilityManagerIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

public class MAXCancelSelectedAisle extends LaneActionAdapter implements LaneActionIfc {
  static final long serialVersionUID = 43618830911952058L;
  
  public void traverse(BusIfc bus) {
    SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
    cargo.getTransaction().setTransactionStatus(3);
    cargo.setAccessFunctionID(62);
  // System.out.println("Going inside MAXCancelSelectedAisle");

    //Changes for Manager Override Report Requirement - Start
		/*
		 * MAXUtilityManagerIfc MAXUtilityManager =
		 * (MAXUtilityManagerIfc)bus.getManager("UtilityManager");
		 * MAXUtilityManager.updateManagerOverrideMap(MAXRoleFunctionIfc.
		 * CANCEL_TRANSACTION, "")
		 */
//    Vector v= cargo.getTenderableTransaction().getTenderLineItemArray(arg0);
//    for(int i=0;i<v.size();i++) {
//    	if (v.elementAt(i) instanceof TenderLineItemIfc() )
//    }
    RetailTransactionIfc trans = cargo.getRetailTransaction();
	MAXManagerOverride mgo= new MAXManagerOverride();
	mgo.setBusinessDay(cargo.getTransaction().getBusinessDay());
	mgo.setStoreCreditId(null);
	mgo.setFeatureId("62");
	mgo.setItemId(null);
	mgo.setManagerId(cargo.getOverrideOperator().getEmployeeID());
	mgo.setSequenceNumber("-1");
	mgo.setTransactionID(cargo.getRetailTransaction().getTransactionID());
	mgo.setWsID(trans.getWorkstation().getWorkstationID().toString());
	mgo.setStoreID(trans.getWorkstation().getStoreID().toString());
	//System.out.println("3rd");
	mgo.setCashierID(cargo.getEmployee().getLoginID().toString());
	mgo.setAmountMO(trans.getTransactionTotals().getGrandTotal().toString());
	//mgo.setAmountMO(((TenderableTransactionIfc) cargo).getTransactionTotals().getAmountOffTotal().toString());
	// amountPaid = transaction.getTransactionTotals().getGrandTotal().subtract(orderTxn.getPayment().getPaymentAmount());
   //  mgo.setAmountMO(((TenderableTransactionIfc)cargo).getTransactionTotals().getAmountTender());
     
	//System.out.println("4th");
	MAXSaveManagerOverrideTransaction dbTrans = null;
	dbTrans = (MAXSaveManagerOverrideTransaction) DataTransactionFactory.create(MAXDataTransactionKeys.SAVE_MANAGER_OVERRIDE_TRANSACTION);
	

	
	try {
		dbTrans.saveManagerOverride(mgo);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}


	
	
		/*
		 * if(null != MAXUtilityManager.getManagerOverrideMap()) { if(null != trans &&
		 * trans instanceof MAXSaleReturnTransactionIfc) {
		 * ((MAXSaleReturnTransactionIfc)trans).setManagerOverrideMap(MAXUtilityManager.
		 * getManagerOverrideMap()); } }
		 */
  	//Changes for Manager Override Report Requirement - End
 
    bus.mail((LetterIfc)new Letter("Override"), BusIfc.CURRENT);
  }
}

