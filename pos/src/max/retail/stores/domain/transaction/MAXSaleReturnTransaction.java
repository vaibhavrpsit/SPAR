/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	Rev 1.14 	May 14, 2024		Kamlesh Pant		Store Credit OTP:
 *	Rev	1.13	sep 22, 2022		Kamlesh Pant   		CapLimit Enforcement for Liquor
 *	Rev	1.12	Aug 31, 2021		Atul Shukla   		Changes Ewallet FES
 *  Rev 1.11    Sep 02, 2020	    Kumar Vaibhav		Pinelabs Integration
 *  Rev 1.10    May 11, 2017	    Ashish Yadav		Changes for M-Coupon Issuance FES	
 *  Rev 1.9     May 04, 2017	    Kritica Agarwal 	GST Changes
 *	Rev 1.8     Apr 13, 2017	    Hitesh Dua			do clonning for tender line item  
 *	Rev 1.7     Jan 13, 2017	    Ashish Yadav		Changes for Loyalty OTP
 *	Rev 1.6		Dec 28, 2016		Mansi Goel			Changes for Gift Card FES
 *	Rev 1.5     Dec 28, 2016	    Ashish Yadav		Changes for Employee Discount FES
 *  Rev 1.4     Nov 09, 2016	    Ashish Yadav		Changes for Home Delivery Send FES
 *  Rev 1.3		Nov 08, 2016        Nadia Arora     	MAX-StoreCredi_Return requirement.
 *	Rev	1.2 	Nov 07, 2016		Mansi Goel			Changes for Discount Rule FES
 *	Rev	1.1 	Oct 19, 2016		Mansi Goel			Changes for Customer FES
 *	Rev	1.0 	Aug 16, 2016		Nitesh Kumar		Changes for Code Merging	
 *
 ********************************************************************************/

package max.retail.stores.domain.transaction;

import java.io.BufferedReader;
// java imports
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import max.retail.stores.domain.MAXPaytmResponse;
import max.retail.stores.domain.bakery.MAXBakeryItemIfc;
import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.discount.MAXDiscountRuleConstantsIfc;
import max.retail.stores.domain.discountCoupon.MAXDiscountCouponIfc;
import max.retail.stores.domain.gstin.MAXGSTINValidationResponseIfc;
import max.retail.stores.domain.lineitem.MAXItemContainerProxy;
import max.retail.stores.domain.lineitem.MAXItemContainerProxyIfc;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItem;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.manager.tenderauth.MAXInstantCreditAuthResponse;
import max.retail.stores.domain.mcoupon.MAXMcouponIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.discount.BestDealGroupIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.discount.DiscountSourceIfc;
import oracle.retail.stores.domain.discount.DiscountTargetIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByAmountIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByPercentageIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.ShippingMethodIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ItemContainerProxyIfc;
import oracle.retail.stores.domain.lineitem.PriceAdjustmentLineItemIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.SendPackageLineItemIfc;
import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.domain.returns.ReturnTenderDataContainerIfc;
import oracle.retail.stores.domain.returns.ReturnTenderDataElementIfc;
import oracle.retail.stores.domain.stock.ItemConstantsIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.tax.SendTaxUtil;
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tax.TaxRulesVO;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderCash;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderCouponIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderGiftCardIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.tender.TenderTravelersCheckIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.utility.CountryCodeMap;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.InstantCreditIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.utility.Util;

//--------------------------------------------------------------------------
/**
 * Sale or return transaction object.
 * <P>
 * 
 * @see com.extendyourstore.domain.transaction.SaleReturnTransactionIfc
 * @version $Revision: /rgbustores_12.0.9in_branch/4 $
 **/
// --------------------------------------------------------------------------
public class MAXSaleReturnTransaction extends SaleReturnTransaction
		implements MAXSaleReturnTransactionIfc, MAXTransactionIfc {

	// This id is used to tell
	// the compiler not to generate a
	// new serialVersionUID.
	//
	// below code is added by aks for storing the Ewallet tender flag and response.
	protected boolean isEWalletTenderFlag = false;
	protected String eWalletCreditResponse = null;
	protected String eWalletTraceId = null;
	

	

	static final long serialVersionUID = 1836685048643285228L;
	protected boolean isItemLevelDiscount = false;
	protected boolean isFatalDeviceCall = false;
	protected boolean isDuplicateReceipt = false;
	
       	//changes for paytmqr
	protected String paytmQROrderId = null;
       	protected HashMap managerOverrideMap;
	
	protected byte[] fileData;
	
	public boolean isDuplicateReceipt() {
		return isDuplicateReceipt;
	}

	public void setDuplicateReceipt(boolean isDuplicateReceipt) {
		this.isDuplicateReceipt = isDuplicateReceipt;
	}

	public HashMap cardNumAck1 = new HashMap();
	/**
	 * Manager Override Hashmap
	 */
	//protected HashMap managerOverrideMap;
	
	protected String submitinvresponse;
	public String getSubmitinvresponse() {
		return submitinvresponse;
	}

	public void setSubmitinvresponse(String submitinvresponse) {
		this.submitinvresponse = submitinvresponse;
	}

		// Change for RTS Start
		protected boolean rtsManagerOverride = false;
		// Change for RTS End
	protected String discountEmployeeName = null;
	protected String discountEmployeeRole = null;
	protected String discountEmployeeLocation = null;
	protected String empDiscountAvailLimit = null;
	// below code is added by atul shukla for employee discount CR
	protected String companyName = null;
	/**
	 * revision number supplied by source-code control system
	 **/
	public static final String revisionNumber = "$Revision: /rgbustores_12.0.9in_branch/4 $";

	/**
	 * order identifier
	 **/
	protected String orderID = "";

	/**
	 * Vector to hold deleted line items, may come from several item clear
	 * actions.
	 **/
	protected Vector deletedLineItems = new Vector();

	private String instantCreditEnrollmentCode = Integer.toString(MAXInstantCreditAuthResponse.OFFLINE);

	// Return Tenders
	protected ReturnTenderDataContainerIfc returnTendersContainer;

	/**
	 * Flag indicating that the transaction is re-entry mode.
	 **/
	boolean reentryMode = false;

	/**
	 * Flag for checked customer physically present. This is actually used to
	 * know if this check has been done or not.
	 **/
	boolean checkedCustomerPresent = false;
	/**
	 * This is used to indicate whether customer is actually physially present
	 * or not
	 **/
	boolean customerPhysicallyPresent = false;

	/**
	 * Flag to indicate what kind of customer used in send transaction It can be
	 * linked customer or captured customer
	 */
	boolean sendCustomerLinked = true;

	/**
	 * Flag indicating if there is a transaction wide gift receipt for this sale
	 * transaction
	 * 
	 */
	boolean transactionGiftReceiptAssigned = false;

	protected boolean taxTypeLegal = false;
	/**
	 * This value is stored in the retail transaction table, to indicate how
	 * many send items are present
	 */
	// PAN Changes
	public String panNumber = null;
	public String idnumber = null;
	public String passportnum = null;
	public String visanum = null;
	public String ackNumber = null;
	
	public String eReceiptOTP = null;
	
	

	int sendPackageCount = 0;
	private static final int LINE_LENGTH = 40;
	protected boolean salesAssociateModifiedFlag = false;
	protected CurrencyIfc promoDiscountForReceipt = null;
	protected boolean sendTransaction = false;
	protected String eComOrderNumber = null;
	protected CurrencyIfc eComOrderAmount = null;
	protected String eComOrderTransNumber = null;
	protected String eComOrderType = null;
	protected boolean eComSendTransaction = false;

	protected BigDecimal foodTotals = null;
	protected BigDecimal nonFoodTotals = null;
	protected BigDecimal easyBuyTotals = null;
	
	protected MAXCustomerIfc maxTicCustomer;
	protected CustomerIfc ticCustomer = null;
	protected boolean ticCustomerVisibleFlag = false;
	// Changes starts for Rev 1.7 (Ashish : online points redemption)
	protected String customerId = null;
	// Changes starts for Rev 1.7 (Ashish : online points redemption)

	/* changes for Rev 1.10 start */
	protected ArrayList<MAXMcouponIfc> mcouponList = null;
	protected List deliveryItems = new ArrayList();
	/* changes for Rev 1.10 end */
	
	public String gstNumber = "";
	public String  gstStoreNumber = "";
	public String customerAddres;
	
	public String customerName;
	public String customerAddres1;
	public String customerAddres2;
	public String Customercity;
	public String Customerstate;
	
	
	

	protected MAXGSTINValidationResponseIfc response = null;
	public BigDecimal getFoodTotals() {
		return foodTotals;
	}

	public void setFoodTotals(BigDecimal foodTotals) {
		this.foodTotals = foodTotals;
	}

	public BigDecimal getNonFoodTotals() {
		return nonFoodTotals;
	}

	public void setNonFoodTotals(BigDecimal nonFoodTotals) {
		this.nonFoodTotals = nonFoodTotals;
	}
	
	public BigDecimal getEasyBuyTotals() {
		return easyBuyTotals;
	}

	public void setEasyBuyTotals(BigDecimal easyBuyTotals) {
		this.easyBuyTotals = easyBuyTotals;
	}

	public boolean iseComSendTransaction() {
		return eComSendTransaction;
	}

	public void seteComSendTransaction(boolean eComSendTransaction) {
		this.eComSendTransaction = eComSendTransaction;
	}

	public String geteComOrderNumber() {
		return eComOrderNumber;
	}

	public void seteComOrderNumber(String eComOrderNumber) {
		this.eComOrderNumber = eComOrderNumber;
	}

	public CurrencyIfc geteComOrderAmount() {
		return eComOrderAmount;
	}

	public void seteComOrderAmount(CurrencyIfc eComOrderAmount) {
		this.eComOrderAmount = eComOrderAmount;
	}

	public String geteComOrderTransNumber() {
		return eComOrderTransNumber;
	}

	public void seteComOrderTransNumber(String eComOrderTransNumber) {
		this.eComOrderTransNumber = eComOrderTransNumber;
	}

	// ---------------------------------------------------------------------
	/**
	 * Constructs SaleReturnTransaction object.
	 **/
	// ---------------------------------------------------------------------
	public MAXSaleReturnTransaction() {
		initialize();
	}

	// ---------------------------------------------------------------------
	/**
	 * Constructs SaleReturnTransaction object.
	 * 
	 * @param station
	 *            The workstation(register) to create a transaction for
	 **/
	// ---------------------------------------------------------------------
	public MAXSaleReturnTransaction(WorkstationIfc station) {
		// initialize Transaction object
		initialize(station);
		// initialize this object
		initialize();
	}

	// izhar
	protected List bdwList = new ArrayList();

	public List getBdwList() {
		return bdwList;
	}

	public void setBdwList(List bdwList) {
		this.bdwList = bdwList;
	}

	// addition ends
	// Added by vaibhav

	public CurrencyIfc getPromoDiscountForReceipt() {
		return promoDiscountForReceipt;
	}

	public void setPromoDiscountForReceipt(CurrencyIfc promoDiscountForReceipt) {
		this.promoDiscountForReceipt = promoDiscountForReceipt;
	}

	// ---------------------------------------------------------------------
	/**
	 * Constructs SaleReturnTransaction object.
	 * 
	 * @param transactionID
	 *            id for the transaction
	 **/
	// ---------------------------------------------------------------------
	public MAXSaleReturnTransaction(String transactionID) {
		// initialize Transaction object
		initialize(transactionID);
		// initialize this object
		initialize();
	}

	// ---------------------------------------------------------------------
	/**
	 * Initialize values.
	 **/
	// ---------------------------------------------------------------------
	protected void initialize() {
		itemProxy = DomainGateway.getFactory().getItemContainerProxyInstance();
		transactionType = TransactionIfc.TYPE_SALE;
		super.initialize();
	}

	
	public Object clone() {
		MAXSaleReturnTransaction srt = new MAXSaleReturnTransaction();
		setCloneAttributes(srt);
		return srt;
	}

	protected void setCloneAttributes(MAXSaleReturnTransaction newClass) {
		super.setCloneAttributes(newClass);
		//changes for rev 1.8 start
		 TenderLineItemIfc[] tli = getTenderLineItems();
	        if (tli != null)
	        {
	            TenderLineItemIfc[] tclone = new TenderLineItemIfc[tli.length];
	            for (int i = 0; i < tli.length; i++)
	            	
	            {
	                tclone[i] = (TenderLineItemIfc)tli[i].clone();
	                if(!isReturn())
	                tclone[i].setCollected(((TenderLineItemIfc)tli[i]).isCollected());
	            }
	            newClass.setTenderLineItems(tclone);
	        }
		//changes for rev 1.8 end	       
		newClass.setFatalDeviceCall(this.isFatalDeviceCall);
		
		if (this.getPrintFreeItem() != null) {
			newClass.setPrintFreeItem(this.getPrintFreeItem());
		}
		if (this.getBdwList().size() > 0) {
			newClass.setBdwList(this.getBdwList());
		}

		newClass.setSalesAssociateModifiedFlag(salesAssociateModifiedFlag);
		newClass.setEmpDiscountAvailLimit(empDiscountAvailLimit);
		// Changes starts for Rev 1.5 (Ashish : Employee Discount)
		newClass.setDiscountEmployeeName(discountEmployeeName);
		newClass.setCustomerId(customerId);
		// Changes ends for Rev 1.5 (Ashish : Employee Discount)
		
		if (this.getCapillaryCouponsApplied() != null) {
			if (this.getCapillaryCouponsApplied().size() > 0) {
				for (int i = 0; i < this.getCapillaryCouponsApplied().size(); i++) {
					MAXDiscountCouponIfc dc = (MAXDiscountCouponIfc) this.getCapillaryCouponsApplied().elementAt(i);
					newClass.addCapillaryCouponsApplied((MAXDiscountCouponIfc) dc.clone());
				}
			}
		}
		newClass.setSendTransaction(sendTransaction);
		if (this.geteComOrderNumber() != null) {
			newClass.seteComOrderNumber(this.geteComOrderNumber());
		}
		if (this.geteComOrderAmount() != null) {
			newClass.seteComOrderAmount(this.geteComOrderAmount());
		}
		if (this.geteComOrderTransNumber() != null) {
			newClass.seteComOrderTransNumber(this.geteComOrderTransNumber());
		}

		newClass.seteComSendTransaction(eComSendTransaction);

		if (this.getFoodTotals() != null) {
			newClass.setFoodTotals(this.getFoodTotals());
		}
		if (this.getNonFoodTotals() != null) {
			newClass.setNonFoodTotals(this.getNonFoodTotals());
		}
		
		if (this.getEasyBuyTotals() != null) {
			newClass.setEasyBuyTotals(this.getEasyBuyTotals());
		}
		newClass.setTicCustomerVisibleFlag(ticCustomerVisibleFlag);
		newClass.setMAXTICCustomer(maxTicCustomer);
		newClass.setTicCustomer(ticCustomer);
		//Change for Rev 1.9 : Starts
		newClass.setTaxCode(taxCode);
		newClass.setGstEnable(gstEnable);
		newClass.setIgstApplicable(igstApplicable);
		//Change for Rev 1.9 : Ends
		
		newClass.setStoreGSTINNumber(gstStoreNumber);
		newClass.setGSTINNumber(gstNumber);
		newClass.setGstinresp(response);
		
		
		newClass.setSbiRewardredeemFlag(sbiRewardredeemFlag);
	}

	public void clearEmployeeDiscount(TransactionDiscountStrategyIfc tds) {
		Iterator itr = itemProxy.getTransactionDiscountsIterator();
		while (itr.hasNext()) {
			if (itr.next() == tds) {
				itr.remove();
				break;
			}
		}

	}

	/**
	 * Rev 1.0 changes end here
	 */
	// ---------------------------------------------------------------------
	/**
	 * Calculates FinancialTotals based on current transaction.
	 * <P>
	 * 
	 * @return FinancialTotalsIfc object
	 **/
	// ---------------------------------------------------------------------
	public FinancialTotalsIfc getFinancialTotals() { // begin
														// getFinancialTotals()
		FinancialTotalsIfc financialTotals = DomainGateway.getFactory().getFinancialTotalsInstance();
		if (getTransactionStatus() == TransactionIfc.STATUS_CANCELED) {
			financialTotals.setNumberCancelledTransactions(1);
			financialTotals
					.setAmountCancelledTransactions(totals.getSubtotal().subtract(totals.getDiscountTotal()).abs());
		} else {
			// get transaction financial totals
			financialTotals.add(getSaleReturnFinancialTotals());

			financialTotals.add(getLineItemsFinancialTotals());

			// get totals from tender line items
			financialTotals.add(getTenderFinancialTotals(getTenderLineItems(), getTransactionTotals()));

			// Get the totals for user deleted lines.
			financialTotals.setUnitsLineVoids(getUnitsLineVoids());
			financialTotals.setAmountLineVoids(getAmountLineVoids());
		}
		if (getInstantCredit() != null) {
			if (getInstantCredit().getApprovalStatus()
					.equals(Integer.toString(MAXInstantCreditAuthResponse.APPROVED))) {
				financialTotals.addHouseCardEnrollmentsApproved(1);
			} else if (getInstantCredit().getApprovalStatus()
					.equals(Integer.toString(MAXInstantCreditAuthResponse.DECLINED))) {
				financialTotals.addHouseCardEnrollmentsDeclined(1);
			}
		}

		return (financialTotals);
	} // end getFinancialTotals()

	// ---------------------------------------------------------------------
	/**
	 * Derives the additive financial totals for a sale for transaction values,
	 * not including line items and tenders .
	 * <P>
	 * 
	 * @return additive financial totals
	 **/
	// ---------------------------------------------------------------------
	public FinancialTotalsIfc getSaleReturnFinancialTotals() { // begin
																// getSaleReturnFinancialTotals()
		FinancialTotalsIfc financialTotals = DomainGateway.getFactory().getFinancialTotalsInstance();

		// gross total is transaction subtotal with discount applied
		CurrencyIfc gross = totals.getSubtotal().subtract(totals.getDiscountTotal());
		CurrencyIfc tax = totals.getTaxTotal();
		CurrencyIfc inclusiveTax = totals.getInclusiveTaxTotal();
		/* India Localization- Tax Related Changes Starts here */
		CurrencyIfc taxableAmount = DomainGateway.getBaseCurrencyInstance();
		if (gross != null && inclusiveTax != null) {
			taxableAmount = gross.subtract(inclusiveTax);
		}
		/* India Localization- Tax Related Changes Ends here */

		// if shipping method is chosen, add the shipping charge amount and
		// count.
		/*SendPackageLineItemIfc[] splis = ((MAXTransactionTotals) totals).getSendPackages();
		if(splis != null)
		{
		for (int i = 0; i < splis.length; i++) {
			financialTotals.add(((AbstractTransactionLineItemIfc) splis[i]).getFinancialTotals(true));
		}
		}*/

		// Back out shipping tax since shipping tax are tracked separately in
		// financial totals.
		tax = tax.subtract(financialTotals.getAmountTaxShippingCharges());
		inclusiveTax = inclusiveTax.subtract(financialTotals.getAmountInclusiveTaxShippingCharges());

		Vector lineItemsVector = itemProxy.getLineItemsVector();
		Iterator i = lineItemsVector.iterator();
		SaleReturnLineItemIfc temp = null;
		while (i.hasNext()) {
			// check for sale items with corresponding kit type
			temp = (SaleReturnLineItemIfc) i.next();
			if (temp.isPriceAdjustmentLineItem()) {
				financialTotals.addUnitsPriceAdjustments(new BigDecimal(1));
			}
		}

		// handle transaction values
		if (transactionType == TransactionIfc.TYPE_SALE || transactionType == TransactionIfc.TYPE_EXCHANGE) { 
			/*
			 * begin handle a sale set tax exempt, taxable sales Note: at this time, 
			 * tax exempt is only managed at the transaction level
			 */
			if (getTransactionTax().getTaxMode() == TaxIfc.TAX_MODE_EXEMPT) {
				financialTotals.addAmountGrossTaxExemptTransactionSales(taxableAmount);
				financialTotals.addCountGrossTaxExemptTransactionSales(1);
			}
			// if tax is positive value, add to taxable
			// Note: tax exempt stuff is counted as non-taxable for now
			// TEC need to see if tax is zero to handle returns
			if (isTaxableTransaction()) {
				financialTotals.addAmountGrossTaxableTransactionSales(taxableAmount);
				financialTotals.addCountGrossTaxableTransactionSales(1);
				financialTotals.addAmountTaxTransactionSales(tax);
				financialTotals.addAmountInclusiveTaxTransactionSales(inclusiveTax);
			} else {
				financialTotals.addAmountGrossNonTaxableTransactionSales(taxableAmount);
				financialTotals.addCountGrossNonTaxableTransactionSales(1);
			}
		} // end handle a sale
		else { // begin handle a return transaction
				// set tax exempt, taxable sales
				// Note: at this time, tax exempt is only managed at the
				// transaction level
			if (getTransactionTax().getTaxMode() == TaxIfc.TAX_MODE_EXEMPT) {
				financialTotals.addAmountGrossTaxExemptTransactionReturns(taxableAmount.abs());
				financialTotals.addCountGrossTaxExemptTransactionReturns(1);
			}
			// if tax is positive value, add to taxable
			// Note: tax exempt stuff is counted as non-taxable for now
			if (isTaxableTransaction()) {
				financialTotals.addAmountGrossTaxableTransactionReturns(taxableAmount.abs());
				financialTotals.addCountGrossTaxableTransactionReturns(1);
				financialTotals.addAmountTaxTransactionReturns(tax.abs());
				financialTotals.addAmountInclusiveTaxTransactionReturns(inclusiveTax.abs());
			} else {
				financialTotals.addAmountGrossNonTaxableTransactionReturns(taxableAmount.abs());
				financialTotals.addCountGrossNonTaxableTransactionReturns(1);
			}
		} // end handle a return transaction

		// handle discount quantities
		// Transaction Amounts are updated in getLineItemsFinancialTotals() by
		// ItemPrice
		TransactionDiscountStrategyIfc[] transDiscounts = getTransactionDiscounts();
		if (transDiscounts != null) {
			for (int x = 0; x < transDiscounts.length; x++) {
				if (transDiscounts[x].getAssignmentBasis() == DiscountRuleIfc.ASSIGNMENT_EMPLOYEE) {
					financialTotals.addUnitsGrossTransactionEmployeeDiscount(new BigDecimal(1));
				} else {
					financialTotals.addNumberTransactionDiscounts(1);
				}
			}
		}

		// set transactions-with-returned-items count
		if (hasReturnItems()) {
			financialTotals.setTransactionsWithReturnedItemsCount(1);
		}

		return (financialTotals);
	} // end getSaleReturnFinancialTotals()

	// ---------------------------------------------------------------------
	/**
	 * Derives the additive financial totals for line items.
	 * <P>
	 * 
	 * @return additive financial totals for line items
	 **/
	// ---------------------------------------------------------------------
	public FinancialTotalsIfc getLineItemsFinancialTotals() {
		FinancialTotalsIfc financialTotals = DomainGateway.getFactory().getFinancialTotalsInstance();

		SaleReturnLineItemIfc[] items = getLineItemsExcluding(
				ItemConstantsIfc.ITEM_KIT_CODE_HEADER | ItemConstantsIfc.ITEM_PRICEADJ_LINEITEM);

		// Kit Headers and price adjustment composite objects are not included
		// in financial totals
		for (int i = 0; i < items.length; i++) {
			financialTotals.add(items[i].getFinancialTotals());
		}

		return financialTotals;
	}

	// ---------------------------------------------------------------------
	/**
	 * Derive the additive financial totals from a given BestDealGroupIfc.
	 * <P>
	 * 
	 * @param bestDealGroup
	 * @param visitedDiscountRules
	 * @return Financial Totals
	 * @deprecated As of release 5.0, Store Coupons are no longer included in
	 *             summary reports
	 **/
	// ---------------------------------------------------------------------
	public FinancialTotalsIfc getFinancialTotalsFromBestDealGroup(BestDealGroupIfc bestDealGroup,
			ArrayList visitedDiscountRules) {
		FinancialTotalsIfc financialTotals = DomainGateway.getFactory().getFinancialTotalsInstance();

		return financialTotals;
	}

	// ---------------------------------------------------------------------
	/**
	 * Adds a line item to the transaction line item vector.
	 * <P>
	 * 
	 * @param lineItem
	 *            SaleReturnLineItemIfc reference
	 **/
	// ---------------------------------------------------------------------
	public void addLineItem(SaleReturnLineItemIfc lineItem) {
		addLineItem((AbstractTransactionLineItemIfc) lineItem);
	}

	// ---------------------------------------------------------------------
	/**
	 * Adds a line item to the transaction line item vector.
	 * <P>
	 * 
	 * @param lineItem
	 *            AbstractTransactionLineItemIfc
	 **/
	// ---------------------------------------------------------------------
	public void addLineItem(AbstractTransactionLineItemIfc lineItem) {
		itemProxy.addLineItem(lineItem);
		// doTaxCalculationForAllLineItems();
		updateTransactionTotals(getItemContainerProxy().getLineItems(),
				getItemContainerProxy().getTransactionDiscounts(), getItemContainerProxy().getTransactionTax());
	}

	// ---------------------------------------------------------------------
	/**
	 * Adds a single PLU item to the transaction.
	 * <P>
	 * 
	 * @param pItem
	 *            PLU item
	 * @return transaction line item
	 **/
	// ---------------------------------------------------------------------
	public SaleReturnLineItemIfc addPLUItem(PLUItemIfc pItem) {
		return addPLUItem(pItem, Util.I_BIG_DECIMAL_ONE);
	}

	// ---------------------------------------------------------------------
	/**
	 * Adds a PLU item to the transaction.
	 * <P>
	 * 
	 * @param pItem
	 *            PLU item
	 * @param qty
	 *            quantity
	 * @return transaction line item
	 **/
	// ---------------------------------------------------------------------
	public SaleReturnLineItemIfc addPLUItem(PLUItemIfc pItem, BigDecimal qty) {
		// izhar
		// boolean isApplyBestDeal =true;
		SaleReturnLineItemIfc srli = itemProxy.addPLUItem(pItem, qty);
		addItemByTaxGroup();
		doTaxCalculationForAllLineItems();
		if (this.getSendTaxRules() != null
				&& ((MAXTransactionTotals) this.getTransactionTotals()).isTransactionLevelSendAssigned()) {
			SendTaxUtil util = new SendTaxUtil();
			TaxRulesVO taxRulesVO = this.getSendTaxRules();
			util.setTaxRulesForLineItem(taxRulesVO, srli);
		}
		updateTransactionTotals(getItemContainerProxy().getLineItems(),
				getItemContainerProxy().getTransactionDiscounts(), getItemContainerProxy().getTransactionTax());
		return (srli);
	}

	// Added by sakshi for Apply Best Deal button starts
	public SaleReturnLineItemIfc addPLUItem(PLUItemIfc pItem, BigDecimal qty, boolean isApplyBestDeal) {
		SaleReturnLineItemIfc srli = ((MAXItemContainerProxy) itemProxy).addPLUItem(pItem, qty, isApplyBestDeal);
		addItemByTaxGroup();
		doTaxCalculationForAllLineItems();
		if (this.getSendTaxRules() != null
				&& ((MAXTransactionTotals) this.getTransactionTotals()).isTransactionLevelSendAssigned()) {
			SendTaxUtil util = new SendTaxUtil();
			TaxRulesVO taxRulesVO = this.getSendTaxRules();
			util.setTaxRulesForLineItem(taxRulesVO, srli);
		}
		updateTransactionTotals(getItemContainerProxy().getLineItems(),
				getItemContainerProxy().getTransactionDiscounts(), getItemContainerProxy().getTransactionTax());
		return (srli);
	}

	// Added by sakshi for Apply Best Deal button ends
	// ---------------------------------------------------------------------
	/**
	 * Adds a return item to the transaction.
	 * <P>
	 * 
	 * @param pItem
	 *            PLUItem object
	 * @param rItem
	 *            ReturnItem object
	 * @param qty
	 *            quantity
	 * @return transaction line item
	 **/
	// ---------------------------------------------------------------------
	public SaleReturnLineItemIfc addReturnItem(PLUItemIfc pItem, ReturnItemIfc rItem, BigDecimal qty) { // begin
																										// addReturnItem()
		SaleReturnLineItemIfc srli = itemProxy.addReturnItem(pItem, rItem, qty);
		// addItemByTaxGroup();
		updateTransactionTotals(getItemContainerProxy().getLineItems(),
				getItemContainerProxy().getTransactionDiscounts(), getItemContainerProxy().getTransactionTax());
		return (srli);
	} // end addReturnItem()

	// ----------------------------------------------------------------------
	/**
	 * Adds a price adjustment line item (including its corresponding return and
	 * sale components) to the transaction.
	 * 
	 * @param saleLineItem
	 *            price adjustment sale line item. Reference is modified.
	 * @param returnItem
	 *            price adjustment return line item. Reference is modified.
	 * @return PriceAdjustmentLineItemIfc instance representing the price
	 *         adjustment
	 **/
	// ----------------------------------------------------------------------
	public PriceAdjustmentLineItemIfc addPriceAdjustmentLineItem(SaleReturnLineItemIfc saleLineItem,
			SaleReturnLineItemIfc returnLineItem) {
		/*
		 * Add the return and sale line items to the transaction.
		 */

		// Add return line item.
		addLineItem(returnLineItem);

		// Add sale line item.
		addLineItem(saleLineItem);

		// Now that we have the return and sale elements, create and add the
		// price adjustment line item.
		// The PriceAdjustmentLineItemIfc instance is added for use by the UI
		// and other facilities
		PriceAdjustmentLineItemIfc priceAdjLineItem = null;
		priceAdjLineItem = DomainGateway.getFactory().getPriceAdjustmentLineItemInstance();
		priceAdjLineItem.initialize(saleLineItem, returnLineItem);
		addLineItem(priceAdjLineItem);

		return priceAdjLineItem;
	}

	// ---------------------------------------------------------------------
	/**
	 * Remove a line from the transaction.
	 * <P>
	 * 
	 * @param index
	 *            Line Item to remove
	 **/
	// ---------------------------------------------------------------------
	public void removeLineItem(int index) {
		itemProxy.removeLineItem(index);
		updateTransactionTotals(getItemContainerProxy().getLineItems(),
				getItemContainerProxy().getTransactionDiscounts(), getItemContainerProxy().getTransactionTax());
	}

	// ---------------------------------------------------------------------
	/**
	 * Add the amount of line void to the transaction.
	 * <P>
	 * 
	 * @param lineItem
	 *            an abstract line item
	 **/
	// ---------------------------------------------------------------------
	public void incrementLineVoid(AbstractTransactionLineItemIfc lineItem) {
		itemProxy.incrementLineVoid(lineItem);
	}

	// ---------------------------------------------------------------------
	/**
	 * Remove multiple lines from the transaction.
	 * <P>
	 * 
	 * @param indices
	 *            Indeces to remove
	 **/
	// ---------------------------------------------------------------------
	public void removeLineItems(int[] indices) {
		itemProxy.removeLineItems(indices);
		updateTransactionTotals(getItemContainerProxy().getLineItems(),
				getItemContainerProxy().getTransactionDiscounts(), getItemContainerProxy().getTransactionTax());
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieve clone of a line item from a transaction.
	 * <P>
	 * 
	 * @param index
	 *            index into line item vector
	 * @return line item object
	 **/
	// ---------------------------------------------------------------------
	public AbstractTransactionLineItemIfc retrieveItemByIndex(int index) {
		return (itemProxy.retrieveItemByIndex(index));
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieve of a line item from a transaction.
	 * <P>
	 * 
	 * @param lineItemNumber
	 *            item number
	 * @return line item object
	 **/
	// ---------------------------------------------------------------------
	public AbstractTransactionLineItemIfc retrieveLineItemByID(int lineItemNumber) {
		return (itemProxy.retrieveLineItemByID(lineItemNumber));
	}

	/**
	 * Retreives the PriceAdjustmentLineItemIfc instance that matches the
	 * provided reference, if available
	 * 
	 * @param priceAdjReference
	 *            The reference by which to search
	 * @return A memory reference of the PriceAdjustmentLineItemIfc instance
	 *         that matches the provided reference, if available
	 */
	public PriceAdjustmentLineItemIfc retrievePriceAdjustmentByReference(int priceAdjReference) {
		return (itemProxy.retrievePriceAdjustmentByReference(priceAdjReference));
	}

	// ---------------------------------------------------------------------
	/**
	 * Replace a line item in a transaction and in all best deal groups that
	 * contain it.
	 * <P>
	 * 
	 * @param lineItem
	 *            lineItem to replace the old one with
	 * @param index
	 *            Index to do the replacement at
	 **/
	// ---------------------------------------------------------------------
	public void replaceLineItem(AbstractTransactionLineItemIfc lineItem, int index) {
		itemProxy.replaceLineItem(lineItem, index);
		updateTransactionTotals(getItemContainerProxy().getLineItems(),
				getItemContainerProxy().getTransactionDiscounts(), getItemContainerProxy().getTransactionTax());
	}

	// ---------------------------------------------------------------------
	/**
	 * Calculates and returns the sum of any advanced pricing discounts applied
	 * for this transaction. This total includes any store coupon discounts
	 * calculated using the best deal calculation.
	 * <P>
	 * 
	 * @return CurrencyIfc total
	 **/
	// ---------------------------------------------------------------------
	public CurrencyIfc getAdvancedPricingDiscountTotal() {
		return (itemProxy.getAdvancedPricingDiscountTotal());
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the best deal winners.
	 * 
	 * @return list of best deal winners
	 **/
	// ---------------------------------------------------------------------
	public ArrayList getBestDealWinners() {
		return itemProxy.getBestDealWinners();
	}

	// ---------------------------------------------------------------------
	/**
	 * Calculates and applies the best deal discounts for advanced pricing
	 * rules.
	 **/
	// ---------------------------------------------------------------------
	public void calculateBestDeal() {
		itemProxy.calculateBestDeal();
		updateTransactionTotals(getItemContainerProxy().getLineItems(),
				getItemContainerProxy().getTransactionDiscounts(), getItemContainerProxy().getTransactionTax());
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns a boolean indicating whether the line item is a source for any
	 * advanced pricing rule held by this transaction.
	 * 
	 * @param source
	 *            The source to check for discounts
	 * @return boolean true if source can be used, false otherwise
	 **/
	// ---------------------------------------------------------------------
	public boolean isPotentialSource(DiscountSourceIfc source) {
		return (itemProxy.isPotentialSource(source));
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns a boolean indicating whether the line item is a target for any
	 * advanced pricing rule held by this transaction.
	 * <P>
	 * 
	 * @param target
	 *            Item to check for advanced pricing rule
	 * @return boolean true if target can be used, false otherwise
	 **/
	// ---------------------------------------------------------------------
	public boolean isPotentialTarget(DiscountTargetIfc target) {
		return (itemProxy.isPotentialTarget(target));
	}

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves line items array in vector form.
	 * <P>
	 * 
	 * @return line item vector for this transaction
	 **/
	// ----------------------------------------------------------------------------
	public Vector getLineItemsVector() {
		return itemProxy.getLineItemsVector();
	}

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves iterator for line items array.
	 * <P>
	 * 
	 * @return iterator for line items array
	 **/
	// ----------------------------------------------------------------------------
	public Iterator getLineItemsIterator() {
		return itemProxy.getLineItemsIterator();
	}

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves line items array.
	 * <P>
	 * 
	 * @return line items for this transaction
	 **/
	// ----------------------------------------------------------------------------
	public AbstractTransactionLineItemIfc[] getLineItems() {
		return itemProxy.getLineItems();
	}

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves a Collection containing any KitHeaderLineItemIfcs that are
	 * associated with this transaction.
	 * <P>
	 * 
	 * @return Collection of KitHeaderLineItemIfcs
	 **/
	// ----------------------------------------------------------------------------
	public Collection getKitHeaderLineItems() {
		return itemProxy.getKitHeaderLineItems();
	}

	// ----------------------------------------------------------------------------
	/**
	 * Checks to see if any line items in this transaction have been flagged to
	 * print a gift receipt. If so, a flag is set for the transaction to signify
	 * that this transaction has items that need a gift receipt. @ return
	 * boolean gift receipt flag
	 **/
	// ----------------------------------------------------------------------------
	public boolean hasGiftReceiptItems() {
		boolean giftReceipt = false;
		Iterator i = itemProxy.getLineItemsIterator();

		while (i.hasNext()) {
			if (((SaleReturnLineItemIfc) i.next()).isGiftReceiptItem()) {
				giftReceipt = true;
				break;
			}
		}
		return giftReceipt;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Checks to see if any line items in this transaction are linked to a gift
	 * registry. If so, a flag is set for the transaction to signify that this
	 * transaction has items that are linked to a gift registry. @ return
	 * boolean gift registry flag
	 **/
	// ----------------------------------------------------------------------------
	public boolean hasGiftRegistryItems() {
		boolean giftRegistry = false;
		Iterator i = itemProxy.getLineItemsIterator();

		while (i.hasNext()) {
			SaleReturnLineItemIfc item = (SaleReturnLineItemIfc) i.next();
			if (item.getRegistry() != null) {
				giftRegistry = true;
				break;
			}
		}
		return giftRegistry;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Checks to see if any line items in this transaction are alteration items.
	 * If so, a flag is set for the transaction to signify that this transaction
	 * has items that need alteration receipts. @ return boolean alteration flag
	 **/
	// ----------------------------------------------------------------------------
	public boolean hasAlterationItems() {
		boolean alteration = false;
		Iterator i = itemProxy.getLineItemsIterator();
		SaleReturnLineItemIfc item = null;

		while (i.hasNext()) {
			item = (SaleReturnLineItemIfc) i.next();
			if (item.isSaleLineItem() && item.isAlterationItem()) {
				alteration = true;
				break;
			}
		}
		return alteration;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Checks to see if any line items in this transaction have been flagged as
	 * send items. If so, a flag is set for the transaction to signify that this
	 * transaction has items that need to be send. @ return boolean send flag
	 **/
	// ----------------------------------------------------------------------------
	public boolean hasSendItems() {
		boolean send = false;
		Iterator i = itemProxy.getLineItemsIterator();
		SaleReturnLineItemIfc item = null;

		while (i.hasNext()) {
			item = (SaleReturnLineItemIfc) i.next();
			if (item.isSaleLineItem() && item.getItemSendFlag()) {
				send = true;
				break;
			}
		}
		return send;
	}

	// ----------------------------------------------------------------------
	/**
	 * This method checks to see if a customer is present during a send
	 * transaction
	 * 
	 * @return
	 **/
	// ----------------------------------------------------------------------
	public boolean isCustomerPresentDuringSend() {
		boolean present = false;

		/*
		 * This method should be completed after the send service is reworked.
		 * The send service will ask if the customer is present or not. This
		 * method should check that whereever it is implemented.
		 */

		return present;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves a subset of line items excluding those of the type passed in as
	 * an argument. This method is used for kit item display and manipulation.
	 * 
	 * @param type
	 *            an int indicating the type of line item to exclude. Valid
	 *            types are declared in ItemKitConstantsIfc
	 * @see com.extendyourstore.domain.stock.ItemKitConstantsIfc
	 * @return an array containing all line items from this transaction
	 *         excluding those of type
	 **/
	// ----------------------------------------------------------------------------
	public SaleReturnLineItemIfc[] getLineItemsExcluding(int type) {
		return (itemProxy.getLineItemsExcluding(type));
	}

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves a subset of line items excluding those of the type passed in as
	 * an argument. This method is used for kit item display and manipulation.
	 * 
	 * @param type
	 *            an int indicating the type of line item to exclude. Valid
	 *            types are declared in ItemKitConstantsIfc
	 * @see com.extendyourstore.domain.stock.ItemKitConstantsIfc
	 * @return an array containing all line items from this transaction
	 *         excluding those of type
	 **/
	// ----------------------------------------------------------------------------
	public SaleReturnLineItemIfc[] getSaleLineItemsExcluding(int type) {
		return (itemProxy.getSaleLineItemsExcluding(type));
	}

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves a subset of line items excluding those of the type passed in as
	 * an argument. This method is used for kit item display and manipulation.
	 * 
	 * @param type
	 *            an int indicating the type of line item to exclude. Valid
	 *            types are declared in ItemKitConstantsIfc
	 * @see com.extendyourstore.domain.stock.ItemKitConstantsIfc
	 * @return an array containing all line items from this transaction
	 *         excluding those of type
	 **/
	// ----------------------------------------------------------------------------
	public SaleReturnLineItemIfc[] getReturnLineItemsExcluding(int type) {
		return (itemProxy.getReturnLineItemsExcluding(type));
	}

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves a subset of price adjustment line items excluding those of the
	 * type passed in as an argument. This method is used for kit item display
	 * and manipulation.
	 * 
	 * @param type
	 *            an int indicating the type of line item to exclude. Valid
	 *            types are declared in ItemKitConstantsIfc
	 * @see com.extendyourstore.domain.stock.ItemKitConstantsIfc
	 * @return an array containing all line items from this transaction
	 *         excluding those of type
	 **/
	// ----------------------------------------------------------------------------
	public SaleReturnLineItemIfc[] getPriceAdjustmentLineItemsExcluding(int type) {
		return (itemProxy.getPriceAdjustmentLineItemsExcluding(type));
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves the line items that are part of the group passed as the
	 * argument.
	 * <P>
	 * 
	 * @param prodGroupID
	 *            product group identifier
	 * @return array of SaleReturnLineItemIfc[]
	 **/
	// ---------------------------------------------------------------------
	public SaleReturnLineItemIfc[] getProductGroupLineItems(String prodGroupID) {
		return (itemProxy.getProductGroupLineItems(prodGroupID));
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets the line items array
	 * 
	 * @param items
	 *            array of AbstractTransactionLineItemIfc references
	 **/
	// ---------------------------------------------------------------------
	public void setLineItems(AbstractTransactionLineItemIfc[] items) {
		itemProxy.setLineItems(items);
		updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(),
				itemProxy.getTransactionTax());
	}

	// ---------------------------------------------------------------------
	/**
	 * Check line quantities and reset transaction type, if needed.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	protected void resetTransactionType() {
		// set default type
		int type = TransactionIfc.TYPE_SALE;
		// Only used in case of trans with grandtotal of zero and contains all
		// return items,
		// otherwise, goes ignored
		boolean allReturnItems = true;
		// get items
		AbstractTransactionLineItemIfc[] items = getLineItems();
		int numberItems = 0;
		if (items != null) {
			numberItems = items.length;
		}
		for (int i = 0; i < numberItems; i++) { // begin walk line items

			// If item is SaleReturnLineItem
			if (items[i] instanceof SaleReturnLineItemIfc) {
				SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) items[i];
				if (srli.isReturnLineItem()) {
					// if quantity negative and total negative,
					// set for return and exit
					if (totals.getGrandTotal().signum() == CurrencyIfc.NEGATIVE) {
						type = TransactionIfc.TYPE_RETURN;
						break;
					}
					// if total is zero and trans contains all return items,
					// then set
					// type to RETURN, otherwise, we conclude it's an exchange
					// trans
					else if (totals.getGrandTotal().signum() == CurrencyIfc.ZERO) {
						if (i + 1 == numberItems && allReturnItems) {
							type = TransactionIfc.TYPE_RETURN;
						}
					}
				} else {
					allReturnItems = false;
				}
			}
		} // end walk line items

		// set transaction type
		setTransactionType(type);
	}

	// ---------------------------------------------------------------------
	/**
	 * Adds a transaction discount and updates the transaction totals.
	 * <P>
	 * 
	 * @param disc
	 *            TransactionDiscountStrategyIfc
	 **/
	// ---------------------------------------------------------------------
	public void addTransactionDiscount(TransactionDiscountStrategyIfc disc) {
		if(disc!=null)
		{
			addTransactionDiscount(disc, true);
		}
	}

	// ---------------------------------------------------------------------
	/**
	 * Adds a transaction discount and optionally updates the transaction
	 * totals.
	 * <P>
	 * 
	 * @param disc
	 *            TransactionDiscountStrategyIfc
	 * @param doUpdate
	 *            flag indicating transaction totals should be updated
	 **/
	// ---------------------------------------------------------------------

	// izhar
	public void addTransactionDiscount(TransactionDiscountStrategyIfc disc, boolean doUpdate) { // begin
																								// addTransactionDiscount()
		String comp = null;
		if(disc!=null)
		{
		if (disc.getReasonCode() == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NatZ$offTiered)
			comp = disc.getDiscountAmount().getStringValue();
		else if (disc.getReasonCode() == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NatZPctoffTiered)
			comp = disc.getDiscountRate().toString();
		
		if (!("0.00").equals(comp)) // gaurav
			itemProxy.addTransactionDiscount(disc);
		}
		if (doUpdate) {
			// update totals
			updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(),
					itemProxy.getTransactionTax());
		}
	} // end addTransactionDiscount()

	// ---------------------------------------------------------------------
	/**
	 * Clears transaction discounts by percentage.
	 * <P>
	 * 
	 * @param doTotals
	 *            flag indicating totals should be updated
	 **/
	// ---------------------------------------------------------------------
	public void clearTransactionDiscountsByPercentage(boolean doTotals) { // begin
																			// clearTransactionDiscountsByPercentage()
		itemProxy.clearTransactionDiscountsByPercentage();
		// check to update totals
		if (doTotals) {
			// update totals
			updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(),
					itemProxy.getTransactionTax());
		}
	} // end clearTransactionDiscountsByPercentage()

	// ---------------------------------------------------------------------
	/**
	 * Removes Preferred Customer discounts that were added to the list of
	 * AdvancedPricingRules.
	 **/
	// ---------------------------------------------------------------------
	public void clearCustomerBestDealDiscounts() {
		itemProxy.clearCustomerBestDealDiscounts();
	}

	// ---------------------------------------------------------------------
	/**
	 * Clears transaction discounts by percentage.
	 * <P>
	 * 
	 * @param doTotals
	 *            flag indicating totals should be updated
	 **/
	// ---------------------------------------------------------------------
	public void clearCustomerDiscountsByPercentage(boolean doTotals) { // begin
																		// clearCustomerDiscountsByPercentage()
		itemProxy.clearCustomerDiscountsByPercentage();
		// check to update totals
		if (doTotals) {
			updateTransactionTotals(getLineItems(), getTransactionDiscounts(), getTransactionTax());
		}
	} // end clearCustomerDiscountsByPercentage()

	// ---------------------------------------------------------------------
	/**
	 * Sets enabled flag on transaction discounts by percentage to specified
	 * value. CustomerDiscountByPercentage discounts are not affected.
	 * <P>
	 * 
	 * @param enableFlag
	 *            desired setting of enable flag
	 * @param doTotals
	 *            flag indicating totals should be updated
	 **/
	// ---------------------------------------------------------------------
	public void enableTransactionDiscountsByPercentage(boolean enableFlag, boolean doTotals) { // begin
																								// enableTransactionDiscountsByPercentage()
		itemProxy.enableTransactionDiscountsByPercentage(enableFlag);
		// check to update totals
		if (doTotals) {
			// update totals
			updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(),
					itemProxy.getTransactionTax());
		}
	} // end enableTransactionDiscountsByPercentage()

	// ---------------------------------------------------------------------
	/**
	 * Sets enabled flag on customer transaction discounts by percentage to
	 * specified value.
	 * <P>
	 * 
	 * @param enableFlag
	 *            desired setting of enable flag
	 * @param doTotals
	 *            flag indicating totals should be updated
	 **/
	// ---------------------------------------------------------------------
	public void enableCustomerDiscountsByPercentage(boolean enableFlag, boolean doTotals) { // begin
																							// enableCustomerDiscountsByPercentage()
		itemProxy.enableCustomerDiscountsByPercentage(enableFlag);
		// check to update totals
		if (doTotals) {
			// update totals
			updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(),
					itemProxy.getTransactionTax());
		}
	} // end enableCustomerDiscountsByPercentage()

	// ---------------------------------------------------------------------
	/**
	 * Clears transaction discounts by percentage, but does not recalculate
	 * toatls.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public void clearTransactionDiscountsByPercentage() {
		itemProxy.clearTransactionDiscountsByPercentage();
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves array of transaction discounts by percentage.
	 * <P>
	 * 
	 * @return array of disc transaction discount objects, null if not found
	 **/
	// ---------------------------------------------------------------------
	public TransactionDiscountStrategyIfc[] getTransactionDiscountsByPercentage() { // begin
																					// getTransactionDiscountsByPercentage()
		return (itemProxy.getTransactionDiscountsByPercentage());
	} // end getTransactionDiscountsByPercentage()

	// ---------------------------------------------------------------------
	/**
	 * Sets array of transaction discounts by percentage and re-calculates the
	 * totals.
	 * <P>
	 * 
	 * @param value
	 *            array of disc transaction discount objects, null if not found
	 **/
	// ---------------------------------------------------------------------
	public void setTransactionDiscountsByPercentage(TransactionDiscountStrategyIfc[] value) { // begin
																								// setTransactionDiscountsByPercentage()
		itemProxy.setTransactionDiscountsByPercentage(value);
		// update totals
		updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(),
				itemProxy.getTransactionTax());
	} // end setTransactionDiscountsByPercentage()

	// ---------------------------------------------------------------------
	/**
	 * Clears transaction discounts by amount.
	 * <P>
	 * 
	 * @param doTotals
	 *            flag indicating totals should be updated
	 **/
	// ---------------------------------------------------------------------
	public void clearTransactionDiscountsByAmount(boolean doTotals) {
		itemProxy.clearTransactionDiscountsByAmount();
		if (doTotals) {
			// update totals
			updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(),
					itemProxy.getTransactionTax());
		}
	}

	// ---------------------------------------------------------------------
	/**
	 * Clears transaction discounts by amount, but does not re-calculate totals.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public void clearTransactionDiscountsByAmount() {
		itemProxy.clearTransactionDiscountsByAmount();
	}

	// ---------------------------------------------------------------------
	/**
	 * Clears specified transaction discounts from the transaction discount
	 * collection and updates the transaction totals.
	 * <P>
	 * 
	 * @param discountMethod
	 *            int discountMethod from DiscountRuleConstantsIfc
	 * @param assignmentBasis
	 *            int assignmentBasis from DiscountRuleConstantsIfc
	 * @see com.extendyourstore.domain.discount.DiscountRuleConstantsIfc
	 **/
	// ---------------------------------------------------------------------
	public void clearTransactionDiscounts(int discountMethod, int assignmentBasis) {
		itemProxy.clearTransactionDiscounts(discountMethod, assignmentBasis);

		updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(),
				itemProxy.getTransactionTax());
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves array containing specified transaction discounts.
	 * <P>
	 * 
	 * @param discountMethod
	 *            from DiscountRuleConstantsIfc
	 * @param assignmentBasis
	 *            from DiscountRuleConstantsIfc
	 * @see com.extendyourstore.domain.discount.DiscountRuleConstantsIfc
	 * @return array of disc transaction discount objects
	 **/
	// ---------------------------------------------------------------------
	public TransactionDiscountStrategyIfc[] getTransactionDiscounts(int discountMethod, int assignmentBasis) {
		return itemProxy.getTransactionDiscounts(discountMethod, assignmentBasis);
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets array of transaction discounts by amount and re-calculates the
	 * totals.
	 * <P>
	 * 
	 * @param value
	 *            of disc transaction discount objects, null if not found
	 **/
	// ---------------------------------------------------------------------
	public void setTransactionDiscountsByAmount(TransactionDiscountStrategyIfc[] value) { // begin
																							// setTransactionDiscountsByAmount()
		itemProxy.setTransactionDiscountsByAmount(value);
		// update totals
		updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(),
				itemProxy.getTransactionTax());

	} // end setTransactionDiscountsByAmount()

	// ---------------------------------------------------------------------
	/**
	 * Retrieves array of transaction discounts by amount.
	 * <P>
	 * 
	 * @return array of disc transaction discount objects, null if not found
	 **/
	// ---------------------------------------------------------------------
	public TransactionDiscountStrategyIfc[] getTransactionDiscountsByAmount() { // begin
																				// getTransactionDiscountsByAmount()
		return (itemProxy.getTransactionDiscountsByAmount());
	} // end getTransactionDiscountsByAmount()

	// ---------------------------------------------------------------------
	/**
	 * Retrieves array of transaction discounts.
	 * <P>
	 * 
	 * @return array of disc transaction discount objects, null if not found
	 **/
	// ---------------------------------------------------------------------
	public TransactionDiscountStrategyIfc[] getTransactionDiscounts() { // begin
																		// getTransactionDiscounts()
		return (itemProxy.getTransactionDiscounts());
	} // end getTransactionDiscounts()

	// ---------------------------------------------------------------------
	/**
	 * Sets array of transaction discounts and re-calculates the totals.
	 * <P>
	 * 
	 * @param value
	 *            array of disc transaction discount objects, null if not found
	 **/
	// ---------------------------------------------------------------------
	public void setTransactionDiscounts(TransactionDiscountStrategyIfc[] value) { // begin
																					// setTransactionDiscounts()
		itemProxy.setTransactionDiscounts(value);
		// update totals
		updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(),
				itemProxy.getTransactionTax());

	} // end setTransactionDiscounts()

	// ---------------------------------------------------------------------
	/**
	 * Adds an array of transaction discounts and re-calculates the totals.
	 * <P>
	 * 
	 * @param value
	 *            of disc transaction discount objects, null if not found
	 **/
	// ---------------------------------------------------------------------
	public void addTransactionDiscounts(TransactionDiscountStrategyIfc[] value) { // begin
																					// addTransactionDiscounts()
		itemProxy.addTransactionDiscounts(value);
		addItemByTaxGroup();
		// update totals
		updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(),
				itemProxy.getTransactionTax());

	} // end addTransactionDiscounts()

	// ---------------------------------------------------------------------
	/**
	 * Clears transaction discounts.
	 * <P>
	 * 
	 * @param doTotals
	 *            flag indicating totals should be updated
	 **/
	// ---------------------------------------------------------------------
	public void clearTransactionDiscounts(boolean doTotals) { // begin
																// clearTransactionDiscounts()
		itemProxy.clearTransactionDiscounts();
		// check to update totals
		if (doTotals) {
			// update totals
			updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(),
					itemProxy.getTransactionTax());
		}
	} // end clearTransactionDiscounts()

	// ---------------------------------------------------------------------
	/**
	 * Adds an advanced pricing rule to the transaction if it is not already
	 * stored in the transaction's advanced pricing rule collection. The key is
	 * the discount rule id.
	 * 
	 * @param rule
	 *            pricing rule to add
	 **/
	// ---------------------------------------------------------------------
	public void addAdvancedPricingRule(AdvancedPricingRuleIfc rule) {
		itemProxy.addAdvancedPricingRule(rule);
	}

	// ----------------------------------------------------------------------
	/**
	 * Adds AdvancedPricing rules to the transaction.
	 * <P>
	 * 
	 * @param rules
	 *            containing advancedPricingRuleIfcs
	 **/
	// ----------------------------------------------------------------------
	public void addAdvancedPricingRules(AdvancedPricingRuleIfc[] rules) {
		itemProxy.addAdvancedPricingRules(rules);
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns an array of the advanced pricing rules for this transaction.
	 * 
	 * @return AdvancedPricingRuleIfc[]
	 **/
	// ---------------------------------------------------------------------
	public AdvancedPricingRuleIfc[] getAdvancedPricingRules() {
		return (itemProxy.getAdvancedPricingRules());
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns an iterator over the advanced pricing rules for this transaction.
	 * 
	 * @return Iterator
	 **/
	// ---------------------------------------------------------------------
	public Iterator advancedPricingRules() {
		return itemProxy.advancedPricingRules();
	}

	// ---------------------------------------------------------------------
	/**
	 * Clears the advanced pricing rules from the transaction.
	 **/
	// ---------------------------------------------------------------------
	public void clearAdvancedPricingRules() {
		itemProxy.clearAdvancedPricingRules();
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns a boolean indicating whether this transaction has advanced
	 * pricing rules.
	 * 
	 * @return boolean
	 **/
	// ---------------------------------------------------------------------
	public boolean hasAdvancedPricingRules() {
		return !(itemProxy.hasAdvancedPricingRules());
	}

	// ---------------------------------------------------------------------
	/**
	 * Adds a best deal group to the transaction.
	 * 
	 * @param group
	 *            BestDealGroup to add
	 **/
	// ---------------------------------------------------------------------
	public void addBestDealGroup(BestDealGroupIfc group) {
		itemProxy.addBestDealGroup(group);
	}

	// ---------------------------------------------------------------------
	/**
	 * Adds a best deal group to the transaction.
	 * 
	 * @param groups
	 *            list of BestDealGroups to add
	 **/
	// ---------------------------------------------------------------------
	public void addBestDealGroups(ArrayList groups) {
		itemProxy.addBestDealGroups(groups);
	}

	// ---------------------------------------------------------------------
	/**
	 * Removes any advanced pricing discounts currently applied to the targets
	 * within the transaction. Calling this method does not have any effect on
	 * the transaction totals.
	 **/
	// ---------------------------------------------------------------------
	public void clearBestDealDiscounts() {
		itemProxy.clearBestDealDiscounts();
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns an array list of DiscountSourceIfcs (objects that are eligible
	 * sources) for an advanced pricing rule. The sources are retrieved from the
	 * line items vector.
	 * 
	 * @return ArrayList containing sources eligible for a rule
	 * @see com.extendyourstore.domain.discount.DiscountSourceIfc
	 **/
	// ---------------------------------------------------------------------
	public ArrayList getDiscountSources() {
		return (itemProxy.getDiscountSources());
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns an array list of available DiscountTargetIfcs (objects that are
	 * eligible targets) for an advanced pricing rule. The targets are retrieved
	 * from the transaction's line items vector.
	 * 
	 * @return ArrayList containing targets eligible for a rule
	 * @see com.extendyourstore.domain.discount.DiscountTargetIfc
	 **/
	// ---------------------------------------------------------------------
	public ArrayList getDiscountTargets() {
		return (itemProxy.getDiscountTargets());
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves size of lineItemsVector vector.
	 * <P>
	 * 
	 * @return line items vector size
	 **/
	// ---------------------------------------------------------------------
	public int getLineItemsSize() {
		return itemProxy.getLineItemsSize();
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves sales associate.
	 * <P>
	 * 
	 * @return sales associate
	 **/
	// ---------------------------------------------------------------------
	public EmployeeIfc getSalesAssociate() {
		return (itemProxy.getSalesAssociate());
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves sales associate identifier.
	 * <P>
	 * 
	 * @return sales associate identifier
	 **/
	// ---------------------------------------------------------------------
	public String getSalesAssociateID() {
		return (itemProxy.getSalesAssociateID());
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets sales associate modified flag.
	 * <P>
	 * 
	 * @param value
	 *            modified flag
	 **/
	// ---------------------------------------------------------------------
	public void setSalesAssociateModifiedFlag(boolean value) {
		salesAssociateModifiedFlag = value;
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves sales associate-modified flag.
	 * <P>
	 * 
	 * @return sales associate-modified flag
	 **/
	// ---------------------------------------------------------------------
	public boolean getSalesAssociateModifiedFlag() {
		return (salesAssociateModifiedFlag);
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets salesAssociate attribute.
	 * <P>
	 * 
	 * @param emp
	 *            salesAssociate
	 **/
	// ---------------------------------------------------------------------
	public void setSalesAssociate(EmployeeIfc emp) {
		itemProxy.setSalesAssociate(emp);

	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves default registry identifier.
	 * <P>
	 * 
	 * @return default registry identifier
	 **/
	// ---------------------------------------------------------------------
	public RegistryIDIfc getDefaultRegistry() {
		return (itemProxy.getDefaultRegistry());
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets defaultRegistry attribute.
	 * <P>
	 * 
	 * @param value
	 *            default registry
	 **/
	// ---------------------------------------------------------------------
	public void setDefaultRegistry(RegistryIDIfc value) { // begin
															// setDefaultRegistry()
		itemProxy.setDefaultRegistry(value);
	} // end setDefaultRegistry()

	// ---------------------------------------------------------------------
	/**
	 * Sets customer attribute and performs other operations associated with
	 * assigning a customer to a transaction, such as setting discount rules.
	 * <P>
	 * 
	 * @param value
	 *            customer
	 **/
	// ---------------------------------------------------------------------
	public void linkCustomer(CustomerIfc value) { // begin linkCustomer()
													// set customer reference
		setCustomer(value);
		itemProxy.linkCustomer(value);
		updateTransactionTotals(getItemContainerProxy().getLineItems(),
				getItemContainerProxy().getTransactionDiscounts(), getItemContainerProxy().getTransactionTax());
	} // end linkCustomer()

	// ---------------------------------------------------------------------
	/**
	 * Returns the discount strategy used to generate the preferred customer
	 * discount.
	 * 
	 * @param includeDealDiscounts
	 *            indicating whether to include customer discounts that are part
	 *            of best deal
	 * @return DiscountRuleIfc[] containing discounts assigned by customer
	 **/
	// ---------------------------------------------------------------------
	public DiscountRuleIfc[] getPreferredCustomerDiscounts(boolean includeDealDiscounts) {
		return itemProxy.getPreferredCustomerDiscounts(includeDealDiscounts);
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves tax object.
	 * <P>
	 * 
	 * @return tax object
	 **/
	// ---------------------------------------------------------------------
	public TransactionTaxIfc getTransactionTax() { // begin getTransactionTax()
		return (itemProxy.getTransactionTax());
	} // end getTransactionTax()

	// ---------------------------------------------------------------------
	/**
	 * Sets tax attribute.
	 * <P>
	 * 
	 * @param value
	 *            tax
	 **/
	// ---------------------------------------------------------------------
	public void setTransactionTax(TransactionTaxIfc value) { // begin
																// setTransactionTax()
		itemProxy.setTransactionTax(value);
	} // end setTransactionTax()

	// ---------------------------------------------------------------------
	/**
	 * Set tax exempt.
	 * <P>
	 * 
	 * @param cert
	 *            tax exempt certificate identifier
	 * @param reason
	 *            reason code
	 **/
	// ---------------------------------------------------------------------
	public void setTaxExempt(String cert, int reason) { // begin setTaxExempt()
		itemProxy.setTaxExempt(cert, reason);
		// update totals
		updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(),
				itemProxy.getTransactionTax());
	} // end setTaxExempt()

	// ---------------------------------------------------------------------
	/**
	 * Clear tax exempt.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public void clearTaxExempt() { // begin clearTaxExempt()
		itemProxy.clearTaxExempt();
		// update totals
		updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(),
				itemProxy.getTransactionTax());
	} // end clearTaxExempt()

	// ---------------------------------------------------------------------
	/**
	 * Set transaction to nontaxable.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public void setNonTaxable() { // begin setNonTaxable()
									// Set the transaction tax to Non Taxable
		TransactionTaxIfc tt = getTransactionTax();
		tt.setTaxExempt(null, -1);
		tt.setTaxMode(TaxIfc.TAX_MODE_NON_TAXABLE);

		// Set all the item taxes to Non Taxable
		AbstractTransactionLineItemIfc[] lineItems = itemProxy.getLineItems();
		for (int i = 0; i < lineItems.length; i += 1) {
			if (lineItems[i] instanceof SaleReturnLineItemIfc) {
				((SaleReturnLineItemIfc) lineItems[i]).getItemTax().setTaxable(false);
			}
		}

		// update totals
		updateTransactionTotals(lineItems, itemProxy.getTransactionDiscounts(), tt);
	}

	// ---------------------------------------------------------------------
	/**
	 * Override tax rate.
	 * <P>
	 * 
	 * @param newRate
	 *            new tax rate
	 * @param updateAllItemsFlag
	 *            flag indicating all items should be updated
	 * @param reason
	 *            reasonCode
	 **/
	// ---------------------------------------------------------------------
	public void overrideTaxRate(double newRate, boolean updateAllItemsFlag, int reason) { // begin
																							// overrideTaxRate()
		itemProxy.overrideTaxRate(newRate, updateAllItemsFlag, reason);
		// update totals
		updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(),
				itemProxy.getTransactionTax());
	} // end overrideTaxRate()

	// ---------------------------------------------------------------------
	/**
	 * Override tax amount.
	 * <P>
	 * 
	 * @param newAmount
	 *            new tax amount
	 * @param updateAllItemsFlag
	 *            flag indicating all items should be updated
	 * @param reason
	 *            reasonCode
	 **/
	// ---------------------------------------------------------------------
	public void overrideTaxAmount(CurrencyIfc newAmount, boolean updateAllItemsFlag, int reason) { // begin
																									// overrideTaxAmount()
		itemProxy.overrideTaxAmount(newAmount, updateAllItemsFlag, reason);
		// update totals
		updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(),
				itemProxy.getTransactionTax());
	} // end overrideTaxAmount()

	// ---------------------------------------------------------------------
	/**
	 * Clear tax override.
	 * <P>
	 * 
	 * @param updateAllItemsFlag
	 *            flag indicating all items should be updated
	 **/
	// ---------------------------------------------------------------------
	public void clearTaxOverride(boolean updateAllItemsFlag) { // begin
																// clearTaxOverride()
		itemProxy.clearTaxOverride(updateAllItemsFlag);
		// update totals
		updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(),
				itemProxy.getTransactionTax());
	} // end clearTaxOverride()

	// ---------------------------------------------------------------------
	/**
	 * Set item tax objects based on transaction tax object's value.
	 * <P>
	 * 
	 * @param updateAllItemsFlag
	 *            flag indicating all items should be updated
	 **/
	// ---------------------------------------------------------------------
	public void setTransactionTaxOnItems(boolean updateAllItemsFlag) { // begin
																		// setTransactionTaxOnItems()
		itemProxy.setTransactionTaxOnItems(updateAllItemsFlag);
	} // end setTransactionTaxOnItems()

	// ---------------------------------------------------------------------
	/**
	 * Retrieves tender display transaction totals. In this case, these are the
	 * same as the standard transaction totals.
	 * <P>
	 * 
	 * @return tender display transaction totals
	 **/
	// ---------------------------------------------------------------------
	public TransactionTotalsIfc getTenderTransactionTotals() { // begin
																// setTenderDisplayTransactionTotals()
		return (getTransactionTotals());
	} // end setTenderDisplayTransactionTotals()

	// ---------------------------------------------------------------------
	/**
	 * Updates transaction totals and resets transaction type.
	 * <P>
	 * 
	 * @param lineItems
	 *            array of line items
	 * @param discounts
	 *            array of transaction discounts
	 * @param tax
	 *            transaction tax object
	 **/
	// ---------------------------------------------------------------------
	// izhar
	// ---------------------------------------------------------------------
	protected void updateTransactionTotals(AbstractTransactionLineItemIfc[] lineItems,
			TransactionDiscountStrategyIfc[] discounts, TransactionTaxIfc tax) {
		// Changes for Rev 1.6 : Starts
		/*if (this.getRounding() != null) {
			((MAXSaleReturnTransaction) totals).setRounding(this.getRounding());
			((MAXSaleReturnTransaction) totals).setRoundingDenominations(this.getRoundingDenominations());
		}*/
		// Changes for Rev 1.6 : Ends
		if (discounts != null && (discounts.length >= 1)) {
			int size = discounts.length;
			List arl = new ArrayList();
			List arl1 = new ArrayList();
			for (int i = 0; i < size; i++) {
				if ((discounts[i]).getReasonCode() == 20) {
					arl.add(discounts[i].getDiscountAmount());
				} else if ((discounts[i])
						.getReasonCode() == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NatZPctoffTiered) {
					arl1.add(discounts[i].getDiscountRate());
				}
			}
		}
		totals.updateTransactionTotals(lineItems, discounts, tax);
		resetTransactionType();
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves size of tenderLineItemsVector vector.
	 * <P>
	 * 
	 * @return tender line items vector size
	 **/
	// ---------------------------------------------------------------------
	public int getTenderLineItemsSize() {
		return tenderLineItemsVector.size();
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves OrderID descriptor string.
	 * <P>
	 * 
	 * @return orderID
	 **/
	// ---------------------------------------------------------------------
	public String getOrderID() {
		return orderID;
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets OrderID descriptor string.
	 * <P>
	 * 
	 * @param id
	 *            Id to set
	 **/
	// ---------------------------------------------------------------------
	public void setOrderID(String id) {
		orderID = id;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves amount of line voids (deleted lines).
	 * <P>
	 * 
	 * @return amount of line voids (deleted lines)
	 **/
	// ----------------------------------------------------------------------------
	public CurrencyIfc getAmountLineVoids() { // begin getAmountLineVoids()
		return (itemProxy.getAmountLineVoids());
	} // end getAmountLineVoids()

	// ----------------------------------------------------------------------------
	/**
	 * Sets amount of line voids (deleted lines).
	 * <P>
	 * 
	 * @param value
	 *            amount of line voids (deleted lines)
	 **/
	// ----------------------------------------------------------------------------
	public void setAmountLineVoids(CurrencyIfc value) { // begin
														// setAmountLineVoids()
		itemProxy.setAmountLineVoids(value);
	} // end setAmountLineVoids()

	// ----------------------------------------------------------------------------
	/**
	 * Adds to amount of line voids (deleted lines).
	 * <P>
	 * 
	 * @param value
	 *            increment amount of line voids (deleted lines)
	 **/
	// ----------------------------------------------------------------------------
	public void addAmountLineVoids(CurrencyIfc value) { // begin
														// addAmountLineVoids()
		itemProxy.addAmountLineVoids(value);
	} // end addAmountLineVoids()

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves units on line voids (deleted lines).
	 * <P>
	 * 
	 * @return units on line voids (deleted lines)
	 **/
	// ----------------------------------------------------------------------------
	public BigDecimal getUnitsLineVoids() { // begin getUnitsLineVoids()
		return (itemProxy.getUnitsLineVoids());
	} // end getUnitsLineVoids()

	// ----------------------------------------------------------------------------
	/**
	 * Sets units on line voids (deleted lines).
	 * <P>
	 * 
	 * @param value
	 *            units on line voids (deleted lines)
	 **/
	// ----------------------------------------------------------------------------
	public void setUnitsLineVoids(BigDecimal value) { // begin
														// setUnitsLineVoids()
		itemProxy.setUnitsLineVoids(value);
	} // end setUnitsLineVoids()

	// ----------------------------------------------------------------------------
	/**
	 * Adds to units on line voids (deleted lines).
	 * <P>
	 * 
	 * @param value
	 *            increment units on line voids (deleted lines)
	 **/
	// ----------------------------------------------------------------------------
	public void addUnitsLineVoids(BigDecimal value) { // begin
														// addUnitsLineVoids()
		itemProxy.addUnitsLineVoids(value);
	} // end addUnitsLineVoids()

	// ----------------------------------------------------------------------------
	/**
	 * Gets the deleted line items.
	 * <P>
	 * 
	 * @return Vector contains the a list of deleted line items.
	 **/
	// ----------------------------------------------------------------------------
	public Vector getDeletedLineItems() {
		return deletedLineItems;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Add a deleted line item into the deletedLineItems vector.
	 * <P>
	 * 
	 * @param lineItems
	 *            contains the list of deleted line item information.
	 **/
	// ----------------------------------------------------------------------------
	public void addDeletedLineItems(SaleReturnLineItemIfc lineItems) {
		deletedLineItems.addElement(lineItems);
	}

	// ----------------------------------------------------------------------------
	/**
	 * Get the items by a tax group.
	 * <P>
	 * 
	 * @return hashtable
	 **/
	// ----------------------------------------------------------------------------
	public Hashtable getItemsByTaxGroup() {
		return itemProxy.getItemsByTaxGroup();
	}

	// ----------------------------------------------------------------------------
	/**
	 * Set the items by a tax group hashtable.
	 * <P>
	 * 
	 * @param ht
	 *            hashtable
	 **/
	// ----------------------------------------------------------------------------
	public void setItemsByTaxGroup(Hashtable ht) {
		itemProxy.setItemsByTaxGroup(ht);
	}

	// ----------------------------------------------------------------------------
	/**
	 * add items to the hashtable by tax group.
	 * <P>
	 **/
	// ----------------------------------------------------------------------------
	public void addItemByTaxGroup() {
		itemProxy.addItemByTaxGroup();
	}

	// ----------------------------------------------------------------------------
	/**
	 * Remove an item from the tax group - item paired hashtable.
	 * <P>
	 * 
	 * @param item
	 *            SaleReturnLineItemIfc
	 **/
	// ----------------------------------------------------------------------------
	public void removeItemByTaxGroup(SaleReturnLineItemIfc item) {
		itemProxy.removeItemByTaxGroup(item);
	}

	// ----------------------------------------------------------------------------
	/**
	 * Compute tax for all line items
	 * 
	 * @deprecated as of Release 7.0
	 **/
	// ----------------------------------------------------------------------------
	public void doTaxCalculationForAllLineItems() {
	}

	// ----------------------------------------------------------------------------
	/**
	 * Update an item in the tax group - item paired hashtable. This method will
	 * be used during tax override.
	 * <P>
	 * 
	 * @param item
	 *            SaleReturnLineItemIfc
	 **/
	// ----------------------------------------------------------------------------
	public void updateItemByTaxGroup(SaleReturnLineItemIfc item) {
		itemProxy.updateItemByTaxGroup(item);
		/*
		 * Vector v = new Vector(); Vector vNew = new Vector();
		 * SaleReturnLineItemIfc srli = null;
		 * 
		 * if
		 * (itemsByTaxGroup.containsKey(String.valueOf(item.getTaxGroupID()))) {
		 * // For an existing tax group, retrieve its item vector from // the
		 * hashtable, modify the item in the vector and put back to // the
		 * hashtable. v =
		 * (Vector)itemsByTaxGroup.get(String.valueOf(item.getTaxGroupID()));
		 * 
		 * // If there is only one line item for a specific tax group. erase it
		 * from the hashtable. if (v.size() > 0) { for (int i=0; i< v.size();
		 * i++) { if (((SaleReturnLineItemIfc)v.elementAt(i)) != null) { srli =
		 * (SaleReturnLineItemIfc)v.elementAt(i); if
		 * ((LocaleUtilities.compareValues(srli.getItemID(), item.getItemID())
		 * == 0) && (srli.getLineNumber() == item.getLineNumber())) { srli =
		 * item; } } vNew.addElement(srli); } }
		 * itemsByTaxGroup.remove(String.valueOf(item.getTaxGroupID()));
		 * itemsByTaxGroup.put(String.valueOf(item.getTaxGroupID()), vNew);
		 * 
		 * } else { // Do nothing }
		 */
	}

	// ----------------------------------------------------------------------------
	/**
	 * Offer a public access of updating transaction totals.
	 * <P>
	 **/
	// ----------------------------------------------------------------------------
	public void updateTransactionTotals() {
		this.addItemByTaxGroup();
		this.doTaxCalculationForAllLineItems();
		this.updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(),
				itemProxy.getTransactionTax());
	}

	// ---------------------------------------------------------------------
	/**
	 * Indicates whether this transaction contains one or more order line items.
	 * 
	 * @return true if line items vector contains order line item(s).
	 **/
	// ---------------------------------------------------------------------
	public boolean containsOrderLineItems() {
		return (itemProxy.containsOrderLineItems());
	}

	// ---------------------------------------------------------------------
	/**
	 * Indicates whether this transaction contains one or more return items.
	 * 
	 * @return true if line items vector contains return line item(s).
	 **/
	// ---------------------------------------------------------------------
	public boolean containsReturnLineItems() {
		return (itemProxy.containsReturnLineItems());
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets item handler.
	 * <P>
	 * 
	 * @param value
	 *            item handler
	 **/
	// ---------------------------------------------------------------------
	public void setItemContainerProxy(ItemContainerProxyIfc value) {
		itemProxy = value;
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves item handler.
	 * <P>
	 * 
	 * @return item handler
	 **/
	// ---------------------------------------------------------------------
	public ItemContainerProxyIfc getItemContainerProxy() {
		return (itemProxy);
	}

	// ----------------------------------------------------------------------
	/**
	 * Validates to see if all store coupons in transaction can be applied
	 * successfully to the current items
	 * <p>
	 * 
	 * @return true if all store coupons are applicable, false otherwise
	 **/
	// ----------------------------------------------------------------------
	public boolean areAllStoreCouponsApplied() {
		return (itemProxy.areAllStoreCouponsApplied());
	}

	// ---------------------------------------------------------------------
	/**
	 * Calculates and returns the sum of any store coupon discount applied to
	 * this transaction.
	 * <P>
	 * 
	 * @param discountScope
	 *            indicating whether to calculate using item or transaction
	 *            scope store coupons. See
	 *            com.extendyourstore.domain.discount.DiscountRuleConstantsIfc
	 * @return CurrencyIfc total
	 **/
	// ---------------------------------------------------------------------
	public CurrencyIfc getStoreCouponDiscountTotal(int discountScope) {
		return (itemProxy.getStoreCouponDiscountTotal(discountScope));
	}

	// ---------------------------------------------------------------------
	/**
	 * Tests whether this transaction has discountable items.
	 * <P>
	 * 
	 * @return boolean
	 **/
	// ---------------------------------------------------------------------
	public boolean hasDiscountableItems() {
		return (itemProxy.hasDiscountableItems());
	}

	// ---------------------------------------------------------------------
	/**
	 * Counts discountable items.
	 * <P>
	 * 
	 * @return number of discountable items
	 **/
	// ---------------------------------------------------------------------
	public int countDiscountableItems() {
		return (itemProxy.countDiscountableItems());
	}

	// ---------------------------------------------------------------------
	/**
	 * Tests whether this transaction has one or more regular transaction
	 * discounts. CustomerDiscountByPercentageIfc discounts are not included in
	 * this test.
	 * <P>
	 * 
	 * @return boolean if one or more transaction discounts are associated with
	 *         this transaction
	 **/
	// ---------------------------------------------------------------------
	public boolean hasTransactionDiscounts() {
		return (itemProxy.hasTransactionDiscounts());
	}

	// ---------------------------------------------------------------------
	/**
	 * Tests whether any of the items in this transaction has a discount amount
	 * greater than its selling price.
	 * <P>
	 * 
	 * @return boolean
	 **/
	// ---------------------------------------------------------------------
	public boolean itemsDiscountExceedsSellingPrice() {
		return (itemProxy.itemsDiscountExceedsSellingPrice());
	}

	// ---------------------------------------------------------------------
	/**
	 * Tests whether any of the items in this container has a Selling Price
	 * greater than its MRP.
	 * <P>
	 * 
	 * @return boolean
	 **/
	// ---------------------------------------------------------------------
	public boolean itemsSellingPriceExceedsMRP() {
		return (((MAXItemContainerProxyIfc) itemProxy).itemsSellingPriceExceedsMRP());
	}

	// ---------------------------------------------------------------------
	/**
	 * Tests whether any of the items in this transaction has a tax amount
	 * greater than its selling price.
	 * <P>
	 * 
	 * @return boolean
	 **/
	// ---------------------------------------------------------------------
	public boolean itemsTaxExceedsSellingPrice() {
		return (itemProxy.itemsTaxExceedsSellingPrice());
	}

	// ---------------------------------------------------------------------
	/**
	 * Calculates and returns a percentage of this transactions subtotal as a
	 * currency amount.
	 * 
	 * @param percentage
	 *            to calculate
	 * @return percentage of subtotal amount as CurrencyIfc
	 **/
	// ---------------------------------------------------------------------
	public CurrencyIfc calculateSubtotalPercentage(BigDecimal percentage) {
		CurrencyIfc value = null;
		if (percentage != null) {
			value = totals.getSubtotal().multiply(percentage);
		}
		return value;
	}

	// ---------------------------------------------------------------------
	/**
	 * Tests whether the transaction discounts exceed the amount passed in as a
	 * parameter.
	 * 
	 * @param amount
	 *            to test
	 * @return boolean
	 **/
	// ---------------------------------------------------------------------
	public boolean transactionDiscountsExceed(CurrencyIfc amount) {
		CurrencyIfc subtotal = totals.getSaleSubtotal();
		CurrencyIfc discountAmount = DomainGateway.getBaseCurrencyInstance();
		boolean discountExceed = false;

		// check for percent discounts
		TransactionDiscountStrategyIfc[] percentDiscounts = getTransactionDiscounts(
				DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE, DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
		if (percentDiscounts.length > 0) {
			// calculate % discount amount after subtracting $ discount amount
			// from subtototal
			CurrencyIfc percentDiscountAmount = subtotal.multiply(percentDiscounts[0].getDiscountRate());

			// add the % discount amount to the $ discount amount
			discountAmount = percentDiscountAmount;

		}

		// get transaction discounts
		TransactionDiscountStrategyIfc[] amountDiscounts = getTransactionDiscounts(
				DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT, DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
		if (amountDiscounts.length > 0) {
			// get transaction discount amount
			discountAmount = discountAmount.add(amountDiscounts[0].getDiscountAmount());
		}

		// if total discount amount is greater than amount, return true
		if (discountAmount.compareTo(amount.abs()) == CurrencyIfc.GREATER_THAN) {
			discountExceed = true;
		}
		return (discountExceed);
	}

	// ---------------------------------------------------------------------
	/**
	 * Tests whether the transaction discounts exceed the amount passed in as a
	 * parameter.
	 * 
	 * @param amount
	 *            to test
	 * @return boolean
	 **/
	// ---------------------------------------------------------------------
	public boolean transactionEmployeeDiscountsExceed(CurrencyIfc amount) {
		CurrencyIfc subtotal = totals.getSaleSubtotal();
		CurrencyIfc discountAmount = DomainGateway.getBaseCurrencyInstance();
		boolean discountExceed = false;

		// check for percent discounts
		TransactionDiscountStrategyIfc[] percentDiscounts = getTransactionDiscounts(
				DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE, DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE);
		if (percentDiscounts.length > 0) {
			// calculate % discount amount after subtracting $ discount amount
			// from subtototal
			CurrencyIfc percentDiscountAmount = subtotal.multiply(percentDiscounts[0].getDiscountRate());

			// add the % discount amount to the $ discount amount
			discountAmount = percentDiscountAmount;
		}

		// get transaction discounts
		TransactionDiscountStrategyIfc[] amountDiscounts = getTransactionDiscounts(
				DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT, DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE);
		if (amountDiscounts.length > 0) {
			// get transaction discount amount
			discountAmount = discountAmount.add(amountDiscounts[0].getDiscountAmount());

		}

		// if total discount amount is greater than amount, return true
		if (discountAmount.compareTo(amount.abs()) == CurrencyIfc.GREATER_THAN) {
			discountExceed = true;
		}
		return (discountExceed);
	}

	// ---------------------------------------------------------------------
	/**
	 * Method to default display string function.
	 * <P>
	 * 
	 * @return String representation of object
	 **/
	// ---------------------------------------------------------------------
	public String toString() { // begin toString()
								// result string
		StringBuilder strResult = Util.classToStringHeader("SaleReturnTransaction", getRevisionNumber(), hashCode());
		strResult.append(super.toString()).append(Util.formatToStringEntry("Item handler", getItemContainerProxy()))
				.append(Util.formatToStringEntry("Order ID", orderID));
		strResult.append(Util.formatToStringEntry("SendCustomerLinked", sendCustomerLinked));
		strResult.append(Util.formatToStringEntry("CustomerPresentChecked", checkedCustomerPresent));
		strResult.append(Util.formatToStringEntry("CustomerPhysicallyPresent", customerPhysicallyPresent));
		// pass back result
		return (strResult.toString());
	} // end toString()

	// ---------------------------------------------------------------------
	/**
	 * Returns default journal string.
	 * <P>
	 * 
	 * @return default journal string
	 **/
	// ---------------------------------------------------------------------
	public String toJournalString() { // begin toJournalString()
		StringBuffer strResult = new StringBuffer();
		// start with Transaction journal data
		strResult.append(journalHeader());

		// journal transaction modifiers
		strResult.append(itemProxy.journalTransactionModifiers(getCustomer()));

		// journal line items
		strResult.append(itemProxy.journalLineItems());

		// tender line items not journaled here (yet)

		// pass back result
		return (strResult.toString());
	} // end toJournalString()

	// ---------------------------------------------------------------------
	/**
	 * Write journal header to specified string buffer.
	 * <P>
	 * 
	 * @return journal fragment string
	 **/
	// ---------------------------------------------------------------------
	public String journalHeader() { // begin journalHeader()
		StringBuffer strResult = new StringBuffer();
		// prateek for bug 6436
		strResult.append(toTransactionJournalString());
		// end for bug 6436

		// The below logic assumes that an EOL was the
		// last string append to, in the above toJournalString()
		// method.
		int index = strResult.lastIndexOf(Util.EOL);
		if (index > 0 && index < strResult.length())
			strResult.delete(index, strResult.length());

		// pass back result
		return (strResult.toString());
	} // end journalHeader()

	// ---------------------------------------------------------------------
	/**
	 * Write line items to journal string.
	 * <P>
	 * 
	 * @return journal fragment string
	 **/
	// ---------------------------------------------------------------------
	public String journalLineItems() { // begin journalLineItems()
		return (itemProxy.journalLineItems());
	} // end journalLineItems()

	// ---------------------------------------------------------------------
	/**
	 * Write transaction modifiers to journal string.
	 * <P>
	 * 
	 * @return journal fragment string
	 **/
	// ---------------------------------------------------------------------
	public String journalTransactionModifiers() { // begin
													// journalTransactionModifiers()
		return (itemProxy.journalTransactionModifiers((CustomerIfc) null));
	} // end journalTransactionModifiers()

	// ---------------------------------------------------------------------
	/**
	 * Write transaction modifiers to journal string.
	 * <P>
	 * 
	 * @param customer
	 *            CustomerIfc reference
	 * @return journal fragment string
	 **/
	// ---------------------------------------------------------------------
	public String journalTransactionModifiers(CustomerIfc customer) { // begin
																		// journalTransactionModifiers()
		return (itemProxy.journalTransactionModifiers(customer));
	} // end journalTransactionModifiers()

	// ---------------------------------------------------------------------
	/**
	 * Determine if two objects are identical.
	 * <P>
	 * 
	 * @param obj
	 *            object to compare with
	 * @return true if the objects are identical, false otherwise
	 **/

	// ---------------------------------------------------------------------
	/*
	 * public boolean equals(Object obj) { // begin equals() boolean equal =
	 * false;
	 * 
	 * // If it's a SaleReturnTransaction, compare its attributes if (obj
	 * instanceof SaleReturnTransaction) { // downcast the input object
	 * SaleReturnTransaction c = (SaleReturnTransaction) obj; if
	 * (!super.equals(obj)) { equal = false; } else if
	 * (!Util.isObjectEqual(getItemContainerProxy(), c.getItemContainerProxy()))
	 * { equal = false; } else if (!Util.isObjectEqual(getItemsByTaxGroup(),
	 * c.getItemsByTaxGroup())) { equal = false; } else if
	 * (!Util.isObjectEqual(getInstantCredit(), c.getInstantCredit())) { equal =
	 * false; } else if (!Util.isObjectEqual(getReturnTenderDataContainer(),
	 * c.getReturnTenderDataContainer())) { equal = false; } else if
	 * (!Util.isObjectEqual(getTransactionTax(), c.getTransactionTax())) { equal
	 * = false; } else if (!Util.isObjectEqual(getDeletedLineItems(),
	 * c.getDeletedLineItems())) { equal = false; } else if (
	 * isTransactionGiftReceiptAssigned() !=
	 * c.isTransactionGiftReceiptAssigned() ) { equal = false; } else if (
	 * checkedCustomerPresent() != c.checkedCustomerPresent() ) { equal = false;
	 * } else if ( isSendCustomerLinked() != c.isSendCustomerLinked() ) { equal
	 * = false; } else if ( !Util.isObjectEqual(getEmployeeDiscountID(),
	 * c.getEmployeeDiscountID()) ) { equal = false; } else if (
	 * !Util.isObjectEqual(getOrderID(), c.getOrderID()) ) { equal = false; }
	 * else if ( !Util.isObjectEqual(getInstantCreditEnrollmentCode(),
	 * c.getInstantCreditEnrollmentCode())) { equal = false; } else if (
	 * isReentryMode() != c.isReentryMode() ) { equal = false; } else if (
	 * getSendPackageCount() != c.getSendPackageCount() ) { equal = false; }
	 * else if ( !Util.isObjectEqual(getAgeRestrictedDOB(),
	 * c.getAgeRestrictedDOB()) ) { equal = false; } else { equal = true; } }
	 * 
	 * return (equal); }
	 */// end equals()
	/**
	 * Rev 1.0 changes start here
	 */
	public boolean equals(Object obj) { // begin equals()
		boolean equal = false;
		// If it's a SaleReturnTransaction, compare its attributes
		if (obj instanceof SaleReturnTransaction) {
			// downcast the input object
			MAXSaleReturnTransaction c = (MAXSaleReturnTransaction) obj;
			if (!super.equals(obj)) {
				equal = false;
			} else {
				equal = true;
			}
		}

		return (equal);
	} // end equals()

	/**
	 * Rev 1.0 changes end here
	 */
	// ---------------------------------------------------------------------
	/**
	 * Returns true if the transaction is an exchage; false otherwise. The
	 * transaction type of exchange is no longer used. This is used in cases
	 * where we need to know an exchange took place, although the transaction is
	 * typed as a sale or a return.
	 * 
	 * @return true if the transaction is an exchange; false otherwise.
	 **/
	// ---------------------------------------------------------------------
	public boolean isExchange() { // begin isExchange()
		boolean exchangeFlag = false;
		if (transactionType == TransactionIfc.TYPE_SALE || transactionType == TransactionIfc.TYPE_RETURN) {
			AbstractTransactionLineItemIfc[] items = getLineItems();
			for (int i = 0; i < items.length; i++) { // begin loop through line
														// items
				if (items[i] instanceof SaleReturnLineItemIfc) {
					boolean returnFlag = ((SaleReturnLineItemIfc) items[i]).isReturnLineItem();
					// if this is a sale with a return item, it's an exchange
					if ((transactionType == TransactionIfc.TYPE_SALE && returnFlag)
							|| (transactionType == TransactionIfc.TYPE_RETURN && !returnFlag)) {
						// set flag and exit
						exchangeFlag = true;
						i = items.length;
					}
				}
			} // end loop through line items
		}
		return (exchangeFlag);

	} // end isExchange()

	// ---------------------------------------------------------------------
	/**
	 * Returns true if the transaction has return items.
	 * 
	 * @return true if the transaction has return items, false otherwise.
	 **/
	// ---------------------------------------------------------------------
	public boolean hasReturnItems() { // begin hasReturnItems()
		boolean hasReturnItems = false;
		if (transactionType == TransactionIfc.TYPE_RETURN || isExchange()) {
			hasReturnItems = true;
		}
		return (hasReturnItems);
	} // end hasReturnItems()

	// ---------------------------------------------------------------------
	/**
	 * Method to determine if any of the items in the transaction contains a
	 * specific serial number.
	 * 
	 * @param serialNumber
	 *            to check
	 * @return boolean - true if serial number found
	 **/
	// ---------------------------------------------------------------------
	public boolean containsSerialNumber(String serialNumber) {
		return (itemProxy.containsSerialNumber(serialNumber));
	}

	// ----------------------------------------------------------------------
	/**
	 * Get the employee Discount id String
	 * 
	 * @return employeeDiscountID String
	 **/
	// ----------------------------------------------------------------------
	public String getEmployeeDiscountID() {
		return employeeDiscountID;
	}

	// ----------------------------------------------------------------------
	/**
	 * Setst the empployee discount id string
	 * 
	 * @param employeeDiscountID
	 *            String
	 **/
	// ----------------------------------------------------------------------
	public void setEmployeeDiscountID(String employeeDiscountID) {
		this.employeeDiscountID = employeeDiscountID;
	}

	/**
	 * retrieve the instant credit enrollment code
	 * <p>
	 * 
	 * @return instant credit enrollment code
	 */

	public String getInstantCreditEnrollmentCode() {
		return instantCreditEnrollmentCode;
	}

	/**
	 * set the instant credit enrollment code
	 * <p>
	 * 
	 * @param value
	 *            the instant credit enrollment code
	 */

	public void setInstantCreditEnrollmentCode(String value) {
		instantCreditEnrollmentCode = value;
	}

	/**
	 * Return an instant credit object associated with this transaction. This
	 * value is populated if a customer has applied for a houseCard.
	 * 
	 * @return instantCredit credit card object
	 * @see com.extendyourstore.domain.transaction.InstantCreditTransactionIfc#getInstantCredit()
	 */
	public InstantCreditIfc getInstantCredit() {
		return this.instantCredit;
	}

	/**
	 * Set the instant credit object. This is set when a customer has applied
	 * for a house card.
	 * 
	 * @param instantCredit
	 *            credit card
	 * @see com.extendyourstore.domain.transaction.InstantCreditTransactionIfc#setInstantCredit(com.extendyourstore.domain.utility.InstantCreditIfc)
	 */
	public void setInstantCredit(InstantCreditIfc instantCredit) {
		this.instantCredit = instantCredit;
		if (instantCredit != null) {
			setInstantCreditEnrollmentCode(instantCredit.getApprovalStatus().toString());
		}
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets the return tender data elements. These will be set into the
	 * ReturnTenderDataContainer.
	 * 
	 * @param dataElements
	 *            return tender data elements to set
	 **/
	// ---------------------------------------------------------------------
	public void setReturnTenderElements(ReturnTenderDataElementIfc[] dataElements) {
		getReturnTenderDataContainer().setTenderElements(dataElements);
	}

	// ---------------------------------------------------------------------
	/**
	 * Gets the return tender data elements. This is a convienience method and
	 * actually retrieves the elements from ReturnTenderDataContainer.
	 * 
	 * @return tenderElements in the returnTenderDataContainer
	 **/
	// ---------------------------------------------------------------------
	public ReturnTenderDataElementIfc[] getReturnTenderElements() {
		return getReturnTenderDataContainer().getTenderElements();
	}

	// ---------------------------------------------------------------------
	/**
	 * Appends the return tender data elements. These will be added into the
	 * ReturnTenderDataContainer.
	 * 
	 * @param dataElements
	 *            to append to the list of existing tender elements
	 **/
	// ---------------------------------------------------------------------
	public void appendReturnTenderElements(ReturnTenderDataElementIfc[] dataElements) {
		getReturnTenderDataContainer().addTenderElements(dataElements);
	}

	// ---------------------------------------------------------------------
	/**
	 * Create the return tender data container (if not already created)
	 * 
	 * @return the return tender data container
	 **/
	// ---------------------------------------------------------------------
	public ReturnTenderDataContainerIfc getReturnTenderDataContainer() {
		if (returnTendersContainer == null)
			returnTendersContainer = (ReturnTenderDataContainerIfc) DomainGateway.getFactory()
					.getReturnTenderDataContainerInstance();
		return returnTendersContainer;
	}

	// ---------------------------------------------------------------------
	/**
	 * Buffers journal string for a transaction level discount.
	 * <P>
	 * 
	 * @param discount
	 *            - transcation discount strategy
	 * @param message
	 *            - journal StringBuffer
	 **/
	// ---------------------------------------------------------------------
	public static void journalTranLevelDiscount(TransactionDiscountStrategyIfc discount, StringBuffer message) {
		int AVAIL_DISCOUNT_LENGTH = 23;

		if (discount != null && message != null) {
			if (discount instanceof TransactionDiscountByAmountIfc) {
				CurrencyIfc amt = ((TransactionDiscountByAmountIfc) discount).getDiscountAmount();
				String amtStr = amt.toFormattedString().trim();
				message.append(Util.EOL).append("TRANS: Discount")
						.append(Util.SPACES.substring(amtStr.length(), AVAIL_DISCOUNT_LENGTH)).append(amtStr)
						.append(Util.EOL).append("  Discount: Amt. Deleted").append(Util.EOL).append("  Disc. Rsn.: ")
						.append((discount.getReasonCodeText() == null)
								? new Integer(discount.getReasonCode()).toString() : discount.getReasonCodeText());
			} else if (discount instanceof TransactionDiscountByPercentageIfc) {
				BigDecimal rate = discount.getDiscountRate();
				rate = rate.movePointRight(2);
				rate = rate.setScale(0, BigDecimal.ROUND_HALF_UP);
				String rateStr = rate.toString();

				message.append(Util.EOL).append("TRANS: Discount").append(Util.EOL).append("  Discount: ")
						.append(rateStr).append("% Deleted").append(Util.EOL).append("  Disc. Rsn.: ")
						.append((discount.getReasonCodeText() == null)
								? new Integer(discount.getReasonCode()).toString() : discount.getReasonCodeText());
			}
		}
	}

	// ---------------------------------------------------------------------
	/**
	 * Retrieves the Team Connection revision number.
	 * <P>
	 * 
	 * @return String representation of revision number
	 **/
	// ---------------------------------------------------------------------
	public String getRevisionNumber() {
		return (Util.parseRevisionNumber(revisionNumber));
	}

	/**
	 * @return Returns the reentryMode.
	 */
	public boolean isReentryMode() {
		return reentryMode;
	}

	/**
	 * @param reentryMode
	 *            The reentryMode to set.
	 */
	public void setReentryMode(boolean reentryMode) {
		this.reentryMode = reentryMode;
	}

	/**
	 * @return Returns the checkedCustomerPresent.
	 */
	public boolean checkedCustomerPresent() {
		return checkedCustomerPresent;
	}

	/**
	 * @param checkedCustomerPresent
	 *            The checkedCustomerPresent to set.
	 */
	public void setCheckedCustomerPresent(boolean checkedCustomerPresent) {
		this.checkedCustomerPresent = checkedCustomerPresent;
	}

	/**
	 * Get send customer is linked customer or captured customer. It is used for
	 * journal and print receipt
	 * 
	 * @param boolean
	 *            sendCustomerLinked
	 */
	public boolean isSendCustomerLinked() {
		return sendCustomerLinked;
	}

	/**
	 * Set send customer. It can be linked customer or captured customer. It is
	 * used for journal and print receipt
	 * 
	 * @param boolean
	 *            sendCustomerLinked
	 */
	public void setSendCustomerLinked(boolean value) {
		sendCustomerLinked = value;
	}

	/**
	 * get all the send items based on send index from the transaction
	 * 
	 * @param int
	 *            sendIndex
	 * @return SaleReturnLineItemsIfc send line items
	 */
	public SaleReturnLineItemIfc[] getSendItemBasedOnIndex(int sendIndex) {
		// some items from this send have been deleted.
		// get the number of item from the send.
		SaleReturnLineItemIfc[] sendItems = null;
		int count = 0; // item count in the send
		SaleReturnLineItemIfc items[] = (SaleReturnLineItemIfc[]) getLineItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].getSendLabelCount() == sendIndex) {
				count++;
			}
		}
		// get the items in the send
		if (count > 0) {
			sendItems = new SaleReturnLineItemIfc[count];
			int index = -1;
			for (int i = 0; i < items.length; i++) {
				if (items[i].getSendLabelCount() == sendIndex) {
					sendItems[++index] = items[i];
				}
			}
		}
		return sendItems;
	}

	/**
	 * get send index based on selected line item index
	 * 
	 * @param int[]
	 *            all selected line item index
	 * @return int send index
	 */
	public int getSendIndexFromSelectedItems(int[] allSelected) {
		int sendIndex = 0;
		for (int i = 0; i < allSelected.length; i++) {
			int index = allSelected[i];
			// get send index
			sendIndex = ((SaleReturnLineItemIfc) retrieveItemByIndex(index)).getSendLabelCount();
			if (sendIndex > 0) {
				// there are send items in the delete
				break;
			}
		}
		return sendIndex;
	}

	/**
	 * check if the all the send items are deleted
	 * 
	 * @param int[]
	 *            selected line item index for delete
	 * @param int
	 *            send index
	 * @return boolean flag to indicate all of them get deleted or not
	 */
	public boolean isAllItemInSendDeleted(int[] allSelected, int sendIndex) {
		boolean isAllItemInSendDeleted = true;
		int countInTheSend = 0;
		// check there is other item in the same send
		SaleReturnLineItemIfc[] lineItems = (SaleReturnLineItemIfc[]) getLineItems();
		for (int i = 0; i < lineItems.length; i++) {
			if (lineItems[i].getSendLabelCount() == sendIndex) {
				countInTheSend++;
			}
		}
		int sendCountInDelete = 0;
		for (int i = 0; i < allSelected.length; i++) {
			SaleReturnLineItemIfc item = (SaleReturnLineItemIfc) retrieveItemByIndex(allSelected[i]);
			if (item.getSendLabelCount() == sendIndex) {
				sendCountInDelete++;
			}
		}
		if (countInTheSend > sendCountInDelete) {
			isAllItemInSendDeleted = true;
		}
		return isAllItemInSendDeleted;
	}

	/**
	 * Checks transactionGiftReceiptAssigned to see if a transaction wide gift
	 * receipt has been assigned
	 * 
	 * @return boolean transactionGiftReceiptAssigned
	 */
	public boolean isTransactionGiftReceiptAssigned() {
		return transactionGiftReceiptAssigned;
	}

	/**
	 * Set transactionGiftReceiptAssigned. It is set to true when a gift receipt
	 * was added to the entire transaction
	 * 
	 * @param boolean
	 *            value
	 */
	public void setTransactionGiftReceiptAssigned(boolean value) {
		transactionGiftReceiptAssigned = value;
	}

	/**
	 * Set sendPackageCount.
	 * 
	 * @param int
	 *            count
	 */
	public void setSendPackageCount(int count) {
		sendPackageCount = count;
	}

	/**
	 * Retrieve sendPackageCount.
	 * 
	 * @return the send package count
	 */
	public int getSendPackageCount() {
		return sendPackageCount;
	}

	/**
	 * Indicates customer is physically present
	 * 
	 * @return boolean true if customer is physically present
	 */
	public boolean isCustomerPhysicallyPresent() {
		return customerPhysicallyPresent;
	}

	/**
	 * Checks if customer is physically present
	 * 
	 * @param customerPhysicallyPresent
	 *            The customerPhysicallyPresent to set.
	 */
	public void setCustomerPhysicallyPresent(boolean customerPhysicallyPresent) {
		this.customerPhysicallyPresent = customerPhysicallyPresent;
	}

	/**
	 * Set the tax rules for transaction level send
	 * 
	 * @param taxRulesVO
	 * @see com.extendyourstore.domain.transaction.SaleReturnTransactionIfc#setSendTaxRules(com.extendyourstore.domain.tax.TaxRulesVO)
	 */
	public void setSendTaxRules(TaxRulesVO taxRulesVO) {
		this.taxRulesVO = taxRulesVO;
	}

	/**
	 * Get the tax rules for transaction level send
	 * 
	 * @return taxRulesVO
	 * @see com.extendyourstore.domain.transaction.SaleReturnTransactionIfc#getSendTaxRules()
	 */
	public TaxRulesVO getSendTaxRules() {
		return this.taxRulesVO;
	}

	/**
	 * Check to see if the transaction is taxable. A transaction is taxable is
	 * assumed taxable, unless every item in the transaction is a non-taxable
	 * item.
	 * 
	 * @return true or false
	 * @since 7.0
	 */
	public boolean isTaxableTransaction() {
		boolean taxableItemFound = false;
		boolean nonTaxableItemFound = false;

		Vector lineItems = this.getLineItemsVector();
		for (int i = 0; i < lineItems.size(); i++) {
			if (lineItems.elementAt(i) instanceof SaleReturnLineItemIfc) {
				SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) lineItems.elementAt(i);
				if (srli.getTaxMode() == TaxConstantsIfc.TAX_MODE_NON_TAXABLE
						|| srli.getTaxMode() == TaxConstantsIfc.TAX_MODE_EXEMPT
						|| srli.getTaxMode() == TaxConstantsIfc.TAX_MODE_TOGGLE_OFF) {
					nonTaxableItemFound = true;
				} else {
					taxableItemFound = true;
				}
			}
		}

		boolean taxable = true; // default
		if (!taxableItemFound && nonTaxableItemFound) {
			taxable = false;
		}
		return taxable;
	}

	// ----------------------------------------------------------------------
	/**
	 * Get the age restriction dob entered by the associate.
	 * 
	 * @return Returns the ageRestrictedDOB.
	 **/
	// ----------------------------------------------------------------------
	public EYSDate getAgeRestrictedDOB() {
		return ageRestrictedDOB;
	}

	// ----------------------------------------------------------------------
	/**
	 * Sets the age restriction dob entered by the associate.
	 * 
	 * @param ageRestrictedDOB
	 *            The ageRestrictedDOB to set.
	 **/
	// ----------------------------------------------------------------------
	public void setAgeRestrictedDOB(EYSDate ageRestrictedDOB) {
		this.ageRestrictedDOB = ageRestrictedDOB;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Remove the send package at the specified location
	 * 
	 * @param index
	 *            the index of the package to remove
	 */
	// Changes start for Rev 1.4 (Send)
	// ----------------------------------------------------------------------
	/*public void removeSendPackage(int index) {
		((MAXTransactionTotals) getTransactionTotals()).getSendPackageVector().remove(index - 1);
	}*/

	// ----------------------------------------------------------------------------
	/**
	 * @return an array of all the send package line items
	 */
	// ----------------------------------------------------------------------
	/*public SendPackageLineItemIfc[] getSendPackages() {
		
		return ((MAXTransactionTotals)getTransactionTotals()).getSendPackages();
		
	}*/
	// Changes ends for Rev 1.4 (Send)

	// izhar
	public void setTaxTypeLegal(boolean taxTypeLegal) {
		this.taxTypeLegal = taxTypeLegal;
	}

	// izhar
	protected String printFreeItem = null;

	public String getPrintFreeItem() {
		return printFreeItem;
	}

	public void setPrintFreeItem(String printFreeItem) {
		this.printFreeItem = printFreeItem;
	}

	// end
	/**
	 * Rev 1.0 changes start here
	 */
	public boolean isFatalDeviceCall() {
		return isFatalDeviceCall;
	}

	public void setFatalDeviceCall(boolean isFatalDeviceCall) {
		this.isFatalDeviceCall = isFatalDeviceCall;
	}

	/**
	 * Rev 1.0 changes end here
	 */
	/**
	 * Rev 1.2 changes start here
	 */
	public String getDiscountEmployeeRole() {
		return discountEmployeeRole;
	}

	public void setDiscountEmployeeRole(String discountEmployeeRole) {
		this.discountEmployeeRole = discountEmployeeRole;
	}

	public String getDiscountEmployeeName() {
		return discountEmployeeName;
	}

	public void setDiscountEmployeeName(String discountEmployeeName) {
		this.discountEmployeeName = discountEmployeeName;
	}

	public String getDiscountEmployeeLocation() {
		return discountEmployeeLocation;
	}

	// prateek for bug 6436
	public String toTransactionJournalString() { // begin toJournalString()
		StringBuffer strResult = new StringBuffer();
		// append training mode banner
		if (isTrainingMode()) {
			strResult.append(JOURNAL_TRAINING_MODE_BANNER).append(Util.EOL).append(Util.EOL);
		}
		// append transaction reentry mode banner
		if (isReentryMode()) {
			strResult.append(TRANSACTION_REENTRY_MODE_BANNER).append(Util.EOL).append(Util.EOL);
		}

		// format date and time
		Locale defaultLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
		DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();
		Date date = getTimestampBegin().dateValue();
		String dateString = dateTimeService.formatDate(date, defaultLocale, DateFormat.SHORT);
		String timeString = dateTimeService.formatTime(date, defaultLocale, DateFormat.SHORT);
		String line = blockLine(new StringBuffer(dateString), new StringBuffer(timeString));
		strResult.append(line);

		StringBuffer tillString = new StringBuffer(10);
		tillString.append("Till: ").append(getTillID());
		tillString = Util.rightJustifyStringBuffer(tillString, 10);
		// build transaction ID, etc.
		strResult.append(Util.EOL).append("Trans.: ").append(getFormattedTransactionSequenceNumber())
				.append("                ").append("Store: ").append(getFormattedStoreID()).append(Util.EOL)
				.append("Reg.: ").append(getFormattedWorkstationID()).append("                     ")
				.append(tillString.toString()).append(Util.EOL).append("Cashier: ").append(getCashier().getEmployeeID())
				.append("              ");
		if (getSalesAssociate() != null) {
			strResult.append("Sales: ").append(getSalesAssociate().getEmployeeID());
		}
		strResult.append(Util.EOL);
		// pass back result
		return (strResult.toString());
	}

	// ---------------------------------------------------------------------
	private String blockLine(StringBuffer left, StringBuffer right) {
		StringBuffer s = new StringBuffer(LINE_LENGTH);
		s.append(left);

		// pad with spaces
		for (int i = left.length(); i < LINE_LENGTH - right.length(); i++) {
			s.append(" ");
		}

		s.append(right);

		return s.toString();
	}

	// end for bug 6436

	public void setDiscountEmployeeLocation(String discountEmployeeLocation) {
		this.discountEmployeeLocation = discountEmployeeLocation;
	}

	public boolean isTaxTypeLegal() {
		return taxTypeLegal;
	}

	/**
	 * Rev 1.2 changes end here
	 */
	public void replaceLineItemWithoutBestDeal(AbstractTransactionLineItemIfc lineItem, int index) {
		if (itemProxy instanceof MAXItemContainerProxyIfc) {
			//Changes for Rev 1.2 : Starts
			((MAXItemContainerProxyIfc) itemProxy).replaceLineItem(lineItem, index);
			//Changes for Rev 1.2 : Ends
		}
		updateTransactionTotals(getItemContainerProxy().getLineItems(),
				getItemContainerProxy().getTransactionDiscounts(), getItemContainerProxy().getTransactionTax());
	}

	/** Changes for Rev 1.5 : Starts **/
	public String getEmpDiscountAvailLimit() {
		return empDiscountAvailLimit;
	}

	public void setEmpDiscountAvailLimit(String empDiscountAvailLimit) {
		this.empDiscountAvailLimit = empDiscountAvailLimit;
	}

	/** Changes for Rev 1.5 : Ends **/
	/* added by Dipak Goit */
	/* implementation of Capillary Coupon Discount */

	protected Vector capillaryCouponsApplied = new Vector();
	protected int couponCount = 0;
	// Rev 1.6 : Start
	protected int itemLevelCouponCount = 0;

	public int getItemLevelCouponCount() {
		return itemLevelCouponCount;
	}

	public void setItemLevelCouponCount(int itemLevelCouponCount) {
		this.itemLevelCouponCount = itemLevelCouponCount;
	}

	// Rev 1.6 : End
	public Vector getCapillaryCouponsApplied() {
		return capillaryCouponsApplied;
	}

	public void addCapillaryCouponsApplied(MAXDiscountCouponIfc discountCoupon) {
		capillaryCouponsApplied.addElement(discountCoupon);
	}

	public void removeCapillaryCouponsApplied() {
		capillaryCouponsApplied.removeAllElements();
	}

	public int getNthCoupon() {
		return couponCount;
	}

	public void setNthCoupon(int couponCount) {
		this.couponCount = couponCount;
	}

	public boolean isItemLevelDiscount() {
		return isItemLevelDiscount;
	}

	public void setItemLevelDiscount(boolean isItemLevelDiscount) {
		this.isItemLevelDiscount = isItemLevelDiscount;
	}

	/* end of the implementation of Capillary Coupon Discount */
	// Rev 1.6 : Start
	public boolean isSendTransaction() {
		return sendTransaction;
	}

	public void setSendTransaction(boolean flag) {

		this.sendTransaction = flag;
	}
	// Rev 1.6 : End
	/// for the tic customer addition akhilesh start

	public void setMAXTICCustomer(MAXCustomerIfc ticcustomer) {
		this.maxTicCustomer = ticcustomer;
	}

	public MAXCustomerIfc getMAXTICCustomer() {
		return maxTicCustomer;
	}

	public boolean isTicCustomerVisibleFlag() {
		return ticCustomerVisibleFlag;
	}

	public void setTicCustomerVisibleFlag(boolean ticCustomerVisibleFlag) {
		this.ticCustomerVisibleFlag = ticCustomerVisibleFlag;
	}

	public CustomerIfc getTicCustomer() {
		return ticCustomer;
	}

	public void setTicCustomer(CustomerIfc ticCustomer) {
		this.ticCustomer = ticCustomer;
		if (this.customer == null)
			this.customer = this.ticCustomer;
	}
	/// for the tic customer addition akhilesh end

	protected String rounding;

	/**
	 * List containing the rounding denominations.The list is expected to be
	 * sorted when set.
	 */
	protected List roundingDenominations;
	private String status_message;

	/**
	 * @param rounding
	 *            The rounding to set.
	 */
	public void setRounding(String rounding) {
		this.rounding = rounding;
	}

	/**
	 * @return rounding The boolean for rounding enabled/disabled
	 */
	public String getRounding() {
		return rounding;
	}

	/**
	 * @return Returns the roundingDenominations
	 */
	public List getRoundingDenominations() {
		return roundingDenominations;
	}

	/**
	 * @param roundingDenominations
	 *            The roundingDenominations to set.
	 */
	public void setRoundingDenominations(List roundingDenominations) {
		this.roundingDenominations = roundingDenominations;
	}
	// Changes starts for Rev 1.7 (Ashish : Online points redemption)
	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	// Changes starts for Rev 1.7 (Ashish : Online points redemption)
	protected FinancialTotalsIfc getTenderFinancialTotals(TenderLineItemIfc[] tenderLineItems,
			TransactionTotalsIfc transTotals) {
		FinancialTotalsIfc financialTotals = DomainGateway.getFactory().getFinancialTotalsInstance();
		// get size of array
		int numTenderLineItems = 0;

		if (tenderLineItems != null) {
			numTenderLineItems = tenderLineItems.length;
		}

		// if elements exist, loop through them
		for (int i = 0; i < numTenderLineItems; i++) {
			if (tenderLineItems[i] != null) {
				financialTotals = financialTotals.add(getFinancialTotalsFromTender(tenderLineItems[i]));
			}
		}

		return (financialTotals);
	}

	/**
	 * Derive the additive financial totals from a given tender line item.
	 *
	 * @param tli
	 *            TenderLineItemIfc entry
	 * @return FinancialTotalsIfc
	 */
	public FinancialTotalsIfc getFinancialTotalsFromTender(TenderLineItemIfc tli) {
		CurrencyIfc amount = tli.getAmountTender();
		TenderDescriptorIfc descriptor = DomainGateway.getFactory().getTenderDescriptorInstance();
		String desc = tli.getTypeDescriptorString();

		// Get the appropriate amount based on the currency used.
		if (tli instanceof TenderAlternateCurrencyIfc) {
			TenderAlternateCurrencyIfc alternate = (TenderAlternateCurrencyIfc) tli;
			CurrencyIfc alternateAmount = alternate.getAlternateCurrencyTendered();

			// if tendered in alternate currency, use that amount.
			if (alternateAmount != null) {
				amount = alternate.getAlternateCurrencyTendered();
				// Prepend nationality to the description.
				String countryCode = amount.getCountryCode();
				String countryDescriptor = CountryCodeMap.getCountryDescriptor(countryCode);
				if (countryDescriptor == null) {
					countryDescriptor = countryCode;
				}
				desc = countryDescriptor + " " + desc;
			}
		}

		String currencyCode = amount.getCountryCode();
		descriptor.setCountryCode(currencyCode);
		descriptor.setCurrencyID(amount.getType().getCurrencyId());
		int tenderType = tli.getTypeCode();

		descriptor.setTenderType(tenderType);
		int numberItems = 1;

		// Traveler's checks have multiple checks for one line item.
		if (tenderType == TenderLineItemIfc.TENDER_TYPE_TRAVELERS_CHECK) {
			numberItems = ((TenderTravelersCheckIfc) tli).getNumberChecks();
		}

		String sDesc = null;

		// Add individual credit card totals to the financial totals
		if (tenderType == TenderLineItemIfc.TENDER_TYPE_CHARGE) {
			// Assuming that Credit cards are always handled in the local
			// currency,
			// so there is no conflict between this description and the
			// alternate above.
			desc = ((TenderChargeIfc) tli).getCardType();
			sDesc = tli.getTypeDescriptorString();
			descriptor.setTenderSubType(desc);
		}
		if (tenderType == TenderLineItemIfc.TENDER_TYPE_COUPON) {
			// Assuming that Cr

			descriptor.setTenderSubType(((TenderCouponIfc) tli).getCouponNumber());
		}
		if (tenderType == TenderLineItemIfc.TENDER_TYPE_MALL_GIFT_CERTIFICATE) {
			String mallCertType = ((TenderGiftCertificateIfc) tli).getCertificateType();
			if (mallCertType != null && !mallCertType.equals("")) {
				TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();
				descriptor.setTenderType(tenderType);
				desc = tenderTypeMap.getDescriptor(tenderType);
				sDesc = tenderTypeMap.getDescriptor(tenderType);
			}

		}

		CurrencyIfc amtIn = null;
		CurrencyIfc amtOut = null;
		int cntIn = 0;
		int cntOut = 0;

		// Set amounts, count in/count out
		if (tli.getAmountTender().signum() == CurrencyIfc.POSITIVE) {
			amtIn = amount;
			amtOut = DomainGateway.getCurrencyInstance(currencyCode);
			cntIn = numberItems;
		} else {
			amtIn = DomainGateway.getCurrencyInstance(currencyCode);
			amtOut = amount.negate();
			cntOut = numberItems;
		}

		FinancialTotalsIfc financialTotals = DomainGateway.getFactory().getFinancialTotalsInstance();
		financialTotals.getTenderCount().addTenderItem(descriptor, cntIn, cntOut, amtIn, amtOut, desc, sDesc,
				tli.getHasDenominations());

		if (tli instanceof TenderStoreCreditIfc) {
			// Add Store Credit Issued ONLY.
			if (tli.getAmountTender().signum() == CurrencyIfc.NEGATIVE) {
				financialTotals.addAmountGrossStoreCreditsIssued(amtOut);
				financialTotals.addUnitsGrossStoreCreditsIssued(BigDecimalConstants.ONE_AMOUNT);
			}
			// Else it is an use of StoreCredit as tender; should not considered
			// as Redeem (Cash Redeem).
		} else if (tli instanceof TenderGiftCardIfc) {
			// if transaction is a refund, add the gift card amount to the item
			// credit bucket
			if (getTransactionType() == TransactionConstantsIfc.TYPE_RETURN
					|| getTransactionType() == TransactionConstantsIfc.TYPE_LAYAWAY_DELETE
					|| getTransactionType() == TransactionConstantsIfc.TYPE_ORDER_CANCEL) {
				financialTotals.addAmountGrossGiftCardItemCredit(tli.getAmountTender().abs());
				financialTotals.addUnitsGrossGiftCardItemCredit(BigDecimalConstants.ONE_AMOUNT);
			}

			// If the transaction is a sale and the tender amount is less than 0
			if (getTransactionType() == TransactionConstantsIfc.TYPE_SALE && tli.getAmountTender().signum() < 0) {
				financialTotals.addAmountGrossGiftCardItemCredit(tli.getAmountTender().abs());
				financialTotals.addUnitsGrossGiftCardItemCredit(BigDecimalConstants.ONE_AMOUNT);
			}
		}

		return (financialTotals);

	}
	
	//Change for Rev 1.9 : Starts
	public boolean gstEnable = false;
	public boolean igstApplicable = false;
	public String homeState;
	public String homeStateCode;
	public String ToState;
	protected Map<String, String> taxCode = new HashMap<String, String>();
	@Override
	public Map<String, String> getTaxCode()
	{
		return taxCode;
	}
	@Override
	public void setTaxCode(Map<String, String> taxCode)
	{
		this.taxCode = taxCode;
	}
	public HashMap<String,String> states = new HashMap<String,String>();
	/**
	 * @return the states
	 */
	@Override
	public HashMap<String, String> getStates() {
		return states;
	}
	/**
	 * @param states the states to set
	 */
	@Override
	public void setStates(HashMap<String, String> states) {
		this.states = states;
	}
	/**
	 * @return the homeStateCode
	 */
	@Override
	public String getHomeStateCode() {
		return homeStateCode;
	}
	/**
	 * @param homeStateCode the homeStateCode to set
	 */
	@Override
	public void setHomeStateCode(String homeStateCode) {
		this.homeStateCode = homeStateCode;
	}

	
	/**
	 * @return the toState
	 */
	@Override
	public String getToState() {
		return ToState;
	}
	/**
	 * @param toState the toState to set
	 */
	@Override
	public void setToState(String toState) {
		ToState = toState;
	}
	/**
	 * @return the homeState
	 */
	@Override
	public String getHomeState() {
		return homeState;
	}
	/**
	 * @param homeState the homeState to set
	 */
	@Override
	public void setHomeState(String homeState) {
		this.homeState = homeState;
	}
	/**
	 * @return the igstApplicable
	 */
	@Override
	public boolean isIgstApplicable() {
		return igstApplicable;
	}
	/**
	 * @param igstApplicable the igstApplicable to set
	 */
	@Override
	public void setIgstApplicable(boolean igstApplicable) {
		this.igstApplicable = igstApplicable;
	}

	public boolean deliverytrnx = false;
	@Override
	public boolean isGstEnable() {
		return gstEnable;
	}
	@Override
	public void setGstEnable(boolean gstEnable) {
		this.gstEnable = gstEnable;
	}
	@Override
	public boolean isDeliverytrnx() {
		return deliverytrnx;
	}
	@Override
	public void setDeliverytrnx(boolean deliverytrnx) {
		this.deliverytrnx = deliverytrnx;
	}
	public boolean captureCustomer = false;
	@Override
	public boolean isCaptureCustomer() {
		return captureCustomer;
	}
	@Override
	public void setCaptureCustomer(boolean captureCustomer) {
		this.captureCustomer = captureCustomer;
	}
	//Change for Rev 1.9 : Ends

// Changes for Rev 1.10 Starts
		/**
		 * @return the mcouponList
		 */
		@Override
		public ArrayList<MAXMcouponIfc> getMcouponList() {
			return mcouponList;
		}

		/**
		 * @param mcouponList
		 *            the mcouponList to set
		 */
		@Override
		public void setMcouponList(ArrayList<MAXMcouponIfc> mcouponList) {
			this.mcouponList = mcouponList;
		}
		// Changes for Rev 1.4 end
		
		// Changes for Rev 1.4 Start

		@Override
		public List getDeliveryItems() {
			return deliveryItems;
		}

		@Override
		public void addDeliveryItems(MAXSaleReturnLineItemIfc deliveryItem) {
			deliveryItems.add(deliveryItem);
		}

		/*@Override
		public boolean itemsSellingPriceExceedsMRP() {
			return (((MAXItemContainerProxyIfc) itemProxy).itemsSellingPriceExceedsMRP());
		}*/
		
		public String getMcouponStatusMessage() {
			return status_message;
		}
		public void setMcouponStatusMessage(String status_message) {
			this.status_message = status_message;
		}
		public String geteComOrderType() {
			return eComOrderType;
		}

		public void seteComOrderType(String eComOrderType) {
			this.eComOrderType = eComOrderType;
		}

		@Override
		public String getEmployeeCompanyName() {
			return companyName;
		}

		@Override
		public void setEmployeeCompanyName(String companyName) {
			this.companyName=companyName;
			
		}

		// Changes for Rev 1.10 Ends
		//protected ArrayList  response; 
			protected Vector<MAXBakeryItemIfc> scansheetVactor= new Vector<MAXBakeryItemIfc>();
		 public Vector<MAXBakeryItemIfc> getScansheetLineItemsVector()
		  {
		     return this.scansheetVactor;
			//this.response = response;
		  }
		 
		/*  Changes Start for Capture PAN CARD CR */
		 public EncipheredDataIfc encryptedPan;

			public String getPanNumber() {
				return panNumber;
			}

			public void setPanNumber(String panNumber) {
				this.panNumber = panNumber;
			}
			public String getForm60IDNumber() {
				return idnumber;
			}

			public void setForm60IDNumber(String idnumber) {
				this.idnumber = idnumber;
			}
			public String getPassportNumber() {
				return passportnum;
			}

			public void setPassportNumber(String passportnum) {
				this.passportnum = passportnum;
			}
			public String getVisaNumber() {
				return visanum;
			}

			public void setVisaNumber(String visanum) {
				this.visanum = visanum;
			}
			public String getITRAckNumber() {
				return ackNumber;
			}

			public void setITRAckNumber(String ackNumber) {
				this.ackNumber = ackNumber;
			}
			
			/*Changes END  for Capture PAN CARD CR */

			public String getEReceiptOTP() {
				return eReceiptOTP;
			}

			public void setEReceiptOTP(String eReceiptOTP) {
				this.eReceiptOTP = eReceiptOTP;
			}
			//Changes starts for Rev 1.11 (Vaibhav : PineLab)
			protected String edcType = null;

			public String getEdcType() {
				return edcType;
			}
			public void setEdcType(String edcType) {
				this.edcType = edcType;
			}
			
			//Changes ends for Rev 1.11 (Vaibhav : PineLab)
			
			public boolean sbiRewardredeemFlag = false;
			public boolean isSbiRewardredeemFlag() {
				return sbiRewardredeemFlag;
			}
			public void setSbiRewardredeemFlag(boolean sbiRewardredeemFlag) {
				this.sbiRewardredeemFlag = sbiRewardredeemFlag;
			}
			// Changes starts for Rev 1.12
			public String geteWalletCreditResponse() {
				return eWalletCreditResponse;
			}

			public void seteWalletCreditResponse(String eWalletCreditResponse) {
				this.eWalletCreditResponse = eWalletCreditResponse;
			}
			public boolean isEWalletTenderFlag() {
				return isEWalletTenderFlag;
			}

			public void setEWalletTenderFlag(boolean isEWalletTenderFlag) {
				this.isEWalletTenderFlag = isEWalletTenderFlag;
			}
			
			public String geteWalletTraceId() {
				return eWalletTraceId;
			}

			public void seteWalletTraceId(String eWalletTraceId) {
				this.eWalletTraceId = eWalletTraceId;
			}
			// Changes End for Rev 1.12

			public void setGstinresp(MAXGSTINValidationResponseIfc response) {
				this.response = response;
			}
			
			public MAXGSTINValidationResponseIfc getGstinresp() {
				return this.response;
			}
			
			public void setGSTINNumber(String gstin) {
				this.gstNumber = gstin;
			}
			public String getGSTINNumber() {
				return this.gstNumber;
			}
			
			public void setStoreGSTINNumber(String gstin) {
				this.gstStoreNumber = gstin;
			}
			public String getStoreGSTINNumber() {
				return this.gstStoreNumber;
			}

			
			public HashMap getManagerOverrideMap() {
			
				return this.managerOverrideMap;
			}

			public void setManagerOverrideMap(HashMap managerOverrideMap) {
			this.managerOverrideMap= managerOverrideMap;
			}
				
			
			
			public void setReceiptData(byte[] readerData) {
				this.fileData=readerData;
			}
			
			
			
		   public byte[] getReceiptData() {
				return this.fileData;
			}
			public boolean isRtsManagerOverride() {
				return rtsManagerOverride;
			}

			/**
			 * @param rtsManagerOverride the rtsManagerOverride to set
			 */
			public void setRtsManagerOverride(boolean rtsManagerOverride) {
				this.rtsManagerOverride = rtsManagerOverride;
			}
			// Changes for RTS ends
				public String getPaytmQROrderId() {
				return paytmQROrderId;
			}
			public void setPaytmQROrderId(String paytmQROrderId) {
				this.paytmQROrderId = paytmQROrderId;
			}
			
//REV 1.13 Starts :Changes by kamlesh for Liquor Starts
			
			float beertot;
			float liquortot;
			float InLiqtot;
			float frnLiqtot;
			
			public float getBeertot() {
				return beertot;
			}
			public void setBeertot(float beertot) {
				this.beertot = beertot;
			}
			
			public float getliquortot() {
				return liquortot;
			}

			public void setliquortot(float liquorTotal) {
				this.liquortot = liquorTotal;
				
			}

			public float getInLiqtot() {
				return InLiqtot;
			}

			public void setInLiqtot(float InLiqtot) {
				this.InLiqtot = InLiqtot;
			}

			public float getfrnLiqtot() {
				return frnLiqtot;
			}

			public void setfrnLiqtot(float frnLiqtot) {
				this.frnLiqtot = frnLiqtot;
			}
			
			//REV 1.13 Ends :Changes for Liquor Ends.
			
			//Rev 1.14 Starts here

			public String scOtp;
			/**
			 * @return the scOtp
			 */
			public String getScOtp() {
				return scOtp;
			}
			/**
			 * @param scOtp the scOtp to set
			 */
			public void setScOtp(String scOtp) {
				this.scOtp = scOtp;
			}
			 public String CustOgMobile;
			/**
			 * @return the custOgMobile
			 */
			public String getCustOgMobile() {
				return CustOgMobile;
			}
			/**
			 * @param custOgMobile the custOgMobile to set
			 */
			public void setCustOgMobile(String custOgMobile) {
				CustOgMobile = custOgMobile;
			}  
			public String ogTransaction;
			/**
			 * @return the ogTransaction
			 */
			public String getOgTransaction() {
				return ogTransaction;
			}
			/**
			 * @param ogTransaction the ogTransaction to set
			 */
			public void setOgTransaction(String ogTransaction) {
				this.ogTransaction = ogTransaction;
			}
			
			public String custMobileforOTP;
			/**
			 * @return the custMobileforOTP
			 */
			public String getCustMobileforOTP() {
				return custMobileforOTP;
			}
			/**
			 * @param custMobileforOTP the custMobileforOTP to set
			 */
			public void setCustMobileforOTP(String custMobileforOTP) {
				this.custMobileforOTP = custMobileforOTP;
			}
			//Rev 1.14 Ends here
			
			public String EmplyoeeDicsOtp;
			   /**
			 * @return the emplyoeeDicsOtp
			 */
			public String getEmplyoeeDicsOtp() {
				return EmplyoeeDicsOtp;
			}
			public void setEmplyoeeDicsOtp(String EmplyoeeDicsOtp) {
					this.EmplyoeeDicsOtp=EmplyoeeDicsOtp;
				}
			public String locale ="";
			public String getLocale() {
				// TODO Auto-generated method stub
			
				return this.locale;
			}
			public void setLocale(String value) {
				// TODO Auto-generated method stub
				
				this.locale = value;
			}
			public String EmpID="";
			 /**
			         * @return the emplyoeeDicsOtp
			   */
			public String getEmpID() {
				return EmpID;
			}
			public void setEmpID(String EmpID) {
			this.EmpID=EmpID;
			}
			
			 public String empAvailableAmt="";
			 
			public String getEmpAvailableAmt() {
				return empAvailableAmt;
			}
			public void setEmpAvailableAmt(String empAvailableAmt) {
			this.empAvailableAmt=empAvailableAmt;
		 }
			
		//Rev 2.9 ends
			
			//changes for Rev 2.10 start
					public boolean employeeOtpValidated = false;
					
					public boolean isEmployeeOtpValidated() {
						return employeeOtpValidated;
					}
					public void setEmployeeOtpValidated(boolean employeeOtpValidated) {
						this.employeeOtpValidated = employeeOtpValidated;
					}
					//Rev 2.10 ends

}
