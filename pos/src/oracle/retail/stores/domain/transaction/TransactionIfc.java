package oracle.retail.stores.domain.transaction;

import java.util.Locale;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.tender.TenderLimitsIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSDomainIfc;

public abstract interface TransactionIfc extends EYSDomainIfc, TransactionConstantsIfc
{
  public static final String revisionNumber = "$Revision: /main/26 $";

  public abstract void initialize(TransactionIDIfc paramTransactionIDIfc);

  public abstract void initialize(String paramString);

  public abstract boolean equals(Object paramObject);

  public abstract String getTransactionID();

  public abstract void setTransactionID(String paramString);

  public abstract long getTransactionSequenceNumber();

  public abstract void setTransactionSequenceNumber(long paramLong);

  public abstract int getTransactionType();

  public abstract void setTransactionType(int paramInt);

  public abstract int getTransactionStatus();

  public abstract void setTransactionStatus(int paramInt);

  public abstract int getPreviousTransactionStatus();

  public abstract EmployeeIfc getCashier();

  public abstract void setCashier(EmployeeIfc paramEmployeeIfc);

  public abstract EmployeeIfc getSalesAssociate();

  public abstract void setSalesAssociate(EmployeeIfc paramEmployeeIfc);

  public abstract EYSDate getBusinessDay();

  public abstract void setBusinessDay(EYSDate paramEYSDate);

  public abstract EYSDate getTimestampBegin();

  public abstract void setTimestampBegin(EYSDate paramEYSDate);

  public abstract void setTimestampBegin();

  public abstract EYSDate getTimestampEnd();

  public abstract void setTimestampEnd(EYSDate paramEYSDate);

  public abstract void setTimestampEnd();

  public abstract void setWorkstation(WorkstationIfc paramWorkstationIfc);

  public abstract WorkstationIfc getWorkstation();

  public abstract String getTillID();

  public abstract void setTillID(String paramString);

  public abstract void setTenderLimits(TenderLimitsIfc paramTenderLimitsIfc);

  public abstract TenderLimitsIfc getTenderLimits();

  public abstract void setTrainingMode(boolean paramBoolean);

  public abstract boolean isTrainingMode();

  public abstract boolean isCompleted();

  public abstract boolean isCanceled();

  public abstract boolean isSuspended();

  public abstract boolean isVoided();

  public abstract LocalizedCodeIfc getSuspendReason();

  public abstract void setSuspendReason(LocalizedCodeIfc paramLocalizedCodeIfc);

  public abstract String statusToString();

  public abstract String toJournalString(Locale paramLocale);

  public abstract String journalFooter(Locale paramLocale);

  public abstract TransactionIDIfc getTransactionIdentifier();

  public abstract void setTransactionIdentifier(TransactionIDIfc paramTransactionIDIfc);

  public abstract String getFormattedStoreID();

  public abstract String getFormattedTransactionSequenceNumber();

  public abstract String getFormattedWorkstationID();

  public abstract void buildTransactionID();

  public abstract void setTransactionAttributes(TransactionIfc paramTransactionIfc);

  public abstract CustomerInfoIfc getCustomerInfo();

  public abstract void setCustomerInfo(CustomerInfoIfc paramCustomerInfoIfc);

  public abstract LocaleRequestor getLocaleRequestor();

  public abstract void setLocaleRequestor(LocaleRequestor paramLocaleRequestor);

  public abstract void setPostProcessingStatus(int paramInt);

  public abstract int getPostProcessingStatus();

  public abstract String postProcessingStatusToString();

  public abstract boolean isReentryMode();

  public abstract void setReentryMode(boolean paramBoolean);

  public abstract void setCaptureCustomer(CaptureCustomerIfc paramCaptureCustomerIfc);

  public abstract CaptureCustomerIfc getCaptureCustomer();

  public abstract void setIsItemBasketTransactionComplete(boolean paramBoolean);

  public abstract boolean getIsItemBasketTransactionComplete();

  public abstract boolean isCanceledTransactionPrinted();

  public abstract void setCanceledTransactionPrinted(boolean paramBoolean);

  public abstract boolean isCanceledTransactionSaved();

  public abstract void setCanceledTransactionSaved(boolean paramBoolean);

  public abstract String getTransactionTypeDescription();

  public abstract String getTransactionStatusDescription();

  public abstract boolean containsXChannelOrderLineItem();
  
  public boolean isDuplicateReceipt();
	
  public abstract void setDuplicateReceipt(boolean duplicateReceipt);
}