package oracle.retail.stores.domain.manager.payment;



public abstract interface AuthorizationConstantsIfc
{
  public static final short ONLINE = 1;
  

  public static final short PAYMENT_APPLICATION_OFFLINE = 2;
  

  public static final short SWITCH_OFFLINE = 3;
  

  public static final short BANK_OFFLINE = 4;
  

  public static final int DEFAULT_TIMEOUT = 30;
  

  public static final int UNUSED = 0;
  

  public static final int PROCESS = 1;
  

  public static final int DISCARD = 2;
  
  public static final int TRANS_SALE = 1;
  
  public static final int TRANS_VOID = 2;
  
  public static final int TRANS_CREDIT = 3;
  
  public static final int TRANS_CREDIT_VOID = 4;
  
  public static final int TRANS_AUTH_ONLY = 5;
  
  public static final int TRANS_FORCE = 6;
  
  public static final int TRANS_SETTLEMENT = 7;
  
  public static final int TRANS_GUARANTEE_DL = 11;
  
  public static final int TRANS_GUARANTEE_MICR = 12;
  
  public static final int TRANS_GUARANTEE_DL_AND_MICR = 13;
  
  public static final int TRANS_SALE_OFFLINE_REFERRAL = 14;
  
  public static final int TRANS_SALE_ONLINE_REFERRAL = 15;
  
  public static final int MAX_TRANS_CONSTANT = 15;
  
  public static final String PERSONAL_ID_TYPE_DRIVERS_LICENSE = "DriversLicense";
  
  public static final String PERSONAL_ID_TYPE_STATE_REGION = "StateRegionID";
  
  public static final String PERSONAL_ID_TYPE_PASSPORT = "Passport";
  
  public static final String PERSONAL_ID_TYPE_MILITARY = "MilitaryID";
  
  public static final String PERSONAL_ID_TYPE_RESIDENT_ALIEN = "ResAlienID";
  
  public static final String PERSONAL_ID_TYPE_STUDENT = "Student";
  
  public static final String CONVERSION_FLAG_GUARANTEE_WITH_CONVERSION = "G";
  
  public static final String CONVERSION_FLAG_VERIFICATION_WITH_CONVERSION = "V";
  
  public static final String CONVERSION_FLAG_CONVERSION_ONLY = "C";
  

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
    PAYTM,
    MOBIKWIK,
    LOYALTY_POINTS,
    ECOM_PREPAID,
    ECOM_COD,
    AMAZON_PAY,
    EWALLET;
    
    private TenderType() {}
  }
}
