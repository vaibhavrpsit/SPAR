/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/LayawayTransaction.java /main/21 2013/07/10 14:47:04 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  07/10/13 - Added Cash Rounding Adjustment to receipt payment
 *                         total 
 *    jswan     07/09/13 - Fixed issues saving the cash adjustment total to the
 *                         history tables for order, layaway, redeem and voided
 *                         transactions.
 *    ohorne    05/26/11 - implemented isHouseAccountPayment
 *    mchellap  08/10/10 - BUG#9975770 Fixed negative payment total amount
 *    jkoppolu  07/20/10 - Fixes for Bug#9698280, issues with reprinted layaway
 *                         create and delete transaction receipts
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   02/02/10 - dd overide method updateTenderTotals
 *    abondala  01/03/10 - update header date
 *    cgreene   03/30/09 - ade method isRefundDue to layaway transaction to aid
 *                         in printing refund amount on receipt
 *    cgreene   03/30/09 - fix wrong change due by subtracting collected
 *                         tenders, just like OrderTransaction
 *    cgreene   11/18/08 - removed call to deprecated salereturntrans method
 *                         that did nothing
 *
 * ===========================================================================
 * $Log:
 *    9    360Commerce 1.8         5/16/2007 7:56:04 PM   Brett J. Larsen
 *         CR 26903 - 8.0.1 merge to trunk
 *
 *         BackOffice <ARG> Summary Report overhaul (many CRs fixed)
 *
 *    8    360Commerce 1.7         5/14/2007 6:08:34 PM   Sandy Gu
 *         update inclusive information in financial totals and history tables
 *    7    360Commerce 1.6         4/25/2007 10:00:20 AM  Anda D. Cadar   I18N
 *         merge
 *    6    360Commerce 1.5         5/12/2006 5:26:36 PM   Charles D. Baker
 *         Merging with v1_0_0_53 of Returns Managament
 *    5    360Commerce 1.4         4/27/2006 7:29:49 PM   Brett J. Larsen CR
 *         17307 - remove inventory functionality - stage 2
 *    4    360Commerce 1.3         1/22/2006 11:41:57 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:50 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:03 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:17 PM  Robert Pearse
 *
 *   Revision 1.17  2004/10/01 20:22:38  mweis
 *   @scr 7269 Inventory counts for Layaway not updating StockLedgerAccount table.
 *
 *   Revision 1.16  2004/09/30 18:08:46  cdb
 *   @scr 7248 Cleaned up inventory location and state in LayawayTransaction object.
 *
 *   Revision 1.15  2004/09/23 00:30:51  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.14  2004/09/21 15:02:21  mweis
 *   @scr 7012 Use generic Inventory constants.
 *
 *   Revision 1.13  2004/09/17 15:49:59  jdeleau
 *   @scr 7146 Define a taxable transaction, for reporting purposes.
 *
 *   Revision 1.12  2004/09/16 20:15:50  mweis
 *   @scr 7012 Correctly update the inventory counts when a layaway is picked up (completed).
 *
 *   Revision 1.11  2004/08/23 16:15:46  cdb
 *   @scr 4204 Removed tab characters
 *
 *   Revision 1.10  2004/07/08 01:05:52  crain
 *   @scr 5898 Deleting a Layaway crashes POS
 *
 *   Revision 1.9  2004/06/29 21:59:00  aachinfiev
 *   Merge the changes for inventory & POS integration
 *
 *   Revision 1.8  2004/06/15 16:05:33  jdeleau
 *   @scr 2775 Add database entry for uniqueID so returns w/
 *   receipt will work, make some fixes to FinancialTotals storage of tax.
 *
 *   Revision 1.7  2004/06/15 00:44:31  jdeleau
 *   @scr 2775 Support register reports and financial totals with the new
 *   tax engine.
 *
 *   Revision 1.6  2004/05/11 23:03:02  jdeleau
 *   @scr 4218 Backout recent changes to remove TransactionDiscounts,
 *   going to go a different route and remove the newly added
 *   voids and grosses instead.
 *
 *   Revision 1.4  2004/02/13 04:11:29  tfritz
 *   @scr 3718 Modify fee now adds fee to current layaway fee.
 *
 *   Revision 1.3  2004/02/12 17:14:42  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:28:50  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:34  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Oct 02 2003 10:45:44   baa
 * fix for rss defect 1406
 * Resolution for 3402: RSS  PRF 1406 Tender Information on Void transactions does not looks correct
 *
 *    Rev 1.0   Aug 29 2003 15:40:50   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   24 Jun 2003 20:06:02   mpm
 * Added code to set financials, bypass inventory updates, when in-process-void transactions are saved.
 *
 *    Rev 1.0   Jun 03 2002 17:05:50   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:11:26   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:30:14   msg
 * Initial revision.
 *
 *    Rev 1.10   Feb 27 2002 17:59:48   mpm
 * Modified inventory update to increment sellable when a layaway is deleted.
 * Resolution for Domain SCR-38: Deleting a Layaway creation does not update the Inventory count
 *
 *    Rev 1.9   Feb 19 2002 14:30:00   dfh
 * a little cleanup in financial totals
 * Resolution for POS SCR-1285: Layaway pickup does not update Sales Taxable count/amount on Summary Report
 *
 *    Rev 1.8   Feb 16 2002 16:51:30   dfh
 * added comment
 * Resolution for POS SCR-1284: Layaway pickup does not update Sales Tax count/amount on Summary Report
 *
 *    Rev 1.7   Feb 16 2002 16:47:22   dfh
 * financial totals updates for reports
 * Resolution for POS SCR-1284: Layaway pickup does not update Sales Tax count/amount on Summary Report
 * Resolution for POS SCR-1296: Completed Layaway Pickup does not update Net Trans Taxable count/amount on Summary Report
 * Resolution for POS SCR-1297: Completed Layaway Pickup does not update Sales Taxable count/amount on Summary Report
 * Resolution for POS SCR-1298: Completed Layaway Pickup does not update Total Item Sales count/amount on Summary Report
 * Resolution for POS SCR-1299: Completed Layaway Pickup does not update Net Item Sales count/amount on Summary Report
 * Resolution for POS SCR-1300: Completed Tax Exempt Layaway Pickup does not update Net Trans Nontaxable count/amunt on Summary Report
 *
 *    Rev 1.6   Feb 14 2002 21:03:14   dfh
 * adding sale tax amount, need count yet.
 * Resolution for POS SCR-1284: Layaway pickup does not update Sales Tax count/amount on Summary Report
 *
 *    Rev 1.4   Feb 05 2002 16:36:28   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 *
 *    Rev 1.3   27 Nov 2001 16:04:18   jbp
 * reset end business date when pickup is made from initial transaction.
 * Resolution for POS SCR-327: Layaway pickup cannot be voided when initial and pickup are different business days
 *
 *    Rev 1.2   07 Nov 2001 17:04:42   pdd
 * Added addTender().
 * Resolution for POS SCR-219: Add Tender Limit Override
 *
 *    Rev 1.1   29 Oct 2001 08:33:16   mpm
 * Added inventory-movement methods.
 *
 *    Rev 1.0   Sep 20 2001 16:05:32   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:40:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.transaction;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.financial.PaymentIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;

/**
 * This class describes a layaway transaction. A layaway transaction reflects
 * the movement of stock and the handling of one payment for a layaway.
 *
 * @see oracle.retail.stores.domain.financial.LayawayIfc
 * @see oracle.retail.stores.domain.transaction.SaleReturnTransaction
 * @see oracle.retail.stores.domain.transaction.LayawayTransactionIfc
 * @version $Revision: /main/21 $
 */
public class LayawayTransaction extends SaleReturnTransaction implements LayawayTransactionIfc,
    LayawayPaymentTransactionIfc
{
  // This id is used to tell the compiler not to generate a new
  // serialVersionUID.
  static final long serialVersionUID = 8473595146874875491L;

  /**
   * revision number supplied by source-code-control system
   */
  public static String revisionNumber = "$Revision: /main/21 $";

  /**
   * layaway associated with this transaction
   */
  protected LayawayIfc layaway = null;

  /**
   * payment associated with this transaction
   */
  protected PaymentIfc payment = null;

  /**
   * tender display totals
   */
  protected TransactionTotalsIfc tenderTotals = null;

  /**
   * Constructs LayawayTransaction object
   */
  public LayawayTransaction()
  {
    initialize();
  }

  /**
   * Initializes object with super class attributes.
   */
  public void initialize(SaleReturnTransaction transaction)
  {
    transaction.setCloneAttributes(this);
  }

  /**
   * Initializes object with seedTransaction and LayawayTransaction class
   * attributes.
   */
  public void initialize(TransactionIfc transaction, LayawayTransactionIfc layawayTransaction)
  {
    layawayTransaction.setTransactionAttributes(this);
    transaction.setTransactionAttributes(this);
    this.setTimestampEnd(null);
    for (int i = 0; i < this.getTenderLineItemsSize();)
    {
      this.removeTenderLineItem(i);
    }
  }

  /**
   * Empty method for implementing layawayPaymentTransactionIfc.
   */
  public void initialize(TransactionIfc transaction)
  {
  }

  /**
   * Initializes object with local attributes.
   */
  @Override
  public void initialize()
  {
    super.initialize();
    tenderTotals = DomainGateway.getFactory().getTransactionTotalsInstance();
  }

  /**
   * Creates clone of this object.
   *
   * @return Object clone of this object
   */
  @Override
  public Object clone()
  {
    // instantiate new object
    LayawayTransaction c = new LayawayTransaction();

    // set values
    setCloneAttributes(c);

    // pass back Object
    return c;
  }

  /**
   * Sets attributes in clone of this object.
   *
   * @param newClass new instance of object
   */
  protected void setCloneAttributes(LayawayTransaction newClass)
  {
    super.setCloneAttributes(newClass);
    if (layaway != null)
    {
      newClass.setLayaway((LayawayIfc) getLayaway().clone());
    }
    if (payment != null)
    {
      newClass.setPayment((PaymentIfc) getPayment().clone());
    }
    if (tenderTotals != null)
    {
      newClass.setTransactionTotals((TransactionTotalsIfc) getTransactionTotals().clone());
    }
  }

  /**
   * Copys transactions attributes to new transaction <B>Pre-Condition(s)</B>
   *
   * @param TransactionIfc transaction
   */
  public void setTransactionAttributes(LayawayTransactionIfc transaction)
  {
    if (transaction instanceof LayawayTransaction)
    {
      setCloneAttributes((LayawayTransaction) transaction);
    }
  }

  /*
   * (non-Javadoc)
   * @seeoracle.retail.stores.domain.transaction.AbstractTenderableTransaction#
   * calculateChangeDue()
   */
  @Override
  public CurrencyIfc calculateChangeDue()
  {
    // For transactions with payments the change due is calculated by
    // subtracting the amount tender from the payment amount.
    CurrencyIfc changeDue = payment.getPaymentAmount().subtract(getCollectedTenderTotalAmount());
    return (changeDue);
  }

  /*
   * (non-Javadoc)
   * @see
   * oracle.retail.stores.domain.transaction.LayawayTransactionIfc#isRefundDue()
   */
  public boolean isRefundDue()
  {
    return (getTransactionType() == TransactionIfc.TYPE_LAYAWAY_DELETE)
        && (getTenderTransactionTotals().getAmountTender().getDoubleValue() < 0);
  }

  /* (non-Javadoc)
   * @see oracle.retail.stores.domain.transaction.PaymentTransactionIfc#isHouseAccountPayment()
   */
  @Override
  public boolean isHouseAccountPayment()
  {
      // Layway of House Account Payment is not supported.
      return false;
  }
  
  /*
   * (non-Javadoc)
   * @see
   * oracle.retail.stores.domain.transaction.SaleReturnTransaction#equals(java
   * .lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    boolean isEqual = true;
    // confirm object instanceof this object
    if (obj instanceof LayawayTransaction)
    {
      // downcast the input object
      LayawayTransaction c = (LayawayTransaction) obj;
      // compare all the attributes of Layaway
      if (Util.isObjectEqual(getLayaway(), c.getLayaway()) && Util.isObjectEqual(getPayment(), c.getPayment())
          && Util.isObjectEqual(getTenderTransactionTotals(), c.getTenderTransactionTotals()))
      {
        isEqual = true; // set the return code to true
      }
      else
      {
        isEqual = false; // set the return code to false
      }
    }
    else
    {
      isEqual = false;
    }
    return (isEqual);
  }

  /**
   * Retrieves layaway associated with this transaction.
   *
   * @return layaway associated with this transaction
   */
  public LayawayIfc getLayaway()
  {
    return (layaway);
  }

  /**
   * Sets the layaway associated with this transaction.
   *
   * @param value layaway associated with this transaction
   */
  public void setLayaway(LayawayIfc value)
  {
    layaway = value;
  }

  /**
   * Retrieves payment associated with this transaction.
   *
   * @return payment associated with this transaction
   */
  public PaymentIfc getPayment()
  {
    return (payment);
  }

  /**
   * Sets the payment associated with this transaction.
   *
   * @param value payment associated with this transaction
   */
  public void setPayment(PaymentIfc value)
  {
    payment = value;
    updateTenderTotals(payment);
  }

  /**
   * Retrieves tender display transaction totals. In this case, these are the
   * same as the standard transaction totals.
   *
   * @return tender display transaction totals
   */
  public TransactionTotalsIfc getTenderTransactionTotals()
  {
    return (tenderTotals);
  }

  /**
   * Adds tender line item.
   *
   * @param item oracle.retail.stores.domain.tender.TenderLineItemIfc The tender
   *          line item to be added
   * @exception IllegalArgumentException if tender line item cannot be added
   */
  public void addTenderLineItem(TenderLineItemIfc item) throws IllegalArgumentException
  {
    super.addTenderLineItem(item);
    getTenderTransactionTotals().updateTenderTotals(getTenderLineItems());
  }

  /**
   * Adds tender line item.
   *
   * @param item oracle.retail.stores.domain.tender.TenderLineItemIfc The tender
   *          line item to be added
   * @exception IllegalArgumentException if tender line item cannot be added
   */
  public void addTender(TenderLineItemIfc item)
  {
    super.addTender(item);
    getTenderTransactionTotals().updateTenderTotals(getTenderLineItems());
  }

  /**
   * Set tender line items array and update totals. Overrides method in
   * abstractTenderableTransaction.
   *
   * @param tli array tender line items
   */
  public void setTenderLineItems(TenderLineItemIfc[] tli)
  {
    super.setTenderLineItems(tli);
    // update totals
    getTenderTransactionTotals().updateTenderTotals(getTenderLineItems());
  }

  /**
   * Remove a tender line from the transaction.
   */
  public void removeTenderLineItem(int index)
  {
    super.removeTenderLineItem(index);
    getTenderTransactionTotals().updateTenderTotals(getTenderLineItems());
  }

  /**
   * Remove a tender line from the transaction.
   *
   * @param tenderToRemove Tender line item to remove from the list
   */
  public void removeTenderLineItem(TenderLineItemIfc tenderToRemove)
  {
    super.removeTenderLineItem(tenderToRemove);
    getTenderTransactionTotals().updateTenderTotals(getTenderLineItems());
  }

  /**
   * Calculates FinancialTotals based on current transaction.
   *
   * @return FinancialTotalsIfc object
   */
  public FinancialTotalsIfc getFinancialTotals()
  {
    FinancialTotalsIfc financialTotals = DomainGateway.getFactory().getFinancialTotalsInstance();

    if (getTransactionStatus() == TransactionIfc.STATUS_CANCELED)
    {
      financialTotals.setAmountCancelledTransactions(totals.getSubtotal().subtract(totals.getDiscountTotal()).abs());
    }
    else
    {
      switch (transactionType) {
      case TransactionIfc.TYPE_LAYAWAY_COMPLETE:
        financialTotals.addAmountLayawayPickup(getPaymentAmount());
        // get line item financial totals
        financialTotals.add(getLineItemsFinancialTotals());
        financialTotals.add(getLayawayFinancialTotals());
        break;
      case TransactionIfc.TYPE_LAYAWAY_DELETE:
        // add the payment (refund or 0)
        financialTotals.addAmountLayawayDeletions(getPaymentAmount());
        financialTotals.addCountLayawayDeletions(1);
        // add the fee
        financialTotals.addAmountLayawayDeletionFees(getLayaway().getDeletionFee());
        financialTotals.addCountLayawayDeletionFees(1);
        break;
      case TransactionIfc.TYPE_LAYAWAY_INITIATE:
        // do not record totals on suspended transaction
        if (getTransactionStatus() != TransactionIfc.STATUS_SUSPENDED)
        {
          // add the payment to Layaway New filed in the financial totals
          financialTotals.addAmountLayawayNew(getPaymentAmount());
          // add the fee
          financialTotals.addAmountLayawayInitiationFees(getLayaway().getCreationFee());
          financialTotals.addCountLayawayInitiationFees(1);
        }
        break;
      default:
        break;
      }

      // Add the rounded (cash) change amount to the financial totals object.
      if (getTenderTransactionTotals().getCashChangeRoundingAdjustment().signum() == CurrencyIfc.NEGATIVE)
      {
          financialTotals.addAmountChangeRoundedOut(getTenderTransactionTotals().getCashChangeRoundingAdjustment().abs());
      }
      if (getTenderTransactionTotals().getCashChangeRoundingAdjustment().signum() == CurrencyIfc.POSITIVE)
      {
          financialTotals.addAmountChangeRoundedIn(getTenderTransactionTotals().getCashChangeRoundingAdjustment().abs());
      }

      // get totals from tender line items
      financialTotals.add(getTenderFinancialTotals(getTenderLineItems(), getTenderTransactionTotals()));
    }

    return (financialTotals);
  }

  /**
   * Retrieves current layaway status. If no layaway exists, status is returned
   * as undefined.
   *
   * @return current layaway status
   */
  public int getStatus()
  {
    int status = LayawayConstantsIfc.STATUS_UNDEFINED;
    if (getLayaway() != null)
    {
      status = getLayaway().getStatus();
    }
    return (status);
  }

  /**
   * Retrieves customer.
   *
   * @return customer
   */
  public CustomerIfc getCustomer()
  {
    return (super.getCustomer());
  }

  /**
   * Sets customer.
   *
   * @param value customer
   */
  public void setCustomer(CustomerIfc value)
  {
    super.setCustomer(value);
    if (layaway != null)
    {
      layaway.setCustomer(value);
    }
  }

  /**
   * Links customer.
   *
   * @param value customer
   */
  public void linkCustomer(CustomerIfc value)
  {
    super.linkCustomer(value);
    if (layaway != null)
    {
      layaway.setCustomer(value);
    }
  }

  /**
   * Update totals in the tenderTotals object
   */
  @Override
  public void updateTenderTotals()
  {
    super.updateTenderTotals();
    getTenderTransactionTotals().updateTenderTotals(getTenderLineItems());
  }

  /**
   * Updates TenderTotals when payment is set.
   *
   * @param value payment
   */
  protected void updateTenderTotals(PaymentIfc payment)
  {
    // Updates tenderTotals with payment amounts
    TransactionTotalsIfc tenderTotals = getTenderTransactionTotals();
    tenderTotals.setGrandTotal(payment.getPaymentAmount());
    tenderTotals.setSubtotal(payment.getPaymentAmount());
    tenderTotals.setBalanceDue(payment.getPaymentAmount());
  }

  /**
   * Sets payment amount
   *
   * @param paymentAmount a CurrencyIfc object representing payment amount.
   */
  public void setPaymentAmount(CurrencyIfc value)
  {
    getPayment().setPaymentAmount(value);
    totals.updateTransactionTotalsForPayment(value);
  }

  /**
   * Gets payment amount
   *
   * @return a CurrencyIfc object representing payment amount.
   */
  public CurrencyIfc getPaymentAmount()
  {
    return getPayment().getPaymentAmount();
  }

  /**
   * Returns the payment amount less any fees. This means the creation fee is
   * eliminated from a layaway-initiate transaction and both fees are eliminated
   * from a layaway-delete transaction.
   *
   * @return payment amount less fees
   */
  public CurrencyIfc getPaymentAmountLessFees()
  {
    CurrencyIfc paymentAmount = getPaymentAmount();
    if (paymentAmount == null)
    {
      paymentAmount = DomainGateway.getBaseCurrencyInstance();
    }
    CurrencyIfc creationFee = getLayaway().getCreationFee();
    CurrencyIfc deletionFee = getLayaway().getDeletionFee();
    CurrencyIfc paymentAmountLessFees = null;

    switch (transactionType) {
    case TransactionIfc.TYPE_LAYAWAY_DELETE:
      // put fees back in (to figure correct payment)
      paymentAmountLessFees = paymentAmount.negate().subtract(creationFee);
      paymentAmountLessFees = paymentAmountLessFees.subtract(deletionFee);
      break;
    case TransactionIfc.TYPE_LAYAWAY_INITIATE:
      // fees are meaningless on suspended transaction
      if (getTransactionStatus() != TransactionIfc.STATUS_SUSPENDED)
      {
        creationFee = getLayaway().getCreationFee();
        paymentAmountLessFees = getPaymentAmount().subtract(creationFee);
      }
      break;
    default:
    case TransactionIfc.TYPE_LAYAWAY_COMPLETE:
      // no fees on last payment
      paymentAmountLessFees = paymentAmount;
      break;
    }

    return (paymentAmountLessFees);
  }

  /**
   * Sets account number
   *
   * @param accountNum String representing account number.
   */
  public void setAccountNum(String accountNum)
  {
    getPayment().setReferenceNumber(accountNum);
  }

  /**
   * Gets account number
   *
   * @return String representing account number.
   */
  public String getAccountNum()
  {
    return getPayment().getReferenceNumber();
  }

  /**
   * Gets description
   *
   * @return String representing description.
   */
  public String getDescription()
  {
    return getPayment().getDescription();
  }

  /**
   * Modifies Fee
   *
   * @param CurrencyIfc fee
   */
  public void modifyFee(CurrencyIfc value)
  {
    // adds change in fee to grand total
    totals.setLayawayFee(totals.getLayawayFee().add(value));
    totals.calculateGrandTotal();
  }

  /*
   * (non-Javadoc)
   * @see
   * oracle.retail.stores.domain.transaction.SaleReturnTransaction#toString()
   */
  @Override
  public String toString()
  {
    // build result string
    StringBuilder strResult = Util.classToStringHeader("LayawayTransaction", getRevisionNumber(), hashCode());

    if (getLayaway() == null)
    {
      strResult.append(Util.formatToStringEntry("layaway", "null"));
    }
    else
    {
      strResult.append(Util.formatToStringEntry("layaway", getLayaway()));
    }
    if (getPayment() == null)

    {
      strResult.append(Util.formatToStringEntry("payment", "null"));
    }
    else
    {
      strResult.append(Util.formatToStringEntry("payment", getPayment()));
    }

    if (getTenderTransactionTotals() == null)
    {
      strResult.append(Util.formatToStringEntry("tender transaction totals", "null"));
    }
    else
    {
      strResult.append(Util.formatToStringEntry("tender transaction totals", getTenderTransactionTotals()));
    }

    // get parent information
    strResult.append(super.toString());

    // pass back result
    return (strResult.toString());
  }

  /**
   * Sets the transaction type to TYPE_LAYAWAY_INITIATE or
   * TYPE_LAYAWAY_COMPLETE.
   */
  protected void resetTransactionType()
  {
    // leave current type either TYPE_LAYAWAY_INITIATE or TYPE_LAYAWAY_COMPLETE
  }

  /**
   * Derives the additive financial totals for a layaway complete transaction,
   * not including line items and tenders .
   *
   * @return additive financial totals
   */
  protected FinancialTotalsIfc getLayawayFinancialTotals()
  {
    FinancialTotalsIfc financialTotals = DomainGateway.getFactory().getFinancialTotalsInstance();

    // gross total is transaction subtotal with discount applied minus payments
    // applied
    CurrencyIfc tax = totals.getTaxTotal();
    CurrencyIfc inclusiveTax = totals.getInclusiveTaxTotal();
    CurrencyIfc gross = getLayaway().getTotal().subtract(tax).subtract(getLayaway().getCreationFee());

    if (getTransactionTax().getTaxMode() == TaxIfc.TAX_MODE_EXEMPT)
    {
      financialTotals.addAmountGrossTaxExemptTransactionSales(gross);
      financialTotals.addCountGrossTaxExemptTransactionSales(1);
    }
    // if tax is positive value, add to taxable
    // Note: tax exempt stuff is counted as non-taxable for now
    // TEC need to see if tax is zero to handle returns
    if (isTaxableTransaction())
    {
      financialTotals.addAmountGrossTaxableTransactionSales(gross);
      financialTotals.addCountGrossTaxableTransactionSales(1);
      financialTotals.addAmountTaxTransactionSales(tax);
      financialTotals.addAmountInclusiveTaxTransactionSales(inclusiveTax);
      // Tax is now part of line item PLU and not needed here
      // TaxTotalsContainer taxTotals = new
      // TaxTotalsContainer(totals.getTaxInformationContainer());
      // financialTotals.addTaxes(taxTotals);
    }
    else
    {
      financialTotals.addAmountGrossNonTaxableTransactionSales(gross);
      financialTotals.addCountGrossNonTaxableTransactionSales(1);
    }
    // handle discount amounts
    financialTotals.setAmountTransactionDiscounts(totals.getTransactionDiscountTotal());
    if (getTransactionDiscounts() != null)
    {
      financialTotals.addNumberTransactionDiscounts(getTransactionDiscounts().length);
    }

    return (financialTotals);
  }

  /**
   * Retrieves the source-code-control system revision number.
   *
   * @return String representation of revision number
   */
  public String getRevisionNumber()
  {
    // return string
    return (revisionNumber);
  }

  /**
     */
  public void generateLayaway()
  {
    if (layaway == null)
    {
      layaway = DomainGateway.getFactory().getLayawayInstance();
    }
  }

  /**
   * Layawaymain method.
   *
   * @param String args[] command-line parameters
   */

  /*
   * Retrieves 'Payment Total' in printed receipt during layaway creation and
   * layaway delete transactions
   */
  public CurrencyIfc getTotalAmountPaid()
  {
    int status = layaway.getStatus();
    if (status == LayawayConstantsIfc.STATUS_DELETED)
    {
      return this.layaway.getCreationFee().add(this.layaway.getDeletionFee()).add(
          this.tenderTotals.getAmountTender().negate()).add(this.tenderTotals.getCashChangeRoundingAdjustment());
    }

    if (status == LayawayConstantsIfc.STATUS_NEW)
    {
      return this.tenderTotals.getAmountTender();
    }

    return this.layaway.getTotalAmountPaid();

  }

  public static void main(String args[])
  {
    // instantiate class
    LayawayTransaction c = new LayawayTransaction();
    // output toString()
    System.out.println(c.toString());
  }


}
