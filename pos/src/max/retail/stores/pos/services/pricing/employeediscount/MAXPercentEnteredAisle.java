/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAX, Inc.    All Rights Reserved.
  Rev 1.2	Atul Shukla		26/04/2018	Changes made for employee discount FES, applying discount for tied up company's
  Rev 1.1	Jyoti Rawal		30/07/2013	Bug 7354 - Remove employee discount : POS crashed 
  Rev 1.0	Jyoti Rawal		09/04/2013		Initial Draft: Changes for Employee Discount
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.pricing.employeediscount;

// java imports
import java.math.BigDecimal;
import java.math.BigInteger;

import max.retail.stores.domain.discount.MAXTransactionDiscountByPercentageIfc;
import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXOrderTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.services.modifytransaction.discount.MAXModifyTransactionDiscountCargo;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByAmountIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByPercentageIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.modifytransaction.discount.ModifyTransactionDiscountCargo;
import oracle.retail.stores.pos.services.modifytransaction.discount.PercentEnteredAisle;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
//--------------------------------------------------------------------------
/**
 * This aisle will validate the Percentage amount entered is valid.
 * <P>
 * 
 * @version $Revision: 6$
 **/
// --------------------------------------------------------------------------
public class MAXPercentEnteredAisle extends PercentEnteredAisle {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6606193022185887752L;
	/**
	 * revision number
	 **/
	public static final String revisionNumber = "$Revision: 6$";

	// ----------------------------------------------------------------------
	/**
	 * Stores the percent and reason code.
	 * <P>
	 * 
	 * @param bus
	 *            Service Bus
	 **/
	// ----------------------------------------------------------------------
	public void traverse(BusIfc bus) {
		// Get access to common elements
		// Rev 1.0 changes start here
		MAXModifyTransactionDiscountCargo cargo = (MAXModifyTransactionDiscountCargo) bus.getCargo();
		// Rev 1.0 changes end here
		// Retrieve data from UI model
		POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		POSBaseBeanModel beanModel = (POSBaseBeanModel) uiManager.getModel(POSUIManagerIfc.ENTER_EMPLOYEE_PERCENT_DISCOUNT);

		/**
		 * Rev 1.0 changes start here  
		 */
		
		cargo.setAccessFunctionID(MAXRoleFunctionIfc.PRICE_DISCOUNT);
		cargo.getAccessFunctionID();

		if (cargo.isEmployeeRemoveSelected() && (cargo.getEmployeeDiscountID() != null) && cargo.getDiscount() != null) {
			TransactionDiscountByPercentageIfc percentDiscount = createDiscountStrategy(cargo, new BigDecimal("0.00"));
			cargo.setDiscount(percentDiscount);
			cargo.setClearDiscount(true);
			SaleReturnTransactionIfc transaction = (SaleReturnTransactionIfc) cargo.getTransaction();
			((MAXSaleReturnTransaction)transaction).setDiscountEmployeeName(null);
			((MAXSaleReturnTransaction)transaction).setEmpDiscountAvailLimit(null);
			((MAXSaleReturnTransaction)transaction).setEmployeeDiscountID(null);
			//transaction.getItemContainerProxy().setTransactionDiscounts(null);
			TransactionDiscountStrategyIfc[] v = transaction.getTransactionDiscounts();
			for(int i = 0; i<v.length; i++)
			{
				if(!(v[i] instanceof TransactionDiscountByAmountIfc) && v[i].getDiscountEmployeeID()!= null && !(("").equals(v[i].getDiscountEmployeeID())))
				{
				//Rev 1.1 changes
					if(transaction instanceof MAXSaleReturnTransaction){
					((MAXSaleReturnTransaction)transaction).clearEmployeeDiscount(v[i]);  //Jyoti
					}
					else if(transaction instanceof MAXLayawayTransaction){
						((MAXLayawayTransaction)transaction).clearEmployeeDiscount(v[i]);  //Jyoti
					}
					else if(transaction instanceof MAXOrderTransaction){
						((MAXOrderTransaction)transaction).clearEmployeeDiscount(v[i]);  //Jyoti
					}
				}
			}
			
			
			// cargo.setTransaction((RetailTransactionIfc)transaction);
			cargo.setEmployeeDiscountID("");
			cargo.setEmployeeRemoveSelected(false);
			bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
		} 
		else {
			String pmValue = null;
			int pmPercent = 0;
			BigDecimal response = new BigDecimal("0.00");
			// below code added by atul shukla for employee discount FES
			SaleReturnTransactionIfc trans = (SaleReturnTransactionIfc) cargo.getTransaction();
			String companyName=null;
		    String empDiscParameter=null;
			if(trans instanceof MAXSaleReturnTransaction)
			{
				MAXSaleReturnTransaction maxLs=(MAXSaleReturnTransaction)trans;
				companyName= maxLs.getEmployeeCompanyName().trim().toString();
			}
			empDiscParameter=companyName.concat("EmployeeDiscountAmountPercent");
			// atul's changes end here
			ParameterManagerIfc pm2 = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
			try {
				pmValue = pm2.getStringValue("EmployeeDiscountMethod");
			} catch (ParameterException e) {
				if (logger.isInfoEnabled())
					logger.info("MAXPercentEnteredAisle.traverse(), cannot find EmployeeDiscountMethod paremeter.");
			}
			if ("Manual".equalsIgnoreCase(pmValue)) {
				response = new BigDecimal(beanModel.getPromptAndResponseModel().getResponseText()).setScale(2);
				cargo.setEmployeeDiscountMethod("Manual");
			} else {
				ParameterManagerIfc pm1 = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
				try {
					// below code is added by atul shukla
					pmPercent = pm1.getIntegerValue(empDiscParameter).intValue();
					// atul's changes end here
					//pmPercent = pm1.getIntegerValue("EmployeeDiscountAmountPercent").intValue();
					response = new BigDecimal(pmPercent).setScale(2);
					//cargo.setAccessFunctionID(MAXRoleFunctionIfc.PRICE_DISCOUNT);
				} catch (ParameterException e) {
					if (logger.isInfoEnabled())
						logger.info("MAXPercentEnteredAisle.traverse(), cannot find EmployeeDiscountAmountPercent parameter.");
				}

			}
			/**
			 * Rev 1.0 changes end here  
			 */
			
			// Get discount percent from bean model
			// BigDecimal response = new
			// BigDecimalExt(beanModel.getPromptAndResponseModel().getResponseText()).setScale(2);

			//Changes done for Defect Doc - 11872877 from Oracle
			//response = response.divide(new BigDecimal("100.0"), BigDecimal.ROUND_HALF_UP);
	        response = response.movePointLeft(2);
			// Chop off the potential long values caused by BigDecimal.
			if (response.toString().length() > 5) {
				BigDecimal scaleOne = new BigDecimal(1);
				//response = response.divide(scaleOne, 2, BigDecimal.ROUND_HALF_UP);
				response = response.divide(scaleOne, 2);
			}

			if (isValidDiscount(bus, response)) {
				// retrieve the reason string
				TransactionDiscountByPercentageIfc percentDiscount = createDiscountStrategy(cargo, response);
				/**
				 * Rev 1.0 changes start here
				 */
//				if((((SaleReturnTransaction) cargo.getTransaction()).getItemContainerProxy().getBestDealWinners() != null)
//				&& (((SaleReturnTransaction) cargo.getTransaction()).getItemContainerProxy().getBestDealWinners().size() != 0)){
//					
//				}else{
				// reference this discount in the cargo
				cargo.setDiscount(percentDiscount);
				cargo.setDoDiscount(true);
				//}
				/**
				 * Rev 1.0 end start here
				 */
				bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
			} else {
				ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
				// get maximum disc % allowed parameter
				BigInteger maxTransDiscPct = getMaximumDiscountPercent(pm);
											 
				String[] msg = { LocaleUtilities.formatNumber(maxTransDiscPct, LocaleConstantsIfc.USER_INTERFACE) };
				/**
				 * Rev 1.0 changes start here  
				 */
				TransactionDiscountByPercentageIfc percentDiscount = createDiscountStrategy(cargo, response);

				// reference this discount in the cargo
				cargo.setDiscount(percentDiscount);
				cargo.setDoDiscount(true);

				// showInvalidTransactionDiscountDiscountDialog(uiManager, msg);
				bus.mail("AmountOverrideYes", BusIfc.CURRENT);
				/**
				 * Rev 1.0 changes end here  
				 */
			}
		}
	}

	protected TransactionDiscountByPercentageIfc createDiscountStrategy(
			ModifyTransactionDiscountCargo cargo, BigDecimal percent) {

		int reasonInt = CodeConstantsIfc.CODE_INTEGER_UNDEFINED;

		TransactionDiscountByPercentageIfc percentDiscount = DomainGateway
				.getFactory().getTransactionDiscountByPercentageInstance();
		percentDiscount.setDiscountRate(percent);
		percentDiscount.setReasonCode(reasonInt);
		percentDiscount
				.setAssignmentBasis(DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE);
		percentDiscount.setDiscountEmployee(cargo.getEmployeeDiscountID());
		// changes made by atul shukla for employee discount CR
		MAXSaleReturnTransaction maxTrx=null;
		String employeeCompanyName="";
		if(cargo.getTransaction() instanceof MAXSaleReturnTransaction)
		{
			maxTrx=(MAXSaleReturnTransaction)cargo.getTransaction();
			if(maxTrx.getDiscountEmployeeName() != null)
			{
			employeeCompanyName=maxTrx.getEmployeeCompanyName().trim().toString();
		}
		}
	percentDiscount.setEmployeeCompanyName(employeeCompanyName);
// atul's changes end here
		return percentDiscount;
	}

	/**
	 * Returns a BigInteger, the maximum discount % allowed from the parameter
	 * file.
	 * <P>
	 * 
	 * @param pm
	 *            ParameterManagerIfc reference
	 * @return maximum discount percent allowed as BigInteger
	 **/
	// ----------------------------------------------------------------------
	private BigInteger getMaximumDiscountPercent(ParameterManagerIfc pm) {
		BigInteger maximum = new BigInteger("100"); // default
		try {
			String s = pm.getStringValue(ModifyTransactionDiscountCargo.MAX_EMPLOYEE_TRANS_DISC_PCT);
			s.trim();
			maximum = new BigInteger(s);
			if (logger.isInfoEnabled())
				logger.info("Parameter read: " + ModifyTransactionDiscountCargo.MAX_EMPLOYEE_TRANS_DISC_PCT + "=[" + maximum + "]");
		} catch (ParameterException e) {
			logger.error("" + Util.throwableToString(e) + "");
		}

		return (maximum);
	}

}
