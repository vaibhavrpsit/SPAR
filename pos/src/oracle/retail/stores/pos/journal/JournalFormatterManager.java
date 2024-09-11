package oracle.retail.stores.pos.journal;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.KitComponentLineItemIfc;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.transaction.BillPayTransactionIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.RedeemTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.manager.Manager;
import org.apache.log4j.Logger;

public class JournalFormatterManager extends Manager
  implements JournalFormatterManagerIfc
{
  private static Logger logger = Logger.getLogger(JournalFormatterManager.class);

  protected boolean taxInclusiveEnabled = Gateway.getBooleanProperty("application", "InclusiveTaxEnabled", false);

  protected JournalFormatterFactoryIfc journalFormatterFactory = null;

  public String toJournalString(TransactionIfc transaction, ParameterManagerIfc parameterManager)
  {
    TransactionJournalFormatterIfc formatter = getFormatter(transaction, parameterManager);

    String journalString = null;
    if (formatter != null)
    {
      journalString = formatter.toJournalString();
    }
    else
    {
      journalString = transaction.toJournalString(LocaleMap.getLocale("locale_Journaling"));
    }
    return journalString;
  }

  public String journalTotals(SaleReturnTransactionIfc transaction, ParameterManagerIfc parameterManager)
  {
    TransactionJournalFormatterIfc formatter = getFormatter(transaction, parameterManager);

    String journalString = null;
    if (formatter != null)
    {
      journalString = formatter.journalTotals();
    }
    return journalString;
  }

  protected TransactionJournalFormatterIfc getFormatter(TransactionIfc transaction, ParameterManagerIfc parameterManager)
  {
    String key = null;
    if ((transaction instanceof SaleReturnTransactionIfc))
    {
      if (this.taxInclusiveEnabled)
      {
        key = "application_VATSaleReturnTransactionJournalFormatter";
      }
      else
      {
        key = "application_SaleReturnTransactionJournalFormatter";
      }
    }
    else if ((transaction instanceof VoidTransactionIfc))
    {
      if (this.taxInclusiveEnabled)
      {
        key = "application_VATVoidTransactionJournalFormatter";
      }
      else
      {
        key = "application_VoidTransactionJournalFormatter";
      }
    }
    else if ((transaction instanceof RedeemTransactionIfc))
    {
      key = "application_redeemTransactionJournalFormatter";
    }
    else if ((transaction instanceof BillPayTransactionIfc))
    {
      key = "application_BillPayTransactionJournalFormatter";
    }

    TransactionJournalFormatterIfc formatter = null;
    if (key != null)
    {
      try
      {
        formatter = (AbstractTransactionJournalFormatter)getJournalFormatterFactory().createJournalFormatter(key);
        formatter.setTransaction(transaction);
        formatter.setParameterManager(parameterManager);
      }
      catch (Exception e)
      {
        logger.warn("Could not load JournalFormatter for " + key, e);
      }
    }
    return formatter;
  }

  protected JournalFormatterFactoryIfc getJournalFormatterFactory()
  {
    if (this.journalFormatterFactory == null)
    {
      this.journalFormatterFactory = new JournalFormatterFactory();
    }
    return this.journalFormatterFactory;
  }

  public String journalSubTotalsForTransactionLevelSend(SaleReturnTransactionIfc transaction, ParameterManagerIfc parameterManager)
  {
    TransactionJournalFormatterIfc formatter = getFormatter(transaction, parameterManager);

    String journalString = null;
    if (formatter != null)
    {
      journalString = formatter.journalSubTotalsForTransactionLevelSend();
    }
    return journalString;
  }

  public String journalOrder(OrderTransactionIfc transaction, OrderIfc order, ParameterManagerIfc parameterManager)
  {
    TransactionJournalFormatterIfc formatter = getFormatter(transaction, parameterManager);

    String journalString = null;
    if (formatter != null)
    {
      journalString = formatter.journalOrder(order);
    }
    return journalString;
  }

  public String journalOrderTotals(OrderIfc order, int serviceType, ParameterManagerIfc parameterManager)
  {
    String key = null;
    if (this.taxInclusiveEnabled)
    {
      key = "application_VATSaleReturnTransactionJournalFormatter";
    }
    else
    {
      key = "application_SaleReturnTransactionJournalFormatter";
    }

    AbstractTransactionJournalFormatter formatter = null;
    if (key != null)
    {
      try
      {
        formatter = (AbstractTransactionJournalFormatter)getJournalFormatterFactory().createJournalFormatter(key);
        formatter.setParameterManager(parameterManager);
        if (formatter.getTransaction() == null)
        {
          formatter.setTransaction(order.getOriginalTransaction());
        }
      }
      catch (Exception e)
      {
        logger.warn("Could not load JournalFormatter for " + key, e);
      }
    }
    return formatter.journalOrderTotals(order, serviceType);
  }

  public void formatTotals(SaleReturnTransactionIfc transaction, StringBuffer sb, TransactionTotalsIfc totals, TransactionTaxIfc tax, ParameterManagerIfc parameterManager)
  {
    TransactionJournalFormatterIfc formatter = getFormatter(transaction, parameterManager);
    if (formatter != null)
    {
      formatter.formatTotals(sb, totals, tax);
    }
  }

  public String toJournalString(SaleReturnLineItemIfc lineItem, EYSDate dob, String relatedItemID)
  {
    String returnValue = "";
    LineItemJournalFormatterIfc formatter = getFormatter(lineItem);
    if (formatter != null)
    {
      if (relatedItemID != null)
      {
        if ((formatter instanceof SaleReturnLineItemJournalFormatter))
        {
          returnValue = formatter.toJournalString(dob, relatedItemID);
        }
        else if (dob != null)
        {
          returnValue = formatter.toJournalString(dob, relatedItemID);
        }
      }
      else if (dob != null)
      {
        returnValue = formatter.toJournalString(dob);
      }
      else
      {
        returnValue = formatter.toJournalString();
      }
    }
    else if ((dob != null) && (relatedItemID != null))
    {
      returnValue = lineItem.toJournalString(dob, relatedItemID, LocaleMap.getLocale("locale_Journaling"));
    }
    else if (dob != null)
    {
      returnValue = lineItem.toJournalString(dob, LocaleMap.getLocale("locale_Journaling"));
    }
    else
    {
      returnValue = lineItem.toJournalString(LocaleMap.getLocale("locale_Journaling"));
    }
    return returnValue;
  }

  protected LineItemJournalFormatterIfc getFormatter(SaleReturnLineItemIfc lineItem)
  {
    String key = null;
    if ((lineItem instanceof KitHeaderLineItemIfc))
    {
      if (this.taxInclusiveEnabled)
      {
        key = "application_VATKitHeaderLineItemJournalFormatter";
      }
      else
      {
        key = "application_KitHeaderLineItemJournalFormatter";
      }
    }
    else if (lineItem.isKitComponent())
    {
      if (this.taxInclusiveEnabled)
      {
        key = "application_VATKitComponentLineItemJournalFormatter";
      }
      else
      {
        key = "application_KitComponentLineItemJournalFormatter";
      }

    }
    else
    {
      key = "application_VATSaleReturnLineItemJournalFormatter";
    }

    LineItemJournalFormatterIfc formatter = null;
    if (key != null)
    {
      formatter = (LineItemJournalFormatterIfc)getJournalFormatterFactory().createJournalFormatter(key);
      formatter.setLineItem(lineItem);
    }
    return formatter;
  }

  public String toJournalRemoveString(SaleReturnLineItemIfc lineItem)
  {
    String returnValue = "";
    LineItemJournalFormatterIfc formatter = getFormatter(lineItem);
    if (formatter != null)
    {
      returnValue = formatter.toJournalRemoveString();
    }
    else
    {
      returnValue = lineItem.toJournalRemoveString(LocaleMap.getLocale("locale_Journaling"));
    }
    return returnValue;
  }

  public String toJournalManualDiscount(SaleReturnLineItemIfc lineItem, ItemDiscountStrategyIfc discount, boolean discountRemoved)
  {
    String returnValue = "";
    LineItemJournalFormatterIfc formatter = getFormatter(lineItem);
    if (formatter != null)
    {
      returnValue = formatter.toJournalManualDiscount(discount, discountRemoved);
    }
    else
    {
      returnValue = lineItem.toJournalManualDiscount(discount, discountRemoved, LocaleMap.getLocale("locale_Journaling"));
    }
    return returnValue;
  }

  public String journalKitComponent(KitComponentLineItemIfc lineItem)
  {
    String returnValue = "";
    String key = null;
    if (this.taxInclusiveEnabled)
    {
      key = "application_VATKitComponentLineItemJournalFormatter";
    }
    else
    {
      key = "application_KitComponentLineItemJournalFormatter";
    }
    KitComponentLineItemJournalFormatterIfc formatter = null;
    if (key != null)
    {
      formatter = (KitComponentLineItemJournalFormatterIfc)getJournalFormatterFactory().createJournalFormatter(key);

      formatter.setLineItem(lineItem);
    }
    if (formatter != null)
    {
      returnValue = formatter.toComponentJournalString();
    }
    else
    {
      returnValue = lineItem.toComponentJournalString(LocaleMap.getLocale("locale_Journaling"));
    }
    return returnValue;
  }

  public String journalCanceledSuspendedTransaction(TenderableTransactionIfc transaction)
  {
    String returnValue = "";
    TransactionJournalFormatterIfc formatter = getFormatter(transaction, null);
    if (formatter != null)
    {
      returnValue = formatter.journalCanceledSuspendedTransaction();
    }
    return returnValue;
  }

  public String journalCanceledTransaction(TenderableTransactionIfc transaction)
  {
    String returnValue = "";
    TransactionJournalFormatterIfc formatter = getFormatter(transaction, null);
    if (formatter != null)
    {
      returnValue = formatter.journalCanceledTransaction();
    }
    return returnValue;
  }

  public String journalLineItems(SaleReturnTransactionIfc transaction)
  {
    String returnValue = "";
    TransactionJournalFormatterIfc formatter = getFormatter(transaction, null);
    if (formatter != null)
    {
      returnValue = formatter.journalLineItems();
    }
    return returnValue;
  }

  public void formatTotals(VoidTransactionIfc voidTrans, StringBuffer sb, ParameterManagerIfc parameterManager)
  {
    TransactionJournalFormatterIfc formatter = getFormatter(voidTrans, parameterManager);
    TransactionIfc origTrans = voidTrans.getOriginalTransaction();
    if ((origTrans instanceof SaleReturnTransactionIfc))
    {
      SaleReturnTransactionIfc saleReturnTransaction = (SaleReturnTransactionIfc)origTrans;
      TransactionTotalsIfc totals = saleReturnTransaction.getTransactionTotals();
      TransactionTaxIfc tax = saleReturnTransaction.getTransactionTax();
      formatter.formatTotals(sb, totals, tax);
    }
  }

  public String journalShippingInfo(SaleReturnTransactionIfc transaction, SaleReturnLineItemIfc[] items, ParameterManagerIfc parameterManager)
  {
    String returnValue = "";
    TransactionJournalFormatterIfc formatter = getFormatter(transaction, parameterManager);
    if (formatter != null)
    {
      returnValue = formatter.journalShippingInfo(items);
    }
    return returnValue;
  }
}