/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
Rev 1.0   Rahul		30/April/2014	initial Draft for  stop Till Reconcilation at offline mode
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.dailyoperations.till.tillreconcile;

import oracle.retail.stores.foundation.manager.ifc.DataManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.DispatcherIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.common.CheckOfflineSite;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
  This site makes sure the data manager is online. 
  @version $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MAXOfflineWarningSite extends CheckOfflineSite
{
  /**
      revision number of this class
  **/
  public static final String revisionNumber = "$Revision: 3$";

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
  		DialogBeanModel dialogModel = new DialogBeanModel();
		dialogModel.setResourceID("DBOfflineWarning");
  		dialogModel.setType(DialogScreensIfc.ERROR);
		dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
      }
      // Otherwise go on.
      else
      {
          LetterIfc letter = new Letter(CommonLetterIfc.SUCCESS);
          bus.mail(letter, BusIfc.CURRENT);
      }
  }

}
