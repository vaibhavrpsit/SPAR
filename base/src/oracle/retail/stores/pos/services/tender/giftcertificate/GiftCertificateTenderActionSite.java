/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/giftcertificate/GiftCertificateTenderActionSite.java /main/19 2012/08/27 11:22:41 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  08/20/12 - removed placeholder from key TenderRedeemed
 *    rabhawsa  08/16/12 - wptg - removed placeholder from key
 *                         InvalidCertificateError
 *    jswan     04/11/12 - Modified to support centralized validation of gift
 *                         certificates and store credits.
 *    jswan     03/26/12 - Modified to support centralized gift certificate and
 *                         store credit.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   07/12/11 - update generics
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    mweis     02/05/10 - Use more descriptive error message
 *    abondala  01/03/10 - update header date
 *    jswan     11/18/09 - Forward to fix use of gift cerificate more than once
 *                         in a transaction and making change to gift
 *                         certificate which already been redeemed.
 *    jswan     11/17/09 - XbranchMerge shagoyal_bug-8553074 from
 *                         rgbustores_13.0x_branch
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:28:18 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:21:57 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:11:15 PM  Robert Pearse
 * $
 * Revision 1.11.2.1  2004/11/12 14:28:53  kll
 * @scr 7337: JournalFactory extensibility initiative
 *
 * Revision 1.11  2004/07/28 01:05:28  blj
 * @scr 6495 updated status so that they all match.
 *
 * Revision 1.10  2004/07/27 18:06:12  epd
 * @scr 6499 Validation offline, tenders now added without validation per requirements
 *
 * Revision 1.9  2004/07/22 22:38:41  bwf
 * @scr 3676 Add tender display to ingenico.
 *
 * Revision 1.8  2004/07/19 17:45:39  jriggins
 * @scr 6026 Removed the Voided Gift Certificate dialog
 *
 * Revision 1.7  2004/07/17 21:14:34  jriggins
 * @scr 6026 Added logic for checking to see if the transaction for an issued gift certificate has been post voided
 *
 * Revision 1.6  2004/07/15 23:22:45  crain
 * @scr 5280 Gift Certificates issued in Training Mode can be Tendered outside of Training Mode
 *
 * Revision 1.5  2004/05/15 21:31:54  crain
 * @scr 4181 Wrong dialog screen
 *
 * Revision 1.4  2004/05/10 19:08:08  crain
 * @scr 4182 Able to use a Issued Gift Cert over and over, Tender Redeemed never appears
 *
 * Revision 1.3  2004/05/05 23:28:04  crain
 * @scr 4182 Able to use a Issued Gift Cert over and over, Tender Redeemed never appears
 *
 * Revision 1.2  2004/05/02 05:48:03  crain
 * @scr 4553 Redeem Gift Certificate
 *
 * Revision 1.1  2004/04/20 23:04:20  bwf
 * @scr 4263 Decomposition of gift certificate.
 *
 * Revision 1.7  2004/03/29 17:44:28  cdb
 * @scr 4204 Replacing tabs with spaces.
 *
 * Revision 1.6  2004/03/26 23:20:06  bjosserand
 * @scr 4093 Transaction Reentry
 * Revision 1.5 2004/03/26 20:20:49 crain @scr 4105 Foreign Currency
 *
 * Revision 1.4 2004/03/23 00:31:09 crain @scr 4105 Foreign Currency
 *
 * Revision 1.3 2004/02/12 16:48:22 mcs Forcing head revision
 *
 * Revision 1.2 2004/02/11 21:22:51 rhafernik @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1 2004/02/11 01:04:12 cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.2 Dec 02 2003 17:53:58 crain Modified flow Resolution for 3421: Tender redesign
 *
 * Rev 1.1 Nov 21 2003 09:28:38 epd refactor
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.giftcertificate;

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
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Attempts to add a gift certificate to the transaction
 */
@SuppressWarnings("serial")
public class GiftCertificateTenderActionSite extends PosSiteActionAdapter
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
                displayDialog(bus, DialogScreensIfc.ERROR, "ValidationOffline", args, "Offline");
                return;
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
