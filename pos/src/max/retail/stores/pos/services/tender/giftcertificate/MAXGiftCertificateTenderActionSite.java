/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 * 
 * Rev 1.0   Feb 17,2017    		Nitesh Kumar     Changes for Validation dialog removal
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.tender.giftcertificate;

import java.util.HashMap;
import java.util.Locale;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderGiftCertificateADO;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.services.tender.giftcertificate.GiftCertificateTenderActionSite;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Attempts to add a gift certificate to the transaction
 */
@SuppressWarnings("serial")
public class MAXGiftCertificateTenderActionSite extends GiftCertificateTenderActionSite
{
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo) bus.getCargo();
        boolean transactionReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();
        boolean isTrainingMode = cargo.getRegister().getWorkstation().isTrainingMode();

        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        // check if same gift certificate is being used
        // in a single transaction
        if(cargo.getCurrentTransactionADO().isGiftCertificateUsed(cargo.getTenderAttributes()))
        {
            showAlreadyIssuedDialog(utility, ui, cargo.getTenderAttributes());
            return;
        }

        // If we already have the gc tender in cargo, we have used it to
        // try and override the tender limits, attempt to add it to the txn
        // again.
        TenderGiftCertificateADO gcTender = null;
        if (cargo.getTenderADO() == null)
        {
            // Get tender attributes
            cargo.getTenderAttributes().put(TenderConstants.TRAINING_MODE, new Boolean(isTrainingMode));

            try
            {
                // create a new gc tender
                TenderFactoryIfc factory = (TenderFactoryIfc) ADOFactoryComplex.getFactory("factory.tender");
                gcTender = (TenderGiftCertificateADO) factory.createTender(cargo.getTenderAttributes());
            }
            catch (ADOException adoe)
            {
                adoe.printStackTrace();
            }
            catch (TenderException e)
            {
                assert(false) : "This should never happen, because UI enforces proper format";
            }
        }
        else
        {
            gcTender = (TenderGiftCertificateADO) cargo.getTenderADO();
        }

        gcTender.setTransactionReentryMode(transactionReentryMode);

        try
        {
            // attempt to add the tender to the transaction
            cargo.getCurrentTransactionADO().addTender(gcTender);
            cargo.setLineDisplayTender(gcTender);

            // journal the added tender
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
            registerJournal.journal(gcTender, JournalFamilyEnum.TENDER, JournalActionEnum.ADD);

            // mail a letter
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
        catch (TenderException e)
        {
            TenderErrorCodeEnum error = e.getErrorCode();

            if (error == TenderErrorCodeEnum.INVALID_NUMBER)
            {
                showInvalidNumberDialog(utility, ui);
                return;
            }
            else if (error == TenderErrorCodeEnum.CERTIFICATE_TENDERED)
            {
                showTenderRedeemedDialog(utility, ui, gcTender.getTenderAttributes());
                return;
            }
            else if (error == TenderErrorCodeEnum.INVALID_CERTIFICATE
                        || error == TenderErrorCodeEnum.CERTIFICATE_VOIDED)
            {
                showInvalidCertificateDialog(utility, ui);
                return;
            }
            else if (error == TenderErrorCodeEnum.GIFT_CERTIFICATE_NUMBER_ALREADY_USED)
            {
            	showAlreadyIssuedDialog(utility, ui, gcTender.getTenderAttributes());
                return;
            }
            else if (error == TenderErrorCodeEnum.INVALID_CURRENCY)
            {
                displayDialog(bus, DialogScreensIfc.ERROR, "InvalidGiftCertificateCurrency", null, "Invalid");
                return;
            }
            else if (error == TenderErrorCodeEnum.VALIDATION_OFFLINE)
            {
                // display dialog screen
                cargo.setTenderADO(gcTender);
                String type = utility.retrieveDialogText("GiftCert", "GiftCert");
                String args[] = {type, type};
                
                //Changes for rev 1.0 starts
                //displayDialog(bus, DialogScreensIfc.ERROR, "ValidationOffline", args, "Offline");
                //return;
                bus.mail(new Letter(CommonLetterIfc.OFFLINE), BusIfc.CURRENT);
              //Changes for rev 1.0 ends
            }
        }
    }

    /**
     * Shows the Tender Redeemed Error dialog screen.
     */
    protected void showTenderRedeemedDialog(UtilityManagerIfc utility, POSUIManagerIfc ui, HashMap<Object,Object> tenderAttributes)
    {
        String args[] = new String[2];

        args[0] = (String)tenderAttributes.get(TenderConstants.NUMBER);
        args[1] = (String)tenderAttributes.get(TenderConstants.REDEEM_DATE);

        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("TenderRedeemed");
        dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        dialogModel.setArgs(args);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Invalid");
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    /**
     * Shows the card type not accepted dialog screen.
     */
    protected void showInvalidCertificateDialog(UtilityManagerIfc utility, POSUIManagerIfc ui)
    {
        String args[] = new String[2];
        args[0] = utility.retrieveDialogText("GiftCertificate", "Gift Certificate");
        args[1] = utility.retrieveDialogText("Tendered", "Tendered");
        args[1] = args[1].toLowerCase(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));

        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("InvalidCertificateError");
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(args);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Invalid");
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    /**
     * Shows the Invalid Number Error dialog screen.
     */
    protected void showInvalidNumberDialog(UtilityManagerIfc utility, POSUIManagerIfc ui)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("InvalidNumberError");
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Loop");

        String[] args = new String[2];
        args[1] = utility.retrieveDialogText("Certificate", "Certificate");
        args[1] += " " + utility.retrieveDialogText("Number", "number");
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        args[0] = args[1].toLowerCase(locale);
        dialogModel.setArgs(args);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    /**
     * Shows the Gift Certificate Already Issued dialog screen.
     */
    protected void showAlreadyIssuedDialog (UtilityManagerIfc utility, POSUIManagerIfc ui, HashMap<String,Object> tenderAttributes)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("ALREADY_ISSUED");
        dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Invalid");

        String[] args = new String[1];
        args[0] = utility.retrieveDialogText("GiftCertificate", "Gift Certificate");
        args[0] += " " +(String) tenderAttributes.get(TenderConstants.NUMBER);
        dialogModel.setArgs(args);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    /**
     *
     * @param bus
     * @param screenType
     * @param message
     * @param args
     * @param letter
     */
    protected void displayDialog(BusIfc bus, int screenType, String message, String[] args, String letter)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UIUtilities.setDialogModel(ui, screenType, message, args, letter);
    }

}
