/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/SaveTenderableTransactionAisle.java /main/14 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    abhayg    08/13/10 - STOPPING POS TRANSACTION IF REGISTER HDD IS FULL
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:50 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:03 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:03 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/09/27 22:32:03  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.6  2004/06/03 14:47:43  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.5  2004/05/06 05:03:14  tfritz
 *   @scr 2582 Fixed InvalidCastException error
 *
 *   Revision 1.4  2004/04/19 15:57:08  tmorris
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.3  2004/02/12 16:49:08  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:54:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Sep 25 2002 10:32:28   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:35:50   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:10:22   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:23:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   29 Jan 2002 09:54:18   epd
 * Deprecated all methods using accumulate parameter and added new methods without this parameter.  Also removed all reference to the parameter wherever used.
 * (The behavior is to accumulate totals)
 * Resolution for POS SCR-770: Remove the accumulate parameter and all references to it.
 * 
 *    Rev 1.0   Sep 21 2001 11:13:14   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:06:04   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.UpdateReturnedItemsCommandDataTransaction;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This aisle does all of the necessary processing needed when saving a
 * transaction.
 * 
 * @version $Revision: /main/14 $
 */
public class SaveTenderableTransactionAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -3258910694700636757L;
    // Class name
    public static final String LANENAME = "SaveTenderableTransactionAisle";

    /**
     * Saves the transaction to database and updates Financial Totals. A letter
     * is sent if it succeeds.
     * 
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {

        TenderableTransactionCargoIfc ttCargo = (TenderableTransactionCargoIfc) bus.getCargo();
        TenderableTransactionIfc trans = ttCargo.getTenderableTransaction();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        boolean saveTransSuccess = true;
        boolean saveHardTotSuccess = true;

        // Save the transaction to persistent storage
        // and update financial totals
        try
        {
            AbstractFinancialCargo afCargo = (AbstractFinancialCargo) bus.getCargo();
            TillIfc till = afCargo.getRegister().getCurrentTill();
            RegisterIfc register = afCargo.getRegister();
            utility.saveTransaction(trans, till, register);

            // Don't do this if we are in training mode
            if (!trans.isTrainingMode())
            {
                // This updates the financial totals for the register
                afCargo.getRegister().addTransaction(trans);
            }
            afCargo.setLastReprintableTransactionID(trans.getTransactionID());

            utility.writeHardTotals();
        }
        catch (DataException exception)
        {
        	   saveTransSuccess = false;
               UtilityManagerIfc util = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
               DialogBeanModel dialogModel = util.createErrorDialogBeanModel(exception);
               // display dialog
               ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
               logger.error( "" + exception + "");
        }
        // catch exception in writing hard totals
        catch (DeviceException e)
        {
            saveHardTotSuccess = false;
            // set bean model
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("WriteHardTotalsError");
            model.setType(DialogScreensIfc.ERROR);
            // show dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            logger.error( "" + Util.throwableToString(e) + "");
        }

        // These additional updates should be in a single transaction,
        // but can't be because they might go to different locations.
        // They CANNOT be in the same try block because if they are, and
        // the second one fails, the transaction sequence numbers get out
        // of wack.
        if (saveTransSuccess && trans instanceof VoidTransactionIfc)
        {
            VoidTransactionIfc voidTrans = (VoidTransactionIfc) trans;
            int origTransType = voidTrans.getOriginalTransaction().getTransactionType();

            if (origTransType == TransactionIfc.TYPE_SALE || origTransType == TransactionIfc.TYPE_RETURN)
            {
                try
                {
                    // If this is a void transaciton, call this object to update
                    // the original Transaction.
                    AbstractTransactionLineItemIfc[] itemArray = ((VoidTransactionIfc) trans).getLineItems();
                    
                    UpdateReturnedItemsCommandDataTransaction dbTrans = null;
                    
                    dbTrans = (UpdateReturnedItemsCommandDataTransaction) DataTransactionFactory.create(DataTransactionKeys.UPDATE_RETURNED_ITEMS_COMMAND_DATA_TRANSACTION);
                    
                    dbTrans.updateVoidedReturnedItems(itemArray);
                }
                catch (DataException e)
                {
                    logger.error("Could not update Voided Return Line Items.", e);
                }
            }
        }

        // If the db write is ok, update the origninal transaction.
        if (saveTransSuccess)
        {
            if (saveHardTotSuccess)
            {
                // if all updates succeeded, mail the continue letter.
                bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
            }
        }
    }
}
