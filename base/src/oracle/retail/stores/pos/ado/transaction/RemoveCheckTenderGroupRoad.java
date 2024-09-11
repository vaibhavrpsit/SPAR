/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header:
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rrkohli   10/14/11 - fix for 'Cash Change More than Max dialog does not
 *                         display when cash change more than max previously
 *                         attempted for Check tender'
 *    rrkohli   10/14/11 - added to remove tender check group
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.ado.transaction;

import java.util.Map;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc;
import oracle.retail.stores.pos.ado.transaction.AbstractRetailTransactionADO;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;

/**
 * Road to remove Check Tender Group when user presses "no" on Price Override
 * screen.
 */

public class RemoveCheckTenderGroupRoad extends PosLaneActionAdapter
{

  private static final long serialVersionUID = 1L;

  public void traverse(BusIfc bus)
  {
    TenderCargo cargo = (TenderCargo) bus.getCargo();
    AbstractRetailTransactionADO ado = (AbstractRetailTransactionADO) cargo.getCurrentTransactionADO();
    Map<TenderTypeEnum, TenderGroupADOIfc> tenderGroupMap = ado.tenderGroupMap;
    if (tenderGroupMap != null)
    {
      tenderGroupMap.remove(TenderTypeEnum.CHECK);
    }

  }

}
