/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/specialorder/add/LinkCustomerReturnShuttle.java /main/20 2012/09/12 11:57:20 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   08/28/12 - Merge project Echo (MPOS) into trunk.
 *    sgu       06/22/12 - refactor order id assignment
 *    sgu       06/20/12 - refactor get order id
 *    sgu       06/18/12 - refactor ORPOS to call order manager to get new
 *                         order id
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    blarsen   02/06/12 - Moving GENERATE_SEQUENCE_NUMBER into
 *                         UtilityManagerIfc.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    acadar    03/19/09 - do not check for Connection Exception because a new
 *                         customer is always saved either to the database or
 *                         to the queue
 *    mahising  01/19/09 - fix special order issue
 *    vchengeg  12/08/08 - EJ I18n formatting
 *
 * ===========================================================================
 * $Log:
 *  4    360Commerce 1.3         4/4/2008 10:45:10 AM   Christian Greene 31178
 *       setInitialTransactionBusinessDate(date) to the actual
 *       orderTransaction.getBusinessDay() instead of a new EYSDate instance
 *       so that data purge can find the orders properly.
 *  3    360Commerce 1.2         3/31/2005 4:28:51 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:23:07 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:12:19 PM  Robert Pearse
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.specialorder.add;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.manager.order.OrderManagerIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderStatusIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;
import oracle.retail.stores.pos.services.specialorder.SpecialOrderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import org.apache.log4j.Logger;

//--------------------------------------------------------------------------
/**
    This shuttle updates the parent cargo with information from the child cargo.
    <P>
**/
//--------------------------------------------------------------------------
public class LinkCustomerReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3725596339456983794L;

    /**
        revision number
    **/
    public static String revisionNumber = "$Revision: /main/20 $";
    /**
        customer main cargo
    **/
    protected CustomerMainCargo customerMainCargo = null;
    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.specialorder.add.LinkCustomerReturnShuttle.class);

    //----------------------------------------------------------------------
    /**
        Copies information needed from child service (customer) to the
        calling service (special order add).
        <P>
        @param  bus    Child Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // retrieve cargo from the customer service
        customerMainCargo = (CustomerMainCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
        Stores information needed by parent service. If a customer is successfully
        returned, then creates a special order transaction and links this customer.
        <P>
        @param  bus     Parent Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void unload(BusIfc bus)
    {
        CustomerIfc customer = customerMainCargo.getCustomer();

        // retrieve cargo from the parent
        SpecialOrderCargo specialOrderCargo = (SpecialOrderCargo)bus.getCargo();
        OrderTransactionIfc orderTransaction = null;
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        RegisterIfc register = specialOrderCargo.getRegister();
        JournalManagerIfc jmgr = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        specialOrderCargo.setDataExceptionErrorCode(customerMainCargo.getDataExceptionErrorCode());

        OrderManagerIfc orderMgr =
            (OrderManagerIfc)bus.getManager(OrderManagerIfc.TYPE);

        // determines if customer service linked the customer
        if (customerMainCargo.isLink() && customer != null)
        {
            // Create special order transaction; the initializeTransaction() method is called
            // in UtilityManagerIfc  set cashier and sales associate.
            orderTransaction = DomainGateway.getFactory().getOrderTransactionInstance();
            orderTransaction.setCashier(specialOrderCargo.getOperator());
            orderTransaction.setSalesAssociate(customerMainCargo.getSalesAssociate());
            OrderStatusIfc orderStatus = orderTransaction.getOrderStatus();

            TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc) bus.getManager(TransactionUtilityManagerIfc.TYPE);
            utility.initializeTransaction(orderTransaction,
                    TransactionUtilityManagerIfc.GENERATE_SEQUENCE_NUMBER,
                    customer.getCustomerID());
            CustomerUtilities.journalCustomerExit(bus, customerMainCargo.getOperator().getEmployeeID(),
                    orderTransaction.getTransactionID());

            orderStatus.initializeStatus();
            orderStatus.setInitiatingChannel(OrderConstantsIfc.ORDER_CHANNEL_STORE);
            orderStatus.setTimestampBegin();
            orderStatus.setTimestampCreated();
            orderStatus.setInitialTransactionBusinessDate(orderTransaction.getBusinessDay());
            orderStatus.setInitialTransactionID(orderTransaction.getTransactionIdentifier());
            orderStatus.setRecordingTransactionID(orderTransaction.getTransactionIdentifier());
            orderStatus.setRecordingTransactionBusinessDate(orderTransaction.getBusinessDay());
            orderTransaction.setTransactionType(TransactionIfc.TYPE_ORDER_INITIATE);
            orderTransaction.linkCustomer(customer);
            orderTransaction.assignNewOrderID(orderMgr.getNewOrderID(register), false);
            orderTransaction.setUniqueID(register.getCurrentUniqueID());
            orderTransaction.setCustomerInfo(specialOrderCargo.getCustomerInfo());
            orderTransaction.setOrderType(OrderConstantsIfc.ORDER_TYPE_SPECIAL);//added by Nilesh to set Order type for SP.order

            specialOrderCargo.setCustomer(customer);
            specialOrderCargo.setOrderTransaction(orderTransaction);

            // reset non-critical database exception
            specialOrderCargo.setDataExceptionErrorCode(DataException.UNKNOWN);

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

            // journal special order creation
            StringBuffer sb = new StringBuffer();
            sb.append(Util.EOL+Util.EOL);
            String specialOrderHeader = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SPECIAL_ORDER_HEADER, null);

            Object dataObject[]={orderTransaction.getOrderID()};


            String specialOrderNumber = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SPECIAL_ORDER_NUMBER, dataObject);

            sb.append(specialOrderHeader)
            .append(Util.EOL)
            .append(Util.EOL)
            .append(specialOrderNumber)
            .append(Util.EOL);

            jmgr.journal(orderTransaction.getCashier().getEmployeeID(),
                    orderTransaction.getTransactionID(), sb.toString());
        }
    } // end unload
}
