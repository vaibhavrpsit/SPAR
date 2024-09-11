/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/CheckForQueueFullSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:01 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    masahu    09/29/10 - New Check for queue full in sale tour
 *    masahu    09/29/10 - New check in sale Tour for Queue Full
 *    masahu    09/29/10 - New Site to check for Transaction Queue Full
 *
 * ===========================================================================
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.DataManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataManager;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    Check to see if the register can write to the transaction queue.
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CheckForQueueFullSite extends PosSiteActionAdapter
{
    /**
     revision number
     **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
     * Check to see if register can write to the transaction queue.
     * <P>
     *
     * @param bus
     *            Service Bus
     */
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        DataManagerIfc ifc = (DataManagerIfc) bus.getManager(DataManagerIfc.TYPE);

        DataManager dataManager = (DataManager) ifc;

        Letter letter = null;

        if(dataManager.isQueueFull())
        {
          DialogBeanModel dialogModel = utility.createErrorDialogBeanModel(new DataException(DataException.QUEUE_FULL_ERROR));

          POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
          // display dialog
          ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dialogModel);
        }
        else
        {
          letter = new Letter(CommonLetterIfc.CONTINUE);
          bus.mail(letter, BusIfc.CURRENT);
        }
    }
}
