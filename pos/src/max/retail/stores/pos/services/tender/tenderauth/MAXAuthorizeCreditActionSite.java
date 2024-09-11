/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved.
*  Rev 1.0	Jyoti Rawal		28/05/2013		Initial Draft:	Credit Card Requirement
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.tenderauth;

import java.util.HashMap;

import max.retail.stores.pos.ado.tender.MAXAuthorizableADOIfc;
import max.retail.stores.pos.ado.tender.MAXTenderCreditADO;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.AuthResponseCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderCreditADO;
import oracle.retail.stores.pos.ado.utility.AuthorizationException;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 *  Authorizes a credit tender
 */
public class MAXAuthorizeCreditActionSite extends PosSiteActionAdapter
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7967674825421665475L;

	/* (non-Javadoc)
     * @see com.extendyourstore.foundation.tour.application.SiteActionAdapter#arrive(com.extendyourstore.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        MAXTenderAuthCargo cargo = (MAXTenderAuthCargo)bus.getCargo();

        // journal the result
        JournalFactoryIfc jrnlFact = null;
        try
        {
            jrnlFact = JournalFactory.getInstance();
        }
        catch (ADOException e)
        {
            logger.error(JournalFactoryIfc.INSTANTIATION_ERROR, e);
            throw new RuntimeException(JournalFactoryIfc.INSTANTIATION_ERROR, e);
        }
        RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();

        // Display authorizing UI
        // Commented for HC
      //  displayAuthorizationUI(bus);

        // get and authorize the current credit tender
        String letter = "Success";
        try
        {
            // attempt the authorization
            HashMap map = new HashMap();
            map.put(TenderConstants.TRANSACTION_NUMBER,
                    cargo.getCurrentTransactionADO().getTransactionID());
            String store = cargo.getRegister().getWorkstation().getStoreID();
            String workStationID = cargo.getRegister().getWorkstation().getWorkstationID();
            map.put(TenderConstants.AUTH_SEQUENCE_NUMBER,store+workStationID);
            ((MAXAuthorizableADOIfc)cargo.getCurrentAuthTender()).authorize(map);

            // if we get here, it was approved.

            // journal the decline
            registerJournal.journal(cargo.getCurrentAuthTender(),
                            JournalFamilyEnum.TENDER,
                            JournalActionEnum.AUTHORIZATION);

            // We need to get customer info if:
            // 1) This is a manually entered house card and
            // 2) we haven't already captured it.
     // changes starts for code merging(comment below line)
            //boolean isManualEntry = (cargo.getCurrentAuthTender()
                                        //  .getTenderAttributes()
                                          //.get(TenderConstants.ENTRY_METHOD) == TenderLineItemIfc.ENTRY_METHOD_MANUAL)
                                          //? true : false;
            boolean isManualEntry = (cargo.getCurrentAuthTender()
                      .getTenderAttributes()
                      .get(TenderConstants.ENTRY_METHOD) == EntryMethod.Manual)
                      ? true : false;
      // Changes ends
//            if (((TenderCreditADO)cargo.getCurrentAuthTender()).getCreditType() == CreditTypeEnum.HOUSECARD &&
//                isManualEntry &&
//                ((TenderCreditADO)cargo.getCurrentAuthTender()).capturedCustomerInfo() == false)
//            {
//                letter = "CheckID";
//            }
        }
        catch (AuthorizationException e)
        {
            AuthResponseCodeEnum error = e.getResponseCode();

            if (error == AuthResponseCodeEnum.DECLINED)
            {
                handleDecline(bus, registerJournal, e);
                // null the letter so it won't be mailed.
                letter = null;
            }
            else if (error == AuthResponseCodeEnum.REFERRAL)
            {
                letter = handleReferral();
            }
            else if (error == AuthResponseCodeEnum.ERROR_RETRY)
            {
                handleErrorRetry(bus);
                // null the letter so it won't be mailed.
                letter = null;
            }
            else if (error == AuthResponseCodeEnum.POSITIVE_ID)
            {
                letter = handlePositiveID((TenderCreditADO)cargo.getCurrentAuthTender());
            }
            else if (error == AuthResponseCodeEnum.TIMEOUT)
            {
                letter = handleOffline(bus, registerJournal, (TenderCreditADO)cargo.getCurrentAuthTender());
            }
            else if (error == AuthResponseCodeEnum.OFFLINE)
            {
                letter = handleOffline(bus, registerJournal, (TenderCreditADO)cargo.getCurrentAuthTender());
            }
            else if (error == AuthResponseCodeEnum.FIRST_TIME_USAGE)
            {
                letter = handleFirstTimeUsage((TenderCreditADO)cargo.getCurrentAuthTender());
            }
        }

        if (letter != null)
        {
            bus.mail(new Letter(letter), BusIfc.CURRENT);
        }
    }

    /**
     * Decline the credit
     * @param bus
     * @param journal
     * @param e
     */
    protected void handleDecline(BusIfc bus, RegisterJournalIfc journal, AuthorizationException e)
    {
        MAXTenderAuthCargo cargo = (MAXTenderAuthCargo)bus.getCargo();
        // is decline override available for this tender?
        String[] dialogArgs = {e.getResponseDisplay()};
        DialogBeanModel dialogModel = new DialogBeanModel();
        if (((TenderCreditADO)cargo.getCurrentAuthTender()).isManagerOverrideForAuthEnabled("OverrideCreditDecline"))
        {
            // Set up to display dialog giving the operator the choice of
            // overriding the charge decline
            dialogModel.setResourceID("OverrideDeclineCredit");
            dialogModel.setType(DialogScreensIfc.CONFIRMATION);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, "Override");
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "Failure");
            dialogModel.setArgs(dialogArgs);
            cargo.setAccessFunctionID(RoleFunctionIfc.OVERRIDE_DECLINED_CREDIT);
        }
        else
        {
            //create String[] containing decline reason for dialog screen
            dialogModel.setResourceID("CreditAuthDeclined");
            dialogModel.setType(DialogScreensIfc.ERROR);
            dialogModel.setArgs(dialogArgs);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Failure");
        }
        // display dialog
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    /**
     * Simply return the appropriate letter
     * @return
     */
    protected String handleReferral()
    {
        return "ReferCharge";
    }

    /**
     * Simply return the appropriate letter
     * @return
     */
    protected void handleErrorRetry(BusIfc bus)
    {
        MAXTenderAuthCargo cargo = (MAXTenderAuthCargo) bus.getCargo();
        TenderCreditADO credit = (TenderCreditADO)cargo.getCurrentAuthTender();

        String[] args = new String[1];
        args[0] = (String)credit.getTenderAttributes().get(TenderConstants.AUTH_RESPONSE);

        // Display error message
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("AuthRetry");
        dialogModel.setType(DialogScreensIfc.RETRY_CANCEL);
        dialogModel.setArgs(args);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, "Yes");
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CANCEL, "No");
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    /**
     * Return success unless haven't captured the customer information
     * @param credit
     * @return
     */
    protected String handlePositiveID(TenderCreditADO credit)
    {
        String letter = "Success";
        if (!credit.capturedCustomerInfo())
        {
            letter = "PositiveID";
        }
        return letter;
    }

    //----------------------------------------------------------------------
    /**
        Return success unless havent captured the customer info.
        @param credit
        @return
    **/
    //----------------------------------------------------------------------
    protected String handleFirstTimeUsage(TenderCreditADO credit)
    {
        String letter = "Success";
        if (!credit.capturedCustomerInfo())
        {
            letter = "CheckID";
        }
        return letter;
    }

    /**
     * Display timed out dialog
     * @param bus
     */
    protected void handleErrorTimeout(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        // Display error message
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("AuthTimedOut");
        dialogModel.setType(DialogScreensIfc.CONFIRMATION);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    /**
     * Display offline dialog
     * @param bus
     * @param journal
     * @return the letter to be mailed
     */
    protected String handleOffline(BusIfc bus, RegisterJournalIfc journal, TenderCreditADO credit)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // set status to offline
        ui.statusChanged(POSUIManagerIfc.CREDIT_STATUS,
                         POSUIManagerIfc.OFFLINE);

        String letter = null;
        if (((MAXTenderCreditADO) credit).offlineAuthorizationOk())
        {
            // journal the decline
            journal.journal(credit,
                            JournalFamilyEnum.TENDER,
                            JournalActionEnum.AUTHORIZATION);
            letter = "Success";
        }
        else
        {
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("CreditAuthOffline");
            dialogModel.setType(DialogScreensIfc.ERROR);
            // button should redirect to call center screen
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "ReferCharge");
            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
            // set letter to be null (just to be explicit about it)
            letter = null;
        }


        return letter;
    }

    /**
     * Displays Authorization UI screen
     * @param bus
     */
    protected void displayAuthorizationUI(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
    	boolean isReentryMode =
    		((MAXTenderAuthCargo)bus.getCargo()).getRegister().getWorkstation().isTransReentryMode();

    	// if in re-entry mode, then skip showing the Authorization screen
    	if(isReentryMode == false)
    	{
	        //get manager for ui and put up "authorizing..." screen
	        UtilityManagerIfc utility =
	          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

	        PromptAndResponseModel parModel = new PromptAndResponseModel();
	        // get text
	        String promptText = utility.retrieveText
	          (POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
	           BundleConstantsIfc.COMMON_BUNDLE_NAME,
	           "CreditAuthorizationPrompt",
	           "CreditAuthorizationPrompt");
	        parModel.setPromptText(promptText);

	        POSBaseBeanModel baseModel = new POSBaseBeanModel();
	        baseModel.setPromptAndResponseModel(parModel);
	        ui.showScreen(POSUIManagerIfc.AUTHORIZATION, baseModel);
    	}

    	//default credit status
        ui.statusChanged(POSUIManagerIfc.CREDIT_STATUS,
                         POSUIManagerIfc.ONLINE);
    }
}
