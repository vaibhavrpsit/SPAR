/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/CheckForSignatureCaptureSite.java /main/6 2014/03/19 13:57:05 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   03/19/14 - Changed isSignatureRequiredLogic(). Prefer the flags
 *                         in the ICCDetails record over the authResponse's
 *                         flag. Servebase always returns true in the
 *                         authResponse.
 *    blarsen   02/13/14 - Refactor logic for determining if signature is
 *                         required. Other classes also have this business
 *                         logic (but not consistent logic).
 *    blarsen   01/22/14 - If the bank says to capture signature, then capture
 *                         it.
 *    asinton   01/22/14 - refactored how signatureRequired flag is generated
 *                         for MPOS in an AJB environment
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    cgreene   10/24/11 - fixed possible setting sig-req to false after
 *                         Serverbase set to true
 *    cgreene   10/24/11 - check for null amount before testing signum
 *    asinton   10/06/11 - prevent the signature capture when credit disclosure
 *                         data is present because a credit slip will be
 *                         printed
 *    jswan     09/28/11 - Modified to force signature capture for manually
 *                         approved credit tenders.
 *    jswan     09/27/11 - Fixed an issue with printing a manual signature slip
 *                         when the CPOI sig cap fails.
 *    jswan     09/22/11 - Rework to prevent gift cards and debit cards from
 *                         requiring a signature.
 *    jswan     09/21/11 - Fix failure to request signature capture.
 *    cgreene   09/09/11 - check sigcap requirement from response first, then
 *                         check if params require it.
 *    blarsen   08/26/11 - Added check for case when the auth service says sig
 *                         cap is required.
 *    jswan     08/26/11 - Modified to prevent signature capture for Re-entry
 *                         transactionsactions.
 *    sgu       08/12/11 - check null pointer for tender sub type
 *    ohorne    08/09/11 - APF:foreign currency support
 *    blarsen   07/15/11 - Removing reversal check since reversals no longer
 *                         use the authorization service.
 *    blarsen   07/12/11 - prevent sig caps for credit reversal requests (they
 *                         come through the auth service too)
 *    jswan     06/29/11 - Added to support signature capture in APF.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.authorization;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc.TenderType;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc.AuthorizationMethod;
import oracle.retail.stores.domain.utility.CardTypeCodesIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

import org.apache.commons.lang3.StringUtils;

/**
 * This site checks to see the amount charged to the card type triggers
 * signature capture.
 *
 * @author jswan
 * @since 13.4
 */
@SuppressWarnings("serial")
public class CheckForSignatureCaptureSite extends PosSiteActionAdapter
{
    /** Constant for Approval letter */
    public static final String REQUIRED_LETTER = "Required";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        AuthorizationCargo cargo = (AuthorizationCargo)bus.getCargo();

        String letter = CommonLetterIfc.SKIP;
        AuthorizeTransferResponseIfc response = cargo.getCurrentResponse();

        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);

        boolean signatureRequired = isSignatureRequired(pm, response, cargo.getRegister().getWorkstation().isTransReentryMode());

        // If the signature is required, mail the letter.
        letter = calculateLetterForSignatureRequired(bus, signatureRequired, letter);

        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }

    /**
     * Is a signature required for this tender?
     *
     * @param pm
     * @param response
     * @param isReentryMode
     * @return true if signature is required.
     */
    static public boolean isSignatureRequired(ParameterManagerIfc pm, AuthorizeTransferResponseIfc response, boolean isReentryMode)
    {
        boolean signatureRequired = false;
        CurrencyIfc amount = response.getBaseAmount();


        // If this is a valid ICC Details object, get the signature required value
        // from that object.
        if (response.getICCDetails().getApplicationID() != null)
        {
            signatureRequired = response.getICCDetails().isSignatureRequired();
        }
        // if the bank's response requests a sigcap, then require it
        else if (response.isSignatureRequired())
        {
            signatureRequired = true;
        }
        // If the tender type is credit AND the tender amount is zero or greater AND
        // the register is NOT in re-entry mode, check the signature capture
        // minimum amount parameter.
        else if (TenderType.CREDIT.equals(response.getTenderType()) &&
             (amount != null && amount.signum() > CurrencyIfc.NEGATIVE) &&
             !isReentryMode)
        {
            String tenderSubType = response.getTenderSubType();
            if (AuthorizationMethod.Manual.equals(response.getAuthorizationMethod()))
            {
                signatureRequired = true;
            }
            else if(CardTypeCodesIfc.HOUSE_CARD.equals(tenderSubType) == false)
            {
                CurrencyIfc signatureLimitAmount = getSignatureLimit(pm, response.getTenderSubType());

                signatureRequired = isSignatureRequiredFromParameters(amount, signatureLimitAmount);
            }
            // House card implied, need signature capture unless promotional
            // details are present in which case a signature slip will be printed.
            else if(isHouseAccountWithoutPromotion(response))
            {
                signatureRequired = true;
            }

            // Resetting this value in case the CPOI signature capture fails.
            if (signatureRequired && !response.isSignatureRequired())
            {
                response.setSignatureRequired(true);
                // otherwise setting to false here might step on Chip and PIN code that set this to true
            }
        }
        return signatureRequired;
    }

    /**
     * If <code>signatureRequired</code> then return appropriate letter, else return original <code>letter</code>.
     * @param bus the tour bus
     * @param signatureRequired boolean flag to indicate if signature is required
     * @param letter the original letter
     * @return the calculated result letter
     */
    protected String calculateLetterForSignatureRequired(BusIfc bus, boolean signatureRequired, String letter)
    {
        String returnLetter = letter;
        if (signatureRequired)
        {
            returnLetter = REQUIRED_LETTER;
        }
        return returnLetter;
    }

    /**
     * Calculates if signature required based on parameters.
     * @param bus
     * @param response
     * @param signatureRequired
     * @param amount
     * @param tenderSubType
     * @return boolean value for signature required based on parameters
     */
    static protected boolean isSignatureRequiredFromParameters(CurrencyIfc amount, CurrencyIfc signatureLimitAmount)
    {
        boolean signatureRequired = false;
        if (signatureLimitAmount != null)
        {
            // if tender amount is great than or equal to the MinimumSigCapFor+<credit type>
            // parameter amount, the signature is required.
            if(amount.compareTo(signatureLimitAmount) != CurrencyIfc.LESS_THAN)
            {
                signatureRequired = true;
            }
        }
        else
        {
            signatureRequired = true;
        }
        return signatureRequired;
    }

    /**
     * Returns true if the response indicates the tender sub type is HouseCard
     * and the response contains promotion disclosure data.
     *
     * @param response
     * @return true if the response indicates the tender sub type is HouseCard
     * and the response contains promotion disclosure data.
     */
    static protected boolean isHouseAccountWithoutPromotion(AuthorizeTransferResponseIfc response)
    {
        boolean isHouseCard = CardTypeCodesIfc.HOUSE_CARD.equals(response.getTenderSubType());
        boolean doesNotPromotionalData = StringUtils.isEmpty(response.getPromotionDescription());
        return isHouseCard && doesNotPromotionalData;
    }

    /**
     * Gets the signature limit amount for the specified tender subtype.
     * Tender amounts above this limit may require a signature
     *
     * @param pm
     * @param tenderSubType
     * @return
     */
    static public CurrencyIfc getSignatureLimit(ParameterManagerIfc pm, String tenderSubType)
    {
        CurrencyIfc signatureLimitAmount = null;

        try
        {
            if (StringUtils.isNotEmpty(tenderSubType))
            {
                // Get Minimum signature capture amount for this card type.
                String parmValue = pm.getStringValue(ParameterConstantsIfc.
                        TENDERAUTHORIZATION_MinimumSigCapFor_PREFIX + tenderSubType);
                signatureLimitAmount = DomainGateway.getBaseCurrencyInstance(parmValue);
            }
        }
        catch(ParameterException pe)
        {
            logger.warn("Unable to obtain parameter value for " + ParameterConstantsIfc.TENDERAUTHORIZATION_MinimumSigCapFor_PREFIX + tenderSubType, pe);
        }
        return signatureLimitAmount;
    }


}
