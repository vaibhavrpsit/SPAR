/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAX, Inc.    All Rights Reserved.
 * Rev 1.7	May 05, 2020	Karni Singh		POS REQ: Register CRM customer with OTP.
  Rev 1.6     May 04, 2017	    Kritica Agarwal     GST Changes
  Rev 1.5		Ashish Yadav	13/09/16	Changes done for code merging
  Rev. 1.4     Rahul Yadav	   16/04/2014 		Pos Offline Alert
  Rev. 1.3     Rahul Yadav	   16/04/2014 		BusinessDate Mismatch Alert
  Rev. 1.2     Izhar	        29/05/2013  		Discount Rule
  Rev. 1.1 		Prateel		23/05/2013              Changes for single bar code functionality
  Rev. 1.0 		Tanmaya		05/04/2013		Initial Draft: Change for Scan and void
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.sale;

import java.math.BigDecimal; 
import java.util.List;
import java.util.Vector;

import max.retail.stores.domain.customer.MAXTICCustomerIfc;
import max.retail.stores.domain.singlebarcode.SingleBarCodeData;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

public interface MAXSaleCargoIfc extends SaleCargoIfc {
	
	public boolean isScanNVoidFlow();

	public void setScanNVoidFlow(boolean scanNVoidFlow);

		/**MAX Rev. 1.3 Change: Start**/
	public boolean isBusinessDateMismatchOverrideSuccess();

	public void setBusinessDateMismatchOverrideSuccess(boolean scanNVoidFlow);
	/**MAX Rev. 1.3 Change: Start**/
	/**MAX Rev. 1.4 Change: Start**/
	public boolean isPosOfflineAlertOverrideSuccess();

	public void setPosOfflineAlertOverrideSuccess(boolean scanNVoidFlow);
	/**MAX Rev. 1.4 Change: Start**/
	/**MAX Rev 1.1 Change: Start**/
	public void setSingleBarCodeData(SingleBarCodeData singleBarCodeData);
	public SingleBarCodeData getSingleBarCodeData();
	public Vector getSingleBarCodeVector();
	public void setSingleBarCodeVector(Vector singleBarCodeVector);
	public int getSingleBarCodeLineItem();
	public void setSingleBarCodeLineItem(int singleBarCodeLineItem);
        /**MAX Rev 1.1 Change: End**/
		
		/**MAX Rev 1.2 Change: Start**/
public boolean isApplyBestDeal();

	public void setApplyBestDeal(boolean applyBestDeal);
		public void setInvoiceRuleAppliedRate(BigDecimal invoiceRuleAppliedRate);

	public BigDecimal getInvoiceRuleAppliedRate();

	public void setInvoiceRuleAlreadyApplied(boolean invoiceRuleAlreadyApplied);

	public boolean isInvoiceRuleAlreadyApplied();
public String getInitialOriginStationLetter();

	public void setInitialOriginStationLetter(String initialOriginStationLetter);
	/**MAX Rev 1.2 Change: end**/
	
	
// changes starts for rev 1.5
	public MAXTICCustomerIfc getTicCustomer();

	public void setTicCustomer(MAXTICCustomerIfc ticcustomer);

	public PhoneIfc getTicCustomerPhoneNo();

	public void setTicCustomerPhoneNo(PhoneIfc ticCustomerPhoneNo);

	public boolean isTicCustomerPhoneNoFlag();

	public void setTicCustomerPhoneNoFlag(boolean ticCustomerPhoneNoFlag);
// changes ends for rev 1.5
	
// changes starts for code merging(adding below methods as they are not present in base 14)
	public void setRounding(String rounding);
	public void setRoundingDenominations(List roundingDenominations);
	public boolean isCustomerEnabled();
	public String getRounding();
	public List getRoundingDenominations();
	public boolean isGiftCardApproved();
	public void setGiftCardApproved(boolean isGiftCardApproved);
	public RetailTransactionIfc getRetailTransactionIfc();
	
// Changes ends for cod merging
	public String getNecBarCode();

	public void setNecBarCode(String necBarCode);
	
	public boolean isNFCScan();

	  public void setNFCScan(boolean isNFCScan);
	//change for Rev 1.6 : Starts
		public Boolean getInterStateDelivery();
		public void setInterStateDelivery(Boolean interStateDelivery) ;
		public String getFromRegion();
		public void setFromRegion(String fromRegion);
		public String getToRegion();
		public void setToRegion(String toRegion);
		//Change for Rev 1.6 : Ends
		public String  getCategoryIDScanSheet();

		public void setCategoryIDScanSheet(String categoryid) ;

		public String  getCategoryDescripionScanSheet();

		public void setCategoryDescripionScanSheet(String categoryDesc) ;

		//Rev 1.7 start
	public boolean isCRMEnrolmentOTPValidated();
	public void setCRMEnrolmentOTPValidated(boolean isCRMEnrolmentOTPValidated);
	public String getCrmEnrolmentOTP();
	public void setCrmEnrolmentOTP(String crmEnrolmentOTP);
	public int getOtpRetries();
	public void setOtpRetries(int otpRetries);
	//Rev 1.7 end
	public boolean getEmpID();
	public void setEmpID(boolean empID);
	
	public String getLiqBarCode();
	public void setLiqBarCode(String LiqBarCode);
	
		}
