/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadOrderByTransaction.java /main/21 2014/06/16 13:56:04 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       06/15/14 - fix transaction total and discount calculation for
 *                         order pickup transaction
 *    sgu       06/14/14 - add support for start index of take with items in
 *                         pickup
 *    sgu       06/13/14 - retrive multiple transaction for an order in
 *                         enterprise order read
 *    sgu       06/11/14 - combine multiple order transactions into one final
 *                         one
 *    sgu       06/09/14 - read item original transaction for an order
 *    jswan     05/07/13 - Removed references to deprecated methods.
 *    sgu       05/09/12 - use the order status from the order transaction
 *    sgu       04/26/12 - save cross channel order delivery info at
 *                         transaction level
 *    asinton   03/19/12 - let client code handle missing customer data.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    jkoppolu  08/05/10 - Fix for Bug#9955620 - Null pointer exception when
 *                         searching for special order.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    jswan     04/24/09 - Code review changes.
 *    jswan     04/24/09 - Modified to ensure that orders created in training
 *                         mode can only retrieve in training mode, and
 *                         non-training mode orders can only be retrieved in
 *                         non-training mode.
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         2/22/2008 10:30:34 AM  Pardee Chhabra  CR
 *         30191: Tender Refund options are not displayed as per specification
 *          for Special Order Cancel feature.
 *    5    360Commerce 1.4         4/27/2006 7:26:57 PM   Brett J. Larsen CR
 *         17307 - remove inventory functionality - stage 2
 *    4    360Commerce 1.3         12/13/2005 4:43:44 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:28:41 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:44 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:59 PM  Robert Pearse
 *
 *   Revision 1.7  2004/06/29 21:58:58  aachinfiev
 *   Merge the changes for inventory & POS integration
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:46  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:00   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Mar 07 2003 15:47:36   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.0   Jun 03 2002 16:37:46   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:47:32   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:07:32   msg
 * Initial revision.
 *
 *    Rev 1.4   04 Mar 2002 10:19:06   pjf
 * Remove advanced pricing discounts from orders initiated through the web.
 * Resolution for POS SCR-196: Line items coming from web orders allow adv pricing
 *
 *    Rev 1.3   Feb 05 2002 16:33:36   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 *
 *    Rev 1.2   09 Dec 2001 10:18:32   mpm
 * Added support for OrderLineItemIfc in order transactions and associated database activity.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   05 Dec 2001 12:53:36   mpm
 * Corrected for not-found in read-order.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   27 Nov 2001 06:25:38   mpm
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;
// java imports
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.TransactionDiscountAuditIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ItemContainerProxyIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.order.OrderStatusIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
//--------------------------------------------------------------------------
/**
    This operation reads order data from the transaction tables.
    It expects that an OrderStatusIfc object is already in the
    result object.<P>
    <P>
    @version $Revision: /main/21 $
**/
//--------------------------------------------------------------------------
public class JdbcReadOrderByTransaction
extends JdbcReadTransaction
{
    /** serialVersionUID */
    private static final long serialVersionUID = 1822613467373695875L;

    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcReadOrderByTransaction.class);

    /**
       The performance logger
    **/
    protected static final Logger perf = Logger.getLogger("PERF." + JdbcReadOrderByTransaction.class.getName());

    //----------------------------------------------------------------------
    /**
        Class constructor.
     **/
    //----------------------------------------------------------------------
    public JdbcReadOrderByTransaction()
    {
        super();
        setName("JdbcReadOrderByTransaction");
    }

    //----------------------------------------------------------------------
    /**
        Executes the SQL statements against the database.
        <P>
        @param  dataTransaction     The data transaction
        @param  dataConnection      The connection to the data source
        @param  action              The information passed by the valet
        @exception DataException upon error
    **/
    //----------------------------------------------------------------------
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
                        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcReadOrderByTransaction.execute");
        if (perf.isDebugEnabled())
        {
            perf.debug("Entering JdbcReadOrderByTransaction.execute");
        }
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        try
        {
            Object resultObject = dataTransaction.getResult();
            if (resultObject != null)
            {
                // get order status as seed
                OrderStatusIfc orderStatus = (OrderStatusIfc) resultObject;
                
                TransactionIfc[] itemOriginalTransactions = getItemOriginalTransactions(orderStatus);
                LocaleRequestor localeRequestor = null;
                if (action.getDataObject() != null)
                {
                    localeRequestor = (LocaleRequestor)action.getDataObject();
                }
                else
                {
                    localeRequestor = new LocaleRequestor(LocaleMap.getLocale(LocaleMap.DEFAULT));
                }
                
                // read transaction
                List<OrderTransactionIfc> transactions = new ArrayList<OrderTransactionIfc>();
                for (TransactionIfc itemOriginalTransation : itemOriginalTransactions)
                {
                    TransactionIfc transaction =
                            selectTransaction(connection, itemOriginalTransation,
                                              orderStatus.getOrderID(), localeRequestor);
                    transactions.add((OrderTransactionIfc)transaction);
                }
               
    
                // load transaction data into order object
                OrderIfc order = createOrder(orderStatus, transactions.toArray(new OrderTransactionIfc[0]));
                dataTransaction.setResult(order);
            }
        }
        catch(Exception e)
        {
            logger.error(Util.throwableToString(e));
        }

        if (perf.isDebugEnabled())
        {
            perf.debug("Exiting JdbcReadOrderByTransaction.execute");
        }
        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcReadOrderByTransaction.execute");
    }
    
    //---------------------------------------------------------------------
    /**
        Removes advanced pricing discounts currently applied to the transaction
        regardless of their participation in a best deal group.
    **/
    //---------------------------------------------------------------------
    public void removeAdvancedPricingDiscounts(Iterator i)
    {
        while (i.hasNext())
        {
            ((SaleReturnLineItemIfc)i.next()).removeAdvancedPricingDiscount();
        }
    }
    
    /**
     * Return a list of item originating transactions from the order status
     * @param orderStatus the order status
     * @return a list of item originating transactions
     */
    protected TransactionIfc[] getItemOriginalTransactions(OrderStatusIfc orderStatus)
    {
        List<TransactionIfc> itemOriginalTransactions = new ArrayList<TransactionIfc>();
        
        for (int i=0; i<orderStatus.getItemOriginalTransactionIDs().length; i++)
        {
            TransactionIfc itemOriginalTransaction = DomainGateway.getFactory().getTransactionInstance();
            itemOriginalTransaction.initialize(orderStatus.getItemOriginalTransactionIDs()[i]);
            itemOriginalTransaction.setBusinessDay(orderStatus.getItemOriginalBusinessDates()[i]);
            itemOriginalTransaction.setTrainingMode(orderStatus.isTrainingMode());
            if (orderStatus.getLocaleRequestor() != null)
            {
                itemOriginalTransaction.setLocaleRequestor(orderStatus.getLocaleRequestor());
            }

            itemOriginalTransactions.add(itemOriginalTransaction);
        }
        
        return itemOriginalTransactions.toArray(new TransactionIfc[0]);
    }
    
    /**
     * Create an order from an order status object and an array of order transactions
     * @param orderStatus the order status object
     * @param orderTransactions an array of order transactions
     * @return the order object
     */
    protected OrderIfc createOrder(OrderStatusIfc orderStatus, OrderTransactionIfc[] orderTransactions)
    {
        // load transaction data into order object
        OrderIfc order = DomainGateway.getFactory().getOrderInstance();
        
        // 30191
        OrderTransactionIfc orderTransaction = combineOrderTransactions(order, orderTransactions);
        order.setCustomer(orderTransaction.getCustomer());
        ItemContainerProxyIfc proxy = orderTransaction.getItemContainerProxy();
        if (orderStatus.getInitiatingChannel() == OrderConstantsIfc.ORDER_CHANNEL_WEB)
        {
             removeAdvancedPricingDiscounts(proxy.getLineItemsIterator());
             proxy.clearAdvancedPricingRules();
             orderTransaction.calculateBestDeal();
        }

        order.setItemContainerProxy(orderTransaction.getItemContainerProxy());
        order.setStatus(orderTransaction.getOrderStatus());
        order.setTotals(orderTransaction.getTransactionTotals());
        if (orderTransaction.getPaymentHistoryInfoCollection() != null
            && orderTransaction.getPaymentHistoryInfoCollection().size() > 0)
        {
            order.getPaymentHistoryInfoCollection().addAll(orderTransaction.getPaymentHistoryInfoCollection());
        }
        if (orderTransaction.getDeliveryDetails() != null
                && orderTransaction.getDeliveryDetails().size() > 0)
        {
            order.getDeliveryDetails().addAll(orderTransaction.getDeliveryDetails());
        }
        
        return order;
    }
    
    /**
     * This function combines order items initiated from multiple transactions into one
     * 
     * @param orderTransactions a list of order transactions to combine
     * @return the order transaction with items from multiple transactions
     */
    protected OrderTransactionIfc combineOrderTransactions(OrderIfc order, OrderTransactionIfc[] orderTransactions)
    {
        OrderTransactionIfc resultTxn = null;
        int saleItemSizeAddedInPickup = 0;

        // If there is only one transaction and it is an order initiate transaction, return it.
        if ((orderTransactions.length == 1) && !orderTransactions[0].isOrderPickupOrCancel())
        {
            resultTxn = orderTransactions[0];
        }
        else
        {
            // If there are more than one transactions, try to combine them into one.
            resultTxn = DomainGateway.getFactory().getOrderTransactionInstance();
            List<TenderLineItemIfc> tenderLineItems = new ArrayList<TenderLineItemIfc>();
            for (OrderTransactionIfc orderTransaction : orderTransactions)
            {
                // set order status and transaction type
                if (StringUtils.isBlank(resultTxn.getOrderID()))
                {
                    resultTxn.setOrderStatus(orderTransaction.getOrderStatus());
                    resultTxn.setTransactionType(TransactionConstantsIfc.TYPE_ORDER_INITIATE);
                    resultTxn.setTransactionStatus(TransactionIfc.STATUS_COMPLETED);
                    resultTxn.setCurrencyType(orderTransaction.getCurrencyType());        
                    resultTxn.setTransactionCountryCode(orderTransaction.getTransactionCountryCode());
                    resultTxn.setTimestampBegin(orderTransaction.getTimestampBegin());
                }

                // get the customer from the first transaction that has a linked customer
                if (resultTxn.getCustomer() == null)
                {
                    resultTxn.setCustomer(orderTransaction.getCustomer());
                }

                // set order line items and order delivery info
                if (orderTransaction.isOrderPickupOrCancel())
                {                
                    // This is an order pickup or cancel transaction
                    // We only add take with items from these transactions, and ignore the order pickup/cancel order line items
                    for (AbstractTransactionLineItemIfc lineItem : orderTransaction.getLineItems())
                    {
                        SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) lineItem;
                        if (srli.getOrderItemStatus().getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_SALE)
                        {
                            saleItemSizeAddedInPickup++;
                            int newLineNumber = resultTxn.getLineItemsSize();

                            // reset line number since they come after line items from order initiate transaction
                            srli.setLineNumber(newLineNumber); 
                            resultTxn.getLineItemsVector().add(srli);
                        }
                    }
                }  
                else
                {
                    // This is an order initiate transaction
                    // Get all line items from an order initiate transaction.
                    // There is no need to reset line number since an order initiate transaction 
                    // is always the first order transaction to be processed.
                    resultTxn.getLineItemsVector().addAll(orderTransaction.getLineItemsVector());

                    // set order delivery from order initiate transaction. 
                    // Order pickup/cancel transaction does not contain order delivery information.
                    if (orderTransaction.getDeliveryDetails() != null
                            && orderTransaction.getDeliveryDetails().size() > 0)
                    {
                        resultTxn.getDeliveryDetails().addAll(orderTransaction.getDeliveryDetails());
                    }
                }

                // collect order tender lines from all transactions
                tenderLineItems.addAll(orderTransaction.getTenderLineItemsVector());

                // set order payment history
                if (orderTransaction.getPaymentHistoryInfoCollection() != null
                        && orderTransaction.getPaymentHistoryInfoCollection().size() > 0)
                {
                    resultTxn.getPaymentHistoryInfoCollection().addAll(orderTransaction.getPaymentHistoryInfoCollection());
                }
            }

            // set transaction discounts
            SaleReturnLineItemIfc[] lineItems = resultTxn.getLineItemsVector().toArray(new SaleReturnLineItemIfc[0]);
            TransactionDiscountAuditIfc[] transactionDiscounts = DomainGateway.getFactory().getItemTransactionDiscountAggregatorInstance().aggregate(lineItems);
            resultTxn.getItemContainerProxy().setTransactionDiscounts(transactionDiscounts);

            // set transaction tax
            TransactionTaxIfc transactionTax = DomainGateway.getFactory().getItemTransactionTaxAggregatorInstance().aggregate(lineItems);
            resultTxn.getItemContainerProxy().setTransactionTax(transactionTax);

            // update transaction totals
            resultTxn.updateTransactionTotals();

            // set tender line items
            resultTxn.setTenderLineItems(tenderLineItems.toArray(new TenderLineItemIfc[0]));
        }
        
        order.setOriginalTransaction(resultTxn);
        order.setSaleItemSizeAddedInPickup(saleItemSizeAddedInPickup);
           
        return resultTxn;
    }
    
}
