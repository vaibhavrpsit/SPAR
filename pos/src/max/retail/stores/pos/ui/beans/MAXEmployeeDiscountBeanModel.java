/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 
  Rev 1.0	Atul Shukla		23/April/2018		Initial Draft: Changes for Employee Discount FES functionality
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package max.retail.stores.pos.ui.beans;

import java.math.BigDecimal;
import java.util.Vector;

import oracle.retail.stores.pos.ui.beans.ReasonBeanModel;

public class MAXEmployeeDiscountBeanModel extends ReasonBeanModel
/*     */ {
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
   protected static final String revisionNumber = "$Revision: /main/14 $";
   protected BigDecimal fieldValue = null;
   protected String empId = null;
   protected Vector companyName;

   public Vector getCompanyName() {
	return companyName;
}

public void setCompanyName(Vector companyName) {
	this.companyName = companyName;
}

protected boolean booleanValue = false;

  
   
   



public String getEmpId() {
	return empId;
}

public void setEmpId(String empId) {
	this.empId = empId;
}

public MAXEmployeeDiscountBeanModel()
{}

  public BigDecimal getValue()
   {
     return this.fieldValue;
   }

   public void setValue(BigDecimal value)
   {
     this.fieldValue = value;
  }
 }
