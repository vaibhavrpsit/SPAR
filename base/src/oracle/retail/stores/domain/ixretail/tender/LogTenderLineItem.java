/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/tender/LogTenderLineItem.java /rgbustores_13.4x_generic_branch/7 2011/09/14 15:11:32 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/14/11 - reduse usage of depreacted method getAccountNumber
 *    cgreene   09/12/11 - revert aba number encryption, which is not sensitive
 *    ohorne    08/18/11 - APF: check cleanup
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    cgreene   11/11/08 - switch to mail check getPrimaryAddress and Phone
 *                         methods
 *    abondala  11/06/08 - updated files related to reason codes
 *    abondala  11/05/08 - updated files related to the reason codes
 *                         CheckIDTypes and MailBankCheckIDTypes
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         12/14/2007 8:59:59 AM  Alan N. Sinton  CR
 *         29761: Removed non-PABP compliant methods and modified card RuleIfc
 *          to take an instance of EncipheredCardData.
 *    5    360Commerce 1.4         8/22/2007 2:20:11 PM   Anda D. Cadar   CR
 *         28495: Send foreign Tender information to poslog and use a scale of
 *          5 when reading exchange rate in CO
 *    4    360Commerce 1.3         4/25/2007 10:00:45 AM  Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:28:57 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:17 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:26 PM  Robert Pearse
 *
 *   Revision 1.5  2004/08/10 20:38:16  kmcbride
 *   @scr 6798: Making this class parse the expiration date string based on the entry method of the card, which seems to dictate the format that it is stored in.
 *
 *   Revision 1.4  2004/07/26 19:57:41  jdeleau
 *   @scr 6324 Expiration date for pos log should have date in correct format
 *
 *   Revision 1.3  2004/02/12 17:13:46  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:28  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:31  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:36:36   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Jul 01 2003 14:09:26   jgs
 * Modifications for new 6.0 data.
 * Resolution for 1157: Add task for Importing IX Retail Transactions.
 *
 *    Rev 1.1   Jan 22 2003 09:59:14   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.0   Sep 05 2002 11:12:54   msg
 * Initial revision.
 *
 *    Rev 1.7   May 27 2002 16:59:08   mpm
 * Modified naming convention for type constants.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.6   May 13 2002 19:04:12   mpm
 * Added more columns to order; add support for deleted items (line voids).
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.5   May 02 2002 17:29:12   mpm
 * Completed financial totals.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.4   Apr 28 2002 13:32:12   mpm
 * Completed translation of sale transactions.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.3   Apr 26 2002 07:49:02   mpm
 * Modified to set line-item-type attribute.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.2   Apr 25 2002 09:00:46   mpm
 * Completed handling of basic sale transactions.
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.1   Apr 22 2002 19:32:38   mpm
 * Additional TLog work
 * Resolution for Domain SCR-45: TLog facility
 *
 *    Rev 1.0   Apr 21 2002 15:23:52   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.ixretail.tender;

import java.awt.Point;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.ixretail.IXRetailConstantsIfc;
import oracle.retail.stores.domain.ixretail.IXRetailGateway;
import oracle.retail.stores.domain.ixretail.lineitem.LogLineItem;
import oracle.retail.stores.domain.ixretail.utility.LogAddressIfc;
import oracle.retail.stores.domain.ixretail.utility.LogGiftCardIfc;
import oracle.retail.stores.domain.tender.AuthorizableTenderIfc;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderCheckIfc;
import oracle.retail.stores.domain.tender.TenderCouponIfc;
import oracle.retail.stores.domain.tender.TenderDebitIfc;
import oracle.retail.stores.domain.tender.TenderGiftCardIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderMailBankCheckIfc;
import oracle.retail.stores.domain.tender.TenderPurchaseOrderIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.tender.TenderTravelersCheckIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;

/**
 * This class creates the elements for a TenderLineItem
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/7 $
 */
public class LogTenderLineItem extends LogLineItem implements LogTenderLineItemIfc
{
    /**
       revision number supplied by source-code-control system
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/7 $";

    /**
     * Constructs LogTenderLineItem object.
     */
    public LogTenderLineItem()
    {
    }

    /**
     * Creates element for the specified TenderLineItem.
     * 
     * @param tenderLineItem TenderLineItem
     * @param doc parent document
     * @param el parent element
     * @param sequenceNumber sequence number
     * @return Element representing TenderLineItem
     * @exception XMLConversionException thrown if error occurs
     */
    public Element createElement(TenderLineItemIfc tenderLineItem, Document doc, Element el, int sequenceNumber)
            throws XMLConversionException
    {
        Element lineItemElement = createElement(doc, el, IXRetailConstantsIfc.ELEMENT_TENDER, sequenceNumber);

        // create entry method element, where applicable
        createEntryMethodElement(tenderLineItem, lineItemElement);

        Element tenderLineItemElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_TENDER);

        // tender ID is tender type descriptor
        TenderTypeMapIfc map = DomainGateway.getFactory().getTenderTypeMapInstance();
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_TENDER_ID,
                map.getIXRetailDescriptor(tenderLineItem.getTypeCode()), tenderLineItemElement);

        // tender sub code is not supported
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_AMOUNT, tenderLineItem.getAmountTender(),
                tenderLineItemElement);

        // handle foreign currency item, if needed
        if (tenderLineItem instanceof TenderAlternateCurrencyIfc
                && ((TenderAlternateCurrencyIfc)tenderLineItem).getAlternateCurrencyTendered() != null)
        {
            createForeignCurrencyElements(tenderLineItem, tenderLineItemElement);
        }

        createAccountIDElement(tenderLineItem, tenderLineItemElement);

        createAccountInfoElement(tenderLineItem, tenderLineItemElement);

        if (tenderLineItem instanceof TenderMailBankCheckIfc)
        {
            // add address
            LogAddressIfc logAddress = IXRetailGateway.getFactory().getLogAddressInstance();
            logAddress.createElement(((TenderMailBankCheckIfc)tenderLineItem).getPrimaryAddress(), parentDocument,
                    tenderLineItemElement);

        }

        if (tenderLineItem instanceof AuthorizableTenderIfc)
        {
            createAuthorizationElements(tenderLineItem, tenderLineItemElement);
        }

        // create tender-specific elements
        createTenderSpecificElements(tenderLineItem, tenderLineItemElement);

        lineItemElement.appendChild(tenderLineItemElement);

        parentElement.appendChild(lineItemElement);

        return (lineItemElement);

    }

    /**
     * Creates element for a tender change item.
     * 
     * @param changeAmount change amount
     * @param doc parent document
     * @param el parent element
     * @param sequenceNumber sequence number
     * @return Element representing TenderLineItem
     * @exception XMLConversionException thrown if error occurs
     */
    public Element createTenderChangeElement(CurrencyIfc changeAmount, Document doc, Element el, int sequenceNumber)
            throws XMLConversionException
    {
        Element lineItemElement = createElement(doc, el, IXRetailConstantsIfc.ELEMENT_TENDER_CHANGE, sequenceNumber);

        Element tenderLineItemElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_TENDER_CHANGE);

        lineItemElement
                .setAttribute(IXRetailConstantsIfc.ATTRIBUTE_LINE_ITEM_TYPE, tenderLineItemElement.getNodeName());

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_AMOUNT, changeAmount.abs(), tenderLineItemElement);

        lineItemElement.appendChild(tenderLineItemElement);

        parentElement.appendChild(lineItemElement);

        return (lineItemElement);
    }

    /**
     * Creates entry method element, where applicable. The 360Store application
     * has its own settings, which must be translated to the IXRetail standard
     * settings in the RetailTransactionEntryMethod type.
     * 
     * @param tenderLineItem tender line item
     * @param el line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createEntryMethodElement(TenderLineItemIfc tenderLineItem, Element el) throws XMLConversionException
    {
        EntryMethod entryMethod = EntryMethod.Manual;
        String ixRetailEntryMethod = IXRetailConstantsIfc.ELEMENT_VALUE_KEYED;
        switch (tenderLineItem.getTypeCode())
        {
            case TenderLineItemIfc.TENDER_TYPE_CHARGE:
                entryMethod = ((TenderChargeIfc)tenderLineItem).getEntryMethod();
                // if magswipe, set msr
                if (entryMethod != null && entryMethod.equals(EntryMethod.Swipe))
                {
                    ixRetailEntryMethod = IXRetailConstantsIfc.ELEMENT_VALUE_MSR;
                }
                break;
            case TenderLineItemIfc.TENDER_TYPE_DEBIT:
                entryMethod = ((TenderDebitIfc)tenderLineItem).getEntryMethod();
                // if magswipe, set msr
                if (entryMethod != null && entryMethod.equals(EntryMethod.Swipe))
                {
                    ixRetailEntryMethod = IXRetailConstantsIfc.ELEMENT_VALUE_MSR;
                }
                break;
            case TenderLineItemIfc.TENDER_TYPE_CHECK:
                entryMethod = ((TenderCheckIfc)tenderLineItem).getEntryMethod();
                // if auto/micr, set micr
                if (EntryMethod.Automatic.equals(entryMethod) || EntryMethod.Micr.equals(entryMethod))
                {
                    ixRetailEntryMethod = IXRetailConstantsIfc.ELEMENT_VALUE_MICR;
                }
                break;
            case TenderLineItemIfc.TENDER_TYPE_COUPON:
                entryMethod = ((TenderCouponIfc)tenderLineItem).getEntryMethod();
                // if auto, set scanned
                if (entryMethod != null && entryMethod.equals(EntryMethod.Automatic))
                {
                    ixRetailEntryMethod = IXRetailConstantsIfc.ELEMENT_VALUE_SCANNED;
                }
                break;
            case TenderLineItemIfc.TENDER_TYPE_GIFT_CARD:
                entryMethod = ((TenderGiftCardIfc)tenderLineItem).getEntryMethod();
                // if magswipe, set msr
                if (entryMethod != null && entryMethod.equals(EntryMethod.Swipe))
                {
                    ixRetailEntryMethod = IXRetailConstantsIfc.ELEMENT_VALUE_MSR;
                }
                break;
            case TenderLineItemIfc.TENDER_TYPE_GIFT_CERTIFICATE:
                entryMethod = ((TenderGiftCertificateIfc)tenderLineItem).getEntryMethod();
                // if auto, set scanned
                if (entryMethod != null && entryMethod.equals(EntryMethod.Automatic))
                {
                    ixRetailEntryMethod = IXRetailConstantsIfc.ELEMENT_VALUE_SCANNED;
                }
                break;
            case TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT:
                entryMethod = ((TenderStoreCreditIfc)tenderLineItem).getEntryMethod();
                // if auto, set scanned
                if (entryMethod != null && entryMethod.equals(EntryMethod.Automatic))
                {
                    ixRetailEntryMethod = IXRetailConstantsIfc.ELEMENT_VALUE_SCANNED;
                }
                break;
            case TenderLineItemIfc.TENDER_TYPE_TRAVELERS_CHECK:
            case TenderLineItemIfc.TENDER_TYPE_CASH:
            default:
                break;
        }

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ENTRY_METHOD, ixRetailEntryMethod, el);

    }

    /**
     * Creates element for RetailTransactionForeignCurrency type.
     * 
     * @param tenderLineItem tender line item
     * @param el tender line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createForeignCurrencyElements(TenderLineItemIfc tenderLineItem, Element el)
            throws XMLConversionException
    {
        TenderAlternateCurrencyIfc alternateCurrencyTender = (TenderAlternateCurrencyIfc)tenderLineItem;

        Element alternateCurrencyElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_FOREIGN_CURRENCY);
        alternateCurrencyElement.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_TYPE_TAG,
                IXRetailConstantsIfc.TYPE_RETAIL_TRANSACTION_FOREIGN_CURRENCY_360);

        // get code and amount
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_CURRENCY_CODE, alternateCurrencyTender
                .getAlternateCurrencyTendered().getCountryCode(), alternateCurrencyElement);
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_AMOUNT,
                alternateCurrencyTender.getAlternateCurrencyTendered(), alternateCurrencyElement);
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_EXCHANGE_RATE, alternateCurrencyTender
                .getAlternateCurrencyTendered().getBaseConversionRate(), alternateCurrencyElement);

        el.appendChild(alternateCurrencyElement);

    }

    /**
     * Creates account identifier element.
     * 
     * @param tenderLineItem tender line item
     * @param tenderLineItemElement tender line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createAccountIDElement(TenderLineItemIfc tenderLineItem, Element tenderLineItemElement)
            throws XMLConversionException
    {
        String accountNumber = "";
        if (tenderLineItem instanceof TenderCheckIfc)
        {
            accountNumber = ((TenderCheckIfc)tenderLineItem).getAccountNumberEncipheredData().getMaskedNumber();
        }
        else if (tenderLineItem instanceof TenderChargeIfc)
        {
            accountNumber = ((TenderChargeIfc)tenderLineItem).getCardNumber();
        }
        if (!Util.isEmpty(accountNumber))
        {
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ACCOUNT_ID, accountNumber, tenderLineItemElement);
        }

    }

    /**
     * Creates account information element. Initially, this only applied to mail
     * bank check.
     * 
     * @param tenderLineItem tender line item
     * @param tenderLineItemElement tender line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createAccountInfoElement(TenderLineItemIfc tenderLineItem, Element tenderLineItemElement)
            throws XMLConversionException
    {
        if (tenderLineItem instanceof TenderMailBankCheckIfc)
        {
            Element accountInfoElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_ACCOUNT_INFO);

            PersonNameIfc payeeName = ((TenderMailBankCheckIfc)tenderLineItem).getPayeeName();

            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ACCOUNT_FIRST_NAME, payeeName.getFirstName(),
                    accountInfoElement);

            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ACCOUNT_MIDDLE_NAME, payeeName.getMiddleName(),
                    accountInfoElement);

            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ACCOUNT_LAST_NAME, payeeName.getLastName(),
                    accountInfoElement);

            tenderLineItemElement.appendChild(accountInfoElement);
        }

    }

    /**
     * Creates Authorization elements.
     * 
     * @param tenderLineItem tender line item
     * @param el tender line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createAuthorizationElements(TenderLineItemIfc tenderLineItem, Element el)
            throws XMLConversionException
    {
        AuthorizableTenderIfc authorizableTender = (AuthorizableTenderIfc)tenderLineItem;

        Element authorizationElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_AUTHORIZATION);

        // set the 360 type
        authorizationElement.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_TYPE_TAG,
                IXRetailConstantsIfc.TYPE_AUTHORIZATION_360);

        // set amount
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_REQUESTED_AMOUNT, authorizableTender.getAmountTender(),
                authorizationElement);

        // set authorization code
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_AUTHORIZATION_CODE,
                authorizableTender.getAuthorizationCode(), authorizationElement);

        // reference number (not supported)
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_REFERENCE_NUMBER,
                IXRetailConstantsIfc.ELEMENT_VALUE_NOT_SUPPORTED, authorizationElement);

        // provider ID (not supported)
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_PROVIDER_ID,
                IXRetailConstantsIfc.ELEMENT_VALUE_NOT_SUPPORTED, authorizationElement);

        // authorization date time (not supported)
        createTimestampTextNodeElement(IXRetailConstantsIfc.ELEMENT_AUTHORIZATION_DATE_TIME, new EYSDate(1980, 1, 1, 0,
                0, 0), authorizationElement);

        // host authorized (not supported)
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_HOST_AUTHORIZED, true, authorizationElement);

        // force online (not supported)
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_FORCE_ON_LINE, false, authorizationElement);

        // check for electronic signature
        boolean flag = false;
        // at this time, we only support signature for credit
        if (authorizableTender instanceof TenderChargeIfc)
        {
            // check for existence of signature data
            TenderChargeIfc tenderCharge = (TenderChargeIfc)authorizableTender;
            // if data exists and has a length, set flag to true
            if (tenderCharge.getSignatureData() != null && ((Point[])tenderCharge.getSignatureData()).length > 0)
            {
                flag = true;
            }
        }
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ELECTRONIC_SIGNATURE, flag, authorizationElement);

        // authorization method code
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_AUTHORIZATION_METHOD,
                authorizableTender.getAuthorizationMethod(), authorizationElement);

        // get signature, if it exists
        createSignatureElement(authorizableTender, authorizationElement);

        el.appendChild(authorizationElement);

    }

    /**
     * Creates tender-specific elements.
     * 
     * @param tenderLineItem tender line item
     * @param el tender line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createTenderSpecificElements(TenderLineItemIfc tenderLineItem, Element el)
            throws XMLConversionException
    {
        switch (tenderLineItem.getTypeCode())
        {
        case TenderLineItemIfc.TENDER_TYPE_CHARGE:
        case TenderLineItemIfc.TENDER_TYPE_DEBIT:
            createTenderChargeElements(tenderLineItem, el);
            break;
        case TenderLineItemIfc.TENDER_TYPE_CHECK:
            createTenderCheckElements(tenderLineItem, el);
            break;
        case TenderLineItemIfc.TENDER_TYPE_TRAVELERS_CHECK:
            createTenderTravelersCheckElements(tenderLineItem, el);
            break;
        case TenderLineItemIfc.TENDER_TYPE_COUPON:
            createTenderCouponElements(tenderLineItem, el);
            break;
        case TenderLineItemIfc.TENDER_TYPE_GIFT_CERTIFICATE:
            createTenderGiftCertificateElements(tenderLineItem, el);
            break;
        case TenderLineItemIfc.TENDER_TYPE_GIFT_CARD:
            createTenderGiftCardElements(tenderLineItem, el);
            break;
        case TenderLineItemIfc.TENDER_TYPE_MAIL_BANK_CHECK:
            createTenderSendCheckElements(tenderLineItem, el);
            break;
        case TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT:
            createTenderStoreCreditElements(tenderLineItem, el);
            break;
        case TenderLineItemIfc.TENDER_TYPE_PURCHASE_ORDER:
            createTenderPurchaseOrderElements(tenderLineItem, el);
            break;
        default:
        case TenderLineItemIfc.TENDER_TYPE_CASH:
            // take no action
            break;
        }

    }

    /**
     * Creates tender check elements.
     * 
     * @param tenderLineItem tender line item
     * @param el tender line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createTenderCheckElements(TenderLineItemIfc tenderLineItem, Element el)
            throws XMLConversionException
    {

        Element checkElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_CHECK);

        // set 360type
        checkElement.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_TYPE_TAG, IXRetailConstantsIfc.TYPE_CHECK_360);

        TenderCheckIfc tenderCheck = (TenderCheckIfc)tenderLineItem;

        // use ABA number for bank ID
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_BANK_ID, tenderCheck.getABANumber(), checkElement);

        // set check number
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_CHECK_NUMBER, tenderCheck.getCheckNumber(), checkElement);
        // set account number
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ENCRYPTED_ACCOUNT_NUMBER, tenderCheck
                .getAccountNumberEncipheredData().getEncryptedNumber(), checkElement);
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_MASKED_ACCOUNT_NUMBER, tenderCheck
                .getAccountNumberEncipheredData().getMaskedNumber(), checkElement);

        // take identification information only if exists
        if (!Util.isEmpty(tenderCheck.getPersonalID().getMaskedNumber()))
        {
            Element identificationElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_IDENTIFICATION);

            // get ID type, issuer, number and DOB
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ID_TYPE, tenderCheck.getPersonalIDType().getCode(),
                    identificationElement);

            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ID_ISSUER, tenderCheck.getIDIssuer(),
                    identificationElement);

            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ENCRYPTED_ID_NUMBER, tenderCheck
                    .getPersonalID().getEncryptedNumber(), identificationElement);
            createTextNodeElement(IXRetailConstantsIfc.ELEMENT_MASKED_ID_NUMBER, tenderCheck
                    .getPersonalID().getMaskedNumber(), identificationElement);

            if (tenderCheck.getDateOfBirth() != null)
            {
                createDateTextNodeElement(IXRetailConstantsIfc.ELEMENT_DATE_OF_BIRTH, tenderCheck.getDateOfBirth(),
                        identificationElement);
            }

            checkElement.appendChild(identificationElement);
        }

        el.appendChild(checkElement);

    }

    /**
     * Creates tender charge (debit or credit) elements.
     * 
     * @param tenderLineItem tender line item
     * @param el tender line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createTenderChargeElements(TenderLineItemIfc tenderLineItem, Element el)
            throws XMLConversionException
    {
        Element chargeElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_CREDIT_DEBIT);

        TenderChargeIfc tenderCharge = (TenderChargeIfc)tenderLineItem;

        createDateTextNodeElement(IXRetailConstantsIfc.ELEMENT_EXPIRATION_DATE,
                convertExpirationDateString(tenderCharge.getExpirationDateString(), tenderCharge.getEntryMethod()),
                chargeElement);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_CARD_ISSUER_ID, tenderCharge.getCardType(), chargeElement);

        // get track data
        createTrackDataElement(IXRetailConstantsIfc.ELEMENT_TRACK2_DATA, tenderCharge.getTrack2Data(), chargeElement);

        // usage of issue sequence is unknown
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ISSUE_SEQUENCE, "0", chargeElement);

        // usage of service code is unknown
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_SERVICE_CODE,
                IXRetailConstantsIfc.ELEMENT_VALUE_NOT_SUPPORTED, chargeElement);

        el.appendChild(chargeElement);

    }

    /**
     * Converts expiration date string to EYSDate. In the IXRetail format, the
     * expiration date is defined as an xs:date, so the expiration date must be
     * converted to a real date.
     * 
     * @param expirationDateString
     * @return EYSDate object
     * @exception XMLConversionException is thrown if date cannot be parsed
     */
    protected EYSDate convertExpirationDateString(String expirationDateString, EntryMethod entryMethod)
            throws XMLConversionException
    {
        DateFormat dateFormat = null;

        // The format of the expiration date string
        // stored via POS depends on how the card info
        // was entered. If the card was swiped, the format
        // is, believe it or not, yyMM (no slash delimeter,
        // 2 digit year, and year before month).
        // If entered via the keyboard
        // the format is MM/yyyy, as currently enforced by
        // the ui.
        //
        if (entryMethod != null && entryMethod.equals(EntryMethod.Swipe))
        {
            // Card was swiped, set the formatter apropriately
            //
            dateFormat = new SimpleDateFormat("yyMM");
        }
        else
        {
            // Card was not swiped, set the formatter apropriately
            //
            dateFormat = new SimpleDateFormat("MM/yyyy");
        }

        Date expirationDate = null;

        // parse the given date
        try
        {
            expirationDate = dateFormat.parse(expirationDateString);
        }
        catch (Exception e)
        {
            // We were unable to parse the expiration date string for
            // some reason. Inform our caller...
            //
            throw new XMLConversionException("Expiration date [" + expirationDateString + "] cannot be parsed.");

        }

        // A date like 06/2004 converts to 06/01/2004. We need it to be
        // the last day of the month.
        Calendar cal = Calendar.getInstance();
        cal.setTime(expirationDate);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        expirationDate = cal.getTime();

        return (new EYSDate(expirationDate));
    }

    /**
     * Converts expiration date string to EYSDate. In the IXRetail format, the
     * expiration date is defined as an xs:date, so the expiration date must be
     * converted to a real date.
     * 
     * @param expirationDateString
     * @return EYSDate object
     * @exception XMLConversionException is thrown if date cannot be parsed
     * @deprecated Use convertExpirationDateString(String expirationDateString,
     *             String entryMethod)
     */
    protected EYSDate convertExpirationDateString(String expirationDateString) throws XMLConversionException
    {
        return convertExpirationDateString(expirationDateString, null);
    }

    /**
     * Creates element of specified name for track data if data exists.
     * 
     * @param elementName element name
     * @param trackData byte array of track data
     * @param el parent element
     * @exception XMLConversionException is thrown if error occurs
     */
    protected void createTrackDataElement(String elementName, byte[] trackData, Element el)
            throws XMLConversionException
    {
        if (trackData != null && trackData.length > 0)
        {
            createTextNodeElement(elementName, new String(trackData), el);
        }
    }

    /**
     * Creates tender send check elements.
     * 
     * @param tenderLineItem tender line item
     * @param el tender line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createTenderSendCheckElements(TenderLineItemIfc tenderLineItem, Element el)
            throws XMLConversionException
    {
        Element sendCheckElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_SEND_CHECK);

        // reason code is not supported by 360
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_REASON_CODE,
                IXRetailConstantsIfc.ELEMENT_VALUE_NOT_SUPPORTED, sendCheckElement);

        el.appendChild(sendCheckElement);

    }

    /**
     * Creates tender store credit elements.
     * 
     * @param tenderLineItem tender line item
     * @param el tender line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createTenderStoreCreditElements(TenderLineItemIfc tenderLineItem, Element el)
            throws XMLConversionException
    {
        Element storeCreditElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_STORE_CREDIT_360);

        TenderStoreCreditIfc storeCredit = (TenderStoreCreditIfc)tenderLineItem;

        // create action attribute
        storeCreditElement.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_ACTION, storeCredit.getState());

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_STORE_CREDIT_ID, storeCredit.getStoreCreditID(),
                storeCreditElement);

        if (storeCredit.getState().equals("ISSUE") && storeCredit.getExpirationDate() != null)
        {
            createDateTextNodeElement(IXRetailConstantsIfc.ELEMENT_EXPIRATION_DATE, storeCredit.getExpirationDate(),
                    storeCreditElement);
        }

        el.appendChild(storeCreditElement);
    }

    /**
     * Creates tender travelers check elements.
     * 
     * @param tenderLineItem tender line item
     * @param el tender line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createTenderTravelersCheckElements(TenderLineItemIfc tenderLineItem, Element el)
            throws XMLConversionException
    {
        Element checkElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_TRAVELERS_CHECK_360);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_CHECK_COUNT,
                Short.toString(((TenderTravelersCheckIfc)tenderLineItem).getNumberChecks()), checkElement);

        el.appendChild(checkElement);
    }

    /**
     * Creates tender purchase order elements.
     * 
     * @param tenderLineItem tender line item
     * @param el tender line item element
     * @exception XMLConversionException thrown if error occurs
     */
    private void createTenderPurchaseOrderElements(TenderLineItemIfc tenderLineItem, Element el)
            throws XMLConversionException
    {
        Element poElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_TENDER_PURCHASE_ORDER_360);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_PURCHASE_ORDER_ID,
                ((TenderPurchaseOrderIfc)tenderLineItem).getPurchaseOrderNumber(), poElement);

        el.appendChild(poElement);
    }

    /**
     * Creates tender gift certificate elements.
     * 
     * @param tenderLineItem tender line item
     * @param el tender line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createTenderGiftCertificateElements(TenderLineItemIfc tenderLineItem, Element el)
            throws XMLConversionException
    {
        Element certElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_GIFT_CERTIFICATE_360);

        TenderGiftCertificateIfc cert = (TenderGiftCertificateIfc)tenderLineItem;

        // Note: It is unclear which element takes the gift certificate number.
        // Therefore, both elements will be populated.
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_GIFT_CERTIFICATE_ID, cert.getGiftCertificateNumber(),
                certElement);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_SERIAL_NUMBER, cert.getGiftCertificateNumber(), certElement);

        // apply face value
        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_FACE_VALUE, cert.getAmountTender(), certElement);

        el.appendChild(certElement);

    }

    /**
     * Creates tender gift card elements.
     * 
     * @param tenderLineItem tender line item
     * @param el tender line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createTenderGiftCardElements(TenderLineItemIfc tenderLineItem, Element el)
            throws XMLConversionException
    {
        TenderGiftCardIfc giftCardTender = (TenderGiftCardIfc)tenderLineItem;

        LogGiftCardIfc logGiftCard = IXRetailGateway.getFactory().getLogGiftCardInstance();

        Element giftCardElement = logGiftCard.createElement(giftCardTender.getGiftCard(),
                IXRetailConstantsIfc.ELEMENT_GIFT_CARD_TENDER_360, parentDocument, el);

        giftCardElement.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_TYPE_TAG,
                IXRetailConstantsIfc.ELEMENT_GIFT_CARD_360);

    }

    /**
     * Creates tender manufacturer coupon elements.
     * 
     * @param tenderLineItem tender line item
     * @param el tender line item element
     * @exception XMLConversionException thrown if error occurs
     */
    protected void createTenderCouponElements(TenderLineItemIfc tenderLineItem, Element el)
            throws XMLConversionException
    {
        Element couponElement = parentDocument.createElement(IXRetailConstantsIfc.ELEMENT_MANUFACTURER_COUPON);

        TenderCouponIfc tenderCoupon = (TenderCouponIfc)tenderLineItem;

        // identify as 360 type
        couponElement.setAttribute(IXRetailConstantsIfc.ATTRIBUTE_SCHEMA_TYPE_TAG,
                IXRetailConstantsIfc.TYPE_TENDER_COUPON_360);

        // add coupon type attribute
        String ixRetailCouponType = IXRetailConstantsIfc.ATTRIBUTE_VALUE_MANUFACTURER;
        switch (tenderCoupon.getCouponType())
        {
            case TenderCouponIfc.COUPON_TYPE_STORE:
                ixRetailCouponType = IXRetailConstantsIfc.ATTRIBUTE_VALUE_STORE;
                break;
            case TenderCouponIfc.COUPON_TYPE_ELECTRONIC:
                ixRetailCouponType = IXRetailConstantsIfc.ATTRIBUTE_VALUE_ELECTRONIC;
                break;
            case TenderCouponIfc.COUPON_TYPE_MANUFACTURER:
            default:
                break;
        }
        couponElement.setAttribute(IXRetailConstantsIfc.ELEMENT_COUPON_TYPE, ixRetailCouponType);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_PRIMARY_LABEL,
                ((TenderCouponIfc)tenderLineItem).getCouponNumber(), couponElement);

        createTextNodeElement(IXRetailConstantsIfc.ELEMENT_SECONDARY_LABEL,
                IXRetailConstantsIfc.ELEMENT_VALUE_NOT_SUPPORTED, couponElement);

        el.appendChild(couponElement);

    }

    /**
     * Creates elements for electronic signature, if it exists.
     * 
     * @param authorizableTender authorizable tender object
     * @param el element to which signature image is to be appended
     * @exception throws XMLConversionException if error occurs
     */
    protected void createSignatureElement(AuthorizableTenderIfc authorizableTender, Element el)
            throws XMLConversionException
    {
        // at this time, signature captured only on charge
        if (authorizableTender instanceof TenderChargeIfc)
        {
            TenderChargeIfc tenderCharge = (TenderChargeIfc)authorizableTender;
            if (tenderCharge.getSignatureData() != null)
            {
                Point[] data = (Point[])tenderCharge.getSignatureData();
                StringBuffer value = new StringBuffer();
                if (data != null)
                {
                    Point p = null;

                    for (int i = 0; i < data.length; i++)
                    {
                        p = data[i];
                        value.append("x" + Integer.toString(p.x) + "y" + Integer.toString(p.y));
                    }
                }
                else
                {
                    value.append("null");
                }

                if (value.length() > 0)
                {
                    createTextNodeElement(IXRetailConstantsIfc.ELEMENT_ELECTRONIC_SIGNATURE_IMAGE, value.toString(), el);
                }
            }
        }
    }

}
