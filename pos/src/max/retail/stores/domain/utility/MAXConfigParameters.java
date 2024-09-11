/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 2010 Lifestyle India Pvt Ltd.    All Rights Reserved. 
 * Rev 2.5	May 14, 2024	Kamlesh Pant		Store Credit OTP:
 * Rev 2.4  June 29, 2020   Nitika Arora        Changes for Whatsapp Integration Functionality  
 * Rev 2.3	6th June 2019	Vidhya Kommareddi	POS REQ: Print Last Day for Exchange on receipt.
 * Rev 2.2	6th June 2019	Vidhya Kommareddi	POS REQ: Block suspend after N suspends
 * Rev 2.1	22nd May 2019	Vidhya Kommareddi	POS does not have to calculate expiry date of Gift Card anymore. QC will be doing it on their end.
 * Rev 2.0	15/10/2018		Jyoti Yadav			LS Edge Phase 2
 * Rev 1.10	10/08/2018		Jyoti Yadav			Quoting PAN CR
 * Rev 1.9  July 28,2018 	Nitika Arora        Changes for ADSR(Till Reconcile) Functionality  
 * Rev 1.8	18/04/2018		Kritica Goel 	    Layaway calculation change
 * Rev 1.7  Apr 06,2018  	Ashish Yadav		Allowing adding items during retrieve transaction (Non furniture Items) suspended from MPOS 
 * Rev 1.6	26/01/2018		Anoop Seth  	    GC Redemption Cross OU changes
 * Rev 1.5	24/12/2017		Shilpa Rawal  	    GC_eGV_CN Redemption Cross OU changes
 * Rev 1.4	03/01/2017		Kritica Agarwal  	GST Changes
 * Rev 1.3  01/08/2016 		Akhilesh Kumar		Mcoupon capillary integration
 * Rev 1.2  29/06/2013 		Karandeep Singh		Change for Dept Check
 * Rev 1.1  5/June/2013		Karandeep Singh     TIC Preview Sale requirement.
 * Rev 1.0 LS_ORPOS_VAT_Extra_v1.0 26/05/2013 Geetika.chugh
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.utility;

import java.math.BigDecimal;

import oracle.retail.stores.domain.utility.EYSDate;

/**
 * @author geetika.chugh
 *
 */

public class MAXConfigParameters implements MAXConfigParametersIfc {

	/**
	 * store and retreive parameter values from db
	 */
	private static final long serialVersionUID = 1L;

	public boolean calculateVatExtraFlag;

	protected String eossMonths;

	protected int vatLimit;

	protected int discountLimit;

	protected String vatExtraDepts;

	// change for Rev 1.1 : Start
	protected EYSDate previewSaleStartDate;

	protected EYSDate previewSaleEndDate;

	/* Rev 1.3 start */
	protected boolean issueCouponEnable;
	/* Rev 1.3 end */
	
	/* Rev 1.4 start */
	protected boolean gstEnable;
	/* Rev 1.4 end */

	/* Rev 1.5 start */
	protected String organizationUnit;
	/* Rev 1.5 end */

	/* Rev 1.6 start */
	protected String binRange;
	/* Rev 1.6 end */
	/*Change for Rev 1.10: Start*/
	protected String PANThreshold;
	/*Change for Rev 1.10: End*/
	

	protected String MAXQOCNIssueamount;


	protected boolean cessEnable;
	//Rev 2.1 start end --commented out below line
	//protected String gcExpExtnDays;
	//Rev 2.2 start 
	protected String maxNumOfSuspends;
	//Rev 2.2 end

	//Rev 2.3 start 
	protected String returnDays;
	//Rev 2.3 end	

	protected String gstInvoice;
		
	//Changes starts for Rev 1.7(Ashish)
	/*protected boolean furnitureNonFurnitureEnable;
	protected int furnitureRegisterNumber;
	
	
	public Boolean isFurnitureNonFurnitureEnable() {
		return furnitureNonFurnitureEnable;
	}

	public void setFurnitureNonFurnitureEnable(boolean furnitureNonFurnitureEnable) {
		this.furnitureNonFurnitureEnable = furnitureNonFurnitureEnable;
	}

	public int getFurnitureRegisterNumber() {
		return furnitureRegisterNumber;
	}

	public void setFurnitureRegisterNumber(int furnitureRegisterNumber) {
		this.furnitureRegisterNumber = furnitureRegisterNumber;
	}
	*/
	//Changes ends for rev 1.7(Ashish)
	
	//changes for paytmqr
	protected int paytmQRStatusCheckRetryCount;

	@Override
	public EYSDate getPreviewSaleStartDate() {
		return previewSaleStartDate;
	}

	@Override
	public void setPreviewSaleStartDate(EYSDate previewSaleStartDate) {
		this.previewSaleStartDate = previewSaleStartDate;
	}

	@Override
	public EYSDate getPreviewSaleEndDate() {
		return previewSaleEndDate;
	}

	@Override
	public void setPreviewSaleEndDate(EYSDate previewSaleEndDate) {
		this.previewSaleEndDate = previewSaleEndDate;
	}

	// change for Rev 1.1 : End
	@Override
	public boolean getCalculateVatExtraFlag() {
		return calculateVatExtraFlag;
	}

	@Override
	public void setCalculateVatExtra(boolean calculateVatExtra) {
		this.calculateVatExtraFlag = calculateVatExtra;
	}

	@Override
	public String getEossMonths() {
		return eossMonths;
	}

	@Override
	public void setEossMonths(String eossMonths) {
		this.eossMonths = eossMonths;
	}

	@Override
	public int getVatLimit() {
		return vatLimit;
	}

	@Override
	public void setVatLimit(int vatLimit) {
		this.vatLimit = vatLimit;
	}

	@Override
	public int getDiscountLimit() {
		return discountLimit;
	}

	@Override
	public void setDiscountLimit(int discountLimit) {
		this.discountLimit = discountLimit;
	}

	/** Change for Rev 1.2 : Start */
	/**
	 * @return the vatExtraDepts
	 */
	@Override
	public String getVatExtraDepts() {
		return vatExtraDepts;
	}

	/**
	 * @param vatExtraDepts
	 *            the vatExtraDepts to set
	 */
	@Override
	public void setVatExtraDepts(String vatExtraDepts) {
		this.vatExtraDepts = vatExtraDepts;
	}
	/** Change for Rev 1.2 : Start */

	/* changes for Rev 1.3 start */
	/**
	 * @return the issueCouponEnable
	 */
	@Override
	public Boolean isIssueCouponEnable() {
		return issueCouponEnable;
	}

	/**
	 * @param issueCouponEnable
	 *            the issueCouponEnable to set
	 */
	public void setIssueCouponEnable(boolean issueCouponEnable) {
		this.issueCouponEnable = issueCouponEnable;
	}

	/* changes for Rev 1.3 end */

	/* changes for Rev 1.4 start */
	/**
	 * @return the GSTEnable
	 */
	@Override
	public Boolean isGSTEnable() {
		return gstEnable;
	}

	/**
	 * @param GSTEnable
	 *            the GSTEnable to set
	 */
	public void setGSTEnable(boolean gstEnable) {
		this.gstEnable = gstEnable;
	}
	
	public String thresholdTax;

	/**
	 * @return the thresholdTax
	 */
	public String getThresholdTax() {
		return thresholdTax;
	}

	/**
	 * @param thresholdTax the thresholdTax to set
	 */
	public void setThresholdTax(String thresholdTax) {
		this.thresholdTax = thresholdTax;
	}
	

	/* changes for Rev 1.4 end */

	

	/** Change for Rev 1.5 : Start */
	/**
	 * @return the organizationUnitId
	 */
	
	@Override
	public String getOraganizationUnit() {
		
		return organizationUnit;
	}
	
	/**
	 * @param organizationUnitId
	 *            the organizationUnitId to set
	 */

	@Override
	public void setOraganizationUnit(String organizationUnit) {
		this.organizationUnit=organizationUnit;
		
	}	

	/* changes for Rev 1.5 end */

	/* changes for Rev 1.6 Start */
	/**
	 * @return the binRange
	 */
	public String getBinRange() {
		return binRange;
	}

	/**
	 * @param binRange the binRange to set
	 */
	public void setBinRange(String binRange) {
		this.binRange = binRange;
	}
	/* changes for Rev 1.6 end */
//Change for Rev 1.8 : Starts
	public  boolean layawayCalculationChange= false;


	/**
	 * @return the layawayCalculationChange
	 */
	public boolean isLayawayCalculationChange() {
		return layawayCalculationChange;
	}

	/**
	 * @param layawayCalculationChange the layawayCalculationChange to set
	 */
	public void setLayawayCalculationChange(boolean layawayCalculationChange) {
		this.layawayCalculationChange = layawayCalculationChange;
	}
//Change for Rev 1.8 : Ends
	//Change for Rev 1.9 : Starts
	protected String dsrDays;
	
	public String getDsrDays() {
		return dsrDays;
	}

	public void setDsrDays(String dsrDays) {
		this.dsrDays = dsrDays;
	}

	public  boolean dsrEnableFlag= false;

	public boolean isDsrEnableFlag() {
		return dsrEnableFlag;
	}

	public void setDsrEnableFlag(boolean dsrEnableFlag) {
		this.dsrEnableFlag = dsrEnableFlag;
	}
	//Change for Rev 1.9 : Ends
	/*Change for Rev 1.10: Start*/
	public String getPANThreshold() {
		return PANThreshold;
	}
	public void setPANThreshold(String PANThreshold) {
		this.PANThreshold = PANThreshold;
	}
	/*Change for Rev 1.10: End*/
	
	
	public String getMAXQOCNIssueamount() {
		return MAXQOCNIssueamount;
	}
	public void setMAXQOCNIssueamount(String MAXQOCNIssueamount) {
		this.MAXQOCNIssueamount = MAXQOCNIssueamount;
	}
	
	
	/*Change for Rev 2.0: Start*/
	protected EYSDate edgePreviewSaleStartDate;
	protected EYSDate edgePreviewSaleEndDate;
	protected String edgeItemValues;
	protected String edgeName;
	
	public EYSDate getEdgePreviewSaleStartDate() {
		return edgePreviewSaleStartDate;
	}
	public void setEdgePreviewSaleStartDate(EYSDate edgePreviewSaleStartDate) {
		this.edgePreviewSaleStartDate = edgePreviewSaleStartDate;
	}

	public EYSDate getEdgePreviewSaleEndDate() {
		return edgePreviewSaleEndDate;
	}
	public void setEdgePreviewSaleEndDate(EYSDate edgePreviewSaleEndDate) {
		this.edgePreviewSaleEndDate = edgePreviewSaleEndDate;
	}
	
	public String getEdgeItemValues() {
		return edgeItemValues;
	}
	public void setEdgeItemValues(String edgeItemValues) {
		this.edgeItemValues = edgeItemValues;
	}	
	/*Change for Rev 2.0: End*/
	
// cess changes start here
	/**
	 * @return the cessEnable
	 */
	public boolean isCessEnable() {
		return cessEnable;
	}

	/**
	 * @return the edgeName
	 */
	public String getEdgeName() {
		return edgeName;
	}

	/**
	 * @param edgeName the edgeName to set
	 */
	public void setEdgeName(String edgeName) {
		this.edgeName = edgeName;
	}

	/**
	 * @param cessEnable the cessEnable to set
	 */
	public void setCessEnable(boolean cessEnable) {
		this.cessEnable = cessEnable;
	}
	
// cess changes end here	
	//Rev 2.1 start 
	
	/**
	 * @return the gcExpExtnDays
	 */
	/*public String getGcExpExtnDays() {
		return gcExpExtnDays;
	}*/

	/**
	 * @param gcExpExtnDays the gcExpExtnDays to set
	 */
	/*public void setGcExpExtnDays(String gcExpExtnDays) {
		this.gcExpExtnDays = gcExpExtnDays;
	}*/
	//Rev 2.1 end
	//Rev 2.2 start 	
	/**
	 * @return the maxNumOfSuspends
	 */
	public String getMaximumNumOfSuspends() {
		return maxNumOfSuspends;
	}

	/**
	 * @param maxNumOfSuspends the maxNumOfSuspends to set
	 */
	public void setMaximumNumOfSuspends(String maxNumOfSuspends) {
		this.maxNumOfSuspends = maxNumOfSuspends;
	}
	//Rev 2.2 end

	//Rev 2.3 start --returndays

	/**
	 * @return the returnDays
	 */
	public String getReturnDays() {
		return returnDays;
	}

	/**
	 * @param returnDays the returnDays to set
	 */
	public void setReturnDays(String returnDays) {
		this.returnDays = returnDays;
	}

	//Rev 2.3 end --returndays
	
	//Changes starts for rev 1.1 (Ashish: Easy Exchnage)
	protected String enableEasyExchange;

	public String getEnableEasyExchange() {
		return enableEasyExchange;
	}
	public void setEnableEasyExchange(String enableEasyExchange) {
		this.enableEasyExchange = enableEasyExchange;
	}
	
	
	//Changes ends for rev 1.1 (Ashish: Easy Exchnage)
	
	//Changes starts for rev 1.1 (Ashish: Easy Exchnage)
		protected String enableYouthCard;
		protected String promotionalYouthCard;
		protected int youthCardDiscPerc;
		protected int youthCardInitialAmount;
		protected String youthCardExpiry;
		protected String youthCardItem;
		protected String youthThresholdAmount;

		public String getEnableYouthCard() {
			return enableYouthCard;
		}
		public void setEnableYouthCard(String enableYouthCard) {
			this.enableYouthCard = enableYouthCard;
		}
		public String getPromotionalYouthCard() {
			return promotionalYouthCard;
		}

		public void setPromotionalYouthCard(String promotionalYouthCard) {
			this.promotionalYouthCard = promotionalYouthCard;
		}

		public int getYouthCardDiscPerc() {
			return youthCardDiscPerc;
		}

		public void setYouthCardDiscPerc(int youthCardDiscPerc) {
			this.youthCardDiscPerc = youthCardDiscPerc;
		}

		public int getYouthCardInitialAmount() {
			return youthCardInitialAmount;
		}
		public void setYouthCardInitialAmount(int youthCardInitialAmount) {
			this.youthCardInitialAmount = youthCardInitialAmount;
		}
		
		public String getYouthCardExpiry() {
			return youthCardExpiry;
		}
		public void setYouthCardExpiry(String youthCardExpiry) {
			this.youthCardExpiry = youthCardExpiry;
		}
		public String getYouthCardItem() {
			return youthCardItem;
		}
		public void setYouthCardItem(String youthCardItem) {
			this.youthCardItem = youthCardItem;
		}

public String getYouthThresholdAmount() {
			return youthThresholdAmount;
		}
		public void setYouthThresholdAmount(String youthThresholdAmount) {
			this.youthThresholdAmount = youthThresholdAmount;
		}
		
		
		//Changes ends for rev 1.1 (Ashish: Easy Exchnage)
		
		//Changes starts for Rev 1.1 (Ashish : EReceipt)
		protected String eReceiptConf;
		protected String eReceiptTrantype;

		public String geteReceiptConf() {
			return eReceiptConf;
		}

		public void seteReceiptConf(String eReceiptConf) {
			this.eReceiptConf = eReceiptConf;
		}
		public String geteReceiptTrantype() {
			return eReceiptTrantype;
		}
		public void seteReceiptTrantype(String eReceiptTrantype) {
			this.eReceiptTrantype = eReceiptTrantype;
		}
		
		
		//Changes ends for Rev 1.1 (Ashish : EReceipt)
		//Change for Rev 2.4 : Starts
		protected String storeId;

		public String getStoreId() {
			return storeId;
		}

		public void setStoreId(String storeId) {
			this.storeId = storeId;
		}
		//Change for Rev 2.4 : Ends

		/**
		 * @return the gstInvoice
		 */
		public String getGstInvoice() {
			return gstInvoice;
		}

		/**
		 * @param gstInvoice the gstInvoice to set
		 */
		public void setGstInvoice(String gstInvoice) {
			this.gstInvoice = gstInvoice;
		}
		
		protected boolean sbiPointConversion;
		protected int sbiMinPoint;
		protected int sbiPointConversionRate;

		/**
		 * @return the sbiPointConversion
		 */
		public boolean isSbiPointConversion() {
			return sbiPointConversion;
		}

		/**
		 * @param sbiPointConversion the sbiPointConversion to set
		 */
		public void setSbiPointConversion(boolean sbiPointConversion) {
			this.sbiPointConversion = sbiPointConversion;
		}

		/**
		 * @return the sbiMinPoint
		 */
		public int getSbiMinPoint() {
			return sbiMinPoint;
		}

		/**
		 * @param sbiMinPoint the sbiMinPoint to set
		 */
		public void setSbiMinPoint(int sbiMinPoint) {
			this.sbiMinPoint = sbiMinPoint;
		}

		/**
		 * @return the sbiPointConversionRate
		 */
		public int getSbiPointConversionRate() {
			return sbiPointConversionRate;
		}

		/**
		 * @param sbiPointConversionRate the sbiPointConversionRate to set
		 */
		public void setSbiPointConversionRate(int sbiPointConversionRate) {
			this.sbiPointConversionRate = sbiPointConversionRate;
		}
		
		//changes for paytmqr
		public int getPaytmQRStatusCheckRetryCount() {
			return paytmQRStatusCheckRetryCount;
		}

		public void setPaytmQRStatusCheckRetryCount(int paytmQRStatusCheckRetryCount) {
			this.paytmQRStatusCheckRetryCount = paytmQRStatusCheckRetryCount;
		}

		//changes by shyvanshu mehra...
		protected BigDecimal CashLimitParameter;
		
		public BigDecimal getCashLimitParameter() {
			// TODO Auto-generated method stub
			return CashLimitParameter;
		}



		public void setCashLimitParameter(BigDecimal CashLimitParameter) {
			// TODO Auto-generated method stub
			this.CashLimitParameter = CashLimitParameter;
			
		}

	/*
	 * @Override public boolean isSpclEmpDisc() { // TODO Auto-generated method stub
	 * return false; }
	 * 
	 * @Override public void setSpclEmpDisc(boolean spclEmpDisc) { // TODO
	 * Auto-generated method stub
	 * 
	 * }
	 */
		
		protected boolean spclEmpDisc;
		public boolean isSpclEmpDisc() 
		{
			 return spclEmpDisc;
		}
		public void setSpclEmpDisc(boolean spclEmpDisc)
		{
			this.spclEmpDisc = spclEmpDisc;
		}		
		
		//Rev 2.5 Starts
			protected int scOtpRetries;
			public int getScOtpRetries() {
					return scOtpRetries;
			}
			public void setScOtpRetries(int scOtpRetries) {
				this.scOtpRetries = scOtpRetries;
			}
		//Rev 2.5 ends		
			// added by Vaibhav for emp disc otp
			protected boolean EmpOtpEnableCheck=false;
			public boolean isEmpOtpEnableCheck() {
				
			return EmpOtpEnableCheck;
			}
			public void setEmpOtpEnableCheck(boolean EmpOtpEnableCheck) {
			this.EmpOtpEnableCheck = EmpOtpEnableCheck;
			}

			// end
}
