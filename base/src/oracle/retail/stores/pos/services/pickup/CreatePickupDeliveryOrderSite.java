/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pickup/CreatePickupDeliveryOrderSite.java /main/2 2013/04/16 13:32:32 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    jswan     04/14/09 - Modified to fix conflict between multi quantity
 *                         items and items that have been marked for Pickup or
 *                         Delivery.
 *    npoola    03/27/09 - fix to avoid the POS crash when multile items with
 *                         quantities are pickedup
 *    npoola    03/26/09 - Fix to display line items properly for Gold
 *                         Customers
 *    mahising  03/18/09 - Fixed issue if item qty change and customer link to
 *                         the transaction
 *    mahising  02/26/09 - Rework for PDO functionality
 *    mahising  02/19/09 - fixed ejournal issue for order transaction
 *    npoola    02/11/09 - fix kit item issue
 *    npoola    02/11/09 - fix kit item issue
 *    mahising  01/19/09 - fix special order issue
 *    mahising  01/14/09 - fixed QA issue
 *    mahising  01/13/09 - fix QA issue
 *    aphulamb  01/02/09 - fix delivery issues
 *    aphulamb  12/23/08 - Mock padding fix and PDO flow related changes for
 *                         buttons enable/disable
 *    aphulamb  11/24/08 - Checking files after code review by amrish
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    aphulamb  11/13/08 - Create pickup delivery order
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pickup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.discount.BestDealGroupIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ItemContainerProxyIfc;
import oracle.retail.stores.domain.lineitem.KitComponentLineItemIfc;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.transaction.OrderTransaction;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

public class CreatePickupDeliveryOrderSite extends PosSiteActionAdapter
{

    /**
     * This id is used to tell the compiler not to generate a new
     * serialVersionUID.
     */
    private static final long serialVersionUID = -5194127654780844041L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/2 $";

    protected static final long GENERATE_SEQUENCE_NUMBER = -1;

    /**
     * special order header journal string
     */
    public static final String PDOOrderHeader = "PICKUP/DELIVERY ORDER";

    /**
     * special order number journal string
     */
    public static final String PDOOrderNumber = "Order Number: ";

    /**
     * Create Order for Pickup and Delivery Order.
     * 
     * @param bus Service bus.
     */
    @Override
    public void arrive(BusIfc bus)
    {

        CustomerIfc customer = null;
        PickupDeliveryOrderCargo pickupDeliveryOrderCargo = (PickupDeliveryOrderCargo)bus.getCargo();
        SaleReturnTransaction saleTransaction = (SaleReturnTransaction)pickupDeliveryOrderCargo.getTransaction();
        if (pickupDeliveryOrderCargo.getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE)
        {
            // if transaction is already order transaction
            bus.mail(CommonLetterIfc.CONTINUE);
        }
        else
        {
            customer = pickupDeliveryOrderCargo.getTransaction().getCustomer();

            OrderTransactionIfc orderTransaction = null;
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            JournalManagerIfc jmgr = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
            RegisterIfc register = pickupDeliveryOrderCargo.getRegister();

            if (customer != null)
            {
                if (customer.getAddresses().size() > 0)
                {
                    ((AddressIfc)customer.getAddresses().elementAt(0)).setAddressType(0);
                }

                orderTransaction = DomainGateway.getFactory().getOrderTransactionInstance();
                orderTransaction.setCashier(pickupDeliveryOrderCargo.getOperator());
                orderTransaction.setSalesAssociate(pickupDeliveryOrderCargo.getSalesAssociate());
                
                // initialize order transaction
                orderTransaction.initializeOrderFromSaleTransaction(saleTransaction);                    
                // Changes ends over here
                
                orderTransaction.setOrderID(register.getNextUniqueID());
                orderTransaction.setUniqueID(register.getCurrentUniqueID());
                orderTransaction.setCustomerInfo(pickupDeliveryOrderCargo.getCustomerInfo());

                pickupDeliveryOrderCargo.setCustomer(customer);
                pickupDeliveryOrderCargo.setOrderTransaction(orderTransaction);
                pickupDeliveryOrderCargo.setTransaction(orderTransaction); // not
                pickupDeliveryOrderCargo.setTransactionType(orderTransaction.getTransactionType());

                // reset non-critical database exception
                pickupDeliveryOrderCargo.setDataExceptionErrorCode(DataException.UNKNOWN);

                // set the customer's name in the status area
                StatusBeanModel statusModel = new StatusBeanModel();
                ui.customerNameChanged(customer.getFirstLastName());
                POSBaseBeanModel baseModel = null;
                baseModel = (POSBaseBeanModel)ui.getModel();

                if (baseModel == null)
                {
                    baseModel = new POSBaseBeanModel();
                }
                baseModel.setStatusBeanModel(statusModel);

                // journal pickup/delivery order creation
                StringBuffer sb = new StringBuffer();
                sb.append(Util.EOL);

                Object dataObject[] = { orderTransaction.getOrderID() };
                String pickupDeliveryOrderHeader = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.PDO_ORDER_HEADER, dataObject);

                String specialOrderNumber = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.SPECIAL_ORDER_NUMBER, dataObject);
                // using same orderno. tag of specialorder
                sb.append(pickupDeliveryOrderHeader).append(Util.EOL).append(Util.EOL).append(specialOrderNumber);
                jmgr.journal(orderTransaction.getCashier().getEmployeeID(), orderTransaction.getTransactionID(), sb
                        .toString());

                bus.mail(CommonLetterIfc.CONTINUE);
            }

        }
    }

    /**
     * Setting Delivery Detail In Order Transaction.
     * 
     * @param bus Service bus.
     */
    @Override
    public void depart(BusIfc bus)
    {

        PickupDeliveryOrderCargo pickupDeliveryOrderCargo = (PickupDeliveryOrderCargo)bus.getCargo();
        OrderTransactionIfc orderTransaction = null;
        KitHeaderLineItemIfc parentKitItem = null;
        KitComponentLineItemIfc childKit = null;
        if (pickupDeliveryOrderCargo.getTransaction() instanceof OrderTransaction)
        {
            orderTransaction = (OrderTransactionIfc)pickupDeliveryOrderCargo.getTransaction();
            pickupDeliveryOrderCargo.setOrderTransaction(orderTransaction);
        }

        if (pickupDeliveryOrderCargo.isDeliveryAction())
        {
            SaleReturnLineItemIfc[] lineItems = pickupDeliveryOrderCargo.getLineItems();
            for (int i = 0; i < lineItems.length; i++)
            {
                int deliveryDetailID = getLatestDeliveryId(orderTransaction);
                deliveryDetailID++;
                lineItems[i].getOrderItemStatus().getDeliveryDetails().setDeliveryDetailID(deliveryDetailID);
                if (lineItems[i].isKitHeader())
                {
                    parentKitItem = (KitHeaderLineItemIfc)lineItems[i];
                    Iterator<KitComponentLineItemIfc> childKitItemIter = parentKitItem.getKitComponentLineItems();
                    while (childKitItemIter.hasNext())
                    {
                        childKit = childKitItemIter.next();
                        childKit.getOrderItemStatus().getDeliveryDetails().setDeliveryDetailID(deliveryDetailID);
                    }

                }
            }

        }
    }

    /**
     * getLatestDeliveryId method returns the latest delivery detailID
     * 
     * @param Order Transaction.
     */
    protected int getLatestDeliveryId(OrderTransactionIfc orderTransaction)
    {
        int latestDeliveryId = 0;
        ArrayList<Integer> listOfDeliveryID = new ArrayList<Integer>();
        AbstractTransactionLineItemIfc[] lineItems = orderTransaction.getLineItems();
        for (int i = 0; i < lineItems.length; i++)
        {
            SaleReturnLineItemIfc item = (SaleReturnLineItemIfc)lineItems[i];
            if (item.getOrderItemStatus().getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_DELIVERY)
            {
                int deliveryID = item.getOrderItemStatus().getDeliveryDetails().getDeliveryDetailID();
                listOfDeliveryID.add(new Integer(deliveryID));
            }
        }
        int id[] = new int[listOfDeliveryID.size()];
        if (listOfDeliveryID.size() > 0)
        {
            Object[] arrOfDeliveryIDs = listOfDeliveryID.toArray();
            for (int i = 0; i < arrOfDeliveryIDs.length; i++)
            {
                Integer deliveryId = (Integer)arrOfDeliveryIDs[i];
                id[i] = deliveryId.intValue();
            }
            Arrays.sort(id);
            latestDeliveryId = id[id.length - 1];
            return latestDeliveryId;

        }
        else
        {
            latestDeliveryId = 0;
            return latestDeliveryId;
        }
    }

}
