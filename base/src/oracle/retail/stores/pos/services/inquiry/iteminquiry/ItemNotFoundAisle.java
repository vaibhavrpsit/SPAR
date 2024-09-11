/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/ItemNotFoundAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:44 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:32 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:29 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:39 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:50:31  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:11  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:00:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Sep 05 2002 15:17:32   jriggins
 * Replaced call to DataException.getErrorCodeString() to the new UtilityManagerIfc.getErrorCodeString().
 * Removed reference to the hardcoded DataManagerMsgIfc.CONTACT in favor of pulling the string from the bundle.
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:22:28   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:33:58   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:29:56   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:08:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

// foundation imports
import oracle.retail.stores.domain.arts.DataManagerMsgIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This aisle is traversed when the item number is not valid.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ItemNotFoundAisle extends LaneActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Displays an error screen.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();

        // error store on the cargo
        int error    = cargo.getDataExceptionErrorCode();

        // message to be display on the dialog screen
        String msg[] = new String[2];

        switch(error)
        {
            case DataException.NO_DATA:
                msg[0] = DataManagerMsgIfc.NO_DATA_ITEM;
                msg[1] = "";
                break;

            default:
                UtilityManagerIfc utility = 
                   (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);            
                msg[0] = utility.getErrorCodeString(error);
                msg[1] = 
                   utility.retrieveDialogText("DATABASE_ERROR.Contact", 
                                              DataManagerMsgIfc.CONTACT);            
                break;
        }

        // initialize bean model
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("INFO_NOT_FOUND_ERROR");
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(msg);

        // display dialog
        POSUIManagerIfc  ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dialogModel);
    }
}
