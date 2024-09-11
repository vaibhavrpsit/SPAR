/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillreconcile/OfflineWarningSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:20 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:29:11 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:23:45 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:12:49 PM  Robert Pearse   
 *
 * Revision 1.1  2004/06/30 00:21:24  dcobb
 * @scr 5165 - Allowed to reconcile till when database is offline.
 * @scr 5167 - Till Close and Till Reconcile will both be journaled.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.DataManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.DispatcherIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.common.CheckOfflineSite;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

//--------------------------------------------------------------------------
/**
  This site makes sure the data manager is online. 
  @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class OfflineWarningSite extends CheckOfflineSite
{
  /**
      revision number of this class
  **/
  public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

  //----------------------------------------------------------------------
  /**
      Checks with the data manager to make sure the database is online.
      Issues the DATABASE_OFFLINE_WARNING dialog if the database is offline.
      A Success or Failure letter is issued for the Yes/No dialog buttons.
      Otherwise, a Success letter is issued.
      <P>
      @param  bus     Service Bus
  **/
  //----------------------------------------------------------------------
  public void arrive(BusIfc bus)
  {
      // Get the data manager
      DispatcherIfc       d = Gateway.getDispatcher();
      DataManagerIfc     dm = (DataManagerIfc)d.getManager(DataManagerIfc.TYPE);     

      // If the transactions are offline, display an error message
      if (transactionsAreOffline(dm))
      {
          POSUIManagerIfc    ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
         
          String args[] = new String[1];
          args[0] = "";

          String letters[] = new String[2];
          letters[0] = CommonLetterIfc.SUCCESS;
          letters[1] = CommonLetterIfc.FAILURE;

          int buttons[] = new int[2];
          buttons[0] = DialogScreensIfc.BUTTON_YES;
          buttons[1] = DialogScreensIfc.BUTTON_NO;
          
          UIUtilities.setDialogModel(ui, DialogScreensIfc.YES_NO,
                  "DatabaseOfflineWarning", args,
                  buttons, letters);

      }
      // Otherwise go on.
      else
      {
          LetterIfc letter = new Letter(CommonLetterIfc.SUCCESS);
          bus.mail(letter, BusIfc.CURRENT);
      }
  }

}
