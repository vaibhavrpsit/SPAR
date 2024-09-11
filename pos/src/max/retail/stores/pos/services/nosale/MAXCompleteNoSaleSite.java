/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/nosale/CompleteNoSaleSite.java /main/17 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * Rev 1.0	Ag 29,2016	Ashish yadav	Changes for code merging
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.nosale;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.NoSaleTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.nosale.CompleteNoSaleSite;
import oracle.retail.stores.pos.services.nosale.NoSaleCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * This site completes a NoSale transaction by saving a
 * <code>NoSaleTransaction</code> and writing hard totals.
 * 
 * @version $Revision: /main/17 $
 */
public class MAXCompleteNoSaleSite extends CompleteNoSaleSite
{
    /**
     * Generated SerialVersionUID
     */
    private static final long serialVersionUID = -2942014166965727867L;

    /**
     * revision number of this object.
     **/
    public static final String revisionNumber = "$Revision: /main/17 $";

    /**
     * print letter
     */
    public static final String LETTER_PRINT = "Print";

    /**
     * Completes a NoSale transaction by saving it to the database and writing
     * the hard totals.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        NoSaleCargo cargo = (NoSaleCargo) bus.getCargo();

        // Create a transaction
        NoSaleTransactionIfc transaction = DomainGateway.getFactory().getNoSaleTransactionInstance();
        transaction.setCashier(cargo.getOperator());

        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        // Initializes the fields common to all transactions.
        utility.initializeTransaction(transaction, -1);

        // Initialize the ui manager
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        boolean openDrawerForTraining = false;

        // letter to be mailed at end
        LetterIfc letter = null;

        // Save the transaction to the database
        try
        {
            // Initialize fields specific to NoSaleTransaction
            transaction.setLocalizedReasonCode(cargo.getSelectedReasonCode());
            transaction.setTimestampEnd();
            transaction.setTransactionStatus(TransactionIfc.STATUS_COMPLETED);
            cargo.setNoSaleTrans(transaction);
            /*
             * Write a journal entry
             */
            JournalManagerIfc journal;
            journal = (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);

            StringBuffer noSaleText = new StringBuffer();

            if (journal != null)
            {
                LocalizedCodeIfc selectedReasonCode = cargo.getSelectedReasonCode();

                if (selectedReasonCode != null && !selectedReasonCode.getCode().equals(CodeConstantsIfc.CODE_UNDEFINED))
                {
                    Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
                	Object dataArgs[] = new Object[]{selectedReasonCode.getText(lcl)};
                	noSaleText.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NO_SALE,dataArgs));
                }

                journal.journal(
                    transaction.getCashier().getLoginID(),
                    transaction.getTransactionID(),
                    noSaleText.toString());
            }
            else
            {
                logger.error("No JournalManager found");
            }

            utility.saveTransaction(transaction, cargo.getRegister().getCurrentTill(), cargo.getRegister());

            // Update the no sale count on the till and register.
            cargo.getRegister().addNumberNoSales(1);

            // update the hard totals
            utility.writeHardTotals();

            // check if in training mode
            ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
            openDrawerForTraining = pm.getBooleanValue(BASE_OpenDrawerInTrainingMode);
        }
        catch (DataException de)
        {
            logger.error("NoSaleTransaction cannot be saved.", de);

            UtilityManagerIfc util = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            /*
             * Display the Error msg Screen
             */
            if (de.getErrorCode() == DataException.QUEUE_FULL_ERROR ||
        			de.getErrorCode() == DataException.STORAGE_SPACE_ERROR ||
        			de.getErrorCode() == DataException.QUEUE_OP_FAILED)
        	{
        		DialogBeanModel dialogModel = util.createErrorDialogBeanModel(de, false);
                // display dialog
        		if (de.getErrorCode() == DataException.STORAGE_SPACE_ERROR ||
        				de.getErrorCode() == DataException.QUEUE_OP_FAILED)
        		{
        			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, "RetryContinue");
        		}

                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dialogModel);
                return;
        	}
            // Changes starts fro rev 1.0
			else
        	{
			// Changes ends fro rev 1.0
            String errorString[] = new String[2];
            errorString[0] =
                util.retrieveDialogText("TranDatabaseError.NoSaleIncomplete", "No sale cannot be completed:");
            errorString[1] = util.getErrorCodeString(de.getErrorCode());

            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("TranDatabaseError");
            dialogModel.setType(DialogScreensIfc.ERROR);
            dialogModel.setArgs(errorString);

            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		// Changes starts fro rev 1.0
				}
			// Changes ends fro rev 1.0
        }
        // catch exception from hard totals
        catch (DeviceException e)
        {
            logger.error("Write Hard totals failed.", e);
            // set bean model
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("WriteHardTotalsError");
            model.setType(DialogScreensIfc.ERROR);

            // show dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        catch (ParameterException pe)
        {
            logger.error("Could not retrieve setting for OpenDrawerInTrainingMode Parameter");
        }

        // Display the NOSALE_COMPLETE Screen
        ui.showScreen(POSUIManagerIfc.NOSALE_COMPLETE, new POSBaseBeanModel());

        // open drawer
        if (!(cargo.getRegister().getWorkstation().isTrainingMode()) || openDrawerForTraining)
        {
            letter = new Letter(CommonLetterIfc.OPEN_CASH_DRAWER);
        }
        else
        {
            letter = new Letter(CommonLetterIfc.PRINT);
        }
        bus.mail(letter, BusIfc.CURRENT);
    }
}
