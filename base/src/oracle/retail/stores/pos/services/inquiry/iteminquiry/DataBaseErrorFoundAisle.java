/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/DataBaseErrorFoundAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:44 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:40 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:45 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:26 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:50:30  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:10  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:00:06   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Sep 05 2002 09:09:06   jriggins
 * Replaced call to DataException.getErrorCodeString() to the new UtilityManagerIfc.getErrorCodeString().
 * 
 * Removed reference to the hardcoded DataManagerMsgIfc.CONTACT in favor of pulling the string from the bundle.
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:22:10   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:33:42   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:29:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:08:14   msg
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
public class DataBaseErrorFoundAisle extends LaneActionAdapter
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
        // error store on cargo
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        int error       = cargo.getDataExceptionErrorCode();

        DialogBeanModel dialogModel = new DialogBeanModel();
        switch(error)
        {
            case DataException.NO_DATA:
            {
                dialogModel.setResourceID("INFO_NOT_FOUND_ERROR");
                break;
            }
            case DataException.DATA_FORMAT:
            {
                dialogModel.setResourceID("INQUIRY_OFFLINE");
                break;
            }
            default:
            {
                dialogModel.setResourceID("DATABASE_ERROR");
                String msg[] = new String[2];
                UtilityManagerIfc utility = 
                   (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);                
                msg[0] = utility.getErrorCodeString(error);
                msg[1] = 
                   utility.retrieveDialogText("DATABASE_ERROR.Contact", 
                                               DataManagerMsgIfc.CONTACT);                         
                dialogModel.setArgs(msg);
                break;
            }
        }
        dialogModel.setType(DialogScreensIfc.ERROR);

        // display dialog
        POSUIManagerIfc  ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dialogModel);
    }
}
