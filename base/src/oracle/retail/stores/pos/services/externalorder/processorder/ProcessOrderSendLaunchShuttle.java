/* ===========================================================================
* Copyright (c) 2010, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/processorder/ProcessOrderSendLaunchShuttle.java /main/8 2012/03/26 13:08:26 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   03/21/12 - update CustomerIfc to use collections generics (i.e.
 *                         List<AddressIfc>) and remove old deprecated methods
 *                         and references to them
 *    sgu       06/22/10 - added the logic to process multiple send package
 *                         instead of just on per order
 *    sgu       06/21/10 - added site declaration
 *    acadar    06/02/10 - refactoring
 *    acadar    06/02/10 - signature capture changes
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    05/21/10 - renamed from _externalorder to externalorder
 *    acadar    05/17/10 - temporarily rename the package
 *    acadar    05/14/10 - initial version for external order processing
 *    acadar    05/14/10 - initial version
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.externalorder.processorder;

import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.commerceservices.externalorder.ExternalOrderIfc;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderItemIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.externalorder.ExternalOrderSendPackageItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.externalorder.processordersend.ProcessOrderSendCargo;

/**
 * Shuttle from process order service to process order send
 * @author acadar
 *
 */
public class ProcessOrderSendLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 5112227046215554114L;

    /**
     * Cargo for the process order service
     */
    protected ProcessOrderCargo processOrderCargo = null;

   /**
    * Loads the cargo
    */
    public void load(BusIfc bus)
    {
        super.load(bus);
        processOrderCargo = (ProcessOrderCargo) bus.getCargo();
    }

    /**
     *  Unloads the cargo into the process order send service
     */
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        ProcessOrderSendCargo cargo = (ProcessOrderSendCargo) bus.getCargo();
        SaleReturnTransactionIfc transaction = processOrderCargo.getTransaction();
        ExternalOrderIfc externalOrder = processOrderCargo.getExternalOrder();
        ExternalOrderSendPackageItemIfc sendPackage = processOrderCargo.getCurrentExternalOrderSendPackage();
        List<ExternalOrderItemIfc> externalOrderSendItems = externalOrder.getItemListForSendPakcage(sendPackage.getExternalOrderSendPackageId());
        ArrayList<String> externalOrderItemIDs = new ArrayList<String>();
        for (ExternalOrderItemIfc externalOrderItem : externalOrderSendItems)
        {
        	externalOrderItemIDs.add(externalOrderItem.getId());
        }
        List<SaleReturnLineItemIfc> externalOrderLineItems = transaction.getExternalOrderLineItems(externalOrderItemIDs);
        CustomerIfc customer = sendPackage.getCustomer();
        String postalCode = customer.getAddressList().get(0).getPostalCode();

        cargo.setTransaction(processOrderCargo.getTransaction());
        cargo.setSaleReturnSendLineItems(externalOrderLineItems);
        cargo.setShippingMethod(sendPackage.getShippingMethod());
        cargo.setShippingPostalCode(postalCode);
        cargo.setShipToCustomer(customer);
    }



}
