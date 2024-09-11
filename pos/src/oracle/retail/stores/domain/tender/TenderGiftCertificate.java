package oracle.retail.stores.domain.tender;

import java.math.BigDecimal;
import java.util.Locale;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.StringUtils;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.AbstractTenderDocumentIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.GiftCertificateDocumentIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.utility.I18NHelper;

public class TenderGiftCertificate extends AbstractTenderLineItem implements TenderGiftCertificateIfc {
  static final long serialVersionUID = 7596355145305094379L;
  
  protected String giftCertificateNumber;
  
  protected boolean tendered;
  
  protected EntryMethod entryMethod = null;
  
  protected EYSDate redeemDate = null;
  
  protected EYSDate issueDate = null;
  
  protected String storeNumber = "";
  
  protected String redeemTransactionID;
  
  protected boolean validateByStoreNumber = false;
  
  protected String certificateType = "";
  
  protected CurrencyIfc alternateCurrencyTendered = null;
  
  protected CurrencyIfc faceValueAmount = null;
  
  protected CurrencyIfc discountAmount = null;
  
  protected String state = "";
  
  protected boolean trainingMode = false;
  
  protected String transactionSeqNumber;
  
  protected String workstationID;
  
  private boolean isPostVoided = false;
  
  protected GiftCertificateDocumentIfc document = null;
  
  public TenderGiftCertificate() {
    this(DomainGateway.getBaseCurrencyInstance(), (String)null);
  }
  
  public TenderGiftCertificate(CurrencyIfc tender, String cert) {
    this.typeCode = 4;
    this.amountTender = tender;
    this.document = DomainGateway.getFactory().getGiftCertificateDocumentInstance();
    this.document.setDocumentID(cert);
  }
  
  public Object clone() {
    TenderGiftCertificate t = new TenderGiftCertificate();
    setCloneAttributes(t);
    return t;
  }
  
  protected void setCloneAttributes(TenderGiftCertificate newClass) {
    setCloneAttributes(newClass);
    if (this.entryMethod != null)
      newClass.setEntryMethod(this.entryMethod); 
    if (this.certificateType != null)
      newClass.setCertificateType(this.certificateType); 
    if (this.alternateCurrencyTendered != null)
      newClass.setAlternateCurrencyTendered((CurrencyIfc)this.alternateCurrencyTendered.clone()); 
    if (this.discountAmount != null)
      newClass.setDiscountAmount((CurrencyIfc)this.discountAmount.clone()); 
    if (this.document != null)
      newClass.setDocument((GiftCertificateDocumentIfc)this.document.clone()); 
    newClass.setTypeCode(this.typeCode);
    newClass.setValidateByStoreNumber(this.validateByStoreNumber);
  }
  
  public int checkLimitsForSale() throws IllegalStateException {
    int errorCode = 0;
    if (getTenderLimits() == null)
      throw new IllegalStateException("TenderLimits were not initialized."); 
    CurrencyIfc maxLimitValue = null;
    if (getTypeCode() == 4) {
      maxLimitValue = getTenderLimits().getCurrencyLimit("MaximumGiftCertificateTenderAmount");
    } else {
      maxLimitValue = getTenderLimits().getCurrencyLimit("MaximumMallCertificateTenderAmount");
    } 
    if (!maxLimitValue.equals(TenderLimits.getTenderNoLimitAmount()) && this.amountTender.compareTo(maxLimitValue) == 1)
      errorCode = 1; 
    return errorCode;
  }
  
  public CurrencyIfc getAmountMaximumChange() throws IllegalStateException {
    CurrencyIfc maxChange;
    if (getTenderLimits() == null)
      throw new IllegalStateException("TenderLimits were not initialized."); 
    if (this.typeCode == 4) {
      maxChange = getTenderLimits().getCurrencyLimit("MaximumAmountCashChangeForGiftCertificate");
    } else {
      Integer percentLimit = getTenderLimits().getPercentageLimit("MaximumPercentCashChangeForMallGiftCertificate");
      maxChange = this.amountTender.multiply(new BigDecimal(percentLimit.doubleValue() * 0.01D));
    } 
    return maxChange;
  }
  
  public String getGiftCertificateNumber() {
    return this.document.getDocumentID();
  }
  
  public void setGiftCertificateNumber(String id) {
    this.document.setDocumentID(id);
  }
  
  public void setEntryMethod(EntryMethod value) {
    this.entryMethod = value;
  }
  
  public EntryMethod getEntryMethod() {
    return this.entryMethod;
  }
  
  public byte[] getNumber() {
    if (StringUtils.isNotEmpty(getGiftCertificateNumber()))
      return getGiftCertificateNumber().getBytes(); 
    return new byte[0];
  }
  
  public boolean getTendered() {
    return "REDEEM".equals(this.document.getStatus());
  }
  
  public void setTendered(boolean value) {}
  
  public void setIssueDateAsString(String value) {
    if (!Util.isEmpty(value))
      this.document.setIssueDate(new EYSDate(value, 3, LocaleMap.getLocale("locale_Default"))); 
  }
  
  public String getIssueDateAsString() {
    String dateStr = "";
    if (this.document.getIssueDate() != null)
      dateStr = this.document.getIssueDate().toFormattedString(3, LocaleMap.getLocale("locale_Default")); 
    return dateStr;
  }
  
  public EYSDate getIssueDate() {
    return this.document.getIssueDate();
  }
  
  public void setIssueDate(EYSDate value) {
    this.document.setIssueDate(value);
  }
  
  public EYSDate getRedeemDate() {
    return this.document.getRedeemDate();
  }
  
  public void setRedeemDate(EYSDate value) {
    this.document.setRedeemDate(value);
  }
  
  public void setRedeemDateAsString(String value) {
    if (!Util.isEmpty(value))
      this.document.setRedeemDate(new EYSDate(value, 3, LocaleMap.getLocale("locale_Default"))); 
  }
  
  public String getRedeemDateAsString() {
    String dateStr = "";
    if (this.document.getRedeemDate() != null)
      dateStr = this.document.getRedeemDate().toFormattedString(3, LocaleMap.getLocale("locale_Default")); 
    return dateStr;
  }
  
  public String getRedeemTransactionID() {
    return this.redeemTransactionID;
  }
  
  public void setRedeemTransactionID(String value) {
    this.redeemTransactionID = value;
  }
  
  public void setStoreNumber(String value) {
    this.document.setIssuingStoreID(value);
  }
  
  public String getStoreNumber() {
    return this.document.getIssuingStoreID();
  }
  
  public void setValidateByStoreNumber(boolean value) {
    this.validateByStoreNumber = value;
  }
  
  public boolean getValidateByStoreNumber() {
    return this.validateByStoreNumber;
  }
  
  public void setTypeCode(int type) {
    this.typeCode = type;
  }
  
  public void setCertificateType(String value) {
    this.certificateType = value;
  }
  
  public String getCertificateType() {
    return this.certificateType;
  }
  
  public GiftCertificateDocumentIfc getDocument() {
    return this.document;
  }
  
  public void setDocument(GiftCertificateDocumentIfc document) {
    if (document != null)
      this.document = document; 
  }
  
  public boolean isMallCertificateAsPurchaseOrder() {
    return "MALL_GC_AS_PO".equals(getCertificateType());
  }
  
  public boolean isMallCertificateAsCheck() {
    return "MALL_GC_AS_CHECK".equals(getCertificateType());
  }
  
  public CurrencyIfc getReversedAmount() {
    return getAmountTender();
  }
  
  public CurrencyIfc getAlternateCurrencyTendered() {
    return this.alternateCurrencyTendered;
  }
  
  public void setAlternateCurrencyTendered(CurrencyIfc value) {
    this.alternateCurrencyTendered = value;
  }
  
  public CurrencyIfc getFaceValueAmount() {
    return this.document.getAmount();
  }
  
  public void setFaceValueAmount(CurrencyIfc value) {
    this.document.setAmount(value);
  }
  
  public CurrencyIfc getDiscountAmount() {
    return this.discountAmount;
  }
  
  public void setDiscountAmount(CurrencyIfc value) {
    this.discountAmount = value;
  }
  
  public String getState() {
    return this.document.getStatus();
  }
  
  public void setState(String val) {
    this.document.setStatus(val);
  }
  
  public boolean isTrainingMode() {
    return this.document.isTrainingMode();
  }
  
  public void setTrainingMode(boolean value) {
    this.document.setTrainingMode(value);
  }
  
  public String toJournalString(Locale journalLocale) {
    String type = "Store";
    if (this.typeCode == 10)
      type = "Mall"; 
    String strResult = new String();
    strResult = strResult + abstractTenderLineItemAttributesToJournalString(journalLocale);
    Object[] dataArgs = { getGiftCertificateNumber() };
    strResult = strResult + Util.EOL + I18NHelper.getString("EJournal", "JournalEntry.TenderCouponNumber", dataArgs, journalLocale);
    dataArgs[0] = this.entryMethod;
    strResult = strResult + Util.EOL + I18NHelper.getString("EJournal", "JournalEntry.EntryMethodLabel", dataArgs, journalLocale);
    dataArgs[0] = type;
    strResult = strResult + Util.EOL + I18NHelper.getString("EJournal", "JournalEntry.GiftCertificateTypeLabel", dataArgs, journalLocale) + Util.EOL;
    return strResult;
  }
  
  public String toString() {
    StringBuilder strResult = new StringBuilder("Class:  TenderGiftCertificate ( @" + hashCode());
    strResult.append(abstractTenderLineItemAttributesToString());
    if (this.entryMethod == null) {
      strResult.append("entryMethod:                          [null]").append(Util.EOL);
    } else {
      strResult.append(this.entryMethod.toString()).append(Util.EOL);
    } 
    if (this.certificateType == null) {
      strResult.append("certificateType:                     [null]").append(Util.EOL);
    } else {
      strResult.append("certificateType:                     [").append(this.certificateType).append(Util.EOL);
    } 
    if (this.alternateCurrencyTendered == null) {
      strResult.append("alternateCurrencyTendered:           [null]").append(Util.EOL);
    } else {
      strResult.append("alternateCurrencyTendered:           [").append(this.alternateCurrencyTendered.toString()).append(Util.EOL);
    } 
    if (this.discountAmount == null) {
      strResult.append("discountAmount:                      [null]").append(Util.EOL);
    } else {
      strResult.append("discountAmount:                      [").append(this.discountAmount.toString()).append(Util.EOL);
    } 
    if (this.document == null) {
      strResult.append("document:                            [null]").append(Util.EOL);
    } else {
      strResult.append("document:                            [").append(this.document.toString()).append(Util.EOL);
    } 
    strResult.append("typeCode:                            [").append(this.typeCode).append(Util.EOL);
    strResult.append("validateByStoreNumber                [").append(this.validateByStoreNumber).append(Util.EOL);
    return strResult.toString();
  }
  
  public boolean equals(Object obj) {
    boolean isEqual = false;
    if (obj instanceof TenderGiftCertificate) {
      TenderGiftCertificate c = (TenderGiftCertificate)obj;
      if (super.equals(obj) && Util.isObjectEqual(this.certificateType, c.certificateType) && this.validateByStoreNumber == c.validateByStoreNumber && Util.isObjectEqual(this.entryMethod, c.entryMethod) && Util.isObjectEqual(this.alternateCurrencyTendered, c.alternateCurrencyTendered) && Util.isObjectEqual(this.document, c.document) && Util.isObjectEqual(this.discountAmount, c.discountAmount))
        isEqual = true; 
    } 
    return isEqual;
  }
  
  public String getTransactionSeqNumber() {
    return Long.toString(this.document.getIssuingTransactionSeqNumber());
  }
  
  public void setTransactionSeqNumber(String transactionSeqNumber) {
    this.document.setIssuingTransactionSeqNumber(Long.getLong(transactionSeqNumber).longValue());
  }
  
  public String getWorkstationID() {
    return this.document.getIssuingWorkstationID();
  }
  
  public void setWorkstationID(String workstationID) {
    this.document.setIssuingWorkstationID(workstationID);
  }
  
  public void setPostVoided(boolean isPostVoided) {}
  
  public boolean getPostVoided() {
    return "VOIDED".equals(this.document.getStatus());
  }
  
  public boolean isIssued() {
    return "ISSUED".equals(this.document.getStatus());
  }
  
  public boolean isRedeemed() {
    return "REDEEM".equals(this.document.getStatus());
  }
  
  public AbstractTenderDocumentIfc getBaseDocument() {
    return (AbstractTenderDocumentIfc)this.document;
  }
}
