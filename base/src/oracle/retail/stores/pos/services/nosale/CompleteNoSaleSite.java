/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/nosale/CompleteNoSaleSite.java /main/17 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    npoola    09/24/10 - changed the parameter name from
 *                         TrainingModeOpenDrawer to OpenDrawerInTrainingMode
 *    abhayg    08/13/10 - STOPPING POS TRANSACTION IF REGISTER HDD IS FULL
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    mdecama   11/04/08 - I18N - Fixed the way the locale was being retrieved
 *    akandru   10/30/08 - EJ changes
 *    mdecama   10/21/08 - I18N - Localizing No Sale ReasonCode

     $Log:
      3    360Commerce 1.2         3/31/2005 4:27:29 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:20:20 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:10:08 PM  Robert Pearse
     $
     Revision 1.11  2004/07/22 15:05:55  awilliam
     @scr 4465 no sale receipt print control transaction header and barcode are missing

     Revision 1.10  2004/05/05 01:15:50  tfritz
     @scr 3285 No Sale now prompts the user to close the cash drawer.

     Revision 1.9  2004/03/31 20:54:04  bjosserand
     @scr 4093 Transaction Reentry

     Revision 1.8  2004/03/31 20:19:01  bjosserand
     @scr 4093 Transaction Reentry

     Revision 1.7  2004/03/30 23:52:26  bjosserand
     @scr 4093 Transaction Reentry

     Revision 1.6  2004/03/15 17:05:52  khassen
     @scr 3945 - Changed letter from "Continue" to "Print" on line 254.

     Revision 1.5  2004/02/27 21:09:04  tfritz
     @scr 0 - Updated CommonLetterIfc import

     Revision 1.4  2004/02/18 18:59:05  tfritz
     @scr 3818 - Made code review changes.

     Revision 1.3  2004/02/12 16:51:18  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:48  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.1   Feb 10 2004 14:39:40   Tim Fritz
 * Added the new CaptureReasonCodeForNoSale parameter for the new No Sale requirments.
 *
 *    Rev 1.0   Aug 29 2003 16:03:04   CSchellenger
 * Initial revision.
 *
 *    Rev 1.6   Jul 21 2003 17:37:30   bwf
 * Save transaction, which ends journal, after journaling reason code.
 * Resolution for 2709: E. Journal is not journaling Cancel/No Sale and MBC Customer correctly
 *
 *    Rev 1.5   Jul 14 2003 15:10:54   mrm
 * Use CashDrawerOpenAisle
 * Resolution for POS SCR-3042: Device is Offline message does not appear during No Sale with Drawer offline
 *
 *    Rev 1.4   Jun 17 2003 16:07:34   RSachdeva
 * Open Cash Drawer and then print receipt
 * Resolution for POS SCR-2179: No - Sale  Cash Drawer will not Open.
 *
 *    Rev 1.3   Apr 09 2003 13:17:42   KLL
 * Print a No Sale "chit"
 * Resolution for POS SCR-1884: Printing Functional Requirements
 *
 *    Rev 1.2   Sep 25 2002 10:22:34   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   05 Jun 2002 22:01:56   baa
 * support for  opendrawerfortrainingmode parameter
 * Resolution for POS SCR-1645: Training Mode Enhancements
 *
 *    Rev 1.0   Apr 29 2002 15:13:50   msg
 * Initial revision.
 *
 *    Rev 1.1   21 Mar 2002 10:17:00   epd
 * Updated code to make sure drawer gets closed
 * Resolution for POS SCR-1571: No Sale trans does not prompt user to Close the Drawer
 *
 *    Rev 1.0   Mar 18 2002 11:40:08   msg
 * Initial revision.
 *
 *    Rev 1.3   Mar 10 2002 18:00:34   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.2   11 Feb 2002 10:28:56   vxs
 * Incorporated getReasonCodeText() while journalling.
 * Resolution for POS SCR-172: No Sale on EJ prints reason code as a number
 *
 *    Rev 1.1   29 Jan 2002 09:54:24   epd
 * Deprecated all methods using accumulate parameter and added new methods without this parameter.  Also removed all reference to the parameter wherever used.
 * (The behavior is to accumulate totals)
 * Resolution for POS SCR-770: Remove the accumulate parameter and all references to it.
 *
 *    Rev 1.0   Sep 21 2001 11:31:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:10   msg
 * header update
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.nosale;

import java.util.Locale;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
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
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
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
public class CompleteNoSaleSite extends PosSiteActionAdapter
    implements ParameterConstantsIfc
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
