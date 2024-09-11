/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  4/6/2013               Izhar                                       MAX-POS-Customer-FES_v1.2.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.customer.lookup;

import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.lookup.CustomerLookupFailedSite;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
//--------------------------------------------------------------------------
/**
    Determines how to handle a database error when looking up a customer.
    $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MAXCustomerLookupFailedSite extends CustomerLookupFailedSite
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: 3$";

    //----------------------------------------------------------------------
    /**
        Determines how to handle a database error when looking up a customer.
        <p>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        int errorCode = cargo.getDataExceptionErrorCode();
        int offlineCode = cargo.getOfflineIndicator();
        DialogBeanModel dialogModel = new DialogBeanModel();
        POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        String args[] = new String[1];
        UtilityManagerIfc utility = 
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        args[0] = utility.getErrorCodeString(errorCode);

        if (errorCode == DataException.NO_DATA)
        {
           UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR, "INFO_NOT_FOUND_ERROR",null,
                                      CommonLetterIfc.RETRY);
        }
        else if (errorCode == DataException.CONNECTION_ERROR || offlineCode == CustomerCargo.OFFLINE_ADD )
        {
            int buttons[] = new int[2];
            String letters[] = new String[2];
            buttons[0] = DialogScreensIfc.BUTTON_YES;
            buttons[1] = DialogScreensIfc.BUTTON_NO;
            switch (offlineCode)
            {
            //MAX Rev 1.0 Change : Start 
              case CustomerCargo.OFFLINE_EXIT:
                 UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR,"DeleteCustOffline",
                                            args, CommonLetterIfc.OFFLINE);
                 //MAX Rev 1.0 Change : end 
                 break;
              case CustomerCargo.OFFLINE_LINK:
                 letters[0] = CommonLetterIfc.LINK;
                 letters[1] = CommonLetterIfc.OFFLINE;

                 UIUtilities.setDialogModel(ui, DialogScreensIfc.CONFIRMATION, "LinkCustOffline", null, buttons,letters);
                 break;
              case  CustomerCargo.OFFLINE_ADD:
                  // build the dialog screen
                  letters[0] = CommonLetterIfc.ADD;
                  letters[1] = CommonLetterIfc.CANCEL;
                  UIUtilities.setDialogModel(ui, DialogScreensIfc.CONFIRMATION,
                                             "DatabaseErrorFind", args,
                                             buttons,letters);

                 //or the message could be  "AddCustoffline" no args
                 break;
             default:
             {
                 // build the dialog screen
                 UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR, "DatabaseError", args,
                                            CommonLetterIfc.RETRY);
             }
           }
        }
        else    // generic database error
        {
                 UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR, "DatabaseError", args,
                                            CommonLetterIfc.CANCEL);
        }
    }


}
