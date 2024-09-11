/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/processorder/SellOrderItemLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:59 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    06/03/10 - refresh to tip
 *    acadar    06/03/10 - fix compilation error
 *    sgu       06/01/10 - check in after merge
 *    sgu       06/01/10 - check in order sell item flow
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    05/21/10 - additional changes for process order flow
 *    acadar    05/14/10 - initial version for external order processing
 *    acadar    05/14/10 - initial version
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.externalorder.processorder;

import oracle.retail.stores.pos.services.externalorder.sellorderitem.SellOrderItemCargo;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

/**
 * Shuttle from process order service to order sell item service
 * @author acadar
 *
 */
public class SellOrderItemLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 9133321825845723318L;


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
     *  Unloads the cargo into the order sell item service
     */
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        SellOrderItemCargo cargo = (SellOrderItemCargo) bus.getCargo();


        cargo.setRegister(processOrderCargo.getRegister());

        cargo.setPLUItemID(processOrderCargo.getCurrentExternalOrderItem().getPOSItemId());
        cargo.setPLUItem(processOrderCargo.getCurrentExternalOrderItem().getPLUItem());
        cargo.setTransaction(processOrderCargo.getTransaction());
        cargo.setStoreStatus(processOrderCargo.getStoreStatus());
        cargo.setOperator(processOrderCargo.getOperator());
        cargo.setCustomerInfo(processOrderCargo.getCustomerInfo());
        cargo.setTenderLimits(processOrderCargo.getTenderLimits());
        cargo.setItemList(processOrderCargo.getCurrentExternalOrderItem().getPLUItems());
        cargo.setSalesAssociate(processOrderCargo.getSalesAssociate());
        cargo.setExternalOrder(processOrderCargo.getExternalOrder());
        cargo.setExternalOrderItem(processOrderCargo.getCurrentExternalOrderItem());
        cargo.setPasswordRequired(processOrderCargo.isPasswordRequired());
    }



}
