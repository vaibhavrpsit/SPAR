/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 2010 Lifestyle India Pvt Ltd.    All Rights Reserved.
 * Rev 1.5	7th Nov 2019	Vidhya Kommareddi
 * MPOS REQ:  MPOS Transaction Indentifier
 * Rev 1.4  Jul 10,2019	    Nitika Arora  E-Wallet Integration
 * Rev 1.3 	Aug 10, 2018	Jyoti Yadav	  Quoting PAN CR
 * Rev 1.2	  Aug 25,2017  Kritica Agarwal Change to capture GSTIN number
 * Rev 1.1 GST Changes
 * Rev 1.0  April 14, 2011 05:00:30 PM Tarun.Gupta
 * Initial revision.
 * Resolution for LMGI-562
 * Receipt Soft Copy document is not created for the Layaway Partial Payment 
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.transaction;

//import max.retail.stores.domain.businessassociate.MAXBusinessAssociateIfc;
import max.retail.stores.domain.customer.MAXCustomerType;
//import max.retail.stores.domain.ewallet.MAXEWalletAPIWebProperties;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransactionIfc;

import java.util.HashMap;
import java.util.Map;

import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;

public interface MAXLayawayPaymentTransactionIfc extends
		LayawayPaymentTransactionIfc {

	/**
	 * revision number
	 **/
	public static final String revisionNumber = "$Revision: 1.3 $";

	// ---------------------------------------------------------------------
	/**
	 * @param strBuffer
	 */
	public void setStringBuffer(StringBuffer strBuffer);

	// ---------------------------------------------------------------------
	/**
	 * @return
	 */
	// ---------------------------------------------------------------------
	public StringBuffer getStringBuffer();

	// ---------------------------------------------------------------------
	/**
	 * @return
	 */
	// ---------------------------------------------------------------------
	public int getCount();

	// ---------------------------------------------------------------------
	/**
	 * @param count
	 */
	public void setCount(int count);
	
	//Added By Chiranjibi Routray For Business Associate Starts Here
	//public MAXBusinessAssociateIfc getBusinessAssociate();
//	public void setBusinessAssociate(MAXBusinessAssociateIfc businessAssociate);
	//Added By Chiranjibi Routray For Business Associate Ends Here
	 
	//Rev 1.1 Starts
		public AbstractTransactionLineItemIfc[] getLineItem() ;
		public void setLineItem(AbstractTransactionLineItemIfc[] pluItem);
		public boolean isGstEnable();
		public void setGstEnable(boolean gstEnable);
		public Map<String,String> getTaxCode();
	//	public void setTaxCode(Map<String,String> taxCode);		
		//Rev 1.1 Starts
		//Change for rev 1.2: Starts
		public String getExternalOrderID();
		public void setExternalOrderID(String externalOrder);
		//Change for rev 1.2: Ends
		
				//Changes for getting the Concept based on the register id starts
		
		public String getConcept();
			
		public void setConcept(String concept);
		//Changes for getting the Concept based on the register id ends
public void setCustomerFeedback(String feedback);
		public String getCustomerFeedback();
	
		/*Change for Rev 1.3: Start*/
		public MAXCustomerType[] getCustomerTypeDetails();
		public void setCustomerTypeDetails(MAXCustomerType[] customerTypeDetails);
		/*Change for Rev 1.3: End*/
		public HashMap<String, String> getGcCustomer();
		//public void setGcCustomer(HashMap<String, String> gcCustomer);

		//Change for Rev 1.4 Start
	//	public MAXEWalletAPIWebProperties getApiWebProperties();

		//public void setApiWebProperties(MAXEWalletAPIWebProperties apiWebProperties);
		//Change for Rev 1.4 Ends
		
		//Rev 1.5 start
		public boolean getIsMposInitiated() ;	
		public void setIsMposInitiated(boolean isMposInitiated) ;
		//Rev 1.5 end
		
		//Changes starts for rev 1.1 (Ashish ; EReceipt)
		public String geteReceiptConf();
		public void seteReceiptConf(String eReceiptConf);
		public String geteReceiptTrantype();
		public void seteReceiptTrantype(String eReceiptTrantype);
		public boolean isEReceiptValueSelected();
		public void setEReceiptValueSelected(boolean isEReceiptValueSelected);
		
		//Changes ends for rev 1.1 (Ashish ; EReceipt)
		
		//changes for paytmqr
		public String getPaytmQROrderId();
		public void setPaytmQROrderId(String paytmQROrderId);
}
