/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/TenderGiftCertificateADO.java /main/22 2013/10/15 13:11:45 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     10/15/13 - Fixed an issue with certificate state that caused
 *                         receipt printing problems.
 *    abondala  09/04/13 - initialize collections
 *    rabhawsa  04/02/13 - no need to ask for gift certificate face value while
 *                         returning change.
 *    vtemker   03/30/12 - Refactoring of getNumber() method of TenderCheck
 *                         class - returns sensitive data in byte[] instead of
 *                         String
 *    jswan     03/26/12 - Modified to support centralized gift certificate and
 *                         store credit.
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    jswan     11/18/09 - Forward to fix use of gift cerificate more than once
 *                         in a transaction and making change to gift
 *                         certificate which already been redeemed.
 *    jswan     11/17/09 - XbranchMerge shagoyal_bug-8553074 from
 *                         rgbustores_13.0x_branch
 *    mchellap  02/09/09 - Reverted the changes to setTenderAttributes
 *    mchellap  02/04/09 - setTenderAttributes: Setting the current store id if
 *                         store id is null
 *    ddbaker   01/21/09 - Removed tab characters causing alignment problems.
 *    ddbaker   01/21/09 - Update to use Currency Type during creation of
 *                         alternate (foreign) currency objects.
 *    vchengeg  11/07/08 - To fix BAT test failure
 *
 * ===========================================================================
 * $Log:
 *  7    360Commerce 1.6         4/25/2007 8:52:54 AM   Anda D. Cadar   I18N
 *       merge
 *
 *  6    360Commerce 1.5         7/24/2006 8:27:40 PM   Keith L. Lesikar Merge
 *       effort.
 *  5    360Commerce 1.4         1/22/2006 11:45:00 AM  Ron W. Haight   removed
 *        references to com.ibm.math.BigDecimal
 *  4    360Commerce 1.3         12/13/2005 4:42:33 PM  Barry A. Pape
 *       Base-lining of 7.1_LA
 *  3    360Commerce 1.2         3/31/2005 4:30:24 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:25:58 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:14:52 PM  Robert Pearse
 * $
 * Revision 1.25  2004/07/28 01:05:28  blj
 * @scr 6495 updated status so that they all match.
 *
 * Revision 1.24  2004/07/15 23:22:45  crain
 * @scr 5280 Gift Certificates issued in Training Mode can be Tendered outside of Training Mode
 *
 * Revision 1.23  2004/07/14 22:11:17  kmcbride
 * @scr 5954 (Services Impact): Adding log4j loggers to these classes to make them easier to debug, also fixed some catch statements that were not logging or re-throwing a new exception w/o nesting the original.
 *
 * Revision 1.22  2004/05/24 21:45:39  crain
 * @scr 5105 Tender Redeem_Gift Cert Redeem w/ Disc. Receipt Incorrect
 *
 * Revision 1.21  2004/05/16 20:52:59  blj
 * @scr 4476 - updated and code cleanup
 *
 * Revision 1.20  2004/05/10 19:08:08  crain
 * @scr 4182 Able to use a Issued Gift Cert over and over, Tender Redeemed never appears
 *
 * Revision 1.19  2004/05/05 16:05:21  epd
 * @scr 0 4513 Gift Certs no longer validate against database if we're issuing them
 *
 * Revision 1.18  2004/05/04 18:23:25  crain
 * @scr 4553 Redeem Gift Certificate
 *
 * Revision 1.17  2004/05/04 03:35:44  crain
 * @scr 4553 Redeem Gift Certificate
 *
 * Revision 1.16  2004/05/02 05:48:03  crain
 * @scr 4553 Redeem Gift Certificate
 *
 * Revision 1.15  2004/05/02 01:54:05  crain
 * @scr 4553 Redeem Gift Certificate
 *
 * Revision 1.14  2004/04/29 19:30:38  lzhao
 * @scr 4553: summary report.
 *
 * Revision 1.13  2004/04/29 15:07:19  crain
 * @scr 4553 Redeem Gift Certificate
 *
 * Revision 1.12  2004/04/22 21:03:53  epd
 * @scr 4513 Changed all toFormattedString() calls to getStringValue() calls
 *
 * Revision 1.11  2004/04/22 19:12:03  crain
 * @scr 4206 Updating Javadoc
 *
 * Revision 1.10  2004/04/13 17:19:31  crain
 * @scr 4206 Updating Javadoc
 *
 * Revision 1.9  2004/04/08 01:40:21  crain
 * @scr 4105 Foreign Currency
 *
 * Revision 1.8  2004/04/06 18:58:56  blj
 * @scr 4301 - Fix for TenderStoreCreditADOTest and TenderGiftCertificateADOTest
 *
 * Revision 1.7  2004/04/01 01:46:32  crain
 * @scr 4105 Foreign Currency
 *
 * Revision 1.6  2004/03/26 21:32:18  cdb
 * @scr 4204 Removing Tabs.
 *
 * Revision 1.5  2004/03/26 20:48:45  bjosserand
 * @scr 4093 Transaction Reentry
 * Revision 1.4 2004/03/26 04:20:19 crain @scr 4105 Foreign Currency
 *
 * Revision 1.3 2004/03/23 00:31:09 crain @scr 4105 Foreign Currency
 *
 * Revision 1.2 2004/02/12 16:47:55 mcs Forcing head revision
 *
 * Revision 1.1.1.1 2004/02/11 01:04:11 cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.12 Feb 05 2004 13:46:36 rhafernik log4j changes
 *
 * Rev 1.11 Feb 05 2004 12:27:16 crain Added UtilityIfc Resolution for 3421: Tender redesign
 *
 * Rev 1.10 Jan 06 2004 11:23:04 epd refactorings to remove unfriendly references to TenderHelper and DomainGateway
 *
 * Rev 1.9 Dec 17 2003 08:38:10 blj updated for storecredit
 *
 * Rev 1.8 Dec 12 2003 13:29:40 blj initial "happy" path for store credit
 *
 * Rev 1.7 Dec 10 2003 13:36:42 crain Added conversion rate Resolution for 3421: Tender redesign
 *
 * Rev 1.6 Dec 07 2003 18:50:34 crain Added foreign amount Resolution for 3421: Tender redesign
 *
 * Rev 1.5 Dec 02 2003 18:23:40 crain Added type code Resolution for 3421: Tender redesign
 *
 * Rev 1.4 Nov 24 2003 17:43:20 cdb Updated to track entry method for gift certificate tendering. Resolution for 3421:
 * Tender redesign
 *
 * Rev 1.3 Nov 21 2003 15:43:26 cdb Checking for null before setting TenderConstants.CERTIFICATE_TYPE Resolution for
 * 3421: Tender redesign
 *
 * Rev 1.2 Nov 21 2003 14:04:48 crain Used the interface instead of the object Resolution for 3421: Tender redesign
 *
 * Rev 1.1 Nov 20 2003 16:07:00 crain Implemented methods Resolution for 3421: Tender redesign
 *
 * Rev 1.0 Nov 04 2003 11:13:16 epd Initial revision.
 *
 * Rev 1.1 Oct 21 2003 10:01:00 epd Refactoring. Moved RDO tender to abstract class
 *
 * Rev 1.0 Oct 17 2003 12:33:48 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderUtilityFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalConstants;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.services.tender.tdo.TenderTDOConstants;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderCertificateIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.GiftCertificateDocumentIfc;
//------------------------------------------------------------------------------
/**
 Class for handling tender by gift certificate . <P>
 @see oracle.retail.stores.pos.ado.tender.AbstractTenderADO
 @version $Revision: /main/22 $
 **/
//------------------------------------------------------------------------------
public class TenderGiftCertificateADO extends AbstractTenderADO
{
    /**
     conversionRate as BigDecimal
     */
    BigDecimal conversionRate = null;

    /**
     *  our logger
     **/
    protected transient Logger logger = Logger.getLogger(TenderGiftCertificateADO.class);

    /**
     * No-arg constructor It is intended that the tender factory instantiate this
     */
    protected TenderGiftCertificateADO()
    {
    }

    /**
     * Initialize tenderRDO
     * @see oracle.retail.stores.pos.ado.tender.AbstractTenderADO#initializeTenderRDO()
     */
    protected void initializeTenderRDO()
    {
        tenderRDO = DomainGateway.getFactory().getTenderGiftCertificateInstance();
        ((TenderGiftCertificateIfc) tenderRDO).setTypeCode(TenderLineItemIfc.TENDER_TYPE_GIFT_CERTIFICATE);
    }

    /**
     * Get the tender type
     * @see oracle.retail.stores.pos. ado.tender.TenderADOIfc#getTenderType()
     */
    public TenderTypeEnum getTenderType()
    {
        return TenderTypeEnum.GIFT_CERT;
    }

    /**
     * @see oracle.retail.stores.pos.ado.tender.TenderADOIfc#validate()
     */
    public void validate() throws TenderException
    {
        if(logger.isInfoEnabled())
        {
            logger.info("Validating gift certiciate information...");
        }

        try
        {
            UtilityIfc utility = getUtility();

            //get the certificate validator
            CertificateValidatorIfc certificateValidator = createCertificateValidator();
            boolean isTransReentry = this.isTransactionReentryMode();
            certificateValidator.setTransactionReentryMode(isTransReentry);

            // validate the number
            certificateValidator.validateNumber();

            String validateGiftCertificate = utility.getParameterValue("ValidateGiftCertificate", "N");
            if (validateGiftCertificate.equals("Y"))
            {
                // if the amount of the gift cert is positive,validate against
                // the database whether this gift certificate is already tendered,
                // if not whether it exists in the master table or not.
                if (getAmount().signum() == CurrencyIfc.POSITIVE)
                {
                    // Lookup the store credit.
                    certificateValidator.lookupCertificate();
                    
                    // If any part of the lookup validation fails, the validator throws an exception and we don't 
                    // get this far.
                    
                    // Cast the tenderRDO, 
                    TenderGiftCertificateIfc originalRDO = (TenderGiftCertificateIfc)tenderRDO;
                    
                    // Get the RDO retrieved from the validator
                    TenderCertificateIfc retrievedTenderRDO = certificateValidator.getTenderRDO();
                    // Set status from the original RDO on the retrieved RDO
                    retrievedTenderRDO.getBaseDocument().setStatus(originalRDO.getBaseDocument().getStatus());
                    // Use the retrieved RDO, which contains additional useful information.
                    tenderRDO = retrievedTenderRDO;
                }
                // if the amount of the gift cert is negative, we're issuing the cert.
                // so need to validate against the database whether this
                // gift certificate number is already issued or not.
                else if (getAmount().signum() == CurrencyIfc.NEGATIVE)
                {
               	 	certificateValidator.lookupIssuedCertificate();
                }

            }
        }
        catch (TenderException te)
        {
            if (te.getErrorCode() == TenderErrorCodeEnum.CERTIFICATE_TENDERED)
            {
                // update tenderRDO after it was changed through RMI
                tenderRDO = (TenderLineItemIfc)te.getChangedObject();

                // rethrow the exception with new changed object.
                throw new TenderException("Certificate Tendered", TenderErrorCodeEnum.CERTIFICATE_TENDERED, te);

            }
            else if (te.getErrorCode() == TenderErrorCodeEnum.VALIDATION_OFFLINE
                    && getAmount().signum() == CurrencyIfc.NEGATIVE)
            {
                logger.error("offline mode; adding gift certificate of the remaining tender amount", te);
            }
            else
            {
                throw te;
            }
        }
        catch (ADOException adoe)
        {
            logger.error("Unable to create ceritifcate validator: " + adoe);
            //            adoe.printStackTrace();
        }
    }

    /**
     * @see oracle.retail.stores.pos.ado.tender.TenderADOIfc#getTenderAttributes()
     * @return HashMap
     */
    public HashMap getTenderAttributes()
    {
        HashMap map = new HashMap(20);
        map.put(TenderConstants.TENDER_TYPE, getTenderType());
        map.put(
                TenderConstants.AMOUNT,
                getAmount().getStringValue());
        if(((TenderAlternateCurrencyIfc)tenderRDO).getAlternateCurrencyTendered() != null)
        {
            map.put(TenderConstants.ALTERNATE_AMOUNT,
                    ((TenderAlternateCurrencyIfc)tenderRDO).getAlternateCurrencyTendered()
                    .getStringValue());
            map.put(TenderConstants.FOREIGN_CURRENCY,
                    ((TenderAlternateCurrencyIfc)tenderRDO).getAlternateCurrencyTendered().getType());
        }

        if(((TenderCertificateIfc)tenderRDO).getFaceValueAmount() != null)
        {
            map.put(TenderConstants.FACE_VALUE_AMOUNT,
                    ((TenderCertificateIfc)tenderRDO).getFaceValueAmount()
                    .getStringValue());
        }

        if(((TenderGiftCertificateIfc)tenderRDO).getDiscountAmount() != null)
        {
            map.put(TenderConstants.DISCOUNT_AMOUNT,
                    ((TenderGiftCertificateIfc)tenderRDO).getDiscountAmount()
                    .getStringValue());
        }

        map.put(TenderConstants.NUMBER, new String(((TenderCertificateIfc) tenderRDO).getNumber()));
        map.put(TenderConstants.ISSUE_DATE, ((TenderCertificateIfc) tenderRDO).getIssueDateAsString());
        map.put(TenderConstants.STORE_NUMBER, ((TenderCertificateIfc) tenderRDO).getStoreNumber());
        map.put(TenderConstants.REDEEM_DATE, ((TenderCertificateIfc) tenderRDO).getRedeemDateAsString());
        map.put(TenderConstants.REDEEM_TRANSACTION_ID, ((TenderCertificateIfc) tenderRDO).getRedeemTransactionID());
        map.put(TenderConstants.ENTRY_METHOD, ((TenderGiftCertificateIfc) tenderRDO).getEntryMethod());
        map.put(
                TenderConstants.CERTIFICATE_TYPE,
                CertificateTypeEnum.makeEnumFromString(((TenderGiftCertificateIfc) tenderRDO).getCertificateType()));
        map.put(TenderConstants.CONVERSION_RATE, conversionRate);
        map.put(TenderGiftCertificateIfc.REDEEMED, ((TenderGiftCertificateIfc) tenderRDO).getState());
        map.put(TenderConstants.TRAINING_MODE, (new Boolean(((TenderCertificateIfc) tenderRDO).isTrainingMode())));
        map.put(TenderConstants.CERTIFICATE_DOCUMENT, ((TenderGiftCertificateIfc) tenderRDO).getDocument());
        
        return map;
    }

    /**
     * @see oracle.retail.stores.pos.ado.tender.TenderADOIfc#setTenderAttributes(java.util.HashMap)
     */
    public void setTenderAttributes(HashMap tenderAttributes) throws TenderException
    {
        ((TenderCertificateIfc) tenderRDO).setAmountTender(
                parseAmount((String) tenderAttributes.get(TenderConstants.AMOUNT)));
        ((TenderGiftCertificateIfc) tenderRDO).setGiftCertificateNumber(
                (String) tenderAttributes.get(TenderConstants.NUMBER));

        if (tenderAttributes.get(TenderConstants.ISSUE_DATE) != null)
        {
            ((TenderCertificateIfc) tenderRDO).setIssueDateAsString(
                    (String) tenderAttributes.get(TenderConstants.ISSUE_DATE));
        }

        if (tenderAttributes.get(TenderConstants.STORE_NUMBER) != null)
        {
            ((TenderCertificateIfc) tenderRDO).setStoreNumber(
                    (String) tenderAttributes.get(TenderConstants.STORE_NUMBER));
        }

        if (tenderAttributes.get(TenderConstants.ENTRY_METHOD) != null)
        {
            ((TenderGiftCertificateIfc) tenderRDO).setEntryMethod(
                    (EntryMethod) tenderAttributes.get(TenderConstants.ENTRY_METHOD));
        }

        if (tenderAttributes.get(TenderConstants.CERTIFICATE_TYPE) != null)
        {
            ((TenderGiftCertificateIfc) tenderRDO).setCertificateType(
                    ((CertificateTypeEnum) tenderAttributes.get(TenderConstants.CERTIFICATE_TYPE)).toString());
        }

        if (tenderAttributes.get(TenderGiftCertificateIfc.REDEEMED) != null)
        {
            ((TenderGiftCertificateIfc) tenderRDO).setState(
                    (String) tenderAttributes.get(TenderGiftCertificateIfc.REDEEMED));
        }

        CurrencyIfc alternateAmount = (CurrencyIfc)tenderAttributes.get(TenderTDOConstants.ALTERNATE_CURRENCY);
        if (alternateAmount != null)
        {
            ((TenderAlternateCurrencyIfc)tenderRDO).setAlternateCurrencyTendered(alternateAmount);
            CurrencyTypeIfc alternateAmountType = (CurrencyTypeIfc)tenderAttributes.get(TenderConstants.FOREIGN_CURRENCY);
            if (alternateAmountType != null)
            {
                alternateAmount.setType(alternateAmountType);
            }
        }

        if (tenderAttributes.get(TenderConstants.FACE_VALUE_AMOUNT) != null)
        {
            ((TenderCertificateIfc)tenderRDO).setFaceValueAmount(
                    parseAmount((String) tenderAttributes.get(TenderConstants.FACE_VALUE_AMOUNT)));
        }

        if (tenderAttributes.get(TenderConstants.DISCOUNT_AMOUNT) != null)
        {
            ((TenderGiftCertificateIfc)tenderRDO).setDiscountAmount(
                    parseAmount((String) tenderAttributes.get(TenderConstants.DISCOUNT_AMOUNT)));
        }

        if (tenderAttributes.get(TenderConstants.STATE) != null)
        {
            ((TenderCertificateIfc)tenderRDO).setState((String)tenderAttributes.get(TenderConstants.STATE));
        }

        conversionRate = (BigDecimal) tenderAttributes.get(TenderConstants.CONVERSION_RATE);

        if (tenderAttributes.get(TenderConstants.TRAINING_MODE) != null)
        {
            ((TenderCertificateIfc)tenderRDO).setTrainingMode(((Boolean)tenderAttributes.get(TenderConstants.TRAINING_MODE)).booleanValue());
        }
        if (tenderAttributes.get(TenderConstants.CERTIFICATE_DOCUMENT) != null)
        {
            ((TenderGiftCertificateIfc)tenderRDO).setDocument(((GiftCertificateDocumentIfc)tenderAttributes.get(TenderConstants.CERTIFICATE_DOCUMENT)));
        }
    }

    /**
     * Indicates Gift Certificate is a NOT type of PAT Cash
     * @return false
     */
    public boolean isPATCash()
    {
        return false;
    }

    /**
     * Sets the validate by store number flag needed for database search.
     * @deprecated in 14.0; no longer used
     */
    public void checkStoreNumber()
    {
    }
    
    /**
     * @return
     * @throws ADOException
     */
    protected CertificateValidatorIfc createCertificateValidator() throws ADOException
    {
        // Create the certificate validator.
        TenderUtilityFactoryIfc factory = (TenderUtilityFactoryIfc)ADOFactoryComplex.getFactory("factory.tenderutility");
        CertificateValidatorIfc certificateValidator = factory.createCertificateValidator((TenderCertificateIfc) tenderRDO);
        return certificateValidator;
    }

    /**
     * @see oracle.retail.stores.pos.ado.journal.JournalableADOIfc#getJournalMemento()
     */
    public Map getJournalMemento()
    {
        Map memento = getTenderAttributes();
        // add tender descriptor
        memento.put(JournalConstants.DESCRIPTOR, getTenderType().toString());
        return memento;
    }

    /**
     * From legacy method
     * @see oracle.retail.stores.pos.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
     */
    public void fromLegacy(EYSDomainIfc rdo)
    {
        //assert(rdo instanceof TenderGiftCertificateIfc);
        tenderRDO = (TenderCertificateIfc) rdo;
    }

    /**
     * To legacy method
     * @see oracle.retail.stores.pos.ado.ADOIfc#toLegacy()
     */
    public EYSDomainIfc toLegacy()
    {
        return tenderRDO;
    }

    /**
     * @see oracle.retail.stores.pos.ado.ADOIfc#toLegacy(java.lang.Class)
     */
    public EYSDomainIfc toLegacy(Class type)
    {
        return toLegacy();
    }
}
