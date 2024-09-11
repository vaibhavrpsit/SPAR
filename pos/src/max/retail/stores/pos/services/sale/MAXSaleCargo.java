/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *  Rev 1.2	    May 05,2020			Karni Singh			POS REQ: Register CRM customer with OTP.
 *  Rev 1.1     May 04, 2017	    Kritica Agarwal     GST Changes
 *	Rev	1.0 	Sep 13, 2016		Ashish Yadav		Changes done for code merging	
 *
 ********************************************************************************/

package max.retail.stores.pos.services.sale;

import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import max.retail.stores.domain.customer.MAXTICCustomerIfc;
import max.retail.stores.domain.singlebarcode.SingleBarCodeData;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.pos.services.sale.SaleCargo;

public class MAXSaleCargo extends SaleCargo implements MAXSaleCargoIfc {

	private static final long serialVersionUID = 4199802046418089913L;

	boolean scanNVoidFlow = false;
	boolean businessDateMismatchOverrideSuccess = false;
	boolean posOfflineAlertOverrideSuccess = false;
	protected SingleBarCodeData singleBarCodeData;
	protected Vector singleBarCodeVector;
	protected int singleBarCodeLineItem;
	protected boolean addSearchPLUItem = false;
	protected int index = -1;
	protected int length = -1;
	protected String rounding;
	protected List roundingDenominations;
	protected boolean isGiftCardApproved = false;
	protected boolean applyBestDeal = false;
	protected MAXTICCustomerIfc ticcustomer = null;
	protected PhoneIfc ticCustomerPhoneNo;
	protected boolean ticCustomerPhoneNoFlag;
	protected BigDecimal invoiceRuleAppliedRate = new BigDecimal("0.00");
	protected String initialOriginStationLetter = null;
	CurrencyIfc invoiceDiscountAmount = DomainGateway.getBaseCurrencyInstance();
	protected boolean invoiceRuleAlreadyApplied = false;
	public String necBarCode = null;
	protected boolean isNFCScan = false;
	public String categoryID = null;
	public String categoryDesc = null;
	
	//Rev 1.2 start
	private boolean isCRMEnrolmentOTPValidated=false;
	private String crmEnrolmentOTP = null;
	private int otpRetries = 0;
	//Rev 1.2 end
	public boolean empID = false;

	public boolean getEmpID() {
		return empID;
	}

	public void setEmpID(boolean empID) {
		this.empID = empID;
	}

	public boolean isNFCScan() {
	    return this.isNFCScan;
	  }

	  public void setNFCScan(boolean isNFCScan) {
	    this.isNFCScan = isNFCScan;
	  }

	public String getNecBarCode() {
		return necBarCode;
	}

	public void setNecBarCode(String necBarCode) {
		this.necBarCode = necBarCode;
	}

	public boolean isBusinessDateMismatchOverrideSuccess() {
		return businessDateMismatchOverrideSuccess;
	}

	public void setBusinessDateMismatchOverrideSuccess(boolean businessDateMismatchOverrideSuccess) {
		this.businessDateMismatchOverrideSuccess = businessDateMismatchOverrideSuccess;
	}

	public boolean isPosOfflineAlertOverrideSuccess() {
		return posOfflineAlertOverrideSuccess;
	}

	public void setPosOfflineAlertOverrideSuccess(boolean posOfflineAlertOverrideSuccess) {
		this.posOfflineAlertOverrideSuccess = posOfflineAlertOverrideSuccess;
	}

	public boolean isScanNVoidFlow() {
		return scanNVoidFlow;
	}

	public void setScanNVoidFlow(boolean scanNVoidFlow) {
		this.scanNVoidFlow = scanNVoidFlow;
	}

	public SingleBarCodeData getSingleBarCodeData() {
		return singleBarCodeData;
	}

	public void setSingleBarCodeData(SingleBarCodeData singleBarCodeData) {
		this.singleBarCodeData = singleBarCodeData;
	}

	public Vector getSingleBarCodeVector() {
		return singleBarCodeVector;
	}

	public void setSingleBarCodeVector(Vector singleBarCodeVector) {
		this.singleBarCodeVector = singleBarCodeVector;
	}

	public int getSingleBarCodeLineItem() {
		return singleBarCodeLineItem;
	}

	public void setSingleBarCodeLineItem(int singleBarCodeLineItem) {
		this.singleBarCodeLineItem = singleBarCodeLineItem;
	}

	public boolean isApplyBestDeal() {
		return applyBestDeal;
	}

	public void setApplyBestDeal(boolean applyBestDeal) {
		this.applyBestDeal = applyBestDeal;
	}

	public boolean isInvoiceRuleAlreadyApplied() {
		return invoiceRuleAlreadyApplied;
	}

	public void setInvoiceRuleAlreadyApplied(boolean invoiceRuleAlreadyApplied) {
		this.invoiceRuleAlreadyApplied = invoiceRuleAlreadyApplied;
	}

	public BigDecimal getInvoiceRuleAppliedRate() {
		return invoiceRuleAppliedRate;
	}

	public void setInvoiceRuleAppliedRate(BigDecimal invoiceRuleAppliedRate) {
		this.invoiceRuleAppliedRate = invoiceRuleAppliedRate;
	}

	public String getInitialOriginStationLetter() {
		return initialOriginStationLetter;
	}

	public void setInitialOriginStationLetter(String initialOriginStationLetter) {
		this.initialOriginStationLetter = initialOriginStationLetter;
	}

	public CurrencyIfc getInvoiceDiscountAmount() {
		return invoiceDiscountAmount;
	}

	public void setInvoiceDiscountAmount(CurrencyIfc invoiceDiscountAmount) {
		this.invoiceDiscountAmount = invoiceDiscountAmount;
	}

	public boolean isAddSearchPLUItem() {
		return addSearchPLUItem;
	}

	public void setAddSearchPLUItem(boolean addSearchPLUItem) {
		this.addSearchPLUItem = addSearchPLUItem;
	}

	public MAXTICCustomerIfc getTicCustomer() {
		return ticcustomer;
	}

	public void setTicCustomer(MAXTICCustomerIfc ticcustomer) {
		this.ticcustomer = ticcustomer;
	}

	public PhoneIfc getTicCustomerPhoneNo() {
		return ticCustomerPhoneNo;
	}

	public void setTicCustomerPhoneNo(PhoneIfc ticCustomerPhoneNo) {
		this.ticCustomerPhoneNo = ticCustomerPhoneNo;
	}

	public boolean isTicCustomerPhoneNoFlag() {
		return ticCustomerPhoneNoFlag;
	}

	public void setTicCustomerPhoneNoFlag(boolean ticCustomerPhoneNoFlag) {
		this.ticCustomerPhoneNoFlag = ticCustomerPhoneNoFlag;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setRounding(String rounding) {
		this.rounding = rounding;
	}

	public String getRounding() {
		return rounding;
	}

	public void setRoundingDenominations(List roundingDenominations) {
		this.roundingDenominations = roundingDenominations;
	}

	public List getRoundingDenominations() {
		return roundingDenominations;
	}

	public boolean isCustomerEnabled() {
		boolean enabled = true;
		if (transaction != null) {

			Enumeration e = transaction.getLineItemsVector().elements();
			while (e.hasMoreElements()) {
				if (e.nextElement() instanceof OrderLineItemIfc) {
					enabled = false;
				}
			}
		}
		return (enabled);
	}

	public boolean isGiftCardApproved() {
		return isGiftCardApproved;
	}

	public void setGiftCardApproved(boolean isGiftCardApproved) {
		this.isGiftCardApproved = isGiftCardApproved;
	}

	public RetailTransactionIfc getRetailTransactionIfc() {
		return transaction;
	}
	//Change for Rev 1.1 : Starts
	protected Boolean interStateDelivery = false;
	protected String fromRegion;
	protected String toRegion;

	/**
	 * @return the fromRegion
	 */
	@Override
	public String getFromRegion() {
		return fromRegion;
	}

	/**
	 * @param fromRegion the fromRegion to set
	 */
	@Override
	public void setFromRegion(String fromRegion) {
		this.fromRegion = fromRegion;
	}

	/**
	 * @return the toRegion
	 */
	@Override
	public String getToRegion() {
		return toRegion;
	}

	/**
	 * @param toRegion the toRegion to set
	 */
	@Override
	public void setToRegion(String toRegion) {
		this.toRegion = toRegion;
	}

	/**
	 * @return the interStateDelivery
	 */
	@Override
	public Boolean getInterStateDelivery() {
		return interStateDelivery;
	}

	/**
	 * @param interStateDelivery the interStateDelivery to set
	 */
	@Override
	public void setInterStateDelivery(Boolean interStateDelry) {
		interStateDelivery = interStateDelry;
	}
//Change for Rev 1.1 : Ends

	@Override
	public String getCategoryIDScanSheet() {
		// TODO Auto-generated method stub
		return categoryID;
	  }

	@Override
	public void setCategoryIDScanSheet(String categoryID) {
		// TODO Auto-generated method stub
	    this.categoryID = categoryID;
	  }

	@Override
	public String getCategoryDescripionScanSheet() {
		// TODO Auto-generated method stub
		return categoryDesc;
	  }

	@Override
	public void setCategoryDescripionScanSheet(String categoryDesc) {
		// TODO Auto-generated method stub
	    this.categoryDesc = categoryDesc;
	  }
	
//Change for Rev 1.1 : Ends

//Rev 1.2 start
	public boolean isCRMEnrolmentOTPValidated() {
		return isCRMEnrolmentOTPValidated;
	}

	public void setCRMEnrolmentOTPValidated(boolean isCRMEnrolmentOTPValidated) {
		this.isCRMEnrolmentOTPValidated = isCRMEnrolmentOTPValidated;
	}			

	public String getCrmEnrolmentOTP() {
		return crmEnrolmentOTP;
	}

	public void setCrmEnrolmentOTP(String crmEnrolmentOTP) {
		this.crmEnrolmentOTP = crmEnrolmentOTP;
	}
	public int getOtpRetries() {
		return otpRetries;
	}

	public void setOtpRetries(int otpRetries) {
		this.otpRetries = otpRetries;
	}
	//Rev 1.2 end
	
	public void setTransaction(MAXSaleReturnTransactionIfc transaction) {
		this.transaction = (RetailTransactionIfc) transaction;
	}

	public MAXSaleReturnTransactionIfc getTransaction() {
		return (MAXSaleReturnTransactionIfc) this.transaction;
	}
	
	public String LiqBarCode;

	public String getLiqBarCode() {
		return LiqBarCode;
	}

	public void setLiqBarCode(String liqBarCode) {
		LiqBarCode = liqBarCode;
	}

}
