/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Jyoti Rawal		09/04/2013		Initial Draft: Changes for Employee Discount
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.pricing.employeediscount;

import java.util.Arrays;
import java.util.Vector;

import max.retail.stores.pos.services.pricing.MAXPricingCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXEmployeeDiscountBeanModel;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.pricing.employeediscount.CaptureEmployeeNumberSite;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//--------------------------------------------------------------------------
/**
 * This site displays the EMPLOYEE_NUMBER screen if the employee ID hasn't been
 * captured earlier.
 * <p>
 * 
 * @version $Revision: 1.2 $
 **/
// --------------------------------------------------------------------------
public class MAXCaptureEmployeeNumberSite extends CaptureEmployeeNumberSite {
	/**
	 * 
	 */
	private static final long serialVersionUID = 964710072740686793L;
	/**
	 * revision number
	 **/
	public static String revisionNumber = "$Revision: 1.2 $";

	// ----------------------------------------------------------------------
	/**
	 * Displays the EMPLOYEE_NUMBER screen if the employee ID hasn't been
	 * captured earlier.
	 * <P>
	 * 
	 * @param bus
	 *            Service Bus
	 **/
	// ----------------------------------------------------------------------
	public void arrive(BusIfc bus) {
		MAXPricingCargo cargo = (MAXPricingCargo) bus.getCargo();

		boolean employeeIDFound = false;
		/**
		 * Rev 1.0 changes start here
		 */
		if (("Back").equals(bus.getCurrentLetter().getName())) {   //Rev 1.0 changes 
			cargo.setEmployeeDiscountID(null);
		}
		if (!Util.isEmpty(cargo.getEmployeeDiscountID())) {
			employeeIDFound = true;
		} else {
			String employeeID = checkForEmployeeID(cargo.getItems()); 
			if (!Util.isEmpty(employeeID)) {
				employeeIDFound = true;
				cargo.setEmployeeDiscountID(employeeID);
			}
		}

		if (employeeIDFound) {
			bus.mail(CommonLetterIfc.CONTINUE, BusIfc.CURRENT);
		} else {
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

		MAXEmployeeDiscountBeanModel beanModel=new MAXEmployeeDiscountBeanModel();
			
			 ParameterManagerIfc parameterManager = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
		        String parameter = "EmployeeCompanyNameList";
		        Vector<String>  v = null;
		        try
		        {
		           // parameter = ParameterConstantsIfc.TENDERAUTHORIZATION_CallReferralList;
		            String[] companyName = parameterManager.getStringValues(parameter);
		            v = new Vector(Arrays.asList(companyName));
		        }
		        catch(ParameterException pe)
		        {
		            logger.warn("Couldn't retrieve parameter: " + parameter, pe);
		        }
              beanModel.setCompanyName(v);
		 
			ui.showScreen(MAXPOSUIManagerIfc.EMPLOYEE_NUMBER,beanModel);
		}
	}

	// ----------------------------------------------------------------------
	/**
	 * Determines if any of the line items have an employee discount with an
	 * employee ID.
	 * <P>
	 * 
	 * @param lineItems
	 *            The Sale return Line Items to search
	 * @return The employee ID
	 **/
	// ----------------------------------------------------------------------
//	public String checkForEmployeeID(SaleReturnLineItemIfc[] lineItems) {
//		String employeeID = "";
//		if (lineItems != null) {
//			mainLoop: for (int i = 0; i < lineItems.length; i++) {
//				SaleReturnLineItemIfc srli = lineItems[i];
//
//				// If the item is not discount eligible, go on to
//				// next item.
//				if (!(srli.getPLUItem().getItemClassification().getEmployeeDiscountAllowedFlag())) {
//					continue;
//				} else {
//					// Scan through the discounts that exist in search of an
//					// employee ID
//					// Check discounts by amount first
//					ItemDiscountStrategyIfc[] currentDiscounts = srli.getItemPrice().getItemDiscountsByAmount();
//					for (int x = 0; x < currentDiscounts.length; x++) {
//						// We're only interested in employee discounts
//						if (currentDiscounts[x].getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE) {
//							// Once we find an employee discount with an
//							// employee ID, we're done
//							employeeID = currentDiscounts[x].getDiscountEmployeeID();
//							if (!Util.isEmpty(employeeID)) {
//								break mainLoop;
//							}
//						}
//					}
//					// Then check discounts by percent
//					currentDiscounts = srli.getItemPrice().getItemDiscountsByPercentage();
//					for (int x = 0; x < currentDiscounts.length; x++) {
//						// We're only interested in employee discounts
//						if (currentDiscounts[x].getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE) {
//							// Once we find an employee discount with an
//							// employee ID, we're done
//							employeeID = currentDiscounts[x].getDiscountEmployeeID();
//							if (!Util.isEmpty(employeeID)) {
//								break mainLoop;
//							}
//						}
//					}
//				}
//			}
//		}
//		return employeeID;
//	}
}
