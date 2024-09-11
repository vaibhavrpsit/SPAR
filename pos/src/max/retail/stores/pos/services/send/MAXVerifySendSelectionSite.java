/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *  Rev 1.0		May 04, 2017		Kritica Agarwal 	GST Changes
 *
 ********************************************************************************/
package max.retail.stores.pos.services.send;

import java.util.Arrays;

import org.apache.log4j.Logger;

import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
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
import oracle.retail.stores.pos.services.send.VerifySendSelectionSite;
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
public class MAXVerifySendSelectionSite extends VerifySendSelectionSite
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       revision number of this class
    **/
    protected static Logger logger = Logger.getLogger(max.retail.stores.pos.services.send.MAXVerifySendSelectionSite.class);
    /**
     already send resource id
     **/
    public static final String SEND_ALREADY_APPLIED = "SendAlreadyApplied";
    //Change for Rev 1.0
    public static final String SEND_ALREADY_APPLIED_GST = "SendAlreadyAppliedGST";
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
                	//Change for Rev 1.0 : Starts
                   /* int[] buttons = new int[] { DialogScreensIfc.BUTTON_UPDATE,
                                                DialogScreensIfc.BUTTON_CANCEL };
                    String[] letters = new String[] { UPDATE_LETTER,
                                                      CommonLetterIfc.CANCEL };
                    UIUtilities.setDialogModel(ui, 
                                               DialogScreensIfc.UPDATE_CANCEL, 
                                               SEND_ALREADY_APPLIED, 
                                               null,
                                               buttons, 
                                               letters );*/                	
                	if(!((cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc) && ((MAXSaleReturnTransactionIfc)cargo.getTransaction()).isGstEnable())){
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
                	}
                	else{
                    int[] buttons = new int[] { DialogScreensIfc.BUTTON_YES,
                                                DialogScreensIfc.BUTTON_NO };
                    String[] letters = new String[] { CommonLetterIfc.YES,
                                                      CommonLetterIfc.NO };
                    UIUtilities.setDialogModel(ui, 
                                               DialogScreensIfc.CONFIRMATION, 
                                               SEND_ALREADY_APPLIED_GST, 
                                               null,
                                               buttons, 
                                               letters );
                	}
                  //Change for Rev 1.0 : Ends
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
        	//Changes for Rev 1.0 : Starts
        	if(cargo.getTransaction() instanceof MAXSaleReturnTransactionIfc)
        		((MAXSaleReturnTransactionIfc)cargo.getTransaction()).setDeliverytrnx(true);
            //Changes for Rev 1.0 : Ends
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
          //Since send transaction always have customer and we dont need shipping and billing address prompt thats why new letter is mail
            if(((MAXSaleReturnTransaction)transaction).isGstEnable()){
            	bus.mail(CommonLetterIfc.DONE, BusIfc.CURRENT);
            }
            else{
            	 bus.mail(CommonLetterIfc.CONTINUE, BusIfc.CURRENT);
            }
        }
        catch(ParameterException pe)
        {
            logger.warn(pe.getStackTraceAsString());
        }
            
       
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
