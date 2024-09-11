/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/VerifySendSelectionSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:03 mszekely Exp $
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
 *    4    360Commerce 1.3         11/2/2006 10:43:32 AM  Keith L. Lesikar
 *         OracleCustomer parameter update.
 *    3    360Commerce 1.2         3/31/2005 4:30:44 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:45 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:32 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/07 22:24:10  rsachdeva
 *   @scr Transaction Level Send removed else since return; was being already used in if statement
 *
 *   Revision 1.5  2004/08/10 16:14:59  rsachdeva
 *   @scr 6791 Check Items present
 *
 *   Revision 1.4  2004/06/19 14:06:40  lzhao
 *   @scr 4670: Integrate with capture customer
 *
 *   Revision 1.3  2004/06/15 14:35:30  rsachdeva
 *   @scr 4670 Send: Multiple Sends Fixed so that Multiple Sends Selected Dialog Check is done first
 *
 *   Revision 1.2  2004/06/14 23:35:26  lzhao
 *   @scr 4670: fix shipping charge calculation.
 *
 *   Revision 1.1  2004/06/09 19:45:14  lzhao
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

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.appmanager.ManagerException;
import oracle.retail.stores.pos.appmanager.ManagerFactory;
import oracle.retail.stores.pos.appmanager.send.SendManagerIfc;
import oracle.retail.stores.pos.appmanager.send.SendException;
import oracle.retail.stores.pos.appmanager.send.SendManager;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
   The purpose of this site is to check for maximum sends allowed and 
   make use of service to verify items are  sendable.  
   A dialog is displayed showing up to the first 3 items not
   sendable. It checks for items attached to different sends also.
   $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class VerifySendSelectionSite extends PosSiteActionAdapter
{
    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       revision number of this class
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.send.VerifySendSelectionSite.class);
    /**
     already send resource id
     **/
    public static final String SEND_ALREADY_APPLIED = "SendAlreadyApplied";
    /**
       error multiple sends resource id
    **/
    public static final String ERROR_MULTIPLE_SENDS = "ErrorMultipleSends";
    /**
      change letter for  change button in the dialog box
     */
    public static final String UPDATE_LETTER = "Update";    
    
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

        if (cargo.getItems() != null && cargo.getItems().length > 0)
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
                sendMgr.checkItemsFromMultipleSends(cargo);
                sendMgr.checkItemAlreadyInSend(cargo);
            }
            catch (SendException e)
            {
                if (e.getErrorType() == SendException.ALREADY_SEND)
                {
                    int[] buttons = new int[] { DialogScreensIfc.BUTTON_UPDATE,
                                                DialogScreensIfc.BUTTON_CANCEL };
                    String[] letters = new String[] { UPDATE_LETTER,
                                                      CommonLetterIfc.CANCEL };
                    UIUtilities.setDialogModel(ui, 
                                               DialogScreensIfc.UPDATE_CANCEL, 
                                               SEND_ALREADY_APPLIED, 
                                               null,
                                               buttons, 
                                               letters );
                    return;    
                }
                if (e.getErrorType() == SendException.MULTIPLE_SENDS)
                {
                    UIUtilities.setDialogModel(ui, 
                                               DialogScreensIfc.ERROR, 
                                               ERROR_MULTIPLE_SENDS, 
                                               null, 
                                               CommonLetterIfc.FAILURE);
                    return;    
                }
                
            }
        }
        
        // set billing customer in cargo
        try
        {
            Boolean is360Customer = pm.getBooleanValue("OracleCustomer");
            SaleReturnTransaction transaction = (SaleReturnTransaction)cargo.getTransaction();
            if ( !is360Customer.booleanValue() )
            {  
                //it is capture customer
                transaction.setSendCustomerLinked(false);
                if ( transaction.getCaptureCustomer() == null ) 
                {
                    
                    //does not have captured customer info
                    bus.mail( new Letter("CaptureCustomerInfo"), BusIfc.CURRENT);
                    return;
                }
                cargo.setCustomer( transaction.getCaptureCustomer() );
            }
            else
            {
                // it is link customer
                transaction.setSendCustomerLinked(true);
                cargo.setCustomer( transaction.getCaptureCustomer() );
            }
        }
        catch(ParameterException pe)
        {
            logger.warn(pe.getStackTraceAsString());
        }
            
        bus.mail(CommonLetterIfc.CONTINUE, BusIfc.CURRENT);
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
