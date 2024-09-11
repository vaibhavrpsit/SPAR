package oracle.retail.stores.commerceservices.transaction;

import java.util.Map;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.manager.externalorder.ExternalOrderException;
import oracle.retail.stores.domain.manager.externalorder.ExternalOrderManagerIfc;
import oracle.retail.stores.domain.manager.ifc.PaymentManagerIfc;
import oracle.retail.stores.domain.manager.order.OrderManagerIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeRequestIfc;
import oracle.retail.stores.domain.manager.payment.ReversalRequestIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.manager.archive.RetailTransactionArchiveSupportingConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.order.common.OrderUtilities;
import org.apache.log4j.Logger;

public class RetailTransactionCancelService implements RetailTransactionCancelServiceIfc, RetailTransactionArchiveSupportingConstantsIfc {
  protected static Logger logger = Logger.getLogger(RetailTransactionCancelService.class);
  
  public RetailTransactionCancelPayload prePersistCancelProcessing(BusIfc bus, RetailTransactionCancelPayload payload) throws RetailTransactionCancelServiceException {
    if (logger.isInfoEnabled())
      logger.info("perPersist processing to cancel transaction " + payload.getRetailTransaction().getTransactionID()); 
    payload.setCancelled(false);
    payload.setPersistTransaction(false);
    payload.setPostPersistProcessing(false);
    RegisterIfc register = payload.getRegister();
    TransactionIfc transaction = payload.getRetailTransaction();
    TransactionTotalsIfc totals = null;
    boolean transCompleted = (transaction != null && transaction.getTransactionStatus() == 2);
    if (transaction != null && !transCompleted && !transaction.isCanceledTransactionSaved()) {
      if (payload.getSupporting() != null && payload.getSupporting().get("SUPPORTING_CANCEL_TENDER_REVERSALS_KEY") != null)
        reversePendingAuthorizations((Map<String, ReversalRequestIfc>)payload.getSupporting().get("SUPPORTING_CANCEL_TENDER_REVERSALS_KEY")); 
      transaction.setTransactionStatus(3);
      if (transaction.getTimestampEnd() == null)
        transaction.setTimestampEnd(); 
      register.addNumberCancelledTransactions(1);
      if (transaction instanceof TenderableTransactionIfc) {
        totals = ((TenderableTransactionIfc)transaction).getTransactionTotals();
        if (totals != null)
          register.addAmountCancelledTransactions(totals.getSubtotal().subtract(totals.getDiscountTotal()).abs()); 
      } 
      TillIfc till = (TillIfc)register.getCurrentTill().clone();
      RegisterIfc reg = (RegisterIfc)register.clone();
      FinancialTotalsIfc accTotals = DomainGateway.getFactory().getFinancialTotalsInstance();
      accTotals.setNumberCancelledTransactions(1);
      if (totals != null)
        accTotals.setAmountCancelledTransactions(totals.getSubtotal().subtract(totals.getDiscountTotal()).abs()); 
      till.setTotals(accTotals);
      reg.setTotals(accTotals);
      payload.setRetailTransaction(transaction);
      payload.setRegister(reg);
      payload.setTill(till);
      payload.setFinancialTotals(accTotals);
      payload.setCancelled(true);
      payload.setPersistTransaction(true);
      payload.setPostPersistProcessing(true);
    } 
    if (logger.isDebugEnabled()) {
      StringBuffer buffer = new StringBuffer();
      buffer.append("prePersist cancellation processing completed for transaction ");
      buffer.append(payload.getRetailTransaction().getTransactionID());
      buffer.append(", isCancelled: ");
      buffer.append(payload.isCancelled());
      buffer.append(", isPersistTransaction: ");
      buffer.append(payload.isPersistTransaction());
      buffer.append(", isPostProcess: ");
      buffer.append(payload.isPostPersistProcessing());
      logger.debug(buffer.toString());
    } 
    return payload;
  }
  
  protected void reversePendingAuthorizations(Map<String, ReversalRequestIfc> reversalsMap) {
    PaymentManagerIfc paymentManager = (PaymentManagerIfc)Gateway.getDispatcher().getManager("PaymentManager");
    for (String key : reversalsMap.keySet()) {
      ReversalRequestIfc reversalRequest = reversalsMap.get(key);
      try {
        if (logger.isDebugEnabled())
          logger.debug("Reversing payment for " + key); 
        paymentManager.reversal((AuthorizeRequestIfc)reversalRequest);
      } catch (Exception e) {
        logger.warn("Excpetion " + e.getMessage() + " caught while attempting to reverse payment for " + key);
      } 
    } 
  }
  
  public RetailTransactionCancelPayload postPersistCancelProcessing(BusIfc bus, RetailTransactionCancelPayload payload) throws RetailTransactionCancelServiceException {
    if (logger.isInfoEnabled())
      logger.info("postPersist processing to cancel transaction " + payload.getRetailTransaction().getTransactionID()); 
    cancelExternalOrders(bus, payload);
    updateHardTotals(bus, payload);
    journalCanceledTransaction(bus, payload);
    return payload;
  }
  
  protected void cancelExternalOrders(BusIfc bus, RetailTransactionCancelPayload payload) {
    TransactionIfc transaction = payload.getRetailTransaction();
    try {
      if (transaction.getTransactionType() == 23) {
        OrderManagerIfc orderMgr = (OrderManagerIfc)bus.getManager("OrderManager");
        OrderUtilities.cancelNewOrderIDs((OrderTransactionIfc)transaction, orderMgr);
      } 
      if (transaction instanceof SaleReturnTransactionIfc && ((SaleReturnTransactionIfc)transaction).hasExternalOrder() && !payload.getRegister().getWorkstation().isTrainingMode()) {
        ExternalOrderManagerIfc externalOrderManager = (ExternalOrderManagerIfc)bus.getManager("ExternalOrderManager");
        String orderId = ((SaleReturnTransactionIfc)transaction).getExternalOrderID();
        externalOrderManager.cancel(orderId, transaction.getWorkstation().getWorkstationID());
      } 
    } catch (ExternalOrderException eoe) {
      logger.error("External Order was not unlocked for transaction " + transaction.getTransactionID() + ", need to manually unlock the order", (Throwable)eoe);
    } 
  }
  
  protected void updateHardTotals(BusIfc bus, RetailTransactionCancelPayload payload) {
    if (payload.isCancelled())
      try {
        TransactionUtilityManagerIfc txnUtilMgr = (TransactionUtilityManagerIfc)bus.getManager("TransactionUtilityManager");
        txnUtilMgr.writeHardTotals();
      } catch (Exception e) {
        logger.error("Unable to save hard totals.");
        logger.error("" + e + "");
      }  
  }
  
  protected void journalCanceledTransaction(BusIfc bus, RetailTransactionCancelPayload payload) {
    if (payload.isCancelled()) {
      TransactionUtilityManagerIfc txnUtilMgr = (TransactionUtilityManagerIfc)bus.getManager("TransactionUtilityManager");
      JournalManagerIfc journalMgr = (JournalManagerIfc)bus.getManager("JournalManager");
      JournalFormatterManagerIfc formatter = (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager("JournalFormatterManager");
      if (journalMgr != null && formatter != null) {
        TransactionIfc transaction = payload.getRetailTransaction();
        String cancelJournal = null;
        if (transaction instanceof TenderableTransactionIfc) {
          cancelJournal = formatter.journalCanceledTransaction((TenderableTransactionIfc)transaction);
        } else {
          cancelJournal = "Non tenderable transaction";
        } 
        String transactionID = payload.getRetailTransaction().getTransactionID();
        journalMgr.setSequenceNumber(transactionID);
        journalMgr.setEntryType(3);
        String opID = null;
        if (payload.getOperator() == null) {
          opID = "SYSTEM";
        } else {
          opID = payload.getOperator().getLoginID();
        } 
        journalMgr.journal(opID, transactionID, cancelJournal);
        if (logger.isInfoEnabled())
          logger.info("Transaction " + transactionID + " Canceled"); 
        txnUtilMgr.completeTransactionJournaling(transaction);
      } else {
        logger.error("No JournalManager found");
      } 
    } 
  }
}
