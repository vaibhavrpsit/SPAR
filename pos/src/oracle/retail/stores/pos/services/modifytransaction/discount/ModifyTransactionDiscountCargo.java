package oracle.retail.stores.pos.services.modifytransaction.discount;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.TransactionCargoIfc;
import oracle.retail.stores.pos.services.pricing.DiscountCargoIfc;

public class ModifyTransactionDiscountCargo extends AbstractFinancialCargo implements TransactionCargoIfc, DiscountCargoIfc {
  private static final long serialVersionUID = -3631731300386585239L;
  
  public static final String revisionNumber = "$Revision: /main/13 $";
  
  protected RetailTransactionIfc transaction;
  
  protected EmployeeIfc salesAssociate;
  
  protected boolean transactionCreated = false;
  
  protected boolean createTransaction = false;
  
  protected boolean doDiscount = false;
  
  protected TransactionDiscountStrategyIfc discount;
  
  protected TransactionDiscountStrategyIfc oldDiscount;
  
  protected int discountType;
  
  protected CodeListIfc localizedDiscountAmountReasonCodes;
  
  protected CodeListIfc localizedDiscountPercentReasonCodes;
  
  protected CurrencyIfc itemTotal = null;
  
  protected String employeeDiscountID = null;
  
  protected SaleReturnTransactionIfc originalTransaction;
  
  protected boolean instantCreditDiscount = false;
  
  protected String spclEmpDisc = null;
  
  public String getSpclEmpDisc() {
	return spclEmpDisc;
}

public void setSpclEmpDisc(String spclEmpDisc) {
	this.spclEmpDisc = spclEmpDisc;
}

public CodeListIfc getLocalizedDiscountPercentReasonCodes() {
    return this.localizedDiscountPercentReasonCodes;
  }
  
  public void setLocalizedDiscountPercentReasonCodes(CodeListIfc list) {
    this.localizedDiscountPercentReasonCodes = list;
  }
  
  public CodeListIfc getLocalizedDiscountAmountReasonCodes() {
    return this.localizedDiscountAmountReasonCodes;
  }
  
  public void setLocalizedDiscountAmountReasonCodes(CodeListIfc list) {
    this.localizedDiscountAmountReasonCodes = list;
  }
  
  public int getAccessFunctionID() {
    return 6;
  }
  
  public void setSalesAssociate(EmployeeIfc employee) {
    this.salesAssociate = employee;
  }
  
  public EmployeeIfc getSalesAssociate() {
    return this.salesAssociate;
  }
  
  public void setTransaction(RetailTransactionIfc trans) {
    this.transaction = trans;
  }
  
  public RetailTransactionIfc getTransaction() {
    return this.transaction;
  }
  
  public void setTransactionCreated(boolean value) {
    this.transactionCreated = value;
  }
  
  public boolean getTransactionCreated() {
    return this.transactionCreated;
  }
  
  public void setCreateTransaction(boolean value) {
    this.createTransaction = value;
  }
  
  public boolean createTransaction() {
    return this.createTransaction;
  }
  
  public void setDoDiscount(boolean value) {
    this.doDiscount = value;
  }
  
  public boolean getDoDiscount() {
    return this.doDiscount;
  }
  
  public void setDiscount(TransactionDiscountStrategyIfc value) {
    this.oldDiscount = getDiscount();
    this.discount = value;
  }
  
  public TransactionDiscountStrategyIfc getDiscount() {
    return this.discount;
  }
  
  public TransactionDiscountStrategyIfc getOldDiscount() {
    return this.oldDiscount;
  }
  
  public void setDiscountType(int value) {
    this.discountType = value;
  }
  
  public int getDiscountType() {
    return this.discountType;
  }
  
  public void setItemTotal(CurrencyIfc itmTtls) {
    this.itemTotal = itmTtls;
  }
  
  public CurrencyIfc getItemTotal() {
    return this.itemTotal;
  }
  
  public String getEmployeeDiscountID() {
    return this.employeeDiscountID;
  }
  
  public void setEmployeeDiscountID(String employeeDiscountID) {
    this.employeeDiscountID = employeeDiscountID;
  }
  
  public SaleReturnTransactionIfc getOriginalTransaction() {
    return this.originalTransaction;
  }
  
  public void setOriginalTransaction(SaleReturnTransactionIfc value) {
    this.originalTransaction = value;
  }
  
  public boolean isInstantCreditDiscount() {
    return this.instantCreditDiscount;
  }
  
  public void setInstantCreditDiscount(boolean ic) {
    this.instantCreditDiscount = ic;
  }
}
