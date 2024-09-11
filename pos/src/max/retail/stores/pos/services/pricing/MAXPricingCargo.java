
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.2	Kamlesh Pant 					Employee discount validation through OTP.
  Rev 1.1	Jyoti Rawal		27/04/2013		Changes for Gift Card FES
  Rev 1.0	Jyoti Rawal		09/04/2013		Initial Draft: Changes for Employee Discount
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.pricing;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.pos.services.pricing.PricingCargo;

//--------------------------------------------------------------------------
/**
    This class represents the cargo for Pricing services. <>P>
    @version $Revision: 10$
**/
//--------------------------------------------------------------------------
public class MAXPricingCargo extends PricingCargo
{
  

	/**
	 * 
	 */
	private static final long serialVersionUID = -2297879880627502236L;

	/** revision number supplied by version control **/
    public static final String revisionNumber = "$Revision: 10$";

    /** The logger to which log messages will be sent. **/
    protected static Logger logger = Logger.getLogger(max.retail.stores.pos.services.pricing.MAXPricingCargo.class);

    /**
     *  Rev 1.0 changes start here 	
     */
    protected String employeeDiscountMethod = null; 
	protected boolean isEmployeeRemoveSelected = false;
	 /**
     *  Rev 1.1 changes start here 	
     */
	protected int gcDiscountPercent = 0;
	protected TransactionDiscountStrategyIfc discount;
	 /**
     *  Rev 1.1 changes end here 	
     */
	/**
     *  Rev 1.0 changes end here 	
     */
    
	/**
     *  Rev 1.0 changes start here 	
     */
	
	protected String spclEmpDisc = null;
	private int otpRetries = 0;
	
	protected boolean empID = false;
	
    public boolean isEmpID() {
		return empID;
	}
	public void setEmpID(boolean empID) {
		this.empID = empID;
	}
	public String getSpclEmpDisc() {
		return spclEmpDisc;
	}
	public void setSpclEmpDisc(String spclEmpDisc) {
		this.spclEmpDisc = spclEmpDisc;
	}
	
	public void setEmployeeDiscountMethod(String employeeDiscountMethod) {
		this.employeeDiscountMethod = employeeDiscountMethod;
	}
	public String getEmployeeDiscountMethod() {
		return employeeDiscountMethod;
	}
	public boolean isEmployeeRemoveSelected() {
		return isEmployeeRemoveSelected;
	}

	public void setEmployeeRemoveSelected(boolean isEmployeeRemoveSelected) {
		this.isEmployeeRemoveSelected = isEmployeeRemoveSelected;
	}
	protected boolean isAutoEmpDiscount = false;

	public boolean isAutoEmpDiscount() {
		return isAutoEmpDiscount;
	}

	public void setAutoEmpDiscount(boolean isAutoEmpDiscount) {
		this.isAutoEmpDiscount = isAutoEmpDiscount;
	}
	/**
     *  Rev 1.0 changes end here 	
     */
	/**
     *  Rev 1.1 changes start here 	
     */
	/**
	 * Gets the GC discount value
	 * 
	 * @return discount
	 **/
	public TransactionDiscountStrategyIfc getDiscount() {
		return discount;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Sets the GC discount value
	 * 
	 * @param TransactionDiscountStrategyIfc
	 **/
	public void setDiscount(TransactionDiscountStrategyIfc discount) {
		this.discount = discount;
	}
	/**
     *  Rev 1.1 changes end here 	
     */
	
	
	// Added By vaibhav Employee Discount thru OTP validation starts
		public int getOtpRetries() {
			return otpRetries;
		}

		public void setOtpRetries(int otpRetries) {
			this.otpRetries = otpRetries;
		}
		
		// Added by kamlesh pant for manager override
		protected EmployeeIfc lastOperator = null;
		
		public void setLastOperator(EmployeeIfc value) {
			       this.lastOperator = value;
			   }
		public EmployeeIfc getLastOperator() {
			       return this.lastOperator;
			   }
}
