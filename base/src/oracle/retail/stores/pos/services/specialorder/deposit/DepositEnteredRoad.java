/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/specialorder/deposit/DepositEnteredRoad.java /main/28 2014/03/04 15:24:50 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  10/30/13 - Fix to include Order Id in the EJ for retrieved txn
 *    yiqzhao   10/02/13 - Exclude KitHeader and ShippingCharge line item.
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    mkutiana  04/02/13 - Store id for pickup display in eJournal
 *    sgu       07/03/12 - added xc order ship delivery date, carrier code and
 *                         type code
 *    sgu       06/27/12 - journal pick up from store case
 *    sgu       05/08/12 - prorate store order and xchannel deposit amount
 *                         separatly
 *    sgu       05/04/12 - refactor OrderStatus to support store order and
 *                         xchannel order
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    mahising  02/27/09 - clean up code after code review by jack for PDO
 *    mahising  02/26/09 - Rework for PDO functionality
 *    mkochumm  02/12/09 - use default locale for dates
 *    npoola    01/27/09 - Fixed the PDO receipt and PDO payment totals in
 *                         TR_LTM_PYAN
 *    mahising  01/19/09 - fix special order issue
 *    aphulamb  11/27/08 - checking files after merging code for receipt
 *                         printing by Amrish
 *    aphulamb  11/24/08 - Checking files after code review by amrish
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         8/6/2007 5:39:48 AM    Chengegowda Venkatesh
 *          CR 28126 : Decreased 1 space character for the fields Minumum
 *         Deposit Amount and Deposit Paid.
 *    4    360Commerce 1.3         4/25/2007 8:51:33 AM   Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:27:44 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:55 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:34 PM  Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:52:03  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:07:22   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Aug 06 2002 16:43:34   jriggins
 * Added SpecialOrderDepositSpec.SpecialOrderDepositDesc to specialorder bundle and reference it in DepositEnteredRoad
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:01:48   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:48:18   msg
 * Initial revision.
 *
 *    Rev 1.3   Jan 13 2002 20:36:48   dfh
 * set payment business date to register business date
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.2   Dec 04 2001 16:08:58   dfh
 * No change.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   Dec 04 2001 16:07:58   dfh
 * test
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Dec 04 2001 15:11:18   dfh
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.specialorder.deposit;

// foundation imports
import java.util.ArrayList;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.PaymentConstantsIfc;
import oracle.retail.stores.domain.financial.PaymentIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderDeliveryDetailIfc;
import oracle.retail.stores.domain.order.OrderStatusIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.specialorder.SpecialOrderCargo;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

//------------------------------------------------------------------------------
/**
 * This class journals the minimum deposit amount and the deposit paid amount
 * for the special order, prior to calling the tender service.
 * <P>
 *
 **/
//------------------------------------------------------------------------------
public class DepositEnteredRoad extends PosLaneActionAdapter
{ // begin class DepositEnteredRoad
    /**
     *
     */
    private static final long serialVersionUID = -1155736380293014475L;

    /**
     * lane name constant
     **/
    public static final String LANENAME = "DepositEnteredRoad";

    /**
     * revision number supplied by source-code-control system
     **/
    public static String revisionNumber = "$Revision: /main/28 $";

    /**
     * Special order deposit label bundle tag
     **/
    protected static final String SPECIAL_ORDER_DEPOSIT_TAG = "SpecialOrderDepositDesc";

    /**
     * Special order deposit label default text
     **/
    protected static final String SPECIAL_ORDER_DEPOSIT_TEXT = "Special Order Deposit";

    //--------------------------------------------------------------------------
    /**
     * Performs the traversal functionality for the aisle. In this case, the
     * minimum deposit amount and the deposit paid are journaled prior to
     * entering the tender service.
     * <P>
     *
     * @param bus the bus traversing this lane
     **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    { // begin traverse()
        SpecialOrderCargo specialOrderCargo = (SpecialOrderCargo)bus.getCargo();
        OrderTransactionIfc orderTransaction = specialOrderCargo.getOrderTransaction();
        OrderStatusIfc orderStatus = orderTransaction.getOrderStatus();
        ArrayList<OrderDeliveryDetailIfc> deliveryItemCollection = new ArrayList<OrderDeliveryDetailIfc>();
        // Gets special order transaction and its components to update
        CurrencyIfc deposit = orderStatus.getDepositAmount();
        CurrencyIfc storeOrderDeposit = orderStatus.getStoreOrderDepositAmount();
        CurrencyIfc storeOrderTotal = orderStatus.getStoreOrderTotal();
        CurrencyIfc xChannelDeposit = orderStatus.getXChannelDepositAmount();
        CurrencyIfc xChannelTotal = orderStatus.getXChannelTotal();
        orderStatus.setStoreOrderBalanceDue(storeOrderTotal.subtract(storeOrderDeposit));
        orderStatus.setXChannelBalanceDue(xChannelTotal.subtract(xChannelDeposit));

        // setup payment - based upon deposit
        PaymentIfc depositPaid = DomainGateway.getFactory().getPaymentInstance();
        CurrencyIfc saleAmount = orderTransaction.getTotalSaleAmount();
        orderStatus.setSaleAmount(saleAmount);
        // for transaction type pdo add this calculated amount
        if (orderTransaction.getOrderType() == OrderConstantsIfc.ORDER_TYPE_ON_HAND)
        {
            ArrayList<Integer> deliveryDetailsID = new ArrayList<Integer>();
            CurrencyIfc pdoDepositAmount = deposit.subtract(saleAmount);
            depositPaid.setPickupDeliveryItemDepositAmount(pdoDepositAmount);
            depositPaid.setPaymentAmount(deposit);
            depositPaid.setIsPDOPayment(true);
            AbstractTransactionLineItemIfc[] lineItems = orderTransaction.getLineItems();
            for (int i = 0; i < lineItems.length; i++)
            {
                SaleReturnLineItemIfc item = (SaleReturnLineItemIfc)lineItems[i];
                if (item.getOrderItemStatus().getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_DELIVERY)
                {
                    int detailId = item.getOrderItemStatus().getDeliveryDetails().getDeliveryDetailID();
                    if (!(deliveryDetailsID.contains(new Integer(detailId))))
                    {
                        deliveryItemCollection.add(item.getOrderItemStatus().getDeliveryDetails());
                        deliveryDetailsID.add(new Integer(detailId));
                    }
                }
            }
            orderTransaction.setDeliveryDetails(deliveryItemCollection);
        }
        else
        {
            depositPaid.setPaymentAmount(deposit);
        }
        depositPaid.setBalanceDue(orderStatus.getBalanceDue());
        depositPaid.setTransactionID(orderTransaction.getTransactionIdentifier());

        // Make sure that we attempt to grab the description from the bundles
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        String specialOrderDepositDesc = utility.retrieveText("SpecialOrderDepositSpec",
                BundleConstantsIfc.SPECIAL_ORDER_BUNDLE_NAME, SPECIAL_ORDER_DEPOSIT_TAG, SPECIAL_ORDER_DEPOSIT_TEXT);
        depositPaid.setDescription(specialOrderDepositDesc);

        depositPaid.setPaymentAccountType(PaymentConstantsIfc.ACCOUNT_TYPE_ORDER);
        depositPaid.setReferenceNumber(orderTransaction.getOrderID());
        depositPaid.setBusinessDate(specialOrderCargo.getRegister().getBusinessDate());
        orderTransaction.setPayment(depositPaid);

        // journal entry for pickup/delivery status of entry
        JournalManagerIfc jmgr = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        if (orderTransaction.getOrderType() == OrderConstantsIfc.ORDER_TYPE_ON_HAND)
        {
            // ejournal entry
            StringBuffer sb = new StringBuffer();

            AbstractTransactionLineItemIfc[] items = specialOrderCargo.getOrderTransaction().getLineItems();
            Object dataObject[] = { orderTransaction.getOrderID() };

            String pickupStoreTag = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PICKUP_STORE_TAG,
                    dataObject);
            
            String pickupDateTag = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PICKUP_DATE_TAG,
                    dataObject);

            String deliveryTag = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DELIVERY_TAG,
                    dataObject);

            String itemLabel = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM_LABEL_PDO,
                    dataObject);



            for (int i = 0; i < items.length; i++)
            {
                SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)items[i];
                if (lineItem.getOrderItemStatus().getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_PICKUP)
                {
                    String pickupStoreID = lineItem.getOrderItemStatus().getPickupStoreID();
                    sb.append(Util.EOL).append(itemLabel).append(((SaleReturnLineItemIfc)items[i]).getItemID().trim())
                    .append(Util.EOL).append(pickupStoreTag.concat(" " + pickupStoreID)).append(Util.EOL);
                    
                    if (!lineItem.getOrderItemStatus().isShipToStoreForPickup())
                    {
                        String pickupDate = lineItem.getOrderItemStatus().getPickupDate().toFormattedString();
                        sb.append(Util.EOL).append(pickupDateTag.concat(" " + pickupDate)).append(Util.EOL);
                    }
                }
                else if (lineItem.getOrderItemStatus().getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_DELIVERY &&
                         !lineItem.isKitHeader() &&
                         !lineItem.isShippingCharge() )
                {
                    String deliveryDate = lineItem.getOrderItemStatus().getDeliveryDetails().getDeliveryDate().toFormattedString();
                    sb.append(Util.EOL).append(itemLabel).append(((SaleReturnLineItemIfc)items[i]).getItemID().trim())
                            .append(Util.EOL).append(deliveryTag.concat(" " + deliveryDate)).append(Util.EOL);
                }
            }
            
            //Journaling orderId
            sb.append(Util.EOL);
            String orderNumber = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.SPECIAL_ORDER_NUMBER, dataObject);
            sb.append(orderNumber).append(Util.EOL);
            jmgr.journal(sb.toString());
        }

        // journal minimum deposit, deposit paid
        StringBuffer sb = new StringBuffer();

        CurrencyIfc minimumDeposit = orderStatus.getMinimumDepositAmount();

        String minDepositString = minimumDeposit.toFormattedString();
        String depositString = deposit.toFormattedString();

        Object dataObject[] = { minDepositString };

        String minDeposit = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.SPECIAL_ORDER_MIN_DEPOSIT_AMOUNT, dataObject);

        sb.append(Util.EOL).append(minDeposit).append(Util.EOL);

        Object depositDataObject[] = { depositString };

        String depositAmountPaid = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.SPECIAL_ORDER_DEPOSIT_PAID, depositDataObject);

        sb.append(Util.EOL).append(depositAmountPaid);

        JournalManagerIfc jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        jmi.journal(orderTransaction.getCashier().getEmployeeID(), orderTransaction.getTransactionID(), sb.toString());
    } // end traverse()

} // end class DepositEnteredRoad
