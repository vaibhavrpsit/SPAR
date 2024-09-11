/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *	Rev 1.1		Mar 24, 2017		Mansi Goel		Changes to resolve manual discount gets removed when bill buster rules are active
 *	Rev	1.0 	Nov 30, 2016		Mansi Goel		Changes for Discount Rule FES	
 *
 ********************************************************************************/

package max.retail.stores.pos.services.pricing;

// java imports
import java.math.BigDecimal;
import java.util.ArrayList;

import max.retail.stores.domain.discount.MAXDiscountRuleConstantsIfc;
import max.retail.stores.pos.services.modifytransaction.discount.MAXModifyTransactionDiscountCargo;
import max.retail.stores.pos.services.sale.MAXSaleCargo;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.CustomerDiscountByPercentage;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByPercentageIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByPercentageStrategy;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;

import org.apache.log4j.Logger;

//--------------------------------------------------------------------------
/**
 * Shuttles data from ModifyTransacationDiscountPercent service to
 * ModifyTransaction service. Added by Gaurav
 * 
 * @version $Revision: 1.2 $
 */
// --------------------------------------------------------------------------
public class MAXModifyInvoiceTypeTransactionDiscountPercentReturnShuttle extends FinancialCargoShuttle {
	/**
	 * The logger to which log messages will be sent.
	 */
	protected static Logger logger = Logger
			.getLogger(max.retail.stores.pos.services.pricing.MAXModifyInvoiceTypeTransactionDiscountPercentReturnShuttle.class);
	/**
	 * revision number of this class
	 */
	public static final String revisionNumber = "$Revision: 1.2 $";
	/** transaction * */
	protected RetailTransactionIfc transaction = null;
	/** flag to see if there was a discount done in the service * */
	protected boolean doDiscount = false;
	/**
	 * flag to see if a discount has to be cleared
	 * 
	 * @deprecated as of release 7.0. No replacement
	 **/
	protected boolean clearDiscount = false;
	/** The new discount amount * */
	protected TransactionDiscountStrategyIfc discountPercent;
	/** The old discount amount * */
	protected TransactionDiscountStrategyIfc oldDiscountPercent;
	/**
	 * employee discount id
	 */
	protected String employeeDiscountID = null;

	protected boolean invoiceRuleAlreadyApplied = false; // Gaurav

	protected BigDecimal invoiceRuleAppliedRate = new BigDecimal("0.00"); // Gaurav
	CurrencyIfc invoiceRuleAppliesAmount = DomainGateway.getBaseCurrencyInstance();
	protected int invoiceDiscountType = 0;
	
	//protected CurrencyIfc discountAmt = D;

	// ----------------------------------------------------------------------
	/**
	 * Loads data from ModifyTransactionDiscountPercent service.
	 * <P>
	 * 
	 * @param bus
	 *            Service Bus
	 */
	// ----------------------------------------------------------------------
	public void load(BusIfc bus) {
		// load financial cargo
		super.load(bus);
		// retrieve the child cargo
		MAXModifyTransactionDiscountCargo cargo = (MAXModifyTransactionDiscountCargo) bus.getCargo();

		// get all the cargo from the child service - all decisions will be in
		// the unload
		doDiscount = cargo.getDoDiscount();
		discountPercent = cargo.getDiscount();
		
		//cargo.getInvoiceDiscounts().get(i);
		
		//cargo.getInvoiceDiscountAmount();
		/*
		 * for(int i=0;i<cargo.getInvoiceDiscounts().size();i++) {
		 * if(cargo.getInvoiceDiscounts().get(i).getDescription().equalsIgnoreCase(
		 * "Buy$NorMoreGetYatZ$offTiered_BillBuster")) discountPercent =
		 * (TransactionDiscountStrategyIfc)
		 * cargo.getInvoiceDiscounts().get(i).getDiscountAmount(); }
		 */
		if(discountPercent!=null)
		{
			discountPercent.setReasonCode(cargo.getDiscountType());
		}
		oldDiscountPercent = cargo.getOldDiscount();
		invoiceDiscountType = cargo.getDiscountType();
		transaction = cargo.getTransaction();
		if (cargo.getDiscountType() == 19)
			invoiceRuleAppliedRate = cargo.getInvoiceRuleAppliedRate();
		else if (cargo.getDiscountType() == 20)
			invoiceRuleAppliesAmount = cargo.getDiscount().getDiscountAmount();
		else if(cargo.getDiscountType() == 99 || cargo.getDiscountType() == 33 ||cargo.getDiscountType() == 34)
		{
			invoiceRuleAppliesAmount = cargo.getInvoiceDiscountAmount();
			TransactionDiscountByPercentageIfc percentDiscount = DomainGateway.getFactory().getTransactionDiscountByPercentageInstance();
			percentDiscount.setDiscountAmount(cargo.getInvoiceDiscountAmount());
			discountPercent = percentDiscount;
			discountPercent.setDiscountAmount(percentDiscount.getDiscountAmount());
			discountPercent.getDiscountAmount();
		}
		invoiceRuleAlreadyApplied = cargo.isInvoiceRuleAlreadyApplied();

		// employeeDiscountID = cargo.getEmployeeDiscountID();
	}

	// ----------------------------------------------------------------------
	/**
	 * Unloads data to ModifyTransaction service.
	 * <P>
	 * 
	 * @param bus
	 *            Service Bus
	 */
	// ----------------------------------------------------------------------
	public void unload(BusIfc bus) {
		// unload financial cargo
		super.unload(bus);
		// retrieve the parent cargo
		MAXSaleCargo cargo = (MAXSaleCargo) bus.getCargo();
		boolean onlyOneDiscount = true;
		cargo.setInvoiceRuleAlreadyApplied(onlyOneDiscount);
		cargo.setInvoiceRuleAlreadyApplied(invoiceRuleAlreadyApplied);
		if (invoiceDiscountType == 19)
			cargo.setInvoiceRuleAppliedRate(invoiceRuleAppliedRate);
		else if (invoiceDiscountType == 20)
			cargo.setInvoiceDiscountAmount(invoiceRuleAppliesAmount);
		else if(invoiceDiscountType == 99 || invoiceDiscountType == 33 ||invoiceDiscountType == 34)
			cargo.setInvoiceDiscountAmount(invoiceRuleAppliesAmount);
		String discountPercentStr = "";
		// Current discount. Might be null if user leaves % field blank
		/*
		 * if (discountPercent != null) { BigDecimal discountRate =
		 * discountPercent.getDiscountRate(); if
		 * (discountRate.toString().length() > 5) { BigDecimal scaleOne = new
		 * BigDecimal(1); discountRate = discountRate.divide(scaleOne, 2,
		 * BigDecimal.ROUND_HALF_UP); } discountRate =
		 * discountRate.movePointRight(2); discountRate =
		 * discountRate.setScale(0, BigDecimal.ROUND_HALF_UP);
		 * discountPercentStr = discountRate.toString(); }
		 */
		// Old discount
		String oldDiscountPercentStr = "";
		/*
		 * if (oldDiscountPercent != null) { BigDecimal discountRate =
		 * oldDiscountPercent.getDiscountRate(); if
		 * (discountRate.toString().length() > 5) { BigDecimal scaleOne = new
		 * BigDecimal(1); discountRate = discountRate.divide(scaleOne, 2,
		 * BigDecimal.ROUND_HALF_UP); } discountRate =
		 * discountRate.movePointRight(2); discountRate =
		 * discountRate.setScale(0, BigDecimal.ROUND_HALF_UP);
		 * oldDiscountPercentStr = discountRate.toString(); }
		 */
		// set transaction
		/*
		 * if (transaction != null) { // cargo.setTransaction(transaction); }
		 */
		// if a new discount apply it to the transaction
		if (doDiscount == true) {
			// Get journal manager
			JournalManagerIfc mgr = null;
			mgr = (JournalManagerIfc) Gateway.getDispatcher().getManager(JournalManagerIfc.TYPE);
			SaleReturnTransactionIfc trans = (SaleReturnTransactionIfc) cargo.getTransaction();

			TransactionDiscountStrategyIfc[] discounts = null;
			if (onlyOneDiscount) {
				// cargo.removeAllManualDiscounts(null, mgr);
				if (trans != null && discountPercent!=null) {
					trans.addTransactionDiscount(discountPercent);
				}
			} else {
				StringBuffer message = new StringBuffer();
				if (trans != null) {
					discounts = getDiscounts(trans);
				}
				int numDiscounts = 0;
				if (discounts != null) {
					numDiscounts = discounts.length;
				}
				// loop through discounts
				for (int i = 0; i < numDiscounts; i++) {
					TransactionDiscountStrategyIfc discount = discounts[i];
					if (discount instanceof TransactionDiscountByPercentageStrategy
							&& !(discount instanceof CustomerDiscountByPercentage)) {
						// journal removal of discount
						message.append(Util.EOL).append("TRANS: Discount").append(Util.EOL).append("  Discount: ")
								.append("(").append(oldDiscountPercentStr).append("%) Removed").append(Util.EOL);
						if (discount.getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE) {
							message.append("  Emp. ID.: ").append(discount.getDiscountEmployeeID());
							
						} else {
							message.append("  Disc. Rsn.: ").append(
									new Integer(oldDiscountPercent.getReasonCode()).toString());
						}
						if (oldDiscountPercent.getReasonCodeText() != null) {
							message.append(" - ").append(oldDiscountPercent.getReasonCodeText());
						}
					}
				} // end loop thru discounts
				if (message.toString() != "" && trans != null) {
					mgr.journal(trans.getCashier().getEmployeeID(), trans.getTransactionID(), message.toString());
				}
			}
			trans.getTransactionDiscounts();
			// journal new transaction discount percentage
			
			StringBuffer strResult = new StringBuffer();
			if(discountPercent!=null)
			{
			strResult.append(Util.EOL).append("TRANS: Discount")
					.append("                   " + "(" + discountPercentStr + "%)").append(Util.EOL)
					.append("  Discount: Pct.").append(Util.EOL);
			if (discountPercent.getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE) {
				strResult.append("  Emp. ID.: ").append(discountPercent.getDiscountEmployeeID());
			} else {
				strResult.append("  Disc. Rsn.: ").append(new Integer(discountPercent.getReasonCode()).toString());
			}
			if (discountPercent.getReasonCodeText() != null) {
				strResult.append(" - ").append(discountPercent.getReasonCodeText());
			}
			if (trans != null) {
				mgr.journal(trans.getCashier().getEmployeeID(), trans.getTransactionID(), strResult.toString());
			}
			if (trans != null) {
				// clear and add discounts
				clearDiscounts(trans);
				trans.addTransactionDiscount(discountPercent);
			}
			}
			// This code was written just to Reset Transaction Level Type,
			// Invoice Discounts..starts here

			TransactionDiscountStrategyIfc[] trxDiscByInvoice = cargo.getTransaction().getItemContainerProxy()
					.getTransactionDiscounts();
			if (trxDiscByInvoice != null) {
				ArrayList trxDiscByInvoiceList = new ArrayList();

				/*
				 * for (int i = 0; i < trxDiscByInvoice.length; i++) { if
				 * (trxDiscByInvoice[i].getReasonCode() ==
				 * MGDiscountRuleConstantsIfc
				 * .DISCOUNT_DESCRIPTION_Buy$NorMoreforZPctoffHappyHours)// for
				 * // invoice // type // rules
				 * //trxDiscByInvoice[i].setDiscountRate(new
				 * BigDecimal("0.00"));
				 * 
				 * if (trxDiscByInvoice[i].getReasonCode() ==
				 * MGDiscountRuleConstantsIfc
				 * .DISCOUNT_REASON_Buy$NatZ$offTiered)// for // invoice // type
				 * // rules
				 * //trxDiscByInvoice[i].setDiscountAmount(DomainGateway
				 * .getBaseCurrencyInstance("0.00"));
				 * 
				 * // ArrayList trxDiscByInvoiceList = null;
				 * trxDiscByInvoiceList.add(trxDiscByInvoice[i]); }
				 */
				// TransactionDiscountStrategyIfc[] trxDiscByInvoiceAfter = new
				// TransactionDiscountStrategyIfc[trxDiscByInvoiceList.size()];
				// trxDiscByInvoiceAfter = (TransactionDiscountStrategyIfc[])
				// trxDiscByInvoiceList.toArray(new
				// TransactionDiscountStrategyIfc[trxDiscByInvoiceList
				// .size()]);

				// cargo.getTransaction().getItemContainerProxy().setTransactionDiscounts(trxDiscByInvoiceAfter);
				// cargo.getTransaction().getItemContainerProxy().clearAdvancedPricingRules();
			}

			// This code was written just to Reset Transaction Level Type,
			// Invoice Discounts..starts here

		}
		if (employeeDiscountID != null) {
			// cargo.setEmployeeDiscountID(employeeDiscountID);
		}
		if (("Tender").equals(cargo.getInitialOriginStationLetter())) {
			// cargo.setInvoiceRuleAlreadyApplied(false);
			cargo.setInvoiceRuleAppliedRate(new BigDecimal("0.00"));
			bus.mail(new Letter("TenderHome"), BusIfc.CURRENT);
		}
	}

	// ----------------------------------------------------------------------
	/**
	 * Gets Manual Discounts by Percentage from transaction.
	 * <P>
	 * 
	 * @param transaction
	 *            SaleReturnTransaction with potential discounts
	 * @return An array of transaction discount strategies
	 */
	// ----------------------------------------------------------------------
	public TransactionDiscountStrategyIfc[] getDiscounts(SaleReturnTransactionIfc transaction) {
		TransactionDiscountStrategyIfc[] discountArray = transaction.getTransactionDiscounts(
				DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE, DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
		return discountArray;
	}

	// ----------------------------------------------------------------------
	/**
	 * Clears Manual Discounts by Percentage from transaction.
	 * <P>
	 * 
	 * @param transaction
	 *            SaleReturnTransaction with potential discounts
	 */
	// ----------------------------------------------------------------------
	/*
	 * public void clearDiscounts(SaleReturnTransactionIfc transaction) {
	 * transaction.clearTransactionDiscounts(
	 * DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE,
	 * DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL); }
	 */
	// Changes for Rev 1.1 : Starts
	public void clearDiscounts(SaleReturnTransactionIfc transaction) {
		if (discountPercent.getReasonCode() == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NatZPctoffTiered) {
			transaction.clearTransactionDiscounts(DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE,
					DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
		} else if (discountPercent.getReasonCode() == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NatZ$offTiered) {
			transaction.clearTransactionDiscounts(DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT,
					DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
		}
	}
	// Changes for Rev 1.1 : Ends
}
