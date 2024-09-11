/*****************************************************************************************************
 *   
 *	Copyright (c) 2019 MAX SPAR Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.0 	Jul 01, 2019		Purushotham Reddy 		Changes for POS-Amazon Pay Integration 
 *
 *****************************************************************************************************/

package max.retail.stores.domain.tender;

/**
@author Purushotham Reddy Sirison
**/

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;

public interface MAXTenderAmazonPayIfc extends TenderLineItemIfc {

	public void setFaceValue(CurrencyIfc faceValue);

	public CurrencyIfc getFaceValue();

	public void setAmazonPayMobileNumber(String moblieno);

	public String getAmazonPayMobileNumber();

	public String getAmazonPayAmount();

	public void setAmazonPayAmount(String paytmamout);

	public void setTypeCode(int type);

	public void setAmazonPayWalletTransactionID(String walletTransID);

	public String getAmazonPayWalletTransactionID();
	
	public void setAmazonPayOrderID(String orderID);

	public String getAmazonPayOrderID();

	
}
