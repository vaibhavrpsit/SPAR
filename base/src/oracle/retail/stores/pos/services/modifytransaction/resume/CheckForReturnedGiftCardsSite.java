/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/resume/CheckForReturnedGiftCardsSite.java /main/7 2014/05/14 14:41:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/14/14 - rename retrieve to resume
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   07/26/11 - moved StatusCode to GiftCardIfc
 *    cgreene   07/26/11 - removed tenderauth and giftcard.activation tours and
 *                         financialnetwork interfaces.
 *    asinton   06/29/11 - Refactored to use EntryMethod and
 *                         AuthorizationMethod enums.
 *    cgreene   05/27/11 - move auth response objects into domain
 *    cgreene   05/20/11 - implemented enums for reponse code and giftcard
 *                         status code
 *    cgreene   05/26/10 - convert to oracle packaging
 *    dwfung    03/10/10 - Handling Training Mode Requests
 *    jswan     01/22/10 - Code review modifications.
 *    jswan     01/21/10 - Fixed comments.
 *    jswan     01/21/10 - Fix an issue in which a returned gift card can be
 *                         modified during the period in which the transaction
 *                         has been suspended.
 *    jswan     01/21/10 - Add site to check if a gift card being returned has
 *                         changed whild the transaction was suspended.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.resume;

import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc.StatusCode;
import oracle.retail.stores.domain.returns.ReturnTenderDataElementIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardConstantsIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site send the gift card inquiry request.
 */
public class CheckForReturnedGiftCardsSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -3768385161620906224L;

    private static final int ALL_ITEMS_VALID   = 0;
    private static final int VALID_GIFT_CARD   = 1;
    private static final int ERROR_TIMEOUT     = 2;
    private static final int ERROR_OFFLINE     = 3;
    private static final int ERROR_MODIFIED    = 4;
    private static final int INVALID_GIFT_CARD = 5;

    /**
     * Determine if there are one or more gift cards being returned in this
     * transaction. If so request the card info from the external processor, and
     * verify that they are still valid to return.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // get transaction and initialize method scoped objects
        ModifyTransactionResumeCargo cargo = (ModifyTransactionResumeCargo) bus.getCargo();
        RetailTransactionIfc retTrans = cargo.getTransaction();
        int transStatus = ALL_ITEMS_VALID;
        StatusCode gcStatus = StatusCode.Unknown;

        // If the transaction is the correct type...
        if (retTrans instanceof SaleReturnTransactionIfc)
        {
            SaleReturnTransactionIfc trans = (SaleReturnTransactionIfc)retTrans;
            AbstractTransactionLineItemIfc[] items = trans.getItemContainerProxy().getLineItems();
            
            // Iterate from back to front, otherwise the wrong line item might be removed.
            for (int i = items.length - 1; i > -1; i--)
            {
                AbstractTransactionLineItemIfc item = items[i];
                // If the item is the right type...
                if (item instanceof SaleReturnLineItemIfc)
                {
                    // If the item contains a gift card...
                    SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)item;
                    if (srli.getPLUItem() instanceof GiftCardPLUItemIfc &&
                        srli.getReturnItem() !=      null)
                    {
                        // Make sure the gift card is still valid to return.
                        int result = VALID_GIFT_CARD;//TODO query PinComm
                        
                        // If not a valid gift card...
                        if (result != VALID_GIFT_CARD)
                        {
                            // Write to the journal, remove the item from the transaction, and set the error
                            // status variables.
                            writeJournalEntry(bus, trans, srli);
                            trans.removeLineItem(srli.getLineNumber());
                            trans.addDeletedLineItems(srli);
                            transStatus = result;
                            gcStatus = ((GiftCardPLUItemIfc)srli.getPLUItem()).getGiftCard().getStatus();
                        }
                    }
                }
            }
            
            // Determine whether mail a letter or display an error
            if (transStatus == ALL_ITEMS_VALID)
            {
                bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
            }
            else
            {
                // Force the transaction to be re-tendered
                ReturnTenderDataElementIfc[] ReturnTenderDataElementArray = new ReturnTenderDataElementIfc[0];
                trans.setReturnTenderElements(ReturnTenderDataElementArray);

                // Note that, if there is more than one gift card in the transactions and they fail in different ways,
                // only the last error message will be displayed to the operator.
                formatErrorDialog(transStatus, bus, gcStatus);
            }
        }
    }

    /*
     * Format the error message for 
     */
    private void formatErrorDialog(int result, BusIfc bus, StatusCode gcStatus)
    {
        POSUIManagerIfc ui        = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        String errorMessage       = null;
        String resourceID         = null;

        switch (result)
        {        
            case ERROR_TIMEOUT:
                errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC,
                        BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                        GiftCardConstantsIfc.ERROR_TIMEOUT_TAG,
                        GiftCardConstantsIfc.ERROR_TIMEOUT);
                resourceID = GiftCardConstantsIfc.GIFT_CARD_PROCESSOR_OFFLINE_ERRIR_DIALOG_ID;
                break;
                
            case ERROR_OFFLINE:
                errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC,
                    BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                    GiftCardConstantsIfc.ERROR_OFFLINE_TAG,
                    GiftCardConstantsIfc.ERROR_OFFLINE);
                resourceID = GiftCardConstantsIfc.GIFT_CARD_PROCESSOR_OFFLINE_ERRIR_DIALOG_ID;
                break;
            
            case ERROR_MODIFIED:
                errorMessage = utility.retrieveText(POSUIManagerIfc.DIALOG_SPEC,
                        BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                        GiftCardConstantsIfc.ERROR_MODIFIED_TAG,
                        GiftCardConstantsIfc.ERROR_MODIFIED);
                resourceID = GiftCardConstantsIfc.GIFT_CARD_RETURN_INVALID_DIALOG_ID;
                break;
        
            case INVALID_GIFT_CARD:
                String status = gcStatus.toString();
                errorMessage = utility.retrieveCommonText("GiftCardAuthResponseCode." + status, status);
                resourceID = "GiftCardReturnInvalid";
                break;
                
            default:
                status = gcStatus.toString();
                errorMessage = utility.retrieveCommonText("GiftCardAuthResponseCode." + status, status);
                resourceID = "GiftCardReturnInvalid";
                break;
        }
        
        showErrorDialog(ui, errorMessage, resourceID);
            
    }    
    
    /*
     * Display the error dialog
     */
    private void showErrorDialog(POSUIManagerIfc ui, String errorCode, String resourceID)
    {
        String args[] = {errorCode};
        DialogBeanModel dialogModel = new DialogBeanModel();
        if (resourceID == null)
        {
            resourceID = GiftCardConstantsIfc.GIFT_CARD_PROCESSOR_OFFLINE_ERRIR_DIALOG_ID;
        }
        dialogModel.setResourceID(resourceID);
        dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.INVALID);
        if (errorCode != null)
        {
          dialogModel.setArgs(args);
        }
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    /*
       Writes an entry in the journal.
    */
    private void writeJournalEntry(BusIfc bus, SaleReturnTransactionIfc transaction,
                                     SaleReturnLineItemIfc item)
    {                                   // begin toString()
        /*
         * Write the journal entry
         */
        JournalManagerIfc journal =
            (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        JournalFormatterManagerIfc formatter =
            (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(JournalFormatterManagerIfc.TYPE);

        if (journal != null && formatter != null)
        {
            journal.journal(transaction.getSalesAssociateID(),
                            transaction.getTransactionID(),
                            formatter.toJournalRemoveString(item));
        }
        else
        {
            logger.error( "JournalManager or JournalFormatterManager not found");
        }
    }
    
}                                       // end class RequestCardInfoSite

