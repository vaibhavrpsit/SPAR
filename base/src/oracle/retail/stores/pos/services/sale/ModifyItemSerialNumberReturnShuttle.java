/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/ModifyItemSerialNumberReturnShuttle.java /main/8 2013/05/10 10:57:59 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   05/10/13 - Avoid PickupShipDialog display again if pick or
 *                         shipping is already selected. It happens when a
 *                         serialized item entered.
 *    sgu       12/27/12 - set presplit index and item selected flags
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *    nkgautam  11/18/09 - Return Shuttle class for serializedItem tour
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.modifyitem.serialnumber.SerializedItemCargo;

/**
 * Return Shuttle class for serialized Item tour
 * @author nkgautam
 *
 */
public class ModifyItemSerialNumberReturnShuttle extends FinancialCargoShuttle implements ShuttleIfc
{

  /**
   * Serialized Item Cargo
   */
  protected SerializedItemCargo serializedCargo;


  /**
   * Loads the Sale Cargo.
   * @param bus Service Bus to copy cargo from.
   */
  public void load(BusIfc bus)
  {
    serializedCargo = (SerializedItemCargo)bus.getCargo();

  }

  /**
   * Copies the SerializedItemCargo contents info to the Salecargo.
   * @param  bus     Service Bus to copy cargo to.
   */
  public void unload(BusIfc bus)
  {
    SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();

    if (serializedCargo.getTransaction() != null)
    {
        SaleReturnTransactionIfc transaction = (SaleReturnTransactionIfc)serializedCargo.getTransaction();
        cargo.setTransaction(transaction);
        
        // reset the selected for modification flag
        for (AbstractTransactionLineItemIfc lineItem : transaction.getLineItems())
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) lineItem;
            srli.setSelectedForItemModification(false);
        }
        
    }
    
    cargo.setPickupOrDeliveryExecuted(serializedCargo.isPickupOrDeliveryExecuted());
  }


}
