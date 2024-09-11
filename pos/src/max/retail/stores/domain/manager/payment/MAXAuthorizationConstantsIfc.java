package max.retail.stores.domain.manager.payment;

import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc;

public abstract interface MAXAuthorizationConstantsIfc extends AuthorizationConstantsIfc
{
  /*public static enum TenderType
  {
	  LOYALTY_POINTS;
    
    private TenderType() {}
  }*/
	
	public static enum TenderType
	  {
	    ALTERNATE, 
	    CASH, 
	    CHECK, 
	    COUPON, 
	    CREDIT, 
	    DEBIT, 
	    GIFT_CARD, 
	    GIFT_CERT, 
	    HOUSE_ACCOUNT, 
	    INSTANT_CREDIT, 
	    ITEM_ACTIVATION, 
	    MAIL_CHECK, 
	    MALL_CERT, 
	    MONEY_ORDER, 
	    PURCHASE_ORDER, 
	    STORE_CREDIT, 
	    TRAVELERS_CHECK,
	    LOYALTY_POINTS,
	    PAYTM,
	    MOBIKWIK;
	  } 
	
	
}