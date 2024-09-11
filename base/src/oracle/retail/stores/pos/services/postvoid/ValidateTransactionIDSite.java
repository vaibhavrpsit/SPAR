/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/postvoid/ValidateTransactionIDSite.java /main/17 2013/11/15 19:11:20 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   11/15/13 - Add different till error dialog for post void a
 *                         transaction
 *    jswan     03/21/12 - Modified to support centralized gift certificate and
 *                         store credit.
 *    asinton   10/04/11 - prevent post voiding of transactions with authorized
 *                         tenders that lack necessary data for reversing.
 *    asinton   09/06/11 - remove ability to post void transactions with issue,
 *                         reload, redeem of gift cards.
 *    asinton   05/10/11 - refactor post void for gift cards for APF
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    09/11/09 - Use one field for scanning/entering transaction id
 *                         for return with receipt
 *    asinton   03/18/09 - Updates per code review.
 *    asinton   03/18/09 - Modified to show correct dialog.
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/12/2007 3:57:48 PM   Ashok.Mondal    CR
 *         4296 :Merge from V7.2.2 to trunk.
 *    4    360Commerce 1.3         3/29/2007 5:32:27 PM   Michael Boyd    CR
 *         26172 - v8x merge to trunk
 *
 *         4    .v8x      1.2.2.0     2/28/2007 10:45:41 AM  Rohit Sachdeva
 *         24879:
 *         CTR Issue: Void from StoreB for StoreA Transaction should display
 *         Transaction number not found for this date or till.
 *    3    360Commerce 1.2         3/31/2005 4:30:43 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:43 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:29 PM  Robert Pearse
 *
 *   Revision 1.6  2004/07/23 22:17:26  epd
 *   @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 *   Revision 1.5  2004/07/15 18:45:22  lwalters
 *   @scr 1888
 *
 *   Message type changed to DATABASE_ERROR so the error dialog will say it's a database error, not a transaction error.
 *
 *   Revision 1.4  2004/06/29 21:21:38  lwalters
 *   @scr 1888
 *
 *   Added a method to display database errors.  Catch DataException which
 *   is now being thrown in RegisterADO, and display appropriate error.
 *
 *   Revision 1.3  2004/02/12 16:48:15  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:28:20  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Jan 22 2004 15:19:54   epd
 * updated void dialogs
 *
 *    Rev 1.0   Nov 04 2003 11:16:08   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 23 2003 17:28:38   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 17 2003 13:03:26   epd
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.postvoid;

// Foundation imports
import oracle.retail.stores.pos.ado.context.ContextFactory;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.VoidErrorCodeEnum;
import oracle.retail.stores.pos.ado.transaction.VoidException;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TransactionID;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;

import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site checks to see that the transaction exists, was created on the same
 * day, and has a transaction type that can be voided.
 * 
 */
@SuppressWarnings("serial")
public class ValidateTransactionIDSite extends PosSiteActionAdapter
{
    /**
     * purchase date field
     */
    public static final String PURCHASE_DATE_FIELD = "purchaseDateField";

    /**
     * store number
     */
    public static final String STORE_NUMBER_FIELD = "storeNumberField";

    /**
     * register number
     */
    public static final String REGISTER_NUMBER_FIELD = "registerNumberField";

    /**
     * transaction number
     */
    public static final String TRANS_NUMBER_FIELD = "transactionNumberField";

    /**
     * Constant for error screen
     */
    public static final String INVALID_RETURN_NUMBER = "InvalidReturnNumber";

    /**
     * Checks to see that the transaction exists, was created on the same day,
     * and has a transaction type that can be voided. If the transaction is a
     * voidable transaction, a Success letter is mailed, otherwise, an error
     * message screen is displayed.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        VoidCargo cargo = (VoidCargo) bus.getCargo();
        RetailTransactionADOIfc txn =null;
        RegisterADO registerADO = ContextFactory.getInstance().getContext().getRegisterADO();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        try
        {
            String receiptId = ui.getInput();

            if (receiptId.length() != TransactionID.getTransactionIDLength())
            {
                // "Receipt" or "Other" number.
                // Using "generic dialog bean".
                DialogBeanModel dialogModel = new DialogBeanModel();
                dialogModel.setResourceID(INVALID_RETURN_NUMBER);
                dialogModel.setType(DialogScreensIfc.ERROR);
                // set and display the model
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
            }
            else
            {
                 TransactionIDIfc receiptNo = DomainGateway.getFactory().getTransactionIDInstance();
                 receiptNo.setTransactionID(receiptId);
                 txn = registerADO.loadTransaction(utility.getRequestLocales(), receiptNo.getTransactionIDString());
            }

            if (txn == null)
            {
               displayNotFoundDialog(bus);
               return;
            }

            try
            {
                if (txn.isVoidable(registerADO.getCurrentTillID())) // never returns false
                {
                    cargo.setOriginalTransactionADO(txn);
                    bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
                }
            }
            catch (VoidException ve)
            {
                // Display proper exception dialog
                VoidErrorCodeEnum error = ve.getErrorCode();
 
                if (error == VoidErrorCodeEnum.DIFFERENT_TILL)
                {
                    displayErrorDialog("DifferentTillError", ui);
                    return;
                }
                else if (error == VoidErrorCodeEnum.PREVIOUSLY_VOIDED)
                {
                     displayErrorDialog("TransactionAlreadyVoided", ui);
                     return;
                }
                else if (error == VoidErrorCodeEnum.TRANSACTION_MODIFIED)
                {
                    displayErrorDialog("VoidModifiedTransaction", ui);
                    return;
                }
                else if (error == VoidErrorCodeEnum.INVALID_TRANSACTION)
                {
                     displayErrorDialog("InvalidTransactionType", ui);
                     return;
                }
                else if (error == VoidErrorCodeEnum.VOID_GIFT_CARD_INVALID)
                {
                     displayErrorDialog("VoidGiftCardInvalid", ui);
                     return;
                }
                else if (error == VoidErrorCodeEnum.GIFT_CARD_VOID_INVALID)
                {
                      displayErrorDialog("GiftCardVoidInvalid", ui);
                     return;
                }
                else if (error == VoidErrorCodeEnum.GIFT_CERTIFICATE_VOID_INVALID)
                {
                      displayErrorDialog("GiftCertificateVoidInvalid", ui);
                     return;
                }
                else if (error == VoidErrorCodeEnum.STORE_CREDIT_VOID_INVALID)
                {
                      displayErrorDialog("StoreCreditVoidInvalid", ui);
                     return;
                }
                else if (error == VoidErrorCodeEnum.AUTH_TENDER_NOT_VOIDABLE)
                {
                     displayErrorDialog("AuthTenderNotVoidable", ui);
                     return;
                }
                else
                { 
                    logger.error("VoidException occurred with unknown error: " + error.toString());
                }
            }
        }
        catch (DataException de)
        {
            //Instead of general The query did not find a record or records that matches your search criteria
            //displaying a specific message Transaction number not found for this date or till as per reqs.
            if (de.getErrorCode() == DataException.NO_DATA)
            {
                displayNotFoundDialog(bus);
                return;
            }
            displayDatabaseErrorDialog(bus, de);
        }
    }

    private void displayNotFoundDialog(BusIfc bus)
    {
        DialogBeanModel dialogModel;
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        String[] args = new String[1];
        args[0] = utility.retrieveDialogText
          ("TransactionNotFound.TransactionNotFound",
           "Transaction number not found for this date or till.");
        dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("TransactionNotFound");
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(args);

        // display dialog
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
    }

    /**
     * Displays the error dialog for the given resource ID.
     * @param resourceID
     * @param ui
     */
    protected void displayErrorDialog(String resourceID, POSUIManagerIfc ui)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(resourceID);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

    }

    private void displayDatabaseErrorDialog(BusIfc bus, DataException de)
    {
        String errorString[] = new String[2];
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        errorString[0] = utility.getErrorCodeString(de.getErrorCode());
        errorString[1] = "";
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("DATABASE_ERROR");
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(errorString);
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
}
