/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/common/LookupStoreStatusErrorAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:16 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    sgu       02/12/10 - exteranlize initialization error
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.common;

//    foundation imports
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
     Aisle to traverse if there is a parameter error preventing a store-status lookup. <P>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
     instead.

**/
//--------------------------------------------------------------------------
public class LookupStoreStatusErrorAisle extends PosLaneActionAdapter
{
     /**
         revision number supplied by Team Connection
     **/
     public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

     //----------------------------------------------------------------------
     /**
         Display an error message, wait for user acknowlegement. <P>
         <B>Pre-Condition(s)</B>
         <UL>
         <LI>none
         </UL>
         <B>Post-Condition(s)</B>
         <UL>
         <LI>none
         </UL>
         @param  bus     Service Bus
     **/
     //----------------------------------------------------------------------
     public void traverse(BusIfc bus)
     {
          // get ui handle
          POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
          UtilityManagerIfc utilityMgr = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

          // set bean model
          String arg = "";
          AbstractFinancialCargo cargo = (AbstractFinancialCargo)bus.getCargo();
          StoreStatusIfc storeStatus = cargo.getStoreStatus();
          if(storeStatus.getBusinessDate() == null)
          {
        	  arg = utilityMgr.retrieveDialogText("InitializationFailure.keyBusinessDate", "Business date is missing.");
          }
          else if(Util.isEmpty(storeStatus.getStore().getGeoCode()))
          {
              arg = utilityMgr.retrieveDialogText("InitializationFailure.keyGeoCode", "Geo code is missing.");
          }
          String args[] = { arg };
          DialogBeanModel model = new DialogBeanModel();
          model.setResourceID("InitializationFailure");
          model.setArgs(args);
          model.setType(DialogScreensIfc.ERROR);
          ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);

          // display dialog
          ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);

     }

     //----------------------------------------------------------------------
     /**
         Returns the revision number of the class. <P>
         @return String representation of revision number
     **/
     //----------------------------------------------------------------------
     public String getRevisionNumber()
     {                                   // begin getRevisionNumber()
          // return string
          return(revisionNumber);
     }                                   // end getRevisionNumber()

}
