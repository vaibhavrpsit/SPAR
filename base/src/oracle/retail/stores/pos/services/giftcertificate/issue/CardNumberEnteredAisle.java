/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/giftcertificate/issue/CardNumberEnteredAisle.java /main/16 2011/12/05 12:16:10 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    jswan     09/09/10 - Initialized gift certificate item department ID to 0
 *                         to maintain backward compatibility.
 *    jswan     08/17/10 - Added a group classification ID for Gift
 *                         Certificates; this allows sale return lines
 *                         containing a gift certificate to suspended and
 *                         retrieved correctly.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    mdecama   02/18/09 - Retrieving the GiftCertificateDescription for all
 *                         the supported languages. These descriptions will be
 *                         used in the user and customer UIs.
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         7/25/2007 4:45:17 PM   Alan N. Sinton  CR
 *         27675 Check if Gift Certificate number was already issued.
 *    7    360Commerce 1.6         7/24/2006 6:17:20 PM   Robert Zurga    CR
 *         6101 Merge
 *    6    360Commerce 1.5         7/24/2006 5:54:39 PM   Robert Zurga
 *         Restore previous version prior to merge of 6101
 *    5    360Commerce 1.4         7/20/2006 8:20:27 PM   Robert Zurga    Merge
 *          from CardNumberEnteredAisle.java, Revision 1.3.1.0
 *    4    360Commerce 1.3         1/22/2006 11:45:07 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:27:21 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:19:59 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:49 PM  Robert Pearse
 *
 *   Revision 1.10  2004/07/15 23:22:45  crain
 *   @scr 5280 Gift Certificates issued in Training Mode can be Tendered outside of Training Mode
 *
 *   Revision 1.9  2004/07/02 19:11:57  aschenk
 *   @scr 5633 - Added a check for re-entry mode.  The gift cert is no longer validated when in re-entry mode.
 *
 *   Revision 1.8  2004/05/18 23:33:10  crain
 *   @scr 4161 Receipts for Gift Certs Issued missing Entry Method
 *
 *   Revision 1.7  2004/05/18 22:16:59  crain
 *   @scr 4939 Check Digit_INVALID_NUMBER_message text incorrect
 *
 *   Revision 1.6  2004/05/14 19:18:34  crain
 *   @scr 5077 Employee discount
 *
 *   Revision 1.5  2004/05/11 02:07:40  crain
 *   @scr 4488 Select Cancel from issue amount screen causes system to issue a GCert for 00.00
 *
 *   Revision 1.4  2004/04/08 21:12:41  tfritz
 *   @scr 3884 - Do not do check digit validation when in training mode.
 *
 *   Revision 1.3  2004/03/10 02:38:24  crain
 *   @scr 3814 Issue Gift Certificate
 *
 *   Revision 1.2  2004/03/03 23:15:14  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.1  2004/02/20 14:15:17  crain
 *   @scr 3814 Issue Gift Certificate
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.giftcertificate.issue;

import java.math.BigDecimal;
import java.util.Locale;

import oracle.retail.stores.common.utility.LanguageResourceBundleUtil;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.CertificateTransaction;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.stock.GiftCertificateItemIfc;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.sale.SaleCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.utility.CheckDigitUtility;
import oracle.retail.stores.utility.I18NConstantsIfc;

/**
 * This aisle retrieves the gift certificate number.
 *
 * @version $Revision: /main/16 $
 */
public class CardNumberEnteredAisle extends PosLaneActionAdapter
{
    /**
     * Generated Serial Version UID
     */
    private static final long serialVersionUID = -2355038942370847805L;

    /**
     * gift certificate description String
     */
    public static final String DESCRIPTION = "Common.GiftCertificateDescription";
    /**
     * gift certificate tag
     */
    public static final String GIFT_CERTIFICATE_TAG = "GiftCertificate";
    /**
     * gift certificate text
     */
    public static final String GIFT_CERTIFICATE = "Gift Certificate";
    /**
     * Invalid gift certificate number
     */
    public static final String INVALID_GIFT_CERTIFICATE_NUMBER = "Invalid gift certificate number";
    /**
     * Gift certification number alread used.
     */
    public static final String GIFT_CERTIFICATE_NUMBER_ALREADY_USED = "Gift certificate number already used";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        /*
         * read the data from the UI
         */
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        SaleCargo cargo = (SaleCargo) bus.getCargo();
        GiftCertificateItemIfc gc = DomainGateway.getFactory().getGiftCertificateItemInstance();
        gc.setTrainingMode(cargo.getTransaction().isTrainingMode());
        gc.getItemClassification().getGroup().setGroupID(ProductGroupConstantsIfc.PRODUCT_GROUP_GIFT_CERTIFICATE);
        LetterIfc letter = null;

        String gcNumber = ui.getInput().trim();
        try
        {

            // Do not validate check digit in Training Mode
            if (!cargo.getRegister().getWorkstation().isTrainingMode() && !cargo.getRegister().getWorkstation().isTransReentryMode())
            {
                validateNumber(gcNumber);
            }

            captureGiftCertificateData(utility, ui, cargo, gc, gcNumber);

            letter = new Letter(CommonLetterIfc.CONTINUE);
        }
        catch(DataException de)
        {
            try
            {
                captureGiftCertificateData(utility, ui, cargo, gc, gcNumber);
                String[] args = new String[] { de.getMessage() };
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ACKNOWLEDGEMENT, "DATABASE_ERROR", args, CommonLetterIfc.CONTINUE);
            }
            catch(Exception e)
            {
                String[] args = new String[] {utility.retrieveDialogText(GIFT_CERTIFICATE_TAG,
                        GIFT_CERTIFICATE).toLowerCase(locale)};
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ACKNOWLEDGEMENT, "InvalidNumberError", args);
            }
        }
        catch (Exception e)
        {
            String[] args = new String[] {utility.retrieveDialogText(GIFT_CERTIFICATE_TAG,
                    GIFT_CERTIFICATE).toLowerCase(locale)};
            if(INVALID_GIFT_CERTIFICATE_NUMBER.equals(e.getMessage()))
            {
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ACKNOWLEDGEMENT, "InvalidNumberError", args);
            }
            else if(GIFT_CERTIFICATE_NUMBER_ALREADY_USED.equals(e.getMessage()))
            {
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ACKNOWLEDGEMENT, "ALREADY_ISSUED", args);
            }
        }
        if (letter != null)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    }

    /**
     * Captures the data for the gift certificate.
     * @param utility
     * @param ui
     * @param cargo
     * @param giftCertificate
     * @param number
     * @throws Exception
     */
    protected void captureGiftCertificateData(
            UtilityManagerIfc utility,
            POSUIManagerIfc ui,
            SaleCargo cargo,
            GiftCertificateItemIfc giftCertificate,
            String number)
    throws Exception
    {
        giftCertificate.setNumber(number);

        LocalizedTextIfc localizedDescriptions = LanguageResourceBundleUtil.getSupportedLocaleStrings(I18NConstantsIfc.COMMON_BUNDLE, DESCRIPTION);
        giftCertificate.setLocalizedDescriptions(localizedDescriptions);
        // Gift certificates don't really have a department, but setting
        // it to '0' prevents problems with POSLog down the line.
        giftCertificate.setDepartmentID("0");

        boolean isScanned = ((POSBaseBeanModel)ui.getModel()).getPromptAndResponseModel().isScanned();
        if (isScanned)
        {
            giftCertificate.setEntryMethod(EntryMethod.Swipe);
        }
        else
        {
            giftCertificate.setEntryMethod(EntryMethod.Manual);
        }
        
        cargo.setPLUItem(giftCertificate);
        // set the quantity to 0 for cancel and undo
        cargo.setItemQuantity(BigDecimal.ZERO);
    }

    /**
     * Validates the gift certificate number.
     * 
     * @param value gift certificate number
     * @exception Exception
     */
    protected void validateNumber(String value) throws DataException, Exception
    {
        UtilityManagerIfc utility = (UtilityManagerIfc) Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        if (!utility.validateCheckDigit(CheckDigitUtility.CHECK_DIGIT_FUNCTION_CERTIFICATE_MOD9,
                                         value)
            &&!utility.validateCheckDigit(CheckDigitUtility.CHECK_DIGIT_FUNCTION_CERTIFICATE_MOD10,
                                         value))
        {
            throw new Exception(INVALID_GIFT_CERTIFICATE_NUMBER);
        }

        CertificateTransaction dataTransaction =
            (CertificateTransaction) DataTransactionFactory.create(DataTransactionKeys.CERTIFICATE_TRANSACTION);
        TenderGiftCertificateIfc certificate =
            DomainGateway.getFactory().getTenderGiftCertificateInstance();
        certificate.setGiftCertificateNumber(value);
        try
        {
            certificate = dataTransaction.readGiftCertificateIssued(certificate);
        }
        catch(DataException de)
        {
            if(de.getErrorCode() != DataException.NO_DATA)
            {
                throw de;
            }
            
            certificate = null;
        }
        if(certificate != null)
        {
            throw new Exception(GIFT_CERTIFICATE_NUMBER_ALREADY_USED);
        }
    }
}
