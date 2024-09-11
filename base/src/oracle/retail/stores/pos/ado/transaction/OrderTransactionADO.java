/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/transaction/OrderTransactionADO.java /main/25 2013/04/16 13:32:42 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    vtemker   04/05/13 - Fixed defect with post void of modified transactions
 *                         (partial orders)
 *    vtemker   02/27/13 - Post void of Completed Orders is no longer permitted
 *                         (see bugdb id: 16406360)
 *    yiqzhao   08/01/12 - add voidCheckForCrossChannelOrder()
 *    sgu       05/04/12 - refactor OrderStatus to support store order and
 *                         xchannel order
 *    blarsen   11/08/11 - Prevent post-void of canceled orders.
 *    cgreene   10/26/11 - check for missing tokens when voiding transactions
 *    asinton   10/05/11 - prevent post voids of transactions with certain gift
 *                         card operations.
 *    cgreene   02/11/11 - Used LinkedHashMaps to ensure order and added
 *                         generics tempaltes.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   03/26/10 - allow for ORDER_INITIATE orders to check for returns
 *                         when checking isVoidable
 *    abondala  01/03/10 - update header date
 *    asinton   06/11/09 - Improved comments and javadocs.
 *    asinton   06/11/09 - Added 'history' of tender state in method
 *                         evaluateTenderState in order to determine next
 *                         tender state.
 *    asinton   06/05/09 - Override hasBeenRetrieved() and
 *                         lessThanOrEqualToMaxReturnCash() in order to fix
 *                         return options. Also changed the
 *                         evaluateTenderState() method to depend on
 *                         OrderTranaction to determine if canceled order items
 *                         exist.
 *    asinton   04/28/09 - Replaced tabs with spaces and improved comment in
 *                         evaluateTenderState().
 *    asinton   04/28/09 - When pickup or delivery orders are canceled that
 *                         have items that are already picked up need to
 *                         iterate thru the remaining order line items to check
 *                         that they're canceled.
 *    asinton   04/21/09 - Moved implementation for getDepositAmount and
 *                         updateOrderStatus from AbstractRetailTransactionADO
 *                         to OrderTransactionADO.
 *    aphulamb  04/15/09 - Display refund option screen instead of change due
 *                         screen while we cancel an Order
 *
 * ===========================================================================
 * $Log:
 *    7    360Commerce 1.6         5/2/2008 5:02:37 PM    Deepti Sharma
 *         CR-31562: Changes to return true for receipt
 *    6    360Commerce 1.5         3/31/2008 1:51:34 PM   Mathews Kochummen
 *         forward port from v12x to trunk
 *    5    360Commerce 1.4         4/25/2007 8:52:48 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    4    360Commerce 1.3         12/13/2005 4:42:33 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:29:15 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:54 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:54 PM  Robert Pearse
 *
 *   Revision 1.3  2004/04/27 15:50:29  epd
 *   @scr 4513 Fixing tender change options functionality
 *
 *   Revision 1.2  2004/02/12 16:47:57  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.4   Feb 05 2004 13:22:44   rhafernik
 * log4j conversion
 *
 *    Rev 1.3   Jan 09 2004 18:28:22   cdb
 * Corrected glaring defect.
 * Resolution for 3677: Service Alert Cancel - not refunding correct amount.
 *
 *    Rev 1.2   Jan 09 2004 12:54:20   cdb
 * Different behavior is required for canceling a special order. Overrides evaluateTenderState for the discrepancy.
 * Resolution for 3677: Service Alert Cancel - not refunding correct amount.
 *
 *    Rev 1.1   Dec 11 2003 19:04:34   Tim Fritz
 * Added the voidCheckForSuspendedTransaction() method to check to see if the transaction is suspended.
 * Resolution for 3500: Suspended transactions can be post voided.
 *
 *    Rev 1.0   Nov 04 2003 11:14:32   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 17 2003 12:35:18   epd
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.transaction;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.PaymentHistoryInfoIfc;
import oracle.retail.stores.domain.financial.PaymentIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderStatusIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc;
import oracle.retail.stores.pos.ado.utility.AuthorizationException;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;

import org.apache.log4j.Logger;

/**
 * Provides special functionality for Order Transactions.
 */
public class OrderTransactionADO extends AbstractRetailTransactionADO
                                 implements ReturnableTransactionADOIfc
{
    /** serialVersionUID */
    private static final long serialVersionUID = 3854355886771460374L;

    /** the performance logger */
    protected static final Logger perf = Logger.getLogger("PERF." + OrderTransactionADO.class.getName());

    /** HashMap of PaymentHistoryInfoIfc's retrieved from originating order */
    protected LinkedHashMap<String,PaymentHistoryInfoIfc> originalPaymentHistory = null;

    /** Initial tender state - This is set in evaluateTenderState() */
    protected TenderStateEnum initialTenderState = null;

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.transaction.AbstractRetailTransactionADO#instantiateTransactionRDO()
     */
    protected TransactionIfc instantiateTransactionRDO()
    {
        return DomainGateway.getFactory().getOrderTransactionInstance();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.transaction.AbstractRetailTransactionADO#isVoidable()
     */
    public boolean isVoidable(String currentTillID) throws VoidException
    {
        // check if authorized tenders are not voidable due to missing account ID token
        voidCheckAuthorizedTenderMissingToken();

        // cannot post void transactions containing gift card issues, reloads, or redeems.
        voidCheckForGiftCardOperation();

        // 1) Make sure we have the same Till ID
        voidCheckForSameTill(currentTillID);

        // 2) Transaction should not already be voided
        voidCheckForPreviousVoid();

        // 3) Check for modified transaction
        voidCheckForModifiedTransaction();

        // 4) Make sure any issued tenders have not been used.
        voidCheckForIssuedTenderModifications();

        // 5) Check that void is allowed for tranasactions
        //    containing debit tenders
        voidCheckDebitAllowed();

        // 6) Make sure the transaction is not suspended
        voidCheckForSuspendedTransaction();

        // 7) Make sure the order was not canceled
        voidCheckForCanceled();

        // 8)  Make sure the transaction does not have a cross channel order item
        voidCheckForCrossChannelOrder();

        // 9)  Make sure the Order status is not Complete
        voidCheckForOrderCompleteStatus();
        
        return true;
    }

    /**
     * Checks to see if transaction has been modified
     * @throws VoidException
     */
    protected void voidCheckForModifiedTransaction()
    throws VoidException
    {
        if (transactionRDO.getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE
                || transactionRDO.getTransactionType() == TransactionIfc.TYPE_ORDER_PARTIAL
                || transactionRDO.getTransactionType() == TransactionIfc.TYPE_ORDER_COMPLETE)
        {
            voidCheckForReturnItems();
        }
        if (transactionRDO.getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE)
        {
            if (((OrderTransactionIfc)transactionRDO).getOrderStatus().getStatus().getStatus() != OrderConstantsIfc.ORDER_STATUS_NEW)
            {
                throw new VoidException("Transaction Modified", VoidErrorCodeEnum.TRANSACTION_MODIFIED);
            }
        }
        else if (transactionRDO.getTransactionType() == TransactionIfc.TYPE_ORDER_PARTIAL)
        {
            if (((OrderTransactionIfc)transactionRDO).getOrderStatus().getStatus().getStatus() != OrderConstantsIfc.ORDER_STATUS_PARTIAL)
            {
                throw new VoidException("Transaction Modified", VoidErrorCodeEnum.TRANSACTION_MODIFIED);
            }
        }
        else if (transactionRDO.getTransactionType() == TransactionIfc.TYPE_ORDER_CANCEL)
        {
            if (((OrderTransactionIfc)transactionRDO).getOrderStatus().getStatus().getStatus() != OrderConstantsIfc.ORDER_STATUS_CANCELED)
            {
                throw new VoidException("Transaction Modified", VoidErrorCodeEnum.TRANSACTION_MODIFIED);
            }
        }
    }

    /**
     * Makes sure the order was not "canceled".
     * @throws VoidException
     */
    protected void voidCheckForCanceled()
    throws VoidException
    {
        if (transactionRDO.getTransactionType() == TransactionIfc.TYPE_ORDER_CANCEL)
        {
            throw new VoidException("Canceled orders not allowed for void", VoidErrorCodeEnum.INVALID_TRANSACTION);
        }
    }
    
    /**
     * Make sure the transaction does not have a cross channel order item
     */
    protected void voidCheckForCrossChannelOrder() throws VoidException
    {
    	if ( transactionRDO instanceof SaleReturnTransactionIfc)
    	{
	    	SaleReturnTransactionIfc txn = ((SaleReturnTransactionIfc)transactionRDO);
	    	AbstractTransactionLineItemIfc lineItems[] = txn.getLineItems();
	        for (int i = 0; i < lineItems.length; i++)
	        {
	            if (lineItems[i] instanceof SaleReturnLineItemIfc && ((SaleReturnLineItemIfc) lineItems[i]).getOrderItemStatus() != null)
	            {
	            	SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc) lineItems[i];
	            	OrderItemStatusIfc orderItemStatus = lineItem.getOrderItemStatus();
	            	if (orderItemStatus != null && orderItemStatus.isCrossChannelItem())
	            	{
	            		 throw new VoidException("Cross Channel Order does not allowed for void", VoidErrorCodeEnum.INVALID_TRANSACTION);
	            	}
	            }
	        }
    	}
    }    
    
    /**
     * Make sure the Order is not of Status Complete. Also, check if the current
     * order has not moved to Completed status in a later transaction
     * 
     * @throws VoidException
     */
    protected void voidCheckForOrderCompleteStatus() throws VoidException
    {
        if (transactionRDO instanceof SaleReturnTransactionIfc)
        {
            // Order should not be of status Complete
            if (transactionRDO.getTransactionType() == TransactionIfc.TYPE_ORDER_COMPLETE)
            {
                throw new VoidException("Completed orders cannot be Voided", VoidErrorCodeEnum.INVALID_TRANSACTION);
            }
            else if(transactionRDO.getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE)
            {
                /*
                 * All order line items should not have progressed to Complete
                 * status
                 */
                SaleReturnTransactionIfc txn = ((SaleReturnTransactionIfc)transactionRDO);
                AbstractTransactionLineItemIfc lineItems[] = txn.getLineItems();
                for (int i = 0; i < lineItems.length; i++)
                {
                    if (lineItems[i] instanceof SaleReturnLineItemIfc
                            && ((SaleReturnLineItemIfc)lineItems[i]).getOrderItemStatus() != null)
                    {
                        SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)lineItems[i];
                        OrderItemStatusIfc orderItemStatus = lineItem.getOrderItemStatus();
                        if (orderItemStatus != null
                                && orderItemStatus.getStatus().getStatus() != OrderConstantsIfc.ORDER_ITEM_STATUS_PICKED_UP)
                        {
                            return;
                        }
                    }
                }
                throw new VoidException("Completed orders cannot be Voided", VoidErrorCodeEnum.TRANSACTION_MODIFIED);
            }
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.journal.JournalableADOIfc#getJournalMemento()
     */
    public Map<String,Object> getJournalMemento()
    {
        return super.getJournalMemento();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.transaction.RetailTransactionADOIfc#save(oracle.retail.stores.ado.store.RegisterADO)
     */
    public void save(RegisterADO registerADO) throws DataException
    {
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.transaction.ReturnableTransactionADOIfc#isReturnWithReceipt()
     */
    public boolean isReturnWithReceipt()
    {
        boolean result = false;

        if (transactionRDO.getTransactionType() == TransactionIfc.TYPE_ORDER_CANCEL ||
            ((transactionRDO.getTransactionType() == TransactionIfc.TYPE_ORDER_COMPLETE ||
              transactionRDO.getTransactionType() == TransactionIfc.TYPE_ORDER_PARTIAL) &&
             ((OrderTransactionIfc)transactionRDO).getTenderTransactionTotals().getGrandTotal().signum() == CurrencyIfc.NEGATIVE))
        {
            result = true;
        }
        return result;
    }

    /**
     * Returns if a type of order transaction is considered to be a return with the original
     * retrieved. Return true for order delete or order complete/partial complete with a
     * negative tender totals. In the case of an order transaction, this function should always
     * return identical result as the isReturnWithReceipt function.
     *
     * @return boolean
     */
    public boolean isReturnWithOriginalRetrieved()
    {
        return isReturnWithReceipt();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.transaction.RetailTransactionADOIfc#evaluateBalanceDue()
     */
    public TenderStateEnum evaluateTenderState()
    {
        // recalculate transaction total if needed based on dirty flag
        // set by tender.
        recalculateTransactionTotal();

        TenderStateEnum tenderState = null;
        /*
         *  return REFUND_OPTIONS if balance is negative and the order is canceled or
         *  any order items are canceled.
         *  The initialTenderState was added to remember the initial tender state.  Using this
         *  value prevents the change due or overtender from logically becoming a refund.  This
         *  affects the tender servicing where a refund requires the customer to be linked,
         *  which is not appropriate when tendering
         */
        OrderTransactionIfc orderTransaction = (OrderTransactionIfc)transactionRDO;
        if (getBalanceDue().signum() == CurrencyIfc.NEGATIVE &&
                (initialTenderState == null || initialTenderState == TenderStateEnum.REFUND_OPTIONS) &&
                (orderTransaction.getTransactionType() == TransactionIfc.TYPE_ORDER_CANCEL ||
                        orderTransaction.containsCanceledOrderLineItems()))
        {
            tenderState = TenderStateEnum.REFUND_OPTIONS;
        }
        else
        {
            tenderState = super.evaluateTenderState();
        }
        // set the initialTenderState to remember what it was next time thru
        if(initialTenderState == null)
        {
            initialTenderState = tenderState;
        }

        return tenderState;
    }

    /**
     * This method returns true if PAT Cash Tender criteria are met
     *
     * @return true if PAT Cash Tender criteria are met
     */
    public boolean isPATCashTransaction()
    {
        if (perf.isDebugEnabled())
        {
            perf.debug("Entering isPATCashTransaction");
        }
        boolean isPATCashTender = false;

        UtilityIfc util = getUtility();
        if ("Y".equals(util.getParameterValue("PATCustomerInformation", "Y")))
        {
            logger.debug("Order Total: " + ((OrderTransactionIfc)transactionRDO).getOrderStatus().getTotal());
            CurrencyIfc patCashThreshold = DomainGateway.getBaseCurrencyInstance(PAT_CASH_THRESHOLD);
            if (patCashThreshold.compareTo(((OrderTransactionIfc)transactionRDO).getOrderStatus().getTotal())
                                                    == CurrencyIfc.LESS_THAN)
            {
                if ((getBalanceDue().signum() != CurrencyIfc.POSITIVE))
                {
                    CurrencyIfc patCashTotal = DomainGateway.getBaseCurrencyInstance();

                    HashMap<String,PaymentHistoryInfoIfc> newTenders = getPATCashTenders();
                    for (Iterator<String> i = newTenders.keySet().iterator(); i.hasNext();)
                    {
                        String key = i.next();
                        patCashTotal = patCashTotal.add(newTenders.get(key).getTenderAmount());
                    }
                    if (patCashTotal.compareTo(((TenderableTransactionIfc) transactionRDO).getTenderTransactionTotals().getGrandTotal())
                        == CurrencyIfc.GREATER_THAN)
                    {
                        patCashTotal = ((TenderableTransactionIfc) transactionRDO).getTenderTransactionTotals().getGrandTotal();
                    }
                    if (transactionRDO.getTransactionType() != TransactionIfc.TYPE_ORDER_COMPLETE)
                    {
                        isPATCashTender = patCashThreshold.compareTo(patCashTotal) == CurrencyIfc.LESS_THAN;

                        if (isPATCashTender)
                        {
                            restorePaymentHistory(originalPaymentHistory,
                                ((OrderTransactionIfc)transactionRDO).getPaymentHistoryInfoCollection());
                            if (logger.isDebugEnabled())
                            {
                                logger.debug("This is a PAT Cash Transaction (including history) for order complete");
                            }
                        } // End check on sum of pat cash tenders
                        else
                        {
                            updatePaymentHistory(originalPaymentHistory,
                                ((OrderTransactionIfc)transactionRDO).getPaymentHistoryInfoCollection(),
                                newTenders);
                            logger.debug("Not PAT Cash Transaction as total pat tenders is less than PAT Cash Threshold: " + patCashTotal);
                        }
                    } // End check for order complete
                    else
                    {
                        patCashTotal = totalPaymentHistory(originalPaymentHistory, patCashTotal);
                        isPATCashTender = patCashThreshold.compareTo(patCashTotal) == CurrencyIfc.LESS_THAN;
                        if (logger.isDebugEnabled())
                        {
                            if (isPATCashTender)
                            {
                                logger.debug("This is a PAT Cash Transaction (including history) for order complete");
                            }
                            else
                            {
                                logger.debug("Not PAT Cash Transaction as total pat tenders (including history) is less than PAT Cash Threshold: " + patCashTotal);
                            }
                        }
                    }
                } // Check balance due
                else if (logger.isDebugEnabled())
                {
                    logger.debug("Not PAT Cash Transaction (yet) as balance due is positive: "
                                 + getBalanceDue());
                }
            } // End order total check
            else if (logger.isDebugEnabled())
            {
                logger.debug("Not PAT Cash order as order total is less than PAT Cash Threshold: "
                             + ((OrderTransactionIfc)transactionRDO).getOrderStatus().getTotal());
            }
        } // End parameter check
        else if (logger.isDebugEnabled())
        {
            logger.debug("Not PAT Cash Transaction as PATCustomerInformation parameter is 'N'");
        }

        if (perf.isDebugEnabled())
        {
            perf.debug("Exiting isPATCashTransaction");
        }
        return isPATCashTender;
    }

    /**
     * Some transactions require specific behavior when voided to reverse certain actions (such as contacting a 3rd
     * party authorizer for a reversal). Each transaction type should be responsible for handling its own specific
     * logic. It returns a HashMap of Tender Groups containing inversed tenders
     *
     * We use this method explicitly for reversing the payment
     *
     * @return Inverse tenders from the current transaction.
     * @throws AuthorizationException
     */
    protected LinkedHashMap<TenderTypeEnum, TenderGroupADOIfc> processVoid() throws AuthorizationException
    {
        if (transactionRDO.getTransactionType() == TransactionIfc.TYPE_ORDER_PARTIAL)
        {
            updatePaymentHistoryForVoid(originalPaymentHistory,
                ((OrderTransactionIfc)transactionRDO).getPaymentHistoryInfoCollection());
        }
        return super.processVoid();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy()
     */
    public EYSDomainIfc toLegacy()
    {
        return transactionRDO;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy(java.lang.Class)
     */
    @SuppressWarnings("rawtypes")
    public EYSDomainIfc toLegacy(Class type)
    {
        return toLegacy();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
     */
    public void fromLegacy(EYSDomainIfc rdo)
    {
        transactionRDO = (OrderTransactionIfc)rdo;

        // get and convert RDO tenders
        Iterator<TenderLineItemIfc> iter = ((TenderableTransactionIfc)transactionRDO).getTenderLineItemsVector().iterator();
        while (iter.hasNext())
        {
            // Create ADO tender from RDO tender
            TenderLineItemIfc tenderRDO = iter.next();
            try
            {
                TenderFactoryIfc factory = (TenderFactoryIfc)ADOFactoryComplex.getFactory("factory.tender");
                TenderADOIfc tenderADO = factory.createTender(tenderRDO);
                ((ADO)tenderADO).fromLegacy(tenderRDO);

                // add the tender to the transaction
                addTenderNoValidation(tenderADO);
            }
            catch (ADOException e)
            {
                logger.warn("ADOException caught in fromLegacy()", e);
            }
        }

        // Save Payment History
        if (((OrderTransactionIfc)transactionRDO).getPaymentHistoryInfoCollection() != null)
        {
            originalPaymentHistory = getPaymentHistoryHash(((OrderTransactionIfc)transactionRDO).getPaymentHistoryInfoCollection());
        }
        else
        {
            originalPaymentHistory = new LinkedHashMap<String,PaymentHistoryInfoIfc>(0);
        }

    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.transaction.AbstractRetailTransactionADO#getDepositAmount()
     */
    @Override
    public CurrencyIfc getDepositAmount()
    {
        CurrencyIfc depositAmount = DomainGateway.getBaseCurrencyInstance();
        TransactionTotalsIfc total = ((OrderTransactionIfc)transactionRDO).getTransactionTotals();
        OrderStatusIfc orderStatus = ((OrderTransactionIfc)transactionRDO).getOrderStatus();
        CurrencyIfc amountTender = DomainGateway.getBaseCurrencyInstance();
        if (orderStatus.getDepositAmount().equals(orderStatus.getTotal()))
        {
            if (total.getAmountTender().equals(amountTender))
            {
                depositAmount = total.getGrandTotal();
            }
            else
            {
                depositAmount = total.getBalanceDue();
            }
        }
        return depositAmount;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.transaction.AbstractRetailTransactionADO#updateOrderStatus()
     */
    @Override
    public void updateOrderStatus()
    {
        OrderTransactionIfc orderTransaction = (OrderTransactionIfc)transactionRDO;
        OrderStatusIfc orderStatus = orderTransaction.getOrderStatus();
        TransactionTotalsIfc total = orderTransaction.getTransactionTotals();
        PaymentIfc payment = orderTransaction.getPayment();
        CurrencyIfc balanceDue = DomainGateway.getBaseCurrencyInstance();

        // order deposit is same as order total
        CurrencyIfc xcGrandTotal = orderTransaction.getXChannelGrandTotal();
        if (orderStatus.getDepositAmount().equals(orderStatus.getTotal()))
        {
            orderStatus.setXChannelDepositAmount(xcGrandTotal);
            orderStatus.setStoreOrderDepositAmount(total.getGrandTotal().subtract(xcGrandTotal));
            orderStatus.setXChannelBalanceDue(balanceDue);
            orderStatus.setStoreOrderBalanceDue(balanceDue);
            payment.setBalanceDue(balanceDue);
            payment.setPaymentAmount(total.getGrandTotal());
            ((OrderTransactionIfc)transactionRDO).setTenderTransactionTotals(total);
        }
        else
        {
            orderStatus.setXChannelTotal(xcGrandTotal);
            orderStatus.setStoreOrderTotal(total.getGrandTotal().subtract(xcGrandTotal));
            orderStatus.setXChannelBalanceDue(orderStatus.getXChannelTotal().subtract(orderStatus.getXChannelDepositAmount()));
            orderStatus.setStoreOrderBalanceDue(orderStatus.getStoreOrderTotal().subtract(orderStatus.getStoreOrderDepositAmount()));
            payment.setBalanceDue(orderStatus.getBalanceDue());
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.transaction.AbstractRetailTransactionADO#hasBeenRetrieved()
     */
    @Override
    protected boolean hasBeenRetrieved()
    {
        /*
         * The order transaction must have been retrieved if not an order initiate.
         */
        boolean retrieved = false;
        int type = transactionRDO.getTransactionType();
        if(type != TransactionConstantsIfc.TYPE_ORDER_INITIATE)
        {
            retrieved = true;
        }
        return retrieved;
    }

    /**
     * This method checks to see if a regular receipt was supplied by the customer.
     * See the hasGiftReceipt() method for testing for gift receipt
     * @return
     */
    protected boolean hasReceipt()
    {
       return isReturnWithReceipt();
    }
}
