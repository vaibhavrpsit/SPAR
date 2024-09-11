/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAX, Inc.    All Rights Reserved. 
  Rev 1.1     May 04, 2017	    Kritica Agarwal     GST Changes
  Rev. 1.0     Animesh	        11/08/2013  		Item advanced search button on sell

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.inquiry.iteminquiry;

import max.retail.stores.domain.singlebarcode.SingleBarCodeData;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;

public class MAXItemInquiryCargo extends ItemInquiryCargo {

	protected SingleBarCodeData singleBarCodeData = null;
	/**MAX Rev 1.0 Change: Start*/
    protected boolean addSearchPLUItem=false;
    /**MAX Rev 1.0 Change: End*/
    public boolean isItemSearched=false;
    
    public String necBarCode = null;
    
    
   
	protected String initialOriginLetter = null;// Sakshi for best deal button
	
	//Changes Starts by Kamlesh Pant for SpecialEmpDiscount
	
	public boolean empID=false;
	

	public boolean getEmpID() {
		return empID;
	}

	public void setEmpID(boolean empID) {
		this.empID = empID;
	}
	//Changes Ends for SpecialEmpDiscount
	
	public String getInitialOriginLetter() {
		return initialOriginLetter;
	}

	public void setInitialOriginLetter(String initialOriginLetter) {
		this.initialOriginLetter = initialOriginLetter;
	}

	public SingleBarCodeData getSingleBarCodeData() {
		return singleBarCodeData;
	}

	public void setSingleBarCodeData(SingleBarCodeData singleBarCodeData) {
		this.singleBarCodeData = singleBarCodeData;
	}
	/**MAX Rev 1.0 Change: Start*/
	public boolean isAddSearchPLUItem() {
		return addSearchPLUItem;
	}

	public void setAddSearchPLUItem(boolean addSearchPLUItem) {
		this.addSearchPLUItem = addSearchPLUItem;
	}
	/**MAX Rev 1.0 Change: End*/
	protected boolean isApplyBestDeal = false;

	public boolean isApplyBestDeal() {
		return isApplyBestDeal;
	}

	public void setApplyBestDeal(boolean isApplyBestDeal) {
		this.isApplyBestDeal = isApplyBestDeal;
	}
	public String getNecBarCode() {
		return necBarCode;
	}

	public void setNecBarCode(String necBarCode) {
		this.necBarCode = necBarCode;
	}
	
		//Change for Rev 1.1 : Starts
		protected Boolean interStateDelivery = false;
		protected String fromRegion;
		protected String toRegion;

		/**
		 * @return the fromRegion
		 */
		public String getFromRegion() {
			return fromRegion;
		}

		/**
		 * @param fromRegion the fromRegion to set
		 */
		
		public void setFromRegion(String fromRegion) {
			this.fromRegion = fromRegion;
		}

		/**
		 * @return the toRegion
		 */
		
		public String getToRegion() {
			return toRegion;
		}

		/**
		 * @param toRegion the toRegion to set
		 */
		
		public void setToRegion(String toRegion) {
			this.toRegion = toRegion;
		}

		/**
		 * @return the interStateDelivery
		 */
		
		public Boolean getInterStateDelivery() {
			return interStateDelivery;
		}

		/**
		 * @param interStateDelivery the interStateDelivery to set
		 */
		
		public void setInterStateDelivery(Boolean interStateDelry) {
			interStateDelivery = interStateDelry;
		}
	//Change for Rev 1.1 : Ends
		
		public String LiqBarCode;

		public String getLiqBarCode() {
			return LiqBarCode;
		}

		public void setLiqBarCode(String liqBarCode) {
			LiqBarCode = liqBarCode;
		}
		
		
}
