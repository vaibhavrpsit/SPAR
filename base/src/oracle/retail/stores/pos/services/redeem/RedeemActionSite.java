/* ===========================================================================
* Copyright (c) 2004, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/RedeemActionSite.java /main/22 2013/02/28 15:30:04 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     02/28/13 - Forward Port Print trace number on receipt for gift
 *                         cards, required by ACI.
 *    asinton   02/06/13 - capture the authorization code when returning to the
 *                         redeem giftcard service
 *    rgour     01/30/13 - gift card redeem transaction's approval code is
 *                         stored in database
 *    rabhawsa  08/20/12 - removed placeholder from key TenderRedeemed
 *    rabhawsa  08/16/12 - wptg - removed placeholder from key
 *                         InvalidCertificateError
 *    icole     08/02/12 - Correct problem of being able to redeem a gift
 *                         certificate for more than the face value.
 *    jswan     04/11/12 - Modified to support centralized validation of gift
 *                         certificates and store credits.
 *    jswan     03/26/12 - Modified to support centralized gift certificate and
 *                         store credit.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/09/10 - optimize calls to LocaleMAp
 *    acadar    04/08/10 - merge to tip
 *    acadar    04/06/10 - use default locale for currency, date and time
 *                         display
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    acadar    04/01/10 - use default locale for currency display
 *    jswan     02/09/10 - Modified to journal Redeemed rather than Tendered.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         3/19/2008 4:46:01 AM   Manikandan Chellapan
 *         CR#30651 Fixed cancel gift card redeem transaction. Code reviewed
 *         by Naveen Ganesh.
 *    4    360Commerce 1.3         1/25/2006 4:11:41 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:29:36 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:35 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:35 PM  Robert Pearse
 *:
 *    5    .v700     1.2.1.1     11/22/2005 09:05:30    Deepanshu       CR
 *         5361: Added if block to the fix to check TendorADO is an instance of
 *         TenderGiftCardADO
 *    4    .v700     1.2.1.0     11/17/2005 17:41:35    Deepanshu       CR
 *         5361: Set the tender attributes for original balance and remaining
 *         balance
 *    3    360Commerce1.2         3/31/2005 15:29:36     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:24:35     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:13:35     Robert Pearse
 *
 *   Revision 1.17.2.1  2004/11/12 14:28:53  kll
 *   @scr 7337: JournalFactory extensibility initiative
 *
 *   Revision 1.17  2004/08/16 19:44:40  blj
 *   @scr 5314 - added new screen for invalid store credits.
 *
 *   Revision 1.16  2004/05/25 15:11:41  blj
 *   @scr 5115 - resolution for printing issues
 *
 *   Revision 1.15  2004/05/20 19:48:52  crain
 *   @scr 5108 Tender Redeem_Redeem Foreign Gift Certificate Receipt Incorrect
 *
 *   Revision 1.14  2004/05/10 19:08:08  crain
 *   @scr 4182 Able to use a Issued Gift Cert over and over, Tender Redeemed never appears
 *
 *   Revision 1.13  2004/05/05 23:28:04  crain
 *   @scr 4182 Able to use a Issued Gift Cert over and over, Tender Redeemed never appears
 *
 *   Revision 1.12  2004/05/03 19:29:46  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.11  2004/04/30 21:04:56  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.10  2004/04/29 23:48:50  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.9  2004/04/22 22:35:55  blj
 *   @scr 3872 - more cleanup
 *
 *   Revision 1.8  2004/04/12 18:37:47  blj
 *   @scr 3872 - fixed a problem with validation occuring after foreign currency has been converted.
 *
 *   Revision 1.7  2004/04/07 22:49:40  blj
 *   @scr 3872 - fixed problems with foreign currency, fixed ui labels, redesigned to do validation and adding tender to transaction in separate sites.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.redeem;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.tender.TenderCertificateIfc;
import oracle.retail.stores.domain.tender.TenderGiftCardIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderGiftCardADO;
import oracle.retail.stores.pos.ado.tender.TenderGiftCertificateADO;
import oracle.retail.stores.pos.ado.tender.TenderStoreCreditADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 *
 * This action site will attempt to add a redeem tender to the redeem
 * transaction.
 */
public class RedeemActionSite extends PosSiteActionAdapter
{

    /**
     *
     */
    private static final long serialVersionUID = -2406509662639304567L;

    //--------------------------------------------------------------------------
    /**

     This site displays the Redeem Number Site and
     collects this number from the ui in the depart method.
     @param bus the bus arriving at this site
     **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        RedeemCargo cargo = (RedeemCargo)bus.getCargo();

        TenderADOIfc redeemTender = null;

        // Update tenderAttributes with redeem state
        cargo.getTenderAttributes().put(TenderConstants.STATE, TenderCertificateIfc.REDEEMED);

        Letter letter = new Letter(CommonLetterIfc.SUCCESS);

        try
        {
            redeemTender = cargo.getTenderADO();
            if (redeemTender == null)
            {
                TenderFactoryIfc factory = (TenderFactoryIfc)ADOFactoryComplex.getFactory("factory.tender");
                redeemTender = factory.createTender(cargo.getTenderAttributes());
            }

            // set the tender attributes again in case the amounts
            // have been changed by foreign currency or discounted
            if(redeemTender instanceof TenderGiftCardADO)
            {
                GiftCardIfc giftCard = cargo.getGiftCard();
                // transfer the authcode from the response gift card to the redeem tender giftcard
                ((TenderGiftCardIfc)(((TenderGiftCardADO)redeemTender).toLegacy())).getGiftCard().setApprovalCode(giftCard.getApprovalCode());
                ((TenderGiftCardIfc)(((TenderGiftCardADO)redeemTender).toLegacy())).getGiftCard().setEncipheredCardData(giftCard.getEncipheredCardData());
                ((TenderGiftCardIfc)(((TenderGiftCardADO)redeemTender).toLegacy())).getGiftCard().setTraceNumber(giftCard.getTraceNumber());

                // use default locale for currency and date time display
                cargo.getTenderAttributes().put(
                    TenderConstants.ORIGINAL_BALANCE,
                    giftCard.getInitialBalance().toFormattedString());
                cargo.getTenderAttributes().put(
                    TenderConstants.REMAINING_BALANCE,
                    giftCard.getCurrentBalance().toFormattedString());
                cargo.getTenderAttributes().put(TenderConstants.AUTH_CODE, giftCard.getApprovalCode());
                cargo.getTenderAttributes().put(TenderConstants.TRACE_NUMBER, giftCard.getTraceNumber());
                // set card data to tender
                cargo.getTenderAttributes().put(TenderConstants.ENCIPHERED_CARD_DATA, cargo.getGiftCard().getEncipheredCardData());
            }

            // Validate and Add the redeem tender to the transaction.
            RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
            txnADO.addRedeemTender(redeemTender);

            // journal the redeemed tender
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
            registerJournal.journal(redeemTender, JournalFamilyEnum.TENDER, JournalActionEnum.REDEEM);

        }
        catch (ADOException adoe)
        {
            adoe.printStackTrace();
        }
        catch (TenderException te)
        {
            TenderErrorCodeEnum error = te.getErrorCode();

            // save tender in cargo
            cargo.setTenderADO(redeemTender);
            UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

            if (error == TenderErrorCodeEnum.CERTIFICATE_TENDERED)
            {
                String args[] = new String[2];

                args[0] = (String)redeemTender.getTenderAttributes().get(TenderConstants.NUMBER);
                args[1] = (String)redeemTender.getTenderAttributes().get(TenderConstants.REDEEM_DATE);
                displayDialog(bus, DialogScreensIfc.ACKNOWLEDGEMENT, "TenderRedeemed", args, "Invalid");
                return;
            }
            else if (error == TenderErrorCodeEnum.INVALID_NUMBER)
            {
                 Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
                 String[] args = new String[] {utility.retrieveDialogText(cargo.getRedeemTypeSelected(), cargo.getRedeemTypeSelected())};
                 args[0] = args[0].toLowerCase(locale);
                 displayDialog(bus, DialogScreensIfc.ACKNOWLEDGEMENT, "InvalidNumberError", args, "Invalid");
                 return;
            }
            else if (error == TenderErrorCodeEnum.INVALID_CERTIFICATE
                    || error == TenderErrorCodeEnum.CERTIFICATE_VOIDED)
            {
                if (redeemTender instanceof TenderGiftCertificateADO)
                {
                    String args[] = new String[1];
                    displayDialog(bus, DialogScreensIfc.ERROR, "InvalidCertificateError", args, "Invalid");
                }
                else
                {
                    displayDialog(bus, DialogScreensIfc.ERROR, "InvalidStoreCreditError", null, "Invalid");
                }
                return;
            }
            else if (error == TenderErrorCodeEnum.INVALID_CURRENCY)
            {
                 if (redeemTender instanceof TenderStoreCreditADO)
                 {
                     displayDialog(bus, DialogScreensIfc.ERROR, "InvalidStoreCreditCurrency", null, "Invalid");
                 }
                 else
                 {
                     displayDialog(bus, DialogScreensIfc.ERROR, "InvalidGiftCertificateCurrency", null, "Invalid");
                 }
                 return;
            }
            else if (error == TenderErrorCodeEnum.VALIDATION_OFFLINE)
            {
                 String args[] = { utility.retrieveDialogText(cargo.getRedeemTypeSelected(), cargo.getRedeemTypeSelected()) };
                 displayDialog(bus, DialogScreensIfc.ERROR, "ValidationOffline", args, "Success");
                 return;
            }

        }
        bus.mail(letter, BusIfc.CURRENT);
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
        if (letter != null)
        {
            UIUtilities.setDialogModel(ui, screenType, message, args, letter);
        }
        else
        {
            UIUtilities.setDialogModel(ui, screenType, message, args);
        }
    }

}
