package oracle.retail.stores.domain.tender;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.GiftCertificateDocumentIfc;

public interface TenderGiftCertificateIfc extends TenderCertificateIfc, TenderAlternateCurrencyIfc, EnterableTenderIfc {
	
  public static final String MALL_GC = "MALL_GC";
  
  public static final String MALL_GC_AS_CHECK = "MALL_GC_AS_CHECK";
  
  public static final String MALL_GC_AS_PO = "MALL_GC_AS_PO";
  
  Object clone();
  
  String getGiftCertificateNumber();
  
  void setGiftCertificateNumber(String paramString);
  
  void setTypeCode(int paramInt);
  
  void setCertificateType(String paramString);
  
  String getCertificateType();
  
  boolean isMallCertificateAsPurchaseOrder();
  
  boolean isMallCertificateAsCheck();
  
  boolean isIssued();
  
  CurrencyIfc getDiscountAmount();
  
  void setDiscountAmount(CurrencyIfc paramCurrencyIfc);
  
  GiftCertificateDocumentIfc getDocument();
  
  void setDocument(GiftCertificateDocumentIfc paramGiftCertificateDocumentIfc);
  
  void setStoreNumber(String paramString);
  
  String getStoreNumber();
}
