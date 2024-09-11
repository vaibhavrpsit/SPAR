/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/SaveRedeemActionSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:09 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhayg    08/13/10 - STOPPING POS TRANSACTION IF REGISTER HDD IS FULL
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:29:50 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:25:02 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:14:03 PM  Robert Pearse   
 *
 *Revision 1.5  2004/07/23 22:17:25  epd
 *@scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 *Revision 1.4  2004/04/21 15:08:58  blj
 *@scr 3872 - cleanup from code review
 *
 *Revision 1.3  2004/04/07 22:49:40  blj
 *@scr 3872 - fixed problems with foreign currency, fixed ui labels, redesigned to do validation and adding tender to transaction in separate sites.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.redeem;

import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.store.StoreADO;
import oracle.retail.stores.pos.ado.store.StoreFactory;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * @author blj
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SaveRedeemActionSite extends PosSiteActionAdapter
{
    //----------------------------------------------------------------------
    /**
     This site saves a redeem transaction.  If an error occurs with the save, dialog screens
     are displayed from the site as well.

     @param  bus     Service Bus
     **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        RedeemCargo cargo = (RedeemCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
        try
        {
            // This statement will update FinancialTotal and save in to db based on the register
            RegisterADO register = StoreFactory.getInstance().getRegisterADOInstance();
            StoreADO store = StoreFactory.getInstance().getStoreADOInstance();
            register.setStoreADO(store);
            register.fromLegacy(cargo.getRegister());
            store.fromLegacy(cargo.getStoreStatus());
            txnADO.save(register);
        }
        catch (DataException dataException)
        {   
            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        	if (dataException.getErrorCode() == DataException.QUEUE_FULL_ERROR ||
        			dataException.getErrorCode() == DataException.STORAGE_SPACE_ERROR ||
        			dataException.getErrorCode() == DataException.QUEUE_OP_FAILED)
        	{
        		DialogBeanModel dialogModel = utility.createErrorDialogBeanModel(dataException, false);
        		// display dialog
        		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dialogModel);
        	}
        	else
        	{
                String errorString[] = new String[2];
                errorString[0] = utility.getErrorCodeString(dataException.getErrorCode());
                errorString[1] = "";
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR, "TranDatabaseError", errorString, "Failure");
        	}
            return;
        }
        
        // Add the transaction to the register so totals are updated
        // this statement will not update FinancialTotal table
        RegisterADO register = StoreFactory.getInstance().getRegisterADOInstance();
        StoreADO store = StoreFactory.getInstance().getStoreADOInstance();
        register.setStoreADO(store);
        register.fromLegacy(cargo.getRegister());
        store.fromLegacy(cargo.getStoreStatus());
        register.addTransaction(txnADO);
        
        // set last reprintable txn_id
        cargo.setLastReprintableTransactionID(txnADO.getTransactionID());
        
        // update hard totals
        try
        {
            register.writeHardTotals();
        }
        catch (DeviceException de)
        {
            //DialogBeanModel model = new DialogBeanModel();
            //model.setResourceID("WriteHardTotalsError");
            //model.setType(DialogScreensIfc.ERROR);
            // show dialog
            //ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR, "WriteHardTotalsError", null, "Failure");
            return;
        }
        
        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
    }
}
