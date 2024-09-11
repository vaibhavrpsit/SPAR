/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tilloptions/UpdateTillStatusRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:19 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:30:41 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:26:37 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:15:26 PM  Robert Pearse   
 *
 * Revision 1.1  2004/06/30 18:18:00  dcobb
 * @scr 5167 - Till Close and Till Reconcile will both be journaled.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tilloptions;

import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
//------------------------------------------------------------------------------
/**
    Updates the register status when escaping or canceling out ot Till Reconcile 
    since the till might have been successfully closed.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class UpdateTillStatusRoad extends PosLaneActionAdapter 
{
  /**
     revision number supplied by Team Connection
  **/
  public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

  //--------------------------------------------------------------------------
  /**
     Updates the till status when the till has been successfully closed.
     <P>
     @param bus the bus traversing this lane
  **/
  //--------------------------------------------------------------------------
  public void traverse(BusIfc bus)
  {

      TillOptionsCargo cargo = (TillOptionsCargo) bus.getCargo();

      // Succeeded in collecting count/till amounts so use tillclose register
      TillIfc[] tills = cargo.getTillCloseRegister().getTills();
      if (tills != null)
      {
          for (int i = 0; i < tills.length; i++)
          {
              int status = tills[i].getStatus();
              if (status != tills[i].getPreviousStatus())
              {
                  if (status == AbstractFinancialEntityIfc.STATUS_CLOSED)
                  {
                      String tillID = tills[i].getTillID();
                      TillIfc till = cargo.getRegister().getTillByID(tillID);
                      till.setStatus(status);
                      till.setCloseTime(tills[i].getCloseTime());
                      till.setSignOffOperator(tills[i].getSignOffOperator());
                  }
              }
          }
      }

  }

  //--------------------------------------------------------------------------
  /**


     @param bus the bus traversing this lane
  **/
  //--------------------------------------------------------------------------
  public void backup(BusIfc bus)
  {


  }
}
