/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 2010 Lifestyle India Pvt Ltd.    All Rights Reserved .
 * Rev 2.5	May 14, 2024	Kamlesh Pant		Store Credit OTP:
 * Rev 2.4  June 29, 2020   Nitika Arora        Changes for Whatsapp Integration Functionality 
 * Rev 2.3	6th June 2019	Vidhya Kommareddi	POS REQ: Print Last Day for Exchange on receipt.
 * Rev 2.2	6th June 2019	Vidhya Kommareddi	POS REQ: Block suspend after N suspends
 * Rev 2.1	22nd May 2019	Vidhya Kommareddi	POS does not have to calculate expiry date of Gift Card anymore. QC will be doing it on their end.
 * Rev 2.0	15/10/2018		Jyoti Yadav			LS Edge Phase 2
 * Rev 1.10	10/08/2018		Jyoti Yadav			Quoting PAN CR
 * Rev 1.9  July 28,2018 	Nitika Arora        Changes for ADSR(Till Reconcile) Functionality  
 * Rev 1.8	18/04/2018		Kritica Goel  	    Layaway calculation change
 * Rev 1.7  20/04/2018      Ashish Yadav        Furniture and non furniture requirement
 * Rev 1.6	26/01/2018		Anoop Seth  	    GC Redemption Cross OU changes
 * Rev 1.5	24/12/2017		Shilpa Rawal  	    GC_eGV_CN Redemption Cross OU changes
 * Rev 1.4	03/01/2017		Kritica Agarwal  	GST Changes
 * Rev 1.3 01/08/2016 		Akhilesh Kumar		Mcoupon capillary integration
 * Rev 1.2 29/06/2013 		Karandeep Singh		Change for Dept Check
 * Rev 1.1 05/June/2013		Karandeep Singh     TIC Preview Sale requirement.
 * Rev 1.0 LS_ORPOS_VAT_Extra_v1.0 26/05/2013 Geetika.chugh
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.utility;

import java.io.Serializable;
import java.math.BigDecimal;

import oracle.retail.stores.domain.utility.EYSDate;

public interface MAXConfigParametersIfc extends Serializable {

	/**
	 * store and retreive parameter values from db
	 */

	public boolean getCalculateVatExtraFlag();

	public void setCalculateVatExtra(boolean calculateVatExtra);

	public String getEossMonths();

	public void setEossMonths(String eossMonths);

	public int getVatLimit();

	public void setVatLimit(int vatLimit);

	public int getDiscountLimit();

	public void setDiscountLimit(int discountLimit);

	// change for Rev 1.1 : Start
	public EYSDate getPreviewSaleStartDate();

	public void setPreviewSaleStartDate(EYSDate previewSaleStartDate);

	public EYSDate getPreviewSaleEndDate();

	public void setPreviewSaleEndDate(EYSDate previewSaleEndDate);
	// change for Rev 1.1 : End

	/** Change for Rev 1.2 : Start */
	public String getVatExtraDepts();

	public void setVatExtraDepts(String vatExtraDepts);

	/** Change for Rev 1.2 : End */

	/* changes for Rev 1.3 start */
	public Boolean isIssueCouponEnable();

	public void setIssueCouponEnable(boolean issueCouponEnable);

	/* changes for Rev 1.3 end */
	
	/* changes for Rev 1.4 start */
	public Boolean isGSTEnable();

	public void setGSTEnable(boolean gstEnable);
	public void setThresholdTax(String thresholdTax);
	public String getThresholdTax();
	/* changes for Rev 1.4 end */

	/* changes for Rev 1.5 Start */
	
	public String getOraganizationUnit();

	public void setOraganizationUnit(String organizationUnit);
	
	/* changes for Rev 1.5 end */

	
	/* changes for Rev 1.6 Start */
	
	public String getBinRange();

	public void setBinRange(String binRange);
	
	/* changes for Rev 1.6 end */



	//Change for Rev 1.8: Starts
	public boolean isLayawayCalculationChange();
	public void setLayawayCalculationChange(boolean layawayCalculationChange);
	//Change for Rev 1.8: Ends
	//Change for Rev 1.9 : Starts
	public boolean isDsrEnableFlag();
	public void setDsrEnableFlag(boolean dsrEnableFlag);
	public String getDsrDays();
	public void setDsrDays(String dsrDays);
	//Change for Rev 1.9 : Ends
	/*Change for Rev 1.10: Start*/
	public String getPANThreshold();
	public void setPANThreshold(String PANThreshold);
	/*Change for Rev 1.10: End*/
	/*Change for Rev 2.0: Start*/
	public EYSDate getEdgePreviewSaleStartDate();
	public void setEdgePreviewSaleStartDate(EYSDate edgePreviewSaleStartDate);
	public EYSDate getEdgePreviewSaleEndDate();
	public void setEdgePreviewSaleEndDate(EYSDate edgePreviewSaleEndDate);
	public String getEdgeItemValues();
	public void setEdgeItemValues(String edgeItemValues);
	/*Change for Rev 2.0: End*/
	
	
	public String getMAXQOCNIssueamount();
	public void setMAXQOCNIssueamount(String MAXQOCNIssueamount);
	// cess changes start here
	public boolean isCessEnable();
	public void setCessEnable(boolean cessEnable);
	// cess changes end here
	//Rev 2.1 start 
	/*public String getGcExpExtnDays();
	public void setGcExpExtnDays(String gcExpExtnDays);*/
	//Rev 2.1 end

	//Rev 2.2 start 
	public String getMaximumNumOfSuspends();
	public void setMaximumNumOfSuspends(String maxNumOfSuspends);
	//Rev 2.2 end

	//Rev 2.3 start 
	public String getReturnDays();	
	public void setReturnDays(String returnDays) ;
	//Rev 2.3 end
	
	//Changes starts for Rev 1.1 (Ashish : Easy Exchnage)
	public String getEnableEasyExchange();
	public void setEnableEasyExchange(String enableEasyExchange) ;
	//Changes ends for Rev 1.1 (Ashish : Easy Exchnage)
	
	//Changes starts for Rev 1.1 (Ashish : Youth Card)
	public String getEnableYouthCard();
	public void setEnableYouthCard(String enableYouthCard) ;
	public String getPromotionalYouthCard();

	public void setPromotionalYouthCard(String promotionalYouthCard);
	public int getYouthCardDiscPerc();

	public void setYouthCardDiscPerc(int youthCardDiscPerc);

	public int getYouthCardInitialAmount();
	public void setYouthCardInitialAmount(int youthCardInitialAmount);
	public String getYouthCardExpiry();
	public void setYouthCardExpiry(String youthCardExpiry);
	public String getYouthCardItem();
	public void setYouthCardItem(String youthCardItem);
public String getYouthThresholdAmount();
	public void setYouthThresholdAmount(String youthThresholdAmount);
	//Changes ends for Rev 1.1 (Ashish : Youth Card)
	//Changes starts for Rev 1.1 (Ashish : EReceipt)
	public String geteReceiptConf();
	public void seteReceiptConf(String eReceiptConf);
	public String geteReceiptTrantype();
	public void seteReceiptTrantype(String eReceiptTrantype);
	//Changes ends for Rev 1.1 (Ashish : EReceipt)
	//Change for Rev 2.4 : Starts
	public String getStoreId();
	public void setStoreId(String storeId);
	//Change for Rev 2.4 : Ends
	public String getGstInvoice();
	public void setGstInvoice(String gstInvoice);
	public String getEdgeName();
	public void setEdgeName(String edgeName);
	
	public boolean isSbiPointConversion();
	public void setSbiPointConversion(boolean sbiPointConversion);
	public int getSbiMinPoint();
	public void setSbiMinPoint(int sbiMinPoint);
	public int getSbiPointConversionRate();
	public void setSbiPointConversionRate(int sbiPointConversionRate);
	
	//changes for paytmqr
	public int getPaytmQRStatusCheckRetryCount();
	public void setPaytmQRStatusCheckRetryCount(int paytmQRStatusCheckRetryCount);
	
	//changes by Shyvanshu Mehra......
	public BigDecimal getCashLimitParameter();
	/* public void setCashLimitParameter(int CashLimitParameter); */

	public void setCashLimitParameter(BigDecimal i);
	
	//Changes by kamlesh Pant for Special Employee Discount
	public boolean isSpclEmpDisc();
	public void setSpclEmpDisc(boolean spclEmpDisc);
	
	//Rev 2.5 start
	 public int getScOtpRetries();
	 public void setScOtpRetries(int scOtpRetries);
	//Rev 2.5 end
	//Added by Vaibhav for emp disc otp
	 public boolean isEmpOtpEnableCheck();
	 public void setEmpOtpEnableCheck(boolean EmpOtpEnableCheck);

	// end
}
