package max.retail.stores.pos.services.pricing;


import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.common.RetailTransactionCargoIfc;
import oracle.retail.stores.pos.services.modifytransaction.discount.ModifyTransactionDiscountCargo;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

import org.apache.log4j.Logger;

import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXSaveManagerOverrideTransaction;
import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import max.retail.stores.domain.manageroverride.MAXManagerOverride;
import max.retail.stores.pos.manager.ifc.MAXUtilityManagerIfc;

public class MAXModifyTransactionDiscountAmountLaunchShuttle extends FinancialCargoShuttle {
  protected static Logger logger = Logger.getLogger(MAXModifyTransactionDiscountAmountLaunchShuttle.class);
  
  public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
  
  protected EmployeeIfc salesAssociate;
  
  protected TransactionDiscountStrategyIfc discountAmount;
  
  protected boolean createTransaction;
  
  protected CurrencyIfc itemTotal;
  
  protected String employeeDiscountID;
  
  protected SaleReturnTransactionIfc transaction;
  
  public void load(BusIfc bus) {
    super.load(bus);
    PricingCargo cargo = (PricingCargo)bus.getCargo();
    this.salesAssociate = (cargo.getTransaction() != null) ? cargo.getTransaction().getSalesAssociate() : null;
    this.employeeDiscountID = cargo.getEmployeeDiscountID();
    this.transaction = (SaleReturnTransactionIfc)cargo.getTransaction();
    if (this.transaction != null) {
      TransactionDiscountStrategyIfc[] discountArray = getDiscounts(this.transaction);
      if (discountArray != null && discountArray.length > 0)
        this.discountAmount = discountArray[0]; 
      this.createTransaction = false;
      this.itemTotal = this.transaction.getTransactionTotals().getDiscountEligibleSubtotal();
    } else {
      this.createTransaction = true;
    } 
    cargo.setAccessFunctionID(MAXRoleFunctionIfc.DISCOUNT);
    MAXUtilityManagerIfc MAXUtilityManager = (MAXUtilityManagerIfc)bus.getManager("UtilityManager");
  SaleReturnLineItemIfc[] MAXSaleReturnLineItem=cargo.getItems();
  
  for(int i=0;i<MAXSaleReturnLineItem.length;i++)
  {
			/*
			 * MAXUtilityManager.updateManagerOverrideMap(MAXRoleFunctionIfc.DISCOUNT,
			 * MAXSaleReturnLineItem[i].getPLUItem().getItemID()); logger.assertLog(true,
			 * "Mukesh");
			 */
		
  }
//changes for set value in manager override start
		//SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
		
		/*
		 * RetailTransactionIfc trans = ((RetailTransactionCargoIfc)
		 * cargo).getRetailTransaction(); MAXManagerOverride mgo= new
		 * MAXManagerOverride();
		 * mgo.setBusinessDay(cargo.getTransaction().getBusinessDay());
		 * mgo.setStoreCreditId(null); mgo.setFeatureId("786"); mgo.setItemId(null);
		 * mgo.setManagerId(cargo.getOverrideOperator().getEmployeeID());
		 * mgo.setSequenceNumber("-1");
		 * mgo.setTransactionID(((RetailTransactionCargoIfc)
		 * cargo).getRetailTransaction().getTransactionID());
		 * mgo.setWsID(trans.getWorkstation().getWorkstationID().toString());
		 * mgo.setStoreID(trans.getWorkstation().getStoreID().toString());
		 * MAXSaveManagerOverrideTransaction dbTrans = null; dbTrans =
		 * (MAXSaveManagerOverrideTransaction)
		 * DataTransactionFactory.create(MAXDataTransactionKeys.
		 * SAVE_MANAGER_OVERRIDE_TRANSACTION); try { dbTrans.saveManagerOverride(mgo); }
		 * catch (DataException |
		 * com.extendyourstore.foundation.manager.data.DataException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } //changes for set value in
		 * manager override end catch (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
	
    

}
  
  public void unload(BusIfc bus) {
    super.unload(bus);
    ModifyTransactionDiscountCargo cargo = (ModifyTransactionDiscountCargo)bus.getCargo();
    cargo.setDiscount(this.discountAmount);
    cargo.setDiscountType(2);
    cargo.setCreateTransaction(this.createTransaction);
    cargo.setItemTotal(this.itemTotal);
    cargo.setEmployeeDiscountID(this.employeeDiscountID);
    if (!this.createTransaction)
      cargo.setTransaction((RetailTransactionIfc)this.transaction); 
  }
  
  public TransactionDiscountStrategyIfc[] getDiscounts(SaleReturnTransactionIfc transaction) {
    TransactionDiscountStrategyIfc[] discountArray = transaction.getTransactionDiscounts(2, 0);
    return discountArray;
  }
}

