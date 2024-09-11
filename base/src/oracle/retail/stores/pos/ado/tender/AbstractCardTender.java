/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/AbstractCardTender.java /rgbustores_13.4x_generic_branch/2 2011/09/19 16:54:02 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       09/16/11 - remove unused expiration date
 *    asinton   09/14/10 - Fixed timeout issue in giftcard activation.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    asinton   03/18/09 - Changed the logic to test if a card was swiped
 *                         successfully, or not.
 *
 * ===========================================================================
 * $Log:
 *      10   360Commerce 1.9         6/2/2008 4:29:30 AM    Manikandan
 *           Chellapan CR#31866 Fixed check digit for cards by sending
 *           enciphered card data to check digit utility method.
 *      9    360Commerce 1.8         12/14/2007 8:59:59 AM  Alan N. Sinton  CR
 *           29761: Removed non-PABP compliant methods and modified card
 *           RuleIfc to take an instance of EncipheredCardData.
 *      8    360Commerce 1.7         12/4/2007 8:18:27 PM   Alan N. Sinton  CR
 *           29598: code changes per code review.
 *      7    360Commerce 1.6         11/21/2007 1:59:17 AM  Deepti Sharma   CR
 *           29598: changes for credit/debit PAPB
 *      6    360Commerce 1.5         8/24/2007 7:14:35 PM   Ranjan X Ojha   Fix
 *            for validationCardNumber for DebitCards.
 *      5    360Commerce 1.4         8/22/2007 11:53:37 AM  Ranjan X Ojha
 *           fixed validation of debit cardnumber.
 *      4    360Commerce 1.3         12/13/2005 4:42:32 PM  Barry A. Pape
 *           Base-lining of 7.1_LA
 *      3    360Commerce 1.2         3/31/2005 4:27:06 PM   Robert Pearse
 *      2    360Commerce 1.1         3/10/2005 10:19:26 AM  Robert Pearse
 *      1    360Commerce 1.0         2/11/2005 12:09:19 PM  Robert Pearse
 *     $
 *     Revision 1.8  2004/08/23 16:15:59  cdb
 *     @scr 4204 Removed tab characters
 *
 *     Revision 1.7  2004/08/05 21:12:50  blj
 *     @scr 6195 - corrected a problem with MSRModel data being lost for postvoids.
 *
 *     Revision 1.6  2004/07/31 16:09:37  bwf
 *     @scr 6551 Enable credit auth charge confirmation.
 *
 *     Revision 1.5  2004/07/15 16:13:22  kmcbride
 *     @scr 5954 (Services Impact): Adding logging to these ADOs, also fixed some
 *     exception handling issues.
 *
 *     Revision 1.4  2004/07/12 21:42:19  bwf
 *     @scr 6125 Made available expiration validation of debit before pin.
 *
 *     Revision 1.3  2004/07/12 20:38:20  kmcbride
 *     @scr 5954: Adding some logging to these classes.
 *
 *     Revision 1.2  2004/02/12 16:47:55  mcs
 *    Forcing head revision
 *
 *     Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *     updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.6   Feb 05 2004 13:46:08   rhafernik
 * log4j changes
 *
 *    Rev 1.5   Jan 28 2004 11:26:44   epd
 * Makes use of POS check digit utility
 *
 *    Rev 1.4   Jan 09 2004 13:37:36   epd
 * fixed but and paste problem
 *
 *    Rev 1.3   Jan 07 2004 15:25:56   epd
 * Refactoring to add MOD10 to debit
 *
 *    Rev 1.2   Dec 10 2003 10:16:34   epd
 * Makes use of new MSRModel interface
 *
 *    Rev 1.1   Dec 01 2003 19:06:20   epd
 * Updates for Credit/Debit
 *
 *    Rev 1.0   Nov 04 2003 11:13:08   epd
 * Initial revision.
 *
 *    Rev 1.1   Oct 30 2003 20:34:42   epd
 * Added check to prevent null pointer
 *
 *    Rev 1.0   Oct 17 2003 12:33:40   epd
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderDebitIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.device.MSRModelIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.utility.CheckDigitUtility;

/**
 * Represents swipable/authorizable card type tenders
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractCardTender extends AbstractTenderADO
{

    /** The MSR model, will be null if card not swiped. */
   protected MSRModelIfc msrModel;

    //  Adding logging support to this class, per
    //  ServicesImpact SCR #5954
    //
    private transient Logger logger = Logger.getLogger(AbstractCardTender.class);

    //------------------------------------------------------------------------------
    /**
     * Gets the track 2 data from the MSR model or
     * null if the card was not swiped or there is not track 1 data.
     * @return track 2 data
     */
    //------------------------------------------------------------------------------
    public byte[] getTrack2Data()
    {
        byte[] result = null;
        if (msrModel != null && msrModel.getEncipheredCardData() != null)
        {
            result = msrModel.getEncipheredCardData().getEncryptedTrack2Data();
        }
        else
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("No track 2 data found");
            }
        }

        return result;
    }

   //------------------------------------------------------------------------------
    /**
     * The name of the card bearer is parsed from the MSR model
     * and returned packaged in a PersonNameIfc instance.
     * @return the name of the card bearer
     */
   //------------------------------------------------------------------------------
    public PersonNameIfc getBearerName()
    {
        if (isCardSwiped())
        {
            return getBearerName(msrModel);
        }
        else
        {
            // return an empty instance
            return DomainGateway.getFactory().getPersonNameInstance();
        }
    }

   //------------------------------------------------------------------------------
    /**
     * Internal method used to parse a person name from an MSRModel
     * @param model
     * @return
     */
   //------------------------------------------------------------------------------
    protected PersonNameIfc getBearerName(MSRModelIfc model)
    {
        PersonNameIfc name = DomainGateway.getFactory().getPersonNameInstance();

        if (isCardSwiped())
        {
            if(model.getFirstName() != null)
            {
                name.setFirstName(model.getFirstName());
            }

            if(model.getSurname() != null)
            {
                name.setSurname(model.getSurname());
                name.setLastName(model.getSurname());
            }
            String mi = model.getMiddleInitial();

            if (mi != null && !mi.equals(""))
            {
                name.setMiddleInitial(mi.charAt(0));
            }

            if(model.getSuffix() != null)
            {
                name.setNameSuffix(model.getSuffix());
            }

            if(model.getTitle() != null)
            {
                name.setTitle(model.getTitle());
            }
        }
        return name;
    }

    //------------------------------------------------------------------------------
    /**
     * Returns a boolean indicating whether or not the
     * card was swiped.  This decision is based on whether
     * we have an MSR model or not.
     * @return card swiped boolean flag.
     */
    //------------------------------------------------------------------------------
    public boolean isCardSwiped()
    {
        // Adding some debug logging here
        //
        if(msrModel == null && logger.isDebugEnabled())
        {
            logger.debug("No MSR Model, assuming card NOT swiped");
        }

        return (msrModel == null) ? false : true;
    }

    //------------------------------------------------------------------------------
    /**
     * Validates that the MSR data read from the card is valid
     * @throws TenderException
     */
    //-------------------------------------------------------------------------------
    protected void validateMSRData() throws TenderException
    {
        if (isCardSwiped())
        {
            // if any of the following is null, error
            // Base check on the data from EncipheredCardData
            String truncatedNumber = null;
            String expiryDate = null;
            byte[] track2Data = null;
            if(msrModel.getEncipheredCardData() != null)
            {
                EncipheredCardDataIfc cardData = msrModel.getEncipheredCardData();
                truncatedNumber = cardData.getTruncatedAcctNumber();
                expiryDate = cardData.getEncryptedExpirationDate();
                track2Data = cardData.getEncryptedTrack2Data();
            }
            else
            {
                // if there's no encrypted data then not swipe?
                throw new TenderException("Bad Mag stripe", TenderErrorCodeEnum.BAD_MAG_SWIPE);
            }
            if (truncatedNumber == null || truncatedNumber.equals("") || // has a number...
                    truncatedNumber.length() < 10 || // ... that is at least 10 digits,
                    expiryDate == null || expiryDate.equals("") || // has an exp date...
                    track2Data == null || // has some track2 data ...
                        (track2Data != null && track2Data.length == 0)) // ... that is non-zero in length
           {
               throw new TenderException("Bad Mag stripe", TenderErrorCodeEnum.BAD_MAG_SWIPE);
           }
        }

    }

    //------------------------------------------------------------------------------
    /**
     * Validate the card number using MOD10 algorithm.
     * @throws TenderException Thrown when card fails MOD10 check.
     */
    //------------------------------------------------------------------------------
    protected void validateCardNumber() throws TenderException
    {
        UtilityManagerIfc utility = (UtilityManagerIfc)getContext().getManager(UtilityManagerIfc.TYPE);
        if (tenderRDO instanceof TenderDebitIfc)
        {
            boolean valid = utility.validateCheckDigit(CheckDigitUtility.CHECK_DIGIT_FUNCTION_DEBIT,
                ((TenderChargeIfc)tenderRDO).getEncipheredCardData());
            if (!valid)
            {
                throw new TenderException("Invalid Card Number", TenderErrorCodeEnum.INVALID_CARD_NUMBER);
            }
        }
        else if (tenderRDO instanceof TenderChargeIfc)
        {

            boolean valid = utility.validateCheckDigit(CheckDigitUtility.CHECK_DIGIT_FUNCTION_CREDIT,
                                                        ((TenderChargeIfc)tenderRDO).getEncipheredCardData());
            if (!valid)
            {
                throw new TenderException("Invalid Card Number", TenderErrorCodeEnum.INVALID_CARD_NUMBER);
            }
        }
        else if (tenderRDO instanceof TenderGiftCertificateIfc)
        {
            boolean valid = utility.validateCheckDigit(CheckDigitUtility.CHECK_DIGIT_FUNCTION_CREDIT,
                                                       ((TenderGiftCertificateIfc)tenderRDO).getGiftCertificateNumber());
            if (!valid)
            {
                throw new TenderException("Invalid Card Number", TenderErrorCodeEnum.INVALID_CARD_NUMBER);
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    /**
     * This is method is used to create the MSRModel from the
     * RDO.  In the legacy code, the current card tenders, GiftCard,
     * Debit and Credit, implement TenderChargeIfc.  Subclasses
     * will need to implement TenderChargeIfc in their RDO objects
     * as well.
     * @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
     */
    //----------------------------------------------------------------------------------------------
    public void fromLegacy(EYSDomainIfc rdo)
    {
        assert rdo instanceof TenderChargeIfc;
        tenderRDO = (TenderChargeIfc)rdo;

        // Only create the MSRModel if card was swiped.
        if (((TenderChargeIfc)tenderRDO).getTrack2Data() != null &&
                msrModel == null)
        {
            msrModel = new MSRModel();
            msrModel.setTrack2Data(((TenderChargeIfc)tenderRDO).getTrack2Data());

            msrModel.setAccountNumber(((TenderChargeIfc)tenderRDO).getCardNumber());
            msrModel.setExpirationDate(((TenderChargeIfc)tenderRDO).getExpirationDateString());
            PersonNameIfc name = DomainGateway.getFactory().getPersonNameInstance();

            name = ((TenderChargeIfc)tenderRDO).getBearerName();

            if (name != null)
            {
                msrModel.setFirstName(name.getFirstName());
                msrModel.setSurname(name.getSurname());
                StringBuffer sb = new StringBuffer();
                sb.append(name.getMiddleInitial());
                msrModel.setMiddleInitial(new String(sb));
                msrModel.setTitle(name.getTitle());
                msrModel.setSuffix(name.getNameSuffix());
            }
        }
    }
}
