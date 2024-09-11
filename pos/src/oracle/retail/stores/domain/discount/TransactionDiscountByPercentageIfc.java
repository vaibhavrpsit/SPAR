package oracle.retail.stores.domain.discount;

public abstract interface TransactionDiscountByPercentageIfc
extends TransactionDiscountStrategyIfc
{
public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
//below code is added bya tul shukla
	 public  void setEmployeeCompanyName(String employeeCompanyName);
	 
	 
	  public String getEmployeeCompanyName() ;
		
	
}