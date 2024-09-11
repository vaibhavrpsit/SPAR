/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/ValidateItemsActionSite.java /rgbustores_13.4x_generic_branch/2 2011/05/19 10:07:27 rsnayak Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rsnayak   05/12/11 - APF send changes removed the check for customer physically present 
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:42 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:41 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:29 PM  Robert Pearse   
 *
 *   Revision 1.18  2004/08/27 14:30:09  rsachdeva
 *   @scr 6791 Item Level Send  to Transaction Level Send Update Flow
 *
 *   Revision 1.17  2004/08/26 22:21:14  rsachdeva
 *   @scr 6791 Transaction Level Send
 *
 *   Revision 1.16  2004/08/10 16:36:15  rsachdeva
 *   @scr 6791 Clean Up: checkedCustomerPresent put seperately from the try-catch block of maximum sends check
 *
 *   Revision 1.15  2004/08/10 16:25:29  rsachdeva
 *   @scr 6791 Send Level In Progress
 *
 *   Revision 1.14  2004/08/10 15:57:25  rsachdeva
 *   @scr 6791 Send Level In Progress
 *
 *   Revision 1.13  2004/06/21 13:13:55  lzhao
 *   @scr 4670: cleanup
 *
 *   Revision 1.12  2004/06/16 13:42:07  lzhao
 *   @scr 4670: refactoring Send for 7.0.
 *
 *   Revision 1.11  2004/06/11 19:10:35  lzhao
 *   @scr 4670: add customer present feature
 *
 *   Revision 1.10  2004/06/09 19:45:14  lzhao
 *   @scr 4670: add customer present dialog and the flow.
 *
 *   Revision 1.9  2004/06/04 20:23:44  lzhao
 *   @scr 4670: add Change send functionality.
 *
 *   Revision 1.8  2004/06/02 19:06:51  lzhao
 *   @scr 4670: add ability to delete send items, modify shipping and display shipping method.
 *
 *   Revision 1.7  2004/05/26 16:37:47  lzhao
 *   @scr 4670: add capture customer and bill addr. same as shipping for send
 *
 *   Revision 1.6  2004/05/06 20:16:27  rsachdeva
 *   @scr 4670 Send: Multiple Sends At One Time Selected
 *
 *   Revision 1.5  2004/05/06 15:15:09  rsachdeva
 *   @scr 4670 Send: Maximum Sends Allowed
 *
 *   Revision 1.4  2004/02/20 15:33:25  epd
 *   @scr 0 Renamed local variable
 *
 *   Revision 1.3  2004/02/13 21:10:51  epd
 *   @scr 0
 *   Refactoring to the Send Application Manager
 *
 *   Revision 1.2  2004/02/12 21:54:19  epd
 *   @scr 0
 *   Refactors for Send tour
 *
 *   Revision 1.1  2004/02/12 21:36:28  epd
 *   @scr 0
 *   These files comprise all new/modified files that make up the refactored send service
 *
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.send;

import java.util.Arrays;

import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.appmanager.ManagerException;
import oracle.retail.stores.pos.appmanager.ManagerFactory;
import oracle.retail.stores.pos.appmanager.send.SendException;
import oracle.retail.stores.pos.appmanager.send.SendManager;
import oracle.retail.stores.pos.appmanager.send.SendManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

import org.apache.log4j.Logger;

//--------------------------------------------------------------------------
/**
   The purpose of this site is to check for maximum sends allowed and 
   make use of service to verify items are  sendable.  
   A dialog is displayed showing up to the first 3 items not
   sendable. It checks for items attached to different sends also.
   $Revision: /rgbustores_13.4x_generic_branch/2 $
**/
//--------------------------------------------------------------------------
public class ValidateItemsActionSite extends PosSiteActionAdapter
{
    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";
    /**
       revision number of this class
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.send.ValidateItemsActionSite.class);
    /**
       send not allowed resource id
    **/
    public static final String SEND_NOT_ALLOWED = "SendNotAllowed";
    /**
       maximum sends allowed  parameter
    **/
    public static final String MAXIMUM_SENDS_ALLOWED = "MaximumSendsAllowed";
    /**
     error multiple sends resource id
     **/
    public static final String CUSTOMER_PRESENT = "CustomerPresent";
    
    //----------------------------------------------------------------------
    /**
        Checks the maximum sends and items allowed for send<P>
        @param  bus  Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        ItemCargo cargo = (ItemCargo)bus.getCargo();
        
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        if(!cargo.isTransactionLevelSendInProgress())
        {
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
                Integer maximumSends = pm.getIntegerValue(MAXIMUM_SENDS_ALLOWED);
                sendMgr.checkForMaximumSends(maximumSends.intValue(), cargo);
                sendMgr.validateItemsForSend(cargo);
            }      
            catch(ParameterException pe)
            {
                logger.warn(pe.getStackTraceAsString());
            }
            catch (SendException e)
            {
                if (e.getErrorType() == SendException.MAXIMUM_SENDS)
                {
                    UIUtilities.setDialogModel(ui, 
                            DialogScreensIfc.ERROR, 
                            SEND_NOT_ALLOWED, 
                            null, 
                            CommonLetterIfc.FAILURE);
                    return;
                }
                if (e.getErrorType() == SendException.CANNOT_SEND)
                {
                    displayErrorDialog(e.getInvalidItemIDs(), bus);
                    return;
                }            
                
            }
        }
       
        bus.mail(CommonLetterIfc.CONTINUE, BusIfc.CURRENT);
    }
    
    //----------------------------------------------------------------------

    public void depart(BusIfc bus)
    {
        
    }
    
   //----------------------------------------------------------------------
    /**
       Displays Error Dialog  to tell which items are not sendable<P>
       @param  invalidIDs invalid ids
       @param  bus  Service Bus
    **/
    //----------------------------------------------------------------------    
    protected void displayErrorDialog(String[] invalidIDs, BusIfc bus)
    {
        // If there is any part of selected items are detected as not sendable items,
        // display the error screen to tell which items are not sendable.
        String[] args = new String[3];
        Arrays.fill(args, "");
        for (int i = 0; i < invalidIDs.length && i < 3; i++)
        {
            args[i] = invalidIDs[i];
        }
        
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("CANNOT_SEND_ERROR");
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(args);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);

        //display dialog
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
}    
