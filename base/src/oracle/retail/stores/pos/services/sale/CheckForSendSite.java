/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/CheckForSendSite.java /main/12 2012/06/29 11:53:42 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   06/29/12 - Add dialog for deleting ship item, disable change
 *                         price for ship item
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:25 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:08 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:56 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/06/17 19:12:07  lzhao
 *   @scr 4670: add dialog for deleting multiple sends.
 *
 *   Revision 1.2  2004/06/09 19:45:13  lzhao
 *   @scr 4670: add customer present dialog and the flow.
 *
 *   Revision 1.1  2004/06/02 19:06:51  lzhao
 *   @scr 4670: add ability to delete send items, modify shipping and display shipping method.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import oracle.retail.stores.pos.appmanager.ManagerException;
import oracle.retail.stores.pos.appmanager.ManagerFactory;
import oracle.retail.stores.pos.appmanager.send.SendException;
import oracle.retail.stores.pos.appmanager.send.SendManager;
import oracle.retail.stores.pos.appmanager.send.SendManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;

//--------------------------------------------------------------------------
/**
    Check to see if user is trying to delete items from multiple send 
    packages. If so, show error dialog screen. if not, call delete send
    road.
    $Revision: /main/12 $
**/
//--------------------------------------------------------------------------
public class CheckForSendSite extends PosSiteActionAdapter
{
    /**
     revision number
     **/
    public static final String revisionNumber = "$Revision: /main/12 $";
    /**
     * update shipping charge letter
     */
    protected static final String UPDATE_SHIPPING_CHARGE = "UpdateShippingCharge";
    /**
     error invalid delete (invalid send modification)
     **/
    public static final String INVALID_SEND_MODIFICATION = "InvalidSendModification";
    
    //----------------------------------------------------------------------
    /**
     * Check to see if user is trying to delete items from multiple send 
     * packages. If so, show error dialog screen. if not, call update 
     * shipping charge.
     * <P>
     * 
     * @param bus
     *            Service Bus
     */
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        
        SaleCargo cargo = (SaleCargo) bus.getCargo();
        
        LineItemsModel beanModel                 = (LineItemsModel)ui.getModel(POSUIManagerIfc.SELL_ITEM);
        int[] allSelected                        = beanModel.getRowsToDelete();
        
        SendManagerIfc sendMgr = null;
        try
        {
            sendMgr = (SendManagerIfc)ManagerFactory.create(SendManagerIfc.MANAGER_NAME);
        }
        catch (ManagerException e)
        {
            // default to product version
            sendMgr = new SendManager();
        }
        try
        {
            sendMgr.checkItemsFromMultipleSends(cargo, allSelected);
        }
        catch (SendException e)
        {
            if (e.getErrorType() == SendException.MULTIPLE_SENDS)
            {
                //if trying to delete items from more than one send, the system
                // will return to sale item screen without delete any item.
                UIUtilities.setDialogModel(ui, 
                    DialogScreensIfc.ERROR, 
                    INVALID_SEND_MODIFICATION, 
                    null, 
                    CommonLetterIfc.FAILURE);
                return;                
            }
        }
        
        ui.setModel(POSUIManagerIfc.SELL_ITEM, beanModel);
        
        // update shipping charge if there is a send item
        Letter letter = new Letter(UPDATE_SHIPPING_CHARGE);
        
        bus.mail(letter, BusIfc.CURRENT);                
    }
}
