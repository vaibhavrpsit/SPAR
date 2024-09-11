/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2017 - 2018 MAX Hypermarket, Inc.    All Rights Reserved.
*	
*	Rev	1.1 	Purushotham Reddy 		Jul 01, 2019 	Changes for POS-Amazon Pay Integration 
*
*	Rev 1.0  	Nitika      			22/03/2017      Code Merge for EComPrepaid and EComCOD tender functionality.
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.ado.tender;

import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc;
import oracle.retail.stores.pos.ado.tender.TenderTypeHelper;

public class MAXTenderTypeHelper extends TenderTypeHelper
{
  private static final long serialVersionUID = -5021459383901128106L;

  public MAXTenderTypeHelper()
  {
    setupTenderTypeMappings();
  }

  protected void setupTenderTypeMappings()
  {
    this.tdrTypeMap.put("Cash", AuthorizationConstantsIfc.TenderType.CASH);
    this.tdrTypeMap.put("Check", AuthorizationConstantsIfc.TenderType.CHECK);
    this.tdrTypeMap.put("Coupon", AuthorizationConstantsIfc.TenderType.COUPON);
    this.tdrTypeMap.put("Credit", AuthorizationConstantsIfc.TenderType.CREDIT);
    this.tdrTypeMap.put("Debit", AuthorizationConstantsIfc.TenderType.DEBIT);
    this.tdrTypeMap.put("GiftCard", AuthorizationConstantsIfc.TenderType.GIFT_CARD);
    this.tdrTypeMap.put("GiftCert", AuthorizationConstantsIfc.TenderType.GIFT_CERT);
    this.tdrTypeMap.put("MailCheck", AuthorizationConstantsIfc.TenderType.MAIL_CHECK);
    this.tdrTypeMap.put("PurchaseOrder", AuthorizationConstantsIfc.TenderType.PURCHASE_ORDER);
    this.tdrTypeMap.put("StoreCredit", AuthorizationConstantsIfc.TenderType.STORE_CREDIT);
    this.tdrTypeMap.put("TravCheck", AuthorizationConstantsIfc.TenderType.TRAVELERS_CHECK);
    this.tdrTypeMap.put("MallCert", AuthorizationConstantsIfc.TenderType.MALL_CERT);
    this.tdrTypeMap.put("MoneyOrder", AuthorizationConstantsIfc.TenderType.MONEY_ORDER);
    this.tdrTypeMap.put("HouseAccount", AuthorizationConstantsIfc.TenderType.HOUSE_ACCOUNT);
    this.tdrTypeMap.put("Alternate", AuthorizationConstantsIfc.TenderType.ALTERNATE);
    this.tdrTypeMap.put("Paytm", AuthorizationConstantsIfc.TenderType.PAYTM);
    this.tdrTypeMap.put("Mobikwik", AuthorizationConstantsIfc.TenderType.MOBIKWIK);
    this.tdrTypeMap.put("LoyaltyPoints", AuthorizationConstantsIfc.TenderType.LOYALTY_POINTS);
    this.tdrTypeMap.put("EComPrepaid", AuthorizationConstantsIfc.TenderType.ECOM_PREPAID);
    this.tdrTypeMap.put("EComCOD", AuthorizationConstantsIfc.TenderType.ECOM_COD);  
    this.tdrTypeMap.put("AmazonPay", AuthorizationConstantsIfc.TenderType.AMAZON_PAY); 
    this.tdrTypeMap.put("EWallet", AuthorizationConstantsIfc.TenderType.EWALLET); 
    
    //System.out.println(tdrTypeMap);
  }
} 
