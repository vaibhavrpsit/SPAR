/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013	MAX HyperMarkets.    All Rights Reserved.
  Rev 1.5      26/04/2018       Atul Shukla				 Changes made for employee discount CR, validating employee from CO 
  Rev 1.4      25/08/2015       Gaurav Bawa				 Bug ID 16369 
   Rev 1.3     21/08/2015     Mohd Arif                  change for tic screen for return transaction. 
    Rev 1.2     28/03/2015      akhilesh kumar           cardless loyalty customer 
	Rev 1.1     12/08/2014      Shruti Singh           		Centralized Employee Discount 
	Rev 1.0 	20/05/2013		Prateek		Initial Draft: Changes for TIC Customer Integration
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.sale.validate;

import java.util.Iterator;
import java.util.Vector;

import max.retail.stores.domain.arts.MAXCentralEmployeeTransaction;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.customer.MAXCustomerConstantsIfc;
import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.customer.MAXTICCustomerIfc;
import max.retail.stores.domain.discount.MAXDiscountRuleConstantsIfc;
import max.retail.stores.domain.employee.MAXEmployee;
import max.retail.stores.domain.employee.MAXEmployeeIfc;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.pos.services.sale.MAXSaleCargo;
import max.retail.stores.pos.services.sale.MAXSaleCargoIfc;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXCheckForLinkedCustomerSite extends PosSiteActionAdapter {

	// ----------------------------------------------------------------------
	/**
	 * serialVersionUID long
	 **/
	// ----------------------------------------------------------------------
	private static final long serialVersionUID = -6406015863599238803L;

	public void arrive(BusIfc bus) {
		// Default the letter value to RequiredCustomer
		String letter = "RequiredCustomer";
		String RedemptionForNonTICCustomer = Gateway.getProperty("application",
				"RedemptionForNonTICCustomer", "Y");
		// SaleCargoIfc cargo = (MAXSaleCargoIfc) bus.getCargo();
		MAXSaleCargoIfc cargo = (MAXSaleCargoIfc) bus.getCargo();
		// SaleReturnTransactionIfc transaction = cargo.getTransaction();
		MAXSaleReturnTransactionIfc transaction = (MAXSaleReturnTransactionIfc) cargo
				.getTransaction();
		POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		if (transaction != null) {

			if (cargo instanceof MAXSaleCargo
					&& ((MAXSaleCargo) cargo).getTicCustomer() != null
					&& ((MAXSaleCargo) cargo).getTicCustomer() instanceof MAXTICCustomerIfc) {
				transaction.setMAXTICCustomer(((MAXSaleCargo) cargo)
						.getTicCustomer());
			}
			POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);

			/** Changes for Rev 1.1 : Starts **/
			String amount, updateamount = null;
			CurrencyIfc extendedDiscountSellingAmount = DomainGateway
					.getBaseCurrencyInstance();

			Vector v = transaction.getItemContainerProxy().getLineItemsVector();
			/** Changes for Rev 1.5 : Starts **/
			String empId = null;
			String companyName=null;
			if(transaction.getEmployeeCompanyName() !=null)
			{
		
			try
			{
		 companyName=transaction.getEmployeeCompanyName().trim().toString();
			}catch(NullPointerException ne)
			{
				ne.printStackTrace();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			}else
			{
				// handling flow in suspened-retrive flow
				try
				{
				String discountedEmployee=transaction.getEmployeeDiscountID();
				String[] empDetails=discountedEmployee.split("-");
				empId=empDetails[0];
				companyName=empDetails[1];
				// setting the company name in case of suspened retrived flow
				transaction.setEmployeeCompanyName(companyName);
				}
				catch(NullPointerException ne)
				{
					//ne.printStackTrace();
				     logger.warn("Link customer. " + ne.getMessage() + "");
				}catch(Exception e)
				{
					//e.printStackTrace();
				     logger.warn("Link customer. " + e.getMessage() + "");
				}
				
			}
			/** Changes for Rev 1.5 : End **/
			for (Iterator itemsIter = v.iterator(); itemsIter.hasNext();) {
				MAXSaleReturnLineItemIfc lineItem = (MAXSaleReturnLineItemIfc) itemsIter
						.next();
				ItemDiscountStrategyIfc[] k = (ItemDiscountStrategyIfc[]) lineItem.getItemPrice()
						.getItemDiscounts();

				for (int disc = 0; disc < k.length; disc++) {
					if (k[disc].getDiscountEmployee() != null
							&& k[disc].getAssignmentBasis() == MAXDiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE) {
						empId = k[disc].getDiscountEmployeeID();
						
					
					}

					if (empId != null && !empId.equals("")) {
						if (lineItem.getAdvancedPricingDiscount() == null) {
							CurrencyIfc price = lineItem
									.getExtendedDiscountedSellingPrice();
							extendedDiscountSellingAmount = price
									.add(extendedDiscountSellingAmount);
						}
					}
				}
			}

			amount = extendedDiscountSellingAmount.getStringValue();
			long val = Math.round(extendedDiscountSellingAmount
					.getDoubleValue());
			updateamount = val + "";

			ParameterManagerIfc pm1 = (ParameterManagerIfc) bus
					.getManager(ParameterManagerIfc.TYPE);
			int empDisGraceAmount = 0;
			try {
				empDisGraceAmount = pm1.getIntegerValue(
						"EmployeeDiscountGraceAmount").intValue();
			} catch (ParameterException e) {
				if (logger.isInfoEnabled())
					logger.info("MAXCheckForLinkedCustomerSite, cannot find EmployeeDiscountGraceAmount paremeter.");
			}

			MAXEmployeeIfc employee = null;
			/** Changes for Rev 1.5 : Starts **/
			if (empId != null && empId != "" && !(empId.equals("")) && companyName !=null) {
				/** Changes for Rev 1.5 : End **/
				MAXCentralEmployeeTransaction centralEmployeeTransaction = null;
				centralEmployeeTransaction = (MAXCentralEmployeeTransaction) DataTransactionFactory
						.create(MAXDataTransactionKeys.EMPLOYEE_CENTRAL_TRANSACTION);
				try {
					employee = centralEmployeeTransaction
							.getEmployeeNumber(empId,companyName);
					//employee = centralEmployeeTransaction
						//	.getEmployeeNumber(empId);
					
					MAXEmployee.availAmount = Integer.parseInt(employee
							.getAvailableAmount());
					MAXEmployee.elligibleAmount = Integer.parseInt(employee
							.getEligibleAmount());

				} catch (DataException e) {
					// e.printStackTrace();
				}
			}

			if (employee != null
					&& Integer.parseInt(employee.getAvailableAmount()) < 1
					&& Integer.parseInt(updateamount) > 0) {
				// Insufficient Fund Notice
				DialogBeanModel dialogModel = new DialogBeanModel();
				dialogModel.setResourceID("InsufficientBalance");
				dialogModel.setType(DialogScreensIfc.ERROR);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				return;
			}

			int total = empDisGraceAmount + MAXEmployee.availAmount;
			if (empId != null && !empId.equals("")
					&& Integer.parseInt(updateamount) > total) {

				// initialize model bean
				DialogBeanModel dialogModel = new DialogBeanModel();
				dialogModel.setResourceID("AmountExceeded");
				dialogModel.setType(DialogScreensIfc.ERROR);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
				ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				return;
			}
			/** Changes for Rev 1.1 : Ends **/

			/* Rev 1.2 Start */

			boolean trainingMode = false;
			boolean reentryMode = false;

			if (cargo.getRegister() != null
					&& cargo.getRegister().getWorkstation() != null) {
				trainingMode = cargo.getRegister().getWorkstation()
						.isTrainingMode();
				reentryMode = cargo.getRegister().getWorkstation()
						.isTransReentryMode();

			}

			MAXSaleReturnTransactionIfc saleTransaction = null;
			if (cargo.getTransaction() != null
					&& cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc) {
				saleTransaction = (MAXSaleReturnTransactionIfc) cargo
						.getTransaction();
			}

			MAXTICCustomerIfc maxTicCustomer = null;
			if (saleTransaction != null
					&& saleTransaction.getMAXTICCustomer() instanceof MAXTICCustomerIfc) {
				maxTicCustomer = (MAXTICCustomerIfc) saleTransaction
						.getMAXTICCustomer();
			}

			MAXCustomerIfc maxCustomer = null;
			if (saleTransaction != null
					&& saleTransaction.getCustomer() instanceof MAXCustomerIfc) {
				maxCustomer = (MAXCustomerIfc) saleTransaction.getCustomer();
			}
			
			/*if(transaction.getTransactionType()==2)
			{
				letter = "DONOTCaptureTICCustomer";
				bus.mail(new Letter(letter), BusIfc.CURRENT);
				
			}*/

			if (!(maxCustomer != null && maxCustomer.getCustomerType() != null && maxCustomer
					.getCustomerType().equalsIgnoreCase(
							MAXCustomerConstantsIfc.CRM))
					&& !(maxTicCustomer != null
							&& maxTicCustomer.getTICCustomerID() != null && !maxTicCustomer
							.getTICCustomerID().equalsIgnoreCase(""))
					&& !reentryMode && !trainingMode) {
				//Rev 1.3 Bug ID 16369 Added for loop and if condition : Start
				AbstractTransactionLineItemIfc[] lineItems = transaction.getLineItems();
				boolean itemSendFlag = false;
				for(int i = 0; i < lineItems.length; i++)
				{
					MAXSaleReturnLineItemIfc item = (MAXSaleReturnLineItemIfc)lineItems[i];
					itemSendFlag = item.getItemSendFlag();
					if(itemSendFlag)
						break;
				}
				if(itemSendFlag && !bus.getCurrentLetter().getName().equalsIgnoreCase("LaunchTender"))
				{
					letter = "Success";
					bus.mail(new Letter(letter), BusIfc.CURRENT);
				}
				//Rev 1.3 Bug ID 16369 : End
				else
					showConfirmationDialog(bus);

			} else if(maxCustomer !=null && maxCustomer.getCustomerType().equalsIgnoreCase("T")){
				letter = "CapillaryCoupon";
				bus.mail(new Letter(letter), BusIfc.CURRENT);
			} 
			else{
				letter = "Success";
				bus.mail(new Letter(letter), BusIfc.CURRENT);
			}

			/* Rev 1.2 END */
			/*
			 * if (transaction.getCustomer() instanceof MAXCustomerIfc) {
			 * MAXCustomerIfc customer = (MAXCustomerIfc)
			 * transaction.getCustomer();
			 * 
			 * if (customer != null) { if(customer.getCustomerType()!=null) { if
			 * (customer.getCustomerType().equals(MAXCustomerConstantsIfc.CRM))
			 * { letter = "Success"; bus.mail(new Letter(letter),
			 * BusIfc.CURRENT); } else showConfirmationDialog(bus); } else {
			 * showConfirmationDialog(bus); } } } else {
			 * showConfirmationDialog(bus); }
			 */

		}
	}

	protected void showConfirmationDialog(BusIfc bus) {
		POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		DialogBeanModel model = new DialogBeanModel();
		model.setResourceID("SearchorEnterMemberNotice");
		model.setType(DialogScreensIfc.CONFIRMATION);
		// when loyal customer does not found the it throws "RequiredCustomer"
		// letter
		model.setButtonLetter(DialogScreensIfc.BUTTON_YES, "RequiredCustomer");

		/* Rev 1.2 Start from letter Success to RequireTICCustomer */
		model.setButtonLetter(DialogScreensIfc.BUTTON_NO, "CheckAmtEligible");

		/* Rev 1.2 END */
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
		try {
			pda.clearText();
			// Display the Loyalty message when customer dialog
			// appears
			UtilityManagerIfc utility = (UtilityManagerIfc) bus
					.getManager(UtilityManagerIfc.TYPE);
			String loyaltyMSG = utility.retrieveLineDisplayText("LoyaltyCard",
					"");
			pda.displayTextAt(0, 0, loyaltyMSG);
		} catch (DeviceException e) {
			logger.warn("Unable to use Line Display: " + e.getMessage() + "");
		}
	}
}
