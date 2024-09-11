/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev 2.0 	May 14, 2024		Kamlesh Pant		Store Credit OTP:
 *  Rev 1.1     Dec 28, 2016	    Ashish Yadav		Changes for Employee Discount FES
 *  Rev 1.2     Sep 01, 2020	    Kumar Vaibhav		Pinelabs Integration
 *
 ********************************************************************************/
package max.retail.stores.domain.transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import max.retail.stores.domain.bakery.MAXBakeryItemIfc;
import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.discountCoupon.MAXDiscountCouponIfc;
import max.retail.stores.domain.gstin.MAXGSTINValidationResponseIfc;
import max.retail.stores.domain.lineitem.MAXItemContainerProxy;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.mcoupon.MAXMcouponIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.tax.SendTaxUtil;
import oracle.retail.stores.domain.tax.TaxRulesVO;
import oracle.retail.stores.domain.transaction.LayawayTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;

public class MAXLayawayTransaction extends LayawayTransaction implements
		MAXSaleReturnTransactionIfc {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4829699481425373134L;
	protected Vector<MAXDiscountCouponIfc> capillaryCouponsApplied = new Vector<MAXDiscountCouponIfc>();
	protected List bdwList = new ArrayList();
	protected String printFreeItem = null;
	protected CustomerIfc ticCustomer = null;
	// Changes starts for Rev 1.1 (Ashish : Employee Discount)
	protected String discountEmployeeName = null;
	protected String empDiscountAvailLimit = null;
	//  below method is added by atul shukla for employee discount company Name
	protected String companyName = null;
	// Changes starts for Rev 1.1 (Ashish : Employee Discount)
	
	//changes for paytmqr
	protected String paytmQROrderId = null;
	
	/*
	 * GST changes
	 */
	public String gstNumber = "";
	public String  gstStoreNumber = "";
	
	protected byte[] fileData;

	protected MAXGSTINValidationResponseIfc response = null;

	public void clearEmployeeDiscount(TransactionDiscountStrategyIfc tds) {
		Iterator itr = itemProxy.getTransactionDiscountsIterator();
		while (itr.hasNext()) {
			if (itr.next() == tds) {
				itr.remove();
				break;
			}
		}
	}

	public SaleReturnLineItemIfc addPLUItem(PLUItemIfc pItem, BigDecimal qty,
			boolean isApplyBestDeal) {
		SaleReturnLineItemIfc srli = ((MAXItemContainerProxy) itemProxy)
				.addPLUItem(pItem, qty, isApplyBestDeal);
		addItemByTaxGroup();
		doTaxCalculationForAllLineItems();
		if (this.getSendTaxRules() != null
				&& ((SaleReturnTransaction) this.getTransactionTotals())
						.isTransactionLevelSendAssigned()) {
			SendTaxUtil util = new SendTaxUtil();
			TaxRulesVO taxRulesVO = this.getSendTaxRules();
			util.setTaxRulesForLineItem(taxRulesVO, srli);
		}
		updateTransactionTotals(getItemContainerProxy().getLineItems(),
				getItemContainerProxy().getTransactionDiscounts(),
				getItemContainerProxy().getTransactionTax());
		return (srli);
	}

	private void doTaxCalculationForAllLineItems() {
		// TODO Auto-generated method stub

	}

	public List getBdwList() {
		return bdwList;
	}

	public void setBdwList(List bdwList) {
		this.bdwList = bdwList;
	}

	public Object clone() {
		MAXLayawayTransaction srt = new MAXLayawayTransaction();

		setCloneAttributes(srt);

		return srt;
	}

	public String getPrintFreeItem() {
		return printFreeItem;
	}

	public void setPrintFreeItem(String printFreeItem) {
		this.printFreeItem = printFreeItem;
	}

	public ArrayList getBestDealWinners() {
		return itemProxy.getBestDealWinners();
	}

	protected void setCloneAttributes(MAXLayawayTransaction newClass) {
		super.setCloneAttributes(newClass);
		if (this.getPrintFreeItem() != null) {
			newClass.setPrintFreeItem(this.getPrintFreeItem());
		}
		if (this.getBdwList().size() > 0) {
			newClass.setBdwList(this.getBdwList());
		}
		// Changes starts for Rev 1.1 (Ashish : Employee Discount)
		newClass.setEmpDiscountAvailLimit(empDiscountAvailLimit);
		newClass.setDiscountEmployeeName(discountEmployeeName);
		// Changes ends for Rev 1.1 (Ashish : Employee Discount)
		
	}

	

	// Changes starts for rev 1.2 
		@Override
		public List getDeliveryItems() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void addDeliveryItems(MAXSaleReturnLineItemIfc deliveryItem) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public ArrayList<MAXMcouponIfc> getMcouponList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setMcouponList(ArrayList<MAXMcouponIfc> mcouponList) {
			// TODO Auto-generated method stub
			
		}
		// Changes ends for rev 1.2 
	public Vector<MAXDiscountCouponIfc> getCapillaryCouponsApplied() {
		return capillaryCouponsApplied;
	}

	public void addCapillaryCouponsApplied(MAXDiscountCouponIfc discountCoupon) {
		capillaryCouponsApplied.addElement(discountCoupon);
	}

	public void removeCapillaryCouponsApplied() {
		capillaryCouponsApplied.removeAllElements();
	}

	public String getDiscountEmployeeRole() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDiscountEmployeeRole(String discountEmployeeRole) {
		// TODO Auto-generated method stub

	}
	// Changes starts for Rev 1.1 (Ashish : Employee Discount)

	public String getDiscountEmployeeName() {
		// TODO Auto-generated method stub
		return discountEmployeeName;
	}

	public void setDiscountEmployeeName(String discountEmployeeName) {
		this.discountEmployeeName = discountEmployeeName;

	}
	// Changes ends for Rev 1.1 (Ashish : Employee Discount)

	public String getDiscountEmployeeLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDiscountEmployeeLocation(String discountEmployeeLocation) {
		// TODO Auto-generated method stub

	}

	public boolean isTaxTypeLegal() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setTaxTypeLegal(boolean taxTypeLegal) {
		// TODO Auto-generated method stub

	}

	public void replaceLineItemWithoutBestDeal(
			AbstractTransactionLineItemIfc lineItem, int index) {
		// TODO Auto-generated method stub

	}
// Changes starts for rev 1.1 (Ashish : Employee Discount)
	public String getEmpDiscountAvailLimit() {
		// TODO Auto-generated method stub
		return empDiscountAvailLimit;
	}

	public void setEmpDiscountAvailLimit(String empDiscountAvailLimit) {
		this.empDiscountAvailLimit = empDiscountAvailLimit;

	}
	// Changes starts for rev 1.1 (Ashish : Employee Discount)
	public int getNthCoupon() {
		// TODO Auto-generated method stub
		return 0;
	}

	// Rev
	public void setNthCoupon(int couponCount) {
		// TODO Auto-generated method stub

	}

	public void setItemLevelDiscount(boolean isItemLevelDiscount) {
		// TODO Auto-generated method stub

	}

	public boolean isItemLevelDiscount() {
		// TODO Auto-generated method stub
		return false;
	}

	// Rev 1.1 : Start
	public boolean isSendTransaction() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setSendTransaction(boolean flag) {
		// TODO Auto-generated method stub

	}

	// Rev 1.1 : End
	public int getItemLevelCouponCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setItemLevelCouponCount(int itemLevelCouponCount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMAXTICCustomer(MAXCustomerIfc ticcustomer) {
		// TODO Auto-generated method stub

	}

	@Override
	public MAXCustomerIfc getMAXTICCustomer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isTicCustomerVisibleFlag() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setTicCustomerVisibleFlag(boolean ticCustomerVisibleFlag) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean itemsSellingPriceExceedsMRP() {
		// TODO Auto-generated method stub
		return false;
	}

	public CustomerIfc getTicCustomer() {
		return ticCustomer;
	}

	public void setTicCustomer(CustomerIfc ticCustomer) {
		this.ticCustomer = ticCustomer;
		if (this.customer == null)
			this.customer = this.ticCustomer;
	}
	 public void modifyFee(CurrencyIfc value)
	  {
	    // adds change in fee to grand total
	    totals.setLayawayFee(totals.getLayawayFee().add(value));
	    totals.calculateGrandTotal();
	    /* India Localization -Tax Changes Starts Here */
		// From GrandTotal Subtract the total tax Amount since for India
		// Localization, Selling Retail is always inclusive of Tax.
		totals.setGrandTotal(totals.getGrandTotal().subtract(totals.getTaxTotal()));

		/* India Localization -Tax Changes ends Here */
	  }

	@Override
	public Map<String, String> getTaxCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTaxCode(Map<String, String> taxCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HashMap<String, String> getStates() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStates(HashMap<String, String> states) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getHomeStateCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setHomeStateCode(String homeStateCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getToState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setToState(String toState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getHomeState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setHomeState(String homeState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isIgstApplicable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setIgstApplicable(boolean igstApplicable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isGstEnable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setGstEnable(boolean gstEnable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDeliverytrnx() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setDeliverytrnx(boolean deliverytrnx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isCaptureCustomer() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCaptureCustomer(boolean captureCustomer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getEmployeeCompanyName() {
		// TODO Auto-generated method stub
		return companyName;
	}

	@Override
	public void setEmployeeCompanyName(String companyName) {
		// TODO Auto-generated method stub
		this.companyName=companyName;
		
	}
	
		public Vector<MAXBakeryItemIfc> getScansheetLineItemsVector() {
		// TODO Auto-generated method stub
		return null;
	}

		@Override
		public String getPanNumber() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setPanNumber(String panNumber) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getForm60IDNumber() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setForm60IDNumber(String idnumber) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getPassportNumber() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setPassportNumber(String passportNum) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getVisaNumber() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setVisaNumber(String visaNum) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getITRAckNumber() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setITRAckNumber(String ackNum) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isDuplicateReceipt() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setDuplicateReceipt(boolean duplicateReceipt) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public BigDecimal getEasyBuyTotals() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setEasyBuyTotals(BigDecimal easyBuyTotals) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String getEReceiptOTP() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setEReceiptOTP(String eReceiptOTP) {
			// TODO Auto-generated method stub
			
		}

	/*@Override
	public ArrayList getPaytmResponse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPaytmResponse(MAXPaytmResponse response) {
		// TODO Auto-generated method stub
		
	}
*/
		//Changes starts for Rev 1.2 (Vaibhav : PineLab)
				protected String edcType = null;

				public String getEdcType() {
					return edcType;
				}
				public void setEdcType(String edcType) {
					this.edcType = edcType;
				}
				
				//Changes ends for Rev 1.2 (Vaibhav : PineLab)
				public boolean isEWalletTenderFlag = false;
				public boolean sbiRewardredeemFlag = false;
				public boolean isSbiRewardredeemFlag() {
					return sbiRewardredeemFlag;
				}
				public void setSbiRewardredeemFlag(boolean sbiRewardredeemFlag) {
					this.sbiRewardredeemFlag = sbiRewardredeemFlag;
				}

	@Override
	public boolean isEWalletTenderFlag() {
		// TODO Auto-generated method stub
		return isEWalletTenderFlag;
	}

	@Override
	public void setEWalletTenderFlag(boolean isEWalletTenderFlag) {
		// TODO Auto-generated method stub

	}

	@Override
	public String geteWalletTraceId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void seteWalletTraceId(String eWalletTraceId) {
		// TODO Auto-generated method stub

	}

	@Override
	public HashMap getManagerOverrideMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setManagerOverrideMap(HashMap managerOverrideMap) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isRtsManagerOverride() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setRtsManagerOverride(boolean rtsManagerOverride) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getSubmitinvresponse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSubmitinvresponse(String submitinvresponse) {
		// TODO Auto-generated method stub
		
	}

	//changes for paytmqr
	public String getPaytmQROrderId() {
		return paytmQROrderId;
	}
	public void setPaytmQROrderId(String paytmQROrderId) {
		this.paytmQROrderId = paytmQROrderId;
	}
	
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

	
/*
* public HashMap getManagerOverrideMap() { return managerOverrideMap; }
* 
* public void setManagerOverrideMap(HashMap managerOverrideMap) {
* this.managerOverrideMap= managerOverrideMap;
* 
* }
*/
	
	public void setReceiptData(byte[] readerData) {
		this.fileData=readerData;
	}
	
	public byte[] getReceiptData() {
		return this.fileData;
	}

	@Override
	public float getBeertot() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setBeertot(float beertot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getliquortot() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setliquortot(float liquorTotal) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getInLiqtot() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setInLiqtot(float InLiqtot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getfrnLiqtot() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setfrnLiqtot(float frnLiqtot) {
		// TODO Auto-generated method stub
		
	}
	
	//Rev 1.3
	
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
	public boolean employeeOtpValidated = false;
	
	public boolean isEmployeeOtpValidated() {
		return employeeOtpValidated;
	}
	public void setEmployeeOtpValidated(boolean employeeOtpValidated) {
		this.employeeOtpValidated = employeeOtpValidated;
	}

	//Rev 2.0 Starts
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
			//changes for Rev 2.0 end
}
