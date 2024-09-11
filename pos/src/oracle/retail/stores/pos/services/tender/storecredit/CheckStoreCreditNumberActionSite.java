/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/storecredit/CheckStoreCreditNumberActionSite.java /main/12 2011/02/03 18:23:32 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       02/03/11 - check in all
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         5/19/2008 2:29:32 AM   ASHWYN TIRKEY
 *         Updated the file to handle offline flow and already issued store
 *         credit flows correctly for issue 31453
 *    7    360Commerce 1.6         3/31/2008 1:58:15 PM   Mathews Kochummen
 *         forward port from v12x to trunk
 *    6    360Commerce 1.5         4/25/2007 8:52:44 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    5    360Commerce 1.4         4/5/2006 5:59:49 AM    Akhilashwar K. Gupta
 *         CR-3861: As per BA decision, reverted back the changes done earlier
 *          to fix the CR i.e. addition of following 4 fields in Store Credit
 *         and related code:
 *         - RetailStoreID
 *         - WorkstationID
 *         - TransactionSequenceNumber
 *         - BusinessDayDate
 *    4    360Commerce 1.3         3/15/2006 11:45:37 PM  Akhilashwar K. Gupta
 *         CR-3861: Modified  arrive() method and Added
 *         setStoreCreditAttributes() method
 *    3    360Commerce 1.2         3/31/2005 4:27:26 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:13 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:59 PM  Robert Pearse
 *
 *   Revision 1.9  2004/08/16 19:44:40  blj
 *   @scr 5314 - added new screen for invalid store credits.
 *
 *   Revision 1.8  2004/07/22 22:38:41  bwf
 *   @scr 3676 Add tender display to ingenico.
 *
 *   Revision 1.7  2004/06/16 18:07:41  bwf
 *   @scr 5000 Back out changes, change to req happening.
 *
 *   Revision 1.6  2004/06/15 22:57:01  bwf
 *   @scr 5000 Check to see if customer was captured before asking again.
 *
 *   Revision 1.5  2004/05/16 20:54:18  blj
 *   @scr 4476 rework,postvoid and cleanup
 *
 *   Revision 1.4  2004/05/11 16:08:47  blj
 *   @scr 4476 - more rework for store credit tender.
 *
 *   Revision 1.3  2004/03/04 23:23:42  nrao
 *   Code Review Changes for Issue Store Credit.
 *
 *   Revision 1.2  2004/02/25 19:27:50  nrao
 *   Added error dialog for Issue Store Credit when check digit determines that an invalid number has been entered.
 *
 *   Revision 1.1  2004/02/17 17:56:49  nrao
 *   New site for Issue Store Credit
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.storecredit;

// java imports
import java.util.HashMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.tender.TenderLineItemConstantsIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderStoreCreditADO;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.ReturnableTransactionADOIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.utility.StoreCreditIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//-----------------------------------------------------------------------
/**
 *    This site executes the check digits usecase and mails a letter
 *
 *    $Revision: /main/12 $
 */
//-----------------------------------------------------------------------
public class CheckStoreCreditNumberActionSite extends PosSiteActionAdapter
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/12 $";

    // Static Strings
    public static final String INVALID_LETTER = "Invalid";
    public static final String VALID_LETTER = "Valid";
    public static final String FAILURE_LETTER = "Failure";
    public static final String STORE_CREDIT_MINIMUM = "StoreCreditMinimum";
    public static final String OVERTENDER_IN_A_RETURN = "OvertenderInAReturn";
    public static final String INVALID_NUMBER_ERROR = "InvalidNumberError";

    //------------------------------------------------------------------------
    /*
     * @param bus  The bus arriving at this site.
     */
    //------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo) bus.getCargo();

        TenderStoreCreditADO storeCreditTender = (TenderStoreCreditADO) cargo.getTenderADO();

        // Get tender attributes
        HashMap tenderAttributes = cargo.getTenderAttributes();
        // add tender type
        tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.STORE_CREDIT);

        try
        {
            // create a new store credit tender
            // TODO: didnt we do this already in IssueStoreCreditAction
            TenderFactoryIfc factory = (TenderFactoryIfc)ADOFactoryComplex.getFactory("factory.tender");
            storeCreditTender = (TenderStoreCreditADO)factory.createTender(tenderAttributes);
        }
        catch (ADOException adoe)
        {
            adoe.printStackTrace();
        }
        catch (TenderException e)
        {
            logger.error("Error creating Issue Store Credit Tender", e);
        }

          RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
          String amount = (String)cargo.getTenderAttributes().get(TenderConstants.AMOUNT);
        try
        {
           // RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
            // String amount = (String)cargo.getTenderAttributes().get(TenderConstants.AMOUNT);
            cargo.setTenderADO(storeCreditTender);
            boolean isReturnWithReceipt = ((ReturnableTransactionADOIfc)txnADO).isReturnWithReceipt();
            boolean isReturnWithOriginalRetrieved = ((ReturnableTransactionADOIfc)txnADO).isReturnWithOriginalRetrieved();
            txnADO.validateRefundLimits(storeCreditTender.getTenderAttributes(), isReturnWithReceipt, isReturnWithOriginalRetrieved);

            storeCreditTender.validateStoreCreditNumber();

            processStoreTender(storeCreditTender, txnADO, amount);


            // create store credit with existing id
            StoreCreditIfc storeCredit = storeCreditTender.createStoreCredit();

            // retrieve amount to be used for store credit
            CurrencyIfc tenderAmount = null;
            tenderAmount = txnADO.issueStoreCreditAmount((String)cargo.getTenderAttributes().get(TenderConstants.AMOUNT));

            // determine type of store credit to be issued
            TenderStoreCreditIfc tscIssue = txnADO.unusedStoreCreditReissued(storeCredit, tenderAmount);
            storeCreditTender.fromLegacy(tscIssue);

            // try to add the tender to the transaction
            txnADO.addTender(storeCreditTender);
            cargo.setLineDisplayTender(storeCreditTender);
            String nextLetter = VALID_LETTER;
            bus.mail(new Letter(nextLetter), BusIfc.CURRENT);
        }
        catch (TenderException e)
        {
            TenderErrorCodeEnum error = e.getErrorCode();
            UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

            if (error == TenderErrorCodeEnum.INVALID_NUMBER)
            {
                String[] args = {(String)tenderAttributes.get(TenderConstants.NUMBER)};

                displayDialog(bus, DialogScreensIfc.ACKNOWLEDGEMENT, INVALID_NUMBER_ERROR, args, INVALID_LETTER);
            }
            else if (error == TenderErrorCodeEnum.MIN_LIMIT_VIOLATED)
            {
                // must save tender in cargo
                cargo.setTenderADO(storeCreditTender);

                displayErrorDialog(bus, STORE_CREDIT_MINIMUM, DialogScreensIfc.ERROR);
            }
            else if (error == TenderErrorCodeEnum.OVERTENDER_ILLEGAL)
            {
                // must save tender in cargo
                cargo.setTenderADO(storeCreditTender);
                displayErrorDialog(bus, OVERTENDER_IN_A_RETURN, DialogScreensIfc.ERROR);
            }
            else if (error == TenderErrorCodeEnum.INVALID_CERTIFICATE)
            {
                //String args[] = new String[2];
                //args[0] = utility.retrieveDialogText("StoreCredit", "StoreCredit");
                //args[1] = utility.retrieveDialogText("Tendered", "Tendered");
                displayDialog(bus, DialogScreensIfc.ERROR, "InvalidStoreCreditError", null, "Invalid");
                return;
            }
            else if (error == TenderErrorCodeEnum.VALIDATION_OFFLINE)
            {
                //process the tender and add the tender to the transaction
            	//here when store server is offlie we assume that the store credit number entered
            	//was unique other it will be a case missing transaction.
            	processStoreTender(storeCreditTender, txnADO, amount);
                txnADO.addValidTender(storeCreditTender);

                cargo.setLineDisplayTender(storeCreditTender);

            	String storeCreditText = utility.retrieveDialogText("StoreCredit", "StoreCredit");
            	String args[] = { storeCreditText,storeCreditText };
                displayDialog(bus, DialogScreensIfc.ERROR, "ValidationOffline", args, "Success");

                return;
            }
     else if (error == TenderErrorCodeEnum.ISSUE_STORE_CREDIT)
            {
            	String args[] = { utility.retrieveDialogText("StoreCredit", "StoreCredit") };
            	displayDialog(bus, DialogScreensIfc.ERROR, "ALREADY_ISSUED", args, "Invalid");
            	return;
            }
        }
    }

    //--------------------------------------------------------------------------------------
    /**
     * Show an error dialog
     * @param bus The bus arriving at the site
     * @param name Name of the dialog message to be displayed
     * @param dialogType Type of error dialog
     */
    //--------------------------------------------------------------------------------------
    protected void displayErrorDialog(BusIfc bus, String name, int dialogType)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(name);
        dialogModel.setType(dialogType);

        if (dialogType == DialogScreensIfc.ERROR)
        {
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, FAILURE_LETTER);
        }
        dialogModel.setArgs(
            new String[] {DomainGateway.getFactory()
                                       .getTenderTypeMapInstance()
                                       .getDescriptor(TenderLineItemConstantsIfc.TENDER_TYPE_STORE_CREDIT)});
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    //----------------------------------------------------------------------
    /**
        Show an error dialog
        @param bus The bus arriving at the site
        @param screenType The dialog type to be displayed
        @param message The dialog text to be displayed
        @param args The arguments to be displayed
        @param letter The letter to be mailed
    **/
    //----------------------------------------------------------------------
    protected void displayDialog(BusIfc bus, int screenType, String message, String[] args, String letter)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        if (letter != null)
        {
            UIUtilities.setDialogModel(ui, screenType, message, args, letter);
        }
        else
        {
            UIUtilities.setDialogModel(ui, screenType, message, args);
        }
    }
 /**
     * This method is used to create store credit, retrieve amount to be used
     * for the store credit and determine the type of store credit to be issued.
     * @param storeCreditTender
     * @param txnADO
     * @param amount
     */
    protected void processStoreTender(TenderStoreCreditADO storeCreditTender,
    								  RetailTransactionADOIfc txnADO,
    								  String amount)
    {
        // create store credit with existing id
        StoreCreditIfc storeCredit = storeCreditTender.createStoreCredit();

        // retrieve amount to be used for store credit
        CurrencyIfc tenderAmount = null;
        tenderAmount = txnADO.issueStoreCreditAmount(amount);

        // determine type of store credit to be issued
        TenderStoreCreditIfc tscIssue = txnADO.unusedStoreCreditReissued(storeCredit, tenderAmount);
        storeCreditTender.fromLegacy(tscIssue);
    }

}
