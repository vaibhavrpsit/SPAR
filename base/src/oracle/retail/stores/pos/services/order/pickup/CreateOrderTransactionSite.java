/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/pickup/CreateOrderTransactionSite.java /main/4 2014/07/08 14:44:25 vineesin Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   06/26/14 - Add comment for revision history.
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.order.pickup;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.returns.ReturnTenderDataElementIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.order.common.OrderCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

public class CreateOrderTransactionSite extends PosSiteActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 4367502582348105887L;

    /** class name constant */
    public static final String SITENAME = "CreateOrderTransactionSite";
    
    /** parameter name of re-price order pickup */
    public static final String REPRICE_ORDER_PICKUP = "RepriceOnOrderPickup";

    /** revision number for this class */
    public static final String revisionNumber = "$Revision: /main/4 $";

     /**
      * Customer name bundle tag
      */
     protected static final String CUSTOMER_NAME_TAG = "CustomerName";

     /**
      * Customer name default text
      */
     protected static final String CUSTOMER_NAME_TEXT = "{0} {1}";

    /**
     * Visual presentation for the Edit Item Status screen.
     * </p>
     * @param bus the bus arriving at this site
     * @throws ParameterException 
     */
    public void arrive(BusIfc bus) 
    {
        //Initialize Variables
        PickupOrderCargo    cargo       = (PickupOrderCargo)bus.getCargo();
        POSUIManagerIfc     ui          = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        TransactionUtilityManagerIfc transUtility =
            (TransactionUtilityManagerIfc) bus.getManager(TransactionUtilityManagerIfc.TYPE);
        
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

        boolean repriceOrderPickup = false;
        try
        {
            repriceOrderPickup = "Y".equalsIgnoreCase(pm.getStringValue(REPRICE_ORDER_PICKUP));
        }
        catch (ParameterException pe)
        {
            logger.error("Could not retrieve parameter " + REPRICE_ORDER_PICKUP);
        }

        OrderTransactionIfc orderTransaction = cargo.getTransaction();
        if (orderTransaction == null)
        {
            OrderIfc order = cargo.getOrder();
            TransactionTaxIfc transactionTax = transUtility.getInitialTransactionTax();
            orderTransaction = order.createOrderTransaction(transactionTax, repriceOrderPickup);

            orderTransaction.setSalesAssociate(cargo.getOperator());
            orderTransaction.getPayment().setBusinessDate(cargo.getRegister().getBusinessDate());
            transUtility.initializeTransaction(orderTransaction);
            cargo.setTransaction(orderTransaction);
            cargo.setOrderTransaction(orderTransaction);

            /*
             * In the case of canceled items where a refund might be given then in order to make
             * the refund options work the original tenders need to be available in the order transaction.
             */
            if(orderTransaction.containsCanceledOrderLineItems())
            {
                SaleReturnTransactionIfc originalTransaction = (SaleReturnTransactionIfc)order.getOriginalTransaction();
                orderTransaction.setReturnTenderElements(getOriginalTenders(originalTransaction.getTenderLineItems()));
            }

            // journal the newly created order pickup/cancel transaction
            writeToJournal(cargo, orderTransaction, bus);
        }
        
        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
    }

    /**
     * Retrieve tenders from original transaction
     *
     * @param tenderList
     * @return ReturnTenderDataElement[] list of tenders
     */
    protected ReturnTenderDataElementIfc[] getOriginalTenders(TenderLineItemIfc[] tenderList)
    {
        ReturnTenderDataElementIfc[] tenders = new ReturnTenderDataElementIfc[tenderList.length];
        for (int i = 0; i < tenderList.length; i++)
        {
            tenders[i] = DomainGateway.getFactory()
                    .getReturnTenderDataElementInstance();
            tenders[i].setTenderType(tenderList[i].getTypeCode());
            if (tenderList[i].getTypeCode() == TenderLineItemIfc.TENDER_TYPE_CHARGE)
            {
                tenders[i].setCardType(((TenderChargeIfc)tenderList[i]).getCardType());
            }
            tenders[i].setAccountNumber(new String(tenderList[i].getNumber()));
            tenders[i].setTenderAmount(tenderList[i].getAmountTender());
        }
        return tenders;
    }

    /**
     * Writes a transaction containing Pickup Order items to the Journal.
     *
     * @param cargo The pickup order cargo.
     * @param transaction The newly create sale return transaction containing.
     * @param bus The bus. order line items
     */
    public void writeToJournal(OrderCargoIfc cargo,
                               OrderTransactionIfc transaction,
                               BusIfc bus)
    {
        OrderIfc order = cargo.getOrder();
        JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        JournalFormatterManagerIfc formatter =
            (JournalFormatterManagerIfc)Gateway.getDispatcher().
                getManager(JournalFormatterManagerIfc.TYPE);
        if(journal != null && formatter != null)
        {
            ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
            journal.journal(transaction.getCashier().getLoginID(),
                    transaction.getTransactionID(),
                    formatter.journalOrder(transaction, order, pm));
        }
    }

}
