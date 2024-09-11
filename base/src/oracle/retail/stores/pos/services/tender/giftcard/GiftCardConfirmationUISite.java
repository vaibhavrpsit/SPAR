/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/giftcard/GiftCardConfirmationUISite.java /rgbustores_13.4x_generic_branch/2 2011/09/16 10:48:09 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     09/15/11 - Fixed issues with completely creating of a refund
 *                         gift card tender from a issue/reload response
 *                         object.
 *    cgreene   06/07/11 - update to first pass of removing pospal project
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   03/18/10 - formatting
 *    abondala  01/03/10 - update header date
 *    asinton   02/24/09 - Honor thy customer's preferred language.
 *
 * ===========================================================================
 * $Log:
 *    9    360Commerce 1.8         5/14/2008 11:44:43 AM  Sameer Thajudin This
 *         is fix as a result of an issue faced at production.
 *         The version refers to 7.x.
 *    8    360Commerce 1.7         2/27/2008 3:19:23 PM   Alan N. Sinton  CR
 *         29989: Changed masked to truncated for UI renders of PAN.
 *    7    360Commerce 1.6         1/17/2008 5:24:06 PM   Alan N. Sinton  CR
 *         29954: Refactor of EncipheredCardData to implement interface and be
 *          instantiated using a factory.
 *    6    360Commerce 1.5         12/12/2007 6:47:38 PM  Alan N. Sinton  CR
 *         29761: FR 8: Prevent repeated decryption of PAN data.
 *    5    360Commerce 1.4         11/21/2007 1:59:17 AM  Deepti Sharma   CR
 *         29598: changes for credit/debit PAPB
 *    4    360Commerce 1.3         7/10/2007 11:22:44 AM  Michael Boyd
 *         Michael Wisbauer Added formating to the amount before displaying on
 *          pin pad
 *    3    360Commerce 1.2         3/31/2005 4:28:16 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:53 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:13 PM  Robert Pearse   
 *
 *   Revision 1.4.2.1  2004/11/18 20:06:04  bwf
 *   @scr 6552 Call correct CPOI screens during tender process for swipe anytime.
 *
 *   Revision 1.4  2004/09/17 23:00:01  rzurga
 *   @scr 7218 Move CPOI screen name constants to CIDAction to make it more generic
 *
 *   Revision 1.3  2004/08/02 23:15:43  bwf
 *   @scr 6551 Fixed for noningenico users.
 *
 *   Revision 1.2  2004/07/31 18:29:16  bwf
 *   @scr 6551 Enable credit auth charge confirmation.
 *
 *   Revision 1.1  2004/07/30 21:56:40  bwf
 *   @scr 6551 Fix debit and put gift card charge confirmation.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.giftcard;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;

/**
 * This method displays the gift card charge confirmation.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
public class GiftCardConfirmationUISite extends PosSiteActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = 6674279629814123164L;

    /**
     * This method displays the gift card confirmation screen.
     * 
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {    
        try
        {
            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            TenderCargo cargo = (TenderCargo)bus.getCargo();
            Locale locale = LocaleMap.getBestMatch(LocaleMap.getLocale(LocaleConstantsIfc.DEVICES));
            if(cargo.getTenderableTransaction() != null &&
                    cargo.getTenderableTransaction().getCustomer() != null &&
                    cargo.getTenderableTransaction().getCustomer().getPreferredLocale() != null)
            {
                locale = cargo.getTenderableTransaction().getCustomer().getPreferredLocale();
            }
            String message = utility.retrieveCommonText("giftCardConfirmation",
                                                        "Authorize Charge Amount:\n {0}\n Gift Card # {1}",
                                                        locale);

            String cardNumber = ((String)cargo.getTenderAttributes().get(TenderConstants.NUMBER));
            EncipheredCardDataIfc cardData = (EncipheredCardDataIfc)cargo.getTenderAttributes().get(TenderConstants.ENCIPHERED_CARD_DATA);
            String truncatedNumber = cardData.getTruncatedAcctNumber();
            // if there is no card number, get it from the msr model
            if (cardNumber == null)
            {
                MSRModel msrModel = (MSRModel)cargo.getTenderAttributes().get(TenderConstants.MSR_MODEL);
                cardNumber = msrModel.getAccountNumber();
                truncatedNumber = msrModel.getEncipheredCardData().getTruncatedAcctNumber();
            }

            // create the tender
            TenderADOIfc tender = cargo.getTenderADO();
            String amountString = tender.getAmount().toFormattedString();
            
            String[] parms = {amountString, 
                    truncatedNumber.substring(truncatedNumber.length()-4)};
            message = LocaleUtilities.formatComplexMessage(message,parms);
             
            logger.info(message);

            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
        catch (Exception e)
        {
            logger.warn(e);
            // convert to gift card anyway
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }        
    }
    
    /**
     * Clear the message.
     * 
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void depart(BusIfc bus)
    {
        if(bus.getCurrentLetter().getName().equalsIgnoreCase("Cancel"))
        {
            // nullify swipe anytime card when canceling
            ((TenderCargo)bus.getCargo()).setPreTenderMSRModel(null);
        }
    }
}
