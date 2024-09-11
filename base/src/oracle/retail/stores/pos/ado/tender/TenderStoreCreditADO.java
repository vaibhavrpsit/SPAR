/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/TenderStoreCreditADO.java /main/35 2013/10/15 13:11:45 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     10/15/13 - Fixed an issue with certificate state that caused
 *                         receipt printing problems.
 *    abondala  09/04/13 - initialize collections
 *    yiqzhao   01/10/13 - Add business name for store credit and store credit
 *                         tender line tables.
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    mjwallac  05/09/12 - Fortify: fix redundant null checks, part 5
 *    mjwallac  05/02/12 - Fortify: fix redundant null checks, part 4
 *    vtemker   03/30/12 - Refactoring of getNumber() method of TenderCheck
 *                         class - returns sensitive data in byte[] instead of
 *                         String
 *    jswan     03/26/12 - Modified to support centralized gift certificate and
 *                         store credit.
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    vtemker   08/26/11 - Forward port 12744180. Store credit id should be
 *                         made case insensitive
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    blarsen   08/26/09 - XbranchMerge
 *                         blarsen_bug8809064-store-credit-no-expiry-fix from
 *                         rgbustores_13.1x_branch
 *    blarsen   08/18/09 - if ComputeStoreCreditExpirationDate param is no,
 *                         then return null expiry date (do not return current
 *                         date)
 *    cgreene   04/22/09 - added ability for store credit to know if customer
 *                         is business so name can print properly (only once)
 *    nkgautam  03/09/09 - fixed parse exception issue of Store credit
 *                         expiration date
 *    jswan     01/29/09 - Modified to correct issues with printing store
 *                         credit.
 *    deghosh   01/22/09 - EJ i18n defect fixes
 *    ddbaker   01/21/09 - Removed tab characters causing alignment problems.
 *    ddbaker   01/21/09 - Update to use Currency Type during creation of
 *                         alternate (foreign) currency objects.
 *    ranojha   11/11/08 - Fixed expiration date for StoreCredit Issuance from
 *                         EJournal
 *    vchengeg  11/07/08 - To fix BAT test failure
 *    abondala  11/06/08 - updated files related to reason codes
 *    sswamygo  11/05/08 - Checkin after merges
 *
 * ===========================================================================
 * $Log:
 *  7    360Commerce 1.6         5/19/2008 2:31:24 AM   ASHWYN TIRKEY   Updated
 *        validateStoreCreditNumber() method to look up store credit in order
 *       to check that the store credit issued is unique or not for issue
 *       31453
 *
 *  6    360Commerce 1.5         4/25/2007 8:52:53 AM   Anda D. Cadar   I18N
 *       merge
 *
 *  5    360Commerce 1.4         1/22/2006 11:45:00 AM  Ron W. Haight   removed
 *        references to com.ibm.math.BigDecimal
 *  4    360Commerce 1.3         12/13/2005 4:42:33 PM  Barry A. Pape
 *       Base-lining of 7.1_LA
 *  3    360Commerce 1.2         3/31/2005 4:30:26 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:26:04 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:14:57 PM  Robert Pearse
 * $
 * Revision 1.27  2004/07/28 01:05:28  blj
 * @scr 6495 updated status so that they all match.
 *
 * Revision 1.26  2004/07/23 22:17:25  epd
 * @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 * Revision 1.25  2004/07/15 16:13:22  kmcbride
 * @scr 5954 (Services Impact): Adding logging to these ADOs, also fixed some exception handling issues.
 *
 * Revision 1.24  2004/06/17 16:26:17  blj
 * @scr 5678 - code cleanup
 *
 * Revision 1.23  2004/05/25 15:11:41  blj
 * @scr 5115 - resolution for printing issues
 *
 * Revision 1.22  2004/05/16 20:52:59  blj
 * @scr 4476 - updated and code cleanup
 *
 * Revision 1.21  2004/05/11 16:08:47  blj
 * @scr 4476 - more rework for store credit tender.
 *
 * Revision 1.20  2004/05/10 19:08:08  crain
 * @scr 4182 Able to use a Issued Gift Cert over and over, Tender Redeemed never appears
 *
 * Revision 1.19  2004/05/02 01:54:05  crain
 * @scr 4553 Redeem Gift Certificate
 *
 * Revision 1.18  2004/04/22 22:35:55  blj
 * @scr 3872 - more cleanup
 *
 * Revision 1.17  2004/04/22 21:03:53  epd
 * @scr 4513 Changed all toFormattedString() calls to getStringValue() calls
 *
 * Revision 1.16  2004/04/22 20:52:19  epd
 * @scr 4513 FIxes to tender, especially gift card, gift cert, and store credit
 *
 * Revision 1.15  2004/04/21 15:08:12  blj
 * @scr 4476 - fixed crashes, foreign currency, validation, etc for store credit rework.
 *
 * Revision 1.14  2004/04/15 20:52:03  blj
 * @scr 3872 - updated validation
 *
 * Revision 1.13  2004/04/06 18:58:56  blj
 * @scr 4301 - Fix for TenderStoreCreditADOTest and TenderGiftCertificateADOTest
 *
 * Revision 1.12  2004/04/02 16:26:22  blj
 * @scr 3872 - Fixed validation, database and training mode errors
 *
 * Revision 1.11  2004/04/01 15:58:17  blj
 * @scr 3872 Added training mode, toggled the redeem button based
 * on transaction==null and fixed post void problems.
 *
 * Revision 1.10  2004/03/26 23:20:06  bjosserand
 * @scr 4093 Transaction Reentry
 * Revision 1.9 2004/03/24 17:06:34 blj @scr 3871-3872 - Added the ability to
 * reprint redeem transaction receipts and added a void receipt.
 *
 * Revision 1.8 2004/03/18 16:30:05 nrao Code cleanup.
 *
 * Revision 1.7 2004/03/04 23:31:01 nrao Added methods as per code review for Issue Store Credit.
 *
 * Revision 1.6 2004/03/04 18:22:38 nrao Fixed class cast exception
 *
 * Revision 1.5 2004/03/01 18:17:51 nrao Entered first name, last name and id type to getTenderAttributes &
 * setTenderAttributes
 *
 * Revision 1.4 2004/02/19 19:00:35 nrao Added expiration date to getTenderAttributes() & setTenderAttributes().
 *
 * Revision 1.3 2004/02/17 17:52:32 nrao Added methods for Issue Store Credit
 *
 * Revision 1.2 2004/02/12 16:47:55 mcs Forcing head revision
 *
 * Revision 1.1.1.1 2004/02/11 01:04:11 cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.5 Jan 06 2004 11:23:10 epd refactorings to remove unfriendly references to TenderHelper and DomainGateway
 *
 * Rev 1.4 Dec 30 2003 11:38:26 blj fixed a minor printing problem on receipt. Also added some code changes due to code
 * review
 *
 * Rev 1.3 Dec 18 2003 19:46:52 blj fixed flow issues and removed debug statements
 *
 * Rev 1.2 Dec 17 2003 08:38:12 blj updated for storecredit
 *
 * Rev 1.1 Dec 12 2003 13:29:42 blj initial "happy" path for store credit
 *
 * Rev 1.0 Nov 04 2003 11:13:20 epd Initial revision.
 *
 * Rev 1.1 Oct 21 2003 10:01:04 epd Refactoring. Moved RDO tender to abstract class
 *
 * Rev 1.0 Oct 17 2003 12:33:50 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.context.ContextFactory;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderUtilityFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalConstants;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargoIfc;
import oracle.retail.stores.pos.services.tender.tdo.TenderTDOConstants;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.StoreCreditIfc;
import oracle.retail.stores.foundation.tour.service.TourContext;

/**
 *
 */
public class TenderStoreCreditADO extends AbstractTenderADO
{
    /** The format of expiration date */
    public static final String STORE_CREDIT_DATE_FORMAT = "MM/dd/yyyy";

    //  conversion rate for foreign currency
    BigDecimal conversionRate = null;

    /**
     * The logger to which log messages will be sent.
     */
    protected static transient Logger logger = Logger.getLogger(TenderStoreCreditADO.class);

    /**
     * No-arg constructor It is intended that the tender factory instantiate this
     */
    protected TenderStoreCreditADO()
    {
    }

    /*
     * @see oracle.retail.stores.ado.tender.AbstractTenderADO#initializeTenderRDO()
     */
    protected void initializeTenderRDO()
    {
        tenderRDO = DomainGateway.getFactory().getTenderStoreCreditInstance();
        ((TenderStoreCreditIfc)tenderRDO).setTrainingMode(isTrainingMode());
    }

    /*
     * @see oracle.retail.stores.ado.tender.TenderADOIfc#getTenderType()
     */
    public TenderTypeEnum getTenderType()
    {
        // TODO Auto-generated method stub
        return TenderTypeEnum.STORE_CREDIT;
    }

    /*
     * @see oracle.retail.stores.ado.journal.JournalableADOIfc#getJournalMemento()
     */
    public Map getJournalMemento()
    {
        Map memento = getTenderAttributes();
        // add tender descriptor
        memento.put(JournalConstants.DESCRIPTOR, getTenderType().toString());
        return memento;
    }

    /*
     * @see oracle.retail.stores.ado.tender.TenderADOIfc#validate()
     */
    public void validate() throws TenderException
    {
        if(logger.isInfoEnabled())
        {
            logger.info("Validating store credit information...");
        }

        try
        {
            //CertificateValidator certificateValidator = new CertificateValidator((TenderCertificateIfc) tenderRDO);
            CertificateValidatorIfc certificateValidator = createCertificateValidator();
            certificateValidator.setTransactionReentryMode(this.isTransactionReentryMode());
            UtilityIfc utility = getUtility();
            
            // POS Generates the store credit number, don't perform the check digit validation.
            String preprintedStoreCredit = utility.getParameterValue("PrePrintedStoreCredit", "N");
            if (preprintedStoreCredit.equals("Y"))
            {
                certificateValidator.validateNumber();
            }

            // If the database validation is turned on, lookup the store credit. 
            String validateStoreCredit = utility.getParameterValue("ValidateStoreCredit", "N");
            if (validateStoreCredit.equals("Y") && !isIssueStoreCredit())
            {
                // Lookup the store credit.
                certificateValidator.lookupCertificate();
                
                // If any part of the lookup validation fails, the validator throws an exception and we don't 
                // get this far.
                
                // Cast the tenderRDO, 
                TenderStoreCreditIfc originalRDO = (TenderStoreCreditIfc)tenderRDO;
                
                // Get the RDO retrieved from the validator
                TenderCertificateIfc retrievedTenderRDO = certificateValidator.getTenderRDO();
                // Set status from the original RDO on the retrieved RDO
                retrievedTenderRDO.getBaseDocument().setStatus(originalRDO.getBaseDocument().getStatus());
                // Use the retrieved RDO, which contains additional useful information.
                tenderRDO = retrievedTenderRDO;
                // Update the map.
                getTenderAttributes();
            }
        }
        catch (TenderException te)
        {
            if (te.getErrorCode() == TenderErrorCodeEnum.CERTIFICATE_TENDERED)
            {
                // update tenderRDO after it was changed through RMI
                tenderRDO = (TenderLineItemIfc)te.getChangedObject();

                // rethrow the exception with the changed object
                throw new TenderException("Certificate Tendered", TenderErrorCodeEnum.CERTIFICATE_TENDERED, te);

            }
            else
            {
                throw te;
            }
        }
        catch (ADOException ae)
        {
            // Log the exception, more thought should be put into
            // this to handle this exceptiuon beyond just logging it.
            // Perhaps it makes sense to throw a different exception
            // and nest this one...
            //
            logger.error("An error occurred while validating store credit: " + ae);
//            ae.printStackTrace();
        }
    }

    /**
     * this method is a convenience method forT voids so it doesnt check the
     * preprinted store credit parameter.
     */
    public void lookupCertificateForVoid() throws TenderException
    {
        try
        {
            CertificateValidatorIfc certificateValidator = createCertificateValidator();
            certificateValidator.lookupCertificate();
        }
        catch (TenderException te)
        {
            if (te.getErrorCode() == TenderErrorCodeEnum.CERTIFICATE_TENDERED)
            {
                // update tenderRDO after it was changed through RMI
                tenderRDO = (TenderLineItemIfc)te.getChangedObject();

                // rethrow the exception
                throw new TenderException("Certificate Tendered", TenderErrorCodeEnum.CERTIFICATE_TENDERED, te);

            }
            else
            {
                throw te;
            }
        }
        catch (ADOException ae)
        {
            // Log the exception, more thought should be put into
            // this to handle this exceptiuon beyond just logging it.
            // Perhaps it makes sense to throw a different exception
            // and nest this one...
            //
            logger.error("An error occurred while looking up store credit: " + ae);
            //ae.printStackTrace();
        }
    }

    /*
     * @see oracle.retail.stores.ado.tender.TenderADOIfc#getTenderAttributes()
     */
    public HashMap<String, Object> getTenderAttributes()
    {
        HashMap<String, Object> map = new HashMap<String, Object>(20);

        UtilityIfc utility = getUtility();
        String computeExpDate = utility.getParameterValue("ComputeStoreCreditExpirationDate", "Y");

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
        map.put(TenderConstants.NUMBER, ((TenderStoreCreditIfc) tenderRDO).getStoreCreditID());
        map.put(TenderConstants.STORE_NUMBER, ((TenderCertificateIfc) tenderRDO).getStoreNumber());
        map.put(TenderConstants.REDEEM_DATE, ((TenderCertificateIfc) tenderRDO).getRedeemDateAsString());
        map.put(TenderConstants.REDEEM_TRANSACTION_ID, ((TenderCertificateIfc) tenderRDO).getRedeemTransactionID());
        map.put(
            TenderConstants.CERTIFICATE_TYPE,
            CertificateTypeEnum.makeEnumFromString(((TenderCertificateIfc) tenderRDO).getCertificateType()));
        map.put(TenderConstants.CONVERSION_RATE, conversionRate);
        map.put(TenderConstants.ISSUE_DATE, ((TenderCertificateIfc) tenderRDO).getIssueDateAsString());
        map.put(TenderConstants.ENTRY_METHOD, ((TenderStoreCreditIfc) tenderRDO).getEntryMethod());
        if (computeExpDate.equalsIgnoreCase("Y"))
        {
            if (((TenderStoreCreditIfc) tenderRDO).getExpirationDate() != null)
            {
                map.put(
                    TenderConstants.EXPIRATION_DATE,
                    ((TenderStoreCreditIfc) tenderRDO).getExpirationDate().toFormattedString(
                        LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE)));
            }
        }
        map.put(TenderConstants.FIRST_NAME, ((TenderStoreCreditIfc) tenderRDO).getFirstName());
        map.put(TenderConstants.LAST_NAME, ((TenderStoreCreditIfc) tenderRDO).getLastName());

        if (((TenderStoreCreditIfc)tenderRDO).getPersonalIDType()!= null)
        {
            map.put(TenderConstants.LOCALIZED_ID_TYPE, ((TenderStoreCreditIfc) tenderRDO).getPersonalIDType());
            map.put(TenderConstants.ID_TYPE, ((TenderStoreCreditIfc) tenderRDO).getPersonalIDType().getCodeName());
        }

        map.put(TenderConstants.STATE, ((TenderStoreCreditIfc) tenderRDO).getState());
        map.put(TenderConstants.COLLECTED, Boolean.valueOf(tenderRDO.isCollected()));

        return map;
    }

    /*
     * Set tender attributes. @param tenderAttributes map of tender attributes @throws TenderException
     */
    public void setTenderAttributes(HashMap tenderAttributes) throws TenderException
    {
        CurrencyIfc amount = parseAmount((String) tenderAttributes.get(TenderConstants.AMOUNT));
        tenderRDO.setAmountTender(amount);
        ((TenderStoreCreditIfc) tenderRDO).setAmount(amount);
        ((TenderStoreCreditIfc) tenderRDO).setStoreCreditID((String) tenderAttributes.get(TenderConstants.NUMBER));
        ((TenderStoreCreditIfc) tenderRDO).setStoreNumber((String) tenderAttributes.get(TenderConstants.STORE_NUMBER));
        if (tenderAttributes.get(TenderConstants.ISSUE_DATE) != null)
        {
            ((TenderCertificateIfc) tenderRDO).setIssueDateAsString(
                (String) tenderAttributes.get(TenderConstants.ISSUE_DATE));
        }
        if (tenderAttributes.get(TenderConstants.CERTIFICATE_TYPE) != null)
        {
            ((TenderCertificateIfc) tenderRDO).setCertificateType(
                ((CertificateTypeEnum) tenderAttributes.get(TenderConstants.CERTIFICATE_TYPE)).toString());
        }
        if (tenderAttributes.get(TenderConstants.ENTRY_METHOD) != null)
        {
            ((TenderStoreCreditIfc) tenderRDO).setEntryMethod(
                (EntryMethod) tenderAttributes.get(TenderConstants.ENTRY_METHOD));
        }
        if (tenderAttributes.get(TenderConstants.EXPIRATION_DATE) != null)
        {
          EYSDate expDate = new EYSDate((String) tenderAttributes.get(TenderConstants.EXPIRATION_DATE),
              DateFormat.SHORT, LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));
          ((TenderStoreCreditIfc) tenderRDO).setExpirationDate(expDate);
        }
        ((TenderStoreCreditIfc) tenderRDO).setFirstName((String) tenderAttributes.get(TenderConstants.FIRST_NAME));
        ((TenderStoreCreditIfc) tenderRDO).setLastName((String) tenderAttributes.get(TenderConstants.LAST_NAME));
        ((TenderStoreCreditIfc) tenderRDO).setBusinessName((String) tenderAttributes.get(TenderConstants.BUSINESS_NAME));  
        if (tenderAttributes.get(TenderConstants.LOCALIZED_ID_TYPE) instanceof LocalizedCodeIfc)
        {
            ((TenderStoreCreditIfc) tenderRDO).setPersonalIDType((LocalizedCodeIfc) tenderAttributes.get(TenderConstants.LOCALIZED_ID_TYPE));
        }

        Boolean collected = (Boolean)tenderAttributes.get(TenderConstants.COLLECTED);
        if (collected != null)
        {
            tenderRDO.setCollected(collected.booleanValue());
        }

        if (tenderAttributes.get(TenderConstants.STATE) != null)
        {
            ((TenderStoreCreditIfc) tenderRDO).setState(
                    (String) tenderAttributes.get(TenderConstants.STATE));
            StoreCreditIfc storeCredit = ((TenderStoreCreditIfc)tenderRDO).getStoreCredit();
            if (storeCredit != null)
            {
                if (((TenderStoreCreditIfc)tenderRDO).getState().equals(TenderCertificateIfc.REDEEMED))
                {
                    storeCredit.setStatus(StoreCreditIfc.REDEEMED);
                }
                else
                {
                    storeCredit.setStatus(StoreCreditIfc.ISSUED);
                }
            }
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

        conversionRate = (BigDecimal) tenderAttributes.get(TenderConstants.CONVERSION_RATE);
    }

    /**
     * Indicates Store Credit is NOT a type of PAT Cash
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
     * Check the parameter and throw the corresponding exception
     *
     * @throws TenderException
     */
    public void checkPrePrintedStoreCreditParameter() throws TenderException
    {
        UtilityIfc utility = getUtility();
        String prePrintedValue = utility.getParameterValue("PrePrintedStoreCredit", "N");

        if (prePrintedValue.equals("Y"))
        {
            throw new TenderException("Enter Store Credit Number", TenderErrorCodeEnum.ENTER_STORE_CREDIT);
        }
        else
        {
            throw new TenderException("Create Unique Store Credit Number", TenderErrorCodeEnum.CREATE_STORE_CREDIT);
        }
    }

    /**
     * Creates a store credit with a unique id.
     *
     * @return StoreCreditIfc
     */
    public StoreCreditIfc createUniqueStoreCredit()
    {
        String storeCreditNum = null;
        StoreCreditIfc storeCredit = DomainGateway.getFactory().getStoreCreditInstance();
        storeCreditNum = getUniqueID();
        if (storeCreditNum != null)
        {
            storeCreditNum = storeCreditNum.toUpperCase();
        }
        storeCredit.setStoreCreditID(storeCreditNum);
        storeCredit.setStatus(StoreCreditIfc.ISSUED);
        return storeCredit;
    }

    /**
     * Creates a store credit with existing id.
     *
     * @return StoreCreditIfc
     */
    public StoreCreditIfc createStoreCredit()
    {
        StoreCreditIfc storeCredit = DomainGateway.getFactory().getStoreCreditInstance();
        storeCredit.setStoreCreditID(returnStoreCreditNumber());
        storeCredit.setStatus(StoreCreditIfc.ISSUED);
        return storeCredit;
    }

    /**
     * Determines whether store credit is of type ISSUE
     *
     * @throws TenderException
     */
    public void determineState() throws TenderException
    {
        TenderStoreCreditIfc tsc = null;

        if (toLegacy() instanceof TenderStoreCreditIfc)
        {
            tsc = (TenderStoreCreditIfc) toLegacy();
        }

        if (tsc != null)
        {
            if (tsc.getStoreCredit() != null)
            {
                if (tsc.getStoreCredit().getStatus() == StoreCreditIfc.ISSUED)
                {
                    throw new TenderException("Issue Store Credit", TenderErrorCodeEnum.ISSUE_STORE_CREDIT);
                }
                else
                {
                    throw new TenderException("Not an Issue Store Credit", TenderErrorCodeEnum.NOT_ISSUE_STORE_CREDIT);
                }
            }
            else
            {
                throw new TenderException("Not an Issue Store Credit", TenderErrorCodeEnum.NOT_ISSUE_STORE_CREDIT);
            }
        }
        else
        {
            throw new TenderException("Not an Issue Store Credit", TenderErrorCodeEnum.NOT_ISSUE_STORE_CREDIT);
        }
    }

    /**
     * Returns computed expiration date
     *
     * @return EYSDate expiration date
     */
    public EYSDate computeExpiryDate()
    {
        UtilityIfc utility = getUtility();
        String computeExpDate = utility.getParameterValue("ComputeStoreCreditExpirationDate", "Y");
        String daysToExp = utility.getParameterValue("StoreCreditDaysToExpiration", "365");

        Integer daysToExpiration = new Integer(daysToExp);

        // initialize to today
        EYSDate expirationDate = null;

        if (computeExpDate.equalsIgnoreCase("Y"))
        {
            expirationDate = new EYSDate();
            Calendar cal = Calendar.getInstance();
            cal.setTime(expirationDate.dateValue());
            cal.add(Calendar.DAY_OF_MONTH, daysToExpiration.intValue());
            expirationDate = new EYSDate(cal.getTime());
        }

        return expirationDate;
    }

    /**
     * Sets the store credit tender with store credit info
     *
     * @param exp
     *            EYSDate Expiration Date
     * @param cust
     *            Customer first name, last name
     * @param entryMethod
     *            String Entry Method
     * @param idType
     *            String identification type
     */
    public void setStoreCreditInfo(EYSDate exp, CustomerIfc cust, EntryMethod entryMethod, String idType)
    {
        if(logger.isInfoEnabled())
        {
            logger.info("Setting store credit information...");
        }

        TenderStoreCreditIfc tsc = (TenderStoreCreditIfc) tenderRDO;
        StoreCreditIfc sc = tsc.getStoreCredit();

        // add entry method to store credit tender
        tsc.setEntryMethod(entryMethod);

        // add expiration date to store credit tender
        tsc.setExpirationDate(exp);

        // add first name, last name to store credit tender
        if (cust != null)
        {
            tsc.setFirstName(cust.getFirstName());
            tsc.setLastName(cust.getLastName());
            tsc.setBusinessName(cust.getCompanyName());
        }
        if (sc != null && cust != null)
        {
            sc.setFirstName(cust.getFirstName());
            sc.setLastName(cust.getLastName());
            sc.setBusinessName(cust.getCompanyName());
            sc.setBusinessCustomer(cust.isBusinessCustomer());
        }

        // add id type to store credit tender
        if (idType != null)
        {
            if (!idType.equals(CodeConstantsIfc.CODE_UNDEFINED))
            {
            	tsc.getPersonalIDType().setCode(idType);
            }
    
            if (sc != null)
            {
            	sc.getPersonalIDType().setCode(idType);
            }
        }
    }

    public void setStoreCreditPersonalIdInfo(LocalizedCodeIfc lidType)
    {
        if(logger.isInfoEnabled())
        {
            logger.info("Setting store credit Personal Id...");
        }

        TenderStoreCreditIfc tsc = (TenderStoreCreditIfc) tenderRDO;
        if (tsc != null)
        {
            StoreCreditIfc sc = tsc.getStoreCredit();       
    
            // add personalid type to store credit tender
            if (lidType != null)
            {
            	tsc.setPersonalIDType(lidType);
            }
    
            if (sc != null)
            {
            	sc.setPersonalIDType(lidType);
            }
        }
    }

    /**
     * Convenience method to retrieve next unique ID
     *
     * @return Unique ID String
     */
    protected String getUniqueID()
    {
        AbstractFinancialCargoIfc cargo = (AbstractFinancialCargoIfc)TourContext.getInstance().getTourBus().getCargo();
        return cargo.getRegister().getNextUniqueID();
    }

    /**
     * Returns store credit number
     *
     * @return String Store Credit Number
     */
    public String returnStoreCreditNumber()
    {
        return new String(tenderRDO.getNumber());
    }

    /**
     * Throws TenderException if store credit number entered is invalid
     *
     * @throws TenderException
     */
    public void validateStoreCreditNumber() throws TenderException
    {
        try
        {
            CertificateValidatorIfc certificateValidator = createCertificateValidator();
            certificateValidator.validateNumber();
            certificateValidator.lookupStoreCredit();
        }
        catch (ADOException adoe)
        {
            // Log the exception, more thought should be put into
            // this to handle this exceptiuon beyond just logging it.
            // Perhaps it makes sense to throw a different exception
            // and nest this one...
            //
            logger.error("An error occurred while validating store credit number: " + adoe);
            //adoe.printStackTrace();
        }

    }
    public boolean isIssueStoreCredit()
    {
        if (((TenderStoreCreditIfc)tenderRDO).getState().equals(TenderConstants.ISSUE))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * @return
     * @throws ADOException
     */
    public CertificateValidatorIfc createCertificateValidator() throws ADOException
    {
        // Create the certificate validator.
        TenderUtilityFactoryIfc factory = (TenderUtilityFactoryIfc)ADOFactoryComplex.getFactory("factory.tenderutility");
        CertificateValidatorIfc certificateValidator = factory.createCertificateValidator((TenderCertificateIfc) tenderRDO);
        return certificateValidator;
    }

    /**
     * This method will update the status of the store credit object
     * for post voids.
     * There are 3 status as of v7.0.0, issued, redeemed or voided.
     * We 'should' never get here if the store credit is marked voided.
     *
     */
    public void updateStateForVoid()
    {
        if (((TenderStoreCreditIfc)tenderRDO).getStoreCredit().getStatus().equals(StoreCreditIfc.REDEEMED))
        {
            // We are voiding the store credit tender
            // so reverse the status back to issued.
            ((TenderStoreCreditIfc)tenderRDO).getStoreCredit()
            .setStatus(StoreCreditIfc.ISSUED);
        }
        else if (((TenderStoreCreditIfc)tenderRDO).getStoreCredit().getStatus().equals(StoreCreditIfc.ISSUED))
        {
            // We are voiding the store credit issue so change status to voided.
            ((TenderStoreCreditIfc)tenderRDO).getStoreCredit()
            .setStatus(StoreCreditIfc.VOIDED);
        }
        else
        {
            // For now we will default to voided, this may change in the future.
            ((TenderStoreCreditIfc)tenderRDO).getStoreCredit()
            .setStatus(StoreCreditIfc.VOIDED);
        }
    }

    /*
     * @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
     */
    public void fromLegacy(EYSDomainIfc rdo)
    {
        tenderRDO = (TenderStoreCreditIfc) rdo;
    }

    /*
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy()
     */
    public EYSDomainIfc toLegacy()
    {
        return tenderRDO;
    }

    /*
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy(java.lang.Class)
     */
    public EYSDomainIfc toLegacy(Class type)
    {
        return null;
    }
}
